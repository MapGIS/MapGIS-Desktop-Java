package com.zondy.mapgis.workspace.itemstyle;

import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.workspace.engine.IItemStyle;
import com.zondy.mapgis.workspace.engine.IPopMenu;
import com.zondy.mapgis.workspace.enums.ItemType;
import javafx.scene.image.Image;

/**
 * 栅格数据集图层节点风格
 *
 * @author cxy
 * @date 2019/11/13
 */
public class RasterCatalogLayerItemStyle implements IItemStyle {
    private RasterLayerPopMenu rasterLayerPopMenu;
    private Image image;

    /**
     * 栅格数据集图层节点风格
     */
    public RasterCatalogLayerItemStyle() {
        rasterLayerPopMenu = new RasterLayerPopMenu();
        image = new Image(getClass().getResourceAsStream("/Png_RasterCatalog_16.png"));
    }

    /**
     * 获取节点图标
     *
     * @return 节点图标
     */
    @Override
    public Image getImage() {
        return image;
    }

    /**
     * 获取节点右键菜单
     *
     * @return 节点右键菜单
     */
    @Override
    public IPopMenu getPopMenu() {
        return rasterLayerPopMenu;
    }

    /**
     * 获取节点类型
     *
     * @return 节点类型
     */
    @Override
    public ItemType getItemType() {
        return ItemType.RASTERCATALOGLAYER;
    }

    /**
     * 取子类型样式
     *
     * @param item 文档项
     * @return 子类型样式
     */
    @Override
    public Image getSubTypeImage(DocumentItem item) {
        return null;
    }
}
