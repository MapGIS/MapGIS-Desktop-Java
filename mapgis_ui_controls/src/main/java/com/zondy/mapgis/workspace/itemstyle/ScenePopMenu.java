package com.zondy.mapgis.workspace.itemstyle;

import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.workspace.engine.IMenuItem;
import com.zondy.mapgis.workspace.engine.ISinglePopMenu;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import com.zondy.mapgis.workspace.menuitem.*;

/**
 * 场景右键菜单
 *
 * @author cxy
 * @date 2019/11/13
 */
public class ScenePopMenu implements ISinglePopMenu {
    private IMenuItem[] menuItems;

    /**
     * 场景右键菜单
     */
    public ScenePopMenu() {
        menuItems = new IMenuItem[9];
        menuItems[0] = new PreviewSceneMenuItem();
        menuItems[1] = new Add3DLayerSubMenu();
//        menuItems[2] = new AddMapRefLayer();
//        menuItems[3] = new AddPointCloudLayer();
        menuItems[4] = new AddGroupMenuItem();
        menuItems[5] = new SortByDefaultRuleMenuItem();
        menuItems[6] = new DeleteMapSceneMenuItem();
        menuItems[7] = new RenameItemMenuItem();
        menuItems[8] = new DocumentItemPropertyMenuItem();
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

    }
}
