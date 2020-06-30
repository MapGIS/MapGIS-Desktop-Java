package com.zondy.mapgis.workspace.engine;

import com.zondy.mapgis.map.DocumentItem;

/**
 * 单选节点右键菜单
 *
 * @author cxy
 * @date 2019/09/12
 */
public interface ISinglePopMenu extends IPopMenu {
    /**
     * 菜单展开前过程
     *
     * @param item 文档项
     */
    void opening(DocumentItem item);
}
