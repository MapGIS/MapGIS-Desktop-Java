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
 * 添加注记层
 *
 * @author cxy
 * @date 2019/11/18
 */
public class AddLabelLayerMenuItem implements ISingleMenuItem {
    private IWorkspace workspace;

    /**
     * 点击菜单项
     *
     * @param item 文档项
     */
    @Override
    public void onClick(DocumentItem item) {
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
                            if (item instanceof Scene) {
                                ((Scene) item).addLayer(g3dLayer);
                            } else if (item instanceof Group3DLayer) {
                                ((Group3DLayer) item).addLayer(g3dLayer);
                            }
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

    /**
     * 获取菜单项图标
     *
     * @return 菜单项图标
     */
    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_AddLabelLayer_16.png"));
    }

    /**
     * 获取命令按钮的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "添加注记层";
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
