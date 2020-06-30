package com.zondy.mapgis.ribbonfx.skin;

import com.zondy.mapgis.ribbonfx.control.Ribbon;
import com.zondy.mapgis.ribbonfx.control.RibbonPage;
import javafx.collections.ListChangeListener;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

import java.util.Collection;

/**
 * @file RibbonSkin.java
 * @brief Ribbon功能区皮肤
 *
 * @author CR
 * @date 2020-6-12
 */
public class RibbonSkin extends SkinBase<Ribbon> {
    private final VBox outerContainer;
    private final TabPane tabPane;

  public RibbonSkin(Ribbon control) {
        super(control);
        tabPane = new TabPane();
        outerContainer = new VBox();

        control.getTabs().addListener(this::tabsChanged);
        updateAddedRibbonTabs(control.getTabs());

        outerContainer.getStyleClass().setAll("outer-container");
        outerContainer.getChildren().addAll(control.getQuickAccessBar(), tabPane);
        getChildren().add(outerContainer);

        control.selectedRibbonTabProperty().addListener((observable, oldValue, newValue) -> tabPane.getSelectionModel().select((RibbonPage) newValue));
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> control.setSelectedRibbonTab((RibbonPage) tabPane.getSelectionModel().getSelectedItem()));
    }

    private void updateAddedRibbonTabs(Collection<? extends RibbonPage> ribbonTabs) {
        for (RibbonPage ribbonPage : ribbonTabs) {
            tabPane.getTabs().add(ribbonPage);
        }
    }

    private void tabsChanged(ListChangeListener.Change<? extends RibbonPage> changed) {
        while (changed.next()) {
            if (changed.wasAdded()) {
                updateAddedRibbonTabs(changed.getAddedSubList());
            }
            if (changed.wasRemoved()) {
                for (RibbonPage ribbonPage : changed.getRemoved()) {
                    tabPane.getTabs().remove(ribbonPage);
                }
            }
        }
    }
}
