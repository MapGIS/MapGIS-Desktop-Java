package com.zondy.mapgis.workspace.event;

import java.util.EventObject;

/**
 * 更新树事件
 *
 * @author cxy
 * @date 2019/11/27
 */
public class UpdateTreeEvent extends EventObject {
    /**
     * 更新树事件
     *
     * @param source 事件源
     * @throws IllegalArgumentException if source is null.
     */
    public UpdateTreeEvent(Object source) {
        super(source);
    }
}
