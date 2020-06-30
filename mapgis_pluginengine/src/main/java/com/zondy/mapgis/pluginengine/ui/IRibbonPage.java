package com.zondy.mapgis.pluginengine.ui;

import com.zondy.mapgis.pluginengine.plugin.IPlugin;

/**
 * Ribbon页面插件
 *
 * @author cxy
 * @date 2019/09/11
 */
public interface IRibbonPage extends IPlugin {
    /**
     * 获取页面所属的页面类别的Key，其格式为“[命名空间].[页面类别的类名]”
     *
     * @return 页面类别的Key
     */
    String getCategoryKey();

    /**
     * 获取页面的标题
     *
     * @return 标题
     */
    String getText();

    /**
     * 获取初始是否选中
     *
     * @return true/false
     */
    boolean isSelected();

    /**
     * 获取 Ribbon 页面组集合
     *
     * @return Ribbon 页面组集合
     */
    IRibbonPageGroup[] getRibbonPageGroups();
}
