package com.zondy.mapgis.workspace.event;

import com.zondy.mapgis.workspace.engine.IMenuItem;
import javafx.scene.image.Image;

import java.util.EventObject;

/**
 * 设置右键菜单图标事件
 *
 * @author cxy
 * @date 2019/11/11
 */
public class SetMenuItemImageEvent extends EventObject {
    private transient IMenuItem menuItem;
    private transient Image image;

    /**
     * 设置右键菜单图标事件
     *
     * @param source 事件源
     * @param menuItem 菜单项
     * @param image 图标
     * @throws IllegalArgumentException if source is null.
     */
    public SetMenuItemImageEvent(Object source, IMenuItem menuItem, Image image) {
        super(source);
        this.menuItem = menuItem;
        this.image = image;
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
     * 获取图标
     *
     * @return 图标
     */
    public Image getImage() {
        return image;
    }
}
