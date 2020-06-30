package com.zondy.mapgis.workspace.event;

import com.zondy.mapgis.workspace.engine.IMenuItem;

import java.util.EventObject;

/**
 * 设置右键菜单是否可见事件
 *
 * @author cxy
 * @date 2019/11/11
 */
public class SetMenuItemVisibleEvent extends EventObject {
    private transient IMenuItem menuItem;
    private transient boolean visible;

    /**
     * 设置右键菜单是否可见事件
     *
     * @param source   事件源
     * @param menuItem 菜单项
     * @param visible  是否可见
     * @throws IllegalArgumentException if source is null.
     */
    public SetMenuItemVisibleEvent(Object source, IMenuItem menuItem, boolean visible) {
        super(source);
        this.menuItem = menuItem;
        this.visible = visible;
    }

    /**
     * 获取菜单项
     *
     * @return 菜单项
     */
    public IMenuItem getMenuItem() {
        return menuItem;
    }

    /**
     * 获取是否可见
     *
     * @return 是否可见
     */
    public boolean isVisible() {
        return visible;
    }
}
