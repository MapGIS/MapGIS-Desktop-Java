package com.zondy.mapgis.pluginengine.plugin;

import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.enums.DockingStyleEnum;
import javafx.scene.Node;
import javafx.scene.image.Image;

/**
 * 停靠面板插件
 *
 * @author cxy
 * @date 2019/09/10
 */
public interface IDockWindow extends IPlugin {
    // TODO: 2019/09/10 BitMap 暂用 Image 替代
    /**
     * 停靠窗口的图标
     *
     * @return 图标
     */
    Image getBitmap();

    /**
     * 停靠窗口的标题
     *
     * @return 标题
     */
    String getCaption();

    /**
     * 停靠窗口上停靠的用户控件
     *
     * @return 用户控件
     */
    Node getChildHWND();

    /**
     * 停靠窗口的默认停靠位置
     *
     * @return 默认停靠位置
     */
    DockingStyleEnum getDefaultDock();

    /**
     * 停靠窗口是否初始加载
     *
     * @return true/false
     */
    boolean isInitCreate();

    /**
     * 停靠窗口的激活状态发生改变时引发的事件
     * @param isActive 停靠窗口的激活状态
     */
    void onActive(boolean isActive);

    /**
     * 停靠窗口被创建时引发的事件
     * @param app 框架的宿主对象
     */
    void onCreate(IApplication app);

    /**
     * 停靠窗口被销毁时引发的事件
     */
    void onDestroy();
}
