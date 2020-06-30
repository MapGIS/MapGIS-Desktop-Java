package com.zondy.mapgis.filedialog;

import com.zondy.mapgis.base.XFunctions;
import com.zondy.mapgis.geodatabase.*;
import com.zondy.mapgis.geodatabase.config.ConnectType;
import com.zondy.mapgis.geodatabase.config.DataSrcInfo;
import com.zondy.mapgis.geodatabase.config.SvcConfig;
import com.zondy.mapgis.geodatabase.net.GNetInfo;
import com.zondy.mapgis.geodatabase.raster.MDsInfo;
import com.zondy.mapgis.geodatabase.raster.RDsInfo;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 文件打开对话框基类
 * @author zkj
 */
public abstract class FileDialog extends Dialog<String[]> {

    //region 内部变量
    private static char[] _GDBInvalidChars = new char[]{'\\', '/', ':', '*', '?', '"', '<', '>', '|'};
    //属性设置
    protected String m_FileName;
    protected String[] m_fileNames;  //返回的文件或路径
    private String m_filter;    //设置文件过滤符
    protected boolean m_MultiSelect = false; //是否多选
    private boolean m_ShowRecentFiles = false; //是否显示最近打开文件
    protected int m_folderType = FolderType.MapGIS_DataBase;  //选择GDB目录范围
    //当前目录
    protected final static String m_GDBProName = "GDBP://";
    private String m_curMapGISDir = null; //当前列表中GDB数据的目录
    private String m_CurDiskDir = ""; // 当前列表中磁盘目录
    //初始化目录
    private String m_initialDir;  //用户设置的初始化目录
    private static String m_lastWinDir = null;  //最近一次打开的本地磁盘目录
    private static String m_lastGDBDir = null; //最近一次打开的GDB数据目录
    private static boolean m_lastIsGDBDir = false; //最近一次打开的是否是GDB数据目录
    //辅助变量
    private FileDialogType DialogType = FileDialogType.OpenFileDialog; //当前文件对话框显示类型
    private CurViewType m_CurViewType = CurViewType.Disk; //当前内容视图展示数据来源
    private String m_type; //对话框类型关键字
    private String m_DlgTitle = "打开文件"; //对话框标题

    //endregion

    //region 界面变量
    private Stage stage = null;
    private GridPane gridPane = null;
    //工具条
    private Label labSearch = null;
    private ComboBox<String> searchCombobox = new ComboBox<>(); //查找范围下拉框
    private ObservableList<String> searchObsList = FXCollections.observableArrayList();
    private Button btnUpOneLevel = null; // 返回上一级目录按钮
    private Button btnCreateNewFolder = null; //创建磁盘目录
    private Button btnDel = null; //删除磁盘文件或目录
    //数据源导航列表
    private TreeView<MySvrTreeItem> m_TreeView = null; //数据源导航树
    private TreeItem<MySvrTreeItem> m_RootTreeItem = null; //导航树根节点
    private ObservableList<TreeItem<MySvrTreeItem>> m_SvrTreeItems = null;
//    private ArrayList<Button> btnServerList = new ArrayList<>();
    private ArrayList<String> rootPathList = new ArrayList<>();
    private ArrayList<Server> m_Servers = new ArrayList<Server>(); //GDB数据源对象列表
    //数据列表
    private TableView<DataInfo> m_DataView = null;
    private ObservableList<DataInfo> m_ObserveList = null;
    //文件类型下拉框
    private ComboBox<FileType> m_FileTypeComboBox = null;
    private ObservableList<FileType> m_ObsFileTypeList = null;
    private FileType m_SelectedFileType; //当前选择的文件过滤符类型
    //文件名下拉框
    private ComboBox<String> m_FileNameComboBox = null;
    private ObservableList<String> m_ObsFileNameList = null;
    private String m_FileNameComboBoxText = ""; //输入的文件名-保存文件时默认显示选中的文件名
    //确定、取消按钮
    private Button btnOk = null;
    //endregion

    //region 初始化图标
    private final List<Image> tableViewImageList = new ArrayList<>(Arrays.asList(
            new Image(getClass().getResourceAsStream("/Png_SfClsSurface_16.png")),//0-面简单要素类
            new Image(getClass().getResourceAsStream("/Png_3dFds_16.png")),//1-三维数据集
            new Image(getClass().getResourceAsStream("/Png_ACls_16.png")),//2-注记
            new Image(getClass().getResourceAsStream("/Png_FDs_16.png")),//3-要素数据集
            new Image(getClass().getResourceAsStream("/Png_GDataBase_16.png")),//4-数据库
            new Image(getClass().getResourceAsStream("/Png_SfClsLin_16.png")),//5-线简单要素类
            new Image(getClass().getResourceAsStream("/Png_NetCls_16.png")),//6-网络类
            new Image(getClass().getResourceAsStream("/Png_SfClsPnt_16.png")),//7-点简单要素类
            new Image(getClass().getResourceAsStream("/Png_RasterDs_16.png")),//8-栅格数据集
            new Image(getClass().getResourceAsStream("/Png_SfClsReg_16.png")),//9-区简单要素类
            new Image(getClass().getResourceAsStream("/Png_SfCls_16.png")),//10-简单要素类
            new Image(getClass().getResourceAsStream("/Png_RasterCatalog_16.png")),//11-栅格目录
            new Image(getClass().getResourceAsStream("/Png_OCls_16.png")),//12-对象类
            new Image(getClass().getResourceAsStream("/Png_Mapset_16.png")),//13-地图集
            new Image(getClass().getResourceAsStream("/Png_CadCls_16.png")),//14-CAD类(不会添加此类型图标)
            new Image(getClass().getResourceAsStream("/Png_SfClsSurface_16.png")),//15-面简单要素类
            new Image(getClass().getResourceAsStream("/Png_SfClsEntity_16.png")),//16-体简单要素类
            new Image(getClass().getResourceAsStream("/Png_File_16.png")),//17-文件
            new Image(getClass().getResourceAsStream("/Png_RCls_16.png")),//18-关系类
            new Image(getClass().getResourceAsStream("/Png_MosaicDataSet_16.png")),//19-镶嵌数据集
            new Image(getClass().getResourceAsStream("/Png_Folder1_16.png")),//20-文件夹
            new Image(getClass().getResourceAsStream("/Png_Disk_32.png")), //21磁盘驱动器
            new Image(getClass().getResourceAsStream("/Png_GDBServer_16.png")) //22数据源
    ));

    //endregion

    /**
     * 构造函数
     */
    public FileDialog(String type, boolean showRecentFiles) {

        //region 确定对话框标题
        m_type = type;
        m_ShowRecentFiles = showRecentFiles;
        switch (m_type) {
            case "Open File":
                DialogType = FileDialogType.OpenFileDialog;
                m_DlgTitle = "打开文件";
                break;
            case "Save File As":
                DialogType = FileDialogType.SaveFileDialog;
                m_DlgTitle = "保存文件";
                break;
            case "Select Directory":
                DialogType = FileDialogType.SelectFolderDialog;
                m_DlgTitle = "浏览文件夹";
                break;
        }
        setTitle(m_DlgTitle);
        //endregion

        //初始化图标

        //初始化界面
        initialize();
        //绑定事件
        bindAction();
        String btnOkTitle = "打开";
        if (DialogType == FileDialogType.SaveFileDialog) {
            labSearch.setText("另存为");
            btnOkTitle = "保存";
        }
        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setContent(this.gridPane);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        stage = (Stage) dialogPane.getScene().getWindow();
        btnOk = (Button) dialogPane.lookupButton(ButtonType.OK);
        btnOk.setText(btnOkTitle);
        btnOk.addEventFilter(ActionEvent.ACTION, this::okButtonClick);
        setResultConverter(dialogButton -> dialogButton == ButtonType.OK ? m_fileNames : null);

    }

    /**
     * 初始化界面
     */
    private void initialize() {

        //region 初始化面板容器
        gridPane = new GridPane();
//        gridPane.setPrefWidth(600);
//        gridPane.setPrefHeight(400);
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        //endregion
        //region 初始化最上方的工具栏
        int rowIndex = 0;
        ToolBar toolBar = new ToolBar();
        toolBar.setPadding(new Insets(0, 5, 5, 5));
        GridPane toolBarGridPane = new GridPane();
        toolBarGridPane.setHgap(5);
        Button btn1 = new Button();
        btn1.setPrefWidth(100);
        btn1.setVisible(false);
        labSearch = new Label("查找范围:");
        labSearch.setPrefWidth(150);
        searchObsList = FXCollections.observableArrayList();
        searchCombobox = new ComboBox<>();
        searchCombobox.setPrefWidth(200);
        searchCombobox.setEditable(false);
        searchCombobox.setItems(searchObsList);
        btnUpOneLevel = new Button("上一级");
        btnUpOneLevel.setDisable(true);
        btnCreateNewFolder = new Button("新建");
        btnCreateNewFolder.setDisable(true);
        btnDel = new Button("删除");
        btnDel.setDisable(true);
        int col = 0;
//        toolBarGridPane.add(btn1,col,rowIndex);
//        col++;
        toolBarGridPane.add(labSearch, col, rowIndex);
        col++;
        toolBarGridPane.add(searchCombobox, col, rowIndex);
        col++;
        toolBarGridPane.add(btnUpOneLevel, col, rowIndex);
        col++;
        toolBarGridPane.add(btnCreateNewFolder, col, rowIndex);
        col++;
        toolBarGridPane.add(btnDel, col, rowIndex);
        toolBar.getItems().addAll(toolBarGridPane);
        gridPane.add(toolBar, 0, rowIndex, 3, 1);
        //endregion
        //region 数据源导航条
        rowIndex++;
//        GridPane svrListGridPane = new GridPane();
//        svrListGridPane.setVgap(5);
//        svrListGridPane.setHgap(5);
        TitledPane titledPane = new TitledPane();
        m_TreeView = new TreeView<>();
        m_SvrTreeItems = FXCollections.observableArrayList();
        m_RootTreeItem = new TreeItem<>(new MySvrTreeItem("",false));
        m_TreeView.setRoot(m_RootTreeItem);
        m_RootTreeItem.setExpanded(true);

        //m_RootTreeItem.getChildren().addAll(m_SvrTreeItems);
//        svrListGridPane.add(m_TreeView,0,0);
        titledPane.setText("数据源");
        titledPane.setCollapsible(false);
        titledPane.setContent(m_TreeView);
        titledPane.setPadding(new Insets(0, 5, 5, 5));
        titledPane.setPrefWidth(150);
        titledPane.setMinHeight(400);
        titledPane.setMaxHeight(600);
        gridPane.add(titledPane, 0, rowIndex, 1, 3);
        ScrollPane sp = new ScrollPane();
        sp.setContent(titledPane);
        //endregion

        //region 添加磁盘数据源
        {
//            Button btnDisk = new Button();
//            svrListGridPane.add(btnDisk, 0, 0);
//            btnDisk.setPrefWidth(130);
//            btnDisk.setText("磁盘");
//            this.btnServerList.add(btnDisk);
//            btnDisk.setOnAction(new EventHandler<ActionEvent>() {
//                @Override
//                public void handle(ActionEvent event) {
//                    m_CurViewType = CurViewType.Disk;
//                    m_CurDiskDir = "";
//                    btnUpOneLevel.setDisable(true);
//                    btnCreateNewFolder.setDisable(true);
//                    btnDel.setDisable(true);
//                    //初始化磁盘根目录
//                    updateDiskViewRootList();
//                }
//            });

            MySvrTreeItem svrTreeItem = new MySvrTreeItem("磁盘",false);
            TreeItem<MySvrTreeItem> subTreeItem = new TreeItem(svrTreeItem);
            subTreeItem.setGraphic(new ImageView(tableViewImageList.get(22)));
            m_RootTreeItem.getChildren().addAll(subTreeItem);
            m_SvrTreeItems.addAll(subTreeItem);
        }
        //endregion

        //region 添加GDB数据源列表
        int count = SvcConfig.count();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                DataSrcInfo info = SvcConfig.get(i);
                if (info != null) {
                    //Linux系统目前只支持Sqlite数据源
                    if(XFunctions.isSystemLinux())
                    {
                        if(info.getSvcType() != ConnectType.LocalPlus)
                            break;
                    }
                    String svrName = info.getSvcName();
//                    Button btnGDB = new Button();
//                    btnGDB.setPrefWidth(130);
//                    svrListGridPane.add(btnGDB, 0, i + 1);
//                    this.btnServerList.add(btnGDB);
//                    btnGDB.setUserData(m_GDBProName + svrName);
//                    btnGDB.setText(svrName);
//                    btnGDB.setOnAction(new EventHandler<ActionEvent>() {
//                        @Override
//                        public void handle(ActionEvent event) {
//                            m_CurViewType = CurViewType.GDB;
//                            //切换GDB数据源更新数据列表
//                            updateListView(m_GDBProName + svrName);
//                            rootPathList.add(m_GDBProName + svrName);
//                        }
//                    });
                    //添加到树节点
                    MySvrTreeItem svrTreeItem = new MySvrTreeItem(svrName,true);
                    TreeItem<MySvrTreeItem> subTreeItem = new TreeItem(svrTreeItem);
                    subTreeItem.setGraphic(new ImageView(tableViewImageList.get(22)));
                    m_RootTreeItem.getChildren().addAll(subTreeItem);
                    m_SvrTreeItems.addAll(subTreeItem);

                }
            }
        }

        //region 切换数据源树节点初始化GDB或磁盘数据列表
        m_TreeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<MySvrTreeItem>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<MySvrTreeItem>> observable, TreeItem<MySvrTreeItem> oldValue, TreeItem<MySvrTreeItem> newValue) {
                if(newValue != null)
                {
                    MySvrTreeItem treeItem = newValue.getValue();
                    if(treeItem.getText() != null && treeItem.getText().length() >0)
                    {
                        boolean isGDBSvr = treeItem.isGDBSvr();
                        if(isGDBSvr)
                        {
                            //region 初始化GDB数据源数据列表
                            m_CurViewType = CurViewType.GDB;
                            //切换GDB数据源更新数据列表
                            String svrName = treeItem.getText();
                            updateListView(m_GDBProName + svrName);
                            rootPathList.add(m_GDBProName + svrName);
                            //endregion
                        }
                        else
                        {
                            //region 初始化磁盘数据列表
                            m_CurViewType = CurViewType.Disk;
                            m_CurDiskDir = "";
                            btnUpOneLevel.setDisable(true);
                            btnCreateNewFolder.setDisable(true);
                            btnDel.setDisable(true);
                            //初始化磁盘根目录
                            updateDiskViewRootList();
                            //endregion
                        }
                    }
                }
            }
        });
        //endregion

        //endregion

        //region 查看数据列表
        m_ObserveList = FXCollections.observableArrayList();
        m_DataView = new TableView<DataInfo>(m_ObserveList);
        m_DataView.setPrefHeight(400);
        m_DataView.setPrefWidth(600);
        m_DataView.setPadding(new Insets(5, 5, 5, 5));
        m_DataView.setEditable(false); //不允许编辑
        //定义数据列
        TableColumn<DataInfo,String> tc_Image = new TableColumn<>("");
        tc_Image.setPrefWidth(30);
        tc_Image.setCellValueFactory(new PropertyValueFactory<>("image"));
        tc_Image.setCellFactory(new Callback<TableColumn<DataInfo, String>, TableCell<DataInfo, String>>() {
            @Override
            public TableCell<DataInfo, String> call(TableColumn<DataInfo, String> param) {
                return new TableCell<DataInfo, String>()
                {
                    @Override
                    protected void updateItem(String image, boolean empty) {
                        if(image != null &&image.length()>0) {
                            int index = Integer.valueOf(image);
                            super.updateItem(image, empty);
                            ImageView imageView = new ImageView(tableViewImageList.get(index));
                            setGraphic(imageView);
                            setAlignment(Pos.CENTER);
                        }
                        else
                        {
                            setGraphic(null);
                        }
                    }
                };
            }
        });
        TableColumn<DataInfo, String> tc_name = new TableColumn("名称");
        tc_name.setMinWidth(100);
        tc_name.setPrefWidth(300);
        tc_name.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataInfo, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DataInfo, String> param) {
                return new SimpleStringProperty(param.getValue().getName());
            }
        });
        TableColumn<DataInfo, String> tc_type = new TableColumn("类型");
        tc_type.setMinWidth(100);
        tc_type.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataInfo, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DataInfo, String> param) {
                return new SimpleStringProperty(param.getValue().getType());
            }
        });
        TableColumn<DataInfo, String> tc_ctime = new TableColumn("创建日期");
        tc_ctime.setMinWidth(100);
        tc_ctime.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataInfo, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DataInfo, String> param) {
                return new SimpleStringProperty(param.getValue().getCtime());
            }
        });
        TableColumn<DataInfo, String> tc_mtime = new TableColumn("修改日期");
        tc_mtime.setMinWidth(100);
        tc_mtime.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DataInfo, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DataInfo, String> param) {
                return new SimpleStringProperty(param.getValue().getMtime());
            }
        });
        m_DataView.getColumns().addAll(tc_Image,tc_name, tc_type, tc_mtime, tc_ctime);
//        m_DataView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        gridPane.add(m_DataView, 1, rowIndex);
        //endregion
        //region 文件名、文件类型
        //region 文件名称下拉框
        Label lbFileName = new Label("文件名称: ");
        m_ObsFileNameList = FXCollections.observableArrayList();
        m_FileNameComboBox = new ComboBox<>(m_ObsFileNameList);
        m_FileNameComboBox.setEditable(true);
        m_FileNameComboBox.setPrefWidth(550);
        //获取保存的文件名称
        m_FileNameComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                m_FileNameComboBoxText = newValue;
            }
        });
        GridPane fileNameGridPane = new GridPane();
        fileNameGridPane.setHgap(5);
        rowIndex++;
        fileNameGridPane.add(lbFileName, 0, 0);
        fileNameGridPane.add(m_FileNameComboBox, 1, 0);
        gridPane.add(fileNameGridPane, 1, rowIndex, 1, 1);
        //endregion
        //region 数据类型下拉框
        Label lbFileType = new Label("文件类型: ");
        this.m_ObsFileTypeList = FXCollections.observableArrayList();
        this.m_FileTypeComboBox = new ComboBox<>(m_ObsFileTypeList);
        m_FileTypeComboBox.setPrefWidth(550);
        if (this.DialogType == FileDialogType.SelectFolderDialog) {
            lbFileName.setText("文件夹名称:");
            lbFileType.setVisible(false);
            m_FileTypeComboBox.setVisible(false);
        }
        GridPane fileTypeGridPane = new GridPane();
        fileTypeGridPane.setHgap(5);
        rowIndex++;
        fileTypeGridPane.add(lbFileType, 0, 0);
        fileTypeGridPane.add(m_FileTypeComboBox, 1, 0);
        gridPane.add(fileTypeGridPane, 1, rowIndex);
        //endregion
        //region 切换文件下拉类型刷新当前数据列表
        this.m_FileTypeComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FileType>() {
            @Override
            public void changed(ObservableValue<? extends FileType> observable, FileType oldValue, FileType newValue) {
                m_SelectedFileType = newValue;
                if (m_SelectedFileType != null) {
                    if (m_CurViewType == CurViewType.GDB) {
                        //初始化当前类型的目录数据
                        String url = m_curMapGISDir;
                        updateListView(url);
                    } else if (m_CurViewType == CurViewType.Disk) {
                        //根据选择文件类型刷新磁盘列表
                        String url = m_CurDiskDir;

                        updateDiskViewCurrentList(url);
                    }
                }
            }
        });
        //endregion
        //endregion

    }

    /**
     * 绑定事件
     */
    private void bindAction() {
        //region 行双击事件,双击展开或选择数据
        m_DataView.setRowFactory(new Callback<TableView<DataInfo>, TableRow<DataInfo>>() {
            class TableRowControl extends TableRow<DataInfo> {
                public TableRowControl() {
                    super();
                    this.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            if (event.getButton().equals(MouseButton.PRIMARY)
                                    && event.getClickCount() == 2
                                    && TableRowControl.this.getIndex() < m_DataView.getItems().size()) {
                                int i = TableRowControl.this.getIndex();
                                if (i > -1) {
                                    // region 双击进入目录或选中文件、目录退出
                                    DataInfo info = m_DataView.getItems().get(i);
                                    String url = info.getUrl();
                                    if (url != null && url.length() > 0) {
                                        //region 选择 Table、Coverage 文件
                                        if ((m_CurViewType == CurViewType.Table || m_CurViewType == CurViewType.Coverage) && DialogType == FileDialogType.OpenFileDialog) {
                                            m_FileName = url;
                                            m_fileNames = new String[]{url};
                                            //关闭对话框前调用方法关闭所有数据源连接
                                            btnOk.fire();
                                            return;
                                        }
                                        //endregion
                                        if (m_curMapGISDir == "RecentOpenFiles") {
                                            btnOk.fire();
                                            return;
                                        }
                                        if (m_CurViewType == CurViewType.GDB) {
                                            //region GDB双击
                                            XClsType xClsType = getXclsTypeByUrl(url);
                                            //海图数据判断，对标识为海图的特殊要素数据集(海图数据)不展开
                                            if ((xClsType != XClsType.XRcat && xClsType != XClsType.XFds && xClsType.value() != -2 && includeXClsObject(xClsType)) ||
                                                    (xClsType == XClsType.XRcat && includeXClsObject(xClsType) && !includeXClsObject(XClsType.XRds)) ||
                                                    (xClsType == XClsType.XFds && includeXClsObject(xClsType) && !includeXClsObject(XClsType.XSFCls) && !includeXClsObject(XClsType.XACls) && !includeXClsObject(XClsType.XOCls) && !includeXClsObject(XClsType.XRCls) && !includeXClsObject(XClsType.XGNet)) ||
                                                    (xClsType.value() == -2 && onlyIncludeXClsObject(xClsType)) /*|| CheckChartDataByUrl(url)*/) {
                                                btnOk.fire();
                                            } else {
                                                if (DialogType == FileDialogType.SelectFolderDialog && onlySelectDataBaseFolder()) {
                                                    btnOk.fire();
                                                } else {
                                                    updateListView(url);
                                                }
                                            }
                                            //endregion
                                        } else if (m_CurViewType == CurViewType.Disk) {
                                            File file = new File(url);
                                            //region 磁盘双击
                                            if (file.isDirectory()) {
                                                updateDiskViewCurrentList(url);
                                            } else {
                                                btnOk.fire();
                                            }
                                            //endregion
                                        }
                                    }
                                    //endregion
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public TableRow<DataInfo> call(TableView<DataInfo> param) {
                return new TableRowControl();
            }
        });
        //endregion

        //region 列表项选择改变事件-保存磁盘文件模式下更新确定按钮名称为打开或保存
        m_DataView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DataInfo>() {
            @Override
            public void changed(ObservableValue<? extends DataInfo> observable, DataInfo oldValue, DataInfo newValue) {
                if (m_CurViewType == CurViewType.Disk && newValue != null) {
                    if (m_curMapGISDir != null && m_CurDiskDir.length() > 0)
                        btnDel.setDisable(false); //选中数据即可激活删除按钮
                }
                if (DialogType == FileDialogType.SaveFileDialog) {
                    DataInfo item = newValue;
                    if (m_CurViewType == CurViewType.GDB) {
                        //region GDB视图模式改确认按钮名称
                        if (item != null) {
                            if (includeXClsObject(getXclsTypeByUrl(item.getUrl()))) {
                                m_FileNameComboBox.setValue(item.getName());
                                m_FileNameComboBoxText = item.getName();
                                if (btnOk.getText() != "保存")
                                    btnOk.setText("保存");
                            } else {
                                if (btnOk.getText() != "打开")
                                    btnOk.setText("打开");
                            }
                        } else {
                            if (btnOk.getText() != "保存")
                                btnOk.setText("保存");
                        }
                        //endregion
                    } else if (m_CurViewType == CurViewType.Disk) {
                        //region 磁盘视图模式改确认按钮名称
                        if (item != null) {
                            if (includeWinFile(getFileExt(item.getUrl()))) {
                                //根据路径取文件名
                                m_FileNameComboBoxText = item.getName();
                                m_FileNameComboBox.setValue(item.getName());
                                if (btnOk.getText() != "保存")
                                    btnOk.setText("保存");
                            } else {
                                if (btnOk.getText() != "打开")
                                    btnOk.setText("打开");
                            }
                        } else {
                            if (btnOk.getText() != "保存")
                                btnOk.setText("保存");
                        }
                        //endregion
                    }
                }
            }
        });
        //endregion

        //region 返回磁盘上一级目录
        btnUpOneLevel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (m_CurViewType == CurViewType.Disk) {
                    //region 返回磁盘上一级目录
                    File file = new File(m_CurDiskDir);
                    if (rootPathList.contains(m_CurDiskDir)) {
                        m_CurDiskDir = "";
                        updateDiskViewRootList();
                        btnUpOneLevel.setDisable(true);
                        btnCreateNewFolder.setDisable(true);
                        btnDel.setDisable(true);
                        return;
                    } else {
                        String upLevelDir = file.getParent();
                        m_CurDiskDir = upLevelDir;
                        updateDiskViewCurrentList(m_CurDiskDir);
                        return;
                    }
                    //endregion
                } else if (m_CurViewType == CurViewType.GDB) {
                    upOneLevelGDBUrl(m_curMapGISDir);
                    //当退到数据源这一级就无法再回退
                    if (rootPathList.contains(m_curMapGISDir)) {
                        btnUpOneLevel.setDisable(true);
                    }
                }
            }
        });
        //endregion

        //region 删除文件或目录
        btnDel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DataInfo item = m_DataView.getSelectionModel().getSelectedItem();
                String strMsg = "是否确定删除此文件?";
                File file = new File(item.getUrl());
                if (file.isDirectory()) {
                    strMsg = "是否确定删除此文件夹?";
                }
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, strMsg, ButtonType.YES, ButtonType.NO);
                Optional<ButtonType> optional = alert.showAndWait();
                if (optional != null && optional.isPresent()) {
                    ButtonType type = optional.get();
                    if (type == ButtonType.YES) {
                        file.delete();
                        updateDiskViewCurrentList(m_CurDiskDir);
                    }
                }
            }
        });
        //endregion

        //region  新建文件夹
        btnCreateNewFolder.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String folderName = "";
                NewFolderDialog dialog = new NewFolderDialog();
                Optional<String> optional = dialog.showAndWait();
                if (optional != null && optional.isPresent()) {
                    folderName = optional.get();
                    //region 提示结果
                    if (folderName == null || folderName.length() == 0) {
                        AutoMessageBox.ShowAutoMessageBox("文件名不能为空!", "提示");
                        event.consume();
                    } else {
                        String sep = java.io.File.separator;
                        String dir = m_CurDiskDir + sep + folderName;
                        File file = new File(dir);
                        if (file.exists()) {
                            AutoMessageBox.ShowAutoMessageBox("目录已存在!", "提示");
                            event.consume();
                        } else {
                            boolean rtn = file.mkdir();
                            if (rtn) {
                                AutoMessageBox.ShowAutoMessageBox("新建成功!", "提示");
                                updateDiskViewCurrentList(m_CurDiskDir);
                            } else {
                                AutoMessageBox.ShowAutoMessageBox("新建失败!", "提示");
                            }
                        }
                    }
                    //endregion
                }
            }
        });
        //endregion
    }

    /**
     * 确定按钮选中后退出或继续进入子目录
     */
    private void okButtonClick(ActionEvent event) {
        if (this.DialogType == FileDialogType.SaveFileDialog) {
            //region 保存文件
            if (m_CurViewType == CurViewType.GDB) {
                //region 保存到GDB目录
                DataInfo item = m_DataView.getSelectionModel().getSelectedItem();
                if (item != null) {
                    if (!includeXClsObject(getXclsTypeByUrl(item.getUrl()))) {
                        updateListView(item.getUrl());
                        event.consume();
                        return;
                    }
                }

                //验证文件名输入是否非法(不含非法字符或长度超出范围)
                for (int i = 0; i < _GDBInvalidChars.length; i++) {
                    char curChar = _GDBInvalidChars[i];
                    if (m_FileNameComboBoxText.contains(String.valueOf(curChar))) {
                        AutoMessageBox.ShowAutoMessageBox("名称非法,不能包含下列任何字符:\\ / : * ? \" < > |", "提示");
                        event.consume();
                        return;
                    }
                }

                String curUrl = m_curMapGISDir;
                String serverName = null;
                String gdbName = null;
                String dsName = null;
                String rCatName = null;
                String d3fdsName = null;
                GDBUrlInfo gdbUrlInfo = analyseURL(m_curMapGISDir);
                serverName = gdbUrlInfo.serverName;
                gdbName = gdbUrlInfo.gdbName;
                dsName = gdbUrlInfo.dsName;
                rCatName = gdbUrlInfo.rCatName;
                d3fdsName = gdbUrlInfo.d3fdsName;
                //修改说明：底层接口变动需对url的数据源加上用户名否则新建图层会失败
                //修改人：张凯俊 2018-5-11
                Server svr = this.getServer(serverName);
                String dbUser = "";
                if (svr != null) {
                    int svrIndex = curUrl.indexOf(serverName);
                    //当服务名存在时若服务名前面没有带上登录名和@符号则补上
                    if (svrIndex == m_GDBProName.length()) {
                        String userName = svr.getLogin()[0];
                        if (userName != null && userName.length() > 0) {
                            dbUser = userName + "@";
                        }
                    }
                }
                if (this.m_FileNameComboBoxText.length() > 0) {
                    if (gdbName == null || gdbName.length() == 0) {
                        AutoMessageBox.ShowAutoMessageBox("请在列表视图中选择存储数据的地理数据库。", "提示");
                        event.consume();
                        return;
                    }
                    FileType gdbType = m_SelectedFileType;
                    if (gdbType != null && /*gdbType.IsSaveMapGISFile()*/ gdbType.getGDBFileExt().size() > 0) {
                        //string ext = gdbType.Extensions[0];
                        String ext = gdbType.getGDBFileExt().get(0);
                        // 修改说明：保存时的URL应移除过滤类型的几何标识，解决bug7138
                        // 修改人：周小飞 2015-09-29
                        if (ext.compareToIgnoreCase("sfclsp") == 0 ||
                                ext.compareToIgnoreCase("sfclsl") == 0 ||
                                ext.compareToIgnoreCase("sfclsr") == 0 ||
                                ext.compareToIgnoreCase("sfclss") == 0 ||
                                ext.compareToIgnoreCase("sfclse") == 0) {
                            ext = "sfcls";
                        }
                        String url = curUrl + "/" + ext + "/" + this.m_FileNameComboBoxText;
                        if (url.toLowerCase().startsWith(m_GDBProName)) {
                            url = m_GDBProName + dbUser + url.substring(m_GDBProName.length());
                        }
                        if (ext.compareToIgnoreCase(getXclsTypeString(XClsType.XRds)) == 0) {
                            // 修改说明：兼容fds标记（平一提供的对ds的可替换标记）
                            // 修改人：华凯 2014-05-30
                            if (url.toLowerCase().indexOf("/ds/") >= 0 || url.toLowerCase().indexOf("/fds/") >= 0)//栅格数据集不能保存在要素数据集中
                            {
                                AutoMessageBox.ShowAutoMessageBox("栅格数据集不能保存在要素数据集中!", "提示");
                                event.consume();
                                return;
                            }
                            if (!canSaveRdsInRcat()) {
                                if (url.toLowerCase().indexOf("/rcat/") >= 0)//栅格数据集不能保存在栅格目录中
                                {
                                    AutoMessageBox.ShowAutoMessageBox("栅格数据集不能保存在栅格目录中!", "提示");
                                    event.consume();
                                    return;
                                }
                            }
                        } else {
                            // 修改说明：栅格目录中不能保存sfcls、ocls等数据
                            // 修改人：周小飞 2014-05-23
                            if (curUrl.toLowerCase().indexOf("/rcat/") >= 0) {
                                AutoMessageBox.ShowAutoMessageBox("不能保存在栅格目录中!", "提示");
                                event.consume();
                                return;
                            }
                        }

                        boolean isOK = true;

                        if (existXCls(url, this.m_FileNameComboBoxText)) {
                            if (getCanOverwrite()) {
                                {
                                    //暂时屏蔽
//                                        if (!FileOverwriteDlg(this.m_FileNameComboBoxText)) {
//                                            isOK = false;
//                                        }
                                    isOK = false;
                                }
                            } else {
                                isOK = false;
                                String msg = String.format("%s已存在，请重新命名", this.m_FileNameComboBoxText);
                                AutoMessageBox.ShowAutoMessageBox(msg, "提示");
                                event.consume();

                            }
                        }
                        if (isOK) {
                            m_fileNames = new String[]{url};
                            // AddRecentFile(m_fileNames, this.DialogType);
                        } else {
                            event.consume();
                        }
                    }
                } else {
                    AutoMessageBox.ShowAutoMessageBox("名称不能为空。", "提示");
                    event.consume();
                }
                //endregion
            } else if (this.m_CurViewType == CurViewType.Disk) {
                //region 保存到磁盘目录
                //region 选中目录则继续进入文件夹
                DataInfo item = m_DataView.getSelectionModel().getSelectedItem();
                if (item != null) {
                    String path = item.getUrl();
                    File file = new File(path);
                    if (file.isDirectory()) {
                        updateDiskViewCurrentList(path);
                        event.consume();
                        return;
                    }
                }
                //endregion
                //region 验证文件名输入是否非法(不含非法字符或长度超出范围)
                for (int i = 0; i < _GDBInvalidChars.length; i++) {
                    char curChar = _GDBInvalidChars[i];
                    if (m_FileNameComboBoxText.contains(String.valueOf(curChar))) {
                        AutoMessageBox.ShowAutoMessageBox("名称非法,不能包含下列任何字符:\\ / : * ? \" < > |", "提示");
                        event.consume();
                        return;
                    }
                }
                if (this.m_FileNameComboBoxText.trim().length() == 0) {
                    AutoMessageBox.ShowAutoMessageBox("名称不能为空", "提示");
                    event.consume();
                    return;
                }
                //endregion
                //region 确认后保存文件
                String dir = m_CurDiskDir;
                if (dir == null || dir.length() > 0) {
                    String path = dir + File.separator + m_FileNameComboBoxText + m_SelectedFileType.getWinFileExt().get(0);
                    File saveFile = new File(path);
                    if (saveFile.exists()) {
                        String msg = String.format("%s已存在，请重新命名", this.m_FileNameComboBoxText);
                        AutoMessageBox.ShowAutoMessageBox(msg, "提示");
                        event.consume();
                        return;
                    }
                    m_fileNames = new String[]{path};
                    return;
                } else {
                    event.consume();
                    return;
                }
                //endregion
                //endregion
            } else if (this.m_CurViewType == CurViewType.VFS) {
                //region 保存到HDFS目录
                //region 接口暂未支持先屏蔽
                /*
                ListViewItem item = this.customListView.FocusedItem;
                if (item != null)
                {
                    if (string.IsNullOrEmpty(m_curVFSDir) || m_curVFSDir == m_VFSName || item.Tag is VFSConfigInfo)
                    {
                        customListView_MouseDoubleClick(this.customListView, new MouseEventArgs(System.Windows.Forms.MouseButtons.Left, 0, 0, 0, 0));
                        return;
                    }
                }
                if (!(string.IsNullOrEmpty(m_curVFSDir) || m_curVFSDir == m_VFSName || (this.m_CurSelectConfigItem != null && m_curVFSDir == this.m_CurSelectConfigItem.VFSUrl)))
                {
                    if (this.fileNameComboBox.Text.IndexOfAny(_GDBInvalidChars) >= 0
                            || this.fileNameComboBox.Text.StartsWith(" ")
                            || GetStringTrueLength(this.fileNameComboBox.Text) >= 128)
                    {
                        AutoMessageBox.Show(Resources.String_NameIllegal, Resources.String_Prompt, MessageBoxButtons.OK, MessageBoxIcon.Asterisk);
                        return;
                    }
                    if (this.fileNameComboBox.Text.Trim().Length > 0)
                    {
                        FileType gdbType = this.m_FileTypeComboBox.SelectedItem as FileType;
                        if (gdbType == null)
                            return;
                        string ext = gdbType.Extensions[0];
                        string url = m_curVFSDir + this.fileNameComboBox.Text + ext;
                        bool isOK = true;
                        if (isOK)
                        {
                            m_fileNames = new string[] { url };
                            this.DialogResult = DialogResult.OK;
                        }
                    }
                    else
                    {
                        AutoMessageBox.Show("名称不能为空。", Resources.String_Prompt, MessageBoxButtons.OK, MessageBoxIcon.Information);
                        return;
                    }
                }
                */
                //endregion暂时屏蔽
                //endregion
            }
            //endregion
        } else if (this.DialogType == FileDialogType.SelectFolderDialog) {
            //region 选择目录
            if (m_CurViewType == CurViewType.GDB) {
                //region 选择GDB目录
                boolean rtn = false;
                String curUrl = m_curMapGISDir;
                DataInfo item = m_DataView.getSelectionModel().getSelectedItem();
                if (item != null) {
                    curUrl = item.getUrl();
                }
                String serverName = null;
                String gdbName = null;
                String dsName = null;
                String rCatName = null;
                String d3fdsName = null;
                GDBUrlInfo gdbUrlInfo = analyseURL(curUrl);
                serverName = gdbUrlInfo.serverName;
                gdbName = gdbUrlInfo.gdbName;
                dsName = gdbUrlInfo.dsName;
                rCatName = gdbUrlInfo.rCatName;
                d3fdsName = gdbUrlInfo.d3fdsName;
                //修改说明：底层接口变动需对url的数据源加上用户名否则新建图层会失败
                //修改人：张凯俊 2018-5-11
                Server svr = this.getServer(serverName);
                String dbUser = "";
                if (svr != null) {
                    int svrIndex = curUrl.indexOf(serverName);
                    //当服务名存在时若服务名前面没有带上登录名和@符号则补上
                    if (svrIndex == m_GDBProName.length()) {
                        String userName = svr.getLogin()[0];
                        if (userName != null && userName.length() > 0) {
                            dbUser = userName + "@";
                        }
                    }
                }
                String url = curUrl;
                url = m_GDBProName + dbUser + url.substring(m_GDBProName.length());
                if (serverName != null && gdbName == null) {
                    if ((m_folderType & FolderType.MapGIS_Server) > 0) {
                        rtn = true;
                    }
                } else if (serverName != null && gdbName != null && dsName == null && rCatName == null) {
                    if ((m_folderType & FolderType.MapGIS_DataBase) > 0) {
                        rtn = true;
                    }
                } else if (serverName != null && gdbName != null && dsName != null && rCatName == null) {
                    if ((m_folderType & FolderType.MapGIS_Fds) > 0) {
                        rtn = true;
                    }
                } else if (serverName != null && gdbName != null && dsName == null && rCatName != null) {
                    if ((m_folderType & FolderType.MapGIS_Rcat) > 0) {
                        rtn = true;
                    }
                }
                if (rtn) {
                    m_fileNames = new String[]{url};
                } else {
                    updateListView(url);
                    event.consume();
                }
                //endregion
            } else if (m_CurViewType == CurViewType.Disk) {
                //region 选择磁盘目录
                String dir = m_CurDiskDir;
                DataInfo item = m_DataView.getSelectionModel().getSelectedItem();
                if (item != null) {
                    dir = item.getUrl();
                }
                if (dir == null || dir.length() == 0) {
                    event.consume();
                    return;
                }
                m_fileNames = new String[]{dir};
                //endregion
            }
            //endregion
        } else {
            if (this.getSelectItems() != null && this.getSelectItems().length > 0) {
                //region 选择文件后退出
                switch (m_CurViewType) {
                    case GDB:
                    case Recent:
                        // region GDB/Recent
                        if (this.getSelectItems().length == 1) {
                            //region 选中一条数据
                            String url = this.getSelectItems()[0].getUrl();
                            XClsType xClsType = getXclsTypeByUrl(url);
                            //判断海图数据
                            if (includeXClsObject(xClsType) || (xClsType == XClsType.Unknown && includeWinFile(getFileExt(url))) /*|| CheckChartDataByUrl(url)*/) {
                                if (url.startsWith(m_GDBProName.toLowerCase())) {
                                    url = m_GDBProName + url.substring(m_GDBProName.length());
                                }
                                m_fileNames = new String[]{url};
                                //AddRecentFile(m_fileNames, this.DialogType);
                                // return;
                            } else {
                                updateListView(url);
                            }
                            //endregion 
                        } else {
                            //region 选择多条数据
                            List<String> selectItems = new ArrayList<String>();
                            for (int i = 0; i < getSelectItems().length; i++) {
                                String url = getSelectItems()[i].getUrl();
                                XClsType xClsType = getXclsTypeByUrl(url);
                                //判断海图数据
                                if (includeXClsObject(xClsType) || (xClsType == XClsType.Unknown && includeWinFile(getFileExt(url))) /*|| CheckChartDataByUrl(url)*/) {
                                    if (url.startsWith(m_GDBProName.toLowerCase())) {
                                        url = m_GDBProName + url.substring(m_GDBProName.length());
                                    }
                                    selectItems.add(url);
                                }
                            }
                            if (selectItems.size() > 0) {
                                m_fileNames = new String[selectItems.size()];
                                //m_fileNames = (String[]) seleItems.toArray(); //这样写会挂掉
                                for (int i = 0; i < selectItems.size(); i++) {
                                    m_fileNames[i] = selectItems.get(i);
                                }
                                //AddRecentFile(m_fileNames, this.DialogType);
                                return;
                            } else {
                                updateListView(getSelectItems()[0].getUrl());
                                event.consume();
                                return;
                            }
                            //endregion
                        }
                        break;
                    // endregion
                    case Disk:
                        //region 选择磁盘文件
                        boolean getFirstDir = false;
                        String firstDir = "";
                        //region 从选择项中筛选出文件列表
                        List<String> selectItems = new ArrayList<String>();
                        for (int i = 0; i < getSelectItems().length; i++) {
                            String url = getSelectItems()[i].getUrl();
                            File file = new File(url);
                            if (file.isFile()) {
                                selectItems.add(url);
                            } else {
                                if (!getFirstDir) {
                                    firstDir = url;
                                    getFirstDir = true;
                                }
                            }
                        }
                        //endregion
                        int size = selectItems.size();
                        if (size > 0) {
                            m_fileNames = new String[size];
                            for (int i = 0; i < size; i++) {
                                m_fileNames[i] = selectItems.get(i);
                            }
                            return;
                        } else if (getFirstDir) {
                            updateDiskViewCurrentList(firstDir);
                        }
                        break;
                    //endregion

                    //region 暂时屏蔽VSF待后续支持
//                        {
//                            case VFS:
//                                //选择分布式文件系统数据
//                                if (this.customListView.SelectedItems.Count == 1) {
//                                    string url = this.customListView.SelectedItems[0].Name;
//                                    int state = VSIFileSystem.VSIFileStat(url);
//                                    if (state == 1) {
//                                        m_fileNames = new string[]{url};
//                                        this.DialogResult = DialogResult.OK;
//                                    } else if (state == 2) {
//                                        UpdadteVFSListView(url);
//                                    }
//                                } else {
//                                    List<string> seleItems = new List<string>();
//                                    foreach(ListViewItem item in this.customListView.SelectedItems)
//                                    {
//                                        string url = item.Name;
//                                        int state = VSIFileSystem.VSIFileStat(url);
//                                        if (state == 1) {
//                                            seleItems.Add(url);
//                                        }
//                                    }
//                                    if (seleItems.Count > 0) {
//                                        m_fileNames = seleItems.ToArray();
//                                        this.DialogResult = DialogResult.OK;
//                                    } else {
//                                        if (this.customListView.FocusedItem != null) {
//                                            UpdadteVFSListView(this.customListView.FocusedItem.Name);
//                                        }
//                                    }
//                                }
//                                    break;
//                                //endregion
                    //region Table、Coverage暂不支持
//                    case CurViewType.Table:
//                    case CurViewType.Coverage: {
//                        List<string> seleItems = new List<string>();
//                        foreach(ListViewItem item in this.customListView.SelectedItems)
//                        {
//                            seleItems.Add(item.Name);
//                        }
//                        if (seleItems.Count > 0) {
//                            m_fileNames = seleItems.ToArray();
//                            this.DialogResult = DialogResult.OK;
//                        }
//                    }
//                    break;
                    //endregion
                }
                //endregion
            } else {
                //region 选择当前目录
                // 修改标识：周小飞 2015-05-13
                // 修改描述：当未选中任何项时,若为文件打开对话框，且能选择目录，如GDB、数据集等，点击打开按钮，也可打开所在目录
                switch (m_CurViewType) {
                    case GDB:
                        String url = getCurMapGISDir();
                        XClsType xClsType = getXclsTypeByUrl(url);
                        if (includeXClsObject(xClsType) || (xClsType == XClsType.Unknown && includeWinFile(getFileExt(url)))) {
                            if (url.startsWith(m_GDBProName.toLowerCase())) {
                                url = m_GDBProName + url.substring(m_GDBProName.length());
                            }
                            m_fileNames = new String[]{url};
                            // AddRecentFile(m_fileNames, this.DialogType);
                            return;
                        } else {
                            event.consume();
                        }
                        break;
                    case Disk: {
                        event.consume(); //未选中磁盘文件不退出
                    }
                    break;
                    default:
                        break;
                }
                //endregion
            }
        }
    }

    //region 磁盘分支

    /**
     * 获取磁盘文件后缀名 返回格式形如 ".tif"
     */
    private String getFileExt(String url) {
        String ext = "";
        int index = url.lastIndexOf(".");
        if (index > -1) {
            ext = url.substring(index);
        }
        return ext;
    }

    /**
     * 初始化磁盘根目录
     */
    private void updateDiskViewRootList() {
        m_ObserveList.clear();
        File[] files = File.listRoots();
        if (files != null && files.length > 0) {
            rootPathList.clear();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String path = file.getPath();
                DataInfo info = new DataInfo();
                info.setName(path);
                info.setUrl(path);
                info.setType("本地磁盘");
                info.setImage("21");
                this.m_ObserveList.add(info);
                rootPathList.add(path);
            }
        }
        m_DataView.refresh();
        searchCombobox.setValue("");
    }

    /**
     * 初始化磁盘当前目录
     */
    private void updateDiskViewCurrentList(String dir) {
        if (dir == null || dir.length() == 0) {
            return;
        }
        m_ObserveList.clear();
        m_CurDiskDir = dir;
        searchCombobox.setValue(m_CurDiskDir);
        updateDiskUI(dir, "", true);
        File curFile = new File(dir);
        //region 遍历当前目录符合要求的数据列表
        if (curFile.isDirectory()) {
            File[] files = curFile.listFiles();
            if (files != null && files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    String path = file.getPath();
                    if (this.DialogType == FileDialogType.OpenFileDialog || this.DialogType == FileDialogType.SaveFileDialog) {
                        //region 选择文件或保存文件则过滤不匹配文件类型
                        if (!file.isDirectory()) {
                            String ext = getFileExt(path);
                            if (m_SelectedFileType != null) {
                                if (!m_SelectedFileType.isIncludeWinFileExt(ext)) {
                                    continue;
                                }
                            }
                        }
                        //endregion
                    } else {
                        //region 选择目录则屏蔽所有文件
                        if (!file.isDirectory()) {
                            continue;
                        }
                        //endregion
                    }
                    DataInfo info = new DataInfo();
                    info.setName(path);
                    if (file.isDirectory()) {
                        info.setType("文件夹");
                        info.setImage("20");
                    } else {
                        info.setType("文件");
                        info.setImage("17");
                    }
                    info.setUrl(path);
                    this.m_ObserveList.add(info);
                }
            }
        }
        //endregion
        m_DataView.refresh();
    }

    /**
     * 更新选择数据时UI状态-显示当前目录、向上、新建、及删除按钮的可用性
     */
    private void updateDiskUI(String url, String serverName, boolean upButtonState) {
        btnOk.setDisable(false);
        btnUpOneLevel.setDisable(!upButtonState);
        m_curMapGISDir = url;
        btnDel.setDisable(true); //选中文件方可点亮删除按钮
        btnCreateNewFolder.setDisable(false);
        m_CurViewType = CurViewType.Disk;
    }
    //endregion

    //region 公共方法、属性

    /**
     * 设置文件过滤符
     */
    public void setFilter(String filter) {
        if (this.DialogType == FileDialogType.OpenFileDialog || this.DialogType == FileDialogType.SaveFileDialog) {
            m_filter = filter;
            initFileTypeCombobox();
            //根据文件过滤类型决定是否显示磁盘或GDB数据源
            if (onlyIncludeWinFile()) {
                initServerItems(true,false);
                //仅显示磁盘文件
//                if (this.btnServerList.size() > 1) {
//                    for (int i = 1; i < this.btnServerList.size(); i++) {
//                        this.btnServerList.get(i).setVisible(false);
//                    }
//                    //初始化磁盘根目录
//                    this.btnServerList.get(0).fire();
//                }
            } else if (onlyIncludeGDBFile()) {
                initServerItems(false,true);
                //仅显示GDB数据
//                this.btnServerList.get(0).setVisible(false);
//                if (this.btnServerList.size() > 1) {
//                    this.btnServerList.get(1).fire();
//                }
            }
        }
    }

    /**
     * 获取文件名
     */
    public String getFileName() {
        return m_FileName;
    }

    /**
     * 获取文件列表
     */
    public String[] getFileNames() {
        return m_fileNames;
    }

    /**
     * 设置是否多选
     */
    public void setMultiSelect(boolean val) {
        if (DialogType == FileDialogType.OpenFileDialog) {
            m_MultiSelect = val;
        }
        //region 设置是否多选
        if (m_MultiSelect) {
            m_DataView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        } else {
            m_DataView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        }
    }

    /**
     * 目录类型
     */
    public void setFolderType(int type) {
        m_folderType = type;
        //根据目录类型决定是否显示磁盘或GDB数据源
        if (this.DialogType == FileDialogType.SelectFolderDialog) {
            if (onlySelectWindowsFolder()) {
                initServerItems(true,false);
                //仅显示磁盘目录
//                if (this.btnServerList.size() > 1) {
//                    for (int i = 1; i < this.btnServerList.size(); i++) {
//                        this.btnServerList.get(i).setVisible(false);
//                    }
//                }
            } else if (onlySelectMapGISFolder()) {
                initServerItems(false,true);
                //仅显示GDB数据
//                this.btnServerList.get(0).setVisible(false);
            }
        }
    }
    //endregion

    //region 过滤方法

    /**
     * 该GDB数据类型是否包含在所选文件类型下拉项过滤符中
     */
    private boolean includeXClsObject(XClsType xClsType) {
        FileType gdbType = this.m_SelectedFileType;
        if (gdbType != null) {
            if (gdbType.isIncludeAllGDBFiles() && xClsType != XClsType.Unknown) {
                return true;
            }
            return gdbType.isIncludeGDBFileExt(this.getXclsTypeString(xClsType));
        }
        return false;
    }

    /**
     * 文所选文件类型下拉项过滤符是否仅表示该GDB数据
     */
    private boolean onlyIncludeXClsObject(XClsType xClsType) {
        FileType gdbType = this.m_SelectedFileType;
        if (gdbType != null) {
            if (gdbType.isIncludeAllGDBFiles() && xClsType != XClsType.Unknown) {
                return false;
            }
            return gdbType.isOnlyIncludeGDBFileExt(this.getXclsTypeString(xClsType));
        }
        return false;
    }

    /**
     * 该文件后缀是否包含在所选文件类型下拉项过滤符中
     */
    private boolean includeWinFile(String ext) {
        FileType gdbType = this.m_SelectedFileType;
        if (gdbType != null) {
            if (gdbType.isIncludeAllWinFiles()) {
                return true;
            }
            return gdbType.isIncludeWinFileExt(ext);
        }
        return false;
    }

    /**
     * 下拉框所有文件类型是否仅表示磁盘文件类型
     */
    private boolean onlyIncludeWinFile() {
        boolean rtn = true;
        for (FileType ft : this.m_ObsFileTypeList) {
            if (ft != null) {
                if (!(ft.containWinFile() && !ft.containGDBFile())) {
                    rtn = false;
                    break;
                }
            }
        }
        return rtn;
    }

    /**
     * 下拉框所有文件类型是否仅表示GDB数据
     */
    private boolean onlyIncludeGDBFile() {
        boolean rtn = true;
        for (int i = 0; i < this.m_ObsFileTypeList.size(); i++) {
            FileType ft = m_ObsFileTypeList.get(i);
            if (ft != null) {
                if (!(!ft.containWinFile() && ft.containGDBFile())) {
                    rtn = false;
                    break;
                }
            }
        }
        return rtn;
    }

    /**
     * GDB数据库中是否存在此数据
     */
    private boolean existXCls(String url, String name) {
        int xClsID = 0;
        XClsType xClsType = getXclsTypeByUrl(url);
        String serverName = null;
        String gdbName = null;
        String dsName = null;
        String rCatName = null;
        String d3fdsName = null;
        GDBUrlInfo info = analyseURL(url);
        serverName = info.serverName;
        gdbName = info.gdbName;
        if (serverName != null && gdbName != null) {
            Server sr = getServer(serverName);
            if (sr != null) {
                DataBase db = getGDB(sr, gdbName);
                if (db != null) {
                    xClsID = (int) db.xClsIsExist(xClsType, name);
                    db.close();
                }
                //sr.DisConnect();
            }
        }
        return xClsID > 0;
    }

    /**
     * 对于GDB目录只选择GDB数据库这一类型
     */
    protected boolean onlySelectDataBaseFolder() {
        return m_folderType == FolderType.MapGIS_DataBase || m_folderType == (FolderType.MapGIS_DataBase | FolderType.Disk_Folder);
    }

    /**
     * 仅选择GDB目录
     */
    protected boolean onlySelectMapGISFolder() {
        return !((m_folderType & FolderType.Disk_Folder) == FolderType.Disk_Folder || (m_folderType & FolderType.VFS_Folder) == FolderType.VFS_Folder);
    }

    /**
     * 仅选择磁盘目录
     */
    protected boolean onlySelectWindowsFolder() {
        return m_folderType == FolderType.Disk_Folder;
    }

    /**
     * 仅选择VFS目录
     */
    protected boolean onlySelectHDFSFolder() {
        return m_folderType == FolderType.VFS_Folder;
    }

    /**
     * 当为保存文件对话框时，若用户选择了已有文件，提示是否要覆盖还是不能覆盖必须选一个未存在的文件
     */
    protected boolean getCanOverwrite() {
        return false;
    }

    /**
     * 当为保存文件对话框时，且GetCanOverwrite()设置为true，即可被覆盖时，是否显示覆盖提示对话框
     */
    protected boolean showOverwritePrompt() {
        return true;
    }

    /**
     * 是否可以将栅格数据集保存到栅格目录
     */
    protected boolean canSaveRdsInRcat() {
        return false;
    }
    //endregion

    //region 内部方法
    /**
     * 初始化数据源导航树
     * 是否列出磁盘树节点
     * 是否列出GDB数据源树节点
     */
    private void initServerItems(boolean showDisk,boolean showGDB)
    {
        m_RootTreeItem.getChildren().clear();
        if (showDisk) {
            MySvrTreeItem svrTreeItem = new MySvrTreeItem("磁盘", false);
            TreeItem<MySvrTreeItem> subTreeItem = new TreeItem(svrTreeItem);
            subTreeItem.setGraphic(new ImageView(tableViewImageList.get(22)));
            m_RootTreeItem.getChildren().addAll(subTreeItem);
            m_SvrTreeItems.addAll(subTreeItem);
        }
        if(showGDB)
        {
            int count = SvcConfig.count();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    DataSrcInfo info = SvcConfig.get(i);
                    if (info != null) {
                        //Linux系统目前只支持Sqlite数据源
                        if(XFunctions.isSystemLinux())
                        {
                            if(info.getSvcType() != ConnectType.LocalPlus)
                                break;
                        }
                        String svrName = info.getSvcName();
//                    Button btnGDB = new Button();
//                    btnGDB.setPrefWidth(130);
//                    svrListGridPane.add(btnGDB, 0, i + 1);
//                    this.btnServerList.add(btnGDB);
//                    btnGDB.setUserData(m_GDBProName + svrName);
//                    btnGDB.setText(svrName);
//                    btnGDB.setOnAction(new EventHandler<ActionEvent>() {
//                        @Override
//                        public void handle(ActionEvent event) {
//                            m_CurViewType = CurViewType.GDB;
//                            //切换GDB数据源更新数据列表
//                            updateListView(m_GDBProName + svrName);
//                            rootPathList.add(m_GDBProName + svrName);
//                        }
//                    });
                        //添加到树节点
                        MySvrTreeItem svrTreeItem = new MySvrTreeItem(svrName,true);
                        TreeItem<MySvrTreeItem> subTreeItem = new TreeItem(svrTreeItem);
                        subTreeItem.setGraphic(new ImageView(tableViewImageList.get(22)));
                        m_RootTreeItem.getChildren().addAll(subTreeItem);
                        m_SvrTreeItems.addAll(subTreeItem);
                    }
                }
            }
        }
    }

    /**
     * 更新下拉框界面
     */
    private void initFileTypeCombobox() {
        //region 初始化文件类型下拉列表
        if (m_filter != null) {
            // Add the filter to the m_FileTypeComboBox
            String[] filterParts = m_filter.split("\\|");
            int addIndex = 0;
            m_ObsFileTypeList.clear();
            for (int i = 1; i < filterParts.length; i += 2) {
                this.m_ObsFileTypeList.add(addIndex, new FileType(filterParts[i - 1], filterParts[i].split(";")));
                addIndex++;
            }
        }
        this.m_FileTypeComboBox.setItems(this.m_ObsFileTypeList);
        if (this.m_ObsFileTypeList != null && this.m_ObsFileTypeList.size() > 0) {
            m_SelectedFileType = this.m_ObsFileTypeList.get(0);
            this.m_FileTypeComboBox.getSelectionModel().select(0);
        }
        //endregion
    }

    /**
     * 获取选择列表
     */
    private DataInfo[] getSelectItems() {
        DataInfo[] arrInfo = null;
        ObservableList<DataInfo> selItems = m_DataView.getSelectionModel().getSelectedItems();
        if (selItems != null && selItems.size() > 0) {
            arrInfo = new DataInfo[selItems.size()];
            for (int i = 0; i < selItems.size(); i++) {
                arrInfo[i] = selItems.get(i);
            }
        }
        return arrInfo;
    }

    /**
     * 解析URL。根据URL，获取 Server、GDB、要素数据集、栅格目录、三维要素数据集 的名称
     */
    protected GDBUrlInfo analyseURL(String url) {
        String serverName = null;
        String gdbName = null;
        String dsName = null;
        String rCatName = null;
        String d3fdsName = null;
        String userName = null;
        String password = null;
        if (url == null || url.trim() == "")
            return null;
        if (!url.toLowerCase().startsWith(m_GDBProName.toLowerCase()))
            return new GDBUrlInfo(serverName, gdbName, dsName, rCatName, d3fdsName, userName, password);
        int p1 = url.indexOf('/');
        int p2 = url.indexOf('/', p1 + 1);
        int p3 = url.indexOf('/', p2 + 1);
        int p4 = url.indexOf('/', p3 + 1);
        if (p1 < 0 || p2 < 0)
            return null;
        if (p3 == -1) {
            serverName = url.substring(p2 + 1);
            return new GDBUrlInfo(serverName, gdbName, dsName, rCatName, d3fdsName, userName, password);
        } else {
//            serverName = url.substring(p2 + 1, p3 - p2 - 1);//C#接口语义为param1开始总长度为param2
            serverName = url.substring(p2 + 1, p3);//java此接口语义为param1开始终止索引为param2
        }

        if (serverName == "") {
            serverName = null;
            return new GDBUrlInfo(serverName, gdbName, dsName, rCatName, d3fdsName, userName, password);
        }
        String oldSerName = serverName;
        int indexAt = serverName.indexOf("@");
        if (indexAt >= 0) {
            serverName = oldSerName.substring(indexAt + 1);
            if (indexAt > 0) {
                String userPas = oldSerName.substring(0, indexAt);
                int indexColon = userPas.indexOf(':');
                if (indexColon >= 0) {
                    if (indexColon + 1 < userPas.length()) {
                        password = userPas.substring(indexColon + 1);
                    }
                    if (indexColon > 0) {
                        userName = userPas.substring(0, indexColon);
                    }
                } else {
                    userName = userPas;
                }
            }
        }
        if (serverName == "") {
            serverName = null;
            return new GDBUrlInfo(serverName, gdbName, dsName, rCatName, d3fdsName, userName, password);
        }
        if (p4 == -1) {
            gdbName = url.substring(p3 + 1);
            return new GDBUrlInfo(serverName, gdbName, dsName, rCatName, d3fdsName, userName, password);
        } else {
//            gdbName = url.substring(p3 + 1, p4 - p3 - 1);//C#接口语义为param1开始总长度为param2
            gdbName = url.substring(p3 + 1, p4); //java此接口语义为param1开始终止索引为param2
        }

        if (gdbName == "") {
            gdbName = null;
            return new GDBUrlInfo(serverName, gdbName, dsName, rCatName, d3fdsName, userName, password);
        }
        int gdbUrlLength = (m_GDBProName + oldSerName + "/" + gdbName).length();
        //获取要素数据集名称
        {
            String temp = "/ds/";
            // 修改说明：兼容fds标记（平一提供的对ds的可替换标记）
            // 修改人：华凯 2014-05-30
            if (url.indexOf("/fds/") >= 0)
                temp = "/fds/";
            int startDs = url.indexOf(temp);
            if (startDs != -1 && startDs == gdbUrlLength) {
                int endDs = url.indexOf("/", startDs + temp.length());
                if (endDs != -1) {
                    //C#接口语义为param1开始总长度为param2
                    {
//                        endDs = endDs - startDs - temp.length();
//                        dsName = url.substring(startDs + temp.length(), endDs);
                    }
                    dsName = url.substring(startDs + temp.length(), endDs);
                } else {
                    dsName = url.substring(startDs + temp.length());
                    if (dsName == "") {
                        dsName = null;
                    }
                }
            }
        }
        //获取栅格目录名称
        {
            String temp = "/rcat/";
            int startDs = url.indexOf(temp);
            if (startDs != -1 && startDs == gdbUrlLength) {
                int endDs = url.indexOf("/", startDs + temp.length());
                if (endDs != -1) {
                    //C#接口语义为param1开始总长度为param2
                    {
//                        endDs = endDs - startDs - temp.length();
//                        rCatName = url.substring(startDs + temp.length(), endDs);
                    }
                    rCatName = url.substring(startDs + temp.length(), endDs);
                } else {
                    rCatName = url.substring(startDs + temp.length());
                    if (rCatName == "") {
                        rCatName = null;
                    }
                }
            }
        }
        //获取三维要素数据集名称
        {
            String temp = "/3dfds/";
            int startDs = url.indexOf(temp);
            if (startDs != -1 && startDs == gdbUrlLength) {
                int endDs = url.indexOf("/", startDs + temp.length());
                if (endDs != -1) {
                    //C#接口语义为param1开始总长度为param2
                    {
//                        endDs = endDs - startDs - temp.length();
//                        d3fdsName = url.substring(startDs + temp.length(), endDs);
                    }
                    d3fdsName = url.substring(startDs + temp.length(), endDs);
                } else {
                    d3fdsName = url.substring(startDs + temp.length());
                    if (d3fdsName == "") {
                        d3fdsName = null;
                    }
                }
            }
        }
        return new GDBUrlInfo(serverName, gdbName, dsName, rCatName, d3fdsName, userName, password);
    }

    /**
     * 将XClsType转换为string 后缀
     */
    private String getXclsTypeString(XClsType xClsType) {
        String rtn = null;
        switch (xClsType.value()) {
            case 62: //XClsType.X3DFCls:
                rtn = "3dfcls";
                break;
            case 25: //XClsType.XRds:
                rtn = "ras";
                break;
            case 66: //XClsType.XMosaicDS:  //镶嵌数据集支持 ch 20180122
                rtn = "mds";
                break;
            case 30: //XClsType.XSFCls:
                rtn = "sfcls";
                break;
            case 4: //XClsType.XOCls:
                rtn = "ocls";
                break;
            case 22: //XClsType.XGNet:
                rtn = "ncls";
                break;
            case 5: //XClsType.XACls:
                rtn = "acls";
                break;
            case 36: //XClsType.XMapSet:
                rtn = "mapset";
                break;
            case 64: //XClsType.XSFCls3D:
                rtn = "s3dfcls";
                break;
            case 24: //XClsType.XRcat:
                rtn = "rcat";
                break;
            case 2: //XClsType.Fds:
                rtn = "ds";
                break;
            case 6: // XClsType.XRCls:
                rtn = "rcls";
                break;
            default:
                if (xClsType.value() == -2) {
                    rtn = "gdb";
                } else if (xClsType.value() == -3) {
                    rtn = "svr";
                }
                break;
        }
        return rtn;
    }

    /**
     * 根据URL获取其类型
     */
    private XClsType getXclsTypeByUrl(String url) {
        XClsType xClsType = XClsType.Unknown;
        if (!url.toLowerCase().startsWith(m_GDBProName.toLowerCase())) {
            return xClsType;
        }
        int last1 = url.lastIndexOf('/');
        if (last1 <= 0) {
            return xClsType;
        }
        String name = url.substring(last1 + 1);
        String str = url.substring(0, last1); // String str = url.Remove(last1);
        int last2 = str.lastIndexOf('/');
        String strType = str.substring(last2 + 1);
        switch (strType.toLowerCase()) {
            case "3dfcls":
                xClsType = XClsType.X3DFCls; //FCls3D; //三维要素类
                break;
            case "ras":
                xClsType = XClsType.XRds;       //栅格数据集
                break;
            case "mds":
                xClsType = XClsType.XMosaicDS;       //镶嵌数据集支持 ch 20180122
                break;
            case "sfcls":
                xClsType = XClsType.XSFCls;     //简单要素类
                break;
            case "ocls":
                xClsType = XClsType.XOCls;      //对象类
                break;
            case "ncls":
                xClsType = XClsType.XGNet;      //网络类
                break;
            case "acls":
                xClsType = XClsType.XACls;      //注记类
                break;
            case "mapset":
                xClsType = XClsType.XMapSet;    //地图集
                break;
            case "s3dfcls":
                xClsType = XClsType.XSFCls3D;   //三维简单要素类
                break;
            case "rcat":
                xClsType = XClsType.XRcat;      //栅格目录
                break;
            case "ds":
                xClsType = XClsType.XFds;       //要素数据集
                break;
            case "rcls":
                xClsType = XClsType.XRCls;      //关系类
                break;
            default: {
                String serverName = null;
                String gdbName = null;
                String dsName = null;
                String rCatName = null;
                String d3fdsName = null;

                GDBUrlInfo gdbUrlInfo = analyseURL(url);
                if (gdbUrlInfo != null) {
                    serverName = gdbUrlInfo.serverName;
                    gdbName = gdbUrlInfo.gdbName;
                }
                if (serverName != null && serverName.length() > 0) {
                    boolean isGDB = (gdbName != null && gdbName.length() > 0);
                    xClsType = isGDB ? XClsType.valueOf(-2) : XClsType.valueOf(-3);
                }

            }
            break;
        }
        return xClsType;
    }

    /**
     * 根据Server名称获取其对象
     */
    protected Server getServer(String serverName) {
        int size = m_Servers.size();
        for (int i = 0; i < size; i++) {
            Server server = m_Servers.get(i);
            if (server.getSvrName().toLowerCase() == serverName.toLowerCase() && server.hasConnected()) {
                return server;
            }
        }
        for (int i = 0; i < SvcConfig.count(); i++) {
            String name = SvcConfig.get(i).getSvcName();
            if (name.compareToIgnoreCase(serverName) == 0) {
                String user = null;
                String pwd = null;
                String[] logInfo = Server.getLogInfo(serverName);
                if (logInfo != null && logInfo.length == 2) {
                    user = logInfo[0];
                    pwd = logInfo[1];
                }
                boolean isUserInput = false;
                Server sr = new Server();
                if (sr.connect(serverName, user, pwd) <= 0) {
                    ConnectServer cs = new ConnectServer(sr, serverName);
                    cs.showDialog();
                    if (cs.isDialogResultOk()) {
                        user = cs.getUserName();
                        pwd = cs.getPassword();
                        isUserInput = true;
                    } else {
                        return null;
                    }
                }
                if (sr.hasConnected()) {
                    if (isUserInput) {
                        Server.appendLogInfo(serverName, user, pwd);
                    }
                    m_Servers.add(sr);
                    return sr;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * 根据GDB的名称获取其对象
     */
    private DataBase getGDB(Server sr, String gdbName) {
        return sr.getGdb(gdbName);
    }

    /**
     * 获取当前的GDB目录
     */
    protected String getCurMapGISDir() {
        String rtn = null;
        if (m_DataView != null) {
            DataInfo dataInfo = m_DataView.getSelectionModel().getSelectedItem();
            if (dataInfo != null) {
                rtn = dataInfo.getUrl();
            }
        } else {
            rtn = m_curMapGISDir;
        }
        if (rtn != null && rtn.startsWith(m_GDBProName.toLowerCase())) {
            rtn = m_GDBProName + rtn.substring(m_GDBProName.length());
        }
        return rtn;
    }

    private void upOneLevelGDBUrl(String url) {
        if (url == null || url.length() == 0) {
            return;
        }
        //region 解析URL
        String serverName = null;
        String gdbName = null;
        String dsName = null;
        String rCatName = null;
        String d3fdsName = null;

        GDBUrlInfo gdbUrlInfo = analyseURL(url);
        if (gdbUrlInfo == null) {
            return;
        }
        serverName = gdbUrlInfo.serverName;
        gdbName = gdbUrlInfo.gdbName;
        dsName = gdbUrlInfo.dsName;
        rCatName = gdbUrlInfo.rCatName;
        d3fdsName = gdbUrlInfo.d3fdsName;
        //endregion
        //region 初始化上一级目录数据
        if (serverName != null && gdbName != null && dsName == null && rCatName == null && d3fdsName == null) {
            updateListView(m_GDBProName + serverName);
        } else if (serverName != null && gdbName != null && dsName != null && rCatName == null && d3fdsName == null) {
            updateListView(m_GDBProName + serverName + "/" + gdbName);
        } else if (serverName != null && gdbName != null && dsName == null && rCatName != null && d3fdsName == null) {
            updateListView(m_GDBProName + serverName + "/" + gdbName);
        } else if (serverName != null && gdbName != null && dsName == null && rCatName == null && d3fdsName != null) {
            updateListView(m_GDBProName + serverName + "/" + gdbName);
        }
        //endregion
    }
    //endregion

    //region 初始化GDB数据

    /**
     * 更新MapGIS数据的ListView
     */
    private void updateListView(String url) {
        if (url == "RecentOpenFiles") {
//            m_curMapGISDir = "RecentOpenFiles";
//            UpdateRecentFileView(_RecentFiles);
            return;
        }
        m_curMapGISDir = url;
        if (m_curMapGISDir.toUpperCase().startsWith(m_GDBProName)) {
            String text = m_curMapGISDir.substring(m_GDBProName.length());
            searchCombobox.setValue(text);
        }
        String serverName = null;
        String gdbName = null;
        String dsName = null;
        String rCatName = null;
        String d3fdsName = null;

        GDBUrlInfo gdbUrlInfo = analyseURL(url);
        if (gdbUrlInfo == null) {
            return;
        }
        serverName = gdbUrlInfo.serverName;
        gdbName = gdbUrlInfo.gdbName;
        dsName = gdbUrlInfo.dsName;
        rCatName = gdbUrlInfo.rCatName;
        d3fdsName = gdbUrlInfo.d3fdsName;

        //region 初始化数据源下面的数据列表
        if (serverName != null && gdbName == null) {
            Server sr = getServer(serverName);
            if (sr != null) {
                this.m_ObserveList.clear();
                {
                    //刷新界面控件
//                lookInComboBox.Items.Clear();
//                lookInComboBox.CurrentItem = new LookInComboBoxItem(serverName, m_GDBProName + serverName, 0, MapGIS.Desktop.Resources.Png_GDBServer_16, MapGIS.Desktop.Resources.Png_GDBServer_16);
//                lookInComboBox.Refresh();
                }
                updateGdbUI(sr.getURL(), serverName, false);
                initGDBServerListView(sr);
                this.m_DataView.setItems(this.m_ObserveList);
            }
        }
        //endregion
        //region 初始化数据库下面的数据列表
        if (serverName != null && gdbName != null && dsName == null && rCatName == null && d3fdsName == null) {
            Server sr = getServer(serverName);
            if (sr != null) {
                DataBase db = getGDB(sr, gdbName);
                if (db != null) {
                    String gdbUrl = db.getURL();
                    this.m_ObserveList.clear();
                    {
//                    lookInComboBox.Items.Clear();
//                    lookInComboBox.CurrentItem = new LookInComboBoxItem(db.Name, gdbUrl, 0, MapGIS.Desktop.Resources.Png_GDataBase_16, MapGIS.Desktop.Resources.Png_GDataBase_16);
//                    lookInComboBox.Refresh();
                        updateGdbUI(url, serverName, true);
                    }
                    initGDBListView(db);
                    initClassView(db, 0, gdbUrl);
                    initRdsListView(db, 0, gdbUrl);
                    initMdsListView(db, gdbUrl);    //镶嵌数据集支持 ch 20180122
                    {
                        //Init3dfclsListView(db, 0, gdbUrl);
                    }
                    initMapSetsListView(db, gdbUrl);
                    {
                        //InitFds_HTListView(db, gdbUrl);//海图数据支持 zkj-2019-6-25
                    }
                    db.close();
                    this.m_DataView.setItems(this.m_ObserveList);
                } else {
                    this.m_ObserveList.clear();
                    {
//                        lookInComboBox.Items.Clear();
//                        lookInComboBox.CurrentItem = new LookInComboBoxItem(serverName, m_GDBProName + serverName, 0, MapGIS.Desktop.Resources.Png_GDBServer_16, MapGIS.Desktop.Resources.Png_GDBServer_16);
//                        lookInComboBox.Refresh();
                        updateGdbUI(url, serverName, false);
                    }
                    initGDBServerListView(sr);
                    this.m_DataView.setItems(this.m_ObserveList);
                }
            }
        }
        //endregion
        //region 初始化数据库下面的数据集中数据列表
        if (serverName != null && gdbName != null && dsName != null && rCatName == null && d3fdsName == null) {
            Server sr = getServer(serverName);
            if (sr != null) {
                DataBase db = getGDB(sr, gdbName);
                if (db != null) {
                    int fdsID = (int) db.xClsIsExist(XClsType.XFds, dsName);
                    if (fdsID > 0) {
                        String gdbUrl = db.getURL();
                        this.m_ObserveList.clear();
                        {
//                        lookInComboBox.Items.Clear();
//                        lookInComboBox.CurrentItem = new LookInComboBoxItem(dsName, gdbUrl + "/ds/" + dsName, 0, MapGIS.Desktop.Resources.Png_FDs_16, MapGIS.Desktop.Resources.Png_FDs_16);
//                        lookInComboBox.Refresh();
                            updateGdbUI(url, serverName, true);
                        }
                        initClassView(db, fdsID, gdbUrl + "/ds/" + dsName);
                        this.m_DataView.setItems(this.m_ObserveList);
                        //List<int> fdsIDs = db.GetXclses(MapGIS.GeoDataBase.XClsType.Fds, -1);
                        //if (fdsIDs != null)
                        //{
                        //    foreach (int fdsID in fdsIDs)
                        //    {
                        //        if (string.Compare(db.GetXclsName(MapGIS.GeoDataBase.XClsType.Fds, fdsID), dsName, true) == 0)
                        //        {
                        //            InitClassView(db, fdsID, gdbUrl + "/ds/" + dsName);
                        //            break;
                        //        }
                        //    }
                        //}
                    } else {
                        String gdbUrl = db.getURL();
                        this.m_ObserveList.clear();
                        {
//                            lookInComboBox.Items.Clear();
//                            lookInComboBox.CurrentItem = new LookInComboBoxItem(db.Name, gdbUrl, 0, MapGIS.Desktop.Resources.Png_GDataBase_16, MapGIS.Desktop.Resources.Png_GDataBase_16);
//                            lookInComboBox.Refresh();
                            updateGdbUI(url, serverName, true);
                        }
                        initGDBListView(db);
                        initClassView(db, 0, gdbUrl);
                        initRdsListView(db, 0, gdbUrl);
                        initMdsListView(db, gdbUrl);    //镶嵌数据集支持 ch 20180122
                        //Init3dfclsListView(db, 0, gdbUrl);
                        initMapSetsListView(db, gdbUrl);
                        {
//                            InitFds_HTListView(db, gdbUrl);//海图数据支持 zkj-2019-6-25
                        }
                    }
                    db.close();
                } else {
                    this.m_ObserveList.clear();
                    {
//                        lookInComboBox.Items.Clear();
//                        lookInComboBox.CurrentItem = new LookInComboBoxItem(serverName, m_GDBProName + serverName, 0, MapGIS.Desktop.Resources.Png_GDBServer_16, MapGIS.Desktop.Resources.Png_GDBServer_16);
//                        lookInComboBox.Refresh();
                        updateGdbUI(url, serverName, false);
                    }
                    initGDBServerListView(sr);
                }
            }
        }
        //endregion
        //region 初始化数据库下面的栅格目录中数据列表
        if (serverName != null && gdbName != null && dsName == null && rCatName != null && d3fdsName == null) {
            Server sr = getServer(serverName);
            if (sr != null) {
                DataBase db = getGDB(sr, gdbName);
                if (db != null) {
                    int rCatID = (int) db.xClsIsExist(XClsType.XRcat, rCatName);
                    if (rCatID > 0) {
                        String gdbUrl = db.getURL();
                        this.m_ObserveList.clear();
                        //
                        {
//                            lookInComboBox.Items.Clear();
//                            lookInComboBox.CurrentItem = new LookInComboBoxItem(rCatName, gdbUrl + "/rcat/" + rCatName, 0, MapGIS.Desktop.Resources.Png_RasterCatalog_16, MapGIS.Desktop.Resources.Png_RasterCatalog_16);
//                            lookInComboBox.Refresh();
                        }
                        updateGdbUI(url, serverName, true);
                        initRdsListView(db, rCatID, gdbUrl + "/rcat/" + rCatName);
                        {
                            //List<int> fdsIDs = db.GetXclses(MapGIS.GeoDataBase.XClsType.Rcat, -1);
                            //if (fdsIDs != null)
                            //{
                            //    foreach (int fdsID in fdsIDs)
                            //    {
                            //        if (string.Compare(db.GetXclsName(MapGIS.GeoDataBase.XClsType.Rcat, fdsID), rCatName, true) == 0)
                            //        {
                            //            InitRdsListView(db, fdsID, gdbUrl + "/rcat/" + rCatName);
                            //            break;
                            //        }
                            //    }
                            //}
                        }
                    } else {
                        String gdbUrl = db.getURL();
                        this.m_ObserveList.clear();
                        {
//                            lookInComboBox.Items.Clear();
//                            lookInComboBox.CurrentItem = new LookInComboBoxItem(db.Name, gdbUrl, 0, MapGIS.Desktop.Resources.Png_GDataBase_16, MapGIS.Desktop.Resources.Png_GDataBase_16);
//                            lookInComboBox.Refresh();
                        }
                        updateGdbUI(url, serverName, true);
                        initGDBListView(db);
                        initClassView(db, 0, gdbUrl);
                        initRdsListView(db, 0, gdbUrl);
                        initMdsListView(db, gdbUrl);    //镶嵌数据集支持 ch 20180122
                        //Init3dfclsListView(db, 0, gdbUrl);
                        initMapSetsListView(db, gdbUrl);
                        {
//                            InitFds_HTListView(db, gdbUrl);//海图数据支持 zkj-2019-6-25
                        }
                    }
                    db.close();
                } else {
                    this.m_ObserveList.clear();
                    {
//                        lookInComboBox.Items.Clear();
//                        lookInComboBox.CurrentItem = new LookInComboBoxItem(serverName, m_GDBProName + serverName, 0, MapGIS.Desktop.Resources.Png_GDBServer_16, MapGIS.Desktop.Resources.Png_GDBServer_16);
//                        lookInComboBox.Refresh();
                    }
                    updateGdbUI(url, serverName, false);
                    initGDBServerListView(sr);
                }
            }
        }
        //endregion

    }

    /**
     * 更新选择数据时UI状态-显示当前目录、向上、新建、及删除按钮的可用性
     */
    private void updateGdbUI(String url, String serverName, boolean upButtonState) {
        //更新回退项
        {
//            if (m_suspendAddBackItem)
//                m_suspendAddBackItem = false;
//            else
//                AddBackItem(m_curMapGISDir);
        }
        btnOk.setDisable(false);
        btnUpOneLevel.setDisable(!upButtonState);
        m_curMapGISDir = url;
        btnDel.setDisable(true);
        btnCreateNewFolder.setDisable(true);
        m_CurViewType = CurViewType.GDB;

    }

    /**
     * 初始化Server下的GDB列表
     */
    private void initGDBServerListView(Server sr) {
        this.m_DataView.refresh();
        int[] gdbIDs = sr.getGdbs();
        if (gdbIDs != null && gdbIDs.length > 0) {
            for (int dbID : gdbIDs) {
                String dbName = sr.getDBName(dbID);
                DataInfo info = new DataInfo();
                info.setName(dbName);
                info.setUrl(sr.getURL() + "/" + dbName);
                info.setImage("4"); //设置图标列
                this.m_ObserveList.add(info);
            }
        }
    }

    /**
     * 初始化GDB下的目录(要素数据集、栅格目录、三维要素数据集)
     */
    private void initGDBListView(DataBase db) {
        if (db != null) {
            if (!(this.DialogType == FileDialogType.SaveFileDialog) || (this.DialogType == FileDialogType.SaveFileDialog && !includeXClsObject(XClsType.XRds) && !includeXClsObject(XClsType.XRcat) && !includeXClsObject(XClsType.X3DFCls))) {
                //region 要素数据集
                int[] fdsIDs = db.getXclses(XClsType.XFds, -1);
                if (fdsIDs != null && fdsIDs.length > 0) {
                    for (int fdsID : fdsIDs) {
                        //修改说明：海图数据从要素数据集中剔除，单独作为一种数据类型进行筛选，避免将其中的物标简单要素类等数据选中
                        //          判断要素数据集类型标识，2标识海图数据
                        // by-zkj
                        //int flag = db.GetFdsFlag(fdsID);
//                            #if VS2015
//                            if(!S57ChartDataset.IsS57Dataset(db, fdsID))
//                            {
//                                string name = db.GetXclsName(MapGIS.GeoDataBase.XClsType.Fds, fdsID);
//                                ListViewItem lvi = new ListViewItem(new string[] { name, Resources.String_FeatureDataset, "", "" }, 3);
//                                lvi.Name = db.URL + "/ds/" + name;
//                                lst.Add(lvi);
//                            }
//                            #else
                        {
                            String name = db.getXclsName(XClsType.XFds, fdsID);
                            DataInfo info = new DataInfo();
                            info.setName(name);
                            info.setType("要素数据集");
                            info.setUrl(db.getURL() + "/ds/" + name);
                            info.setImage("3"); //设置图标列
                            this.m_ObserveList.add(info);
                        }
//                            #endif
                    }
                }
                //endregion
            }
            if (!(this.DialogType == FileDialogType.SaveFileDialog) || (this.DialogType == FileDialogType.SaveFileDialog && (includeXClsObject(XClsType.XRds) || includeXClsObject(XClsType.XRcat))/* && CanSaveRdsInRcat()*/)) {
                //region 栅格目录
                int[] fdsIDs = db.getXclses(XClsType.XRcat, -1);
                if (fdsIDs != null) {
                    for (int fdsID : fdsIDs) {
                        String name = db.getXclsName(XClsType.XRcat, fdsID);
                        DataInfo info = new DataInfo();
                        info.setName(name);
                        info.setType("栅格目录");
                        info.setUrl(db.getURL() + "/rcat/" + name);
                        info.setImage("11");//设置图标列
                        this.m_ObserveList.add(info);
//                            ListViewItem lvi = new ListViewItem(new string[] { name, Resources.String_RasterCatalog, "", "" }, 11);
//                            lvi.Name = db.URL + "/rcat/" + name;
//                            lst.Add(lvi);
                    }
                }
                //endregion
            }

        }
    }

    /**
     * 获取简单要素类、注记类、网络类等矢量数据
     */
    private void initClassView(DataBase db, int dsID, String parPath) {
        if (db != null) {

            //region 简单要素类
            if (includeXClsObject(XClsType.XSFCls)) {
                int[] sfcsIDs = db.getXclses(XClsType.XSFCls, dsID);
                if (sfcsIDs != null) {
                    for (int sfcsID : sfcsIDs) {
                        try {
                            int imageIndex = 10;
                            SFClsInfo sfclsInfo = (SFClsInfo) db.getXclsInfo(XClsType.XSFCls, sfcsID);
                            if (sfclsInfo != null) {
                                String sfclsName = sfclsInfo.getName();
                                String sfclsUrl = parPath + "/sfcls/" + sfclsName;
                                String sfclsType = "简单要素类";// Resources.String_SFCls;
                                switch (sfclsInfo.getfType().value()) {
                                    case 1:
                                        sfclsType = "点简单要素类";//Resources.String_PSFCls;
                                        imageIndex = 7;
                                        break;
                                    case 2:
                                        sfclsType = "线简单要素类";//Resources.String_LSFCls;
                                        imageIndex = 5;
                                        break;
                                    case 3:
                                        sfclsType = "区简单要素类";// Resources.String_RSFCls;
                                        imageIndex = 9;
                                        break;
                                    case 11:
                                        sfclsType = "面简单要素类";//Resources.String_SSFCls;
                                        imageIndex = 15;
                                        break;
                                    case 12:
                                        sfclsType = "体简单要素类";//Resources.String_ESFCls;
                                        imageIndex = 16;
                                        break;
                                    default:
                                        break;
                                }
                                if (includeXClsObject(sfclsInfo.getType())) {
                                    DataInfo info = new DataInfo();
                                    info.setName(sfclsName);
                                    info.setUrl(sfclsUrl);
                                    info.setType(sfclsType);
                                    info.setImage(String.valueOf(imageIndex)); //设置图标列
                                    try {
                                        Calendar mtime = sfclsInfo.getModifyTime();
                                        Date date = mtime.getTime();
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                        String dateInfo = sdf.format(date);
                                        info.setMtime(dateInfo);
                                        Calendar ctime = sfclsInfo.getCreateTime();
                                        date = ctime.getTime();
                                        dateInfo = sdf.format(date);
                                        info.setCtime(dateInfo);
                                    } catch (Exception e) {

                                    }

                                    this.m_ObserveList.add(info);
//                                    ListViewItem lvi = new ListViewItem(new String[] { sfclsName, sfclsType, sfclsInfo.getModifyTime().ToString("yyyy-MM-dd HH:mm:ss"), sfclsInfo.CreateTime.ToString("yyyy-MM-dd HH:mm:ss") }, imageIndex);
//                                    lvi.Name = sfclsUrl;
//                                    lst.Add(lvi);
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
            //endregion

            //region 注记类
            if (includeXClsObject(XClsType.XACls)) {
                int[] aclsIDs = db.getXclses(XClsType.XACls, dsID);
                if (aclsIDs != null) {
                    for (int aclsID : aclsIDs) {
                        try {
                            AClsInfo aClsInfo = (AClsInfo) db.getXclsInfo(XClsType.XACls, aclsID);
                            if (aClsInfo != null) {
                                DataInfo info = new DataInfo();
                                info.setName(aClsInfo.getName());
                                info.setUrl(parPath + "/acls/" + aClsInfo.getName());
                                info.setType("注记类");
                                info.setImage("2");
                                try {
                                    Calendar mtime = aClsInfo.getModifyTime();
                                    Date date = mtime.getTime();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                    String dateInfo = sdf.format(date);
                                    info.setMtime(dateInfo);
                                    Calendar ctime = aClsInfo.getCreateTime();
                                    date = ctime.getTime();
                                    dateInfo = sdf.format(date);
                                    info.setCtime(dateInfo);
                                } catch (Exception e) {

                                }

                                this.m_ObserveList.add(info);
//                                ListViewItem lvi = new ListViewItem(new String[] { aClsInfo.Name, Resources.String_ACls, aClsInfo.ModifyTime.ToString("yyyy-MM-dd HH:mm:ss"), aClsInfo.CreateTime.ToString("yyyy-MM-dd HH:mm:ss") }, 2);
//                                lvi.Name = parPath + "/acls/" + aClsInfo.Name;
//                                lst.Add(lvi);
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
            //endregion

            //region 对象类
            if (includeXClsObject(XClsType.XOCls)) {
                int[] oclsIDs = db.getXclses(XClsType.XOCls, dsID);
                if (oclsIDs != null) {
                    for (int aclsID : oclsIDs) {
                        try {
                            OClsInfo oClsInfo = (OClsInfo) db.getXclsInfo(XClsType.XOCls, aclsID);
                            if (oClsInfo != null) {
                                DataInfo info = new DataInfo();
                                info.setName(oClsInfo.getName());
                                info.setUrl(parPath + "/ocls/" + oClsInfo.getName());
                                info.setType("对象类");
                                info.setImage("12");
                                try {
                                    Calendar mtime = oClsInfo.getModifyTime();
                                    Date date = mtime.getTime();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                    String dateInfo = sdf.format(date);
                                    info.setMtime(dateInfo);
                                    Calendar ctime = oClsInfo.getCreateTime();
                                    date = ctime.getTime();
                                    dateInfo = sdf.format(date);
                                    info.setCtime(dateInfo);
                                } catch (Exception e) {

                                }

                                this.m_ObserveList.add(info);

//                                ListViewItem lvi = new ListViewItem(new String[] { oClsInfo.Name, Resources.String_OCls, oClsInfo.ModifyTime.ToString("yyyy-MM-dd HH:mm:ss"), oClsInfo.CreateTime.ToString("yyyy-MM-dd HH:mm:ss") }, 12);
//                                lvi.Name = parPath + "/ocls/" + aClsInfo.Name;
//                                lst.Add(lvi);
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
            //endregion

            //region 关系类
            if (includeXClsObject(XClsType.XRCls)) {
                int[] rclsIDs = db.getXclses(XClsType.XRCls, dsID);
                if (rclsIDs != null) {
                    for (int rclsID : rclsIDs) {
                        try {
                            RClsInfo rClsInfo = (RClsInfo) db.getXclsInfo(XClsType.XRCls, rclsID);
                            if (rClsInfo != null) {
                                DataInfo info = new DataInfo();
                                info.setName(rClsInfo.getName());
                                info.setUrl(parPath + "/rcls/" + rClsInfo.getName());
                                info.setType("关系类");
                                info.setImage("18");
                                this.m_ObserveList.add(info);

//                                ListViewItem lvi = new ListViewItem(new String[] { rClsInfo.Name, Resources.String_RCls, "", "" }, 18);
//                                lvi.Name = parPath + "/rcls/" + rClsInfo.Name;
//                                lst.Add(lvi);
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
            //endregion

            // region 网络类
            if (includeXClsObject(XClsType.XGNet)) {
                int[] nclsIDs = db.getXclses(XClsType.XGNet, dsID);
                if (nclsIDs != null) {
                    for (int nclsID : nclsIDs) {
                        try {
                            GNetInfo gNetClsInfo = (GNetInfo) db.getXclsInfo(XClsType.XGNet, nclsID);
                            if (gNetClsInfo != null) {
                                DataInfo info = new DataInfo();
                                info.setName(gNetClsInfo.getName());
                                info.setUrl(parPath + "/ncls/" + gNetClsInfo.getName());
                                info.setType("网络类");
                                info.setImage("6");
                                this.m_ObserveList.add(info);

//                                ListViewItem lvi = new ListViewItem(new String[] { gNetClsInfo.Name, Resources.String_NCls, "", "" }, 6);
//                                lvi.Name = parPath + "/ncls/" + gNetClsInfo.Name;
//                                lst.Add(lvi);
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
            // #endregion

            // #region 三维简单要素类
            if (includeXClsObject(XClsType.XSFCls3D)) {
                int[] sfcsIDs = db.getXclses(XClsType.XSFCls3D, dsID);
                if (sfcsIDs != null) {
                    for (int sfcsID : sfcsIDs) {
                        try {
                            int imageIndex = 1;
                            SFClsInfo sfclsInfo = (SFClsInfo) db.getXclsInfo(XClsType.XSFCls3D, sfcsID);
                            if (sfclsInfo.getName() != null && sfclsInfo.getName().trim() != "") {
                                String sfclsName = sfclsInfo.getName();
                                String sfclsUrl = parPath + "/s3dfcls/" + sfclsName;
                                String sfclsType = "三维简单要素类"; // Resources.String_TDSFCls;
                                switch (sfclsInfo.getfType().value()) {
                                    case 3:
                                        sfclsType = "面三维简单要素类";//Resources.String_TDRSFCls;
                                        imageIndex = 0;
                                        break;
                                    default:
                                        break;
                                }
                                DataInfo info = new DataInfo();
                                info.setName(sfclsName);
                                info.setUrl(sfclsUrl);
                                info.setType(sfclsType);
                                info.setImage(String.valueOf(imageIndex));
                                this.m_ObserveList.add(info);

//                                ListViewItem lvi = new ListViewItem(new String[] { sfclsName, sfclsType, sfclsInfo.ModifyTime.ToString("yyyy-MM-dd HH:mm:ss"), sfclsInfo.CreateTime.ToString("yyyy-MM-dd HH:mm:ss") }, imageIndex);
//                                lvi.Name = sfclsUrl;
//                                lst.Add(lvi);
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
            //endregion

//            if (_CustomGDBItemStyle && CustomGDBItemStyleEvent != null)
//            {
//                CustomGDBItemStyleEvent(this, new CustomGDBItemStyleEventArgs(lst, this.customListView));
//            }
//            this.customListView.Items.AddRange(lst.ToArray());
        }
    }

    /**
     * 获取栅格数据集
     */
    private void initRdsListView(DataBase db, int rCatID, String parPath) {
//        #region 栅格数据集
        if (includeXClsObject(XClsType.XRds)) {
            int[] rdsIDes = db.getXclses(XClsType.XRds, rCatID);
            if (rdsIDes != null) {
                for (int rdsID : rdsIDes) {
                    RDsInfo rdsInfo = (RDsInfo) db.getXclsInfo(XClsType.XRds, rdsID);
                    if (rdsInfo != null) {
                        DataInfo info = new DataInfo();
                        info.setName(rdsInfo.getName());
                        info.setUrl(parPath + "/ras/" + rdsInfo.getName());
                        info.setType("栅格数据集");
                        info.setImage("8");
                        try {
                            Calendar mtime = rdsInfo.getModifyTime();
                            Date date = mtime.getTime();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            String dateInfo = sdf.format(date);
                            info.setMtime(dateInfo);
                            Calendar ctime = rdsInfo.getCreateTime();
                            date = ctime.getTime();
                            dateInfo = sdf.format(date);
                            info.setCtime(dateInfo);
                        } catch (Exception e) {

                        }

                        this.m_ObserveList.add(info);
                        {
//                        ListViewItem lvi = new ListViewItem(new string[] { rdsInfo.Name, Resources.String_RasterDataset, rdsInfo.ModifyTime.ToString("yyyy-MM-dd HH:mm:ss"), rdsInfo.CreateTime.ToString("yyyy-MM-dd HH:mm:ss") }, 8);
//                        lvi.Name = parPath + "/ras/" + rdsInfo.Name;
//                        lst.Add(lvi);
                        }
                    }
                }
//                if (_CustomGDBItemStyle && CustomGDBItemStyleEvent != null)
//                {
//                    CustomGDBItemStyleEvent(this, new CustomGDBItemStyleEventArgs(lst, this.customListView));
//                }
//                this.customListView.Items.AddRange(lst.ToArray());
            }
        }
//        #endregion
    }

    /**
     * 获取镶嵌数据集
     */
    private void initMdsListView(DataBase db, String parPath) {
        //region 镶嵌数据集
        if (includeXClsObject(XClsType.XMosaicDS)) {
            int[] mdsIDes = db.getXclses(XClsType.XMosaicDS, 0);
            if (mdsIDes != null) {
                for (int mdsID : mdsIDes) {
                    MDsInfo mdsInfo = (MDsInfo) db.getXclsInfo(XClsType.XMosaicDS, mdsID);
                    if (mdsInfo != null) {
                        DataInfo info = new DataInfo();
                        info.setName(mdsInfo.getName());
                        info.setUrl(parPath + "/mds/" + mdsInfo.getName());
                        info.setType("镶嵌数据集");
                        info.setImage("19");
                        try {
                            Calendar mtime = mdsInfo.getModifyTime();
                            Date date = mtime.getTime();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            String dateInfo = sdf.format(date);
                            info.setMtime(dateInfo);
                            Calendar ctime = mdsInfo.getCreateTime();
                            date = ctime.getTime();
                            dateInfo = sdf.format(date);
                            info.setCtime(dateInfo);
                        } catch (Exception e) {

                        }

                        this.m_ObserveList.add(info);
                    }
                }
            }
        }
        //endregion
    }

    /**
     * 初始化地图集
     */
    private void initMapSetsListView(DataBase db, String parPath) {
        if (includeXClsObject(XClsType.XMapSet)) {
            int[] mapSetclsIDs = db.getXclses(XClsType.XMapSet, -1);
            if (mapSetclsIDs != null) {
                for (int nclsID : mapSetclsIDs) {
                    MapSetInfo mapSetClsInfo = (MapSetInfo) db.getXclsInfo(XClsType.XMapSet, nclsID);
                    if (mapSetClsInfo != null) {
                        DataInfo info = new DataInfo();
                        info.setName(mapSetClsInfo.getName());
                        info.setUrl(parPath + "/mapset/" + mapSetClsInfo.getName());
                        info.setType("地图集");
                        info.setImage("13");
                        this.m_ObserveList.add(info);
                        {
//                        ListViewItem lvi = new ListViewItem(new string[] { gNetClsInfo.Name, Resources.String_Mapset, "", "" }, 13);
//                        lvi.Name = parPath + "/mapset/" + gNetClsInfo.Name;
//                        lst.Add(lvi);
                        }
                    }
                }
//                if (_CustomGDBItemStyle && CustomGDBItemStyleEvent != null)
//                {
//                    CustomGDBItemStyleEvent(this, new CustomGDBItemStyleEventArgs(lst, this.customListView));
//                }
//                this.customListView.Items.AddRange(lst.ToArray());
            }
        }
    }
    //endregion

    //region  内部类

    /**
     * 数据源树节点
     */
    class MySvrTreeItem
    {
        private String text;
        private String name;
        private boolean isGDBSvr;
        public MySvrTreeItem(String text,boolean isGDBSvr)
        {
            this.text = text;
            this.isGDBSvr = isGDBSvr;
            if (isGDBSvr)
                this.name = "GDBP://" + text;
            else
                this.name = text;
        }
        @Override
        public String toString()
        {
            return this.text;
        }

        public String getText()
        {
            return this.text;
        }
        public void setText(String val)
        {
            this.text = val;
        }
        public String getName()
        {
            return this.name;
        }
        public void setName(String val)
        {
            this.name = val;
        }
        public boolean isGDBSvr()
        {
            return this.isGDBSvr;
        }
        public void setGDBSvr(boolean val)
        {
            this.isGDBSvr = val;
        }
    }

    /**
     * 对话框类型
     */
    enum FileDialogType {
        OpenFileDialog,
        SaveFileDialog,
        SelectFolderDialog;

    }

    /**
     * 显示数据视图
     */
    enum CurViewType {
        /**
         * 当前视图为磁盘文件视图
         */
        Disk,
        /**
         * 当前视图为GDB视图
         */
        GDB,
        /**
         * 当前视图为最近打开文件视图
         */
        Recent,
        /**
         * 当前视图为表格文件视图
         */
        Table,
        /**
         * 当前视图为Coverage文件视图
         */
        Coverage,
        /**
         * 当前视图虚拟文件系统文件视图
         */
        VFS
    }

    /**
     * 数据列表
     */
//    class DataInfo {
//        public DataInfo() {
//
//        }
//
//        public String name;
//        public String type;
//        public String ctime;
//        public String mtime;
//        public String url;
//        public String image;
//
//        public String getImage()
//        {
//            return this.image;
//        }
//        public void setImage(String img)
//        {
//            this.image = img;
//        }
//
//        public String getName() {
//            return this.name;
//        }
//
//        public void setName(String val) {
//            this.name = val;
//        }
//
//        public String getType() {
//            return this.type;
//        }
//
//        public void setType(String val) {
//            this.type = val;
//        }
//
//        public String getCtime() {
//            return this.ctime;
//        }
//
//        public void setCtime(String val) {
//            this.ctime = val;
//        }
//
//        public String getMtime() {
//            return this.mtime;
//        }
//
//        public void setMtime(String val) {
//            this.mtime = val;
//        }
//
//        public String getUrl() {
//            return this.url;
//        }
//
//        public void setUrl(String val) {
//            this.url = val;
//        }
//    }

    /**
     * URL信息
     */
    class GDBUrlInfo {
        public GDBUrlInfo(String serverName, String gdbName, String dsName, String rCatName, String d3fdsName, String userName, String password) {
            this.serverName = serverName;
            this.gdbName = gdbName;
            this.dsName = dsName;
            this.rCatName = rCatName;
            this.d3fdsName = d3fdsName;
            this.userName = userName;
            this.password = password;
        }

        public String serverName;
        public String gdbName;
        public String dsName;
        public String rCatName;
        public String d3fdsName;
        public String userName;
        public String password;
    }

    /**
     * 下拉框文件过滤类型
     */
    private class FileType {
        //region Member Fields

        private String m_name;
        private String[] m_extensions;
        private String m_filterPattern;
        private boolean m_includeAllWinFiles;
        private boolean m_includeAllGDBFiles;
        //private boolean m_isGDBFile = true;
        private ArrayList<String> m_includeWinFileExt = new ArrayList<String>();
        private ArrayList<String> m_includeGDBFileExt = new ArrayList<String>();
        private boolean m_includeWinFolder = false;//是否包含Windows目录(此属性只为文件打开对话框支持目录选择服务)
        private boolean m_includeCoverage = false;//是否包含Coverage文件(此属性只为文件打开对话框支持Coverage文件选择服务)

        //endregion

        // Construction
        public FileType(String name, String[] extensions) {
            m_name = name;
            m_extensions = extensions;

            ArrayList<String> winList = new ArrayList<String>();
            ArrayList<String> gdbList = new ArrayList<String>();
            for (String str : extensions) {
                if (str == "WinFolder") {
                    m_includeWinFolder = true;
                } else if (str == "Coverage") {
                    m_includeCoverage = true;
                } else if (str.contains(".")) {
                    winList.add(str);
                } else {
                    gdbList.add(str);
                }
            }
            if (extensions.length == 1 && extensions[0] == "*") {
                m_includeAllGDBFiles = true;
                m_includeWinFileExt.add(".wt");
                m_includeWinFileExt.add(".wl");
                m_includeWinFileExt.add(".wp");
                m_includeWinFileExt.add(".msi");
                m_includeWinFileExt.add(".hdf");
                m_includeWinFileExt.add(".tdf");
                winList.add("*.wt");
                winList.add("*.wl");
                winList.add("*.wp");
                winList.add("*.msi");
                winList.add("*.hdf");
                winList.add("*.tdf");
            }
            if (extensions.length == 1 && extensions[0] == "*.*")
                m_includeAllWinFiles = true;

            for (String str : gdbList) {
                int indexThis = str.indexOf('.');
                if (indexThis == -1) {
                    if (!m_includeGDBFileExt.contains(str)) {
                        m_includeGDBFileExt.add(str);
                    }
                }
            }
            for (String str : winList) {
                int indexThis = str.indexOf('.');
                if (indexThis > -1 && str.length() > indexThis + 1) {
                    String newStr = str.substring(indexThis).toLowerCase();
                    if (!m_includeWinFileExt.contains(newStr)) {
                        m_includeWinFileExt.add(newStr);
                    }
                }
            }

            m_filterPattern = "^";
//            {
//                String[] winArray = (String[]) winList.toArray();
//                for (int i = 0; i < winArray.length; i++) {
//                    if (i > 0) m_filterPattern += "|";
//                    m_filterPattern += "(";
//                    for (int j = 0; j < winArray[i].length(); j++) {
//                        char c = winArray[i].charAt(j);
//                        switch (c) {
//                            case '*':
//                                m_filterPattern += ".*";
//                                break;
//                            case '?':
//                                m_filterPattern += ".";
//                                break;
//                            case '\\':
////                            if (j < winArray[i].length() - 1)
////                                m_filterPattern += Regex.Escape(winArray[i][++j].ToString());
//                                break;
//                            default:
////                            m_filterPattern += Regex.Escape(winArray[i][j].ToString());
//                                break;
//                        }
//                    }
//                    m_filterPattern += ")";
//                }
//                m_filterPattern += "$";
//            }
        }

        // Methods
        @Override
        public String toString() {
            return m_name;
        }

        public boolean isIncludeWinFileExt(String ext) {
            if (ext == null) {
                return false;
            }
            return m_includeWinFileExt.contains(ext.toLowerCase());
        }

        public boolean isIncludeGDBFileExt(String ext) {
            if (ext == null) {
                return false;
            }
            if (ext.toLowerCase() == "sfcls") {
                return m_includeGDBFileExt.contains("sfcls") | m_includeGDBFileExt.contains("sfclsp") |
                        m_includeGDBFileExt.contains("sfclsl") | m_includeGDBFileExt.contains("sfclsr") |
                        m_includeGDBFileExt.contains("sfclss") | m_includeGDBFileExt.contains("sfclse");
            } else if (ext.toLowerCase() == "sfclsp") {
                return m_includeGDBFileExt.contains("sfcls") | m_includeGDBFileExt.contains("sfclsp");
            } else if (ext.toLowerCase() == "sfclsl") {
                return m_includeGDBFileExt.contains("sfcls") | m_includeGDBFileExt.contains("sfclsl");
            } else if (ext.toLowerCase() == "sfclsr") {
                return m_includeGDBFileExt.contains("sfcls") | m_includeGDBFileExt.contains("sfclsr");
            } else if (ext.toLowerCase() == "sfclss") {
                return m_includeGDBFileExt.contains("sfcls") | m_includeGDBFileExt.contains("sfclss");
            } else if (ext.toLowerCase() == "sfclse") {
                return m_includeGDBFileExt.contains("sfcls") | m_includeGDBFileExt.contains("sfclse");
            }
            return m_includeGDBFileExt.contains(ext.toLowerCase());
        }

        public boolean isOnlyIncludeGDBFileExt(String ext) {
            boolean rtn = false;
            if (isIncludeGDBFileExt(ext) && m_includeGDBFileExt.size() == 1) {
                rtn = true;
            }
            return rtn;
        }

        // region Properties

        /**
         * Gets the friendly name of the filter.
         */
        public String getName() {
            return m_name;
        }

        /**
         * Gets the extensions of the file type.
         */
        public String[] getExtensions() {
            return m_extensions;
        }

        /**
         * Gets the filter pattern.
         */
        public String getFilterPattern() {
            return m_filterPattern;
        }

        /**
         * Gets a boolean value indicating weather to include all files.
         */
        public boolean isIncludeAllWinFiles() {
            return m_includeAllWinFiles;
        }

        public boolean isIncludeAllGDBFiles() {
            return m_includeAllGDBFiles;
        }

        /**
         * 磁盘文件扩展名列表
         */
        public ArrayList<String> getWinFileExt() {
            return m_includeWinFileExt;
        }

        /**
         * GDB文件扩展列表
         */
        public ArrayList<String> getGDBFileExt() {
            return m_includeGDBFileExt;
        }

        ///// <summary>
        ///// 当对话框为保存文件对话框时，判断此时是否为保存MapGIS文件，仅当对话框为保存文件对话框才使用此属性
        ///// </summary>
        //public boolean IsSaveMapGISFile()
        //{
        //    if (m_extensions != null && m_extensions.Length > 0)
        //    {
        //        if (m_extensions[0] != null)
        //            return !m_extensions[0].contains(".");
        //    }
        //    return false;
        //}

        /**
         * 此时文件类型是否包含磁盘文件
         */
        public boolean containWinFile() {
            if (m_includeAllWinFiles || m_includeWinFolder) {
                return true;
            }
            return m_includeWinFileExt.size() > 0;
        }

        /**
         * 此时文件类型是否包含MapGIS文件
         */
        public boolean containGDBFile() {
            if (m_includeAllGDBFiles) {
                return true;
            }
            return m_includeGDBFileExt.size() > 0;
        }

        /**
         * 是否包含Windows目录(此属性只为文件打开对话框支持目录选择服务)
         */
        public boolean containWinFolder() {
            return m_includeWinFolder;
        }

        /**
         * 是否为Coverage文件
         */
        public boolean IsCoverageFile() {
            return m_includeCoverage;
        }
        //endregion
    }

    /**
     * 连接数据源对话框-临时
     */
    private class ConnectServer {
        private Stage stage;
        private String _userName;
        private String _password;
        private String dsName;
        private Server server;
        private boolean isResultOk;

        public ConnectServer(Server server, String serverName) {
            this.dsName = serverName;
            this.server = server;
        }

        public Server getConnectedServer() {
            return this.server;
        }

        public String getUserName() {
            return this._userName;
        }

        public String getPassword() {
            return this._password;
        }

        public void showDialog() {
            stage = new Stage();
            GridPane gridPane = new GridPane();
            Scene scene = new Scene(gridPane);
            Text name = new Text("用户名：");
            Text password = new Text("密码：");
            TextField e_userName = new TextField();
            e_userName.setLayoutX(100);
            e_userName.setPrefWidth(150);
            PasswordField e_password = new PasswordField();
            e_password.setPrefWidth(150);
            e_password.setLayoutX(100);
            Button btnOk = new Button("确定");
            Button btnCancel = new Button("取消");
            btnOk.setPrefWidth(75);
            btnCancel.setPrefWidth(75);

            HBox hBox1 = new HBox();
            hBox1.getChildren().addAll(name, e_userName);
            HBox hBox2 = new HBox();
            hBox2.getChildren().addAll(password, e_password);
            HBox hBox3 = new HBox();
            hBox3.getChildren().addAll(btnOk, btnCancel);
            gridPane.add(hBox1, 0, 0);
            gridPane.add(hBox2, 0, 1);
            gridPane.add(hBox3, 0, 2);

            GridPane.setMargin(btnOk, new Insets(0, 0, 0, 100));
            gridPane.setVgap(10);
            gridPane.setHgap(10);
            gridPane.setAlignment(Pos.CENTER);

            stage.setHeight(200);
            stage.setWidth(300);
            stage.setTitle("连接到" + this.dsName);
            stage.setScene(scene);
            btnOk.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    String username = e_userName.getText();
                    String pwd = e_password.getText();
                    if (server.connect(dsName, username, pwd) > 0) {
                        _userName = username;
                        _password = pwd;
                        isResultOk = true;
                        stage.close();
                        // Platform.exit();
                    } else {
                        Dialog<ButtonType> dialog = new Dialog<ButtonType>();
                        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
                        dialog.setContentText("数据源连接失败，请输入正确的用户名和密码!");
                        dialog.show();
                    }
                }
            });
            btnCancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    stage.close();
                    // Platform.exit();
                }
            });

            stage.showAndWait();

        }

        public boolean isDialogResultOk() {
            return this.isResultOk;
        }

    }

    /**
     * 智能提示框
     **/
    private static class AutoMessageBox {
        public static void ShowAutoMessageBox(String message, String caption) {
            Dialog<ButtonType> dialog = new Dialog<ButtonType>();
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
            dialog.setHeaderText(null);
            dialog.setContentText(message);
            dialog.setTitle(caption);
            dialog.show();
        }
    }


    //endregion
}
