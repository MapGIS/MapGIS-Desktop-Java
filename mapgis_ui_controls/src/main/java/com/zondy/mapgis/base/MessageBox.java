package com.zondy.mapgis.base;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

import javax.xml.stream.FactoryConfigurationError;
import java.util.Optional;


/**
 * @author CR
 * @file MessageBox.java
 * @brief 消息框（Alert）
 * @create 2019-11-14.
 */
public class MessageBox
{
    //region information

    /**
     * 显示消息框（按钮：确定；标题：提示）
     *
     * @param text 消息内容
     */
    public static void information(String text)
    {
        MessageBox.information(text, null);
    }

    /**
     * 显示消息框（按钮：确定；标题：提示）
     *
     * @param text  消息内容
     * @param owner 父窗口，设置后消息框不会出现在任务栏
     */
    public static void information(String text, Window owner)
    {
        MessageBox.information(text, owner, "提示");
    }

    /**
     * 显示消息框（按钮：确定）
     *
     * @param text    消息内容
     * @param owner   父窗口，设置后消息框不会出现在任务栏
     * @param caption 标题
     */
    public static void information(String text, Window owner, String caption)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, text, ButtonType.OK);
        alert.setTitle(caption);
        alert.setHeaderText(null);
        alert.initOwner(owner);
        alert.show();
    }
    //endregion

    //region question(OKCancel)

    /**
     * 显示提问框（按钮：确定+取消，标题：“提问”）
     *
     * @param text 提问内容
     * @return 按下的按钮
     */
    public static ButtonType question(String text)
    {
        return MessageBox.question(text, null);
    }

    /**
     * 显示提问框（按钮：确定+取消，标题：“提问”）
     *
     * @param text  提问内容
     * @param owner 父窗口，设置后消息框不会出现在任务栏
     * @return 按下的按钮
     */
    public static ButtonType question(String text, Window owner)
    {
        return MessageBox.question(text, null, "提问");
    }

    /**
     * 显示提问框（按钮：确定+取消）
     *
     * @param text    提问内容
     * @param owner   父窗口，设置后消息框不会出现在任务栏
     * @param caption 标题
     * @return 按下的按钮
     */
    public static ButtonType question(String text, Window owner, String caption)
    {
        ButtonType buttonType = null;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, text, ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle(caption);
        alert.setHeaderText(null);
        alert.initOwner(owner);
        Optional<ButtonType> response = alert.showAndWait();
        if (response.isPresent()) {
            buttonType = response.get();
        }
        return buttonType;
    }
    //endregion

    //region question-YesNo(Cancel)

    /**
     * 显示提问框（按钮：是否，根据参数加取消；标题：“提问”）
     *
     * @param text 提问内容
     * @return 按下的按钮
     */
    public static ButtonType questionEx(String text)
    {
        return MessageBox.questionEx(text, null);
    }

    /**
     * 显示提问框（按钮：是否，根据参数加取消；标题：“提问”）
     *
     * @param text  提问内容
     * @param owner 父窗口，设置后消息框不会出现在任务栏
     * @return 按下的按钮
     */
    public static ButtonType questionEx(String text, Window owner)
    {
        return MessageBox.questionEx(text, owner, false);
    }

    /**
     * 显示提问框（按钮：是否，根据参数加取消；标题：“提问”）
     *
     * @param text   提问内容
     * @param owner  父窗口，设置后消息框不会出现在任务栏
     * @param cancel 是否需要取消按钮
     * @return 按下的按钮
     */
    public static ButtonType questionEx(String text, Window owner, boolean cancel)
    {
        return MessageBox.questionEx(text, owner, cancel, "提问");
    }

    /**
     * 显示提问框（按钮：是否，根据参数加取消）
     *
     * @param text    提问内容
     * @param owner   父窗口，设置后消息框不会出现在任务栏
     * @param cancel  是否需要取消按钮
     * @param caption 标题
     * @return 按下的按钮
     */
    public static ButtonType questionEx(String text, Window owner, boolean cancel, String caption)
    {
        ButtonType buttonType = null;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, text, ButtonType.YES, ButtonType.NO);
        if (cancel) {
            alert.getButtonTypes().add(ButtonType.CANCEL);
        }
        alert.setTitle(caption);
        alert.setHeaderText(null);
        alert.initOwner(owner);
        Optional<ButtonType> response = alert.showAndWait();
        if (response.isPresent()) {
            buttonType = response.get();
        }
        return buttonType;
    }
    //endregion

    //region error

    /**
     * 显示错误框（按钮：确定；标题：出错）
     *
     * @param text 错误内容
     */
    public static void error(String text)
    {
        MessageBox.error(text, null);
    }

    /**
     * 显示错误框（按钮：确定；标题：出错）
     *
     * @param text  错误内容
     * @param owner 父窗口，设置后消息框不会出现在任务栏
     */
    public static void error(String text, Window owner)
    {
        MessageBox.error(text, owner, "出错");
    }

    /**
     * 显示错误框（按钮：确定）
     *
     * @param text    错误内容
     * @param owner   父窗口，设置后消息框不会出现在任务栏
     * @param caption 标题
     */
    public static void error(String text, Window owner, String caption)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR, text, ButtonType.OK);
        alert.setTitle(caption);
        alert.setHeaderText(null);
        alert.initOwner(owner);
        alert.show();
    }
    //endregion

    //region warning

    /**
     * 显示警告框（按钮：确定；标题：警告）
     *
     * @param text 错误内容
     */
    public static void warning(String text)
    {
        MessageBox.warning(text, null);
    }

    /**
     * 显示警告框（按钮：确定；标题：警告）
     *
     * @param text  错误内容
     * @param owner 父窗口，设置后消息框不会出现在任务栏
     */
    public static void warning(String text, Window owner)
    {
        MessageBox.warning(text, owner, "警告");
    }

    /**
     * 显示警告框（按钮：确定）
     *
     * @param text    错误内容
     * @param owner   父窗口，设置后消息框不会出现在任务栏
     * @param caption 标题
     */
    public static void warning(String text, Window owner, String caption)
    {
        Alert alert = new Alert(Alert.AlertType.WARNING, text, ButtonType.OK);
        alert.setTitle(caption);
        alert.setHeaderText(null);
        alert.initOwner(owner);
        alert.show();
    }
    //endregion
}
