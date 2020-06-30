package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 应用程序标题改变事件触发器
 *
 * @author cxy
 * @date 2019/11/07
 */
public interface TitleChangedListener extends EventListener {
    /**
     * 触发应用程序标题改变事件
     *
     * @param titleChangedEvent 应用程序标题改变事件
     */
    void titleChanged(TitleChangedEvent titleChangedEvent);
}
