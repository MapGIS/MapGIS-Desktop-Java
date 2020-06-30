package com.zondy.mapgis.dockfx.dock;

import javafx.scene.Node;

/**
 * @file DockView.java
 * @brief 内容视图
 *
 * @author CR
 * @date 2020-6-12
 */
public class DockView extends DockWindow {
    /**
     * 构造带有默认标题栏和布局的默认DockView
     */
    public DockView() {
        this((Node) null);
    }

    /**
     * 构造带有默认标题栏和布局的默认DockView
     *
     * @param contents 停靠节点的界面内容
     */
    public DockView(Node contents) {
        this(contents, null, null);
    }

    /**
     * 构造带有默认标题栏和布局的默认DockView
     *
     * @param contents 停靠节点的界面内容
     * @param title    停靠节点的标题
     */
    public DockView(Node contents, String title) {
        this(contents, title, null);
    }

    /**
     * 构造带有默认标题栏和布局的默认DockView
     *
     * @param contents 停靠节点的界面内容
     * @param title    停靠节点的标题
     * @param graphic  停靠节点的标题图形
     */
    public DockView(Node contents, String title, Node graphic) {
        super(contents, title, graphic);
    }

    /**
     * 从fxml中读取界面内容构造节点
     *
     * @param fxmlPath fxml文件路径
     */
    public DockView(String fxmlPath) {
        this(fxmlPath, null, null);
    }

    /**
     * 从fxml中读取界面内容构造节点
     *
     * @param fxmlPath fxml文件路径
     * @param title    节点标题
     */
    public DockView(String fxmlPath, String title) {
        this(fxmlPath, title, null);
    }

    /**
     * 从fxml中读取界面内容构造节点
     *
     * @param fxmlPath fxml文件路径
     * @param title    节点标题
     * @param graphic  节点图标
     * @return
     */
    public DockView(String fxmlPath, String title, Node graphic) {
        this(loadNode(fxmlPath), title, graphic);
    }

    /**
     * 关闭节点，并从停靠布局容器中移除。
     */
    @Override
    public void close() {
        super.close();
        if (this.getStage() != null) {
            this.getStage().close();
        }
    }
}
