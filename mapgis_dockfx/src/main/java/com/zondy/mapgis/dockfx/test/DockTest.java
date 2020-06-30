package com.zondy.mapgis.dockfx.test;

import com.zondy.mapgis.dockfx.dock.DockPane;
import com.zondy.mapgis.dockfx.dock.DockPos;
import com.zondy.mapgis.dockfx.dock.DockView;
import com.zondy.mapgis.dockfx.dock.DockWindow;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;


/**
 * @file DockTest.java
 * @brief
 *
 * @author CR
 * @date 2020-06-23.
 */
public class DockTest extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        DockPane dockPane = new DockPane();
        VBox vbox = new VBox(6, new MenuBar(new Menu("File"), new Menu("Options")), dockPane);
        VBox.setVgrow(dockPane, Priority.ALWAYS);

        primaryStage.setTitle("DockTest");
        primaryStage.setScene(new Scene(vbox, 800, 500));
        primaryStage.sizeToScene();
        primaryStage.show();

        DockPane.initDefaultUserAgentStylesheet();//必须在show之后初始化才有用

         TextArea textArea = new TextArea("rrrrrrrrrrrrrrdfdasfsdfdsfdsfdsfsdfsdfdsfsdf");
        DockView dockView = new DockView(textArea, "View");
          dockPane.dock(dockView);

        DockWindow treeDock1 = new DockWindow(generateRandomTree("AAA", 5), "Tree-A");
        treeDock1.setPrefSize(600, 100);
        dockPane.dock(treeDock1, DockPos.LEFT);
        DockWindow treeDock2 = new DockWindow(generateRandomTree("BBB-", 8), "Tree-B");
         dockPane.dock(treeDock2, DockPos.LEFT);
        DockWindow treeDock3 = new DockWindow(generateRandomTree("iii", 12), "Tree-i");
        treeDock3.setPrefSize(500, 100);
        dockPane.dock(treeDock3, DockPos.RIGHT);
    }

    private TreeView<String> generateRandomTree(String text, int size) {
        TreeItem<String> root = new TreeItem<String>("Root");
        TreeView<String> treeView = new TreeView<String>(root);
        treeView.setShowRoot(false);

        for (int i = 0; i < size; i++) {
            TreeItem<String> treeItem = new TreeItem<String>(text + i);
            root.getChildren().add(treeItem);
        }

        return treeView;
    }
}