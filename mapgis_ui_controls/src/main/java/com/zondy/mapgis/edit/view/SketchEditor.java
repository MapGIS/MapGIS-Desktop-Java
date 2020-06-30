package com.zondy.mapgis.edit.view;

import com.zondy.mapgis.controls.EditType;
import com.zondy.mapgis.controls.InteractionListener;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.controls.MapCursors;
import com.zondy.mapgis.edit.CommandManager;
import com.zondy.mapgis.edit.event.SelectedVertexChangedEvent;
import com.zondy.mapgis.edit.event.SelectedVertexChangedListener;
import com.zondy.mapgis.edit.event.SketchGeometryChangedEvent;
import com.zondy.mapgis.edit.event.SketchGeometryChangedListener;
import com.zondy.mapgis.edit.tool.*;
import com.zondy.mapgis.edit.util.SketchVertex;
import com.zondy.mapgis.edit.util.Vertex;
import com.zondy.mapgis.geodatabase.IVectorCls;
import com.zondy.mapgis.geodatabase.SpaQueryMode;
import com.zondy.mapgis.geometry.Dot3D;
import com.zondy.mapgis.geometry.Geometry;
import com.zondy.mapgis.geometry.GeometryType;
import com.zondy.mapgis.map.*;
import com.zondy.mapgis.utilities.Check;
import com.zondy.mapgis.view.GraphicsOverlay;
import com.zondy.mapgis.view.SelectResult;
import com.zondy.mapgis.view.SketchGeometry;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * SketchEditor
 *
 * @author cxy
 * @date 2020/05/20
 */
public final class SketchEditor {
    private GeometryType geometryType;
    private boolean started = false;
    private float opacity = 1.0F;
    private boolean visible = true;

    private SketchStyle sketchStyle;

    private GraphicsOverlay sketchOverlay;
    private MapControl mapControl;
    private SketchTool sketchTool;
    private SelectTool selectTool;

    private final List<ListenerRunner<SketchGeometryChangedListener, SketchGeometryChangedEvent>> sketchGeometryChangedListenerRunners = new CopyOnWriteArrayList();
    private final List<ListenerRunner<SelectedVertexChangedListener, SelectedVertexChangedEvent>> selectedVertexChangedListenerRunners = new CopyOnWriteArrayList();
    private final CommandManager commandManager = new CommandManager();
    private SketchEditConfiguration sketchEditConfiguration;
    private SketchGeometry sketchGeometry;
    private MapLayer mapLayer;
    private long oid = -1;

    private SketchEditor.SketchEditorInteractionListener sketchEditorInteractionListener;
    private ContextMenu sketchEditorContextMenu;
    private InteractionListener mapControlInteractionListener;
    private ContextMenu mapControlContextMenu;

    public SketchEditor() {
        this.setSketchStyle(new SketchStyle());
        this.sketchEditConfiguration = new SketchEditConfiguration();
    }

    public void startSelect() {
        // 每添加一个类型的Tool，需要在stop中添加相应stop。
        SelectOption selectOption = new SelectOption();
        selectOption.setDataType(SelectDataType.AnyVector);
        selectOption.setLayerCtrl(SelectLayerControl.Visible);
        selectOption.setSelMode(SelectMode.Multiply);
        selectOption.setUnMode(UnionMode.Copy);
        this.selectTool = new SelectTool(this.mapControl, SelectType.RECTANGLE, selectOption, SpaQueryMode.ModeIntersect, this.mapControl.getTransformation());
        this.started = true;
        this.setSketchEditorInteractionListener();
        this.mapControl.setEditType(EditType.SELECT);
        this.mapControl.setCursor(MapCursors.ARROW);
    }

    public void startEditVertex() {
        // 每添加一个类型的Tool，需要在stop中添加相应stop。
    }

    public void startMove() {
        // 每添加一个类型的Tool，需要在stop中添加相应stop。
    }

    public void startInput(MapLayer layer, GeometryType geometryType) {
        this.startInputOrEdit(layer, -1, geometryType, null);
        this.mapControl.setEditType(EditType.INPUT);
        this.mapControl.setCursor(MapCursors.CROSS);
    }

    public void startEdit(SketchGeometry sketchGeometry) {
        this.startInputOrEdit(sketchGeometry.getLayer(), sketchGeometry.getObjID(), null, sketchGeometry.getGeometry());
        this.sketchGeometry = sketchGeometry;
        this.mapControl.setEditType(EditType.EDITVERTEX);
        this.mapControl.setCursor(MapCursors.EDITVERTEX);
    }

    public void startEdit(SelectResult selectResult) {
        this.startInputOrEdit(selectResult.getLayer(), selectResult.getObjID(), null, selectResult.getGeometry());
        this.mapControl.setEditType(EditType.EDITVERTEX);
        this.mapControl.setCursor(MapCursors.EDITVERTEX);
    }

    public void startEdit(MapLayer layer, long oid) {
        this.startInputOrEdit(layer, oid, null, null);
        this.mapControl.setEditType(EditType.EDITVERTEX);
        this.mapControl.setCursor(MapCursors.EDITVERTEX);
    }

    private void startInputOrEdit(MapLayer layer, long oid, GeometryType geometryType, Geometry geometry) {
        this.mapLayer = layer;
        this.oid = oid;
        this.sketchGeometry = null;

        if (geometry == null && layer != null && oid > 0) {
            IVectorCls vCls = (IVectorCls) layer.getData();
            if (vCls != null) {
                geometry = vCls.getGeometry(oid);
            }
        }

        if (geometryType == null && geometry != null) {
            geometryType = geometry.getType();
        }
        this.setSketchTool(geometry, geometryType);
        this.setSketchEditorInteractionListener();
    }

    public void reStartInput() {
        if (this.started) {
            this.sketchTool.clearGeometry();
            this.sketchOverlay.getGraphics().clear();
            this.commandManager.clear();
            this.sketchTool = null;
            this.started = false;
            this.mapControl.refreshOverlay();
        }

        if (this.mapControl.getEditType().equals(EditType.INPUT)) {
            this.startInput(this.mapLayer, this.geometryType);
        } else {
            this.startSelect();
        }
    }

    public void stop() {
        if (this.started) {
            if (this.sketchTool != null) {
                this.sketchTool.clearGeometry();
                this.sketchTool = null;
            }
            if (this.selectTool != null) {
                this.selectTool = null;
            }
            this.sketchOverlay.getGraphics().clear();
            this.mapControl.refreshOverlay();
            this.commandManager.clear();
            this.geometryType = null;
            this.started = false;
            this.mapControl.setEditType(EditType.NONE);
            this.mapControl.setCursor(Cursor.DEFAULT);
            this.resetMapViewInteractionListener();
        }
    }

    public void clearGeometry() {
        if (this.sketchTool != null) {
            this.sketchTool.clearGeometry();
        }
    }

    public void replaceGeometry(Geometry geometry) {
        Check.throwIfNull(geometry, "geometry");
        if (this.started) {
            this.checkGeometryCreationMode(geometry, this.geometryType);
            this.sketchTool.replaceGeometry(geometry);
            this.sketchTool.updateSketch();
            this.sketchTool.fireGeometryChanged();
        } else {
            throw new IllegalStateException("The replaceGeometry method cannot be called if the sketch editor is not started.");
        }
    }

    public boolean isSketchValid() {
        return this.sketchTool != null && this.sketchTool.isSketchValid();
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if (this.sketchOverlay != null) {
            this.sketchOverlay.setVisible(this.visible);
        }
    }

    public float getOpacity() {
        return this.opacity;
    }

    public void setOpacity(float opacity) {
        Check.throwIfNotInRange(opacity, "opacity", 0.0D, 1.0D);
        this.opacity = opacity;
        if (this.sketchOverlay != null) {
            this.sketchOverlay.setOpacity(opacity);
        }
    }

    public GeometryType getSketchGeometryType() {
        return this.geometryType;
    }

    public void setSketchStyle(SketchStyle sketchStyle) {
        Check.throwIfNull(sketchStyle, "sketchStyle");
        this.sketchStyle = sketchStyle;
//        this.mSketchStyleSymbolAdapter = new SketchStyleSymbolAdapter(this.sketchStyle);
        if (this.sketchTool != null) {
            this.sketchTool.updateSketch();
            SketchVertex sketchVertex = this.sketchTool.getSelectedVertex();
            if (sketchVertex != null) {
                this.sketchTool.selectVertex(sketchVertex.getPartIndex(), sketchVertex.getPointIndex());
            }

            this.sketchTool.fireGeometryChanged();
        }
    }

    public SketchStyle getSketchStyle() {
        return this.sketchStyle;
    }

    public SketchEditConfiguration getSketchEditConfiguration() {
        return this.sketchEditConfiguration;
    }

    public void setSketchEditConfiguration(SketchEditConfiguration configuration) {
        this.sketchEditConfiguration = configuration;
    }

    public void addSketchGeometryChangedListener(SketchGeometryChangedListener listener) {
        Check.throwIfNull(listener, "listener");
        this.sketchGeometryChangedListenerRunners.add(new ListenerRunner<SketchGeometryChangedListener, SketchGeometryChangedEvent>(listener) {
            @Override
            protected void onRun(SketchGeometryChangedEvent event) {
                listener.sketchGeometryChanged(event);
            }
        });
    }

    public boolean removeSketchGeometryChangedListener(SketchGeometryChangedListener listener) {
        return ListenerRunner.removeListener(this.sketchGeometryChangedListenerRunners, listener);
    }

    public void fireSketchGeometryChanged() {
        if (!this.sketchGeometryChangedListenerRunners.isEmpty()) {
            SketchGeometryChangedEvent event = new SketchGeometryChangedEvent(this);

            for (ListenerRunner<SketchGeometryChangedListener, SketchGeometryChangedEvent> sketchGeometryChangedListenerRunner : this.sketchGeometryChangedListenerRunners) {
                sketchGeometryChangedListenerRunner.run(event);
            }
        }
    }

    public void addSelectedVertexChangedListener(SelectedVertexChangedListener selectedVertexChangedListener) {
        Check.throwIfNull(selectedVertexChangedListener, "selectedVertexChangedListener");
        this.selectedVertexChangedListenerRunners.add(new ListenerRunner<SelectedVertexChangedListener, SelectedVertexChangedEvent>(selectedVertexChangedListener) {
            @Override
            protected void onRun(SelectedVertexChangedEvent event) {
                selectedVertexChangedListener.selectedVertexChanged(event);
            }
        });
    }

    public boolean removeSelectedVertexChangedListener(SelectedVertexChangedListener selectedVertexChangedListener) {
        return ListenerRunner.removeListener(this.selectedVertexChangedListenerRunners, selectedVertexChangedListener);
    }

    public void fireSelectedVertexChanged(SketchVertex newSketchVertex) {
        if (!this.selectedVertexChangedListenerRunners.isEmpty()) {
            SelectedVertexChangedEvent event = new SelectedVertexChangedEvent(this, newSketchVertex);

            for (ListenerRunner<SelectedVertexChangedListener, SelectedVertexChangedEvent> selectedVertexChangedListenerRunner : this.selectedVertexChangedListenerRunners) {
                selectedVertexChangedListenerRunner.run(event);
            }
        }
    }

    public Geometry getGeometry() {
        return this.sketchTool != null ? this.sketchTool.getGeometry() : null;
    }

    public void addCommand(CommandManager.Command command) {
        this.commandManager.addCommand(command);
    }

    public void undo() {
        this.commandManager.undo();
    }

    public boolean canUndo() {
        return this.commandManager.canUndo();
    }

    public void redo() {
        this.commandManager.redo();
    }

    public boolean canRedo() {
        return this.commandManager.canRedo();
    }

    public SketchVertex getSelectedVertex() {
        return this.sketchTool != null ? this.sketchTool.getSelectedVertex() : null;
    }

    public boolean setSelectedVertex(SketchVertex sketchVertex) {
        if (this.sketchTool != null) {
            if (sketchVertex == null) {
                return this.sketchTool.selectVertex(null);
            }

            if (this.sketchTool.isSketchVertexInGeometry(sketchVertex)) {
                return this.sketchTool.selectVertex(new Vertex(sketchVertex, this.sketchTool.findSketchVertexGraphic(sketchVertex)));
            }
        }

        return false;
    }

    public boolean removeSelectedVertex() {
        return this.sketchTool != null && this.sketchTool.removeSelectedVertex();
    }

    public boolean insertVertexAfterSelectedVertex(Dot3D dot3D) {
        Check.throwIfNull(dot3D, "point");
        return this.sketchTool != null && this.sketchTool.insertVertexAfterSelectedVertex(dot3D);
    }

    public boolean moveSelectedVertex(Dot3D dot3D) {
        Check.throwIfNull(dot3D, "point");
        return this.sketchTool != null && this.sketchTool.moveSelectedVertex(dot3D);
    }

    public void setMapControl(MapControl mapControl) {
        if (mapControl != null) {
            this.mapControl = mapControl;
            if (this.sketchOverlay == null) {
                this.sketchOverlay = new GraphicsOverlay();
                mapControl.setSketchGraphicsOverlay(this.sketchOverlay);
            }
            this.sketchEditorInteractionListener = new SketchEditor.SketchEditorInteractionListener();
            this.sketchEditorContextMenu = createMenu();
        }
    }

    private ContextMenu createMenu() {
        if (this.sketchEditorContextMenu != null) {
            return this.sketchEditorContextMenu;
        }
        ContextMenu contextMenu = new ContextMenu();
//        javafx.scene.control.MenuItem restoreMenuItem = new javafx.scene.control.MenuItem("复位地图(_F)");
//        restoreMenuItem.setOnAction((ActionEvent event) -> this.mapControl.restoreWnd());
//        javafx.scene.control.MenuItem refreshMenuItem = new javafx.scene.control.MenuItem("更新地图(_U)");
//        refreshMenuItem.setOnAction((ActionEvent event) -> this.mapControl.refreshWnd());
//        boolean b = contextMenu.getItems().addAll(restoreMenuItem, new SeparatorMenuItem(), refreshMenuItem);
//        contextMenu.setAutoHide(true);
        return contextMenu;
    }

    private void setSketchEditorInteractionListener() {
        if (this.sketchEditorInteractionListener != this.mapControl.getInteractionListener()) {
            this.mapControlInteractionListener = this.mapControl.getInteractionListener();
            this.mapControlContextMenu = this.mapControl.getContextMenu();
            this.mapControl.setInteractionListener(this.sketchEditorInteractionListener, this.sketchEditorContextMenu);
        }
    }

    private void setSketchToolContextMenuListener(ContextMenu sketchToolContextMenu) {
        SketchEditor.this.mapControl.setContextMenu(sketchToolContextMenu);

        //if (this.sketchEditorInteractionListener != null) {
        //    this.mapControl.setInteractionListener(this.sketchEditorInteractionListener, sketchToolContextMenu);
        //}
    }

    private void resetSketchEditorContextMenuListener() {
        if (this.sketchEditorInteractionListener != null && this.sketchEditorContextMenu != null) {
            this.mapControl.setInteractionListener(this.sketchEditorInteractionListener, this.sketchEditorContextMenu);
        }
    }

    private void resetMapViewInteractionListener() {
        if (this.mapControlInteractionListener != null) {
            this.mapControl.setInteractionListener(this.mapControlInteractionListener, this.mapControlContextMenu);
        }
    }

    private void setSketchTool(Geometry geometry, GeometryType geometryType) {
        if (this.mapControl != null && this.mapControl.getMap() != null/* && this.mapControl.getMap().getLoadStatus() == LoadStatus.LOADED*/) {
            switch (geometryType) {
                case GeoPoint:
                    this.sketchTool = new PointSketchTool(this.mapControl);
                    break;
                case GeoMultiPoint:
                    this.sketchTool = new MultiPointSketchTool(this.mapControl);
                    break;
                case GeoVarLine:
                    this.sketchTool = new PolylineSketchTool(this.mapControl);
                    break;
                case GeoMultiLine:
                    this.sketchTool = new PolylineSketchTool(this.mapControl);
                    break;
                case GeoPolygon:
                    this.sketchTool = new PolygonSketchTool(this.mapControl);
                    break;
                case GeoMultiPolygon:
                    this.sketchTool = new PolygonSketchTool(this.mapControl);
                    break;
                case GeoRect:
                    this.sketchTool = new RectSketchTool(this.mapControl);
                    break;
                case GeoAnno:
                    this.sketchTool = new TextSketchTool(this.mapControl);
                    break;
                default:
                    throw new UnsupportedOperationException("Not implemented");
            }

            this.geometryType = geometryType;
            this.sketchOverlay.setOpacity(this.opacity);
            this.sketchOverlay.setVisible(this.visible);
            if (geometry != null) {
                this.checkGeometryCreationMode(geometry, this.geometryType);
                this.sketchTool.replaceGeometryInternal(geometry);
                this.sketchTool.updateSketch();
                this.sketchTool.fireGeometryChanged();
            }

            this.started = true;
        } else {
            throw new IllegalStateException("Map view cannot be null and must have a loaded map");
        }
    }

    private void checkGeometryCreationMode(Geometry geometry, GeometryType geometryType) {
        if (!geometryType.equals(geometry.getType())) {
            throw new IllegalArgumentException("A geometry of type " + geometry.getType() + " is incompatible with the sketch creation mode " + geometryType);
        }
    }

    public boolean onPrimaryPointerClicked(double x, double y, int clickCount) {
        boolean rtn = false;
        if (this.sketchTool != null) {
            this.sketchTool.onPointerClicked(x, y);
            if (this.sketchTool instanceof PointSketchTool) {

                this.saveInputAndRestart();
            }
        }
        if (!this.isSketchValid()) {
            return rtn;
        }
        if (this.sketchTool instanceof PointSketchTool) {
            SketchGeometry sketchGeometry = null;
            Geometry geometry = this.getGeometry();
            if (geometry != null) {
                if (this.sketchGeometry != null) {
                    sketchGeometry = this.sketchGeometry;
                    sketchGeometry.setGeometry(geometry);
                } else {
                    sketchGeometry = new SketchGeometry();
                    sketchGeometry.setLayer(this.mapLayer);
                    sketchGeometry.setObjID(this.oid);
                    sketchGeometry.setGeometry(geometry);

                    this.mapControl.getSketchGeometrys().add(sketchGeometry);
                    this.mapControl.refreshWnd();
                }
            }
            this.reStartInput();
        }
        return rtn;
    }

    private void saveInputAndRestart() {
        if (this.isSketchValid()) {
            SketchGeometry sketchGeometry = null;
            Geometry geometry = this.getGeometry();
            if (geometry != null) {
                if (this.sketchGeometry != null) {
                    sketchGeometry = this.sketchGeometry;
                    sketchGeometry.setGeometry(geometry);
                } else {
                    sketchGeometry = new SketchGeometry();
                    sketchGeometry.setLayer(this.mapLayer);
                    sketchGeometry.setObjID(this.oid);
                    sketchGeometry.setGeometry(geometry);

                    this.mapControl.getSketchGeometrys().add(sketchGeometry);
                }
                this.mapControl.refreshWnd();
            }
            this.reStartInput();
        }
    }

    public boolean onSecondaryPointerClicked(double x, double y, int clickCount) {
        if (this.sketchTool != null) {
            List<MenuItem> miList = this.sketchTool.onSecondaryPointerClicked(x, y);
            //空白处右键，结束输入。
            if (miList == null || miList.size() == 0) {
                this.setSketchToolContextMenuListener(null);
                this.saveInputAndRestart();
            } else {
                //TODO：显示右键菜单

                ContextMenu contextMenu = new ContextMenu(miList.toArray(new MenuItem[0]));
                this.setSketchToolContextMenuListener(contextMenu);
            }
        }
        return false;
    }

    public boolean onSinglePointerDown(double x, double y) {
        boolean rtn = false;
        if (this.selectTool != null) {
            this.selectTool.onMouseDown(x, y);
            rtn = true;
        }
        if (this.sketchTool != null) {
            rtn = this.sketchTool.onSinglePointerDown(x, y);
        }
        return rtn;
    }

    public boolean onSinglePointerMove(double x, double y) {
        boolean rtn = false;
        if (this.selectTool != null) {
            this.selectTool.onMouseMove(x, y);
            rtn = true;
        }
        if (this.sketchTool != null) {
            rtn = this.sketchTool.onSinglePointerMove(x, y);
        }
        return rtn;
    }

    public boolean onSinglePointerUp(double x, double y) {
        boolean rtn = false;
        if (this.selectTool != null) {
            this.selectTool.onMouseUp(x, y);
            rtn = true;
        }
        if (this.sketchTool != null) {
            rtn = this.sketchTool.onSinglePointerUp(x, y);
        }
        return rtn;
    }

    int aa = 0;

    public boolean onRubberBandMove(double x, double y) {
        boolean rtn = false;
        if (this.selectTool != null) {
            this.selectTool.onMouseMove(x, y);
            rtn = true;
        }
        if (this.sketchTool != null) {
            rtn = this.sketchTool.onRubberBandMove(x, y);
        }
        return rtn;
    }

    public boolean onRubberBandExited() {
        return this.sketchTool != null && this.sketchTool.onRubberBandExited();
    }

    public boolean prepareMove(double x, double y) {
        return this.sketchTool != null && this.sketchTool.prepareMove(x, y);
    }

    final class SketchEditorInteractionListener extends MapControl.DefaultInteractionListener {
        private int mMoveTolerance = 8;
        private boolean mMoveInProgress;
        private int mLastPressX;
        private int mLastPressY;

        public SketchEditorInteractionListener() {
            super(SketchEditor.this.mapControl);
        }

        @Override
        public void onRemoved() {
            if (SketchEditor.this.started) {
                SketchEditor.this.sketchTool.clearGeometry();
                SketchEditor.this.sketchOverlay.getGraphics().clear();
                SketchEditor.this.mapControl.refreshOverlay();
                SketchEditor.this.commandManager.clear();
                SketchEditor.this.sketchTool = null;
                SketchEditor.this.geometryType = null;
                SketchEditor.this.started = false;
            }

            SketchEditor.this.mapControl.setCursor(Cursor.DEFAULT);
        }

        @Override
        public void onMousePressed(MouseEvent event) {
            if (!event.isSynthesized()) {
                this.mLastPressX = (int) SketchEditor.this.mapControl.getAdjustedX(event.getX());
                this.mLastPressY = (int) SketchEditor.this.mapControl.getAdjustedY(event.getY());
                this.mMoveInProgress = false;
                boolean isConsumed = SketchEditor.this.onSinglePointerDown(this.mLastPressX, this.mLastPressY);
                if (isConsumed) {
                    event.consume();
                }
            }
        }

        @Override
        public void onMouseReleased(MouseEvent event) {
            if (!event.isSynthesized() && event.getButton() == MouseButton.PRIMARY) {
                boolean isConsumed = SketchEditor.this.onSinglePointerUp(SketchEditor.this.mapControl.getAdjustedX(event.getX()), SketchEditor.this.mapControl.getAdjustedY(event.getY()));
                if (isConsumed) {
                    event.consume();
                }
            }
        }

        @Override
        public void onMouseClicked(MouseEvent event) {
            if (!event.isSynthesized() && event.isStillSincePress()) {
                double adjustedX = SketchEditor.this.mapControl.getAdjustedX(event.getX());
                double adjustedY = SketchEditor.this.mapControl.getAdjustedY(event.getY());
                if (event.getButton() == MouseButton.PRIMARY) {
                    SketchEditor.this.onPrimaryPointerClicked(adjustedX, adjustedY, event.getClickCount());
                } else if (event.getButton() == MouseButton.SECONDARY) {
                    SketchEditor.this.onSecondaryPointerClicked(adjustedX, adjustedY, event.getClickCount());
                }

                event.consume();
            }
        }

        @Override
        public void onMouseDragged(MouseEvent event) {
            if (!event.isSynthesized() && event.getButton() == MouseButton.PRIMARY) {
                boolean isConsumed = false;
                if (this.mMoveInProgress) {
                    isConsumed = SketchEditor.this.onSinglePointerMove(SketchEditor.this.mapControl.getAdjustedX(event.getX()), SketchEditor.this.mapControl.getAdjustedY(event.getY()));
                } else if (Math.abs(event.getX() - (double) this.mLastPressX) > (double) this.mMoveTolerance || Math.abs(event.getY() - (double) this.mLastPressY) > (double) this.mMoveTolerance) {
                    isConsumed = SketchEditor.this.prepareMove(this.mLastPressX, this.mLastPressY);
                    this.mMoveInProgress = true;
                }

                if (SketchEditor.this.sketchTool instanceof RectSketchTool) {
                    SketchEditor.this.sketchTool.onRubberBandMove(event.getX(), event.getY());
                }

                if (isConsumed) {
                    event.consume();
                }
            }
        }

        @Override
        public void onMouseMoved(MouseEvent event) {
            if (!event.isSynthesized() && (event.getButton() == MouseButton.NONE)) {
                boolean isConsumed = SketchEditor.this.onRubberBandMove(SketchEditor.this.mapControl.getAdjustedX(event.getX()), SketchEditor.this.mapControl.getAdjustedY(event.getY()));
                if (isConsumed) {
                    event.consume();
                }
            }
        }

        @Override
        public void onMouseExited(MouseEvent event) {
            boolean isConsumed = SketchEditor.this.onRubberBandExited();
            if (isConsumed) {
                event.consume();
            }
        }

        @Override
        public void onScroll(ScrollEvent event) {
            super.onScroll(event);
        }

        @Override
        public void onRotate(RotateEvent e) {
        }

        @Override
        public void onKeyPressed(KeyEvent keyEvent) {
            super.onKeyPressed(keyEvent);
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                SketchEditor.this.reStartInput();
            }
        }

        @Override
        public void onKeyReleased(KeyEvent e) {
            super.onKeyReleased(e);
        }

        @Override
        public void onKeyTyped(KeyEvent event) {
            super.onKeyTyped(event);
        }
    }

    public void clearRedoUndo() {
        this.commandManager.clear();
    }
}
