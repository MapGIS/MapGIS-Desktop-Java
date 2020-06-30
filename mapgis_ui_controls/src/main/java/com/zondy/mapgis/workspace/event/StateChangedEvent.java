package com.zondy.mapgis.workspace.event;

import java.util.EventObject;
import java.util.HashMap;

/**
 * 图层状态改变事件
 *
 * @author cxy
 * @date 2019/11/20
 */
public class StateChangedEvent extends EventObject {
    private HashMap<Object, Object> hashMap;

    /**
     * 图层状态改变事件
     *
     * @param source 事件源
     * @throws IllegalArgumentException if source is null.
     */
    public StateChangedEvent(Object source) {
        super(source);
        this.hashMap = new HashMap<>();
    }

    /**
     * 图层状态改变事件
     *
     * @param source 事件源
     * @param key    键
     * @param value  值
     * @throws IllegalArgumentException if source is null.
     */
    public StateChangedEvent(Object source, Object key, Object value) {
        super(source);
        this.hashMap = new HashMap<>();
        if (key != null && value != null) {
            this.hashMap.put(key, value);
        }
    }

    /**
     * 获取 HashMap
     *
     * @return HashMap
     */
    public HashMap<Object, Object> getHashMap() {
        return this.hashMap;
    }
}
