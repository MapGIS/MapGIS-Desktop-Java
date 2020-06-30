package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 激活内容视图改变事件监听器
 *
 * @author cxy
 * @date 2019/09/11
 */
public interface ContentsViewChangedListener extends EventListener {
    /**
     * 激活内容视图改变回调函数
     *
     * @param contentsViewChangedEvent 内容视图改变事件
     */
    void contentsViewChanged(ContentsViewChangedEvent contentsViewChangedEvent);
}
