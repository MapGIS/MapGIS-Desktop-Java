package com.zondy.mapgis.workspace.plugin.command;

import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.map.Map;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICommand;
import com.zondy.mapgis.pluginengine.plugin.IMapContentsView;
import com.zondy.mapgis.workspace.StaticFunction;
import javafx.scene.image.Image;

/**
 * 添加服务图层
 *
 * @author cxy
 * @date 2019/11/29
 */
public class AddServerLayerCommand implements ICommand {
    private IApplication application;

    /**
     * 命令按钮的图标
     *
     * @return 图标
     */
    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_AddServerLayerCmd_16.png"));
    }

    /**
     * 命令按钮的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "添加服务图层";
    }

    /**
     * 命令按钮所属的类别
     *
     * @return 类别
     */
    @Override
    public String getCategory() {
        return "";
    }

    /**
     * 命令按钮是否可用
     *
     * @return true/false
     */
    @Override
    public boolean isEnabled() {
        return false;
    }

    /**
     * 鼠标移到命令按钮上时状态栏上显示的文本
     *
     * @return 显示的文本
     */
    @Override
    public String getMessage() {
        return "添加服务图层";
    }

    /**
     * 鼠标停留在命令按钮上时弹出的提示文本
     *
     * @return 提示文本
     */
    @Override
    public String getTooltip() {
        return "添加服务图层";
    }

    /**
     * 命令按钮被单击时引发的事件
     */
    @Override
    public void onClick() {
        if (this.application.getActiveContentsView() != null && this.application.getActiveContentsView() instanceof IMapContentsView) {
            MapControl mapControl = ((IMapContentsView) this.application.getActiveContentsView()).getMapControl();
            Map map = mapControl.getMap();
            StaticFunction.addServerLayer(this.application.getWorkSpace(), map);
        }
    }

    /**
     * 命令按钮被创建时引发的事件
     *
     * @param app 框架的宿主对象
     */
    @Override
    public void onCreate(IApplication app) {
        this.application = app;
        this.application.getPluginContainer().addContentsViewChangedListener(contentsViewChangedEvent ->
                application.getPluginContainer().setPluginEnable(this, contentsViewChangedEvent.getContentsView() instanceof IMapContentsView));
    }
}
