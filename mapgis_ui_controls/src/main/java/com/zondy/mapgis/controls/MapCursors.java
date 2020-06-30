package com.zondy.mapgis.controls;

import com.sun.javafx.cursor.CursorType;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;

/**
 * @author CR
 * @file MapCursors.java
 * @brief 地图控件中用到的自定义光标
 * @create 2020-06-01.
 */
public class MapCursors {
    /**
     * 箭头光标（黑色不带柄的箭头），多用于选择
     */
    public static final ImageCursor ARROW = new ImageCursor(new Image(MapCursors.class.getResourceAsStream("Png_CursorArrow_32.png")));
    /**
     * 移动图元光标（选中箭头加移动图标）：多用于移动选中的图元
     */
    public static final ImageCursor MOVEITEM = new ImageCursor(new Image(MapCursors.class.getResourceAsStream("Png_CursorTopoEdit_32.png")), 7, 7);
    /**
     * 拓扑编辑光标（空心黑边框不带柄的箭头）：多用于选完直接拓扑处理
     */
    public static final ImageCursor TOPOEDIT = new ImageCursor(new Image(MapCursors.class.getResourceAsStream("Png_CursorTopoEdit_32.png")), 7, 7);
    /**
     * 十字光标（十字形状，比Cursor.CROSSHAIR细一些）
     */
    public static final ImageCursor CROSS = new ImageCursor(new Image(MapCursors.class.getResourceAsStream("Png_CursorCross_32.png")), 7, 7);
    /**
     * 十字光标（小十字形状，比Cursor.CROSSHAIR小）
     */
    public static final ImageCursor CROSSMINI = new ImageCursor(new Image(MapCursors.class.getResourceAsStream("Png_CursorCrossMini_32.png")), 7, 7);
    /**
     * 格式刷光标（格式刷+小十字）
     */
    public static final ImageCursor BRUSH = new ImageCursor(new Image(MapCursors.class.getResourceAsStream("Png_CursorBrush_32.png")), 7, 14);
    /**
     * 注记文本编辑光标（空心箭头+I）
     */
    public static final ImageCursor ANNEDIT = new ImageCursor(new Image(MapCursors.class.getResourceAsStream("Png_CursorAnnEdit_32.png")), 0, 1);
    /**
     * 旋转光标
     */
    public static final ImageCursor ROTATE = new ImageCursor(new Image(MapCursors.class.getResourceAsStream("Png_CursorRotate_32.png")), 10, 14);
    /**
     * 放大
     */
    public static final ImageCursor ZOOMIN = new ImageCursor(new Image(MapCursors.class.getResourceAsStream("Png_CursorZoomIn_32.png")), 5, 5);
    /**
     * 缩小
     */
    public static final ImageCursor ZOOMOUT = new ImageCursor(new Image(MapCursors.class.getResourceAsStream("Png_CursorZoomOut_32.png")), 5, 5);
    /**
     * 移动（握紧的手图标）
     */
    public static final ImageCursor CLOSEDHANDMOVE = new ImageCursor(new Image(MapCursors.class.getResourceAsStream("Png_CursorClosedHandMove_32.png")), 7, 0);
    /**
     * 移动（张开的手图标）
     */
    public static final ImageCursor OPENEDHANDMOVE = new ImageCursor(new Image(MapCursors.class.getResourceAsStream("Png_CursorOpenedHandMove_32.png")), 7, 0);
    /**
     * 图元变换光标（中间一个点，四个方向有黑色小箭头）
     */
    public static final ImageCursor MOVE = new ImageCursor(new Image(MapCursors.class.getResourceAsStream("Png_CursorMove_32.png")), 9, 9);
    ///**
    // * 图元变换光标（中间一个点，四个方向有黑色小箭头）
    // */
    //public static final ImageCursor MOVEVERTEX = new ImageCursor(new Image(MapCursors.class.getResourceAsStream("Png_CursorMoveVertex_32.png")), 2, 4);
    ///**
    // * 图元变换光标（中间一个点，四个方向有黑色小箭头）
    // */
    //public static final ImageCursor ADDVERTEX = new ImageCursor(new Image(MapCursors.class.getResourceAsStream("Png_CursorAddVertex_32.png")), 2, 4);
    ///**
    // * 图元变换光标（中间一个点，四个方向有黑色小箭头）
    // */
    //public static final ImageCursor DELETEVERTEX = new ImageCursor(new Image(MapCursors.class.getResourceAsStream("Png_CursorDeleteVertex_32.png")), 2, 4);
    ///**
    // * 移动顶点光标（中间一个方框，四个方向有黑色小箭头）：多用于移动顶点，如线上点、区边界点
    // */
    //public static final ImageCursor MOVINGVERTEX = new ImageCursor(new Image(MapCursors.class.getResourceAsStream("Png_CursorMoveVertex1_32.png")), 7, 7);
/**
     * 图元变换光标（中间一个点，四个方向有黑色小箭头）
     */
    public static final ImageCursor EDITVERTEX = new ImageCursor(new Image(MapCursors.class.getResourceAsStream("Png_CursorEditVertex_32.png")), 2, 4);
}
