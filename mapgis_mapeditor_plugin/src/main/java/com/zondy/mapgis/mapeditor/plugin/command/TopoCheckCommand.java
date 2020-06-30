package com.zondy.mapgis.mapeditor.plugin.command;

import com.zondy.mapgis.base.Window;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.map.Document;
import com.zondy.mapgis.mapeditor.dialogs.TopoCheckDialog;
import com.zondy.mapgis.mapeditor.dialogs.TopoErrorResultsDialog;
import com.zondy.mapgis.mapeditor.plugin.dockwindow.TopoCheckedResultsDW;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICommand;
import com.zondy.mapgis.pluginengine.plugin.IDockWindow;
import com.zondy.mapgis.pluginengine.plugin.IMapContentsView;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;

public class TopoCheckCommand implements ICommand {
    private IApplication app;

    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_TopoCheck_32.png"));
    }

    @Override
    public String getCaption() {
        return "拓扑检查";
    }

    @Override
    public String getCategory() {
        return "常用工具";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    @Override
    public String getMessage() {
        return "拓扑检查";
    }

    @Override
    public String getTooltip() {
        return "拓扑检查";
    }

    @Override
    public void onClick() {
        if (this.app != null)
        {
            Document document = this.app.getDocument();
            MapControl mapControl = null;
            if (this.app.getActiveContentsView() != null && this.app.getActiveContentsView() instanceof IMapContentsView) {
                mapControl = ((IMapContentsView)this.app.getActiveContentsView()).getMapControl();
            }
            TopoCheckDialog dialog = new TopoCheckDialog(document, mapControl);
            dialog.initOwner(Window.primaryStage);
            dialog.show();
            if (ButtonType.OK == dialog.getResult())
            {
                IDockWindow dw = this.app.getPluginContainer().getDockWindows().get(TopoCheckedResultsDW.class.getName());
                if (dw == null) {
                    dw = this.app.getPluginContainer().createDockWindow(TopoCheckedResultsDW.class.getName());
                }
                ((TopoErrorResultsDialog) dw.getChildHWND()).listTopoErrors(dialog.getTopologyErrorManager(), dialog.getSfclsList());
                ((TopoErrorResultsDialog) dw.getChildHWND()).setTolerane(dialog.getTolerance());
                this.app.getPluginContainer().activeDockWindow(dw);
            }
        }
    }

    @Override
    public void onCreate(IApplication app) {
        if (app != null) {
            this.app = app;
        }
    }
}
