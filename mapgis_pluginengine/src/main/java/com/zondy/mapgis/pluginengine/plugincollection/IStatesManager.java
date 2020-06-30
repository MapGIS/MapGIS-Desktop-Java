package com.zondy.mapgis.pluginengine.plugincollection;

import com.zondy.mapgis.pluginengine.plugin.IPlugin;
import com.zondy.mapgis.workspace.event.StateChangedEvent;
import com.zondy.mapgis.workspace.event.StateChangedListener;

/**
 * 状态管理器接口
 *
 * @author cxy
 * @date 2019/10/15
 */
public interface IStatesManager {
    /**
     * 触发状态改变，功能同 fireStateChanged()
     *
     * @param sender 触发者
     */
    void onStateChanged(Object sender);

    /**
     * 添加状态改变事件监听器
     *
     * @param stateChangedListener 状态改变事件监听器
     */
    void addStateChangedListener(StateChangedListener stateChangedListener);

    /**
     * 删除状态改变事件监听器
     *
     * @param stateChangedListener 状态改变事件监听器
     */
    void removeStateChangedListener(StateChangedListener stateChangedListener);

    /**
     * 触发状态改变事件
     *
     * @param stateChangedEvent 状态改变事件
     */
    void fireStateChanged(StateChangedEvent stateChangedEvent);
}
