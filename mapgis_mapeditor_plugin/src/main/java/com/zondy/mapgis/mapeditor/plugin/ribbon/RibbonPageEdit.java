package com.zondy.mapgis.mapeditor.plugin.ribbon;

import com.zondy.mapgis.mapeditor.plugin.command.SelectDataByAttCommand;
import com.zondy.mapgis.mapeditor.plugin.command.edit.*;
import com.zondy.mapgis.pluginengine.ui.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName RibbonPageEdit
 * @Description: TODO
 * @Author ysp
 * @Date 2020/3/24
 **/
public class RibbonPageEdit implements IRibbonPage {
    private List<IRibbonPageGroup> ribbonPageGroupList = new ArrayList<>();

    public RibbonPageEdit() {
        ribbonPageGroupList.add(new RibbonPageGroupEdit());
        ribbonPageGroupList.add(new RibbonPageGroupSelect());
    }

    /**
     * 获取页面所属的页面类别的Key，其格式为“[命名空间].[页面类别的类名]”
     *
     * @return 页面类别的Key
     */
    @Override
    public String getCategoryKey() {
        return null;
    }

    /**
     * 获取页面的标题
     *
     * @return 标题
     */
    @Override
    public String getText() {
        return "编辑";
    }

    /**
     * 获取初始是否选中
     *
     * @return true/false
     */
    @Override
    public boolean isSelected() {
        return true;
    }

    /**
     * 获取 Ribbon 页面组集合
     *
     * @return Ribbon 页面组集合
     */
    @Override
    public IRibbonPageGroup[] getRibbonPageGroups() {
        return this.ribbonPageGroupList.toArray(new IRibbonPageGroup[0]);
    }

    public static final class RibbonPageGroupEdit implements IRibbonPageGroup {
        List<IItem> itemList = new ArrayList<>();

        public RibbonPageGroupEdit() {
            this.itemList.add(new Item(StartOrEndEditCommand.class.getName(), false, true));
            this.itemList.add(new Item(SaveEditsCommand.class.getName(), false, true));
            this.itemList.add(new ButtonGroup(true, new Item(SelectCommand.class.getName()),
                    new Item(MoveCommand.class.getName(), true),
                    new Item(RemoveCommand.class.getName()),
                    new Item(InputCommand.class.getName(), true)));
            this.itemList.add(new ButtonGroup(new Item(VertexEditCommand.class.getName())));
            //new Item(VertexMoveCommand.class.getName()),
            //new Item(VertexAddCommand.class.getName()),
            //new Item(VertexDeleteCommand.class.getName()))

        }

        @Override
        public IItem[] getIItems() {
            return itemList.toArray(new IItem[0]);
        }

        @Override
        public String getText() {
            return "编辑";
        }
    }

    /**
     * @ClassName RibbonPageGroupCommonEdit
     * @Description: TODO
     * @Author ysp
     * @Date 2020/3/24
     **/
    public static final class RibbonPageGroupSelect implements IRibbonPageGroup {
        private IItem[] items;

        public RibbonPageGroupSelect() {
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
            return "选择";
        }
    }
}

