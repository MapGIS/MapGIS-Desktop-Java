package com.zondy.mapgis.edit.tool;

import com.google.common.util.concurrent.ListenableFuture;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.edit.CommandManager;
import com.zondy.mapgis.edit.base.*;
import com.zondy.mapgis.edit.event.ToolFinishedEvent;
import com.zondy.mapgis.edit.event.ToolFinishedListener;
import com.zondy.mapgis.edit.util.SketchVertex;
import com.zondy.mapgis.edit.util.Vertex;
import com.zondy.mapgis.edit.view.*;
import com.zondy.mapgis.geometry.*;
import com.zondy.mapgis.view.*;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CR
 * @file SketckTool.java
 * @brief 编辑工具基类
 * @create 2020-05-18.
 */
public abstract class SketchTool {
    protected static final double IDENTIFY_TOLERANCE = 1.0D;//identify标识（选择）数据时的容差
    protected static final int GRAPHIC_FILL_ZORDER = 0;//填充图形的Z序
    protected static final int GRAPHIC_LINE_ZORDER = 100;//线的Z序
    protected static final int GRAPHIC_MOVEDVERTEX_ZORDER = 250;//移动中的结点的Z序
    protected static final int GRAPHIC_VERTEX_ZORDER = 300;//顶点的Z序
    protected static final int GRAPHIC_TEXT_ZORDER = 400;//文本的Z序
    protected static final int GRAPHIC_MOVEDPOLY_ZORDER = 500;//移动着的线（虚线）的Z序

    protected final MapControl mapControl;//地图控件
    protected final GraphicsOverlay sketchOverlay;//草图绘制层
    protected final SketchEditor sketchEditor;//草图编辑器
    protected GeometryBuilder geometryBuilder;//几何构造器
    protected int selectedPartIndex = -1;//选中图形的Part索引
    protected boolean isDisablePan = false;//移动状态标识
    protected Vertex selectedVertex = null;//选中的结点
    protected final List<Graphic> partOutlineGraphics;//MultiPart图形每个Part的线或边线构成的集合
    protected final List<Graphic> lastSegmentGraphics;//输入区时每个Part最后一个点和第一个点之间的闭合线构成的集合
    protected final List<Graphic> vertices = new ArrayList();//顶点图形集合
    protected final ContextMenu toolContextMenu = new ContextMenu();

    public SketchTool(MapControl mapControl) {
        if (mapControl == null) {
            throw new IllegalStateException("地图视图控件为空。");
        } else {
            this.mapControl = mapControl;
            this.sketchEditor = this.mapControl.getSketchEditor();
            if (sketchEditor == null) {
                throw new IllegalStateException("地图视图控件上未设置SketchEditor。");
            }

            //未完成，需要添加mapControl或map的参照系更改事件。
            //this.mapControl.addSpatialReferenceChangedListener(event -> SketchTool.this.initGeometryBuilder());
            this.sketchOverlay = mapControl.getSketchGraphicsOverlay();
            //this.sketchOverlay.setSelectionColor(this.sketchEditor.getSketchStyle().getSelectionColor());
            this.selectedPartIndex = -1;
            this.partOutlineGraphics = new ArrayList();
            this.lastSegmentGraphics = new ArrayList();
            this.initGeometryBuilder();
        }
    }

    /**
     * 获取几何类型
     *
     * @return
     */
    public abstract GeometryType getGeometryType();

    /**
     * 左键单击
     *
     * @param x
     * @param y
     * @return
     */
    public abstract List<MenuItem> onPointerClicked(double x, double y);

    /**
     * 右键单击
     *
     * @param x
     * @param y
     * @return
     */
    public abstract List<MenuItem> onSecondaryPointerClicked(double x, double y);

    /**
     * 移动
     *
     * @param x
     * @param y
     * @return
     */
    public abstract boolean prepareMove(double x, double y);

    /**
     * 左键按下
     *
     * @param x
     * @param y
     * @return
     */
    public abstract boolean onSinglePointerDown(double x, double y);

    /**
     * 左键移动
     * _
     *
     * @param x
     * @param y
     * @return
     */
    public abstract boolean onSinglePointerMove(double x, double y);

    /**
     * 左键弹起
     *
     * @param x
     * @param y
     * @return
     */
    public abstract boolean onSinglePointerUp(double x, double y);

    /**
     * 橡皮条移动
     *
     * @param x
     * @param y
     * @return
     */
    public boolean onRubberBandMove(double x, double y) {
        return false;
    }

    public boolean onRubberBandExited() {
        return false;
    }

    /**
     * 获取当前的草图几何
     *
     * @return
     */
    public Geometry getGeometry() {
        return this.geometryBuilder.toGeometry();
    }

    /**
     * 清空当前的草图几何
     */
    public void clearGeometry() {
        this.sketchEditor.addCommand(new SketchTool.RemoveGeometryCommand(this, this.geometryBuilder.toGeometry()));
    }

    /**
     * 草图是否有效
     *
     * @return
     */
    public boolean isSketchValid() {
        return this.geometryBuilder.isSketchValid();
    }

    /**
     * 用新的几何替换掉现有几何
     *
     * @param geometry
     */
    public void replaceGeometry(Geometry geometry) {
        this.sketchEditor.addCommand(new SketchTool.ReplaceGeometryCommand(this, this.geometryBuilder.toGeometry(), geometry));
    }

    /**
     * 替换GeometryBuilder里面的几何（参照系不一致时先投影）
     *
     * @param geometry
     */
    public void replaceGeometryInternal(Geometry geometry) {
        //if (!geometry.getSpatialReference().equals(this.mapControl.getSpatialReference())) {
        //    geometry = GeometryOperator.project(geometry, geometry.getSpatialReference(), this.mapControl.getSpatialReference());
        //}

        this.geometryBuilder.replaceGeometry(geometry);
    }

    /**
     * 获取禁止移动的标记
     *
     * @return
     */
    public boolean isDisablePan() {
        return this.isDisablePan;
    }

    /**
     * 清空当前几何
     */
    protected void clear() {
        if (this.geometryBuilder != null) {
            switch (this.getGeometryType()) {
                case GeoPoint: {
                    PointBuilder pointBuilder = (PointBuilder) this.geometryBuilder;
                    pointBuilder.setXY(0.0, 0.0);
                    break;
                }
                case GeoMultiPoint: {
                    MultipointBuilder multipointBuilder = (MultipointBuilder) this.geometryBuilder;
                    multipointBuilder.getDot3DCollection().clear();
                    break;
                }
                case GeoVarLine:
                case GeoPolygon: {
                    MultipartBuilder multipartBuilder = (MultipartBuilder) this.geometryBuilder;
                    if (this.selectedPartIndex >= 0 && this.selectedPartIndex < multipartBuilder.getParts().size()) {
                        multipartBuilder.getParts().remove(this.selectedPartIndex);
                    } else {
                        multipartBuilder.getParts().clear();
                    }
                    break;
                }
                case GeoRect: {
                    RectBuilder rectBuilder = (RectBuilder) this.geometryBuilder;
                    rectBuilder.setXY(0.0, 0.0, 0.0, 0.0);
                    break;
                }
            }

            this.selectedPartIndex = -1;
            this.selectedVertex = null;
        }
    }

    /**
     * 刷新草图
     */
    public abstract void updateSketch();

    /**
     * 清空选中的节点和Part
     */
    protected void clearSelection() {
        this.selectVertex(null);
        this.selectedPartIndex = -1;
    }

    /**
     * 根据part的索引选中相应的Graphic
     *
     * @param partIndex
     */
    protected void selectGraphicByPartIndex(int partIndex) {
        this.clearSelection();
        if (this.geometryBuilder != null && this.geometryBuilder instanceof MultipartBuilder) {
            MultipartBuilder multipartBuilder = (MultipartBuilder) this.geometryBuilder;
            if (partIndex >= 0 && partIndex < multipartBuilder.getParts().size()) {
                this.selectedPartIndex = partIndex;
                Graphic graphic;
                if (partIndex < this.partOutlineGraphics.size()) {
                    graphic = this.partOutlineGraphics.get(partIndex);
                    graphic.setSelected(true);
                }

                if (this.lastSegmentGraphics.size() > 0 && this.getGeometryType() == GeometryType.GeoPolygon) {
                    graphic = this.lastSegmentGraphics.get(partIndex);
                    graphic.setSelected(true);
                }
            }
        }
    }

    /**
     * 移除选中的顶点
     *
     * @return
     */
    public boolean removeSelectedVertex() {
        boolean rtn = false;
        if (this.selectedVertex != null) {
            this.sketchEditor.addCommand(new SketchTool.RemoveVertexCommand(this, this.selectedVertex.getPartIndex(), this.selectedVertex.getPointIndex(), this.selectedVertex.getPoint()));
            rtn = true;
        }
        return rtn;
    }

    /**
     * 几何更改
     */
    public void fireGeometryChanged() {
        this.sketchEditor.fireSketchGeometryChanged();
    }

    /**
     * 选中结点
     *
     * @param newVertex
     */
    public void fireSelectedVertexChanged(Vertex newVertex) {
        if (newVertex == null) {
            if (this.selectedVertex != null) {
                this.selectedVertex = null;
                this.sketchEditor.fireSelectedVertexChanged((SketchVertex) null);
            }
        } else {
            boolean fireEvent = !newVertex.equals(this.selectedVertex);
            this.selectedVertex = newVertex;
            if (fireEvent) {
                this.sketchEditor.fireSelectedVertexChanged(newVertex.getSketchVertex());
            }
        }
    }

    /**
     * 获取选中的草图节点
     *
     * @return
     */
    public SketchVertex getSelectedVertex() {
        return this.selectedVertex != null ? this.selectedVertex.getSketchVertex() : null;
    }

    /**
     * 根据草图节点寻找到相应的Graphic
     *
     * @param sketchVertex
     * @return
     */
    public abstract Graphic findSketchVertexGraphic(SketchVertex sketchVertex);

    /**
     * 判断草图节点是否在当前的几何中
     *
     * @param sketchVertex
     * @return
     */
    public boolean isSketchVertexInGeometry(SketchVertex sketchVertex) {
        boolean isValid = false;
        if (this.geometryBuilder != null && sketchVertex != null) {
            switch (this.getGeometryType()) {
                case GeoPoint: {
                    if (sketchVertex.getPartIndex() == 0 && sketchVertex.getPointIndex() == 0) {
                        isValid = true;
                    }
                    break;
                }
                case GeoMultiPoint: {
                    MultipointBuilder multipointBuilder = (MultipointBuilder) this.geometryBuilder;
                    if (sketchVertex.getPartIndex() == 0 && sketchVertex.getPointIndex() >= 0 && sketchVertex.getPointIndex() < multipointBuilder.getDot3DCollection().size()) {
                        isValid = true;
                    }
                    break;
                }
                case GeoVarLine: {
                    isValid = this.checkVertexInMultipartGeometry(sketchVertex, true);
                    break;
                }
                case GeoPolygon: {
                    isValid = this.checkVertexInMultipartGeometry(sketchVertex, false);
                    break;
                }
                case GeoRect:
                    break;
            }
        }
        return isValid;
    }

    /**
     * 判断草图节点是否在当前的几何中
     *
     * @param sketchVertex
     * @param isPolyline
     * @return
     */
    private boolean checkVertexInMultipartGeometry(SketchVertex sketchVertex, boolean isPolyline) {
        boolean rtn = false;
        MultipartBuilder multipartBuilder = (MultipartBuilder) this.geometryBuilder;
        int partIndex = sketchVertex.getPartIndex();
        int pointIndex = sketchVertex.getPointIndex();
        if (partIndex >= 0 && partIndex < multipartBuilder.getParts().size()) {
            Part part = multipartBuilder.getParts().get(partIndex);
            if (pointIndex < part.getPointCount()) {
                rtn = true;
            }
        }
        return rtn;
    }

    /**
     * 根据屏幕点获取地图点
     *
     * @param x 屏幕点
     * @param y
     * @return
     */
    protected Dot3D getMapPoint(double x, double y) {
        Dot3D dot3D = null;
        Dot dot = this.mapControl.screenToLocation(x, y);
        if (dot != null) {
            dot3D = new Dot3D(dot.getX(), dot.getY(), 0.0);
        }
        return dot3D;
    }

    /**
     * 移除指定索引的点
     *
     * @param partIndex
     * @param pointIndex
     */
    protected void removePointInPart(int partIndex, int pointIndex) {
        if (this.geometryBuilder != null) {
            switch (this.getGeometryType()) {
                case GeoPoint: {
                    PointBuilder pointBuilder = (PointBuilder) this.geometryBuilder;
                    if (pointIndex == 0) {
                        pointBuilder.setXY(0.0, 0.0);
                    }
                    break;
                }
                case GeoMultiPoint: {
                    MultipointBuilder multipointBuilder = (MultipointBuilder) this.geometryBuilder;
                    if (pointIndex >= 0 && pointIndex < multipointBuilder.getDot3DCollection().size()) {
                        multipointBuilder.getDot3DCollection().remove(pointIndex);
                    }
                    break;
                }
                case GeoVarLine:
                case GeoPolygon: {
                    MultipartBuilder multipartBuilder = (MultipartBuilder) this.geometryBuilder;
                    if (partIndex >= 0 && partIndex < multipartBuilder.getParts().size()) {
                        Part part = multipartBuilder.getParts().get(partIndex);
                        if (pointIndex >= 0 && pointIndex < part.getPointCount()) {
                            part.removeDot3D(pointIndex);
                        }
                    }
                    break;
                }
                case GeoRect:
                    break;
            }
        }
    }

    /**
     * 用新的点替换指定的点
     *
     * @param partIndex
     * @param pointIndex
     * @param mapPoint
     */
    protected void replacePointInPart(int partIndex, int pointIndex, Dot3D mapPoint) {
        if (this.geometryBuilder != null) {
            switch (this.getGeometryType()) {
                case GeoPoint: {
                    PointBuilder pointBuilder = (PointBuilder) this.geometryBuilder;
                    pointBuilder.replaceGeometry(new GeoPoint(mapPoint));
                    break;
                }
                case GeoMultiPoint: {
                    MultipointBuilder multipointBuilder = (MultipointBuilder) this.geometryBuilder;
                    if (pointIndex >= 0 && pointIndex < multipointBuilder.getDot3DCollection().size()) {
                        multipointBuilder.getDot3DCollection().remove(pointIndex);
                        multipointBuilder.getDot3DCollection().add(pointIndex, mapPoint);
                    }
                    break;
                }
                case GeoVarLine:
                case GeoPolygon: {
                    MultipartBuilder multipartBuilder = (MultipartBuilder) this.geometryBuilder;
                    if (partIndex >= 0 && partIndex < multipartBuilder.getParts().size()) {
                        Part part = multipartBuilder.getParts().get(partIndex);
                        if (pointIndex >= 0 && pointIndex < part.getPointCount()) {
                            part.removeDot3D(pointIndex);
                            part.addDot3D(pointIndex, mapPoint);
                        }
                    }
                    break;
                }
                case GeoRect: {
                    break;
                }
            }
        }
    }

    /**
     * 在选中的点的后面添加点
     *
     * @param point
     * @return
     */
    public boolean insertVertexAfterSelectedVertex(Dot3D point) {
        boolean rtn = false;
        if (this.selectedVertex != null) {
            if (this.getGeometryType() == GeometryType.GeoPoint) {
                rtn = this.moveSelectedVertex(point);
            } else {
                this.sketchEditor.addCommand(new SketchTool.AddVertexCommand(this, this.selectedVertex.getPartIndex(), this.selectedVertex.getPointIndex() + 1, point));
                rtn = true;
            }
        } else if (this.isEmptyGeometry()) {
            this.sketchEditor.addCommand(new SketchTool.AddVertexCommand(this, 0, 0, point));
            rtn = true;
        }
        return rtn;
    }

    /**
     * 将选中的点移动到指定的位置
     *
     * @param toPoint
     * @return
     */
    public boolean moveSelectedVertex(Dot3D toPoint) {
        boolean rtn = false;
        if (this.selectedVertex != null) {
            this.sketchEditor.addCommand(new SketchTool.MoveVertexCommand(this, this.selectedVertex.getPartIndex(), this.selectedVertex.getPointIndex(), this.selectedVertex.getPoint(), this.selectedVertex.getPointIndex(), toPoint));
            rtn = true;
        }
        return rtn;
    }

    /**
     * 选中指定节点
     *
     * @param vertex
     * @return
     */
    public abstract boolean selectVertex(Vertex vertex);

    /**
     * 选中指定节点
     *
     * @param partIndex
     * @param pointIndex
     * @return
     */
    public boolean selectVertex(int partIndex, int pointIndex) {
        SketchVertex sketchVertex = new SketchVertex(partIndex, pointIndex);
        return this.isSketchVertexInGeometry(sketchVertex) ? this.selectVertex(new Vertex(sketchVertex, this.findSketchVertexGraphic(sketchVertex))) : false;
    }

    /**
     * 判断当前几何是否为空
     *
     * @return
     */
    protected boolean isEmptyGeometry() {
        return this.geometryBuilder == null ? false : this.geometryBuilder.isEmpty();
    }

    /**
     * 在指定位置插入点
     *
     * @param partIndex
     * @param pointIndex
     * @param mapPoint
     */
    protected void insertPointInPart(int partIndex, int pointIndex, Dot3D mapPoint) {
        if (this.geometryBuilder != null) {
            switch (this.getGeometryType()) {
                case GeoPoint: {
                    PointBuilder pointBuilder = (PointBuilder) this.geometryBuilder;
                    pointBuilder.replaceGeometry(new GeoPoint(mapPoint));
                    break;
                }
                case GeoMultiPoint: {
                    MultipointBuilder multipointBuilder = (MultipointBuilder) this.geometryBuilder;
                    if (pointIndex >= 0 && pointIndex < multipointBuilder.getDot3DCollection().size()) {
                        multipointBuilder.getDot3DCollection().add(pointIndex, mapPoint);
                    } else {
                        multipointBuilder.getDot3DCollection().add(mapPoint);
                    }
                    break;
                }
                case GeoVarLine:
                case GeoPolygon: {
                    MultipartBuilder multipartBuilder = (MultipartBuilder) this.geometryBuilder;
                    if (multipartBuilder.getParts().size() == 0) {
                        this.addPart();
                    }

                    if (partIndex >= 0 && partIndex < multipartBuilder.getParts().size()) {
                        Part part = multipartBuilder.getParts().get(partIndex);
                        if (pointIndex >= 0 && pointIndex < part.getPointCount()) {
                            part.addDot3D(pointIndex, mapPoint);
                        } else {
                            part.addDot3D(mapPoint);
                        }
                    }
                    break;
                }
                case GeoRect:
                    break;
            }
        }
    }

    /**
     * 为MultiPart添加Part，返回添加的part的索引
     *
     * @return
     */
    protected int addPart() {
        int rtn = -1;
        if (this.geometryBuilder instanceof MultipartBuilder) {
            MultipartBuilder multipartBuilder = (MultipartBuilder) this.geometryBuilder;
            multipartBuilder.addPart();
            rtn = multipartBuilder.getParts().size() - 1;
        }
        return rtn;
    }

    /**
     * 将指定的Part按照指定的偏移量移动
     *
     * @param partIndex
     * @param dx        X偏移量
     * @param dy        Y偏移量
     */
    protected void movePartAtIndex(int partIndex, double dx, double dy) {
        if (this.geometryBuilder instanceof MultipartBuilder) {
            MultipartBuilder multipartBuilder = (MultipartBuilder) this.geometryBuilder;
            MultipartBuilder tempBuilder = this.getNewMultipartBuilder();//临时Builder，只装当前part
            if (partIndex >= 0 && partIndex < multipartBuilder.getParts().size()) {
                Part part = multipartBuilder.getParts().get(partIndex);
                for (int i = 0; i < part.getPointCount(); i++) {
                    tempBuilder.addPoint(new Dot3D(part.getDot3D(i).getX() + dx, part.getDot3D(i).getY() + dy, 0.0));
                }

                multipartBuilder.getParts().set(partIndex, tempBuilder.getParts().get(0));
                this.updateSketch();
                this.fireGeometryChanged();
            }
        }
    }

    /**
     * 构造最后一段的图形（区最后一点和起点连接用于封闭的线
     *
     * @return
     */
    protected Graphic getLastSegmentGraphic() {
        Graphic lastSegmentGraphic = new Graphic();
        lastSegmentGraphic.setAtt(new DataPropertySet());
        //lastSegmentGraphic.setSymbol(this.sketchEditor.getSketchStyle().getLineSymbol());
        lastSegmentGraphic.setZIndex(SketchTool.GRAPHIC_LINE_ZORDER);
        return lastSegmentGraphic;
    }

    ///**
    // * 获取最后段移动中的图形符号
    // *
    // * @param geometryType
    // * @return
    // */
    //protected Symbol getFeedbackSymbolByGeometryType(GeometryType geometryType) {
    //    SketchStyle style = this.sketchEditor.getSketchStyle();
    //    if (geometryType == GeometryType.GeoVarLine) {
    //        return style.getFeedbackLineSymbol();
    //    } else if (geometryType == GeometryType.GeoPolygon) {
    //        return style.getFeedbackFillSymbol();
    //    } else {
    //        return geometryType != GeometryType.GeoPoint && geometryType != GeometryType.GeoMultiPoint ? null : style.getFeedbackVertexSymbol();
    //    }
    //}

    /**
     * 构造新的MultipartBuilder
     *
     * @return
     */
    protected MultipartBuilder getNewMultipartBuilder() {
        MultipartBuilder multipartBuilder = null;
        if (this.geometryBuilder != null) {
            if (this.getGeometryType() == GeometryType.GeoPolygon) {
                multipartBuilder = new PolygonBuilder(this.geometryBuilder.getSRefData());
            } else if (this.getGeometryType() == GeometryType.GeoVarLine) {
                multipartBuilder = new LineBuilder(this.geometryBuilder.getSRefData());
            }
        }
        return multipartBuilder;
    }

    /**
     * 获取几何中离指定点最近的点
     *
     * @param geometry
     * @param x
     * @param y
     * @return
     */
    protected Dot3D getNearestCoordinate(Geometry geometry, double x, double y) {
        Dot3D rtn = null;
        Dot dot = this.mapControl.screenToLocation(x, y);
        if (dot != null) {
            Dot dotDes = new Dot(0, 0);
            GeometryOperator.calculateDistanceDotToLine(dot, (GeoVarLine) geometry, dotDes, 0);
            rtn = new Dot3D(dotDes.getX(), dotDes.getY(), 0.0);
        }
        return rtn;
    }

    /**
     * 移除几何，清空选择
     */
    protected void removeGeometry() {
        this.clear();
        this.selectedVertex = null;
        this.selectedPartIndex = -1;
    }

    /**
     * 判断List里面是否有后面的Graphic，Graphic底层没有提供比较函数。
     *
     * @param list
     * @param graphic
     * @return
     */
    protected boolean containGraphic(List<Graphic> list, Graphic graphic) {
        boolean rtn = false;
        if (list != null && graphic != null) {
            for (int i = 0; i < list.size(); i++) {
                Graphic g = list.get(i);
                if (g.getKey().equals(graphic.getKey())) {
                    rtn = true;
                    break;
                }
            }
        }
        return rtn;
    }

    /**
     * 根据几何类型初始化Builder
     */
    private void initGeometryBuilder() {
        if (this.mapControl != null && this.mapControl.getSpatialReference() != null) {
            if (this.geometryBuilder == null || !this.mapControl.getSpatialReference().equals(this.geometryBuilder.getSRefData())) {
                GeometryType type = this.getGeometryType();
                switch (type) {
                    case GeoPoint:
                        this.geometryBuilder = new PointBuilder(this.mapControl.getSpatialReference());
                        break;
                    case GeoMultiPoint:
                        this.geometryBuilder = new MultipointBuilder(this.mapControl.getSpatialReference());
                        break;
                    case GeoVarLine:
                        this.geometryBuilder = new LineBuilder(this.mapControl.getSpatialReference());
                        break;
                    case GeoPolygon:
                        this.geometryBuilder = new PolygonBuilder(this.mapControl.getSpatialReference());
                        break;
                    case GeoRect:
                        this.geometryBuilder = new RectBuilder(this.mapControl.getSpatialReference());
                        break;
                    default:
                        throw new UnsupportedOperationException("The geometry type " + type + " is not supported yet!");
                }
            }
        }
    }

    /**
     * 是否允许节点交互
     *
     * @return
     */
    protected boolean isAllowVertexInteractionEdit() {
        return this.sketchEditor.getSketchEditConfiguration().getVertexEditMode() == SketchEditConfiguration.SketchVertexEditMode.INTERACTION_EDIT;
    }

    /**
     * 是否允许选中part
     *
     * @return
     */
    protected boolean isAllowPartSelection() {
        return this.sketchEditor.getSketchEditConfiguration().isAllowPartSelection();
    }

    /**
     * 是否允许右键菜单
     *
     * @return
     */
    protected boolean isContextMenuEnabled() {
        return this.sketchEditor.getSketchEditConfiguration().isContextMenuEnabled();
    }

    /**
     * 是否要在拖动前选中
     *
     * @return
     */
    protected boolean isRequireSelectionBeforeDrag() {
        return this.sketchEditor.getSketchEditConfiguration().isRequireSelectionBeforeDrag();
    }

    private List<ToolFinishedListener> listeners;

    /**
     * 添加事件
     *
     * @param listener
     */
    public void addToolFinishedListener(ToolFinishedListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(listener);
    }

    /**
     * 移除事件
     *
     * @param listener
     */
    public void removeToolFinishedListener(ToolFinishedListener listener) {
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    /**
     * 触发完成事件
     */
    protected void fireToolFinished(ToolFinishedEvent event) {
        if (listeners != null) {
            for (ToolFinishedListener listener : listeners) {
                listener.toolFinished(event);
            }
        }
    }

    /**
     * 替换几何
     */
    public static final class ReplaceGeometryCommand implements CommandManager.Command {
        private final SketchTool sketchTool;
        private final Geometry oldGeometry;
        private final Geometry newGeometry;

        public ReplaceGeometryCommand(SketchTool tool, Geometry oldGeometry, Geometry newGeometry) {
            this.sketchTool = tool;
            this.oldGeometry = oldGeometry;
            this.newGeometry = newGeometry;
        }

        @Override
        public void redo() {
            this.sketchTool.replaceGeometryInternal(this.newGeometry);
            this.sketchTool.updateSketch();
            this.sketchTool.fireGeometryChanged();
        }

        @Override
        public void undo() {
            this.sketchTool.replaceGeometryInternal(this.oldGeometry);
            this.sketchTool.updateSketch();
            this.sketchTool.fireGeometryChanged();
        }
    }

    /**
     * 移除几何
     */
    public static final class RemoveGeometryCommand implements CommandManager.Command {
        private final SketchTool sketchTool;
        private final Geometry geometry;

        public RemoveGeometryCommand(SketchTool tool, Geometry geometry) {
            this.sketchTool = tool;
            this.geometry = geometry;
        }

        @Override
        public void redo() {
            this.sketchTool.removeGeometry();
            this.sketchTool.updateSketch();
            this.sketchTool.fireGeometryChanged();
        }

        @Override
        public void undo() {
            this.sketchTool.replaceGeometryInternal(this.geometry);
            this.sketchTool.updateSketch();
            this.sketchTool.fireGeometryChanged();
        }
    }

    /**
     * 移动几何
     */
    public static final class MoveGeometryCommand implements CommandManager.Command {
        private final SketchTool sketchTool;
        private final Geometry fromGeometry;
        private final Geometry toGeometry;

        public MoveGeometryCommand(SketchTool tool, Geometry fromGeometry, Geometry toGeometry) {
            this.sketchTool = tool;
            this.fromGeometry = fromGeometry;
            this.toGeometry = toGeometry;
        }

        @Override
        public void redo() {
            this.sketchTool.replaceGeometryInternal(this.toGeometry);
            this.sketchTool.updateSketch();
            this.sketchTool.fireGeometryChanged();
        }

        @Override
        public void undo() {
            this.sketchTool.replaceGeometryInternal(this.fromGeometry);
            this.sketchTool.updateSketch();
            this.sketchTool.fireGeometryChanged();
        }
    }

    /**
     * 添加几何
     */
    public static final class AddGeometryCommand implements CommandManager.Command {
        private final SketchTool sketchTool;
        private final Geometry geometry;

        public AddGeometryCommand(SketchTool tool, Geometry geometry) {
            this.sketchTool = tool;
            this.geometry = geometry;
        }

        @Override
        public void redo() {
            this.sketchTool.replaceGeometryInternal(this.geometry);
            this.sketchTool.updateSketch();
            this.sketchTool.fireGeometryChanged();
        }

        @Override
        public void undo() {
            this.sketchTool.removeGeometry();
            this.sketchTool.updateSketch();
            this.sketchTool.fireGeometryChanged();
        }
    }

    /**
     * 移除顶点几何
     */
    public static final class RemoveVertexCommand implements CommandManager.Command {
        private final SketchTool sketchTool;
        private final int partIndex;
        private final int pointIndex;
        private final Dot3D point;

        public RemoveVertexCommand(SketchTool tool, int partIndex, int pointIndex, Dot3D point) {
            this.sketchTool = tool;
            this.partIndex = partIndex;
            this.pointIndex = pointIndex;
            this.point = point;
        }

        @Override
        public void redo() {
            this.sketchTool.removePointInPart(this.partIndex, this.pointIndex);
            this.sketchTool.updateSketch();
            int pointIndex = this.pointIndex - 1 < 0 ? 0 : this.pointIndex - 1;
            if (!this.sketchTool.isEmptyGeometry()) {
                this.sketchTool.selectVertex(this.partIndex, pointIndex);
            } else {
                this.sketchTool.selectVertex(null);
            }

            this.sketchTool.fireGeometryChanged();
        }

        @Override
        public void undo() {
            this.sketchTool.insertPointInPart(this.partIndex, this.pointIndex, this.point);
            this.sketchTool.updateSketch();
            this.sketchTool.selectVertex(this.partIndex, this.pointIndex);
            this.sketchTool.fireGeometryChanged();
        }
    }

    /**
     * 移动节点几何
     */
    public static final class MoveVertexCommand implements CommandManager.Command {
        private final SketchTool sketchTool;
        private final int partIndex;
        private final int fromPointIndex;
        private final Dot3D fromPoint;
        private final int toPointIndex;
        private final Dot3D toPoint;

        public MoveVertexCommand(SketchTool tool, int partIndex, int fromPointIndex, Dot3D fromPoint, int toPointIndex, Dot3D toPoint) {
            this.sketchTool = tool;
            this.partIndex = partIndex;
            this.fromPointIndex = fromPointIndex;
            this.fromPoint = fromPoint;
            this.toPointIndex = toPointIndex;
            this.toPoint = toPoint;
        }

        @Override
        public void redo() {
            this.sketchTool.replacePointInPart(this.partIndex, this.toPointIndex, this.toPoint);
            this.sketchTool.updateSketch();
            this.sketchTool.selectVertex(this.partIndex, this.toPointIndex);
            this.sketchTool.fireGeometryChanged();
        }

        @Override
        public void undo() {
            this.sketchTool.replacePointInPart(this.partIndex, this.fromPointIndex, this.fromPoint);
            this.sketchTool.updateSketch();
            this.sketchTool.selectVertex(this.partIndex, this.fromPointIndex);
            this.sketchTool.fireGeometryChanged();
        }
    }

    /**
     * 添加顶点几何
     */
    public static final class AddVertexCommand implements CommandManager.Command {
        private final SketchTool sketchTool;
        private final int partIndex;
        private final int pointIndex;
        private final Dot3D point;

        public AddVertexCommand(SketchTool tool, int partIndex, int pointIndex, Dot3D point) {
            this.sketchTool = tool;
            this.partIndex = partIndex;
            this.pointIndex = pointIndex;
            this.point = point;
        }

        @Override
        public void redo() {
            this.sketchTool.insertPointInPart(this.partIndex, this.pointIndex, this.point);
            this.sketchTool.updateSketch();
            this.sketchTool.selectVertex(this.partIndex, this.pointIndex);
            this.sketchTool.fireGeometryChanged();
        }

        @Override
        public void undo() {
            this.sketchTool.removePointInPart(this.partIndex, this.pointIndex);
            this.sketchTool.updateSketch();
            if (this.pointIndex - 1 < 0) {
                this.sketchTool.selectVertex(null);
            } else {
                this.sketchTool.selectVertex(this.partIndex, this.pointIndex - 1);
            }

            this.sketchTool.fireGeometryChanged();
        }
    }
}
