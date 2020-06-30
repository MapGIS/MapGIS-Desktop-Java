package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.pluginengine.plugin.IEditCombobox;

import java.util.EventObject;

/**
 * 设置组合框的对象值事件
 *
 * @author cxy
 * @date 2019/09/16
 */
public class SetEditComboboxValueEvent  extends EventObject {
    private transient IEditCombobox editCombobox;
    private transient Object value;

    /**
     * 设置组合框的对象值事件
     *
     * @param source       The object on which the Event initially occurred.
     * @param editCombobox 组合框
     * @param value        对象值
     * @throws IllegalArgumentException if source is null.
     */
    public SetEditComboboxValueEvent(Object source, IEditCombobox editCombobox, Object value) {
        super(source);
        this.editCombobox = editCombobox;
        this.value = value;
    }

    /**
     * 获取组合框
     *
     * @return 组合框
     */
    public IEditCombobox getEditCombobox() {
        return editCombobox;
    }

    /**
     * 获取组合框对象值
     *
     * @return 对象值
     */
    public Object getValue() {
        return value;
    }
}
