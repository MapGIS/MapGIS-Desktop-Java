import com.zondy.mapgis.controls.LayerSelectComboBoxItem;
import com.zondy.mapgis.controls.LayerSelectControl;
import com.zondy.mapgis.map.*;
import com.zondy.mapgis.rastereditor.dialogs.*;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by Administrator on 2020/6/5.
 */
public class test extends Application {

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage){
        Button button1 = new Button("栅格计算器");
        button1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Document document = new Document();
                document.open("E:\\Java\\WorkSpace\\JavaTest\\testraster.mapx");
                FormulaCaculateDialog test = new FormulaCaculateDialog(document);
                test.show();
                document.close(false);
            }
        });

        Button button2 = new Button("地形因子分析");
        button2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Document document = new Document();
                document.open("E:\\Java\\WorkSpace\\JavaTest\\testraster.mapx");
                TerrainAnalysisDialog test = new TerrainAnalysisDialog(document);
                test.show();
                document.close(false);
            }
        });

        Button button3 = new Button("插值分析");
        button3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Document document = new Document();
                document.open("E:\\Java\\WorkSpace\\JavaTest\\testraster.mapx");
                RasterInterpolationDialog test = new RasterInterpolationDialog(document);
                test.show();
                document.close(false);
            }
        });

        Button button4 = new Button("重要点提取");
        button4.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Document document = new Document();
                document.open("E:\\Java\\WorkSpace\\JavaTest\\testraster.mapx");
                ImportantPointGetDialog test = new ImportantPointGetDialog(document);
                test.show();
                document.close(false);
            }
        });

        Button button5 = new Button("函数生成规则网");
        button5.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Document document = new Document();
                document.open("E:\\Java\\WorkSpace\\JavaTest\\testraster.mapx");
                CreateRasterFromMathDialog test = new CreateRasterFromMathDialog(document);
                test.show();
                document.close(false);
            }
        });

        Button button6 = new Button("重分类");
        button6.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Document document = new Document();
                document.open("E:\\Java\\WorkSpace\\JavaTest\\testraster.mapx");
                RasterReclassDialog test = new RasterReclassDialog(document);
                test.show();
                document.close(false);
            }
        });

        Button button7 = new Button("插值分析参数");
        button7.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

//                System.loadLibrary("mapgis_geomap");
//                System.loadLibrary("mapgis_geoanalysis");
//                System.loadLibrary("mapgis_geoobjects");
//                System.loadLibrary("mapgis_geodatabase");
//                System.out.print("yuhan \n");

//                MBSplineParam spline = new MBSplineParam();
//                BlineParamDialog test = new BlineParamDialog(spline);
//                test.show();

//                DistInsProperty distInsProperty = new DistInsProperty();
//                DistInsParamDialog test = new DistInsParamDialog(distInsProperty);
//                test.show();

//                KringInsProperty kringInsProperty = new KringInsProperty();
//                KringParmDialog test = new KringParmDialog(kringInsProperty);
//                test.show();

//                SearchProperty searchProperty = new SearchProperty();
//                SearchDatParamDialog test = new SearchDatParamDialog(searchProperty,2,3);
//                test.show();


            }
        });

        Button button8 = new Button("日照");
        button8.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Document document = new Document();
                document.open("E:\\Java\\WorkSpace\\JavaTest\\testraster.mapx");
                SunLightYXOutDialog test = new SunLightYXOutDialog(document);
                test.show();
            }
        });

        Button button9 = new Button("test");
        button9.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Document document = new Document();
                document.open("E:\\Java\\WorkSpace\\JavaTest\\testraster.mapx");
                String filter = "";
                String url = "";
                LayerSelectControl lsc = new LayerSelectControl(document,filter);
                lsc.selectFirstItem();
                url = lsc.getSelectedItemUrl();

//                Maps maps = document.getMaps();
//                Map map = maps.getMap(0);
//                DocumentItem item = ((DocumentItem)map);
//                url = ((MapLayer)item).getURL();

//                MapLayer mpl = map.getLayer(0);
//                url = mpl.getURL();

//        DocumentItem item = map;
//        if (item instanceof MapLayer){
//            url = ((MapLayer) item).getURL();
//        }
                System.out.print(url);
                document.close(false);
            }
        });

        Document document = new Document();
        document.open("E:\\Java\\WorkSpace\\JavaTest\\testraster.mapx");
        String filter = "";
        LayerSelectControl lsc = new LayerSelectControl(document,filter);
        lsc.selectFirstItem();
        System.out.print(lsc.getSelectedItemUrl());
        System.out.print("\n");
        lsc.setOnSelectedItemChanged(new ChangeListener<LayerSelectComboBoxItem>() {
            @Override
            public void changed(ObservableValue<? extends LayerSelectComboBoxItem> observable, LayerSelectComboBoxItem oldValue, LayerSelectComboBoxItem newValue) {
                    String url = "";
                    url = lsc.getSelectedItemUrl();
                    System.out.print(url);
                    System.out.print("\n");
            }
        });
        //document.close(false);

        VBox vBox1 = new VBox();
        vBox1.getChildren().addAll(button1,button2,button3,button4,button5,button6,button7,button8,button9,lsc);

        primaryStage.setTitle("test");
        primaryStage.setScene(new Scene(vBox1, 500,700));
        primaryStage.show();
    }

}
