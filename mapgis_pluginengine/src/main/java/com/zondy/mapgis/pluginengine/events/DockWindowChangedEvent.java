package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.pluginengine.plugin.IDockWindow;

import java.util.EventObject;

/**
 * 激活停靠面板改变事件
 *
 * @author cxy
 * @date 2019/09/12
 */
public class DockWindowChangedEvent extends EventObject {
    private transient IDockWindow dockWindow;
    private transient IDockWindow oldDockWindow;

    /**
     * 激活停靠面板改变事件
     *
     * @param source        事件源
     * @param dockWindow    停靠面板
     * @param oldDockWindow 旧停靠面板
     * @throws IllegalArgumentException if source is null.
     */
    public DockWindowChangedEvent(Object source, IDockWindow dockWindow, IDockWindow oldDockWindow) {
        super(source);
        this.dockWindow = dockWindow;
        this.oldDockWindow = oldDockWindow;
    }

    /**
     * 获取停靠面板
     *
     * @return 停靠面板
     */
    public IDockWindow getDockWindow() {
        return dockWindow;
    }

    /**
     * 获取旧停靠面板
     *
     * @return 旧停靠面板
     */
    public IDockWindow getOldDockWindow() {
        return oldDockWindow;
    }
}
