package com.zondy.mapgis.workspace.menuitem;

import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.map.LayerState;
import com.zondy.mapgis.map.Map;
import com.zondy.mapgis.map.MapLayer;
import com.zondy.mapgis.scene.Map3DLayer;
import com.zondy.mapgis.workspace.StaticFunction;
import com.zondy.mapgis.workspace.engine.ISingleMenuItem;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import javafx.scene.image.Image;

/**
 * @author cxy
 * @date 2019/11/04
 */
public class VisibleLayerMenuItem implements ISingleMenuItem {
    private IWorkspace workspace;

    /**
     * 点击菜单项
     *
     * @param item 文档项
     */
    @Override
    public void onClick(DocumentItem item) {
        if (item != null) {
            if (item instanceof MapLayer) {
                boolean refresh = false;
//                if (item instanceof MapSetClsLayer) {
//                    refresh = !((MapSetClsLayer) item).isDisp;
//                    ((MapSetClsLayer) item).setDisp(true);
//                    ((Workspace) workspace).fireMenuItemClickEvent(getClass().getName(), item);
//                } else {
                MapLayer layer = (MapLayer) item;
                refresh = layer.getState() == LayerState.UnVisible;
                layer.setState(LayerState.Visible);
//                }

                if (refresh) {
                    Map map = StaticFunction.getOwnerMap(item);
                    MapControl mc = workspace.getMapControl(map);
                    if (mc != null/* && ViewInfoHelp.GetSoonFresh(mc)*/) {
                        mc.refreshWnd();
                    }
                }
            } else if (item instanceof Map3DLayer) {
                Map3DLayer layer = (Map3DLayer) item;
                layer.setState(LayerState.Visible);
            }
        }
    }

    /**
     * 获取菜单项图标
     *
     * @return 菜单项图标
     */
    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_NodeVisible_16.png"));
    }

    /**
     * 获取命令按钮的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "可见";
    }

    /**
     * 获取命令按钮是否可用
     *
     * @return true/false
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * 获取命令按钮是否可见
     *
     * @return true/false
     */
    @Override
    public boolean isVisible() {
        return true;
    }

    /**
     * 获取命令按钮是否选中
     *
     * @return true/false
     */
    @Override
    public boolean isChecked() {
        return true;
    }

    /**
     * 获取是否启用分割符
     *
     * @return true/false
     */
    @Override
    public boolean isBeginGroup() {
        return false;
    }

    /**
     * 创建后事件
     *
     * @param ws 工作空间引擎
     */
    @Override
    public void onCreate(IWorkspace ws) {
        workspace = ws;
    }
}
