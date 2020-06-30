package com.zondy.mapgis.mapeditor.projecttransform;

/**
 * 转换参数信息
 */
public class TransParamInfo {
    private String paramName; //参数名
    private String paramValue;//参数值

    public TransParamInfo(String name, String value) {
        paramName = name;
        paramValue = value;
    }

    public TransParamInfo() {

    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String val) {
        this.paramName = val;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String val) {
        this.paramValue = val;
    }
}
