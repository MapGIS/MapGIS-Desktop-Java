package com.zondy.mapgis.workspace.event;

import java.util.EventListener;

/**
 * 移除菜单项监听器
 *
 * @author cxy
 * @date 2019/10/25
 */
public interface RemoveMenuItemListener extends EventListener {
    /**
     * 触发移除菜单项
     *
     * @param removeMenuItemEvent 移除菜单项
     */
    public void fireRemoveMenuItem(RemoveMenuItemEvent removeMenuItemEvent);
}
