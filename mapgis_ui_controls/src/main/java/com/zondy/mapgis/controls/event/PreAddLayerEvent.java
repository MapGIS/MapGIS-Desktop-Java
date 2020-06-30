package com.zondy.mapgis.controls.event;

import java.util.EventObject;

/**
 * 预加载图层代理事件
 */
public class PreAddLayerEvent extends EventObject {
    private transient Object data;

    /**
     * 预加载图层代理事件
     *
     * @param source 事件源
     * @param data 图层的数据,即 MapLayer.GetData() 或 G3DLayer.GetData() 或 URL
     * @throws IllegalArgumentException if source is null.
     */
    public PreAddLayerEvent(Object source, Object data) {
        super(source);
        this.data = data;
    }

    /**
     * 获取图层的数据
     *
     * @return 图层的数据,即 MapLayer.GetData() 或 G3DLayer.GetData() 或 URL
     */
    public Object getData() {
        return data;
    }
}
