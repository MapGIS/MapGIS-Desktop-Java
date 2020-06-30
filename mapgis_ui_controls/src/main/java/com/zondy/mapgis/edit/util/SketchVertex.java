package com.zondy.mapgis.edit.util;

import com.zondy.mapgis.geometry.Dot3D;
import com.zondy.mapgis.geometry.GeoPoint;
import com.zondy.mapgis.utilities.Check;

/**
 * @author CR
 * @file SketchVertex.java
 * @brief 交互结点
 * @create 2020-05-19.
 */
public final class SketchVertex {
    private final int partIndex;//MultiPart中结点所在Part的索引
    private final int pointIndex;//结点在所在Part中的索引
    private Dot3D point;//结点中的点位

    public SketchVertex(int partIndex, int pointIndex) {
        Check.throwIfNegative(partIndex, "partIndex");
        Check.throwIfNegative(pointIndex, "pointIndex");

        this.partIndex = partIndex;
        this.pointIndex = pointIndex;
    }

    public int getPartIndex() {
        return this.partIndex;
    }

    public int getPointIndex() {
        return this.pointIndex;
    }

    public Dot3D getPoint() {
        return this.point;
    }

    public void setPoint(Dot3D point) {
        this.point = point;
    }

    public void setPoint(GeoPoint point) {
        if (point != null) {
            this.point = point.get();
        }
    }

    @Override
    public boolean equals(Object obj) {
        boolean rtn = false;
        if (this == obj) {
            rtn = true;
        } else if (obj != null && this.getClass() == obj.getClass()) {
            SketchVertex other = (SketchVertex) obj;
            if (this.partIndex == other.partIndex && this.pointIndex == other.pointIndex && (this.point == other.point)) {
                rtn = true;
            }
        }
        return rtn;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + this.partIndex;
        result = 31 * result + (this.point == null ? 0 : this.point.hashCode());
        result = 31 * result + this.pointIndex;
        return result;
    }
}
