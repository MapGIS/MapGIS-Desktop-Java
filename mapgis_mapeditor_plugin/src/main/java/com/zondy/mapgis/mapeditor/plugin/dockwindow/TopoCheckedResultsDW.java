package com.zondy.mapgis.mapeditor.plugin.dockwindow;

import com.zondy.mapgis.mapeditor.dialogs.TopoErrorResultsDialog;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.enums.DockingStyleEnum;
import com.zondy.mapgis.pluginengine.plugin.IDockWindow;
import javafx.scene.Node;
import javafx.scene.image.Image;

public class TopoCheckedResultsDW implements IDockWindow {
    private IApplication app;
    private TopoErrorResultsDialog topoErrorResultsDialog;

    /**
     * 停靠窗口的图标
     *
     * @return 图标
     */
    @Override
    public Image getBitmap() {
        return null;
    }

    /**
     * 停靠窗口的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "拓扑错误管理";
    }

    /**
     * 停靠窗口上停靠的用户控件
     *
     * @return 用户控件
     */
    @Override
    public Node getChildHWND() {
        return this.topoErrorResultsDialog;
    }

    /**
     * 停靠窗口的默认停靠位置
     *
     * @return 默认停靠位置
     */
    @Override
    public DockingStyleEnum getDefaultDock() {
        return DockingStyleEnum.BOTTOM;
    }

    /**
     * 停靠窗口是否初始加载
     *
     * @return true/false
     */
    @Override
    public boolean isInitCreate() {
        return false;
    }

    /**
     * 停靠窗口的激活状态发生改变时引发的事件
     *
     * @param isActive 停靠窗口的激活状态
     */
    @Override
    public void onActive(boolean isActive) {

    }

    /**
     * 停靠窗口被创建时引发的事件
     *
     * @param app 框架的宿主对象
     */
    @Override
    public void onCreate(IApplication app) {
        if (app != null) {
            this.app = app;
            this.topoErrorResultsDialog = new TopoErrorResultsDialog(this.app);
        }
    }

    /**
     * 停靠窗口被销毁时引发的事件
     */
    @Override
    public void onDestroy() {

    }
}
