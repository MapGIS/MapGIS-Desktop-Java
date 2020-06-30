package com.zondy.mapgis.rastereditor.dialogs;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 栅格重分类条目
 */
public class ReclassItem {
    private SimpleStringProperty oldValue = null;
    private SimpleIntegerProperty newValue = null;

    public ReclassItem(String OldValue, Integer NewValue)
    {
        oldValue = new SimpleStringProperty(OldValue);
        newValue = new SimpleIntegerProperty(NewValue);
    }

    public String getOldValue()
    {
        return oldValue.get();
    }

    public void setOldValue(String OldValue)
    {
        oldValue.set(OldValue);
    }

    public StringProperty oldValueProperty()
    {
        return oldValue;
    }

    public Integer getNewValue()
    {
        return newValue.get();
    }

    public void setNewValue(Integer NewValue)
    {
        newValue.set(NewValue);
    }

    public IntegerProperty newValueProperty()
    {
        return newValue;
    }
}
