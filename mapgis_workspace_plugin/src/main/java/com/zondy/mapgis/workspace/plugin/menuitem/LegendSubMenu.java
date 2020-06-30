package com.zondy.mapgis.workspace.plugin.menuitem;

import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.workspace.engine.IMenuItem;
import com.zondy.mapgis.workspace.engine.ISubMenu;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import javafx.scene.image.Image;

/**
 * 图例板菜单
 *
 * @author cxy
 * @date 2019/11/21
 */
public class LegendSubMenu implements ISubMenu {
    private IMenuItem[] menuItems;

    /**
     * 图例板菜单
     *
     * @param application 应用程序
     */
    public LegendSubMenu(IApplication application) {
        menuItems = new IMenuItem[]{
                new SetLegendBoardMenuItem(application),
                new LookLegendBoardMenuItem(application),
                new PickupLegendMenuItem(),
                new LegendBoardToSfclsMenuItem(application)
        };
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
        return "图例板";
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
        for (IMenuItem menuItem : menuItems) {
            menuItem.onCreate(ws);
        }
    }
}
