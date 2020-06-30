package com.zondy.mapgis.pluginengine.plugin;

import javafx.scene.image.Image;

/**
 * 欢迎屏插件
 *
 * @author cxy
 * @date 2019/09/11
 */
public interface IWelcomeScreen extends IPlugin {
    /**
     * 获取欢迎屏中显示的图片
     *
     * @return 显示的图片
     */
    Image getScreenImage();
}
