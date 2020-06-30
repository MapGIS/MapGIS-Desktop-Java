package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 设置组合框的文本值事件监听器
 *
 * @author cxy
 * @date 2019/09/16
 */
public interface SetEditComboboxTextListener extends EventListener {
    /**
     * 触发设置组合框的文本值事件
     *
     * @param setEditComboboxTextEvent 设置组合框的文本值事件
     */
    void setEditComboboxText(SetEditComboboxTextEvent setEditComboboxTextEvent);
}
