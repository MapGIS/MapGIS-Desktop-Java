package com.zondy.mapgis.mapeditor.plugin.command;

import com.zondy.mapgis.base.Window;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.mapeditor.common.CommonFunctions;
import com.zondy.mapgis.mapeditor.dialogs.SpatialQueryDialog;
import com.zondy.mapgis.mapeditor.enums.StateEnum;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICommand;
import com.zondy.mapgis.pluginengine.plugin.IMapContentsView;
import javafx.scene.image.Image;

import java.util.EnumSet;

public class SpatialQueryCommand implements ICommand {
    private IApplication app;

    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_SpatialQuery_16.png"));
    }

    @Override
    public String getCaption() {
        return "空间查询";
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
        return "空间查询";
    }

    @Override
    public String getTooltip() {
        return "空间查询";
    }

    @Override
    public void onClick() {
        MapControl mapControl = null;
        if (this.app.getActiveContentsView() != null && this.app.getActiveContentsView() instanceof IMapContentsView) {
            mapControl = ((IMapContentsView) this.app.getActiveContentsView()).getMapControl();
            SpatialQueryDialog dialog = new SpatialQueryDialog(mapControl);
            dialog.initOwner(Window.primaryStage);
            dialog.show();
        }
    }

    @Override
    public void onCreate(IApplication app) {
        if (app != null) {
            this.app = app;
            this.app.getStateManager().addStateChangedListener(stateChangedEvent -> {
                CommonFunctions.setPluginEnable(this.app, this, stateChangedEvent, EnumSet.of(StateEnum.LINE_VISIBLE, StateEnum.PNT_VISIBLE,
                        StateEnum.ANN_VISIBLE, StateEnum.POLYGON_VISIBLE), false);
            });
        }
    }
}
