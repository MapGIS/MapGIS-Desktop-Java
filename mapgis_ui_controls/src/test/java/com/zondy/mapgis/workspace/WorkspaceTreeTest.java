package com.zondy.mapgis.workspace;

import com.zondy.mapgis.map.Document;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;

/**
 * @author cxy
 * @date 2019/11/26
 */
public class WorkspaceTreeTest extends Application {
    private Stage stage;
    private WorkspaceTree workspaceTree;
    private Document document;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = stage;
        this.workspaceTree = new WorkspaceTree(null);
        Button openButton = new Button();
        openButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                openDocument();
            }
        });
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(new VBox(this.workspaceTree, openButton));
        primaryStage.setScene(new Scene(anchorPane, 250, 500));
        primaryStage.show();
    }

    private void openDocument() {
        FileChooser fc = new FileChooser();
        fc.setTitle("打开地图文档");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Map Files(*.mapx)", "*.mapx"),
                new FileChooser.ExtensionFilter("Map Files(*.map)", "*.map"),
                new FileChooser.ExtensionFilter("Map Bag(*.mbag)", "*.mbag"),
                new FileChooser.ExtensionFilter("Map project(*.mpj)", "*.mpj"),
                new FileChooser.ExtensionFilter("All File", "*.*")
        );
        File file = fc.showOpenDialog(this.stage);
        boolean closed = this.document.close(false);
        if (closed && file != null) {
            //this.isOpeningDocument = true;
            this.document.open(file.getAbsolutePath());
            //this.isOpeningDocument = false;

            //this.initTestDocument(this.document);
        }
    }
}
