package com.zondy.mapgis.workspace.event;

import java.util.EventListener;

/**
 * 双击节点事件监听器
 *
 * @author cxy
 * @date 2019/11/26
 */
public interface DoubleClickNodeListener extends EventListener {
    /**
     * 触发双击节点事件
     *
     * @param clickNodeEvent 双击节点事件
     */
    public void fireDoubleClickNode(ClickNodeEvent clickNodeEvent);
}
