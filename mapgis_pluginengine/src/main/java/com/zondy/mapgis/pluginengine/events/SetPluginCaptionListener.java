package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 设置插件标题事件监听器
 *
 * @author cxy
 * @date 2020/06/05
 */
public interface SetPluginCaptionListener extends EventListener {
    /**
     * 触发设置插件标题事件
     *
     * @param setPluginCaptionEvent 设置插件标题事件
     */
    void setPluginCaption(SetPluginCaptionEvent setPluginCaptionEvent);
}
