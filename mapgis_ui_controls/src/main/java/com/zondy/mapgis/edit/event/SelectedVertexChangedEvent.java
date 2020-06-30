package com.zondy.mapgis.edit.event;

import com.zondy.mapgis.edit.util.SketchVertex;
import com.zondy.mapgis.edit.view.SketchEditor;

import java.util.EventObject;

/**
 * SelectedVertexChangedEvent
 *
 * @author cxy
 * @date 2020/05/29
 */
public final class SelectedVertexChangedEvent extends EventObject {
    private static final long serialVersionUID = 1L;
    private final SketchEditor sketchEditor;
    private final SketchVertex sketchVertex;

    public SelectedVertexChangedEvent(SketchEditor source, SketchVertex sketchVertex) {
        super(source);
        this.sketchEditor = source;
        this.sketchVertex = sketchVertex;
    }

    @Override
    public SketchEditor getSource() {
        return this.sketchEditor;
    }

    public SketchVertex getSketchVertex() {
        return this.sketchVertex;
    }
}
