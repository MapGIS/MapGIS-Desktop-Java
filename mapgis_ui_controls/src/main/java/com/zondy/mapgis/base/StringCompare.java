package com.zondy.mapgis.base;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * @author CR
 * @file StringCompare.java
 * @brief 字符串比较器，处理中文的排序
 * @create 2020-04-07.
 */
public class StringCompare implements Comparator<String>
{
    public int compare(String str1, String str2)
    {
        return Collator.getInstance(Locale.CHINA).compare(str1, str2);
    }
}
