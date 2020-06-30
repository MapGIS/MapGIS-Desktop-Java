package com.zondy.mapgis.workspace.itemstyle;

import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.workspace.engine.IMenuItem;
import com.zondy.mapgis.workspace.engine.ISinglePopMenu;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import com.zondy.mapgis.workspace.menuitem.*;

/**
 * 三维组图层右键菜单
 *
 * @author cxy
 * @date 2019/11/13
 */
public class Group3DLayerPopMenu implements ISinglePopMenu {
    private IWorkspace workspace;
    private IMenuItem[] menuItems;

    /**
     * 三维组图层右键菜单
     */
    public Group3DLayerPopMenu() {
        // TODO: 右键菜单补充
        menuItems = new IMenuItem[8];
        menuItems[0] = new Add3DLayerSubMenu();
//        menuItems[1] = new AddMapRefLayer();
//        menuItems[2] = new AddPointCloudLayer();
        menuItems[3] = new AddGroupMenuItem();
        menuItems[4] = new RemoveGroupMenuItem();
        menuItems[5] = new DeleteLayerMenuItem();
        menuItems[6] = new RenameItemMenuItem();
        menuItems[7] = new DocumentItemPropertyMenuItem();
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
        workspace = ws;
    }
}
