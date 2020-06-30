package com.zondy.mapgis.base;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author CR
 * @file XString.java
 * @brief 字符串的一些静态方法
 * @create 2019-11-28.
 */
public class XString
{
    /**
     * 判断字串是否为null或者空字串
     *
     * @param string 字串
     * @return 为null或者空字串返回true
     */
    public static boolean isNullOrEmpty(String string)
    {
        return string == null || string.isEmpty();
    }

    public static boolean isNullOrEmpty(StringProperty string)
    {
        return string.isNull().get() || string.isEmpty().get();
    }

    /**
     * 或许字符数组中的任意字符在字符串中第一个匹配项的索引。
     *
     * @param string 字符串
     * @param anyOf  字符数组
     * @return 第一个匹配项的索引
     */
    public static int indexOfAny(String string, Character[] anyOf)
    {
        int index = -1;
        if (!isNullOrEmpty(string) && anyOf != null)
        {
            for (char c : anyOf)
            {
                index = string.indexOf(c);
                if (index >= 0)
                {
                    break;
                }
            }
        }
        return index;
    }

    /**
     * 删除字符串origText中从指定位置到最后的所有字符。
     *
     * @param origText   原始字符串
     * @param startIndex 删除的起始位置
     * @return 删除后的字串
     */
    public static String remove(String origText, int startIndex)
    {
        String str = origText;
        if (origText != null && origText.length() > startIndex)
        {
            str = origText.substring(0, startIndex);
        }
        return str;
    }

    /**
     * 从origText中的指定位置开始删除指定数目的字符
     *
     * @param origText   原始字符串
     * @param startIndex 删除的起始位置
     * @param removeLen  删除字符的数目
     * @return 删除后的字串
     */
    public static String remove(String origText, int startIndex, int removeLen)
    {
        String str = origText;
        if (origText != null && origText.length() >= startIndex + removeLen)
        {
            str = origText.substring(0, startIndex) + origText.substring(startIndex + removeLen);
        }
        return str;
    }

    /**
     * 分割字符串（去掉空项）
     *
     * @param string 待分割的字串
     * @param regex  分割字符
     * @return 分隔开的非空字符串数组
     */
    public static String[] splitRemoveEmpty(String string, String regex)
    {
        String[] strs = null;
        if (!isNullOrEmpty(string))
        {
            List<String> list = new ArrayList<>(Arrays.asList(string.split("/")));
            list.remove("");
            strs = list.toArray(new String[0]);
        }
        return strs;
    }

    /**
     * 在origText的指定索引位置插入insertText。
     *
     * @param origText   原操作字串
     * @param startIndex 插入位置
     * @param insertText 插入文本
     * @return 处理后的文本
     */
    public static String insert(String origText, int startIndex, String insertText)
    {
        String str = origText;
        if (origText != null && origText.length() > startIndex)
        {
            StringBuilder stringBuilder = new StringBuilder(origText);
            stringBuilder.insert(startIndex, insertText);
            str = stringBuilder.toString();
        }
        return str;
    }

    /**
     * 获取字符串的字节长度（汉字算两个字节）
     *
     * @param str 字符串
     * @return 字符串的字节长度
     */
    public static int getStringByteLength(String str)
    {
        int length = 0;
        if (!XString.isNullOrEmpty(str))
        {
            for (int i = 0; i < str.length(); i++)
            {
                int ascii = Character.codePointAt(str, i);
                if (ascii >= 0 && ascii <= 255)
                {
                    length++;
                } else
                {
                    length += 2;
                }
            }
        }
        return length;
    }

    /**
     * 从字符串中从头截取指定字节长度的字串
     *
     * @param str
     * @param lenByte
     * @return
     */
    public static String substringByte(String str, int lenByte)
    {
        String rtnStr = "";
        if (!XString.isNullOrEmpty(str) && lenByte > 0)
        {
            if (str.length() > lenByte)
            {
                str = str.substring(0, lenByte);
            }
            while (getStringByteLength(str) > lenByte)
            {
                str = str.substring(0, str.length() - 1);
            }
            rtnStr = str;
        }
        return rtnStr;
    }

    /// <summary>
    /// 类名称允许的最大字节长度（非字符长度）
    /// </summary>
    public final static int maxLengthOfClassName = 128;
    /// <summary>
    /// 图层名称允许的最大字节长度（非字符长度）
    /// </summary>
    public final static int maxLengthOfMapLayerName = 128;

    /**
     * 判断输入的新字串是否合法并返回错误信息
     *
     * @param newValue   新字串
     * @param maxByteLen 长度限制（长度必须小于它）
     * @return 文本有效返回true，否则返回false
     */
    public static boolean isTextValid(String newValue, int maxByteLen, StringProperty errorMsg)
    {
        return isTextValid(newValue, maxByteLen, null, errorMsg);
    }

    /**
     * 判断输入的新字串是否合法并返回错误信息
     *
     * @param newValue     新字串
     * @param maxByteLen   长度限制（长度必须小于它）
     * @param invalidChars 无效字符集（文本不能包括其中任何一个字符）
     * @return 文本有效返回true，否则返回false
     */
    public static boolean isTextValid(String newValue, int maxByteLen, List<Character> invalidChars, StringProperty errorMsg)
    {
        return isTextValid(newValue, maxByteLen, invalidChars, false, errorMsg);
    }

    /**
     * 判断输入的新字串是否合法并返回错误信息
     *
     * @param newValue      新字串
     * @param maxByteLen    长度限制（长度必须小于它）
     * @param invalidChars  无效字符集（文本不能包括其中任何一个字符）
     * @param firstNotSpace 首字母是否要求不能为空格
     * @param errorMsg      传出错误文本（要想有返回值，不能传null）
     * @return 文本有效返回true，否则返回false
     */
    public static boolean isTextValid(String newValue, int maxByteLen, List<Character> invalidChars, boolean firstNotSpace, StringProperty errorMsg)
    {
        if(errorMsg==null)
        {
            errorMsg = new SimpleStringProperty();
        }
    String strError ="";
        if (newValue != null)
        {
            if (firstNotSpace && newValue.startsWith(" "))
            {
                strError = "首字符不能输入空格。";
            } else if (XString.getStringByteLength(newValue) >= maxByteLen)
            {
                strError = String.format("必须少于%d个字符。", maxByteLen);
            } else if (invalidChars != null)
            {
                for (char invalidChar : invalidChars)
                {
                    if (newValue.indexOf(String.valueOf(invalidChar)) >= 0)
                    {
                        strError = "不能包含下列任何字符之一：\n";
                        int count = 0;
                        for (char ch : invalidChars)
                        {
                            if (ch > 32)//前面32个为ASCII码表中的控制字符，可能显示乱码现,只显示ASCII码值大于32的字符。
                            {
                                strError += " " + ch;
                                count++;
                            }
                            if (count > 12)
                            {
                                strError += "……";
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }
        errorMsg.set(strError);
        return strError == "";
    }
}
