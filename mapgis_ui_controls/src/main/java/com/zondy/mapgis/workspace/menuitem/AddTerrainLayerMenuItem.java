package com.zondy.mapgis.workspace.menuitem;

import com.zondy.mapgis.base.SortLayers;
import com.zondy.mapgis.base.XPath;
import com.zondy.mapgis.base.XString;
import com.zondy.mapgis.filedialog.GDBOpenFileDialog;
import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.map.LayerState;
import com.zondy.mapgis.scene.Group3DLayer;
import com.zondy.mapgis.scene.Map3DLayer;
import com.zondy.mapgis.scene.Scene;
import com.zondy.mapgis.scene.TerrainLayer;
import com.zondy.mapgis.utilities.UtilityTool;
import com.zondy.mapgis.workspace.engine.ISingleMenuItem;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

/**
 * 添加地形图
 *
 * @author cxy
 * @date 2019/11/18
 */
public class AddTerrainLayerMenuItem implements ISingleMenuItem {
    private IWorkspace workspace;

    /**
     * 点击菜单项
     *
     * @param item 文档项
     */
    @Override
    public void onClick(DocumentItem item) {
        GDBOpenFileDialog gdbShell = new GDBOpenFileDialog();
        gdbShell.setFilter("栅格数据集|ras|影像文件|*.msi;*.img;*.tif;*.pix;*.jpg;*.bmp;*.gif;*.png;*.jp2");
        gdbShell.setMultiSelect(true);
        Optional<String[]> optional = gdbShell.showAndWait();
        if (optional != null && optional.isPresent()) {
            String[] fileNames = optional.get();
            if (fileNames.length > 0) {
                this.workspace.beginUpdateTree();
                ArrayList<Map3DLayer> layerLst = new ArrayList<>();
                for (String url : fileNames) {
                    if (!url.isEmpty()) {
                        Map3DLayer mapLayer = null;
                        File file = new File(url);
                        if (url.contains("/ras/")) {//栅格图层
                            mapLayer = new TerrainLayer();
                            mapLayer.setURL(url);
                        } else if (file.exists()) {
                            String ext = XPath.getExtension(url);
                            switch (ext.toLowerCase()) {
                                case ".msi":
                                case ".img":
                                case ".tif":
                                case ".pix":
                                case ".jpg":
                                case ".bmp":
                                case ".gif":
                                case ".png":
                                case ".jp2": {
                                    mapLayer = new TerrainLayer();
                                    mapLayer.setURL("file:///" + url);
                                    break;
                                }
                                default:
                                    break;
                            }
                        }
                        if (mapLayer != null && mapLayer.connectData()) {
                            if (file.exists()) {
                                try {
                                    mapLayer.setName(UtilityTool.autoBreakString(XPath.getNameWithoutExt(url), XString.maxLengthOfMapLayerName, ""));
                                } catch (Exception ex) {
                                    mapLayer.setName(url.substring(url.lastIndexOf(File.pathSeparator) + 1));
                                }
                            } else {
                                try {
                                    mapLayer.setName(UtilityTool.autoBreakString(url.substring(url.lastIndexOf('/') + 1), XString.maxLengthOfMapLayerName, ""));
                                } catch (Exception ex) {
                                    mapLayer.setName(url.substring(url.lastIndexOf('/') + 1));
                                }
                            }
                            mapLayer.setState(LayerState.Visible);
                            if (item instanceof Scene) {
                                ((Scene) item).addLayer(mapLayer);
                            } else if (item instanceof Group3DLayer) {
                                ((Group3DLayer) item).addLayer(mapLayer);
                            }
                            SortLayers.sortTargetLayer(mapLayer);
                            layerLst.add(mapLayer);
                        }
                    }
                }
                this.workspace.endUpdateTree();
            }
        }
//        gdbShell.Dispose();
    }

    /**
     * 获取菜单项图标
     *
     * @return 菜单项图标
     */
    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_AddTerrainLayer_16.png"));
    }

    /**
     * 获取命令按钮的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "添加地形图";
    }

    /**
     * 获取命令按钮是否可用
     *
     * @return true/false
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * 获取命令按钮是否可见
     *
     * @return true/false
     */
    @Override
    public boolean isVisible() {
        return true;
    }

    /**
     * 获取命令按钮是否选中
     *
     * @return true/false
     */
    @Override
    public boolean isChecked() {
        return false;
    }

    /**
     * 获取是否启用分割符
     *
     * @return true/false
     */
    @Override
    public boolean isBeginGroup() {
        return false;
    }

    /**
     * 创建后事件
     *
     * @param ws 工作空间引擎
     */
    @Override
    public void onCreate(IWorkspace ws) {
        workspace = ws;
    }
}
