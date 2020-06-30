package com.zondy.mapgis.pluginengine.events;

/**
 * 插件是否可见改变事件监听器
 * @author cxy
 * @date 2019/09/16
 */
public interface PluginVisibleChangedListener {
    /**
     * 触发插件是否可见改变事件
     *
     * @param pluginVisibleChangedEvent 插件是否可见改变事件
     */
    void pluginVisibleChanged(PluginVisibleChangedEvent pluginVisibleChangedEvent);
}
