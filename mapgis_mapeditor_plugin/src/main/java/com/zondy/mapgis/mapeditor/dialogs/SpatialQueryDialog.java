package com.zondy.mapgis.mapeditor.dialogs;

import com.zondy.mapgis.att.Record;
import com.zondy.mapgis.base.MessageBox;
import com.zondy.mapgis.base.Window;
import com.zondy.mapgis.controls.*;
import com.zondy.mapgis.controls.common.ButtonEdit;
import com.zondy.mapgis.controls.common.ButtonEditEvent;
import com.zondy.mapgis.controls.common.NumberTextField;
import com.zondy.mapgis.filedialog.FolderType;
import com.zondy.mapgis.filedialog.GDBSelectFolderDialog;
import com.zondy.mapgis.geodatabase.*;
import com.zondy.mapgis.geometry.*;
import com.zondy.mapgis.map.*;
import com.zondy.mapgis.map.Map;
import com.zondy.mapgis.sqlquery.SQLQueryDialog;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

public class SpatialQueryDialog extends Dialog {
    private RadioButton radioButton_QueryLayerA;
    private RadioButton radioButton_Distance;
    private RadioButton radioButton_QueryLayerB;
    private LayerSelectControl layerSelectControl_QueryLayerA;
    private NumberTextField numberTextField_Distance;
    private ComboBox<String> comboBox_Condition;
    private TableView<LayerSettingInfo> tableView_QueryLayerB;
    private TextField textField_Prefix;
    private TextField textField_Suffix;
    private ButtonEdit buttonEdit_SavePath;
    private Button button_OK;

    private Image imagePnt = new Image(getClass().getResourceAsStream("/Png_SfClsPnt_16.png"));
    private Image imageLin = new Image(getClass().getResourceAsStream("/Png_SfClsLin_16.png"));
    private Image imageReg = new Image(getClass().getResourceAsStream("/Png_SfClsReg_16.png"));
    private Image imageAnn = new Image(getClass().getResourceAsStream("/Png_ACls_16.png"));
    private char[] gdbInvalidChars = new char[]{'\\', '/', ':', '*', '?', '"', '<', '>', '|'};

    private MapControl mapControl;
    private ArrayList<MapLayer> mapLayers;
    private ArrayList<String> desURLs;
    private List<SelectSetItem> selectSetItems;

    public SpatialQueryDialog(MapControl mapControl) {
        setTitle("空间查询");
        this.mapLayers = new ArrayList<>();
        this.desURLs = new ArrayList<>();

        this.mapControl = mapControl;
        Map map = mapControl.getMap();

        // region Query Options

        radioButton_QueryLayerA = new RadioButton("采用查询图层A:");
        radioButton_QueryLayerA.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                layerSelectControl_QueryLayerA.setDisable(false);
                comboBox_Condition.setDisable(false);
                numberTextField_Distance.setDisable(true);
            }
        });
        radioButton_Distance = new RadioButton("查询距离地图中选择的图元:");
        radioButton_Distance.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                layerSelectControl_QueryLayerA.setDisable(true);
                comboBox_Condition.setDisable(false);
                numberTextField_Distance.setDisable(false);
            }
        });
        //过滤选择集中图层个数为1，且为简单要素类图层时
        //radioButton_Distance.setDisable(!(selectSetItems.size() == 1 && new MapLayer(selectSetItems.get(0).getLayer()).getGeometryType() != GeomType.GeomAnn && new MapLayer(selectSetItems.get(0).getLayer()).getGeometryType() != GeomType.GeomUnknown));
        radioButton_Distance.setDisable(true);
        radioButton_QueryLayerB = new RadioButton("只查询B中符合给定SQL查询条件的图元");
        radioButton_QueryLayerB.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                layerSelectControl_QueryLayerA.setDisable(true);
                comboBox_Condition.setDisable(true);
                numberTextField_Distance.setDisable(true);
            }
        });
        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(radioButton_QueryLayerA, radioButton_Distance, radioButton_QueryLayerB);
        layerSelectControl_QueryLayerA = new LayerSelectControl(map, "区|sfclsr");
        numberTextField_Distance = new NumberTextField(new BigDecimal(10));

        HBox hBox_QueryLayerA = new HBox(radioButton_QueryLayerA, layerSelectControl_QueryLayerA);
        HBox.setHgrow(layerSelectControl_QueryLayerA, Priority.ALWAYS);
        HBox hBox_Distance = new HBox(radioButton_Distance, numberTextField_Distance, new Label("图元单位"));
        HBox.setHgrow(numberTextField_Distance, Priority.ALWAYS);
        VBox vBox_QueryOptions = new VBox(5, hBox_QueryLayerA, hBox_Distance, radioButton_QueryLayerB);

//        GridPane queryOptionsPane = new GridPane();
//        queryOptionsPane.setHgap(3);
//        queryOptionsPane.setVgap(3);
//        queryOptionsPane.add(radioButton_QueryLayerA, 0, 0, 1, 1);
//        queryOptionsPane.add(layerSelectControl_QueryLayerA, 1, 0, 2, 1);
//        queryOptionsPane.add(radioButton_Distance, 0, 1, 1, 1);
//        queryOptionsPane.add(numberTextField_Distance, 1, 1, 1, 1);
//        queryOptionsPane.add(new Label("图元单位"), 2, 1, 1, 1);
//        queryOptionsPane.add(radioButton_QueryLayerB, 0, 2, 3, 1);

        // endregion

        // region Query Condition

        comboBox_Condition = new ComboBox<>(FXCollections.observableArrayList(
                "包含", "相离", "相交", "外包矩形相交"));

        HBox hBox_QueryCondition = new HBox(new Label("查询与区块边界"), comboBox_Condition, new Label("B中的图元"));

//        GridPane queryConditionPane = new GridPane();
//        queryConditionPane.setHgap(3);
//        queryConditionPane.setVgap(1);
//        queryConditionPane.add(new Label("查询与区块边界"), 0, 0);
//        queryConditionPane.add(comboBox_Condition, 1, 0);
//        queryConditionPane.add(new Label("B中的图元"), 2, 0);

        // endregion

        // region Query Layer Setting

        tableView_QueryLayerB = new TableView<>();
        tableView_QueryLayerB.setPrefHeight(150);
        TableColumn<LayerSettingInfo, CheckBox> colIsSelected = new TableColumn<>("");
        colIsSelected.setCellValueFactory(param -> {
            CheckBox checkBox = new CheckBox();
            checkBox.setSelected(param.getValue().isSelected());
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                param.getValue().setSelected(newValue);
                boolean hasQueryLayer = false;
                if (newValue) {
                    hasQueryLayer = true;
                } else {
                    for (int i = 0; i < tableView_QueryLayerB.getItems().size(); i++) {
                        if (i != tableView_QueryLayerB.getFocusModel().getFocusedIndex() && tableView_QueryLayerB.getItems().get(i).isSelected()) {
                            hasQueryLayer = true;
                            break;
                        }
                    }
                }
                button_OK.setDisable(!hasQueryLayer);
            });
            return new SimpleObjectProperty<>(checkBox);
        });
        TableColumn<LayerSettingInfo, HBox> colLayerName = new TableColumn<>("被裁剪图层");
        colLayerName.setCellValueFactory(param -> {
            GeomType geomType = param.getValue().getMapLayer().getGeometryType();
            Image image = imagePnt;
            switch (geomType) {
                case GeomPnt:
                    image = imagePnt;
                    break;
                case GeomLin:
                    image = imageLin;
                    break;
                case GeomReg:
                    image = imageReg;
                    break;
                case GeomAnn:
                    image = imageAnn;
                    break;
                default:
                    break;
            }
            HBox hBox = new HBox(new ImageView(image), new Label(param.getValue().getLayerName()));
            return new SimpleObjectProperty<>(hBox);
        });
        TableColumn<LayerSettingInfo, TextField> colDesName = new TableColumn<>("目标类名称");
        colDesName.setCellValueFactory(param -> {
            TextField textField = new TextField(param.getValue().getDesName());
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                param.getValue().setDesName(newValue);
            });
            return new SimpleObjectProperty<>(textField);
        });
        TableColumn<LayerSettingInfo, ButtonEdit> colSql = new TableColumn<>("SQL 表达式");
        colSql.setCellValueFactory(param -> {
            ButtonEdit buttonEdit = new ButtonEdit();
            buttonEdit.setTextEditable(true);
            IBasCls basCls = param.getValue().getMapLayer().getData();
            if (basCls instanceof IVectorCls) {
                buttonEdit.setOnButtonClick(event -> {
                    SQLQueryDialog sqlQueryDialog = new SQLQueryDialog((IVectorCls) basCls, param.getValue().getSql());
                    Optional<ButtonType> response = sqlQueryDialog.showAndWait();
                    if (response.isPresent() && response.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                        param.getValue().setSql(sqlQueryDialog.getSQLText());
                        tableView_QueryLayerB.refresh();
                    }
                });
            }
            buttonEdit.textProperty().addListener((observable, oldValue, newValue) -> {
                param.getValue().setSql(newValue);
                tableView_QueryLayerB.refresh();
            });
            return new SimpleObjectProperty<>(buttonEdit);
        });

        tableView_QueryLayerB.getColumns().addAll(colIsSelected, colLayerName, colDesName, colSql);

        textField_Prefix = new TextField();
        textField_Prefix.textProperty().addListener(textField_TextChanged_ChangeListener);
        textField_Suffix = new TextField();
        textField_Suffix.textProperty().addListener(textField_TextChanged_ChangeListener);
        buttonEdit_SavePath = new ButtonEdit();
        buttonEdit_SavePath.setTextEditable(true);
        buttonEdit_SavePath.setOnButtonClick(buttonEdit_SavePath_ButtonClick_EventHandler);

        GridPane gridPane_QueryLayerSetting = new GridPane();
        gridPane_QueryLayerSetting.setHgap(4);
        gridPane_QueryLayerSetting.setVgap(3);
        gridPane_QueryLayerSetting.add(tableView_QueryLayerB, 0, 0, 4, 1);
        gridPane_QueryLayerSetting.add(new Label("结果类名统加前缀:"), 0, 1, 1, 1);
        gridPane_QueryLayerSetting.add(textField_Prefix, 1, 1, 1, 1);
        gridPane_QueryLayerSetting.add(new Label("后缀:"), 2, 1, 1, 1);
        gridPane_QueryLayerSetting.add(textField_Suffix, 3, 1, 1, 1);
        gridPane_QueryLayerSetting.add(new Label("结果保存目录:"), 0, 2, 1, 1);
        gridPane_QueryLayerSetting.add(buttonEdit_SavePath, 1, 2, 3, 1);
        gridPane_QueryLayerSetting.getRowConstraints().add(new RowConstraints(150));

        // endregion

        // region TitlePane(Group)

        TitledPane titledPane_QueryOptions = new TitledPane("查询选项", vBox_QueryOptions);
        titledPane_QueryOptions.setCollapsible(false);
        TitledPane titledPane_QueryCondition = new TitledPane("查询条件", hBox_QueryCondition);
        titledPane_QueryCondition.setCollapsible(false);
        TitledPane titledPane_QueryLayerSetting = new TitledPane("被查询图层B设置", gridPane_QueryLayerSetting);
        titledPane_QueryLayerSetting.setCollapsible(false);

        // endregion

        // region Layout

        GridPane gridPane = new GridPane();
        gridPane.setHgap(1);
        gridPane.setVgap(3);
        gridPane.add(titledPane_QueryOptions, 0, 0);
        gridPane.add(titledPane_QueryCondition, 0, 1);
        gridPane.add(titledPane_QueryLayerSetting, 0, 2);

        // endregion

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setContent(gridPane);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.setPrefHeight(480);
        dialogPane.setPrefHeight(540);
        button_OK = (Button) dialogPane.lookupButton(ButtonType.OK);
        button_OK.addEventFilter(ActionEvent.ACTION, this::button_OK_Click);

//        sets = map.GetSelectSet();
//        SelectList = sets.Get();
//        //过滤选择集中图层个数为1，且为简单要素类图层时
//        if (SelectList.Count == 1 && SelectList[0].Layer.GeometryType != GeomType.Ann && SelectList[0].Layer.GeometryType != GeomType.Unknown) {
//            this.radioButton2.Enabled = true;
//        }
//        this.selectSetItems = new ArrayList<>();// map.getSelectSet().get();
        radioButton_QueryLayerA.setSelected(true);
        //  遍历map和其中的组图层
        for (int i = 0; i < map.getLayerCount(); i++) {
            MapLayer mapLayer = map.getLayer(i);
            if (mapLayer instanceof VectorLayer && mapLayer.getGeometryType() != GeomType.GeomUnknown) {
                this.mapLayers.add(mapLayer);
            } else if (mapLayer instanceof GroupLayer) {
                addMapLayerFromGroupLayer((GroupLayer) mapLayer);
            }
        }
        //过滤区图层
        if (layerSelectControl_QueryLayerA.size() != 0) {
            layerSelectControl_QueryLayerA.selectFirstItem();
        } else {
            radioButton_QueryLayerA.setDisable(true);
            layerSelectControl_QueryLayerA.setDisable(true);
            if (!radioButton_Distance.isDisable()) {
                radioButton_Distance.setSelected(true);
            } else {
                radioButton_QueryLayerB.setSelected(true);
            }
        }
        comboBox_Condition.getSelectionModel().select(0);

        // 将遍历的矢量图层集添加到列表框中
        for (MapLayer layer : this.mapLayers) {
            LayerSettingInfo layerSettingInfo = new LayerSettingInfo(layer, "");
            layerSettingInfo.setSelected(layer.getState() != LayerState.UnVisible);
            tableView_QueryLayerB.getItems().add(layerSettingInfo);
        }

        button_OK.setDisable(tableView_QueryLayerB.getItems().size() == 0);

        //取默认保存路径
        for (MapLayer mapLayer : this.mapLayers) {
            String url = mapLayer.getURL();
            if (url == null || url.isEmpty()) {
                continue;
            }
            if (!url.contains("file:///")) {
                String savePath = url.substring(0, url.lastIndexOf('/'));
                savePath = savePath.substring(0, savePath.lastIndexOf('/'));
                buttonEdit_SavePath.setText(savePath);
                break;
            }
        }
    }


    // event

    private ChangeListener<String> textField_TextChanged_ChangeListener = (observable, oldValue, newValue) -> {
        for (LayerSettingInfo info : tableView_QueryLayerB.getItems()) {
            info.setDesName(textField_Prefix.getText() + info.getLayerName() + textField_Suffix.getText());
        }
        tableView_QueryLayerB.refresh();
    };

    private EventHandler<ButtonEditEvent> buttonEdit_SavePath_ButtonClick_EventHandler = event -> {
        GDBSelectFolderDialog dialog = new GDBSelectFolderDialog();
        dialog.setFolderType(FolderType.Disk_Folder | FolderType.MapGIS_DataBase | FolderType.MapGIS_Fds);
        Optional<String[]> optional = dialog.showAndWait();
        if (optional != null && optional.isPresent()) {
            String[] files = optional.get();
            if (files.length > 0) {
                buttonEdit_SavePath.setText(files[0]);
            }
        }
    };

    private void button_OK_Click(ActionEvent event) {
        String savePath = buttonEdit_SavePath.getText();
        if (savePath.isEmpty()) {
            MessageBox.information("请选择结果保存目录!");
            return;
        }
        if (savePath.toLowerCase().startsWith("gdbp://")) {
            String[] strs = savePath.split("/");
            DataBase db = DataBase.openByURL(strs[0] + "//" + strs[2] + "/" + strs[3]);
            if (db == null) {
                return;
            }
            for (int i = 0; i < tableView_QueryLayerB.getItems().size(); i++) {
                if (tableView_QueryLayerB.getItems().get(i).isSelected()) {
                    if (mapLayers.get(i).getGeometryType() == GeomType.GeomAnn) {
                        if (db.xClsIsExist(XClsType.XACls, tableView_QueryLayerB.getItems().get(i).getDesName().trim()) > 0) {
                            MessageBox.information("保存目录下已存在同名数据,请重新修改目的类名或保存目录!");
                            db.close();
                            return;
                        }
                    } else {
                        if (db.xClsIsExist(XClsType.XSFCls, tableView_QueryLayerB.getItems().get(i).getDesName().trim()) > 0) {
                            MessageBox.information("保存目录下已存在同名数据,请重新修改目的类名或保存目录!");
                            db.close();
                            return;
                        }
                    }
                }
            }
            db.close();
        } else {
            String aftername = "";
            for (int i = 0; i < tableView_QueryLayerB.getItems().size(); i++) {
                if (tableView_QueryLayerB.getItems().get(i).isSelected()) {
                    switch (mapLayers.get(i).getGeometryType()) {
                        case GeomAnn:
                        case GeomPnt:
                            aftername = ".wt";
                            break;
                        case GeomLin:
                            aftername = ".wl";
                            break;
                        case GeomReg:
                            aftername = ".wp";
                            break;
                        default:
                            break;
                    }
                    String name = tableView_QueryLayerB.getItems().get(i).getDesName().trim();
                    String path = savePath + File.separator + name + (name.toLowerCase().endsWith(aftername) ? "" : aftername);
                    File file = new File(path);
                    if (file.exists()) {
                        MessageBox.information("保存目录下已存在同名数据,请重新修改目的类名或保存目录!");
                        return;
                    }
                }
            }
        }
        spatialQuery();
    }

    private void spatialQuery() {
        QueryDef def = new QueryDef();
        this.desURLs.clear();
        for (int i = 0; i < tableView_QueryLayerB.getItems().size(); i++) {
            if (tableView_QueryLayerB.getItems().get(i).isSelected()) {
                String name = tableView_QueryLayerB.getItems().get(i).getDesName().trim();
                boolean includeInvalidChar = false;
                for (char c : this.gdbInvalidChars) {
                    if (name.indexOf(c) >= 0) {
                        includeInvalidChar = true;
                        break;
                    }
                }
                if (name.isEmpty() || includeInvalidChar || name.startsWith(" ")) {
                    MessageBox.information("输入名称存在不合法,请检查并重新输入!");
                    return;
                }
                String savePath = buttonEdit_SavePath.getText();
                if (savePath.toLowerCase().startsWith("gdbp://")) {
                    if (mapLayers.get(i).getGeometryType() == GeomType.GeomAnn) {
                        this.desURLs.add(savePath + "/acls/" + name);
                    } else {
                        this.desURLs.add(savePath + "/sfcls/" + name);
                    }
                } else {
                    String aftername = "";
                    switch (mapLayers.get(i).getGeometryType()) {
                        case GeomAnn:
                        case GeomPnt:
                            aftername = ".wt";
                            break;
                        case GeomLin:
                            aftername = ".wl";
                            break;
                        case GeomReg:
                            aftername = ".wp";
                            break;
                        default:
                            break;
                    }
                    this.desURLs.add(savePath + File.pathSeparator + name + (name.toLowerCase().endsWith(aftername) ? "" : aftername));
                }
            }
        }
        SpaQueryMode mode = SpaQueryMode.ModeDisJoint;
        switch (comboBox_Condition.getSelectionModel().getSelectedIndex()) {
            case 0:
                mode = SpaQueryMode.ModeContain;
                break;
            case 1:
                mode = SpaQueryMode.ModeDisJoint;
                break;
            case 2:
                mode = SpaQueryMode.ModeIntersect;
                break;
            case 3:
                mode = SpaQueryMode.ModeMBRIntersect;
                break;
            default:
                break;
        }
        // 设置类查询选项
//        SelectToClsOption selectoclsOption = new SelectToClsOption();
//        selectoclsOption.SetOption(SelToClsType.HasInfo, 1);
        // 进度条步长
//        int limitNo = this.desURLs.size();
        // 设置进度条
//        XProgress.WaitDialog wd = XProgress.CreateWaitDialog(new Win32Window(MapGIS.PlugUtility.XHelp.GetMainWindowHandle()));
//        XProgress.SetProgress(wd, MapGIS.MapEditor.Plugin.Properties.Resources.String_SpatialQuery, MapGIS.MapEditor.Plugin.Properties.Resources.String_QueryRate + MapGIS.MapEditor.Plugin.Properties.Resources.String_Colon, 0, limitNo, false);
//        TimeSpan tsBegin = new TimeSpan(DateTime.Now.Ticks);
//        InitPlugin.SetKeyValue(Resources.String_SpatialQuery, "[" + MapGIS.MapEditor.Plugin.Properties.Resources.String_SpatialQuery + "]:" + DateTime.Now.ToString() + ":" + MapGIS.MapEditor.Plugin.Properties.Resources.String_BeginSpatialQuery + "\r\n");
        boolean rtn = false;
        StringBuilder selectresult = new StringBuilder();
        if (radioButton_QueryLayerA.isSelected()) {
            // region 区查询
            if (tableView_QueryLayerB.getItems().size() == 0) {
                MessageBox.information("没有可查询的区图层A!");
                return;
            }
            SFeatureCls sf = (SFeatureCls) ((MapLayer) layerSelectControl_QueryLayerA.getSelectedDocumentItem()).getData();
            //造大区
            GeoPolygon polygon1 = createBigPolygon(sf);
            //设置查询条件
            def.setSpatial(polygon1, mode);
            for (int m = 0, i = 0; m < tableView_QueryLayerB.getItems().size(); m++) {
                if (tableView_QueryLayerB.getItems().get(m).isSelected()) {
                    //进度条
//                    XProgress.SetProgress(wd, "空间查询", this.mapLayers.get(m].Name + ": ", 1, limitNo, false);
//                    if (wd.OperateCanceled) {
//                        XProgress.CloseWaitDialog(ref wd);
//                        break;
//                    }

                    def.setFilter(tableView_QueryLayerB.getItems().get(m).getSql());//设置属性查询条件
                    if (mapLayers.get(m).getGeometryType() == GeomType.GeomAnn) {
                        AnnotationCls ann = (AnnotationCls) mapLayers.get(m).getData();
                        AnnotationCls dann = new AnnotationCls();
                        if (this.desURLs.get(i).toLowerCase().startsWith("gdbp://")) {
                            if (dann.create(this.desURLs.get(i)) <= 0) {
                                continue;
                            }
                        } else {
                            SFeatureCls sfcls = new SFeatureCls();
                            if (sfcls.create("file:///" + this.desURLs.get(i), GeomType.GeomPnt) <= 0) {
                                continue;
                            }
                            sfcls.close();
                            File file = new File(this.desURLs.get(i));
                            if (file.exists()) {
                                if (dann.openByURL("file:///" + this.desURLs.get(i) + "@ann") == 0) {
                                    continue;
                                }
                            }
                        }
                        QueryToCls queryToCls = new QueryToCls(ann);
                        queryToCls.setOption(QueryToCls.OptionType.HasInfo, 1);
                        rtn = queryToCls.toCls(def, dann) > 0;
//                        rtn = ann.SelectToCls(def, dann, selectoclsOption);
                        if (rtn) {
                            dann.setsrID(ann.getsrID());
                            dann.setScaleXY(ann.getScaleX(), ann.getScaleY());
                            String name = dann.getName();
                            long count = dann.getObjCount();
                            dann.close();
                            if (this.desURLs.get(i).toLowerCase().startsWith("gdbp://")) {
                                VectorLayer ml = new VectorLayer(VectorLayerType.AnnLayer);
                                ml.setURL(this.desURLs.get(i));
                                ml.setName(this.desURLs.get(i).substring(this.desURLs.get(i).lastIndexOf('/') + 1));
                                if (ml.connectData()) {
                                    ml.setIsSymbolic(true);
                                    ml.setIsFollowZoom(false);
                                    this.mapControl.getMap().append(ml);
                                }
                            } else {
                                String uurl = "file:///" + this.desURLs.get(i);
                                FileLayer6x ml = new FileLayer6x();
                                ml.setURL(uurl);
                                ml.setName(uurl.substring(uurl.lastIndexOf(File.pathSeparator) + 1));
                                if (ml.connectData()) {
                                    this.mapControl.getMap().append(ml);
                                }
                            }
                            selectresult.append("结果名:").append(name).append(" 要素个数:").append(count).append("\r\n");
                        } else {
                            if (this.desURLs.get(i).toLowerCase().startsWith("gdbp://")) {
                                int annID = dann.getClsID();
                                String gdbUrl = dann.getGDataBase().getURL();
                                dann.close();
                                DataBase delDB = DataBase.openByURL(gdbUrl);
                                if (delDB != null) {
                                    AnnotationCls.remove(delDB, annID);
                                    delDB.close();
                                }
                            } else {
                                dann.close();
                                String url = this.desURLs.get(i);
                                File file = new File(url);
                                if (file.exists()) {
                                    file.delete();
                                }
                                file = new File(url + "~");
                                if (file.exists()) {
                                    file.delete();
                                }
                            }
                        }
                    } else {
                        SFeatureCls sf2 = (SFeatureCls) this.mapLayers.get(m).getData();
                        GeomType tpye = sf2.getGeomType();
                        SFeatureCls dsfcls = new SFeatureCls();
                        if (this.desURLs.get(i).toLowerCase().startsWith("gdbp://")) {
                            if (dsfcls.create(this.desURLs.get(i), tpye) <= 0) {
                                continue;
                            }
                        } else {
                            if (dsfcls.create("file:///" + this.desURLs.get(i), tpye) <= 0) {
                                continue;
                            }
                        }
                        QueryToCls queryToCls = new QueryToCls(sf2);
                        queryToCls.setOption(QueryToCls.OptionType.HasInfo, 1);
                        rtn = queryToCls.toCls(def, dsfcls) > 0;
//                        rtn = sf2.SelectToCls(def, dsfcls, selectoclsOption);
                        if (rtn) {
                            dsfcls.setsrID(sf2.getsrID());
                            dsfcls.setScaleXY(sf2.getScaleX(), sf2.getScaleY());
                            String name = dsfcls.getName();
                            long count = dsfcls.getObjCount();
                            dsfcls.close();
                            if (this.desURLs.get(i).toLowerCase().startsWith("gdbp://")) {
                                VectorLayer ml = new VectorLayer(VectorLayerType.SFclsLayer);
                                ml.setURL(this.desURLs.get(i));
                                ml.setName(this.desURLs.get(i).substring(this.desURLs.get(i).lastIndexOf('/') + 1));
                                if (ml.connectData()) {
                                    ml.setIsSymbolic(true);
                                    ml.setIsFollowZoom(false);
                                    this.mapControl.getMap().append(ml);
                                }
                            } else {
                                String uurl = "file:///" + this.desURLs.get(i);
                                FileLayer6x ml = new FileLayer6x();
                                ml.setURL(uurl);
                                ml.setName(uurl.substring(uurl.lastIndexOf(File.pathSeparator + 1)));
                                if (ml.connectData()) {
                                    this.mapControl.getMap().append(ml);
                                }
                            }
                            selectresult.append("结果名:").append(name).append("要素个数:").append(count).append("\r\n");
                        } else {
                            if (this.desURLs.get(i).toLowerCase().startsWith("gdbp://")) {
                                int clsID = dsfcls.getClsID();
                                String gdbUrl = dsfcls.getGDataBase().getURL();
                                dsfcls.close();
                                DataBase delDB = DataBase.openByURL(gdbUrl);
                                if (delDB != null) {
                                    SFeatureCls.remove(delDB, clsID);
                                    delDB.close();
                                }
                            } else {
                                dsfcls.close();
                                String url = this.desURLs.get(i);
                                File file = new File(url);
                                if (file.exists()) {
                                    file.delete();
                                }
                                file = new File(url + "~");
                                if (file.exists()) {
                                    file.delete();
                                }
                            }
                        }
                    }
                    i++;
                }
            }
            // endregion
        } else if (radioButton_Distance.isSelected()) {
            // region 选择集查询
            // 此处修改为不再使用矢量类的缓冲分析，使用几何的缓冲分析方法，因为矢量类的缓冲分析当缓冲半径给太小时会很慢且失败，当然矢量类的缓冲分析算法p2正在修正与优化。
            Geometry geom = null;
            Record rcd = null;
            GeomInfo info = null;

            SFeatureCls sf1 = (SFeatureCls) new MapLayer(this.selectSetItems.get(0).getLayer().getURL()).getData();
            GeomType type = sf1.getGeomType();
            DataBase daba = DataBase.openTempDB();
            SFeatureCls Tempsf = new SFeatureCls(daba);
            int h = Tempsf.create(UUID.randomUUID().toString(), /*type*/GeomType.GeomReg, 0, 0, null);//创建临时简单要素类
            if (h <= 0) {
                daba.close();
                daba.dispose();
                MessageBox.information("临时简单要素类创建失败!");
                return;
            }
            //将选择结果要素放到临时简单要素类中
//            for (int g = 0; g < this.selectSetItems.get(0).IDList.getObjCount(); g++) {
//                sf1.get(SelectList[0].IDList[g], geom, rcd, info);
//                GeometryExp exp = (GeometryExp) geom;
//                if (exp != null) {
//                    double distance = numberTextField_Distance.getNumber().doubleValue();
//                    geom = exp.buffer(distance, distance);
//                    if (geom != null) {
//                        Tempsf.append(geom, null, null);
//                    }
//                }
//            }
            //造大区
            GeoPolygon polyg = createBigPolygon(Tempsf);
            Tempsf.close();
            daba.close();
            daba.dispose();
            //设置查询条件
            def.setSpatial(polyg, mode);
            for (int b = 0, k = 0; b < tableView_QueryLayerB.getItems().size(); b++) {
                if (tableView_QueryLayerB.getItems().get(b).isSelected()) {
                    //进度条
//                    XProgress.SetProgress(wd, MapGIS.MapEditor.Plugin.Properties.Resources.String_SpatialQuery, this.mapLayers.get(b].Name + MapGIS.MapEditor.Plugin.Properties.Resources.String_Colon, 1, limitNo, false);
//                    if (wd.OperateCanceled) {
//                        XProgress.CloseWaitDialog(ref wd);
//                        break;
//                    }

                    def.setFilter(tableView_QueryLayerB.getItems().get(b).getSql());//设置属性查询条件
                    if (this.mapLayers.get(b).getGeometryType() == GeomType.GeomAnn) {
                        AnnotationCls ann = (AnnotationCls) this.mapLayers.get(b).getData();
                        AnnotationCls dann = new AnnotationCls();
                        if (this.desURLs.get(k).toLowerCase().startsWith("gdbp://")) {
                            if (dann.create(this.desURLs.get(k)) <= 0) {
                                continue;
                            }
                        } else {
                            SFeatureCls sfcls = new SFeatureCls();
                            if (sfcls.create("file:///" + this.desURLs.get(k), GeomType.GeomPnt) <= 0) {
                                continue;
                            }
                            sfcls.close();
                            File file = new File(this.desURLs.get(k));
                            if (file.exists()) {
                                if (dann.openByURL("file:///" + this.desURLs.get(k) + "@ann") == 0) {
                                    continue;
                                }
                            }
                        }
                        QueryToCls queryToCls = new QueryToCls(ann);
                        queryToCls.setOption(QueryToCls.OptionType.HasInfo, 1);
                        rtn = queryToCls.toCls(def, dann) > 0;
//                        rtn = ann.SelectToCls(def, dann, selectoclsOption);
                        if (rtn) {
                            dann.setsrID(ann.getsrID());
                            dann.setScaleXY(ann.getScaleX(), ann.getScaleY());
                            String name = dann.getName();
                            long count = dann.getObjCount();
                            dann.close();
                            if (this.desURLs.get(k).toLowerCase().startsWith("gdbp://")) {
                                VectorLayer ml = new VectorLayer(VectorLayerType.AnnLayer);
                                ml.setURL(this.desURLs.get(k));
                                ml.setName(this.desURLs.get(k).substring(this.desURLs.get(k).lastIndexOf('/') + 1));
                                if (ml.connectData()) {
                                    ml.setIsSymbolic(true);
                                    ml.setIsFollowZoom(false);
                                    this.mapControl.getMap().append(ml);
                                }
                            } else {
                                String uurl = "file:///" + this.desURLs.get(k);
                                FileLayer6x ml = new FileLayer6x();
                                ml.setURL(uurl);
                                ml.setName(uurl.substring(uurl.lastIndexOf(File.pathSeparator + 1)));
                                if (ml.connectData()) {
                                    this.mapControl.getMap().append(ml);
                                }
                            }
                            selectresult.append("结果名:").append(name).append("要素个数:").append(count).append("\r\n");
                        } else {
                            if (this.desURLs.get(k).toLowerCase().startsWith("gdbp://")) {
                                int annID = dann.getClsID();
                                String gdbUrl = dann.getGDataBase().getURL();
                                dann.close();
                                DataBase delDB = DataBase.openByURL(gdbUrl);
                                if (delDB != null) {
                                    AnnotationCls.remove(delDB, annID);
                                    delDB.close();
                                }
                            } else {
                                dann.close();
                                String url = this.desURLs.get(k);
                                File file = new File(url);
                                if (file.exists()) {
                                    file.delete();
                                }
                                file = new File(url + "~");
                                if (file.exists()) {
                                    file.delete();
                                }
                            }
                        }
                    } else {
                        SFeatureCls sf2 = (SFeatureCls) this.mapLayers.get(b).getData();
                        GeomType tpye = sf2.getGeomType();
                        SFeatureCls dsfcls = new SFeatureCls();
                        if (this.desURLs.get(k).toLowerCase().startsWith("gdbp://")) {
                            if (dsfcls.create(this.desURLs.get(k), tpye) <= 0) {
                                continue;
                            }
                        } else {
                            if (dsfcls.create("file:///" + this.desURLs.get(k), tpye) <= 0) {
                                continue;
                            }
                        }
                        QueryToCls queryToCls = new QueryToCls(sf2);
                        queryToCls.setOption(QueryToCls.OptionType.HasInfo, 1);
                        rtn = queryToCls.toCls(def, dsfcls) > 0;
//                        rtn = sf2.SelectToCls(def, dsfcls, selectoclsOption);
                        if (rtn) {
                            dsfcls.setsrID(sf2.getsrID());
                            dsfcls.setScaleXY(sf2.getScaleX(), sf2.getScaleY());
                            String name = dsfcls.getName();
                            long count = dsfcls.getObjCount();
                            dsfcls.close();
                            if (this.desURLs.get(k).toLowerCase().startsWith("gdbp://")) {
                                VectorLayer ml = new VectorLayer(VectorLayerType.SFclsLayer);
                                ml.setURL(this.desURLs.get(k));
                                ml.setName(this.desURLs.get(k).substring(this.desURLs.get(k).lastIndexOf('/') + 1));
                                if (ml.connectData()) {
                                    ml.setIsSymbolic(true);
                                    ml.setIsFollowZoom(false);
                                    this.mapControl.getMap().append(ml);
                                }
                            } else {
                                String uurl = "file:///" + this.desURLs.get(k);
                                FileLayer6x ml = new FileLayer6x();
                                ml.setURL(uurl);
                                ml.setName(uurl.substring(uurl.lastIndexOf(File.pathSeparator + 1)));
                                if (ml.connectData()) {
                                    this.mapControl.getMap().append(ml);
                                }
                            }
                            selectresult.append("结果名:").append(name).append("要素个数:").append(count).append("\r\n");
                        } else {
                            if (this.desURLs.get(k).toLowerCase().startsWith("gdbp://")) {
                                int clsID = dsfcls.getClsID();
                                String gdbUrl = dsfcls.getGDataBase().getURL();
                                dsfcls.close();
                                DataBase delDB = DataBase.openByURL(gdbUrl);
                                if (delDB != null) {
                                    SFeatureCls.remove(delDB, clsID);
                                    delDB.close();
                                }
                            } else {
                                dsfcls.close();
                                String url = this.desURLs.get(k);
                                File file = new File(url);
                                if (file.exists()) {
                                    file.delete();
                                }
                                file = new File(url + "~");
                                if (file.exists()) {
                                    file.delete();
                                }
                            }
                        }
                    }
                    k++;
                }
            }
            // endregion
        } else {
            // region 属性查询
            for (int d = 0, t = 0; d < tableView_QueryLayerB.getItems().size(); d++) {
                if (tableView_QueryLayerB.getItems().get(d).isSelected()) {
                    //进度条
//                    XProgress.SetProgress(wd, MapGIS.MapEditor.Plugin.Properties.Resources.String_SpatialQuery, this.mapLayers.get(d].Name + MapGIS.MapEditor.Plugin.Properties.Resources.String_Colon, 1, limitNo, false);
//                    if (wd.OperateCanceled) {
//                        XProgress.CloseWaitDialog(ref wd);
//                        break;
//                    }
                    //设置查询条件
                    def.setFilter(tableView_QueryLayerB.getItems().get(d).getSql());
                    if (this.mapLayers.get(d).getGeometryType() == GeomType.GeomAnn) {
                        AnnotationCls ann = (AnnotationCls) this.mapLayers.get(d).getData();
                        AnnotationCls dann = new AnnotationCls();
                        if (this.desURLs.get(t).toLowerCase().startsWith("gdbp://")) {
                            if (dann.create(this.desURLs.get(t)) <= 0) {
                                continue;
                            }
                        } else {
                            SFeatureCls sfcls = new SFeatureCls();
                            if (sfcls.create("file:///" + this.desURLs.get(t), GeomType.GeomPnt) <= 0) {
                                continue;
                            }
                            sfcls.close();
                            File file = new File(this.desURLs.get(t));
                            if (file.exists()) {
                                if (dann.openByURL("file:///" + this.desURLs.get(t) + "@ann") == 0) {
                                    continue;
                                }
                            }
                        }
                        QueryToCls queryToCls = new QueryToCls(ann);
                        queryToCls.setOption(QueryToCls.OptionType.HasInfo, 1);
                        rtn = queryToCls.toCls(def, dann) > 0;
//                        rtn = ann.SelectToCls(def, dann, selectoclsOption);
                        if (rtn) {
                            dann.setsrID(ann.getsrID());
                            dann.setScaleXY(ann.getScaleX(), ann.getScaleY());
                            String name = dann.getName();
                            long count = dann.getObjCount();
                            dann.close();
                            if (this.desURLs.get(t).toLowerCase().startsWith("gdbp://")) {
                                VectorLayer ml = new VectorLayer(VectorLayerType.AnnLayer);
                                ml.setURL(this.desURLs.get(t));
                                ml.setName(this.desURLs.get(t).substring(this.desURLs.get(t).lastIndexOf('/') + 1));
                                if (ml.connectData()) {
                                    ml.setIsSymbolic(true);
                                    ml.setIsFollowZoom(false);
                                    this.mapControl.getMap().append(ml);
                                }
                            } else {
                                String uurl = "file:///" + this.desURLs.get(t);
                                FileLayer6x ml = new FileLayer6x();
                                ml.setURL(uurl);
                                ml.setName(uurl.substring(uurl.lastIndexOf(File.pathSeparator + 1)));
                                if (ml.connectData()) {
                                    this.mapControl.getMap().append(ml);
                                }
                            }
                            selectresult.append("结果名:").append(name).append("要素个数:").append(count).append("\r\n");
                        } else {
                            if (this.desURLs.get(t).toLowerCase().startsWith("gdbp://")) {
                                int annID = dann.getClsID();
                                String gdbUrl = dann.getGDataBase().getURL();
                                dann.close();
                                DataBase delDB = DataBase.openByURL(gdbUrl);
                                if (delDB != null) {
                                    AnnotationCls.remove(delDB, annID);
                                    delDB.close();
                                }
                            } else {
                                dann.close();
                                String url = this.desURLs.get(t);
                                File file = new File(url);
                                if (file.exists()) {
                                    file.delete();
                                }
                                file = new File(url + "~");
                                if (file.exists()) {
                                    file.delete();
                                }
                            }
                        }
                    } else {
                        SFeatureCls sf2 = (SFeatureCls) this.mapLayers.get(d).getData();
                        GeomType tpye = sf2.getGeomType();
                        SFeatureCls dsfcls = new SFeatureCls();
                        if (this.desURLs.get(t).toLowerCase().startsWith("gdbp://")) {
                            if (dsfcls.create(this.desURLs.get(t), tpye) <= 0) {
                                continue;
                            }
                        } else {
                            if (dsfcls.create("file:///" + this.desURLs.get(t), tpye) <= 0) {
                                continue;
                            }
                        }
                        QueryToCls queryToCls = new QueryToCls(sf2);
                        queryToCls.setOption(QueryToCls.OptionType.HasInfo, 1);
                        rtn = queryToCls.toCls(def, dsfcls) > 0;
//                        rtn = sf2.SelectToCls(def, dsfcls, selectoclsOption);
                        if (rtn) {
                            dsfcls.setsrID(sf2.getsrID());
                            dsfcls.setScaleXY(sf2.getScaleX(), sf2.getScaleY());
                            String name = dsfcls.getName();
                            long count = dsfcls.getObjCount();
                            dsfcls.close();
                            if (this.desURLs.get(t).toLowerCase().startsWith("gdbp://")) {
                                VectorLayer ml = new VectorLayer(VectorLayerType.SFclsLayer);
                                ml.setURL(this.desURLs.get(t));
                                ml.setName(this.desURLs.get(t).substring(this.desURLs.get(t).lastIndexOf('/') + 1));
                                if (ml.connectData()) {
                                    ml.setIsSymbolic(true);
                                    ml.setIsFollowZoom(false);
                                    this.mapControl.getMap().append(ml);
                                }
                            } else {
                                String uurl = "file:///" + this.desURLs.get(t);
                                FileLayer6x ml = new FileLayer6x();
                                ml.setURL(uurl);
                                ml.setName(uurl.substring(uurl.lastIndexOf(File.pathSeparator + 1)));
                                if (ml.connectData()) {
                                    this.mapControl.getMap().append(ml);
                                }
                            }
                            selectresult.append("结果名:").append(name).append("要素个数:").append(count).append("\r\n");
                        } else {
                            if (this.desURLs.get(t).toLowerCase().startsWith("gdbp://")) {
                                int clsID = dsfcls.getClsID();
                                String gdbUrl = dsfcls.getGDataBase().getURL();
                                dsfcls.close();
                                DataBase delDB = DataBase.openByURL(gdbUrl);
                                if (delDB != null) {
                                    SFeatureCls.remove(delDB, clsID);
                                    delDB.close();
                                }
                            } else {
                                dsfcls.close();
                                String url = this.desURLs.get(t);
                                File file = new File(url);
                                if (file.exists()) {
                                    file.delete();
                                }
                                file = new File(url + "~");
                                if (file.exists()) {
                                    file.delete();
                                }
                            }
                        }
                    }
                    t++;
                }
            }
            // endregion
        }
        def.dispose();
//        TimeSpan tsEnd = new TimeSpan(DateTime.Now.Ticks);
//        TimeSpan tsEx = tsEnd.Subtract(tsBegin).Duration();
//        String timeSpend = InitPlugin.FormatTime(tsEx);
//        InitPlugin.SetKeyValue(Resources.String_SpatialQuery, "[" + MapGIS.MapEditor.Plugin.Properties.Resources.String_SpatialQuery + "]:" + DateTime.Now.ToString() + ":" + MapGIS.MapEditor.Plugin.Properties.Resources.String_FinishSpatialQuerySumTime + timeSpend + "\r\n");
//        //关闭进度条
//        if (!wd.OperateCanceled)
//            XProgress.CloseWaitDialog(ref wd);
        if (!selectresult.toString().isEmpty()) {
            MessageBox.information(selectresult.toString(), Window.primaryStage, "查询结果");
        }

//        StressMapItem.EndStress(this.mapControl);
//        this.DialogResult = DialogResult.OK;
    }

    private GeoPolygon createBigPolygon(SFeatureCls sfcls) {
        if (sfcls == null) {
            return null;
        }
        GeoPolygon geop = null;
        GeoMultiPolygon geops = null;
        GeoPolygon geopolyg = new GeoPolygon();
        ArrayList<Dots3D> listdots = new ArrayList<>();

        QueryDef queryDef = new QueryDef();
        queryDef.setCursorType(QueryDef.SetCursorType.ForwardOnly);
        RecordSet set = sfcls.query(queryDef);
        if (set != null) {
            set.moveFirst();
            while (!set.isEOF()) {
                GeometryType type = set.getGeometry().getType();
                if (GeometryType.GeoPolygon.equals(type)) {
                    geop = (GeoPolygon) set.getGeometry();
                    if (geop.getDots3DArray() != null) {
                        listdots.addAll(Arrays.asList(geop.getDots3DArray()));
                    }
                } else if (GeometryType.GeoMultiPolygon.equals(type)) {
                    geops = (GeoMultiPolygon) set.getGeometry();
                    if (geops != null) {
                        for (int j = 0; j < geops.getNum(); j++) {
                            listdots.addAll(Arrays.asList(geops.getPolygon(j).getDots3DArray()));
                        }
                    }
                }
                set.moveNext();
            }
            Dots3D[] dots3darray = new Dots3D[listdots.size()];
            for (int n = 0; n < listdots.size(); n++) {
                dots3darray[n] = listdots.get(n);
            }
            geopolyg.setDots3DArray(dots3darray);
            set.detach();
            return geopolyg;
        } else {
            return null;
        }
    }

    private void addMapLayerFromGroupLayer(GroupLayer groupLayer) {
        for (int i = 0; i < groupLayer.getCount(); i++) {
            MapLayer mapLayer = groupLayer.item(i);
            if (mapLayer instanceof VectorLayer && mapLayer.getGeometryType() != GeomType.GeomUnknown) {
                this.mapLayers.add(mapLayer);
            } else if (mapLayer instanceof GroupLayer) {
                addMapLayerFromGroupLayer((GroupLayer) mapLayer);
            }
        }
    }
}

