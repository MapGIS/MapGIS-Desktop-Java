package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.pluginengine.plugin.IContentsView;

import java.util.EventObject;

/**
 * 内容视图关闭前事件
 *
 * @author cxy
 * @date 2019/09/12
 */
public class ContentsViewClosingEvent extends EventObject {
    private transient IContentsView contentsView;
    private transient boolean cancel;

    /**
     * 内容视图关闭前事件
     *
     * @param source       事件源
     * @param contentsView 内容视图
     * @param isCancel     是否取消
     * @throws IllegalArgumentException if source is null.
     */
    public ContentsViewClosingEvent(Object source, IContentsView contentsView, boolean isCancel) {
        super(source);
        this.contentsView = contentsView;
        this.cancel = isCancel;
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
     * 获取取消标识
     *
     * @return true/false
     */
    public boolean isCancel() {
        return cancel;
    }

    /**
     * 设置取消标识
     *
     * @param isCancel true/false
     */
    public void setCancel(boolean isCancel) {
        this.cancel = isCancel;
    }
}
