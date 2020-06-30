package com.zondy.mapgis.controls.common;

import com.zondy.mapgis.controls.wizard.WizardEvent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.control.Button;

/**
 * @author CR
 * @file ButtonEditEvent.java
 * @brief ButtonEdit的事件
 * @create 2020-04-07.
 */
public class ButtonEditEvent extends Event
{
    public static final EventType<ButtonEditEvent> BUTTONCLICK = new EventType<ButtonEditEvent>(Event.ANY, "BUTTONCLICK");

    private Button button;

    public ButtonEditEvent(EventType<? extends Event> eventType, Button button)
    {
        super(eventType);
        this.button = button;
    }

    public Button getButton()
    {
        return button;
    }
}
