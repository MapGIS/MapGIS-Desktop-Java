package com.zondy.mapgis.workspace.plugin.ribbon;

import com.zondy.mapgis.pluginengine.ui.IItem;
import com.zondy.mapgis.pluginengine.ui.IRibbonPageGroup;
import com.zondy.mapgis.pluginengine.ui.Item;
import com.zondy.mapgis.pluginengine.ui.SubItem;
import com.zondy.mapgis.workspace.plugin.command.*;

/**
 * 三维场景
 *
 * @author cxy
 * @date 2019/11/29
 */
public class RibbonPageGroupScene implements IRibbonPageGroup {
    private IItem[] items;

    public RibbonPageGroupScene() {
        IItem[] add3DLayers = new IItem[]{
                new Item(AddModelLayerCommand.class.getName()),
                new Item(AddTerrainLayerCommand.class.getName()),
                new Item(AddLabelLayerCommand.class.getName()),
                new Item(Add2DLayerCommand.class.getName())
        };

        items = new IItem[]{
                new Item(AddSceneCommand.class.getName(), false, true),
                new SubItem("添加图层", add3DLayers, Add3DLayerSubItem.class.getName()),
                new Item(AddServer3DLayerCommand.class.getName())
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
        return "三维场景";
    }
}
