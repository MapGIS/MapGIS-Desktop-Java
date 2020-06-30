package com.zondy.mapgis.pluginengine.events;

import java.util.EventObject;

/**
 * 应用程序加载事件
 *
 * @author cxy
 * @date 2019/10/15
 */
public class ApplicationLoadedEvent extends EventObject {
    /**
     * 应用程序加载事件
     *
     * @param source 事件源
     * @throws IllegalArgumentException if source is null.
     */
    public ApplicationLoadedEvent(Object source) {
        super(source);
    }
}
