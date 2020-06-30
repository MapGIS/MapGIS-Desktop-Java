package com.zondy.mapgis.pluginengine.enums;

/**
 * 指定一个控件的状态，例如复选框可以是选中、未选中或设置为不确定状态。
 *
 * @author cxy
 * @date 2019/09/10
 */
public enum CheckStateEnum {
    /**
     * 该控件处于未选中状态
     */
    UNCHECKED,

    /**
     * 该控件处于选中状态
     */
    CHECKED,

    /**
     * 该控件处于不确定状态。一个不确定的控件通常具有灰色的外观。
     */
    INDETERMINATE
}
