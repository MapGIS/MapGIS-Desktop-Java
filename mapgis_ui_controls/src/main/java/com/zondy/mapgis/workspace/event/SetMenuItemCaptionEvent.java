package com.zondy.mapgis.workspace.event;

import com.zondy.mapgis.workspace.engine.IMenuItem;

import java.util.EventObject;

/**
 * 设置右键菜单标题事件
 *
 * @author cxy
 * @date 2019/11/11
 */
public class SetMenuItemCaptionEvent extends EventObject {
    private transient IMenuItem menuItem;
    private transient String caption;

    /**
     * 设置右键菜单标题事件
     *
     * @param source   事件源
     * @param menuItem 菜单项
     * @param caption  标题
     * @throws IllegalArgumentException if source is null.
     */
    public SetMenuItemCaptionEvent(Object source, IMenuItem menuItem, String caption) {
        super(source);
        this.menuItem = menuItem;
        this.caption = caption;
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
     * 获取标题
     *
     * @return 标题
     */
    public String getCaption() {
        return caption;
    }
}
