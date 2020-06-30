package com.zondy.mapgis.workspace.plugin.ribbon;

import com.zondy.mapgis.pluginengine.ui.IItem;
import com.zondy.mapgis.pluginengine.ui.IRibbonPageGroup;
import com.zondy.mapgis.pluginengine.ui.Item;
import com.zondy.mapgis.workspace.plugin.command.*;

/**
 * 文档
 *
 * @author cxy
 * @date 2019/11/29
 */
public class RibbonPageGroupFile implements IRibbonPageGroup {
    private IItem[] items;

    /**
     * 构造并初始化 文档 页面组
     */
    public RibbonPageGroupFile() {
        items = new IItem[]{
                new Item(CreateCommand.class.getName(), false, true),
                new Item(OpenCommand.class.getName(), false, true),
                new Item(SaveCommand.class.getName()),
                new Item(SaveAsCommand.class.getName()),
                new Item(CloseCommand.class.getName())
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
        return "文档";
    }
}
