package com.zondy.mapgis.pluginengine.events;

/**
 * 加载包事件监听器
 *
 * @author cxy
 * @date 2019/09/16
 */
public interface LoadPackageListener {
    /**
     * 触发加载包事件
     *
     * @param loadPackageEvent 加载包事件
     */
    void loadPackage(LoadPackageEvent loadPackageEvent);
}
