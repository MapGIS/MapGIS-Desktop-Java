package com.zondy.mapgis.edit.util;

import com.zondy.mapgis.base.XString;
import com.zondy.mapgis.geometry.Dot3D;
import com.zondy.mapgis.geometry.GeoMultiPoint;
import com.zondy.mapgis.geometry.GeoPoint;
import com.zondy.mapgis.geometry.Geometry;
import com.zondy.mapgis.utilities.Check;
import com.zondy.mapgis.view.DataPropertySet;
import com.zondy.mapgis.view.Graphic;

import java.util.Map;

/**
 * @author CR
 * @file Vertex.java
 * @brief 结点
 * @create 2020-05-19.
 */
public final class Vertex {
    public static final String PART_INDEX = "PART_INDEX";
    public static final String POINT_INDEX = "POINT_INDEX";

    private final Graphic graphic;//结点对应的Graphic对象
    private final SketchVertex sketchVertex;//草图结点

    public Vertex(Graphic graphic) {
        Check.throwIfNull(graphic, "graphic");

        this.graphic = graphic;
        int partIndex = -1;
        int pointIndex = -1;
        DataPropertySet dpSet = this.graphic.getAtt();
        if (dpSet != null) {
            String partValue = dpSet.getProperty(Vertex.PART_INDEX);
            if (!XString.isNullOrEmpty(partValue)) {
                partIndex = Integer.valueOf(partValue);
            }

            String ptrValue = dpSet.getProperty(Vertex.POINT_INDEX);
            if (!XString.isNullOrEmpty(ptrValue)) {
                pointIndex = Integer.valueOf(ptrValue);
            }
        }

        this.sketchVertex = new SketchVertex(partIndex, pointIndex);
        this.setPoint();
    }

    public Vertex(SketchVertex sketchVertex, Graphic graphic) {
        Check.throwIfNull(sketchVertex, "sketchVertex");
        Check.throwIfNull(graphic, "graphic");

        this.sketchVertex = sketchVertex;
        this.graphic = graphic;
        this.setPoint();
    }

    private void setPoint() {
        Dot3D dot3D = null;
        Geometry geometry = this.graphic.getGeometry();
        if (geometry instanceof GeoPoint) {
            dot3D = ((GeoPoint) geometry).get();
        } else if (geometry instanceof GeoMultiPoint) {
            GeoMultiPoint multiPoint = (GeoMultiPoint) geometry;
            if (multiPoint.getDotNum() > 0) {
                dot3D = multiPoint.get(0);
            }
        }
        this.sketchVertex.setPoint(dot3D);
    }

    public Dot3D getPoint() {
        return this.sketchVertex.getPoint();
    }

    public void setSelected(boolean selected) {
        this.graphic.setSelected(selected);
    }

    public void setVisible(boolean visible) {
        this.graphic.setVisible(visible);
    }

    //public void setSymbol(Symbol symbol) {
    //    Check.throwIfNull(symbol, "symbol");
    //    this.graphic.setSymbol(symbol);
    //}

    public DataPropertySet getAttributes() {
        return this.graphic.getAtt();
    }

    public int getPartIndex() {
        return this.sketchVertex.getPartIndex();
    }

    public int getPointIndex() {
        return this.sketchVertex.getPointIndex();
    }

    public boolean isSameGraphic(Graphic graphic) {
        return graphic == this.graphic;
    }

    public SketchVertex getSketchVertex() {
        return this.sketchVertex;
    }

    @Override
    public boolean equals(Object o) {
        boolean rtn = false;
        if (this == o) {
            rtn = true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Vertex vertex = (Vertex) o;
            if (this.sketchVertex != null && vertex.sketchVertex != null && this.sketchVertex.equals(vertex.sketchVertex)) {
                rtn = true;
            }
        }
        return rtn;
    }

    @Override
    public int hashCode() {
        return this.sketchVertex != null ? this.sketchVertex.hashCode() : super.hashCode();
    }
}
