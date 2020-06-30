package com.zondy.mapgis.filedialog;

/**
 * 数据列表项信息
 */
public class DataInfo {
    public DataInfo() {

    }

    private String name;
    private String type;
    private String ctime;
    private String mtime;
    private String url;
    private String image;

    public String getImage()
    {
        return this.image;
    }
    public void setImage(String img)
    {
        this.image = img;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String val) {
        this.name = val;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String val) {
        this.type = val;
    }

    public String getCtime() {
        return this.ctime;
    }

    public void setCtime(String val) {
        this.ctime = val;
    }

    public String getMtime() {
        return this.mtime;
    }

    public void setMtime(String val) {
        this.mtime = val;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String val) {
        this.url = val;
    }
}
