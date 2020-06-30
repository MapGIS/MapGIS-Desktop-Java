package com.zondy.mapgis.workspace.event;

import com.zondy.mapgis.workspace.engine.IMenuItem;

import java.util.EventObject;

/**
 * 设置右键菜单是否选中事件
 *
 * @author cxy
 * @date 2019/11/11
 */
public class SetMenuItemCheckedEvent extends EventObject {
    private transient IMenuItem menuItem;
    private transient boolean checked;

    /**
     * 设置右键菜单是否选中事件
     *
     * @param source   事件源
     * @param menuItem 菜单项
     * @param checked  是否选中
     * @throws IllegalArgumentException if source is null.
     */
    public SetMenuItemCheckedEvent(Object source, IMenuItem menuItem, boolean checked) {
        super(source);
        this.menuItem = menuItem;
        this.checked = checked;
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
     * 获取是否选中
     *
     * @return 是否选中
     */
    public boolean isChecked() {
        return checked;
    }
}
