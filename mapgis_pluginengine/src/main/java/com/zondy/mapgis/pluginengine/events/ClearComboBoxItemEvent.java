package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.pluginengine.plugin.IEditCombobox;

import java.util.EventObject;

/**
 * 清空下拉框项事件
 *
 * @author cxy
 * @date 2019/09/16
 */
public class ClearComboBoxItemEvent extends EventObject {
    private transient IEditCombobox editCombobox;

    /**
     * 清空下拉框项事件
     *
     * @param source       事件源
     * @param editCombobox 下拉框
     * @throws IllegalArgumentException if source is null.
     */
    public ClearComboBoxItemEvent(Object source, IEditCombobox editCombobox) {
        super(source);
        this.editCombobox = editCombobox;
    }

    /**
     * 获取下拉框
     *
     * @return 下拉框
     */
    public IEditCombobox getEditCombobox() {
        return editCombobox;
    }
}
