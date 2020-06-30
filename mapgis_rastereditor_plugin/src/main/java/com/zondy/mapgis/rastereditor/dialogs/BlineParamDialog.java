package com.zondy.mapgis.rastereditor.dialogs;

import com.zondy.mapgis.analysis.rasteranalysis.MBSplineParam;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
 * B样条参数设置
 * @author yuhan
 * @version 1.0.0
 */
public class BlineParamDialog extends Dialog {

    //
    private MBSplineParam m_MBSplineParam = null;
    //
    private HBox bLineParamHBox = null;
    //
    private Label layerLabel = null;
    private Spinner<Integer> layerSpinner = null;
    private Label firstLayerXGridLabel = null;
    private Spinner<Integer> firstLayerXGridSpinner = null;
    private Label firstLayerYGridLabel = null;
    private Spinner<Integer> firstLayerYGridSpinner = null;
    //
    private CheckBox calculateNoDataCheckBox = null;
    private Label initialMLabel = null;
    private Spinner<Integer> initialMSpinner = null;
    private Label initialNLabel = null;
    private Spinner<Integer> initialNSpinner = null;
    //
    private Button okButton = null;
    private Button closeButton = null;

    public BlineParamDialog(MBSplineParam oldMBSplineParam){
        setTitle("多层B-Spline参数设置");

        initializeComponent();

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setContent(bLineParamHBox);
        dialogPane.getButtonTypes().addAll(ButtonType.OK,ButtonType.CLOSE);
        dialogPane.setMinSize(400,200);
        dialogPane.setPrefSize(400,200);
        dialogPane.setMaxSize(400,200);

        m_MBSplineParam = oldMBSplineParam;
        layerSpinner.getValueFactory().setValue(m_MBSplineParam.getLayerNum());
        firstLayerXGridSpinner.getValueFactory().setValue(m_MBSplineParam.getXCtrlNum());
        firstLayerYGridSpinner.getValueFactory().setValue(m_MBSplineParam.getYCtrlNum());
//        initialMSpinner.getValueFactory().setValue(m_MBSplineParam.getCalM());  //未实现
//        initialNSpinner.getValueFactory().setValue(m_MBSplineParam.getCalN());  //未实现
        calculateNoDataCheckBox.setSelected(m_MBSplineParam.getIsCalInValidCtrlPnt());

        okButton = new Button();
        okButton = (Button)dialogPane.lookupButton(ButtonType.OK);
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                m_MBSplineParam.setLayerNum(layerSpinner.getValueFactory().getValue());
                m_MBSplineParam.setXCtrlNum(firstLayerXGridSpinner.getValueFactory().getValue());
                m_MBSplineParam.setYCtrlNum(firstLayerYGridSpinner.getValueFactory().getValue());
//                m_MBSplineParam.setCalM(initialMSpinner.getValueFactory().getValue()); //未实现
//                m_MBSplineParam.setCalN(initialNSpinner.getValueFactory().getValue()); //未实现
                m_MBSplineParam.setIsCalInValidCtrlPnt(calculateNoDataCheckBox.isSelected());
            }
        });
    }

    private void initializeComponent(){
        bLineParamHBox = new HBox();
        bLineParamHBox.setSpacing(5);
        bLineParamHBox.setStyle("-fx-font-size: 9pt;");

        TitledPane titledPane1 = new TitledPane();
        titledPane1.setCollapsible(false);
        titledPane1.setText("差值参数");
        titledPane1.setMinSize(190,130);
        titledPane1.setPrefSize(190,130);
        GridPane gridPane1 = new GridPane();
        gridPane1.setPadding(new Insets(10,10,10,10));
        gridPane1.setHgap(5);
        gridPane1.setVgap(7);
        layerLabel = new Label("层数:");
        gridPane1.add(layerLabel,0,0);
        layerSpinner = new Spinner<>(0,Integer.MAX_VALUE,4);
        gridPane1.add(layerSpinner,1,0);
        firstLayerXGridLabel = new Label("第一层X网格数:");
        firstLayerXGridLabel.setMinWidth(85);
        firstLayerXGridLabel.setPrefWidth(85);
        gridPane1.add(firstLayerXGridLabel,0,1);
        firstLayerXGridSpinner = new Spinner<>(0,Integer.MAX_VALUE,8);
        gridPane1.add(firstLayerXGridSpinner,1,1);
        firstLayerYGridLabel = new Label("第一层Y网格数:");
        gridPane1.add(firstLayerYGridLabel,0,2);
        firstLayerYGridSpinner = new Spinner<>(0,Integer.MAX_VALUE,8);
        gridPane1.add(firstLayerYGridSpinner,1,2);
        titledPane1.setContent(gridPane1);

        TitledPane titledPane2 = new TitledPane();
        titledPane2.setText("无效值处理");
        titledPane2.setCollapsible(false);
        titledPane2.setMinSize(190,130);
        titledPane2.setPrefSize(190,130);
        GridPane gridPane2 = new GridPane();
        gridPane2.setPadding(new Insets(10,10,10,10));
        gridPane2.setHgap(5);
        gridPane2.setVgap(10);
        calculateNoDataCheckBox = new CheckBox("是否计算无效值");
        gridPane2.add(calculateNoDataCheckBox,0,0,2,1);
        initialMLabel = new Label("初始(M):");
        initialMLabel.setMinWidth(50);
        initialMLabel.setPrefWidth(50);
        gridPane2.add(initialMLabel,0,1);
        initialMSpinner = new Spinner<>(0,Integer.MAX_VALUE,1);
        gridPane2.add(initialMSpinner,1,1);
        initialNLabel = new Label("初始(N):");
        gridPane2.add(initialNLabel,0,2);
        initialNSpinner = new Spinner<>(0,Integer.MAX_VALUE,1);
        gridPane2.add(initialNSpinner,1,2);
        titledPane2.setContent(gridPane2);

        bLineParamHBox.getChildren().addAll(titledPane1,titledPane2);
    }
}
