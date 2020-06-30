package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.pluginengine.plugin.IEditCombobox;

import java.util.EventObject;

/**
 * 设置组合框的文本值事件
 *
 * @author cxy
 * @date 2019/09/16
 */
public class SetEditComboboxTextEvent extends EventObject {
    private transient IEditCombobox editCombobox;
    private transient String text;

    /**
     * 设置组合框的文本值事件
     *
     * @param source       The object on which the Event initially occurred.
     * @param editCombobox 组合框
     * @param text         文本值
     * @throws IllegalArgumentException if source is null.
     */
    public SetEditComboboxTextEvent(Object source, IEditCombobox editCombobox, String text) {
        super(source);
        this.editCombobox = editCombobox;
        this.text = text;
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
     * 获取组合框文本值
     *
     * @return 文本值
     */
    public String getText() {
        return text;
    }
}
