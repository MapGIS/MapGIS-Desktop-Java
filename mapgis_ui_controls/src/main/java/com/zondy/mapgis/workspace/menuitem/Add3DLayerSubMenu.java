package com.zondy.mapgis.workspace.menuitem;

import com.zondy.mapgis.workspace.engine.IMenuItem;
import com.zondy.mapgis.workspace.engine.ISingleMenuItem;
import com.zondy.mapgis.workspace.engine.ISubMenu;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import javafx.scene.image.Image;

import java.util.ArrayList;

/**
 * 添加图层（三维场景）
 *
 * @author cxy
 * @date 2019/11/18
 */
public class Add3DLayerSubMenu implements ISubMenu {
    private IWorkspace workspace;
    private IMenuItem[] menuItems;

    /**
     * 添加图层（三维场景）
     */
    public Add3DLayerSubMenu(){
        menuItems = new IMenuItem[4];
        menuItems[0] = new AddModelLayerMenuItem();
        menuItems[1] = new AddTerrainLayerMenuItem();
        menuItems[2] = new AddLabelLayerMenuItem();
        menuItems[3] = new Add2DLayerMenuItem();
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
     * 获取菜单项图标
     *
     * @return 菜单项图标
     */
    @Override
    public Image getImage() {
        return null;
    }

    /**
     * 获取命令按钮的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "添加图层";
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
        workspace = ws;
    }
}
