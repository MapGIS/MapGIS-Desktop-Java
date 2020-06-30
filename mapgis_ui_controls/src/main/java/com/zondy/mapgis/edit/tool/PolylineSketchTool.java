package com.zondy.mapgis.edit.tool;

import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.edit.base.LineBuilder;
import com.zondy.mapgis.edit.base.MultipartBuilder;
import com.zondy.mapgis.edit.base.Part;
import com.zondy.mapgis.edit.base.PartCollection;
import com.zondy.mapgis.geometry.GeometryType;
import com.zondy.mapgis.view.Graphic;

/**
 * @author CR
 * @file PolylineSketchTool.java
 * @brief 折线工具
 * @create 2020-06-04.
 */
public final class PolylineSketchTool extends MultipartSketchTool {
    public PolylineSketchTool(MapControl mapControl) {
        super(mapControl);
    }

    @Override
    public GeometryType getGeometryType() {
        return GeometryType.GeoVarLine;
    }

    @Override
    public boolean onRubberBandMove(double x, double y) {
      if (!this.geometryBuilder.isEmpty() && this.selectedVertex != null) {
            Graphic rubberBandGraphic = this.addRubberBandGraphic();
            LineBuilder builder = new LineBuilder(this.geometryBuilder.getSRefData());
            PartCollection parts = ((MultipartBuilder) this.geometryBuilder).getParts();
            Part part = parts.get(this.selectedVertex.getPartIndex());
            boolean lastPoint = this.selectedVertex.getPointIndex() == part.getPointCount() - 1;
            if (lastPoint) {
                builder.addPoint(this.selectedVertex.getPoint());
                builder.addPoint(this.getMapPoint(x, y));
            } else {
                builder.addPoint(this.selectedVertex.getPoint());
                builder.addPoint(this.getMapPoint(x, y));
                builder.addPoint(part.getDot3D(this.selectedVertex.getPointIndex() + 1));
            }

            rubberBandGraphic.setGeometry(builder.toGeometry());
            this.mapControl.refreshOverlay();
        }
        return true;
    }
}
