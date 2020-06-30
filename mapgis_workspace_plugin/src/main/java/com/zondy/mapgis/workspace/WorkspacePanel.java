package com.zondy.mapgis.workspace;

import com.zondy.mapgis.base.XPath;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.controls.SceneControl;
import com.zondy.mapgis.geometry.Rect;
import com.zondy.mapgis.map.*;
import com.zondy.mapgis.map.event.*;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.IContentsView;
import com.zondy.mapgis.pluginengine.plugin.IDockWindow;
import com.zondy.mapgis.pluginengine.plugin.IMapContentsView;
import com.zondy.mapgis.pluginengine.plugin.ISceneContentsView;
import com.zondy.mapgis.scene.Scene;
import com.zondy.mapgis.scene.SceneMode;
import com.zondy.mapgis.scene.Scenes;
import com.zondy.mapgis.workspace.control.PropertyView;
import com.zondy.mapgis.workspace.event.*;
import com.zondy.mapgis.workspace.event.LayerPropertyChangedEvent;
import com.zondy.mapgis.workspace.event.RemoveLayerEvent;
import com.zondy.mapgis.workspace.menuitem.DocumentItemPropertyMenuItem;
import com.zondy.mapgis.workspace.menuitem.NewDocumentMenuItem;
import com.zondy.mapgis.workspace.menuitem.PreviewMapMenuItem;
import com.zondy.mapgis.workspace.menuitem.PreviewSceneMenuItem;
import com.zondy.mapgis.workspace.plugin.MapView;
import com.zondy.mapgis.workspace.plugin.SceneView;
import com.zondy.mapgis.workspace.plugin.dockwindow.DocItemPropertyDW;
import com.zondy.mapgis.workspace.plugin.dockwindow.PropertyViewDW;
import com.zondy.mapgis.workspace.plugin.menuitem.LookRecords;
import javafx.geometry.Rectangle2D;

import java.io.File;
import java.util.ArrayList;

/**
 * 工作空间面板
 *
 * @author cxy
 * @date 2019/09/18
 */
public class WorkspacePanel {
    private static WorkspacePanel workspacePanel;

    private IApplication application;
    private WorkspaceTree workspaceTree;

    /**
     * 工作空间面板构造
     *
     * @param application 应用程序
     */
    private WorkspacePanel(IApplication application) {
        this.application = application;
        workspaceTree = new WorkspaceTree(this.application.getWorkSpace(), this.application.getDocument());
        workspaceTree.addItemMouseClickListener(itemMouseClickListener);
        workspaceTree.addFocusedNodeChangedListener(focusedNodeChangedListener);
        workspaceTree.addItemMouseDoubleClickListener(itemMouseDoubleClickListener);
        workspaceTree.addMenuItemClickListener(menuItemClickListener);
        workspaceTree.addStateChangedListener(stateChangedListener);

        //workspaceTree.ExternalNodeImage += new WorkSpaceTree.ExternalNodeImageHandler(tree_ExternalNodeImage);
        //workspaceTree.ExternalStateImages += new WorkSpaceTree.ExternalStateImagesHandler(tree_ExternalStateImages);
        //workspaceTree.CustomNodeState += new WorkSpaceTree.CustomNodeStateHandler(tree_CustomNodeState);
        workspaceTree.getDocument().addCreatedDocumentListener(createdDocumentListener);
        workspaceTree.getDocument().addOpenedDocumentListener(openedDocumentListener);
        workspaceTree.getDocument().addClosingDocumentListener(closingDocumentListener);
        workspaceTree.getDocument().addClosedDocumentListener(closedDocumentListener);
        workspaceTree.getDocument().addSavedDocumentListener(savedDocumentListener);
        workspaceTree.getDocument().addSavedAsDocumentListener(savedAsDocumentListener);
        workspaceTree.getDocument().addPropertyChangedListener(documentPropertyChangedListener);
        workspaceTree.addRemoveMapListener(removeMapListener);
        workspaceTree.addMapRemoveLayerListener(mapRemoveLayerListener);
        workspaceTree.addGroupLayerRemoveLayerListener(groupLayerRemoveLayerListener);
        workspaceTree.addMapPropertyChangedListener(mapPropertyChangedListener);
        //workspaceTree.RemoveScene += new Scenes.RemoveSceneHandle(tree_RemoveScene);
        //workspaceTree.Group3D_RemoveLayer += new Group3DLayer.RemoveLayerHandle(tree_Group3D_RemoveLayer);
        //workspaceTree.Scene_RemoveLayer += new Scene.RemoveLayerHandle(tree_Scene_RemoveLayer);
        //workspaceTree.Scene_PropertyChanged += new Scene.PropertyChangeHandle(tree_Scene_PropertyChanged);
        workspaceTree.addLayerPropertyChangedListener(layerPropertyChangedListener);
        //workspaceTree.G3DLayer_PropertyChanged += new G3DLayer.PropertyChangeHandle(tree_G3DLayer_PropertyChanged);
        //workspaceTree.Group3D_InsertLayer += new Group3DLayer.InsertLayerHandle(tree_Group3D_InsertLayer);
    }

    /**
     * 获取工作空间面板单例
     *
     * @param application 应用程序
     * @return 工作空间面板单例
     */
    public static WorkspacePanel getWorkspacePanel(IApplication application) {
        if (workspacePanel == null || workspacePanel.application == null) {
            workspacePanel = new WorkspacePanel(application);
        }
        return workspacePanel;
    }

    /**
     * 获取工作空间树
     *
     * @return 工作空间树
     */
    public WorkspaceTree getWorkspaceTree() {
        return workspaceTree;
    }

    // region Listeners

    private ArrayList<RemoveLayerListener> removeLayerListeners = new ArrayList<>();

    /**
     * 添加移除图层事件监听器
     *
     * @param removeLayerListener 移除图层事件监听器
     */
    public void addRemoveLayerListener(RemoveLayerListener removeLayerListener) {
        this.removeLayerListeners.add(removeLayerListener);
    }

    /**
     * 移除移除图层事件监听器
     *
     * @param removeLayerListener 移除图层事件监听器
     */
    public void removeRemoveLayerListener(RemoveLayerListener removeLayerListener) {
        this.removeLayerListeners.remove(removeLayerListener);
    }

    /**
     * 触发移除图层事件
     *
     * @param removeLayerEvent 移除图层事件
     */
    public void fireRemoveLayer(RemoveLayerEvent removeLayerEvent) {
        for (RemoveLayerListener removeLayerListener : this.removeLayerListeners) {
            removeLayerListener.fireRemoveLayer(removeLayerEvent);
        }
    }

    private ArrayList<Remove3DLayerListener> remove3DLayerListeners = new ArrayList<>();

    /**
     * 添加移除三维图层事件监听器
     *
     * @param remove3DLayerListener 移除三维图层事件监听器
     */
    public void addRemove3DLayerListener(Remove3DLayerListener remove3DLayerListener) {
        this.remove3DLayerListeners.add(remove3DLayerListener);
    }

    /**
     * 移除移除三维图层事件监听器
     *
     * @param remove3DLayerListener 移除三维图层事件监听器
     */
    public void removeRemove3DLayerListener(Remove3DLayerListener remove3DLayerListener) {
        this.remove3DLayerListeners.remove(remove3DLayerListener);
    }

    /**
     * 触发移除三维图层事件
     *
     * @param remove3DLayerEvent 移除三维图层事件
     */
    public void fireRemove3DLayer(Remove3DLayerEvent remove3DLayerEvent) {
        for (Remove3DLayerListener remove3DLayerListener : this.remove3DLayerListeners) {
            remove3DLayerListener.fireRemove3DLayer(remove3DLayerEvent);
        }
    }

    private ArrayList<LayerPropertyChangedListener> layerPropertyChangedListeners = new ArrayList<>();

    /**
     * 添加图层属性改变事件监听器
     *
     * @param layerPropertyChangedListener 图层属性改变事件监听器
     */
    public void addLayerPropertyChangedListener(LayerPropertyChangedListener layerPropertyChangedListener) {
        this.layerPropertyChangedListeners.add(layerPropertyChangedListener);
    }

    /**
     * 移除图层属性改变事件监听器
     *
     * @param layerPropertyChangedListener 图层属性改变事件监听器
     */
    public void removeLayerPropertyChangedListener(LayerPropertyChangedListener layerPropertyChangedListener) {
        this.layerPropertyChangedListeners.remove(layerPropertyChangedListener);
    }

    /**
     * 触发图层属性改变事件
     *
     * @param layerPropertyChangedEvent 图层属性改变事件
     */
    public void fireLayerPropertyChanged(LayerPropertyChangedEvent layerPropertyChangedEvent) {
        for (LayerPropertyChangedListener layerPropertyChangedListener : this.layerPropertyChangedListeners) {
            layerPropertyChangedListener.fireLayerPropertyChanged(layerPropertyChangedEvent);
        }
    }

    private ArrayList<Layer3DPropertyChangedListener> layer3DPropertyChangedListeners = new ArrayList<>();

    /**
     * 添加三维图层属性改变事件监听器
     *
     * @param layer3DPropertyChangedListener 三维图层属性改变事件监听器
     */
    public void addLayer3DPropertyChangedListener(Layer3DPropertyChangedListener layer3DPropertyChangedListener) {
        this.layer3DPropertyChangedListeners.add(layer3DPropertyChangedListener);
    }

    /**
     * 移除三维图层属性改变事件监听器
     *
     * @param layer3DPropertyChangedListener 三维图层属性改变事件监听器
     */
    public void removeLayer3DPropertyChangedListener(Layer3DPropertyChangedListener layer3DPropertyChangedListener) {
        this.layer3DPropertyChangedListeners.remove(layer3DPropertyChangedListener);
    }

    /**
     * 触发三维图层属性改变事件
     *
     * @param layer3DPropertyChangedEvent 三维图层属性改变事件
     */
    public void fireLayer3DPropertyChanged(Layer3DPropertyChangedEvent layer3DPropertyChangedEvent) {
        for (Layer3DPropertyChangedListener layer3DPropertyChangedListener : this.layer3DPropertyChangedListeners) {
            layer3DPropertyChangedListener.fireLayer3DPropertyChanged(layer3DPropertyChangedEvent);
        }
    }

    private ArrayList<MenuItemClickListener> menuItemClickListeners = new ArrayList<>();

    /**
     * 添加菜单点击事件监听器
     *
     * @param menuItemClickListener 菜单点击事件监听器
     */
    public void addMenuItemClickListener(MenuItemClickListener menuItemClickListener) {
        this.menuItemClickListeners.add(menuItemClickListener);
    }

    /**
     * 移除菜单点击事件监听器
     *
     * @param menuItemClickListener 菜单点击事件监听器
     */
    public void removeMenuItemClickListener(MenuItemClickListener menuItemClickListener) {
        this.menuItemClickListeners.remove(menuItemClickListener);
    }

    /**
     * 统一触发菜单点击事件
     *
     * @param menuItemClickEvent 菜单点击事件
     */
    public void fireMenuItemClick(MenuItemClickEvent menuItemClickEvent) {
        for (MenuItemClickListener menuItemClickListener : this.menuItemClickListeners) {
            menuItemClickListener.fireMenuItemClick(menuItemClickEvent);
        }
    }

    private ArrayList<FocusedNodeChangedListener> focusedNodeChangedListeners = new ArrayList<>();

    /**
     * 添加焦点节点改变事件监听器
     *
     * @param focusedNodeChangedListener 焦点节点改变事件监听器
     */
    public void addFocusedNodeChangedListener(FocusedNodeChangedListener focusedNodeChangedListener) {
        this.focusedNodeChangedListeners.add(focusedNodeChangedListener);
    }

    /**
     * 移除焦点节点改变事件监听器
     *
     * @param focusedNodeChangedListener 焦点节点改变事件监听器
     */
    public void removeFocusedNodeChangedListener(FocusedNodeChangedListener focusedNodeChangedListener) {
        this.focusedNodeChangedListeners.remove(focusedNodeChangedListener);
    }

    /**
     * 统一触发焦点节点改变事件
     *
     * @param focusedNodeChangedEvent 焦点节点改变事件
     */
    public void fireFocusedNodeChanged(FocusedNodeChangedEvent focusedNodeChangedEvent) {
        for (FocusedNodeChangedListener focusedNodeChangedListener : this.focusedNodeChangedListeners) {
            focusedNodeChangedListener.fireFocusedNodeChanged(focusedNodeChangedEvent);
        }
    }

    private ArrayList<ItemMouseClickListener> itemMouseClickListeners = new ArrayList<>();

    /**
     * 添加单击节点事件监听器
     *
     * @param itemMouseClickListener 单击节点事件监听器
     */
    public void addItemMouseClickListener(ItemMouseClickListener itemMouseClickListener) {
        this.itemMouseClickListeners.add(itemMouseClickListener);
    }

    /**
     * 移除单击节点事件监听器
     *
     * @param itemMouseClickListener 单击节点事件监听器
     */
    public void removeItemMouseClickListener(ItemMouseClickListener itemMouseClickListener) {
        this.itemMouseClickListeners.remove(itemMouseClickListener);
    }

    /**
     * 触发单击节点事件
     *
     * @param itemMouseClickEvent 单击节点事件
     */
    protected void fireItemMouseClick(ItemMouseClickEvent itemMouseClickEvent) {
        for (ItemMouseClickListener itemMouseClickListener : this.itemMouseClickListeners) {
            itemMouseClickListener.fireItemMouseClick(itemMouseClickEvent);
        }
    }

    // endregion

    // region Event

    private ItemMouseClickListener itemMouseClickListener = itemMouseClickEvent -> {

    };
    private FocusedNodeChangedListener focusedNodeChangedListener = focusedNodeChangedEvent -> {

    };
    private ItemMouseDoubleClickListener itemMouseDoubleClickListener = itemMouseDoubleClickEvent -> {

    };
    private MenuItemClickListener menuItemClickListener = menuItemClickEvent-> {
            if (application != null && workspaceTree != null) {
                Class<?> type = menuItemClickEvent.getType();
                DocumentItem documentItem = menuItemClickEvent.getDocumentItem();
                if (NewDocumentMenuItem.class.equals(type)) {

                } else if (PreviewMapMenuItem.class.equals(type)) {
                    // region 预览地图
                    if (documentItem instanceof Map) {
                        Map map = (Map) documentItem;
                        String viewKey = String.format("%s$MapView", map.getHandle());
                        IContentsView icv = application.getPluginContainer().getContentsViews().getOrDefault(viewKey, null);
                        if (icv == null) {
                            icv = application.getPluginContainer().createContentsView(MapView.class.getName(), viewKey, map.getName());
                            if (icv instanceof IMapContentsView) {
                                IMapContentsView mv = (IMapContentsView) icv;
                                mv.getMapControl().setMap(map);
                                workspaceTree.getWorkspace().setMapControl(map, mv.getMapControl());
                                Rect rect = map.getViewRange();
                                if (rect != null && (rect.getXMax() > rect.getXMin()) && (rect.getYMax() > rect.getYMin())) {
                                    mv.getMapControl().getTransformation().setDisplayRect(rect);
                                }
                                mv.getMapControl().refreshWnd();
                                this.application.getStateManager().onStateChanged(this);
                            }
                        } else if (icv instanceof IMapContentsView) {
                            IMapContentsView mv = (IMapContentsView) icv;
                            application.getPluginContainer().activeContentsView(mv);
                            Rect rt = map.getEntireRange();
                            Rect rt1 = map.getViewRange();
                            if (isIntersect(rt, rt1)) {
                                if (rt1 != null && (rt1.getXMax() > rt1.getXMin()) && (rt1.getYMax() > rt1.getYMin())) {
                                    mv.getMapControl().getTransformation().setDisplayRect(rt1);
                                }
                            } else {
                                if (rt != null && (rt.getXMax() > rt.getXMin()) && (rt.getYMax() > rt.getYMin())) {
                                    mv.getMapControl().getTransformation().setDisplayRect(rt);
                                }
                            }
                            mv.getMapControl().refreshWnd();
                        }
                    }
                    if (menuItemClickListeners != null) {
                        WorkspacePanel.this.fireMenuItemClick(new MenuItemClickEvent(this, type, documentItem));
                    }
                    // endregion
                } else if (PreviewSceneMenuItem.class.equals(type)) {
                    // region 预览三维地图
                    if (documentItem instanceof Scene) {
                        Scene scene = (Scene) documentItem;
                        // region 输出框
//                        scene.SceneExceptionCBack -= Scene_SceneExceptionCBack;
//                        scene.SceneExceptionCBack += Scene_SceneExceptionCBack;
                        // endregion
                        String viewKey = String.format("%s$SceneView", scene.getHandle());
                        IContentsView icv = this.application.getPluginContainer().getContentsViews().get(viewKey);
                        if (icv == null) {
                            icv = this.application.getPluginContainer().createContentsView(SceneView.class.getName(), viewKey, scene.getName());
                            if (icv instanceof ISceneContentsView) {
                                ISceneContentsView sv = (ISceneContentsView) icv;
                                sv.getSceneControl().setMapGISScene(scene);
                                this.workspaceTree.getWorkspace().setSceneControl(scene, sv.getSceneControl());
                                if (sv.getSceneControl().getSceneMode() == SceneMode.LOCAL) {
                                    sv.getSceneControl().reset();
                                }
                                this.application.getStateManager().fireStateChanged(new StateChangedEvent(this));
                                //TODO: 待添加
//                                    WorkspaceTree.UpdateServer3DLayerInScene(sv.SceneControl, scene);
                            }
                        } else if (icv instanceof ISceneContentsView) {
                            ISceneContentsView sv = (ISceneContentsView) icv;
                            this.application.getPluginContainer().activeContentsView(sv);
                        }
                    }
                    // endregion
                } else if (DocumentItemPropertyMenuItem.class.equals(type)) {
                    // region 属性

                    IDockWindow dockWindow = this.application.getPluginContainer().getDockWindows().getOrDefault(DocItemPropertyDW.class.getName(), null);
                    if (dockWindow == null) {
                        dockWindow = this.application.getPluginContainer().createDockWindow(DocItemPropertyDW.class.getName());
                    }
                    ((DocItemPropertyDW) dockWindow).displayItemInfo(documentItem);
//                    DocumentItemProperty property = DocItemPropertyDW.ShowProperty(item, true);
//                    if (property != null) {
//                        property.BeforePropertyApply -= new PropertyApplyEventHandler(property_BeforePropertyApply);
//                        property.BeforePropertyApply += new PropertyApplyEventHandler(property_BeforePropertyApply);
//                        property.AfterPropertyApply -= new PropertyApplyEventHandler(property_AfterPropertyApply);
//                        property.AfterPropertyApply += new PropertyApplyEventHandler(property_AfterPropertyApply);
//                    }

                    // endregion
                }
                else if(LookRecords.class.equals(type))
                {
                    //region 查看属性
                    IDockWindow dockWindow = this.application.getPluginContainer().getDockWindows().getOrDefault(PropertyViewDW.class.getName(), null);
                    if (dockWindow == null) {
                        dockWindow = this.application.getPluginContainer().createDockWindow(PropertyViewDW.class.getName());
                    }
                    ((PropertyViewDW) dockWindow).browseProperty(documentItem);
                    //endregion
                }
                /*else if (PreviewLayoutMenuItem.class.equals(type)) {

                } else if (ImportLayerStyleMenuItem.class.equals(type)) {

                } else if (ImportMapStyleMenuItem.class.equals(type)) {

                }*/ else {
                    if (menuItemClickListeners != null) {
                        fireMenuItemClick(new MenuItemClickEvent(this, type, documentItem));
                    }
                }
            }
    };
    private StateChangedListener stateChangedListener = stateChangedEvent -> {
        if (this.application != null && this.workspaceTree != null) {
            this.application.getStateManager().onStateChanged(stateChangedEvent.getSource());
        }
    };
    private ICreatedDocumentListener createdDocumentListener = createdDocumentEvent -> {
        if (createdDocumentEvent.getSource() instanceof Document) {
            Document doc = (Document) createdDocumentEvent.getSource();
            // 读取是否禁用文档标题设置到应用标题属性(1:禁用 非1:启用)
            if (true/*NewDocumentTool.ReadDisableAppTitle() != "1"*/) {
                String filePath = doc.getFilePath();
                File file = new File(filePath);
                String fileName = file.getName();
                if (fileName.isEmpty()) {
                    fileName = "新文档";
                }
                String title = WorkspacePanel.workspacePanel.application.getTitle();
                int index = title.lastIndexOf(" - ");
                if (index > 0) {
                    title = title.substring(index + 3);
                }
                title = fileName + " - " + title;
                WorkspacePanel.workspacePanel.application.setTitle(title);
            }
        }
    };
    private IOpenedDocumentListener openedDocumentListener = openedDocumentEvent -> {
        if (openedDocumentEvent.getSource() instanceof Document) {
            Document doc = (Document) openedDocumentEvent.getSource();
            String filePath = doc.getFilePath();
            // 读取是否禁用文档标题设置到应用标题属性(1:禁用 非1:启用)
            if (true/*NewDocumentTool.ReadDisableAppTitle() != "1"*/) {
                File file = new File(filePath);
                String fileName = file.getName();
                if (fileName.isEmpty()) {
                    fileName = "新文档";
                }
                String title = WorkspacePanel.workspacePanel.application.getTitle();
                int index = title.lastIndexOf(" - ");
                if (index >= 0) {
                    title = title.substring(index + 3);
                }
                title = fileName + " - " + title;
                WorkspacePanel.workspacePanel.application.setTitle(title);
            }
            // TODO: 补充 增加最近打开的文档到最近打开组
            //this.AddRecentDoc(filePath);
        }
    };
    private IClosingDocumentListener closingDocumentListener = closingDocumentEvent -> {
        if (this.application != null && this.workspaceTree != null) {
            if (!closingDocumentEvent.getArgs().isCancel()) {
                Scenes scenes = this.workspaceTree.getDocument().getScenes();
                for (int i = 0; i < scenes.getCount(); i++) {
                    Scene scene = scenes.getScene(i);
                    IContentsView icv = this.application.getPluginContainer().getContentsViews().get(scene.getHandle() + "$SceneView");
                    if (icv != null) {
                        this.application.getPluginContainer().closeContentsView(icv);
                    }
                }
            }
        }
    };
    private IClosedDocumentListener closedDocumentListener = closedDocumentEvent -> {
        if (this.application != null && this.workspaceTree != null) {
            IContentsView icvLayout = this.application.getPluginContainer().getContentsViews().get(this.workspaceTree.getDocument().getHandle() + "$LayoutView");
            if (icvLayout != null) {
                this.application.getPluginContainer().closeContentsView(icvLayout);
            }

//            if (NewDocumentTool.ReadDisableAppTitle() != "1")
//            {
//                string title = WorkSpacePanel.app.Title;
//                int index = title.LastIndexOf(" - ");
//                if (index > 0)
//                    title = title.Substring(index + 3);
//                WorkspacePanel.app.Title = title;
//            }
        }
    };
    private ISavedDocumentListener savedDocumentListener = savedDocumentEvent -> {
        if (savedDocumentEvent.getSource() instanceof Document) {
            Document doc = (Document) savedDocumentEvent.getSource();
            String filePath = doc.getFilePath();
//            if (NewDocumentTool.ReadDisableAppTitle() != "1") {
            String fileName = XPath.getNameWithoutExt(filePath);
            String title = this.application.getTitle();
            int index = title.lastIndexOf(" - ");
            if (index > 0) {
                title = title.substring(index + 3);
            }
            title = fileName + " - " + title;
            this.application.setTitle(title);
//            }
            addRecentDoc(filePath);
        }
    };
    private ISavedAsDocumentListener savedAsDocumentListener = savedAsDocumentEvent -> {
        if (savedAsDocumentEvent.getSource() instanceof Document) {
            Document doc = (Document) savedAsDocumentEvent.getSource();
            String filePath = doc.getFilePath();
//            if (NewDocumentTool.ReadDisableAppTitle() != "1") {
            String fileName = XPath.getNameWithoutExt(filePath);
            String title = this.application.getTitle();
            int index = title.lastIndexOf(" - ");
            if (index > 0) {
                title = title.substring(index + 3);
            }
            title = fileName + " - " + title;
            this.application.setTitle(title);
//            }
            addRecentDoc(filePath);
        }
    };
    private IDocumentPropertyChangedListener documentPropertyChangedListener = documentPropertyChangedEvent -> {
        DocumentEventArgs args = documentPropertyChangedEvent.getArgs();
        if (args != null) {
            String key = args.getPropertyName();
            if (key.compareTo("Title") == 0) {
                // region 更新节点名称和视图标题
                Document doc = args.getDocument();
                if (doc != null) {
                    IContentsView icv = this.application.getPluginContainer().getContentsViews().get(doc.getHandle() + "$LayoutView");
                    if (icv != null) {
                        this.application.getPluginContainer().setContentsViewText(icv, doc.getTitle());
                    }
                }
                // endregion
            }
        }
    };
    private IRemoveMapListener removeMapListener = removeMapEvent -> {
        if (this.application != null && this.workspaceTree != null) {
            IContentsView icv = this.application.getPluginContainer().getContentsViews().get(removeMapEvent.getArgs().getRemovedMap().getHandle() + "$MapView");
            if (icv != null) {
                this.application.getPluginContainer().closeContentsView(icv);
            }
        }
    };
    private IRemoveLayerListener mapRemoveLayerListener = removeLayerEvent -> {

        if (removeLayerEvent.getSource() instanceof Map && removeLayerEvent.getArgs().getRemovedLayer() != null) {
            fireRemoveLayer(new RemoveLayerEvent(this, (Map) removeLayerEvent.getSource(), removeLayerEvent.getArgs().getRemovedLayer()));
        }
    };
    private IRemoveLayerListener groupLayerRemoveLayerListener = removeLayerEvent -> {
        if (removeLayerEvent.getArgs().getGroupLayer() != null) {
            fireRemoveLayer(new RemoveLayerEvent(this, getOwnerMap(removeLayerEvent.getArgs().getGroupLayer()), removeLayerEvent.getArgs().getRemovedLayer()));
        }
    };
    private IMapPropertyChangedListener mapPropertyChangedListener = mapPropertyChangedEvent -> {
        if (mapPropertyChangedEvent.getArgs().getPropertyName().toLowerCase().equals("mapname")) {
            // region 更新节点名称和视图标题
            Map map = mapPropertyChangedEvent.getArgs().getMap();
            if (map != null) {
                IContentsView icv = this.application.getPluginContainer().getContentsViews().get(map.getHandle() + "$MapView");
                if (icv != null) {
                    this.application.getPluginContainer().setContentsViewText(icv, map.getName());
                }
            }
            // endregion
        }
    };
    private ILayerPropertyChangedListener layerPropertyChangedListener = layerPropertyChangedEvent -> {

        if (layerPropertyChangedEvent.getArgs().getPropertyName().equals("State")) {
            // region 图层状态属性改变后处理（维护选择集）
//            if (layerPropertyChangedEvent.getArgs().getMapLayer() != null) {
//                MapLayer layer = layerPropertyChangedEvent.getArgs().getMapLayer();
//                if (layer.getState() == LayerState.UnVisible) {
//                    Map map = getOwnerMap(layer);
//                    if (map != null && this.application != null) {
//                        IContentsView icv = this.application.getPluginContainer().getContentsViews().get(map.getHandle() + "$MapView");
//                        if (icv instanceof IMapContentsView) {
//                            SelectSet selSet = map.getSelectSet();
//                            if (selSet != null) {
//                                List<SelectSetItem> selItems = selSet.get();
//                                if (selItems != null) {
//                                    for (SelectSetItem selItem : selItems) {
//                                        if (selItem.getLayer().getHandle() == layer.getHandle()) {
//                                            // TODO: SelectSetItem.IDList
////                                            selSet.remove(layer, selItem.IDList);
////                                            StressMapItem.StressSelectSet((icv as IMapContentsView).MapControl);
//                                            break;
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
            // endregion
        }
        if (layerPropertyChangedEvent.getArgs().getPropertyName().toLowerCase().equals("systemlibrary")) {
            // region 图层系统库改变后处理
            if (layerPropertyChangedEvent.getArgs().getMapLayer() != null) {
                fireLayerPropertyChanged(new LayerPropertyChangedEvent(this,
                        layerPropertyChangedEvent.getArgs().getMapLayer(), layerPropertyChangedEvent.getArgs().getPropertyName()));
            }
            // endregion
        }
    };

    // endregion

    // region 公有方法

    /**
     * 清除 Workspace 中该 MapControl 和 Map 的映射关系
     *
     * @param mapControl 地图视图
     */
    public void clearMapControl(MapControl mapControl) {
        if (mapControl != null && workspaceTree != null) {
            workspaceTree.getWorkspace().setMapControl(mapControl.getMap(), null);
        }
    }

    /**
     * 清除 WorkSpace 中该 SceneControl 和 Scene 的映射关系
     *
     * @param sceneControl 场景视图
     */
    public void clearSceneControl(SceneControl sceneControl) {
        if (sceneControl != null && workspaceTree != null) {
            workspaceTree.getWorkspace().setSceneControl(null, sceneControl);
        }
    }

    // endregion

    // region 私有方法

    /**
     * 矩形是否相交
     *
     * @param rt 矩形
     * @param rt1 矩形1
     * @return true/false
     */
    private boolean isIntersect(Rect rt, Rect rt1) {
        boolean rtn = false;
        if (rt != null && rt1 != null) {
            Rectangle2D rect = new Rectangle2D(rt.getXMin(), rt.getYMin(), rt.getXMax() - rt.getXMin(), rt.getYMax() - rt.getYMin());
            Rectangle2D rect1 = new Rectangle2D(rt1.getXMin(), rt1.getYMin(), rt1.getXMax() - rt1.getXMin(), rt1.getYMax() - rt1.getYMin());
            rtn = rect.intersects(rect1);
        }
        return rtn;
    }

    /**
     * 获取已知 DocumentItem 所属的 Map
     *
     * @param docItem
     * @return
     */
    private Map getOwnerMap(DocumentItem docItem) {
        Map map = null;
        if (docItem != null) {
            if (docItem instanceof Map) {
                map = (Map) docItem;
            } else if (docItem.getParent() != null) {
                map = getOwnerMap(docItem.getParent());
            }
        }
        return map;
    }

    /**
     * 增加最近打开的文档到最近打开组
     * @param recentFile
     */
    private void addRecentDoc(String recentFile) {
        if (recentFile != null && !recentFile.isEmpty()) {
//            IRecentFileGroup rfg = null;
//            rfg = app.RecentFileManager["RecentDocument"];
//            if (rfg != null) {
//                rfg.Insert(0, recentFile);
//            }
        }
    }

    // endregion
}
