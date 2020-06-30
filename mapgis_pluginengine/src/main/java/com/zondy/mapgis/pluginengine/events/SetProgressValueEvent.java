package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.pluginengine.plugin.IProgress;

import java.util.EventObject;

/**
 * 设置进度条值事件
 *
 * @author cxy
 * @date 2019/09/16
 */
public class SetProgressValueEvent extends EventObject {
    private transient IProgress progress;
    private transient int value;

    /**
     * 设置进度条值事件
     *
     * @param source   事件源
     * @param progress 进度条
     * @param value    进度条值
     * @throws IllegalArgumentException if source is null.
     */
    public SetProgressValueEvent(Object source, IProgress progress, int value) {
        super(source);
        this.progress = progress;
        this.value = value;
    }

    /**
     * 获取进度条
     *
     * @return 进度条
     */
    public IProgress getProgress() {
        return progress;
    }

    /**
     * 获取进度条值
     *
     * @return 进度条值
     */
    public int getValue() {
        return value;
    }
}
