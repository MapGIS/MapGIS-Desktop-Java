package com.zondy.mapgis.mapeditor.plugin.ribbon;

import com.zondy.mapgis.pluginengine.ui.IItem;
import com.zondy.mapgis.pluginengine.ui.IRibbonPageGroup;
import com.zondy.mapgis.pluginengine.ui.Item;
import com.zondy.mapgis.mapeditor.plugin.command.*;

/**
 * 投影转换页面组
 */
public class RibbonPageGroupProjectTransform implements IRibbonPageGroup{
    private IItem[] items;

    public RibbonPageGroupProjectTransform()
    {
        items = new IItem[]{
                new Item(SimplePntProjectCommand.class.getName(),false,true),
                new Item(MultiProjectOfVectorCommand.class.getName(),true),
                new Item(MultiProjectOfRasterCommand.class.getName()),
                //new Item(MultiProjectOfTileCommand.class.getName()),
                new Item(ElpTransParamSettingCommand.class.getName(), true, true)
        };
    }

    @Override
    public IItem[] getIItems()
    {
        return items;
    }

    @Override
    public String getText()
    {
        return "投影变换";
    }
}
