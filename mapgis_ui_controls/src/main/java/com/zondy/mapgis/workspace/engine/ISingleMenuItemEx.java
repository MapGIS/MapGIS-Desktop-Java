package com.zondy.mapgis.workspace.engine;

/**
 * 单选节点右键菜单项(扩展类型)
 *
 * @author cxy
 * @date 2019/09/12
 */
public interface ISingleMenuItemEx extends IMenuItem {
    /**
     * 点击菜单项
     *
     * @param item 文档项
     */
    void onClick(Object item);
}
