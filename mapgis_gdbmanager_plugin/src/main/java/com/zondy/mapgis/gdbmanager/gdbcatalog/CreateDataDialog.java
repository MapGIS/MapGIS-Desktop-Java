package com.zondy.mapgis.gdbmanager.gdbcatalog;

import com.zondy.mapgis.att.Field;
import com.zondy.mapgis.att.Fields;
import com.zondy.mapgis.base.*;
import com.zondy.mapgis.controls.common.ButtonEdit;
import com.zondy.mapgis.controls.common.ZDComboBox;
import com.zondy.mapgis.geodatabase.*;
import com.zondy.mapgis.geodatabase.raster.*;
import com.zondy.mapgis.geometry.AnnType;
import com.zondy.mapgis.geometry.GeomType;
import com.zondy.mapgis.geometry.Rect;
import com.zondy.mapgis.sref.SRefManagerDialog;
import com.zondy.mapgis.srs.SRefData;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author CR
 * @file CreateDataDialog.java
 * @brief 新建数据
 * @create 2020-01-06.
 */
public class CreateDataDialog extends Dialog {
    private ZDComboBox<XClsType> comboBoxClsType = new ZDComboBox<>();
    private TextField textFieldName = new TextField();
    private Label labelGeoType = new Label("几何类型:");
    private ZDComboBox<GeomType> comboBoxGeoType = new ZDComboBox<>();
    private Label labelDS = new Label("要素数据集:");
    private ZDComboBox<String> comboBoxDS = new ZDComboBox<>();
    private ZDComboBox<String> comboboxRCat = new ZDComboBox<>();
    private Label labelSRef = new Label("空间参照系:");
    private ButtonEdit buttonEditSRef = new ButtonEdit("DEFAULT Sref");
    private Tooltip tooltipError = new Tooltip();//错误提示
    private DataBase db;
    private int clsID = 0;
    private int dsID = 0;
    private XClsType clsType = XClsType.Unknown;
    private GridPane gridPaneExpand;//展开的显示详细信息的布局控件
    private ZDComboBox<PixelType> comboBoxRasType = new ZDComboBox<>();
    private TextField textFieldBandCount = new TextField("1");
    private TextField textFieldRowCount = new TextField("1");
    private TextField textFieldColCount = new TextField("1");
    private TextField textFieldRowScale = new TextField("1");
    private TextField textFieldColScale = new TextField("1");
    private TextField textFieldXMin = new TextField("0");
    private TextField textFieldYMin = new TextField("0");
    private Window window;

    public CreateDataDialog(DataBase db) {
        this(db, XClsType.XSFCls);
    }

    public CreateDataDialog(DataBase db, XClsType clsType) {
        this(db, clsType, 0);
    }

    public CreateDataDialog(DataBase db, int dsID) {
        this(db, XClsType.XSFCls, dsID);
    }

    public CreateDataDialog(DataBase db, XClsType clsType, int dsID) {
        this.db = db;

        //region 数据类型下拉
        this.comboBoxClsType.getItems().addAll(XClsType.XSFCls, XClsType.XACls, XClsType.XOCls);
        if (dsID == 0) {
            this.comboBoxClsType.getItems().addAll(XClsType.XFds, XClsType.XRcat, XClsType.XRds);
        }
        this.comboBoxClsType.prefWidthProperty().bind(this.textFieldName.widthProperty());
        this.comboBoxClsType.setConverter(new StringConverter<XClsType>() {
            @Override
            public String toString(XClsType object) {
                return LanguageConvert.xClsTypeConvert(object);
            }

            @Override
            public XClsType fromString(String String) {
                return LanguageConvert.xClsTypeConvert(String);
            }
        });
        this.comboBoxClsType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
        {
            this.labelSRef.setVisible(!newValue.equals(XClsType.XOCls));
            this.buttonEditSRef.setVisible(!newValue.equals(XClsType.XOCls));
            this.labelGeoType.setVisible(newValue.equals(XClsType.XSFCls));
            this.comboBoxGeoType.setVisible(newValue.equals(XClsType.XSFCls));
            boolean isRas = newValue.equals(XClsType.XRds) || newValue.equals(XClsType.XRcat);
            this.labelDS.setText(isRas ? "栅格目录:" : "要素数据集:");
            this.comboBoxDS.setVisible(!isRas);
            this.comboboxRCat.setVisible(isRas);
            this.comboBoxDS.setDisable(newValue.equals(XClsType.XFds));
            this.comboboxRCat.setDisable(newValue.equals(XClsType.XRcat));

            if (newValue.equals(XClsType.XRds)) {
                if (this.gridPaneExpand == null) {
                    this.gridPaneExpand = new GridPane();
                    this.gridPaneExpand.setHgap(6);
                    this.gridPaneExpand.setVgap(6);
                    this.gridPaneExpand.add(new Label("像元类型:"), 0, 0);
                    this.gridPaneExpand.add(new Label("波段数:"), 0, 1);
                    this.gridPaneExpand.add(new Label("行数:"), 0, 2);
                    this.gridPaneExpand.add(new Label("列数:"), 0, 3);
                    this.gridPaneExpand.add(new Label("行向分辨率:"), 0, 4);
                    this.gridPaneExpand.add(new Label("列向分辨率:"), 0, 5);
                    this.gridPaneExpand.add(new Label("左下角X:"), 0, 6);
                    this.gridPaneExpand.add(new Label("左下角Y:"), 0, 7);
                    this.gridPaneExpand.add(this.textFieldBandCount, 1, 1);
                    this.gridPaneExpand.add(this.textFieldRowCount, 1, 2);
                    this.gridPaneExpand.add(this.textFieldColCount, 1, 3);
                    this.gridPaneExpand.add(this.textFieldRowScale, 1, 4);
                    this.gridPaneExpand.add(this.textFieldColScale, 1, 5);
                    this.gridPaneExpand.add(this.textFieldXMin, 1, 6);
                    this.gridPaneExpand.add(this.textFieldYMin, 1, 7);
                    this.gridPaneExpand.add(this.comboBoxRasType, 1, 0);//ComboBox需要放在其bind的对象后面添加，否则，在linux下面首次弹出来长度不对。
                    GridPane.setHgrow(this.textFieldBandCount, Priority.ALWAYS);

                    this.comboBoxRasType.prefWidthProperty().bind(this.textFieldBandCount.widthProperty());
                    this.comboBoxRasType.getItems().addAll(PixelType.Byte, PixelType.UInt16, PixelType.Int16, PixelType.UInt32, PixelType.Int32, PixelType.Float32, PixelType.Float64, PixelType.CInt16, PixelType.CInt32, PixelType.CFloat32, PixelType.CFloat64, PixelType.Bit, PixelType.Int8);
                    this.comboBoxRasType.getSelectionModel().select(0);
                    this.comboBoxRasType.setConverter(new StringConverter<PixelType>() {
                        @Override
                        public String toString(PixelType object) {
                            return LanguageConvert.pixelTypeConvert(object);
                        }

                        @Override
                        public PixelType fromString(String string) {
                            return LanguageConvert.pixelTypeConvert(string);
                        }
                    });
                }
                super.getDialogPane().setExpandableContent(this.gridPaneExpand);
            } else {
                super.getDialogPane().setExpandableContent(null);
            }
        });
        this.comboBoxClsType.getSelectionModel().select(clsType);
        //endregion

        //region 数据名称
        this.textFieldName.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if (!XString.isNullOrEmpty(newValue)) {
                char[] invalidChars = this.db.getServer().getInvalidChars(3);//3-类名称
                List<Character> invalidCharList = new ArrayList<>();
                if (invalidChars != null && invalidChars.length > 0) {
                    for (char ch : invalidChars) {
                        invalidCharList.add(ch);
                    }
                }
                StringProperty errorMsg = new SimpleStringProperty();
                if (!XString.isTextValid(newValue, 128, invalidCharList, errorMsg)) {
                    this.textFieldName.setText(oldValue);
                    UIFunctions.showErrorTip(this.textFieldName, errorMsg.get(), this.tooltipError);
                }
            }
        });
        //endregion

        //region 要素数据集/栅格目录下拉
        this.comboBoxDS.getItems().add("(无)");
        this.comboBoxDS.prefWidthProperty().bind(this.textFieldName.widthProperty());
        if (this.db != null) {
            int[] dsIds = this.db.getXclses(XClsType.XFds, 0);
            if (dsIds != null && dsIds.length > 0) {
                for (int id : dsIds) {
                    this.comboBoxDS.getItems().add(this.db.getXclsName(XClsType.XFds, id));
                }
            }
        }
        if (dsID > 0) {
            this.comboBoxDS.getSelectionModel().select(this.db.getXclsName(XClsType.XFds, dsID));
        } else {
            this.comboBoxDS.getSelectionModel().select(0);
        }

        this.comboboxRCat.getItems().add("(无)");
        this.comboboxRCat.prefWidthProperty().bind(this.textFieldName.widthProperty());
        if (this.db != null) {
            int[] dsIds = this.db.getXclses(XClsType.XRcat, 0);
            if (dsIds != null && dsIds.length > 0) {
                for (int id : dsIds) {
                    this.comboboxRCat.getItems().add(this.db.getXclsName(XClsType.XRcat, id));
                }
            }
        }
        if (dsID > 0) {
            this.comboboxRCat.getSelectionModel().select(this.db.getXclsName(XClsType.XRcat, dsID));
        } else {
            this.comboboxRCat.getSelectionModel().select(0);
        }
        //endregion

        //region 简单要素类的几何类型下拉
        this.comboBoxGeoType.getItems().addAll(GeomType.GeomPnt, GeomType.GeomLin, GeomType.GeomReg, GeomType.GeomSurface, GeomType.GeomEntity);
        this.comboBoxGeoType.prefWidthProperty().bind(this.textFieldName.widthProperty());
        this.comboBoxGeoType.getSelectionModel().select(0);
        this.comboBoxGeoType.setConverter(new StringConverter<GeomType>() {
            @Override
            public String toString(GeomType object) {
                return LanguageConvert.geomTypeConvert(object);
            }

            @Override
            public GeomType fromString(String String) {
                return LanguageConvert.geomTypeConvert(String);
            }
        });
        //endregion

        //region 参照系
        this.buttonEditSRef.setOnButtonClick(event ->
        {
            SRefManagerDialog dlg = new SRefManagerDialog();
            dlg.initOwner(CreateDataDialog.this.window);
            if (dlg.showAndWait().equals(Optional.of(ButtonType.OK))) {
                this.buttonEditSRef.setText(dlg.getSelectedSRef().getSRSName());
                this.buttonEditSRef.setUserData(dlg.getSelectedSRef());
            }
        });
        //endregion

        //region 布局GridPane
        GridPane gridPane = new GridPane();
        gridPane.setVgap(6);
        gridPane.setHgap(6);
        gridPane.add(new Label("数据类型:"), 0, 0);
        gridPane.add(new Label("名称:"), 0, 1);
        gridPane.add(this.labelDS, 0, 2);
        gridPane.add(this.labelSRef, 0, 3);
        gridPane.add(this.labelGeoType, 0, 4);
        gridPane.add(this.textFieldName, 1, 1);
        gridPane.add(this.comboBoxDS, 1, 2);
        gridPane.add(this.comboboxRCat, 1, 2);
        gridPane.add(this.buttonEditSRef, 1, 3);
        gridPane.add(this.comboBoxGeoType, 1, 4);
        gridPane.add(this.comboBoxClsType, 1, 0);//ComboBox需要放在其bind的对象后面添加，否则，在linux下面首次弹出来长度不对。
        GridPane.setHgrow(this.textFieldName, Priority.ALWAYS);
        //endregion

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setContent(gridPane);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.setPrefWidth(400);
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, this::okButtonClick);
        this.setTitle("创建");
        this.window = dialogPane.getScene().getWindow();
    }

    public int getClsID() {
        return this.clsID;
    }

    public int getDsID() {
        return this.dsID;
    }

    public XClsType getClsType() {
        return this.clsType;
    }

    /**
     * 确定
     */
    private void okButtonClick(ActionEvent event) {
        String errorMsg = "";
        String clsName = this.textFieldName.getText();
        this.clsType = this.comboBoxClsType.getValue();
        if (XString.isNullOrEmpty(clsName)) {
            errorMsg = "数据名称不能为空。";
        } else if (this.db.xClsIsExist(clsType, clsName) > 0) {
            errorMsg = String.format("数据库中已经存在同名%s,请重新命名。", LanguageConvert.xClsTypeConvert(this.clsType));
        }

        if (errorMsg != "") {
            MessageBox.information(errorMsg);
            this.textFieldName.requestFocus();
        } else {
            int srID = 1;
            if (this.buttonEditSRef.isVisible() && this.buttonEditSRef.getUserData() instanceof SRefData) {
                SRefData sref = (SRefData) this.buttonEditSRef.getUserData();
                srID = this.db.addSRef(sref);
            }

            if (XClsType.XFds.equals(clsType)) {
                this.clsID = (int) this.db.createFds(clsName, srID);
            } else if (XClsType.XSFCls.equals(clsType)) {
                this.dsID = this.comboBoxDS.getValue().equals("(无)") ? 0 : (int) this.db.xClsIsExist(XClsType.XFds, this.comboBoxDS.getValue());
                GeomType geoType = this.comboBoxGeoType.getValue();
                Fields flds = this.getDefaultFields(geoType);
                SFeatureCls sfcls = new SFeatureCls(db);
                this.clsID = sfcls.create(clsName, geoType, this.dsID, srID, flds);
                if (this.clsID > 0) {
                    sfcls.close();
                }
            } else if (XClsType.XACls.equals(clsType)) {
                this.dsID = this.comboBoxDS.getValue().equals("(无)") ? 0 : (int) this.db.xClsIsExist(XClsType.XFds, this.comboBoxDS.getValue());
                Fields flds = this.getDefaultFields(GeomType.GeomAnn);
                AnnotationCls aCls = new AnnotationCls(this.db);
                this.clsID = aCls.create(clsName, AnnType.AnnText, this.dsID, srID, flds);
                if (this.clsID > 0) {
                    aCls.close();
                }
            } else if (XClsType.XOCls.equals(clsType)) {
                this.dsID = this.comboBoxDS.getValue().equals("(无)") ? 0 : (int) this.db.xClsIsExist(XClsType.XFds, this.comboBoxDS.getValue());
                Fields flds = new Fields();
                Field fldID = new Field();
                fldID.setFieldName("ID");
                fldID.setFieldType(Field.FieldType.fldLong);
                //fldID.setEditable((short) 1);
                fldID.setFieldLength((short) 10);

                flds.appendField(fldID);

                ObjectCls oCls = new ObjectCls(db);
                this.clsID = oCls.create(clsName, this.dsID, 1, flds);
                if (this.clsID > 0) {
                    oCls.close();
                }
            } else if (XClsType.XRcat.equals(clsType)) {
                RasterCatalog rcat = new RasterCatalog(db);
                this.clsID = (int) rcat.create2(clsName);
                if (this.clsID > 0) {
                    if (!rcat.hasOpened())//封装问题：此处应该在create完成后是打开状态才对。
                    {
                        rcat.open(this.clsID);
                    }
                    RCatInfo rcatInfo = (RCatInfo) rcat.getInfo();
                    if (rcatInfo != null) {
                        rcatInfo.setSrID(srID);
                    }
                    rcat.setInfo(rcatInfo);
                    rcat.close();
                }
            } else if (XClsType.XRds.equals(clsType)) {
                RasterDataset rds = new RasterDataset(this.db);
                int rowCount = Integer.parseInt(this.textFieldRowCount.getText());
                int colCount = Integer.parseInt(this.textFieldColCount.getText());
                if (rds.create(RasterFormat.MAPGIS7MSI, clsName, colCount, rowCount, Integer.parseInt(this.textFieldBandCount.getText()), this.comboBoxRasType.getValue()) > 0) {
                    this.clsID = rds.getClsID();
                    double xMin = Double.parseDouble(this.textFieldXMin.getText());
                    double yMin = Double.parseDouble(this.textFieldYMin.getText());
                    rds.setRange(new Rect(xMin, yMin, xMin + (colCount * Double.parseDouble(this.textFieldColScale.getText())), yMin + (rowCount * Double.parseDouble(this.textFieldRowScale.getText()))));

                    RDsInfo rdsInfo = (RDsInfo) rds.getInfo();
                    if (rdsInfo == null) {
                        rdsInfo = new RDsInfo();
                    }
                    rdsInfo.setSrID(srID);
                    rds.setInfo(rdsInfo);

                    this.dsID = this.comboboxRCat.getValue().equals("(无)") ? 0 : (int) this.db.xClsIsExist(XClsType.XRcat, this.comboboxRCat.getValue());
                    if (this.dsID > 0) {
                        RasterCatalog rcat = new RasterCatalog(db);
                        if (rcat.open(this.dsID) > 0) {
                            rcat.insertItem(clsName);
                            rcat.close();
                        }
                    }
                    rds.close();
                }
            }

            if (this.clsID <= 0) {
                MessageBox.information(String.format("创建%s-%s失败。", LanguageConvert.xClsTypeConvert(this.clsType), clsName));
            }
        }
    }

    /**
     * 根据界面上设置的几何形态获取初始的属性结构（添加默认字段）
     *
     * @param geoType 几何形态
     * @return 初始属性结构
     */
    private Fields getDefaultFields(GeomType geoType) {
        Fields initFields = new Fields();
        if (GeomType.GeomAnn.equals(geoType) || GeomType.GeomPnt.equals(geoType)) {
            initFields.appendField(GISDefaultValues.getDefaultFieldLayer());
        } else if (GeomType.GeomLin.equals(geoType)) {
            initFields.appendField(GISDefaultValues.getDefaultFieldLayer());
            initFields.appendField(GISDefaultValues.getDefaultFieldLength());
        } else if (GeomType.GeomReg.equals(geoType)) {
            initFields.appendField(GISDefaultValues.getDefaultFieldLayer());
            initFields.appendField(GISDefaultValues.getDefaultFieldArea());
            initFields.appendField(GISDefaultValues.getDefaultFieldPerimeter());
        } else if (GeomType.GeomSurface.equals(geoType)) {
            initFields.appendField(GISDefaultValues.getDefaultFieldLayer());
            initFields.appendField(GISDefaultValues.getDefaultFieldSurfArea());
        } else if (GeomType.GeomEntity.equals(geoType)) {
            initFields.appendField(GISDefaultValues.getDefaultFieldLayer());
            initFields.appendField(GISDefaultValues.getDefaultFieldSurfArea());
            initFields.appendField(GISDefaultValues.getDefaultFieldVolume());
        }
        return initFields;
    }
}
