package com.zondy.mapgis.base;

import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

/**
 * Created by Administrator on 2020/3/11.
 */
public class Notification
{
    /**
     * 显示提示（默认3秒后关闭，显示到owner中间）
     *
     * @param owner 父控件
     * @param title 标题
     * @param text  提示内容
     */
    public static void showInformation(javafx.stage.Window owner, String title, String text)
    {
        showInformation(owner, title, text, Pos.CENTER);
    }

    /**
     * 显示提示
     *
     * @param owner 父控件
     * @param title 标题
     * @param text  提示内容
     * @param pos   显示位置，相对于父控件owner
     */
    public static void showInformation(javafx.stage.Window owner, String title, String text, Pos pos)
    {
        showInformation(owner, title, text, pos, 3);
    }

    /**
     * 显示提示
     *
     * @param owner            父控件
     * @param title            标题
     * @param text             提示内容
     * @param hideAfterSeconds 几秒后关闭
     * @param pos              显示位置，相对于父控件owner
     */
    public static void showInformation(javafx.stage.Window owner, String title, String text, Pos pos, int hideAfterSeconds)
    {
        Notifications note = Notifications.create()
                .owner(owner)
                .title(title)
                .text(text)
                .hideAfter(Duration.seconds(hideAfterSeconds))
                .position(pos);
        note.showInformation();
    }

    /**
     * 显示警告
     *
     * @param owner 父控件
     * @param title 标题
     * @param text  提示内容
     */
    public static void showWarning(javafx.stage.Window owner, String title, String text)
    {
        showInformation(owner, title, text, Pos.CENTER);
    }

    /**
     * 显示警告
     *
     * @param owner 父控件
     * @param title 标题
     * @param text  提示内容
     * @param pos   显示位置，相对于父控件owner
     */
    public static void showWarning(javafx.stage.Window owner, String title, String text, Pos pos)
    {
        showInformation(owner, title, text, pos, 3);
    }

    /**
     * 显示警告
     *
     * @param owner            父控件
     * @param title            标题
     * @param text             提示内容
     * @param hideAfterSeconds 几秒后关闭
     * @param pos              显示位置，相对于父控件owner
     */
    public static void showWarning(javafx.stage.Window owner, String title, String text, Pos pos, int hideAfterSeconds)
    {
        Notifications note = Notifications.create()
                .owner(owner)
                .title(title)
                .text(text)
                .hideAfter(Duration.seconds(hideAfterSeconds))
                .position(pos);
        note.showWarning();
    }
}
