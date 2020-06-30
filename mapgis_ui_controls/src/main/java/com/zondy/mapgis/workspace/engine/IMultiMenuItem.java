package com.zondy.mapgis.workspace.engine;

import com.zondy.mapgis.map.DocumentItem;

/**
 * 复选节点右键菜单项
 *
 * @author cxy
 * @date 2019/09/12
 */
public interface IMultiMenuItem extends IMenuItem {
    /**
     * 点击菜单项
     *
     * @param items 文档项
     */
    void onClick(DocumentItem[] items);
}
