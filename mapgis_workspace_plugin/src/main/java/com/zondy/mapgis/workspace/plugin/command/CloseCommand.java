package com.zondy.mapgis.workspace.plugin.command;

import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICommand;
import com.zondy.mapgis.pluginengine.plugin.IContentsView;
import com.zondy.mapgis.pluginengine.plugin.IDockWindow;
import com.zondy.mapgis.workspace.WorkspacePanel;
import com.zondy.mapgis.workspace.plugin.DwWorkspace;
import javafx.scene.image.Image;

/**
 * 关闭
 *
 * @author cxy
 * @date 2019/11/29
 */
public class CloseCommand implements ICommand
{
    private IApplication application;

    /**
     * 命令按钮的图标
     *
     * @return 图标
     */
    @Override
    public Image getImage()
    {
        return new Image(getClass().getResourceAsStream("/Png_Close_16.png"));
    }

    /**
     * 命令按钮的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption()
    {
        return "关闭";
    }

    /**
     * 命令按钮所属的类别
     *
     * @return 类别
     */
    @Override
    public String getCategory()
    {
        return "常用工具";
    }

    /**
     * 命令按钮是否可用
     *
     * @return true/false
     */
    @Override
    public boolean isEnabled()
    {
        return true;
    }

    /**
     * 鼠标移到命令按钮上时状态栏上显示的文本
     *
     * @return 显示的文本
     */
    @Override
    public String getMessage()
    {
        return "关闭地图文档";
    }

    /**
     * 鼠标停留在命令按钮上时弹出的提示文本
     *
     * @return 提示文本
     */
    @Override
    public String getTooltip()
    {
        return "关闭地图文档";
    }

    /**
     * 命令按钮被单击时引发的事件
     */
    @Override
    public void onClick() {
        IDockWindow dw = this.application.getPluginContainer().getDockWindows().getOrDefault(DwWorkspace.class.getName(), null);
        if (dw instanceof DwWorkspace) {
            WorkspacePanel.getWorkspacePanel(this.application).getWorkspaceTree().closeDocument(false);
        }
    }

    /**
     * 命令按钮被创建时引发的事件
     *
     * @param app 框架的宿主对象
     */
    @Override
    public void onCreate(IApplication app)
    {
        this.application = app;
    }
}
