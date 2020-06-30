package com.zondy.mapgis.workspace.enums;

import com.zondy.mapgis.geodatabase.raster.MosaicDataset;
import com.zondy.mapgis.map.*;
import com.zondy.mapgis.scene.*;

/**
 * 节点类型
 *
 * @author cxy
 * @date 2019/10/16
 */
public enum ItemType {
    /**
     * 空白
     */
    BLANKAREA,
    /**
     * 文档
     */
    DOCUMENT,
    /**
     * 三维组图层
     */
    GROUP3DLAYER,
    /**
     * 二维组图层
     */
    GROUPLAYER,
    /**
     * 三维注记图层
     */
    LABELLAYER,
    /**
     * 地图
     */
    MAP,
    /**
     * 模型缓存图层
     */
    MODELCACHELAYER,
    /**
     * 模型图层
     */
    MODELLAYER,
    /**
     * 镶嵌数据集图层
     */
    MOSAICLAYER,
    /**
     * 栅格图层
     */
    RASTERLAYER,
    /**
     * 栅格数据集图层
     */
    RASTERCATALOGLAYER,
    /**
     * 场景
     */
    SCENE,
    /**
     * 三维地形图层
     */
    TERRAINLAYER,
    /**
     * 三维矢量图层
     */
    VECTOR3DLAYER,
    /**
     * 二维矢量图层
     */
    VECTORLAYER,
    /**
     * 6x文件图层
     */
    FILELAYER6X,
    /**
     * 对象图层
     */
    OBJECTLAYER,
    /**
     * 多地图
     */
    MULTIMAP,
    /**
     * 多二维图层
     */
    MULTILAYER,
    /**
     * 多场景
     */
    MULTISCENE,
    /**
     * 多三维图层
     */
    MULTI3DLAYER;

    public static ItemType toValue(Class<?> cls) {
        if (cls == null) {
            return ItemType.BLANKAREA;
        } else if (cls == Document.class) {
            return ItemType.DOCUMENT;
        } else if (cls == Group3DLayer.class) {
            return ItemType.GROUP3DLAYER;
        } else if (cls == GroupLayer.class) {
            return ItemType.GROUPLAYER;
        } else if (cls == LabelLayer.class) {
            return ItemType.LABELLAYER;
        } else if (cls == Map.class) {
            return ItemType.MAP;
        } else if (cls == ModelCacheLayer.class) {
            return ItemType.MODELCACHELAYER;
        } else if (cls == ModelLayer.class) {
            return ItemType.MODELLAYER;
        } else if (cls == MosaicDataset.class) {
            // TODO:修改 MosaicDataSet 为 MosaicLayer
            return ItemType.MOSAICLAYER;
        } else if (cls == RasterLayer.class) {
            return ItemType.RASTERLAYER;
        } else if (cls == RasterCatalogLayer.class) {
            return ItemType.RASTERCATALOGLAYER;
        } else if (cls == Scene.class) {
            return ItemType.SCENE;
        } else if (cls == TerrainLayer.class) {
            return ItemType.TERRAINLAYER;
        } else if (cls == Vector3DLayer.class) {
            return ItemType.VECTOR3DLAYER;
        } else if (cls == VectorLayer.class) {
            return ItemType.VECTORLAYER;
        } else if (cls == FileLayer6x.class) {
            return ItemType.FILELAYER6X;
        } else if (cls == ObjectLayer.class) {
            return ItemType.OBJECTLAYER;
        }
        return null;
    }
}
