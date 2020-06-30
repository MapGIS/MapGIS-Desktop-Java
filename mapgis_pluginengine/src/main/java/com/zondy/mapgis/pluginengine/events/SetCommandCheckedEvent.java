package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.pluginengine.plugin.ICheckCommand;
import com.zondy.mapgis.pluginengine.plugin.ICommand;

import java.util.EventObject;

/**
 * 设置 Command 按钮选中状态事件
 *
 * @author cxy
 * @date 2019/09/16
 */
public class SetCommandCheckedEvent extends EventObject {
    private transient ICheckCommand checkCommand;
    private transient boolean checkState;

    /**
     * 设置 Command 按钮选中状态事件
     *
     * @param source     事件源
     * @param command    Command
     * @param checkState 选中状态
     * @throws IllegalArgumentException if source is null.
     */
    public SetCommandCheckedEvent(Object source, ICheckCommand command, boolean checkState) {
        super(source);
        this.checkCommand = command;
        this.checkState = checkState;
    }

    /**
     * 获取 Command
     *
     * @return Command
     */
    public ICheckCommand getCheckCommand() {
        return checkCommand;
    }

    /**
     * 获取 Command 选中状态
     *
     * @return Command 选中状态
     */
    public boolean isCheckState() {
        return checkState;
    }
}
