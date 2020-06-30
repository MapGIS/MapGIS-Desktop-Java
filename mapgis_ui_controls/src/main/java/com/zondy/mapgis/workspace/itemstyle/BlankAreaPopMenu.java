package com.zondy.mapgis.workspace.itemstyle;

import com.zondy.mapgis.map.Document;
import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.workspace.engine.IMenuItem;
import com.zondy.mapgis.workspace.engine.ISinglePopMenu;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import com.zondy.mapgis.workspace.menuitem.CloseDocumentMenuItem;
import com.zondy.mapgis.workspace.menuitem.NewDocumentMenuItem;
import com.zondy.mapgis.workspace.menuitem.OpenDocumentMenuItem;
import com.zondy.mapgis.workspace.menuitem.SaveDocumentMenuItem;

/**
 * 空白处右键菜单
 *
 * @author cxy
 * @date 2019/10/29
 */
public class BlankAreaPopMenu implements ISinglePopMenu {
    private IWorkspace workspace;
    private IMenuItem[] menuItems;

    /**
     * 空白处右键菜单
     */
    public BlankAreaPopMenu() {
        this.menuItems = new IMenuItem[4];
        this.menuItems[0] = new NewDocumentMenuItem();
        this.menuItems[1] = new OpenDocumentMenuItem();
        this.menuItems[2] = new SaveDocumentMenuItem();
        this.menuItems[3] = new CloseDocumentMenuItem();
    }

    /**
     * 菜单展开前过程
     *
     * @param item 文档项
     */
    @Override
    public void opening(DocumentItem item) {
        if (item != null && item instanceof Document) {
            Document doc = (Document) item;
            // TODO: 2019/10/29 待确定支持图层后补充注释函数
            workspace.setMenuItemEnable(menuItems[2], doc.getIsDirty() || doc.getIsNew() /*|| this.Has6xEditLayers(doc) || this.HasTempLayers(doc))*/);
        }
    }

    /**
     * 获取菜单栏插件功能项的集合
     *
     * @return 菜单栏插件功能项的集合
     */
    @Override
    public IMenuItem[] getItems() {
        return menuItems;
    }

    /**
     * 创建后事件
     *
     * @param ws 工作空间
     */
    @Override
    public void onCreate(IWorkspace ws) {
        workspace = ws;
    }
}
