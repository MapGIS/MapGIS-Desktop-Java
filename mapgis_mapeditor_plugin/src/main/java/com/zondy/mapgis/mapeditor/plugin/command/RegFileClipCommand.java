package com.zondy.mapgis.mapeditor.plugin.command;

import com.zondy.mapgis.base.Window;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.mapeditor.common.CommonFunctions;
import com.zondy.mapgis.mapeditor.dialogs.ClipDialog;
import com.zondy.mapgis.mapeditor.enums.StateEnum;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICommand;
import com.zondy.mapgis.pluginengine.plugin.IMapContentsView;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.EnumSet;

public class RegFileClipCommand implements ICommand {
    private IApplication app;

    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_RegFileClip_16.png"));
    }

    @Override
    public String getCaption() {
        return "区文件裁剪";
    }

    @Override
    public String getCategory() {
        return "通用编辑";
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getMessage() {
        return "区文件裁剪";
    }

    @Override
    public String getTooltip() {
        return "区文件裁剪";
    }

    @Override
    public void onClick() {
        if (this.app.getActiveContentsView() != null && this.app.getActiveContentsView() instanceof IMapContentsView) {
            MapControl mapControl = ((IMapContentsView) this.app.getActiveContentsView()).getMapControl();
            boolean isToolCan = false;
//            mapControl.getMap().getEditLayer(EditLayerType.All | EditLayerType.RasterAll, SelectLayerControl.Visible);
//            List<MapLayer> editLayerList = this.mapControl.ActiveMap.GetEditLayer(EditLayerType.All | EditLayerType.RasterAll, SelectLayerControl.Visible);
            if (mapControl != null && mapControl.getMap() != null) {
                ClipDialog dialog = new ClipDialog(mapControl, null);
                dialog.initOwner(Window.primaryStage);
                dialog.show();
            }
            if (!isToolCan) {
//                MessageBox.information("当前地图中没有可裁剪的图层!");
//                mapControl.SetBasTool(null);
            }
        }
    }

    @Override
    public void onCreate(IApplication app) {
        if (app != null) {
            this.app = app;
            this.app.getStateManager().addStateChangedListener(stateChangedEvent -> {
                CommonFunctions.setPluginEnable(this.app, this, stateChangedEvent, EnumSet.of(StateEnum.PNT_VISIBLE, StateEnum.LINE_VISIBLE,
                        StateEnum.POLYGON_VISIBLE, StateEnum.ANN_VISIBLE, StateEnum.RASTERDATASET_VISIBLE, StateEnum.RASTERCATALOG_VISIBLE), false);
            });
        }
    }
}
