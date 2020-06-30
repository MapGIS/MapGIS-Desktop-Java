package com.zondy.mapgis.mapeditor.plugin.command.edit;

import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICommand;
import com.zondy.mapgis.workspace.plugin.MapView;
import javafx.scene.image.Image;

/**
 * @author CR
 * @file MoveCommand.java
 * @brief 移动
 * @create 2020-06-10.
 */
public class MoveCommand implements ICommand
{  private IApplication application;

    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_Move1_16.png"));
    }

    @Override
    public String getCaption() {
        return "移动";
    }

    @Override
    public String getCategory() {
        return "编辑";
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    //@Override
    //public boolean isChecked() {
    //    return true;
    //}

    @Override
    public String getMessage() {
        return "移动";
    }

    @Override
    public String getTooltip() {
        return "移动选中图元";
    }

    @Override
    public void onClick() {

    }

    @Override
    public void onCreate(IApplication app) {
        this.application = app;
        this.application.getStateManager().addStateChangedListener(stateChangedEvent -> {
            boolean enable = false;
            if (this.application.getActiveContentsView() instanceof MapView) {
                MapControl mapControl = ((MapView) this.application.getActiveContentsView()).getMapControl();
                enable = mapControl.hasSelectedItemsToEdit();
            }
            this.application.getPluginContainer().setPluginEnable(this, enable);
        });
    }
}