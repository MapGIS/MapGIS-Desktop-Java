package com.zondy.mapgis.ribbonapploader;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.List;

/**
 * @file WelcomeScreenThread.java
 * @brief 显示欢迎屏的线程Runable
 *
 * @author CR
 * @date 2020-6-12
 */
public class WelcomeScreenThread extends Thread {
    private Image image;//图片
    private List<Object> welcomeScreens;//显示的欢迎屏集合
    public boolean isShown = false;//显示标志

    public WelcomeScreenThread(Image image, List<Object> welcomeScreens) {
        this.image = image;
        this.welcomeScreens = welcomeScreens;
    }

    @Override
    public void run() {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setTitle("MapGIS 10");
            stage.setOnShown(event -> {
                isShown = true;
                welcomeScreens.add(stage);
            });
            new WelcomeScreenForm(image).start(stage);
        });
    }
}
