package com.zondy.mapgis.workspace;

import com.zondy.mapgis.base.MessageBox;
import com.zondy.mapgis.base.SortLayers;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.map.*;
import com.zondy.mapgis.scene.Group3DLayer;
import com.zondy.mapgis.scene.Map3DLayer;
import com.zondy.mapgis.scene.Scene;
import com.zondy.mapgis.scene.ServerLayer;
import com.zondy.mapgis.workspace.control.AddServerLayerDialog;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * 内部静态方法
 *
 * @author cxy
 * @date 2019/12/18
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
     * 添加二维/三维服务图层
     *
     * @param workSpace    工作空间
     * @param documentItem map/scene
     */
    public static void addServerLayer(IWorkspace workSpace, DocumentItem documentItem) {
        if (documentItem != null) {
            AddServerLayerDialog dialog = new AddServerLayerDialog();
            if (dialog.showAndWait().equals(Optional.of(ButtonType.OK))) {
                MapServer mapServer = dialog.getMapServer();
                if (mapServer != null && mapServer.getIsValid()) {
                    if (documentItem instanceof Scene || documentItem instanceof Group3DLayer) {
                        // region 添加三维服务图层
                        ServerLayer imageLayer = new ServerLayer();
                        imageLayer.setMapServer(mapServer);
                        imageLayer.setName(getNameByURL(mapServer.getURL()));
                        if (imageLayer.connectData()) {
                            if (documentItem instanceof Scene) {
                                Scene scene = (Scene) documentItem;
                                if (!StaticFunction.isLayerExisted(imageLayer, scene)) {
                                    scene.addLayer(imageLayer);
                                } else {
                                    MessageBox.information("地图中已包含下列数据,不必重复添加:\r\n    " + imageLayer.getURL());
                                }
                            } else if (documentItem instanceof Group3DLayer) {
                                Group3DLayer group3DLayer = (Group3DLayer) documentItem;
                                if (!StaticFunction.isLayerExisted(imageLayer, group3DLayer)) {
                                    group3DLayer.addLayer(imageLayer);
                                } else {
                                    MessageBox.information("地图中已包含下列数据,不必重复添加:\r\n    " + imageLayer.getURL());
                                }
                            }
                            SortLayers.sortTargetLayer(imageLayer);
                        }
                        // endregion
                    } else if (documentItem instanceof Map || documentItem instanceof GroupLayer) {
                        // region 添加二维服务图层
                        ImageLayer imageLayer = new ImageLayer();
                        imageLayer.setMapServer(mapServer);
                        imageLayer.setName(getNameByURL(mapServer.getURL()));
                        if (imageLayer.connectData()) {
                            if (documentItem instanceof Map) {
                                // region 添加到地图下
                                Map map = (Map) documentItem;
                                if (!StaticFunction.isLayerExisted(imageLayer, map)) {
                                    map.append(imageLayer);
                                } else {
                                    MessageBox.information("地图中已包含下列数据,不必重复添加:\r\n    " + imageLayer.getURL());
                                }
                                // endregion
                            } else {
                                // region 添加到组图层下
                                GroupLayer groupLayer = (GroupLayer) documentItem;
                                Map map = StaticFunction.getOwnerMap(groupLayer);
                                if (map != null) {
                                    if (!StaticFunction.isLayerExisted(imageLayer, map)) {
                                        groupLayer.append(imageLayer);
                                    } else {
                                        MessageBox.information("地图中已包含下列数据,不必重复添加:\r\n    " + imageLayer.getURL());
                                    }
                                }
                                // endregion
                            }
                            SortLayers.sortTargetLayer(imageLayer);
                            Map map1 = StaticFunction.getOwnerMap(documentItem);
                            MapControl mc = workSpace.getMapControl(map1);
                            if (mc != null) {
                                mc.refreshWnd();
                            }
                        }
                        // endregion
                    }
                } else {
                    MessageBox.information("创建地图服务图层失败,请检查是否启动了瓦片服务或者路径是否有效!");
                }
            }
        }
    }

    /**
     * 根据 URL 获取名称
     *
     * @param url URL
     * @return 名称
     */
    private static String getNameByURL(String url) {
        String name = url;
        if (!name.isEmpty()) {
            int index = name.indexOf(":");
            if (index >= 0) {
                name = name.substring(index + 1);
            }
            index = name.indexOf("?");
            if (index >= 0) {
                name = name.substring(0, index);
            }
            name = name.replace("\\", "/");
            index = name.lastIndexOf('/');
            if (index >= 0) {
                name = name.substring(index + 1);
            }
        }
        if (name.isEmpty()) {
            name = "ServerLayer";
        }
        return name;
    }
}
