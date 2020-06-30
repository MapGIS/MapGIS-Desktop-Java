package com.zondy.mapgis.pluginengine.plugin;

import com.zondy.mapgis.pluginengine.IApplication;

/**
 * 连接插件程序集接口
 *
 * @author cxy
 * @date 2019/09/10
 */
public interface IConnect {
    /**
     * 当框架准备加载此 jar 时，首先调用此方法
     *
     * @param app 框架的宿主对象
     */
    void onConnection(IApplication app);

    /**
     * 当框架卸载此 jar 后，调用此方法
     */
    void onDisconnection();
}
