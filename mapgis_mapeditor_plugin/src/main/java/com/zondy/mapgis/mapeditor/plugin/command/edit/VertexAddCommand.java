package com.zondy.mapgis.mapeditor.plugin.command.edit;

import com.zondy.mapgis.controls.EditType;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.mapeditor.common.CommonFunctions;
import com.zondy.mapgis.mapeditor.enums.StateEnum;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICommand;
import com.zondy.mapgis.workspace.plugin.MapView;
import javafx.scene.image.Image;

import java.util.EnumSet;

/**
 * @author CR
 * @file AddDotCommand.java
 * @brief 线上加点/区边界加点
 * @create 2020-06-10.
 */
public class VertexAddCommand implements ICommand {
    private IApplication application;

    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_AddDot_16.png"));
    }

    @Override
    public String getCaption() {
        return "添加顶点";
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
        return "添加顶点";
    }

    @Override
    public String getTooltip() {
        return "线上加点/区边界加点";
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
                enable = mapControl.getEditType().equals(EditType.EDITVERTEX);
            }
            this.application.getPluginContainer().setPluginEnable(this, enable);
        });
    }
}
