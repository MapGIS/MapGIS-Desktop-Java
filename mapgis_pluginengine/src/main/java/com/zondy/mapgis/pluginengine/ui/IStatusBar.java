package com.zondy.mapgis.pluginengine.ui;

import com.zondy.mapgis.pluginengine.plugin.IPlugin;

/**
 * 状态栏插件
 *
 * @author cxy
 * @date 2019/09/11
 */
public interface IStatusBar extends IBar, IPlugin {
    /**
     * 获取状态栏是否是默认状态栏。在当前激活视图没有被绑定到指定状态栏时，显示默认状态栏。
     *
     * @return true/false
     */
    boolean isDefaultStatusBar();
}
