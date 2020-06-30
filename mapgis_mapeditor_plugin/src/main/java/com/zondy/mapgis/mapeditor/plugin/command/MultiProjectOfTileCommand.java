package com.zondy.mapgis.mapeditor.plugin.command;

import com.zondy.mapgis.base.Window;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICommand;
import com.zondy.mapgis.mapeditor.projecttransform.MultiProjectOfTileDialog;
import javafx.scene.image.Image;

/**
 * 瓦片数据批量投影
 */
public class MultiProjectOfTileCommand implements ICommand {
    private IApplication app;

    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_TileProject_16.png"));
    }

    @Override
    public String getCaption() {
        return "瓦片批量投影";
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
        return "瓦片批量投影";
    }

    @Override
    public String getTooltip() {
        return "瓦片批量投影";
    }

    @Override
    public void onClick() {
        MultiProjectOfTileDialog dialog = new MultiProjectOfTileDialog();
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
