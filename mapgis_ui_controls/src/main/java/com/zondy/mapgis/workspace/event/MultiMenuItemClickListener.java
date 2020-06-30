package com.zondy.mapgis.workspace.event;

import java.util.EventListener;

/**
 * 执行多选节点右键菜单事件监听器
 *
 * @author cxy
 * @date 2019/11/20
 */
public interface MultiMenuItemClickListener extends EventListener {
    /**
     * 触发执行多选节点右键菜单事件
     *
     * @param multiMenuItemClickEvent 执行多选节点右键菜单事件
     */
    public void fireMultiMenuItemClick(MultiMenuItemClickEvent multiMenuItemClickEvent);
}
