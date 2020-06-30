package com.zondy.mapgis.workspace.event;

import com.zondy.mapgis.scene.Map3DLayer;

import java.util.EventObject;

/**
 * 三维图层属性改变事件
 *
 * @author cxy
 * @date 2019/11/20
 */
public class Layer3DPropertyChangedEvent extends EventObject {
    private transient Map3DLayer map3DLayer;
    private transient String propertyName;

    /**
     * 三维图层属性改变事件
     *
     * @param source       事件源
     * @param map3DLayer   三维图层
     * @param propertyName 属性名
     * @throws IllegalArgumentException if source is null.
     */
    public Layer3DPropertyChangedEvent(Object source, Map3DLayer map3DLayer, String propertyName) {
        super(source);
        this.map3DLayer = map3DLayer;
        this.propertyName = propertyName;
    }

    /**
     * 获取三维图层
     *
     * @return 三维图层
     */
    public Map3DLayer getMapLayer() {
        return map3DLayer;
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
