package com.zondy.mapgis.workspace.menuitem;

import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.workspace.Workspace;
import com.zondy.mapgis.workspace.engine.IMultiMenuItem;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import com.zondy.mapgis.workspace.event.MultiMenuItemClickEvent;
import javafx.scene.image.Image;

/**
 * 多图层属性
 *
 * @author cxy
 * @date 2019/12/16
 */
public class MultiItemPropertyMenuItem implements IMultiMenuItem {
    private IWorkspace workspace;

    /**
     * 点击菜单项
     *
     * @param items 文档项
     */
    @Override
    public void onClick(DocumentItem[] items) {
        if (items != null && items.length > 0 && workspace instanceof Workspace) {
            workspace.fireMultiMenuItemClick(new MultiMenuItemClickEvent(this, getClass(), items));
        }
    }

    /**
     * 获取菜单项图标
     *
     * @return 菜单项图标
     */
    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_MapAttr_16.png"));
    }

    /**
     * 获取命令按钮的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "属性";
    }

    /**
     * 获取命令按钮是否可用
     *
     * @return true/false
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * 获取命令按钮是否可见
     *
     * @return true/false
     */
    @Override
    public boolean isVisible() {
        return true;
    }

    /**
     * 获取命令按钮是否选中
     *
     * @return true/false
     */
    @Override
    public boolean isChecked() {
        return false;
    }

    /**
     * 获取是否启用分割符
     *
     * @return true/false
     */
    @Override
    public boolean isBeginGroup() {
        return true;
    }

    /**
     * 创建后事件
     *
     * @param ws 工作空间引擎
     */
    @Override
    public void onCreate(IWorkspace ws) {
        this.workspace = ws;
    }
}
