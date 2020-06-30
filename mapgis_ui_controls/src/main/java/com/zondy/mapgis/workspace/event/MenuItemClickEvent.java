package com.zondy.mapgis.workspace.event;

import com.zondy.mapgis.map.DocumentItem;

import java.util.EventObject;

/**
 * 执行单选节点右键菜单事件
 *
 * @author cxy
 * @date 2019/11/20
 */
public class MenuItemClickEvent extends EventObject {
    private transient Class<?> type;
    private transient DocumentItem documentItem;

    /**
     * 执行单选节点右键菜单事件
     *
     * @param source       事件源
     * @param type         节点菜单类
     * @param documentItem 文档项
     * @throws IllegalArgumentException if source is null.
     */
    public MenuItemClickEvent(Object source, Class<?> type, DocumentItem documentItem) {
        super(source);
        this.type = type;
        this.documentItem = documentItem;
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
     * 获取文档项
     *
     * @return 文档项
     */
    public DocumentItem getDocumentItem() {
        return documentItem;
    }
}
