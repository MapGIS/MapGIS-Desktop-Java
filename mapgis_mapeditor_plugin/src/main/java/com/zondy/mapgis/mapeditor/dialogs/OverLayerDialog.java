package com.zondy.mapgis.mapeditor.dialogs;

import com.zondy.mapgis.analysis.spatialanalysis.*;
import com.zondy.mapgis.base.MessageBox;
import com.zondy.mapgis.base.Window;
import com.zondy.mapgis.common.MapGISErrorDialog;
import com.zondy.mapgis.controls.*;
import com.zondy.mapgis.controls.common.ButtonEdit;
import com.zondy.mapgis.controls.common.ButtonEditEvent;
import com.zondy.mapgis.controls.common.NumberTextField;
import com.zondy.mapgis.filedialog.GDBSaveFileDialog;
import com.zondy.mapgis.geodatabase.DataBase;
import com.zondy.mapgis.geodatabase.SFeatureCls;
import com.zondy.mapgis.geometry.GeomType;
import com.zondy.mapgis.map.*;
import com.zondy.mapgis.mapeditor.common.CustomClass;
import com.zondy.mapgis.srs.SRefData;
import com.zondy.mapgis.workspace.plugin.InitPlugin;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.File;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Optional;

public class OverLayerDialog extends Dialog {
    private LayerSelectControl layerSelectControl_Layer1;
    private LayerSelectControl layerSelectControl_Layer2;
    private NumberTextField numberTextField_ToleranceRadius;
    private ComboBox<String> comboBox_OverlayType;
    private CheckBox checkBox_AttributeOperate;
    private CheckBox checkBox_MultiFeatureOperate;
    private ComboBox<String> comboBox_GraphicParameter;
    private ButtonEdit buttonEdit_SavePath;
    private CheckBox checkbox_AddInMap;
    private Button button_OK;

    private MapControl mapControl;
    private GeomType zdGeomType = GeomType.GeomUnknown;

    public OverLayerDialog(Document document, MapControl mapControl) {
        setTitle("叠加分析");

        this.mapControl = mapControl;

        // region Input Settings

        layerSelectControl_Layer1 = new LayerSelectControl(document, "简单要素类，6x数据|sfcls;*.wp;*.wl;*.wt");
        layerSelectControl_Layer1.setOnSelectedItemChanged(layerSelectControl_Layer1_SelectedItemChanged_ChangeListener);
        layerSelectControl_Layer2 = new LayerSelectControl(document, "简单要素类，6x数据|sfcls;*.wp;*.wl;*.wt");
        layerSelectControl_Layer2.setOnSelectedItemChanged(layerSelectControl_Layer2_SelectedItemChanged_ChangeListener);

        HBox hBox_Layer1 = new HBox(new Label("图层1:"), layerSelectControl_Layer1);
        HBox.setHgrow(layerSelectControl_Layer1, Priority.ALWAYS);
        HBox hBox_Layer2 = new HBox(new Label("图层2:"), layerSelectControl_Layer2);
        HBox.setHgrow(layerSelectControl_Layer2, Priority.ALWAYS);
        VBox vBox_InputSettings = new VBox(5, hBox_Layer1, hBox_Layer2);

        // endregion

        // region Parameters Settings

        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(8);
        numberTextField_ToleranceRadius = new NumberTextField(new BigDecimal("0.00001"), numberFormat);
        numberTextField_ToleranceRadius.setOnKeyTyped(event -> {
            if (event.getCode() == KeyCode.MINUS) {
                event.consume();
            }
        });
        comboBox_OverlayType = new ComboBox<>(FXCollections.observableArrayList(
                "求交", "相减", "求并", "对称差", "判别", "区区更新"));

        HBox hBox_ToleranceRadius = new HBox(new Label("容差半径:"), numberTextField_ToleranceRadius);
        HBox.setHgrow(numberTextField_ToleranceRadius, Priority.ALWAYS);
        HBox hBox_OverlayType = new HBox(new Label("叠加方式:"), comboBox_OverlayType);
        HBox.setHgrow(comboBox_OverlayType, Priority.ALWAYS);
        VBox vBox_ParametersSettings = new VBox(5, hBox_ToleranceRadius, hBox_OverlayType);

        // endregion

        // region Advanced Options

        checkBox_AttributeOperate = new CheckBox("进行属性操作");
        checkBox_MultiFeatureOperate = new CheckBox("处理符合要素(多点/多线/多区)");
        comboBox_GraphicParameter = new ComboBox<>(FXCollections.observableArrayList(
                "使用随机图形参数", "使用图层2(叠加对象)的图形参数", "使用图层1(被叠加对象)的图形参数"));

        HBox hBox_Operate = new HBox(5, checkBox_AttributeOperate, checkBox_MultiFeatureOperate);
        HBox hBox_GraphicParameter = new HBox(new Label("图形参数:"), comboBox_GraphicParameter);
        HBox.setHgrow(comboBox_GraphicParameter, Priority.ALWAYS);
        VBox vBox_AdvancedOptions = new VBox(5, hBox_Operate, hBox_GraphicParameter);

        // endregion

        // region Output Settings

        buttonEdit_SavePath = new ButtonEdit();
        buttonEdit_SavePath.setTextEditable(true);
        buttonEdit_SavePath.setOnButtonClick(buttonEdit_SavePath_ButtonClick_EventHandler);
        checkbox_AddInMap = new CheckBox("结果添加到地图文档");

        HBox hBox_SavePath = new HBox(new Label("输出结果:"), buttonEdit_SavePath);
        HBox.setHgrow(buttonEdit_SavePath, Priority.ALWAYS);
        VBox vBox_OutputSettings = new VBox(5, hBox_SavePath, checkbox_AddInMap);

        // endregion

        // region TitlePane(Group)

        TitledPane titledPane_InputSettings = new TitledPane("输入设置", vBox_InputSettings);
        titledPane_InputSettings.setCollapsible(false);
        TitledPane titledPane_ParametersSettings = new TitledPane("参数设置", vBox_ParametersSettings);
        titledPane_ParametersSettings.setCollapsible(false);
        TitledPane titledPane_AdvancedOption = new TitledPane("高级选项", vBox_AdvancedOptions);
        titledPane_AdvancedOption.setCollapsible(false);
        TitledPane titledPane_OutputSettings = new TitledPane("输出设置", vBox_OutputSettings);
        titledPane_OutputSettings.setCollapsible(false);

        // endregion

        // region Layout


//        layerSelectControl_Layer1.prefWidthProperty().bind(numberTextField_ToleranceRadius.widthProperty());
//        layerSelectControl_Layer2.prefWidthProperty().bind(numberTextField_ToleranceRadius.widthProperty());
//        comboBox_OverlayType.prefWidthProperty().bind(numberTextField_ToleranceRadius.widthProperty());
//        comboBox_GraphicParameter.prefWidthProperty().bind(numberTextField_ToleranceRadius.widthProperty());
//        buttonEdit_SavePath.prefWidthProperty().bind(numberTextField_ToleranceRadius.widthProperty());

        VBox vBox = new VBox(5, titledPane_InputSettings, titledPane_ParametersSettings, titledPane_AdvancedOption, titledPane_OutputSettings);

        // endregion

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setContent(vBox);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.setPrefWidth(400);
        dialogPane.setPrefHeight(480);
        button_OK = (Button) dialogPane.lookupButton(ButtonType.OK);
        button_OK.addEventFilter(ActionEvent.ACTION, this::button_OK_Click);

        if (this.mapControl == null) {
            checkbox_AddInMap.setDisable(true);
            checkbox_AddInMap.setSelected(false);
        }

        layerSelectControl_Layer1.selectFirstItem();
        layerSelectControl_Layer2.selectFirstItem();
        comboBox_OverlayType.getSelectionModel().select(0);
        checkBox_AttributeOperate.setSelected(true);
        comboBox_GraphicParameter.getSelectionModel().select(2);
    }

    // event

    private ChangeListener<LayerSelectComboBoxItem> layerSelectControl_Layer1_SelectedItemChanged_ChangeListener = (observable, oldValue, newValue) -> {
        MapLayer mapLayer = (MapLayer) layerSelectControl_Layer1.getSelectedDocumentItem();
        String zdsfclsUrl = layerSelectControl_Layer1.getSelectedItemUrl();
        String bdsfclsUrl = layerSelectControl_Layer2.getSelectedItemUrl();
        if (zdsfclsUrl != null && zdsfclsUrl != "" && bdsfclsUrl != null && bdsfclsUrl != "") {
            SFeatureCls zdsf = new SFeatureCls();
            SFeatureCls bdsf = new SFeatureCls();
            zdsfclsUrl = zdsfclsUrl.toLowerCase().startsWith("gdbp://") || zdsfclsUrl.toLowerCase().startsWith("file:///") ? zdsfclsUrl : "file:///" + zdsfclsUrl;
            bdsfclsUrl = bdsfclsUrl.toLowerCase().startsWith("gdbp://") || bdsfclsUrl.toLowerCase().startsWith("file:///") ? bdsfclsUrl : "file:///" + bdsfclsUrl;
            if (zdsf.openByURL(zdsfclsUrl) > 0) {
                this.zdGeomType = zdsf.getGeomType();
                if (bdsf.openByURL(bdsfclsUrl) > 0) {
                    initRegOptStyle(zdsf, bdsf);
                    setTolerance(zdsf, bdsf);
                    bdsf.close();
                }
                zdsf.close();
            }
            if (zdsfclsUrl.toLowerCase().startsWith("gdbp://") && bdsfclsUrl.toLowerCase().startsWith("gdbp://")) {
                checkBox_MultiFeatureOperate.setDisable(false);
            } else {
                checkBox_MultiFeatureOperate.setDisable(true);
            }
        }
        buttonEdit_SavePath.setText("");
//            url = "";
    };

    private ChangeListener<LayerSelectComboBoxItem> layerSelectControl_Layer2_SelectedItemChanged_ChangeListener = (observable, oldValue, newValue) -> {
        String zdsfclsUrl = layerSelectControl_Layer1.getSelectedItemUrl();
        String bdsfclsUrl = layerSelectControl_Layer2.getSelectedItemUrl();
        if (zdsfclsUrl != null && zdsfclsUrl != "" && bdsfclsUrl != null && bdsfclsUrl != "") {
            SFeatureCls zdsf = new SFeatureCls();
            SFeatureCls bdsf = new SFeatureCls();
            zdsfclsUrl = zdsfclsUrl.toLowerCase().startsWith("gdbp://") || zdsfclsUrl.toLowerCase().startsWith("file:///") ? zdsfclsUrl : "file:///" + zdsfclsUrl;
            bdsfclsUrl = bdsfclsUrl.toLowerCase().startsWith("gdbp://") || bdsfclsUrl.toLowerCase().startsWith("file:///") ? bdsfclsUrl : "file:///" + bdsfclsUrl;
            if (zdsf.openByURL(zdsfclsUrl) > 0) {
                this.zdGeomType = zdsf.getGeomType();
                if (bdsf.openByURL(bdsfclsUrl) > 0) {
                    initRegOptStyle(zdsf, bdsf);
                    setTolerance(zdsf, bdsf);
                    bdsf.close();
                }
                zdsf.close();
            }
            if (zdsfclsUrl.toLowerCase().startsWith("gdbp://") && bdsfclsUrl.toLowerCase().startsWith("gdbp://")) {
                checkBox_MultiFeatureOperate.setDisable(false);
            } else {
                checkBox_MultiFeatureOperate.setDisable(true);
            }
        }
    };

    private EventHandler<ButtonEditEvent> buttonEdit_SavePath_ButtonClick_EventHandler = event -> {
        GDBSaveFileDialog savefile = new GDBSaveFileDialog();
//            savefile.CanOverwrite = false;
        if (zdGeomType == GeomType.GeomPnt) {
            savefile.setFilter("简单要素类、6x点文件|sfcls;*.wt");
        } else if (zdGeomType == GeomType.GeomLin) {
            savefile.setFilter("简单要素类、6x线文件|sfcls;*.wl");
        } else if (zdGeomType == GeomType.GeomReg) {
            savefile.setFilter("简单要素类、6x区文件|sfcls;*.wp");
        }
        Optional<String[]> optional = savefile.showAndWait();
        if (optional != null && optional.isPresent()) {
            String[] files = optional.get();
            if (files.length > 0) {
                buttonEdit_SavePath.setText(files[0]);
            }
        }
    };


    private void button_OK_Click(ActionEvent event) {
        if (!checkFormInput()) {
            return;
        }
//            this.hasCompleted = false;
//            this.waitForm = new WaitForm(false, false);
//            this.waitForm.CanCancel += new WaitForm.CanCancelHandler(waitForm_CanCancel);
//            bool first = true;
//            this.waitForm.Load += (ws, es) =>
//            {
//                if (first)
//                {
//                    first = false;
//                    Thread threadCheck = new Thread(new ParameterizedThreadStart(StartOverlay));
//                    threadCheck.SetApartmentState(ApartmentState.STA);
//                    threadCheck.CurrentUICulture = System.Threading.Thread.CurrentThread.CurrentUICulture;
//                    threadCheck.Start();
//                }
//            };
//            this.waitForm.ShowDialog(new Win32Window(XHelp.GetMainWindowHandle()));

        startOverlay();
    }


    private boolean checkFormInput() {
        String zdsfclsUrl = layerSelectControl_Layer1.getSelectedItemUrl();
        String bdsfclsUrl = layerSelectControl_Layer2.getSelectedItemUrl();
        String desturl = buttonEdit_SavePath.getText();
        if (zdsfclsUrl.isEmpty()) {
            MessageBox.information("输入图层1为空,请选择!");
            return false;
        }
        if (bdsfclsUrl.isEmpty()) {
            MessageBox.information("输入图层1为空,请选择!");
            return false;
        }
        double tol = numberTextField_ToleranceRadius.getNumber().doubleValue();
        if (tol < 0) {
            MessageBox.information("容差必须为正数!");
            return false;
        }
        if (desturl.isEmpty()) {
            MessageBox.information("请输入保存路径!");
            return false;
        } else {
            if (!desturl.toLowerCase().startsWith("gdbp://")) {
                if (zdsfclsUrl.toLowerCase().startsWith("gdbp://") || bdsfclsUrl.toLowerCase().startsWith("gdbp://")) {
                    MessageBox.information("因叠加的数据源类型与保存的数据源类型不一致,不能操作。请重新选择保存结果!");
                    return false;
                }
            }
            //被动简单要素类
            SFeatureCls bdsf = new SFeatureCls();
            if (bdsf.openByURL(bdsfclsUrl) == 0) {
                if (bdsf.getObjCount() == 0) {
                    MessageBox.information("输入图层2的数据为空,请检查!");
                    return false;
                }
                bdsf.close();
            }

            if (tol > CustomClass.MAX_TOLERANCE || tol < CustomClass.MIN_TOLERANCE) {
                String warnMsg = String.format("容差不在推荐范围({%f}~{%f})内,可能导致生成结果错误,是否仍然使用该容差？", CustomClass.MIN_TOLERANCE, CustomClass.MAX_TOLERANCE);
                return MessageBox.question(warnMsg, Window.primaryStage, "警告") != ButtonType.CANCEL;
            }
        }
        return true;
    }

    private void startOverlay() {
        String failMsg = "";
//        this.hasCompleted = false;
        String zdsfclsUrl = layerSelectControl_Layer1.getSelectedItemUrl();
        String bdsfclsUrl = layerSelectControl_Layer2.getSelectedItemUrl();
        SFeatureCls zdsf = new SFeatureCls();//主动简单要素类
        SFeatureCls bdsf = new SFeatureCls();//被动简单要素类
        zdsfclsUrl = zdsfclsUrl.toLowerCase().startsWith("gdbp://") || zdsfclsUrl.toLowerCase().startsWith("file:///") ? zdsfclsUrl : "file:///" + zdsfclsUrl;
        bdsfclsUrl = bdsfclsUrl.toLowerCase().startsWith("gdbp://") || bdsfclsUrl.toLowerCase().startsWith("file:///") ? bdsfclsUrl : "file:///" + bdsfclsUrl;
        if (zdsf.openByURL(zdsfclsUrl) == 0) {
            return;
        }
        if (bdsf.openByURL(bdsfclsUrl) == 0) {
            zdsf.close();
            return;
        }
        //创建结果简单要素类
        int a = 0;
        SFeatureCls dsfcls = new SFeatureCls();
        String desPath = buttonEdit_SavePath.getText();
        if (desPath.toLowerCase().startsWith("gdbp://")) {
            a = dsfcls.create(desPath, zdsf.getGeomType());
            if (a <= 0) {
                MessageBox.information("创建简单要素类失败!");
                //return;
                dsfcls = new SFeatureCls();
                if (dsfcls.openByURL("GDBP://MapGisLocal/sample/ds/地图综合/sfcls/desOverLayer") > 0) {
                    System.out.println("des open true");
                }
            }
        } else {
            a = dsfcls.create("file:///" + desPath, zdsf.getGeomType());
            if (a <= 0) {
                MessageBox.information("创建 6x 文件失败!");
                return;
            }
        }
        System.out.println("Start SP_OverLay");
        //叠加参数
        OverlayOption option = new OverlayOption();
        //叠加操作
        String strType = comboBox_OverlayType.getSelectionModel().getSelectedItem();
        switch (strType) {
            case "求并":
                option.setOverlayType(OverlayOption.OverlayType.UNION);
                break;
            case "求交":
                option.setOverlayType(OverlayOption.OverlayType.INTER);
                break;
            case "相减":
                option.setOverlayType(OverlayOption.OverlayType.SUB);
                break;
            case "判别":
                option.setOverlayType(OverlayOption.OverlayType.IDENT);
                break;
            case "对称差":
                option.setOverlayType(OverlayOption.OverlayType.SYMDIFF);
                break;
            case "区区更新":
                option.setOverlayType(OverlayOption.OverlayType.UPDATE);
                break;
            default:
                break;
        }
        //图形参数
        switch (comboBox_GraphicParameter.getSelectionModel().getSelectedIndex()) {
            case 0:
                option.setInfoOptType(OverlayOption.OverlayInfoOptType.RandomInfo);
                break;
            case 1:
                option.setInfoOptType(OverlayOption.OverlayInfoOptType.UserBInfo);
                break;
            case 2:
                option.setInfoOptType(OverlayOption.OverlayInfoOptType.UsesAInfo);
                break;
            default:
                break;
        }
        //属性操作
        if (checkBox_AttributeOperate.isSelected()) {
            option.setAttOptType(OverlayOption.OverlayAttOptType.KeepAtt);
        } else {
            option.setAttOptType(OverlayOption.OverlayAttOptType.IgnoreAtt);
        }
        //处理复合要素
        option.setMultiFeatureOption(checkBox_MultiFeatureOperate.isSelected());
        option.setTolerance(numberTextField_ToleranceRadius.getNumber().doubleValue());
//        TimeSpan tsBegin = new TimeSpan(DateTime.Now.Ticks);
//        InitPlugin.SetKeyValue(MapGIS.MapEditor.Plugin.Properties.Resources.String_OverlayAnalyse, "[" + MapGIS.MapEditor.Plugin.Properties.Resources.String_OverlayAnalyse + "]:" + DateTime.Now.ToString() + ":" + MapGIS.MapEditor.Plugin.Properties.Resources.String_BeginOverLayerAnalyses + "\r\n");
//        option.ProcessCallback = logRev;
//        option.ProcessCallback.StepStart += new StepStartEventHandle(onStepStart);
//        option.ProcessCallback.StepMessage += new StepMessageEventHandle(onStepMessage);
//        option.ProcessCallback.StepEnd += new StepEndEventHandle(onStepEnd);
        boolean rtn = SpatialAnalysis.overLay(zdsf, bdsf, dsfcls, option) > 0;
        System.out.println("SpatialAnalysis.overLay rtn: " + rtn);
//        option.Dispose();
        String uurl = dsfcls.getURL();
//        zdsf.close();
//        bdsf.close();
//        this.waitForm.Close();
//        this.waitForm = null;
        if (rtn) {
//            this.hasCompleted = true;
            dsfcls.setsrID(zdsf.getsrID());
            dsfcls.setScaleXY(zdsf.getScaleX(), zdsf.getScaleY());
            dsfcls.setModelName(zdsf.getModelName());
            dsfcls.close();
            if (checkbox_AddInMap.isSelected()) {
                DocumentItem documentItem = layerSelectControl_Layer1.getSelectedDocumentItem();
                if (uurl.toLowerCase().startsWith("gdbp://")) {
                    VectorLayer ml;
                    if (documentItem instanceof MapLayer) {
                        ml = (VectorLayer) ((MapLayer) documentItem).clone();
                    } else {
                        ml = new VectorLayer(VectorLayerType.SFclsLayer);
                    }
                    ml.setURL(uurl);
                    ml.setName(uurl.substring(uurl.lastIndexOf('/') + 1));
                    if (ml.connectData()) {
                        ml.setIsSymbolic(true);
                        ml.setIsFollowZoom(false);
//                        this.mapControl.Invoke(new MethodInvoker(delegate {
                        this.mapControl.getMap().append(ml);
//                        }));
                    }
                } else if (uurl.toLowerCase().startsWith("file:///")) {
                    FileLayer6x ml;
                    if (documentItem instanceof MapLayer) {
                        ml = (FileLayer6x) ((MapLayer) documentItem).clone();
                    } else {
                        ml = new FileLayer6x();
                    }

                    ml.setURL(uurl);
                    ml.setName(uurl.substring(uurl.lastIndexOf(File.pathSeparator) + 1));
                    if (ml.connectData()) {
//                        this.mapControl.Invoke(new MethodInvoker(delegate {
                        this.mapControl.getMap().append(ml);
//                        }));
                    }
                }
            }
        } else {
            if (uurl.toLowerCase().startsWith("gdbp://")) {
                int clsID = dsfcls.getClsID();
                String gdbUrl = dsfcls.getGDataBase().getURL();
                dsfcls.close();
                DataBase delDB = DataBase.openByURL(gdbUrl);
                if (delDB != null) {
                    SFeatureCls.remove(delDB, clsID);
                    delDB.close();
                }
            } else if (uurl.toLowerCase().startsWith("file:///")) {
                dsfcls.close();
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
            boolean isSucceed = MapGISErrorDialog.ShowLastError();
            if (!isSucceed) {
                MessageBox.information("叠加分析失败!");
            }
//            }));
        }

        zdsf.close();
        bdsf.close();
//        TimeSpan tsEnd = new TimeSpan(DateTime.Now.Ticks);
//        TimeSpan tsEx = tsEnd.Subtract(tsBegin).Duration();
//        string timeSpend = InitPlugin.FormatTime(tsEx);
//        InitPlugin.SetKeyValue(MapGIS.MapEditor.Plugin.Properties.Resources.String_OverlayAnalyse, "[" + MapGIS.MapEditor.Plugin.Properties.Resources.String_OverlayAnalyse + "]:" + DateTime.Now.ToString() + ":" + MapGIS.MapEditor.Plugin.Properties.Resources.String_FinishOverLayerAnalysesSumTime + timeSpend + "\r\n");
//        this.DialogResult = DialogResult.OK;
    }

    /**
     * 初始化叠加类型下拉框
     *
     * @param zdsf
     * @param bdsf
     */
    private void initRegOptStyle(SFeatureCls zdsf, SFeatureCls bdsf) {
        comboBox_OverlayType.getItems().clear();
//        comboBox_OverlayType.Text = "";
        // 点对线
        if (zdsf.getGeomType() == GeomType.GeomPnt && bdsf.getGeomType() == GeomType.GeomLin) {
            comboBox_OverlayType.getItems().add("求交");
        }
        // 点对区
        if (zdsf.getGeomType() == GeomType.GeomPnt && bdsf.getGeomType() == GeomType.GeomReg) {
            comboBox_OverlayType.getItems().add("求交");
            comboBox_OverlayType.getItems().add("相减");
        }
        // 线对线
        if (zdsf.getGeomType() == GeomType.GeomLin && bdsf.getGeomType() == GeomType.GeomLin) {
            comboBox_OverlayType.getItems().add("求并");
            comboBox_OverlayType.getItems().add("判别");
        }
        // 线对区
        if (zdsf.getGeomType() == GeomType.GeomLin && bdsf.getGeomType() == GeomType.GeomReg) {
            comboBox_OverlayType.getItems().add("求并");
            comboBox_OverlayType.getItems().add("求交");
            comboBox_OverlayType.getItems().add("相减");
            comboBox_OverlayType.getItems().add("判别");
        }
        // 区对点
        if (zdsf.getGeomType() == GeomType.GeomReg && bdsf.getGeomType() == GeomType.GeomPnt) {
            comboBox_OverlayType.getItems().add("求交");
            comboBox_OverlayType.getItems().add("相减");
        }
        // 区对线
        if (zdsf.getGeomType() == GeomType.GeomReg && bdsf.getGeomType() == GeomType.GeomLin) {
            comboBox_OverlayType.getItems().add("求交");
        }
        // 区对区
        if (zdsf.getGeomType() == GeomType.GeomReg && bdsf.getGeomType() == GeomType.GeomReg) {
            comboBox_OverlayType.getItems().add("求交");
            comboBox_OverlayType.getItems().add("相减");
            comboBox_OverlayType.getItems().add("求并");
            comboBox_OverlayType.getItems().add("对称差");
            comboBox_OverlayType.getItems().add("判别");
            comboBox_OverlayType.getItems().add("区区更新");
        }
        if (comboBox_OverlayType.getItems().size() != 0) {
            comboBox_OverlayType.getSelectionModel().select(0);
            button_OK.setDisable(false);
        } else {
            button_OK.setDisable(true);
        }
    }

    /**
     * 设置不同参照系下的容差
     *
     * @param zdsf
     * @param bdsf
     */
    private void setTolerance(SFeatureCls zdsf, SFeatureCls bdsf) {
        if (zdsf != null && bdsf != null) {
            SRefData sRefData0 = zdsf.getGDataBase().getSRef(zdsf.getsrID());
            SRefData sRefData1 = zdsf.getGDataBase().getSRef(bdsf.getsrID());
            try {
                numberTextField_ToleranceRadius.setNumber(BigDecimal.valueOf(CustomClass.getToleranceBySRefData(CustomClass.TOLERANCE, sRefData0)));
            } catch (Exception ex) {
                MessageBox.information(zdsf.getURL() + "的参考系设置有误!");
            }
        }
    }
}
