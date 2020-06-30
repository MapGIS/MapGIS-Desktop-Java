package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.pluginengine.plugin.ComboBoxItem;
import com.zondy.mapgis.pluginengine.plugin.IEditCombobox;

import java.util.EventObject;

/**
 * 添加下拉框项事件
 *
 * @author cxy
 * @date 2019/09/16
 */
public class AddComboBoxItemEvent extends EventObject {
    private transient IEditCombobox editCombobox;
    private transient ComboBoxItem comboBoxItem;

    /**
     * 添加下拉框项事件
     *
     * @param source       事件源
     * @param editCombobox 下拉框
     * @param comboBoxItem 下拉框项
     * @throws IllegalArgumentException if source is null.
     */
    public AddComboBoxItemEvent(Object source, IEditCombobox editCombobox, ComboBoxItem comboBoxItem) {
        super(source);
        this.editCombobox = editCombobox;
        this.comboBoxItem = comboBoxItem;
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
     * 获取下拉框项
     *
     * @return 下拉框项
     */
    public ComboBoxItem getComboBoxItem() {
        return comboBoxItem;
    }
}
