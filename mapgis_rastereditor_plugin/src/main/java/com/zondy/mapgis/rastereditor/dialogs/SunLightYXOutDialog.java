package com.zondy.mapgis.rastereditor.dialogs;


import com.zondy.mapgis.analysis.rasteranalysis.OutputInfo;
import com.zondy.mapgis.analysis.rasteranalysis.RasterSurfaceOp;
import com.zondy.mapgis.controls.common.ButtonEdit;
import com.zondy.mapgis.controls.LayerSelectComboBoxItem;
import com.zondy.mapgis.controls.LayerSelectControl;
import com.zondy.mapgis.controls.common.ButtonEditEvent;
import com.zondy.mapgis.filedialog.GDBSaveFileDialog;
import com.zondy.mapgis.filedialog.GDBSelectFolderDialog;
import com.zondy.mapgis.geodatabase.raster.GeoRaster;
import com.zondy.mapgis.geodatabase.raster.RasterAccess;
import com.zondy.mapgis.geodatabase.raster.RasterDataset;
import com.zondy.mapgis.geodatabase.raster.RasterFormat;
import com.zondy.mapgis.map.*;
import com.zondy.mapgis.utilities.UtilityTool;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 日照晕渲图输出
 * @author yuhan
 * @version 1.0.0
 */
public class SunLightYXOutDialog extends Dialog{

    VBox sunLightYXOutVBox = null;
    Stage primaryStage = null;
    // 源数据
    private Label inputRasterLabel = null;
    private LayerSelectControl inputRasterLayerSelect = null;
    //private Label inputRasterBandNoLabel = null;
    //private ComboBox<Long> inputRastserBandNoComboBox = null;
    // 参数设置
    //private Label paramsTypeLabel = null;
    //private RadioButton paramsHornRadioButton = null;
   // private RadioButton paramsSunShineRadioButton = null;
    private Label paramsHeightAngleLabel = null;
    private TextField paramsHeightAngleTextField = null;
    private Label paramsAzimuthLabel = null;
    private TextField paramsAzimuthTextField = null;
    private Label paramsZFactorLabel = null;
    private TextField paramsZFactorTextField = null;
    // 结果输出
    private Label outputRasterSunShineLabel = null;
    private ButtonEdit outputRasterSunShineButtonEdit = null;
    private CheckBox outputRasterGrayCheckBox = null;
    private ButtonEdit outputRasterGrayButtonEdit = null;
    private CheckBox outputRasterColorCheckBox = null;
    private ButtonEdit outputRasterColorButtonEdit = null;
    // 计算
    private CheckBox addToMapDocument = null;
    private Button calButton = null;
    private Button okButtonUnVisible = null;
    private Button cancleButton = null;
    private Button cancleButtonUnVisible = null;

    public SunLightYXOutDialog(Document document){
        setTitle("日照晕渲图输出");

        initSunLightYXOutVBox(document);

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setContent(sunLightYXOutVBox);
        dialogPane.getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);
        primaryStage = (Stage)dialogPane.getScene().getWindow();
        dialogPane.setMinSize(500,405);
        dialogPane.setPrefSize(500,405);

        okButtonUnVisible = (Button)dialogPane.lookupButton(ButtonType.OK);
        okButtonUnVisible.setVisible(false);
        cancleButtonUnVisible = (Button)dialogPane.lookupButton(ButtonType.CANCEL);
        cancleButtonUnVisible.setVisible(false);
    }

    private void initSunLightYXOutVBox(Document document){
        sunLightYXOutVBox = new VBox();
        sunLightYXOutVBox.setSpacing(7);
        sunLightYXOutVBox.setStyle("-fx-font-size: 9pt;");

        int width1 = 480;
        int height1 = 70;
        // region 源数据
        TitledPane inputRasterTitledPane = new TitledPane();
        inputRasterTitledPane.setText("源数据");
        inputRasterTitledPane.setCollapsible(false);
        inputRasterTitledPane.setPrefSize(width1,height1);
        inputRasterTitledPane.setMinSize(width1,height1);

        GridPane inputRasterGridPane = new GridPane();
        inputRasterGridPane.setPadding(new Insets(10,10,10,10));
        inputRasterGridPane.setHgap(5);
        inputRasterGridPane.setVgap(5);

        inputRasterLabel = new Label("输入栅格:");
        inputRasterLabel.setPrefWidth(60);
        inputRasterGridPane.add(inputRasterLabel,0,0);
        String inputRatserFilter = "栅格数据，6x数据|ras;*.msi;*.tif;*.img";
        inputRasterLayerSelect = new LayerSelectControl(document,inputRatserFilter);
        inputRasterLayerSelect.setPrefWidth(400);
        inputRasterGridPane.add(inputRasterLayerSelect,1,0);
        inputRasterTitledPane.setContent(inputRasterGridPane);
        // endregion

        int width2 = 480;
        int height2 = 130;
        // region 参数设置
        TitledPane paramsTitledPane = new TitledPane();
        paramsTitledPane.setText("参数设置");
        paramsTitledPane.setCollapsible(false);
        paramsTitledPane.setPrefSize(width2,height2);
        paramsTitledPane.setMinSize(width2,height2);

        GridPane paramsGridPane = new GridPane();
        paramsGridPane.setPadding(new Insets(10,10,10,10));
        paramsGridPane.setVgap(5);
        paramsGridPane.setHgap(5);

        paramsHeightAngleLabel = new Label("高度角:");
        paramsGridPane.add(paramsHeightAngleLabel,0,0);
        paramsHeightAngleTextField = new TextField();
        paramsHeightAngleTextField.setEditable(true);
        paramsHeightAngleTextField.setText(Double.toString(45.0));
        paramsGridPane.add(paramsHeightAngleTextField,1,0);

        paramsAzimuthLabel = new Label("方位角:");
        paramsGridPane.add(paramsAzimuthLabel,0,1);
        paramsAzimuthTextField = new TextField();
        paramsAzimuthTextField.setEditable(true);
        paramsAzimuthTextField.setText(Double.toString(135.0));
        paramsGridPane.add(paramsAzimuthTextField,1,1);

        paramsZFactorLabel = new Label("高程缩放因子:");
        paramsZFactorLabel.setPrefWidth(80);
        paramsGridPane.add(paramsZFactorLabel,0,2);
        paramsZFactorTextField = new TextField();
        paramsZFactorTextField.setEditable(true);
        paramsZFactorTextField.setPrefWidth(380);
        paramsZFactorTextField.setText(Integer.toString(1));
        paramsGridPane.add(paramsZFactorTextField,1,2);
        paramsTitledPane.setContent(paramsGridPane);
        // endregion

        int width3 = 480;
        int height3 = 130;
        // region 结果输出
        TitledPane outputRasterTitledPane = new TitledPane();
        outputRasterTitledPane.setText("结果数据");
        outputRasterTitledPane.setCollapsible(false);
        outputRasterTitledPane.setPrefSize(width3,height3);
        outputRasterTitledPane.setMinSize(width3,height3);

        GridPane outputRasterGridPane = new GridPane();
        outputRasterGridPane.setPadding(new Insets(10,10,10,10));
        outputRasterGridPane.setHgap(5);
        outputRasterGridPane.setVgap(5);

        outputRasterSunShineLabel = new Label("日照图:");
        outputRasterGridPane.add(outputRasterSunShineLabel,0,0);
        outputRasterSunShineButtonEdit = new ButtonEdit();
        outputRasterSunShineButtonEdit.setOnButtonClick(new EventHandler<ButtonEditEvent>() {
            @Override
            public void handle(ButtonEditEvent event) {
                GDBSaveFileDialog saveRaster = new GDBSaveFileDialog();
                saveRaster.setMultiSelect(false);
                String rasterFilter = "栅格数据集" + "|ras|" + "MAPGISMSI (*.msi)" + "|*.msi|" + "GTiff (*.tif)" + "|*.tif|"  + "HFA (*.img)" + "|*.img";
                saveRaster.setFilter(rasterFilter);
                Optional<String[]> rasterOption = saveRaster.showAndWait();
                if(rasterOption!=null && rasterOption.isPresent()){
                    List<String> rasterOutPath = Arrays.asList(rasterOption.get());
                    outputRasterSunShineButtonEdit.setText(rasterOutPath.get(0));
                }
            }
        });
        outputRasterGridPane.add(outputRasterSunShineButtonEdit,1,0);

        outputRasterGrayCheckBox = new CheckBox("灰度图");
        outputRasterGrayCheckBox.setSelected(false);
        outputRasterGrayCheckBox.setPrefWidth(65);
        outputRasterGrayCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue){
                    outputRasterGrayButtonEdit.setDisable(false);
                    outputRasterGrayButtonEdit.setDisable(false);
                }else {
                    outputRasterGrayButtonEdit.setDisable(true);
                    outputRasterGrayButtonEdit.setDisable(true);
                }
            }
        });
        outputRasterGridPane.add(outputRasterGrayCheckBox,0,1);
        outputRasterGrayButtonEdit = new ButtonEdit();
        outputRasterGrayButtonEdit.setPrefWidth(395);
        outputRasterGrayButtonEdit.setDisable(true);
        outputRasterGrayButtonEdit.setOnButtonClick(new EventHandler<ButtonEditEvent>() {
            @Override
            public void handle(ButtonEditEvent event) {
                GDBSaveFileDialog saveGrayRaster = new GDBSaveFileDialog();
                saveGrayRaster.setMultiSelect(false);
                String rasterFilter = "栅格数据集" + "|ras|" + "MAPGISMSI (*.msi)" + "|*.msi|" + "GTiff (*.tif)" + "|*.tif|"  + "HFA (*.img)" + "|*.img";
                saveGrayRaster.setFilter(rasterFilter);
                Optional<String[]> grayRasterOption = saveGrayRaster.showAndWait();
                if(grayRasterOption!=null && grayRasterOption.isPresent()){
                    List<String> grayRasterOutPath = Arrays.asList(grayRasterOption.get());
                    outputRasterGrayButtonEdit.setText(grayRasterOutPath.get(0));
                }
            }
        });
        outputRasterGridPane.add(outputRasterGrayButtonEdit,1,1);

        outputRasterColorCheckBox = new CheckBox("彩色图");
        outputRasterColorCheckBox.setSelected(false);
        outputRasterColorCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue){
                    outputRasterColorButtonEdit.setDisable(false);
                }else{
                    outputRasterColorButtonEdit.setDisable(true);
                }
            }
        });
        outputRasterGridPane.add(outputRasterColorCheckBox,0,2);
        outputRasterColorButtonEdit = new ButtonEdit();
        outputRasterColorButtonEdit.setDisable(true);
        outputRasterColorButtonEdit.setOnButtonClick(new EventHandler<ButtonEditEvent>() {
            @Override
            public void handle(ButtonEditEvent event) {
                GDBSaveFileDialog saveColorRaster = new GDBSaveFileDialog();
                saveColorRaster.setMultiSelect(false);
                String rasterFilter = "栅格数据集" + "|ras|" + "MAPGISMSI (*.msi)" + "|*.msi|" + "GTiff (*.tif)" + "|*.tif|"  + "HFA (*.img)" + "|*.img";
                saveColorRaster.setFilter(rasterFilter);
                Optional<String[]> colorRasterOption = saveColorRaster.showAndWait();
                if(colorRasterOption!=null && colorRasterOption.isPresent()){
                    List<String> colorRasterOutPath = Arrays.asList(colorRasterOption.get());
                    outputRasterColorButtonEdit.setText(colorRasterOutPath.get(0));
                }
            }
        });
        outputRasterGridPane.add(outputRasterColorButtonEdit,1,2);
        outputRasterTitledPane.setContent(outputRasterGridPane);
        // endregion

        // region 计算
        AnchorPane anchorPane1 = new AnchorPane();
        anchorPane1.setPrefWidth(480);
        anchorPane1.setMinWidth(480);

        addToMapDocument = new CheckBox("添加到地图文档");
        addToMapDocument.setSelected(true);
        addToMapDocument.setLayoutX(0);
        addToMapDocument.setPrefWidth(250);

        calButton = new Button("计算");
        calButton.setPrefSize(60,30);
        calButton.setLayoutX(345);
        calButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("日照晕渲图输出");

                if(inputRasterLayerSelect.getSelectedItemUrl().isEmpty()){
                    alert.headerTextProperty().set("输入栅格数据为空!");
                    alert.showAndWait();
                    return;
                }
                if(Double.parseDouble(paramsAzimuthTextField.getText())<0 || Double.parseDouble(paramsAzimuthTextField.getText())>360){
                    alert.headerTextProperty().set("请输入正确的方位角!");
                    alert.showAndWait();
                    return;
                }
                if(Double.parseDouble(paramsHeightAngleTextField.getText())<0 || Double.parseDouble(paramsHeightAngleTextField.getText())>90){
                    alert.headerTextProperty().set("请输入正确的高度角!");
                    alert.showAndWait();
                    return;
                }
                if(outputRasterSunShineButtonEdit.getText().isEmpty()){
                    alert.headerTextProperty().set("日照图路径为空!");
                    alert.showAndWait();
                    return;
                }
                if(outputRasterGrayCheckBox.isSelected()){
                    if(outputRasterGrayButtonEdit.getText().isEmpty()){
                        alert.headerTextProperty().set("山体阴影图路径为空!");
                        alert.showAndWait();
                        return;
                    }
                }
                if(outputRasterColorCheckBox.isSelected()){
                    if(outputRasterColorButtonEdit.getText().isEmpty()){
                        alert.headerTextProperty().set("山体阴影彩色图路径为空!");
                        alert.showAndWait();
                        return;
                    }
                }
                // 栅格数据
                String tmpRasterPath = inputRasterLayerSelect.getSelectedItemUrl();
                GeoRaster geoRaster = new GeoRaster();
                OutputInfo output = new OutputInfo();
                OutputInfo outputHillShade = null;
                OutputInfo outputColor = null;
                if(!geoRaster.Open(tmpRasterPath)){
                    alert.headerTextProperty().set("打开栅格数据失败!");
                    alert.showAndWait();
                    return;
                }
                // 参数
                double tmpAzimuth = Double.parseDouble(paramsAzimuthTextField.getText());
                double tmpAltitude = Double.parseDouble(paramsHeightAngleTextField.getText());
                double tmpZFactor = Double.parseDouble(paramsZFactorTextField.getText());
                String tmpOutputPath = outputRasterSunShineButtonEdit.getText();
                output.setPath(tmpOutputPath);
                output.setFormat(getRasterFormatByURL(tmpOutputPath));
                // 计算
                long lErr1 = -1, lErr2 = -1, lErr3 = -1;
                lErr1 = RasterSurfaceOp.sunshine(geoRaster, output, tmpAzimuth, tmpAltitude, tmpZFactor, null);
                if(outputRasterGrayCheckBox.isSelected()){
                    String tmpOutputHillShade = outputRasterGrayButtonEdit.getText();
                    outputHillShade = new OutputInfo();
                    outputHillShade.setPath(tmpOutputHillShade);
                    outputHillShade.setFormat(getRasterFormatByURL(tmpOutputHillShade));
                    lErr2 = RasterSurfaceOp.hillShade(geoRaster, outputHillShade, tmpAzimuth, tmpAltitude, tmpZFactor, null);
                }else{
                    lErr2 = 1;
                }
                if(outputRasterColorCheckBox.isSelected()){
                    String tmpOutputColor = outputRasterColorButtonEdit.getText();
                    outputColor = new OutputInfo();
                    outputColor.setPath(tmpOutputColor);
                    outputColor.setFormat(getRasterFormatByURL(tmpOutputColor));
                    lErr3 = RasterSurfaceOp.hillShadeColor(geoRaster, outputColor, tmpAzimuth, tmpAltitude, tmpZFactor, null);
                }else{
                    lErr3 = 1;
                }

                if(lErr1<=0 || lErr2<=0 || lErr3<=0 ) {
                    alert.titleProperty().set("日照晕渲图");
                    alert.headerTextProperty().set("日照晕渲图失败!");
                    alert.showAndWait();
                }else{
                    alert.titleProperty().set("日照晕渲图");
                    alert.headerTextProperty().set("日照晕渲图成功!");
                    alert.showAndWait();
                    if(addToMapDocument.isSelected()){
                        // 添加日照图结果到地图文档
                        String savePath1 = outputRasterSunShineButtonEdit.getText().replace("\\","\\\\");
                        RasterLayer rasterLayer1 = new RasterLayer();
                        addLayerToDoc(savePath1, rasterLayer1, document);
                        // 添加山体阴影图结果到地图文档
                        if(outputRasterGrayCheckBox.isSelected()){
                            String savePath2 = outputRasterGrayButtonEdit.getText().replace("\\","\\\\");
                            RasterLayer rasterLayer2 = new RasterLayer();
                            addLayerToDoc(savePath2, rasterLayer2, document);
                        }
                        // 添加山体阴影彩色图结果到地图文档
                        if(outputRasterColorCheckBox.isSelected()){
                            String savePath3 = outputRasterColorButtonEdit.getText().replace("\\","\\\\");
                            RasterLayer rasterLayer3 = new RasterLayer();
                            addLayerToDoc(savePath3, rasterLayer3, document);
                        }
                    }
                    okButtonUnVisible.disarm();
                    okButtonUnVisible.fire();
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
        anchorPane1.getChildren().addAll(addToMapDocument,calButton,cancleButton);
        // endregion

        sunLightYXOutVBox.getChildren().addAll(inputRasterTitledPane,paramsTitledPane,outputRasterTitledPane,anchorPane1);
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

    private void  addLayerToDoc(String savePath, RasterLayer rasterLayer, Document document)
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
