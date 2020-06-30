package com.zondy.mapgis.mapeditor.plugin.command;

import com.zondy.mapgis.base.Window;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICommand;
import com.zondy.mapgis.mapeditor.projecttransform.SimplePntProjectDialog;
import javafx.scene.image.Image;

/**
 * 单点投影
 */
public class SimplePntProjectCommand implements ICommand{
    private IApplication app;

    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_PoingProject_32.png"));
    }

    @Override
    public String getCaption() {
        return "单点投影";
    }

    @Override
    public String getCategory() {
        return "工具";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getMessage() {
        return "单点投影";
    }

    @Override
    public String getTooltip() {
        return "单点投影";
    }

    @Override
    public void onClick() {
        SimplePntProjectDialog dialog = new SimplePntProjectDialog();
        dialog.initOwner(Window.primaryStage);
        dialog.show();
    }

    @Override
    public void onCreate(IApplication app) {
        if (app != null){
            this.app = app;
        }
        else {
            System.out.println("app == null");
        }
    }
}
