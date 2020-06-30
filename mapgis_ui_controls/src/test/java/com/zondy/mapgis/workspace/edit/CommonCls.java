package com.zondy.mapgis.workspace.edit;

import com.zondy.mapgis.att.Record;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.geodatabase.SFeatureCls;
import com.zondy.mapgis.geometry.GeomType;
import com.zondy.mapgis.info.GeomInfo;
import com.zondy.mapgis.info.LinInfo;
import com.zondy.mapgis.info.PntInfo;
import com.zondy.mapgis.info.RegInfo;
import com.zondy.mapgis.map.MapLayer;
import com.zondy.mapgis.view.SketchGeometry;
import com.zondy.mapgis.view.SketchGeometryList;

/**
 * @file CommonCls.java
 * @brief 
 * 
 * @author CR
 * @date 2020-06-19.
 */ 
public class CommonCls {
    private static LinInfo linInfo = null;
    private static RegInfo regInfo = null;
    private static PntInfo pntInfo = null;

    public static GeomInfo getDefInfo(GeomType geomType) {
        switch (geomType) {
            case GeomPnt:
                if (pntInfo == null) {
                    pntInfo = new PntInfo();
                    pntInfo.setSymID(1);
                    pntInfo.setWidth(8);
                    pntInfo.setHeight(8);
                    pntInfo.setAngle(0);
                    pntInfo.setOutClr1(3);
                    pntInfo.setOvprnt(true);
                }
                return pntInfo;
            case GeomLin:
                if (linInfo == null) {
                    linInfo = new LinInfo();
                    linInfo.setLinStyID(1);
                    linInfo.setOutClr1(3);
                    linInfo.setOutClr2(4);
                    linInfo.setOutClr3(5);
                    linInfo.setOutPenW1(1);
                    linInfo.setOutPenW2(1);
                    linInfo.setOutPenW3(1);
                    linInfo.setXScale(10);
                    linInfo.setYScale(10);
                    linInfo.setOvprnt(true);
                }
                return linInfo;
            case GeomReg:
                if (regInfo == null) {
                    regInfo = new RegInfo();
                    regInfo.setPatID(0);
                    regInfo.setFillClr(828);
                    regInfo.setPatHeight(50);
                    regInfo.setPatWidth(50);
                    regInfo.setOutPenW(1.0F);
                    regInfo.setAngle(0);
                    regInfo.setPatClr(3);
                    regInfo.setFillMode((short) 0);
                    regInfo.setOvprnt(true);
                }
                return regInfo;
            case GeomAnn:
                break;
            default:
                break;
        }
        return null;
    }

    public static void saveEdits(MapControl mapControl) {
        SketchGeometryList list = mapControl.getSketchGeometrys();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                SketchGeometry sketchGeometry = list.get(i);
                if (sketchGeometry != null) {
                    if (sketchGeometry.isDeleted()) {
                        //TODO：从图层删除图元
                    } else if (sketchGeometry.getObjID() == -1) {
                        MapLayer mapLayer = sketchGeometry.getLayer();
                        SFeatureCls cls = (SFeatureCls) mapLayer.getData();
                        if (cls != null) {
                            GeomInfo geomInfo = getDefInfo(cls.getGeomType());
                            Record rcd = new Record();
                            rcd.setFields(cls.getFields());
                            cls.append(sketchGeometry.getGeometry(), rcd, geomInfo);
                        }
                    } else {
                        //TODO：更新图元
                        MapLayer mapLayer = sketchGeometry.getLayer();
                        SFeatureCls cls = (SFeatureCls) mapLayer.getData();
                        if (cls != null) {
                            cls.updateGeom(sketchGeometry.getObjID(),sketchGeometry.getGeometry());
                        }
                    }
                }
            }
            mapControl.getSketchGeometrys().clear();
            mapControl.refreshWnd();
            mapControl.refreshOverlay();
        }
    }
}
