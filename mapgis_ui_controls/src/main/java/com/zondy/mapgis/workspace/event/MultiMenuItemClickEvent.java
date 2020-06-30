package com.zondy.mapgis.workspace.event;

import com.zondy.mapgis.map.DocumentItem;

import java.util.EventObject;

/**
 * 执行多选节点右键菜单事件
 *
 * @author cxy
 * @date 2019/11/20
 */
public class MultiMenuItemClickEvent extends EventObject {
    private transient Class<?> type;
    private transient DocumentItem[] documentItems;

    /**
     * 执行多选节点右键菜单事件
     *
     * @param source        事件源
     * @param type      节点菜单类
     * @param documentItems 文档项列表
     * @throws IllegalArgumentException if source is null.
     */
    public MultiMenuItemClickEvent(Object source, Class<?> type, DocumentItem[] documentItems) {
        super(source);
        this.type = type;
        this.documentItems = documentItems;
    }

    /**
     * 获取节点菜单类
     *
     * @return 节点菜单类
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * 获取文档项列表
     *
     * @return 文档项列表
     */
    public DocumentItem[] getDocumentItem() {
        return documentItems;
    }
}
