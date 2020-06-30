package com.zondy.mapgis.workspace.event;

import com.zondy.mapgis.scene.Map3DLayer;
import com.zondy.mapgis.scene.Scene;

import java.util.EventObject;

/**
 * 移除三维图层事件
 *
 * @author cxy
 * @date 2019/11/20
 */
public class Remove3DLayerEvent extends EventObject {
    private transient Scene scene;
    private transient Map3DLayer map3DLayer;

    /**
     * 移除三维图层事件
     *
     * @param source     事件源
     * @param scene      场景
     * @param map3DLayer 三维图层
     * @throws IllegalArgumentException if source is null.
     */
    public Remove3DLayerEvent(Object source, Scene scene, Map3DLayer map3DLayer) {
        super(source);
        this.scene = scene;
        this.map3DLayer = map3DLayer;
    }

    /**
     * 获取场景
     *
     * @return 场景
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * 获取三维图层
     *
     * @return 三维图层
     */
    public Map3DLayer getMap3DLayer() {
        return map3DLayer;
    }
}
