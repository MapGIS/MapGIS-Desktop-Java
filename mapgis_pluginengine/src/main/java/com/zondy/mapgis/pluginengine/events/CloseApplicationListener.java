package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 关闭应用程序事件监听器
 *
 * @author cxy
 * @date 2019/11/07
 */
public interface CloseApplicationListener extends EventListener {
    /**
     * 触发关闭应用程序事件
     *
     * @param closeApplicationEvent 关闭应用程序事件
     */
    void closeApplication(CloseApplicationEvent closeApplicationEvent);
}
