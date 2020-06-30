package com.zondy.mapgis.mapeditor.plugin.ribbon;

import com.zondy.mapgis.mapeditor.plugin.command.*;
import com.zondy.mapgis.pluginengine.ui.IItem;
import com.zondy.mapgis.pluginengine.ui.IRibbonPageGroup;
import com.zondy.mapgis.pluginengine.ui.Item;
import com.zondy.mapgis.pluginengine.ui.SubItem;

public class RibbonPageGroupSpatialAnalysis implements IRibbonPageGroup
{
    private IItem[] items;

    public RibbonPageGroupSpatialAnalysis()
    {
        IItem[] clip = new IItem[]{
                new Item(MultiPolygonClipCommand.class.getName()),
                new Item(RegFileClipCommand.class.getName()),
                new Item(StandardClipCommand.class.getName(), true),
                new Item(ProjectClipCommand.class.getName())
        };

        IItem[] query = new IItem[]{
                new Item(SpatialQueryCommand.class.getName()),
                new Item(ExtractFeatureCommand.class.getName())
        };

        IItem[] merge = new IItem[]{
                new Item(MergeRegCommand.class.getName()),
                new Item(LinkLineCommand.class.getName())
        };

        IItem[] buffer = new IItem[]{
                new Item(BufferAnalysisCommand.class.getName()),
                new Item(MultipleRingBufferAnalysisCommand.class.getName())
        };

        IItem[] topo = new IItem[]{
                new Item(TopoCheckCommand.class.getName()),
                new Item(TopoErrorManageCommand.class.getName())
        };

        items = new IItem[]{
                new Item(BufferAnalysisCommand.class.getName()),
                new Item(SpatialAnalysisCommand.class.getName()),
                new Item(SpatialQueryCommand.class.getName(), true),
                new Item(RegFileClipCommand.class.getName()),
                new Item(TopoCheckCommand.class.getName(), true, true)
//                new SubItem("裁剪", clip, ClipSubItem.class.getName(), false, true),
//                new SubItem("空间查询", query, SpatialQueryCommand.class.getName()),
//                new Item(SpatialAnalysisCommand.class.getName()),
//                new SubItem("融合图元", merge, MergeSubItem.class.getName()),
//                new SubItem("缓冲分析", buffer, BufferSubItem.class.getName()),
//                new SubItem("拓扑检查", topo, TopoSubItem.class.getName(), false, true)
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
        return "空间分析";
    }
}
