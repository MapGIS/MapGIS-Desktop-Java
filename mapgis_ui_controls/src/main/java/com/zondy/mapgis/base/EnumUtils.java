package com.zondy.mapgis.base;

import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 枚举处理
 *
 * @author CR
 * @file EnumUtils.java
 * @brief 根据枚举值获取枚举项
 * @create 2020-03-10.
 */
public class EnumUtils
{
    /**
     * 根据value值获取enum对象（getValue方法）
     *
     * @param enumClass 枚举类
     * @param value     枚举值
     * @return 对应枚举
     */
    public static <E extends Enum<E>> E valueOf(Class<E> enumClass, Object value)
    {
        return valueOf(enumClass, value, "getValue");
    }

    /**
     * 值映射为枚举
     *
     * @param enumClass  枚举类
     * @param value      枚举值
     * @param methodName 取值方法名称
     * @return 对应枚举
     */
    public static <E extends Enum<?>> E valueOf(Class<E> enumClass, Object value, String methodName)
    {
        E rtn = null;
        E[] es = enumClass.getEnumConstants();
        try
        {
            Method method = enumClass.getMethod(methodName);
            for (E e : es)
            {
                Object eValue = null;
                method.setAccessible(true);
                eValue = method.invoke(e);
                if (value instanceof Number && eValue instanceof Number && new BigDecimal(String.valueOf(value)).compareTo(new BigDecimal(String.valueOf(eValue))) == 0)
                {
                    rtn = e;
                    break;
                }
                if (Objects.equals(eValue, value))
                {
                    rtn = e;
                    break;
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex)
        {
            ex.printStackTrace();
        }
        return rtn;
    }

    /**
     * 根据text获取enum对象
     *
     * @param enumClass 枚举类
     * @param text      枚举值
     * @return 对应枚举
     */
    public static <E extends Enum<E>> E valueOfText(final Class<E> enumClass, String text)
    {
        return valueOf(enumClass, text, "getText");
    }

    /**
     * 根据value值获取text
     *
     * @param enumClass 枚举类
     * @param value     枚举值
     * @return value值对应的text
     */
    public static <E extends Enum<E>> String getText(final Class<E> enumClass, Object value)
    {
        String str = null;
        E e = valueOf(enumClass, value);
        Object eValue = null;
        Method method = null;
        try
        {
            method = enumClass.getMethod("getText");
            method.setAccessible(true);
            eValue = method.invoke(e);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex)
        {
            ex.printStackTrace();
        }
        if (eValue != null)
        {
            str = String.valueOf(eValue);
        }
        return str;
    }

    /**
     * 值(包含指定值）映射为枚举
     *
     * @param enumClass  枚举类
     * @param value      枚举值
     * @param methodName 取值方法名称
     * @return 对应枚举
     */
    public static <E extends Enum<?>> E valueContains(Class<E> enumClass, Object value, String methodName)
    {
        E rtn = null;
        E[] es = enumClass.getEnumConstants();
        try
        {
            Method method = enumClass.getMethod(methodName);
            for (E e : es)
            {
                Object eValue = null;
                method.setAccessible(true);
                eValue = method.invoke(e);
                List list = null;
                if (eValue instanceof List)
                {
                    list = (List) eValue;
                } else if (eValue.getClass().isArray())
                {
                    list = Arrays.asList(eValue);
                }

                if (list != null && list.contains(value))
                {
                    rtn = e;
                    break;
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex)
        {
            ex.printStackTrace();
        }
        return rtn;
    }
}