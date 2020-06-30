package com.zondy.mapgis.workspace.itemstyle;

import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.map.LayerState;
import com.zondy.mapgis.scene.TerrainLayer;
import com.zondy.mapgis.workspace.engine.IMenuItem;
import com.zondy.mapgis.workspace.engine.ISinglePopMenu;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import com.zondy.mapgis.workspace.menuitem.*;

/**
 * 三维地形图层右键菜单
 *
 * @author cxy
 * @date 2019/11/13
 */
public class TerrainLayerPopMenu implements ISinglePopMenu {
    private IWorkspace workspace;
    private IMenuItem[] menuItems;

    /**
     * 三维地形图层右键菜单
     */
    public TerrainLayerPopMenu() {
        menuItems = new IMenuItem[12];
        menuItems[0] = new VisibleLayerMenuItem();
        menuItems[1] = new UnVisibleLayerMenuItem();
        menuItems[2] = new EditLayerMenuItem();
        menuItems[3] = new CurEditLayerMenuItem();
//        menuItems[4] = new AddMapRefLayer();
        menuItems[5] = new AddLabelLayerMenuItem();
        menuItems[6] = new AddModelLayerMenuItem();
        menuItems[7] = new AddPntVector3DLayerMenuItem();
        menuItems[8] = new SetCurrentDisplayRangeMenuItem();
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
        if (item instanceof TerrainLayer) {
            TerrainLayer terrainLayer = (TerrainLayer) item;
            LayerState layerState = terrainLayer.getState();
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
