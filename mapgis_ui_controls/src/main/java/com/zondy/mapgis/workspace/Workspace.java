package com.zondy.mapgis.workspace;

import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.controls.SceneControl;
import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.map.Map;
import com.zondy.mapgis.map.Document;
import com.zondy.mapgis.scene.Scene;
import com.zondy.mapgis.utilities.PackageUtility;
import com.zondy.mapgis.workspace.engine.*;
import com.zondy.mapgis.workspace.enums.ItemType;
import com.zondy.mapgis.workspace.event.*;
import com.zondy.mapgis.workspace.itemstyle.*;
import javafx.scene.image.Image;

import java.util.*;

/**
 * 工作空间引擎
 *
 * @author cxy
 * @date 2019/09/18
 */
public class Workspace implements IWorkspace {
    private HashMap<ItemType, IItemStyle> itemStyles;           // 节点样式
    private HashMap<ItemType, IMenuExtender> menuExtenders;     // 菜单扩展接口
    private HashMap<Long, MapControl> mapControls;                  // Map.Handle 映射 MapControl
    private HashMap<Long, SceneControl> sceneControls;                // Scene.Handle 映射 SceneControl

    /**
     * 工作空间引擎
     */
    public Workspace() {
        this.itemStyles = new HashMap<>();
        this.menuExtenders = new HashMap<>();
        this.mapControls = new HashMap<>();
        this.sceneControls = new HashMap<>();
    }

    // region 载入工作空间配置

    /**
     * 根据 jar包部署工作空间
     *
     * @param jarPath jar包路径
     */
    public void loadCustomWorkSpace(String jarPath) {
        try {
            Set<Class<?>> types = PackageUtility.getClasses(jarPath);
            //PackageUtility.findAndAddClassesInPackageByFile();
            for (Class<?> type : types) {
                if (IItemStyle.class.isAssignableFrom(type)) {
                    IItemStyle nodeStyle = (IItemStyle) (type.newInstance());
                    itemStyles.remove(nodeStyle.getItemType());
                    itemStyles.put(nodeStyle.getItemType(), nodeStyle);
                    this.createMenuItem(nodeStyle.getPopMenu());
                }
            }

        } catch (Exception ignored) {
        }
    }

    /**
     * 部署默认工作空间
     */
    @Override
    public void loadCustomWorkSpace() {
        this.itemStyles.clear();
        // BlankArea
        BlankAreaItemStyle blankAreaItemStyle = new BlankAreaItemStyle();
        this.itemStyles.put(ItemType.BLANKAREA, blankAreaItemStyle);
        this.createMenuItem(blankAreaItemStyle.getPopMenu());
        // Document
        DocumentItemStyle documentItemStyle = new DocumentItemStyle();
        this.itemStyles.put(ItemType.DOCUMENT, documentItemStyle);
        this.createMenuItem(documentItemStyle.getPopMenu());
        // Group3DLayer
        Group3DLayerItemStyle group3DLayerItemStyle = new Group3DLayerItemStyle();
        this.itemStyles.put(ItemType.GROUP3DLAYER, group3DLayerItemStyle);
        this.createMenuItem(group3DLayerItemStyle.getPopMenu());
        // GroupLayer
        GroupLayerItemStyle groupLayerItemStyle = new GroupLayerItemStyle();
        this.itemStyles.put(ItemType.GROUPLAYER, groupLayerItemStyle);
        this.createMenuItem(groupLayerItemStyle.getPopMenu());
        //LabelLayer
        LabelLayerItemStyle labelLayerItemStyle = new LabelLayerItemStyle();
        this.itemStyles.put(ItemType.LABELLAYER, labelLayerItemStyle);
        this.createMenuItem(labelLayerItemStyle.getPopMenu());
        // Map
        MapItemStyle mapItemStyle = new MapItemStyle();
        this.itemStyles.put(ItemType.MAP, mapItemStyle);
        this.createMenuItem(mapItemStyle.getPopMenu());
        // ModelCacheLayer
        ModelCacheLayerItemStyle modelCacheLayerItemStyle = new ModelCacheLayerItemStyle();
        this.itemStyles.put(ItemType.MODELCACHELAYER, modelCacheLayerItemStyle);
        this.createMenuItem(modelCacheLayerItemStyle.getPopMenu());
        // ModelLayer
        ModelLayerItemStyle modelLayerItemStyle = new ModelLayerItemStyle();
        this.itemStyles.put(ItemType.MODELLAYER, modelLayerItemStyle);
        this.createMenuItem(modelLayerItemStyle.getPopMenu());
        // MosaicLayer
        MosaicLayerItemStyle mosaicLayerItemStyle = new MosaicLayerItemStyle();
        this.itemStyles.put(ItemType.MOSAICLAYER, mosaicLayerItemStyle);
        this.createMenuItem(mosaicLayerItemStyle.getPopMenu());
        // RasterLayer
        RasterLayerItemStyle rasterLayerItemStyle = new RasterLayerItemStyle();
        this.itemStyles.put(ItemType.RASTERLAYER, rasterLayerItemStyle);
        this.createMenuItem(rasterLayerItemStyle.getPopMenu());
        // RasterCatalogLayer
        RasterCatalogLayerItemStyle rasterCatalogLayerItemStyle = new RasterCatalogLayerItemStyle();
        this.itemStyles.put(ItemType.RASTERCATALOGLAYER, rasterCatalogLayerItemStyle);
        this.createMenuItem(rasterCatalogLayerItemStyle.getPopMenu());
        // Scene
        SceneItemStyle sceneItemStyle = new SceneItemStyle();
        this.itemStyles.put(ItemType.SCENE, sceneItemStyle);
        this.createMenuItem(sceneItemStyle.getPopMenu());
        // TerrainLayer
        TerrainLayerItemStyle terrainLayerItemStyle = new TerrainLayerItemStyle();
        this.itemStyles.put(ItemType.TERRAINLAYER, terrainLayerItemStyle);
        this.createMenuItem(terrainLayerItemStyle.getPopMenu());
        // Vector3DLayer
        Vector3DLayerItemStyle vector3DLayerItemStyle = new Vector3DLayerItemStyle();
        this.itemStyles.put(ItemType.VECTOR3DLAYER, vector3DLayerItemStyle);
        this.createMenuItem(vector3DLayerItemStyle.getPopMenu());
        // VectorLayer
        VectorLayerItemStyle vectorLayerItemStyle = new VectorLayerItemStyle();
        this.itemStyles.put(ItemType.VECTORLAYER, vectorLayerItemStyle);
        this.createMenuItem(vectorLayerItemStyle.getPopMenu());
        // FileLayer6x
        FileLayer6xItemStyle file6xItemStyle = new FileLayer6xItemStyle();
        this.itemStyles.put(ItemType.FILELAYER6X, file6xItemStyle);
        this.createMenuItem(file6xItemStyle.getPopMenu());
        // ObjectLayer
        ObjectLayerItemStyle objectLayerItemStyle = new ObjectLayerItemStyle();
        this.itemStyles.put(ItemType.OBJECTLAYER, objectLayerItemStyle);
        this.createMenuItem(objectLayerItemStyle.getPopMenu());
        // MultiMap
        MultiMapItemStyle multiMapItemStyle = new MultiMapItemStyle();
        this.itemStyles.put(ItemType.MULTIMAP, multiMapItemStyle);
        this.createMenuItem(multiMapItemStyle.getPopMenu());
        // MultiLayer
        MultiLayerItemStyle multiLayerItemStyle = new MultiLayerItemStyle();
        this.itemStyles.put(ItemType.MULTILAYER, multiLayerItemStyle);
        this.createMenuItem(multiLayerItemStyle.getPopMenu());
        // MultiScene
        MultiSceneItemStyle multiSceneItemStyle = new MultiSceneItemStyle();
        this.itemStyles.put(ItemType.MULTISCENE, multiSceneItemStyle);
        this.createMenuItem(multiSceneItemStyle.getPopMenu());
        // Multi3DLayer
        Multi3DLayerItemStyle multi3DLayerItemStyle = new Multi3DLayerItemStyle();
        this.itemStyles.put(ItemType.MULTI3DLAYER, multi3DLayerItemStyle);
        this.createMenuItem(multi3DLayerItemStyle.getPopMenu());
    }

    // endregion

    // region 触发 UpdateTreeEvent

    /**
     * 开始更新树界面
     */
    @Override
    public void beginUpdateTree() {

    }

    /**
     * 结束更新树界面
     */
    @Override
    public void endUpdateTree() {

    }

    // endregion

    // region 触发 ClickMenuItemEvent

    public void onClickNode(DocumentItem documentItem) {

    }

    public void onDoubleClickNode(DocumentItem documentItem) {

    }

    // endregion

    // region 设置获取节点类型属性

    /**
     * 获取节点样式
     *
     * @param itemType 节点类型
     * @return 节点样式
     */
    @Override
    public IItemStyle getItemStyle(ItemType itemType) {
        if (itemType == null) {
            return null;
        }
        return this.itemStyles.getOrDefault(itemType, null);
    }

    /**
     * 获取菜单扩展接口
     *
     * @param itemType 节点类型
     * @return 节点菜单扩展
     */
    @Override
    public IMenuExtender getMenuExtender(ItemType itemType) {
        IMenuExtender menuExtender = null;
        if (itemType != null) {
            menuExtender = this.menuExtenders.get(itemType);
        }
        if (menuExtender == null) {
            menuExtender = new MenuExtender(itemType, this);
            this.menuExtenders.put(itemType, menuExtender);
        }
        return menuExtender;
    }

    // TODO: 待 MapControl 添加后修改参数类型

    /**
     * 对 Map 设置 MapControl
     *
     * @param map        Map 节点
     * @param mapControl 对应的 MapControl
     */
    @Override
    public void setMapControl(Map map, MapControl mapControl) {
        if (map != null) {
            // TODO: MapAttachMapControlEvent 事件待添加
//            if (this.MapAttachMapControlEvent != null) {
//                MapAttachMapControlEventArgs args = new MapAttachMapControlEventArgs(map, mapControl);
//                this.MapAttachMapControlEvent(this, args);
//            }
            this.mapControls.remove(map.getHandle());
            if (mapControl != null) {
                this.mapControls.put(map.getHandle(), mapControl);
            }
        } else if (mapControl != null) {
            // 清理所有 mapControl 的值
            for (java.util.Map.Entry<Long, MapControl> entry : this.mapControls.entrySet()) {
                // TODO: 待 MapControl 添加后取消注释
                if (entry.getValue() != null /*&& entry.getValue().getHandle() == mapControl.getHandle()*/) {
//                    if (this.MapAttachMapControlEvent != null) {
//                        MapAttachMapControlEventArgs args = new MapAttachMapControlEventArgs(mapControl.ActiveMap, mapControl);
//                        this.MapAttachMapControlEvent(this, args);
//                    }
                    this.mapControls.remove(entry.getKey());
                }
            }
        }
    }

    /**
     * 根据 Map 获取 MapControl
     *
     * @param map Map 节点
     * @return 对应的 MapControl
     */
    @Override
    public MapControl getMapControl(Map map) {
        if (map == null) {
            return null;
        }
        return this.mapControls.getOrDefault(map.getHandle(), null);
    }

    /**
     * 清除所有 Map 和 MapControl 映射关系
     */
    public void clearMapControl() {
        this.mapControls.clear();
    }

    /**
     * 对 Scene 设置 SceneControl
     *
     * @param scene        Scene 节点
     * @param sceneControl 对应的 SceneControl
     */
    @Override
    public void setSceneControl(Scene scene, SceneControl sceneControl) {
        if (scene != null) {
            this.sceneControls.remove(scene.getHandle());
            if (sceneControl != null) {
                this.sceneControls.put(scene.getHandle(), sceneControl);
            }
        } else if (sceneControl != null) {
            // 清理所有 sceneControl 的值
            for (java.util.Map.Entry<Long, SceneControl> entry : this.sceneControls.entrySet()) {
                if (entry.getValue() != null /*&& entry.getValue() == sceneControl.getHandle()*/) {
                    this.sceneControls.remove(entry.getKey());
                }
            }
        }
    }

    /**
     * 根据 Scene 获取 SceneControl
     *
     * @param scene Scene 节点
     * @return 对应的 SceneControl
     */
    @Override
    public SceneControl getSceneControl(Scene scene) {
        if (scene == null) {
            return null;
        }
        return this.sceneControls.getOrDefault(scene.getHandle(), null);
    }

    /**
     * 对 doc 设置 LayoutControl
     *
     * @param doc           doc 节点
     * @param layoutControl 对应的 LayoutControl
     */
    @Override
    public void setLayoutControl(Document doc, Object layoutControl) {
//        if (doc != null) {
//            this.layoutControls.remove(doc.getHandle());
//        }
//        if (layoutControl != null) {
//            this.layoutControls.put(doc.getHandle(), layoutControl);
//        }
    }

    /**
     * 根据 doc 获取 LayoutControl
     *
     * @param doc doc 节点
     * @return 对应的 LayoutControl
     */
    @Override
    public Object getLayoutControl(Document doc) {
//        if (doc == null) {
//            return null;
//        }
//        return this.layoutControls.getOrDefault(doc.getHandle(), null);
        return null;
    }

    // endregion

    // region 设置菜单项属性

    /**
     * 设置右键菜单是否可用
     *
     * @param item   菜单项
     * @param enable 是否可用
     * @return 返回菜单项最终是否可用
     */
    @Override
    public boolean setMenuItemEnable(IMenuItem item, boolean enable) {
        if (item == null || this.setMenuItemEnableListeners == null) {
            return false;
        }
        return this.fireSetMenuItemEnable(new SetMenuItemEnableEvent(this, item, enable));
    }

    /**
     * 设置右键菜单是否可见
     *
     * @param item    菜单项
     * @param visible 是否可见
     * @return 返回菜单项最终是否可见
     */
    @Override
    public boolean setMenuItemVisible(IMenuItem item, boolean visible) {
        if (item == null || this.setMenuItemVisibleListeners == null) {
            return false;
        }
        return this.fireSetMenuItemVisible(new SetMenuItemVisibleEvent(this, item, visible));
    }

    /**
     * 设置右键菜单是否选中
     *
     * @param item    菜单项
     * @param checked 是否选中
     * @return 返回菜单项最终是否选中
     */
    @Override
    public boolean setMenuItemChecked(IMenuItem item, boolean checked) {
        if (item == null || this.setMenuItemCheckedListeners == null) {
            return false;
        }
        return this.fireSetMenuItemChecked(new SetMenuItemCheckedEvent(this, item, checked));
    }

    /**
     * 设置右键菜单标题
     *
     * @param item    菜单项
     * @param caption 标题
     * @return 返回菜单项最终标题
     */
    @Override
    public String setMenuItemCaption(IMenuItem item, String caption) {
        if (item == null || this.setMenuItemCaptionListeners == null) {
            return "";
        }
        return this.fireSetMenuItemCaption(new SetMenuItemCaptionEvent(this, item, caption));
    }

    /**
     * 设置右键菜单图标
     *
     * @param item  菜单项
     * @param image 图标
     * @return 返回菜单项最终图标
     */
    @Override
    public Image setMenuItemImage(IMenuItem item, Image image) {
        if (item == null || this.setMenuItemImageListeners == null) {
            return null;
        }
        return this.fireSetMenuItemImage(new SetMenuItemImageEvent(this, item, image));
    }

    /**
     * 设置右键菜单项的 BeginGroup 属性
     *
     * @param item       菜单项
     * @param beginGroup 是否开始新的组
     * @return 返回菜单项的 BeginGroup 属性
     */
    @Override
    public boolean setMenuItemBeginGroup(IMenuItem item, boolean beginGroup) {
        if (item == null || this.setMenuItemBeginGroupListeners == null) {
            return false;
        }
        return this.fireSetMenuItemBeginGroup(new SetMenuItemBeginGroupEvent(this, item, beginGroup));
    }

    // endregion

    // region 公共事件

    private ArrayList<SetMenuItemBeginGroupListener> setMenuItemBeginGroupListeners = new ArrayList<>();

    /**
     * 添加设置菜单项为新组事件监听器
     *
     * @param setMenuItemBeginGroupListener 设置菜单项为新组事件监听器
     */
    public void addSetMenuItemBeginGroupListener(SetMenuItemBeginGroupListener setMenuItemBeginGroupListener) {
        this.setMenuItemBeginGroupListeners.add(setMenuItemBeginGroupListener);
    }

    /**
     * 删除设置菜单项为新组事件监听器
     *
     * @param setMenuItemBeginGroupListener 设置菜单项为新组事件监听器
     */
    public void removeSetMenuItemBeginGroupListener(SetMenuItemBeginGroupListener setMenuItemBeginGroupListener) {
        this.setMenuItemBeginGroupListeners.remove(setMenuItemBeginGroupListener);
    }

    /**
     * 触发设置菜单项为新组事件
     *
     * @param setMenuItemBeginGroupEvent 设置菜单项为新组事件
     * @return 返回菜单项的 BeginGroup 属性
     */
    public boolean fireSetMenuItemBeginGroup(SetMenuItemBeginGroupEvent setMenuItemBeginGroupEvent) {
        boolean rtn = false;
        for (SetMenuItemBeginGroupListener setMenuItemBeginGroupListener : this.setMenuItemBeginGroupListeners) {
            rtn = setMenuItemBeginGroupListener.fireSetMenuItemBeginGroup(setMenuItemBeginGroupEvent);
        }
        return rtn;
    }

    private ArrayList<SetMenuItemImageListener> setMenuItemImageListeners = new ArrayList<>();

    /**
     * 添加设置右键菜单图标事件监听器
     *
     * @param setMenuItemImageListener 设置右键菜单图标事件监听器
     */
    public void addSetMenuItemImageListener(SetMenuItemImageListener setMenuItemImageListener) {
        this.setMenuItemImageListeners.add(setMenuItemImageListener);
    }

    /**
     * 删除设置右键菜单图标事件监听器
     *
     * @param setMenuItemImageListener 设置右键菜单图标事件监听器
     */
    public void removeSetMenuItemImageListener(SetMenuItemImageListener setMenuItemImageListener) {
        this.setMenuItemImageListeners.remove(setMenuItemImageListener);
    }

    /**
     * 触发设置右键菜单图标事件
     *
     * @param setMenuItemImageEvent 设置右键菜单图标事件
     * @return 返回菜单项最终图标
     */
    public Image fireSetMenuItemImage(SetMenuItemImageEvent setMenuItemImageEvent) {
        Image rtn = null;
        for (SetMenuItemImageListener setMenuItemImageListener : this.setMenuItemImageListeners) {
            rtn = setMenuItemImageListener.fireSetMenuItemImage(setMenuItemImageEvent);
        }
        return rtn;
    }

    private ArrayList<SetMenuItemCaptionListener> setMenuItemCaptionListeners = new ArrayList<>();

    /**
     * 添加设置右键菜单标题事件监听器
     *
     * @param setMenuItemCaptionListener 设置右键菜单标题事件监听器
     */
    public void addSetMenuItemCaptionListener(SetMenuItemCaptionListener setMenuItemCaptionListener) {
        this.setMenuItemCaptionListeners.add(setMenuItemCaptionListener);
    }

    /**
     * 删除设置右键菜单标题事件监听器
     *
     * @param setMenuItemCaptionListener 设置右键菜单标题事件监听器
     */
    public void removeSetMenuItemCaptionListener(SetMenuItemCaptionListener setMenuItemCaptionListener) {
        this.setMenuItemCaptionListeners.remove(setMenuItemCaptionListener);
    }

    /**
     * 触发设置右键菜单标题事件
     *
     * @param setMenuItemCaptionEvent 设置右键菜单标题事件
     * @return 返回菜单项最终标题
     */
    public String fireSetMenuItemCaption(SetMenuItemCaptionEvent setMenuItemCaptionEvent) {
        String rtn = null;
        for (SetMenuItemCaptionListener setMenuItemCaptionListener : this.setMenuItemCaptionListeners) {
            rtn = setMenuItemCaptionListener.fireSetMenuItemCaption(setMenuItemCaptionEvent);
        }
        return rtn;
    }

    private ArrayList<SetMenuItemCheckedListener> setMenuItemCheckedListeners = new ArrayList<>();

    /**
     * 添加设置右键菜单是否选中事件监听器
     *
     * @param setMenuItemCheckedListener 设置右键菜单是否选中事件监听器
     */
    public void addSetMenuItemCheckedListener(SetMenuItemCheckedListener setMenuItemCheckedListener) {
        this.setMenuItemCheckedListeners.add(setMenuItemCheckedListener);
    }

    /**
     * 删除设置右键菜单是否选中事件监听器
     *
     * @param setMenuItemCheckedListener 设置右键菜单是否选中事件监听器
     */
    public void removeSetMenuItemCheckedListener(SetMenuItemCheckedListener setMenuItemCheckedListener) {
        this.setMenuItemCheckedListeners.remove(setMenuItemCheckedListener);
    }

    /**
     * 触发设置右键菜单是否选中事件
     *
     * @param setMenuItemCheckedEvent 设置右键菜单是否选中事件
     * @return 返回菜单项最终是否选中
     */
    public boolean fireSetMenuItemChecked(SetMenuItemCheckedEvent setMenuItemCheckedEvent) {
        boolean rtn = false;
        for (SetMenuItemCheckedListener setMenuItemCheckedListener : this.setMenuItemCheckedListeners) {
            rtn = setMenuItemCheckedListener.fireSetMenuItemChecked(setMenuItemCheckedEvent);
        }
        return rtn;
    }

    private ArrayList<SetMenuItemEnableListener> setMenuItemEnableListeners = new ArrayList<>();

    /**
     * 添加设置右键菜单是否可用事件监听器
     *
     * @param setMenuItemEnableListener 设置右键菜单是否可用事件监听器
     */
    public void addSetMenuItemEnableListener(SetMenuItemEnableListener setMenuItemEnableListener) {
        this.setMenuItemEnableListeners.add(setMenuItemEnableListener);
    }

    /**
     * 删除设置右键菜单是否可用事件监听器
     *
     * @param setMenuItemEnableListener 设置右键菜单是否可用事件监听器
     */
    public void removeSetMenuItemEnableListener(SetMenuItemEnableListener setMenuItemEnableListener) {
        this.setMenuItemEnableListeners.remove(setMenuItemEnableListener);
    }

    /**
     * 触发设置右键菜单是否可用事件
     *
     * @param setMenuItemEnableEvent 设置右键菜单是否可用事件
     * @return 返回菜单项最终是否可用
     */
    public boolean fireSetMenuItemEnable(SetMenuItemEnableEvent setMenuItemEnableEvent) {
        boolean rtn = false;
        for (SetMenuItemEnableListener setMenuItemEnableListener : this.setMenuItemEnableListeners) {
            rtn = setMenuItemEnableListener.fireSetMenuItemEnable(setMenuItemEnableEvent);
        }
        return rtn;
    }

    private ArrayList<SetMenuItemVisibleListener> setMenuItemVisibleListeners = new ArrayList<>();

    /**
     * 添加设置右键菜单是否可见事件监听器
     *
     * @param setMenuItemVisibleListener 设置右键菜单是否可见事件监听器
     */
    public void addSetMenuItemVisibleListener(SetMenuItemVisibleListener setMenuItemVisibleListener) {
        this.setMenuItemVisibleListeners.add(setMenuItemVisibleListener);
    }

    /**
     * 删除设置右键菜单是否可见事件监听器
     *
     * @param setMenuItemVisibleListener 设置右键菜单是否可见事件监听器
     */
    public void removeSetMenuItemVisibleListener(SetMenuItemVisibleListener setMenuItemVisibleListener) {
        this.setMenuItemVisibleListeners.remove(setMenuItemVisibleListener);
    }

    /**
     * 触发设置右键菜单是否可见事件
     *
     * @param setMenuItemVisibleEvent 设置右键菜单是否可见事件
     * @return 返回菜单项最终是否可见
     */
    public boolean fireSetMenuItemVisible(SetMenuItemVisibleEvent setMenuItemVisibleEvent) {
        boolean rtn = false;
        for (SetMenuItemVisibleListener setMenuItemVisibleListener : this.setMenuItemVisibleListeners) {
            rtn = setMenuItemVisibleListener.fireSetMenuItemVisible(setMenuItemVisibleEvent);
        }
        return rtn;
    }

    private ArrayList<ClickNodeListener> clickNodeListeners = new ArrayList<>();

    /**
     * 添加单击节点事件监听器
     *
     * @param clickNodeListener 单击节点事件监听器
     */
    @Override
    public void addClickNodeListener(ClickNodeListener clickNodeListener) {
        this.clickNodeListeners.add(clickNodeListener);
    }

    /**
     * 删除单击节点事件监听器
     *
     * @param clickNodeListener 单击节点事件监听器
     */
    @Override
    public void removeClickNodeListener(ClickNodeListener clickNodeListener) {
        this.clickNodeListeners.remove(clickNodeListener);
    }

    /**
     * 触发单击节点事件
     *
     * @param clickNodeEvent 单击节点事件
     */
    @Override
    public void fireClickNode(ClickNodeEvent clickNodeEvent) {
        for (ClickNodeListener clickNodeListener : this.clickNodeListeners) {
            clickNodeListener.fireClickNode(clickNodeEvent);
        }
    }

    private ArrayList<ClickNodeListener> doubleClickNodeListeners = new ArrayList<>();

    /**
     * 添加双击节点事件监听器
     *
     * @param doubleClickNodeListener 双击节点事件监听器
     */
    @Override
    public void addDoubleClickNodeListener(ClickNodeListener doubleClickNodeListener) {
        this.doubleClickNodeListeners.add(doubleClickNodeListener);
    }

    /**
     * 删除双击节点事件监听器
     *
     * @param doubleClickNodeListener 双击节点事件监听器
     */
    @Override
    public void removeDoubleClickNodeListener(ClickNodeListener doubleClickNodeListener) {
        this.doubleClickNodeListeners.remove(doubleClickNodeListener);
    }

    /**
     * 触发双击节点事件
     *
     * @param doubleClickNodeEvent 双击节点事件
     */
    @Override
    public void fireDoubleClickNode(ClickNodeEvent doubleClickNodeEvent) {
        for (ClickNodeListener doubleClickNodeListener : this.doubleClickNodeListeners) {
            doubleClickNodeListener.fireClickNode(doubleClickNodeEvent);
        }
    }

    private ArrayList<MenuItemClickListener> menuItemClickListeners = new ArrayList<>();

    /**
     * 添加菜单项 Click 事件监听器
     *
     * @param menuItemClickListener 菜单项 Click 事件监听器
     */
    @Override
    public void addMenuItemClickListener(MenuItemClickListener menuItemClickListener) {
        this.menuItemClickListeners.add(menuItemClickListener);
    }

    /**
     * 删除菜单项 Click 事件监听器
     *
     * @param menuItemClickListener 菜单项 Click 事件监听器
     */
    @Override
    public void removeMenuItemClickListener(MenuItemClickListener menuItemClickListener) {
        this.menuItemClickListeners.remove(menuItemClickListener);
    }

    /**
     * 触发菜单项 Click 事件(通常由用户在 ISingleMenuItem 的 onClick 中调用)
     *
     * @param menuItemClickEvent 菜单项 Click 事件
     */
    @Override
    public void fireMenuItemClick(MenuItemClickEvent menuItemClickEvent) {
        for (MenuItemClickListener menuItemClickListener : this.menuItemClickListeners) {
            menuItemClickListener.fireMenuItemClick(menuItemClickEvent);
        }
    }

    private ArrayList<MultiMenuItemClickListener> multiMenuItemClickListeners = new ArrayList<>();

    /**
     * 添加多菜单项 Click 事件监听器
     *
     * @param multiMenuItemClickListener 多菜单项 Click 事件监听器
     */
    @Override
    public void addMultiMenuItemClickListener(MultiMenuItemClickListener multiMenuItemClickListener) {
        this.multiMenuItemClickListeners.add(multiMenuItemClickListener);
    }

    /**
     * 删除多菜单项 Click 事件监听器
     *
     * @param multiMenuItemClickListener 多菜单项 Click 事件监听器
     */
    @Override
    public void removeMultiMenuItemClickListener(MultiMenuItemClickListener multiMenuItemClickListener) {
        this.multiMenuItemClickListeners.remove(multiMenuItemClickListener);
    }

    /**
     * 触发多菜单项 Click 事件(通常由用户在 IMultiMenuItem 的 onClick 中调用)
     *
     * @param multiMenuItemClickEvent 多菜单项 Click 事件
     */
    @Override
    public void fireMultiMenuItemClick(MultiMenuItemClickEvent multiMenuItemClickEvent) {
        for (MultiMenuItemClickListener multiMenuItemClickListener : this.multiMenuItemClickListeners) {
            multiMenuItemClickListener.fireMultiMenuItemClick(multiMenuItemClickEvent);
        }
    }

    private ArrayList<UpdateTreeListener> beginUpdateTreeListeners = new ArrayList<>();

    /**
     * 添加开始更新树事件监听器
     *
     * @param beginUpdateTreeListener 开始更新树事件监听器
     */
    public void addBeginUpdateTreeListener(UpdateTreeListener beginUpdateTreeListener) {
        this.beginUpdateTreeListeners.add(beginUpdateTreeListener);
    }

    /**
     * 移除开始更新树事件监听器
     *
     * @param beginUpdateTreeListener 开始更新树事件监听器
     */
    public void removeBeginUpdateTreeListener(UpdateTreeListener beginUpdateTreeListener) {
        this.beginUpdateTreeListeners.remove(beginUpdateTreeListener);
    }

    /**
     * 触发开始更新树事件
     *
     * @param beginUpdateTreeEvent 开始更新树事件
     */
    public void fireBeginUpdateTree(UpdateTreeEvent beginUpdateTreeEvent) {
        for (UpdateTreeListener beginUpdateTreeListener : this.beginUpdateTreeListeners) {
            beginUpdateTreeListener.fireUpdateTree(beginUpdateTreeEvent);
        }
    }

    private ArrayList<UpdateTreeListener> endUpdateTreeListeners = new ArrayList<>();

    /**
     * 添加结束更新树事件监听器
     *
     * @param endUpdateTreeListener 结束更新树事件监听器
     */
    public void addEndUpdateTreeListener(UpdateTreeListener endUpdateTreeListener) {
        this.endUpdateTreeListeners.add(endUpdateTreeListener);
    }

    /**
     * 移除结束更新树事件监听器
     *
     * @param endUpdateTreeListener 结束更新树事件监听器
     */
    public void removeEndUpdateTreeListener(UpdateTreeListener endUpdateTreeListener) {
        this.endUpdateTreeListeners.remove(endUpdateTreeListener);
    }

    /**
     * 触发结束更新树事件
     *
     * @param endUpdateTreeEvent 结束更新树事件
     */
    public void fireEndUpdateTree(UpdateTreeEvent endUpdateTreeEvent) {
        for (UpdateTreeListener endUpdateTreeListener : this.endUpdateTreeListeners) {
            endUpdateTreeListener.fireUpdateTree(endUpdateTreeEvent);
        }
    }

    // endregion

    // region 内部私有方法

    /**
     * 调用创建菜单项
     *
     * @param popMenu 菜单项
     */
    private void createMenuItem(IPopMenu popMenu) {
        try {
            if (popMenu != null) {
                popMenu.onCreate(this);
                IMenuItem[] items = popMenu.getItems();
                if (items != null) {
                    for (IMenuItem item : items) {
                        this.recurCreateMenuItem(item);
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * 递归调用创建菜单项
     *
     * @param menuItem 菜单项
     */
    private void recurCreateMenuItem(IMenuItem menuItem) {
        try {
            menuItem.onCreate(this);
            if (menuItem instanceof ISubMenu) {
                ISubMenu subMenuItem = (ISubMenu) menuItem;
                IMenuItem[] subMenuItems = subMenuItem.getItems();
                if (subMenuItems != null) {
                    for (IMenuItem it : subMenuItems) {
                        this.recurCreateMenuItem(it);
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    // endregion
}
