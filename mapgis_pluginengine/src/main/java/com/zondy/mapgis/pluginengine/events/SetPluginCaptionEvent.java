package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.pluginengine.plugin.IPlugin;

import java.util.EventObject;

/**
 * 设置插件标题事件
 *
 * @author cxy
 * @date 2020/06/05
 */
public class SetPluginCaptionEvent extends EventObject {
    private transient IPlugin plugin;
    private transient String caption;

    /**
     * 设置插件标题事件
     *
     * @param source  事件源
     * @param plugin  插件
     * @param caption 插件标题
     * @throws IllegalArgumentException if source is null.
     */
    public SetPluginCaptionEvent(Object source, IPlugin plugin, String caption) {
        super(source);
        this.plugin = plugin;
        this.caption = caption;
    }

    /**
     * 获取插件
     *
     * @return 插件
     */
    public IPlugin getPlugin() {
        return plugin;
    }

    /**
     * 获取插件标题
     *
     * @return 插件标题
     */
    public String getCaption() {
        return caption;
    }
}
