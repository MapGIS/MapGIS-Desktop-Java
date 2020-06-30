package com.zondy.mapgis.workspace;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class LayerSelectComboBoxTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {


        GridPane gridPane = new GridPane();
        gridPane.setHgap(2);
        gridPane.setVgap(3);
        gridPane.add(new Label("0,0 "), 0,0);
        gridPane.add(new Label("0,1 "), 0,1);
        gridPane.add(new Label("0,2 "), 0,2);
        gridPane.add(new Label("1,0 "), 1,0);
        TitledPane titledPane = new TitledPane("title",new Label("1,1"));
        titledPane.setCollapsible(false);
        gridPane.add(titledPane, 1,1, 1,2);


        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(gridPane);
        primaryStage.setScene(new Scene(anchorPane, 250, 500));
        primaryStage.show();
    }
}
