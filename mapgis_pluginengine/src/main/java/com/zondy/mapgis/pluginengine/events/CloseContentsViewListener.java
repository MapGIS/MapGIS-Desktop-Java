package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 关闭 ContentsView 事件触发器
 *
 * @author cxy
 * @date 2019/09/16
 */
public interface CloseContentsViewListener extends EventListener {
    /**
     * 触发关闭 ContentsView 事件
     *
     * @param closeContentsViewEvent 关闭 ContentsView 事件
     */
    void closeContentsView(CloseContentsViewEvent closeContentsViewEvent);
}
