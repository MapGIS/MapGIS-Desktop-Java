package com.zondy.mapgis.gdbmanager.clientconfig;

import com.zondy.mapgis.base.MessageBox;
import com.zondy.mapgis.base.UIFunctions;
import com.zondy.mapgis.base.XFunctions;
import com.zondy.mapgis.base.XString;
import com.zondy.mapgis.controls.common.ButtonEdit;
import com.zondy.mapgis.controls.common.ButtonEditEvent;
import com.zondy.mapgis.controls.common.ZDComboBox;
import com.zondy.mapgis.geodatabase.config.*;
import com.zondy.mapgis.geodatabase.middleware.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.File;
import java.sql.NClob;
import java.util.Optional;

/**
 * @author CR
 * @file ClientConfigPane.java
 * @brief 客户端配置
 * @create 2019-12-10.
 */
public class ClientConfigDialog extends Dialog {
    //region 变量
    private final Image imageSucceed = new Image("/succeed_16.png");
    private final Image imageFail = new Image("/fail_16.png");
    private final Image imageEmpty = new Image("/empty_16.png");

    private final ButtonEdit beDirWork = new ButtonEdit();
    private final ButtonEdit beDirSlib = new ButtonEdit();
    private final ButtonEdit beDirClib = new ButtonEdit();
    private final ButtonEdit beDirTemp = new ButtonEdit();
    private final ButtonEdit beDirMedia = new ButtonEdit();
    private final ButtonEdit beDir6xSlib = new ButtonEdit();
    private final ButtonEdit beDir6xClib = new ButtonEdit();
    private final TextField cfgCacheSize = UIFunctions.newIntTextField();//未完成。有最大值。
    private final TextField cfgPGCount = UIFunctions.newIntTextField();
    private final TextField cfgMySQLCount = UIFunctions.newIntTextField();
    private final TextField cfgKingbaseCount = UIFunctions.newIntTextField();
    private final TextField cfgDMCount = UIFunctions.newIntTextField();
    private final TextField cfgPlusCount = UIFunctions.newIntTextField();
    private ZDComboBox<Long> cfgCacheGraph;
    private ZDComboBox<Long> cfgCacheMode;
    private ZDComboBox<Long> cfgFields;
    private ZDComboBox<Long> cfgResult;
    private ZDComboBox<Long> cfgPlusUpload;
    private final TextField midwareName = new TextField();
    private final TextField midwareManage = new TextField();
    private final TextField midwareControl = new TextField();
    private final TextField midwareConfig = new TextField();
    private ImageView midwareManageImage = new ImageView(imageEmpty);
    private ImageView midwareControlImage = new ImageView(imageEmpty);
    private ImageView midwareConfigImage = new ImageView(imageEmpty);
    private TableView<MiddleWareInfo> tableViewMidware;
    private GridPane gpMidware;
    private Button buttonUnReg;
    private Button buttonModify;
    private Button buttonCheck;
    private MiddleWareConfig middleWareConfig;//中间件操作类
    //endregion

    public ClientConfigDialog() {
        setTitle("客户端配置管理");

        //region 目录设置
        MapGisEnv gisEnv = EnvConfig.getGisEnv();
        this.beDirWork.setText(gisEnv != null ? gisEnv.getCur() : "");
        this.beDirWork.setTooltip(new Tooltip("地理数据库、地图文档等的默认保存目录。"));
        this.beDirWork.setOnButtonClick(this::selectDirectoryButtonClick);

        this.beDirSlib.setText(gisEnv != null ? gisEnv.getSlib() : "");
        this.beDirSlib.setTooltip(new Tooltip("MapGIS符号库、颜色库等文件所在的目录。"));
        this.beDirSlib.setOnButtonClick(this::selectDirectoryButtonClick);

        this.beDirClib.setText(gisEnv != null ? gisEnv.getClib() : "");
        this.beDirClib.setTooltip(new Tooltip("MapGIS字体库文件所在的目录。"));
        this.beDirClib.setOnButtonClick(this::selectDirectoryButtonClick);

        this.beDirTemp.setText(gisEnv != null ? gisEnv.getTemp() : "");
        this.beDirTemp.setTooltip(new Tooltip("存放MapGIS运行过程中所产生的临时文件的目录。"));
        this.beDirTemp.setOnButtonClick(this::selectDirectoryButtonClick);

        ConfigOption coMedia = EnvConfig.getConfigOptInfo(SysConfigType.MultiMedia_Directory);
        this.beDirMedia.setText((coMedia != null && coMedia.getValue() instanceof String) ? (String) coMedia.getValue() : "");
        this.beDirMedia.setTooltip(new Tooltip("存放多媒体属性数据的缺省目录。"));
        this.beDirMedia.setOnButtonClick(this::selectDirectoryButtonClick);
        if (coMedia != null) {
            coMedia.dispose();
        }

        ConfigOption co6xSlib = EnvConfig.getConfigOptInfo(SysConfigType.SLib6x_Directory);
        this.beDir6xSlib.setText((co6xSlib != null && co6xSlib.getValue() instanceof String) ? (String) co6xSlib.getValue() : "");
        this.beDir6xSlib.setTooltip(new Tooltip("MapGIS 6x数据显示所用的符号库、颜色库等文件所在的目录。"));
        this.beDir6xSlib.setOnButtonClick(this::selectDirectoryButtonClick);
        if (co6xSlib != null) {
            co6xSlib.dispose();
        }

        ConfigOption co6xClib = EnvConfig.getConfigOptInfo(SysConfigType.CLib6x_Directory);
        this.beDir6xClib.setText((co6xClib != null && co6xClib.getValue() instanceof String) ? (String) co6xClib.getValue() : "");
        this.beDir6xClib.setTooltip(new Tooltip("MapGIS 6x数据显示所用的字体库等文件所在的目录。"));
        this.beDir6xClib.setOnButtonClick(this::selectDirectoryButtonClick);
        if (co6xClib != null) {
            co6xClib.dispose();
        }

        GridPane dirPane = new GridPane();
        dirPane.setVgap(6);
        dirPane.setHgap(3);
        dirPane.add(new Label("工作目录:"), 0, 0);
        dirPane.add(new Label("系统库目录:"), 0, 1);
        dirPane.add(new Label("字体库目录:"), 0, 2);
        dirPane.add(new Label("系统临时目录:"), 0, 3);
        dirPane.add(new Label("多媒体数据目录:"), 0, 4);
        dirPane.add(new Label("6x系统库目录:"), 0, 5);
        dirPane.add(new Label("6x字体库目录:"), 0, 6);
        dirPane.add(this.beDirWork, 1, 0);
        dirPane.add(this.beDirSlib, 1, 1);
        dirPane.add(this.beDirClib, 1, 2);
        dirPane.add(this.beDirTemp, 1, 3);
        dirPane.add(this.beDirMedia, 1, 4);
        dirPane.add(this.beDir6xSlib, 1, 5);
        dirPane.add(this.beDir6xClib, 1, 6);

        Tab tabDir = new Tab("目录设置", dirPane);
        tabDir.setClosable(false);
        dirPane.setPadding(new Insets(12, 0, 0, 0));
        GridPane.setHgrow(this.beDirWork, Priority.ALWAYS);
        //endregion

        //region 配置信息
        ConfigOption coCacheMode = EnvConfig.getConfigOptInfo(SysConfigType.Cache_Module);
        this.cfgCacheMode = new ZDComboBox<Long>(FXCollections.observableArrayList(0L, 1L, 2L, 3L));
        this.cfgCacheMode.setValue(coCacheMode != null ? (long) coCacheMode.getValue() : 0L);
        this.cfgCacheMode.setTooltip(new Tooltip("设置数据在客户端的缓存模式。\n“无”模式提供较低的系统性能，但却有较高的安全性，适合成块的频率不高的读写操作。\n“不限制”模式使客户端对数据的操作完全缓存在客户端，适合数据量小但性能要求十分高的场合。\n“限量淘汰”模式采用定额的缓存分配策略，在系统缓存资源不足时，通过淘汰部分页面实现空间的复用，适合大数据的缓存。"));
        this.cfgCacheMode.setConverter(new StringConverter<Long>() {
            @Override
            public String toString(Long object) {
                String string = "无";
                if (object == 1) {
                    string = "不限量";
                } else if (object == 2) {
                    string = "限量淘汰";
                } else if (object == 3) {
                    string = "自定义";
                }
                return string;
            }

            @Override
            public Long fromString(String string) {
                long i = 0;
                if ("不限量".equals(string)) {
                    i = 1;
                } else if ("限量淘汰".equals(string)) {
                    i = 2;
                } else if ("自定义".equals(string)) {
                    i = 3;
                }
                return i;
            }
        });
        if (coCacheMode != null) {
            coCacheMode.dispose();
        }

        ConfigOption coCacheSize = EnvConfig.getConfigOptInfo(SysConfigType.Cache_Pagepoolsize);
        this.cfgCacheSize.setText((coCacheSize != null && coCacheSize.getValue() != null) ? coCacheSize.getValue().toString() : "4096");
        this.cfgCacheSize.setTooltip(new Tooltip("设置缓存内存用量，只有在使用“限量淘汰”时才有意义。\n每一个客户端实例都有自己的全局缓存池，缓存按页进行分配，一页包含8k的内存空间，增加缓存页数值可以在一定程度上提高系统的速度和性能，但所有客户端缓存页之和不能超过当前可用的物理内存页数。"));
        if (coCacheSize != null) {
            coCacheSize.dispose();
        }

        ConfigOption coCacheGraph = EnvConfig.getConfigOptInfo(SysConfigType.Graph_Cache);
        this.cfgCacheGraph = new ZDComboBox<Long>(FXCollections.observableArrayList(1L, 0L));
        this.cfgCacheGraph.setValue(coCacheGraph != null ? (long) coCacheGraph.getValue() : 0L);
        this.cfgCacheGraph.setTooltip(new Tooltip("设置是否要在客户端缓存图形信息。\n在客户端缓存图形信息可以避免系统频繁的访问服务器以提取相关信息，从而可在一定程度上提高系统的速度和性能。"));
        this.cfgCacheGraph.setConverter(new StringConverter<Long>() {
            @Override
            public String toString(Long object) {
                return object == 1 ? "是" : "否";
            }

            @Override
            public Long fromString(String string) {
                return string.equals("是") ? 1L : 0L;
            }
        });
        if (coCacheGraph != null) {
            coCacheGraph.dispose();
        }

        ConfigOption coFields = EnvConfig.getConfigOptInfo(SysConfigType.Display_Field);
        this.cfgFields = new ZDComboBox<Long>(FXCollections.observableArrayList(1L, 2L));
        this.cfgFields.setValue(coFields != null ? (long) coFields.getValue() : 1L);
        this.cfgFields.setTooltip(new Tooltip("设置在查看/编辑数据属性记录时属性字段名称的显示内容。别名模式下如果字段没有别名，则显示名称。"));
        this.cfgFields.setConverter(new StringConverter<Long>() {
            @Override
            public String toString(Long object) {
                String string = "字段名称";
                if (object == 2) {
                    string = "字段别名";
                }
                return string;
            }

            @Override
            public Long fromString(String string) {
                long i = 1;
                if ("字段别名".equals(string)) {
                    i = 2;
                }
                return i;
            }
        });
        if (coFields != null) {
            coFields.dispose();
        }

        ConfigOption coResult = EnvConfig.getConfigOptInfo(SysConfigType.Set_Cache_Size);
        this.cfgResult = new ZDComboBox<Long>(FXCollections.observableArrayList(0L, 1L, 2L));
        this.cfgResult.setValue(coResult != null ? (long) coResult.getValue() : 1L);
        this.cfgResult.setTooltip(new Tooltip("“低”为5M，适用并发进程较多的场景。\n“中”为50M，适用并发进程较少，机器性能较好的场景。\n“高”为500M，适用机器性能非常好，机器内存大的场景。"));
        this.cfgResult.setConverter(new StringConverter<Long>() {
            @Override
            public String toString(Long object) {
                String string = "低";
                if (object == 1) {
                    string = "中";
                } else if (object == 2) {
                    string = "高";
                }
                return string;
            }

            @Override
            public Long fromString(String string) {
                long i = 0;
                if ("中".equals(string)) {
                    i = 1;
                } else if ("高".equals(string)) {
                    i = 2;
                }
                return i;
            }
        });
        if (coResult != null) {
            coResult.dispose();
        }

        ConfigOption coPGCount = EnvConfig.getConfigOptInfo(SysConfigType.GDBSERVER_PG_CONNECTPOOL_SIZE);
        this.cfgPGCount.setText((coPGCount != null && coPGCount.getValue() != null) ? coPGCount.getValue().toString() : "10");
        if (coPGCount != null) {
            coPGCount.dispose();
        }

        ConfigOption coMySQLCount = EnvConfig.getConfigOptInfo(SysConfigType.GDBSERVER_MYSQL_CONNECTPOOL_SIZE);
        this.cfgMySQLCount.setText((coMySQLCount != null && coMySQLCount.getValue() != null) ? coMySQLCount.getValue().toString() : "10");
        if (coMySQLCount != null) {
            coMySQLCount.dispose();
        }

        ConfigOption coKingbaseCount = EnvConfig.getConfigOptInfo(SysConfigType.GDBSERVER_KBS_CONNECTPOOL_SIZE);
        this.cfgKingbaseCount.setText((coKingbaseCount != null && coKingbaseCount.getValue() != null) ? coKingbaseCount.getValue().toString() : "10");
        if (coKingbaseCount != null) {
            coKingbaseCount.dispose();
        }

        ConfigOption coDMCount = EnvConfig.getConfigOptInfo(SysConfigType.GDBSERVER_DM_CONNECTPOOL_SIZE);
        this.cfgDMCount.setText((coDMCount != null && coDMCount.getValue() != null) ? coDMCount.getValue().toString() : "32");
        if (coDMCount != null) {
            coDMCount.dispose();
        }

        ConfigOption coPlusCount = EnvConfig.getConfigOptInfo(SysConfigType.GDBSERVER_SQLITE_CONNECTPOOL_SIZE);
        this.cfgPlusCount.setText((coPlusCount != null && coPlusCount.getValue() != null) ? coPlusCount.getValue().toString() : "20");
        if (coPlusCount != null) {
            coPlusCount.dispose();
        }

        ConfigOption coPlusUpload = EnvConfig.getConfigOptInfo(SysConfigType.GDBSERVER_SQLITE_UPLOADMODE);
        this.cfgPlusUpload = new ZDComboBox<Long>(FXCollections.observableArrayList(0L, 1L));
        this.cfgPlusUpload.setValue(coPlusUpload != null ? (long) coPlusUpload.getValue() : 1L);
        this.cfgPlusUpload.setTooltip(new Tooltip("安全上载模式下，在上载过程中出现断电或系统崩溃等问题时，可保证数据的安全性。\n快速上载模式下，在断电或系统崩溃的场景下不保证数据的安全性，但上载速度是安全模式的2~3倍。"));
        this.cfgPlusUpload.setConverter(new StringConverter<Long>() {
            @Override
            public String toString(Long object) {
                String string = "快速模式";
                if (object == 1) {
                    string = "安全模式";
                }
                return string;
            }

            @Override
            public Long fromString(String string) {
                long i = 0;
                if ("安全模式".equals(string)) {
                    i = 1;
                }
                return i;
            }
        });
        if (coPlusUpload != null) {
            coPlusUpload.dispose();
        }

        GridPane configPane = new GridPane();
        configPane.setVgap(6);
        configPane.setHgap(6);
        configPane.add(new Label("客户端缓存模式:"), 0, 2);
        configPane.add(new Label("客户端缓存规模:"), 0, 0);
        configPane.add(new Label("是否缓冲图形信息:"), 0, 1);
        configPane.add(new Label("字段显示方式:"), 0, 3);
        configPane.add(new Label("结果集缓存大小:"), 0, 4);
        configPane.add(new Label("PG数据源单用户占用连接数:"), 0, 5);
        configPane.add(new Label("MySQL数据源单用户占用连接数:"), 0, 6);
        configPane.add(new Label("KINGBASE数据源单用户占用连接数:"), 0, 7);
        configPane.add(new Label("DM数据源单用户占用连接数:"), 0, 8);
        configPane.add(new Label("MapGISLocalPlus数据源单用户占用连接数:"), 0, 9);
        configPane.add(new Label("MapGISLocalPlus数据源数据上载模式:"), 0, 10);
         configPane.add(this.cfgCacheSize, 1, 1);
        configPane.add(this.cfgCacheGraph, 1, 2);
        configPane.add(this.cfgFields, 1, 3);
        configPane.add(this.cfgResult, 1, 4);
        configPane.add(this.cfgPGCount, 1, 5);
        configPane.add(this.cfgMySQLCount, 1, 6);
        configPane.add(this.cfgKingbaseCount, 1, 7);
        configPane.add(this.cfgDMCount, 1, 8);
        configPane.add(this.cfgPlusCount, 1, 9);
        configPane.add(this.cfgPlusUpload, 1, 10);
        configPane.add(this.cfgCacheMode, 1, 0);//ComboBox需要放在其bind的对象后面添加，否则，在linux下面首次弹出来长度不对。

        Tab tabConfig = new Tab("配置信息", configPane);
        tabConfig.setClosable(false);
        configPane.setPadding(new Insets(12, 0, 0, 0));
        GridPane.setHgrow(this.cfgCacheSize, Priority.ALWAYS);
        this.cfgCacheGraph.prefWidthProperty().bind(this.cfgCacheSize.widthProperty());
        this.cfgCacheMode.prefWidthProperty().bind(this.cfgCacheSize.widthProperty());
        this.cfgFields.prefWidthProperty().bind(this.cfgCacheSize.widthProperty());
        this.cfgResult.prefWidthProperty().bind(this.cfgCacheSize.widthProperty());
        this.cfgPlusUpload.prefWidthProperty().bind(this.cfgCacheSize.widthProperty());
        //endregion

        //region 中间件
        ObservableList<MiddleWareInfo> midwares = FXCollections.observableArrayList();
        this.middleWareConfig = new MiddleWareConfig();
        this.middleWareConfig.open();
        for (int i = 0; i < this.middleWareConfig.count(); i++) {
            MiddleWareInfo midWare = this.middleWareConfig.getItemByIndex(i);
            if (midWare != null) {
                midwares.add(midWare);
            }
        }
        this.middleWareConfig.close();
        this.tableViewMidware = new TableView<>(midwares);

        this.tableViewMidware.setPrefHeight(150);
        TableColumn<MiddleWareInfo, String> colName = new TableColumn<>("名称");
        colName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getDisplayName()));
        TableColumn<MiddleWareInfo, String> colDescription = new TableColumn<>("描述");
        colDescription.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getDescription()));
        TableColumn<MiddleWareInfo, String> colOGDC = new TableColumn<>("OGDC版本");
        colOGDC.setCellValueFactory(param -> {
            if (param.getValue().getModeDesc() != null) {
                return new SimpleStringProperty(param.getValue().getModeDesc().getOGDCVersion());
            } else {
                return null;
            }
        });
        TableColumn<MiddleWareInfo, String> colDate = new TableColumn<>("注册日期");
        colDate.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getRegistDate()));
        this.tableViewMidware.getColumns().addAll(colName, colDescription, colOGDC, colDate);
        this.tableViewMidware.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.tableViewMidware.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            ClientConfigDialog.this.midwareManageImage.setImage(imageEmpty);
            ClientConfigDialog.this.midwareControlImage.setImage(imageEmpty);
            ClientConfigDialog.this.midwareConfigImage.setImage(imageEmpty);
            ClientConfigDialog.this.midwareName.setText((newValue != null && newValue.getModeDesc() != null) ? newValue.getModeDesc().getName() : "");
            ClientConfigDialog.this.midwareManage.setText(newValue != null ? newValue.getManageDLL() : "");
            ClientConfigDialog.this.midwareControl.setText(newValue != null ? newValue.getXClsDLL() : "");
            ClientConfigDialog.this.midwareConfig.setText(newValue != null ? newValue.getConfigDLL() : "");

            ClientConfigDialog.this.buttonUnReg.setDisable(newValue == null);
            ClientConfigDialog.this.buttonModify.setDisable(newValue == null);
            ClientConfigDialog.this.buttonCheck.setDisable(newValue == null);
        });

        this.midwareName.setDisable(true);
        this.midwareManage.setDisable(true);
        this.midwareControl.setDisable(true);
        this.midwareConfig.setDisable(true);
        this.gpMidware = new GridPane();
        this.gpMidware.setHgap(6);
        this.gpMidware.setVgap(6);
        this.gpMidware.add(new Label("内部名称:"), 0, 0);
        this.gpMidware.add(new Label("管理模块:"), 0, 1);
        this.gpMidware.add(new Label("控制模块:"), 0, 2);
        this.gpMidware.add(new Label("配置模块:"), 0, 3);
        this.gpMidware.add(this.midwareName, 1, 0);
        this.gpMidware.add(this.midwareManage, 1, 1);
        this.gpMidware.add(this.midwareControl, 1, 2);
        this.gpMidware.add(this.midwareConfig, 1, 3);
        this.gpMidware.add(this.midwareManageImage, 2, 1);
        this.gpMidware.add(this.midwareControlImage, 2, 2);
        this.gpMidware.add(this.midwareConfigImage, 2, 3);
        GridPane.setHgrow(this.midwareName, Priority.ALWAYS);
        TitledPane titledPane = new TitledPane("模块信息", this.gpMidware);
        titledPane.setCollapsible(false);

        VBox vBoxButton = new VBox(6);
        Button buttonReg = new Button("注册...");
        buttonReg.setPrefWidth(75);
        buttonReg.setOnAction(event ->
        {
            MidwareInfoDialog dlg = new MidwareInfoDialog();
            dlg.initOwner(this.getCurrentWindow());
            if (dlg.showAndWait().equals(Optional.of(ButtonType.OK))) {
                this.middleWareConfig.open();
                MiddleWareInfo midWare = dlg.getMiddleWareInfo();
                if (midWare != null && this.middleWareConfig.addItem(midWare)) {
                    midWare = this.middleWareConfig.getItemByName(midWare.getDisplayName());
                    this.tableViewMidware.getItems().add(midWare);
                    this.tableViewMidware.getSelectionModel().select(midWare);
                } else {
                    MessageBox.information("中间件添加失败，请确保MiddleWare_Cfg.xml文件的读写权限。", this.getCurrentWindow());
                }
                this.middleWareConfig.close();
            }
        });

        this.buttonUnReg = new Button("注销");
        this.buttonUnReg.setPrefWidth(75);
        this.buttonUnReg.setOnAction(event ->
        {
            MiddleWareInfo mwInfo = this.tableViewMidware.getFocusModel().getFocusedItem();
            if (mwInfo != null) {
                if (this.isMiddlewareBeUsed(mwInfo)) {
                    MessageBox.warning("有数据源在使用该中间件,不可以注销。", this.getCurrentWindow());
                } else {
                    if (ButtonType.OK == MessageBox.question("确定要注销所选中间件吗?", this.getCurrentWindow())) {
                        this.middleWareConfig.open();
                        if (this.middleWareConfig.deleteItem(mwInfo.getID())) {
                            this.tableViewMidware.getItems().remove(mwInfo);
                        }
                        this.middleWareConfig.close();
                    }
                }
            }
        });

        this.buttonModify = new Button("修改...");
        this.buttonModify.setPrefWidth(75);
        this.buttonModify.setOnAction(event ->
        {
            MiddleWareInfo mwInfo = this.tableViewMidware.getFocusModel().getFocusedItem();
            if (mwInfo != null) {
                if (this.isMiddlewareBeUsed(mwInfo)) {
                    MessageBox.warning("有数据源在使用该中间件,不可以修改。", this.getCurrentWindow());
                } else {
                    MidwareInfoDialog dlg = new MidwareInfoDialog(mwInfo);
                    dlg.initOwner(this.getCurrentWindow());
                    if (dlg.showAndWait().equals(Optional.of(ButtonType.OK))) {
                        this.middleWareConfig.open();
                        mwInfo = dlg.getMiddleWareInfo();
                        if (mwInfo != null && this.middleWareConfig.updateItem(mwInfo.getID(), mwInfo)) {
                            //未完成。调试看看表中内容是否修改了。没有的话需要重设。
                            this.tableViewMidware.refresh();
                        }
                        this.middleWareConfig.close();
                    }
                }
            }
        });

        this.buttonCheck = new Button("检查");
        this.buttonCheck.setPrefWidth(75);
        this.buttonCheck.setOnAction(event ->
        {
            boolean rtnManage = false;
            boolean rtnControl = false;
            boolean rtnConfig = false;
            MiddleWareInfo mwInfo = this.tableViewMidware.getFocusModel().getFocusedItem();
            if (mwInfo != null) {
                MiddleWareConfigTool mwcTool = new MiddleWareConfigTool();
                if (mwcTool.init(mwInfo.getDisplayName(), null, null, mwInfo.getManageDLL(), mwInfo.getXClsDLL(), mwInfo.getConfigDLL())) {
                    Object[] mng = mwcTool.checkMidwareModule(MidwareModType.Module_Type_Mng);
                    Object[] ctl = mwcTool.checkMidwareModule(MidwareModType.Module_Type_Ctl);
                    Object[] cfg = mwcTool.checkMidwareModule(MidwareModType.Module_Type_Cfg);
                    if (mng != null && mng.length == 2 && ctl != null && ctl.length == 2 && cfg != null && cfg.length == 2) {
                        rtnManage = (boolean) mng[0];
                        rtnControl = (boolean) ctl[0];
                        rtnConfig = (boolean) cfg[0];
                    }

                    if (rtnManage && rtnControl && rtnConfig) {
                        boolean temp = (mng[1] == ctl[1] && mng[1] == cfg[1]) || (cfg[1] == MiddleWareType.ArcLocal && mng[1] == MiddleWareType.ArcSDE && ctl[1] == MiddleWareType.ArcSDE);
                        rtnManage = temp;
                        rtnControl = temp;
                        rtnConfig = temp;
                    }
                }
                mwcTool.dispose();
            }
            this.midwareManageImage.setImage(rtnManage ? imageSucceed : imageFail);
            this.midwareControlImage.setImage(rtnControl ? imageSucceed : imageFail);
            this.midwareConfigImage.setImage(rtnConfig ? imageSucceed : imageFail);
        });

        vBoxButton.getChildren().addAll(buttonReg, this.buttonUnReg, this.buttonModify, this.buttonCheck);

        HBox hBox = new HBox(6);
        hBox.getChildren().addAll(titledPane, vBoxButton);
        HBox.setHgrow(titledPane, Priority.ALWAYS);

        VBox vBoxMidware = new VBox(6);
        vBoxMidware.getChildren().addAll(this.tableViewMidware, hBox);
        VBox.setVgrow(this.tableViewMidware, Priority.ALWAYS);
        vBoxMidware.setPadding(new Insets(12, 0, 0, 0));

        Tab tabMidware = new Tab("中间件", vBoxMidware);
        tabMidware.setClosable(false);
        //endregion

        TabPane tabPane = new TabPane(tabDir, tabConfig, tabMidware);

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setContent(tabPane);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.setPrefSize(550, 500);
        dialogPane.setMinSize(550, 500);
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, this::okButtonClick);

        if (this.tableViewMidware.getItems().size() > 0) {
            this.tableViewMidware.getSelectionModel().select(0);
        }
    }

    /**
     * 目录设置中的选择目录
     *
     * @param event 事件参数
     */
    public void selectDirectoryButtonClick(ButtonEditEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择目录");
        ButtonEdit buttonEdit = (ButtonEdit) event.getSource();
        File initPath = new File(buttonEdit.getText());
        if (initPath.isDirectory()) {
            directoryChooser.setInitialDirectory(initPath);
        }
        File directory = directoryChooser.showDialog(this.getCurrentWindow());
        if (directory != null) {
            String path = directory.getAbsolutePath();
            if (XString.getStringByteLength(path) >= 128) {
                MessageBox.information("目录路径的长度必须少于128个字符。", this.getCurrentWindow());
            } else {
                buttonEdit.setText(path);
            }
        }
    }

    public boolean validateInput() {
        //region 目录设置
        String pathWork = this.beDirWork.getText();
        if (XString.isNullOrEmpty(pathWork)) {
            MessageBox.information("工作目录不能为空。", this.getCurrentWindow());
            return false;
        } else if (!(new File(pathWork)).exists()) {
            MessageBox.information("工作目录所设置的路径不存在。", this.getCurrentWindow());
            return false;
        }

        String pathSlib = this.beDirSlib.getText();
        if (XString.isNullOrEmpty(pathSlib)) {
            MessageBox.information("系统库目录不能为空。", this.getCurrentWindow());
            return false;
        } else if (!(new File(pathSlib)).exists()) {
            MessageBox.information("系统库目录所设置的路径不存在。", this.getCurrentWindow());
            return false;
        }

        String pathClib = this.beDirClib.getText();
        if (XString.isNullOrEmpty(pathClib)) {
            MessageBox.information("字体库目录不能为空。", this.getCurrentWindow());
            return false;
        } else if (!(new File(pathClib)).exists()) {
            MessageBox.information("字体库目录所设置的路径不存在。", this.getCurrentWindow());
            return false;
        }

        String pathTemp = this.beDirTemp.getText();
        if (XString.isNullOrEmpty(pathTemp)) {
            MessageBox.information("临时目录不能为空。", this.getCurrentWindow());
            return false;
        } else if (!(new File(pathTemp)).exists()) {
            MessageBox.information("临时目录所设置的路径不存在。", this.getCurrentWindow());
            return false;
        }

        String pathMedia = this.beDirMedia.getText();
        if (XString.isNullOrEmpty(pathMedia)) {
            MessageBox.information("多媒体目录不能为空。", this.getCurrentWindow());
            return false;
        } else if (!(new File(pathMedia)).exists()) {
            MessageBox.information("多媒体目录所设置的路径不存在。", this.getCurrentWindow());
            return false;
        }

        String path6xSlib = this.beDir6xSlib.getText();
        if (!XString.isNullOrEmpty(path6xSlib) && !(new File(path6xSlib)).exists()) {
            MessageBox.information("6x系统库目录所设置的路径不存在。", this.getCurrentWindow());
            return false;
        }

        String path6xClib = this.beDir6xClib.getText();
        if (!XString.isNullOrEmpty(path6xClib) && !(new File(path6xClib)).exists()) {
            MessageBox.information("6x字体库目录所设置的路径不存在。", this.getCurrentWindow());
            return false;
        }
        //endregion

        //region 配置信息
        try {
            long cacheSize = Long.valueOf(this.cfgCacheSize.getText());
            if (cacheSize <= 0) {
                MessageBox.information("客户端缓存规模必须为正整数。", this.getCurrentWindow());
                return false;
            } else {
                long freePage = Long.MAX_VALUE;//未完成。计算可用的物理内存页数
                if (cacheSize > freePage) {
                    MessageBox.information("客户端缓存规模不能超过当前可用的物理内存页数。", this.getCurrentWindow());
                }
            }
        } catch (Exception ex) {
            MessageBox.information(ex.getMessage(), this.getCurrentWindow());
            return false;
        }
        //endregion

        return true;
    }

    /**
     * 确定，保存更改。其中中间件是即时保存的。
     *
     * @param event
     */
    private void okButtonClick(ActionEvent event) {
        if (this.validateInput()) {
            //region 保存目录设置
            MapGisEnv gisEnv = EnvConfig.getGisEnv();
            if (gisEnv != null) {
                gisEnv.setCur(this.beDirWork.getText());
                gisEnv.setSlib(this.beDirSlib.getText());
                gisEnv.setClib(this.beDirClib.getText());
                gisEnv.setTemp(this.beDirTemp.getText());
            }
            EnvConfig.setGisEnv(gisEnv);

            ConfigOption coMedia = EnvConfig.getConfigOptInfo(SysConfigType.MultiMedia_Directory);
            if (coMedia != null) {
                coMedia.setValue(this.beDirMedia.getText());
                EnvConfig.setConfigOptInfo(SysConfigType.MultiMedia_Directory, coMedia);
                coMedia.dispose();
            }

            ConfigOption coSLib6x = EnvConfig.getConfigOptInfo(SysConfigType.SLib6x_Directory);
            if (coSLib6x != null) {
                coSLib6x.setValue(this.beDir6xSlib.getText());
                EnvConfig.setConfigOptInfo(SysConfigType.SLib6x_Directory, coSLib6x);
                coSLib6x.dispose();
            }

            ConfigOption coCLib6x = EnvConfig.getConfigOptInfo(SysConfigType.CLib6x_Directory);
            if (coCLib6x != null) {
                coCLib6x.setValue(this.beDir6xClib.getText());
                EnvConfig.setConfigOptInfo(SysConfigType.CLib6x_Directory, coCLib6x);
                coCLib6x.dispose();
            }
            //endregion

            //region 保存配置信息
            ConfigOption coCacheSize = EnvConfig.getConfigOptInfo(SysConfigType.Cache_Pagepoolsize);
            if (coCacheSize != null) {
                coCacheSize.setValue(this.cfgCacheSize.getText());
                EnvConfig.setConfigOptInfo(SysConfigType.Cache_Pagepoolsize, coCacheSize);
                coCacheSize.dispose();
            }

            ConfigOption coCacheGraph = EnvConfig.getConfigOptInfo(SysConfigType.Graph_Cache);
            if (coCacheGraph != null) {
                coCacheGraph.setValue(this.cfgCacheGraph.getValue());
                EnvConfig.setConfigOptInfo(SysConfigType.Graph_Cache, coCacheGraph);
                coCacheGraph.dispose();
            }

            ConfigOption coCacheMode = EnvConfig.getConfigOptInfo(SysConfigType.Cache_Module);
            if (coCacheMode != null) {
                coCacheMode.setValue(this.cfgCacheMode.getValue());
                EnvConfig.setConfigOptInfo(SysConfigType.Cache_Module, coCacheMode);
                coCacheMode.dispose();
            }

            ConfigOption coFieldDisplay = EnvConfig.getConfigOptInfo(SysConfigType.Display_Field);
            if (coFieldDisplay != null) {
                coFieldDisplay.setValue(this.cfgFields.getValue());
                EnvConfig.setConfigOptInfo(SysConfigType.Display_Field, coFieldDisplay);
                coFieldDisplay.dispose();
            }

            ConfigOption coResult = EnvConfig.getConfigOptInfo(SysConfigType.Set_Cache_Size);
            if (coResult != null) {
                coResult.setValue(this.cfgResult.getValue());
                EnvConfig.setConfigOptInfo(SysConfigType.Set_Cache_Size, coResult);
                coResult.dispose();
            }

            ConfigOption coPGCount = EnvConfig.getConfigOptInfo(SysConfigType.GDBSERVER_PG_CONNECTPOOL_SIZE);
            if (coPGCount != null) {
                coPGCount.setValue(Long.valueOf(this.cfgPGCount.getText()));
                EnvConfig.setConfigOptInfo(SysConfigType.GDBSERVER_PG_CONNECTPOOL_SIZE, coPGCount);
                coPGCount.dispose();
            }

            ConfigOption coMySqlCount = EnvConfig.getConfigOptInfo(SysConfigType.GDBSERVER_MYSQL_CONNECTPOOL_SIZE);
            if (coMySqlCount != null) {
                coMySqlCount.setValue(Long.valueOf(this.cfgMySQLCount.getText()));
                EnvConfig.setConfigOptInfo(SysConfigType.GDBSERVER_MYSQL_CONNECTPOOL_SIZE, coMySqlCount);
                coMySqlCount.dispose();
            }

            //SQLite数据源新增配置项-暂只在2015环境下支持
            ConfigOption coPlusCount = EnvConfig.getConfigOptInfo(SysConfigType.GDBSERVER_SQLITE_CONNECTPOOL_SIZE);
            if (coPlusCount != null) {
                coPlusCount.setValue(Long.valueOf(this.cfgPlusCount.getText()));
                EnvConfig.setConfigOptInfo(SysConfigType.GDBSERVER_SQLITE_CONNECTPOOL_SIZE, coPlusCount);
                coPlusCount.dispose();
            }

            //SQLite数据源连接类型-0-快速连接；1-安全连接
            ConfigOption coSqliteConnectType = EnvConfig.getConfigOptInfo(SysConfigType.GDBSERVER_SQLITE_UPLOADMODE);
            if (coSqliteConnectType != null) {
                coSqliteConnectType.setValue(Long.valueOf(this.cfgPlusUpload.getValue()));
                EnvConfig.setConfigOptInfo(SysConfigType.GDBSERVER_SQLITE_UPLOADMODE, coSqliteConnectType);
                coSqliteConnectType.dispose();
            }

            ConfigOption coKingbaseCount = EnvConfig.getConfigOptInfo(SysConfigType.GDBSERVER_KBS_CONNECTPOOL_SIZE);
            if (coKingbaseCount != null) {
                coKingbaseCount.setValue(Long.valueOf(this.cfgKingbaseCount.getText()));
                EnvConfig.setConfigOptInfo(SysConfigType.GDBSERVER_KBS_CONNECTPOOL_SIZE, coKingbaseCount);
                coKingbaseCount.dispose();
            }

            ConfigOption coDMCount = EnvConfig.getConfigOptInfo(SysConfigType.GDBSERVER_DM_CONNECTPOOL_SIZE);
            if (coDMCount != null) {
                coDMCount.setValue(Long.valueOf(this.cfgDMCount.getText()));
                EnvConfig.setConfigOptInfo(SysConfigType.GDBSERVER_DM_CONNECTPOOL_SIZE, coDMCount);
                coDMCount.dispose();
            }
            //endregion

            //中间件涉及注册注销，是即时保存的。

        } else {
            event.consume();
        }
    }

    /**
     * 判断中间件是否被数据源占用，如果占用，则不能注销
     *
     * @param mwInfo 中间件
     * @return 是否正被占用
     */
    private boolean isMiddlewareBeUsed(MiddleWareInfo mwInfo) {
        boolean hasUsed = false;
        if (mwInfo != null) {
            for (int i = 0; i < SvcConfig.count(); i++) {
                DataSrcInfo dsInfo = SvcConfig.get(i);
                if (dsInfo != null) {
                    String name = dsInfo.getDnsName();
                    int index = name.indexOf("&");
                    if (index >= 0) {
                        name = XString.remove(name, index);
                    }

                    if (name.equalsIgnoreCase(mwInfo.getDisplayName())) {
                        hasUsed = true;
                        break;
                    }
                }
            }
        }
        return hasUsed;
    }

    private Window window;

    /**
     * 获取当前窗口的window对象
     *
     * @return 当前窗口的window对象
     */
    private Window getCurrentWindow() {
        if (this.window == null) {
            this.window = this.getDialogPane().getScene().getWindow();
        }
        return this.window;
    }
}
