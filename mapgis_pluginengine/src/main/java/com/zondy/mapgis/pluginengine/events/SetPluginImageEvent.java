package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.pluginengine.plugin.IPlugin;
import javafx.scene.image.Image;

import java.util.EventObject;

/**
 * 设置插件图标事件
 *
 * @author cxy
 * @date 2019/09/16
 */
public class SetPluginImageEvent extends EventObject {
    private transient IPlugin plugin;
    private transient Image image;

    /**
     * 设置插件图标事件
     *
     * @param source 事件源
     * @param plugin 插件
     * @param image  插件图标
     * @throws IllegalArgumentException if source is null.
     */
    public SetPluginImageEvent(Object source, IPlugin plugin, Image image) {
        super(source);
        this.plugin = plugin;
        this.image = image;
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
     * 获取插件图标
     *
     * @return 插件图标
     */
    public Image getImage() {
        return image;
    }
}
