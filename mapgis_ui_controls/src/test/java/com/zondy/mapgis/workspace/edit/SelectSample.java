package com.zondy.mapgis.workspace.edit;

import com.zondy.mapgis.att.Record;
import com.zondy.mapgis.common.CoordinateTran;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.controls.MapCursors;
import com.zondy.mapgis.edit.tool.RectSketchTool;
import com.zondy.mapgis.edit.view.SketchEditor;
import com.zondy.mapgis.geodatabase.SFeatureCls;
import com.zondy.mapgis.geometry.*;
import com.zondy.mapgis.info.LinInfo;
import com.zondy.mapgis.map.Map;
import com.zondy.mapgis.map.MapLayer;
import com.zondy.mapgis.map.VectorLayer;
import com.zondy.mapgis.map.VectorLayerType;
import com.zondy.mapgis.view.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @author CR
 * @file InputLineSample.java
 * @brief
 * @create 2020-05-26.
 */
public class SelectSample extends Application {
    private final VBox vBox = new VBox(6);
    private final MapControl mapControl = new MapControl();
    private final Map map = new Map();
    private VectorLayer vectorLayer;
    private final SketchEditor sketchEditor = new SketchEditor();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("选择");
        primaryStage.setWidth(800);
        primaryStage.setHeight(700);
        primaryStage.setScene(new Scene(vBox));
        primaryStage.show();

        Button buttonInput = new Button("输入");
        Button buttonPS = new Button("点选");
        Button buttonRS = new Button("框选");
        Button buttonClear = new Button("清空");

        this.mapControl.setMinSize(100, 100);
        this.vBox.setPadding(new Insets(12));
        this.vBox.setFillWidth(true);
        this.vBox.getChildren().addAll(new HBox(6, buttonInput, buttonPS, buttonRS, buttonClear), this.mapControl);
        VBox.setVgrow(this.mapControl, Priority.ALWAYS);

        this.vectorLayer = new VectorLayer(VectorLayerType.SFclsLayer);
        this.vectorLayer.setURL("gdbp://MapGISLocalPlus/hhhtest/sfcls/Lin1");
        if (this.vectorLayer.connectData()) {
            this.map.append(this.vectorLayer);
        }

        this.mapControl.setMap(this.map);
        this.mapControl.setSketchEditor(sketchEditor);

        buttonInput.setOnAction(event -> {
            this.mapControl.getSketchGraphicsOverlay().getGraphics().clear();
            this.sketchEditor.stop();
            this.sketchEditor.startInput(this.vectorLayer, GeometryType.GeoVarLine);
        });
        buttonPS.setOnAction(event -> {
            this.mapControl.setCursor(MapCursors.ARROW);
            this.mapControl.setOnMouseClicked(mouseEvent -> {
                this.mapControl.getSketchGraphicsOverlay().getGraphics().clear();
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    Dot dotMP = CoordinateTran.wpToMp(this.mapControl, mouseEvent.getX(), mouseEvent.getY());

                    SketchGeometryList sketchGeometryList = this.mapControl.getSketchGeometrys().select(dotMP, 1, 10);
                    if (sketchGeometryList != null && sketchGeometryList.size() > 0) {
                        Geometry geometry = sketchGeometryList.get(0).getGeometry();
                        Graphic graphic = new Graphic();
                        graphic.setGeometry(geometry);
                        graphic.setSelected(true);
                        this.mapControl.getSketchGraphicsOverlay().getGraphics().add(graphic);
                    }

                    SelectResultList selectResultList = this.mapControl.identifySelections(dotMP, 1, false);
                    if (selectResultList != null && selectResultList.size() > 0) {
                        Geometry geometry = selectResultList.get(0).getGeometry();
                        Graphic graphic = new Graphic();
                        graphic.setGeometry(geometry);
                        graphic.setSelected(true);
                        this.mapControl.getSketchGraphicsOverlay().getGraphics().add(graphic);
                    }
                }
                this.mapControl.refreshOverlay();
            });
        });
        buttonRS.setOnAction(event -> {
            this.mapControl.setCursor(MapCursors.ARROW);
            this.mapControl.getSketchGraphicsOverlay().getGraphics().clear();
            RectSketchTool rectSketchTool = new RectSketchTool(this.mapControl);
            this.mapControl.setCursor(MapCursors.CROSS);
            rectSketchTool.addToolFinishedListener(toolFinishedEvent -> {
                System.out.println(toolFinishedEvent.getGeometry());
            });
        });
        buttonClear.setOnAction(event -> {
            this.mapControl.getSketchGraphicsOverlay().getGraphics().clear();
            this.mapControl.refreshOverlay();
        });
    }

    private GeoVarLine buildLine(Dot... dots) {
        GeoVarLine line = null;
        if (dots != null && dots.length > 0) {
            line = new GeoVarLine();
            for (Dot dot : dots) {
                line.append2D(dot);
            }
        }
        return line;
    }

    private void startSketch() {
        this.sketchEditor.stop();
        this.sketchEditor.startInput(this.vectorLayer, GeometryType.GeoVarLine);
    }

    private LinInfo linInfo = null;

    private LinInfo getLinInfo() {
        if (this.linInfo == null) {
            linInfo = new LinInfo();
            linInfo.setLinStyID(1);
            linInfo.setOutClr1(3);
            linInfo.setOutClr2(4);
            linInfo.setOutClr3(5);
            linInfo.setOutPenW1(1);
            linInfo.setOutPenW2(1);
            linInfo.setOutPenW3(1);
            linInfo.setXScale(10);
            linInfo.setYScale(10);
            linInfo.setOvprnt(true);
        }
        return linInfo;
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
