package com.zondy.mapgis.rastereditor.dialogs;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 栅格计算器待计算的条目
 */
public class FormulaItem {
    private SimpleStringProperty aliasName = null;
    private SimpleStringProperty rasterName = null;
    //private SimpleStringProperty bandNo = null;

    public FormulaItem(String AliasName, String RasterName) //, String BandNo)
    {
        aliasName = new SimpleStringProperty(AliasName);
        rasterName = new SimpleStringProperty(RasterName);
        //bandNo = new SimpleStringProperty(BandNo);
    }

    public String getAliasName()
    {
        return aliasName.get();
    }

    public void setAliasName(String AliasName)
    {
        aliasName.set(AliasName);
    }

    public StringProperty aliasNameProperty()
    {
        return aliasName;
    }

    String getRasterName()
    {
        return rasterName.get();
    }

    void setRasterName(String RasterName)
    {
        rasterName.set(RasterName);;
    }

    public StringProperty rasterNameProperty()
    {
        return rasterName;
    }

//    public String getBandNo()
//    {
//        return bandNo.get();
//    }
//
//    public void setBandNo(String BandNo)
//    {
//        bandNo.set(BandNo);
//    }
//
//    public StringProperty bandNoProperty()
//    {
//        return bandNo;
//    }

}
