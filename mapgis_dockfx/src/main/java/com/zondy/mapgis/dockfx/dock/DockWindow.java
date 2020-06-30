package com.zondy.mapgis.dockfx.dock;

import com.zondy.mapgis.dockfx.pane.DockNodeTab;
import javafx.beans.property.*;
import javafx.event.Event;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * 停靠节点基类，它提供节点内容以及标题栏和样式化边框。停靠节点可以分离并浮动或关闭，并从布局中移除。拖动行为通过标题栏实现。
 *
 * @author CR
 * @create 2019-09-09.
 */
public class DockWindow extends VBox implements EventHandler<MouseEvent> {
    //region 变量
    /**
     * 浮动时节点的StageStyle
     */
    private static StageStyle stageStyle = StageStyle.TRANSPARENT;
    /**
     * 浮动时的CSS伪类选择器。
     */
    private static final PseudoClass FLOATING_PSEUDO_CLASS = PseudoClass.getPseudoClass("floating");
    /**
     * 停靠时的CSS伪类选择器。
     */
    private static final PseudoClass DOCKED_PSEUDO_CLASS = PseudoClass.getPseudoClass("docked");
    /**
     * 最大化时的CSS伪类选择器。
     */
    private static final PseudoClass MAXIMIZED_PSEUDO_CLASS = PseudoClass.getPseudoClass("maximized");
    /**
     * 最小化时的CSS伪类选择器。
     */
    private static final PseudoClass MINIMIZED_PSEUDO_CLASS = PseudoClass.getPseudoClass("minimized");

    /**
     * 浮动时使用的Stage
     */
    private Stage stage;
    /**
     * 节点包含的界面内容
     */
    private Node contents;
    /**
     * 节点的标题栏
     */
    private DockTitleBar dockTitleBar;
    /**
     * 浮动时用于提供样式化自定义边框的边框窗格。
     */
    private BorderPane borderPane;
    /**
     * 停靠时使用的停靠布局容器
     */
    private DockPane dockPane;

    //endregion

    //region 构造函数

    /**
     * 构造停靠节点
     */
    public DockWindow() {
        this((Node) null);
    }

    /**
     * 构造停靠节点
     *
     * @param contents 界面界面内容
     */
    public DockWindow(Node contents) {
        this(contents, null, null);
    }

    /**
     * 构造停靠节点
     *
     * @param contents 界面界面内容
     * @param title    节点标题
     */
    public DockWindow(Node contents, String title) {
        this(contents, title, null);
    }

    /**
     * 构造停靠节点
     *
     * @param contents 界面界面内容
     * @param title    节点标题
     * @param graphic  节点图标
     */
    public DockWindow(Node contents, String title, Node graphic) {
        this.titleProperty.setValue(title);
        this.graphicProperty.setValue(graphic);
        this.contents = contents;

        dockTitleBar = new DockTitleBar(this);
        getChildren().add(dockTitleBar);
        if (contents != null) {
            getChildren().add(contents);
            VBox.setVgrow(contents, Priority.ALWAYS);
            double width = contents.prefWidth(0);
            double height = contents.prefHeight(0);
            if (width <= 0) {
                width = 240;
            }
            if (height <= 0) {
                height = 200;
            }
            this.setPrefSize(width, height);
        }

        this.getStyleClass().add("dock-window");
    }

    /**
     * 从fxml读取节点界面内容并构造节点
     *
     * @param fxmlPath fxml文件路径
     */
    public DockWindow(String fxmlPath) {
        this(fxmlPath, null, null);
    }

    /**
     * 从fxml读取节点界面内容并构造节点
     *
     * @param fxmlPath fxml文件路径
     * @param title    节点标题
     */
    public DockWindow(String fxmlPath, String title) {
        this(fxmlPath, title, null);
    }

    /**
     * 从fxml读取节点界面内容并构造节点
     *
     * @param fxmlPath fxml文件路径
     * @param title    节点标题
     * @param graphic  节点图标
     */
    public DockWindow(String fxmlPath, String title, Node graphic) {
        this(loadNode(fxmlPath), title, graphic);
    }
    //endregion

    /**
     * 加载fxml界面
     *
     * @param fxmlPath fxml文件路径
     * @return fxml中的界面根部控件
     */
    static Node loadNode(String fxmlPath) {
        Node node = null;
        FXMLLoader loader = new FXMLLoader();
        try {
            node = loader.load(DockWindow.class.getResourceAsStream(fxmlPath));
        } catch (Exception e) {
            e.printStackTrace();
            loader.setRoot(new StackPane(new Label("无法加载" + fxmlPath)));
        }
        return node;
    }

    //region 属性getter&setter

    /**
     * 设置浮动时节点的StageStyle
     *
     * @param stageStyle Stage风格
     */
    public void setStageStyle(StageStyle stageStyle) {
        DockWindow.stageStyle = stageStyle;
    }

    /**
     * 获取节点界面内容
     *
     * @return 节点界面内容
     */
    public final Node getContents() {
        return contents;
    }

    /**
     * 设置节点界面内容
     *
     * @param contents 界面内容控件
     */
    public void setContents(Node contents) {
        this.getChildren().set(this.getChildren().indexOf(this.contents), contents);
        this.contents = contents;
    }

    /**
     * 获取标题栏
     *
     * @return 标题栏
     */
    public final DockTitleBar getDockTitleBar() {
        return this.dockTitleBar;
    }

    /**
     * 设置标题栏
     *
     * @param dockTitleBar 标题栏
     */
    public void setDockTitleBar(DockTitleBar dockTitleBar) {
        if (dockTitleBar != null) {
            if (this.dockTitleBar != null) {
                this.getChildren().set(this.getChildren().indexOf(this.dockTitleBar), dockTitleBar);
            } else {
                this.getChildren().add(0, dockTitleBar);
            }
        } else {
            this.getChildren().remove(this.dockTitleBar);
        }

        this.dockTitleBar = dockTitleBar;
    }

    /**
     * 设置节点是否浮动
     *
     * @param floating 是否浮动
     */
    public void setFloating(boolean floating) {
        setFloating(floating, null);
    }

    /**
     * 设置节点是否浮动
     *
     * @param floating    是否浮动
     * @param translation 设置为浮动后的节点偏移量。可以为null。
     */
    public void setFloating(boolean floating, Point2D translation) {
        if (floating && !this.isFloating()) {
            Point2D floatScene = this.localToScene(0, 0);
            Point2D floatScreen = this.localToScreen(0, 0);

            dockTitleBar.setVisible(this.isCustomTitleBar());
            dockTitleBar.setManaged(this.isCustomTitleBar());

            if (this.isDocked()) {
                this.undock();
            }

            stage = new Stage();
            stage.titleProperty().bind(titleProperty);
            if (dockPane != null && dockPane.getScene() != null && dockPane.getScene().getWindow() != null) {
                stage.initOwner(dockPane.getScene().getWindow());
            }

            stage.initStyle(stageStyle);

            //偏移新的舞台，以准确地覆盖停靠点在场景本地的区域，这对于用户按下+号并且我们没有鼠标单击位置的信息很有用
            Point2D stagePosition;
            boolean translateToCenter = false;
            if (this.isDecorated()) {
                Window owner = stage.getOwner();
                stagePosition = floatScene.add(new Point2D(owner.getX(), owner.getY()));
            } else if (floatScreen != null) {
                stagePosition = floatScreen;
            } else {
                translateToCenter = true;
                Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
                double centerX = (primScreenBounds.getWidth() - Math.max(getWidth(), getMinWidth())) / 2;
                double centerY = (primScreenBounds.getHeight() - Math.max(getHeight(), getMinHeight())) / 2;
                stagePosition = new Point2D(centerX, centerY);
            }
            if (translation != null) {
                stagePosition = stagePosition.add(translation);
            }

            //border pane允许停靠节点在边框上具有阴影效果，但也可以保持内容的布局，例如不包含任何内容的选项卡
            borderPane = new BorderPane();
            borderPane.getStyleClass().add("dock-window-border");
            borderPane.setCenter(this);

            Scene scene = new Scene(borderPane);

            this.floatingProperty.set(floating);
            this.applyCss();

            borderPane.applyCss();
            Insets insetsDelta = borderPane.getInsets();

            stage.setScene(scene);

            double insetsWidth = insetsDelta.getLeft() + insetsDelta.getRight();
            double insetsHeight = insetsDelta.getTop() + insetsDelta.getBottom();
            if (translateToCenter) {
                stage.setX(stagePosition.getX() - insetsDelta.getLeft() - (borderPane.getPrefWidth() / 2.0));
                stage.setY(stagePosition.getY() - insetsDelta.getTop() - (borderPane.getPrefHeight() / 2.0));
            } else {

                stage.setX(Math.max(0, stagePosition.getX() - insetsDelta.getLeft()));
                stage.setY(Math.max(0, stagePosition.getY() - insetsDelta.getTop()));
            }

            double width = this.getWidth() == 0 ? this.getPrefWidth() : this.getWidth();
            double height = this.getHeight() == 0 ? this.getPrefHeight() : this.getHeight();
            stage.setMinWidth(borderPane.minWidth(width) + insetsWidth);
            stage.setMinHeight(borderPane.minHeight(height) + insetsHeight);

            borderPane.setPrefSize(width + insetsWidth, height + insetsHeight);

            if (stageStyle == StageStyle.TRANSPARENT) {
                scene.setFill(null);
            }

            stage.setResizable(this.isStageResizable());
            if (this.isStageResizable()) {
                stage.addEventFilter(MouseEvent.MOUSE_PRESSED, this);
                stage.addEventFilter(MouseEvent.MOUSE_MOVED, this);
                stage.addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            }
            stage.sizeToScene();
            stage.show();
            System.out.println("stage: " + stage.getScene().getFocusOwner());
        } else if (!floating && this.isFloating()) {
            this.floatingProperty.set(floating);

            stage.removeEventFilter(MouseEvent.MOUSE_PRESSED, this);
            stage.removeEventFilter(MouseEvent.MOUSE_MOVED, this);
            stage.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);

            stage.close();
        }
    }

    /**
     * 获取DockPane
     *
     * @return DockPane
     */
    public final DockPane getDockPane() {
        return dockPane;
    }

    /**
     * 与该停靠节点关联的Stage。 如果停靠节点从未设置为浮动，则为null。
     *
     * @return 节点关联的Stage
     */
    public final Stage getStage() {
        return stage;
    }

    /**
     * 获取浮动时用于提供样式化自定义边框的边框窗格
     *
     * @return 浮动时用于提供样式化自定义边框的边框窗格
     */
    public final BorderPane getBorderPane() {
        return borderPane;
    }

    /**
     * 标题栏图标对象属性
     *
     * @defaultValue null
     */
    public final ObjectProperty<Node> graphicProperty() {
        return graphicProperty;
    }

    private ObjectProperty<Node> graphicProperty = new SimpleObjectProperty<Node>() {
        @Override
        public String getName() {
            return "graphic";
        }
    };

    public final Node getGraphic() {
        return graphicProperty.get();
    }

    public final void setGraphic(Node graphic) {
        this.graphicProperty.setValue(graphic);
    }

    /**
     * 标题栏标题对象属性
     *
     * @defaultValue "Dock"
     */
    public final StringProperty titleProperty() {
        return titleProperty;
    }

    private StringProperty titleProperty = new SimpleStringProperty("Dock") {
        @Override
        public String getName() {
            return "title";
        }
    };

    public final String getTitle() {
        return titleProperty.get();
    }

    public final void setTitle(String title) {
        this.titleProperty.setValue(title);
    }

    /**
     * Boolean属性，用于维护此节点当前是否正在使用自定义标题栏。 这可用于强制默认标题栏显示何时停靠节点设置为浮动而不是使用本机窗口边框。
     *
     * @defaultValue true
     */
    public final BooleanProperty customTitleBarProperty() {
        return customTitleBarProperty;
    }

    private BooleanProperty customTitleBarProperty = new SimpleBooleanProperty(true) {
        @Override
        public String getName() {
            return "customTitleBar";
        }
    };

    public final boolean isCustomTitleBar() {
        return customTitleBarProperty.get();
    }

    public final void setUseCustomTitleBar(boolean useCustomTitleBar) {
        if (this.isFloating()) {
            dockTitleBar.setVisible(useCustomTitleBar);
            dockTitleBar.setManaged(useCustomTitleBar);
        }
        this.customTitleBarProperty.set(useCustomTitleBar);
    }

    /**
     * 浮动状态对象属性
     *
     * @defaultValue false
     */
    public final BooleanProperty floatingProperty() {
        return floatingProperty;
    }

    private BooleanProperty floatingProperty = new SimpleBooleanProperty(false) {
        @Override
        protected void invalidated() {
            DockWindow.this.pseudoClassStateChanged(FLOATING_PSEUDO_CLASS, get());
            if (borderPane != null) {
                borderPane.pseudoClassStateChanged(FLOATING_PSEUDO_CLASS, get());
            }
        }

        @Override
        public String getName() {
            return "floating";
        }
    };

    public final boolean isFloating() {
        return floatingProperty.get();
    }

    /**
     * 能否浮动标志的对象属性
     *
     * @defaultValue true
     */
    public final BooleanProperty floatableProperty() {
        return floatableProperty;
    }

    public static boolean isSystemLinux()
    {
        String osName = System.getProperty("os.name", "");
        return osName.startsWith("Linux");
    }

    private BooleanProperty floatableProperty = new SimpleBooleanProperty(isSystemLinux()) {
        @Override
        public String getName() {
            return "floatable";
        }
    };

    public final boolean isFloatable() {
        return floatableProperty.get();
    }

    public final void setFloatable(boolean floatable) {
        if (!floatable && this.isFloating()) {
            this.setFloating(false);
        }
        this.floatableProperty.set(floatable);
    }

    /**
     * 能够修改大小的对象属性
     *
     * @defaultValue true
     */
    public final BooleanProperty resizableProperty() {
        return stageResizableProperty;
    }

    private BooleanProperty stageResizableProperty = new SimpleBooleanProperty(true) {
        @Override
        public String getName() {
            return "resizable";
        }
    };

    public final boolean isStageResizable() {
        return stageResizableProperty.get();
    }

    public final void setStageResizable(boolean resizable) {
        stageResizableProperty.set(resizable);
    }

    /**
     * 停靠状态的对象属性
     *
     * @defaultValue false
     */
    public final BooleanProperty dockedProperty() {
        return dockedProperty;
    }

    private BooleanProperty dockedProperty = new SimpleBooleanProperty(false) {
        @Override
        protected void invalidated() {
            if (get()) {
                if (dockTitleBar != null) {
                    dockTitleBar.setVisible(true);
                    dockTitleBar.setManaged(true);
                }
            }

            DockWindow.this.pseudoClassStateChanged(DOCKED_PSEUDO_CLASS, get());
        }

        @Override
        public String getName() {
            return "docked";
        }
    };

    public final boolean isDocked() {
        return dockedProperty.get();
    }

    /**
     * 是否用Tab页停靠对象属性
     *
     * @defaultValue false
     */
    public final BooleanProperty tabbedProperty() {
        return tabbedProperty;
    }

    private BooleanProperty tabbedProperty = new SimpleBooleanProperty(false) {
        @Override
        protected void invalidated() {

            if (getChildren() != null && dockTitleBar != null) {
                if (get()) {
                    getChildren().remove(dockTitleBar);
                } else {
                    getChildren().add(0, dockTitleBar);
                }
            }
        }

        @Override
        public String getName() {
            return "tabbed";
        }
    };

    public final boolean isTabbed() {
        return tabbedProperty.get();
    }

    private BooleanProperty minimizedProperty = new SimpleBooleanProperty(false) {
        @Override
        protected void invalidated() {
            DockWindow.this.pseudoClassStateChanged(MINIMIZED_PSEUDO_CLASS, get());
            if (borderPane != null) {
                borderPane.pseudoClassStateChanged(MINIMIZED_PSEUDO_CLASS, get());
            }

            ////最小化的实现
            //stage.setMaximized(get());
            //
            //if (this.get()) {
            //    Screen screen = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight()).get(0);
            //    Rectangle2D bounds = screen.getVisualBounds();
            //
            //    stage.setX(bounds.getMinX());
            //    stage.setY(bounds.getMinY());
            //
            //    stage.setWidth(bounds.getWidth());
            //    stage.setHeight(bounds.getHeight());
            //}
        }

        @Override
        public String getName() {
            return "minimized";
        }
    };

    public final BooleanProperty minimizedProperty() {
        return minimizedProperty;
    }

    public final boolean isMinimized() {
        return minimizedProperty.get();
    }

    public final void setMinimized(boolean minimized) {
        if (null != stage) {
            stage.setIconified(minimized);
            this.minimizedProperty.set(minimized);
        }
    }

    private BooleanProperty minimizableProperty = new SimpleBooleanProperty(false) {
        @Override
        public String getName() {
            return "minimizable";
        }
    };

    /**
     * 能否最小化对象属性
     *
     * @return
     */
    public final BooleanProperty minimizableProperty() {
        return minimizableProperty;
    }

    public final boolean isMinimizable() {
        return minimizableProperty.get();
    }

    public final void setMinimizable(boolean minimizable) {
        this.minimizableProperty.set(minimizable);
    }

    public final boolean isDecorated() {
        return stageStyle != StageStyle.TRANSPARENT && stageStyle != StageStyle.UNDECORATED;
    }

    /**
     * 最大化状态对象属性
     *
     * @defaultValue false
     */
    private BooleanProperty maximizedProperty = new SimpleBooleanProperty(false) {
        @Override
        protected void invalidated() {
            DockWindow.this.pseudoClassStateChanged(MAXIMIZED_PSEUDO_CLASS, get());
            if (borderPane != null) {
                borderPane.pseudoClassStateChanged(MAXIMIZED_PSEUDO_CLASS, get());
            }

            stage.setMaximized(get());

            // TODO: This is a work around to fill the screen bounds and not overlap the task bar when
            // the window is undecorated as in Visual Studio. A similar work around needs applied for
            // JFrame in Swing. http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4737788
            // Bug report filed:
            // https://bugs.openjdk.java.net/browse/JDK-8133330
            if (this.get()) {
                Screen screen = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight()).get(0);
                Rectangle2D bounds = screen.getVisualBounds();

                stage.setX(bounds.getMinX());
                stage.setY(bounds.getMinY());

                stage.setWidth(bounds.getWidth());
                stage.setHeight(bounds.getHeight());
            }
        }

        @Override
        public String getName() {
            return "maximized";
        }
    };

    public final BooleanProperty maximizedProperty() {
        return maximizedProperty;
    }

    public final boolean isMaximized() {
        return maximizedProperty.get();
    }

    public final void setMaximized(boolean maximized) {
        maximizedProperty.set(maximized);
    }

    private BooleanProperty maximizableProperty = new SimpleBooleanProperty(true) {
        @Override
        public String getName() {
            return "maximizable";
        }
    };

    /**
     * 能否最大化的对象属性
     *
     * @return
     */
    public final BooleanProperty maximizableProperty() {
        return maximizableProperty;
    }

    public final boolean isMaximizable() {
        return maximizableProperty.get();
    }

    public final void setMaximizable(boolean maximizable) {
        this.maximizableProperty.set(maximizable);
    }

    /**
     * 关闭状态对象属性
     */
    public final BooleanProperty closedProperty() {
        return closedProperty;
    }

    protected BooleanProperty closedProperty = new SimpleBooleanProperty(false) {
        @Override
        protected void invalidated() {
        }

        @Override
        public String getName() {
            return "closed";
        }
    };

    public final boolean isClosed() {
        return closedProperty.get();
    }

    /**
     * 能否被关闭的对象属性
     *
     * @defaultValue true
     */
    public final BooleanProperty closableProperty() {
        return closableProperty;
    }

    private BooleanProperty closableProperty = new SimpleBooleanProperty(true) {
        @Override
        public String getName() {
            return "closable";
        }
    };

    public final boolean isClosable() {
        return closableProperty.get();
    }

    public final void setClosable(boolean closable) {
        this.closableProperty.set(closable);
    }
    //endregion

    //region 事件
    private final ObjectProperty<EventHandler<DockEvent>> onDockClosing = new ObjectPropertyBase<EventHandler<DockEvent>>() {
        @Override
        protected void invalidated() {
            setEventHandler(DockEvent.DOCKCLOSING, get());
        }

        @Override
        public Object getBean() {
            return this;
        }

        @Override
        public String getName() {
            return "onDockClosing";
        }
    };
    private final ObjectProperty<EventHandler<DockEvent>> onDockClosed = new ObjectPropertyBase<EventHandler<DockEvent>>() {
        @Override
        protected void invalidated() {
            setEventHandler(DockEvent.DOCKCLOSED, get());
        }

        @Override
        public Object getBean() {
            return this;
        }

        @Override
        public String getName() {
            return "onDockClosed";
        }
    };
    private final ObjectProperty<EventHandler<DockEvent>> onDockSelected = new ObjectPropertyBase<EventHandler<DockEvent>>() {
        @Override
        protected void invalidated() {
            setEventHandler(DockEvent.DOCKSELECTED, get());
        }

        @Override
        public Object getBean() {
            return this;
        }

        @Override
        public String getName() {
            return "onDockSelected";
        }
    };

    public EventHandler<DockEvent> getOnDockClosing() {
        return onDockClosing.get();
    }

    public ObjectProperty<EventHandler<DockEvent>> onDockClosingProperty() {
        return onDockClosing;
    }

    public void setOnDockClosing(EventHandler<DockEvent> onDockClosing) {
        this.onDockClosing.set(onDockClosing);
    }

    public EventHandler<DockEvent> getOnDockClosed() {
        return onDockClosed.get();
    }

    public ObjectProperty<EventHandler<DockEvent>> onDockClosedProperty() {
        return onDockClosed;
    }

    public void setOnDockClosed(EventHandler<DockEvent> onDockClosed) {
        this.onDockClosed.set(onDockClosed);
    }

    public EventHandler<DockEvent> getOnDockSelected() {
        return onDockSelected.get();
    }

    public ObjectProperty<EventHandler<DockEvent>> onDockSelectedProperty() {
        return onDockSelected;
    }

    public void setOnDockSelected(EventHandler<DockEvent> onDockSelected) {
        this.onDockSelected.set(onDockSelected);
    }
    //endregion

    //region 其他
    private DockPos lastDockPos;

    public DockPos getLastDockPos() {
        return lastDockPos;
    }

    private Node lastDockSibling;

    public Node getLastDockSibling() {
        return lastDockSibling;
    }

    public void dockImpl(DockPane dockPane, DockPos dockPos, Node sibling) {
        if (isFloating()) {
            setFloating(false);
        }
        this.dockPane = dockPane;
        this.dockedProperty.set(true);
        this.closedProperty.set(false);
        this.lastDockPos = dockPos;// (dockPos == DockPos.CENTER && sibling instanceof DockWindow) ? ((DockWindow) sibling).getLastDockPos() : dockPos;
        this.lastDockSibling = sibling;
    }

    /**
     * 分离停靠的节点让其处理浮动
     */
    public void undock() {
        if (dockPane != null) {
            dockPane.undock(this);
        }
        this.dockedProperty.set(false);
        this.tabbedProperty.set(false);
    }

    /**
     * 关闭节点
     */
    public void close() {
        DockEvent closingEvent = new DockEvent(this, DockEvent.DOCKCLOSING, this, false);
        Event.fireEvent(this, closingEvent);
        if (closingEvent.isCancel()) {
            return;
        }

        this.closedProperty.set(true);
        dockPane.getAllNodes().remove(this);
        if (isFloating()) {
            setFloating(false);
        } else if (isDocked()) {
            undock();
        }
        Event.fireEvent(this, new DockEvent(this, DockEvent.DOCKCLOSED, this));
    }

    private DockNodeTab dockNodeTab;

    public void setNodeTab(DockNodeTab nodeTab) {
        this.dockNodeTab = nodeTab;
    }

    public DockNodeTab getNodeTab() {
        return dockNodeTab;
    }

    public void focus() {
        if (tabbedProperty().get()) {
            dockNodeTab.select();
        }
    }

    /**
     * 鼠标在最小布局范围内的最后位置.
     */
    private Point2D sizeLast;
    /**
     * 当前是否正在按给定方向调整大小。
     */
    private boolean sizeWest = false, sizeEast = false, sizeNorth = false, sizeSouth = false;

    /**
     * 鼠标是否正在调整大小的区域上.
     *
     * @return Whether the mouse is currently in this dock node's resize zone.
     */
    public boolean isMouseResizeZone() {
        return sizeWest || sizeEast || sizeNorth || sizeSouth;
    }
    //endregion

    @Override
    public void handle(MouseEvent event) {
        Cursor cursor = Cursor.DEFAULT;

        // TODO: use escape to cancel resize/drag operation like visual studio
        if (!this.isFloating() || !this.isStageResizable()) {
            return;
        }

        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            sizeLast = new Point2D(event.getScreenX(), event.getScreenY());
        } else if (event.getEventType() == MouseEvent.MOUSE_MOVED) {
            Insets insets = borderPane.getPadding();

            sizeWest = event.getX() < insets.getLeft();
            sizeEast = event.getX() > borderPane.getWidth() - insets.getRight();
            sizeNorth = event.getY() < insets.getTop();
            sizeSouth = event.getY() > borderPane.getHeight() - insets.getBottom();

            if (sizeWest) {
                if (sizeNorth) {
                    cursor = Cursor.NW_RESIZE;
                } else if (sizeSouth) {
                    cursor = Cursor.SW_RESIZE;
                } else {
                    cursor = Cursor.W_RESIZE;
                }
            } else if (sizeEast) {
                if (sizeNorth) {
                    cursor = Cursor.NE_RESIZE;
                } else if (sizeSouth) {
                    cursor = Cursor.SE_RESIZE;
                } else {
                    cursor = Cursor.E_RESIZE;
                }
            } else if (sizeNorth) {
                cursor = Cursor.N_RESIZE;
            } else if (sizeSouth) {
                cursor = Cursor.S_RESIZE;
            }

            this.getScene().setCursor(cursor);
        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED && this.isMouseResizeZone()) {
            Point2D sizeCurrent = new Point2D(event.getScreenX(), event.getScreenY());
            Point2D sizeDelta = sizeCurrent.subtract(sizeLast);

            double newX = stage.getX(), newY = stage.getY(), newWidth = stage.getWidth(),
                    newHeight = stage.getHeight();

            if (sizeNorth) {
                newHeight -= sizeDelta.getY();
                newY += sizeDelta.getY();
            } else if (sizeSouth) {
                newHeight += sizeDelta.getY();
            }

            if (sizeWest) {
                newWidth -= sizeDelta.getX();
                newX += sizeDelta.getX();
            } else if (sizeEast) {
                newWidth += sizeDelta.getX();
            }

            // TODO: find a way to do this synchronously and eliminate the flickering of moving the stage
            // around, also file a bug report for this feature if a work around can not be found this
            // primarily occurs when dragging north/west but it also appears in native windows and Visual
            // Studio, so not that big of a concern.
            // Bug report filed:
            // https://bugs.openjdk.java.net/browse/JDK-8133332
            double currentX = sizeLast.getX(), currentY = sizeLast.getY();
            if (newWidth >= stage.getMinWidth()) {
                stage.setX(newX);
                stage.setWidth(newWidth);
                currentX = sizeCurrent.getX();
            }

            if (newHeight >= stage.getMinHeight()) {
                stage.setY(newY);
                stage.setHeight(newHeight);
                currentY = sizeCurrent.getY();
            }
            sizeLast = new Point2D(currentX, currentY);
            // we do not want the title bar getting these events
            // while we are actively resizing
            if (sizeNorth || sizeSouth || sizeWest || sizeEast) {
                event.consume();
            }
        }
    }
}
