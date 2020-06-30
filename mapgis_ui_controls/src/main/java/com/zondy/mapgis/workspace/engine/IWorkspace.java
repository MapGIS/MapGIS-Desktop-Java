package com.zondy.mapgis.workspace.engine;

import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.controls.SceneControl;
import com.zondy.mapgis.map.Document;
import com.zondy.mapgis.map.Map;
import com.zondy.mapgis.scene.Scene;
import com.zondy.mapgis.workspace.enums.ItemType;
import com.zondy.mapgis.workspace.event.*;
import javafx.scene.image.Image;

/**
 * 工作空间引擎，负责样式的加载和菜单状态操作以及一些中间转储功能，
 * 如需高级应用（截获或抛送消息时）可以将其转换为MapGIS.WorkSpaceEngine.WorkSpace对象
 *
 * @author cxy
 * @date 2019/09/12
 */
public interface IWorkspace {
    //region 载入工作空间配置

    /**
     * 部署工作空间
     */
    void loadCustomWorkSpace();

    //endregion

    //region 触发UpdateTreeEvent

    /**
     * 开始更新树界面
     */
    void beginUpdateTree();

    /**
     * 结束更新树界面
     */
    void endUpdateTree();

    //endregion

    //region 设置获取节点类型属性

    /**
     * 获取节点样式
     *
     * @param itemType 节点类型
     * @return 节点样式
     */
    IItemStyle getItemStyle(ItemType itemType);

    /**
     * 获取菜单扩展接口
     *
     * @param itemType 节点类型
     * @return 节点菜单扩展
     */
    IMenuExtender getMenuExtender(ItemType itemType);

    /**
     * 对 Map 设置 MapControl
     *
     * @param map        Map 节点
     * @param mapControl 对应的 MapControl
     */
    void setMapControl(Map map, MapControl mapControl);

    /**
     * 根据 Map 获取 MapControl
     *
     * @param map Map 节点
     * @return 对应的 MapControl
     */
    MapControl getMapControl(Map map);

    /**
     * 对 Scene 设置 SceneControl
     *
     * @param scene        Scene 节点
     * @param sceneControl 对应的 SceneControl
     */
    void setSceneControl(Scene scene, SceneControl sceneControl);

    /**
     * 根据 Scene 获取 SceneControl
     *
     * @param scene Scene 节点
     * @return 对应的 SceneControl
     */
    SceneControl getSceneControl(Scene scene);

    // TODO: 2019/09/12 暂用 Object 代替 LayoutControl

    /**
     * 对 doc 设置 LayoutControl
     *
     * @param doc           doc 节点
     * @param layoutControl 对应的 LayoutControl
     */
    void setLayoutControl(Document doc, Object layoutControl);

    // TODO: 2019/09/12 暂用 Object 代替 LayoutControl

    /**
     * 根据 doc 获取 LayoutControl
     *
     * @param doc doc 节点
     * @return 对应的 LayoutControl
     */
    Object getLayoutControl(Document doc);

    //endregion

    //region 设置菜单项属性

    /**
     * 设置右键菜单是否可用
     *
     * @param item   菜单项
     * @param enable 是否可用
     * @return 返回菜单项最终是否可用
     */
    boolean setMenuItemEnable(IMenuItem item, boolean enable);

    /**
     * 设置右键菜单是否可见
     *
     * @param item    菜单项
     * @param visible 是否可见
     * @return 返回菜单项最终是否可见
     */
    boolean setMenuItemVisible(IMenuItem item, boolean visible);

    /**
     * 设置右键菜单是否选中
     *
     * @param item    菜单项
     * @param checked 是否选中
     * @return 返回菜单项最终是否选中
     */
    boolean setMenuItemChecked(IMenuItem item, boolean checked);

    /**
     * 设置右键菜单标题
     *
     * @param item    菜单项
     * @param caption 标题
     * @return 返回菜单项最终标题
     */
    String setMenuItemCaption(IMenuItem item, String caption);

    /**
     * 设置右键菜单图标
     *
     * @param item  菜单项
     * @param image 图标
     * @return 返回菜单项最终图标
     */
    Image setMenuItemImage(IMenuItem item, Image image);

    /**
     * 设置右键菜单项的 BeginGroup 属性
     *
     * @param item       菜单项
     * @param beginGroup 是否开始新的组
     * @return 返回菜单项的 BeginGroup 属性
     */
    boolean setMenuItemBeginGroup(IMenuItem item, boolean beginGroup);

    //endregion

    //region 事件

    /**
     * 添加单击节点事件监听器
     *
     * @param clickNodeListener 单击节点事件监听器
     */
    public void addClickNodeListener(ClickNodeListener clickNodeListener);

    /**
     * 删除单击节点事件监听器
     *
     * @param clickNodeListener 单击节点事件监听器
     */
    public void removeClickNodeListener(ClickNodeListener clickNodeListener);

    /**
     * 触发单击节点事件
     *
     * @param clickNodeEvent 单击节点事件
     */
    public void fireClickNode(ClickNodeEvent clickNodeEvent);

    /**
     * 添加双击节点事件监听器
     *
     * @param clickNodeListener 双击节点事件监听器
     */
    public void addDoubleClickNodeListener(ClickNodeListener clickNodeListener);

    /**
     * 删除双击节点事件监听器
     *
     * @param clickNodeListener 双击节点事件监听器
     */
    public void removeDoubleClickNodeListener(ClickNodeListener clickNodeListener);

    /**
     * 触发双击节点事件
     *
     * @param clickNodeEvent 双击节点事件
     */
    public void fireDoubleClickNode(ClickNodeEvent clickNodeEvent);

    /**
     * 添加菜单项 Click 事件监听器
     *
     * @param menuItemClickListener 菜单项 Click 事件监听器
     */
    public void addMenuItemClickListener(MenuItemClickListener menuItemClickListener);

    /**
     * 删除菜单项 Click 事件监听器
     *
     * @param menuItemClickListener 菜单项 Click 事件监听器
     */
    public void removeMenuItemClickListener(MenuItemClickListener menuItemClickListener);

    /**
     * 触发菜单项 Click 事件(通常由用户在 ISingleMenuItem 的 onClick 中调用)
     *
     * @param menuItemClickEvent 菜单项 Click 事件
     */
    public void fireMenuItemClick(MenuItemClickEvent menuItemClickEvent);

    /**
     * 添加多菜单项 Click 事件监听器
     *
     * @param multiMenuItemClickListener 多菜单项 Click 事件监听器
     */
    public void addMultiMenuItemClickListener(MultiMenuItemClickListener multiMenuItemClickListener);

    /**
     * 删除多菜单项 Click 事件监听器
     *
     * @param multiMenuItemClickListener 多菜单项 Click 事件监听器
     */
    public void removeMultiMenuItemClickListener(MultiMenuItemClickListener multiMenuItemClickListener);

    /**
     * 触发多菜单项 Click 事件(通常由用户在 IMultiMenuItem 的 onClick 中调用)
     *
     * @param multiMenuItemClickEvent 多菜单项 Click 事件
     */
    public void fireMultiMenuItemClick(MultiMenuItemClickEvent multiMenuItemClickEvent);

    //endregion
}
