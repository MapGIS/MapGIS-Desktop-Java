package com.zondy.mapgis.workspace.event;

import com.zondy.mapgis.map.MapLayer;

import java.util.EventObject;

/**
 * 图层属性改变事件
 *
 * @author cxy
 * @date 2019/11/20
 */
public class LayerPropertyChangedEvent extends EventObject {
    private transient MapLayer mapLayer;
    private transient String propertyName;
    /**
     * 图层属性改变事件
     *
     * @param source 事件源
     * @param mapLayer 图层
     * @param propertyName 属性名
     * @throws IllegalArgumentException if source is null.
     */
    public LayerPropertyChangedEvent(Object source, MapLayer mapLayer, String propertyName) {
        super(source);
        this.mapLayer = mapLayer;
        this.propertyName = propertyName;
    }

    /**
     * 获取图层
     *
     * @return 图层
     */
    public MapLayer getMapLayer() {
        return mapLayer;
    }

    /**
     * 获取属性名
     *
     * @return 属性名
     */
    public String getPropertyName() {
        return propertyName;
    }
}
