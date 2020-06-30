package com.zondy.mapgis.mapeditor.plugin.command;

import com.zondy.mapgis.base.Window;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICommand;
import com.zondy.mapgis.mapeditor.projecttransform.ElpTransSettingDialog;
import javafx.scene.image.Image;

/**
 * 地理转换参数设置
 */
public class ElpTransParamSettingCommand implements ICommand {
    private IApplication app;

    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_ProjectSetting_32.png"));
    }

    @Override
    public String getCaption() {
        return "地理转换参数设置";
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
        return "地理转换参数设置";
    }

    @Override
    public String getTooltip() {
        return "地理转换参数设置";
    }

    @Override
    public void onClick() {
        ElpTransSettingDialog dialog = new ElpTransSettingDialog();
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
