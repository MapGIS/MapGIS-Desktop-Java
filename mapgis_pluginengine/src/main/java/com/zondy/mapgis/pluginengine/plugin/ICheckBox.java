package com.zondy.mapgis.pluginengine.plugin;

import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.enums.CheckStateEnum;

/**
 * 复选框插件
 *
 * @author cxy
 * @date 2019/09/10
 */
public interface ICheckBox extends IPlugin {
    /**
     * 复选框是否支持三种选择状态
     *
     * @return true/false
     */
    boolean isAllowThreeState();

    /**
     * 复选框的标题
     *
     * @return 标题
     */
    String getCaption();

    /**
     * 复选框所属的类别
     *
     * @return 类别
     */
    String getCategory();

    /**
     * 复选框是否可用
     *
     * @return true/false
     */
    boolean isEnabled();

    /**
     * 复选框的初始选择状态
     *
     * @return 选择状态
     */
    CheckStateEnum getInitState();

    /**
     * 鼠标移到复选框上时状态栏上显示的文本
     *
     * @return 显示的文本
     */
    String getMessage();

    /**
     * 鼠标停留在复选框上时弹出的提示文本
     *
     * @return 提示文本
     */
    String getTooltip();

    /**
     * 复选框界面对象的宽度，此属性仅在Ribbon风格中支持
     *
     * @return 宽度
     */
    int getWidth();

    /**
     * 复选框的选择状态改变时引发的事件
     *
     * @param newCheckState 改变后的选择状态
     * @param oldCheckState 改变前的选择状态
     */
    void onCheckStateChanged(CheckStateEnum newCheckState, CheckStateEnum oldCheckState);

    /**
     * 复选框被创建时引发的事件
     *
     * @param app 框架的宿主对象
     */
    void onCreate(IApplication app);
}
