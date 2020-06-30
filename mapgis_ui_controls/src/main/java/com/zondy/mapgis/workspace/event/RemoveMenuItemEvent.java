package com.zondy.mapgis.workspace.event;

import com.zondy.mapgis.workspace.engine.IMenuItem;

import java.util.EventObject;

/**
 * 移除菜单项事件
 *
 * @author cxy
 * @date 2019/10/25
 */
public class RemoveMenuItemEvent extends EventObject {
    private transient IMenuItem menuItem;

    /**
     * 移除菜单项事件
     *
     * @param source 事件源
     * @param menuItem 菜单项
     * @throws IllegalArgumentException if source is null.
     */
    public RemoveMenuItemEvent(Object source, IMenuItem menuItem) {
        super(source);
        this.menuItem = menuItem;
    }

    /**
     * 获取菜单项
     *
     * @return 菜单项
     */
    public IMenuItem getMenuItem() {
        return menuItem;
    }
}
