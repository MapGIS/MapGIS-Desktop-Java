package com.zondy.mapgis.mapeditor.projecttransform;

import com.zondy.mapgis.base.*;
import com.zondy.mapgis.cache.MapTileCache;
import com.zondy.mapgis.common.URLParse;
import com.zondy.mapgis.filedialog.GDBSaveFileDialog;
import com.zondy.mapgis.geodatabase.DataBase;
import com.zondy.mapgis.geodatabase.LogEventReceiver;
import com.zondy.mapgis.geodatabase.XClsType;
import com.zondy.mapgis.geodatabase.event.IStepEndListener;
import com.zondy.mapgis.geodatabase.event.IStepMessageListener;
import com.zondy.mapgis.geodatabase.event.IStepStartListener;
import com.zondy.mapgis.geodatabase.event.ProgressStatus;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.util.List;
import java.util.Optional;

/**
 * 瓦片类批量投影
 */
public class MultiProjectOfTileDialog extends Dialog{
    private Stage stage = null;
    private LogEventReceiver m_LogEventReceiver = null;

    /**
     * 构造方法
     */
    public MultiProjectOfTileDialog() {
        setTitle("瓦片投影转换");
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
//        toolBar.getItems().addAll(btnAdd, new Separator(), btnDel, new Separator(), btnModify);
//        toolBar.getItems().addAll(btnAdd, btnDel, btnModify);
        toolBar.getItems().addAll(btnAdd, btnDel, btnModify,btnSaveLog,btnViewLog);
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
        tc_srcUrl.setPrefWidth(400);
        TableColumn<ProjectDataItem, String> tc_desName = new TableColumn<>("目的数据名");
        tc_desName.setMinWidth(100);
        projectDataItemTableView.getColumns().addAll(tc_srcName, tc_srcUrl, tc_desName);
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
                    }
                };
            }
        });
        //单击行显示对应的目的数据目录及对应的源参照系信息
        projectDataItemTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ProjectDataItem>() {
            @Override
            public void changed(ObservableValue<? extends ProjectDataItem> observable, ProjectDataItem oldValue, ProjectDataItem newValue) {
                if (newValue != null) {
                    String desDir = newValue.getDesDir();
                    desDirText.setText(desDir);
                    //单击行同时更新对应的源参照系信息
                    String srefInfo = getSRefDataInfo(newValue.getSrcRefData(),true);
                    srcSRefInfoPane.setSrcSRefInfoText(srefInfo);
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
        desDirGrid.add(desDirSettingLabel,0,0);
        desDirGrid.add(desDirText,1,0);
        desDirGrid.add(desDirBtn,2,0);
        desDirGrid.setHgap(5);
        desDirGrid.setVgap(5);
        desDirGrid.setPadding(new Insets(5,5,5,5));
        TitledPane desDirSettingTitledPane = new TitledPane();
        desDirSettingTitledPane.setText("输出设置");
        desDirSettingTitledPane.setCollapsible(false);
        desDirSettingTitledPane.setContent(desDirGrid);
        desDirSettingTitledPane.setPadding(new Insets(5, 5, 5, 5));
        gridPane.add(desDirSettingTitledPane, 1, rowIndex);


        //endregion

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
        //移除数据
        this.btnDel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ObservableList<ProjectDataItem> projectDataItemsSelected = projectDataItemTableView.getSelectionModel().getSelectedItems();
                if(projectDataItemsSelected != null && projectDataItemsSelected.size() >0) {
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
                if(projectDataItemsSelected != null && projectDataItemsSelected.size() >0)
                {
                    UnificationModifyDialog dialog = new UnificationModifyDialog();
                    dialog.setModifyGDBDir(false);
                    Optional<ButtonType> rtn  = dialog.showAndWait();
                    if(rtn != null && rtn.get() == ButtonType.OK)
                    {
                        for(ProjectDataItem dataItem :projectDataItemsSelected)
                        {
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
//                            if (dialog.isModifyGDBDir() && dialog.getGDBDir() != null && dialog.getGDBDir().length() > 0) {
//                                if (dataItem.getSrcLUrl().toUpperCase().startsWith("GDBP://")) {
//                                    dataItem.setDesDir(dialog.getGDBDir());
//                                }
//                            }
//                            if(dialog.isModifyFileDir() && dialog.getFielDir() != null && dialog.getFielDir().length() >0)
//                            {
//                                if(!dataItem.getSrcLUrl().toUpperCase().startsWith("GDBP://"))
//                                {
//                                    dataItem.setDesDir(dialog.getFielDir());
//                                }
//                            }
                        }
                        projectDataItemTableView.refresh();
                    }
                }
                //刷新当前选择的行的目的数据目录
                ProjectDataItem curSelectedItem = projectDataItemTableView.getSelectionModel().getSelectedItem();
                if(curSelectedItem != null)
                {
                    String desDir = curSelectedItem.getDesDir();
                    desDirText.setText(desDir);
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
                        Runtime.getRuntime().exec("notepad " + file);
                    } catch (IOException ex) {

                    }
                }
            }
        });
        //修改目的数据目录
        desDirBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ProjectDataItem item = projectDataItemTableView.getSelectionModel().getSelectedItem();
                if (item != null) {
                    DirectoryChooser directoryChooser = new DirectoryChooser();
                    //directoryChooser.setTitle("浏览文件夹");
                    File directory = directoryChooser.showDialog(stage);
                    if (directory != null) {
                        String dir = directory.getAbsolutePath();
                        desDirText.setText(dir);
                        item.setDesDir(dir);
                    }
                }
            }
        });
    }

    /**
     * 处理添加功能
     */
    private void btnAddClick()
    {
        FileChooser fileChooser = new FileChooser();
        if(XFunctions.isSystemWindows())
        {
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("tdf", "*.tdf"),
                    new FileChooser.ExtensionFilter("mut", "*.mut"));
        }
        else
        {
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("mut", "*.mut"));
        }

        List<File> files = fileChooser.showOpenMultipleDialog(stage);
        if (files != null && files.size() > 0) {
            for (int i = 0; i < files.size(); i++) {
                File file = files.get(i);
                String url = file.getAbsolutePath();
                addRowData(url);
            }
        }
    }

    /**
     * 添加源数据到列表中
     */
    private void addRowData(String url)
    {
        ProjectDataItem dataItem = new ProjectDataItem();
        {
            MapTileCache mapTileCache = new MapTileCache();
            try {
                if (mapTileCache.open(url, 0) > 0) {
                    String name = getNameFromFileUrl(url);
                    File file = new File(url);
                    String desDir = file.getParent();
                    SRefData srcSRefData = mapTileCache.getSRS();
                    dataItem.setSrcLUrl(url);
                    dataItem.setSrcName(name);
                    dataItem.setDesName(name);
                    dataItem.setDesDir(desDir);
                    if (url.toLowerCase().endsWith(".tdf"))
                        dataItem.setDataType(ProjectDataType.Tdf);
                    else
                        dataItem.setDataType(ProjectDataType.Mut);
                    dataItem.setSrcRefData(srcSRefData);
                    String srefInfo = getSRefDataInfo(srcSRefData, true);
                    srcSRefInfoPane.setSrcSRefInfoText(srefInfo);
                    projectDataItemList.addAll(dataItem);
                    projectDataItemTableView.refresh();
                    projectDataItemTableView.getSelectionModel().select(0);
                    mapTileCache.close();
                    mapTileCache.dispose();

                }
            } catch (Exception e) {
                //打开瓦片失败
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
        //对每条源数据进行检查，去掉重复路径
        //然后遍历每一条有效源数据进行投影转换操作
        //筛选出的有效数据进行投影
       // ArrayList<ProjectDataItem> validProjectItems = new ArrayList<>();
        ObservableList<ProjectDataItem> projectItems = this.projectDataItemList;
        if(projectItems != null || projectItems.size() >0)
        {
            String str = String.format("开始投影转换，共计%d个转换项。",projectItems.size());
            writeLog(str);
            int successNum = 0; //统计转换成功的数目
            for (int i = 0; i < projectItems.size(); i++) {
                ProjectDataItem projItem = projectItems.get(i);
                String curMsg = String.format("开始转换第%d项。", i + 1);
                writeLog(curMsg);
                if (projItem.getDesName() != null && projItem.getDesName().length() > 0) {
                    curMsg = String.format("当前转换数据:%s", projItem.getSrcLUrl());
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
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "转换结束!", ButtonType.OK);
            alert.show();
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "请先添加源数据!", ButtonType.OK);
            alert.show();
            event.consume();
        }
    }

    /**
     * 转换每一条数据
     */
    private boolean beginConvert(ProjectDataItem projItem)
    {
        SRefData desSRefData = desSRefSettingPane.getDesSrefData();
        ElpTransParam elpTransParam = elpTransParamSettingPane.getElpTransParam();
        boolean rtn = false;
        String sep = File.separator;
        String desUrl = projItem.getDesDir();
        String curMsg = "";
        String ext = ".tdf";
        if(projItem.getDataType() == ProjectDataType.Mut)
            ext = ".mut";
        desUrl += sep + projItem.getDesName() + ext;
        File file = new File(desUrl);
        if(file.exists())
        {
            //结果文件已存在 不处理
            curMsg = String.format("结果文件已存在。");
            writeLog(curMsg);
            return false;
        }
        //region 瓦片数据->瓦片数据
        MapTileCache mapTitle = new MapTileCache();
        int openRtn = 0;
        try {
            openRtn = mapTitle.open(projItem.getSrcLUrl(), 1);
        } catch (Exception e) {
        }

        if (openRtn < 1) {
            //打开源数据失败
            curMsg = String.format("打开源瓦片失败。");
            writeLog(curMsg);
           return false;
        } else {
            //打开源数据成功
            curMsg = String.format("打开源瓦片成功。");
            writeLog(curMsg);
        }

        SRefData sourSRS = projItem.getSrcRefData();
        SRefData desSRS = desSRefData;
        //开始投影转换
        if (m_LogEventReceiver == null) {
            m_LogEventReceiver = new LogEventReceiver();
            m_LogEventReceiver.addStepStartListener(new IStepStartListener() {
                @Override
                public void onStepStart(String name) {
                    String curMsg = "";
                    curMsg = String.format("转换中信息：开始%s",name);
                    writeLog(curMsg);
                }
            });
            m_LogEventReceiver.addStepMessageListener(new IStepMessageListener() {
                @Override
                public void onStepMessage(String message) {
                    String curMsg = "";
                    curMsg = String.format("转换中信息：%s",message);
                    writeLog(curMsg);
                }
            });
            m_LogEventReceiver.addStepEndListener(new IStepEndListener() {
                @Override
                public void onStepEnd(ProgressStatus status, double progress, String stepName, boolean isAppendLog) {
                    String curMsg = "";
                    String rtn = status == ProgressStatus.Succeeded ? "成功":"失败";
                    curMsg = String.format("转换中信息：结束%s  %s",stepName,rtn);
                    writeLog(curMsg);
                }
            });
        }
        curMsg = String.format("开始投影转换。");
        writeLog(curMsg);
        int bRtn = mapTitle.projTrans(sourSRS, desSRS, elpTransParam, desUrl, null, m_LogEventReceiver);
        if (bRtn < 1) {
            //结束转换-转换失败
            curMsg = String.format("结束转换。 转换失败");
            writeLog(curMsg);
        } else {
            //结束转换-转换成功
            curMsg = String.format("结束转换。 转换成功");
            writeLog(curMsg);
            rtn = true;
        }
        mapTitle.close();
        mapTitle.dispose();
        //endregion

       return  rtn;
    }

    /**
     * 写日志
     *
     * @param logText       日志内容
     */
    private void writeLog(String logText)
    {
        if(logFilePath == null || logFilePath.length() <=0)
            return;
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
     * 根据路径取文件名
     */
    private String getNameFromFileUrl(String url)
    {
        String name = url;
        String sep = java.io.File.separator;
        int index = name.lastIndexOf(sep);
        if(index >-1)
        {
            name = name.substring(index + 1);
            index = name.lastIndexOf(".");
            if(index >-1)
            {
                name = name.substring(0,index);
            }
        }
        return name;
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
        this.hasErrItem = false;
        String errorMsg = "";
        for (int i = 0; i < this.projectDataItemList.size(); i++)
        {
            ProjectDataItem projItem = this.projectDataItemList.get(i);
            projItem.setCurErrorMsg("");
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
            String ext = ".tdf";
            if(projItem.getDataType() == ProjectDataType.Mut)
                ext = ".mut";
            desUrl += sep + projItem.getDesName() + ext;
            switch (projectDataType)
            {
                case Tdf:
                case Mut:
                {
                    if (srcSRefData == null)
                    {
                        errorMsg = "源参照系为空!";
                        projItem.setCurErrorMsg(errorMsg);
                        this.hasErrItem = true;
                        continue;
                    }
                    if (desSRefData == null)
                    {
                        errorMsg = "目的参照系为空!";
                        projItem.setCurErrorMsg(errorMsg);
                        this.hasErrItem = true;
                        continue;
                    }
                    if (desName == null || desName.trim().length() <=0)
                    {
                        errorMsg = "目的数据名为空!";
                        projItem.setCurErrorMsg(errorMsg);
                        this.hasErrItem = true;
                        continue;
                    }
                    if (desDir == null || desDir.trim().length() <=0)
                    {
                        errorMsg = "目的数据目录空!";
                        projItem.setCurErrorMsg(errorMsg);
                        this.hasErrItem = true;
                        continue;
                    }
                    File desDirFile = new File(desDir);
                    if (!(desDirFile.isDirectory() && desDirFile.exists()))
                    {
                        errorMsg = "目的数据目录不存在!";
                        projItem.setCurErrorMsg(errorMsg);
                        this.hasErrItem = true;
                        continue;
                    }

                    File desUrlFile = new File(desUrl);
                    if (desUrlFile.exists())
                    {
                        errorMsg = "目的数据已存在!";
                        projItem.setCurErrorMsg(errorMsg);
                        this.hasErrItem = true;
                        continue;
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }
}
