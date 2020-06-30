package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 设置插件图标事件监听器
 *
 * @author cxy
 * @date 2019/09/16
 */
public interface SetPluginImageListener extends EventListener {
    /**
     * 触发设置插件图标事件
     *
     * @param setPluginImageEvent 设置插件图标事件
     */
    void setPluginImage(SetPluginImageEvent setPluginImageEvent);
}
