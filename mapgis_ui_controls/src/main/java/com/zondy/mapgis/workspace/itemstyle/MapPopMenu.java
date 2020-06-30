package com.zondy.mapgis.workspace.itemstyle;

import com.zondy.mapgis.map.*;
import com.zondy.mapgis.workspace.engine.IMenuItem;
import com.zondy.mapgis.workspace.engine.ISinglePopMenu;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import com.zondy.mapgis.workspace.menuitem.*;

/**
 * 地图右键菜单
 *
 * @author cxy
 * @date 2019/11/11
 */
public class MapPopMenu implements ISinglePopMenu {
    private IWorkspace workspace;
    private IMenuItem[] menuItems;

    /**
     * 地图右键菜单
     */
    public MapPopMenu() {
        // TODO: 右键菜单补充
        menuItems = new IMenuItem[10];
        menuItems[0] = new PreviewMapMenuItem();
        menuItems[1] = new AddLayerMenuItem();
        menuItems[2] = new AddGroupMenuItem();
        menuItems[3] = new SortByDefaultRuleMenuItem();
        menuItems[4] = new SaveToAnnClsMenuItem();
        menuItems[5] = new ExportGraphicsDataMenuItem();
//        menuItems[6] = new MapStyle();
        menuItems[7] = new DeleteMapSceneMenuItem();
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
        boolean enable = false;
        if (item instanceof Map) {
            Map map = (Map) item;
            enable = this.workspace.getMapControl(map) != null;
            enable &= this.hasLabelLayer(map);
        }
        IMenuItem target = null;
        // TODO: 添加 SaveToAnnClsMenuItem 后，取消注释
//        for (IMenuItem menuItem : this.menuItems) {
//            if (menuItem instanceof SaveToAnnClsMenuItem) {
//                target = menuItem;
//                break;
//            }
//        }
        if (target != null) {
            this.workspace.setMenuItemEnable(target, enable);
        }
    }

    /**
     * 是否存在动态注记显示的图层
     *
     * @param item 文档项
     * @return true/false
     */
    private boolean hasLabelLayer(DocumentItem item) {
        boolean rtn = false;
        if (item instanceof VectorLayer) {
            VectorLayer layer = (VectorLayer) item;
//            Label label = layer.getLabel();
//            if (label != null && label.getVisible()) {
//                rtn = true;
//            }
        } else if (item instanceof GroupLayer) {
            GroupLayer gLayer = (GroupLayer) item;
            int count = gLayer.getCount();
            for (int i = 0; i < count; i++) {
                if (this.hasLabelLayer(gLayer.item(i))) {
                    rtn = true;
                    break;
                }
            }
        } else if (item instanceof Map) {
            Map map = (Map) item;
            int count = map.getLayerCount();
            for (int i = 0; i < count; i++) {
                if (this.hasLabelLayer(map.getLayer(i))) {
                    rtn = true;
                    break;
                }
            }
        }
        return rtn;
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
