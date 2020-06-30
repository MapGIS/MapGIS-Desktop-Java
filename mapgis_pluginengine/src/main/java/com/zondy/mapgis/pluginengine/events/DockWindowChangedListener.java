package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 激活停靠面板改变事件监听器
 *
 * @author cxy
 * @date 2019/09/11
 */
public interface DockWindowChangedListener extends EventListener {
    /**
     * 激活停靠面板改变回调函数
     *
     * @param dockWindowChangedEvent 停靠面板改变事件
     */
    void dockWindowChanged(DockWindowChangedEvent dockWindowChangedEvent);
}
