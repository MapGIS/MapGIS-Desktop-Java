package com.zondy.mapgis.workspace.menuitem;

import com.zondy.mapgis.map.Document;
import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.map.Map;
import com.zondy.mapgis.scene.Group3DLayer;
import com.zondy.mapgis.scene.MapRefLayer;
import com.zondy.mapgis.scene.Scene;
import com.zondy.mapgis.scene.TerrainLayer;
import com.zondy.mapgis.workspace.AddMapRefLayerDialog;
import com.zondy.mapgis.workspace.engine.ISingleMenuItem;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Optional;

/**
 * 添加Map引用图层（三维场景）
 *
 * @author cxy
 * @date 2019/12/18
 */
public class AddMapRefLayerMenuItem implements ISingleMenuItem {
    private IWorkspace workspace;

    /**
     * 点击菜单项
     *
     * @param item 文档项
     */
    @Override
    public void onClick(DocumentItem item) {
        DocumentItem documentItem = item;
        while (documentItem != null) {
            if (documentItem instanceof Document) {
                break;
            } else {
                documentItem = documentItem.getParent();
            }
        }

        if (documentItem != null) {
            AddMapRefLayerDialog arld = new AddMapRefLayerDialog((Document) documentItem, this.workspace);
            if (arld.showAndWait().equals(Optional.of(ButtonType.OK))) {
                ArrayList<Map> mapList = arld.getSelectMapList();
                if (mapList.size() > 0) {
                    for (Map map : mapList) {
                        MapRefLayer mapRefLayer = new MapRefLayer();
                        mapRefLayer.setName(map.getName());
                        mapRefLayer.setURL(map.getName());
                        if (mapRefLayer.connectData()) {
                            if (item instanceof Scene) {
                                ((Scene) item).addLayer(mapRefLayer);
                            } else if (item instanceof Group3DLayer || item instanceof TerrainLayer) {
                                Group3DLayer group3DLayer = (Group3DLayer) item;
                                group3DLayer.addLayer(mapRefLayer);
                            }
                        }
                    }
                }
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
        return new Image(getClass().getResourceAsStream("/Png_AddMapRefLayer_16.png"));
    }

    /**
     * 获取命令按钮的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "添加地图";
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
        return true;
    }

    /**
     * 创建后事件
     *
     * @param ws 工作空间引擎
     */
    @Override
    public void onCreate(IWorkspace ws) {
        this.workspace = ws;
    }
}
