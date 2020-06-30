package com.zondy.mapgis.pluginengine.ui;

/**
 * 带有子项的界面元素项
 *
 * @author cxy
 * @date 2019/09/11
 */
public class SubItem implements IItem {
    private String caption = "";
    private IItem[] items;
    private String key = "";
    private boolean group = false;
    private boolean showLargeImage = false;

    public SubItem() {

    }

    public SubItem(String caption) {
        this.caption = caption;
    }

    public SubItem(String caption, IItem[] items) {
        this.caption = caption;
        this.items = items;
    }

    public SubItem(String caption, IItem[] items, String key) {
        this.caption = caption;
        this.items = items;
        this.key = key;
    }

    public SubItem(String caption, IItem[] items, String key, boolean group) {
        this.caption = caption;
        this.items = items;
        this.key = key;
        this.group = group;
    }

    public SubItem(String caption, IItem[] items, String key, boolean group, boolean showLargeImage) {
        this.caption = caption;
        this.items = items;
        this.key = key;
        this.group = group;
        this.showLargeImage = showLargeImage;
    }

    /**
     * 获取 SubItem 的标题
     *
     * @return 标题
     */
    public String getCaption() {
        return caption;
    }

    /**
     * 设置 SubItem 的标题
     *
     * @param caption 标题
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * 获取 SubItem 的项
     *
     * @return 项
     */
    public IItem[] getItems() {
        return items;
    }

    /**
     * 设置 SubItem 的项
     *
     * @param items 项
     */
    public void setItems(IItem[] items) {
        this.items = items;
    }

    /**
     * 获取 Item 的 Key
     *
     * @return Key
     */
    @Override
    public String getKey() {
        return key;
    }

    /**
     * 设置 Item 的 Key
     *
     * @param key 功能Key
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
