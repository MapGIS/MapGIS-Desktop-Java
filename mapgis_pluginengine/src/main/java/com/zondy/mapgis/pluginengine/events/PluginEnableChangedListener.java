package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 插件是否可用改变事件监听器
 *
 * @author cxy
 * @date 2019/09/16
 */
public interface PluginEnableChangedListener extends EventListener {
    /**
     * 触发插件是否可用改变事件
     *
     * @param pluginEnableChangedEvent 插件是否可用改变事件
     */
    void pluginEnableChanged(PluginEnableChangedEvent pluginEnableChangedEvent);
}
