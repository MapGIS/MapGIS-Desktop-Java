package com.zondy.mapgis.workspace.menuitem;

import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.map.MapLayer;
import com.zondy.mapgis.map.ScaleRange;
import com.zondy.mapgis.workspace.StaticFunction;
import com.zondy.mapgis.workspace.engine.ISingleMenuItem;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import javafx.scene.image.Image;

/**
 * 设为最大显示比率
 *
 * @author cxy
 * @date 2019/11/18
 */
public class SetMaxDisplayScaleMenuItem implements ISingleMenuItem {
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
            MapControl mc = this.workspace.getMapControl(StaticFunction.getOwnerMap(item));
            if (mc != null) {
//                layer.setMaxScale(mc.getCurMapScale());
                ScaleRange scaleRange = layer.getScaleRange();
                scaleRange.setMaxScale(mc.getCurMapScale());
                layer.setScaleRange(scaleRange);
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
        return null;
    }

    /**
     * 获取命令按钮的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "设为最大比例尺";
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
