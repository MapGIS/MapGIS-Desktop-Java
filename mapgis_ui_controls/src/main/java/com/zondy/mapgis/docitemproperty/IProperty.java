package com.zondy.mapgis.docitemproperty;

import com.zondy.mapgis.map.DocumentItem;
import org.controlsfx.control.PropertySheet;

/**
 * 图层属性类接口
 */
public interface IProperty {
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
    public DocumentItem getDocItem();

    /**
     * 设置地图文档项
     * @param item 地图文档项
     */
    public void setDocItem(DocumentItem item);

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
     * @param isUpdate
     */
    public void setImmediatelyUpdate(boolean isUpdate);

    /**
     * 获取是否立即更新
     * @return
     */
    public boolean isImmediatelyUpdate();

}
