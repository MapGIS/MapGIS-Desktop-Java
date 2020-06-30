package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 移除插件事件监听器
 *
 * @author cxy
 * @date 2019/09/16
 */
public interface PluginRemovedListener extends EventListener {
    /**
     * 触发移除插件事件
     *
     * @param pluginRemovedEvent 移除插件事件
     */
    void pluginRemoved(PluginRemovedEvent pluginRemovedEvent);
}
