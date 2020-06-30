package com.zondy.mapgis.gdbmanager.gdbcatalog;

import com.zondy.mapgis.base.XString;
import javafx.beans.property.*;

/**
 * @author CR
 * @file TreeItemObject.java
 * @brief Catalog树节点的对象类
 * @create 2019-12-02.
 */
public class TreeItemObject
{
    private StringProperty text = new SimpleStringProperty();
    private ObjectProperty tag = new SimpleObjectProperty();
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty url = new SimpleStringProperty();
    private String sortType = "";//类的排序，用于新增数据时计算插入位置

    /**
     * GDBCatalog树节点的数据对象
     *
     * @param text 节点文本
     */
    public TreeItemObject(String text)
    {
        this(text, null);
    }

    /**
     * GDBCatalog树节点的数据对象
     *
     * @param text 节点文本
     * @param tag  节点挂载的数据
     */
    public TreeItemObject(String text, Object tag)
    {
        this(text, tag, 0);
    }

    /**
     * GDBCatalog树节点的数据对象
     *
     * @param text 节点文本
     * @param tag  节点挂载的数据
     * @param id   节点对应数据的ID，占位节点上挂数据集的ID
     */
    public TreeItemObject(String text, Object tag, int id)
    {
        this(text, tag, id, null);
    }

    /**
     * GDBCatalog树节点的数据对象
     *
     * @param text 节点文本
     * @param tag  节点挂载的数据
     * @param id   节点对应数据的ID，占位节点上挂数据集的ID
     * @param url  节点类数据的URL
     */
    public TreeItemObject(String text, Object tag, int id, String url)
    {
        this(text, tag, id, url, "");
    }

    /**
     * GDBCatalog树节点的数据对象
     *
     * @param text 节点文本
     * @param tag  节点挂载的数据
     * @param id   节点对应数据的ID，占位节点上挂数据集的ID
     * @param url  节点类数据的URL
     */
    public TreeItemObject(String text, Object tag, int id, String url, String sortType)
    {
        this.text.set(text);
        this.tag.set(tag);
        this.id.set(id);
        this.url.set(url);
        this.sortType = sortType;
    }

    /**
     * 获取节点文本
     *
     * @return 节点文本
     */
    public String getText()
    {
        return this.text.get();
    }

    public StringProperty textProperty()
    {
        return text;
    }

    /**
     * 设置节点文本
     *
     * @param text 节点文本
     */
    public void setText(String text)
    {
        this.text.set(text);

        //重命名后修改url
        String url = this.getUrl();
        if (!XString.isNullOrEmpty(url))
        {
            int index = url.lastIndexOf('/');
            if (index > 0)
            {
                url = url.substring(0, index + 1) + text;
                this.setUrl(url);
            }
        }
    }

    /**
     * 获取节点挂载的数据对象
     *
     * @return 节点挂载的数据对象
     */
    public Object getTag()
    {
        return this.tag.get();
    }

    public ObjectProperty tagProperty()
    {
        return tag;
    }

    /**
     * 设置节点挂载的数据对象
     *
     * @param tag 节点挂载的数据对象
     */
    public void setTag(Object tag)
    {
        this.tag.set(tag);
    }

    public int getId()
    {
        return id.get();
    }

    public IntegerProperty idProperty()
    {
        return id;
    }

    /**
     * 获取节点类数据的URL
     *
     * @return 节点类数据的URL
     */
    public String getUrl()
    {
        return this.url.get();
    }

    public StringProperty urlProperty()
    {
        return url;
    }

    /**
     * 设置节点类数据的URL
     *
     * @param url 节点类数据的URL
     */
    public void setUrl(String url)
    {
        this.url.set(url);
    }

    public String getSortType()
    {
        return this.sortType;
    }

    public void setSortType(String sortType)
    {
        this.sortType = sortType;
    }
}