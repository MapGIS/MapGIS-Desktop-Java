package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 移除下拉框项事件监听器
 *
 * @author cxy
 * @date 2019/09/16
 */
public interface RemoveComboBoxItemListener extends EventListener {
    /**
     * 触发移除下拉框项事件
     *
     * @param removeComboBoxItemEvent 移除下拉框项事件
     */
    void removeComboBoxItem(RemoveComboBoxItemEvent removeComboBoxItemEvent);
}
