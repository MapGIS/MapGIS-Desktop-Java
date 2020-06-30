package com.zondy.mapgis.edit.base;

import com.zondy.mapgis.common.CoordinateTran;
import com.zondy.mapgis.geometry.*;
import com.zondy.mapgis.map.MapLayer;
import com.zondy.mapgis.srs.SRefData;

/**
 * PolygonBuilder
 *
 * @author cxy
 * @date 2020/05/19
 */
public class PolygonBuilder extends MultipartBuilder {

    public PolygonBuilder(SRefData sRefData) {
        super(sRefData);
    }

    public PolygonBuilder(GeoPolygon geoPolygon, SRefData sRefData) {
        super(sRefData);
        for (long i = 0; i < geoPolygon.getCircleNum(); i++) {
            Dots dots = geoPolygon.getDots(i);
            Part part = new Part(null);
            for (int j = 0; j < dots.size(); j++) {
                part.addDot3D(dots.get(j).getX(), dots.get(j).getY());
            }
            this.addPart(part);
        }
    }

    public PolygonBuilder(Dot3DCollection dot3Ds, SRefData sRefData) {
        super(dot3Ds, sRefData);
    }

    public PolygonBuilder(Part part, SRefData sRefData) {
        super(part, sRefData);
    }

    public PolygonBuilder(PartCollection parts, SRefData sRefData) {
        super(parts, sRefData);
    }

    @Override
    public boolean isSketchValid() {
        for (Part part : this.getParts()) {
            if (part.getPointCount() >= 3) {
                return true;
            }
        }

        return false;
    }

    /**
     * Indicates if any coordinates have been added to this builder.
     *
     * @return
     */
    @Override
    public boolean isEmpty() {
        for (Part part : this.getParts()) {
            if (part.getPointCount() > 0) {
                return false;
            }
        }

        return true;
    }

    @Override
    public GeoPolygon toGeometry() {
        GeoPolygon geoPolygon = new GeoPolygon();
        PartCollection parts = this.getParts();
        for (Part part : parts) {
            GeoMultiLine geoMultiLine = new GeoMultiLine();
            GeoVarLine geoVarLine = new GeoVarLine();
            for (Dot3D dot3D : part) {
                geoVarLine.append3D(dot3D);
            }
            if (part.getPointCount() > 0) {
                geoVarLine.append3D(part.get(0));
            }
            geoMultiLine.append(geoVarLine);
            geoPolygon.append(geoMultiLine);
        }
        return geoPolygon;
    }

    @Override
    public void replaceGeometry(Geometry geometry) {
        super.replaceGeometry(geometry);
        if (geometry instanceof GeoPolygon) {
                 Dots3D[] dotsArray = ((GeoPolygon) geometry).getDots3DArray();
             for (Dots3D dots : dotsArray) {
                 Part part = new Part(null);
                 for (int i = 0; i < dots.size(); i++) {
                     part.addDot3D(dots.get(i));
                 }
                 this.addPart(part);
            }
        }
    }

    public GeoVarLine toGeoVarline() {
        GeoVarLine geoVarLine = new GeoVarLine();
        this.getParts().getPartsAsDot3Ds().forEach(geoVarLine::append3D);
        return geoVarLine;
    }
}
