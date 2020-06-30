package com.zondy.mapgis.controls.event;

import java.util.EventListener;

/**
 * 预加载图层代理事件监听器
 */
public interface PreAddLayerListener extends EventListener {
    /**
     * 触发预加载图层代理事件
     *
     * @param preAddLayerEvent 预加载图层代理事件
     * @return true/false
     */
    boolean firePreAddLayer(PreAddLayerEvent preAddLayerEvent);
}
