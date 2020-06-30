package com.zondy.mapgis.workspace.itemstyle;

import com.zondy.mapgis.map.*;
import com.zondy.mapgis.workspace.engine.IMenuItem;
import com.zondy.mapgis.workspace.engine.IMultiPopMenu;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import com.zondy.mapgis.workspace.menuitem.*;

/**
 * 多选图层右键菜单
 *
 * @author cxy
 * @date 2019/11/13
 */
public class MultiLayerPopMenu implements IMultiPopMenu {
    private IWorkspace workspace;
    private IMenuItem[] menuItems;

    /**
     * 多选图层右键菜单
     */
    public MultiLayerPopMenu() {
        menuItems = new IMenuItem[10];
        menuItems[0] = new MultiVisibleLayerMenuItem();
        menuItems[1] = new MultiUnVisibleLayerMenuItem();
        menuItems[2] = new MultiEditLayerMenuItem();
//        menuItems[3] = new MultiSave6xProperty();
//        menuItems[4] = new MultiUnSave6xProperty();
        menuItems[5] = new RemoveLayersMenuItem();
        menuItems[6] = new MakeGroupLayersMenuItem();
        menuItems[7] = new RemoveGroupsMenuItem();
        menuItems[8] = new ResetGroupSpatialRangeMenuItem();
        menuItems[9] = new MultiLayerPropertyMenuItem();
    }

    /**
     * 菜单展开前过程
     *
     * @param items 文档项
     */
    @Override
    public void opening(DocumentItem[] items) {
        // 不考虑地图集图幅图层 MapSetFrmLayer 和地图集层类 MapSetClsLayer
        if (items != null && items.length > 0 && menuItems != null) {
            DocumentItem firstItem = items[0];
            // region 编辑、移除、成组功能的可见性

            boolean hasGroupLayer = false;
            for (DocumentItem item : items) {
                // TODO: 图层类型补充完整
                if (item instanceof GroupLayer && !(item instanceof FileLayer6x || item instanceof NetClsLayer/* || item instanceof MapSetLayer*/)) {
                    hasGroupLayer = true;
                    break;
                }
            }
            boolean b6xSubLayer = firstItem.getParent() instanceof FileLayer6x;
            // TODO: 添加类之后取消注释
            for (IMenuItem mItem : menuItems) {
//            if (mItem instanceof MultiEditLayer) {
//                workspace.setMenuItemEnable(mItem, !hasGroupLayer);
//            } else if (mItem instanceof RemovLayers || mItem instanceof MakeGroupLayers) {
//                workspace.setMenuItemEnable(mItem, !b6xSubLayer);
//            } else if (mItem instanceof RemoveGroups) {
//                workspace.setMenuItemEnable(mItem, hasGroupLayer);
//            } else {
//                workspace.setMenuItemEnable(mItem, true);
//            }
            }

            // endregion

            // region 保存、不保存的可见性（多选图层全为6x图层时可见）

            boolean all6x = true;
            for (DocumentItem item : items) {
                if (!(item instanceof FileLayer6x)) {
                    all6x = false;
                    break;
                }
            }
            workspace.setMenuItemEnable(menuItems[3], all6x);
            workspace.setMenuItemEnable(menuItems[4], all6x);

            // endregion
            // region 编辑、可见、不可见的Checked属性

            int iUnVisible = 0;
            int iVisible = 0;
            int iEditable = 0;
            for (DocumentItem item : items) {
                if (item instanceof GroupLayer) {
                    int state = getGroupLayerStateFromSubLayer((GroupLayer)item);
                    if (state == 0) {
                        iUnVisible++;
                    } else if (state == 2) {
                        iVisible++;
                    } else {
                        break;
                    }
                } else if (item instanceof MapLayer) {
                    MapLayer layer = (MapLayer) item;
                    if (layer.getState() == LayerState.UnVisible) {
                        iUnVisible++;
                    } else if (layer.getState() == LayerState.Visible) {
                        iVisible++;
                    } else {
                        iEditable++;
                    }
                }
                if (iUnVisible + iVisible + iEditable > Math.max(iUnVisible, Math.max(iVisible, iEditable))) {
                    break;//三个非负数的和大于三个数中最大的一个数，说明三个数至少有两个不为0
                }
            }

            workspace.setMenuItemChecked(menuItems[0], iVisible == items.length);
            workspace.setMenuItemChecked(menuItems[1], iUnVisible == items.length);
            workspace.setMenuItemChecked(menuItems[2], iEditable == items.length);

            // endregion
        }
    }

    /**
     * 根据组图层的子图层的状态获取组图层的状态
     *
     * @param group 组图层
     * @return 返回0表不可见；返回1表中间状态；返回2表可见
     */
    private int getGroupLayerStateFromSubLayer(GroupLayer group) {
        int iRtn = 0;
        if (group.getCount() == 0) {
            if (group.getState() == LayerState.Visible) {
                iRtn = 2;
            }
        } else {
            int iUnVisible = 0;
            int iVisible = 0;
            for (int i = 0; i < group.getCount(); i++) {
                MapLayer layer = group.item(i);
                if (layer instanceof GroupLayer) {
                    int state = getGroupLayerStateFromSubLayer((GroupLayer) layer);
                    if (state == 0) {
                        iUnVisible++;
                    } else if (state == 2) {
                        iVisible++;
                    } else {
                        iRtn = 1;
                        break;
                    }
                } else {
                    if (layer.getState() == LayerState.UnVisible) {
                        iUnVisible++;
                    } else {
                        iVisible++;
                    }
                }
                if (iVisible > 0 && iUnVisible > 0) {
                    iRtn = 1;//既有可见的，也有不可见的
                    break;
                }
            }
            //全可见
            if (iVisible == group.getCount()) {
                iRtn = 2;
            }
        }
        return iRtn;
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
