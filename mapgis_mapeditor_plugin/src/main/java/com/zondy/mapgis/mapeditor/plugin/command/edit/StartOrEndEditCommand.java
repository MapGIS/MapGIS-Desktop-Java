package com.zondy.mapgis.mapeditor.plugin.command.edit;

import com.zondy.mapgis.base.MessageBox;
import com.zondy.mapgis.base.Window;
import com.zondy.mapgis.controls.EditType;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.edit.view.SketchEditor;
import com.zondy.mapgis.mapeditor.plugin.dockwindow.CreateFeatureDW;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICommand;
import com.zondy.mapgis.pluginengine.plugin.IContentsView;
import com.zondy.mapgis.pluginengine.plugin.IDockWindow;
import com.zondy.mapgis.workspace.plugin.MapView;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;

/**
 * @author CR
 * @file StartOrEndEditCommand.java
 * @brief 开始或结束编辑
 * @create 2020-06-05.
 */
public class StartOrEndEditCommand implements ICommand {
    private IApplication application;
    private Image startImage = new Image(getClass().getResourceAsStream("/Png_Start_32.png"));
    private Image stopImage = new Image(getClass().getResourceAsStream("/Png_Stop_32.png"));

    @Override
    public Image getImage() {
        return this.startImage;
    }

    @Override
    public String getCaption() {
        return "开始编辑";
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
        return "开始/结束编辑";
    }

    @Override
    public String getTooltip() {
        return "开始/结束编辑";
    }

    @Override
    public void onClick() {
        IContentsView cv = this.application.getActiveContentsView();
        if (cv instanceof MapView) {
            IDockWindow dockWindowCreate = this.application.getPluginContainer().getDockWindows().getOrDefault(CreateFeatureDW.class.getName(), null);
            MapControl mapControl = ((MapView) cv).getMapControl();
            if (mapControl.isEditing()) {
                //结束编辑
                if (mapControl.getSketchGeometrys().size() > 0) {
                    ButtonType buttonType = MessageBox.questionEx("是否要保存当前的编辑?", Window.primaryStage, true);
                    if (ButtonType.CANCEL.equals(buttonType)) {
                        return;
                    }

                    if (ButtonType.YES.equals(buttonType)) {
                        ((MapView) cv).getMapViewControl().saveEdits();
                    } else {
                        mapControl.getSketchGraphicsOverlay().getGraphics().clear();
                        mapControl.getSketchGeometrys().clear();
                        mapControl.refreshWnd();
                        mapControl.refreshOverlay();
                    }
                }

                mapControl.setEditing(false);
                this.application.getPluginContainer().setPluginCaption(this, "开始编辑");//等pluginengine，也需要在MainForm里面添加
                this.application.getPluginContainer().setPluginImage(this, this.startImage);

                if (dockWindowCreate != null) {
                    this.application.getPluginContainer().destroyDockWindow(dockWindowCreate);
                }
                SketchEditor sketchEditor = mapControl.getSketchEditor();
                if (sketchEditor != null) {
                    sketchEditor.stop();
                    mapControl.setSketchEditor(null);
                }
            } else {
                //开始编辑
                mapControl.setEditing(true);
                this.application.getPluginContainer().setPluginCaption(this, "结束编辑");
                this.application.getPluginContainer().setPluginImage(this, this.stopImage);

                SketchEditor sketchEditor = mapControl.getSketchEditor();
                if (sketchEditor == null) {
                    sketchEditor = new SketchEditor();
                    mapControl.setSketchEditor(sketchEditor);
                }
                sketchEditor.startSelect();
                mapControl.setEditType(EditType.SELECT);
            }
            this.application.getStateManager().onStateChanged(this);
        }
    }

    @Override
    public void onCreate(IApplication app) {
        this.application = app;
        this.application.getStateManager().addStateChangedListener(stateChangedEvent -> {
            this.application.getPluginContainer().setPluginEnable(this, this.application.getActiveContentsView() instanceof MapView);
        });
    }
}
