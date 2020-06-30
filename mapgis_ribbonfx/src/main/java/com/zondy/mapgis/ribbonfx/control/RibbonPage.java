package com.zondy.mapgis.ribbonfx.control;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;

import java.util.List;

/**
 * @file RibbonPage.java
 * @brief Ribbon页
 *
 * @author CR
 * @date 2020-6-12
 */
public class RibbonPage extends Tab {
    private static final String DEFAULT_STYLE_CLASS = "ribbon-page";
    private final ObservableList<RibbonGroup> ribbonGroups = FXCollections.observableArrayList();
    private HBox content;
    private String contextualColor;

    public RibbonPage() {
        init();
    }

    public RibbonPage(String title) {
        super(title);
        init();
    }

    private void init() {
        content = new HBox();
        this.setContent(content);
        setClosable(false);

        ribbonGroups.addListener(this::groupsChanged);
        content.getStyleClass().setAll(DEFAULT_STYLE_CLASS, "tab");
        getStyleClass().addListener((ListChangeListener<String>) c ->
        {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (String style : c.getAddedSubList()) {
                        content.getStyleClass().add(style);
                    }
                }
            }
        });
    }

    public void setContextualColor(String color) {
        contextualColor = color;
        getStyleClass().add(color);
    }

    public String getContextualColor() {
        return contextualColor;
    }

    private void groupsChanged(ListChangeListener.Change<? extends RibbonGroup> changed) {
        while (changed.next()) {
            if (changed.wasAdded()) {
                updateAddedGroups(changed.getAddedSubList());
            }
            if (changed.wasRemoved()) {
                for (RibbonGroup group : changed.getRemoved()) {
                    int groupIndex = content.getChildren().indexOf(group);
                    if (groupIndex != 0) {
                        content.getChildren().remove(groupIndex - 1); // Remove separator
                    }
                    content.getChildren().remove(group);
                }
            }
        }
    }

    private void updateAddedGroups(List<? extends RibbonGroup> addedSubList) {
        for (RibbonGroup group : addedSubList) {
            content.getChildren().add(group);
        }
    }

    public ObservableList<RibbonGroup> getRibbonGroups() {
        return ribbonGroups;
    }
}
