package com.zondy.mapgis.rastereditor.plugin.command;

import com.zondy.mapgis.base.Window;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICommand;
import com.zondy.mapgis.rastereditor.dialogs.TerrainAnalysisDialog;
import javafx.scene.image.Image;

/**
 * 表面分析
 */
public class TerriainAnalysisCommand implements ICommand {
    private IApplication app;

    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_RasTerrainFacter_16.png"));
    }

    @Override
    public String getCaption() {
        return "地形因子分析";
    }

    @Override
    public String getCategory() {
        return "栅格分析";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getMessage() {
        return "地形因子分析";
    }

    @Override
    public String getTooltip() {
        return "地形因子分析";
    }

    @Override
    public void onClick() {
        TerrainAnalysisDialog dialog = new TerrainAnalysisDialog(this.app.getDocument());
        dialog.initOwner(Window.primaryStage);
        dialog.show();
    }

    @Override
    public void onCreate(IApplication app) {
        if (app != null){
            this.app = app;
        } else {
            System.out.println("app == null");
        }
    }
}
