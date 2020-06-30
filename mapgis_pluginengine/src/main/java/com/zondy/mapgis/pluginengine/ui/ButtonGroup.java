package com.zondy.mapgis.pluginengine.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 按钮组界面元素
 * 此界面元素仅能在Ribbon框架中使用
 *
 * @author cxy
 * @date 2019/09/11
 */
public class ButtonGroup implements IItem {
    private String caption = "";
    private String key = "";
    private boolean group = false;
    private boolean showLargeImage = false;
    private ObservableList<IItem> items = FXCollections.observableArrayList();

    public ButtonGroup() {
    }

    public ButtonGroup(IItem... items) {
        this.items.addAll(items);
    }

    public ButtonGroup(boolean newGroup, IItem... items) {
        group = newGroup;
        this.items.addAll(items);
    }

    /**
     * 获取 ButtonGroup 的标题
     *
     * @return ButtonGroup 的标题
     */
    public String getCaption() {
        return caption;
    }

    /**
     * 设置 ButtonGroup 的标题
     *
     * @param caption ButtonGroup 的标题
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * 获取 ButtonGroup 的项
     *
     * @return ButtonGroup 的项
     */
    public ObservableList<IItem> getItems() {
        return items;
    }

    /**
     * 获取 Key
     *
     * @return Key
     */
    @Override
    public String getKey() {
        return key;
    }

    /**
     * 设置 Key
     *
     * @param key 功能Key
     */
    @Override
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 获取是否为新组
     *
     * @return true/false
     */
    @Override
    public boolean isGroup() {
        return group;
    }

    /**
     * 设置是否为新组
     *
     * @param isGroup true/false
     */
    @Override
    public void setGroup(boolean isGroup) {
        this.group = isGroup;
    }

    /**
     * 此处无意义
     *
     * @return true/false
     */
    @Override
    public boolean isShowLargeImage() {
        return showLargeImage;
    }

    /**
     * 此处无意义
     *
     * @param isShowLargeImage true/false
     */
    @Override
    public void setShowLargeImage(boolean isShowLargeImage) {
        this.showLargeImage = isShowLargeImage;
    }
}
