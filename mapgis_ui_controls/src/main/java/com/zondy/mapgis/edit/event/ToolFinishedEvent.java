package com.zondy.mapgis.edit.event;

import com.zondy.mapgis.edit.tool.SketchTool;
import com.zondy.mapgis.geometry.Geometry;

import java.util.EventObject;

/**
 * @file ToolFinishedEvent.java
 * @brief 工具结束事件
 * 
 * @author CR
 * @date 2020-06-29.
 */ 
public class ToolFinishedEvent extends EventObject {
    private static final long serialVersionUID = 1158951589L;
    private final SketchTool sketchTool;
    private final Geometry geometry;

    public ToolFinishedEvent(SketchTool source, Geometry geometry) {
        super(source);
        this.sketchTool = source;
        this.geometry = geometry;
    }

    @Override
    public SketchTool getSource() {
        return this.sketchTool;
    }

    public Geometry getGeometry() {
        return this.geometry;
    }
}
