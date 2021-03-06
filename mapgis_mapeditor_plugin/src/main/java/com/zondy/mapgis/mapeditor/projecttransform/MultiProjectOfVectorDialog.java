package com.zondy.mapgis.mapeditor.projecttransform;

import com.zondy.mapgis.base.*;
import com.zondy.mapgis.common.URLParse;
import com.zondy.mapgis.filedialog.FolderType;
import com.zondy.mapgis.filedialog.GDBOpenFileDialog;
import com.zondy.mapgis.filedialog.GDBSaveFileDialog;
import com.zondy.mapgis.filedialog.GDBSelectFolderDialog;
import com.zondy.mapgis.geodatabase.*;
import com.zondy.mapgis.geometry.GeomType;
import com.zondy.mapgis.srs.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

/**
 * 矢量类批量投影
 */
public class MultiProjectOfVectorDialog extends Dialog {
    private Stage stage = null;


    /**
     * 构造方法
     */
    public MultiProjectOfVectorDialog() {
        setTitle("矢量投影转换");
        //初始化界面布局
        initialize();
        //绑定事件
        bindAction();
        DialogPane dialogPane = super.getDialogPane();
        stage = (Stage) dialogPane.getScene().getWindow();
        ButtonType projectBtn = new ButtonType("投影", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(projectBtn, ButtonType.CANCEL);
        dialogPane.setContent(this.gridPane);

        Button okButton = (Button) dialogPane.lookupButton(projectBtn);
        okButton.addEventFilter(ActionEvent.ACTION, this::okButtonClick);

    }

    //工具栏
    private Button btnAdd = null;
    private Button btnDel = null;
    private Button btnModify = null;
    private Button btnSaveLog = null; //输出日志日志
    private Button btnViewLog = null; //查看日志
    private Label labelMessage = new Label(); //参数错误提示信息
    private static String logFilePath = null;    //日志保存路径
    private File m_OutLogFile = null;
    private GridPane gridPane = null;

    //源数据列表
    private TableView<ProjectDataItem> projectDataItemTableView = null;
    private ObservableList<ProjectDataItem> projectDataItemList = null;
    //源数据参考系信息面板
    private SrcSRefInfoPane srcSRefInfoPane = null;
    //目的参考系设置面板
    private DesSRefSettingPane desSRefSettingPane = null;
    //地理转换参数设置
    private ElpTransParamSettingPane elpTransParamSettingPane = null;
    //输出设置
    private TextField desDirText = new TextField();
    private Button desDirBtn = new Button("...");
    private CheckBox closeCheckBox = new CheckBox("转换完成后关闭此框");

    /**
     * 初始化界面布局
     */
    private void initialize() {

        gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        int rowIndex = 0;

        //region 工具栏
        ToolBar toolBar = new ToolBar();
        toolBar.setPadding(new Insets(0, 5, 3, 5));

        btnAdd = new Button("添加");
        btnDel = new Button("删除");
        btnModify = new Button("统改");
        btnSaveLog = new Button("输出日志");
        btnViewLog = new Button("查看日志");
        //参数错误信息提示
        labelMessage.setTextFill(Paint.valueOf("red"));
        HBox hBoxLoc = new HBox();//占位控件，用于使得后面的消息Label靠右停
        HBox.setHgrow(hBoxLoc, Priority.ALWAYS);
//        toolBar.getItems().addAll(btnAdd, new Separator(), btnDel, new Separator(), btnModify);
//        toolBar.getItems().addAll(btnAdd, btnDel, btnModify);
        toolBar.getItems().addAll(btnAdd, btnDel, btnModify,btnSaveLog,btnViewLog,hBoxLoc,labelMessage);
        gridPane.add(toolBar, 0, rowIndex, 2, 1);

        //endregion

        //region 数据列表
        rowIndex++;
        projectDataItemTableView = new TableView<>();
        projectDataItemTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        projectDataItemTableView.setPrefWidth(600);
        projectDataItemTableView.setEditable(true);
        TableColumn<ProjectDataItem, String> tc_srcName = new TableColumn<>("源数据名");
        tc_srcName.setMinWidth(100);
        TableColumn<ProjectDataItem, String> tc_srcUrl = new TableColumn<>("源路径");
        tc_srcUrl.setMinWidth(100);
        tc_srcUrl.setPrefWidth(320);
        TableColumn<ProjectDataItem, String> tc_desName = new TableColumn<>("目的数据名");
        tc_desName.setMinWidth(100);
        //增加状态列
        TableColumn<ProjectDataItem,String> tc_state = new TableColumn<ProjectDataItem, String>("状态");
        tc_state.setMinWidth(50);
        projectDataItemTableView.getColumns().addAll(tc_srcName, tc_srcUrl, tc_desName,tc_state);
//        projectDataItemTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tc_srcName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ProjectDataItem, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ProjectDataItem, String> param) {
                return new SimpleStringProperty(param.getValue().getSrcName());
            }
        });
        tc_srcUrl.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ProjectDataItem, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ProjectDataItem, String> param) {
                return new SimpleStringProperty(param.getValue().getSrcLUrl());
            }
        });
        tc_desName.setEditable(true);
        tc_desName.setCellValueFactory(new PropertyValueFactory<>("desName"));
        tc_desName.setCellFactory(new Callback<TableColumn<ProjectDataItem, String>, TableCell<ProjectDataItem, String>>() {
            @Override
            public TableCell<ProjectDataItem, String> call(TableColumn<ProjectDataItem, String> param) {

                return new TableCell<ProjectDataItem,String>()
                {
                    private TextField textField = new TextField();

                    @Override
                    public void startEdit()
                    {
                        super.startEdit();
                        textField.setText(getItem());
                        setGraphic(textField);
                        textField.requestFocus();

                        textField.textProperty().addListener((o, ov, nv) ->
                        {
                            StringProperty errorMsg = new SimpleStringProperty();
                            if (!XString.isTextValid(nv, 20, GISDefaultValues.getInvalidNameCharList(), errorMsg))
                            {
                                UIFunctions.showErrorTip(textField, errorMsg.get(), new Tooltip());
                                textField.setText(ov);
                            }
                        });
                        //按Enter键完成修改。
                        textField.setOnKeyPressed(event ->
                        {
                            if (KeyCode.ENTER.equals(event.getCode()))
                            {
                                projectDataItemTableView.requestFocus();
                            }
                        });
                        textField.focusedProperty().addListener((o, ov, nv) ->
                        {
                            if (!nv)
                            {
                                commitEdit(textField.getText());
                            }
                        });
                    }

                    @Override
                    public void commitEdit(String value)
                    {
                        super.commitEdit(value);
                        ProjectDataItem projItem = (ProjectDataItem) getTableRow().getItem();
                        if (projItem != null)
                        {
                            projItem.setDesName(value);
                        }
                        updateItem(value, XString.isNullOrEmpty(value));
                        //checkCheckError();
                    }

                    @Override
                    protected void updateItem(String item, boolean empty)
                    {
                        super.updateItem(item, empty);
                        Label label = null;
                        if (!empty)
                        {
                            label = new Label(item);
                        }
                        setGraphic(label);
                        checkCheckError();
                    }
                };
            }
        });
        tc_state.setCellValueFactory(new PropertyValueFactory<>("state"));
        tc_state.setCellFactory(new Callback<TableColumn<ProjectDataItem, String>, TableCell<ProjectDataItem, String>>() {
            @Override
            public TableCell<ProjectDataItem, String> call(TableColumn<ProjectDataItem, String> param) {
                return new TableCell<ProjectDataItem, String>()
                {
                    @Override
                    protected void updateItem(String state, boolean empty) {
                        super.updateItem(state, empty);
                        ImageView imageView = null;
                        if (!empty) {
                            if(state != null)
                            {
                                imageView = new ImageView(new Image("/Png_Error_16.png"));
                            }
                        }
                        setGraphic(imageView);
                        setAlignment(Pos.CENTER);
                    }
                };
            }
        });
        //单击行显示对应的目的数据目录及对应的源参照系信息及参数错误提示信息(如果检测出错误)
        projectDataItemTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ProjectDataItem>() {
            @Override
            public void changed(ObservableValue<? extends ProjectDataItem> observable, ProjectDataItem oldValue, ProjectDataItem newValue) {
                if (newValue != null) {
                    String desDir = newValue.getDesDir();
                    desDirText.setText(desDir);
                    //单击行同时更新对应的源参照系信息
                    String srefInfo = getSRefDataInfo(newValue.getSrcRefData(),true);
                    srcSRefInfoPane.setSrcSRefInfoText(srefInfo);
                    labelMessage.setText(newValue.getCurErrorMsg());

                }
            }
        });
        projectDataItemList = FXCollections.observableArrayList();
        projectDataItemTableView.setItems(projectDataItemList);
        gridPane.add(projectDataItemTableView, 0, rowIndex, 1, 4);
        //endregion

        //region 源参考系信息
        srcSRefInfoPane = new SrcSRefInfoPane();
        srcSRefInfoPane.setPrefWidth(250);
        srcSRefInfoPane.setPrefHeight(100);
        TitledPane srcSRefTitledPane = new TitledPane();
        srcSRefTitledPane.setText("源参考系信息");
        srcSRefTitledPane.setCollapsible(false);
        srcSRefTitledPane.setContent(srcSRefInfoPane);
        srcSRefTitledPane.setPadding(new Insets(0, 5, 0, 5));
        gridPane.add(srcSRefTitledPane, 1, rowIndex);
        //endregion

        //region 目的参考系信息
        rowIndex++;
        desSRefSettingPane = new DesSRefSettingPane();
        desSRefSettingPane.setPrefWidth(250);
        desSRefSettingPane.setPrefHeight(150);
        TitledPane desSRefTitledPane = new TitledPane();
        desSRefTitledPane.setText("目的参考系设置");
        desSRefTitledPane.setCollapsible(false);
        desSRefTitledPane.setContent(desSRefSettingPane);
        desSRefTitledPane.setPadding(new Insets(5, 5, 0, 5));
        gridPane.add(desSRefTitledPane, 1, rowIndex);
        //endregion

        //region 地理转换参数
        rowIndex++;
        elpTransParamSettingPane = new ElpTransParamSettingPane();
        elpTransParamSettingPane.setPrefWidth(250);
        //elpTransParamSettingPane.setPrefHeight(100);
        TitledPane transParamSettingTitledPane = new TitledPane();
        transParamSettingTitledPane.setText("地理转换参数设置");
        transParamSettingTitledPane.setCollapsible(false);
        transParamSettingTitledPane.setContent(elpTransParamSettingPane);
        transParamSettingTitledPane.setPadding(new Insets(5, 5, 0, 5));
        gridPane.add(transParamSettingTitledPane, 1, rowIndex);

        //endregion

        //region 输出设置
        rowIndex++;
        GridPane desDirGrid = new GridPane();
        Label desDirSettingLabel = new Label("目的数据目录:");
        desDirText = new TextField();
        desDirText.setEditable(false);
        desDirText.setFocusTraversable(true);
        desDirBtn = new Button("...");
        desDirGrid.add(desDirSettingLabel, 0, 0);
        desDirGrid.add(desDirText, 1, 0);
        desDirGrid.add(desDirBtn, 2, 0);
        desDirGrid.setHgap(5);
        desDirGrid.setVgap(5);
        desDirGrid.setPadding(new Insets(5, 5, 5, 5));
        TitledPane desDirSettingTitledPane = new TitledPane();
        desDirSettingTitledPane.setText("输出设置");
        desDirSettingTitledPane.setCollapsible(false);
        desDirSettingTitledPane.setContent(desDirGrid);
        desDirSettingTitledPane.setPadding(new Insets(5, 5, 5, 5));
        gridPane.add(desDirSettingTitledPane, 1, rowIndex);

        //endregion
        //转换完成后关闭复选框
        rowIndex++;
        closeCheckBox.setSelected(false);
        gridPane.add(closeCheckBox,0,rowIndex);

    }

    /**
     * 控件事件绑定
     */
    private void bindAction() {
        //添加数据
        this.btnAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                btnAddClick();
            }
        });
        //移除选中列表行
        this.btnDel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ObservableList<ProjectDataItem> projectDataItemsSelected = projectDataItemTableView.getSelectionModel().getSelectedItems();
                if (projectDataItemsSelected != null && projectDataItemsSelected.size() > 0) {
                    projectDataItemList.removeAll(projectDataItemsSelected);
                    projectDataItemTableView.refresh();
                }
            }
        });
        //统改数据
        this.btnModify.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ObservableList<ProjectDataItem> projectDataItemsSelected = projectDataItemTableView.getSelectionModel().getSelectedItems();
                if (projectDataItemsSelected != null && projectDataItemsSelected.size() > 0) {
                    UnificationModifyDialog dialog = new UnificationModifyDialog();
                    //linux系统不支持6x数据，故屏蔽磁盘目录
                    if (XFunctions.isSystemLinux()) {
                        dialog.setModifyDiskDir(false);
                    }
                    Optional<ButtonType> rtn = dialog.showAndWait();
                    if (rtn != null && rtn.get() == ButtonType.OK) {
                        for (ProjectDataItem dataItem : projectDataItemsSelected) {
                            if (dialog.isModifyBefExt() && dialog.getBefExt() != null && dialog.getBefExt().length() > 0) {
                                dataItem.setDesName(dialog.getBefExt() + dataItem.getDesName());
                            }
                            if (dialog.isModifyAfExt() && dialog.getAfExt() != null && dialog.getAfExt().length() > 0) {
                                dataItem.setDesName(dataItem.getDesName() + dialog.getAfExt());
                            }
                            if(dialog.getFielDir() != null && dialog.getFielDir().length() >0)
                            {
                                dataItem.setDesDir(dialog.getFielDir());
                            }
                            else if(dialog.getGDBDir() != null && dialog.getGDBDir().length() >0)
                            {
                                dataItem.setDesDir(dialog.getGDBDir());
                            }
//                            if (dialog.isModifyGDBDir() && dialog.getGDBDir() != null && dialog.getGDBDir().length() > 0) {
//                                if (dataItem.getSrcLUrl().toUpperCase().startsWith("GDBP://")) {
//                                    dataItem.setDesDir(dialog.getGDBDir());
//                                }
//                            }
//                            if (dialog.isModifyFileDir() && dialog.getFielDir() != null && dialog.getFielDir().length() > 0) {
//                                if (!dataItem.getSrcLUrl().toUpperCase().startsWith("GDBP://")) {
//                                    dataItem.setDesDir(dialog.getFielDir());
//                                }
//                            }
                        }

                        projectDataItemTableView.refresh();
                    }
                    //刷新当前选择的行的目的数据目录
                    ProjectDataItem curSelectedItem = projectDataItemTableView.getSelectionModel().getSelectedItem();
                    if(curSelectedItem != null)
                    {
                        String desDir = curSelectedItem.getDesDir();
                        desDirText.setText(desDir);
                    }
                }
            }
        });
        //输出日志
        this.btnSaveLog.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                GDBSaveFileDialog dialog = new GDBSaveFileDialog();
                dialog.setFilter("日志文件(*.log)|*.log");
                Optional<String[]> optional = dialog.showAndWait();
                if(optional != null && optional.isPresent())
                {
                    String file = optional.get()[0];
                    if(file != null && file.length() >0)
                    {
                        logFilePath = file;
                    }
                }
            }
        });
        //查看日志
        this.btnViewLog.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (logFilePath != null && logFilePath.length() > 0)
                {
                    String file = logFilePath;
                    try {
                        Runtime run = Runtime.getRuntime();
                        if (XFunctions.isSystemWindows())
                            run.exec(String.format("notepad %s", file));
                        else
                            run.exec(String.format("deepin-editor %s", file));
                    } catch (IOException ex) {

                    }
                }
            }
        });
        //单独修改某一项的目的数据目录
        desDirBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //结果目录既可以选择本地磁盘也可选择GDB目录，本地磁盘则后缀名根据源数据的几何类型决定
                ProjectDataItem item = projectDataItemTableView.getSelectionModel().getSelectedItem();
                if (item != null) {
                    GDBSelectFolderDialog selectFolderDialog = new GDBSelectFolderDialog();
                    selectFolderDialog.setMultiSelect(false);
                    //linux系统不支持6x数据，故屏蔽磁盘目录
                    if (XFunctions.isSystemLinux()) {
                        selectFolderDialog.setFolderType(FolderType.MapGIS_DataBase | FolderType.MapGIS_Fds);
                    } else {
                        selectFolderDialog.setFolderType(FolderType.Disk_Folder | FolderType.MapGIS_DataBase | FolderType.MapGIS_Fds);
                    }
                    Optional<String[]> optional = selectFolderDialog.showAndWait();
                    if (optional != null && optional.isPresent()) {
                        ProjectDataItem projItem = projectDataItemTableView.getSelectionModel().getSelectedItem();
                        String url = optional.get()[0];
                        if (url != null && url.length() > 0) {
                            desDirText.setText(url);
                            projItem.setDesDir(url);
                        }
                    }
                }
            }
        });
    }

    /**
     * 处理添加功能
     */
    private void btnAddClick() {
        GDBOpenFileDialog gdbOpenFileDialog = new GDBOpenFileDialog();
        String filter = "简单要素类、注记类|sfcls;acls|6x数据|*.wt;*.wl;*.wp";
        if(XFunctions.isSystemLinux())
        {
            filter = "简单要素类、注记类|sfcls;acls";
        }
        gdbOpenFileDialog.setFilter(filter);
        gdbOpenFileDialog.setMultiSelect(true);
        Optional<String[]> optional = gdbOpenFileDialog.showAndWait();
        if (optional != null && optional.isPresent()) {
            String[] files = optional.get();
            if (files != null && files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    String url = files[i];
                    if (url != null && url.length() > 0) {
                        addRowData(url);
                    }
                }
            }
        }

    }

    /**
     * 添加源数据到列表中
     */
    private void addRowData(String url) {
        ProjectDataItem dataItem = new ProjectDataItem();
        //  简单要素类
        if (url.toLowerCase().contains("/sfcls/")) {
            SFeatureCls sfcls = new SFeatureCls();
            if (sfcls.openByURL(url) > 0) {
                dataItem.setSrcLUrl(url);
                dataItem.setSrcName(sfcls.getName());
                dataItem.setDesName(sfcls.getName());
                //解析desdir
                String desDir = getGDBDir(url.toLowerCase(), "/sfcls/");
                dataItem.setDesDir(desDir);
                dataItem.setDataType(ProjectDataType.Sfcls);
                //记录源数据的几何类型，确保目的数据类型一致
                GeomType geomType = sfcls.getGeomType();
                dataItem.setGeomType(geomType);
                int srID = sfcls.getsrID();
                SRefData sRefData = sfcls.getGDataBase().getSRef(srID);
                dataItem.setSrcRefData(sRefData);
                String srefInfo = getSRefDataInfo(sRefData,true);
                srcSRefInfoPane.setSrcSRefInfoText(srefInfo);
                projectDataItemList.addAll(dataItem);
                projectDataItemTableView.refresh();
                projectDataItemTableView.getSelectionModel().select(0);
                sfcls.close();
            }
        }
        //注记类
        else if (url.toLowerCase().contains("/acls/")) {
            AnnotationCls acls = new AnnotationCls();
            if (acls.openByURL(url) > 0) {
                dataItem.setSrcLUrl(url);
                dataItem.setSrcName(acls.getName());
                dataItem.setDesName(acls.getName());
                //解析desdir
                String desDir = getGDBDir(url.toLowerCase(), "/acls/");
                dataItem.setDesDir(desDir);
                dataItem.setDataType(ProjectDataType.Acls);
                //记录源数据的几何类型，确保目的数据类型一致
                int srID = acls.getsrID();
                SRefData sRefData = acls.getGDataBase().getSRef(srID);
                dataItem.setSrcRefData(sRefData);
                String srefInfo = getSRefDataInfo(sRefData,true);
                srcSRefInfoPane.setSrcSRefInfoText(srefInfo);
                projectDataItemList.addAll(dataItem);
                projectDataItemTableView.refresh();
                projectDataItemTableView.getSelectionModel().select(0);
                acls.close();
            }
        }
        //6x数据
        else if (url.toLowerCase().endsWith(".wt") || url.toLowerCase().endsWith(".wl") || url.toLowerCase().endsWith(".wp")) {
            SFeatureCls sfcls = new SFeatureCls();
            if (sfcls.openByURL("file:///" + url) > 0) {
                File file = new File(url);
                String desDir = file.getParent();
                dataItem.setSrcLUrl(url);
                String name = sfcls.getName();
                name = name.substring(0, name.lastIndexOf('.'));
                dataItem.setSrcName(name);
                dataItem.setDesName(name);
                dataItem.setDesDir(desDir);
                dataItem.setDataType(ProjectDataType.File6x);
                //记录源数据的几何类型，确保目的数据类型一致
                GeomType geomType = sfcls.getGeomType();
                dataItem.setGeomType(geomType);
                int srID = sfcls.getdsID();
                SRefData sRefData = sfcls.getGDataBase().getSRef(srID);
                dataItem.setSrcRefData(sRefData);
                String srefInfo = getSRefDataInfo(sRefData,true);
                srcSRefInfoPane.setSrcSRefInfoText(srefInfo);
                projectDataItemList.addAll(dataItem);
                projectDataItemTableView.refresh();
                projectDataItemTableView.getSelectionModel().select(0);
                sfcls.close();
            }
        }
    }

    /**
     * 确定按钮执行
     */
    private void okButtonClick(ActionEvent event) {
        SRefData desSRefData = desSRefSettingPane.getDesSrefData();
        if (desSRefData == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "请选择目的参照系!", ButtonType.OK);
            alert.show();
            event.consume();
            return;
        }
        //对每条源数据进行判断，首先去掉重复路径和目的数据类型不一致的情况
        //然后遍历每一条有效源数据进行投影转换操作
        //筛选出的有效数据进行投影
        //ArrayList<ProjectDataItem> validProjectItems = new ArrayList<>();
        //转换前参数检验
        checkCheckError();
        if(this.hasErrItem)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "投影转换列表中存在错误的参数项!", ButtonType.OK);
            alert.show();
            event.consume();
            return;
        }
        ObservableList<ProjectDataItem> projectItems = this.projectDataItemList;
        if (projectItems != null || projectItems.size() >0) {
            writeLog("");
            String str = String.format("开始投影转换，共计%d个转换项。",projectItems.size());
            writeLog(str);
            //start time
            Date now = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式
            String strSTime  = dateFormat.format( now );
            writeLog("开始时间 "+ strSTime);
            int successNum = 0; //统计转换成功的数目
            for (int i = 0; i < projectItems.size(); i++) {
                ProjectDataItem projItem = projectItems.get(i);
                String curMsg = String.format("开始转换第%d项。", i + 1);
                writeLog(curMsg);
                if (projItem.getDesName() != null && projItem.getDesName().length() > 0) {
                    curMsg = String.format("当前转换源数据:%s", projItem.getSrcLUrl());
                    writeLog(curMsg);
                    boolean rtn = beginConvert(projItem);
                    if (rtn) {
                        successNum++;
                    }
                    String states = rtn ? "成功" : "失败";
                    curMsg = String.format("结束转换第%d项。转换结果%s", i + 1, states);
                    writeLog(curMsg);
                } else {
                    curMsg = String.format("结束转换第%d项。转换结果%s", i + 1, "失败。转换参数不正确。");
                    writeLog(curMsg);
                }
            }
            str = String.format("投影转换结束，转换成功%d项。",successNum);
            writeLog(str);
            now = new Date();
            String strETime  = dateFormat.format( now );
            writeLog("结束时间 "+ strETime);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "转换结束!", ButtonType.OK);
            alert.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "请先添加源数据!", ButtonType.OK);
            alert.show();
            event.consume();
        }
        if(!closeCheckBox.isSelected())
        {
            event.consume();//未勾选转换结束后关闭则界面不退出
        }
    }

    /**
     * 转换每一条数据
     */
    private boolean beginConvert(ProjectDataItem projItem) {
        boolean rtn = false;
        SRefData desSRefData = desSRefSettingPane.getDesSrefData();
        ElpTransParam elpTransParam = elpTransParamSettingPane.getElpTransParam();
        ProjectDataType dataType = projItem.getDataType();
        GeomType geomType = projItem.getGeomType();
        String srcUrl = projItem.getSrcLUrl();
        String desDir = projItem.getDesDir();
        String desName = projItem.getDesName();
        String desUrl = desDir;
        String sep = File.separator;
        String curMsg = "";
        if (desDir.toLowerCase().startsWith("gdbp://")) {
            if (geomType == GeomType.GeomAnn)
                desUrl = desDir + "/acls/" + desName;
            else
                desUrl = desDir + "/sfcls/" + desName;
        } else {
            String ext = ".wt";
            if (geomType == GeomType.GeomPnt || geomType == GeomType.GeomAnn) {
                ext = ".wt";
            } else if (geomType == GeomType.GeomLin) {
                ext = ".wl";
            } else if (geomType == GeomType.GeomReg) {
                ext = ".wp";
            }
            desUrl = desDir + sep + desName + ext;
        }

        curMsg = String.format("当前转换目的数据:%s", desUrl);
        writeLog(curMsg);

        switch (dataType) {
            case Sfcls: {
                //region  简单要素类 -> 简单要素类 或 6x数据
                SFeatureCls sf = new SFeatureCls();
                if (sf.openByURL(srcUrl) <= 0) {
                    // 打开源简单要素类失败
                    curMsg = String.format("打开源简单要素类失败,结束转换。");
                    writeLog(curMsg);
                    return false;
                } else {
                    // 打开源简单要素类成功
                    curMsg = String.format("打开源简单要素类成功。");
                    writeLog(curMsg);
                    if (desDir.toLowerCase().startsWith("gdbp://")) {
                        desUrl = desDir + "/sfcls/" + desName;
                    } else {
//                        String ext = ".wt";
//                        if (geomType == GeomType.GeomPnt || geomType == GeomType.GeomAnn) {
//                            ext = ".wt";
//                        } else if (geomType == GeomType.GeomLin) {
//                            ext = ".wl";
//                        } else if (geomType == GeomType.GeomReg) {
//                            ext = ".wp";
//                        }
//                        desUrl = "file:///" + desDir + sep + desName + ext;
                        File file = new File(desUrl);
                        if (file.exists()) {
                            //结果文件已存在不处理
                            curMsg = String.format("结果文件已存在。");
                            writeLog(curMsg);
                            sf.close();
                            return false;
                        }
                    }
                    SFeatureCls destsf = new SFeatureCls();
                    if (destsf.create(desUrl.toLowerCase().startsWith("gdbp://")? desUrl : "file:///" + desUrl, geomType) <= 0) {
                        //创建目的简单要素类失败
                        curMsg = String.format("创建目的简单要素类失败。");
                        writeLog(curMsg);
                        sf.close();
                        return false;
                    } else {
                        //创建目的简单要素类成功
                        curMsg = String.format("创建目的简单要素类成功。");
                        writeLog(curMsg);
                    }
                    destsf.setModelName(sf.getModelName());
                    SRefData sourSRS = projItem.getSrcRefData();
                    SRefData desSRS = desSRefData;
                    //开始投影转换
                    curMsg = String.format("开始投影转换。");
                    writeLog(curMsg);
                    boolean isRotateSymbol = false;
                    boolean bRtn = sf.projTransEx(null, desSRS, destsf, elpTransParam, isRotateSymbol);
                    if (bRtn) {
                        //结束投影转换-成功
                        curMsg = String.format("结束投影转换。 成功");
                        writeLog(curMsg);
                        destsf.close();
                        rtn = true;
                    } else {
                        //结束投影转换-失败
                        curMsg = String.format("结束投影转换。 失败");
                        writeLog(curMsg);
                        if (desUrl.toLowerCase().startsWith("gdbp://")) {
                            int clsID = destsf.getClsID();
                            String gdbUrl = destsf.getGDataBase().getURL();
                            destsf.close();
                            DataBase delDB = DataBase.openByURL(gdbUrl);
                            if (delDB != null) {
                                SFeatureCls.remove(delDB, clsID);
                                delDB.close();
                            }
                        } else {
                            destsf.close();
                            File file = new File(desUrl);
                            if (file.exists()) {
                                file.delete();
                            }
                            file = new File(desUrl + "~");
                            if (file.exists()) {
                                file.delete();
                            }
                        }
                    }
                }
                break;
                //endregion
            }
            case File6x: {
                //region 6x数据 -> MapGIS7数据 或 6x数据
                SFeatureCls srcSfcls = new SFeatureCls();
                if (srcSfcls.openByURL("file:///" + srcUrl) <= 0) {
                    //打开源简单要素类失败
                    curMsg = String.format("打开源简单要素类失败。");
                    writeLog(curMsg);
                    return false;
                }
                //打开源简单要素类成功
                curMsg = String.format("打开源简单要素类成功。");
                writeLog(curMsg);
                GeomType srcDataGeomType = srcSfcls.getGeomType();
                SFeatureCls destSfcls = new SFeatureCls();
                {
                    if (desDir.toLowerCase().startsWith("gdbp://")) {
                        desUrl = desDir + "/sfcls/" + desName;
                    } else {
                        String ext = ".wt";
                        if (geomType == GeomType.GeomPnt || geomType == GeomType.GeomAnn) {
                            ext = ".wt";
                        } else if (geomType == GeomType.GeomLin) {
                            ext = ".wl";
                        } else if (geomType == GeomType.GeomReg) {
                            ext = ".wp";
                        }
                        desUrl = "file:///" + desDir + sep + desName + ext;
                        File file = new File(desUrl);
                        if (file.exists()) {
                            //结果文件已存在 不处理
                            curMsg = String.format("结果文件已存在。");
                            writeLog(curMsg);
                            srcSfcls.close();
                            return false;
                        }
                    }
                }
                if (destSfcls.create(desUrl, srcDataGeomType) <= 0) {
                    srcSfcls.close();
                    //创建目的简单要素类失败
                    curMsg = String.format("创建目的简单要素类失败。");
                    writeLog(curMsg);
                    return false;
                }
                //创建目的简单要素类成功
                curMsg = String.format("创建目的简单要素类成功。");
                writeLog(curMsg);
                destSfcls.setModelName(srcSfcls.getModelName());
                SRefData sourSRS = projItem.getSrcRefData();
                SRefData desSRS = desSRefData;
                //开始投影转换
                curMsg = String.format("开始投影转换。");
                writeLog(curMsg);
                boolean isRotateSymbol = false;
                boolean bRtn = srcSfcls.projTransEx(null, desSRS, destSfcls, elpTransParam, isRotateSymbol);
                if (bRtn) {
                    //结束投影转换-成功
                    curMsg = String.format("结束投影转换。 成功");
                    writeLog(curMsg);
                    String anclsName = desName.trim();
                    if (srcDataGeomType == GeomType.GeomPnt && desUrl.toLowerCase().startsWith("gdbp://")) {
                        int nameIndex = 2;
                        while (destSfcls.getGDataBase().xClsIsExist(XClsType.XACls, anclsName) > 0) {
                            anclsName = desName.trim() + String.valueOf(nameIndex);
                            nameIndex++;
                        }
                    }
                    String pntName = desName.trim();
                    if (srcDataGeomType == GeomType.GeomReg && desUrl.toLowerCase().startsWith("gdbp://")) {
                        int nameIndex = 2;
                        while (destSfcls.getGDataBase().xClsIsExist(XClsType.XSFCls, pntName) > 0) {
                            pntName = desName.trim() + String.valueOf(nameIndex);
                            nameIndex++;
                        }
                    }
                    destSfcls.close();
                    srcSfcls.close();
                    // 修改说明：对于6x点文件，再次投影到注记类或6x点文件上的注记，解决bug7081
                    // 修改人：周小飞 2015-09-21
                    if (srcDataGeomType == GeomType.GeomPnt) {
                        AnnotationCls srcWtAncls = new AnnotationCls();
                        if (srcWtAncls.openByURL("file:///" + srcUrl + "@ann") > 0) {
                            if (srcWtAncls.getObjCount() > 0) {
                                if (desUrl.toLowerCase().startsWith("gdbp://")) {
                                    //region 6x点文件投影到简单要素类时，再次投影6x点文件中注记到注记类
                                    AnnotationCls destAncls = new AnnotationCls();
                                    if (destAncls.create(desDir + "/acls/" + anclsName) > 0) {
                                        boolean projAncls = srcWtAncls.projTransEx(null, desSRS, destAncls, elpTransParam, isRotateSymbol);
                                        String gdbUrl = destAncls.getGDataBase().getURL();
                                        int clsID = destAncls.getClsID();
                                        destAncls.close();
                                        if (!projAncls) {
                                            DataBase delDB = DataBase.openByURL(gdbUrl);
                                            if (delDB != null) {
                                                AnnotationCls.remove(delDB, clsID);
                                                delDB.close();
                                            }
                                        }
                                    }
                                    //endregion
                                } else {
                                    //region 6x点文件投影到6x点文件时，再次投影6x点文件中注记到6x点文件
                                    AnnotationCls destWtAncls = new AnnotationCls();
                                    if (destWtAncls.openByURL(desUrl + "@ann") > 0) {
                                        srcWtAncls.projTransEx(null, desSRS, destWtAncls, elpTransParam, isRotateSymbol);
                                        destWtAncls.close();
                                    }
                                    //endregion
                                }
                            }
                            srcWtAncls.close();
                        }
                    }
                    // 修改说明：对于6x区文件，再次投影到点简单要素类或6x区文件上的点
                    // 修改人：周小飞 2017-11-29
                    else if (srcDataGeomType == GeomType.GeomReg) {
                        SFeatureCls srcWpPnt = new SFeatureCls();
                        if (srcWpPnt.openByURL(srcUrl + "@node") > 0) {
                            if (srcWpPnt.getObjCount() > 0) {
                                if (desUrl.toLowerCase().startsWith("gdbp://")) {
                                    //region 6x区文件投影到简单要素类时，再次投影6x区文件中点到点简单要素类
                                    SFeatureCls destPnt = new SFeatureCls();
                                    if (destPnt.create(desDir + "/sfcls/" + pntName, GeomType.GeomPnt) > 0) {
                                        boolean projAncls = srcWpPnt.projTransEx(null, desSRS, destPnt, elpTransParam, isRotateSymbol);
                                        String gdbUrl = destPnt.getGDataBase().getURL();
                                        int clsID = destPnt.getClsID();
                                        destPnt.close();
                                        if (!projAncls) {
                                            DataBase delDB = DataBase.openByURL(gdbUrl);
                                            if (delDB != null) {
                                                SFeatureCls.remove(delDB, clsID);
                                                delDB.close();
                                            }
                                        }
                                    }
                                    //endregion
                                } else {
                                    //region 6x区文件投影到6x区文件时，再次投影6x区文件中的点到6x区文件中的点
                                    SFeatureCls destWpPnt = new SFeatureCls();
                                    if (destWpPnt.openByURL(desUrl + "@node") > 0) {
                                        srcWpPnt.projTransEx(null, desSRS, destWpPnt, elpTransParam, isRotateSymbol);
                                        destWpPnt.close();
                                    }
                                    //endregion
                                }
                            }
                            srcWpPnt.close();
                        }
                    }
                    rtn = true;
                } else {
                    //结束投影转换-失败
                    curMsg = String.format("结束投影转换。 失败");
                    writeLog(curMsg);
                    if (desUrl.toLowerCase().startsWith("gdbp://")) {
                        int clsID = destSfcls.getClsID();
                        String gdbUrl = destSfcls.getGDataBase().getURL();
                        destSfcls.close();
                        DataBase delDB = DataBase.openByURL(gdbUrl);
                        if (delDB != null) {
                            SFeatureCls.remove(delDB, clsID);
                            delDB.close();
                        }
                    } else {
                        destSfcls.close();
                        File file = new File(desUrl);
                        if (file.exists()) {
                            file.delete();
                        }
                        file = new File(desUrl + "~");
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                    srcSfcls.close();
                }
                break;
                //endregion
            }
            case Acls: {
                //region 注记类 -> 注记类 或 6x点数据
                AnnotationCls ann = new AnnotationCls();
                if (ann.openByURL(srcUrl) <= 0) {
                    //打开源注记类失败
                    curMsg = String.format("打开源注记类失败。");
                    writeLog(curMsg);
                    return false;
                } else {
                    //打开源注记类成功
                    curMsg = String.format("打开源注记类成功。");
                    writeLog(curMsg);
                    AnnotationCls destann = new AnnotationCls();
                    if (desUrl.toLowerCase().startsWith("gdbp://")) {
                        desUrl = desDir + "/acls/" + desName;
                        if (destann.create(desUrl) <= 0) {
                            //创建目的注记类失败
                            curMsg = String.format("创建目的注记类失败。");
                            writeLog(curMsg);
                            ann.close();
                            return false;
                        } else {
                            //创建目的注记类成功
                            curMsg = String.format("创建目的注记类成功。");
                            writeLog(curMsg);
                        }
                    } else {
//                        desUrl = "file:///" + desDir + sep + desName + ".wt";
                        SFeatureCls sfcls = new SFeatureCls();
                        if (sfcls.create("file:///" + desUrl, GeomType.GeomPnt) <= 0) {
                            //创建目的注记类失败
                            curMsg = String.format("创建目的注记类失败。");
                            writeLog(curMsg);
                            ann.close();
                            return false;
                        } else {
                            //创建目的注记类成功
                            curMsg = String.format("创建目的注记类成功。");
                            writeLog(curMsg);
                            sfcls.close();
                        }
                        File file = new File(desUrl);
                        if (file.exists()) {
                            if (destann.openByURL("file:///" + desUrl + "@ann") <= 0) {
                                //打开目的注记类失败
                                //this.gridView1.RefreshRow(rowHandler);
                                ann.close();
                                return false;
                            }
                        }
                    }
                    destann.setModelName(ann.getModelName());
                    SRefData sourSRS = projItem.getSrcRefData();
                    SRefData desSRS = desSRefData;
                    //开始投影转换
                    curMsg = String.format("开始投影转换。");
                    writeLog(curMsg);
                    boolean isRotateSymbol = false;
                    boolean bRtn = ann.projTransEx(null, desSRS, destann, elpTransParam, isRotateSymbol);
                    if (bRtn) {
                        //结束投影转换-成功
                        curMsg = String.format("结束投影转换。 成功");
                        writeLog(curMsg);
                        destann.close();
                        rtn = true;
                    } else {
                        //结束投影转换-失败
                        curMsg = String.format("结束投影转换。 失败");
                        writeLog(curMsg);
                        if (desUrl.toLowerCase().startsWith("gdbp://")) {
                            int clsID = destann.getClsID();
                            String gdbUrl = destann.getGDataBase().getURL();
                            destann.close();
                            DataBase delDB = DataBase.openByURL(gdbUrl);
                            if (delDB != null) {
                                AnnotationCls.remove(delDB, clsID);
                                delDB.close();
                            }
                        } else {
                            destann.close();
                            File file = new File(desUrl);
                            if (file.exists())
                                file.delete();
                            file = new File(desUrl + "~");
                            if (file.exists())
                                file.delete();
                        }
                    }
                    ann.close();
                }
                break;
                //endregion
            }
            default:
                break;
        }

        return rtn;
    }

    /**
     * 写日志
     *
     * @param logText       日志内容
     */
    private void writeLog(String logText)
    {
        if(logFilePath == null || logFilePath.length() <=0) {
            return;
        }
        try {
            String str = logText;
            if (m_OutLogFile == null) {
                m_OutLogFile = new File(logFilePath);
            }

            // 如果文本文件不存在则创建它
            if (!m_OutLogFile.exists())
            {
                m_OutLogFile.createNewFile();
                m_OutLogFile = new File(logFilePath); //重新实例化
            }
            FileOutputStream logStream = new FileOutputStream(logFilePath, true);
            Writer logWriter = new OutputStreamWriter(logStream, "utf-8");
            logWriter.write(str);
            logWriter.write(System.getProperty("line.separator"));
            logWriter.close();
            logStream.flush();
            logStream.close();
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * 根据GDB数据的URL解析器所属目录
     * @param subFlag GDB数据类型过滤符
     */
    private String getGDBDir(String url, String subFlag) {
        String rtn = url;
        int index = url.indexOf(subFlag);
        if (index > -1) {
            rtn = url.substring(0, index);
        }
        return rtn;
    }

    /**
     * 获取参照系的字符串信息
     *
     * @param sRef     参照系
     * @param showName 字符串中是否需要显示名称，如参照系管理界面已经有显示名称了，就不需要再显示
     * @return
     */
    private String getSRefDataInfo(SRefData sRef, boolean showName)
    {
        String sRefInfo = "";
        if (sRef != null && sRef.getSRSName() != null && !sRef.getSRSName().isEmpty())
        {
            if (showName)
            {
                sRefInfo += String.format("名称:%s%n%n", sRef.getSRSName());
            }

            //region 投影坐标系信息
            if (sRef.getType() == SRefType.PRJ || (sRef.getType() == SRefType.JWD && sRef.getProjType().value() > 0))//3-投影坐标系，1-地理坐标系
            {
                sRefInfo += String.format("投影类型：%s", LanguageConvert.sRefProjTypeConvertEx(sRef.getProjType()));
                int index = sRef.getProjType().value();
                switch (index)
                {
                    case 23:
                        break;
                    default:
                        sRefInfo += String.format("%n    投影东偏：%f", sRef.getFalseEasting());
                        sRefInfo += String.format("%n    投影北偏：%f", sRef.getFalseNorthing());
                        break;
                }

                switch (index)
                {
                    case 23:
                    case 15:
                    case 17:
                    case 18:
                    case 21:
                    case 22:
                    {
                            sRefInfo += String.format("%n    中央经线：%f", sRef.getCentralMeridian());
                        break;
                    }
                    case 1:
                    case 5:
                    case 6:
                    case 24:
                    case 25:
                    case 26:
                    {
                        sRefInfo += String.format("%n    中央经线：%f", sRef.getCentralMeridian());
                        sRefInfo += String.format("%n    投影原点纬度：%f", sRef.getLatitudeOfOrigin());
                        break;
                    }
                    case 4:
                    case 16:
                    {
                        sRefInfo += String.format("%n    中央经线：%f", sRef.getCentralMeridian());
                        sRefInfo += String.format("%n    无变形纬度：%f", sRef.getLatitudeOfOrigin());
                        break;
                    }
                    case 2:
                    case 7:
                    {
                        sRefInfo += String.format("%n    中央经线：%f", sRef.getCentralMeridian());
                        sRefInfo += String.format("%n    投影原点纬度：%f", sRef.getLatitudeOfOrigin());
                        sRefInfo += String.format("%n    第一标准纬度：%f", sRef.getStandardParallel1());
                        sRefInfo += String.format("%n    第二标准纬度：%f", sRef.getStandardParallel2());
                        break;
                    }
                    case 3:
                    {
                        sRefInfo += String.format("%n    中央经线：%f", sRef.getCentralMeridian());
                        sRefInfo += String.format("%n    投影原点纬度：%f", sRef.getLatitudeOfOrigin());
                        sRefInfo += String.format("%n    第一标准纬度：%f", sRef.getStandardParallel1());
                        sRefInfo += String.format("%n    第二标准纬度：%f", sRef.getStandardParallel2());
                        sRefInfo += String.format("%n    比例因子：%f", sRef.getScaleFactor());
                        break;
                    }
                    case 8:
                    {
                        sRefInfo += String.format("%n    中央经线：%f", sRef.getCentralMeridian());
                        sRefInfo += String.format("%n    投影原点纬度：%f", sRef.getLatitudeOfOrigin());
                        sRefInfo += String.format("%n    比例因子：%f", sRef.getScaleFactor());
                        break;
                    }
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    {
                        sRefInfo += String.format("%n    中央经线：%f", sRef.getCentralMeridian());
                        sRefInfo += String.format("%n    投影中心点纬度：%f", sRef.getLatitudeOfOrigin());
                        break;
                    }
                    case 14:
                    {
                        sRefInfo += String.format("%n    中央经线：%f", sRef.getCentralMeridian());
                        sRefInfo += String.format("%n    投影中心点纬度：%f", sRef.getLatitudeOfOrigin());
                        sRefInfo += String.format("%n    透视点到球面的距离：%f", sRef.getStandardParallel1());
                        break;
                    }
                    case 19:
                    {
                        sRefInfo += String.format("%n    投影中心点的比例因子：%f", sRef.getCentralMeridian());
                        sRefInfo += String.format("%n    投影原点纬度：%f", sRef.getLatitudeOfOrigin());
                        sRefInfo += String.format("%n    定义中心投影线的第一经度：%f", sRef.getLongitudeOf1st());
                        sRefInfo += String.format("%n    定义中心投影线的第一纬度：%f", sRef.getStandardParallel1());
                        sRefInfo += String.format("%n    定义中心投影线的第二经度：%f", sRef.getLongitudeOf2nd());
                        sRefInfo += String.format("%n    定义中心投影线的第二纬度：%f", sRef.getStandardParallel2());
                        break;
                    }
                    case 20:
                    {
                        sRefInfo += String.format("%n    无变形纬度：%f", sRef.getLatitudeOfOrigin());
                        sRefInfo += String.format("%n    地球Y轴对应的经度：%f", sRef.getCentralMeridian());
                        break;
                    }
                    default:
                        break;
                }

                sRefInfo += String.format("%n    水平比例尺：%f", sRef.getRate());
                sRefInfo += String.format("%n    长度单位：%s", LanguageConvert.sRefLenUnitConvert(sRef.getUnit()));
                sRefInfo += String.format("%n    图形平移：dx = %f, dy = %f%n", sRef.getX(), sRef.getY());

                sRefInfo += String.format("%n地理坐标系：%s%n", sRef.getGCSName());
            }
            //endregion

            //region 地理坐标系信息

            ElpParam ep = ElpTransformation.getElpParam(sRef.getSpheroid().value());
            if (ep != null)
            {
                sRefInfo += String.format("标准椭球：%s", ep.getName());

                sRefInfo += String.format("%n    长轴：%f", sRef.getSemiMajorAxis());
                sRefInfo += String.format("%n    短轴：%f", sRef.getSemiMinorAxis());
                sRefInfo += String.format("%n    扁率：%f", sRef.getFlattening());
                sRefInfo += String.format("%n角度单位：%s", LanguageConvert.sRefLenUnitConvert(sRef.getAngUnit()));
                sRefInfo += String.format("%n本初子午线：%s", sRef.getPrimeMeridian());
                if (sRef.getPrimeMeridian() == "<自定义...>")
                {
                    double dms = sRef.getPMOffset();
                    boolean negative = dms < 0;
                    dms = Math.abs(dms);
                    int d = (int) Math.floor(dms / 10000);
                    if (negative)
                    {
                        d *= -1;
                    }
                    dms = dms % 10000;
                    sRefInfo += String.format(" (经度：%d度%d分%f秒)", d, (int) Math.floor(dms / 100), dms % 100.0);
                }
            }

            //endregion
        }
        return sRefInfo;
    }

    private boolean hasErrItem = false;
    private String errorMsg = "";

    //检查错误
    private void checkCheckError()
    {
        this.hasErrItem = false; //是否检测出错误参数项
        String errorMsg = "";
        this.labelMessage.setText("");
        for (int i = 0; i < this.projectDataItemList.size(); i++)
        {
            ProjectDataItem projItem = this.projectDataItemList.get(i);
            projItem.setCurErrorMsg("");
            projItem.setState(null);
            ProjectDataType projectDataType = projItem.getDataType();
            SRefData srcSRefData = projItem.getSrcRefData();
            SRefData desSRefData = this.desSRefSettingPane.getDesSrefData();
            GeomType geomType = projItem.getGeomType();
            String srcUrl = projItem.getSrcLUrl();
            String desDir = projItem.getDesDir();
            String desName = projItem.getDesName();
            String desUrl = desDir;
            String sep = File.separator;
            String curMsg = "";
            if (desDir.toLowerCase().startsWith("gdbp://")) {
                if (geomType == GeomType.GeomAnn)
                    desUrl = desDir + "/acls/" + desName;
                else
                    desUrl = desDir + "/sfcls/" + desName;
            } else {
                String ext = ".wt";
                if (geomType == GeomType.GeomPnt || geomType == GeomType.GeomAnn) {
                    ext = ".wt";
                } else if (geomType == GeomType.GeomLin) {
                    ext = ".wl";
                } else if (geomType == GeomType.GeomReg) {
                    ext = ".wp";
                }
                desUrl = desDir + sep + desName + ext;
            }
            switch (projectDataType)
            {
                case Sfcls:
                case Acls:
                case File6x:
                {
                    if (desSRefData == null)
                    {
                        errorMsg = "目的参照系为空!";
                        projItem.setCurErrorMsg(errorMsg);
                        this.hasErrItem = true;
                        projItem.setState("error");
                        continue;
                    }
                    if (desName == null || desName.trim().length() <=0)
                    {
                        errorMsg = "目的数据名为空!";
                        projItem.setCurErrorMsg(errorMsg);
                        this.hasErrItem = true;
                        projItem.setState("error");
                        continue;
                    }
                    if (desDir == null || desDir.trim().length() <=0)
                    {
                        errorMsg = "目的数据目录空!";
                        projItem.setCurErrorMsg(errorMsg);
                        this.hasErrItem = true;
                        projItem.setState("error");
                        continue;
                    }
                    if (projectDataType == ProjectDataType.Sfcls || projectDataType == ProjectDataType.Acls)
                    {
                        if (desDir.toLowerCase().startsWith("gdbp://")) {
                            String destDir = desDir;
                            DataBase pDstDB = DataBase.openByURL(URLParse.getDataBase(destDir));
                            if (pDstDB == null) {
                                errorMsg = "目的数据目录不存在!";
                                projItem.setCurErrorMsg(errorMsg);
                                this.hasErrItem = true;
                                projItem.setState("error");
                                continue;
                            }

                            SimpleStringProperty dsNamePropperty = new SimpleStringProperty();
                            String dsName = dsNamePropperty.getValue();
                            XClsType rtnXClsType = URLParse.getXClsType(destDir, dsNamePropperty);
                            if (rtnXClsType == XClsType.XFds && dsName != null && dsName.length() > 0) {
                                if (pDstDB.xClsIsExist(XClsType.XFds, dsName) <= 0) {
                                    pDstDB.close();
                                    errorMsg = "目的数据目录不存在!";
                                    projItem.setCurErrorMsg(errorMsg);
                                    this.hasErrItem = true;
                                    projItem.setState("error");
                                    continue;
                                }
                            }

                            XClsType clsType = srcUrl.contains("/sfcls/") ? XClsType.XSFCls : XClsType.XACls;
                            int id = (int) pDstDB.xClsIsExist(clsType, desName);
                            pDstDB.close();
                            if (id > 0) {
                                errorMsg = "目的数据已存在!";
                                projItem.setCurErrorMsg(errorMsg);
                                this.hasErrItem = true;
                                projItem.setState("error");
                                continue;
                            }
                        }
                        else
                        {
                            File desDirFile = new File(desDir);
                            if (!(desDirFile.isDirectory() && desDirFile.exists()))
                            {
                                errorMsg = "目的数据目录不存在!";
                                projItem.setCurErrorMsg(errorMsg);
                                this.hasErrItem = true;
                                projItem.setState("error");
                                continue;
                            }

                            File desUrlFile = new File(desUrl);
                            if (desUrlFile.exists())
                            {
                                errorMsg = "目的数据已存在!";
                                projItem.setCurErrorMsg(errorMsg);
                                this.hasErrItem = true;
                                projItem.setState("error");
                                continue;
                            }
                        }
                    }
                    else
                    {
                        File desDirFile = new File(desDir);
                        if (!(desDirFile.isDirectory() && desDirFile.exists()))
                        {
                            errorMsg = "目的数据目录不存在!";
                            projItem.setCurErrorMsg(errorMsg);
                            this.hasErrItem = true;
                            projItem.setState("error");
                            continue;
                        }

                        File desUrlFile = new File(desUrl);
                        if (desUrlFile.exists())
                        {
                            errorMsg = "目的数据已存在!";
                            projItem.setCurErrorMsg(errorMsg);
                            this.hasErrItem = true;
                            projItem.setState("error");
                            continue;
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ProjectDataItem curSelectedItem = this.projectDataItemTableView.getSelectionModel().getSelectedItem();
        if(curSelectedItem != null)
        {
            this.labelMessage.setText(curSelectedItem.getCurErrorMsg());
        }
        //this.projectDataItemTableView.refresh();
    }


}
