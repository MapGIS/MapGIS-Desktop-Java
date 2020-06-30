package com.zondy.mapgis.dockfx.pane;

import java.util.List;
import java.util.Stack;

import com.zondy.mapgis.dockfx.dock.DockPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Region;

/**
 * @file ContentPane.java
 * @brief 停靠窗口的布局基类
 *
 * @author CR
 * @date 2020-6-12
 */
public interface ContentPane {
    enum Type {
        SplitPane,
        /**
         * The TabPane.
         */
        TabPane
    }

    /**
     * 获取布局类型
     *
     * @return
     */
    Type getType();

    /**
     * 添加窗口
     *
     * @param root
     * @param sibling
     * @param node
     * @param dockPos
     */
    void addNode(Node root, Node sibling, Node node, DockPos dockPos);

    /**
     * 移除窗口
     *
     * @param stack
     * @param node
     * @return 移除成功true
     */
    boolean removeNode(Stack<Parent> stack, Node node);

    /**
     * 获取sibling的父级布局
     *
     * @param stack
     * @param sibling
     * @return
     */
    ContentPane getSiblingParent(Stack<Parent> stack, Node sibling);

    /**
     * 获取子节点
     *
     * @return
     */
    List<Node> getChildrenList();

    /**
     * 替换窗口
     *
     * @param sibling
     * @param node
     */
    void set(Node sibling, Node node);

    /**
     * 替换指定索引处的窗口
     *
     * @param idx
     * @param node
     */
    void set(int idx, Node node);

    /**
     * 获取父级布局
     *
     * @return
     */
    ContentPane getContentParent();

    /**
     * 设置父级布局
     *
     * @param pane
     */
    void setContentParent(ContentPane pane);

    static double getRealLength(Node node, boolean isWidth) {
        double len = 0.0;
        if (node != null) {
            if (node instanceof Region) {
                len = isWidth ? ((Region) node).getWidth() : ((Region) node).getHeight();
            }

            if (len <= 0) {
                len = isWidth ? node.prefWidth(0) : node.prefHeight(0);
            }
        }
        return len;
    }
}
