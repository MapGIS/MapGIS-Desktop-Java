package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.pluginengine.plugin.IStatic;

import java.util.EventObject;

/**
 * 设置静态框标题事件
 *
 * @author cxy
 * @date 2019/09/16
 */
public class SetStaticCaptionEvent extends EventObject {
    private transient IStatic iStatic;
    private transient String caption;

    /**
     * 设置静态框标题事件
     *
     * @param source 事件源
     * @throws IllegalArgumentException if source is null.
     */
    public SetStaticCaptionEvent(Object source, IStatic iStatic, String caption) {
        super(source);
        this.iStatic = iStatic;
        this.caption = caption;
    }

    /**
     * 获取静态框
     *
     * @return 静态框
     */
    public IStatic getiStatic() {
        return iStatic;
    }

    /**
     * 获取静态框标题
     *
     * @return 标题
     */
    public String getCaption() {
        return caption;
    }
}
