package com.zondy.mapgis.workspace.plugin;

import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.IMapContentsView;
import com.zondy.mapgis.workspace.WorkspacePanel;
import com.zondy.mapgis.workspace.control.MapViewControl;
import javafx.scene.Node;
import javafx.scene.image.Image;

/**
 * 地图视图
 *
 * @author cxy
 * @date 2019/12/05
 */
public class MapView implements IMapContentsView {
    private IApplication application;
    private MapViewControl mapViewControl;

    public MapViewControl getMapViewControl() {
        return this.mapViewControl;
    }

    /**
     * 获取内容视图中的地图控制控件
     *
     * @return 地图控制控件
     */
    @Override
    public MapControl getMapControl() {
        return mapViewControl == null ? null : mapViewControl.getMapControl();
    }

    /**
     * 内容视图的图标
     *
     * @return 图标
     */
    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_MapView_16.png"));
    }

    /**
     * 内容视图的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "地图视图";
    }

    /**
     * 在内容视图的标题栏是否显示控件框，即是否显示关闭按钮
     *
     * @return true/false
     */
    @Override
    public boolean isControlBox() {
        return true;
    }

    /**
     * 内容视图是否初始加载
     *
     * @return true/false
     */
    @Override
    public boolean isInitCreate() {
        return false;
    }

    /**
     * 停靠在内容视图上的用户控件
     *
     * @return 用户控件
     */
    @Override
    public Node getObjecthWnd() {
        return mapViewControl;
    }

    /**
     * 内容视图的激活状态发生改变时引发的事件
     *
     * @param isActive 内容视图的激活状态
     */
    @Override
    public void onActive(boolean isActive) {

    }

    /**
     * 内容视图被关闭时引发的事件
     */
    @Override
    public void onClose() {
        // 从 WorkSpace 中清除 MapControl
        WorkspacePanel.getWorkspacePanel(application).clearMapControl(getMapControl());
    }

    /**
     * 内容视图被创建时引发的事件
     *
     * @param app 框架的宿主对象
     */
    @Override
    public void onCreate(IApplication app) {
        this.application = app;
        this.mapViewControl = new MapViewControl(this.application);
    }
}
