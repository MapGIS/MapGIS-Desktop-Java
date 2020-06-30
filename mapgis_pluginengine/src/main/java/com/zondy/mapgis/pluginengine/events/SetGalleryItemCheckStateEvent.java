package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.pluginengine.plugin.GalleryItem;

import java.util.EventObject;

/**
 * 设置 GalleryItem 的 Check 状态事件
 *
 * @author cxy
 * @date 2019/09/16
 */
public class SetGalleryItemCheckStateEvent  extends EventObject {
    private transient GalleryItem galleryItem;
    private transient boolean checkState;

    /**
     * 设置 GalleryItem 的 Check 状态事件
     *
     * @param source 事件源
     * @throws IllegalArgumentException if source is null.
     */
    public SetGalleryItemCheckStateEvent(Object source, GalleryItem galleryItem, boolean checkState) {
        super(source);
        this.galleryItem = galleryItem;
        this.checkState = checkState;
    }

    /**
     * 获取 GalleryItem
     *
     * @return GalleryItem
     */
    public GalleryItem getGalleryItem() {
        return galleryItem;
    }

    /**
     * 获取 Check 状态
     *
     * @return Check 状态
     */
    public boolean isCheckState() {
        return checkState;
    }
}
