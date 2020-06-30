package com.zondy.mapgis.rastereditor.dialogs;

import com.zondy.mapgis.analysis.rasteranalysis.OutputInfo;
import com.zondy.mapgis.analysis.rasteranalysis.OutputInfoEx;
import com.zondy.mapgis.analysis.rasteranalysis.RasterMathOp;
import com.zondy.mapgis.controls.LayerSelectComboBoxItem;
import com.zondy.mapgis.controls.LayerSelectControl;
import com.zondy.mapgis.controls.common.ButtonEdit;
import com.zondy.mapgis.controls.common.ButtonEditEvent;
import com.zondy.mapgis.filedialog.GDBOpenFileDialog;
import com.zondy.mapgis.filedialog.GDBSaveFileDialog;
import com.zondy.mapgis.geodatabase.raster.*;
import com.zondy.mapgis.geometry.Dot;
import com.zondy.mapgis.geometry.Rect;
import com.zondy.mapgis.map.Document;
import com.zondy.mapgis.map.Map;
import com.zondy.mapgis.map.RasterLayer;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 * 栅格计算器主界面
 */
public class FormulaCaculateDialog extends Dialog{

    private VBox leftVBox =  null;
    private TitledPane titledPane = null;
    private HBox baseHBox = null;
    // 源数据
    private Label inputLabel = null;
    private LayerSelectControl inputLayerSelect = null;
    private TableView<FormulaItem> tableview = null;
    private ObservableList<FormulaItem> data = null;
    // 公式
    private TextArea expressionTextArea = null;
    private Button importButton = null;
    private Button exportButton = null;
    // 栅格数据输出
    private Label outputRasterTypeLabel = null;
    private ComboBox<String> outputRasterTypeComboBox = null;
    private Label outputXLabel = null;
    private TextField outputXTextField = null;
    private Label outputYLabel = null;
    private TextField outputYTextField = null;
    private Label outputPathLabel = null;
    private ButtonEdit outputPathButtonEdit = null;
    // 确定
    private CheckBox addToMapCheckBox = null;
    private Button okButton = null;
    private Button okButtonUnVisible = null;
    private Button cancleButton = null;
    private Button cancleButtonUnVisible = null;
    // 变量与数字
    private Button btn_I = null;
    private Button btn_0 = null;
    private Button btn_1 = null;
    private Button btn_2 = null;
    private Button btn_3 = null;
    private Button btn_4 = null;
    private Button btn_5 = null;
    private Button btn_6 = null;
    private Button btn_7 = null;
    private Button btn_8 = null;
    private Button btn_9 = null;
    // 分隔符
    private Button btn_Point = null;
    private Button btn_LeftBrackets = null;
    private Button btn_RightBrackets = null;
    private Button btn_Comma = null;
    private Button btn_Blank = null;
    // 运算符
    private Button btn_Plus = null;
    private Button btn_Minus = null;
    private Button btn_Multiply = null;
    private Button btn_Divide = null;
    private Button btn_Greater = null;
    private Button btn_Less = null;
    private Button btn_Equal = null;
    private Button btn_NotEqual = null;
    private Button btn_GreatEqual = null;
    private Button btn_LessEqual = null;
    private Button btn_Mod = null;
    private Button btn_Div = null;
    private Button btn_LogicNot = null;
    private Button btn_LogicAnd = null;
    private Button btn_LogicXor = null;
    private Button btn_LogicOr = null;
    private Button btn_BitWizeAnd = null;
    private Button btn_BitWizeOr = null;
    private Button btn_BitWizeNot = null;
    private Button btn_Clear = null;
    // 函数
    private Label mathLabel = null;
    private ComboBox<String> mathComboBox = null;
    private Label trigonometricLabel = null;
    private ComboBox<String> trigonometricComboBox = null;
    private Label exponentialAndLogarithmicLabel = null;
    private ComboBox<String> exponentialAndLogarithmicComboBox = null;
    private Label elseLabel = null;
    private ComboBox<String> elseComboBox = null;

    public FormulaCaculateDialog(Document document){
        setTitle("栅格计算器");
        setResizable(false);

        initLeftVBox(document);
        initRightVBox();

        baseHBox = new HBox();
        baseHBox.setSpacing(5);
        baseHBox.getChildren().addAll(leftVBox,titledPane);

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.setContent(baseHBox);
        dialogPane.setPrefSize(840,675);
        dialogPane.setMaxSize(850,675);
        dialogPane.setMinSize(850,675);

        // 处理 ButtonType
        okButtonUnVisible = (Button)dialogPane.lookupButton(ButtonType.OK);
        okButtonUnVisible.setVisible(false);
        cancleButtonUnVisible = (Button)dialogPane.lookupButton(ButtonType.CANCEL);
        cancleButtonUnVisible.setVisible(false);

        // 输入栅格数据到列表
        inputLayerSelect.setOnSelectedItemChanged(new ChangeListener<LayerSelectComboBoxItem>() {
            @Override
            public void changed(ObservableValue<? extends LayerSelectComboBoxItem> observable, LayerSelectComboBoxItem oldValue, LayerSelectComboBoxItem newValue) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("栅格计算器");

                // 变量名
                Integer tableviewItemCounts = tableview.getItems().size()+1;
                String rasterAliasName = "I"+tableviewItemCounts.toString();
                // 影像路径
                String rasterUrl = inputLayerSelect.getSelectedItemUrl();
                if(rasterUrl.isEmpty()){
                    alert.headerTextProperty().set("输入的栅格数据路径为空!");
                    alert.showAndWait();
                    return;
                }
                if(rasterUrl.toLowerCase().startsWith("file:///")){
                    rasterUrl = rasterUrl.substring(8);
                }
                //rasterUrl = rasterUrl.replace("\\","\\\\");
                if(tableviewItemCounts==1 && outputXTextField!=null && outputYTextField!=null){
                    RasterDataset raster = new RasterDataset();
                    if(raster.open(rasterUrl.replace("\\","\\\\"), RasterAccess.Read)!=0){
                        outputXTextField.setText(Double.toString(raster.getResolutionX()));
                        outputYTextField.setText(Double.toString(raster.getResolutionY()));
                    }
                    raster.close();
                }
                tableview.getItems().add(new FormulaItem(rasterAliasName, rasterUrl));
                tableview.refresh();
            }
        });

        // 列表每行的鼠标事件
        tableview.setRowFactory(new Callback<TableView<FormulaItem>, TableRow<FormulaItem>>() {
            @Override
            public TableRow<FormulaItem> call(TableView<FormulaItem> param) {
                TableRow<FormulaItem> row = new TableRow<FormulaItem>();
                // 右键菜单
                // 移除
                ContextMenu contextMenu = new ContextMenu();
                MenuItem removeMenuItem = new MenuItem("移除");
                removeMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        tableview.getItems().remove(row.getItem());
                        // 列表更新
                        for(Integer index=0; index<tableview.getItems().size(); index++){
                            String rasterAliasName = "I"+Integer.toString(index+1);
                            tableview.getItems().get(index).setAliasName(rasterAliasName);
                        }
                        tableview.refresh();
                    }
                });
                // 作为分辨率输出
                MenuItem addRangeItem = new MenuItem("作为分辨率输出:");
                addRangeItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        String rasterPath = row.getItem().getRasterName();
                        RasterDataset raster = new RasterDataset();
                        if(raster.open(rasterPath.replace("\\","\\\\"), RasterAccess.Read)==0){
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("栅格计算器");
                            alert.headerTextProperty().set("设置失败!");
                            alert.showAndWait();
                            return;
                        }
                        outputXTextField.setText(Double.toString(raster.getResolutionX()));
                        outputYTextField.setText(Double.toString(raster.getResolutionY()));
                        raster.close();
                    }
                });
                contextMenu.getItems().addAll(removeMenuItem,addRangeItem);
                row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu)null).otherwise(contextMenu));
                // 左键双击
                row.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if(event.getClickCount()==2){
                            String rasterAliasName = row.getItem().getAliasName();
                            int currentCarePosotion = expressionTextArea.getCaretPosition();
                            expressionTextArea.insertText(currentCarePosotion, rasterAliasName);
                        }
                    }
                });
                return row;
            }
        });

        // 公式导入
        importButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                GDBOpenFileDialog openFile = new GDBOpenFileDialog();
                String strFilter = "文本文件" + "(*.txt)|*.txt";
                openFile.setFilter(strFilter);
                Optional<String[]> optional = openFile.showAndWait();
                if (optional!=null && optional.isPresent()){
                    List<String> optionalPath = Arrays.asList(optional.get());
                    try {
                        FileInputStream fis = new FileInputStream(optionalPath.get(0));
                        InputStreamReader isr = new InputStreamReader(fis, "GB2312");
                        BufferedReader br = new BufferedReader(isr);
                        String line = "";
                        while ((line=br.readLine())!=null){
                            expressionTextArea.appendText(line);
                            expressionTextArea.appendText("\n");
                        }
                        br.close();
                        isr.close();
                        fis.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });

        // 公式导出
        exportButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                GDBSaveFileDialog saveFile = new GDBSaveFileDialog();
                String strFilter = "文本文件" + "(*.txt)|*.txt";
                saveFile.setFilter(strFilter);
                Optional<String[]> optional = saveFile.showAndWait();
                if (optional!=null && optional.isPresent()){
                    List<String> optionalPath = Arrays.asList(optional.get());
                    try {
                        FileOutputStream fos = new FileOutputStream(new File(optionalPath.get(0)));
                        OutputStreamWriter osw = new OutputStreamWriter(fos, "GB2312");
                        BufferedWriter bw = new BufferedWriter(osw);
                        String line = expressionTextArea.getText();
                        bw.write(line);
                        bw.close();
                        osw.close();
                        fos.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });

        // 选择路径
        outputPathButtonEdit.setOnButtonClick(new EventHandler<ButtonEditEvent>() {
            @Override
            public void handle(ButtonEditEvent event) {
                GDBSaveFileDialog saveToGDB = new GDBSaveFileDialog();
                saveToGDB.setMultiSelect(false);
                String rasterFilter = "栅格数据集" + "|ras|" + "MAPGISMSI (*.msi)" + "|*.msi|" + "GTiff (*.tif)" + "|*.tif|"  + "HFA (*.img)" + "|*.img";
                saveToGDB.setFilter(rasterFilter);
                Optional<String[]> optional = saveToGDB.showAndWait();
                if(optional!=null && optional.isPresent()){
                    List<String> outputRasterPath = Arrays.asList(optional.get());
                    outputPathButtonEdit.setText(outputRasterPath.get(0));
                }
            }
        });

        // 取消
        cancleButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                cancleButtonUnVisible.disarm();
                cancleButtonUnVisible.fire();
            }
        });

        // 计算
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("栅格计算器");

                if(tableview.getItems().size()<1){
                    alert.headerTextProperty().set("待计算的栅格列表为空!");
                    alert.showAndWait();
                    return;
                }
                if(expressionTextArea.getText().isEmpty()){
                    alert.headerTextProperty().set("栅格运算公式为空!");
                    alert.showAndWait();
                    return;
                }
                if(outputPathButtonEdit.getText().isEmpty()){
                    alert.headerTextProperty().set("栅格运算输出结果为空!");
                    alert.showAndWait();
                    return;
                }
                if(Double.parseDouble(outputXTextField.getText())<=0 || Double.parseDouble(outputYTextField.getText())<=0){
                    alert.headerTextProperty().set("输出栅格数据分辨率需大于0");
                    alert.showAndWait();
                    return;
                }
                // 栅格运算
                long lErr = -1;
                Integer dataCounts = tableview.getItems().size();
                String expression = expressionTextArea.getText();
                GeoRaster[] geoRasters = new GeoRaster[dataCounts];
                Rect outputRange = new Rect(Double.MAX_VALUE, Double.MAX_VALUE, Double.MIN_VALUE, Double.MIN_VALUE);
                OutputInfo output = new OutputInfo();
                OutputInfoEx outputEx = new OutputInfoEx();
                for(Integer index=0; index<dataCounts; index++){
                    geoRasters[index] = new GeoRaster();
                    if(!geoRasters[index].Open(tableview.getItems().get(index).getRasterName())){
                        alert.headerTextProperty().set("打开栅格数据失败!");
                        alert.showAndWait();
                        return;
                    }
                    if(geoRasters[index].getBandCount()!=geoRasters[0].getBandCount()){
                        alert.headerTextProperty().set("待处理栅格数据波段不一致!");
                        alert.showAndWait();
                        return;
                    }
                    outputRange.setXMin(Math.min(geoRasters[index].getRange().getXMin(), outputRange.getXMin()));
                    outputRange.setXMax(Math.max(geoRasters[index].getRange().getXMax(), outputRange.getXMax()));
                    outputRange.setYMin(Math.min(geoRasters[index].getRange().getYMin(), outputRange.getYMin()));
                    outputRange.setYMax(Math.max(geoRasters[index].getRange().getYMax(), outputRange.getYMax()));
                }
                // 设置输出路径
                output.setPath(outputPathButtonEdit.getText());
                output.setFormat(getRasterFormatByURL(outputPathButtonEdit.getText()));
                // 设置输出扩展信息
                outputEx.setCellSize(new Dot(Double.parseDouble(outputXTextField.getText()), Double.parseDouble(outputYTextField.getText())));
                outputEx.setOrigin(new Dot(geoRasters[0].getRange().getXMin(),geoRasters[0].getRange().getYMax()));
                outputEx.setPixelType(dataTypeStringToRasterDataType(outputRasterTypeComboBox.getSelectionModel().getSelectedItem()));
                //outputEx.setBandCount(geoRasters[0].getBandCount());
                Integer cols = ((Double)Math.ceil((outputRange.getXMax()-outputRange.getXMin())/outputEx.getCellSize().getX())).intValue();
                Integer rows = ((Double)Math.ceil((outputRange.getYMax()-outputRange.getYMin())/outputEx.getCellSize().getY())).intValue();
                outputEx.setWidth(cols);
                outputEx.setHeight(rows);
                outputEx.setNoData(geoRasters[0].getNoData());
                output.setExtraInfo(outputEx);

                lErr = RasterMathOp.rasterCalculator(geoRasters, expression, output, null);
                if(lErr>0){
                    alert.headerTextProperty().set("栅格计算成功!");
                    alert.showAndWait();
                    if(addToMapCheckBox.isSelected()){
                        String savePath = outputPathButtonEdit.getText().replace("\\","\\\\");
                        RasterLayer rasLayer = new RasterLayer();
                        addLayerToDoc(savePath, rasLayer,document);
                    }
                    okButtonUnVisible.disarm();
                    okButtonUnVisible.fire();
                }else{
                    alert.headerTextProperty().set("栅格计算失败!");
                    alert.showAndWait();
                }
            }
        });

    }

    private void initLeftVBox(Document document){
        leftVBox = new VBox();
        leftVBox.setSpacing(5);
        leftVBox.setStyle("-fx-font-size: 8pt;");

        int width1 = 520;
        int height1 = 240;
        // 源数据布局
        TitledPane titledPane1 = new TitledPane();
        titledPane1.setCollapsible(false);
        titledPane1.setText("源数据");
        titledPane1.setPrefSize(width1,height1);
        titledPane1.setMinSize(width1,height1);

        GridPane gridPane1 = new GridPane();
        gridPane1.setPadding(new Insets(10,10,10,10));
        gridPane1.setHgap(5);
        gridPane1.setVgap(5);

        inputLabel = new Label("源数据:");
        inputLabel.setPrefWidth(40);
        gridPane1.add(inputLabel,0,0);
        String rasterFilter = "栅格数据|ras;*.msi;*.tif;*.img";
        inputLayerSelect = new LayerSelectControl(document, rasterFilter);
        inputLayerSelect.setPrefSize(455,25);
        gridPane1.add(inputLayerSelect,1,0);

        data = FXCollections.observableArrayList();
        tableview = new TableView<>(data);
        tableview.setPrefSize(500,165);

        TableColumn<FormulaItem, String> tc_AliasName = new TableColumn<>("变量");
        tc_AliasName.setSortable(false);
        tc_AliasName.setPrefWidth(60);
        tc_AliasName.setMinWidth(60);
        tc_AliasName.setMaxWidth(60);
        tc_AliasName.setCellValueFactory(new PropertyValueFactory<>("aliasName"));
        TableColumn<FormulaItem, String> tc_RasterName = new TableColumn<>("影像文件");
        tc_RasterName.setSortable(false);
        tc_RasterName.setPrefWidth(440);
        tc_RasterName.setMinWidth(440);
        tc_RasterName.setMaxWidth(440);
        tc_RasterName.setCellValueFactory(new PropertyValueFactory<>("rasterName"));
        tableview.getColumns().addAll(tc_AliasName,tc_RasterName);
        gridPane1.add(tableview,0,1,2,1);

        int width2 = 520;
        int height2 = 205;
        // 运算表达式布局
        TitledPane titledPane2 = new TitledPane();
        titledPane2.setCollapsible(false);
        titledPane2.setText("运算表达式");
        titledPane2.setPrefSize(width2,height2);
        titledPane2.setMinSize(width2,height2);

        GridPane gridPane2 = new GridPane();
        gridPane2.setPadding(new Insets(10,10,10,10));
        gridPane2.setHgap(5);
        gridPane2.setVgap(5);

        expressionTextArea = new TextArea();
        expressionTextArea.setWrapText(true);
        expressionTextArea.setEditable(true);
        expressionTextArea.setPrefSize(500,130);
        gridPane2.add(expressionTextArea,0,0,2,1);

        importButton = new Button("导入...");
        importButton.setPrefHeight(25);
        gridPane2.add(importButton,0,1);
        exportButton = new Button("导出...");
        exportButton.setPrefHeight(25);
        gridPane2.add(exportButton,1,1);

        // 栅格数据输出布局
        int width3 = 520;
        int height3 = 160;
        TitledPane titledPane3 = new TitledPane();
        titledPane3.setText("数据输出");
        titledPane3.setCollapsible(false);
        titledPane3.setPrefSize(width3,height3);
        titledPane3.setMinSize(width3,height3);

        GridPane gridPane3 = new GridPane();
        gridPane3.setPadding(new Insets(10,10,10,10));
        gridPane3.setHgap(5);
        gridPane3.setVgap(5);

        outputRasterTypeLabel = new Label("像元类型:");
        outputRasterTypeLabel.setPrefWidth(50);
        gridPane3.add(outputRasterTypeLabel,0,0);
        outputRasterTypeComboBox = new ComboBox<>();
        outputRasterTypeComboBox.setEditable(false);
        outputRasterTypeComboBox.setPrefSize(445,25);
        outputRasterTypeComboBox.getItems().addAll("8位无符号整数","16位无符号整数","16位有符号整数","32位无符号整数","32位有符号整数","32位浮点数","64位浮点数");
        outputRasterTypeComboBox.getSelectionModel().selectFirst();
        gridPane3.add(outputRasterTypeComboBox,1,0);

        outputXLabel = new Label("X分辨率:");
        gridPane3.add(outputXLabel,0,1);
        outputXTextField = new TextField();
        outputXTextField.setPrefSize(445,25);
        gridPane3.add(outputXTextField,1,1);
        outputYLabel = new Label("Y分辨率:");
        gridPane3.add(outputYLabel,0,2);
        outputYTextField = new TextField();
        outputYTextField.setPrefSize(445,25);
        gridPane3.add(outputYTextField,1,2);

        outputPathLabel = new Label("结果栅格:");
        gridPane3.add(outputPathLabel,0,3);
        outputPathButtonEdit = new ButtonEdit();
        outputPathButtonEdit.setPrefSize(445,25);
        gridPane3.add(outputPathButtonEdit,1,3);

        AnchorPane anchorPane1 = new AnchorPane();
        anchorPane1.setPrefSize(520,30);

        addToMapCheckBox = new CheckBox("添加到地图文档");
        addToMapCheckBox.setSelected(true);

        okButton = new Button("计算");
        okButton.setPrefSize(60,30);
        okButton.setLayoutX(365);

        cancleButton = new Button("取消");
        cancleButton.setPrefSize(60,30);
        cancleButton.setLayoutX(435);

        anchorPane1.getChildren().addAll(addToMapCheckBox, okButton, cancleButton);
        titledPane3.setContent(gridPane3);
        titledPane2.setContent(gridPane2);
        titledPane1.setContent(gridPane1);
        leftVBox.getChildren().addAll(titledPane1,titledPane2,titledPane3,anchorPane1);
    }

    private void initRightVBox() {

        VBox rightVBox = new VBox();
        rightVBox.setSpacing(5);

        titledPane = new TitledPane();
        titledPane.setCollapsible(false);
        titledPane.setText("公式编辑器");
        titledPane.setContent(rightVBox);
        titledPane.setStyle("-fx-font-size: 8pt;");

        int width01 = 265;
        int height01 = 134;

        TitledPane titledPane1 = new TitledPane();
        titledPane1.setCollapsible(false);
        titledPane1.setText("变量与数字");
        titledPane1.setPrefSize(width01,height01);
        titledPane1.setMinSize(width01,height01);

        GridPane gridPane1 = new GridPane();
        gridPane1.setPadding(new Insets(10,10,10,10));
        gridPane1.setHgap(5);
        gridPane1.setVgap(5);

        int width1 = 45;
        int height1 = 25;

        btn_I = new Button("I");
        btn_I.setPrefSize(width1,height1);
        btn_I.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData("I");
            }
        });
        gridPane1.add(btn_I,0,0);

        btn_0 = new Button("0");
        btn_0.setPrefSize(width1,height1);
        btn_0.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData("0");
            }
        });
        gridPane1.add(btn_0,0,1);

        btn_1 = new Button("1");
        btn_1.setPrefSize(width1,height1);
        btn_1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData("1");
            }
        });
        gridPane1.add(btn_1,1,1);

        btn_2 = new Button("2");
        btn_2.setPrefSize(width1,height1);
        btn_2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData("2");
            }
        });
        gridPane1.add(btn_2,2,1);

        btn_3 = new Button("3");
        btn_3.setPrefSize(width1,height1);
        btn_3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData("3");
            }
        });
        gridPane1.add(btn_3,3,1);

        btn_4 = new Button("4");
        btn_4.setPrefSize(width1,height1);
        btn_4.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData("4");
            }
        });
        gridPane1.add(btn_4,4,1);

        btn_5 = new Button("5");
        btn_5.setPrefSize(width1,height1);
        btn_5.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData("5");
            }
        });
        gridPane1.add(btn_5,0,2);

        btn_6 = new Button("6");
        btn_6.setPrefSize(width1,height1);
        btn_6.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData("6");
            }
        });
        gridPane1.add(btn_6,1,2);

        btn_7 = new Button("7");
        btn_7.setPrefSize(width1,height1);
        btn_7.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData("7");
            }
        });
        gridPane1.add(btn_7,2,2);

        btn_8 = new Button("8");
        btn_8.setPrefSize(width1,height1);
        btn_8.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData("8");
            }
        });
        gridPane1.add(btn_8,3,2);

        btn_9 = new Button("9");
        btn_9.setPrefSize(width1,height1);
        btn_9.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData("9");
            }
        });
        gridPane1.add(btn_9,4,2);

        // 分割符
        int width02 = 265;
        int height02 = 75;
        TitledPane titledPane2 = new TitledPane();
        titledPane2.setCollapsible(false);
        titledPane2.setText("分隔符");
        titledPane2.setPrefSize(width02,height02);
        titledPane2.setMinSize(width02,height02);

        GridPane gridPane2 = new GridPane();
        gridPane2.setPadding(new Insets(10,10,10,10));
        gridPane2.setHgap(5);
        gridPane2.setVgap(5);

        int width2 = 45;
        int height2 = 25;

        btn_Point = new Button(".");
        btn_Point.setPrefSize(width2,height2);
        btn_Point.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData(".");
            }
        });
        gridPane2.add(btn_Point,0,0);

        btn_LeftBrackets = new Button("(");
        btn_LeftBrackets.setPrefSize(width2,height2);
        btn_LeftBrackets.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData("(");
            }
        });
        gridPane2.add(btn_LeftBrackets,1,0);

        btn_RightBrackets = new Button(")");
        btn_RightBrackets.setPrefSize(width2,height2);
        btn_RightBrackets.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData(")");
            }
        });
        gridPane2.add(btn_RightBrackets,2,0);

        btn_Comma = new Button(",");
        btn_Comma.setPrefSize(width2,height2);
        btn_Comma.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData(",");
            }
        });
        gridPane2.add(btn_Comma,3,0);

        btn_Blank = new Button("空");
        btn_Blank.setPrefSize(width2,height2);
        btn_Blank.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData(" ");
            }
        });
        gridPane2.add(btn_Blank,4,0);

        // 运算符
        int width03 = 265;
        int height03 = 190;
        TitledPane titledPane3 = new TitledPane();
        titledPane3.setCollapsible(false);
        titledPane3.setText("运算符");
        titledPane3.setPrefSize(width03,height03);
        titledPane3.setMinSize(width03,height03);

        GridPane gridPane3 = new GridPane();
        gridPane3.setPadding(new Insets(10,10,10,10));
        gridPane3.setHgap(5);
        gridPane3.setVgap(5);

        int width3 = 57;
        int height3 = 25;

        btn_Plus = new Button("+");
        btn_Plus.setPrefSize(width3,height3);
        btn_Plus.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData("+");
            }
        });
        gridPane3.add(btn_Plus,0,0);

        btn_Minus = new Button("-");
        btn_Minus.setPrefSize(width3,height3);
        btn_Minus.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData("-");
            }
        });
        gridPane3.add(btn_Minus,1,0);

        btn_Multiply = new Button("*");
        btn_Multiply.setPrefSize(width3,height3);
        btn_Multiply.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData("*");
            }
        });
        gridPane3.add(btn_Multiply,2,0);

        btn_Divide = new Button("/");
        btn_Divide.setPrefSize(width3,height3);
        btn_Divide.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData("/");
            }
        });
        gridPane3.add(btn_Divide,3,0);

        btn_Greater = new Button(">");
        btn_Greater.setPrefSize(width3,height3);
        btn_Greater.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData(">");
            }
        });
        gridPane3.add(btn_Greater,0,1);

        btn_Less = new Button("<");
        btn_Less.setPrefSize(width3,height3);
        btn_Less.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData("<");
            }
        });
        gridPane3.add(btn_Less,1,1);

        btn_Equal = new Button("=");
        btn_Equal.setPrefSize(width3,height3);
        btn_Equal.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData("=");
            }
        });
        gridPane3.add(btn_Equal,2,1);

        btn_NotEqual = new Button("<>");
        btn_NotEqual.setPrefSize(width3,height3);
        btn_NotEqual.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData("<>");
            }
        });
        gridPane3.add(btn_NotEqual,3,1);

        btn_GreatEqual = new Button(">=");
        btn_GreatEqual.setPrefSize(width3,height3);
        btn_GreatEqual.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData(">=");
            }
        });
        gridPane3.add(btn_GreatEqual,0,2);

        btn_LessEqual = new Button("<=");
        btn_LessEqual.setPrefSize(width3,height3);
        btn_LessEqual.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData("<=");
            }
        });
        gridPane3.add(btn_LessEqual,1,2);

        btn_Mod = new Button("mod");
        btn_Mod.setPrefSize(width3,height3);
        btn_Mod.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData("mod");
            }
        });
        gridPane3.add(btn_Mod,2,2);

        btn_Div = new Button("div");
        btn_Div.setPrefSize(width3,height3);
        btn_Div.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData("div");
            }
        });
        gridPane3.add(btn_Div,3,2);

        btn_LogicNot = new Button("not");
        btn_LogicNot.setPrefSize(width3,height3);
        btn_LogicNot.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData("not");
            }
        });
        gridPane3.add(btn_LogicNot,0,3);

        btn_LogicAnd = new Button("and");
        btn_LogicAnd.setPrefSize(width3,height3);
        btn_LogicAnd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData("and");
            }
        });
        gridPane3.add(btn_LogicAnd,1,3);

        btn_LogicXor = new Button("xor");
        btn_LogicXor.setPrefSize(width3,height3);
        btn_LogicXor.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData("xor");
            }
        });
        gridPane3.add(btn_LogicXor,2,3);

        btn_LogicOr = new Button("or");
        btn_LogicOr.setPrefSize(width3,height3);
        btn_LogicOr.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData("or");
            }
        });
        gridPane3.add(btn_LogicOr,3,3);

        btn_BitWizeAnd = new Button("&");
        btn_BitWizeAnd.setPrefSize(width3,height3);
        btn_BitWizeAnd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData("&&");
            }
        });
        gridPane3.add(btn_BitWizeAnd,0,4);

        btn_BitWizeOr = new Button("|");
        btn_BitWizeOr.setPrefSize(width3,height3);
        btn_BitWizeOr.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData("|");
            }
        });
        gridPane3.add(btn_BitWizeOr,1,4);

        btn_BitWizeNot = new Button("~");
        btn_BitWizeNot.setPrefSize(width3,height3);
        btn_BitWizeNot.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                insetData("~");
            }
        });
        gridPane3.add(btn_BitWizeNot,2,4);

        btn_Clear = new Button("清除");
        btn_Clear.setPrefSize(width3,height3);
        btn_Clear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                expressionTextArea.clear();
            }
        });
        gridPane3.add(btn_Clear,3,4);

        //常用函数
        int width04 = 265;
        int height04 = 160;
        TitledPane titledPane4 = new TitledPane();
        titledPane4.setCollapsible(false);
        titledPane4.setText("常用函数");
        titledPane4.setPrefSize(width04,height04);
        titledPane4.setMinSize(width04,height04);

        GridPane gridPane4 = new GridPane();
        gridPane4.setPadding(new Insets(10,10,10,10));
        gridPane4.setHgap(5);
        gridPane4.setVgap(5);

        int width4 = 165;
        int height4 = 25;

        mathLabel = new Label("算数函数:");
        gridPane4.add(mathLabel,0,0);

        mathComboBox = new ComboBox<String>();
        mathComboBox.setEditable(false);
        mathComboBox.setPrefSize(width4,height4);
        mathComboBox.getItems().addAll("abs()","max(,,)","min(,,)","floor()","ceil()");
        mathComboBox.getSelectionModel().selectFirst();
        mathComboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                ListCell<String> cell = new ListCell<String>(){
                    @Override
                    public void updateItem(String item, boolean empty)
                    {
                        super.updateItem(item,empty);
                        if(!empty) {
                            setText(item);
                        }
                    }
                };
                cell.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        mathComboBox.setValue(null);
                        mathComboBox.getSelectionModel().select(cell.getItem());
                        event.consume();
                    }
                });
                return cell;
            }
        });
        mathComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue!=null)
                {
                    insetData(newValue);
                }
            }
        });
        gridPane4.add(mathComboBox,1,0);

        trigonometricLabel = new Label("三角函数:");
        //trigonometricLabel.setPrefWidth(85);
        gridPane4.add(trigonometricLabel,0,1);

        trigonometricComboBox = new ComboBox<String>();
        trigonometricComboBox.setEditable(false);
        trigonometricComboBox.setPrefSize(width4,height4);
        trigonometricComboBox.getItems().addAll("sin()","cos()","tan()","acos()","asin()","atan()");
        trigonometricComboBox.setValue("sin()");
        trigonometricComboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                ListCell<String> cell = new ListCell<String>(){
                    @Override
                    public void updateItem(String item, boolean empty){
                        super.updateItem(item,empty);
                        if(!empty){
                            setText(item);
                        }
                    }
                };
                cell.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        trigonometricComboBox.setValue(null);
                        trigonometricComboBox.getSelectionModel().select(cell.getItem());
                    }
                });
                return cell;
            }
        });
        trigonometricComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue!=null)
                {
                    insetData(newValue);
                }
            }
        });
        gridPane4.add(trigonometricComboBox,1,1);

        exponentialAndLogarithmicLabel = new Label("指数/对数函数:");
        exponentialAndLogarithmicLabel.setPrefWidth(75);
        gridPane4.add(exponentialAndLogarithmicLabel,0,2);

        exponentialAndLogarithmicComboBox = new ComboBox<String>();
        exponentialAndLogarithmicComboBox.setEditable(false);
        exponentialAndLogarithmicComboBox.setPrefSize(width4,height4);
        exponentialAndLogarithmicComboBox.getItems().addAll("power(,)","sqrt()","exp()","log()","lg()");
        exponentialAndLogarithmicComboBox.setValue("power(,)");
        exponentialAndLogarithmicComboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                ListCell<String> cell = new ListCell<String>(){
                    @Override
                    public void updateItem(String item, boolean empty)
                    {
                        super.updateItem(item,empty);
                        if(!empty){
                            setText(item);
                        }
                    }
                };
                cell.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        exponentialAndLogarithmicComboBox.setValue(null);
                        exponentialAndLogarithmicComboBox.getSelectionModel().select(cell.getItem());
                    }
                });
                return cell;
            }
        });
        exponentialAndLogarithmicComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue!=null){
                    insetData(newValue);
                }
            }
        });
        gridPane4.add(exponentialAndLogarithmicComboBox,1,2);

        elseLabel = new Label("其他函数:");
        //elseLabel.setPrefWidth(85);
        gridPane4.add(elseLabel,0,3);

        elseComboBox = new ComboBox<String>();
        elseComboBox.setEditable(false);
        elseComboBox.setPrefSize(width4,height4);
        elseComboBox.getItems().addAll("if(,,)");
        elseComboBox.setValue("if(,,)");
        elseComboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                ListCell<String> cell = new ListCell<String>(){
                    @Override
                    public void updateItem(String item, boolean empty)
                    {
                        super.updateItem(item,empty);
                        if(!empty){
                            setText(item);
                        }
                    }
                };
                cell.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        elseComboBox.setValue(null);
                        elseComboBox.getSelectionModel().select(cell.getItem());
                    }
                });
                return cell;
            }
        });
        elseComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue!=null){
                    insetData(newValue);
                }
            }
        });
        gridPane4.add(elseComboBox,1,3);

        titledPane4.setContent(gridPane4);
        titledPane3.setContent(gridPane3);
        titledPane2.setContent(gridPane2);
        titledPane1.setContent(gridPane1);
        rightVBox.getChildren().addAll(titledPane1,titledPane2,titledPane3,titledPane4);
    }

    // 插入字符
    private void insetData(String value){
        int currentCaretPosition = expressionTextArea.getCaretPosition();
        expressionTextArea.insertText(currentCaretPosition, value);
    }

    // 根据中文字符串获取栅格数据像元类型
    private PixelType dataTypeStringToRasterDataType(String dataTypeStr){
        PixelType rasterDataType = PixelType.Unknown;
        switch (dataTypeStr){
            case "8位无符号整数":
                rasterDataType = PixelType.Byte;
                break;
            case "16位无符号整数":
                rasterDataType = PixelType.UInt16;
                break;
            case "32位无符号整数":
                rasterDataType = PixelType.UInt32;
                break;
            case "32位有符号整数":
                rasterDataType = PixelType.Int32;
                break;
            case "32位浮点数":
                rasterDataType = PixelType.Float32;
                break;
            case "64位浮点数":
                rasterDataType = PixelType.Float64;
                break;
        }
        return rasterDataType;
    }

    // 根据URL获得输出栅格的格式
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

    // 添加到地图文档
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

//    private Map getMapByLayer(DocumentItem item)
//    {
//        if(item==null){
//            return null;
//        }
//        DocumentItem itemParent = item.getParent();
//        if(itemParent.getDocumentItemType().equals(DocumentItemType.Map)){
//            return  (Map)itemParent;
//        }else{
//            return getMapByLayer(itemParent);
//        }
//    }
}
