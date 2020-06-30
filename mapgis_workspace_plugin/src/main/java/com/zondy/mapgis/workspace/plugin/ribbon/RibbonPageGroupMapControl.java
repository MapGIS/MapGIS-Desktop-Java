package com.zondy.mapgis.workspace.plugin.ribbon;

import com.zondy.mapgis.pluginengine.ui.ButtonGroup;
import com.zondy.mapgis.pluginengine.ui.IItem;
import com.zondy.mapgis.pluginengine.ui.IRibbonPageGroup;
import com.zondy.mapgis.pluginengine.ui.Item;
import com.zondy.mapgis.workspace.plugin.command.*;

public class RibbonPageGroupMapControl implements IRibbonPageGroup {
    private IItem[] items;

    public RibbonPageGroupMapControl() {
        ButtonGroup buttonGroup1 = new ButtonGroup(
                new Item(ZoomInCommand.class.getName()),
                new Item(ZoomOutCommand.class.getName()),
                new Item(MoveWindowCommand.class.getName()),
                new Item(RestoreCommand.class.getName()),
                new Item(PreWindowCommand.class.getName()),
                new Item(OriginalDisplayCommand.class.getName()),
                new Item(EagleEyeCommand.class.getName()));

        ButtonGroup buttonGroup2 = new ButtonGroup(
                new Item(RedoCommand.class.getName()),
                new Item(UndoCommand.class.getName()),
                new Item(ClearToolCommand.class.getName()));

        items = new IItem[]{buttonGroup1, buttonGroup2};
    }

    @Override
    public IItem[] getIItems() {
        return items;
    }

    @Override
    public String getText() {
        return "地图";
    }
}
