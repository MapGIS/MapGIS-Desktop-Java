package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 清空下拉框项事件监听器
 *
 * @author cxy
 * @date 2019/09/16
 */
public interface ClearComboBoxItemListener extends EventListener {
    /**
     * 触发清空下拉框项事件
     *
     * @param clearComboBoxItemEvent 清空下拉框项事件
     */
    void clearComboBoxItem(ClearComboBoxItemEvent clearComboBoxItemEvent);
}
