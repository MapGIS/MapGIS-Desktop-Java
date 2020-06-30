package com.zondy.mapgis.pluginengine.ui;

/**
 * Bar接口
 *
 * @author cxy
 * @date 2019/09/11
 */
public interface IBar {
    /**
     * 获取菜单栏、工具条或者状态栏的绑定的视图的Key，其格式为“[命名空间].[视图的类名]”
     *
     * @return 视图的Key
     */
    String getAttachContentsViewKey();

    /**
     * 获取菜单栏、工具条或者状态栏的标题
     *
     * @return 标题
     */
    String getCaption();

    /**
     * 获取菜单栏、工具条或者状态栏上的插件功能项的集合
     *
     * @return 插件功能项的集合
     */
    IItem[] getIItems();
}
