package com.zondy.mapgis.workspace.event;

import com.zondy.mapgis.workspace.engine.IMenuItem;

import java.util.EventObject;

/**
 * 插入菜单项事件
 *
 * @author cxy
 * @date 2019/10/25
 */
public class InsertMenuItemEvent extends EventObject {
    private transient IMenuItem menuItem;
    private transient int index;

    /**
     * 插入菜单项事件
     *
     * @param source 事件源
     * @param menuItem 菜单项
     * @param index 索引
     * @throws IllegalArgumentException if source is null.
     */
    public InsertMenuItemEvent(Object source, IMenuItem menuItem, int index) {
        super(source);
        this.menuItem = menuItem;
        this.index = index;
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
     * 获取索引
     *
     * @return 索引
     */
    public int getIndex() {
        return index;
    }
}
