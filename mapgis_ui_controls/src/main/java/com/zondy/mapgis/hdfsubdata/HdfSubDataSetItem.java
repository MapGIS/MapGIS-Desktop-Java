package com.zondy.mapgis.hdfsubdata;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 */
public class HdfSubDataSetItem {
    private SimpleIntegerProperty id = null;
    private SimpleStringProperty url = null;
    private SimpleStringProperty description = null;

    public HdfSubDataSetItem()
    {
        id = new SimpleIntegerProperty();
        url = new SimpleStringProperty();
        description = new SimpleStringProperty();
    }

    public HdfSubDataSetItem(int ID, String Url, String Description)
    {
        id = new SimpleIntegerProperty(ID);
        url = new SimpleStringProperty(Url);
        description = new SimpleStringProperty(Description);
    }

    public int getId()
    {
        return id.get();
    }

    public void setId(int Id)
    {
        id.set(Id);
    }

    public IntegerProperty idProperty()
    {
        return id;
    }

    public String getUrl()
    {
        return url.get();
    }

    public void setUrl(String Url)
    {
        url.set(Url);
    }

    public StringProperty urlProperty()
    {
        return url;
    }

    public String getDescription()
    {
        return description.get();
    }

    public void setDescription(String Description)
    {
        description.set(Description);
    }

    public StringProperty descriptionProperty()
    {
        return description;
    }

}
