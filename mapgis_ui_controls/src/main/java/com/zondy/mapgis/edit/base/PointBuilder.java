package com.zondy.mapgis.edit.base;


import com.zondy.mapgis.geometry.Dot3D;
import com.zondy.mapgis.geometry.GeoPoint;
import com.zondy.mapgis.srs.SRefData;

/**
 * PointBuilder
 *
 * @author cxy
 * @date 2020/05/19
 */
public class PointBuilder extends GeometryBuilder {

    public PointBuilder(SRefData sRefData) {
        super(new GeoPoint(new Dot3D(0, 0, 0)), sRefData);
    }

    public PointBuilder(GeoPoint geoPoint, SRefData sRefData) {
        super(geoPoint, sRefData);
    }

    public PointBuilder(double x, double y, SRefData sRefData) {
        super(new GeoPoint(new Dot3D(x, y, 0)), sRefData);
    }

    public PointBuilder(double x, double y, double z, SRefData sRefData) {
        super(new GeoPoint(new Dot3D(x, y, z)), sRefData);
    }

    public double getX() {
        return ((GeoPoint) this.geometry).get().getX();
    }

    public void setX(double x) {
//        this.setDirty();
        ((GeoPoint) this.geometry).get().setX(x);
    }

    public double getY() {
        return ((GeoPoint) this.geometry).get().getY();
    }

    public void setY(double y) {
//        this.setDirty();
        ((GeoPoint) this.geometry).get().setY(y);
    }

    public double getZ() {
        return ((GeoPoint) this.geometry).get().getZ();
    }

    public void setZ(double z) {
//        this.setDirty();
        ((GeoPoint) this.geometry).get().setZ(z);
    }

    public void setXY(double x, double y) {
//        this.setDirty();
        ((GeoPoint) this.geometry).get().setX(x);
        ((GeoPoint) this.geometry).get().setY(y);
    }

    @Override
    public boolean isSketchValid() {
        return this.geometry instanceof GeoPoint;
    }

    /**
     * Indicates if any coordinates have been added to this builder.
     *
     * @return
     */
    @Override
    public boolean isEmpty() {
        return !(this.geometry instanceof GeoPoint);
    }

    @Override
    public GeoPoint toGeometry() {
        return (GeoPoint) this.geometry;
    }
}
