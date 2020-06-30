package com.zondy.mapgis.mapeditor.dialogs;

import com.zondy.mapgis.geometry.GeomType;
import com.zondy.mapgis.map.LayerState;
import com.zondy.mapgis.map.MapLayer;

class LayerSettingInfo {
    private boolean selected;
    private MapLayer mapLayer;
    private String layerName;
    private String desName;
    private String sql;

    public LayerSettingInfo(MapLayer mapLayer, String sql) {
        this.selected = !(mapLayer.getState() == LayerState.UnVisible);
        this.mapLayer = mapLayer;
        this.layerName = this.desName = mapLayer.getName();
        this.sql = sql;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public MapLayer getMapLayer() {
        return mapLayer;
    }

    public String getLayerName() {
        return layerName;
    }

    public String getDesName() {
        return desName;
    }

    public void setDesName(String desName) {
        this.desName = desName;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
