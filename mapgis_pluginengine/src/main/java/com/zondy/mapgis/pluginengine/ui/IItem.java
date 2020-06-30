package com.zondy.mapgis.pluginengine.ui;

/**
 * 界面元素项
 *
 * @author cxy
 * @date 2019/09/11
 */
public interface IItem {
    /**
     * 获取此项的功能Key，一般是某个插件项的ID，格式为[插件项的命名空间].[插件项的类名]
     *
     * @return 功能Key
     */
    String getKey();

    /**
     * 设置此项的功能Key，一般是某个插件项的ID，格式为[插件项的命名空间].[插件项的类名]
     *
     * @param key 功能Key
     */
    void setKey(String key);

    /**
     * 获取是否从此项开始一个新的组
     *
     * @return true/false
     */
    boolean isGroup();

    /**
     * 设置是否从此项开始一个新的组
     *
     * @param isGroup true/false
     */
    void setGroup(boolean isGroup);

    /**
     * 获取是否显示大图标
     *
     * @return true/false
     */
    boolean isShowLargeImage();

    /**
     * 设置是否显示大图标
     *
     * @param isShowLargeImage true/false
     */
    void setShowLargeImage(boolean isShowLargeImage);
}
