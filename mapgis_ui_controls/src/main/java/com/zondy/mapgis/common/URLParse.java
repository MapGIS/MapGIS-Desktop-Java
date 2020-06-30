package com.zondy.mapgis.common;

import com.zondy.mapgis.base.LanguageConvert;
import com.zondy.mapgis.base.XString;
import com.zondy.mapgis.geodatabase.DataBase;
import com.zondy.mapgis.geodatabase.IBasCls;
import com.zondy.mapgis.geodatabase.Server;
import com.zondy.mapgis.geodatabase.XClsType;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Administrator on 2020/3/9.
 */
public class URLParse
{
    /**
     * 地理数据库URL协议头
     */
    public static final String gdbpPro = "gdbp://";
    /**
     * 本地文件URL协议头
     */
    public static final String filePro = "file:///";

    //region URL字符解析

    /**
     * 获取数据类型字符串（如 sfcls）
     *
     * @param url 数据url
     * @return 数据类型字符串
     */
    public static String getXClsTypeString(String url)
    {
        return getXClsTypeString(url, new SimpleStringProperty());
    }

    /**
     * 获取数据类型字符串（如 sfcls）
     *
     * @param url  数据url
     * @param name 数据名称
     * @return 数据类型字符串
     */
    public static String getXClsTypeString(String url, StringProperty name)
    {
        String type = null;
        if (!XString.isNullOrEmpty(url))
        {
            StringProperty user = new SimpleStringProperty();
            StringProperty psw = new SimpleStringProperty();
            StringProperty server = new SimpleStringProperty();
            StringProperty database = new SimpleStringProperty();
            StringProperty ds = new SimpleStringProperty();
            StringProperty strtype = new SimpleStringProperty();
            getURLInfo(url, user, psw, server, database, ds, strtype, name);
            type = strtype.get();
        }
        return type;
    }

    /**
     * 根据URL返回数据类型，并且可以返回类型URL和名称
     *
     * @param url 数据url
     * @return 数据类型
     */
    public static XClsType getXClsType(String url)
    {
        return getXClsType(url, new SimpleStringProperty());
    }

    /**
     * 根据URL返回数据类型，和数据名称
     *
     * @param url
     * @param clsName 类名
     * @return
     */
    public static XClsType getXClsType(String url, StringProperty clsName)
    {
        return getXClsType(url, clsName, new SimpleStringProperty());
    }

    /**
     * 根据URL返回数据类型，并且可以返回类型URL和名称
     *
     * @param url     数据url
     * @param clsName 数据名称
     * @param dsName  数据集名称
     * @return 数据类型
     */
    public static XClsType getXClsType(String url, StringProperty clsName, StringProperty dsName)
    {
        return getXClsType(url, clsName, dsName, new SimpleStringProperty(), new SimpleStringProperty());
    }

    /**
     * 根据URL返回数据类型，并且可以返回类型URL和名称
     *
     * @param url     数据url
     * @param clsName 数据名称
     * @param dsName  数据集名称
     * @param dbName  数据库名称
     * @return 数据类型
     */
    public static XClsType getXClsType(String url, StringProperty clsName, StringProperty dsName, StringProperty dbName)
    {
        return getXClsType(url, clsName, dsName, dbName, new SimpleStringProperty());
    }

    /**
     * 根据URL返回数据类型，并且可以返回类型URL和名称
     *
     * @param url        数据url
     * @param clsName    数据名称
     * @param dsName     数据集名称
     * @param dbName     数据库名称
     * @param serverName 数据源名称
     * @return 数据类型
     */
    public static XClsType getXClsType(String url, StringProperty clsName, StringProperty dsName, StringProperty dbName, StringProperty serverName)
    {
        XClsType type = XClsType.Unknown;
        if (!XString.isNullOrEmpty(url))
        {
            StringProperty strtype = new SimpleStringProperty();
            getURLInfo(url, new SimpleStringProperty(), new SimpleStringProperty(), serverName, dbName, dsName, strtype, clsName);
            type = stringToXClsType(strtype.get());
        }
        return type;
    }

    /**
     * 根据URL返回数据类型，并且可以返回类型URL和名称
     *
     * @param url     数据url
     * @param clsName 数据名称
     * @param db      数据库
     * @return 数据类型
     */
    public static XClsType getXClsType(String url, StringProperty clsName, ObjectProperty<DataBase> db)
    {
        return getXClsType(url, clsName, new SimpleStringProperty(), db);
    }

    /**
     * 根据URL返回数据类型，并且可以返回类型URL和名称
     *
     * @param url     数据url
     * @param clsName 数据名称
     * @param dsName  数据集名称
     * @param db      数据库
     * @return 数据类型
     */
    public static XClsType getXClsType(String url, StringProperty clsName, StringProperty dsName, ObjectProperty<DataBase> db)
    {
        if (db == null)
        {
            db = new SimpleObjectProperty<>();
        }
        XClsType clsType = XClsType.Unknown;
        if (!XString.isNullOrEmpty(url))
        {
            db.set(null);
            if (!XString.isNullOrEmpty(url))
            {
                StringProperty user = new SimpleStringProperty("");
                StringProperty psw = new SimpleStringProperty("");
                StringProperty server = new SimpleStringProperty();
                StringProperty database = new SimpleStringProperty();
                StringProperty type = new SimpleStringProperty();
                int rtn = getURLInfo(url, user, psw, server, database, dsName, type, clsName);
                if (rtn > 0)
                {
                    clsType = stringToXClsType(type.get());
                    String serverURL = combineServer(user.get(), psw.get(), server.get());
                    if (!XString.isNullOrEmpty(serverURL) && !XString.isNullOrEmpty(database))
                    {
                        String dbURL = serverURL + "/" + database.get();
                        db.set(DataBase.openByURL(dbURL));
                    }
                }
            }
        }
        return clsType;
    }

    /**
     * 根据URL获取数据源名称
     *
     * @param url
     * @return
     */
    public static String getServerName(String url)
    {
        StringProperty server = new SimpleStringProperty();
        getURLInfo(url, new SimpleStringProperty(), new SimpleStringProperty(), server, new SimpleStringProperty(), new SimpleStringProperty(), new SimpleStringProperty(), new SimpleStringProperty());
        return server.get();
    }

    /**
     * 根据URL获取的该路径所包含的地理数据源部分
     *
     * @param url
     * @return
     */
    public static String getServer(String url)
    {
        return getServer(url, new SimpleStringProperty(), new SimpleStringProperty(), new SimpleStringProperty());
    }

    /**
     * 解析URL获得数据源URL，返出数据库名称
     *
     * @param url
     * @param db  数据库名称
     * @return 数据源路径
     */
    public static String getServer(String url, StringProperty db)
    {
        String strSvr = "";
        if (!XString.isNullOrEmpty(url))
        {
            StringProperty user = new SimpleStringProperty();
            StringProperty psw = new SimpleStringProperty();
            StringProperty server = new SimpleStringProperty();
            getURLInfo(url, user, psw, server, db, new SimpleStringProperty(), new SimpleStringProperty(), new SimpleStringProperty());
            strSvr = combineServer(user.get(), psw.get(), server.get());
        }
        return strSvr;
    }

    /**
     * 根据URL获得用户名密码和服务器
     *
     * @param url
     * @param user
     * @param psw
     * @param server 数据源名称
     * @return 数据源路径
     */
    public static String getServer(String url, StringProperty user, StringProperty psw, StringProperty server)
    {
        String strSvr = "";
        if (!XString.isNullOrEmpty(url))
        {
            StringProperty database = new SimpleStringProperty();
            StringProperty ds = new SimpleStringProperty();
            StringProperty type = new SimpleStringProperty();
            StringProperty name = new SimpleStringProperty();
            getURLInfo(url, user, psw, server, database, ds, type, name);
            strSvr = combineServer(user.get(), psw.get(), server.get());
        }
        return strSvr;
    }

    /**
     * 根据URL获取的该路径所包含的地理数据库部分
     *
     * @param url
     * @return
     */
    public static String getDataBase(String url)
    {
        String strDB = "";
        if (!XString.isNullOrEmpty(url))
        {
            StringProperty user = new SimpleStringProperty();
            StringProperty psw = new SimpleStringProperty();
            StringProperty server = new SimpleStringProperty();
            StringProperty database = new SimpleStringProperty();
            StringProperty ds = new SimpleStringProperty();
            StringProperty type = new SimpleStringProperty();
            StringProperty name = new SimpleStringProperty();
            getURLInfo(url, user, psw, server, database, ds, type, name);
            server.set(combineServer(user.get(), psw.get(), server.get()));
            if (!XString.isNullOrEmpty(server) && !XString.isNullOrEmpty(database))
            {
                strDB = server.get() + "/" + database.get();
            }
        }
        return strDB;
    }

    /**
     * 取url中的名称（数据url取数据名称，数据库URL取数据库名称，总之取最后一段string）
     *
     * @param url
     * @return
     */
    public static String getName(String url)
    {
        String name = "";
        if (!XString.isNullOrEmpty(url))
        {
            int index = url.lastIndexOf("/");
            if (index >= 0)
            {
                name = url.substring(index + 1);
            }
        }
        return name;
    }

    /**
     * 根据URL获取的该路径所包含的地理数据库部分
     *
     * @param url        数据库/要素数据集/栅格目录/类路径
     * @param serverName 数据源名称
     * @param dbName     数据库名称
     * @param dsName     要素数据集/栅格目录名称（url为数据库，为空字符串)
     * @param clsName    类名称（url为数据库/要素数据集/栅格目录，为空字符串)
     */
    public static void getNames(String url, StringProperty serverName, StringProperty dbName, StringProperty dsName, StringProperty clsName)
    {
        if (!XString.isNullOrEmpty(url))
        {
            StringProperty user = new SimpleStringProperty();
            StringProperty psw = new SimpleStringProperty();
            StringProperty type = new SimpleStringProperty();
            getURLInfo(url, user, psw, serverName, dbName, dsName, type, clsName);
        }
    }

    /**
     * 根据数据类型标记返回数据类型
     *
     * @param type 例如：sfcls、acls等
     * @return
     */
    public static XClsType stringToXClsType(String type)
    {
        XClsType xtype = XClsType.Unknown;
        if (!XString.isNullOrEmpty(type))
        {
            String temp = type.toLowerCase();
            switch (temp)
            {
                case "ds":
                case "fds":
                    xtype = XClsType.XFds;
                    break;
                case "sfcls":
                    xtype = XClsType.XSFCls;
                    break;
                case "acls":
                    xtype = XClsType.XACls;
                    break;
                case "ocls":
                    xtype = XClsType.XOCls;
                    break;
                case "ncls":
                    xtype = XClsType.XGNet;
                    break;
                case "rcat":
                    xtype = XClsType.XRcat;
                    break;
                case "ras":
                    xtype = XClsType.XRds;
                    break;
                case "mds":
                    xtype = XClsType.XMosaicDS;  //镶嵌数据集支持 ch 20180122
                    break;
                default:
                    xtype = XClsType.Unknown;
                    break;
            }
        }
        return xtype;
    }

    /**
     * 根据数据类型返回数据类型标记
     *
     * @param xtype 数据类型
     * @return 例如：sfcls、acls等
     */
    public static String xClsTypeToString(XClsType xtype)
    {
        String type = "";
        if (xtype.equals(XClsType.XFds))
        {
            type = "ds";
        } else if (xtype.equals(XClsType.XSFCls))
        {
            type = "sfcls";
        } else if (xtype.equals(XClsType.XACls))
        {
            type = "acls";
        } else if (xtype.equals(XClsType.XOCls))
        {
            type = "ocls";
        } else if (xtype.equals(XClsType.XGNet))
        {
            type = "ncls";
        } else if (xtype.equals(XClsType.XRcat))
        {
            type = "rcat";
        } else if (xtype.equals(XClsType.XRds))
        {
            type = "ras";
        } else if (xtype.equals(XClsType.XMosaicDS))
        {
            type = "mds";
        }
        return type;
    }

    /**
     * 根据用户名密码数据源将其拼接成标准的URL格式
     *
     * @param user
     * @param psw
     * @param server
     * @return
     */
    public static String combineServer(String user, String psw, String server)
    {
        String strSvr = "";
        if (!XString.isNullOrEmpty(server))
        {
            if (!XString.isNullOrEmpty(user) && !XString.isNullOrEmpty(psw))
            {
                strSvr = gdbpPro + user + ":" + psw + "@" + server;
            } else if (!XString.isNullOrEmpty(user))
            {
                strSvr = gdbpPro + user + "@" + server;
            } else if (!XString.isNullOrEmpty(psw))
            {
                strSvr = gdbpPro + ":" + psw + "@" + server;
            } else
            {
                strSvr = gdbpPro + server;
            }
        }
        return strSvr;
    }

    /**
     * 获取URL各个部分的信息
     *
     * @param url      对于有效的URL必定返回正确信息
     * @param user     用户名，有可能为空
     * @param psw      密码，有可能为空
     * @param server   地理数据源名称
     * @param database 地理数据库名称
     * @param ds       要素数据集/栅格目录名称（可以为空）
     * @param type     类型
     * @param clsName  类名称
     * @return 小于等于0表示无效；大于0表示有效，1有效数据源，2有效数据库，3有效数据(要素数据集、栅格目录或者不带要素数据集数据)，4带要素数据集或栅格目录的有效数据
     */
    public static int getURLInfo(String url, StringProperty user, StringProperty psw, StringProperty server, StringProperty database, StringProperty ds, StringProperty type, StringProperty clsName)
    {
        int rtn = 0;
        if (!XString.isNullOrEmpty(url))
        {
            String temp = url.trim();
            if (temp.toLowerCase().startsWith(gdbpPro))
            {
                temp = temp.substring(gdbpPro.length());
                if (!XString.isNullOrEmpty(temp))
                {
                    String[] segs = temp.split("/");

                    //region 解析数据源
                    if (segs.length > 0)
                    {
                        String seg = segs[0];
                        server.set(seg);
                        int a = seg.lastIndexOf("@");
                        if (a >= 0)
                        {
                            server.set(seg.substring(a + 1));
                            seg = seg.substring(0, a);
                            int b = seg.indexOf(":");
                            if (b >= 0)
                            {
                                user.set(seg.substring(0, b));
                                psw.set(seg.substring(b + 1));
                            } else
                            {
                                user.set(seg);
                            }
                        }
                    }
                    //endregion

                    if (segs.length > 1)
                    {
                        database.set(segs[1]);
                    }
                    if (segs.length > 2)
                    {
                        //region 解析ds类型名称
                        type.set(segs[2]);
                        XClsType xtype = stringToXClsType(segs[2]);
                        if (xtype == XClsType.XFds || xtype == XClsType.XRcat)
                        {
                            //对于只有3级的ds为空，例如gdbp://MapGisLocal/中国地图/fds|ds|rcat
                            if (segs.length > 3)
                            {
                                //只有四级的例如：gdbp://MapGisLocal/中国地图/fds|ds|rcat/ddd
                                //if (xtype == XClsType.XFds)
                                ds.set(segs[3]);
                                clsName.set(segs[3]);
                            }
                            if (segs.length > 4)
                            {
                                //目录URL则name为空，例如：gdbp://MapGisLocal/中国地图/fds|ds|rcat/ddd/sfcls|ras[/h506144.tif]
                                //ds = segs[3];
                                type.set(segs[4]);
                                if (segs.length > 5)
                                    clsName.set(segs[5]);
                            }
                        } else
                        {
                            //非栅格目录和要素数据集不处理后面的，例如：gdbp://MapGisLocal/电子地图/sfcls[/aaa]
                            if (segs.length > 3)
                                clsName.set(segs[3]);
                        }
                        //endregion
                    }

                    //region 判断有效性

                    if (segs.length == 1)
                    {
                        //数据源
                        if (!XString.isNullOrEmpty(combineServer(user.get(), psw.get(), server.get())))
                            rtn = 1;
                    } else if (segs.length == 2)
                    {
                        //数据库
                        if (!XString.isNullOrEmpty(combineServer(user.get(), psw.get(), server.get())) && !XString.isNullOrEmpty(database))
                            rtn = 2;
                    } else if (segs.length == 4)
                    {
                        //不存在要素数据集或栅格目录
                        //if (! XString.isNullOrEmpty(clsName))
                        {
                            XClsType xtype = stringToXClsType(type.get());
                            if (xtype != XClsType.Unknown)
                                rtn = 3;
                        }
                    } else if (segs.length == 6)
                    {
                        //存在要素数据集或栅格目录
                        if (!XString.isNullOrEmpty(clsName) && !XString.isNullOrEmpty(ds))
                        {
                            XClsType xtype = stringToXClsType(type.get());
                            if (xtype != XClsType.Unknown)
                                rtn = 4;
                        }
                    }
                    //endregion
                }
            }
        }
        return rtn;
    }
    //endregion

    /**
     * 根据URL打开数据源
     *
     * @param url
     * @return 数据源
     */
    public static Server openServer(String url)
    {
        Server svr = null;
        if (!XString.isNullOrEmpty(url))
        {
            StringProperty user = new SimpleStringProperty("");
            StringProperty psw = new SimpleStringProperty("");
            StringProperty server = new SimpleStringProperty("");
            int rtn = getURLInfo(url, user, psw, server, new SimpleStringProperty(), new SimpleStringProperty(), new SimpleStringProperty(), new SimpleStringProperty());
            if (rtn > 0)
            {
                Server svr1 = new Server();
                if (svr1.connect(server.get(), user.get(), psw.get()) > 0)
                {
                    svr = svr1;
                } else
                {
                    svr1.dispose();
                }
            }
        }
        return svr;
    }

    /**
     * 从url中解析并打开数据库
     *
     * @param url
     * @return 数据库对象
     */
    public static DataBase openDataBase(String url)
    {
        return openDataBase(url, new SimpleStringProperty(), new SimpleObjectProperty<>(XClsType.Unknown), new SimpleStringProperty());
    }

    public static DataBase openDataBase(String url, StringProperty clsName)
    {
        return openDataBase(url, clsName, new SimpleObjectProperty<>(XClsType.Unknown), new SimpleStringProperty());
    }

    public static DataBase openDataBase(String url, StringProperty clsName, ObjectProperty<XClsType> clsType)
    {
        return openDataBase(url, clsName, clsType, new SimpleStringProperty());
    }

    public static DataBase openDataBase(String url, StringProperty clsName, ObjectProperty<XClsType> clsType, StringProperty dsName)
    {
        if (clsType == null)
        {
            clsType = new SimpleObjectProperty<>(XClsType.Unknown);
        }

        DataBase db = null;
        if (!XString.isNullOrEmpty(url))
        {
            StringProperty user = new SimpleStringProperty("");
            StringProperty psw = new SimpleStringProperty("");
            StringProperty server = new SimpleStringProperty();
            StringProperty database = new SimpleStringProperty();
            StringProperty type = new SimpleStringProperty();
            int rtn = getURLInfo(url, user, psw, server, database, dsName, type, clsName);
            if (rtn > 0)
            {
                String serverURL = combineServer(user.get(), psw.get(), server.get());
                if (!XString.isNullOrEmpty(serverURL) && !XString.isNullOrEmpty(database.toString()))
                {
                    String dbURL = serverURL + "/" + database.get();
                    db = DataBase.openByURL(dbURL);
                }
                clsType.set(stringToXClsType(type.get()));
            }
        }
        return db;
    }

    /**
     * 修改url中的数据类型，返回修改后的url
     *
     * @param url
     * @param newType
     * @return
     */
    public static String changeType(String url, XClsType newType)
    {
        String rtnUrl = url;
        if (!XString.isNullOrEmpty(url) && !XClsType.Unknown.equals(newType))
        {
            StringProperty user = new SimpleStringProperty();
            StringProperty psw = new SimpleStringProperty();
            StringProperty server = new SimpleStringProperty();
            StringProperty database = new SimpleStringProperty();
            StringProperty ds = new SimpleStringProperty();
            StringProperty strtype = new SimpleStringProperty();
            StringProperty name = new SimpleStringProperty();
            getURLInfo(url, user, psw, server, database, ds, strtype, name);
            XClsType clsType = stringToXClsType(strtype.get());
            if (!XClsType.Unknown.equals(clsType) && !clsType.equals(newType))
            {
                rtnUrl = String.format("%s/%s%s/%s%s", combineServer(user.get(), psw.get(), server.get()), database.get()
                        , (XString.isNullOrEmpty(ds) ? "" : "/ds/" + ds.get())
                        , xClsTypeToString(newType)
                        , (XString.isNullOrEmpty(name) ? "" : "/" + name.get())
                );
            }
        }
        return rtnUrl;
    }

    //    //region URL类操作
//    /// <summary>
//    /// 根据URL打开类，支持6x数据及栅格文件
//    /// </summary>
//    /// <param name="url"></param>
//    /// <returns></returns>
//    public static IBasCls OpenXCls(String url)
//    {
//        IBasCls xcls = null;
//        if (Is6xURL(url)) {
//            //region 创建6x图层
//            GeomType type = GeomType.Unknown;
//            String temp = url.trim();
//            int i = temp.LastIndexOf(".");
//            if (i > 0)
//                temp = temp.substring(i + 1);
//            String ann = "";
//            int j = temp.indexOf("@");
//            if (j > 0) {
//                ann = temp.substring(j + 1);
//                temp = temp.substring(0, j);
//            }
//            if (String.Compare(temp, "wt", true) == 0)
//                type = GeomType.Pnt;
//            if (String.Compare(temp, "wl", true) == 0)
//                type = GeomType.Lin;
//            if (String.Compare(temp, "wp", true) == 0)
//                type = GeomType.Reg;
//            // 修改说明：若6x数据的路径不以FilePro开头时，也确保此函数能够打开6x数据。
//            // 修改人：周小飞 2015-08-24
//            if (!url.StartsWith(FilePro, StringComparison.OrdinalIgnoreCase)) {
//                url = FilePro + url;
//            }
//            if (String.Compare(ann, "ann", true) == 0) {
//                AnnotationCls acls = new AnnotationCls();
//                if (acls.Open(url))
//                    xcls = acls;
//                else
//                    acls.Dispose();
//            } else if (String.Compare(temp, "wb", true) == 0) {
//                ObjectCls ocls = new ObjectCls();
//                if (ocls.Open(url))
//                    xcls = ocls;
//                else
//                    ocls.Dispose();
//            } else {
//                if (type != GeomType.Unknown) {
//                    SFeatureCls sfcls = new SFeatureCls();
//                    if (sfcls.Open(url))
//                        xcls = sfcls;
//                    else
//                        sfcls.Dispose();
//                }
//            }
//            //endregion
//        } else if (IsRasterURL(url)) {
//            //region 创建栅格数据集
//            RasterDataSet rdcls = new RasterDataSet();
//            if (rdcls.Open(url, RasAccessType.RasAccessType_Update))
//                xcls = rdcls;
//            else
//                MapGISErrorForm.ShowLastError();
//            //endregion
//        } else {
//            if (!XString.isNullOrEmpty(url)) {
//                String user = "";
//                String psw = "";
//                String server = "";
//                String database = "";
//                String strtype = "";
//                String ds = "";
//                String xclsname = "";
//                int rtn = getURLInfo(url, out user, out psw, out server, out database, out ds, out strtype, out xclsname);
//                if (rtn > 0) {
//                    XClsType type = stringToXClsType(strtype);
//                    switch (type) {
//                        case XClsType.XFds: {
//                            break;
//                        }
//                        case XClsType.XSFCls: {
//                            SFeatureCls sfcls = new SFeatureCls();
//                            if (sfcls.Open(url))
//                                xcls = sfcls;
//                            else
//                                sfcls.Dispose();
//                            break;
//                        }
//                        case XClsType.XACls: {
//                            AnnotationCls acls = new AnnotationCls();
//                            if (acls.Open(url))
//                                xcls = acls;
//                            else
//                                acls.Dispose();
//                            break;
//                        }
//                        case XClsType.XOCls: {
//                            ObjectCls ocls = new ObjectCls();
//                            if (ocls.Open(url))
//                                xcls = ocls;
//                            else
//                                ocls.Dispose();
//                            break;
//                        }
//                        case XClsType.RCls: {
//                            RelationCls rcls = new RelationCls();
//                            if (rcls.Open(url))
//                                xcls = rcls;
//                            else
//                                rcls.Dispose();
//                            break;
//                        }
//                        case XClsType.XGNet: {
//                            NetCls ncls = new NetCls();
//                            if (ncls.Open(url))
//                                xcls = ncls;
//                            else
//                                ncls.Dispose();
//                            break;
//                        }
//                        case XClsType.MapSet: {
//                            MapSet mcls = new MapSet(new DataBase());
//                            if (mcls.Open(url))
//                                xcls = mcls;
//                            else
//                                mcls.Dispose();
//                            break;
//                        }
//                        case XClsType.XRcat: {
//                            RasterCatalog rccls = new RasterCatalog();
//                            if (rccls.OpenByURL(url))
//                                xcls = rccls;
//                            else
//                                MapGISErrorForm.ShowLastError();
//                            break;
//                        }
//                        case XClsType.XRds: {
//                            RasterDataSet rdcls = new RasterDataSet();
//                            if (rdcls.Open(url, RasAccessType.RasAccessType_Update))
//                                xcls = rdcls;
//                            else
//                                MapGISErrorForm.ShowLastError();
//                            break;
//                        }
//                        default:
//                            break;
//                    }
//                }
//            }
//        }
//        return xcls;
//    }
//
//    /// <summary>
//    /// 根据URL创建类（对于简单要素类则缺省以区创建，并且不支持栅格数据集创建），支持6x数据
//    /// </summary>
//    /// <param name="url"></param>
//    /// <returns>返回创建成功并已经打开的类</returns>
//    public static IBasCls CreateXCls(String url)
//    {
//        return CreateXCls(url, GeomType.Reg);
//    }
//
//    /// <summary>
//    /// 根据URL创建类（不支持栅格数据集创建）
//    /// </summary>
//    /// <param name="url"></param>
//    /// <param name="geom">要素类型，在创建矢量类时有必要</param>
//    /// <returns>返回创建成功并已经打开的类</returns>
//    public static IBasCls CreateXCls(String url, GeomType geom)
//    {
//        IBasCls xcls = null;
//        if (Is6xURL(url)) {
//            //region 创建6x图层
//            GeomType type = GeomType.Unknown;
//            String temp = url.trim();
//            int i = temp.LastIndexOf(".");
//            if (i > 0)
//                temp = temp.substring(i + 1);
//            String ann = "";
//            int j = temp.indexOf("@");
//            if (j > 0) {
//                ann = temp.substring(j + 1);
//                temp = temp.substring(0, j);
//            }
//            if (String.Compare(temp, "wt", true) == 0)
//                type = GeomType.Pnt;
//            if (String.Compare(temp, "wl", true) == 0)
//                type = GeomType.Lin;
//            if (String.Compare(temp, "wp", true) == 0)
//                type = GeomType.Reg;
//            if (String.Compare(ann, "ann", true) == 0) {
//                AnnotationCls acls = new AnnotationCls();
//                if (acls.Create(url) > 0)
//                    xcls = acls;
//                else
//                    acls.Dispose();
//            } else if (String.Compare(temp, "wb", true) == 0) {
//                ObjectCls ocls = new ObjectCls();
//                if (ocls.Create(url, null) > 0)
//                    xcls = ocls;
//                else
//                    ocls.Dispose();
//            } else {
//                if (type != GeomType.Unknown) {
//                    SFeatureCls sfcls = new SFeatureCls();
//                    if (sfcls.Create(url, type) > 0)
//                        xcls = sfcls;
//                    else
//                        sfcls.Dispose();
//                }
//            }
//            //endregion
//        } else {
//            if (!XString.isNullOrEmpty(url)) {
//                String user = "";
//                String psw = "";
//                String server = "";
//                String database = "";
//                String strtype = "";
//                String ds = "";
//                String xclsname = "";
//                int rtn = getURLInfo(url, out user, out psw, out server, out database, out ds, out strtype, out xclsname);
//                if (rtn > 0) {
//                    XClsType type = stringToXClsType(strtype);
//                    switch (type) {
//                        case XClsType.XFds: {
//                            server = combineServer(user, psw, server);
//                            if (!XString.isNullOrEmpty(server) && !XString.isNullOrEmpty(database)) {
//                                database = server + "/" + database;
//                                DataBase db = DataBase.openByURL(database);
//                                if (db != null) {
//                                    db.CreateFds(ds, 0);
//                                }
//                            }
//                            break;
//                        }
//                        case XClsType.XSFCls: {
//                            SFeatureCls sfcls = new SFeatureCls();
//                            if (sfcls.Create(url, geom) > 0)
//                                xcls = sfcls;
//                            else
//                                sfcls.Dispose();
//                            break;
//                        }
//                        case XClsType.XACls: {
//                            AnnotationCls acls = new AnnotationCls();
//                            if (acls.Create(url) > 0)
//                                xcls = acls;
//                            else
//                                acls.Dispose();
//                            break;
//                        }
//                        case XClsType.XOCls: {
//                            ObjectCls ocls = new ObjectCls();
//                            if (ocls.Create(url, null) > 0)
//                                xcls = ocls;
//                            else
//                                ocls.Dispose();
//                            break;
//                        }
//                        case XClsType.RCls: {
//                            RelationCls rcls = new RelationCls();
//                            if (rcls.Create(url) > 0)
//                                xcls = rcls;
//                            else
//                                rcls.Dispose();
//                            break;
//                        }
//                        case XClsType.XGNet: {
//                            NetCls ncls = new NetCls();
//                            if (ncls.Create(url) > 0)
//                                xcls = ncls;
//                            else
//                                ncls.Dispose();
//                            break;
//                        }
//                        case XClsType.MapSet: {
//                            MapSet mcls = new MapSet(new DataBase());
//                            if (mcls.Create(url) > 0)
//                                xcls = mcls;
//                            else
//                                mcls.Dispose();
//                            break;
//                        }
//                        case XClsType.XRcat: {
//                            server = combineServer(user, psw, server);
//                            if (!XString.isNullOrEmpty(server) && !XString.isNullOrEmpty(database)) {
//                                database = server + "/" + database;
//                                DataBase db = DataBase.openByURL(database);
//                                if (db != null) {
//                                    RasterCatalog rccls = new RasterCatalog(db);
//                                    if (rccls.CreateAndOpenRasterCatalog(xclsname) > 0)
//                                        xcls = rccls;
//                                }
//                            }
//                            break;
//                        }
//                        case XClsType.XRds: {
//                            break;
//                        }
//                        default:
//                            break;
//                    }
//                }
//            }
//        }
//        return xcls;
//    }
//
//    /// <summary>
//    /// 关闭类，支持6x数据
//    /// </summary>
//    /// <param name="xcls"></param>
//    /// <returns></returns>
//    public static boolean CloseXCls(IBasCls xcls)
//    {
//        boolean bRtn = false;
//        if (xcls != null) {
//            switch (xcls.ClsType) {
//                case XClsType.XFds: {
//                    bRtn = true;
//                    break;
//                }
//                case XClsType.XSFCls: {
//                    SFeatureCls sfcls = xcls as SFeatureCls;
//                    if (sfcls != null)
//                        bRtn = sfcls.Close();
//                    break;
//                }
//                case XClsType.XACls: {
//                    AnnotationCls acls = xcls as AnnotationCls;
//                    if (acls != null)
//                        bRtn = acls.Close();
//                    break;
//                }
//                case XClsType.XOCls: {
//                    ObjectCls ocls = xcls as ObjectCls;
//                    if (ocls != null)
//                        bRtn = ocls.Close();
//                    break;
//                }
//                case XClsType.RCls: {
//                    RelationCls rcls = xcls as RelationCls;
//                    if (rcls != null)
//                        bRtn = rcls.Close();
//                    break;
//                }
//                case XClsType.XGNet: {
//                    NetCls ncls = xcls as NetCls;
//                    if (ncls != null)
//                        bRtn = ncls.Close();
//                    break;
//                }
//                case XClsType.MapSet: {
//                    MapSet mcls = xcls as MapSet;
//                    if (mcls != null)
//                        bRtn = mcls.Close();
//                    break;
//                }
//                case XClsType.XRcat: {
//                    RasterCatalog rccls = xcls as RasterCatalog;
//                    if (rccls != null)
//                        bRtn = rccls.Close();
//                    break;
//                }
//                case XClsType.XRds: {
//                    RasterDataSet rdcls = xcls as RasterDataSet;
//                    if (rdcls != null)
//                        bRtn = rdcls.Close();
//                    break;
//                }
//                default:
//                    break;
//            }
//        } else
//            bRtn = true;
//        return bRtn;
//    }
//
//    /// <summary>
//    /// 根据URL删除类，支持6x数据及磁盘文件
//    /// </summary>
//    /// <param name="url"></param>
//    /// <returns></returns>
//    public static boolean DeleteXCls(String url)
//    {
//        boolean bRtn = false;
//        if (Is6xURL(url))
//            bRtn = DeleteFile(url);
//        else if (File.Exists(url))
//            bRtn |= DeleteFile(url);
//        else {
//            if (!XString.isNullOrEmpty(url)) {
//                int clsid = 0;
//                int dsid = 0;
//                XClsType type = XClsType.Unknown;
//                String name = "";
//                DataBase db = GetXClsInfo(url, out clsid, out dsid, out type, out name);
//                if (db != null && clsid > 0 && type != XClsType.Unknown)
//                    bRtn = DeleteXCls(db, type, clsid);
//                if (db != null)
//                    db.Close();
//            }
//        }
//        return bRtn;
//    }
//
//    /// <summary>
//    /// 删除类，支持6x数据
//    /// </summary>
//    /// <param name="xcls"></param>
//    /// <returns></returns>
//    public static boolean DeleteXCls(IBasCls xcls)
//    {
//        boolean bRtn = false;
//        if (xcls != null) {
//            String url = xcls.URL;
//            int clsid = xcls.ClsID;
//            XClsType type = xcls.ClsType;
//            DataBase db = xcls.GDataBase;
//            String dbUrl = null;
//            if (db != null) {
//                dbUrl = db.URL;
//            }
//            CloseXCls(xcls);
//            DataBase db1 = DataBase.openByURL(dbUrl);
//            if (db1 != null) {
//                bRtn = DeleteXCls(db1, type, clsid);
//                db1.Close();
//            }
//            if (Is6xURL(url))
//                bRtn |= DeleteFile(url);
//            else if (File.Exists(url))
//                bRtn |= DeleteFile(url);
//        }
//        return bRtn;
//    }
//
//    /// <summary>
//    /// 删除类
//    /// </summary>
//    /// <param name="db"></param>
//    /// <param name="type"></param>
//    /// <param name="clsid"></param>
//    /// <returns></returns>
//    public static boolean DeleteXCls(DataBase db, XClsType type, int clsid)
//    {
//        boolean bRtn = false;
//        if (db != null && type != XClsType.Unknown && clsid > 0) {
//            switch (type) {
//                case XClsType.XFds: {
//                    bRtn = db.RemoveFds(clsid);
//                    break;
//                }
//                case XClsType.XSFCls: {
//                    bRtn = SFeatureCls.Remove(db, clsid);
//                    break;
//                }
//                case XClsType.XACls: {
//                    bRtn = AnnotationCls.Remove(db, clsid);
//                    break;
//                }
//                case XClsType.XOCls: {
//                    bRtn = ObjectCls.Remove(db, clsid);
//                    break;
//                }
//                case XClsType.RCls: {
//                    bRtn = RelationCls.Remove(db, clsid);
//                    break;
//                }
//                case XClsType.XGNet: {
//                    bRtn = NetCls.Remove(db, clsid);
//                    break;
//                }
//                case XClsType.MapSet: {
//                    bRtn = MapSet.Remove(db, clsid);
//                    break;
//                }
//                case XClsType.XRcat: {
//                    bRtn = RasterCatalog.DeleteCatalog(db, clsid);
//                    break;
//                }
//                case XClsType.XRds: {
//                    bRtn = RasterDataSet.DeleteFromGDB(db, clsid);
//                    break;
//                }
//                default:
//                    break;
//            }
//        }
//        return bRtn;
//    }
//
//    /// <summary>
//    /// 删除类（不支持要素数据集、地图集、栅格目录、栅格数据集）
//    /// </summary>
//    /// <param name="db"></param>
//    /// <param name="type"></param>
//    /// <param name="name"></param>
//    /// <returns></returns>
//    public static boolean DeleteXCls(DataBase db, XClsType type, String name)
//    {
//        boolean bRtn = false;
//        if (db != null && type != XClsType.Unknown && !XString.isNullOrEmpty(name)) {
//            switch (type) {
//                case XClsType.XFds: {
//                    break;
//                }
//                case XClsType.XSFCls: {
//                    bRtn = SFeatureCls.Remove(db, name);
//                    break;
//                }
//                case XClsType.XACls: {
//                    bRtn = AnnotationCls.Remove(db, name);
//                    break;
//                }
//                case XClsType.XOCls: {
//                    bRtn = ObjectCls.Remove(db, name);
//                    break;
//                }
//                case XClsType.RCls: {
//                    bRtn = RelationCls.Remove(db, name);
//                    break;
//                }
//                case XClsType.XGNet: {
//                    bRtn = NetCls.Remove(db, name);
//                    break;
//                }
//                case XClsType.MapSet: {
//                    break;
//                }
//                case XClsType.XRcat: {
//                    break;
//                }
//                case XClsType.XRds: {
//                    break;
//                }
//                default:
//                    break;
//            }
//        }
//        return bRtn;
//    }
//
//    /// <summary>
//    /// 判断URL是否已经存在，支持6x数据及磁盘文件
//    /// </summary>
//    /// <param name="url"></param>
//    /// <returns>对于数据源和数据库验证有效性（会打开又关闭一次），对于数据集（要素数据集或栅格目录）及数据验证是否存在</returns>
//    public static boolean HasExisted(String url)
//    {
//        boolean bRtn = false;
//        if (File.Exists(url))
//            bRtn = true;
//        else if (Is6xURL(url))
//            bRtn = IsFileExisted(url);
//        else {
//            if (!XString.isNullOrEmpty(url)) {
//                String user = "";
//                String psw = "";
//                String server = "";
//                String database = "";
//                String type = "";
//                String ds = "";
//                String name = "";
//                int rtn = getURLInfo(url, out user, out psw, out server, out database, out ds, out type, out name);
//                switch (rtn) {
//                    case 1: {
//                        //region 验证数据源有效性
//                        Server svr = new Server();
//                        if (svr.Connect(server, user, psw)) {
//                            bRtn = true;
//                            svr.DisConnect();
//                        }
//                        svr.Dispose();
//                        if (!bRtn && !XString.isNullOrEmpty(server) && !XString.isNullOrEmpty(user) && XString.isNullOrEmpty(psw)) {
//                            String curUser;
//                            String curPwd;
//                            if (Server.GetLogInfo(server, out curUser, out curPwd)) {
//                                if (curUser == user) {
//                                    Server svr2 = new Server();
//                                    if (svr2.Connect(server, curUser, curPwd)) {
//                                        bRtn = true;
//                                        svr2.DisConnect();
//                                    }
//                                    svr2.Dispose();
//                                }
//                            }
//                        }
//                        break;
//                        //endregion
//                    }
//                    case 2: {
//                        //region 验证数据库有效性
//                        server = combineServer(user, psw, server);
//                        if (!XString.isNullOrEmpty(server) && !XString.isNullOrEmpty(database)) {
//                            database = server + "/" + database;
//                            DataBase db = DataBase.openByURL(database);
//                            if (db != null && db.HasOpened) {
//                                bRtn = true;
//                                db.Close();
//                                db.Dispose();
//                            }
//                        }
//                        break;
//                        //endregion
//                    }
//                    case 3:
//                    case 4: {
//                        //region 验证数据是否存在
//                        server = combineServer(user, psw, server);
//                        if (!XString.isNullOrEmpty(server) && !XString.isNullOrEmpty(database)) {
//                            database = server + "/" + database;
//                            DataBase db = DataBase.openByURL(database);
//                            if (db != null && db.HasOpened) {
//                                if (db.XClsIsExist(stringToXClsType(type), name) > 0)
//                                    bRtn = true;
//                                db.Close();
//                                db.Dispose();
//                            }
//                        }
//                        break;
//                        //endregion
//                    }
//                }
//            }
//        }
//        return bRtn;
//    }
//
//    /// <summary>
//    /// 判断URL是否合法有效（不验证是否存在），支持6x数据及磁盘文件
//    /// </summary>
//    /// <param name="url"></param>
//    /// <returns></returns>
//    public static boolean IsValid(String url)
//    {
//        boolean bRtn = false;
//        if (File.Exists(url))
//            bRtn = true;
//        else if (Is6xURL(url))
//            bRtn = IsValidOfFileURL(url);
//        else {
//            if (!XString.isNullOrEmpty(url)) {
//                String ds = "";
//                String xclsName = "";
//                String user = "";
//                String psw = "";
//                String server = "";
//                String database = "";
//                String strtype = "";
//                bRtn = getURLInfo(url, out user, out psw, out server, out database, out ds, out strtype, out xclsName) > 0;
//            }
//        }
//        return bRtn;
//    }
    //
    ///// <summary>
    ///// 根据数据URL获取信息（此过程不会打开类，但会打开数据库）
    ///// </summary>
    ///// <param name="url"></param>
    ///// <param name="clsid"></param>
    ///// <param name="dsid"></param>
    ///// <param name="type"></param>
    ///// <param name="name"></param>
    ///// <returns></returns>
    //public static DataBase GetXClsInfo(String url, out int clsid, out int dsid, out XClsType type, out String name)
    //{
    //    DataBase db = null;
    //    clsid = 0;
    //    dsid = 0;
    //    type = XClsType.Unknown;
    //    name = "";
    //    if (!XString.isNullOrEmpty(url)) {
    //        String user = "";
    //        String psw = "";
    //        String server = "";
    //        String database = "";
    //        String strtype = "";
    //        String ds = "";
    //        String xclsname = "";
    //        int rtn = getURLInfo(url, out user, out psw, out server, out database, out ds, out strtype, out xclsname);
    //        if (rtn > 0) {
    //            type = stringToXClsType(strtype);
    //            name = xclsname;
    //            server = combineServer(user, psw, server);
    //            if (!XString.isNullOrEmpty(server) && !XString.isNullOrEmpty(database)) {
    //                database = server + "/" + database;
    //                db = DataBase.openByURL(database);
    //                if (db != null && type != XClsType.Unknown) {
    //                    clsid = db.XClsIsExist(type, name);
    //                    if (!XString.isNullOrEmpty(ds))
    //                        dsid = db.XClsIsExist(XClsType.XFds, ds);
    //                }
    //            }
    //        }
    //    }
    //    return db;
    //}
//
//    /// <summary>
//    /// 创建临时矢量类
//    /// </summary>
//    /// <param name="name">为空时则自动创建名称</param>
//    /// <param name="geom">为空时则缺省为区简简单要素类</param>
//    /// <returns></returns>
//    public static IBasCls CreateTempVectorCls(String name="", GeomType geom=GeomType.Reg)
//    {
//        IBasCls xcls = null;
//        DataBase db = DataBase.OpenTempDB();
//        if (db != null) {
//            String xclsname = name;
//            if (XString.isNullOrEmpty(xclsname))
//                xclsname = Guid.NewGuid().ToString();
//            switch (geom) {
//                case GeomType.Pnt:
//                case GeomType.Lin:
//                case GeomType.Reg:
//                case GeomType.Surface:
//                case GeomType.Entity:
//                    SFeatureCls sfcls = new SFeatureCls(db);
//                    if (sfcls.Create(xclsname, geom, 0, 0, null) > 0)
//                        xcls = sfcls;
//                    else
//                        sfcls.Dispose();
//                    break;
//                case GeomType.Ann:
//                    AnnotationCls acls = new AnnotationCls(db);
//                    if (acls.Create(xclsname, AnnType.Text, 0, 0, null) > 0)
//                        xcls = acls;
//                    else
//                        acls.Dispose();
//                    break;
//                case GeomType.Unknown:
//                    break;
//            }
//        }
//        return xcls;
//    }
//    //endregion
//
//    //region 对6x支持
//    /// <summary>
//    /// 判断是否为6x路径
//    /// </summary>
//    /// <param name="url"></param>
//    /// <returns></returns>
//    private static boolean Is6xURL(String url)
//    {
//        boolean bRtn = false;
//        if (!XString.isNullOrEmpty(url)) {
//            if (url.trim().indexOf(FilePro, StringComparison.CurrentCultureIgnoreCase) == 0) {
//                int i = url.LastIndexOf(".");
//                if (i > 0) {
//                    String ext = url.substring(i + 1);
//                    int j = ext.indexOf("@");
//                    if (j > 0)
//                        ext = ext.substring(0, j);
//                    if (String.Compare(ext, "wt", true) == 0 || String.Compare(ext, "wl", true) == 0
//                            || String.Compare(ext, "wp", true) == 0 || String.Compare(ext, "wb", true) == 0)
//                        bRtn = true;
//                }
//            } else if (url.trim().indexOf(GDBPPro, StringComparison.CurrentCultureIgnoreCase) < 0) {
//                int i = url.LastIndexOf(".");
//                if (i > 0) {
//                    String ext = url.substring(i + 1);
//                    int j = ext.indexOf("@");
//                    if (j > 0)
//                        ext = ext.substring(0, j);
//                    if (String.Compare(ext, "wt", true) == 0 || String.Compare(ext, "wl", true) == 0
//                            || String.Compare(ext, "wp", true) == 0 || String.Compare(ext, "wb", true) == 0)
//                        bRtn = true;
//                }
//            }
//        }
//        return bRtn;
//    }
//
//    /// <summary>
//    /// 判断是否为栅格文件路径
//    /// </summary>
//    /// <param name="url"></param>
//    /// <returns></returns>
//    private static boolean IsRasterURL(String url)
//    {
//        boolean rtn = false;
//        if (!XString.isNullOrEmpty(url) && File.Exists(url)) {
//            String ext = Path.GetExtension(url);
//            if (!XString.isNullOrEmpty(ext)) {
//                String[] filters = GetImgFilters();
//                if (filters != null && filters.Length > 0) {
//                    foreach(String filter in filters)
//                    {
//                        if (String.Compare(filter, ext, true) == 0) {
//                            rtn = true;
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//        return rtn;
//    }
//
//    /// <summary>
//    /// 获取带扩展名的所有影像文件过滤符(例如返回结果：*.msi;*.img;*.tif;*.grd)
//    /// </summary>
//    /// <returns></returns>
//    private static String[] GetImgFilters()
//    {
//        List<String> rtn = new List<String>()
//        {
//            ".msi",".img",".tif",".pix",".jpg",".bmp",".gif",".png",".jp2",".grd"
//        };
//        List<String> rtn1 = new List<String>();
//        RasFileExtInfo[] infos = RasterDataSet.GetRasFileExtInfo(RasFileExtType.Input);
//        if (infos != null && infos.Length > 0) {
//            foreach(RasFileExtInfo info in infos)
//            {
//                if (!info.Descripe.Contains("*.*")) {
//                    if (info.FileExt1 != null && info.FileExt1.trim().Length > 0)
//                        rtn1.Add("." + info.FileExt1);
//                    if (info.FileExt2 != null && info.FileExt2.trim().Length > 0)
//                        rtn1.Add("." + info.FileExt2);
//                }
//            }
//        }
//        return (rtn1.Count > 0 ? rtn1 : rtn).ToArray();
//    }
//
//    /// <summary>
//    /// 文件路径是否有效
//    /// </summary>
//    /// <param name="url"></param>
//    /// <returns></returns>
//    private static boolean IsValidOfFileURL(String url)
//    {
//        boolean bRtn = false;
//        if (!XString.isNullOrEmpty(url)) {
//            String temp = url.trim();
//            if (temp.IndexOfAny(Path.GetInvalidPathChars()) < 0) {
//                if (temp.indexOf(FilePro, StringComparison.CurrentCultureIgnoreCase) == 0 || temp.indexOf(GDBPPro, StringComparison.CurrentCultureIgnoreCase) < 0) {
//                    if (temp.IndexOfAny(new char[]{':'}) > 0)
//                        bRtn = true;
//                }
//            }
//        }
//        return bRtn;
//    }
//
//    /// <summary>
//    /// 判断6x路径文件是否已经存在
//    /// </summary>
//    /// <param name="url"></param>
//    /// <returns></returns>
//    private static boolean IsFileExisted(String url)
//    {
//        boolean bRtn = false;
//        if (!XString.isNullOrEmpty(url)) {
//            String temp = url.trim();
//            if (temp.StartsWith(FilePro, StringComparison.CurrentCultureIgnoreCase))
//                temp = temp.substring(FilePro.Length);
//            int i = temp.LastIndexOf("@");
//            if (i > 0)
//                temp = temp.substring(0, i);
//            try {
//                bRtn = !File.Exists(temp);
//            } catch {
//            }
//        }
//        return bRtn;
//    }
//
//    /// <summary>
//    /// 删除文件
//    /// </summary>
//    /// <param name="url"></param>
//    /// <returns></returns>
//    private static boolean DeleteFile(String url)
//    {
//        boolean bRtn = false;
//        if (!XString.isNullOrEmpty(url)) {
//            String temp = url.trim();
//            if (temp.StartsWith(FilePro, StringComparison.CurrentCultureIgnoreCase))
//                temp = temp.substring(FilePro.Length);
//            int i = temp.LastIndexOf("@");
//            if (i > 0)
//                temp = temp.substring(0, i);
//            try {
//                File.Delete(temp);
//            } catch {
//            }
//            try {
//                bRtn = !File.Exists(temp);
//            } catch {
//            }
//        } else
//            bRtn = true;
//        return bRtn;
//    }
//    //endregion
}
