package com.zondy.mapgis.rastereditor.dialogs;

import com.zondy.mapgis.analysis.rasteranalysis.KringInsProperty;
import com.zondy.mapgis.analysis.rasteranalysis.KringParm;
import com.zondy.mapgis.analysis.rasteranalysis.KringType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * 克立格网格化参数配置
 * @author yuhan
 * @version 1.0.0
 */
public class KringParmDialog extends Dialog{

    //
    private KringInsProperty m_KringParam = null;
    //
    private VBox kringParamVBox = null;
    //
    private Label kringTypeLabel = null;
    private ComboBox<String> kringTypeComboxBox = null;
    private Label fcValueLabel = null;
    private TextField fcValueTextField = null;
    private Label fRaudisALabel = null;
    private Spinner<Double> fRaudisASpinner = null;
    private double raudisStepSize = 1;
    //
    private RadioButton driftNoRadioButton = null;
    private RadioButton driftOneRadioButton = null;
    private RadioButton driftTwoRadioButton = null;
    private RadioButton driftThreeRadioButton = null;
    //
    private Label ratioLabel = null;
    private Spinner<Double> ratioSpinner = null;
    private double ratioStepSize = 1;
    private Label angleLabel = null;
    private Spinner<Integer> angleSpinner = null;
    //
    private Label ferrValueLabel = null;
    private TextField ferrTextField = null;
    private Label fmErrValueLabel = null;
    private TextField fmErrValueTextField = null;
    //
    private Button okButtonUnVisible = null;
    private Button closeButtonUnVisible = null;
    //
    private Button resetButton = null;
    private Button okButton = null;
    private Button closeButton = null;

    public KringParmDialog(KringInsProperty oldKringParam){
        setTitle("克立格网格化参数配置");
        m_KringParam = oldKringParam;

        initializeComponent();

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setContent(kringParamVBox);
        dialogPane.getButtonTypes().addAll(ButtonType.OK,ButtonType.CLOSE);
        dialogPane.setMinSize(430,270);
        dialogPane.setPrefSize(430,270);

        okButtonUnVisible = (Button)dialogPane.lookupButton(ButtonType.OK);
        okButtonUnVisible.setVisible(false);
        okButtonUnVisible.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                m_KringParam.setKringType(KringType.valueOf(kringTypeComboxBox.getSelectionModel().getSelectedIndex()));
                m_KringParam.setCValue(Double.parseDouble(fcValueTextField.getText()));
                m_KringParam.setRaudisA(fRaudisASpinner.getValueFactory().getValue());
                if(driftNoRadioButton.isSelected()){
                    m_KringParam.setDriftTime(0);
                }else if(driftOneRadioButton.isSelected()){
                    m_KringParam.setDriftTime(1);
                }else if(driftTwoRadioButton.isSelected()){
                    m_KringParam.setDriftTime(2);
                }else if(driftThreeRadioButton.isSelected()){
                    m_KringParam.setDriftTime(3);
                }
                m_KringParam.setErrValue(Double.parseDouble(ferrTextField.getText()));
                m_KringParam.setMicrostErrValue(Double.parseDouble(fmErrValueTextField.getText()));
                m_KringParam.setRatio(ratioSpinner.getValueFactory().getValue());
                m_KringParam.setAngle(angleSpinner.getValueFactory().getValue());
            }
        });
        closeButtonUnVisible = (Button)dialogPane.lookupButton(ButtonType.CLOSE);
        closeButtonUnVisible.setVisible(false);

        kringTypeComboxBox.getSelectionModel().select(m_KringParam.getKringType().value());
        fcValueTextField.setText(Double.toString(m_KringParam.getCValue()));
        fRaudisASpinner.getValueFactory().setValue(m_KringParam.getRaudisA());
        switch ((int)m_KringParam.getDriftTime()){
            case 0:
                driftNoRadioButton.setSelected(true);
                break;
            case 1:
                driftOneRadioButton.setSelected(true);
                break;
            case 2:
                driftTwoRadioButton.setSelected(true);
                break;
            case 3:
                driftThreeRadioButton.setSelected(true);
                break;
        }
        ferrTextField.setText(Double.toString(m_KringParam.getErrValue()));
        fmErrValueTextField.setText(Double.toString(m_KringParam.getMicrostErrValue()));
        ratioSpinner.getValueFactory().setValue(m_KringParam.getRatio());
        angleSpinner.getValueFactory().setValue((int)m_KringParam.getAngle());
    }

    private void initializeComponent(){
        kringParamVBox = new VBox();
        kringParamVBox.setSpacing(10);
        kringParamVBox.setStyle("-fx-font-size: 9pt;");

        GridPane gridPaneBase = new GridPane();
        gridPaneBase.setVgap(5);
        gridPaneBase.setHgap(5);

        TitledPane titledPane1 = new TitledPane();
        titledPane1.setText("变差函数类型");
        titledPane1.setCollapsible(false);
        titledPane1.setMinSize(200,115);
        titledPane1.setPrefSize(200,115);
        GridPane gridPane1 = new GridPane();
        gridPane1.setPadding(new Insets(5,5,5,5));
        gridPane1.setHgap(5);
        gridPane1.setVgap(5);
        kringTypeLabel = new Label("函数类型:");
        kringTypeLabel.setMinWidth(60);
        kringTypeLabel.setPrefWidth(60);
        gridPane1.add(kringTypeLabel,0,0);
        kringTypeComboxBox = new ComboBox<>();
        kringTypeComboxBox.setEditable(false);
        kringTypeComboxBox.getItems().addAll("线性模型","幂指数模型","球状(马特隆)模型");
        kringTypeComboxBox.getSelectionModel().select(1);
        gridPane1.add(kringTypeComboxBox,1,0);
        fcValueLabel = new Label("拱高值:");
        gridPane1.add(fcValueLabel,0,1);
        fcValueTextField = new TextField();
        fcValueTextField.setText(Double.toString(0));
        gridPane1.add(fcValueTextField,1,1);
        fRaudisALabel = new Label("变程值:");
        gridPane1.add(fRaudisALabel,0,2);
        double tmpMaxValue = m_KringParam.getRaudisA()*100;
        double tmpMinValue = m_KringParam.getRaudisA()/100;
        raudisStepSize = m_KringParam.getRaudisA()/100;
        fRaudisASpinner = new Spinner<>(tmpMinValue,tmpMaxValue,1);
        fRaudisASpinner.setEditable(true);
        fRaudisASpinner.setValueFactory(new SpinnerValueFactory<Double>() {
            @Override
            public void decrement(int steps) {
                double tmpValue = getValue()-steps*raudisStepSize;
                if(tmpValue<tmpMinValue){
                    setValue(tmpMinValue);
                }else{
                    setValue(tmpValue);
                }
            }

            @Override
            public void increment(int steps) {
                double tmpValue = getValue()+steps*raudisStepSize;
                if(tmpValue>tmpMaxValue){
                    setValue(tmpMaxValue);
                }else{
                    setValue(tmpValue);
                }
            }
        });
        gridPane1.add(fRaudisASpinner,1,2);
        titledPane1.setContent(gridPane1);

        TitledPane titledPane2 = new TitledPane();
        titledPane2.setText("漂移类型");
        titledPane2.setCollapsible(false);
        titledPane2.setMinSize(200,115);
        titledPane2.setPrefSize(200,115);
        GridPane gridPane2 = new GridPane();
        gridPane2.setPadding(new Insets(5,5,5,5));
        gridPane2.setHgap(10);
        gridPane2.setVgap(30);
        ToggleGroup toggleGroup = new ToggleGroup();
        driftNoRadioButton = new RadioButton("无漂移");
        driftNoRadioButton.setMinWidth(90);
        driftNoRadioButton.setPrefWidth(90);
        driftNoRadioButton.setSelected(true);
        driftNoRadioButton.setToggleGroup(toggleGroup);
        gridPane2.add(driftNoRadioButton,0,0);
        driftOneRadioButton = new RadioButton("一阶漂移");
        driftOneRadioButton.setToggleGroup(toggleGroup);
        gridPane2.add(driftOneRadioButton,1,0);
        driftTwoRadioButton = new RadioButton("二阶漂移");
        driftTwoRadioButton.setToggleGroup(toggleGroup);
        gridPane2.add(driftTwoRadioButton,0,1);
        driftThreeRadioButton = new RadioButton("三阶漂移");
        driftThreeRadioButton.setToggleGroup(toggleGroup);
        gridPane2.add(driftThreeRadioButton,1,1);
        titledPane2.setContent(gridPane2);

        TitledPane titledPane3 = new TitledPane();
        titledPane3.setText("几何异向性参数");
        titledPane3.setCollapsible(false);
        titledPane3.setMinSize(200,90);
        titledPane3.setPrefSize(200,90);
        GridPane gridPane3 = new GridPane();
        gridPane3.setPadding(new Insets(5,5,5,5));
        gridPane3.setHgap(5);
        gridPane3.setVgap(5);
        ratioLabel = new Label("比率:");
        ratioLabel.setMinWidth(60);
        ratioLabel.setPrefWidth(60);
        gridPane3.add(ratioLabel,0,0);
        ratioStepSize = m_KringParam.getRatio()>=1?1:0.1;
        ratioSpinner = new Spinner<>(0.1,10,1.0);
        ratioSpinner.setEditable(true);
        ratioSpinner.setValueFactory(new SpinnerValueFactory<Double>() {
            @Override
            public void decrement(int steps) {
                double tmpValue = getValue()-steps*ratioStepSize;
                if(tmpValue<0.1){
                    setValue(0.1);
                }else{
                    setValue(tmpValue);
                }
            }

            @Override
            public void increment(int steps) {
                double tmpValue = getValue()+steps*ratioStepSize;
                if(tmpValue>10.0){
                    setValue(10.0);
                }else{
                    setValue(tmpValue);
                }
            }
        });
        gridPane3.add(ratioSpinner,1,0);
        angleLabel = new Label("角度");
        gridPane3.add(angleLabel,0,1);
        angleSpinner = new Spinner<>(-360,360,0);
        angleSpinner.setEditable(true);
        gridPane3.add(angleSpinner,1,1);
        titledPane3.setContent(gridPane3);

        TitledPane titledPane4 = new TitledPane();
        titledPane4.setText("块金效应");
        titledPane4.setCollapsible(false);
        titledPane4.setMinSize(200,90);
        titledPane4.setPrefSize(200,90);
        GridPane gridPane4 = new GridPane();
        gridPane4.setPadding(new Insets(5,5,5,5));
        gridPane4.setHgap(5);
        gridPane4.setVgap(5);
        ferrValueLabel = new Label("测量误差效应值:");
        ferrValueLabel.setMinWidth(110);
        ferrValueLabel.setPrefWidth(110);
        gridPane4.add(ferrValueLabel,0,0);
        ferrTextField = new TextField();
        gridPane4.add(ferrTextField,1,0);
        fmErrValueLabel = new Label("微结构误差效应:");
        gridPane4.add(fmErrValueLabel,0,1);
        fmErrValueTextField = new TextField();
        gridPane4.add(fmErrValueTextField,1,1);
        titledPane4.setContent(gridPane4);

        AnchorPane anchorPane1 = new AnchorPane();
        anchorPane1.setMinWidth(380);
        anchorPane1.setPrefWidth(380);
        resetButton = new Button("参数复位(R)");
        resetButton.setLayoutX(0);
        resetButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                fcValueTextField.setText(Double.toString(m_KringParam.getCValue()));
                fRaudisASpinner.getValueFactory().setValue(m_KringParam.getRaudisA());
                ferrTextField.setText(Double.toString(0));
                fmErrValueTextField.setText(Double.toString(0));
                ratioSpinner.getValueFactory().setValue(1.0);
                angleSpinner.getValueFactory().setValue(0);
            }
        });
        okButton = new Button("确定");
        okButton.setLayoutX(200);
        okButton.setPrefWidth(80);
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                okButtonUnVisible.disarm();
                okButtonUnVisible.fire();
            }
        });
        closeButton = new Button("关闭");
        closeButton.setLayoutX(290);
        closeButton.setPrefWidth(80);
        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                closeButtonUnVisible.disarm();
                closeButtonUnVisible.fire();
            }
        });

        gridPaneBase.add(titledPane1,0,0);
        gridPaneBase.add(titledPane2,1,0);
        gridPaneBase.add(titledPane3,0,1);
        gridPaneBase.add(titledPane4,1,1);
        anchorPane1.getChildren().addAll(resetButton,okButton,closeButton);
        kringParamVBox.getChildren().addAll(gridPaneBase,anchorPane1);
    }
}
