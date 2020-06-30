package com.zondy.mapgis.edit.base;

import com.zondy.mapgis.geometry.Dot3D;
import com.zondy.mapgis.geometry.GeoVarLine;
import com.zondy.mapgis.geometry.Geometry;
import com.zondy.mapgis.srs.SRefData;

import java.util.Iterator;

/**
 * MultipartBuilder
 *
 * @author cxy
 * @date 2020/05/19
 */
public abstract class MultipartBuilder extends GeometryBuilder {
    private final PartCollection partCollection = new PartCollection();

    protected MultipartBuilder(SRefData sRefData) {
        super(sRefData);
    }

    protected MultipartBuilder(Dot3DCollection dot3Ds, SRefData sRefData) {
        super(sRefData);
        this.addPoints(dot3Ds);
    }

    protected MultipartBuilder(Part part, SRefData sRefData) {
        super(sRefData);
        this.addPart(part);
    }

    protected MultipartBuilder(PartCollection parts, SRefData sRefData) {
        super(sRefData);
        this.addParts(parts);
    }

    public PartCollection getParts() {
        return this.partCollection;
    }

    public void addPart() {
        this.addPart(new Part(null));
    }

    public void addPart(Part part) {
        if (part == null) {
            throw new NullPointerException(String.format("Parameter %s must not be null", "part"));
        } else {
            this.getParts().add(part);
        }
    }

    public void addPart(Dot3DCollection dot3Ds) {
        if (dot3Ds == null) {
            throw new NullPointerException(String.format("Parameter %s must not be null", "dot3Ds"));
        } else {
            this.addPart(new Part(dot3Ds));
        }
    }

    public void addParts(PartCollection parts) {
        if (parts == null) {
            throw new NullPointerException(String.format("Parameter %s must not be null", "parts"));
        } else {
            this.getParts().addAll(parts);
        }
    }

    public void addPoint(double x, double y) {
        this.addPoint(new Dot3D(x, y, 0));
    }

    public void addPoint(double x, double y, double z) {
        this.addPoint(new Dot3D(x, y, z));
    }

    public void addPoint(Dot3D dot3D) {
        if (dot3D == null) {
            throw new NullPointerException(String.format("Parameter %s must not be null", "dot3D"));
        } else {
            if (this.getParts().size() == 0) {
                this.addPart();
            }
            this.getParts().get(this.getParts().size() - 1).add(dot3D);
        }
    }

    public void addPointToPart(int partIndex, Dot3D dot3D) {
        if (dot3D != null) {
            Part part = this.getParts().get(partIndex);
            if (part != null) {
                part.add(dot3D);
            }
        }
    }

    public void addPoints(Iterable<Dot3D> dot3Ds) {
        if (dot3Ds == null) {
            throw new NullPointerException(String.format("Parameter %s must not be null", "dot3Ds"));
        } else {
            if (this.getParts().size() == 0) {
                this.addPart();
            }
            Part part = this.getParts().get(this.getParts().size() - 1);
            for (Dot3D curDot3D : dot3Ds) {
                part.add(curDot3D);
            }
        }
    }

    public void addPointsToPart(int partIndex, Iterable<Dot3D> dot3Ds) {
        if (dot3Ds == null) {
            throw new NullPointerException(String.format("Parameter %s must not be null", "dot3Ds"));
        } else {
            Part part = this.getParts().get(partIndex);
            if (part != null) {
                for (Dot3D dot3D : dot3Ds) {
                    part.add(dot3D);
                }
            }
        }
    }

    @Override
    public void replaceGeometry(Geometry geometry) {
        this.getParts().clear();
        super.replaceGeometry(geometry);
    }
}
