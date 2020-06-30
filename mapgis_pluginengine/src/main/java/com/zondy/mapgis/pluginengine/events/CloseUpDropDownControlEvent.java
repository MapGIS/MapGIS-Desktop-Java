package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.pluginengine.plugin.IDropDown;

import java.util.EventObject;

/**
 * 关闭 DropDownControl 事件
 *
 * @author cxy
 * @date 2019/09/16
 */
public class CloseUpDropDownControlEvent extends EventObject {
    private transient IDropDown dropDown;

    /**
     * 关闭 DropDownControl 事件
     *
     * @param source   事件源
     * @param dropDown DropDownControl
     * @throws IllegalArgumentException if source is null.
     */
    public CloseUpDropDownControlEvent(Object source, IDropDown dropDown) {
        super(source);
        this.dropDown = dropDown;
    }

    /**
     * 获取 DropDownControl
     *
     * @return DropDownControl
     */
    public IDropDown getDropDown() {
        return dropDown;
    }
}
