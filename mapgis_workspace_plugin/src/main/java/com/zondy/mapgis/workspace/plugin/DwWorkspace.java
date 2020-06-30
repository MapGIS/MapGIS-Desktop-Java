package com.zondy.mapgis.workspace.plugin;

import com.zondy.mapgis.workspace.WorkspacePanel;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.enums.DockingStyleEnum;
import com.zondy.mapgis.pluginengine.plugin.IDockWindow;
import javafx.scene.Node;
import javafx.scene.image.Image;

/**
 * @author cxy
 * @date 2019/09/18
 */
public class DwWorkspace implements IDockWindow {
    private IApplication app;       // 当前应用程序框架
    private WorkspacePanel workspacePanel;  // 工作空间树的一个单实例对象

    public WorkspacePanel getWorkSpaceHelp() {
        return workspacePanel;
    }

    //region IDockWindow 成员

    @Override
    public Image getBitmap() {
        return null;
    }

    @Override
    public String getCaption() {
        return "工作空间";
    }

    @Override
    public Node getChildHWND() {
        return workspacePanel.getWorkspaceTree();
    }

    @Override
    public DockingStyleEnum getDefaultDock() {
        return DockingStyleEnum.LEFT;
    }

    @Override
    public boolean isInitCreate() {
        return true;
    }

    @Override
    public void onActive(boolean isActive) {

    }

    @Override
    public void onCreate(IApplication app) {
        this.app = app;
        this.workspacePanel = WorkspacePanel.getWorkspacePanel(this.app);

    }

    @Override
    public void onDestroy() {

    }

    //endregion
}
