package com.zondy.mapgis.dataconvert;

import com.zondy.mapgis.analysis.g3danalysis.GeoDataConv3D;
import com.zondy.mapgis.analysis.imageanalysis.RasTrans;
import com.zondy.mapgis.analysis.imageanalysis.datatype.MSRSDataType;
import com.zondy.mapgis.base.*;
import com.zondy.mapgis.common.URLParse;
import com.zondy.mapgis.controls.common.*;
import com.zondy.mapgis.dataconvert.option.SourcePane;
import com.zondy.mapgis.dataconvert.option.UnificationButton;
import com.zondy.mapgis.dataconvert.option.UnificationDialog;
import com.zondy.mapgis.filedialog.FolderType;
import com.zondy.mapgis.filedialog.GDBOpenFileDialog;
import com.zondy.mapgis.filedialog.GDBSelectFolderDialog;
import com.zondy.mapgis.geodatabase.*;
import com.zondy.mapgis.geodatabase.config.MapGisEnv6x;
import com.zondy.mapgis.geodatabase.convert.ConvertOptionType;
import com.zondy.mapgis.geodatabase.convert.DataConvert;
import com.zondy.mapgis.geodatabase.convert.FeatureInfo;
import com.zondy.mapgis.geodatabase.convert.SourceInfoType;
import com.zondy.mapgis.geodatabase.event.ProgressStatus;
import com.zondy.mapgis.geodatabase.raster.RasterCatalog;
import com.zondy.mapgis.geometry.Dot3D;
import com.zondy.mapgis.geometry.GeomType;
import com.zondy.mapgis.sref.SRefManagerDialog;
import com.zondy.mapgis.srs.SRefData;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 数据转换主界面
 *
 * @author CR
 * @file DataConvertDialog.java
 * @brief 数据转换主界面
 * @create 2020-03-11.
 */
class DataConvertDialog extends Dialog {
    //region 变量
    private final TableView<ConvertItem> tableView = new TableView<>();
    private final CheckBox checkClose = new CheckBox("转换全部成功后关闭此对话框");
    private final VBox vBoxParams = new VBox(6);
    private final SourcePane sourcePane = new SourcePane();//源信息界面
    private final TitledPane titledPaneParams = new TitledPane("参数设置", null);
    private final TitledPane titledPaneSource = new TitledPane("源信息", this.sourcePane);
    private final Tooltip tooltipError = new Tooltip();
    private BooleanProperty isConverting = new SimpleBooleanProperty(false);
    private final Label labelMessage = new Label();
    private boolean hasConvertSuccess = false;
    private String logFilePath = null;
    private boolean clickedClose = false;
    private boolean hasConvertFailed = false;//标记是否有转换失败的。

    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    //endregion

    //region 构造函数
    //请勿使用此构造方法实现导入，使用DataConverts类实现
    public DataConvertDialog() {
        //region 初始化Owner和标题
        if (XFunctions.isSystemWindows()) {
            this.setResizable(true);
        }

        this.setTitle("数据转换");
        //endregion

        //region 工具条
        Button buttonAdd = new Button("添加数据", new ImageView(new Image(getClass().getResourceAsStream("addFiles_20.png"))));
        Button buttonAddArcGDB = new Button("添加ArcGIS File GDB", new ImageView(new Image(getClass().getResourceAsStream("addarcfilegdb_20.png"))));
        Button buttonAddGDB = new Button("添加地理数据库", new ImageView(new Image(getClass().getResourceAsStream("addgdbdatas_20.png"))));
        Button buttonRemove = new Button("移除", new ImageView(new Image(getClass().getResourceAsStream("remove_20.png"))));
        Button buttonSelectAll = new Button("全选", new ImageView(new Image(getClass().getResourceAsStream("selectAll_20.png"))));
        Button buttonUnSelectAll = new Button("全不选", new ImageView(new Image(getClass().getResourceAsStream("unselectAll_20.png"))));
        Button buttonUnifiedEdit = new Button("统改", new ImageView(new Image(getClass().getResourceAsStream("edit_20.png"))));
        Button buttonSetting = new Button("设置", new ImageView(new Image(getClass().getResourceAsStream("setting_20.png"))));
        Button buttonLog = new Button("查看日志", new ImageView(new Image(getClass().getResourceAsStream("viewlog_20.png"))));
        buttonAdd.setTooltip(new Tooltip("添加数据"));
        buttonAddGDB.setTooltip(new Tooltip("添加地理数据库"));
        buttonAddArcGDB.setTooltip(new Tooltip("添加ArcGIS File GDB"));
        buttonRemove.setTooltip(new Tooltip("移除选中的项"));
        buttonSelectAll.setTooltip(new Tooltip("全选"));
        buttonUnSelectAll.setTooltip(new Tooltip("全不选"));
        buttonUnifiedEdit.setTooltip(new Tooltip("统改选中的项"));
        buttonSetting.setTooltip(new Tooltip("设置"));
        buttonLog.setTooltip(new Tooltip("查看日志"));

        buttonAdd.setOnAction(event ->
        {
            GDBOpenFileDialog dlg = new GDBOpenFileDialog();
            if (XFunctions.isSystemWindows()) {
                dlg.setFilter("所有支持文件(MapGIS、ArcGIS)|sfcls;acls;ocls;ds;ras;rcat;*.wt;*.wl;*.wp;*.shp;*.mdb;*.gdb" +
                        "|MapGIS数据库数据(sfcls;acls;ocls;ds;ras;rcat)|sfcls;acls;ocls;ds;ras;rcat" +
                        "|MapGIS 6x文件(*.wt;*.wl;*.wp)|*.wt;*.wl;*.wp" +
                        "|ArcGIS(*.shp;*.mdb;*.gdb)|*.shp;*.mdb;*.gdb" +
                //增加三维数据支持 0414 ysp 暂时屏蔽
                        "|三维模型文件(*.3ds;*.obj;*.dae;*.osgb;*.fbx;*.xml;*.x;*.las)|*.3ds;*.obj;*.dae;*.osgb;*.fbx;*.xml;*.x;*.las");
                //e00数据也去掉了。
                //"ArcGIS(*.shp;*.e00;*.grd;*.dbf;*.mdb;*.gdb)|*.shp;*.e00;*.grd;*.dbf;*.mdb;*.gdb"
                //"AutoCAD(*.dxf;*.dwg;*.dgn)|*.dxf;*.dwg;*.dgn"
                //"其他矢量文件(*.mif;*.vct;*.gml;*.kml;*.txt;*.json)|*.mif;*.vct;*.gml;*.kml;*.txt;*.json"
                //"表格文件(*.wb;*.xls;*.xlsx;*.mdb;*.accdb)|*.wb;*.xls;*.xlsx;*.mdb;*.accdb"
                //"影像文件(*.inf;*.msi;*.tif;*.img;*.bmp;*.jpg;*.gif;*.jp2;*.png;*.h5;*.grd;*.adf;*.bil)|*.inf;*.msi;*.tif;*.tiff;*.img;*.bmp;*.jpg;*.jpeg;*.gif;*.jp2;*.png;*.h5;*.grd;*.adf;*.bil"
                //"三维模型文件(*.3ds;*.obj;*.dae;*.osgb;*.fbx;*.xml;*.x;*.las)|*.3ds;*.obj;*.dae;*.osgb;*.fbx;*.xml;*.x;*.las"
                //MAPGIS_GDB
                //ARCINFO_COVERAGE
            } else {
                dlg.setFilter("所有支持文件(MapGIS、ArcGIS)|sfcls;acls;ocls;ds;ras;rcat;*.wt;*.wl;*.wp;*.shp;*.gdb" +
                        "|MapGIS数据库数据(sfcls;acls;ocls;ds;ras;rcat)|sfcls;acls;ocls;ds;ras;rcat" +
                        "|ArcGIS(*.shp;*.gdb)|*.shp;*.gdb"+
                        //增加三维数据支持 0414 ysp
                        "|三维模型文件(*.3ds;*.obj;*.dae;*.osgb;*.fbx;*.xml;*.x;*.las)|*.3ds;*.obj;*.dae;*.osgb;*.fbx;*.xml;*.x;*.las");
            }

            dlg.setMultiSelect(true);
            Optional<String[]> optional = dlg.showAndWait();
            if (optional != null && optional.isPresent()) {
                String[] files = optional.get();
                if (files != null && files.length > 0) {
                    List<String> urlList = Arrays.asList(files);
                    this.addConvertItems(urlList);
                }
            }
        });
        buttonAddArcGDB.setOnAction(event ->
        {
            GDBSelectFolderDialog dlg = new GDBSelectFolderDialog();
            dlg.setFolderType(FolderType.Disk_Folder);
            Optional<String[]> optional = dlg.showAndWait();
            if (optional != null && optional.isPresent()) {
                List<String> urlList = Arrays.asList(optional.get());
                this.addConvertItems(urlList);
            }
        });
        buttonAddGDB.setOnAction(event ->
        {
            GDBSelectFolderDialog dlg = new GDBSelectFolderDialog();
            dlg.setFolderType(FolderType.MapGIS_DataBase);
            Optional<String[]> optional = dlg.showAndWait();
            if (optional != null && optional.isPresent()) {
                List<String> urlList = Arrays.asList(optional.get());
                this.addConvertItems(urlList);
            }
        });
        buttonRemove.setOnAction(event ->
        {
            this.tableView.getItems().removeAll(this.tableView.getSelectionModel().getSelectedItems());
        });
        buttonSelectAll.setOnAction(event ->
        {
            this.tableView.getSelectionModel().selectAll();
            this.tableView.requestFocus();
        });
        buttonUnSelectAll.setOnAction(event ->
        {
            this.tableView.getSelectionModel().clearSelection();
            this.tableView.requestFocus();
        });
        buttonUnifiedEdit.setOnAction(event ->
        {
            ObservableList<ConvertItem> itemList = tableView.getSelectionModel().getSelectedItems();
            if (itemList.size() <= 0) {
                Notification.showInformation(getCurrentWindow(), "统改", "请至少选中一个转换项。");
            } else {
                UnificationDialog dlg = new UnificationDialog(itemList);
                if (dlg.showAndWait().equals(Optional.of(ButtonType.OK))) {
                    this.checkConvertItems();
                }
            }
        });
        buttonSetting.setOnAction(event ->
        {

        });
        buttonLog.setOnAction(event ->
        {
            File file = new File(this.logFilePath);
            if (file.isFile()) {
                try {
                    if (XFunctions.isSystemWindows()) {
                        Runtime.getRuntime().exec(String.format("cmd.exe /c notepad %s", logFilePath));
                    } else {
                        Runtime run = Runtime.getRuntime();
                        run.exec(String.format("deepin-editor %s", logFilePath));
                    }
                } catch (IOException e1) {
                    MessageBox.information("日志文件打开失败。");
                }
            } else {
                MessageBox.information("日志文件不存在.");
            }
        });

        this.labelMessage.setTextFill(Paint.valueOf("red"));
        HBox hBoxLoc = new HBox();//占位控件，用于使得后面的消息Label靠右停
        HBox.setHgrow(hBoxLoc, Priority.ALWAYS);
        ZDToolBar toolBar = new ZDToolBar(buttonAdd, buttonAddGDB, buttonAddArcGDB, new Separator(), buttonSelectAll, buttonUnSelectAll, buttonUnifiedEdit, new Separator(), buttonSetting, buttonRemove, buttonLog, hBoxLoc, this.labelMessage);
        buttonSetting.setVisible(false);//暂时隐藏，未实现
        buttonSetting.setManaged(false);//暂时隐藏，未实现
        //endregion

        //region 转换列表
        //region 初始化
        TableColumn<ConvertItem, Integer> tcCount = new TableColumn<>("序号");
        TableColumn<ConvertItem, ConvertState> tcState = new TableColumn<>("状态");
        TableColumn<ConvertItem, DataType> tcSourType = new TableColumn<>("源类型");
        TableColumn<ConvertItem, String> tcSourName = new TableColumn<>("源名称");
        TableColumn<ConvertItem, DataType> tcDestType = new TableColumn<>("目的类型");
        TableColumn<ConvertItem, String> tcDestName = new TableColumn<>("目的名称");
        TableColumn<ConvertItem, String> tcDestPath = new TableColumn<>("目的路径");
        TableColumn<ConvertItem, Double> tcProgress = new TableColumn<>("转换进度");
        this.tableView.getColumns().addAll(tcCount, tcState, tcSourType, tcSourName, tcDestType, tcDestName, tcDestPath, tcProgress);
        this.tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tcCount.setPrefWidth(37);
        tcCount.setResizable(false);
        tcState.setPrefWidth(37);
        tcState.setResizable(false);
        tcSourType.setPrefWidth(52);
        tcSourType.setResizable(false);
        tcProgress.setPrefWidth(120);
        tcProgress.setResizable(false);
        tcDestType.setPrefWidth(150);
        tcSourName.setPrefWidth(110);
        tcDestName.setPrefWidth(120);
        tcDestPath.prefWidthProperty().bind(tableView.widthProperty().subtract(2).subtract(tcCount.getWidth()).subtract(tcState.getWidth()).subtract(tcSourType.getWidth()).subtract(tcSourName.getWidth()).subtract(tcDestType.getWidth()).subtract(tcDestName.getWidth()).subtract(tcProgress.getWidth()));

        tcCount.setSortable(false);
        tcProgress.setSortable(false);
        tcState.sortableProperty().bind(isConverting.not());
        tcSourType.sortableProperty().bind(isConverting.not());
        tcSourName.sortableProperty().bind(isConverting.not());
        tcDestType.sortableProperty().bind(isConverting.not());
        tcDestName.sortableProperty().bind(isConverting.not());
        tcDestPath.sortableProperty().bind(isConverting.not());

        this.tableView.setEditable(true);
        tcState.setEditable(false);
        tcSourType.setEditable(false);
        tcSourName.setEditable(false);
        tcProgress.setEditable(false);

        tcState.setCellValueFactory(new PropertyValueFactory<>("state"));
        tcSourType.setCellValueFactory(new PropertyValueFactory<>("sourType"));
        tcSourName.setCellValueFactory(new PropertyValueFactory<>("sourName"));
        tcDestType.setCellValueFactory(new PropertyValueFactory<>("destType"));
        tcDestName.setCellValueFactory(new PropertyValueFactory<>("destName"));
        tcDestPath.setCellValueFactory(new PropertyValueFactory<>("destPath"));
        tcProgress.setCellValueFactory(new PropertyValueFactory<>("progress"));
        //endregion

        //region 事件监控
        this.tableView.getItems().addListener((ListChangeListener<ConvertItem>) c -> {
            while (c.next()) {
                checkConvertItems();
            }
        });
        this.tableView.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) ->
        {
            this.setErrorMessage(nv);
            this.setParams(nv);
        });
        //endregion

        //region 编辑
        tcCount.setCellFactory(param -> new TableCell<ConvertItem, Integer>() {
            @Override
            public void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                this.setText(null);
                if (!empty) {
                    int rowIndex = this.getIndex() + 1;
                    this.setText(String.valueOf(rowIndex));
                }
            }
        });
        tcState.setCellFactory(param -> new TableCell<ConvertItem, ConvertState>() {
            @Override
            protected void updateItem(ConvertState item, boolean empty) {
                super.updateItem(item, empty);
                ImageView imageView = null;
                if (!empty) {
                    String imageName = item.getImageName();
                    if (!XString.isNullOrEmpty(imageName)) {
                        imageView = new ImageView(new Image(getClass().getResourceAsStream(imageName)));
                    }
                }
                setGraphic(imageView);
                setAlignment(Pos.CENTER);
            }
        });
        tcSourType.setCellFactory(param -> new TableCell<ConvertItem, DataType>() {
            @Override
            protected void updateItem(DataType item, boolean empty) {
                super.updateItem(item, empty);
                ImageView imageView = null;
                if (!empty) {
                    Image image = item.getImage();
                    if (image != null) {
                        imageView = new ImageView(image);
                    }
                }
                setGraphic(imageView);
                setAlignment(Pos.CENTER);
            }
        });
        tcDestType.setCellFactory(param -> new TableCell<ConvertItem, DataType>() {
            private ZDComboBox<DataType> comboBox = new ZDComboBox<DataType>(FXCollections.observableArrayList());
            private DataType editValue = null;

            @Override
            public void startEdit() {
                super.startEdit();
                ConvertItem convertItem = (ConvertItem) getTableRow().getItem();
                if (convertItem != null) {
                    if (comboBox.getItems().size() == 0) {
                        comboBox.getItems().addAll(convertItem.getDestTypes());
                    }
                    comboBox.setConverter(new StringConverter<DataType>() {
                        @Override
                        public String toString(DataType object) {
                            return object.getText();
                        }

                        @Override
                        public DataType fromString(String String) {
                            return EnumUtils.valueOfText(DataType.class, String);
                        }
                    });
                    comboBox.setCellFactory(param -> new ListCell<DataType>() {
                        @Override
                        protected void updateItem(DataType item, boolean empty) {
                            super.updateItem(item, empty);
                            String text = null;
                            ImageView imageView = null;
                            if (!empty) {
                                text = item.getText();
                                Image image = item.getImage();
                                if (image != null) {
                                    imageView = new ImageView(image);
                                }
                            }
                            setText(text);
                            setGraphic(imageView);
                        }
                    });
                    comboBox.setButtonCell(new ListCell<DataType>() {
                        @Override
                        protected void updateItem(DataType item, boolean empty) {
                            super.updateItem(item, empty);
                            String text = null;
                            ImageView imageView = null;
                            if (!empty) {
                                text = item.getText();
                                Image image = item.getImage();
                                if (image != null) {
                                    imageView = new ImageView(image);
                                }
                            }
                            setText(text);
                            setGraphic(imageView);
                        }
                    });
                    //TODO: 根据源类型添加可转换的目标类型
                    comboBox.setOnShown(event ->
                    {
                        editValue = comboBox.getValue();
                    });
                    comboBox.setOnHidden(event ->
                    {
                        if (editValue != comboBox.getValue()) {
                            commitEdit(comboBox.getValue());
                        } else {
                            cancelEdit();
                        }
                    });
                    comboBox.focusedProperty().addListener((o, ov, nv) ->
                    {
                        if (!nv) {
                            cancelEdit();
                        }
                    });
                    comboBox.setValue(getItem());
                    comboBox.prefWidthProperty().bind(getTableColumn().widthProperty());
                    this.setGraphic(comboBox);
                    comboBox.requestFocus();
                }
            }

            @Override
            public void commitEdit(DataType value) {
                super.commitEdit(value);

                ConvertItem convertItem = (ConvertItem) getTableRow().getItem();
                if (convertItem != null) {
                    convertItem.setDestType(value);
                    updateItem(value, false);
                    checkConvertItems();
                    setParams(convertItem);
                }
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                updateItem(this.getItem(), false);
            }

            @Override
            protected void updateItem(DataType item, boolean empty) {
                super.updateItem(item, empty);
                String text = null;
                ImageView imageView = null;
                if (!empty && item != null) {
                    text = item.getText();
                    Image image = item.getImage();
                    if (image != null) {
                        imageView = new ImageView(image);
                    }
                }
                setText(text);
                setGraphic(imageView);
                tableView.requestFocus();
            }
        });
        tcDestName.setCellFactory(param -> new TableCell<ConvertItem, String>() {
            private TextField textField = new TextField();

            @Override
            public void startEdit() {
                super.startEdit();
                textField.setText(getItem());
                setGraphic(textField);
                textField.requestFocus();

                textField.textProperty().addListener((o, ov, nv) ->
                {
                    StringProperty errorMsg = new SimpleStringProperty();
                    if (!XString.isTextValid(nv, 20, GISDefaultValues.getInvalidNameCharList(), errorMsg)) {
                        UIFunctions.showErrorTip(textField, errorMsg.get(), tooltipError);
                        textField.setText(ov);
                    }
                });
                //按Enter键完成修改。
                textField.setOnKeyPressed(event ->
                {
                    if (KeyCode.ENTER.equals(event.getCode())) {
                        tableView.requestFocus();
                    }
                });
                textField.focusedProperty().addListener((o, ov, nv) ->
                {
                    if (!nv) {
                        commitEdit(textField.getText());
                    }
                });
            }

            @Override
            public void commitEdit(String value) {
                super.commitEdit(value);
                ConvertItem convertItem = (ConvertItem) getTableRow().getItem();
                if (convertItem != null) {
                    convertItem.setDestName(value);
                }
                updateItem(value, XString.isNullOrEmpty(value));
                checkConvertItems();
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                Label label = null;
                if (!empty) {
                    label = new Label(item);
                }
                setGraphic(label);
            }
        });
        tcDestPath.setCellFactory(param -> new TableCell<ConvertItem, String>() {
            private ButtonEdit buttonEdit = new ButtonEdit();

            @Override
            public void startEdit() {
                super.startEdit();

                this.buttonEdit.setTextEditable(true);//暂时放开编辑
                buttonEdit.setText(getItem());
                setGraphic(buttonEdit);
                buttonEdit.setOnButtonClick(event ->
                {
                    ConvertItem convertItem = (ConvertItem) getTableRow().getItem();
                    if (convertItem != null) {
                        String destDir = "";
                        DataType destType = convertItem.getDestType();
                        switch (destType) {
                            case MAPGIS_SFCLS:
                            case MAPGIS_SFCLSP:
                            case MAPGIS_SFCLSL:
                            case MAPGIS_SFCLSR:
                            case MAPGIS_SFCLSS:
                            case MAPGIS_SFCLSE:
                            case MAPGIS_ACLS:
                            case MAPGIS_OCLS: {
                                GDBSelectFolderDialog dlg = new GDBSelectFolderDialog();
                                dlg.setFolderType(FolderType.MapGIS_DataBase | FolderType.MapGIS_Fds);
                                Optional<String[]> optional = dlg.showAndWait();
                                if (optional != null && optional.isPresent()) {
                                    String[] files = optional.get();
                                    if (files != null && files.length > 0) {
                                        destDir = files[0];
                                    }
                                }
                                break;
                            }
                            case MAPGIS_FDS:
                            case MAPGIS_RAS:
                            case MAPGIS_RCAT:
                            case MAPGIS_GDB: {
                                GDBSelectFolderDialog dlg = new GDBSelectFolderDialog();
                                dlg.setFolderType(FolderType.MapGIS_DataBase);
                                Optional<String[]> optional = dlg.showAndWait();
                                if (optional != null && optional.isPresent()) {
                                    String[] files = optional.get();
                                    if (files != null && files.length > 0) {
                                        destDir = files[0];
                                    }
                                }
                                break;
                            }
                            case TABLE_EXCEL:
                            case TABLE_ACCESS: {
                                //SelectTableFolderDialog of = new SelectTableFolderDialog();
                                //of.Filter = (destType == DataType.TABLE_EXCEL) ? "Excel表格(*.xls;*.xlsx)|*.xls;*.xlsx" : "Access表格(*.mdb;*.accdb)|*.mdb;*.accdb";
                                //if (of.ShowDialog() == DialogResult.OK)
                                //{
                                //    destDir = CustomOperate.tableProName + of.SelectedPath;
                                //}
                                //TODO: 既可以选择文件也可以选择目录
                                break;
                            }
                            default: {
                                DirectoryChooser dirChooser = new DirectoryChooser();
                                File file = dirChooser.showDialog(getCurrentWindow());
                                if (file != null) {
                                    destDir = file.getPath();
                                }
                                break;
                            }
                        }

                        if (!XString.isNullOrEmpty(destDir)) {
                            buttonEdit.setText(destDir);
                            commitEdit(destDir);
                        } else {
                            cancelEdit();
                        }
                    }
                });
                buttonEdit.setOnTextKeyPressed(event ->
                {
                    if (KeyCode.ENTER.equals(event.getCode())) {
                        commitEdit(buttonEdit.getText());
                    }
                });
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                updateItem(getItem(), XString.isNullOrEmpty(getItem()));
            }

            @Override
            public void commitEdit(String value) {
                super.commitEdit(value);
                ConvertItem convertItem = (ConvertItem) getTableRow().getItem();
                if (convertItem != null) {
                    convertItem.setDestPath(value);
                }
                updateItem(value, XString.isNullOrEmpty(value));
                checkConvertItems();
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                Label label = null;
                if (!empty) {
                    label = new Label(item);
                }
                setGraphic(label);
                tableView.requestFocus();
            }
        });
        tcProgress.setCellFactory(param -> new TableCell<ConvertItem, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                ProgressBar progressBar = null;
                if (!empty) {
                    progressBar = new ProgressBar(item);
                    progressBar.setStyle("-fx-accent: limegreen");
                    progressBar.prefWidthProperty().bind(tcProgress.widthProperty());
                }
                setGraphic(progressBar);
            }
        });
        //endregion
        //endregion

        //region 参数设置
        this.titledPaneParams.setCollapsible(false);
        this.titledPaneSource.setCollapsible(false);
        this.vBoxParams.getChildren().addAll(this.titledPaneParams, this.titledPaneSource);
        //endregion

        //region 转换关闭按钮
        Region region = new Region();
        Button buttonConvert = new Button("开始转换");
        buttonConvert.setPrefSize(80, 30);
        Button buttonClose = new Button("关闭");
        buttonClose.setPrefSize(80, 30);
        HBox hBoxButton = new HBox(6, this.checkClose, region, buttonConvert, buttonClose);
        HBox.setHgrow(region, Priority.ALWAYS);

        buttonConvert.setOnAction(this::convertButtonClick);
        buttonClose.setOnAction(this::closeButtonClick);
        //endregion

        //region 界面布局
        VBox vBoxLeft = new VBox(toolBar, this.tableView, hBoxButton);
        vBoxLeft.setMargin(hBoxButton, new Insets(12, 0, 0, 0));
        VBox.setVgrow(this.tableView, Priority.ALWAYS);

        SplitPane root = new SplitPane(vBoxLeft, vBoxParams);
        root.setBackground(new Background(new BackgroundFill(Paint.valueOf("transparent"), null, null)));
        root.setDividerPosition(0, 0.75);

        DialogPane dialogPane = this.getDialogPane();
        dialogPane.setContent(root);
        dialogPane.setPrefSize(1200, 600);

        //添加不可见的按钮，使对话框上的×可用
        dialogPane.getButtonTypes().addAll(ButtonType.CLOSE);
        Button button = (Button) dialogPane.lookupButton(ButtonType.CLOSE);
        button.setManaged(false);
        button.setVisible(false);
        dialogPane.setPadding(new Insets(0, 0, -20, 0));
        //endregion

        //region 初始化
        CustomOperate.initRasterDrive();
        //endregion

        logFilePath = XPath.combine(XPath.getTemp(), "DataConvert.log");
    }
    //endregion

    //region 转换和关闭事件
    private void convertButtonClick(ActionEvent event) {
        checkConvertItems();

        writeLog(String.format("---------------------开始转换：%s---------------------", XFunctions.getDateTimeString()));
        writeLogEmptyLine();

        this.tableView.requestFocus();
        this.isConverting.set(true);
        this.hasConvertFailed = false;
        this.hasConvertSuccess = false;
        for (ConvertItem item : this.tableView.getItems()) {
            item.setProgress(0);
            //增加三维数据支持 0414 ysp
            DataType srcDataType = item.getSourType();
            DataType destDataType = item.getDestType();
            boolean rtn = false;
            if (srcDataType == DataType.Model_3DS ||
                    srcDataType == DataType.Model_OBJ ||
                    srcDataType == DataType.Model_DAE ||
                    srcDataType == DataType.Model_OSGB ||
                    srcDataType == DataType.Model_FBX ||
                    srcDataType == DataType.Model_XML ||
                    srcDataType == DataType.Model_X ||
                    srcDataType == DataType.Model_LAS) {
                String desUrl = String.format("%s/sfcls/%s",item.getDestPath(),item.getDestName());
                writeLog("开始转换");
                if (srcDataType == DataType.Model_LAS) {
                    //增加三维数据支持 0414 ysp
                    GeoDataConv3D dataConvert3D = new GeoDataConv3D();
                    rtn = convertPointCloudFileToModel(dataConvert3D, item.getSourPath(),desUrl , item.getConvertOption());
                    dataConvert3D.dispose();
                } else {
                    //增加三维数据支持 0414 ysp
                    GeoDataConv3D dataConvert3D = new GeoDataConv3D();
                    rtn = convert3DFileToModel(dataConvert3D, item.getSourPath(), desUrl, item.getConvertOption());
                    dataConvert3D.dispose();

                }
                if (rtn) {
                    writeLog(String.format("结束转换第%d项。成功。", tableView.getItems().indexOf(item) + 1, ""));
                    this.hasConvertFailed = true;
                    item.setState(ConvertState.SUCCEED);
                } else {
                    writeLog(String.format("结束转换第%d项。失败。", tableView.getItems().indexOf(item) + 1, ""));
                    this.hasConvertFailed = false;
                    item.setState(ConvertState.FAILED);
                }
            } else {
                ConvertTask task = new ConvertTask(item);
                executorService.submit(task);

                task.progressProperty().addListener((o, ov, nv) ->
                {
                    item.setProgress(nv.doubleValue());
                });
                task.stateProperty().addListener((o, ov, nv) ->
                {
                    if (nv == Worker.State.FAILED) {
                        ConvertItem curItem = task.getConvertItem();
                        if (curItem != null) {
                            curItem.setState(ConvertState.FAILED);
                            writeLog(String.format("结束转换第%d项。失败。异常消息：%s", tableView.getItems().indexOf(curItem) + 1, task.getException().getMessage()));
                            this.hasConvertFailed = true;
                        }
                    }
                });
                task.runningProperty().addListener((o, ov, nv) ->
                {
                    if (nv) {
                        ConvertItem curItem = task.getConvertItem();
                        if (curItem != null) {
                            curItem.setState(ConvertState.CONVERTING);
                            this.tableView.getSelectionModel().clearSelection();
                            this.tableView.getSelectionModel().select(curItem);
                        }
                    } else {
                        if (clickedClose) {
                            executorService.shutdownNow();
                            DataConverts.close();
                            //((Stage) this.getCurrentWindow()).close();
                        }
                        if (tableView.getItems().indexOf(task.getConvertItem()) == tableView.getItems().size() - 1) {
                            writeLog(String.format("---------------------结束转换：%s---------------------", XFunctions.getDateTimeString()));
                            writeLogEmptyLine();
                            writeLogEmptyLine();
                            isConverting.set(false);
                            if (this.checkClose.isSelected()) {
                                if (!this.hasConvertFailed) {
                                    try {
                                        Thread.sleep(300);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    DataConverts.close();
                                }
                            }
                        }
                    }
                });
                task.valueProperty().addListener((o, ov, nv) ->
                {
                    if (nv) {
                        item.setErrorType(ErrorType.NOERROR);
                        hasConvertSuccess = true;
                    } else {
                        this.hasConvertFailed = true;
                        item.setErrorType(ErrorType.CONVERTERROR);
                    }
                });
            }
        }
    }

    private void closeButtonClick(ActionEvent event) {
        if (this.isConverting.get()) {
            this.clickedClose = true;
        } else {
            DataConverts.close();
            //((Stage) this.getCurrentWindow()).close();
        }
    }
    //endregion

    //region 公有方法

    /**
     * 是否有转换成功的项
     *
     * @return
     */
    public boolean hasConvertSuccess() {
        return hasConvertSuccess;
    }

    public void addConvertItem(String url) {
        if (!XString.isNullOrEmpty(url)) {
            this.addConvertItems(Arrays.asList(url));
        }
    }

    public void addConvertItems(List<String> urlList) {
        this.addConvertItems(urlList, DataConverts.getDestPath());
    }

    public void addConvertItems(List<String> urlList, String destDir) {
        if (urlList != null && urlList.size() > 0) {
            List<String> existFiles = new ArrayList<String>();
            for (ConvertItem item : this.tableView.getItems()) {
                existFiles.add(item.getSourPath().toLowerCase());
            }

            boolean isAdded = false;
            String exFiles = "";
            for (String url : urlList) {
                boolean hasInTable = existFiles.contains(url.toLowerCase());
                if (hasInTable) {
                    exFiles += "\n" + url;
                } else {
                    this.tableView.getItems().add(new ConvertItem(url, destDir));
                    isAdded = true;
                }
            }

            if (isAdded) {
                this.tableView.scrollTo(this.tableView.getItems().size() - 1);
                this.tableView.getSelectionModel().clearSelection();
                this.tableView.getSelectionModel().select(this.tableView.getItems().size() - 1);
            }

            if (exFiles != "") {
                Notification.showInformation(getCurrentWindow(), "添加转换数据", "下列文件已存在于列表中:" + exFiles);
            }
        }
    }
    //endregion

    //region 私有方法

    /**
     * 添加源文件（其中mpj文件需要解析）
     *
     * @param fileNames
     */
    private void addSourFiles(String... fileNames) {
        List<String> newFileNames = new ArrayList<>();
        if (fileNames != null) {
            for (String str : fileNames) {
                if (".mpj".equals(XPath.getExtension(str))) {
                    //String[] strFiles = MapGIS.GeoDataBase.Convert.DataConvertCommonFun.Parse6xPrj(str);//未封装
                    //if (strFiles != null)
                    //{
                    //    for (String s : strFiles)
                    //    {   if (!XString.isNullOrEmpty(s) && (new File(s)).exists())
                    //        {
                    //            newFileNames.add(s);
                    //        }
                    //    }
                    //}
                } else {
                    newFileNames.add(str);
                }
            }
        }
        fileNames = newFileNames.toArray(new String[0]);
        for (String srcDataUrl : fileNames) {
            ConvertItem item = new ConvertItem(srcDataUrl);
            this.tableView.getItems().add(item);
        }
    }

    /**
     * 显示当前行的错误消息
     *
     * @param item 当前行的转换项
     */
    private void setErrorMessage(ConvertItem item) {
        this.labelMessage.setText("");
        if (item != null) {
            ErrorType errorType = item.getErrorType();
            this.labelMessage.setText(errorType.getValue());
        }
    }

    /**
     * 在日志中输入空行
     */
    private void writeLogEmptyLine() {
        writeLog("");
    }

    /**
     * 写日志（时间部分仅写时间不写日志）
     *
     * @param logText 日志内容
     */
    private void writeLog(String logText) {
        try {
            File logFile = new File(logFilePath);
            // 如果文本文件不存在则创建它
            if (!logFile.exists()) {
                if (!logFile.getParentFile().exists()) {
                    logFile.mkdirs();
                }
                logFile.createNewFile();
                logFile = new File(logFilePath); //重新实例化
            }
            FileOutputStream logStream = new FileOutputStream(logFile, true);
            Writer logWriter = new OutputStreamWriter(logStream, "utf-8");
            if (!XString.isNullOrEmpty(logText)) {
                logWriter.write(logText);
            }
            logWriter.write(System.getProperty("line.separator"));
            logWriter.close();
            logStream.flush();
            logStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //建立金字塔(对栅格转换结果创建金字塔)
    private void buildPyramid(String url) {
        //if (ConvertOption.isBuildPyramid())
        //{
        //    RasterDataset rds = new RasterDataset();
        //    if (rds.openByURL(url, RasterAccess.Write)>0)
        //    {
        //        int pyNum = rds.getPyramidNum();
        //        if ((rtn && pyNum == 1) || !rtn)
        //        {
        //            writeLog("开始创建金字塔", null);
        //            rtn = rds.buildPyramidLayer(ConvertOption.getPYType(), ConvertOption.getLines(), ConvertOption.getLineCells());
        //            writeLog("结束创建金字塔", rtn);
        //        }
        //        rds.close();
        //    }
        //}
    }

    //region 获取Window
    private javafx.stage.Window window;

    /**
     * 获取当前窗口的window对象
     *
     * @return 当前窗口的window对象
     */
    private javafx.stage.Window getCurrentWindow() {
        if (this.window == null) {
            this.window = this.getDialogPane().getScene().getWindow();
        }
        return this.window;
    }
    //endregion

    //endregion

    //region 检查目的数据错误功能

    /**
     * 检查界面错误
     */
    private void checkConvertItems() {
        Character[] gdbInvalidChars = {'\\', ':', '*', '?', '"', '<', '>', '|'};
        char gdbInvalidChar = '/';
        Character[] arcGISLocalInvalidChars = {'.'};

        HashMap<String, DataBase> dbList = new HashMap<>();
        List<ConvertItemInfo> itemList = new ArrayList<>();
        for (ConvertItem convertItem : tableView.getItems()) {
            ConvertOption option = convertItem.getConvertOption();
            boolean isError = false;
            DataType destType = convertItem.getDestType();
            switch (destType) {
                case UNKNOWN:
                    convertItem.setErrorType(ErrorType.DESTTYPEERROR);
                    isError = true;
                    break;
                case RASTER_6XDEM:
                case RASTER_BMP:
                case RASTER_GIF:
                case RASTER_HDF5:
                case RASTER_IMG:
                case RASTER_JP2:
                case RASTER_JPG:
                case RASTER_MSI:
                case RASTER_PNG:
                case RASTER_TIFF: {
                    if (option == null || XString.isNullOrEmpty(option.getDestDrive())) {
                        convertItem.setErrorType(ErrorType.DESTTYPEERROR);
                        isError = true;
                    }
                    break;
                }
            }
            if (isError) {
                continue;
            }

            String destName = convertItem.getDestName();
            if (XString.isNullOrEmpty(destName)) {
                convertItem.setErrorType(ErrorType.DESTNAMENULL);
                continue;
            }

            String destDir = convertItem.getDestPath();
            if (XString.isNullOrEmpty(destDir)) {
                convertItem.setErrorType(ErrorType.DESTDIRNULL);
                continue;
            }

            DataType srcType = convertItem.getSourType();
            switch (destType) {
                case MAPGIS_SFCLSP:
                case MAPGIS_SFCLSL:
                case MAPGIS_SFCLSR:
                case MAPGIS_SFCLSS:
                case MAPGIS_SFCLSE:
                case MAPGIS_SFCLS:
                case MAPGIS_ACLS:
                case MAPGIS_OCLS:
                case MAPGIS_FDS:
                case MAPGIS_RAS:
                case MAPGIS_RCAT:
                case MAPGIS_GDB: {
                    if (XString.indexOfAny(destName, gdbInvalidChars) >= 0 || destName.indexOf(gdbInvalidChar) >= 0) {
                        convertItem.setErrorType(ErrorType.DESTNAMECHARINVALID);
                        continue;
                    }

                    if (XString.getStringByteLength(destName) >= 128) {
                        convertItem.setErrorType(ErrorType.DESTNAMETOOLONG);
                        continue;
                    }

                    if (XString.indexOfAny(destDir.toLowerCase().replace(CustomOperate.gdbProName, ""), gdbInvalidChars) >= 0) {
                        convertItem.setErrorType(ErrorType.DESTDIRCHARINVALID);
                        continue;
                    }

                    if (destType == DataType.MAPGIS_GDB) {
                        if (!checkMapGISServerUrl(destDir)) {
                            convertItem.setErrorType(ErrorType.DESTDIRINVALID);
                            continue;
                        }

                        String dbUrl = convertItem.calcDestURL();
                        if (XString.isNullOrEmpty(dbUrl)) {
                            convertItem.setErrorType(ErrorType.DESTDIRNOTEXIST);
                            continue;
                        }

                        if (!dbList.containsKey(dbUrl.toLowerCase())) {
                            DataBase db = DataBase.openByURL(dbUrl);
                            if (db != null) {
                                dbList.put(dbUrl.toLowerCase(), db);
                            }
                        }
                        if (!dbList.containsKey(dbUrl.toLowerCase())) {
                            convertItem.setErrorType(ErrorType.DESTGDBNOTEXIST);
                            continue;
                        }
                    } else {
                        if (!checkMapGISDestDir(destDir, destType)) {
                            convertItem.setErrorType(ErrorType.DESTDIRINVALID);
                            continue;
                        }

                        String dbUrl = URLParse.getDataBase(convertItem.getDestPath());
                        if (XString.isNullOrEmpty(dbUrl)) {
                            convertItem.setErrorType(ErrorType.DESTDIRNOTEXIST);
                            continue;
                        }
                        if (!dbList.containsKey(dbUrl.toLowerCase())) {
                            DataBase db = DataBase.openByURL(dbUrl);
                            if (db != null) {
                                dbList.put(dbUrl.toLowerCase(), db);
                            }
                        }
                        if (!dbList.containsKey(dbUrl.toLowerCase())) {
                            convertItem.setErrorType(ErrorType.DESTDIRNOTEXIST);
                            continue;
                        }

                        //如果目的数据库位ArcGISLocal中间件，则目的数据名也不能包含'.'
                        if (CustomOperate.isArcGISLocal(URLParse.getServerName(dbUrl))) {
                            if (XString.indexOfAny(destName, arcGISLocalInvalidChars) >= 0) {
                                convertItem.setErrorType(ErrorType.DESTNAMECHARINVALID);
                                continue;
                            }
                        }

                        DataBase db = dbList.get(dbUrl.toLowerCase());
                        XClsType clsType = URLParse.stringToXClsType(destType.getExts().get(0));
                        List<ConvertItemInfo> list = new ArrayList<>();
                        switch (srcType) {
                            case VECTOR_MIF:
                            case VECTOR_E00:
                            case VECTOR_VCT:
                            case VECTOR_GML:
                            case VECTOR_DGN:
                            case VECTOR_KML: {
                                int id1 = (int) db.xClsIsExist(clsType, destName + (destName.endsWith("_pnt") ? "" : "_pnt"));
                                int id2 = (int) db.xClsIsExist(clsType, destName + (destName.endsWith("_lin") ? "" : "_lin"));
                                int id3 = (int) db.xClsIsExist(clsType, destName + (destName.endsWith("_reg") ? "" : "_reg"));
                                int id4 = (int) db.xClsIsExist(XClsType.XACls, destName + (destName.endsWith("_ann") ? "" : "_ann"));
                                if (id1 > 0 || id2 > 0 || id3 > 0 || id4 > 0) {
                                    if (isAppendMode(option, destType)) {
                                        if (id1 > 0) {
                                            SFClsInfo sfclsInfo = (SFClsInfo) db.getXclsInfo(XClsType.XSFCls, id1);
                                            if (sfclsInfo != null && sfclsInfo.getfType() != GeomType.GeomPnt) {
                                                //追加模式下，已存在的目的数据的几何类型与源数据不一致
                                                convertItem.setErrorType(ErrorType.DESTDATAGEOMTYPNOTEQUAL);
                                                continue;
                                            }
                                        }
                                        if (id2 > 0) {
                                            SFClsInfo sfclsInfo = (SFClsInfo) db.getXclsInfo(XClsType.XSFCls, id2);
                                            if (sfclsInfo != null && sfclsInfo.getfType() != GeomType.GeomLin) {
                                                //追加模式下，已存在的目的数据的几何类型与源数据不一致
                                                convertItem.setErrorType(ErrorType.DESTDATAGEOMTYPNOTEQUAL);
                                                continue;
                                            }
                                        }
                                        if (id3 > 0) {
                                            SFClsInfo sfclsInfo = (SFClsInfo) db.getXclsInfo(XClsType.XSFCls, id3);
                                            if (sfclsInfo != null && sfclsInfo.getfType() != GeomType.GeomReg) {
                                                //追加模式下，已存在的目的数据的几何类型与源数据不一致
                                                convertItem.setErrorType(ErrorType.DESTDATAGEOMTYPNOTEQUAL);
                                                continue;
                                            }
                                        }
                                    } else {
                                        convertItem.setErrorType(ErrorType.DESTDATAEXIST);
                                        continue;
                                    }
                                }

                                list.add(new ConvertItemInfo(DataType.MAPGIS_SFCLS, destName + (destName.endsWith("_pnt") ? "" : "_pnt"), destDir, GeomType.GeomPnt));
                                list.add(new ConvertItemInfo(DataType.MAPGIS_SFCLS, destName + (destName.endsWith("_lin") ? "" : "_lin"), destDir, GeomType.GeomLin));
                                list.add(new ConvertItemInfo(DataType.MAPGIS_SFCLS, destName + (destName.endsWith("_reg") ? "" : "_reg"), destDir, GeomType.GeomReg));
                                list.add(new ConvertItemInfo(DataType.MAPGIS_ACLS, destName + (destName.endsWith("_ann") ? "" : "_ann"), destDir, GeomType.GeomAnn));
                                break;
                            }
                            default: {
                                int id = (int) db.xClsIsExist(clsType, destName);
                                if (id > 0) {
                                    if (destType == DataType.MAPGIS_FDS && (srcType == DataType.VECTOR_DWG || srcType == DataType.VECTOR_DXF || srcType == DataType.VECTOR_JSON)) {
                                        //此时可以存在同名的要素数据集
                                    } else {
                                        if (isAppendMode(option, destType)) {
                                            if (destType == DataType.MAPGIS_SFCLS) {
                                                SFClsInfo sfclsInfo = (SFClsInfo) db.getXclsInfo(XClsType.XSFCls, id);
                                                if (sfclsInfo != null) {
                                                    switch (srcType) {
                                                        case MAPGIS_SFCLS:
                                                        case MAPGIS_SFCLSP:
                                                        case MAPGIS_SFCLSL:
                                                        case MAPGIS_SFCLSR:
                                                        case MAPGIS_SFCLSS:
                                                        case MAPGIS_SFCLSE: {
                                                            if (!sfclsInfo.getfType().equals(convertItem.getGeoType())) {
                                                                //追加模式下，已存在的目的数据的几何类型与源数据不一致
                                                                convertItem.setErrorType(ErrorType.DESTDATAGEOMTYPNOTEQUAL);
                                                                continue;
                                                            }
                                                            break;
                                                        }
                                                        case MAPGIS_6X_FILE: {
                                                            if (sfclsInfo.getfType() == GeomType.GeomPnt && convertItem.getSourType() != DataType.MAPGIS_6X_WT) {
                                                                //追加模式下，已存在的目的数据的几何类型与源数据不一致
                                                                convertItem.setErrorType(ErrorType.DESTDATAGEOMTYPNOTEQUAL);
                                                                continue;
                                                            } else if (sfclsInfo.getfType() == GeomType.GeomLin && convertItem.getSourType() != DataType.MAPGIS_6X_WL) {
                                                                //追加模式下，已存在的目的数据的几何类型与源数据不一致
                                                                convertItem.setErrorType(ErrorType.DESTDATAGEOMTYPNOTEQUAL);
                                                                continue;
                                                            } else if (sfclsInfo.getfType() == GeomType.GeomReg && convertItem.getSourType() != DataType.MAPGIS_6X_WP) {
                                                                //追加模式下，已存在的目的数据的几何类型与源数据不一致
                                                                convertItem.setErrorType(ErrorType.DESTDATAGEOMTYPNOTEQUAL);
                                                                continue;
                                                            }
                                                            break;
                                                        }
                                                        case TXT: {
                                                            boolean genLine = false;//生成线标记
                                                            //未封装(Txt27xParam)
                                                            //if (option.getTxt27xParam() != null)
                                                            //{
                                                            //    genLine = (option.getTxt27xParam().genLineStatus == 1);
                                                            //}
                                                            if (genLine && sfclsInfo.getfType() != GeomType.GeomLin) {
                                                                //追加模式下，已存在的目的数据的几何类型与源数据不一致
                                                                convertItem.setErrorType(ErrorType.DESTDATAGEOMTYPNOTEQUAL);
                                                                continue;
                                                            }
                                                            if (!genLine && sfclsInfo.getfType() != GeomType.GeomPnt) {
                                                                //追加模式下，已存在的目的数据的几何类型与源数据不一致
                                                                convertItem.setErrorType(ErrorType.DESTDATAGEOMTYPNOTEQUAL);
                                                                continue;
                                                            }
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            convertItem.setErrorType(ErrorType.DESTDATAEXIST);
                                            continue;
                                        }
                                    }
                                }

                                GeomType destGeomType = GeomType.GeomUnknown;
                                switch (srcType) {
                                    case MAPGIS_SFCLS:
                                    case MAPGIS_SFCLSP:
                                    case MAPGIS_SFCLSL:
                                    case MAPGIS_SFCLSR:
                                    case MAPGIS_SFCLSS:
                                    case MAPGIS_SFCLSE: {
                                        destGeomType = convertItem.getGeoType();
                                        break;
                                    }
                                    case MAPGIS_6X_WT: {
                                        destGeomType = GeomType.GeomPnt;
                                        break;
                                    }
                                    case MAPGIS_6X_WL: {
                                        destGeomType = GeomType.GeomLin;
                                        break;
                                    }
                                    case MAPGIS_6X_WP: {
                                        destGeomType = GeomType.GeomReg;
                                        break;
                                    }
                                    case TXT: {
                                        boolean genLine = false;//生成线标记
                                        //未封装(Txt27xParam)
                                        //if (option.getTxt27xParam() != null)
                                        //{
                                        //    genLine = (option.getTxt27xParam().genLineStatus == 1);
                                        //}
                                        //else
                                        //{
                                        //    convertItem.setErrorType(ItemErrorType.NOSETTXT27XPARAM);
                                        //    continue;
                                        //}

                                        destGeomType = genLine ? GeomType.GeomLin : GeomType.GeomPnt;
                                        break;
                                    }
                                }

                                ConvertItemInfo itemInfo = new ConvertItemInfo(destType, destName, destDir, destGeomType);
                                list.add(itemInfo);
                                break;
                            }
                        }

                        boolean tempBreak = false;
                        for (ConvertItemInfo curItemInfo : list) {
                            ConvertItemInfo sameInfo = checkDestDataHasNamed(itemList, curItemInfo);
                            if (sameInfo != null) {
                                if (isAppendMode(option, destType)) {
                                    if (sameInfo != null && sameInfo.getDestType() == DataType.MAPGIS_SFCLS && !sameInfo.getGeoType().equals(curItemInfo.getGeoType())) {
                                        tempBreak = true;
                                        break;
                                    }
                                } else {
                                    tempBreak = true;
                                    break;
                                }
                            } else {
                                itemList.add(curItemInfo);
                            }
                        }
                        if (tempBreak) {
                            convertItem.setErrorType(ErrorType.DESTDATAHASNAMED);
                            continue;
                        }
                    }
                    break;
                }
                default: {
                    destDir = destDir.replace(CustomOperate.fileProName, "").replace(CustomOperate.demProName, "").replace(CustomOperate.tableProName, "");
                    if (!XPath.isPathValid(destDir)) {
                        convertItem.setErrorType(ErrorType.DESTDIRCHARINVALID);
                        continue;
                    }

                    File file = new File(destDir);
                    if (!file.exists()) {
                        convertItem.setErrorType(ErrorType.DESTDIRNOTEXIST);
                        continue;
                    }

                    if (destType == DataType.TABLE_EXCEL) {
                        int thisIndex = destName.indexOf('/');

                        if (thisIndex > 0 && thisIndex < destName.length() - 1) {
                            String newFileName = destName.substring(0, thisIndex);
                            String newTableName = destName.substring(thisIndex + 1);
                            if (XString.isNullOrEmpty(newFileName) || XString.isNullOrEmpty(newTableName)) {
                                convertItem.setErrorType(ErrorType.DESTNAMENULL);
                                continue;
                            }
                            if (XPath.isNameValid(newFileName)) {
                                convertItem.setErrorType(ErrorType.DESTNAMECHARINVALID);
                                continue;
                            }
                            ErrorType tempErrorType = checkExcelTableName(newTableName);
                            if (tempErrorType != ErrorType.UNCHECK) {
                                convertItem.setErrorType(tempErrorType);
                                continue;
                            }
                        } else {
                            if (XPath.isNameValid(destName)) {
                                convertItem.setErrorType(ErrorType.DESTNAMECHARINVALID);
                                continue;
                            }

                            ErrorType tempErrorType = checkExcelTableName(destName);
                            if (tempErrorType != ErrorType.UNCHECK) {
                                convertItem.setErrorType(tempErrorType);
                                continue;
                            }
                        }
                    } else if (destType == DataType.TABLE_ACCESS) {
                        if (XPath.isNameValid(destName)) {
                            convertItem.setErrorType(ErrorType.DESTNAMECHARINVALID);
                            continue;
                        }

                        ErrorType tempErrorType = checkAccessTableName(destName);
                        if (tempErrorType != ErrorType.UNCHECK) {
                            convertItem.setErrorType(tempErrorType);
                            continue;
                        }
                    } else if (destType == DataType.TABLE_DBF) {
                        if (XPath.isNameValid(destName)) {
                            convertItem.setErrorType(ErrorType.DESTNAMECHARINVALID);
                            continue;
                        }
                        ErrorType tempErrorType = checkFoxproTableName(destName);
                        if (tempErrorType != ErrorType.UNCHECK) {
                            convertItem.setErrorType(tempErrorType);
                            continue;
                        }
                    } else {
                        if (!XPath.isNameValid(destName)) {
                            convertItem.setErrorType(ErrorType.DESTNAMECHARINVALID);
                            continue;
                        }
                    }

                    try {
                        String destUrl = convertItem.calcDestURL();
                        destUrl = destUrl.replace(CustomOperate.fileProName, "").replace(CustomOperate.demProName, "").replace(CustomOperate.tableProName, "");
                        File file1 = new File(destDir);
                        if (destUrl.toLowerCase().endsWith(".db") ? file1.isDirectory() : file1.isFile()) {
                            if (!isAppendMode(option, destType)) {
                                convertItem.setErrorType(ErrorType.DESTDATAEXIST);
                                continue;
                            }
                        }
                    } catch (Exception ex) {
                        convertItem.setErrorType(ErrorType.UNKNOWN);
                        continue;
                    }

                    ConvertItemInfo itemInfo = new ConvertItemInfo(destType, destName, destDir);
                    ConvertItemInfo outInfo = checkDestDataHasNamed(itemList, itemInfo);
                    if (outInfo != null) {
                        if (!isAppendMode(option, destType)) {
                            convertItem.setErrorType(ErrorType.DESTDATAHASNAMED);
                            continue;
                        }
                    } else {
                        itemList.add(itemInfo);
                    }
                    break;
                }
            }
            convertItem.setErrorType(ErrorType.UNCHECK);
        }

        //region 关闭数据库
        for (DataBase db : dbList.values()) {
            db.close();
        }
        dbList.clear();
        //endregion

        ConvertItem item = tableView.getSelectionModel().getSelectedItem();
        if (item != null) {
            setErrorMessage(item);
        }
    }

    /**
     * 检查数据源url是否合法(只有这样才合法：gdbp://MapGisLocal)
     *
     * @param destDir
     * @return
     */
    private static boolean checkMapGISServerUrl(String destDir) {
        boolean rtn = false;
        if (!XString.isNullOrEmpty(destDir) && destDir.toLowerCase().startsWith(CustomOperate.gdbProName)) {
            destDir = destDir.substring(CustomOperate.gdbProName.length());
            rtn = !XString.isNullOrEmpty(destDir) && !destDir.contains("/");
        }
        return rtn;
    }

    /**
     * 检查GDB数据目录是否合法(只有类似这些才合法：gdbp://MapGisLocal/中国地图、gdbp://MapGisLocal/平台基础示例数据/ds/武汉市区、gdbp://MapGisLocal/平台基础示例数据/rcat/栅格目录)
     *
     * @param destDir
     * @param dataType
     * @return
     */
    private static boolean checkMapGISDestDir(String destDir, DataType dataType) {
        boolean rtn = false;
        if (!XString.isNullOrEmpty(destDir) && destDir.toLowerCase().startsWith(CustomOperate.gdbProName)) {
            destDir = destDir.substring(CustomOperate.gdbProName.length());
            if (destDir.length() >= 3)//服务名 + / + 数据库名 = 3
            {
                String[] strs = destDir.split("/");
                if (strs.length == 2) {
                    rtn = !XString.isNullOrEmpty(strs[0]) && !XString.isNullOrEmpty(strs[1]);
                } else if (strs.length == 4) {
                    rtn = !XString.isNullOrEmpty(strs[0]) && !XString.isNullOrEmpty(strs[1]) && (strs[2].equalsIgnoreCase("ds") || strs[2].equalsIgnoreCase("rcat")) && !XString.isNullOrEmpty(strs[3]);
                }
            }
        }
        return rtn;
    }

    private static ConvertItemInfo checkDestDataHasNamed(List<ConvertItemInfo> items, ConvertItemInfo curItem) {
        ConvertItemInfo namedItem = null;
        if (items != null && curItem != null) {
            for (ConvertItemInfo item : items) {
                if (item.getDestType() == curItem.getDestType() && item.getDestName().equalsIgnoreCase(curItem.getDestName()) && item.getDestDir().equalsIgnoreCase(curItem.getDestDir())) {
                    namedItem = item;
                    break;
                }
            }
        }
        return namedItem;
    }

    private static ErrorType checkExcelTableName(String tableName) {
        ErrorType errorType = ErrorType.UNCHECK;
        if (!XString.isNullOrEmpty(tableName)) {
            if (tableName.toLowerCase().endsWith(".xls")) {
                tableName = tableName.substring(0, tableName.length() - 4);
            } else if (tableName.toLowerCase().endsWith(".xlsx")) {
                tableName = tableName.substring(0, tableName.length() - 5);
            }

            if (tableName.length() > 255) {
                //表名过长
                errorType = ErrorType.DESTNAMETOOLONG;
            }
            char firstChar = tableName.charAt(0);
            if (!(CustomOperate.isLetterChar(firstChar) || CustomOperate.isNumberChar(firstChar) || CustomOperate.isUnderlineChar(firstChar) || CustomOperate.isChineseChar(firstChar))) {
                //首字符非法
                errorType = ErrorType.DESTNAMEFIRSTCHARIINVALID;
            }
            int strLength = tableName.length();
            for (int i = 1; i < strLength; i++) {
                char otherChar = tableName.charAt(i);
                if (!(CustomOperate.isLetterChar(otherChar) || CustomOperate.isNumberChar(otherChar) || CustomOperate.isUnderlineChar(otherChar) || CustomOperate.isChineseChar(otherChar) || otherChar == ' ')) {
                    //其他字符非法
                    errorType = ErrorType.DESTNAMECHARINVALID;
                }
            }
        }
        return errorType;
    }

    private static ErrorType checkAccessTableName(String tableName) {
        ErrorType errorType = ErrorType.UNCHECK;
        if (!XString.isNullOrEmpty(tableName)) {
            if (tableName.toLowerCase().endsWith(".mdb")) {
                tableName = tableName.substring(0, tableName.length() - 4);
            } else if (tableName.toLowerCase().endsWith(".accdb")) {
                tableName = tableName.substring(0, tableName.length() - 6);
            }
            if (tableName.length() > 64) {
                //表名过长
                errorType = ErrorType.DESTNAMETOOLONG;
            }
            int strLength = tableName.length();
            for (char ch : tableName.toCharArray()) {
                if (ch == '.' || ch == '!' || ch == '[' || ch == ']' || ch == '`') {
                    //字符非法
                    errorType = ErrorType.DESTNAMECHARINVALID;
                }
            }
        }
        return errorType;
    }

    private static ErrorType checkFoxproTableName(String tableName) {
        ErrorType errorType = ErrorType.UNCHECK;
        if (!XString.isNullOrEmpty(tableName)) {
            if (tableName.toLowerCase().endsWith(".dbf")) {
                tableName = tableName.substring(0, tableName.lastIndexOf('.'));
            }
            if (tableName.length() > 128) {
                //表名过长
                errorType = ErrorType.DESTNAMETOOLONG;
            }
            char firstChar = tableName.charAt(0);
            if (!(CustomOperate.isLetterChar(firstChar) || CustomOperate.isUnderlineChar(firstChar) || CustomOperate.isChineseChar(firstChar))) {
                //首字符非法
                errorType = ErrorType.DESTNAMEFIRSTCHARIINVALID;
            }
            int strLength = tableName.length();
            for (int i = 1; i < strLength; i++) {
                char otherChar = tableName.charAt(i);
                if (!(CustomOperate.isLetterChar(otherChar) || CustomOperate.isUnderlineChar(otherChar) || CustomOperate.isChineseChar(otherChar))) {
                    //其他字符非法
                    errorType = ErrorType.DESTNAMECHARINVALID;
                }
            }
        }
        return errorType;
    }

    /**
     * 判断是否为Append模式
     *
     * @param option
     * @param destDataType
     * @return
     */
    private static boolean isAppendMode(ConvertOption option, DataType destDataType) {
        boolean rtn = false;
        if (option != null) {
            switch (destDataType) {
                case MAPGIS_SFCLS:
                case MAPGIS_ACLS:
                case MAPGIS_OCLS:
                case VECTOR_DXF:
                case VECTOR_DWG:
                    rtn = option.isAppendMode();
                    break;
            }
        }
        return rtn;
    }

    class ConvertItemInfo {
        public DataType destType;
        public String destName = "";
        public String destDir = "";
        public GeomType geoType;

        public ConvertItemInfo(DataType destType, String destName, String destDir) {
            this.destType = destType;
            this.destName = destName;
            if (!XString.isNullOrEmpty(destDir) && destDir.toLowerCase().startsWith(CustomOperate.gdbProName)) {
                destDir = URLParse.getDataBase(destDir);
            }
            this.destDir = destDir;
        }

        public ConvertItemInfo(DataType destType, String destName, String destDir, GeomType geom) {
            this.destType = destType;
            this.destName = destName;
            if (!XString.isNullOrEmpty(destDir) && destDir.toLowerCase().startsWith(CustomOperate.gdbProName)) {
                destDir = URLParse.getDataBase(destDir);
            }
            this.destDir = destDir;
            this.geoType = geom;
        }

        public DataType getDestType() {
            return destType;
        }

        public String getDestName() {
            return destName;
        }

        public String getDestDir() {
            return destDir;
        }

        public GeomType getGeoType() {
            return geoType;
        }
    }
    //endregion

    //region 参数
    private void setParams(ConvertItem convertItem) {
        if (convertItem != null) {
            DataType srcType = convertItem.getSourType();
            DataType destType = convertItem.getDestType();
            ConvertOption convertOption = convertItem.getConvertOption();
            String srcUrl = convertItem.getSourPath();
            this.sourcePane.setSrcPath(srcUrl);
            this.titledPaneParams.setContent(null);
            switch (srcType) {
                case MAPGIS_FDS:
                case MAPGIS_GDB: {
                    switch (destType) {
                        case ARCGIS_PERSONALGDB: {
                            VBox vBox = this.createPersonalGDBPane(convertOption);
                            this.titledPaneParams.setContent(vBox);
                            break;
                        }
                        default:
                            break;
                    }
                    break;
                }
                case MAPGIS_SFCLS:
                case MAPGIS_SFCLSP:
                case MAPGIS_SFCLSL:
                case MAPGIS_SFCLSR:
                case MAPGIS_SFCLSS:
                case MAPGIS_SFCLSE: {
                    switch (destType) {
                        case MAPGIS_SFCLS:
                        case MAPGIS_ACLS: {
                            VBox vBox = this.createSRefPane(convertOption, srcUrl, false);
                            this.titledPaneParams.setContent(vBox);
                            break;
                        }
                        case MAPGIS_6X_FILE: {
                            VBox vBoxSref = createSRefPane(convertOption, srcUrl, true);
                            VBox vBoxInt64Fld = this.createInt64FiledPane(convertOption, true);
                            this.titledPaneParams.setContent(new VBox(6, vBoxSref, vBoxInt64Fld));
                            break;
                        }
                        ////未完成
                        //case VECTOR_MIF:
                        //    //case VECTOR_DXF:
                        //{
                        //    _7xToOtherOptionForm form = new _7xToOtherOptionForm(convertOption, destType);
                        //    form.ShowDialog();
                        //    break;
                        //}
                        case VECTOR_SHP: {
                            VBox vBox = this.createShpPane(convertOption);
                            this.titledPaneParams.setContent(vBox);
                            break;
                        }
                        ////未完成
                        //case VECTOR_DXF:
                        //case VECTOR_DWG:
                        //{
                        //    _7xToDwgOptionForm form = new _7xToDwgOptionForm(convertOption);
                        //    if (form.ShowDialog() == DialogResult.OK && form.BatchSet)
                        //    {
                        //        for (ConvertItem item : tableView.getItems())
                        //        {
                        //            ConvertOption option = item.getConvertOption();
                        //            if (option == null)
                        //                continue;
                        //            DataType sType = item.getSourType();
                        //            if (option != convertOption && sType == srcType)
                        //            {
                        //                option.setDwgVersion(convertOption.getDwgVersion());
                        //                option.setDxfSymbolTable(convertOption.getDxfSymbolTable());
                        //            }
                        //        }
                        //    }
                        //    break;
                        //}
                        //case TABLE_ACCESS:
                        //case TABLE_DBF:
                        //case TABLE_EXCEL:
                        //    //case TABLE_TXT:
                        //{
                        //    String destUrl = convertItem.calcDestURL();
                        //    TableInfoForm tf = new TableInfoForm(srcUrl, destUrl, destType, convertOption);
                        //    tf.ShowDialog();
                        //    break;
                        //}
                        case ARCGIS_PERSONALGDB: {
                            VBox vBox = this.createPersonalGDBPane(convertOption);
                            this.titledPaneParams.setContent(vBox);
                            break;
                        }
                    }
                    break;
                }
                case MAPGIS_ACLS: {
                    switch (destType) {
                        case MAPGIS_SFCLS:
                        case MAPGIS_ACLS: {
                            VBox vBox = this.createSRefPane(convertOption, srcUrl, false);
                            this.titledPaneParams.setContent(vBox);
                            break;
                        }
                        case MAPGIS_6X_FILE: {
                            VBox vBoxSref = createSRefPane(convertOption, srcUrl, true);
                            VBox vBoxInt64Fld = this.createInt64FiledPane(convertOption, false);
                            this.titledPaneParams.setContent(new VBox(6, vBoxSref, vBoxInt64Fld));
                            break;
                        }
                        ////未完成
                        //case VECTOR_MIF:
                        //    //case VECTOR_DXF:
                        //{
                        //    _7xToOtherOptionForm form = new _7xToOtherOptionForm(convertOption, destType);
                        //    form.ShowDialog();
                        //    break;
                        //}
                        //case VECTOR_DXF:
                        //case VECTOR_DWG:
                        //{
                        //    _7xToDwgOptionForm form = new _7xToDwgOptionForm(convertOption);
                        //    if (form.ShowDialog() == DialogResult.OK && form.BatchSet)
                        //    {
                        //        for (ConvertItem item : tableView.getItems())
                        //        {
                        //            ConvertOption option = item.getConvertOption();
                        //            if (option == null)
                        //                continue;
                        //            DataType sType = item.getSourType();
                        //            if (option != convertOption && sType == srcType)
                        //            {
                        //                option.setDwgVersion(convertOption.getDwgVersion());
                        //                option.setDxfSymbolTable(convertOption.getDxfSymbolTable());
                        //            }
                        //        }
                        //    }
                        //    break;
                        //}
                        //case TABLE_ACCESS:
                        //case TABLE_DBF:
                        //case TABLE_EXCEL:
                        //    //case TABLE_TXT:
                        //{
                        //    String destUrl = convertItem.calcDestURL();
                        //    TableInfoForm tf = new TableInfoForm(srcUrl, destUrl, destType, convertOption);
                        //    tf.ShowDialog();
                        //    break;
                        //}
                        case ARCGIS_PERSONALGDB: {
                            VBox vBox = this.createPersonalGDBPane(convertOption);
                            this.titledPaneParams.setContent(vBox);
                            break;
                        }
                    }
                    break;
                }
                case MAPGIS_OCLS: {
                    switch (destType) {
                        case ARCGIS_PERSONALGDB: {
                            VBox vBox = this.createPersonalGDBPane(convertOption);
                            this.titledPaneParams.setContent(vBox);
                            break;
                        }
                        ////未完成
                        //case TABLE_EXCEL:
                        //case TABLE_ACCESS:
                        //case TABLE_DBF:
                        //    //case TABLE_TXT:
                        //{
                        //    String destUrl = convertItem.calcDestURL();
                        //    TableInfoForm tf = new TableInfoForm(srcUrl, destUrl, destType, convertOption);
                        //    tf.ShowDialog();
                        //    break;
                        //}
                    }
                    break;
                }
                ////未完成
                //case MAPGIS_RAS:
                //{
                //    switch (destType)
                //    {
                //        case Raster_FILE: //栅格数据集 -> 栅格文件
                //        {
                //            XToRasFileForm form = new XToRasFileForm(convertOption);
                //            if (form.ShowDialog() == DialogResult.OK && form.BatchSet)
                //            {
                //                for (ConvertItem item : tableView.getItems())
                //                {
                //                    boolean nolongerPromt = false;
                //                    ConvertOption option = item.getConvertOption();
                //                    if (option == null)
                //                        continue;
                //                    DataType sType = item.getSourType();
                //                    DataType dType = item.getDestType();
                //                    if (option != convertOption && sType == srcType && dType == destType)
                //                    {
                //                        RasterDataset rds = new RasterDataset();
                //                        if (rds.open(item.getSourPath(), RasterAccess.Read) > 0)
                //                        {
                //                            int bandCont = rds.getBandNum();
                //                            RasterDataType srcRasDataType = rds.getPixelType();
                //                            rds.close();
                //                            boolean supportMultiBand = true;
                //                            List<RasterDataType> srcDataTypes = XToRasFileForm.GetSupportType(bandCont, convertOption.getDestDrive(), ref supportMultiBand);
                //                            if (srcDataTypes.contains(srcRasDataType))
                //                            {
                //                                if (supportMultiBand || bandCont == 1)
                //                                {
                //                                    //此处与 栅格文件->栅格文件 相同，此处仍需要再打开源数据判断目的数据是否能被转换
                //                                    //栅格文件参数
                //                                    option.setDestDrive(convertOption.getDestDrive());
                //                                    //Bil数据参数
                //                                    option.m_RasToDemBil.ByteOrderNo = convertOption.m_RasToDemBil.ByteOrderNo;
                //                                    option.m_RasToDemBil.PixelType = convertOption.m_RasToDemBil.PixelType;
                //                                    option.m_RasToDemBil.PixelBits = convertOption.m_RasToDemBil.PixelBits;
                //                                    option.m_RasToDemBil.EnlargeCeff = convertOption.m_RasToDemBil.EnlargeCeff;
                //                                    option.m_RasToDemBil.InvalidZValue = convertOption.m_RasToDemBil.InvalidZValue;
                //                                    option.m_RasToDemBil.MoveCeff = convertOption.m_RasToDemBil.MoveCeff;
                //
                //                                    option.getRasTrans() = convertOption.getRasTrans().Clone();
                //                                }
                //                            }
                //                        } else
                //                        {
                //                            if (!nolongerPromt)
                //                            {
                //                                this.Invoke(new MethodInvoker(delegate
                //                                {
                //                                MapGIS.Desktop.UI.Controls.MapGISErrorForm.ShowLastErrorEx(
                //                                    out nolongerPromt
                //                                );
                //                                }));
                //                            }
                //                        }
                //                    }
                //                }
                //            }
                //            break;
                //        }
                //        case MAPGIS_RAS: //栅格数据集 -> 栅格数据集
                //        {
                //            RasCommonParamSetForm form = new RasCommonParamSetForm(convertOption);
                //            if (form.ShowDialog() == DialogResult.OK && form.BatchSet)
                //            {
                //                for (ConvertItem item : tableView.getItems())
                //                {
                //                    ConvertOption option = item.getConvertOption();
                //                    if (option == null)
                //                        continue;
                //                    DataType sType = item.getSourType();
                //                    DataType dType = item.getDestType();
                //                    if (option != convertOption && sType == srcType && dType == destType)
                //                    {
                //                        option.getRasTrans().IsBuildPyraid = convertOption.getRasTrans().IsBuildPyraid;
                //                        option.getRasTrans().Interpolate = convertOption.getRasTrans().Interpolate;
                //                        option.getRasTrans().TopPyraidRowSize = convertOption.getRasTrans().TopPyraidRowSize;
                //                        option.getRasTrans().TopPyraidColSize = convertOption.getRasTrans().TopPyraidColSize;
                //
                //                        option.getRasTrans().IsCalStatasics = convertOption.getRasTrans().IsCalStatasics;
                //                        option.getRasTrans().StaSampleRate = convertOption.getRasTrans().StaSampleRate;
                //                    }
                //                }
                //            }
                //            break;
                //        }
                //    }
                //    break;
                //}
                case MAPGIS_6X_FILE:
                case MAPGIS_6X_WT:
                case MAPGIS_6X_WL:
                case MAPGIS_6X_WP: {
                    switch (destType) {
                        case MAPGIS_SFCLS:
                        case MAPGIS_ACLS: {
                            VBox vBoxSref = createSRefPane(convertOption, srcUrl, true);
                            VBox vBox6xLib = create6xLibPane(convertOption, true);
                            VBox vBoxEmpty = createEmptyDataPane(convertOption, true);
                            UnificationButton button = new UnificationButton();
                            VBox vBox = new VBox(6, vBoxSref, vBox6xLib, vBoxEmpty, button);
                            VBox.setMargin(button, new Insets(6, 0, 0, 0));
                            this.titledPaneParams.setContent(vBox);

                            button.setOnAction(event ->
                            {
                                for (ConvertItem item : tableView.getItems()) {
                                    ConvertOption option = item.getConvertOption();
                                    if (option != null) {
                                        DataType dType = item.getDestType();
                                        if (option != convertOption && dType == destType) {
                                            option.setSrsID(convertOption.getSrsID());
                                            option.setSlib6x(convertOption.getSlib6x());
                                            option.setClib6x(convertOption.getClib6x());
                                            option.setKeepNullData(convertOption.isKeepNullData());
                                        }
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //case VECTOR_MIF:
                //    //case VECTOR_DXF:
                //{
                //    switch (destType)
                //    {
                //        case MAPGIS_SFCLS:
                //        {
                //            MifDxfTo7xOptionForm form = new MifDxfTo7xOptionForm(convertOption, srcUrl);
                //            if (form.ShowDialog() == DialogResult.OK && form.BatchSet)
                //            {
                //                for (ConvertItem item : tableView.getItems())
                //                {
                //                    ConvertOption option = item.getConvertOption();
                //                    if (option == null)
                //                        continue;
                //                    DataType sType = item.getSourType();
                //                    DataType dType = item.getDestType();
                //                    if (option != convertOption && dType == destType && (sType == DataType.VECTOR_MIF))
                //                    {
                //                        option.setDxfSymbolTable(convertOption.getDxfSymbolTable());
                //                        option.setMifSymbolTable(convertOption.getMifSymbolTable());
                //                        option.setKeepNullData(convertOption.isKeepNullData());
                //                        option.setMappingWay(convertOption.getMappingWay());
                //                        option.setAdjustStrPosition(convertOption.isAdjustStrPosition());
                //                        option.setAdjustFontSize(convertOption.isAdjustFontSize());
                //                    }
                //                }
                //            }
                //            break;
                //        }
                //    }
                //    break;
                //}
                case VECTOR_DGN:
                case VECTOR_E00:
                case VECTOR_GML:
                case VECTOR_KML:
                case VECTOR_SHP:
                case VECTOR_VCT: {
                    switch (destType) {
                        case MAPGIS_SFCLS: {
                            VBox vBox = this.createEmptyDataPane(convertOption, false);
                            UnificationButton button = new UnificationButton();
                            this.titledPaneParams.setContent(new VBox(12, vBox, button));
                            //统改同类型转换
                            button.setOnAction(event ->
                            {
                                for (ConvertItem item : tableView.getItems()) {
                                    ConvertOption option = item.getConvertOption();
                                    if (option != null) {
                                        DataType sType = item.getSourType();
                                        DataType dType = item.getDestType();
                                        if (option != convertOption && dType == destType
                                                && (sType == DataType.VECTOR_DGN || sType == DataType.VECTOR_E00 || sType == DataType.VECTOR_GML
                                                || sType == DataType.VECTOR_KML || sType == DataType.VECTOR_SHP || sType == DataType.VECTOR_VCT)) {
                                            option.setKeepNullData(convertOption.isKeepNullData());
                                        }
                                    }
                                }
                            });
                            break;
                        }
                    }
                    break;
                }
                //增加三维数据支持 0414 ysp
                case Model_3DS:
                case Model_OBJ:
                case Model_DAE:
                case Model_OSGB:
                case Model_FBX:
                case Model_XML:
                case Model_X:
                case Model_LAS: {
                    VBox vBox = this.create3DFileParamsPane(convertOption, false);
                    this.titledPaneParams.setContent(vBox);
                    break;
                }
                ////未完成
                //case VECTOR_DXF:
                //case VECTOR_DWG:
                //{
                //    DwgTo7xOptionForm form = new DwgTo7xOptionForm(convertOption);
                //    if (form.ShowDialog() == DialogResult.OK && form.BatchSet)
                //    {
                //        for (ConvertItem item : tableView.getItems())
                //        {
                //            ConvertOption option = item.getConvertOption();
                //            if (option == null)
                //                continue;
                //            DataType sType = item.getSourType();
                //            if (option != convertOption && sType == srcType)
                //            {
                //                option.setMappingWay(convertOption.getMappingWay());
                //                option.setAdjustFontSize(convertOption.isAdjustFontSize());
                //                option.setDxfSymbolTable(convertOption.getDxfSymbolTable());
                //            }
                //        }
                //    }
                //    break;
                //}
                //case TXT:
                //{
                //    switch (destType)
                //    {
                //        case MAPGIS_SFCLS:
                //        {
                //            TxtToSfclsForm form = new TxtToSfclsForm(convertOption);
                //            if (form.ShowDialog() == DialogResult.OK)
                //            {
                //                if (form.BatchSet)
                //                {
                //                    for (ConvertItem item : tableView.getItems())
                //                    {
                //                        ConvertOption option = item.getConvertOption();
                //                        if (option == null)
                //                            continue;
                //                        DataType dType = item.getDestType();
                //                        if (option != convertOption && item.getSourType() == srcType && dType == destType)
                //                        {
                //                            if (option.getTxt27xParam() == null)
                //                                option.getTxt27xParam() = new GeoDataBase.Convert.Txt27xParamStruct();
                //                            option.getTxt27xParam().attNameNum = convertOption.getTxt27xParam().attNameNum;
                //                            option.getTxt27xParam().destProjTrans = convertOption.getTxt27xParam().destProjTrans;
                //                            option.getTxt27xParam().dimNum = convertOption.getTxt27xParam().dimNum;
                //                            option.getTxt27xParam().editStartX = convertOption.getTxt27xParam().editStartX;
                //                            option.getTxt27xParam().editStartY = convertOption.getTxt27xParam().editStartY;
                //                            option.getTxt27xParam().fieldNameArr = convertOption.getTxt27xParam().fieldNameArr;
                //                            option.getTxt27xParam().fieldTypeArr = convertOption.getTxt27xParam().fieldTypeArr;
                //                            option.getTxt27xParam().genLineStatus = convertOption.getTxt27xParam().genLineStatus;
                //                            option.getTxt27xParam().lineAttPosNum = convertOption.getTxt27xParam().lineAttPosNum;
                //                            option.getTxt27xParam().lineSeparator = convertOption.getTxt27xParam().lineSeparator;
                //                            option.getTxt27xParam().linInfo = convertOption.getTxt27xParam().linInfo;
                //                            option.getTxt27xParam().pntInfo = convertOption.getTxt27xParam().pntInfo;
                //                            option.getTxt27xParam().projStatus = convertOption.getTxt27xParam().projStatus;
                //                            option.getTxt27xParam().projTrans = convertOption.getTxt27xParam().projTrans;
                //                            option.getTxt27xParam().readType = convertOption.getTxt27xParam().readType;
                //                            option.getTxt27xParam().separator = convertOption.getTxt27xParam().separator;
                //                            option.getTxt27xParam().sepStru = convertOption.getTxt27xParam().sepStru;
                //                            option.getTxt27xParam().skipLess = convertOption.getTxt27xParam().skipLess;
                //                            option.getTxt27xParam().startLine = convertOption.getTxt27xParam().startLine;
                //                            option.getTxt27xParam().str7xName = convertOption.getTxt27xParam().str7xName;
                //                            option.getTxt27xParam().x = convertOption.getTxt27xParam().x;
                //                            option.getTxt27xParam().y = convertOption.getTxt27xParam().y;
                //                        }
                //                    }
                //                }
                //                this.checkConvertItems();
                //            }
                //            break;
                //        }
                //    }
                //    break;
                //}
                //case Raster_FILE:
                //{
                //    switch (destType)
                //    {
                //        case Raster_FILE: //栅格文件 -> 栅格文件
                //        {
                //            XToRasFileForm form = new XToRasFileForm(convertOption);
                //            if (form.ShowDialog() == DialogResult.OK && form.BatchSet)
                //            {
                //                boolean nolongerPromt = false;
                //                for (ConvertItem item : tableView.getItems())
                //                {
                //                    ConvertOption option = item.getConvertOption();
                //                    if (option == null)
                //                        continue;
                //                    DataType sType = item.getSourType();
                //                    DataType dType = item.getDestType();
                //                    if (option != convertOption && sType == srcType && dType == destType)
                //                    {
                //                        RasterDataset rds = new RasterDataset();
                //                        if (rds.open(item.getSourPath(), RasterAccess.Read) > 0)
                //                        {
                //                            int bandCont = rds.getBandNum();
                //                            RasterDataType srcRasDataType = rds.getPixelType();
                //                            rds.close();
                //                            boolean supportMultiBand = true;
                //                            List<RasterDataType> srcDataTypes = XToRasFileForm.GetSupportType(bandCont, convertOption.getDestDrive(), ref supportMultiBand);
                //                            if (srcDataTypes.contains(srcRasDataType))
                //                            {
                //                                if (supportMultiBand || bandCont == 1)
                //                                {
                //                                    option.setDestDrive(convertOption.getDestDrive());
                //                                    option.setRasTrans(convertOption.getRasTrans().clone());
                //                                }
                //                            }
                //                        } else
                //                        {
                //                            if (!nolongerPromt)
                //                            {
                //                                this.Invoke(new MethodInvoker(delegate
                //                                {
                //                                MapGIS.Desktop.UI.Controls.MapGISErrorForm.ShowLastErrorEx(
                //                                    out nolongerPromt
                //                                );
                //                                }));
                //                            }
                //                        }
                //                    }
                //                }
                //            }
                //            break;
                //        }
                //        case MAPGIS_RAS: //栅格文件 -> 栅格数据集
                //        {
                //            switch (convertOption.SrcDataExtension)
                //            {
                //                // 修改说明：屏蔽掉arcInfo和Bil的设置参数框，使用通用的参数设置框，因为底层不支持这些特殊参数。解决bug 8215、8216、10055。
                //                // 修改人：周小飞 2018-04-27
                //                        /*
                //                        case ".txt":
                //                            {
                //                                DemArcInfoToRasOptionForm form = new DemArcInfoToRasOptionForm(convertOption);
                //                                if (form.ShowDialog() == DialogResult.OK && form.BatchSet)
                //                                {
                //                                    for (ConvertItem item : tableView.getItems())
                //                                    {
                //                                        if (!this.gridView1.IsValidRowHandle(curRowHandler))
                //                                            continue;
                //                                        DataRow curDataRow = this.gridView1.GetDataRow(curRowHandler);
                //
                //                                        ConvertOption option = item.getConvertOption()
                //                                        itemOptionDictionary.TryGetValue(curDataRow, out option);
                //                                        if (option == null)
                //                                            continue;
                //                                        DataType sType = item.getSourType();
                //                                        DataType dType = item.getDestType();
                //                                        if (option != convertOption && sType == srcType && dType == destType && option.SrcDataExtension == convertOption.SrcDataExtension)
                //                                        {
                //                                            option.isTxtDemToRas_DataType() = convertOption.isTxtDemToRas_DataType();
                //
                //                                            option.getRasTrans().IsBuildPyraid = convertOption.getRasTrans().IsBuildPyraid;
                //                                            option.getRasTrans().Interpolate = convertOption.getRasTrans().Interpolate;
                //                                            option.getRasTrans().TopPyraidRowSize = convertOption.getRasTrans().TopPyraidRowSize;
                //                                            option.getRasTrans().TopPyraidColSize = convertOption.getRasTrans().TopPyraidColSize;
                //
                //                                            option.getRasTrans().IsCalStatasics = convertOption.getRasTrans().IsCalStatasics;
                //                                            option.getRasTrans().StaSampleRate = convertOption.getRasTrans().StaSampleRate;
                //                                        }
                //                                    }
                //                                }
                //
                //                            }
                //                            break;
                //                        case ".bil":
                //                            {
                //                                DemBilToRasOptionForm form = new DemBilToRasOptionForm(convertOption);
                //                                if (form.ShowDialog() == DialogResult.OK && form.BatchSet)
                //                                {
                //                                    for (ConvertItem item : tableView.getItems())
                //                                    {
                //                                        if (!this.gridView1.IsValidRowHandle(curRowHandler))
                //                                            continue;
                //                                        DataRow curDataRow = this.gridView1.GetDataRow(curRowHandler);
                //
                //                                        ConvertOption option = item.getConvertOption()
                //                                        itemOptionDictionary.TryGetValue(curDataRow, out option);
                //                                        if (option == null)
                //                                            continue;
                //                                        DataType sType = item.getSourType();
                //                                        DataType dType = item.getDestType();
                //                                        if (option != convertOption && sType == srcType && dType == destType && option.SrcDataExtension == convertOption.SrcDataExtension)
                //                                        {
                //                                            option.m_BilDemToRas_Option.EnlargeCeff = convertOption.m_BilDemToRas_Option.EnlargeCeff;
                //                                            option.m_BilDemToRas_Option.MoveCeff = convertOption.m_BilDemToRas_Option.MoveCeff;
                //                                            option.m_BilDemToRas_Option.PixelType = convertOption.m_BilDemToRas_Option.PixelType;
                //
                //                                            option.getRasTrans().IsBuildPyraid = convertOption.getRasTrans().IsBuildPyraid;
                //                                            option.getRasTrans().Interpolate = convertOption.getRasTrans().Interpolate;
                //                                            option.getRasTrans().TopPyraidRowSize = convertOption.getRasTrans().TopPyraidRowSize;
                //                                            option.getRasTrans().TopPyraidColSize = convertOption.getRasTrans().TopPyraidColSize;
                //
                //                                            option.getRasTrans().IsCalStatasics = convertOption.getRasTrans().IsCalStatasics;
                //                                            option.getRasTrans().StaSampleRate = convertOption.getRasTrans().StaSampleRate;
                //                                        }
                //                                    }
                //                                }
                //
                //                            }
                //                            break;
                //                        */
                //                default: //其他栅格文件 -> 栅格数据集
                //                {
                //                    RasCommonParamSetForm form = new RasCommonParamSetForm(convertOption);
                //                    if (form.ShowDialog() == DialogResult.OK && form.BatchSet)
                //                    {
                //                        for (ConvertItem item : tableView.getItems())
                //                        {
                //                            ConvertOption option = item.getConvertOption();
                //                            if (option == null)
                //                                continue;
                //                            DataType sType = item.getSourType();
                //                            DataType dType = item.getDestType();
                //                            if (option != convertOption && sType == srcType && dType == destType)
                //                            {
                //                                option.getRasTrans().IsBuildPyraid = convertOption.getRasTrans().IsBuildPyraid;
                //                                option.getRasTrans().Interpolate = convertOption.getRasTrans().Interpolate;
                //                                option.getRasTrans().TopPyraidRowSize = convertOption.getRasTrans().TopPyraidRowSize;
                //                                option.getRasTrans().TopPyraidColSize = convertOption.getRasTrans().TopPyraidColSize;
                //
                //                                option.getRasTrans().IsCalStatasics = convertOption.getRasTrans().IsCalStatasics;
                //                                option.getRasTrans().StaSampleRate = convertOption.getRasTrans().StaSampleRate;
                //                            }
                //                        }
                //                    }
                //                }
                //                break;
                //            }
                //            break;
                //        }
                //    }
                //    break;
                //}
                //case TABLE_EXCEL:
                //case TABLE_ACCESS:
                //case TABLE_DBF:
                //    //case TABLE_TXT:
                //{
                //    switch (destType)
                //    {
                //        case MAPGIS_OCLS:
                //        {
                //            String destUrl = convertItem.calcDestURL();
                //            TableInfoForm tf = new TableInfoForm(srcUrl, destUrl, destType, convertOption);
                //            tf.ShowDialog();
                //            break;
                //        }
                //    }
                //    break;
                //}
                default:
                    break;
            }

            boolean showParams = this.titledPaneParams.getContent() != null;
            this.titledPaneParams.setManaged(showParams);
            this.titledPaneParams.setVisible(showParams);
        }
    }

    /**
     * 创建参照系的设置界面
     *
     * @param option
     * @param url
     * @param createTitle 是否创建标题分割栏
     * @return
     */
    private VBox createSRefPane(ConvertOption option, String url, boolean createTitle) {
        CheckBox checkBox = new CheckBox("保持源空间参照系");
        ButtonEdit buttonEdit = new ButtonEdit();
        buttonEdit.disableProperty().bind(checkBox.selectedProperty());
        if (option != null) {
            checkBox.setSelected(option.getSrsID() == -1);
            if (option.getSrsID() != -1) {
                buttonEdit.setUserData(option.getSrsID());
                DataBase db = URLParse.openDataBase(url);
                if (db != null) {
                    SRefData sref = db.getSRef(option.getSrsID());
                    if (sref != null) {
                        buttonEdit.setText(sref.getSRSName());
                    }
                    db.close();
                }
            }
        }

        buttonEdit.setOnButtonClick(event ->
        {
            DataBase db = URLParse.openDataBase(url);
            if (db != null) {
                SRefManagerDialog dlg = new SRefManagerDialog();
                if (dlg.showAndWait().equals(Optional.of((ButtonType.OK)))) {
                    SRefData sref = dlg.getSelectedSRef();
                    if (sref != null) {
                        buttonEdit.setText(sref.getSRSName());
                        int srsID = db.addSRef(sref);
                        buttonEdit.setUserData(db.addSRef(sref));//未封装，用appendex那种
                        option.setSrsID(checkBox.isSelected() ? -1 : srsID);
                    }
                }
                db.close();
            } else {
                Notification.showInformation(checkBox.getScene().getWindow(), "选择参照系", "数据库打开失败.", Pos.TOP_RIGHT);
            }
        });

        VBox vBox = new VBox(6, checkBox, buttonEdit);
        if (createTitle) {
            vBox.setPadding(new Insets(0, 6, 0, 12));
            vBox = new VBox(6, new TitleSeparator("空间坐标系选项", true), vBox);
        }
        vBox.setFillWidth(true);
        return vBox;
    }

    /**
     * 创建空数据处理的设置界面
     *
     * @param option
     * @param createTitle 是否创建标题分割栏
     * @return
     */
    private VBox createEmptyDataPane(ConvertOption option, boolean createTitle) {
        CheckBox checkBox = new CheckBox("保留空数据(只有属性结构，不存在实体)");
        if (option != null) {
            checkBox.setSelected(option.isKeepNullData());
            option.keepNullDataProperty().bind(checkBox.selectedProperty());
        }
        VBox vBox = new VBox(6, checkBox);
        if (createTitle) {
            vBox.setPadding(new Insets(0, 6, 0, 12));
            vBox = new VBox(6, new TitleSeparator("空数据选项"), vBox);
        }
        vBox.setFillWidth(true);
        return vBox;
    }

    /**
     * 创建导出为shp数据的设置界面（包含统改）
     *
     * @param option
     * @return
     */
    private VBox createShpPane(ConvertOption option) {
        CheckBox checkBox = new CheckBox("导出带z坐标的shp文件");
        UnificationButton button = new UnificationButton();
        if (option != null) {
            checkBox.setSelected(option.isSfclsToZShp());
            option.sfclsToZShpProperty().bind(checkBox.selectedProperty());

            button.setOnAction(event ->
            {
                for (ConvertItem item : tableView.getItems()) {
                    ConvertOption opt = item.getConvertOption();
                    if (opt != null) {
                        if (opt != option) {
                            opt.setSfclsToZShp(option.isSfclsToZShp());
                        }
                    }
                }
            });
        }

        return new VBox(12, checkBox, button);
    }

    /**
     * 创建导出为ArcGIS Personal GDB时的参数界面（包含统改）
     *
     * @param option
     * @return
     */
    private VBox createPersonalGDBPane(ConvertOption option) {
        RadioButton radioButton9 = new RadioButton("9.x");
        RadioButton radioButton10 = new RadioButton("10.x");
        radioButton9.setSelected(option != null && option.getPgdbVersion() != 2);
        radioButton10.setSelected(option == null || option.getPgdbVersion() == 2);
        ToggleGroup tg = new ToggleGroup();
        tg.getToggles().addAll(radioButton9, radioButton10);
        HBox hBox = new HBox(12, new Label("ArcGIS Personal GDB版本:"), radioButton9, radioButton10);
        HBox.setMargin(radioButton10, new Insets(0, 0, 0, 12));
        UnificationButton button = new UnificationButton();

        if (option != null) {
            radioButton10.selectedProperty().addListener((o, ov, nv) -> option.setPgdbVersion(nv ? 2 : 1));

            button.setOnAction(event ->
            {
                for (ConvertItem item : tableView.getItems()) {
                    ConvertOption opt = item.getConvertOption();
                    if (opt != null) {
                        DataType dType = item.getDestType();
                        if (opt != option && dType == DataType.ARCGIS_PERSONALGDB) {
                            opt.setPgdbVersion(option.getPgdbVersion());
                        }
                    }
                }
            });
        }
        return new VBox(12, hBox, button);
    }

    /**
     * MapGIS 6x系统库设置
     *
     * @param option
     * @param createTitle 是否创建标题分割栏
     * @return
     */
    private VBox create6xLibPane(ConvertOption option, boolean createTitle) {
        ButtonEdit buttonEditSLib = new ButtonEdit();
        ButtonEdit buttonEditCLib = new ButtonEdit();
        if (option != null) {
            buttonEditSLib.setText(option.getSlib6x());
            buttonEditCLib.setText(option.getClib6x());
        }
        buttonEditSLib.setOnButtonClick(event ->
        {
            DirectoryChooser dlg = new DirectoryChooser();
            dlg.setTitle("选择MapGIS 6x符号库目录");
            File file = dlg.showDialog(this.getCurrentWindow());
            if (file != null) {
                buttonEditSLib.setText(file.getPath());
                option.setSlib6x(file.getPath());
            }
        });
        buttonEditCLib.setOnButtonClick(event ->
        {
            DirectoryChooser dlg = new DirectoryChooser();
            dlg.setTitle("选择MapGIS 6x字体库目录");
            File file = dlg.showDialog(this.getCurrentWindow());
            if (file != null) {
                buttonEditCLib.setText(file.getPath());
                option.setClib6x(file.getPath());
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.setHgap(6);
        gridPane.setVgap(6);
        gridPane.add(new Label("符号库(SLib):"), 0, 0);
        gridPane.add(new Label("字体库(CLib):"), 0, 1);
        gridPane.add(buttonEditSLib, 1, 0);
        gridPane.add(buttonEditCLib, 1, 1);
        gridPane.getColumnConstraints().add(new ColumnConstraints(95));
        GridPane.setHgrow(buttonEditSLib, Priority.ALWAYS);
        VBox vBox = new VBox(6, gridPane);

        if (createTitle) {
            vBox.setPadding(new Insets(0, 6, 0, 12));
            vBox = new VBox(6, new TitleSeparator("MapGIS 6x系统库设置"), vBox);
        }
        vBox.setFillWidth(true);
        return vBox;
    }

    /**
     * Int64类型字段处理选项
     *
     * @param option
     * @param createTitle
     * @return
     */
    private VBox createInt64FiledPane(ConvertOption option, boolean createTitle) {
        ZDComboBox<String> comboBox = new ZDComboBox<>(FXCollections.observableArrayList("转换成字符串类型", "去掉"));
        if (option != null) {
            comboBox.getSelectionModel().select(option.getFldInt64Operate());
            comboBox.getSelectionModel().selectedIndexProperty().addListener((o, ov, nv) -> option.setFldInt64Operate(nv.intValue()));
        }

        HBox hBox = new HBox(6, new Label("Int64类型字段:"), comboBox);
        hBox.setAlignment(Pos.CENTER_LEFT);
        comboBox.prefWidthProperty().bind(hBox.widthProperty().subtract(107));
        VBox vBox = new VBox(6, hBox);
        if (createTitle) {
            vBox.setPadding(new Insets(0, 6, 0, 12));
            vBox = new VBox(6, new TitleSeparator("Int64类型字段处理选项"), vBox);
        }
        vBox.setFillWidth(true);
        return vBox;
    }

    /**
     * 创建三维模型导入参数设置界面
     *
     * @param option
     * @param createTitle
     * @return
     */
    private VBox create3DFileParamsPane(ConvertOption option, boolean createTitle) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(6);
        gridPane.setVgap(6);
        NumberTextField numberTextFieldPosX = new NumberTextField(BigDecimal.valueOf(option.getPosX()));
        NumberTextField numberTextFieldPosY = new NumberTextField(BigDecimal.valueOf(option.getPosY()));
        NumberTextField numberTextFieldPosZ = new NumberTextField(BigDecimal.valueOf(option.getPosZ()));
        NumberTextField numberTextFieldScaleX = new NumberTextField(BigDecimal.valueOf(option.getScaleX()));
        NumberTextField numberTextFieldScaleY = new NumberTextField(BigDecimal.valueOf(option.getScaleY()));
        NumberTextField numberTextFieldScaleZ = new NumberTextField(BigDecimal.valueOf(option.getScaleZ()));
        NumberTextField numberTextFieldAngleX = new NumberTextField(BigDecimal.valueOf(option.getAngleX()));
        NumberTextField numberTextFieldAngleY = new NumberTextField(BigDecimal.valueOf(option.getAngleY()));
        NumberTextField numberTextFieldAngleZ = new NumberTextField(BigDecimal.valueOf(option.getAngleZ()));
        option.posXProperty().bind(numberTextFieldPosX.numberProperty());
        option.posYProperty().bind(numberTextFieldPosY.numberProperty());
        option.posZProperty().bind(numberTextFieldPosZ.numberProperty());
        option.scaleXProperty().bind(numberTextFieldScaleX.numberProperty());
        option.scaleYProperty().bind(numberTextFieldScaleY.numberProperty());
        option.scaleZProperty().bind(numberTextFieldScaleZ.numberProperty());
        option.angleXProperty().bind(numberTextFieldAngleX.numberProperty());
        option.angleYProperty().bind(numberTextFieldAngleY.numberProperty());
        option.angleZProperty().bind(numberTextFieldAngleZ.numberProperty());

        CheckBox checkBoxComplex = new CheckBox("整体导入");
        checkBoxComplex.setSelected(option.isComplex());
        option.complexProperty().bind(checkBoxComplex.selectedProperty());

        CheckBox checkBoxOverWrite = new CheckBox("覆盖同名纹理文件");
        checkBoxOverWrite.setSelected(option.isOverWrite());
        option.overWriteProperty().bind(checkBoxComplex.selectedProperty());

        CheckBox checkBoxGlobal = new CheckBox("球面数据");
        checkBoxGlobal.setSelected(option.isGlobal());
        option.globalProperty().bind(checkBoxGlobal.selectedProperty());

        gridPane.add(new Label("偏移量X:"), 0, 0);
        gridPane.add(new Label("偏移量Y:"), 0, 1);
        gridPane.add(new Label("偏移量Z:"), 0, 2);
        gridPane.add(numberTextFieldPosX, 1, 0);
        gridPane.add(numberTextFieldPosY, 1, 1);
        gridPane.add(numberTextFieldPosZ, 1, 2);

        gridPane.add(new Label("缩放比X:"), 0, 3);
        gridPane.add(new Label("缩放比Y:"), 0, 4);
        gridPane.add(new Label("缩放比Z:"), 0, 5);
        gridPane.add(numberTextFieldScaleX, 1, 3);
        gridPane.add(numberTextFieldScaleY, 1, 4);
        gridPane.add(numberTextFieldScaleZ, 1, 5);

        gridPane.add(new Label("旋转角度X:"), 0, 6);
        gridPane.add(new Label("旋转角度Y:"), 0, 7);
        gridPane.add(new Label("旋转角度Z:"), 0, 8);
        gridPane.add(numberTextFieldAngleX, 1, 6);
        gridPane.add(numberTextFieldAngleY, 1, 7);
        gridPane.add(numberTextFieldAngleZ, 1, 8);
        gridPane.add(checkBoxComplex, 0, 9);
        gridPane.add(checkBoxOverWrite, 1, 9);
        gridPane.add(checkBoxGlobal, 0, 10);
        gridPane.getColumnConstraints().add(new ColumnConstraints(95));
        GridPane.setHgrow(numberTextFieldPosX, Priority.ALWAYS);

        VBox vBox = new VBox(6, gridPane);
        if (createTitle) {
            vBox.setPadding(new Insets(0, 6, 0, 12));
            vBox = new VBox(6, new TitleSeparator("三维模型导入参数设置"), vBox);
        }
        vBox.setFillWidth(true);
        return vBox;
    }
    //endregion

    //region 三维导入方法

    /**
     * 外部三维数据文件(*.3ds;*.obj;*.dae;*.osgb;*.fbx;*.xml;*.x;)-> MapGIS_GDB（面简单要素类）
     *
     * @param dc3D
     * @param srcUrl
     * @param destUrl
     * @param option
     * @return
     */
    private boolean convert3DFileToModel(GeoDataConv3D dc3D, String srcUrl, String destUrl, ConvertOption option) {
        boolean rtn = false;
        SFeatureCls desSfcls = new SFeatureCls();
        int clsID = desSfcls.create(destUrl, GeomType.GeomSurface);
        if (clsID > 0) {
            int result1 = dc3D.setImpDataSrc(srcUrl, desSfcls);
            boolean bComplex = option.isComplex();
            boolean isOverWrite = option.isOverWrite();
            result1 = dc3D.setOption(bComplex, isOverWrite);
            boolean isGlobe = option.isGlobal();
            Dot3D pos = new Dot3D(option.getPosX(), option.getPosY(), option.getPosZ());
            Dot3D scale = new Dot3D(option.getScaleX(), option.getScaleY(), option.getScaleZ());
            Dot3D angle = new Dot3D(option.getAngleX(), option.getAngleY(), option.getAngleZ());
            result1 = dc3D.setTransOption(isGlobe, pos, scale, angle);
            rtn = dc3D.convert() > 0;
            desSfcls.close();
        }
        return rtn;
    }

    /**
     * 点云数据(*.las;)-> MapGIS_GDB（点简单要素类）
     *
     * @param dc3D
     * @param srcUrl
     * @param destUrl
     * @param option
     * @return
     */
    private boolean convertPointCloudFileToModel(GeoDataConv3D dc3D, String srcUrl, String destUrl, ConvertOption option) {
        boolean rtn = false;
        SFeatureCls desSfcls = new SFeatureCls();
        int clsID = desSfcls.create(destUrl, GeomType.GeomPnt);
        if (clsID > 0) {
            int result1 = dc3D.setImpDataSrc(srcUrl, desSfcls);
            boolean bComplex = option.isComplex();
            boolean isOverWrite = option.isOverWrite();
            result1 = dc3D.setOption(bComplex, isOverWrite);
            boolean isGlobe = option.isGlobal();
            Dot3D pos = new Dot3D(option.getPosX(), option.getPosY(), option.getPosZ());
            Dot3D scale = new Dot3D(option.getScaleX(), option.getScaleY(), option.getScaleZ());
            Dot3D angle = new Dot3D(option.getAngleX(), option.getAngleY(), option.getAngleZ());
            result1 = dc3D.setTransOption(isGlobe, pos, scale, angle);
            rtn = dc3D.convert() > 0;
            desSfcls.close();
        }
        return rtn;
    }
    //endregion

    /**
     * @author CR
     * @file ConvertTask.java
     * @brief 转换任务
     * @create 2020-03-16.
     */
    public class ConvertTask extends Task<Boolean>//此处泛型为自己需要的泛型
    {
        private ConvertItem convertItem;

        public ConvertTask(ConvertItem item) {
            this.convertItem = item;
        }

        public ConvertItem getConvertItem() {
            return convertItem;
        }

        @Override
        protected Boolean call() {
            boolean rtn = false;
            if (this.convertItem != null) {
                writeLog(String.format("准备转换第%d项：[%s] -> [%s]", tableView.getItems().indexOf(this.convertItem) + 1, this.convertItem.getSourPath(), this.convertItem.calcDestURL()));
                if (this.convertItem.getErrorType() == ErrorType.UNCHECK) {
                    String msg = "";
                    try {
                        rtn = convert(this.convertItem);
                        if (!rtn) {
                            int errorCode = 0;
                            //MapGIS.UI.Controls.MapGISErrorForm.GetLastError(out errorCode);//未封装
                            if (errorCode != 0) {
                                String errorMsg = "";
                                //MapGIS.UI.Controls.MapGISErrorForm.GetErrorMsg(errorCode, out errorMsg);//未封装
                                msg = String.format("失败原因：%s(%s)", errorCode, errorMsg);
                            }
                        }
                    } catch (Exception ex) {
                        msg = ex.getMessage();
                        rtn = false;
                    }

                    writeLog(combineMessage(String.format("结束转换第%d项。%s", tableView.getItems().indexOf(this.convertItem) + 1, msg), rtn));
                } else {
                    writeLog(String.format("结束转换第%d项。存在错误：%s\t跳过", tableView.getItems().indexOf(this.convertItem) + 1, this.convertItem.getErrorType().getValue()));
                }
                writeLogEmptyLine();
            }
            return rtn;
        }

        //region 转换方法
        //开始转换
        private boolean convert(ConvertItem item) {
            boolean rtn = false;
            if (item != null) {
                String srcUrl = item.getSourPath();
                String destUrl = item.calcDestURL();
                DataType destDataType = item.getDestType();
                ConvertOption option = item.getConvertOption();

                //DataConvert dataConvert = option.getBatRWNum() > 0 ? new DataConvert(option.getBatRWNum(), false) : new DataConvert();//未封装(DataConvert构造）
                DataConvert dataConvert = new DataConvert();

                DataType srcDataType = item.getSourType();
                switch (srcDataType) {
                    case MAPGIS_SFCLS:
                    case MAPGIS_SFCLSP:
                    case MAPGIS_SFCLSL:
                    case MAPGIS_SFCLSR:
                    case MAPGIS_SFCLSS:
                    case MAPGIS_SFCLSE: {
                        ObjectProperty<DataBase> srcDB = new SimpleObjectProperty<>();
                        SFeatureCls srcCls = (SFeatureCls) CustomOperate.openVectorCls(srcUrl, srcDB);
                        DataBase srcDataBase = srcDB.get();
                        if (srcCls != null) {
                            writeLog(combineMessage("打开源简单要素类", true));
                            switch (destDataType) {
                                case MAPGIS_SFCLS: {
                                    //简单要素类->简单要素类
                                    ObjectProperty<DataBase> destDB = new SimpleObjectProperty<>();
                                    BooleanProperty isCreateProperty = new SimpleBooleanProperty(true);
                                    SFeatureCls destCls = (SFeatureCls) CustomOperate.createVectorCls(destUrl, srcDataBase, srcCls, option.isAppendMode(), option.getSrsID(), destDB, isCreateProperty);
                                    DataBase destDataBase = destDB.get();
                                    boolean isCreate = isCreateProperty.get();
                                    if (destCls != null && dataConvert.openSource(srcCls) > 0 && dataConvert.openDestination(destCls) > 0) {
                                        if (option.isAppendMode()) {
                                            dataConvert.setAppendMode();
                                        }
                                        writeLog(combineMessage(isCreate ? "创建目的简单要素类" : "打开目的简单要素类", true));
                                        rtn = this.convertVclsToVcls(dataConvert, srcCls, destCls);
                                        int sfclsID = destCls.getClsID();
                                        destCls.close();
                                        if (!rtn && destDataBase != null && isCreate) {
                                            SFeatureCls.remove(destDataBase, sfclsID);
                                        }
                                    } else {
                                        writeLog(combineMessage("创建目的简单要素类", false));
                                    }
                                    if (destCls != null) {
                                        destCls.dispose();
                                    }
                                    if (destDataBase != null) {
                                        destDataBase.close();
                                        destDataBase.dispose();
                                    }
                                    break;
                                }
                                case MAPGIS_OCLS: {
                                    ObjectProperty<DataBase> destDB = new SimpleObjectProperty<>();
                                    BooleanProperty isCreateProperty = new SimpleBooleanProperty(true);
                                    ObjectCls destCls = (ObjectCls) CustomOperate.createVectorCls(destUrl, srcCls, option.isAppendMode(), destDB, isCreateProperty);
                                    DataBase destDataBase = destDB.get();
                                    boolean isCreate = isCreateProperty.get();
                                    if (destCls != null && dataConvert.openSource(srcCls) > 0 && dataConvert.openDestination(destCls) > 0) {
                                        if (option.isAppendMode()) {
                                            dataConvert.setAppendMode();
                                        }
                                        writeLog(combineMessage(isCreate ? "创建目的对象类" : "打开目的对象类", true));
                                        rtn = this.convertVclsToVcls(dataConvert, srcCls, destCls);
                                        int sfclsID = destCls.getClsID();
                                        destCls.close();
                                        if (!rtn && destDataBase != null && isCreate)
                                            ObjectCls.remove(destDataBase, sfclsID);
                                    } else {
                                        writeLog(combineMessage("创建目的简单要素类", false));
                                    }
                                    if (destCls != null)
                                        destCls.dispose();
                                    if (destDataBase != null) {
                                        destDataBase.close();
                                        destDataBase.dispose();
                                    }
                                    break;
                                }
                                case VECTOR_DXF:
                                case VECTOR_DWG: {
                                    rtn = convertVclsToDxfDwg(dataConvert, srcUrl, destUrl, option);
                                    break;
                                }
                                case VECTOR_SHP: {
                                    if (dataConvert.openSource(srcCls) > 0 && dataConvert.openDestination(destUrl) > 0) {
                                        dataConvert.setOption(ConvertOptionType.OPT_SHP_ZINFO, option.isSfclsToZShp());
                                        rtn = this.convertVclsTo6xShp(dataConvert, srcCls, destUrl);
                                    }
                                    break;
                                }
                                case VECTOR_MIF: {
                                    if (dataConvert.openSource(srcCls) > 0 && dataConvert.openDestination(destUrl) > 0) {
                                        if (!XString.isNullOrEmpty(option.getMifSymbolTable())) {
                                            dataConvert.setOption(ConvertOptionType.OPT_MIF_MAPFILE, option.getMifSymbolTable());
                                        }
                                        rtn = this.convertVclsTo6xShp(dataConvert, srcCls, destUrl);
                                    }
                                    break;
                                }
                                case VECTOR_E00:
                                case VECTOR_VCT:
                                case VECTOR_JSON:
                                case VECTOR_GML:
                                case VECTOR_DGN:
                                case VECTOR_KML:
                                case ARCGIS_FILEGDB: {
                                    if (dataConvert.openSource(srcCls.getURL()) > 0 && dataConvert.openDestination(destUrl) > 0) {
                                        rtn = this.convertVclsTo6xShp(dataConvert, srcCls, destUrl);
                                    }
                                    break;
                                }
                                case ARCGIS_PERSONALGDB: {
                                    if (dataConvert.openSource(srcCls.getURL()) > 0 && dataConvert.openDestination(destUrl) > 0) {
                                        dataConvert.setOption(ConvertOptionType.OPT_PGDB_VERSION, option.getPgdbVersion());
                                        rtn = this.convertVclsTo6xShp(dataConvert, srcCls, destUrl);
                                    }
                                    break;
                                }
                                case TXT: {
                                    if (dataConvert.openSource(srcCls) > 0 && dataConvert.openDestination(destUrl) > 0) {
                                        rtn = this.convertVclsTo6xShp(dataConvert, srcCls, destUrl);
                                    }
                                    break;
                                }
                                case MAPGIS_6X_FILE: {
                                    SFeatureCls srcSfcls = (SFeatureCls) srcCls;
                                    if (srcSfcls != null) {
                                        String ext = XPath.getExtension(destUrl);
                                        switch (srcSfcls.getGeomType()) {
                                            case GeomPnt:
                                                if (ext != ".wt") {
                                                    destUrl += ".wt";
                                                }
                                                break;
                                            case GeomLin:
                                                if (ext != ".wl") {
                                                    destUrl += ".wl";
                                                }
                                                break;
                                            case GeomReg:
                                                if (ext != ".wp") {
                                                    destUrl += ".wp";
                                                }
                                                break;
                                            default:
                                                if (ext != ".wt") {
                                                    destUrl += ".wt";
                                                }
                                                break;
                                        }
                                    }
                                    if (dataConvert.openSource(srcCls) > 0 && dataConvert.openDestination(destUrl) > 0) {
                                        if (option.getSrsID() != -1) {
                                            dataConvert.setOption(ConvertOptionType.OPT_7XTO6XSRSID, option.getSrsID());
                                        }
                                        dataConvert.setOption(ConvertOptionType.OPT_6X_PROC_I64FLD, option.getFldInt64Operate());
                                        rtn = this.convertVclsTo6xShp(dataConvert, srcCls, destUrl);
                                    }
                                    break;
                                }
                                case TABLE_6X: {
                                    destUrl = destUrl.replace(CustomOperate.tableProName, "");
                                    rtn = this.convertVclsToTable(dataConvert, srcCls, destUrl, option, destDataType);
                                    break;
                                }
                                case TABLE_DBF:
                                case TABLE_EXCEL:
                                case TABLE_ACCESS:
                                    //case Table_TXT:
                                {
                                    rtn = this.convertVclsToTable(dataConvert, srcCls, destUrl, option, destDataType);
                                }
                            }

                            srcCls.close();
                            srcCls.dispose();
                        } else {
                            writeLog(combineMessage("打开源简单要素类", false));
                        }

                        if (srcDataBase != null) {
                            srcDataBase.close();
                            srcDataBase.dispose();
                        }
                        break;
                    }
                    case MAPGIS_ACLS: {
                        ObjectProperty<DataBase> srcDB = new SimpleObjectProperty<>();
                        AnnotationCls srcCls = (AnnotationCls) CustomOperate.openVectorCls(srcUrl, srcDB);
                        DataBase srcDataBase = srcDB.get();
                        if (srcCls != null) {
                            writeLog(combineMessage("打开源注记类", true));
                            switch (destDataType) {
                                case MAPGIS_ACLS: {
                                    ObjectProperty<DataBase> destDB = new SimpleObjectProperty<>();
                                    BooleanProperty isCreateProperty = new SimpleBooleanProperty(true);
                                    AnnotationCls destAcls = (AnnotationCls) CustomOperate.createVectorCls(destUrl, srcDataBase, srcCls, option.isAppendMode(), option.getSrsID(), destDB, isCreateProperty);
                                    DataBase destDataBase = destDB.get();
                                    boolean isCreate = isCreateProperty.get();
                                    if (destAcls != null && dataConvert.openSource(srcCls) > 0 && dataConvert.openDestination(destAcls) > 0) {
                                        if (option.isAppendMode()) {
                                            dataConvert.setAppendMode();
                                        }
                                        writeLog(combineMessage(isCreate ? "创建目的注记类" : "打开目的注记类", true));
                                        rtn = this.convertVclsToVcls(dataConvert, srcCls, destAcls);
                                        int sfclsID = destAcls.getClsID();
                                        destAcls.close();
                                        if (!rtn && destDataBase != null && isCreate) {
                                            AnnotationCls.remove(destDataBase, sfclsID);
                                        }
                                    } else {
                                        writeLog(combineMessage("创建目的注记类", false));
                                    }
                                    if (destAcls != null) {
                                        destAcls.dispose();
                                    }
                                    if (destDataBase != null) {
                                        destDataBase.close();
                                        destDataBase.dispose();
                                    }
                                    break;
                                }
                                case MAPGIS_OCLS: {
                                    ObjectProperty<DataBase> destDB = new SimpleObjectProperty<>();
                                    BooleanProperty isCreateProperty = new SimpleBooleanProperty(true);
                                    ObjectCls destCls = (ObjectCls) CustomOperate.createVectorCls(destUrl, srcCls, option.isAppendMode(), destDB, isCreateProperty);
                                    DataBase destDataBase = destDB.get();
                                    boolean isCreate = isCreateProperty.get();
                                    if (destCls != null && dataConvert.openSource(srcCls) > 0 && dataConvert.openDestination(destCls) > 0) {
                                        if (option.isAppendMode()) {
                                            dataConvert.setAppendMode();
                                        }
                                        writeLog(combineMessage(isCreate ? "创建目的对象类" : "打开目的对象类", true));
                                        rtn = this.convertVclsToVcls(dataConvert, srcCls, destCls);
                                        int sfclsID = destCls.getClsID();
                                        destCls.close();
                                        if (!rtn && destDataBase != null && isCreate) {
                                            ObjectCls.remove(destDataBase, sfclsID);
                                        }
                                    } else {
                                        writeLog(combineMessage("创建目的对象类", false));
                                    }
                                    if (destCls != null) {
                                        destCls.dispose();
                                    }
                                    if (destDataBase != null) {
                                        destDataBase.close();
                                        destDataBase.dispose();
                                    }
                                    break;
                                }
                                case VECTOR_DXF: {
                                    rtn = convertVclsToDxfDwg(dataConvert, srcUrl, destUrl, option);
                                    break;
                                }
                                case VECTOR_DWG: {
                                    rtn = convertVclsToDxfDwg(dataConvert, srcUrl, destUrl, option);
                                    break;
                                }
                                case VECTOR_E00:
                                case VECTOR_VCT:
                                case VECTOR_JSON:
                                case VECTOR_DGN: {
                                    if (dataConvert.openSource(srcCls) > 0 && dataConvert.openDestination(destUrl) > 0) {
                                        rtn = this.convertVclsTo6xShp(dataConvert, srcCls, destUrl);
                                    }
                                    break;
                                }
                                case VECTOR_MIF: {
                                    if (dataConvert.openSource(srcCls) > 0 && dataConvert.openDestination(destUrl) > 0) {
                                        if (!XString.isNullOrEmpty(option.getMifSymbolTable())) {
                                            dataConvert.setOption(ConvertOptionType.OPT_MIF_MAPFILE, option.getMifSymbolTable());
                                        }
                                        rtn = this.convertVclsTo6xShp(dataConvert, srcCls, destUrl);
                                    }
                                    break;
                                }
                                case ARCGIS_FILEGDB: {
                                    if (dataConvert.openSource(srcCls.getURL()) > 0 && dataConvert.openDestination(destUrl) > 0) {
                                        rtn = this.convertVclsTo6xShp(dataConvert, srcCls, destUrl);
                                    }
                                    break;
                                }
                                case ARCGIS_PERSONALGDB: {
                                    if (dataConvert.openSource(srcCls.getURL()) > 0 && dataConvert.openDestination(destUrl) > 0) {
                                        dataConvert.setOption(ConvertOptionType.OPT_PGDB_VERSION, option.getPgdbVersion());
                                        rtn = this.convertVclsTo6xShp(dataConvert, srcCls, destUrl);
                                    }
                                    break;
                                }
                                case TXT: {
                                    if (dataConvert.openSource(srcCls) > 0 && dataConvert.openDestination(destUrl) > 0) {
                                        rtn = this.convertVclsTo6xShp(dataConvert, srcCls, destUrl);
                                    }
                                    break;
                                }
                                case MAPGIS_6X_FILE: {
                                    if (XPath.getExtension(destUrl) != ".wt") {
                                        destUrl += ".wt";
                                    }
                                    if (dataConvert.openSource(srcCls) > 0 && dataConvert.openDestination(destUrl) > 0) {
                                        if (option.getSrsID() != -1) {
                                            dataConvert.setOption(ConvertOptionType.OPT_7XTO6XSRSID, option.getSrsID());
                                        }
                                        dataConvert.setOption(ConvertOptionType.OPT_6X_PROC_I64FLD, option.getFldInt64Operate());
                                        rtn = this.convertVclsTo6xShp(dataConvert, srcCls, destUrl);
                                    }
                                    break;
                                }
                                case TABLE_6X: {
                                    destUrl = destUrl.replace(CustomOperate.tableProName, "");
                                    rtn = this.convertVclsToTable(dataConvert, srcCls, destUrl, option, destDataType);
                                    break;
                                }
                                case TABLE_DBF:
                                case TABLE_EXCEL:
                                case TABLE_ACCESS:
                                    //case TABLE_TXT:
                                {
                                    rtn = this.convertVclsToTable(dataConvert, srcCls, destUrl, option, destDataType);
                                    break;
                                }
                            }
                            srcCls.close();
                            srcCls.dispose();
                        } else {
                            writeLog(combineMessage("打开源注记类", false));
                        }

                        if (srcDataBase != null) {
                            srcDataBase.close();
                            srcDataBase.dispose();
                        }
                        break;
                    }
                    case MAPGIS_OCLS: {
                        ObjectProperty<DataBase> srcDB = new SimpleObjectProperty<>();
                        ObjectCls srcCls = (ObjectCls) CustomOperate.openVectorCls(srcUrl, srcDB);
                        DataBase srcDataBase = srcDB.get();
                        if (srcCls != null) {
                            writeLog(combineMessage("打开源对象类", true));
                            switch (destDataType) {
                                case MAPGIS_OCLS: {
                                    ObjectProperty<DataBase> destDB = new SimpleObjectProperty<>();
                                    BooleanProperty isCreateProperty = new SimpleBooleanProperty(true);
                                    ObjectCls destCls = (ObjectCls) CustomOperate.createVectorCls(destUrl, srcCls, option.isAppendMode(), destDB, isCreateProperty);
                                    DataBase destDataBase = destDB.get();
                                    boolean isCreate = isCreateProperty.get();
                                    if (destCls != null && dataConvert.openSource(srcCls) > 0 && dataConvert.openDestination(destCls) > 0) {
                                        if (option.isAppendMode()) {
                                            dataConvert.setAppendMode();
                                        }
                                        writeLog(combineMessage(isCreate ? "创建目的对象类" : "打开目的对象类", true));
                                        rtn = this.convertVclsToVcls(dataConvert, srcCls, destCls);
                                        int sfclsID = destCls.getClsID();
                                        destCls.close();
                                        if (!rtn && destDataBase != null && isCreate) {
                                            ObjectCls.remove(destDataBase, sfclsID);
                                        }
                                    } else {
                                        writeLog(combineMessage("创建目的对象类", false));
                                    }
                                    if (destCls != null) {
                                        destCls.dispose();
                                    }
                                    if (destDataBase != null) {
                                        destDataBase.close();
                                        destDataBase.dispose();
                                    }
                                    break;
                                }
                                case ARCGIS_FILEGDB: {
                                    if (dataConvert.openSource(srcCls.getURL()) > 0 && dataConvert.openDestination(destUrl) > 0) {
                                        rtn = this.convertVclsTo6xShp(dataConvert, srcCls, destUrl);
                                    }
                                    break;
                                }
                                case ARCGIS_PERSONALGDB: {
                                    if (dataConvert.openSource(srcCls.getURL()) > 0 && dataConvert.openDestination(destUrl) > 0) {
                                        dataConvert.setOption(ConvertOptionType.OPT_PGDB_VERSION, option.getPgdbVersion());
                                        rtn = this.convertVclsTo6xShp(dataConvert, srcCls, destUrl);
                                    }
                                    break;
                                }
                                case TABLE_6X: {
                                    destUrl = destUrl.replace(CustomOperate.tableProName, "");
                                    rtn = this.convertVclsToTable(dataConvert, srcCls, destUrl, option, destDataType);
                                    break;
                                }
                                case TABLE_DBF:
                                case TABLE_EXCEL:
                                case TABLE_ACCESS:
                                    //case TABLE_TXT:
                                {
                                    rtn = this.convertVclsToTable(dataConvert, srcCls, destUrl, option, destDataType);
                                    break;
                                }
                            }

                            srcCls.close();
                            srcCls.dispose();
                        } else {
                            writeLog(combineMessage("打开源对象类", false));
                        }

                        if (srcDataBase != null) {
                            srcDataBase.close();
                            srcDataBase.dispose();
                        }
                        break;
                    }
                    case MAPGIS_RAS: {
                        if (destDataType == DataType.MAPGIS_RAS) {
                            rtn = convertRdsToRds(srcUrl, destUrl, "MAPGIS7MSI", option);
                        } else {
                            rtn = convertRdsToRasFile(srcUrl, destUrl, option.getDestDrive(), destDataType, option);
                        }
                        break;
                    }
                    case MAPGIS_RCAT: {
                        if (destDataType == DataType.MAPGIS_RCAT) {
                            rtn = convertRcatToRcat(srcUrl, destUrl);
                        }
                        break;
                    }
                    case MAPGIS_FDS:
                    case MAPGIS_GDB: {
                        if (destDataType == DataType.ARCGIS_FILEGDB) {
                            rtn = convertGDBToFileGDB(dataConvert, srcUrl, destUrl, option);
                        } else if (destDataType == DataType.ARCGIS_PERSONALGDB) {
                            rtn = convertGDBToPGDB(dataConvert, srcUrl, destUrl, option);
                        }
                        break;
                    }
                    case MAPGIS_6X_FILE:
                    case MAPGIS_6X_WT:
                    case MAPGIS_6X_WL:
                    case MAPGIS_6X_WP: {
                        if (dataConvert.openSource(srcUrl) > 0) {
                            switch (destDataType) {
                                case MAPGIS_SFCLS:
                                case MAPGIS_ACLS: {
                                    if (option.getClib6x() != null || option.getSlib6x() != null) {
                                        MapGisEnv6x gisEnv = new MapGisEnv6x(0);
                                        gisEnv.setClib(option.getClib6x());
                                        gisEnv.setSlib(option.getSlib6x());
                                        dataConvert.setOption(ConvertOptionType.OPT_GISENV6X, gisEnv);
                                    }
                                    rtn = this.convert6xToVcls(dataConvert, srcUrl, destUrl, option);
                                    break;
                                }
                                case MAPGIS_OCLS:
                                    rtn = this.convert6xToVcls(dataConvert, srcUrl, destUrl, option);
                                    break;
                            }
                        }
                        break;
                    }
                    case VECTOR_MIF: {
                        if (destDataType == DataType.MAPGIS_SFCLS && dataConvert.openSource(srcUrl) > 0) {
                            if (!XString.isNullOrEmpty(option.getMifSymbolTable())) {
                                dataConvert.setOption(ConvertOptionType.OPT_MIF_MAPFILE, option.getMifSymbolTable());
                            }
                            rtn = this.convertShpToVcls(dataConvert, destUrl, option);
                        }
                        break;
                    }
                    case VECTOR_SHP: {
                        if (destDataType == DataType.MAPGIS_SFCLS && dataConvert.openSource(srcUrl) > 0) {
                            rtn = this.convertShpToVclsEx(dataConvert, destUrl, option);
                        }
                        break;
                    }
                    case VECTOR_DXF:
                    case VECTOR_DWG: {
                        if (destDataType == DataType.MAPGIS_FDS || destDataType == DataType.MAPGIS_GDB) {
                            rtn = convertDwgTo7x(dataConvert, srcUrl, destUrl, option);
                        }
                        break;
                    }
                    case VECTOR_E00:
                    case VECTOR_VCT:
                    case VECTOR_GML:
                    case VECTOR_DGN:
                    case VECTOR_KML: {
                        if (destDataType == DataType.MAPGIS_SFCLS && dataConvert.openSource(srcUrl) > 0) {
                            rtn = this.convertShpToVcls(dataConvert, destUrl, option);
                        }
                        break;
                    }
                    case TXT: {
                        //Txt文件->简单要素类
                        if (destDataType == DataType.MAPGIS_SFCLS && dataConvert.openSource(srcUrl) > 0) {
                            rtn = this.convertTxtFileToVcls(dataConvert, srcUrl, destUrl, option);
                        }

                        //Txt表格->对象类
                        if (destDataType == DataType.MAPGIS_OCLS) {
                            rtn = this.convertTableToOcls(dataConvert, srcUrl, destUrl, option, srcDataType);
                        }
                        break;
                    }
                    case VECTOR_JSON: {
                        if (destDataType == DataType.MAPGIS_FDS || destDataType == DataType.MAPGIS_GDB) {
                            rtn = convertJsonTo7x(dataConvert, srcUrl, destUrl, option);
                        }
                        break;
                    }
                    case TABLE_6X: {
                        if (destDataType == DataType.MAPGIS_OCLS) {
                            srcUrl = srcUrl.replace(CustomOperate.tableProName, "");
                            rtn = this.convertTableToOcls(dataConvert, srcUrl, destUrl, option, srcDataType);
                        }
                        break;
                    }
                    case TABLE_EXCEL: {
                        if (destDataType == DataType.MAPGIS_OCLS) {
                            rtn = this.convertTableToOcls(dataConvert, srcUrl + "?Driver=Excel", destUrl, option, srcDataType);
                        }
                        break;
                    }
                    case TABLE_ACCESS: {
                        if (destDataType == DataType.MAPGIS_OCLS) {
                            rtn = this.convertTableToOcls(dataConvert, srcUrl + "?Driver=Access", destUrl, option, srcDataType);
                        }
                        break;
                    }
                    case TABLE_DBF: {
                        if (destDataType == DataType.MAPGIS_OCLS) {
                            rtn = this.convertTableToOcls(dataConvert, srcUrl, destUrl, option, srcDataType);
                        }
                        break;
                    }

                    case RASTER_6XDEM: {
                        if (destDataType == DataType.MAPGIS_RAS) {
                            rtn = convert6xdemToRas(srcUrl, destUrl, option);
                        }
                        break;
                    }
                    case RASTER_MSI:
                    case RASTER_TIFF:
                    case RASTER_IMG:
                    case RASTER_BMP:
                    case RASTER_JPG:
                    case RASTER_GIF:
                    case RASTER_JP2:
                    case RASTER_PNG:
                    case RASTER_HDF5: {
                        if (destDataType == DataType.MAPGIS_RAS) {
                            rtn = convertRasFileToRds(srcUrl, destUrl, "MAPGIS7MSI", srcDataType, option);
                        } else {
                            rtn = convertRasFileToRasFile(srcUrl, destUrl, option.getDestDrive(), option);
                        }
                        break;
                    }
                    case ARCINFO_COVERAGE: {
                        if (destDataType == DataType.MAPGIS_FDS) {
                            rtn = convertArcCoverageToFds(dataConvert, srcUrl, destUrl);
                        }
                        break;
                    }
                    case ARCGIS_FILEGDB: {
                        if (destDataType == DataType.MAPGIS_GDB) {
                            rtn = convertFileGDBTo7x(dataConvert, srcUrl, destUrl);
                        }
                        break;
                    }
                    case ARCGIS_PERSONALGDB: {
                        if (destDataType == DataType.MAPGIS_GDB) {
                            rtn = convertFileGDBTo7x(dataConvert, srcUrl, destUrl);
                        }
                        break;
                    }
//                    //增加三维数据支持 0414 ysp
//                    case Model_3DS:
//                    case Model_OBJ:
//                    case Model_DAE:
//                    case Model_OSGB:
//                    case Model_FBX:
//                    case Model_XML:
//                    case Model_X:
//                    {
//                        if (destDataType == DataType.MAPGIS_SFCLS)
//                        {
//                            //增加三维数据支持 0414 ysp
//                            GeoDataConv3D dataConvert3D = new GeoDataConv3D();
//                            rtn = convert3DFileToModel(dataConvert3D, srcUrl, destUrl,option);
//                            dataConvert3D.dispose();
//                        }
//                        break;
//                    }
//                    case Model_LAS:
//                    {
//                        if (destDataType == DataType.MAPGIS_SFCLS)
//                        {
//                            //增加三维数据支持 0414 ysp
//                            GeoDataConv3D dataConvert3D = new GeoDataConv3D();
//                            rtn = convertPointCloudFileToModel(dataConvert3D, srcUrl, destUrl,option);
//                            dataConvert3D.dispose();
//                        }
//                        break;
//                    }
                }
                dataConvert.dispose();
            }
            return rtn;
        }

        /**
         * 数据迁移(矢量类->矢量类)
         *
         * @param dc
         * @param srcVcls
         * @param destVcls
         * @return
         */
        private boolean convertVclsToVcls(DataConvert dc, IVectorCls srcVcls, IVectorCls destVcls) {
            boolean rtn = false;
            registerCallback(dc);
            writeLog("开始转换");
            rtn = dc.convert() > 0;
            writeLog(combineMessage("结束转换", rtn));
            dc.close();
            return rtn;
        }

        /**
         * 矢量类(简单要素类、注记类)->Dxf、Dwg文件
         *
         * @param dc
         * @param srcUrl
         * @param destUrl
         * @param option
         * @return
         */
        private boolean convertVclsToDxfDwg(DataConvert dc, String srcUrl, String destUrl, ConvertOption option) {
            dc.openSource(srcUrl);
            dc.openDestination(destUrl);
            if (option.isAppendMode()) {
                File file = new File(destUrl);
                if (file.exists()) {
                    dc.setAppendMode();
                }
            }
            dc.setOption(ConvertOptionType.OPT_CAD_VERSION, option.getDwgVersion());
            if (!XString.isNullOrEmpty(option.getDxfSymbolTable())) {
                dc.setOption(ConvertOptionType.OPT_DXF_MAPFILE, option.getDxfSymbolTable());
            }
            registerCallback(dc);
            return dc.convert() > 0;
        }

        /**
         * 数据下载(矢量类->6x文件、SHP文件等)
         *
         * @param dc
         * @param srcVcls
         * @param destFileUrl
         * @return
         */
        private boolean convertVclsTo6xShp(DataConvert dc, IVectorCls srcVcls, String destFileUrl) {
            boolean rtn = false;
            registerCallback(dc);
            writeLog("开始转换");
            rtn = dc.convert() > 0;
            writeLog(combineMessage("结束转换", rtn));
            dc.close();
            return rtn;
        }

        /**
         * 数据上载(6x文件->矢量类)
         *
         * @param dc
         * @param srcFile
         * @param destUrl
         * @param option
         * @return
         */
        private boolean convert6xToVcls(DataConvert dc, String srcFile, String destUrl, ConvertOption option) {
            boolean rtn = false;
            GeomType geomType = GeomType.GeomUnknown;
            switch (XPath.getExtension(srcFile)) {
                case ".wt":
                    geomType = GeomType.GeomPnt;
                    break;
                case ".wp":
                    geomType = GeomType.GeomReg;
                    break;
                case ".wl":
                    geomType = GeomType.GeomLin;
                    break;
            }
            if (geomType != GeomType.GeomUnknown) {
                String clsMsg = XClsType.XACls.equals(URLParse.getXClsType(destUrl)) ? "注记类" : LanguageConvert.geomTypeConvert(geomType) + "简单要素类";
                ObjectProperty<DataBase> dbProperty = new SimpleObjectProperty<>();
                BooleanProperty isCreateProperty = new SimpleBooleanProperty(true);
                IVectorCls destVectCls = CustomOperate.createVectorCls(destUrl, geomType, null, option.isAppendMode(), dbProperty, isCreateProperty);
                DataBase db = dbProperty.get();
                boolean isCreate = isCreateProperty.get();
                if (destVectCls != null) {
                    registerCallback(dc);
                    writeLog(combineMessage(String.format("%s目的%s", isCreate ? "创建" : "打开", clsMsg), true));
                    dc.openDestination(destVectCls);
                    writeLog("开始转换");
                    // 修改说明：6x数据追加到sfcls中，追加模式下，不会处理属性结构（此处与shp追加到sfcls中不同），因此如果是创建出的sfcls，则不设置追加模式，已与平1人员讨论，上层暂时这么修改，等待底层人员确定修改方案
                    // 修改人：周小飞 2014-08-15
                    if (!isCreate && option.isAppendMode()) {
                        dc.setAppendMode();
                    }
                    rtn = dc.convert() > 0;
                    writeLog(combineMessage("结束转换", rtn));
                    dc.close();
                    if (rtn) {
                        if (option != null && option.getSrsID() != -1) {
                            destVectCls.setsrID(option.getSrsID());
                        }
                        int sfclsId = destVectCls.getClsID();
                        long count = destVectCls.getObjCount();
                        destVectCls.close();
                        if (count <= 0 && !option.isKeepNullData()) {
                            boolean delRtn = SFeatureCls.remove(db, sfclsId);
                            writeLog(combineMessage("目的数据为空，且用户并未保留空数据。因此删除目的" + clsMsg, delRtn));
                        }
                    } else//失败则删除创建的类
                    {
                        int sfclsId = destVectCls.getClsID();
                        destVectCls.close();
                        if (isCreate) {
                            boolean delRtn = SFeatureCls.remove(db, sfclsId);
                            writeLog(combineMessage("删除目的" + clsMsg, delRtn));
                        }
                    }
                    //region 6x点转为简单要素类或注记类时，再次转换6x点到注记类或简单要素类
                    if (geomType == GeomType.GeomPnt) {
                        if (destVectCls instanceof SFeatureCls) {
                            String anclsUrl = destUrl.replace("/sfcls/", "/acls/");

                            ObjectProperty<DataBase> anclsGDBProperty = new SimpleObjectProperty<>();
                            BooleanProperty isCreateAnclsProperty = new SimpleBooleanProperty(true);
                            IVectorCls ancls = CustomOperate.createVectorCls(anclsUrl, geomType, null, option.isAppendMode(), anclsGDBProperty, isCreateAnclsProperty);
                            DataBase anclsGDB = anclsGDBProperty.get();
                            boolean isCreateAncls = isCreateAnclsProperty.get();
                            if (ancls != null) {
                                writeLog(combineMessage(isCreateAncls ? "创建目的注记类" : "打开目的注记类", true));
                                DataConvert dataConvert = new DataConvert();
                                dataConvert.openSource(srcFile);
                                dataConvert.openDestination(ancls);
                                if (option.getClib6x() != null || option.getSlib6x() != null) {
                                    MapGisEnv6x gisEnv = new MapGisEnv6x(0);
                                    gisEnv.setClib(option.getClib6x());
                                    gisEnv.setSlib(option.getSlib6x());
                                    dataConvert.setOption(ConvertOptionType.OPT_GISENV6X, gisEnv);
                                }
                                registerCallback(dataConvert);
                                if (!isCreateAncls && option.isAppendMode()) {
                                    dataConvert.setAppendMode();
                                }
                                writeLog("开始转换");
                                boolean rtnAncls = dataConvert.convert() > 0;
                                writeLog(combineMessage("结束转换", rtnAncls));
                                dataConvert.close();
                                dataConvert.dispose();
                                if (rtnAncls) {
                                    if (option != null && option.getSrsID() != -1)
                                        ancls.setsrID(option.getSrsID());
                                    long rcdCount = ancls.getObjCount();
                                    int anclsId = ancls.getClsID();
                                    ancls.close();
                                    if (rcdCount <= 0 && isCreateAncls)//不包含注记类删除
                                    {
                                        boolean delRtn = AnnotationCls.remove(anclsGDB, anclsId);
                                        writeLog(combineMessage("点文件未包含注记信息，删除目的注记类。", delRtn));
                                    }
                                } else//失败则删除创建的类
                                {
                                    int anclsId = ancls.getClsID();
                                    ancls.close();
                                    if (isCreateAncls) {
                                        boolean delRtn = AnnotationCls.remove(anclsGDB, anclsId);
                                        writeLog(combineMessage("删除目的注记类", delRtn));
                                    }
                                }
                            } else
                                writeLog(combineMessage("创建目的注记类", false));

                            if (anclsGDB != null) {
                                anclsGDB.close();
                                anclsGDB.dispose();
                            }
                        } else if (destVectCls instanceof AnnotationCls) {
                            String pntSfclsUrl = destUrl.replace("/acls/", "/sfcls/");
                            ObjectProperty<DataBase> sfclsGDBProperty = new SimpleObjectProperty<>();
                            BooleanProperty isCreateSfclsProperty = new SimpleBooleanProperty(true);
                            IVectorCls sfcls = CustomOperate.createVectorCls(pntSfclsUrl, geomType, null, option.isAppendMode(), sfclsGDBProperty, isCreateSfclsProperty);
                            DataBase sfclsGDB = sfclsGDBProperty.get();
                            boolean isCreateSfcls = isCreateSfclsProperty.get();
                            if (sfcls != null) {
                                writeLog(combineMessage(isCreateSfcls ? "创建目的点简单要素类" : "打开目的点简单要素类", true));
                                DataConvert dataConvert = new DataConvert();
                                dataConvert.openSource(srcFile);
                                dataConvert.openDestination(sfcls);
                                if (option.getClib6x() != null || option.getSlib6x() != null) {
                                    MapGisEnv6x gisEnv = new MapGisEnv6x(0);
                                    gisEnv.setClib(option.getClib6x());
                                    gisEnv.setSlib(option.getSlib6x());
                                    dataConvert.setOption(ConvertOptionType.OPT_GISENV6X, gisEnv);
                                }
                                registerCallback(dataConvert);
                                if (!isCreateSfcls && option.isAppendMode()) {
                                    dataConvert.setAppendMode();
                                }
                                writeLog("开始转换");
                                boolean rtnAncls = dataConvert.convert() > 0;
                                writeLog(combineMessage("结束转换", rtnAncls));
                                dataConvert.close();
                                dataConvert.dispose();
                                if (rtnAncls) {
                                    if (option != null && option.getSrsID() != -1) {
                                        sfcls.setsrID(option.getSrsID());
                                    }
                                    long rcdCount = sfcls.getObjCount();
                                    int anclsId = sfcls.getClsID();
                                    sfcls.close();
                                    if (rcdCount <= 0 && isCreateSfcls)//不包含点简单要素类删除
                                    {
                                        boolean delRtn = AnnotationCls.remove(sfclsGDB, anclsId);
                                        writeLog(combineMessage("点文件未包含点信息，删除目的点简单要素类。", delRtn));
                                    }
                                } else//失败则删除创建的类
                                {
                                    int anclsId = sfcls.getClsID();
                                    sfcls.close();
                                    if (isCreateSfcls) {
                                        boolean delRtn = AnnotationCls.remove(sfclsGDB, anclsId);
                                        writeLog(combineMessage("删除目的点简单要素类", delRtn));
                                    }
                                }
                            } else {
                                writeLog(combineMessage("创建目的点简单要素类", false));
                            }

                            if (sfclsGDB != null) {
                                sfclsGDB.close();
                                sfclsGDB.dispose();
                            }
                        }
                    }
                    //endregion
                } else {
                    writeLog(combineMessage("创建目的" + clsMsg, false));
                }

                if (db != null) {
                    db.close();
                    db.dispose();
                }
            } else {
                writeLog(combineMessage("创建目的简单要素类失败，未知几何类型。", false));
            }
            return rtn;
        }

        /**
         * 数据上载(Dxf、Mif、E00文件等->矢量类)
         *
         * @param dc
         * @param destUrl
         * @param option
         * @return
         */
        private boolean convertShpToVcls(DataConvert dc, String destUrl, ConvertOption option) {
            boolean rtn = false;

            ObjectProperty<IVectorCls[]> clses = new SimpleObjectProperty<>();
            ObjectProperty<boolean[]> isCreates = new SimpleObjectProperty<>();
            ObjectProperty<DataBase> dbProperty = new SimpleObjectProperty<>();
            boolean createRtn = CustomOperate.create7xCls(destUrl, option.isAppendMode(), clses, isCreates, dbProperty);
            DataBase db = dbProperty.get();
            if (createRtn) {
                SFeatureCls sfclsPnt = (SFeatureCls) clses.get()[0];
                SFeatureCls sfclsLin = (SFeatureCls) clses.get()[1];
                SFeatureCls sfclsReg = (SFeatureCls) clses.get()[2];
                AnnotationCls annCls = (AnnotationCls) clses.get()[3];
                boolean isCreatePnt = isCreates.get()[0];
                boolean isCreateLin = isCreates.get()[1];
                boolean isCreateReg = isCreates.get()[2];
                boolean isCreateAnn = isCreates.get()[3];

                registerCallback(dc);
                writeLog(combineMessage("创建目的点、线、区、注记矢量类", true));
                dc.openDestination(sfclsPnt, sfclsLin, sfclsReg, annCls);
                if (option.isAppendMode()) {
                    dc.setAppendMode();
                }
                writeLog("开始转换");
                rtn = dc.convert() > 0;
                writeLog(combineMessage("结束转换", rtn));
                dc.close();
                if (rtn) {
                    int id1 = sfclsPnt.getClsID(), id2 = sfclsLin.getClsID(), id3 = sfclsReg.getClsID(), id4 = annCls.getClsID();
                    long count1 = sfclsPnt.getObjCount(), count2 = sfclsLin.getObjCount(), count3 = sfclsReg.getObjCount(), count4 = annCls.getObjCount();
                    sfclsPnt.close();
                    sfclsLin.close();
                    sfclsReg.close();
                    annCls.close();
                    if (count1 == 0 && !option.isKeepNullData() && isCreatePnt) {
                        boolean delRtn = SFeatureCls.remove(db, id1);
                        writeLog(combineMessage("目的点数据为空且用户并未保留空数据，因此删除点简单要素类。", delRtn));
                    }
                    if (count2 == 0 && !option.isKeepNullData() && isCreateLin) {
                        boolean delRtn = SFeatureCls.remove(db, id2);
                        writeLog(combineMessage("目的线数据为空且用户并未保留空数据，因此删除点简单要素类。", delRtn));
                    }
                    if (count3 == 0 && !option.isKeepNullData() && isCreateReg) {
                        boolean delRtn = SFeatureCls.remove(db, id3);
                        writeLog(combineMessage("目的区数据为空且用户并未保留空数据，因此删除点简单要素类。", delRtn));
                    }
                    if (count4 == 0 && !option.isKeepNullData() && isCreateAnn) {
                        boolean delRtn = AnnotationCls.remove(db, id4);
                        writeLog(combineMessage("目的注记数据为空且用户并未保留空数据，因此删除点简单要素类。", delRtn));
                    }
                } else//失败则删除创建的类
                {
                    int id1 = sfclsPnt.getClsID(), id2 = sfclsLin.getClsID(), id3 = sfclsReg.getClsID(), id4 = annCls.getClsID();
                    sfclsPnt.close();
                    sfclsLin.close();
                    sfclsReg.close();
                    annCls.close();
                    boolean delRtn = false;
                    if (isCreatePnt) {
                        delRtn = SFeatureCls.remove(db, id1);
                        writeLog(combineMessage("删除目的点简单要素类", delRtn));
                    }
                    if (isCreateLin) {
                        delRtn = SFeatureCls.remove(db, id2);
                        writeLog(combineMessage("删除目的线简单要素类", delRtn));
                    }
                    if (isCreateReg) {
                        delRtn = SFeatureCls.remove(db, id3);
                        writeLog(combineMessage("删除目的点区简单要素类", delRtn));
                    }
                    if (isCreateAnn) {
                        delRtn = AnnotationCls.remove(db, id4);
                        writeLog(combineMessage("删除目的注记类", delRtn));
                    }
                }

                if (sfclsPnt != null) {
                    sfclsPnt.dispose();
                }
                if (sfclsLin != null) {
                    sfclsLin.dispose();
                }
                if (sfclsReg != null) {
                    sfclsReg.dispose();
                }
                if (annCls != null) {
                    annCls.dispose();
                }
            } else {
                writeLog(combineMessage("创建目的点、线、区、注记矢量类", false));
            }

            if (db != null) {
                db.close();
                db.dispose();
            }
            return rtn;
        }

        /**
         * 数据上载(SHP文件->矢量类)
         *
         * @param dc
         * @param destUrl
         * @param option
         * @return
         */
        private boolean convertShpToVclsEx(DataConvert dc, String destUrl, ConvertOption option) {
            boolean rtn = false;
            FeatureInfo feaInfo = new FeatureInfo();
            dc.getSourceInfo(SourceInfoType.FEATURE_INFO, feaInfo);
            ObjectProperty<DataBase> dbProperty = new SimpleObjectProperty<>();
            BooleanProperty isProperty = new SimpleBooleanProperty(true);
            IVectorCls destVectCls = CustomOperate.createVectorCls(destUrl, feaInfo.geomType(), null, option.isAppendMode(), dbProperty, isProperty);
            DataBase db = dbProperty.get();
            boolean isCreate = isProperty.get();
            if (destVectCls != null) {
                registerCallback(dc);
                writeLog(combineMessage("创建目的简单要素类", true));
                dc.openDestination(destVectCls);
                if (option.isAppendMode())
                    dc.setAppendMode();
                writeLog("开始转换");
                rtn = dc.convert() > 0;
                writeLog(combineMessage("结束转换", rtn));
                dc.close();
                if (rtn) {
                    int id = destVectCls.getClsID();
                    long count = destVectCls.getObjCount();
                    destVectCls.close();
                    if (count <= 0 && !option.isKeepNullData() && isCreate) {
                        boolean delRtn = SFeatureCls.remove(db, id);
                        writeLog(combineMessage("目的数据为空，且用户并未保留空数据。因此删除目的" + "简单要素类", delRtn));
                    }
                } else//失败则删除创建的类
                {
                    int id1 = destVectCls.getClsID();
                    destVectCls.close();
                    if (isCreate) {
                        boolean delRtn = SFeatureCls.remove(db, id1);
                        writeLog(combineMessage("删除目的" + "简单要素类", delRtn));
                    }
                }
            } else {
                writeLog(combineMessage("创建目的简单要素类失败，未知几何类型。", false));
            }

            if (db != null) {
                db.close();
                db.dispose();
            }
            return rtn;
        }

        /**
         * 数据上载(Txt文件->矢量类)
         *
         * @param dc
         * @param srcFile
         * @param destUrl
         * @param option
         * @return
         */
        private boolean convertTxtFileToVcls(DataConvert dc, String srcFile, String destUrl, ConvertOption option) {
            boolean rtn = false;
            //未封装(Txt27xParam)
            //Txt27xParamStruct param = option.getTxt27xParam();
            //if (param != null)
            //{
            //    param.strSrcPath = srcFile;
            //    //boolean genLine = param.genLineStatus == 1;//生成线标识(当生成线时，创建的点简单要素类无意义(底层必须一个点简单要素类)
            //    boolean genLine = true;
            //
            //    ObjectProperty<DataBase> dbProperty = new SimpleObjectProperty<>();
            //    BooleanProperty isCreatePnt = new SimpleBooleanProperty(true);
            //    BooleanProperty isCreateLin = new SimpleBooleanProperty(true);
            //    SFeatureCls destClsPnt = (SFeatureCls) CustomOperate.createVectorCls(genLine ? (destUrl + UUID.randomUUID().toString()) : destUrl, GeomType.GeomPnt, null, (genLine ? false : option.isAppendMode()), dbProperty, isCreatePnt);
            //    SFeatureCls destClsLin = !genLine ? null : (SFeatureCls) CustomOperate.createVectorCls(destUrl, GeomType.GeomLin, null, option.isAppendMode(), dbProperty, isCreateLin);
            //    DataBase db = dbProperty.get();
            //    if ((!genLine && destClsPnt != null) || (genLine && destClsPnt != null && destClsLin != null))
            //    {
            //        registerCallback(dc);
            //        boolean showCreateMsg = (!genLine && destClsPnt != null) ? isCreatePnt.get() : isCreateLin.get();
            //         writeLog(combineMessage(showCreateMsg ? "创建目的简单要素类" : "打开目的简单要素类", true));
            //
            //        dc.openDestination(destClsPnt, destClsLin, null, null);
            //        if (option.isAppendMode())
            //        {
            //            dc.setAppendMode();
            //        }
            //        //dc.setOption(ConvertOptionType.OPT_TXT_PARAM, param);//未封装
            //         writeLog("开始转换");
            //        rtn = dc.convert() > 0;
            //         writeLog(combineMessage("结束转换", rtn));
            //        dc.close();
            //        if (rtn)
            //        {
            //            if (genLine)
            //            {
            //                int pntId = destClsPnt.getClsID();
            //                destClsPnt.close();
            //                destClsLin.close();
            //                SFeatureCls.remove(db, pntId);
            //            } else
            //            {
            //                destClsPnt.close();
            //            }
            //        } else
            //        {
            //            if (genLine)
            //            {
            //                int pntId = destClsPnt.getClsID();
            //                int linId = destClsLin.getClsID();
            //
            //                destClsPnt.close();
            //                destClsLin.close();
            //
            //                SFeatureCls.remove(db, pntId);
            //                if (isCreateLin.get())
            //                {
            //                    boolean delRtn = SFeatureCls.remove(db, linId);
            //                     writeLog(combineMessage("删除目的" + "简单要素类", delRtn));
            //                }
            //            } else
            //            {
            //                int sfclsId = destClsPnt.getClsID();
            //                destClsPnt.close();
            //                if (isCreatePnt.get())
            //                {
            //                    boolean delRtn = SFeatureCls.remove(db, sfclsId);
            //                     writeLog(combineMessage("删除目的" + "简单要素类", delRtn));
            //                }
            //            }
            //        }
            //    } else
            //    {
            //        if (genLine)
            //        {
            //            if (destClsPnt != null)
            //            {
            //                int pntId = destClsPnt.getClsID();
            //                destClsPnt.close();
            //                SFeatureCls.remove(db, pntId);
            //            }
            //            if (destClsLin != null)
            //            {
            //                int linId = destClsLin.getClsID();
            //                destClsLin.close();
            //                if (isCreateLin.get())
            //                {
            //                    SFeatureCls.remove(db, linId);
            //                }
            //            }
            //        }
            //         writeLog(combineMessage("创建目的简单要素类", false));
            //    }
            //
            //    if (destClsPnt != null)
            //    {
            //        destClsPnt.dispose();
            //    }
            //    if (destClsLin != null)
            //    {
            //        destClsLin.dispose();
            //    }
            //    if (db != null)
            //    {
            //        db.close();
            //        db.dispose();
            //    }
            //} else
            //{
            //     writeLog(combineMessage("未设置导入txt参数.", false));
            //    ////以下为设置默认的txt转sfcls参数(默认生成点)
            //    //param = new Txt27xParamStruct();
            //    //param.strSrcPath = srcFile;
            //    //param.readType = 2;
            //    //param.genLineStatus = 0;
            //    //param.startLine = 1;
            //    //
            //    //SeparatorStru separatorStru = param.sepStru;
            //    //separatorStru.comma = true;
            //    //separatorStru.suc = true;
            //    //param.sepStru = separatorStru;
            //    //param.separator = CustomOperate.getSeparatorString(separatorStru);
            //    //
            //    //File file = new File(srcFile);
            //    //if (file.exists())
            //    //{
            //    //    String firstLineStr = "";
            //    //    BufferedReader reader = null;
            //    //    StringBuffer sbf = new StringBuffer();
            //    //    try
            //    //    {
            //    //        reader = new BufferedReader(new FileReader(file));
            //    //        firstLineStr = reader.readLine().trim();
            //    //        reader.close();
            //    //    } catch (IOException e)
            //    //    {
            //    //        e.printStackTrace();
            //    //    } finally
            //    //    {
            //    //        if (reader != null)
            //    //        {
            //    //            try
            //    //            {
            //    //                reader.close();
            //    //            } catch (IOException e1)
            //    //            {
            //    //                e1.printStackTrace();
            //    //            }
            //    //        }
            //    //    }
            //    //    if (!XString.isNullOrEmpty(firstLineStr))
            //    //    {
            //    //        List<String> fieldNames = new ArrayList<>();
            //    //        List<Integer> fieldTypes = new ArrayList<>();
            //    //        String[] stres = CustomOperate.getSeparatorStringArray(firstLineStr, separatorStru);
            //    //        for (int i = 0; i < stres.length; i++)
            //    //        {
            //    //            fieldNames.add(stres[i]);
            //    //            fieldTypes.add(FieldType.fldStr.value());
            //    //        }
            //    //        param.attNameNum = 0;
            //    //        param.fieldNameArr = fieldNames;
            //    //        param.fieldTypeArr = fieldTypes;
            //    //        param.lineAttPosNum = 1;
            //    //    }
            //    //}
            //    //option.setTxt27xParam(param);
            //}
            return rtn;
        }

        /**
         * 数据下载(矢量类->表格文件)
         *
         * @param dc
         * @param srcVcls
         * @param destFileUrl
         * @param option
         * @param destDataType
         * @return
         */
        private boolean convertVclsToTable(DataConvert dc, IVectorCls srcVcls, String destFileUrl, ConvertOption option, DataType destDataType) {
            boolean rtn = false;
            dc.setCopyMode();
            if (dc.openSource(srcVcls) > 0 && dc.openDestination(destFileUrl) > 0) {
                //未封装（TableInfo）
                //TableInfo tableInfo = new TableInfo();
                //dc.setOption(ConvertOptionType.OPT_CONVTABLE, tableInfo);
                //dc.getDestInfo(DestInfoType.CONVERT_MPDOPT_CONVTABLE, tableInfo);
                //switch (destDataType)
                //{
                //    case TABLE_EXCEL:
                //    {
                //        if (option.tableInfoExcel != null)
                //        {
                //            dc.setOption(ConvertOptionType.OPT_CONVTABLE_SETFLD, option.tableInfoExcel);
                //        }
                //        break;
                //    }
                //    case TABLE_ACCESS:
                //    {
                //        if (option.tableInfoAccess != null)
                //        {
                //            dc.setOption(ConvertOptionType.OPT_CONVTABLE_SETFLD, option.tableInfoAccess);
                //        }
                //    }
                //    break;
                //    case TABLE_DBF:
                //    {
                //        if (option.tableInfoDbf != null)
                //        {
                //            dc.setOption(ConvertOptionType.OPT_CONVTABLE_SETFLD, option.tableInfoDbf);
                //        }
                //    }
                //    break;
                //    case TXT:
                //    {
                //        if (option.tableInfoTxt != null)
                //        {
                //            dc.setOption(ConvertOptionType.OPT_CONVTABLE_SETFLD, option.tableInfoTxt);
                //        }
                //    }
                //    break;
                //}
                registerCallback(dc);
                writeLog("开始转换");
                rtn = dc.convert() > 0;
                writeLog(combineMessage("结束转换", rtn));
                dc.close();
            }
            return rtn;
        }

        /**
         * 数据上载(表格文件->对象类)
         *
         * @param dc
         * @param tableFile
         * @param destUrl
         * @param option
         * @param srcDataType
         * @return
         */
        private boolean convertTableToOcls(DataConvert dc, String tableFile, String destUrl, ConvertOption option, DataType srcDataType) {
            boolean rtn = false;
            registerCallback(dc);
            dc.setCopyMode();
            dc.openSource(tableFile);
            ObjectProperty<DataBase> dbProperty = new SimpleObjectProperty<>();
            BooleanProperty isCreate = new SimpleBooleanProperty(true);
            ObjectCls ocls = (ObjectCls) CustomOperate.createVectorCls(destUrl, GeomType.GeomUnknown, null, true, dbProperty, isCreate);//存在则打开，不存在则创建
            DataBase db = dbProperty.get();
            if (db != null) {
                if (ocls != null) {
                    dc.openDestination(ocls);
                    //未封装（TableInfo）
                    //TableInfo tableInfo = new TableInfo();
                    //if (srcDataType == DataType.TXT && option != null && option.getTableInfoOcls() != null)
                    //{
                    //    tableInfo.sepNo = option.getTableInfoOcls().sepNo;
                    //}
                    //if (option.isAppendMode())
                    //{
                    //    dc.setAppendMode();
                    //}
                    //dc.setOption(ConvertOptionType.OPT_CONVTABLE, tableInfo);
                    //dc.getSourceInfo(SourceInfoType.CONVERT_MPDOPT_CONVTABLE, tableInfo);
                    //switch (srcDataType)
                    //{
                    //    case TABLE_ACCESS:
                    //    case TABLE_EXCEL:
                    //    case TABLE_DBF:
                    //    case TXT:
                    //    {
                    //        if (option != null && option.getTableInfoOcls() != null)
                    //        {
                    //            dc.setOption(ConvertOptionType.OPT_CONVTABLE_SETFLD, option.getTableInfoOcls());
                    //        }
                    //    }
                    //    break;
                    //}

                    writeLog("开始转换");
                    rtn = dc.convert() > 0;
                    writeLog(combineMessage("结束转换", rtn));
                    dc.close();

                    int id = ocls.getClsID();
                    ocls.close();
                    ocls.dispose();

                    if (!rtn && isCreate.get()) {
                        ObjectCls.remove(db, id);
                    }
                }
                db.close();
                db.dispose();
            }
            return rtn;
        }

        /**
         * 栅格数据集->栅格数据集
         *
         * @param srcUrl
         * @param destUrl
         * @param desFormat
         * @param option
         * @return
         */
        private boolean convertRdsToRds(String srcUrl, String destUrl, String desFormat, ConvertOption option) {
            boolean rtn = false;
            RasTrans rasTrans = new RasTrans();
            rasTrans.setTransPara(option.getRasTrans());
            rtn = rasTrans.rsImgTrans(srcUrl, destUrl, desFormat) > 0;
            if (rtn) {
                updateProgress(1, 1);
                buildPyramid(destUrl);
            }
            return rtn;
        }

        /**
         * 栅格数据集->栅格文件
         *
         * @param srcUrl
         * @param destUrl
         * @param desFormat
         * @param destDataType
         * @param option
         * @return
         */
        private boolean convertRdsToRasFile(String srcUrl, String destUrl, String desFormat, DataType destDataType, ConvertOption option) {
            boolean rtn = false;
            String fileUrl = destUrl.replace(CustomOperate.demProName, "");
            if (!fileUrl.toLowerCase().startsWith(CustomOperate.fileProName)) {
                fileUrl = CustomOperate.fileProName + fileUrl;
            }

            RasTrans rasTrans = new RasTrans();
            rasTrans.setTransPara(option.getRasTrans());
            switch (destDataType)//Dem数据特殊处理
            {
                case TXT:
                    rtn = rasTrans.convertRasDataSetToArcGrid(srcUrl, fileUrl) > 0;
                    break;
                case DEM_BIL:
                    rtn = rasTrans.convertRasDataSetToBil(srcUrl, fileUrl) > 0;
                    break;
                case DEM_GRD:
                    rtn = rasTrans.convertRasDataSetToGrd(srcUrl, fileUrl) > 0;
                    break;
                case RASTER_6XDEM:
                    rtn = rasTrans.convertRasDataSetToDem(srcUrl, fileUrl) > 0;
                    break;
                default:
                    rtn = rasTrans.rsImgTrans(srcUrl, destUrl, desFormat) > 0;
                    break;
            }
            if (rtn) {
                updateProgress(1, 1);
                buildPyramid(destUrl);
            }
            return rtn;
        }

        /**
         * 栅格文件->栅格数据集
         *
         * @param srcUrl
         * @param destUrl
         * @param desFormat
         * @param srcDataType
         * @param option
         * @return
         */
        private boolean convertRasFileToRds(String srcUrl, String destUrl, String desFormat, DataType srcDataType, ConvertOption option) {
            boolean rtn = false;
            RasTrans rasTrans = new RasTrans();
            rasTrans.setTransPara(option.getRasTrans());

            String srcFile = srcUrl.replace(CustomOperate.demProName, "");
            if (!srcFile.toLowerCase().startsWith(CustomOperate.fileProName)) {
                srcFile = CustomOperate.fileProName + srcFile;
            }
            switch (srcDataType)//Dem数据特殊处理
            {
                case TXT:
                    MSRSDataType msiDataType = option.isTxtDemToRas_DataType() ? MSRSDataType.MS_Int32 : MSRSDataType.MS_Float32;
                    rtn = rasTrans.convertArcGridToRasDataSet(srcFile, destUrl) > 0;
                    break;
                case DEM_ADF:
                    rtn = rasTrans.convertArcDEMToRasDataSet(srcFile, destUrl) > 0;
                    break;
                case DEM_GRD:
                    rtn = rasTrans.convertGrdToRasDataSet(srcFile, destUrl) > 0;
                    break;
                case DEM_BIL:
                    rtn = rasTrans.convertBilToRasDataSet(srcFile, /*option.m_BilDemToRas_Option,*/ destUrl) > 0;
                    break;
                case RASTER_6XDEM:
                    rtn = rasTrans.convertDemToRasDataSet(srcFile, destUrl) > 0;
                    break;
                default:
                    rtn = rasTrans.rsImgTrans(srcUrl, destUrl, desFormat) > 0;
                    break;
            }
            if (rtn) {
                updateProgress(1, 1);
                buildPyramid(destUrl);
            }
            return rtn;
        }

        /**
         * 栅格文件->栅格文件
         *
         * @param srcUrl
         * @param destUrl
         * @param desFormat
         * @param option
         * @return
         */
        private boolean convertRasFileToRasFile(String srcUrl, String destUrl, String desFormat, ConvertOption option) {
            boolean rtn = false;
            RasTrans rasTrans = new RasTrans();
            rasTrans.setTransPara(option.getRasTrans());
            rtn = rasTrans.rsImgTrans(srcUrl, destUrl, desFormat) > 0;
            if (rtn) {
                updateProgress(1, 1);
                buildPyramid(destUrl);
            }
            return rtn;
        }

        /**
         * 栅格目录->栅格目录
         *
         * @param srcUrl
         * @param destUrl
         * @return
         */
        private boolean convertRcatToRcat(String srcUrl, String destUrl) {
            boolean rtn = false;
            RasterCatalog rc = new RasterCatalog();
            if (rc.openByURL(srcUrl) > 0) {
                StringProperty name = new SimpleStringProperty();
                DataBase db = URLParse.openDataBase(destUrl, name);
                String destRcatName = name.get();
                if (db != null) {
                    rtn = rc.saveCatalogAs(db, destRcatName) > 0;
                    if (rtn) {
                        updateProgress(1, 1);
                    }
                    db.close();
                }
                rc.close();
            } else {
                //String errorMsg = getLastError//未封装
                writeLog(combineMessage("打开源栅格目录", false));
            }
            return rtn;
        }

        /**
         * 6x Dem库数据->栅格数据集
         *
         * @param srcUrl
         * @param destUrl
         * @param option
         * @return
         */
        private boolean convert6xdemToRas(String srcUrl, String destUrl, ConvertOption option) {
            RasTrans rasTrans = new RasTrans();
            rasTrans.setTransPara(option.getRasTrans());
            boolean rtn = rasTrans.convert6xDemToRasDataSet(srcUrl, destUrl) > 0;
            if (rtn) {
                updateProgress(1, 1);
            }
            return rtn;
        }

        /**
         * ArcInfo Coverage->要素数据集
         *
         * @param dc
         * @param srcUrl
         * @param destUrl
         * @return
         */
        private boolean convertArcCoverageToFds(DataConvert dc, String srcUrl, String destUrl) {
            boolean rtn = false;
            dc.openSource(srcUrl);
            dc.openDestination(destUrl);
            registerCallback(dc);
            rtn = dc.convert() > 0;
            return rtn;
        }

        /**
         * Dxf、Dwg文件->要素数据集、MapGIS GDB
         *
         * @param dc
         * @param srcUrl
         * @param destUrl
         * @param option
         * @return
         */
        private boolean convertDwgTo7x(DataConvert dc, String srcUrl, String destUrl, ConvertOption option) {
            boolean rtn = false;
            dc.openSource(srcUrl);
            dc.openDestination(destUrl);
            if (!XString.isNullOrEmpty(option.getDxfSymbolTable())) {
                dc.setOption(ConvertOptionType.OPT_DXF_MAPFILE, option.getDxfSymbolTable());
            }
            dc.setOption(ConvertOptionType.OPT_DXF_BLKPROC, option.getMappingWay() == 1);
            dc.setOption(ConvertOptionType.OPT_DXF_ADJUST_FONT, option.isAdjustFontSize());
            registerCallback(dc);
            rtn = dc.convert() > 0;
            return rtn;
        }

        /**
         * ArcGIS File GDB文件、Personal GDB->MapGIS GDB
         *
         * @param dc
         * @param srcUrl
         * @param destUrl
         * @return
         */
        private boolean convertFileGDBTo7x(DataConvert dc, String srcUrl, String destUrl) {
            boolean rtn = false;
            dc.openSource(srcUrl);
            dc.openDestination(destUrl);
            registerCallback(dc);
            rtn = dc.convert() > 0;
            return rtn;
        }

        /**
         * Json文件->要素数据集、MapGIS GDB
         *
         * @param dc
         * @param srcUrl
         * @param destUrl
         * @param option
         * @return
         */
        private boolean convertJsonTo7x(DataConvert dc, String srcUrl, String destUrl, ConvertOption option) {
            boolean rtn = false;
            dc.openSource(srcUrl);
            dc.openDestination(destUrl);
            registerCallback(dc);
            rtn = dc.convert() > 0;
            return rtn;
        }

        /**
         * MapGIS GDB FDS -> ArcGIS Prsonal GDB
         *
         * @param dc
         * @param srcUrl
         * @param destUrl
         * @param option
         * @return
         */
        private boolean convertGDBToPGDB(DataConvert dc, String srcUrl, String destUrl, ConvertOption option) {
            boolean rtn = false;
            dc.openSource(srcUrl);
            dc.openDestination(destUrl);
            dc.setOption(ConvertOptionType.OPT_PGDB_VERSION, option.getPgdbVersion());
            registerCallback(dc);
            rtn = dc.convert() > 0;
            return rtn;
        }

        //MapGIS_GDB、MapGIS_FDS -> ArcGIS_FileGDB
        private boolean convertGDBToFileGDB(DataConvert dc, String srcUrl, String destUrl, ConvertOption gdco) {
            boolean rtn = false;
            dc.openSource(srcUrl);
            dc.openDestination(destUrl);
            registerCallback(dc);
            rtn = dc.convert() > 0;
            return rtn;
        }

        //


        private String combineMessage(String msg, boolean rtn) {
            return String.format("%s\t%s", msg, rtn ? "成功" : "失败");
        }

        //endregion

        //region 注册回调
        private LogEventReceiver logEventReceiver;

        /**
         * 注册回调
         *
         * @param dc 数据转换对象
         */
        private void registerCallback(DataConvert dc) {
            if (logEventReceiver == null) {
                logEventReceiver = new LogEventReceiver();
                logEventReceiver.addStepStartListener(stepName ->
                {
                    if (!XString.isNullOrEmpty(stepName)) {
                        writeLog("转换中信息：开始 " + stepName);
                    }
                });
                logEventReceiver.addStepMessageListener(message ->
                {
                    if (!XString.isNullOrEmpty(message)) {
                        writeLog("转换中信息：" + message);
                    }
                });
                logEventReceiver.addStepEndListener((status, progress, stepName, isAppendLog) ->
                {
                    if (!XString.isNullOrEmpty(stepName)) {
                        writeLog(combineMessage("转换中信息：结束 " + stepName, status == ProgressStatus.Succeeded));
                    }
                    if (this.convertItem != null) {
                        updateProgress(progress, 1);
                    }
                });
            }
            dc.setProcessCallback(logEventReceiver);
        }
        //endregion
    }
}
