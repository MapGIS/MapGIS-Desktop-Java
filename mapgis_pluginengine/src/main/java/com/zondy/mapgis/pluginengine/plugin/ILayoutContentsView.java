package com.zondy.mapgis.pluginengine.plugin;

/**
 * 版面视图插件
 *
 * @author cxy
 * @date 2019/09/11
 */
public interface ILayoutContentsView extends IContentsView {
    // TODO: 2019/09/11 待 MapGIS.GISControl.LayoutControl 导入后将返回类型修改
    /**
     * 获取版面视图控件
     *
     * @return 版面视图控件
     */
    Object getLayoutControl();
}
