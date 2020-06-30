package com.zondy.mapgis.rastereditor.plugin.ribbon;

import com.zondy.mapgis.pluginengine.ui.IItem;
import com.zondy.mapgis.pluginengine.ui.IRibbonPageGroup;
import com.zondy.mapgis.pluginengine.ui.Item;
import com.zondy.mapgis.rastereditor.plugin.command.*;

/**
 * Created by Administrator on 2020/6/8.
 */
public class RibbonPageGroupRasterAnalyze implements IRibbonPageGroup {

    private IItem[] items;

    public RibbonPageGroupRasterAnalyze()
    {
        items = new IItem[]{
                new Item(FormulaCaculateCommand.class.getName()),
                new Item(CreateRasterFromMathCommand.class.getName()),
                new Item(RasterReclassCommand.class.getName()),
                new Item(TerriainAnalysisCommand.class.getName(),true),
                new Item(RasterInterpolationCommand.class.getName()),
                new Item(ImportantPointGetCommand.class.getName()),
                new Item(SunLightXYOutCommand.class.getName())
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
        return "栅格分析";
    }
}
