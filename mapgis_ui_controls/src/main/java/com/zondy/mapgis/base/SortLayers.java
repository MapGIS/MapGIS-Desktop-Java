package com.zondy.mapgis.base;

import com.zondy.mapgis.geodatabase.XClsType;
import com.zondy.mapgis.geometry.GeomType;
import com.zondy.mapgis.map.*;
import com.zondy.mapgis.scene.*;

import java.util.ArrayList;

/**
 * 图层约束排序
 *
 * @author cxy
 * @date 2019/12/17
 */
public class SortLayers {
    /**
     * 指定类型图层的单个排序(如添加图层时)
     *
     * @param documentItem 图层
     */
    public static void sortTargetLayer(DocumentItem documentItem) {
        if (documentItem != null && documentItem.getParent() != null) {
            ArrayList<DocumentItem> documentItems = getItems2(documentItem.getParent());
            int a = -1;
            for (int i = 0; i < documentItems.size(); i++) {
                if (documentItems.get(i).getHandle() == documentItem.getHandle()) {
                    a = i;
                    break;
                }
            }
            if (a >= 0) {
                documentItems.remove(a);
            }
            sortLayer2(documentItem, documentItem.getParent(), documentItems);
        }
    }

    private static ArrayList<DocumentItem> getItems2(DocumentItem documentItem) {
        ArrayList<DocumentItem> documentItems = new ArrayList<>();
        if (documentItem instanceof Map) {
            Map map = (Map) documentItem;
            for (int i = 0; i < map.getLayerCount(); i++) {
                documentItems.add(map.getLayer(i));
            }
        } else if (documentItem instanceof GroupLayer) {
            GroupLayer groupLayer = (GroupLayer) documentItem;
            for (int i = 0; i < groupLayer.getCount(); i++) {
                documentItems.add(groupLayer.item(i));
            }
        } else if (documentItem instanceof Scene) {
            Scene sence = (Scene) documentItem;
            for (int i = 0; i < sence.getLayerCount(); i++) {
                documentItems.add(sence.getLayer(i));
            }
        } else if (documentItem instanceof Group3DLayer) {
            Group3DLayer group3DLayer = (Group3DLayer) documentItem;
            for (int i = 0; i < group3DLayer.getLayerCount(); i++) {
                documentItems.add(group3DLayer.getLayer(i));
            }
        } else if (documentItem instanceof TerrainLayer) {
            TerrainLayer terrainLayer = (TerrainLayer) documentItem;
            // TODO: 待添加接口
//            for (int i = 0; i < terrainLayer.Count; i++) {
//                documentItems.add(terrainLayer.GetLayer(i));
//            }
        }
        return documentItems;
    }

    private static void sortLayer2(DocumentItem documentItem, DocumentItem parentDocumentItem, ArrayList<DocumentItem> documentItems) {
        ArrayList<Integer> sortList = new ArrayList<>();
        for (DocumentItem i : documentItems) {
            sortList.add(getSortType2(i).toValue());
        }
        SortType sortType = getSortType2(documentItem);
        int curInt = sortType.toValue();
        int index = getSortLocation2(sortType, sortList);
        if (index == -2) {
            sortList.add(curInt);
        } else {
            if (parentDocumentItem instanceof Map) {
                Map map = (Map) parentDocumentItem;
                if (map.getLayerCount() - 1 == index + 1) {
                    if (index + 1 < sortList.size()) {
                        sortList.add(index + 1, curInt);
                    } else {
                        sortList.add(curInt);
                    }
                } else {
                    if (map.move(map.getLayerCount() - 1, index + 1)) {
                        if (index + 1 < sortList.size()) {
                            sortList.add(index + 1, curInt);
                        } else {
                            sortList.add(curInt);
                        }
                    } else {
                        sortList.add(curInt);
                    }
                }
            }
            if (parentDocumentItem instanceof GroupLayer) {
                GroupLayer groupLayer = (GroupLayer) parentDocumentItem;
                if (groupLayer.getCount() - 1 == index + 1) {
                    if (index + 1 < sortList.size()) {
                        sortList.add(index + 1, curInt);
                    } else {
                        sortList.add(curInt);
                    }
                } else {
                    if (groupLayer.move(groupLayer.getCount() - 1, index + 1)) {
                        if (index + 1 < sortList.size()) {
                            sortList.add(index + 1, curInt);
                        } else {
                            sortList.add(curInt);
                        }
                    } else {
                        sortList.add(curInt);
                    }
                }
            }
            if (parentDocumentItem instanceof Scene) {
                Scene scene = (Scene) parentDocumentItem;
                if (scene.getLayerCount() - 1 == index + 1) {
                    if (index + 1 < sortList.size()) {
                        sortList.add(index + 1, curInt);
                    } else {
                        sortList.add(curInt);
                    }
                } else {
                    if (scene.moveLayerTo(scene.getLayerCount() - 1, index + 1)) {
                        if (index + 1 < sortList.size()) {
                            sortList.add(index + 1, curInt);
                        } else {
                            sortList.add(curInt);
                        }
                    } else {
                        sortList.add(curInt);
                    }
                }
            }
            if (parentDocumentItem instanceof Group3DLayer) {
                Group3DLayer group3DLayer = (Group3DLayer) parentDocumentItem;
                if (group3DLayer.getLayerCount() - 1 == index + 1) {
                    if (index + 1 < sortList.size()) {
                        sortList.add(index + 1, curInt);
                    } else {
                        sortList.add(curInt);
                    }
                } else {
                    if (group3DLayer.moveLayerTo(group3DLayer.getLayerCount() - 1, index + 1)) {
                        if (index + 1 < sortList.size()) {
                            sortList.add(index + 1, curInt);
                        } else {
                            sortList.add(curInt);
                        }
                    } else {
                        sortList.add(curInt);
                    }
                }
            }
        }
    }

    private static SortType getSortType2(DocumentItem documentItem) {
        SortType sortType = SortType.UNKNOWN;
        if (documentItem instanceof MapLayer) {
            // region 获取矢量图层权值
            MapLayer layer = (MapLayer) documentItem;
            if ((layer instanceof GroupLayer && !(layer instanceof NetClsLayer) && !(layer instanceof FileLayer6x)
                    /*&& !(layer instanceof MosaicDatasetLayer)*/)) {
                sortType = SortType.GROUP;
            }
            if (layer instanceof FileLayer6x) {
                switch (((FileLayer6x) layer).getCount()) {
                    case 1:
                        sortType = SortType.WL;
                        break;
                    case 2:
                        sortType = SortType.WT;
                        break;
                    case 3:
                        sortType = SortType.WP;
                        break;
                }
            } else if (layer instanceof ImageLayer) {
                sortType = SortType.IMGLAYER;
            } else {
                XClsType clsType = layer.getClsType();
                if (XClsType.XSFCls.equals(clsType)) {
                    GeomType geometryType = layer.getGeometryType();
                    if (GeomType.GeomLin.equals(geometryType)) {
                        sortType = SortType.SFCLS_LIN;
                    } else if (GeomType.GeomPnt.equals(geometryType)) {
                        sortType = SortType.SFCLS_PNT;
                    } else if (GeomType.GeomReg.equals(geometryType)) {
                        sortType = SortType.SFCLS_REG;
                    }
                } else if (XClsType.XACls.equals(clsType)) {
                    sortType = SortType.ACLS;
                } else if (XClsType.XRcat.equals(clsType)) {
                    sortType = SortType.RASTERCAT;
                } else if (XClsType.XRds.equals(clsType)) {
                    sortType = SortType.RASTER;
                } else if (XClsType.XGNet.equals(clsType)) {
                    sortType = SortType.NETCLS;
                } else if (XClsType.XMosaicDS.equals(clsType)) {
                    sortType = SortType.MOSAICDATASET;
                }
            }
            // endregion
        } else if (documentItem instanceof Map3DLayer) {
            if (documentItem instanceof Group3DLayer) {
                if (documentItem instanceof TerrainLayer) {
                    sortType = SortType.TERRAIN;
                } else {
                    sortType = SortType.GROUP;
                }
            } else if (documentItem instanceof MapRefLayer) {
                sortType = SortType.MAPREF;
            } else if (documentItem instanceof Vector3DLayer) {
                Vector3DLayer vector3DLayer = (Vector3DLayer) documentItem;
                GeomType geometryType = vector3DLayer.getGeometryType();
                if (GeomType.GeomReg.equals(geometryType)) {
                    sortType = SortType.SFCLS3D_REG;
                } else if (GeomType.GeomLin.equals(geometryType)) {
                    sortType = SortType.SFCLS3D_LIN;
                } else if (GeomType.GeomPnt.equals(geometryType)) {
                    sortType = SortType.SFCLS3D_PNT;
                } else if (GeomType.GeomAnn.equals(geometryType)) {
                    sortType = SortType.ACLS3D;
                }
            } else if (documentItem instanceof LabelLayer) {
                sortType = SortType.ACLS3D;
            } else if (documentItem instanceof ModelLayer) {
                sortType = SortType.MODEL;
            }
            // endregion
        }
        return sortType;
    }

    private static int getSortLocation2(SortType sortType, ArrayList<Integer> sortList) {
        int index = sortType.toValue();
        if (sortList.size() == 0) {
            return -2;
        }
        int cur = sortList.lastIndexOf(index);
        if (cur == -1) {
            for (int i = 0; i < sortList.size() - 1; i++) {
                if (index > sortList.get(i) && index < sortList.get(i + 1)) {
                    return i;
                }
            }
            if (index < sortList.get(0)) {
                return -1;
            }
            if (index > sortList.get(sortList.size() - 1)) {
                return sortList.size() - 1;
            }
        } else {
            return cur;
        }
        return index;
    }
}
