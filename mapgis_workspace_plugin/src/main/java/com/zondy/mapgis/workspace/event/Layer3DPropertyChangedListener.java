package com.zondy.mapgis.workspace.event;

import java.util.EventListener;

/**
 * 三维图层属性改变事件监听器
 *
 * @author cxy
 * @date 2019/11/20
 */
public interface Layer3DPropertyChangedListener extends EventListener {
    /**
     * 触发三维图层属性改变事件
     *
     * @param layer3DPropertyChangedEvent 三维图层属性改变事件
     */
    public void fireLayer3DPropertyChanged(Layer3DPropertyChangedEvent layer3DPropertyChangedEvent);
}
