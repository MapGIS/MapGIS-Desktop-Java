package com.zondy.mapgis.workspace.engine;

import com.zondy.mapgis.workspace.enums.ItemType;

import java.lang.reflect.Type;

/**
 * 菜单扩展接口，
 * 用户对INodeStyle中IPopupMenu进行扩展
 *
 * @author cxy
 * @date 2019/09/12
 */
public interface IMenuExtender {
    /**
     * 获取节点类型
     *
     * @return 节点类型
     */
    ItemType getItemType();

    /**
     * 添加菜单项
     *
     * @param item 菜单项
     * @return 返回正确索引（按总菜单项计算索引）
     */
    int addItem(IMenuItem item);

    /**
     * 插入菜单项（一定会插入成功的，小于0则插入0号位置，大于Max则添加到末尾）
     *
     * @param item 菜单项
     * @param index 要插入的位置（按总菜单项计算索引）
     * @return 返回正确索引（按总菜单项计算索引）
     */
    int insertItem(IMenuItem item, int index);

    /**
     * 获取索引（仅在扩展集合中计算）
     *
     * @param item 菜单项
     * @return 返回索引（按总菜单项计算索引）
     */
    int getIndex(IMenuItem item);

    /**
     * 获取索引（在整个集合中计算）
     *
     * @param itemType 菜单项的类型串：item.ToString()
     * @return 返回索引（按总菜单项计算索引）
     */
    int getIndex(String itemType);

    /**
     * 移除菜单项
     *
     * @param item 菜单项
     * @return 菜单项位于扩展集合中返回true，否则返回false
     */
    boolean removeItem(IMenuItem item);

    /**
     * 移除制定位置菜单项（按总菜单项计算索引）
     *
     * @param index 索引
     * @return 菜单项位于扩展集合中返回true，否则返回false
     */
    boolean removeItem(int index);

    /**
     * 清空扩展菜单项集合
     */
    void clearItems();

    /**
     * 获取菜单项总集合(扩展项加固有项)的副本
     *
     * @return 菜单项总集合的副本
     */
    IMenuItem[] getItems();
}
