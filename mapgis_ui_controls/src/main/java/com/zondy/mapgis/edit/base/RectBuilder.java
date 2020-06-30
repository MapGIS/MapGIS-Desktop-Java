package com.zondy.mapgis.edit.base;

import com.zondy.mapgis.geometry.Dot;
import com.zondy.mapgis.geometry.GeoRect;
import com.zondy.mapgis.geometry.GeoRect1;
import com.zondy.mapgis.geometry.Geometry;
import com.zondy.mapgis.srs.SRefData;
import com.zondy.mapgis.utilities.Check;

/**
 * RectBuilder
 *
 * @author cxy
 * @date 2020/05/19
 */
public class RectBuilder extends GeometryBuilder {
    public RectBuilder(SRefData sRefData) {
        super(new GeoRect(), sRefData);
    }

    public RectBuilder(GeoRect geoRect, SRefData sRefData) {
        super(geoRect, sRefData);
    }

    public RectBuilder(Dot lbDot, Dot rtDot, double angle, SRefData sRefData) {
        super(new GeoRect(), sRefData);
        Check.throwIfNull(lbDot, "lbDot");
        Check.throwIfNull(rtDot, "rtDot");
        this.setLbDot(lbDot);
        this.setRtDot(rtDot);
        this.setAngle(angle);
    }

    public Dot getLbDot() {
        return ((GeoRect) this.geometry).getLbDot();
    }

    public void setLbDot(Dot lbDot) {
//        this.setDirty();
        ((GeoRect) this.geometry).setLbDot(lbDot);
    }

    public Dot getRtDot() {
        return ((GeoRect) this.geometry).getRtDot();
    }

    public void setRtDot(Dot rtDot) {
//        this.setDirty();
        ((GeoRect) this.geometry).setRtDot(rtDot);
    }

    public double getAngle() {
        return ((GeoRect) this.geometry).getAngle();
    }

    public void setAngle(double angle) {
//        this.setDirty();
        ((GeoRect) this.geometry).setAngle(angle);
    }

    public double getHeight() {
        return getRtDot().getY() - getLbDot().getY();
    }

    public double getWidth() {
        return getRtDot().getX() - getLbDot().getX();
    }

    public double getXMax() {
        return getRtDot().getX();
    }

    public void setXMax(double xMax) {
        Dot dot = this.getRtDot();
        dot.setX(xMax);
        this.setRtDot(dot);
    }

    public double getXMin() {
        return getLbDot().getX();
    }

    public void setXMin(double xMin) {
        Dot dot = this.getLbDot();
        dot.setX(xMin);
        this.setLbDot(dot);
    }

    public double getYMax() {
        return getRtDot().getY();
    }

    public void setYMax(double yMax) {
        Dot dot = this.getRtDot();
        dot.setY(yMax);
        this.setRtDot(dot);
    }

    public double getYMin() {
        return getLbDot().getY();
    }

    public void setYMin(double yMin) {
        Dot dot = this.getLbDot();
        dot.setY(yMin);
        this.setLbDot(dot);
    }

    @Override
    public boolean isSketchValid() {
        return this.getWidth() > 0 && this.getHeight() > 0;
    }

    /**
     * Indicates if any coordinates have been added to this builder.
     *
     * @return
     */
    @Override
    public boolean isEmpty() {
        return !(this.geometry instanceof GeoRect);
    }
//    public double getZMax() {
//        return nativeGetZMax(this.getHandle());
//    }
//
//    public void setZMax(double z_max) {
//        nativeSetZMax(this.getHandle(), z_max);
//    }
//
//    public double getZMin() {
//        return nativeGetZMin(this.getHandle());
//    }
//
//    public void setZMin(double z_min) {
//        nativeSetZMin(this.getHandle(), z_min);
//    }

    public void setXY(double xMin, double yMin, double xMax, double yMax) {
        Dot lbDot = this.getLbDot();
        Dot rtDot = this.getRtDot();
        lbDot.setX(xMin);
        lbDot.setY(yMin);
        rtDot.setX(xMax);
        rtDot.setY(yMax);
        this.setLbDot(lbDot);
        this.setRtDot(rtDot);
    }

//    public void setZ(double z_min, double z_max) {
//        nativeSetZ(this.getHandle(), z_min, z_max);
//    }

    @Override
    public GeoRect toGeometry() {
        return (GeoRect) this.geometry;
    }
}
