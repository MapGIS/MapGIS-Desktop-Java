package com.zondy.mapgis.workspace.plugin.menuitem;

import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.workspace.Workspace;
import com.zondy.mapgis.workspace.engine.ISingleMenuItem;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import com.zondy.mapgis.workspace.event.MenuItemClickEvent;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;

/**
 * 浏览属性菜单项
 *
 * @author zkj
 * @since 2020-5-22
 */
public class LookRecords implements ISingleMenuItem {
    //查看属性
    private IWorkspace _ws;

    //region ISingleMenuItem 成员
    @Override
    public void onClick(DocumentItem item) {
        ((Workspace)this._ws).fireMenuItemClick(new MenuItemClickEvent(this,LookRecords.class,item));
    }
    //endregion

    //region IMenuItem 成员
    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_LookUpAttribute_16.png"));
    }

    @Override
    public String getCaption() {
        return "查看属性";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public boolean isChecked() {
        return false;
    }

    @Override
    public boolean isBeginGroup() {
        return false;
    }

    @Override
    public void onCreate(IWorkspace ws) {
        this._ws = ws;
    }
    //endregion
}
