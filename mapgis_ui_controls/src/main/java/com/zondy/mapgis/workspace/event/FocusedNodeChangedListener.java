package com.zondy.mapgis.workspace.event;

import java.util.EventListener;

/**
 * 焦点节点改变事件监听器
 *
 * @author cxy
 * @date 2019/11/20
 */
public interface FocusedNodeChangedListener extends EventListener {
    /**
     * 触发焦点节点改变事件
     *
     * @param focusedNodeChangedEvent 焦点节点改变事件
     */
    public void fireFocusedNodeChanged(FocusedNodeChangedEvent focusedNodeChangedEvent);
}
