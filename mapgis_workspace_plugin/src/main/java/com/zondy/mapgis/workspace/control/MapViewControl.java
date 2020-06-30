package com.zondy.mapgis.workspace.control;

import com.zondy.mapgis.att.Record;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.geodatabase.SFeatureCls;
import com.zondy.mapgis.geometry.GeomType;
import com.zondy.mapgis.info.GeomInfo;
import com.zondy.mapgis.info.LinInfo;
import com.zondy.mapgis.info.PntInfo;
import com.zondy.mapgis.info.RegInfo;
import com.zondy.mapgis.map.MapLayer;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.view.SketchGeometry;
import com.zondy.mapgis.view.SketchGeometryList;
import javafx.geometry.Insets;

/**
 * 地图视图容器
 *
 * @author cxy
 * @date 2019/12/05
 */
public class MapViewControl extends javafx.scene.layout.StackPane {
    private IApplication application;
    private MapControl mapControl;

    public MapViewControl(IApplication application) {
        this.application = application;

        this.mapControl = new MapControl();
        this.mapControl.setMinSize(100, 100);
        this.getChildren().add(mapControl);
        this.setPadding(new Insets(0));
    }

    public MapControl getMapControl() {
        return mapControl;
    }

    public boolean saveEdits() {
        SketchGeometryList list = this.mapControl.getSketchGeometrys();
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
                            GeomInfo geomInfo = this.getDefInfo(cls.getGeomType());
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
            this.mapControl.getSketchGraphicsOverlay().getGraphics().clear();
            this.mapControl.getSketchGeometrys().clear();
            this.mapControl.refreshWnd();
            this.mapControl.refreshOverlay();
        }
        return true;
    }

    private LinInfo linInfo = null;
    private RegInfo regInfo = null;
    private PntInfo pntInfo = null;

    private GeomInfo getDefInfo(GeomType geomType) {
        switch (geomType) {
            case GeomPnt:
                if (this.pntInfo == null) {
                    this.pntInfo = new PntInfo();
                    this.pntInfo.setSymID(1);
                    this.pntInfo.setWidth(8);
                    this.pntInfo.setHeight(8);
                    this.pntInfo.setAngle(0);
                    this.pntInfo.setOutClr1(3);
                    this.pntInfo.setOvprnt(true);
                }
                return this.pntInfo;
            case GeomLin:
                if (this.linInfo == null) {
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
                if (this.regInfo == null) {
                    this.regInfo = new RegInfo();
                    this.regInfo.setPatID(0);
                    this.regInfo.setFillClr(828);
                    this.regInfo.setPatHeight(50);
                    this.regInfo.setPatWidth(50);
                    this.regInfo.setOutPenW(1.0F);
                    this.regInfo.setAngle(0);
                    this.regInfo.setPatClr(3);
                    this.regInfo.setFillMode((short) 0);
                    this.regInfo.setOvprnt(true);
                }
                return regInfo;
            case GeomAnn:
                break;
            default:
                break;
        }
        return null;
    }
}
