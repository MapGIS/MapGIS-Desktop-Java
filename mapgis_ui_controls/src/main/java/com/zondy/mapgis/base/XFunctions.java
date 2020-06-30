package com.zondy.mapgis.base;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;

/**
 * @author CR
 * @file XFunctions.java
 * @brief 自定义public静态方法
 * @create 2020-03-19.
 */
public class XFunctions
{
    /**
     * 获取当前日期时间的字符串格式（2020-01-01 00:00:00)
     *
     * @return 日期时间的字符串格式（2020-01-01 00:00:00)
     */
    public static String getDateTimeString()
    {
        return getDateTimeString(new Date());
    }

    /**
     * 获取日期时间的字符串格式（2020-01-01 00:00:00)
     *
     * @param date 日期
     * @return 日期时间的字符串格式（2020-01-01 00:00:00)
     */
    public static String getDateTimeString(Date date)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }

    /**
     * 获取日期的字符串格式（2020-01-01)
     *
     * @return 日期的字符串格式（2020-01-01)
     */
    public static String getDateString()
    {
        return getDateString(new Date());
    }

    /**
     * 获取日期的字符串格式（2020-01-01)
     *
     * @param date 时间
     * @return 日期的字符串格式（2020-01-01)
     */
    public static String getDateString(Date date)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    /**
     * 获取当前时间的字符串格式（00:00:00)
     *
     * @return 时间的字符串格式（00:00:00)
     */
    public static String getTimeString()
    {
        return getTimeString(new Date());
    }

    /**
     * 获取时间的字符串格式（00:00:00)
     *
     * @param date 时间
     * @return 时间的字符串格式（00:00:00)
     */
    public static String getTimeString(Date date)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        return formatter.format(date);
    }

    /**
     * 判断系统是否是Windows
     *
     * @return
     */
    public static boolean isSystemWindows()
    {
        String osName = System.getProperty("os.name", "");
        return osName.startsWith("Windows");
    }

    /**
     * 判断系统是否是Linux
     *
     * @return
     */
    public static boolean isSystemLinux()
    {
        String osName = System.getProperty("os.name", "");
        return osName.startsWith("Linux");
    }
}
