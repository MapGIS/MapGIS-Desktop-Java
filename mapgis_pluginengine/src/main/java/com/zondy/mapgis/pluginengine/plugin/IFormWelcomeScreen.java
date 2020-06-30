package com.zondy.mapgis.pluginengine.plugin;

import javafx.scene.image.Image;

/**
 * 欢迎屏插件
 *
 * @author cxy
 * @date 2019/11/07
 */
public interface IFormWelcomeScreen {
    // TODO: 2019/09/11 返回值原为 winform 的 DialogResult，在 javafx 中用什么替代
    /**
     * 获取欢迎屏显示框的返回值
     *
     * @return 返回值
     */
    Object getResult();

    /**
     * 获取欢迎屏中显示的图片
     *
     * @return 显示的图片
     */
    Image getScreenImage();

    /**
     * 关闭欢迎屏
     */
    void close();

    /**
     * 显示欢迎屏
     */
    void show();
}
