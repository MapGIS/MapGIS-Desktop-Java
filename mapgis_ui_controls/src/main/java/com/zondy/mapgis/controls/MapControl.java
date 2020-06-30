package com.zondy.mapgis.controls;

import com.zondy.mapgis.common.CoordinateTran;
import com.zondy.mapgis.controls.skin.MapControlSkin;
import com.zondy.mapgis.edit.view.SketchEditor;
import com.zondy.mapgis.geomap.inner.InternalResource;
import com.zondy.mapgis.geometry.Dot;
import com.zondy.mapgis.geometry.Dot3D;
import com.zondy.mapgis.geometry.Rect;
import com.zondy.mapgis.map.Map;
import com.zondy.mapgis.map.Transformation;
import com.zondy.mapgis.srs.SRefData;
import com.zondy.mapgis.utilities.Check;
import com.zondy.mapgis.view.*;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * MapControl
 * <p>
 * Created by zxf on 2019/10/31.
 */
public class MapControl extends Control {
    //region 变量
    private long mapControlNative;
    private static final int DEFAULT_WIDTH = 512;
    private static final int DEFAULT_HEIGHT = 512;
    private ContextMenu contextMenu = null;
    private Canvas zoomRectangle = null;
    private SketchEditor sketchEditor;
    private BackgroundImage backgroundImage = null; //绘制的数据图片
    private ObjectProperty<EditType> editType = new SimpleObjectProperty<>(EditType.NONE);
    private BooleanProperty editing = new SimpleBooleanProperty(false);//MapControl的编辑状态

    private final SimpleBooleanProperty enableKeyboardNavigationProperty = new SimpleBooleanProperty(true);
    private final SimpleBooleanProperty enableMouseZoomProperty = new SimpleBooleanProperty(true);
    private InteractionListener interactionListener;
    private ObservableList<SelectResult> selectedResults = FXCollections.observableArrayList();
    private ObservableList<SketchGeometry> selectedSketchGeometries = FXCollections.observableArrayList();
    //endregion

    //region 构造
    public MapControl() {
        this.setSkin(new MapControlSkin(this));
        createMenu();

        javafx.scene.canvas.Canvas cavas = new javafx.scene.canvas.Canvas(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        this.getChildren().add(cavas);
        GraphicsContext gc = cavas.getGraphicsContext2D();
        gc.setFill(javafx.scene.paint.Color.rgb(69, 110, 169, 0.4));
        gc.setStroke(javafx.scene.paint.Color.rgb(69, 110, 169, 1));
        gc.setLineWidth(1);
        cavas.setVisible(false);
        zoomRectangle = cavas;

        mapControlNative = MapControlNative.jni_CreateObj(this);
        this.setPrefWidth(DEFAULT_WIDTH);
        this.setPrefHeight(DEFAULT_HEIGHT);
        MapControlNative.jni_OnSize(mapControlNative, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.widthProperty().addListener((observable, oldValue, newValue) -> {
            double height = this.getHeight();
            cavas.setWidth(newValue.doubleValue());
            MapControlNative.jni_OnSize(mapControlNative, 0, newValue.intValue(), (int) height);
        });
        this.heightProperty().addListener((observable, oldValue, newValue) -> {
            double width = this.getWidth();
            cavas.setHeight(newValue.doubleValue());
            MapControlNative.jni_OnSize(mapControlNative, 0, (int) width, newValue.intValue());
        });

        this.setInteractionListener(new MapControl.DefaultInteractionListener(this), this.contextMenu);

        this.selectedResults.addListener(this::onSelectedResultsChanged);
        this.selectedSketchGeometries.addListener(this::onSelectedSketchGeometriesChanged);
    }

    private void createMenu() {
        contextMenu = new ContextMenu();
        javafx.scene.control.MenuItem zoominMenuItem = new javafx.scene.control.MenuItem("放大地图(_I)");
        zoominMenuItem.setOnAction((ActionEvent event) -> this.zoomIn());
        javafx.scene.control.MenuItem zoomoutMenuItem = new javafx.scene.control.MenuItem("缩小地图(_O)");
        zoomoutMenuItem.setOnAction((ActionEvent event) -> this.zoomOut());
        javafx.scene.control.MenuItem moveMenuItem = new javafx.scene.control.MenuItem("移动地图(_M)");
        moveMenuItem.setOnAction((ActionEvent event) -> this.moveWnd());
        javafx.scene.control.MenuItem restoreMenuItem = new javafx.scene.control.MenuItem("复位地图(_F)");
        restoreMenuItem.setOnAction((ActionEvent event) -> this.restoreWnd());
        javafx.scene.control.MenuItem refreshMenuItem = new javafx.scene.control.MenuItem("更新地图(_U)");
        refreshMenuItem.setOnAction((ActionEvent event) -> this.refreshWnd());
        javafx.scene.control.MenuItem preMenuItem = new javafx.scene.control.MenuItem("上级地图(_B)");
        preMenuItem.setOnAction((ActionEvent event) -> this.showPreWnd());
        javafx.scene.control.MenuItem nextMenuItem = new javafx.scene.control.MenuItem("下级地图(_N)");
        nextMenuItem.setOnAction((ActionEvent event) -> this.showNextWnd());
        javafx.scene.control.MenuItem clearMenuItem = new javafx.scene.control.MenuItem("清空(_C)");
        clearMenuItem.setOnAction((ActionEvent event) -> this.clearTool());
        boolean b = contextMenu.getItems().addAll(moveMenuItem, new SeparatorMenuItem(), zoominMenuItem, zoomoutMenuItem, new SeparatorMenuItem(), restoreMenuItem, new SeparatorMenuItem(), refreshMenuItem, new SeparatorMenuItem(), preMenuItem, nextMenuItem, new SeparatorMenuItem(), clearMenuItem);
        contextMenu.setAutoHide(true);
    }

    public void dispose() {
        MapControlNative.jni_DeleteObj(mapControlNative);
        mapControlNative = 0;
    }
    //endregion

    //region Callback
    protected void onCallbackToolChanged(int oldToolType, int newToolType) {
        switch (newToolType) {
            case 1:
                this.setCursor(MapCursors.ZOOMIN);
                break;
            case 2:
                this.setCursor(MapCursors.ZOOMOUT);
                break;
            case 3:
                this.setCursor(Cursor.OPEN_HAND);
                break;
            default:
                this.setCursor(Cursor.DEFAULT);
                break;
        }
    }

    protected void onCallbackAfterReFresh(byte[] imageBuffer) {
        try {
//            BaseFXRobot robot = new BaseFXRobot(this.getScene());
//            robot.waitForIdle();
            if (imageBuffer == null || imageBuffer.length <= 0) {
                this.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
            } else {
//                ByteArrayInputStream in = new ByteArrayInputStream(imageBuffer);
//                BufferedImage bufferImage = null;
//                bufferImage = ImageIO.read(in);
                //Image image = SwingFXUtils.toFXImage(bufferImage, null);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBuffer);
                Image image = new Image(inputStream);
                inputStream.close();
                backgroundImage = new BackgroundImage(image, null, null, null, null);
                this.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, null, null)}, new BackgroundImage[]{backgroundImage}));
                //this.setGraphic(new ImageView(image));
            }
        } catch (Exception e) {
        }
    }

    protected void onCallbackAfterOverlayReFresh(byte[] imageBuffer) {
        try {
            if (imageBuffer == null || imageBuffer.length <= 0) {
                this.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
            } else {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBuffer);
                Image image = new Image(inputStream);
                inputStream.close();

                BackgroundImage overlayImage = new BackgroundImage(image, null, null, null, null);
                BackgroundImage[] bgImages = null;
                if (backgroundImage != null) {
                    bgImages = new BackgroundImage[]{backgroundImage, overlayImage};
                } else {
                    bgImages = new BackgroundImage[]{overlayImage};
                }
                this.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, null, null)}, bgImages));
            }
        } catch (Exception e) {
        }
    }

    protected int onCallbackMiddleBitBlt(byte[] imageBuffer) {
        onCallbackAfterReFresh(imageBuffer);
        return MapControlNative.jni_IsCancel(0);
    }
    //endregion

    //region 基本方法（Map、Transformation等）
    public Map getMap() {
        long mapHandle = MapControlNative.jni_GetMap(mapControlNative);
        return (mapHandle == 0) ? null : new Map(mapHandle);
    }

    public void setMap(Map map) {
        MapControlNative.jni_SetMap(mapControlNative, map.getHandle());
    }

    public Transformation getTransformation() {
        long handle = MapControlNative.jni_GetTransformation(mapControlNative);
        return (handle == 0) ? null : new Transformation(handle);
    }

    public ToolType getWinOpType() {
        int i = MapControlNative.jni_GetWinOpType(mapControlNative);
        return ToolType.valueOf(i);
    }

    public long getNativeHandle() {
        return this.mapControlNative;
    }

    public Canvas getZoomRectangle() {
        return this.zoomRectangle;
    }

    private byte[] copyScreen() {
        byte[] rtn = null;
        try {
            Bounds b = this.getBoundsInLocal();
            Point2D p = this.localToScreen(b.getMinX(), b.getMinY());
            int x = (int) Math.round(p.getX());
            int y = (int) Math.round(p.getY());
            int w = (int) Math.round(b.getWidth());
            int h = (int) Math.round(b.getHeight());
//            FXRobot fxRobot = FXRobotFactory.createRobot(this.getScene());
//            FXRobotImage fxi = fxRobot.getSceneCapture(x, y, w, h);

            //使用AWT机器人获取图像
            java.awt.Robot robot = new java.awt.Robot();
            java.awt.image.BufferedImage bi = robot.createScreenCapture(new java.awt.Rectangle(x, y, w, h));
            //将BufferedImage转换为javafx.scene.image.Image
            java.io.ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
            ImageIO.write(bi, "png", stream);
            rtn = stream.toByteArray();
            stream.close();
        } catch (Exception e) {
        }
        return rtn;
    }

    //endregion

    //region 窗口操作（复位、更新……）
    public void restoreWnd() {
        MapControlNative.jni_RestoreWnd(mapControlNative);
    }

    public void refreshWnd() {
        MapControlNative.jni_RefreshWnd(mapControlNative);
    }

    public void showPreWnd() {
        MapControlNative.jni_ShowPreWnd(mapControlNative);
    }

    public void showNextWnd() {
        MapControlNative.jni_ShowNextWnd(mapControlNative);
    }

    public void moveWnd() {
        MapControlNative.jni_MoveWnd(mapControlNative);
    }

    public void jumpWnd(double devX, double devY, boolean refresh) {
        MapControlNative.jni_JumpWnd(mapControlNative, devX, devY, refresh);
    }

    public void jumpWnd(Rect devRect, boolean refresh) {
        MapControlNative.jni_JumpWnd(mapControlNative, devRect, refresh);
    }

    public void zoomIn() {
        MapControlNative.jni_ZoomIn(mapControlNative);
    }

    public void zoomOut() {
        MapControlNative.jni_ZoomOut(mapControlNative);
    }
    //endregion

    //region 比例尺
    public double getCurMapScale() {
        return MapControlNative.jni_GetCurMapScale(mapControlNative);
    }

    public int setCurMapScale(double scale, boolean refresh) {
        return MapControlNative.jni_SetCurMapScale(mapControlNative, scale, refresh);
    }

    public void setZoomScale(double zoomScale) {
        MapControlNative.jni_SetZoomScale(mapControlNative, zoomScale);
    }

    public double getZoomScale() {
        return MapControlNative.jni_GetZoomScale(mapControlNative);
    }
    //endregion

    //region 参照系和坐标

    /**
     * 获取MapControl中地图的参照系/动态投影参照系
     *
     * @return
     */
    public SRefData getSpatialReference() {
        SRefData sref = null;
        Map map = this.getMap();
        if (map != null) {
            sref = map.getIsProjTrans() ? map.getProjTrans() : map.getSRSInfo();
        }
        return sref;
    }

    /**
     * 根据MapControl窗口坐标获取地图坐标
     *
     * @param x
     * @param y
     * @return
     */
    public Dot screenToLocation(double x, double y) {
        Dot dot = null;
        Dot mp = CoordinateTran.wpToMp(this, x, y);
        if (mp != null) {
            dot = new Dot(mp.getX(), mp.getY());
        }
        return dot;
    }

    /**
     * 根据地图坐标获取MapControl窗口坐标
     *
     * @param x
     * @param y
     * @return
     */
    public Point2D locationToScreen(double x, double y) {
        Point2D point = null;
        Dot wp = CoordinateTran.mpToWp(this.getTransformation(), this.getSpatialReference(), new Dot(x, y));
        if (wp != null) {
            point = new Point2D(wp.getX(), wp.getY());
        }
        return point;
    }

    /**
     * 根据地图坐标获取MapControl窗口坐标
     *
     * @param dot3D
     * @return
     */
    public Point2D locationToScreen(Dot3D dot3D) {
        Point2D point = null;
        if (dot3D != null) {
            Dot wp = CoordinateTran.mpToWp(this.getTransformation(), this.getSpatialReference(), new Dot(dot3D.getX(), dot3D.getY()));
            if (wp != null) {
                point = new Point2D(wp.getX(), wp.getY());
            }
        }
        return point;
    }

    //endregion

    //region 选择

    public ObservableList<SelectResult> getSelectedResults() {
        return selectedResults;
    }

    public ObservableList<SketchGeometry> getSelectedSketchGeometries() {
        return selectedSketchGeometries;
    }

    public void onSelectedResultsChanged(ListChangeListener.Change<? extends SelectResult> c) {
        while (c.next()) {
            if (c.wasAdded()) {
                for (SelectResult selectResult : c.getAddedSubList()) {
                    Graphic graphic = new Graphic();
                    graphic.setGeometry(selectResult.getGeometry());
                    graphic.setSelected(true);
                    this.getSketchGraphicsOverlay().getGraphics().add(graphic);
                }
            }
        }
    }

    public void onSelectedSketchGeometriesChanged(ListChangeListener.Change<? extends SketchGeometry> c) {
        while (c.next()) {
            if (c.wasAdded()) {
                for (SketchGeometry sketchGeometry : c.getAddedSubList()) {
                    Graphic graphic = new Graphic();
                    graphic.setGeometry(sketchGeometry.getGeometry());
                    graphic.setSelected(true);
                    this.getSketchGraphicsOverlay().getGraphics().add(graphic);
                }
            }
        }
    }


    public void clearSelection() {
        this.clearSelection(true);
    }

    public void clearSelection(boolean refreh) {
        this.selectedResults.clear();
        this.selectedSketchGeometries.clear();
        this.getSketchGraphicsOverlay().getGraphics().clear();
        if (refreh) {
            this.refreshOverlay();
        }
    }

    public void Select(Dot point, double dTol) {
        this.clearSelection(false);

        SelectResultList selectResultList = this.identifySelections(point, dTol, false);
        if (selectResultList != null) {
            for (int i = 0; i < selectResultList.size(); i++) {
                this.selectedResults.add(selectResultList.get(i));
            }
        }

        SketchGeometryList sketchGeometryList = this.getSketchGeometrys().select(point, dTol, -1);
        if (sketchGeometryList != null) {
            for (int i = 0; i < sketchGeometryList.size(); i++) {
                this.selectedSketchGeometries.add(sketchGeometryList.get(i));
            }
        }
        this.refreshOverlay();
    }

    public void Select(Rect rect) {
        this.clearSelection(false);

        SelectResultList selectResultList = this.identifySelections(rect, false);
        if (selectResultList != null) {
            for (int i = 0; i < selectResultList.size(); i++) {
                this.selectedResults.add(selectResultList.get(i));
            }
        }

        SketchGeometryList sketchGeometryList = this.getSketchGeometrys().select(rect, -1);
        if (sketchGeometryList != null) {
            for (int i = 0; i < sketchGeometryList.size(); i++) {
                this.selectedSketchGeometries.add(sketchGeometryList.get(i));
            }
        }
        this.refreshOverlay();
    }



    public SelectResultList identifySelections(Dot point, double dTol, boolean containsSketch) {
        return identifySelections(point, dTol, -1, true, true, containsSketch);
    }

    public SelectResultList identifySelections(Rect rect, boolean containsSketch) {
        return identifySelections(rect, -1, true, true, containsSketch);
    }

    public SelectResultList identifySelections(Dot point, double dTol, int maxinumResults, boolean withAtt, boolean withSymbol, boolean containsSketch) {
        SelectResultList selectResultList = new SelectResultList();
        MapControlNative.jni_IdentifySelectionsByDot(mapControlNative, selectResultList.getHandle(), withAtt ? 1 : 0, withSymbol ? 1 : 0, point, dTol, containsSketch, maxinumResults);
        return selectResultList;
    }

    public SelectResultList identifySelections(Rect rect, int maxinumResults, boolean withAtt, boolean withSymbol, boolean containsSketch) {
        SelectResultList selectResultList = new SelectResultList();
        MapControlNative.jni_IdentifySelectionsByRect(mapControlNative, selectResultList.getHandle(), withAtt ? 1 : 0, withSymbol ? 1 : 0, rect, containsSketch, maxinumResults);
        return selectResultList;
    }

    /**
     * 是否有选中的整个图元用于做移动、删除等编辑
     *
     * @return
     */
    public boolean hasSelectedItemsToEdit() {
        boolean hasSelected = false;
        if (this.isEditing() && this.getEditType().equals(EditType.SELECT)) {
            hasSelected = this.getSelectedResults().size() > 0 || this.getSelectedSketchGeometries().size() > 0;
        }
        return hasSelected;
    }

    //endregion

    //region 编辑

    /**
     * 获取MapControl的编辑标志
     *
     * @return
     */
    public boolean isEditing() {
        return editing.get();
    }

    public BooleanProperty editingProperty() {
        return editing;
    }

    /**
     * 设置MapControl的编辑状态
     *
     * @param isEditing
     */
    public void setEditing(boolean isEditing) {
        this.editing.set(isEditing);
    }

    public EditType getEditType() {
        return editType.get();
    }

    public ObjectProperty<EditType> editTypeProperty() {
        return editType;
    }

    public void setEditType(EditType editType) {
        this.editType.set(editType);
    }

    public void setSketchEditor(SketchEditor sketchEditor) {
        if (this.sketchEditor != null) {
            this.sketchEditor.stop();
            this.sketchEditor.setMapControl(null);
        }

        this.sketchEditor = sketchEditor;
        if (sketchEditor != null) {
            sketchEditor.setMapControl(this);
        }
    }

    public SketchEditor getSketchEditor() {
        return this.sketchEditor;
    }

    public void clearTool() {
        MapControlNative.jni_ClearIATool(mapControlNative);
    }

    public void refreshOverlay() {
        MapControlNative.jni_OnRefreshOverlay(mapControlNative);
    }

    public GraphicsOverlay getSketchGraphicsOverlay() {
        GraphicsOverlay graphicsOverlay = null;
        long handle = MapControlNative.jni_GetSketchGraphicsOverlay(mapControlNative);
        if (handle != 0L) {
            graphicsOverlay = new GraphicsOverlay(handle);
            graphicsOverlay.setIsDisposable(false);
        }
        return graphicsOverlay;
    }

    public int setSketchGraphicsOverlay(GraphicsOverlay graphicsOverlay) {
        if (graphicsOverlay != null) {
            int rtn = MapControlNative.jni_SetSketchGraphicsOverlay(mapControlNative, graphicsOverlay.getHandle());
            if (rtn > 0) {
                graphicsOverlay.setIsDisposable(false);
            }
            return rtn;
        } else {
            String rtn = InternalResource.loadString("Append", "Handle_ObjectHasBeenDisposed", "data_resources");
            throw new IllegalStateException(rtn);
        }
    }

    public SketchGeometryList getSketchGeometrys() {
        SketchGeometryList sketchGeometryList = null;
        long handle = MapControlNative.jni_GetSketchGeometrys(mapControlNative);
        if (handle != 0L) {
            sketchGeometryList = new SketchGeometryList(handle);
            sketchGeometryList.setIsDisposable(false);
        }
        return sketchGeometryList;
    }
    //endregion

    //region 编辑交互
    public BooleanProperty enableKeyboardNavigationProperty() {
        return this.enableKeyboardNavigationProperty;
    }

    public void setEnableKeyboardNavigation(boolean enable) {
        enableKeyboardNavigationProperty().set(enable);
    }

    public boolean isEnableKeyboardNavigation() {
        return enableKeyboardNavigationProperty().get();
    }

    public BooleanProperty enableMouseZoomProperty() {
        return this.enableMouseZoomProperty;
    }

    public void setEnableMouseZoom(boolean enable) {
        this.enableMouseZoomProperty().set(enable);
    }

    public boolean isEnableMouseZoom() {
        return this.enableMouseZoomProperty().get();
    }

    public double getAdjustedX(double x) {
        double adjustedX = x;
        switch (this.getEffectiveNodeOrientation()) {
            case LEFT_TO_RIGHT:
                adjustedX = x - this.getInsets().getLeft();
                break;
            case RIGHT_TO_LEFT:
                adjustedX = this.getWidth() - this.getInsets().getRight() - x;
        }

        return adjustedX;
    }

    public double getAdjustedY(double y) {
        return y - this.getInsets().getTop();
    }

    public void setInteractionListener(InteractionListener interactionListener, ContextMenu contextMenu) {
        this.setContextMenu(contextMenu);

        Check.throwIfNull(interactionListener, "interactionListener");
        if (this.interactionListener != null) {
            this.interactionListener.onRemoved();
        }
        this.interactionListener = interactionListener;

        Objects.requireNonNull(this.interactionListener);
        this.setOnMousePressed(this.interactionListener::onMousePressed);
        this.setOnMouseClicked(this.interactionListener::onMouseClicked);
        this.setOnMouseDragged(this.interactionListener::onMouseDragged);
        this.setOnMouseReleased(this.interactionListener::onMouseReleased);
        this.setOnMouseMoved(this.interactionListener::onMouseMoved);
//        this.setOnMouseEntered(this.interactionListener::onMouseEntered);
//        this.setOnMouseExited(this.interactionListener::onMouseExited);
        this.setOnKeyPressed(this.interactionListener::onKeyPressed);
        this.setOnKeyReleased(this.interactionListener::onKeyReleased);
        this.setOnKeyTyped(this.interactionListener::onKeyTyped);
//        this.setOnZoom(this.interactionListener::onZoom);
//        this.setOnRotate(this.interactionListener::onRotate);
        this.setOnScroll(this.interactionListener::onScroll);
//        this.setOnRotationStarted(this.interactionListener::onRotationStarted);
//        this.setOnRotationFinished(this.interactionListener::onRotationFinished);
        this.setOnScrollStarted(this.interactionListener::onScrollStarted);
        this.setOnScrollFinished(this.interactionListener::onScrollFinished);
//        this.setOnZoomStarted(this.interactionListener::onZoomStarted);
//        this.setOnZoomFinished(this.interactionListener::onZoomFinished);
        this.interactionListener.onAdded();
    }

    public InteractionListener getInteractionListener() {
        return this.interactionListener;
    }

    public static class DefaultInteractionListener implements InteractionListener {
        private final MapControl mapControl;
        protected double[] lastDragPoint = null;
        private final AtomicReference<KeyCode> currentKey = new AtomicReference();
        private final ChangeListener<Boolean> focusListener;
        private final AnimationTimer continuousKeyAnimator = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (DefaultInteractionListener.this.currentKey.get() != null) {
                    switch (DefaultInteractionListener.this.currentKey.get()) {
                        case LEFT:
//                            DefaultInteractionListener.this.mapControl.getImpl().interactionDrag(20.0D, 0.0D);
                            break;
                        case RIGHT:
//                            DefaultInteractionListener.this.mapControl.getImpl().interactionDrag(-20.0D, 0.0D);
                            break;
                        case UP:
//                            DefaultInteractionListener.this.mapControl.getImpl().interactionDrag(0.0D, 20.0D);
                            break;
                        case DOWN:
//                            DefaultInteractionListener.this.mapControl.getImpl().interactionDrag(0.0D, -20.0D);
                            break;
                        case A:
//                            DefaultInteractionListener.this.mapControl.getImpl().interactionDrag(20.0D, 0.0D);
                            break;
                        case D:
//                            DefaultInteractionListener.this.mapControl.getImpl().interactionDrag(-20.0D, 0.0D);
                            break;
                        case W:
//                            DefaultInteractionListener.this.mapControl.getImpl().interactionDrag(0.0D, 20.0D);
                            break;
                        case S:
//                            DefaultInteractionListener.this.mapControl.getImpl().interactionDrag(0.0D, -20.0D);
                            break;
                        default:
                            this.stop();
                    }
                }
            }
        };

        private double zoomRate = 1;
        private Image zoomImage = null;
        private double zoomX = 0;
        private double zoomY = 0;
        private Image moveImage = null;
        private double mouseX = 0;
        private double mouseY = 0;

        protected DefaultInteractionListener(MapControl mapControl) {
            this.mapControl = mapControl;
            this.mapControl.enableKeyboardNavigationProperty().addListener((observable, oldValue, newValue) -> {
                this.currentKey.set(null);
                this.continuousKeyAnimator.stop();
            });
            this.focusListener = (observable, oldValue, newValue) -> {
                if (!newValue) {
//                    this.mapControl.getImpl().setInteracting(false);
                    this.currentKey.set(null);
                    this.continuousKeyAnimator.stop();
                }
            };
            this.mapControl.focusedProperty().addListener(this.focusListener);
        }

        @Override
        public void onRemoved() {
            this.mapControl.focusedProperty().removeListener(this.focusListener);
        }

        @Override
        public void onMousePressed(MouseEvent e) {
//            this.mapControl.getImpl().setInteracting(true);
//            e.consume();

            this.mouseX = e.getX();
            this.mouseY = e.getY();
            MouseButton mb = e.getButton();
            if (mb == MouseButton.PRIMARY) {
                if (this.mapControl.getWinOpType() == ToolType.MOVE) {
                    this.mapControl.setCursor(Cursor.CLOSED_HAND);
                    try {
                        this.moveImage = this.mapControl.snapshot(new SnapshotParameters(), null);
                    } catch (Exception ee) {
                        this.moveImage = null;
                    }
                }
                MapControlNative.jni_OnLButtonDown(this.mapControl.getNativeHandle(), 1, (int) e.getX(), (int) e.getY());
            } else if (mb == MouseButton.MIDDLE) {
                MapControlNative.jni_OnMButtonDown(this.mapControl.getNativeHandle(), 16, (int) e.getX(), (int) e.getY());
            } else if (mb == MouseButton.SECONDARY) {
                MapControlNative.jni_OnRButtonDown(this.mapControl.getNativeHandle(), 2, (int) e.getX(), (int) e.getY());
            }

            e.consume();
        }

        @Override
        public void onMouseReleased(MouseEvent e) {
////            this.mapControl.getImpl().setInteracting(false);
//            this.lastDragPoint = null;
//            e.consume();

            MouseButton mb = e.getButton();
            if (mb == MouseButton.PRIMARY) {
                if (this.mapControl.getWinOpType() == ToolType.MOVE) {
                    this.mapControl.setCursor(Cursor.OPEN_HAND);
                }
                MapControlNative.jni_OnLButtonUp(this.mapControl.getNativeHandle(), 0, (int) e.getX(), (int) e.getY());
            } else if (mb == MouseButton.MIDDLE) {
                MapControlNative.jni_OnMButtonUp(this.mapControl.getNativeHandle(), 0, (int) e.getX(), (int) e.getY());
            } else if (mb == MouseButton.SECONDARY) {
                //_contextMenu.show(this, e.getScreenX(), e.getScreenY());
                MapControlNative.jni_OnRButtonUp(this.mapControl.getNativeHandle(), 0, (int) e.getX(), (int) e.getY());
            }
            this.moveImage = null;
            this.mapControl.getZoomRectangle().setVisible(false);

            e.consume();
        }

        @Override
        public void onMouseClicked(MouseEvent e) {
            MouseButton mb = e.getButton();
            if (mb == MouseButton.PRIMARY) {
                if (e.getClickCount() == 2) {
                    MapControlNative.jni_OnLButtonDblClk(this.mapControl.getNativeHandle(), 1, (int) e.getX(), (int) e.getY());
                }
            } else if (mb == MouseButton.MIDDLE) {
                if (e.getClickCount() == 2) {
                    MapControlNative.jni_OnMButtonDblClk(this.mapControl.getNativeHandle(), 16, (int) e.getX(), (int) e.getY());
                }
            }
            e.consume();
        }

        @Override
        public void onMouseMoved(MouseEvent event) {
            MapControlNative.jni_OnMouseMove(this.mapControl.getNativeHandle(), 0, (int) event.getX(), (int) event.getY());
            event.consume();
        }

        @Override
        public void onMouseDragged(MouseEvent e) {
//            if (!e.isSynthesized() && e.isPrimaryButtonDown()) {
//                double[] point = new double[]{this.mapControl.getAdjustedX(e.getX()), this.mapControl.getAdjustedY(e.getY())};
//                if (this.lastDragPoint == null) {
//                    this.lastDragPoint = point;
//                } else {
//                    double deltaX = point[0] - this.lastDragPoint[0];
//                    double deltaY = point[1] - this.lastDragPoint[1];
//                    this.lastDragPoint = new double[]{point[0], point[1]};
////                    this.mapControl.getImpl().interactionDrag(deltaX, deltaY);
//                }
//            }
//
//            e.consume();

            MouseButton mb = e.getButton();
            if (mb == MouseButton.PRIMARY) {
                if (this.mapControl.getWinOpType() == ToolType.MOVE && this.moveImage != null) {
                    this.mapControl.setBackground(new Background(
                            new BackgroundFill[]{new BackgroundFill(Color.WHITE, null, null)},
                            new BackgroundImage[]{new BackgroundImage(this.moveImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, new BackgroundPosition(Side.LEFT, e.getX() - this.mouseX, false, Side.TOP, e.getY() - this.mouseY, false), null)}
                    ));
                } else if (this.mapControl.getWinOpType() == ToolType.ZOOMIN || this.mapControl.getWinOpType() == ToolType.ZOOMOUT) {
                    GraphicsContext gcc = this.mapControl.getZoomRectangle().getGraphicsContext2D();
                    gcc.clearRect(0, 0, this.mapControl.getZoomRectangle().getWidth(), this.mapControl.getZoomRectangle().getHeight());
                    gcc.fillRect(Math.min(e.getX(), this.mouseX), Math.min(e.getY(), this.mouseY), Math.abs(e.getX() - this.mouseX), Math.abs(e.getY() - this.mouseY));
                    gcc.strokeRect(Math.min(e.getX(), this.mouseX), Math.min(e.getY(), this.mouseY), Math.abs(e.getX() - this.mouseX), Math.abs(e.getY() - this.mouseY));
                    this.mapControl.getZoomRectangle().setVisible(true);
                }
                MapControlNative.jni_OnMouseMove(this.mapControl.getNativeHandle(), 1, (int) e.getX(), (int) e.getY());
            } else if (mb == MouseButton.MIDDLE) {
                MapControlNative.jni_OnMouseMove(this.mapControl.getNativeHandle(), 16, (int) e.getX(), (int) e.getY());
            } else if (mb == MouseButton.SECONDARY) {
                MapControlNative.jni_OnMouseMove(this.mapControl.getNativeHandle(), 2, (int) e.getX(), (int) e.getY());
            }
            e.consume();
        }

        @Override
        public void onScroll(ScrollEvent e) {
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(400), event -> {
                this.zoomRate = 1;
                this.zoomImage = null;
                this.zoomX = 0;
                this.zoomY = 0;
                this.mapControl.refreshWnd();
            }));

            if (e.getDeltaY() == 0) {
                return;
            }

            if (timeline.getStatus() != Animation.Status.STOPPED) {
                timeline.stop();
            }
            timeline.play();
            //绘制缩位图
            double zoomRate = 0;
            if (e.getDeltaY() > 0) {
                zoomRate = 1.25;
            } else {
                zoomRate = 0.8;
            }
            this.zoomRate *= zoomRate;
            //获取屏幕坐标
            try {
                Bounds b = this.mapControl.getBoundsInLocal();
                Point2D p = this.mapControl.localToScreen(b.getMinX(), b.getMinY());
                int x = (int) Math.round(p.getX());
                int y = (int) Math.round(p.getY());
                int w = (int) Math.round(b.getWidth());
                int h = (int) Math.round(b.getHeight());
                if (this.zoomImage == null) {
                    this.zoomImage = this.mapControl.snapshot(new SnapshotParameters(), null);
                }
                this.zoomX = e.getX() - (e.getX() - this.zoomX) * zoomRate;
                this.zoomY = e.getY() - (e.getY() - this.zoomY) * zoomRate;
                if (w * this.zoomRate < 1 || h * this.zoomRate < 1) {
                    this.mapControl.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
                } else {
                    if (this.zoomRate >= 1) {
                        double srcW = b.getWidth() / this.zoomRate;
                        double srcH = b.getHeight() / this.zoomRate;
                        double srcX = Math.abs(this.zoomX) / this.zoomRate;
                        double srcY = Math.abs(this.zoomY) / this.zoomRate;
                        if ((int) srcW > 0 && (int) srcH > 0) {
                            PixelReader pixelReader = this.zoomImage.getPixelReader();
                            WritableImage wImage = new WritableImage(
                                    (int) srcW,
                                    (int) srcH);
                            PixelWriter pixelWriter = wImage.getPixelWriter();
                            // Determine the color of each pixel in a specified row
                            for (int readY = 0; readY < (int) srcH; readY++) {
                                for (int readX = 0; readX < (int) srcW; readX++) {
                                    if (((int) srcX + readX) < (int) this.zoomImage.getWidth() && ((int) srcY + readY) < (int) this.zoomImage.getHeight()) {

                                        Color color = pixelReader.getColor((int) srcX + readX, (int) srcY + readY);
                                        // Now write a brighter color to the PixelWriter.
                                        pixelWriter.setColor(readX, readY, color);
                                    }
                                }
                            }
                            this.mapControl.setBackground(new Background(new BackgroundImage(wImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, new BackgroundPosition(Side.LEFT, 0, false, Side.TOP, 0, false), new BackgroundSize(b.getWidth(), b.getHeight(), false, false, false, false))));
                        }
                    } else {
                        this.mapControl.setBackground(new Background(
                                new BackgroundFill[]{new BackgroundFill(Color.WHITE, null, null)},
                                new BackgroundImage[]{new BackgroundImage(this.zoomImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, new BackgroundPosition(Side.LEFT, this.zoomX, false, Side.TOP, this.zoomY, false), new BackgroundSize(b.getWidth() * this.zoomRate, b.getHeight() * this.zoomRate, false, false, false, false))}
                        ));
                    }
                }
                MapControlNative.jni_OnMouseWheel(this.mapControl.getNativeHandle(), 0, (short) e.getDeltaY(), (int) e.getX(), (int) e.getY());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.consume();
        }

        @Override
        public void onKeyPressed(KeyEvent e) {
            MapControlNative.jni_OnKeyDown(this.mapControl.getNativeHandle(), e.getCode().impl_getCode(), 0, 0);
            e.consume();
        }

        @Override
        public void onKeyReleased(KeyEvent e) {
            MapControlNative.jni_OnKeyUp(this.mapControl.getNativeHandle(), e.getCode().impl_getCode(), 0, 0);
            e.consume();
        }

        @Override
        public void onKeyTyped(KeyEvent event) {
            event.consume();
        }

        @Override
        public void onScrollStarted(ScrollEvent event) {
//            this.mapControl.getImpl().setInteracting(true);
        }

        @Override
        public void onScrollFinished(ScrollEvent event) {
//            this.mapControl.getImpl().setInteracting(false);
        }
    }
    //endregion

    static {
        com.zondy.mapgis.geomap.inner.Environment.loadLib();
    }
}
