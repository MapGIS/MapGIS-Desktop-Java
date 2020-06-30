package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 添加下拉框项事件监听器
 *
 * @author cxy
 * @date 2019/09/16
 */
public interface AddComboBoxItemListener extends EventListener {
    /**
     * 触发添加下拉框项事件
     *
     * @param addComboBoxItemEvent 添加下拉框项事件
     * @return 下拉框项索引
     */
    int addComboboxItem(AddComboBoxItemEvent addComboBoxItemEvent);
}
