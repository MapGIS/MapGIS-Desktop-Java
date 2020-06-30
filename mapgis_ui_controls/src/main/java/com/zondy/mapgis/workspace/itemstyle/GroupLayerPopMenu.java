package com.zondy.mapgis.workspace.itemstyle;

import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.workspace.engine.IMenuItem;
import com.zondy.mapgis.workspace.engine.ISinglePopMenu;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import com.zondy.mapgis.workspace.menuitem.*;

/**
 * 组图层右键菜单
 *
 * @author cxy
 * @date 2019/11/11
 */
public class GroupLayerPopMenu implements ISinglePopMenu {
    private IWorkspace workspace;
    private IMenuItem[] menuItems;

    /**
     * 组图层右键菜单
     */
    public GroupLayerPopMenu() {
        menuItems = new IMenuItem[6];
        menuItems[0] = new AddLayerMenuItem();
        menuItems[1] = new AddGroupMenuItem();
        menuItems[2] = new RemoveGroupMenuItem();
        menuItems[3] = new DeleteLayerMenuItem();
        menuItems[4] = new RenameItemMenuItem();
        menuItems[5] = new DocumentItemPropertyMenuItem();
    }

    /**
     * 菜单展开前过程
     *
     * @param item 文档项
     */
    @Override
    public void opening(DocumentItem item) {

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
        this.workspace = ws;
    }
}
