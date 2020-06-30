package com.zondy.mapgis.mapeditor.plugin.command;

import com.zondy.mapgis.base.Window;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.mapeditor.dialogs.BufferAnalysisDialog;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICommand;
import com.zondy.mapgis.pluginengine.plugin.IMapContentsView;
import javafx.scene.image.Image;

public class BufferAnalysisCommand implements ICommand {
    private IApplication app;

    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_BufferAnalysis_16.png"));
    }

    @Override
    public String getCaption() {
        return "缓冲分析";
    }

    @Override
    public String getCategory() {
        return "通用编辑";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getMessage() {
        return "缓冲分析";
    }

    @Override
    public String getTooltip() {
        return "缓冲分析";
    }

    @Override
    public void onClick() {
        MapControl mapControl = null;
        if (this.app.getActiveContentsView() != null && this.app.getActiveContentsView() instanceof IMapContentsView) {
            mapControl = ((IMapContentsView) this.app.getActiveContentsView()).getMapControl();
        }
        BufferAnalysisDialog dialog = new BufferAnalysisDialog(this.app.getDocument(), mapControl);
        dialog.initOwner(Window.primaryStage);
        dialog.show();
    }

    @Override
    public void onCreate(IApplication app) {
        if (app != null){
            this.app = app;
        }
    }
}
