package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 应用程序关闭后事件监听器
 *
 * @author cxy
 * @date 2019/10/15
 */
public interface ApplicationClosedListener extends EventListener {
    /**
     * 触发应用程序关闭后事件
     *
     * @param applicationClosedEvent 应用程序关闭后事件
     */
    void applicationClosed(ApplicationClosedEvent applicationClosedEvent);
}
