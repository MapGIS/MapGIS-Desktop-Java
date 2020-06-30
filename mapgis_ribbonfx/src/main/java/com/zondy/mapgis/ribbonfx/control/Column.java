package com.zondy.mapgis.ribbonfx.control;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;

/**
 * @file Column.java
 * @brief 列（一列2-3个小图标按钮）
 *
 * @author CR
 * @date 2020-6-12
 */
public class Column extends VBox {
    public Column() {
        super.setPadding(new Insets(6, 3, 6, 3));
        super.setSpacing(6);
    }
}