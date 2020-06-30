package com.zondy.mapgis.mapeditor.projecttransform;

import com.zondy.mapgis.base.LanguageConvert;
import com.zondy.mapgis.sref.SRefManagerDialog;
import com.zondy.mapgis.srs.ElpParam;
import com.zondy.mapgis.srs.ElpTransformation;
import com.zondy.mapgis.srs.SRefData;
import com.zondy.mapgis.srs.SRefType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;

/**
 * 源参照系信息面板
 * @author zkj
 */
public class DesSRefSettingPane extends GridPane {

    private String desSRefInfoText = "";
    private TextArea desSrefInfoArea = null;
    private SRefData desSrefData = null;
    public DesSRefSettingPane()
    {
        Label   desSrefLabel = new Label("目的参考系:");
        Button btnDesSRef = new Button("选择...");
        btnDesSRef.setPrefWidth(200);
        this.add(desSrefLabel,0,0);
        this.add(btnDesSRef,1,0);

        desSrefInfoArea = new TextArea();
        desSrefInfoArea.setEditable(false);
        this.add(desSrefInfoArea,0,1,2,1);
        this.setHgap(5);
        this.setVgap(5);
        this.setPadding(new Insets(5,5,5,5));

        btnDesSRef.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                SRefManagerDialog sRefManagerDialog = new SRefManagerDialog(true);
                if(sRefManagerDialog.showAndWait().equals(Optional.of(ButtonType.OK)))
                {
                    desSrefData = sRefManagerDialog.getSelectedSRef();
                    String srefInfo = getSRefDataInfo(desSrefData,true);
                    setDesSRefInfoText(srefInfo);
                }
            }
        });
    }
    /**
     * 获取参考系信息
     */
    public String getDesSRefInfoText()
    {
        return this.desSRefInfoText;
    }
    /**
     * 设置参考系信息
     */
    public void setDesSRefInfoText(String text)
    {
        this.desSRefInfoText = text;
        this.desSrefInfoArea.setText(text);
    }
    /**
     * 获取目的参考系对象
     */
    public SRefData getDesSrefData()
    {
        return this.desSrefData;
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
