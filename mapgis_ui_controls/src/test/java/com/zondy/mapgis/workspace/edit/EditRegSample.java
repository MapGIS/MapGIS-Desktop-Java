package com.zondy.mapgis.workspace.edit;

import com.zondy.mapgis.common.CoordinateTran;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.controls.MapCursors;
import com.zondy.mapgis.edit.view.SketchEditor;
import com.zondy.mapgis.geodatabase.SFeatureCls;
import com.zondy.mapgis.geometry.*;
import com.zondy.mapgis.info.LinInfo;
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
 * @file EditLineSample.java
 * @brief
 *
 * @author CR
 * @date 2020-06-16.
 */
public class EditRegSample extends Application {
    private final VBox vBox = new VBox(6);
    private final MapControl mapControl = new MapControl();
    private final Map map = new Map();
    private VectorLayer vectorLayer;
    private final SketchEditor sketchEditor = new SketchEditor();
    SelectResult selectResult = null;
    SketchGeometry selSketchGeometry = null;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("编辑区边界点");
        primaryStage.setWidth(800);
        primaryStage.setHeight(700);
        primaryStage.setScene(new Scene(vBox));
        primaryStage.show();

        Button buttonInput = new Button("输入");
        Button buttonPS = new Button("点选");
        Button buttonEdit = new Button("编辑");
        Button buttonSave = new Button("保存");
        Button buttonStop = new Button("停止");
        Button buttonTest = new Button("Test");

        this.mapControl.setMinSize(100, 100);
        this.vBox.setPadding(new Insets(12));
        this.vBox.setFillWidth(true);
        this.vBox.getChildren().addAll(new HBox(6, buttonInput, buttonPS, buttonEdit, buttonSave, buttonStop, buttonTest), this.mapControl);
        VBox.setVgrow(this.mapControl, Priority.ALWAYS);

        this.vectorLayer = new VectorLayer(VectorLayerType.SFclsLayer);
        this.vectorLayer.setURL("gdbp://MapGISLocalPlus/hhhtest/sfcls/Reg1");
        if (this.vectorLayer.connectData()) {
            this.map.append(this.vectorLayer);
        }

        this.mapControl.setMap(this.map);
        this.mapControl.setSketchEditor(sketchEditor);

        buttonInput.setOnAction(event -> {
            this.selectResult = null;
            this.selSketchGeometry = null;
            this.sketchEditor.stop();
            this.sketchEditor.startInput(this.vectorLayer, GeometryType.GeoPolygon);
        });
        buttonPS.setOnAction(event -> {
            this.sketchEditor.stop();
            this.mapControl.setCursor(MapCursors.ARROW);
            this.mapControl.setOnMouseClicked(mouseEvent -> {
                if (this.selectResult == null && this.selSketchGeometry == null) {
                    this.selectResult = null;
                    this.selSketchGeometry = null;
                    if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                        this.mapControl.getSketchGraphicsOverlay().clearSelection();
                        Dot dotMP = CoordinateTran.wpToMp(this.mapControl, mouseEvent.getX(), mouseEvent.getY());

                        SketchGeometryList sketchGeometryList = this.mapControl.getSketchGeometrys().select(dotMP, 1, 10);
                        Geometry geometry = null;
                        if (sketchGeometryList != null && sketchGeometryList.size() > 0) {
                            selSketchGeometry = sketchGeometryList.get(0);
                            geometry = selSketchGeometry.getGeometry().clone();
                        } else {
                            SelectResultList selectResultList = this.mapControl.identifySelections(dotMP, 1, false);
                            if (selectResultList != null && selectResultList.size() > 0) {
                                selectResult = selectResultList.get(0);
                                geometry = selectResult.getGeometry();
                            }
                        }

                        Graphic graphic = new Graphic();
                        graphic.setGeometry(geometry);
                        graphic.setSelected(true);
                        this.mapControl.getSketchGraphicsOverlay().getGraphics().add(graphic);
                        this.mapControl.refreshOverlay();
                    }
                }
            });
        });
        buttonEdit.setOnAction(event -> {
            if (this.selectResult != null) {
                this.sketchEditor.startEdit(this.selectResult);
            } else if (this.selSketchGeometry != null) {
                this.sketchEditor.startEdit(this.selSketchGeometry);
            }
        });
        buttonSave.setOnAction(event -> {
            this.sketchEditor.stop();
            this.selectResult = null;
            this.selSketchGeometry = null;
            CommonCls.saveEdits(this.mapControl);
        });
        buttonStop.setOnAction(event -> {
            this.sketchEditor.stop();
            this.selectResult = null;
            this.selSketchGeometry = null;
            this.mapControl.getSketchGraphicsOverlay().getGraphics().clear();
        });

        buttonTest.setOnAction(event -> {

        });
    }



    /**
     * Graphic里面的几何是否为点
     *
     * @param graphic
     * @return
     */
    private boolean isPointGraphic(Graphic graphic) {
        boolean rtn = false;
        Geometry geometry = graphic.getGeometry();
        if (geometry instanceof GeoPoint) {
            rtn = true;
        } else if (geometry instanceof GeoMultiPoint) {
            rtn = ((GeoMultiPoint) geometry).getDotNum() == 1;
        }
        return rtn;
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
