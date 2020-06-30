package com.zondy.mapgis.edit.tool;

import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.geodatabase.SpaQueryMode;
import com.zondy.mapgis.geometry.GeoRect;
import com.zondy.mapgis.geometry.Rect;
import com.zondy.mapgis.map.Map;
import com.zondy.mapgis.map.SelectOption;
import com.zondy.mapgis.map.Transformation;

/**
 * SelectTool
 *
 * @author cxy
 * @date 2020/06/19
 */
public class SelectTool {
    private SketchTool selectSketchTool;
    private MapControl mapControl;
    private Map map;
    private SelectType selectType = SelectType.RECTANGLE;
    private SelectOption selectOption;
    private SpaQueryMode spaQueryMode;
    private Transformation transformation;

    public SelectTool(MapControl mapControl) {
        this.selectSketchTool = null;
        this.mapControl = mapControl;

        init();
    }

    public SelectTool(MapControl mapControl, SelectType selectType, SelectOption selectOption, SpaQueryMode spaQueryMode, Transformation transformation) {
        this.selectSketchTool = null;
        this.mapControl = mapControl;
        this.selectType = selectType;
        this.selectOption = selectOption;
        this.spaQueryMode = spaQueryMode;
        this.transformation = transformation;

        init();
    }

    private boolean init() {
        if (this.mapControl == null) {
            return false;
        }

        this.map = this.mapControl.getMap();
        if (this.transformation == null) {
            this.transformation = this.mapControl.getTransformation();
        }

        if (this.selectType == SelectType.RECTANGLE) {
            this.selectSketchTool = new RectSketchTool(this.mapControl);
            //this.selectSketchTool.addToolFinishedListener(toolFinishedEvent -> {
            //    GeoRect geoRect = (GeoRect) toolFinishedEvent.getGeometry();
            //});
        } else if (this.selectType == SelectType.CIRCLE) {
            this.selectSketchTool = null;//new IACircle(m_control, nullptr, true, true, m_trans, nullptr, false);
        }
        if (this.selectSketchTool != null) {

            //m_selTool -> Finish += gcnew ToolEventHandler(this, & SelectTool::tool_OnFinish);
        }
        return true;
    }

    public void onMouseDown(double x, double y) {
        if (this.selectSketchTool != null) {
            this.selectSketchTool.onSinglePointerDown(x, y);
        }
    }

    public void onMouseMove(double x, double y) {
        if (this.selectSketchTool != null) {
            this.selectSketchTool.onRubberBandMove(x, y);
        }
    }

    public void onMouseUp(double x, double y) {
        if (this.selectSketchTool != null) {
            this.selectSketchTool.onSinglePointerUp(x, y);
            GeoRect geoRect = (GeoRect) this.selectSketchTool.getGeometry().clone();
            this.selectSketchTool.clear();
            if (geoRect != null) {
                if (geoRect.getRtDot().getX() - geoRect.getLbDot().getX() < 0.0001 && geoRect.getRtDot().getY() - geoRect.getLbDot().getY() < 0.0001) {
                    this.mapControl.Select(geoRect.getLbDot(), SketchTool.IDENTIFY_TOLERANCE);
                } else {
                    //Rect rect = geoRect.calRect();//Bug，cal不出来
                    this.mapControl.Select(new Rect(geoRect.getLbDot().getX(), geoRect.getLbDot().getY(), geoRect.getRtDot().getX(), geoRect.getRtDot().getY()));
                }
            }
        }
    }
}
