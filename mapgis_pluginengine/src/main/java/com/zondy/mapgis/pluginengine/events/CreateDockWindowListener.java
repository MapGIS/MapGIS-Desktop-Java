package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 创建 DockWindow 事件监听器
 *
 * @author cxy
 * @date 2019/09/16
 */
public interface CreateDockWindowListener extends EventListener {
    /**
     * 触发创建 DockWindow 事件
     *
     * @param createDockWindowEvent 创建 DockWindow 事件
     */
    void createDockWindow(CreateDockWindowEvent createDockWindowEvent);
}
