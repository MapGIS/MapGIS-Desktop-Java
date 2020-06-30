package com.zondy.mapgis.rastereditor.dialogs;

import com.zondy.mapgis.analysis.rasteranalysis.OutputInfo;
import com.zondy.mapgis.analysis.rasteranalysis.RasterReclassOp;
import com.zondy.mapgis.analysis.rasteranalysis.ReclassifyInfo;
import com.zondy.mapgis.base.MessageBox;
import com.zondy.mapgis.controls.LayerSelectComboBoxItem;
import com.zondy.mapgis.controls.LayerSelectControl;
import com.zondy.mapgis.controls.common.ButtonEdit;
import com.zondy.mapgis.controls.common.ButtonEditEvent;
import com.zondy.mapgis.filedialog.GDBOpenFileDialog;
import com.zondy.mapgis.filedialog.GDBSaveFileDialog;
import com.zondy.mapgis.geodatabase.DataBase;
import com.zondy.mapgis.geodatabase.raster.*;
import com.zondy.mapgis.map.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.io.*;
import java.util.*;
import java.util.Map;

/**
 * 栅格重分类
 */
public class RasterReclassDialog extends Dialog {

    // 变量
    private Document  m_doc;                        // 地图文档
    private int       m_lClassNum = 10;             // 分类数
    private double    m_Tap = 1;                    // 步长
    private ReclassifyInfo[] m_pClassInfo = null;   // 分类追踪区间
    private double    m_Std = Math.sqrt(32);        // 初始化标准差步长
    private int       m_ValidTinPntNum = 0;         // 有效像元数目
    private boolean   m_IsEqualNumFirst = true;     // 是否第一次打开
    private RasterDataset m_RasterDataset = null;
    private RasterDataset m_TmpRaster = null;
    private boolean   m_ByLength = false;
    private boolean   m_Make = true;
    private boolean   m_IsButtonMakeChange = false;
    //
    private VBox reclassVBox = null;
    private ObservableList<ReclassItem> m_data = null;
    // 源数据
    private Label inputLabel = null;
    private LayerSelectControl inputLayerSelect = null;
    // 分类信息
    private Label reclassMethodLabel = null;
    private ComboBox<String> reclassMethodComboBox = null;
    private Label reclassCountsLabel = null;
    private Spinner<Integer> reclassCountsSpinner = null;
    private Label reclassTapLabel = null;
    private TextField reclassTapTextField = null;
    private TableView<ReclassItem> tableview = null;
    private Button mergeButton = null;
    private Button cancleMergeButton = null;
    private Button deleteButton = null;
    private Button loadButton = null;
    private Button saveButton = null;
    private CheckBox setNoDataCheckBox = null;
    //
    private Label outputLabel = null;
    private ButtonEdit outputButtonEdit = null;
    private RadioButton outputToDemRadioButton = null;
    private RadioButton outputToTxtRadioButton = null;
    private CheckBox addToDocCheckBox = null;
    private Button okButton = null;
    private Button okButtonUnVisible = null;
    private Button cancleButton = null;
    private Button cancleButtonUnVisible = null;

    public RasterReclassDialog(Document document)
    {
        setTitle("栅格重分类");
        initReclassVBox(document);
        setResizable(false);

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setContent(reclassVBox);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.setPrefSize(630, 565);
        dialogPane.setMinSize(630, 565);
        dialogPane.setMaxSize(630,565);

        okButtonUnVisible = (Button)dialogPane.lookupButton(ButtonType.OK);
        okButtonUnVisible.setVisible(false);
        cancleButtonUnVisible = (Button)dialogPane.lookupButton(ButtonType.CANCEL);
        cancleButtonUnVisible.setVisible(false);

        m_doc = document;

        // 选择栅格
        inputLayerSelect.setOnSelectedItemChanged(new ChangeListener<LayerSelectComboBoxItem>() {
            @Override
            public void changed(ObservableValue<? extends LayerSelectComboBoxItem> observable, LayerSelectComboBoxItem oldValue, LayerSelectComboBoxItem newValue) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("栅格重分类");

                if (m_RasterDataset!=null)
                {
                    reclassMethodComboBox.getSelectionModel().select(-1);
                    m_RasterDataset.close();
                    m_RasterDataset = null;
                }
                String url = inputLayerSelect.getSelectedItemUrl();
                if (url==null || url.isEmpty())
                {
                    return;
                }
                m_RasterDataset = new RasterDataset();
                long lErr = -1;
                if(url.toLowerCase().startsWith("file:///"))
                {
                    lErr = m_RasterDataset.openByURL(url, RasterAccess.Read);
                }
                else
                {
                    lErr = m_RasterDataset.open(url, RasterAccess.Read);
                }
                // 打开栅格数据
                if(lErr==0)
                {
                    alert.headerTextProperty().set("打开栅格数据失败!");
                    alert.showAndWait();
                    m_RasterDataset = null;
                }
                else
                {
                    m_IsEqualNumFirst = true;
                    reclassMethodComboBox.getSelectionModel().select(0);
                }
            }
        });

        // 重分类方法
        reclassMethodComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (m_RasterDataset==null)
                {
                    return;
                }
                // 等间距分类
                if (reclassMethodComboBox.getSelectionModel().getSelectedIndex()==0)
                {
                    reclassTapLabel.setText("分类间距:");
                    reclassCountsSpinner.setDisable(false);
                    m_lClassNum = 10;
                    EqualInterval();
                }
                // 等数目分类
                else if (reclassMethodComboBox.getSelectionModel().getSelectedIndex()==1)
                {
                    reclassTapLabel.setText("每类数目:");
                    reclassCountsSpinner.setDisable(false);
                    m_lClassNum = 10;
                    EqualDataNum();
                }
                // 标准差分类
                else if (reclassMethodComboBox.getSelectionModel().getSelectedIndex()==2)
                {
                    reclassTapLabel.setText("标准差倍数:");
                    reclassCountsSpinner.setDisable(true);
                    m_Tap = 1;
                    StandardDeviation();
                }
                SetListInfo();
                m_Make = true;
            }
        });

        // 分类数
        reclassCountsSpinner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                if (!m_Make)
                {
                    return;
                }
                if (m_IsButtonMakeChange)
                {
                    return;
                }
                if (m_RasterDataset==null)
                {
                    return;
                }
                m_lClassNum = reclassCountsSpinner.getValue();
                // 等间距分类
                if (reclassMethodComboBox.getSelectionModel().getSelectedIndex()==0)
                {
                    EqualInterval();
                }
                // 等数目分类
                else if (reclassMethodComboBox.getSelectionModel().getSelectedIndex()==1)
                {
                    EqualDataNum();
                }
                SetListInfo();
                m_Make = true;
            }
        });

        // 分类间隔
        reclassTapTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!m_Make)
                {
                    return;
                }
                if (m_RasterDataset==null)
                {
                    return;
                }
                if (Double.parseDouble(newValue)<=0)
                {
                    Alert alter = new Alert(Alert.AlertType.INFORMATION);
                    alter.setTitle("栅格重分类");
                    alter.headerTextProperty().set("请输入大于0的数!");
                    alter.showAndWait();
                    return;
                }
                m_Tap = Double.parseDouble(newValue);
                // 等间距分类
                if (reclassMethodComboBox.getSelectionModel().getSelectedIndex()==0)
                {
                    m_ByLength = true;
                    EqualInterval();
                    m_ByLength = false;
                }
                // 等数目分类
                else if (reclassMethodComboBox.getSelectionModel().getSelectedIndex()==1)
                {
                    m_ByLength = true;
                    EqualDataNum();
                    m_ByLength = false;
                }
                // 标准差分类
                else if (reclassMethodComboBox.getSelectionModel().getSelectedIndex()==2)
                {
                    StandardDeviation();
                }
                SetListInfo();
                m_Make = true;
            }
        });

        // 合并
        mergeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (tableview.getItems().size()!=0 && tableview.getSelectionModel().getSelectedIndices()!=null &&
                        tableview.getSelectionModel().getSelectedIndices().size()>1 )
                {
                    int SelectedCounts = tableview.getSelectionModel().getSelectedIndices().size();
                    String ss = "";
                    for (int i=0; i<SelectedCounts; i++)
                    {
                        int SelectedIndex = tableview.getSelectionModel().getSelectedIndices().get(i);
                        if (i!=SelectedCounts-1)
                        {
                            ss += tableview.getItems().get(SelectedIndex).getOldValue() + ";";
                        }
                        else
                        {
                            ss += tableview.getItems().get(SelectedIndex).getOldValue();
                        }
                    }
                    int SelectedIndex0 = tableview.getSelectionModel().getSelectedIndices().get(0);
                    tableview.getItems().get(SelectedIndex0).setOldValue(ss);
                    for (int i=1; i<SelectedCounts; i++)
                    {
                        int SelectedIndex = tableview.getSelectionModel().getSelectedIndices().get(i);
                        tableview.getItems().remove(SelectedIndex);
                    }
                    tableview.refresh();
                    DataTableTom_pClassInfo();
                    m_IsButtonMakeChange = true;
                    reclassCountsSpinner.getValueFactory().setValue(m_pClassInfo.length);
                    m_IsButtonMakeChange = false;
                }
            }
        });

        // 取消合并
        cancleMergeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (tableview.getItems().size()!=0 && tableview.getSelectionModel().getSelectedIndices()!=null &&
                        tableview.getSelectionModel().getSelectedIndices().size()==1)
                {
                    int SelectedIndex0 = tableview.getSelectionModel().getSelectedIndices().get(0);
                    String tmpOldValue = tableview.getItems().get(SelectedIndex0).getOldValue();
                    if (tmpOldValue.contains(";"))
                    {
                        Integer tmpNewValue = tableview.getItems().get(SelectedIndex0).getNewValue();
                        String[] ss = tmpOldValue.split(";");
                        for (int i=0; i<ss.length; i++)
                        {
                            tableview.getItems().add(new ReclassItem(ss[i],tmpNewValue));
                        }
                        tableview.getItems().remove(SelectedIndex0);
                        tableview.refresh();
                        RestoreOrder();
                        DataTableTom_pClassInfo();
                        m_IsButtonMakeChange = true;
                        reclassCountsSpinner.getValueFactory().setValue(m_pClassInfo.length);
                        m_IsButtonMakeChange = false;
                    }
                }
            }
        });

        // 删除
        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (tableview.getItems().size()!=0 && tableview.getSelectionModel().getSelectedIndices()!=null &&
                        tableview.getSelectionModel().getSelectedIndices().size()>0 )
                {
                    int SelectedCounts = tableview.getSelectionModel().getSelectedIndices().size();
                    for (int i=0; i<SelectedCounts; i++)
                    {
                        int SelectedIndex = tableview.getSelectionModel().getSelectedIndices().get(i) - i;
                        tableview.getItems().remove(SelectedIndex);
                    }
                    tableview.refresh();
                    DataTableTom_pClassInfo();
                    m_IsButtonMakeChange = true;
                    reclassCountsSpinner.getValueFactory().setValue(m_pClassInfo.length);
                    m_IsButtonMakeChange = false;
                }
            }
        });

        // 装入
        loadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                GDBOpenFileDialog openFile = new GDBOpenFileDialog();
                String strFilter = "分类区间文件" + "(*.cla)|*.cla";
                openFile.setFilter(strFilter);
                Optional<String[]> ClassOption = openFile.showAndWait();
                if(ClassOption!=null && ClassOption.isPresent()){
                    tableview.getItems().clear();
                    List<String> classPath = Arrays.asList(ClassOption.get());
                    try{
                        FileInputStream fis = new FileInputStream(classPath.get(0));
                        InputStreamReader isr = new InputStreamReader(fis,"GB2312");
                        BufferedReader br = new BufferedReader(isr);
                        String line = "";
                        while ((line = br.readLine())!=null)
                        {
                            String[] ss = line.split(",");
                            tableview.getItems().add(new ReclassItem(ss[0],Integer.parseInt(ss[1])));
                        }
                        br.close();
                        isr.close();
                        fis.close();
                        tableview.refresh();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });

        // 导出
        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                GDBSaveFileDialog saveClassInfo = new GDBSaveFileDialog();
                String saveFilter = "分类区间文件" + "(*.cla)|*.cla";
                saveClassInfo.setFilter(saveFilter);
                Optional<String[]> SaveClassOption = saveClassInfo.showAndWait();
                if(SaveClassOption!=null && SaveClassOption.isPresent()) {
                    List<String> saveClassPath = Arrays.asList(SaveClassOption.get());
                    try {
                        FileOutputStream fos = new FileOutputStream(new File(saveClassPath.get(0)));
                        OutputStreamWriter osw = new OutputStreamWriter(fos,"GB2312");
                        BufferedWriter bw = new BufferedWriter(osw);
                        String line = "";
                        for (int i=0; i<tableview.getItems().size();i++)
                        {
                            line = tableview.getItems().get(i).getOldValue() + "," + Integer.toString(tableview.getItems().get(i).getNewValue()) + "\t\n";
                            bw.write(line);
                        }
                        bw.close();
                        osw.close();
                        fos.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });

        //
        outputToDemRadioButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue)
                {
                    addToDocCheckBox.setText("添加到地图文档");
                }
                else
                {
                    addToDocCheckBox.setText("自动打开文件");
                }
            }
        });

        // 保存
        outputButtonEdit.setOnButtonClick(new EventHandler<ButtonEditEvent>() {
            @Override
            public void handle(ButtonEditEvent event) {
                if (outputToDemRadioButton.isSelected())
                {
                    GDBSaveFileDialog saveRaster = new GDBSaveFileDialog();
                    String rasterFilter = "栅格数据集" + "|ras|" + "MAPGISMSI (*.msi)" + "|*.msi|" + "GTiff (*.tif)" + "|*.tif|" + "HFA (*.img)" + "|*.img";
                    saveRaster.setFilter(rasterFilter);
                    Optional<String[]> RasterOption = saveRaster.showAndWait();
                    if(RasterOption!=null && RasterOption.isPresent()){
                        List<String> rasterOutPath = Arrays.asList(RasterOption.get());
                        outputButtonEdit.setText(rasterOutPath.get(0));
                    }
                }
                else
                {
                    GDBSaveFileDialog saveRaster = new GDBSaveFileDialog();
                    String rasterFilter = "文本文件" + "|*.txt|" + "Excel文件" + "|*.xls";
                    saveRaster.setFilter(rasterFilter);
                    Optional<String[]> RasterOption = saveRaster.showAndWait();
                    if(RasterOption!=null && RasterOption.isPresent()){
                        List<String> colorRasterOutPath = Arrays.asList(RasterOption.get());
                        outputButtonEdit.setText(colorRasterOutPath.get(0));
                    }
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

        // 确定
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("栅格重分类");
                // 数据校验
                String selUrl = inputLayerSelect.getSelectedItemUrl();
                if (selUrl==null || selUrl.isEmpty())
                {
                    alert.headerTextProperty().set("请选择输入影像!");
                    alert.showAndWait();
                    return;
                }
                if (tableview.getItems().size()==0)
                {
                    alert.headerTextProperty().set("列表不允许为空!");
                    alert.showAndWait();
                    return;
                }
                String path = outputButtonEdit.getText();
                if (path==null || path.isEmpty())
                {
                    alert.headerTextProperty().set("请选择输出路径!");
                    alert.showAndWait();
                    return;
                }
                if (!DataTableTom_pClassInfo())
                {
                    return;
                }
                // 重分类
                //RasterReclassOp reclassOp;
                // 栅格数据
                GeoRaster geoRaster = new GeoRaster();
                if (!geoRaster.Open(selUrl)){
                    alert.headerTextProperty().set("打开栅格数据失败!");
                    alert.showAndWait();
                    return;
                }
                long lErr = -1;
                if (outputToDemRadioButton.isSelected())
                {
                    OutputInfo output = new OutputInfo();
                    output.setPath(path);
                    output.setFormat(getRasterFormatByURL(path));
                    lErr = RasterReclassOp.reclass(geoRaster,m_pClassInfo,setNoDataCheckBox.isSelected(),output,null);
                    if (lErr>0)
                    {
                        alert.headerTextProperty().set("栅格重分类成功!");
                        alert.showAndWait();
                        if (addToDocCheckBox.isSelected())
                        {
                            // 添加重采样结果到地图文档
                            String savePath = outputButtonEdit.getText();
                            RasterLayer rasterLayer = new RasterLayer();
                            addLayerToDoc(savePath,rasterLayer,document);
                        }
                        okButtonUnVisible.disarm();
                        okButtonUnVisible.fire();
                    }
                    else
                    {
                        alert.headerTextProperty().set("栅格重分类失败!");
                        alert.showAndWait();
                    }
                }
                else
                {
                    alert.headerTextProperty().set("输出到Txt未实现!");
                    alert.showAndWait();
                }
            }
        });
    }

    private void initReclassVBox(Document document)
    {
        reclassVBox = new VBox();
        reclassVBox.setSpacing(10);
        reclassVBox.setStyle("-fx-font-size: 9pt;");

        int width = 600;

        // 源数据
        TitledPane titledPane1 = new TitledPane();
        titledPane1.setCollapsible(false);
        titledPane1.setText("源数据");
        titledPane1.setMinWidth(width);
        titledPane1.setPrefWidth(width);

        GridPane gridPane1 = new GridPane();
        gridPane1.setPadding(new Insets(10,10,10,10));
        gridPane1.setVgap(5);
        gridPane1.setHgap(5);

        inputLabel = new Label("输入数据:");
        inputLabel.setPrefWidth(60);
        gridPane1.add(inputLabel,0,0);
        String rasterFilter = "栅格数据，6x数据|ras;*.msi;*.tif;*.img";
        inputLayerSelect = new LayerSelectControl(document,rasterFilter);
        inputLayerSelect.setPrefWidth(520);
        gridPane1.add(inputLayerSelect,1,0);

        // 分类信息
        TitledPane titledPane2 = new TitledPane();
        titledPane2.setCollapsible(false);
        titledPane2.setText("分类信息");
        titledPane2.setMinWidth(width);
        titledPane2.setPrefWidth(width);

        GridPane gridPane2 = new GridPane();
        gridPane2.setPadding(new Insets(10,10,10,10));
        gridPane2.setHgap(5);
        gridPane2.setVgap(5);

        int width2 = 130;
        int height2 = 30;
        reclassMethodLabel = new Label("分类方法:");
        reclassMethodLabel.setPrefWidth(width2);
        reclassMethodLabel.setAlignment(Pos.CENTER_RIGHT);
        gridPane2.add(reclassMethodLabel,0,0);
        reclassMethodComboBox = new ComboBox<>();
        reclassMethodComboBox.setPrefWidth(450);
        reclassMethodComboBox.getItems().addAll("等间距分类","等数目分类","标准差分类");
        gridPane2.add(reclassMethodComboBox,1,0);
        reclassCountsLabel = new Label("分 类 数:");
        reclassCountsLabel.setPrefWidth(width2);
        reclassCountsLabel.setAlignment(Pos.CENTER_RIGHT);
        gridPane2.add(reclassCountsLabel,0,1);
        reclassCountsSpinner = new Spinner<>(1,Integer.MAX_VALUE,10);
        reclassCountsSpinner.setPrefWidth(450);
        reclassCountsSpinner.setEditable(true);
        gridPane2.add(reclassCountsSpinner,1,1);
        reclassTapLabel = new Label("分类间距:");
        reclassTapLabel.setPrefWidth(width2);
        reclassTapLabel.setAlignment(Pos.CENTER_RIGHT);
        gridPane2.add(reclassTapLabel,0,2);
        reclassTapTextField = new TextField();
        gridPane2.add(reclassTapTextField,1,2);
        mergeButton = new Button("合并分类(U)");
        mergeButton.setPrefSize(width2,height2);
        gridPane2.add(mergeButton,0,3);
        cancleMergeButton = new Button("取消合并(N)");
        cancleMergeButton.setPrefSize(width2,height2);
        gridPane2.add(cancleMergeButton,0,4);
        deleteButton = new Button("删除分类(D)");
        deleteButton.setPrefSize(width2,height2);
        gridPane2.add(deleteButton,0,5);
        loadButton = new Button("装入分类文件(I)");
        loadButton.setPrefSize(width2,height2);
        gridPane2.add(loadButton,0,6);
        saveButton = new Button("保存分类文件(S)");
        saveButton.setPrefSize(width2,height2);
        gridPane2.add(saveButton,0,7);

        // 表格信息
        m_data = FXCollections.observableArrayList();
        tableview = new TableView<>(m_data);
        tableview.setEditable(true);
        tableview.setPrefHeight(170);
        tableview.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        TableColumn<ReclassItem,String> tc_OldValue = new TableColumn<>("旧值");
        tc_OldValue.setSortable(false);
        tc_OldValue.setPrefWidth(220);
        tc_OldValue.setMinWidth(220);
        tc_OldValue.setCellValueFactory(new PropertyValueFactory<>("oldValue"));
        tc_OldValue.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<String>() {
            @Override
            public String toString(String object) {
                return object;
            }

            @Override
            public String fromString(String string) {
                return string;
            }
        }));
        TableColumn<ReclassItem,Integer> tc_NewValue = new TableColumn<>("新值");
        tc_NewValue.setSortable(false);
        tc_NewValue.setPrefWidth(220);
        tc_NewValue.setMinWidth(220);
        tc_NewValue.setCellValueFactory(new PropertyValueFactory<>("newValue"));
        tc_NewValue.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        tableview.getColumns().addAll(tc_OldValue,tc_NewValue);
        gridPane2.add(tableview,1,3,1,5);

        setNoDataCheckBox = new CheckBox("是否设置缺省值为无效值");
        setNoDataCheckBox.setSelected(true);
        gridPane2.add(setNoDataCheckBox,0,8,2,1);

        TitledPane titledPane3 = new TitledPane();
        titledPane3.setCollapsible(false);
        titledPane3.setText("结果数据");
        titledPane3.setMinWidth(width);
        titledPane3.setPrefWidth(width);

        GridPane gridPane3 = new GridPane();
        gridPane3.setPadding(new Insets(10,10,10,10));
        gridPane3.setHgap(5);
        gridPane3.setVgap(5);

        outputLabel = new Label("输出路径:");
        outputLabel.setPrefWidth(65);
        gridPane3.add(outputLabel,0,0);
        outputButtonEdit = new ButtonEdit();
        outputButtonEdit.setPrefWidth(515);
        gridPane3.add(outputButtonEdit,1,0);

        HBox hBox3 = new HBox();
        hBox3.setSpacing(100);
        ToggleGroup toggle3 = new ToggleGroup();
        outputToDemRadioButton = new RadioButton("栅格数据");
        outputToDemRadioButton.setSelected(true);
        outputToDemRadioButton.setToggleGroup(toggle3);
        outputToTxtRadioButton = new RadioButton("统计报告");
        outputToTxtRadioButton.setToggleGroup(toggle3);
        hBox3.getChildren().addAll(outputToDemRadioButton,outputToTxtRadioButton);
        gridPane3.add(hBox3,0,1,2,1);

        AnchorPane anchorPane1 = new AnchorPane();
        anchorPane1.setMinWidth(width);
        anchorPane1.setPrefWidth(width);

        addToDocCheckBox = new CheckBox("添加到地图文档");
        addToDocCheckBox.setSelected(true);
        addToDocCheckBox.setLayoutX(0);

        okButton = new Button("计算");
        okButton.setPrefSize(60,30);
        okButton.setLayoutX(460);

        cancleButton = new Button("取消");
        cancleButton.setPrefSize(60,30);
        cancleButton.setLayoutX(530);

        anchorPane1.getChildren().addAll(addToDocCheckBox,okButton,cancleButton);
        titledPane3.setContent(gridPane3);
        titledPane2.setContent(gridPane2);
        titledPane1.setContent(gridPane1);
        reclassVBox.getChildren().addAll(titledPane1,titledPane2,titledPane3,anchorPane1);
    }

    // 设置表格信息
    private void SetListInfo()
    {
        if (m_pClassInfo==null)
        {
            return;
        }
        tableview.getItems().clear();
        for (int i=0; i<m_pClassInfo.length; i++)
        {
            String oldValue = Double.toString(m_pClassInfo[i].getMinValue()) + "-" + Double.toString(m_pClassInfo[i].getMaxValue());
            Integer newValue = ((Long)m_pClassInfo[i].getNewValue()).intValue();
            tableview.getItems().add(new ReclassItem(oldValue,newValue));
        }
        tableview.refresh();
        reclassCountsSpinner.getValueFactory().setValue(m_lClassNum);
        reclassTapTextField.setText(Double.toString(m_Tap));
    }

    // 根据第一列内容开头部分数值大小排序列表
    private void RestoreOrder()
    {
        int n = tableview.getItems().size();
        ArrayList<Double> dHeadVal = new ArrayList<>();
        Map<String,Integer> originKeyValue = new HashMap<>();
        for (int i=0; i<n; i++)
        {
            String cls_key = tableview.getItems().get(i).getOldValue();
            int cls_Val = tableview.getItems().get(i).getNewValue();//Integer.parseInt();
            originKeyValue.put(cls_key,cls_Val);
            int p = cls_key.indexOf('-');
            String strHeadVal = cls_key.substring(0,p);
            dHeadVal.add(Double.parseDouble(strHeadVal));
        }
        Collections.sort(dHeadVal);
        tableview.getItems().clear();
        for (Double ele: dHeadVal)
        {
            String order_Col1 = "";  // 第一列内容
            Integer order_Col2 = 0;  // 第二列内容
            for (String str: originKeyValue.keySet())
            {
                if (str.startsWith(ele.toString()))
                {
                    order_Col1 = str;
                    order_Col2 = originKeyValue.get(str);
                    break;
                }
            }
            tableview.getItems().add(new ReclassItem(order_Col1,order_Col2));
        }
        tableview.refresh();
    }

    private boolean DataTableTom_pClassInfo()
    {
        int n = tableview.getItems().size();
        if (n==0)
        {
            return false;
        }
        Map<String, Integer> classInfoMap = new HashMap<>();
        ArrayList<String> classOldValue =  new ArrayList<>();
        for (int i=0; i<n; i++)
        {
            String tmpOldValue = tableview.getItems().get(i).getOldValue();
            Integer tmpNewValue = tableview.getItems().get(i).getNewValue();
            String[] ss_oldValue = tmpOldValue.split(";");
            for (String str: ss_oldValue)
            {
                classOldValue.add(str);
                classInfoMap.put(str,tmpNewValue);
            }
        }
        m_pClassInfo = new ReclassifyInfo[classOldValue.size()];
        double max=0, min = 0;
        for (int i=0; i<classOldValue.size(); i++)
        {
            m_pClassInfo[i] = new ReclassifyInfo();
            String tmpOldValue = classOldValue.get(i);
            Integer tmpNewValue = classInfoMap.get(tmpOldValue);
            String[] ss = tmpOldValue.split("-");
            try {
                min = Double.parseDouble(ss[0]);
                max = Double.parseDouble(ss[1]);
            }catch (Exception ex){
                MessageBox.information("分类信息存在不合法,请重新输入!");
            }
            m_pClassInfo[i].setMinValue(min);
            m_pClassInfo[i].setMaxValue(max);
            m_pClassInfo[i].setNewValue(tmpNewValue);
        }
        return true;
    }

    // 等间距分类
    private void EqualInterval()
    {
        double max = m_RasterDataset.getRasterBand(1).getStatistics().getMax();
        double min = m_RasterDataset.getRasterBand(1).getStatistics().getMin();
        max = Double.valueOf(String.format("%.2f",max));
        min = Double.valueOf(String.format("%.2f",min));
        if (m_ByLength)
        {
            m_lClassNum = (int)Math.round((max - min)/m_Tap);
        }
        else
        {
            m_Tap = (max - min)/m_lClassNum;
        }
        // 重分类区间信息
        m_pClassInfo = new ReclassifyInfo[m_lClassNum];
        for (int i=0; i<m_lClassNum; i++)
        {
            m_pClassInfo[i] = new ReclassifyInfo();
        }
        //
        double MinValue = 0, MaxValue = 0;
        MinValue = min;
        MaxValue = min+m_Tap;
        for (int i=0; i<m_lClassNum - 1; i++)
        {
            if (MaxValue>max)
            {
                MaxValue = max;
            }
            MinValue = Double.valueOf(String.format("%.2f", MinValue));
            MaxValue = Double.valueOf(String.format("%.2f", MaxValue));
            m_pClassInfo[i].setMinValue(MinValue);
            m_pClassInfo[i].setMaxValue(MaxValue);
            m_pClassInfo[i].setNewValue(i+1);
            MinValue = MaxValue;
            MaxValue = MinValue + m_Tap;
        }
        // 最后一类数据
        if (m_lClassNum > 1)
        {
            m_pClassInfo[m_lClassNum-1].setMinValue(m_pClassInfo[m_lClassNum-2].getMaxValue());
            m_pClassInfo[m_lClassNum-1].setMaxValue(max);
            m_pClassInfo[m_lClassNum-1].setNewValue(m_lClassNum);
        }
        if (m_lClassNum == 1)
        {
            m_pClassInfo[0].setMinValue(min);
            m_pClassInfo[0].setMaxValue(max);
            m_pClassInfo[0].setNewValue(m_lClassNum);
        }
    }

    // 等数目分类
    private void EqualDataNum()
    {
        RasterBand srcBand = m_RasterDataset.getRasterBand(1);
        double max = m_RasterDataset.getRasterBand(1).getStatistics().getMax();
        double min = m_RasterDataset.getRasterBand(1).getStatistics().getMin();
        int linecell = 0, line = 0, linecell1 = 0, line1 = 0;
        int k = 0, i = 0;
        int nx = m_RasterDataset.getWidth();
        int ny = m_RasterDataset.getHeight();
        RasterBand tmpRasBand = null;
        if (m_IsEqualNumFirst)
        {
            m_IsEqualNumFirst = false;
            // 删除上一次的临时栅格数据
            if (m_TmpRaster != null)
            {
                DataBase db = m_TmpRaster.getGDataBase();
                int id = m_TmpRaster.getClsID();
                m_TmpRaster.close();
                RasterDataset.deleteFromGDB(db,id);
                m_TmpRaster = null;
            }
            // 创建临时的栅格数据
            DataBase gdb = DataBase.openTempDB();
            if (gdb==null)
            {
                return;
            }
            m_TmpRaster = new RasterDataset(gdb);
            long rtn = m_TmpRaster.create(RasterFormat.MAPGIS7MSI,UUID.randomUUID().toString(),nx,ny,1,m_RasterDataset.getPixelType());
            if (rtn==0L)
            {
                return;
            }
            tmpRasBand = m_TmpRaster.getRasterBand(1);

            m_ValidTinPntNum = 0;
            // 初始化分类数据
            for (line=0; line<ny; line++)
            {
                for (linecell=0; linecell<nx; linecell++)
                {
                    double zValue = srcBand.getPixel(linecell,line);
                    if (m_RasterDataset.getNullVal() == zValue)
                    {
                        continue;
                    }
                    tmpRasBand.setPixel(linecell1,line1,zValue);
                    linecell1++;
                    m_ValidTinPntNum++;
                    if (linecell1>=nx)
                    {
                        line1++;
                        linecell1 = 0;
                    }
                }
            }
            // 升序排序
            QuickSort(tmpRasBand,0,m_ValidTinPntNum-1,nx);
        }
        else
        {
            tmpRasBand = m_TmpRaster.getRasterBand(1);
        }

        if (m_ByLength)
        {
            m_lClassNum = (int)Math.round(m_ValidTinPntNum/m_Tap);
        }
        else
        {
            m_Tap = m_ValidTinPntNum/m_lClassNum;
        }

        // 初始化重分类区间
        m_pClassInfo = new ReclassifyInfo[m_lClassNum];
        for (int j=0; j<m_lClassNum; j++)
        {
            m_pClassInfo[j] = new ReclassifyInfo();
        }

        // 为重分类区间赋值
        k = 0;
        for (i=0; i<m_lClassNum - 1; i++)
        {
            // 下限
            line = k/nx;
            linecell = k - line*nx;
            double zValue = tmpRasBand.getPixel(linecell,line);
            m_pClassInfo[i].setMinValue(zValue);

            // 上限
            k += (int)m_Tap;
            if (k>m_ValidTinPntNum - 1)
            {
                k = m_ValidTinPntNum - 1;
            }
            line = k/nx;
            linecell = k - line*nx;
            zValue = tmpRasBand.getPixel(linecell,line);
            m_pClassInfo[i].setMaxValue(zValue);
            m_pClassInfo[i].setNewValue(i+1);
        }
        // 最后一类数据
        if (m_lClassNum>1)
        {
            m_pClassInfo[m_lClassNum-1].setMinValue(m_pClassInfo[m_lClassNum-2].getMaxValue());
            m_pClassInfo[m_lClassNum-1].setMaxValue(max);
            m_pClassInfo[m_lClassNum-1].setNewValue(m_lClassNum);
        }
        if (m_lClassNum == 1)
        {
            m_pClassInfo[0].setMinValue(min);
            m_pClassInfo[0].setMaxValue(max);
            m_pClassInfo[0].setNewValue(m_lClassNum);
        }
    }

    // 标准差分类
    private void StandardDeviation()
    {
        double max = m_RasterDataset.getRasterBand(1).getStatistics().getMax();
        double min = m_RasterDataset.getRasterBand(1).getStatistics().getMin();

        m_lClassNum = (int)((max-min)/(m_Tap*m_Std));
        m_pClassInfo = new ReclassifyInfo[m_lClassNum];
        for (int i=0; i<m_lClassNum; i++)
        {
            m_pClassInfo[i] = new ReclassifyInfo();
        }

        double Tap = 0;
        double MinValue = 0, MaxValue = 0;

        Tap = m_Tap*m_Std;
        MinValue = min;
        MaxValue = min+Tap;

        for (int i=0; i<m_lClassNum-1; i++)
        {
            if (MaxValue > max)
            {
                MaxValue = max;
            }
            m_pClassInfo[i].setMinValue(MinValue);
            m_pClassInfo[i].setMaxValue(MaxValue);
            m_pClassInfo[i].setNewValue(i+1);
            MinValue = MaxValue;
            MaxValue = MinValue+Tap;
        }

        // 最后一类数据
        if (m_lClassNum>1)
        {
            m_pClassInfo[m_lClassNum-1].setMinValue(m_pClassInfo[m_lClassNum-2].getMaxValue());
            m_pClassInfo[m_lClassNum-1].setMaxValue(max);
            m_pClassInfo[m_lClassNum-1].setNewValue(m_lClassNum);
        }
        if (m_lClassNum==1)
        {
            m_pClassInfo[0].setMinValue(min);
            m_pClassInfo[0].setMaxValue(max);
            m_pClassInfo[0].setNewValue(m_lClassNum);
        }
    }

    // 排序
    private void QuickSort(RasterBand rasterband,int left,int right,int nx)
    {
        int i = left, j = right, midnum = 0;
        int line = 0, linecell = 0;
        double Tmp = 0, Mid = 0;
        double ZValueI = 0, ZValueJ = 0;

        // 初始化
        midnum = (left+right)/2;
        line = midnum/nx;
        linecell = midnum - line*nx;
        Mid = rasterband.getPixel(linecell,line);

        do {
            line = i/nx;
            linecell = i-line*nx;
            ZValueI = rasterband.getPixel(linecell,line);

            line = j/nx;
            linecell = j-line*nx;
            ZValueJ = rasterband.getPixel(linecell,line);

            while(ZValueI<Mid && i<right)
            {
                i++;
                line = i/nx;
                linecell = i-line*nx;
                ZValueI = rasterband.getPixel(linecell,line);
            }

            while(ZValueJ>Mid && j>left)
            {
                j--;
                line = j/nx;
                linecell = j-line*nx;
                ZValueJ = rasterband.getPixel(linecell,line);
            }

            if (i <= j)
            {
                Tmp = ZValueI;
                ZValueI = ZValueJ;
                ZValueJ = Tmp;

                line = i/nx;
                linecell = i-line*nx;
                rasterband.setPixel(linecell,line,ZValueI);

                line = j/nx;
                linecell = j-line*nx;
                rasterband.setPixel(linecell,line,ZValueJ);

                i++;
                j--;
            }
        }while (i<=j);

        if (left<j)
        {
            QuickSort(rasterband,left,j,nx);
        }
        if (i<right)
        {
            QuickSort(rasterband,i,right,nx);
        }
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
            com.zondy.mapgis.map.Map mp = getMapByLayer(inputLayerSelect.getSelectedDocumentItem());
            if(mp!=null){
                mp.append(rasterLayer);
            }else{
                if(document.getMaps().getCount()!=0){
                    document.getMaps().getMap(0).append(rasterLayer);
                }else{
                    com.zondy.mapgis.map.Map map = new com.zondy.mapgis.map.Map();
                    map.setName("栅格重分类");
                    map.append(rasterLayer);
                    document.getMaps().append(map);
                }
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

}
