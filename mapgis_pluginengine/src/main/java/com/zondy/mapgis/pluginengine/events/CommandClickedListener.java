package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * Command 插件点击事件监听器
 *
 * @author cxy
 * @date 2019/09/11
 */
public interface CommandClickedListener extends EventListener {
    /**
     * 触发 Command 插件点击事件
     *
     * @param commandClickedEvent Command 插件点击事件
     */
    void commandClicked(CommandClickedEvent commandClickedEvent);
}
