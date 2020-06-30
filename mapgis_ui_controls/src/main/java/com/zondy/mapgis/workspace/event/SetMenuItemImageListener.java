package com.zondy.mapgis.workspace.event;

import javafx.scene.image.Image;

import java.util.EventListener;

/**
 * 设置右键菜单图标事件监听器
 *
 * @author cxy
 * @date 2019/11/11
 */
public interface SetMenuItemImageListener extends EventListener {
    /**
     * 触发设置右键菜单图标事件
     *
     * @param setMenuItemImageEvent 设置右键菜单图标事件
     * @return 返回菜单项最终图标
     */
    public Image fireSetMenuItemImage(SetMenuItemImageEvent setMenuItemImageEvent);
}
