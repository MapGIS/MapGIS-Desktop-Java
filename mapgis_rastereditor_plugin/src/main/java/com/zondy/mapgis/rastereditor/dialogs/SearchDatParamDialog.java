package com.zondy.mapgis.rastereditor.dialogs;
import com.zondy.mapgis.analysis.rasteranalysis.SearchDat;
import com.zondy.mapgis.analysis.rasteranalysis.SearchProperty;
import com.zondy.mapgis.analysis.rasteranalysis.Seekway;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * 网格化点搜索参数配置
 * @author yuhan
 * @version 1.0.0
 */
public class SearchDatParamDialog extends Dialog{

    //
    private SearchProperty m_SearchDatParam = null;
    private int m_DotCount = 0;
    //
    private VBox searchDatVBox = null;
    //
    private RadioButton allPntRadioButton = null;
    private RadioButton simSearchRadioButton = null;
    private RadioButton fourDirectionRadioButton = null;
    private RadioButton eightDirectionRadioButton = null;
    //
    private Label pntNumLabel = null;
    private Spinner<Integer> pntNumSpinner = null;
    private Label minDataPntLabel = null;
    private Spinner<Integer> minDataPntSpinner = null;
    private Label maxNumLabel = null;
    private Spinner<Integer> maxNumSpinner = null;
    //
    private Label searchRadisLabel = null;
    private Spinner<Double> searchRadisSpinner = null;
    private double radiusStep = 1;
    //
    private Button okButtonUnVisible = null;
    private Button closeButtonUnVisible = null;
    //
    private Button resetButton = null;
    private Button okButton = null;
    private Button closeButton = null;

    public SearchDatParamDialog(SearchProperty oldSearchDat, int gridingMethod, int dotCount){
        setTitle("网格化点搜索参数配置");
        m_SearchDatParam = oldSearchDat;
        m_DotCount = dotCount;

        initializeComponent();

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setContent(searchDatVBox);
        dialogPane.getButtonTypes().addAll(ButtonType.OK,ButtonType.CLOSE);
        dialogPane.setMinSize(450,320);
        dialogPane.setPrefSize(450,320);

        if((gridingMethod==0 || gridingMethod==1) && dotCount<=128){
            allPntRadioButton.setSelected(true);
            m_SearchDatParam.setSeekWay(Seekway.All);
        }else{
            allPntRadioButton.setSelected(false);
            if(m_SearchDatParam.getSeekWay().value()==1){
                m_SearchDatParam.setSeekWay(Seekway.FourDirection);
            }
        }
        switch (m_SearchDatParam.getSeekWay().value()){
            case 1:
                allPntRadioButton.setSelected(true);
                break;
            case 2:
                simSearchRadioButton.setSelected(true);
                break;
            case 3:
                fourDirectionRadioButton.setSelected(true);
                break;
            case 4:
                eightDirectionRadioButton.setSelected(true);
                break;
        }

        if(gridingMethod==4){
            allPntRadioButton.setDisable(true);
            simSearchRadioButton.setDisable(true);
            fourDirectionRadioButton.setDisable(true);
            eightDirectionRadioButton.setDisable(true);
        }
        pntNumSpinner.getValueFactory().setValue((int)m_SearchDatParam.getDotPerSec());
        minDataPntSpinner.getValueFactory().setValue((int)m_SearchDatParam.getMinAllDot());
        maxNumSpinner.getValueFactory().setValue((int)m_SearchDatParam.getMaxNullSec());
        searchRadisSpinner.getValueFactory().setValue(m_SearchDatParam.getRadius());

        okButtonUnVisible = (Button)dialogPane.lookupButton(ButtonType.OK);
        okButtonUnVisible.setVisible(false);
        okButtonUnVisible.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(allPntRadioButton.isSelected()){
                    m_SearchDatParam.setSeekWay(Seekway.All);
                }else if(simSearchRadioButton.isSelected()){
                    m_SearchDatParam.setSeekWay(Seekway.KNearest);
                }else if(fourDirectionRadioButton.isSelected()){
                    m_SearchDatParam.setSeekWay(Seekway.FourDirection);
                }else if(eightDirectionRadioButton.isSelected()){
                    m_SearchDatParam.setSeekWay(Seekway.EightDirection);
                }

                m_SearchDatParam.setDotPerSec(pntNumSpinner.getValueFactory().getValue());
                if(m_SearchDatParam.getDotPerSec()>m_DotCount){
                    m_SearchDatParam.setDotPerSec(m_DotCount);
                }

                m_SearchDatParam.setMinAllDot(minDataPntSpinner.getValueFactory().getValue());
                if(m_SearchDatParam.getMinAllDot()>m_DotCount){
                    m_SearchDatParam.setMinAllDot(m_DotCount);
                }

                m_SearchDatParam.setMaxNullSec(maxNumSpinner.getValueFactory().getValue());
                if((m_SearchDatParam.getSeekWay().value()<3) && (m_SearchDatParam.getMaxNullSec()>1)){
                    m_SearchDatParam.setMaxNullSec(1);
                }
                if((m_SearchDatParam.getSeekWay().value()==3) && (m_SearchDatParam.getMaxNullSec()>4)){
                    m_SearchDatParam.setMaxNullSec(4);
                }
                if((m_SearchDatParam.getSeekWay().value()==4) && (m_SearchDatParam.getMaxNullSec()>8)){
                    m_SearchDatParam.setMaxNullSec(8);
                }

                m_SearchDatParam.setRadius(searchRadisSpinner.getValueFactory().getValue());
                if(m_SearchDatParam.getRadius()<=radiusStep){
                    m_SearchDatParam.setRadius(radiusStep);
                }
                if(m_SearchDatParam.getRadius()>1000*radiusStep){
                    m_SearchDatParam.setRadius(1000*radiusStep);
                }
                // 步长设置
            }
        });
        closeButtonUnVisible = (Button)dialogPane.lookupButton(ButtonType.CLOSE);
        closeButtonUnVisible.setVisible(false);
    }

    private void initializeComponent(){
        searchDatVBox = new VBox();
        searchDatVBox.setSpacing(5);
        searchDatVBox.setStyle("-fx-font-size: 9pt;");

        int width = 430;

        TitledPane titledPane1 = new TitledPane();
        titledPane1.setText("搜索类型");
        titledPane1.setCollapsible(false);
        titledPane1.setMinWidth(width);
        titledPane1.setPrefWidth(width);
        GridPane gridPane1 = new GridPane();
        gridPane1.setPadding(new Insets(10,10,10,10));
        gridPane1.setHgap(30);
        ToggleGroup toggleGroup = new ToggleGroup();
        allPntRadioButton = new RadioButton("所有点");
        allPntRadioButton.setSelected(true);
        allPntRadioButton.setToggleGroup(toggleGroup);
        gridPane1.add(allPntRadioButton,0,0);
        simSearchRadioButton = new RadioButton("简单搜索");
        simSearchRadioButton.setToggleGroup(toggleGroup);
        gridPane1.add(simSearchRadioButton,1,0);
        fourDirectionRadioButton = new RadioButton("四方向");
        fourDirectionRadioButton.setToggleGroup(toggleGroup);
        gridPane1.add(fourDirectionRadioButton,2,0);
        eightDirectionRadioButton = new RadioButton("八方向");
        eightDirectionRadioButton.setToggleGroup(toggleGroup);
        gridPane1.add(eightDirectionRadioButton,3,0);
        titledPane1.setContent(gridPane1);
        toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if(newValue==allPntRadioButton){
                    pntNumSpinner.setDisable(true);
                    minDataPntSpinner.setDisable(true);
                    maxNumSpinner.setDisable(true);
                    searchRadisSpinner.setDisable(true);
                }else if(newValue==simSearchRadioButton){
                    pntNumSpinner.setDisable(false);
                    minDataPntSpinner.setDisable(false);
                    maxNumSpinner.setDisable(false);
                    searchRadisSpinner.setDisable(false);

                    pntNumSpinner.getValueFactory().setValue(64);
                    minDataPntSpinner.getValueFactory().setValue(5);
                    maxNumSpinner.getValueFactory().setValue(4);
                }else if(newValue==fourDirectionRadioButton){
                    pntNumSpinner.setDisable(false);
                    minDataPntSpinner.setDisable(false);
                    maxNumSpinner.setDisable(false);
                    searchRadisSpinner.setDisable(false);

                    pntNumSpinner.getValueFactory().setValue(16);
                    minDataPntSpinner.getValueFactory().setValue(5);
                    maxNumSpinner.getValueFactory().setValue(4);
                }else if(newValue==eightDirectionRadioButton){
                    pntNumSpinner.setDisable(false);
                    minDataPntSpinner.setDisable(false);
                    maxNumSpinner.setDisable(false);
                    searchRadisSpinner.setDisable(false);

                    pntNumSpinner.getValueFactory().setValue(8);
                    minDataPntSpinner.getValueFactory().setValue(5);
                    maxNumSpinner.getValueFactory().setValue(4);
                }
            }
        });

        int width2 = 300;

        TitledPane titledPane2 = new TitledPane();
        titledPane2.setText("搜索规则");
        titledPane2.setCollapsible(false);
        titledPane2.setMinWidth(width);
        titledPane2.setPrefWidth(width);
        GridPane gridPane2 = new GridPane();
        gridPane2.setPadding(new Insets(10,10,10,10));
        gridPane2.setHgap(5);
        gridPane2.setVgap(7);
        pntNumLabel = new Label("每搜索方向点数:");
        gridPane2.add(pntNumLabel,0,0);
        pntNumSpinner = new Spinner<>(1,Integer.MAX_VALUE,16);
        pntNumSpinner.setMinWidth(width2);
        pntNumSpinner.setPrefWidth(width2);
        pntNumSpinner.setEditable(true);
        pntNumSpinner.setDisable(true);
        gridPane2.add(pntNumSpinner,1,0);
        minDataPntLabel = new Label("有效最少数据点:");
        gridPane2.add(minDataPntLabel,0,1);
        minDataPntSpinner = new Spinner<>(1,Integer.MAX_VALUE,5);
        minDataPntSpinner.setMinWidth(width2);
        minDataPntSpinner.setPrefWidth(width2);
        minDataPntSpinner.setEditable(true);
        minDataPntSpinner.setDisable(true);
        gridPane2.add(minDataPntSpinner,1,1);
        maxNumLabel = new Label("最大空方向允许数:");
        maxNumLabel.setMinWidth(100);
        maxNumLabel.setPrefWidth(100);
        gridPane2.add(maxNumLabel,0,2);
        maxNumSpinner = new Spinner<>(1,Integer.MAX_VALUE,4);
        maxNumSpinner.setMinWidth(width2);
        maxNumSpinner.setPrefWidth(width2);
        maxNumSpinner.setEditable(true);
        maxNumSpinner.setDisable(true);
        gridPane2.add(maxNumSpinner,1,2);
        titledPane2.setContent(gridPane2);

        int width3 = 340;

        TitledPane titledPane3 = new TitledPane();
        titledPane3.setText("搜索参数");
        titledPane3.setCollapsible(false);
        titledPane3.setMinWidth(width);
        titledPane3.setPrefWidth(width);
        GridPane gridPane3 = new GridPane();
        gridPane3.setPadding(new Insets(10,10,10,10));
        gridPane3.setVgap(5);
        gridPane3.setHgap(5);
        searchRadisLabel = new Label("搜索参数:");
        searchRadisLabel.setMinWidth(60);
        searchRadisLabel.setPrefWidth(60);
        gridPane3.add(searchRadisLabel,0,0);
        double tmpMinValue = m_SearchDatParam.getRadius()/100;
        double tmpMaxValue = m_SearchDatParam.getRadius()*100;
        radiusStep = m_SearchDatParam.getRadius()/100;
        searchRadisSpinner = new Spinner<>(tmpMinValue,tmpMaxValue,1);
        searchRadisSpinner.setMinWidth(width3);
        searchRadisSpinner.setPrefWidth(width3);
        searchRadisSpinner.setEditable(true);
        searchRadisSpinner.setDisable(true);
        searchRadisSpinner.setValueFactory(new SpinnerValueFactory<Double>() {
            @Override
            public void decrement(int steps) {
                // 减小
                double tmpValue = getValue()-steps*radiusStep;
               if(tmpValue<tmpMinValue){
                   setValue(tmpMinValue);
               }else{
                   setValue(tmpValue);
               }
            }

            @Override
            public void increment(int steps) {
                // 增加
                double tmpValue = getValue()+steps*radiusStep;
                if(tmpValue>tmpMaxValue){
                    setValue(tmpMaxValue);
                }else{
                    setValue(tmpValue);
                }
            }
        });
        gridPane3.add(searchRadisSpinner,1,0);
        titledPane3.setContent(gridPane3);

        AnchorPane anchorPane1 = new AnchorPane();
        anchorPane1.setMinWidth(width);
        anchorPane1.setPrefWidth(width);
        resetButton = new Button("参数复位(R)");
        resetButton.setLayoutX(0);
        resetButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                fourDirectionRadioButton.setSelected(true);
            }
        });
        okButton = new Button("确定");
        okButton.setLayoutX(220);
        okButton.setPrefWidth(80);
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                okButtonUnVisible.disarm();
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
        anchorPane1.getChildren().addAll(resetButton,okButton,closeButton);

        searchDatVBox.getChildren().addAll(titledPane1,titledPane2,titledPane3,anchorPane1);
    }
}
