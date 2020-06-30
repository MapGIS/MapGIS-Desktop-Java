package com.zondy.mapgis.rastereditor.plugin.command;

import com.zondy.mapgis.base.Window;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICommand;
import com.zondy.mapgis.pluginengine.plugin.IMapContentsView;
import com.zondy.mapgis.rastereditor.dialogs.FormulaCaculateDialog;
import javafx.scene.image.Image;

/**
 * 栅格计算器
 */
public class FormulaCaculateCommand implements ICommand{
    private IApplication app;

    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_RasterCalculator_16.png"));
    }

    @Override
    public String getCaption() {
        return "栅格计算器";
    }

    @Override
    public String getCategory() {
        return "栅格分析";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getMessage() {
        return "栅格计算器";
    }

    @Override
    public String getTooltip() {
        return "栅格计算器";
    }

    @Override
    public void onClick() {
        FormulaCaculateDialog dialog = new FormulaCaculateDialog(this.app.getDocument());
        dialog.initOwner(Window.primaryStage);
        dialog.show();
    }

    @Override
    public void onCreate(IApplication app) {
        if (app != null){
            this.app = app;
        }else{
            System.out.println("app == null");
        }
    }
}
