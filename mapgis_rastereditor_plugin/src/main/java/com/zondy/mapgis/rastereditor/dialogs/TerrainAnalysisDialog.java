package com.zondy.mapgis.rastereditor.dialogs;

import com.zondy.mapgis.analysis.rasteranalysis.OutputInfo;
import com.zondy.mapgis.analysis.rasteranalysis.RasterSurfaceOp;
import com.zondy.mapgis.controls.common.ButtonEdit;
import com.zondy.mapgis.controls.LayerSelectComboBoxItem;
import com.zondy.mapgis.controls.LayerSelectControl;
import com.zondy.mapgis.controls.common.ButtonEditEvent;
import com.zondy.mapgis.filedialog.GDBSaveFileDialog;
import com.zondy.mapgis.geodatabase.raster.GeoRaster;
import com.zondy.mapgis.geodatabase.raster.RasterAccess;
import com.zondy.mapgis.geodatabase.raster.RasterDataset;
import com.zondy.mapgis.geodatabase.raster.RasterFormat;
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
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 地形分析界面
 * @author yuhan
 * @version 1.0.0
 */
public class TerrainAnalysisDialog extends Dialog {

    VBox rasterTerrainVBox = null;
    Stage primaryStage = null;
    // 源数据
    private Label rasterDataLabel = null;
    private LayerSelectControl rasterSourceLayerSelect = null;
    // 参数设置
    private Label ZValueLabel = null;
    private TextField zValueTextFiled = null;
    private Label analyseTypeLabel = null;
    private  ComboBox<String> analyseTyepComboBox = null;
    // 结果输出
    private CheckBox addToMapCheckBox = null;
    private Label outputPathLabel = null;
    private ButtonEdit outputSaveButtonEdit = null;
    //
    private Button calButton =  null;
    private Button okButtonUnVisible = null;
    private Button cancleButton = null;
    private Button cancleButtonUnVisible = null;

    public TerrainAnalysisDialog(Document document){
        setTitle("地形分析");

        initRasterTerrainVBox(document);

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setContent(rasterTerrainVBox);
        dialogPane.getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);
        primaryStage = (Stage)dialogPane.getScene().getWindow();
        dialogPane.setPrefSize(520,300);
        dialogPane.setMinSize(520,300);
        dialogPane.setMaxSize(520,300);

        okButtonUnVisible = (Button)dialogPane.lookupButton(ButtonType.OK);
        okButtonUnVisible.setVisible(false);
        cancleButtonUnVisible = (Button)dialogPane.lookupButton(ButtonType.CANCEL);
        cancleButtonUnVisible.setVisible(false);
    }

    private void initRasterTerrainVBox(Document document){

        rasterTerrainVBox = new VBox();
        rasterTerrainVBox.setSpacing(5);
        rasterTerrainVBox.setStyle("-fx-font-size: 8pt;");

        int width0 = 500;
        int height0 = 70;

        // 源数据
        TitledPane rasterSourceTitledPane = new TitledPane();
        rasterSourceTitledPane.setText("源数据");
        rasterSourceTitledPane.setCollapsible(false);
        rasterSourceTitledPane.setPrefSize(width0,height0);
        rasterSourceTitledPane.setMinSize(width0,height0);

        GridPane rasterSourceGridPane = new GridPane();
        rasterSourceGridPane.setPadding(new Insets(10,10,10,10));
        rasterSourceGridPane.setHgap(5);
        rasterSourceGridPane.setVgap(5);

        rasterDataLabel = new Label("栅格数据:");
        rasterDataLabel.setPrefWidth(50);
        rasterSourceGridPane.add(rasterDataLabel,0,0);
        String rasterFilter = "栅格数据，6x数据|ras;*.msi;*.tif;*.img";
        rasterSourceLayerSelect = new LayerSelectControl(document,rasterFilter);
        rasterSourceLayerSelect.setPrefWidth(425);
        rasterSourceGridPane.add(rasterSourceLayerSelect,1,0);
        rasterSourceTitledPane.setContent(rasterSourceGridPane);

        // 参数设置
        TitledPane paramsTitledPane = new TitledPane();
        paramsTitledPane.setText("参数设置");
        paramsTitledPane.setCollapsible(false);
        paramsTitledPane.setMinWidth(width0);
        paramsTitledPane.setMaxWidth(width0);

        GridPane paramsGridPane = new GridPane();
        paramsGridPane.setPadding(new Insets(10,10,10,10));
        paramsGridPane.setHgap(5);
        paramsGridPane.setVgap(5);

        analyseTypeLabel = new Label("地形因子:");
        paramsGridPane.add(analyseTypeLabel,0,0);
        analyseTyepComboBox = new ComboBox<String>();
        analyseTyepComboBox.setEditable(false);
        analyseTyepComboBox.setPrefWidth(405);
        analyseTyepComboBox.getItems().addAll("坡度","坡向","倾斜百分比","地形起伏度","曲率");
        analyseTyepComboBox.getSelectionModel().selectFirst();
        paramsGridPane.add(analyseTyepComboBox,1,0);

        ZValueLabel = new Label("高程缩放因子:");
        ZValueLabel.setPrefWidth(70);
        paramsGridPane.add(ZValueLabel,0,1);
        zValueTextFiled = new TextField();
        zValueTextFiled.setEditable(true);
        zValueTextFiled.setText(Integer.toString(1));
        paramsGridPane.add(zValueTextFiled,1,1);
        paramsTitledPane.setContent(paramsGridPane);

        // 结果数据
        TitledPane outputRasterTitledPane = new TitledPane();
        outputRasterTitledPane.setText("结果数据");
        outputRasterTitledPane.setCollapsible(false);
        outputRasterTitledPane.setMinWidth(width0);
        outputRasterTitledPane.setMaxWidth(width0);

        GridPane outputRasterGridPane = new GridPane();
        outputRasterGridPane.setPadding(new Insets(10,10,10,10));
        outputRasterGridPane.setHgap(5);
        outputRasterGridPane.setVgap(5);

        outputPathLabel = new Label("输出路径:");
        outputPathLabel.setPrefWidth(50);
        outputRasterGridPane.add(outputPathLabel,0,0);

        outputSaveButtonEdit = new ButtonEdit();
        outputSaveButtonEdit.setMinWidth(425);
        outputSaveButtonEdit.setPrefWidth(425);
        outputSaveButtonEdit.setOnButtonClick(new EventHandler<ButtonEditEvent>() {
            @Override
            public void handle(ButtonEditEvent event) {
                GDBSaveFileDialog saveToGDB = new GDBSaveFileDialog();
                saveToGDB.setMultiSelect(false);
                String rasterFilter = "栅格数据集" + "|ras|" + "MAPGISMSI (*.msi)" + "|*.msi|" + "GTiff (*.tif)" + "|*.tif|"  + "HFA (*.img)" + "|*.img";
                saveToGDB.setFilter(rasterFilter);
                Optional<String[]> optional = saveToGDB.showAndWait();
                if(optional!=null && optional.isPresent()){
                    List<String> outputRasterGDBPath = Arrays.asList(optional.get());
                    outputSaveButtonEdit.setText(outputRasterGDBPath.get(0));
                }
            }
        });
        outputRasterGridPane.add(outputSaveButtonEdit,1,0);
        outputRasterTitledPane.setContent(outputRasterGridPane);

        AnchorPane anchorPane1 = new AnchorPane();
        anchorPane1.setPrefWidth(width0);
        anchorPane1.setMinWidth(width0);

        addToMapCheckBox = new CheckBox("添加到地图文档");
        addToMapCheckBox.setPrefWidth(250);
        addToMapCheckBox.setSelected(true);
        addToMapCheckBox.setLayoutX(0);

        calButton =  new Button("计算");
        calButton.setPrefSize(60,30);
        calButton.setLayoutX(345);
        calButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("地形分析结果");

                String test = rasterSourceLayerSelect.getSelectedItemUrl();
                System.out.print(test);

                if(rasterSourceLayerSelect.getSelectedItemUrl().isEmpty()){
                    alert.headerTextProperty().set("输入栅格数据为空!");
                    alert.showAndWait();
                    return;
                }
                if(Double.parseDouble(zValueTextFiled.getText())<=0){
                    alert.headerTextProperty().set("请输入正确的缩放因子!");
                    alert.showAndWait();
                    return;
                }
                if(outputSaveButtonEdit.getText().isEmpty()){
                    alert.headerTextProperty().set("输出栅格路径为空!");
                    alert.showAndWait();
                    return;
                }
                // 栅格数据
                long lErr = -1;
                String tmpRasterPath = rasterSourceLayerSelect.getSelectedItemUrl();
                GeoRaster geoRaster = new GeoRaster();
                OutputInfo output = new OutputInfo();
                if(!geoRaster.Open(tmpRasterPath)){
                    alert.headerTextProperty().set("打开栅格数据失败!");
                    alert.showAndWait();
                    return;
                }
                // 参数
                Double tmpZFactor = Double.parseDouble(zValueTextFiled.getText());
                String outputRasterPath = outputSaveButtonEdit.getText();
                output.setPath(outputRasterPath);
                output.setFormat(getRasterFormatByURL(outputRasterPath));
                String terrainType = analyseTyepComboBox.getValue();
                lErr = -1;
                switch (terrainType){
                    //"坡度","坡向","倾斜百分比","地形起伏度","曲率"
                    case "坡度":
                        lErr = RasterSurfaceOp.slope(geoRaster, output, tmpZFactor, null);
                        break;
                    case "坡向":
                        lErr = RasterSurfaceOp.aspect(geoRaster, output, null);
                        break;
                    case "倾斜百分比":
                        lErr = RasterSurfaceOp.slopePrecent(geoRaster, output, tmpZFactor, null);
                        break;
                    case "地形起伏度":
                        lErr = RasterSurfaceOp.coarsbeness(geoRaster, output,tmpZFactor, null);
                        break;
                    case "曲率":
                        lErr = RasterSurfaceOp.curvature(geoRaster, output, tmpZFactor, null);
                        break;
                }
                if(lErr>0){
                    alert.titleProperty().set("栅格地形分析");
                    alert.headerTextProperty().set(terrainType+"分析成功!");
                    alert.showAndWait();
                    if(addToMapCheckBox.isSelected()){
                        String savePath = outputSaveButtonEdit.getText().replace("\\","\\\\");
                        RasterLayer rasLayer = new RasterLayer();
                        addLayerToDoc(savePath, rasLayer,document);
                    }
                    okButtonUnVisible.disarm();
                    okButtonUnVisible.fire();
                }else{
                    alert.titleProperty().set("栅格地形分析");
                    alert.headerTextProperty().set(terrainType+"分析失败!");
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
        anchorPane1.getChildren().addAll(addToMapCheckBox,calButton,cancleButton);

        rasterTerrainVBox.getChildren().addAll(rasterSourceTitledPane,paramsTitledPane,outputRasterTitledPane,anchorPane1);
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
