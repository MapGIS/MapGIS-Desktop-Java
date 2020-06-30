package com.zondy.mapgis.rastereditor.dialogs;

import com.zondy.mapgis.analysis.rasteranalysis.OutputInfo;
import com.zondy.mapgis.analysis.rasteranalysis.OutputInfoEx;
import com.zondy.mapgis.analysis.rasteranalysis.RasterMakerOp;
import com.zondy.mapgis.controls.common.ButtonEdit;
import com.zondy.mapgis.controls.common.ButtonEditEvent;
import com.zondy.mapgis.filedialog.GDBSaveFileDialog;
import com.zondy.mapgis.geodatabase.raster.PixelType;
import com.zondy.mapgis.geodatabase.raster.RasterFormat;
import com.zondy.mapgis.geometry.Dot;
import com.zondy.mapgis.geometry.Rect;
import com.zondy.mapgis.map.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 函数生成规则网
 */
public class CreateRasterFromMathDialog extends Dialog{
    //
    private VBox mathVBox = null;
    //
    private TextField mathExpressionTextField = null;
    //
    private Label XMinLabel = null;
    private TextField XMinTextField = null;
    private Label XMaxLabel = null;
    private TextField XMaxTextField = null;
    private Label YMinLabel = null;
    private TextField YMinTextField = null;
    private Label YMaxLabel = null;
    private TextField YMaxTextField = null;
    private Label XCellSizeLabel = null;
    private TextField XCellSizeTextField = null;
    private Label YCellSizeLabel = null;
    private TextField YCellSizeTextField = null;
    //
    private Label outputPathLabel = null;
    private ButtonEdit outputPathButtonEdit = null;
    //
    private CheckBox addToMapDocCheckBox = null;
    private CheckBox isIntCheckBox = null;
    private Button okButton = null;
    private Button okUnVisibleButton = null;
    private Button cancleButton = null;
    private Button cancleUnVisibleButton = null;

    public CreateRasterFromMathDialog(Document document)
    {
        setTitle("函数生成规则网");
        setResizable(false);

        initMathVBox();

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setContent(mathVBox);
        dialogPane.getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);
        dialogPane.setPrefSize(500,420);
        dialogPane.setMinSize(500,420);

        okUnVisibleButton = (Button)dialogPane.lookupButton(ButtonType.OK);
        okUnVisibleButton.setVisible(false);
        cancleUnVisibleButton = (Button)dialogPane.lookupButton(ButtonType.CANCEL);
        cancleUnVisibleButton.setVisible(false);

        outputPathButtonEdit.setOnButtonClick(new EventHandler<ButtonEditEvent>() {
            @Override
            public void handle(ButtonEditEvent event) {
                GDBSaveFileDialog saveRaster = new GDBSaveFileDialog();
                saveRaster.setMultiSelect(false);
                String rasterFilter = "栅格数据集" + "|ras|" + "MAPGISMSI (*.msi)" + "|*.msi|" + "GTiff (*.tif)" + "|*.tif|" + "HFA (*.img)" + "|*.img";
                saveRaster.setFilter(rasterFilter);
                Optional<String[]> RasterOption = saveRaster.showAndWait();
                if(RasterOption!=null && RasterOption.isPresent()){
                    List<String> colorRasterOutPath = Arrays.asList(RasterOption.get());
                    outputPathButtonEdit.setText(colorRasterOutPath.get(0));
                }
            }
        });

        cancleButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                cancleUnVisibleButton.disarm();
                cancleUnVisibleButton.fire();
            }
        });

        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("函数规则生成网");

                String pExpression = mathExpressionTextField.getText();
                if (pExpression.isEmpty())
                {
                    alert.headerTextProperty().set("函数表达式为空!");
                    alert.showAndWait();
                    return;
                }
                double xmin = Double.parseDouble(XMinTextField.getText());
                double xmax = Double.parseDouble(XMaxTextField.getText());
                double ymin = Double.parseDouble(YMinTextField.getText());
                double ymax = Double.parseDouble(YMaxTextField.getText());
                double xCellSize = Double.parseDouble(XCellSizeTextField.getText());
                double yCellSize = Double.parseDouble(YCellSizeTextField.getText());
                String rasterOutUrl = outputPathButtonEdit.getText();
                if(xmax<=xmin || ymax<=ymin)
                {
                    alert.headerTextProperty().set("请输入正确的范围!");
                    alert.showAndWait();
                    return;
                }
                if (xCellSize<=0 || yCellSize<=0)
                {
                    alert.headerTextProperty().set("分辨率必须为大于0的值!");
                    alert.showAndWait();
                    return;
                }
                if (rasterOutUrl.isEmpty())
                {
                    alert.headerTextProperty().set("请选择输出路径!");
                    alert.showAndWait();
                    return;
                }
                PixelType eType = isIntCheckBox.isSelected()?PixelType.Int32:PixelType.Float32;
                int nRows = (int) ((ymax-ymin)/yCellSize);
                int nCols = (int) ((xmax-xmin)/xCellSize);
                long lErr = -1;
                // 输出
                OutputInfo output = new OutputInfo();
                output.setPath(rasterOutUrl);
                output.setFormat(getRasterFormatByURL(rasterOutUrl));
                // 输出信息
                OutputInfoEx extend = new OutputInfoEx();
                extend.setCellSize(new Dot(xCellSize,yCellSize));
                extend.setOrigin(new Dot(xmin,ymax));
                extend.setPixelType(eType);
                //extend.setBandCount(1);
                extend.setHeight(nRows);
                extend.setWidth(nCols);
                output.setExtraInfo(extend);

                lErr = RasterMakerOp.createRasterFromMathFuction(pExpression,output,null);
                if (lErr>0)
                {
                    alert.headerTextProperty().set("处理成功!");
                    alert.showAndWait();
                    if (addToMapDocCheckBox.isSelected())
                    {
                        // 添加生成结果到地图文档
                        String savePath = outputPathButtonEdit.getText();
                        RasterLayer rasterLayer = new RasterLayer();
                        addLayerToDoc(savePath,rasterLayer,document);
                    }
                    okUnVisibleButton.disarm();
                    okUnVisibleButton.fire();
                } else {
                    alert.headerTextProperty().set("处理失败!");
                    alert.showAndWait();
                }
            }
        });
    }

    private void initMathVBox()
    {
        mathVBox = new VBox();
        mathVBox.setSpacing(10);
        mathVBox.setStyle("-fx-font-size: 9pt;");

        int width = 480;

        // 函数表达式
        TitledPane titledPane1 = new TitledPane();
        titledPane1.setCollapsible(false);
        titledPane1.setText("数学表达式(例:Z=F(X,Y))");
        titledPane1.setPrefWidth(width);
        titledPane1.setMinWidth(width);

        mathExpressionTextField = new TextField();
        mathExpressionTextField.setPadding(new Insets(10,10,10,10));

        // 参数设置
        TitledPane titledPane2 = new TitledPane();
        titledPane2.setCollapsible(false);
        titledPane2.setText("数据层范围");
        titledPane2.setPrefWidth(width);
        titledPane2.setMinWidth(width);

        GridPane gridPane2 = new GridPane();
        gridPane2.setPadding(new Insets(10,10,10,10));
        gridPane2.setHgap(5);
        gridPane2.setVgap(5);

        XMinLabel = new Label("X最小值:");
        XMinLabel.setPrefWidth(50);
        gridPane2.add(XMinLabel,0,0);
        XMinTextField = new TextField();
        XMinTextField.setPrefWidth(405);
        gridPane2.add(XMinTextField,1,0);
        XMaxLabel = new Label("X最大值:");
        gridPane2.add(XMaxLabel,0,1);
        XMaxTextField = new TextField();
        gridPane2.add(XMaxTextField,1,1);
        YMinLabel = new Label("Y最小值:");
        gridPane2.add(YMinLabel,0,2);
        YMinTextField = new TextField();
        gridPane2.add(YMinTextField,1,2);
        YMaxLabel = new Label("Y最大值:");
        gridPane2.add(YMaxLabel,0,3);
        YMaxTextField = new TextField();
        gridPane2.add(YMaxTextField,1,3);
        XCellSizeLabel = new Label("X分辨率:");
        gridPane2.add(XCellSizeLabel,0,4);
        XCellSizeTextField = new TextField();
        gridPane2.add(XCellSizeTextField,1,4);
        YCellSizeLabel = new Label("Y分辨率:");
        gridPane2.add(YCellSizeLabel,0,5);
        YCellSizeTextField = new TextField();
        gridPane2.add(YCellSizeTextField,1,5);

        // 输出
        TitledPane titledPane3 = new TitledPane();
        titledPane3.setCollapsible(false);
        titledPane3.setText("结果数据");
        titledPane3.setPrefWidth(width);
        titledPane3.setMinWidth(width);

        GridPane gridPane3 = new GridPane();
        gridPane3.setPadding(new Insets(10,10,10,10));
        gridPane3.setHgap(5);
        gridPane3.setVgap(5);

        outputPathLabel = new Label("输出路径:");
        outputPathLabel.setPrefWidth(60);
        gridPane3.add(outputPathLabel,0,0);
        outputPathButtonEdit = new ButtonEdit();
        outputPathButtonEdit.setPrefWidth(395);
        gridPane3.add(outputPathButtonEdit,1,0);

        // 结果
        AnchorPane anchorPane1 = new AnchorPane();
        anchorPane1.setPrefWidth(width);
        anchorPane1.setMinWidth(width);

        addToMapDocCheckBox = new CheckBox("添加到地图文档");
        addToMapDocCheckBox.setSelected(true);
        addToMapDocCheckBox.setLayoutX(0);
        addToMapDocCheckBox.setPrefWidth(140);

        isIntCheckBox = new CheckBox("输出整型数据");
        isIntCheckBox.setLayoutX(150);
        isIntCheckBox.setPrefWidth(140);

        okButton = new Button("计算");
        okButton.setPrefSize(60,30);
        okButton.setLayoutX(340);

        cancleButton = new Button("取消");
        cancleButton.setPrefSize(60,30);
        cancleButton.setLayoutX(410);

        anchorPane1.getChildren().addAll(addToMapDocCheckBox,isIntCheckBox,okButton,cancleButton);
        titledPane3.setContent(gridPane3);
        titledPane2.setContent(gridPane2);
        titledPane1.setContent(mathExpressionTextField);
        mathVBox.getChildren().addAll(titledPane1,titledPane2,titledPane3,anchorPane1);
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
            if (document.getMaps().getCount()!=0) {
                document.getMaps().getMap(0).append(rasterLayer);
            }else{
                Map map = new Map();
                map.setName("新地图1");
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
