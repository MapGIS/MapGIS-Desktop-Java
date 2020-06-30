package com.zondy.mapgis.workspace.event;

import java.util.EventListener;

/**
 * 移除图层事件监听器
 *
 * @author cxy
 * @date 2019/11/19
 */
public interface RemoveLayerListener extends EventListener {
    /**
     * 触发移除图层事件
     *
     * @param removeLayerEvent 移除图层事件
     */
    public void fireRemoveLayer(RemoveLayerEvent removeLayerEvent);
}
