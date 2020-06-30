package com.zondy.mapgis.workspace;

import com.zondy.mapgis.map.Document;
import com.zondy.mapgis.map.Map;
import com.zondy.mapgis.map.Maps;
import com.zondy.mapgis.workspace.engine.IItemStyle;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import com.zondy.mapgis.workspace.enums.ItemType;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.ArrayList;

/**
 * @author cxy
 * @date 2019/12/18
 */
public class AddMapRefLayerDialog extends Dialog {
    private ListView<HBox> listView;

    public AddMapRefLayerDialog(Document document, IWorkspace workspace) {
        // region 初始化 Dialog
        this.setTitle("添加地图引用");
        this.listView = new ListView<>();
        this.listView.setCellFactory(new Callback<ListView<HBox>, ListCell<HBox>>() {
            @Override
            public ListCell<HBox> call(ListView<HBox> param) {
                return null;
            }
        });
        TitledPane titledPane = new TitledPane("请选择要引用的地图:", this.listView);
        titledPane.setCollapsible(false);
        titledPane.setPrefWidth(150);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(titledPane);

        DialogPane dialogPane = this.getDialogPane();
        dialogPane.setPrefWidth(300);
        dialogPane.setPrefHeight(430);
        dialogPane.setContent(vBox);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        // endregion

        if (document != null && workspace != null) {
            Image image = workspace.getItemStyle(ItemType.MAP).getImage();
            Maps maps = document.getMaps();
            for (int i = 0; i < maps.getCount(); i++) {
                Map map = maps.getMap(i);
                TextField textField = new TextField(map.getName());
                textField.setUserData(map);
                this.listView.getItems().add(new HBox(new ImageView(image), textField));
            }
        }
    }

    public ArrayList<Map> getSelectMapList() {
        ArrayList<Map> mapList = new ArrayList<>();
        if (this.listView.getSelectionModel().getSelectedItems().size() > 0) {
            for (HBox hBox : this.listView.getSelectionModel().getSelectedItems()) {
                mapList.add((Map) hBox.getChildren().get(1).getUserData());
            }
        }
        return mapList;
    }
}
