package com.zondy.mapgis.workspace.menuitem;

import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.map.LayerState;
import com.zondy.mapgis.map.Map;
import com.zondy.mapgis.scene.Group3DLayer;
import com.zondy.mapgis.scene.Map3DLayer;
import com.zondy.mapgis.workspace.engine.IMultiMenuItem;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import javafx.scene.image.Image;

/**
 * 可见
 *
 * @author cxy
 * @date 2019/11/18
 */
public class MultiVisibleG3DLayerMenuItem implements IMultiMenuItem {
    private IWorkspace workspace;

    /**
     * 点击菜单项
     *
     * @param items 文档项
     */
    @Override
    public void onClick(DocumentItem[] items) {
        if (items != null && items.length > 0) {
            workspace.beginUpdateTree();
            for (DocumentItem item : items) {
                if (item.getClass() == Group3DLayer.class) {
                    ((Group3DLayer) item).setState(LayerState.UnVisible);
                }
            }
            for (DocumentItem item : items) {
                if (item instanceof Map3DLayer) {
                    Map3DLayer layer = (Map3DLayer) item;
                    layer.setState(LayerState.Visible);
                }
            }
            workspace.endUpdateTree();
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
