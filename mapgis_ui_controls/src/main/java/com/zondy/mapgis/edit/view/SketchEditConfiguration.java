package com.zondy.mapgis.edit.view;

import com.zondy.mapgis.utilities.Check;

/**
 * SketchEditConfiguration
 *
 * @author cxy
 * @date 2020/05/20
 */
public final class SketchEditConfiguration {
    private boolean allowPartSelection;
    private boolean contextMenuEnabled;
    private boolean requireSelectionBeforeDrag;
    private SketchEditConfiguration.SketchVertexEditMode sketchVertexEditMode;

    public SketchEditConfiguration() {
        this.sketchVertexEditMode = SketchEditConfiguration.SketchVertexEditMode.INTERACTION_EDIT;
        this.allowPartSelection = true;
        this.contextMenuEnabled = true;
        this.requireSelectionBeforeDrag = false;
    }

    public boolean isAllowPartSelection() {
        return this.allowPartSelection;
    }

    public void setAllowPartSelection(boolean allowPartSelection) {
        this.allowPartSelection = allowPartSelection;
    }

    public boolean isContextMenuEnabled() {
        return this.contextMenuEnabled;
    }

    public void setContextMenuEnabled(boolean contextMenuEnabled) {
        this.contextMenuEnabled = contextMenuEnabled;
    }

    public boolean isRequireSelectionBeforeDrag() {
        return this.requireSelectionBeforeDrag;
    }

    public void setRequireSelectionBeforeDrag(boolean requireSelectionBeforeDrag) {
        this.requireSelectionBeforeDrag = requireSelectionBeforeDrag;
    }

    public SketchEditConfiguration.SketchVertexEditMode getVertexEditMode() {
        return this.sketchVertexEditMode;
    }

    public void setVertexEditMode(SketchEditConfiguration.SketchVertexEditMode sketchVertexEditMode) {
        Check.throwIfNull(sketchVertexEditMode, "sketchVertexEditMode");
        this.sketchVertexEditMode = sketchVertexEditMode;
    }

    public static enum SketchVertexEditMode {
        INTERACTION_EDIT,
        SELECT_ONLY;

        private SketchVertexEditMode() {
        }
    }
}
