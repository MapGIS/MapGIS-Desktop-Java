package com.zondy.mapgis.workspace.plugin.command;

import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICommand;
import com.zondy.mapgis.workspace.plugin.MapView;
import javafx.scene.image.Image;

public class ZoomOutCommand implements ICommand {
    private IApplication application;

    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_ZoomOut_16.png"));
    }

    @Override
    public String getCaption() {
        return "缩小";
    }

    @Override
    public String getCategory() {
        return "常用工具";
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getMessage() {
        return "缩小";
    }

    @Override
    public String getTooltip() {
        return "缩小";
    }

    @Override
    public void onClick() {
        MapView mapView = (MapView) this.application.getActiveContentsView();
        if (mapView != null) {
            MapControl mapControl = mapView.getMapControl();
            if (mapControl != null) {
                mapControl.zoomOut();
            }
        }
    }

    @Override
    public void onCreate(IApplication app) {
        this.application = app;
        this.application.getStateManager().addStateChangedListener(stateChangedEvent -> {
            this.application.getPluginContainer().setPluginEnable(this, this.application.getActiveContentsView() instanceof MapView);
        });
    }
}
