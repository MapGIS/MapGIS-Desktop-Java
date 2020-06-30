package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.pluginengine.plugin.IPlugin;

import java.util.EventObject;

/**
 * 添加插件事件
 *
 * @author cxy
 * @date 2019/09/16
 */
public class PluginAddedEvent extends EventObject {
    private transient IPlugin plugin;
    private transient Object parameter1;
    private transient Object parameter2;

    /**
     * 添加插件事件
     *
     * @param source 事件源
     * @param plugin 插件
     */
    public PluginAddedEvent(Object source, IPlugin plugin) {
        super(source);
        this.plugin = plugin;
        this.parameter1 = null;
        this.parameter2 = null;
    }

    /**
     * 添加插件事件
     *
     * @param source     事件源
     * @param plugin     插件
     * @param parameter1 参数1
     */
    public PluginAddedEvent(Object source, IPlugin plugin, Object parameter1) {
        super(source);
        this.plugin = plugin;
        this.parameter1 = parameter1;
        this.parameter2 = null;
    }

    /**
     * 添加插件事件
     *
     * @param source     事件源
     * @param plugin     插件
     * @param parameter1 参数1
     * @param parameter2 参数2
     * @throws IllegalArgumentException if source is null.
     */
    public PluginAddedEvent(Object source, IPlugin plugin, Object parameter1, Object parameter2) {
        super(source);
        this.plugin = plugin;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
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
     * 获取参数1
     *
     * @return 参数1
     */
    public Object getParameter1() {
        return parameter1;
    }

    /**
     * 获取参数2
     *
     * @return 参数2
     */
    public Object getParameter2() {
        return parameter2;
    }
}
