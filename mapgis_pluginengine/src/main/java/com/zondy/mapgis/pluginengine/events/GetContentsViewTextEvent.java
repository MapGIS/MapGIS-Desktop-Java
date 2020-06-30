package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.pluginengine.plugin.IContentsView;

import java.util.EventObject;

/**
 * 获取 ContentsView 文本标题事件
 *
 * @author cxy
 * @date 2019/09/16
 */
public class GetContentsViewTextEvent extends EventObject {
    private transient IContentsView contentsView;

    /**
     * 获取 ContentsView 文本标题事件
     *
     * @param source       事件源
     * @param contentsView ContentsView
     * @throws IllegalArgumentException if source is null.
     */
    public GetContentsViewTextEvent(Object source, IContentsView contentsView) {
        super(source);
        this.contentsView = contentsView;
    }

    /**
     * 获取 ContentsView
     *
     * @return ContentsView
     */
    public IContentsView getContentsView() {
        return contentsView;
    }
}
