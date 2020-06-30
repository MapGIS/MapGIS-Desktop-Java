package com.zondy.mapgis.workspace.event;

import java.util.EventListener;

/**
 * 图层属性改变事件监听器
 *
 * @author cxy
 * @date 2019/11/20
 */
public interface LayerPropertyChangedListener extends EventListener {
    /**
     * 触发图层属性改变事件
     *
     * @param layerPropertyChangedEvent 图层属性改变事件
     */
    public void fireLayerPropertyChanged(LayerPropertyChangedEvent layerPropertyChangedEvent);
}
