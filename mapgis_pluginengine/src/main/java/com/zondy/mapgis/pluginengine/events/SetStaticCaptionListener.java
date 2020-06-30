package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 设置静态框标题事件监听器
 *
 * @author cxy
 * @date 2019/09/16
 */
public interface SetStaticCaptionListener extends EventListener {
    /**
     * 触发设置静态框标题事件
     *
     * @param setStaticCaptionEvent 设置静态框标题事件
     */
    void setStaticCaption(SetStaticCaptionEvent setStaticCaptionEvent);
}
