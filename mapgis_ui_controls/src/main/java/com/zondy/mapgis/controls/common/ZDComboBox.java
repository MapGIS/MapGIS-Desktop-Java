package com.zondy.mapgis.controls.common;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.AccessibleRole;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionModel;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;

/**
 * @author CR
 * @file ZDComboBox.java
 * @brief 改写版的ComboBox（背景白色）
 * @create 2020-04-02.
 */
public class ZDComboBox<T> extends ComboBox<T>
{
    public ZDComboBox()
    {
        this(FXCollections.<T>observableArrayList());
    }

    public ZDComboBox(T... items)
    {
        this(FXCollections.<T>observableArrayList(items));
    }

    public ZDComboBox(ObservableList<T> items)
    {
        super(items);
        this.setBackground(new Background(new BackgroundFill(Paint.valueOf("white"), null, null)));
        this.setBorder(new Border(new BorderStroke(Paint.valueOf("#B5B5B5"), BorderStrokeStyle.SOLID, new CornerRadii(2), BorderStroke.THIN)));
       }
}
