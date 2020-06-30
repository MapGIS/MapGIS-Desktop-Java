package com.zondy.mapgis.dataconvert;

import com.zondy.mapgis.base.*;
import javafx.stage.*;
import javafx.stage.Window;

import java.util.*;

/**
 * @author CR
 * @file DataConverts.java
 * @brief 数据转换工具类
 * @create 2020-03-31.
 */
public class DataConverts
{
    private static DataConvertDialog dataConvertDialog;
    private static String destPath = "";

    /**
     * 数据转换
     *
     * @param owner 父窗口
     * @return
     */
    public static DataConvertDialog convert(Window owner)
    {
        return convert(owner, "");
    }

    /**
     * 数据转换
     *
     * @param owner  父窗口
     * @param srcUrl 源文件
     * @return
     */
    public static DataConvertDialog convert(Window owner, String srcUrl)
    {
        return convert(owner, srcUrl, "");
    }

    /**
     * 数据转换
     *
     * @param owner  父窗口
     * @param srcUrl 源文件
     * @return
     */
    public static DataConvertDialog convert(Window owner, String srcUrl, String destPath)
    {
        List<String> list = XString.isNullOrEmpty(srcUrl) ? null : Arrays.asList(srcUrl);
        return convert(owner, list, destPath);
    }

    /**
     * 数据转换
     *
     * @param owner   父窗口
     * @param srcUrls 源文件
     * @return
     */
    public static DataConvertDialog convert(Window owner, String[] srcUrls)
    {
        return convert(owner, srcUrls, "");
    }

    /**
     * 数据转换
     *
     * @param owner   父窗口
     * @param srcUrls 源文件
     * @return
     */
    public static DataConvertDialog convert(Window owner, String[] srcUrls, String destPath)
    {
        return convert(owner, Arrays.asList(srcUrls), destPath);
    }

    /**
     * 数据转换
     *
     * @param owner   父窗口
     * @param srcUrls 源文件
     * @return
     */
    public static DataConvertDialog convert(Window owner, List<String> srcUrls)
    {
        return convert(owner, srcUrls, "");
    }

    /**
     * 数据转换
     *
     * @param owner    父窗口
     * @param srcUrls  源文件
     * @param destPath 目标路径
     * @return
     */
    public static DataConvertDialog convert(Window owner, List<String> srcUrls, String destPath)
    {
        DataConverts.destPath = destPath;

        if (dataConvertDialog == null)
        {
            dataConvertDialog = new DataConvertDialog();
            dataConvertDialog.initModality(Modality.WINDOW_MODAL);
            if (owner != null)
            {
                //dataConvertDialog.initOwner(owner);
            }
        }

        if (dataConvertDialog != null)
        {
            if (dataConvertDialog.isShowing())
            {
                ((Stage) dataConvertDialog.getDialogPane().getScene().getWindow()).toFront();
            } else
            {
                dataConvertDialog.show();
            }

            if (srcUrls != null && srcUrls.size() > 0)
            {
                dataConvertDialog.addConvertItems(srcUrls, DataConverts.destPath);
            }
        }
        return dataConvertDialog;
    }

    /**
     * 返回界面是否正在显示着
     *
     * @return
     */
    public static boolean isShowing()
    {
        return dataConvertDialog != null && dataConvertDialog.isShowing();
    }

    /**
     * 关闭对话框
     */
    public static void close()
    {
        if (dataConvertDialog != null)
        {
            ((Stage) dataConvertDialog.getDialogPane().getScene().getWindow()).close();
            dataConvertDialog = null;
        }
    }

    /**
     * 隐藏或显示对话框
     *
     * @param isVisible
     */
    public static void setVisible(boolean isVisible)
    {
        if (dataConvertDialog != null && dataConvertDialog.isShowing())
        {
            dataConvertDialog.show();
        }
    }

    public static String getDestPath()
    {
        return destPath;
    }
}
