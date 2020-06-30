package com.zondy.mapgis.workspace.event;

import java.util.EventListener;

/**
 * 设置右键菜单是否可用事件监听器
 *
 * @author cxy
 * @date 2019/11/11
 */
public interface SetMenuItemEnableListener extends EventListener {
    /**
     * 触发设置右键菜单是否可用事件
     *
     * @param setMenuItemEnableEvent 设置右键菜单是否可用事件
     * @return 返回菜单项最终是否可用
     */
    public boolean fireSetMenuItemEnable(SetMenuItemEnableEvent setMenuItemEnableEvent);
}
