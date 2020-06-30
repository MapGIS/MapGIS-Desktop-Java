package com.zondy.mapgis.edit.tool;

import com.zondy.mapgis.common.CoordinateTran;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.controls.MapCursors;
import com.zondy.mapgis.edit.base.*;
import com.zondy.mapgis.edit.util.Vertex;
import com.zondy.mapgis.geometry.*;
import com.zondy.mapgis.view.DataPropertySet;
import com.zondy.mapgis.view.GeometryElement;
import com.zondy.mapgis.view.Graphic;
import com.zondy.mapgis.view.GraphicList;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;

/**
 * @author CR
 * @file MultipartSketchTool.java
 * @brief 多部分编辑工具（线、区）
 * @date 2020-05-18.
 */
public abstract class MultipartSketchTool extends VertexSketchTool {
    private LineBuilder feedbackLineBuilder;//正在交互移动的橡皮线构造器
    private MultipartBuilder moveMultipartBuilder;//移动Part过程中的橡皮线构造器
    private Dot3D movePartStartPoint;//移动part时选中去移动的点，用于与目标位置点计算偏移
    private Dot3D movePartLastPoint;//移动part时上次的鼠标位置点，用于在移动过程中计算偏移
    private double moveLastScreenX;//移动过程中的鼠标屏幕点X
    private double moveLastScreenY;//移动过程中的鼠标屏幕点Y

    public MultipartSketchTool(MapControl mapControl) {
        super(mapControl);
    }

    /**
     * 清空
     */
    @Override
    protected void clear() {
        super.clear();
        this.moveMultipartBuilder = null;
        this.movingVertexGraphic = null;
        this.movePartStartPoint = null;
        this.movePartLastPoint = null;
    }

    /**
     * 刷新绘制
     */
    @Override
    public void updateSketch() {
        this.clearGraphics();

        MultipartBuilder multipartBuilder = (MultipartBuilder) this.geometryBuilder;
        this.vertices.clear();
        this.partOutlineGraphics.clear();
        this.lastSegmentGraphics.clear();
        if (this.getGeometryType() == GeometryType.GeoPolygon && multipartBuilder.isSketchValid()) {
            Graphic fillGraphic = new Graphic();
            fillGraphic.setAtt(new DataPropertySet());
            //fillGraphic.setSymbol(this.sketchEditor.getSketchStyle().getFillSymbol());
            fillGraphic.setGeometry(multipartBuilder.toGeometry());
            this.sketchOverlay.getGraphics().add(fillGraphic);
        }

        for (int partIndex = 0; partIndex < multipartBuilder.getParts().size(); partIndex++) {
            Part part = multipartBuilder.getParts().get(partIndex);
            for (int pointIndex = 0; pointIndex < part.getPointCount(); pointIndex++) {
                Dot3D point = part.getDot3D(pointIndex);
                Graphic vertexGraphic = this.addVertexGraphic(point, partIndex, pointIndex);
                this.vertices.add(vertexGraphic);
            }

            if (part.getPointCount() > 1) {
                Graphic outlineGraphic = new Graphic();
                outlineGraphic.setAtt(new DataPropertySet());
                //outlineGraphic.setSymbol(this.sketchEditor.getSketchStyle().getLineSymbol());
                MultipartBuilder outlineBuilder = new LineBuilder(multipartBuilder.getSRefData());
                outlineBuilder.addPart(part);
                outlineGraphic.setGeometry(outlineBuilder.toGeometry());
                outlineGraphic.setZIndex(SketchTool.GRAPHIC_LINE_ZORDER);
                outlineGraphic.getAtt().setProperty(Vertex.PART_INDEX, String.valueOf(partIndex));
                this.sketchOverlay.getGraphics().add(outlineGraphic);
                this.partOutlineGraphics.add(outlineGraphic);
            }

            if (this.getGeometryType() == GeometryType.GeoPolygon && part.getPointCount() > 2 && multipartBuilder.isSketchValid()) {
                Dot3D firstPoint = part.getStartDot3D();
                Dot3D lastPoint = part.getEndDot3D();
                LineBuilder lastSegmentBuilder = new LineBuilder(multipartBuilder.getSRefData());
                Graphic lastSegmentGraphic = this.getLastSegmentGraphic();
                lastSegmentGraphic.setAtt(new DataPropertySet());
                lastSegmentGraphic.getAtt().setProperty(Vertex.PART_INDEX, String.valueOf(partIndex));
                lastSegmentBuilder.addPart();
                lastSegmentBuilder.getParts().get(0).addDot3D(firstPoint);
                lastSegmentBuilder.getParts().get(0).addDot3D(lastPoint);
                lastSegmentGraphic.setGeometry(lastSegmentBuilder.toGeometry());
                this.sketchOverlay.getGraphics().add(lastSegmentGraphic);
                this.lastSegmentGraphics.add(lastSegmentGraphic);
            }
        }
        this.mapControl.refreshOverlay();
    }

    /**
     * 添加顶点：若有选中点则添加再其后面，如果没有且几何为空则直接添加，若没有且几何不为空则添加到最后
     *
     * @param x
     * @param y
     */
    @Override
    protected void addVertex(double x, double y) {
        Dot3D mapPoint = this.getMapPoint(x, y);
        MultipartBuilder multipartBuilder = (MultipartBuilder) this.geometryBuilder;
        if (this.selectedVertex != null) {
            this.insertVertexAfterSelectedVertex(mapPoint);
        } else if (this.isEmptyGeometry()) {
            this.sketchEditor.addCommand(new AddVertexCommand(this, 0, 0, mapPoint));
        } else {
            int partIndex = multipartBuilder.getParts().size() - 1;
            int pointIndex = multipartBuilder.getParts().get(partIndex).getPointCount();
            this.sketchEditor.addCommand(new AddVertexCommand(this, partIndex, pointIndex, mapPoint));
        }
    }

    /**
     * 移动前，记录移动位置
     *
     * @param x
     * @param y
     */
    @Override
    protected void handlePrepareMove(double x, double y) {
        Dot3D mapPoint = this.getMapPoint(x, y);
        MultipartBuilder multipartBuilder = (MultipartBuilder) this.geometryBuilder;
        if (this.movingVertexGraphic == null) {
            this.movingVertexGraphic = new Graphic();
        }

        this.movingVertexGraphic.setZIndex(SketchTool.GRAPHIC_MOVEDPOLY_ZORDER);
        //this.movingVertexGraphic.setSymbol(this.sketchEditor.getSketchStyle().getFeedbackLineSymbol());
        this.movingVertexGraphic.setGeometry(new GeoPoint(new Dot3D(0.0, 0.0, 0.0)));//暂时用的。后面需要屏蔽掉
        this.sketchOverlay.getGraphics().add(this.movingVertexGraphic);
        if (this.selectedPartIndex >= 0) {
            this.movePartStartPoint = new Dot3D(mapPoint.getX(), mapPoint.getY(), 0.0);
            this.movePartLastPoint = new Dot3D(mapPoint.getX(), mapPoint.getY(), 0.0);
            if (this.moveMultipartBuilder == null) {
                this.moveMultipartBuilder = this.getNewMultipartBuilder();
            }

            this.moveMultipartBuilder.replaceGeometry(multipartBuilder.toGeometry());
            this.getMovingGeometryGraphic().setGeometry(this.moveMultipartBuilder.toGeometry());
            this.moveLastScreenX = x;
            this.moveLastScreenY = y;
        }

        this.mapControl.refreshOverlay();
    }

    /**
     * 鼠标弹起时完成选中的顶点或Part的移动
     *
     * @param x
     * @param y
     */
    @Override
    protected void handleOnSinglePointerUp(double x, double y) {
        Dot3D toPoint = this.getMapPoint(x, y);
        if (this.selectedVertex != null && this.isAllowVertexInteractionEdit()) {
            this.moveSelectedVertexTo(toPoint);
        } else if (this.selectedPartIndex >= 0 && this.isAllowPartSelection()) {
            this.moveSelectedPartTo(toPoint);
        }
    }

    /**
     * 将选中的Part移动到指定位置
     *
     * @param toPoint
     */
    private void moveSelectedPartTo(Dot3D toPoint) {
        Geometry fromGeometry = this.geometryBuilder.toGeometry();
        double dx = toPoint.getX() - this.movePartStartPoint.getX();
        double dy = toPoint.getY() - this.movePartStartPoint.getY();
        this.movePartAtIndex(this.selectedPartIndex, dx, dy);
        this.sketchOverlay.getGraphics().remove(this.movingVertexGraphic);
        this.movingVertexGraphic = null;
        if (this.moveMultipartBuilder != null) {
            this.moveMultipartBuilder.getParts().clear();
        }

        Geometry toGeometry = this.geometryBuilder.toGeometry();
        this.sketchEditor.addCommand(new MoveGeometryCommand(this, fromGeometry, toGeometry));
        this.mapControl.refreshOverlay();
    }

    /**
     * 将选中顶点移动到指定位置
     *
     * @param toPoint
     */
    private void moveSelectedVertexTo(Dot3D toPoint) {
        this.sketchEditor.addCommand(new MoveVertexCommand(this, this.selectedVertex.getPartIndex(), this.selectedVertex.getPointIndex(), this.selectedVertex.getPoint(), this.selectedVertex.getPointIndex(), toPoint));
        if (this.feedbackLineBuilder != null) {
            this.feedbackLineBuilder.getParts().clear();
        }
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

        System.out.println("handleOnSinglePointerMove");
        boolean rtn = false;
        if (x != this.moveLastScreenX || y != this.moveLastScreenY) {
            this.moveLastScreenX = x;
            this.moveLastScreenY = y;
            if (this.selectedVertex != null && this.isAllowVertexInteractionEdit()) {
                rtn = this.moveByVertex(x, y);
            } else {
                rtn = this.selectedPartIndex >= 0 && this.isAllowPartSelection() ? this.moveByPart(x, y) : true;
            }
        }
        return rtn;
    }

    /**
     * 移动顶点，构造前后两点和中间的鼠标点行程的橡皮线
     *
     * @param x
     * @param y
     * @return
     */
    private boolean moveByVertex(double x, double y) {
        Dot3D tempMapPoint = this.getMapPoint(x, y);
        MultipartBuilder multipartBuilder = (MultipartBuilder) this.geometryBuilder;
        int partIndex = this.selectedVertex.getPartIndex();
        int pointIndex = this.selectedVertex.getPointIndex();
        Part selectedPart = multipartBuilder.getParts().get(partIndex);
        if (selectedPart.getPointCount() > 1) {
            this.feedbackLineBuilder = new LineBuilder(this.mapControl.getSpatialReference());
            Dot3D prePoint = this.getPreviousPointWithinPart(partIndex, pointIndex);
            if (prePoint != null) {
                this.feedbackLineBuilder.addPoint(prePoint);
            } else if (this.getGeometryType() == GeometryType.GeoPolygon && pointIndex == 0) {
                prePoint = this.getPointWithinPart(partIndex, selectedPart.getPointCount() - 1);
                this.feedbackLineBuilder.addPoint(prePoint);
            }

            this.feedbackLineBuilder.addPoint(tempMapPoint);
            Dot3D nextPoint = this.getNextPointWithinPart(partIndex, pointIndex);
            if (nextPoint != null) {
                this.feedbackLineBuilder.addPoint(nextPoint);
            } else if (this.getGeometryType() == GeometryType.GeoPolygon && pointIndex == selectedPart.getPointCount() - 1) {
                nextPoint = this.getPointWithinPart(partIndex, 0);
                this.feedbackLineBuilder.addPoint(nextPoint);
            }
            this.movingVertexGraphic.setGeometry(this.feedbackLineBuilder.toGeometry());
            this.mapControl.refreshOverlay();
        }
        return true;
    }

    /**
     * 移动Part，构造移动中的Part图形
     *
     * @param x
     * @param y
     * @return
     */
    private boolean moveByPart(double x, double y) {
        Dot3D mapPoint = this.getMapPoint(x, y);
        double dx = mapPoint.getX() - this.movePartLastPoint.getX();
        double dy = mapPoint.getY() - this.movePartLastPoint.getY();
        MultipartBuilder tempBuilder = this.getNewMultipartBuilder();
        Part part = this.moveMultipartBuilder.getParts().get(this.selectedPartIndex);
        for (int i = 0; i < part.getPointCount(); i++) {
            Dot3D dot = part.getDot3D(i);
            tempBuilder.addPoint(new Dot3D(dot.getX() + dx, dot.getY() + dy, 0.0));
        }

        this.moveMultipartBuilder.replaceGeometry(tempBuilder.toGeometry());
        this.getMovingGeometryGraphic().setGeometry(this.moveMultipartBuilder.toGeometry());
        this.movePartLastPoint = mapPoint;
        return true;
    }

    /**
     * 获取给定点前面的点
     *
     * @param partIndex
     * @param pointIndex
     * @return
     */
    private Dot3D getPreviousPointWithinPart(int partIndex, int pointIndex) {
        Dot3D dot3D = null;
        MultipartBuilder multipartBuilder = (MultipartBuilder) this.geometryBuilder;
        if (partIndex >= 0 && partIndex < multipartBuilder.getParts().size()) {
            Part part = multipartBuilder.getParts().get(partIndex);
            if (pointIndex >= 1 && pointIndex < part.getPointCount()) {
                dot3D = part.getDot3D(pointIndex - 1);
            }
        }
        return dot3D;
    }

    /**
     * 获取给定点后面的点
     *
     * @param partIndex
     * @param pointIndex
     * @return
     */
    private Dot3D getNextPointWithinPart(int partIndex, int pointIndex) {
        Dot3D dot3D = null;
        MultipartBuilder multipartBuilder = (MultipartBuilder) this.geometryBuilder;
        if (partIndex >= 0 && partIndex < multipartBuilder.getParts().size()) {
            Part part = multipartBuilder.getParts().get(partIndex);
            if (pointIndex >= 0 && pointIndex < part.getPointCount() - 1) {
                dot3D = part.getDot3D(pointIndex + 1);
            }
        }
        return dot3D;
    }

    /**
     * 获取指定索引的点
     *
     * @param partIndex
     * @param pointIndex
     * @return
     */
    private Dot3D getPointWithinPart(int partIndex, int pointIndex) {
        Dot3D dot3D = null;
        MultipartBuilder multipartBuilder = (MultipartBuilder) this.geometryBuilder;
        if (partIndex >= 0 && partIndex < multipartBuilder.getParts().size()) {
            Part part = multipartBuilder.getParts().get(partIndex);
            if (pointIndex >= 0 && pointIndex < part.getPointCount()) {
                dot3D = part.getDot3D(pointIndex);
            }
        }
        return dot3D;
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
