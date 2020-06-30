package com.zondy.mapgis.dockfx.dock;

import com.sun.javafx.css.StyleManager;
import com.zondy.mapgis.dockfx.pane.ContentPane;
import com.zondy.mapgis.dockfx.pane.ContentSplitPane;
import com.zondy.mapgis.dockfx.pane.ContentTabPane;
import com.zondy.mapgis.dockfx.pane.DockNodeTab;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * @file DockPane.java
 * @brief 停靠布局容器
 *
 * @author CR
 * @date 2020-6-12
 */
public class DockPane extends StackPane implements EventHandler<DockEvent> {
    //region 变量

    private static DockWindow oldSelectedWindow;
    private static DockView oldSelectedView;

    /**
     * 用于事件鼠标选取的所有DockPanes的包私有内部列表。
     */
    static List<DockPane> dockPanes = new ArrayList<DockPane>();

    /**
     * 当前停靠区域的根窗口
     */
    private Node root;
    /**
     * 拖拽的目标区域容器，是DockWindow
     */
    private Node dockNodeDrag;
    /**
     * 拖拽的目标区域容器。是DockWindow或ContentPane
     */
    private Node dockAreaDrag;
    /**
     * 在指示器中选择的按钮对应的停靠位置
     */
    private DockPos dockPosDrag;
    /**
     * 没有窗口的空白区域只显示Center按钮
     */
    private Popup popupDockEmpty;
    /**
     * 显示当前区域的圆形指示器（圆形加上下左右中五个按钮）。
     */
    private Popup popupDockCurrent;
    /**
     * 显示根部停靠指示器的弹出窗口（应用程序全局的上下左右四个按钮）。
     */
    private Popup popupDockRoot;
    /**
     * 空白区域的Center指示器
     */
    private VBox paneDockEmpty;
    /**
     * 中心圆形区域从用于布置方位指示器按钮的网格
     */
    private GridPane peneDockCurrent;
    /**
     * 停靠效果预览框
     */
    private Rectangle dockEffectPreview;
    /**
     * DOCK_ENTER事件接收标志
     */
    private boolean receivedEnter = false;
    /**
     * 内容视图占位Tab
     */
    private static ContentTabPane viewTabPane;
    /**
     * 用于管理指示器按钮并在DOCK_OVER事件期间自动执行命中检测的集合。（包含全局的上下左右和当前区域的上下左右中9个按钮）
     */
    private ObservableList<DockPosButton> dockPosButtons;
    /**
     * 停靠布局中的所有窗口节点
     */
    private final List<DockWindow> allNodes = new LinkedList<>();
    //endregion

    /**
     * 构造 停靠布局容器
     */
    public DockPane() {
        super();
        DockPane.dockPanes.add(this);

        this.addEventHandler(DockEvent.ANY, this);
        this.addEventFilter(DockEvent.ANY, event ->
        {
            if (event.getEventType() == DockEvent.DOCKENTER) {
                DockPane.this.receivedEnter = true;
            } else if (event.getEventType() == DockEvent.DOCKOVER) {
                DockPane.this.dockNodeDrag = null;
            }
        });

        popupDockCurrent = new Popup();
        popupDockCurrent.setAutoFix(false);
        popupDockRoot = new Popup();
        popupDockRoot.setAutoFix(false);
        popupDockEmpty = new Popup();
        popupDockEmpty.setAutoFix(false);

        dockEffectPreview = new Rectangle();
        dockEffectPreview.setManaged(false);
        dockEffectPreview.setMouseTransparent(true);

        DockPosButton dockCenter = new DockPosButton(false, DockPos.CENTER);
        dockCenter.getStyleClass().add("dock-center");
        DockPosButton dockTop = new DockPosButton(false, DockPos.TOP);
        dockTop.getStyleClass().add("dock-top");
        DockPosButton dockRight = new DockPosButton(false, DockPos.RIGHT);
        dockRight.getStyleClass().add("dock-right");
        DockPosButton dockBottom = new DockPosButton(false, DockPos.BOTTOM);
        dockBottom.getStyleClass().add("dock-bottom");
        DockPosButton dockLeft = new DockPosButton(false, DockPos.LEFT);
        dockLeft.getStyleClass().add("dock-left");

        DockPosButton dockTopRoot = new DockPosButton(true, DockPos.TOP);
        StackPane.setAlignment(dockTopRoot, Pos.TOP_CENTER);
        dockTopRoot.getStyleClass().add("dock-top-root");

        DockPosButton dockRightRoot = new DockPosButton(true, DockPos.RIGHT);
        StackPane.setAlignment(dockRightRoot, Pos.CENTER_RIGHT);
        dockRightRoot.getStyleClass().add("dock-right-root");

        DockPosButton dockBottomRoot = new DockPosButton(true, DockPos.BOTTOM);
        StackPane.setAlignment(dockBottomRoot, Pos.BOTTOM_CENTER);
        dockBottomRoot.getStyleClass().add("dock-bottom-root");

        DockPosButton dockLeftRoot = new DockPosButton(true, DockPos.LEFT);
        StackPane.setAlignment(dockLeftRoot, Pos.CENTER_LEFT);
        dockLeftRoot.getStyleClass().add("dock-left-root");

        DockPosButton dockFill = new DockPosButton(false, DockPos.CENTER);
        dockFill.getStyleClass().add("dock-fill");

        dockPosButtons = FXCollections.observableArrayList(dockCenter, dockTop, dockRight, dockBottom, dockLeft, dockFill, dockTopRoot, dockRightRoot, dockBottomRoot, dockLeftRoot);

        StackPane stackPaneRoot = new StackPane();
        stackPaneRoot.prefWidthProperty().bind(this.widthProperty());
        stackPaneRoot.prefHeightProperty().bind(this.heightProperty());
        stackPaneRoot.getChildren().addAll(dockEffectPreview, dockTopRoot, dockRightRoot, dockBottomRoot, dockLeftRoot);

        peneDockCurrent = new GridPane();
        peneDockCurrent.add(dockLeft, 0, 1);
        peneDockCurrent.add(dockTop, 1, 0);
        peneDockCurrent.add(dockCenter, 1, 1);
        peneDockCurrent.add(dockBottom, 1, 2);
        peneDockCurrent.add(dockRight, 2, 1);

        paneDockEmpty = new VBox();
        paneDockEmpty.getStyleClass().add("dock-fill-indicator");
        paneDockEmpty.getChildren().addAll(dockFill);

        popupDockRoot.getContent().add(stackPaneRoot);
        popupDockCurrent.getContent().add(peneDockCurrent);
        popupDockEmpty.getContent().addAll(paneDockEmpty);

        this.getStyleClass().add("dock-pane");
        stackPaneRoot.getStyleClass().add("dock-root-pane");
        peneDockCurrent.getStyleClass().add("dock-pos-indicator");
        dockEffectPreview.getStyleClass().add("dock-area-indicator");
    }

    /**
     * 获取空白的用于放置内容视图的窗口容器
     *
     * @return 空白的用于放置内容视图的窗口容器
     */
    public static ContentTabPane getViewTabPane() {
        return viewTabPane;
    }

    @Override
    public String getUserAgentStylesheet() {
        return DockPane.class.getResource("/dockfx.css").toExternalForm();
    }

    /**
     * 初始化css样式
     */
    public final static void initDefaultUserAgentStylesheet() {
        StyleManager.getInstance().addUserAgentStylesheet(DockPane.class.getResource("/dockfx.css").toExternalForm());
    }

    //region 激活选中

    /**
     * 获取旧的选中窗口
     *
     * @return
     */
    public static DockWindow getOldSelectedWindow() {
        return oldSelectedWindow;
    }

    /**
     * 设置旧的选中窗口
     *
     * @param oldSelectedWindow
     */
    public static void setOldSelectedWindow(DockWindow oldSelectedWindow) {
        DockPane.oldSelectedWindow = oldSelectedWindow;
    }

    /**
     * 获取旧的选中视图
     *
     * @return
     */
    public static DockView getOldSelectedView() {
        return oldSelectedView;
    }

    /**
     * 设置旧的选中视图
     *
     * @param oldSelectedView
     */
    public static void setOldSelectedView(DockView oldSelectedView) {
        DockPane.oldSelectedView = oldSelectedView;
    }
    //endregion

    //region 停靠
    /**
     * 用于跟踪当前停靠区域的所有停靠节点事件处理程序的缓存。
     */
    private ObservableMap<Node, DockNodeEventHandler> dockNodeEventFilters = FXCollections.observableHashMap();

    /**
     * 停靠内容视图（到{@code viewTabPane}中）
     *
     * @param node 内容视图节点
     */
    public void dock(DockView node) {
        node.dockImpl(this, DockPos.CENTER, null);

        DockNodeEventHandler dockNodeEventHandler = new DockNodeEventHandler(this, node);
        dockNodeEventFilters.put(node, dockNodeEventHandler);
        node.addEventFilter(DockEvent.DOCKOVER, dockNodeEventHandler);

        if (!allNodes.contains(node)) {
            allNodes.add(node);
        }

        if (viewTabPane != null) {
            viewTabPane.addNode(root, viewTabPane, node, DockPos.CENTER);
        } else {
            viewTabPane = new ContentTabPane();
            viewTabPane.addNode(null, null, node, DockPos.RIGHT);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            viewTabPane.setPrefSize(screenSize.getWidth(), screenSize.getHeight() * 0.8);

            ContentPane pane = new ContentSplitPane(viewTabPane);
            ((ContentSplitPane) pane).setPrefSize(viewTabPane.getPrefWidth(), viewTabPane.getPrefHeight());
            root = (Node) pane;
            this.getChildren().add(root);
        }
    }

    /**
     * 停靠节点
     *
     * @param content 停靠节点里面的内容
     * @param dockPos 停靠位置
     * @param title   停靠节点的标题
     */
    public void dock(Node content, DockPos dockPos, String title) {
        dock(new DockWindow(content, title, null), dockPos);
    }

    /**
     * 停靠节点
     *
     * @param node    待停靠节点
     * @param dockPos 停靠位置
     */
    public void dock(DockWindow node, DockPos dockPos) {
        this.dock(node, dockPos, (Node) null);
    }

    /**
     * 停靠节点
     *
     * @param node    待停靠节点
     * @param dockPos 停靠位置
     * @param sibling 停靠位置的相对节点
     */
    public void dock(DockWindow node, DockPos dockPos, Node sibling) {
        if (sibling == null && dockPos != DockPos.CENTER) {
            for (DockWindow dw : this.getAllNodes()) {
                if (dw.isDocked() && dw.getLastDockPos() == dockPos) {
                    sibling = dw;
                    dockPos = DockPos.CENTER;
                    break;
                }
            }
        }
        node.dockImpl(this, dockPos, sibling);

        DockNodeEventHandler dockNodeEventHandler = new DockNodeEventHandler(this, node);
        dockNodeEventFilters.put(node, dockNodeEventHandler);
        node.addEventFilter(DockEvent.DOCKOVER, dockNodeEventHandler);

        if (!allNodes.contains(node)) {
            allNodes.add(node);
        }

        ContentPane pane = (ContentPane) root;
        if (pane == null) {
            pane = new ContentSplitPane(node);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            double wholeWidth = screenSize.getWidth();
            double wholeHeight = screenSize.getHeight() * 0.8;
            ((ContentSplitPane) pane).setPrefSize(wholeWidth, wholeHeight);

            SplitPane.setResizableWithParent(node, false);
            boolean isHorizontal = dockPos == DockPos.LEFT || dockPos == DockPos.RIGHT;
            ((ContentSplitPane) pane).setOrientation(isHorizontal ? Orientation.HORIZONTAL : Orientation.VERTICAL);

            double position = isHorizontal ? ContentPane.getRealLength(node, true) / wholeWidth : ContentPane.getRealLength(node, false) / wholeHeight;
            viewTabPane = new ContentTabPane();
            viewTabPane.setPrefSize(isHorizontal ? wholeWidth * (1 - position) : wholeWidth, isHorizontal ? wholeHeight : wholeHeight * (1 - position));
            if (dockPos == DockPos.RIGHT || dockPos == DockPos.BOTTOM) {
                position = 1 - position;
                ((ContentSplitPane) pane).add(0, viewTabPane);
            } else {
                ((ContentSplitPane) pane).add(viewTabPane);
            }
            ((ContentSplitPane) pane).setDividerPosition(0, position);

            root = (Node) pane;
            this.getChildren().add(root);
        } else {
            if (sibling == null) {
                sibling = root;
            } else if (sibling != root) {
                Stack<Parent> stack = new Stack<>();
                stack.push((Parent) root);
                pane = pane.getSiblingParent(stack, sibling);
                if (pane == null) {
                    sibling = root;
                    dockPos = DockPos.RIGHT;
                    pane = (ContentPane) root;
                }
            }

            if (dockPos == DockPos.CENTER) {
                if (sibling instanceof DockWindow) {
                    if (pane instanceof ContentSplitPane) {
                        // 创建ContentTabPane添加两个节点
                        DockWindow siblingNode = (DockWindow) sibling;

                        ContentTabPane tabPane = new ContentTabPane();
                        tabPane.addDockNodeTab(new DockNodeTab(siblingNode));
                        tabPane.addDockNodeTab(new DockNodeTab(node));
                        tabPane.setPrefSize(siblingNode.getPrefWidth(), siblingNode.getPrefHeight());

                        pane.set(sibling, tabPane);
                        SplitPane.setResizableWithParent(tabPane, false);
                    }
                } else if (sibling instanceof ContentTabPane) {
                    pane = (ContentTabPane) sibling;
                } else if (sibling instanceof ContentSplitPane) {
                    ContentSplitPane siblingSplitPane = (ContentSplitPane) sibling;
                    ContentPane parent = siblingSplitPane.getContentParent();
                    if (parent == null) {
                        Node child = siblingSplitPane.getChildrenList().get(0);
                        if (child instanceof DockWindow) {
                            ContentTabPane tabPane = new ContentTabPane();
                            tabPane.addNode(root, null, child, DockPos.CENTER);
                            tabPane.setPrefSize(((DockWindow) child).getPrefWidth(), ((DockWindow) child).getPrefHeight());
                            siblingSplitPane.set(child, tabPane);
                            SplitPane.setResizableWithParent(tabPane, false);

                            pane = tabPane;
                            sibling = null;
                        } else if (child instanceof ContentSplitPane) {
                            pane = (ContentSplitPane) child;
                            dockPos = DockPos.LEFT;
                        } else if (child instanceof ContentTabPane) {
                            pane = (ContentTabPane) child;
                        }
                    }
                }
            } else {
                Orientation requestedOrientation = (dockPos == DockPos.LEFT || dockPos == DockPos.RIGHT) ? Orientation.HORIZONTAL : Orientation.VERTICAL;
                if (pane instanceof ContentSplitPane) {
                    ContentSplitPane split = (ContentSplitPane) pane;
                    if (split.getOrientation() != requestedOrientation) {
                        if (split.getItems().size() > 1) {
                            ContentSplitPane splitPane = new ContentSplitPane();
                            if (split == root && sibling == root) {
                                this.getChildren().set(this.getChildren().indexOf(root), splitPane);
                                splitPane.setPrefSize(split.getPrefWidth(), split.getPrefHeight());
                                splitPane.add(split);
                                root = splitPane;
                            } else {
                                split.set(sibling, splitPane);
                                splitPane.setPrefSize(((Region) sibling).getPrefWidth(), ((Region) sibling).getPrefHeight());
                                splitPane.add(sibling);
                            }
                            split = splitPane;
                        }
                        split.setOrientation(requestedOrientation);
                        pane = split;
                    }
                } else if (pane instanceof ContentTabPane) {
                    if (pane.getContentParent() != null) {
                        ContentSplitPane split = (ContentSplitPane) pane.getContentParent();
                        if (split.getOrientation() != requestedOrientation) {
                            ContentSplitPane splitPane = new ContentSplitPane();
                            if (split == root && sibling == root) {
                                this.getChildren().set(this.getChildren().indexOf(root), splitPane);
                                splitPane.setPrefSize(split.getPrefWidth(), split.getPrefHeight());
                                splitPane.add(split);
                                root = splitPane;
                            } else {
                                sibling = (Node) pane;
                                split.set(sibling, splitPane);
                                splitPane.setPrefSize(((Region) sibling).getPrefWidth(), ((Region) sibling).getPrefHeight());
                                splitPane.add(sibling);
                            }
                            split = splitPane;
                        } else {
                            sibling = (Node) pane;
                        }

                        split.setOrientation(requestedOrientation);
                        pane = split;
                    } else {
                        ContentSplitPane split = new ContentSplitPane();
                        sibling = (Node) pane;
                        split.add(sibling);
                        split.setOrientation(requestedOrientation);
                        pane = split;
                    }
                }
            }
            pane.addNode(root, sibling, node, dockPos);
            if (pane instanceof ContentSplitPane) {
                SplitPane.setResizableWithParent(node, false);
            }
        }
    }

    /**
     * 从停靠布局中移除停靠着的节点
     *
     * @param node 节点
     */
    public void undock(DockWindow node) {
        DockNodeEventHandler dockNodeEventHandler = dockNodeEventFilters.get(node);
        node.removeEventFilter(DockEvent.DOCKOVER, dockNodeEventHandler);
        dockNodeEventFilters.remove(node);

        //深度优先搜索以找到节点的父级
        Stack<Parent> findStack = new Stack<Parent>();
        findStack.push((Parent) root);
        while (!findStack.isEmpty()) {
            Parent parent = findStack.pop();
            if (parent instanceof ContentPane) {
                ContentPane pane = (ContentPane) parent;
                pane.removeNode(findStack, node);

                // 移除没有子节点的pane
                if (pane.getChildrenList().isEmpty()) {
                    if (root == pane) {
                        this.getChildren().remove(pane);
                        root = null;
                        viewTabPane = null;
                    }
                } else if (pane.getChildrenList().size() == 1 && pane instanceof ContentTabPane && pane.getChildrenList().get(0) instanceof DockWindow) {
                    //如果ContentTabPane仅剩1个标签，将其替换为SplitPane
                    List<Node> children = pane.getChildrenList();
                    Node sibling = children.get(0);
                    ContentPane contentParent = pane.getContentParent();

                    contentParent.set((Node) pane, sibling);
                    ((DockWindow) sibling).tabbedProperty().setValue(false);
                }
            }
        }
    }
    //endregion

    /**
     * 获取布局中的所有节点（包含浮动的节点）
     *
     * @return
     */
    public List<DockWindow> getAllNodes() {
        return allNodes;
    }

    /**
     * 设置拖动过程中到达的目标节点
     *
     * @param node 目标节点
     */
    public void setDockNodeDrag(Node node) {
        dockNodeDrag = node;
    }

    /**
     * 添加节点事件
     *
     * @param dockNode 节点
     * @param handler
     */
    public void addDockNodeEventFilter(DockWindow dockNode, DockNodeEventHandler handler) {
        dockNodeEventFilters.put(dockNode, handler);
    }

    /**
     * 设置停靠的根Split
     *
     * @param root
     */
    public void setRoot(ContentSplitPane root) {
        this.root = root;
    }

    /**
     * 关闭所有节点的stage
     */
    public void dispose() {
        List<DockWindow> temp = new ArrayList<>(allNodes);
        allNodes.clear();
        for (DockWindow node : temp) {
            if (node.getStage() != null) {
                node.getStage().close();
            }
        }
    }
    @Override
    public void handle(DockEvent event) {
        if (event.getEventType() == DockEvent.DOCKENTER) {
            if (!popupDockRoot.isShowing()) {
                Point2D originToScreen;
                if (null != root) {
                    originToScreen = root.localToScreen(0, 0);
                } else {
                    originToScreen = this.localToScreen(0, 0);
                }

                popupDockRoot.show(this.getScene().getWindow(), originToScreen.getX(), originToScreen.getY());
            }
        } else if (event.getEventType() == DockEvent.DOCKOVER) {
            this.receivedEnter = false;
            dockPosDrag = null;
            dockAreaDrag = dockNodeDrag;

            //region 读取停靠位置
            for (DockPosButton dockIndicatorButton : dockPosButtons) {
                if (dockIndicatorButton.contains(dockIndicatorButton.screenToLocal(event.getScreenX(), event.getScreenY()))) {
                    dockPosDrag = dockIndicatorButton.getDockPos();
                    if (dockIndicatorButton.isDockRoot()) {
                        dockAreaDrag = root;
                    }
                    dockIndicatorButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), true);
                    break;
                } else {
                    dockIndicatorButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), false);
                }
            }
            if (dockAreaDrag == null && viewTabPane.getTabs().size() == 0 && viewTabPane.localToScreen(viewTabPane.getLayoutBounds()).contains(event.getScreenX(), event.getScreenY())) {
                //为null的情况包括空的viewTabPane上，和ContentTabPane标题栏的空白区域。
                dockAreaDrag = viewTabPane;
            }
            //endregion

            //region 显示停靠后的效果预览
            if (dockPosDrag != null && dockAreaDrag != null) {
                Point2D originToScene = dockAreaDrag.localToScreen(0, 0);
                dockEffectPreview.setVisible(true);//拖拽结果效果预览框
                dockEffectPreview.relocate(originToScene.getX() - popupDockRoot.getAnchorX(), originToScene.getY() - popupDockRoot.getAnchorY());
                double calWidth = dockAreaDrag.getLayoutBounds().getWidth() / 2;
                double calHeight = dockAreaDrag.getLayoutBounds().getHeight() / 2;
                if (dockPosDrag == DockPos.RIGHT) {
                    dockEffectPreview.setTranslateX(calWidth);
                } else {
                    dockEffectPreview.setTranslateX(0);
                }

                if (dockPosDrag == DockPos.BOTTOM) {
                    dockEffectPreview.setTranslateY(calHeight);
                } else {
                    dockEffectPreview.setTranslateY(0);
                }

                if (dockPosDrag == DockPos.LEFT || dockPosDrag == DockPos.RIGHT) {
                    dockEffectPreview.setWidth(calWidth);
                } else {
                    dockEffectPreview.setWidth(dockAreaDrag.getLayoutBounds().getWidth());
                }
                if (dockPosDrag == DockPos.TOP || dockPosDrag == DockPos.BOTTOM) {
                    dockEffectPreview.setHeight(calHeight);
                } else {
                    dockEffectPreview.setHeight(dockAreaDrag.getLayoutBounds().getHeight());
                }
            } else {
                dockEffectPreview.setVisible(false);
            }
            //endregion

            //region 显示当前区域的圆形方位指示器
            if (dockNodeDrag != null && ((DockWindow) dockNodeDrag).getDockTitleBar() != null) {
                popupDockEmpty.hide();
                Point2D originToScreen = dockNodeDrag.localToScreen(0, 0);
                double posX = originToScreen.getX() + dockNodeDrag.getLayoutBounds().getWidth() / 2 - peneDockCurrent.getWidth() / 2;
                double posY = originToScreen.getY() + dockNodeDrag.getLayoutBounds().getHeight() / 2 - peneDockCurrent.getHeight() / 2;
                if (!popupDockCurrent.isShowing()) {
                    popupDockCurrent.show(DockPane.this, posX, posY);
                } else {
                    popupDockCurrent.setX(posX);
                    popupDockCurrent.setY(posY);
                }
            } else if (viewTabPane.getTabs().size() == 0 && viewTabPane.localToScreen(viewTabPane.getLayoutBounds()).contains(event.getScreenX(), event.getScreenY())) {
                popupDockCurrent.hide();
                Point2D originToScreen = viewTabPane.localToScreen(0, 0);
                double posX = originToScreen.getX() + viewTabPane.getLayoutBounds().getWidth() / 2 - paneDockEmpty.getWidth() / 2;
                double posY = originToScreen.getY() + viewTabPane.getLayoutBounds().getHeight() / 2 - paneDockEmpty.getHeight() / 2;
                if (!popupDockEmpty.isShowing()) {
                    popupDockEmpty.show(DockPane.this, posX, posY);
                } else {
                    popupDockEmpty.setX(posX);
                    popupDockEmpty.setY(posY);
                }
            } else {
                popupDockEmpty.hide();
                popupDockCurrent.hide();
            }
            //endregion
        }

        if (event.getEventType() == DockEvent.DOCKRELEASED && event.getContents() != null) {
            if (dockPosDrag != null && popupDockRoot.isShowing()) {
                DockWindow dockNode = (DockWindow) event.getContents();
                this.dock(dockNode, dockPosDrag, dockAreaDrag);
            }
        }

        if ((event.getEventType() == DockEvent.DOCKEXIT && !this.receivedEnter) || event.getEventType() == DockEvent.DOCKRELEASED) {
            if (popupDockRoot.isShowing()) {
                popupDockRoot.hide();
            }
            if (popupDockCurrent.isShowing()) {
                popupDockCurrent.hide();
            }
            if (popupDockEmpty.isShowing()) {
                popupDockEmpty.hide();
            }
        }
    }
}
