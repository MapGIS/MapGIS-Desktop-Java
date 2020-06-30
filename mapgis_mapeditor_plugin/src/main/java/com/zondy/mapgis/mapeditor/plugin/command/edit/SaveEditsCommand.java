package com.zondy.mapgis.mapeditor.plugin.command.edit;

import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICommand;
import com.zondy.mapgis.pluginengine.plugin.IContentsView;
import com.zondy.mapgis.workspace.plugin.MapView;
import javafx.scene.image.Image;

/**
 * @author CR
 * @file SaveEditsCommand.java
 * @brief 保存编辑
 * @create 2020-06-05.
 */
public class SaveEditsCommand implements ICommand {
    private IApplication application;

    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_SaveEdits_32.png"));
    }

    @Override
    public String getCaption() {
        return "保存编辑";
    }

    @Override
    public String getCategory() {
        return "编辑";
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getMessage() {
        return "保存编辑";
    }

    @Override
    public String getTooltip() {
        return "保存编辑";
    }

    @Override
    public void onClick() {
        IContentsView cv = this.application.getActiveContentsView();
        if (cv instanceof MapView) {
            ((MapView) cv).getMapViewControl().saveEdits();
        }
    }

    @Override
    public void onCreate(IApplication app) {
        this.application = app;
        this.application.getStateManager().addStateChangedListener(stateChangedEvent -> {
            boolean enable = false;
            if (this.application.getActiveContentsView() instanceof MapView) {
                MapControl mapControl = ((MapView) this.application.getActiveContentsView()).getMapControl();
                enable = mapControl.isEditing();
            }
            this.application.getPluginContainer().setPluginEnable(this, enable);
        });
    }
}
