package com.zondy.mapgis.pluginengine.plugin;

import javafx.scene.image.Image;

/**
 * 组合框插件项
 *
 * @author cxy
 * @date 2019/09/10
 */
public class ComboBoxItem {
    private String description;
    private Object value;
    // TODO: 2019/09/10 BitMap 暂用 Image 替代
    private Image image;

    /**
     * IEditCombobox的项(仅当IEditCombobox的IsDropDown为true,才使用此类)
     *
     * @param value 项值
     */
    public ComboBoxItem(Object value) {
        this.value = value;
    }

    /**
     * IEditCombobox的项(仅当IEditCombobox的IsDropDown为true,才使用此类)
     *
     * @param description 项标题(仅当IEditCombobox的Editable为false才有意义)
     * @param value       项值
     */
    public ComboBoxItem(String description, Object value) {
        this.description = description;
        this.value = value;
    }

    /**
     * IEditCombobox的项(仅当IEditCombobox的IsDropDown为true,才使用此类)
     *
     * @param description 项标题(仅当IEditCombobox的Editable为false才有意义)
     * @param value       项值
     * @param image       项图标(仅当IEditCombobox的Editable为false才有意义)，暂不支持
     */
    public ComboBoxItem(String description, Object value, Image image) {
        this.description = description;
        this.value = value;
        this.image = image;
    }

    /**
     * 获取项描述(仅当IEditCombobox的Editable为false才有意义)
     *
     * @return 项描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置项描述(仅当IEditCombobox的Editable为false才有意义)
     *
     * @param description 项描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取项值
     *
     * @return 项值
     */
    public Object getValue() {
        return value;
    }

    /**
     * 设置项值
     *
     * @param value 项值
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * 获取图标(仅当IEditCombobox的Editable为false才有意义)，此属性暂不支持
     *
     * @return 图标
     */
    public Image getImage() {
        return image;
    }

    /**
     * 设置图标(仅当IEditCombobox的Editable为false才有意义)，此属性暂不支持
     *
     * @param image 图标
     */
    public void setImage(Image image) {
        this.image = image;
    }
}
