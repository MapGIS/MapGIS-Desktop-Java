package com.zondy.mapgis.rastereditor.dialogs;

import com.zondy.mapgis.analysis.rasteranalysis.DistIns;
import com.zondy.mapgis.analysis.rasteranalysis.DistInsProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * 距离反比网格化参数设置
 * @author yuhan
 * @version 1.0.0
 */
public class DistInsParamDialog extends Dialog {

    //
    private DistInsProperty m_distInsParam = null;
    //
    private VBox distInsParamVBox = null;
    //
    private Label powerLabel = null;
    private Spinner<Integer> powerSpinner = null;
    private Label smoothLabel = null;
    private TextField smoothTextField = null;
    private Label ratioLabel = null;
    private Spinner<Double> ratioSpinner = null;
    private  Label angleLabel = null;
    private Spinner<Integer> angleSpinner = null;
    private Button resetParamButton = null;
    //
    private Button okButtonUnVisible = null;
    private Button closeButtonUnVisible = null;
    //
    private Button okButton = null;
    private Button closeButton  = null;


    public DistInsParamDialog(DistInsProperty oldDistIns){
        setTitle("距离反比网格化参数配置");
        setResizable(false);

        initializeComponent();

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK,ButtonType.CLOSE);
        dialogPane.setContent(distInsParamVBox);
        dialogPane.setMinSize(430,155);
        dialogPane.setPrefSize(430,155);

        okButtonUnVisible = (Button)dialogPane.lookupButton(ButtonType.OK);
        okButtonUnVisible.setVisible(false);
        okButtonUnVisible.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                m_distInsParam.setPower(powerSpinner.getValueFactory().getValue().shortValue());
                m_distInsParam.setSmooth(Double.parseDouble(smoothTextField.getText()));
                m_distInsParam.setRatio(ratioSpinner.getValueFactory().getValue());
                m_distInsParam.setAngle(angleSpinner.getValueFactory().getValue());
            }
        });
        closeButtonUnVisible = (Button)dialogPane.lookupButton(ButtonType.CLOSE);
        closeButtonUnVisible.setVisible(false);

        m_distInsParam = oldDistIns;
        powerSpinner.getValueFactory().setValue((int)m_distInsParam.getPower());
        smoothTextField.setText(Double.toString(m_distInsParam.getSmooth()));
        ratioSpinner.getValueFactory().setValue(m_distInsParam.getRatio());
        angleSpinner.getValueFactory().setValue((int)m_distInsParam.getAngle());
    }

    private void initializeComponent(){
        distInsParamVBox = new VBox();
        distInsParamVBox.setSpacing(10);
        distInsParamVBox.setStyle("-fx-font-size: 9pt;");

        HBox hBox1 = new HBox();
        hBox1.setSpacing(5);

        TitledPane titledPane1 = new TitledPane();
        titledPane1.setText("距离反比参数");
        titledPane1.setCollapsible(false);
        titledPane1.setMinSize(200,95);
        titledPane1.setPrefSize(200,95);
        GridPane gridPane1 = new GridPane();
        gridPane1.setPadding(new Insets(5,5,5,5));
        gridPane1.setHgap(5);
        gridPane1.setVgap(5);
        powerLabel = new Label("距离幂指数:");
        powerLabel.setMinWidth(65);
        powerLabel.setPrefWidth(65);
        gridPane1.add(powerLabel,0,0);
        powerSpinner = new Spinner<>(0,12,2);
        powerSpinner.setEditable(true);
        gridPane1.add(powerSpinner,1,0);
        smoothLabel = new Label("光滑系数:");
        gridPane1.add(smoothLabel,0,1);
        smoothTextField = new TextField();
        gridPane1.add(smoothTextField,1,1);
        titledPane1.setContent(gridPane1);

        TitledPane titledPane2 = new TitledPane();
        titledPane2.setText("几何异向性参数");
        titledPane2.setCollapsible(false);
        titledPane2.setMinSize(200,95);
        titledPane2.setMaxSize(200,95);
        GridPane gridPane2 = new GridPane();
        gridPane2.setPadding(new Insets(5,5,5,5));
        gridPane2.setHgap(5);
        gridPane2.setVgap(7);
        ratioLabel = new Label("比率:");
        ratioLabel.setMinWidth(30);
        ratioLabel.setPrefWidth(30);
        gridPane2.add(ratioLabel,0,0);
        ratioSpinner = new Spinner<Double>(0.1,10,1.0);
        ratioSpinner.setEditable(true);
        gridPane2.add(ratioSpinner,1,0);
        angleLabel = new Label("角度:");
        gridPane2.add(angleLabel,0,1);
        angleSpinner = new Spinner<>(-360,360,0);
        angleSpinner.setEditable(true);
        gridPane2.add(angleSpinner,1,1);
        titledPane2.setContent(gridPane2);

        hBox1.getChildren().addAll(titledPane1,titledPane2);

        AnchorPane anchorPane1  = new AnchorPane();
        anchorPane1.setMinWidth(400);
        anchorPane1.setPrefWidth(400);

        resetParamButton = new Button("参数复位(R)");
        resetParamButton.setLayoutX(0);
        resetParamButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                powerSpinner.getValueFactory().setValue(2);
                smoothTextField.setText(Integer.toString(0));
                ratioSpinner.getValueFactory().setValue(1.0);
                angleSpinner.getValueFactory().setValue(0);
            }
        });
        okButton = new Button("确定");
        okButton.setLayoutX(220);
        okButton.setPrefWidth(80);
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                okButtonUnVisible.disarm();;
                okButtonUnVisible.fire();
            }
        });
        closeButton = new Button("关闭");
        closeButton.setLayoutX(320);
        closeButton.setPrefWidth(80);
        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                closeButtonUnVisible.disarm();
                closeButtonUnVisible.fire();
            }
        });
        anchorPane1.getChildren().addAll(resetParamButton,okButton,closeButton);

        distInsParamVBox.getChildren().addAll(hBox1,anchorPane1);
    }
}
