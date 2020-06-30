package com.zondy.mapgis.pluginengine.events;

import java.util.EventObject;

/**
 * 关闭应用程序事件
 *
 * @author cxy
 * @date 2019/11/07
 */
public class CloseApplicationEvent extends EventObject {
    /**
     * 关闭应用程序事件
     *
     * @param source 事件源
     * @throws IllegalArgumentException if source is null.
     */
    public CloseApplicationEvent(Object source) {
        super(source);
    }
}
