package com.zondy.mapgis.mapeditor.dialogs;

import com.zondy.mapgis.analysis.spatialanalysis.*;
import com.zondy.mapgis.base.MessageBox;
import com.zondy.mapgis.controls.LayerSelectComboBoxItem;
import com.zondy.mapgis.controls.LayerSelectControl;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.controls.common.NumberTextField;
import com.zondy.mapgis.controls.common.ZDToolBar;
import com.zondy.mapgis.geodatabase.SFeatureCls;
import com.zondy.mapgis.geometry.GeomType;
import com.zondy.mapgis.geometry.Rect;
import com.zondy.mapgis.map.Document;
import com.zondy.mapgis.map.MapLayer;
import com.zondy.mapgis.mapeditor.common.CustomClass;
import com.zondy.mapgis.srs.SRefData;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;

public class TopoCheckDialog extends Dialog {
    private ZDToolBar zdToolBar_InputSetting;
    private Button button_Add;
    private Button button_SelectAll;
    private Button button_Remove;
    private Button button_Import;
    private Button button_Export;
    private TableView<TopoCheckItem> tableView_Layer;
    private NumberTextField numberTextField_Tolerance;
    private CheckBox checkBox_CurrentDisplayRange;
    private CheckBox checkBox_MaxErrorNum;
    private NumberTextField numberTextField_MaxErrorNum;
    private NumberTextField numberTextField_MinArcLength;
    private NumberTextField numberTextField_MinRegArea;
    private CheckBox checkBox_DisplayError;
    private ImageView imageView_Display;
    private TextArea textArea_Describe;
    private Button button_OK;

    private Image imageUnknown = new Image(getClass().getResourceAsStream("/Png_Unknown_16.png"));
    private Image imagePnt = new Image(getClass().getResourceAsStream("/Png_SfClsPnt_16.png"));
    private Image imageLin = new Image(getClass().getResourceAsStream("/Png_SfClsLin_16.png"));
    private Image imageReg = new Image(getClass().getResourceAsStream("/Png_SfClsReg_16.png"));

    private Document document;
    private MapControl mapControl;
    private TopologyErrorManager topologyErrorManager;
    private ArrayList<SFeatureCls> sfclsList;
    //private ExtraOption extraOption;

    public TopoCheckDialog(Document document, MapControl mapControl) {
        setTitle("拓扑检查");

        this.document = document;
        this.mapControl = mapControl;
        //this.extraOption = new ExtraOption();

        // region Input Setting

        button_Add = new Button("添加");
        button_Add.setOnMouseClicked(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("添加，待 GDBOpenFileDialog");
            alert.showAndWait();
        });
        button_SelectAll = new Button("全选");
        button_SelectAll.setOnMouseClicked(event -> {
            ObservableList<TopoCheckItem> curSelectedItems = tableView_Layer.getSelectionModel().getSelectedItems();
            if (curSelectedItems == null || curSelectedItems.size() == 0) {
                MessageBox.information("请选择要删除的行!");
                return;
            }
            if (tableView_Layer.getSelectionModel().isSelected(tableView_Layer.getItems().size() - 1)) {
                curSelectedItems.remove(curSelectedItems.get(tableView_Layer.getItems().size() - 1));
            }
            tableView_Layer.getItems().removeAll(curSelectedItems);
            tableView_Layer.getSelectionModel().clearSelection();
        });
        button_Remove = new Button("移除");
        button_Remove.setOnMouseClicked(event -> {
            if (tableView_Layer != null && tableView_Layer.getItems() != null && tableView_Layer.getItems().size() != 0) {
                tableView_Layer.getSelectionModel().clearSelection();
                tableView_Layer.getSelectionModel().selectAll();
            }
        });
        button_Import = new Button("导入");
        button_Import.setOnMouseClicked(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("添加，待 GDBOpenFileDialog");
            alert.showAndWait();
        });
        button_Export = new Button("导出");
        button_Export.setOnMouseClicked(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("添加，待 GDBOpenFileDialog");
            alert.showAndWait();
        });
        zdToolBar_InputSetting = new ZDToolBar(true,
                button_Add, button_SelectAll, button_Remove, button_Import, button_Export);
        tableView_Layer = new TableView<>();
        TableColumn<TopoCheckItem, LayerSelectControl> tableColumn_LayerA = new TableColumn<>("图层A");
        tableColumn_LayerA.setCellValueFactory(param -> {
            LayerSelectControl layerSelectControl = new LayerSelectControl(param.getValue().getDocument(), "简单要素类|sfclsp;sfclsl;sfclsr");
            layerSelectControl.setOnSelectedItemChanged((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    addSrcData(newValue, 1);
                }
            });
            //heckBox.setSelected(param.getValue().isSelected());
            param.getValue().setTopoRules(CustomClass.getTopoRuleTypeItems(param.getValue().getSrcGeomType(), param.getValue().getRefGeomType()));
            return new SimpleObjectProperty<>(layerSelectControl);
        });
        //new PropertyValueFactory<>(""));
//        tableColumn_LayerA.setCellFactory(param -> new TableCell<TopoCheckItem, MapLayer>(){
//            private DocumentItem documentItem;
//
//            @Override
//            public void startEdit() {
//                super.startEdit();
//                TopoCheckItem item = (TopoCheckItem) getTableRow().getItem();
//                if (item != null){
//                    LayerSelectControl layerSelectControl = new LayerSelectControl(item.getDocument(), "简单要素类|sfclsp;sfclsl;sfclsr");
//                    layerSelectControl.setOnSelectedItemChanged((observable, oldValue, newValue) -> {
//                        if (newValue != null) {
//                            addSrcData(newValue, 1);
//                        }
//                    });
//                    layerSelectControl.setOnShown(event -> {
//                        documentItem = layerSelectControl.getSelectedDocumentItem();
//                    });
//                    layerSelectControl.setOnHidden(event -> {
//                        if (documentItem != layerSelectControl.getSelectedDocumentItem() && layerSelectControl.getSelectedDocumentItem() instanceof MapLayer)
//                        {
//                            commitEdit((MapLayer) layerSelectControl.getSelectedDocumentItem());
//                        } else
//                        {
//                            cancelEdit();
//                        }
//                    });
//                    setGraphic(layerSelectControl);
//                }
//            }
//
//            @Override
//            public void commitEdit(MapLayer newValue) {
//                super.commitEdit(newValue);
//                TopoCheckItem item = (TopoCheckItem) getTableRow().getItem();
//                if (item != null)
//                {
//                    updateItem(newValue, false);
//                    //checkConvertItems();
//                    //setParams(item);
//                }
//            }
//
//            @Override
//            public void cancelEdit() {
//                super.cancelEdit();
//                updateItem(getItem(), false);
//            }
//
//            @Override
//            protected void updateItem(MapLayer item, boolean empty) {
//                super.updateItem(item, empty);
//                if (item != null && !empty){
//                    setText(item.getName());
//                    Image image = imageUnknown;
//                    switch (item.getGeometryType()){
//                        case GeomPnt:
//                            image = imagePnt;
//                            break;
//                        case GeomLin:
//                            image = imageLin;
//                            break;
//                        case GeomReg:
//                            image = imageReg;
//                            break;
//                        default:
//                            break;
//                    }
//                    setGraphic(new ImageView(image));
//                }
//            }
//        });
        TableColumn<TopoCheckItem, LayerSelectControl> tableColumn_LayerB = new TableColumn<>("图层B");
        tableColumn_LayerB.setCellValueFactory(param -> {
            if (param.getValue().getSrcUrl() != null && param.getValue().getSrcUrl().isEmpty()){
                return null;
            }
            LayerSelectControl layerSelectControl = new LayerSelectControl(param.getValue().getDocument(), "简单要素类|sfclsp;sfclsl;sfclsr");
            layerSelectControl.setOnSelectedItemChanged((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    addSrcData(newValue, 2);
                }
            });
            //checkBox.setSelected(param.getValue().isSelected());
            return new SimpleObjectProperty<>(layerSelectControl);
        });
        TableColumn<TopoCheckItem, ComboBox<TopologyRuleType>> tableColumn_TopoRule = new TableColumn<>("拓扑规则");
        tableColumn_TopoRule.setCellValueFactory(param -> {
            ComboBox<TopologyRuleType> comboBox = new ComboBox<>(FXCollections.observableArrayList(param.getValue().getTopoRules()));
            comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                TopoCheckItem tcio = tableView_Layer.getSelectionModel().getSelectedItem();
                if (tcio != null) {
                    tcio.getTopoRules().clear();
                    if ((tcio.getSrcGeomType() == GeomType.GeomLin && tcio.getRefGeomType() == GeomType.GeomUnknown
                            || tcio.getSrcGeomType() == GeomType.GeomReg && tcio.getRefGeomType() == GeomType.GeomUnknown)
                            && comboBox.getSelectionModel().getSelectedIndex() == 0) {
                        ArrayList<String> defaultItems = CustomClass.getDefaultTopoRuleItems(tcio.getSrcGeomType());
                        for (String str : defaultItems) {
                            tcio.getTopoRules().add(CustomClass.convertTopoRuleType(str));
                        }
                    } else {
                        tcio.getTopoRules().add(newValue);
                    }
                    this.initRuleDescription(tcio.getTopoRules());
                }
            });
            comboBox.setCellFactory(param1 -> {
                TopologyRuleTranslator spTopologyRuleTranslator = new TopologyRuleTranslator();
                ComboBoxListCell<TopologyRuleType> cell = new ComboBoxListCell<TopologyRuleType>() {
                    @Override
                    public void updateItem(TopologyRuleType item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == TopologyRuleType.Unknown) {
                            setText("默认");
                        } else {
                            setText(spTopologyRuleTranslator.convertTopologyRule(item));
                        }
                    }
                };

                return cell;
            });
            return new SimpleObjectProperty<>(comboBox);
        });
        tableView_Layer.getColumns().addAll(tableColumn_LayerA, tableColumn_LayerB, tableColumn_TopoRule);
        tableView_Layer.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox inputSettingBox = new VBox(zdToolBar_InputSetting, tableView_Layer);

        // endregion

        // region Parameter Setting

        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(4);
        numberTextField_Tolerance = new NumberTextField(new BigDecimal("0.0001"), numberFormat);
        checkBox_MaxErrorNum = new CheckBox("是否检查最大错误个数");
        numberTextField_MaxErrorNum = new NumberTextField(new BigDecimal(10000));
        NumberFormat numberFormat1 = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(4);
        numberTextField_MinArcLength = new NumberTextField(new BigDecimal("0.01"), numberFormat1);
        numberTextField_MinRegArea = new NumberTextField(new BigDecimal("0.01"), numberFormat1);
        checkBox_CurrentDisplayRange = new CheckBox("检查当前显示范围");

        GridPane parameterSettingPane = new GridPane();
        parameterSettingPane.setHgap(6);
        parameterSettingPane.setVgap(6);
        parameterSettingPane.add(new Label("容差:"), 0, 0);
        parameterSettingPane.add(numberTextField_Tolerance, 1, 0);
        parameterSettingPane.add(checkBox_MaxErrorNum, 0, 1, 2, 1);
        parameterSettingPane.add(new Label("是否检查最大错误个数:"), 0, 2);
        parameterSettingPane.add(numberTextField_MaxErrorNum, 1, 2);
        parameterSettingPane.add(new Label("是否检查最大错误个数:"), 0, 3);
        parameterSettingPane.add(numberTextField_MinArcLength, 1, 3);
        parameterSettingPane.add(new Label("是否检查最大错误个数:"), 0, 4);
        parameterSettingPane.add(numberTextField_MinRegArea, 1, 4);
        parameterSettingPane.add(checkBox_CurrentDisplayRange, 0, 5, 2, 1);

        // endregion

        // region Image And Describe

        checkBox_DisplayError = new CheckBox("显示错误(红色部分)");
        imageView_Display = new ImageView(new Image(getClass().getResourceAsStream("/Png_TopoCheck_32.png")));
        textArea_Describe = new TextArea("默认检查规则:圈必须封闭(区)、圈必须有足够的坐标点(区)、洞必须在壳内(区)、洞必须与洞分离(区)、必须没有狭小区(区)、必须不能自相交(区)、边线上不能有重复点(区)、必须没有碎小区(区)");
        textArea_Describe.setWrapText(true);

        VBox imageAndDescribeBox = new VBox(checkBox_DisplayError, imageView_Display, textArea_Describe);

        // endregion

        // region TitlePane(Group)

        TitledPane inputSettingGroupPane = new TitledPane("输入设置", inputSettingBox);
        inputSettingGroupPane.setCollapsible(false);
        TitledPane parameterSettingGroupPane = new TitledPane("参数设置", parameterSettingPane);
        parameterSettingGroupPane.setCollapsible(false);
        TitledPane imageAndDescribeGroupPane = new TitledPane("规则图示及描述", imageAndDescribeBox);
        imageAndDescribeGroupPane.setCollapsible(false);

        // endregion

        // region Layout

        VBox leftVBox = new VBox(inputSettingGroupPane, parameterSettingGroupPane);
        VBox rightVBox = new VBox(imageAndDescribeGroupPane);
        HBox hBox = new HBox(leftVBox, rightVBox);

        // endregion

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setContent(hBox);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        //dialogPane.setPrefHeight(417);
        button_OK = (Button) dialogPane.lookupButton(ButtonType.OK);
        button_OK.addEventFilter(ActionEvent.ACTION, this::button_OK_Click);


        if (this.mapControl == null) {
            checkBox_CurrentDisplayRange.setDisable(true);
        }

        addBlankTableRow();

    }

    // private method

    private void addBlankTableRow() {
        tableView_Layer.getItems().add(new TopoCheckItem(document));
        tableView_Layer.getSelectionModel().select(tableView_Layer.getItems().size() - 1);
    }

    private void addSrcData(LayerSelectComboBoxItem item, int flag) {
        GeomType type = GeomType.GeomUnknown;
        SRefData srefData = null;
        String url = item.getDocumentItemUrl();
        if (!url.isEmpty()) {
            if (!url.toLowerCase().startsWith("gdbp://") && !url.toLowerCase().startsWith("file:///")) {
                url = "file:///" + url;
            }
            SFeatureCls sfcls = new SFeatureCls();
            if (sfcls.openByURL(url) > 0) {
                type = sfcls.getGeomType();
                srefData = sfcls.getGDataBase().getSRef(sfcls.getsrID());
                sfcls.close();
            }
        }
        if (item.getDocumentItem() instanceof MapLayer) {
            MapLayer layer = (MapLayer) item.getDocumentItem();
            if (layer != null) {
                SFeatureCls sfcls = (SFeatureCls) layer.getData();
                if (sfcls != null) {
                    type = sfcls.getGeomType();
                    srefData = sfcls.getGDataBase().getSRef(sfcls.getsrID());
                    url = layer.getURL();
                }
            }
        }

        TopoCheckItem tcio = tableView_Layer.getSelectionModel().getSelectedItem();
        if (tcio != null) {
            if (flag == 1) {
                tcio.setSrcGeomType(type);
                tcio.setSrcUrl(url);
//                this.gridView1.SetFocusedRowCellValue(this.gridView1.Columns[0], item);
            } else if (flag == 2) {
                tcio.getTopoRules().clear();
                tcio.setRefGeomType(type);
                tcio.setRefUrl(url);
//                this.gridView1.SetFocusedRowCellValue(this.gridView1.Columns[1], item);
            }
            ArrayList<String> items = CustomClass.getTopoRuleStringItems(tcio.getSrcGeomType(), tcio.getRefGeomType());
            if (items.size() != 0) {
                if (tcio.getSrcGeomType() == GeomType.GeomLin && tcio.getRefGeomType() == GeomType.GeomUnknown
                        || tcio.getSrcGeomType() == GeomType.GeomReg && tcio.getRefGeomType() == GeomType.GeomUnknown) {
                    ArrayList<String> defaultItems = CustomClass.getDefaultTopoRuleItems(tcio.getSrcGeomType());
                    for (String str : defaultItems) {
                        tcio.getTopoRules().add(CustomClass.convertTopoRuleType(str));
                    }
                } else {
                    tcio.getTopoRules().add(CustomClass.convertTopoRuleType(items.get(0)));
                }
//                this.gridView1.SetFocusedRowCellValue(this.gridView1.Columns[2], items.get(0));
            }
        }
        this.initRuleDescription(tcio.getTopoRules());
        if (isLastEditRow(tableView_Layer.getSelectionModel().getSelectedIndex())) {
            this.addBlankTableRow();
        }
        this.setTolerance();
    }

    /**
     * 根据规则种类更改图片说明信息
     *
     * @param topoRuleList 拓扑规则类型
     */
    private void initRuleDescription(ArrayList<TopologyRuleType> topoRuleList) {
        boolean showError = checkBox_DisplayError.isSelected();
        if (topoRuleList.size() != 0) {
            TopologyRuleType topoRule = topoRuleList.get(0);
            String description = "";
            TopologyRuleTranslator sprtl = new TopologyRuleTranslator();
            description = sprtl.describeTopologyRule(topoRule);
            // 1.区规则 - 区图层
            if (TopologyRuleType.Must_Be_Single_Part_Reg.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustBeSinglePartE_Reg_135_200.png" : "/Png_MustBeSinglePart_Reg_135_200.png")));
            } else if (TopologyRuleType.Ring_Must_Closed_Reg.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_RingMustClosedE_Reg_135_200.png" : "/Png_RingMustClosed_Reg_135_200.png")));
            } else if (TopologyRuleType.Ring_Must_Have_Enough_Points_Reg.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_RingMustHaveEnoughPointsE_Reg_135_200.png" : "/Png_RingMustHaveEnoughPoints_Reg_135_200.png")));
            } else if (TopologyRuleType.Hole_Must_Inside_Shell_Reg.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_HoleMustInsideShellE_Reg_135_200.png" : "/Png_HoleMustInsideShell_Reg_135_200.png")));
            } else if (TopologyRuleType.Hole_Must_Disjoint_Hole_Reg.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_HoleMustDisjointHoleE_Reg_135_200.png" : "/Png_HoleMustDisjointHole_Reg_135_200.png")));
            } else if (TopologyRuleType.Must_Not_Thin_Reg_Reg.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustNotThinRegE_Reg_135_200.png" : "/Png_MustNotThinReg_Reg_135_200.png")));
            } else if (TopologyRuleType.Must_Not_Self_Intersect_Reg.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustNotSelfIntersectE_Reg_135_200.png" : "/Png_MustNotSelfIntersect_Reg_135_200.png")));
            } else if (TopologyRuleType.Must_Not_Have_Gaps_Reg.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustNotHaveGapsE_Reg_135_200.png" : "/Png_MustNotHaveGaps_Reg_135_200.png")));
            } else if (TopologyRuleType.Must_Not_Overlap_Reg.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustNotOverlapE_Reg_135_200.png" : "/Png_MustNotOverlap_Reg_135_200.png")));
            } else if (TopologyRuleType.Must_Not_Have_Same_Dot_Reg.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustNotHaveSameDotE_Reg_135_200.png" : "/Png_MustNotHaveSameDot_Reg_135_200.png")));
            } else if (TopologyRuleType.Must_Not_Small_Reg_Reg.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustNotSmallRegE_Reg_135_200.png" : "/Png_MustNotSmallReg_Reg_135_200.png")));
                // 1.区规则 - 区区图层
            } else if (TopologyRuleType.Must_Be_Covered_By_Feature_Class_Of_RegReg.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustBeCoveredByFeatureClassOfE_Reg_Reg_135_200.png" : "/Png_MustBeCoveredByFeatureClassOf_Reg_Reg_135_200.png")));
            } else if (TopologyRuleType.Must_Cover_Each_Other_RegReg.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustCoverEachOtherE_Reg_Reg_135_200.png" : "/Png_MustCoverEachOther_Reg_Reg_135_200.png")));
            } else if (TopologyRuleType.Must_Be_Covered_By_RegReg.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustBeCoveredByE_Reg_Reg_135_200.png" : "/Png_MustBeCoveredBy_Reg_Reg_135_200.png")));
            } else if (TopologyRuleType.Must_Not_Overlap_With_RegReg.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustNotOverlapWithE_Reg_Reg_135_200.png" : "/Png_MustNotOverlapWith_Reg_Reg_135_200.png")));
            } else if (TopologyRuleType.Boundary_Must_Be_Covered_By_Boundary_Of_RegReg.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_BoundaryMustBeCoveredByBoundaryOfE_Reg_Reg_135_200.png" : "/Png_BoundaryMustBeCoveredByBoundaryOf_Reg_Reg_135_200.png")));
                // 1.区规则 - 区线图层
            } else if (TopologyRuleType.Boundary_Must_Be_Covered_By_RegLin.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_BoundaryMustBeCoveredByE_Reg_Lin_135_200.png" : "/Png_BoundaryMustBeCoveredBy_Reg_Lin_135_200.png")));
                // 1.区规则 - 区点图层
            } else if (TopologyRuleType.Contains_Point_RegPnt.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_ContainsPointE_Reg_Pnt_135_200.png" : "/Png_ContainsPoint_Reg_Pnt_135_200.png")));
            } else if (TopologyRuleType.Contains_One_Pnt_RegPnt.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_ContainsOnePntE_Reg_Pnt_135_200.png" : "/Png_ContainsOnePnt_Reg_Pnt_135_200.png")));
                // 2.线规则 - 线图层
            } else if (TopologyRuleType.Must_Be_Single_Part_Lin.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustBeSinglePartE_Lin_135_200.png" : "/Png_MustBeSinglePart_Lin_135_200.png")));
            } else if (TopologyRuleType.Must_Not_Short_Arc_Lin.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustNotShortArcE_Lin_135_200.png" : "/Png_MustNotShortArc_Lin_135_200.png")));
            } else if (TopologyRuleType.Must_Not_Overlap_Lin.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustNotOverlapE_Lin_135_200.png" : "/Png_MustNotOverlap_Lin_135_200.png")));
            } else if (TopologyRuleType.Must_Not_Intersect_Lin.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustNotIntersectE_Lin_135_200.png" : "/Png_MustNotIntersect_Lin_135_200.png")));
            } else if (TopologyRuleType.Must_Not_Have_Dangles_Lin.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustNotHaveDanglesE_Lin_135_200.png" : "/Png_MustNotHaveDangles_Lin_135_200.png")));
            } else if (TopologyRuleType.Must_Not_Have_Pseudo_Nodes_Lin.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustNotHavePseudoNodesE__Lin_135_200.png" : "/Png_MustNotHavePseudoNodes__Lin_135_200.png")));
            } else if (TopologyRuleType.Must_Not_Self_Overlap_Lin.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustNotSelfOverlapE_Lin_135_200.png" : "/Png_MustNotSelfOverlap_Lin_135_200.png")));
            } else if (TopologyRuleType.Must_Not_Self_Intersect_Lin.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustNotSelfIntersectE_Lin_135_200.png" : "/Png_MustNotSelfIntersect_Lin_135_200.png")));
            } else if (TopologyRuleType.Must_Not_Have_Same_Dot_Lin.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustNotHaveSameDotE_Lin_135_200.png" : "/Png_MustNotHaveSameDot_Lin_135_200.png")));
                // 2.线规则 - 线区图层
            } else if (TopologyRuleType.Must_Be_Covered_By_Boundary_Of_LinReg.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustBeCoveredByBoundaryOfE_Lin_Reg_135_200.png" : "/Png_MustBeCoveredByBoundaryOf_Lin_Reg_135_200.png")));
            } else if (TopologyRuleType.Must_Be_Inside_LinReg.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustBeInsideE_Lin_Reg_135_200.png" : "/Png_MustBeInside_Lin_Reg_135_200.png")));
                // 2.线规则 - 线线图层
            } else if (TopologyRuleType.Must_Be_Covered_By_Feature_Class_Of_LinLin.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustBeCoveredByFeature_ClassOfE_Lin_Lin_135_200.png" : "/Png_MustBeCoveredByFeature_ClassOf_Lin_Lin_135_200.png")));
            } else if (TopologyRuleType.Must_Not_Overlap_With_LinLin.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustNotOverlapWithE_Lin_Lin_135_200.png" : "/Png_MustNotOverlapWith_Lin_Lin_135_200.png")));
            } else if (TopologyRuleType.Must_Not_Intersect_With_LinLin.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustNotIntersectWithE_Lin_Lin_135_200.png" : "/Png_MustNotIntersectWith_Lin_Lin_135_200.png")));
                // 2.线规则 - 线点图层
            } else if (TopologyRuleType.Endpoint_Must_Be_Covered_By_LinPnt.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_EndpointMustBeCoveredByE_Lin_Pnt_135_200.png" : "/Png_EndpointMustBeCoveredBy_Lin_Pnt_135_200.png")));
                // 3.点规则 - 点图层
            } else if (TopologyRuleType.Must_Be_Single_Part_Pnt.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustBeSinglePartE_Pnt_135_200.png" : "/Png_MustBeSinglePart_Pnt_135_200.png")));
            } else if (TopologyRuleType.Must_Be_Disjoint_Pnt.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustBeDisjointE_Pnt_135_200.png" : "/Png_MustBeDisjoint_Pnt_135_200.png")));
                // 3.点规则 - 点区图层
            } else if (TopologyRuleType.Must_Be_Covered_By_Boundary_Of_PntReg.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustBeCoveredByBoundaryOfE_Pnt_Reg_135_200.png" : "/Png_MustBeCoveredByBoundaryOf_Pnt_Reg_135_200.png")));
            } else if (TopologyRuleType.Must_Be_Properly_Inside_PntReg.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustBeProperlyInsideE_Pnt_Reg_135_200.png" : "/Png_MustBeProperlyInside_Pnt_Reg_135_200.png")));
            } else if (TopologyRuleType.Must_Be_Properly_Inside_And_Reg_Contains_One_Pnt_PntReg.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Must_Be_Properly_Inside_And_Reg_Contains_One_Pnt_Pnt_RegE_135_200.png" : "/Must_Be_Properly_Inside_And_Reg_Contains_One_Pnt_Pnt_Reg_135_200.png")));
                // 3.点规则 - 点线图层
            } else if (TopologyRuleType.Must_Be_Covered_By_PntLin.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustBeCoveredByE_Pnt_Lin_135_200.png" : "/Png_MustBeCoveredBy_Pnt_Lin_135_200.png")));
            } else if (TopologyRuleType.Must_Be_Covered_By_Endpoint_Of_PntLin.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustBeCoveredByEndpointOfE_Pnt_Lin_135_200.png" : "/Png_MustBeCoveredByEndpointOf_Pnt_Lin_135_200.png")));
                // 3.点规则 - 点点图层
            } else if (TopologyRuleType.Must_Coincide_With_PntPnt.equals(topoRule)) {
                imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_MustCoincideWithE_Pnt_Pnt_135_200.png" : "/Png_MustCoincideWith_Pnt_Pnt_135_200.png")));
            }
            if (topoRuleList.size() > 1) {
                if (topoRule == TopologyRuleType.Must_Not_Have_Same_Dot_Reg) {
                    imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_DefaultE_Reg_135_200.png" : "/Png_Default_Reg_135_200.png")));
                    description = "默认检查规则:圈必须封闭(区)、圈必须有足够的坐标点(区)、洞必须在壳内(区)、洞必须与洞分离(区)、必须没有狭小区(区)、必须不能自相交(区)、边线上不能有重复点(区)、必须没有碎小区(区)";
                }
                if (topoRule == TopologyRuleType.Must_Not_Have_Same_Dot_Lin) {
                    imageView_Display.setImage(new Image(getClass().getResourceAsStream(showError ? "/Png_DefaultE_Lin_135_200.png" : "/Png_Default_Lin_135_200.png")));
                    description = "默认检查规则:必须没有微短线(线)、不能自重叠(线)、不能自相交(线)、不能有重复点(线)";
                }
            }
            textArea_Describe.setText(description);
        }
    }

    /**
     * 是否是编辑状态下的最后一个空行
     *
     * @param rowIndex 要判断的行的rowIndex
     * @return 是最后一个空行返回true，否则返回false
     */
    private boolean isLastEditRow(int rowIndex) {
        return (/*this.gridView1.IsNewItemRow(rowHandle) || */rowIndex == tableView_Layer.getItems().size() - 1);
    }

    private ArrayList<TopoCheckItem> getTopoCheckItems() {
        ArrayList<TopoCheckItem> items = new ArrayList<>();
        for (int i = 0; i < tableView_Layer.getItems().size(); i++) {
            TopoCheckItem tcio = tableView_Layer.getItems().get(i);
            if (tcio.getSrcUrl() == null) {
                continue;
            }
            for (int j = i; j < tableView_Layer.getItems().size(); j++) {
                TopoCheckItem curTcio = tableView_Layer.getItems().get(j);

                if (curTcio.getSrcUrl() == null) {
                    continue;
                }
                TopoCheckItem rtnTcio = getUrlAndGeomTypeSameInItems(items, curTcio);
                if (rtnTcio != null) {
                    if (curTcio.getTopoRules().size() != 0) {
                        for (TopologyRuleType rule : curTcio.getTopoRules()) {
                            if (!rtnTcio.getTopoRules().contains(rule)) {
                                rtnTcio.getTopoRules().add(rule);
                            }
                        }
                    }
                } else {
                    items.add(curTcio);
                }
            }
        }
        return items;
    }

    private TopoCheckItem getUrlAndGeomTypeSameInItems(ArrayList<TopoCheckItem> items, TopoCheckItem tcio) {
        TopoCheckItem rtnTcio = null;
        for (TopoCheckItem item : items) {
            if (tcio.getSrcUrl() == item.getSrcUrl() && tcio.getRefUrl() == item.getRefUrl() &&
                    tcio.getSrcGeomType() == item.getSrcGeomType() && tcio.getRefGeomType() == item.getRefGeomType()) {
                rtnTcio = item;
                break;
            }
        }
        return rtnTcio;
    }

    /**
     * 是否设置微短线的最小弧段长度和碎小区的最小区面积
     */
    private void isSetMinArcLengthOrMinRegArea() {
        if (tableView_Layer.getItems().size() != 0) {
            for (TopoCheckItem item : tableView_Layer.getItems()) {
                if (item.getTopoRules().size() != 0) {
                    for (TopologyRuleType ruleType : item.getTopoRules()) {
                        if (ruleType == TopologyRuleType.Must_Not_Short_Arc_Lin) {
                            numberTextField_MinArcLength.setDisable(false);//this.extraOption.IsSetMinArcLength = true;
                            break;
                        } else {
                            numberTextField_MinArcLength.setDisable(true);//this.extraOption.IsSetMinArcLength = false;
                        }
                        if (ruleType == TopologyRuleType.Must_Not_Small_Reg_Reg) {
                            numberTextField_MinRegArea.setDisable(false);//this.extraOption.IsSetMinRegArea = true;
                            break;
                        } else {
                            numberTextField_MinRegArea.setDisable(true);//this.extraOption.IsSetMinRegArea = false;
                        }
                    }
                }
            }
        }
    }

    /**
     * 开始检查
     *
     * @param tcio
     * @return
     */
    private boolean beginCheck(TopoCheckItem tcio) {
        boolean rtn = false;
        if (tcio != null) {
            SFeatureCls sfcls0 = new SFeatureCls();
            if (!sfcls0.hasOpened() && sfcls0.openByURL(tcio.getSrcUrl()) > 0) {
                this.sfclsList.add(sfcls0);
                SFeatureCls sfcls1 = new SFeatureCls();
                if (tcio.getRefUrl() == null) {
                    sfcls1 = null;
                } else if (!sfcls1.hasOpened() && sfcls1.openByURL(tcio.getRefUrl()) > 0) {
                    this.sfclsList.add(sfcls1);
                }
                TopologyChecker topologyChecker = new TopologyChecker();
                TopologyCheckOption topologyCheckOption = new TopologyCheckOption();
                if (topologyChecker.setCheckSfcls(sfcls0, sfcls1) > 0) {
                    if (tcio.getTopoRules().size() != 0) {
                        for (TopologyRuleType topoRule : tcio.getTopoRules()) {
                            topologyChecker.addTopologyRule(topoRule);
                        }
                    }
//                    RegisterCallback();
//                    spTopologyCheckParameter.ProcessCallback = this.logEventReceiver;
                    topologyCheckOption.setRange(this.mapControl != null ?
                            checkBox_CurrentDisplayRange.isSelected() ? null : null : null);
                    topologyCheckOption.setMaxErrorCount(numberTextField_MaxErrorNum.getNumber().intValue()); //= this.extraOption.MaxErrorCount;
                    topologyCheckOption.setTolerance(numberTextField_Tolerance.getNumber().doubleValue());// = this.extraOption.Tolerance;
                    if (!numberTextField_MinArcLength.isDisable())//this.extraOption.IsSetMinArcLength)
                    {
                        topologyCheckOption.setMinArcLength(numberTextField_MinArcLength.getNumber().doubleValue());// = this.extraOption.MinArcLength;
                    }
                    if (!numberTextField_MinRegArea.isDisable())//this.extraOption.IsSetMinRegArea)
                    {
                        topologyCheckOption.setMinRegArea(numberTextField_MinRegArea.getNumber().doubleValue());// = this.extraOption.MinRegArea;
                    }
                    if (this.topologyErrorManager == null) {
                        this.topologyErrorManager = new TopologyErrorManager();
                    } else {
                        this.topologyErrorManager.clearTopologyError();
                    }
                    if (topologyChecker.checkTopology(topologyCheckOption, this.topologyErrorManager) > 0) {
                        rtn = true;
                    }
//                    spTopologyCheckParameter.Dispose();
                    topologyChecker.dispose();
                }
            }
        }
        return rtn;
    }

    /**
     * 根据参照系设置容差
     */
    private void setTolerance() {
        if (tableView_Layer.getItems().size() > 0) {
            TopoCheckItem tcio = tableView_Layer.getItems().get(0);
            if (tcio != null) {
                String url = tcio.getSrcUrl();
                if (!url.isEmpty()) {
                    SFeatureCls sfcls = new SFeatureCls();
                    if (sfcls.openByURL(url) > 0) {
                        SRefData srefData = sfcls.getGDataBase().getSRef(sfcls.getsrID());
                        try {
                            numberTextField_Tolerance.setNumber(BigDecimal.valueOf(CustomClass.getToleranceBySRefData(CustomClass.TOLERANCE, srefData)));
                        } catch (Exception ex) {
                            MessageBox.information(url + "的参考系设置有误!");
                        }
                        numberTextField_MinRegArea.setNumber(numberTextField_Tolerance.getNumber().multiply(new BigDecimal(100)));
                        numberTextField_MinArcLength.setNumber(numberTextField_Tolerance.getNumber().multiply(new BigDecimal(100)));
                        sfcls.close();
                    }
                }
            }
        }
    }

    // public method

    public TopologyErrorManager getTopologyErrorManager() {
        return topologyErrorManager;
    }

    public ArrayList<SFeatureCls> getSfclsList() {
        return sfclsList;
    }

    public double getTolerance() {
        return 0;
    }

    // control event

    private void button_OK_Click(ActionEvent event) {
//        if (this.extraOption != null && this.extraOption.Tolerance == 0)
        if (numberTextField_Tolerance.getNumber().doubleValue() == 0) {
            MessageBox.information("容差必须为正数!");
            return;
        }
        ArrayList<TopoCheckItem> items = getTopoCheckItems();
        isSetMinArcLengthOrMinRegArea();
        if (items.size() == 0) {
            MessageBox.information("请选择数据源!");
            return;
        } else {
            boolean isError = false;
            for (TopoCheckItem item : items) {
                if (!item.getSrcUrl().isEmpty()) {
                    SFeatureCls sfcls = new SFeatureCls();
                    if (sfcls.openByURL(item.getSrcUrl()) > 0 && sfcls.getObjCount() == 0) {
                        isError = true;
                        sfcls.close();
                    }
                }
                if (!item.getRefUrl().isEmpty()) {
                    SFeatureCls sfcls = new SFeatureCls();
                    if (sfcls.openByURL(item.getRefUrl()) > 0 && sfcls.getObjCount() == 0) {
                        isError = true;
                        sfcls.close();
                    }
                }
            }
            if (isError) {
                MessageBox.information("源数据错误或不存在,请选择正确的源数据!");
                return;
            }
        }
//        TimeSpan tsBegin = new TimeSpan(DateTime.Now.Ticks);
//        InitPlugin.SetKeyValue(MapGIS.MapEditor.Plugin.Properties.Resources.String_TopoCheck, "[" + MapGIS.MapEditor.Plugin.Properties.Resources.String_TopoCheck + "]:" + DateTime.Now.ToString() + ":" + MapGIS.MapEditor.Plugin.Properties.Resources.String_BeginTopoCheck + "\r\n");
//        CheckProcessForm form = new CheckProcessForm(items, this.extraOption);
//        if (form.ShowDialog() == DialogResult.OK)
//        {
//            TimeSpan tsEnd = new TimeSpan(DateTime.Now.Ticks);
//            TimeSpan tsEx = tsEnd.Subtract(tsBegin).Duration();
//            string timeSpend = InitPlugin.FormatTime(tsEx);
//            InitPlugin.SetKeyValue(MapGIS.MapEditor.Plugin.Properties.Resources.String_TopoCheck, "[" + MapGIS.MapEditor.Plugin.Properties.Resources.String_TopoCheck + "]:" + DateTime.Now.ToString() + ":" + MapGIS.MapEditor.Plugin.Properties.Resources.String_FinishTopoCheck + timeSpend + "\r\n");
//            bool rtn = form.HasChecksSuccess;
//            if (rtn)
//            {
//                this.sfclsList = form.SfclsList;
//                this.spTopologyErrorManager = form.SpTopologyErrorManager;
//                this.DialogResult = DialogResult.OK;
//            }
//            else
//            {
//                bool isSucceed = MapGIS.Desktop.UI.Controls.MapGISErrorForm.ShowLastError();
//                if (!isSucceed)
//                    XMessageBox.Information(Resources.String_TopoCheckFaild);
//            }
//            form.Dispose();
//        }

        {
            boolean hasChecksSuccess = false;
            int allCount = items.size();
            int index = 0;
            for (int i = 0; i < allCount; i++) {
                TopoCheckItem tcio = items.get(i);
                if (tcio == null) {
                    continue;
                }
                String param = (i + 1) + "/" + allCount;
                int allCur1 = (int) ((i) * 100.0 / allCount);
//                this.Invoke(new UpdateUIHandler(UpdateUI), param);
//                this.Invoke(new ProcessHandler(AllProcess), allCur1);
//                this.Invoke(new ProcessHandler(CurProcess), 0);
                boolean checkResult = beginCheck(tcio);
                int allCur2 = (int) ((i + 1) * 100.0 / allCount);
//                this.Invoke(new ProcessHandler(AllProcess), allCur2);
                if (checkResult) {
                    index++;
                }
            }
            if (index > 0) {
                hasChecksSuccess = true;
            } else {
                hasChecksSuccess = false;
            }
            if (hasChecksSuccess) {
//                this.sfclsList = form.SfclsList;
//                this.spTopologyErrorManager = form.SpTopologyErrorManager;
//                this.DialogResult = DialogResult.OK;
            } else {
//                bool isSucceed = MapGIS.Desktop.UI.Controls.MapGISErrorForm.ShowLastError();
//                if (!isSucceed)
//                    XMessageBox.Information(Resources.String_TopoCheckFaild);
            }
        }

    }
}

class TopoCheckItem {
    private Document document;
    private GeomType srcGeomType;
    private GeomType refGeomType;
    private String srcUrl;
    private String refUrl;
    private ArrayList<TopologyRuleType> topoRules;

    public TopoCheckItem(Document document) {
        this.document = document;
        this.srcGeomType = GeomType.GeomUnknown;
        this.refGeomType = GeomType.GeomUnknown;
        this.topoRules = new ArrayList<>();
    }

    public Document getDocument() {
        return document;
    }

    public GeomType getSrcGeomType() {
        return srcGeomType;
    }

    public void setSrcGeomType(GeomType srcGeomType) {
        this.srcGeomType = srcGeomType;
    }

    public GeomType getRefGeomType() {
        return refGeomType;
    }

    public void setRefGeomType(GeomType refGeomType) {
        this.refGeomType = refGeomType;
    }

    public String getSrcUrl() {
        return srcUrl;
    }

    public void setSrcUrl(String srcUrl) {
        this.srcUrl = srcUrl;
    }

    public String getRefUrl() {
        return refUrl;
    }

    public void setRefUrl(String refUrl) {
        this.refUrl = refUrl;
    }

    public ArrayList<TopologyRuleType> getTopoRules() {
        return topoRules;
    }

    public void setTopoRules(ArrayList<TopologyRuleType> topoRules) {
        this.topoRules = topoRules;
    }
}

class ExtraOption {
    //检查范围
    private Rect range = null;
    //容差
    private double tolerance = 0.0001;
    //最大错误个数(默认全部检查)
    private int maxErrorCount = -1;
    //最小弧段长度（检查微短线时设置）
    private double minArcLength = 0.1;
    //最小的区面积（检查碎小区时设置）
    private double minRegArea = 0.01;
    //是否设置最小弧段长度
    private boolean isSetMinArcLength = false;
    //是否设置检查最大错误个数
    private boolean isSetMaxErrorCount = false;
    //是否设置最小的区面积
    private boolean isSetMinRegArea = false;

    public Rect getRange() {
        return range;
    }

    public void setRange(Rect range) {
        this.range = range;
    }

    public double getTolerance() {
        return tolerance;
    }

    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }

    public int getMaxErrorCount() {
        return maxErrorCount;
    }

    public void setMaxErrorCount(int maxErrorCount) {
        this.maxErrorCount = maxErrorCount;
    }

    public double getMinArcLength() {
        return minArcLength;
    }

    public void setMinArcLength(double minArcLength) {
        this.minArcLength = minArcLength;
    }

    public double getMinRegArea() {
        return minRegArea;
    }

    public void setMinRegArea(double minRegArea) {
        this.minRegArea = minRegArea;
    }

    public boolean isSetMinArcLength() {
        return isSetMinArcLength;
    }

    public void setSetMinArcLength(boolean setMinArcLength) {
        isSetMinArcLength = setMinArcLength;
    }

    public boolean isSetMaxErrorCount() {
        return isSetMaxErrorCount;
    }

    public void setSetMaxErrorCount(boolean setMaxErrorCount) {
        isSetMaxErrorCount = setMaxErrorCount;
    }

    public boolean isSetMinRegArea() {
        return isSetMinRegArea;
    }

    public void setSetMinRegArea(boolean setMinRegArea) {
        isSetMinRegArea = setMinRegArea;
    }
}
