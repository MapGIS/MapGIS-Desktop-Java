package com.zondy.mapgis.workspace.itemstyle;

import com.zondy.mapgis.geodatabase.XClsType;
import com.zondy.mapgis.map.*;
import com.zondy.mapgis.workspace.engine.IMenuItem;
import com.zondy.mapgis.workspace.engine.ISinglePopMenu;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import com.zondy.mapgis.workspace.menuitem.*;

/**
 * 矢量图层右菜单
 *
 * @author cxy
 * @date 2019/11/11
 */
public class VectorLayerPopMenu implements ISinglePopMenu {
    private IWorkspace workspace;
    private IMenuItem[] menuItems;

    /**
     * 矢量图层右菜单
     */
    public VectorLayerPopMenu() {
        menuItems = new IMenuItem[12];
        menuItems[0] = new VisibleLayerMenuItem();
        menuItems[1] = new UnVisibleLayerMenuItem();
        menuItems[2] = new EditLayerMenuItem();
        menuItems[3] = new CurEditLayerMenuItem();
        menuItems[4] = new SetCurrentDisplayRangeMenuItem();
        menuItems[5] = new ResetSpatialRangeMenuItem();
        menuItems[6] = new DisplayScaleSubMenu();
        menuItems[7] = new ExportAddSubMenu();
//        menuItems[8] = new LayerStyle();
        menuItems[9] = new DeleteLayerMenuItem();
        menuItems[10] = new RenameItemMenuItem();
        menuItems[11] = new DocumentItemPropertyMenuItem();
    }

    /**
     * 菜单展开前过程
     *
     * @param item 文档项
     */
    @Override
    public void opening(DocumentItem item) {
        if (item instanceof MapLayer) {
            MapLayer mapLayer = (MapLayer) item;
            LayerState layerState = mapLayer.getState();
            workspace.setMenuItemChecked(menuItems[0], layerState == LayerState.Visible);
            workspace.setMenuItemChecked(menuItems[1], layerState == LayerState.UnVisible);
            workspace.setMenuItemChecked(menuItems[2], layerState == LayerState.Editable);
            workspace.setMenuItemChecked(menuItems[3], layerState == LayerState.Active);

            DocumentItem parItem = mapLayer.getParent();
            if (parItem instanceof GroupLayer && ((MapLayer) parItem).getClsType() == XClsType.XMosaicDS) {
                workspace.setMenuItemEnable(menuItems[2], false);
                workspace.setMenuItemEnable(menuItems[3], false);
                workspace.setMenuItemEnable(menuItems[5], false);
                workspace.setMenuItemEnable(menuItems[7], false);
                workspace.setMenuItemEnable(menuItems[8], false);
                workspace.setMenuItemEnable(menuItems[9], false);
                workspace.setMenuItemEnable(menuItems[10], true);
            } else {
                workspace.setMenuItemEnable(menuItems[2], true);
                workspace.setMenuItemEnable(menuItems[3], true);
                workspace.setMenuItemEnable(menuItems[5], true);
                workspace.setMenuItemEnable(menuItems[7], true);
                workspace.setMenuItemEnable(menuItems[8], true);
                workspace.setMenuItemEnable(menuItems[9], !(parItem instanceof FileLayer6x || parItem instanceof NetClsLayer));
                workspace.setMenuItemEnable(menuItems[10], !(parItem instanceof FileLayer6x || parItem instanceof NetClsLayer));
            }
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
        this.workspace = ws;
    }
}
