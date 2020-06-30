package com.zondy.mapgis.workspace.event;

import com.zondy.mapgis.workspace.engine.IMenuItem;

import java.util.EventObject;

/**
 * 设置右键菜单是否可用事件
 *
 * @author cxy
 * @date 2019/11/11
 */
public class SetMenuItemEnableEvent extends EventObject {
    private transient IMenuItem menuItem;
    private transient boolean enable;

    /**
     * 设置右键菜单是否可用事件
     *
     * @param source   事件源
     * @param menuItem 菜单项
     * @param enable   是否可用
     * @throws IllegalArgumentException if source is null.
     */
    public SetMenuItemEnableEvent(Object source, IMenuItem menuItem, boolean enable) {
        super(source);
        this.menuItem = menuItem;
        this.enable = enable;
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
     * 获取是否可用
     *
     * @return 是否可用
     */
    public boolean isEnable() {
        return enable;
    }
}
