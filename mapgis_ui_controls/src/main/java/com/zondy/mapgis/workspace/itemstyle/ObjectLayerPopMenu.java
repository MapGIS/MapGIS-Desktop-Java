package com.zondy.mapgis.workspace.itemstyle;

import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.map.LayerState;
import com.zondy.mapgis.map.ObjectLayer;
import com.zondy.mapgis.workspace.engine.IMenuItem;
import com.zondy.mapgis.workspace.engine.ISinglePopMenu;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import com.zondy.mapgis.workspace.menuitem.*;

/**
 * @author cxy
 * @date 2020/04/27
 */
public class ObjectLayerPopMenu implements ISinglePopMenu {
    private IWorkspace workspace;
    private IMenuItem[] menuItems;

    public ObjectLayerPopMenu() {
        menuItems = new IMenuItem[7];
        menuItems[0] = new VisibleLayerMenuItem();
        menuItems[1] = new UnVisibleLayerMenuItem();
        menuItems[2] = new EditLayerMenuItem();
        menuItems[3] = new CurEditLayerMenuItem();
        menuItems[4] = new DeleteLayerMenuItem();
        menuItems[5] = new RenameItemMenuItem();
        menuItems[6] = new DocumentItemPropertyMenuItem();
    }

    /**
     * 菜单展开前过程
     *
     * @param item 文档项
     */
    @Override
    public void opening(DocumentItem item) {
        if (item instanceof ObjectLayer) {
            LayerState layerState = ((ObjectLayer) item).getState();
            workspace.setMenuItemChecked(menuItems[0], layerState == LayerState.Visible);
            workspace.setMenuItemChecked(menuItems[1], layerState == LayerState.UnVisible);
            workspace.setMenuItemChecked(menuItems[2], layerState == LayerState.Editable);
            workspace.setMenuItemChecked(menuItems[3], layerState == LayerState.Active);
        }
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
