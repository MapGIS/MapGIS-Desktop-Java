package com.zondy.mapgis.edit.base;

import com.zondy.mapgis.geometry.Dot;
import com.zondy.mapgis.geometry.GeoPoint;
import com.zondy.mapgis.geometry.TextAnno;
import com.zondy.mapgis.srs.SRefData;

/**
 * TextBuilder
 *
 * @author cxy
 * @date 2020/05/27
 */
public class TextBuilder extends GeometryBuilder {

    public TextBuilder(SRefData sRefData) {
        super(new TextAnno(), sRefData);
    }

    public TextBuilder(TextAnno textAnno, SRefData sRefData) {
        super(textAnno, sRefData);
    }

    public TextBuilder(double x, double y, String text, SRefData sRefData) {
        super(new TextAnno(), sRefData);
        ((TextAnno) this.geometry).setAnchorDot(new Dot(x, y));
        ((TextAnno) this.geometry).setText(text);
    }

    @Override
    public boolean isSketchValid() {
        return this.geometry instanceof TextAnno;
    }

    /**
     * Indicates if any coordinates have been added to this builder.
     *
     * @return
     */
    @Override
    public boolean isEmpty() {
        return !(this.geometry instanceof TextAnno);
    }

    @Override
    public TextAnno toGeometry() {
        return (TextAnno) this.geometry;
    }
}
