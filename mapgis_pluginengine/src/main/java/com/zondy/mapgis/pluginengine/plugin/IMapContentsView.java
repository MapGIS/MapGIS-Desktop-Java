package com.zondy.mapgis.pluginengine.plugin;

import com.zondy.mapgis.controls.MapControl;

/**
 * 地图视图插件
 *
 * @author cxy
 * @date 2019/09/11
 */
public interface IMapContentsView extends IContentsView {
    /**
     * 获取内容视图中的地图控制控件
     *
     * @return 地图控制控件
     */
    MapControl getMapControl();
}
