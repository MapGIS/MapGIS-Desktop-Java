package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 应用程序关闭前事件监听器
 *
 * @author cxy
 * @date 2019/10/15
 */
public interface ApplicationClosingListener extends EventListener {
    /**
     * 触发应用程序关闭前事件
     * @param applicationClosingEvent 应用程序关闭前事件
     */
    void applicationClosing(ApplicationClosingEvent applicationClosingEvent);
}
