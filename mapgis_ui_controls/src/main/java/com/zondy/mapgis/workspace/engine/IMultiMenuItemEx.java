package com.zondy.mapgis.workspace.engine;

/**
 * 复选节点右键菜单项(扩展类型)
 *
 * @author cxy
 * @date 2019/09/12
 */
public interface IMultiMenuItemEx extends IMenuItem {
    /**
     * 点击菜单项
     *
     * @param items 文档项
     */
    void onClick(Object[] items);
}
