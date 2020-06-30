package com.zondy.mapgis.workspace.engine;

/**
 * 含多级子菜单项的菜单项
 *
 * @author cxy
 * @date 2019/09/12
 */
public interface ISubMenu extends IMenuItem {
    /**
     * 获取菜单栏插件功能项的集合
     *
     * @return 菜单栏插件功能项的集合
     */
    IMenuItem[] getItems();
}
