package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 插件卸载事件监听器
 *
 * @author cxy
 * @date 2019/09/11
 */
public interface PluginUnLoadedListener extends EventListener {
    /**
     * 插件卸载回调函数
     *
     * @param pluginUnLoadedEvent 插件卸载事件
     */
    void pluginUnLoaded(PluginUnLoadedEvent pluginUnLoadedEvent);
}
