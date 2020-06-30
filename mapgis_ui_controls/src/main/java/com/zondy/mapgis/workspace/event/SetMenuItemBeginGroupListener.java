package com.zondy.mapgis.workspace.event;

import java.util.EventListener;

/**
 * 设置右键菜单项的 BeginGroup 属性事件监听器
 *
 * @author cxy
 * @date 2019/11/11
 */
public interface SetMenuItemBeginGroupListener extends EventListener {
    /**
     * 触发设置右键菜单项的 BeginGroup 属性事件
     *
     * @param setMenuItemBeginGroupEvent 设置右键菜单项的 BeginGroup 属性事件
     * @return 返回菜单项的 BeginGroup 属性
     */
    public boolean fireSetMenuItemBeginGroup(SetMenuItemBeginGroupEvent setMenuItemBeginGroupEvent);
}
