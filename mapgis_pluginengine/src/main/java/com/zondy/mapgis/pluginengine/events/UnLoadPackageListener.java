package com.zondy.mapgis.pluginengine.events;

/**
 * 卸载包事件监听器
 *
 * @author cxy
 * @date 2019/09/16
 */
public interface UnLoadPackageListener {
    /**
     * 触发卸载包事件
     *
     * @param unLoadPackageEvent 卸载包事件
     */
    void unLoadPackage(UnLoadPackageEvent unLoadPackageEvent);
}
