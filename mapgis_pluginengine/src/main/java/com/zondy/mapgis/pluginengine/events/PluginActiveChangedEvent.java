package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.pluginengine.plugin.IContentsView;

import java.util.EventObject;

/**
 * 插件激活状态改变事件
 * 只针对于 ContentView 生效
 *
 * @author cxy
 * @date 2019/09/16
 */
public class PluginActiveChangedEvent extends EventObject {
    private transient IContentsView contentsView;

    /**
     * 插件激活状态改变事件
     *
     * @param source 事件源
     * @throws IllegalArgumentException if source is null.
     */
    public PluginActiveChangedEvent(Object source, IContentsView contentsView) {
        super(source);
        this.contentsView = contentsView;
    }

    /**
     * 获取 ContentView
     *
     * @return ContentView
     */
    public IContentsView getContentsView() {
        return contentsView;
    }
}
