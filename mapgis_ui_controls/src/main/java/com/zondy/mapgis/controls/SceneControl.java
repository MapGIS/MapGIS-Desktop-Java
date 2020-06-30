package com.zondy.mapgis.controls;

import com.zondy.mapgis.controls.skin.SceneControlSkin;
import javafx.animation.AnimationTimer;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.scene.input.*;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import com.zondy.mapgis.geometry.Rect3D;
import com.zondy.mapgis.scene.Camera;
import com.zondy.mapgis.scene.SceneMode;
import javafx.scene.control.Control;

/**
 * 场景视图控件
 *
 * @author cxy
 * @date 2019/12/04
 */
public class SceneControl extends Control {
    private long mSceneControlNative = 0;
    private final SimpleDoubleProperty mAttributionTopProperty = new SimpleDoubleProperty();
    private final SimpleObjectProperty<Insets> mViewInsetsProperty = new SimpleObjectProperty<>(Insets.EMPTY);
    private final SimpleBooleanProperty mAttributionTextVisibleProperty = new SimpleBooleanProperty(true);
    private final SimpleBooleanProperty mEnableKeyboardNavigationProperty = new SimpleBooleanProperty(true);
    private InteractionListener mInteractionListener;
    private final SimpleBooleanProperty mEnableTouchPanProperty = new SimpleBooleanProperty(true);
    private final SimpleBooleanProperty mEnableMousePanProperty = new SimpleBooleanProperty(true);
    private final SimpleBooleanProperty mEnableMouseZoomProperty = new SimpleBooleanProperty(true);
    private final SimpleBooleanProperty mEnableTouchZoomProperty = new SimpleBooleanProperty(true);
    private final SimpleBooleanProperty mEnableTouchRotateProperty = new SimpleBooleanProperty(false);

    private static void throwIfNull(Object value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(String.format("Parameter %s must not be null", new Object[]{name}));
        }
    }

    public SceneControl() {
        mSceneControlNative = SceneControlNative.jni_CreateObj(this);

        this.setPrefWidth(512);
        this.setPrefHeight(512);

        setFocusTraversable(true);
        enableTouchRotateProperty().set(true);

        setInteractionListener(new DefaultInteractionListener(this));
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        SceneControlSkin skin = new SceneControlSkin(this);
        this.mAttributionTopProperty.bind(skin.attributionTopProperty());
        return (Skin<?>) skin;
    }

    public long getNativeHandle() {

        return mSceneControlNative;
    }

    public com.zondy.mapgis.scene.Scene getMapGISScene() {
        long sceneHandle = SceneControlNative.jni_GetScene(this.mSceneControlNative);
        return (sceneHandle == 0) ? null : new com.zondy.mapgis.scene.Scene(sceneHandle);
    }

    public void setMapGISScene(com.zondy.mapgis.scene.Scene scene) {
        SceneControlNative.jni_SetScene(this.mSceneControlNative, scene == null ? 0 : scene.getHandle());
    }

    public boolean isViewInsetsValid() {
        return true;
    }

    public String getAttributionText() {
        return "";
    }

    public void addAttributionTextChangedListener(AttributionTextChangedListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener must not be null");
        }
        //this.mAttributionTextChangedRunners.add(new AttributionTextChangedListenerRunnerImpl(listener));
    }

    public BooleanProperty attributionTextVisibleProperty() {
        return this.mAttributionTextVisibleProperty;
    }

    public ObjectProperty<Insets> viewInsetsProperty() {
        return this.mViewInsetsProperty;
    }

    public void setInteractionListener(InteractionListener interactionListener) {
        throwIfNull(interactionListener, "interactionListener");

        if (this.mInteractionListener != null) {
            this.mInteractionListener.onRemoved();
        }
        this.mInteractionListener = interactionListener;

        Objects.requireNonNull(this.mInteractionListener);
        setOnMousePressed(this.mInteractionListener::onMousePressed);
        Objects.requireNonNull(this.mInteractionListener);
        setOnMouseClicked(this.mInteractionListener::onMouseClicked);
        Objects.requireNonNull(this.mInteractionListener);
        setOnMouseDragged(this.mInteractionListener::onMouseDragged);
        Objects.requireNonNull(this.mInteractionListener);
        setOnMouseReleased(this.mInteractionListener::onMouseReleased);
        Objects.requireNonNull(this.mInteractionListener);
        setOnMouseMoved(this.mInteractionListener::onMouseMoved);
        Objects.requireNonNull(this.mInteractionListener);
        setOnMouseEntered(this.mInteractionListener::onMouseEntered);
        Objects.requireNonNull(this.mInteractionListener);
        setOnMouseExited(this.mInteractionListener::onMouseExited);

        Objects.requireNonNull(this.mInteractionListener);
        setOnKeyPressed(this.mInteractionListener::onKeyPressed);
        Objects.requireNonNull(this.mInteractionListener);
        setOnKeyReleased(this.mInteractionListener::onKeyReleased);
        Objects.requireNonNull(this.mInteractionListener);
        setOnKeyTyped(this.mInteractionListener::onKeyTyped);

        Objects.requireNonNull(this.mInteractionListener);
        setOnTouchMoved(this.mInteractionListener::onTouchMoved);
        Objects.requireNonNull(this.mInteractionListener);
        setOnTouchPressed(this.mInteractionListener::onTouchPressed);
        Objects.requireNonNull(this.mInteractionListener);
        setOnTouchReleased(this.mInteractionListener::onTouchReleased);
        Objects.requireNonNull(this.mInteractionListener);
        setOnTouchStationary(this.mInteractionListener::onTouchStationary);

        Objects.requireNonNull(this.mInteractionListener);
        setOnSwipeDown(this.mInteractionListener::onSwipeDown);
        Objects.requireNonNull(this.mInteractionListener);
        setOnSwipeLeft(this.mInteractionListener::onSwipeLeft);
        Objects.requireNonNull(this.mInteractionListener);
        setOnSwipeRight(this.mInteractionListener::onSwipeRight);
        Objects.requireNonNull(this.mInteractionListener);
        setOnSwipeUp(this.mInteractionListener::onSwipeUp);
        Objects.requireNonNull(this.mInteractionListener);
        setOnZoom(this.mInteractionListener::onZoom);
        Objects.requireNonNull(this.mInteractionListener);
        setOnRotate(this.mInteractionListener::onRotate);
        Objects.requireNonNull(this.mInteractionListener);
        setOnScroll(this.mInteractionListener::onScroll);

        Objects.requireNonNull(this.mInteractionListener);
        setOnRotationStarted(this.mInteractionListener::onRotationStarted);
        Objects.requireNonNull(this.mInteractionListener);
        setOnRotationFinished(this.mInteractionListener::onRotationFinished);
        Objects.requireNonNull(this.mInteractionListener);
        setOnScrollStarted(this.mInteractionListener::onScrollStarted);
        Objects.requireNonNull(this.mInteractionListener);
        setOnScrollFinished(this.mInteractionListener::onScrollFinished);
        Objects.requireNonNull(this.mInteractionListener);
        setOnZoomStarted(this.mInteractionListener::onZoomStarted);
        Objects.requireNonNull(this.mInteractionListener);
        setOnZoomFinished(this.mInteractionListener::onZoomFinished);

        this.mInteractionListener.onAdded();
    }

    public BooleanProperty enableTouchPanProperty() {
        return this.mEnableTouchPanProperty;
    }

    public void setEnableTouchPan(boolean enable) {
        enableTouchPanProperty().set(enable);
    }

    public boolean isEnableTouchPan() {
        return enableTouchPanProperty().get();
    }

    public BooleanProperty enableMouseZoomProperty() {
        return this.mEnableMouseZoomProperty;
    }

    public void setEnableMouseZoom(boolean enable) {
        enableMouseZoomProperty().set(enable);
    }

    public boolean isEnableMouseZoom() {
        return enableMouseZoomProperty().get();
    }

    public BooleanProperty enableMousePanProperty() {
        return this.mEnableMousePanProperty;
    }

    public void setEnableMousePan(boolean enable) {
        enableMousePanProperty().set(enable);
    }

    public boolean isEnableMousePan() {
        return enableMousePanProperty().get();
    }

    public BooleanProperty enableKeyboardNavigationProperty() {
        return this.mEnableKeyboardNavigationProperty;
    }

    public void setEnableKeyboardNavigation(boolean enable) {
        enableKeyboardNavigationProperty().set(enable);
    }

    public boolean isEnableKeyboardNavigation() {
        return enableKeyboardNavigationProperty().get();
    }

    public BooleanProperty enableTouchZoomProperty() {
        return this.mEnableTouchZoomProperty;
    }

    public void setEnableTouchZoom(boolean enable) {
        enableTouchZoomProperty().set(enable);
    }

    public boolean isEnableTouchZoom() {
        return enableTouchZoomProperty().get();
    }


    public BooleanProperty enableTouchRotateProperty() {
        return this.mEnableTouchRotateProperty;
    }

    public void setEnableTouchRotate(boolean enable) {
        enableTouchRotateProperty().set(enable);
    }

    public boolean isEnableTouchRotate() {
        return enableTouchRotateProperty().get();
    }

    public double getAdjustedX(double x) {
        double adjustedX = x;
        switch (getEffectiveNodeOrientation()) {
            case LEFT_TO_RIGHT:
                adjustedX -= getInsets().getLeft();
                break;
            case RIGHT_TO_LEFT:
                adjustedX = getWidth() - getInsets().getRight() - adjustedX;
                break;
        }

        return adjustedX;
    }

    public double getAdjustedY(double y) {
        return y - getInsets().getTop();
    }

    double[] getAdjustedScreenPoint(Point2D screenPoint) {
        return new double[]{getAdjustedX(screenPoint.getX()), getAdjustedY(screenPoint.getY())};
    }

    /**
     * 获取场景绘制模式
     *
     * @return 场景绘制模式
     */
    public SceneMode getSceneMode() {
        return SceneMode.GLOBE;
    }

    /**
     * 设置场景相机属性
     *
     * @param camera 相机对象
     */
    public void getCamera(Camera camera) {

    }

    /**
     * 设置视图显示范围
     *
     * @param rect3D 范围
     */
    public void setViewRect(Rect3D rect3D) {
    }

    /**
     * 笛卡尔坐标到地理坐标转换
     *
     * @param x X 坐标
     * @param y X 坐标
     * @param z X 坐标
     * @return X, Y, Z 坐标
     */
    public double[] cartesianToGeodetic(double x, double y, double z) {
        return new double[]{0, 0, 0};
    }

    /**
     * 设置视图参数信息
     *
     * @param x       X 坐标
     * @param y       Y 坐标
     * @param dist    视点距离
     * @param heading 方位角
     * @param tilt    高度角
     * @param play    自动跳转
     */
    public void setViewPos(double x, double y, double dist, double heading, double tilt, boolean play) {
    }

    /**
     * 复位
     */
    public void reset() {

    }

    static {
        com.zondy.mapgis.geomap.inner.Environment.loadLib();
    }

    public static class DefaultInteractionListener implements InteractionListener {
        private final SceneControl mSceneView;
        protected double[] mLastDragPoint = null;
        protected boolean mPanning = false;
        private static final float ANIMATION_DURATION = 0.25F;
        private final AtomicReference<KeyCode> mCurrentKey = new AtomicReference<>();
        private final ChangeListener<Boolean> mFocusListener;
        boolean mStartedGesture = false;
        AnimationTimer mContinuousKeyAnimator = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (SceneControl.DefaultInteractionListener.this.mCurrentKey.get() != null) {
                    switch ((KeyCode) SceneControl.DefaultInteractionListener.this.mCurrentKey.get()) {
                        case LEFT:
                            //SceneControl.DefaultInteractionListener.this.mSceneView.getImpl().interactionUpdatePanOrigin(20.0D, 0.0D);
                            return;
                        case RIGHT:
                            //SceneControl.DefaultInteractionListener.this.mSceneView.getImpl().interactionUpdatePanOrigin(-20.0D, 0.0D);
                            return;
                        case UP:
                            //SceneControl.DefaultInteractionListener.this.mSceneView.getImpl().interactionUpdatePanOrigin(0.0D, 20.0D);
                            return;
                        case DOWN:
                            //SceneControl.DefaultInteractionListener.this.mSceneView.getImpl().interactionUpdatePanOrigin(0.0D, -20.0D);
                            return;
                        case A:
                            SceneControl.DefaultInteractionListener.this.handleKeyboardRotate(-1.0D);
                            return;
                        case D:
                            SceneControl.DefaultInteractionListener.this.handleKeyboardRotate(1.0D);
                            return;
                        case W:
                            SceneControl.DefaultInteractionListener.this.handleKeyboardPitch(1.0D);
                            return;
                        case S:
                            SceneControl.DefaultInteractionListener.this.handleKeyboardPitch(-1.0D);
                            return;
                        case U:
                            SceneControl.DefaultInteractionListener.this.handleKeyboardElevate(1.1D);
                            return;
                        case J:
                            SceneControl.DefaultInteractionListener.this.handleKeyboardElevate(0.9090909090909091D);
                            return;
                    }
                    stop();
                }
            }
        };

        protected DefaultInteractionListener(SceneControl sceneView) {
            this.mSceneView = sceneView;

            sceneView.enableKeyboardNavigationProperty().addListener((observable, oldValue, newValue) -> {
                this.mCurrentKey.set(null);
                this.mContinuousKeyAnimator.stop();
            });

            this.mFocusListener = ((observable, oldValue, newValue) -> {
                if (!newValue.booleanValue()) {
                    //this.mSceneView.getImpl().setInteracting(false);
                    this.mCurrentKey.set(null);
                    this.mContinuousKeyAnimator.stop();
                }
            });
            this.mSceneView.focusedProperty().addListener(this.mFocusListener);

            this.mSceneView.enableKeyboardNavigationProperty().addListener((observable, oldValue, newValue) -> {
                this.mCurrentKey.set(null);
                this.mContinuousKeyAnimator.stop();
            });
        }

        @Override
        public void onRemoved() {
            this.mSceneView.focusedProperty().removeListener(this.mFocusListener);
        }

        @Override
        public void onMousePressed(MouseEvent e) {
            //this.mSceneView.getImpl().setInteracting(true);
            //this.mSceneView.getImpl().setInteractionOrigin(new double[]{this.mSceneView.getAdjustedX(e.getX()), this.mSceneView.getAdjustedY(e.getY())});
            MouseButton mb = e.getButton();
            if (mb == MouseButton.PRIMARY) {
                SceneControlNative.jni_OnLButtonDown(this.mSceneView.getNativeHandle(), 1, (int) e.getX(), (int) e.getY());
            }
            e.consume();
        }

        @Override
        public void onMouseReleased(MouseEvent e) {
            //this.mSceneView.getImpl().setInteracting(false);
            this.mLastDragPoint = null;
            this.mStartedGesture = false;
            MouseButton mb = e.getButton();
            if (mb == MouseButton.PRIMARY) {
                SceneControlNative.jni_OnLButtonUp(this.mSceneView.getNativeHandle(), 0, (int) e.getX(), (int) e.getY());
            }
            e.consume();
        }

        @Override
        public void onMouseDragged(MouseEvent e) {
            if ((this.mSceneView.isEnableTouchPan() && e.isSynthesized()) || (this.mSceneView.isEnableMousePan() && !e.isSynthesized())) {
                this.mPanning = true;
                double[] point = {this.mSceneView.getAdjustedX(e.getX()), this.mSceneView.getAdjustedY(e.getY())};
                if (this.mLastDragPoint == null) {
                    this.mLastDragPoint = point;
                } else {
                    double deltaX = point[0] - this.mLastDragPoint[0];
                    double deltaY = point[1] - this.mLastDragPoint[1];

                    this.mLastDragPoint = new double[]{point[0], point[1]};

                    if (e.isPrimaryButtonDown()) {
                        //this.mSceneView.getImpl().interactionUpdatePanOrigin(deltaX, deltaY);
                        //
                        SceneControlNative.jni_OnMouseMove(this.mSceneView.getNativeHandle(), 1, (int) (e.getX()), (int) (e.getY()));
                        //SceneControlNative.jni_InterActionUpdatePanOrigin(this.mSceneView.getNativeHandle(), deltaX, deltaY);

                    } else if (e.isSecondaryButtonDown()) {
                        //this.mSceneView.getImpl().interactionUpdateRotateAroundOrigin(-deltaX * 360.0D / this.mSceneView.getWidth(), -deltaY * 360.0D / this.mSceneView.getHeight());
                    }
                }
            }
            e.consume();
        }

        @Override
        public void onScroll(ScrollEvent e) {
            if (e.isDirect()) {
                return;
            }
            if (this.mSceneView.isEnableMouseZoom()) {
                double deltaY = e.getDeltaY();
                double x = this.mSceneView.getAdjustedX(e.getX());
                double y = this.mSceneView.getAdjustedY(e.getY());

                if (deltaY > 0.0D) {
                    //this.mSceneView.getImpl().interactionZoomInAnimated(new double[]{x, y});
                }
                if (deltaY < 0.0D) {
                    //this.mSceneView.getImpl().interactionZoomOutAnimated(new double[]{x, y});
                }
                SceneControlNative.jni_OnMouseWheel(this.mSceneView.getNativeHandle(), 0, (short) e.getDeltaY(), (int) e.getX(), (int) e.getY());
            }
            e.consume();
        }

        @Override
        public void onKeyPressed(KeyEvent e) {
            if (this.mSceneView.isEnableKeyboardNavigation()) {
                double[] adjustedScreenPoint = {this.mSceneView.getAdjustedX(this.mSceneView.getWidth() / 2.0D), this.mSceneView.getAdjustedY(this.mSceneView.getHeight() / 2.0D)};

                if (this.mCurrentKey.get() == null) {
                    //Camera camera;
                    //this.mSceneView.getImpl().setInteracting(true);
                    //this.mSceneView.getImpl().setInteractionOrigin(adjustedScreenPoint);
                    this.mCurrentKey.set(e.getCode());
                    this.mContinuousKeyAnimator.start();


                    switch ((KeyCode) this.mCurrentKey.get()) {
                        case EQUALS:
                        case ADD:
                            //this.mSceneView.getImpl().interactionZoomInAnimated(adjustedScreenPoint);
                            break;
                        case MINUS:
                        case SUBTRACT:
                            //this.mSceneView.getImpl().interactionZoomOutAnimated(adjustedScreenPoint);
                            break;
                        case N:
                            //camera = this.mSceneView.getImpl().getCurrentViewpointCamera();
                            //camera = new Camera(camera.getLocation(), 0.0D, camera.getPitch(), camera.getRoll());
                            //this.mSceneView.setViewpointAsync(new Viewpoint(camera.getLocation(), 1.0D, camera), 0.25F);
                            break;
                        case P:
                            //camera = this.mSceneView.getImpl().getCurrentViewpointCamera();
                            //camera = new Camera(camera.getLocation(), camera.getHeading(), 0.0D, camera.getRoll());
                            //this.mSceneView.setViewpointAsync(new Viewpoint(camera.getLocation(), 1.0D, camera), 0.25F);
                            break;
                    }
                }
            }
            e.consume();
        }

        @Override
        public void onKeyReleased(KeyEvent e) {
            if (e.getCode() != this.mCurrentKey.get() || !this.mSceneView.isEnableKeyboardNavigation()) {
                return;
            }
            //this.mSceneView.getImpl().setInteracting(false);
            this.mCurrentKey.set(null);
            this.mContinuousKeyAnimator.stop();
            e.consume();
        }

        @Override
        public void onZoom(ZoomEvent e) {
            if (this.mSceneView.isEnableTouchZoom()) {
                if (!this.mStartedGesture) {
                    //this.mSceneView.getImpl().setInteractionOrigin(new double[]{this.mSceneView.getAdjustedX(e.getX()), this.mSceneView.getAdjustedY(e.getY())});
                    this.mStartedGesture = true;
                }
                //this.mSceneView.getImpl().interactionUpdateZoomToOrigin(e.getZoomFactor());
            }
            e.consume();
        }

        @Override
        public void onRotate(RotateEvent e) {
            if (this.mSceneView.isEnableTouchRotate()) {
                if (!this.mStartedGesture) {
                    //this.mSceneView.getImpl().setInteractionOrigin(new double[]{this.mSceneView.getAdjustedX(e.getX()), this.mSceneView.getAdjustedY(e.getY())});
                    this.mStartedGesture = true;
                }
                //this.mSceneView.getImpl().interactionUpdateRotateAroundOrigin(e.getAngle(), 0.0D);
            }
        }

        @Override
        public void onRotationStarted(RotateEvent event) {
            //this.mSceneView.getImpl().setInteracting(true);
        }

        @Override
        public void onRotationFinished(RotateEvent event) {
            //this.mSceneView.getImpl().setInteracting(false);
        }

        @Override
        public void onScrollStarted(ScrollEvent event) {
            //this.mSceneView.getImpl().setInteracting(true);
        }

        @Override
        public void onScrollFinished(ScrollEvent event) {
            //this.mSceneView.getImpl().setInteracting(false);
        }

        @Override
        public void onZoomStarted(ZoomEvent event) {
            //this.mSceneView.getImpl().setInteracting(true);
        }

        @Override
        public void onZoomFinished(ZoomEvent event) {
            //this.mSceneView.getImpl().setInteracting(false);
        }

        private void handleKeyboardRotate(double degreesChange) {
            //Camera camera = this.mSceneView.getCurrentViewpointCamera();

            //camera = new Camera(camera.getLocation(), camera.getHeading() + degreesChange, camera.getPitch(), camera.getRoll());
            //this.mSceneView.setViewpointCamera(camera);
        }

        private void handleKeyboardPitch(double degreesChange) {
//            Camera camera = this.mSceneView.getCurrentViewpointCamera();
//            double newPitch = camera.getPitch() + degreesChange;
//            if (newPitch >= 0.0D && newPitch <= 180.0D) {
//                camera = new Camera(camera.getLocation(), camera.getHeading(), newPitch, camera.getRoll());
//                this.mSceneView.setViewpointCamera(camera);
//            }
        }

        private void handleKeyboardElevate(double factor) {
//            Camera camera = this.mSceneView.getCurrentViewpointCamera();
//            Point location = camera.getLocation();
//            camera = camera.moveTo(new Point(location.getX(), location.getY(), location.getZ() * factor, location
//                    .getSpatialReference()));
//            this.mSceneView.setViewpointCamera(camera);
        }
    }
}

