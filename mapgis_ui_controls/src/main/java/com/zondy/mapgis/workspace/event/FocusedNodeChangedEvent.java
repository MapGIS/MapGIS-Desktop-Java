package com.zondy.mapgis.workspace.event;

import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.map.Theme;
import com.zondy.mapgis.scene.Theme3D;

import java.util.EventObject;

/**
 * 焦点节点改变事件
 *
 * @author cxy
 * @date 2019/11/20
 */
public class FocusedNodeChangedEvent extends ItemEvent {
    /**
     * 焦点节点改变事件
     *
     * @param source         事件源
     * @param documentItem   所属图层节点
     * @param theme          所属专题图节点
     * @param theme3D        所属三维专题图节点
     * @param themeInfoIndex 专题图子项ThemeInfo索引，如果该索引等于Theme或Theme3D子项个数，则表示为DefaultInfo（未分类信息）
     * @throws IllegalArgumentException if source is null.
     */
    public FocusedNodeChangedEvent(Object source, DocumentItem documentItem, Theme theme, Theme3D theme3D, int themeInfoIndex) {
        super(source, documentItem, theme, theme3D, themeInfoIndex);
    }

    /**
     * 焦点节点改变事件
     *
     * @param source         事件源
     * @param documentItem   所属图层节点
     * @param theme          所属专题图节点
     * @param theme3D        所属三维专题图节点
     * @param themeInfoIndex 专题图子项ThemeInfo索引，如果该索引等于Theme或Theme3D子项个数，则表示为DefaultInfo（未分类信息）
     * @param data           自定义项挂接数据
     * @throws IllegalArgumentException if source is null.
     */
    public FocusedNodeChangedEvent(Object source, DocumentItem documentItem, Theme theme, Theme3D theme3D, int themeInfoIndex, Object data) {
        super(source, documentItem, theme, theme3D, themeInfoIndex, data);
    }
}
