package com.zondy.mapgis.mapeditor.plugin.command.edit;

import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.edit.event.SketchGeometryChangedEvent;
import com.zondy.mapgis.edit.tool.SketchTool;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICheckCommand;
import com.zondy.mapgis.workspace.plugin.MapView;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.Image;

/**
 * @author CR
 * @file EditVertexCommand.java
 * @brief 编辑顶点工具
 * @create 2020-06-11.
 */
public class VertexEditCommand implements ICheckCommand {
    private IApplication application;
    private BooleanProperty checked = new SimpleBooleanProperty(false);

    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_EditDot_16.png"));
    }

    @Override
    public String getCaption() {
        return "编辑顶点";
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
        return "编辑顶点";
    }

    @Override
    public String getTooltip() {
        return "编辑顶点";
    }

    @Override
    public void onSelectedChanged(boolean isChecked) {
        this.checked.set(isChecked);

        if (isChecked) {
            if (this.application.getActiveContentsView() instanceof MapView) {
                MapControl mapControl = ((MapView) this.application.getActiveContentsView()).getMapControl();
                if (mapControl.getSelectedResults().size() == 1) {
                    mapControl.getSketchEditor().startEdit(mapControl.getSelectedResults().get(0));
                } else if (mapControl.getSelectedSketchGeometries().size() == 1) {
                    mapControl.getSketchEditor().startEdit(mapControl.getSelectedSketchGeometries().get(0));
                }
            }
        }
    }

    @Override
    public void onCreate(IApplication app) {
        this.application = app;
        this.application.getStateManager().addStateChangedListener(stateChangedEvent -> {
            boolean enable = false;
            if (this.application.getActiveContentsView() instanceof MapView) {
                MapControl mapControl = ((MapView) this.application.getActiveContentsView()).getMapControl();
                enable = mapControl.getSelectedResults().size() + mapControl.getSelectedSketchGeometries().size() == 1;
            }
            this.application.getPluginContainer().setPluginEnable(this, enable);
        });
    }
}
