package com.zondy.mapgis.gdbmanager.clientconfig;

import com.zondy.mapgis.base.*;
import com.zondy.mapgis.controls.common.ButtonEdit;
import com.zondy.mapgis.controls.common.ButtonEditEvent;
import com.zondy.mapgis.controls.common.ZDComboBox;
import com.zondy.mapgis.geodatabase.middleware.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author CR
 * @file MidwareInfoDialog.java
 * @brief 添加/修改中间件的界面
 * @create 2019-12-12.
 */
public class MidwareInfoDialog extends Dialog {
    private final Image imageSucceed = new Image("/succeed_16.png");
    private final Image imageFail = new Image("/fail_16.png");
    private final Image imageEmpty = new Image("/empty_16.png");

    private MiddleWareInfo midwareInfo;  //当前中间件对象，新建的或者修改的
    private ZDComboBox<MiddleWareType> comboBoxType = new ZDComboBox<>();
    private TextField tfName = new TextField();
    private TextField tfDescription = new TextField();
    private ButtonEdit beManage = new ButtonEdit();
    private ButtonEdit beControl = new ButtonEdit();
    private ButtonEdit beConfig = new ButtonEdit();
    private ImageView ivManage = new ImageView(this.imageEmpty);
    private ImageView ivControl = new ImageView(this.imageEmpty);
    private ImageView ivConfig = new ImageView(this.imageEmpty);
    private Tooltip tooltipError = new Tooltip();//错误提示

    /**
     * 构造注册中间件对话框
     */
    public MidwareInfoDialog() {
        this(null);
    }

    /**
     * 构造修改中间件对话框
     *
     * @param mwInfo 要修改的中间件，传null则注册
     */
    public MidwareInfoDialog(MiddleWareInfo mwInfo) {
        this.midwareInfo = mwInfo;
        this.setTitle(this.midwareInfo == null ? "注册中间件" : "修改中间件");

        //region 用GridPane布局界面
        //this.comboBoxType.prefWidthProperty().bind(this.tfName.widthProperty());
        GridPane gridPane = new GridPane();
        gridPane.setVgap(6);
        gridPane.setHgap(6);
        gridPane.add(new Label("类型:"), 0, 0);
        gridPane.add(new Label("名称:"), 0, 1);
        gridPane.add(new Label("描述:"), 0, 2);
        gridPane.add(new Label("管理模块:"), 0, 3);
        gridPane.add(new Label("控制模块:"), 0, 4);
        gridPane.add(new Label("配置模块:"), 0, 5);
        gridPane.add(this.tfName, 1, 1);
        gridPane.add(this.tfDescription, 1, 2);
        gridPane.add(this.beManage, 1, 3);
        gridPane.add(this.beControl, 1, 4);
        gridPane.add(this.beConfig, 1, 5);
        gridPane.add(this.ivManage, 2, 3);
        gridPane.add(this.ivControl, 2, 4);
        gridPane.add(this.ivConfig, 2, 5);
        gridPane.add(this.comboBoxType, 1, 0);//ComboBox需要放在其bind的对象后面添加，否则，在linux下面首次弹出来长度不对。
        GridPane.setHgrow(this.tfName, Priority.ALWAYS);

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setPrefWidth(450);
        dialogPane.setContent(gridPane);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, this::okButtonClick);
        //endregion

        //region 控件内容和事件
        this.comboBoxType.getItems().addAll(MiddleWareType.CROSS_ARCSDE, MiddleWareType.CROSS_FILEGDB, MiddleWareType.CROSS_MDB, MiddleWareType.valueOf(0));
        this.comboBoxType.setConverter(new StringConverter<MiddleWareType>() {
            @Override
            public String toString(MiddleWareType object) {
                String dspName = "";
                switch (object) {
                    case CROSS_ARCSDE:
                        dspName = "跨平台ArcSDE中间件";
                        break;
                    case CROSS_FILEGDB:
                        dspName = "跨平台FileGDB中间件";
                        break;
                    case CROSS_MDB:
                        dspName = "跨平台PersonalGDB中间件";
                        break;
                    default:
                        dspName = "(自定义)";
                        break;
                }
                return dspName;
            }

            @Override
            public MiddleWareType fromString(String string) {
                MiddleWareType type = MiddleWareType.valueOf(0);
                switch (string) {
                    case "跨平台ArcSDE中间件":
                        type = MiddleWareType.CROSS_ARCSDE;
                        break;
                    case "跨平台FileGDB中间件":
                        type = MiddleWareType.CROSS_FILEGDB;
                        break;
                    case "跨平台PersonalGDB中间件":
                        type = MiddleWareType.CROSS_MDB;
                        break;
                    case "(自定义)":
                        type = MiddleWareType.valueOf(0);
                        break;
                }
                return type;
            }
        });
        this.comboBoxType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
        {
            this.ivManage.setImage(this.imageEmpty);
            this.ivControl.setImage(this.imageEmpty);
            this.ivConfig.setImage(this.imageEmpty);

            this.beManage.setDisable(true);
            this.beControl.setDisable(true);
            this.beConfig.setDisable(true);
            switch (newValue) {
                case ArcLocal:
                    this.tfName.setText("ArcGISLocal");
                    this.tfDescription.setText("ArcGIS本地数据中间件");
                    this.beManage.setText("esri_Manager.dll");
                    this.beControl.setText("esriMng_XCls.dll");
                    this.beConfig.setText("esriLocal_Config.dll");
                    break;
                case CROSS_ARCSDE:
                    this.tfName.setText("跨平台ArcSDE");
                    this.tfDescription.setText("跨平台ArcSDE中间件");
                    this.beManage.setText("ArcSDE_Manager.dll");
                    this.beControl.setText("ArcSDEMng_XCls.dll");
                    this.beConfig.setText("ArcSDE_Config.dll");
                    break;
                case CROSS_FILEGDB:
                    this.tfName.setText("跨平台FileGDB");
                    this.tfDescription.setText("跨平台FileGDB中间件");
                    this.beManage.setText("FileGDB_Manager.dll");
                    this.beControl.setText("FileGDBMng_XCls.dll");
                    this.beConfig.setText("FileGDB_Config.dll");
                    break;
                case CROSS_MDB:
                    this.tfName.setText("跨平台PersonalGDB");
                    this.tfDescription.setText("跨平台PersonalGDB中间件");
                    this.beManage.setText("PGDB_Manager.dll");
                    this.beControl.setText("PGDBMng_XCls.dll");
                    this.beConfig.setText("PGDB_Config.dll");
                    break;
                case ArcSDE:
                    this.tfName.setText("ArcSDE");
                    this.tfDescription.setText("基于ArcGIS COM组件的SDE中间件");
                    this.beManage.setText("esri_Manager.dll");
                    this.beControl.setText("esriMng_XCls.dll");
                    this.beConfig.setText("esri_Config.dll");
                    break;
                case SDE:
                    this.tfName.setText("ArcSDE");
                    this.tfDescription.setText("基于ArcGISSDE API的SDE中间件");
                    this.beManage.setText("SDE_Manager.dll");
                    this.beControl.setText("SDEMng_XCls.dll");
                    this.beConfig.setText("SDE_Config.dll");
                    break;
                case Access:
                    this.tfName.setText("Access");
                    this.tfDescription.setText("Access中间件");
                    this.beManage.setText("Acs_Manager.dll");
                    this.beControl.setText("AcsMng_XCls.dll");
                    this.beConfig.setText("Acs_Config.dll");
                    break;
                case CAD:
                    this.tfName.setText("AutoCAD");
                    this.tfDescription.setText("AutoCAD中间件");
                    this.beManage.setText("AutoCAD_Manager.dll");
                    this.beControl.setText("AutoCADMng_Xcls.dll");
                    this.beConfig.setText("AutoCADLocal_Config.dll");
                    break;
                case MapInfo:
                    this.tfName.setText("MapInfo");
                    this.tfDescription.setText("MapInfo中间件");
                    this.beManage.setText("MapInfo_Manager.dll");
                    this.beControl.setText("MapInfoMng_Xcls.dll");
                    this.beConfig.setText("MapInfo_Config.dll");
                    break;
                case VCT:
                    this.tfName.setText("VCT");
                    this.tfDescription.setText("VCT中间件");
                    this.beManage.setText("vct_Manager.dll");
                    this.beControl.setText("vctMng_Xcls.dll");
                    this.beConfig.setText("vct_Config.dll");
                    break;
                default:
                    this.beManage.setDisable(false);
                    this.beControl.setDisable(false);
                    this.beConfig.setDisable(false);
                    this.tfName.setText("");
                    this.tfDescription.setText("");
                    this.beManage.setText("");
                    this.beControl.setText("");
                    this.beConfig.setText("");
                    break;
            }
        });

        //中间件名称不能包含特殊字符，且必须少于20个字符.
        this.tfName.textProperty().addListener((observable, oldValue, newValue) ->
        {
            List<Character> invalidCharList = GISDefaultValues.getInvalidNameCharList();
            invalidCharList.add('&');
            StringProperty errorMsg = new SimpleStringProperty();
            if (!XString.isTextValid(newValue, 20, invalidCharList, false, errorMsg)) {
                UIFunctions.showErrorTip(this.tfName, errorMsg.get(), this.tooltipError);
                this.tfName.setText(oldValue);
            }
        });

        //中间件描述必须少于128个字符.
        this.tfDescription.textProperty().addListener((observable, oldValue, newValue) ->
        {
            StringProperty errorMsg = new SimpleStringProperty();
            if (!XString.isTextValid(newValue, 128, errorMsg)) {
                UIFunctions.showErrorTip(this.tfDescription, errorMsg.get(), this.tooltipError);
                this.tfDescription.setText(oldValue);
            }
        });

        this.beManage.setOnButtonClick(this::selectFileButtonClick);
        this.beControl.setOnButtonClick(this::selectFileButtonClick);
        this.beConfig.setOnButtonClick(this::selectFileButtonClick);
        //endregion

        if (this.midwareInfo != null) {
            this.comboBoxType.getSelectionModel().select(this.midwareInfo.getMidWareType());
            this.tfName.setText(this.midwareInfo.getDisplayName());
            this.tfDescription.setText(this.midwareInfo.getDescription());
            this.beManage.setText(this.midwareInfo.getManageDLL());
            this.beControl.setText(this.midwareInfo.getXClsDLL());
            this.beConfig.setText(this.midwareInfo.getConfigDLL());
            this.comboBoxType.setDisable(true);
            this.beManage.setDisable(true);
            this.beControl.setDisable(true);
            this.beConfig.setDisable(true);
        } else {
            this.comboBoxType.getSelectionModel().select(0);
        }

        System.out.println("tttttttttttttttttttttt");
        this.setOnShown(event -> this.comboBoxType.prefWidthProperty().bind(this.tfName.widthProperty()));
    }

    /**
     * 确定
     *
     * @param event 事件参数
     */
    private void okButtonClick(ActionEvent event) {
        if (this.valiadateMiddleWareInfo()) {
            ModuleDescription moduleDes = null;
            MiddleWareType midWareType = MiddleWareType.V6X;
            boolean isNew = this.midwareInfo == null;
            if (!isNew || this.checkMidwareModules(moduleDes, midWareType)) {
                if (this.midwareInfo == null) {
                    this.midwareInfo = new MiddleWareInfo();
                    this.midwareInfo.setRegistDate(XFunctions.getDateString());
                }
                this.midwareInfo.setDisplayName(this.tfName.getText());
                this.midwareInfo.setDescription(this.tfDescription.getText());
                this.midwareInfo.setModeDesc(moduleDes);
                if (isNew) {
                    this.midwareInfo.setMidWareType(midWareType);
                    this.midwareInfo.setFileKeyValue("");
                    this.midwareInfo.setManageDLL(this.beManage.getText());
                    this.midwareInfo.setXClsDLL(this.beControl.getText());
                    this.midwareInfo.setConfigDLL(this.beConfig.getText());
                }
            } else {
                event.consume();
            }
        } else {
            event.consume();
        }
    }

    /**
     * 获取当前操作的中间件
     *
     * @return 当前操作的中间件
     */
    public MiddleWareInfo getMiddleWareInfo() {
        return this.midwareInfo;
    }

    /**
     * 目录设置中的选择目录
     *
     * @param event 事件参数
     */
    public void selectFileButtonClick(ButtonEditEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择中间件模块文件。");
        String path = XPath.getProgramPath();
        if (!XString.isNullOrEmpty(path)) {
            String programPath = XPath.getProgramPath();
            if (!XString.isNullOrEmpty(programPath) && (new File(programPath)).exists()) {
                fileChooser.setInitialDirectory(new File(XPath.getProgramPath()));
            }
        }
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("dll文件", "*.dll"));
        ButtonEdit buttonEdit = ((ButtonEdit) event.getSource());
        File file = fileChooser.showOpenDialog(this.getCurrentWindow());
        if (file != null) {
            buttonEdit.setText(file.getName());
            buttonEdit.setUserData(file.getAbsolutePath());
        }
    }

    /**
     * 验证模块是否能够注册成功，若能获取其模块描述和中间件类型
     *
     * @param moduleDes   返回注册的中间件模块的描述信息
     * @param midWareType 返回注册的中间件的类型
     * @return 若所选的三个模块能注册成功返回true，否则返回false
     */
    private boolean checkMidwareModules(ModuleDescription moduleDes, MiddleWareType midWareType) {
        boolean rtnManage = false;
        boolean rtnControl = false;
        boolean rtnConfig = false;
        MiddleWareConfigTool mwcTool = new MiddleWareConfigTool();
        //if (mwcTool.init(this.tfName.getText(), this.tfDescription.getText(), "", this.beManage.getText(), this.beControl.getText(), this.beConfig.getText()))
        //封装层有Bug，没有返回true
        mwcTool.init(this.tfName.getText(), this.tfDescription.getText(), "", this.beManage.getText(), this.beControl.getText(), this.beConfig.getText());
        {
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
                if (temp) {
                    moduleDes = mwcTool.getModuleDesc();
                    midWareType = (MiddleWareType) cfg[1];
                } else {
                    rtnManage = false;
                    rtnControl = false;
                    rtnConfig = false;
                }
            }
        }
        mwcTool.dispose();

        this.ivManage.setImage(rtnManage ? imageSucceed : imageFail);
        this.ivControl.setImage(rtnControl ? imageSucceed : imageFail);
        this.ivConfig.setImage(rtnConfig ? imageSucceed : imageFail);

        return (rtnManage && rtnControl && rtnConfig);
    }

    /**
     * 验证中间件相关输入
     *
     * @return 中间件相关信息设置无误返回true，否则返回false
     */
    private boolean valiadateMiddleWareInfo() {
        String errorMsg = "";

        //验证中间件名称是否有效
        String name = this.tfName.getText();
        if (XString.isNullOrEmpty(name)) {
            errorMsg = "中间件名称不能为空。";
        } else if (XString.getStringByteLength(name) >= 20) {
            errorMsg = "中间件名称必须少于20个字符。";
        } else {
            MiddleWareConfig middleWareConfig = new MiddleWareConfig();
            middleWareConfig.open();
            MiddleWareInfo midWare = middleWareConfig.getItemByName(name);
            if (midWare != null && (this.midwareInfo == null || this.midwareInfo.getID() != midWare.getID())) {
                errorMsg = "已有同名中间件存在。";
            }
            middleWareConfig.close();
        }

        if (errorMsg == "") {
            //验证3个模块的输入
            String manage = this.beManage.getText();
            String control = this.beControl.getText();
            String config = this.beConfig.getText();
            if (XString.isNullOrEmpty(manage) || XString.isNullOrEmpty(control) || XString.isNullOrEmpty(config)) {
                errorMsg = "中间件的三个模块都不能为空。";
            } else {
                //学习版不支持sde,sdo中间件注册//未完成。跨平台还有狗和版本概念吗
                //if (CheckLcc.GetCardNo() == 607888888 || !ClientConfigManage.HigherThanProfessional())
                //{
                //    switch (config.toLowerCase())
                //    {
                //        case "acs_config.dll":
                //        case "esri_config.dll":
                //        case "sdo_config.dll":
                //        case "sde_config.dll":
                //            errorMsg = "您目前安装的版本不支持此类型中间件。";
                //            break;
                //        default:
                //            break;
                //    }
                //}
                //endregion
            }
        }

        if (!XString.isNullOrEmpty(errorMsg)) {
            MessageBox.information(errorMsg, this.getCurrentWindow());
        }

        return errorMsg == "";
    }

    private javafx.stage.Window window;

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
