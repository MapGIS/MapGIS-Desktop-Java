package com.zondy.mapgis.mapeditor.dialogs;

import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.edit.view.SketchEditor;
import com.zondy.mapgis.geodatabase.XClsType;
import com.zondy.mapgis.geometry.GeometryType;
import com.zondy.mapgis.map.*;
import com.zondy.mapgis.map.event.InsertLayerEvent;
import com.zondy.mapgis.map.event.LayerPropertyChangedEvent;
import com.zondy.mapgis.map.event.RemoveLayerEvent;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.HashMap;
import java.util.List;

/**
 * CreateFeatureDialog
 *
 * @author cxy
 * @date 2020/06/05
 */
public class CreateFeaturePane extends SplitPane {
    private final Image inputPntImage = new Image(getClass().getResourceAsStream("/Png_InputPntDefault_16.png"));
    private final Image inputCombinePntImage = new Image(getClass().getResourceAsStream("/Png_InputCombinePnt_16.png"));
    private final Image inputLinBrokenImage = new Image(getClass().getResourceAsStream("/Png_InputLinBroken_16.png"));
    private final Image inputLinDoubleImage = new Image(getClass().getResourceAsStream("/Png_InputLinDouble_16.png"));
    private final Image inputRegBrokenLineImage = new Image(getClass().getResourceAsStream("/Png_InputRegBrokenLine_16.png"));
    private final Image inputRegRectImage = new Image(getClass().getResourceAsStream("/Png_InputRegRect_16.png"));
    private final Image inputRegWithHoleImage = new Image(getClass().getResourceAsStream("/Png_InputRegWithHole_16.png"));
    private final Image inputAnnoParamImage = new Image(getClass().getResourceAsStream("/Png_InputAnnoParam_16.png"));
    private final Image errorImage = new Image(getClass().getResourceAsStream("/Png_Error_16.png"));

    private MapControl mapControl;
    private Map map;
    private SketchEditor sketchEditor;
    private HashMap<Long, MapLayer> mapLayerHashMap;
    private ListView<GeometryType> listViewConstructionTool;
    private ListView<MapLayer> listViewLayer;

    public CreateFeaturePane(MapControl mapControl) {
        this.mapControl = mapControl;
        this.map = this.mapControl.getMap();
        this.addMapLisreners(this.map);
        this.sketchEditor = this.mapControl.getSketchEditor();
        if (this.sketchEditor == null) {
            this.sketchEditor = new SketchEditor();
            this.mapControl.setSketchEditor(this.sketchEditor);
        }

        this.setOrientation(Orientation.VERTICAL);

        this.listViewConstructionTool = new ListView<>();
        this.listViewConstructionTool.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.listViewConstructionTool.setCellFactory(param -> new ListCell<GeometryType>() {
            @Override
            protected void updateItem(GeometryType item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(getTextByGeometryType(item));
                    setGraphic(new ImageView(getImageByGeometryType(item)));
                }
            }
        });
        this.listViewConstructionTool.getSelectionModel().selectedItemProperty().addListener(this::listViewConstructionToolSelectedItemChanged);

        this.listViewLayer = new ListView<>();
        this.listViewLayer.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.listViewLayer.setCellFactory(param -> new ListCell<MapLayer>() {
            @Override
            protected void updateItem(MapLayer item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getName());
                    // TODO:
                    //setGraphic();
                }
            }
        });
        this.listViewLayer.getSelectionModel().selectedItemProperty().addListener(this::listViewLayerSelectedItemChanged);

           TitledPane titledPane = new TitledPane("构建工具", this.listViewConstructionTool);
        titledPane.setCollapsible(false);

        this.getItems().addAll(this.listViewLayer, titledPane);

        // init
        this.mapLayerHashMap = new HashMap<>();
        List<MapLayer> editLayers = this.map.getEditLayer(EditLayerType.All, SelectLayerControl.Visible);
        editLayers.forEach(mapLayer -> this.mapLayerHashMap.put(mapLayer.getHandle(), mapLayer));
        this.listViewLayer.getItems().addAll(editLayers);
        this.listViewLayer.getSelectionModel().select(0);
    }

    public void replaceMapControl(MapControl mapControl) {

    }

    public void startConstruction() {
        this.sketchEditor.reStartInput();
        if (this.listViewLayer.getItems().size() > 0 || this.listViewLayer.getSelectionModel().getSelectedIndex() >= 0) {
            this.listViewLayer.getSelectionModel().select(0);
        }
    }

    public void stopConstruction() {
        this.sketchEditor.stop();
        this.listViewLayer.getSelectionModel().clearSelection();

    }

    private String getTextByGeometryType(GeometryType geometryType) {
        switch (geometryType) {
            case GeoPoint:
                return "单点";
            case GeoMultiPoint:
                return "多点";
            case GeoVarLine:
                return "折线";
            case GeoMultiLine:
                return "多线";
            case GeoPolygon:
                return "折线区";
            case GeoMultiPolygon:
                return "多区";
            case GeoRect:
                return "矩形";
            case GeoAnno:
                return "注记";
            default:
                return "暂不支持";
        }
    }

    private Image getImageByGeometryType(GeometryType geometryType) {
        switch (geometryType) {
            case GeoPoint:
                return this.inputPntImage;
            case GeoMultiPoint:
                return this.inputCombinePntImage;
            case GeoVarLine:
                return this.inputLinBrokenImage;
            case GeoMultiLine:
                return this.inputLinDoubleImage;
            case GeoPolygon:
                return this.inputRegBrokenLineImage;
            case GeoMultiPolygon:
                return this.inputRegWithHoleImage;
            case GeoRect:
                return this.inputRegRectImage;
            case GeoAnno:
                return this.inputAnnoParamImage;
            default:
                return this.errorImage;
        }
    }

    private void listViewConstructionToolSelectedItemChanged(ObservableValue<? extends GeometryType> observable, GeometryType oldValue, GeometryType newValue) {
        if (newValue == null) {
            this.sketchEditor.stop();
        } else {
            this.sketchEditor.startInput(this.listViewLayer.getSelectionModel().getSelectedItem(), newValue);
        }
    }

    private void listViewLayerSelectedItemChanged(ObservableValue<? extends MapLayer> observable, MapLayer oldValue, MapLayer newValue) {
        if (newValue == null) {
            this.listViewConstructionTool.getItems().clear();
            return;
        }
        if (oldValue == null || oldValue.getGeometryType() != newValue.getGeometryType()) {
            this.listViewConstructionTool.getItems().clear();
            switch (newValue.getGeometryType()) {
                case GeomPnt:
                    this.listViewConstructionTool.getItems().add(GeometryType.GeoPoint);
                    this.listViewConstructionTool.getItems().add(GeometryType.GeoMultiPoint);
                    break;
                case GeomLin:
                    this.listViewConstructionTool.getItems().add(GeometryType.GeoVarLine);
                    this.listViewConstructionTool.getItems().add(GeometryType.GeoMultiLine);
                    break;
                case GeomReg:
                    this.listViewConstructionTool.getItems().add(GeometryType.GeoPolygon);
                    this.listViewConstructionTool.getItems().add(GeometryType.GeoMultiPolygon);
                    this.listViewConstructionTool.getItems().add(GeometryType.GeoRect);
                    break;
                case GeomAnn:
                    this.listViewConstructionTool.getItems().add(GeometryType.GeoAnno);
                    break;
                default:
                    return;
            }
            this.listViewConstructionTool.getSelectionModel().select(0);
            return;
        }
        if (oldValue.getGeometryType() == newValue.getGeometryType()) {
            this.listViewConstructionTool.getSelectionModel().clearSelection();
            this.listViewConstructionTool.getSelectionModel().select(0);
        }
    }

    private void addMapLisreners(Map map) {
        for (int i = 0; i < map.getLayerCount(); i++) {
            MapLayer mapLayer = map.getLayer(i);
            XClsType xClsType = mapLayer.getClsType();
            if (xClsType != XClsType.XSFCls && xClsType != XClsType.XACls) {
                continue;
            }
            mapLayer.addPropertyChangedListener(this::layerPropertyChangedListener);
        }
        map.addInsertLayerListener(this::insertLayerListener);
        map.addRemoveLayerListener(this::removeLayerListener);
    }

    private void removeMapLisreners(Map map) {
        for (int i = 0; i < map.getLayerCount(); i++) {
            MapLayer mapLayer = map.getLayer(i);
            XClsType xClsType = mapLayer.getClsType();
            if (xClsType != XClsType.XSFCls && xClsType != XClsType.XACls) {
                continue;
            }
            mapLayer.removePropertyChangedListener(this::layerPropertyChangedListener);
        }
        map.removeInsertLayerListener(this::insertLayerListener);
        map.removeRemoveLayerListener(this::removeLayerListener);
    }

    private void layerPropertyChangedListener(LayerPropertyChangedEvent layerPropertyChangedEvent) {
        MapLayer mapLayer = layerPropertyChangedEvent.getArgs().getMapLayer();
        String propertyName = layerPropertyChangedEvent.getArgs().getPropertyName();
        Object propertyValue = layerPropertyChangedEvent.getArgs().getPropertyValue();
        if (!propertyName.equals("State")) {
            return;
        }
        // TODO: 修改 getPropertyValue
        LayerState layerState = mapLayer.getState();//(LayerState) propertyValue;
        if (layerState != LayerState.UnVisible) {
            this.listViewLayer.getItems().add(mapLayerHashMap.get(mapLayer.getHandle()));
        } else {
            this.listViewLayer.getItems().remove(mapLayerHashMap.get(mapLayer.getHandle()));
        }
    }

    private void insertLayerListener(InsertLayerEvent insertLayerEvent) {
        MapLayer mapLayer = insertLayerEvent.getArgs().getInsertedLayer();
        XClsType xClsType = mapLayer.getClsType();
        if (xClsType != XClsType.XSFCls && xClsType != XClsType.XACls) {
            return;
        }
        mapLayer.addPropertyChangedListener(this::layerPropertyChangedListener);
        this.mapLayerHashMap.put(mapLayer.getHandle(), mapLayer);
        this.listViewLayer.getItems().add(mapLayer);
    }

    private void removeLayerListener(RemoveLayerEvent removeLayerEvent) {
        MapLayer mapLayer = removeLayerEvent.getArgs().getRemovedLayer();
        XClsType xClsType = mapLayer.getClsType();
        if (xClsType != XClsType.XSFCls && xClsType != XClsType.XACls) {
            return;
        }
        mapLayer.removePropertyChangedListener(this::layerPropertyChangedListener);
        this.listViewLayer.getItems().remove(this.mapLayerHashMap.get(mapLayer.getHandle()));
        this.mapLayerHashMap.remove(mapLayer.getHandle());
    }

    public void destroy() {
        this.sketchEditor.stop();
        this.removeMapLisreners(this.map);
        this.mapControl.setSketchEditor(null);
    }
}
