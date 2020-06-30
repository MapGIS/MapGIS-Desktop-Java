package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.pluginengine.plugin.IPlugin;

import java.util.EventObject;

/**
 * 插件是否可见改变事件
 *
 * @author cxy
 * @date 2019/09/16
 */
public class PluginVisibleChangedEvent extends EventObject {
    private transient IPlugin plugin;
    private transient boolean visible;

    /**
     * 插件是否可见改变事件
     *
     * @param source  事件源
     * @param plugin  插件
     * @param visible 是否可见
     * @throws IllegalArgumentException if source is null.
     */
    public PluginVisibleChangedEvent(Object source, IPlugin plugin, boolean visible) {
        super(source);
        this.plugin = plugin;
        this.visible = visible;
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
     * 获取是否可见
     *
     * @return true/false
     */
    public boolean isVisible() {
        return visible;
    }
}
