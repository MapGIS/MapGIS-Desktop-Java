package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 内容视图关闭前事件监听器
 *
 * @author cxy
 * @date 2019/09/11
 */
public interface ContentsViewClosingListener extends EventListener {
    /**
     * 内容视图关闭前回调函数
     *
     * @param contentsViewClosingEvent 内容视图关闭中事件
     */
    void contentsViewClosing(ContentsViewClosingEvent contentsViewClosingEvent);
}
