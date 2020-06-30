package com.zondy.mapgis.gdbmanager;

import com.zondy.mapgis.base.XString;
import com.zondy.mapgis.geodatabase.config.ConnectType;
import com.zondy.mapgis.geodatabase.config.DataSrcInfo;
import com.zondy.mapgis.geodatabase.config.SvcConfig;
import com.zondy.mapgis.geodatabase.Server;
import com.zondy.mapgis.geodatabase.XClsType;
import com.zondy.mapgis.geodatabase.middleware.*;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.IFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author CR
 * @file CommonFunctions.java
 * @brief 工程通用的静态方法
 * @create 2019-12-04.
 */
public class CommonFunctions
{
    /**
     * 判断数据源是否支持导出功能：SuperMap中间件数据源不支持
     *
     * @param ds 数据源对象
     * @return true支持导出，false不支持
     */
    public static boolean canImportExport(Server ds)
    {
        boolean canExport = true;
        if (ds != null && ConnectType.Custom.equals(ds.getConnectType()))
        {
            DataSrcInfo dsInfo = SvcConfig.get(ds.getSvrName());
            if (dsInfo != null && !XString.isNullOrEmpty(dsInfo.getDnsName()))
            {
                MiddleWareConfig middleWareConfig = new MiddleWareConfig();
                middleWareConfig.open();
                String dnsName = dsInfo.getDnsName();
                int index = dnsName.indexOf("&");
                String mwName = index >= 0 ? dnsName.substring(0, index) : dnsName;
                MiddleWareInfo mwInfo = middleWareConfig.getItemByName(mwName);
                if (mwInfo != null && "SupMp_Manager.dll".equalsIgnoreCase(mwInfo.getManageDLL()))
                {
                    canExport = false;
                }
            }
        }
        return canExport;
    }

    /**
     * 判断数据源是否支持添加数据（创建、粘贴、导入）
     *
     * @param ds 数据源对象
     * @return true支持添加
     */
    public static boolean canAddData(Server ds)
    {
        boolean canAdd = false;
        if (ds != null)
        {
            switch (ds.getConnectType())
            {
                case Local:
                case LocalPlus:
                case DBSQL:
                case DBOracle:
                case DBSybase:
                case DBPG:
                case DBDm:
                    canAdd = true;
                    break;
                default:
                    break;
            }
        }
        return canAdd;
    }

    /**
     * 获取中间件数据源的中间件类型
     *
     * @param server 中间件数据中间件数据源的中间件类型源
     * @return
     */
    public static MiddleWareType getCustomServerType(Server server)
    {
        MiddleWareType mwType = null;
        if (server != null && ConnectType.Custom.equals(server.getConnectType()))
        {
            DataSrcInfo dsInfo = SvcConfig.get(server.getSvrName());
            if (dsInfo != null && !XString.isNullOrEmpty(dsInfo.getDnsName()))
            {
                MiddleWareConfig middleWareConfig = new MiddleWareConfig();
                middleWareConfig.open();
                String dnsName = dsInfo.getDnsName();
                int index = dnsName.indexOf("&");
                String mwName = index >= 0 ? dnsName.substring(0, index) : dnsName;
                MiddleWareInfo mwInfo = middleWareConfig.getItemByName(mwName);
                if (mwInfo != null)
                {
                    mwType = mwInfo.getMidWareType();
                }
                middleWareConfig.close();
            }
        }
        return mwType;
    }

    /**
     * 获取右键菜单？？？
     *
     * @param server 中间件数据中间件数据源的中间件类型源
     * @return
     */
    public static List<Integer> getCustomDataSourceMenuList(Server server)
    {
        List<Integer> list = null;
        if (server != null && server.hasConnected() && ConnectType.Custom.equals(server.getConnectType()))
        {
            list = new ArrayList<>();
            DataSrcInfo dsInfo = SvcConfig.get(server.getSvrName());
            if (dsInfo != null && !XString.isNullOrEmpty(dsInfo.getDnsName()))
            {
                MiddleWareConfig middleWareConfig = new MiddleWareConfig();
                middleWareConfig.open();
                String dnsName = dsInfo.getDnsName();
                int index = dnsName.indexOf("&");
                String mwName = index >= 0 ? dnsName.substring(0, index) : dnsName;
                MiddleWareConfigFactory fac = new MiddleWareConfigFactory(mwName);
                int[] chk = fac.catalogPopMenuValidChk(MenuItemType.DataSource);
                middleWareConfig.close();

                if (chk != null && chk.length > 0)
                {
                    for (int i : chk)
                    {
                        list.add(i);
                    }
                }
            }
        }
        return list;
    }

    /**
     * 获取给定数据源的数据库管理员
     *
     * @param server 数据源对象
     * @return 数据源的数据库管理员
     */
    public static String getAdministratorName(Server server)
    {
        String name = "";
        if (server != null)
        {
            ConnectType connectType = server.getConnectType();
            if (ConnectType.DBDB2.equals(connectType) || ConnectType.DBGBase.equals(connectType) || ConnectType.DBBeyon.equals(connectType))
            {
                name = "Admin";
            } else if (ConnectType.DBDm.equals(connectType))
            {
                name = "sysdba";
            } else if (ConnectType.DBMySQL.equals(connectType))
            {
                name = "root";
            } else if (ConnectType.DBSQL.equals(connectType) || ConnectType.DBSybase.equals(connectType))
            {
                name = "sa";
            } else
            {
                name = "sys";
            }
        }
        return name;
    }

    /**
     * 获取给定数据源的主数据库名称
     *
     * @param server 数据源对象
     * @return 数据源的主数据库名称
     */
    public static String getMasterDBName(Server server)
    {
        String dbName = "";
        if (server != null)
        {
            ConnectType conType = server.getConnectType();
            if (ConnectType.DBDB2.equals(conType))
            {
                dbName = "MPDBMAST";
            } else if (ConnectType.DBPG.equals(conType))
            {
                dbName = "";
            } else
            {
                dbName = "MPDBMASTER";
            }
        }
        return dbName;
    }

    /**
     * 是否加载了指定插件，加载了才能用里面的功能，如导入导出。
     *
     * @param app         应用程序框架
     * @param functionKey 方法的Key，是IFunction的namespace加上.IFunction的名称，如MapGIS.DataConvert.Plugin.DataConvertFunction
     * @return 加载了返回true，否则返回false
     */
    public static boolean hasLoadedPluginFunction(IApplication app, String functionKey)
    {
        //boolean loaded = false;
        //if (app != null)
        //{
        //    IFunction func = app.getPluginContainer().getFunctions().get(functionKey);
        //    loaded = (func != null);
        //}
        //return loaded;
        return true;
    }

    //------------------分界线---------------------




    public static final String copySingleObject = "MapGISSingleObject";//复制单个数据时Clipboard中用到的format
    public static final String copyMultiObjects = "MapGISMultiObjects";//复制多个数据时Clipboard中用到的format
    public static final String imExportFunctionKey = "MapGIS.DataConvert.Plugin.DataConvertFunction";  //导入导出的功能Key//未完成
    public static final String attStatisticsFunctionKey = "MapGIS.AttStatistics.Plugin.AttributesStatisticsFunction";//属性统计的功能Key//未完成

    /**
     * 复制单个数据时Clipboard中用到的format，复制的是其URL
     *
     * @return
     */
    public static String getCopySingleObject()
    {
        return CommonFunctions.copySingleObject;
    }

    /**
     * 复制多个数据时Clipboard中用到的format，复制的是数据URL的集合，即List<String>
     *
     * @return
     */
    public static String getCopyMultiObjects()

    {
        return CommonFunctions.copyMultiObjects;
    }

    /**
     * 导入导出的功能Key//未完成
     *
     * @return
     */
    public static String getImExportFunctionKey()
    {
        return CommonFunctions.imExportFunctionKey;
    }

    /**
     * 属性统计的功能Key//未完成
     *
     * @return
     */
    public static String getAttStatisticsFunctionKey()
    {
        return CommonFunctions.attStatisticsFunctionKey;
    }

    /// <summary>
    /// 根据剪贴板上的数据判断能否粘贴，用于设置右键菜单中的“粘贴”的Enable属性
    /// </summary>
    /// <param name="clsTypes">能粘贴的数据类型</param>
    /// <returns>能粘贴返回true，否则返回false</returns>
    public static boolean clipboardContains(XClsType... clsTypes)
    {
        boolean canPaste = false;
        if (clsTypes != null)
        {
            List<XClsType> clsTypeList = new ArrayList<>();
            clsTypeList.addAll(Arrays.asList(clsTypes));

            //未完成
            //if (Clipboard.ContainsData(CommonFunctions.getCopySingleObject()))
            //{
            //    String srcUrl = Clipboard.GetData(CommonFunctions.CopySingleObject) as String;
            //    XClsType clsType = MapGIS.Desktop.UI.Controls.WorkSpaceTree.GetTypeFromUrl(srcUrl);
            //    canPaste = clsTypeList.Contains(clsType);
            //} else if (Clipboard.ContainsData(CommonFunctions.CopyMultiObjects))
            //{
            //    List<String> srcUrlList = Clipboard.GetData(CommonFunctions.CopyMultiObjects) as List<String >;
            //    if (srcUrlList != null && srcUrlList.Count > 0)
            //    {
            //        foreach(String srcUrl in srcUrlList)
            //        {
            //            XClsType clsType = MapGIS.Desktop.UI.Controls.WorkSpaceTree.GetTypeFromUrl(srcUrl);
            //            canPaste = clsTypeList.Contains(clsType);
            //            if (!canPaste)
            //                break;
            //        }
            //    }
            //}
        }
        return canPaste;
    }

    /// <summary>
    /// 获取导出时的默认目标类型
    /// </summary>
    /// <param name="srcClsType">源数据类型</param>
    /// <param name="expSign">导出标志，0-6x数据，1-GDB数据，2-表格数据，3-其他数据。根据他们传默认的目标类型。但如果下面处理好了就不需要</param>
    /// <returns>导出时的默认目标类型</returns>
    public static String GetDesDataType(XClsType srcClsType, int expSign)
    {
        String dType = "UnKnown";
        //未完成
        //switch (srcClsType)
        //{
        //    case XClsType.XSFCls:
        //    {
        //        //region 简单要素类
        //
        //        switch (expSign)
        //        {
        //            case 0:
        //                dType = "MapGIS_6X_FILE";
        //                break;
        //            case 1:
        //                dType = "MapGIS_SFCLS";
        //                break;
        //            case 2:
        //                dType = "Excel_Table";
        //                break;
        //            case 3:
        //                dType = "SHP_FILE";
        //                break;
        //            default:
        //                break;
        //        }
        //        break;
        //
        //        //endregion
        //    }
        //    case XClsType.XACls:
        //    {
        //        //region 注记类
        //
        //        switch (expSign)
        //        {
        //            case 0:
        //                dType = "MapGIS_6X_FILE";
        //                break;
        //            case 1:
        //                dType = "MapGIS_ACLS";
        //                break;
        //            case 2:
        //                dType = "Excel_Table";
        //                break;
        //            case 3:
        //                dType = "MIF_FILE";
        //                break;
        //            default:
        //                break;
        //        }
        //        break;
        //
        //        //endregion
        //    }
        //    case XClsType.XOCls:
        //    {
        //        //region 对象类
        //
        //        switch (expSign)
        //        {
        //            case 0:
        //                dType = "MapGIS_6X_Table";
        //                break;
        //            case 1:
        //                dType = "MapGIS_OCLS";
        //                break;
        //            case 2:
        //                dType = "Excel_Table";
        //                break;
        //            default:
        //                break;
        //        }
        //        break;
        //
        //        //endregion
        //    }
        //    case XClsType.XRcat:
        //    {
        //        //region 栅格目录
        //
        //        switch (expSign)
        //        {
        //            case 1:
        //                dType = "MapGIS_RCAT";
        //                break;
        //            default:
        //                break;
        //        }
        //        break;
        //
        //        //endregion
        //    }
        //    case XClsType.XRds:
        //    {
        //        //region 栅格数据集
        //
        //        switch (expSign)
        //        {
        //            case 1:
        //                dType = "MapGIS_RAS";
        //                break;
        //            case 3:
        //            case 4:
        //                dType = "Raster_FILE";
        //                break;
        //            default:
        //                break;
        //        }
        //        break;
        //
        //        //endregion
        //    }
        //    case XClsType.XFds:
        //    {
        //        switch (expSign)
        //        {
        //            case 5:
        //                dType = "ArcGIS_PersonalGDB";
        //                break;
        //            case 6:
        //                dType = "ArcGIS_FileGDB";
        //                break;
        //            default:
        //                break;
        //        }
        //        break;
        //    }
        //    case (XClsType) (-2)://gdb
        //    {
        //        switch (expSign)
        //        {
        //            case 5:
        //                dType = "ArcGIS_PersonalGDB";
        //                break;
        //            case 6:
        //                dType = "ArcGIS_FileGDB";
        //                break;
        //            default:
        //                break;
        //        }
        //        break;
        //    }
        //    default:
        //        break;
        //}
        return dType;
    }
}
