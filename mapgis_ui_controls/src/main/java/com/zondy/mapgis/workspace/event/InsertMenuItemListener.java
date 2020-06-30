package com.zondy.mapgis.workspace.event;

import java.util.EventListener;

/**
 * 插入菜单项监听器
 *
 * @author cxy
 * @date 2019/10/25
 */
public interface InsertMenuItemListener extends EventListener {
    /**
     * 触发插入菜单项事件
     *
     * @param insertMenuItemEvent 插入菜单项事件
     */
    public void fireInsertMenuItem(InsertMenuItemEvent insertMenuItemEvent);
}
