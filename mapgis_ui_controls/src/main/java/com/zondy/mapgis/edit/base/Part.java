package com.zondy.mapgis.edit.base;

import com.zondy.mapgis.geometry.Dot3D;
import com.zondy.mapgis.srs.SRefData;
import com.zondy.mapgis.utilities.Check;

import java.util.*;

/**
 * Part
 *
 * Remove Segment, just Dot3D
 *
 * Represents a mutable collection of Segments that define the shape of a part of a Multipart geometry under construction. Used in Polygon and Polyline constructors, and by MultipartBuilder.getParts().
 * Use the methods inherited from the generic Java AbstractList<T> class to define and change the shape of the geometry by adding, removing, or changing its Segments.
 * Additionally, the addPoint, addPoints, setPoint, and removePoint Point-based helper methods allow working with Points that represent the vertices of the Part, instead of working with Segments.
 *
 * Parts can then be added to, inserted into, and removed from PartCollections, in order to build up the complete shape of a geometry with multiple parts.
 *
 * @author cxy
 * @date 2020/05/20
 */
public class Part extends ArrayList<Dot3D/*Segment*/> {
//    public Part(Iterable<Segment> segments) {
//        this.dot3DCollectionImpl = new Dot3DCollectionImpl();
//        if (segments != null) {
//            Iterator iterator = segments.iterator();
//
//            while (iterator.hasNext()) {
//                Segment curSegment = (Segment) iterator.next();
//                this.add(curSegment);
//            }
//        }
//    }

    public Part(Dot3DCollection points) {
        if (points != null) {
            Iterator iterator = points.iterator();

            while (iterator.hasNext()) {
                Dot3D curPoint = (Dot3D) iterator.next();
                this.addDot3D(curPoint);
            }
        }
    }

//    @Override
//    public boolean contains(Object o) {
//        Iterator<Segment> it = this.iterator();
//        if (o == null) {
//            while (it.hasNext()) {
//                if (it.next() == null) {
//                    return true;
//                }
//            }
//        } else if (o instanceof Segment) {
//            Segment toFind = (Segment) o;
//
//            while (it.hasNext()) {
//                if (toFind.equals(it.next())) {
//                    return true;
//                }
//            }
//        }
//
//        return false;
//    }

//    @Override
//    public boolean removeAll(Collection<?> c) {
//        boolean modified = false;
//        Iterator it = this.iterator();
//        while (it.hasNext()) {
//            if (collectionContainsSegment(c, (Segment) it.next())) {
//                it.remove();
//                modified = true;
//            }
//        }
//
//        return modified;
//    }

//    @Override
//    public boolean retainAll(Collection<?> c) {
//        boolean modified = false;
//        Iterator it = this.iterator();
//
//        while (it.hasNext()) {
//            if (!collectionContainsSegment(c, (Segment) it.next())) {
//                it.remove();
//                modified = true;
//            }
//        }
//
//        return modified;
//    }

    public int getPointCount() {
        return this.size();
    }

    public Dot3D getStartDot3D() {
        return this.get(0);
    }

    public Dot3D getEndDot3D() {
        return this.get(this.size() - 1);
    }

    public boolean hasCurves() {
        return false;
    }

//    public Iterable<Dot3D> getDot3Ds() {
//        return this.dot3DCollectionImpl;
//    }

    public Dot3D getDot3D(int index) {
        return this.get(index);
    }

    public void addDot3D(Dot3D point) {
        this.add(point);
    }

    public void addDot3D(double x, double y) {
        this.add(new Dot3D(x, y, 0));
    }

    public void addDot3D(double x, double y, double z) {
        this.add(new Dot3D(x, y, z));
    }

    public void addDot3Ds(Collection<? extends Dot3D> dot3Ds) {
        this.addAll(dot3Ds);
    }

    public void addDot3D(int index, Dot3D dot3D) {
        this.add(index, dot3D);
    }

    public void addDot3D(int index, double x, double y) {
        this.add(index, new Dot3D(x, y, 0));
    }

    public void addDot3D(int index, double x, double y, double z) {
        this.add(index, new Dot3D(x, y, z));
    }

    public void addAllDot3Ds(int index, Collection<? extends Dot3D> dot3Ds) {
        this.addAll(index, dot3Ds);
    }

    public void setDot3D(int index, Dot3D dot3D) {
        Check.throwIfNull(dot3D, "dot3D");
        this.set(index, dot3D);
    }

    public Dot3D removeDot3D(int index) {
        return this.remove(index);
    }

//    public int getSegmentIndexFromStartDot3DIndex(int startPointIndex) {
//        Check.throwIfIndexOutOfBounds((double) startPointIndex, "startPointIndex", 0.0D, (double) (this.getPointCount() - 1));
//        return (int) this.mCoreMutablePart.getSegmentIndexFromStartPointIndex((long) startPointIndex);
//    }
//
//    public int getSegmentIndexFromEndDot3DIndex(int endPointIndex) {
//        Check.throwIfIndexOutOfBounds((double) endPointIndex, "endPointIndex", 0.0D, (double) (this.getPointCount() - 1));
//        return (int) this.mCoreMutablePart.getSegmentIndexFromEndPointIndex((long) endPointIndex);
//    }
//
//    public int getStartDot3DIndexFromSegmentIndex(int segmentIndex) {
//        Check.throwIfIndexOutOfBounds((double) segmentIndex, "segmentIndex", 0.0D, (double) (this.size() - 1));
//        return (int) this.mCoreMutablePart.getStartPointIndexFromSegmentIndex((long) segmentIndex);
//    }
//
//    public int getEndDot3DIndexFromSegmentIndex(int segmentIndex) {
//        Check.throwIfIndexOutOfBounds((double) segmentIndex, "segmentIndex", 0.0D, (double) (this.size() - 1));
//        return (int) this.mCoreMutablePart.getEndPointIndexFromSegmentIndex((long) segmentIndex);
//    }


//    private static boolean collectionContainsSegment(Collection<?> c, Segment segment) {
//        Iterator var2 = c.iterator();
//
//        Object curSegment;
//        do {
//            if (!var2.hasNext()) {
//                return false;
//            }
//
//            curSegment = var2.next();
//        } while (!curSegment.equals(segment));
//
//        return true;
//    }

}
