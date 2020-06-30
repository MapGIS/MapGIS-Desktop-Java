package com.zondy.mapgis.edit.base;

import com.zondy.mapgis.geometry.*;
import com.zondy.mapgis.srs.SRefData;

/**
 * MultipointBuilder
 *
 * @author cxy
 * @date 2020/05/25
 */
public final class MultipointBuilder extends GeometryBuilder {

    public MultipointBuilder(SRefData sRefData) {
        super(new GeoMultiPoint(), sRefData);
    }

    public MultipointBuilder(GeoMultiPoint geoMultiPoint, SRefData sRefData) {
        super(geoMultiPoint, sRefData);
    }

    public MultipointBuilder(Iterable<Dot3D> dot3Ds, SRefData sRefData) {
        this(new GeoMultiPoint(), sRefData);
        for (Dot3D dot3D : dot3Ds) {
            ((GeoMultiPoint) this.geometry).append(dot3D);
        }
    }

    public MultipointBuilder(Dot3DCollection dot3DCollection, SRefData sRefData) {
        this(new GeoMultiPoint(), sRefData);
        for (Dot3D dot3D : dot3DCollection) {
            ((GeoMultiPoint) this.geometry).append(dot3D);
        }
    }

    public Dots3D getDots3D() {
        return ((GeoMultiPoint) this.geometry).getDots();
    }

    public void setDots3D(Dots3D dots3D) {
        ((GeoMultiPoint) this.geometry).setDots(dots3D);
    }

    public Dot3DCollection getDot3DCollection() {
        Dot3DCollection dot3DCollection = new Dot3DCollection(null);
        for (long i = 0; i < ((GeoMultiPoint) this.geometry).getDotNum(); i++) {
            dot3DCollection.add(((GeoMultiPoint) this.geometry).get(i));
        }
        return dot3DCollection;
    }

    public void setDot3DCollection(Dot3DCollection dot3DCollection) {
        Dots3D dots3D = new Dots3D();
        for (Dot3D dot3D : dot3DCollection) {
            dots3D.append(dot3D);
        }
        ((GeoMultiPoint) this.geometry).setDots(dots3D);
    }

    @Override
    public boolean isSketchValid() {
        return this.getDot3DCollection().size()>=0;
    }

    /**
     * Indicates if any coordinates have been added to this builder.
     *
     * @return
     */
    @Override
    public boolean isEmpty() {
        return this.getDot3DCollection().size() == 0;
    }

    @Override
    public GeoMultiPoint toGeometry() {
        return (GeoMultiPoint) this.geometry;
    }

    public GeometryBuilderType getBuilderType() {
        return GeometryBuilderType.MULTIPOINTBUILDER;
    }
}
