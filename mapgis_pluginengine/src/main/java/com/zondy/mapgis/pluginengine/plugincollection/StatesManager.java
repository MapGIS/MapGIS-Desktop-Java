package com.zondy.mapgis.pluginengine.plugincollection;

import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.workspace.event.StateChangedEvent;
import com.zondy.mapgis.workspace.event.StateChangedListener;

import java.util.ArrayList;

/**
 * @author cxy
 * @date 2019/11/07
 */
public class StatesManager implements IStatesManager {
    private IApplication application;

    public StatesManager(IApplication application) {
        this.application = application;
    }

    /**
     * 触发状态改变，功能同 fireStateChanged()
     *
     * @param sender 触发者
     */
    @Override
    public void onStateChanged(Object sender) {
        fireStateChanged(new StateChangedEvent(sender));
    }

    private ArrayList<StateChangedListener> stateChangedListeners = new ArrayList<>();

    /**
     * 添加状态改变事件监听器
     *
     * @param stateChangedListener 状态改变事件监听器
     */
    @Override
    public void addStateChangedListener(StateChangedListener stateChangedListener) {
        this.stateChangedListeners.add(stateChangedListener);
    }

    /**
     * 删除状态改变事件监听器
     *
     * @param stateChangedListener 状态改变事件监听器
     */
    @Override
    public void removeStateChangedListener(StateChangedListener stateChangedListener) {
        this.stateChangedListeners.remove(stateChangedListener);
    }

    /**
     * 触发状态改变事件
     *
     * @param stateChangedEvent 状态改变事件
     */
    @Override
    public void fireStateChanged(StateChangedEvent stateChangedEvent) {
        for (StateChangedListener stateChangedListener : stateChangedListeners) {
            stateChangedListener.fireStateChanged(stateChangedEvent);
        }
    }

}
