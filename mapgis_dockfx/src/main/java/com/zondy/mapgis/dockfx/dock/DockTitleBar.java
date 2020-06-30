package com.zondy.mapgis.dockfx.dock;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 * @file DockTitleBar.java
 * @brief 停靠节点标题栏的基类，提供鼠标拖动功能，标题，停靠和状态操作。
 *
 * @author CR
 * @date 2020-6-12
 */
public class DockTitleBar extends HBox implements EventHandler<MouseEvent> {
    /**
     * 此标题栏所属的停靠节点
     */
    private DockWindow dockNode;
    /**
     * 标题栏上的显示标题和图标的标签
     */
    private Label label;
    /**
     * 标题栏上的关闭、浮动、最小化按钮
     */
    private Button minimizeButton, maximizeButton, restoreButton, floatButton, closeButton;

    /**
     * 构造待默认标题和按钮的标题栏
     *
     * @param dockNode 此标题栏所属的停靠节点
     */
    public DockTitleBar(DockWindow dockNode) {
        this.dockNode = dockNode;

        label = new Label("Dock Title Bar");
        label.textProperty().bind(dockNode.titleProperty());
        label.graphicProperty().bind(dockNode.graphicProperty());

        minimizeButton = new Button();
        minimizeButton.setOnAction(event -> dockNode.setMinimized(true));
        minimizeButton.visibleProperty().bind(dockNode.floatingProperty().and(dockNode.minimizableProperty()));
        minimizeButton.managedProperty().bind(minimizeButton.visibleProperty());

        maximizeButton = new Button();
        maximizeButton.setOnAction(event -> dockNode.setMaximized(true));
        maximizeButton.visibleProperty().bind(dockNode.floatingProperty().and(dockNode.maximizableProperty()).and(dockNode.maximizedProperty().not()));
        maximizeButton.managedProperty().bind(maximizeButton.visibleProperty());

        restoreButton = new Button();
        restoreButton.setOnAction(event -> dockNode.setMaximized(false));
        restoreButton.visibleProperty().bind(dockNode.maximizedProperty());
        restoreButton.managedProperty().bind(restoreButton.visibleProperty());

        //floatButton = new Button();
        //floatButton.setOnAction(event -> dockNode.setFloating(true));
        //floatButton.visibleProperty().bind(dockNode.floatableProperty().and(dockNode.dockedProperty()));
        //floatButton.managedProperty().bind(floatButton.visibleProperty());

        closeButton = new Button();
        closeButton.setOnAction(event -> dockNode.close());
        closeButton.visibleProperty().bind(dockNode.closableProperty());
        closeButton.managedProperty().bind(closeButton.visibleProperty());

        this.addEventHandler(MouseEvent.MOUSE_PRESSED, this);
        this.addEventHandler(MouseEvent.DRAG_DETECTED, this);
        this.addEventHandler(MouseEvent.MOUSE_DRAGGED, this);
        this.addEventHandler(MouseEvent.MOUSE_RELEASED, this);

        label.getStyleClass().add("dock-title-label");
        minimizeButton.getStyleClass().add("dock-minimize-button");
        maximizeButton.getStyleClass().add("dock-maximize-button");
        restoreButton.getStyleClass().add("dock-restore-button");
        //floatButton.getStyleClass().add("dock-float-button");
        closeButton.getStyleClass().add("dock-close-button");
        this.getStyleClass().add("dock-title-bar");

        //创建一个Region可拉伸使按钮右对齐
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        getChildren().addAll(label, region, minimizeButton, maximizeButton, restoreButton/*, floatButton*/, closeButton);
    }

    /**
     * 获取此标题栏所属的停靠节点
     *
     * @return 此标题栏所属的停靠节点
     */
    public final DockWindow getDockNode() {
        return dockNode;
    }

    /**
     * 标识此标题栏是否正在被拖动
     */
    private boolean dragging = false;

    /**
     * 标识此标题栏是否正在被拖动。
     *
     * @return 此标题栏是否正在被拖动。
     */
    public final boolean isDragging() {
        return dragging;
    }

    /**
     * 最初点击的鼠标位置，用来确定拖动过程中的偏移量。标题栏拖动是异步的，因此不会像在Linux上大多数最新的JavaFX实现那样受到频率较低或滞后的鼠标事件的负面影响。
     */
    private Point2D dragStart;

    /**
     * 当前节点被拖到的每个窗口，可以跟踪输入/退出事件。
     */
    private HashMap<Window, Node> dragNodes = new HashMap<Window, Node>();

    /**
     * 选择停靠目标时要执行的任务。提供了有关应触发哪些特定事件和顺序的上下文。
     *
     * @since DockFX 0.1
     */
    private abstract class EventTask {
        /**
         * 执行此任务的次数
         */
        protected int executions = 0;

        /**
         * 执行任务
         *
         * @param node     事件目标节点
         * @param dragNode 最后一个事件目标节点
         */
        public abstract void run(Node node, Node dragNode);

        /**
         * 获取执行此任务的次数
         *
         * @return 执行此任务的次数
         */
        public int getExecutions() {
            return executions;
        }

        /**
         * 重置执行此任务的次数为0
         */
        public void reset() {
            executions = 0;
        }
    }

    /**
     * 遍历所有Stage，并根据位置为停靠事件选择事件目标。 选择了事件目标后，使用其和上一个目标（如果已缓存）运行事件任务。 如果未找到事件目标，则在阶段根上触发显式停靠事件（explicit）。
     *
     * @param location  停靠事件的屏幕坐标
     * @param eventTask 找到事件目标时要运行的事件任务
     * @param explicit  未找到事件目标时，将在阶段根上触发的显式事件
     */
    private void pickEventTarget(Point2D location, EventTask eventTask, Event explicit) {
        List<DockPane> dockPanes = DockPane.dockPanes;

        //激活的Stages发DockOver事件
        for (DockPane dockPane : dockPanes) {
            Window window = dockPane.getScene().getWindow();
            if (!(window instanceof Stage)) {
                continue;
            }
            Stage targetStage = (Stage) window;

            if (targetStage == this.dockNode.getStage())//当前标题栏不需要接收自己的事件
            {
                continue;
            }
            eventTask.reset();

            Node dragNode = dragNodes.get(targetStage);

            Parent root = targetStage.getScene().getRoot();
            Stack<Parent> stack = new Stack<Parent>();
            if (root.contains(root.screenToLocal(location.getX(), location.getY())) && !root.isMouseTransparent()) {
                stack.push(root);
            }
            // 深度优先遍历以找到与兴趣点相交的最深节点或父节点，没有子节点
            while (!stack.isEmpty()) {
                Parent parent = stack.pop();
                //如果此父级包含鼠标屏幕坐标，则遍历其子级
                boolean notFired = true;
                for (Node node : parent.getChildrenUnmodifiable()) {
                    if (node.contains(node.screenToLocal(location.getX(), location.getY())) && !node.isMouseTransparent()) {
                        if (node instanceof Parent) {
                            stack.push((Parent) node);
                        } else {
                            eventTask.run(node, dragNode);
                        }
                        notFired = false;
                        break;
                    }
                }
                if (notFired) {
                    eventTask.run(parent, dragNode);
                }
            }

            if (explicit != null && dragNode != null && eventTask.getExecutions() < 1) {
                Event.fireEvent(dragNode, explicit.copyFor(this, dragNode));
                dragNodes.put(targetStage, null);
            }
        }
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            if (dockNode.isFloating() && event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                dockNode.setMaximized(!dockNode.isMaximized());
            } else {
                dragStart = new Point2D(event.getX(), event.getY());
            }
        } else if (event.getEventType() == MouseEvent.DRAG_DETECTED) {
            if (!dockNode.isFloating()) {
                if (!dockNode.isCustomTitleBar() && dockNode.isDecorated()) {
                    dockNode.setFloating(true, new Point2D(0, DockTitleBar.this.getHeight()));
                } else {
                    dockNode.setFloating(true);
                }
                if (DockWindow.isSystemLinux()) {
                    //Linux拖动时间用else里面的方案解决不了，于是，直接release掉。
                    dragging = false;

                    DockEvent dockReleasedEvent = new DockEvent(this, DockEvent.NULL_SOURCE_TARGET, DockEvent.DOCKRELEASED, event.getX(), event.getY(), event.getScreenX(), event.getScreenY(), this.getDockNode());
                    EventTask eventTask = new EventTask() {
                        @Override
                        public void run(Node node, Node dragNode) {
                            executions++;
                            if (dragNode != node) {
                                Event.fireEvent(node, dockReleasedEvent.copyFor(DockTitleBar.this, node));
                            }
                            Event.fireEvent(node, dockReleasedEvent.copyFor(DockTitleBar.this, node));
                        }
                    };

                    this.pickEventTarget(new Point2D(event.getScreenX(), event.getScreenY()), eventTask, null);

                    dragNodes.clear();
                } else {
                    //临时决从Scene中拖动到另一个场景中丢失属性表时间的问题。此为javafx的一个bug（https://bugs.openjdk.java.net/browse/JDK-8133335）
                    DockPane dockPane = this.getDockNode().getDockPane();
                    if (dockPane != null) {
                        dockPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
                        dockPane.addEventFilter(MouseEvent.MOUSE_RELEASED, this);
                    }
                }
            } else if (dockNode.isMaximized()) {
                double ratioX = event.getX() / this.getDockNode().getWidth();
                double ratioY = event.getY() / this.getDockNode().getHeight();

                //请注意，setMaximized被Stage上发生的宽度和高度变化破坏了，需要通过保存恢复的边界来手动实现最大化的行为。问题在于，DockWindow.java中的调整大小功能正在同时执行，以取消最大化的更改。
                dockNode.setMaximized(false);
                dragStart = new Point2D(ratioX * dockNode.getWidth(), ratioY * dockNode.getHeight());
            }
            dragging = true;
            event.consume();
        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            if (dockNode.isFloating() && event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                event.setDragDetect(false);
                event.consume();
                return;
            }

            if (!dragging) {
                return;
            }

            Stage stage = dockNode.getStage();
            Insets insetsDelta = this.getDockNode().getBorderPane().getInsets();

            if (null == dragStart) {
                dragStart = new Point2D(event.getX(), event.getY());
            }

            //以这种方式进行拖动可以使接口在系统滞后的情况下更加响应，就像大多数最新的LinuxFX Java实现一样
            stage.setX(event.getScreenX() - dragStart.getX() - insetsDelta.getLeft());
            stage.setY(event.getScreenY() - dragStart.getY() - insetsDelta.getTop());

            DockEvent dockEnterEvent = new DockEvent(this, DockEvent.NULL_SOURCE_TARGET, DockEvent.DOCKENTER, event.getX(), event.getY(), event.getScreenX(), event.getScreenY(), this.getDockNode());
            DockEvent dockOverEvent = new DockEvent(this, DockEvent.NULL_SOURCE_TARGET, DockEvent.DOCKOVER, event.getX(), event.getY(), event.getScreenX(), event.getScreenY(), this.getDockNode());
            DockEvent dockExitEvent = new DockEvent(this, DockEvent.NULL_SOURCE_TARGET, DockEvent.DOCKEXIT, event.getX(), event.getY(), event.getScreenX(), event.getScreenY(), this.getDockNode());

            EventTask eventTask = new EventTask() {
                @Override
                public void run(Node node, Node dragNode) {
                    executions++;

                    if (dragNode != node) {
                        Event.fireEvent(node, dockEnterEvent.copyFor(DockTitleBar.this, node));

                        if (dragNode != null) {
                            //首先触发Dock Exit，以便跟踪当前所处的目标节点
                            Event.fireEvent(dragNode, dockExitEvent.copyFor(DockTitleBar.this, dragNode));
                        }

                        dragNodes.put(node.getScene().getWindow(), node);
                    }
                    Event.fireEvent(node, dockOverEvent.copyFor(DockTitleBar.this, node));
                }
            };

            this.pickEventTarget(new Point2D(event.getScreenX(), event.getScreenY()), eventTask, dockExitEvent);
        } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
            dragging = false;

            DockEvent dockReleasedEvent = new DockEvent(this, DockEvent.NULL_SOURCE_TARGET, DockEvent.DOCKRELEASED, event.getX(), event.getY(), event.getScreenX(), event.getScreenY(), this.getDockNode());
            EventTask eventTask = new EventTask() {
                @Override
                public void run(Node node, Node dragNode) {
                    executions++;
                    if (dragNode != node) {
                        Event.fireEvent(node, dockReleasedEvent.copyFor(DockTitleBar.this, node));
                    }
                    Event.fireEvent(node, dockReleasedEvent.copyFor(DockTitleBar.this, node));
                }
            };

            this.pickEventTarget(new Point2D(event.getScreenX(), event.getScreenY()), eventTask, null);

            dragNodes.clear();

            //删除上述错误的临时事件处理程序。
            DockPane dockPane = this.getDockNode().getDockPane();
            if (dockPane != null) {
                dockPane.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
                dockPane.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
            }
        }
    }
}
