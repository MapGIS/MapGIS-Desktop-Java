package com.zondy.mapgis.rastereditor.dialogs;

import com.zondy.mapgis.analysis.rasteranalysis.ErrorCriterionType;
import com.zondy.mapgis.analysis.rasteranalysis.IPExtractInfo;
import com.zondy.mapgis.analysis.rasteranalysis.OutputInfo;
import com.zondy.mapgis.analysis.rasteranalysis.RasterImportantPointOp;
import com.zondy.mapgis.controls.common.ButtonEdit;
import com.zondy.mapgis.controls.LayerSelectComboBoxItem;
import com.zondy.mapgis.controls.LayerSelectControl;
import com.zondy.mapgis.controls.common.ButtonEditEvent;
import com.zondy.mapgis.filedialog.GDBSaveFileDialog;
import com.zondy.mapgis.geodatabase.SFeatureCls;
import com.zondy.mapgis.geodatabase.raster.GeoRaster;
import com.zondy.mapgis.geodatabase.raster.RasterAccess;
import com.zondy.mapgis.geodatabase.raster.RasterDataset;
import com.zondy.mapgis.geodatabase.raster.RasterFormat;
import com.zondy.mapgis.geometry.GeomType;
import com.zondy.mapgis.map.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.image.Raster;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 重要点提取
 * @author yuhan
 * @version 1.0.0
 */
public class ImportantPointGetDialog extends Dialog{

    VBox importantPointGetVBox = new VBox();
    Stage primaryStage = null;
    // 源数据
    private Label sourceRasterLabel = null;
    private LayerSelectControl sourceRasterLayerSelect = null;
    // 参数设置
    private Label paramErrorMethodLabel = null;
    private ComboBox<String> paramsErrorMethodComboBox = null;
    private Label paramsThreshodLabel = null;
    private TextField paramsThreshodTextField = null;
    private Label paramsThreshodNote = null;
    private Label paramsPointAllLabel = null;
    private TextField paramsPointAllTextField = null;
    private Label paramsExtractCountsLabel = null;
    private Spinner<Integer> paramsExtractCountsSpinner = null;
    private Label paramsExtractRatioLabel = null;
    private Spinner<Integer> paramsExtractRatioSpinner = null;
    // 结果数据
    private RadioButton outputRasterPointRadiaButton = null;
    private RadioButton outputRasterTxtRadioButton = null;
    private RadioButton outputRasterImageRadioButton = null;
    private Label outputRasterLabel = null;
    private ButtonEdit outputSaveButtonEdit = null;
    private CheckBox addToCheckBox = null;
    //
    private Button calButton = null;
    private Button okButtonUnVisible = null;
    private Button cancleButton = null;
    private Button cancleButtonUnVisible = null;

    public ImportantPointGetDialog(Document document){
        setTitle("重要点提取");

        initImportantPointVBox(document);

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setContent(importantPointGetVBox);
        dialogPane.getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);
        primaryStage = (Stage)dialogPane.getScene().getWindow();
        dialogPane.setMinSize(540,440);
        dialogPane.setPrefSize(540,440);
        dialogPane.setMaxSize(540,440);

        okButtonUnVisible = (Button)dialogPane.lookupButton(ButtonType.OK);
        okButtonUnVisible.setVisible(false);
        cancleButtonUnVisible = (Button)dialogPane.lookupButton(ButtonType.CANCEL);
        cancleButtonUnVisible.setVisible(false);
    }

    private void initImportantPointVBox(Document document){
        importantPointGetVBox = new VBox();
        importantPointGetVBox.setSpacing(10);
        importantPointGetVBox.setStyle("-fx-font-size: 9pt;");

        int width = 520;
        // region 源数据
        TitledPane sourceRasterTitledPane = new TitledPane();
        sourceRasterTitledPane.setText("源数据");
        sourceRasterTitledPane.setCollapsible(false);
        sourceRasterTitledPane.setPrefWidth(width);
        sourceRasterTitledPane.setMinWidth(width);

        GridPane sourceRasterGridPane = new GridPane();
        sourceRasterGridPane.setPadding(new Insets(10,10,10,10));
        sourceRasterGridPane.setHgap(5);
        sourceRasterGridPane.setVgap(5);

        sourceRasterLabel = new Label("输入栅格:");
        sourceRasterLabel.setPrefWidth(60);
        sourceRasterGridPane.add(sourceRasterLabel,0,0);
        String rasterFilter = "栅格数据，6x数据|ras;*.msi;*.tif;*.img";
        sourceRasterLayerSelect = new LayerSelectControl(document,rasterFilter);
        sourceRasterLayerSelect.setPrefWidth(435);
        sourceRasterLayerSelect.setOnSelectedItemChanged(new ChangeListener<LayerSelectComboBoxItem>() {
            @Override
            public void changed(ObservableValue<? extends LayerSelectComboBoxItem> observable, LayerSelectComboBoxItem oldValue, LayerSelectComboBoxItem newValue) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("重要点提取");
                String tmpRasterUrl = sourceRasterLayerSelect.getSelectedItemUrl();
                if(tmpRasterUrl.isEmpty()){
                    alert.headerTextProperty().set("栅格数据路径为空!");
                    alert.showAndWait();
                    return;
                }
                long lErr = -1;
                RasterDataset tmpRasterDataSet = new RasterDataset();
                if(tmpRasterUrl.toLowerCase().startsWith("file:///")){
                    lErr = tmpRasterDataSet.openByURL(tmpRasterUrl, RasterAccess.Read);
                }else{
                    lErr = tmpRasterDataSet.open(tmpRasterUrl,RasterAccess.Read);
                }
                if(lErr==0){
                    alert.headerTextProperty().set("栅格数据打开失败!");
                    alert.showAndWait();
                    return;
                }
                paramsErrorMethodComboBox.getSelectionModel().selectFirst();
                paramsThreshodTextField.setText(Double.toString(0.5));
                int PntNums = tmpRasterDataSet.getWidth()*tmpRasterDataSet.getHeight();
                paramsPointAllTextField.setText(Integer.toString(PntNums));
                paramsExtractCountsSpinner.getValueFactory().setValue(PntNums/10);
                paramsExtractRatioSpinner.getValueFactory().setValue(10);

                tmpRasterDataSet.dispose();
            }
        });
        sourceRasterGridPane.add(sourceRasterLayerSelect,1,0);
        sourceRasterTitledPane.setContent(sourceRasterGridPane);
        // endregion

        // region 参数设置
        TitledPane paramsTitledPane = new TitledPane();
        paramsTitledPane.setText("参数设置");
        paramsTitledPane.setCollapsible(false);
        paramsTitledPane.setMaxWidth(width);
        paramsTitledPane.setMinWidth(width);

        GridPane paramsGridPane = new GridPane();
        paramsGridPane.setPadding(new Insets(10,10,10,10));
        paramsGridPane.setHgap(5);
        paramsGridPane.setVgap(7);

        paramErrorMethodLabel = new Label("误差方法选取:");
        paramErrorMethodLabel.setPrefWidth(80);
        paramsGridPane.add(paramErrorMethodLabel,0,0);
        paramsErrorMethodComboBox = new ComboBox<>();
        paramsErrorMethodComboBox.setEditable(false);
        paramsErrorMethodComboBox.setPrefWidth(410);
        paramsErrorMethodComboBox.getItems().addAll("局部误差总和","最大局部误差","局部误差平方和","向量夹角误差");
        paramsErrorMethodComboBox.getSelectionModel().selectFirst();
        paramsGridPane.add(paramsErrorMethodComboBox,1,0,2,1);

        paramsThreshodLabel = new Label("筛选阈值:");
        paramsGridPane.add(paramsThreshodLabel,0,1);
        paramsThreshodTextField = new TextField();
        paramsThreshodTextField.setEditable(true);
        paramsThreshodTextField.setPrefWidth(300);
        paramsThreshodTextField.setText(Double.toString(0.5));
        paramsGridPane.add(paramsThreshodTextField,1,1);
        paramsThreshodNote = new Label("(0-1)之间的数");
        paramsGridPane.add(paramsThreshodNote,2,1);

        paramsPointAllLabel = new Label("总点数:");
        paramsGridPane.add(paramsPointAllLabel,0,2);
        paramsPointAllTextField = new TextField();
        paramsPointAllTextField.setEditable(false);
        paramsGridPane.add(paramsPointAllTextField,1,2,2,1);

        paramsExtractCountsLabel = new Label("提取点数:");
        paramsGridPane.add(paramsExtractCountsLabel,0,3);
        paramsExtractCountsSpinner = new Spinner<>(0,Integer.MAX_VALUE,0);
        paramsExtractCountsSpinner.setPrefWidth(410);
        paramsExtractCountsSpinner.setEditable(true);
        paramsExtractCountsSpinner.setEditable(false);
        paramsExtractCountsSpinner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                if(!paramsPointAllTextField.getText().isEmpty()){
                    Integer allPntNums = Integer.parseInt(paramsPointAllTextField.getText());
                    paramsExtractRatioSpinner.getValueFactory().setValue(100*newValue/allPntNums);
                }
            }
        });
        paramsGridPane.add(paramsExtractCountsSpinner,1,3,2,1);
        paramsExtractRatioLabel = new Label("提取比率:");
        paramsGridPane.add(paramsExtractRatioLabel,0,4);
        paramsExtractRatioSpinner = new Spinner<>(1,100,10);
        paramsExtractRatioSpinner.setPrefWidth(410);
        paramsExtractRatioSpinner.setEditable(true);
        paramsExtractRatioSpinner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                if(!paramsPointAllTextField.getText().isEmpty()){
                    Integer allPntNums = Integer.parseInt(paramsPointAllTextField.getText());
                    paramsExtractCountsSpinner.getValueFactory().setValue(allPntNums*newValue/100);
                }
            }
        });
        paramsGridPane.add(paramsExtractRatioSpinner,1,4,2,1);
        paramsTitledPane.setContent(paramsGridPane);
        // endregion

        // region 结果数据
        TitledPane outputRasterTitledPane = new TitledPane();
        outputRasterTitledPane.setText("结果数据");
        outputRasterTitledPane.setCollapsible(false);
        outputRasterTitledPane.setMinWidth(width);
        outputRasterTitledPane.setMaxWidth(width);

        GridPane outputRasterGridPane = new GridPane();
        outputRasterGridPane.setPadding(new Insets(10,10,10,10));
        outputRasterGridPane.setHgap(5);
        outputRasterGridPane.setVgap(10);

        GridPane gridPane1 = new GridPane();
        gridPane1.setHgap(5);
        gridPane1.setVgap(5);

        ToggleGroup radioButtonToggleGrop = new ToggleGroup();
        outputRasterPointRadiaButton = new RadioButton("点要素");
        outputRasterPointRadiaButton.setPrefWidth(120);
        outputRasterPointRadiaButton.setSelected(true);
        outputRasterPointRadiaButton.setToggleGroup(radioButtonToggleGrop);
        gridPane1.add(outputRasterPointRadiaButton,0,0);
        outputRasterTxtRadioButton = new RadioButton("文本文件");
        outputRasterTxtRadioButton.setPrefWidth(120);
        outputRasterTxtRadioButton.setToggleGroup(radioButtonToggleGrop);
        gridPane1.add(outputRasterTxtRadioButton,1,0);
        outputRasterImageRadioButton = new RadioButton("栅格数据集");
        outputRasterImageRadioButton.setPrefWidth(120);
        outputRasterImageRadioButton.setToggleGroup(radioButtonToggleGrop);
        gridPane1.add(outputRasterImageRadioButton,2,0);
        radioButtonToggleGrop.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                String tmpRadioButtonName = ((RadioButton)newValue).getText();
                switch (tmpRadioButtonName){
                    case "点要素":
                        addToCheckBox.setText("添加到地图文档");
                        break;
                    case "文本文件":
                        addToCheckBox.setText("自动打开文件");
                        break;
                    case "栅格数据集":
                        addToCheckBox.setText("添加到地图文档");
                        break;
                }
            }
        });
        outputRasterGridPane.add(gridPane1,0,0);

        GridPane gridPane2 = new GridPane();
        gridPane2.setHgap(5);
        gridPane2.setVgap(5);

        outputRasterLabel = new Label("输出路径:");
        outputRasterLabel.setPrefWidth(60);
        gridPane2.add(outputRasterLabel,0,1);
        outputSaveButtonEdit = new ButtonEdit();
        outputSaveButtonEdit.setPrefWidth(435);
        outputSaveButtonEdit.setOnButtonClick(new EventHandler<ButtonEditEvent>() {
            @Override
            public void handle(ButtonEditEvent event) {
                GDBSaveFileDialog saveToGDB = new GDBSaveFileDialog();
                saveToGDB.setMultiSelect(false);
                String tmpRadioButtonName = ((RadioButton)radioButtonToggleGrop.getSelectedToggle()).getText();
                String strFilter = "";
                switch (tmpRadioButtonName){
                    case "点要素":
                        strFilter = "简单要素类" + "|sfcls";
                        saveToGDB.setFilter(strFilter);
                        break;
                    case "文本文件":
                        strFilter = "文本文件(*.txt)" + "|*.txt";
                        saveToGDB.setFilter(strFilter);
                        break;
                    case "栅格数据集":
                        strFilter = "栅格数据集" + "|ras|" + "MAPGISMSI (*.msi)" + "|*.msi|" + "GTiff (*.tif)" + "|*.tif|"  + "HFA (*.img)" + "|*.img";
                        saveToGDB.setFilter(strFilter);
                        break;
                }
                Optional<String[]> rasterOption = saveToGDB.showAndWait();
                if(rasterOption!=null && rasterOption.isPresent()){
                    List<String> rasterOutputToGDB = Arrays.asList(rasterOption.get());
                    outputSaveButtonEdit.setText(rasterOutputToGDB.get(0));
                }
            }
        });
        gridPane2.add(outputSaveButtonEdit,1,1);
        outputRasterGridPane.add(gridPane2,0,1);
        outputRasterTitledPane.setContent(outputRasterGridPane);
        // endregion

        // region
        AnchorPane anchorPane1 = new AnchorPane();
        anchorPane1.setMinWidth(480);
        anchorPane1.setPrefWidth(480);

        addToCheckBox = new CheckBox("添加到地图文档");
        addToCheckBox.setPrefWidth(250);
        addToCheckBox.setLayoutX(0);
        addToCheckBox.setSelected(true);

        calButton = new Button("计算");
        calButton.setPrefSize(60,30);
        calButton.setLayoutX(345);
        calButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("重要点提取");
                if(sourceRasterLayerSelect.getSelectedItemUrl().isEmpty()){
                    alert.headerTextProperty().set("输入的栅格数据路径为空!");
                    alert.showAndWait();
                    return;
                }
                Double tmpThreshod = Double.parseDouble(paramsThreshodTextField.getText());
                if(tmpThreshod<0 || tmpThreshod>1){
                    alert.headerTextProperty().set("请输入正确的筛选阈值!");
                    alert.showAndWait();
                    return;
                }
                if(outputSaveButtonEdit.getText().isEmpty()){
                    alert.headerTextProperty().set("输出栅格数据路径为空!");
                    alert.showAndWait();
                    return;
                }
                long lErr = -1;
                // 栅格数据
                String tmpRasterUrl = sourceRasterLayerSelect.getSelectedItemUrl();
                GeoRaster geoRaster = new GeoRaster();
                if(!geoRaster.Open(tmpRasterUrl)){
                    alert.headerTextProperty().set("打开栅格数据失败!");
                    alert.showAndWait();
                    return;
                }
                // 重要点信息
                IPExtractInfo VipInfo = new IPExtractInfo();
                VipInfo.setCriterionNo(ErrorCriterionType.valueOf(paramsErrorMethodComboBox.getSelectionModel().getSelectedIndex()));
                VipInfo.setThreshold(Float.parseFloat(paramsThreshodTextField.getText()));
                VipInfo.setPointNums(paramsExtractCountsSpinner.getValue());
                // 功能实现
                String tmpOutPath = outputSaveButtonEdit.getText();
                if(outputRasterPointRadiaButton.isSelected()){
                    SFeatureCls sfcls = new SFeatureCls();
                    if(sfcls.create(tmpOutPath, GeomType.GeomPnt)<=0){
                        alert.headerTextProperty().set("创建点简单要素类失败!");
                        alert.showAndWait();
                        return;
                    }
                    lErr = RasterImportantPointOp.toPntSfcls(geoRaster, VipInfo, sfcls, null);
                }else if(outputRasterTxtRadioButton.isSelected()){
                    lErr = RasterImportantPointOp.toTxt(geoRaster, VipInfo, tmpOutPath, null);
                }else{
                    OutputInfo output = new OutputInfo();
                    output.setPath(tmpOutPath);
                    output.setFormat(getRasterFormatByURL(tmpOutPath));
                    lErr = RasterImportantPointOp.toDEM(geoRaster, VipInfo,output,null);
                }
                if(lErr>0){
                    alert.headerTextProperty().set("重要点提取成功!");
                    alert.showAndWait();
                    if(lErr==1 && addToCheckBox.isSelected()){
                        if(outputRasterPointRadiaButton.isSelected()){
                            String savePath = outputSaveButtonEdit.getText();
                            VectorLayer vecLayer = new VectorLayer(VectorLayerType.SFclsLayer);
                            addSfcLayerToDoc(savePath, vecLayer, document);
                        }else if(outputRasterImageRadioButton.isSelected()){
                            String savePath = outputSaveButtonEdit.getText();
                            RasterLayer rasLayer = new RasterLayer();
                            addRasterLayerToDoc(savePath, rasLayer, document);
                        }
                    }
                    okButtonUnVisible.disarm();
                    okButtonUnVisible.fire();
                }else{
                    alert.headerTextProperty().set("重要点提取失败!");
                    alert.showAndWait();
                }
            }
        });

        cancleButton = new Button("取消");
        cancleButton.setPrefSize(60,30);
        cancleButton.setLayoutX(415);
        cancleButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                cancleButtonUnVisible.disarm();
                cancleButtonUnVisible.fire();
            }
        });
        anchorPane1.getChildren().addAll(addToCheckBox,calButton,cancleButton);
        // endregion
        importantPointGetVBox.getChildren().addAll(sourceRasterTitledPane,paramsTitledPane,outputRasterTitledPane,anchorPane1);
    }

    private RasterFormat getRasterFormatByURL(String outputULR)
    {
        RasterFormat formatValue = RasterFormat.MAPGIS7MSI;
        String formatStr = "";
        if(outputULR.toLowerCase().startsWith("gdbp://")){
            formatValue = RasterFormat.MAPGIS7MSI;
        }else{
            String[] urlSplite = outputULR.split("\\.");
            formatStr = urlSplite[urlSplite.length-1];
            switch (formatStr)
            {
                case "msi":
                    formatValue = RasterFormat.MAPGISMSI;
                    break;
                case "tif":
                    formatValue = RasterFormat.GTiff;
                    break;
                case "img":
                    formatValue = RasterFormat.HFA;
                    break;
                case "pix":
                    formatValue = RasterFormat.PCIDSK;
                    break;
                case "evi":
                    formatValue = RasterFormat.ENVI;
                    break;
                case "raw":
                    formatValue = RasterFormat.EHdr;
                    break;
                case "bmp":
                    formatValue = RasterFormat.BMP;
                    break;
                case "jpg":
                    formatValue = RasterFormat.JPEG;
                    break;
                case "jp2":
                case "j2k":
                    formatValue = RasterFormat.JPEG2000;
                    break;
                case "gif":
                    formatValue = RasterFormat.GIF;
                    break;
                case "ntf":
                    formatValue = RasterFormat.NITF;
                    break;
                case "png":
                    formatValue = RasterFormat.PNG;
                    break;
                case "xpm":
                    formatValue = RasterFormat.XPM;
                    break;
                case "asc":
                    formatValue = RasterFormat.AAIGrid;
                    break;
                case "bt":
                    formatValue = RasterFormat.BT;
                    break;
                case "fit":
                    formatValue = RasterFormat.FIT;
                    break;
                case "ddf":
                    formatValue = RasterFormat.MFF2;
                    break;
                case "WinDisp":
                    formatValue = RasterFormat.IDA;
                    break;
                case "mpr":
                case "mpl":
                    formatValue = RasterFormat.ILWIS;
                    break;
                case "imr":
                    formatValue = RasterFormat.MEM;
                    break;
                case "hdr":
                    formatValue = RasterFormat.MFF;
                    break;
                case "pAux":
                    formatValue = RasterFormat.pAux;
                    break;
                case "map":
                    formatValue = RasterFormat.PCRaster;
                    break;
                case "ppm":
                case "pgm":
                    formatValue = RasterFormat.PNM;
                    break;
                case "mtw":
                case "rsw":
                    formatValue = RasterFormat.RMF;
                    break;
                default:
                    formatValue = RasterFormat.MAPGISMSI;
                    break;
            }
        }
        return formatValue;
    }

    private void addSfcLayerToDoc(String savePath, VectorLayer vecLayer, Document document)
    {
        vecLayer.setURL(savePath);
        String[] ss = savePath.split("/");
        savePath = ss[ss.length-1];
        if(vecLayer.connectData()){
            vecLayer.setIsSymbolic(true);
            vecLayer.setIsFollowZoom(false);
            vecLayer.setName(savePath);
            Map mp = getMapByLayer(sourceRasterLayerSelect.getSelectedDocumentItem());
            if(mp!=null){
                mp.append(vecLayer);
            }else{
                if(document.getMaps().getCount()!=0){
                    document.getMaps().getMap(0).append(vecLayer);
                }else{
                    Map map = new Map();
                    map.setName("重要点提取");
                    map.append(vecLayer);
                    document.getMaps().append(map);
                }
            }
        }
    }

    private void  addRasterLayerToDoc(String savePath, RasterLayer rasterLayer, Document document)
    {
        if(savePath.contains("/ras/")){
            // 栅格数据集
            rasterLayer.setURL(savePath);
            String [] ss = savePath.split("/");
            savePath = ss[ss.length-1];
        }else{
            // 影像文件
            rasterLayer.setURL("file:///"+savePath);
            String[] ss = savePath.split("\\\\");
            savePath = ss[ss.length-1];
        }
        if(rasterLayer.connectData()){
            rasterLayer.setName(savePath);
            if (document.getMaps().getCount()!=0){
                document.getMaps().getMap(0).append(rasterLayer);
            }else{
                Map map = new Map();
                map.setName(this.getTitle());
                map.append(rasterLayer);
                document.getMaps().append(map);
            }
        }
    }

    private Map getMapByLayer(DocumentItem item)
    {
        if(item==null){
            return null;
        }
        DocumentItem itemParent = item.getParent();
        if(itemParent.getDocumentItemType().equals(DocumentItemType.Map)){
            return  (Map)itemParent;
        }else{
            return getMapByLayer(itemParent);
        }
    }
}
