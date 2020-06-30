package com.zondy.mapgis.edit.tool;

import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.controls.wizard.WizardEvent;
import com.zondy.mapgis.edit.base.LineBuilder;
import com.zondy.mapgis.edit.base.RectBuilder;
import com.zondy.mapgis.edit.event.ToolFinishedEvent;
import com.zondy.mapgis.edit.event.ToolFinishedListener;
import com.zondy.mapgis.geometry.Dot3D;
import com.zondy.mapgis.geometry.GeoPoint;
import com.zondy.mapgis.geometry.Geometry;
import com.zondy.mapgis.geometry.GeometryType;
import com.zondy.mapgis.view.DataPropertySet;
import com.zondy.mapgis.view.Graphic;
import com.zondy.mapgis.view.GraphicList;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;

import java.util.*;

/**
 * @author CR
 * @file RectSketchTool.java
 * @brief 矩形工具
 * @create 2020-05-18.
 */
public class RectSketchTool extends VertexSketchTool {
    private Dot3D startPoint;

    public RectSketchTool(MapControl mapControl) {
        super(mapControl);
    }

    @Override
    public GeometryType getGeometryType() {
        return GeometryType.GeoRect;
    }

    /**
     * 清空
     */
    @Override
    protected void clear() {
        super.clear();
        this.rubberBandGraphic = null;
        this.movingVertexGraphic = null;
        this.startPoint = null;
    }

    /**
     * 刷新绘制
     */
    @Override
    public void updateSketch() {
        this.clearGraphics();
        RectBuilder rectBuilder = (RectBuilder) this.geometryBuilder;
        this.vertices.clear();
        this.partOutlineGraphics.clear();
        this.lastSegmentGraphics.clear();
        //if (this.fillGraphic != null && this.getGeometryType() == GeometryType.GeoRect1 && multipartBuilder.isSketchValid()) {
        //        //    this.fillGraphic.setSymbol(this.sketchEditor.getSketchStyle().getFillSymbol());
        //        //    this.fillGraphic.setGeometry(rectBuilder.toGeometry());
        //        //    this.sketchOverlay.getGraphics().add(this.fillGraphic);
        //        //}

        if (rectBuilder.getWidth() > 0.0 || rectBuilder.getHeight() > 0.0) {
            Graphic outlineGraphic = new Graphic();
            outlineGraphic.setAtt(new DataPropertySet());
            //outlineGraphic.setSymbol(this.sketchEditor.getSketchStyle().getLineSymbol());
            outlineGraphic.setGeometry(rectBuilder.toGeometry());
            outlineGraphic.setZIndex(SketchTool.GRAPHIC_LINE_ZORDER);
            this.sketchOverlay.getGraphics().add(outlineGraphic);
            this.partOutlineGraphics.add(outlineGraphic);
        }
        this.mapControl.refreshOverlay();
    }

    /**
     * 左键按下
     *
     * @param x
     * @param y
     * @return
     */
    @Override
    public boolean onSinglePointerDown(double x, double y) {
        this.getRubberBandGraphic().setGeometry(null);
        if (this.startPoint == null) {
            this.startPoint = this.getMapPoint(x, y);
            this.isDisablePan = true;
        }
        return true;
    }

    @Override
    protected void handleOnSinglePointerUp(double x, double y) {
        if (this.startPoint != null) {
            Dot3D endPoint = this.getMapPoint(x, y);
            this.updateRectBuilder((RectBuilder) this.geometryBuilder, endPoint);
            this.fireToolFinished(new ToolFinishedEvent(this, this.geometryBuilder.toGeometry()));
        }
    }

    @Override
    public boolean onRubberBandMove(double x, double y) {
        if (!this.geometryBuilder.isEmpty() && this.startPoint != null) {
            Graphic rubberBandGraphic = this.addRubberBandGraphic();
            RectBuilder builder = new RectBuilder(this.geometryBuilder.getSRefData());
            Dot3D endPoint = this.getMapPoint(x, y);
            //this.updateRectBuilder(builder, endPoint);
            //rubberBandGraphic.setGeometry(builder.toGeometry());
            //底层Bug：GeoRect绘不出来
            LineBuilder lineBuilder = new LineBuilder(this.geometryBuilder.getSRefData());
            lineBuilder.addPoint(this.startPoint.getX(), this.startPoint.getY());
            lineBuilder.addPoint(endPoint.getX(), this.startPoint.getY());
            lineBuilder.addPoint(endPoint.getX(), endPoint.getY());
            lineBuilder.addPoint(this.startPoint.getX(), endPoint.getY());
            lineBuilder.addPoint(this.startPoint.getX(), this.startPoint.getY());
            rubberBandGraphic.setGeometry(lineBuilder.toGeometry());
            this.mapControl.refreshOverlay();
        }
        return true;
    }

    private void updateRectBuilder(RectBuilder rectBuilder, Dot3D endPoint) {
        if (rectBuilder != null && this.startPoint != null && endPoint != null) {
            double xmin = Math.min(this.startPoint.getX(), endPoint.getX());
            double xmax = Math.max(this.startPoint.getX(), endPoint.getX());
            double ymin = Math.min(this.startPoint.getY(), endPoint.getY());
            double ymax = Math.max(this.startPoint.getY(), endPoint.getY());
            rectBuilder.setXY(xmin, ymin, xmax, ymax);
        }
    }

    @Override
    protected void addVertex(double x, double y) {

    }

    /**
     * 鼠标移动
     *
     * @param x
     * @param y
     * @return
     */
    @Override
    public boolean onSinglePointerMove(double x, double y) {
        //if (!this.geometryBuilder.isEmpty() && this.startPoint != null) {
        //    Graphic rubberBandGraphic = this.addRubberBandGraphic();
        //    RectBuilder builder = new RectBuilder(this.geometryBuilder.getSRefData());
        //    Dot3D endPoint = this.getMapPoint(x, y);
        //    this.updateRectBuilder(builder, endPoint);
        //    rubberBandGraphic.setGeometry(builder.toGeometry());
        //
        //
        //    this.mapControl.refreshOverlay();
        //}
        return true;
    }

    /**
     * 移动前，记录移动位置
     *
     * @param x
     * @param y
     */
    @Override
    protected void handlePrepareMove(double x, double y) {
        if (this.movingVertexGraphic == null) {
            this.movingVertexGraphic = new Graphic();
        }

        this.movingVertexGraphic.setZIndex(SketchTool.GRAPHIC_MOVEDPOLY_ZORDER);
        //this.movingVertexGraphic.setSymbol(this.sketchEditor.getSketchStyle().getFeedbackLineSymbol());
        this.sketchOverlay.getGraphics().add(this.movingVertexGraphic);
        this.mapControl.refreshOverlay();
    }

    /**
     * 移动过程中，绘制橡皮段
     *
     * @param x
     * @param y
     * @return
     */
    @Override
    protected boolean handleOnSinglePointerMove(double x, double y) {
        //if (this.startPoint != null) {
        //    Dot3D endPoint = this.getMapPoint(x, y);
        //
        //    RectBuilder rectBuilder = new RectBuilder(this.mapControl.getSpatialReference());
        //    this.updateRectBuilder(rectBuilder, endPoint);
        //    this.movingVertexGraphic.setGeometry(rectBuilder.toGeometry());
        //    return true;
        //}
        return false;
    }

    Graphic addRubberBandGraphic() {
        Graphic rubberBandGraphic = this.getRubberBandGraphic();
        //rubberBandGraphic.setSymbol(this.getFeedbackSymbolByGeometryType(this.getGeometryType()));
        boolean contains = false;
        GraphicList graphicList = this.sketchOverlay.getGraphics();
        for (int i = 0; i < graphicList.size(); i++) {
            Graphic graphic = graphicList.get(i);
            if (graphic == rubberBandGraphic || graphic.getKey().equals(rubberBandGraphic.getKey())) {
                contains = true;
                break;
            }
        }
        if (!contains) {
            rubberBandGraphic.setGeometry(new GeoPoint(new Dot3D(0, 0, 0)));
            this.sketchOverlay.getGraphics().add(rubberBandGraphic);
        }
        return rubberBandGraphic;
    }
}
