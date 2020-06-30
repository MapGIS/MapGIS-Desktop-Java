package com.zondy.mapgis.mapeditor.plugin.command;

import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICommand;
import com.zondy.mapgis.pluginengine.plugin.IMapContentsView;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;

public class MultiPolygonClipCommand implements ICommand {
    private IApplication app;

    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_MultiPolygonClip_16.png"));
    }

    @Override
    public String getCaption() {
        return "多边形裁剪";
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
        return "多边形裁剪";
    }

    @Override
    public String getTooltip() {
        return "多边形裁剪";
    }

    @Override
    public void onClick() {
        if (this.app.getActiveContentsView() != null && this.app.getActiveContentsView() instanceof IMapContentsView) {
            MapControl ctr = ((IMapContentsView) this.app.getActiveContentsView()).getMapControl();
            // TODO: 待添加
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("多边形裁剪");
            alert.showAndWait();
//            ClipByPolygonTool tool = new ClipByPolygonTool(ctr);
//            tool.Clip();
        }
    }

    @Override
    public void onCreate(IApplication app) {
        this.app = app;
        this.app.getStateManager().addStateChangedListener(stateChangedEvent -> {
//            CommonFunctions.setPluginEnable(this.app, this, stateChangedEvent, EnumSet.of(StateEnum.PNT_VISIBLE, StateEnum.LINE_VISIBLE,
//                    StateEnum.POLYGON_VISIBLE, StateEnum.ANN_VISIBLE, StateEnum.RASTERDATASET_VISIBLE, StateEnum.RASTERCATALOG_VISIBLE), false);
        });
    }
}
