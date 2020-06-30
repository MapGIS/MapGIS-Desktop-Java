package com.zondy.mapgis.sref;

import com.zondy.mapgis.base.*;
import com.zondy.mapgis.controls.common.ZDComboBox;
import com.zondy.mapgis.geodatabase.DataBase;
import com.zondy.mapgis.srs.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import org.dom4j.Node;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author CR
 * @file PrjSRefDialog.java
 * @brief 投影坐标系对话框界面
 * @create 2020-02-04.
 */
public class PrjSRefDialog extends Dialog {
    //region 控件定义
    private final TextField textFieldName = new TextField();//名称
    private final ZDComboBox<SRefPrjType> comboBoxType = new ZDComboBox<>();//投影类型
    private final Label labelCenterLong = new Label("中央经线:");
    private final TextField textFieldCenterLong = UIFunctions.newDecimalTextField(0);
    private final Label labelOrigLat = new Label("原点纬度:");
    private final TextField textFieldOrigLat = UIFunctions.newDecimalTextField(0);
    private final Label labelFLat = new Label("第一纬度:");
    private final TextField textFieldFLat = UIFunctions.newDecimalTextField(0);
    private final Label labelSLat = new Label("第二纬度:");
    private final TextField textFieldSLat = UIFunctions.newDecimalTextField(0);
    private final Label labelNoneLat = new Label("无变形纬度:");
    private final TextField textFieldNoneLat = UIFunctions.newDecimalTextField(0);
    private final Label labelCenterLat = new Label("中心点纬度:");
    private final TextField textFieldCenterLat = UIFunctions.newDecimalTextField(0);
    private final Label labelScale = new Label("比例因子:");
    private final TextField textFieldScale = UIFunctions.newDecimalTextField(1);
    private final Label labelFLong = new Label("第一经度:");
    private final TextField textFieldFLong = UIFunctions.newDecimalTextField(0);
    private final Label labelSLong = new Label("第二经度:");
    private final TextField textFieldSLong = UIFunctions.newDecimalTextField(0);
    private final Label labelDis = new Label("透视点距离:");
    private final TextField textFieldDis = UIFunctions.newDecimalTextField();
    private final Label labelYLong = new Label("Y轴经度:");
    private final TextField textFieldYLong = UIFunctions.newDecimalTextField(0);
    private final Label labelEast = new Label("投影东偏:");
    private final TextField textFieldEast = UIFunctions.newDecimalTextField(0);
    private final Label labelNorth = new Label("投影北偏:");
    private final TextField textFieldNorth = UIFunctions.newDecimalTextField(0);
    private final TextField textFieldHorScale = UIFunctions.newDecimalTextField(1);
    private final ZDComboBox<SRefLenUnit> comboBoxUnit = new ZDComboBox<>();
    private final TextField textFieldHorOff = UIFunctions.newDecimalTextField(0);
    private final TextField textFieldVerOff = UIFunctions.newDecimalTextField(0);
    //endregion

    private GeoSRefPane geoSRefPane;
    private SRefData sRefData;   //当前参照系对象
    private SRefData geoSRef;//当前投影参照系的地理坐标系信息
    private SRefZoneType zoneType = SRefZoneType.Degree6;
    private short zone = 1;
    private Tooltip tooltipError = new Tooltip();

    public PrjSRefDialog() {
        this(null);
    }

    public PrjSRefDialog(SRefData sref) {
        this(sref, false);
    }

    /**
     * 构造投影参照系的界面
     *
     * @param sref      待修改的参照系
     * @param frameSRef 是否是图框参照系（图框参照系界面上只有名称能修改）
     */
    public PrjSRefDialog(SRefData sref, boolean frameSRef) {
        this.setTitle(sref != null ? "修改投影坐标系" : "新建投影坐标系");
        this.sRefData = sref;

        //region 名称
        //名称不能包括特殊字符
        this.textFieldName.textProperty().addListener((o, ov, nv) ->
        {
            StringProperty errorMsg = new SimpleStringProperty();
            if (!XString.isTextValid(nv, 96, GISDefaultValues.getInvalidNameCharList(), errorMsg)) {
                UIFunctions.showErrorTip(this.textFieldName, errorMsg.get(), this.tooltipError);
                this.textFieldName.setText(ov);
            }
        });

        GridPane gridPaneName = new GridPane();
        gridPaneName.setHgap(6);
        gridPaneName.setVgap(6);
        gridPaneName.add(new Label("名称:"), 0, 0);
        gridPaneName.add(this.textFieldName, 1, 0);
        GridPane.setHgrow(this.textFieldName, Priority.ALWAYS);
        //endregion

        //region 地理坐标系
        this.geoSRefPane = new GeoSRefPane(sref);
        this.geoSRefPane.setPadding(new Insets(12, 12, 0, 12));
        Button buttonImport = new Button("从参照系导入...");
        buttonImport.setOnAction(event ->
        {
            //TODO: 选择参照系导入地理坐标系部分
            SRefManagerDialog dlg = new SRefManagerDialog();
            if (dlg.showAndWait().equals(Optional.of(ButtonType.OK))) {
                SRefData sRef = dlg.getSelectedSRef();
                if (sRef != null && !XString.isNullOrEmpty(sRef.getGCSName())) {
                    this.geoSRef = sRef;
                    if (!XString.isNullOrEmpty(sRef.getGCSName())) {
                        this.geoSRef.setSRSName(sRef.getGCSName());
                    }
                    this.geoSRef.setType(SRefType.JWD);
                }
            }
        });
        VBox vBox = new VBox(6, this.geoSRefPane, buttonImport);
        vBox.setMargin(buttonImport, new Insets(0, 0, 0, 89));
        Tab tabGeo = new Tab("地理坐标系", vBox);
        tabGeo.setClosable(false);
        //endregion

        //region 投影参数
        GridPane gridPanePrj = new GridPane();
        gridPanePrj.setHgap(6);

        //投影类型
        for (SRefPrjType type : SRefPrjType.values()) {
            if (!SRefPrjType.LonLat.equals(type)) {
                this.comboBoxType.getItems().add(type);
            }
        }
        this.comboBoxType.getItems().add(SRefPrjType.LonLat);
        this.comboBoxType.setConverter(new StringConverter<SRefPrjType>() {
            @Override
            public String toString(SRefPrjType object) {
                return LanguageConvert.sRefProjTypeConvertEx(object);
            }

            @Override
            public SRefPrjType fromString(String string) {
                return LanguageConvert.sRefProjTypeConvert(string);
            }
        });
        //根据选择的投影类型修改参数设置界面
        this.comboBoxType.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) ->
        {
            //region 调整界面控件可见性
            boolean needCenterLong = !(SRefPrjType.Mercator_Oblique.equals(nv) || SRefPrjType.Polar_Srereographic.equals(nv));
            this.labelCenterLong.setManaged(needCenterLong);
            this.textFieldCenterLong.setManaged(needCenterLong);
            this.labelCenterLong.setVisible(needCenterLong);
            this.textFieldCenterLong.setVisible(needCenterLong);

            boolean needOrigLat = Arrays.asList(SRefPrjType.UTM, SRefPrjType.Albers_CEQA, SRefPrjType.Lambert_CC, SRefPrjType.GaussKruger, SRefPrjType.Polyconic, SRefPrjType.EQ_DC, SRefPrjType.Mercator_Transverse, SRefPrjType.Mercator_Oblique, SRefPrjType.Bonne_Ellipsolid, SRefPrjType.Bonne_Sphere, SRefPrjType.Winkel_II).contains(nv);
            this.labelOrigLat.setManaged(needOrigLat);
            this.textFieldOrigLat.setManaged(needOrigLat);
            this.labelOrigLat.setVisible(needOrigLat);
            this.textFieldOrigLat.setVisible(needOrigLat);

            boolean needFLat = Arrays.asList(SRefPrjType.Albers_CEQA, SRefPrjType.Lambert_CC, SRefPrjType.EQ_DC, SRefPrjType.Mercator_Oblique).contains(nv);
            this.labelFLat.setManaged(needFLat);
            this.textFieldFLat.setManaged(needFLat);
            this.labelSLat.setManaged(needFLat);
            this.textFieldSLat.setManaged(needFLat);
            this.labelFLat.setVisible(needFLat);
            this.textFieldFLat.setVisible(needFLat);
            this.labelSLat.setVisible(needFLat);
            this.textFieldSLat.setVisible(needFLat);

            boolean needNoneLat = Arrays.asList(SRefPrjType.Mercator, SRefPrjType.Equirectangular, SRefPrjType.Polar_Srereographic).contains(nv);
            this.labelNoneLat.setManaged(needNoneLat);
            this.textFieldNoneLat.setManaged(needNoneLat);
            this.labelNoneLat.setVisible(needNoneLat);
            this.textFieldNoneLat.setVisible(needNoneLat);

            boolean needCenterLat = Arrays.asList(SRefPrjType.StereoGraphic, SRefPrjType.Lambert_AEQA, SRefPrjType.Azimuthal_EQD, SRefPrjType.Gnomonic, SRefPrjType.Orthographic, SRefPrjType.General_VNSP).contains(nv);
            this.labelCenterLat.setManaged(needCenterLat);
            this.textFieldCenterLat.setManaged(needCenterLat);
            this.labelCenterLat.setVisible(needCenterLat);
            this.textFieldCenterLat.setVisible(needCenterLat);

            boolean needScale = Arrays.asList(SRefPrjType.Lambert_CC, SRefPrjType.Mercator_Transverse, SRefPrjType.Mercator_Oblique).contains(nv);
            this.labelScale.setManaged(needScale);
            this.textFieldScale.setManaged(needScale);
            this.labelScale.setVisible(needScale);
            this.textFieldScale.setVisible(needScale);

            boolean needFLong = SRefPrjType.Mercator_Oblique.equals(nv);
            this.labelFLong.setManaged(needFLong);
            this.textFieldFLong.setManaged(needFLong);
            this.labelSLong.setManaged(needFLong);
            this.textFieldSLong.setManaged(needFLong);
            this.labelFLong.setVisible(needFLong);
            this.textFieldFLong.setVisible(needFLong);
            this.labelSLong.setVisible(needFLong);
            this.textFieldSLong.setVisible(needFLong);

            boolean needDis = SRefPrjType.General_VNSP.equals(nv);
            this.labelDis.setManaged(needDis);
            this.textFieldDis.setManaged(needDis);
            this.labelDis.setVisible(needDis);
            this.textFieldDis.setVisible(needDis);

            boolean needYLong = SRefPrjType.Polar_Srereographic.equals(nv);
            this.labelYLong.setManaged(needYLong);
            this.textFieldYLong.setManaged(needYLong);
            this.labelYLong.setVisible(needYLong);
            this.textFieldYLong.setVisible(needYLong);

            boolean needOff = !SRefPrjType.Web_Mercator.equals(nv);
            this.labelEast.setManaged(needOff);
            this.textFieldEast.setManaged(needOff);
            this.labelNorth.setManaged(needOff);
            this.textFieldNorth.setManaged(needOff);
            this.labelEast.setVisible(needOff);
            this.textFieldEast.setVisible(needOff);
            this.labelNorth.setVisible(needOff);
            this.textFieldNorth.setVisible(needOff);
            //endregion

            this.textFieldHorScale.setEditable(!SRefPrjType.Web_Mercator.equals(nv));//WEB墨卡托投影
            if (SRefPrjType.Web_Mercator.equals(nv)) {
                this.textFieldHorScale.setText("1");
            }

            double midLon = PrjSRefDialog.calcMeridianLongitude(nv, this.zoneType, this.zone);
            this.textFieldFLong.setText(String.valueOf(midLon));
            this.textFieldCenterLong.setText(String.valueOf(midLon));
            this.textFieldYLong.setText(String.valueOf(midLon));
        });

        gridPanePrj.add(new Label("投影类型:    "), 0, 0);

        //region 投影类型的参数
        gridPanePrj.add(this.labelCenterLong, 0, 1);
        gridPanePrj.add(this.textFieldCenterLong, 1, 1);
        gridPanePrj.add(this.labelOrigLat, 0, 2);
        gridPanePrj.add(this.textFieldOrigLat, 1, 2);
        gridPanePrj.add(this.labelFLat, 0, 3);
        gridPanePrj.add(this.textFieldFLat, 1, 3);
        gridPanePrj.add(this.labelSLat, 0, 4);
        gridPanePrj.add(this.textFieldSLat, 1, 4);
        gridPanePrj.add(this.labelNoneLat, 0, 5);
        gridPanePrj.add(this.textFieldNoneLat, 1, 5);
        gridPanePrj.add(this.labelCenterLat, 0, 6);
        gridPanePrj.add(this.textFieldCenterLat, 1, 6);
        gridPanePrj.add(this.labelFLong, 0, 7);
        gridPanePrj.add(this.textFieldFLong, 1, 7);
        gridPanePrj.add(this.labelSLong, 0, 8);
        gridPanePrj.add(this.textFieldSLong, 1, 8);
        gridPanePrj.add(this.labelScale, 0, 9);
        gridPanePrj.add(this.textFieldScale, 1, 9);
        gridPanePrj.add(this.labelDis, 0, 10);
        gridPanePrj.add(this.textFieldDis, 1, 10);
        gridPanePrj.add(this.labelYLong, 0, 11);
        gridPanePrj.add(this.textFieldYLong, 1, 11);
        gridPanePrj.add(this.labelEast, 0, 12);
        gridPanePrj.add(this.textFieldEast, 1, 12);
        gridPanePrj.add(this.labelNorth, 0, 13);
        gridPanePrj.add(this.textFieldNorth, 1, 13);
        gridPanePrj.add(this.comboBoxType, 1, 0);//ComboBox需要放在其bind的对象后面添加，否则，在linux下面首次弹出来长度不对。
        GridPane.setMargin(this.comboBoxType, new Insets(0, 0, 3, 0));
        GridPane.setMargin(this.textFieldCenterLong, new Insets(3, 0, 3, 0));
        GridPane.setMargin(this.textFieldOrigLat, new Insets(3, 0, 3, 0));
        GridPane.setMargin(this.textFieldFLat, new Insets(3, 0, 3, 0));
        GridPane.setMargin(this.textFieldSLat, new Insets(3, 0, 3, 0));
        GridPane.setMargin(this.textFieldNoneLat, new Insets(3, 0, 3, 0));
        GridPane.setMargin(this.textFieldCenterLat, new Insets(3, 0, 3, 0));
        GridPane.setMargin(this.textFieldScale, new Insets(3, 0, 3, 0));
        GridPane.setMargin(this.textFieldFLong, new Insets(3, 0, 3, 0));
        GridPane.setMargin(this.textFieldSLong, new Insets(3, 0, 3, 0));
        GridPane.setMargin(this.textFieldDis, new Insets(3, 0, 3, 0));
        GridPane.setMargin(this.textFieldYLong, new Insets(3, 0, 3, 0));
        GridPane.setMargin(this.textFieldEast, new Insets(3, 0, 3, 0));
        GridPane.setMargin(this.textFieldNorth, new Insets(3, 0, 3, 0));
        gridPanePrj.setPadding(new Insets(12, 0, 12, 12));
        GridPane.setHgrow(this.textFieldCenterLong, Priority.ALWAYS);
        comboBoxType.prefWidthProperty().bind(this.textFieldCenterLong.widthProperty());
        //endregion

        //region 单位等参数
        this.comboBoxUnit.getItems().addAll(
                SRefLenUnit.KiloMeter,//公里
                SRefLenUnit.Meter,//米
                SRefLenUnit.DeciMeter,//分米
                SRefLenUnit.CentiMeter,//厘米
                SRefLenUnit.MilliMeter,//毫米
                SRefLenUnit.Foot,//英尺
                SRefLenUnit.Inch,//英寸
                SRefLenUnit.Yard,//码
                SRefLenUnit.SeaMile,//海里
                SRefLenUnit.Mile);//英里
        this.comboBoxUnit.setConverter(new StringConverter<SRefLenUnit>() {
            @Override
            public String toString(SRefLenUnit object) {
                return LanguageConvert.sRefLenUnitConvert(object);
            }

            @Override
            public SRefLenUnit fromString(String string) {
                return LanguageConvert.sRefLenUnitConvert(string);
            }
        });
        //切换角度单位时，自动计算“米/单位”

        GridPane gridPaneUnit = new GridPane();
        gridPaneUnit.setHgap(6);
        gridPaneUnit.setVgap(6);
        gridPaneUnit.add(new Label("水平比例尺:"), 2, 0);
        gridPaneUnit.add(this.textFieldHorScale, 3, 0);
        gridPaneUnit.add(new Label("长度单位:"), 2, 1);
        gridPaneUnit.add(this.comboBoxUnit, 3, 1);
        gridPaneUnit.add(new Label("水平平移:"), 2, 2);
        gridPaneUnit.add(this.textFieldHorOff, 3, 2);
        gridPaneUnit.add(new Label("垂直平移:"), 2, 3);
        gridPaneUnit.add(this.textFieldVerOff, 3, 3);
        gridPaneUnit.setPadding(new Insets(12, 12, 12, 0));
        this.textFieldHorScale.setPrefWidth(90);
        this.textFieldHorScale.setMaxWidth(90);
        this.comboBoxUnit.prefWidthProperty().bind(this.textFieldHorScale.widthProperty());
        this.textFieldHorOff.prefWidthProperty().bind(this.textFieldHorScale.widthProperty());
        this.textFieldVerOff.prefWidthProperty().bind(this.textFieldHorScale.widthProperty());
        //endregion

        Tab tabPrj = new Tab("投影参数", new HBox(12, gridPanePrj, gridPaneUnit));
        HBox.setHgrow(gridPanePrj, Priority.ALWAYS);
        tabPrj.setClosable(false);
        //endregion

        //region 初始化
        if (this.sRefData == null) {
            this.comboBoxType.getSelectionModel().select(0);
            this.comboBoxUnit.getSelectionModel().select(SRefLenUnit.MilliMeter);//毫米
            double midLon = PrjSRefDialog.calcMeridianLongitude(SRefPrjType.UTM, this.zoneType, this.zone);
            this.textFieldFLong.setText(String.valueOf(midLon));
            this.textFieldCenterLong.setText(String.valueOf(midLon));
            this.textFieldYLong.setText(String.valueOf(midLon));
        } else {
            this.geoSRef = this.sRefData.clone();
            this.geoSRef.setType(SRefType.JWD);
            this.geoSRef.setSRSName(this.sRefData.getGCSName());//用于修改！
            this.geoSRefPane.initGeoInfo(this.geoSRef);

            if (frameSRef) {
                this.textFieldHorScale.setEditable(false);
                this.comboBoxUnit.setDisable(true);
                this.textFieldHorOff.setEditable(false);
                this.textFieldVerOff.setEditable(false);
            }

            this.textFieldName.setText(this.sRefData.getSRSName());
            if (SRefPrjType.LonLat.equals(this.sRefData.getProjType()))//自定义
            {
                this.comboBoxType.getSelectionModel().select(this.comboBoxType.getItems().size() - 1);
            } else {
                this.comboBoxType.getSelectionModel().select(this.sRefData.getProjType());
            }

            //接口删除，暂时屏蔽代码
            //this.zoneType = this.sRefData.getZoneType();
            //this.zone = this.sRefData.getZone();
            SRefPrjType prjType = this.sRefData.getProjType();

            //region 显示参数
            this.textFieldEast.setText(String.valueOf(this.sRefData.getFalseEasting()));
            this.textFieldNorth.setText(String.valueOf(this.sRefData.getFalseNorthing()));
            double lon = this.sRefData.getCentralMeridian();
            this.textFieldCenterLong.setText(String.valueOf(lon));
            this.textFieldYLong.setText(String.valueOf(lon));
            if (SRefPrjType.Mercator_Oblique.equals(prjType)) {
                this.textFieldScale.setText(String.valueOf(lon));
            } else {
                this.textFieldScale.setText(String.valueOf(this.sRefData.getScaleFactor()));
            }
            double lat = this.sRefData.getLatitudeOfOrigin();
            this.textFieldCenterLat.setText(String.valueOf(lat));
            this.textFieldOrigLat.setText(String.valueOf(lat));
            this.textFieldNoneLat.setText(String.valueOf(lat));
            this.textFieldFLong.setText(String.valueOf(this.sRefData.getLongitudeOf1st()));
            this.textFieldSLong.setText(String.valueOf(this.sRefData.getLongitudeOf2nd()));
            double lat1 = this.sRefData.getStandardParallel1();
            this.textFieldFLat.setText(String.valueOf(lat1));
            this.textFieldDis.setText(String.valueOf(lat1));
            this.textFieldSLat.setText(String.valueOf(this.sRefData.getStandardParallel2()));
            double midLon = PrjSRefDialog.calcMeridianLongitude(prjType, this.zoneType, this.zone);
            if ((SRefPrjType.Mercator_Oblique.equals(prjType) && this.sRefData.getLongitudeOf1st() != midLon) || (!SRefPrjType.Mercator_Oblique.equals(prjType) && this.sRefData.getCentralMeridian() != midLon))//19-斜轴墨卡托投影坐标系
            {
                this.zoneType = SRefZoneType.Unknown;
                this.zone = 0;
            }

            this.textFieldHorScale.setText(String.valueOf(this.sRefData.getRate()));
            this.comboBoxUnit.getSelectionModel().select(this.sRefData.getUnit());
            this.textFieldHorOff.setText(String.valueOf(this.sRefData.getX()));
            this.textFieldVerOff.setText(String.valueOf(this.sRefData.getY()));
            //endregion
        }
        //endregion

        TabPane tabPane = new TabPane(tabGeo, tabPrj);
        VBox root = new VBox(6);
        root.getChildren().addAll(gridPaneName, tabPane);
        root.setPadding(new Insets(12, 12, 0, 12));

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setPrefSize(560,510);
        dialogPane.setMinSize(560,510);
        dialogPane.setContent(root);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, this::okButtonClick);
    }

    /**
     * 计算中央精度
     *
     * @param prjType  投影类型
     * @param zoneType 分带类型
     * @param zoneNo   带号
     * @return 中央精度
     */
    public static double calcMeridianLongitude(SRefPrjType prjType, SRefZoneType zoneType, int zoneNo) {
        double mLong = 0;
        switch (zoneType) {
            case Degree6://6度带
            {
                if (SRefPrjType.UTM.equals(prjType))//通用UTM投影
                {
                    mLong = ((int) zoneNo * 6 - 3) * 10000 - 1800000;
                } else {
                    mLong = ((int) zoneNo * 6 - 3) * 10000;
                    if (mLong > 1800000) {
                        mLong = mLong - 3600000;
                    }
                }
                break;
            }
            case Degree3://3度带
            {
                if (SRefPrjType.UTM.equals(prjType))//通用UTM投影
                {
                    mLong = ((int) zoneNo * 3) * 10000 - 1800000;
                } else {
                    mLong = ((int) zoneNo * 3) * 10000;
                    if (zoneNo > 1800000) {
                        mLong = mLong - 3600000;
                    }
                }
                break;
            }
            default:
                break;
        }
        return mLong;
    }

    /**
     * 获取新建（或修改后）的参照系
     *
     * @return
     */
    public SRefData getSpatialReference() {
        if (this.sRefData == null) {
            this.sRefData = new SRefData();
        }

        this.sRefData.setType(SRefType.PRJ);//投影平面直角坐标系
        this.sRefData.setSRSName(this.textFieldName.getText());
        this.sRefData.setProjName(LanguageConvert.sRefProjTypeConvert(this.comboBoxType.getValue()));
        this.sRefData.setProjType(this.comboBoxType.getValue());
        this.sRefData.setPCSName(this.sRefData.getProjName() + "坐标系");

        //region 投影参数
        //接口删除，暂时屏蔽代码
        //this.sRefData.setZoneType(this.zoneType);
        //this.sRefData.setZone(this.zone);
        if (this.textFieldEast.isVisible()) {
            this.sRefData.setFalseEasting(Double.parseDouble(this.textFieldEast.getText()));
            this.sRefData.setFalseNorthing(Double.parseDouble(this.textFieldNorth.getText()));
        }
        if (this.textFieldCenterLong.isVisible()) {
            this.sRefData.setCentralMeridian(Double.valueOf(this.textFieldCenterLong.getText()));
        }
        if (this.textFieldOrigLat.isVisible()) {
            this.sRefData.setLatitudeOfOrigin(Double.valueOf(this.textFieldOrigLat.getText()));
        }
        if (this.textFieldNoneLat.isVisible()) {
            this.sRefData.setLatitudeOfOrigin(Double.valueOf(this.textFieldNoneLat.getText()));
        }
        if (this.textFieldFLat.isVisible()) {
            this.sRefData.setStandardParallel1(Double.valueOf(this.textFieldFLat.getText()));
            this.sRefData.setStandardParallel2(Double.valueOf(this.textFieldSLat.getText()));
        }
        if (this.textFieldScale.isVisible()) {
            this.sRefData.setScaleFactor(Double.valueOf(this.textFieldScale.getText()));
        }
        if (this.textFieldCenterLat.isVisible()) {
            this.sRefData.setLatitudeOfOrigin(Double.valueOf(this.textFieldCenterLat.getText()));
        }
        if (this.textFieldDis.isVisible()) {
            this.sRefData.setStandardParallel1(Double.valueOf(this.textFieldDis.getText()));
        }
        if (this.textFieldSLong.isVisible()) {
            this.sRefData.setCentralMeridian(Double.valueOf(this.textFieldScale.getText()));
            this.sRefData.setLongitudeOf1st(Double.valueOf(this.textFieldFLong.getText()));
            this.sRefData.setLongitudeOf2nd(Double.valueOf(this.textFieldSLong.getText()));
        }
        if (this.textFieldYLong.isVisible()) {
            this.sRefData.setCentralMeridian(Double.valueOf(this.textFieldYLong.getText()));
        }
        //endregion

        this.sRefData.setRate(Double.valueOf(this.textFieldHorScale.getText()));
        this.sRefData.setUnit(this.comboBoxUnit.getValue());
        this.sRefData.setUnitFactor(Double.valueOf(SRefData.unitConvert(this.sRefData.getUnit(), SRefLenUnit.Meter)));
        this.sRefData.setX(Double.valueOf(this.textFieldHorOff.getText()));
        this.sRefData.setY(Double.valueOf(this.textFieldVerOff.getText()));

        if (this.geoSRef != null)//地理坐标系
        {
            this.sRefData.setGCSName(this.geoSRef.getGCSName());
            this.sRefData.setSpheroid(this.geoSRef.getSpheroid());
            this.sRefData.setSemiMajorAxis(this.geoSRef.getSemiMajorAxis());
            this.sRefData.setB(this.geoSRef.getSemiMinorAxis());
            this.sRefData.setFlattening(this.geoSRef.getFlattening());
            this.sRefData.setAngUnit(this.geoSRef.getAngUnit());
            this.sRefData.setAngUnitFactor(this.geoSRef.getAngUnitFactor());
            this.sRefData.setPrimeMeridian(this.geoSRef.getPrimeMeridian());
            this.sRefData.setPMOffset(this.geoSRef.getPMOffset());
        }

        return this.sRefData;
    }

    /**
     * 只读时将各控件设为只读
     */
    public void unEditableControls() {
        this.textFieldName.setEditable(false);
        this.comboBoxType.setEditable(false);
        this.textFieldCenterLong.setEditable(false);
        this.textFieldOrigLat.setEditable(false);
        this.textFieldFLat.setEditable(false);
        this.textFieldSLat.setEditable(false);
        this.textFieldNoneLat.setEditable(false);
        this.textFieldCenterLat.setEditable(false);
        this.textFieldScale.setEditable(false);
        this.textFieldFLong.setEditable(false);
        this.textFieldSLong.setEditable(false);
        this.textFieldDis.setEditable(false);
        this.textFieldYLong.setEditable(false);
        this.textFieldEast.setEditable(false);
        this.textFieldNorth.setEditable(false);
        this.textFieldHorScale.setEditable(false);
        this.comboBoxUnit.setEditable(false);
        this.textFieldHorOff.setEditable(false);
        this.textFieldVerOff.setEditable(false);
    }

    private void okButtonClick(ActionEvent event) {
        String errorText = "";
        String srsName = this.textFieldName.getText();
        if (XString.isNullOrEmpty(srsName)) {
            errorText = "投影参照系名称不能为空。";
        } else if (XString.indexOfAny(srsName, GISDefaultValues.getInvalidNameChars()) >= 0) {
            errorText = "参照系名称不能包含下列任何字符之一：\\ / * ? \" \" < > |";
        } else {
            if (this.sRefData == null || !srsName.equals(this.sRefData.getSRSName())) {
                if (SRefManagerDialog.isSrefNameExisted(srsName)) {
                    errorText = "已经存在同名参照系。";
                }
            }
        }

        if (errorText == "" && this.geoSRef == null) {
            errorText = "必须为投影参照系选择地理坐标系。";
        }

        if (errorText != "") {
            MessageBox.information(errorText);
            event.consume();
        }
    }
}
