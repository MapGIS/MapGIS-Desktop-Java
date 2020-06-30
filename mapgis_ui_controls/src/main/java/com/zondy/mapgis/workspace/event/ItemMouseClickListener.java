package com.zondy.mapgis.workspace.event;

import java.util.EventListener;

/**
 * 单击节点事件监听器
 *
 * @author cxy
 * @date 2019/11/20
 */
public interface ItemMouseClickListener extends EventListener {
    /**
     * 触发单击节点事件
     *
     * @param itemMouseClickEvent 单击节点事件
     */
    public void fireItemMouseClick(ItemMouseClickEvent itemMouseClickEvent);
}
