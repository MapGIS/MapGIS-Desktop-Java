package com.zondy.mapgis.workspace.itemstyle;

import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.workspace.engine.IItemStyle;
import com.zondy.mapgis.workspace.engine.IPopMenu;
import com.zondy.mapgis.workspace.enums.ItemType;
import javafx.scene.image.Image;

/**
 * 镶嵌数据集图层节点样式
 *
 * @author cxy
 * @date 2019/11/13
 */
public class MosaicRasterLayerItemStyle implements IItemStyle {
    private MosaicRasterLayerPopMenu mosaicRasterLayerPopMenu;
    private Image image;

    /**
     * 镶嵌数据集图层节点样式
     */
    public MosaicRasterLayerItemStyle() {
        mosaicRasterLayerPopMenu = new MosaicRasterLayerPopMenu();
        image = new Image(getClass().getResourceAsStream("/Png_RasterDs_16.png"));
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
        return mosaicRasterLayerPopMenu;
    }

    /**
     * 获取节点类型
     *
     * @return 节点类型
     */
    @Override
    public ItemType getItemType() {
        // TODO: 补充 MosaicDatasetRasterLayer 类型
        return null;
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
