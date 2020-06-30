package com.zondy.mapgis.gdbmanager;

import com.zondy.mapgis.gdbmanager.gdbcatalog.CreateNetclsDialog;
import com.zondy.mapgis.gdbmanager.gdbcatalog.GDBCatalogPane;
import com.zondy.mapgis.gdbmanager.gdbcatalog.MoveToFdsDialog;
import com.zondy.mapgis.gdbmanager.gdbcatalog.index.IndexManagerDialog;
import com.zondy.mapgis.geodatabase.DataBase;
import com.zondy.mapgis.geodatabase.XClsType;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * @author CR
 * @file TestGDBManager.java
 * @brief
 * @create 2020-04-22.
 */
public class TestGDBManager extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
        Button button1 = new Button("创建网络类");
        Button button2 = new Button("GDBCatalog");
        Button button3 = new Button("移动到数据集");
        Button button4 = new Button("索引管理");

        VBox vBox = new VBox(6, button1, button2, button3, button4);
        vBox.setPadding(new Insets(12));
        primaryStage.setTitle("TestGDBManager");
        primaryStage.setScene(new Scene(vBox, 400, 500));
        primaryStage.show();

        button1.setOnAction(event ->
        {
            DataBase db = DataBase.openByURL("gdbp://MapGISLocalPlus/Chong");
            if (db != null && db.hasOpened())
            {
                CreateNetclsDialog dlg = new CreateNetclsDialog(primaryStage, db, 9);
                dlg.showAndWait();
                db.close();
            }
        });
        button2.setOnAction(event ->
        {
            Stage stage = new Stage();
            stage.setScene(new Scene(new GDBCatalogPane(null), 400, 800));
            stage.show();
        });
        button3.setOnAction(event ->
        {
            DataBase db = DataBase.openByURL("gdbp://MapGISLocalPlus/aaa");
            if (db != null && db.hasOpened())
            {
                MoveToFdsDialog dlg = new MoveToFdsDialog(db, 0, XClsType.XSFCls, 50);
                dlg.initOwner(primaryStage);
                if (dlg.showAndWait().equals(Optional.of(ButtonType.OK)))
                {
                    System.out.println("aaaa");
                }

                MoveToFdsDialog dlg1 = new MoveToFdsDialog(db, 0, XClsType.XSFCls, 53);
                dlg1.initOwner(primaryStage);
                if (dlg1.showAndWait().equals(Optional.of(ButtonType.OK)))
                {
                    System.out.println("bbbbb");
                }

                MoveToFdsDialog dlg2 = new MoveToFdsDialog(db, 0, XClsType.XSFCls, 55);
                dlg2.initOwner(primaryStage);
                if (dlg2.showAndWait().equals(Optional.of(ButtonType.OK)))
                {
                    System.out.println("ccccc");
                }
                db.close();
            }
        });
        button4.setOnAction(event ->
        {
            DataBase db = DataBase.openByURL("gdbp://MapGISLocalPlus/hhhtest");
            if (db != null && db.hasOpened())
            {
                IndexManagerDialog dlg = new IndexManagerDialog(db, XClsType.XSFCls, 2);
                dlg.initOwner(primaryStage);
                dlg.showAndWait();
                db.close();
            }
        });
    }
}