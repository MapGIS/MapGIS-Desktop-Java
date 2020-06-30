package com.zondy.mapgis.mapeditor.plugin.command.edit;

import com.zondy.mapgis.controls.EditType;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICheckCommand;
import com.zondy.mapgis.pluginengine.plugin.ICommand;
import com.zondy.mapgis.workspace.plugin.MapView;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.Image;

/**
 * @author CR
 * @file MoveDotCommand.java
 * @brief 线上移点/区边界移点
 * @create 2020-06-10.
 */
public class VertexMoveCommand implements ICheckCommand {
    private IApplication application;
    private BooleanProperty checked = new SimpleBooleanProperty(false);

    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_MoveDot_16.png"));
    }

    @Override
    public String getCaption() {
        return "移动顶点";
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
    public boolean isChecked() {
        return checked.get();
    }

    @Override
    public String getMessage() {
        return "移动顶点";
    }

    @Override
    public String getTooltip() {
        return "线上移点/区边界移点";
    }

    @Override
    public void onSelectedChanged(boolean isChecked) {
        this.checked.set(isChecked);
    }

    @Override
    public void onCreate(IApplication app) {
        this.application = app;
        this.application.getStateManager().addStateChangedListener(stateChangedEvent -> {
            boolean enable = false;
            if (this.application.getActiveContentsView() instanceof MapView) {
                MapControl mapControl = ((MapView) this.application.getActiveContentsView()).getMapControl();
                enable = mapControl.getEditType().equals(EditType.EDITVERTEX);
            }
            this.application.getPluginContainer().setPluginEnable(this, enable);
        });
    }
}
