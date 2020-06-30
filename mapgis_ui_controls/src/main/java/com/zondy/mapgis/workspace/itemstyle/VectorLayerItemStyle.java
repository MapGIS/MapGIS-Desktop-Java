package com.zondy.mapgis.workspace.itemstyle;

import com.zondy.mapgis.geodatabase.XClsType;
import com.zondy.mapgis.geometry.GeomType;
import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.map.GroupLayer;
import com.zondy.mapgis.map.MapLayer;
import com.zondy.mapgis.map.VectorLayer;
import com.zondy.mapgis.workspace.engine.IItemStyle;
import com.zondy.mapgis.workspace.engine.IPopMenu;
import com.zondy.mapgis.workspace.enums.ItemType;
import javafx.scene.image.Image;

/**
 * 矢量图层节点风格
 *
 * @author cxy
 * @date 2019/11/11
 */
public class VectorLayerItemStyle implements IItemStyle {
    private VectorLayerPopMenu vectorLayerPopMenu;
    private Image image;
    private Image pntImage;
    private Image linImage;
    private Image regImage;
    private Image annImage;
    private Image surfaceImage;
    private Image entityImage;
    private Image unKnownImage;
    private Image footprintsImage;  // 轮廓线
    private Image boundaryImage;    // 边界线
    private Image seamLineImage;    // 接缝线

    /**
     * 矢量图层节点风格
     */
    public VectorLayerItemStyle() {
        vectorLayerPopMenu = new VectorLayerPopMenu();
        image = new Image(getClass().getResourceAsStream("/Png_SfCls_16.png"));
        pntImage = new Image(getClass().getResourceAsStream("/Png_SfClsPnt_16.png"));
        linImage = new Image(getClass().getResourceAsStream("/Png_SfClsLin_16.png"));
        regImage = new Image(getClass().getResourceAsStream("/Png_SfClsReg_16.png"));
        annImage = new Image(getClass().getResourceAsStream("/Png_ACls_16.png"));
        surfaceImage = new Image(getClass().getResourceAsStream("/Png_SfClsSurface_16.png"));
        entityImage = new Image(getClass().getResourceAsStream("/Png_SfClsEntity_16.png"));
        unKnownImage = new Image(getClass().getResourceAsStream("/Png_Unknown_16.png"));
        footprintsImage = new Image(getClass().getResourceAsStream("/Png_FootPrints_16.png"));
        boundaryImage = new Image(getClass().getResourceAsStream("/Png_Boundary_16.png"));
        seamLineImage = new Image(getClass().getResourceAsStream("/Png_SeamLine_16.png"));
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
        return vectorLayerPopMenu;
    }

    /**
     * 获取节点类型
     *
     * @return 节点类型
     */
    @Override
    public ItemType getItemType() {
        return ItemType.VECTORLAYER;
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
        if (item instanceof VectorLayer) {
            VectorLayer vectLayer = (VectorLayer) item;
            if (vectLayer.getParent() != null && ((vectLayer.getParent() instanceof GroupLayer) && ((MapLayer) vectLayer.getParent()).getClsType() == XClsType.XMosaicDS)) {
                GroupLayer mdsLayer = (GroupLayer) vectLayer.getParent();
                int index = 0;
                for (int i = 0; i < mdsLayer.getCount(); i++) {
                    if (mdsLayer.item(i).getName().equals(vectLayer.getName())) {
                        index = i;
                        break;
                    }
                }
                switch (index) {
                    case 1:
                        rtnImage = this.footprintsImage;
                        break;
                    case 2:
                        rtnImage = this.boundaryImage;
                        break;
                    case 3:
                        rtnImage = this.seamLineImage;
                        break;
                    default:
                        rtnImage = this.footprintsImage;
                        break;
                }
            } else {
                GeomType geometryType = vectLayer.getGeometryType();
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
        }
        return rtnImage;
    }
}
