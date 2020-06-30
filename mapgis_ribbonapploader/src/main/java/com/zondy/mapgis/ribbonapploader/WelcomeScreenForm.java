package com.zondy.mapgis.ribbonapploader;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @file WelcomeScreenForm.java
 * @brief 图片欢迎屏显示界面
 *
 * @author CR
 * @date 2020-6-12
 */
public class WelcomeScreenForm extends Application
{
    private Image image;

    public WelcomeScreenForm(Image image)
    {
        this.image = image;
    }

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
        AnchorPane pane = new AnchorPane();
        pane.getChildren().addAll(new ImageView(image));

        primaryStage.setScene(new Scene(pane, image.getWidth(), image.getHeight()));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
    }
}
