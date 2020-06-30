package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 插件激活状态改变事件监听器
 * 只针对于 ContentView 生效
 *
 * @author cxy
 * @date 2019/09/16
 */
public interface PluginActiveChangedListener extends EventListener {
    /**
     * 激活插件激活状态改变事件
     *
     * @param pluginActiveChangedEvent 插件激活状态改变事件
     */
    void pluginActiveChanged(PluginActiveChangedEvent pluginActiveChangedEvent);
}
