package com.zondy.mapgis.edit.base;

import com.zondy.mapgis.geometry.Dot3D;

/**
 * LineSegment
 *
 * Represents a straight line between a start and end Point.
 * LineSegments are used to represent the edges of Polygons and Polylines.
 *
 * LineSegment is immutable. Create new instances by using the constructors and factory methods, instead of changing the properties of an existing LineSegment.
 *
 * @author cxy
 * @date 2020/05/20
 */
public class LineSegment extends Segment {
    public LineSegment(double startX, double startY, double endX, double endY) {
        super(new Dot3D(startX, startY, 0), new Dot3D(endX, endY, 0));
    }

    public LineSegment(double startX, double startY, double startZ, double endX, double endY, double endZ) {
        super(new Dot3D(startX, startY, startZ), new Dot3D(endX, endY, endZ));
    }

    public LineSegment(Dot3D startDot3D, Dot3D endDot3D) {
        super(startDot3D, endDot3D);
    }

    @Override
    public String toString() {
        Dot3D startDot3D = this.getStartDot3D();
        Dot3D endDot3D = this.getEndDot3D();
        String retVal = "";
        if (startDot3D != null && endDot3D != null) {
            retVal = String.format("Line: [%f, %f], [%f, %f]", startDot3D.getX(), startDot3D.getY(), endDot3D.getX(), endDot3D.getY());
        } else {
            retVal = "Line: invalid (null start or end point)";
        }

        return retVal;
    }
}
