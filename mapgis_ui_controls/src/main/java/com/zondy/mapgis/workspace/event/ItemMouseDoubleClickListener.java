package com.zondy.mapgis.workspace.event;

import java.util.EventListener;

/**
 * 双击节点事件监听器
 *
 * @author cxy
 * @date 2019/11/20
 */
public interface ItemMouseDoubleClickListener extends EventListener {
    /**
     * 触发双击节点事件
     *
     * @param itemMouseDoubleClickEvent 双击节点事件
     */
    public void fireItemMouseDoubleClick(ItemMouseClickEvent itemMouseDoubleClickEvent);
}
