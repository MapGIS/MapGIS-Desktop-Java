package com.zondy.mapgis.workspace.edit;

import com.zondy.mapgis.att.Record;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.edit.view.SketchEditor;
import com.zondy.mapgis.geodatabase.SFeatureCls;
import com.zondy.mapgis.geometry.GeometryType;
import com.zondy.mapgis.info.LinInfo;
import com.zondy.mapgis.info.RegInfo;
import com.zondy.mapgis.map.Map;
import com.zondy.mapgis.map.MapLayer;
import com.zondy.mapgis.map.VectorLayer;
import com.zondy.mapgis.map.VectorLayerType;
import com.zondy.mapgis.view.GraphicsOverlay;
import com.zondy.mapgis.view.SketchGeometry;
import com.zondy.mapgis.view.SketchGeometryList;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @file InputRegSample.java
 * @brief
 *
 * @author CR
 * @date 2020-06-15.
 */
public class InputRegSample extends Application {
    private final VBox vBox = new VBox(6);
    private final MapControl mapControl = new MapControl();
    private final Map map = new Map();
    private VectorLayer vectorLayer;
    private final SketchEditor sketchEditor = new SketchEditor();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("输入折线区");
        primaryStage.setWidth(800);
        primaryStage.setHeight(700);
        primaryStage.setScene(new Scene(vBox));
        primaryStage.show();

        Button buttonStart = new Button("开始输入");
        Button buttonEnd = new Button("结束输入");
        this.mapControl.setMinSize(100, 100);
        this.vBox.setPadding(new Insets(12));
        this.vBox.setFillWidth(true);
        this.vBox.getChildren().addAll(new HBox(6, buttonStart, buttonEnd), this.mapControl);
        VBox.setVgrow(this.mapControl, Priority.ALWAYS);

        this.vectorLayer = new VectorLayer(VectorLayerType.SFclsLayer);
        this.vectorLayer.setURL("gdbp://MapGISLocalPlus/hhhtest/sfcls/Reg1");
        if (this.vectorLayer.connectData()) {
            this.map.append(this.vectorLayer);
        }

        this.mapControl.setSketchEditor(sketchEditor);
        this.mapControl.setMap(this.map);

        buttonStart.setOnAction(event -> {
            this.startSketch();
            this.mapControl.requestFocus();
        });
        buttonEnd.setOnAction(event -> {
            this.sketchEditor.stop();
            SketchGeometryList list = this.mapControl.getSketchGeometrys();
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    SketchGeometry sketchGeometry = list.get(i);
                    if (sketchGeometry != null) {
                        if (sketchGeometry.isDeleted()) {
                            //TODO：从图层删除图元
                        } else if (sketchGeometry.getObjID() == -1) {
                            MapLayer mapLayer = sketchGeometry.getLayer();
                            SFeatureCls cls = (SFeatureCls) mapLayer.getData();
                            if (cls != null) {
                                RegInfo regInfo = this.getRegInfo();
                                Record rcd = new Record();
                                rcd.setFields(cls.getFields());
                                long oid = cls.append(sketchGeometry.getGeometry(), rcd, regInfo);
                                System.out.println("oid: " + oid);
                            }
                        } else {
                            //TODO：更新图元
                        }
                    }
                }
                this.mapControl.getSketchGeometrys().clear();
                this.mapControl.refreshWnd();
            }
        });
    }

    private void startSketch() {
        this.sketchEditor.stop();
        this.sketchEditor.startInput(this.vectorLayer, GeometryType.GeoPolygon);
    }

    private RegInfo regInfo = null;

    private RegInfo getRegInfo() {
        if (this.regInfo == null) {
            this.regInfo = new RegInfo();
            this.regInfo.setPatID(0);
            this.regInfo.setFillClr(828);
            this.regInfo.setPatHeight(50);
            this.regInfo.setPatWidth(50);
            this.regInfo.setOutPenW(1.0F);
            this.regInfo.setAngle(0);
            this.regInfo.setPatClr(3);
            this.regInfo.setFillMode((short) 0);
            this.regInfo.setOvprnt(true);
        }
        return regInfo;
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
