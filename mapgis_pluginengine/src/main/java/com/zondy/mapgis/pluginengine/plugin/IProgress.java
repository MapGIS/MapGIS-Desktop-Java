package com.zondy.mapgis.pluginengine.plugin;

import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.enums.ProgressAlignmentEnum;

/**
 * 进度条插件
 * 此插件仅能在状态栏中使用
 *
 * @author cxy
 * @date 2019/09/11
 */
public interface IProgress extends IPlugin {
    /**
     * 获取进度条的水平对齐方式
     *
     * @return 进度条的水平对齐方式
     */
    ProgressAlignmentEnum getAlignment();

    /**
     * 获取进度条的标题
     *
     * @return 标题
     */
    String getCaption();

    /**
     * 获取进度条所属的类别
     *
     * @return 类别
     */
    String getCategory();

    /**
     * 获取进度条界面对象的高度
     *
     * @return 高度
     */
    int getHeight();

    /**
     * 获取进度条的最大进度值
     *
     * @return 最大进度值
     */
    int getMaximum();

    /**
     * 获取进度条的最小进度值
     *
     * @return 最小进度值
     */
    int getMinimum();

    /**
     * 获取进度条移动的步值
     *
     * @return 步值
     */
    int getStep();

    /**
     * 获取进度条界面对象的宽度
     *
     * @return 宽度
     */
    int getWidth();

    /**
     * 进度条被创建时引发的事件
     *
     * @param app 框架的宿主对象
     */
    void onCreate(IApplication app);
}
