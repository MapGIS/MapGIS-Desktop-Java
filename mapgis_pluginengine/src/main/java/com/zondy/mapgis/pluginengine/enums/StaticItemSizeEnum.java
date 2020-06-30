package com.zondy.mapgis.pluginengine.enums;

/**
 * 静态框的尺寸控制方式
 *
 * @author cxy
 * @date 2019/09/11
 */
public enum StaticItemSizeEnum {
    /**
     * 无,根据静态文本框插件Width属性控制
     */
    NONE,

    /**
     * 内容长度控制
     */
    CONTENT,

    /**
     * 弹性控制
     */
    SPRING
}
