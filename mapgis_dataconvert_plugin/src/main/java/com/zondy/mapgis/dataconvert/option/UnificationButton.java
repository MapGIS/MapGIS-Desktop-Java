package com.zondy.mapgis.dataconvert.option;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * @author CR
 * @file UnifiedModifyPane.java
 * @brief 统改按扭，使其保持在靠右边
 * @create 2020-03-25.
 */
public class UnificationButton extends HBox
{
    private final ObjectProperty<EventHandler<ActionEvent>> onAction = new SimpleObjectProperty<>();

    public UnificationButton()
    {
        Region region = new Region();
        Button button = new Button("统改同类型转换项");
        this.getChildren().addAll(region, button);
        HBox.setHgrow(region, Priority.ALWAYS);

        button.onActionProperty().bind(onAction);
    }

    public EventHandler<ActionEvent> getOnAction()
    {
        return onAction.get();
    }

    public ObjectProperty<EventHandler<ActionEvent>> onActionProperty()
    {
        return onAction;
    }

    public void setOnAction(EventHandler<ActionEvent> onAction)
    {
        this.onAction.set(onAction);
    }
}
