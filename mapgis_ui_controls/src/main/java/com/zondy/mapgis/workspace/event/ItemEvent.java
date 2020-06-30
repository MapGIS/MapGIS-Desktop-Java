package com.zondy.mapgis.workspace.event;

import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.map.Theme;
import com.zondy.mapgis.scene.Theme3D;

import java.util.EventObject;

/**
 * 节点事件基础
 *
 * @author cxy
 * @date 2019/11/20
 */
public class ItemEvent extends EventObject {
    private transient DocumentItem documentItem;
    private transient Theme theme;
    private transient Theme3D theme3D;
    private transient int themeInfoIndex;
    private transient Object data = null;

    /**
     * 节点事件基础
     *
     * @param source         事件源
     * @param documentItem   所属图层节点
     * @param theme          所属专题图节点
     * @param theme3D        所属三维专题图节点
     * @param themeInfoIndex 专题图子项ThemeInfo索引，如果该索引等于Theme或Theme3D子项个数，则表示为DefaultInfo（未分类信息）
     * @throws IllegalArgumentException if source is null.
     */
    public ItemEvent(Object source, DocumentItem documentItem, Theme theme, Theme3D theme3D, int themeInfoIndex) {
        super(source);
        this.documentItem = documentItem;
        this.theme = theme;
        this.theme3D = theme3D;
        this.themeInfoIndex = themeInfoIndex;
    }

    /**
     * 节点事件基础
     *
     * @param source         事件源
     * @param documentItem   所属图层节点
     * @param theme          所属专题图节点
     * @param theme3D        所属三维专题图节点
     * @param themeInfoIndex 专题图子项ThemeInfo索引，如果该索引等于Theme或Theme3D子项个数，则表示为DefaultInfo（未分类信息）
     * @param data           自定义项挂接数据
     * @throws IllegalArgumentException if source is null.
     */
    public ItemEvent(Object source, DocumentItem documentItem, Theme theme, Theme3D theme3D, int themeInfoIndex, Object data) {
        super(source);
        this.documentItem = documentItem;
        this.theme = theme;
        this.theme3D = theme3D;
        this.themeInfoIndex = themeInfoIndex;
        this.data = data;
    }

    /**
     * 获取所属图层节点
     *
     * @return 所属图层节点
     */
    public DocumentItem getDocumentItem() {
        return documentItem;
    }

    /**
     * 获取所属专题图节点
     *
     * @return 所属专题图节点
     */
    public Theme getTheme() {
        return theme;
    }

    /**
     * 获取所属三维专题图节点
     *
     * @return 所属三维专题图节点
     */
    public Theme3D getTheme3D() {
        return theme3D;
    }

    /**
     * 获取专题图子项ThemeInfo索引，如果该索引等于Theme或Theme3D子项个数，则表示为DefaultInfo（未分类信息）
     *
     * @return 专题图子项ThemeInfo索引
     */
    public int getThemeInfoIndex() {
        return themeInfoIndex;
    }

    /**
     * 获取自定义项挂接数据
     *
     * @return 自定义项挂接数据
     */
    public Object getData() {
        return data;
    }
}
