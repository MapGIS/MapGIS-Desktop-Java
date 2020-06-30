package com.zondy.mapgis.workspace.itemstyle;

import com.zondy.mapgis.geometry.GeomType;
import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.scene.ModelLayer;
import com.zondy.mapgis.workspace.engine.IItemStyle;
import com.zondy.mapgis.workspace.engine.IPopMenu;
import com.zondy.mapgis.workspace.enums.ItemType;
import javafx.scene.image.Image;

/**
 * 模型图层节点样式
 *
 * @author cxy
 * @date 2019/11/13
 */
public class ModelLayerItemStyle implements IItemStyle {
    private ModelLayerPopMenu modelLayerPopMenu;
    private Image image;
    private Image pntImage;
    private Image linImage;
    private Image regImage;
    private Image annImage;
    private Image surfaceImage;
    private Image entityImage;
    private Image unKnownImage;

    /**
     * 模型图层节点样式
     */
    public ModelLayerItemStyle() {
        modelLayerPopMenu = new ModelLayerPopMenu();
        image = new Image(getClass().getResourceAsStream("/Png_ModelLayer_16.png"));
        pntImage = new Image(getClass().getResourceAsStream("/Png_SfClsPnt_16.png"));
        linImage = new Image(getClass().getResourceAsStream("/Png_SfClsLin_16.png"));
        regImage = new Image(getClass().getResourceAsStream("/Png_SfClsReg_16.png"));
        annImage = new Image(getClass().getResourceAsStream("/Png_ACls_16.png"));
        surfaceImage = new Image(getClass().getResourceAsStream("/Png_SfClsSurface_16.png"));
        entityImage = new Image(getClass().getResourceAsStream("/Png_SfClsEntity_16.png"));
        unKnownImage = new Image(getClass().getResourceAsStream("/Png_Unknown_16.png"));
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
        return modelLayerPopMenu;
    }

    /**
     * 获取节点类型
     *
     * @return 节点类型
     */
    @Override
    public ItemType getItemType() {
        return ItemType.MODELLAYER;
    }

    /**
     * 取子类型样式
     *
     * @param item 文档项
     * @return 子类型样式
     */
    @Override
    public Image getSubTypeImage(DocumentItem item) {
        Image rtnImage = null;
        if (item instanceof ModelLayer) {
            ModelLayer modelLayer = (ModelLayer) item;
            GeomType geometryType = modelLayer.getGeometryType();
            if (GeomType.GeomPnt.equals(geometryType)) {
                rtnImage = this.pntImage;
            } else if (GeomType.GeomLin.equals(geometryType)) {
                rtnImage = this.linImage;
            } else if (GeomType.GeomReg.equals(geometryType)) {
                rtnImage = this.regImage;
            } else if (GeomType.GeomAnn.equals(geometryType)) {
                rtnImage = this.annImage;
            } else if (GeomType.GeomSurface.equals(geometryType)) {
                rtnImage = this.surfaceImage;
            } else if (GeomType.GeomEntity.equals(geometryType)) {
                rtnImage = this.entityImage;
            } else if (GeomType.GeomUnknown.equals(geometryType)) {
                rtnImage = this.unKnownImage;
            }
        }
        image = rtnImage;
        return rtnImage;
    }
}
