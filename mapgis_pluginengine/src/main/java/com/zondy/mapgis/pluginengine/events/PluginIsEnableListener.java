package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 插件是否可用事件监听器
 *
 * @author cxy
 * @date 2019/09/16
 */
public interface PluginIsEnableListener extends EventListener {
    /**
     * 触发插件是否可用事件
     *
     * @param pluginIsEnableEvent 插件是否可用事件
     * @return true/false
     */
    boolean pluginIsEnable(PluginIsEnableEvent pluginIsEnableEvent);
}
