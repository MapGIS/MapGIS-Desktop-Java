package com.zondy.mapgis.workspace.edit;

import com.zondy.mapgis.common.CoordinateTran;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.controls.MapCursors;
import com.zondy.mapgis.edit.view.SketchEditor;
import com.zondy.mapgis.geodatabase.SFeatureCls;
import com.zondy.mapgis.geometry.*;
import com.zondy.mapgis.map.Map;
import com.zondy.mapgis.map.VectorLayer;
import com.zondy.mapgis.map.VectorLayerType;
import com.zondy.mapgis.view.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @file RectTest.java
 * @brief
 *
 * @author CR
 * @date 2020-06-22.
 */
public class RectTest extends Application {
    private final VBox vBox = new VBox(6);
    private MapControl mapControl;
    private final Map map = new Map();
    private VectorLayer vectorLayer;
    private final SketchEditor sketchEditor = new SketchEditor();
    private SelectResult selectResult = null;
    private SketchGeometry selSketchGeometry = null;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("RectTest");
        primaryStage.setWidth(800);
        primaryStage.setHeight(700);
        primaryStage.setScene(new Scene(vBox));
        primaryStage.show();

        Button buttonInput = new Button("输入矩形");
        Button buttonStop = new Button("停止");
        Button buttonTest = new Button("Test");

        this.mapControl = new MapControl();
        this.mapControl.setMinSize(100, 100);
        this.vBox.setPadding(new Insets(12));
        this.vBox.setFillWidth(true);
        this.vBox.getChildren().addAll(new HBox(6, buttonInput,buttonStop, buttonTest), this.mapControl);
        VBox.setVgrow(this.mapControl, Priority.ALWAYS);

        this.vectorLayer = new VectorLayer(VectorLayerType.SFclsLayer);
        this.vectorLayer.setURL("gdbp://MapGISLocalPlus/hhhtest/sfcls/Lin1");
        if (this.vectorLayer.connectData()) {
            this.map.append(this.vectorLayer);
        }

        this.mapControl.setMap(this.map);
        this.mapControl.setSketchEditor(sketchEditor);

        buttonInput.setOnAction(event -> {
            this.sketchEditor.startInput(this.vectorLayer, GeometryType.GeoRect);
        });
        buttonStop.setOnAction(event -> {
            this.sketchEditor.stop();
            this.mapControl.refreshWnd();
        });
        buttonTest.setOnAction(event -> {
        });
    }

    @Override
    public void stop() {
        this.map.clear();
        this.mapControl.dispose();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
