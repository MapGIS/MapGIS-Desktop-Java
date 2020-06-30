package com.zondy.mapgis.workspace.event;

import com.zondy.mapgis.map.Map;
import com.zondy.mapgis.map.MapLayer;

import java.util.EventObject;

/**
 * 移除图层事件
 *
 * @author cxy
 * @date 2019/11/19
 */
public class RemoveLayerEvent extends EventObject {
    private transient Map map;
    private transient MapLayer mapLayer;

    /**
     * 移除图层事件
     *
     * @param source 事件源
     * @param map 地图
     * @param mapLayer 图层
     * @throws IllegalArgumentException if source is null.
     */
    public RemoveLayerEvent(Object source, Map map, MapLayer mapLayer) {
        super(source);
        this.map = map;
        this.mapLayer = mapLayer;
    }

    /**
     * 获取地图
     *
     * @return 地图
     */
    public Map getMap() {
        return map;
    }

    /**
     * 获取图层
     *
     * @return 图层
     */
    public MapLayer getMapLayer() {
        return mapLayer;
    }
}
