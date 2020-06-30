package com.zondy.mapgis.workspace.itemstyle;

import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.map.LayerState;
import com.zondy.mapgis.scene.Vector3DLayer;
import com.zondy.mapgis.workspace.engine.*;
import com.zondy.mapgis.workspace.enums.ItemType;
import com.zondy.mapgis.workspace.menuitem.*;

/**
 * 三维矢量图层右键菜单
 *
 * @author cxy
 * @date 2019/11/13
 */
public class Vector3DLayerPopMenu implements ISinglePopMenu {
    private IWorkspace workspace;
    private IMenuItem[] menuItems;

    /**
     * 三维矢量图层右键菜单
     */
    public Vector3DLayerPopMenu() {
        menuItems = new IMenuItem[11];
        menuItems[0] = new VisibleLayerMenuItem();
        menuItems[1] = new UnVisibleLayerMenuItem();
        menuItems[2] = new EditLayerMenuItem();
        menuItems[3] = new CurEditLayerMenuItem();
        menuItems[4] = new SetCurrentDisplayRangeMenuItem();
        menuItems[5] = new ResetSpatialRangeMenuItem();
        menuItems[6] = new ExportLayerMenuItem();
        menuItems[7] = new AddToLayerMenuItem();
        menuItems[8] = new DeleteLayerMenuItem();
        menuItems[9] = new RenameItemMenuItem();
        menuItems[10] = new DocumentItemPropertyMenuItem();
    }

    /**
     * 菜单展开前过程
     *
     * @param item 文档项
     */
    @Override
    public void opening(DocumentItem item) {
        if (item instanceof Vector3DLayer) {
            Vector3DLayer vector3DLayer = (Vector3DLayer) item;
            LayerState layerState = vector3DLayer.getState();
            workspace.setMenuItemChecked(menuItems[0], layerState == LayerState.Visible);
            workspace.setMenuItemChecked(menuItems[1], layerState == LayerState.UnVisible);
            workspace.setMenuItemChecked(menuItems[2], layerState == LayerState.Editable);
            workspace.setMenuItemChecked(menuItems[3], layerState == LayerState.Active);

            IMenuItem target = null;
            IMenuExtender menuExtender = workspace.getMenuExtender(ItemType.VECTOR3DLAYER);
            if (menuExtender != null) {
                IMenuItem[] allMenu = menuExtender.getItems();
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
