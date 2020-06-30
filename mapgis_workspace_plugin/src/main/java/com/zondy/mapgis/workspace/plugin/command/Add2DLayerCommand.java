package com.zondy.mapgis.workspace.plugin.command;

import com.zondy.mapgis.base.SortLayers;
import com.zondy.mapgis.base.XPath;
import com.zondy.mapgis.base.XString;
import com.zondy.mapgis.controls.SceneControl;
import com.zondy.mapgis.filedialog.GDBOpenFileDialog;
import com.zondy.mapgis.map.LayerState;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICommand;
import com.zondy.mapgis.pluginengine.plugin.ISceneContentsView;
import com.zondy.mapgis.scene.*;
import com.zondy.mapgis.utilities.UtilityTool;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import javafx.scene.image.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

/**
 * 添加二维图层（三维场景）
 *
 * @author cxy
 * @date 2019/11/29
 */
public class Add2DLayerCommand implements ICommand {
    private IApplication application;
    private IWorkspace workspace;

    /**
     * 命令按钮的图标
     *
     * @return 图标
     */
    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_AddVectorLayerCmd_16.png"));
    }

    /**
     * 命令按钮的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "添加矢量图层";
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
        return "添加矢量图层";
    }

    /**
     * 鼠标停留在命令按钮上时弹出的提示文本
     *
     * @return 提示文本
     */
    @Override
    public String getTooltip() {
        return "添加矢量图层";
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
            gdbShell.setFilter("简单要素类、6x文件|sfclsp;sfclsl;sfclsr;*.wl;*.wt;*.wp");// "所有文件(*.*)|*.*|6x文件|*.wl;*.wt;*.wp";
            gdbShell.setMultiSelect(true);
            Optional<String[]> optional = gdbShell.showAndWait();
            if (optional != null && optional.isPresent()) {
                String[] fileNames = optional.get();
                if (fileNames.length > 0) {
                    this.workspace.beginUpdateTree();
                    ArrayList<Map3DLayer> layerLst = new ArrayList<>();
                    for (String url : fileNames) {
                        if (!url.isEmpty()) {
                            Vector3DLayer mapLayer = null;
                            File file = new File(url);

                            if (file.exists()) {
                                String ext = XPath.getExtension(url);
                                switch (ext.toLowerCase()) {
                                    case ".wt":
                                    case ".wl":
                                    case ".wp":
                                        mapLayer = new Vector3DLayer();
                                        mapLayer.setURL("file:///" + url);    //6x图层必须加file前缀才可以打开
                                        break;
                                    default:
                                        break;
                                }
                            } else if (url.indexOf("/sfcls/") > 0) {
                                //矢量图层
                                mapLayer = new Vector3DLayer();//简单要素类
                                mapLayer.setURL(url);
                            }
                            if (mapLayer != null && mapLayer.connectData()) {
                                if (file.exists()) {
                                    try {
                                        mapLayer.setName(UtilityTool.autoBreakString(XPath.getNameWithoutExt(url), XString.maxLengthOfMapLayerName, ""));
                                    } catch (Exception ex) {
                                        mapLayer.setName(url.substring(url.lastIndexOf(File.pathSeparator) + 1));
                                    }
                                    mapLayer.connectData();
                                } else {
                                    try {
                                        mapLayer.setName(UtilityTool.autoBreakString(url.substring(url.lastIndexOf('/') + 1), XString.maxLengthOfMapLayerName, ""));
                                    } catch (Exception ex) {
                                        mapLayer.setName(url.substring(url.lastIndexOf('/') + 1));
                                    }
                                }
                                mapLayer.setState(LayerState.Visible);
                                scene.addLayer(mapLayer);
                                SortLayers.sortTargetLayer(mapLayer);
                                layerLst.add(mapLayer);
                            }
                        }
                    }
                }
                this.workspace.endUpdateTree();
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
        this.application.getPluginContainer().addContentsViewChangedListener(contentsViewChangedEvent ->
                this.application.getPluginContainer().setPluginEnable(this, contentsViewChangedEvent.getContentsView() instanceof ISceneContentsView)
        );
    }
}
