package com.zondy.mapgis.ribbonfx.skin;

import com.zondy.mapgis.ribbonfx.control.QuickAccessBar;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.Collection;

/**
 * @file QuickAccessBarSkin.java
 * @brief 快速访问栏的皮肤
 *
 * @author CR
 * @date 2020-6-12
 */
public class QuickAccessBarSkin extends SkinBase<QuickAccessBar> {
    private final BorderPane outerContainer;
    private final HBox buttonContainer;
    private final HBox rightButtons;

    public QuickAccessBarSkin(QuickAccessBar control) {
        super(control);
        buttonContainer = new HBox();
        buttonContainer.getStyleClass().add("button-container");
        rightButtons = new HBox();
        outerContainer = new BorderPane();
        outerContainer.getStyleClass().add("outer-container");
        outerContainer.setCenter(buttonContainer);
        outerContainer.setRight(rightButtons);
        getChildren().add(outerContainer);
        updateAddedButtons(control.getButtons());
    }

    private void buttonsChanged(ListChangeListener.Change<? extends Button> changed) {
        while (changed.next()) {
            if (changed.wasAdded()) {
                updateAddedButtons(changed.getAddedSubList());
            }
            if (changed.wasRemoved()) {
                for (Button button : changed.getRemoved()) {
                    buttonContainer.getChildren().remove(button);
                }
            }
        }
    }

    private void updateAddedButtons(Collection<? extends Button> addedSubList) {
        QuickAccessBar skinnable = getSkinnable();
        for (Button button : skinnable.getButtons()) {
            buttonContainer.getChildren().add(button);
        }
        for (Button button : skinnable.getRightButtons()) {
            rightButtons.getChildren().add(button);
        }
    }
}
