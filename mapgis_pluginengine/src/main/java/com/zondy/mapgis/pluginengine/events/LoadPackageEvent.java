package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.pluginengine.plugin.IConnect;

import java.util.EventObject;

/**
 * 加载包事件
 *
 * @author cxy
 * @date 2019/09/16
 */
public class LoadPackageEvent extends EventObject {
    private transient String packagePath;
    private transient IConnect[] connects;

    /**
     * 加载包事件
     *
     * @param source      事件源
     * @param packagePath 包路径
     * @param connects 连接插件程序集接口
     * @throws IllegalArgumentException if source is null.
     */
    public LoadPackageEvent(Object source, String packagePath, IConnect[] connects) {
        super(source);
        this.packagePath = packagePath;
        this.connects = connects;
    }

    /**
     * 获取包路径
     *
     * @return 包路径
     */
    public String getPackagePath() {
        return packagePath;
    }

    /**
     * 获取连接插件程序集接口
     *
     * @return 连接插件程序集接口
     */
    public IConnect[] getConnects() {
        return connects;
    }
}
