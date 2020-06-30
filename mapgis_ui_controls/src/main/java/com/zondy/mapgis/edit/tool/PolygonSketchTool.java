package com.zondy.mapgis.edit.tool;

import com.zondy.mapgis.common.CoordinateTran;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.edit.base.*;
import com.zondy.mapgis.geometry.*;
import com.zondy.mapgis.view.GeometryElement;
import com.zondy.mapgis.view.Graphic;

/**
 * @author CR
 * @file PolygonSketchTool.java
 * @brief 折线区工具
 * @create 2020-06-04.
 */
public final class PolygonSketchTool extends MultipartSketchTool {
    public PolygonSketchTool(MapControl mapControl) {
        super(mapControl);
    }

    @Override
    public GeometryType getGeometryType() {
        return GeometryType.GeoPolygon;
    }

    @Override
    public boolean onRubberBandMove(double x, double y) {
        if (!this.geometryBuilder.isEmpty() && this.selectedVertex != null) {
            Graphic rubberBandGraphic = this.addRubberBandGraphic();
            PolygonBuilder builder = new PolygonBuilder(this.geometryBuilder.getSRefData());
            PartCollection parts = ((MultipartBuilder) this.geometryBuilder).getParts();
            Part part = parts.get(this.selectedVertex.getPartIndex());
            int pointCount = part.getPointCount();
            int pointIndex = this.selectedVertex.getPointIndex();
            builder.addPoint(this.selectedVertex.getPoint());
            builder.addPoint(this.getMapPoint(x, y));
            if (pointIndex == 0 && pointCount > 1) {
                builder.addPoint(part.getDot3D(1));
            } else if (pointIndex == pointCount - 1) {
                builder.addPoint(part.getDot3D(0));
            } else {
                builder.addPoint(part.getDot3D(pointIndex + 1));
            }

            rubberBandGraphic.setGeometry(builder.toGeometry());
            this.mapControl.refreshOverlay();
        }
        return true;
    }

    /**
     * 是否可以添加点（输入区不能自相交）
     *
     * @param partIndex 当前点的Part索引
     * @param x         坐标点X
     * @param y         坐标点X
     * @param isMP      是否是地图坐标，窗口坐标需要转换
     * @return
     */
    private boolean canAddPoint(int partIndex, double x, double y, boolean isMP) {
        Dot dotCursor = new Dot(x, y);
        if (!isMP)//若传入参数是设备坐标需转化
        {
            dotCursor = CoordinateTran.wpToMp(this.mapControl, (int) x, (int) y);
        }
        MultipartBuilder multipartBuilder = (MultipartBuilder) this.geometryBuilder;
        if (partIndex >= 0 && partIndex < multipartBuilder.getParts().size()) {
            Part part = multipartBuilder.getParts().get(partIndex);
            double curX = dotCursor.getX();
            double curY = dotCursor.getY();
            if (part != null) {
                double tolerance = 0.000001;
                for (int i = 0; i < part.getPointCount(); i++) {
                    Dot3D dot3D = part.getDot3D(i);
                    if (Math.abs(dot3D.getX() - curX) < tolerance && Math.abs(dot3D.getY() - curY) < tolerance) {
                        return false;//同一个part不能有重复点
                    }
                }

                if (part.getPointCount() >= 2) {
                    GeoVarLine line = new GeoVarLine();
                    for (int i = 0; i < part.getPointCount(); i++) {
                        line.append3D(part.getDot3D(i));
                    }
                    line.append3D(new Dot3D(dotCursor.getX(), dotCursor.getY(), 0.0));
                    int selfCross = GeometryOperator.calculateLineSelfIntersection(line, tolerance, null, null);
                    if (selfCross > 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 是否可以添加点（输入区不能自相交）
     *
     * @param partIndex 当前点的Part索引
     * @param x         坐标点X
     * @param y         坐标点X
     * @param isMP      是否是地图坐标，窗口坐标需要转换
     * @return
     */
    private boolean canMovePoint(int partIndex, int pointIndex, double x, double y, boolean isMP) {
        Dot dotCursor = new Dot(x, y);
        if (!isMP)//若传入参数是设备坐标需转化
        {
            dotCursor = CoordinateTran.wpToMp(this.mapControl, (int) x, (int) y);
        }

        MultipartBuilder multipartBuilder = (MultipartBuilder) this.geometryBuilder;
        if (partIndex >= 0 && partIndex < multipartBuilder.getParts().size()) {
            Part part = multipartBuilder.getParts().get(partIndex);
            double curX = dotCursor.getX();
            double curY = dotCursor.getY();
            if (part != null) {
                double tolerance = 0.000001;
                for (int i = 0; i < part.getPointCount(); i++) {
                    if (i != pointIndex) {
                        Dot3D dot3D = part.getDot3D(i);
                        if (Math.abs(dot3D.getX() - curX) < tolerance && Math.abs(dot3D.getY() - curY) < tolerance) {
                            return false;//同一个part不能有重复点
                        }
                    }
                }

                if (part.getPointCount() >= 2) {
                    GeoVarLine line = new GeoVarLine();
                    for (int i = 0; i < part.getPointCount(); i++) {
                        if (i == pointIndex) {
                            line.append3D(new Dot3D(dotCursor.getX(), dotCursor.getY(), 0.0));
                        } else {
                            line.append3D(part.getDot3D(i));
                        }
                    }
                    int selfCross = GeometryOperator.calculateLineSelfIntersection(line, tolerance, null, null);
                    if (selfCross > 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
