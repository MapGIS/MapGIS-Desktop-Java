package com.zondy.mapgis.workspace.event;

import java.util.EventListener;

/**
 * 设置右键菜单标题事件监听器
 *
 * @author cxy
 * @date 2019/11/11
 */
public interface SetMenuItemCaptionListener extends EventListener {
    /**
     * 触发设置右键菜单标题事件
     *
     * @param setMenuItemCaptionEvent 设置右键菜单标题事件
     * @return 返回菜单项最终标题
     */
    public String fireSetMenuItemCaption(SetMenuItemCaptionEvent setMenuItemCaptionEvent);
}
