package com.zondy.mapgis.workspace.event;

import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.map.Theme;
import com.zondy.mapgis.scene.Theme3D;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;

/**
 * 单击节点事件
 *
 * @author cxy
 * @date 2019/11/20
 */
public class ItemMouseClickEvent extends ItemEvent {
    private transient MouseButton mouseButton = MouseButton.NONE;
    private transient Point2D point2D;

    /**
     * 单击节点事件
     *
     * @param source         事件源
     * @param mouseButton    鼠标按键
     * @param point2D        鼠标点击坐标
     * @param documentItem   所属图层节点
     * @param theme          所属专题图节点
     * @param theme3D        所属三维专题图节点
     * @param themeInfoIndex 专题图子项ThemeInfo索引，如果该索引等于Theme或Theme3D子项个数，则表示为DefaultInfo（未分类信息）
     * @throws IllegalArgumentException if source is null.
     */
    public ItemMouseClickEvent(Object source, MouseButton mouseButton, Point2D point2D, DocumentItem documentItem, Theme theme, Theme3D theme3D, int themeInfoIndex) {
        super(source, documentItem, theme, theme3D, themeInfoIndex);
        this.mouseButton = mouseButton;
        this.point2D = point2D;
    }

    /**
     * 单击节点事件
     *
     * @param source         事件源
     * @param mouseButton    鼠标按键
     * @param point2D        鼠标点击坐标
     * @param documentItem   所属图层节点
     * @param theme          所属专题图节点
     * @param theme3D        所属三维专题图节点
     * @param themeInfoIndex 专题图子项ThemeInfo索引，如果该索引等于Theme或Theme3D子项个数，则表示为DefaultInfo（未分类信息）
     * @param data           自定义项挂接数据
     * @throws IllegalArgumentException if source is null.
     */
    public ItemMouseClickEvent(Object source, MouseButton mouseButton, Point2D point2D, DocumentItem documentItem, Theme theme, Theme3D theme3D, int themeInfoIndex, Object data) {
        super(source, documentItem, theme, theme3D, themeInfoIndex, data);
        this.mouseButton = mouseButton;
        this.point2D = point2D;
    }

    /**
     * 获取鼠标按键
     *
     * @return 鼠标按键
     */
    public MouseButton getMouseButton() {
        return mouseButton;
    }

    /**
     * 获取鼠标点击坐标
     *
     * @return 鼠标点击坐标
     */
    public Point2D getPoint2D() {
        return point2D;
    }
}
