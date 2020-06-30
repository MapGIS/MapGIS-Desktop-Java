package com.zondy.mapgis.gdbmanager.gdbcatalog;

import com.zondy.mapgis.base.MessageBox;
import com.zondy.mapgis.base.XPath;
import com.zondy.mapgis.base.XString;
import com.zondy.mapgis.controls.common.ButtonEdit;
import com.zondy.mapgis.controls.common.ZDComboBox;
import com.zondy.mapgis.geodatabase.Server;
import com.zondy.mapgis.geodatabase.config.ConnectType;
import com.zondy.mapgis.geodatabase.config.EnvConfig;
import com.zondy.mapgis.geodatabase.middleware.MiddleWareType;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

/**
 * @author CR
 * @file AttachDataBaseDialog.java
 * @brief 附加数据库
 * @create 2019-12-18.
 */
public class AttachGDBDialog extends Dialog {
    private final ButtonEdit buttonEditFile = new ButtonEdit();
    private final TextField textFieldName = new TextField();
    private final ButtonEdit buttonEditCustom = new ButtonEdit();
    private final CheckBox checkBox = new CheckBox("重置数据库文件的GUID");
    private final PasswordField passwordField = new PasswordField();
    private final PasswordField passwordField2 = new PasswordField();
    private ZDComboBox<String> nameComboBox;
    private Server server;     //记录待附加数据库的数据源
    private String initPath;//选择文件框的初始位置
    private int attachID;
    private MiddleWareType midwareType;

    public AttachGDBDialog(Server ds) {
        this(ds, MiddleWareType.V6X);
    }

    public AttachGDBDialog(Server ds, MiddleWareType mwType) {
        this.setTitle("附加数据库");
        this.server = ds;
        this.midwareType = mwType;
        if (this.server != null) {
            GridPane gridPane = new GridPane();
            gridPane.setVgap(6);
            gridPane.setHgap(6);
            switch (this.server.getConnectType()) {
                case Local:
                case LocalPlus: {
                    this.buttonEditFile.setOnButtonClick(event ->
                    {
                        //region 计算初始位置（sample)
                        System.out.println("1: setOnButtonClick");
                        System.out.println("2: " + this.initPath);
                        if (XString.isNullOrEmpty(this.initPath)) {
                            String path = EnvConfig.getGisEnv().getCur();
                            System.out.println("3: " + path);
                            if (XString.isNullOrEmpty(path) || !(new File(path)).isDirectory()) {
                                path = XPath.getProgramPath();
                                System.out.println("4: " + path);
                                if (!XString.isNullOrEmpty(path)) {
                                    path = (new File(path).getParent()) + File.separator + "Sample";
                                    System.out.println("5: " + path);
                                }
                            }
                            this.initPath = path;
                            System.out.println("6: " + this.initPath);
                        }
                        //endregion

                        //region 过滤器
                        FileChooser.ExtensionFilter filter = null;
                        if (ConnectType.Local.equals(this.server.getConnectType())) {
                            filter = new FileChooser.ExtensionFilter("HDF文件(*.hdf)", "*.hdf");
                        }
                        if (ConnectType.LocalPlus.equals(this.server.getConnectType())) {
                            filter = new FileChooser.ExtensionFilter("HDB文件(*.hdb)", "*.hdb");
                        }
                        //endregion

                        FileChooser fileChooser = new FileChooser();
                        fileChooser.getExtensionFilters().add(filter);
                        System.out.println("7: " + this.initPath);
                        if (!XString.isNullOrEmpty(this.initPath) && (new File(this.initPath).exists())) {
                            fileChooser.setInitialDirectory(new File(this.initPath));
                            System.out.println("8: aaaaaaaaaaaaaa");
                        }

                        File file = fileChooser.showOpenDialog(this.getCurrentWindow());
                        if (file != null) {
                            this.buttonEditFile.setText(file.getPath());
                            this.initPath = file.getParent();
                            this.textFieldName.setText(XPath.getNameWithoutExt(file));
                        }
                    });

                    gridPane.add(new Label("数据库文件:"), 0, 0);
                    gridPane.add(new Label("数据库名称:"), 0, 1);
                    gridPane.add(this.buttonEditFile, 1, 0);
                    gridPane.add(this.textFieldName, 1, 1);
                    if (ConnectType.Local.equals(this.server.getConnectType())) {
                        gridPane.add(this.checkBox, 1, 2);
                    }
                    GridPane.setHgrow(this.textFieldName, Priority.ALWAYS);
                    break;
                }
                case DBSQL:
                case DBOracle:
                case DBDm:
                case DBPG://postgre数据源
                case DBKBS://金仓数据源支持-zkj
                {
                    this.nameComboBox = new ZDComboBox<>(FXCollections.observableArrayList(this.server.getCanAttachTableSpaces()));
                    gridPane.add(new Label("数据库:"), 0, 0);
                    GridPane.setHgrow(this.nameComboBox, Priority.ALWAYS);

                    if (ConnectType.DBSQL.equals(this.server.getConnectType())) {
                        this.nameComboBox.prefWidthProperty().bind(this.passwordField.widthProperty());
                        gridPane.add(new Label("管理员口令:"), 0, 1);
                        gridPane.add(new Label("确认口令:"), 0, 2);
                        gridPane.add(this.passwordField, 1, 1);
                        gridPane.add(this.passwordField2, 1, 2);
                    }
                    gridPane.add(this.nameComboBox, 1, 0);//ComboBox需要放在其bind的对象后面添加，否则，在linux下面首次弹出来长度不对。
                    if (this.nameComboBox.getItems().size() > 0) {
                        this.nameComboBox.getSelectionModel().select(0);
                    }
                    break;
                }
                case Custom: {
                    gridPane.add(new Label("路径:"), 0, 0);
                    gridPane.add(this.buttonEditCustom, 1, 0);
                    this.buttonEditCustom.setOnButtonClick(event ->
                    {
                        if (MiddleWareType.CROSS_FILEGDB.equals(this.midwareType)) {
                            DirectoryChooser directoryChooser = new DirectoryChooser();
                            directoryChooser.setTitle("选择目录");
                            File directory = directoryChooser.showDialog(this.getCurrentWindow());
                            if (directory != null) {
                                this.buttonEditCustom.setText(directory.getPath());
                            }
                        } else {
                            FileChooser fileChooser = new FileChooser();
                            fileChooser.setTitle("打开文件");
                            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MDB文件(.mdb)", "*.mdb"));
                            File file = fileChooser.showOpenDialog(this.getCurrentWindow());
                            if (file != null) {
                                this.buttonEditCustom.setText(file.getPath());
                            }
                        }
                    });
                }
                default:
                    break;
            }

            DialogPane dialogPane = super.getDialogPane();
            dialogPane.setPrefWidth(450);
            dialogPane.setContent(gridPane);
            dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
            okButton.addEventFilter(ActionEvent.ACTION, this::okButtonClick);
        }
    }

    /**
     * 获取附加的数据库的ID
     *
     * @return
     */
    public int getAttachID() {
        return this.attachID;
    }

    /**
     * 确定
     *
     * @param event
     */
    private void okButtonClick(ActionEvent event) {
        String errorMsg = "";
        switch (this.server.getConnectType()) {
            case Local:
            case LocalPlus: {
                String dbFile = this.buttonEditFile.getText();
                String dbName = this.textFieldName.getText();
                if (XString.isNullOrEmpty(dbFile)) {
                    errorMsg = "数据库文件不能为空。";
                } else if (!(new File(dbFile)).isFile()) {
                    errorMsg = "数据库文件不存在。";
                } else if (XString.isNullOrEmpty(dbName)) {
                    errorMsg = "数据库名称不能为空。";
                } else if (this.server.getDBID(dbName) > 0) {
                    errorMsg = "数据源下已经存在同名数据库，请更改数据库名称。";
                } else {
                    String ext = XPath.getExtension(dbFile);
                    if (!ext.equalsIgnoreCase(ConnectType.Local.equals(this.server.getConnectType()) ? ".hdf" : ".hdb")) {
                        errorMsg = "数据库文件格式不正确。";
                    }
                }

                if (XString.isNullOrEmpty(errorMsg)) {
                    ButtonType buttonType = ButtonType.YES;
                    if (dbFile.startsWith("\\\\")) {
                        buttonType = MessageBox.questionEx("存储服务须以当前用户启动才能成功附加远程数据库,是否继续?", this.getCurrentWindow(), true);
                    }

                    if (ButtonType.CANCEL.equals(buttonType)) {
                        this.close();
                    } else if (ButtonType.NO.equals(buttonType)) {
                        event.consume();
                    } else {
                        this.attachID = (int) this.server.attachDB(dbName, dbFile, "");
                        if (this.attachID <= 0) {
                            errorMsg = AttachGDBDialog.getAttachError(this.attachID);
                        }
                    }
                }
                break;
            }
            case DBSQL:
            case DBOracle:
            case DBDm:
            case DBPG://postgre数据源
            case DBKBS://金仓数据源支持-zkj
            {
                String dbName = this.nameComboBox.getValue();
                if (XString.isNullOrEmpty(dbName)) {
                    errorMsg = "数据库名称不能为空。";
                } else if (ConnectType.DBSQL.equals(this.server.getConnectType())) {
                    String password = this.passwordField.getText();
                    String password2 = this.passwordField2.getText();
                    if (XString.isNullOrEmpty(password)) {
                        errorMsg = "数据库管理员口令不能为空。";
                    } else if (!password.equalsIgnoreCase(password2)) {
                        errorMsg = "两次输入的数据库管理员口令不一致。";
                    }
                }

                if (XString.isNullOrEmpty(errorMsg)) {
                    this.attachID = (int) this.server.attachDB(dbName, null, null);
                    if (this.attachID <= 0) {
                        errorMsg = AttachGDBDialog.getAttachError(this.attachID);
                    } else if (ConnectType.DBSQL.equals(this.server.getConnectType())) {
                        //SQL数据库如果附加成功，则进行登录名的创建。(暂时只支持sql数据源)
                        String password = this.passwordField.getText();
                        if (XString.isNullOrEmpty(password)) {
                            try {
                                //未完成。
                                //DataSrcInfo svcInfo = SvcConfig.get(this.server.getSvrName());
                                //String[] logins = Server.getLogInfo(this.server.getSvrName());
                                //String connectText = String.format("server = '%s';uid = '%s';pwd = '%s';", this.server.getSvrName(), logins[0], logins[1]);
                                //SqlConnection connect = new SqlConnection(connectText);
                                //connect.Open();
                                //String cmdText = "exec master.dbo.sp_addlogin '" + ad.DBName + "','" + ad.SQLPassword + "','" + ad.DBName + "'";
                                //SqlCommand cmd = new SqlCommand(cmdText, connect);
                                //cmd.ExecuteNonQuery();
                                //cmd.CommandText = "exec master.dbo.sp_addsrvrolemember '" + ad.DBName + "','dbcreator'";
                                //cmd.ExecuteNonQuery();
                                //cmd.CommandText = "exec master.dbo.sp_addsrvrolemember '" + ad.DBName + "','sysadmin'";
                                //cmd.ExecuteNonQuery();
                                //connect.close();
                            } catch (Exception ex) {
                                errorMsg = ex.getMessage();
                                this.server.deleteGDB(attachID);
                                attachID = -1;
                            }
                        }
                    }
                }
                break;
            }
            case Custom: {
                String path = this.buttonEditCustom.getText();
                if (XString.isNullOrEmpty(path)) {
                    errorMsg = "路径不能为空。";
                } else {
                    File file = new File(path);
                    if (MiddleWareType.CROSS_FILEGDB.equals(this.midwareType)) {
                        if (!file.isDirectory() || !path.toLowerCase().endsWith(".gdb")) {
                            errorMsg = "指定的路径不存在或不符合需求(.gdb)。";
                        }
                    } else if (!file.isFile()) {
                        errorMsg = "指定的路径不存在或不是文件。";
                    }

                    if (XString.isNullOrEmpty(errorMsg)) {
                        path = String.format("dbPath=%s;dbName=%s", path, XPath.getNameWithoutExt(file));//$"dbPath={this.Path};dbName={dirName}";//dbPath = F:\Data\PersonalGDB\圆弧.mdb; dbName = 圆弧
                        this.attachID = (int) this.server.attachDB(path, null, null);
                        if (this.attachID <= 0) {
                            errorMsg = AttachGDBDialog.getAttachError(this.attachID);
                        }
                    }
                }
                break;
            }
            default:
                break;
        }
        if (errorMsg != "") {
            MessageBox.information(errorMsg, this.getCurrentWindow());
            event.consume();
        }
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

    public static String getAttachError(long attachID) {
        String errorMsg = "";
        if (attachID <= 0) {
            switch ((int) attachID) {
                case -1002:
                    errorMsg = "数据库文件打不开";
                    break;
                case -1003:
                    errorMsg = "数据库已存在";
                    break;
                case -1004:
                    errorMsg = "与已有库引用了相同的数据库文件";
                    break;
                case -1005:
                    errorMsg = "已有库日志引用了相同的文件";
                    break;
                case -1006:
                    errorMsg = "存储服务没有以当前用户启动";
                    break;
                case -1007:
                    errorMsg = "数据库的目录IDF缺失";
                    break;
                case -1008:
                    errorMsg = "数据库的目录IDF打不开";
                    break;
                case -1009:
                    errorMsg = "数据库GUID已存在";
                    break;
                case -1010:
                    errorMsg = "数据库证书错误";
                    break;
                case -1011:
                    errorMsg = "数据库目录添加错误";
                    break;
                case -6:
                    errorMsg = "地理数据库未正常关闭";
                    break;
                default:
                    errorMsg = "存储桶打开失败,有可能数据库已被损坏";
                    break;
            }
            errorMsg += ", 附加失败。";
        }
        return errorMsg;
    }
}
