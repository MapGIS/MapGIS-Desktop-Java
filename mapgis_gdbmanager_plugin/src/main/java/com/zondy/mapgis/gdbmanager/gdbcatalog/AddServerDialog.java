package com.zondy.mapgis.gdbmanager.gdbcatalog;

import com.google.common.collect.ImmutableMap;
import com.zondy.mapgis.base.*;
import com.zondy.mapgis.controls.common.ZDComboBox;
import com.zondy.mapgis.geodatabase.config.*;
import com.zondy.mapgis.geodatabase.middleware.MiddleWareConfig;
import com.zondy.mapgis.geodatabase.middleware.MiddleWareConfigFactory;
import com.zondy.mapgis.geodatabase.middleware.MiddleWareInfo;
import com.zondy.mapgis.srs.SRefPrjType;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Window;

import java.net.ConnectException;
import java.net.InetAddress;
import java.util.*;

/**
 * @author CR
 * @file AddDataSourceDialog.java
 * @brief 添加数据源
 * @create 2019-12-13.
 */
public class AddServerDialog extends Dialog<DataSrcInfo> {
    private final ListView<String> listView;
    private final TextField tfDNSName;
    private final ZDComboBox<String> cbDNSName;
    private final TextField tfServerName;
    private final GridPane gridPane;
    private List<String> existedNames;//界面中现有的数据源名列表，用于检测同名数据源。由于数据源没有即时保存，所以从界面读取传递。
    private MiddleWareConfig middleWareConfig = new MiddleWareConfig();//中间件配置类对象
    private DataSrcInfo dsInfo;//当前所要添加的数据源对象
    private HashMap<ConnectType, List<String>> dnsNameMap = new HashMap<>();
    private Tooltip tooltipError = new Tooltip();//错误提示
    private String serverName;//修改的数据源的原名称

    /**
     * 构造界面
     */
    public AddServerDialog() {
        this("", ConnectType.Local);
    }

    /**
     * 构造界面
     *
     * @param dsName 数据源名称，为null时默认为新建，否则修改
     */
    public AddServerDialog(String dsName, ConnectType conType) {
        this.serverName = dsName;
        this.setTitle(XString.isNullOrEmpty(this.serverName) ? "添加数据源" : "修改数据源");
        this.setResizable(false);

        //region 创建界面控件
        this.listView = new ListView<>();
        TitledPane titledPaneType = new TitledPane("数据源类型", this.listView);
        titledPaneType.setCollapsible(false);
        titledPaneType.setPrefWidth(240);
        titledPaneType.setMinWidth(200);
        //titledPaneType.setPrefSize(200, 300);

        this.gridPane = new GridPane();
        this.gridPane.setHgap(6);
        this.gridPane.setVgap(6);
        this.gridPane.add(new Label("服务名称:"), 0, 0);
        this.gridPane.add(new Label("数据源名称:"), 0, 1);
        this.tfDNSName = new TextField();
        this.cbDNSName = new ZDComboBox<>();
        this.cbDNSName.prefWidthProperty().bind(this.tfDNSName.widthProperty());
        this.cbDNSName.setOnShowing(this::dnsComboBoxShowing);
        this.cbDNSName.valueProperty().addListener(this::dnsNameChanged);
        this.tfDNSName.textProperty().addListener(this::dnsNameChanged);
        this.tfServerName = new TextField();
        this.tfServerName.textProperty().addListener(this::serverNameChanged);
        this.gridPane.add(this.tfDNSName, 1, 0);
        this.gridPane.add(this.cbDNSName, 1, 0);
        this.gridPane.add(this.tfServerName, 1, 1);
        GridPane.setHgrow(this.tfServerName, Priority.ALWAYS);
        TitledPane titledPaneInfo = new TitledPane("服务信息", this.gridPane);
        titledPaneInfo.setCollapsible(false);

        HBox hBox = new HBox(6);
        hBox.getChildren().addAll(titledPaneType, titledPaneInfo);
        hBox.setFillHeight(true);
        HBox.setHgrow(titledPaneInfo, Priority.ALWAYS);

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setPrefSize(600, 450);
        dialogPane.setContent(hBox);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        this.setResizable(true);
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, this::okButtonClick);
        //endregion

        //region 初始化数据源类型列表
        ObservableList list = FXCollections.observableArrayList(
                this.getConnectTypeName(ConnectType.DBDm),
                this.getConnectTypeName(ConnectType.DBPG));
        if (XFunctions.isSystemWindows()) {
            list.addAll(
                    this.getConnectTypeName(ConnectType.DBOracle),
                    this.getConnectTypeName(ConnectType.DBMySQL),
                    this.getConnectTypeName(ConnectType.DBGBase),
                    this.getConnectTypeName(ConnectType.DBBeyon),
                    this.getConnectTypeName(ConnectType.DBKBS),
                    this.getConnectTypeName(ConnectType.DBKDB),
                    this.getConnectTypeName(ConnectType.DBGaussDB100));
            //上述是1.0暂不支持的，下述是linux（暂时）无法支持的
            list.addAll(this.getConnectTypeName(ConnectType.DBSQL), this.getConnectTypeName(ConnectType.DBDB2), this.getConnectTypeName(ConnectType.DBSybase));
        }
        this.middleWareConfig.open();
        for (int i = 0; i < this.middleWareConfig.count(); i++) {
            MiddleWareInfo midWare = this.middleWareConfig.getItemByIndex(i);
            if (midWare != null) {
                this.listView.getItems().add(midWare.getDisplayName());
            }
        }
        this.middleWareConfig.close();
        this.listView.setItems(list);
        this.listView.getSelectionModel().selectedItemProperty().addListener(this::focusedTypeChanged);
        //endregion

        this.listView.getSelectionModel().select(this.getConnectTypeName(XString.isNullOrEmpty(this.serverName) ? ConnectType.DBOracle : conType));
        if (!XString.isNullOrEmpty(this.serverName)) {
            this.listView.setDisable(true);
            DataSrcInfo dsInfo = SvcConfig.get(this.serverName);
            if (dsInfo != null) {
                this.tfDNSName.setText(dsInfo.getDnsName());
                this.cbDNSName.setValue(dsInfo.getDnsName());
                this.tfServerName.setText(dsInfo.getSvcName());
            }
        }
    }

    //region 事件

    /**
     * 切换数据源类型时，修改服务和数据源信息
     *
     * @param observable
     * @param oldValue
     * @param newValue
     */
    private void focusedTypeChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        this.cbDNSName.getItems().clear();
        this.tfDNSName.setText("");
        this.tfServerName.setText("");
        this.tfDNSName.setDisable(false);
        if (newValue != null) {
            ConnectType conType = this.getConnectType(newValue);
            boolean canSelect = ConnectType.DBOracle.equals(conType) || ConnectType.DBSQL.equals(conType);
            this.cbDNSName.setVisible(canSelect);
            this.tfDNSName.setVisible(!canSelect);

            if (conType == ConnectType.Custom) {
                String midWareName = newValue;
                this.middleWareConfig.open();
                MiddleWareConfigFactory mwcFactory = new MiddleWareConfigFactory(midWareName);
                if (mwcFactory.getSvrInfoList() != null && mwcFactory.getSvrInfoList().size() > 0) {
                    String svrName = mwcFactory.getSvrInfoList().get(0).getMidSvrName();
                    this.tfDNSName.setText(svrName);
                    this.tfServerName.setText(svrName.trim());
                }
                mwcFactory.dispose();
                this.middleWareConfig.close();
            } else {
                this.cbDNSName.setEditable(conType == ConnectType.DBSQL);
            }
        }
    }

    /**
     * 解析获取可添加的数据源，更新数据源下拉列表。
     *
     * @param event
     */
    private void dnsComboBoxShowing(Event event) {
        ConnectType conType = this.getConnectType(this.listView.getFocusModel().getFocusedItem());
        if (this.cbDNSName.getItems().size() == 0) {
            if (!this.dnsNameMap.containsKey(conType)) {
                List<String> list = new ArrayList<>();
                if (ConnectType.DBOracle.equals(conType)) {
                    TNSParser tnsParser = new TNSParser(1L);//封装异常。参数不知道怎么传
                    if (tnsParser.parseSys() && tnsParser.getItems() != null) {
                        for (TNSDef tns : tnsParser.getItems()) {
                            if (tns != null) {
                                list.add(tns.getTnsName());
                            }
                        }
                    }
                    tnsParser.dispose();
                } else if (ConnectType.DBSQL.equals(conType)) {
                    SQLSvrParser sqlParser = new SQLSvrParser(1L);
                    if (sqlParser.parse() && sqlParser.getCount() > 0) {
                        for (int i = 0; i < sqlParser.getCount(); i++) {
                            SQLSvrInfo info = sqlParser.getItem(i);
                            if (info != null) {
                                list.add(info.getHost());
                            }
                        }
                    }
                    sqlParser.dispose();
                }
                this.dnsNameMap.put(conType, list);
            }

            List<String> dsList = this.dnsNameMap.get(conType);
            this.cbDNSName.getItems().addAll(dsList);
        }
    }

    /**
     * 当服务名称改变时，联动修改数据源名称（去特殊字符处理）
     *
     * @param observable
     * @param oldValue
     * @param newValue
     */
    private void dnsNameChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (newValue != null) {
            this.tfServerName.setText(newValue.replaceAll("[\\s\t\\\\/:*?\"<>|@]", ""));
        }
    }

    /**
     * 数据源名称必须少于32个字符，且不能包含特殊字符
     *
     * @param observable
     * @param oldValue
     * @param newValue
     */
    private void serverNameChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (!XString.isNullOrEmpty(newValue)) {
            List<Character> invalidCharList = GISDefaultValues.getInvalidNameCharList();
            invalidCharList.addAll(Arrays.asList(' ', '@'));
            StringProperty errorMsg = new SimpleStringProperty();
            if (!XString.isTextValid(newValue, 32, invalidCharList, errorMsg)) {
                this.tfServerName.setText(oldValue);
                UIFunctions.showErrorTip(this.tfServerName, errorMsg.get(), this.tooltipError);
            }
        }
    }

    /**
     * 确定：添加数据源
     *
     * @param event
     */
    private void okButtonClick(ActionEvent event) {
        ConnectType conType = this.getConnectType(this.listView.getFocusModel().getFocusedItem());
        if (conType == ConnectType.DBDm || this.validateServerAndName()) {
            String tnsName = this.cbDNSName.isVisible() ? this.cbDNSName.getValue() : this.tfDNSName.getText();

            if (conType == ConnectType.Custom) {
                tnsName = this.listView.getFocusModel().getFocusedItem();

                // 底层定义 MAX_SVC_NAME_LEN 为32，因此存储的数据源名称和tnsName长度不得超过31。
                // 所以用户可输入字段长度为 31 - "&" - tnsName。

                int usrLength = 31 - 1 - XString.getStringByteLength(tnsName);
                //重新构造数据源的TNSName
                this.middleWareConfig.open();
                MiddleWareInfo midWare = this.middleWareConfig.getItemByName(tnsName);
                MiddleWareConfigFactory mwcFactory = new MiddleWareConfigFactory(tnsName);
                if (mwcFactory.getSvrInfoList() != null && mwcFactory.getSvrInfoList().size() > 0) {
                    tnsName += "&" + tnsName;
                }
                this.middleWareConfig.close();
                if (XString.getStringByteLength(tnsName) > 31) {
                    MessageBox.information(String.format("直连方式连接字符串过长，需控制在%d字节数以内，建议改用TNS方式连接。", usrLength));
                    event.consume();
                }
            }

            this.dsInfo = new DataSrcInfo();
            this.dsInfo.setSvcType(conType);
            this.dsInfo.setSvcName(this.tfServerName.getText());
            this.dsInfo.setDnsName(tnsName);
            //this.dsInfo.setIpAddr(this.getIPAddress());
            this.dsInfo.setPortNo(this.getPort());
            if (XString.isNullOrEmpty(this.serverName)) {
                if (!SvcConfig.append(this.dsInfo)) {
                    this.dsInfo = null;
                    MessageBox.information("添加数据源失败。");
                }
            } else {
                for (int i = 0; i < SvcConfig.count(); i++) {
                    DataSrcInfo dsInfo = SvcConfig.get(i);
                    if (dsInfo != null && this.serverName.equals(dsInfo.getSvcName())) {
                        if (!SvcConfig.set(i, this.dsInfo)) {
                            this.dsInfo = null;
                            MessageBox.information("修改数据源失败。");
                        }
                        break;
                    }
                }
            }
        } else {
            event.consume();
        }
    }
    //endregion

    /**
     * 返回当前添加的数据源
     *
     * @return 当前添加的数据源
     */
    public DataSrcInfo getServerInfo() {
        return this.dsInfo;
    }

    //region 内部方法

    private static final Map<ConnectType, String> connectTypeMap = new ImmutableMap.Builder<ConnectType, String>().
            put(ConnectType.DBOracle, "ORACLE 数据源").
            put(ConnectType.DBSQL, "SQL SERVER 数据源").
            put(ConnectType.DBMySQL, "MySQL 数据源").
            put(ConnectType.DBDB2, "DB2 数据源").
            put(ConnectType.DBDm, "DM 数据源").
            put(ConnectType.DBSybase, "SYBASE 数据源").
            put(ConnectType.DBGBase, "GBASE 数据源").
            put(ConnectType.DBBeyon, "Beyon 数据源").
            put(ConnectType.DBKbx, "金仓 数据源").
            put(ConnectType.DBInf, "ODBC 数据源").
            put(ConnectType.DBKDB, "KDB 数据源").
            put(ConnectType.DBKBS, "KINGBASE 数据源").
            put(ConnectType.DBPG, "PostgreSQL 数据源").
            put(ConnectType.DBGaussDB100, "高斯 数据源").build();

    /**
     * 根据界面显示的中文类型获取ConnectType
     *
     * @return ConnectType
     */
    private ConnectType getConnectType(String typeName) {
        return LanguageConvert.getKey(connectTypeMap, typeName);
    }

    /**
     * 获取数据源类型的名称字串
     *
     * @param type
     * @return
     */
    private String getConnectTypeName(ConnectType type) {
        String str = "";
        if (connectTypeMap.containsKey(type)) {
            str = connectTypeMap.get(type);
        }
        return str;
    }

    /**
     * 获取当前操作的数据源的IP地址
     *
     * @return 当前数据源的主机
     */
    private String getIPAddress() {
        String ipAddress = this.cbDNSName.isVisible() ? this.cbDNSName.getValue() : this.tfDNSName.getText();
        if (XString.isNullOrEmpty(ipAddress)) {
            ConnectType conType = this.getConnectType(this.listView.getFocusModel().getFocusedItem());
            if (ConnectType.DBOracle.equals(conType)) {
                TNSParser tnsParser = new TNSParser(0L);
                if (tnsParser.parseSys()) {
                    TNSDef tnsDef = tnsParser.getItem(ipAddress);
                    if (tnsDef != null) {
                        ipAddress = tnsDef.getHost();
                    }
                }
                tnsParser.dispose();
            } else if (ConnectType.DBSQL.equals(conType)) {//对本地的SQL数据源服务名进行特殊处理
                try {
                    if (ipAddress == "localhost" || ipAddress == "(local)" || ipAddress == ".") {
                        ipAddress = InetAddress.getLocalHost().getHostAddress();
                    } else //一般的本地或远程SQL数据源服务名或IP
                    {
                        if (ipAddress.contains("\\")) {
                            ipAddress = ipAddress.substring(0, ipAddress.indexOf("\\"));
                        }
                        ipAddress = InetAddress.getByName(ipAddress).getHostAddress();
                    }
                } catch (Exception ex) {
                    MessageBox.information(String.format("%s\n请输入正确的主机名或IP。", ex.getMessage()));
                }
            }
        }
        return ipAddress;
    }

    /**
     * 获取当前操作的数据源的端口号（根据其类型）
     *
     * @return 当前数据源的端口号
     */
    private int getPort() {
        int port = 0;
        ConnectType conType = this.getConnectType(this.listView.getFocusModel().getFocusedItem());
        switch (conType) {
            case DBOracle: {
                TNSParser tnsParser = new TNSParser(0L);
                {
                    if (tnsParser.parseSys()) {
                        TNSDef tnsDef = tnsParser.getItem(this.cbDNSName.getValue());
                        if (tnsDef != null) {
                            port = Integer.valueOf(tnsDef.getPort());
                        }
                    }
                }
                tnsParser.dispose();
                break;
            }
            case DBSQL: {
                port = 1433;
                break;
            }
            case DBInf:
            case DBSybase:
            case DBKBS:
            case DBDm: {
                port = 0;
                break;
            }
            case DBDB2:
            case DBGBase:
            case DBBeyon: {
                port = 50000;
                break;
            }
            case DBMySQL: {
                port = 3306;
                break;
            }
        }
        return port;
    }

    /**
     * 验证服务名称和数据源名称
     *
     * @return 数据源的相关输入有效返回true，否则返回false
     */
    private boolean validateServerAndName() {
        String errorMsg = "";

        //region 服务名不能为空，Oracle数据源判断服务是否存在
        ConnectType conType = this.getConnectType(this.listView.getFocusModel().getFocusedItem());
        String tnsName = this.cbDNSName.isVisible() ? this.cbDNSName.getValue() : this.tfDNSName.getText();
        if (XString.isNullOrEmpty(tnsName)) {
            errorMsg = "数据源的服务名不能为空。";
        } else if (XString.isNullOrEmpty(tnsName.trim())) {
            errorMsg = "数据源的服务名不能全为空格。";
        } else {
            if (ConnectType.DBOracle.equals(conType)) {
                boolean canFind = false;
                if (this.cbDNSName.getItems().size() > 0) {
                    canFind = this.cbDNSName.getItems().contains(tnsName);
                } else {
                    try {
                        TNSParser tnsParser = new TNSParser(0L);
                        if (tnsParser.parseSys()) {
                            canFind = tnsParser.getItem(tnsName) != null;
                        }
                        tnsParser.dispose();
                    } catch (Exception ex) {
                        errorMsg = ex.getMessage();
                    }
                }

                if (!canFind) {
                    errorMsg = "未找到相应服务。";
                }
            }
        }
        //endregion

        if (errorMsg == "") {
            //数据源名称不能为空、不能全为空格
            String dsName = this.tfServerName.getText();
            if (XString.isNullOrEmpty(dsName)) {
                errorMsg = "数据源名不能为空。";
            } else if (this.getExistedNames().contains(dsName)) {
                errorMsg = "与已有数据源同名。";
            }
        }

        if (errorMsg != "") {
            MessageBox.information(errorMsg, this.getCurrentWindow());
        }

        return errorMsg == "";
    }

    /**
     * 获取现有名称
     *
     * @return
     */
    private List<String> getExistedNames() {
        if (this.existedNames == null) {
            this.existedNames = new ArrayList<>();//记录原有数据源名称，用于判断重名
            for (int i = 0; i < SvcConfig.count(); i++) {
                DataSrcInfo dsInfo = SvcConfig.get(i);
                if (dsInfo != null) {
                    this.existedNames.add(dsInfo.getSvcName());
                }
            }
            if (XString.isNullOrEmpty(this.serverName)) {
                this.existedNames.remove(this.serverName);
            }
        }
        return this.existedNames;
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
    //endregion
}
