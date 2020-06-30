package com.zondy.mapgis.workspace.itemstyle;

import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.map.LayerState;
import com.zondy.mapgis.scene.Group3DLayer;
import com.zondy.mapgis.scene.Map3DLayer;
import com.zondy.mapgis.scene.TerrainLayer;
import com.zondy.mapgis.workspace.engine.IMenuItem;
import com.zondy.mapgis.workspace.engine.IMultiPopMenu;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import com.zondy.mapgis.workspace.menuitem.*;

/**
 * 多选三维图层右键菜单
 *
 * @author cxy
 * @date 2019/11/13
 */
public class Multi3DLayerPopMenu implements IMultiPopMenu {
    private IWorkspace workspace;
    private IMenuItem[] menuItems;

    /**
     * 多选三维图层右键菜单
     */
    public Multi3DLayerPopMenu() {
        menuItems = new IMenuItem[7];
        menuItems[0] = new MultiEditG3DLayerMenuItem();
        menuItems[1] = new MultiVisibleG3DLayerMenuItem();
        menuItems[2] = new MultiUnVisibleG3DLayerMenuItem();
        menuItems[3] = new RemoveG3DLayersMenuItem();
        menuItems[4] = new MakeGroup3DLayersMenuItem();
        menuItems[5] = new RemoveGroupsMenuItem();
        menuItems[6] = new MultiLayerPropertyMenuItem();
    }

    /**
     * 菜单展开前过程
     *
     * @param items 文档项
     */
    @Override
    public void opening(DocumentItem[] items) {
        if (items != null && items.length > 0) {
            // region 设置菜单项“可见”、“不可见”的Checked属性
            int iUnVisible = 0;
            int iVisible = 0;
            int iEditable = 0;
            for (DocumentItem item : items) {
                if (item instanceof Map3DLayer) {
                    Map3DLayer layer = (Map3DLayer) item;
                    if (layer.getState() == LayerState.UnVisible) {
                        iUnVisible++;
                    } else if (layer.getState() == LayerState.Visible) {
                        iVisible++;
                    } else {
                        iEditable++;
                    }
                }
                if (iUnVisible + iVisible + iEditable > Math.max(iUnVisible, Math.min(iVisible, iEditable))) {
                    break;//三个非负数的和大于三个数中最大的一个数，说明三个数至少有两个不为0
                }
            }
            if (iEditable == items.length) {//全编辑
                workspace.setMenuItemChecked(menuItems[0], false);
                workspace.setMenuItemChecked(menuItems[1], false);
            } else if (iVisible == items.length) {//全可见
                workspace.setMenuItemChecked(menuItems[0], true);
                workspace.setMenuItemChecked(menuItems[1], false);
            } else if (iUnVisible == items.length) {//全不可见
                workspace.setMenuItemChecked(menuItems[0], false);
                workspace.setMenuItemChecked(menuItems[1], true);
            } else {
                workspace.setMenuItemChecked(menuItems[0], false);
                workspace.setMenuItemChecked(menuItems[1], false);
            }
            // endregion
            // region 取消组功能的可见性

            boolean hasGroupLayer = false;
            for (DocumentItem item : items) {
                if (item instanceof Group3DLayer && !(item instanceof TerrainLayer)) {
                    hasGroupLayer = true;
                    break;
                }
            }
            for (IMenuItem mItem : menuItems) {
                // TODO: 待相应类补充完成取消注释
//                if (mItem instanceof RemoveGroups) {
//                    workspace.setMenuItemEnable(mItem, hasGroupLayer);
//                } else if (mItem instanceof MultiEditG3DLayer) {
//                    workspace.setMenuItemEnable(mItem, !hasGroupLayer);
//                }
            }
            // endregion
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
