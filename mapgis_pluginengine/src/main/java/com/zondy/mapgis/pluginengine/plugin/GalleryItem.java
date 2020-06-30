package com.zondy.mapgis.pluginengine.plugin;

import javafx.scene.image.Image;

/**
 * 工具廊插件项
 *
 * @author cxy
 * @date 2019/09/10
 */
public class GalleryItem {
    private String caption;
    private String description;
    private String groupName;
    // TODO: 2019/09/10 BitMap 暂用 Image 替代
    private Image image;
    private Image hoverImage;
    private boolean checked;
    private String toolTip;
    private Object tag;

    /**
     * 工具廊插件项
     */
    public GalleryItem() {

    }

    /**
     * 工具廊插件项
     *
     * @param caption   标题
     * @param image     图标
     * @param groupName 所属组名
     */
    public GalleryItem(String caption, Image image, String groupName) {
        this.caption = caption;
        this.image = image;
        this.groupName = groupName;
    }

    /**
     * 工具廊插件项
     *
     * @param caption     标题
     * @param description 描述
     * @param image       图标
     * @param hoverImage  Hover图标与下拉图标
     * @param groupName   所属组名
     */
    public GalleryItem(String caption, String description, Image image, Image hoverImage, String groupName) {
        this.caption = caption;
        this.description = description;
        this.image = image;
        this.hoverImage = hoverImage;
        this.groupName = groupName;
    }

    /**
     * 工具廊插件项
     *
     * @param caption     标题
     * @param description 描述
     * @param image       图标
     * @param hoverImage  Hover图标与下拉图标
     * @param groupName   所属组名
     * @param checked     初始状态是否被按下
     */
    public GalleryItem(String caption, String description, Image image, Image hoverImage, String groupName, boolean checked) {
        this.caption = caption;
        this.description = description;
        this.image = image;
        this.hoverImage = hoverImage;
        this.groupName = groupName;
        this.checked = checked;
    }

    /**
     * 工具廊插件项
     *
     * @param caption     标题
     * @param description 描述
     * @param image       图标
     * @param hoverImage  Hover图标与下拉图标
     * @param groupName   所属组名
     * @param tooltip     提示
     */
    public GalleryItem(String caption, String description, Image image, Image hoverImage, String groupName, String tooltip) {
        this.caption = caption;
        this.description = description;
        this.image = image;
        this.hoverImage = hoverImage;
        this.groupName = groupName;
        this.toolTip = tooltip;
    }

    /**
     * 工具廊插件项
     *
     * @param caption     标题
     * @param description 描述
     * @param image       图标
     * @param hoverImage  Hover图标与下拉图标
     * @param groupName   所属组名
     * @param tooltip     提示
     * @param tag         工具廊项有关数据的对象
     */
    public GalleryItem(String caption, String description, Image image, Image hoverImage, String groupName, String tooltip, Object tag) {
        this.caption = caption;
        this.description = description;
        this.image = image;
        this.hoverImage = hoverImage;
        this.groupName = groupName;
        this.toolTip = tooltip;
        this.tag = tag;
    }

    /**
     * 获取标题
     *
     * @return 标题
     */
    public String getCaption() {
        return caption;
    }

    /**
     * 设置标题
     *
     * @param caption 标题
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * 获取描述
     *
     * @return 描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置描述
     *
     * @param description 描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取所属组名
     *
     * @return 所属组名
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * 设置所属组名
     *
     * @param groupName 所属组名
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * 获取图标
     *
     * @return 图标
     */
    public Image getImage() {
        return image;
    }

    /**
     * 设置图标
     *
     * @param image 图标
     */
    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * 获取Hover图标与下拉图标
     *
     * @return Hover图标与下拉图标
     */
    public Image getHoverImage() {
        return hoverImage;
    }

    /**
     * 设置Hover图标与下拉图标
     *
     * @param hoverImage Hover图标与下拉图标
     */
    public void setHoverImage(Image hoverImage) {
        this.hoverImage = hoverImage;
    }

    /**
     * 获取初始状态是否被按下
     *
     * @return true/false
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * 设置初始状态是否被按下
     *
     * @param checked true/false
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    /**
     * 获取提示
     *
     * @return 提示
     */
    public String getToolTip() {
        return toolTip;
    }

    /**
     * 设置提示
     *
     * @param toolTip 提示
     */
    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
    }

    /**
     * 获取工具廊项有关数据的对象
     *
     * @return 数据对象
     */
    public Object getTag() {
        return tag;
    }

    /**
     * 设置工具廊项有关数据的对象
     *
     * @param tag 数据对象
     */
    public void setTag(Object tag) {
        this.tag = tag;
    }
}
