package com.zondy.mapgis.workspace.menuitem;

import com.zondy.mapgis.map.*;
import com.zondy.mapgis.scene.Group3DLayer;
import com.zondy.mapgis.scene.Map3DLayer;
import com.zondy.mapgis.scene.Scene;
import com.zondy.mapgis.utilities.UtilityTool;
import com.zondy.mapgis.workspace.engine.ISingleMenuItem;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import javafx.scene.image.Image;

import java.io.UnsupportedEncodingException;

/**
 * 添加组图层
 *
 * @author cxy
 * @date 2019/11/04
 */
public class AddGroupMenuItem implements ISingleMenuItem {
    /**
     * 点击菜单项
     *
     * @param item 文档项
     */
    @Override
    public void onClick(DocumentItem item) {
        if (item instanceof Map) {
            //region 地图
            Map map = (Map) item;
            int start = 1;
            String groupName = "新组图层" + start;
            boolean existed = true;
            while (existed) {
                if (map.getLayerCount() == 0) {
                    existed = false;
                } else {
                    for (int i = 0; i < map.getLayerCount(); i++) {
                        MapLayer layer = map.getLayer(i);
                        if (layer instanceof GroupLayer && layer.getName().equals(groupName)) {
                            existed = true;
                            groupName = "新组图层" + ++start;
                            break;
                        }
                        existed = false;
                    }
                }
            }
            GroupLayer subG = new GroupLayer();
            try {
                subG.setName(UtilityTool.autoBreakString(groupName, UtilityTool.MAX_LENGTH_OF_MAPLAYER_NAME, "gbk"));
            } catch (UnsupportedEncodingException e) {
                subG.setName(groupName);
            }
            subG.setState(LayerState.Visible);
            map.append(subG);
            //endregion
        } else if (item instanceof Scene) {
            //region 场景
            Scene scene = (Scene) item;
            int start = 1;
            String groupName = "新组图层" + start;
            boolean existed = true;
            while (existed) {
                if (scene.getLayerCount() == 0) {
                    existed = false;
                } else {
                    for (int i = 0; i < scene.getLayerCount(); i++) {
                        Map3DLayer layer = scene.getLayer(i);
                        if (layer instanceof Group3DLayer && layer.getName().equals(groupName)) {
                            existed = true;
                            groupName = "新组图层" + ++start;
                            break;
                        }
                        existed = false;
                    }
                }
            }
            Group3DLayer group3DLayer = new Group3DLayer();
            group3DLayer.setName(groupName);
            group3DLayer.setState(LayerState.Visible);
            scene.addLayer(group3DLayer);
            //endregion
        } else if (item instanceof GroupLayer) {
            //region 二维组图层
            GroupLayer groupLayer = (GroupLayer) item;
            int start = 1;
            String groupName = "新组图层" + start;
            boolean existed = true;
            while (existed) {
                if (groupLayer.getCount() == 0) {
                    existed = false;
                } else {
                    for (int i = 0; i < groupLayer.getCount(); i++) {
                        MapLayer layer = groupLayer.item(i);
                        if (layer instanceof GroupLayer && layer.getName().equals(groupName)) {
                            existed = true;
                            groupName = "新组图层" + ++start;
                            break;
                        }
                        existed = false;
                    }
                }
            }
            GroupLayer subG = new GroupLayer();
            try {
                subG.setName(UtilityTool.autoBreakString(groupName, UtilityTool.MAX_LENGTH_OF_MAPLAYER_NAME, "gbk"));
            } catch (UnsupportedEncodingException e) {
                subG.setName(groupName);
            }
            subG.setState(LayerState.Visible);
            groupLayer.append(subG);
            //endregion
        } else if (item instanceof Group3DLayer) {
            //region 三维组图层
            Group3DLayer group3DLayer = (Group3DLayer) item;
            int start = 1;
            String groupName = "新组图层" + start;
            boolean existed = true;
            int count = group3DLayer.getLayerCount();
            while (existed) {
                if (count == 0) {
                    existed = false;
                } else {
                    for (int i = 0; i < count; i++) {
                        Map3DLayer layer = group3DLayer.getLayer(i);
                        if (layer instanceof Group3DLayer && layer.getName().equals(groupName)) {
                            existed = true;
                            groupName = "新组图层" + ++start;
                            break;
                        }
                        existed = false;
                    }
                }
            }
            Group3DLayer subGroup3DLayer = new Group3DLayer();
            try {
                subGroup3DLayer.setName(UtilityTool.autoBreakString(groupName, UtilityTool.MAX_LENGTH_OF_MAPLAYER_NAME, "gbk"));
            } catch (UnsupportedEncodingException e) {
                subGroup3DLayer.setName(groupName);
            }
            subGroup3DLayer.setState(LayerState.Visible);
            group3DLayer.addLayer(subGroup3DLayer);
            //endregion
        }
    }

    /**
     * 获取菜单项图标
     *
     * @return 菜单项图标
     */
    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_AddGroupLayer_16.png"));
    }

    /**
     * 获取命令按钮的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "添加组图层";
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

    }
}
