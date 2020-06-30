package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 设置组合框的对象值事件监听器
 *
 * @author cxy
 * @date 2019/09/16
 */
public interface SetEditComboboxValueListener extends EventListener {
    /**
     * 触发设置组合框的对象值事件
     *
     * @param setEditComboboxValueEvent 设置组合框的对象值事件
     */
    void setEditComboboxValue(SetEditComboboxValueEvent setEditComboboxValueEvent);
}
