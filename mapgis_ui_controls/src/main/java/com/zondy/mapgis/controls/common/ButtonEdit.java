package com.zondy.mapgis.controls.common;

import com.zondy.mapgis.controls.wizard.WizardEvent;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;

/**
 * ButtonEdit控件：自动包含“..."按钮，若不需要可删除
 *
 * @author CR
 * @file ButtonEdit.java
 * @brief ButtonEdit控件
 * @create 2020-03-11.
 */
public class ButtonEdit extends HBox {
    private final static String DEFAULT_STYLE_CLASS = "button-edit";

    private final TextField textField = new TextField();
    private final Button button = new Button("选择", new ImageView(new Image(getClass().getResourceAsStream("selectbutton.png"))));
    private ObjectProperty<Tooltip> tooltip = new SimpleObjectProperty<>();
    private ObservableList<Button> buttons = FXCollections.observableArrayList();

    /**
     * ButtonEdit控件：自动包含“..."按钮，若不需要可删除
     */
    public ButtonEdit() {
        this("");
    }

    public ButtonEdit(String text) {
        this(text, new Button[]{});
    }

    public ButtonEdit(Button... buttons) {
        this("", buttons);
    }

    public ButtonEdit(String text, Button... buttons) {
        this.setText(text);
        this.getChildren().addAll(this.textField);
        HBox.setHgrow(this.textField, Priority.ALWAYS);
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);

        this.buttons.addListener((ListChangeListener<Button>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    getChildren().addAll(c.getAddedSubList());

                    for (Button button : c.getAddedSubList()) {
                        button.setOnAction(event ->
                        {
                            fireEvent(new ButtonEditEvent(ButtonEditEvent.BUTTONCLICK, button));
                        });
                    }
                } else if (c.wasRemoved()) {
                    getChildren().removeAll(c.getRemoved());
                }
            }
        });

        this.buttons.add(this.button);
        if (buttons != null) {
            this.buttons.addAll(buttons);
        }

        this.textField.setOnKeyPressed(event ->
        {
            fireEvent(event);
        });
        this.setTextEditable(false);
        this.setAlignment(Pos.CENTER);
    }

    @Override
    public String getUserAgentStylesheet() {
        return getClass().getResource("buttonedit.css").toExternalForm();
    }

    public void setButtonVisible(boolean visible) {
        for (Button button : this.getButtons()) {
            button.setManaged(visible);
            button.setVisible(visible);
        }
    }

    //region 属性
    public ObservableList<Button> getButtons() {
        return buttons;
    }

    public String getText() {
        return textProperty().get();
    }

    public StringProperty textProperty() {
        return this.textField.textProperty();
    }

    public void setText(String text) {
        this.textProperty().set(text);
    }

    public boolean getTextEditable() {
        return this.textField.editableProperty().get();
    }

    public BooleanProperty textEditableProperty() {
        return this.textField.editableProperty();
    }

    public void setTextEditable(boolean textEditable) {
        this.textField.editableProperty().set(textEditable);
    }

    public Tooltip getTooltip() {
        return tooltip.get();
    }

    public ObjectProperty<Tooltip> tooltipProperty() {
        return tooltip;
    }

    public void setTooltip(Tooltip toolTip) {
        this.tooltip.set(toolTip);
        this.textField.setTooltip(toolTip);
        this.button.setTooltip(toolTip);
    }
    //endregion

    //region 事件
    private final ObjectProperty<EventHandler<ButtonEditEvent>> onButtonClick = new ObjectPropertyBase<EventHandler<ButtonEditEvent>>() {
        @Override
        protected void invalidated() {
            setEventHandler(ButtonEditEvent.BUTTONCLICK, get());
        }

        @Override
        public Object getBean() {
            return this;
        }

        @Override
        public String getName() {
            return "onButtonClick";
        }
    };
    private final ObjectProperty<EventHandler<KeyEvent>> onTextKeyPressed = new ObjectPropertyBase<EventHandler<KeyEvent>>() {
        @Override
        protected void invalidated() {
            setEventHandler(KeyEvent.KEY_PRESSED, get());
        }

        @Override
        public Object getBean() {
            return this;
        }

        @Override
        public String getName() {
            return "onTextKeyPressed";
        }
    };

    public EventHandler<ButtonEditEvent> getOnButtonClick() {
        return onButtonClick.get();
    }

    public ObjectProperty<EventHandler<ButtonEditEvent>> onButtonClickProperty() {
        return onButtonClick;
    }

    public void setOnButtonClick(EventHandler<ButtonEditEvent> onButtonClick) {
        this.onButtonClick.set(onButtonClick);
    }

    public EventHandler<KeyEvent> getOnTextKeyPressed() {
        return onTextKeyPressed.get();
    }

    public ObjectProperty<EventHandler<KeyEvent>> onTextKeyPressedProperty() {
        return onTextKeyPressed;
    }

    public void setOnTextKeyPressed(EventHandler<KeyEvent> onTextKeyPressed) {
        this.onTextKeyPressed.set(onTextKeyPressed);
    }
    //endregion
}
