package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 应用程序加载事件监听器
 *
 * @author cxy
 * @date 2019/10/15
 */
public interface ApplicationLoadedListener extends EventListener {
    /**
     * 触发应用程序加载事件
     *
     * @param applicationLoadedEvent 应用程序加载事件
     */
    void applicationLoaded(ApplicationLoadedEvent applicationLoadedEvent);
}
