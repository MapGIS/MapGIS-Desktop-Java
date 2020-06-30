package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.utilities.MyEventListener;

/**
 * 开始加载事件监听器
 *
 * @author cxy
 * @date 2019/09/16
 */
public interface BeginLoadListener extends MyEventListener {
    /**
     * 触发开始加载事件
     *
     * @param beginLoadEvent 开始加载事件
     * @return int
     */
    int beginLoad(BeginLoadEvent beginLoadEvent);
}
