package com.zondy.mapgis.ribbonfx.skin;

import com.zondy.mapgis.ribbonfx.control.RibbonItem;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

/**
 * @file RibbonItemSkin.java
 * @brief Ribbon项的皮肤
 *
 * @author CR
 * @date 2020-6-12
 */
public class RibbonItemSkin extends SkinBase<RibbonItem> {
    private final BorderPane borderPane;
    private final Label label;

     public RibbonItemSkin(RibbonItem control) {
        super(control);

        borderPane = new BorderPane();
        label = new Label();

        control.graphicProperty().addListener((observable, oldValue, newValue) -> graphicChanged());
        control.labelPropery().addListener((observable, oldValue, newValue) -> labelChanged());
        control.itemProperty().addListener((observable, oldValue, newValue) -> itemChanged());

        String text = control.getLabel();
        Node graphic = control.getGraphic();
        if ((text != null && !text.isEmpty()) || graphic != null) {
            if (text != null && !text.isEmpty()) {
                label.setText(text);
            }
            if (control.getGraphic() != null) {
                label.setGraphic(graphic);
            }
            addLabel();
        }

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(control.getItem());
        borderPane.setRight(stackPane);

        getChildren().add(borderPane);
    }

    private void itemChanged() {
        Node item = getSkinnable().getItem();
        if (item != null) {
            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(item);
            borderPane.setRight(stackPane);
        }
    }

    private void labelChanged() {
        String labelText = getSkinnable().getLabel();
        if (labelText != null) {
            label.setText(labelText);
            addLabel();
        }
    }

    private void graphicChanged() {
        Node graphic = getSkinnable().getGraphic();
        if (graphic != null) {
            label.setGraphic(getSkinnable().getGraphic());
            addLabel();
        }
    }

    private void addLabel() {
        if (borderPane.getLeft() == null) {
            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(label);
            borderPane.setLeft(stackPane);
        }
    }
}
