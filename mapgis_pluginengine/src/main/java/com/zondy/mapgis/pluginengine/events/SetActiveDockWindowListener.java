package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 设置激活 DockWindow 事件触发器
 *
 * @author cxy
 * @date 2019/09/16
 */
public interface SetActiveDockWindowListener extends EventListener {
    /**
     * 触发设置激活 DockWindow 事件
     *
     * @param setActiveDockWindowEvent 设置激活 DockWindow 事件
     */
    void setActiveDockWindow(SetActiveDockWindowEvent setActiveDockWindowEvent);
}
