package com.zondy.mapgis.workspace.menuitem;

import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.geodatabase.IVectorCls;
import com.zondy.mapgis.geometry.Rect;
import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.map.VectorLayer;
import com.zondy.mapgis.scene.Map3DLayer;
import com.zondy.mapgis.workspace.StaticFunction;
import com.zondy.mapgis.workspace.engine.ISingleMenuItem;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import javafx.scene.image.Image;

/**
 * 重置空间范围
 *
 * @author cxy
 * @date 2019/11/18
 */
public class ResetSpatialRangeMenuItem implements ISingleMenuItem {
    private IWorkspace workspace;

    /**
     * 点击菜单项
     *
     * @param item 文档项
     */
    @Override
    public void onClick(DocumentItem item) {
        if (item instanceof VectorLayer) {
            VectorLayer layer = (VectorLayer) item;
            if (layer.getData() instanceof IVectorCls) {
                IVectorCls vCls = (IVectorCls) layer.getData();
                vCls.calRange();
                MapControl mc = this.workspace.getMapControl(StaticFunction.getOwnerMap(item));
                if (mc != null) {
                    Rect rect = layer.getRange();
                    if (mc.getMap().getIsProjTrans()) {
                        // TODO: 待添加 mc.getTransformation().SetSourceSRef()
//                        mc.getTransformation().SetSourceSRef(layer.getSrefInfo());
                        double[] temp = mc.getTransformation().lpToMp(rect.getXMin(), rect.getYMin());
                        double mXMin = temp[0];
                        double mYMin = temp[1];
                        temp = mc.getTransformation().lpToMp(rect.getXMax(), rect.getYMax());
                        double mXMax = temp[0];
                        double mYMax = temp[1];
                        StaticFunction.setDisplayScale(mc, StaticFunction.restoreRect(StaticFunction.calcRotateRect(mc, new Rect(mXMin, mYMin, mXMax, mYMax))));
                    } else {
                        StaticFunction.setDisplayScale(mc, StaticFunction.restoreRect(StaticFunction.calcRotateRect(mc, rect)));
                    }
                    // TODO: 待添加接口
//                    if (ViewInfoHelp.GetSoonFresh(mc))
//                        mc.TryRefresh();
                }
            }
        } else if (item instanceof Map3DLayer) {
            Map3DLayer map3DLayer = (Map3DLayer) item;
            if (map3DLayer.getData() instanceof IVectorCls) {
                IVectorCls vCls = (IVectorCls) map3DLayer.getData();
                vCls.calRange();
                SetCurrentDisplayRangeMenuItem scdr = new SetCurrentDisplayRangeMenuItem();
                scdr.onCreate(this.workspace);
                scdr.onClick(item);
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
        return new Image(getClass().getResourceAsStream("/Png_ResetSpatialRange_16.png"));
    }

    /**
     * 获取命令按钮的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "重置空间范围";
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
        return false;
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
