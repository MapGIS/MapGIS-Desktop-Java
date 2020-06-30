package com.zondy.mapgis.pluginengine.events;

import java.util.EventListener;

/**
 * 设置 GalleryItem 的 Check 状态事件触发器
 *
 * @author cxy
 * @date 2019/09/16
 */
public interface SetGalleryItemCheckStateListener extends EventListener {
    /**
     * 触发设置 GalleryItem 的 Check 状态事件
     *
     * @param setGalleryItemCheckStateEvent 设置 GalleryItem 的 Check 状态事件
     */
    void setGalleryItemCheckState(SetGalleryItemCheckStateEvent setGalleryItemCheckStateEvent);
}
