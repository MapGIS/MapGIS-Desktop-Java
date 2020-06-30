package com.zondy.mapgis.sref;

import com.zondy.mapgis.base.*;
import com.zondy.mapgis.controls.common.ZDComboBox;
import com.zondy.mapgis.geodatabase.DataBase;
import com.zondy.mapgis.srs.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.dom4j.Node;

import java.util.List;

/**
 * @author CR
 * @file GeoSRefPane.java
 * @brief 地理参照系界面
 * @create 2020-02-04.
 */
public class GeoSRefPane extends VBox
{
    //region 变量
    private final TextField textFieldName = new TextField();
    private final TextField textFieldEPSG = UIFunctions.newIntTextField();
    private final Label labelB = new Label("短轴:");
    private final Label labelAF = new Label("扁率:");
    private final ZDComboBox<String> comboBoxEllipsoid = new ZDComboBox<>();
    private final TextField textFieldA = UIFunctions.newDecimalTextField();
    private final TextField textFieldB = UIFunctions.newDecimalTextField();
    private final TextField textFieldAF = UIFunctions.newDecimalTextField();
    private final RadioButton radioButtonB = new RadioButton("短轴:");
    private final RadioButton radioButtonAF = new RadioButton("扁率:");
    private final ZDComboBox<String> comboBoxUnit = new ZDComboBox<>();
    private final ZDComboBox<String> comboBoxPM = new ZDComboBox<>();
    private final TextField textFieldPM = new TextField();
    private SRefData sRefData;//当前参照系对象
    private boolean nameCanEmpty;//是否允许Name为空，投影参照系的地理坐标系名称可为空
    private Tooltip tooltipError = new Tooltip();
    //endregion

    /**
     * 构造新建地理坐标系的界面
     */
    public GeoSRefPane()
    {
        this(null);
    }

    /**
     * 构造新建/修改地理坐标系的界面
     *
     * @param sref 要创建参照系的数据库
     */
    public GeoSRefPane(SRefData sref)
    {
        this(sref, false);
    }

    /**
     * 构造新建/修改地理坐标系的界面
     *
     * @param sref         待修改的参照系
     * @param nameCanEmpty 名称能否为空(地理坐标系名称不能为空，投影坐标系里面地理坐标系名称可为空。）
     */
    public GeoSRefPane(SRefData sref, boolean nameCanEmpty)
    {
        this.sRefData = sref;
        this.nameCanEmpty = nameCanEmpty;

        //region 名称信息
        GridPane gridPaneName = new GridPane();
        gridPaneName.setHgap(6);
        gridPaneName.setVgap(6);
        gridPaneName.add(new Label("名称:"), 0, 0);
        gridPaneName.add(this.textFieldName, 1, 0);
        gridPaneName.add(new Label("EPSG代码:"), 0, 1);
        gridPaneName.add(this.textFieldEPSG, 1, 1);
        gridPaneName.getColumnConstraints().add(0, new ColumnConstraints(71));
        GridPane.setHgrow(this.textFieldName, Priority.ALWAYS);

        //名称输入验证
        this.textFieldName.textProperty().addListener((observable, oldValue, newValue) ->
        {
            StringProperty errorMsg = new SimpleStringProperty();
            if (!XString.isTextValid(newValue, 96, GISDefaultValues.getInvalidNameCharList(), errorMsg))
            {
                this.textFieldName.setText(oldValue);
                UIFunctions.showErrorTip(this.textFieldName, errorMsg.get(), this.tooltipError);
            }
        });
        //endregion

        //region 椭球体
        int count = ElpTransformation.getElpCount();
        for (int i = 1; i <= count; i++)//貌似这个索引是从1开始的。
        {
            if (i != 5)//5是自定义，拎到最后面去了。
            {
                ElpParam ep = ElpTransformation.getElpParam(i);
                if (ep != null)
                {
                    this.comboBoxEllipsoid.getItems().add(ep.getName());
                }
            }
        }
        this.comboBoxEllipsoid.getItems().add("<自定义...>");

        GridPane gridPaneEllipsoid = new GridPane();
        gridPaneEllipsoid.setHgap(6);
        gridPaneEllipsoid.setVgap(6);
        gridPaneEllipsoid.add(new Label("标准椭球:"), 0, 0);
        gridPaneEllipsoid.add(new Label("长轴:"), 0, 1);
        gridPaneEllipsoid.add(this.labelB, 0, 2);
        gridPaneEllipsoid.add(this.labelAF, 0, 3);
        gridPaneEllipsoid.add(this.radioButtonB, 0, 2);
        gridPaneEllipsoid.add(this.radioButtonAF, 0, 3);
        gridPaneEllipsoid.add(this.comboBoxEllipsoid, 1, 0);
        gridPaneEllipsoid.add(this.textFieldA, 1, 1);
         gridPaneEllipsoid.add(this.textFieldB, 1, 2);
        gridPaneEllipsoid.add(this.textFieldAF, 1, 3);
        gridPaneEllipsoid.getColumnConstraints().add(0, new ColumnConstraints(71));
        GridPane.setHgrow(this.textFieldA, Priority.ALWAYS);
        this.radioButtonB.setVisible(false);
        this.radioButtonAF.setVisible(false);
        this.comboBoxEllipsoid.prefWidthProperty().bind(this.textFieldName.widthProperty());

        //切换标准椭球时，自动读取“长轴、短轴和扁率”
        this.comboBoxEllipsoid.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
        {
            int ellipseIndex = this.comboBoxEllipsoid.getSelectionModel().getSelectedIndex();
            boolean customEllipse = (ellipseIndex == this.comboBoxEllipsoid.getItems().size() - 1);//标准椭球，<自定义>

            //设置长轴、短轴、扁率的可编辑性
            boolean isB = this.radioButtonB.isSelected();
            this.textFieldA.setDisable(!customEllipse);
            this.radioButtonB.setVisible(customEllipse);
            this.radioButtonAF.setVisible(customEllipse);
            this.labelB.setVisible(!customEllipse);
            this.labelAF.setVisible(!customEllipse);
            this.textFieldB.setDisable(!(customEllipse && isB));
            this.textFieldAF.setDisable(!(customEllipse && !isB));

            if (!customEllipse)
            {
                //读长轴、短轴、扁率
                if (ellipseIndex < 4)//5是自定义，拎到最后面去了,而取参数时索引是从1开始的。
                {
                    ellipseIndex += 1;
                } else if (ellipseIndex == this.comboBoxEllipsoid.getItems().size() - 1)
                {
                    ellipseIndex = 5;
                } else if (ellipseIndex >= 4)
                {
                    ellipseIndex += 2;
                }
                ElpParam ep = ElpTransformation.getElpParam(ellipseIndex);
                if (ep != null)
                {
                    this.textFieldA.setText(String.valueOf(ep.getA()));
                    this.textFieldB.setText(String.valueOf(ep.getB()));
                    this.textFieldAF.setText(String.valueOf(ep.getAF()));
                }
            } else
            {
                //给个默认的参数吧~~~（2000国家大地坐标系）
                this.textFieldA.setText("6378137");
                this.textFieldB.setText(String.valueOf(6378137 * (1 - 1 / 298.257222101)));
                this.textFieldAF.setText(String.valueOf(1 / 298.257222101));
            }
        });

        //修改了长轴以后，计算短轴或扁率
        this.textFieldA.focusedProperty().addListener((observable, oldValue, newValue) ->
        {
            double a = Double.valueOf(this.textFieldA.getText());
            if (a == 0)
            {
                this.textFieldB.setText("0");
                this.textFieldAF.setText("0");
            } else
            {
                boolean isB = this.radioButtonB.isSelected();
                if (isB)
                {
                    double b = Double.valueOf(this.textFieldB.getText());
                    this.textFieldAF.setText(String.valueOf((a - b) / a));
                } else
                {
                    double af = Double.valueOf(this.textFieldAF.getText());
                    this.textFieldB.setText(String.valueOf(a * (1 - af)));
                }
            }
        });

        //修改了短轴后计算扁率
        this.textFieldB.focusedProperty().addListener((observable, oldValue, newValue) ->
        {
            if (this.radioButtonB.isSelected())
            {
                double b = Double.valueOf(this.textFieldB.getText());
                if (b != 0)
                {
                    double a = Double.valueOf(this.textFieldA.getText());
                    if (a == 0)
                    {
                        this.textFieldA.setText(this.textFieldB.getText());
                    }
                    this.textFieldAF.setText(String.valueOf((a - b) / a));
                }
            }
        });

        //修改了扁率后计算短轴
        this.textFieldAF.focusedProperty().addListener((observable, oldValue, newValue) ->
        {
            if (this.radioButtonAF.isSelected())
            {
                double oblateness = Double.valueOf(String.valueOf(this.textFieldAF.getText()));
                double a = Double.valueOf(String.valueOf(this.textFieldA.getText()));
                this.textFieldB.setText(String.valueOf(a * (1 - oblateness)));
            }
        });

        ToggleGroup tg = new ToggleGroup();
        tg.getToggles().addAll(this.radioButtonB, this.radioButtonAF);
        this.radioButtonB.setSelected(true);
        tg.selectedToggleProperty().addListener((observable, oldValue, newValue) ->
        {
            boolean isB = this.radioButtonB.isSelected();
            this.textFieldB.setDisable(!isB);
            this.textFieldAF.setDisable(isB);
        });
        //endregion

        //region 单位
        this.comboBoxUnit.getItems().addAll(new String[]{
                LanguageConvert.sRefLenUnitConvert(SRefLenUnit.Degree),//度
                LanguageConvert.sRefLenUnitConvert(SRefLenUnit.Minute),//分
                LanguageConvert.sRefLenUnitConvert(SRefLenUnit.Second),//秒
                //LanguageConvert.sRefLenUnitConvert(SRefLenUnit.DMS),//度分秒
                LanguageConvert.sRefLenUnitConvert(SRefLenUnit.Radian),//弧度
                LanguageConvert.sRefLenUnitConvert(SRefLenUnit.Grad)});//梯度

        GridPane gridPaneUnit = new GridPane();
        gridPaneUnit.setHgap(6);
        gridPaneUnit.setVgap(6);
        gridPaneUnit.add(new Label("角度单位:"), 0, 0);
        gridPaneUnit.add(this.comboBoxUnit, 1, 0);
        gridPaneUnit.getColumnConstraints().add(0, new ColumnConstraints(71));
        GridPane.setHgrow(this.comboBoxUnit, Priority.ALWAYS);
        this.comboBoxUnit.prefWidthProperty().bind(this.textFieldName.widthProperty());
        //endregion

        //region 本初子午线
        this.comboBoxPM.getItems().addAll(new String[]{"格林威治", "巴黎", "<自定义...>"});

        GridPane gridPanePM = new GridPane();
        gridPanePM.setHgap(6);
        gridPanePM.setVgap(6);
        gridPanePM.add(new Label("名称:"), 0, 0);
        gridPanePM.add(new Label("经度:"), 0, 1);
        gridPanePM.add(this.comboBoxPM, 1, 0);
        gridPanePM.add(this.textFieldPM, 1, 1);
        gridPanePM.getColumnConstraints().add(0, new ColumnConstraints(71));
        GridPane.setHgrow(this.textFieldPM, Priority.ALWAYS);
        this.comboBoxPM.prefWidthProperty().bind(this.textFieldName.widthProperty());

        //切换本初子午线时，自动读取其经度
        this.comboBoxPM.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
        {
            int pmIndex = this.comboBoxPM.getSelectionModel().getSelectedIndex();//本初子午线
            this.textFieldPM.setDisable(pmIndex < this.comboBoxPM.getItems().size() - 1);
            if (pmIndex == 0)//格林威治
            {
                this.textFieldPM.setText("0 度 0 分 0 秒");
            } else if (pmIndex == 1)//巴黎
            {
                this.textFieldPM.setText("2 度 20 分 14.0250000000009 秒");
            } else if (pmIndex == 2)//<自定义>
            {
                this.textFieldPM.setText("");
            }
        });
        //endregion

        this.initGeoInfo(this.sRefData);

        super.setSpacing(6);
        super.getChildren().addAll(gridPaneName, new Separator(), gridPaneEllipsoid, new Separator(), gridPaneUnit, new Separator(), gridPanePM);
    }

    public void unEditableControls()
    {
        this.textFieldName.setDisable(true);
        this.comboBoxEllipsoid.setDisable(true);
        this.radioButtonB.setVisible(false);
        this.radioButtonAF.setVisible(false);
        this.labelB.setVisible(true);
        this.labelAF.setVisible(true);
        this.textFieldA.setDisable(true);
        this.textFieldB.setDisable(true);
        this.textFieldAF.setDisable(true);
        this.comboBoxUnit.setDisable(true);
        this.comboBoxPM.setDisable(true);
        this.textFieldPM.setDisable(true);
    }

    /**
     * 获取新建（或修改后）的参照系
     *
     * @return 新建（或修改后）的参照系
     */
    public SRefData getSpatialReference()
    {
        if (this.sRefData == null)
        {
            this.sRefData = new SRefData();
        }

        this.sRefData.setType(SRefType.JWD);//地理坐标系
        this.sRefData.setSRSName(this.textFieldName.getText());
        this.sRefData.setGCSName(this.textFieldName.getText());
        this.sRefData.setEpsgId(Integer.valueOf(this.textFieldEPSG.getText()));

        int ellipseIndex = this.comboBoxEllipsoid.getSelectionModel().getSelectedIndex();
        if (ellipseIndex < 4)//5是自定义，拎到最后面去了,而取参数时索引是从1开始的。
        {
            ellipseIndex += 1;
        } else if (ellipseIndex == this.comboBoxEllipsoid.getItems().size() - 1)
        {
            ellipseIndex = 5;
        } else if (ellipseIndex >= 4)
        {
            ellipseIndex += 2;
        }
        this.sRefData.setSpheroid(SRefEPType.valueOf(ellipseIndex));
        this.sRefData.setSemiMajorAxis(Double.valueOf(this.textFieldA.getText()));
        this.sRefData.setB(Double.valueOf(this.textFieldB.getText()));
        this.sRefData.setFlattening(Double.valueOf(this.textFieldAF.getText()));

        this.sRefData.setAngUnit(LanguageConvert.sRefLenUnitConvert(this.comboBoxUnit.getValue()));
        this.sRefData.setAngUnitFactor(SRefData.unitConvert(this.sRefData.getAngUnit(), SRefLenUnit.Radian));//8-弧度

        this.sRefData.setPrimeMeridian(this.comboBoxPM.getValue());

        //region 本初子午线经度
        String text = this.textFieldPM.getText();
        if (!XString.isNullOrEmpty(text))
        {
            int index = text.indexOf(" 度");
            if (index > 0)
            {
                int d = Integer.valueOf(text.substring(0, index));
                text = text.substring(index + 3);
                index = text.indexOf(" 分");
                if (index > 0)
                {
                    int m = Integer.valueOf(text.substring(0, index));
                    text = text.substring(index + 3);
                    index = text.indexOf(" 秒");
                    if (index > 0)
                    {
                        double s = Double.valueOf(text.substring(0, index));
                        double dDMS = Math.abs(d) * 10000 + m * 100 + s;
                        if (d < 0)
                        {
                            dDMS *= -1;
                        }
                        this.sRefData.setPMOffset(dDMS);
                    }
                }
            }
        }
        //endregion

        return this.sRefData;
    }

    /**
     * 验证输入的有效性
     *
     * @return 有效返回true
     */
    public boolean validInput()
    {
        String errorText = "";
        if (this.textFieldName.isEditable())
        {
            String srsName = this.textFieldName.getText();
            if (XString.isNullOrEmpty(srsName))
            {
                if (!this.nameCanEmpty)
                {
                    errorText = "地理参照系名称不能为空。";
                }
            } else if (XString.indexOfAny(srsName, GISDefaultValues.getInvalidNameChars()) >= 0)
            {
                errorText = "参照系名称不能包含下列任何字符之一：\\ / * ? \" \" < > |";
            } else
            {
                if (this.sRefData == null || !srsName.equals(this.sRefData.getSRSName()))
                {
                    if (SRefManagerDialog.isSrefNameExisted(srsName))
                    {
                        errorText = "已经存在同名参照系。";
                    }
                }
            }

            if (errorText == "")
            {
                if (this.comboBoxEllipsoid.getSelectionModel().getSelectedIndex() == this.comboBoxEllipsoid.getItems().size() - 1)//自定义
                {
                    double a = Double.valueOf(this.textFieldA.getText());
                    double b = Double.valueOf(this.textFieldB.getText());
                    if (a < 0 || b < 0 || a < b)
                    {
                        errorText = "长短轴应该大于0，且长轴应大于等于短轴（扁率的范围为[0，1)）。";
                    }
                }
            }

            if (errorText != "")
            {
                MessageBox.information(errorText);
            }
        }
        return errorText == "";
    }

    /**
     * 显示地理参照系信息
     *
     * @param sRef
     */
    public void initGeoInfo(SRefData sRef)
    {
        if (this.sRefData == null)//新建
        {
            this.comboBoxEllipsoid.getSelectionModel().select(0);
            this.comboBoxUnit.getSelectionModel().select(0);
            this.comboBoxPM.getSelectionModel().select(0);
        } else//初始化界面
        {
            this.textFieldName.setText(this.sRefData.getSRSName());
            this.textFieldEPSG.setText(String.valueOf(this.sRefData.getEpsgId()));

            //region 标准椭球
            int epIndex = this.sRefData.getSpheroid().value();
            if (epIndex < 5)//索引从1开始的
            {
                epIndex -= 1;
            } else if (epIndex == 5)
            {
                epIndex = (short) (this.comboBoxEllipsoid.getItems().size() - 1);
            } else if (epIndex > 5)//5(自定义)拉到最后面去了
            {
                epIndex -= 2;
            }

            if (epIndex >= 0)
            {
                this.comboBoxEllipsoid.getSelectionModel().select(epIndex);
            } else
            {
                this.comboBoxEllipsoid.setValue("");
            }
            //endregion

            this.textFieldA.setText(String.valueOf(this.sRefData.getSemiMajorAxis()));
            this.textFieldB.setText(String.valueOf(this.sRefData.getSemiMinorAxis()));
            this.textFieldAF.setText(String.valueOf(this.sRefData.getFlattening()));

            this.comboBoxUnit.setValue(LanguageConvert.sRefLenUnitConvert(this.sRefData.getAngUnit()));
            this.comboBoxPM.setValue(this.sRefData.getPrimeMeridian());

            double dms = this.sRefData.getPMOffset();
            boolean negative = dms < 0;
            dms = Math.abs(dms);
            int d = (int) Math.floor(dms / 10000);
            if (negative)
            {
                d *= -1;
            }
            dms = dms % 10000;
            int m = (int) Math.floor(dms / 100);
            double s = dms % 100.0;

            this.textFieldPM.setText(String.format("%d 度 %d 分 %f 秒", d, m, s));
        }
    }
}
