package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 插件是否可见事件监听器
 *
 * @author cxy
 * @date 2019/09/16
 */
public interface PluginIsVisibleListener extends EventListener {
    /**
     * 触发插件是否可见事件
     *
     * @param pluginIsVisibleEvent 插件是否可见事件
     * @return true/false
     */
    boolean pluginIsVisible(PluginIsVisibleEvent pluginIsVisibleEvent);
}
