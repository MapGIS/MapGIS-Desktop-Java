package com.zondy.mapgis.workspace.event;

import com.zondy.mapgis.map.DocumentItem;

import java.util.EventObject;

/**
 * 单击/双击节点事件
 *
 * @author cxy
 * @date 2019/11/26
 */
public class ClickNodeEvent extends EventObject {
    private transient DocumentItem documentItem;

    /**
     * 单击/双击节点事件
     *
     * @param source 事件源
     * @param documentItem 地图文档项
     * @throws IllegalArgumentException if source is null.
     */
    public ClickNodeEvent(Object source, DocumentItem documentItem) {
        super(source);
        this.documentItem = documentItem;
    }

    /**
     * 获取地图文档项
     *
     * @return 地图文档项
     */
    public DocumentItem getDocumentItem() {
        return documentItem;
    }
}
