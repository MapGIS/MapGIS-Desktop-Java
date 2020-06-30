package com.zondy.mapgis.mapeditor.plugin.ribbon;

import com.zondy.mapgis.pluginengine.ui.IRibbonPage;
import com.zondy.mapgis.pluginengine.ui.IRibbonPageGroup;

public class RibbonPageStart implements IRibbonPage {
    private IRibbonPageGroup[] ribbonPageGroups;

    public RibbonPageStart() {
        ribbonPageGroups = new IRibbonPageGroup[]{
                new RibbonPageGroupSpatialAnalysis()
        };
    }

    /**
     * 获取页面所属的页面类别的Key，其格式为“[命名空间].[页面类别的类名]”
     *
     * @return 页面类别的Key
     */
    @Override
    public String getCategoryKey() {
        return null;
    }

    /**
     * 获取页面的标题
     *
     * @return 标题
     */
    @Override
    public String getText() {
        return "分析";
    }

    /**
     * 获取初始是否选中
     *
     * @return true/false
     */
    @Override
    public boolean isSelected() {
        return false;
    }

    /**
     * 获取 Ribbon 页面组集合
     *
     * @return Ribbon 页面组集合
     */
    @Override
    public IRibbonPageGroup[] getRibbonPageGroups() {
        return ribbonPageGroups;
    }
}
