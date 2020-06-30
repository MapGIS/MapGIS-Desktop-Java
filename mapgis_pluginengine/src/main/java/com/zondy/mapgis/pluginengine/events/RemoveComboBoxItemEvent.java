package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.pluginengine.plugin.IEditCombobox;

import java.util.EventObject;

/**
 * 移除下拉框项事件
 *
 * @author cxy
 * @date 2019/09/16
 */
public class RemoveComboBoxItemEvent extends EventObject {
    private transient IEditCombobox editCombobox;
    private transient Object value;

    /**
     * 移除下拉框项事件
     *
     * @param source       事件源
     * @param editCombobox 下拉框
     * @param value        要移除的下拉框项
     * @throws IllegalArgumentException if source is null.
     */
    public RemoveComboBoxItemEvent(Object source, IEditCombobox editCombobox, Object value) {
        super(source);
        this.editCombobox = editCombobox;
        this.value = value;
    }

    /**
     * 获取下拉框
     *
     * @return 下拉框
     */
    public IEditCombobox getEditCombobox() {
        return editCombobox;
    }

    /**
     * 获取要删除的下拉框项
     *
     * @return 要删除的下拉框项
     */
    public Object getValue() {
        return value;
    }
}
