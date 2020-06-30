package com.zondy.mapgis.dataconvert;/**
 * Created by Administrator on 2020/3/10.
 */

import com.zondy.mapgis.controls.common.ZDComboBox;
import com.zondy.mapgis.dataconvert.option.UnificationButton;
import com.zondy.mapgis.dataconvert.option.UnificationDialog;
import com.zondy.mapgis.map.Document;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class TestDataConvert extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
        Button button1 = new Button("数据转换");
        button1.setOnAction(event ->
        {
            //List<String> srcUrls = Arrays.asList("gdbp://MapGisLocal/中国地图/ds/中国地图/sfcls/省级行政区划",
            //        "gdbp://MapGisLocal/中国地图/ds/中国地图/sfcls/河流",
            //        "gdbp://MapGisLocal/中国地图/ds/中国地图/sfcls/全国县级城市",
            //        "gdbp://MapGisLocal/中国地图/ds/中国地图/acls/省名注记");
            List<String> srcUrls = Arrays.asList("D:\\GISData\\ArcGISData\\China\\背景图.shp", "D:\\GISData\\ArcGISData\\China\\全国公路线.shp");
            DataConverts.convert(primaryStage, srcUrls);
        });

        Button button2 = new Button("Test");
        button2.setOnAction(event ->
        {
        });

        ZDComboBox<String> comboBox = new ZDComboBox<>(FXCollections.observableArrayList("aaa", "bbbbbbbbb"));
        comboBox.setValue("aaa");
        VBox vBox = new VBox(6, button1, button2, comboBox);
        vBox.setPadding(new Insets(12));
        primaryStage.setTitle("test");
        primaryStage.setScene(new Scene(vBox, 400, 500));
        primaryStage.show();
    }
}
