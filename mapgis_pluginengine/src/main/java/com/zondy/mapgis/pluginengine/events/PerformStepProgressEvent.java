package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.pluginengine.plugin.IProgress;

import java.util.EventObject;

/**
 * 增加进度栏步进事件
 *
 * @author cxy
 * @date 2019/09/16
 */
public class PerformStepProgressEvent extends EventObject {
    private transient IProgress progress;

    /**
     * 增加进度栏步进事件
     *
     * @param source   事件源
     * @param progress 进度栏
     * @throws IllegalArgumentException if source is null.
     */
    public PerformStepProgressEvent(Object source, IProgress progress) {
        super(source);
        this.progress = progress;
    }

    /**
     * 获取进度栏
     *
     * @return 进度栏
     */
    public IProgress getProgress() {
        return progress;
    }
}
