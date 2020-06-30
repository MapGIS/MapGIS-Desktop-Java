package com.zondy.mapgis.pluginengine.plugin;

import com.zondy.mapgis.controls.SceneControl;

/**
 * 场景视图插件
 *
 * @author cxy
 * @date 2019/09/11
 */
public interface ISceneContentsView extends IContentsView {
    /**
     * 获取内容视图中的场景控制控件
     *
     * @return 场景控制控件
     */
    SceneControl getSceneControl();
}
