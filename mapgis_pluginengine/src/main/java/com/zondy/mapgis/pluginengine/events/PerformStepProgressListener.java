package com.zondy.mapgis.pluginengine.events;

/**
 * 增加进度栏步进事件监听器
 *
 * @author cxy
 * @date 2019/09/16
 */
public interface PerformStepProgressListener {
    /**
     * 触发增加进度栏步进事件
     *
     * @param performStepProgressEvent 增加进度栏步进事件
     */
    void performStepProgress(PerformStepProgressEvent performStepProgressEvent);
}
