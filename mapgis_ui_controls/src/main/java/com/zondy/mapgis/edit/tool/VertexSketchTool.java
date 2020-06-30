package com.zondy.mapgis.edit.tool;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.zondy.mapgis.common.CoordinateTran;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.edit.util.SketchVertex;
import com.zondy.mapgis.edit.util.ThreadPools;
import com.zondy.mapgis.edit.util.Vertex;
import com.zondy.mapgis.geometry.*;
import com.zondy.mapgis.map.MapLayer;
import com.zondy.mapgis.view.DataPropertySet;
import com.zondy.mapgis.view.GeometryElement;
import com.zondy.mapgis.view.Graphic;
import com.zondy.mapgis.view.GraphicList;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CR
 * @file VertexSketchTool.java
 * @brief 节点工具
 * @create 2020-05-18.
 */
public abstract class VertexSketchTool extends SketchTool {
    protected Graphic movingGeometryGraphic = null;//移动中的图形
    protected Graphic rubberBandGraphic = null;//绘制交互中的橡皮段
    protected Graphic movingVertexGraphic;//移动节点形成的橡皮线

    public VertexSketchTool(MapControl mapControl) {
        super(mapControl);
    }

    @Override
    public boolean onSinglePointerDown(double x, double y) {
        this.getRubberBandGraphic().setGeometry(null);
        return true;
    }

    @Override
    public boolean onRubberBandExited() {
        this.getRubberBandGraphic().setGeometry(null);
        return true;
    }

    @Override
    public boolean prepareMove(final double x, final double y) {
        this.isDisablePan = false;
        GraphicList graphics = this.mapControl.getSketchGraphicsOverlay().select(CoordinateTran.wpToMp(this.mapControl, x, y), SketchTool.IDENTIFY_TOLERANCE, 10);
        if (graphics != null) {
            for (int i = 0; i < graphics.size(); i++) {
                Graphic graphic = graphics.get(i);
                if (this.isPointGraphic(graphic) && this.canPointGraphicMove(graphic)) {
                    this.isDisablePan = true;
                    break;
                }

                if (this.canPartGraphicMove(graphic)) {
                    this.isDisablePan = true;
                    break;
                }
            }

            if (this.isDisablePan) {
                this.sketchOverlay.getGraphics().add(this.getMovingGeometryGraphic());
                this.handlePrepareMove(x, y);
            }
        }
        return true;
    }

    protected void clearGraphics() {
        this.mapControl.getSketchGraphicsOverlay().getGraphics().clear();
        this.rubberBandGraphic = null;
        this.movingGeometryGraphic = null;
        this.movingVertexGraphic = null;
        this.selectedVertex = null;
    }

    /**
     * 判断Part图形能否移动
     *
     * @param graphic
     * @return
     */
    private boolean canPartGraphicMove(Graphic graphic) {
        boolean isMovable = false;
        if (this.isAllowPartSelection()) {
            if (this.isRequireSelectionBeforeDrag()) {
                if (this.selectedPartIndex >= 0 && (this.partOutlineGraphics.get(this.selectedPartIndex) == graphic || this.lastSegmentGraphics.get(this.selectedPartIndex) == graphic)) {
                    isMovable = true;
                }
            } else if (this.containGraphic(this.partOutlineGraphics, graphic) || this.containGraphic(this.lastSegmentGraphics, graphic)) {
                Integer partIndex = Integer.valueOf(graphic.getAtt().getProperty(Vertex.PART_INDEX));
                if (partIndex != null) {
                    this.selectGraphicByPartIndex(partIndex);
                    isMovable = true;
                }
            }
        }
        return isMovable;
    }

    /**
     * 判断点Graphic能否移动（选中的结点，或者某顶点）
     *
     * @param graphic
     * @return
     */
    private boolean canPointGraphicMove(Graphic graphic) {
        boolean isMovable = false;
        if (this.isAllowVertexInteractionEdit()) {
            if (this.isRequireSelectionBeforeDrag()) {
                if (this.selectedVertex != null && this.selectedVertex.isSameGraphic(graphic)) {
                    isMovable = true;
                }
            } else if (this.containGraphic(this.vertices, graphic)) {
                this.selectVertex(new Vertex(graphic));
                isMovable = true;
            }
        }
        return isMovable;
    }

    /**
     * Graphic里面的几何是否为点
     *
     * @param graphic
     * @return
     */
    private boolean isPointGraphic(Graphic graphic) {
        boolean rtn = false;
        Geometry geometry = graphic.getGeometry();
        if (geometry instanceof GeoPoint) {
            rtn = true;
        } else if (geometry instanceof GeoMultiPoint) {
            rtn = ((GeoMultiPoint) geometry).getDotNum() == 1;
        }
        return rtn;
    }

    /**
     * 鼠标单击
     *
     * @param x
     * @param y
     * @return
     */
    @Override
    public List<MenuItem> onPointerClicked(double x, double y) {
        return this.handleSinglePointerClick(x, y, true);
    }

    /**
     * 鼠标右击
     *
     * @param x
     * @param y
     * @return
     */
    @Override
    public List<MenuItem> onSecondaryPointerClicked(double x, double y) {
        return this.handleSinglePointerClick(x, y, false);
    }

    /**
     * 鼠标移动
     *
     * @param x
     * @param y
     * @return
     */
    @Override
    public boolean onSinglePointerMove(double x, double y) {
        return this.isDisablePan ? this.handleOnSinglePointerMove(x, y) : true;
    }

    /**
     * 鼠标弹起
     *
     * @param x
     * @param y
     * @return
     */
    @Override
    public boolean onSinglePointerUp(double x, double y) {
        if (this.isDisablePan || this instanceof RectSketchTool) {
            this.handleOnSinglePointerUp(x, y);
            this.isDisablePan = false;
        }

        this.clearFeedbackGraphic();
        return true;
    }

    /**
     * 移除移动中的橡皮段图形
     */
    private void clearFeedbackGraphic() {
        Graphic movingGeometryGraphic = this.getMovingGeometryGraphic();
        movingGeometryGraphic.setGeometry(null);
        this.sketchOverlay.getGraphics().remove(movingGeometryGraphic);
        this.movingGeometryGraphic = null;
    }

    private List<MenuItem> handleSinglePointerClick(final double x, final double y, final boolean isAddVertex) {
        List<MenuItem> miList = null;
        GraphicList graphics = this.mapControl.getSketchGraphicsOverlay().select(CoordinateTran.wpToMp(this.mapControl, x, y), SketchTool.IDENTIFY_TOLERANCE, 10);
        if (graphics != null) {
            miList = new ArrayList<>();
            for (int i = 0; i < graphics.size(); i++) {
                Graphic graphic = graphics.get(i);
                if (this.containGraphic(this.vertices, graphic)) {
                    this.selectVertex(new Vertex(graphic));
                    if (this.isContextMenuEnabled()) {
                        MenuItem mi = new MenuItem("移除顶点");
                        mi.setOnAction(event -> this.removeSelectedVertex());
                        miList.add(mi);
                        break;
                    }
                } else {
                    //if (this.containGraphic(this.partOutlineGraphics, graphic) || this.containGraphic(this.lastSegmentGraphics, graphic)) {
                    //    Integer partIndex = Integer.valueOf(graphic.getAtt().getProperty(Vertex.PART_INDEX));
                    //    if (partIndex != null && this.isAllowPartSelection()) {
                    //        this.selectGraphicByPartIndex(partIndex);
                    //        if (this.isContextMenuEnabled()) {
                    //            calloutInfo = new SketchTool.CalloutInfo(SketchTool.CalloutContentType.CLEAR_PART, this.getNearestCoordinate(graphic.getGeometry(), x, y));
                    //        }
                    //    }
                    //}
                }
            }
        }
        if ((miList == null || miList.size() == 0) && isAddVertex) {
            VertexSketchTool.this.addVertexAt(x, y);
        }
        return miList;
    }

    /**
     * 添加顶点
     *
     * @param x
     * @param y
     */
    private void addVertexAt(double x, double y) {
        if (this.isAllowVertexInteractionEdit()) {
            if (this.isEmptyGeometry()) {
                this.sketchEditor.clearRedoUndo();
            }

            this.addVertex(x, y);
        }
    }

    protected abstract void addVertex(double x, double y);

    /**
     * 移动前事件
     *
     * @param x
     * @param y
     */
    protected abstract void handlePrepareMove(double x, double y);

    protected abstract boolean handleOnSinglePointerMove(double x, double y);

    protected abstract void handleOnSinglePointerUp(double x, double y);

    /**
     * 选中节点
     *
     * @param vertex
     * @return
     */
    @Override
    public boolean selectVertex(Vertex vertex) {
        this.sketchOverlay.clearSelection();
        this.updateSelectedVertexStyle(false);
        this.fireSelectedVertexChanged(vertex);
        this.updateSelectedVertexStyle(true);
        return true;
    }

    /**
     * 根据节点的选中状态刷新符号
     *
     * @param isSelected
     */
    private void updateSelectedVertexStyle(boolean isSelected) {
        if (this.selectedVertex != null) {
            this.selectedVertex.setSelected(isSelected);
            //this.selectedVertex.setSymbol(this.getVertexSymbol(isSelected));
        }
    }

    /**
     * 根据草图节点寻找到相应的Graphic
     *
     * @param sketchVertex
     * @return
     */
    @Override
    public Graphic findSketchVertexGraphic(SketchVertex sketchVertex) {
        Graphic graphic = null;
        Integer part;
        Integer point;
        if (sketchVertex.getPartIndex() == 0 && sketchVertex.getPointIndex() < this.vertices.size()) {
            graphic = this.vertices.get(sketchVertex.getPointIndex());
        } else {
            for (Graphic g : this.vertices) {
                part = Integer.valueOf(g.getAtt().getProperty(Vertex.PART_INDEX));
                point = Integer.valueOf(g.getAtt().getProperty(Vertex.POINT_INDEX));
                if (part != null && point != null && part == sketchVertex.getPartIndex() && point == sketchVertex.getPointIndex()) {
                    graphic = g;
                    break;
                }
            }
        }
        return graphic;
    }

    ///**
    // * 获取数字文本的符号
    // *
    // * @param textNumber
    // * @return
    // */
    //private TextSymbol getTextSymbolWithTextNumber(String textNumber) {
    //    TextSymbol styleTextSymbol = this.sketchEditor.getSketchStyle().getVertexTextSymbol();
    //    return new TextSymbol(styleTextSymbol.size(), textNumber, styleTextSymbol.getColor(), HorizontalAlignment.CENTER, VerticalAlignment.MIDDLE);
    //}

    ///**
    // * @param isSelected
    // * @return
    // */
    //private Symbol getVertexSymbol(boolean isSelected) {
    //    SketchStyle style = this.sketchEditor.getSketchStyle();
    //    Symbol symbol = isSelected ? style.getSelectedVertexSymbol() : style.getVertexSymbol();
    //    return symbol;
    //}

    /**
     * 添加顶点Graphic
     *
     * @param dot
     * @param partIndex
     * @param pointIndex
     * @return
     */
    protected Graphic addVertexGraphic(Dot3D dot, int partIndex, int pointIndex) {
        Graphic graphic = new Graphic();
        graphic.setGeometry(new GeoPoint(dot));
        graphic.setAtt(new DataPropertySet());
        //graphic.setSymbol(this.getVertexSymbol(false));
        graphic.setZIndex(SketchTool.GRAPHIC_VERTEX_ZORDER);
        graphic.getAtt().setProperty(Vertex.PART_INDEX, String.valueOf(partIndex));
        graphic.getAtt().setProperty(Vertex.POINT_INDEX, String.valueOf(pointIndex));
        this.sketchOverlay.getGraphics().add(graphic);
        return graphic;
    }

    /**
     * 获取移动中的图形
     *
     * @return
     */
    protected Graphic getMovingGeometryGraphic() {
        if (this.movingGeometryGraphic == null) {
            this.movingGeometryGraphic = new Graphic();
            this.movingGeometryGraphic.setZIndex(SketchTool.GRAPHIC_MOVEDVERTEX_ZORDER);
            //this.movingGeometryGraphic.setSymbol(this.getFeedbackSymbolByGeometryType(this.getGeometryType()));
        }
        return this.movingGeometryGraphic;
    }

    /**
     * 获取绘制中的橡皮段图形
     *
     * @return
     */
    Graphic getRubberBandGraphic() {
        if (this.rubberBandGraphic == null) {
            this.rubberBandGraphic = new Graphic();
        }

        return this.rubberBandGraphic;
    }
}
