package com.zondy.mapgis.pluginengine.plugin;

/**
 * 功能插件接口
 *
 * @author cxy
 * @date 2019/09/10
 */
public interface IFunction {
    /**
     * 功能调用
     *
     * @param obj 调用对象
     * @param arg 参数
     * @return Object
     */
    Object invoke(Object obj, Object arg);
}
