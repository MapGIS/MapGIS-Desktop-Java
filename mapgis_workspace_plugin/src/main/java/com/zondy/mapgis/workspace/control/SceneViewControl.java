package com.zondy.mapgis.workspace.control;

import com.zondy.mapgis.controls.SceneControl;
import com.zondy.mapgis.pluginengine.IApplication;
import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;

/**
 * 场景视图容器
 *
 * @author cxy
 * @date 2020/04/30
 */
public class SceneViewControl extends StackPane {
    private IApplication application;
    private SceneControl sceneControl;

    public SceneViewControl(IApplication application) {
        this.application = application;

        this.sceneControl = new SceneControl();
        this.sceneControl.setMinSize(100, 100);
        this.getChildren().add(sceneControl);
        this.setPadding(new Insets(0));
    }

    public SceneControl getSceneControl() {
        return this.sceneControl;
    }
}
