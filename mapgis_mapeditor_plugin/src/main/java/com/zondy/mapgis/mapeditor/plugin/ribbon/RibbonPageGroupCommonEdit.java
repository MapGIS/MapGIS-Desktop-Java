package com.zondy.mapgis.mapeditor.plugin.ribbon;

import com.zondy.mapgis.mapeditor.plugin.command.SelectDataByAttCommand;
import com.zondy.mapgis.pluginengine.ui.IItem;
import com.zondy.mapgis.pluginengine.ui.IRibbonPageGroup;
import com.zondy.mapgis.pluginengine.ui.Item;
import com.zondy.mapgis.pluginengine.ui.SubItem;

/**
 * @ClassName RibbonPageGroupCommonEdit
 * @Description: TODO
 * @Author ysp
 * @Date 2020/3/24
 **/
public class RibbonPageGroupCommonEdit implements IRibbonPageGroup {
    private IItem[] items;

    public RibbonPageGroupCommonEdit() {
        items = new IItem[1];
        items[0] = new Item(SelectDataByAttCommand.class.getName(), false, true);
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
        return "通用编辑";
    }
}
