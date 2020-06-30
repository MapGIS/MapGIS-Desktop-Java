package com.zondy.mapgis.fields;

import com.zondy.mapgis.att.Field;
import com.zondy.mapgis.base.XString;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CR
 * @file FieldFunctions.java
 * @brief Field的一些计算方法
 * @create 2019-11-25.
 */
public class FieldFunctions
{
    /**
     * 根据字段类型转换字段值，由String转为相应类型
     *
     * @param strVal  字段属性值的字符串形式
     * @param fldType 字段类型
     * @return 字段值（对应于各自的类型）
     */
    public static Object convertFieldValues(String strVal, Field.FieldType fldType)
    {
        Object objVal = strVal;
        if (XString.isNullOrEmpty(strVal))
        {
            objVal = null;
        } else
        {
            try
            {
                if (Field.FieldType.fldBool.equals(fldType))
                {
                    objVal = (strVal != "0" && !strVal.equalsIgnoreCase("false"));
                } else if (Field.FieldType.fldByte.equals(fldType))
                {
                    objVal = Byte.parseByte(strVal);
                } else if (Field.FieldType.fldDate.equals(fldType))
                {
                    objVal = LocalDate.parse(strVal);
                } else if (Field.FieldType.fldDouble.equals(fldType))
                {
                    objVal = Double.parseDouble(strVal);
                } else if (Field.FieldType.fldFloat.equals(fldType))
                {
                    objVal = Float.parseFloat(strVal);
                } else if (Field.FieldType.fldInt64.equals(fldType))
                {
                    objVal = Long.parseLong(strVal);
                } else if (Field.FieldType.fldLong.equals(fldType))
                {
                    objVal = Integer.parseInt(strVal);
                } else if (Field.FieldType.fldShort.equals(fldType))
                {
                    objVal = Short.parseShort(strVal);
                } else if (Field.FieldType.fldTime.equals(fldType))
                {
                    objVal = LocalTime.parse(strVal);
                }
            } catch (Exception ex)
            {
            }
        }
        return objVal;
    }

    /**
     * 根据字段类型获取字段的默认长度(数组中第一个值为MskLength，第二个为PointLength）
     *
     * @param fldType 字段类型
     * @return 数组。第一个值为MskLength，第二个为PointLength
     */
    public static short[] getDefaultLength(Field.FieldType fldType)
    {
        short[] lens = {0, 0};
        if (Field.FieldType.fldStr.equals(fldType))
        {
            lens[0] = 255;
        } else if (Field.FieldType.fldByte.equals(fldType))
        {
            lens[0] = 3;
        } else if (Field.FieldType.fldShort.equals(fldType))
        {
            lens[0] = 5;
        } else if (Field.FieldType.fldLong.equals(fldType))
        {
            lens[0] = 10;
        } else if (Field.FieldType.fldInt64.equals(fldType))
        {
            lens[0] = 20;
        } else if (Field.FieldType.fldFloat.equals(fldType))
        {
            lens[0] = 8;
            lens[1] = 3;
        } else if (Field.FieldType.fldDouble.equals(fldType))
        {
            lens[0] = 15;
            lens[1] = 6;
        } else if (Field.FieldType.fldBinary.equals(fldType))
        {
            lens[0] = 255;
        } else if (Field.FieldType.fldBool.equals(fldType))
        {
            lens[0] = 1;
        } else if (Field.FieldType.fldDate.equals(fldType) || Field.FieldType.fldTime.equals(fldType))
        {
            lens[0] = 10;
        } else if (Field.FieldType.fldTimeStamp.equals(fldType))
        {
            lens[0] = 21;
        } else if (Field.FieldType.fldBlob.equals(fldType))
        {
            lens[0] = 5;
        } else
        {
            lens[0] = 1;
        }
        return lens;
    }

    /**
     * 获取字段的编辑范围(第一个是最小值，第二个是最大值）
     *
     * @param fldType 字段类型
     * @param mskLen  字段的长度
     * @return 一个两值数组，第一个是最小值，第二个是最大值
     */
    public static Object[] getFieldValueRange(Field.FieldType fldType, int mskLen)
    {
        Object[] vals = {null, null};

        //region 根据类型取最大最小值
        if (Field.FieldType.fldByte.equals(fldType))
        {
            vals[0] = Byte.MIN_VALUE;
            vals[1] = Byte.MAX_VALUE;
        } else if (Field.FieldType.fldShort.equals(fldType))
        {
            vals[0] = Short.MIN_VALUE;
            vals[1] = Short.MAX_VALUE;
        } else if (Field.FieldType.fldLong.equals(fldType))
        {
            vals[0] = Integer.MIN_VALUE;
            vals[1] = Integer.MAX_VALUE;
        } else if (Field.FieldType.fldInt64.equals(fldType))
        {
            vals[0] = Long.MIN_VALUE;
            vals[1] = Long.MAX_VALUE;
        } else if (Field.FieldType.fldFloat.equals(fldType))
        {
            vals[0] = Float.MIN_VALUE;
            vals[1] = Float.MAX_VALUE;
        } else if (Field.FieldType.fldDouble.equals(fldType))
        {
            vals[0] = Double.MIN_VALUE;
            vals[1] = Double.MAX_VALUE;
        } else if (Field.FieldType.fldDate.equals(fldType))
        {
            vals[0] = LocalDate.MIN;
            vals[1] = LocalDate.MAX;
        } else if (Field.FieldType.fldTime.equals(fldType))
        {
            vals[0] = LocalTime.MIN;
            vals[1] = LocalTime.MAX;
        }

        //endregion

        //region 根据用户限定的字段长度取最大最小值
        int maxLen = FieldFunctions.getMaxLength(fldType);
        if (mskLen < maxLen)
        {
            if (Field.FieldType.fldByte.equals(fldType) || Field.FieldType.fldShort.equals(fldType) || Field.FieldType.fldLong.equals(fldType) || Field.FieldType.fldInt64.equals(fldType))
            {
                double max = 0;
                for (int i = 0; i < mskLen; i++)
                {
                    max += Math.pow(10, i) * 9;
                }
                vals[1] = max;
                vals[0] = -1 * max;
            } else if (Field.FieldType.fldFloat.equals(fldType) || Field.FieldType.fldDouble.equals(fldType))
            {
                double max = 0.0;
                for (int i = 0; i < mskLen; i++)
                {
                    max += Math.pow(10, i) * 9;
                }
                vals[1] = max;
                vals[0] = -1 * max;
            }
        }
        //endregion

        return vals;
    }

    /**
     * 获取不同字段类型的最大长度
     *
     * @param fldType 字段类型
     * @return 该类型的字段的最大长度
     */
    public static int getMaxLength(Field.FieldType fldType)
    {
        int maxLen = 0;

        //region 根据字段类型设置长度的最大值
        if (Field.FieldType.fldStr.equals(fldType))
        {
            maxLen = 512;
        } else if (Field.FieldType.fldByte.equals(fldType))
        {
            maxLen = 3;
        } else if (Field.FieldType.fldShort.equals(fldType))
        {
            maxLen = 5;
        } else if (Field.FieldType.fldLong.equals(fldType))
        {
            maxLen = 10;
        } else if (Field.FieldType.fldInt64.equals(fldType))
        {
            maxLen = 20;
        } else if (Field.FieldType.fldFloat.equals(fldType) || Field.FieldType.fldDouble.equals(fldType))
        {
            maxLen = 64;
        } else if (Field.FieldType.fldBinary.equals(fldType))
        {
            maxLen = 4096;

            //不可编辑
        } else if (Field.FieldType.fldBool.equals(fldType))
        {
            maxLen = 1;
        } else if (Field.FieldType.fldDate.equals(fldType) || Field.FieldType.fldTime.equals(fldType))
        {
            maxLen = 10;
        } else if (Field.FieldType.fldTimeStamp.equals(fldType))
        {
            maxLen = 21;
        } else if (Field.FieldType.fldBlob.equals(fldType))
        {
            maxLen = 5;
        }
        //endregion

        return maxLen;
    }

    /**
     * 字段长度是否固定
     *
     * @param fldType 字段类型
     * @return 固定返回true
     */
    public static boolean isLengthFixed(Field.FieldType fldType)
    {
        boolean isLengthFixed = false;

        if (Field.FieldType.fldBool.equals(fldType) || Field.FieldType.fldDate.equals(fldType) || Field.FieldType.fldTime.equals(fldType) || Field.FieldType.fldTimeStamp.equals(fldType) || Field.FieldType.fldBlob.equals(fldType))
        {
            isLengthFixed = true;
        }

        return isLengthFixed;
    }

    /**
     * 根据组合框形态字段的值的名称获取值
     *
     * @param fld      字段
     * @param itemName 组合框形态字段下拉项的名称
     * @return 组合框形态字段下拉项的名称对应的实际存储的值
     */
    public static Object getComboFieldValueByName(Field fld, String itemName)
    {
        Object fldVal = null;
        if (fld != null && !XString.isNullOrEmpty(itemName) && itemName != "<NULL>")
        {
            fldVal = FieldFunctions.getComboFieldValueByName(fld.getExtField(), itemName);
        }
        return fldVal;
    }

    /**
     * 根据组合框形态字段的值的名称获取值
     *
     * @param extField 字段的扩展信息
     * @param itemName 组合框形态字段下拉项的名称
     * @return 组合框形态字段下拉项的名称对应的实际存储的值
     */
    public static Object getComboFieldValueByName(Field.ExtField extField, String itemName)
    {
        Object fldVal = null;
        if (!XString.isNullOrEmpty(itemName) && itemName != "<NULL>")
        {
            fldVal = itemName;
            if (extField != null && extField.getShape() == Field.FieldShape.fldShpCombo)
            {
                for (short i = 0; i < extField.getShapeInfoNum(); i++)
                {
                    String name = "";
                    Object[] vals = extField.getShapeInfo(i);
                    if (vals != null && vals.length >= 2 && vals[0] == itemName)//未完成。需确定0和1谁是name谁是value
                    {
                        fldVal = vals[1];
                        break;
                    }
                }
            }
        }
        return fldVal;
    }

    /**
     * 根据组合框形态字段的值获取其名称
     *
     * @param fld    字段
     * @param fldVal 组合框形态字段的值
     * @return 组合框形态字段的值对应的名称
     */
    public static String getComboNameByFieldValue(Field fld, Object fldVal)
    {
        String itemName = "";
        if (fld != null && fldVal != null && !fldVal.toString().isEmpty())
        {
            itemName = FieldFunctions.getComboNameByFieldValue(fld.getExtField(), fldVal);
        }
        return itemName;
    }

    /**
     * 根据组合框形态字段的值获取其名称
     *
     * @param extField 字段的扩展信息
     * @param fldVal   组合框形态字段实际的属性值（一般从记录上读来）
     * @return 组合框形态字段下拉项中的显示名称
     */
    public static String getComboNameByFieldValue(Field.ExtField extField, Object fldVal)
    {
        String itemName = "";
        if (fldVal != null && !fldVal.toString().isEmpty())
        {
            itemName = fldVal.toString();
            if (extField != null && extField.getShape() == Field.FieldShape.fldShpCombo)
            {
                for (short i = 0; i < extField.getShapeInfoNum(); i++)
                {
                    Object[] vals = extField.getShapeInfo(i);
                    Object val = (vals != null && vals.length >= 2) ? vals[1] : null;//未完成。需确定1是不是value，不是取0
                    if (val != null)
                    {
                        String strFldVal = fldVal.toString();
                        String strVal = val.toString();
                        if (extField.getFieldType() == Field.FieldType.fldDate)
                        {
                            strFldVal = fldVal.toString();
                            strVal = val.toString();
                        } else if (extField.getFieldType() == Field.FieldType.fldTime)
                        {
                            strFldVal = fldVal.toString();
                            strVal = val.toString();
                        }

                        if (strFldVal == strVal)
                        {
                            itemName = (String) vals[0];//未完成。需确定0是不是name，不是取1
                            break;
                        }
                    }
                }
            }
        }
        return itemName;
    }

    /**
     * 获取组合框形态字段的选项名称
     *
     * @param fld 字段
     * @return 组合框形态字段的下拉项集合
     */
    public static List<String> getFieldComboItems(Field fld)
    {
        List<String> itemList = null;
        if (fld != null)
        {
            itemList = FieldFunctions.getFieldComboItems(fld.getExtField());
        }
        return itemList;
    }

    /**
     * 获取组合框形态字段的选项名称
     *
     * @param extField 字段的扩展信息
     * @return 组合框形态字段的下拉项集合
     */
    public static List<String> getFieldComboItems(Field.ExtField extField)
    {
        List<String> itemList = null;
        if (extField != null && extField.getShape() == Field.FieldShape.fldShpCombo)
        {
            itemList = new ArrayList<>();
            if (extField.getIsNull())
            {
                itemList.add("<NULL>");
            }

            for (short i = 0; i < extField.getShapeInfoNum(); i++)
            {
                Object[] vals = extField.getShapeInfo(i);
                if (vals != null && vals.length > 1)
                {
                    itemList.add(String.valueOf(vals[0]));//未完成。需确定0是不是name，如果不是取1
                }
            }
        }
        return itemList;
    }

    /**
     * 判断AttributeStatistic属性统计类是否支持该字段类型
     *
     * @param type
     * @return
     */
    public static boolean canAttStatistic(Field.FieldType type)
    {
        boolean canAttstatistic = false;
        if (Field.FieldType.fldStr.equals(type) || Field.FieldType.fldByte.equals(type) || Field.FieldType.fldBool.equals(type) || Field.FieldType.fldShort.equals(type) || Field.FieldType.fldLong.equals(type) || Field.FieldType.fldInt64.equals(type) || Field.FieldType.fldFloat.equals(type) || Field.FieldType.fldDouble.equals(type) || Field.FieldType.fldDate.equals(type) || Field.FieldType.fldTime.equals(type) || Field.FieldType.fldTimeStamp.equals(type))
        {
            canAttstatistic = true;
        }
        return canAttstatistic;
    }

    /**
     * 根据setFields的结果给出失败的提示信息
     *
     * @param rtnFieldsSetting setFields的返回值
     * @return 提示信息
     */
    public static String getFieldsSettingError(long rtnFieldsSetting)
    {
        String errorMsg = "";
        if (rtnFieldsSetting <= 0)
        {
            if (rtnFieldsSetting == -1501)
            {
                errorMsg = "字段不能被删除。该字段可能是字段可能是:1）继承字段；2）外关键字；3）子类型字段。";//不能被删除：字段可能是①.继承字段；②.外关键字；③.子类型字段。
            } else if (rtnFieldsSetting == -1502)//界面已禁用编辑
            {
                errorMsg = "字段不能被更新。该字段可能是字段可能是: 1）继承字段；2）外关键字。";//不能更新：字段可能是①.继承字段；②.外关键字。
            } else if (rtnFieldsSetting == -1503)
            {
                errorMsg = "不能添加字段。该字段可能是字段可能是: 1）系统保留字段；2）继承字段。";//不能添加字段：字段可能是①.系统保留字段；②.继承字段。
            } else if (rtnFieldsSetting == -1504)//界面已禁用编辑
            {
                errorMsg = "不能更新字段名。该字段可能: 1）设置了域(属性规则)；2）是子类型字段。";//不能更新字段名：字段可能①.设置了域(属性规则)；②.子类型字段。
            } else if (rtnFieldsSetting == -1505)//界面已禁用编辑
            {
                errorMsg = "不能更新字段类型。该字段可能:1）设置了域(属性规则)；2）是子类型字段。";//不能更新字段类型：字段可能是①.设置了域(属性规则)；②.子类型字段。
            } else if (rtnFieldsSetting == -1506)
            {
                errorMsg = "字段必须支持NULL，原记录集中该字段存在NULL值。";
            } else if (rtnFieldsSetting == -14501)
            {
                errorMsg = "该数据被打开多次，为保证数据安全，不允许修改属性结构。";
            } else
            {
                errorMsg = "属性结构设置失败。";
            }
        }
        return errorMsg;
    }
}
