package com.zondy.mapgis.workspace.plugin.ribbon;

import com.zondy.mapgis.pluginengine.ui.IItem;
import com.zondy.mapgis.pluginengine.ui.IRibbonPageGroup;
import com.zondy.mapgis.pluginengine.ui.Item;
import com.zondy.mapgis.workspace.plugin.command.AddLayerCommand;
import com.zondy.mapgis.workspace.plugin.command.AddMapCommand;
import com.zondy.mapgis.workspace.plugin.command.AddServerLayerCommand;

/**
 * 二维地图
 *
 * @author cxy
 * @date 2019/11/29
 */
public class RibbonPageGroupMap implements IRibbonPageGroup {
    private IItem[] items;

    /**
     * 构造并初始化 二维地图 页面组
     */
    public RibbonPageGroupMap() {
        items = new IItem[]{
                new Item(AddMapCommand.class.getName(), false, true),
                new Item(AddLayerCommand.class.getName()),
                new Item(AddServerLayerCommand.class.getName())
        };
    }

    /**
     * 获取页面组中的插件功能项的集合
     *
     * @return 功能项的集合
     */
    @Override
    public IItem[] getIItems() {
        return items;
    }

    /**
     * 获取页面组的标题
     *
     * @return 标题
     */
    @Override
    public String getText() {
        return "二维地图";
    }
}
