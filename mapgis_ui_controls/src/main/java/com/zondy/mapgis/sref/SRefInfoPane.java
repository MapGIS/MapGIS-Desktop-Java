package com.zondy.mapgis.sref;

import com.zondy.mapgis.base.LanguageConvert;
import com.zondy.mapgis.controls.common.ButtonEdit;
import com.zondy.mapgis.geodatabase.DataBase;
import com.zondy.mapgis.geodatabase.IBasCls;
import com.zondy.mapgis.geodatabase.IVectorCls;
import com.zondy.mapgis.geodatabase.raster.RCatInfo;
import com.zondy.mapgis.geodatabase.raster.RDsInfo;
import com.zondy.mapgis.geodatabase.raster.RasterCatalog;
import com.zondy.mapgis.geodatabase.raster.RasterDataset;
import com.zondy.mapgis.srs.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import javax.xml.stream.FactoryConfigurationError;
import java.util.Optional;

/**
 * @author CR
 * @file SRefInfoPane.java
 * @brief 参照系信息界面
 * @create 2019-11-11.
 */
public class SRefInfoPane extends VBox
{
    private final ButtonEdit buttonEditName = new ButtonEdit();//参照系名称选择框
    private TextArea textAreaInfo = new TextArea();//参照系信息框
    private SRefData sRefData;//显示信息的参照系对象
    private DataBase db;//数据库
    private IBasCls cls;
    private final ObjectProperty<EventHandler<SRefEvent>> onSRefDataChanged = new ObjectPropertyBase<EventHandler<SRefEvent>>()
    {
        @Override
        protected void invalidated()
        {
            setEventHandler(SRefEvent.SREFCHANGED, get());
        }

        @Override
        public Object getBean()
        {
            return this;
        }

        @Override
        public String getName()
        {
            return "onSRefDataChanged";
        }
    };

    //region 构造及初始化

    /**
     * 参照系信息界面
     *
     * @param sRefData 参照系
     */
    public SRefInfoPane(SRefData sRefData)
    {
        this(sRefData, false);
    }

    /**
     * 参照系信息界面
     *
     * @param sRefData 参照系
     * @param justView 是否只是查看，不能选择新的参照系
     */
    public SRefInfoPane(SRefData sRefData, boolean justView)
    {
        this.setSpacing(6);
        this.setFillWidth(true);
        HBox hBox = new HBox(6, new Label("名称:"), this.buttonEditName);
        HBox.setHgrow(this.buttonEditName, Priority.ALWAYS);
        this.textAreaInfo.setWrapText(true);
        this.textAreaInfo.setEditable(false);
        this.getChildren().addAll(hBox, this.textAreaInfo);
        VBox.setVgrow(this.textAreaInfo, Priority.ALWAYS);

        if (justView)
        {
            this.buttonEditName.setButtonVisible(false);
        }

        this.buttonEditName.setOnButtonClick(event ->
        {
            SRefManagerDialog dlg = new SRefManagerDialog();
            if (dlg.showAndWait().equals(Optional.of(ButtonType.OK)))
            {
                this.setSRefData(dlg.getSelectedSRef());
            }
        });

        this.setSRefData(sRefData);
    }
    //endregion

    //region 获取和设置参照系、事件

    /**
     * 设置参照系，显示其信息
     *
     * @param sref
     */
    public void setSRefData(SRefData sref)
    {
        if ((this.sRefData == null && sref != null) || (this.sRefData != null && sref == null) || (this.sRefData != null && sref != null && this.sRefData.getSRSName() != sref.getSRSName()))
        {
            SRefData oldSRef = this.sRefData;
            this.sRefData = sref;
            this.buttonEditName.setText(this.sRefData != null ? this.sRefData.getSRSName() : "");
            this.textAreaInfo.setText(this.getSRefDataInfo(this.sRefData, false));
            Event.fireEvent(this, new SRefEvent(this, SRefEvent.SREFCHANGED, this.sRefData, oldSRef));
        }
    }

    /**
     * 获取当前参照系
     *
     * @return
     */
    public SRefData getSRefData()
    {
        return this.sRefData;
    }

    public EventHandler<SRefEvent> getOnSRefDataChanged()
    {
        return onSRefDataChanged.get();
    }

    public ObjectProperty<EventHandler<SRefEvent>> onSRefDataChangedProperty()
    {
        return onSRefDataChanged;
    }

    public void setOnSRefDataChanged(EventHandler<SRefEvent> onSRefDataChanged)
    {
        this.onSRefDataChanged.set(onSRefDataChanged);
    }
    //endregion

    //region 私有方法

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
            if (sRef.getType() == SRefType.PRJ || (sRef.getType() == SRefType.JWD && !sRef.getProjType().equals(SRefPrjType.LonLat)))//3-投影坐标系，1-地理坐标系
            {
                sRefInfo += String.format("投影类型：%s", LanguageConvert.sRefProjTypeConvertEx(sRef.getProjType()));
                SRefPrjType type = sRef.getProjType();
                switch (type)
                {
                    case Web_Mercator:
                        break;
                    default:
                        sRefInfo += String.format("%n    投影东偏：%f", sRef.getFalseEasting());
                        sRefInfo += String.format("%n    投影北偏：%f", sRef.getFalseNorthing());
                        break;
                }

                switch (type)
                {
                    case Web_Mercator:
                    case Sinusoidal:
                    case Miller_Cylindrical:
                    case VDG_I:
                    case Tan_Diff_Woof:
                    case EQ_Diff_Woof:
                    {
                        sRefInfo += String.format("%n    中央经线：%f", sRef.getCentralMeridian());
                        break;
                    }
                    case UTM:
                    case GaussKruger:
                    case Polyconic:
                    case Bonne_Ellipsolid:
                    case Bonne_Sphere:
                    case Winkel_II:
                    {
                        sRefInfo += String.format("%n    中央经线：%f", sRef.getCentralMeridian());
                        sRefInfo += String.format("%n    投影原点纬度：%f", sRef.getLatitudeOfOrigin());
                        break;
                    }
                    case Mercator:
                    case Equirectangular:
                    {
                        sRefInfo += String.format("%n    中央经线：%f", sRef.getCentralMeridian());
                        sRefInfo += String.format("%n    无变形纬度：%f", sRef.getLatitudeOfOrigin());
                        break;
                    }
                    case Albers_CEQA:
                    case EQ_DC:
                    {
                        sRefInfo += String.format("%n    中央经线：%f", sRef.getCentralMeridian());
                        sRefInfo += String.format("%n    投影原点纬度：%f", sRef.getLatitudeOfOrigin());
                        sRefInfo += String.format("%n    第一标准纬度：%f", sRef.getStandardParallel1());
                        sRefInfo += String.format("%n    第二标准纬度：%f", sRef.getStandardParallel2());
                        break;
                    }
                    case Lambert_CC:
                    {
                        sRefInfo += String.format("%n    中央经线：%f", sRef.getCentralMeridian());
                        sRefInfo += String.format("%n    投影原点纬度：%f", sRef.getLatitudeOfOrigin());
                        sRefInfo += String.format("%n    第一标准纬度：%f", sRef.getStandardParallel1());
                        sRefInfo += String.format("%n    第二标准纬度：%f", sRef.getStandardParallel2());
                        sRefInfo += String.format("%n    比例因子：%f", sRef.getScaleFactor());
                        break;
                    }
                    case Mercator_Transverse:
                    {
                        sRefInfo += String.format("%n    中央经线：%f", sRef.getCentralMeridian());
                        sRefInfo += String.format("%n    投影原点纬度：%f", sRef.getLatitudeOfOrigin());
                        sRefInfo += String.format("%n    比例因子：%f", sRef.getScaleFactor());
                        break;
                    }
                    case StereoGraphic:
                    case Lambert_AEQA:
                    case Azimuthal_EQD:
                    case Gnomonic:
                    case Orthographic:
                    {
                        sRefInfo += String.format("%n    中央经线：%f", sRef.getCentralMeridian());
                        sRefInfo += String.format("%n    投影中心点纬度：%f", sRef.getLatitudeOfOrigin());
                        break;
                    }
                    case General_VNSP:
                    {
                        sRefInfo += String.format("%n    中央经线：%f", sRef.getCentralMeridian());
                        sRefInfo += String.format("%n    投影中心点纬度：%f", sRef.getLatitudeOfOrigin());
                        sRefInfo += String.format("%n    透视点到球面的距离：%f", sRef.getStandardParallel1());
                        break;
                    }
                    case Mercator_Oblique:
                    {
                        sRefInfo += String.format("%n    投影中心点的比例因子：%f", sRef.getCentralMeridian());
                        sRefInfo += String.format("%n    投影原点纬度：%f", sRef.getLatitudeOfOrigin());
                        sRefInfo += String.format("%n    定义中心投影线的第一经度：%f", sRef.getLongitudeOf1st());
                        sRefInfo += String.format("%n    定义中心投影线的第一纬度：%f", sRef.getStandardParallel1());
                        sRefInfo += String.format("%n    定义中心投影线的第二经度：%f", sRef.getLongitudeOf2nd());
                        sRefInfo += String.format("%n    定义中心投影线的第二纬度：%f", sRef.getStandardParallel2());
                        break;
                    }
                    case Polar_Srereographic:
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
    //endregion
}
