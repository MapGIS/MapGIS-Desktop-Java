package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 插件加载事件监听器
 *
 * @author cxy
 * @date 2019/09/11
 */
public interface PluginLoadedListener extends EventListener {
    /**
     * 插件加载回调函数
     *
     * @param pluginLoadedEvent 插件加载事件
     */
    void pluginLoaded(PluginLoadedEvent pluginLoadedEvent);
}
