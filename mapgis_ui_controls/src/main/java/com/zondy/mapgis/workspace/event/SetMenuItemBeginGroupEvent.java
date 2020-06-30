package com.zondy.mapgis.workspace.event;

import com.zondy.mapgis.workspace.engine.IMenuItem;

import java.util.EventObject;

/**
 * 设置右键菜单项的 BeginGroup 属性事件
 *
 * @author cxy
 * @date 2019/11/11
 */
public class SetMenuItemBeginGroupEvent extends EventObject {
    private transient IMenuItem menuItem;
    private transient boolean beginGroup;

    /**
     * 设置右键菜单项的 BeginGroup 属性事件
     *
     * @param source 事件源
     * @param menuItem 菜单项
     * @param beginGroup 是否开始新的组
     * @throws IllegalArgumentException if source is null.
     */
    public SetMenuItemBeginGroupEvent(Object source, IMenuItem menuItem, boolean beginGroup) {
        super(source);
        this.menuItem = menuItem;
        this.beginGroup = beginGroup;
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
     * 获取是否开始新的组
     *
     * @return 是否开始新的组
     */
    public boolean isBeginGroup() {
        return beginGroup;
    }
}
