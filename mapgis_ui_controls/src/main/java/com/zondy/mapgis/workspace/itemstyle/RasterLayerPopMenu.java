package com.zondy.mapgis.workspace.itemstyle;

import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.map.LayerState;
import com.zondy.mapgis.map.RasterLayer;
import com.zondy.mapgis.workspace.engine.*;
import com.zondy.mapgis.workspace.enums.ItemType;
import com.zondy.mapgis.workspace.menuitem.*;

/**
 * 栅格图层右键菜单
 *
 * @author cxy
 * @date 2019/11/13
 */
public class RasterLayerPopMenu implements ISinglePopMenu {
    private IWorkspace workspace;
    private IMenuItem[] menuItems;

    /**
     * 栅格图层右键菜单
     */
    public RasterLayerPopMenu() {
        menuItems = new IMenuItem[10];
        menuItems[0] = new VisibleLayerMenuItem();
        menuItems[1] = new UnVisibleLayerMenuItem();
        menuItems[2] = new EditLayerMenuItem();
        menuItems[3] = new CurEditLayerMenuItem();
        menuItems[4] = new SetCurrentDisplayRangeMenuItem();
        menuItems[5] = new DisplayScaleSubMenu();
        menuItems[6] = new ExportLayerRasterMenuItem();
        menuItems[7] = new DeleteLayerMenuItem();
        menuItems[8] = new RenameItemMenuItem();
        menuItems[9] = new DocumentItemPropertyMenuItem();
    }

    /**
     * 菜单展开前过程
     *
     * @param item 文档项
     */
    @Override
    public void opening(DocumentItem item) {
        if (item instanceof RasterLayer) {
            RasterLayer rasterLayer = (RasterLayer) item;
            LayerState layerState = rasterLayer.getState();
            workspace.setMenuItemChecked(menuItems[0], layerState == LayerState.Visible);
            workspace.setMenuItemChecked(menuItems[1], layerState == LayerState.UnVisible);
            workspace.setMenuItemChecked(menuItems[2], layerState == LayerState.Editable);
            workspace.setMenuItemChecked(menuItems[3], layerState == LayerState.Active);

            IMenuItem target = null;
            IMenuExtender lastMenu = workspace.getMenuExtender(ItemType.RASTERLAYER);
            if (lastMenu != null) {
                IMenuItem[] allMenu = lastMenu.getItems();
                for (IMenuItem menuItem : allMenu) {
                    if (menuItem instanceof ISubMenu) {
                        for (IMenuItem subMenuItem : ((ISubMenu) menuItem).getItems()) {
                            // TODO: 待验证正确性
                            if ("com.zondy.mapgis.workspace.plugin.CreateUniqueThemeByLayerInfo".equals(subMenuItem.getClass().getName())) {
                                target = subMenuItem;
                                break;
                            }
                        }
                    }
                }
            }
            if (target != null) {
                workspace.setMenuItemEnable(target, false);
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
        workspace = ws;
    }
}
