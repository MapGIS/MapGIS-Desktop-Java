package com.zondy.mapgis.workspace.menuitem;

import com.zondy.mapgis.base.SortLayers;
import com.zondy.mapgis.base.XString;
import com.zondy.mapgis.filedialog.GDBOpenFileDialog;
import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.map.LayerState;
import com.zondy.mapgis.scene.*;
import com.zondy.mapgis.utilities.UtilityTool;
import com.zondy.mapgis.workspace.engine.ISingleMenuItem;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Optional;

/**
 * 添加模型图
 *
 * @author cxy
 * @date 2019/11/18
 */
public class AddModelLayerMenuItem implements ISingleMenuItem {
    private IWorkspace workspace;

    /**
     * 点击菜单项
     *
     * @param item 文档项
     */
    @Override
    public void onClick(DocumentItem item) {
        GDBOpenFileDialog gdbShell = new GDBOpenFileDialog();
        gdbShell.setFilter("三维简单要素类|sfclss;sfclse");
        gdbShell.setMultiSelect(true);

        Optional<String[]> optional = gdbShell.showAndWait();
        if (optional != null && optional.isPresent()) {
            String[] fileNames = optional.get();
            if (fileNames.length > 0) {
                this.workspace.beginUpdateTree();
                ArrayList<Map3DLayer> layerLst = new ArrayList<>();
                for (String url : fileNames) {
                    if (!url.isEmpty()) {
                        ModelLayer mapLayer = null;
                        if (url.contains("/sfcls/")) {
                            //三维简单要素类
                            mapLayer = new ModelLayer();
                            mapLayer.setURL(url);
                        }
                        if (mapLayer != null && mapLayer.connectData()) {
                            try {
                                mapLayer.setName(UtilityTool.autoBreakString(url.substring(url.lastIndexOf('/') + 1), XString.maxLengthOfMapLayerName, ""));
                            } catch (Exception ex) {
                                mapLayer.setName(url.substring(url.lastIndexOf('/') + 1));
                            }
                            mapLayer.setState(LayerState.Visible);
                            if (item instanceof Scene) {
                                ((Scene) item).addLayer(mapLayer);
                            } else if (item instanceof Group3DLayer) {
                                ((Group3DLayer) item).addLayer(mapLayer);
                            }
                            SortLayers.sortTargetLayer(mapLayer);
                            mapLayer.setVisible(true);
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
        return new Image(getClass().getResourceAsStream("/Png_AddModelLayer_16.png"));
    }

    /**
     * 获取命令按钮的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "添加模型图";
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
