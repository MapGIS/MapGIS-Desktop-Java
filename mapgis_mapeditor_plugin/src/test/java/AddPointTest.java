import com.zondy.mapgis.att.Record;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.geodatabase.IVectorCls;
import com.zondy.mapgis.geodatabase.SFeatureCls;
import com.zondy.mapgis.geometry.Dot3D;
import com.zondy.mapgis.geometry.GeoPoint;
import com.zondy.mapgis.info.PntInfo;
import com.zondy.mapgis.map.*;
import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @author cxy
 * @date 2020/05/07
 */
public class AddPointTest extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Button button = new Button("添加点");
        HBox hBox = new HBox(button);

        MapLayer mapLayer = new VectorLayer(VectorLayerType.SFclsLayer);
        mapLayer.setURL("gdbp://MapGISLocalPlus/cxy_test/sfcls/pnt");
        boolean rtb  =mapLayer.connectData();


        Map map = new Map();
        map.append(mapLayer);

        MapControl mapControl = new MapControl();
        mapControl.setMinSize(100,100);
        mapControl.setMap(map);


        mapControl.setOnMouseClicked(event -> {



            GeoPoint geoPoint = new GeoPoint(new Dot3D(event.getSceneX(),event.getScreenY(),0));
            PntInfo pntInfo = new PntInfo();
            Record record = new Record();
            record.setFields(((SFeatureCls)mapLayer.getData()).getFields());


            ((SFeatureCls)mapLayer.getData()).append(geoPoint, record, pntInfo);

        });


        VBox vBox = new VBox(hBox, mapControl);

        primaryStage.setTitle("WorkspaceTree");
        primaryStage.setScene(new javafx.scene.Scene(vBox, 500, 500));

        primaryStage.show();
    }
}

class InputPnt {
    public InputPnt(MapControl mapControl){

    }


}
