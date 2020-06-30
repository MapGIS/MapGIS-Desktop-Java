package com.zondy.mapgis.mapeditor.dialogs;

import com.zondy.mapgis.analysis.rasteranalysis.RasterAnalyse;
import com.zondy.mapgis.analysis.spatialanalysis.*;
import com.zondy.mapgis.base.MessageBox;
import com.zondy.mapgis.base.Window;
import com.zondy.mapgis.base.XPath;
import com.zondy.mapgis.controls.LayerSelectComboBoxItem;
import com.zondy.mapgis.controls.LayerSelectControl;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.controls.common.ButtonEdit;
import com.zondy.mapgis.controls.common.ButtonEditEvent;
import com.zondy.mapgis.controls.common.NumberTextField;
import com.zondy.mapgis.filedialog.FolderType;
import com.zondy.mapgis.filedialog.GDBSelectFolderDialog;
import com.zondy.mapgis.geodatabase.*;
import com.zondy.mapgis.geodatabase.raster.RasterCatalog;
import com.zondy.mapgis.geodatabase.raster.RasterDataset;
import com.zondy.mapgis.geometry.GeoPolygon;
import com.zondy.mapgis.geometry.GeomType;
import com.zondy.mapgis.map.*;
import com.zondy.mapgis.mapeditor.common.CustomClass;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.Optional;
import java.util.UUID;

public class ClipDialog extends Dialog
{
    private LayerSelectControl layerSelectControl_ClipRegionFile;
    private TableView<LayerSettingInfo> tableView_ClipLayerSetting;
    private TextField textField_Prefix;
    private Button button_Prefix;
    private TextField textField_Suffix;
    private Button button_Suffix;
    private ButtonEdit buttonEdit_SavePath;
    private ComboBox<String> comboBox_ClipType;
    private ComboBox<String> comboBox_AttOperate;
    private ComboBox<String> comboBox_GraphOperate;
    private NumberTextField numberTextField_Tolerance;
    private CheckBox checkBox_PolygonValidate;
    private CheckBox checkBox_ClipAOI;
    private Button button_OK;

    Image imagePnt = new Image(getClass().getResourceAsStream("/Png_SfClsPnt_16.png"));
    Image imageLin = new Image(getClass().getResourceAsStream("/Png_SfClsLin_16.png"));
    Image imageReg = new Image(getClass().getResourceAsStream("/Png_SfClsReg_16.png"));
    Image imageRasterCatalog = new Image(getClass().getResourceAsStream("/Png_RasterCatalog_16.png"));
    Image imageRasterDs = new Image(getClass().getResourceAsStream("/Png_RasterDs_16.png"));

    private MapControl mapControl;
    private GeoPolygon clipPolygon;
    private MapLayer clipLayer;     // 记录当前选择的裁剪区图层，下次切换裁剪图层时，会在下面的列表中将其勾起来
    private SFeatureCls clipSFCls;  // 记录从外面选取的裁剪要素类
    private boolean allRasCatalogLayers = true; // 记录被裁剪图层是否全是栅格目录图层，如果是则不让保存到本地目录


    /**
     * 构造并初始化多边形/区文件裁剪对话框
     *
     * @param mapControl 地图控件
     * @param polygon    裁剪区
     */
    public ClipDialog(MapControl mapControl, GeoPolygon polygon)
    {

        this.mapControl = mapControl;
        this.clipPolygon = polygon;
        Map map = null;
        if (this.mapControl != null)
        {
            map = this.mapControl.getMap();
        }
        boolean clipByFile = this.clipPolygon == null;
        if (clipByFile)
        {
            setTitle("区文件裁剪");
        }

        // region Clip Region File

        layerSelectControl_ClipRegionFile = new LayerSelectControl(
                map != null ? (Document) map.getParent() : null, "区简单要素类;6x区文件(*.wp)|sfclsr;*.wp");
        layerSelectControl_ClipRegionFile.setOnSelectedItemChanged(layerSelectControl_ClipRegionFile_SelectedItemChanged_ChangeListener);

        HBox hBox_ClipRegionFile = new HBox(new Label("区文件:"), layerSelectControl_ClipRegionFile);
        HBox.setHgrow(layerSelectControl_ClipRegionFile, Priority.ALWAYS);

        // endregion

        // region Clip Layers Setting

        tableView_ClipLayerSetting = new TableView<LayerSettingInfo>();
        tableView_ClipLayerSetting.setPrefHeight(150);
        TableColumn<LayerSettingInfo, CheckBox> colIsSelected = new TableColumn<>("");
        colIsSelected.setCellValueFactory(param ->
        {
            CheckBox checkBox = new CheckBox();
            checkBox.setSelected(param.getValue().isSelected());
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) ->
            {
                param.getValue().setSelected(newValue);
                boolean canClip = !buttonEdit_SavePath.getText().isEmpty();
                if (canClip)
                {
                    boolean hasClipLayer = false;
                    if (newValue)
                    {
                        hasClipLayer = true;
                    } else
                    {
                        for (int i = 0; i < tableView_ClipLayerSetting.getItems().size(); i++)
                        {
                            if (i != tableView_ClipLayerSetting.getFocusModel().getFocusedIndex() && tableView_ClipLayerSetting.getItems().get(i).isSelected())
                            {
                                hasClipLayer = true;
                                break;
                            }
                        }
                    }
                    canClip = hasClipLayer;
                }
                button_OK.setDisable(!canClip);
            });
            return new SimpleObjectProperty<>(checkBox);
        });
        TableColumn<LayerSettingInfo, HBox> colLayerName = new TableColumn<>("被裁剪图层");
        colLayerName.setCellValueFactory(param ->
        {
            Image image = imagePnt;
            if (param.getValue().getMapLayer().getData() instanceof RasterCatalog)
            {
                image = imageRasterCatalog;
            } else if (param.getValue().getMapLayer().getData() instanceof RasterDataset)
            {
                image = imageRasterDs;
            } else
            {
                GeomType geomType = param.getValue().getMapLayer().getGeometryType();
                switch (geomType)
                {
                    case GeomPnt:
                        image = imagePnt;
                        break;
                    case GeomLin:
                        image = imageLin;
                        break;
                    case GeomReg:
                        image = imageReg;
                        break;
                    default:
                        break;
                }
            }
            HBox hBox = new HBox(new ImageView(image), new Label(param.getValue().getLayerName()));
            return new SimpleObjectProperty<>(hBox);
        });
        TableColumn<LayerSettingInfo, TextField> colDesName = new TableColumn<>("目标类名称");
        colDesName.setCellValueFactory(param ->
        {
            TextField textField = new TextField(param.getValue().getDesName());
            textField.setEditable(true);
            textField.textProperty().addListener((observable, oldValue, newValue) ->
            {
                param.getValue().setDesName(newValue);
            });
            return new SimpleObjectProperty<>(textField);
        });
        tableView_ClipLayerSetting.getColumns().addAll(colIsSelected, colLayerName, colDesName);

        textField_Prefix = new TextField();
        button_Prefix = new Button("统加前缀");
        button_Prefix.setOnAction(button_Prefix_Action_EventHandler);
        textField_Suffix = new TextField("_Clip");
        button_Suffix = new Button("统加后缀");
        button_Suffix.setOnAction(button_Suffix_Action_EventHandler);
        buttonEdit_SavePath = new ButtonEdit();
        buttonEdit_SavePath.setOnButtonClick(buttonEdit_SavePath_ButtonClick_EventHandler);
        buttonEdit_SavePath.setTextEditable(true);

//        HBox hBox_Fix = new HBox(5, new Label("目标类名统改:"), textField_Prefix, button_Prefix, textField_Suffix, button_Suffix);
//        HBox hBox_SavePath = new HBox(new Label("结果保存路径:"), buttonEdit_SavePath);
//        HBox.setHgrow(buttonEdit_SavePath, Priority.ALWAYS);
//        VBox vBox_ClipLayersSetting = new VBox(5, tableView_ClipLayerSetting, hBox_Fix, hBox_SavePath);
        GridPane gridPane_ClipLayersSetting = new GridPane();
        gridPane_ClipLayersSetting.setHgap(5);
        gridPane_ClipLayersSetting.setVgap(5);
        gridPane_ClipLayersSetting.add(tableView_ClipLayerSetting, 0, 0, 5, 1);
        gridPane_ClipLayersSetting.add(new Label("目标类名统改:"), 0, 1, 1, 1);
        gridPane_ClipLayersSetting.add(textField_Prefix, 1, 1, 1, 1);
        gridPane_ClipLayersSetting.add(button_Prefix, 2, 1, 1, 1);
        gridPane_ClipLayersSetting.add(textField_Suffix, 3, 1, 1, 1);
        gridPane_ClipLayersSetting.add(button_Suffix, 4, 1, 1, 1);
        gridPane_ClipLayersSetting.add(new Label("结果保存路径:"), 0, 2, 1, 1);
        gridPane_ClipLayersSetting.add(buttonEdit_SavePath, 1, 2, 4, 1);
        gridPane_ClipLayersSetting.getRowConstraints().add(new RowConstraints(150));
        // endregion

        // region Clip Parameters Setting

        comboBox_ClipType = new ComboBox<>(FXCollections.observableArrayList(
                "内裁", "外裁"));
        comboBox_ClipType.getSelectionModel().select(0);
        comboBox_AttOperate = new ComboBox<>(FXCollections.observableArrayList(
                "不保留原属性", "保留原属性"));
        comboBox_AttOperate.getSelectionModel().select(1);
        comboBox_GraphOperate = new ComboBox<>(FXCollections.observableArrayList(
                "随机", "使用被裁剪类的图形参数", "使用裁剪类的图形参数"));
        comboBox_GraphOperate.getSelectionModel().select(1);
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(8);
        numberTextField_Tolerance = new NumberTextField(new BigDecimal("0.00001"), numberFormat);
        numberTextField_Tolerance.setOnKeyTyped(numberTextField_Tolerance_KeyTyped_EventHandler);
        checkBox_PolygonValidate = new CheckBox("处理复合要素");
        checkBox_ClipAOI = new CheckBox("添加裁剪AOI(栅格数据)");

        GridPane gridPane_clipParametersSetting = new GridPane();
        gridPane_clipParametersSetting.setHgap(4);
        gridPane_clipParametersSetting.setVgap(3);
        gridPane_clipParametersSetting.add(new Label("裁剪类型:"), 0, 0, 1, 1);
        gridPane_clipParametersSetting.add(comboBox_ClipType, 1, 0, 1, 1);
        gridPane_clipParametersSetting.add(new Label("容差半径:"), 2, 0, 1, 1);
        gridPane_clipParametersSetting.add(numberTextField_Tolerance, 3, 0, 1, 1);
        gridPane_clipParametersSetting.add(new Label("属性操作:"), 0, 1, 1, 1);
        gridPane_clipParametersSetting.add(comboBox_AttOperate, 1, 1, 1, 1);
        gridPane_clipParametersSetting.add(checkBox_PolygonValidate, 2, 1, 2, 1);
        gridPane_clipParametersSetting.add(new Label("区图形操作:"), 0, 2, 1, 1);
        gridPane_clipParametersSetting.add(comboBox_GraphOperate, 1, 2, 1, 1);
        gridPane_clipParametersSetting.add(checkBox_ClipAOI, 2, 2, 2, 1);

        // endregion

        // region TitlePane(Group)

        TitledPane titledPane_clipRegionFile = new TitledPane("裁剪区文件", hBox_ClipRegionFile);
        titledPane_clipRegionFile.setCollapsible(false);
        TitledPane titledPane_clipLayersSetting = new TitledPane("裁剪图层设置", gridPane_ClipLayersSetting);
        titledPane_clipLayersSetting.setCollapsible(false);
        TitledPane titledPane_clipParametersSetting = new TitledPane("裁剪参数设置", gridPane_clipParametersSetting);
        titledPane_clipParametersSetting.setCollapsible(false);

        // endregion

        // region Layout

//        layerSelectControl_ClipRegionFile.prefWidth(600);
        comboBox_ClipType.prefWidthProperty().bind(comboBox_GraphOperate.widthProperty());
        comboBox_AttOperate.prefWidthProperty().bind(comboBox_GraphOperate.widthProperty());

        VBox vBox = new VBox(5, titledPane_clipRegionFile, titledPane_clipLayersSetting, titledPane_clipParametersSetting);

        // endregion

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setContent(vBox);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.setPrefWidth(565);
        dialogPane.setPrefHeight(530);
        button_OK = (Button) dialogPane.lookupButton(ButtonType.OK);
        button_OK.addEventFilter(ActionEvent.ACTION, this::button_OK_Click);

        hBox_ClipRegionFile.setVisible(clipByFile);
        gridPane_clipParametersSetting.setVisible(clipByFile);
//        this.simpleButton_ModifyPolygon.Visible = !clipByFile;

        if (this.mapControl != null)
        {
            if (map != null)
            {
                // 初始化图层列表和区文件列表
                for (int i = 0; i < map.getLayerCount(); i++)
                {
                    MapLayer layer = map.getLayer(i);
                    if (layer instanceof VectorLayer)
                    {
                        try
                        {
                            numberTextField_Tolerance.setNumber(BigDecimal.valueOf(CustomClass.getToleranceBySRefData(CustomClass.TOLERANCE, layer.getSrefInfo())));
                        } catch (Exception ex)
                        {
                            MessageBox.information(layer.getURL() + "的参考系设置有误!");
                        }
                    }
                    addClipedLayer(layer, clipByFile);
                }
//                for (LayerSettingInfo info : tableView_ClipLayerSetting.getItems()) {
//                    if (!info.isSelected()) {
//                        checkEdit_SelectAll.Checked = false;
//                        break;
//                    }
//                }
//                this.checkEdit_SelectAll.CheckedChanged += new System.EventHandler(this.checkEdit_SelectAll_CheckedChanged);

                // 初始化结果保存路径
                String sPath = buttonEdit_SavePath.getText();
                if (sPath.isEmpty() || (this.allRasCatalogLayers && !sPath.toLowerCase().startsWith("gdbp://")))
                {
                    if (tableView_ClipLayerSetting.getItems().size() > 0 && tableView_ClipLayerSetting.getItems().get(0).getMapLayer() != null)
                    {
                        sPath = tableView_ClipLayerSetting.getItems().get(0).getMapLayer().getURL();
                        if (!sPath.equals(""))
                        {
                            if (sPath.toLowerCase().startsWith("gdbp://"))
                            {
                                sPath = sPath.substring(0, sPath.lastIndexOf('/'));
                                sPath = sPath.substring(0, sPath.lastIndexOf('/'));
                            } else
                            {
                                if (sPath.toLowerCase().startsWith("file:///"))
                                {
                                    sPath = sPath.substring(8);
                                }
                                sPath = sPath.substring(0, sPath.lastIndexOf(File.separator));
                            }
                        }
                    }
                }
                buttonEdit_SavePath.setText(sPath);

                if (clipByFile && layerSelectControl_ClipRegionFile != null)
                {
                    layerSelectControl_ClipRegionFile.selectFirstItem();
                }
            }
            checkBox_PolygonValidate.setSelected(false);
            comboBox_ClipType.getSelectionModel().select(0);
            comboBox_GraphOperate.getSelectionModel().select(1);
            comboBox_AttOperate.getSelectionModel().select(1);

            textField_Suffix.setText("_Clip"); // 触发TextChanged事件，修改目的类名。
        }
    }

    // event

    private ChangeListener<LayerSelectComboBoxItem> layerSelectControl_ClipRegionFile_SelectedItemChanged_ChangeListener = (observable, oldValue, newValue) ->
    {
        // 如果原来的裁剪图层可见，把它勾起来
        if (this.clipLayer != null)
        {
            for (LayerSettingInfo info : tableView_ClipLayerSetting.getItems())
            {
                if (info.getMapLayer().getHandle() == this.clipLayer.getHandle())
                {
                    if (this.clipLayer.getState() != LayerState.UnVisible)
                    {
                        info.setSelected(true);
                    }
                    break;
                }
            }
            this.clipLayer = null;
        }
        // Close原来选择的裁剪类
        if (this.clipSFCls != null)
        {
            if (this.clipSFCls.hasOpened())
            {
                this.clipSFCls.close();
            }
            this.clipSFCls = null;
        }

        if (layerSelectControl_ClipRegionFile.getSelectedDocumentItem() instanceof MapLayer)
        {
            MapLayer newClipLayer = (MapLayer) layerSelectControl_ClipRegionFile.getSelectedDocumentItem();
            // 将新的裁剪图层勾掉
            for (LayerSettingInfo info : tableView_ClipLayerSetting.getItems())
            {
                if (info.getMapLayer().getHandle() == newClipLayer.getHandle())
                {
                    info.setSelected(false);
                    break;
                }
            }
            this.clipLayer = newClipLayer;
            setClipButtonEnabled();
        } else
        {
            String url = layerSelectControl_ClipRegionFile.getSelectedItemUrl();
            if (!url.toLowerCase().startsWith("gdbp://"))
            {
                url = "file:///" + url;
            }
            SFeatureCls sfCls = new SFeatureCls();
            if (sfCls.openByURL(url) == 0)
            {
                MessageBox.information("所选裁剪框区文件打开失败!");
            } else
            {
                this.clipSFCls = sfCls;
                setClipButtonEnabled();
            }
        }
    };

    private EventHandler<ButtonEditEvent> buttonEdit_SavePath_ButtonClick_EventHandler = event ->
    {
        GDBSelectFolderDialog dialog = new GDBSelectFolderDialog();
        if (this.allRasCatalogLayers)
        {
            dialog.setFolderType(FolderType.MapGIS_DataBase);
        } else
        {
            dialog.setFolderType(FolderType.Disk_Folder | FolderType.MapGIS_DataBase | FolderType.MapGIS_Fds);
        }
        Optional<String[]> optional = dialog.showAndWait();
        if (optional != null && optional.isPresent())
        {
            String[] files = optional.get();
            if (files.length > 0)
            {
                buttonEdit_SavePath.setText(files[0]);
            }
        }
    };

    private EventHandler<KeyEvent> numberTextField_Tolerance_KeyTyped_EventHandler = event ->
    {
        if (event.getCode() == KeyCode.MINUS)
        {
            event.consume();
        }
    };

    private EventHandler<ActionEvent> button_Prefix_Action_EventHandler = event ->
    {
        String prefix = textField_Prefix.getText();
        if (!prefix.isEmpty())
        {
            for (LayerSettingInfo info : tableView_ClipLayerSetting.getItems())
            {
                String destName = prefix + info.getDesName();
                if (destName.getBytes().length < 128)
                {
                    info.setDesName(destName);
                }
            }
        }
        tableView_ClipLayerSetting.refresh();
    };

    private EventHandler<ActionEvent> button_Suffix_Action_EventHandler = event ->
    {
        String postfix = textField_Suffix.getText();
        if (!postfix.isEmpty())
        {
            for (LayerSettingInfo info : tableView_ClipLayerSetting.getItems())
            {
                String destName = info.getDesName() + postfix;
                if (destName.getBytes().length < 128)
                {
                    info.setDesName(destName);
                }
            }
            tableView_ClipLayerSetting.refresh();
        }
    };


    private void button_OK_Click(ActionEvent event)
    {
        StringBuilder errorName = new StringBuilder();
        for (LayerSettingInfo info : tableView_ClipLayerSetting.getItems())
        {
            if (info.isSelected())
            {
                String destName = info.getDesName();
                if (destName.getBytes().length >= 128)
                {
                    errorName.append(!errorName.toString().equals("") ? "\r\n" : "").append(info.getDesName());
                }
            }
        }
        if (errorName.toString().equals("") || ButtonType.YES == MessageBox.questionEx("目标类名称必须少于128个字符!下列参与裁剪的数据目标名称过长:\r\n" + errorName + "\r\n继续裁剪将无法成功创建结果类,是否继续?", Window.primaryStage))
        {
            StringBuilder tipText = new StringBuilder();
            if (!buttonEdit_SavePath.getText().toLowerCase().startsWith("gdbp://"))
            {
                for (LayerSettingInfo info : tableView_ClipLayerSetting.getItems())
                {
                    if (info.isSelected())
                    {
                        MapLayer layer = info.getMapLayer();
                        if (layer instanceof RasterCatalogLayer)
                        {
                            tipText.append(!tipText.toString().equals("") ? "\r\n      " : "      ").append(info.getDesName());
                        }
                    }
                }
            } else
            {
                String[] strs = buttonEdit_SavePath.getText().split("/");
                DataBase db = DataBase.openByURL(strs[0] + "//" + strs[2] + "/" + strs[3]);
                if (db == null)
                {
                    return;
                }
                for (LayerSettingInfo info : tableView_ClipLayerSetting.getItems())
                {
                    if (info.isSelected())
                    {
                        if (db.xClsIsExist(XClsType.XSFCls, info.getDesName().trim()) > 0 || db.xClsIsExist(XClsType.XACls, info.getDesName().trim()) > 0)
                        {
                            MessageBox.information("保存目录下已存在同名数据,请重新修改目的类名或保存目录!");
                            db.close();
                            return;
                        }
                    }
                }
                db.close();
            }
            if (tipText.toString().equals("") || ButtonType.YES == MessageBox.questionEx("不支持将栅格目录裁剪为本地文件数据!\r\n继续裁剪下列数据将保存在其源数据目录下:\r\n" + tipText + "\r\n是否继续"))
            {
//                ClipForm.clipType = this.comboBoxEdit_ClipType.SelectedIndex;
//                ClipForm.MultiFeatureOpr = this.checkEdit_RegValid.Checked;
//                ClipForm.infoOptType = this.comboBoxEdit_RegInfo.SelectedIndex;
//                ClipForm.attOperate = this.comboBoxEdit_AttOperate.SelectedIndex;

//                this.waitForm = new WaitForm(false, true);
//                this.waitForm.CanCancel += new WaitForm.CanCancelHandler(waitForm_CanCancel);
//
//                bool first = true;
//                this.waitForm.Load += (ws, es) =>
//                {
//                    if (first) {
//                        first = false;
//                        Thread threadCheck = new Thread(new ParameterizedThreadStart(ClipLayer));
//                        threadCheck.SetApartmentState(ApartmentState.STA);
//                        threadCheck.CurrentUICulture = System.Threading.Thread.CurrentThread.CurrentUICulture;
//                        threadCheck.Start();
//                    }
//                }
//                ;
//                this.waitForm.ShowDialog(new Win32Window(XHelp.GetMainWindowHandle()));
                clipLayer();
//                this.DialogResult = DialogResult.OK;
            }
        }
    }

    private void clipLayer()
    {
        DataBase tempDB = null;
        boolean isProjTrans = false;
//        this.hasCompleted = false;
        // TODO: 临时获取 clipSFCls
//        MapLayer clipLayer = (MapLayer) layerSelectControl_ClipRegionFile.getSelectedDocumentItem();
//        String clipLayerUrl = layerSelectControl_ClipRegionFile.getSelectedItemUrl();
//        this.clipSFCls = new SFeatureCls();
//        if (this.clipSFCls.openByURL(clipLayerUrl) == 0) {
//            MessageBox.information("所选裁剪框区文件打开失败!");
//        }

        if (this.clipSFCls == null)
        {
            if (this.clipPolygon == null)
            {
                this.clipSFCls = (SFeatureCls) this.clipLayer.getData();
            } else
            {
                tempDB = DataBase.openTempDB();
                if (tempDB != null)
                {
                    isProjTrans = this.mapControl.getMap().getIsProjTrans();
                    this.clipSFCls = new SFeatureCls(tempDB);
                    this.clipSFCls.create(UUID.randomUUID().toString(), GeomType.GeomReg, 0, 0, null);
                    this.clipSFCls.append(this.clipPolygon, null, null);
                    if (isProjTrans)
                    {
                        this.clipSFCls.setsrID(tempDB.getSRefIDByName(this.mapControl.getMap().getProjTrans().getSRSName()));
                    }
                }
            }
        }

        if (this.clipSFCls != null)
        {
            String formText = this.clipPolygon == null ? "区文件裁剪" : "多边形裁剪";
//            this.waitForm.SetProgress(formText, formText + ":", 0, 100, false, false);
            int count = 0;
            for (LayerSettingInfo info : tableView_ClipLayerSetting.getItems())
            {
                if (info.isSelected())
                {
                    count++;
                }
            }
//            this.waitForm.SetProgress(formText, formText + ":", 0, count, false, false);
//            TimeSpan tsBegin = new TimeSpan(DateTime.Now.Ticks);
//            InitPlugin.SetKeyValue("裁剪输出", "[" + "多边形裁剪" + "]：" + DateTime.Now.ToString() + "：开始进行裁剪\r\n");

            StringBuilder failedCreate = new StringBuilder();
            StringBuilder failedClip = new StringBuilder();
            OverlayOption opt = new OverlayOption();
            opt.setOverlayType(comboBox_ClipType.getSelectionModel().getSelectedIndex() == 0 ? OverlayOption.OverlayType.INCLIP : OverlayOption.OverlayType.OUTCLIP);
            opt.setInfoOptType(OverlayOption.OverlayInfoOptType.valueOf(comboBox_GraphOperate.getSelectionModel().getSelectedIndex()));
            opt.setAttOptType(OverlayOption.OverlayAttOptType.valueOf(comboBox_AttOperate.getSelectionModel().getSelectedIndex()));
            opt.setMultiFeatureOption(checkBox_PolygonValidate.isSelected());
            opt.setTolerance(numberTextField_Tolerance.getNumber().doubleValue());
            int val = 1;
            for (LayerSettingInfo info : tableView_ClipLayerSetting.getItems())
            {
                if (info.isSelected() && info.getMapLayer() != null)
                {
                    MapLayer layer = info.getMapLayer();

                    String ext = getFileExtension(info);
                    String url = getResultURL(layer, info.getDesName(), buttonEdit_SavePath.getText(), ext);
                    IBasCls basCls = layer.getData();
                    if (basCls instanceof SFeatureCls)
                    {
                        // region 裁剪简单要素类

                        SFeatureCls sourSFCls = (SFeatureCls) basCls;
                        SFeatureCls desSFCls = new SFeatureCls();
                        int rtn = desSFCls.create(url, layer.getGeometryType());
                        if (rtn <= 0)
                        {
                            failedCreate.append(failedCreate.toString().equals("") ? "" : "、").append("“").append(layer.getName()).append("”");
                        } else
                        {
                            if (this.clipPolygon != null && isProjTrans)
                            {
                                this.clipSFCls.close(); //每次都重新创建是因为这是在循环里面，可能要裁剪多个不同参照系的数据，使用同一个对象投来投去可能就混乱了。
                                SFeatureCls.remove(tempDB, this.clipSFCls.getClsID());
                                this.clipSFCls = new SFeatureCls(tempDB);
                                this.clipSFCls.create(UUID.randomUUID().toString(), GeomType.GeomReg, 0, 0, null);
                                long clipSFClsID = this.clipSFCls.append(this.clipPolygon, null, null);
                                if (clipSFClsID > 0)
                                {
                                    this.clipSFCls.setsrID(tempDB.getSRefIDByName(this.mapControl.getMap().getProjTrans().getSRSName()));
                                    this.clipSFCls.projTrans(sourSFCls.getGDataBase().getSRef(sourSFCls.getsrID()));
                                }
                            }
                            if (SpatialAnalysis.clip(sourSFCls, this.clipSFCls, desSFCls, opt) <= 0)
                            {
                                failedClip.append(failedClip.toString().equals("") ? "" : "、").append("“").append(layer.getName()).append("”");
                            } else
                            {
                                desSFCls.setsrID(desSFCls.getGDataBase().addSRef(sourSFCls.getGDataBase().getSRef(sourSFCls.getsrID())));
                                desSFCls.setScaleXY(sourSFCls.getScaleX(), sourSFCls.getScaleY());
                                desSFCls.setModelName(sourSFCls.getModelName());
                                MapLayer mLayer = null;
                                if (desSFCls.getURL().toLowerCase().startsWith("gdbp://"))
                                {
                                    mLayer = (VectorLayer) layer.clone();
                                } else
                                {
                                    mLayer = (FileLayer6x) layer.clone();
                                }
                                if (mLayer != null)
                                {
                                    mLayer.setURL(desSFCls.getURL());
                                    if (mLayer.connectData())
                                    {
                                        mLayer.setName(desSFCls.getName());
//                                        if (this.mapControl.InvokeRequired)
//                                            this.mapControl.Invoke(new MethodInvoker(delegate { this.mapControl.ActiveMap.Append(mLayer);
//                                            }));
//                                            else
                                        this.mapControl.getMap().append(mLayer);
                                    }
                                }
                            }
                            desSFCls.close();
                        }

                        // endregion
                    } else if (basCls instanceof AnnotationCls)
                    {
                        // region 裁剪注记类

                        AnnotationCls sourACls = (AnnotationCls) basCls;
                        AnnotationCls desACls = new AnnotationCls();
                        int rtn = desACls.create(url);
                        if (rtn <= 0)
                        {
                            failedCreate.append(failedCreate.toString().equals("") ? "" : "、").append("“").append(layer.getName()).append("”");
                        } else
                        {
                            if (this.clipPolygon != null && isProjTrans)
                            {
                                this.clipSFCls.close(); //每次都重新创建是因为这是在循环里面，可能要裁剪多个不同参照系的数据，使用同一个对象投来投去可能就混乱了。
                                SFeatureCls.remove(tempDB, this.clipSFCls.getClsID());
                                this.clipSFCls = new SFeatureCls(tempDB);
                                this.clipSFCls.create(UUID.randomUUID().toString(), GeomType.GeomReg, 0, 0, null);
                                long clipSFClsID = this.clipSFCls.append(this.clipPolygon, null, null);
                                if (clipSFClsID > 0)
                                {
                                    this.clipSFCls.setsrID(tempDB.getSRefIDByName(this.mapControl.getMap().getProjTrans().getSRSName()));
                                    this.clipSFCls.projTrans(sourACls.getGDataBase().getSRef(sourACls.getsrID()));
                                }
                            }

                            if (SpatialAnalysis.clipAnnotation(sourACls, this.clipSFCls, desACls, opt) <= 0)
                            {
                                failedClip.append(failedClip.toString() == "" ? "" : "、").append("“").append(layer.getName()).append("”");
                            } else
                            {
                                desACls.setsrID(desACls.getGDataBase().addSRef(sourACls.getGDataBase().getSRef(sourACls.getsrID())));
                                desACls.setScaleXY(sourACls.getScaleX(), sourACls.getScaleY());
                                desACls.setModelName(sourACls.getModelName());

                                MapLayer mLayer = null;
                                if (desACls.getURL().toLowerCase().startsWith("gdbp://"))
                                {
                                    mLayer = (VectorLayer) layer.clone();
                                } else
                                {
                                    mLayer = (FileLayer6x) layer.clone();
                                }
                                if (mLayer != null)
                                {
                                    String layerURL = url;
                                    String layerName = desACls.getName();
                                    if (layerURL.toLowerCase().startsWith("file:///") && layerName.toLowerCase().endsWith("_ann"))
                                    {
                                        layerName = layerName.substring(0, layerName.length() - 4);
                                    }

                                    mLayer.setURL(layerURL);
                                    if (mLayer.connectData())
                                    {
                                        mLayer.setName(layerName);
//                                        if (this.mapControl.InvokeRequired)
//                                            this.mapControl.Invoke(new MethodInvoker(delegate { this.mapControl.ActiveMap.Append(mLayer);
//                                            }));
//                                            else
                                        this.mapControl.getMap().append(mLayer);
                                    }
                                }
                            }
                            desACls.close();
                        }

                        // endregion
                    } else if (basCls instanceof RasterDataset)
                    {
                        // region 裁剪栅格数据集

                        RasterDataset rasDs = (RasterDataset) basCls;
                        if (this.clipPolygon != null && isProjTrans)
                        {
                            this.clipSFCls.close(); //每次都重新创建是因为这是在循环里面，可能要裁剪多个不同参照系的数据，使用同一个对象投来投去可能就混乱了。
                            SFeatureCls.remove(tempDB, this.clipSFCls.getClsID());
                            this.clipSFCls = new SFeatureCls(tempDB);
                            this.clipSFCls.create(UUID.randomUUID().toString(), GeomType.GeomReg, 0, 0, null);
                            long clipSFClsID = this.clipSFCls.append(this.clipPolygon, null, null);
                            if (clipSFClsID > 0)
                            {
                                this.clipSFCls.setsrID(tempDB.getSRefIDByName(this.mapControl.getMap().getProjTrans().getSRSName()));
                                this.clipSFCls.projTrans(rasDs.getSref());
                            }
                        }

                        RasterAnalyse rasterAnalyse = new RasterAnalyse();
                        if (rasterAnalyse.clipDataSet(rasDs, this.clipSFCls, checkBox_ClipAOI.isSelected(), url, null, null) <= 0) {
//                        RasImgSubset rasAna = new RasImgSubset();
//                        rasAna.IsAddClipAOI(checkBox_ClipAOI.isSelected());
//                        int n = rasDs.getBandNum();
//                        int[] ints = new int[n];
//                        for (int i = 0; i < n; i++) {
//                            ints[i] = i + 1;
//                        }
//                        rasAna.SetData(rasDs, ints);
//                        rasAna.SetClipType(comboBox_ClipType.getSelectionModel().getSelectedIndex());
//                        if (rasAna.RsClipImageBySFCls(this.clipSFCls, 0, 3, url) <= 0) {
                            failedClip.append(failedClip.toString().equals("") ? "" : "、").append("“").append(layer.getName()).append("”");
                        } else {
                            RasterLayer rasLayer = (RasterLayer) layer.clone();
                            rasLayer.detachData();
                            rasLayer.setURL(url);
                            if (rasLayer.connectData()) {
                                rasLayer.setName(info.getDesName() + ext);
//                                if (this.mapControl.InvokeRequired)
//                                    this.mapControl.Invoke(new MethodInvoker(delegate { this.mapControl.ActiveMap.Append(rasLayer);
//                                    }));
//                                    else
                                this.mapControl.getMap().append(rasLayer);
                            }
                        }

                        // endregion
                    } else if (basCls instanceof RasterCatalog)
                    {
                        // region 裁剪栅格目录

                        RasterCatalog rasCat = (RasterCatalog) basCls;
                        if (this.clipPolygon != null && isProjTrans)
                        {
                            this.clipSFCls.close(); //每次都重新创建是因为这是在循环里面，可能要裁剪多个不同参照系的数据，使用同一个对象投来投去可能就混乱了。
                            SFeatureCls.remove(tempDB, this.clipSFCls.getClsID());
                            this.clipSFCls = new SFeatureCls(tempDB);
                            this.clipSFCls.create(UUID.randomUUID().toString(), GeomType.GeomReg, 0, 0, null);
                            long clipSFClsID = this.clipSFCls.append(this.clipPolygon, null, null);
                            if (clipSFClsID > 0)
                            {
                                this.clipSFCls.setsrID(tempDB.getSRefIDByName(this.mapControl.getMap().getProjTrans().getSRSName()));
                                this.clipSFCls.projTrans(rasCat.getGDataBase().getSRef((int) rasCat.getInfo().getSrID()))
                                ;
                            }
                        }

                        RasterAnalyse rasterAnalyse = new RasterAnalyse();
                        if (rasterAnalyse.clipDataSet(null, this.clipSFCls, checkBox_ClipAOI.isSelected(), url, null, null) <= 0) {
//                        RasImgSubset rasAna = new RasImgSubset();
//                        rasAna.IsAddClipAOI(checkBox_ClipAOI.isSelected());
//                        rasAna.SetData(rasCat);
//                        rasAna.SetClipType(comboBox_ClipType.getSelectionModel().getSelectedIndex());
//                        if (rasAna.RsClipCatalogBySFCls(this.clipSFCls, 3, 1, url) <= 0) {
                            failedClip.append(failedClip.toString().equals("") ? "" : "、").append("“").append(layer.getName()).append("”");
                        } else {
                            RasterCatalogLayer rCatLayer = (RasterCatalogLayer) layer.clone();
                            rCatLayer.detachData();
                            rCatLayer.setURL(url);
                            if (rCatLayer.connectData()) {
                                rCatLayer.setName(info.getDesName());
//                                if (this.mapControl.InvokeRequired)
//                                    this.mapControl.Invoke(new MethodInvoker(delegate { this.mapControl.ActiveMap.Append(rCatLayer);
//                                    }));
//                                    else
                                this.mapControl.getMap().append(rCatLayer);
                            }
                        }

                        // endregion
                    }

//                    this.waitForm.SetProgress(formText, Resources.String_Cuting + layer.Name + "：", val++, count, false, false);
//                    if (this.waitForm.Canceled)
//                        break;
                }
            }
//            opt.Dispose();
            if (this.clipPolygon != null || clipLayer == null)
            {
                int clsID = this.clipSFCls.getClsID();
                this.clipSFCls.close();
                if (this.clipPolygon != null)
                {
                    SFeatureCls.remove(tempDB, clsID);
                }
            }
//            if (!this.waitForm.Canceled) {
////                if (failedCreate.toString() != "")
////                    failedCreate = new StringBuilder(Resources.String_Layer + failedCreate.toString() + Resources.String_ResultClsCreateFailed);
////                if (failedClip.toString() != "")
////                    failedCreate.append((failedCreate.toString() == "" ? "" : "\r\n") + Resources.String_Layer + failedClip + Resources.String_ClipFailed);
////
////                if (failedCreate.toString() != "") {
////                    bool isSucceed = false;
////                    if (failedClip.toString() != "")
////                        isSucceed = MapGIS.Desktop.UI.Controls.MapGISErrorForm.ShowLastError();
////                    if (!isSucceed)
////                        XMessageBox.Information(failedCreate.toString());
////                } else {
////                    TimeSpan tsEnd = new TimeSpan(DateTime.Now.Ticks);
////                    TimeSpan tsEx = tsEnd.Subtract(tsBegin).Duration();
////                    String timeSpend = InitPlugin.FormatTime(tsEx);
////                    InitPlugin.SetKeyValue(Resources.String_ClipOutput, "[" + "多边形裁剪" + "]：" + DateTime.Now.ToString() + "：" + Resources.String_ClipEnd + timeSpend + "\r\n");
////                }
////            }
        }
        if (tempDB != null)
        {
            tempDB.close();
            tempDB.dispose();
        }
//        this.waitForm.Close();
//        this.hasCompleted = true;
    }


    /**
     * 获取裁剪结果URL，如果选择的保存路径为本地目录，则栅格目录存到源目录
     *
     * @param layer    原图层
     * @param newName  图层裁剪结果名称
     * @param savePath 保存路径
     * @param ext      文件后缀
     * @return 裁剪结果图层的URL
     */
    private String getResultURL(MapLayer layer, String newName, String savePath, String ext)
    {
        String rtnURL = "";
        if (layer != null && !savePath.isEmpty())
        {
            if (savePath.toLowerCase().startsWith("gdbp://"))
            {
                // region 数据库存储路径

                if (!savePath.endsWith("/"))
                {
                    savePath += "/";
                }
                XClsType clsType = layer.getClsType();
                if (XClsType.XSFCls.equals(clsType))
                {
                    rtnURL = savePath + "sfcls/" + newName;
                } else if (XClsType.XACls.equals(clsType))
                {
                    rtnURL = savePath + "acls/" + newName;
                } else if (XClsType.XRds.equals(clsType))
                {
                    String[] names = savePath.substring(7).split("/");
                    if (names.length >= 2)//不能裁剪到栅格目录下去
                    {
                        rtnURL = "GDBP://" + names[0] + "/" + names[1] + "/ras/" + newName;
                    }
                } else if (XClsType.XRcat.equals(clsType))
                {
                    rtnURL = savePath + "rcat/" + newName;
                }

                // endregion
            } else
            {
                // region 本地存储路径

                String oldURL = layer.getURL();
                if (layer instanceof RasterCatalogLayer)
                {
                    rtnURL = oldURL.substring(0, oldURL.lastIndexOf("/") + 1) + newName;
                } else
                {
                    rtnURL = Paths.get(savePath, newName).toString();
                    if (!rtnURL.endsWith(ext))
                    {
                        rtnURL += ext;
                    }
                    rtnURL = "file:///" + rtnURL;
                }

                // endregion
            }
        }
        return rtnURL;
    }

    /**
     * 设置“裁剪”按钮是否可用
     */
    private void setClipButtonEnabled()
    {
        boolean canClip = (this.clipPolygon != null || this.clipSFCls != null || this.clipLayer != null) && !this.buttonEdit_SavePath.getText().isEmpty();
        if (canClip)
        {
            boolean hasClipLayer = false;
            for (LayerSettingInfo info : tableView_ClipLayerSetting.getItems())
            {
                if (info.isSelected())
                {
                    hasClipLayer = true;
                    break;
                }
            }
            canClip = hasClipLayer;
        }
        button_OK.setDisable(!canClip);
    }

    /**
     * 将待裁剪的图层信息添加到表格中
     *
     * @param layer      图层对象
     * @param clipByFile 如果是根据文件裁剪，则将区图层添加到裁剪图层的选择下拉框中
     */
    private void addClipedLayer(MapLayer layer, boolean clipByFile)
    {
        if (layer instanceof VectorLayer || layer instanceof RasterLayer || layer instanceof RasterCatalogLayer)
        {
//            int imgIndex = 0;
//            String fileType = "";
            if (layer instanceof VectorLayer)
            {
//                // region 矢量图层
//
                this.allRasCatalogLayers = false;
//                switch (layer.getGeometryType()) {
//                    case GeomType.GeomPnt: {
//                        imgIndex = 0;
//                        fileType = ".wt";
//                        break;
//                    }
//                    case GeomType.GeomLin: {
//                        imgIndex = 1;
//                        fileType = ".wl";
//                        break;
//                    }
//                    case GeomType.GeomReg: {
//                        imgIndex = 2;
//                        fileType = ".wp";
//                        break;
//                    }
//                    case GeomType.GeomAnn: {
//                        imgIndex = 3;
//                        fileType = ".wt";
//                        break;
//                    }
//                    default:
//                        break;
//                }
//
//                // endregion
            } else if (layer instanceof RasterLayer)
            {
//                fileType = "MapGIS MSI文件(*.msi)";
                this.allRasCatalogLayers = false;
//                imgIndex = 4;
//            } else {
//                imgIndex = 5;
            }

//            int layerNo = this.repositoryItemImageComboBox_ClipedLayer.Items.Count;
//            this.repositoryItemImageComboBox_ClipedLayer.Items.Add(new ImageComboBoxItem(layer.Name, layerNo, imgIndex));
//            this.dataTable.Rows.Add(layer.State != LayerState.UnVisible, layerNo, layer.Name, fileType, layer);
            LayerSettingInfo layerSettingInfo = new LayerSettingInfo(layer, "");
            layerSettingInfo.setSelected(layer.getState() != LayerState.UnVisible);
            layerSettingInfo.setDesName(layer.getName());
            tableView_ClipLayerSetting.getItems().add(layerSettingInfo);
        } else if (layer instanceof GroupLayer)
        {
            GroupLayer groupLayer = (GroupLayer) layer;
            for (int i = 0; i < groupLayer.getCount(); i++)
            {
                addClipedLayer(groupLayer.item(i), clipByFile);
            }
        }
    }

    /**
     * 获取当前行的后缀名
     *
     * @param info 当前行信息
     * @return 存成本地文件时的后缀名
     */
    private String getFileExtension(LayerSettingInfo info)
    {
        String ext = "";
        String sPath = buttonEdit_SavePath.getText();
        if (sPath != null && !sPath.toLowerCase().startsWith("gdbp://"))
        {
            if (info != null && info.getMapLayer() != null)
            {
                MapLayer layer = info.getMapLayer();
//                if (layer instanceof RasterLayer) {
//                    string rasType = row["FileType"] as string;
//                    if (rasType == "MapGIS MSI" + Resources.String_File + "(*.msi)")
//                        ext = ".msi";
//                    else if (rasType == "GTiff" + Resources.String_File + "(*.tif)")
//                        ext = ".tif";
//                    else if (rasType == "HFA" + Resources.String_File + "(*.img)")
//                        ext = ".img";
//                    else if (rasType == "PCIDSK" + Resources.String_File + "(*.pix)")
//                        ext = ".pix";
//                    else if (rasType == "ENVI" + Resources.String_File + "(*.evi)")
//                        ext = ".evi";
//                } else {
//                    ext = row["FileType"] as string;
//                }
            }
        }
        return ext;
    }
}
