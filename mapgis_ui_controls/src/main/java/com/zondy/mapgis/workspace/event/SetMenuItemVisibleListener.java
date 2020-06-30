package com.zondy.mapgis.workspace.event;

import java.util.EventListener;

/**
 * 设置右键菜单是否可见事件监听器
 *
 * @author cxy
 * @date 2019/11/11
 */
public interface SetMenuItemVisibleListener extends EventListener {
    /**
     * 触发设置右键菜单是否可见事件
     *
     * @param setMenuItemVisibleEvent 设置右键菜单是否可见事件
     * @return 返回菜单项最终是否可见
     */
    public boolean fireSetMenuItemVisible(SetMenuItemVisibleEvent setMenuItemVisibleEvent);
}
