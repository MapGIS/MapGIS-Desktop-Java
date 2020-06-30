package com.zondy.mapgis.workspace.event;

import java.util.EventListener;

/**
 * 选中项改变事件监听器
 *
 * @author cxy
 * @date 2019/11/20
 */
public interface SelectionChangedListener extends EventListener {
    /**
     * 触发选中项改变事件
     *
     * @param selectionChangedEvent 选中项改变事件
     */
    public void fireSelectionChanged(SelectionChangedEvent selectionChangedEvent);
}
