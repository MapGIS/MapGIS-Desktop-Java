package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 设置 ContentsView 文本标题事件触发器
 *
 * @author cxy
 * @date 2019/09/16
 */
public interface SetContentsViewTextListener extends EventListener {
    /**
     * 触发设置 ContentsView 文本标题事件
     *
     * @param setContentsViewTextEvent 设置 ContentsView 文本标题事件
     */
    void setContentsViewText(SetContentsViewTextEvent setContentsViewTextEvent);
}
