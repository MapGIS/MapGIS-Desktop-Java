package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.pluginengine.plugin.IContentsView;

import java.util.EventObject;

/**
 * 激活内容视图改变事件
 *
 * @author cxy
 * @date 2019/09/12
 */
public class ContentsViewChangedEvent extends EventObject {
    private transient IContentsView contentsView;
    private transient IContentsView oldContentsView;

    /**
     * 激活内容视图改变事件
     *
     * @param source          事件源
     * @param contentsView    内容视图
     * @param oldContentsView 旧内容视图
     * @throws IllegalArgumentException if source is null.
     */
    public ContentsViewChangedEvent(Object source, IContentsView contentsView, IContentsView oldContentsView) {
        super(source);
        this.contentsView = contentsView;
        this.oldContentsView = oldContentsView;
    }

    /**
     * 获取内容视图
     *
     * @return 内容视图
     */
    public IContentsView getContentsView() {
        return contentsView;
    }

    /**
     * 获取旧内容视图
     *
     * @return 旧内容视图
     */
    public IContentsView getOldContentsView() {
        return oldContentsView;
    }
}
