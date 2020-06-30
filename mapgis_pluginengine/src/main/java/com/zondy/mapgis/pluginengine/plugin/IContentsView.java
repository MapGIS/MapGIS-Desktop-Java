package com.zondy.mapgis.pluginengine.plugin;

import com.zondy.mapgis.pluginengine.IApplication;
import javafx.scene.Node;
import javafx.scene.image.Image;

/**
 * 内容视图插件
 *
 * @author cxy
 * @date 2019/09/10
 */
public interface IContentsView extends IPlugin{
    /**
     * 内容视图的图标
     *
     * @return 图标
     */
    Image getImage();

    /**
     * 内容视图的标题
     *
     * @return 标题
     */
    String getCaption();

    /**
     * 在内容视图的标题栏是否显示控件框，即是否显示关闭按钮
     *
     * @return true/false
     */
    boolean isControlBox();

    /**
     * 内容视图是否初始加载
     *
     * @return true/false
     */
    boolean isInitCreate();

    /**
     * 停靠在内容视图上的用户控件
     *
     * @return 用户控件
     */
    Node getObjecthWnd();

    /**
     * 内容视图的激活状态发生改变时引发的事件
     *
     * @param isActive 内容视图的激活状态
     */
    void onActive(boolean isActive);

    /**
     * 内容视图被关闭时引发的事件
     */
    void onClose();

    /**
     * 内容视图被创建时引发的事件
     *
     * @param app 框架的宿主对象
     */
    void onCreate(IApplication app);
}
