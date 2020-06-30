package com.zondy.mapgis.pluginengine.events;

import java.util.EventObject;

/**
 * 应用程序标题改变事件
 *
 * @author cxy
 * @date 2019/11/07
 */
public class TitleChangedEvent extends EventObject {
    private transient String title;

    /**
     * 应用程序标题改变事件
     *
     * @param source 事件源
     * @param title  应用程序标题
     * @throws IllegalArgumentException if source is null.
     */
    public TitleChangedEvent(Object source, String title) {
        super(source);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
