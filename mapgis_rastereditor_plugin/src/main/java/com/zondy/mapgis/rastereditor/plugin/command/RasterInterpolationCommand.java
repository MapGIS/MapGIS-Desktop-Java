package com.zondy.mapgis.rastereditor.plugin.command;

import com.zondy.mapgis.base.Window;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICommand;
import com.zondy.mapgis.rastereditor.dialogs.FormulaCaculateDialog;
import com.zondy.mapgis.rastereditor.dialogs.RasterInterpolationDialog;
import javafx.scene.image.Image;

/**
 * Created by Administrator on 2020/6/8.
 */
public class RasterInterpolationCommand implements ICommand {
    private IApplication app;

    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_RasInterpolation_16.png"));
    }

    @Override
    public String getCaption() {
        return "离散数据网格化";
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
        return "离散数据网格化";
    }

    @Override
    public String getTooltip() {
        return "离散数据网格化";
    }

    @Override
    public void onClick() {
        RasterInterpolationDialog dialog = new RasterInterpolationDialog(this.app.getDocument());
        dialog.initOwner(Window.primaryStage);
        dialog.show();
    }

    @Override
    public void onCreate(IApplication app) {
        if (app != null){
            this.app = app;
        }else{
            System.out.println("app == null");
        }
    }
}
