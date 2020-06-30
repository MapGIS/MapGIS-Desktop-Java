package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 销毁 DockWindow 事件触发器
 *
 * @author cxy
 * @date 2019/09/16
 */
public interface DestroyDockWindowListener extends EventListener {
    /**
     * 触发销毁 DockWindow 事件
     *
     * @param destroyDockWindowEvent 销毁 DockWindow 事件
     */
    void destroyDockWindow(DestroyDockWindowEvent destroyDockWindowEvent);
}
