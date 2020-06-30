package com.zondy.mapgis.workspace.edit;

import com.zondy.mapgis.att.Record;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.edit.view.SketchCreationMode;
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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.temporal.ValueRange;
import java.util.List;

/**
 * @author CR
 * @file InputLineSample.java
 * @brief
 * @create 2020-05-26.
 */
public class SelectTest extends Application {
    private final VBox vBox = new VBox(6);
    private MapControl mapControl = null;
    private final Map map = new Map();
    private VectorLayer vectorLayer;
    private final SketchEditor sketchEditor = new SketchEditor();

    @Override
    public void start(Stage primaryStage) {
        mapControl = new MapControl();
        primaryStage.setTitle("选择函数测试");
        primaryStage.setWidth(800);
        primaryStage.setHeight(700);
        primaryStage.setScene(new Scene(vBox));
        primaryStage.show();

        Button buttonAdd = new Button("添加");
        Button buttonPS = new Button("点选");
        Button buttonRS = new Button("框选");

        this.mapControl.setMinSize(100, 100);
        this.vBox.setPadding(new Insets(12));
        this.vBox.setFillWidth(true);
        this.vBox.getChildren().addAll(new HBox(6, buttonAdd, buttonPS, buttonRS), this.mapControl);
        VBox.setVgrow(this.mapControl, Priority.ALWAYS);

        this.vectorLayer = new VectorLayer(VectorLayerType.SFclsLayer);
        this.vectorLayer.setURL("gdbp://MapGISLocalPlus/hhhtest/sfcls/Lin1");
        if (this.vectorLayer.connectData()) {
            this.map.append(this.vectorLayer);
        }

        this.mapControl.setMap(this.map);
        this.mapControl.setSketchEditor(sketchEditor);

        buttonAdd.setOnAction(event -> {
            SketchGeometry sketchGeometry1 = new SketchGeometry();
            sketchGeometry1.setLayer(this.vectorLayer);
            sketchGeometry1.setGeometry(this.buildLine(new Dot(-283, 286), new Dot(-256, 314), new Dot(-265, 252)));
            this.mapControl.getSketchGeometrys().add(sketchGeometry1);
            SketchGeometry sketchGeometry2 = new SketchGeometry();
            sketchGeometry2.setLayer(this.vectorLayer);
            sketchGeometry2.setGeometry(this.buildLine(new Dot(-207, 303), new Dot(-217, 260), new Dot(-178, 288)));
            this.mapControl.getSketchGeometrys().add(sketchGeometry2);

            GraphicsOverlay graphicsOverlay = this.mapControl.getSketchGraphicsOverlay();
            Graphic graphic1 = new Graphic();
            graphic1.setGeometry(new GeoPoint(new Dot3D(-116, 288, 0.0)));
            graphicsOverlay.getGraphics().add(graphic1);
            Graphic graphic2 = new Graphic();
            graphic2.setGeometry(new GeoPoint(new Dot3D(-54, 259, 0.0)));
            graphicsOverlay.getGraphics().add(graphic2);
            Graphic graphic3 = new Graphic();
            graphic3.setGeometry(new GeoPoint(new Dot3D(-106, 228, 0.0)));
            graphicsOverlay.getGraphics().add(graphic3);
            Graphic graphic = new Graphic();
            graphic.setGeometry(this.buildLine(new Dot(-116, 288), new Dot(-54, 259), new Dot(-106, 228)));
            graphicsOverlay.getGraphics().add(graphic);

            this.mapControl.refreshWnd();
            this.mapControl.refreshOverlay();
        });

        buttonPS.setOnAction(event -> {
            GraphicList graphicList = this.mapControl.getSketchGraphicsOverlay().select(new Dot(-54, 258), 0.0001, 10);
            if (graphicList != null) {
                System.out.println("graphicList: " + graphicList.size());
            }
            SketchGeometryList sketchGeometryList = this.mapControl.getSketchGeometrys().select(new Dot(-217, 260), 0.0001, 10);
            if (sketchGeometryList != null) {
                System.out.println("sketchGeometryList: " + sketchGeometryList.size());
            }

            SelectResultList selectResultList = this.mapControl.identifySelections(new Dot(-114, 99), 1, false);
            if (selectResultList != null) {
                System.out.println("selectResultList: " + selectResultList.size());
            }
        });

        buttonRS.setOnAction(event -> {
            Rect rect = new Rect(-300, 50, -20, 330);
            GraphicList graphicList = this.mapControl.getSketchGraphicsOverlay().select(rect, 10);
            if (graphicList != null) {
                System.out.println("graphicList: " + graphicList.size());
            }
            int[] sels = this.mapControl.getSketchGraphicsOverlay().getSelected();
            if (sels == null) {
                System.out.println("sels is null");
            } else {
                System.out.println("sels: " + sels.length);
            }


            SketchGeometryList sketchGeometryList = this.mapControl.getSketchGeometrys().select(rect, 10);
            if (sketchGeometryList != null) {
                System.out.println("sketchGeometryList: " + sketchGeometryList.size());
            }

            SelectResultList selectResultList = this.mapControl.identifySelections(rect, true);
            if (selectResultList != null) {
                System.out.println("selectResultList: " + selectResultList.size());
            }

            SelectResultList selectResultList1 = this.mapControl.identifySelections(rect, false);
            if (selectResultList1 != null) {
                System.out.println("selectResultList1: " + selectResultList1.size());
            }
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

    @Override
    public void stop() {
        this.map.clear();
        this.mapControl.dispose();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
