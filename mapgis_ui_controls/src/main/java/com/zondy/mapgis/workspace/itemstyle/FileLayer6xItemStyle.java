package com.zondy.mapgis.workspace.itemstyle;

import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.map.FileLayer6x;
import com.zondy.mapgis.workspace.engine.IItemStyle;
import com.zondy.mapgis.workspace.engine.IPopMenu;
import com.zondy.mapgis.workspace.enums.ItemType;
import javafx.scene.image.Image;

/**
 * 6x图层图层节点风格
 *
 * @author cxy
 * @date 2019/12/23
 */
public class FileLayer6xItemStyle implements IItemStyle {
    private FileLayer6xPopMenu file6xPopMenu;
    private Image bitmap;
    private Image pnt6xBitmap;
    private Image lin6xBitmap;
    private Image reg6xBitmap;
    private Image unKnown6xBitmap;

    public FileLayer6xItemStyle() {
        file6xPopMenu = new FileLayer6xPopMenu();
        bitmap = new Image(getClass().getResourceAsStream("/Png_SfCls_16.png"));
        pnt6xBitmap = new Image(getClass().getResourceAsStream("/Png_6xPnt_16.png"));
        lin6xBitmap = new Image(getClass().getResourceAsStream("/Png_6xLin_16.png"));
        reg6xBitmap = new Image(getClass().getResourceAsStream("/Png_6xReg_16.png"));
        unKnown6xBitmap = new Image(getClass().getResourceAsStream("/Png_Unknown_16.png"));
    }

    /**
     * 获取节点图标
     *
     * @return 节点图标
     */
    @Override
    public Image getImage() {
        return bitmap;
    }

    /**
     * 获取节点右键菜单
     *
     * @return 节点右键菜单
     */
    @Override
    public IPopMenu getPopMenu() {
        return file6xPopMenu;
    }

    /**
     * 获取节点类型
     *
     * @return 节点类型
     */
    @Override
    public ItemType getItemType() {
        return ItemType.FILELAYER6X;
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
        if (item instanceof FileLayer6x) {
            FileLayer6x fileLayer6x = (FileLayer6x) item;
            if (fileLayer6x.getCount() == 2) {
                rtnImage = this.pnt6xBitmap;
            } else if (fileLayer6x.getCount() == 1) {
                rtnImage = this.lin6xBitmap;
            } else if (fileLayer6x.getCount() == 3) {
                rtnImage = this.reg6xBitmap;
            } else {
                rtnImage = this.unKnown6xBitmap;
            }
        }
        this.bitmap = rtnImage;
        return rtnImage;
    }
}
