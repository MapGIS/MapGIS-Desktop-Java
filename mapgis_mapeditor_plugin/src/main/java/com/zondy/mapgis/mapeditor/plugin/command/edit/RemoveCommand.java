package com.zondy.mapgis.mapeditor.plugin.command.edit;

import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICheckCommand;
import com.zondy.mapgis.pluginengine.plugin.ICommand;
import com.zondy.mapgis.view.GraphicsOverlay;
import com.zondy.mapgis.view.SelectResult;
import com.zondy.mapgis.view.SketchGeometry;
import com.zondy.mapgis.workspace.plugin.MapView;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.Image;

/**
 * @author CR
 * @file RemoveCommand.java
 * @brief 移除
 * @create 2020-06-10.
 */
public class RemoveCommand implements ICommand {
    private IApplication application;

    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_Remove_16.png"));
    }

    @Override
    public String getCaption() {
        return "移除";
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
        return "移除";
    }

    @Override
    public String getTooltip() {
        return "移除选中图元";
    }

    @Override
    public void onClick() {
        MapView mapView = (MapView) this.application.getActiveContentsView();
        if (mapView != null) {
            MapControl mapControl = ((MapView) this.application.getActiveContentsView()).getMapControl();
            for (SelectResult selectResult : mapControl.getSelectedResults()) {
                SketchGeometry sketchGeometry = new SketchGeometry();
                sketchGeometry.setLayer(selectResult.getLayer());
                sketchGeometry.setObjID(selectResult.getObjID());
                sketchGeometry.setGeometry(selectResult.getGeometry());
                sketchGeometry.setDeleted(true);
                mapControl.getSketchGeometrys().add(sketchGeometry);
            }
            for (SketchGeometry sketchGeometry : mapControl.getSelectedSketchGeometries()) {
                sketchGeometry.setDeleted(true);//测试：这样改行吗
            }
            mapControl.getSketchGraphicsOverlay().getGraphics().clear();
            mapControl.refreshOverlay();
            mapControl.refreshWnd();
        }
    }

    @Override
    public void onCreate(IApplication app) {
        this.application = app;
        //this.application.getStateManager().addStateChangedListener(stateChangedEvent -> {
        //    boolean enable = false;
        //    if (this.application.getActiveContentsView() instanceof MapView) {
        //        MapControl mapControl = ((MapView) this.application.getActiveContentsView()).getMapControl();
        //        enable = mapControl.hasSelectedItemsToEdit();
        //    }
        //    this.application.getPluginContainer().setPluginEnable(this, enable);
        //});
    }
}
