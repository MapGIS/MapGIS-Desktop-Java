package com.zondy.mapgis.workspace.edit;

import com.zondy.mapgis.att.Record;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.edit.view.SketchEditor;
import com.zondy.mapgis.geodatabase.SFeatureCls;
import com.zondy.mapgis.geometry.GeometryType;
import com.zondy.mapgis.info.LinInfo;
import com.zondy.mapgis.info.PntInfo;
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
 * @author CR
 * @file InputLineSample.java
 * @brief
 * @create 2020-05-26.
 */
public class InputPointSample extends Application {
    private final VBox vBox = new VBox(6);
    private final MapControl mapControl = new MapControl();
    private final Map map = new Map();
    private VectorLayer vectorLayer;
    private final SketchEditor sketchEditor = new SketchEditor();
    private final GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
    //private final SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF64c113, 4);

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("输入点");
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

        //VectorLayer layer = new VectorLayer(VectorLayerType.SFclsLayer);
        //layer.setURL("gdbp://MapGISLocalPlus/hhhtest/sfcls/lin1");
        //if (layer.connectData()) {
        //    this.map.append(layer);
        //}

        this.vectorLayer = new VectorLayer(VectorLayerType.SFclsLayer);
        this.vectorLayer.setURL("gdbp://MapGISLocalPlus/hhhtest/sfcls/pnt1");
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
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    SketchGeometry sketchGeometry = list.get(i);
                    if (sketchGeometry != null) {
                        if (sketchGeometry.isDeleted()) {
                            //TODO：从图层删除图元
                        } else if (sketchGeometry.getObjID() == -1) {
                            MapLayer mapLayer = sketchGeometry.getLayer();
                            SFeatureCls cls = (SFeatureCls) mapLayer.getData();
                            if (cls != null) {
                                PntInfo pntInfo = this.getPntInfo();
                                Record rcd = new Record();
                                rcd.setFields(cls.getFields());
                                long oid = cls.append(sketchGeometry.getGeometry(), rcd, pntInfo);
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
        this.sketchEditor.startInput(this.vectorLayer, GeometryType.GeoPoint);
    }

    private PntInfo pntInfo = null;

    private PntInfo getPntInfo() {
        if (this.pntInfo == null) {
            this.pntInfo = new PntInfo();
            this.pntInfo.setSymID(1);
            this.pntInfo.setWidth(8);
            this.pntInfo.setHeight(8);
            this.pntInfo.setAngle(0);
            this.pntInfo.setOutClr1(3);
            this.pntInfo.setOvprnt(true);
        }
        return this.pntInfo;
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
