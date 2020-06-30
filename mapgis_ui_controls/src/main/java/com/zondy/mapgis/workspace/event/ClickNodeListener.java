package com.zondy.mapgis.workspace.event;

import java.util.EventListener;

/**
 * 单击节点事件监听器
 *
 * @author cxy
 * @date 2019/11/26
 */
public interface ClickNodeListener extends EventListener {
    /**
     * 触发单击节点事件
     *
     * @param clickNodeEvent 单击节点事件
     */
    public void fireClickNode(ClickNodeEvent clickNodeEvent);
}
