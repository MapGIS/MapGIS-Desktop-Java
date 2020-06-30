package com.zondy.mapgis.workspace.menuitem;

import com.zondy.mapgis.map.*;
import com.zondy.mapgis.scene.Group3DLayer;
import com.zondy.mapgis.scene.Map3DLayer;
import com.zondy.mapgis.scene.TerrainLayer;
import com.zondy.mapgis.workspace.StaticFunction;
import com.zondy.mapgis.workspace.engine.ISingleMenuItem;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;

import java.util.Optional;

/**
 * 取消组
 *
 * @author cxy
 * @date 2019/11/04
 */
public class RemoveGroupMenuItem implements ISingleMenuItem {
    private IWorkspace workspace;
    /**
     * 点击菜单项
     *
     * @param item 文档项
     */
    @Override
    public void onClick(DocumentItem item) {
        if (item instanceof GroupLayer || item instanceof Group3DLayer) {
            boolean existGroup = false;
            boolean removeAllGroup = false;
            if (item instanceof GroupLayer) {
                GroupLayer gl = (GroupLayer) item;
                for (int i = 0; i < gl.getCount(); i++) {
                    MapLayer mLayer = gl.item(i);
                    if (mLayer instanceof GroupLayer && !(mLayer instanceof NetClsLayer || mLayer instanceof FileLayer6x))// || mLayer instanceof MapSetLayer))
                    {
                        existGroup = true;
                        break;
                    }
                }
            }
            if (item instanceof Group3DLayer) {
                Group3DLayer g3l = (Group3DLayer) item;
                for (int i = 0; i < g3l.getLayerCount(); i++) {
                    Map3DLayer gLayer = g3l.getLayer(i);
                    if (gLayer instanceof Group3DLayer) {
                        existGroup = true;
                        break;
                    }
                }
            }
            workspace.beginUpdateTree();
            if (existGroup) {
                Alert alert = new Alert(Alert.AlertType.NONE, "该组包含其它组，是否全部取消？", ButtonType.YES, ButtonType.NO);
                if (alert.showAndWait().equals(Optional.of(ButtonType.YES))) {
                    removeAllGroup = true;
                }
            }
            StaticFunction.removeGroupLayer(item, removeAllGroup);
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
        return new Image(getClass().getResourceAsStream("/Png_RemoveGroupLayer_16.png"));
    }

    /**
     * 获取命令按钮的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "取消组";
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
