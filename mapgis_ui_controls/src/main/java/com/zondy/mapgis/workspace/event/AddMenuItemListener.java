package com.zondy.mapgis.workspace.event;

import java.util.EventListener;

/**
 * 添加菜单项事件监听器
 *
 * @author cxy
 * @date 2019/10/25
 */
public interface AddMenuItemListener extends EventListener {
    /**
     * 触发添加菜单项事件
     *
     * @param addItemEvent 添加菜单项事件
     */
    public void fireAddItem(AddMenuItemEvent addItemEvent);
}
