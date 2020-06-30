package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 关闭 DropDownControl 事件监听器
 *
 * @author cxy
 * @date 2019/09/16
 */
public interface CloseUpDropDownControlListener extends EventListener {
    /**
     * 触发关闭 DropDownControl 事件
     *
     * @param closeUpDropDownControlEvent 关闭 DropDownControl 事件
     */
    void closeUpDropDownControl(CloseUpDropDownControlEvent closeUpDropDownControlEvent);
}
