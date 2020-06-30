/**
 * @author cxy
 * @date 2019/09/24
 */

import com.zondy.mapgis.map.Document;
import javafx.application.Application;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class WorkspaceTreeTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("WorkspaceTree");
        primaryStage.setScene(new javafx.scene.Scene(null, 250, 500));

        primaryStage.show();
    }
}
