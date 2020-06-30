package com.zondy.mapgis.mapeditor.projecttransform;

import com.zondy.mapgis.base.GISDefaultValues;
import com.zondy.mapgis.base.UIFunctions;
import com.zondy.mapgis.base.XString;
import com.zondy.mapgis.geodatabase.config.EnvConfig;
import com.zondy.mapgis.geodatabase.config.SysConfigDirType;
import com.zondy.mapgis.srs.ElpTransParam;
import com.zondy.mapgis.srs.ElpTransformation;
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
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * 地理转换设置
 */
public class ElpTransSettingDialog extends Dialog<ArrayList<ElpTransParam>> {
    private boolean modify = false;
    private Button applyButton = null;

    public ElpTransSettingDialog() {

        setTitle("地理转换项参数设置");
        //初始化界面布局
        initialize();
        //绑定事件
        bindAction();
        if (srcSRefItems.size() > 0)
            srcSRefCombo.getSelectionModel().select(0);
        if (desSRefItems.size() > 0)
            desSRefCombo.getSelectionModel().select(0);
        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setContent(this.gridPane);
        ButtonType applyType = new ButtonType("应用", ButtonBar.ButtonData.APPLY);
        dialogPane.getButtonTypes().addAll(applyType,ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, this::okButtonClick);
        applyButton = (Button) dialogPane.lookupButton(applyType);
        applyButton.addEventFilter(ActionEvent.ACTION, this::applyButtonClick);
        setDisabled();
        setResultConverter(dialogButton -> dialogButton == ButtonType.OK ? this.elpTransParamArrayList : null);
    }

    private ArrayList<ElpTransParam> elpTransParamArrayList = new ArrayList<>();
    private GridPane gridPane = null;
    //转换项
    private Button btnAdd = null;
    private Button btnModify = null;
    private Button btnDel = null;
    //已存在或新加的的转换项
    private TableView<MyTransItemInfo> transItemInfoTableView = null;
    private ObservableList<MyTransItemInfo> transItemInfoObservableList = null;
    //转换信息
    private ComboBox<String> srcSRefCombo = null;
    private ComboBox<String> desSRefCombo = null;
    private ObservableList<String> srcSRefItems = null;
    private ObservableList<String> desSRefItems = null;
    private TextField transNameText = null;  //转换名称
    private ComboBox<String> transMethodCombo = null; //转换方法下拉项
    private ObservableList<String> transMethodItems = null;

    //转换参数列表
    private ObservableList<TransParamInfo> transParamInfoObservableList = null;
    private TableView<TransParamInfo> transParamInfoTableView = null;

    private void initialize() {
        gridPane = new GridPane();
        gridPane.setPrefWidth(500);
        gridPane.setPrefHeight(550);

        //region 转换项版块

        GridPane transItemGrid = new GridPane();
        transItemGrid.setVgap(5);
        transItemGrid.setHgap(10);
        TitledPane transItemTPan = new TitledPane();
        transItemTPan.setText("转换项");
        transItemTPan.setContent(transItemGrid);
        transItemTPan.setCollapsible(false);
        transItemTPan.setPadding(new Insets(5, 0, 0, 5));

        //添加的转换项列表
        transItemInfoTableView = new TableView<>();
        transItemInfoObservableList = FXCollections.observableArrayList();
        //转换项名称
        TableColumn<MyTransItemInfo, String> tc_TransName = new TableColumn<>("");
        tc_TransName.setEditable(false);
        tc_TransName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MyTransItemInfo, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<MyTransItemInfo, String> param) {
                return new SimpleStringProperty(param.getValue().getTransName());
            }
        });
        //转换对象-隐藏列
        TableColumn<MyTransItemInfo, ElpTransParam> tc_TransParam = new TableColumn<>("");
        tc_TransParam.setVisible(false);

        transItemInfoTableView.getColumns().addAll(tc_TransName, tc_TransParam);

        //初始化已存在的转换项
        loadTransParamList();
        transItemInfoTableView.setItems(transItemInfoObservableList);
        transItemInfoTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        transItemInfoTableView.setPrefWidth(400);
        transItemInfoTableView.setMaxWidth(400);

        transItemGrid.add(transItemInfoTableView, 0, 0, 1, 1);

        //添加、修改、删除

        btnAdd = new Button("添加");
        btnAdd.setPrefWidth(50);
        btnModify = new Button("修改");
        btnModify.setPrefWidth(50);
        btnDel = new Button("删除");
        btnDel.setPrefWidth(50);
//        transItemGrid.add(btnAdd, 1, 0);
//        transItemGrid.add(btnModify, 1, 1);
//        transItemGrid.add(btnDel, 1,2);

        VBox btnVBox = new VBox();
        btnVBox.getChildren().addAll(btnAdd,btnModify,btnDel);
        btnVBox.setSpacing(5);
        transItemGrid.add(btnVBox,1,0);

        //endregion

        //region 坐标系选项-转换信息
        GridPane transInfoGrid = new GridPane();
        transInfoGrid.setVgap(5);
        transInfoGrid.setHgap(5);
        TitledPane transInfoTPan = new TitledPane();
        transInfoTPan.setText("坐标系选项");
        transInfoTPan.setContent(transInfoGrid);
        transInfoTPan.setCollapsible(false);
        transInfoTPan.setPadding(new Insets(5, 0, 0, 5));

        //原坐标系、目的坐标系
        Label srcSRefLabel = new Label("原坐标系:");
        Label desSRefLabel = new Label("目的坐标系:");
        srcSRefCombo = new ComboBox<>();
        srcSRefCombo.setPrefWidth(355);
        srcSRefItems = FXCollections.observableArrayList();
        srcSRefCombo.setItems(srcSRefItems);

        desSRefCombo = new ComboBox<>();
        desSRefCombo.setPrefWidth(355);
        desSRefItems = FXCollections.observableArrayList();
        desSRefCombo.setItems(desSRefItems);
        //添加椭球参考系列表
        for (int i = 1; i <= ElpTransformation.getElpCount(); i++) {
            String name = ElpTransformation.getElpParam(i).getName();
            if (name != null && name.length() > 0) {
                srcSRefItems.addAll(name);
                desSRefItems.addAll(name);
            }
        }
//        if(srcSRefItems.size() >0)
//            srcSRefCombo.getSelectionModel().select(0);
//        if(desSRefItems.size() >0)
//            desSRefCombo.getSelectionModel().select(0);

        //第一行
        int rowIndex = 0;
        transInfoGrid.add(srcSRefLabel, 0, rowIndex);
        transInfoGrid.add(srcSRefCombo, 1, rowIndex);
        //第二行
        rowIndex++;
        transInfoGrid.add(desSRefLabel, 0, rowIndex);
        transInfoGrid.add(desSRefCombo, 1, rowIndex);
        //转换名称、转换方法
        Label transNameLabel = new Label("转换名称:");
        Label transMethodLabel = new Label("转换方法:");
        transNameText = new TextField();
        transNameText.setPrefWidth(355);
        transNameText.setEditable(false); //转换名称不允许编辑
        transMethodCombo = new ComboBox<>();
        transMethodCombo.setPrefWidth(355);
        transMethodItems = FXCollections.observableArrayList();
        transMethodItems.addAll(
                "三参数直角平移法", "七参数bursawol法", "小区域微分平展法", "三参数经纬平移法",
                "二维平面坐标转换法", "自定义");
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
        transParamLabel.setPrefWidth(150);
        transInfoGrid.add(transParamLabel, 0, rowIndex);

        //转换参数列表
        transParamInfoObservableList = FXCollections.observableArrayList();
        transParamInfoTableView = new TableView<>();
        transParamInfoTableView.setPrefWidth(400);
        transParamInfoTableView.setEditable(true);

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
                            StringProperty errorMsg = new SimpleStringProperty();
                            if (!XString.isTextValid(nv, 20, GISDefaultValues.getInvalidNameCharList(), errorMsg))
                            {
                                UIFunctions.showErrorTip(textField, errorMsg.get(), new Tooltip());
                                textField.setText(ov);
                            }
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
        transParamInfoTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //初始化参数列表
        transParamInfoObservableList.add(new TransParamInfo("△X", ""));
        transParamInfoObservableList.add(new TransParamInfo("△Y", ""));
        transParamInfoObservableList.add(new TransParamInfo("△Z", ""));
        transParamInfoTableView.setItems(transParamInfoObservableList);
        //第六行
        rowIndex++;
        transInfoGrid.add(transParamInfoTableView, 0, rowIndex, 4, 1);
        //endregion

        gridPane.add(transItemTPan, 0, 0);
        gridPane.add(transInfoTPan, 0, 1);


    }

    private void bindAction() {
        //切换源参考系-更新转换名称
        srcSRefCombo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateTransName();
            }
        });
        //切换目的参考系-更新转换名称
        desSRefCombo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateTransName();
            }
        });
        //切换转换方法-更新转换参数列表
        transMethodCombo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateTransParamList();
            }
        });
        //单击选中转换项列表-更新界面及对应参数列表
        transItemInfoTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MyTransItemInfo>() {
            @Override
            public void changed(ObservableValue<? extends MyTransItemInfo> observable, MyTransItemInfo oldValue, MyTransItemInfo newValue) {
                updateSelectItemParamInfo();
            }
        });
        //添加地理转换项
        btnAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (transItemInfoObservableList.size() != 0 && transItemInfoTableView.getSelectionModel().getSelectedItems() != null && transItemInfoTableView.getSelectionModel().getSelectedItems().size() != 0 && modify) {
                    //是否保存修改项？
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "是否保存修改项", ButtonType.YES, ButtonType.NO);
                    Optional<ButtonType> optional = alert.showAndWait();
                    if (optional != null && optional.isPresent()) {
                        ButtonType type = optional.get();
                        if (type == ButtonType.YES) {
                            applyItem();
                        }
                    } else {
                        ElpTransParam param = transItemInfoTableView.getSelectionModel().getSelectedItem().getTransParam();
                        srcSRefCombo.getSelectionModel().select(param.getInCord());
                        desSRefCombo.getSelectionModel().select(param.getOutCord());
                        transMethodCombo.getSelectionModel().select(param.getType());
                        transNameText.setText(param.getTransName());
                        setDisabled();
                    }
                }
                ArrayList<String> currentTransNameList = new ArrayList<String>();
                if(transItemInfoObservableList.size() >0)
                {
                    for(int i=0;i<transItemInfoObservableList.size();i++)
                    {
                        currentTransNameList.add(transItemInfoObservableList.get(i).getTransName());
                    }
                }
                AddElpTransParamDialog dialog = new AddElpTransParamDialog(currentTransNameList);
                Optional<ElpTransParam> optional = dialog.showAndWait();
                if (optional != null && optional.isPresent()) {
                    ElpTransParam param = optional.get();
                    if(param != null)
                    {
                        transItemInfoObservableList.addAll(new MyTransItemInfo(param.getTransName(), param));
                        transItemInfoTableView.refresh();
                        transItemInfoTableView.getSelectionModel().select(transItemInfoObservableList.size() -1);
                        srcSRefCombo.getSelectionModel().select(param.getInCord());
                        desSRefCombo.getSelectionModel().select(param.getOutCord());
                        transMethodCombo.getSelectionModel().select(param.getType());
                        transNameText.setText(param.getTransName());
                        refreshTransParamTableView(param);
                    }
                }
            }
        });
        //删除
        btnDel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (transItemInfoObservableList.size() != 0 && transItemInfoTableView.getSelectionModel().getSelectedItems() != null && transItemInfoTableView.getSelectionModel().getSelectedItems().size() != 0) {
                    {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "确定删除此转换项?", ButtonType.YES, ButtonType.NO);
                        Optional<ButtonType> optional = alert.showAndWait();
                        if(optional != null && optional.isPresent())
                        {
                            ButtonType type = optional.get();
                            if (type == ButtonType.YES)
                            {
                                int a = transItemInfoTableView.getSelectionModel().getFocusedIndex();
                                transItemInfoObservableList.remove(a);
                                transItemInfoTableView.refresh();
                                if (transItemInfoObservableList.size() != 0)
                                    transItemInfoTableView.getSelectionModel().select(0);
                                else {
                                    srcSRefCombo.getSelectionModel().select(0);
                                    desSRefCombo.getSelectionModel().select(0);
                                    transMethodCombo.getSelectionModel().select(0);
                                    transParamInfoObservableList.clear();
                                    transParamInfoObservableList.addAll(new TransParamInfo("△X", ""));
                                    transParamInfoObservableList.addAll(new TransParamInfo("△Y", ""));
                                    transParamInfoObservableList.addAll(new TransParamInfo("△Z", ""));
                                    transItemInfoTableView.refresh();
                                    setDisabled();
                                }
                            }
                        }
                    }
                }
            }
        });
        //修改
        btnModify.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setEnabled();
            }
        });
    }

    //region 内部方法

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

    /**
     * 切换转换方法-更新转换参数列表
     */
    private void updateTransParamList() {
        if (this.transItemInfoObservableList.size() == 0)
            return;
        MyTransItemInfo transItemInfo = this.transItemInfoTableView.getSelectionModel().getSelectedItem();
        if (transItemInfo == null)
            return;
        ElpTransParam param = transItemInfo.getTransParam();
        if (param == null)
            return;
        int index = this.transMethodCombo.getSelectionModel().getSelectedIndex();
        if (index == 0) {
            transParamInfoObservableList.clear();
            if (param.getType() == 0) {
                transParamInfoObservableList.addAll(new TransParamInfo("△X", String.valueOf(param.getDx())));
                transParamInfoObservableList.addAll(new TransParamInfo("△Y", String.valueOf(param.getDy())));
                transParamInfoObservableList.addAll(new TransParamInfo("△Z", String.valueOf(param.getDz())));
            } else {
                transParamInfoObservableList.addAll(new TransParamInfo("△X", ""));
                transParamInfoObservableList.addAll(new TransParamInfo("△Y", ""));
                transParamInfoObservableList.addAll(new TransParamInfo("△Z", ""));
            }
            this.transParamInfoTableView.setItems(transParamInfoObservableList);
            this.transParamInfoTableView.refresh();
        } else if (index == 1) {
            transParamInfoObservableList.clear();
            if (param.getType() == 1) {
                transParamInfoObservableList.addAll(new TransParamInfo("△X", String.valueOf(param.getDx())));
                transParamInfoObservableList.addAll(new TransParamInfo("△Y", String.valueOf(param.getDy())));
                transParamInfoObservableList.addAll(new TransParamInfo("△Z", String.valueOf(param.getDz())));
                transParamInfoObservableList.addAll(new TransParamInfo("Wx", String.valueOf(param.getWx())));
                transParamInfoObservableList.addAll(new TransParamInfo("Wy", String.valueOf(param.getWy())));
                transParamInfoObservableList.addAll(new TransParamInfo("Wz", String.valueOf(param.getWz())));
                transParamInfoObservableList.addAll(new TransParamInfo("dm", String.valueOf(param.getM())));
            } else {
                transParamInfoObservableList.addAll(new TransParamInfo("△X", ""));
                transParamInfoObservableList.addAll(new TransParamInfo("△Y", ""));
                transParamInfoObservableList.addAll(new TransParamInfo("△Z", ""));
                transParamInfoObservableList.addAll(new TransParamInfo("Wx", ""));
                transParamInfoObservableList.addAll(new TransParamInfo("Wy", ""));
                transParamInfoObservableList.addAll(new TransParamInfo("Wz", ""));
                transParamInfoObservableList.addAll(new TransParamInfo("dm", ""));

            }
            this.transParamInfoTableView.setItems(transParamInfoObservableList);
            this.transParamInfoTableView.refresh();
        } else if (index == 2) {
            transParamInfoObservableList.clear();
            if (param.getType() == 2) {
                transParamInfoObservableList.addAll(new TransParamInfo("dS", String.valueOf(param.getDx())));
                transParamInfoObservableList.addAll(new TransParamInfo("dA", String.valueOf(param.getDy())));
                transParamInfoObservableList.addAll(new TransParamInfo("df", String.valueOf(param.getDz())));
                transParamInfoObservableList.addAll(new TransParamInfo("L1", String.valueOf(param.getWx())));
                transParamInfoObservableList.addAll(new TransParamInfo("B1", String.valueOf(param.getWy())));
                transParamInfoObservableList.addAll(new TransParamInfo("dL1", String.valueOf(param.getWz())));
                transParamInfoObservableList.addAll(new TransParamInfo("dB1", String.valueOf(param.getM())));
            } else {
                transParamInfoObservableList.addAll(new TransParamInfo("dS", ""));
                transParamInfoObservableList.addAll(new TransParamInfo("dA", ""));
                transParamInfoObservableList.addAll(new TransParamInfo("df", ""));
                transParamInfoObservableList.addAll(new TransParamInfo("L1", ""));
                transParamInfoObservableList.addAll(new TransParamInfo("B1", ""));
                transParamInfoObservableList.addAll(new TransParamInfo("dL1", ""));
                transParamInfoObservableList.addAll(new TransParamInfo("dB1", ""));
            }
            this.transParamInfoTableView.setItems(transParamInfoObservableList);
            this.transParamInfoTableView.refresh();
        } else if (index == 3) {
            this.transParamInfoObservableList.clear();
            if (param.getType() == 3) {
                transParamInfoObservableList.addAll(new TransParamInfo("dB", String.valueOf(param.getDx())));
                transParamInfoObservableList.addAll(new TransParamInfo("dL", String.valueOf(param.getDy())));
                transParamInfoObservableList.addAll(new TransParamInfo("dh", String.valueOf(param.getDz())));
            } else {
                transParamInfoObservableList.addAll(new TransParamInfo("dB", ""));
                transParamInfoObservableList.addAll(new TransParamInfo("dL", ""));
                transParamInfoObservableList.addAll(new TransParamInfo("dh", ""));
            }
            this.transParamInfoTableView.setItems(transParamInfoObservableList);
            this.transParamInfoTableView.refresh();
        } else if (index == 4) {
            transParamInfoObservableList.clear();
            if (param.getType() == 4) {
                transParamInfoObservableList.addAll(new TransParamInfo("△X", String.valueOf(param.getDx())));
                transParamInfoObservableList.addAll(new TransParamInfo("△Y", String.valueOf(param.getDy())));
                transParamInfoObservableList.addAll(new TransParamInfo("θ", String.valueOf(param.getDz())));
                transParamInfoObservableList.addAll(new TransParamInfo("mx", String.valueOf(param.getWx())));
                transParamInfoObservableList.addAll(new TransParamInfo("my", String.valueOf(param.getWy())));

            } else {
                transParamInfoObservableList.addAll(new TransParamInfo("△X", ""));
                transParamInfoObservableList.addAll(new TransParamInfo("△Y", ""));
                transParamInfoObservableList.addAll(new TransParamInfo("θ", ""));
                transParamInfoObservableList.addAll(new TransParamInfo("mx", ""));
                transParamInfoObservableList.addAll(new TransParamInfo("my", ""));

            }
            this.transParamInfoTableView.setItems(transParamInfoObservableList);
            this.transParamInfoTableView.refresh();
        } else if (index == 5) {
            transParamInfoObservableList.clear();
            if (param.getType() == 5) {
                transParamInfoObservableList.addAll(new TransParamInfo("X'=", String.valueOf(param.getEquationX())));
                transParamInfoObservableList.addAll(new TransParamInfo("Y'=", String.valueOf(param.getEquationY())));
                transParamInfoObservableList.addAll(new TransParamInfo("Z'=", String.valueOf(param.getEquationZ())));
            } else {
                transParamInfoObservableList.addAll(new TransParamInfo("X'=", "2*x*x+5*x*y+8"));
                transParamInfoObservableList.addAll(new TransParamInfo("Y'=", "2*x*x+5*x*y+8"));
                transParamInfoObservableList.addAll(new TransParamInfo("Z'=", "4*x*x+6*x*y+7"));
            }
            this.transParamInfoTableView.setItems(transParamInfoObservableList);
            this.transParamInfoTableView.refresh();
        }
    }

    /**
     * 单击选中转换项列表更新对应的界面及转换参数列表
     */
    private void updateSelectItemParamInfo() {
        MyTransItemInfo transItemInfo = this.transItemInfoTableView.getSelectionModel().getSelectedItem();
        if (transItemInfo == null)
            return;
        ElpTransParam param = transItemInfo.getTransParam();
        if (param == null)
            return;
        this.srcSRefCombo.getSelectionModel().select(param.getInCord());
        this.desSRefCombo.getSelectionModel().select(param.getOutCord());
        this.transMethodCombo.getSelectionModel().select(param.getType());
        this.transNameText.setText(param.getTransName());

        setDisabled();
        refreshTransParamTableView(param);
    }

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

    private void applyItem() {
        boolean flag = true;
        for (int i = 0; i < this.transItemInfoObservableList.size(); i++) {
            MyTransItemInfo info = this.transItemInfoTableView.getSelectionModel().getSelectedItem();
            if (i == this.transItemInfoTableView.getSelectionModel().getFocusedIndex())
                continue;
            if (this.transNameText.getText() == info.getTransName()) {
                flag = false;
                break;
            }
        }
        if (!flag) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "转换项列表中已存在同名转换项，请修改此转换项名称。");
            alert.show();
            return;
        }
        if (!lookOutGridViewValue()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "手动输入转换参数不合法，请重新输入。");
            alert.show();
            return;
        }
        ElpTransParam param = this.transItemInfoTableView.getSelectionModel().getSelectedItem().getTransParam();
        param.setInCord((short) this.srcSRefCombo.getSelectionModel().getSelectedIndex());
        param.setOutCord((short) this.desSRefCombo.getSelectionModel().getSelectedIndex());
        param.setType((short) this.transMethodCombo.getSelectionModel().getSelectedIndex());
        param.setTransName(this.transNameText.getText());
        modifyParam(param);//修改参数
        this.transItemInfoTableView.getSelectionModel().getSelectedItem().setTransName(param.getTransName());
        this.transItemInfoTableView.getSelectionModel().getSelectedItem().setTransParam(param);
        setDisabled();
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

    //更新参数
    private void modifyParam(ElpTransParam param) {
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
    //禁止编辑
    private void setDisabled()
    {
        this.srcSRefCombo.setDisable(true);
        this.desSRefCombo.setDisable(true);
        this.transNameText.setEditable(false);
        this.transMethodCombo.setDisable(true);
        this.transParamInfoTableView.getColumns().get(1).setEditable(false);
        this.applyButton.setDisable(true);
        modify = false;
    }
    //允许编辑
    private void setEnabled()
    {
        this.srcSRefCombo.setDisable(false);
        this.desSRefCombo.setDisable(false);
        this.transNameText.setEditable(true);
        this.transMethodCombo.setDisable(false);
        this.transParamInfoTableView.getColumns().get(1).setEditable(true);
        this.applyButton.setDisable(false);
        modify = true;
    }

    //endregion

    // 确定按钮
    private void okButtonClick(ActionEvent event) {

        if (transItemInfoObservableList.size() != 0 && transItemInfoTableView.getSelectionModel().getSelectedItems() != null && transItemInfoTableView.getSelectionModel().getSelectedItems().size() != 0 && modify) {
                //是否保存修改项？
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "是否保存修改项", ButtonType.YES, ButtonType.NO);
                Optional<ButtonType> optional = alert.showAndWait();
                if (optional != null && optional.isPresent()) {
                    ButtonType type = optional.get();
                    if (type == ButtonType.YES) {
                        applyItem();
                    }
                }
        }
        ElpTransformation.clearElpTransParam();
        for (int i = 0; i <transItemInfoObservableList.size(); i++) {
            ElpTransformation.addElpTransParam(transItemInfoObservableList.get(i).getTransParam());
            elpTransParamArrayList.add(transItemInfoObservableList.get(i).getTransParam());
        }
        String sep = File.separator;
        String m_Path = (EnvConfig.getConfigDirectory(SysConfigDirType.Projection));//TransLst.dat
        m_Path += sep + "TransLst.dat";
        ElpTransformation.saveElpTransParam(m_Path);
    }

    // 应用按钮
    private void applyButtonClick(ActionEvent event) {
        if (transItemInfoObservableList.size() != 0 && transItemInfoTableView.getSelectionModel().getSelectedItems() != null && transItemInfoTableView.getSelectionModel().getSelectedItems().size() != 0 && modify) {
            {
                applyItem();
            }
            event.consume();
        }
    }

    /**
     * 加载地理转换参数
     */
    private void loadTransParamList() {
        transItemInfoObservableList.clear();
        String sep = File.separator;
        String m_Path = (EnvConfig.getConfigDirectory(SysConfigDirType.Projection));//TransLst.dat
        m_Path += sep + "TransLst.dat";
        File file = new File(m_Path);
        if (file.exists()) {
            ElpTransformation.loadElpTransParam(m_Path);
            for (int i = 0; i < ElpTransformation.getElpTransParamCount(); i++) {
                if (ElpTransformation.getElpTransParam(i) != null) {
                    String transName = ElpTransformation.getElpTransParam(i).getTransName();
                    ElpTransParam transParam = ElpTransformation.getElpTransParam(i);
                    transItemInfoObservableList.addAll(new MyTransItemInfo(transName, transParam));
                }
            }
            ElpTransformation.saveElpTransParam(m_Path);
        }
        if (transItemInfoObservableList.size() > 0)
            transItemInfoTableView.getSelectionModel().select(0);
    }

    //转换项信息
    class MyTransItemInfo {
        private String transName;
        private ElpTransParam transParam;

        public MyTransItemInfo(String name, ElpTransParam param) {
            transName = name;
            transParam = param;
        }

        public String getTransName() {
            return transName;
        }

        public void setTransName(String val) {
            this.transName = val;
        }

        public ElpTransParam getTransParam() {
            return transParam;
        }

        public void setTransParam(ElpTransParam val) {
            this.transParam = val;
        }
    }

    //转换参数信息
//    class TransParamInfo {
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
