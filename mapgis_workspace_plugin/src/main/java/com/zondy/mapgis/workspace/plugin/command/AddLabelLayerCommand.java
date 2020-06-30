package com.zondy.mapgis.workspace.plugin.command;

import com.zondy.mapgis.base.SortLayers;
import com.zondy.mapgis.base.XString;
import com.zondy.mapgis.controls.SceneControl;
import com.zondy.mapgis.filedialog.GDBOpenFileDialog;
import com.zondy.mapgis.map.LayerState;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICommand;
import com.zondy.mapgis.pluginengine.plugin.ISceneContentsView;
import com.zondy.mapgis.scene.LabelLayer;
import com.zondy.mapgis.scene.Map3DLayer;
import com.zondy.mapgis.scene.Scene;
import com.zondy.mapgis.utilities.UtilityTool;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Optional;

/**
 * 添加注记图层
 *
 * @author cxy
 * @date 2019/11/29
 */
public class AddLabelLayerCommand implements ICommand {
    private IApplication application;
    private IWorkspace workspace;

    /**
     * 命令按钮的图标
     *
     * @return 图标
     */
    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_AddLabelLayerCmd_16.png"));
    }

    /**
     * 命令按钮的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "添加注记层";
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
        return "添加注记层";
    }

    /**
     * 鼠标停留在命令按钮上时弹出的提示文本
     *
     * @return 提示文本
     */
    @Override
    public String getTooltip() {
        return "添加注记层";
    }

    /**
     * 命令按钮被单击时引发的事件
     */
    @Override
    public void onClick() {
        if (this.application.getActiveContentsView() != null && this.application.getActiveContentsView() instanceof ISceneContentsView) {
            SceneControl sceneControl = ((ISceneContentsView) this.application.getActiveContentsView()).getSceneControl();
            Scene scene = sceneControl.getMapGISScene();
            GDBOpenFileDialog gdbShell = new GDBOpenFileDialog();
            gdbShell.setFilter("注记类|acls");
            gdbShell.setMultiSelect(true);
            Optional<String[]> optional = gdbShell.showAndWait();
            if (optional != null && optional.isPresent()) {
                String[] fileNames = optional.get();
                if (fileNames.length > 0) {
                    this.workspace.beginUpdateTree();
                    ArrayList<Map3DLayer> layerLst = new ArrayList<>();
                    for (String url : fileNames) {
                        if (!url.isEmpty()) {
                            Map3DLayer g3dLayer = null;
                            if (url.contains("/acls/")) {
                                g3dLayer = new LabelLayer();//注记类
                                g3dLayer.setURL(url);
                            }
                            if (g3dLayer != null && g3dLayer.connectData()) {
                                try {
                                    g3dLayer.setName(UtilityTool.autoBreakString(url.substring(url.lastIndexOf('/') + 1), XString.maxLengthOfMapLayerName, ""));
                                } catch (Exception ex) {
                                    g3dLayer.setName(url.substring(url.lastIndexOf('/') + 1));
                                }
                                g3dLayer.setState(LayerState.Visible);
                                scene.addLayer(g3dLayer);
                                SortLayers.sortTargetLayer(g3dLayer);
                                layerLst.add(g3dLayer);
                            }
                        }
                    }
                    this.workspace.endUpdateTree();
                }
            }
//        gdbShell.Dispose();
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
        this.workspace = app.getWorkSpace();
        this.application.getPluginContainer().addContentsViewChangedListener(contentsViewChangedEvent ->
                this.application.getPluginContainer().setPluginEnable(this, contentsViewChangedEvent.getContentsView() instanceof ISceneContentsView)
        );
    }
}
