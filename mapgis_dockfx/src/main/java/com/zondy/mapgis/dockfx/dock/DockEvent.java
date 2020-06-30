package com.zondy.mapgis.dockfx.dock;

import com.sun.javafx.scene.input.InputEventUtils;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.input.PickResult;

/**
 * @file DockEvent.java
 * @brief 事件基类。每个事件都有一个关联的源、目标和类型。
 *
 * @author CR
 * @date 2019-09-09
 */
public class DockEvent extends Event {
    /**
     * 序列化
     */
    private static final long serialVersionUID = 23780820L;

    /**
     * 所有dock事件类型的公共超类型。
     */
    public static final EventType<DockEvent> ANY = new EventType<DockEvent>(Event.ANY, "DOCK");

    //region 关闭事件

    /**
     * DockWindow的关闭前事件。
     */
    public static final EventType<DockEvent> DOCKCLOSING = new EventType<DockEvent>(DockEvent.ANY, "DOCKCLOSING");

    /**
     * DockWindow的关闭后事件。
     */
    public static final EventType<DockEvent> DOCKCLOSED = new EventType<DockEvent>(DockEvent.ANY, "DOCKCLOSED");

    /**
     * DockWindow的选中（激活）事件。
     */
    public static final EventType<DockEvent> DOCKSELECTED = new EventType<DockEvent>(DockEvent.ANY, "DOCK_SELECTED");

    /**
     * 窗口关闭事件
     *
     * @param source    要关闭的窗口对象
     * @param eventType 事件类型.
     */
    public DockEvent(Object source, EventType<? extends DockEvent> eventType, DockWindow dockWindow) {
        this(source, eventType, dockWindow, null);
    }

    /**
     * 窗口关闭前事件
     *
     * @param source    要关闭的窗口对象
     * @param eventType The type of the event.
     * @param isCancel  是否取消关闭
     */
    public DockEvent(Object source, EventType<? extends DockEvent> eventType, DockWindow dockWindow, boolean isCancel) {
        this(source, eventType, dockWindow, null);
        this.isCancel = isCancel;
    }

    /**
     * 窗口选中/激活事件
     *
     * @param source    要关闭的窗口对象
     * @param oldWindow
     * @param eventType The type of the event.
     */
    public DockEvent(Object source, EventType<? extends DockEvent> eventType, DockWindow dockWindow, DockWindow oldWindow) {
        super(source, null, eventType);
        this.dockWindow = dockWindow;
        this.oldDockWindow = oldWindow;
    }

    /**
     * 窗口对象
     */
    private DockWindow dockWindow;
    /**
     * 旧的选中窗口
     */
    private DockWindow oldDockWindow;

    /**
     * 窗口对象
     *
     * @return 要关闭的窗口对象
     */
    public final DockWindow getDockWindow() {
        return dockWindow;
    }

    /**
     * 旧的选中窗口
     *
     * @return 要关闭的窗口对象
     */
    public final DockWindow getOldDockWindow() {
        return oldDockWindow;
    }

    /**
     * 是否取消关闭
     */
    private boolean isCancel = false;

    /**
     * 获取是否取消了关闭
     *
     * @return
     */
    public boolean isCancel() {
        return isCancel;
    }

    /**
     * 设置是否要取消关闭
     *
     * @param isCancel
     */
    public void setCancel(boolean isCancel) {
        this.isCancel = isCancel;
    }
    //endregion

    //region 拖动停靠事件

    /**
     * 当拖动停靠窗口标题栏并且鼠标进入节点边界时，会发生此事件。与{@code DragEvent}不同，Dock over事件被传递给可能对接收停靠窗格感兴趣的所有stage。
     */
    public static final EventType<DockEvent> DOCKENTER = new EventType<DockEvent>(DockEvent.ANY, "DOCKENTER");

    /**
     * 当拖动停靠窗口标题栏并且鼠标包含在节点的边界中时，会发生此事件。
     */
    public static final EventType<DockEvent> DOCKOVER = new EventType<DockEvent>(DockEvent.ANY, "DOCKOVER");

    /**
     * 当拖动停靠窗口标题栏并且鼠标退出节点的边界时，会发生此事件。
     */
    public static final EventType<DockEvent> DOCKEXIT = new EventType<DockEvent>(DockEvent.ANY, "DOCKEXIT");

    /**
     * 当拖动停靠窗口标题栏并在节点的边界上释放鼠标时，会发生此事件。
     */
    public static final EventType<DockEvent> DOCKRELEASED = new EventType<DockEvent>(DockEvent.ANY, "DOCKRELEASED");

    /**
     * 构造事件对象
     *
     * @param eventType 事件类型
     * @param x         事件相对于DockEvent节点原点的X坐标
     * @param y         事件相对于DockEvent节点原点的Y坐标
     * @param screenX   获取事件相对于屏幕左上角的X坐标
     * @param screenY   获取事件相对于屏幕左上角的Y坐标
     */
    public DockEvent(EventType<? extends DockEvent> eventType, double x, double y, double screenX, double screenY) {
        this(null, null, eventType, x, y, screenX, screenY);
    }

    /**
     * 构造事件对象
     *
     * @param source    事件源，可为null。
     * @param target    事件目标，可为null。
     * @param eventType 事件类型
     * @param x         事件相对于DockEvent节点原点的X坐标
     * @param y         事件相对于DockEvent节点原点的Y坐标
     * @param screenX   获取事件相对于屏幕左上角的X坐标
     * @param screenY   获取事件相对于屏幕左上角的Y坐标
     */
    public DockEvent(Object source, EventTarget target, EventType<? extends DockEvent> eventType, double x, double y, double screenX, double screenY) {
        this(source, target, eventType, x, y, screenX, screenY, null);
    }

    /**
     * 构造事件对象
     *
     * @param source    事件源，可为null。
     * @param target    事件目标，可为null。
     * @param eventType 事件类型
     * @param x         事件相对于DockEvent节点原点的X坐标
     * @param y         事件相对于DockEvent节点原点的Y坐标
     * @param screenX   获取事件相对于屏幕左上角的X坐标
     * @param screenY   获取事件相对于屏幕左上角的Y坐标
     * @param contents  正在拖动的节点
     */
    public DockEvent(Object source, EventTarget target, EventType<? extends DockEvent> eventType, double x, double y, double screenX, double screenY, Node contents) {
        super(source, target, eventType);
        this.x = x;
        this.y = y;
        this.screenX = screenX;
        this.screenY = screenY;
        this.sceneX = x;
        this.sceneY = y;
        final Point3D p = InputEventUtils.recomputeCoordinates(new PickResult(target, x, y), null);
        this.x = p.getX();
        this.y = p.getY();
        this.z = p.getZ();
        this.contents = contents;
    }

    //region X、Y、Z
    /**
     * 事件相对于DockEvent节点原点的X坐标。
     */
    private transient double x;

    /**
     * 获取事件相对于DockEvent节点原点的X坐标。
     *
     * @return 事件相对于DockEvent节点原点的X坐标。
     */
    public final double getX() {
        return x;
    }

    /**
     * 事件相对于DockEvent节点原点的Y坐标
     */
    private transient double y;

    /**
     * 获取事件相对于DockEvent节点原点的Y坐标
     *
     * @return 事件相对于DockEvent节点原点的Y坐标
     */
    public final double getY() {
        return y;
    }

    /**
     * 事件相对于DockEvent节点原点的Z坐标
     */
    private transient double z;

    /**
     * 获取事件相对于DockEvent节点原点的Z坐标
     *
     * @return 事件相对于DockEvent节点原点的Z坐标
     */
    public final double getZ() {
        return z;
    }
    //endregion

    //region screenX、Y
    /**
     * 事件相对于屏幕左上角的X坐标。
     */
    private double screenX = 0;

    /**
     * 获取事件相对于屏幕左上角的X坐标
     *
     * @return 事件相对于屏幕左上角的X坐标
     */
    public final double getScreenX() {
        return screenX;
    }

    /**
     * 获取事件相对于屏幕左上角的Y坐标
     */
    private double screenY = 0;

    /**
     * 获取获取事件相对于屏幕左上角的Y坐标
     *
     * @return 获取事件相对于屏幕左上角的Y坐标
     */
    public final double getScreenY() {
        return screenY;
    }
    //endregion

    //region sceneX、Y
    /**
     * 事件相对于包含DockEvent节点的{@code Scene}的原点的X坐标。 如果该节点不在{@code Scene}中，则该值相对于DockEvent节点的最根级父节点的boundsInParent。
     */
    private double sceneX = 0;

    /**
     * 获取事件相对于包含DockEvent节点的{@code Scene}的原点的X坐标。 如果该节点不在{@code Scene}中，则该值相对于DockEvent节点的最根级父节点的boundsInParent。
     *
     * @return 事件相对于包含DockEvent节点的{@code Scene}的原点的X坐标
     */
    public final double getSceneX() {
        return sceneX;
    }

    /**
     * 事件相对于包含DockEvent节点的{@code Scene}的原点的Y坐标。 如果该节点不在{@code Scene}中，则该值相对于DockEvent节点的最根级父节点的boundsInParent。
     */
    private double sceneY = 0;

    /**
     * 获取事件相对于包含DockEvent节点的{@code Scene}的原点的Y坐标。 如果该节点不在{@code Scene}中，则该值相对于DockEvent节点的最根级父节点的boundsInParent。
     *
     * @return 事件相对于包含DockEvent节点的{@code Scene}的原点的Y坐标
     */
    public final double getSceneY() {
        return sceneY;
    }
    //endregion

    //region contents（Node）
    /**
     * 正在拖动的节点
     */
    private Node contents;

    /**
     * 获取事件内容（正在拖动的节点）
     *
     * @return 正在拖动的节点
     */
    public final Node getContents() {
        return contents;
    }
    //endregion
    //endregion
}
