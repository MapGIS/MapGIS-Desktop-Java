package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 设置进度条值事件监听器
 *
 * @author cxy
 * @date 2019/09/16
 */
public interface SetProgressValueListener extends EventListener {
    /**
     * 触发设置进度条值事件
     *
     * @param setProgressValueEvent 设置进度条值事件
     */
    void setProgressValue(SetProgressValueEvent setProgressValueEvent);
}
