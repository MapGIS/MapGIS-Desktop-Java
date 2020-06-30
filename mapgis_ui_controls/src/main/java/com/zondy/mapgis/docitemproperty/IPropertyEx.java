package com.zondy.mapgis.docitemproperty;

import org.controlsfx.control.PropertySheet;

/**
 * 扩展属性类接口
 */
public interface IPropertyEx {
    /**
     * 获取该属性页面是否发生了改变，便于在刷新页面时提示和保存
     * @return
     */
    public boolean hasChanged();

    /**
     * 应用（保存）当前属性
     */
    public void apply();

    /**
     * 获取地图文档项
     * @return
     */
    public Object getItem();

    /**
     * 设置地图文档项
     * @param obj 地图文档项
     */
    public void setItem(Object obj);

    /**
     * 获取属性子项集合
     * @return
     */
    public PropertyItem[] getPropertyItems();

    /**
     * 获取对应PropertySheet控件
     * @return
     */
    public PropertySheet getPropertySheet();
    /**
     * 设置是否立即更新
     * @return
     */
    public void setImmediatelyUpdate(boolean isUpdate);

    /**
     * 获取是否立即更新
     * @return
     */
    public boolean isImmediatelyUpdate();


    /**
     * 获取字符串表达
     * @return
     */
    public String toString();
}

