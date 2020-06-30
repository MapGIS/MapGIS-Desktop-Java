package com.zondy.mapgis.workspace.event;

import java.util.EventListener;

/**
 * 执行单选节点右键菜单事件监听器
 *
 * @author cxy
 * @date 2019/11/20
 */
public interface MenuItemClickListener extends EventListener {
    /**
     * 触发执行单选节点右键菜单事件
     *
     * @param menuItemClickEvent 执行单选节点右键菜单事件
     */
    public void fireMenuItemClick(MenuItemClickEvent menuItemClickEvent);
}
