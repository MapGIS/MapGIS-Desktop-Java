package com.zondy.mapgis.workspace.itemstyle;

import com.zondy.mapgis.map.Document;
import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.workspace.engine.IMenuItem;
import com.zondy.mapgis.workspace.engine.ISinglePopMenu;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import com.zondy.mapgis.workspace.menuitem.*;

import java.io.File;

/**
 * 文档右键菜单
 *
 * @author cxy
 * @date 2019/10/29
 */
public class DocumentPopMenu implements ISinglePopMenu {
    private IWorkspace workspace;
    private IMenuItem[] menuItems;

    /**
     * 文档右键菜单
     */
    public DocumentPopMenu() {
        // TODO: 右键菜单补充
        menuItems = new IMenuItem[10];
        menuItems[0] = new AddMapMenuItem();
        menuItems[1] = new AddSceneMenuItem();
        menuItems[2] = new ImportMapOrSceneMenuItem();
//        menuItems[3] = new PreviewLayoutMenuItem();
        menuItems[4] = new RenameItemMenuItem();
        menuItems[5] = new OpenToDocDirectoryMenuItem();
//        menuItems[6] = new ExportToTemplateMenuItem();
        menuItems[7] = new SaveDocumentMenuItem();
        menuItems[8] = new CloseDocumentMenuItem();
        menuItems[9] = new DocumentItemPropertyMenuItem();
    }

    /**
     * 菜单展开前过程
     *
     * @param item 文档项
     */
    @Override
    public void opening(DocumentItem item) {
        this.workspace.setMenuItemBeginGroup(menuItems[2], true);
        this.workspace.setMenuItemBeginGroup(menuItems[3], true);
        this.workspace.setMenuItemBeginGroup(menuItems[4], true);

        this.workspace.setMenuItemEnable(menuItems[5], item instanceof Document && new File(((Document) item).getFilePath()).exists());
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
        this.workspace = ws;
    }
}
