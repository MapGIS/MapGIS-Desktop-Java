package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 获取 ContentsView 文本标题事件监听器
 *
 * @author cxy
 * @date 2019/09/16
 */
public interface GetContentsViewTextListener extends EventListener {
    /**
     * 触发获取 ContentsView 文本标题事件
     *
     * @param getContentsViewTextEvent 获取 ContentsView 文本标题事件
     * @return ContentsView 文本标题
     */
    String getContentsViewText(GetContentsViewTextEvent getContentsViewTextEvent);
}
