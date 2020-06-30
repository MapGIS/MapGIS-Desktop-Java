package com.zondy.mapgis.mapeditor.projecttransform;

import com.zondy.mapgis.base.LanguageConvert;
import com.zondy.mapgis.geodatabase.config.EnvConfig;
import com.zondy.mapgis.geodatabase.config.SysConfigDirType;
import com.zondy.mapgis.geometry.*;
import com.zondy.mapgis.sref.SRefInfoPane;
import com.zondy.mapgis.sref.SRefManagerDialog;
import com.zondy.mapgis.srs.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

/**
 * 单点投影对话框
 */
public class SimplePntProjectDialog extends Dialog {
    private SRefLenUnit m_SrcAngUnit = SRefLenUnit.Unknown;//源参照系水平单位
    private SRefLenUnit m_DesAngUnit = SRefLenUnit.Unknown;//目的参照系水平单位
    private Dot3D projectedDot;  //记录投影结果点，用于在度和度分秒单位间切换显示

    /**
     * 构造方法
     */
    public SimplePntProjectDialog() {
        setTitle("单点投影");
        //setHeight(450);
        //setWidth(550);
        //初始化界面
        initialize();


        //绑定事件
        bindAction();
        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setContent(this.gridPane);
        ButtonType okbuttonType = new ButtonType("投影", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(okbuttonType, ButtonType.CANCEL);

        Button okButton = (Button) dialogPane.lookupButton(okbuttonType);
        okButton.addEventFilter(ActionEvent.ACTION, this::okButtonClick);
    }

    private GridPane gridPane = null;
    //原坐标点
    private TextField srcxTextField = null;
    private TextField srcyTextField = null;
    private TextField srczTextField = null;
    private CheckBox srcCheckbox = null;
    //目的坐标点
    private TextField desxTextField = null;
    private TextField desyTextField = null;
    private TextField deszTextField = null;
    private CheckBox desCheckbox = null;
    //源参照系
    private Button srcSRefBtn = null;
    private TextArea srcSRefInfo = null;
    private SRefData srcSRefData = null;

    //目的参照系
    private Button desSRefBtn = null;
    private TextArea desSRefInfo = null;
    private SRefData desSRefData = null;

    //转换方法及参数
    private ComboBox transMethodCombobox = null;
    private Button transParamBtn = null;
    private ArrayList<ElpTransParam> elpTransParamArrayList = new ArrayList<>();
    private ObservableList<String> elpTransParamNameList = FXCollections.observableArrayList();

    /**
     * 初始化界面
     */
    private void initialize() {
        gridPane = new GridPane();
        gridPane.setPrefWidth(600);
        gridPane.setPrefHeight(400);

        //region 原坐标点
        Label srcxLable = new Label("经度/X:");
        Label srcyLable = new Label("维度/Y");
        Label srczLable = new Label("高程/Z");
        srcCheckbox = new CheckBox();
        srcCheckbox.setSelected(false);
        Label srcCheckboxText = new Label("源地理数据为度分秒格式");
        srcxTextField = new TextField();
        srcxTextField.setPrefWidth(200);
        srcyTextField = new TextField();
        srczTextField = new TextField();
        //endregion 原坐标点

        //region 源坐标点布局
        TitledPane srcTPane = new TitledPane();
        srcTPane.setPrefWidth(300);
        srcTPane.setText("源坐标点");
        srcTPane.setCollapsible(false);
        srcTPane.setPadding(new Insets(5, 0, 0, 5));
        GridPane srcPntGrid = new GridPane();
        srcPntGrid.setHgap(5);
        srcPntGrid.setVgap(5);
        srcPntGrid.add(srcCheckbox, 0, 0);
        srcPntGrid.add(srcCheckboxText, 1, 0);
        srcPntGrid.add(srcxLable, 0, 1);
        srcPntGrid.add(srcxTextField, 1, 1);
        srcPntGrid.add(srcyLable, 0, 2);
        srcPntGrid.add(srcyTextField, 1, 2);
        srcPntGrid.add(srczLable, 0, 3);
        srcPntGrid.add(srczTextField, 1, 3);
        srcTPane.setContent(srcPntGrid);
        //endregion

        //region结果坐标点
        Label desxLable = new Label("经度/X:");
        Label desyLable = new Label("维度/Y");
        Label deszLable = new Label("高程/Z");

        desxTextField = new TextField();
        desxTextField.setPrefWidth(200);
        desyTextField = new TextField();
        deszTextField = new TextField();
        //结果文本框不允许编辑
        desxTextField.setEditable(false);
        desyTextField.setEditable(false);
        deszTextField.setEditable(false);
        desCheckbox = new CheckBox();
        desCheckbox.setSelected(false);
        Label desCheckboxText = new Label("将结果显示成度分秒格式");
        //endregion

        //region 结果坐标点布局
        TitledPane desTPane = new TitledPane();
        desTPane.setPrefWidth(300);
        desTPane.setText("结果坐标点");
        desTPane.setCollapsible(false);
        desTPane.setPadding(new Insets(5, 5, 0, 5));
        GridPane desPntGrid = new GridPane();
        desPntGrid.setHgap(5);
        desPntGrid.setVgap(5);
        int desRowIndex = 0;
        desPntGrid.add(desCheckbox, 0, desRowIndex);
        desPntGrid.add(desCheckboxText, 1, desRowIndex);
        desRowIndex++;
        desPntGrid.add(desxLable, 0, desRowIndex);
        desPntGrid.add(desxTextField, 1, desRowIndex);
        desRowIndex++;
        desPntGrid.add(desyLable, 0, desRowIndex);
        desPntGrid.add(desyTextField, 1, desRowIndex);
        desRowIndex++;
        desPntGrid.add(deszLable, 0, desRowIndex);
        desPntGrid.add(deszTextField, 1, desRowIndex);
        desTPane.setContent(desPntGrid);
        //endregion

        //region 源参考系
        TitledPane srcSRefTPane = new TitledPane();
        srcSRefTPane.setText("源坐标系");
        srcSRefTPane.setCollapsible(false);
        srcSRefTPane.setPrefWidth(300);
        srcSRefTPane.setPadding(new Insets(5, 0, 0, 5));
        //设置内容
        GridPane srcSRefGrid = new GridPane();
        srcSRefGrid.setHgap(5);
        srcSRefGrid.setVgap(5);
        Label srcSRefLabel = new Label("选择源坐标系:");
        srcSRefBtn = new Button("...");
        srcSRefInfo = new TextArea();
        srcSRefInfo.setPrefWidth(300);
//        srcSRefInfo.setText(getSrcSrefInfo());
        srcSRefInfo.setEditable(false);
        srcSRefGrid.add(srcSRefLabel, 0, 0);
        srcSRefGrid.add(srcSRefBtn, 1, 0);
        srcSRefGrid.add(srcSRefInfo, 0, 1, 2, 1);
        srcSRefTPane.setContent(srcSRefGrid);
        //endregion

        //region 目的参考系
        TitledPane desSRefTPane = new TitledPane();
        desSRefTPane.setText("目的坐标系");
        desSRefTPane.setCollapsible(false);
        desSRefTPane.setPrefWidth(300);
        desSRefTPane.setPadding(new Insets(5, 5, 0, 5));
        //设置内容
        GridPane desSRefGrid = new GridPane();
        desSRefGrid.setHgap(5);
        desSRefGrid.setVgap(5);
        Label desSRefLabel = new Label("选择目的坐标系:");
        desSRefBtn = new Button("...");
        desSRefInfo = new TextArea();
//        desSRefInfo.setText(getDesSrefInfo());
        desSRefInfo.setPrefWidth(300);
        desSRefInfo.setEditable(false);
        desSRefGrid.add(desSRefLabel, 0, 0);
        desSRefGrid.add(desSRefBtn, 1, 0);
        desSRefGrid.add(desSRefInfo, 0, 1, 2, 1);
        desSRefTPane.setContent(desSRefGrid);
        //endregion

        //region 地理转换参数设置
        TitledPane transParamTPane = new TitledPane();
        transParamTPane.setText("参照系转换设置");
        transParamTPane.setCollapsible(false);
        transParamTPane.setPadding(new Insets(5, 5, 5, 5));
        Label transMethodLabel = new Label("转换方法:");
        Label transParamLabel = new Label("地理转换参数:");
        transMethodCombobox = new ComboBox();
        elpTransParamNameList.addAll("无");
        elpTransParamArrayList.add(null);
        //初始化地理转换项列表
        loadTransParamList();
        transMethodCombobox.setItems(elpTransParamNameList);
        transMethodCombobox.getSelectionModel().select(0);
        transMethodCombobox.setPrefWidth(200);
        transParamBtn = new Button("设置...");
        transParamBtn.setPrefWidth(200);
        GridPane transParamGrid = new GridPane();
        transParamGrid.setHgap(5);
        transParamGrid.setVgap(5);
        transParamGrid.add(transMethodLabel, 0, 0);
        transParamGrid.add(transMethodCombobox, 1, 0);
        transParamGrid.add(transParamLabel, 2, 0);
        transParamGrid.add(transParamBtn, 3, 0);
        transParamTPane.setContent(transParamGrid);
        //endregion

        gridPane.add(srcTPane, 0, 0);
        gridPane.add(desTPane, 1, 0);
        gridPane.add(srcSRefTPane, 0, 1);
        gridPane.add(desSRefTPane, 1, 1);
        gridPane.add(transParamTPane, 0, 2, 400, 1);
    }

    /**
     * 加载地理转换参数
     */
    private void loadTransParamList() {
        elpTransParamNameList.clear();
        elpTransParamArrayList.clear();
        elpTransParamNameList.addAll("无");
        elpTransParamArrayList.add(null);
        String sep = File.separator;
        String m_Path = (EnvConfig.getConfigDirectory(SysConfigDirType.Projection));//TransLst.dat
        m_Path += sep + "TransLst.dat";
        File file = new File(m_Path);
        if (file.exists()) {
            ElpTransformation.loadElpTransParam(m_Path);
            for (int i = 0; i < ElpTransformation.getElpTransParamCount(); i++) {
                if (ElpTransformation.getElpTransParam(i) != null) {
                    elpTransParamNameList.addAll((ElpTransformation.getElpTransParam(i).getTransName()));
                    elpTransParamArrayList.add(ElpTransformation.getElpTransParam(i));
                }
            }
            ElpTransformation.saveElpTransParam(m_Path);
        }
        transMethodCombobox.getSelectionModel().select(0);
    }

    /**
     * 获取地理转换参数
     */
    private ElpTransParam getSelectTransPanram() {
        return this.elpTransParamArrayList.get(this.transMethodCombobox.getSelectionModel().getSelectedIndex());
    }
    /**
     * 事件绑定
     */
    private void bindAction() {
        //源点X
        this.srcxTextField.setText("0");
        srcxTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue == null || newValue.trim() == "")
                    srcxTextField.setText("0");
                else {
                    //输入检查
                    try {
                        double a = Double.valueOf(newValue);
                    } catch (Exception e) {
                        srcxTextField.setText(oldValue);
                    }
                }
                clearProjectResult();
            }
        });
        //源点Y
        this.srcyTextField.setText("0");
        srcyTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue == null || newValue.trim() == "")
                    srcyTextField.setText("0");
                else {
                    //输入检查
                    try {
                        double a = Double.valueOf(newValue);
                    } catch (Exception e) {
                        srcyTextField.setText(oldValue);
                    }
                }
                clearProjectResult();
            }
        });

        //源点Z
        this.srczTextField.setText("0");
        srczTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue == null || newValue.trim() == "")
                    srczTextField.setText("0");
                else {
                    //输入检查
                    try {
                        double a = Double.valueOf(newValue);
                    } catch (Exception e) {
                        srczTextField.setText(oldValue);
                    }
                }
                clearProjectResult();
            }
        });

        //目的点坐标
        this.desxTextField.setText("0");
        this.desyTextField.setText("0");
        this.deszTextField.setText("0");

        //选择源参照系
        srcSRefBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                SRefManagerDialog sRefManagerDialog = new SRefManagerDialog(true);
                if(sRefManagerDialog.showAndWait().equals(Optional.of(ButtonType.OK)))
                {
                    srcSRefData = sRefManagerDialog.getSelectedSRef();
                    m_SrcAngUnit = srcSRefData.getAngUnit();
                    String srefInfo = getSRefDataInfo(srcSRefData,true);
                    setSrcSrefInfo(srefInfo);
                }
                clearProjectResult();
            }
        });
        //选择目的参照系
        desSRefBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                SRefManagerDialog sRefManagerDialog = new SRefManagerDialog(true);
                if(sRefManagerDialog.showAndWait().equals(Optional.of(ButtonType.OK)))
                {
                    desSRefData = sRefManagerDialog.getSelectedSRef();
                    m_DesAngUnit = desSRefData.getAngUnit();
                    String srefInfo = getSRefDataInfo(desSRefData,true);
                    setDesSrefInfo(srefInfo);
                }
                clearProjectResult();
            }
        });
        //结果显示为度分秒
        desCheckbox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (projectedDot != null) {
                    if (desCheckbox.isSelected()) {
                        desxTextField.setText(String.valueOf(convertDesAngUnitToDMS(projectedDot.getX())));
                        desyTextField.setText(String.valueOf(convertDesAngUnitToDMS(projectedDot.getY())));
                    } else {
                        desxTextField.setText(String.valueOf(projectedDot.getX()));
                        desyTextField.setText(String.valueOf(projectedDot.getY()));
                    }
                }
            }
        });
        //选择转换方法
        transMethodCombobox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                clearProjectResult();
            }
        });

        //设置地理转换参数
        transParamBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ElpTransSettingDialog dialog = new ElpTransSettingDialog();
                Optional<ArrayList<ElpTransParam>> optional = dialog.showAndWait();
                if (optional.isPresent()) {
                    elpTransParamArrayList.clear();
                    elpTransParamArrayList = optional.get();
                    //刷新下拉列表
                    elpTransParamNameList.clear();
                    elpTransParamNameList.addAll("无");
                    if (elpTransParamArrayList != null && elpTransParamArrayList.size() > 0) {
                        for (int i = 0; i < elpTransParamArrayList.size(); i++) {
                            ElpTransParam param = elpTransParamArrayList.get(i);
                            if (param != null) {
                                String name = param.getTransName();
                                if (name != null && name.length() > 0) {
                                    elpTransParamNameList.addAll(name);
                                }
                            }
                        }
                        transMethodCombobox.setItems(elpTransParamNameList);
                        transMethodCombobox.getSelectionModel().select(0);
                    }
                }
            }
        });
    }

    /**
     * 清空投影结果
     */
    private void clearProjectResult() {
        this.desxTextField.setText("0");
        this.desyTextField.setText("0");
        this.deszTextField.setText("0");
    }

    /**
     * 将原参照系的坐标(度分秒)转换为度
     */
    private double convertSrcAngUnitToD(double srcAngUnit) {
        double rtn = srcAngUnit;
        switch (this.m_SrcAngUnit) {
            case Minute:
                rtn = srcAngUnit / 60;
                break;
            case Second:
                rtn = srcAngUnit / 3600;
                break;
            case DMS:
                rtn = AngleConvert.dmsSToD(srcAngUnit);
                break;
            default:
                break;
        }
        return rtn;
    }

    /**
     * 将目的参照系的坐标(度分秒)转换为度分秒(度分秒)转换为度
     */
    private double convertDesAngUnitToDMS(double desAngUnit) {
        double rtn = desAngUnit;
        switch (this.m_DesAngUnit) {
            case Degree:
                rtn = AngleConvert.dToDMS(desAngUnit);
                break;
            case Minute:
                rtn = AngleConvert.dToDMS(desAngUnit / 60);
                break;
            case Second:
                rtn = AngleConvert.dToDMS(desAngUnit / 3600);
                break;
            default:
                break;
        }
        return rtn;
    }

    // 投影按钮
    private void okButtonClick(ActionEvent event) {
        if (this.srcSRefData == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "请选择源参照系!", ButtonType.OK);
            alert.show();
            event.consume();
        } else if (this.desSRefData == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "请选择目的参照系!", ButtonType.OK);
            alert.show();
            event.consume();
        }
        //开始投影转换
        else {
            Dot3D oldDot = new Dot3D();
            oldDot.setX(Double.valueOf(srcxTextField.getText()));
            oldDot.setY(Double.valueOf(srcyTextField.getText()));
            oldDot.setZ(Double.valueOf(srczTextField.getText()));
            if (!this.srcCheckbox.isDisable() && this.srcCheckbox.isSelected()) {
                oldDot.setX(Double.valueOf(convertSrcAngUnitToD(oldDot.getX())));
                oldDot.setY(Double.valueOf(convertSrcAngUnitToD(oldDot.getY())));
            }
            GeoPoint oldPoint = new GeoPoint(oldDot);
            Geometry geometry = GeometryOperator.project(oldPoint,this.srcSRefData, this.desSRefData, getSelectTransPanram());
            GeoPoint newPoint = null;
            if(geometry != null && geometry instanceof GeoMultiPoint)
            {
                newPoint = new GeoPoint(((GeoMultiPoint) geometry).get(0));
            }
//            GeoPoint newPoint = (GeoPoint) oldPoint.transSRS(this.srcSRefData, this.desSRefData, getSelectTransPanram());
            if (newPoint != null) {
                this.projectedDot = newPoint.get();
                if (!this.desCheckbox.isDisable() && this.desCheckbox.isSelected()) {
                    this.desxTextField.setText(String.valueOf(convertDesAngUnitToDMS(projectedDot.getX())));
                    this.desyTextField.setText(String.valueOf(convertDesAngUnitToDMS(projectedDot.getY())));
                } else {
                    this.desxTextField.setText(String.valueOf(projectedDot.getX()));
                    this.desyTextField.setText(String.valueOf(projectedDot.getY()));
                }
                this.deszTextField.setText(String.valueOf(projectedDot.getZ()));
            }
        }
        event.consume();
    }

    //设置源参照系信息-选择参照系后更新文本框
    private void setSrcSrefInfo(String text)
    {
        srcSRefInfo.setText(text);
    }
    //设置目的参照系信息-选择参照系后更新文本框
    private void setDesSrefInfo(String text)
    {
        desSRefInfo.setText(text);
    }

    /**
     * 获取参照系的字符串信息
     *
     * @param sRef     参照系
     * @param showName 字符串中是否需要显示名称，如参照系管理界面已经有显示名称了，就不需要再显示
     * @return
     */
    private String getSRefDataInfo(SRefData sRef, boolean showName)
    {
        String sRefInfo = "";
        if (sRef != null && sRef.getSRSName() != null && !sRef.getSRSName().isEmpty())
        {
            if (showName)
            {
                sRefInfo += String.format("名称:%s%n%n", sRef.getSRSName());
            }

            //region 投影坐标系信息
            if (sRef.getType() == SRefType.PRJ || (sRef.getType() == SRefType.JWD && sRef.getProjType().value() > 0))//3-投影坐标系，1-地理坐标系
            {
                sRefInfo += String.format("投影类型：%s", LanguageConvert.sRefProjTypeConvertEx(sRef.getProjType()));
                int index = sRef.getProjType().value();
                switch (index)
                {
                    case 23:
                        break;
                    default:
                        sRefInfo += String.format("%n    投影东偏：%f", sRef.getFalseEasting());
                        sRefInfo += String.format("%n    投影北偏：%f", sRef.getFalseNorthing());
                        break;
                }

                switch (index)
                {
                    case 23:
                    case 15:
                    case 17:
                    case 18:
                    case 21:
                    case 22:
                    {
                        sRefInfo += String.format("%n    中央经线：%f", sRef.getCentralMeridian());
                        break;
                    }
                    case 1:
                    case 5:
                    case 6:
                    case 24:
                    case 25:
                    case 26:
                    {
                        sRefInfo += String.format("%n    中央经线：%f", sRef.getCentralMeridian());
                        sRefInfo += String.format("%n    投影原点纬度：%f", sRef.getLatitudeOfOrigin());
                        break;
                    }
                    case 4:
                    case 16:
                    {
                        sRefInfo += String.format("%n    中央经线：%f", sRef.getCentralMeridian());
                        sRefInfo += String.format("%n    无变形纬度：%f", sRef.getLatitudeOfOrigin());
                        break;
                    }
                    case 2:
                    case 7:
                    {
                        sRefInfo += String.format("%n    中央经线：%f", sRef.getCentralMeridian());
                        sRefInfo += String.format("%n    投影原点纬度：%f", sRef.getLatitudeOfOrigin());
                        sRefInfo += String.format("%n    第一标准纬度：%f", sRef.getStandardParallel1());
                        sRefInfo += String.format("%n    第二标准纬度：%f", sRef.getStandardParallel2());
                        break;
                    }
                    case 3:
                    {
                        sRefInfo += String.format("%n    中央经线：%f", sRef.getCentralMeridian());
                        sRefInfo += String.format("%n    投影原点纬度：%f", sRef.getLatitudeOfOrigin());
                        sRefInfo += String.format("%n    第一标准纬度：%f", sRef.getStandardParallel1());
                        sRefInfo += String.format("%n    第二标准纬度：%f", sRef.getStandardParallel2());
                        sRefInfo += String.format("%n    比例因子：%f", sRef.getScaleFactor());
                        break;
                    }
                    case 8:
                    {
                        sRefInfo += String.format("%n    中央经线：%f", sRef.getCentralMeridian());
                        sRefInfo += String.format("%n    投影原点纬度：%f", sRef.getLatitudeOfOrigin());
                        sRefInfo += String.format("%n    比例因子：%f", sRef.getScaleFactor());
                        break;
                    }
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    {
                        sRefInfo += String.format("%n    中央经线：%f", sRef.getCentralMeridian());
                        sRefInfo += String.format("%n    投影中心点纬度：%f", sRef.getLatitudeOfOrigin());
                        break;
                    }
                    case 14:
                    {
                        sRefInfo += String.format("%n    中央经线：%f", sRef.getCentralMeridian());
                        sRefInfo += String.format("%n    投影中心点纬度：%f", sRef.getLatitudeOfOrigin());
                        sRefInfo += String.format("%n    透视点到球面的距离：%f", sRef.getStandardParallel1());
                        break;
                    }
                    case 19:
                    {
                        sRefInfo += String.format("%n    投影中心点的比例因子：%f", sRef.getCentralMeridian());
                        sRefInfo += String.format("%n    投影原点纬度：%f", sRef.getLatitudeOfOrigin());
                        sRefInfo += String.format("%n    定义中心投影线的第一经度：%f", sRef.getLongitudeOf1st());
                        sRefInfo += String.format("%n    定义中心投影线的第一纬度：%f", sRef.getStandardParallel1());
                        sRefInfo += String.format("%n    定义中心投影线的第二经度：%f", sRef.getLongitudeOf2nd());
                        sRefInfo += String.format("%n    定义中心投影线的第二纬度：%f", sRef.getStandardParallel2());
                        break;
                    }
                    case 20:
                    {
                        sRefInfo += String.format("%n    无变形纬度：%f", sRef.getLatitudeOfOrigin());
                        sRefInfo += String.format("%n    地球Y轴对应的经度：%f", sRef.getCentralMeridian());
                        break;
                    }
                    default:
                        break;
                }

                sRefInfo += String.format("%n    水平比例尺：%f", sRef.getRate());
                sRefInfo += String.format("%n    长度单位：%s", LanguageConvert.sRefLenUnitConvert(sRef.getUnit()));
                sRefInfo += String.format("%n    图形平移：dx = %f, dy = %f%n", sRef.getX(), sRef.getY());

                sRefInfo += String.format("%n地理坐标系：%s%n", sRef.getGCSName());
            }
            //endregion

            //region 地理坐标系信息

            ElpParam ep = ElpTransformation.getElpParam(sRef.getSpheroid().value());
            if (ep != null)
            {
                sRefInfo += String.format("标准椭球：%s", ep.getName());

                sRefInfo += String.format("%n    长轴：%f", sRef.getSemiMajorAxis());
                sRefInfo += String.format("%n    短轴：%f", sRef.getSemiMinorAxis());
                sRefInfo += String.format("%n    扁率：%f", sRef.getFlattening());
                sRefInfo += String.format("%n角度单位：%s", LanguageConvert.sRefLenUnitConvert(sRef.getAngUnit()));
                sRefInfo += String.format("%n本初子午线：%s", sRef.getPrimeMeridian());
                if (sRef.getPrimeMeridian() == "<自定义...>")
                {
                    double dms = sRef.getPMOffset();
                    boolean negative = dms < 0;
                    dms = Math.abs(dms);
                    int d = (int) Math.floor(dms / 10000);
                    if (negative)
                    {
                        d *= -1;
                    }
                    dms = dms % 10000;
                    sRefInfo += String.format(" (经度：%d度%d分%f秒)", d, (int) Math.floor(dms / 100), dms % 100.0);
                }
            }

            //endregion
        }
        return sRefInfo;
    }

}
