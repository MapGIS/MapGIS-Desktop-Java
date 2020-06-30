package com.zondy.mapgis.rastereditor.dialogs;

import com.zondy.mapgis.analysis.rasteranalysis.*;
import com.zondy.mapgis.att.Field.FieldType;
import com.zondy.mapgis.controls.common.ButtonEdit;
import com.zondy.mapgis.controls.common.ButtonEditEvent;
import com.zondy.mapgis.controls.wizard.Direction;
import com.zondy.mapgis.controls.wizard.Wizard;
import com.zondy.mapgis.controls.wizard.WizardEvent;
import com.zondy.mapgis.controls.wizard.WizardPage;
import com.zondy.mapgis.filedialog.GDBOpenFileDialog;
import com.zondy.mapgis.filedialog.GDBSaveFileDialog;
import com.zondy.mapgis.geodatabase.SFeatureCls;
import com.zondy.mapgis.geodatabase.raster.RasterAccess;
import com.zondy.mapgis.geodatabase.raster.RasterDataset;
import com.zondy.mapgis.geodatabase.raster.RasterFormat;
import com.zondy.mapgis.geometry.*;
import com.zondy.mapgis.map.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 * 插值分析(离散数据网格化)
 * @author yuhan
 * @version 1.0.0
 */
public class RasterInterpolationDialog extends Dialog {

    //
    private Document    m_doc = null;                 // 当前地图文档
    private boolean     m_DataChanged = false;        // 是否切换输入数据
    private Dots3D      m_dots3D = null;              // 点集
    private DemInfo     m_demInfo = null;             // 高程参数
    private SearchProperty   m_SearchProperty = null;  // 搜索参数
    private DistInsProperty  m_DistInsProperty = null; // 距离幂反比插值参数
    private KringInsProperty m_KringInsProperty = null;// 克立金插值参数
    private MBSplineParam    m_MBSplineParam = null;   //多层B-Spline插值参数
    private double m_Oxmin = 0;
    private double m_Oxmax = 0;
    private double m_Oymin = 0;
    private double m_Oymax = 0;
    //
    Wizard wizard = null;
    WizardPage wizardPage1 = null;
    WizardPage wizardPage2 = null;
    //
    private Label dataTypeLabel = null;
    private ComboBox<String> dataTypeComboBox = null;
    private Label inputDataLabel = null;
    private ButtonEdit inputDataButtonEdit = null;
    private Label  xValueLabel = null;
    private ComboBox<String> xValueComboBox = null;
    private Label yValueLabel = null;
    private ComboBox<String> yValueComboBox = null;
    private Label zValueLabel = null;
    private ComboBox<String> zValueComboBox = null;
    //
    private Label xDirectionLabel = null;
    private Label yDirectionLabel = null;
    private Label zDirectionLabel = null;
    private Label startLabel = null;
    private TextField startXTextField = null;
    private TextField startYTextField = null;
    private TextField startZTextField = null;
    private Label endLabel = null;
    private TextField endXTextField = null;
    private TextField endYTextField = null;
    private TextField endZTextField = null;
    private Label netStepLabel = null;
    private TextField netStepXTextField = null;
    private TextField netStepYTextField = null;
    private Label netLineNumLabel = null;
    private TextField netLineNumXTextField = null;
    private TextField netLineNumYTextField = null;
    private Button netRangeButton = null;
    //
    private Label netMethodLabel = null;
    private ComboBox<String> netMethodComboBox = null;
    private Button selectButton = null;
    private Button searchButton = null;
    private Label outputLabel = null;
    private ButtonEdit outputButtonEdit = null;
    private CheckBox outputAddToMapCheckBox = null;

    public RasterInterpolationDialog(Document document) {
        setTitle("离散数据网格化");

        initializeComponentPage1();
        initializeComponentPage2();

        m_doc = document;

        wizard = new Wizard(this,wizardPage1,wizardPage2);
        wizard.setPrefSize(550,420);
        wizard.setMinSize(550,420);
        setDialogPane(wizard);


        // 页面切换
        wizardPage1.setOnPageValidating(new EventHandler<WizardEvent>() {
            @Override
            public void handle(WizardEvent event) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("离散数据网格化");
                if(event.getDirection()== Direction.Forward){
                    String msgErr = "";
                    if(inputDataButtonEdit.getText().isEmpty()){
                        msgErr = "初始化失败,请重新添加数据!";
                        event.setValid(msgErr.equals(""));
                        if(!event.isValid()){
                            alert.headerTextProperty().set(msgErr);
                            alert.showAndWait();
                            return;
                        }
                    }
                    if(m_DataChanged){
                        if(dataTypeComboBox.getSelectionModel().getSelectedIndex()==0){
                            // 获取点集
                            SFeatureCls Sfcls = new SFeatureCls();
                            Sfcls = new SFeatureCls();
                            String tmpSfclsUrl = inputDataButtonEdit.getText();
                            tmpSfclsUrl = tmpSfclsUrl.toLowerCase().startsWith("gdbp://") || tmpSfclsUrl.toLowerCase().startsWith("file:///") ? tmpSfclsUrl : "file:///" + tmpSfclsUrl;
                            if(Sfcls.openByURL(tmpSfclsUrl)>0){
                                String x = xValueComboBox.getValue().equals("X图形坐标")?"x":xValueComboBox.getValue();
                                String y = yValueComboBox.getValue().equals("Y图形坐标")?"y":yValueComboBox.getValue();
                                String z = zValueComboBox.getValue().equals("Z图形坐标")?"z":zValueComboBox.getValue();
                                m_dots3D = RasterInterpolationOp.getDiscreteDataExByCls(Sfcls,x,y,z);
                                Sfcls.close();
                            }else{
                                msgErr = "初始化失败,请重新添加数据!";
                                event.setValid(msgErr.equals(""));
                                if(!event.isValid()){
                                    alert.headerTextProperty().set(msgErr);
                                    alert.showAndWait();
                                }
                            }
                        }else{
                            String txtFile = inputDataButtonEdit.getText();
                            int xCol = xValueComboBox.getSelectionModel().getSelectedIndex()+1;
                            int yCol = yValueComboBox.getSelectionModel().getSelectedIndex()+1;
                            int zCol = zValueComboBox.getSelectionModel().getSelectedIndex()+1;
                            m_dots3D = RasterInterpolationOp.getDiscreteDataExByTxt(txtFile,xCol,yCol,zCol);
                        }
                        // 更新信息
                        if((m_dots3D!=null && m_dots3D.size()>0)){
                            GetDefaultParam();
                            m_Oxmin = m_demInfo.getXMin();
                            m_Oxmax = m_demInfo.getXMax();
                            m_Oymin = m_demInfo.getYMin();
                            m_Oymax = m_demInfo.getYMax();
                            startXTextField.setText(Double.toString(m_demInfo.getXMin()));
                            endXTextField.setText(Double.toString(m_demInfo.getXMax()));
                            netStepXTextField.setText(Double.toString((m_demInfo.getXMax()-m_demInfo.getXMin())/m_demInfo.getWidth()));
                            netLineNumXTextField.setText(Integer.toString(m_demInfo.getWidth()));
                            startYTextField.setText(Double.toString(m_demInfo.getYMin()));
                            endYTextField.setText(Double.toString(m_demInfo.getYMax()));
                            netStepYTextField.setText(Double.toString((m_demInfo.getYMax()-m_demInfo.getYMin())/m_demInfo.getHeight()));
                            netLineNumYTextField.setText(Integer.toString(m_demInfo.getHeight()));
                            startZTextField.setText(Double.toString(m_demInfo.getZMin()));
                            endZTextField.setText(Double.toString(m_demInfo.getZMax()));
                        }else{
                            msgErr = "初始化失败,请重新添加数据!";
                            event.setValid(msgErr.equals(""));
                            if(!event.isValid()){
                                alert.headerTextProperty().set(msgErr);
                                alert.showAndWait();
                                dataReset();
                            }
                        }
                    }
                }else{
                    m_DataChanged = false;
                }
            }
        });

        wizard.setOnFinish(new EventHandler<WizardEvent>() {
            @Override
            public void handle(WizardEvent event) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("离散数据网格化)");

                String outputUrl = outputButtonEdit.getText();
                if (outputUrl==null || outputUrl.isEmpty())
                {
                    alert.headerTextProperty().set("请选择输出路径!");
                    alert.showAndWait();
                    return;
                }
                if (m_dots3D!=null || m_dots3D.size()!=0)
                {
                    if (m_Oxmin>m_demInfo.getXMax() || m_Oxmax<m_demInfo.getXMin() || m_Oymin>m_demInfo.getYMax() || m_Oymax<m_demInfo.getYMin())
                    {
                        alert.headerTextProperty().set("输出范围与原始范围没有相交,请修改!");
                        alert.showAndWait();
                        return;
                    }
                    long lErr = -1;
                    // 格网分辨率
                    double xStep = Double.parseDouble(netStepXTextField.getText());
                    double yStep = Double.parseDouble(netStepYTextField.getText());
                    Dot cellSize = new Dot(xStep,yStep);
                    // 输出路径
                    OutputInfo output = new OutputInfo();
                    output.setPath(outputUrl);
                    output.setFormat(getRasterFormatByURL(outputUrl));
                    // 扩展信息
                    OutputInfoEx outputEx = new OutputInfoEx();
                    // 行列值
                    outputEx.setWidth(m_demInfo.getWidth());
                    outputEx.setHeight(m_demInfo.getHeight());
                    // 原点
                    outputEx.setOrigin(new Dot(m_demInfo.getXMin(),m_demInfo.getYMax()));
                    // 分辨率
                    outputEx.setCellSize(cellSize);
                    //outputEx.setBandCount(1);
                    output.setExtraInfo(outputEx);

                    if (netMethodComboBox.getSelectionModel().getSelectedIndex()==0)
                    {
                        lErr = RasterInterpolationOp.kring(m_dots3D,m_SearchProperty,m_KringInsProperty,cellSize,output,null);
                    }
                    else if (netMethodComboBox.getSelectionModel().getSelectedIndex()==1)
                    {
                        lErr = RasterInterpolationOp.distIns(m_dots3D,m_SearchProperty,m_DistInsProperty,cellSize,output,null);
                    }
                    else if (netMethodComboBox.getSelectionModel().getSelectedIndex()==2)
                    {
                        alert.headerTextProperty().set("多层B样条未实现!");
                        alert.showAndWait();
                        return;
                    }
                    else if (netMethodComboBox.getSelectionModel().getSelectedIndex()==3)
                    {
                        lErr = RasterInterpolationOp.distInsMQS(m_dots3D,m_SearchProperty,cellSize,output,null);
                    }
                    else if (netMethodComboBox.getSelectionModel().getSelectedIndex()==4)
                    {
                        alert.headerTextProperty().set("稠密数据中值未实现!");
                        alert.showAndWait();
                        return;
                    }
                    else if (netMethodComboBox.getSelectionModel().getSelectedIndex()==5)
                    {
                        alert.headerTextProperty().set("稠密数据高斯距离加权未实现!");
                        alert.showAndWait();
                        return;
                    }
                    if (lErr==1) {
                        alert.headerTextProperty().set("处理成功");
                        alert.showAndWait();
                        if (outputAddToMapCheckBox.isSelected())
                        {
                            // 添加重采样结果到地图文档
                            String savePath = outputButtonEdit.getText();
                            RasterLayer rasterLayer = new RasterLayer();
                            addLayerToDoc(savePath,rasterLayer,document);
                        }
                    }else{
                        alert.headerTextProperty().set("处理失败");
                        alert.showAndWait();
                    }
                }
            }
        });

    }

    private void initializeComponentPage1(){
        GridPane gridPane1 = new GridPane();
        gridPane1.setPadding(new Insets(10,10,10,10));
        gridPane1.setHgap(5);
        gridPane1.setVgap(10);
        gridPane1.setStyle("-fx-font-size: 10pt;");

        dataTypeLabel = new Label("数据类型:");
        gridPane1.add(dataTypeLabel,0,0);
        dataTypeComboBox = new ComboBox<>();
        dataTypeComboBox.setPrefWidth(430);
        dataTypeComboBox.setEditable(false);
        dataTypeComboBox.getItems().addAll("简单要素类","文本");
        dataTypeComboBox.getSelectionModel().selectFirst();
        dataTypeComboBox.selectionModelProperty().addListener(new ChangeListener<SingleSelectionModel<String>>() {
            @Override
            public void changed(ObservableValue<? extends SingleSelectionModel<String>> observable, SingleSelectionModel<String> oldValue, SingleSelectionModel<String> newValue) {
                dataReset();
            }
        });
        gridPane1.add(dataTypeComboBox,1,0);

        inputDataLabel = new Label("输入数据:");
        gridPane1.add(inputDataLabel,0,1);
        inputDataButtonEdit = new ButtonEdit();
        inputDataButtonEdit.setOnButtonClick(new EventHandler<ButtonEditEvent>() {
            @Override
            public void handle(ButtonEditEvent event) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("离散数据网格化");

                int dataTypeIndex = dataTypeComboBox.getSelectionModel().getSelectedIndex();
                String dataFilter = dataTypeIndex==0 ? "点、线简单要素类|sfclsp;sfclsl" : "文本文件|*.txt";
                GDBOpenFileDialog gdbOpenDlg = new GDBOpenFileDialog();
                gdbOpenDlg.setFilter(dataFilter);
                gdbOpenDlg.setMultiSelect(false);
                Optional<String[]> optionalSfcls = gdbOpenDlg.showAndWait();
                if(optionalSfcls!=null && optionalSfcls.isPresent()){
                    List<String> sfclsUrlList = Arrays.asList(optionalSfcls.get());
                    if(dataTypeIndex==0){
                        SFeatureCls sfcls = new SFeatureCls();
                        if(sfcls.openByURL(sfclsUrlList.get(0))>0){
                            dataReset();
                            inputDataButtonEdit.setText(sfclsUrlList.get(0));
                            xValueComboBox.getItems().add("X图形坐标");
                            yValueComboBox.getItems().add("Y图形坐标");
                            zValueComboBox.getItems().add("Z图形坐标");
                            for(int i=0; i<sfcls.getFields().getFieldCount();i++){
                                if(sfcls.getFields().getField((short) i).getFieldType().compareTo(FieldType.fldShort)>=0 &&
                                        sfcls.getFields().getField((short)i).getFieldType().compareTo(FieldType.fldDouble)<=0){
                                    xValueComboBox.getItems().add(sfcls.getFields().getField((short)i).getFieldName());
                                    yValueComboBox.getItems().add(sfcls.getFields().getField((short)i).getFieldName());
                                    zValueComboBox.getItems().add(sfcls.getFields().getField((short)i).getFieldName());
                                }
                            }
                            sfcls.close();
                            xValueComboBox.getSelectionModel().selectFirst();
                            yValueComboBox.getSelectionModel().selectFirst();
                            zValueComboBox.getSelectionModel().selectFirst();
                        }else{
                            alert.headerTextProperty().set("简单要素类打开失败!");
                            alert.showAndWait();
                        }
                    }else{
                        boolean flag = true;
                        String line = "";
                        File txtPntFile = new File(sfclsUrlList.get(0));
                        try {
                            BufferedReader br = new BufferedReader(new FileReader(txtPntFile));
                            while (br.readLine()!=null && flag){
                                line = br.readLine();
                                if(line.equals("")){
                                    continue;
                                }
                                flag = false;
                            }
                            br.close();
                            line = line.replace(","," ");
                            line = line.replace("\t"," ");
                            String[] strs = line.split(" ");
                            if(strs.length>=3){
                                dataReset();
                                for(int i=0,j=0;i<strs.length;i++){
                                    if(!strs[i].equals("")){
                                        j++;
                                        String msg = "第"+j+"列数据";
                                        xValueComboBox.getItems().add(msg);
                                        yValueComboBox.getItems().add(msg);
                                        zValueComboBox.getItems().add(msg);
                                    }
                                }
                                inputDataButtonEdit.setText(sfclsUrlList.get(0));
                                xValueComboBox.getSelectionModel().select(0);
                                yValueComboBox.getSelectionModel().select(1);
                                zValueComboBox.getSelectionModel().select(2);
                            }else{
                                alert.headerTextProperty().set("请检查输入文本,列数要大于等于3!");
                                alert.showAndWait();
                            }
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        gridPane1.add(inputDataButtonEdit,1,1);

        xValueLabel = new Label("X值:");
        gridPane1.add(xValueLabel,0,2);
        xValueComboBox = new ComboBox<>();
        xValueComboBox.setPrefWidth(430);
        xValueComboBox.setEditable(false);
        xValueComboBox.selectionModelProperty().addListener(new ChangeListener<SingleSelectionModel<String>>() {
            @Override
            public void changed(ObservableValue<? extends SingleSelectionModel<String>> observable, SingleSelectionModel<String> oldValue, SingleSelectionModel<String> newValue) {
                m_DataChanged = true;
            }
        });
        gridPane1.add(xValueComboBox,1,2);

        yValueLabel = new Label("Y值:");
        gridPane1.add(yValueLabel,0,3);
        yValueComboBox = new ComboBox<>();
        yValueComboBox.setPrefWidth(430);
        yValueComboBox.setEditable(false);
        yValueComboBox.selectionModelProperty().addListener(new ChangeListener<SingleSelectionModel<String>>() {
            @Override
            public void changed(ObservableValue<? extends SingleSelectionModel<String>> observable, SingleSelectionModel<String> oldValue, SingleSelectionModel<String> newValue) {
                m_DataChanged = true;
            }
        });
        gridPane1.add(yValueComboBox,1,3);

        zValueLabel = new Label("Z值:");
        gridPane1.add(zValueLabel,0,4);
        zValueComboBox = new ComboBox<>();
        zValueComboBox.setPrefWidth(430);
        zValueComboBox.setEditable(false);
        zValueComboBox.selectionModelProperty().addListener(new ChangeListener<SingleSelectionModel<String>>() {
            @Override
            public void changed(ObservableValue<? extends SingleSelectionModel<String>> observable, SingleSelectionModel<String> oldValue, SingleSelectionModel<String> newValue) {
                m_DataChanged = true;
            }
        });
        gridPane1.add(zValueComboBox,1,4);

        wizardPage1 = new WizardPage(gridPane1,"输入设置");
    }

    private void initializeComponentPage2(){
        VBox vBox1 = new VBox();
        vBox1.setSpacing(5);
        vBox1.setStyle("-fx-font-size: 10pt;");

        GridPane gridPane1 = new GridPane();
        gridPane1.setPadding(new Insets(10,10,10,10));
        gridPane1.setHgap(5);
        gridPane1.setVgap(7);

        xDirectionLabel = new Label("X方向:");
        xDirectionLabel.setMinWidth(65);
        xDirectionLabel.setPrefWidth(65);
        gridPane1.add(xDirectionLabel,0,1);
        yDirectionLabel = new Label("Y方向:");
        gridPane1.add(yDirectionLabel,0,2);
        zDirectionLabel = new Label("Z方向:");
        gridPane1.add(zDirectionLabel,0,3);

        startLabel = new Label("起点坐标");
        gridPane1.add(startLabel,1,0);
        startXTextField = new TextField();
        startXTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                m_demInfo.setXMin(Double.parseDouble(startXTextField.getText()));
                if(m_demInfo.getWidth()!=0){
                    netStepXTextField.setText(Double.toString((m_demInfo.getXMax()-m_demInfo.getXMin())/m_demInfo.getWidth()));
                }
            }
        });
        gridPane1.add(startXTextField,1,1);
        startYTextField = new TextField();
        startYTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                m_demInfo.setYMin(Double.parseDouble(startYTextField.getText()));
                if(m_demInfo.getHeight()!=0){
                    netStepYTextField.setText(Double.toString((m_demInfo.getYMax()-m_demInfo.getYMin())/m_demInfo.getHeight()));
                }
            }
        });
        gridPane1.add(startYTextField,1,2);
        startZTextField = new TextField();
        startZTextField.setEditable(false);
        gridPane1.add(startZTextField,1,3);

        endLabel = new Label("终点坐标");
        gridPane1.add(endLabel,2,0);
        endXTextField = new TextField();
        endXTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                m_demInfo.setXMax(Double.parseDouble(endXTextField.getText()));
                if(m_demInfo.getWidth()!=0){
                    netStepXTextField.setText(Double.toString((m_demInfo.getXMax()-m_demInfo.getXMin())/m_demInfo.getWidth()));
                }
            }
        });
        gridPane1.add(endXTextField,2,1);
        endYTextField = new TextField();
        endYTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                m_demInfo.setYMax(Double.parseDouble(endYTextField.getText()));
                if(m_demInfo.getHeight()!=0){
                    netStepYTextField.setText(Double.toString((m_demInfo.getYMax()-m_demInfo.getYMin())/m_demInfo.getHeight()));
                }
            }
        });
        gridPane1.add(endYTextField,2,2);
        endZTextField = new TextField();
        endZTextField.setEditable(false);
        gridPane1.add(endZTextField,2,3);

        netStepLabel = new Label("网格间距");
        gridPane1.add(netStepLabel,3,0);
        netStepXTextField = new TextField();
        netStepXTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                double netStepX = Double.parseDouble(netStepXTextField.getText());
                if(netStepX!=0){
                    m_demInfo.setWidth((int)Math.ceil((m_demInfo.getXMax()-m_demInfo.getXMin())/netStepX));
                    netLineNumXTextField.setText(Integer.toString(m_demInfo.getWidth()));
                }
            }
        });
        gridPane1.add(netStepXTextField,3,1);
        netStepYTextField = new TextField();
        netStepYTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                double netStepY = Double.parseDouble(netStepYTextField.getText());
                if(netStepY!=0){
                    m_demInfo.setHeight((int)Math.ceil((m_demInfo.getYMax()-m_demInfo.getYMin())/netStepY));
                    netLineNumXTextField.setText(Integer.toString(m_demInfo.getHeight()));
                }
            }
        });
        gridPane1.add(netStepYTextField,3,2);
        netRangeButton = new Button("网格化范围(R)");
        netRangeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("离散数据网格化");

                GDBOpenFileDialog openFile = new GDBOpenFileDialog();
                openFile.setFilter("简单要素类,栅格数据|sfcls;ras;*.msi");
                openFile.setMultiSelect(false);
                Optional<String[]> optionalOpen = openFile.showAndWait();
                if(optionalOpen!=null && optionalOpen.isPresent()){
                    List<String> urlLst = Arrays.asList(optionalOpen.get());
                    String url = urlLst.get(0);
                    if(url.contains("/sfcls/")){
                        SFeatureCls sfcls = new SFeatureCls();
                        if(sfcls.openByURL(url)>0){
                            Rect rc = sfcls.getRange();
                            if(rc==null){
                                alert.headerTextProperty().set("数据范围为空,请重新选择!");
                                alert.showAndWait();
                                return;
                            }else{
                               if(m_Oxmin>rc.getXMax() || m_Oxmax<rc.getXMin() || m_Oymin>rc.getYMax() || m_Oymax<rc.getYMin()){
                                   alert.headerTextProperty().set("所选数据范围与原始数据范围没有相交,请重新选择!");
                               }else{
                                   m_demInfo.setXMin(rc.getXMin());
                                   m_demInfo.setXMax(rc.getXMax());
                                   m_demInfo.setYMin(rc.getYMin());
                                   m_demInfo.setYMax(rc.getYMax());
                                   startXTextField.setText(Double.toString(m_demInfo.getXMin()));
                                   endXTextField.setText(Double.toString(m_demInfo.getXMax()));
                                   startYTextField.setText(Double.toString(m_demInfo.getYMin()));
                                   endYTextField.setText(Double.toString(m_demInfo.getYMax()));
                               }
                            }
                            sfcls.close();
                        }else {
                            alert.headerTextProperty().set("打开数据失败,请重新选择!");
                            alert.showAndWait();
                        }
                    }else{
                        RasterDataset rasterDataSet = new RasterDataset();
                        long lErr = -1;
                        lErr = rasterDataSet.open(url, RasterAccess.Read);
                        if(lErr>0){
                            Rect rc = rasterDataSet.getRange();
                            if(rc!=null){
                                alert.headerTextProperty().set("数据范围为空,请重新选择!");
                                alert.showAndWait();
                                return;
                            }else{
                                if(m_Oxmin>rc.getXMax() || m_Oxmax>rc.getXMin() || m_Oymin>rc.getYMax() || m_Oymax<rc.getYMin()){
                                    alert.headerTextProperty().set("所选数据范围与原始数据范围没有相交,请重新选择!");
                                    alert.showAndWait();
                                }else{
                                    m_demInfo.setXMin(rc.getXMin());
                                    m_demInfo.setXMax(rc.getXMax());
                                    m_demInfo.setYMin(rc.getYMin());
                                    m_demInfo.setYMax(rc.getYMax());
                                    startXTextField.setText(Double.toString(m_demInfo.getXMin()));
                                    endXTextField.setText(Double.toString(m_demInfo.getXMax()));
                                    startYTextField.setText(Double.toString(m_demInfo.getYMin()));
                                    endYTextField.setText(Double.toString(m_demInfo.getYMax()));
                                }
                            }
                            rasterDataSet.close();
                        }else{
                            alert.headerTextProperty().set("打开数据失败,请重新选择!");
                            alert.showAndWait();
                        }
                    }
                }
            }
        });
        gridPane1.add(netRangeButton,3,3,2,1);

        netLineNumLabel = new Label("网格线数");
        gridPane1.add(netLineNumLabel,4,0);
        netLineNumXTextField = new TextField();
        netLineNumXTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                m_demInfo.setWidth(Integer.parseInt(netLineNumXTextField.getText()));
                if(m_demInfo.getWidth()!=0){
                    netStepXTextField.setText(Double.toString((m_demInfo.getXMax()-m_demInfo.getXMin())/m_demInfo.getWidth()));
                }
            }
        });
        gridPane1.add(netLineNumXTextField,4,1);
        netLineNumYTextField = new TextField();
        netLineNumYTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                m_demInfo.setHeight(Integer.parseInt(netLineNumYTextField.getText()));
                if(m_demInfo.getHeight()!=0){
                    netStepYTextField.setText(Double.toString((m_demInfo.getYMax()-m_demInfo.getYMin())/m_demInfo.getHeight()));
                }
            }
        });
        gridPane1.add(netLineNumYTextField,4,2);

        GridPane gridPane2 = new GridPane();
        gridPane2.setPadding(new Insets(10,10,10,10));
        gridPane2.setHgap(5);
        gridPane2.setVgap(5);

        netMethodLabel = new Label("网格化方法:");
        netMethodLabel.setMinWidth(75);
        netMethodLabel.setPrefWidth(75);
        gridPane2.add(netMethodLabel,0,0);
        netMethodComboBox = new ComboBox<>();
        netMethodComboBox.setMinWidth(250);
        netMethodComboBox.setPrefWidth(250);
        netMethodComboBox.setEditable(false);
        netMethodComboBox.getItems().addAll("Kring泛克立格法网格化","距离幂函数反比加权网格化","多层B样条网格化","距离幂反比MQS加权网格化","稠密数据中值选取网格化","稠密数据高斯距离权网格化");
        netMethodComboBox.getSelectionModel().selectFirst();
        netMethodComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                switch (netMethodComboBox.getSelectionModel().getSelectedIndex()){
                    case 0:
                        selectButton.setDisable(false);
                        searchButton.setDisable(false);
                        break;
                    case 1:
                        selectButton.setDisable(false);
                        searchButton.setDisable(false);
                        break;
                    case 2:
                        selectButton.setDisable(false);
                        searchButton.setDisable(true);
                        break;
                    case 3:
                        selectButton.setDisable(true);
                        searchButton.setDisable(false);
                        break;
                    case 4:
                        selectButton.setDisable(true);
                        searchButton.setDisable(true);
                        break;
                    case 5:
                        selectButton.setDisable(true);
                        searchButton.setDisable(false);
                        break;
                }
            }
        });
        gridPane2.add(netMethodComboBox,1,0);
        selectButton = new Button("选择(s)...");
        selectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(netMethodComboBox.getSelectionModel().getSelectedIndex()==0){
                    KringParmDialog kringParmDlg = new KringParmDialog(m_KringInsProperty);
                    kringParmDlg.showAndWait();
                }else if(netMethodComboBox.getSelectionModel().getSelectedIndex()==1){
                    DistInsParamDialog distInsDlg = new DistInsParamDialog(m_DistInsProperty);
                    distInsDlg.showAndWait();
                }else if(netMethodComboBox.getSelectionModel().getSelectedIndex()==2){
                    BlineParamDialog bLineParamDlg = new BlineParamDialog(m_MBSplineParam);
                    bLineParamDlg.showAndWait();
                }
            }
        });
        gridPane2.add(selectButton,2,0);
        searchButton = new Button("搜索(F)...");
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                SearchDatParamDialog searchDatDlg = new SearchDatParamDialog(m_SearchProperty,netMethodComboBox.getSelectionModel().getSelectedIndex(),m_dots3D.size());
                searchDatDlg.showAndWait();
            }
        });
        gridPane2.add(searchButton,3,0);
        outputLabel = new Label("输出设置:");
        gridPane2.add(outputLabel,0,1);
        outputButtonEdit = new ButtonEdit();
        outputButtonEdit.setOnButtonClick(new EventHandler<ButtonEditEvent>() {
            @Override
            public void handle(ButtonEditEvent event) {
                GDBSaveFileDialog gdbSaveFile = new GDBSaveFileDialog();
                String rasterFilter = "栅格数据集" + "|ras|" + "MAPGISMSI (*.msi)" + "|*.msi|" + "GTiff (*.tif)" + "|*.tif|" + "HFA (*.img)" + "|*.img";
                gdbSaveFile.setFilter(rasterFilter);
                gdbSaveFile.setMultiSelect(false);
                Optional<String[]> optionalSaveFile = gdbSaveFile.showAndWait();
                if(optionalSaveFile!=null && optionalSaveFile.isPresent()){
                    List<String> outputUrlLst = Arrays.asList(optionalSaveFile.get());
                    outputButtonEdit.setText(outputUrlLst.get(0));
                }
            }
        });
        gridPane2.add(outputButtonEdit,1,1,3,1);

        outputAddToMapCheckBox = new CheckBox("添加到地图文档");
        outputAddToMapCheckBox.setSelected(true);
        gridPane2.add(outputAddToMapCheckBox,0,2,4,1);

        vBox1.getChildren().addAll(gridPane1,gridPane2);
        wizardPage2 = new WizardPage(vBox1,"网格参数设置");
    }

    private void dataReset(){
        inputDataButtonEdit.setText("");
        xValueComboBox.getItems().clear();
        xValueComboBox.getSelectionModel().select(-1);
        yValueComboBox.getItems().clear();
        yValueComboBox.getSelectionModel().select(-1);
        zValueComboBox.getItems().clear();
        zValueComboBox.getSelectionModel().select(-1);
        m_DataChanged = true;

        m_dots3D = new Dots3D();
        m_demInfo = new DemInfo();
        m_SearchProperty = new SearchProperty();
        m_DistInsProperty = new DistInsProperty();
        m_KringInsProperty = new KringInsProperty();
        m_MBSplineParam = new MBSplineParam();
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

    private com.zondy.mapgis.map.Map getMapByLayer(DocumentItem item)
    {
        if(item==null){
            return null;
        }
        DocumentItem itemParent = item.getParent();
        if(itemParent.getDocumentItemType().equals(DocumentItemType.Map)){
            return  (com.zondy.mapgis.map.Map) itemParent;
        }else{
            return getMapByLayer(itemParent);
        }
    }

    private void GetDefaultParam()
    {
        if (m_demInfo==null || m_dots3D==null)
        {
            return;
        }
        Rect rc = new Rect(Double.MAX_VALUE,Double.MAX_VALUE,Double.MIN_VALUE,Double.MIN_VALUE);
        // 根据点集获取相关信息
        double ZMaxValue = Double.MIN_VALUE;
        double ZMinValue = Double.MAX_VALUE;
        double ZSumValue = 0;
        for (int i=0; i<m_dots3D.size(); i++)
        {
            rc.setXMin(Math.min(rc.getXMin(),m_dots3D.get(i).getX()));
            rc.setXMax(Math.max(rc.getXMax(),m_dots3D.get(i).getX()));
            rc.setYMin(Math.min(rc.getYMin(),m_dots3D.get(i).getY()));
            rc.setYMax(Math.max(rc.getYMax(),m_dots3D.get(i).getY()));
            ZSumValue += m_dots3D.get(i).getZ();
            ZMinValue = Math.min(ZMinValue,m_dots3D.get(i).getZ());
            ZMaxValue = Math.max(ZMaxValue,m_dots3D.get(i).getZ());
        }

        // 设置格网信息
        m_demInfo.setInfo(101,101,rc);
        m_demInfo.setZMax(ZMaxValue);
        m_demInfo.setZMin(ZMinValue);
        double dXSpace = (m_demInfo.getXMax() - m_demInfo.getXMin())/m_demInfo.getWidth();
        if (dXSpace>0.00001){
            int nRows = (int)Math.ceil((m_demInfo.getYMax()-m_demInfo.getYMin())/dXSpace);
            m_demInfo.setHeight(nRows);
        }else{
            m_demInfo.setHeight(101);
        }
        if (m_demInfo.getHeight()<1)
        {
            m_demInfo.setHeight(101);
        }
        // 搜索参数
        if (m_SearchProperty!=null)
        {
            m_SearchProperty.setSeekWay(Seekway.FourDirection);
            m_SearchProperty.setDotPerSec(16);
            m_SearchProperty.setMinAllDot(5);
            m_SearchProperty.setMaxNullSec(4);
            double tmpRadius = 0.5*Math.sqrt(Math.pow((rc.getXMax()-rc.getXMin()),2)+Math.pow((rc.getYMax()-rc.getYMin()),2));
            m_SearchProperty.setRadius(tmpRadius);
        }
        // 距离幂反比插值参数
        if (m_DistInsProperty!=null)
        {
            m_DistInsProperty.setPower(2);
            m_DistInsProperty.setSmooth(0);
            m_DistInsProperty.setRatio(1);
            m_DistInsProperty.setAngle(0);
        }
        // 克里金插值参数
        int lCount = m_dots3D.size();
        double dZAverage = ZSumValue/lCount;
        if (m_KringInsProperty!=null)
        {
            m_KringInsProperty.setKringType(KringType.Kring_Liner);
            m_KringInsProperty.setDriftTime(0);
            m_KringInsProperty.setCValue(0);
            double zzSum = m_KringInsProperty.getCValue();
            for (int index=0; index<lCount; index++){
                double zz = m_dots3D.get(index).getZ();
                zzSum += Math.pow((zz-dZAverage),2);
            }
            m_KringInsProperty.setCValue(zzSum/lCount);
            m_KringInsProperty.setErrValue(0);
            m_KringInsProperty.setMicrostErrValue(0);
            double tmpRadiusA = 0.5*Math.sqrt(Math.pow((rc.getXMax()-rc.getXMin()),2)+Math.pow((rc.getYMax()-rc.getYMin()),2));
            m_KringInsProperty.setRaudisA(tmpRadiusA);
            m_KringInsProperty.setRatio(1);
            m_KringInsProperty.setAngle(0);
        }
        // 多层B样条插值
        if (m_MBSplineParam!=null)
        {
            m_MBSplineParam.setLayerNum(4);
            if (lCount>200)
            {
                m_MBSplineParam.setXCtrlNum(8);
                m_MBSplineParam.setYCtrlNum(8);
            }
            else
            {
                m_MBSplineParam.setXCtrlNum(1);
                m_MBSplineParam.setYCtrlNum(1);
            }
            m_MBSplineParam.setIsCalInValidCtrlPnt(false);
//            m_MBSplineParam.setCalN(0);
//            m_MBSplineParam.setCalM(0);
        }
    }

}
