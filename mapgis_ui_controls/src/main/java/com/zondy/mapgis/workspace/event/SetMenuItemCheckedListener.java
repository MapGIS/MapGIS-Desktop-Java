package com.zondy.mapgis.workspace.event;

import java.util.EventListener;

/**
 * 设置右键菜单是否选中事件监听器
 *
 * @author cxy
 * @date 2019/11/11
 */
public interface SetMenuItemCheckedListener extends EventListener {
    /**
     * 触发设置右键菜单是否选中事件
     *
     * @param setMenuItemCheckedEvent 设置右键菜单是否选中事件
     * @return 返回菜单项最终是否选中
     */
    public boolean fireSetMenuItemChecked(SetMenuItemCheckedEvent setMenuItemCheckedEvent);
}
