package com.zondy.mapgis.workspace.engine;

import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.workspace.enums.ItemType;
import javafx.scene.image.Image;

import java.lang.reflect.Type;

/**
 * 节点样式
 *
 * @author cxy
 * @date 2019/09/12
 */
public interface IItemStyle {
    // TODO: 2019/09/12 暂用 Image 代替 BitMap
    /**
     * 获取节点图标
     *
     * @return 节点图标
     */
    Image getImage();

    /**
     * 获取节点右键菜单
     *
     * @return 节点右键菜单
     */
    IPopMenu getPopMenu();

    /**
     * 获取节点类型
     *
     * @return 节点类型
     */
    ItemType getItemType();

    /**
     * 取子类型样式
     *
     * @param item 文档项
     * @return 子类型样式
     */
    Image getSubTypeImage(DocumentItem item);
}
