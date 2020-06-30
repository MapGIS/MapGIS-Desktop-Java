package com.zondy.mapgis.pluginengine.plugin;

import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.ui.IItem;
import com.sun.glass.ui.Size;

/**
 * 工具廊插件
 * 此插件仅能在Ribbon框架中使用
 *
 * @author cxy
 * @date 2019/09/11
 */
public interface IGallery extends IPlugin {
    /**
     * 获取工具廊是否显示HoverImage
     *
     * @return true/false
     */
    boolean isAllowHoverImages();

    /**
     * 获取工具廊的标题
     *
     * @return 标题
     */
    String getCaption();

    /**
     * 获取工具廊显示的列的数量
     *
     * @return 列的数量
     */
    int getColumnCount();

    /**
     * 获取绘制 GalleryItem 时是否绘制背景
     *
     * @return true/false
     */
    boolean isDrawImageBackground();

    /**
     * 获取 Gallery 是否可用
     * @return true/false
     */
    boolean isEnabled();

    /**
     * 获取工具廊中的 GalleryItem 的集合
     *
     * @return GalleryItem 的集合
     */
    GalleryItem[] getGalleryItems();

    /**
     * 获取工具廊中的 HoverImage 的大小
     *
     * @return HoverImage 的大小
     */
    Size getHoverImageSize();

    /**
     * 获取工具廊中功能项的集合
     *
     * @return 功能项的集合
     */
    IItem[] getIItems();

    /**
     * 获取工具廊中项的图像的大小
     *
     * @return 图像的大小
     */
    Size getImageSize();

    /**
     * 获取鼠标移到工具廊上时状态栏上出现的文本
     *
     * @return 出现的文本
     */
    String getMessage();

    /**
     * 获取工具廊下拉时显示的列的数量
     *
     * @return 列的数量
     */
    int getPopupColumnCount();

    /**
     * 获取工具廊下拉时图像的大小
     *
     * @return 图像的大小
     */
    Size getPopupImageSize();

    /**
     * 获取工具廊下拉时显示的行的数量
     *
     * @return 行的数量
     */
    int getPopupRowCount();

    /**
     * 获取工具廊显示的行的数量
     *
     * @return 行的数量
     */
    int getRowCount();

    /**
     * 获取是否显示工具廊的组标题
     *
     * @return true/false
     */
    boolean isShowGroupCaption();

    /**
     * 获取是否显示GalleryItem的Text(包括GalleryItem的Caption和Description)
     *
     * @return true/false
     */
    boolean isShowItemText();

    /**
     * 获取鼠标停留在工具廊上时弹出的提示文本
     *
     * @return 提示文本
     */
    String getTooltip();

    /**
     * 工具廊中的项被单击时引发的事件
     *
     * @param item 工具廊中的项
     */
    void itemClick(GalleryItem item);

    /**
     * 工具廊被创建时引发的事件
     *
     * @param app 框架的宿主对象
     */
    void onCreate(IApplication app);
}
