package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.pluginengine.plugin.IContentsView;

import java.util.EventObject;

/**
 * 关闭 ContentsView 事件
 *
 * @author cxy
 * @date 2019/09/16
 */
public class CloseContentsViewEvent extends EventObject {
    private transient IContentsView contentsView;

    /**
     * 关闭 ContentsView 事件
     *
     * @param source       事件源
     * @param contentsView ContentsView
     * @throws IllegalArgumentException if source is null.
     */
    public CloseContentsViewEvent(Object source, IContentsView contentsView) {
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
