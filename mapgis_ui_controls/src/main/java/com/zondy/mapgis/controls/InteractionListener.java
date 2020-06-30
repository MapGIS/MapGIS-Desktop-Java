package com.zondy.mapgis.controls;

/**
 * Created by Administrator on 2020/4/23.
 */
import javafx.scene.input.KeyEvent;
        import javafx.scene.input.MouseEvent;
        import javafx.scene.input.RotateEvent;
        import javafx.scene.input.ScrollEvent;
        import javafx.scene.input.SwipeEvent;
        import javafx.scene.input.TouchEvent;
        import javafx.scene.input.ZoomEvent;

public interface InteractionListener {
    default void onMouseClicked(MouseEvent event) {}

    default void onMouseDragged(MouseEvent event) {}

    default void onMousePressed(MouseEvent event) {}

    default void onMouseReleased(MouseEvent event) {}

    default void onMouseMoved(MouseEvent event) {}

    default void onTouchMoved(TouchEvent event) {}

    default void onTouchPressed(TouchEvent event) {}

    default void onTouchReleased(TouchEvent event) {}

    default void onTouchStationary(TouchEvent event) {}

    default void onScroll(ScrollEvent event) {}

    default void onKeyPressed(KeyEvent event) {}

    default void onKeyReleased(KeyEvent event) {}

    default void onKeyTyped(KeyEvent event) {}

    default void onZoom(ZoomEvent event) {}

    default void onRotate(RotateEvent event) {}

    default void onSwipeDown(SwipeEvent event) {}

    default void onSwipeLeft(SwipeEvent event) {}

    default void onSwipeRight(SwipeEvent event) {}

    default void onSwipeUp(SwipeEvent event) {}

    default void onAdded() {}

    default void onRemoved() {}

    default void onMouseEntered(MouseEvent event) {}

    default void onMouseExited(MouseEvent event) {}

    default void onScrollStarted(ScrollEvent event) {}

    default void onScrollFinished(ScrollEvent event) {}

    default void onZoomStarted(ZoomEvent event) {}

    default void onZoomFinished(ZoomEvent event) {}

    default void onRotationStarted(RotateEvent event) {}

    default void onRotationFinished(RotateEvent event) {}
}