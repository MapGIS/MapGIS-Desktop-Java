package com.zondy.mapgis.ribbonfx.control;

import com.zondy.mapgis.ribbonfx.skin.QuickAccessBarSkin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * @file QuickAccessBar.java
 * @brief 快速访问栏
 *
 * @author CR
 * @date 2020-6-12
 */
public class QuickAccessBar extends Control {
    private final static String DEFAULT_STYLE_CLASS = "quick-access-bar";
    private final ObservableList<Button> buttons;
    private final ObservableList<Button> rightButtons;

    public QuickAccessBar() {
        buttons = FXCollections.observableArrayList();
        rightButtons = FXCollections.observableArrayList();

        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
    }

    /**
     * 快速访问栏左侧的按钮
     *
     * @return
     */
    public ObservableList<Button> getButtons() {
        return buttons;
    }

    /**
     * 快速访问栏右侧的按钮
     *
     * @return
     */
    public ObservableList<Button> getRightButtons() {
        return rightButtons;
    }

    /**
     * 快速访问栏左右侧的所有按钮
     *
     * @return
     */
    public ObservableList<Button> getAllButtons() {
        ObservableList<Button> allButtons = FXCollections.observableArrayList(buttons);
        allButtons.addAll(rightButtons);
        return allButtons;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new QuickAccessBarSkin(this);
    }
}
