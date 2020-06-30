package com.zondy.mapgis.ribbonfx.skin;

import com.zondy.mapgis.ribbonfx.control.RibbonGroup;
import javafx.collections.ListChangeListener;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Collection;

/**
 * @file RibbonGroupSkin.java
 * @brief 页面皮肤
 *
 * @author CR
 * @date 2020-6-12
 */
public class RibbonGroupSkin extends SkinBase<RibbonGroup> {
    private final static int DEFAULT_SPACING = 0;
    private final HBox content;
    private final HBox container;
    private final Label title;

    public RibbonGroupSkin(RibbonGroup control) {
        super(control);
        content = new HBox();
        content.setAlignment(Pos.CENTER);
        content.setSpacing(DEFAULT_SPACING);

        Separator separator = new Separator(Orientation.VERTICAL);

        container = new HBox();

        title = new Label();
        StackPane titleContainer = new StackPane();
        titleContainer.getChildren().add(title);

        title.textProperty().bind(control.titleProperty());
        titleContainer.getStyleClass().setAll("title-container");
        title.getStyleClass().setAll("title");

        control.getNodes().addListener(this::buttonsChanged);
        updateAddedButtons(control.getNodes());

        VBox vBox = new VBox();
        vBox.getChildren().addAll(content, titleContainer);
        container.getChildren().addAll(vBox, separator);

        getChildren().add(container);

        content.getStyleClass().setAll("ribbon-group-content");
    }

    private void updateAddedButtons(Collection<? extends Node> nodes) {
        for (Node node : nodes) {
            content.getChildren().add(node);
        }
    }

    private void buttonsChanged(ListChangeListener.Change<? extends Node> changed) {
        while (changed.next()) {
            if (changed.wasAdded()) {
                updateAddedButtons(changed.getAddedSubList());
            }
            if (changed.wasRemoved()) {
                for (Node node : changed.getRemoved()) {
                    content.getChildren().remove(node);
                }
            }
        }
    }
}
