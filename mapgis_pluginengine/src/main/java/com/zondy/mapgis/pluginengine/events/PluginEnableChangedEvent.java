package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.pluginengine.plugin.IPlugin;

import java.util.EventObject;

/**
 * 插件是否可用改变事件
 *
 * @author cxy
 * @date 2019/09/16
 */
public class PluginEnableChangedEvent extends EventObject {
    private transient IPlugin plugin;
    private transient boolean enable;

    /**
     * 插件是否可用改变事件
     *
     * @param source 事件源
     * @param plugin 插件
     * @param enable 是否可见
     * @throws IllegalArgumentException if source is null.
     */
    public PluginEnableChangedEvent(Object source, IPlugin plugin, boolean enable) {
        super(source);
        this.plugin = plugin;
        this.enable = enable;
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
     * 获取是否可用
     *
     * @return true/false
     */
    public boolean isEnable() {
        return enable;
    }
}
