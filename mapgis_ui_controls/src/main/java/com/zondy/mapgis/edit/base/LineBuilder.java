package com.zondy.mapgis.edit.base;

import com.zondy.mapgis.geometry.*;
import com.zondy.mapgis.srs.SRefData;

/**
 * LineBuilder
 *
 * @author cxy
 * @date 2020/05/19
 */
public class LineBuilder extends MultipartBuilder {

    public LineBuilder(SRefData sRefData) {
        super(sRefData);
    }

    public LineBuilder(GeoVarLine geoVarLine, SRefData sRefData) {
        super(sRefData);
        Dots3D dots3D = geoVarLine.get3Dots();
        for (int i = 0; i < dots3D.size(); i++) {
            this.addPoint(dots3D.get(i));
        }
    }

    public LineBuilder(Dot3DCollection dot3Ds, SRefData sRefData) {
        super(dot3Ds, sRefData);
    }

    public LineBuilder(Part part, SRefData sRefData) {
        super(part, sRefData);
    }

    public LineBuilder(PartCollection parts, SRefData sRefData) {
        super(parts, sRefData);
    }

    @Override
    public boolean isSketchValid() {
        for (Part part : this.getParts()) {
            if (part.getPointCount() >= 2) {
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
    public GeoVarLine toGeometry() {
        if (this.getParts().size() == 0) {
            return null;
        }
        GeoVarLine geoVarLine = new GeoVarLine();
        Part part = this.getParts().get(0);
        for (Dot3D dot3D : part) {
            geoVarLine.append3D(dot3D);
        }
        return geoVarLine;
    }

    @Override
    public void replaceGeometry(Geometry geometry) {
       super.replaceGeometry(geometry);
       if(geometry instanceof GeoVarLine){
           Dots3D dots3D = ((GeoVarLine)geometry).get3Dots();
           for (int i = 0; i < dots3D.size(); i++) {
               this.addPoint(dots3D.get(i));
           }
       }
    }
}
