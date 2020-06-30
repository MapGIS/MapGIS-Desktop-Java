package com.zondy.mapgis.controls;

import com.zondy.mapgis.geodatabase.ObjectCls;
import com.zondy.mapgis.geodatabase.SFeatureCls;
import com.zondy.mapgis.map.*;
import com.zondy.mapgis.scene.Map3DLayer;
import com.zondy.mapgis.scene.Scene;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class LayerSelectControl extends HBox {
    private LayerSelectComboBox layerSelectComboBox;
    private Button button;

    public LayerSelectControl(Document document, String filter) {
        layerSelectComboBox = new LayerSelectComboBox(document, filter);
        button = getOpenButton();
        getChildren().addAll(layerSelectComboBox, button);
    }

    public LayerSelectControl(Map map, String filter) {
        layerSelectComboBox = new LayerSelectComboBox(map, filter);
        button = getOpenButton();
        button.setMinWidth(40);
        getChildren().addAll(layerSelectComboBox, button);
    }

    @Override
    public void setWidth(double value) {
        layerSelectComboBox.setMinWidth(value - 40);
    }

    public LayerSelectControl(Scene scene, String filter) {
        layerSelectComboBox = new LayerSelectComboBox(scene, filter);
        button = getOpenButton();
        getChildren().addAll(layerSelectComboBox, button);
    }

    private Button getOpenButton() {
        Button openButton = new Button("", new ImageView(new Image(getClass().getResourceAsStream("/Png_Open_16.png"))));
        openButton.setOnMouseClicked(event -> {
            // TODO: 待添加
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("打开，待选择图层框完成后添加功能。");
            alert.showAndWait();
        });
        return openButton;
    }

    // property

    public boolean isButtonVisible() {
        return button.isVisible();
    }

    public void setButtonVisible(boolean visible) {
        button.setVisible(visible);
    }

    public int size() {
        return layerSelectComboBox.getItems().size();
    }

    // public method

    public void selectFirstItem() {
        for (LayerSelectComboBoxItem item : layerSelectComboBox.getItems()) {
            if (item.isCanSelect()) {
                layerSelectComboBox.getSelectionModel().select(item);
                break;
            }
        }
    }

    public String getSelectedItemUrl() {
        String url = "";
        LayerSelectComboBoxItem item = layerSelectComboBox.getSelectionModel().getSelectedItem();
        if (item != null) {
            DocumentItem documentItem = item.getDocumentItem();
            if (documentItem instanceof VectorLayer) {
                if (((VectorLayer) documentItem).getData() instanceof SFeatureCls) {
                    url = (((VectorLayer) documentItem).getData()).getURL();
                } else {
                    url = ((VectorLayer) documentItem).getURL();
                }
            } else if (documentItem instanceof ObjectLayer) {
                if (((ObjectLayer) documentItem).getData() instanceof ObjectCls) {
                    url = ((ObjectLayer) documentItem).getData().getURL();
                } else {
                    url = ((ObjectLayer) documentItem).getURL();
                }
            } else if (documentItem instanceof MapLayer) {
                url = ((MapLayer) documentItem).getURL();
            } else if (documentItem instanceof Map3DLayer) {
                url = ((Map3DLayer) documentItem).getURL();
            } else if (item.isCreateByUrl()) {
                url = item.getDocumentItemUrl();
            }
        }
        return url;
    }

    public DocumentItem getSelectedDocumentItem() {
        DocumentItem documentItem = null;
        LayerSelectComboBoxItem layerSelectComboBoxItem = layerSelectComboBox.getSelectionModel().getSelectedItem();
        if (layerSelectComboBoxItem != null) {
            documentItem = layerSelectComboBoxItem.getDocumentItem();
        }
        return documentItem;
    }

    public LayerSelectComboBoxItem getSelectedItem() {
        return layerSelectComboBox.getSelectionModel().getSelectedItem();
    }

    // event

    public void setOnSelectedItemChanged(ChangeListener<LayerSelectComboBoxItem> listener) {
        layerSelectComboBox.getSelectionModel().selectedItemProperty().addListener(listener);
    }
}
