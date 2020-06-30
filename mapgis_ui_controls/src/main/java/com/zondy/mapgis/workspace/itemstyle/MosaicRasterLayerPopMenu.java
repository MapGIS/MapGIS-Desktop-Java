package com.zondy.mapgis.workspace.itemstyle;

import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.map.LayerState;
import com.zondy.mapgis.map.RasterLayer;
import com.zondy.mapgis.workspace.engine.IMenuItem;
import com.zondy.mapgis.workspace.engine.ISinglePopMenu;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import com.zondy.mapgis.workspace.menuitem.*;

/**
 * 镶嵌数据集图层右键菜单
 *
 * @author cxy
 * @date 2019/11/13
 */
public class MosaicRasterLayerPopMenu implements ISinglePopMenu {
    private IWorkspace workspace;
    private IMenuItem[] menuItems;

    /**
     * 镶嵌数据集图层右键菜单
     */
    public MosaicRasterLayerPopMenu() {
        menuItems = new IMenuItem[6];
        menuItems[0] = new VisibleLayerMenuItem();
        menuItems[1] = new UnVisibleLayerMenuItem();
        menuItems[2] = new SetCurrentDisplayRangeMenuItem();
        menuItems[3] = new DisplayScaleSubMenu();
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
        if (item instanceof RasterLayer) {
            LayerState layerState = ((RasterLayer) item).getState();
            workspace.setMenuItemChecked(menuItems[0], layerState == LayerState.Visible);
            workspace.setMenuItemChecked(menuItems[1], layerState == LayerState.UnVisible);
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
