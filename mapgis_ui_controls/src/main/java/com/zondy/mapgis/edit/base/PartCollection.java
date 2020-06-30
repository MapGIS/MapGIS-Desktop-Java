package com.zondy.mapgis.edit.base;

import com.zondy.mapgis.geometry.Dot3D;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * PartCollection
 *
 * @author cxy
 * @date 2020/05/25
 */
public final class PartCollection extends ArrayList<Part> {
    public PartCollection() {
    }

    public PartCollection(Part part) {
        if (part != null) {
            this.add(part);
        }
    }

    public PartCollection(Dot3DCollection partAsDot3Ds) {
        this(new Part(partAsDot3Ds));
    }

    public PartCollection(PartCollection parts) {
        this.addAll(parts);
    }

    public Iterable<Dot3D> getPartsAsDot3Ds() {
        return new PartCollection.AllDot3DsIterable();
    }

    public boolean add(Dot3DCollection dot3DCollection) {
        if (dot3DCollection == null) {
            throw new NullPointerException(String.format("Parameter %s must not be null", "dot3DCollection"));
        } else {
            return this.add(new Part(dot3DCollection));
        }
    }

    public void add(int index, Dot3DCollection dot3DCollection) {
        if (dot3DCollection == null) {
            throw new NullPointerException(String.format("Parameter %s must not be null", "dot3DCollection"));
        } else if (index >= 0 && index <= this.size()) {
            this.add(index, new Part(dot3DCollection));
        } else {
            throw new IndexOutOfBoundsException(String.format("Parameter %s is out of bounds", "index"));
        }
    }

    private class AllDot3DsIterator implements Iterator<Dot3D> {
        private int partCount = 0;
        private int dot3DCount = 0;

        private AllDot3DsIterator() {
        }

        @Override
        public boolean hasNext() {
            return this.partCount < PartCollection.this.size() && this.dot3DCount < PartCollection.this.get(this.partCount).getPointCount();
        }

        @Override
        public Dot3D next() {
            Dot3D retVal = null;
            if (this.hasNext()) {
                Part currentPart = PartCollection.this.get(this.partCount);
                retVal = currentPart.getDot3D(this.dot3DCount);
                ++this.dot3DCount;
                if (this.dot3DCount >= currentPart.getPointCount()) {
                    this.dot3DCount = 0;
                    ++this.partCount;
                }
            }

            return retVal;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Read only iterator");
        }
    }

    private class AllDot3DsIterable implements Iterable<Dot3D> {
        private AllDot3DsIterable() {
        }

        @Override
        public Iterator<Dot3D> iterator() {
            return PartCollection.this.new AllDot3DsIterator();
        }
    }
}
