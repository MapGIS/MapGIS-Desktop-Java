package com.zondy.mapgis.edit.base;

import com.zondy.mapgis.geometry.Dot3D;
import com.zondy.mapgis.srs.SRefData;

/**
 * Segment
 *
 * Represents a linear shape, defined by a start and an end Point.
 * Segment is an abstract base class from which different types of segment class inherit, each defining a different way of connecting the start and end point. Segments are immutable.
 *
 * Currently only one segment type, LineSegment, is available. Polygons and Polylines are composed of one or more collections of Segments.
 *
 * @author cxy
 * @date 2020/05/20
 */
public abstract class Segment {
    private Dot3D startDot3D;
    private Dot3D endDot3D;

    protected Segment(Dot3D startDot3D, Dot3D endDot3D) {
        this.startDot3D = startDot3D;
        this.endDot3D = endDot3D;
    }

    public Dot3D getStartDot3D() {
        return this.startDot3D;
    }

    public Dot3D getEndDot3D() {
        return this.endDot3D;
    }

    public boolean isClosed() {
        return false;
    }

    public boolean isCurve() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Segment) {
            return pointsEqual(this.getStartDot3D(), ((Segment) obj).getStartDot3D()) && pointsEqual(this.getEndDot3D(), ((Segment) obj).getEndDot3D());
        } else {
            return false;
        }
    }

    private static boolean pointsEqual(Dot3D dot3DOne, Dot3D dot3DTwo) {
        return Double.compare(dot3DOne.getX(), dot3DTwo.getX()) == 0 && Double.compare(dot3DOne.getY(), dot3DTwo.getY()) == 0 && Double.compare(dot3DOne.getZ(), dot3DTwo.getZ()) == 0;
    }
}
