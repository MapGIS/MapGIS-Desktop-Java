package com.zondy.mapgis.edit.base;

import com.zondy.mapgis.geometry.Dot3D;
import com.zondy.mapgis.geometry.Dots3D;
import com.zondy.mapgis.srs.SRefData;
import com.zondy.mapgis.utilities.Check;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Dot3DCollection
 *
 * @author cxy
 * @date 2020/05/20
 */
public class Dot3DCollection extends ArrayList<Dot3D> {
    public Dot3DCollection()
    {

    }
    public Dot3DCollection(Dot3D... dot3Ds) {
        if (dot3Ds != null) {
            for (Dot3D dot3D : dot3Ds) {
                this.add(dot3D);
            }
        }
    }

    public boolean add(double x, double y) {
        return this.add(new Dot3D(x, y, 0));
    }

    public boolean add(double x, double y, double z) {
        return this.add(new Dot3D(x, y, z));
    }
}
