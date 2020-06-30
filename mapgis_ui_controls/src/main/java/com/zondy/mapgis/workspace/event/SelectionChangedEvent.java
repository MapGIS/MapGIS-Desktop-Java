package com.zondy.mapgis.workspace.event;

import java.util.EventObject;

/**
 * 选中项改变事件
 *
 * @author cxy
 * @date 2019/11/20
 */
public class SelectionChangedEvent extends EventObject {
    /**
     * 选中项改变事件
     *
     * @param source 事件源
     * @throws IllegalArgumentException if source is null.
     */
    public SelectionChangedEvent(Object source) {
        super(source);
    }
}
