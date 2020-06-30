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

public class test extends Application
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

        Button button2 = new Button("数据导入");
        button2.setOnAction(event ->
        {
            //"gdbp://MapGisLocal/中国地图/ocls/Class"
            DataConverts.convert(primaryStage, "D:\\GISData\\ArcGISData\\China\\背景图.shp");
        });

        Button button3 = new Button("数据导出");
        button3.setOnAction(event ->
        {
            //String[] aa = {"gdbp://MapGisLocal/DC/ds/地图综合", "gdbp://MapGisLocal/DC", "D:\\GISData\\ArcGISData\\mdb\\全国50万接图表.mdb"};
            String[] aa = {"D:\\GISData\\1-矢量数据\\6x\\Ku6_1.wl", "D:\\GISData\\1-矢量数据\\6x\\Ku6_2.wl", "D:\\GISData\\1-矢量数据\\6x\\Ku6_3.wl"};
            DataConverts.convert(primaryStage, aa);
        });

        Button button4 = new Button("Test");
        button4.setOnAction(event ->
        {
            Stage stage = new Stage();

            ConvertOption option = new ConvertOption();
            //HBox pane = ConvertOption.createPersonalGDBPane(option);
            //pane.setPadding(new Insets(12));
            //stage.setScene(new Scene(pane));
            stage.show();

            option.pgdbVersionProperty().addListener((o, ov, nv) -> System.out.println(nv));
        });

        Button button5 = new Button("Test00000000000");
        button5.setOnAction(event ->
        {
            UnificationDialog dlg = new UnificationDialog(null);
            dlg.show();
        });

        ZDComboBox<String> comboBox = new ZDComboBox<>(FXCollections.observableArrayList("aaa", "bbbbbbbbb"));
        comboBox.setValue("aaa");
        VBox vBox = new VBox(6, button1, button2, button3, button4, button5, comboBox);
        vBox.setPadding(new Insets(12));
        primaryStage.setTitle("test");
        primaryStage.setScene(new Scene(vBox, 400, 500));
        primaryStage.show();
    }
}
