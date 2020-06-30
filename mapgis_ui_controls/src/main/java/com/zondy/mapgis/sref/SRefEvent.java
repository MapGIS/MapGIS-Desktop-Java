package com.zondy.mapgis.sref;

import com.zondy.mapgis.srs.SRefData;
import javafx.event.Event;
import javafx.event.EventType;

/**
 * @author CR
 * @file SRefEvent.java
 * @brief 参照系界面的事件
 * @create 2019-11-11.
 */
public class SRefEvent extends Event
{
    /**
     * 参照系修改事件
     */
    public static final EventType<SRefEvent> SREFCHANGED = new EventType<SRefEvent>(Event.ANY, "SREFCHANGED");

    private SRefData sRefData;
    private SRefData oldSRefData;

    /**
     * 窗口关闭事件
     *
     * @param source    要关闭的窗口对象
     * @param eventType The type of the event.
     * @param sRefData  新参照系
     */
    public SRefEvent(Object source, EventType<? extends SRefEvent> eventType, SRefData sRefData, SRefData oldSRefData)
    {
        super(source, null, eventType);
        this.sRefData = sRefData;
        this.oldSRefData = oldSRefData;
    }

    /**
     * 获取修改后的参照系
     *
     * @return
     */
    public SRefData getSRefData()
    {
        return this.sRefData;
    }

    /**
     * 获取修改前的旧参照系
     *
     * @return
     */
    public SRefData getOldSRefData()
    {
        return this.oldSRefData;
    }
}


