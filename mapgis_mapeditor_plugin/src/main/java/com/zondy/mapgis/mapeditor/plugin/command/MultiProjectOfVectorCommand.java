package com.zondy.mapgis.mapeditor.plugin.command;

import com.zondy.mapgis.base.Window;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICommand;
import com.zondy.mapgis.mapeditor.projecttransform.MultiProjectOfVectorDialog;
import javafx.scene.image.Image;

/**
 * 矢量数据批量投影
 */
public class MultiProjectOfVectorCommand implements ICommand{
    private IApplication app;

    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_VectorProject_16.png"));
    }

    @Override
    public String getCaption() {
        return "矢量批量投影";
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
        return "矢量批量投影";
    }

    @Override
    public String getTooltip() {
        return "矢量批量投影";
    }

    @Override
    public void onClick() {
        MultiProjectOfVectorDialog dialog = new MultiProjectOfVectorDialog();
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
