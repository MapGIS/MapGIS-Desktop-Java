package com.zondy.mapgis.dataconvert.option;

import com.zondy.mapgis.base.EnumUtils;
import com.zondy.mapgis.base.UIFunctions;
import com.zondy.mapgis.base.XFunctions;
import com.zondy.mapgis.base.XString;
import com.zondy.mapgis.controls.common.ButtonEdit;
import com.zondy.mapgis.controls.common.TitleSeparator;
import com.zondy.mapgis.controls.common.ZDComboBox;
import com.zondy.mapgis.dataconvert.ConvertItem;
import com.zondy.mapgis.dataconvert.ConvertOption;
import com.zondy.mapgis.dataconvert.CustomOperate;
import com.zondy.mapgis.dataconvert.DataType;
import com.zondy.mapgis.filedialog.FolderType;
import com.zondy.mapgis.filedialog.GDBSelectFolderDialog;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author CR
 * @file DestUnifiedSetting.java
 * @brief 统一设置目标类型、名称、目录
 * @create 2020-03-26.
 */
public class UnificationDialog extends Dialog {
    private static String destMapGISFolder = "";
    private static String destFileFolder = "";

    public UnificationDialog(ObservableList<ConvertItem> selectItems) {
        this.setTitle("统改目的数据");
        VBox vBox = new VBox(6);

        //region 数据类型
        ObservableList<DataType> types = FXCollections.observableArrayList(DataType.MAPGIS_SFCLS, DataType.MAPGIS_ACLS, DataType.MAPGIS_OCLS, DataType.MAPGIS_FDS, DataType.MAPGIS_RAS, DataType.MAPGIS_RCAT, DataType.MAPGIS_GDB,
                DataType.MAPGIS_6X_FILE, DataType.VECTOR_SHP, DataType.ARCGIS_FILEGDB

                //DataType.VECTOR_MIF, DataType.VECTOR_DXF, DataType.VECTOR_VCT, DataType.VECTOR_GML, DataType.VECTOR_DGN, DataType.VECTOR_KML, DataType.VECTOR_DWG, DataType.VECTOR_JSON, DataType.TXT,
                //DataType.TABLE_6X, DataType.TABLE_EXCEL, DataType.TABLE_ACCESS, DataType.TABLE_DBF, DataType.RASTER_6XDEM,
                //DataType.RASTER_MSI, DataType.RASTER_TIFF, DataType.RASTER_IMG, DataType.RASTER_BMP, DataType.RASTER_JPG, DataType.RASTER_GIF, DataType.RASTER_JP2, DataType.RASTER_PNG, DataType.RASTER_HDF5,
                //DataType.DEM_ADF, DataType.DEM_GRD, DataType.DEM_BIL
        );
        if (XFunctions.isSystemWindows()) {
            types.addAll(DataType.VECTOR_E00, DataType.ARCGIS_PERSONALGDB);
        }
        ZDComboBox<DataType> comboType = new ZDComboBox<>(types);
        comboType.setConverter(new StringConverter<DataType>() {
            @Override
            public String toString(DataType object) {
                return object.getText();
            }

            @Override
            public DataType fromString(String string) {
                return EnumUtils.valueOfText(DataType.class, string);
            }
        });
        comboType.setCellFactory(param -> new ListCell<DataType>() {
            @Override
            protected void updateItem(DataType item, boolean empty) {
                super.updateItem(item, empty);
                String text = null;
                ImageView imageView = null;
                if (!empty) {
                    text = item.getText();
                    Image image = item.getImage();
                    if (image != null) {
                        imageView = new ImageView(image);
                    }
                }
                setText(text);
                setGraphic(imageView);
            }
        });
        comboType.setButtonCell(new ListCell<DataType>() {
            @Override
            protected void updateItem(DataType item, boolean empty) {
                super.updateItem(item, empty);
                String text = null;
                ImageView imageView = null;
                if (!empty) {
                    text = item.getText();
                    Image image = item.getImage();
                    if (image != null) {
                        imageView = new ImageView(image);
                    }
                }
                setText(text);
                setGraphic(imageView);
            }
        });
        CheckBox checkType = new CheckBox("统改目的数据类型:");
        comboType.disableProperty().bind(checkType.selectedProperty().not());
        comboType.setValue(DataType.MAPGIS_SFCLS);
        //HBox hBoxDest = new HBox(6, checkType, comboType);
        //hBoxDest.setPadding(new Insets(0, 6, 0, 12));
        //hBoxDest.setAlignment(Pos.CENTER_LEFT);
        //HBox.setHgrow(checkType,Priority.ALWAYS);
        GridPane gridPane = new GridPane();
        gridPane.setHgap(6);
        gridPane.setVgap(6);
        gridPane.add(checkType, 0, 0);
        gridPane.add(comboType, 1, 0);
        TextField tf = new TextField();
        gridPane.add(tf, 1, 0);
        tf.setVisible(false);
        comboType.prefWidthProperty().bind(tf.widthProperty());
        gridPane.setPadding(new Insets(0, 6, 0, 12));

        GridPane.setHgrow(comboType, Priority.ALWAYS);
        vBox.getChildren().addAll(new TitleSeparator("目的数据类型", true), gridPane);
        //endregion

        //region 数据名称-追加模式
        CheckBox checkAppend = new CheckBox("追加模式下统改数据名:");
        TextField textAppend = new TextField();
        textAppend.disableProperty().bind(checkAppend.selectedProperty().not());
        HBox hBoxAppend = new HBox(6, checkAppend, textAppend);
        hBoxAppend.setPadding(new Insets(0, 6, 0, 12));
        hBoxAppend.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textAppend, Priority.ALWAYS);

        boolean canSetAppendMode = false;
        boolean canSetAppendName = false;
        List<DataType> destTypes = new ArrayList<>();
        List<MyGeom> geoms = new ArrayList<>();
        for (ConvertItem item : selectItems) {
            destTypes.add(item.getDestType());

            if (item.getDestType() == DataType.MAPGIS_SFCLS) {
                MyGeom geom = MyGeom.UnKnown;
                if (item != null) {
                    switch (item.getSourType()) {
                        case MAPGIS_SFCLSP:
                        case MAPGIS_6X_WT:
                            geom = MyGeom.MapGIS_Pnt;
                            break;
                        case MAPGIS_SFCLSL:
                        case MAPGIS_6X_WL:
                            geom = MyGeom.MapGIS_Lin;
                            break;
                        case MAPGIS_SFCLSR:
                        case MAPGIS_6X_WP:
                            geom = MyGeom.MapGIS_Reg;
                            break;
                        case VECTOR_MIF:
                        case VECTOR_KML:
                            geom = MyGeom.OtherFile;
                            break;
                        case TXT:
                            boolean genLine = false;//生成线标记
                            //未封装(Txt27xParam)
                            //if (item.getConvertOption().getTxt27xParam() != null)
                            //{
                            //    genLine = (item.getConvertOption().getTxt27xParam().genLineStatus == 1);
                            //}
                            geom = genLine ? MyGeom.MapGIS_Lin : MyGeom.MapGIS_Pnt;
                            break;
                    }
                }
                geoms.add(geom);
            }
        }

        boolean rtn = true;
        for (DataType dt : destTypes) {
            if (dt != DataType.MAPGIS_SFCLS && dt != DataType.MAPGIS_ACLS && dt != DataType.MAPGIS_OCLS && dt != DataType.VECTOR_DXF && dt != DataType.VECTOR_DWG) {
                rtn = false;
                break;
            }
        }
        if (rtn) {
            canSetAppendMode = true;
            if (geoms.size() > 0) {
                boolean allSame = true;
                MyGeom myGeom = geoms.get(0);
                for (MyGeom tempMygeon : geoms) {
                    if (tempMygeon != myGeom) {
                        allSame = false;
                        break;
                    }
                }
                canSetAppendName = allSame;
            }
        }
        checkAppend.setDisable(!canSetAppendMode);
        //endregion

        //region 数据名称—前后缀
        GridPane gridPaneName = new GridPane();
        gridPaneName.setHgap(6);
        gridPaneName.setVgap(6);
        CheckBox checkPrefix = new CheckBox("统加前缀:");
        TextField textPrefix = new TextField("F_");
        textPrefix.disableProperty().bind(checkPrefix.selectedProperty().not());
        CheckBox checkPostfix = new CheckBox("统加后缀:");
        TextField textPostfix = new TextField("_1");
        textPostfix.disableProperty().bind(checkPostfix.selectedProperty().not());
        gridPaneName.add(checkPrefix, 0, 0);
        gridPaneName.add(textPrefix, 1, 0);
        gridPaneName.add(checkPostfix, 0, 1);
        gridPaneName.add(textPostfix, 1, 1);
        //endregion

        //region 数据名称—去除字符
        CheckBox checkStart = new CheckBox("去除前字符数:");
        TextField textStart = UIFunctions.newIntTextField(1, true);
        textStart.disableProperty().bind(checkStart.selectedProperty().not());
        CheckBox checkEnd = new CheckBox("去除后字符数:");
        TextField textEnd = UIFunctions.newIntTextField(1, true);
        textEnd.disableProperty().bind(checkEnd.selectedProperty().not());
        gridPaneName.add(checkStart, 2, 0);
        gridPaneName.add(textStart, 3, 0);
        gridPaneName.add(checkEnd, 2, 1);
        gridPaneName.add(textEnd, 3, 1);
        GridPane.setHgrow(textPrefix, Priority.ALWAYS);
        gridPaneName.setPadding(new Insets(0, 6, 0, 12));

        ColumnConstraints cc = new ColumnConstraints(150);
        cc.setHalignment(HPos.RIGHT);
        gridPaneName.getColumnConstraints().addAll(new ColumnConstraints(), new ColumnConstraints(), cc, new ColumnConstraints(80));
        vBox.getChildren().addAll(new TitleSeparator("目的数据名称"), new VBox(6, hBoxAppend, gridPaneName));
        //endregion

        //region 数据目录
        CheckBox checkMapGISDir = new CheckBox("统改MapGIS目录:");
        ButtonEdit beMapGISDir = new ButtonEdit();
        beMapGISDir.disableProperty().bind(checkMapGISDir.selectedProperty().not());
        CheckBox checkFileDir = new CheckBox("统改文件目录:");
        ButtonEdit beFileDir = new ButtonEdit();
        beFileDir.disableProperty().bind(checkFileDir.selectedProperty().not());
        GridPane gridPaneDir = new GridPane();
        gridPaneDir.setHgap(6);
        gridPaneDir.setVgap(6);
        gridPaneDir.add(checkMapGISDir, 0, 0);
        gridPaneDir.add(beMapGISDir, 1, 0);
        gridPaneDir.add(checkFileDir, 0, 1);
        gridPaneDir.add(beFileDir, 1, 1);
        GridPane.setHgrow(beMapGISDir, Priority.ALWAYS);
        gridPaneDir.setPadding(new Insets(0, 6, 0, 12));
        vBox.getChildren().addAll(new TitleSeparator("目的数据目录"), gridPaneDir);

        beMapGISDir.setOnButtonClick(event ->
        {
            GDBSelectFolderDialog dlg = new GDBSelectFolderDialog();
            dlg.setFolderType(FolderType.MapGIS_DataBase | FolderType.MapGIS_Fds);
            Optional<String[]> optional = dlg.showAndWait();
            if (optional != null && optional.isPresent()) {
                String[] paths = optional.get();
                if (paths != null && paths.length > 0) {
                    beMapGISDir.setText(paths[0]);
                }
            }
        });
        beFileDir.setOnButtonClick(event ->
        {
            DirectoryChooser dc = new DirectoryChooser();
            File file = dc.showDialog(this.getCurrentWindow());
            if (file != null) {
                beFileDir.setText(file.getPath());
            }
        });
        //endregion

        //region 其他
        CheckBox checkThreshold = new CheckBox("统改批量读写要素数目阈值:");
        TextField textThreshold = UIFunctions.newIntTextField(2000, true);
        textThreshold.disableProperty().bind(checkThreshold.selectedProperty().not());
        HBox hBoxOthers = new HBox(6, checkThreshold, textThreshold);
        hBoxOthers.setPadding(new Insets(0, 6, 0, 12));
        hBoxOthers.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textThreshold, Priority.ALWAYS);
        vBox.getChildren().addAll(new TitleSeparator("其他"), hBoxOthers);
        //endregion

        DialogPane dialogPane = this.getDialogPane();
        dialogPane.setContent(vBox);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button button = (Button) dialogPane.lookupButton(ButtonType.OK);
        button.setOnAction(event ->
        {
            if (checkMapGISDir.isSelected()) {
                destMapGISFolder = beMapGISDir.getText();
            }
            if (checkFileDir.isSelected()) {
                destFileFolder = beFileDir.getText();
            }
            if (checkType.isSelected() || checkAppend.isSelected() || checkPrefix.isSelected() || checkPostfix.isSelected() || checkStart.isSelected() || checkEnd.isSelected() || checkMapGISDir.isSelected() || checkFileDir.isSelected() || checkThreshold.isSelected()) {
                for (ConvertItem item : selectItems) {
                    ConvertOption option = item.getConvertOption();
                    if (checkType.isSelected()) {
                        DataType selType = comboType.getValue();
                        if (item.getDestTypes().contains(selType)) {
                            item.setDestType(selType);
                        }
                    }

                    if (!checkAppend.isDisable() && checkAppend.isSelected()) {
                        option.setAppendMode(true);
                    }
                    if (checkPrefix.isSelected() || checkPostfix.isSelected() || checkStart.isSelected() || checkEnd.isSelected()) {
                        if (!item.getDestType().equals(DataType.MAPGIS_GDB)) {
                            String destName = item.getDestName();
                            if (!textAppend.isDisable() && !XString.isNullOrEmpty(textAppend.getText())) {
                                destName = textAppend.getText();
                            }
                            if (!XString.isNullOrEmpty(destName)) {
                                if (checkStart.isSelected() && !XString.isNullOrEmpty(destName)) {
                                    int removeCount = Integer.valueOf(textStart.getText().trim());
                                    if (removeCount > destName.length()) {
                                        removeCount = destName.length();
                                    }
                                    destName = destName.substring(removeCount);
                                }
                                if (checkEnd.isSelected() && !XString.isNullOrEmpty(destName)) {
                                    int removeCount = Integer.valueOf(textEnd.getText().trim());
                                    if (removeCount >= destName.length()) {
                                        destName = "";
                                    } else {
                                        destName = destName.substring(0, destName.length() - removeCount);
                                    }
                                }
                                if (checkPrefix.isSelected()) {
                                    destName = textPrefix.getText() + destName;
                                }
                                if (checkPostfix.isSelected()) {
                                    destName = destName + textPostfix.getText();
                                }
                                item.setDestName(destName);
                            }
                        }
                    } else {
                        String destName = null;
                        if (!textAppend.isDisable() && !XString.isNullOrEmpty(textAppend.getText())) {
                            destName = textAppend.getText();
                        }
                        if (destName != null) {
                            item.setDestName(destName);
                        }
                    }

                    if (checkMapGISDir.isSelected()) {
                        DataType destType = item.getDestType();
                        String dirText = beMapGISDir.getText();
                        switch (destType) {
                            case MAPGIS_SFCLS:
                            case MAPGIS_ACLS:
                            case MAPGIS_OCLS:
                            case MAPGIS_RAS:
                            case MAPGIS_RCAT:
                            case MAPGIS_GDB:
                                item.setDestPath(dirText);
                                break;
                        }
                    }

                    if (checkFileDir.isSelected()) {
                        DataType destType = item.getDestType();
                        String dirText = beFileDir.getText();
                        switch (destType) {
                            case MAPGIS_SFCLS:
                            case MAPGIS_ACLS:
                            case MAPGIS_OCLS:
                            case MAPGIS_RAS:
                            case MAPGIS_RCAT:
                            case MAPGIS_FDS:
                            case MAPGIS_GDB:
                                break;
                            case TABLE_6X:
                            case TABLE_EXCEL:
                            case TABLE_ACCESS:
                            case TABLE_DBF:
                                //case DataType.TABLE_TXT:
                                item.setDestPath(CustomOperate.tableProName + dirText);
                                break;
                            default:
                                item.setDestPath(dirText);
                                break;
                        }
                    }

                    if (checkThreshold.isSelected()) {
                        option.setBatRWNum(Integer.valueOf(textThreshold.getText()));
                    }
                }
            }
        });

        //Linux下面部分ComboBox刚启动时宽度没调整好，重绘一次
        if (!XFunctions.isSystemWindows()) {
            Platform.runLater(() -> comboType.requestLayout());
        }
    }

    public static String getDestMapGISFolder() {
        return destMapGISFolder;
    }

    public static String getDestFileFolder() {
        return destFileFolder;
    }

    //region 获取Window
    private javafx.stage.Window window;

    /**
     * 获取当前窗口的window对象
     *
     * @return 当前窗口的window对象
     */
    private javafx.stage.Window getCurrentWindow() {
        if (this.window == null) {
            this.window = this.getDialogPane().getScene().getWindow();
        }
        return this.window;
    }
    //endregion

    //几何类型枚举
    enum MyGeom {
        UnKnown,
        MapGIS_Pnt,
        MapGIS_Lin,
        MapGIS_Reg,
        OtherFile,
    }
}
