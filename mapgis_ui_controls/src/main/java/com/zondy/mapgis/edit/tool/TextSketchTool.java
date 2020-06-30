package com.zondy.mapgis.edit.tool;

import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.edit.base.PointBuilder;
import com.zondy.mapgis.edit.base.TextBuilder;
import com.zondy.mapgis.geometry.Dot;
import com.zondy.mapgis.geometry.Dot3D;
import com.zondy.mapgis.geometry.GeoPoint;
import com.zondy.mapgis.geometry.GeometryType;
import com.zondy.mapgis.view.Graphic;

import javax.xml.soap.Text;

/**
 * @author CR
 * @file TextSketchTool.java
 * @brief 注记工具
 * @create 2020-05-18.
 */
public class TextSketchTool extends VertexSketchTool {
    public TextSketchTool(MapControl mapControl) {
        super(mapControl);
    }

    @Override
    public GeometryType getGeometryType() {
        return GeometryType.GeoAnno;
    }

    /**
     * 添加点（如已经存在，则移动）
     *
     * @param x
     * @param y
     */
    @Override
    protected void addVertex(double x, double y) {
        Dot3D mapPoint = this.getMapPoint(x, y);
        TextBuilder textBuilder = (TextBuilder) this.geometryBuilder;
        if (textBuilder.isEmpty()) {
            this.sketchEditor.addCommand(new AddVertexCommand(this, 0, 0, mapPoint));
        } else {
            Dot dot = textBuilder.toGeometry().getAnchorDot();
            this.sketchEditor.addCommand(new MoveVertexCommand(this, 0, 0, new Dot3D(dot.getX(), dot.getY(), 0.0), 0, mapPoint));
        }
    }

    /**
     * 移动前的工作
     *
     * @param x
     * @param y
     */
    @Override
    protected void handlePrepareMove(double x, double y) {
        if (this.selectedVertex != null) {
            this.selectedVertex.setVisible(false);
        }
    }

    /**
     * 鼠标弹起时，若有选中点，则移动
     *
     * @param x
     * @param y
     */
    @Override
    protected void handleOnSinglePointerUp(double x, double y) {
        if (this.isAllowVertexInteractionEdit()) {
            Dot3D mapPoint = this.getMapPoint(x, y);
            if (this.selectedVertex != null) {
                this.sketchEditor.addCommand(new MoveVertexCommand(this, this.selectedVertex.getPartIndex(), 0, ((PointBuilder) this.geometryBuilder).toGeometry().get(), this.selectedVertex.getPointIndex(), mapPoint));
                this.selectedVertex.setVisible(true);
            }
        }
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
        if (this.isAllowVertexInteractionEdit()) {
            Dot3D mapPoint = this.getMapPoint(x, y);
            this.getMovingGeometryGraphic().setGeometry(new GeoPoint(mapPoint));
        }

        return true;
    }

    /**
     * 刷新绘制
     */
    @Override
    public void updateSketch() {
        this.clearGraphics();
        TextBuilder textBuilder = (TextBuilder) this.geometryBuilder;
        this.vertices.clear();
        Dot dot = textBuilder.toGeometry().getAnchorDot();
        Graphic vertexGraphic = this.addVertexGraphic(new Dot3D(dot.getX(), dot.getY(), 0.0), 0, 0);
        this.vertices.add(vertexGraphic);
        this.mapControl.refreshOverlay();
    }
}
