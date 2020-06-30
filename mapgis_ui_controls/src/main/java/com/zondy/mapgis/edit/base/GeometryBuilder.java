package com.zondy.mapgis.edit.base;

import com.zondy.mapgis.geometry.GeoPoint;
import com.zondy.mapgis.geometry.GeoRect;
import com.zondy.mapgis.geometry.Geometry;
import com.zondy.mapgis.geometry.GeometryType;
import com.zondy.mapgis.srs.SRefData;
import com.zondy.mapgis.utilities.Check;

/**
 * GeometryBuilder
 *
 * @author cxy
 * @date 2020/05/18
 */
public abstract class GeometryBuilder {
    protected Geometry geometry;
    protected SRefData sRefData;

    protected GeometryBuilder(SRefData sRefData) {
        this.sRefData = sRefData;
    }

    protected GeometryBuilder(Geometry geometry, SRefData sRefData) {
        this.geometry = geometry;
        this.sRefData = sRefData;
    }

    public SRefData getSRefData() {
        return this.sRefData;
    }

    /**
     * Check if a geometry builder contains sufficient points to show a valid graphical sketch.
     *
     * @return
     */
    public abstract boolean isSketchValid();

    /**
     * Replaces the geometry currently stored in this builder with the given geometry.
     *
     * @param geometry
     */
    public void replaceGeometry(Geometry geometry) {
        if (geometry == null) {
            throw new NullPointerException(String.format("Parameter %s must not be null", "geometry"));
        } else {
            this.geometry = geometry;
        }
    }

    /**
     * Indicates if any coordinates have been added to this builder.
     *
     * @return
     */
    public abstract boolean isEmpty();

    public abstract Geometry toGeometry();
}
