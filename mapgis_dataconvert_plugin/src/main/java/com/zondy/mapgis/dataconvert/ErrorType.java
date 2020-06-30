package com.zondy.mapgis.dataconvert;

/**
 * 转换数据错误类型
 *
 * @author CR
 * @file ItemErrorType.java
 * @brief 转换数据错误类型
 * @create 2020-03-10.
 */
public enum ErrorType
{
    UNCHECK(null),//未检查
    NOERROR(""),//无措
    UNKNOWN("未知错误"),
    DESTNAMENULL("目的数据名称为空"),
    DESTNAMEFIRSTCHARIINVALID("目的数据首字符是非法字符"),//(表格数据)
    DESTNAMECHARINVALID("目的数据名称含有非法字符"),
    DESTNAMETOOLONG("目的数据名过长"),
    DESTDIRNULL("目的数据目为空"),
    DESTDIRNOTEXIST("目的数据目录不存在"),
    DESTDIRCHARINVALID("目的数据目录含有非法字符"),
    DESTDIRINVALID("目的数据目录不合法"),
    DESTDATAEXIST("目的数据已存在"),
    DESTDATAHASNAMED("目的数据已被其他转换项占用"),
    DESTDATAGEOMTYPNOTEQUAL("追加模式下，已存在的目的数据的几何类型与源数据不一致"),
    DESTTYPEERROR("目的类型错误"),
    CONVERTERROR("转换过程中出错"),
    DESTGDBNOTEXIST("目的GDB不存在"),
    NOSETTXT27XPARAM("未设置导入TXT参数");

    private String value;

    ErrorType(String val)
    {
        this.value = val;
    }

    public String getValue()
    {
        return this.value;
    }
}
