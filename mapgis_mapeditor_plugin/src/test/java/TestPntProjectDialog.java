import com.zondy.mapgis.mapeditor.projecttransform.*;
import com.zondy.mapgis.srs.ElpTransParam;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Optional;


/**
 * 测试投影转换相关对话框界面
 */
public class TestPntProjectDialog extends Application {
    public static void main(String[]args)
    {
       launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("测试对话框");
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10,10,10,10));
        gridPane.setVgap(10);
        gridPane.setPrefWidth(400);
        gridPane.setPrefHeight(400);
        gridPane.setAlignment(Pos.CENTER);
        Scene scene = new Scene(gridPane);
        primaryStage.setScene(scene);

        int rowIndex = 0;
        //单点投影
        Button btnSimplePntProject = new Button("单点投影");
        btnSimplePntProject.setPrefWidth(200);
        gridPane.add(btnSimplePntProject,0,rowIndex);
        //添加地理转换项
        rowIndex++;
        Button btnAddTransParamItem = new Button("添加地理转换项");
        gridPane.add(btnAddTransParamItem,0,rowIndex);
        //地理转换参数设置
        rowIndex++;
        Button transSetting = new Button("地理转换参数设置");
        gridPane.add(transSetting,0,rowIndex);
        //矢量类批量投影
        rowIndex++;
        Button btnMultiProjectVector = new Button("矢量类批量投影");
        gridPane.add(btnMultiProjectVector,0,rowIndex);
        //栅格类批量投影
        rowIndex++;
        Button btnMultiProjectRaster = new Button("栅格类批量投影");
        gridPane.add(btnMultiProjectRaster,0,rowIndex);
        //瓦片投影批量投影
        rowIndex++;
        Button btnMultiProjectTile = new Button("瓦片类批量投影");
        gridPane.add(btnMultiProjectTile,0,rowIndex);
        //统改设置
        rowIndex++;
        Button btnModify = new Button("统改设置");
        gridPane.add(btnModify,0,rowIndex);

        primaryStage.setMaxHeight(500);
        primaryStage.setMaxWidth(500);
        primaryStage.show();


        //单点投影
        btnSimplePntProject.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                SimplePntProjectDialog dialog = new SimplePntProjectDialog();
                dialog.show();
            }
        });

        //添加地理转换项
        btnAddTransParamItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AddElpTransParamDialog dialog = new AddElpTransParamDialog();
                Optional<ElpTransParam> optional = dialog.showAndWait();
                if (optional.isPresent()) {
                    ElpTransParam elpTransParam = optional.get();
                }
            }
        });
        //地理转换参数设置
        transSetting.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ElpTransSettingDialog dialog = new ElpTransSettingDialog();
                Optional<ArrayList <ElpTransParam>>  optional =  dialog.showAndWait();
                if(optional.isPresent())
                {
                    ArrayList <ElpTransParam> elpTransParamList = optional.get();
                }
            };
        });
        //矢量类批量投影
        btnMultiProjectVector.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                MultiProjectOfVectorDialog dialog = new MultiProjectOfVectorDialog();
                dialog.show();
            }
        });
        //栅格投影
        btnMultiProjectRaster.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                MultiProjectOfRasterDialog dialog = new MultiProjectOfRasterDialog();
                dialog.show();
            }
        });
        //瓦片投影
        btnMultiProjectTile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                MultiProjectOfTileDialog dialog = new MultiProjectOfTileDialog();
                dialog.show();
            }
        });
        //统改设置
        btnModify.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                UnificationModifyDialog dialog = new UnificationModifyDialog();
                //dialog.show();
                Optional<ButtonType> optional = dialog.showAndWait();
                if (optional != null) {
                    if (optional.get() == ButtonType.OK) {
                        boolean flag = dialog.isModifyAfExt();
                        String ext = dialog.getBefExt();
                    }
                }
            }
        });


    }
}
