package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 设置 Command 按钮选中状态事件监听器
 *
 * @author cxy
 * @date 2019/09/16
 */
public interface SetCommandCheckedListener extends EventListener {
    /**
     * 触发设置 Command 按钮选中状态事件
     *
     * @param setCommandCheckedEvent 设置 Command 按钮选中状态事件
     */
    void setCommandChecked(SetCommandCheckedEvent setCommandCheckedEvent);
}
