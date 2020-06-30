package com.zondy.mapgis.docitemproperty;

import com.zondy.mapgis.base.LanguageConvert;
import com.zondy.mapgis.common.DrawSymbolItem;
import com.zondy.mapgis.controls.MapGISColorPicker;
import com.zondy.mapgis.controls.event.ColorChangedListener;
import com.zondy.mapgis.docitemproperty.controls.PropertyComboBox;
import com.zondy.mapgis.docitemproperty.editor.Dot3DEditorDialog;
import com.zondy.mapgis.docitemproperty.editor.RectBoundEitorDialog;
import com.zondy.mapgis.geodatabase.IVectorCls;
import com.zondy.mapgis.geodatabase.raster.RasterResampling;
import com.zondy.mapgis.geometry.Dot;
import com.zondy.mapgis.geometry.Dot3D;
import com.zondy.mapgis.geometry.Rect;
import com.zondy.mapgis.geometry.Rect3D;
import com.zondy.mapgis.info.GeomInfo;
import com.zondy.mapgis.info.LinInfo;
import com.zondy.mapgis.info.PntInfo;
import com.zondy.mapgis.info.RegInfo;
import com.zondy.mapgis.map.*;
import com.zondy.mapgis.scene.*;
import com.zondy.mapgis.sqlquery.SQLQueryDialog;
import com.zondy.mapgis.sref.SRefManagerDialog;
import com.zondy.mapgis.srs.SRefData;
import com.zondy.mapgis.symbolselect.SymbolSelectDialog;
import com.zondy.mapgis.systemlib.SymbolGeomType;
import com.zondy.mapgis.systemlib.SystemLibrary;
import com.zondy.mapgis.systemlib.SystemLibrarys;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.StringConverter;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.BeanPropertyUtils;
import org.controlsfx.property.editor.PropertyEditor;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

/**
 * 属性 基础类
 */
public class PropertyBaseClass {

    //region RectBound Rect3DBound

    /**
     * 二维范围(不可编辑)
     */
    public static class RectBound {
        //        private Rect range = null;
        private double xMin = 0;
        private double yMin = 0;
        private double xMax = 0;
        private double yMax = 0;

        @Override
        public String toString() {
//            return String.format("xMin:%.2f,yMin:%.2f,xMax:%.2f,yMax:%.2f", xMin, yMin, xMax, yMax);
            return String.format("%.2f,%.2f,%.2f,%.2f", xMin, yMin, xMax, yMax);
//            return "xMin:"+Double.toString(xMin) + ", " + Double.toString(yMin) + ", " + Double.toString(xMax) + ", " + Double.toString(yMax);
        }

        public RectBound() {
        }

        public RectBound(Rect r) {
            if (r != null) {
                this.xMin = r.getXMin();
                this.yMin = r.getYMin();
                this.xMax = r.getXMax();
                this.yMax = r.getYMax();
            }
        }

        public double getXMin() {
            return this.xMin;
        }

        public double getYMin() {
            return this.yMin;
        }

        public double getXMax() {
            return this.xMax;
        }

        public double getYMax() {
            return this.yMax;
        }

//        public Rect getRange() {
//            return new Rect(xMin, yMin, xMax, yMax);
//        }
    }

    /**
     * 二维范围(可编辑)
     */
    public static class RectBound1 {
        //        private Rect range = null;
        private double xMin = 0;
        private double yMin = 0;
        private double xMax = 0;
        private double yMax = 0;

        @Override
        public String toString() {
            return String.format("%.2f,%.2f,%.2f,%.2f", xMin, yMin, xMax, yMax);
        }

        public RectBound1() {
        }

        public RectBound1(Rect r) {
            if (r != null) {
                this.xMin = r.getXMin();
                this.yMin = r.getYMin();
                this.xMax = r.getXMax();
                this.yMax = r.getYMax();
            }
        }

        public void setXMin(double xMin) {
            this.xMin = xMin;
        }

        public double getXMin() {
            return this.xMin;
        }

        public void setYMin(double yMin) {
            this.yMin = yMin;
        }

        public double getYMin() {
            return this.yMin;
        }

        public void setXMax(double xMax) {
            this.xMax = xMax;
        }

        public double getXMax() {
            return this.xMax;
        }

        public void setYMax(double yMax) {
            this.yMax = yMax;
        }

        public double getYMax() {
            return this.yMax;
        }

//        public Rect getRange() {
//            return new Rect(xMin, yMin, xMax, yMax);
//        }
    }

    public static class Rect3DBound {
        //        private Rect3D range = null;
        private double xMin = 0;
        private double yMin = 0;
        private double zMin = 0;
        private double xMax = 0;
        private double yMax = 0;
        private double zMax = 0;

        @Override
        public String toString() {
//            return String.format("xMin:%f,yMin:%f,zMin:%f,xMax:%f,yMax:%f,zMax:%f,",
//                    xMin, yMin, zMin,
//                    xMax, yMax, zMax);
            return String.format("%.2f,%.2f,%.2f,%.2f,%.2f,%.2f",
                    xMin, yMin, zMin,
                    xMax, yMax, zMax);
        }

        public Rect3DBound() {
        }

        public Rect3DBound(Rect3D rt) {
            if (rt != null) {
                this.xMin = rt.getXMin();
                this.yMin = rt.getYMin();
                this.zMin = rt.getZMin();
                this.xMax = rt.getXMax();
                this.yMax = rt.getYMax();
                this.zMax = rt.getZMax();
            }
        }

        public double getXMin() {
            return this.xMin;
        }

        public double getYMin() {
            return this.yMin;
        }

        public double getZMin() {
            return this.zMin;
        }

        public double getXMax() {
            return this.xMax;
        }

        public double getYMax() {
            return this.yMax;
        }

        public double getZMax() {
            return this.zMax;
        }

//        public Rect getRange() {
//            return this.range;
//        }
    }
    //endregion

    public static class ZhDot {
        private double x = 0.0;
        private double y = 0.0;

        public ZhDot() {

        }

        public ZhDot(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return String.format("x:%f,y:%f", x, y);
//            return "xMin:"+Double.toString(xMin) + ", " + Double.toString(yMin) + ", " + Double.toString(xMax) + ", " + Double.toString(yMax);
        }

        public Dot getDot() {
            return new Dot(x, y);
        }
    }

    /**
     * 显示比例
     */
    public static class ZhDot3D {
        private int x = 0;
        private int y = 0;
        private int z = 0;

        public ZhDot3D() {

        }

        public ZhDot3D(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        public ZhDot3D(double x, double y, double z) {
            this.x = (int)x;
            this.y = (int)y;
            this.z = (int)z;
        }

        @Override
        public String toString() {
            return String.format("%d,%d,%d", x, y, z);
        }
        public void setX(int x) {
            this.x = x;
        }
        public int getX() {
            return this.x;
        }
        public void setY(int y) {
            this.y = y;
        }
        public int getY() {
            return this.y;
        }
        public void setZ(int z) {
            this.z = z;
        }
        public int getZ() {
            return this.z;
        }
    }
    /**
     * 三维点(显示比例等)
     */
    public static class ZhDot3DEx implements IPropertyEx {
        private boolean isUpdate = false;//是否立即更新
        private boolean hasChanged = false;//类中的属性值是否被编辑过
        private PropertySheet propertySheet;
        private PropertyItem[] items;
        private Dot3D item;
        private double x = 0;
        private double y = 0;
        private double z = 0;

        @Override
        public boolean hasChanged() {
            return false;
        }

        @Override
        public void apply() {
            if (!isUpdate && hasChanged) {
                item.setX(x);
                item.setY(y);
                item.setZ(z);
            }
        }

        @Override
        public Object getItem() {
            return this.item;
        }

        @Override
        public void setItem(Object obj) {
            if (obj instanceof Dot3D) {
                this.item = (Dot3D)obj;
                this.x = this.item.getX();
                this.y = this.item.getY();
                this.z = this.item.getZ();
            }
            items = new PropertyItem[3];
            items[0] = new PropertyItem("x", "x", "常规", true, Integer.class, null);
            items[0].setValue(this.x);
            if (items[0].getObservableValue().isPresent()) {
                items[0].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Integer) {
                            x = (int) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            if (item != null){
                                item.setX(x);
                            }
                        }
                    }
                });
            }

            items[1] = new PropertyItem("y", "y", "常规", true, Integer.class, null);
            items[1].setValue(y);
            if (items[1].getObservableValue().isPresent()) {
                items[1].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Integer) {
                            y = (int) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            if (item != null){
                                item.setY(y);
                            }
                        }
                    }
                });
            }

            items[2] = new PropertyItem("z", "z", "常规", true, Integer.class, null);
            items[2].setValue(z);
            if (items[2].getObservableValue().isPresent()) {
                items[2].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Integer) {
                            z = (int) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            if (item != null){
                                item.setZ(z);
                            }
                        }
                    }
                });
            }
            for (PropertyItem propertyItem :
                    items) {
                propertyItem.setItem(item);
            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return items;
        }

        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
            }
            return propertySheet;
        }

        @Override
        public void setImmediatelyUpdate(boolean isUpdate) {
            this.isUpdate = isUpdate;
        }

        @Override
        public boolean isImmediatelyUpdate() {
            return false;
        }

        @Override
        public String toString() {
            return String.format("%d,%d,%d", x, y, z);
        }
    }

    /**
     * 数据范围
     */
    public static class RectBoundEx implements IPropertyEx {
        private boolean isUpdate = false;//是否立即更新
        private boolean hasChanged = false;//类中的属性值是否被编辑过
        private PropertySheet propertySheet;
        private PropertyItem[] items;
        private Object item;
        private double xMin = 0;
        private double yMin = 0;
        private double xMax = 0;
        private double yMax = 0;


        @Override
        public boolean hasChanged() {
            return false;
        }

        @Override
        public void apply() {
            if (!isUpdate && hasChanged) {
                if (item instanceof Rect) {
                    ((Rect) item).setXMin(this.xMin);
                    ((Rect) item).setYMin(this.yMin);
                    ((Rect) item).setXMax(this.xMax);
                    ((Rect) item).setYMax(this.yMax);
                }
            }
        }

        @Override
        public Object getItem() {
            return this.item;
        }

        @Override
        public void setItem(Object obj) {
            if (obj instanceof Rect) {
                Rect rt = (Rect) obj;
                this.xMin = rt.getXMin();
                this.yMin = rt.getYMin();
                this.xMax = rt.getXMax();
                this.yMax = rt.getYMax();
            }
            items = new PropertyItem[4];

            items[0] = new PropertyItem("xMin", "xMin", "常规", true, Double.class, null);
            items[0].setValue(this.xMin);
            if (items[0].getObservableValue().isPresent()) {
                items[0].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Double) {
                            xMin = (double) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            if (item instanceof Rect) {
                                ((Rect) item).setXMin(xMin);
                            }
                        }
                    }
                });
            }

            items[1] = new PropertyItem("ymin", "ymin", "常规", true, Double.class, null);
            items[1].setValue(yMin);
            if (items[1].getObservableValue().isPresent()) {
                items[1].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Double) {
                            yMin = (double) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            if (item instanceof Rect) {
                                ((Rect) item).setYMin(yMin);
                            }
                        }
                    }
                });
            }

            items[2] = new PropertyItem("xMax", "xMax", "常规", true, Double.class, null);
            items[2].setValue(xMax);
            if (items[2].getObservableValue().isPresent()) {
                items[2].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Double) {
                            xMax = (double) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            if (item instanceof Rect) {
                                ((Rect) item).setXMax(xMax);
                            }
                        }
                    }
                });
            }
            items[3] = new PropertyItem("yMax", "yMax", "常规", true, Double.class, null);
            items[3].setValue(yMin);
            if (items[3].getObservableValue().isPresent()) {
                items[3].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Double) {
                            yMax = (double) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            if (item instanceof Rect) {
                                ((Rect) item).setYMax(yMax);
                            }
                        }
                    }
                });
            }
            for (PropertyItem propertyItem :
                    items) {
                propertyItem.setItem(item);
            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return items;
        }

        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
            }
            return propertySheet;
        }

        @Override
        public void setImmediatelyUpdate(boolean isUpdate) {
            this.isUpdate = isUpdate;
        }

        @Override
        public boolean isImmediatelyUpdate() {
            return false;
        }

        @Override
        public String toString() {
            return String.format("%.2f,%.2f,%.2f,%.2f", xMin, yMin, xMax, yMax);
        }
    }

    /**
     * 尺寸
     */
    public static class SizeDouble implements IPropertyEx {
        private boolean isUpdate = false;//是否立即更新
        private boolean hasChanged = false;//类中的属性值是否被编辑过
        private PropertySheet propertySheet;
        private PropertyItem[] items;
        private Object item;
        private double width = 0;//宽度
        private double height = 0;//高度


        @Override
        public boolean hasChanged() {
            return false;
        }

        @Override
        public void apply() {
            if (!isUpdate && hasChanged) {
                if (item instanceof Dot) {
                    ((Dot) item).setX(this.width);
                    ((Dot) item).setY(this.height);
                }
            }
        }

        @Override
        public Object getItem() {
            return this.item;
        }

        @Override
        public void setItem(Object obj) {
            if (obj instanceof Dot) {
                this.item = (Dot) obj;
                this.width = ((Dot) item).getX();
                this.height = ((Dot) item).getY();
            }
            items = new PropertyItem[2];

            items[0] = new PropertyItem("宽度", "宽度", "常规", true, Double.class, null);
            items[0].setValue(this.width);
            if (items[0].getObservableValue().isPresent()) {
                items[0].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Double) {
                            width = (double) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            if (item instanceof Dot) {
                                ((Dot) item).setX(width);
                            }
                        }
                    }
                });
            }

            items[1] = new PropertyItem("高度", "高度", "常规", true, Double.class, null);
            items[1].setValue(height);
            if (items[1].getObservableValue().isPresent()) {
                items[1].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Double) {
                            height = (double) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            if (item instanceof Dot) {
                                ((Dot) item).setY(height);
                            }
                        }
                    }
                });
            }

            for (PropertyItem propertyItem :
                    items) {
                propertyItem.setItem(item);
            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return items;
        }

        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
            }
            return propertySheet;
        }

        @Override
        public void setImmediatelyUpdate(boolean isUpdate) {
            this.isUpdate = isUpdate;
        }

        @Override
        public boolean isImmediatelyUpdate() {
            return false;
        }

        @Override
        public String toString() {
            return String.format("%f,%f", width, height);
        }
    }

    /**
     * 点图形参数
     */
    public static class ZhPntInfo implements IPropertyEx {
        private boolean isUpdate = false;//是否立即更新
        private SimpleBooleanProperty hasChanged;//类中的属性值是否被编辑过
        private PropertySheet propertySheet;
        private PropertyItem[] items;

        private PntInfo item = null;//点图形参数
        private int sID = 1;//子图号
        private double h = 10;//高度
        private double w = 10;//宽度
        private double angle = 0;//角度
        private int oColor = 3;//子图颜色
        private int oColor1 = 4;//可变颜色1
        private int oColor2 = 5;//可变颜色2
        private double oPenW = 2;//笔宽
        private double oPenW1 = 2;//可变笔宽1
        private double oPenW2 = 2;//可变笔宽2
        private boolean ovprnt = false; //透明输出
        private boolean showSymID = false; //透明输出

        public ZhPntInfo() {
            hasChanged = new SimpleBooleanProperty(this, "hasChanged", false);
        }

        public final SimpleBooleanProperty getHasChangedProperty() {
            return this.hasChanged;
        }

        public void setShowSymID(boolean visible) {
            if (items != null && items.length > 0) {
                items[0].setVisible(visible);
            }
        }

        @Override
        public boolean hasChanged() {
            return hasChanged.get();
        }

        @Override
        public void apply() {
            if (!isUpdate && hasChanged.get()) {
                if (item != null) {
                    item.setSymID(this.sID);
                    item.setWidth(this.w);
                    item.setHeight(this.h);
                    item.setAngle(this.angle);
                    item.setOutPenW1(this.oPenW);
                    item.setOutPenW2(this.oPenW1);
                    item.setOutPenW3(this.oPenW2);
                    item.setOutClr1(this.oColor);
                    item.setOutClr2(this.oColor1);
                    item.setOutClr3(this.oColor2);
                    item.setOvprnt(this.ovprnt);
                }
                this.hasChanged.set(false);
            }
        }

        @Override
        public Object getItem() {
            return this.item;
        }

        @Override
        public void setItem(Object obj) {
            if (obj instanceof PntInfo) {
                this.item = (PntInfo) obj;
                this.sID = item.getSymID();
                this.w = item.getWidth();
                this.h = item.getHeight();
                this.angle = item.getAngle();
                this.oPenW = item.getOutPenW1();
                this.oPenW1 = item.getOutPenW2();
                this.oPenW2 = item.getOutPenW3();
                this.oColor = item.getOutClr1();
                this.oColor1 = item.getOutClr2();
                this.oColor2 = item.getOutClr3();
                this.ovprnt = item.getOvprnt();
                items = new PropertyItem[11];
                items[0] = new PropertyItem("子图号", "子图号", "点参数", true, Integer.class, SymbolSelectEditor.class);
                items[0].setValue(this.sID);
                if (items[0].getObservableValue().isPresent()) {
                    items[0].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Integer) {
                                sID = (int) newValue;
                                hasChanged.set(true);
                            }
                            if (isUpdate && hasChanged.get()) {
                                item.setSymID(sID);
                                hasChanged.set(false);
                            }
                        }
                    });
                }
                items[1] = new PropertyItem("高度", "高度", "点参数", true, Double.class, null);
                items[1].setValue(h);
                if (items[1].getObservableValue().isPresent()) {
                    items[1].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Double) {
                                h = (double) newValue;
                                hasChanged.set(true);
                            }
                            if (isUpdate && hasChanged.get()) {
                                item.setHeight(h);
                                hasChanged.set(false);
                            }
                        }
                    });
                }
                items[2] = new PropertyItem("宽度", "宽度", "点参数", true, Double.class, null);
                items[2].setValue(this.w);
                if (items[2].getObservableValue().isPresent()) {
                    items[2].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Double) {
                                w = (double) newValue;
                                hasChanged.set(true);
                            }
                            if (isUpdate && hasChanged.get()) {
                                item.setWidth(w);
                                hasChanged.set(false);
                            }
                        }
                    });
                }
                items[3] = new PropertyItem("角度", "角度", "点参数", true, Double.class, null);
                items[3].setValue(this.angle);
                if (items[3].getObservableValue().isPresent()) {
                    items[3].getObservableValue().get().addListener((ChangeListener<Object>) (observable, oldValue, newValue) ->
                    {
                        if (newValue instanceof Double) {
                            angle = (double) newValue;
                            hasChanged.set(true);
                        }
                        if (isUpdate && hasChanged.get()) {
                            item.setAngle(angle);
                            hasChanged.set(false);
                        }
                    });
                }
                items[4] = new PropertyItem("子图颜色", "子图颜色", "点参数", true, Integer.class, MapGISColorPickerEditor.class);
                items[4].setValue(oColor);
                if (items[4].getObservableValue().isPresent()) {
                    items[4].getObservableValue().get().addListener((ChangeListener<Object>) (observable, oldValue, newValue) ->
                    {
                        if (newValue instanceof Integer) {
                            oColor = (int) newValue;
                            hasChanged.set(true);
                        }
                        if (isUpdate && hasChanged.get()) {
                            item.setOutClr1(oColor);
                            hasChanged.set(false);
                        }
                    });
                }
                items[5] = new PropertyItem("可变颜色1", "可变颜色1", "点参数", true, Integer.class, MapGISColorPickerEditor.class);
                items[5].setValue(oColor1);
                if (items[5].getObservableValue().isPresent()) {
                    items[5].getObservableValue().get().addListener((ChangeListener<Object>) (observable, oldValue, newValue) ->
                    {
                        if (newValue instanceof Integer) {
                            oColor1 = (int) newValue;
                            hasChanged.set(true);
                        }
                        if (isUpdate && hasChanged.get()) {
                            item.setOutClr2(oColor1);
                            hasChanged.set(false);
                        }
                    });
                }
                items[6] = new PropertyItem("可变颜色2", "可变颜色2", "点参数", true, Integer.class, MapGISColorPickerEditor.class);
                items[6].setValue(oColor2);
                if (items[6].getObservableValue().isPresent()) {
                    items[6].getObservableValue().get().addListener((ChangeListener<Object>) (observable, oldValue, newValue) ->
                    {
                        if (newValue instanceof Integer) {
                            oColor2 = (int) newValue;
                            hasChanged.set(true);
                        }
                        if (isUpdate && hasChanged.get()) {
                            item.setOutClr3(oColor2);
                            hasChanged.set(false);
                        }
                    });
                }
                items[7] = new PropertyItem("笔宽", "笔宽", "点参数", true, Double.class, null);
                items[7].setValue(oPenW);
                if (items[7].getObservableValue().isPresent()) {
                    items[7].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Double) {
                                oPenW = (double) newValue;
                                hasChanged.set(true);
                            }
                            if (isUpdate && hasChanged.get()) {
                                item.setOutPenW1(oPenW);
                                hasChanged.set(false);
                            }
                        }
                    });
                }
                items[8] = new PropertyItem("可变笔宽1", "可变笔宽1", "点参数", true, Double.class, null);
                items[8].setValue(oPenW1);
                if (items[8].getObservableValue().isPresent()) {
                    items[8].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Double) {
                                oPenW1 = (double) newValue;
                                hasChanged.set(true);
                            }
                            if (isUpdate && hasChanged.get()) {
                                item.setOutPenW2(oPenW1);
                                hasChanged.set(false);
                            }
                        }
                    });
                }
                items[9] = new PropertyItem("可变笔宽2", "可变笔宽2", "点参数", true, Double.class, null);
                items[9].setValue(oPenW2);
                if (items[9].getObservableValue().isPresent()) {
                    items[9].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Double) {
                                oPenW2 = (double) newValue;
                                hasChanged.set(true);
                            }
                            if (isUpdate && hasChanged.get()) {
                                item.setOutPenW3(oPenW2);
                                hasChanged.set(false);
                            }
                        }
                    });
                }
                items[10] = new PropertyItem("透明输出", "透明输出", "点参数", true, Boolean.class, null);
                items[10].setValue(this.ovprnt);
                if (items[10].getObservableValue().isPresent()) {
                    items[10].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Boolean) {
                                ovprnt = (boolean) newValue;
                                hasChanged.set(true);
                            }
                            if (isUpdate && hasChanged.get()) {
                                item.setOvprnt(ovprnt);
                                hasChanged.set(false);
                            }
                        }
                    });
                }

                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setItem(item);
                }
            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return items;
        }

        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
            }
            return propertySheet;
        }

        @Override
        public void setImmediatelyUpdate(boolean isUpdate) {
            this.isUpdate = isUpdate;
        }

        @Override
        public boolean isImmediatelyUpdate() {
            return false;
        }

        @Override
        public String toString() {
            return String.format("子图号:%d,子图颜色:%d ...", sID, oColor);
        }
    }

    /**
     * 线图形参数
     */
    public static class ZhLinInfo implements IPropertyEx {
        private boolean isUpdate = false;//是否立即更新
        private SimpleBooleanProperty hasChanged; //类中的属性值是否被编辑过
        private PropertySheet propertySheet;
        private PropertyItem[] items;

        private LinInfo item = null;//点图形参数
        //                private LineStyle lStyle = LineStyle.General;//线模式
        int linStyID = 1;//线型号
        int linStyIDEx = 0;//线型号
        private int oColor = 3;//线颜色
        private int oColor1 = 4;//可变颜色1
        private int oColor2 = 5;//可变颜色2
        private double oPenW = 2;//线宽
        private double oPenW1 = 2;//可变线宽1
        private double oPenW2 = 2;//可变线宽2
        //                private LinHeadType hType = LinHeadType.Round;//线头
//        private LinJointType ljType = LinJointType.Round;//拐角
//        private LinAdjustType aType = LinAdjustType.NoAdjust;//线型调整方法
//        private LinStyleMakeType lsmType = LinStyleMakeType.Bypoint;//线型生成方法
        private double xScale = 0;//X系数
        private double yScale = 0;//Y系数
        private boolean ovprnt = false; //透明输出

        public ZhLinInfo() {
            hasChanged = new SimpleBooleanProperty(this, "hasChanged", false);
        }

        public void setShowSymID(boolean visible) {
            if (items != null && items.length > 0) {
                items[0].setVisible(visible);
            }
        }

        public final SimpleBooleanProperty getHasChangedProperty() {
            return this.hasChanged;
        }

        @Override
        public boolean hasChanged() {
            return hasChanged.get();
        }

        @Override
        public void apply() {
            if (!isUpdate && hasChanged.get()) {
                if (item != null) {
                    item.setLinStyID(linStyID);
                    item.setOutPenW1(this.oPenW);
                    item.setOutPenW2(this.oPenW1);
                    item.setOutPenW3(this.oPenW2);
                    item.setOutClr1(this.oColor);
                    item.setOutClr2(this.oColor1);
                    item.setOutClr3(this.oColor2);
                    item.setXScale(this.xScale);
                    item.setYScale(this.yScale);
                    hasChanged.set(false);
                }
            }
        }

        @Override
        public Object getItem() {
            return this.item;
        }

        @Override
        public void setItem(Object obj) {
            if (obj instanceof LinInfo) {
                this.item = (LinInfo) obj;
                this.oPenW = item.getOutPenW1();
                this.oPenW1 = item.getOutPenW2();
                this.oPenW2 = item.getOutPenW3();
                this.oColor = item.getOutClr1();
                this.oColor1 = item.getOutClr2();
                this.oColor2 = item.getOutClr3();
                this.xScale = item.getXScale();
                this.yScale = item.getYScale();
                items = new PropertyItem[9];
                items[0] = new PropertyItem("线型号", "线型号", "线参数", true, Integer.class, SymbolSelectEditor.class);
                items[0].setValue(this.linStyID);
                if (items[0].getObservableValue().isPresent()) {
                    items[0].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Integer) {
                                linStyID = (int) newValue;
                                hasChanged.set(true);
                            }
                            if (isUpdate && hasChanged.get()) {
                                item.setLinStyID(linStyID);
                                hasChanged.set(false);
                            }
                        }
                    });
                }
                items[1] = new PropertyItem("线颜色", "线颜色", "线参数", true, Integer.class, MapGISColorPickerEditor.class);
                items[1].setValue(oColor);
                if (items[1].getObservableValue().isPresent()) {
                    items[1].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Integer) {
                                oColor = (int) newValue;
                                hasChanged.set(true);
                            }
                            if (isUpdate && hasChanged.get()) {
                                item.setOutClr1(oColor);
                                hasChanged.set(false);
                            }
                        }
                    });
                }
                items[2] = new PropertyItem("可变颜色1", "可变颜色1", "线参数", true, Integer.class, MapGISColorPickerEditor.class);
                items[2].setValue(oColor1);
                if (items[2].getObservableValue().isPresent()) {
                    items[2].getObservableValue().get().addListener((ChangeListener<Object>) (observable, oldValue, newValue) ->
                    {
                        if (newValue instanceof Integer) {
                            oColor1 = (int) newValue;
                            hasChanged.set(true);
                        }
                        if (isUpdate && hasChanged.get()) {
                            item.setOutClr2(oColor1);
                            hasChanged.set(false);
                        }
                    });
                }
                items[3] = new PropertyItem("可变颜色2", "可变颜色2", "线参数", true, Integer.class, MapGISColorPickerEditor.class);
                items[3].setValue(oColor2);
                if (items[3].getObservableValue().isPresent()) {
                    items[3].getObservableValue().get().addListener((ChangeListener<Object>) (observable, oldValue, newValue) ->
                    {
                        if (newValue instanceof Integer) {
                            oColor1 = (int) newValue;
                            hasChanged.set(true);
                        }
                        if (isUpdate && hasChanged.get()) {
                            item.setOutClr3(oColor2);
                            hasChanged.set(false);
                        }
                    });
                }
                items[4] = new PropertyItem("线宽", "线宽", "线参数", true, Double.class, null);
                items[4].setValue(oPenW);
                if (items[4].getObservableValue().isPresent()) {
                    items[4].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Double) {
                                oPenW = (double) newValue;
                                hasChanged.set(true);
                            }
                            if (isUpdate && hasChanged.get()) {
                                item.setOutPenW1(oPenW);
                                hasChanged.set(false);
                            }
                        }
                    });
                }
                items[5] = new PropertyItem("可变线宽1", "可变线宽1", "线参数", true, Double.class, null);
                items[5].setValue(oPenW1);
                if (items[5].getObservableValue().isPresent()) {
                    items[5].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Double) {
                                oPenW1 = (double) newValue;
                                hasChanged.set(true);
                            }
                            if (isUpdate && hasChanged.get()) {
                                item.setOutPenW1(oPenW1);
                                hasChanged.set(false);
                            }
                        }
                    });
                }
                items[6] = new PropertyItem("可变线宽2", "可变线宽2", "线参数", true, Double.class, null);
                items[6].setValue(oPenW2);
                if (items[6].getObservableValue().isPresent()) {
                    items[6].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Double) {
                                oPenW2 = (double) newValue;
                                hasChanged.set(true);
                            }
                            if (isUpdate && hasChanged.get()) {
                                item.setOutPenW2(oPenW2);
                                hasChanged.set(false);
                            }
                        }
                    });
                }
                items[7] = new PropertyItem("X系数", "X系数", "线参数", true, Double.class, null);
                items[7].setValue(this.xScale);
                if (items[7].getObservableValue().isPresent()) {
                    items[7].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Double) {
                                oPenW2 = (double) newValue;
                                hasChanged.set(true);
                            }
                            if (isUpdate && hasChanged.get()) {
                                item.setXScale(xScale);
                                hasChanged.set(false);
                            }
                        }
                    });
                }
                items[8] = new PropertyItem("Y系数", "Y系数", "线参数", true, Double.class, null);
                items[8].setValue(this.yScale);
                if (items[8].getObservableValue().isPresent()) {
                    items[8].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Double) {
                                yScale = (double) newValue;
                                hasChanged.set(true);
                            }
                            if (isUpdate && hasChanged.get()) {
                                item.setXScale(yScale);
                                hasChanged.set(false);
                            }
                        }
                    });
                }
                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setItem(item);
                }
            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return items;
        }

        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
            }
            return propertySheet;
        }

        @Override
        public void setImmediatelyUpdate(boolean isUpdate) {
            this.isUpdate = isUpdate;
        }

        @Override
        public boolean isImmediatelyUpdate() {
            return false;
        }

        @Override
        public String toString() {
            return String.format("线型号:%d,线颜色:%d ...", linStyID, oColor);
        }
    }

    /**
     * 区图形参数
     */
    public static class ZhRegInfo implements IPropertyEx {
        private boolean isUpdate = false;//是否立即更新
        private SimpleBooleanProperty hasChanged;
        private PropertySheet propertySheet;
        private PropertyItem[] items;

        private RegInfo item = null;//区图形参数
        private int fMode = 0;//填充模式
        private int fColor = 10;//填充色
        private int patID = 1;//图案编号
        private double patHeight = 10;//图案高度
        private double patWidth = 10;//图案宽度

        private double angle = 0;//图案角度
        private int pColor = 4;//图案颜色
        private double oPenW = 2;//图案笔宽
//        private boolean ovprnt = 0;//透明度

        public ZhRegInfo() {
            hasChanged = new SimpleBooleanProperty(this, "hasChanged", false);//类中的属性值是否被编辑过
        }

        public void setShowSymID(boolean visible) {
            if (items != null && items.length > 2) {
                items[2].setVisible(visible);
            }
        }

        public final SimpleBooleanProperty getHasChangedProperty() {
            return this.hasChanged;
        }


        @Override
        public boolean hasChanged() {
            return hasChanged.get();
        }

        @Override
        public void apply() {
            if (!isUpdate && hasChanged.get()) {
                if (item != null) {
                    item.setFillMode((short) this.fMode);
                    item.setFillClr(this.fColor);
                    item.setPatID(this.patID);
                    item.setPatWidth(this.patWidth);
                    item.setPatHeight(this.patHeight);
                    item.setAngle(this.angle);
                    item.setPatClr(this.pColor);
                    item.setOutPenW(this.oPenW);
//                    item.setOvprnt(this.ovprnt) ;
                    hasChanged.set(false);
                }
            }
        }

        @Override
        public Object getItem() {
            return this.item;
        }

        @Override
        public void setItem(Object obj) {
            if (obj instanceof RegInfo) {
                this.item = (RegInfo) obj;
                this.fMode = item.getFillMode();
                this.fColor = item.getFillClr();
                this.patID = item.getPatID();
                this.patHeight = item.getPatHeight();
                this.patWidth = item.getPatWidth();
                this.angle = item.getAngle();
                this.pColor = item.getPatClr();
                this.oPenW = item.getOutPenW();
                items = new PropertyItem[9];
                //TODO 接口缺失
                items[0] = new PropertyItem("填充模式", "填充模式", "区参数", false, String.class, null);
//                items[0].setValue(this.fMode);
                items[0].setValue("常规填充");
                if (items[0].getObservableValue().isPresent()) {
                    items[0].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
//                            if(newValue instanceof Integer)
//                            {
//                                fMode = (int) newValue;
//                                hasChanged = true;
//                            }
//                            if (isUpdate && hasChanged) {
//                                item.setFillMode((short) fMode);
//                            }
                        }
                    });
                }

                items[1] = new PropertyItem("填充颜色", "填充颜色", "区参数", true, Integer.class, MapGISColorPickerEditor.class);
                items[1].setValue(fColor);
                if (items[1].getObservableValue().isPresent()) {
                    items[1].getObservableValue().get().addListener((ChangeListener<Object>) (observable, oldValue, newValue) ->
                    {
                        if (newValue instanceof Integer) {
                            fColor = (int) newValue;
                            hasChanged.set(true);
                        }
                        if (isUpdate && hasChanged.get()) {
                            item.setFillClr(fColor);
                            hasChanged.set(false);
                        }
                    });
                }
                items[2] = new PropertyItem("图案编号", "图案编号", "区参数", true, Integer.class, SymbolSelectEditor.class);
                items[2].setValue(this.patID);
                if (items[2].getObservableValue().isPresent()) {
                    items[2].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Integer) {
                                patID = (int) newValue;
                                hasChanged.set(true);
                            }
                            if (isUpdate && hasChanged.get()) {
                                item.setPatID(patID);
                                hasChanged.set(false);
                            }
                        }
                    });
                }
                items[3] = new PropertyItem("图案高度", "图案高度", "区参数", true, Double.class, null);
                items[3].setValue(patHeight);
                if (items[3].getObservableValue().isPresent()) {
                    items[3].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Double) {
                                patHeight = (double) newValue;
                                hasChanged.set(true);
                            }
                            if (isUpdate && hasChanged.get()) {
                                item.setPatHeight(patHeight);
                                hasChanged.set(false);
                            }
                        }
                    });
                }
                items[4] = new PropertyItem("图案宽度", "图案宽度", "区参数", true, Double.class, null);
                items[4].setValue(this.patWidth);
                if (items[4].getObservableValue().isPresent()) {
                    items[4].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Double) {
                                patWidth = (double) newValue;
                                hasChanged.set(true);
                            }
                            if (isUpdate && hasChanged.get()) {
                                item.setPatWidth(patWidth);
                                hasChanged.set(false);
                            }
                        }
                    });
                }
                items[5] = new PropertyItem("图案角度", "图案角度", "区参数", true, Double.class, null);
                items[5].setValue(this.angle);
                if (items[5].getObservableValue().isPresent()) {
                    items[5].getObservableValue().get().addListener((ChangeListener<Object>) (observable, oldValue, newValue) ->
                    {
                        if (newValue instanceof Double) {
                            angle = (double) newValue;
                            hasChanged.set(true);
                        }
                        if (isUpdate && hasChanged.get()) {
                            item.setAngle(angle);
                            hasChanged.set(false);
                        }
                    });
                }
                items[6] = new PropertyItem("图案颜色", "图案颜色", "区参数", true, Integer.class, MapGISColorPickerEditor.class);
                items[6].setValue(pColor);
                if (items[6].getObservableValue().isPresent()) {
                    items[6].getObservableValue().get().addListener((ChangeListener<Object>) (observable, oldValue, newValue) ->
                    {
                        if (newValue instanceof Integer) {
                            pColor = (int) newValue;
                            hasChanged.set(true);
                        }
                        if (isUpdate && hasChanged.get()) {
                            item.setPatClr(pColor);
                            hasChanged.set(false);
                        }
                    });
                }
                items[7] = new PropertyItem("图案笔宽", "图案笔宽", "区参数", true, Double.class, null);
                items[7].setValue(oPenW);
                if (items[7].getObservableValue().isPresent()) {
                    items[7].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Double) {
                                oPenW = (double) newValue;
                                hasChanged.set(true);
                            }
                            if (isUpdate && hasChanged.get()) {
                                item.setOutPenW(oPenW);
                                hasChanged.set(false);
                            }
                        }
                    });
                }
                //TODO 接口缺失
                items[8] = new PropertyItem("透明度", "透明度", "区参数", false, Integer.class, null);
                items[8].setValue(0);
                if (items[8].getObservableValue().isPresent()) {
                    items[8].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
//                            if (newValue instanceof Integer) {
//                                oPenW1 = (int) newValue;
//                                hasChanged = true;
//                            }
//                            if (isUpdate && hasChanged) {
//                                item.setOutPenW2(oPenW1);
//                            hasChanged.set(false);
//                            }
                        }
                    });
                }

                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setItem(item);
                }
            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return items;
        }

        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
            }
            return propertySheet;
        }

        @Override
        public void setImmediatelyUpdate(boolean isUpdate) {
            this.isUpdate = isUpdate;
        }

        @Override
        public boolean isImmediatelyUpdate() {
            return false;
        }

        @Override
        public String toString() {
            return String.format("图案编号:%d,填充颜色:%d ...", patID, fColor);
        }
    }

    //region 编辑器

    /**
     * 弹出框编辑器
     *
     * @param <T>
     */
    public static class PopupPropertyEditor<T> implements PropertyEditor<T> {

        private final Button btnEditor;
        private final PropertySheet.Item item;
        private final ObjectProperty<T> value = new SimpleObjectProperty<>();

        public PopupPropertyEditor(PropertySheet.Item item) {
            this.item = item;
            if (item.getValue() != null) {
                btnEditor = new Button(item.getValue().toString());
                value.set((T) item.getValue());
            } else {
                btnEditor = new Button("<empty>");
            }
            btnEditor.setAlignment(Pos.CENTER_LEFT);
            btnEditor.setOnAction((ActionEvent event) ->
            {
                displayPopupEditor();
            });
        }

        private void displayPopupEditor() {
            PopupPropertySheet<T> sheet = new PopupPropertySheet<>(item, this);
            sheet.setPrefWidth(400);
            sheet.setPrefHeight(350);
            Alert alert = new Alert(Alert.AlertType.NONE);
//        alert.setWidth(700);
            alert.setResizable(false);
            alert.getDialogPane().setContent(sheet);
            if (item.getValue() instanceof DocItemPropertyClasses.RectBound) {
                alert.setTitle("数据范围");
            } else if (sheet.getBean() instanceof DocItemPropertyClasses.RectBound) {
                alert.setTitle("数据范围");
            } else if (item.getValue() instanceof DocItemPropertyClasses.Rect3DBound) {
                alert.setTitle("数据范围");
            } else if (sheet.getBean() instanceof DocItemPropertyClasses.Rect3DBound) {
                alert.setTitle("数据范围");
            }else if (sheet.getBean() instanceof DocItemPropertyClasses.ZhDot3D) {
                alert.setTitle("显示比例");
            }

            ButtonType saveButton = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().addAll(ButtonType.CANCEL, saveButton);
            final Button btTest = (Button) alert.getDialogPane().lookupButton(saveButton);
            btTest.addEventFilter(ActionEvent.ACTION, event ->
            {
//                if (item.getValue() instanceof DocItemPropertyClasses.RectBound) {
//
//                } else if (sheet.getBean() instanceof DocItemPropertyClasses.RectBound) {
//                } else if (item.getValue() instanceof DocItemPropertyClasses.Rect3DBound) {
//                } else if (sheet.getBean() instanceof DocItemPropertyClasses.Rect3DBound) {
//                }
            });

            Optional<ButtonType> response = alert.showAndWait();
            if (response.isPresent() && saveButton.equals(response.get())) {
                item.setValue(null);
                item.setValue(sheet.getBean());
                btnEditor.setText(sheet.getBean().toString());
            }
        }

        @Override
        public Node getEditor() {
            return btnEditor;
        }

        @Override
        public T getValue() {
            return value.get();
        }

        @Override
        public void setValue(T t) {
            value.set(t);
            if (t != null) {
                btnEditor.setText(t.toString());
            }
        }

        private class PopupPropertySheet<T> extends BorderPane {

            private final PropertyEditor<T> owner;
            private final PropertySheet sheet;
            private final PropertySheet.Item item;
            private T bean;

            public PopupPropertySheet(PropertySheet.Item item, PropertyEditor<T> owner) {

                this.item = item;
                this.owner = owner;
                sheet = new PropertySheet();
                sheet.setSearchBoxVisible(false);
                sheet.setModeSwitcherVisible(false);
                sheet.setMode(PropertySheet.Mode.CATEGORY);
                setCenter(sheet);
                setMinHeight(350);
                initSheet();
            }

            public T getBean() {
                return bean;
            }

            private void initSheet() {
                if (item.getValue() == null) {

                    bean = null;
                    try {
                        bean = (T) item.getType().newInstance();
                    } catch (InstantiationException | IllegalAccessException ex) {
                        ex.printStackTrace();
                        return;
                    }
                    if (bean == null) {
                        return;
                    }
                } else {
                    bean = (T) item.getValue();
                }

                Service<?> service = new Service<ObservableList<PropertySheet.Item>>() {
                    @Override
                    protected Task<ObservableList<PropertySheet.Item>> createTask() {
                        return new Task<ObservableList<PropertySheet.Item>>() {
                            @Override
                            protected ObservableList<PropertySheet.Item> call() throws Exception {
                                return BeanPropertyUtils.getProperties(bean);
                            }
                        };
                    }
                };
                service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void handle(WorkerStateEvent e) {
//                        for (PropertySheet.Item i : (ObservableList<PropertySheet.Item>) e.getSource().getValue()) {
//                            if (i instanceof BeanProperty && ((BeanProperty) i).getPropertyDescriptor() instanceof CustomPropertyDescriptor) {
//                                BeanProperty bi = (BeanProperty) i;
//                                bi.setEditable(((CustomPropertyDescriptor) bi.getPropertyDescriptor()).isEditable());
//                            }
//                        }
                        sheet.getItems().setAll((ObservableList<PropertySheet.Item>) e.getSource().getValue());
                    }
                });
                service.start();
            }
        }
    }

    /**
     * 弹出框编辑器(弹出GeomInfo对象的PropertySheet页面)
     *
     * @param <T>
     */
    public static class PopupPropertyGeomInfoEditor<T> implements PropertyEditor<T> {

        private final Button btnEditor;
        private ImageView imageView;
        private final PropertySheet.Item item;
        private final ObjectProperty<T> value = new SimpleObjectProperty<>();
        private ArrayList<IPropertyEx> propertyExLst = new ArrayList<>();
        private ArrayList<PropertySheet> propertySheetLst = new ArrayList<>();
        private String name = "";
        private SystemLibrary systemLibrary;

        private Image setInfo(GeomInfo obj) {
            Image imageTmp = null;
            propertyExLst.clear();
            propertySheetLst.clear();
            if (obj instanceof PntInfo) {
                DocItemPropertyClasses.ZhPntInfo zhPntInfo = new DocItemPropertyClasses.ZhPntInfo();
                zhPntInfo.setItem(obj);
                propertyExLst.add(zhPntInfo);
                propertySheetLst.add(zhPntInfo.getPropertySheet());
                name = "子图参数";
            } else if (obj instanceof LinInfo) {
                DocItemPropertyClasses.ZhLinInfo zhLinInfo = new DocItemPropertyClasses.ZhLinInfo();
                zhLinInfo.setItem(obj);
                propertyExLst.add(zhLinInfo);
                propertySheetLst.add(zhLinInfo.getPropertySheet());
                name = "线图元参数";
            } else if (obj instanceof RegInfo) {
                DocItemPropertyClasses.ZhRegInfo zhRegInfo = new DocItemPropertyClasses.ZhRegInfo();
                zhRegInfo.setItem(obj);
                propertyExLst.add(zhRegInfo);
                propertySheetLst.add(zhRegInfo.getPropertySheet());
                name = "区图元参数";
            }
            if (systemLibrary != null) {
                boolean rtn = false;
                byte[] bytes = DrawSymbolItem.drawSymbol(obj, 16, 16, 9, systemLibrary.getSysLibGuid());
                if (bytes != null && bytes.length > 0) {
                    ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
                    imageTmp = new Image(stream);
                }
            }
            return imageTmp;
        }

        public PopupPropertyGeomInfoEditor(PropertySheet.Item item) {
            this.item = item;
            if (((PropertyItem) item).getDocumentItem() instanceof VectorLayer) {
                systemLibrary = ((VectorLayer) ((PropertyItem) item).getDocumentItem()).getSysLibrary();
            } else if (((PropertyItem) item).getDocumentItem() instanceof Vector3DLayer) {
                systemLibrary = ((Vector3DLayer) ((PropertyItem) item).getDocumentItem()).getSysLibrary();
            } else {
                systemLibrary = SystemLibrarys.getSystemLibrarys().getDefaultSystemLibrary();
            }
            if (item.getValue() != null) {
                if (item.getValue() instanceof GeomInfo) {
                    this.setInfo((GeomInfo) item.getValue());
                }
                btnEditor = new Button(propertyExLst.size() > 0 ? propertyExLst.get(0).toString() : "");
                value.set((T) item.getValue());
            } else {
                btnEditor = new Button("<empty>");
            }
            imageView = new ImageView();
            btnEditor.setAlignment(Pos.CENTER_LEFT);
            btnEditor.setOnAction((ActionEvent event) ->
                    displayPopupEditor());
        }

        private void displayPopupEditor() {
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setResizable(false);
            alert.setGraphic(null);
            VBox centerBox = new VBox();
            centerBox.setAlignment(Pos.TOP_LEFT);
            centerBox.setPrefWidth(460);
            centerBox.getChildren().addAll(propertySheetLst);
            alert.getDialogPane().setContent(centerBox);
            alert.setTitle(name);
            ButtonType saveButton = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().addAll(saveButton, new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE));
            final Button btn = (Button) alert.getDialogPane().lookupButton(saveButton);
            btn.addEventFilter(ActionEvent.ACTION, event ->
            {
                for (int i = 0; i < propertyExLst.size(); i++) {
                    propertyExLst.get(i).apply();
                }
            });
            Optional<ButtonType> response = alert.showAndWait();
            if (response.isPresent() && saveButton.equals(response.get())) {
                Image imageTmp = null;
                GeomInfo geomInfo = null;
                if (propertyExLst.size() > 0 && propertyExLst.get(0).getItem() instanceof GeomInfo) {
                    geomInfo = (GeomInfo) (propertyExLst.get(0).getItem());
                    item.setValue(geomInfo);
                    if (systemLibrary != null) {
                        boolean rtn = false;
                        byte[] bytes = DrawSymbolItem.drawSymbol(geomInfo, 16, 16, 9, systemLibrary.getSysLibGuid());
                        if (bytes != null && bytes.length > 0) {
                            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
                            imageTmp = new Image(stream);
                        }
                    }
                }
                imageView.setImage(imageTmp);
                btnEditor.setText(propertyExLst.size() > 0 ? propertyExLst.get(0).toString() : "");
                btnEditor.setGraphic(imageView);
            }
        }

        @Override
        public Node getEditor() {
            return btnEditor;
        }

        @Override
        public T getValue() {
            return value.get();
        }

        @Override
        public void setValue(T t) {
            value.set(t);
            if (t instanceof GeomInfo) {
                Image imageTmp = this.setInfo((GeomInfo) t);
                imageView.setImage(imageTmp);
                btnEditor.setText(propertyExLst.size() > 0 ? propertyExLst.get(0).toString() : "");
                btnEditor.setGraphic(imageView);
            }
        }

        private class PopupPropertySheet<T> extends BorderPane {

            private final PropertyEditor<T> owner;
            private final PropertySheet sheet;
            private final PropertySheet.Item item;
            private T bean;

            public PopupPropertySheet(PropertySheet.Item item, PropertyEditor<T> owner) {

                this.item = item;
                this.owner = owner;
                sheet = new PropertySheet();
                sheet.setSearchBoxVisible(false);
                sheet.setModeSwitcherVisible(false);
                sheet.setMode(PropertySheet.Mode.CATEGORY);
                setCenter(sheet);
                setMinHeight(350);
                initSheet();
            }

            public T getBean() {
                return bean;
            }

            private void initSheet() {
                if (item.getValue() == null) {

                    bean = null;
                    try {
                        bean = (T) item.getType().newInstance();
                    } catch (InstantiationException | IllegalAccessException ex) {
                        ex.printStackTrace();
                        return;
                    }
                    if (bean == null) {
                        return;
                    }
                } else {
                    bean = (T) item.getValue();
                }

                Service<?> service = new Service<ObservableList<PropertySheet.Item>>() {
                    @Override
                    protected Task<ObservableList<PropertySheet.Item>> createTask() {
                        return new Task<ObservableList<PropertySheet.Item>>() {
                            @Override
                            protected ObservableList<PropertySheet.Item> call() throws Exception {
                                return BeanPropertyUtils.getProperties(bean);
                            }
                        };
                    }
                };
                service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void handle(WorkerStateEvent e) {
//                        for (PropertySheet.Item i : (ObservableList<PropertySheet.Item>) e.getSource().getValue()) {
//                            if (i instanceof BeanProperty && ((BeanProperty) i).getPropertyDescriptor() instanceof CustomPropertyDescriptor) {
//                                BeanProperty bi = (BeanProperty) i;
//                                bi.setEditable(((CustomPropertyDescriptor) bi.getPropertyDescriptor()).isEditable());
//                            }
//                        }
                        sheet.getItems().setAll((ObservableList<PropertySheet.Item>) e.getSource().getValue());
                    }
                });
                service.start();
            }
        }
    }

    /**
     * 弹出框编辑器(弹出IPropertyEx对象的PropertySheet页面)
     *
     * @param <T>
     */
    public static class PopupPropertyExEditor<T> implements PropertyEditor<T> {

        private final Button btnEditor;
        private final PropertySheet.Item item;
        private final ObjectProperty<T> value = new SimpleObjectProperty<>();
        private ArrayList<IPropertyEx> propertyExLst = new ArrayList<>();
        private ArrayList<PropertySheet> propertySheetLst = new ArrayList<>();
        private String name = "";
        private SystemLibrary systemLibrary = null;

        public PopupPropertyExEditor(PropertySheet.Item item) {
            this.item = item;
            if (item.getValue() != null) {
                propertyExLst.clear();
                propertySheetLst.clear();
                if (item.getValue() instanceof SizeDouble) {
                    DocItemPropertyClasses.SizeDouble sizeDouble = new DocItemPropertyClasses.SizeDouble();
                    sizeDouble.setItem((Dot) item.getValue());
                    propertyExLst.add(sizeDouble);
                    propertySheetLst.add(sizeDouble.getPropertySheet());
                    name = "网格大小";
                }
                btnEditor = new Button(propertyExLst.get(0).toString());
                value.set((T) item.getValue());
            } else {
                btnEditor = new Button("<empty>");
            }
            btnEditor.setAlignment(Pos.CENTER_LEFT);
            btnEditor.setOnAction((ActionEvent event) ->
            {
                displayPopupEditor();
            });
        }

        private void displayPopupEditor() {
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setResizable(false);
            alert.setGraphic(null);
            VBox centerBox = new VBox();
            centerBox.setAlignment(Pos.TOP_LEFT);
            centerBox.setPrefWidth(460);
//            centerBox.setPrefHeight(430);
            centerBox.getChildren().addAll(propertySheetLst);
            alert.getDialogPane().setContent(centerBox);
            alert.setTitle(name);
            ButtonType saveButton = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().addAll(ButtonType.CANCEL, saveButton);
            final Button btTest = (Button) alert.getDialogPane().lookupButton(saveButton);
            btTest.addEventFilter(ActionEvent.ACTION, event ->
            {
                for (int i = 0; i < propertyExLst.size(); i++) {
                    propertyExLst.get(i).apply();
                }
            });

            Optional<ButtonType> response = alert.showAndWait();

            if (response.isPresent() && saveButton.equals(response.get())) {
                item.setValue(propertyExLst.get(0).getItem());
                btnEditor.setText(propertyExLst.get(0).toString());
            }
        }

        @Override
        public Node getEditor() {
            return btnEditor;
        }

        @Override
        public T getValue() {
            return value.get();
        }

        @Override
        public void setValue(T t) {
            value.set(t);
            if (t != null) {
                Image imageTmp = null;
                propertyExLst.clear();
                propertySheetLst.clear();
                if (t instanceof SizeDouble) {
                    DocItemPropertyClasses.SizeDouble sizeDouble = new DocItemPropertyClasses.SizeDouble();
                    sizeDouble.setItem((Dot) t);
                    propertyExLst.add(sizeDouble);
                    propertySheetLst.add(sizeDouble.getPropertySheet());
                    name = "网格大小";
                }
                btnEditor.setText(propertyExLst.get(0).toString());
            }
        }

        private class PopupPropertySheet<T> extends BorderPane {

            private final PropertyEditor<T> owner;
            private final PropertySheet sheet;
            private final PropertySheet.Item item;
            private T bean;

            public PopupPropertySheet(PropertySheet.Item item, PropertyEditor<T> owner) {

                this.item = item;
                this.owner = owner;
                sheet = new PropertySheet();
                sheet.setSearchBoxVisible(false);
                sheet.setModeSwitcherVisible(false);
                sheet.setMode(PropertySheet.Mode.CATEGORY);
                setCenter(sheet);
//            installButtons();
                setMinHeight(350);
                initSheet();
            }

            public T getBean() {
                return bean;
            }

            private void initSheet() {
                if (item.getValue() == null) {

                    bean = null;
                    try {
                        bean = (T) item.getType().newInstance();
                    } catch (InstantiationException | IllegalAccessException ex) {
                        ex.printStackTrace();
                        return;
                    }
                    if (bean == null) {
                        return;
                    }
                } else {
                    bean = (T) item.getValue();
                }

                Service<?> service = new Service<ObservableList<PropertySheet.Item>>() {
                    @Override
                    protected Task<ObservableList<PropertySheet.Item>> createTask() {
                        return new Task<ObservableList<PropertySheet.Item>>() {
                            @Override
                            protected ObservableList<PropertySheet.Item> call() throws Exception {
                                return BeanPropertyUtils.getProperties(bean);
                            }
                        };
                    }
                };
                service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void handle(WorkerStateEvent e) {
//                        for (PropertySheet.Item i : (ObservableList<PropertySheet.Item>) e.getSource().getValue()) {
//                            if (i instanceof BeanProperty && ((BeanProperty) i).getPropertyDescriptor() instanceof CustomPropertyDescriptor) {
//                                BeanProperty bi = (BeanProperty) i;
//                                bi.setEditable(((CustomPropertyDescriptor) bi.getPropertyDescriptor()).isEditable());
//                            }
//                        }
                        sheet.getItems().setAll((ObservableList<PropertySheet.Item>) e.getSource().getValue());
                    }
                });
                service.start();
            }
        }
    }

    /**
     * 下拉项编辑器(LayerState,SceneMode)
     *
     * @param <T>
     */
    public static class ComboBoxPropertyEditor<T> implements PropertyEditor<T> {

        private final ComboBox<T> editor;
        private final PropertySheet.Item item;
        private final ObjectProperty<T> value = new SimpleObjectProperty<>();

        public ComboBoxPropertyEditor(PropertySheet.Item item) {
            this.item = item;
            Object obj = item.getValue();
            if (obj != null) {
                value.set((T) item.getValue());
            }
            ObservableList<Object> items = FXCollections.observableArrayList();
            if (obj instanceof LayerState) {

                items.add(LayerState.UnVisible);
                items.add(LayerState.Visible);
                items.add(LayerState.Editable);
                items.add(LayerState.Active);
            } else if (obj instanceof SceneMode) {
                items.add(SceneMode.GLOBE);
                items.add(SceneMode.LOCAL);
            } else if (obj instanceof FilterMode) {
                items.add(FilterMode.None);
                items.add(FilterMode.AttFilter);
                items.add(FilterMode.RectFilter);
            } else if (obj instanceof RasterResampling) {
                items.addAll(RasterResampling.values());
//                items.add(RasterSampling.NearestNeighbor);
//                items.add(RasterSampling.BilinearInterpolatio);
//                items.add(RasterSampling.CubicConvolution);
            } else if (obj instanceof GrayscaleTransform) {
                items.addAll(GrayscaleTransform.values());
            } else if (obj instanceof MapServerAccessMode) {
//                nums = MapServerAccessMode.getEnums(MapServerAccessMode.class);
                items.add(MapServerAccessMode.CacheOnly);
                items.add(MapServerAccessMode.ServerAndCache);
                items.add(MapServerAccessMode.ServerOnly);
            } else if (obj instanceof CullingMode) {
                items.add(CullingMode.None);
                items.add(CullingMode.Clockwise);
                items.add(CullingMode.AntiClockwise);
            } else if (obj instanceof ModelRenderType) {
                items.add(ModelRenderType.RenderCommon);
                items.add(ModelRenderType.RenderGrid);
            }else if(obj instanceof RenderModeDef){
                items.add(RenderModeDef.RenderModeVector);
                items.add(RenderModeDef.RenderModeRaster);
            }
            if (items.size() > 0) {
                editor = new ComboBox(items);
            } else {
                editor = new ComboBox();
            }

            if (obj != null) {
                editor.setValue((T) obj);
            }

            editor.setConverter(new StringConverter<T>() {
                @Override
                public String toString(T object) {
                    String objStr = "";
                    if (object instanceof LayerState) {
                        objStr = LanguageConvert.layerStateConvert((LayerState) object);
                    } else if (object instanceof SceneMode) {
                        objStr = LanguageConvert.sceneModeConvert((SceneMode) object);
                    } else if (object instanceof FilterMode) {
                        switch ((FilterMode) object) {
                            case None:
                                objStr = "无";
                                break;
                            case AttFilter:
                                objStr = "属性过滤";
                                break;
                            case RectFilter:
                                objStr = "范围过滤";
                                break;
                            default:
                                break;
                        }
                    } else if (object instanceof CullingMode) {
                        switch ((CullingMode) object) {
                            case None:
                                objStr = "无";
                                break;
                            case Clockwise:
                                objStr = "顺时针";
                                break;
                            case AntiClockwise:
                                objStr = "逆时针";
                                break;
                            default:
                                break;
                        }
                    } else if (object instanceof RasterResampling) {
                        objStr = LanguageConvert.rasterSamplingConvert((RasterResampling) object);
                    } else if (object instanceof GrayscaleTransform) {
                        objStr = LanguageConvert.rasGrayConvertFormConvert((GrayscaleTransform) object);
                    } else if (object instanceof MapServerAccessMode) {
                        objStr = LanguageConvert.mapServerAccessModeConvert((MapServerAccessMode) object);
                    } else if (object instanceof ModelRenderType) {
                        if ((ModelRenderType) object == ModelRenderType.RenderCommon) {
                            objStr = "普通渲染";
                        } else if ((ModelRenderType) object == ModelRenderType.RenderGrid) {
                            objStr = "分块渲染";
                        }
                    }else if (object instanceof RenderModeDef) {
                        if ((RenderModeDef) object == RenderModeDef.RenderModeVector) {
                            objStr = "矢量绘制";
                        } else if ((RenderModeDef) object == RenderModeDef.RenderModeRaster) {
                            objStr = "栅格绘制";
                        }
                    }
                    return objStr;
                }

                @Override
                public T fromString(String string) {
                    return null;
                }
            });
            editor.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<T>() {
                @Override
                public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
                    value.set(newValue);
                    item.setValue(newValue);
                }
            });
        }

        @Override
        public Node getEditor() {
            return editor;
        }

        @Override
        public T getValue() {
            return value.get();
        }

        @Override
        public void setValue(T t) {
            value.set(t);
            if (t != null) {
                editor.setValue((T) t);
            }
        }
    }

    /**
     * 显示比例尺 编辑器
     *
     * @param <T>
     */
    public static class ScaleComboBoxPropertyEditor<T> implements PropertyEditor<T> {

        private final ComboBox<Double> editor;
        private final PropertySheet.Item item;
        private final ObjectProperty<T> value = new SimpleObjectProperty<>();

        public ScaleComboBoxPropertyEditor(PropertySheet.Item item) throws IOException {
            this.item = item;
            Object obj = item.getValue();
            if (obj instanceof Double) {
                value.set((T) item.getValue());
            }
            ObservableList<Double> items = FXCollections.observableArrayList();
            items.addAll(DocItemPropertyClasses.ScaleHandler.GetDefaultScales());
            editor = new ComboBox(items);
            if (obj instanceof Double) {
                editor.setValue((double) obj);
            }
            editor.setEditable(true);
            editor.setConverter(new StringConverter<Double>() {
                @Override
                public String toString(Double object) {
                    return DocItemPropertyClasses.ScaleHandler.getScaleString(object.doubleValue());
                }

                @Override
                public Double fromString(String string) {
                    return DocItemPropertyClasses.ScaleHandler.getScaleDouble(string);
                }
            });
            editor.valueProperty().addListener(new ChangeListener<Double>() {
                @Override
                public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
                    value.set((T) newValue);
                    item.setValue(newValue);
                }
            });
        }

        @Override
        public Node getEditor() {
            return editor;
        }

        @Override
        public T getValue() {
            return value.get();
        }

        @Override
        public void setValue(T t) {
            value.set(t);
            if (t != null) {
                editor.setValue((Double) t);
            }
        }
    }

    /**
     * 系统库 下拉项
     *
     * @param <T>
     */
    public static class SystemLibComboBoxEditor<T> implements PropertyEditor<T> {

        private final ComboBox<T> editor;
        private final PropertySheet.Item item;
        private final ObjectProperty<T> value = new SimpleObjectProperty<>();

        public SystemLibComboBoxEditor(PropertySheet.Item item) {
            this.item = item;
            Object obj = item.getValue();
            if (obj != null) {
                value.set((T) item.getValue());
            }
            ObservableList<T> items = FXCollections.observableArrayList();
            SystemLibrary systemLibrary = null;
            long count = SystemLibrarys.getSystemLibrarys().getCount();
            for (long i = 0; i < count; i++) {
                systemLibrary = SystemLibrarys.getSystemLibrarys().getSystemLibrary(i);
                if (systemLibrary != null) {
                    items.add((T) systemLibrary);
                }
            }
            if (items.size() > 0) {
                editor = new ComboBox(items);
            } else {
                editor = new ComboBox();
            }

            if (obj != null) {
                editor.setValue((T) obj);
            }

            editor.setConverter(new StringConverter<T>() {
                @Override
                public String toString(T object) {
                    String objStr = "";
                    if (object instanceof SystemLibrary) {
                        objStr = ((SystemLibrary) object).getName();
                    }
                    return (String) objStr;
                }

                @Override
                public T fromString(String string) {
                    return null;
                }
            });
            editor.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<T>() {
                @Override
                public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
                    value.set(newValue);
                    item.setValue(newValue);
                }
            });
        }

        @Override
        public Node getEditor() {
            return editor;
        }

        @Override
        public T getValue() {
            return value.get();
        }

        @Override
        public void setValue(T t) {
            value.set(t);
            if (t != null) {
                editor.setValue((T) t);
            }
        }
    }

    /**
     * 服务图层 图层 下拉项
     *
     * @param <T>
     */
    public static class LayersComboBoxEditor<T> implements PropertyEditor<T> {
        private final ComboBox<T> editor;
        private final PropertySheet.Item item;
        private final ObjectProperty<T> value = new SimpleObjectProperty<>();

        public LayersComboBoxEditor(PropertySheet.Item item) {
            this.item = item;
            Object objVal = item.getValue();
            if (objVal != null) {
                value.set((T) item.getValue());
            }
            String name = item.getName();
            MapServer server = null;
            DocumentItem docItem = ((PropertyItem) item).getDocumentItem();
            if (docItem instanceof ImageLayer) {
                server = ((ImageLayer) docItem).getMapServer();
            } else if (docItem instanceof ServerLayer) {
                server = ((ImageLayer) docItem).getMapServer();
            }
            ObservableList<String> items = FXCollections.observableArrayList();
            if (server != null) {
                if (name == "图层") {
                    Object obj = server.getProperty("Layers");
                    if (obj != null) {
                        String layersStr = obj instanceof String ? (String) obj : "";
                        String[] layers = layersStr.split(";");
                        for (int i = 0; i < layers.length; i++) {
                            items.add(layers[i]);
                        }
                    }
                } else if (name == "瓦片集") {
                    Object obj = server.getProperty("TileMatrixSets");
                    if (obj != null) {
                        String tileMatrixSetsStr = obj instanceof String ? (String) obj : "";
                        String[] tileMatrixSets = tileMatrixSetsStr.split(";");
                        for (int i = 0; i < tileMatrixSets.length; i++) {
                            items.add(tileMatrixSets[i]);
                        }
                    }
                }
            }

            if (items.size() > 0) {
                editor = new ComboBox(items);
            } else {
                editor = new ComboBox();
            }

            if (objVal != null) {
                editor.setValue((T) objVal);
            }

            editor.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<T>() {
                @Override
                public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
                    value.set(newValue);
                    item.setValue(newValue);
                }
            });
        }

        @Override
        public Node getEditor() {
            return editor;
        }

        @Override
        public T getValue() {
            return value.get();
        }

        @Override
        public void setValue(T t) {
            value.set(t);
            if (t != null) {
                editor.setValue((T) t);
            }
        }
    }

    /**
     * 服务图层 单位 下拉项
     *
     * @param <T>
     */
    public static class ServerUnitComboBoxEditor<T> implements PropertyEditor<T> {
        private final ComboBox<T> editor;
        private final PropertySheet.Item item;
        private final ObjectProperty<T> value = new SimpleObjectProperty<>();

        public ServerUnitComboBoxEditor(PropertySheet.Item item) {
            this.item = item;
            Object objVal = item.getValue();
            if (objVal != null) {
                value.set((T) item.getValue());
            }
            ObservableList<Double> items = FXCollections.observableArrayList();
            String name = item.getName();
            if (name == "毫米/像素") {
                items.addAll((double) -1, 25.4 / 96, 25.4000508 / 96, 0.28, 0.28 * 25.4000508 / 25.4);
            } else if (name == "毫米/数据单位") {
                items.addAll((double) -1, (double) 1000, 111319490.79327358, 111194872.221777);
            }
            if (items.size() > 0) {
                editor = new ComboBox(items);
            } else {
                editor = new ComboBox();
            }
            if (objVal != null) {
                editor.setValue((T) objVal);
            }
            editor.setEditable(true);
            editor.setConverter(new StringConverter<T>() {
                @Override
                public String toString(T object) {
                    String objStr = "";
                    if (object instanceof Double) {
                        double val = (double) (Object) object;
                        if (val == -1) {
                            objStr = "默认解析";
                        } else if (val == 1000) {
                            objStr = "1000";
                        } else if (val == 111319490.79327358) {
                            objStr = "111319490.79327358";
                        } else if (val == 111194872.221777) {
                            objStr = "111194872.221777";
                        } else if (val == 25.4 / 96) {
                            objStr = "221777";
                        } else if (val == 25.4000508 / 96) {
                            objStr = "25.4000508 / 96";
                        } else if (val == 0.28) {
                            objStr = "0.28";
                        } else if (val == 0.28 * 25.4000508 / 25.4) {
                            objStr = "0.28 * 25.4000508 / 25.4";
                        }
                    }
                    return objStr;
                }

                @Override
                public T fromString(String string) {
                    return null;
                }
            });
//            editor.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<T>() {
//                @Override
//                public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
//                    value.set(newValue);
//                    item.setValue(newValue);
//                }
//            });
            editor.valueProperty().addListener(new ChangeListener<T>() {
                @Override
                public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
                    value.set(newValue);
                    item.setValue(newValue);
                }
            });
        }

        @Override
        public Node getEditor() {
            return editor;
        }

        @Override
        public T getValue() {
            return value.get();
        }

        @Override
        public void setValue(T t) {
            value.set(t);
            if (t != null) {
                editor.setValue((T) t);
            }
        }
    }

    /**
     * 参照系选择 编辑器
     */
    public static class SRefDataPropertyEditor implements PropertyEditor<SRefData> {

        private final HBox editor;
        private TextField textField;
        private final PropertySheet.Item item;
        private final ObjectProperty<SRefData> value = new SimpleObjectProperty<>();

        public SRefDataPropertyEditor(PropertySheet.Item item) {
            this.item = item;
            editor = new HBox();
            Object obj = item.getValue();
            if (obj instanceof SRefData) {
                value.set((SRefData) obj);
                textField = new TextField(((SRefData) obj).getSRSName());
                HBox.setHgrow(textField, Priority.ALWAYS);
                Button button = new Button("...");
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {

                        SRefManagerDialog dlg = new SRefManagerDialog();
                        if (dlg.showAndWait().equals(Optional.of(ButtonType.OK))) {
                            SRefData sRefData = dlg.getSelectedSRef();
                            if (sRefData != null) {
                                value.set(sRefData);
//                                    item.setValue(sRefData);
                                textField.setText(sRefData.getSRSName());
                            }
                        }
                    }
                });
//                editor.getItems().addAll(textField,button);
                editor.getChildren().addAll(textField, button);
            }
        }

        @Override
        public Node getEditor() {
            return editor;
        }

        @Override
        public SRefData getValue() {
            return value.getValue();
        }

        @Override
        public void setValue(SRefData t) {
            value.setValue(t);
            if (t != null) {
                textField.setText(t.getSRSName());
            }
        }
    }

    /**
     * 颜色选择 编辑器
     *
     * @param <T>
     */
    public static class MapGISColorPickerEditor<T> implements PropertyEditor<T> {

        private final MapGISColorPicker editor;

        private final PropertySheet.Item item;
        private final ObjectProperty<T> value = new SimpleObjectProperty<>();
        private int intClr = 0;
        private SystemLibrary systemLibrary = null;

        public MapGISColorPickerEditor(PropertySheet.Item item) {
            this.item = item;
            if (((PropertyItem) item).getDocumentItem() instanceof VectorLayer) {
                systemLibrary = ((VectorLayer) ((PropertyItem) item).getDocumentItem()).getSysLibrary();
            } else if (((PropertyItem) item).getDocumentItem() instanceof Vector3DLayer) {
                systemLibrary = ((Vector3DLayer) ((PropertyItem) item).getDocumentItem()).getSysLibrary();
            } else {
                systemLibrary = SystemLibrarys.getSystemLibrarys().getDefaultSystemLibrary();
            }
            Object obj = item.getValue();
            if (obj instanceof Integer) {
                intClr = (int) obj;
                value.set((T) item.getValue());
            }
            editor = new MapGISColorPicker();
            editor.setSelectColorNumber(intClr);
            editor.setMinHeight(23);
            editor.addSelectColorChangedListener(new ColorChangedListener() {
                @Override
                public void colorChanged() {
                    intClr = editor.getSelectColorNumber();
                    item.setValue(intClr);
//                    value.setValue((T)((Object)intClr));
                }
            });
        }

        @Override
        public Node getEditor() {
            return editor;
        }

        @Override
        public T getValue() {
            return value.get();
        }

        @Override
        public void setValue(T t) {
            value.set(t);
            if (t != null) {
                Object obj = item.getValue();
                if (obj instanceof Integer) {
                    intClr = (int) obj;
                    editor.setSelectColorNumber(intClr);
                }
//                intClr = (int)value.get();
//                editor.setSelectColorNumber(intClr);
//                editor.setValue((T) t);
            }
        }
    }

    /**
     * 文件夹选择 编辑器
     *
     * @param <T>
     */
    public static class FileChooserEditor<T> implements PropertyEditor<T> {

        private final HBox editor;
        private TextField textField;
        private Button button;
        private final PropertySheet.Item item;
        private final ObjectProperty<T> value = new SimpleObjectProperty<>();

        public FileChooserEditor(PropertySheet.Item item) {
            this.item = item;
            editor = new HBox();
            button = new Button("...");
            textField = new TextField("");
            HBox.setHgrow(textField, Priority.ALWAYS);
            editor.getChildren().addAll(textField, button);
            Object obj = item.getValue();
            if (obj instanceof String) {
                value.set((T) item.getValue());
                textField.setText((String) obj);
            }

            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("打开文件");
//                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MDB文件(.mdb)", "*.mdb"));
                    File file = fileChooser.showOpenDialog(getCurrentWindow(button));
                    if (file != null) {
                        textField.setText(file.getPath());
                    }
                }
            });
        }

        @Override
        public Node getEditor() {
            return editor;
        }

        @Override
        public T getValue() {
            return value.get();
        }

        @Override
        public void setValue(T t) {
            value.set(t);
            if (t != null) {
                Object obj = item.getValue();
                if (obj instanceof String) {
                    this.textField.setText((String) obj);
                }
            }
        }
    }

    /**
     * 文件目录选择 编辑器
     *
     * @param <T>
     */
    public static class DirectoryChooserEditor<T> implements PropertyEditor<T> {

        private final HBox editor;
        private TextField textField;
        private Button button;
        private final PropertySheet.Item item;
        private final ObjectProperty<T> value = new SimpleObjectProperty<>();

        public DirectoryChooserEditor(PropertySheet.Item item) {
            this.item = item;
            editor = new HBox();
            button = new Button("...");
            textField = new TextField("");
            HBox.setHgrow(textField, Priority.ALWAYS);
            editor.getChildren().addAll(textField, button);
            Object obj = item.getValue();
            if (obj instanceof String) {
                value.set((T) item.getValue());
                textField.setText((String) obj);
            }

            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    DirectoryChooser directoryChooser = new DirectoryChooser();
                    directoryChooser.setTitle("选择目录");
                    File directory = directoryChooser.showDialog(getCurrentWindow(button));
                    if (directory != null) {
                        textField.setText(directory.getPath());
                    }
                }
            });
        }

        @Override
        public Node getEditor() {
            return editor;
        }

        @Override
        public T getValue() {
            return value.get();
        }

        @Override
        public void setValue(T t) {
            value.set(t);
            if (t != null) {
                Object obj = item.getValue();
                if (obj instanceof String) {
                    this.textField.setText((String) obj);
                }
            }
        }
    }

    /**
     * 服务图层 缓存 路径选择+清理 编辑器
     *
     * @param <T>
     */
    public static class CacheDealEditor<T> implements PropertyEditor<T> {

        private final HBox editor;
        private TextField textField;
        private Button buttonChoose;
        private Button buttonDel;
        private final PropertySheet.Item item;
        private final ObjectProperty<T> value = new SimpleObjectProperty<>();


        public CacheDealEditor(PropertySheet.Item item) {
            this.item = item;
            editor = new HBox();
            buttonChoose = new Button("...");
            buttonDel = new Button("清理缓存");
            buttonDel.setMinWidth(120);
            textField = new TextField("");
            HBox.setHgrow(textField, Priority.ALWAYS);
            editor.getChildren().addAll(textField, buttonChoose, buttonDel);
            Object obj = item.getValue();
            if (obj instanceof String) {
                value.set((T) item.getValue());
                textField.setText((String) obj);
            }

            buttonChoose.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    DirectoryChooser directoryChooser = new DirectoryChooser();
                    directoryChooser.setTitle("选择目录");
                    File directory = directoryChooser.showDialog(getCurrentWindow(buttonChoose));
                    if (directory != null) {
                        textField.setText(directory.getPath());
                    }
                }
            });
            buttonDel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    File file = new File(textField.getText());
                    if (file.exists()) {
                        file.delete();
                    }
                }
            });
        }

        @Override
        public Node getEditor() {
            return editor;
        }

        @Override
        public T getValue() {
            return value.get();
        }

        @Override
        public void setValue(T t) {
            value.set(t);
            if (t != null) {
                Object obj = item.getValue();
                if (obj instanceof String) {
                    this.textField.setText((String) obj);
                }
            }
        }
    }

    /**
     * 符号选择 编辑器
     *
     * @param <T>
     */
    public static class SymbolSelectEditor<T> implements PropertyEditor<T> {

        private final HBox editor;
        private TextField textField;
        private ImageView imageView;
        private final PropertySheet.Item item;
        private final ObjectProperty<T> value = new SimpleObjectProperty<>();
        private SymbolGeomType symbolGeomType = SymbolGeomType.UnknownGeom;
        private int patID = 0;
        private SystemLibrary systemLibrary = null;

        public SymbolSelectEditor(PropertySheet.Item item) {
            this.item = item;
            if (((PropertyItem) item).getDocumentItem() instanceof VectorLayer) {
                systemLibrary = ((VectorLayer) ((PropertyItem) item).getDocumentItem()).getSysLibrary();
            } else if (((PropertyItem) item).getDocumentItem() instanceof Vector3DLayer) {
                systemLibrary = ((Vector3DLayer) ((PropertyItem) item).getDocumentItem()).getSysLibrary();
            } else {
                systemLibrary = SystemLibrarys.getSystemLibrarys().getDefaultSystemLibrary();
            }
            editor = new HBox();
            Object obj = ((PropertyItem) item).getItem();//item.getValue();
            if (obj instanceof PntInfo) {
                value.set((T) obj);
                patID = ((PntInfo) obj).getSymID();
                symbolGeomType = SymbolGeomType.GeomPnt;
            } else if (obj instanceof LinInfo) {
                value.set((T) obj);
                patID = ((LinInfo) obj).getLinStyID();
                symbolGeomType = SymbolGeomType.GeomLin;
            } else if (obj instanceof RegInfo) {
                value.set((T) obj);
                patID = ((RegInfo) obj).getPatID();
                symbolGeomType = SymbolGeomType.GeomReg;
            }
            editor.setFillHeight(true);
            editor.setAlignment(Pos.CENTER_LEFT);
            Image imageTmp = null;
            if (obj instanceof GeomInfo)
//            if (patID > 0)
            {
                GeomInfo info = (GeomInfo) obj;
//                byte[] bytes = DrawSymbolItem.drawSymbol(info, 32, 32, 9, systemLibrary.getSysLibGuid());
                byte[] bytes = DrawSymbolItem.drawSymbol(symbolGeomType, patID, 0, 32, 32, 9, systemLibrary.getSysLibGuid());
                if (bytes != null && bytes.length > 0) {
                    ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
                    imageTmp = new Image(stream);
                }
            }
            imageView = new ImageView(imageTmp);
            textField = new TextField(Integer.toString(patID));
            Button button = new Button("...");
            editor.getChildren().addAll(imageView, textField, button);
            HBox.setHgrow(textField, Priority.ALWAYS);
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    SymbolSelectDialog symbolSelectDialog = new SymbolSelectDialog(systemLibrary, symbolGeomType, false, patID);
                    Optional<ButtonType> response = symbolSelectDialog.showAndWait();
                    if (response.isPresent() && response.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                        patID = symbolSelectDialog.getSelectNum();
                        if (obj instanceof PntInfo) {
                            PntInfo info = new PntInfo();
                            info.setSymID(patID);
                            value.setValue((T) info);
                            item.setValue(patID);
                        } else if (obj instanceof LinInfo) {
                            LinInfo info = new LinInfo();
                            info.setLinStyID(patID);
                            value.setValue((T) info);
                            item.setValue(patID);
                        } else if (obj instanceof RegInfo) {
                            RegInfo info = new RegInfo();
                            info.setPatID(patID);
                            value.setValue((T) info);
                            item.setValue(patID);
                        }
                        Image imageTmp = null;
                        if (patID > 0) {
                            byte[] bytes = DrawSymbolItem.drawSymbol(symbolGeomType, patID, 0, 32, 32, 9, systemLibrary.getSysLibGuid());
                            if (bytes != null && bytes.length > 0) {
                                ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
                                imageTmp = new Image(stream);
                            }
                        }
                        imageView.setImage(imageTmp);
                        textField.setText(Integer.toString(patID));
                    }
                }
            });
//                editor.getItems().addAll(textField,button);

        }

        @Override
        public Node getEditor() {
            return editor;
        }

        @Override
        public T getValue() {
            return value.getValue();
        }

        @Override
        public void setValue(T t) {
            value.setValue(t);
            if (t != null) {
                if (t instanceof PntInfo) {
                    patID = ((PntInfo) t).getSymID();
                    symbolGeomType = SymbolGeomType.GeomPnt;
                } else if (t instanceof LinInfo) {
                    patID = ((LinInfo) t).getLinStyID();
                    symbolGeomType = SymbolGeomType.GeomLin;
                } else if (t instanceof RegInfo) {
                    patID = ((RegInfo) t).getPatID();
                    symbolGeomType = SymbolGeomType.GeomReg;
                }
                Image imageTmp = null;
                if (patID > 0) {
                    byte[] bytes = DrawSymbolItem.drawSymbol(symbolGeomType, patID, 0, 32, 32, 9, systemLibrary.getSysLibGuid());
                    if (bytes != null && bytes.length > 0) {
                        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
                        imageTmp = new Image(stream);
                    }
                }
                imageView.setImage(imageTmp);
                textField.setText(Integer.toString(patID));
            }
        }
    }

    public static class SymbolSelectEditor2<T> implements PropertyEditor<T> {

        private final HBox editor;
        private TextField textField;
        private ImageView imageView;
        private final PropertySheet.Item item;
        private final ObjectProperty<T> value = new SimpleObjectProperty<>();
        private SymbolGeomType symbolGeomType = SymbolGeomType.UnknownGeom;
        private int patID = 0;
        private SystemLibrary systemLibrary = null;

        public SymbolSelectEditor2(PropertySheet.Item item) {
            this.item = item;
            if (((PropertyItem) item).getDocumentItem() instanceof VectorLayer) {
                systemLibrary = ((VectorLayer) ((PropertyItem) item).getDocumentItem()).getSysLibrary();
            } else if (((PropertyItem) item).getDocumentItem() instanceof Vector3DLayer) {
                systemLibrary = ((Vector3DLayer) ((PropertyItem) item).getDocumentItem()).getSysLibrary();
            }
            editor = new HBox();
            Object obj = item.getValue();
            if (obj instanceof PntInfo) {
                value.set((T) obj);
                patID = ((PntInfo) obj).getSymID();
                symbolGeomType = SymbolGeomType.GeomPnt;
            } else if (obj instanceof LinInfo) {
                value.set((T) obj);
                patID = ((LinInfo) obj).getLinStyID();
                symbolGeomType = SymbolGeomType.GeomLin;
            } else if (obj instanceof RegInfo) {
                value.set((T) obj);
                patID = ((RegInfo) obj).getPatID();
                symbolGeomType = SymbolGeomType.GeomReg;
            }
            editor.setFillHeight(true);
            editor.setAlignment(Pos.CENTER_LEFT);
            Image imageTmp = null;
            if (patID > 0) {
                byte[] bytes = DrawSymbolItem.drawSymbol(symbolGeomType, patID, 0, 32, 32, 9, systemLibrary.getSysLibGuid());
                if (bytes != null && bytes.length > 0) {
                    ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
                    imageTmp = new Image(stream);
                }
            }
            imageView = new ImageView(imageTmp);
            textField = new TextField(Integer.toString(patID));
            Button button = new Button("...");
            editor.getChildren().addAll(imageView, textField, button);
            HBox.setHgrow(textField, Priority.ALWAYS);
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    SymbolSelectDialog symbolSelect = new SymbolSelectDialog(systemLibrary, symbolGeomType, true, patID);
                    symbolSelect.show();
                    symbolSelect.setOnCloseRequest(new EventHandler<DialogEvent>() {
                        @Override
                        public void handle(DialogEvent event) {
                            patID = symbolSelect.getSelectNum();
                            if (obj instanceof PntInfo) {
                                PntInfo info = new PntInfo();
                                info.setSymID(patID);
                                value.setValue((T) info);
                                item.setValue(info);
                            } else if (obj instanceof LinInfo) {
                                LinInfo info = new LinInfo();
                                info.setLinStyID(patID);
                                value.setValue((T) info);
                                item.setValue(info);
                            } else if (obj instanceof RegInfo) {
                                RegInfo info = new RegInfo();
                                info.setPatID(patID);
                                value.setValue((T) info);
                                item.setValue(info);
                            }
                            Image imageTmp = null;
                            if (patID > 0) {
                                byte[] bytes = DrawSymbolItem.drawSymbol(symbolGeomType, patID, 0, 32, 32, 9, systemLibrary.getSysLibGuid());
                                if (bytes != null && bytes.length > 0) {
                                    ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
                                    imageTmp = new Image(stream);
                                }
                            }
                            imageView.setImage(imageTmp);
                            textField.setText(Integer.toString(patID));
                        }
                    });
                }
            });
//                editor.getItems().addAll(textField,button);

        }

        @Override
        public Node getEditor() {
            return editor;
        }

        @Override
        public T getValue() {
            return value.getValue();
        }

        @Override
        public void setValue(T t) {
            value.setValue(t);
            if (t != null) {
                if (t instanceof PntInfo) {
                    patID = ((PntInfo) t).getSymID();
                    symbolGeomType = SymbolGeomType.GeomPnt;
                } else if (t instanceof LinInfo) {
                    patID = ((LinInfo) t).getLinStyID();
                    symbolGeomType = SymbolGeomType.GeomLin;
                } else if (t instanceof RegInfo) {
                    patID = ((RegInfo) t).getPatID();
                    symbolGeomType = SymbolGeomType.GeomReg;
                }
                Image imageTmp = null;
                if (patID > 0) {
                    byte[] bytes = DrawSymbolItem.drawSymbol(symbolGeomType, patID, 0, 32, 32, 9, systemLibrary.getSysLibGuid());
                    if (bytes != null && bytes.length > 0) {
                        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
                        imageTmp = new Image(stream);
                    }
                }
                imageView.setImage(imageTmp);
                textField.setText(Integer.toString(patID));
            }
        }
    }

    /**
     * SQL查询条件输入 编辑器
     *
     * @param <T>
     */
    public static class SQLQueryEditor<T> implements PropertyEditor<T> {

        private final HBox editor;
        private TextField textField;
        private Button button;
        private final PropertySheet.Item item;
        private final ObjectProperty<T> value = new SimpleObjectProperty<>();
        private IVectorCls vectorCls;

        public SQLQueryEditor(PropertySheet.Item item) {
            this.item = item;
            if (((PropertyItem) item).getDocumentItem() instanceof VectorLayer) {
                vectorCls = (IVectorCls) ((VectorLayer) ((PropertyItem) item).getDocumentItem()).getData();
            } else if (((PropertyItem) item).getDocumentItem() instanceof Vector3DLayer) {
                vectorCls = (IVectorCls) ((Vector3DLayer) ((PropertyItem) item).getDocumentItem()).getData();
            }
            editor = new HBox();
            button = new Button("...");
            textField = new TextField("");
            editor.getChildren().addAll(textField, button);
            HBox.setHgrow(textField, Priority.ALWAYS);
            Object obj = item.getValue();
            if (obj instanceof String) {
                value.set((T) item.getValue());
                textField.setText((String) obj);
            }

            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    SQLQueryDialog sqlQueryDialog = new SQLQueryDialog(vectorCls, textField.getText());
                    Optional<ButtonType> response = sqlQueryDialog.showAndWait();
                    if (response.isPresent() && response.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                        value.setValue((T) sqlQueryDialog.getSQLText());
                        textField.setText(sqlQueryDialog.getSQLText());
                    }
                }
            });
        }

        @Override
        public Node getEditor() {
            return editor;
        }

        @Override
        public T getValue() {
            return value.get();
        }

        @Override
        public void setValue(T t) {
            value.set(t);
            if (t != null) {
                Object obj = item.getValue();
                if (obj instanceof String) {
                    this.textField.setText((String) obj);
                }
            }
        }
    }

    /**
     * 显示比例 编辑器
     *
     * @param <T>
     */
    public static class Dot3DEditor<T> implements PropertyEditor<T> {

        private final HBox editor;
        private Button button;
        private TextField textField;
        private final PropertySheet.Item item;
        private final ObjectProperty<T> value = new SimpleObjectProperty<>();

        private Dot3D dt3D;
        public Dot3DEditor(PropertySheet.Item item) {
            this.item = item;
            editor = new HBox();
            textField = new TextField("");
            textField.setDisable(true);
            button = new Button("...");
            editor.getChildren().addAll(textField,button);
            HBox.setHgrow(textField,Priority.ALWAYS);
            Object obj = item.getValue();
            if (obj instanceof Dot3D) {
                value.set((T) item.getValue());
                dt3D = (Dot3D)obj;
                textField.setText(String.format("%d,%d,%d",(int)dt3D.getX(),(int)dt3D.getY(),(int)dt3D.getZ()));
            }
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Dot3DEditorDialog dot3DEditorDialog = new Dot3DEditorDialog(dt3D);
                    Optional<ButtonType> rtn =  dot3DEditorDialog.showAndWait();
                    if (rtn.get().getButtonData().equals(ButtonType.OK.getButtonData()) ) {
                        if (rtn != null) {
                            dt3D = dot3DEditorDialog.getDot3D();
                            textField.setText(String.format("%d,%d,%d",(int)dt3D.getX(),(int)dt3D.getY(),(int)dt3D.getZ()));
                            item.setValue(dt3D);
                        }
                    }
                }
            });
        }

        @Override
        public Node getEditor() {
            return editor;
        }

        @Override
        public T getValue() {
            return value.get();
        }

        @Override
        public void setValue(T t) {
            value.set(t);
            if (t != null) {
                Object obj = item.getValue();
                if (obj instanceof Dot3D) {
                    dt3D = (Dot3D)obj;
                    textField.setText(String.format("%d,%d,%d",(int)dt3D.getX(),(int)dt3D.getY(),(int)dt3D.getZ()));
                }
            }
        }
    }

    /**
     * 显示范围/渲染范围 编辑器
     *
     * @param <T>
     */
    public static class RectBoundEitor<T> implements PropertyEditor<T> {

        private final HBox editor;
        private Button button;
        private TextField textField;
        private final PropertySheet.Item item;
        private final ObjectProperty<T> value = new SimpleObjectProperty<>();

        private Rect rect;
        public RectBoundEitor(PropertySheet.Item item) {
            this.item = item;
            editor = new HBox();
            textField = new TextField("");
            textField.setDisable(true);
            button = new Button("...");
            editor.getChildren().addAll(textField,button);
            HBox.setHgrow(textField,Priority.ALWAYS);
            Object obj = item.getValue();
            if (obj instanceof Rect) {
                value.set((T) item.getValue());
                rect = (Rect)obj;
                textField.setText(String.format("%.2f,%.2f,%.2f,%.2f",
                        rect.getXMin(),rect.getYMin(),rect.getXMax(),rect.getYMax()));
            }
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    RectBoundEitorDialog rectBoundEitorDialog = new RectBoundEitorDialog(rect);
                    Optional<ButtonType> rtn =  rectBoundEitorDialog.showAndWait();
                    if (rtn.get().getButtonData().equals(ButtonType.OK.getButtonData()) ) {
                        if (rtn != null) {
                            rect = rectBoundEitorDialog.getRect();
                            textField.setText(String.format("%.2f,%.2f,%.2f,%.2f",
                                    rect.getXMin(),rect.getYMin(),rect.getXMax(),rect.getYMax()));
                            item.setValue(rect);
                        }
                    }
                }
            });
        }

        @Override
        public Node getEditor() {
            return editor;
        }

        @Override
        public T getValue() {
            return value.get();
        }

        @Override
        public void setValue(T t) {
            value.set(t);
            if (t != null) {
                Object obj = item.getValue();
                if (obj instanceof Rect) {
                    rect = (Rect)obj;
                    textField.setText(String.format("%.2f,%.2f,%.2f,%.2f",
                            rect.getXMin(),rect.getYMin(),rect.getXMax(),rect.getYMax()));
                }
            }
        }
    }

    /**
     * 下拉项
     *
     * @param <T>
     */
    public static class PropertySheetComboBoxEditor<T> implements PropertyEditor<T> {

        private final PropertyComboBox editor;
        private final PropertySheet.Item item;
        private final ObjectProperty<T> value = new SimpleObjectProperty<>();

        public PropertySheetComboBoxEditor(PropertySheet.Item item) {
            this.item = item;
            Object obj = item.getValue();
            if (obj != null) {
                value.set((T) obj);
            }
            ObservableList<IPropertyEx> items = FXCollections.observableArrayList();
            if (obj instanceof IPropertyEx) {
                editor = new PropertyComboBox((IPropertyEx) obj);
            } else {
                editor = new PropertyComboBox(null);
            }
            if (editor.getObservableValue() != null) {
                editor.getObservableValue().addListener(new ChangeListener() {
                    @Override
                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {

                    }
                });
            }
        }

        @Override
        public Node getEditor() {
            return editor;
        }

        @Override
        public T getValue() {
            return value.get();
        }

        @Override
        public void setValue(T t) {
            value.set(t);
            if (t instanceof IPropertyEx) {
                editor.setValue((IPropertyEx) t);
            }
        }

    }

//    /**
//     *  下拉项
//     *
//     * @param <T>
//     */
//    public static class PropertySheetComboBoxEditor<T> implements PropertyEditor<T> {
//
//        private final ComboBox<IPropertyEx> editor;
//        private final PropertySheet.Item item;
//        private final ObjectProperty<T> value = new SimpleObjectProperty<>();
//
//        public PropertySheetComboBoxEditor(PropertySheet.Item item) {
//            this.item = item;
//            Object obj = item.getValue();
//            if (obj != null) {
//                value.set((T) obj);
//            }
//            ObservableList<IPropertyEx> items = FXCollections.observableArrayList();
//            if (obj instanceof SizeDouble)
//                items.addAll((SizeDouble)obj);
//            if (items.size() > 0) {
//                editor = new ComboBox(items);
//            } else {
//                editor = new ComboBox();
//            }
//
//            if (obj != null) {
//                editor.setValue((IPropertyEx)obj);
//            }
//            editor.setCellFactory(new Callback<ListView<IPropertyEx>, ListCell<IPropertyEx>>() {
//                @Override
//                public ListCell<IPropertyEx> call(ListView<IPropertyEx> param) {
//                    return new PropertyExListCell();
//                }
//            });
////            editor.setConverter(new StringConverter<T>() {
////                @Override
////                public String toString(T object) {
////                    String objStr = "";
////                    if (object instanceof SystemLibrary) {
////                        objStr = ((SystemLibrary) object).getName();
////                    }
////                    return (String) objStr;
////                }
////
////                @Override
////                public T fromString(String string) {
////                    return null;
////                }
////            });
////            editor.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<T>() {
////                @Override
////                public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
////                    value.set(newValue);
////                    item.setValue(newValue);
////                }
////            });
//        }
//
//        @Override
//        public Node getEditor() {
//            return editor;
//        }
//
//        @Override
//        public T getValue() {
//            return value.get();
//        }
//
//        @Override
//        public void setValue(T t) {
//            value.set(t);
//            if (t instanceof IPropertyEx) {
//                editor.setValue((IPropertyEx)t);
//            }
//        }
//
//    }

    public static class PropertyExListCell extends ListCell<IPropertyEx> {

        public PropertyExListCell() {
            super();
        }


        @Override
        protected void updateItem(IPropertyEx item, boolean arg1) {
            super.updateItem(item, arg1);
            // 实现的单元格显示
            if (item == null) {
                this.setText("");
            } else {
//                Button button = new Button("aa");
//                setGraphic(button);
                PropertySheet propertySheet = item.getPropertySheet();
                int count = propertySheet.getItems().size();
                setGraphic(propertySheet);
            }
        }
    }
    //endregion

    protected static Window window = null;

    /**
     * 获取当前窗口的window对象
     *
     * @return 当前窗口的window对象
     */
    protected static Window getCurrentWindow(Node node) {
        if (window == null) {
            window = node.getScene().getWindow();
        }
        return window;
    }

    /**
     * 根据属性编辑项集合创建属性表
     *
     * @param items 属性编辑项集合
     * @return
     */
    protected static PropertySheet createPropertySheet(PropertyItem[] items) {
        PropertySheet propertySheet = null;
        if (items != null) {
            ArrayList<PropertyItem> tmpItems = new ArrayList<>();
            for (int i = 0; i < items.length; i++) {
                if (items[i] != null && items[i].isVisible()) {
                    tmpItems.add(items[i]);
                }
            }
            PropertyItem[] newItems = new PropertyItem[0];
            newItems = (PropertyItem[]) tmpItems.toArray(newItems);

            ObservableList<PropertySheet.Item> list = FXCollections.observableArrayList();
            list.addAll(newItems);
            propertySheet = new PropertySheet(list);
            propertySheet.setPadding(new Insets(0, 5, 0, 5));
            propertySheet.setSearchBoxVisible(false);
            propertySheet.setMode(PropertySheet.Mode.CATEGORY);
            propertySheet.setModeSwitcherVisible(false);
        }
        return propertySheet;
    }

    //region 比例尺处理

    protected static double minDef = 0.000001;
    protected static double maxDef = 1e100;

    /**
     * 比例尺处理
     */
    protected static class ScaleHandler {
        /// <summary>
        /// 根据比例尺分母值获取比例尺字符串
        /// </summary>
        public static String getScaleString(double scale) {
            if (Math.abs(scale) <= minDef || Math.abs(scale) >= maxDef) {
                return "<None>";
            }
            DecimalFormat format = new DecimalFormat("################.##########");
            String scaleStr = format.format(scale);
            return String.format("1:%s", scaleStr);
        }

        /**
         * 获取字符串比例尺(1:10000)的分母
         *
         * @param scale
         * @param isMinScale
         * @return
         */
        public static double getScaleDouble(String scale, boolean isMinScale) {

            if (scale == null || scale.isEmpty() || scale.trim().compareTo("<None>") == 0) {
                return isMinScale ? 1e100 : 0;
            }
            int strIndex = scale.indexOf("1:");
            if (strIndex >= 0) {
                scale = scale.substring(strIndex + 2);
            }
            double rtn = 0;
            rtn = Double.parseDouble(scale);
            return rtn;
        }

        public static double getScaleDouble(String scale) {
            return getScaleDouble(scale, false);
        }

        private static String deafultScaleFile = null;

        /// <summary>
        /// 获取当前比例尺列表
        /// </summary>
        public static ArrayList<Double> GetDefaultScales() throws IOException {
            ArrayList<Double> scaleList = new ArrayList<>();

//            List<double> currentScaleList = AppDomain.CurrentDomain.GetData("CurrentScaleList") as List<double>;
//            if (currentScaleList != null)
//            {
//                scaleList.AddRange(currentScaleList);
//                scaleList.Sort(delegate(double a, double b)
//                {
//                    return a > b ? -1 : 1;
//                });
//            }

            if (deafultScaleFile == null) {
                //修改说明：现在将自定义比例尺的文件路径统一保存在program目录下，各地方设置的默认比例尺将作用于同一份文件。
                //修改人：张凯俊 2019-2-28
                String searchPath = System.getProperty("usr.dir") + "\\CustomScale.txt";
                File file = new File(searchPath);
                if (file.exists()) {
                    deafultScaleFile = searchPath;
                }
            }

            if (deafultScaleFile != null) {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(deafultScaleFile));
                String line;
                while ((line = bufferedReader.readLine()) != null) {

                    double d = Double.parseDouble(line);
                    if (!scaleList.contains(d)) {
                        scaleList.add(d);
                    }
                }
                bufferedReader.close();
                scaleList.sort(new Comparator<Double>() {
                    @Override
                    public int compare(Double o1, Double o2) {
                        return o1 > o2 ? -1 : 1;
                    }
                });
            } else {
                scaleList.add((double) 500000);
                scaleList.add((double) 250000);
                scaleList.add((double) 200000);
                scaleList.add((double) 100000);
                scaleList.add((double) 50000);
                scaleList.add((double) 25000);
                scaleList.add((double) 10000);
                scaleList.add((double) 5000);
            }
            return scaleList;
        }
    }

    //endregion

    //region 自定义枚举

    /**
     * 属性过滤模式
     */
    public enum FilterMode {
        /**
         * 无
         */
        None,

        /**
         * 属性过滤
         */
        AttFilter,

        /**
         * 范围过滤
         */
        RectFilter
    }

    /**
     * 裁减模式
     */
    public enum CullingMode {
        /**
         * 无
         */
        None,

        /**
         * 顺时针
         */
        Clockwise,

        /**
         * 逆时针
         */
        AntiClockwise
    }
    /**
     * 裁减模式
     */
    public enum RenderModeDef {

        /**
         * 矢量绘制
         */
        RenderModeVector,

        /**
         * 栅格绘制
         */
        RenderModeRaster
    }
    //endregion
}
