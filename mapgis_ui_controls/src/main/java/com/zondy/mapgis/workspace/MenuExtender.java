package com.zondy.mapgis.workspace;

import com.zondy.mapgis.workspace.engine.*;
import com.zondy.mapgis.workspace.enums.ItemType;
import com.zondy.mapgis.workspace.event.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 右键菜单扩展类
 *
 * @author cxy
 * @date 2019/10/25
 */
public class MenuExtender implements IMenuExtender {
    private List<IMenuItem> menuItems;
    private ItemType itemType;
    private IWorkspace workspace;

    /**
     * 右键菜单扩展类构造
     *
     * @param itemType      类型
     * @param workspace 工作空间
     */
    public MenuExtender(ItemType itemType, IWorkspace workspace) {
        this.menuItems = new ArrayList<>();
        this.itemType = itemType;
        this.workspace = workspace;

        IItemStyle itemStyle = this.workspace.getItemStyle(itemType);
        if (itemStyle != null) {
            IPopMenu popMenu = itemStyle.getPopMenu();
            if (popMenu != null) {
                IMenuItem[] popMenuItems = popMenu.getItems();
                if (popMenuItems != null) {
                    this.menuItems.addAll(new ArrayList<>(Arrays.asList(popMenuItems)));
                }
            }
        }
    }

    /**
     * 获取节点类型
     *
     * @return 节点类型
     */
    @Override
    public ItemType getItemType() {
        return itemType;
    }

    /**
     * 添加菜单项
     *
     * @param menuItem 菜单项
     * @return 返回正确索引（按总菜单项计算索引）
     */
    @Override
    public int addItem(IMenuItem menuItem) {
        if (menuItem == null) {
            return -1;
        }

        this.menuItems.add(menuItem);
        if (this.addMenuItemListeners != null) {
            this.fireAddMenuItem(new AddMenuItemEvent(this, menuItem));
        }
        return this.menuItems.size();
    }

    /**
     * 插入菜单项（一定会插入成功的，小于0则插入0号位置，大于Max则添加到末尾）
     *
     * @param menuItem 菜单项
     * @param index    要插入的位置（按总菜单项计算索引）
     * @return 返回正确索引（按总菜单项计算索引）
     */
    @Override
    public int insertItem(IMenuItem menuItem, int index) {
        if (menuItem == null) {
            return -1;
        }

        int rtn = -1;
        if (index <= 0) {
            this.menuItems.add(0, menuItem);
            if (this.insertMenuItemListeners != null) {
                this.fireInsertMenuItem(new InsertMenuItemEvent(this, menuItem, 0));
            }
            rtn = 0;
        } else if (index < getCount()) {
            this.menuItems.add(index, menuItem);
            if (this.insertMenuItemListeners != null) {
                this.fireInsertMenuItem(new InsertMenuItemEvent(this, menuItem, index));
            }
            rtn = index;
        } else {
            rtn = addItem(menuItem);
        }
        return rtn;
    }

    /**
     * 获取索引（仅在扩展集合中计算）
     *
     * @param menuItem 菜单项
     * @return 返回索引（按总菜单项计算索引）
     */
    @Override
    public int getIndex(IMenuItem menuItem) {
        return menuItem == null ? -1 : this.menuItems.indexOf(menuItem);
    }

    /**
     * 获取索引（在整个集合中计算）
     *
     * @param itemType 菜单项的类型串：item.ToString()
     * @return 返回索引（按总菜单项计算索引）
     */
    @Override
    public int getIndex(String itemType) {
        int rtn = -1;
        if (!itemType.isEmpty()) {
            for (int i = 0; i < this.menuItems.size(); i++) {
                if (this.menuItems.get(i).toString().equals(itemType)) {
                    rtn = i;
                    break;
                }
            }
        }
        return rtn;
    }

    /**
     * 移除菜单项
     *
     * @param menuItem 菜单项
     * @return 菜单项位于扩展集合中返回true，否则返回false
     */
    @Override
    public boolean removeItem(IMenuItem menuItem) {
        if (menuItem == null) {
            return false;
        }

        boolean rtn = this.menuItems.remove(menuItem);
        if (rtn && this.removeMenuItemListeners != null) {
            this.fireRemoveMenuitem(new RemoveMenuItemEvent(this, menuItem));
        }
        return rtn;
    }

    /**
     * 移除制定位置菜单项（按总菜单项计算索引）
     *
     * @param index 索引
     * @return 菜单项位于扩展集合中返回true，否则返回false
     */
    @Override
    public boolean removeItem(int index) {
        if (index < 0) {
            return false;
        }

        return removeItem(this.menuItems.get(index));
    }

    /**
     * 清空扩展菜单项集合
     */
    @Override
    public void clearItems() {
        if (this.removeMenuItemListeners != null) {
            for (IMenuItem menuItem : this.menuItems) {
                this.fireRemoveMenuitem(new RemoveMenuItemEvent(this, menuItem));
            }
        }
        this.menuItems.clear();
    }

    /**
     * 获取菜单项总集合(扩展项加固有项)的副本
     *
     * @return 菜单项总集合的副本
     */
    @Override
    public IMenuItem[] getItems() {
        return this.menuItems.toArray(new IMenuItem[this.menuItems.size()]);
    }

    /**
     * 获取菜单项总集合(扩展项加固有项)的数量
     *
     * @return 数量
     */
    private int getCount() {
        return this.menuItems.size();
    }

    private ArrayList<AddMenuItemListener> addMenuItemListeners = new ArrayList<>();

    /**
     * 添加添加菜单项事件监听器
     *
     * @param addMenuItemListener 添加菜单项事件监听器
     */
    public void addAddMenuItemListener(AddMenuItemListener addMenuItemListener) {
        this.addMenuItemListeners.add(addMenuItemListener);
    }

    /**
     * 删除添加菜单项事件监听器
     *
     * @param addMenuItemListener 添加菜单项事件监听器
     */
    public void removeAddMenuItemListeners(AddMenuItemListener addMenuItemListener) {
        this.addMenuItemListeners.remove(addMenuItemListener);
    }

    /**
     * 触发添加菜单项事件
     *
     * @param addMenuItemEvent 添加菜单项事件
     */
    public void fireAddMenuItem(AddMenuItemEvent addMenuItemEvent) {
        for (AddMenuItemListener addMenuItemListener : this.addMenuItemListeners) {
            addMenuItemListener.fireAddItem(addMenuItemEvent);
        }
    }

    private ArrayList<InsertMenuItemListener> insertMenuItemListeners = new ArrayList<>();

    /**
     * 添加插入菜单项事件监听器
     *
     * @param insertMenuItemListener 插入菜单项事件监听器
     */
    public void addInsertMenuItemListener(InsertMenuItemListener insertMenuItemListener) {
        this.insertMenuItemListeners.add(insertMenuItemListener);
    }

    /**
     * 删除插入菜单项事件监听器
     *
     * @param insertMenuItemListener 插入菜单项事件监听器
     */
    public void removeInsertMenuItemListeners(InsertMenuItemListener insertMenuItemListener) {
        this.insertMenuItemListeners.remove(insertMenuItemListener);
    }

    /**
     * 触发插入菜单项事件
     *
     * @param insertMenuItemEvent 插入菜单项事件
     */
    public void fireInsertMenuItem(InsertMenuItemEvent insertMenuItemEvent) {
        for (InsertMenuItemListener insertMenuItemListener : this.insertMenuItemListeners) {
            insertMenuItemListener.fireInsertMenuItem(insertMenuItemEvent);
        }
    }

    private ArrayList<RemoveMenuItemListener> removeMenuItemListeners = new ArrayList<>();

    /**
     * 添加移除菜单项事件监听器
     *
     * @param removeMenuItemListener 移除菜单项事件监听器
     */
    public void addRemoveMenuItemListener(RemoveMenuItemListener removeMenuItemListener) {
        this.removeMenuItemListeners.add(removeMenuItemListener);
    }

    /**
     * 删除移除菜单项事件监听器
     *
     * @param removeMenuItemListener 移除菜单项事件监听器
     */
    public void removeRemoveMenuItemListeners(RemoveMenuItemListener removeMenuItemListener) {
        this.removeMenuItemListeners.remove(removeMenuItemListener);
    }

    /**
     * 触发移除菜单项事件
     *
     * @param removeMenuItemEvent 移除菜单项事件
     */
    public void fireRemoveMenuitem(RemoveMenuItemEvent removeMenuItemEvent) {
        for (RemoveMenuItemListener removeMenuItemListener : this.removeMenuItemListeners) {
            removeMenuItemListener.fireRemoveMenuItem(removeMenuItemEvent);
        }
    }

}

// IndexItem 类删除，其逻辑合并修改于 MenuExtender 类中
//class IndexItem {
//    private IMenuItem menuItem;
//    private int index;
//
//    public IndexItem(IMenuItem menuItem, int index) {
//        this.menuItem = menuItem;
//        this.index = index;
//    }
//
//    public IMenuItem getMenuItem() {
//        return menuItem;
//    }
//
//    public void setMenuItem(IMenuItem menuItem) {
//        this.menuItem = menuItem;
//    }
//
//    public int getIndex() {
//        return index;
//    }
//
//    public void setIndex(int index) {
//        this.index = index;
//    }
//}
