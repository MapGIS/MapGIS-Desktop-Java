package com.zondy.mapgis.pluginengine.ui;

import com.zondy.mapgis.pluginengine.plugin.IPlugin;

/**
 * Ribbon页面组插件
 *
 * @author cxy
 * @date 2019/09/11
 */
public interface IRibbonPageGroup extends IPlugin {
    /**
     * 获取页面组中的插件功能项的集合
     *
     * @return 功能项的集合
     */
    IItem[] getIItems();

    /**
     * 获取页面组的标题
     *
     * @return 标题
     */
    String getText();
}
