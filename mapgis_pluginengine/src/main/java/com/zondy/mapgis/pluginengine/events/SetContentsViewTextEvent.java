package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.pluginengine.plugin.IContentsView;

import java.util.EventObject;

/**
 * 设置 ContentsView 文本标题事件
 *
 * @author cxy
 * @date 2019/09/16
 */
public class SetContentsViewTextEvent extends EventObject {
    private transient IContentsView contentsView;
    private transient String contentsViewText;

    /**
     * 设置 ContentsView 文本标题事件
     *
     * @param source           事件源
     * @param contentsView     ContentsView
     * @param contentsViewText ContentsView 文本标题
     * @throws IllegalArgumentException if source is null.
     */
    public SetContentsViewTextEvent(Object source, IContentsView contentsView, String contentsViewText) {
        super(source);
        this.contentsView = contentsView;
        this.contentsViewText = contentsViewText;
    }

    /**
     * 获取 ContentsView
     *
     * @return ContentsView
     */
    public IContentsView getContentsView() {
        return contentsView;
    }

    /**
     * 获取 ContentsView 文本标题
     *
     * @return ContentsView 文本标题
     */
    public String getContentsViewText() {
        return contentsViewText;
    }
}
