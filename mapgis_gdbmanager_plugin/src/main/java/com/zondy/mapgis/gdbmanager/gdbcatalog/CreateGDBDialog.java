package com.zondy.mapgis.gdbmanager.gdbcatalog;

import com.zondy.mapgis.base.*;
import com.zondy.mapgis.controls.common.ButtonEdit;
import com.zondy.mapgis.gdbmanager.CommonFunctions;
import com.zondy.mapgis.geodatabase.config.ConnectType;
import com.zondy.mapgis.geodatabase.config.DataSrcInfo;
import com.zondy.mapgis.geodatabase.config.EnvConfig;
import com.zondy.mapgis.geodatabase.config.SvcConfig;
import com.zondy.mapgis.geodatabase.*;
import com.zondy.mapgis.geodatabase.event.ProgressStatus;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URI;
import java.security.PrivateKey;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author CR
 * @file CreateDataBaseDialog.java
 * @brief 创建数据库
 * @create 2019-12-18.
 */
public class CreateGDBDialog extends Dialog {
    private Server server;
    private final TextField textFieldName = new TextField();
    private final ButtonEdit buttonEditPath = new ButtonEdit();
    private final TextField adminTextField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final PasswordField passwordField2 = new PasswordField();
    private final CheckBox logCheckBox = new CheckBox("完成后显示创建日志（创建失败时强制显示）");
    private int createMode = 1;//1-新建，2-在已有库中初始化
    private Tooltip tooltipError = new Tooltip();//错误提示
    private int stepLevel = 0;     //用于控制创建日志的缩进       
    private String logText = "";   //日志内容，在向导关闭后写入日志文件

    public CreateGDBDialog(Server ds) {
        this.server = ds;
        this.setTitle("创建数据库");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(6);
        gridPane.setVgap(6);

        if (this.server != null) {
            ConnectType conType = this.server.getConnectType();
            this.buttonEditPath.setText(this.server.getDefaultDataPath());
            switch (conType) {
                case Local:
                case LocalPlus: {
                    this.buttonEditPath.setOnButtonClick(event ->
                    {
                        DirectoryChooser directoryChooser = new DirectoryChooser();
                        String workPath = EnvConfig.getGisEnv().getCur();
                        if (!XString.isNullOrEmpty(workPath) && ((new File(workPath).exists()))) {
                            directoryChooser.setInitialDirectory(new File(workPath));
                        }
                        File file = directoryChooser.showDialog(this.getCurrentWindow());
                        if (file != null) {
                            this.buttonEditPath.setText(file.getPath());
                        }
                    });
                    gridPane.add(new Label("数据库名称:"), 0, 0);
                    gridPane.add(new Label("存储位置:"), 0, 1);
                    gridPane.add(this.textFieldName, 1, 0);
                    gridPane.add(buttonEditPath, 1, 1);
                    gridPane.add(this.logCheckBox, 1, 2);
                    break;
                }
                case DBSQL:
                case DBMySQL:
                case DBDm:
                case DBDB2:
                case DBGBase:
                case DBBeyon:
                case DBSybase:
                case DBOracle:
                case DBPG://Postgre数据源
                case DBKBS://金仓数据源支持-zkj
                {
                    RadioButton radioButton1 = new RadioButton("新建地理数据库");
                    radioButton1.setUserData(1);
                    RadioButton radioButton2 = new RadioButton("在现有数据库中初始化地理数据库");
                    radioButton2.setUserData(2);
                    radioButton1.setSelected(true);
                    ToggleGroup tg = new ToggleGroup();
                    tg.getToggles().addAll(radioButton1, radioButton2);
                    VBox vBox = new VBox(6, radioButton1, radioButton2);

                    this.adminTextField.setDisable(true);

                    gridPane.add(new Label("建库方式:"), 0, 0);
                    gridPane.add(new Label("数据库名称:"), 0, 1);
                    gridPane.add(new Label("管理员名称:"), 0, 2);
                    gridPane.add(new Label("管理员口令:"), 0, 3);
                    gridPane.add(new Label("确认口令:"), 0, 4);
                    gridPane.add(vBox, 1, 0);
                    gridPane.add(this.textFieldName, 1, 1);
                    gridPane.add(this.adminTextField, 1, 2);
                    gridPane.add(this.passwordField, 1, 3);
                    gridPane.add(this.passwordField2, 1, 4);
                    gridPane.add(this.logCheckBox, 1, 5);

                    this.adminTextField.textProperty().bind(this.textFieldName.textProperty());
                    tg.selectedToggleProperty().addListener((observable, oldValue, newValue) ->
                    {
                        if (newValue instanceof RadioButton) {
                            this.createMode = (int) ((RadioButton) newValue).getUserData();
                            this.passwordField.setDisable(this.createMode == 2);
                            this.passwordField2.setDisable(this.createMode == 2);
                        }
                    });

                    if (conType == ConnectType.DBPG || conType == ConnectType.DBKBS) {
                        //PG、金仓数据源，不判断是否有MPDBMASTER库
                        radioButton1.setDisable(true);
                        radioButton2.setSelected(true);
                        if (this.server.getConnectType() == ConnectType.DBKBS) {
                            //根据底层最新需求，金仓数据源只有一个数据库并且名称固定为MPDBMASTER
                            this.textFieldName.setText("MPDBMASTER");
                            this.textFieldName.setDisable(true);
                        } else {
                            if (this.server.getConnectType() == ConnectType.DBPG) {
                                //初始化默认的数据库名 PG、KBS数据源格式为 192.168.80.90:5432/test
                                DataSrcInfo dsInfo = SvcConfig.get(this.server.getSvrName());
                                String dnsName = dsInfo.getDnsName();
                                int index = dnsName.lastIndexOf('/');
                                if (index > -1) {
                                    this.textFieldName.setText(dnsName.substring(index + 1));
                                }
                            }
                            this.textFieldName.requestFocus();
                        }
                    } else if (XString.isNullOrEmpty(this.server.getDBName(1))) {
                        String masterDBName = CommonFunctions.getMasterDBName(this.server);
                        this.textFieldName.setText(masterDBName);
                        this.textFieldName.setDisable(conType != ConnectType.DBDB2);
                    }

                    this.passwordField.textProperty().addListener((observable, oldValue, newValue) ->
                    {
                        StringProperty errorMsg = new SimpleStringProperty();
                        if (!XString.isNullOrEmpty(newValue)) {
                            List<Character> invalidCharList = null;
                            char[] invalidChars = this.server.getInvalidChars(2);//2-数据库密码
                            if (invalidChars != null) {
                                //从数据源获取的数据库名非法字符中包含了Ctrl+C和Ctrl+V，导致无法粘贴。
                                invalidCharList = new ArrayList<>();
                                for (char c : invalidCharList) {
                                    if (c != 3 && c != 22) {
                                        invalidCharList.add(c);
                                    }
                                }
                            }
                            XString.isTextValid(newValue, 64, invalidCharList, errorMsg);
                        }

                        if (!XString.isNullOrEmpty(errorMsg)) {
                            this.passwordField.setText(oldValue);
                            UIFunctions.showErrorTip(this.passwordField, errorMsg.get(), this.tooltipError);
                        }
                    });
                    this.passwordField2.textProperty().addListener((observable, oldValue, newValue) ->
                    {
                        StringProperty errorMsg = new SimpleStringProperty();
                        if (!XString.isNullOrEmpty(newValue)) {
                            List<Character> invalidCharList = null;
                            char[] invalidChars = this.server.getInvalidChars(2);//2-数据库密码
                            if (invalidChars != null) {
                                //从数据源获取的数据库名非法字符中包含了Ctrl+C和Ctrl+V，导致无法粘贴。
                                invalidCharList = new ArrayList<>();
                                for (char c : invalidCharList) {
                                    if (c != 3 && c != 22) {
                                        invalidCharList.add(c);
                                    }
                                }
                            }
                            XString.isTextValid(newValue, 64, invalidCharList, errorMsg);
                        }

                        if (!XString.isNullOrEmpty(errorMsg)) {
                            this.passwordField2.setText(oldValue);
                            UIFunctions.showErrorTip(this.passwordField2, errorMsg.get(), this.tooltipError);
                        }
                    });
                    break;
                }
                default:
                    break;
            }
            VBox vBox = new VBox(6);
        }

        this.textFieldName.textProperty().addListener((o, oldValue, newValue) ->
        {
            StringProperty errorMsg = new SimpleStringProperty();
            if (!XString.isNullOrEmpty(newValue)) {
                ConnectType conType = this.server.getConnectType();
                if (conType != ConnectType.Local && conType != ConnectType.LocalPlus) {
                    char firChar = newValue.charAt(0);
                    if (firChar >= '0' && firChar <= '9') {
                        errorMsg.set("首字符不能是数字。");
                    }
                }

                if (XString.isNullOrEmpty(errorMsg)) {
                    List<Character> invalidCharList = null;
                    char[] invalidChars = this.server.getInvalidChars(1);//1-数据库名称
                    if (invalidChars != null) {
                        //从数据源获取的数据库名非法字符中包含了Ctrl+C和Ctrl+V，导致无法粘贴。
                        invalidCharList = new ArrayList<>();
                        for (char c : invalidChars) {
                            if (c != 3 && c != 22) {
                                invalidCharList.add(c);
                            }
                        }
                    }
                    XString.isTextValid(newValue, 32, invalidCharList, errorMsg);
                }

                if (!XString.isNullOrEmpty(errorMsg)) {
                    this.textFieldName.setText(oldValue);
                    UIFunctions.showErrorTip(this.textFieldName, errorMsg.get(), this.tooltipError);
                }
            }
        });

        GridPane.setHgrow(this.textFieldName, Priority.ALWAYS);
        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setContent(gridPane);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, this::okButtonClick);
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

    /**
     * 获取当前数据源下创建数据库的数据库文件的扩展类型
     *
     * @return
     */
    private String getDBFileExtension() {
        String ext = "";
        switch (this.server.getConnectType()) {
            case Local:
                ext = ".hdf";
                break;
            case LocalPlus:
                ext = ".hdb";
                break;
            case DBSQL:
            case DBMySQL:
            case DBDB2:
            case DBGBase:
            case DBBeyon:
            case DBSybase:
            case DBKBS://金仓数据源支持-zkj
                ext = ".mdf";
                break;
            case DBDm:
            case DBOracle:
            case DBPG://Postgre数据源
                ext = ".dbf";
                break;
            default:
                break;
        }
        return ext;
    }

    /**
     * 验证数据库名称：①、不能为空；②、不能包含特殊字符；③、必须少于32个字符；④、不能与已有数据库同名……
     *
     * @param dbName 数据库名称
     * @return 返回错误提示，为空则表示名称合法
     */
    private String validateDBName(String dbName) {
        String errorMsg = "";
        if (XString.isNullOrEmpty(dbName)) {
            errorMsg = "数据库名称不能为空。";
        } else {
            if (XString.indexOfAny(dbName, GISDefaultValues.getInvalidNameChars()) >= 0 || dbName.contains(" ")) {
                errorMsg = String.format("数据库名称不能包含空格和下列任何字符之一: \n      \\ / : * ? \" \" < > |");
            } else if (XString.getStringByteLength(dbName) > 32) {
                errorMsg = "数据库名称必须少于32个字符。";
            } else if (this.server.getDBID(dbName) > 0) {
                errorMsg = "该数据源下已经存在同名数据库。";
            } else if (dbName.equals(CommonFunctions.getAdministratorName(this.server))) {
                errorMsg = "地理数据库不能与当前数据源的数据库管理员同名。";
            } else if (GISDefaultValues.getReservedFileNameList().contains(dbName.toUpperCase())) {
                errorMsg = "数据库不能与系统保留设备重名。";
            }
        }
        return errorMsg;
    }

    private void okButtonClick(ActionEvent event) {
        ConnectType conType = this.server.getConnectType();
        String dbName = this.textFieldName.getText();
        String errorMsg = this.validateDBName(dbName);
        Node errorNode = null;
        if (!XString.isNullOrEmpty(errorMsg)) {
            errorNode = this.textFieldName;
        } else {
            switch (conType) {
                case Local:
                case LocalPlus: {
                    String path = this.buttonEditPath.getText();
                    if (XString.isNullOrEmpty(path)) {
                        errorNode = this.buttonEditPath;
                        errorMsg = "请指定数据库文件的存储位置。";
                    }
                    break;
                }
                case DBSQL:
                case DBMySQL:
                case DBDm:
                case DBDB2:
                case DBGBase:
                case DBBeyon:
                case DBSybase:
                case DBOracle:
                case DBPG://Postgre数据源
                case DBKBS://金仓数据源支持-zkj
                {
                    if (this.createMode == 1) {
                        String pswd = this.passwordField.getText();
                        String pswd2 = this.passwordField2.getText();
                        if (XString.isNullOrEmpty(pswd)) {
                            errorMsg = "数据库管理员口令不能为空。";
                            errorNode = this.passwordField;
                        } else if (!pswd.equals(pswd2)) {
                            errorMsg = "数据库管理员口令不匹配。";
                            errorNode = this.passwordField;
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }

        if (!XString.isNullOrEmpty(errorMsg)) {
            event.consume();
            MessageBox.information(errorMsg, this.getCurrentWindow());
            if (errorNode != null) {
                errorNode.requestFocus();
            }
        } else {
            //region 数据文件和日志/其他/索引文件
            //数据文件
            DBFileInfo dataFileInfo = null;
            if (this.createMode == 1)//新建
            {
                dataFileInfo = new DBFileInfo();
                String path = this.buttonEditPath.getText();
                if (!path.endsWith("/") && !path.endsWith("\\")) {
                    if (path.contains("/")) {
                        path += path.contains("/") ? "/" : "\\";
                    }
                }
                path += dbName + this.getDBFileExtension();
                dataFileInfo.setFilePath(path);
                dataFileInfo.setInitSize(10);

                FileExtendInfo extendInfo = new FileExtendInfo();
                extendInfo.setExtendable(true);
                extendInfo.setExtendMode(FileExtendMode.Size);
                extendInfo.setExtendSize(2);
                extendInfo.setExtendUnit(FileExtendUnit.Mbyte);
                extendInfo.setMaxFileSize(0);
                dataFileInfo.setExtendInfo(extendInfo);
            }

            DBFileInfo logFileInfo = null;
            DBFileInfo itFileInfo = null;
            switch (conType) {
                case DBSQL:
                case DBMySQL:
                case DBDm:
                case DBDB2:
                case DBGBase:
                case DBBeyon:
                case DBSybase:
                case DBOracle:
                case DBPG://Postgre数据源
                case DBKBS://金仓数据源支持-zkj
                {
                    //其他/索引文件
                    itFileInfo = new DBFileInfo();
                    itFileInfo.setFilePath(this.server.getDefaultDataPath());
                    itFileInfo.setInitSize(20);
                    FileExtendInfo itExtendInfo = new FileExtendInfo();
                    itExtendInfo.setExtendable(true);
                    itExtendInfo.setExtendMode(FileExtendMode.Size);
                    itExtendInfo.setExtendSize(20);
                    itExtendInfo.setExtendUnit(FileExtendUnit.Mbyte);
                    itFileInfo.setExtendInfo(itExtendInfo);
                    break;
                }
                default:
                    break;
            }
            //endregion

            GDBCreateParam createParam = new GDBCreateParam();
            createParam.setGDBName(dbName);
            createParam.setGDBOwner(dbName);
            if (this.passwordField != null) {
                createParam.setGOwnerPsw(this.passwordField.getText());
            }
            createParam.setDataFileInfos(new DBFileInfo[]{dataFileInfo});
            if (logFileInfo != null) {
                createParam.setLogFileInfos(new DBFileInfo[]{logFileInfo});
            }
            createParam.setIndexFileInfo(itFileInfo);

            logText = "<html><head><title>MapGIS地理数据库创建日志</title></head><body><font style=\"font-family: \'宋体\'\">\n<center><h1>MapGIS地理数据库创建日志</h1><h3>创建于 <i>" + LocalDate.now().toString() + "</i></h3></center></font>\n<hr style=\"width: 75%; height: 3px;\" noshade/>\n\n";//字体，不翻译
            LogEventReceiver logEventReceiver = new LogEventReceiver();
            logEventReceiver.addStepStartListener(stepName ->
            {
                if (!XString.isNullOrEmpty(stepName)) {
                    if (this.stepLevel == 0)
                        stepName = "开始" + stepName;
                    writeStepTextLog(stepName, ProgressStatus.Succeeded, true);
                    this.stepLevel++;
                }
            });
            logEventReceiver.addStepMessageListener(message ->
            {
                if (!XString.isNullOrEmpty(message)) {
                    //message来自底层，所以把最后的换行符处理下
                    if (message.endsWith("\r\n")) {
                        message = message.substring(0, message.length() - 2);
                    } else if (message.endsWith("\r") || message.endsWith("\n")) {
                        message = message.substring(0, message.length() - 1);
                    }
                    if (message.startsWith("\r\n")) {
                        message = message.substring(2);
                    } else if (message.startsWith("\r") || message.endsWith("\n")) {
                        message = message.substring(1);
                    }

                    int left = 40 * (this.stepLevel + 1);
                    this.logText += "<br/><span style=\"padding-left:" + left + "px\"><font color=\"blue\" style=\"font-family: \'宋体\'\" style=\"font-size: 16px\"><b>";//字体，不翻译
                    this.logText += (new Date()).toString() + " -------> " + message + "</b></font></span>";
                }
            });
            logEventReceiver.addStepEndListener((status, progress, stepName, isAppendLog) ->
            {
                this.stepLevel--;
                //设置进度，progress
                if (!isAppendLog) {
                    stepName = "";
                }
                switch (status) {
                    case Succeeded:
                        stepName += " 成功。";
                        break;
                    case Failed:
                        stepName += " 失败。";
                        break;
                    default:
                        break;
                }
                writeStepTextLog(stepName, status, isAppendLog);
            });
            System.out.println("---------------");
            DataBase db = this.server.createGDB(createParam, logEventReceiver);
            System.out.println("-------------------------");
            logText += "</body></html>";
            if (db == null || this.logCheckBox.isSelected()) {
                String logFilePath = XPath.combine(XPath.getTemp(), "GDBSvrInslog.html");
                try {
                    File logFile = new File(logFilePath);
                    if (!logFile.exists()) {
                        if (!logFile.getParentFile().exists()) {
                            logFile.mkdirs();
                        }
                        logFile.createNewFile();
                        logFile = new File(logFilePath); //重新实例化
                    }
                    FileOutputStream logStream = new FileOutputStream(logFile, false);
                    Writer logWriter = new OutputStreamWriter(logStream, "utf-8");
                    if (!XString.isNullOrEmpty(logText)) {
                        logWriter.write(logText);
                    }
                    logWriter.close();
                    logStream.flush();
                    logStream.close();

                    if (XFunctions.isSystemWindows()) {
                        Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + logFilePath);
                    } else if (XFunctions.isSystemLinux()) {
                        try {
                            String[] browsers = {"/usr/share/uosbrowser/uosbrowser", "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"};
                            String browser = null;
                            for (String br : browsers) {
                                // 这里是如果进程创建成功了，==0是表示正常结束。
                                if (Runtime.getRuntime().exec(new String[]{"which", br}).waitFor() == 0) {
                                    browser = br;
                                    break;
                                }
                            }

                            System.out.println("browser: " + browser);
                            if (browser == null) {
                                MessageBox.information("未找到任何可用的浏览器，无法查看日志文件。");
                            } else {// 这个值在上面已经成功的得到了一个进程。
                                Runtime.getRuntime().exec(new String[]{browser, logFilePath});
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("显示日志文件出错。");
                }
            }
        }
    }

    /**
     * 写步骤日志
     *
     * @param log
     * @param status
     * @param isAppendLog
     */
    private void writeStepTextLog(String log, ProgressStatus status, boolean isAppendLog) {
        if (!XString.isNullOrEmpty(log)) {
            //log来自底层，所以把最后的换行符处理下
            if (log.endsWith("\r\n")) {
                log = log.substring(0, log.length() - 2);
            } else if (log.endsWith("\r") || log.endsWith("\n")) {
                log = log.substring(0, log.length() - 1);
            }
            if (log.startsWith("\r\n")) {
                log = log.substring(2);
            } else if (log.startsWith("\r") || log.endsWith("\n")) {
                log = log.substring(1);
            }
        }
        if (isAppendLog) {
            int left = 40 * (this.stepLevel + 1);
            this.logText += "<br/><span style=\"padding-left:" + left + "px\">";
        }
        switch (status) {
            case Succeeded:
                this.logText += "<font color=\"black\" style=\"font-family: \'宋体\'\" style=\"font-size: 16px\"><b>";//字体，不翻译
                if (isAppendLog) {
                    this.logText += (new Date()).toString() + " -------> ";
                }
                break;
            case Failed:
                this.logText += "<font color=\"red\" style=\"font-family: \'宋体\'\" style=\"font-size: 16px\"><b>";//字体，不翻译
                if (isAppendLog) {
                    this.logText += (new Date()).toString() + " |ERROR|> ";
                }
                break;
            default:
                break;
        }
        this.logText += log + "</b></font>";
        if (isAppendLog)
            this.logText += "</span>";
    }

    /**
     * @author CR
     * @file CreateGDBTask.java
     * @brief 创建数据库
     * @create 2020-04-16.
     */
    public class CreateGDBTask extends Task<DataBase> {
        private GDBCreateParam createParam;
        private LogEventReceiver logEventReceiver;

        public CreateGDBTask(GDBCreateParam createParam, LogEventReceiver logEventReceiver) {
            this.createParam = createParam;
            this.logEventReceiver = logEventReceiver;
        }

        @Override
        protected DataBase call() {
            return CreateGDBDialog.this.server.createGDB(createParam, logEventReceiver);
        }
        //endregion
    }
}
