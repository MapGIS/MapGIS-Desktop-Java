package com.zondy.mapgis.pluginengine.plugin;

import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.enums.StaticAlignmentEnum;
import com.zondy.mapgis.pluginengine.enums.StaticItemSizeEnum;

/**
 * 静态文本框插件
 * 此插件仅能在状态栏中使用
 *
 * @author cxy
 * @date 2019/09/11
 */
public interface IStatic extends IPlugin {
    /**
     * 获取静态框的水平对齐方式
     *
     * @return 水平对齐方式
     */
    StaticAlignmentEnum getAlignment();

    /**
     * 获取静态框的尺寸控制方式
     *
     * @return 尺寸控制方式
     */
    StaticItemSizeEnum getAutoSize();

    /**
     * 获取静态框的标题
     *
     * @return 标题
     */
    String getCaption();

    /**
     * 获取静态框所属的类别
     *
     * @return 类别
     */
    String getCategory();

    /**
     * 获取静态框的界面对象的宽度（只有在AutoSize设为None时，此属性才有意义）
     *
     * @return 宽度
     */
    int getWidth();

    /**
     * 双击静态框引发的事件
     */
    void onDoubleClick();

    /**
     * 静态框被创建时引发的事件
     *
     * @param app 框架的宿主对象
     */
    void onCreate(IApplication app);
}
