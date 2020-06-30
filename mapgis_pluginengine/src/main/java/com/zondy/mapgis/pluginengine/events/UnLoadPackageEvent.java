package com.zondy.mapgis.pluginengine.events;

import java.util.EventObject;

/**
 * 卸载包事件
 *
 * @author cxy
 * @date 2019/09/16
 */
public class UnLoadPackageEvent extends EventObject {
    private transient String packagePath;

    /**
     * 卸载包事件
     *
     * @param source      事件源
     * @param packagePath 包路径
     * @throws IllegalArgumentException if source is null.
     */
    public UnLoadPackageEvent(Object source, String packagePath) {
        super(source);
        this.packagePath = packagePath;
    }

    /**
     * 获取包路径
     *
     * @return 包路径
     */
    public String getPackagePath() {
        return packagePath;
    }
}
