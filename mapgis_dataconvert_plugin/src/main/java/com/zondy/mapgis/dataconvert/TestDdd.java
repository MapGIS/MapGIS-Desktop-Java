package com.zondy.mapgis.dataconvert;

import com.zondy.mapgis.controls.common.ZDToolBar;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @author CR
 * @file TestDdd.java
 * @brief
 * @create 2020-04-24.
 */
public class TestDdd extends Dialog
{
    private TableView<Testt> tableView = new TableView<>();

    public TestDdd()
    {
        Button buttonAdd = new Button("添加数据", new ImageView(new Image(getClass().getResourceAsStream("addFiles_20.png"))));
        Button buttonRemove = new Button("移除", new ImageView(new Image(getClass().getResourceAsStream("remove_20.png"))));
        buttonAdd.setOnAction(event -> this.tableView.getItems().add(new Testt("aaa", 18)));
        buttonRemove.setOnAction(event -> this.tableView.getItems().clear());
        ZDToolBar toolBar = new ZDToolBar(buttonAdd, buttonRemove);

        TableColumn<Testt, String> tcName = new TableColumn<>("名称");
        TableColumn<Testt, Integer> tcAge = new TableColumn<>("年龄");
        tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcAge.setCellValueFactory(new PropertyValueFactory<>("age"));

        this.tableView.getColumns().addAll(tcName, tcAge);
        this.tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        DialogPane dialogPane = this.getDialogPane();
        dialogPane.setContent(new VBox(toolBar, tableView));
        dialogPane.setPrefSize(400, 300);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    }

    public static class Testt
    {
        StringProperty name = new SimpleStringProperty();
        IntegerProperty age = new SimpleIntegerProperty();

        public Testt(String name, int age)
        {
            this.name.set(name);
            this.age.set(age);
        }

        public String getName()
        {
            return name.get();
        }

        public StringProperty nameProperty()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name.set(name);
        }

        public int getAge()
        {
            return age.get();
        }

        public IntegerProperty ageProperty()
        {
            return age;
        }

        public void setAge(int age)
        {
            this.age.set(age);
        }
    }
}
