package com.zondy.mapgis.dataconvert.option;

import com.zondy.mapgis.base.XString;
import com.zondy.mapgis.controls.common.ZDComboBox;
import com.zondy.mapgis.dataconvert.CustomOperate;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;

import javax.xml.bind.annotation.XmlSchema;

/**
 * @author CR
 * @file SourcePane.java
 * @brief 源信息界面
 * @create 2020-03-25.
 */
public class SourcePane extends GridPane
{
    private final TextField textField = new TextField();
    private final ZDComboBox<String> comboBox = new ZDComboBox<>(FXCollections.observableArrayList("Default", "UTF-8", "GB2312"));
    private final Label labelEncoding = new Label("源字符集:");
    private StringProperty encoding = new SimpleStringProperty("Default");

    public SourcePane()
    {
        this.textField.setEditable(false);
        this.setBackground(new Background(new BackgroundFill(Paint.valueOf("transparent"), null, null)));
        this.comboBox.getSelectionModel().select(0);
        this.encoding.bind(comboBox.valueProperty());
        this.comboBox.setValue("Default");
        this.comboBox.prefWidthProperty().bind(textField.widthProperty());

        this.setHgap(6);
        this.setVgap(6);
        this.add(new javafx.scene.control.Label("源数据路径:"), 0, 0);
        this.add(this.textField, 1, 0);
        this.add(this.labelEncoding, 0, 1);
        this.add(this.comboBox, 1, 1);
        GridPane.setHgrow(this.textField, Priority.ALWAYS);

        this.setPadding(new Insets(12));
        this.getColumnConstraints().add(0, new ColumnConstraints(94));
    }

    public void setSrcPath(String srcPath)
    {
        this.textField.setText(srcPath);
        if (!XString.isNullOrEmpty(srcPath))
        {
            boolean isGDBData = (srcPath.toLowerCase().startsWith(CustomOperate.gdbProName));
            this.labelEncoding.setManaged(!isGDBData);
            this.labelEncoding.setVisible(!isGDBData);
            this.comboBox.setManaged(!isGDBData);
            this.comboBox.setVisible(!isGDBData);
        }
    }

    public String getEncoding()
    {
        return encoding.get();
    }

    public StringProperty encodingProperty()
    {
        return encoding;
    }

    public void setEncoding(String encoding)
    {
        this.encoding.set(encoding);
    }
}
