package com.zondy.mapgis.workspace.menuitem;

import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.map.*;
import com.zondy.mapgis.scene.Map3DLayer;
import com.zondy.mapgis.workspace.StaticFunction;
import com.zondy.mapgis.workspace.engine.ISingleMenuItem;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import javafx.scene.image.Image;

/**
 * 当前编辑
 *
 * @author cxy
 * @date 2019/11/18
 */
public class CurEditLayerMenuItem implements ISingleMenuItem {
    private IWorkspace workspace;

    /**
     * 点击菜单项
     *
     * @param item 文档项
     */
    @Override
    public void onClick(DocumentItem item) {
        if (item instanceof MapLayer) {
            MapLayer layer = (MapLayer) item;
            boolean refresh = layer.getState() == LayerState.UnVisible;
            if (layer instanceof FileLayer6x) {
                FileLayer6x layer6X = (FileLayer6x) layer;
                for (int i = 0; i < layer6X.getCount(); i++) {
                    MapLayer sLayer = layer6X.item(i);
                    refresh = sLayer.getState() == LayerState.UnVisible;
                    sLayer.setState(LayerState.Active);
                }
            } else {
                layer.setState(LayerState.Active);
            }

            if (refresh) {
                // region 处理地图集

//                if (layer instanceof MapSetClsLayer && layer.getParent() instanceof MapSetLayer) {
//                    MapSet mapSet = (MapSet) ((MapSetLayer) item.getParent()).GetData();
//                    if (mapSet != null) {
//                        ArrayList<Integer> msLayerInfos = mapSet.GetAllLayIDList(false);
//                        if (msLayerInfos != null) {
//                            for (int i = 0; i < msLayerInfos.size(); i++) {
//                                MapSetLayInfo msLayerInfo = mapSet.GetLayInfo(msLayerInfos[i]);
//                                if (msLayerInfo.Name == ((MapSetClsLayer) item).Name) {
//                                    msLayerInfo.IsDisp = true;
//                                    mapSet.UpdateLayInfo(msLayerInfos.get(i), msLayerInfo);
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                }

                // endregion

                Map map = StaticFunction.getOwnerMap(item);
                MapControl mc = this.workspace.getMapControl(map);
                if (mc != null/* && ViewInfoHelp.GetSoonFresh(mc)*/) {
                    mc.refreshWnd();
                }
            }
        } else if (item instanceof Map3DLayer) {
            Map3DLayer layer = (Map3DLayer) item;
            layer.setState(LayerState.Active);
        }
    }

    /**
     * 获取菜单项图标
     *
     * @return 菜单项图标
     */
    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_NodeActive_16.png"));
    }

    /**
     * 获取命令按钮的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "当前编辑";
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
