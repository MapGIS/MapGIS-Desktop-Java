package com.zondy.mapgis.mapeditor.projecttransform;

import com.zondy.mapgis.geometry.GeomType;
import com.zondy.mapgis.srs.SRefData;

/**
 * 投影数据项参数模型
 */
public class ProjectDataItem {
    private String srcLUrl = "";
    private String srcName = "";
    private String desDir = "";
    private String desName = "";
    private ProjectDataType dataType = ProjectDataType.Sfcls;
    private SRefData srcRefData = null;
    private boolean isGDBData = false;  //根据是否是GDB数据确保目的数据目录一致
    private GeomType geomType = GeomType.GeomUnknown; //矢量数据的几何类型，区分6x后缀
    private String curErrorMsg = ""; //当前转换项的参数错误信息
    private String state = null; //是否检测出参数错误，显示错误状态提示图标

    public ProjectDataItem() {

    }

    public String getSrcLUrl() {
        return this.srcLUrl;
    }

    public void setSrcLUrl(String val) {
        this.srcLUrl = val;
        if(val != null)
        {
            if(val.toUpperCase().startsWith("GDBP://"))
                this.isGDBData = true;
            else
                this.isGDBData = false;
        }
    }

    public String getSrcName() {
        return this.srcName;
    }

    public void setSrcName(String val) {
        this.srcName = val;
    }

    public String getDesDir() {
        return this.desDir;
    }

    public void setDesDir(String val) {
        this.desDir = val;
    }

    public String getDesName() {
        return this.desName;
    }

    public void setDesName(String val) {
        this.desName = val;
    }

    public ProjectDataType getDataType() {
        return this.dataType;
    }

    public void setDataType(ProjectDataType val) {
        this.dataType = val;
    }

    public SRefData getSrcRefData() {
        return this.srcRefData;
    }

    public void setSrcRefData(SRefData val) {
        this.srcRefData = val;
    }

    public boolean isGDBData()
    {
        return this.isGDBData;
    }
    public void setGDBData(boolean val)
    {
        this.isGDBData = val;
    }

    public GeomType getGeomType()
    {
        return this.geomType;
    }
    public void setGeomType(GeomType val)
    {
        this.geomType = val;
    }

    public String getCurErrorMsg() {
        return this.curErrorMsg;
    }

    public void setCurErrorMsg(String val) {
        this.curErrorMsg = val;
    }

    public String getState()
    {
        return this.state;
    }
    public void setState(String val)
    {
        this.state = val;
    }

}
