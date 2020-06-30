package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.pluginengine.plugin.IProgress;

import java.util.EventObject;

/**
 * 增加进度栏值事件
 *
 * @author cxy
 * @date 2019/09/16
 */
public class IncrementProgressEvent extends EventObject {
    private transient IProgress progress;
    private transient int value;

    /**
     * 增加进度栏值事件
     *
     * @param source   事件源
     * @param progress 进度栏
     * @param value    进度栏值
     * @throws IllegalArgumentException if source is null.
     */
    public IncrementProgressEvent(Object source, IProgress progress, int value) {
        super(source);
        this.progress = progress;
        this.value = value;
    }

    /**
     * 获取进度栏
     *
     * @return 进度栏
     */
    public IProgress getProgress() {
        return progress;
    }

    /**
     * 获取进度栏值
     *
     * @return 进度栏值
     */
    public int getValue() {
        return value;
    }
}
