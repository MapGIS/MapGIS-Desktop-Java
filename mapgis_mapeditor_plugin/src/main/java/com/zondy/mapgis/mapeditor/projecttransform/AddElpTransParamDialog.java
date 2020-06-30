package com.zondy.mapgis.mapeditor.projecttransform;


import com.zondy.mapgis.base.GISDefaultValues;
import com.zondy.mapgis.base.UIFunctions;
import com.zondy.mapgis.base.XFunctions;
import com.zondy.mapgis.base.XString;
import com.zondy.mapgis.srs.ElpTransParam;
import com.zondy.mapgis.srs.ElpTransformation;
import com.zondy.mapgis.srs.Pnt3Struct;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.util.ArrayList;

/**
 * 添加地理转换项
 */
public class AddElpTransParamDialog extends Dialog<ElpTransParam> {
    private Stage stage = null;
    private ElpTransParam elpTransParam = null;
    private ArrayList<String> currentTransNameList = null;//已经存在或待添加的转换项名称
    private Button calculateButton = null;


    public AddElpTransParamDialog(ArrayList<String> transNameList) {
        this.currentTransNameList = transNameList;
        setTitle("添加地理转换项");
        initialize();
        //绑定事件
        bindAction();
        radio1.setSelected(true);
        //region 初始化界面暂时解决未触发单选按钮选择事件
        //计算控制点界面可见
        gcpLabel.setVisible(true);
        gcpText.setVisible(true);
        gcpBtn.setVisible(true);
        viewBtn.setVisible(true);
        //导入转换项内容不可见
        importUrlLabel.setVisible(false);
        importUrlText.setVisible(false);
        importUrlBtn.setVisible(false);
        importTransItemLabel.setVisible(false);
        importTransItemCombobox.setVisible(false);
        //endregion
        if (srcSRefItems.size() > 0)
            srcSRefCombo.getSelectionModel().select(0);
        if (desSRefItems.size() > 0)
            desSRefCombo.getSelectionModel().select(0);
        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setContent(this.gridPane);
        ButtonType calculateType = new ButtonType("计算", ButtonBar.ButtonData.APPLY);
        dialogPane.getButtonTypes().addAll(calculateType, ButtonType.OK, ButtonType.CANCEL);
        stage = (Stage) dialogPane.getScene().getWindow();

        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, this::okButtonClick);
        calculateButton = (Button) dialogPane.lookupButton(calculateType);
        calculateButton.addEventFilter(ActionEvent.ACTION, this::calculateButtonClick);
        calculateButton.setDisable(true);
        setResultConverter(dialogButton -> dialogButton == ButtonType.OK ? this.elpTransParam : null);
    }

    public AddElpTransParamDialog() {
        setTitle("添加地理转换项");
        initialize();

        //绑定事件
        bindAction();
        radio1.setSelected(true);
        //region 初始化界面暂时解决未触发单选按钮选择事件
        //计算控制点界面可见
        gcpLabel.setVisible(true);
        gcpText.setVisible(true);
        gcpBtn.setVisible(true);
        viewBtn.setVisible(true);
        //导入转换项内容不可见
        importUrlLabel.setVisible(false);
        importUrlText.setVisible(false);
        importUrlBtn.setVisible(false);
        importTransItemLabel.setVisible(false);
        importTransItemCombobox.setVisible(false);
        //endregion
        if (srcSRefItems.size() > 0)
            srcSRefCombo.getSelectionModel().select(0);
        if (desSRefItems.size() > 0)
            desSRefCombo.getSelectionModel().select(0);
        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setContent(this.gridPane);
        ButtonType calculateType = new ButtonType("计算", ButtonBar.ButtonData.APPLY);
        dialogPane.getButtonTypes().addAll(calculateType, ButtonType.OK, ButtonType.CANCEL);
        stage = (Stage) dialogPane.getScene().getWindow();

        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, this::okButtonClick);
        calculateButton = (Button) dialogPane.lookupButton(calculateType);
        calculateButton.addEventFilter(ActionEvent.ACTION, this::calculateButtonClick);
        calculateButton.setDisable(true);
        setResultConverter(dialogButton -> dialogButton == ButtonType.OK ? this.elpTransParam : null);
    }

    private GridPane gridPane = null;
    //添加方式
    private RadioButton radio1 = null;
    private RadioButton radio2 = null;
    private RadioButton radio3 = null;
    //根据控制点文件计算
    private Label gcpLabel = null;
    private TextField gcpText = null;  //控制点文件名
    private Button gcpBtn = null;   //选择控制点文件
    private Button viewBtn = null; //查看
    private ArrayList<Pnt3Struct> lstpnt3 = new ArrayList<Pnt3Struct>();//点结构列表

    //导入转换项
    private Label importUrlLabel = null;
    private TextField importUrlText = null; //文件路径
    private Button importUrlBtn = null;     //选择文件路径
    private Label importTransItemLabel = null;
    private ComboBox<String> importTransItemCombobox = null; //转换项下拉列表
    private ObservableList<String> importTransItemList = null;
    private ArrayList<ElpTransParam> importTransParamItems = new ArrayList<>();  //导入的转换项列表

    //转换信息
    private ComboBox<String> srcSRefCombo = null;
    private ComboBox<String> desSRefCombo = null;
    private Label srcUnitLabel = null;
    private Label desUnitLabel = null;
    private ObservableList<String> srcSRefItems = null;
    private ObservableList<String> desSRefItems = null;
    private ComboBox<String> srcUnitCombo = null;
    private ComboBox<String> desUnitCombo = null;
    private TextField transNameText = null;  //转换名称
    private ComboBox<String> transMethodCombo = null; //转换方法下拉项
    private ObservableList<String> transMethodItems = null;

    //转换参数列表
    private ObservableList<TransParamInfo> transParamInfoObservableList = null;
    private TableView<TransParamInfo> transParamInfoTableView = null;

    private void initialize() {
        gridPane = new GridPane();
        gridPane.setPrefWidth(500);
        gridPane.setPrefHeight(500);
//        gridPane.setVgap(5);
//        gridPane.setHgap(5);

        //region 添加方式板块
        GridPane addWayGrid = new GridPane();
        addWayGrid.setVgap(5);
        addWayGrid.setHgap(5);
        TitledPane addWayTPan = new TitledPane();
        addWayTPan.setText("添加方式");
        addWayTPan.setContent(addWayGrid);
        addWayTPan.setCollapsible(false);
        addWayTPan.setPadding(new Insets(5, 0, 0, 5));

        ToggleGroup groupRad = new ToggleGroup();
        radio1 = new RadioButton("根据控制点文件计算");
        radio2 = new RadioButton("手动输入");
        radio3 = new RadioButton("导入转换项");
        radio1.setToggleGroup(groupRad);
        radio1.setUserData(0);
        radio2.setToggleGroup(groupRad);
        radio2.setUserData(1);
        radio3.setToggleGroup(groupRad);
        radio3.setUserData(2);

        int rowIndex = 0;
        //第一行
        gcpLabel = new Label("控制点文件:");
        gcpText = new TextField();
        gcpBtn = new Button("...");
        viewBtn = new Button("查看");
        addWayGrid.add(radio1, 0, 0);
        addWayGrid.add(gcpLabel, 1, 0);
        addWayGrid.add(gcpText, 2, 0);
        addWayGrid.add(gcpBtn, 3, 0);
        addWayGrid.add(viewBtn, 4, 0);

        //第二行
        rowIndex++;
        importUrlLabel = new Label("文件路径:");
        importUrlText = new TextField();
        importUrlBtn = new Button("...");

        addWayGrid.add(radio2, 0, rowIndex);
        addWayGrid.add(importUrlLabel, 1, rowIndex);
        addWayGrid.add(importUrlText, 2, rowIndex);
        addWayGrid.add(importUrlBtn, 3, rowIndex);


        //第三行
        rowIndex++;
        importTransItemLabel = new Label("转换项:");
        importTransItemList = FXCollections.observableArrayList();
        importTransItemCombobox = new ComboBox<>(importTransItemList);
        addWayGrid.add(radio3, 0, rowIndex);
        addWayGrid.add(importTransItemLabel, 1, rowIndex);
        addWayGrid.add(importTransItemCombobox, 2, rowIndex, 3, 1);
        importTransItemCombobox.setPrefWidth(160);
        //endregion

        //region 转换信息板块
        GridPane transInfoGrid = new GridPane();
        transInfoGrid.setVgap(5);
        transInfoGrid.setHgap(5);
        TitledPane transInfoTPan = new TitledPane();
        transInfoTPan.setText("转换信息");
        transInfoTPan.setContent(transInfoGrid);
        transInfoTPan.setCollapsible(false);
        transInfoTPan.setPadding(new Insets(5, 0, 0, 5));

        //原坐标系
        Label srcSRefLabel = new Label("原坐标系:");
        Label desSRefLabel = new Label("目的坐标系:");
        srcSRefCombo = new ComboBox<>();
        srcSRefCombo.setPrefWidth(215);
        srcSRefItems = FXCollections.observableArrayList();
        desSRefItems = FXCollections.observableArrayList();
        for (int i = 1; i <= ElpTransformation.getElpCount(); i++) {
            String name = ElpTransformation.getElpParam(i).getName();
            if (name != null && name.length() > 0) {
                srcSRefItems.addAll(name);
                desSRefItems.addAll(name);
            }
        }
        srcSRefCombo.setItems(srcSRefItems);
//        if (srcSRefItems.size() > 0) {
//            srcSRefCombo.getSelectionModel().select(0);
//        }
        //目的坐标系
        desSRefCombo = new ComboBox<>();
        desSRefCombo.setPrefWidth(215);
        desSRefCombo.setItems(desSRefItems);
//        if (desSRefItems.size() > 0) {
//            desSRefCombo.getSelectionModel().select(0);
//        }
        srcUnitLabel = new Label("单位:");
        desUnitLabel = new Label("单位:");
        srcUnitCombo = new ComboBox<>();
        desUnitCombo = new ComboBox<>();
        ObservableList<String> unitItems = FXCollections.observableArrayList();
        unitItems.addAll("度", "分", "秒", "度分秒");
        srcUnitCombo.setItems(unitItems);
        srcUnitCombo.getSelectionModel().select("秒");
        desUnitCombo.setItems(unitItems);
        desUnitCombo.getSelectionModel().select("秒");

        //第一行
        rowIndex = 0;
        transInfoGrid.add(srcSRefLabel, 0, rowIndex);
        transInfoGrid.add(srcSRefCombo, 1, rowIndex);
        transInfoGrid.add(srcUnitLabel, 2, rowIndex);
        transInfoGrid.add(srcUnitCombo, 3, rowIndex);
        //第二行
        rowIndex++;
        transInfoGrid.add(desSRefLabel, 0, rowIndex);
        transInfoGrid.add(desSRefCombo, 1, rowIndex);
        transInfoGrid.add(desUnitLabel, 2, rowIndex);
        transInfoGrid.add(desUnitCombo, 3, rowIndex);

        //转换名称、转换方法
        Label transNameLabel = new Label("转换名称:");
        Label transMethodLabel = new Label("转换方法:");
        transNameText = new TextField();
        //transNameText.setText("北京54->北京54");
        transNameText.setPrefWidth(215);
        transMethodCombo = new ComboBox<>();
        transMethodCombo.setPrefWidth(215);
        transMethodItems = FXCollections.observableArrayList();
        transMethodItems.addAll("三参数直角平移法", "七参数bursawol法");
        transMethodCombo.setItems(transMethodItems);
        transMethodCombo.getSelectionModel().select(0);
        //第三行
        rowIndex++;
        transInfoGrid.add(transNameLabel, 0, rowIndex);
        transInfoGrid.add(transNameText, 1, rowIndex);
        //第四行
        rowIndex++;
        transInfoGrid.add(transMethodLabel, 0, rowIndex);
        transInfoGrid.add(transMethodCombo, 1, rowIndex);
        //第五行
        rowIndex++;
        Label transParamLabel = new Label("转换参数(米/弧度):");
        transInfoGrid.add(transParamLabel, 0, rowIndex);

        //转换参数列表
        transParamInfoObservableList = FXCollections.observableArrayList();
        transParamInfoTableView = new TableView<>();
        transParamInfoTableView.setEditable(true);
        transParamInfoTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<TransParamInfo, String> tc_name = new TableColumn("参数名");
        tc_name.setEditable(false);
        tc_name.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TransParamInfo, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<TransParamInfo, String> param) {
                return new SimpleStringProperty(param.getValue().getParamName());
            }
        });
        TableColumn<TransParamInfo, String> tc_value = new TableColumn("参数值");
        tc_value.setEditable(true);  //点击修改按钮后方可编辑参数值
//        tc_value.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TransParamInfo, String>, ObservableValue<String>>() {
//            @Override
//            public ObservableValue<String> call(TableColumn.CellDataFeatures<TransParamInfo, String> param) {
//                return new SimpleStringProperty(param.getValue().getParamValue());
//            }
//        });
//        //设置参数值的单元格为编辑框模式
//        tc_value.setCellFactory(TextFieldTableCell.forTableColumn());
//        tc_value.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<TransParamInfo, String>>() {
//            @Override
//            public void handle(TableColumn.CellEditEvent<TransParamInfo, String> event) {
//                event.getRowValue().setParamValue(event.getNewValue());
//            }
//        });

        tc_value.setCellValueFactory(new PropertyValueFactory<>("paramValue"));
        tc_value.setCellFactory(new Callback<TableColumn<TransParamInfo, String>, TableCell<TransParamInfo, String>>() {
            @Override
            public TableCell<TransParamInfo, String> call(TableColumn<TransParamInfo, String> param) {
                return new TableCell<TransParamInfo, String>()
                {
                    private TextField textField = new TextField();

                    @Override
                    public void startEdit()
                    {
                        super.startEdit();
                        textField.setText(getItem());
                        setGraphic(textField);
                        textField.requestFocus();

                        textField.textProperty().addListener((o, ov, nv) ->
                        {
//                            StringProperty errorMsg = new SimpleStringProperty();
//                            if (!XString.isTextValid(nv, 20, GISDefaultValues.getInvalidNameCharList(), errorMsg))
//                            {
//                                UIFunctions.showErrorTip(textField, errorMsg.get(), new Tooltip());
//                                textField.setText(ov);
//                            }
                        });
                        //按Enter键完成修改。
                        textField.setOnKeyPressed(event ->
                        {
                            if (KeyCode.ENTER.equals(event.getCode()))
                            {
                                transParamInfoTableView.requestFocus();
                            }
                        });
                        textField.focusedProperty().addListener((o, ov, nv) ->
                        {
                            if (!nv)
                            {
                                commitEdit(textField.getText());
                            }
                        });
                    }

                    @Override
                    public void commitEdit(String value)
                    {
                        super.commitEdit(value);
                        TransParamInfo paramInfo = (TransParamInfo) getTableRow().getItem();
                        if (paramInfo != null)
                        {
                            paramInfo.setParamValue(value);
                        }
                        updateItem(value, XString.isNullOrEmpty(value));
                    }

                    @Override
                    protected void updateItem(String item, boolean empty)
                    {
                        super.updateItem(item, empty);
                        Label label = null;
                        if (!empty)
                        {
                            label = new Label(item);
                        }
                        setGraphic(label);
                    }
                };
            }
        });

        transParamInfoTableView.getColumns().addAll(tc_name, tc_value);
        //初始化参数列表
        transParamInfoObservableList.add(new TransParamInfo("△X", ""));
        transParamInfoObservableList.add(new TransParamInfo("△Y", ""));
        transParamInfoObservableList.add(new TransParamInfo("△Z", ""));
        transParamInfoTableView.setItems(transParamInfoObservableList);
        //第六行
        rowIndex++;
        transInfoGrid.add(transParamInfoTableView, 0, rowIndex, 4, 1);
        //endregion

        gridPane.add(addWayTPan, 0, 0);
        gridPane.add(transInfoTPan, 0, 1);

    }

    private void bindAction() {
        //根据控制点文件计算
        radio1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (radio1.isSelected()) {
                    //计算控制点界面可见
                    gcpLabel.setVisible(true);
                    gcpText.setVisible(true);
                    gcpBtn.setVisible(true);
                    viewBtn.setVisible(true);
                    //导入转换项内容不可见
                    importUrlLabel.setVisible(false);
                    importUrlText.setVisible(false);
                    importUrlBtn.setVisible(false);
                    importTransItemLabel.setVisible(false);
                    importTransItemCombobox.setVisible(false);
                    //参照系可编辑
                    srcSRefCombo.setDisable(false);
                    desSRefCombo.setDisable(false);
                    //单位可见
                    srcUnitLabel.setVisible(true);
                    desUnitLabel.setVisible(true);
                    srcUnitCombo.setVisible(true);
                    desUnitCombo.setVisible(true);
                    //转换方法可编辑
                    transMethodCombo.setDisable(false);
                    //参数值不可编辑
                    transParamInfoTableView.getColumns().get(1).setEditable(false);
                    transMethodItems.clear();
                    transMethodItems.addAll("三参数直角平移法", "七参数bursawol法");
                    transMethodCombo.getSelectionModel().select(0);

                    if (gcpText.getText() != null && gcpText.getText().length() >0)
                        calculateButton.setDisable(false);
                        //参照系可选择
                        srcSRefCombo.setDisable(false);
                    desSRefCombo.setDisable(false);
                    transMethodCombo.setDisable(false);
                    //单位可选择
                    srcUnitCombo.setDisable(false);
                    desUnitCombo.setDisable(false);

                    srcSRefCombo.getSelectionModel().select(0);
                    desSRefCombo.getSelectionModel().select(0);
                    transMethodCombo.getSelectionModel().select(0);
                    transParamInfoObservableList.clear();
                    transParamInfoObservableList.add(new TransParamInfo("△X", ""));
                    transParamInfoObservableList.add(new TransParamInfo("△Y", ""));
                    transParamInfoObservableList.add(new TransParamInfo("△Z", ""));
                    transParamInfoTableView.refresh();
                }
            }
        });
        //手动输入
        radio2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (radio2.isSelected()) {
                    //计算控制点界面不可见
                    gcpLabel.setVisible(false);
                    gcpText.setVisible(false);
                    gcpBtn.setVisible(false);
                    viewBtn.setVisible(false);
                    //导入转换项内容不可见
                    importUrlLabel.setVisible(false);
                    importUrlText.setVisible(false);
                    importUrlBtn.setVisible(false);
                    importTransItemLabel.setVisible(false);
                    importTransItemCombobox.setVisible(false);
                    //参照系可编辑
                    srcSRefCombo.setDisable(false);
                    desSRefCombo.setDisable(false);
                    //单位不可见
                    srcUnitLabel.setVisible(false);
                    desUnitLabel.setVisible(false);
                    srcUnitCombo.setVisible(false);
                    desUnitCombo.setVisible(false);
                    //转换方法可编辑
                    transMethodCombo.setDisable(false);
                    //计算按钮不可用
                    calculateButton.setDisable(true);
                    //参数值可编辑
                    transParamInfoTableView.getColumns().get(1).setEditable(true);
                    transMethodItems.clear();
                    transMethodItems.addAll(
                            "三参数直角平移法", "七参数bursawol法", "小区域微分平展法", "三参数经纬平移法",
                            "二维平面坐标转换法", "自定义");

                    srcSRefCombo.getSelectionModel().select(0);
                    desSRefCombo.getSelectionModel().select(0);
                    transMethodCombo.setItems(transMethodItems);
                    transMethodCombo.getSelectionModel().select(0);
                    transParamInfoObservableList.clear();
                    transParamInfoObservableList.add(new TransParamInfo("△X", ""));
                    transParamInfoObservableList.add(new TransParamInfo("△Y", ""));
                    transParamInfoObservableList.add(new TransParamInfo("△Z", ""));
                    transParamInfoTableView.refresh();
                }
            }
        });
        //选择导入转换项
        radio3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (radio3.isSelected()) {
                    //计算控制点界面不可见
                    gcpLabel.setVisible(false);
                    gcpText.setVisible(false);
                    gcpBtn.setVisible(false);
                    viewBtn.setVisible(false);
                    //导入转换项内容不可见
                    importUrlLabel.setVisible(true);
                    importUrlText.setVisible(true);
                    importUrlBtn.setVisible(true);
                    importTransItemLabel.setVisible(true);
                    importTransItemCombobox.setVisible(true);
                    //参照系不可编辑
                    srcSRefCombo.setDisable(true);
                    desSRefCombo.setDisable(true);
                    //转换方法不可编辑
                    transMethodCombo.setDisable(true);
                    //单位不可见
                    srcUnitLabel.setVisible(false);
                    desUnitLabel.setVisible(false);
                    srcUnitCombo.setVisible(false);
                    desUnitCombo.setVisible(false);
                    //计算按钮不可用
                    calculateButton.setDisable(true);
                    //参数值不可编辑
                    transParamInfoTableView.getColumns().get(1).setEditable(false);
                    transMethodItems.clear();
                    transMethodItems.addAll(
                            "三参数直角平移法", "七参数bursawol法", "小区域微分平展法", "三参数经纬平移法",
                            "二维平面坐标转换法", "自定义");
                    transMethodCombo.setItems(transMethodItems);
                    transMethodCombo.getSelectionModel().select(0);
                    int index = importTransItemCombobox.getSelectionModel().getSelectedIndex();
                    if (index > -1) {
                        if (importTransParamItems == null || importTransParamItems.size() == 0)
                            return;
                        elpTransParam = importTransParamItems.get(index);
                        srcSRefCombo.getSelectionModel().select(elpTransParam.getInCord());
                        desSRefCombo.getSelectionModel().select(elpTransParam.getOutCord());
                        transNameText.setText(elpTransParam.getTransName());
                        transMethodCombo.getSelectionModel().select(elpTransParam.getType());
                        refreshTransParamTableView(elpTransParam);
                    }
                }
            }
        });
        //切换转换方法
        transMethodCombo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                int index = transMethodCombo.getSelectionModel().getSelectedIndex();
                if (index == 0) {
                    transParamInfoObservableList.clear();
                    transParamInfoObservableList.addAll(new TransParamInfo("△X", ""));
                    transParamInfoObservableList.addAll(new TransParamInfo("△Y", ""));
                    transParamInfoObservableList.addAll(new TransParamInfo("△Z", ""));
                    transParamInfoTableView.setItems(transParamInfoObservableList);
                    transParamInfoTableView.refresh();
                } else if (index == 1) {
                    transParamInfoObservableList.clear();
                    transParamInfoObservableList.addAll(new TransParamInfo("△X", ""));
                    transParamInfoObservableList.addAll(new TransParamInfo("△Y", ""));
                    transParamInfoObservableList.addAll(new TransParamInfo("△Z", ""));
                    transParamInfoObservableList.addAll(new TransParamInfo("Wx", ""));
                    transParamInfoObservableList.addAll(new TransParamInfo("Wy", ""));
                    transParamInfoObservableList.addAll(new TransParamInfo("Wz", ""));
                    transParamInfoObservableList.addAll(new TransParamInfo("dm", ""));
                    transParamInfoTableView.setItems(transParamInfoObservableList);
                    transParamInfoTableView.refresh();
                } else if (index == 2) {
                    transParamInfoObservableList.clear();
                    transParamInfoObservableList.addAll(new TransParamInfo("dS", ""));
                    transParamInfoObservableList.addAll(new TransParamInfo("dA", ""));
                    transParamInfoObservableList.addAll(new TransParamInfo("df", ""));
                    transParamInfoObservableList.addAll(new TransParamInfo("L1", ""));
                    transParamInfoObservableList.addAll(new TransParamInfo("B1", ""));
                    transParamInfoObservableList.addAll(new TransParamInfo("dL1", ""));
                    transParamInfoObservableList.addAll(new TransParamInfo("dB1", ""));
                    transParamInfoTableView.setItems(transParamInfoObservableList);
                    transParamInfoTableView.refresh();
                } else if (index == 3) {
                    transParamInfoObservableList.clear();
                    transParamInfoObservableList.addAll(new TransParamInfo("dB", ""));
                    transParamInfoObservableList.addAll(new TransParamInfo("dL", ""));
                    transParamInfoObservableList.addAll(new TransParamInfo("dh", ""));
                    transParamInfoTableView.setItems(transParamInfoObservableList);
                    transParamInfoTableView.refresh();
                } else if (index == 4) {
                    transParamInfoObservableList.clear();
                    transParamInfoObservableList.addAll(new TransParamInfo("△X", ""));
                    transParamInfoObservableList.addAll(new TransParamInfo("△Y", ""));
                    transParamInfoObservableList.addAll(new TransParamInfo("θ", ""));
                    transParamInfoObservableList.addAll(new TransParamInfo("mx", ""));
                    transParamInfoObservableList.addAll(new TransParamInfo("my", ""));
                    transParamInfoTableView.setItems(transParamInfoObservableList);
                    transParamInfoTableView.refresh();
                } else if (index == 5) {
                    transParamInfoObservableList.clear();
                    transParamInfoObservableList.addAll(new TransParamInfo("X'=", "2*x*x+5*x*y+8"));
                    transParamInfoObservableList.addAll(new TransParamInfo("Y'=", "2*x*x+5*x*y+8"));
                    transParamInfoObservableList.addAll(new TransParamInfo("Z'=", "4*x*x+6*x*y+7"));
                    transParamInfoTableView.setItems(transParamInfoObservableList);
                    transParamInfoTableView.refresh();
                }
            }
        });
        //切换源参照系-更新转换名称
        srcSRefCombo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateTransName();
            }
        });
        //切换目的参照系-更新转换名称
        desSRefCombo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateTransName();
            }
        });
        //添加控制点文件
        gcpBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //region cpt文件新旧格式描述
            /* .cpt文件新旧格式描述:
             * 旧格式:
             * --------------------------------------------------------------------------------------------------------------
             * 控制点数=4   输入坐标=3  输入单位=3  输出坐标=0  输出单位=3  维数=3
             * 1 x=109867.344044023    y=411828.354073751    z=1    xp=109867.666269992    yp=411832.637139969    zp=1
             * 2 x=109876.801691908    y=411815.844101136    z=1    xp=109877.124069992    yp=411820.127189969    zp=1
             * 3 x=109905.281179077    y=411813.636071255    z=1    xp=109905.603469992    yp=411817.919429969    zp=1
             * 4 x=109913.774820479    y=411820.670590685    z=1    xp=109914.097179992    yp=411824.954189969    zp=1
             * --------------------------------------------------------------------------------------------------------------
             * 其中:
             *   1.首行(控制...)可有可无。
             *   2.旧格式读取数据条件: 每行必须按照顺序依次包含 { "x=", "y=", "z=", "xp=", "yp=", "zp=" } 六项
             *   3.除数据行，其他行可任意书写任意排列，但不能符合旧格式读取数据条件。
             *
             * 新格式:
             * --------------------------------------------------------------------------------------------------------------
             * B               , L               , H, Bp              , Lp              , Hp
             * 411828.354073751, 109867.344044023, 1, 411832.637139969, 109867.666269992, 1
             * 411815.844101136, 109876.801691908, 1, 411820.127189969, 109877.124069992, 1
             * 411813.636071255, 109905.281179077, 1, 411817.919429969, 109905.603469992, 1
             * 411820.670590685, 109913.774820479, 1, 411824.954189969, 109914.097179992, 1
             * --------------------------------------------------------------------------------------------------------------
             * 其中:
             *   1.首行必须注明参数为: { B, L, H, Bp, Lp, Hp }，与原格式中 { "y=", "x=", "z=", "yp=", "xp=", "zp=" } 一一对应。
             *   2.首行前面可有空行。
             *   3.数据行按照参数顺序排列
             *   4.数据行中，数据间使用 { ',', ':', ';', '，', '：', '；' } 六种间隔符均可，也无需在意空格及制表符。
             *   5.可通过 excel 编辑好后，导出为CSV文件后，修改文件后缀为 .cpt 生成。
             */
                //endregion
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("公共点文件(*.cpt)", "*.cpt"));
                File file = fileChooser.showOpenDialog(stage);
                if (file != null) {
                    boolean isSucceed = true;
                    gcpText.setText(file.getAbsolutePath());
                    lstpnt3.clear();
                    ArrayList<String> lines = null;

                    //region 逐行读取文件内容
                    FileInputStream fileInputStream = null;
                    try {
                        fileInputStream = new FileInputStream(file);
                        BufferedReader bufferedReader = null;
                        try {
                            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, "GB2312"));
                            lines = new ArrayList<String>();
                            String str = null;
                            try {
                                while ((str = bufferedReader.readLine()) != null) {
                                    if (str.isEmpty() || str.replace(" ", "").replace("\t", "").isEmpty()) {
                                        continue;
                                    } else {
                                        String line = str.toLowerCase().replace(" ", "").replace("\t", "");
                                        if (line != null && line.length() > 0)
                                            lines.add(line);
                                    }
                                }
                                bufferedReader.close();
                            } catch (IOException e) {
                            } finally {
                                try {
                                    if (bufferedReader != null)
                                        bufferedReader.close();
                                    if (fileInputStream != null)
                                        fileInputStream.close();
                                } catch (Exception e) {

                                }
                            }

                        } catch (UnsupportedEncodingException ex) {

                        } finally {
                            try {
                                if (bufferedReader != null)
                                    bufferedReader.close();
                                if (fileInputStream != null)
                                    fileInputStream.close();
                            } catch (Exception e) {

                            }
                        }

                    } catch (FileNotFoundException ex) {

                    } finally {
                        try {
                            if (fileInputStream != null)
                                fileInputStream.close();
                        } catch (Exception e) {

                        }
                    }
                    //endregion

                    if (lines.size() > 0) {
                        // 通过是否是新格式(第一行必须包含:'B','L','H','Bp','Lp','Hp')来判断 .cpt 文件为新格式还是旧格式
                        // 旧格式数据行必须包含:"x=","y=","z=","xp=","yp=","zp="
                        String firstLine = lines.get(0);
                        if (firstLine.contains("b") && firstLine.contains("l") && firstLine.contains("h")
                                && firstLine.contains("bp") && firstLine.contains("lp") && firstLine.contains("hp")) {
                            // 新格式
                            for (int i = 1; i < lines.size(); i++) {
                                String[] strs = lines.get(i).split(",|:|;|，|：|；");
                                //String[] strs = lines.get(i).split(new char[] { ',', ';', ':', '，', '；', '：' }, StringSplitOptions.RemoveEmptyEntries);
                                try {
//                                    System.out.println("新格式控制点记录：" + lines.get(i));
                                    Pnt3Struct pnt3 = new Pnt3Struct();
//                                    System.out.println("新格式");
//                                    System.out.println("x:" + strs[1]);
//                                    System.out.println("y:" + strs[0]);
//                                    System.out.println("z:" + strs[2]);
//                                    System.out.println("xp:" + strs[4]);
//                                    System.out.println("yp:" + strs[3]);
//                                    System.out.println("zp:" + strs[5]);

                                    pnt3.setX(Double.valueOf(strs[1]));
                                    pnt3.setY(Double.valueOf(strs[0]));
                                    pnt3.setZ(Double.valueOf(strs[2]));

                                    pnt3.setXP(Double.valueOf(strs[4]));
                                    pnt3.setYP(Double.valueOf(strs[3]));
                                    pnt3.setZP(Double.valueOf(strs[5]));

                                    lstpnt3.add(pnt3);
                                } catch (Exception ex) {
                                    calculateButton.setDisable(true);
                                    Alert alert = new Alert(Alert.AlertType.WARNING, "控制点文件编写有误，请修改。");
                                    alert.show();
                                    return;
                                }
                            }
                        } else {
                            // 旧格式
                            for (int i = 0; i < lines.size(); i++) {
                                String line = lines.get(i);
                                if (line.contains("x=") && line.contains("y=") && line.contains("z=")
                                        && line.contains("xp=") && line.contains("yp=") && line.contains("zp=")) {
                                    try {
                                        //line = line.Remove(0, line.IndexOf('x'));
//                                        System.out.println("旧格式控制点记录：" + line);
                                        line = line.substring(line.indexOf('x'));
                                        line = line.replace("x=", "");
                                        line = line.replace("y=", ",");
                                        line = line.replace("z=", ",");
                                        line = line.replace("xp=", ",");
                                        line = line.replace("yp=", ",");
                                        line = line.replace("zp=", ",");
                                        String[] strs = line.split(",");
//                                        System.out.println("旧格式");
//                                        System.out.println("x:" + strs[0]);
//                                        System.out.println("y:" + strs[1]);
//                                        System.out.println("z:" + strs[2]);
//                                        System.out.println("xp:" + strs[3]);
//                                        System.out.println("yp:" + strs[4]);
//                                        System.out.println("zp:" + strs[5]);

                                        Pnt3Struct pnt3 = new Pnt3Struct();
//                                        System.out.println("pnt3 handle");
//                                        System.out.println( pnt3.getHandle());
//                                        System.out.println("getx");
//                                        System.out.println(pnt3.getX());
                                        pnt3.setX(Double.valueOf(strs[0]));
                                        pnt3.setY(Double.valueOf(strs[1]));
                                        pnt3.setZ(Double.valueOf(strs[2]));

                                        pnt3.setXP(Double.valueOf(strs[3]));
                                        pnt3.setYP(Double.valueOf(strs[4]));
                                        pnt3.setZP(Double.valueOf(strs[5]));

                                        lstpnt3.add(pnt3);

                                    } catch (Exception ex) {
                                        calculateButton.setDisable(true);
                                        Alert alert = new Alert(Alert.AlertType.WARNING, "控制点文件为旧格式且编写有误，请修改。");
                                        alert.show();
                                        return;

                                    }
                                }
                            }
                        }
                    }
                    if (lstpnt3.size() == 0) {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "控制点文件编写有误，请修改。");
                        alert.show();
                    }
                    calculateButton.setDisable(!isSucceed);
                }
            }
        });

        //region 查看按钮
        //endregion
        viewBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String file = gcpText.getText();
                if(file != null && file.length() >0)
                {
                    try {
                        Runtime run = Runtime.getRuntime();
                        if (XFunctions.isSystemWindows())
                            run.exec(String.format("notepad %s", file));
                        else
                            run.exec(String.format("deepin-editor %s", file));
                    } catch (IOException ex) {
                    }
                }
            }
        });
        //导入转换项文件
        importUrlBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("转换项文件(*.dat)", "*.dat"));
                File file = fileChooser.showOpenDialog(stage);
                if (file != null) {
                    if (importTransParamItems != null)
                        importTransParamItems = null;
                    importTransParamItems = new ArrayList<ElpTransParam>();
                    importUrlText.setText(file.getAbsolutePath());
                    importTransItemList.clear();
                    importTransParamItems.clear();
                    //ElpTransformation elpTrans1 = new ElpTransformation();
                    if (!ElpTransformation.loadElpTransParam(file.getAbsolutePath())) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "读取文件内容失败，请检查转换项文件格式或内容。");
                        alert.show();
                        return;
                    }
                    for (int i = 0; i < ElpTransformation.getElpTransParamCount(); i++) {
                        importTransItemList.addAll(ElpTransformation.getElpTransParam(i).getTransName());
                        importTransParamItems.add(ElpTransformation.getElpTransParam(i));
                    }
                    if (importTransItemList.size() > 0)
                        importTransItemCombobox.getSelectionModel().select(0);
                }
            }
        });
        //切换转换下拉项
        importTransItemCombobox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                int index = importTransItemCombobox.getSelectionModel().getSelectedIndex();
                if (index > -1) {
                    if (importTransParamItems == null || importTransParamItems.size() == 0)
                        return;
                    elpTransParam = importTransParamItems.get(index);
                    srcSRefCombo.getSelectionModel().select(elpTransParam.getInCord());
                    desSRefCombo.getSelectionModel().select(elpTransParam.getOutCord());
                    transMethodCombo.getSelectionModel().select(elpTransParam.getType());
                    transNameText.setText(elpTransParam.getTransName());
                    refreshTransParamTableView(elpTransParam);
                }
            }
        });

    }

    //region 内部方法

    /**
     * 单击选中转换项列表更新对应的转换参数列表
     */
    private void refreshTransParamTableView(ElpTransParam param) {
        if (param.getType() == 0) {
            this.transParamInfoObservableList.clear();
            transParamInfoObservableList.addAll(new TransParamInfo("△X", String.valueOf(param.getDx())));
            transParamInfoObservableList.addAll(new TransParamInfo("△Y", String.valueOf(param.getDy())));
            transParamInfoObservableList.addAll(new TransParamInfo("△Z", String.valueOf(param.getDz())));
            this.transParamInfoTableView.refresh();
        } else if (param.getType() == 1) {
            this.transParamInfoObservableList.clear();
            transParamInfoObservableList.addAll(new TransParamInfo("△X", String.valueOf(param.getDx())));
            transParamInfoObservableList.addAll(new TransParamInfo("△Y", String.valueOf(param.getDy())));
            transParamInfoObservableList.addAll(new TransParamInfo("△Z", String.valueOf(param.getDz())));
            transParamInfoObservableList.addAll(new TransParamInfo("Wx", String.valueOf(param.getWx())));
            transParamInfoObservableList.addAll(new TransParamInfo("Wy", String.valueOf(param.getWy())));
            transParamInfoObservableList.addAll(new TransParamInfo("Wz", String.valueOf(param.getWz())));
            transParamInfoObservableList.addAll(new TransParamInfo("dm", String.valueOf(param.getM())));
            this.transParamInfoTableView.refresh();
        } else if (param.getType() == 2) {
            this.transParamInfoObservableList.clear();
            transParamInfoObservableList.addAll(new TransParamInfo("dS", String.valueOf(param.getDx())));
            transParamInfoObservableList.addAll(new TransParamInfo("dA", String.valueOf(param.getDy())));
            transParamInfoObservableList.addAll(new TransParamInfo("df", String.valueOf(param.getDz())));
            transParamInfoObservableList.addAll(new TransParamInfo("L1", String.valueOf(param.getWx())));
            transParamInfoObservableList.addAll(new TransParamInfo("B1", String.valueOf(param.getWy())));
            transParamInfoObservableList.addAll(new TransParamInfo("dL1", String.valueOf(param.getWz())));
            transParamInfoObservableList.addAll(new TransParamInfo("dB1", String.valueOf(param.getM())));
            this.transParamInfoTableView.refresh();
        } else if (param.getType() == 3) {
            this.transParamInfoObservableList.clear();
            transParamInfoObservableList.addAll(new TransParamInfo("dB", String.valueOf(param.getDx())));
            transParamInfoObservableList.addAll(new TransParamInfo("dL", String.valueOf(param.getDy())));
            transParamInfoObservableList.addAll(new TransParamInfo("dh", String.valueOf(param.getDz())));
            this.transParamInfoTableView.refresh();
        } else if (param.getType() == 4) {
            this.transParamInfoObservableList.clear();
            transParamInfoObservableList.addAll(new TransParamInfo("△X", String.valueOf(param.getDx())));
            transParamInfoObservableList.addAll(new TransParamInfo("△Y", String.valueOf(param.getDy())));
            transParamInfoObservableList.addAll(new TransParamInfo("θ", String.valueOf(param.getDz())));
            transParamInfoObservableList.addAll(new TransParamInfo("mx", String.valueOf(param.getWx())));
            transParamInfoObservableList.addAll(new TransParamInfo("my", String.valueOf(param.getWy())));
            this.transParamInfoTableView.refresh();
        } else if (param.getType() == 5) {
            this.transParamInfoObservableList.clear();
            transParamInfoObservableList.addAll(new TransParamInfo("X'=", String.valueOf(param.getEquationX())));
            transParamInfoObservableList.addAll(new TransParamInfo("Y'=", String.valueOf(param.getEquationY())));
            transParamInfoObservableList.addAll(new TransParamInfo("Z'=", String.valueOf(param.getEquationZ())));
            this.transParamInfoTableView.refresh();
        } else {
            this.transParamInfoObservableList.clear();
            transParamInfoObservableList.addAll(new TransParamInfo("△X", ""));
            transParamInfoObservableList.addAll(new TransParamInfo("△Y", ""));
            transParamInfoObservableList.addAll(new TransParamInfo("△Z", ""));
            this.transParamInfoTableView.refresh();
        }
    }

    /**
     * 根据源和目的参考系类型解析转换名称
     */
    private void updateTransName() {
        String transname = this.srcSRefCombo.getSelectionModel().getSelectedItem();
        String transname1 = this.desSRefCombo.getSelectionModel().getSelectedItem();
        if (transname != null && transname1 != null) {
            if (transname.contains("/"))
                transname = transname.substring(0, transname.lastIndexOf('/'));
            int index = transname.indexOf(":");
            if (index > -1)
                transname = transname.substring(index + 1);
//            String[] strs = transname.split(':');
//            transname = strs[1];
            if (transname1.contains("/"))
                transname1 = transname1.substring(0, transname1.lastIndexOf('/'));
            int index1 = transname1.indexOf(":");
            if (index1 > -1)
                transname1 = transname1.substring(index1 + 1);
//            string[] strs1 = transname1.Split(':');
//            transname1 = strs1[1];
            this.transNameText.setText(transname.trim() + "->" + transname1.trim());
        }
    }

    //检查手动输入参数值合法性
    private boolean lookOutGridViewValue() {
        boolean rtn = true;
        if (this.transMethodCombo.getSelectionModel().getSelectedIndex() == 0) {
            String strRow0 = this.transParamInfoObservableList.get(0).getParamValue();
            String strRow1 = this.transParamInfoObservableList.get(1).getParamValue();
            String strRow2 = this.transParamInfoObservableList.get(2).getParamValue();

            if (strRow0.trim() == "" || strRow1.trim() == "" || strRow2.trim() == "") {
                rtn = false;
                return rtn;
            }
            try {
                Double.valueOf(strRow0);
                Double.valueOf(strRow1);
                Double.valueOf(strRow2);
            } catch (Exception ex) {
                rtn = false;
                return rtn;
            }
        } else if (this.transMethodCombo.getSelectionModel().getSelectedIndex() == 1) {
            String strRow0 = this.transParamInfoObservableList.get(0).getParamValue();
            String strRow1 = this.transParamInfoObservableList.get(1).getParamValue();
            String strRow2 = this.transParamInfoObservableList.get(2).getParamValue();
            String strRow3 = this.transParamInfoObservableList.get(3).getParamValue();
            String strRow4 = this.transParamInfoObservableList.get(4).getParamValue();
            String strRow5 = this.transParamInfoObservableList.get(5).getParamValue();
            String strRow6 = this.transParamInfoObservableList.get(6).getParamValue();
            if (strRow0.trim() == "" || strRow1.trim() == "" || strRow2.trim() == "" || strRow3.trim() == "" || strRow4.trim() == "" || strRow5.trim() == "" || strRow6.trim() == "") {
                rtn = false;
                return rtn;
            }
            try {
                Double.valueOf(strRow0);
                Double.valueOf(strRow1);
                Double.valueOf(strRow2);
                Double.valueOf(strRow3);
                Double.valueOf(strRow4);
                Double.valueOf(strRow5);
                Double.valueOf(strRow6);
            } catch (Exception ex) {
                rtn = false;
                return rtn;
            }
        } else if (this.transMethodCombo.getSelectionModel().getSelectedIndex() == 2) {
            String strRow0 = this.transParamInfoObservableList.get(0).getParamValue();
            String strRow1 = this.transParamInfoObservableList.get(1).getParamValue();
            String strRow2 = this.transParamInfoObservableList.get(2).getParamValue();
            String strRow3 = this.transParamInfoObservableList.get(3).getParamValue();
            String strRow4 = this.transParamInfoObservableList.get(4).getParamValue();
            String strRow5 = this.transParamInfoObservableList.get(5).getParamValue();
            String strRow6 = this.transParamInfoObservableList.get(6).getParamValue();
            if (strRow0.trim() == "" || strRow1.trim() == "" || strRow2.trim() == "" || strRow3.trim() == "" || strRow4.trim() == "" || strRow5.trim() == "" || strRow6.trim() == "") {
                rtn = false;
                return rtn;
            }
            try {
                Double.valueOf(strRow0);
                Double.valueOf(strRow1);
                Double.valueOf(strRow2);
                Double.valueOf(strRow3);
                Double.valueOf(strRow4);
                Double.valueOf(strRow5);
                Double.valueOf(strRow6);
            } catch (Exception ex) {
                rtn = false;
                return rtn;
            }
        } else if (this.transMethodCombo.getSelectionModel().getSelectedIndex() == 3) {
            String strRow0 = this.transParamInfoObservableList.get(0).getParamValue();
            String strRow1 = this.transParamInfoObservableList.get(1).getParamValue();
            String strRow2 = this.transParamInfoObservableList.get(2).getParamValue();

            if (strRow0.trim() == "" || strRow1.trim() == "" || strRow2.trim() == "") {
                rtn = false;
                return rtn;
            }
            try {
                Double.valueOf(strRow0);
                Double.valueOf(strRow1);
                Double.valueOf(strRow2);
            } catch (Exception ex) {
                rtn = false;
                return rtn;
            }
        } else if (this.transMethodCombo.getSelectionModel().getSelectedIndex() == 4) {
            String strRow0 = this.transParamInfoObservableList.get(0).getParamValue();
            String strRow1 = this.transParamInfoObservableList.get(1).getParamValue();
            String strRow2 = this.transParamInfoObservableList.get(2).getParamValue();
            String strRow3 = this.transParamInfoObservableList.get(3).getParamValue();
            String strRow4 = this.transParamInfoObservableList.get(4).getParamValue();
            if (strRow0.trim() == "" || strRow1.trim() == "" || strRow2.trim() == "" || strRow3.trim() == "" || strRow4.trim() == "") {
                rtn = false;
                return rtn;
            }
            try {
                Double.valueOf(strRow0);
                Double.valueOf(strRow1);
                Double.valueOf(strRow2);
                Double.valueOf(strRow3);
                Double.valueOf(strRow4);
            } catch (Exception ex) {
                rtn = false;
                return rtn;
            }
        }
        return rtn;
    }

    private void modifyParam(ElpTransParam param) {
        short in_unit = 0;
        switch (srcUnitCombo.getSelectionModel().getSelectedIndex()) {
            case 0:
                in_unit = 4;
                break;
            case 1:
                in_unit = 7;
                break;
            case 2:
                in_unit = 3;
                break;
            case 3:
                in_unit = 5;
                break;
        }
        short out_unit = 0;
        switch (desUnitCombo.getSelectionModel().getSelectedIndex()) {
            case 0:
                out_unit = 4;
                break;
            case 1:
                out_unit = 7;
                break;
            case 2:
                out_unit = 3;
                break;
            case 3:
                out_unit = 5;
                break;
        }
        param.setAngunit((short) 0);
        param.setLenunit((short) 1);
        param.setEquationX("");
        param.setEquationY("");
        param.setEquationZ("");
        param.setInCord((short) srcSRefCombo.getSelectionModel().getSelectedIndex());
        param.setInUnit(in_unit);
        param.setOutCord((short) desSRefCombo.getSelectionModel().getSelectedIndex());
        param.setOutUnit(out_unit);
        param.setType((short) transMethodCombo.getSelectionModel().getSelectedIndex());
        param.setTransName(transNameText.getText());
        if (param.getType() == 0) {
            param.setDx(Double.valueOf(this.transParamInfoObservableList.get(0).getParamValue()));
            param.setDy(Double.valueOf(this.transParamInfoObservableList.get(1).getParamValue()));
            param.setDz(Double.valueOf(this.transParamInfoObservableList.get(2).getParamValue()));
        } else if (param.getType() == 1) {
            param.setDx(Double.valueOf(this.transParamInfoObservableList.get(0).getParamValue()));
            param.setDy(Double.valueOf(this.transParamInfoObservableList.get(1).getParamValue()));
            param.setDz(Double.valueOf(this.transParamInfoObservableList.get(2).getParamValue()));
            param.setWx(Double.valueOf(this.transParamInfoObservableList.get(3).getParamValue()));
            param.setWy(Double.valueOf(this.transParamInfoObservableList.get(4).getParamValue()));
            param.setWz(Double.valueOf(this.transParamInfoObservableList.get(5).getParamValue()));
            param.setM(Double.valueOf(this.transParamInfoObservableList.get(6).getParamValue()));

        } else if (param.getType() == 2) {
            param.setDx(Double.valueOf(this.transParamInfoObservableList.get(0).getParamValue()));
            param.setDy(Double.valueOf(this.transParamInfoObservableList.get(1).getParamValue()));
            param.setDz(Double.valueOf(this.transParamInfoObservableList.get(2).getParamValue()));
            param.setWx(Double.valueOf(this.transParamInfoObservableList.get(3).getParamValue()));
            param.setWy(Double.valueOf(this.transParamInfoObservableList.get(4).getParamValue()));
            param.setWz(Double.valueOf(this.transParamInfoObservableList.get(5).getParamValue()));
            param.setM(Double.valueOf(this.transParamInfoObservableList.get(6).getParamValue()));
        } else if (param.getType() == 3) {
            param.setDx(Double.valueOf(this.transParamInfoObservableList.get(0).getParamValue()));
            param.setDy(Double.valueOf(this.transParamInfoObservableList.get(1).getParamValue()));
            param.setDz(Double.valueOf(this.transParamInfoObservableList.get(2).getParamValue()));
        } else if (param.getType() == 4) {
            param.setDx(Double.valueOf(this.transParamInfoObservableList.get(0).getParamValue()));
            param.setDy(Double.valueOf(this.transParamInfoObservableList.get(1).getParamValue()));
            param.setDz(Double.valueOf(this.transParamInfoObservableList.get(2).getParamValue()));
            param.setWx(Double.valueOf(this.transParamInfoObservableList.get(3).getParamValue()));
            param.setWy(Double.valueOf(this.transParamInfoObservableList.get(4).getParamValue()));
        } else if (param.getType() == 5) {
            param.setEquationX(this.transParamInfoObservableList.get(0).getParamValue());
            param.setEquationY(this.transParamInfoObservableList.get(0).getParamValue());
            param.setEquationZ(this.transParamInfoObservableList.get(0).getParamValue());
        }
    }


    //endregion

    // 确定按钮
    private void okButtonClick(ActionEvent event) {
        if (radio2.isSelected()) {
            if (!lookOutGridViewValue()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "手动输入转换参数不合法，请重新输入。");
                alert.show();
                event.consume();
                return;
            }
            if (elpTransParam != null)
                elpTransParam = null;
            elpTransParam = new ElpTransParam();
            modifyParam(elpTransParam);
        } else {
            if (elpTransParam == null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "请输入完整的转换参数。");
                alert.show();
                event.consume();
                return;
            } else {
                elpTransParam.setTransName(transNameText.getText().trim());
            }
        }

        boolean flag = true;
        if (currentTransNameList != null && currentTransNameList.size() > 0) {
            for (int i = 0; i < currentTransNameList.size(); i++) {

                if (transNameText.getText() == currentTransNameList.get(i).trim()) {
                    flag = false;
                    break;
                }
            }
        }
        if (!flag) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "转换项列表中已存在同名转换项，请修改此转换项名称。");
            alert.show();
            event.consume();
            return;
        }

    }

    // 计算按钮
    private void calculateButtonClick(ActionEvent event) {
        if (lstpnt3.size() == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "控制点数为0，请重新选择控制点文件。");
            alert.show();
            event.consume();
            return;
        }
        short in_unit = 0;
        switch (srcUnitCombo.getSelectionModel().getSelectedIndex()) {
            case 0:
                in_unit = 4;
                break;
            case 1:
                in_unit = 7;
                break;
            case 2:
                in_unit = 3;
                break;
            case 3:
                in_unit = 5;
                break;
        }
        short out_unit = 0;
        switch (desUnitCombo.getSelectionModel().getSelectedIndex()) {
            case 0:
                out_unit = 4;
                break;
            case 1:
                out_unit = 7;
                break;
            case 2:
                out_unit = 3;
                break;
            case 3:
                out_unit = 5;
                break;
        }
        if (elpTransParam != null)
            elpTransParam = null;

        Pnt3Struct[] pnt3StructsArr = new Pnt3Struct[lstpnt3.size()];
        for(int i=0;i<lstpnt3.size();i++)
        {
            pnt3StructsArr[i] = lstpnt3.get(i);
        }
        elpTransParam = ElpTransformation.countCoeByPntList(pnt3StructsArr, (short) transMethodCombo.getSelectionModel().getSelectedIndex(), (short) (srcSRefCombo.getSelectionModel().getSelectedIndex() + 1), in_unit, (short) (this.desSRefCombo.getSelectionModel().getSelectedIndex() + 1), out_unit);
        if (elpTransParam != null) {
            //param.Angunit = 0;
            //param.Lenunit = 1;
            //param.InCord = this.comboBoxEdit1.SelectedIndex;
            //param.InUnit = in_unit;
            //param.OutCord = this.comboBoxEdit2.SelectedIndex;
            //param.OutUnit = out_unit;
            //param.Type = this.comboBoxEdit3.SelectedIndex;
            elpTransParam.setTransName(transNameText.getText().trim());
            refreshTransParamTableView(elpTransParam);
        }

        event.consume();
    }

    //转换参数信息
//   public class TransParamInfo {
//        public String paramName;
//        public String paramValue;
//
//        public TransParamInfo(String name, String value) {
//            paramName = name;
//            paramValue = value;
//        }
//
//        public TransParamInfo() {
//
//        }
//
//        public String getParamName() {
//            return paramName;
//        }
//
//        public void setParamName(String val) {
//            this.paramName = val;
//        }
//
//        public String getParamValue() {
//            return paramValue;
//        }
//
//        public void setParamValue(String val) {
//            this.paramValue = val;
//        }
//    }
}
