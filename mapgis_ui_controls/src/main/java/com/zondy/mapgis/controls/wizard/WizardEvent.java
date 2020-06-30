package com.zondy.mapgis.controls.wizard;

import javafx.beans.property.*;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 * Created by Administrator on 2020/3/6.
 */
public class WizardEvent extends Event
{
    public static final EventType<WizardEvent> FINISH = new EventType<WizardEvent>(Event.ANY, "FINISH");
    public static final EventType<WizardEvent> HELP = new EventType<WizardEvent>(Event.ANY, "HELP");
    public static final EventType<WizardEvent> CANCEL = new EventType<WizardEvent>(Event.ANY, "CANCEL");
    public static final EventType<WizardEvent> SELECTEDPAGECHANGING = new EventType<WizardEvent>(Event.ANY, "SELECTEDPAGECHANGING");
    public static final EventType<WizardEvent> SELECTEDPAGECHANGED = new EventType<WizardEvent>(Event.ANY, "SELECTEDPAGECHANGED");
    public static final EventType<WizardEvent> PAGEVALIDATING = new EventType<WizardEvent>(Event.ANY, "PAGEVALIDATING");

    private BooleanProperty cancel = new SimpleBooleanProperty(false);//是否取消后续工作，如点击Finish验证之后不通过，将此值设为true便可不关闭界面
    private WizardPage oldPage;
    private WizardPage page;
    private Direction direction = Direction.Forward;
    private StringProperty errorText = new SimpleStringProperty();
    private BooleanProperty valid = new SimpleBooleanProperty(true);

    public WizardEvent(EventType<WizardEvent> eventType)
    {
        super(eventType);
    }

    public WizardEvent(EventType<WizardEvent> eventType, WizardPage oldPage, WizardPage page, Direction direction)
    {
        super(eventType);
        this.oldPage = oldPage;
        this.page = page;
        this.direction = direction;
    }

    public WizardEvent(Object source, EventTarget target, EventType<WizardEvent> eventType)
    {
        super(source, target, eventType);
    }

    public boolean isCancel()
    {
        return cancel.get();
    }

    public BooleanProperty cancelProperty()
    {
        return cancel;
    }

    public void setCancel(boolean cancel)
    {
        this.cancel.set(cancel);
    }

    public WizardPage getOldPage()
    {
        return oldPage;
    }

    public WizardPage getPage()
    {
        return page;
    }

    public Direction getDirection()
    {
        return direction;
    }

    public String getErrorText()
    {
        return errorText.get();
    }

    public StringProperty errorTextProperty()
    {
        return errorText;
    }

    public void setErrorText(String errorText)
    {
        this.errorText.set(errorText);
    }

    public boolean isValid()
    {
        return valid.get();
    }

    public BooleanProperty validProperty()
    {
        return valid;
    }

    public void setValid(boolean valid)
    {
        this.valid.set(valid);
    }

    @Override
    public WizardEvent copyFor(Object newSource, EventTarget newTarget)
    {
        return (WizardEvent) super.copyFor(newSource, newTarget);
    }

    @Override
    public EventType<? extends WizardEvent> getEventType()
    {
        return (EventType<? extends WizardEvent>) super.getEventType();
    }
}

