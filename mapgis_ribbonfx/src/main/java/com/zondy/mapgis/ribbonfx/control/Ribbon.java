package com.zondy.mapgis.ribbonfx.control;

import com.zondy.mapgis.ribbonfx.skin.RibbonSkin;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * @file Ribbon.java
 * @brief Ribbon功能区
 *
 * @author CR
 * @date 2020-6-12
 */
public class Ribbon extends Control {
    private final static String DEFAULT_STYLE_CLASS = "ribbon";
    private final ObservableList<RibbonPage> tabs;
    private final SimpleObjectProperty<RibbonPage> selectedRibbonTab = new SimpleObjectProperty<>();
    private QuickAccessBar quickAccessBar;

    public Ribbon() {
        quickAccessBar = new QuickAccessBar();
        tabs = FXCollections.observableArrayList();
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
    }

    public ObservableList<RibbonPage> getTabs() {
        return tabs;
    }

    public SimpleObjectProperty selectedRibbonTabProperty() {
        return selectedRibbonTab;
    }

    public RibbonPage getSelectedRibbonTab() {
        return selectedRibbonTab.get();
    }

    public void setSelectedRibbonTab(RibbonPage ribbonPage) {
        selectedRibbonTab.set(ribbonPage);
    }

    public QuickAccessBar getQuickAccessBar() {
        return quickAccessBar;
    }

    public void setQuickAccessBar(QuickAccessBar qAccessBar) {
        quickAccessBar = qAccessBar;
    }

    @Override
    public String getUserAgentStylesheet() {
        return Ribbon.class.getResource("ribbonfx.css").toExternalForm();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new RibbonSkin(this);
    }
}
