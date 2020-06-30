package com.zondy.mapgis.workspace.event;

import java.util.EventListener;

/**
 * 图层状态改变事件监听器
 *
 * @author cxy
 * @date 2019/11/20
 */
public interface StateChangedListener extends EventListener {
    /**
     * 触发图层状态改变事件
     *
     * @param stateChangedEvent 图层状态改变事件
     */
    public void fireStateChanged(StateChangedEvent stateChangedEvent);
}
