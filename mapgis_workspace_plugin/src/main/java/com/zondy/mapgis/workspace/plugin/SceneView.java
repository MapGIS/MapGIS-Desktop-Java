package com.zondy.mapgis.workspace.plugin;

import com.zondy.mapgis.controls.SceneControl;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ISceneContentsView;
import com.zondy.mapgis.workspace.WorkspacePanel;
import com.zondy.mapgis.workspace.control.SceneViewControl;
import javafx.scene.Node;
import javafx.scene.image.Image;

/**
 * 场景视图
 *
 * @author cxy
 * @date 2020/04/30
 */
public class SceneView implements ISceneContentsView {
    private IApplication application;
    private SceneViewControl sceneViewControl;

    /**
     * 获取内容视图中的场景控制控件
     *
     * @return 场景控制控件
     */
    @Override
    public SceneControl getSceneControl() {
        return this.sceneViewControl == null ? null : this.sceneViewControl.getSceneControl();
    }

    /**
     * 内容视图的图标
     *
     * @return 图标
     */
    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_SceneView_16.png"));
    }

    /**
     * 内容视图的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "三维视图";
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
        return this.sceneViewControl;
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
        // 从 WorkSpace 中清除 SceneControl
        WorkspacePanel.getWorkspacePanel(this.application).clearSceneControl(this.sceneViewControl.getSceneControl());
    }

    /**
     * 内容视图被创建时引发的事件
     *
     * @param app 框架的宿主对象
     */
    @Override
    public void onCreate(IApplication app) {
        this.application = app;
        this.sceneViewControl = new SceneViewControl(this.application);
//        HistoryOperate history = HistoryOperate.GetInstance(this.mvCtrl.sceneControl1);
//        history.EndOneActionEvent3D += new HistoryOperate.Event3DHand(HistorySingle_EndOneActionEvent3D);
//        history.EndRedoUndoEvent3D += new HistoryOperate.Event3DHand(HistorySingle_EndRedoUndoEvent3D);
    }
}
