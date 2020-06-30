package com.zondy.mapgis.pluginengine.plugin;

import com.zondy.mapgis.pluginengine.IApplication;

/**
 * 组合框插件
 *
 * @author cxy
 * @date 2019/09/10
 */
public interface IEditCombobox extends IPlugin {
    /**
     * 组合框的标题
     *
     * @return 标题
     */
    String getCaption();

    /**
     * 组合框所属的类别
     *
     * @return 类别
     */
    String getCategory();

    /**
     * 组合框下拉时显示的最多的项数
     *
     * @return 项数
     */
    int getDropDownRows();

    /**
     * 组合框是否可编辑输入
     *
     * @return true/false
     */
    boolean isEditable();

    /**
     * 组合框是否可用
     *
     * @return true/false
     */
    boolean isEnabled();

    /**
     * 组合框是否为Combobox。为true，则为Combobox，否则为TextBox。
     *
     * @return true/false
     */
    boolean isDropDown();

    /**
     * 组合框的下拉项的集合（IsDropDown属性设为true时，此属性才有意义）
     *
     * @return 下拉项的集合
     */
    ComboBoxItem[] getItems();

    /**
     * 鼠标移到组合框上时状态栏上出现的文本
     *
     * @return 文本
     */
    String getMessage();

    /**
     * 鼠标停留在组合框上时弹出的提示文本
     *
     * @return 提示文本
     */
    String getTooltip();

    /**
     * 组合框的初始显示值
     *
     * @return 初始显示值
     */
    Object getValue();

    /**
     * 组合框界面对象的宽度
     *
     * @return 宽度
     */
    int getWidth();

    /**
     * 组合框的EditValue修改时引发的事件
     *
     * @param value Object
     */
    void editValueChanged(Object value);

    /**
     * 组合框被创建时引发的事件
     *
     * @param app 框架的宿主对象
     */
    void onCreate(IApplication app);
}
