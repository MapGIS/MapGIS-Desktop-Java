package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.pluginengine.enums.CloseReason;

import java.util.EventObject;

/**
 * 应用程序关闭后事件
 *
 * @author cxy
 * @date 2019/10/15
 */
public class ApplicationClosedEvent extends EventObject {
    /**
     * 应用程序关闭后事件
     *
     * @param source      事件源
     * @throws IllegalArgumentException if source is null.
     */
    public ApplicationClosedEvent(Object source) {
        super(source);
    }
}
