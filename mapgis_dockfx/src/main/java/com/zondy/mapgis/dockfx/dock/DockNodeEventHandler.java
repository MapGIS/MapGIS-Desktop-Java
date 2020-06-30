package com.zondy.mapgis.dockfx.dock;

import javafx.event.EventHandler;
import javafx.scene.Node;

/**
 * @file DockNodeEventHandler.java
 * @brief 类型参数化通用EventHandler的包装器，用于监视当前拖动的停靠窗格布局中的停靠节点。
 *
 * @author CR
 * @date 2019-09-09
 */
public class DockNodeEventHandler implements EventHandler<DockEvent> {
    private DockPane dockPane;
    /**
     * 事件关联的节点
     */
    private final Node node;

    /**
     * 构造停靠节点事件处理程序，帮助此停靠窗格跟踪当前停靠区域。
     *
     * @param node 事件关联的节点
     */
    public DockNodeEventHandler(DockPane dockPane, Node node) {
        this.dockPane = dockPane;
        this.node = node;
    }

    @Override
    public void handle(DockEvent event) {
        dockPane.setDockNodeDrag(node);
    }
}
