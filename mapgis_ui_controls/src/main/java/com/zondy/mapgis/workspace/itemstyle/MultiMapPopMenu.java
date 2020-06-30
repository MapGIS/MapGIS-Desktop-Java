package com.zondy.mapgis.workspace.itemstyle;

import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.workspace.engine.IMenuItem;
import com.zondy.mapgis.workspace.engine.IMultiPopMenu;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import com.zondy.mapgis.workspace.enums.ItemType;
import com.zondy.mapgis.workspace.menuitem.RemoveMapsMenuItem;

/**
 * 多选地图右键菜单
 *
 * @author cxy
 * @date 2019/11/13
 */
public class MultiMapPopMenu implements IMultiPopMenu {
    private IMenuItem[] menuItems;
    /**
     * 多选地图右键菜单
     */
    public MultiMapPopMenu() {
        menuItems = new IMenuItem[1];
        menuItems[0] = new RemoveMapsMenuItem();
    }
    /**
     * 菜单展开前过程
     *
     * @param items 文档项
     */
    @Override
    public void opening(DocumentItem[] items) {

    }

    /**
     * 获取菜单栏插件功能项的集合
     *
     * @return 菜单栏插件功能项的集合
     */
    @Override
    public IMenuItem[] getItems() {
        return menuItems;
    }

    /**
     * 创建后事件
     *
     * @param ws 工作空间
     */
    @Override
    public void onCreate(IWorkspace ws) {

    }
}
