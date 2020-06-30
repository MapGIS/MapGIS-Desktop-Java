package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.pluginengine.plugin.ICommand;

import java.util.EventObject;

/**
 * Command 插件点击事件
 *
 * @author cxy
 * @date 2019/09/12
 */
public class CommandClickedEvent extends EventObject {
    private transient ICommand command;

    /**
     * Command 插件点击事件
     *
     * @param source  事件源
     * @param command Command 插件
     * @throws IllegalArgumentException if source is null.
     */
    public CommandClickedEvent(Object source, ICommand command) {
        super(source);
        this.command = command;
    }

    /**
     * 获取 Command 插件
     *
     * @return Command 插件
     */
    public ICommand getCommand() {
        return command;
    }
}
