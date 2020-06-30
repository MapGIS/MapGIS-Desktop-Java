package com.zondy.mapgis.workspace.event;

import java.util.EventListener;

/**
 * 移除三维图层事件监听器
 *
 * @author cxy
 * @date 2019/11/20
 */
public interface Remove3DLayerListener extends EventListener {
    /**
     * 触发移除三维图层事件
     *
     * @param remove3DLayerEvent 移除三维图层事件
     */
    public void fireRemove3DLayer(Remove3DLayerEvent remove3DLayerEvent);
}
