package com.zondy.mapgis.dataconvert;

import com.zondy.mapgis.att.Fields;
import com.zondy.mapgis.base.GISDefaultValues;
import com.zondy.mapgis.base.XFunctions;
import com.zondy.mapgis.base.XString;
import com.zondy.mapgis.common.URLParse;
import com.zondy.mapgis.geodatabase.*;
import com.zondy.mapgis.geodatabase.config.DataSrcInfo;
import com.zondy.mapgis.geodatabase.config.EnvConfig;
import com.zondy.mapgis.geodatabase.config.MapGisEnv;
import com.zondy.mapgis.geodatabase.config.SvcConfig;
import com.zondy.mapgis.geometry.AnnType;
import com.zondy.mapgis.geometry.GeomType;
import com.zondy.mapgis.srs.SRefData;
import javafx.beans.property.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author CR
 * @file CustomOperate.java
 * @brief 自定义编辑类
 * @create 2020-03-16.
 */
public class CustomOperate
{
    public static final String gdbProName = "gdbp://";
    public static final String tableProName = "dbms://";
    public static final String fileProName = "file:///";
    public static final String demProName = "dem://";//为了区分txt格式的Dem数据。（注意：txt有三种文件格式：一般为矢量类文件；DBMS://开头为表格文件；DEM://开头为Arc/Info Grid数据）
    public static HashMap<String, List<String>> driveExtMap = new HashMap<>();
    public static HashMap<DataType, List<DataType>> dataTypeMap = new HashMap<>();

    //region 打开/创建矢量数据

    /**
     * 打开数据，同时返出打开的数据库
     *
     * @param url
     * @param db  可返出，打开的数据库
     * @return
     */
    public static IVectorCls openVectorCls(String url, ObjectProperty<DataBase> db)
    {
        IVectorCls vCls = null;
        if (!XString.isNullOrEmpty(url))
        {
            StringProperty name = new SimpleStringProperty();
            XClsType clsType = URLParse.getXClsType(url, name, db);
            if (db != null && db.get() != null)
            {
                IBasCls cls = db.get().getXClass(clsType);
                if (cls instanceof IVectorCls)
                {
                    vCls = (IVectorCls) cls;
                    if (vCls.open(name.get(), 1) <= 0)
                    {
                        vCls = null;
                    }
                }
            }
        }
        return vCls;
    }

    public static IVectorCls createVectorCls(String url, IVectorCls srcVcls, boolean appendMode, ObjectProperty<DataBase> dbProperty, BooleanProperty isCreate)
    {
        return createVectorCls(url, null, srcVcls, appendMode, -1, dbProperty, isCreate);
    }

    public static IVectorCls createVectorCls(String url, DataBase srcDataBase, IVectorCls srcVcls, boolean appendMode, int customSrsID, ObjectProperty<DataBase> dbProperty, BooleanProperty isCreate)
    {
        if (dbProperty == null)
        {
            dbProperty = new SimpleObjectProperty<>();
        }
        if (isCreate == null)
        {
            isCreate = new SimpleBooleanProperty();
        }
        dbProperty.set(null);
        isCreate.set(true);

        IVectorCls rtnCls = null;
        StringProperty clsNameSP = new SimpleStringProperty();
        StringProperty dsNameSP = new SimpleStringProperty();
        XClsType clsType = URLParse.getXClsType(url, clsNameSP, dsNameSP, dbProperty);
        DataBase db = dbProperty.get();
        String clsName = clsNameSP.get();
        String dsName = dsNameSP.get();
        if (db != null)
        {
            int dsID = 0;
            if (!XString.isNullOrEmpty(dsName))
            {
                dsID = (int) db.xClsIsExist(XClsType.XFds, dsName);
                if (dsID <= 0)//不存在，创建要素数据集
                {
                    dsID = (int) db.createFds(dsName, 0);
                    if (dsID <= 0)//创建失败
                    {
                        dsID = 0;
                    }
                }
            }

            IVectorCls vcls = (IVectorCls) db.getXClass(clsType);
            if (vcls instanceof SFeatureCls)
            {
                if (srcVcls instanceof SFeatureCls)
                {
                    SFeatureCls srcCls = (SFeatureCls) srcVcls;
                    SFeatureCls cls = (SFeatureCls) vcls;
                    Fields flds = srcCls.getFields();
                    int srID = 1;
                    if (srcDataBase != null)
                    {
                        srID = customSrsID != -1 ? customSrsID : srcVcls.getsrID();
                        SRefData sRefData = srcDataBase.getSRef(srID);
                        if (sRefData != null)
                        {
                            srID = db.addSRef(sRefData);
                            sRefData.dispose();
                        }
                    }

                    //封装层Bug：create之后数据没有打开，需要重新new了open
                    if (cls.create(clsName, srcCls.getGeomType(), dsID, srID, flds) > 0)
                    {
                        rtnCls = cls;
                    } else if (appendMode && cls.open(clsName, 1) > 0)
                    {
                        isCreate.set(false);
                        rtnCls = cls;
                    }

                    if (flds != null)
                    {
                        flds.dispose();
                    }
                }
            } else if (vcls instanceof AnnotationCls)
            {
                if (srcVcls != null)
                {
                    AnnotationCls cls = (AnnotationCls) vcls;
                    Fields flds = srcVcls.getFields();
                    int srID = 1;
                    if (srcDataBase != null)
                    {
                        srID = customSrsID != -1 ? customSrsID : srcVcls.getsrID();
                        SRefData sRefData = srcDataBase.getSRef(srID);
                        if (sRefData != null)
                        {
                            srID = db.addSRef(sRefData);
                            sRefData.dispose();
                        }
                    }

                    if (cls.create(clsName, AnnType.AnnText, dsID, srID, flds) > 0)
                    {
                        rtnCls = cls;
                    } else if (appendMode && cls.open(clsName, 1) > 0)
                    {
                        isCreate.set(false);
                        rtnCls = cls;
                    }

                    if (flds != null) {
                        flds.dispose();
                    }
                }
            } else if (vcls instanceof ObjectCls)
            {
                if (srcVcls != null)
                {
                    ObjectCls cls = (ObjectCls) vcls;
                    Fields flds = srcVcls.getFields();
                    if (flds != null)
                    {
                        if (cls.create(clsName, dsID, 1, flds) > 0)
                        {
                            rtnCls = cls;
                        } else if (appendMode && cls.open(clsName, 1) > 0)
                        {
                            isCreate.set(false);
                            rtnCls = cls;
                        }
                        flds.dispose();
                    }
                }
            }
        }
        return rtnCls;
    }

    public static IVectorCls createVectorCls(String url, GeomType geomType, Fields flds, boolean appendMode, ObjectProperty<DataBase> dbProperty, BooleanProperty isCreate)
    {
        if (dbProperty == null)
        {
            dbProperty = new SimpleObjectProperty<>();
        }
        if (isCreate == null)
        {
            isCreate = new SimpleBooleanProperty();
        }
        dbProperty.set(null);
        isCreate.set(true);

        IVectorCls rtnCls = null;
        StringProperty clsNameSP = new SimpleStringProperty();
        StringProperty dsNameSP = new SimpleStringProperty();
        XClsType clsType = URLParse.getXClsType(url, clsNameSP, dsNameSP, dbProperty);
        DataBase db = dbProperty.get();
        String clsName = clsNameSP.get();
        String dsName = dsNameSP.get();
        if (db != null)
        {
            int dsID = 0;
            if (!XString.isNullOrEmpty(dsName))
            {
                dsID = (int) db.xClsIsExist(XClsType.XFds, dsName);
                if (dsID <= 0)//不存在，创建要素数据集
                {
                    dsID = (int) db.createFds(dsName, 0);
                    if (dsID <= 0)//创建失败
                    {
                        dsID = 0;
                    }
                }
            }

            IVectorCls vcls = (IVectorCls) db.getXClass(clsType);
            if (vcls instanceof SFeatureCls)
            {
                SFeatureCls cls = (SFeatureCls) vcls;
                if (cls.create(clsName, geomType, dsID, 1, null) > 0)
                {
                    rtnCls = cls;
                } else if (appendMode && cls.open(clsName, 1) > 0)
                {
                    isCreate.set(false);
                    rtnCls = cls;
                }
            } else if (vcls instanceof AnnotationCls)
            {
                AnnotationCls cls = (AnnotationCls) vcls;
                if (cls.create(clsName, AnnType.AnnText, dsID, 1, null) > 0)
                {
                    rtnCls = cls;
                } else if (appendMode && cls.open(clsName, 1) > 0)
                {
                    isCreate.set(false);
                    rtnCls = cls;
                }
            } else if (vcls instanceof ObjectCls)
            {
                ObjectCls cls = (ObjectCls) vcls;
                boolean newFlds = false;
                if (flds == null || flds.getFieldCount() == 0)
                {
                    newFlds = true;
                    flds = new Fields();
                    flds.appendField(GISDefaultValues.getFieldCustom());
                    //flds.calLengthOffset();
                }
                if (cls.create(clsName, dsID, 1, flds) > 0)
                {
                    rtnCls = cls;
                } else if (appendMode && cls.open(clsName, 1) > 0)
                {
                    isCreate.set(false);
                    rtnCls = cls;
                }
                if (newFlds)
                {
                    flds.dispose();
                }
            }
        }
        return rtnCls;
    }

    public static boolean create7xCls(String url, boolean appendMode, ObjectProperty<IVectorCls[]> clses, ObjectProperty<boolean[]> isCreates, ObjectProperty<DataBase> dbProperty)
    {
        boolean rtn = false;
        if (clses == null)
        {
            clses = new SimpleObjectProperty<>();
        }
        if (isCreates == null)
        {
            isCreates = new SimpleObjectProperty<>();
        }
        if (dbProperty == null)
        {
            dbProperty = new SimpleObjectProperty<>();
        }
        clses.set(new IVectorCls[4]);
        isCreates.set(new boolean[]{true, true, true, true});

        DataBase db = URLParse.openDataBase(url);
        dbProperty.set(db);
        if (db != null)
        {
            String sfclsPntUrl = url + (url.toLowerCase().endsWith("_pnt") ? "" : "_pnt");
            String sfclsLinUrl = url + (url.toLowerCase().endsWith("_lin") ? "" : "_lin");
            String sfclsRegUrl = url + (url.toLowerCase().endsWith("_reg") ? "" : "_reg");
            String annclsUrl = URLParse.changeType(url, XClsType.XACls) + (url.toLowerCase().endsWith("_ann") ? "" : "_ann");
            if (appendMode)//存在则打开，否则创建
            {
                BooleanProperty isCreatePnt = new SimpleBooleanProperty(true);
                BooleanProperty isCreateLin = new SimpleBooleanProperty(true);
                BooleanProperty isCreateReg = new SimpleBooleanProperty(true);
                BooleanProperty isCreateAnn = new SimpleBooleanProperty(true);
                SFeatureCls s1 = createSFClS(sfclsPntUrl, GeomType.GeomPnt, db, true, isCreatePnt);
                SFeatureCls s2 = createSFClS(sfclsLinUrl, GeomType.GeomLin, db, true, isCreateLin);
                SFeatureCls s3 = createSFClS(sfclsRegUrl, GeomType.GeomReg, db, true, isCreateReg);
                AnnotationCls a1 = createACls(annclsUrl, db, true, isCreateAnn);
                if (s1 != null && s2 != null && s3 != null && a1 != null)
                {
                    clses.get()[0] = s1;
                    clses.get()[1] = s2;
                    clses.get()[2] = s3;
                    clses.get()[3] = a1;
                    rtn = true;
                } else
                {
                    if (s1 != null)
                    {
                        int clsID = s1.getClsID();
                        s1.close();
                        s1.dispose();
                        if (isCreatePnt.get())
                        {
                            SFeatureCls.remove(db, clsID);
                        }
                    }
                    if (s2 != null)
                    {
                        int clsID = s2.getClsID();
                        s2.close();
                        s2.dispose();
                        if (isCreateLin.get())
                        {
                            SFeatureCls.remove(db, clsID);
                        }
                    }
                    if (s3 != null)
                    {
                        int clsID = s3.getClsID();
                        s3.close();
                        s3.dispose();
                        if (isCreateReg.get())
                        {
                            SFeatureCls.remove(db, clsID);
                        }
                    }
                    if (a1 != null)
                    {
                        int clsID = a1.getClsID();
                        a1.close();
                        a1.dispose();
                        if (isCreateAnn.get())
                        {
                            AnnotationCls.remove(db, clsID);
                        }
                    }
                }
            } else
            {
                if (!(db.xClsIsExist(XClsType.XSFCls, URLParse.getName(sfclsPntUrl)) > 0 || db.xClsIsExist(XClsType.XSFCls, URLParse.getName(sfclsLinUrl)) > 0 || db.xClsIsExist(XClsType.XSFCls, URLParse.getName(sfclsRegUrl)) > 0 || db.xClsIsExist(XClsType.XACls, URLParse.getName(annclsUrl)) > 0))
                {
                    BooleanProperty isCreatePnt = new SimpleBooleanProperty(true);
                    BooleanProperty isCreateLin = new SimpleBooleanProperty(true);
                    BooleanProperty isCreateReg = new SimpleBooleanProperty(true);
                    BooleanProperty isCreateAnn = new SimpleBooleanProperty(true);
                    SFeatureCls s1 = createSFClS(sfclsPntUrl, GeomType.GeomPnt, db, false, isCreatePnt);
                    SFeatureCls s2 = createSFClS(sfclsLinUrl, GeomType.GeomLin, db, false, isCreateLin);
                    SFeatureCls s3 = createSFClS(sfclsRegUrl, GeomType.GeomReg, db, false, isCreateReg);
                    AnnotationCls a1 = createACls(annclsUrl, db, false, isCreateAnn);
                    if (s1 != null && s2 != null && s3 != null && a1 != null)
                    {
                        clses.get()[0] = s1;
                        clses.get()[1] = s2;
                        clses.get()[2] = s3;
                        clses.get()[3] = a1;
                        rtn = true;
                    } else
                    {
                        if (s1 != null)
                        {
                            int clsID = s1.getClsID();
                            s1.close();
                            s1.dispose();
                            SFeatureCls.remove(db, clsID);
                        }
                        if (s2 != null)
                        {
                            int clsID = s2.getClsID();
                            s2.close();
                            s2.dispose();
                            SFeatureCls.remove(db, clsID);
                        }
                        if (s3 != null)
                        {
                            int clsID = s3.getClsID();
                            s3.close();
                            s3.dispose();
                            SFeatureCls.remove(db, clsID);
                        }
                        if (a1 != null)
                        {
                            int clsID = a1.getClsID();
                            a1.close();
                            a1.dispose();
                            AnnotationCls.remove(db, clsID);
                        }
                    }
                }
            }
        }
        return rtn;
    }

    private static SFeatureCls createSFClS(String url, GeomType geomType, DataBase db, boolean appendMode, BooleanProperty isCreate)
    {
        if (isCreate == null)
        {
            isCreate = new SimpleBooleanProperty();
        }
        isCreate.set(true);

        SFeatureCls sfcls = null;
        StringProperty clsNameProperty = new SimpleStringProperty();
        StringProperty dsNameProperty = new SimpleStringProperty();
        XClsType clsType = URLParse.getXClsType(url, clsNameProperty, dsNameProperty);
        if (XClsType.XSFCls.equals(clsType))
        {
            String clsName = clsNameProperty.get();
            String dsName = dsNameProperty.get();
            int dsID = 0;
            if (!XString.isNullOrEmpty(dsName))
            {
                dsID = (int) db.xClsIsExist(XClsType.XFds, dsName);
                if (dsID <= 0)//不存在，创建要素数据集
                {
                    dsID = (int) db.createFds(dsName, 0);
                    if (dsID <= 0)//创建失败
                    {
                        dsID = 0;
                    }
                }
            }

            IVectorCls destVcls = (IVectorCls) db.getXClass(clsType);
            if (destVcls instanceof SFeatureCls)
            {
                SFeatureCls cls = (SFeatureCls) destVcls;
                if (cls.create(clsName, geomType, dsID, 1, null) > 0)
                {
                    sfcls = cls;
                } else if (appendMode && cls.open(clsName, 1) > 0)
                {
                    isCreate.set(false);
                    sfcls = cls;
                }
            }
        }

        return sfcls;
    }

    private static AnnotationCls createACls(String url, DataBase db, boolean appendMode, BooleanProperty isCreate)
    {
        if (isCreate == null)
        {
            isCreate = new SimpleBooleanProperty();
        }
        isCreate.set(true);

        AnnotationCls acls = null;
        StringProperty clsNameProperty = new SimpleStringProperty();
        StringProperty dsNameProperty = new SimpleStringProperty();
        XClsType clsType = URLParse.getXClsType(url, clsNameProperty, dsNameProperty);
        if (XClsType.XACls.equals(clsType))
        {
            String clsName = clsNameProperty.get();
            String dsName = dsNameProperty.get();
            int dsID = 0;
            if (!XString.isNullOrEmpty(dsName))
            {
                dsID = (int) db.xClsIsExist(XClsType.XFds, dsName);
                if (dsID <= 0)//不存在，创建要素数据集
                {
                    dsID = (int) db.createFds(dsName, 0);
                    if (dsID <= 0)//创建失败
                    {
                        dsID = 0;
                    }
                }
            }

            IVectorCls destVcls = (IVectorCls) db.getXClass(clsType);
            if (destVcls instanceof AnnotationCls)
            {
                AnnotationCls cls = (AnnotationCls) destVcls;
                if (cls.create(clsName, AnnType.AnnText, dsID, 1, null) > 0)
                {
                    acls = cls;
                } else if (appendMode && cls.open(clsName, 1) > 0)
                {
                    isCreate.set(false);
                    acls = cls;
                }
            }
        }
        return acls;
    }
    //endregion

    //region 字符判断

    /**
     * 字符是否为英文字符
     *
     * @param c
     * @return
     */
    public static boolean isLetterChar(char c)
    {
        return (c >= 97 && c <= 122) || (c >= 65 && c <= 90);
    }

    /**
     * 字符是否为数字字符
     *
     * @param c
     * @return
     */
    public static boolean isNumberChar(char c)
    {
        return c >= 48 && c <= 57;
    }

    /**
     * 字符是否为下划线
     *
     * @param c
     * @return
     */
    public static boolean isUnderlineChar(char c)
    {
        return c == '_';
    }

    /**
     * 字符是否为汉字
     *
     * @param c
     * @return
     */
    public static boolean isChineseChar(char c)
    {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS)
        {
            return true;
        }
        return false;
    }
    //endregion

    //region 初始化栅格Drive、目标类型

    /**
     * 初始化栅格数据的Drive
     */
    public static void initRasterDrive()
    {
        //未封装(RasFileExtInfo)
        //driveExtMap.clear();
        //MapGIS.GeoDataBase.GeoRaster.RasFileExtInfo[] infos = RasterDataset.GetRasFileExtInfo(MapGIS.GeoDataBase.GeoRaster.RasFileExtType.Input);
        //if (infos != null && infos.length > 0)
        //{
        //    for (MapGIS.GeoDataBase.GeoRaster.RasFileExtInfo info : infos)
        //    {
        //        if (!info.getDescripe().contains("*.*") && info.getDriver() != null && info.getDriver().trim().length() > 0 && !driveExtMap.containsKey(info.getDriver()))
        //        {
        //            List<String> exts = new ArrayList<>();
        //            if (!XString.isNullOrEmpty(info.getFileExt1() ))
        //            {
        //                exts.add(String.format(".%s", info.getFileExt1().toLowerCase()));
        //            }
        //            if (!XString.isNullOrEmpty(info.getFileExt2()))
        //            {
        //                exts.add(String.format(".%s", info.getFileExt2().toLowerCase()));
        //            }
        //            driveExtMap.put(info.getDriver(), exts);
        //        }
        //    }
        //}
    }

    /**
     * 根据源类型获取可选的目标类型
     *
     * @param sourType 源类型
     * @return 目标类型列表
     */
    public static List<DataType> getDestTypes(DataType sourType)
    {
        List<DataType> destTypes = new ArrayList<>();
        if (dataTypeMap.containsKey(sourType))
        {
            destTypes.addAll(dataTypeMap.get(sourType));
        } else
        {
            switch (sourType)
            {
                case MAPGIS_GDB:
                case MAPGIS_FDS:
                {
                    destTypes.add(DataType.ARCGIS_FILEGDB);
                    if (XFunctions.isSystemWindows())
                    {
                        destTypes.add(DataType.ARCGIS_PERSONALGDB);
                    }
                    break;
                }
                case MAPGIS_SFCLSS:
                case MAPGIS_SFCLSE:
                {
                    destTypes.add(DataType.MAPGIS_SFCLS);
                    break;
                }
                case MAPGIS_SFCLSP:
                case MAPGIS_SFCLSL:
                case MAPGIS_SFCLSR:
                {
                    destTypes.add(DataType.MAPGIS_SFCLS);
                    destTypes.add(DataType.MAPGIS_OCLS);
                    destTypes.add(DataType.VECTOR_SHP);
                    //destTypes.add(DataType.VECTOR_DXF);
                    //destTypes.add(DataType.VECTOR_DWG);
                    //destTypes.add(DataType.VECTOR_MIF);
                    //destTypes.add(DataType.VECTOR_VCT);
                    //destTypes.add(DataType.VECTOR_JSON);
                    destTypes.add(DataType.ARCGIS_FILEGDB);
                    if (XFunctions.isSystemWindows())
                    {
                        //destTypes.add(DataType.VECTOR_E00);
                        destTypes.add(DataType.MAPGIS_6X_FILE);
                        destTypes.add(DataType.ARCGIS_PERSONALGDB);
                    }
                    //destTypes.add(DataType.VECTOR_GML);
                    ////destTypes.add(DataType.VECTOR_DGN);
                    //destTypes.add(DataType.VECTOR_KML);
                    //destTypes.add(DataType.TXT);
                    //destTypes.add(DataType.TABLE_6X);
                    //destTypes.add(DataType.TABLE_EXCEL);
                    //destTypes.add(DataType.TABLE_ACCESS);
                    ////destTypes.add(DataType.TABLE_DBF);
                    ////destTypes.add(DataType.TABLE_TXT);
                    break;
                }
                case MAPGIS_ACLS:
                {
                    destTypes.add(DataType.MAPGIS_ACLS);
                    destTypes.add(DataType.MAPGIS_OCLS);
                       //destTypes.add(DataType.VECTOR_MIF);
                    //destTypes.add(DataType.VECTOR_DXF);
                    //destTypes.add(DataType.VECTOR_DWG);
                    //destTypes.add(DataType.VECTOR_VCT);
                    //destTypes.add(DataType.VECTOR_JSON);
                    ////destTypes.add(DataType.VECTOR_DGN);
                    //destTypes.add(DataType.TXT);
                    destTypes.add(DataType.ARCGIS_FILEGDB);
                    if (XFunctions.isSystemWindows())
                    {
                        destTypes.add(DataType.MAPGIS_6X_FILE);
                        //destTypes.add(DataType.VECTOR_E00);
                        destTypes.add(DataType.ARCGIS_PERSONALGDB);
                    }

                    //destTypes.add(DataType.TABLE_6X);
                    //destTypes.add(DataType.TABLE_EXCEL);
                    //destTypes.add(DataType.TABLE_ACCESS);
                    ////destTypes.add(DataType.TABLE_DBF);
                    ////destTypes.add(DataType.TABLE_TXT);
                    break;
                }
                case MAPGIS_OCLS:
                {
                    destTypes.add(DataType.MAPGIS_OCLS);
                    destTypes.add(DataType.ARCGIS_FILEGDB);
                    if (XFunctions.isSystemWindows())
                    {
                        destTypes.add(DataType.ARCGIS_PERSONALGDB);
                    }

                    //destTypes.add(DataType.TABLE_6X);
                    //destTypes.add(DataType.TABLE_EXCEL);
                    //destTypes.add(DataType.TABLE_ACCESS);
                    ////destTypes.add(DataType.TABLE_DBF);
                    ////destTypes.add(DataType.TABLE_TXT);
                    break;
                }
                case MAPGIS_6X_FILE:
                case MAPGIS_6X_WT:
                case MAPGIS_6X_WL:
                case MAPGIS_6X_WP:
                {
                    destTypes.add(DataType.MAPGIS_SFCLS);
                    destTypes.add(DataType.MAPGIS_OCLS);
                    if (sourType.equals(DataType.MAPGIS_6X_WT))
                    {
                        destTypes.add(DataType.MAPGIS_ACLS);
                    }
                    break;
                }
                case VECTOR_SHP:
                case VECTOR_MIF:
                case VECTOR_E00:
                case VECTOR_VCT:
                case VECTOR_GML:
                case VECTOR_DGN:
                case VECTOR_KML:
                case TXT:
                {
                    destTypes.add(DataType.MAPGIS_SFCLS);
                    break;
                }
                case TABLE_6X:
                case TABLE_EXCEL:
                case TABLE_DBF:
                    //case TABLE_TXT:
                {
                    destTypes.add(DataType.MAPGIS_OCLS);
                    break;
                }
                case MAPGIS_RCAT:
                {
                    destTypes.add(DataType.MAPGIS_RCAT);
                    break;
                }
                case MAPGIS_RAS:
                {
                    destTypes.add(DataType.MAPGIS_RAS);
                    //destTypes.add(DataType.RASTER_BMP);
                    //destTypes.add(DataType.RASTER_GIF);
                    //destTypes.add(DataType.RASTER_HDF5);
                    //destTypes.add(DataType.RASTER_IMG);
                    //destTypes.add(DataType.RASTER_JP2);
                    //destTypes.add(DataType.RASTER_JPG);
                    //destTypes.add(DataType.RASTER_MSI);
                    //destTypes.add(DataType.RASTER_PNG);
                    //destTypes.add(DataType.RASTER_TIFF);
                    break;
                }
                case RASTER_BMP:
                case RASTER_GIF:
                case RASTER_HDF5:
                case RASTER_IMG:
                case RASTER_JP2:
                case RASTER_JPG:
                case RASTER_MSI:
                case RASTER_PNG:
                case RASTER_TIFF:
                case RASTER_6XDEM:
                {
                    destTypes.add(DataType.MAPGIS_RAS);
                }
                break;
                case ARCINFO_COVERAGE:
                {
                    destTypes.add(DataType.MAPGIS_FDS);
                    break;
                }
                case VECTOR_DXF:
                case VECTOR_DWG:
                case VECTOR_JSON:
                {
                    destTypes.add(DataType.MAPGIS_GDB);
                    destTypes.add(DataType.MAPGIS_FDS);
                    break;
                }
                case ARCGIS_FILEGDB:
                case ARCGIS_PERSONALGDB:
                {
                    destTypes.add(DataType.MAPGIS_GDB);
                    break;
                }
                //增加三维数据支持 0414 ysp
                case Model_3DS:
                case Model_OBJ:
                case Model_DAE:
                case Model_OSGB:
                case Model_FBX:
                case Model_XML:
                case Model_X:
                case Model_LAS:
                {
                    destTypes.add(DataType.MAPGIS_SFCLS);
                    break;
                }
                default:
                    break;
            }
        }
        return destTypes;
    }
    //endregion

    //region 数据源类型判断

    /**
     * 判断Server名为serverName的服务是否为中间件
     *
     * @param serverName
     * @return
     */
    public static boolean isMiddleWare(String serverName)
    {
        boolean rtn = false;
        if (!XString.isNullOrEmpty(serverName))
        {
            int indexAt = serverName.indexOf("@");
            if (indexAt >= 0)
            {
                serverName = serverName.substring(indexAt + 1);
            }

            DataSrcInfo svcInfo = SvcConfig.get(serverName);
            if (svcInfo != null)
            {
                String dnsName = svcInfo.getDnsName();
                rtn = dnsName != null && dnsName.contains("&");
            }
        }
        return rtn;
    }

    /**
     * 判断Server名为serverName的服务是否为ArcGIS中间件
     *
     * @param serverName
     * @return
     */
    public static boolean isArcGISLocal(String serverName)
    {
        boolean rtn = false;
        if (!XString.isNullOrEmpty(serverName))
        {
            int indexAt = serverName.indexOf("@");
            if (indexAt >= 0)
            {
                serverName = serverName.substring(indexAt + 1);
            }
            DataSrcInfo svcInfo = SvcConfig.get(serverName);
            if (svcInfo != null)
            {
                String dnsName = svcInfo.getDnsName();
                if (dnsName != null)
                {
                    int cur = dnsName.indexOf('&');
                    if (cur >= 0)
                    {
                        rtn = "&ArcGISLocal".equalsIgnoreCase(dnsName.substring(cur));
                    }
                }
            }
        }
        return rtn;
    }
    //endregion

    //region 获取参数信息
    public static String getDefaultMifSymMapPath()
    {
        MapGisEnv gisEnv = EnvConfig.getGisEnv();
        String slib = gisEnv.getSlib();
        return String.format("%s%s%s", slib, slib.endsWith(File.separator) ? "" : File.separator, "GisTInfo.mtg");
    }

    public static String getDefaultDxfSymMapPath()
    {
        MapGisEnv gisEnv = EnvConfig.getGisEnv();
        String slib = gisEnv.getSlib();
        return String.format("%s%s%s", slib, slib.endsWith(File.separator) ? "" : File.separator, "mpdcCADMapFile.txt");
    }

    //未封装
    //public static String[] getSeparatorStringArray(String str, SeparatorStru ss)
    //{
    //    List<Character> chares = new ArrayList<>();
    //    if (ss.tab)
    //    {
    //        chares.add('\t');
    //    }
    //    if (ss.semi)
    //    {
    //        chares.add(';');
    //    }
    //    if (ss.comma)
    //    {
    //        chares.add(',');
    //    }
    //    if (ss.space)
    //    {
    //        chares.add(' ');
    //    }
    //    if (ss.other && ss.ch != 0)
    //    {
    //        char[] tempChars = System.Text.Encoding.Default.GetChars(new byte[]{(byte) ss.ch});
    //        if (tempChars != null && tempChars.length == 1)
    //        {
    //            chares.add(tempChars[0]);
    //        }
    //    }
    //    return chares.size() <= 0 ? new String[] { str } : str.split(chares.toArray(), ss.suc ? StringSplitOptions.None : StringSplitOptions.RemoveEmptyEntries);
    //}

    //public static String getSeparatorString(SeparatorStru sstru)
    //{
    //    //1.只有Tab键
    //    if (sstru.tab && !sstru.space && !sstru.comma && !sstru.semi && !sstru.other)
    //    {
    //        return "\t";
    //    }
    //    //2.只有空格
    //    if (!sstru.tab && sstru.space && !sstru.comma && !sstru.semi && !sstru.other)
    //    {
    //        return " ";
    //    }
    //    //3.只有逗号
    //    if (!sstru.tab && !sstru.space && sstru.comma && !sstru.semi && !sstru.other)
    //    {
    //        return ",";
    //    }
    //    //4.只有分号
    //    if (!sstru.tab && !sstru.space && !sstru.comma && sstru.semi && !sstru.other)
    //    {
    //        return ";";
    //    }
    //    //5.只有其他
    //    if (!sstru.tab && !sstru.space && !sstru.comma && !sstru.semi && sstru.other && sstru.ch != 0)
    //    {
    //        return GetStringByByte(sstru.ch);
    //    }
    //    //6.Tab键和空格
    //    if (sstru.tab && sstru.space && !sstru.comma && !sstru.semi && !sstru.other)
    //    {
    //        return "\t ";
    //    }
    //    //7.Tab键和逗号
    //    if (sstru.tab && !sstru.space && sstru.comma && !sstru.semi && !sstru.other)
    //    {
    //        return "\t,";
    //    }
    //    //8.Tab键和分号
    //    if (sstru.tab && !sstru.space && !sstru.comma && sstru.semi && !sstru.other)
    //    {
    //        return "\t;";
    //    }
    //    //9.Tab键和其他
    //    if (sstru.tab && !sstru.space && !sstru.comma && !sstru.semi && sstru.other && sstru.ch != 0)
    //    {
    //        return "\t" + GetStringByByte(sstru.ch);
    //    }
    //    //10.空格和逗号
    //    if (!sstru.tab && sstru.space && sstru.comma && !sstru.semi && !sstru.other)
    //    {
    //        return " ,";
    //    }
    //    //11.空格和分号
    //    if (!sstru.tab && sstru.space && !sstru.comma && sstru.semi && !sstru.other)
    //    {
    //        return " ;";
    //    }
    //    //12.空格和其他
    //    if (!sstru.tab && sstru.space && !sstru.comma && !sstru.semi && sstru.other && sstru.ch != 0)
    //    {
    //        return " " + GetStringByByte(sstru.ch);
    //    }
    //    //13.逗号和分号
    //    if (!sstru.tab && !sstru.space && sstru.comma && sstru.semi && !sstru.other)
    //    {
    //        return ",;";
    //    }
    //    //14.逗号和其他
    //    if (!sstru.tab && !sstru.space && sstru.comma && !sstru.semi && sstru.other && sstru.ch != 0)
    //    {
    //        return "," + GetStringByByte(sstru.ch);
    //    }
    //    //15.分号和其他
    //    if (!sstru.tab && !sstru.space && !sstru.comma && sstru.semi && sstru.other && sstru.ch != 0)
    //    {
    //        return ";" + GetStringByByte(sstru.ch);
    //    }
    //    //16.Tab、空格和逗号
    //    if (sstru.tab && sstru.space && sstru.comma && !sstru.semi && !sstru.other)
    //    {
    //        return "\t ,";
    //    }
    //    //17.Tab、空格和分号
    //    if (sstru.tab && sstru.space && !sstru.comma && sstru.semi && !sstru.other)
    //    {
    //        return "\t ;";
    //    }
    //    //18.Tab、空格和其他
    //    if (sstru.tab && sstru.space && !sstru.comma && !sstru.semi && sstru.other && sstru.ch != 0)
    //    {
    //        return "\t " + GetStringByByte(sstru.ch);
    //    }
    //    //19.空格、逗号和分号
    //    if (!sstru.tab && sstru.space && sstru.comma && sstru.semi && !sstru.other)
    //    {
    //        return " ,;";
    //    }
    //    //20.空格、逗号和其他
    //    if (!sstru.tab && sstru.space && sstru.comma && !sstru.semi && sstru.other && sstru.ch != 0)
    //    {
    //        return " ," + GetStringByByte(sstru.ch);
    //    }
    //    //21.逗号、分号和其他
    //    if (!sstru.tab && !sstru.space && sstru.comma && sstru.semi && sstru.other && sstru.ch != 0)
    //    {
    //        return ",;" + GetStringByByte(sstru.ch);
    //    }
    //    // 修改说明：上述代码从K9拷贝而来，显然存在漏掉的选项，例如全部勾选。现添加以下代码，全部组合都能考虑到了，解决Bug5847
    //    // 修改人：周小飞 2014-09-03
    //    String rtn = "";
    //    if (sstru.tab)
    //    {
    //        rtn += "\t";
    //    }
    //    if (sstru.space)
    //    {
    //        rtn += " ";
    //    }
    //    if (sstru.comma)
    //    {
    //        rtn += ",";
    //    }
    //    if (sstru.semi)
    //    {
    //        rtn += ";";
    //    }
    //    if (sstru.other)
    //    {
    //        rtn += GetStringByByte(sstru.ch);
    //    }
    //    return rtn;
    //}
    //
    //public static String GetStringByByte(sbyte sb)
    //{
    //    return new String(System.Text.Encoding.Default.GetChars(new byte[]{(byte) sb}));
    //}
    //endregion
}
