package com.zondy.mapgis.edit.tool;

import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.edit.base.MultipointBuilder;
import com.zondy.mapgis.geometry.Dot3D;
import com.zondy.mapgis.geometry.GeoPoint;
import com.zondy.mapgis.geometry.GeometryType;
import com.zondy.mapgis.view.Graphic;

/**
 * @author CR
 * @file MultiPointSketchTool.java
 * @brief 多点工具
 * @create 2020-05-18.
 */
public class MultiPointSketchTool extends VertexSketchTool {
    public MultiPointSketchTool(MapControl mapControl) {
        super(mapControl);
    }

    @Override
    public GeometryType getGeometryType() {
        return GeometryType.GeoMultiPoint;
    }

    /**
     * 添加或插入（在选中点后面）点
     *
     * @param x
     * @param y
     */
    @Override
    protected void addVertex(double x, double y) {
        MultipointBuilder multipointBuilder = (MultipointBuilder) this.geometryBuilder;
        Dot3D mapPoint = this.getMapPoint(x, y);
        int pointIndex = multipointBuilder.getDot3DCollection().size();
        if (this.selectedVertex != null) {
            this.insertVertexAfterSelectedVertex(mapPoint);
        } else {
            this.sketchEditor.addCommand(new AddVertexCommand(this, 0, pointIndex, mapPoint));
        }
    }

    @Override
    protected void handlePrepareMove(double x, double y) {
        if (this.selectedVertex != null) {
            this.selectedVertex.setVisible(false);
        }
    }

    /**
     * 完成选中点的移动
     *
     * @param x
     * @param y
     */
    @Override
    protected void handleOnSinglePointerUp(double x, double y) {
        if (this.isAllowVertexInteractionEdit()) {
            Dot3D mapPoint = this.getMapPoint(x, y);
            MultipointBuilder multipointBuilder = (MultipointBuilder) this.geometryBuilder;
            Dot3D fromPoint = multipointBuilder.getDot3DCollection().get(this.selectedVertex.getPointIndex());
            this.sketchEditor.addCommand(new MoveVertexCommand(this, this.selectedVertex.getPartIndex(), this.selectedVertex.getPointIndex(), fromPoint, this.selectedVertex.getPointIndex(), mapPoint));
        }
        this.selectedVertex.setVisible(true);
    }

    /**
     * 将移动中的位置点设置到movingGeometryGraphic
     *
     * @param x
     * @param y
     * @return
     */
    @Override
    protected boolean handleOnSinglePointerMove(double x, double y) {
        boolean rtn = false;
        if (this.isAllowVertexInteractionEdit()) {
            Dot3D mapPoint = this.getMapPoint(x, y);
            this.getMovingGeometryGraphic().setGeometry(new GeoPoint(mapPoint));
            rtn = true;
        }
        return rtn;
    }

    /**
     * 刷新绘制
     */
    @Override
    public void updateSketch() {
        this.clearGraphics();
        MultipointBuilder multipointBuilder = (MultipointBuilder) this.geometryBuilder;
        this.vertices.clear();
        int partIndex = 0;
        int totalPoints = multipointBuilder.getDot3DCollection().size();
        for (int pointIndex = 0; pointIndex < totalPoints; pointIndex++) {
            Dot3D point = multipointBuilder.getDot3DCollection().get(pointIndex);
            Graphic vertexGraphic = this.addVertexGraphic(point, partIndex, pointIndex);
            this.vertices.add(vertexGraphic);
        }
        this.mapControl.refreshOverlay();
    }
}
