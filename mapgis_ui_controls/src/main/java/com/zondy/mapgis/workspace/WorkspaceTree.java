package com.zondy.mapgis.workspace;

import com.zondy.mapgis.base.MessageBox;
import com.zondy.mapgis.base.Window;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.controls.SceneControl;
import com.zondy.mapgis.geodatabase.raster.MosaicDataset;
import com.zondy.mapgis.map.*;
import com.zondy.mapgis.map.Map;
import com.zondy.mapgis.map.event.*;
import com.zondy.mapgis.scene.*;
import com.zondy.mapgis.scene.event.*;
import com.zondy.mapgis.systemlib.SystemLibrary;
import com.zondy.mapgis.workspace.engine.*;
import com.zondy.mapgis.workspace.enums.DisplayState;
import com.zondy.mapgis.workspace.enums.ItemType;
import com.zondy.mapgis.workspace.event.*;
import com.zondy.mapgis.workspace.menuitem.*;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.File;
import java.util.*;

public class WorkspaceTree extends TreeView<Object> {
    /**
     * 获取地图文档元素名称
     *
     * @param item 地图文档元素
     * @return 地图文档元素名称
     */
    private String getDocumentItemName(DocumentItem item) {
        if (item != null) {
            if (item instanceof Document) {
                return ((Document) item).getTitle();
            } else if (item instanceof Map) {
                return ((Map) item).getName();
            } else if (item instanceof Scene) {
                return ((Scene) item).getName();
            } else if (item instanceof MapLayer) {
                return ((MapLayer) item).getName();
            } else if (item instanceof Map3DLayer) {
                return ((Map3DLayer) item).getName();
            }
        }
        return "";
    }

    private WorkspaceTree treeView;                         // 工作空间树对象
    private Workspace workspace;                            // 工作空间对象
    private HashMap<String, Image> images;                  // 缓存图标
    private HashMap<String, Image> dynamicImages;           // 记录专题图子节点的图标索引，需要在关闭的时候将其图标释放

    private Document document;                              // 文档
    private Maps maps;                                      // 地图
    private Scenes scenes;                                  // 场景

    private boolean closed = true;                          // 是否关闭了，即点击关闭之后是否选择了“取消”
    private boolean updateing = false;                      // 正在更新树标志
    private boolean dragingNode = false;                    // 是否正在拖动节点
    private boolean enablePopupMenu = true;                 // 是否启用右键菜单
    private boolean causeLayerStateChanged = false;         // 标志Layer的State改变时是否触发PropertyChanged事件。当从编辑节点向上或向下修改状态
    private boolean groupLayerStateSelf = false;            // 标记组图层的状态是否独立于其子图层，如果是，修改组图层时不会相应修改子图层，修改子图层也不会修改组图层

    private List<IMenuExtender> menuExtenders;              // 在添加事件时防止重复添加而设立
    private HashMap<IMenuItem, MenuItem> menuItems;         // 缓存 IMenuItem 动态信息，用于构建 MenuItem 和 Menu
    private HashMap<IMenuItem, Boolean> menuItemGroups;     // 缓存 IMenuItem 动态信息 BeginGroup，用于构建 SeparatorMenuItem
    private HashMap<Long, TreeItem<Object>> treeItems;      // 缓存文档对象（主要是专题图对象）和节点的映射关系，方便GetNodeByTheme的快速查找

    /**
     * 创建工作空间树
     *
     * @param document 文档对象
     */
    public WorkspaceTree(Document document) {
        super();
        this.init(null, document);
        //this.initTree();
    }

    /**
     * 创建工作空间树
     *
     * @param workspace 工作空间
     * @param document  文档对象
     */
    public WorkspaceTree(IWorkspace workspace, Document document) {
        super();
        this.init(workspace instanceof Workspace ? ((Workspace) workspace) : null, document);
        //this.initTree();
    }

    // region 属性

    /**
     * 获取工作空间对象
     *
     * @return 工作空间对象
     */
    public IWorkspace getWorkspace() {
        return workspace;
    }

    /**
     * 获取当前文档
     *
     * @return 文档
     */
    public Document getDocument() {
        return document;
    }

    // endregion

    private DataFormat moveDataFormat = new DataFormat("moveTreeItem");
    private TreeItem[] moveTreeItems = null;
    private TreeCell<Object> tempTreeCell = null;

    /**
     * 初始化
     *
     * @param workspace 工作空间
     * @param document  文档对象
     */
    private void init(Workspace workspace, Document document) {
        this.treeView = this;
//        this.treeView.setCellFactory(p -> new TextFieldTreeCell<Object>() {
//            @Override
//            public void updateItem(Object item, boolean empty) {
//                super.updateItem(item, empty);
//                if (item instanceof DocumentItem) {
//                    setText(getDocumentItemName((DocumentItem) item));
//                } else if (item instanceof Theme) {
//                    setText(((Theme) item).getName());
//                } else if (item instanceof Theme3D) {
//                    setText(((Theme3D) item).getName());
//                }
//            }
//        });
        // 设置多选
        this.treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // 设置右键菜单
        ContextMenu contextMenu = new ContextMenu();
        this.treeView.setContextMenu(contextMenu);
        this.treeView.setOnMousePressed(this::onMousePressed);

        // 设置拖拽
        this.treeView.setCellFactory(new Callback<TreeView<Object>, TreeCell<Object>>() {
            @Override
            public TreeCell<Object> call(TreeView<Object> param) {
                // 设置节点名称
                TextFieldTreeCell<Object> cell = new TextFieldTreeCell<Object>() {
                    @Override
                    public void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item instanceof DocumentItem) {
                            setText(getDocumentItemName((DocumentItem) item));
                        } else if (item instanceof Theme) {
                            setText(((Theme) item).getName());
                        } else if (item instanceof Theme3D) {
                            setText(((Theme3D) item).getName());
                        }
                    }
                };

                // 获取拖拽选择节点集
                cell.setOnDragDetected(event -> {
                    moveTreeItems = treeView.getSelectionModel().getSelectedItems().toArray(new TreeItem[0]);
                    TreeItem parentTreeItem = null;
                    for (TreeItem treeItem : moveTreeItems) {
                        if (parentTreeItem == null) {
                            parentTreeItem = treeItem.getParent();
                        }
                        if (treeItem.getParent() != parentTreeItem) {
                            moveTreeItems = null;
                            break;
                        }
                    }
                    Dragboard dragboard = cell.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.put(moveDataFormat, "moveTreeItem");
                    dragboard.setContent(content);
                    System.out.println("移动项个数：" + moveTreeItems.length);
                });
                // 拖拽至某节点上
                cell.setOnDragOver(event -> {
                    if (moveTreeItems != null) {
                        event.acceptTransferModes(TransferMode.MOVE);

                        TreeItem<Object> targetTreeItem = cell.getTreeItem();

                        if (tempTreeCell != null && tempTreeCell != cell) {
                            tempTreeCell.setBorder(null);
                        }
                        tempTreeCell = cell;
                        Border border = null;
                        if (event.getY() >= 0 && event.getY() <= 10) {
                            BorderStroke borderStroke = new BorderStroke(Paint.valueOf("#71C671"), null, null, null,
                                    BorderStrokeStyle.SOLID, null, null, null,
                                    null, new BorderWidths(2, 0, 0, 0), null);
                            border = new Border(borderStroke);
                        } else if (event.getY() > cell.getHeight() - 10 && event.getY() <= cell.getHeight()) {
                            BorderStroke borderStroke = new BorderStroke(Paint.valueOf("#71C671"), null, null, null,
                                    BorderStrokeStyle.SOLID, null, null, null,
                                    null, new BorderWidths(0, 0, 2, 0), null);
                            border = new Border(borderStroke);
                        }
                        cell.setBorder(border);
                    }
                });
                // 确定目标节点，执行移动操作
                cell.setOnDragDropped(event -> {
                    if (moveTreeItems != null) {
                        if (event.getDragboard().getContent(moveDataFormat) != null) {
                            Map targetDI = (Map) cell.getTreeItem().getValue();
                            int count = moveTreeItems.length;
                            for (int i = 0; i < count; i++) {
                                System.out.println("第" + (i + 1) + "次");
                                TreeItem TreeItem = moveTreeItems[i];
                                MapLayer moveDI = (MapLayer) TreeItem.getValue();
                                if (moveDI != null && targetDI != null) {
                                    ((Map) moveDI.getParent()).dragOut(moveDI);
                                    targetDI.append(moveDI);
                                    System.out.println(moveDI.getName() + " 由 " + ((Map) moveDI.getParent()).getName() + " 移动至 " + targetDI.getName());
                                }
                            }
                        } else {
                            System.out.println("moveDataFormat == null");
                        }
                        cell.setBorder(null);
                    }
                });
                return cell;
            }
        });


        this.initImages();

        this.menuExtenders = new ArrayList<>();
        this.menuItems = new HashMap<>();
        this.menuItemGroups = new HashMap<>();
        this.treeItems = new HashMap<>();

        if (workspace == null) {
            workspace = new Workspace();
            workspace.loadCustomWorkSpace();
            // TODO: 测试加载其他包
            //workspace.loadCustomWorkSpace("./mapgis_workspace_plugin/target/mapgis_workspace_plugin-1.0-SNAPSHOT.jar");
        }
        this.attachWorkspace(workspace);
        this.document = document;
        this.maps = this.document.getMaps();
        this.scenes = this.document.getScenes();
        this.addDocumentItemListener(this.document);
        this.addDocumentItemListener(this.maps);
        this.addDocumentItemListener(this.scenes);
    }

    private void onMousePressed(MouseEvent event) {
        TreeItem treeItem = getTreeItemByNode(event.getPickResult().getIntersectedNode());
        if (this.enablePopupMenu && event.getButton() == MouseButton.SECONDARY && event.getClickCount() == 1) {
            System.out.println("click menu");
            if (treeItem == null) {
                System.out.println("null");
                // 显示空白地方的右键菜单
                showBlankAreaPopupMenu();
            } else {
                treeView.getContextMenu().getItems().clear();
                ObservableList<TreeItem<Object>> treeItems = treeView.getSelectionModel().getSelectedItems();
                if (treeItems.size() == 1) {
                    System.out.println("1  " + treeItem.getValue());

                    // region 选中单一节点时的菜单

                    Object obj = treeItem.getValue();
                    if (obj != null) {
                        if (obj instanceof Theme || obj instanceof Theme3D || obj instanceof ThemeInfo) {
                            // 专题图及其子节点菜单
                            //this.ShowThemeMenu(trNode, e, false);
                        } else {
                            // region 获取样式菜单
                            IItemStyle itemStyle = null;
                            IMenuExtender menuExtender = null;
                            // 镶嵌集矢量子图层标记
                            // TODO: 待添加 LayerType.MosaicDatasetSFeature 后，取消注释。
                            // boolean flag = obj instanceof VectorLayer && ((VectorLayer) obj).getType() == LayerType.MosaicDatasetSFeature;
                            boolean flag = false;
                            itemStyle = this.workspace.getItemStyle(ItemType.toValue(obj.getClass()));
                            menuExtender = this.workspace.getMenuExtender(ItemType.toValue(obj.getClass()));
                            this.listenMenuExtender(menuExtender);
                            IMenuItem[] menuItems = menuExtender.getItems();
                            // endregion
                            if (itemStyle != null && menuItems != null) {
                                ObservableList<MenuItem> jfxMenuItems = treeView.getContextMenu().getItems();
                                jfxMenuItems.clear();
                                for (IMenuItem menuItem : menuItems) {
                                    if (flag) {
                                        String caption = menuItem.getCaption();
                                        if ("专题图".equals(caption) || "层编辑".equals(caption) || "统改参数/属性".equals(caption) || "属性结构设置".equals(caption)) {
                                            continue;
                                        }
                                    }
                                    this.initMenuItem(menuItem, jfxMenuItems, true);
                                }
                                if (itemStyle.getPopMenu() instanceof ISinglePopMenu) {
                                    ((ISinglePopMenu) itemStyle.getPopMenu()).opening((DocumentItem) obj);
                                }
                            }
                        }
                    }

                    // endregion
                } else {
                    if (isTreeItemsInSameLevel(treeItems)) {
                        System.out.println(treeItems.size());
                    } else {
                    }
                }
            }
        } else if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
            Node node = event.getPickResult().getIntersectedNode();
            if (node instanceof ImageView) {
                ImageView imageView = (ImageView) node;
                System.out.println("imageView");
                int index = ((HBox) imageView.getParent()).getChildren().indexOf(imageView);
                System.out.println(index);
                if (index == 0) {
                    onStateImageViewClick(treeItem);
                } else {
                    onTypeImageViewClick(treeItem);
                }
            }
        }
    }

    private void onStateImageViewClick(TreeItem treeItem) {
        this.beginUpdate();
        if (treeItem != null) {
            if (treeItem.getValue() instanceof Theme || treeItem.getValue() instanceof Theme3D) {
//                this.ChangeThemeNodeState(treeItem);
            } else if (treeItem.getValue() instanceof ThemeInfo) {
//                this.ChangeThemeSubNodeState(treeItem, true);
            } else if (treeItem.getValue() instanceof DocumentItem) {
                DocumentItem documentItem = (DocumentItem) treeItem.getValue();
                if (documentItem instanceof MapLayer) {
//                    if (documentItem instanceof MapSetClsLayer) {
//                        ((MapSetClsLayer) documentItem).IsDisp = !((MapSetClsLayer) documentItem).IsDisp;
//                        int imageIndex = 2;
//                        if (documentItem.ImageIndex > 4) {
//                            imageIndex = ((MapSetClsLayer) documentItem).IsDisp ? 6 : 5;
//                        } else {
//                            imageIndex = ((MapSetClsLayer) documentItem).IsDisp ? 2 : 1;
//                        }
//                        documentItem.ImageIndex = imageIndex;
//                        documentItem.SelectImageIndex = imageIndex;
//                    } else if (!(documentItem instanceof MapSetFrmLayer)) {
                    MapLayer mapLayer = (MapLayer) documentItem;
                    if (mapLayer.getState() == LayerState.UnVisible) {
                        mapLayer.setState(LayerState.Visible);
                    } else if (mapLayer.getState() == LayerState.Visible || mapLayer.getState() == LayerState.Editable || mapLayer.getState() == LayerState.Active) {
                        mapLayer.setState(LayerState.UnVisible);
                    }
//                    }
                } else if (documentItem instanceof Map3DLayer) {
                    Map3DLayer map3DLayer = (Map3DLayer) documentItem;
                    if (map3DLayer.getState() == LayerState.UnVisible) {
                        map3DLayer.setState(LayerState.Visible);
                    } else if (map3DLayer.getState() == LayerState.Visible || map3DLayer.getState() == LayerState.Editable || map3DLayer.getState() == LayerState.Active) {
                        map3DLayer.setState(LayerState.UnVisible);
                    }
                } else {
                    DisplayState displayState = DisplayState.UNVISIBLE;
                    if (getDisplayState(treeItem) == DisplayState.UNVISIBLE) {
                        displayState = DisplayState.VISIBLE;
                    }
                    setDisplayState(treeItem, displayState);
                    this.setParentNodeShowState(treeItem.getParent());
                    if (treeItem.getChildren().size() > 0) {
//                        this.SetSubNodeImage(treeItem);
                    }
                }
                this.innerRefreshMapView(documentItem);
            }
        }
        this.endUpdate();
    }

    private void onTypeImageViewClick(TreeItem treeItem) {
        // TODO: 待添加接口后，去除注释代码
        this.beginUpdate();
        if (treeItem != null) {
            if (treeItem.getValue() instanceof Theme || treeItem.getValue() instanceof Theme3D) {
//                this.ChangeThemeNodeState(treeItem);
            } else if (treeItem.getValue() instanceof ThemeInfo) {
//                this.ChangeThemeSubNodeState(treeItem, true);
            } else if (treeItem.getValue() instanceof DocumentItem) {
                DocumentItem documentItem = (DocumentItem) treeItem.getValue();
                boolean refresh = true;
                if (documentItem instanceof GroupLayer) {
                    // region 更新组图层状态
                    GroupLayer groupLayer = (GroupLayer) documentItem;
                    if (groupLayer.getState() == LayerState.UnVisible) {
                        groupLayer.setState(LayerState.Visible);
                    } else if (groupLayer.getState() == LayerState.Visible) {
                        if (/*groupLayer instanceof MapSetLayer || */groupLayer instanceof NetClsLayer) {
                            groupLayer.setState(LayerState.Active);
                        } else {
                            groupLayer.setState(LayerState.UnVisible);
                        }
                    } else if (groupLayer.getState() == LayerState.Active) {
                        groupLayer.setState(LayerState.UnVisible);
                    }
                    // endregion
                } else if (documentItem instanceof Group3DLayer) {
                    // region 更新组图层状态
                    Group3DLayer group3DLayer = (Group3DLayer) documentItem;
                    boolean leafNode = false;
//                    if (group3DLayer.GetPropertyEx("ShowSubNode").toLowerCase().equals("false")) {
//                        leafNode = true;
//                    }
                    if (group3DLayer.getState() == LayerState.UnVisible) {
                        group3DLayer.setState(LayerState.Visible);
                    } else if (group3DLayer.getState() == LayerState.Visible) {
                        if (leafNode/* || group3DLayer instanceof TerrainLayer*/) {
                            group3DLayer.setState(LayerState.Editable);
                        } else {
                            group3DLayer.setState(LayerState.UnVisible);
                        }
                    } else if (group3DLayer.getState() == LayerState.Editable) {
                        if (leafNode/* || group3DLayer instanceof TerrainLayer*/) {
                            group3DLayer.setState(LayerState.Active);
                        } else {
                            group3DLayer.setState(LayerState.UnVisible);
                        }
                    } else if (group3DLayer.getState() == LayerState.Active) {
                        group3DLayer.setState(LayerState.UnVisible);
                    }
                    // endregion
                } else if (documentItem instanceof MapLayer) {
                    // region 更新图层状态
//                    if (documentItem instanceof MapSetClsLayer) {
//                        ((MapSetClsLayer) documentItem).IsDisp = !((MapSetClsLayer) documentItem).IsDisp;
//                        int imageIndex = 2;
//                        if (treeItem.ImageIndex > 4)
//                            imageIndex = ((MapSetClsLayer) documentItem).IsDisp ? 6 : 5;
//                        else
//                            imageIndex = ((MapSetClsLayer) documentItem).IsDisp ? 2 : 1;
//                        treeItem.ImageIndex = imageIndex;
//                        treeItem.SelectImageIndex = imageIndex;
//                    } else if (!(documentItem instanceof MapSetFrmLayer)) {
                    if (documentItem instanceof ImageLayer) {
                        LayerState state = ((MapLayer) documentItem).getState();
                        if (LayerState.UnVisible.equals(state)) {
                            refresh = true;
                            ((MapLayer) documentItem).setState(LayerState.Visible);
                        } else if (LayerState.Editable.equals(state) || LayerState.Active.equals(state)) {
                            ((MapLayer) documentItem).setState(LayerState.Visible);
                        } else if (LayerState.Visible.equals(state)) {
                            refresh = true;
                            ((MapLayer) documentItem).setState(LayerState.UnVisible);
                        }
                    } else if (documentItem.getParent() instanceof GroupLayer && ((MapLayer) documentItem.getParent()).getData() instanceof MosaicDataset) {
                        LayerState state = ((MapLayer) documentItem).getState();
                        if (LayerState.UnVisible.equals(state)) {
                            ((MapLayer) documentItem).setState(LayerState.Visible);
                        } else if (LayerState.Visible.equals(state)) {
                            ((MapLayer) documentItem).setState(LayerState.UnVisible);
                        }
                    } else {
                        LayerState state = ((MapLayer) documentItem).getState();
                        if (LayerState.UnVisible.equals(state)) {
                            ((MapLayer) documentItem).setState(LayerState.Visible);
                        } else if (LayerState.Visible.equals(state)) {
                            refresh = false;
                            ((MapLayer) documentItem).setState(LayerState.Editable);
                        } else if (LayerState.Editable.equals(state)) {
                            refresh = false;
                            ((MapLayer) documentItem).setState(LayerState.Active);
                        } else if (LayerState.Active.equals(state)) {
                            ((MapLayer) documentItem).setState(LayerState.UnVisible);
                        }
                    }
//                    }
                    // endregion
                } else if (documentItem instanceof Map3DLayer) {
                    // region 更新图层状态
//                    if (documentItem instanceof Server3DLayer) {
//                        LayerState state = ((Map3DLayer) documentItem).getState();
//                        if (LayerState.UnVisible.equals(state) || LayerState.Editable.equals(state) || LayerState.Active.equals(state)) {
//                            ((Map3DLayer) documentItem).setState(LayerState.Visible);
//                        } else if (LayerState.Visible.equals(state)) {
//                            ((Map3DLayer) documentItem).setState(LayerState.UnVisible);
//                        }
//                    } else {
                    // region 对模型层/地形层 / 注记层 / 矢量层状态进行处理
                    LayerState state = ((Map3DLayer) documentItem).getState();
                    if (LayerState.UnVisible.equals(state)) {
                        ((Map3DLayer) documentItem).setState(LayerState.Visible);
                    } else if (LayerState.Visible.equals(state)) {
                        ((Map3DLayer) documentItem).setState(LayerState.Editable);
                    } else if (LayerState.Editable.equals(state)) {
                        ((Map3DLayer) documentItem).setState(LayerState.Active);
                    } else if (LayerState.Active.equals(state)) {
                        ((Map3DLayer) documentItem).setState(LayerState.UnVisible);
                    }
                    // endregion
//                    }
                    // endregion
                } else {
                    // region 更新其他类型节点

                    if (documentItem instanceof Document) {
                        refresh = false;
                    }
                    DisplayState displayState = DisplayState.UNVISIBLE;
                    if (getDisplayState(treeItem) == DisplayState.UNVISIBLE) {
                        displayState = DisplayState.VISIBLE;
                    }
                    setDisplayState(treeItem, displayState);
                    this.setParentNodeShowState(treeItem.getParent());
                    if (treeItem.getChildren().size() > 0) {
//                        this.SetSubNodeImage(treeItem);
                    }

                    // endregion
                }
                if (refresh) {
                    this.innerRefreshMapView(documentItem);
                } else if (documentItem instanceof Document) {
                    this.refreshDocumentItem(documentItem);
                }
            }
        }
        this.endUpdate();
    }

    /**
     * 根据即时刷新来刷新视图
     *
     * @param documentItem 文档项
     */
    private void innerRefreshMapView(DocumentItem documentItem) {
        Map map = this.getMap(documentItem);
        if (this.workspace != null && map != null) {
            MapControl mapControl = this.workspace.getMapControl(map);
            if (mapControl != null/* && ViewInfoHelp.GetSoonFresh(mapControl)*/) {
                mapControl.refreshWnd();
            }
        }
    }

    /**
     * 获取地图节点
     *
     * @param documentItem 任意图层节点
     * @return 地图
     */
    private Map getMap(DocumentItem documentItem) {
        if (documentItem == null) {
            return null;
        }
        if (documentItem instanceof Map) {
            return (Map) documentItem;
        } else {
            return getMap(documentItem.getParent());
        }
    }

    /**
     * 获取场景节点
     *
     * @param documentItem 任意图层节点
     * @return 场景
     */
    private Scene getScene(DocumentItem documentItem) {
        if (documentItem == null) {
            return null;
        }
        if (documentItem instanceof Scene) {
            return (Scene) documentItem;
        } else {
            return getScene(documentItem.getParent());
        }
    }

    /**
     * 初始化图标
     */
    private void initImages() {
        images = new HashMap<>();
        images.put("Png_NodeActive_16", new Image(getClass().getResourceAsStream("/Png_NodeActive_16.png")));
        images.put("Png_NodeActive_18_16", new Image(getClass().getResourceAsStream("/Png_NodeActive_18_16.png")));
        images.put("Png_NodeEditable_18_16", new Image(getClass().getResourceAsStream("/Png_NodeEditable_18_16.png")));
        images.put("Png_NodeIndeterminate_18_16", new Image(getClass().getResourceAsStream("/Png_NodeIndeterminate_18_16.png")));
        images.put("Png_NodeUnVisible_18_16", new Image(getClass().getResourceAsStream("/Png_NodeUnVisible_18_16.png")));
        images.put("Png_NodeVisible_18_16", new Image(getClass().getResourceAsStream("/Png_NodeVisible_18_16.png")));
        images.put("Png_DocumentNew_16", new Image(getClass().getResourceAsStream("/Png_DocumentNew_16.png")));
        images.put("Png_MapView_16", new Image(getClass().getResourceAsStream("/Png_MapView_16.png")));
        images.put("Png_SceneView_16", new Image(getClass().getResourceAsStream("/Png_SceneView_16.png")));
        images.put("Png_Unknown_16", new Image(getClass().getResourceAsStream("/Png_Unknown_16.png")));
        images.put("Png_SfClsPnt_16", new Image(getClass().getResourceAsStream("/Png_SfClsPnt_16.png")));
        images.put("Png_SfClsLin_16", new Image(getClass().getResourceAsStream("/Png_SfClsLin_16.png")));
        images.put("Png_SfClsReg_16", new Image(getClass().getResourceAsStream("/Png_SfClsReg_16.png")));
        images.put("Png_SfClsSurface_16", new Image(getClass().getResourceAsStream("/Png_SfClsSurface_16.png")));
        images.put("Png_Open_16", new Image(getClass().getResourceAsStream("/Png_Open_16.png")));
        images.put("Png_AddMap_16", new Image(getClass().getResourceAsStream("/Png_AddMap_16.png")));
        images.put("Png_AddScene_16", new Image(getClass().getResourceAsStream("/Png_AddScene_16.png")));
        dynamicImages = new HashMap<>();
    }

    /**
     * 关联一个新的 WorkSpace
     *
     * @param workspace WorkSpace
     */
    private void attachWorkspace(Workspace workspace) {
        if (workspace != null) {
            if (this.workspace != null) {
                this.workspace.removeSetMenuItemBeginGroupListener(this.setMenuItemBeginGroupListener);
                this.workspace.removeSetMenuItemImageListener(this.setMenuItemImageListener);
                this.workspace.removeSetMenuItemCaptionListener(this.setMenuItemCaptionListener);
                this.workspace.removeSetMenuItemCheckedListener(this.setMenuItemCheckedListener);
                this.workspace.removeSetMenuItemEnableListener(this.setMenuItemEnableListener);
                this.workspace.removeSetMenuItemVisibleListener(this.setMenuItemVisibleListener);
                this.workspace.removeMenuItemClickListener(this.menuItemClickListener);
                this.workspace.removeMultiMenuItemClickListener(this.multiMenuItemClickListener);
                this.workspace.removeBeginUpdateTreeListener(this.beginUpdateTreeListener);
                this.workspace.removeEndUpdateTreeListener(this.endUpdateTreeListener);
                // this.workspace.MapAttachMapControlListener();
            }
            this.workspace = workspace;

            this.workspace.addSetMenuItemBeginGroupListener(this.setMenuItemBeginGroupListener);
            this.workspace.addSetMenuItemImageListener(this.setMenuItemImageListener);
            this.workspace.addSetMenuItemCaptionListener(this.setMenuItemCaptionListener);
            this.workspace.addSetMenuItemCheckedListener(this.setMenuItemCheckedListener);
            this.workspace.addSetMenuItemEnableListener(this.setMenuItemEnableListener);
            this.workspace.addSetMenuItemVisibleListener(this.setMenuItemVisibleListener);
            this.workspace.addMenuItemClickListener(this.menuItemClickListener);
            this.workspace.addMultiMenuItemClickListener(this.multiMenuItemClickListener);
            this.workspace.addBeginUpdateTreeListener(this.beginUpdateTreeListener);
            this.workspace.addEndUpdateTreeListener(this.endUpdateTreeListener);
            // this.workspace.MapAttachMapControlListener();
        }
    }

    // region Workspace Listeners

    private SetMenuItemBeginGroupListener setMenuItemBeginGroupListener = setMenuItemBeginGroupEvent -> {
        IMenuItem menuItem = setMenuItemBeginGroupEvent.getMenuItem();
        boolean beginGroup = setMenuItemBeginGroupEvent.isBeginGroup();
        if (menuItem != null && menuItemGroups.containsKey(menuItem)) {
            menuItemGroups.replace(menuItem, beginGroup);
        }
        return beginGroup;
    };
    private SetMenuItemImageListener setMenuItemImageListener = setMenuItemImageEvent -> {
        IMenuItem menuItem = setMenuItemImageEvent.getMenuItem();
        Image image = setMenuItemImageEvent.getImage();
        if (menuItem != null) {
            if (menuItems.containsKey(menuItem)) {
                menuItems.get(menuItem).setGraphic(new ImageView(image));
            } else {
                addMenuItem(menuItem).setGraphic(new ImageView(image));
            }
        }
        return image;
    };
    private SetMenuItemCaptionListener setMenuItemCaptionListener = setMenuItemCaptionEvent -> {
        IMenuItem menuItem = setMenuItemCaptionEvent.getMenuItem();
        String caption = setMenuItemCaptionEvent.getCaption();
        if (menuItem != null) {
            if (menuItems.containsKey(menuItem)) {
                menuItems.get(menuItem).setText(caption);
            } else {
                addMenuItem(menuItem).setText(caption);
            }
        }
        return caption;
    };
    private SetMenuItemCheckedListener setMenuItemCheckedListener = setMenuItemCheckedEvent -> {
        IMenuItem menuItem = setMenuItemCheckedEvent.getMenuItem();
        boolean checked = setMenuItemCheckedEvent.isChecked();
        if (menuItem != null) {
            if (menuItems.containsKey(menuItem)) {
                MenuItem jfxmi = menuItems.get(menuItem);
                if (jfxmi instanceof CheckMenuItem) {
                    ((CheckMenuItem) jfxmi).setSelected(checked);
                }
            } else {
                MenuItem newJfxmi = addMenuItem(menuItem);
                if (newJfxmi instanceof CheckMenuItem) {
                    ((CheckMenuItem) newJfxmi).setSelected(checked);
                }
            }
        }
        return checked;
    };
    private SetMenuItemEnableListener setMenuItemEnableListener = setMenuItemEnableEvent -> {
        IMenuItem menuItem = setMenuItemEnableEvent.getMenuItem();
        boolean enable = setMenuItemEnableEvent.isEnable();
        if (menuItem != null) {
            if (menuItems.containsKey(menuItem)) {
                menuItems.get(menuItem).setDisable(!enable);
            } else {
                addMenuItem(menuItem).setDisable(!enable);
            }
        }
        return enable;
    };
    private SetMenuItemVisibleListener setMenuItemVisibleListener = setMenuItemVisibleEvent -> {
        IMenuItem menuItem = setMenuItemVisibleEvent.getMenuItem();
        boolean visible = setMenuItemVisibleEvent.isVisible();
        if (menuItem != null) {
            if (menuItems.containsKey(menuItem)) {
                menuItems.get(menuItem).setVisible(visible);
            } else {
                addMenuItem(menuItem).setVisible(visible);
            }
        }
        return visible;
    };
    private MenuItemClickListener menuItemClickListener = menuItemClickEvent -> {
        Class<?> type = menuItemClickEvent.getType();
        DocumentItem item = menuItemClickEvent.getDocumentItem();
        if (VisibleLayerMenuItem.class.equals(type) || UnVisibleLayerMenuItem.class.equals(type)) {
            // region 可见/不可见

            // endregion
        } else if (RenameItemMenuItem.class.equals(type)) {

        } else if (DocumentItemPropertyMenuItem.class.equals(type)) {
            this.fireMenuItemClick(menuItemClickEvent);
        } else if (SaveDocumentMenuItem.class.equals(type)) {
            // region 保存文档
            saveDocument((Document) item);
            // endregion
        } else if (RefreshDocumentMenuItem.class.equals(type)) {
            // region 刷新文档
            refreshDocumentItem((Document) item);
            // endregion
        } else if (DeleteMapSceneMenuItem.class.equals(type)) {
            // region 删除单个地图/场景

            workspace.beginUpdateTree();
            if (item instanceof Map) {
                if (item.getParent() != null) {
                    Maps maps = ((Document) item.getParent()).getMaps();
                    maps.remove(maps.indexOf((Map) item));
                }
            } else if (item instanceof Scene) {
                if (item.getParent() != null) {
                    Scenes scenes = ((Document) item.getParent()).getScenes();
                    scenes.removeScene(scenes.indexOfScene((Scene) item));
                }
            }
            workspace.endUpdateTree();

            // endregion
        } else if (DeleteLayerMenuItem.class.equals(type)) {
            // region 删除单个图层

            workspace.beginUpdateTree();
            Map map = StaticFunction.getOwnerMap(item);
            DocumentItem parent = item.getParent();
            if (parent instanceof Map) {
                boolean a = ((Map) parent).remove((MapLayer) item);
                a = false;
            } else if (parent instanceof Scene) {
                ((Scene) parent).removeLayer((Map3DLayer) item);
            } else if (parent instanceof GroupLayer) {
                boolean b = ((GroupLayer) parent).remove((MapLayer) item);
                b = false;
            } else if (parent instanceof Group3DLayer) {
                ((Group3DLayer) parent).removeLayer((Map3DLayer) item);
            }
            workspace.endUpdateTree();
            // 改用TryRefresh提高刷新效率
            MapControl mc = workspace.getMapControl(map);
            // TODO: 完成判断
            if (mc != null/* && ViewInfoHelp.GetSoonFresh(mc)*/) {
                mc.refreshWnd();
            }

            // endregion
        } else {
            this.fireMenuItemClick(menuItemClickEvent);
        }
    };
    private MultiMenuItemClickListener multiMenuItemClickListener = multiMenuItemClickEvent -> {
        Class<?> type = multiMenuItemClickEvent.getType();
        DocumentItem[] items = multiMenuItemClickEvent.getDocumentItem();
        if (VisibleLayerMenuItem.class.equals(type) || UnVisibleLayerMenuItem.class.equals(type)) {
            // region 可见/不可见

            // endregion
        } else if (MultiItemPropertyMenuItem.class.equals(type)) {

        } else if (RemovMapsScenesMenuItem.class.equals(type)) {
            // region 删除多个地图/场景
            if (items != null && items.length > 0) {
                workspace.beginUpdateTree();
                for (DocumentItem item : items) {
                    if (item instanceof Map && item.getParent() instanceof Document) {
                        Document doc = (Document) item.getParent();
                        Maps maps = doc.getMaps();
                        maps.remove(maps.indexOf((Map) item));
                    }
                    if (item instanceof Scene && item.getParent() instanceof Document) {
                        Document doc = (Document) item.getParent();
                        Scenes scenes = doc.getScenes();
                        scenes.removeScene(scenes.indexOfScene((Scene) item));
                    }
                }
                workspace.endUpdateTree();
            }
            // endregion
        } else if (RemovMapLayersMenuItem.class.equals(type)) {
            // region 删除多个二维图层
            if (items != null && items.length > 0) {
                workspace.beginUpdateTree();
                Map map = StaticFunction.getOwnerMap(items[0]);
                for (DocumentItem item : items) {
                    if (item.getParent() instanceof Map) {
                        ((Map) item.getParent()).remove((MapLayer) item);
                    } else if (item.getParent() instanceof GroupLayer) {
                        ((GroupLayer) item.getParent()).remove((MapLayer) item);
                    }
                }
                workspace.endUpdateTree();
                MapControl mc = workspace.getMapControl(map);
                if (mc != null)//&& ViewInfoHelp.GetSoonFresh(mc))
                {
                    mc.refreshWnd();
                }
            }
            // endregion
        } else if (RemovMap3DLayersMenuItem.class.equals(type)) {
            // region 删除多个三维图层
            if (items != null && items.length > 0) {
                workspace.beginUpdateTree();
                for (DocumentItem item : items) {
                    if (item.getParent() instanceof Scene) {
                        ((Scene) item.getParent()).removeLayer((Map3DLayer) item);
                    } else if (item.getParent() instanceof Group3DLayer) {
                        ((Group3DLayer) item.getParent()).removeLayer((Map3DLayer) item);
                    }
                }
                workspace.endUpdateTree();
            }
            // endregion
        } else {
            this.fireMultiMenuItemClick(multiMenuItemClickEvent);
        }
    };
    private UpdateTreeListener beginUpdateTreeListener = updateTreeEvent -> {
    };
    private UpdateTreeListener endUpdateTreeListener = updateTreeEvent -> {
    };
    // TODO: 待添加
    // private MapAttachMapControlListener

    // endregion

    private FocusedNodeChangedListener focusedNodeChangedListener = focusedNodeChangedEvent -> {
    };

    // region DocumentItem Event

    // TODO: 添加注释

    private ArrayList<OpeningDocumentListener> beforeOpeningDocumentListeners = new ArrayList<>();

    public void addBeforeOpeningDocumentListeners(OpeningDocumentListener beforeOpeningDocumentListener) {
        this.beforeOpeningDocumentListeners.add(beforeOpeningDocumentListener);
    }

    public void removeBeforeOpeningDocumentListeners(OpeningDocumentListener beforeOpeningDocumentListener) {
        this.beforeOpeningDocumentListeners.remove(beforeOpeningDocumentListener);
    }

    public void fireBeforeOpeningDocument(OpeningDocumentEvent beforeOpeningDocumentEvent) {
        for (OpeningDocumentListener beforeOpeningDocumentListener : this.beforeOpeningDocumentListeners) {
            beforeOpeningDocumentListener.fireOpeningDocument(beforeOpeningDocumentEvent);
        }
    }

    private ArrayList<OpeningDocumentListener> afterOpeningDocumentListeners = new ArrayList<>();

    public void addAfterOpeningDocumentListeners(OpeningDocumentListener afterOpeningDocumentListener) {
        this.afterOpeningDocumentListeners.add(afterOpeningDocumentListener);
    }

    public void removeAfterOpeningDocumentListeners(OpeningDocumentListener afterOpeningDocumentListener) {
        this.afterOpeningDocumentListeners.remove(afterOpeningDocumentListener);
    }

    public void fireAfterOpeningDocument(OpeningDocumentEvent afterOpeningDocumentEvent) {
        for (OpeningDocumentListener afterOpeningDocumentListener : this.afterOpeningDocumentListeners) {
            afterOpeningDocumentListener.fireOpeningDocument(afterOpeningDocumentEvent);
        }
    }

    private ArrayList<IInsertMapListener> insertMapListeners = new ArrayList<>();

    public void addInsertMapListener(IInsertMapListener insertMapListener) {
        this.insertMapListeners.add(insertMapListener);
    }

    public void removeInsertMapListener(IInsertMapListener insertMapListener) {
        this.insertMapListeners.remove(insertMapListener);
    }

    public void fireInsertMap(InsertMapEvent insertMapEvent) {
        for (IInsertMapListener insertMapListener : this.insertMapListeners) {
            insertMapListener.insertMap(insertMapEvent);
        }
    }

    private ArrayList<IRemoveMapListener> removeMapListeners = new ArrayList<>();

    public void addRemoveMapListener(IRemoveMapListener removeMapListener) {
        this.removeMapListeners.add(removeMapListener);
    }

    public void removeRemoveMapListener(IRemoveMapListener removeMapListener) {
        this.removeMapListeners.remove(removeMapListener);
    }

    public void fireRemoveMap(RemoveMapEvent removeMapEvent) {
        for (IRemoveMapListener removeMapListener : this.removeMapListeners) {
            removeMapListener.removeMap(removeMapEvent);
        }
    }

    // Scenes
    private ArrayList<IInsertSinceListener> insertSceneListeners = new ArrayList<>();

    public void addInsertSceneListener(IInsertSinceListener insertSceneListener) {
        this.insertSceneListeners.add(insertSceneListener);
    }

    public void removeInsertSceneListener(IInsertSinceListener insertSceneListener) {
        this.insertSceneListeners.remove(insertSceneListener);
    }

    public void fireInsertScene(InsertSinceEvent insertSceneEvent) {
        for (IInsertSinceListener insertSceneListener : this.insertSceneListeners) {
            insertSceneListener.insertSince(insertSceneEvent);
        }
    }

    private ArrayList<IRemoveSinceListener> removeSceneListeners = new ArrayList<>();

    public void addRemoveSceneListener(IRemoveSinceListener removeSceneListener) {
        this.removeSceneListeners.add(removeSceneListener);
    }

    public void removeRemoveSceneListener(IRemoveSinceListener removeSceneListener) {
        this.removeSceneListeners.remove(removeSceneListener);
    }

    public void fireRemoveScene(RemoveSinceEvent removeSceneEvent) {
        for (IRemoveSinceListener removeSceneListener : this.removeSceneListeners) {
            removeSceneListener.removeSince(removeSceneEvent);
        }
    }

    // Map
    private ArrayList<IInsertLayerListener> mapInsertLayerListeners = new ArrayList<>();

    public void addMapInsertLayerListener(IInsertLayerListener mapInsertLayerListener) {
        this.mapInsertLayerListeners.add(mapInsertLayerListener);
    }

    public void removeMapInsertLayerListener(IInsertLayerListener mapInsertLayerListener) {
        this.mapInsertLayerListeners.remove(mapInsertLayerListener);
    }

    public void fireMapInsertLayer(InsertLayerEvent mapInsertLayerEvent) {
        for (IInsertLayerListener mapInsertLayerListener : this.mapInsertLayerListeners) {
            mapInsertLayerListener.insertLayer(mapInsertLayerEvent);
        }
    }

    private ArrayList<IMoveLayerListener> mapMoveLayerListeners = new ArrayList<>();

    public void addMapMoveLayerListener(IMoveLayerListener mapMoveLayerListener) {
        this.mapMoveLayerListeners.add(mapMoveLayerListener);
    }

    public void removeMapMoveLayerListener(IMoveLayerListener mapMoveLayerListener) {
        this.mapMoveLayerListeners.remove(mapMoveLayerListener);
    }

    public void fireMapMoveLayer(MoveLayerEvent mapMoveLayerEvent) {
        for (IMoveLayerListener mapMoveLayerListener : this.mapMoveLayerListeners) {
            mapMoveLayerListener.moveLayer(mapMoveLayerEvent);
        }
    }

    private ArrayList<IMapPropertyChangedListener> mapPropertyChangedListeners = new ArrayList<>();

    public void addMapPropertyChangedListener(IMapPropertyChangedListener mapPropertyChangedListener) {
        this.mapPropertyChangedListeners.add(mapPropertyChangedListener);
    }

    public void removeMapPropertyChangedListener(IMapPropertyChangedListener mapPropertyChangedListener) {
        this.mapPropertyChangedListeners.remove(mapPropertyChangedListener);
    }

    public void fireMapPropertyChanged(MapPropertyChangedEvent mapPropertyChangedEvent) {
        for (IMapPropertyChangedListener mapPropertyChangedListener : this.mapPropertyChangedListeners) {
            mapPropertyChangedListener.mapPropertyChanged(mapPropertyChangedEvent);
        }
    }

    private ArrayList<IRemoveLayerListener> mapRemoveLayerListeners = new ArrayList<>();

    public void addMapRemoveLayerListener(IRemoveLayerListener mapRemoveLayerListener) {
        this.mapRemoveLayerListeners.add(mapRemoveLayerListener);
    }

    public void removeMapRemoveLayerListener(IRemoveLayerListener mapRemoveLayerListener) {
        this.mapRemoveLayerListeners.remove(mapRemoveLayerListener);
    }

    public void fireMapRemoveLayer(RemoveLayerEvent mapRemoveLayerEvent) {
        for (IRemoveLayerListener mapRemoveLayerListener : this.mapRemoveLayerListeners) {
            mapRemoveLayerListener.removeLayer(mapRemoveLayerEvent);
        }
    }

    // Scene
    private ArrayList<ISince3DInsertG3DLayerListener> sceneInsertG3DLayerListeners = new ArrayList<>();

    public void addSceneInsertG3DLayerListener(ISince3DInsertG3DLayerListener sceneInsertG3DLayerListener) {
        this.sceneInsertG3DLayerListeners.add(sceneInsertG3DLayerListener);
    }

    public void removeSceneInsertG3DLayerListener(ISince3DInsertG3DLayerListener sceneInsertG3DLayerListener) {
        this.sceneInsertG3DLayerListeners.remove(sceneInsertG3DLayerListener);
    }

    public void fireSceneInsertG3DLayer(Since3DInsertG3DLayerEvent sceneInsertG3DLayerEvent) {
        for (ISince3DInsertG3DLayerListener sceneInsertG3DLayerListener : this.sceneInsertG3DLayerListeners) {
            sceneInsertG3DLayerListener.insertLayer(sceneInsertG3DLayerEvent);
        }
    }

    private ArrayList<ISince3DRemoveG3DLayerListener> sceneRemoveG3DLayerListeners = new ArrayList<>();

    public void addSceneRemoveG3DLayerListener(ISince3DRemoveG3DLayerListener sceneRemoveG3DLayerListener) {
        this.sceneRemoveG3DLayerListeners.add(sceneRemoveG3DLayerListener);
    }

    public void removeSceneRemoveG3DLayerListener(ISince3DRemoveG3DLayerListener sceneRemoveG3DLayerListener) {
        this.sceneRemoveG3DLayerListeners.remove(sceneRemoveG3DLayerListener);
    }

    public void fireSceneRemoveG3DLayer(Since3DRemoveG3DLayerEvent sceneRemoveG3DLayerEvent) {
        for (ISince3DRemoveG3DLayerListener sceneRemoveG3DLayerListener : this.sceneRemoveG3DLayerListeners) {
            sceneRemoveG3DLayerListener.removeLayer(sceneRemoveG3DLayerEvent);
        }
    }

    private ArrayList<ISince3DMoveG3DLayerListener> sceneMoveG3DLayerListeners = new ArrayList<>();

    public void addSceneMoveG3DLayerListener(ISince3DMoveG3DLayerListener sceneMoveG3DLayerListener) {
        this.sceneMoveG3DLayerListeners.add(sceneMoveG3DLayerListener);
    }

    public void removeSceneMoveG3DLayerListener(ISince3DMoveG3DLayerListener sceneMoveG3DLayerListener) {
        this.sceneMoveG3DLayerListeners.remove(sceneMoveG3DLayerListener);
    }

    public void fireSceneMoveG3DLayer(Since3DMoveG3DLayerEvent sceneMoveG3DLayerEvent) {
        for (ISince3DMoveG3DLayerListener sceneMoveG3DLayerListener : this.sceneMoveG3DLayerListeners) {
            sceneMoveG3DLayerListener.moveLayer(sceneMoveG3DLayerEvent);
        }
    }

    private ArrayList<ISince3DPropertyChangedListener> scenePropertyChangedListeners = new ArrayList<>();

    public void addScenePropertyChangedListener(ISince3DPropertyChangedListener scenePropertyChangedListener) {
        this.scenePropertyChangedListeners.add(scenePropertyChangedListener);
    }

    public void removeScenePropertyChangedListener(ISince3DPropertyChangedListener scenePropertyChangedListener) {
        this.scenePropertyChangedListeners.remove(scenePropertyChangedListener);
    }

    public void fireScenePropertyChanged(Since3DPropertyChangedEvent scenePropertyChangedEvent) {
        for (ISince3DPropertyChangedListener scenePropertyChangedListener : this.scenePropertyChangedListeners) {
            scenePropertyChangedListener.since3DPropertyChanged(scenePropertyChangedEvent);
        }
    }

    // GroupLayer
    private ArrayList<IInsertLayerListener> groupLayerInsertLayerListeners = new ArrayList<>();

    public void addGroupLayerInsertLayerListener(IInsertLayerListener groupLayerInsertLayerListener) {
        this.groupLayerInsertLayerListeners.add(groupLayerInsertLayerListener);
    }

    public void removeGroupLayerInsertLayerListener(IInsertLayerListener groupLayerInsertLayerListener) {
        this.groupLayerInsertLayerListeners.remove(groupLayerInsertLayerListener);
    }

    public void fireGroupLayerInsertLayer(InsertLayerEvent groupLayerInsertLayerEvent) {
        for (IInsertLayerListener groupLayerInsertLayerListener : this.groupLayerInsertLayerListeners) {
            groupLayerInsertLayerListener.insertLayer(groupLayerInsertLayerEvent);
        }
    }

    private ArrayList<IMoveLayerListener> groupLayerMoveLayerListeners = new ArrayList<>();

    public void addGroupLayerMoveLayerListener(IMoveLayerListener groupLayerMoveLayerListener) {
        this.groupLayerMoveLayerListeners.add(groupLayerMoveLayerListener);
    }

    public void removeGroupLayerMoveLayerListener(IMoveLayerListener groupLayerMoveLayerListener) {
        this.groupLayerMoveLayerListeners.remove(groupLayerMoveLayerListener);
    }

    public void fireGroupLayerMoveLayer(MoveLayerEvent groupLayerMoveLayerEvent) {
        for (IMoveLayerListener groupLayerMoveLayerListener : this.groupLayerMoveLayerListeners) {
            groupLayerMoveLayerListener.moveLayer(groupLayerMoveLayerEvent);
        }
    }

    private ArrayList<IRemoveLayerListener> groupLayerRemoveLayerListeners = new ArrayList<>();

    public void addGroupLayerRemoveLayerListener(IRemoveLayerListener groupLayerRemoveLayerListener) {
        this.groupLayerRemoveLayerListeners.add(groupLayerRemoveLayerListener);
    }

    public void removeGroupLayerRemoveLayerListener(IRemoveLayerListener groupLayerRemoveLayerListener) {
        this.groupLayerRemoveLayerListeners.remove(groupLayerRemoveLayerListener);
    }

    public void fireGroupLayerRemoveLayer(RemoveLayerEvent groupLayerRemoveLayerEvent) {
        for (IRemoveLayerListener groupLayerRemoveLayerListener : this.groupLayerRemoveLayerListeners) {
            groupLayerRemoveLayerListener.removeLayer(groupLayerRemoveLayerEvent);
        }
    }

    // Layer
    private ArrayList<ILayerPropertyChangedListener> layerPropertyChangedListeners = new ArrayList<>();

    public void addLayerPropertyChangedListener(ILayerPropertyChangedListener layerPropertyChangedListener) {
        this.layerPropertyChangedListeners.add(layerPropertyChangedListener);
    }

    public void removeLayerPropertyChangedListener(ILayerPropertyChangedListener layerPropertyChangedListener) {
        this.layerPropertyChangedListeners.remove(layerPropertyChangedListener);
    }

    public void fireLayerPropertyChanged(LayerPropertyChangedEvent layerPropertyChangedEvent) {
        for (ILayerPropertyChangedListener layerPropertyChangedListener : this.layerPropertyChangedListeners) {
            layerPropertyChangedListener.layerPropertyChanged(layerPropertyChangedEvent);
        }
    }

    // Group3DLayer
    private ArrayList<IG3DGroupLayerInsertG3DLayerListener> group3DLayerInsertG3DLayerListeners = new ArrayList<>();

    public void addGroup3DLayerInsertG3DLayerListener(IG3DGroupLayerInsertG3DLayerListener group3DLayerInsertG3DLayerListener) {
        this.group3DLayerInsertG3DLayerListeners.add(group3DLayerInsertG3DLayerListener);
    }

    public void removeGroup3DLayerInsertG3DLayerListener(IG3DGroupLayerInsertG3DLayerListener group3DLayerInsertG3DLayerListener) {
        this.group3DLayerInsertG3DLayerListeners.remove(group3DLayerInsertG3DLayerListener);
    }

    public void fireGroup3DLayerInsertG3DLayer(G3DGroupLayerInsertG3DLayerEvent groupLayerInsertG3DLayerEvent) {
        for (IG3DGroupLayerInsertG3DLayerListener group3DLayerInsertG3DLayerListener : this.group3DLayerInsertG3DLayerListeners) {
            group3DLayerInsertG3DLayerListener.insertLayer(groupLayerInsertG3DLayerEvent);
        }
    }

    private ArrayList<IG3DGroupLayerRemoveG3DLayerListener> group3DLayerRemoveG3DLayerListeners = new ArrayList<>();

    public void addGroup3DLayerRemoveG3DLayerListener(IG3DGroupLayerRemoveG3DLayerListener group3DLayerRemoveG3DLayerListener) {
        this.group3DLayerRemoveG3DLayerListeners.add(group3DLayerRemoveG3DLayerListener);
    }

    public void removeGroup3DLayerRemoveG3DLayerListener(IG3DGroupLayerRemoveG3DLayerListener group3DLayerRemoveG3DLayerListener) {
        this.group3DLayerRemoveG3DLayerListeners.remove(group3DLayerRemoveG3DLayerListener);
    }

    public void fireGroup3DLayerRemoveG3DLayer(G3DGroupLayerRemoveG3DLayerEvent groupLayerRemoveG3DLayerEvent) {
        for (IG3DGroupLayerRemoveG3DLayerListener group3DLayerRemoveG3DLayerListener : this.group3DLayerRemoveG3DLayerListeners) {
            group3DLayerRemoveG3DLayerListener.removeLayer(groupLayerRemoveG3DLayerEvent);
        }
    }

    private ArrayList<IG3DGroupLayerMoveG3DLayerListener> group3DLayerMoveG3DLayerListeners = new ArrayList<>();

    public void addGroup3DLayerMoveG3DLayerListener(IG3DGroupLayerMoveG3DLayerListener group3DLayerMoveG3DLayerListener) {
        this.group3DLayerMoveG3DLayerListeners.add(group3DLayerMoveG3DLayerListener);
    }

    public void removeGroup3DLayerMoveG3DLayerListener(IG3DGroupLayerMoveG3DLayerListener group3DLayerMoveG3DLayerListener) {
        this.group3DLayerMoveG3DLayerListeners.remove(group3DLayerMoveG3DLayerListener);
    }

    public void fireGroup3DLayerMoveG3DLayer(G3DGroupLayerMoveG3DLayerEvent groupLayerMoveG3DLayerEvent) {
        for (IG3DGroupLayerMoveG3DLayerListener group3DLayerMoveG3DLayerListener : this.group3DLayerMoveG3DLayerListeners) {
            group3DLayerMoveG3DLayerListener.moveLayer(groupLayerMoveG3DLayerEvent);
        }
    }

    // G3DLayer
    private ArrayList<IG3DLayerPropertyChangedListener> g3dLayerPropertyChangedListeners = new ArrayList<>();

    public void addG3DLayerPropertyChangedListener(IG3DLayerPropertyChangedListener g3dLayerPropertyChangedListener) {
        this.g3dLayerPropertyChangedListeners.add(g3dLayerPropertyChangedListener);
    }

    public void removeG3DLayerPropertyChangedListener(IG3DLayerPropertyChangedListener g3dLayerPropertyChangedListener) {
        this.g3dLayerPropertyChangedListeners.remove(g3dLayerPropertyChangedListener);
    }

    public void fireG3DLayerPropertyChanged(G3DLayerPropertyChangedEvent g3dLayerPropertyChangedEvent) {
        for (IG3DLayerPropertyChangedListener g3dLayerPropertyChangedListener : this.g3dLayerPropertyChangedListeners) {
            g3dLayerPropertyChangedListener.g3DLayerPropertyChanged(g3dLayerPropertyChangedEvent);
        }
    }

    // endregion

    // region DocumentItem Listeners

    private ICreatedDocumentListener createdDocumentListener = createdDocumentEvent -> {
        initTreeWhileNewOrOpen(true);
    };
    private IOpenedDocumentListener openedDocumentListener = openedDocumentEvent -> {
        try {
            if (beforeOpeningDocumentListeners != null) {
                fireBeforeOpeningDocument(new OpeningDocumentEvent(this));
            }
            initTreeWhileNewOrOpen(true);
        } catch (Exception ex) {
            throw (ex);
        } finally {
            if (afterOpeningDocumentListeners != null) {
                fireAfterOpeningDocument(new OpeningDocumentEvent(this));
            }
        }
    };
    private IClosingDocumentListener closingDocumentListener = closingDocumentEvent -> {
        boolean cancel = closingDocumentEvent.getArgs().isCancel();
        // TODO: JNI接口需改写。
        Document document = (Document) closingDocumentEvent.getSource();
        if (!cancel && (document.getIsDirty() || isTempLayersChanged(document))) {
            ButtonType buttonType = MessageBox.questionEx("地图文档已被修改，是否保存？", Window.primaryStage, true);
            if (buttonType == ButtonType.YES) {
                cancel = saveDocument(document);
                closingDocumentEvent.getArgs().setCancel(cancel);
            } else if (buttonType == ButtonType.CANCEL) {
                closingDocumentEvent.getArgs().setCancel(true);
            } else if (buttonType == ButtonType.NO) {
//                    List<MapLayer> layer6xs = Get6xLayers(document);
//                    if (layer6xs != null)
//                    {
//                        for (MapLayer layer : layer6xs)
//                        {
//                            if (layer instanceof FileLayer6x)
//                            {
//                                FileLayer6x layer6x = (FileLayer6x)layer;
//                                layer6x.SaveOrGiveUpEdit(false);
//                            }
//                        }
//                    }
            }
        }
    };
    private IClosedDocumentListener closedDocumentListener = closedDocumentEvent -> {
        removeAllDocumentItemEvent();
        treeView.setRoot(null);
        treeItems.clear();

        closed = true;
        if (!updateing) {
            fireStateChanged(new StateChangedEvent(WorkspaceTree.this));
        }
    };
    private IDocumentPropertyChangedListener documentPropertyChangedListener = documentPropertyChangedEvent -> {
    };

    // Maps
    private IInsertMapListener insertMapListener = insertMapEvent -> {
        if (treeView.getRoot() == null) {
            initTreeWhileNewOrOpen(false);
        } else {
            Map map = insertMapEvent.getArgs().getInsertedMap();
            if (map != null) {
                TreeItem<Object> docTreeItem = treeView.getRoot();
                TreeItem<Object> mapTreeItem = initMap(map, docTreeItem);
                if (mapTreeItem != null) {
                    docTreeItem.setExpanded(true);
                    if (treeView.getFocusModel().getFocusedItem() != mapTreeItem) {
                        treeView.getFocusModel().focus(treeView.getRow(mapTreeItem));
                    }
                    setParentNodeShowState(docTreeItem);
                }
                //this.treeList.Update();
            }
        }
        if (insertMapListeners != null) {
            fireInsertMap(new InsertMapEvent(insertMapEvent.getSource(), insertMapEvent.getArgs()));
        }
        if (!updateing) {
            fireStateChanged(new StateChangedEvent(WorkspaceTree.this));
        }

    };
    private IRemoveMapListener removeMapListener = removeMapEvent -> {
        Map map = removeMapEvent.getArgs().getRemovedMap();
        if (map != null) {
            this.removeDocumentItemListener(map);
            if (this.treeView.getRoot() != null) {
                TreeItem<Object> docTreeItem = this.treeView.getRoot();
                docTreeItem.getChildren().remove(this.getMapTreeItemByMap(map));
                this.treeItems.remove(map.getHandle());
                this.setParentNodeShowState(docTreeItem);
            }
        }
        if (this.removeMapListeners != null) {
            this.fireRemoveMap(new RemoveMapEvent(this, removeMapEvent.getArgs()));
        }
        if (!this.updateing) {
            this.fireStateChanged(new StateChangedEvent(this));
        }
    };

    // Scenes
    private IInsertSinceListener insertSceneListener = insertSceneEvent -> {
        if (treeView.getRoot() == null) {
            this.initTreeWhileNewOrOpen(false);
        } else {
            Scene scene = insertSceneEvent.getArgs().getInsertedScene();
            if (scene != null) {
                TreeItem<Object> docTreeItem = treeView.getRoot();
                TreeItem<Object> sceneTreeItem = initScene(scene, docTreeItem);
                if (sceneTreeItem != null) {
                    docTreeItem.setExpanded(true);
                    if (treeView.getFocusModel().getFocusedItem() != sceneTreeItem) {
                        treeView.getFocusModel().focus(treeView.getRow(sceneTreeItem));
                    }
                    setParentNodeShowState(docTreeItem);
                }
            }
        }
        if (insertSceneListeners != null) {
            fireInsertScene(new InsertSinceEvent(insertSceneEvent.getSource(), insertSceneEvent.getArgs()));
        }
        if (!updateing) {
            fireStateChanged(new StateChangedEvent(WorkspaceTree.this));
        }
    };
    private IRemoveSinceListener removeSceneListener = removeSceneEvent -> {
        Scene scene = removeSceneEvent.getArgs().getRemovedScene();
        if (scene != null) {
            this.removeDocumentItemListener(scene);
            if (this.treeView.getRoot() != null) {
                TreeItem<Object> docTreeItem = this.treeView.getRoot();
                docTreeItem.getChildren().remove(this.getSceneTreeItemByScene(scene));
                this.treeItems.remove(scene.getHandle());
                this.setParentNodeShowState(docTreeItem);
            }
        }
        if (this.removeSceneListeners != null) {
            this.fireRemoveScene(new RemoveSinceEvent(this, removeSceneEvent.getArgs()));
        }
        if (!this.updateing) {
            this.fireStateChanged(new StateChangedEvent(this));
        }
    };

    // Map
    private IInsertLayerListener mapInsertLayerListener = insertLayerEvent -> {
        if (insertLayerEvent.getSource() instanceof Map) {
            Map map = (Map) insertLayerEvent.getSource();
            TreeItem<Object> mapTreeItem = this.getMapTreeItemByMap(map);
            this.appendMapLayer(insertLayerEvent.getArgs().getInsertedLayer(), insertLayerEvent.getArgs().getIndex(), mapTreeItem);
            this.setParentNodeShowState(mapTreeItem);
            if (this.mapInsertLayerListeners != null) {
                this.fireMapInsertLayer(new InsertLayerEvent(insertLayerEvent.getSource(), insertLayerEvent.getArgs()));
            }
            if (!this.updateing) {
                this.fireStateChanged(new StateChangedEvent(this));
            }
        }
    };
    private IRemoveLayerListener mapRemoveLayerListener = removeLayerEvent -> {
        if (removeLayerEvent.getSource() instanceof Map) {
            Map map = (Map) removeLayerEvent.getSource();
            MapLayer layer = removeLayerEvent.getArgs().getRemovedLayer();
            this.removeDocumentItemListener(layer);
            TreeItem<Object> mapTreeItem = this.getMapTreeItemByMap(map);
            if (mapTreeItem != null) {
                TreeItem<Object> layerTreeItem = this.getTreeItemByDocumentItem(layer, mapTreeItem);
                if (layerTreeItem != null) {
                    mapTreeItem.getChildren().remove(layerTreeItem);
                    this.treeItems.remove(layer.getHandle());
                    this.setParentNodeShowState(mapTreeItem);
                }
            }
            if (layer instanceof FileLayer6x) {
                // TODO: 待添加接口
//                Object obj = layer.GetProperty("IsDeleteLayer");
//                if (obj instanceof Boolean && (boolean) obj) {
//                    if ((layer.GetDirtyFlag() & 0x10) != 0x00000) {
//                        if (MessageBox.questionEx("6x图层<" + layer.getName() + ">数据发生改变，是否要对其进行保存？") == ButtonType.YES) {
//                            ((FileLayer6x) layer).SaveOrGiveUpEdit(true);
//                        } else {
//                            ((FileLayer6x) layer).SaveOrGiveUpEdit(false);
//                        }
//                    }
//                }
            }
        }
        if (this.mapRemoveLayerListeners != null && !this.dragingNode) {
            this.fireMapRemoveLayer(new RemoveLayerEvent(removeLayerEvent.getSource(), removeLayerEvent.getArgs()));
        }
        if (!this.updateing) {
            this.fireStateChanged(new StateChangedEvent(this));
        }
    };
    private IMoveLayerListener mapMoveLayerListener = moveLayerEvent -> {
        if (moveLayerEvent.getSource() instanceof Map) {
            Map map = (Map) moveLayerEvent.getSource();
            MapLayer moveLayer = null;
            if (moveLayerEvent.getArgs().getToIndex() == -1) {
                moveLayer = map.getLayer(map.getLayerCount() - 1);
            } else {
                moveLayer = map.getLayer(moveLayerEvent.getArgs().getToIndex());
            }
            TreeItem<Object> treeItem = this.getTreeItemByDocumentItem(moveLayer, this.getMapTreeItemByMap(map));
            if (treeItem != null) {
                // TODO: 未完成
//                boolean temp = this.isSetNodeIndexing;
//                this.isSetNodeIndexing = true;
//                if (moveLayerEvent.getArgs().getToIndex() == -1) {
//                    this.treeList.SetNodeIndex(trNode, map.LayerCount - 1);
//                } else {
//                    this.treeList.SetNodeIndex(trNode, moveLayerEvent.getArgs().getToIndex());
//                }
//                this.isSetNodeIndexing = temp;
            }
        }
        if (this.mapMoveLayerListeners != null) {
            this.fireMapMoveLayer(new MoveLayerEvent(moveLayerEvent.getSource(), moveLayerEvent.getArgs()));
        }
    };
    private IMapPropertyChangedListener mapPropertyChangedListener = mapPropertyChangedEvent -> {
        if (mapPropertyChangedEvent.getArgs().getPropertyName().toLowerCase().equals("mapname")) {
            // 更新节点名称和视图标题
            if (causeLayerStateChanged) {
                Map map = mapPropertyChangedEvent.getArgs().getMap();
                if (map != null) {
                    TreeItem<Object> mapTreeItem = getMapTreeItemByMap(map);
                    // TODO: 是否需要手动更新
                    if (mapTreeItem != null) {
                        mapTreeItem.setValue(map);
                    }
                    // 截获属性改变，以及时刷新界面
//                    String name = map.getName();
//                    DocumentItemProperty dp = DocumentItemProperties.DocItemProperty;
//                    if (dp != null && !this.isPropertyAppling) {
//                        if (dp.Item != null && map.getHandle() == dp.Item.Handle) {
//                            // 地图属性界面控制
//                            bool temp = this.isUpdateing;
//                            this.isCauseLayerStateChanged = false;
//                            this.isUpdateing = true;
//                            map.setName(name);
//                            DocumentItemProperties.ShowProperty(dp.Item, false);
//                            this.isUpdateing = false;
//                            this.isCauseLayerStateChanged = true;
//                            this.isUpdateing = temp;
//                        }
//                    }
                }
            }
        } else if (mapPropertyChangedEvent.getArgs().getPropertyName().toLowerCase().equals("expand")) {
            // 更新节点展开属性
            Map map = mapPropertyChangedEvent.getArgs().getMap();
            if (map != null) {
                TreeItem<Object> layerTreeItem = this.getTreeItemByDocumentItem(map, this.treeView.getRoot());
                if (layerTreeItem != null) {
                    TreeItem<Object> focusedTreeItem = this.treeView.getFocusModel().getFocusedItem();
                    String expand = (String) mapPropertyChangedEvent.getArgs().getPropertyValue();
                    layerTreeItem.setExpanded("true".equals(expand));
                    this.removeFocusedNodeChangedListener(focusedNodeChangedListener);
                    this.treeView.getFocusModel().focus(this.treeView.getRow(focusedTreeItem));
                    this.addFocusedNodeChangedListener(focusedNodeChangedListener);
                }
            }
        }
        if (mapPropertyChangedListeners != null) {
            fireMapPropertyChanged(new MapPropertyChangedEvent(mapPropertyChangedEvent.getSource(), mapPropertyChangedEvent.getArgs()));
        }
    };

    // Scene
    private ISince3DInsertG3DLayerListener sceneInsertG3DLayerListener = insertG3DLayerEvent -> {
        if (insertG3DLayerEvent.getSource() instanceof Scene) {
            Scene scene = (Scene) insertG3DLayerEvent.getSource();
            TreeItem<Object> sceneTreeItem = this.getSceneTreeItemByScene(scene);
            this.appendG3DLayer(insertG3DLayerEvent.getArgs().getInsertedLayer(), insertG3DLayerEvent.getArgs().getIndex(), sceneTreeItem);
            this.setParentNodeShowState(sceneTreeItem);

            if (insertG3DLayerEvent.getArgs().getInsertedLayer() instanceof ServerLayer) {
                SceneControl sceneControl = this.workspace.getSceneControl(scene);
                // TODO: 补充
                // WorkSpaceTree.UpdateServer3DLayerInScene(sc, scene);
            }
        }
        if (this.sceneInsertG3DLayerListeners != null) {
            this.fireSceneInsertG3DLayer(new Since3DInsertG3DLayerEvent(insertG3DLayerEvent.getSource(), insertG3DLayerEvent.getArgs()));
        }
        if (!this.updateing) {
            this.fireStateChanged(new StateChangedEvent(this));
        }
    };
    private ISince3DRemoveG3DLayerListener sceneRemoveG3DLayerListener = removeG3DLayerEvent -> {
        if (removeG3DLayerEvent.getSource() instanceof Scene) {
            Scene scene = (Scene) removeG3DLayerEvent.getSource();
            Map3DLayer g3dLayer = removeG3DLayerEvent.getArgs().getRemovedLayer();
            this.removeDocumentItemListener(g3dLayer);
            TreeItem<Object> sceneTreeItem = this.getSceneTreeItemByScene(scene);
            if (sceneTreeItem != null) {
                TreeItem<Object> g3dLayerTreeItem = this.getTreeItemByDocumentItem(g3dLayer, sceneTreeItem);
                if (g3dLayerTreeItem != null) {
                    sceneTreeItem.getChildren().remove(g3dLayerTreeItem);
                    this.treeItems.remove(g3dLayer.getHandle());
                    this.setParentNodeShowState(sceneTreeItem);
                }
            }
        }
        if (this.sceneRemoveG3DLayerListeners != null && !this.dragingNode) {
            this.fireSceneRemoveG3DLayer(new Since3DRemoveG3DLayerEvent(removeG3DLayerEvent.getSource(), removeG3DLayerEvent.getArgs()));
        }
        if (!this.updateing) {
            this.fireStateChanged(new StateChangedEvent(this));
        }
    };
    private ISince3DMoveG3DLayerListener sceneMoveG3DLayerListener = moveG3DLayerEvent -> {
        if (moveG3DLayerEvent.getSource() instanceof Scene) {
            Scene scene = (Scene) moveG3DLayerEvent.getSource();
            Map3DLayer moveLayer = null;
            if (moveG3DLayerEvent.getArgs().getToIndex() == -1) {
                moveLayer = scene.getLayer(scene.getLayerCount() - 1);
            } else {
                moveLayer = scene.getLayer(moveG3DLayerEvent.getArgs().getToIndex());
            }
            TreeItem<Object> moveTreeItem = this.getTreeItemByDocumentItem(moveLayer, this.getSceneTreeItemByScene(scene));
            if (moveTreeItem != null) {
//                boolean temp = this.isSetNodeIndexing;
//                this.isSetNodeIndexing = true;
//                if (moveG3DLayerEvent.getArgs().getToIndex() == -1)
//                    this.treeList.SetNodeIndex(moveTreeItem, scene.getLayerCount() - 1);
//                else
//                    this.treeList.SetNodeIndex(moveTreeItem, moveG3DLayerEvent.getArgs().getToIndex());
//                this.isSetNodeIndexing = temp;

                if (moveLayer instanceof ServerLayer) {
                    SceneControl sc = this.workspace.getSceneControl(scene);
//                    WorkspaceTree.UpdateServer3DLayerInScene(sc, scene);
                }
            }
        }
        if (this.sceneMoveG3DLayerListeners != null) {
            this.fireSceneMoveG3DLayer(new Since3DMoveG3DLayerEvent(moveG3DLayerEvent.getSource(), moveG3DLayerEvent.getArgs()));
        }
    };

    // GroupLayer
    private IInsertLayerListener groupLayerInsertLayerListener = insertLayerEvent -> {
        if (insertLayerEvent.getSource() instanceof GroupLayer) {
            GroupLayer group = (GroupLayer) insertLayerEvent.getSource();
            TreeItem<Object> groupLayerTreeItem = this.getTreeItemByDocumentItem(group, treeView.getRoot());
            this.appendMapLayer(insertLayerEvent.getArgs().getInsertedLayer(), insertLayerEvent.getArgs().getIndex(), groupLayerTreeItem);
            this.setParentNodeShowState(groupLayerTreeItem);
        }
        if (this.groupLayerInsertLayerListeners != null) {
            this.fireGroupLayerInsertLayer(new InsertLayerEvent(insertLayerEvent.getSource(), insertLayerEvent.getArgs()));
        }
        if (!this.updateing) {
            this.fireStateChanged(new StateChangedEvent(this));
        }
    };
    private IRemoveLayerListener groupLayerRemoveLayerListener = removeLayerEvent -> {
        if (removeLayerEvent.getSource() instanceof GroupLayer && this.treeView.getRoot() != null) {
            GroupLayer groupLayer = (GroupLayer) removeLayerEvent.getSource();
            MapLayer mapLayer = removeLayerEvent.getArgs().getRemovedLayer();
            this.removeDocumentItemListener(mapLayer);
            TreeItem<Object> groupTreeItem = this.getTreeItemByDocumentItem(groupLayer, this.treeView.getRoot());
            if (groupTreeItem != null) {
                TreeItem<Object> layerTreeItem = this.getTreeItemByDocumentItem(mapLayer, groupTreeItem);
                if (layerTreeItem != null) {
                    groupTreeItem.getChildren().remove(layerTreeItem);
                    this.treeItems.remove(removeLayerEvent.getArgs().getRemovedLayer().getHandle());
                    this.setParentNodeShowState(groupTreeItem);
                }
            }
            if (mapLayer instanceof FileLayer6x) {
                // TODO: 待添加接口
//                Object obj = mapLayer.GetProperty("IsDeleteLayer");
//                if (obj instanceof Boolean && (boolean) obj) {
//                    if ((mapLayer.GetDirtyFlag() & 0x10) != 0x00000) {
//                        if (MessageBox.questionEx("6x图层<" + mapLayer.getName() + ">数据发生改变，是否要对其进行保存？") == ButtonType.YES) {
//                            ((FileLayer6x) mapLayer).SaveOrGiveUpEdit(true);
//                        } else {
//                            ((FileLayer6x) mapLayer).SaveOrGiveUpEdit(false);
//                        }
//                    }
//                }
            }
        }
        if (this.groupLayerRemoveLayerListeners != null && !this.dragingNode) {
            this.fireGroupLayerRemoveLayer(new RemoveLayerEvent(removeLayerEvent.getSource(), removeLayerEvent.getArgs()));
        }
        if (!this.updateing) {
            this.fireStateChanged(new StateChangedEvent(this));
        }
    };
    private IMoveLayerListener groupLayerMoveLayerListener = moveLayerEvent -> {
        if (moveLayerEvent.getSource() instanceof GroupLayer && this.treeView.getRoot() != null) {
            GroupLayer groupLayer = (GroupLayer) moveLayerEvent.getSource();
            MapLayer moveLayer = null;
            if (moveLayerEvent.getArgs().getToIndex() == -1) {
                moveLayer = groupLayer.item(groupLayer.getCount() - 1);
            } else {
                moveLayer = groupLayer.item(moveLayerEvent.getArgs().getToIndex());
            }
            TreeItem<Object> treeItem = this.getTreeItemByDocumentItem(moveLayer, this.treeView.getRoot());
            if (treeItem != null) {
                // TODO: 未完成
//                boolean temp = this.isSetNodeIndexing;
//                this.isSetNodeIndexing = true;
//                if (moveLayerEvent.getArgs().getToIndex() == -1) {
//                    this.treeList.SetNodeIndex(treeItem, groupLayer.getCount() - 1);
//                } else {
//                    this.treeList.SetNodeIndex(treeItem, moveLayerEvent.getArgs().getToIndex());
//                }
//                this.isSetNodeIndexing = temp;
            }
        }
        if (this.groupLayerMoveLayerListeners != null) {
            this.fireGroupLayerMoveLayer(new MoveLayerEvent(moveLayerEvent.getSource(), moveLayerEvent.getArgs()));
        }
    };

    // Group3DLayer
    private IG3DGroupLayerInsertG3DLayerListener group3DLayerInsertG3DLayerListener = insertG3DLayerEvent -> {
        if (insertG3DLayerEvent.getSource() instanceof Group3DLayer) {
            Group3DLayer group3DLayer = (Group3DLayer) insertG3DLayerEvent.getSource();
            TreeItem<Object> group3DTreeItem = this.getTreeItemByDocumentItem(group3DLayer, this.treeView.getRoot());
            this.appendG3DLayer(insertG3DLayerEvent.getArgs().getInsertedLayer(), insertG3DLayerEvent.getArgs().getIndex(), group3DTreeItem);
            this.setParentNodeShowState(group3DTreeItem);
            if (insertG3DLayerEvent.getArgs().getInsertedLayer() instanceof ServerLayer) {
                Scene scene = this.getScene(insertG3DLayerEvent.getArgs().getInsertedLayer());
                SceneControl sc = this.workspace.getSceneControl(scene);
//                WorkspaceTree.UpdateServer3DLayerInScene(sc, scene);
            }
        }
        if (this.group3DLayerInsertG3DLayerListeners != null) {
            this.fireGroup3DLayerInsertG3DLayer(new G3DGroupLayerInsertG3DLayerEvent(insertG3DLayerEvent.getSource(), insertG3DLayerEvent.getArgs()));
        }
        if (!this.updateing) {
            this.fireStateChanged(new StateChangedEvent(this));
        }
    };
    private IG3DGroupLayerRemoveG3DLayerListener group3DLayerRemoveG3DLayerListener = removeG3DLayerEvent -> {
        if (removeG3DLayerEvent.getSource() instanceof Group3DLayer && this.treeView.getRoot() != null) {
            Group3DLayer group3DLayer = (Group3DLayer) removeG3DLayerEvent.getSource();
            Map3DLayer g3dLayer = removeG3DLayerEvent.getArgs().getRemovedLayer();
            this.removeDocumentItemListener(g3dLayer);
            TreeItem<Object> group3DTreeItem = this.getTreeItemByDocumentItem(group3DLayer, this.treeView.getRoot());
            if (group3DTreeItem != null) {
                TreeItem<Object> layer3DTrNode = this.getTreeItemByDocumentItem(g3dLayer, group3DTreeItem);
                if (layer3DTrNode != null) {
                    group3DTreeItem.getChildren().remove(layer3DTrNode);
                    this.treeItems.remove(removeG3DLayerEvent.getArgs().getRemovedLayer().getHandle());
                    this.setParentNodeShowState(group3DTreeItem);
                }
            }
        }
        if (this.group3DLayerRemoveG3DLayerListeners != null && !this.dragingNode) {
            this.fireGroup3DLayerRemoveG3DLayer(new G3DGroupLayerRemoveG3DLayerEvent(removeG3DLayerEvent.getSource(), removeG3DLayerEvent.getArgs()));
        }
        if (!this.updateing) {
            this.fireStateChanged(new StateChangedEvent(this));
        }
    };
    private IG3DGroupLayerMoveG3DLayerListener group3DLayerMoveG3DLayerListener = moveG3DLayerEvent -> {
        if (moveG3DLayerEvent.getSource() instanceof Group3DLayer && this.treeView.getRoot() != null) {
            Group3DLayer group3DLayer = (Group3DLayer) moveG3DLayerEvent.getSource();
            Map3DLayer moveLayer = group3DLayer.getLayer(moveG3DLayerEvent.getArgs().getToIndex());
            TreeItem<Object> g3dLayerTreeItem = this.getTreeItemByDocumentItem(moveLayer, this.treeView.getRoot());
            if (g3dLayerTreeItem != null) {
//                boolean temp = this.isSetNodeIndexing;
//                this.isSetNodeIndexing = true;
//                this.treeList.SetNodeIndex(g3dLayerTreeItem, e.ToIndex);
//                this.isSetNodeIndexing = temp;

                if (moveLayer instanceof ServerLayer) {
                    Scene scene = this.getScene(moveLayer);
                    SceneControl sc = this.workspace.getSceneControl(scene);
//                    WorkspaceTree.UpdateServer3DLayerInScene(sc, scene);
                }
            }
        }
        if (this.group3DLayerMoveG3DLayerListeners != null) {
            this.fireGroup3DLayerMoveG3DLayer(new G3DGroupLayerMoveG3DLayerEvent(moveG3DLayerEvent.getSource(), moveG3DLayerEvent.getArgs()));
        }
    };

    // Layer
    private ILayerPropertyChangedListener layerPropertyChangedListener = layerPropertyChangedEvent -> {
        String propertyName = layerPropertyChangedEvent.getArgs().getPropertyName();
        Object propertyValue = layerPropertyChangedEvent.getArgs().getPropertyValue();
        if (propertyName.equals("State")) {
            if (this.causeLayerStateChanged) {
                // region 更新节点状态
                MapLayer mapLayer = (MapLayer) layerPropertyChangedEvent.getSource();
                TreeItem<Object> layerTreeItem = getTreeItemByDocumentItem(mapLayer, this.treeView.getRoot());
                if (layerTreeItem != null) {
                    setDisplayState(layerTreeItem, getDisplayState(mapLayer.getState()));
                    //refreshDocumentItem(mapLayer);
                    treeView.refresh();

                    // TODO: 截获属性改变，以及时刷新界面 未完成
//                    // region 截获属性改变，以及时刷新界面
//                    LayerState layerState = mapLayer.getState();
//                    DocumentItemProperty dp = DocumentItemProperties.DocItemProperty;
//                    if (dp != null && !this.isPropertyAppling) {
//                        if (dp.Item != null && mapLayer.getHandle() == dp.Item.Handle) {
//                            // region 单图层属性界面控制
//                            boolean temp = this.updateing;
//                            this.causeLayerStateChanged = false;
//                            this.updateing = true;
//                            mapLayer.setState(layerState);
//                            DocumentItemProperties.ShowProperty(dp.Item, false);
//                            this.updateing = false;
//                            this.causeLayerStateChanged = true;
//                            this.updateing = temp;
//                            // endregion
//                        }
//                    }
//                    // endregion

                    setParentNodeShowState(layerTreeItem.getParent());
                    // TODO: 向下设置图标
//                    this.SetSubNodeImage(layerTreeItem);
                    if (mapLayer instanceof VectorLayer) {
                        // 当图层不可见时，将其专题图全设为不可见。
//                        if (mapLayer.getState() == LayerState.UnVisible && mapLayer.getThemes() != null && mapLayer.getThemes().getCount() > 0) {
//                            for (TreeItem<Object> treeItem : layerTreeItem.getChildren()) {
//                                Theme theme = (Theme) treeItem.getValue();
//                                if (theme != null) {
//                                    theme.setVisible(false);
//                                    setDisplayState(treeItem, DisplayState.UNVISIBLE);
//                                    if (theme instanceof UniqueTheme || theme instanceof MultiClassTheme) {
//                                        //将单值专题图和分段专题图的子节点设为不可见
//                                        for (int i = 0; i < treeItem.getChildren().size(); i++) {
//                                            TreeItem<Object> childTreeItem = treeItem.getChildren().get(i);
//                                            ThemeInfo themeInfo = this.refreshThemeInfo(childTreeItem);
//                                            if (themeInfo != null) {
//                                                themeInfo.setIsVisible(false);
//                                                this.pushThemeInfo(childTreeItem);
//                                                setDisplayState(childTreeItem, DisplayState.UNVISIBLE);
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
                    }
                }
                // endregion
            }
            if (!this.updateing) {
                fireStateChanged(new StateChangedEvent(this));
            }
        } else if (propertyName.equals("LayerName")) {
            // region 更新节点名称
            if (this.causeLayerStateChanged) {
                MapLayer mapLayer = (MapLayer) layerPropertyChangedEvent.getSource();
                TreeItem<Object> layerTreeItem = getTreeItemByDocumentItem(mapLayer, this.treeView.getRoot());
                if (layerTreeItem != null) {
                    layerTreeItem.setValue(mapLayer);
                }

                // TODO: 截获属性改变，以及时刷新界面 未完成
//                // region 截获属性改变，以及时刷新界面
//                String name = mapLayer.getName();
//                DocumentItemProperty dp = DocumentItemProperties.DocItemProperty;
//                if (dp != null && !this.isPropertyAppling) {
//                    if (dp.Item != null && mapLayer.getHandle() == dp.Item.Handle) {
//                        // region 单图层属性界面控制
//                        boolean temp = this.updateing;
//                        this.causeLayerStateChanged = false;
//                        this.updateing = true;
//                        mapLayer.setName(name);
//                        DocumentItemProperties.ShowProperty(dp.Item, false);
//                        this.updateing = false;
//                        this.causeLayerStateChanged = true;
//                        this.updateing = temp;
//                        // endregion
//                    }
//                }
                // endregion
            }
            // endregion
        } else if (propertyName.toLowerCase().equals("visible")) {
            // region 更新节点可见性
            MapLayer mapLayer = (MapLayer) layerPropertyChangedEvent.getSource();
            TreeItem<Object> layerTreeItem = getTreeItemByDocumentItem(mapLayer, this.treeView.getRoot());
            if (layerTreeItem != null) {
                String visible = (String) propertyValue;
                // TODO: setVisible 考虑是否用图标表示状态
//                if (visible.compareToIgnoreCase("false") == 0) {
//                    layerTreeItem.Visible = false;
//                } else {
//                    layerTreeItem.Visible = true;
//                }
            }
            // endregion
            if (!this.updateing) {
                fireStateChanged(new StateChangedEvent(this));
            }
        } else if (propertyName.toLowerCase().equals("expand")) {
            // region 更新节点展开属性
            MapLayer mapLayer = (MapLayer) layerPropertyChangedEvent.getSource();
            TreeItem<Object> layerTreeItem = this.getTreeItemByDocumentItem(mapLayer, this.treeView.getRoot());
            if (layerTreeItem != null) {
                TreeItem<Object> fcsNode = this.treeView.getFocusModel().getFocusedItem();
                String expand = (String) propertyValue;
                layerTreeItem.setExpanded(expand.compareToIgnoreCase("true") == 0);
                this.treeView.removeFocusedNodeChangedListener(focusedNodeChangedListener);
                this.treeView.getFocusModel().focus(this.treeView.getRow(fcsNode));
                this.treeView.addFocusedNodeChangedListener(focusedNodeChangedListener);
            }
            // endregion
        } else if (propertyName.toLowerCase().equals("systemlibrary")) {
            // region 更新节点的系统库属性
            VectorLayer vectorLayer = (VectorLayer) layerPropertyChangedEvent.getSource();
            TreeItem<Object> layerTreeItem = this.getTreeItemByDocumentItem(vectorLayer, this.treeView.getRoot());
            if (vectorLayer != null && layerTreeItem != null) {
                SystemLibrary sysLibrary = vectorLayer.getSysLibrary();
                for (TreeItem<Object> treeItem : layerTreeItem.getChildren()) {
                    if (treeItem.getValue() instanceof Theme) {
                        if (treeItem.getValue() instanceof SimpleTheme) {
                            this.SetNodeImage(treeItem, ((SimpleTheme) treeItem.getValue()).getThemeInfo().getGeoInfo(), sysLibrary.getGuid());
                        }
                        for (TreeItem<Object> subTreeItem : treeItem.getChildren()) {
                            ThemeInfo thInfo = this.refreshThemeInfo(subTreeItem);
                            if (thInfo != null) {
                                this.SetNodeImage(subTreeItem, thInfo.getGeoInfo(), sysLibrary.getGuid());
                            }
                        }
                    }
                }
                // TODO: 未完成
                //this.InnerRefreshMapView(vectorLayer);
            }
            // endregion
        }
        if (this.layerPropertyChangedListeners != null) {
            fireLayerPropertyChanged(new LayerPropertyChangedEvent(layerPropertyChangedEvent.getSource(), layerPropertyChangedEvent.getArgs()));
        }
    };
    // G3DLayer
    private IG3DLayerPropertyChangedListener g3dLayerPropertyChangedListener = g3dLayerPropertyChangedEvent -> {
        String propertyName = g3dLayerPropertyChangedEvent.getArgs().getPropertyName();
        Object propertyValue = g3dLayerPropertyChangedEvent.getArgs().getPropertyValue();
        if (propertyName.equals("State")) {
            if (this.causeLayerStateChanged) {
                // region 更新节点状态
                Map3DLayer layer = (Map3DLayer) g3dLayerPropertyChangedEvent.getSource();
                TreeItem<Object> layerTreeItem = getTreeItemByDocumentItem(layer, this.treeView.getRoot());
                if (layerTreeItem != null) {
                    setDisplayState(layerTreeItem, getDisplayState(layer.getState()));
                    treeView.refresh();

//                    // region 截获属性改变，以及时刷新界面
//                    LayerState ls = layer.State;
//                    DocumentItemProperty dp = DocumentItemProperties.DocItemProperty;
//                    if (dp != null && !this.isPropertyAppling) {
//                        if (dp.Item != null && layer.Handle == dp.Item.Handle) {
//                            // region 单图层属性界面控制
//                            bool temp = this.isUpdateing;
//                            this.isCauseLayerStateChanged = false;
//                            this.isUpdateing = true;
//                            layer.State = ls;
//                            DocumentItemProperties.ShowProperty(dp.Item, false);
//                            this.isUpdateing = false;
//                            this.isCauseLayerStateChanged = true;
//                            this.isUpdateing = temp;
//                            // endregion
//                        }
//                    }
//                    // endregion

                    setParentNodeShowState(layerTreeItem.getParent());
//                    this.SetSubNodeImage(layerTreeItem);
                    if (layer instanceof Vector3DLayer) {
                        // 当图层不可见时，将其专题图全设为不可见。
                        if (layer.getState() == LayerState.UnVisible && ((Vector3DLayer) layer).get3DThemes() != null && ((Vector3DLayer) layer).get3DThemes().getCount() > 0) {
                            for (TreeItem<Object> treeItem : layerTreeItem.getChildren()) {
                                if (treeItem.getValue() instanceof Theme3D) {
                                    Theme3D theme = (Theme3D) treeItem.getValue();
                                    theme.setVisible(false);
                                    setDisplayState(treeItem, DisplayState.UNVISIBLE);
                                    if (theme instanceof UniqueTheme3D || theme instanceof MultiClassTheme3D) {
                                        //将单值专题图和分段专题图的子节点设为不可见
                                        for (int i = 0; i < treeItem.getChildren().size(); i++) {
                                            TreeItem<Object> childTreeItem = treeItem.getChildren().get(i);
                                            ThemeInfo themeInfo = refreshThemeInfo(childTreeItem);
                                            if (themeInfo != null) {
                                                themeInfo.setIsVisible(false);
                                                pushThemeInfo(childTreeItem);
                                                setDisplayState(childTreeItem, DisplayState.UNVISIBLE);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // endregion
            }
            if (!this.updateing) {
                fireStateChanged(new StateChangedEvent(this));
            }
        } else if (propertyName.equals("LayerName")) {
            // region 更新节点名称
            if (this.causeLayerStateChanged) {
                Map3DLayer layer = (Map3DLayer) g3dLayerPropertyChangedEvent.getSource();
                TreeItem<Object> layerTreeItem = getTreeItemByDocumentItem(layer, this.treeView.getRoot());
                if (layerTreeItem != null) {
                    layerTreeItem.setValue(layer);
                }

//                // region 截获属性改变，以及时刷新界面
//                String name = layer.getName();
//                DocumentItemProperty dp = DocumentItemProperties.DocItemProperty;
//                if (dp != null && !this.isPropertyAppling) {
//                    if (dp.Item != null && layer.Handle == dp.Item.Handle) {
//                        // region 单图层属性界面控制
//                        bool temp = this.isUpdateing;
//                        this.isCauseLayerStateChanged = false;
//                        this.isUpdateing = true;
//                        layer.Name = name;
//                        DocumentItemProperties.ShowProperty(dp.Item, false);
//                        this.isUpdateing = false;
//                        this.isCauseLayerStateChanged = true;
//                        this.isUpdateing = temp;
//                        // endregion
//                    }
//                }
                // endregion
            }
            // endregion
        } else if (propertyName.toLowerCase().equals("visible")) {
            // region 更新节点可见性
            Map3DLayer layer = (Map3DLayer) g3dLayerPropertyChangedEvent.getSource();
            TreeItem<Object> layerTreeItem = getTreeItemByDocumentItem(layer, this.treeView.getRoot());
            if (layerTreeItem != null) {
//                String visible = (String) propertyValue;
//                if (string.Compare(visible, "false", true) == 0) {
//                    layerTreeItem.Visible = false;
//                } else {
//                    layerTreeItem.Visible = true;
//                }
            }
            // endregion
            if (!this.updateing) {
                fireStateChanged(new StateChangedEvent(this));
            }
        } else if (propertyName.toLowerCase().equals("expand")) {
            // region 更新节点展开属性
            Map3DLayer layer = (Map3DLayer) g3dLayerPropertyChangedEvent.getSource();
            TreeItem<Object> layerTreeItem = this.getTreeItemByDocumentItem(layer, this.treeView.getRoot());
            if (layerTreeItem != null) {
                TreeItem<Object> fcsNode = this.treeView.getFocusModel().getFocusedItem();
                String expand = (String) propertyValue;
                layerTreeItem.setExpanded(expand.compareToIgnoreCase("true") == 0);
                this.treeView.removeFocusedNodeChangedListener(focusedNodeChangedListener);
                this.treeView.getFocusModel().focus(this.treeView.getRow(fcsNode));
                this.treeView.addFocusedNodeChangedListener(focusedNodeChangedListener);
            }
            // endregion
        } else if (propertyName.toLowerCase().equals("systemlibrary")) {
            // region 更新专题图节点的系统库属性
            if (g3dLayerPropertyChangedEvent.getSource() instanceof  Vector3DLayer) {
                Vector3DLayer vector3DLayer = (Vector3DLayer) g3dLayerPropertyChangedEvent.getSource();
                TreeItem<Object> layerTreeItem = this.getTreeItemByDocumentItem(vector3DLayer, this.treeView.getRoot());
                if (vector3DLayer != null && layerTreeItem != null && layerTreeItem.getChildren().size() > 0) {
                    SystemLibrary sysLibrary = vector3DLayer.getSysLibrary();
                    for (TreeItem<Object> treeItem : layerTreeItem.getChildren()) {
                        if (treeItem.getValue() instanceof Theme3D) {
                            if (treeItem.getValue() instanceof SimpleTheme3D) {
                                SetNodeImage(treeItem, ((SimpleTheme) treeItem.getValue()).getThemeInfo().getGeoInfo(), sysLibrary.getGuid());
                            }
                            for (TreeItem<Object> subTreeItem : treeItem.getChildren()) {
                                ThemeInfo thInfo = refreshThemeInfo(subTreeItem);
                                if (thInfo != null) {
                                    this.SetNodeImage(subTreeItem, thInfo.getGeoInfo(), sysLibrary.getGuid());
                                }
                            }
                        }
                    }
                }
            }

            // endregion
        } else if (propertyName.toLowerCase().equals("rendertype")) {
            if (!this.updateing) {
                fireStateChanged(new StateChangedEvent(this));
            }
        }
        if (this.g3dLayerPropertyChangedListeners != null) {
            fireG3DLayerPropertyChanged(new G3DLayerPropertyChangedEvent(g3dLayerPropertyChangedEvent.getSource(), g3dLayerPropertyChangedEvent.getArgs()));
        }
    };

    /**
     * 添加文档对象事件
     *
     * @param documentItem 文档对象
     */
    private void addDocumentItemListener(DocumentItem documentItem) {
        if (documentItem instanceof Document) {
            Document document = (Document) documentItem;
            document.addCreatedDocumentListener(createdDocumentListener);
            document.addOpenedDocumentListener(openedDocumentListener);
            document.addClosingDocumentListener(closingDocumentListener);
            document.addClosedDocumentListener(closedDocumentListener);
            document.addPropertyChangedListener(documentPropertyChangedListener);
        } else if (documentItem instanceof Maps) {
            Maps maps = (Maps) documentItem;
            maps.addInsertMapListener(insertMapListener);
            maps.addRemoveMapListener(removeMapListener);
        } else if (documentItem instanceof Scenes) {
            Scenes scenes = (Scenes) documentItem;
            scenes.addInsertSinceListener(insertSceneListener);
            scenes.addRemoveSinceListener(removeSceneListener);
        } else if (documentItem instanceof Map) {
            Map map = (Map) documentItem;
            map.addInsertLayerListener(mapInsertLayerListener);
            map.addRemoveLayerListener(mapRemoveLayerListener);
            map.addMoveLayerListener(mapMoveLayerListener);
            map.addPropertyChangedListener(mapPropertyChangedListener);
        } else if (documentItem instanceof Scene) {
            Scene scene = (Scene) documentItem;
            scene.addInsertLayerListener(sceneInsertG3DLayerListener);
            scene.addRemoveLayerListener(sceneRemoveG3DLayerListener);
        } else if (documentItem instanceof MapLayer) {
            MapLayer mapLayer = (MapLayer) documentItem;
            mapLayer.addPropertyChangedListener(layerPropertyChangedListener);
            GroupLayer groupLayer = mapLayer instanceof GroupLayer ? ((GroupLayer) mapLayer) : null;
            if (groupLayer != null && !(groupLayer instanceof FileLayer6x)) {
                groupLayer.addInsertLayerListener(groupLayerInsertLayerListener);
                groupLayer.addRemoveLayerListener(groupLayerRemoveLayerListener);
                groupLayer.addMoveLayerListener(groupLayerMoveLayerListener);
            }
        } else if (documentItem instanceof Map3DLayer) {
            Map3DLayer map3DLayer = (Map3DLayer) documentItem;
            map3DLayer.addPropertyChangedListener(g3dLayerPropertyChangedListener);
            Group3DLayer group3DLayer = map3DLayer instanceof Group3DLayer ? ((Group3DLayer) map3DLayer) : null;
            if (group3DLayer != null) {
                group3DLayer.addInsertLayerListener(group3DLayerInsertG3DLayerListener);
                group3DLayer.addRemoveLayerListener(group3DLayerRemoveG3DLayerListener);
                group3DLayer.addMoveLayerListener(group3DLayerMoveG3DLayerListener);
            }
        }
    }

    /**
     * 移除文档对象事件
     *
     * @param documentItem 文档对象
     */
    private void removeDocumentItemListener(DocumentItem documentItem) {
        if (documentItem != null) {
            TreeItem<Object> treeItem = this.treeItems.getOrDefault(documentItem.getHandle(), null);
            if (treeItem != null && treeItem.getValue() != null && documentItem.getClass() == treeItem.getValue().getClass()) {
                documentItem = (DocumentItem) treeItem.getValue();
            }
        }
        if (documentItem instanceof Map) {
            Map map = (Map) documentItem;
            map.removeInsertLayerListener(mapInsertLayerListener);
            map.removeRemoveLayerListener(mapRemoveLayerListener);
            map.removeMoveLayerListener(mapMoveLayerListener);
            map.removePropertyChangedListener(mapPropertyChangedListener);
        } else if (documentItem instanceof Scene) {
            Scene scene = (Scene) documentItem;

        } else if (documentItem instanceof MapLayer) {
            MapLayer mapLayer = (MapLayer) documentItem;
            mapLayer.removePropertyChangedListener(layerPropertyChangedListener);
            GroupLayer groupLayer = mapLayer instanceof GroupLayer ? ((GroupLayer) mapLayer) : null;
            if (groupLayer != null && !(groupLayer instanceof FileLayer6x)) {
                groupLayer.removeInsertLayerListener(groupLayerInsertLayerListener);
                groupLayer.removeRemoveLayerListener(groupLayerRemoveLayerListener);
                groupLayer.removeMoveLayerListener(groupLayerMoveLayerListener);
            }
        } else if (documentItem instanceof Map3DLayer) {
            Map3DLayer map3DLayer = (Map3DLayer) documentItem;

            if (map3DLayer instanceof Group3DLayer) {
                Group3DLayer group3DLayer = (Group3DLayer) map3DLayer;
            }
        }
    }


    // endregion

    // region 方法

    /**
     * 打开地图文档
     */
    public void openDocument() {
        FileChooser fc = new FileChooser();
        fc.setTitle("打开地图文档");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Map Files(*.mapx)", "*.mapx"),
                new FileChooser.ExtensionFilter("Map Files(*.map)", "*.map"),
                new FileChooser.ExtensionFilter("Map Bag(*.mbag)", "*.mbag"),
                new FileChooser.ExtensionFilter("Map project(*.mpj)", "*.mpj"),
                new FileChooser.ExtensionFilter("All File", "*.*")
        );
        File file = fc.showOpenDialog(Window.primaryStage);

        boolean closed = this.document.close(false);
        if (closed && file != null) {
            //this.isOpeningDocument = true;
            this.document.open(file.getAbsolutePath());
            //this.isOpeningDocument = false;

            //this.initTestDocument(this.document);
        }
    }

    /**
     * 保存文档
     *
     * @param document 文档
     * @return 是否取消了此次保存
     */
    public boolean saveDocument(Document document) {
        boolean rtn = false;
        if (document == null) {
            document = this.document;
        }
        if (document.getIsNew() || this.has6xEditLayers(document) || this.hasTempLayers(document)) {
            List<DocumentItem> layers = this.getTempLayers(document);
            if (layers.size() > 0) {
                // TODO: 待添加窗体
//                SaveDataForm sdf = new SaveDataForm(doc, this.workspace);
//                rtn = sdf.ShowDialog() != ApiDialog.DialogResult.OK;
            } else {
                FileChooser fc = new FileChooser();
                fc.setTitle("地图文档保存");
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Map Files(*.mapx)", "*.mapx"));
                String fileName = document.getTitle();
                if (fileName.isEmpty()) {
                    fileName = "NewDocument.mapx";
                } else {
                    fileName = fileName + ".mapx";
                }
                fc.setInitialFileName(fileName);
                File file = fc.showSaveDialog(Window.primaryStage);

                if (file != null) {
                    String path = file.getAbsolutePath();
                    if (!path.endsWith(".mapx")) {
                        path = path + ".mapx";
                    }
                    document.saveAs(path);
                } else {
                    rtn = true;
                }
            }
        } else {
            document.save();
        }
        return rtn;
    }

    /**
     * 地图文档另存为
     */
    public void saveAsDocument() {
        if (this.hasTempLayers(this.document)) {
            // TODO: 待添加
//            SaveDataForm sdf = new SaveDataForm(this.document, this.workSpace);
//            sdf.ShowDialog();
        } else {
            FileChooser fc = new FileChooser();
            fc.setTitle("地图文档另存为");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Map Files(*.mapx)", "*.mapx"));
            String fileName = document.getTitle();
            if (fileName.isEmpty()) {
                fileName = "NewDocument.mapx";
            } else {
                fileName = fileName + ".mapx";
            }
            fc.setInitialFileName(fileName);
            File file = fc.showSaveDialog(Window.primaryStage);
            if (file != null) {
                String path = file.getAbsolutePath();
                if (!path.endsWith(".mapx")) {
                    path = path + ".mapx";
                }
                document.saveAs(path);
            }
        }
    }

    /**
     * 关闭地图文档
     *
     * @param save 是否保存
     * @return true/false
     */
    public boolean closeDocument(boolean save) {
        this.workspace.beginUpdateTree();
        this.document.close(save);//在Document的Closing事件中修改this.isClosed的值
        this.workspace.endUpdateTree();
        return this.closed;//此变量的值会在Document的Closing和Closed事件中被修改
    }

    /**
     * 刷新文档树
     *
     * @param documentItem 文档节点，null时刷新整个文档
     */
    public void refreshDocumentItem(DocumentItem documentItem) {
        this.beginUpdate();
        if (documentItem == null || documentItem instanceof Document) {
            // region 移除清理
            this.removeAllDocumentItemEvent();
            this.treeView.setRoot(null);
            this.treeItems.clear();
            // endregion

            // region 重新构建
            this.initTreeWhileNewOrOpen(true);
            // endregion
        } else if (documentItem instanceof Map) {
            // region 移除清理
            Map map = (Map) documentItem;
            this.removeDocumentItemListener(map);
            TreeItem<Object> docTreeItem = this.treeView.getRoot();
            if (docTreeItem != null && docTreeItem.getChildren().size() > 0) {
                docTreeItem.getChildren().remove(this.getMapTreeItemByMap(map));
                this.treeItems.remove(map.getHandle());
                this.setParentNodeShowState(docTreeItem);
            }
            // endregion

            // region 重新构建
            if (docTreeItem == null) {
                this.initTreeWhileNewOrOpen(true);
            } else {
                TreeItem<Object> mapTreeItem = this.initMap(map, docTreeItem);
                if (mapTreeItem != null) {
                    docTreeItem.setExpanded(true);
                    this.treeView.getSelectionModel().select(mapTreeItem);
                    this.setParentNodeShowState(docTreeItem);
                }
            }
            // endregion
        } else if (documentItem instanceof Scene) {
            // region 移除清理
            Scene scene = (Scene) documentItem;
            this.removeDocumentItemListener(scene);
            TreeItem<Object> docTreeItem = this.treeView.getRoot();
            if (docTreeItem != null && docTreeItem.getChildren().size() > 0) {
                docTreeItem.getChildren().remove(this.getSceneTreeItemByScene(scene));
                this.treeItems.remove(scene.getHandle());
                this.setParentNodeShowState(docTreeItem);
            }
            // endregion

            // region 重新构建
            if (docTreeItem == null) {
                this.initTreeWhileNewOrOpen(true);
            } else {
                TreeItem<Object> sceneTreeItem = this.initScene(scene, docTreeItem);
                if (sceneTreeItem != null) {
                    docTreeItem.setExpanded(true);
                    this.treeView.getSelectionModel().select(sceneTreeItem);
                    this.setParentNodeShowState(docTreeItem);
                }
            }
            // endregion
        } else if (documentItem instanceof MapLayer) {
            DocumentItem parentItem = documentItem.getParent();
            if (parentItem != null) {
                // region 移除清理
                MapLayer layer = (MapLayer) documentItem;
                int index = -1;
                if (parentItem instanceof Map) {
                    index = ((Map) parentItem).indexOf(layer);
                } else if (parentItem instanceof GroupLayer) {
                    index = ((GroupLayer) parentItem).indexOf(layer);
                }
                this.removeDocumentItemListener(layer);
                TreeItem<Object> parentTreeItem = this.getTreeItemByDocumentItem(parentItem);
                boolean focused = false;
                if (parentTreeItem != null) {
                    TreeItem<Object> layerTreeItem = this.getTreeItemByDocumentItem(layer, parentTreeItem);
                    if (layerTreeItem != null) {
                        focused = treeView.getSelectionModel().isSelected(treeView.getRow(layerTreeItem));
                        parentTreeItem.getChildren().remove(layerTreeItem);
                        this.treeItems.remove(layer.getHandle());
                        this.setParentNodeShowState(parentTreeItem);
                    }
                }
                // endregion

                // region 重新构建
                this.appendMapLayer(layer, index, parentTreeItem);
                this.setParentNodeShowState(parentTreeItem);
                if (focused) {
                    // 修改说明：如果之前该节点是焦点则保证之后也是焦点，但允许焦点切换两次
                    // 修改人：华凯 2014-06-26
                    if (parentTreeItem.getChildren().size() > index) {
                        this.treeView.getFocusModel().focus(this.treeView.getRow(this.treeItems.get(layer.getHandle())));
                    }
                }
                // endregion
            }
        } else if (documentItem instanceof Map3DLayer) {
            DocumentItem parentItem = documentItem.getParent();
            if (parentItem != null) {
                // region 移除清理
                Map3DLayer layer = (Map3DLayer) documentItem;
                int index = -1;
                if (parentItem instanceof Scene) {
                    index = ((Scene) parentItem).indexOfLayer(layer);
                } else if (parentItem instanceof Group3DLayer) {
                    index = ((Group3DLayer) parentItem).indexOfLayer(layer);
                }
//                this.removeDocumentItemListener(layer);
//                TreeListNode mapTrNode = this.GetNodeByItem(parentItem);
//                bool bFocused = false;
//                if (mapTrNode != null) {
//                    TreeListNode layerTrNode = this.GetLayerTrNodeByMapLayer(layer, mapTrNode);
//                    if (layerTrNode != null) {
//                        bFocused = layerTrNode.Focused;
//                        mapTrNode.Nodes.Remove(layerTrNode);
//                        this.itemNodes.Remove(layer.Handle);
//                        this.SetParNodeShowState(mapTrNode);
//                    }
//                }
                // endregion

                // region 重新构建
//                this.AppendMapLayer(layer, index, mapTrNode);
//                this.SetParNodeShowState(mapTrNode);
//                if (bFocused) {
//                    // 修改说明：如果之前该节点是焦点则保证之后也是焦点，但允许焦点切换两次
//                    // 修改人：华凯 2014-06-26
//                    if (mapTrNode.Nodes.Count > index)
//                        this.treeList.FocusedNode = mapTrNode.Nodes[index];
//                }
                // endregion
            }
        }
        this.endUpdate();
    }

    /**
     * 获取文档中需要保存的临时图层
     *
     * @param documentItem 文档项
     * @return 文档中需要保存的临时图层
     */
    private List<DocumentItem> getTempLayers(DocumentItem documentItem) {
        ArrayList<DocumentItem> layers = new ArrayList<>();
        if (documentItem instanceof Document) {
            Maps maps = ((Document) documentItem).getMaps();
            int count = maps.getCount();
            for (int i = 0; i < count; i++) {
                layers.addAll(this.getTempLayers(maps.getMap(i)));
            }
            Scenes scenes = ((Document) documentItem).getScenes();
            count = scenes.getCount();
            for (int i = 0; i < count; i++) {
                layers.addAll(this.getTempLayers(scenes.getScene(i)));
            }
        } else if (documentItem instanceof Map) {
            Map map = (Map) documentItem;
            int count = map.getLayerCount();
            for (int i = 0; i < count; i++) {
                layers.addAll(this.getTempLayers(map.getLayer(i)));
            }
        } else if (documentItem instanceof Scene) {
            Scene scene = (Scene) documentItem;
            int count = scene.getLayerCount();
            for (int i = 0; i < count; i++) {
                layers.addAll(this.getTempLayers(scene.getLayer(i)));
            }
        } else if (documentItem instanceof GroupLayer) {
            if (documentItem instanceof FileLayer6x) {
                MapLayer mapLayer = (MapLayer) documentItem;
                // TODO: 待添加接口
//                if ((mapLayer.GetDirtyFlag() & 0x10) != 0x00000) {
//                    layers.add(mapLayer);
//                }
            } else {
                GroupLayer groupLayer = (GroupLayer) documentItem;
                int count = groupLayer.getCount();
                for (int i = 0; i < count; i++) {
                    layers.addAll(this.getTempLayers(groupLayer.item(i)));
                }
            }
        } else if (documentItem instanceof MapLayer) {
            if (((MapLayer) documentItem).getURL().toLowerCase().startsWith("gdbp://mapgislocaltemp/tempgdb")) {
                layers.add(documentItem);
            }
        } else if (documentItem instanceof Vector3DLayer || documentItem instanceof LabelLayer) {
            if (((Map3DLayer) documentItem).getURL().toLowerCase().startsWith("gdbp://mapgislocaltemp/tempgdb")) {
                layers.add(documentItem);
            }
        }
        return layers;
    }

    /**
     * 判断文档中是否有要保存的6x图层
     *
     * @param documentItem 文档项
     * @return 是否有要保存的6x图层
     */
    private boolean has6xEditLayers(DocumentItem documentItem) {
        boolean rtn = false;
        if (documentItem instanceof Document) {
            Maps maps = ((Document) documentItem).getMaps();
            int count = maps.getCount();
            for (int i = 0; i < count; i++) {
                rtn = this.has6xEditLayers(maps.getMap(i));
                if (rtn) {
                    break;
                }
            }
        } else if (documentItem instanceof Map) {
            Map map = (Map) documentItem;
            int count = map.getLayerCount();
            for (int i = 0; i < count; i++) {
                rtn = this.has6xEditLayers(map.getLayer(i));
                if (rtn) {
                    break;
                }
            }
        } else if (documentItem instanceof GroupLayer) {
            if (documentItem instanceof FileLayer6x) {
                MapLayer mapLayer = (MapLayer) documentItem;
                // TODO: 待添加接口
                //rtn = ((mapLayer.getDirtyFlag() & 0x10) != 0x00000);
            } else {
                GroupLayer groupLayer = (GroupLayer) documentItem;
                int count = groupLayer.getCount();
                for (int i = 0; i < count; i++) {
                    rtn = this.has6xEditLayers(groupLayer.item(i));
                    if (rtn) {
                        break;
                    }
                }
            }
        }
        return rtn;
    }

    /**
     * 判断文档中是否存在临时图层
     *
     * @param documentItem 文档项
     * @return true/false
     */
    private boolean hasTempLayers(DocumentItem documentItem) {
        boolean rtn = false;
        if (documentItem instanceof Document) {
            Maps maps = ((Document) documentItem).getMaps();
            int count = maps.getCount();
            for (int i = 0; i < count; i++) {
                rtn = this.hasTempLayers(maps.getMap(i));
                if (rtn) {
                    break;
                }
            }
        } else if (documentItem instanceof Map) {
            Map map = (Map) documentItem;
            int count = map.getLayerCount();
            for (int i = 0; i < count; i++) {
                rtn = this.hasTempLayers(map.getLayer(i));
                if (rtn) {
                    break;
                }
            }
        } else if (documentItem instanceof Scene) {
            Scene scene = (Scene) documentItem;
            int count = scene.getLayerCount();
            for (int i = 0; i < count; i++) {
                rtn = this.hasTempLayers(scene.getLayer(i));
                if (rtn) {
                    break;
                }
            }
        } else if (documentItem instanceof GroupLayer) {
            if (!(documentItem instanceof FileLayer6x)) {
                GroupLayer groupLayer = (GroupLayer) documentItem;
                int count = groupLayer.getCount();
                for (int i = 0; i < count; i++) {
                    rtn = this.hasTempLayers(groupLayer.item(i));
                    if (rtn) {
                        break;
                    }
                }
            }
        } else if (documentItem instanceof MapLayer) {
            rtn = ((MapLayer) documentItem).getURL().toLowerCase().startsWith("gdbp://mapgislocaltemp/tempgdb");
        } else if (documentItem instanceof Vector3DLayer || documentItem instanceof LabelLayer) {
            rtn = ((Map3DLayer) documentItem).getURL().toLowerCase().startsWith("gdbp://mapgislocaltemp/tempgdb");
        }
        return rtn;
    }

    private void initTreeWhileNewOrOpen(boolean allPreview) {
        if (this.treeView.getRoot() == null) {
//            this.isIniting = true;
            this.updateing = true;
            this.removeAllDocumentItemEvent();
//            this.treeList.Nodes.Clear();
            this.treeItems.clear();
//            this.ResetStateImages();

            // region 文档节点

            Image image = null;
            IItemStyle documentStyle = this.workspace.getItemStyle(ItemType.toValue(this.document.getClass()));
            if (documentStyle != null) {
                // this.OnExternalNodeImage(this.document, out bitmap); // 暂不需要
                if (image == null) {
                    image = documentStyle.getSubTypeImage(this.document);
                }
                if (image == null) {
                    image = documentStyle.getImage();
                }
            }
            TreeItem<Object> documentTreeItem = createTreeItem(this.document, DisplayState.VISIBLE, image);
            documentTreeItem.setExpanded(true);
            treeView.setRoot(documentTreeItem);

            // endregion
            System.out.println(maps.getCount() + " + " + scenes.getCount());
            // region 地图节点
            if (this.maps.getCount() == 0) {
                this.setParentNodeShowState(documentTreeItem);
            } else {
                for (int i = 0; i < this.maps.getCount(); i++) {
                    Map map = this.maps.getMap(i);
                    this.initMap(map, documentTreeItem);
                }
            }
            // endregion

            // region 场景节点
            if (this.scenes.getCount() == 0) {
                this.setParentNodeShowState(documentTreeItem);
            } else {
                for (int i = 0; i < this.scenes.getCount(); i++) {
                    Scene scene = this.scenes.getScene(i);
                    this.initScene(scene, documentTreeItem);
                }
            }
            // endregion

            documentTreeItem.setExpanded(true);
//            this.treeList.EndUpdate();
//            this.treeList.Update();

            // region 推迟到树节点绘制后再预览视图
            if (allPreview) {
                for (int i = 0; i < this.maps.getCount(); i++) {
                    Map map = this.maps.getMap(i);
                    if (map != null) {
                        //String value = map.GetProperty("InitOpenView");
                        String value = ""; // TODO: 需修改
                        if (!value.isEmpty() && value.compareToIgnoreCase("true") == 0) {
                            this.workspace.fireMenuItemClick(new MenuItemClickEvent(this, PreviewMapMenuItem.class, map));
                        }
                        this.workspace.fireMenuItemClick(new MenuItemClickEvent(this, PreviewMapMenuItem.class, map));
                    }
                }
                for (int i = 0; i < this.scenes.getCount(); i++) {
                    Scene scene = this.scenes.getScene(i);
                    if (scene != null) {
                        //String value = scene.GetPropertyEx("InitOpenView");
                        String value = ""; // TODO: 需修改
                        if (!value.isEmpty() && value.compareToIgnoreCase("true") == 0) {
                            this.workspace.fireMenuItemClick(new MenuItemClickEvent(this, PreviewSceneMenuItem.class, scene));
                        }
                    }
                }
            }
            // endregion

            this.updateing = false;
            this.fireStateChanged(new StateChangedEvent(this));
//            this.isIniting = false;
//            this.CalcAndSetColumnBestWidth(this.treeListColumn1);

        } else {
            // region 文档节点
//            this.treeView.setRoot(new TreeItem(this.document));
            // endregion

            // region 地图节点
            for (int i = 0; i < this.maps.getCount(); i++) {
                Map map = this.maps.getMap(i);
                if (map != null) {
                    // TODO: 待修改
                    String value = "true";//map.GetPropertyEx("InitOpenView");
                    if (value != null && !value.isEmpty() && value.compareToIgnoreCase("true") == 0) {
                        if (this.workspace.getMapControl(map) == null) {
                            this.workspace.fireMenuItemClick(new MenuItemClickEvent(this, PreviewMapMenuItem.class, map));
                        }
                    }
                }
            }
            // endregion

            // region 场景节点
            for (int i = 0; i < this.scenes.getCount(); i++) {
                Scene scene = this.scenes.getScene(i);
                if (scene != null) {
//                    string value = scene.GetPropertyEx("InitOpenView");
//                    if (!string.IsNullOrEmpty(value) && string.Compare(value, "true", true) == 0) {
//                        if (this.workSpace.GetSceneControl(scene) == null)
//                            this.workSpace.FireMenuItemClickEvent("MapGIS.WorkSpace.Style.PreviewScene", scene);
                }
            }
        }
        // endregion
    }

    private void removeAllDocumentItemEvent() {
        for (TreeItem<Object> treeItem : this.treeItems.values()) {
            if (treeItem.getValue() != null && treeItem.getValue() instanceof DocumentItem) {
                this.removeDocumentItemListener((DocumentItem) treeItem.getValue());
            }
        }
    }

    /**
     * 根据子节点的显示状态得出节点本身的显示状态
     *
     * @param treeItem 待确定显示状态的父节点
     */
    private void setParentNodeShowState(TreeItem treeItem) {
        this.causeLayerStateChanged = false;
        if (treeItem != null) {
            Object tag = treeItem.getValue();
            if (!(/*tag instanceof MapSetLayer || */tag instanceof NetClsLayer)) {
                if (tag instanceof FileLayer6x || !this.groupLayerStateSelf) {
                    if (treeItem.getChildren().size() == 0) {
                        // 当从组图层下移除最后一个图层后，组图层会没有子节点
                        DisplayState displayState = DisplayState.VISIBLE;
                        if ((tag instanceof GroupLayer && ((GroupLayer) tag).getState() == LayerState.UnVisible) ||
                                (tag instanceof Group3DLayer && ((Group3DLayer) tag).getState() == LayerState.UnVisible)) {
                            displayState = DisplayState.UNVISIBLE;
                        }
                        setDisplayState(treeItem, displayState);
                    } else {
                        // region 向上设置各父级节点的状态

                        int iUnvisible = 0;
                        int iVisible = 0;
                        // TODO: 待添加镶嵌数据集相关接口
                        //镶嵌数据集子图层状态改变对父图层的关联关系需单独控制
//                        if (tag instanceof MosaicDatasetLayer) {
//                            MosaicDatasetLayer mdsLayer = (MosaicDatasetLayer) (treeItem.getValue());
//                            boolean rasChecked = false;//影像图层是否可见
//                            for (Object object : treeItem.getChildren()) {
//                                TreeItem subTreeItem = (TreeItem) object;
//                                int imgIndex = subTreeItem.ImageIndex;
//                                if (imgIndex == 2 && subTreeItem.getValue() instanceof MosaicDatasetRasterLayer) {
//                                    rasChecked = true;
//                                    iVisible++;
//                                } else if (imgIndex == 2) {
//                                    iVisible++;
//                                } else {
//                                    iUnvisible++;
//                                }
//                            }
//                            if (rasChecked) {
//                                //影像图层可见
//                                if (iUnvisible == 0) {
//                                    //全可见
//                                    if (subTreeItem.ImageIndex == 0) {
//                                        mdsLayer.setState(LayerState.Visible);
//                                        subTreeItem.ImageIndex = 2;
//                                        subTreeItem.SelectImageIndex = 2;
//                                    }
//                                } else {
//                                    //部分可见部分不可见且影像图层可见
//                                    if (subTreeItem.ImageIndex == 1) {
//                                        mdsLayer.setState(LayerState.Visible);
//                                        subTreeItem.ImageIndex = 0;
//                                        subTreeItem.SelectImageIndex = 0;
//                                    }
//                                }
//                            } else {
//                                //影像图层不可见
//                                if (iVisible == 0) {
//                                    //全部不可见
//                                    subTreeItem.ImageIndex = 1;
//                                    subTreeItem.SelectImageIndex = 1;
//                                    mdsLayer.setState(LayerState.UnVisible);
//                                } else {
//                                    //部分可见部分不可见且影像图层不可见
//                                    subTreeItem.ImageIndex = 0;
//                                    subTreeItem.SelectImageIndex = 0;
//                                    mdsLayer.setState(LayerState.Visible);
//                                }
//                            }
//                        } else {
                        for (Object subTreeItem : treeItem.getChildren()) {
                            DisplayState ds = getDisplayState((TreeItem) subTreeItem);
                            if (ds == DisplayState.UNVISIBLE) {
                                iUnvisible++;
                            } else if (ds == DisplayState.VISIBLE || ds == DisplayState.EDITABLE || ds == DisplayState.ACTIVE) {
                                iVisible++;
                            }
                        }
                        DisplayState displayState = DisplayState.UNVISIBLE;
                        // 地形层作为父节点时，子图层可见，地形层必须可见；子图层不可见时，地形层状态不变；
                        if (tag instanceof TerrainLayer) {
                            if (iVisible >= 0) {
                                displayState = DisplayState.VISIBLE;
                            }
                        } else {
                            if (iUnvisible == treeItem.getChildren().size()) {
                                //全不可见
                                displayState = DisplayState.UNVISIBLE;
                            } else if (iUnvisible == 0) {
                                //没有不可见的
                                if (iVisible == treeItem.getChildren().size()) {
                                    //全可见
                                    displayState = DisplayState.VISIBLE;
                                } else {
                                    //部分可见或全都是中间状态
                                    displayState = DisplayState.INTERMEDIATE;
                                }
                            } else {
                                //部分不可见，余下的可见或未中间状态
                                displayState = DisplayState.INTERMEDIATE;
                            }
                        }

                        setDisplayState(treeItem, displayState);

                        if (tag instanceof GroupLayer) {
                            GroupLayer group = (GroupLayer) tag;
                            if (displayState == DisplayState.UNVISIBLE) {
                                if (group.getState() != LayerState.UnVisible) {
                                    group.setState(LayerState.UnVisible);
                                }
                            } else {
                                if (group.getState() == LayerState.UnVisible) {
                                    group.setState(LayerState.Visible);
                                }
                            }
                        } else if (tag instanceof Group3DLayer) {
                            Group3DLayer group3D = (Group3DLayer) tag;
                            if (displayState == DisplayState.UNVISIBLE) {
                                if (group3D.getState() != LayerState.UnVisible) {
                                    group3D.setState(LayerState.UnVisible);
                                }
                            } else {
                                if (group3D.getState() == LayerState.UnVisible) {
                                    group3D.setState(LayerState.Visible);
                                }
                            }
                        }
//                    }

                        // endregion
                    }
                    // 修改说明：自定义节点的状态不影响到文档节点
                    // 修改人：易师盼 2017-08-22
//                    if (!(tag instanceof CustomNode) || ((tag instanceof CustomNode) && (treeItem.getParent().getValue() instanceof CustomNode))) {
                    this.setParentNodeShowState(treeItem.getParent());
//                    }
                }
            }
        }
        this.causeLayerStateChanged = true;
    }

    /**
     * 绘制专题图节点及其子节点图标
     *
     * @param treeItem   树节点
     * @param geomInfo   几何信息
     * @param sysLibGuid GUID
     */
    private void SetNodeImage(TreeItem<Object> treeItem, GeomInfo geomInfo, String sysLibGuid) {
//        Image image = GetBitmapFromGeomInfo(gInfo, sysLibGuid);
//        this.addDynamicImage(image);
//        if (treeItem != null)
//        {
//            this.SetEmptyImage(treeItem.StateImageIndex);
//            ImageCollection imgList = treeItem.TreeList.StateImageList as ImageCollection;
//            if (imgList != null && image != null)
//                treeItem.StateImageIndex = imgList.Images.Add(image);
//            else
//                treeItem.StateImageIndex = -1;
//        }
    }

    /**
     * 记录动态图片
     *
     * @param image 图片
     */
    private void addDynamicImage(Image image) {
        if (image != null) {
            this.dynamicImages.put("", image);
        }
    }

    /**
     * 初始化/插入 Map 节点
     *
     * @param map            地图
     * @param parentTreeItem 父节点
     * @return Map 节点
     */
    private TreeItem<Object> initMap(Map map, TreeItem<Object> parentTreeItem) {
        TreeItem<Object> mapTreeItem = null;
        if (map != null && parentTreeItem != null) {
            this.addDocumentItemListener(map);
            Image image = null;
            IItemStyle mapStyle = this.workspace.getItemStyle(ItemType.MAP);
            if (mapStyle != null) {
                //this.OnExternalNodeImage(map, out bitmap);
                if (image == null) {
                    image = mapStyle.getSubTypeImage(map);
                }
                if (image == null) {
                    image = mapStyle.getImage();
                }
            }
            mapTreeItem = createTreeItem(map, DisplayState.VISIBLE, image);
            parentTreeItem.getChildren().add(mapTreeItem);
            if (!this.treeItems.containsKey(map.getHandle())) {
                this.treeItems.put(map.getHandle(), mapTreeItem);
            }
            if (map.getLayerCount() == 0) {
                this.setParentNodeShowState(mapTreeItem);
            } else {
                for (int j = 0; j < map.getLayerCount(); j++) {
                    MapLayer layer = map.getLayer(j);
                    this.initMapLayer(layer, mapTreeItem);
                }
            }
//            TreeItem fcsNode = this.treeView.getFocusModel().getFocusedItem();
//            String expand = map.getPropertyEx("Expand");
//            mapTreeItem.Expanded = string.IsNullOrEmpty(expand) ? true : (string.Compare(expand, "true", true) == 0);
            mapTreeItem.setExpanded(true);
//            // 修改说明：因为dxp内部在设置Expanded时可能会改变焦点（反射dxp代码中可以看到），因此保证焦点不变Bug 5030
//            // 修改人：华凯 2014-05-19
//            this.treeList.FocusedNodeChanged -= new FocusedNodeChangedEventHandler(treeList_FocusedNodeChanged);
//            this.treeList.FocusedNode = fcsNode;
//            this.treeList.FocusedNodeChanged += new FocusedNodeChangedEventHandler(treeList_FocusedNodeChanged);
        }
        return mapTreeItem;
    }

    /**
     * 初始化/插入 Scene 节点
     *
     * @param scene    场景
     * @param parentTreeItem 父节点
     */
    private TreeItem<Object> initScene(Scene scene, TreeItem<Object> parentTreeItem) {
        TreeItem<Object> sceneTreeItem = null;
        if (scene != null && parentTreeItem != null) {
            this.addDocumentItemListener(scene);
            Image image = null;
            IItemStyle sceneStyle = this.workspace.getItemStyle(ItemType.SCENE);
            if (sceneStyle != null) {
//                this.OnExternalNodeImage(scene, out bitmap);
                if (image == null) {
                    image = sceneStyle.getSubTypeImage(scene);
                }
                if (image == null) {
                    image = sceneStyle.getImage();
                }
            }
            sceneTreeItem = createTreeItem(scene, DisplayState.VISIBLE, image);
            parentTreeItem.getChildren().add(sceneTreeItem);
            if (!this.treeItems.containsKey(scene.getHandle())) {
                this.treeItems.put(scene.getHandle(), sceneTreeItem);
            }
            if (scene.getLayerCount() == 0) {
                this.setParentNodeShowState(sceneTreeItem);
            } else {
                for (int j = 0; j < scene.getLayerCount(); j++) {
                    Map3DLayer layer = scene.getLayer(j);
                    this.initG3DLayer(layer, sceneTreeItem);
                }
            }
//            TreeItem fcsNode = this.treeList.FocusedNode;
//            String expand = scene.GetPropertyEx("Expand");
//            sceneTreeItem.Expanded = string.IsNullOrEmpty(expand) ? true : (string.Compare(expand, "true", true) == 0);
            sceneTreeItem.setExpanded(true);
//            // 修改说明：因为dxp内部在设置Expanded时可能会改变焦点（反射dxp代码中可以看到），因此保证焦点不变Bug 5030
//            // 修改人：华凯 2014-05-19
//            this.treeList.FocusedNodeChanged -= new FocusedNodeChangedEventHandler(treeList_FocusedNodeChanged);
//            this.treeList.FocusedNode = fcsNode;
//            this.treeList.FocusedNodeChanged += new FocusedNodeChangedEventHandler(treeList_FocusedNodeChanged);
        }
        return sceneTreeItem;
    }

    /**
     * 初始化/插入 图层节点
     *
     * @param mapLayer       图层
     * @param parentTreeItem 父节点
     */
    private void initMapLayer(MapLayer mapLayer, TreeItem<Object> parentTreeItem) {
        if (mapLayer != null && parentTreeItem != null) {
            Image typeImage = null;
            DisplayState displayState = DisplayState.VISIBLE;
            IItemStyle mapLayerStyle = this.workspace.getItemStyle(ItemType.toValue(mapLayer.getClass()));
            if (mapLayerStyle != null) {
//                this.OnExternalNodeImage(layer, out bitmap);
                if (typeImage == null) {
                    typeImage = mapLayerStyle.getSubTypeImage(mapLayer);
                }
                if (typeImage == null) {
                    typeImage = mapLayerStyle.getImage();
                }
                if (!mapLayer.getIsValid()/* && !(mapLayer instanceof MapSetClsLayer ||mapLayer instanceof MapSetFrmLayer)*/) {
                    typeImage = images.get("Png_Unknown_16");
                }
            }

            TreeItem<Object> mapLayerTreeItem = createTreeItem(mapLayer, getDisplayState(mapLayer.getState()), typeImage);
            parentTreeItem.getChildren().add(mapLayerTreeItem);
            if (!this.treeItems.containsKey(mapLayer.getHandle())) {
                this.treeItems.put(mapLayer.getHandle(), mapLayerTreeItem);
            }

            if (mapLayer instanceof GroupLayer) {
                // region 组图层
                GroupLayer groupLayer = (GroupLayer) mapLayer;
                if (groupLayer.getCount() == 0) {
                    this.setParentNodeShowState(mapLayerTreeItem);
                }
//                else if (mapLayer instanceof S52Layer) {
//                    //海图数据支持-zkj(根据实际情况决定是否添加海图物标要素子节点)
//                    if (!this.m_IsHTEditable)
//                        this.SetParNodeShowState(layerTrNode); //(Desktop不添加海图物标要素子图层节点)
//                    else {
//                        for (int i = 0; i < groupLayer.Count; i++) {
//                            MapLayer mapLayer = groupLayer.get_Item(i);
//                            this.InitMapLayer(mapLayer, layerTrNode);//(海图系统需要添加海图物标要素子图层节点)
//                        }
//                    }
//                }
                else {
                    for (int i = 0; i < groupLayer.getCount(); i++) {
                        MapLayer layer = groupLayer.item(i);
                        this.initMapLayer(layer, mapLayerTreeItem);
                    }
                }
                // endregion
            } else {
                // region 图层
                this.setParentNodeShowState(parentTreeItem);
                this.initTheme(mapLayer, mapLayerTreeItem);
                // endregion
            }
//            String visible = mapLayer.GetPropertyEx("Visible");
//            if (string.Compare(visible, "false", true) == 0)
//                mapLayerTreeItem.Visible = false;
//            else
//                mapLayerTreeItem.Visible = true;
//            TreeItem fcsNode = this.treeView.getFocusModel().getFocusedItem();
//            string expand = layer.GetPropertyEx("Expand");
//            if (string.Compare(expand, "true", true) == 0)
//                mapLayerTreeItem.Expanded = true;
//            else
//                mapLayerTreeItem.Expanded = false;
            mapLayerTreeItem.setExpanded(true);
//            // 修改说明：因为dxp内部在设置Expanded时可能会改变焦点（反射dxp代码中可以看到），因此保证焦点不变Bug 5030
//            // 修改人：华凯 2014-05-19
//            this.treeList.FocusedNodeChanged -= new FocusedNodeChangedEventHandler(treeList_FocusedNodeChanged);
//            this.treeList.FocusedNode = fcsNode;
//            this.treeList.FocusedNodeChanged += new FocusedNodeChangedEventHandler(treeList_FocusedNodeChanged);

            this.addDocumentItemListener(mapLayer);
        }
    }

    /**
     * 初始化/插入 三维图层节点
     *
     * @param g3dLayer 三维图层
     * @param parentTreeItem 父节点
     */
    private void initG3DLayer(Map3DLayer g3dLayer, TreeItem<Object> parentTreeItem) {
        if (g3dLayer != null && parentTreeItem != null) {
            Image typeImage = null;
            DisplayState displayState = DisplayState.VISIBLE;
            IItemStyle g3dLayerStyle = this.workspace.getItemStyle(ItemType.toValue(g3dLayer.getClass()));
            if (g3dLayerStyle != null) {
//                this.OnExternalNodeImage(g3dLayer, out bitmap);
                if (typeImage == null) {
                    typeImage = g3dLayerStyle.getSubTypeImage(g3dLayer);
                }
                if (typeImage == null) {
                    typeImage = g3dLayerStyle.getImage();
                }
                if (!g3dLayer.getIsValid()) {
                    typeImage = images.get("Png_Unknown_16");
                }
            }

            TreeItem<Object> g3dLayerTreeItem = createTreeItem(g3dLayer, getDisplayState(g3dLayer.getState()), typeImage);
            parentTreeItem.getChildren().add(g3dLayerTreeItem);
            if (!this.treeItems.containsKey(g3dLayer.getHandle())) {
                this.treeItems.put(g3dLayer.getHandle(), g3dLayerTreeItem);
            }

            if (g3dLayer instanceof Group3DLayer) {
                // region 组图层
                Group3DLayer group3DLayer = (Group3DLayer) g3dLayer;
                int count = group3DLayer.getLayerCount();
                if (count == 0) {
                    this.setParentNodeShowState(g3dLayerTreeItem);
                } else {
                    for (int i = 0; i < count; i++) {
                        Map3DLayer g3DLayer = group3DLayer.getLayer(i);
                        this.initG3DLayer(g3DLayer, g3dLayerTreeItem);
                    }
                }
                // endregion
            } else {
                // region 图层
                this.setParentNodeShowState(parentTreeItem);
                this.initTheme(g3dLayer, g3dLayerTreeItem);
                // endregion
            }

            this.addDocumentItemListener(g3dLayer);
        }
    }

    /**
     * 读取专题图节点
     *
     * @param mapLayer 图层
     * @param treeItem 图层节点
     */
    private void initTheme(MapLayer mapLayer, TreeItem<Object> treeItem) {
//        Themes ths = mapLayer.getThemes();
//        if (ths != null && ths.getCount() > 0) {
//            String sysLibGuid = "";
//            if (mapLayer instanceof VectorLayer) {
//                SystemLibrary sl = ((VectorLayer) mapLayer).getSysLibrary();
//                if (sl != null) {
//                    sysLibGuid = sl.getGuid();
//                }
//            }
//            for (int i = 0; i < ths.getCount(); i++) {
//                Theme th = ths.getTheme(i);
//                TreeItem thNode = this.appendThemeTreeItem(treeItem, th, sysLibGuid);//未完成。第三个参数
//            }
//        }
    }

    /**
     * 读取专题图节点
     *
     * @param g3dLayer 三维图层
     * @param treeItem 图层节点
     */
    private void initTheme(Map3DLayer g3dLayer, TreeItem<Object> treeItem) {

        String sysLibGuid = "";
        if (g3dLayer instanceof Vector3DLayer) {
            SystemLibrary sl = ((Vector3DLayer) g3dLayer).getSysLibrary();
            if (sl != null) {
                sysLibGuid = sl.getGuid();
            }
            Themes3D ths = ((Vector3DLayer) g3dLayer).get3DThemes();
            if (ths != null && ths.getCount() > 0) {

                for (int i = 0; i < ths.getCount(); i++) {
                    Theme3D th = ths.getTheme(i);
                    TreeItem thNode = this.appendTheme3DTreeItem(treeItem, th, sysLibGuid);//未完成。第三个参数
                }
            }
        }
    }

    /**
     * 添加图层或组图层
     *
     * @param insertLayer    待添加的图层
     * @param index          图层的位置，若为-1则追加，否则为插入
     * @param parentTreeItem 待添加图层的地图或组对应的树节点
     */
    private void appendMapLayer(MapLayer insertLayer, int index, TreeItem<Object> parentTreeItem) {
        if (insertLayer != null && parentTreeItem != null) {
            DisplayState displayState = null;
            Image typeImage = null;
            IItemStyle itemStyle = null;
            // 镶嵌数据集矢量子图层不能使用区图层那套菜单
            if (insertLayer.getParent() instanceof GroupLayer && ((MapLayer) insertLayer.getParent()).getData() instanceof MosaicDataset) {
                if (insertLayer instanceof VectorLayer) {
                    itemStyle = this.workspace.getItemStyle(ItemType.toValue(VectorLayer.class));
                } else {
                    itemStyle = this.workspace.getItemStyle(ItemType.toValue(insertLayer.getClass()));
                }
            } else {
                itemStyle = this.workspace.getItemStyle(ItemType.toValue(insertLayer.getClass()));
            }
            if (itemStyle != null) {
                //this.OnExternalNodeImage(insertLayer, out bitmap);
                if (typeImage == null) {
                    typeImage = itemStyle.getSubTypeImage(insertLayer);
                }
                if (typeImage == null) {
                    typeImage = itemStyle.getImage();
                }
            }
            if (true)// !(insertLayer instanceof MapSetFrmLayer))
            {
//                if (insertLayer instanceof MapSetClsLayer) {
//                    if (layerTrNode.ImageIndex > 4) {
//                        imgIndex = (insertLayer as MapSetClsLayer).IsDisp ? 6 : 5;
//                    } else {
//                        imgIndex = (insertLayer as MapSetClsLayer).IsDisp ? 2 : 1;
//                    }
//                } else
                {
                    displayState = getDisplayState(insertLayer.getState());
                    if (insertLayer instanceof GroupLayer)// &&!(insertLayer is MapSetLayer))
                    {
                        if (this.isSubLayersVisible((GroupLayer) insertLayer)) {
                            displayState = DisplayState.VISIBLE;
                        } else if (this.isSubLayersUnVisible((GroupLayer) insertLayer)) {
                            displayState = DisplayState.UNVISIBLE;
                        }
                    }
                }
            }
            TreeItem<Object> layerTreeItem = createTreeItem(insertLayer, displayState, typeImage);
            parentTreeItem.getChildren().add(layerTreeItem);
            if (!this.treeItems.containsKey(insertLayer.getHandle())) {
                this.treeItems.put(insertLayer.getHandle(), layerTreeItem);
            }

            if (insertLayer instanceof GroupLayer) {
                GroupLayer groupLayer = (GroupLayer) insertLayer;
                for (int i = 0; i < groupLayer.getCount(); i++) {
                    this.appendMapLayer(groupLayer.item(i), i, layerTreeItem);
                }
            } else {
                this.initTheme(insertLayer, layerTreeItem);
            }
            // TODO: 待添加属性 getPropertyEx()
            //String visible = insertLayer.GetPropertyEx("Visible");
//            if (string.Compare(visible, "false", true) == 0)
//                layerTrNode.Visible = false;
//            else
//                layerTrNode.Visible = true;
//            string expand = insertLayer.GetPropertyEx("Expand");
//            TreeListNode fcsNode = this.treeList.FocusedNode;
//            if (string.Compare(expand, "true", true) == 0)
//                layerTrNode.Expanded = true;
//            else
//                layerTrNode.Expanded = false;
            // TODO: 待验证是否出现 dxp 具有的 bug 现象
            // 修改说明：因为dxp内部在设置Expanded时可能会改变焦点（反射dxp代码中可以看到），因此保证焦点不变Bug 5030
            // 修改人：华凯 2014-05-19
//            this.treeList.FocusedNodeChanged -= new FocusedNodeChangedEventHandler(treeList_FocusedNodeChanged);
//            this.treeList.FocusedNode = fcsNode;
//            this.treeList.FocusedNodeChanged += new FocusedNodeChangedEventHandler(treeList_FocusedNodeChanged);


            // TODO: 作用是什么，怎么实现
//            if (index > -1) {
//                bool temp = this.isSetNodeIndexing;
//                this.isSetNodeIndexing = true;
//                this.treeList.SetNodeIndex(layerTrNode, index);
//                this.isSetNodeIndexing = temp;
//            }

//            TreeListNode fcsNode1 = this.treeList.FocusedNode;
            parentTreeItem.setExpanded(true);
//            // 修改说明：因为dxp内部在设置Expanded时可能会改变焦点（反射dxp代码中可以看到），因此保证焦点不变Bug 5030
//            // 修改人：华凯 2014-05-19
//            this.treeList.FocusedNodeChanged -= new FocusedNodeChangedEventHandler(treeList_FocusedNodeChanged);
//            this.treeList.FocusedNode = fcsNode1;
//            this.treeList.FocusedNodeChanged += new FocusedNodeChangedEventHandler(treeList_FocusedNodeChanged);

            this.addDocumentItemListener(insertLayer);

            // TODO: 三维暂不管
            // 修改说明：添加图层后更新属性页面（某些属性的可用性可能会因为添加图层后改变，如形三维地形图层的“地叠加范围裁剪模式”）
            // 修改人：易师盼 2018-10-22
//            if (MapGIS.Desktop.UI.Controls.DocumentItemProperties.DocItemProperty != null && MapGIS.Desktop.UI.Controls.DocumentItemProperties.DocItemProperty.Visible)
//                this.workSpace.FireMenuItemClick0Event("MapGIS.WorkSpace.Style.ItemProperty", this.treeList.FocusedNode.Tag as DocumentItem);
        }
    }

    private void appendG3DLayer(Map3DLayer insertLayer, int index, TreeItem<Object> parentTreeItem) {
        if (insertLayer != null && parentTreeItem != null) {
            DisplayState displayState = null;
            Image typeImage = null;
            IItemStyle itemStyle = null;
            itemStyle = this.workspace.getItemStyle(ItemType.toValue(insertLayer.getClass()));
            if (itemStyle != null) {
                //this.OnExternalNodeImage(insertLayer, out bitmap);
                if (typeImage == null) {
                    typeImage = itemStyle.getSubTypeImage(insertLayer);
                }
                if (typeImage == null) {
                    typeImage = itemStyle.getImage();
                }
            }

            displayState = getDisplayState(insertLayer.getState());

            TreeItem<Object> layerTreeItem = createTreeItem(insertLayer, displayState, typeImage);
            parentTreeItem.getChildren().add(layerTreeItem);
            if (!this.treeItems.containsKey(insertLayer.getHandle())) {
                this.treeItems.put(insertLayer.getHandle(), layerTreeItem);
            }

            if (insertLayer instanceof Group3DLayer) {
                // region 组图层
                Group3DLayer group3D = (Group3DLayer) insertLayer;
                int count = group3D.getLayerCount();
                for (int i = 0; i < count; i++) {
                    this.appendG3DLayer(group3D.getLayer(i), i, layerTreeItem);
                }
                // endregion
            } else {
                // region 图层
                this.setParentNodeShowState(parentTreeItem);
                this.initTheme(insertLayer, layerTreeItem);
                // endregion
            }

//            if (index > -1) {
//                bool temp = this.isSetNodeIndexing;
//                this.isSetNodeIndexing = true;
//                this.treeList.SetNodeIndex(layerTrNode, index);
//                this.isSetNodeIndexing = temp;
//            }
//            TreeListNode fcsNode = this.treeList.FocusedNode;
            parentTreeItem.setExpanded(true);
//            // 修改说明：因为dxp内部在设置Expanded时可能会改变焦点（反射dxp代码中可以看到），因此保证焦点不变Bug 5030
//            // 修改人：华凯 2014-05-19
//            this.treeList.FocusedNodeChanged -= new FocusedNodeChangedEventHandler(treeList_FocusedNodeChanged);
//            this.treeList.FocusedNode = fcsNode;
//            this.treeList.FocusedNodeChanged += new FocusedNodeChangedEventHandler(treeList_FocusedNodeChanged);

            this.addDocumentItemListener(insertLayer);

//            // 修改说明：添加图层后更新属性页面（某些属性的可用性可能会因为添加图层后改变，如三维地形图层的“地形叠加范围裁剪模式”）
//            // 修改人：易师盼 2018-10-22
//            if (MapGIS.Desktop.UI.Controls.DocumentItemProperties.DocItemProperty != null && MapGIS.Desktop.UI.Controls.DocumentItemProperties.DocItemProperty.Visible)
//                this.workSpace.FireMenuItemClickEvent("MapGIS.WorkSpace.Style.ItemProperty", this.treeList.FocusedNode.Tag as DocumentItem);
        }
    }

    /**
     * 获取已知 Map 对应的树节点
     *
     * @param map 地图
     * @return 对应的树节点
     */
    private TreeItem<Object> getMapTreeItemByMap(Map map) {
        TreeItem<Object> mapTreeItem = null;
        if (this.treeView.getRoot() != null) {
            for (TreeItem<Object> tempTreeItem : this.treeView.getRoot().getChildren()) {
                if (tempTreeItem.getValue() instanceof Map) {
                    Map tempMap = (Map) tempTreeItem.getValue();
                    if (tempMap != null && tempMap.getHandle() == map.getHandle()) {
                        mapTreeItem = tempTreeItem;
                        break;
                    }
                }
            }
        }
        return mapTreeItem;
    }

    /**
     * 获取已知 Scene 对应的树节点
     *
     * @param scene 场景
     * @return 对应的树节点
     */
    private TreeItem<Object> getSceneTreeItemByScene(Scene scene) {
        TreeItem<Object> sceneTreeItem = null;
        if (this.treeView.getRoot() != null) {
            for (TreeItem<Object> tempTreeItem : this.treeView.getRoot().getChildren()) {
                if (tempTreeItem.getValue() instanceof Scene) {
                    Scene tempScene = (Scene) tempTreeItem.getValue();
                    if (tempScene != null && tempScene.getHandle() == scene.getHandle()) {
                        sceneTreeItem = tempTreeItem;
                        break;
                    }
                }
            }
        }
        return sceneTreeItem;
    }

    /**
     * 根据指定文档项获取树节点
     *
     * @param documentItem 文档项
     * @return 树节点
     */
    private TreeItem<Object> getTreeItemByDocumentItem(DocumentItem documentItem) {
        TreeItem<Object> treeItem = null;
        if (documentItem != null && this.treeView.getRoot() != null) {
            treeItem = this.treeItems.getOrDefault(documentItem.getHandle(), null);
            if (treeItem == null) {
                treeItem = this.getTreeItemByDocumentItem(documentItem, this.treeView.getRoot());
            }
        }
        return treeItem;
    }

    private TreeItem<Object> getTreeItemByDocumentItem(DocumentItem documentItem, TreeItem<Object> parentTreeItem) {
        if (documentItem == null || parentTreeItem == null || !(parentTreeItem.getValue() instanceof DocumentItem)) {
            return null;
        }

        if (((DocumentItem) parentTreeItem.getValue()).getHandle() == documentItem.getHandle()) {
            return parentTreeItem;
        }

        for (TreeItem<Object> treeItem : parentTreeItem.getChildren()) {
            if (treeItem.getValue() instanceof DocumentItem && ((DocumentItem) treeItem.getValue()).getHandle() == documentItem.getHandle()) {
                return treeItem;
            }
            if (treeItem.getChildren().size() > 0) {
                TreeItem<Object> ti = getTreeItemByDocumentItem(documentItem, treeItem);
                if (ti != null) {
                    return ti;
                }
            }
        }
        return null;
    }

    private TreeItem<Object> appendThemeTreeItem(TreeItem<Object> parNode, Theme th, String sysLibGuid) {
        this.beginUpdate();
        TreeItem<Object> thNode = null;

        this.endUpdate();
        return thNode;
    }

    private TreeItem<Object> appendTheme3DTreeItem(TreeItem<Object> parNode, Theme3D th, String sysLibGuid) {
        this.beginUpdate();
        TreeItem<Object> thNode = null;

        this.endUpdate();
        return thNode;
    }

    /**
     * 开始更新树
     */
    public void beginUpdate() {
//        this.treeView.BeginUpdate();
//        this.isUpdateing = true;
//        this.isIniting = true;
    }

    /**
     * 结束树的更新
     */
    public void endUpdate() {
//        this.treeList.EndUpdate();
//        // 修改说明：立即对树进行一次重绘，可以防止很长时间收不到Windows重绘消息Bug 4947
//        // 修改人：华凯 2014-05-13
//        this.treeList.Refresh();
//        this.isUpdateing = false;
//        this.isIniting = false;
//        // 修改说明：更新完成后需要计算一次列宽，否则滚动条和可见节点文字不一致。
//        // 修改人：陈容 2015-12-29
//        this.CalcAndSetColumnBestWidth(this.treeListColumn1);
//        this.OnStateChanged(this, EventArgs.Empty);
    }

    private boolean isTreeItemsInSameLevel(List<TreeItem<Object>> treeItems) {
        if (treeItems == null || treeItems.size() == 0) {
            return false;
        }
        int level = treeView.getTreeItemLevel(treeItems.get(0));
        for (TreeItem treeItem : treeItems) {
            if (level != treeView.getTreeItemLevel(treeItem)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 子图层是否均为可见
     *
     * @param groupLayer 组图层
     * @return true/false
     */
    private boolean isSubLayersVisible(GroupLayer groupLayer) {
        boolean rtn = true;
        for (int i = 0; i < groupLayer.getCount(); i++) {
            MapLayer subLayer = groupLayer.item(i);
            if (subLayer != null) {
                if (subLayer instanceof GroupLayer) {
                    if (!this.isSubLayersVisible((GroupLayer) subLayer)) {
                        rtn = false;
                        break;
                    }
                } else {
                    if (subLayer.getState() == LayerState.UnVisible) {
                        rtn = false;
                        break;
                    }
                }
            }
        }
        return rtn;
    }

    /**
     * 子图层是否均为不可见
     *
     * @param groupLayer 组图层
     * @return true/false
     */
    private boolean isSubLayersUnVisible(GroupLayer groupLayer) {
        boolean rtn = true;
        for (int i = 0; i < groupLayer.getCount(); i++) {
            MapLayer subLayer = groupLayer.item(i);
            if (subLayer instanceof GroupLayer) {
                if (!this.isSubLayersUnVisible((GroupLayer) subLayer)) {
                    rtn = false;
                    break;
                }
            } else {
                if (subLayer.getState() != LayerState.UnVisible) {
                    rtn = false;
                    break;
                }
            }
        }
        return rtn;
    }

    /**
     * 判断临时图层是否发生了改变
     *
     * @param documentItem 文档项
     * @return true/false
     */
    private boolean isTempLayersChanged(DocumentItem documentItem) {
        boolean rtn = false;
        if (documentItem instanceof Document) {
            Maps maps = ((Document) documentItem).getMaps();
            int count = maps.getCount();
            for (int i = 0; i < count; i++) {
                rtn = this.isTempLayersChanged(maps.getMap(i));
                if (rtn) {
                    break;
                }
            }
        } else if (documentItem instanceof Map) {
            Map map = (Map) documentItem;
            int count = map.getLayerCount();
            for (int i = 0; i < count; i++) {
                rtn = this.isTempLayersChanged(map.getLayer(i));
                if (rtn) {
                    break;
                }
            }
        } else if (documentItem instanceof GroupLayer) {
            GroupLayer groupLayer = (GroupLayer) documentItem;
            int count = groupLayer.getCount();
            for (int i = 0; i < count; i++) {
                rtn = this.isTempLayersChanged(groupLayer.item(i));
                if (rtn) {
                    break;
                }
            }
        } else if (documentItem instanceof MapLayer) {
            MapLayer mapLayer = (MapLayer) documentItem;
            String url = mapLayer.getURL();
            if (url.toLowerCase().startsWith("gdbp://mapgislocaltemp/tempgdb")) {
                if (mapLayer.getRange() != null) {
                    rtn = true;
                }
            }
        } else if (documentItem instanceof Vector3DLayer) {
            Vector3DLayer vector3DLayer = (Vector3DLayer) documentItem;
            String url = vector3DLayer.getURL();
            if (url.toLowerCase().startsWith("gdbp://mapgislocaltemp/tempgdb")) {
                if (vector3DLayer.get3DRange() != null) {
                    rtn = true;
                }
            }
        } else if (documentItem instanceof LabelLayer) {
            LabelLayer labelLayer = (LabelLayer) documentItem;
            String url = labelLayer.getURL();
            if (url.toLowerCase().startsWith("gdbp://mapgislocaltemp/tempgdb")) {
                if (labelLayer.get3DRange() != null) {
                    rtn = true;
                }
            }
        }
        return rtn;
    }

    /**
     * 通过选中树节点获取 TreeItem
     *
     * @param node 树节点
     * @return TreeItem
     */
    private TreeItem getTreeItemByNode(Node node) {
        if (node == null) {
            return null;
        } else if (node instanceof TreeCell) {
            return ((TreeCell) node).getTreeItem();
        } else {
            return getTreeItemByNode(node.getParent());
        }
    }

    /**
     * 设置 TreeItem 节点图标
     *
     * @param treeItem 树节点
     * @param index    图标索引
     * @param image    图标
     */
    private void setTreeItemImage(TreeItem treeItem, int index, Image image) {
        if (treeItem != null && image != null) {
            Node hBoxNode = treeItem.getGraphic();
            if (hBoxNode instanceof HBox) {
                ObservableList<Node> list = ((HBox) hBoxNode).getChildren();
                if (list != null && list.size() == 2) {
                    Node imageViewNode = list.get(index);
                    if (imageViewNode instanceof ImageView) {
                        ((ImageView) imageViewNode).setImage(image);
                    }
                }
            }
        }
    }

    /**
     * 设置 TreeItem 节点的状态图标
     *
     * @param treeItem   树节点
     * @param stateImage 状态图标
     */
    private void setTreeItemStateImage(TreeItem treeItem, Image stateImage) {
        setTreeItemImage(treeItem, 0, stateImage);
    }

    /**
     * 设置 TreeItem 节点的显示图标
     *
     * @param treeItem     树节点
     * @param displayImage 显示图标
     */
    private void setTreeItemDisplayImage(TreeItem treeItem, Image displayImage) {
        setTreeItemImage(treeItem, 1, displayImage);
    }

    /**
     * 监听 MenuExtender 的事件
     *
     * @param menuExtender 扩展菜单
     */
    private void listenMenuExtender(IMenuExtender menuExtender) {
        if (menuExtender != null && !this.menuExtenders.contains(menuExtender)) {
            this.menuExtenders.add(menuExtender);
            ((MenuExtender) menuExtender).addAddMenuItemListener(new AddMenuItemListener() {
                @Override
                public void fireAddItem(AddMenuItemEvent addMenuItemEvent) {
                    addMenuItem(addMenuItemEvent.getMenuItem());
                }
            });
            ((MenuExtender) menuExtender).addInsertMenuItemListener(new InsertMenuItemListener() {
                @Override
                public void fireInsertMenuItem(InsertMenuItemEvent insertMenuItemEvent) {
                    addMenuItem(insertMenuItemEvent.getMenuItem());
                }
            });
            ((MenuExtender) menuExtender).addRemoveMenuItemListener(new RemoveMenuItemListener() {
                @Override
                public void fireRemoveMenuItem(RemoveMenuItemEvent removeMenuItemEvent) {
                    IMenuItem menuItem = removeMenuItemEvent.getMenuItem();
                    if (menuItem != null) {
                        menuItems.remove(menuItem);
                        menuItemGroups.remove(menuItem);
                    }
                }
            });
        }
    }

    private MenuItem addMenuItem(IMenuItem menuItem) {
        MenuItem jfxmi = null;
        if (menuItem != null) {
            jfxmi = this.menuItems.getOrDefault(menuItem, null);
            if (jfxmi == null) {
                if (menuItem instanceof ISubMenu) {
                    jfxmi = new Menu();
                } else {
                    jfxmi = new CheckMenuItem();
                    ((CheckMenuItem) jfxmi).setSelected(menuItem.isChecked());
                }

                jfxmi.setText(menuItem.getCaption());
                jfxmi.setDisable(!menuItem.isEnabled());
                jfxmi.setVisible(menuItem.isVisible());
                jfxmi.setGraphic(new ImageView(menuItem.getImage()));

                if (this.menuItemGroups.containsKey(menuItem)) {
                    this.menuItemGroups.replace(menuItem, menuItem.isBeginGroup());
                }
                this.menuItems.put(menuItem, jfxmi);
            }
        }
        return jfxmi;
    }

    private void initMenuItem(IMenuItem menuItem, ObservableList<MenuItem> jfxMenuItems, boolean isTreeItem) {
        if (menuItem != null) {
            MenuItem mi = this.menuItems.getOrDefault(menuItem, null);
            boolean beginGroup = false;
            if (menuItem instanceof ISubMenu) {
                Menu menu = new Menu();
                ISubMenu subMenu = (ISubMenu) menuItem;
                if (subMenu.getItems() != null) {
                    menu.setText(mi != null ? mi.getText() : subMenu.getCaption());
                    menu.setDisable(mi != null ? mi.isDisable() : !subMenu.isEnabled());
                    menu.setVisible(mi != null ? mi.isVisible() : subMenu.isVisible());
                    menu.setUserData(subMenu);
                    menu.setGraphic(mi != null ? mi.getGraphic() : new ImageView(subMenu.getImage()));

                    for (IMenuItem item : subMenu.getItems()) {
                        this.initMenuItem(item, menu.getItems(), isTreeItem);
                    }
                    if (this.menuItemGroups.getOrDefault(menuItem, menuItem.isBeginGroup())) {
                        jfxMenuItems.add(new SeparatorMenuItem());
                    }
                    jfxMenuItems.add(menu);
                }
                mi = menu;
            } else {
                MenuItem tempMI = menuItem.isChecked() ? new CheckMenuItem() : new MenuItem();
                tempMI.setText(mi != null ? mi.getText() : menuItem.getCaption());
                tempMI.setDisable(mi != null ? mi.isDisable() : !menuItem.isEnabled());
                tempMI.setVisible(mi != null ? mi.isVisible() : menuItem.isVisible());
                if (menuItem.isChecked() && tempMI instanceof CheckMenuItem) {
                    ((CheckMenuItem) tempMI).setSelected((mi instanceof CheckMenuItem && ((CheckMenuItem) mi).isSelected()) ? ((CheckMenuItem) mi).isSelected() : menuItem.isChecked());
                }
                tempMI.setUserData(menuItem);
                tempMI.setGraphic(mi != null ? mi.getGraphic() : new ImageView(menuItem.getImage()));

                if (menuItem instanceof ISingleMenuItem || menuItem instanceof ISingleMenuItemEx) {
                    if (isTreeItem) {
                        //bbItem.ItemClick += new ItemClickEventHandler(this.PopupMenu_ItemClick);
                        tempMI.setOnAction(event -> {
                            MenuItem jfxcmi = ((MenuItem) event.getSource());
                            IMenuItem mi1 = ((IMenuItem) jfxcmi.getUserData());
                            TreeItem ti = treeView.getSelectionModel().getSelectedItem();
                            if (mi1 instanceof ISingleMenuItem) {
                                if (ti != null && ti.getValue() instanceof DocumentItem) {
                                    ((ISingleMenuItem) mi1).onClick((DocumentItem) ti.getValue());
                                }
                            } else if (mi1 instanceof ISingleMenuItemEx) {
                                ((ISingleMenuItemEx) mi1).onClick(ti.getValue());
                            }
                        });
                    } else {
                        //bbItem.ItemClick += new ItemClickEventHandler(this.GlobalPopupMenu_ItemClick);
                        tempMI.setOnAction(event -> {
                            MenuItem jfxcmi = ((MenuItem) event.getSource());
                            IMenuItem mi1 = ((IMenuItem) jfxcmi.getUserData());
                            if (mi1 instanceof ISingleMenuItem) {
                                ((ISingleMenuItem) mi1).onClick(document);
                            }
                        });
                    }
                } else if (menuItem instanceof IMultiMenuItem || menuItem instanceof IMultiMenuItemEx) {
                    //bbItem.ItemClick += new ItemClickEventHandler(this.MultiPopupMenu_ItemClick);
                    tempMI.setOnAction(event -> {
                        MenuItem jfxcmi = ((MenuItem) event.getSource());
                        IMenuItem mi12 = ((IMenuItem) jfxcmi.getUserData());
                        ObservableList<TreeItem<Object>> tis = treeView.getSelectionModel().getSelectedItems();
                        if (mi12 instanceof IMultiMenuItem && tis != null) {
                            DocumentItem[] docItems = new DocumentItem[tis.size()];
                            int index = 0;
                            for (TreeItem<Object> ti : tis) {
                                if (ti.getValue() instanceof DocumentItem) {
                                    docItems[index++] = (DocumentItem) ti.getValue();
                                }
                            }
                            ((IMultiMenuItem) mi12).onClick(docItems);
                        } else if (mi12 instanceof IMultiMenuItemEx && tis != null) {
                            Object[] docItems = new Object[tis.size()];
                            int index = 0;
                            for (TreeItem<Object> ti : tis) {
                                docItems[index++] = ti.getValue();
                            }
                            ((IMultiMenuItemEx) mi12).onClick(docItems);
                        }
                    });
                }

                if (this.menuItemGroups.getOrDefault(menuItem, menuItem.isBeginGroup())) {
                    jfxMenuItems.add(new SeparatorMenuItem());
                }
                jfxMenuItems.add(tempMI);

                mi = tempMI;
            }
            this.menuItems.remove(menuItem);
            this.menuItems.put(menuItem, mi);
            this.menuItemGroups.remove(menuItem);
            this.menuItemGroups.put(menuItem, beginGroup);
        }
    }

    private void showBlankAreaPopupMenu() {
        IItemStyle itemStyle = this.workspace.getItemStyle(ItemType.BLANKAREA);
        IMenuExtender menuExtender = this.workspace.getMenuExtender(ItemType.BLANKAREA);
        listenMenuExtender(menuExtender);
        IMenuItem[] menuItems = menuExtender.getItems();
        if (itemStyle != null && menuItems != null) {
            ObservableList<MenuItem> jfxMenuItems = treeView.getContextMenu().getItems();
            jfxMenuItems.clear();
            for (IMenuItem menuItem : menuItems) {
                this.initMenuItem(menuItem, jfxMenuItems, false);
            }
            if (itemStyle.getPopMenu() instanceof ISinglePopMenu) {
                ((ISinglePopMenu) itemStyle.getPopMenu()).opening(this.document);
            }
        }
    }

    // endregion

    // region 工作空间树事件

    private ArrayList<StateChangedListener> stateChangedListeners = new ArrayList<>();

    /**
     * 添加图层状态改变事件监听器
     *
     * @param stateChangedListener 图层状态改变事件监听器
     */
    public void addStateChangedListener(StateChangedListener stateChangedListener) {
        this.stateChangedListeners.add(stateChangedListener);
    }

    /**
     * 移除图层状态改变事件监听器
     *
     * @param stateChangedListener 图层状态改变事件监听器
     */
    public void removeStateChangedListener(StateChangedListener stateChangedListener) {
        this.stateChangedListeners.remove(stateChangedListener);
    }

    /**
     * 触发图层状态改变事件
     *
     * @param stateChangedEvent 图层状态改变事件
     */
    protected void fireStateChanged(StateChangedEvent stateChangedEvent) {
        for (StateChangedListener stateChangedListener : this.stateChangedListeners) {
            stateChangedListener.fireStateChanged(stateChangedEvent);
        }
    }

    private ArrayList<SelectionChangedListener> selectionChangedListeners = new ArrayList<>();

    /**
     * 添加选中项改变事件监听器
     *
     * @param selectionChangedListener 选中项改变事件监听器
     */
    public void addSelectionChangedListener(SelectionChangedListener selectionChangedListener) {
        this.selectionChangedListeners.add(selectionChangedListener);
    }

    /**
     * 移除选中项改变事件监听器
     *
     * @param selectionChangedListener 选中项改变事件监听器
     */
    public void removeSelectionChangedListener(SelectionChangedListener selectionChangedListener) {
        this.selectionChangedListeners.remove(selectionChangedListener);
    }

    /**
     * 触发选中项改变事件
     *
     * @param selectionChangedEvent 选中项改变事件
     */
    protected void fireSelectionChanged(SelectionChangedEvent selectionChangedEvent) {
        for (SelectionChangedListener selectionChangedListener : this.selectionChangedListeners) {
            selectionChangedListener.fireSelectionChanged(selectionChangedEvent);
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
     * 触发焦点节点改变事件
     *
     * @param focusedNodeChangedEvent 焦点节点改变事件
     */
    protected void fireFocusedNodeChanged(FocusedNodeChangedEvent focusedNodeChangedEvent) {
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
        if (itemMouseClickEvent != null && itemMouseClickEvent.getTheme() == null && itemMouseClickEvent.getThemeInfoIndex() < 0) {
            // TODO: 待 workspace 添加触发事件函数
            // this.workspace.OnClickNode(itemMouseClickEvent.getDocumentItem());
        }
    }

    private ArrayList<ItemMouseDoubleClickListener> itemMouseDoubleClickListeners = new ArrayList<>();

    /**
     * 添加双击节点事件监听器
     *
     * @param itemMouseDoubleClickListener 双击节点事件监听器
     */
    public void addItemMouseDoubleClickListener(ItemMouseDoubleClickListener itemMouseDoubleClickListener) {
        this.itemMouseDoubleClickListeners.add(itemMouseDoubleClickListener);
    }

    /**
     * 移除双击节点事件监听器
     *
     * @param itemMouseDoubleClickListener 双击节点事件监听器
     */
    public void removeItemMouseDoubleClickListener(ItemMouseDoubleClickListener itemMouseDoubleClickListener) {
        this.itemMouseDoubleClickListeners.remove(itemMouseDoubleClickListener);
    }

    /**
     * 触发双击节点事件
     *
     * @param itemMouseDoubleClickEvent 双击节点事件
     */
    protected void fireItemMouseDoubleClick(ItemMouseClickEvent itemMouseDoubleClickEvent) {
        for (ItemMouseDoubleClickListener itemMouseDoubleClickListener : this.itemMouseDoubleClickListeners) {
            itemMouseDoubleClickListener.fireItemMouseDoubleClick(itemMouseDoubleClickEvent);
        }
        if (itemMouseDoubleClickEvent != null && itemMouseDoubleClickEvent.getTheme() == null && itemMouseDoubleClickEvent.getThemeInfoIndex() < 0) {
            // TODO: 待 workspace 添加触发事件函数
            // this.workspace.OnClickNode(itemMouseDoubleClickEvent.getDocumentItem());
        }
    }

    private ArrayList<MenuItemClickListener> menuItemClickListeners = new ArrayList<>();

    /**
     * 添加执行单选节点右键菜单事件监听器
     *
     * @param menuItemClickListener 执行单选节点右键菜单事件监听器
     */
    public void addMenuItemClickListener(MenuItemClickListener menuItemClickListener) {
        this.menuItemClickListeners.add(menuItemClickListener);
    }

    /**
     * 移除执行单选节点右键菜单事件监听器
     *
     * @param menuItemClickListener 执行单选节点右键菜单事件监听器
     */
    public void removeMenuItemClickListener(MenuItemClickListener menuItemClickListener) {
        this.menuItemClickListeners.remove(menuItemClickListener);
    }

    /**
     * 触发执行单选节点右键菜单事件
     *
     * @param menuItemClickEvent 执行单选节点右键菜单事件
     */
    protected void fireMenuItemClick(MenuItemClickEvent menuItemClickEvent) {
        for (MenuItemClickListener menuItemClickListener : this.menuItemClickListeners) {
            menuItemClickListener.fireMenuItemClick(menuItemClickEvent);
        }
    }

    private ArrayList<MultiMenuItemClickListener> multiMenuItemClickListeners = new ArrayList<>();

    /**
     * 添加执行多选节点右键菜单事件监听器
     *
     * @param multiMenuItemClickListener 执行多选节点右键菜单事件监听器
     */
    public void addMultiMenuItemClickListener(MultiMenuItemClickListener multiMenuItemClickListener) {
        this.multiMenuItemClickListeners.add(multiMenuItemClickListener);
    }

    /**
     * 移除执行多选节点右键菜单事件监听器
     *
     * @param multiMenuItemClickListener 执行多选节点右键菜单事件监听器
     */
    public void removeMultiMenuItemClickListener(MultiMenuItemClickListener multiMenuItemClickListener) {
        this.multiMenuItemClickListeners.remove(multiMenuItemClickListener);
    }

    /**
     * 触发执行多选节点右键菜单事件
     *
     * @param multiMenuItemClickEvent 执行多选节点右键菜单事件
     */
    protected void fireMultiMenuItemClick(MultiMenuItemClickEvent multiMenuItemClickEvent) {
        for (MultiMenuItemClickListener multiMenuItemClickListener : this.multiMenuItemClickListeners) {
            multiMenuItemClickListener.fireMultiMenuItemClick(multiMenuItemClickEvent);
        }
    }

    // endregion

    // region Theme

    /**
     * 根据专题图子节点实时获取 ThemeInfo，同时还会重新设置改树节点值的 ThemeInfo，仅适用于二三维单值和分段专题图
     *
     * @param treeItem 树节点
     * @return 专题图信息
     */
    private ThemeInfo refreshThemeInfo(TreeItem<Object> treeItem) {
        ThemeInfo info = null;
        if (treeItem != null && treeItem.getValue() instanceof ThemeInfo) {
            if (treeItem.getParent() != null) {
                long count = 0;
                int index = treeItem.getParent().getChildren().indexOf(treeItem);
                if (treeItem.getParent().getValue() instanceof Theme) {
                    // region 更新矢量专题图信息
                    Theme theme = (Theme) treeItem.getParent().getValue();
                    ThemeType type = theme.getType();
                    if (ThemeType.UniqueTheme.equals(type)) {
                        count = ((UniqueTheme) theme).getCount();
                        if (index < count) {
                            info = ((UniqueTheme) theme).getItem(index);
                        } else if (index == count) {
                            info = ((UniqueTheme) theme).getDefaultInfo();
                        }
                    } else if (ThemeType.MultiExpClassTheme.equals(type)) {
                        count = ((MultiClassTheme) theme).getItemCount();
                        if (index < count) {
                            // TODO: 接口有误
                            //info = ((MultiClassTheme) theme).getItem(index);
                        } else if (index == count) {
                            info = ((MultiClassTheme) theme).getDefaultInfo();
                        }
                    }
                    // endregion
                } else if (treeItem.getParent().getValue() instanceof Theme3D) {
                    // region 更新三维专题图信息
                    Theme3D theme3D = (Theme3D) treeItem.getParent().getValue();
                    Theme3DType type = theme3D.getType();
                    if (Theme3DType.type3DUniqueTheme.equals(type)) {// TODO: 接口有误
                        count = 0;//((UniqueTheme3D) theme3D).getItemCount();
                        if (index < count) {
                            info = ((UniqueTheme3D) theme3D).getItem(index);
                        } else if (index == count) {
                            // TODO: 接口有误
                            info = ((UniqueTheme3D) theme3D).setDefaultInfo();
                        }
                    } else if (Theme3DType.type3DMultiClassTheme.equals(type)) {
                        count = ((MultiClassTheme3D) theme3D).getItemCount();
                        if (index < count) {
                            // TODO: 接口有误
                            //info = ((MultiClassTheme3D) theme3D).getItem(index);
                        } else if (index == count) {
                            // TODO: 接口有误
                            info = ((MultiClassTheme3D) theme3D).setDefaultInfo();
                        }
                    }
                    // endregion
                }
            }
            if (info != null) {
                treeItem.setValue(info);
            }
        }
        return info;
    }

    /**
     * 将树节点值的 ThemeInfo 写入到专题图中去，必须保证树节点值的信息是最新的，仅适用于二三维单值和分段专题图
     *
     * @param treeItem 树节点
     */
    private void pushThemeInfo(TreeItem<Object> treeItem) {
        if (treeItem != null && treeItem.getParent() != null && treeItem.getValue() instanceof ThemeInfo) {
            ThemeInfo info = (ThemeInfo) treeItem.getValue();
            long count = 0;
            int index = treeItem.getParent().getChildren().indexOf(treeItem);
            if (treeItem.getParent().getValue() instanceof Theme) {
                // region 更新矢量专题图信息
                Theme theme = (Theme) treeItem.getParent().getValue();
                ThemeType type = theme.getType();
                if (ThemeType.UniqueTheme.equals(type)) {
                    count = ((UniqueTheme) theme).getCount();
                    if (index < count) {
                        ((UniqueTheme) theme).setItem(index, (info instanceof UniqueThemeInfo ? (UniqueThemeInfo) info : null));
                    } else if (index == count) {
                        ((UniqueTheme) theme).setDefaultInfo(info);
                    }
                } else if (ThemeType.MultiExpClassTheme.equals(type)) {
                    count = ((MultiClassTheme) theme).getItemCount();
                    if (index < count) {
                        // TODO: 接口有误
                        //((MultiClassTheme) theme).setItem(index, (info instanceof MultiClassThemeInfo ? (MultiClassThemeInfo) info : null));
                    } else if (index == count) {
                        ((MultiClassTheme) theme).setDefaultInfo(info);
                    }
                }
                // endregion
            } else if (treeItem.getParent().getValue() instanceof Theme3D) {
                // region 更新三维专题图信息
                Theme3D theme3D = (Theme3D) treeItem.getParent().getValue();
                Theme3DType type = theme3D.getType();
                if (Theme3DType.type3DUniqueTheme.equals(type)) {// TODO: 接口错误
                    count = 0;//((UniqueTheme3D)theme3D).getItemCount();
                    if (index < count) {
                        ((UniqueTheme3D) theme3D).setItem(index, (info instanceof UniqueThemeInfo ? (UniqueThemeInfo) info : null));
                    } else if (index == count) {
                        ((UniqueTheme3D) theme3D).setDefaultInfo(info);
                    }
                } else if (Theme3DType.type3DMultiClassTheme.equals(type)) {
                    count = ((MultiClassTheme3D) theme3D).getItemCount();
                    if (index < count) {
                        // TODO: 接口错误
                        //((MultiClassTheme3D) theme3D).setItem(index, (info instanceof MultiClassThemeInfo ? (MultiClassThemeInfo) info : null));
                    } else if (index == count) {
                        ((MultiClassTheme3D) theme3D).setDefaultInfo(info);
                    }
                }
                // endregion
            }
        }
    }

    // endregion

    private DisplayState getDisplayState(LayerState layerState) {
        DisplayState displayState = null;
        if (LayerState.UnVisible.equals(layerState)) {
            displayState = DisplayState.UNVISIBLE;
        } else if (LayerState.Visible.equals(layerState)) {
            displayState = DisplayState.VISIBLE;
        } else if (LayerState.Editable.equals(layerState)) {
            displayState = DisplayState.EDITABLE;
        } else if (LayerState.Active.equals(layerState)) {
            displayState = DisplayState.ACTIVE;
        } else {
            displayState = DisplayState.UNVISIBLE;
        }
        return displayState;
    }

    private TreeItem<Object> createTreeItem(Object userData, DisplayState displayState, Image typeImage) {
        if (userData == null || displayState == null || typeImage == null) {
            return null;
        }
        Image stateImage = null;
        switch (displayState) {
            case UNVISIBLE:
                stateImage = images.get("Png_NodeUnVisible_18_16");
                break;
            case VISIBLE:
                stateImage = images.get("Png_NodeVisible_18_16");
                break;
            case EDITABLE:
                stateImage = images.get("Png_NodeEditable_18_16");
                break;
            case ACTIVE:
                stateImage = images.get("Png_NodeActive_18_16");
                break;
            case INTERMEDIATE:
                stateImage = images.get("Png_NodeIndeterminate_18_16");
                break;
            default:
                stateImage = images.get("Png_NodeVisible_18_16");
        }
        TreeItem<Object> treeItem = new TreeItem<>(userData);
        ImageView stateImageView = new ImageView(stateImage);
        stateImageView.setPickOnBounds(true);
        ImageView typeImageView = new ImageView(typeImage);
        typeImageView.setPickOnBounds(true);
        HBox hBox = new HBox(stateImageView, typeImageView);
        hBox.setUserData(displayState);
        treeItem.setGraphic(hBox);
        return treeItem;
    }

    private DisplayState getDisplayState(TreeItem treeItem) {
        DisplayState displayState = DisplayState.UNVISIBLE;
        if (treeItem != null) {
            displayState = (DisplayState) treeItem.getGraphic().getUserData();
        }
        return displayState;
    }

    private void setDisplayState(TreeItem treeItem, DisplayState displayState) {
        if (treeItem == null || displayState == null) {
            return;
        }
        HBox hBox = (HBox) treeItem.getGraphic();
        hBox.setUserData(displayState);
        ImageView stateImageView = (ImageView) hBox.getChildren().get(0);
        ImageView typeImageView = (ImageView) hBox.getChildren().get(1);
        Image stateImage = null;
        switch (displayState) {
            case UNVISIBLE:
                stateImage = images.get("Png_NodeUnVisible_18_16");
                break;
            case VISIBLE:
                stateImage = images.get("Png_NodeVisible_18_16");
                break;
            case EDITABLE:
                stateImage = images.get("Png_NodeEditable_18_16");
                break;
            case ACTIVE:
                stateImage = images.get("Png_NodeActive_18_16");
                break;
            case INTERMEDIATE:
                stateImage = images.get("Png_NodeIndeterminate_18_16");
                break;
            default:
                stateImage = images.get("Png_NodeUnVisible_18_16");
        }
        stateImageView.setImage(stateImage);
    }
}
