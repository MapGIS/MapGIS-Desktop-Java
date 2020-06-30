package com.zondy.mapgis.mapeditor.plugin.command.edit;

import com.zondy.mapgis.controls.EditType;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.mapeditor.plugin.dockwindow.CreateFeatureDW;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICheckCommand;
import com.zondy.mapgis.pluginengine.plugin.IDockWindow;
import com.zondy.mapgis.workspace.plugin.MapView;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.Image;

/**
 * @author CR
 * @file InputCommand.java
 * @brief 输入工具
 * @create 2020-06-10.
 */
public class InputCommand implements ICheckCommand {
    private IApplication application;
    private MapControl mapControl;
    private BooleanProperty checked = new SimpleBooleanProperty(false);

    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_Input_16.png"));
    }

    @Override
    public String getCaption() {
        return "输入";
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
        return "输入";
    }

    @Override
    public String getTooltip() {
        return "输入";
    }

    @Override
    public void onSelectedChanged(boolean isChecked) {
        this.checked.set(isChecked);
        if (this.mapControl == null || this.mapControl.getSketchEditor() == null) {
            return;
        }

        IDockWindow dockWindow = this.application.getPluginContainer().getDockWindows().getOrDefault(CreateFeatureDW.class.getName(), null);
        if (isChecked) {
            if (dockWindow == null) {
                dockWindow = this.application.getPluginContainer().createDockWindow(CreateFeatureDW.class.getName());
            }
            this.application.getPluginContainer().activeDockWindow(dockWindow);
        } else {
            if (dockWindow != null && this.mapControl.getEditType() != EditType.INPUT) {
                dockWindow.onDestroy();
            }
        }
        this.application.getStateManager().onStateChanged(this);
    }

    @Override
    public void onCreate(IApplication app) {
        this.application = app;
        this.application.getStateManager().addStateChangedListener(stateChangedEvent -> {
            boolean enable = false;
            EditType editType = EditType.NONE;
            if (this.application.getActiveContentsView() instanceof MapView) {
                this.mapControl = ((MapView) this.application.getActiveContentsView()).getMapControl();
                enable = this.mapControl.isEditing();
                editType = this.mapControl.getEditType();
            }
            this.application.getPluginContainer().setPluginEnable(this, enable);
            this.application.getPluginContainer().setCommandChecked(this, editType == EditType.INPUT);
        });
    }
}
