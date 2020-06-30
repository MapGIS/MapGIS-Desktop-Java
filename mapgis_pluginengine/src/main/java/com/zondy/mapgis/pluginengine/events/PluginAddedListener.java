package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 添加插件事件监听器
 *
 * @author cxy
 * @date 2019/09/16
 */
public interface PluginAddedListener extends EventListener {
    /**
     * 触发添加插件事件
     *
     * @param pluginAddedEvent 添加插件事件
     */
    void pluginAdded(PluginAddedEvent pluginAddedEvent);
}
