package com.zondy.mapgis.sref;

import com.zondy.mapgis.base.MessageBox;
import com.zondy.mapgis.base.XString;
import com.zondy.mapgis.common.MapGISErrorDialog;
import com.zondy.mapgis.geodatabase.Server;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author CR
 * @file ConnectGisServerDialog.java
 * @brief 连接MapGIS数据源对话框
 * @create 2019-11-06.
 */
public class ConnectGisServerDialog extends Dialog
{
    public static boolean isRememberPassword;
    public static Map<String, LinkedHashMap<String, String>> dsLogInfo = new HashMap<>();//记录数据源及其连接信息（数据源名称及其用户名密码对集合）
    private static final String secretKey = "MapGIS_DataCenter";//保存数据源连接密码时的机密密钥

    private Server ds;
    private String serverName;//数据源名称
    private LinkedHashMap<String, String> userPasswords;

    public ConnectGisServerDialog(final String serverName)
    {
        this.serverName = serverName;

        //region 初始化界面
        this.setTitle(String.format("连接到 %s", this.serverName));

        TextField textUser = new TextField();
        PasswordField textPassword = new PasswordField();
        CheckBox checkRemenber = new CheckBox("记住密码");
        checkRemenber.setSelected(ConnectGisServerDialog.isRememberPassword);

        GridPane gridPane = new GridPane();
        gridPane.setVgap(6);
        gridPane.setHgap(6);
        gridPane.add(new Label("用户名:"), 0, 0);
        gridPane.add(new Label("密码:"), 0, 1);
        gridPane.add(textUser, 1, 0);
        gridPane.add(textPassword, 1, 1);
        GridPane.setHgrow(textUser, Priority.ALWAYS);
        gridPane.add(checkRemenber, 1, 2);

        DialogPane dialogPane = this.getDialogPane();
        dialogPane.setContent(gridPane);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.setPrefWidth(320);
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, event ->
        {
            String user = textUser.getText();
            String pswd = textPassword.getText();
            String alertText = "";
            if (XString.isNullOrEmpty(user))
            {
                alertText = "用户名不能为空。";
            } else if (user.length() > 60)
            {
                alertText = "用户名长度不能超过60.";
            } else if (pswd.length() > 60)
            {
                alertText = "密码长度不能超过60.";
            } else
            {
                Server server = new Server();
                if (server.connect(serverName, user, pswd) <= 0)
                {
                    alertText = "连接失败.";
                    MapGISErrorDialog.ShowLastError();
                } else
                {
                    ConnectGisServerDialog.isRememberPassword = checkRemenber.isSelected();
                    for (String key : userPasswords.keySet())
                    {
                        if (key == user)
                        {
                            userPasswords.remove(key);
                            break;
                        }
                    }

                    if (!ConnectGisServerDialog.isRememberPassword)
                    {
                        pswd = "";
                    } else
                    {
                        userPasswords.put(user, pswd);//仅当选择保存时才添加此用户登录信息，若已保存此用户则会删除该用户登录信息。
                    }

                    if (ConnectGisServerDialog.dsLogInfo.containsKey(serverName))
                    {
                        ConnectGisServerDialog.dsLogInfo.remove(serverName);
                    }
                    if (userPasswords != null && userPasswords.size() > 0)
                    {
                        ConnectGisServerDialog.dsLogInfo.put(serverName, userPasswords);
                    }

                    this.ds = server;
                }
            }

            if (!XString.isNullOrEmpty(alertText))
            {
                event.consume();
                MessageBox.information(alertText, this.getOwner());
            }
        });
        //endregion

        textUser.textProperty().addListener((observable, oldValue, newValue) ->
        {
            textPassword.setText(""); //编辑用户名时将密码置空
        });
        checkRemenber.selectedProperty().addListener((o, ov, nv) ->
        {
            if (nv)
            {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("提示");
                alert.setHeaderText(null);
                alert.setContentText("保存密码可能存在风险，是否确认保存?");
                alert.initOwner(this.getOwner());
                alert.showAndWait().ifPresent(result ->
                {
                    checkRemenber.setSelected(result == ButtonType.OK);
                });
            }
        });

        if (ConnectGisServerDialog.dsLogInfo != null)
        {
            userPasswords = ConnectGisServerDialog.dsLogInfo.get(this.serverName);
        }
        if (userPasswords != null && userPasswords.size() > 0)
        {
            textUser.setText(ConnectGisServerDialog.getTheFirst(userPasswords).getKey());
            textPassword.setText(ConnectGisServerDialog.getTheFirst(userPasswords).getValue());
        } else
        {
            userPasswords = new LinkedHashMap<>();
        }

        textUser.requestFocus();
    }

    /**
     * 获取连接的数据源
     *
     * @return
     */
    public Server getServer()
    {
        return this.ds;
    }

    /**
     * 获取登录的用户名和密码信息；用于保存到xml
     *
     * @return 格式：serverName1@sa:sa@sa1:sa|serverName2@sa:sa@sa1:sa|……
     */
    public static String getServerLoginfo()
    {
        String strLoginfo = "";
        if (ConnectGisServerDialog.dsLogInfo != null)
        {
            for (String key : ConnectGisServerDialog.dsLogInfo.keySet())
            {
                strLoginfo += (strLoginfo != "" ? "|" : "") + key;
                LinkedHashMap<String, String> ups = ConnectGisServerDialog.dsLogInfo.get(key);
                if (ups != null)
                {
                    for (String user : ups.keySet())
                    {
                        strLoginfo += "@" + user + ":" + ConnectGisServerDialog.encrypt(ups.get(user), ConnectGisServerDialog.secretKey);
                    }
                }
            }
        }
        return strLoginfo;
    }

    /**
     * 解析登录的用户名和密码信息
     *
     * @param value 格式：serverName1@sa:sa@sa1:sa|serverName2@sa:sa@sa1:sa|……
     */
    public static void setServerLoginfo(String value)
    {
        if (!XString.isNullOrEmpty(value))
        {
            String[] serverLogins = value.split("\\|");
            for (String serverLogin : serverLogins)
            {
                String[] logins = serverLogin.split("@");
                if (logins.length > 1)
                {
                    LinkedHashMap<String, String> loginInfoes = new LinkedHashMap<>();
                    for (int i = 1; i < logins.length; i++)
                    {
                        String[] userPswd = logins[i].split(":");
                        if (userPswd.length >= 2)
                        {
                            loginInfoes.put(userPswd[0], ConnectGisServerDialog.decrypt(userPswd[1], ConnectGisServerDialog.secretKey));
                        }
                    }

                    if (loginInfoes != null && loginInfoes.size() > 0)
                    {
                        ConnectGisServerDialog.dsLogInfo.put(logins[0], loginInfoes);
                    }
                }
            }
        }
    }

    //region 静态方法
    public static <K, V> Map.Entry<K, V> getTheFirst(LinkedHashMap<K, V> map)
    {
        return map != null ? map.entrySet().iterator().next() : null;
    }

    public static <K, V> Map.Entry<K, V> getTheLast(LinkedHashMap<K, V> map)
    {
        Map.Entry<K, V> last = null;
        Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
        while (iterator.hasNext())
        {
            last = iterator.next();
        }
        return last;
    }

    /**
     * 加密数据源连接密码
     *
     * @param password 原密码
     * @param strKey   密钥
     * @return 加密后的密码
     */
    public static String encrypt(String password, String strKey)
    {
        String encryptedPassword = "";
        return encryptedPassword;
    }

    /**
     * 解密数据源连接密码
     *
     * @param encryptedPassword 加密后的密码
     * @param strKey            密钥
     * @return 原密码
     */
    public static String decrypt(String encryptedPassword, String strKey)
    {
        String password = "";
        return password;
    }
    //endregion
}
