package com.zondy.mapgis.workspace.itemstyle;

import com.zondy.mapgis.geometry.GeomType;
import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.scene.Vector3DLayer;
import com.zondy.mapgis.workspace.engine.IItemStyle;
import com.zondy.mapgis.workspace.engine.IPopMenu;
import com.zondy.mapgis.workspace.enums.ItemType;
import javafx.scene.image.Image;

/**
 * 三维矢量图层节点样式
 *
 * @author cxy
 * @date 2019/11/13
 */
public class Vector3DLayerItemStyle implements IItemStyle {
    private Vector3DLayerPopMenu vector3DLayerPopMenu;
    private Image image;
    private Image pntImage;
    private Image linImage;
    private Image regImage;
    private Image annImage;
    private Image surfaceImage;
    private Image entityImage;
    private Image unKnownImage;

    /**
     * 三维矢量图层节点样式
     */
    public Vector3DLayerItemStyle() {
        vector3DLayerPopMenu = new Vector3DLayerPopMenu();
        image = new Image(getClass().getResourceAsStream("/Png_SfCls_16.png"));
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
        return vector3DLayerPopMenu;
    }

    /**
     * 获取节点类型
     *
     * @return 节点类型
     */
    @Override
    public ItemType getItemType() {
        return ItemType.VECTOR3DLAYER;
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
        if (item instanceof Vector3DLayer) {
            Vector3DLayer vectLayer = (Vector3DLayer) item;
            switch (vectLayer.getGeometryType()) {
                case GeomPnt:
                    rtnImage = this.pntImage;
                    break;
                case GeomLin:
                    rtnImage = this.linImage;
                    break;
                case GeomReg:
                    rtnImage = this.regImage;
                    break;
                case GeomAnn:
                    rtnImage = this.annImage;
                    break;
                case GeomSurface:
                    rtnImage = this.surfaceImage;
                    break;
                case GeomEntity:
                    rtnImage = this.entityImage;
                    break;
                case GeomUnknown:
                    rtnImage = this.unKnownImage;
                    break;
                default:
                    break;
            }
        }
        return rtnImage;
    }
}
