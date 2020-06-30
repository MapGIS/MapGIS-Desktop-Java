package com.zondy.mapgis.base;

import com.google.common.collect.ImmutableMap;
import com.zondy.mapgis.att.Field;
import com.zondy.mapgis.geodatabase.*;
import com.zondy.mapgis.geodatabase.net.ComplexTypeExt;
import com.zondy.mapgis.geodatabase.raster.PixelType;
import com.zondy.mapgis.geodatabase.raster.RasterResampling;
import com.zondy.mapgis.geometry.GeomType;
import com.zondy.mapgis.map.GrayscaleTransform;
import com.zondy.mapgis.map.LayerState;
import com.zondy.mapgis.map.MapServerAccessMode;
import com.zondy.mapgis.scene.LayerType3D;
import com.zondy.mapgis.scene.SceneMode;
import com.zondy.mapgis.srs.*;

import java.util.Map;

/**
 * @author CR
 * @file LanguageConvert.java
 * @brief 枚举和文字的转换
 * @create 2019-11-11.
 */
public class LanguageConvert
{
    public static <K, V> K getKey(Map<K, V> map, V value)
    {
        K key = null;
        if (map != null && map.containsValue(value))
        {
            for (Map.Entry<K, V> entry : map.entrySet())
            {
                if (value.equals(entry.getValue()))
                {
                    key = entry.getKey();
                    break;
                }
            }
        }
        return key;
    }

    //region 投影类型(SRefProjType)
    private static final Map<SRefPrjType, String> sRefProjTypeMap = new ImmutableMap.Builder<SRefPrjType, String>().
            put(SRefPrjType.LonLat, "(无)").
            put(SRefPrjType.UTM, "通用横向墨卡托投影").
            put(SRefPrjType.Albers_CEQA, "亚尔勃斯等积圆锥投影").
            put(SRefPrjType.Lambert_CC, "兰伯特等角圆锥投影").
            put(SRefPrjType.Mercator, "墨卡托(正轴等角椭圆柱)投影").
            put(SRefPrjType.GaussKruger, "高斯-克吕格(横切椭圆柱等角)投影").
            put(SRefPrjType.Polyconic, "普通多圆锥投影").
            put(SRefPrjType.EQ_DC, "等距圆锥投影").
            put(SRefPrjType.Mercator_Transverse, "横向墨卡托(横切圆柱等角)投影").
            put(SRefPrjType.StereoGraphic, "球面投影(视点在球面)").
            put(SRefPrjType.Lambert_AEQA, "兰伯特等积方位投影").
            put(SRefPrjType.Azimuthal_EQD, "等距方位投影").
            put(SRefPrjType.Gnomonic, "心射切面(球心)投影").
            put(SRefPrjType.Orthographic, "正射投影(视点无穷远)").
            put(SRefPrjType.General_VNSP, "通用垂直近距透视(外心)投影").
            put(SRefPrjType.Sinusoidal, "正弦投影(伪圆柱)").
            put(SRefPrjType.Equirectangular, "等距离切圆柱(方格)投影").
            put(SRefPrjType.Miller_Cylindrical, "米勒圆柱(透视正圆柱)投影").
            put(SRefPrjType.VDG_I, "范德格林顿I投影").
            put(SRefPrjType.Mercator_Oblique, "斜轴墨卡托投影").
            put(SRefPrjType.Polar_Srereographic, "极点球面投影").
            put(SRefPrjType.Tan_Diff_Woof, "正切差分纬线多圆锥投影").
            put(SRefPrjType.EQ_Diff_Woof, "等差分纬线多圆锥投影").
            put(SRefPrjType.Web_Mercator, "WEB墨卡托投影").
            put(SRefPrjType.Bonne_Ellipsolid, "彭纳(椭球)投影").
            put(SRefPrjType.Bonne_Sphere, "彭纳(球)投影").
            put(SRefPrjType.Winkel_II, "Winkel II投影").build();

    /**
     * 获取投影类型名称（不带序号）
     *
     * @param type
     */
    public static String sRefProjTypeConvert(SRefPrjType type)
    {
        String str = "";
        if (sRefProjTypeMap.containsKey(type))
        {
            str = sRefProjTypeMap.get(type);
        }
        return str;
    }

    /**
     * 获取投影类型名称（带序号）
     *
     * @param type
     */
    public static String sRefProjTypeConvertEx(SRefPrjType type)
    {
        String str = sRefProjTypeConvert(type);
        if (!XString.isNullOrEmpty(str))
        {
            str = String.format("%d:%s", type.value(), str);
        }
        return str;
    }

    /**
     * 根据名称获取投影类型ID
     *
     * @param strType
     * @return
     */
    public static SRefPrjType sRefProjTypeConvert(String strType)
    {
        return getKey(sRefProjTypeMap, strType);
    }
    //endregion

    //region 长度单位（SRefLenUnit）
    private static final Map<SRefLenUnit, String> sRefLenUnitMap = new ImmutableMap.Builder<SRefLenUnit, String>().
            put(SRefLenUnit.MilliMeter, "毫米").
            put(SRefLenUnit.Meter, "米").
            put(SRefLenUnit.Second, "秒").
            put(SRefLenUnit.Degree, "度").
            put(SRefLenUnit.DMS, "度分秒").
            put(SRefLenUnit.Foot, "英尺").
            put(SRefLenUnit.Minute, "分").
            put(SRefLenUnit.Radian, "弧度").
            put(SRefLenUnit.Grad, "梯度").
            put(SRefLenUnit.KiloMeter, "公里").
            put(SRefLenUnit.DeciMeter, "分米").
            put(SRefLenUnit.CentiMeter, "厘米").
            put(SRefLenUnit.Inch, "英寸").
            put(SRefLenUnit.Yard, "码").
            put(SRefLenUnit.SeaMile, "海里").
            put(SRefLenUnit.Mile, "英里").
            put(SRefLenUnit.DM_S, "DM.S").
            put(SRefLenUnit.D_MS, "D.MS").build();

    /**
     * 长度单位转成中文
     *
     * @param unit
     * @return
     */
    public static String sRefLenUnitConvert(SRefLenUnit unit)
    {
        String str = "";
        if (sRefLenUnitMap.containsKey(unit))
        {
            str = sRefLenUnitMap.get(unit);
        }
        return str;
    }

    /**
     * 长度单位转换
     *
     * @param strType
     * @return
     */
    public static SRefLenUnit sRefLenUnitConvert(String strType)
    {
        return getKey(sRefLenUnitMap, strType);
    }
    //endregion

    //region 参考椭球体参数类型（SRefEPType）
    private static final Map<SRefEPType, String> sRefEPTypeMap = new ImmutableMap.Builder<SRefEPType, String>().
            //put(SRefEPType. UnDefine, "未知类型").
                    put(SRefEPType.Beijing54, "北京54").
                    put(SRefEPType.Xian80, "西安80").
                    put(SRefEPType.IUGG1979, "IUGG1979").
                    put(SRefEPType.IUGG1983, "IUGG1983").
                    put(SRefEPType.UserDefine, "自定义").
                    put(SRefEPType.IUGG1967, "IUGG1967").
                    put(SRefEPType.WGS84, "WGS-84").
                    put(SRefEPType.GRS80, "GRS-80").
                    put(SRefEPType.WGS72, "WGS-72").
                    put(SRefEPType.Australia1965, "澳大利亚1965").build();

    /**
     * 标准椭球转成字符串
     *
     * @param type
     * @return
     */
    public static String sRefEPTypeConvert(SRefEPType type)
    {
        String str = "";
        if (sRefEPTypeMap.containsKey(type))
        {
            str = sRefEPTypeMap.get(type);
        }
        return str;
    }

    /**
     * 标准椭球字符串转成id
     *
     * @param strType
     * @return
     */
    public static SRefEPType sRefEPTypeConvert(String strType)
    {
        return getKey(sRefEPTypeMap, strType);
    }
    //endregion

    //region 空间参照系类型（SRefType）
    public static final Map<SRefType, String> sRefTypeMap = new ImmutableMap.Builder<SRefType, String>().
            put(SRefType.NOPAR, "用户自定义坐标系").
            put(SRefType.JWD, "地理坐标系").
            put(SRefType.LOC, "地方坐标系").
            put(SRefType.PRJ, "投影平面直角坐标系").
            put(SRefType.XYZ, "地心大地直角坐标系").
            put(SRefType.VTC, "纵坐标系").
            put(SRefType.PHG, "像平面坐标系").
            put(SRefType.SOS, "SOS坐标系").
            put(SRefType.DSP, "显示坐标系").
            put(SRefType.MGC, "MilitaryGrid坐标系").build();

    /**
     * 空间参照系类型转成字符串
     *
     * @param type
     * @return
     */
    public static String sRefTypeConvert(SRefType type)
    {
        String str = "";
        if (sRefTypeMap.containsKey(type))
        {
            str = sRefTypeMap.get(type);
        }
        return str;
    }

    /**
     * 空间参照系类型字符串转成id
     *
     * @param strType
     * @return
     */
    public static SRefType sRefTypeConvert(String strType)
    {
        return getKey(sRefTypeMap, strType);
    }
    //endregion

    //region 投影带类型（SRefZoneType）
    private static final Map<SRefZoneType, String> sRefZoneTypeMap = new ImmutableMap.Builder<SRefZoneType, String>().
            //put(SRefZoneType.Unknown, "未知类型").
                    put(SRefZoneType.Degree6, "6度分带").
                    put(SRefZoneType.Degree3, "3度分带").
                    put(SRefZoneType.Degree1P5, "1.5度分带").build();

    /**
     * 投影带类型转成字符串
     *
     * @param type
     * @return
     */
    public static String sRefZoneTypeConvert(SRefZoneType type)
    {
        String str = "";
        if (sRefZoneTypeMap.containsKey(type))
        {
            str = sRefZoneTypeMap.get(type);
        }
        return str;
    }

    /**
     * 投影带类型字符串转成id
     *
     * @param strType
     * @return
     */
    public static SRefZoneType sRefZoneTypeConvert(String strType)
    {
        return getKey(sRefZoneTypeMap, strType);
    }
    //endregion

    //region 字段类型（FieldType）

    private static final Map<Field.FieldType, String> fieldTypeMap = new ImmutableMap.Builder<Field.FieldType, String>().
            put(Field.FieldType.fldStr, "字符串").
            put(Field.FieldType.fldByte, "字节型").
            put(Field.FieldType.fldBool, "布尔型").
            put(Field.FieldType.fldShort, "短整型").
            put(Field.FieldType.fldLong, "长整型").
            put(Field.FieldType.fldInt64, "64位长整型").
            put(Field.FieldType.fldFloat, "浮点型").
            put(Field.FieldType.fldDouble, "双精度型").
            put(Field.FieldType.fldDate, "日期型").
            put(Field.FieldType.fldTime, "时间型").
            put(Field.FieldType.fldTimeStamp, "邮戳型").
            //put(FieldType.fldBinary, "定长二进制类型").
                    put(Field.FieldType.fldBlob, "二进制大对象类型").build();

    /**
     * 字段类型转成字符串
     *
     * @param type
     * @return
     */
    public static String fieldTypeConvert(Field.FieldType type)
    {
        String str = "";
        if (fieldTypeMap.containsKey(type))
        {
            str = fieldTypeMap.get(type);
        }
        return str;
    }

    /**
     * 字段类型字符串转成FieldType
     *
     * @param strType
     * @return
     */
    public static Field.FieldType fieldTypeConvert(String strType)
    {
        return getKey(fieldTypeMap, strType);
    }
    //endregion

    //region 网络类点捏合策略
    private static final Map<ComplexTypeExt, String> complexTypeMap = new ImmutableMap.Builder<ComplexTypeExt, String>().
            put(ComplexTypeExt.EndPntSnapEdge, "端点策略").
            put(ComplexTypeExt.VertexSnapEdge, "顶点策略").
            put(ComplexTypeExt.HonerNode, "依边策略").
            put(ComplexTypeExt.OverRideNode, "优先策略").
            put(ComplexTypeExt.ComplexNode, "复杂点").build();

    public static String complexTypeExtConvert(ComplexTypeExt type)
    {
        String str = "";
        if (complexTypeMap.containsKey(type))
        {
            str = complexTypeMap.get(type);
        }
        return str;
    }

    public static ComplexTypeExt complexTypeExtConvert(String strType)
    {
        return getKey(complexTypeMap, strType);
    }
    //endregion

///// <summary>
///// 动态注记的字形转成中文
///// </summary>
///// <param name="annFontShape">动态注记的字形</param>
///// <returns>中文名称</returns>
//public static String AnnFontShapeConvert(int annFontShape)
//{
//    String cnAnnFontShape = "";
//
//    switch (annFontShape)
//    {
//        case 0:
//            cnAnnFontShape = Resources.String_Normal;
//            break;
//        case 1:
//            cnAnnFontShape = Resources.String_LeftItalic;
//            break;
//        case 2:
//            cnAnnFontShape = Resources.String_Italic;
//            break;
//        case 3:
//            cnAnnFontShape = Resources.String_LeftShear;
//            break;
//        case 4:
//            cnAnnFontShape = Resources.String_RightShear;
//            break;
//        default:
//            break;
//    }
//
//    return cnAnnFontShape;
//}
///// <summary>
///// 动态注记的字形转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static int AnnFontShapeConvert(String strType)
//{
//    int intAnnFontShape = 0;
//
//    if (strType == Resources.String_Normal)
//        intAnnFontShape = 0;
//    else if (strType == Resources.String_LeftItalic)
//        intAnnFontShape = 1;
//    else if (strType == Resources.String_Italic)
//        intAnnFontShape = 2;
//    else if (strType == Resources.String_LeftShear)
//        intAnnFontShape = 3;
//    else if (strType == Resources.String_RightShear)
//        intAnnFontShape = 4;
//
//    return intAnnFontShape;
//}
//
///// <summary>
///// 背景扩展转换
///// </summary>
///// <param name="bgExp">背景扩展</param>
///// <returns>对应字符串</returns>
//public static String BackGroudExpConvert(float bgExp)
//{
//    String str = "0%";
//    if (bgExp == 0.1F)
//        str = "10%";
//    else if (bgExp == 0.2F)
//        str = "20%";
//    else if (bgExp == 0.5F)
//        str = "50%";
//    else if (bgExp == 1F)
//        str = "100%";
//    else if (bgExp == 2F)
//        str = "200%";
//
//    return str;
//}
///// <summary>
///// 背景扩展转换
///// </summary>
///// <param name="strType">类型字串</param>
///// <returns>背景扩展值</returns>
//public static float BackGroudExpConvert(String strType)
//{
//    float f = 0;
//    if (strType == "10%")
//        f = 0.1F;
//    else if (strType == "20%")
//        f = 0.2F;
//    else if (strType == "50%")
//        f = 0.5F;
//    else if (strType == "100%")
//        f = 1F;
//    else if (strType == "200%")
//        f = 2F;
//
//    return f;
//}
//
///// <summary>
///// 背景/轮廓转换
///// </summary>
///// <param name="bgOrContour">背景/轮廓值</param>
///// <returns>对应显示文本</returns>
//public static String BGOrContourConvert(int bgOrContour)
//{
//    String str = "";
//
//    switch (bgOrContour)
//    {
//        case 0:
//            str = Resources.String_None;
//            break;
//        case 1:
//            str = Resources.String_Background;
//            break;
//        case 2:
//            str = Resources.String_Contour;
//            break;
//        default:
//            break;
//    }
//
//    return str;
//}
///// <summary>
///// 背景/轮廓转换
///// </summary>
///// <param name="strType">显示文本</param>
///// <returns>对应背景/轮廓值</returns>
//public static int BGOrContourConvert(String strType)
//{
//    int i = 0;
//    if (strType == Resources.String_None)
//        i = 0;
//    else if (strType == Resources.String_Background)
//        i = 1;
//    else if (strType == Resources.String_Contour)
//        i = 2;
//
//    return i;
//}
//
///// <summary>
///// 坐标系的维数转成中文
///// </summary>
///// <param name="coordiDimension">坐标系的维数</param>
///// <returns>中文名称</returns>
//public static String CoordiDimensionConvert(Byte coordiDimension)
//{
//    String str = "";
//
//    switch (coordiDimension)
//    {
//        case 2:
//            str = Resources.String_2D;
//            break;
//        case 3:
//            str = Resources.String_3D;
//            break;
//        case 4:
//            str = Resources.String_4D;
//            break;
//        default:
//            break;
//    }
//
//    return str;
//}
///// <summary>
///// 坐标系的维数成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static Byte CoordiDimensionConvert(String strType)
//{
//    Byte intCoordiDimension = 0;
//
//    if (strType == Resources.String_2D)
//        intCoordiDimension = 2;
//    else if (strType == Resources.String_3D)
//        intCoordiDimension = 3;
//    else if (strType == Resources.String_4D)
//        intCoordiDimension = 4;
//
//    return intCoordiDimension;
//}
//
///// <summary>
///// 装饰模式转成中文
///// </summary>
///// <param name="decorateMode">装饰模式</param>
///// <returns>中文名称</returns>
//public static String DecorateModeConvert(int decorateMode)
//{
//    String cnDecorateMode = "";
//
//    switch (decorateMode)
//    {
//        case 1:
//            cnDecorateMode = Resources.String_ByNum;
//            break;
//        case 2:
//            cnDecorateMode = Resources.String_BySpace;
//            break;
//        default:
//            break;
//    }
//
//    return cnDecorateMode;
//}
///// <summary>
/////  装饰模式转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static int DecorateModeConvert(String strType)
//{
//    int intDecorateMode = 1;
//
//    if (strType == Resources.String_ByNum)
//        intDecorateMode = 1;
//    else if (strType == Resources.String_BySpace)
//        intDecorateMode = 2;
//
//    return intDecorateMode;
//}
//
///// <summary>
///// 通过合并策略类型得到str
///// </summary>
///// <param name="DmnFldMerge">合并策略</param>
///// <returns>对应显示文本</returns>
//public static String DomainFldMergeConvert(DomainFldMerge DmnFldMerge)
//{
//    String str = null;
//    switch (DmnFldMerge)
//    {
//        case DomainFldMerge.DefVal:
//            str = Resources.String_DefVal;
//            break;
//        case DomainFldMerge.Sum:
//            str = Resources.String_SumValue;
//            break;
//        case DomainFldMerge.WgtAvg:
//            str = Resources.String_WgtAvg;
//            break;
//        default:
//            str = Resources.String_DefVal;
//            break;
//    }
//    return str;
//}
///// <summary>
///// 通过str得到合并策略类型
///// </summary>
///// <param name="str">显示文本</param>
///// <returns>对应合并策略</returns>
//public static DomainFldMerge DomainFldMergeConvert(String str)
//{
//    DomainFldMerge DmnFldMerge = DomainFldMerge.DefVal;
//    if (str == Resources.String_DefVal)
//        DmnFldMerge = DomainFldMerge.DefVal;
//    else if (str == Resources.String_SumValue)
//        DmnFldMerge = DomainFldMerge.Sum;
//    else if (str == Resources.String_WgtAvg)
//        DmnFldMerge = DomainFldMerge.WgtAvg;
//    return DmnFldMerge;
//}
//
///// <summary>
///// 通过拆分策略类型得到str
///// </summary>
///// <param name="DmnFldSplit">拆分策略</param>
///// <returns>对应显示文本</returns>
//public static String DomainFldSplitConvert(DomainFldSplit DmnFldSplit)
//{
//    String str = null;
//    switch (DmnFldSplit)
//    {
//        case DomainFldSplit.DefVal:
//            str = Resources.String_DefVal;
//            break;
//        case DomainFldSplit.Copy:
//            str = Resources.String_Copy;
//            break;
//        case DomainFldSplit.Ratio:
//            str = Resources.String_RatioValue;
//            break;
//        default:
//            str = Resources.String_DefVal;
//            break;
//    }
//    return str;
//}
///// <summary>
///// 通过str得到拆分策略类型
///// </summary>
///// <param name="str">显示文本</param>
///// <returns>对应拆分策略</returns>
//public static DomainFldSplit DomainFldSplitConvert(String str)
//{
//    DomainFldSplit DmnFldSplit = DomainFldSplit.DefVal;
//    if (str == Resources.String_DefVal)
//        DmnFldSplit = DomainFldSplit.DefVal;
//    else if (str == Resources.String_Copy)
//        DmnFldSplit = DomainFldSplit.Copy;
//    else if (str == Resources.String_RatioValue)
//        DmnFldSplit = DomainFldSplit.Ratio;
//    return DmnFldSplit;
//}
//
///// <summary>
///// 通过域类型得到str
///// </summary>
///// <param name="DmnType">域类型</param>
///// <returns>对应显示文本</returns>
//public static String DomainTypeConvert(DomainType DmnType)
//{
//    String str = null;
//    switch (DmnType)
//    {
//        case DomainType.Code:
//            str = Resources.String_CodeDomain;
//            break;
//        case DomainType.Range:
//            str = Resources.String_RangeDomain;
//            break;
//        default:
//            break;
//    }
//    return str;
//}
///// <summary>
///// 通过str得到域类型
///// </summary>
///// <param name="str">显示文本</param>
///// <returns>对应域类型</returns>
//public static DomainType DomainTypeConvert(String str)
//{
//    DomainType DmnType = DomainType.Unknown;
//    if (str == Resources.String_CodeDomain)
//        DmnType = DomainType.Code;
//    else if (str == Resources.String_RangeDomain)
//        DmnType = DomainType.Range;
//    return DmnType;
//}
//
///// <summary>
///// 区注记重复类型转成中文
///// </summary>
///// <param name="duplicateType">区注记重复类型</param>
///// <returns>中文名称</returns>
//public static String DuplicateTypeConvert(DuplicateType duplicateType)
//{
//    String cnDuplicateType = "";
//
//    switch (duplicateType)
//    {
//        case DuplicateType.OneLabelPreFeature:
//            cnDuplicateType = Resources.String_OneLabelPreFeature;
//            break;
//        case DuplicateType.OneLabelPreFeaturePart:
//            cnDuplicateType = Resources.String_OneLabelPreFeaturePart;
//            break;
//        case DuplicateType.RemoveDuplicatesLabels:
//            cnDuplicateType = Resources.String_RemoveDuplicatesLabels;
//            break;
//        default:
//            break;
//    }
//
//    return cnDuplicateType;
//}
///// <summary>
///// 区注记重复类型转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static DuplicateType DuplicateTypeConvert(String strType)
//{
//    DuplicateType enDuplicateType = DuplicateType.Unknown;
//
//    if (strType == Resources.String_OneLabelPreFeature)
//        enDuplicateType = DuplicateType.OneLabelPreFeature;
//    else if (strType == Resources.String_OneLabelPreFeaturePart)
//        enDuplicateType = DuplicateType.OneLabelPreFeaturePart;
//    else if (strType == Resources.String_RemoveDuplicatesLabels)
//        enDuplicateType = DuplicateType.RemoveDuplicatesLabels;
//
//    return enDuplicateType;
//}
///// <summary>
///// 填充模式转成中文
///// </summary>
///// <param name="fillMode">填充模式</param>
///// <returns>中文名称</returns>
//public static String FillModeConvert(int fillMode)
//{
//    String cnFillMode = "";
//
//    switch (fillMode)
//    {
//        case 1:
//            cnFillMode = Resources.String_GridAlignOrder;
//            break;
//        case 2:
//            cnFillMode = Resources.String_CrossOrder;
//            break;
//        default:
//            break;
//    }
//
//    return cnFillMode;
//}
///// <summary>
///// 填充模式转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static int FillModeConvert(String strType)
//{
//    int intFillMode = 1;
//
//    if (strType == Resources.String_GridAlignOrder)
//        intFillMode = 1;
//    else if (strType == Resources.String_CrossOrder)
//        intFillMode = 2;
//
//    return intFillMode;
//}
//
///// <summary>
///// 过滤方式转成中文
///// </summary>
///// <param name="filtrateMode">过滤方式</param>
///// <returns>中文名称</returns>
//public static String FiltrateModeConvert(int filtrateMode)
//{
//    String cnFiltrateMode = "";
//
//    switch (filtrateMode)
//    {
//        case 0:
//            cnFiltrateMode = Resources.String_None;
//            break;
//        case 1:
//            cnFiltrateMode = Resources.String_AttFilter;
//            break;
//        case 2:
//            cnFiltrateMode = Resources.String_ScopeFtr;
//            break;
//        default:
//            break;
//    }
//
//    return cnFiltrateMode;
//}
///// <summary>
///// 过滤方式转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static int FiltrateModeConvert(String strType)
//{
//    int intFiltrateMode = 0;
//
//    if (strType == Resources.String_None)
//        intFiltrateMode = 0;
//    else if (strType == Resources.String_AttFilter)
//        intFiltrateMode = 1;
//    else if (strType == Resources.String_ScopeFtr)
//        intFiltrateMode = 2;
//
//    return intFiltrateMode;
//}
//
///// <summary>
///// 焦点模式转成中文
///// </summary>
///// <param name="focusMode">焦点模式</param>
///// <returns>中文名称</returns>
//public static String FocusModeConvert(short focusMode)
//{
//    String cnFocusMode = "";
//
//    switch (focusMode)
//    {
//        case 0:
//            cnFocusMode = Resources.String_Flicker;
//            break;
//        case 1:
//            cnFocusMode = Resources.String_Highlight;
//            break;
//        default:
//            break;
//    }
//
//    return cnFocusMode;
//}
///// <summary>
///// 焦点模式转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static short FocusModeConvert(String strType)
//{
//    short intFocusMode = 0;
//
//    if (strType == Resources.String_Flicker)
//        intFocusMode = 0;
//    else if (strType == Resources.String_Highlight)
//        intFocusMode = 1;
//
//    return intFocusMode;
//}
//
/// <summary>
/// 几何类型枚举类型转成中文
/// </summary>
/// <param name="geomType">几何类型枚举类型</param>
/// <returns>中文名称</returns>

    /**
     * @param geomType
     * @return
     */
    public static String geomTypeConvert(GeomType geomType)
    {
        String cnGeomType = "";
        if (geomType.equals(GeomType.GeomAnn))
        {
            cnGeomType = "注记";
        } else if (geomType.equals(GeomType.GeomPnt))
        {
            cnGeomType = "点";
        } else if (geomType.equals(GeomType.GeomLin))
        {
            cnGeomType = "线";
        } else if (geomType.equals(GeomType.GeomReg))
        {
            cnGeomType = "区";
        } else if (geomType.equals(GeomType.GeomSurface))
        {
            cnGeomType = "面";
        } else if (geomType.equals(GeomType.GeomEntity))
        {
            cnGeomType = "体";
        } else if (geomType.equals(GeomType.GeomUnknown))
        {
            cnGeomType = "未知";
        }
        return cnGeomType;
    }

    /// <summary>
/// 几何类型枚举类型转成枚举值
/// </summary>
/// <param name="strType">中文名称</param>
/// <returns>枚举值</returns>
    public static GeomType geomTypeConvert(String cnGeomType)
    {
        GeomType geomType = GeomType.GeomUnknown;
        if ("注记".equals(cnGeomType))
        {
            geomType = GeomType.GeomAnn;
        } else if ("点".equals(cnGeomType))
        {
            geomType = GeomType.GeomPnt;
        } else if ("线".equals(cnGeomType))
        {
            geomType = GeomType.GeomLin;
        } else if ("区".equals(cnGeomType))
        {
            geomType = GeomType.GeomReg;
        } else if ("面".equals(cnGeomType))
        {
            geomType = GeomType.GeomSurface;
        } else if ("体".equals(cnGeomType))
        {
            geomType = GeomType.GeomEntity;
        }
        return geomType;
    }

    /**
     * 图层状态类型转成中文
     *
     * @param layerState 图层状态类型
     * @return 中文名称
     */
    public static String layerStateConvert(LayerState layerState)
    {
        String cnLayerState = "";
        if (LayerState.Active.equals(layerState))
        {
            cnLayerState = "激活";
        } else if (LayerState.Editable.equals(layerState))
        {
            cnLayerState = "编辑";
        } else if (LayerState.UnVisible.equals(layerState))
        {
            cnLayerState = "不可见";
        } else if (LayerState.Visible.equals(layerState))
        {
            cnLayerState = "可见";
        }
        return cnLayerState;
    }

    /**
     * 图层状态类型转成枚举值
     *
     * @param strType 中文名称
     * @return 枚举值
     */
    public static LayerState layerStateConvert(String strType)
    {
        LayerState enLayerState = LayerState.UnVisible;
        if ("激活".equals(strType))
        {
            enLayerState = LayerState.Active;
        } else if ("编辑".equals(strType))
        {
            enLayerState = LayerState.Editable;
        } else if ("不可见".equals(strType))
        {
            enLayerState = LayerState.UnVisible;
        } else if ("可见".equals(strType))
        {
            enLayerState = LayerState.Visible;
        }
        return enLayerState;
    }


    /**
     * 场景模式类型转成中文
     *
     * @param type 图层状态类型
     * @return 中文名称
     */
    public static String sceneModeConvert(SceneMode type)
    {
        String strType = "";
        if (SceneMode.GLOBE.equals(type))
        {
            strType = "球面模式";
        } else if (SceneMode.LOCAL.equals(type))
        {
            strType = "平面模式";
        }
        return strType;
    }

    /**
     * 场景模式类型转成枚举值
     *
     * @param strType 中文名称
     * @return 枚举值
     */
    public static SceneMode sceneModeConvert(String strType)
    {
        SceneMode type = SceneMode.LOCAL;
        if ("球面模式".equals(strType))
        {
            type = SceneMode.GLOBE;
        } else if ("平面模式".equals(strType))
        {
            type = SceneMode.LOCAL;
        }
        return type;
    }

    ///// <summary>
///// 线型调整方法枚举转成中文
///// </summary>
///// <param name="linAdjustType">线型调整方法枚举</param>
///// <returns>中文名称</returns>
//public static String LinAdjustTypeConvert(LinAdjustType linAdjustType)
//{
//    String cnLinAdjustType = "";
//
//    switch (linAdjustType)
//    {
//        case LinAdjustType.Adjust:
//            cnLinAdjustType = Resources.String_Adjust;
//            break;
//        case LinAdjustType.NoAdjust:
//            cnLinAdjustType = Resources.String_NoAdjust;
//            break;
//        default:
//            break;
//    }
//
//    return cnLinAdjustType;
//}
///// <summary>
///// 线型调整方法枚举转成枚举值
///// </summary>
///// <param name="strType">中文名称 </param>
///// <returns>枚举值</returns>
//public static LinAdjustType LinAdjustTypeConvert(String strType)
//{
//    LinAdjustType enLinAdjustType = LinAdjustType.NoAdjust;
//
//    if (strType == Resources.String_NoAdjust)
//        enLinAdjustType = LinAdjustType.NoAdjust;
//    else if (strType == Resources.String_Adjust)
//        enLinAdjustType = LinAdjustType.Adjust;
//
//    return enLinAdjustType;
//}
//
///// <summary>
///// 线模式转成中文
///// </summary>
///// <param name="linMode">线模式</param>
///// <returns>中文名称</returns>
//public static String LineMarkModeConvert(LineMarkMode linMode)
//{
//    String cnLinMode = "";
//    switch (linMode)
//    {
//        case LineMarkMode.BeginAndEndMarkMode:
//            cnLinMode = Resources.String_BeginAndEndMarkMode;
//            break;
//        case LineMarkMode.BeginMarkMode:
//            cnLinMode = Resources.String_BeginMarkMode;
//            break;
//        case LineMarkMode.EndMarkMode:
//            cnLinMode = Resources.String_EndMarkMode;
//            break;
//        case LineMarkMode.EveryOneMarkMode:
//            cnLinMode = Resources.String_EveryOneMarkMode;
//            break;
//        case LineMarkMode.MiddleMarkMode:
//            cnLinMode = Resources.String_MiddleMarkMode;
//            break;
//        case LineMarkMode.MiddleOfTwoMarkMode:
//            cnLinMode = Resources.String_MiddleOfTwoMarkMode;
//            break;
//        default:
//            break;
//    }
//    return cnLinMode;
//}
///// <summary>
///// 线模式转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static LineMarkMode LineMarkModeConvert(String strType)
//{
//    LineMarkMode linMode = LineMarkMode.BeginMarkMode;
//
//    if (strType == Resources.String_BeginMarkMode)
//        linMode = LineMarkMode.BeginMarkMode;
//    else if (strType == Resources.String_EndMarkMode)
//        linMode = LineMarkMode.EndMarkMode;
//    else if (strType == Resources.String_BeginAndEndMarkMode)
//        linMode = LineMarkMode.BeginAndEndMarkMode;
//    else if (strType == Resources.String_EveryOneMarkMode)
//        linMode = LineMarkMode.EveryOneMarkMode;
//    else if (strType == Resources.String_MiddleMarkMode)
//        linMode = LineMarkMode.MiddleMarkMode;
//    else if (strType == Resources.String_MiddleOfTwoMarkMode)
//        linMode = LineMarkMode.MiddleOfTwoMarkMode;
//
//    return linMode;
//}
//
///// <summary>
///// 线注记方位转成中文
///// </summary>
///// <param name="linePlaceType">线注记方位</param>
///// <returns>中文名称</returns>
//public static String LinePlaceTypeConvert(LinePlaceType linePlaceType)
//{
//    String cnLinePlaceType = "";
//
//    switch (linePlaceType)
//    {
//        case LinePlaceType.CurvedPlace:
//            cnLinePlaceType = Resources.String_CurvedPlace;
//            break;
//        case LinePlaceType.HorizationPlace:
//            cnLinePlaceType = Resources.String_HorizationPlace;
//            break;
//        case LinePlaceType.PerpendicularPlace:
//            cnLinePlaceType = Resources.String_PerpendicularPlace;
//            break;
//        case LinePlaceType.StraightPlace:
//            cnLinePlaceType = Resources.String_StraightPlace;
//            break;
//        default:
//            break;
//    }
//
//    return cnLinePlaceType;
//}
///// <summary>
///// 线注记方位转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static LinePlaceType LinePlaceTypeConvert(String strType)
//{
//    LinePlaceType enLinePlaceType = LinePlaceType.Unknown;
//
//    if (strType == Resources.String_CurvedPlace)
//        enLinePlaceType = LinePlaceType.CurvedPlace;
//    else if (strType == Resources.String_HorizationPlace)
//        enLinePlaceType = LinePlaceType.HorizationPlace;
//    else if (strType == Resources.String_PerpendicularPlace)
//        enLinePlaceType = LinePlaceType.PerpendicularPlace;
//    else if (strType == Resources.String_StraightPlace)
//        enLinePlaceType = LinePlaceType.StraightPlace;
//
//    return enLinePlaceType;
//}
//
///// <summary>
///// 线注记重复类型转成中文
///// </summary>
///// <param name="lineRepeatType">线注记重复类型</param>
///// <returns>中文名称</returns>
//public static String LineRepeatTypeConvert(LineRepeatType lineRepeatType)
//{
//    String cnLineRepeatType = "";
//
//    switch (lineRepeatType)
//    {
//        case LineRepeatType.AutoRepeat:
//            cnLineRepeatType = Resources.String_AutoRepeat;
//            break;
//        case LineRepeatType.NeverRepeat:
//            cnLineRepeatType = Resources.String_NeverRepeat;
//            break;
//        case LineRepeatType.RepeatByStep:
//            cnLineRepeatType = Resources.String_RepeatByStep;
//            break;
//        default:
//            break;
//    }
//
//    return cnLineRepeatType;
//}
///// <summary>
///// 线注记重复类型转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static LineRepeatType LineRepeatTypeConvert(String strType)
//{
//    LineRepeatType enLineRepeatType = LineRepeatType.Unknown;
//
//    if (strType == Resources.String_AutoRepeat)
//        enLineRepeatType = LineRepeatType.AutoRepeat;
//    else if (strType == Resources.String_NeverRepeat)
//        enLineRepeatType = LineRepeatType.NeverRepeat;
//    else if (strType == Resources.String_RepeatByStep)
//        enLineRepeatType = LineRepeatType.RepeatByStep;
//
//    return enLineRepeatType;
//}
//
///// <summary>
///// 线注记偏离线约束转成中文
///// </summary>
///// <param name="lineRestrictType">线注记偏离线约束</param>
///// <returns>中文名称</returns>
//public static String LineRestrictTypeConvert(LineRestrictType lineRestrictType)
//{
//    String cnLineRestrictType = "";
//
//    switch (lineRestrictType)
//    {
//        case LineRestrictType.AboveLine:
//            cnLineRestrictType = Resources.String_AboveLine;
//            break;
//        case LineRestrictType.BelowLine:
//            cnLineRestrictType = Resources.String_BelowLine;
//            break;
//        case LineRestrictType.LineHead:
//            cnLineRestrictType = Resources.String_LineHead;
//            break;
//        case LineRestrictType.LineHeadAndTail:
//            cnLineRestrictType = Resources.String_LineHeadAndTail;
//            break;
//        case LineRestrictType.LineTail:
//            cnLineRestrictType = Resources.String_LineTail;
//            break;
//        case LineRestrictType.OnLine:
//            cnLineRestrictType = Resources.String_OnLine;
//            break;
//        default:
//            break;
//    }
//
//    return cnLineRestrictType;
//}
///// <summary>
///// 线注记偏离线约束转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static LineRestrictType LineRestrictTypeConvert(String strType)
//{
//    LineRestrictType enLineRestrictType = LineRestrictType.Unknown;
//
//    if (strType == Resources.String_AboveLine)
//        enLineRestrictType = LineRestrictType.AboveLine;
//    else if (strType == Resources.String_BelowLine)
//        enLineRestrictType = LineRestrictType.BelowLine;
//    else if (strType == Resources.String_LineHead)
//        enLineRestrictType = LineRestrictType.LineHead;
//    else if (strType == Resources.String_LineHeadAndTail)
//        enLineRestrictType = LineRestrictType.LineHeadAndTail;
//    else if (strType == Resources.String_LineTail)
//        enLineRestrictType = LineRestrictType.LineTail;
//    else if (strType == Resources.String_OnLine)
//        enLineRestrictType = LineRestrictType.OnLine;
//
//    return enLineRestrictType;
//}
//
///// <summary>
///// 线注记分布策略转成中文
///// </summary>
///// <param name="lineSpreadType">线注记分布策略</param>
///// <returns>中文名称</returns>
//public static String LineSpreadTypeConvert(LineSpreadType lineSpreadType)
//{
//    String cnLineSpreadType = "";
//
//    switch (lineSpreadType)
//    {
//        case LineSpreadType.AutoSpread:
//            cnLineSpreadType = Resources.String_AutoRepeat;
//            break;
//        case LineSpreadType.CentralizationSpread:
//            cnLineSpreadType = Resources.String_CentralizationSpread;
//            break;
//        case LineSpreadType.DecentralizeSpread:
//            cnLineSpreadType = Resources.String_DecentralizeSpread;
//            break;
//        default:
//            break;
//    }
//
//    return cnLineSpreadType;
//}
///// <summary>
///// 线注记分布策略转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static LineSpreadType LineSpreadTypeConvert(String strType)
//{
//    LineSpreadType enLineSpreadType = LineSpreadType.Unknown;
//
//    if (strType == Resources.String_AutoSpread)
//        enLineSpreadType = LineSpreadType.AutoSpread;
//    else if (strType == Resources.String_CentralizationSpread)
//        enLineSpreadType = LineSpreadType.CentralizationSpread;
//    else if (strType == Resources.String_DecentralizeSpread)
//        enLineSpreadType = LineSpreadType.DecentralizeSpread;
//
//    return enLineSpreadType;
//}
//
///// <summary>
///// 线模式转成中文
///// </summary>
///// <param name="linStyle">线模式</param>
///// <returns>中文名称</returns>
//public static String LineStyleConvert(LineStyle linStyle)
//{
//    String cnLineStyle = "";
//    switch (linStyle)
//    {
//        case LineStyle.Arrow:
//            cnLineStyle = Resources.String_ArrowLine;
//            break;
//        case LineStyle.General:
//            cnLineStyle = Resources.String_GeneralMode;
//            break;
//        case LineStyle.Gradual:
//            cnLineStyle = Resources.String_GradualLine;
//            break;
//        case LineStyle.Mark:
//            cnLineStyle = Resources.String_MarkLine;
//            break;
//        case LineStyle.Telegraphic:
//            cnLineStyle = Resources.String_TeleLine;
//            break;
//        default:
//            break;
//    }
//
//    return cnLineStyle;
//}
///// <summary>
///// 线模式转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static LineStyle LineStyleConvert(String strType)
//{
//    LineStyle linStyle = LineStyle.General;
//
//    if (strType == Resources.String_GeneralMode)
//        linStyle = LineStyle.General;
//    else if (strType == Resources.String_ArrowLine)
//        linStyle = LineStyle.Arrow;
//    else if (strType == Resources.String_GradualLine)
//        linStyle = LineStyle.Gradual;
//    else if (strType == Resources.String_MarkLine)
//        linStyle = LineStyle.Mark;
//    else if (strType == Resources.String_TeleLine)
//        linStyle = LineStyle.Telegraphic;
//
//    return linStyle;
//}
//
///// <summary>
///// 线图形参数中特定的线型对应的线型号转换成对应文本
///// </summary>
///// <param name="linType">线型号</param>
///// <returns>线型类型</returns>
//public static String SpecificLinTypeConvert(int linType)
//{
//    String str = "";
//
//    switch (linType)
//    {
//        case -1:
//            str = Resources.String_FullGraLine;
//            break;
//        case -2:
//            str = Resources.String_DottedGraLine;
//            break;
//        case -3:
//            str = Resources.String_DotGraLine;
//            break;
//        case -11:
//            str = Resources.String_TeleLine;
//            break;
//        case -12:
//            str = Resources.String_CommuniLine;
//            break;
//        case -21:
//            str = Resources.String_SameWdithLine;
//            break;
//        case -22:
//            str = Resources.String_GradualLine;
//            break;
//        case -23:
//            str = Resources.String_ThreeLine;
//            break;
//        default:
//            break;
//    }
//
//    return str;
//}
///// <summary>
///// 线图形参数中特定的线型对应文本转换成线型号
///// </summary>
///// <param name="strType">线型类型</param>
///// <returns>线型号</returns>
//public static int SpecificLinTypeConvert(String strType)
//{
//    int i = -1;
//    if (strType == Resources.String_FullGraLine)
//        i = -1;
//    else if (strType == Resources.String_DottedGraLine)
//        i = -2;
//    else if (strType == Resources.String_DotGraLine)
//        i = -3;
//    else if (strType == Resources.String_TeleLine)
//        i = -11;
//    else if (strType == Resources.String_CommuniLine)
//        i = -12;
//    else if (strType == Resources.String_SameWdithLine)
//        i = -21;
//    else if (strType == Resources.String_GradualLine)
//        i = -22;
//    else if (strType == Resources.String_ThreeLine)
//        i = -23;
//
//    return i;
//}
//
///// <summary>
///// 电力线修饰点形状枚举定义转成中文
///// </summary>
///// <param name="ldpType">线头类型枚举</param>
///// <returns>中文名称</returns>
//public static String LinDePntTypeConvert(LinDePntType ldpType)
//{
//    String cnLDPType = Resources.String_None;
//    switch (ldpType)
//    {
//        case LinDePntType.Arrow:
//            cnLDPType = Resources.String_SingleArrow;
//            break;
//        case LinDePntType.DoubleArrow:
//            cnLDPType = Resources.String_DoubleArrow;
//            break;
//        default:
//            break;
//    }
//
//    return cnLDPType;
//}
///// <summary>
///// 电力线修饰点形状字符串转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static LinDePntType LinDePntTypeConvert(String strType)
//{
//    LinDePntType ldpType = LinDePntType.None;
//
//    if (strType == Resources.String_SingleArrow)
//        ldpType = LinDePntType.Arrow;
//    else if (strType == Resources.String_DoubleArrow)
//        ldpType = LinDePntType.DoubleArrow;
//
//    return ldpType;
//}
//
///// <summary>
///// 线头类型枚举定义转成中文
///// </summary>
///// <param name="linHeadType">线头类型枚举</param>
///// <returns>中文名称</returns>
//public static String LinHeadTypeConvert(LinHeadType linHeadType)
//{
//    String cnLinHeadType = "";
//
//    switch (linHeadType)
//    {
//        case LinHeadType.Butt:
//            cnLinHeadType = Resources.String_ButtHead;
//            break;
//        case LinHeadType.Round:
//            cnLinHeadType = Resources.String_RoundHead;
//            break;
//        case LinHeadType.Square:
//            cnLinHeadType = Resources.String_SquareHead;
//            break;
//        default:
//            break;
//    }
//
//    return cnLinHeadType;
//}
///// <summary>
///// 线尾类型枚举定义转成中文
///// </summary>
///// <param name="linHeadType">线尾类型枚举</param>
///// <returns>中文名称</returns>
//public static String LinTailTypeConvert(LinHeadType linHeadType)
//{
//    String cnLinHeadType = "";
//
//    switch (linHeadType)
//    {
//        case LinHeadType.Butt:
//            cnLinHeadType = Resources.String_SharpHead;
//            break;
//        case LinHeadType.Round:
//            cnLinHeadType = Resources.String_RoundHead;
//            break;
//        case LinHeadType.Square:
//            cnLinHeadType = Resources.String_SquareHead;
//            break;
//        default:
//            break;
//    }
//
//    return cnLinHeadType;
//}
///// <summary>
///// 线头/尾类型枚举定义转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static LinHeadType LinHeadTypeConvert(String strType)
//{
//    LinHeadType enLinHeadType = LinHeadType.Butt;
//
//    if (strType == Resources.String_ButtHead || strType == Resources.String_SharpHead)
//        enLinHeadType = LinHeadType.Butt;
//    else if (strType == Resources.String_RoundHead)
//        enLinHeadType = LinHeadType.Round;
//    else if (strType == Resources.String_SquareHead)
//        enLinHeadType = LinHeadType.Square;
//
//    return enLinHeadType;
//}
//
///// <summary>
///// 线拐角类型枚举转成中文
///// </summary>
///// <param name="linJointType">线拐角类型枚举</param>
///// <returns>中文名称</returns>
//public static String LinJointTypeConvert(LinJointType linJointType)
//{
//    String cnLinJointType = "";
//
//    switch (linJointType)
//    {
//        case LinJointType.Butt:
//            cnLinJointType = Resources.String_Butt;
//            break;
//        case LinJointType.Round:
//            cnLinJointType = Resources.String_Round;
//            break;
//        case LinJointType.Square:
//            cnLinJointType = Resources.String_Square;
//            break;
//        default:
//            break;
//    }
//
//    return cnLinJointType;
//}
///// <summary>
///// 线拐角类型枚举转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static LinJointType LinJointTypeConvert(String strType)
//{
//    LinJointType enLinJointType = LinJointType.Butt;
//
//    if (strType == Resources.String_Butt)
//        enLinJointType = LinJointType.Butt;
//    else if (strType == Resources.String_Round)
//        enLinJointType = LinJointType.Round;
//    else if (strType == Resources.String_Square)
//        enLinJointType = LinJointType.Square;
//
//    return enLinJointType;
//}
//
///// <summary>
///// 电力线控制点形状枚举转成中文
///// </summary>
///// <param name="lpType">电力线控制点形状枚举</param>
///// <returns>中文名称</returns>
//public static String LinPntTypeConvert(LinPntType lpType)
//{
//    String cnLPType = "";
//    switch (lpType)
//    {
//        case LinPntType.Circle:
//            cnLPType = Resources.String_SolidRound;
//            break;
//        case LinPntType.None:
//            cnLPType = Resources.String_None;
//            break;
//        case LinPntType.UnCircle:
//            cnLPType = Resources.String_HollowRound;
//            break;
//        default:
//            break;
//    }
//
//    return cnLPType;
//}
///// <summary>
///// 电力线控制点形状字符串转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static LinPntType LinPntTypeConvert(String strType)
//{
//    LinPntType lpType = LinPntType.None;
//
//    if (strType == Resources.String_SolidRound)
//        lpType = LinPntType.Circle;
//    else if (strType == Resources.String_HollowRound)
//        lpType = LinPntType.UnCircle;
//
//    return lpType;
//}
//
///// <summary>
///// 线型生成方法转成中文
///// </summary>
///// <param name="linStyleMakeType">线型生成方法</param>
///// <returns>中文名称</returns>
//public static String LinStyleMakeTypeConvert(LinStyleMakeType linStyleMakeType)
//{
//    String cnLinStyleMakeType = "";
//
//    switch (linStyleMakeType)
//    {
//        case LinStyleMakeType.Bypoint:
//            cnLinStyleMakeType = Resources.String_ByPoint;
//            break;
//        case LinStyleMakeType.Byrule:
//            cnLinStyleMakeType = Resources.String_ByRule;
//            break;
//        default:
//            break;
//    }
//
//    return cnLinStyleMakeType;
//}
///// <summary>
///// 线型生成方法转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static LinStyleMakeType LinStyleMakeTypeConvert(String strType)
//{
//    LinStyleMakeType enLinStyleMakeType = LinStyleMakeType.Bypoint;
//
//    if (strType == Resources.String_ByPoint)
//        enLinStyleMakeType = LinStyleMakeType.Bypoint;
//    else if (strType == Resources.String_ByRule)
//        enLinStyleMakeType = LinStyleMakeType.Byrule;
//
//    return enLinStyleMakeType;
//}
//
///// <summary>
///// 线类型转成中文
///// </summary>
///// <param name="linType">线类型</param>
///// <returns>中文名称</returns>
//public static String LinTypeConvert(LineType linType)
//{
//    String cnLinType = "";
//
//    switch (linType)
//    {
//        case LineType.BreakLin:
//            cnLinType = Resources.String_BreakLin;
//            break;
//        case LineType.SmoothLin:
//            cnLinType = Resources.String_SmoothLin;
//            break;
//        default:
//            break;
//    }
//    return cnLinType;
//}
///// <summary>
///// 线类型转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static LineType LinTypeConvert(String strType)
//{
//    LineType intLinType = LineType.BreakLin;
//
//    if (strType == Resources.String_BreakLin)
//        intLinType = LineType.BreakLin;
//    else if (strType == Resources.String_SmoothLin)
//        intLinType = LineType.SmoothLin;
//
//    return intLinType;
//}
//
///// <summary>
///// 地图图框的生成方式转成中文
///// </summary>
///// <param name="mapFrameGenMode">地图图框的生成方式</param>
///// <returns>中文名称</returns>
//public static String MapFrameGenModeConvert(int mapFrameGenMode)
//{
//    String cnMapFrameGenMode = "";
//
//    switch (mapFrameGenMode)
//    {
//        case 1:
//            cnMapFrameGenMode = Resources.String_ByLongLat;
//            break;
//        case 2:
//            cnMapFrameGenMode = Resources.String_ByFieldDis;
//            break;
//        case 3:
//            cnMapFrameGenMode = Resources.String_BySpecifedRowCol;
//            break;
//        default:
//            break;
//    }
//
//    return cnMapFrameGenMode;
//}
///// <summary>
///// 地图图框的生成方式转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static int MapFrameGenModeConvert(String strType)
//{
//    int intMapFrameGenMode = 1;
//
//    if (strType == Resources.String_ByLongLat)
//        intMapFrameGenMode = 1;
//    else if (strType == Resources.String_ByFieldDis)
//        intMapFrameGenMode = 2;
//    else if (strType == Resources.String_BySpecifedRowCol)
//        intMapFrameGenMode = 3;
//
//    return intMapFrameGenMode;
//}
//
///// <summary>
///// 地图集目录显示类型转成中文
///// </summary>
///// <param name="mapSetCatType">地图集目录显示类型</param>
///// <returns>中文名称</returns>
//public static String MapSetCatTypeConvert(MapSetCatType mapSetCatType)
//{
//    String cnMapSetCatType = "";
//
//    switch (mapSetCatType)
//    {
//        case MapSetCatType.ByFrm:
//            cnMapSetCatType = Resources.String_ByFrm;
//            break;
//        case MapSetCatType.ByLay:
//            cnMapSetCatType = Resources.String_ByLay;
//            break;
//        default:
//            break;
//    }
//
//    return cnMapSetCatType;
//}
///// <summary>
///// 地图集目录显示类型转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static MapSetCatType MapSetCatTypeConvert(String strType)
//{
//    MapSetCatType enMapSetCatType = MapSetCatType.ByFrm;
//
//    if (strType == Resources.String_ByFrm)
//        enMapSetCatType = MapSetCatType.ByFrm;
//    else if (strType == Resources.String_ByLay)
//        enMapSetCatType = MapSetCatType.ByLay;
//
//    return enMapSetCatType;
//}
//
///// <summary>
///// 地图集显示模式转成中文
///// </summary>
///// <param name="mapSetDspMode">地图集显示模式</param>
///// <returns>中文名称</returns>
//public static String MapSetDspModeConvert(MapSetDspMode mapSetDspMode)
//{
//    String cnMapSetDspMode = "";
//
//    switch (mapSetDspMode)
//    {
//        case MapSetDspMode.AllDsp:
//            cnMapSetDspMode = Resources.String_AllDsp;
//            break;
//        case MapSetDspMode.DataDsp:
//            cnMapSetDspMode = Resources.String_DataDisplay;
//            break;
//        case MapSetDspMode.TableDsp:
//            cnMapSetDspMode = Resources.String_TableDsp;
//            break;
//        default:
//            break;
//    }
//
//    return cnMapSetDspMode;
//}
///// <summary>
///// 地图集显示模式转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static MapSetDspMode MapSetDspModeConvert(String strType)
//{
//    MapSetDspMode enMapSetDspMode = MapSetDspMode.AllDsp;
//
//    if (strType == Resources.String_AllDsp)
//        enMapSetDspMode = MapSetDspMode.AllDsp;
//    else if (strType == Resources.String_DataDisplay)
//        enMapSetDspMode = MapSetDspMode.DataDsp;
//    else if (strType == Resources.String_TableDsp)
//        enMapSetDspMode = MapSetDspMode.TableDsp;
//
//    return enMapSetDspMode;
//}
//
///// <summary>
///// 地图集的类型转成中文
///// </summary>
///// <param name="mapSetType">地图集的类型</param>
///// <returns>中文名称</returns>
//public static String MapSetTypeConvert(int mapSetType)
//{
//    String cnMapSetType = "";
//
//    switch (mapSetType)
//    {
//        case 1:
//            cnMapSetType = Resources.String_CombinedLayer;
//            break;
//        case 2:
//            cnMapSetType = Resources.String_UnCombinedLayer;
//            break;
//        default:
//            break;
//    }
//
//    return cnMapSetType;
//}
///// <summary>
///// 地图集的类型转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static int MapSetTypeConvert(String strType)
//{
//    int intMapSetType = 1;
//
//    if (strType == Resources.String_CombinedLayer)
//        intMapSetType = 1;
//    else if (strType == Resources.String_UnCombinedLayer)
//        intMapSetType = 2;
//
//    return intMapSetType;
//}
//
///// <summary>
///// 点注记分布类型转成中文
///// </summary>
///// <param name="pointPlaceType">点注记分布类型</param>
///// <returns>中文名称</returns>
//public static String PointPlaceTypeConvert(PointPlaceType pointPlaceType)
//{
//    String cnPointPlaceType = "";
//
//    switch (pointPlaceType)
//    {
//        case PointPlaceType.AnyLocationPlace:
//            cnPointPlaceType = Resources.String_AnyAngle;
//            break;
//        case PointPlaceType.EightLocationPlace:
//            cnPointPlaceType = Resources.String_EightLocationPlace;
//            break;
//        case PointPlaceType.OnPointSymbolPlace:
//            cnPointPlaceType = Resources.String_OnPointSymbolPlace;
//            break;
//        default:
//            break;
//    }
//
//    return cnPointPlaceType;
//}
///// <summary>
///// 点注记分布类型转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static PointPlaceType PointPlaceTypeConvert(String strType)
//{
//    PointPlaceType enPointPlaceType = PointPlaceType.Unknown;
//
//    if (strType == Resources.String_AnyAngle)
//        enPointPlaceType = PointPlaceType.AnyLocationPlace;
//    else if (strType == Resources.String_EightLocationPlace)
//        enPointPlaceType = PointPlaceType.EightLocationPlace;
//    else if (strType == Resources.String_OnPointSymbolPlace)
//        enPointPlaceType = PointPlaceType.OnPointSymbolPlace;
//
//    return enPointPlaceType;
//}
//

    /**
     * 像元值类型转成中文
     *
     * @param rasDataType 像元值类型
     * @return 中文名称
     */
    public static String pixelTypeConvert(PixelType rasDataType)
    {
        String cnRasDataType = "";

        if (PixelType.Bit.equals(rasDataType))
        {
            cnRasDataType = "1位二值图的数据";
        } else if (PixelType.Byte.equals(rasDataType))
        {
            cnRasDataType = "8位无符号整数";
        } else if (PixelType.CFloat32.equals(rasDataType))
        {
            cnRasDataType = "32位浮点复数";
        } else if (PixelType.CFloat64.equals(rasDataType))
        {
            cnRasDataType = "64位浮点复数";
        } else if (PixelType.CInt16.equals(rasDataType))
        {
            cnRasDataType = "16位整型复数";
        } else if (PixelType.CInt32.equals(rasDataType))
        {
            cnRasDataType = "32位整型复数";
        } else if (PixelType.Float32.equals(rasDataType))
        {
            cnRasDataType = "32位浮点数据";
        } else if (PixelType.Float64.equals(rasDataType))
        {
            cnRasDataType = "64位浮点数据";
        } else if (PixelType.Int16.equals(rasDataType))
        {
            cnRasDataType = "16位有符号整数";
        } else if (PixelType.Int32.equals(rasDataType))
        {
            cnRasDataType = "32位有符号整数";
        } else if (PixelType.Int8.equals(rasDataType))
        {
            cnRasDataType = "8位有符号整数";
        } else if (PixelType.UInt16.equals(rasDataType))
        {
            cnRasDataType = "16位无符号整数";
        } else if (PixelType.UInt32.equals(rasDataType))
        {
            cnRasDataType = "32位无符号整数";
        }

        return cnRasDataType;
    }

    /**
     * 像元值类型转成枚举值
     *
     * @param strType 中文名称
     * @return 枚举值
     */
    public static PixelType pixelTypeConvert(String strType)
    {
        PixelType enRasDataType = PixelType.Unknown;

        if ("1位二值图的数据".equals(strType))
        {
            enRasDataType = PixelType.Bit;
        } else if ("8位无符号整数".equals(strType))
        {
            enRasDataType = PixelType.Byte;
        } else if ("32位浮点复数".equals(strType))
        {
            enRasDataType = PixelType.CFloat32;
        } else if ("64位浮点复数".equals(strType))
        {
            enRasDataType = PixelType.CFloat64;
        } else if ("16位整型复数".equals(strType))
        {
            enRasDataType = PixelType.CInt16;
        } else if ("32位整型复数".equals(strType))
        {
            enRasDataType = PixelType.CInt32;
        } else if ("32位浮点数据".equals(strType))
        {
            enRasDataType = PixelType.Float32;
        } else if ("64位浮点数据".equals(strType))
        {
            enRasDataType = PixelType.Float64;
        } else if ("16位有符号整数".equals(strType))
        {
            enRasDataType = PixelType.Int16;
        } else if ("32位有符号整数".equals(strType))
        {
            enRasDataType = PixelType.Int32;
        } else if ("8位有符号整数".equals(strType))
        {
            enRasDataType = PixelType.Int8;
        } else if ("16位无符号整数".equals(strType))
        {
            enRasDataType = PixelType.UInt16;
        } else if ("32位无符号整数".equals(strType))
        {
            enRasDataType = PixelType.UInt32;
        }

        return enRasDataType;
    }

    //
///// <summary>
///// 镶嵌数据集产品定义类型转名称
///// </summary>
///// <param name="rasProductDefineType"></param>
///// <returns></returns>
//public static String RasProductDefineConvert(RasProductDefine rasProductDefineType)
//{
//    String enProductDefine = "";
//    switch (rasProductDefineType)
//    {
//        case RasProductDefine.RAS_PRODUECT_NONE:
//            enProductDefine = Resources.String_Undefined;
//            break;
//        case RasProductDefine.RAS_PRODUECT_NATURAL_COLOR_RGB:
//            enProductDefine = Resources.String_TrueColor;
//            break;
//        case RasProductDefine.RAS_PRODUECT_FALE_COLOR_RGB:
//            enProductDefine = Resources.String_FalseColor;
//            break;
//        case RasProductDefine.RAS_PRODUECT_CUSTOM:
//            enProductDefine = Resources.String_Custom1;
//            break;
//        default:
//            enProductDefine = Resources.String_Undefined;
//            break;
//    }
//    return enProductDefine;
//}
//
///// <summary>
///// 镶嵌数据集名称转产品定义类型
///// </summary>
///// <param name="strType"></param>
///// <returns></returns>
//public static RasProductDefine RasProductDefineConvert(String strType)
//{
//    RasProductDefine productDefine = RasProductDefine.RAS_PRODUECT_NONE;
//
//    if (strType == Resources.String_Undefined)
//    {
//        productDefine = RasProductDefine.RAS_PRODUECT_NONE;
//    }
//    else if (strType == Resources.String_TrueColor)
//    {
//        productDefine = RasProductDefine.RAS_PRODUECT_NATURAL_COLOR_RGB;
//    }
//    else if (strType == Resources.String_FalseColor)
//    {
//        productDefine = RasProductDefine.RAS_PRODUECT_FALE_COLOR_RGB;
//    }
//    else if (strType == Resources.String_Custom1)
//    {
//        productDefine = RasProductDefine.RAS_PRODUECT_CUSTOM;
//    }
//    else
//    {
//        productDefine = RasProductDefine.RAS_PRODUECT_CUSTOM;//其他名称统一视为自定义
//    }
//    return productDefine;
//}
//
///// <summary>
///// 镶嵌方法类型转名称
///// </summary>
///// <param name="mosaicMethodType"></param>
///// <returns></returns>
//public static String RasSortMosaicMethod(RasSortMethod mosaicMethodType)
//{
//    String cnMosaicMethodType = "";
//    switch (mosaicMethodType)
//    {
//        case RasSortMethod.RAS_SORT_NONE:
//            cnMosaicMethodType = Resources.String_None;
//            break;
//        case RasSortMethod.RAS_SORT_Center:
//            cnMosaicMethodType = Resources.String_ByCenter;
//            break;
//        case RasSortMethod.RAS_SORT_Nadir:
//            cnMosaicMethodType = Resources.String_Nadir;
//            break;
//        case RasSortMethod.RAS_SORT_Viewpoint:
//            cnMosaicMethodType = Resources.String_ViewPoint;
//            break;
//        case RasSortMethod.RAS_SORT_Attribute:
//            cnMosaicMethodType = Resources.String_ByAttributes;
//            break;
//        case RasSortMethod.RAS_SORT_NorthWest:
//            cnMosaicMethodType = Resources.String_InTheNorthwestDirection;
//            break;
//        case RasSortMethod.RAS_SORT_Seamline:
//            cnMosaicMethodType = Resources.String_SeamLine;
//            break;
//        default:
//            break;
//    }
//
//    return cnMosaicMethodType;
//}
//
///// <summary>
///// 镶嵌方法名称转类型
///// </summary>
//public static RasSortMethod RasSortMosaicMethod(String strType)
//{
//    RasSortMethod mosaicMethod = RasSortMethod.RAS_SORT_NONE;
//    if (strType == Resources.String_None)
//    {
//        mosaicMethod = RasSortMethod.RAS_SORT_NONE;
//    }
//    if (strType == Resources.String_ByCenter)
//    {
//        mosaicMethod = RasSortMethod.RAS_SORT_Center;
//    }
//    if (strType == Resources.String_Nadir)
//    {
//        mosaicMethod = RasSortMethod.RAS_SORT_Nadir;
//    }
//    if (strType == Resources.String_ViewPoint)
//    {
//        mosaicMethod = RasSortMethod.RAS_SORT_Viewpoint;
//    }
//    if (strType == Resources.String_ByAttributes)
//    {
//        mosaicMethod = RasSortMethod.RAS_SORT_Attribute;
//    }
//    if (strType == Resources.String_InTheNorthwestDirection)
//    {
//        mosaicMethod = RasSortMethod.RAS_SORT_NorthWest;
//    }
//    if (strType == Resources.String_SeamLine)
//    {
//        mosaicMethod = RasSortMethod.RAS_SORT_Seamline;
//    }
//    return mosaicMethod;
//}
//
//
///// <summary>
///// 镶嵌运算符类型转名称
///// </summary>
///// <param name="mosiacOperatorType"></param>
///// <returns></returns>
//public static String RasMoaicOperatorConvert(RasMosaicMethod mosiacOperatorType)
//{
//    String cnMosiacOperatorType = "";
//    switch (mosiacOperatorType)
//    {
//        case RasMosaicMethod.RAS_MOSAIC_First:
//            cnMosiacOperatorType = Resources.String_First;
//            break;
//        case RasMosaicMethod.RAS_MOSAIC_Last:
//            cnMosiacOperatorType = Resources.String_Last;
//            break;
//        case RasMosaicMethod.RAS_MOSAIC_Min:
//            cnMosiacOperatorType = Resources.String_Minimum;
//            break;
//        case RasMosaicMethod.RAS_MOSAIC_Max:
//            cnMosiacOperatorType =Resources.String_Maximum;
//            break;
//        case RasMosaicMethod.RAS_MOSAIC_Mean:
//            cnMosiacOperatorType = Resources.String_Average;
//            break;
//        case RasMosaicMethod.RAS_MOSAIC_Blend:
//            cnMosiacOperatorType = Resources.String_Fuse;
//            break;
//        default:
//            break;
//    }
//    return cnMosiacOperatorType;
//}
///// <summary>
///// 镶嵌运算符名称转类型
///// </summary>
///// <param name="strType"></param>
///// <returns></returns>
//public static RasMosaicMethod RasMoaicOperatorConvert(String strType)
//{
//    RasMosaicMethod mosaicOperator = RasMosaicMethod.RAS_MOSAIC_First;
//    if (strType == Resources.String_First)
//    {
//        mosaicOperator = RasMosaicMethod.RAS_MOSAIC_First;
//    }
//    if (strType == Resources.String_Last)
//    {
//        mosaicOperator = RasMosaicMethod.RAS_MOSAIC_Last;
//    }
//    if (strType == Resources.String_Minimum)
//    {
//        mosaicOperator = RasMosaicMethod.RAS_MOSAIC_Min;
//    }
//    if (strType == Resources.String_Maximum)
//    {
//        mosaicOperator = RasMosaicMethod.RAS_MOSAIC_Max;
//    }
//    if (strType == Resources.String_Average)
//    {
//        mosaicOperator = RasMosaicMethod.RAS_MOSAIC_Mean;
//    }
//    if (strType == Resources.String_Fuse)
//    {
//        mosaicOperator = RasMosaicMethod.RAS_MOSAIC_Blend;
//    }
//    return mosaicOperator;
//}
//
///// <summary>
///// 栅格数据空间参考的编辑方式转成中文
///// </summary>
///// <param name="rasRefInfoEditMode">栅格数据空间参考的编辑方式</param>
///// <returns>中文名称</returns>
//public static String RasRefInfoEditModeConvert(int rasRefInfoEditMode)
//{
//    String cnRasRefInfoEditMode = "";
//
//    switch (rasRefInfoEditMode)
//    {
//        case 0:
//            cnRasRefInfoEditMode = Resources.String_ModifyParam;
//            break;
//        case 1:
//            cnRasRefInfoEditMode = Resources.String_ReplaceReference;
//            break;
//        case 2:
//            cnRasRefInfoEditMode = Resources.String_DeleteReference;
//            break;
//        default:
//            break;
//    }
//
//    return cnRasRefInfoEditMode;
//}
///// <summary>
///// 栅格数据空间参考的编辑方式转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static int RasRefInfoEditModeConvert(String strType)
//{
//    int intRasRefInfoEditMode = 0;
//
//    if (strType == Resources.String_ModifyParam)
//        intRasRefInfoEditMode = 0;
//    else if (strType == Resources.String_ReplaceReference)
//        intRasRefInfoEditMode = 1;
//    else if (strType == Resources.String_DeleteReference)
//        intRasRefInfoEditMode = 2;
//
//    return intRasRefInfoEditMode;
//}
//
///// <summary>
///// 区图层属性 填充模式 转成中文
///// </summary>
///// <param name="regFillMode">区填充模式</param>
///// <returns>中文名称</returns>
//public static String RegFillModeConvert(int regFillMode)
//{
//    String cnRegFillMode = "";
//
//    switch (regFillMode)
//    {
//        case 0:
//            cnRegFillMode = Resources.String_Region;
//            break;
//        case 1:
//            cnRegFillMode = Resources.String_EdgeLine;
//            break;
//        case 2:
//            cnRegFillMode = Resources.String_egionAndEdgeLine;
//            break;
//        default:
//            break;
//    }
//
//    return cnRegFillMode;
//}
///// <summary>
///// 区填充模式转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static int RegFillModeConvert(String strType)
//{
//    int intRegFillMode = 0;
//
//    if (strType == Resources.String_Region)
//        intRegFillMode = 0;
//    else if (strType == Resources.String_EdgeLine)
//        intRegFillMode = 1;
//    else if (strType == Resources.String_egionAndEdgeLine)
//        intRegFillMode = 2;
//
//    return intRegFillMode;
//}
//
///// <summary>
///// 区图元参数 填充模式 转成中文
///// </summary>
///// <param name="fillMode">焦点模式</param>
///// <returns>中文名称</returns>
//public static String RegPatFillModeConvert(int fillMode)
//{
//    String cnFillMode = "";
//
//    switch (fillMode)
//    {
//        case 0:
//            cnFillMode = Resources.String_CommonGradient;
//            break;
//        case 1:
//            cnFillMode = Resources.String_LinearGradient;
//            break;
//        case 2:
//            cnFillMode = Resources.String_RectangularGradient;
//            break;
//        case 3:
//            cnFillMode = Resources.String_CircleGradient;
//            break;
//        default:
//            break;
//    }
//
//    return cnFillMode;
//}
///// <summary>
///// 区填充模式转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static int RegPatFillModeConvert(String strType)
//{
//    int intFillMode = 0;
//
//    if (strType == Resources.String_CommonGradient)
//        intFillMode = 0;
//    else if (strType == Resources.String_LinearGradient)
//        intFillMode = 1;
//    else if (strType == Resources.String_RectangularGradient)
//        intFillMode = 2;
//    else if (strType == Resources.String_CircleGradient)
//        intFillMode = 3;
//
//    return intFillMode;
//}
//
///// <summary>
///// 面注记分布类型转成中文
///// </summary>
///// <param name="regPlaceType">面注记分布类型</param>
///// <returns>中文名称</returns>
//public static String RegPlaceTypeConvert(RegPlaceType regPlaceType)
//{
//    String cnRegPlaceType = "";
//
//    switch (regPlaceType)
//    {
//        case RegPlaceType.BoundrayPlace:
//            cnRegPlaceType = Resources.String_BoundrayPlace;
//            break;
//        case RegPlaceType.CurvedPlace:
//            cnRegPlaceType = Resources.String_CurvedPlace;
//            break;
//        case RegPlaceType.HorizationPlace:
//            cnRegPlaceType = Resources.String_HorizationPlace;
//            break;
//        case RegPlaceType.OutsidePlace:
//            cnRegPlaceType = Resources.String_OutsidePlace;
//            break;
//        case RegPlaceType.StraightPlace:
//            cnRegPlaceType = Resources.String_StraightPlace;
//            break;
//        default:
//            break;
//    }
//
//    return cnRegPlaceType;
//}
///// <summary>
///// 面注记分布类型转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static RegPlaceType RegPlaceTypeConvert(String strType)
//{
//    RegPlaceType enRegPlaceType = RegPlaceType.Unknown;
//
//    if (strType == Resources.String_BoundrayPlace)
//        enRegPlaceType = RegPlaceType.BoundrayPlace;
//    else if (strType == Resources.String_CurvedPlace)
//        enRegPlaceType = RegPlaceType.CurvedPlace;
//    else if (strType == Resources.String_HorizationPlace)
//        enRegPlaceType = RegPlaceType.HorizationPlace;
//    else if (strType == Resources.String_OutsidePlace)
//        enRegPlaceType = RegPlaceType.OutsidePlace;
//    else if (strType == Resources.String_StraightPlace)
//        enRegPlaceType = RegPlaceType.StraightPlace;
//
//    return enRegPlaceType;
//}
//
/// <summary>
/// 关系类的关系映射类型转成中文
/// </summary>
/// <param name="relCardType">关系类的关系映射类型</param>
/// <returns>中文名称</returns>
    public static String relCardTypeConvert(RelCard relCardType)
    {
        String cnRelCardType = "";
        if (RelCard.RelCard1_1.equals(relCardType))
        {
            cnRelCardType = "一对一";
        } else if (RelCard.RelCard1_M.equals(relCardType))
        {
            cnRelCardType = "一对多";
        } else if (RelCard.RelCardN_M.equals(relCardType))
        {
            cnRelCardType = "多对多";
        }
        return cnRelCardType;
    }

    /// <summary>
/// 关系类的关系映射类型转成枚举值
/// </summary>
/// <param name="strType">中文名称</param>
/// <returns>枚举值</returns>
    public static RelCard relCardTypeConvert(String strType)
    {
        RelCard enRelCardType = RelCard.RelCard1_1;

        if (strType == "一对一")
        {
            enRelCardType = RelCard.RelCard1_1;
        } else if (strType == "一对多")
        {
            enRelCardType = RelCard.RelCard1_M;
        } else if (strType == "多对多")
        {
            enRelCardType = RelCard.RelCardN_M;
        }

        return enRelCardType;
    }

    /**
     * 关系类的关系通知类型转成中文
     *
     * @param relNotifyType 关系类的关系通知类型
     * @return 中文名称
     */
    public static String relNotifyTypeConvert(RelNotify relNotifyType)
    {
        String cnRelNotifyType = "";
        if (RelNotify.RelNone.equals(relNotifyType))
        {
            cnRelNotifyType = "不通知";
        } else if (RelNotify.Relforward.equals(relNotifyType))
        {
            cnRelNotifyType = "向前通知";
        } else if (RelNotify.RelBackward.equals(relNotifyType))
        {
            cnRelNotifyType = "向后通知";
        } else if (RelNotify.RelBoth.equals(relNotifyType))
        {
            cnRelNotifyType = "前后通知";
        }
        return cnRelNotifyType;
    }

    /**
     * 关系类的关系通知类型字符串转成枚举
     *
     * @param strType 中文名称
     * @return 枚举值
     */
    public static RelNotify relNotifyTypeConvert(String strType)
    {
        RelNotify enRelNotifyType = RelNotify.RelNone;
        if (strType == "不通知")
        {
            enRelNotifyType = RelNotify.RelNone;
        } else if (strType == "向前通知")
        {
            enRelNotifyType = RelNotify.Relforward;
        } else if (strType == "向后通知")
        {
            enRelNotifyType = RelNotify.RelBackward;
        } else if (strType == "前后通知")
        {
            enRelNotifyType = RelNotify.RelBoth;
        }
        return enRelNotifyType;
    }


    /**
     * 关系类的关系类型转成中文
     *
     * @param relType 关系类的关系类型
     * @return 中文名称
     */
    public static String relTypeConvert(RelType relType)
    {
        String cnRelType = "";
        if (RelType.RelAssociate.equals(relType))
        {
            cnRelType = "关联关系";
        } else if (RelType.RelComposite.equals(relType))
        {
            cnRelType = "组合关系";
        } else if (RelType.RelDependence.equals(relType))
        {
            cnRelType = "依赖关系";
        } else if (RelType.RelInherited.equals(relType))
        {
            cnRelType = "继承关系";
        } else if (RelType.Relmeta.equals(relType))
        {
            cnRelType = "元关系";
        }
        return cnRelType;
    }

    /**
     * 关系类的关系类型字符串转成枚举
     *
     * @param strType 中文名称
     * @return 枚举值
     */
    public static RelType relTypeConvert(String strType)
    {
        RelType enRelType = RelType.RelAssociate;

        if (strType == "关联关系")
        {
            enRelType = RelType.RelAssociate;
        } else if (strType == "组合关系")
        {
            enRelType = RelType.RelComposite;
        } else if (strType == "依赖关系")
        {
            enRelType = RelType.RelDependence;
        } else if (strType == "继承关系")
        {
            enRelType = RelType.RelInherited;
        } else if (strType == "元关系")
        {
            enRelType = RelType.Relmeta;
        }

        return enRelType;
    }
//
///// <summary>
///// 制图表达_道路时的绘制方式转成中文
///// </summary>
///// <param name="roadPaintStyle">制图表达_道路时的绘制方式</param>
///// <returns>中文名称</returns>
//public static String RoadPaintStyleConvert(int roadPaintStyle)
//{
//    String cnRoadPaintStyle = "";
//
//    switch (roadPaintStyle)
//    {
//        case 0:
//            cnRoadPaintStyle = Resources.String_Simple;
//            break;
//        case 1:
//            cnRoadPaintStyle = Resources.String_Theme;
//            break;
//        case 2:
//            cnRoadPaintStyle = Resources.String_Param;
//            break;
//        default:
//            break;
//    }
//
//    return cnRoadPaintStyle;
//}
///// <summary>
///// 制图表达_道路时的绘制方式转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static int RoadPaintStyleConvert(String strType)
//{
//    int intRoadPaintStyle = 0;
//
//    if (strType == Resources.String_Simple)
//        intRoadPaintStyle = 0;
//    else if (strType == Resources.String_Theme)
//        intRoadPaintStyle = 1;
//    else if (strType == Resources.String_Param)
//        intRoadPaintStyle = 2;
//
//    return intRoadPaintStyle;
//}
//
///// <summary>
///// 按指定行列划分生成图框时的行列间距转成中文
///// </summary>
///// <param name="rowColSpacing">按指定行列划分生成图框时的行列间距</param>
///// <returns>中文名称</returns>
//public static String RowColSpacingConvert(int rowColSpacing)
//{
//    String cnRowColSpacing = "";
//
//    switch (rowColSpacing)
//    {
//        case 1:
//            cnRowColSpacing = Resources.String_ByRowCol;
//            break;
//        case 2:
//            cnRowColSpacing = Resources.String_BySpacing;
//            break;
//        default:
//            break;
//    }
//
//    return cnRowColSpacing;
//}
///// <summary>
///// 按指定行列划分生成图框时的行列间距转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static int RowColSpacingConvert(String strType)
//{
//    int intRowColSpacing = 1;
//
//    if (strType == Resources.String_ByRowCol)
//        intRowColSpacing = 1;
//    else if (strType == Resources.String_BySpacing)
//        intRowColSpacing = 2;
//
//    return intRowColSpacing;
//}
//
//
//
///// <summary>
///// 网络权类型转换成中文文字
///// </summary>
///// <param name="wgtType">网络权类型</param>
///// <returns>中文名称</returns>
//public static String WgtTypeConvert(WgtType wgtType)
//{
//    String cnWgtType = "";
//
//    switch (wgtType)
//    {
//        case WgtType.Ratio:
//            cnWgtType = Resources.String_RatioWeight;
//            break;
//        case WgtType.Absolute:
//            cnWgtType = Resources.String_AbsoluteWeight;
//            break;
//        default:
//            break;
//    }
//
//    return cnWgtType;
//}
///// <summary>
///// 网络权类型中文名称转成枚举值
///// </summary>
///// <param name="strType">中文名称</param>
///// <returns>枚举值</returns>
//public static WgtType WgtTypeConvert(String strType)
//{
//    WgtType wgtType = WgtType.Ratio;
//
//    if (strType == Resources.String_RatioWeight)
//        wgtType = WgtType.Ratio;
//    else if (strType == Resources.String_AbsoluteWeight)
//        wgtType = WgtType.Absolute;
//
//    return wgtType;
//}
//
///// <summary>
///// 网络权数据类型转换成中文文字
///// </summary>
///// <param name="type">网络权类型</param>
///// <returns>中文名称</returns>
//public static String WgtDataTypeConvert(WgtDataType type)
//{
//    String strType = Resources.String_Unknown;
//    switch (type)
//    {
//        case WgtDataType.WgtShort:
//            strType = Resources.String_Short;
//            break;
//        case WgtDataType.WgtLong:
//            strType = Resources.String_Long;
//            break;
//        case WgtDataType.WgtFloat:
//            strType = Resources.String_Float;
//            break;
//        case WgtDataType.WgtDouble:
//            strType = Resources.String_Double;
//            break;
//        case WgtDataType.WgtBit:
//            strType = Resources.String_32Binary;
//            break;
//        default:
//            break;
//    }
//    return strType;
//}
///// <summary>
///// 网络权数据类型中文名称转成枚举值
///// </summary>
///// <param name="str">中文名称</param>
///// <returns>枚举值</returns>
//public static WgtDataType WgtDataTypeConvert(String str)
//{
//    if (str == Resources.String_Short)
//        return WgtDataType.WgtShort;
//    else if (str == Resources.String_Long)
//        return WgtDataType.WgtLong;
//    else if (str == Resources.String_Float)
//        return WgtDataType.WgtFloat;
//    else if (str == Resources.String_Double)
//        return WgtDataType.WgtDouble;
//    else if (str == Resources.String_32Binary)
//        return WgtDataType.WgtBit;
//    else
//        return WgtDataType.WgtUnknown;
//}
//

    /**
     * 地理数据类类型转换成中文文字
     *
     * @param xClsType 地理数据类类型
     * @return 中文名称
     */
    public static String xClsTypeConvert(XClsType xClsType)
    {
        String cnClsType = "";

        if (XClsType.XACls.equals(xClsType))
        {
            cnClsType = "注记类";
        } else if (XClsType.XAddrBase.equals(xClsType))
        {
            cnClsType = "地名库";
        } else if (XClsType.XAddrCode.equals(xClsType))
        {
            cnClsType = "地址编码";
        } else if (XClsType.XFds.equals(xClsType))
        {
            cnClsType = "要素数据集";
        } else if (XClsType.XGNet.equals(xClsType))
        {
            cnClsType = "几何网络";
        } else if (XClsType.XLoc.equals(xClsType))
        {
            cnClsType = "定位数据集";
        } else if (XClsType.XMTDB.equals(xClsType))
        {
            cnClsType = "元数据库";
        } else if (XClsType.XMTDS.equals(xClsType))
        {
            cnClsType = "元数据集";
        } else if (XClsType.XMapSet.equals(xClsType))
        {
            cnClsType = "地图集";
        } else if (XClsType.XOCls.equals(xClsType))
        {
            cnClsType = "对象类";
        } else if (XClsType.XRCls.equals(xClsType))
        {
            cnClsType = "关系类";
        } else if (XClsType.XRcat.equals(xClsType))
        {
            cnClsType = "栅格目录";
        } else if (XClsType.XRds.equals(xClsType))
        {
            cnClsType = "栅格数据集";
        } else if (XClsType.XSFCls.equals(xClsType))
        {
            cnClsType = "简单要素类";
        } else if (XClsType.XSFCls3D.equals(xClsType))
        {
            cnClsType = "三维简单要素类";
        } else if (XClsType.XTIN.equals(xClsType))
        {
            cnClsType = "TIN镶嵌数据集";
        } else if (XClsType.XMosaicDS.equals(xClsType))
        {
            cnClsType = "镶嵌数据集";
        }
//    else if ("-2".equals(xClsType.toString())) {
//        cnClsType = "地理数据库";
//    }

        return cnClsType;
    }

    /**
     * 地理数据类中文名称转成枚举值
     *
     * @param strType 中文名称
     * @return 枚举值
     */
    public static XClsType xClsTypeConvert(String strType)
    {
        XClsType xClsType = XClsType.Unknown;

        if (strType == "注记类")
        {
            xClsType = XClsType.XACls;
        } else if (strType == "地名库")
        {
            xClsType = XClsType.XAddrBase;
        } else if (strType == "地址编码")
        {
            xClsType = XClsType.XAddrCode;
        } else if (strType == "要素数据集")
        {
            xClsType = XClsType.XFds;
        } else if (strType == "几何网络")
        {
            xClsType = XClsType.XGNet;
        } else if (strType == "定位数据集")
        {
            xClsType = XClsType.XLoc;
        } else if (strType == "元数据库")
        {
            xClsType = XClsType.XMTDB;
        } else if (strType == "元数据集")
        {
            xClsType = XClsType.XMTDS;
        } else if (strType == "地图集")
        {
            xClsType = XClsType.XMapSet;
        } else if (strType == "对象类")
        {
            xClsType = XClsType.XOCls;
        } else if (strType == "关系类")
        {
            xClsType = XClsType.XRCls;
        } else if (strType == "栅格目录")
        {
            xClsType = XClsType.XRcat;
        } else if (strType == "栅格数据集")
        {
            xClsType = XClsType.XRds;
        } else if (strType == "简单要素类" || strType == "点简单要素类"
                || strType == "线简单要素类" || strType == "区简单要素类"
                || strType == "面简单要素类" || strType == "体简单要素类")
        {
            xClsType = XClsType.XSFCls;
        } else if (strType == "三维简单要素类")
        {
            xClsType = XClsType.XSFCls3D;
        } else if (strType == "TIN镶嵌数据集")
        {
            xClsType = XClsType.XTIN;
        } else if (strType == "镶嵌数据集")
        {
            xClsType = XClsType.XMosaicDS;//zkj-2018-01-17
        }
//    else if (strType == "地理数据库") {
//        xClsType = (XClsType)(-2);
//    }
        return xClsType;
    }

///// <summary>
///// 三维图层类型转换成中文文字
///// </summary>
///// <param name="layerType">三维图层类型</param>
///// <returns>对应显示文本</returns>
//public static String G3DLayerTypeConvert(G3DLayerType layerType)
//{
//    String cnLayerType = "";
//
//    switch (layerType)
//    {
//        case G3DLayerType.Cloud:
//            cnLayerType = Resources.String_3DCloudLayer;
//            break;
//        case G3DLayerType.Label:
//            cnLayerType = Resources.String_3DLabelLayer;
//            break;
//        case G3DLayerType.MapRef:
//            cnLayerType = Resources.String_2DMapLayer;
//            break;
//        case G3DLayerType.Model:
//            cnLayerType = Resources.String_3DModelLayer;
//            break;
//        case G3DLayerType.Panorama:
//            cnLayerType = Resources.String_3DPanLaeyr;
//            break;
//        case G3DLayerType.Server:
//            cnLayerType = Resources.String_3DSvrLayer;
//            break;
//        case G3DLayerType.Terrain:
//            cnLayerType = Resources.String_3DTerLayer;
//            break;
//        case G3DLayerType.Vector:
//            cnLayerType = Resources.String_3DVectLayer;
//            break;
//        case G3DLayerType.Group:
//            cnLayerType = Resources.String_3DGroupLayer;
//            break;
//        case G3DLayerType.ModelCacheLayer:
//            cnLayerType = Resources.String_CacheLayer;
//            break;
//        case G3DLayerType.PointCloudLayer:
//            cnLayerType = Resources.String_PointCloudLayer;
//            break;
//        default:
//            break;
//    }
//
//    return cnLayerType;
//}
///// <summary>
///// 三维图层类型中文文字转为枚举值
///// </summary>
///// <param name="strType">显示文本</param>
///// <returns>对应三维图层类型</returns>
//public static G3DLayerType G3DLayerTypeConvert(String strType)
//{
//    G3DLayerType layerType = G3DLayerType.Unknown;
//
//    if (strType == Resources.String_3DCloudLayer)
//        layerType = G3DLayerType.Cloud;
//    else if (strType == Resources.String_3DLabelLayer)
//        layerType = G3DLayerType.Label;
//    else if (strType == Resources.String_2DMapLayer)
//        layerType = G3DLayerType.MapRef;
//    else if (strType == Resources.String_3DModelLayer)
//        layerType = G3DLayerType.Model;
//    else if (strType == Resources.String_3DPanLaeyr)
//        layerType = G3DLayerType.Panorama;
//    else if (strType == Resources.String_3DSvrLayer)
//        layerType = G3DLayerType.Server;
//    else if (strType == Resources.String_3DTerLayer)
//        layerType = G3DLayerType.Terrain;
//    else if (strType == Resources.String_3DVectLayer)
//        layerType = G3DLayerType.Vector;
//
//    return layerType;
//}
//
///// <summary>
///// 三维矢量图层绘制方式
///// </summary>
///// <param name="mode">是否矢量绘制</param>
///// <returns>对应显示文本</returns>
//public static String V3DLayerReaderModeConvert(boolean mode)
//{
//    return mode ? Resources.String_VectorMode : Resources.String_RasterMode;
//}
///// <summary>
///// 三维矢量图层绘制方式
///// </summary>
///// <param name="mode">显示文本</param>
///// <returns>是否矢量绘制</returns>
//public static boolean V3DLayerReaderModeConvert(String mode)
//{
//    return mode == Resources.String_VectorMode;
//}
//
///// <summary>
///// 模型层渲染类型转为文本
///// </summary>
///// <param name="mrType">模型层渲染类型</param>
///// <returns>文本名称</returns>
//public static String ModelRenderTypeConvert(ModelRenderType mrType)
//{
//    String cnmrType = "";
//    switch (mrType)
//    {
//        case ModelRenderType.RenderCommon:
//            cnmrType = Resources.String_RenderCommon;
//            break;
//        case ModelRenderType.RenderGrid:
//            cnmrType = Resources.String_RenderGrid;
//            break;
//        default:
//            break;
//    }
//    return cnmrType;
//}
///// <summary>
///// 文本转为模型层渲染类型
///// </summary>
///// <param name="cnmrType">文本名称</param>
///// <returns>枚举值</returns>
//public static ModelRenderType ModelRenderTypeConvert(String cnmrType)
//{
//    ModelRenderType mrType = ModelRenderType.RenderCommon;
//    if (cnmrType == Resources.String_RenderGrid)
//        mrType = ModelRenderType.RenderGrid;
//    return mrType;
//}
//
///// <summary>
///// bool类型转文本
///// </summary>
///// <param name="boolType"></param>
///// <returns></returns>
//public static String BoolRenderTypeConvert(boolean boolType)
//{
//    String cboolType = "";
//    switch (boolType)
//    {
//        case true:
//            cboolType = Resources.String_True;
//            break;
//        case false:
//            cboolType = Resources.String_False;
//            break;
//        default:
//            break;
//    }
//    return cboolType;
//}
///// <summary>
///// 文本转bool类型
///// </summary>
///// <param name="cboolType"></param>
///// <returns></returns>
//public static boolean BoolRenderTypeConvert(String cboolType)
//{
//    boolean boolType = true;
//    if (cboolType == Resources.String_True)
//        boolType = true;
//    else if (cboolType == Resources.String_False)
//        boolType = false;
//    return boolType;
//}

    /**
     * 栅格重采样类型转成中文
     *
     * @param rasterSampling 栅格重采样方式
     * @return 中文名称
     */
    public static String rasterSamplingConvert(RasterResampling rasterSampling)
    {
        String cnRasterSampling = "";
        if (RasterResampling.BilinearInterpolation.equals(rasterSampling))
        {
            cnRasterSampling = "双线性";
        } else if (RasterResampling.CubicConvolution.equals(rasterSampling))
        {
            cnRasterSampling = "三次卷积";
        } else if (RasterResampling.NearestNeighbor.equals(rasterSampling))
        {
            cnRasterSampling = "最邻近";
        }
        return cnRasterSampling;
    }

    /**
     * 栅格重采样类型转成枚举值
     *
     * @param strType 中文名称
     * @return 枚举值
     */
    public static RasterResampling rasterSamplingConvert(String strType)
    {
        RasterResampling rasterSampling = RasterResampling.NearestNeighbor;
        if ("双线性".equals(strType))
        {
            rasterSampling = RasterResampling.BilinearInterpolation;
        } else if ("三次卷积".equals(strType))
        {
            rasterSampling = RasterResampling.CubicConvolution;
        } else if ("最邻近".equals(strType))
        {
            rasterSampling = RasterResampling.NearestNeighbor;
        }
        return rasterSampling;
    }

    /**
     * 栅格重采样类型转成中文
     *
     * @param rasGrayConvertForm 栅格重采样方式
     * @return 中文名称
     */
    public static String rasGrayConvertFormConvert(GrayscaleTransform rasGrayConvertForm)
    {
        String cnrasGrayConvertForm = "";
        if (GrayscaleTransform.None.equals(rasGrayConvertForm))
        {
            cnrasGrayConvertForm = "原始显示";
        } else if (GrayscaleTransform.Equalize.equals(rasGrayConvertForm))
        {
            cnrasGrayConvertForm = "均衡化显示";
        } else if (GrayscaleTransform.Normailze.equals(rasGrayConvertForm))
        {
            cnrasGrayConvertForm = "归一化显示";
        } else if (GrayscaleTransform.Boot.equals(rasGrayConvertForm))
        {
            cnrasGrayConvertForm = "平方根显示";
        } else if (GrayscaleTransform.Square.equals(rasGrayConvertForm))
        {
            cnrasGrayConvertForm = "平方显示";
        } else if (GrayscaleTransform.Linear.equals(rasGrayConvertForm))
        {
            cnrasGrayConvertForm = "线性显示";
        } else if (GrayscaleTransform.Invert.equals(rasGrayConvertForm))
        {
            cnrasGrayConvertForm = "反转显示";
        } else if (GrayscaleTransform.Adaptive.equals(rasGrayConvertForm))
        {
            cnrasGrayConvertForm = "自适应显示";
        } else if (GrayscaleTransform.UserDefine.equals(rasGrayConvertForm))
        {
            cnrasGrayConvertForm = "用户自定义";
        } else if (GrayscaleTransform.StandardDeviations.equals(rasGrayConvertForm))
        {
            cnrasGrayConvertForm = "标准差拉伸";
        } else if (GrayscaleTransform.PercentMinMax.equals(rasGrayConvertForm))
        {
            cnrasGrayConvertForm = "百分比拉伸";
        } else if (GrayscaleTransform.MinMax.equals(rasGrayConvertForm))
        {
            cnrasGrayConvertForm = "最大最小值拉伸";
        }
        return cnrasGrayConvertForm;
    }

    /**
     * 文本转为缓存模式类型
     */
    public static MapServerAccessMode mapServerAccessModeConvert(String cnAccessMode)
    {
        MapServerAccessMode accessMode = MapServerAccessMode.Unknown;
        if (cnAccessMode == "本地缓存")
        {
            accessMode = MapServerAccessMode.CacheOnly;
        } else if (cnAccessMode == "智能缓存")
        {
            accessMode = MapServerAccessMode.ServerAndCache;
        } else if (cnAccessMode == "不缓存")
        {
            accessMode = MapServerAccessMode.ServerOnly;
        }
        return accessMode;
    }

    /**
     * 缓存模式类型转为文本
     */
    public static String mapServerAccessModeConvert(MapServerAccessMode accessMode)
    {
        String cnAccessMode = "";
        if (MapServerAccessMode.CacheOnly.equals(accessMode))
        {
            cnAccessMode = "本地缓存";
        } else if (MapServerAccessMode.ServerAndCache.equals(accessMode))
        {
            cnAccessMode = "智能缓存";
        } else if (MapServerAccessMode.ServerOnly.equals(accessMode))
        {
            cnAccessMode = "不缓存";
        }
        return cnAccessMode;
    }

    public static LayerType3D layerType3DConvert(String strType)
    {
        LayerType3D layerType = LayerType3D.typeNone;

        if (strType == "三维云图层")
        {
            layerType = LayerType3D.type3DCloudLayer;
        } else if (strType == "三维注记图层")
        {
            layerType = LayerType3D.type3DLabelLayer;
        } else if (strType == "二维Map引用图层")
        {
            layerType = LayerType3D.type2DMapRefLayer;
        } else if (strType == "三维模型图层")
        {
            layerType = LayerType3D.type3DModelLayer;
        } else if (strType == "三维街景图层")
        {
            layerType = LayerType3D.type3DPanoramaLayer;
        } else if (strType == "三维服务图层")
        {
            layerType = LayerType3D.type3dServerLayer;
        } else if (strType == "三维地形图层")
        {
            layerType = LayerType3D.type3DTerrainLayer;
        } else if (strType == "三维矢量图层")
        {
            layerType = LayerType3D.type3DVectorLayer;
        } else if (strType == "三维组图层")
        {
            layerType = LayerType3D.type3dGroupLayer;
        } else if (strType == "模型缓存图层")
        {
            layerType = LayerType3D.type3dModelCacheLayer;
        } else if (strType == "点云图层")
        {
            layerType = LayerType3D.type3dPointCloudLayer;
        }

        return layerType;
    }

    public static String layerType3DConvert(LayerType3D layerType)
    {
        String cnLayerType = "";
        if (LayerType3D.type3DCloudLayer.equals(layerType))
        {
            cnLayerType = "三维云图层";
        } else if (LayerType3D.type3DLabelLayer.equals(layerType))
        {
            cnLayerType = "三维注记图层";
        } else if (LayerType3D.type2DMapRefLayer.equals(layerType))
        {
            cnLayerType = "二维Map引用图层";
        } else if (LayerType3D.type3DModelLayer.equals(layerType))
        {
            cnLayerType = "三维模型图层";
        } else if (LayerType3D.type3DPanoramaLayer.equals(layerType))
        {
            cnLayerType = "三维街景图层";
        } else if (LayerType3D.type3dServerLayer.equals(layerType))
        {
            cnLayerType = "三维服务图层";
        } else if (LayerType3D.type3DTerrainLayer.equals(layerType))
        {
            cnLayerType = "三维地形图层";
        } else if (LayerType3D.type3DVectorLayer.equals(layerType))
        {
            cnLayerType = "三维矢量图层";
        } else if (LayerType3D.type3dGroupLayer.equals(layerType))
        {
            cnLayerType = "三维组图层";
        } else if (LayerType3D.type3dModelCacheLayer.equals(layerType))
        {
            cnLayerType = "模型缓存图层";
        } else if (LayerType3D.type3dPointCloudLayer.equals(layerType))
        {
            cnLayerType = "点云图层";
        }

        return cnLayerType;
    }
}