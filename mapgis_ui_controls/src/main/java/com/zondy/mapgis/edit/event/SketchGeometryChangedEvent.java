package com.zondy.mapgis.edit.event;

import com.zondy.mapgis.edit.view.SketchEditor;
import com.zondy.mapgis.geometry.Geometry;

import java.util.EventObject;

/**
 * SketchGeometryChangedEvent
 *
 * @author cxy
 * @date 2020/05/29
 */
public final class SketchGeometryChangedEvent extends EventObject {
    private static final long serialVersionUID = 1L;
    private final SketchEditor sketchEditor;
    private Geometry geometry;

    public SketchGeometryChangedEvent(SketchEditor source) {
        super(source);
        this.sketchEditor = source;
        this.geometry = this.sketchEditor.getGeometry();
    }

    @Override
    public SketchEditor getSource() {
        return this.sketchEditor;
    }

    public Geometry getGeometry() {
        return this.geometry;
    }
}
