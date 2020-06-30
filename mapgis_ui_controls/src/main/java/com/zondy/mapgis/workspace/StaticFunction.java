package com.zondy.mapgis.workspace;

import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.geometry.Dot;
import com.zondy.mapgis.geometry.Rect;
import com.zondy.mapgis.map.*;
import com.zondy.mapgis.scene.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 内部静态方法
 *
 * @author cxy
 * @date 2019/11/04
 */
public class StaticFunction {
    /**
     * 获取已知 DocumentItem 所属的 Map
     *
     * @param documentItem 文档项
     * @return 地图
     */
    public static Map getOwnerMap(DocumentItem documentItem) {
        Map map = null;
        if (documentItem != null) {
            if (documentItem instanceof Map) {
                map = (Map) documentItem;
            } else if (documentItem.getParent() != null) {
                map = getOwnerMap(documentItem.getParent());
            }
        }
        return map;
    }

    /**
     * 获取已知 DocumentItem 所属的 Scene
     *
     * @param documentItem 文档项
     * @return 场景
     */
    public static Scene getOwnerScene(DocumentItem documentItem) {
        Scene scene = null;
        if (documentItem != null) {
            DocumentItem parent = documentItem.getParent();
            if (documentItem instanceof Scene) {
                scene = (Scene) documentItem;
            } else if (parent != null) {
                scene = getOwnerScene(parent);
            }
        }
        return scene;
    }

    /**
     * 获取一个布尔值，此值指示已知图层是否已经存在于已知集合中
     *
     * @param mapLayer     已知图层
     * @param documentItem 已知集合（地图或组图层）
     * @return 若存在，则返回true，否则返回false
     */
    public static boolean isLayerExisted(MapLayer mapLayer, DocumentItem documentItem) {
        boolean existed = false;
        if (mapLayer != null && documentItem != null) {
            if (!mapLayer.getURL().isEmpty()) {
                if (documentItem instanceof Map) {
                    // region 判断图层是否在地图中
                    Map map = (Map) documentItem;
                    for (int i = 0; i < map.getLayerCount(); i++) {
                        MapLayer layer = map.getLayer(i);
                        if (mapLayer.getURL().equals(layer.getURL())) {
                            existed = true;
                            break;
                        }
                        if (layer instanceof GroupLayer) {
                            existed = isLayerExisted(mapLayer, layer);
                            if (existed) {
                                break;
                            }
                        }
                    }
                    // endregion
                } else if (documentItem instanceof GroupLayer) {
                    // region 判断图层是否在组中
                    GroupLayer groupLayer = (GroupLayer) documentItem;
                    for (int i = 0; i < groupLayer.getCount(); i++) {
                        MapLayer layer = groupLayer.item(i);
                        if (mapLayer.getURL().equals(layer.getURL())) {
                            existed = true;
                            break;
                        }
                        if (layer instanceof GroupLayer) {
                            existed = isLayerExisted(mapLayer, layer);
                            if (existed) {
                                break;
                            }
                        }
                    }
                    // endregion
                }
                //组中是否有图层包含在集合中
                if (!existed && mapLayer instanceof GroupLayer) {
                    GroupLayer groupLayer = (GroupLayer) mapLayer;
                    for (int i = 0; i < groupLayer.getCount(); i++) {
                        existed = isLayerExisted(groupLayer.item(i), StaticFunction.getOwnerMap(documentItem));
                        if (existed) {
                            break;
                        }
                    }
                }
            }
        }
        return existed;
    }

    /**
     * 获取一个布尔值，此值指示已知图层是否已经存在于已知集合中
     *
     * @param map3DLayer   已知图层
     * @param documentItem 已知集合（场景或组图层）
     * @return 若存在，则返回true，否则返回false
     */
    public static boolean isLayerExisted(Map3DLayer map3DLayer, DocumentItem documentItem) {
        boolean existed = false;
        if (map3DLayer != null && documentItem != null) {
            if (map3DLayer.getURL() != "") {
                if (documentItem instanceof Scene) {
                    // region 判断图层是否在场景中
                    Scene scene = (Scene) documentItem;
                    for (int i = 0; i < scene.getLayerCount(); i++) {
                        Map3DLayer layer = scene.getLayer(i);
                        if (map3DLayer.getURL().equals(layer.getURL())) {
                            existed = true;
                            break;
                        }
                        if (layer instanceof Group3DLayer) {
                            existed = isLayerExisted(map3DLayer, layer);
                            if (existed) {
                                break;
                            }
                        }
                    }
                    // endregion
                } else if (documentItem instanceof Group3DLayer) {
                    // region 判断图层是否在组中
                    Group3DLayer group3DLayer = (Group3DLayer) documentItem;
                    for (int i = 0; i < group3DLayer.getLayerCount(); i++) {
                        Map3DLayer layer = group3DLayer.getLayer(i);
                        if (map3DLayer.getURL().equals(layer.getURL())) {
                            existed = true;
                            break;
                        }
                        if (layer instanceof Group3DLayer) {
                            existed = isLayerExisted(map3DLayer, layer);
                            if (existed) {
                                break;
                            }
                        }
                    }
                    // endregion
                }
                //组中是否有图层包含在集合中
                if (!existed && map3DLayer instanceof Group3DLayer) {
                    Group3DLayer group3DLayer = (Group3DLayer) map3DLayer;
                    for (int i = 0; i < group3DLayer.getLayerCount(); i++) {
                        existed = isLayerExisted(group3DLayer.getLayer(i), StaticFunction.getOwnerScene(documentItem));
                        if (existed) {
                            break;
                        }
                    }
                }
            }
        }
        return existed;
    }

    /**
     * 移除组图层，如果组图层中还存在组图层，根据第二个参数判断是否递归移除
     *
     * @param item           组图层
     * @param removeAllGroup 是否递归
     */
    public static void removeGroupLayer(DocumentItem item, boolean removeAllGroup) {
        if (item instanceof GroupLayer) {
            //region 取消组 (二维)
            GroupLayer gl = (GroupLayer) item;
            List<MapLayer> items = new ArrayList<>();
            for (int i = 0; i < gl.getCount(); i++) {
                items.add(gl.item(i));
            }
            DocumentItem parent = gl.getParent();
            if (parent instanceof GroupLayer) {
                GroupLayer pGL = (GroupLayer) parent;
                int index = pGL.indexOf(gl);
                for (MapLayer it : items) {
                    gl.dragOut(it);
                }
                pGL.remove(gl);
                if (index >= pGL.getCount()) {
                    for (MapLayer it : items) {
                        pGL.append(it);
                    }
                } else {
                    Collections.reverse(items);
                    for (MapLayer it : items) {
                        pGL.dragIn(index, it);
                    }
                }
            } else if (parent instanceof Map) {
                Map pMap = (Map) parent;
                int index = pMap.indexOf(gl);
                for (MapLayer it : items) {
                    gl.dragOut(it);
                }
                pMap.remove(gl);
                if (index >= pMap.getLayerCount()) {
                    for (MapLayer it : items) {
                        pMap.append(it);
                    }
                } else {
                    Collections.reverse(items);
                    for (MapLayer it : items) {
                        pMap.dragIn(index, it);
                    }
                }
            }
            if (removeAllGroup) {
                for (MapLayer it : items) {
                    removeGroupLayer(it, true);
                }
            }
            //endregion
        } else if (item instanceof Group3DLayer) {
            //region 取消组 (三维)
            Group3DLayer group3D = (Group3DLayer) item;
            List<Map3DLayer> items = new ArrayList<>();
            int count = group3D.getLayerCount();
            for (int i = 0; i < count; i++) {
                items.add(group3D.getLayer(i));
            }
            DocumentItem parent = group3D.getParent();
            if (parent instanceof Group3DLayer) {
                Group3DLayer pGL = (Group3DLayer) parent;
                int index = pGL.indexOfLayer(group3D);
                for (Map3DLayer it : items) {
                    group3D.dragOutLayer(it);
                }
                pGL.removeLayer(group3D);
                if (index >= pGL.getLayerCount()) {
                    for (Map3DLayer it : items) {
                        pGL.addLayer(it);
                    }
                } else {
                    Collections.reverse(items);
                    for (Map3DLayer it : items) {
                        pGL.dragInLayer(it, index);
                    }
                }
            } else if (parent instanceof Scene) {
                Scene scene = (Scene) parent;
                int index = scene.indexOfLayer(group3D);
                for (Map3DLayer it : items) {
                    group3D.dragOutLayer(it);
                }
                scene.removeLayer(group3D);
                if (index >= scene.getLayerCount()) {
                    for (Map3DLayer it : items) {
                        scene.addLayer(it);
                    }
                } else {
                    Collections.reverse(items);
                    for (Map3DLayer it : items) {
                        scene.dragInLayer(it, index);
                    }
                }
            }
            if (removeAllGroup) {
                for (Map3DLayer it : items) {
                    removeGroupLayer(it, true);
                }
            }
            //endregion
        }
    }

    /**
     * 设置显示范围
     *
     * @param mc 地图视图
     * @param rt 矩形对象
     */
    public static void setDisplayScale(MapControl mc, Rect rt) {
        if (mc != null) {
            // TODO: 添加 mapControl.DispScale, mapControl.ViewHeight接口
            double scale = 0;//mc.DispScale;
            if (rt != null) {
                int height = 0;//mc.ViewHeight;
                int width = 0;//mc.ViewWidth;
                double xScale = 0;
                double yScale = 0;
                if ((rt.getXMax() - rt.getXMin()) > 0) {
                    xScale = width / (rt.getXMax() - rt.getXMin());
                }
                if ((rt.getYMax() - rt.getYMin()) > 0) {
                    yScale = height / (rt.getYMax() - rt.getYMin());
                }
                Rect rect = new Rect();
                rect.setXMax(rt.getXMax());
                rect.setXMin(rt.getXMin());
                rect.setYMax(rt.getYMax());
                rect.setYMin(rt.getYMin());
                scale = Math.min(xScale, yScale);
                if (scale > 0) {
                    double dx = width / scale;
                    double dy = height / scale;
                    rect.setXMin((rt.getXMax() + rt.getXMin()) / 2 - dx / 2);
                    rect.setXMax((rt.getXMax() + rt.getXMin()) / 2 + dx / 2);
                    rect.setYMin((rt.getYMax() + rt.getYMin()) / 2 - dy / 2);
                    rect.setYMax((rt.getYMax() + rt.getYMin()) / 2 + dy / 2);
                }
//                mc.DispRect = rect;
            }
        }
    }

    /**
     * 按 Restore 逻辑规范矩形防止高宽为0的情况
     *
     * @param rt 矩形对象
     * @return 矩形对象
     */
    public static Rect restoreRect(Rect rt) {
        Rect rt1 = null;
        if (rt != null) {
            // TODO: 修改 Rect.clone() 接口返回值
            rt1 = (Rect) rt.clone();
            double eps = 1E-7;
            if (rt1.getXMin() > rt1.getXMax() || rt1.getYMin() > rt1.getYMax()) {
                rt1.setXMin(-200);
                rt1.setYMin(-200);
                rt1.setXMax(200);
                rt1.setYMax(200);
            } else {
                double dxExt = Math.abs(rt1.getXMax() - rt1.getXMin());
                double dyExt = Math.abs(rt1.getYMax() - rt1.getYMin());
                if (dxExt < eps) {
                    dxExt = (dyExt > eps) ? dyExt : 400;
                    rt1.setXMin(rt1.getXMin() - dxExt / 2);
                    rt1.setXMax(rt1.getXMax() + dxExt / 2);
                }
                if (dyExt < eps) {
                    dyExt = (dxExt > eps) ? dxExt : 400;
                    rt1.setYMin(rt1.getYMin() - dyExt / 2);
                    rt1.setYMax(rt1.getYMax() + dyExt / 2);
                }
            }
        } else {
            rt1 = new Rect();
            rt1.setXMin(-200);
            rt1.setYMin(-200);
            rt1.setXMax(200);
            rt1.setYMax(200);
        }
        return rt1;
    }

    /**
     * 计算旋转矩形
     *
     * @param mc 地图视图
     * @param rt 矩形对象
     * @return 矩形对象
     */
    public static Rect calcRotateRect(MapControl mc, Rect rt) {
        Rect rect = rt;
        if (mc != null && rt != null) {
            // TODO: 修改 Rect.clone() 接口返回值
            rect = (Rect) rt.clone();
            // TODO: 用 mapControl.GetMap() 代替 mapControl.ActiveMap 是否正确？
            Map map = mc.getMap();
            if (map != null) {
                double cx = 0;
                double cy = 0;
                // TODO: 待添加 map.GetRotateCenter(), map.GetRotateAngle() 接口
//                map.GetRotateCenter(ref cx, ref cy);
                double angle = 0;//map.GetRotateAngle();
                angle = angle / 180 * Math.PI;

                Dot[] dots = new Dot[4];
                Dot[] dots1 = new Dot[4];
                dots[0] = new Dot(rect.getXMin(), rect.getYMin());
                dots[1] = new Dot(rect.getXMin(), rect.getYMax());
                dots[2] = new Dot(rect.getXMax(), rect.getYMax());
                dots[3] = new Dot(rect.getXMax(), rect.getYMin());
                for (int i = 0; i < 4; i++) {
                    dots1[i] = new Dot();
                    dots1[i].setX(((dots[i].getX() - cx) * Math.cos(angle)) - ((dots[i].getY() - cy) * Math.sin(angle)) + cx);
                    dots1[i].setY(((dots[i].getY() - cy) * Math.cos(angle)) + ((dots[i].getX() - cx) * Math.sin(angle)) + cy);
                }
                //找出最大范围
                rect.setXMin(dots1[0].getX());
                rect.setXMax(dots1[0].getX());
                rect.setYMin(dots1[0].getY());
                rect.setYMax(dots1[0].getY());
                for (int i = 0; i < 4; i++) {
                    if (dots1[i].getX() < rect.getXMin()) {
                        rect.setXMin(dots1[i].getX());
                    }
                    if (dots1[i].getX() > rect.getXMax()) {
                        rect.setXMax(dots1[i].getX());
                    }
                    if (dots1[i].getY() < rect.getYMin()) {
                        rect.setYMin(dots1[i].getY());
                    }
                    if (dots1[i].getY() > rect.getYMax()) {
                        rect.setYMax(dots1[i].getY());
                    }
                }
            }
        }
        return rect;
    }
}
