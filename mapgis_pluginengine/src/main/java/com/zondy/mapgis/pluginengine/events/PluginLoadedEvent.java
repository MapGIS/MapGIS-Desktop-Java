package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.pluginengine.plugin.IPlugin;

import java.util.EventObject;

/**
 * 插件加载事件
 *
 * @author cxy
 * @date 2019/09/12
 */
public class PluginLoadedEvent extends EventObject {

    private transient IPlugin plugin;

    /**
     * 插件加载事件
     *
     * @param source 事件源
     * @param plugin 插件
     * @throws IllegalArgumentException if source is null.
     */
    public PluginLoadedEvent(Object source, IPlugin plugin) {
        super(source);
        this.plugin = plugin;
    }

    /**
     * 获取插件
     *
     * @return 插件
     */
    public IPlugin getPlugin() {
        return plugin;
    }
}
