package com.zondy.mapgis.workspace.plugin.command;

import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICommand;
import javafx.scene.image.Image;

public class RedoCommand implements ICommand {
    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_Redo_16.png"));
    }

    @Override
    public String getCaption() {
        return "恢复";
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
        return "恢复";
    }

    @Override
    public String getTooltip() {
        return "恢复";
    }

    @Override
    public void onClick() {

    }

    @Override
    public void onCreate(IApplication app) {

    }
}
