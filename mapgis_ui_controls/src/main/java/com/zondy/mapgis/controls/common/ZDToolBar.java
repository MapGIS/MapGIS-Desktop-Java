package com.zondy.mapgis.controls.common;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;

/**
 * @author CR
 * @file ZDToolBar.java
 * @brief 自定义工具条，格式类似.net
 * @create 2019-11-22.
 */
public class ZDToolBar extends ToolBar {
    private boolean showText = false;//是否要显示按钮标题

    public ZDToolBar() {
        this(false);
    }

    public ZDToolBar(Node... items) {
        this(false, items);
    }

    public ZDToolBar(boolean showText) {
        this(showText, new Node[]{});
    }

    public ZDToolBar(boolean showText, Node... items) {
        super(items);
        this.showText = showText;
    }

    @Override
    public String getUserAgentStylesheet() {
        if (this.showText) {
            return getClass().getResource("zdtoolbarex.css").toExternalForm();
        } else {
            return getClass().getResource("zdtoolbar.css").toExternalForm();
        }
    }
}
