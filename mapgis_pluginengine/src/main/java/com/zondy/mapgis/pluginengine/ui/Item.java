package com.zondy.mapgis.pluginengine.ui;

/**
 * 界面元素项
 *
 * @author cxy
 * @date 2019/09/11
 */
public class Item implements IItem {
    private String key = "";
    private boolean group = false;
    private boolean showLargeImage = false;

    /**
     * 界面元素项
     */
    public Item() {

    }

    /**
     * 界面元素项
     *
     * @param key ID
     */
    public Item(String key) {
        this.key = key;
    }

    /**
     * 界面元素项
     *
     * @param key   ID
     * @param group 是否属于新组
     */
    public Item(String key, boolean group) {
        this.key = key;
        this.group = group;
    }

    /**
     * 界面元素项
     *
     * @param key            ID
     * @param group          是否属于新组
     * @param showLargeImage 是否显示大图标
     */
    public Item(String key, boolean group, boolean showLargeImage) {
        this.key = key;
        this.group = group;
        this.showLargeImage = showLargeImage;
    }

    /**
     * 获取 Item 的 ID
     *
     * @return ID
     */
    @Override
    public String getKey() {
        return key;
    }

    /**
     * 设置 Item 的 ID
     *
     * @param key ID
     */
    @Override
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 获取该按钮是否属于一个新组
     *
     * @return true/false
     */
    @Override
    public boolean isGroup() {
        return group;
    }

    /**
     * 设置该按钮是否属于一个新组
     *
     * @param isGroup true/false
     */
    @Override
    public void setGroup(boolean isGroup) {
        this.group = isGroup;
    }

    /**
     * 获取是否显示大图标
     *
     * @return true/false
     */
    @Override
    public boolean isShowLargeImage() {
        return showLargeImage;
    }

    /**
     * 设置是否显示大图标
     *
     * @param isShowLargeImage true/false
     */
    @Override
    public void setShowLargeImage(boolean isShowLargeImage) {
        this.showLargeImage = isShowLargeImage;
    }
}
