package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 增加进度栏值事件监听器
 *
 * @author cxy
 * @date 2019/09/16
 */
public interface IncrementProgressListener extends EventListener {
    /**
     * 触发增加进度栏值事件
     *
     * @param incrementProgressEvent 增加进度栏值事件
     */
    void incrementProgress(IncrementProgressEvent incrementProgressEvent);
}
