package com.zondy.mapgis.mapeditor.dialogs;

import com.zondy.mapgis.analysis.spatialanalysis.BufferCapType;
import com.zondy.mapgis.analysis.spatialanalysis.BufferOption;
import com.zondy.mapgis.analysis.spatialanalysis.SpatialAnalysis;
import com.zondy.mapgis.att.Field;
import com.zondy.mapgis.att.Fields;
import com.zondy.mapgis.base.MessageBox;
import com.zondy.mapgis.base.Window;
import com.zondy.mapgis.controls.LayerSelectComboBoxItem;
import com.zondy.mapgis.controls.LayerSelectControl;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.controls.MapGISColorPicker;
import com.zondy.mapgis.controls.common.ButtonEdit;
import com.zondy.mapgis.controls.common.ButtonEditEvent;
import com.zondy.mapgis.controls.common.NumberTextField;
import com.zondy.mapgis.filedialog.GDBSaveFileDialog;
import com.zondy.mapgis.geodatabase.DataBase;
import com.zondy.mapgis.geodatabase.SFeatureCls;
import com.zondy.mapgis.geometry.GeomType;
import com.zondy.mapgis.geometry.ObjectIDs;
import com.zondy.mapgis.geometry.Rect;
import com.zondy.mapgis.map.*;
import com.zondy.mapgis.mapeditor.common.CustomClass;
import com.zondy.mapgis.srs.SRefData;
import com.zondy.mapgis.srs.SRefEPType;
import com.zondy.mapgis.srs.SRefLenUnit;
import com.zondy.mapgis.srs.SRefType;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.File;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BufferAnalysisDialog extends Dialog {
    private LayerSelectControl layerSelectControl_DataSource;
    private CheckBox checkBox_SelectItem;
    private NumberTextField numberTextField_Tolerance;
    private MapGISColorPicker mapGISColorPicker_SetColor;
    private CheckBox checkBox_MultiFeatureOperation;
    private ButtonEdit buttonEdit_SavePath;
    private CheckBox checkBox_AddInMap;
    private ComboBox<String> comboBox_LineEndType;
    private ComboBox<String> comboBox_IsDissolve;
    private ComboBox<String> comboBox_Unit;
    private RadioButton radioButton_Radius;
    private CheckBox checkBox_EqualsRadius;
    private NumberTextField numberTextField_LeftRadius;
    private NumberTextField numberTextField_RightRadius;
    private RadioButton radioButton_Attribute;
    private ComboBox<String> comboBox_Attribute;
    private Button button_OK;

    private MapControl mapControl;

    private List<SelectSetItem> selectSetItems; // 选择元素集
    private GeomType geomType;                  // 几何类型
    private SRefData sRefData;                  // 空间参照系
    private boolean exist = false;              // 判断事件存在的标识

    public BufferAnalysisDialog(Document document, MapControl mapControl) {
        this.mapControl = mapControl;

        System.out.println(mapControl == null);
        setTitle("缓冲分析");
        String filter = "简单要素类，6x数据|sfcls;*.wp;*.wl;*.wt";

        // region Source Data

        layerSelectControl_DataSource = new LayerSelectControl(document, filter);
        layerSelectControl_DataSource.setPrefWidth(200);

        layerSelectControl_DataSource.setOnSelectedItemChanged(layerSelectControl_DataSource_SelectedItemChanged_ChangeListener);
        HBox hBox_SelectLayer = new HBox(5, new Label("选择图层:"), layerSelectControl_DataSource);
        HBox.setHgrow(layerSelectControl_DataSource, Priority.ALWAYS);
        checkBox_SelectItem = new CheckBox("只对选择数据进行操作");
        checkBox_SelectItem.selectedProperty().addListener(checkBox_SelectItem_CheckedChanged_ChangeListener);

        VBox vBox_sourceData = new VBox(5, hBox_SelectLayer, checkBox_SelectItem);

        // endregion

        // region Buffer Style

        comboBox_LineEndType = new ComboBox<>(FXCollections.observableArrayList("圆头", "平头"));
        comboBox_LineEndType.setPrefWidth(110);
        comboBox_IsDissolve = new ComboBox<>(FXCollections.observableArrayList("合并", "不合并"));
        comboBox_IsDissolve.getSelectionModel().selectedIndexProperty().addListener(comboBox_IsDissolve_SelectedIndexChanged_ChangeListener);

        HBox hBox_LineEndType = new HBox(new Label("缓冲区线端类型:"), comboBox_LineEndType);
        HBox.setHgrow(comboBox_LineEndType, Priority.ALWAYS);
        HBox hBox_IsDissolve = new HBox(new Label("缓冲区合并样式:"), comboBox_IsDissolve);
        HBox.setHgrow(comboBox_IsDissolve, Priority.ALWAYS);
        VBox vBox_BufferStyle = new VBox(5, hBox_LineEndType, hBox_IsDissolve);

        // endregion

        // region Buffer Param

        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(10);
        numberTextField_Tolerance = new NumberTextField(new BigDecimal("0.0001"), numberFormat);
        numberTextField_Tolerance.setOnKeyPressed(numberTextField_Tolerance_KeyPressed_EventHandler);
        HBox hBox_Tolerance = new HBox(new Label("设置容差:"), numberTextField_Tolerance);
        HBox.setHgrow(numberTextField_Tolerance, Priority.ALWAYS);
        mapGISColorPicker_SetColor = new MapGISColorPicker();
        HBox hBox_SetColor = new HBox(new Label("设置颜色:"), mapGISColorPicker_SetColor);
        HBox.setHgrow(mapGISColorPicker_SetColor, Priority.ALWAYS);
        checkBox_MultiFeatureOperation = new CheckBox("处理复合要素");

        VBox vBox_BufferParam = new VBox(5, hBox_Tolerance, hBox_SetColor, checkBox_MultiFeatureOperation);

        // endregion

        // region Buffer Mode

        // TODO: this.mapControl.GisEnvironment.GetEditParam(ref edp);,以及选择集事件
        comboBox_Unit = new ComboBox<>(FXCollections.observableArrayList(
                "数据单位", "毫米", "厘米", "分米", "米", "千米", "英寸", "英尺", "英里", "码"
        ));
        comboBox_Unit.getSelectionModel().selectedIndexProperty().addListener(comboBox_Unit_SelectedIndexChanged_ChangeListener);
        radioButton_Radius = new RadioButton("指定半径缓冲");
        radioButton_Radius.selectedProperty().addListener(radioButton_Radius_SelectedChanged_ChangeListener);
        checkBox_EqualsRadius = new CheckBox("左右等半径");
        checkBox_EqualsRadius.selectedProperty().addListener(checkBox_EqualsRadius_SelectedChanged_ChangeListener);
        numberTextField_LeftRadius = new NumberTextField(new BigDecimal(10));
        numberTextField_LeftRadius.setOnKeyPressed(numberTextField_Radius_KeyPressed_EventHandler);
        numberTextField_RightRadius = new NumberTextField(new BigDecimal(10));
        numberTextField_RightRadius.setOnKeyPressed(numberTextField_Radius_KeyPressed_EventHandler);
        radioButton_Attribute = new RadioButton("根据属性缓冲");
        comboBox_Attribute = new ComboBox<>();
        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(radioButton_Radius, radioButton_Attribute);

        GridPane gridPane_BufferMode = new GridPane();
        gridPane_BufferMode.setHgap(5);
        gridPane_BufferMode.setVgap(5);
        gridPane_BufferMode.add(new Label("单位:"), 0, 0);
        gridPane_BufferMode.add(comboBox_Unit, 1, 0);
        gridPane_BufferMode.add(radioButton_Radius, 0, 1, 2, 1);
        gridPane_BufferMode.add(checkBox_EqualsRadius, 0, 2, 2, 1);
        gridPane_BufferMode.add(new Label("  左半径:"), 0, 3);
        gridPane_BufferMode.add(numberTextField_LeftRadius, 1, 3);
        gridPane_BufferMode.add(new Label("  右半径:"), 0, 4);
        gridPane_BufferMode.add(numberTextField_RightRadius, 1, 4);
        gridPane_BufferMode.add(radioButton_Attribute, 0, 5);
        gridPane_BufferMode.add(comboBox_Attribute, 1, 5);

        // endregion

        // region Result

        buttonEdit_SavePath = new ButtonEdit();
        buttonEdit_SavePath.setTextEditable(true);
        buttonEdit_SavePath.setOnButtonClick(buttonEdit_SavePath_ButtonClick_EventHandler);
        HBox hBox_SavePath = new HBox(5, new Label("保存路径:"), buttonEdit_SavePath);
        HBox.setHgrow(buttonEdit_SavePath, Priority.ALWAYS);
        checkBox_AddInMap = new CheckBox("将结果图层添加到视图中");

        VBox vBox_bufferResult = new VBox(5, hBox_SavePath, checkBox_AddInMap);

        // endregion

        // region TitlePane(Group)

        TitledPane topPane_SourceData = new TitledPane("缓冲区图层", vBox_sourceData);
        topPane_SourceData.setCollapsible(false);
        TitledPane titledPane_BufferStyle = new TitledPane("缓冲区样式", vBox_BufferStyle);
        titledPane_BufferStyle.setCollapsible(false);
        TitledPane titledPane_BufferParam = new TitledPane("缓冲区参数", vBox_BufferParam);
        titledPane_BufferParam.setCollapsible(false);
        TitledPane titledPane_BufferMode = new TitledPane("缓冲区半径方式", gridPane_BufferMode);
        titledPane_BufferMode.setCollapsible(false);
        VBox middleLeftVBox = new VBox(5, titledPane_BufferStyle, titledPane_BufferParam);
        HBox middleHBox = new HBox(5, middleLeftVBox, titledPane_BufferMode);
        TitledPane bufferResultButtonPane = new TitledPane("缓冲区结果", vBox_bufferResult);
        bufferResultButtonPane.setCollapsible(false);

        // endregion

        // region Layout

        mapGISColorPicker_SetColor.prefWidthProperty().bind(numberTextField_Tolerance.widthProperty());
        comboBox_IsDissolve.prefWidthProperty().bind(comboBox_LineEndType.widthProperty());
        comboBox_Unit.prefWidthProperty().bind(numberTextField_LeftRadius.widthProperty());
        comboBox_Attribute.prefWidthProperty().bind(numberTextField_LeftRadius.widthProperty());
        titledPane_BufferMode.prefHeightProperty().bind(middleHBox.heightProperty());

        VBox mainVBox = new VBox(5, topPane_SourceData, middleHBox, bufferResultButtonPane);

        // endregion

        // controls init

        layerSelectControl_DataSource.selectFirstItem();
        if (this.mapControl != null) {
//            EditDefParam edp = new EditDefParam();
//            this.mapControl.GisEnvironment.GetEditParam(ref edp);
//            this.textEdit_LeftRadius.EditValue = edp.BufferRadius;
//            this.textEdit_RightRadius.EditValue = edp.BufferRadius;
//            SelectSet sets = this.mapControl.getMap().getSelectSet();
//            List<SelectSetItem> selectsetList = sets.get();
//            //过滤选择集中图层个数为1且为简单要素类图层
//            if (selectsetList.size() == 1 && selectsetList.get(0).getLayer().getGeometryType() != GeomType.GeomAnn && selectsetList.get(0).getLayer().getGeometryType() != GeomType.GeomUnknown) {
//                checkBox_SelectItem.setDisable(false);
//                checkBox_SelectItem.setSelected(true);
//                layerSelectControl_DataSource.setDisable(true);
//            }
        } else {
            checkBox_AddInMap.setSelected(false);
            checkBox_AddInMap.setDisable(true);
        }
        comboBox_LineEndType.getSelectionModel().select(0);
        comboBox_IsDissolve.getSelectionModel().select(0);
        comboBox_Unit.getSelectionModel().select(0);
        radioButton_Radius.setSelected(true);

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setContent(mainVBox);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.setPrefWidth(520);
        dialogPane.setPrefHeight(460);
        button_OK = (Button) dialogPane.lookupButton(ButtonType.OK);
        button_OK.setOnAction(button_OK_Action_EventHandler);

    }

    // event

    private ChangeListener<LayerSelectComboBoxItem> layerSelectControl_DataSource_SelectedItemChanged_ChangeListener = (observable, oldValue, newValue) -> {
        comboBox_Attribute.getItems().clear();
        String sfclsUrl = layerSelectControl_DataSource.getSelectedItemUrl();
        if (!sfclsUrl.isEmpty()) {
            SFeatureCls sfcls = new SFeatureCls();
            sfclsUrl = sfclsUrl.toLowerCase().startsWith("gdbp://") || sfclsUrl.toLowerCase().startsWith("file:///") ? sfclsUrl : "file:///" + sfclsUrl;
            if (sfcls.openByURL(sfclsUrl) > 0) {
                String modelName = sfcls.getModelName();
                // TODO: GUID
                UUID uuid = UUID.randomUUID();
                if (modelName != null && !modelName.isEmpty()) {
                    uuid = UUID.fromString(modelName);
                } else {
                    if (sfclsUrl.toLowerCase().startsWith("file:///")) {
                        FileLayer6x fileLayer6x = new FileLayer6x();
                        fileLayer6x.setURL(sfclsUrl);
                        if (fileLayer6x.connectData()) {
                            if (fileLayer6x.getCount() > 0) {
                                VectorLayer vl = (VectorLayer) fileLayer6x.item(0);
                                if (vl != null) {
                                    uuid = UUID.fromString(vl.getSysLibrary().getGuid());
                                    //guid = guidHelper.fromString(vl.getSysLibrary().getGuid());
                                }
                            }
                        }
                    }
                }
                // TODO: 待颜色选择器支持
                //mapGISColorPicker_SetColor.Init(guid, null, true);
                mapGISColorPicker_SetColor.setSelectColorNumber(10);
                this.geomType = sfcls.getGeomType();
                // TODO: 半径不为负数，因此不能输入负号 (使用时看是否需要支持)
                //Keep_Radius_OK();
                this.sRefData = sfcls.getGDataBase().getSRef(sfcls.getsrID());
                if (radioButton_Radius.isSelected()) {
                    radioButton_Attribute.setDisable(false);
                    numberTextField_LeftRadius.setDisable(false);
                    numberTextField_RightRadius.setDisable(false);
                    checkBox_EqualsRadius.setDisable(false);
                    if (comboBox_Unit.getSelectionModel().getSelectedIndex() != 0 && this.sRefData.getType() == SRefType.JWD) {
                        radioButton_Attribute.setDisable(true);
                        numberTextField_RightRadius.setDisable(true);
                        checkBox_EqualsRadius.setDisable(true);
                    } else {
                        if (this.geomType == GeomType.GeomLin) {
                            checkBox_EqualsRadius.setDisable(false);
                            numberTextField_RightRadius.setDisable(checkBox_EqualsRadius.isSelected());
                        } else {
                            checkBox_EqualsRadius.setDisable(true);
                            numberTextField_RightRadius.setDisable(true);
                        }
                    }
                } else {
                    checkBox_EqualsRadius.setDisable(true);
                    numberTextField_LeftRadius.setDisable(true);
                    numberTextField_RightRadius.setDisable(true);
                }
                boolean isLine = !(this.geomType == GeomType.GeomPnt || this.geomType == GeomType.GeomReg);
                comboBox_LineEndType.getSelectionModel().select(isLine ? 1 : 0);
                comboBox_LineEndType.setDisable(!isLine);
                //遍历属性字段
                Fields fields = sfcls.getFields();
                if (fields != null) {
                    for (short c = 0; c < fields.getFieldCount(); c++) {
                        Field field = fields.getField(c);
                        // TODO FieldType需继承enum
                        if (field.getFieldType().value() >= Field.FieldType.fldShort.value() && field.getFieldType().value() <= Field.FieldType.fldDouble.value()) {
                            comboBox_Attribute.getItems().add(field.getFieldName());
                        }
                    }
                }
                if (this.comboBox_Attribute.getItems().size() != 0) {
                    this.comboBox_Attribute.getSelectionModel().select(0);
                }
                //设置不同参考系下的容差
                double tol = CustomClass.getToleranceBySRefData(CustomClass.TOLERANCE, this.sRefData);
                try {
                    numberTextField_Tolerance.setNumber(new BigDecimal(tol));
                } catch (Exception ex) {
                    MessageBox.information(sfclsUrl + "的参考系设置有误!");
                }
                sfcls.close();
            }
        } else {
            button_OK.setDisable(true);
        }
        buttonEdit_SavePath.setText("");
    };

    private ChangeListener<Boolean> checkBox_SelectItem_CheckedChanged_ChangeListener = (observable, oldValue, newValue) -> {
        SFeatureCls sf = null;
        if (checkBox_SelectItem.isSelected()) {
            layerSelectControl_DataSource.setDisable(true);
            sf = (SFeatureCls) new MapLayer(this.selectSetItems.get(0).getLayer().getURL()).getData();
            // TODO: 需求列表2
            //this.geomType = (new MapLayer(this.selectSetItems.get(0).getLayer())).getGeometryType();
            //imageComboBoxEdit_DataSource.SelectItemByHandleEx(selectsetList[0].Layer.Handle);
        } else {
            layerSelectControl_DataSource.setDisable(false);
            sf = new SFeatureCls();
            String sfclsUrl = layerSelectControl_DataSource.getSelectedItemUrl();
            sfclsUrl = sfclsUrl.toLowerCase().startsWith("gdbp://") || sfclsUrl.toLowerCase().startsWith("file:///") ? sfclsUrl : "file:///" + sfclsUrl;
            if (sf.openByURL(sfclsUrl) == 0) {
                return;
            }
            this.geomType = sf.getGeomType();
        }
        // TODO: 半径不为负数，因此不能输入负号 (使用时看是否需要支持)
        //Keep_Radius_OK();
        if (radioButton_Radius.isSelected()) {
            numberTextField_LeftRadius.setDisable(false);
            if (comboBox_Unit.getSelectionModel().getSelectedIndex() == 0) {
                if (this.geomType == GeomType.GeomLin) {
                    checkBox_EqualsRadius.setDisable(false);
                    if (checkBox_EqualsRadius.isSelected()) {
                        numberTextField_RightRadius.setDisable(true);
                    } else {
                        numberTextField_RightRadius.setDisable(false);
                    }
                } else {
                    checkBox_EqualsRadius.setDisable(true);
                    numberTextField_RightRadius.setDisable(true);
                }
            } else {
                if (this.sRefData.getType() == SRefType.JWD) {
                    radioButton_Attribute.setDisable(true);
                    checkBox_EqualsRadius.setDisable(true);
                }
            }
        } else {
            checkBox_EqualsRadius.setDisable(true);
            numberTextField_LeftRadius.setDisable(true);
            numberTextField_RightRadius.setDisable(true);
        }
        comboBox_LineEndType.setDisable(this.geomType == GeomType.GeomPnt || this.geomType == GeomType.GeomReg);
        //遍历属性字段
        comboBox_Attribute.getItems().clear();
        if (sf != null && sf.getFields() != null) {
            for (short c = 0; c < sf.getFields().getFieldCount(); c++) {
                if (sf.getFields().getField(c).getFieldType().value() >= Field.FieldType.fldShort.value() && sf.getFields().getField(c).getFieldType().value() <= Field.FieldType.fldDouble.value()) {
                    comboBox_Attribute.getItems().add(sf.getFields().getField(c).getFieldName());
                }
            }
        }
        if (comboBox_Attribute.getItems().size() != 0) {
            comboBox_Attribute.getSelectionModel().select(0);
        }
        if (!checkBox_SelectItem.isSelected()) {
            sf.close();
        }
    };

    private EventHandler<ButtonEditEvent> buttonEdit_SavePath_ButtonClick_EventHandler = event -> {
        GDBSaveFileDialog saveFileDialog = new GDBSaveFileDialog();
//        saveFileDialog.CanOverwrite = false;
        saveFileDialog.setFilter("简单要素类;6x文件|sfcls;*.wp");
        Optional<String[]> optional = saveFileDialog.showAndWait();
        if (optional != null && optional.isPresent()) {
            String[] files = optional.get();
            if (files.length > 0) {
                buttonEdit_SavePath.setText(files[0]);
            }
        }
    };

    private ChangeListener<Boolean> radioButton_Radius_SelectedChanged_ChangeListener = (observable, oldValue, newValue) -> {
        if (radioButton_Radius.isSelected()) {
            numberTextField_LeftRadius.setDisable(false);
            comboBox_Unit.setDisable(false);
            comboBox_Attribute.setDisable(true);
            if (this.geomType == GeomType.GeomLin) {
                checkBox_EqualsRadius.setDisable(false);
                if (!checkBox_EqualsRadius.isSelected()) {
                    numberTextField_RightRadius.setDisable(false);
                }
            } else {
                checkBox_EqualsRadius.setSelected(true);
                checkBox_EqualsRadius.setDisable(true);
            }
        } else {
            numberTextField_LeftRadius.setDisable(true);
            numberTextField_RightRadius.setDisable(true);
            checkBox_EqualsRadius.setDisable(true);
            comboBox_Unit.setDisable(true);
            comboBox_Attribute.setDisable(false);

        }
    };

    private ChangeListener<Number> comboBox_Unit_SelectedIndexChanged_ChangeListener = (observable, oldValue, newValue) -> {
        if (comboBox_Unit.getSelectionModel().getSelectedIndex() == 0) {
            radioButton_Attribute.setDisable(false);
            if (this.geomType == GeomType.GeomLin) {
                checkBox_EqualsRadius.setDisable(false);
                if (!checkBox_EqualsRadius.isSelected()) {
                    numberTextField_RightRadius.setDisable(false);
                }
            }
        } else {
            comboBox_Attribute.setDisable(true);
            if (this.sRefData.getType() == SRefType.JWD) {
                radioButton_Attribute.setDisable(true);
                numberTextField_RightRadius.setDisable(true);
                checkBox_EqualsRadius.setDisable(true);
            }
        }
        keepRadiusOK();
    };

    private ChangeListener<Boolean> checkBox_EqualsRadius_SelectedChanged_ChangeListener = (observable, oldValue, newValue) -> {
        if (checkBox_EqualsRadius.isSelected()) {
            numberTextField_RightRadius.setNumber(numberTextField_LeftRadius.getNumber());
            numberTextField_RightRadius.setDisable(true);
        } else {
            numberTextField_RightRadius.setDisable(false);
        }
    };

    private ChangeListener<Number> comboBox_IsDissolve_SelectedIndexChanged_ChangeListener = (observable, oldValue, newValue) -> {
        checkBox_MultiFeatureOperation.setDisable(comboBox_IsDissolve.getSelectionModel().getSelectedIndex() != 0);
    };

    private EventHandler<KeyEvent> numberTextField_Tolerance_KeyPressed_EventHandler = event -> {
        if (event.getCode() == KeyCode.MINUS) {
            event.consume();
        }
    };

    private EventHandler<KeyEvent> numberTextField_Radius_KeyPressed_EventHandler = event -> {
        if (event.getCode() == KeyCode.MINUS) {
            event.consume();
        } else if (event.getSource() == numberTextField_LeftRadius) {
            if (numberTextField_LeftRadius.getNumber().toString().trim() != "") {
                if (checkBox_EqualsRadius.isSelected()) {
                    numberTextField_RightRadius.setNumber(numberTextField_LeftRadius.getNumber());

                }
            }
        } else if (event.getSource() == numberTextField_LeftRadius) {
            if (numberTextField_RightRadius.getNumber().toString().trim() != "") {
                if (checkBox_EqualsRadius.isSelected()) {
                    numberTextField_LeftRadius.setNumber(numberTextField_RightRadius.getNumber());
                }
            }
        }
    };

    private EventHandler<ActionEvent> button_OK_Action_EventHandler = event -> {
        System.out.println("button_ok_click");
        if (!checkFormInput()) {
            System.out.println("checkFormInput_False");
            return;
        }
        startAnalyse();

//        {
//            this.waitForm = new WaitForm(false, false);
//            bool first = true;
//            this.waitForm.Load += (ws, es) =>
//            {
//                if (first)
//                {
//                    first = false;
//                    Thread threadCheck = new Thread(new ParameterizedThreadStart(StartAnalyse));
//                    threadCheck.SetApartmentState(ApartmentState.STA);
//                    threadCheck.CurrentUICulture = System.Threading.Thread.CurrentThread.CurrentUICulture;
//                    threadCheck.Start();
//                }
//            };
//            this.waitForm.ShowDialog(new Win32Window(XHelp.GetMainWindowHandle()));
//        }
    };

    // private method

    private void keepRadiusOK() {
        if (this.geomType == GeomType.GeomReg && comboBox_Unit.getSelectionModel().getSelectedIndex() == 0 && exist) {
            numberTextField_LeftRadius.setOnKeyPressed(null);
            numberTextField_RightRadius.setOnKeyPressed(null);
            exist = false;
        } else if ((this.geomType != GeomType.GeomReg || comboBox_Unit.getSelectionModel().getSelectedIndex() != 0) && !exist) {
            numberTextField_LeftRadius.setOnKeyPressed(numberTextField_Radius_KeyPressed_EventHandler);
            numberTextField_RightRadius.setOnKeyPressed(numberTextField_Radius_KeyPressed_EventHandler);
            exist = true;
            if (numberTextField_LeftRadius.getNumber().doubleValue() < 0) {
                numberTextField_LeftRadius.setNumber(new BigDecimal(10));
            }
        }
    }

    private boolean checkFormInput() {
        System.out.println("checkFormInput_Start");
        String sfclsUrl = layerSelectControl_DataSource.getSelectedItemUrl();
        if (sfclsUrl.isEmpty()) {
            MessageBox.information("请输入源数据路径");
            return false;
        }
        double tol = numberTextField_Tolerance.getNumber().doubleValue();
        if (tol <= 0) {
            MessageBox.information("容差必须为正数");
            return false;
        }
        String destUrl = buttonEdit_SavePath.getText();
        if (!destUrl.isEmpty()) {
            SFeatureCls sfcls = null;
            if (checkBox_SelectItem.isSelected()) {
                sfcls = (SFeatureCls) (new MapLayer(this.selectSetItems.get(0).getLayer().getURL()).getData());
            } else {
                sfcls = new SFeatureCls();
                sfclsUrl = sfclsUrl.toLowerCase().startsWith("gdbp://") || sfclsUrl.toLowerCase().startsWith("file:///") ? sfclsUrl : "file:///" + sfclsUrl;
                if (sfcls.openByURL(sfclsUrl) == 0) {
                    MessageBox.information("打开源数据失败");
                    return false;
                } else if (sfcls.getObjCount() == 0) {
                    MessageBox.information("简单要素中不存在图元");
                    return false;
                }
            }
            if (sfcls != null) {
                SRefData srs = sfcls.getGDataBase().getSRef(sfcls.getsrID());
                if (comboBox_Unit.getSelectionModel().getSelectedIndex() != 0) {
                    //未设置空间参考系
                    if (srs != null) {
                        if (srs.getType() == SRefType.JWD && srs.getAngUnit() == SRefLenUnit.Degree) {
                            if (srs.getSpheroid() == SRefEPType.Beijing54 || srs.getSpheroid() == SRefEPType.Xian80 || srs.getSpheroid() == SRefEPType.IUGG1979) {
                                Rect rc = sfcls.getRange();
                                if (rc != null) {
                                    //源数据不在经纬度范围内
                                    if (!(rc.getXMax() < 180 && rc.getXMin() > -180 && rc.getYMax() < 90 && rc.getYMin() > -90)) {
                                        MessageBox.information("源数据经纬度不在范围内(-180&lt;X&lt;180，-90&lt;Y&lt;90)。");
                                        return false;
                                    }
                                } else {
                                    MessageBox.information("源数据经纬度不在范围内(-180&lt;X&lt;180，-90&lt;Y&lt;90)。");
                                    return false;
                                }
                            } else {
                                MessageBox.information("空间参照系不支持动态投影（只有北京54，西安80，中国2000国家大地坐标系支持）。");
                                return false;
                            }
                        }
                    } else {
                        MessageBox.information("未设置空间参考系");
                        return false;
                    }
                }
                if (!checkBox_SelectItem.isSelected()) {
                    sfcls.close();
                }
            }
            if (tol > CustomClass.MAX_TOLERANCE || tol < CustomClass.MIN_TOLERANCE) {
                String warnMsg = String.format("容差不在推荐范围(%f~%f)内,可能导致生成结果错误,是否仍然使用该容差？", (double) CustomClass.MIN_TOLERANCE, (double) CustomClass.MAX_TOLERANCE);
                return MessageBox.question(warnMsg, Window.primaryStage, "警告") != ButtonType.CANCEL;
            }
        } else {
            MessageBox.information("请输入保存路径");
            return false;
        }
        System.out.println("checkFormInput_End");
        return true;
    }

    private void startAnalyse() {
        ObjectIDs OIDs = null;//OID集
        boolean rtn = false;
        SFeatureCls sfcls = null;//源简单要素类
        SFeatureCls destsfcls = null;//目的简单要素类
        if (checkBox_SelectItem.isSelected()) {
            sfcls = (SFeatureCls) new MapLayer(this.selectSetItems.get(0).getLayer().getURL()).getData();
            OIDs = new ObjectIDs();
//            ObjectID id = new ObjectID();
//            for (int g = 0; g < selectSetItems.get(0).IDList.Count; g++) {
//                id.Int64Val = selectsetList[0].IDList[g];
//                OIDs.append(id);
//            }
        } else {
            sfcls = new SFeatureCls();
            String sfclsUrl = layerSelectControl_DataSource.getSelectedItemUrl();
            sfclsUrl = sfclsUrl.toLowerCase().startsWith("gdbp://") || sfclsUrl.toLowerCase().startsWith("file:///") ? sfclsUrl : "file:///" + sfclsUrl;
            if (sfcls.openByURL(sfclsUrl) == 0) {
                return;
            }
        }
        System.out.println("sfcls open");
        destsfcls = new SFeatureCls();
        String destUrl = buttonEdit_SavePath.getText();
        if (destUrl.toLowerCase().startsWith("gdbp://")) {
            if (destsfcls.create(destUrl, GeomType.GeomReg) <= 0) {
                return;
            }
        } else {
            if (destsfcls.create("file:///" + destUrl, GeomType.GeomReg) <= 0) {
                return;
            }
        }
        System.out.println("dessfcls create");
//        TimeSpan tsBegin = new TimeSpan(DateTime.Now.Ticks);
//        InitPlugin.SetKeyValue("缓冲分析", "[" + "缓冲分析" + "]:" + DateTime.Now.ToString() + ":" + "开始进行缓冲分析" + "\r\n");
        BufferOption bufferOption = new BufferOption();
        if (comboBox_Attribute.getSelectionModel().getSelectedIndex() > -1) {
            bufferOption.setFldName(comboBox_Attribute.getSelectionModel().getSelectedItem());
        }
        bufferOption.setTolerance(numberTextField_Tolerance.getNumber().doubleValue());
        bufferOption.setClr(mapGISColorPicker_SetColor.getSelectColorNumber());
        bufferOption.setDynPrjRad(unitTransform(numberTextField_LeftRadius.getNumber().doubleValue()));
        bufferOption.setIsDynPrj(comboBox_Unit.getSelectionModel().getSelectedIndex() != 0);
        SRefData srs = sfcls.getGDataBase().getSRef(sfcls.getsrID());
        if (srs.getType() == SRefType.PRJ) {
            bufferOption.setIsDynPrj(false);
        }
        bufferOption.setIsAtt(radioButton_Attribute.isSelected());
        bufferOption.setLineEndType(comboBox_LineEndType.getSelectionModel().getSelectedIndex() == 0 ? BufferCapType.ROUND : BufferCapType.Flat);
        bufferOption.setIsDissolve(comboBox_IsDissolve.getSelectionModel().getSelectedIndex() == 0);
        bufferOption.setLeftRad(numberTextField_LeftRadius.getNumber().doubleValue());
        bufferOption.setRightRad(numberTextField_RightRadius.getNumber().doubleValue());
        bufferOption.setMultiFeatureOption(checkBox_MultiFeatureOperation.isSelected());
//        spBufferOption.setLogEventReceiver(new LogEventReceiver());
        //spBufferOption.ProcessCallback = logRev;
//        spBufferOption.ProcessCallback.StepStart += new StepStartEventHandle(onStepStart);
//        spBufferOption.ProcessCallback.StepMessage += new StepMessageEventHandle(onStepMessage);
//        spBufferOption.ProcessCallback.StepEnd += new StepEndEventHandle(onStepEnd);

        rtn = SpatialAnalysis.buffer(sfcls, destsfcls, bufferOption, OIDs) > 0;
        System.out.println(rtn);
//        spBufferOption.Dispose();
        String uurl = destsfcls.getURL();
        System.out.println(uurl);
        // region 返回值判断

//        this.waitForm.Close();
//        this.waitForm = null;
        if (rtn) {
            destsfcls.setsrID(sfcls.getsrID());
            destsfcls.setScaleXY(sfcls.getScaleX(), sfcls.getScaleY());
            destsfcls.setModelName(sfcls.getModelName());
            long l = destsfcls.close();
            System.out.println("des close " + l);
            if (checkBox_AddInMap.isSelected()) {
                if (uurl.toLowerCase().startsWith("gdbp://")) {
                    VectorLayer ml = new VectorLayer(VectorLayerType.SFclsLayer);
                    ml.setURL(uurl);
                    ml.setName(uurl.substring(uurl.lastIndexOf('/') + 1));
                    if (ml.connectData()) {
                        ml.setIsSymbolic(true);
                        ml.setIsFollowZoom(false);
//                        if (this.mapControl.InvokeRequired)
//                            this.mapControl.Invoke(new MethodInvoker(delegate { this.mapControl.ActiveMap.Append(ml);
//                            }));
//                            else
                        this.mapControl.getMap().append(ml);
                    }
                } else if (uurl.toLowerCase().startsWith("file:///")) {
                    FileLayer6x ml = new FileLayer6x();
                    ml.setURL(uurl);
                    ml.setName(uurl.substring(uurl.lastIndexOf('\\') + 1));
                    if (ml.connectData()) {
//                        if (this.mapControl.InvokeRequired)
//                            this.mapControl.Invoke(new MethodInvoker(delegate { this.mapControl.ActiveMap.Append(ml);
//                            }));
//                            else
                        this.mapControl.getMap().append(ml);
                    }
                }
            }
        } else {
            if (uurl.toLowerCase().startsWith("gdbp://")) {
                int clsID = destsfcls.getClsID();
                String gdbUrl = destsfcls.getGDataBase().getURL();
                destsfcls.close();
                DataBase delDB = DataBase.openByURL(gdbUrl);
                if (delDB != null) {
                    SFeatureCls.remove(delDB, clsID);
                    delDB.close();
                }
            } else if (uurl.toLowerCase().startsWith("file:///")) {
                destsfcls.close();
                String pp = uurl.substring(uurl.lastIndexOf("/") + 1);
                File file = new File(pp);
                if (file.exists()) {
                    file.delete();
                }
                file = new File(pp + "~");
                if (file.exists()) {
                    file.delete();
                }
            }
//            this.Invoke(new MethodInvoker(delegate {
//                bool isSucceed = MapGIS.Desktop.UI.Controls.MapGISErrorForm.ShowLastError();
//                    if(!isSucceed)
//                        XMessageBox.information(MapGIS.MapEditor.Plugin.Properties.Resources.String_FailBufferAnalyse);
//            }));

        }
        if (!checkBox_SelectItem.isSelected()) {
            sfcls.close();
        }

        // endregion
//        TimeSpan tsEnd = new TimeSpan(DateTime.Now.Ticks);
//        TimeSpan tsEx = tsEnd.Subtract(tsBegin).Duration();
//        string timeSpend = InitPlugin.FormatTime(tsEx);
//        InitPlugin.SetKeyValue(MapGIS.MapEditor.Plugin.Properties.Resources.String_BufferAnalyse, "[" + MapGIS.MapEditor.Plugin.Properties.Resources.String_BufferAnalyse + "]:" + DateTime.Now.ToString() + ":" + MapGIS.MapEditor.Plugin.Properties.Resources.String_FinishBufferAnalyseSumTime + timeSpend + "\r\n");
//        this.DialogResult = DialogResult.OK;
    }

    /**
     * 数据单位转换
     */
    private double unitTransform(double n) {
        double m = 0;
        switch (comboBox_Unit.getSelectionModel().getSelectedIndex()) {
            case 1:
                m = n / (double) 1000;
                break;
            case 2:
                m = n / (double) 100;
                break;
            case 3:
                m = n / (double) 10;
                break;
            case 4:
                m = n;
                break;
            case 5:
                m = (double) 1000 * n;
                break;
            case 6:
                m = 0.0254 * n;
                break;
            case 7:
                m = 0.3048 * n;
                break;
            case 8:
                m = 1609.344 * n;
                break;
            case 9:
                m = 0.9144 * n;
                break;
            default:
                m = n;
                break;
        }
        return m;
    }
}
