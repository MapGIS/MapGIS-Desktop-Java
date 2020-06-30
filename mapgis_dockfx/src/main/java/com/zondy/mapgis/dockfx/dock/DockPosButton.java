package com.zondy.mapgis.dockfx.dock;

import javafx.scene.control.Button;

/**
 * @file DockPosButton.java
 * @brief 停靠方位指示器按钮，在拖动停靠事件期间显示
 *
 * @author CR
 * @date 2020-6-12
 */
public class DockPosButton extends Button {
    /**
     * 是否是根停靠节点（相对于整个DockPane的上下左右四个按钮）。
     */
    private boolean dockRoot;
    /**
     * 停靠方位
     */
    private DockPos dockPos;

    /**
     * 构造方位指示器按钮
     */
    public DockPosButton(boolean dockRoot, DockPos dockPos) {
        super();
        this.dockRoot = dockRoot;
        this.dockPos = dockPos;
    }

    /**
     * 设置停靠方位
     *
     * @param dockPos 停靠方位
     */
    public final void setDockPos(DockPos dockPos) {
        this.dockRoot = true;
        this.dockPos = dockPos;
    }

    /**
     * 获取停靠方位
     *
     * @return 停靠方位
     */
    public final DockPos getDockPos() {
        return dockPos;
    }

    /**
     * 设置是否是根停靠节点
     *
     * @param dockRoot 是否是根停靠节点
     */
    public final void setDockRoot(boolean dockRoot) {
        this.dockRoot = dockRoot;
    }

    /**
     * 获取是否是根停靠节点（相对于整个DockPane的上下左右四个按钮）。
     *
     * @return 是否是根停靠节点（相对于整个DockPane的上下左右四个按钮）。
     */
    public final boolean isDockRoot() {
        return dockRoot;
    }
}
