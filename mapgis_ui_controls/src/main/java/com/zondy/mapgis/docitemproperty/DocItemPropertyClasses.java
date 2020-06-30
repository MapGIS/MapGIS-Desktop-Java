package com.zondy.mapgis.docitemproperty;

import com.zondy.mapgis.base.LanguageConvert;
import com.zondy.mapgis.controls.MapGISColorPicker;
import com.zondy.mapgis.geodatabase.*;
import com.zondy.mapgis.geodatabase.config.EnvConfig;
import com.zondy.mapgis.geodatabase.config.SysConfigDirType;
//import com.zondy.mapgis.geodatabase.raster.RasGrayConvertForm;
import com.zondy.mapgis.geodatabase.raster.RasterCatalog;
//import com.zondy.mapgis.geodatabase.raster.RasterDispInf;
//import com.zondy.mapgis.geodatabase.raster.RasterSampling;
import com.zondy.mapgis.geodatabase.raster.RasterColorInterp;
import com.zondy.mapgis.geodatabase.raster.RasterResampling;
import com.zondy.mapgis.geometry.*;
import com.zondy.mapgis.info.LinInfo;
import com.zondy.mapgis.info.PntInfo;
import com.zondy.mapgis.info.RegInfo;
import com.zondy.mapgis.map.*;
import com.zondy.mapgis.scene.*;
import com.zondy.mapgis.srs.ElpTransParam;
import com.zondy.mapgis.srs.ElpTransformation;
import com.zondy.mapgis.srs.SRefData;
import com.zondy.mapgis.systemlib.SystemLibrary;
import com.zondy.mapgis.systemlib.SystemLibrarys;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.controlsfx.control.PropertySheet;

import java.io.*;
import java.util.ArrayList;
import java.util.UUID;


/**
 * @Description: 图层属性框中用于显示的属性类
 * @Author ysp
 * @Date 2019/11/22
 **/
public class DocItemPropertyClasses extends PropertyBaseClass {

    //region 文档 地图 场景

    /**
     * 文档-常规
     */
    public static class DocumentLayoutProperty implements IPropertyEx {
        private boolean isUpdate = false;//是否立即更新
        private boolean hasChanged = false;//类中的属性值是否被编辑过
        private PropertySheet propertySheet;
        private Document item = null;//当前显示属性的文档项
        private PropertyItem[] items;
        private int mapNum = 0;//Map数
        private int graphItemCount = 0;//制图元素数
        private String title = "";//标题
        private String subject = "";//主题
        private String author = "";//作者
        private String category;//类别
        private String keywords;//关键字
        private String comments;//备注

        @Override
        public boolean hasChanged() {
            return false;
        }

        @Override
        public void apply() {
            if (!isUpdate && hasChanged) {
                if (item instanceof Document) {
                    item.setTitle(this.title);
                    item.setSubject(this.subject);
                    item.setAuthor(this.author);
                    item.setCategory(this.category);
                    item.setKeywords(this.keywords);
                    item.setComments(this.comments);
                }
            }
        }

        @Override
        public Document getItem() {
            return this.item;
        }

        @Override
        public void setItem(Object obj) {
            if (obj instanceof Document) {
                this.item = (Document) obj;
                this.title = this.item.getTitle();
                this.subject = this.item.getSubject();
                this.author = this.item.getAuthor();
                this.category = this.item.getCategory();
                this.keywords = this.item.getKeywords();
                this.comments = this.item.getComments();
                items = new PropertyItem[8];
                items[0] = new PropertyItem("Map数", "Map数", "常规", false, Integer.class, null);
                items[0].setValue(this.item.getMaps().getCount());
                items[1] = new PropertyItem("制图元素数", "制图元素数", "常规", false, Integer.class, null);
                items[1].setValue(this.item.getLayout().getCount());
                items[2] = new PropertyItem("标题", "标题", "常规", true, String.class, null);
                items[2].setValue(this.title);
                if (items[2].getObservableValue().isPresent()) {
                    items[2].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            hasChanged = true;
                            title = (String) newValue;
                            if (isUpdate) {
                                item.setTitle(title);
                            }
                        }
                    });
                }
                items[3] = new PropertyItem("主题", "主题", "常规", true, String.class, null);
                items[3].setValue(this.subject);
                if (items[3].getObservableValue().isPresent()) {
                    items[3].getObservableValue().get().addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> {
                        hasChanged = true;
                        subject = (String) newValue;
                        if (isUpdate) {
                            item.setSubject(subject);
                        }
                    });
                }
                items[4] = new PropertyItem("作者", "作者", "常规", true, String.class, null);
                items[4].setValue(author);
                if (items[4].getObservableValue().isPresent()) {
                    items[4].getObservableValue().get().addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> {
                        hasChanged = true;
                        author = (String) newValue;
                        if (isUpdate) {
                            item.setAuthor(author);
                        }
                    });
                }
                items[5] = new PropertyItem("类别", "类别", "常规", true, String.class, null);
                items[5].setValue(category);
                if (items[5].getObservableValue().isPresent()) {
                    items[5].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            hasChanged = true;
                            category = (String) newValue;
                            if (isUpdate) {
                                item.setCategory(category);
                            }
                        }
                    });
                }

                items[6] = new PropertyItem("关键字", "关键字", "常规", true, String.class, null);
                items[6].setValue(this.keywords);
                if (items[6].getObservableValue().isPresent()) {
                    items[6].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            hasChanged = true;
                            keywords = (String) newValue;
                            if (isUpdate) {
                                item.setKeywords(keywords);
                            }
                        }
                    });
                }

                items[7] = new PropertyItem("备注", "备注", "常规", true, String.class, null);
                items[7].setValue(this.comments);
                if (items[7].getObservableValue().isPresent()) {
                    items[7].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            hasChanged = true;
                            comments = (String) newValue;
                            if (isUpdate) {
                                item.setComments(comments);
                            }
                        }
                    });
                }
                //ceshi
//                SizeDouble aa = new SizeDouble();
//                aa.setItem(new Dot(0, 0));
//                items[8] = new PropertyItem("备注", "备注", "常规", true, String.class, PropertySheetComboBoxEditor.class);
//                items[8].setValue(aa);
//                if (items[8].getObservableValue().isPresent()) {
//                    items[8].getObservableValue().get().addListener(new ChangeListener<Object>() {
//                        @Override
//                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
//                            if (newValue instanceof SizeDouble) {
//                                hasChanged = true;
////                                comments = (String) newValue;
//                                if (isUpdate) {
////                                    item.setComments(comments);
//                                }
//                            }
//                        }
//                    });
//                }

                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setDocumentItem(item);
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
            return this.getClass().toString();
        }
    }

    /**
     * 地图-常规
     */
    public static class MapCommonProperty implements IProperty {
        private boolean isUpdate = false;//是否立即更新
        private boolean hasChanged = false;//类中的属性值是否被编辑过
        private PropertySheet propertySheet;
        private DocumentItem item = null;//当前显示属性的文档项
        private PropertyItem[] items;
        private String name;
        private String description;
        private RectBound dataScope;//数据范围
        private double angle = 0.0;//旋转角度
        private ZhDot dt = new ZhDot(0, 0);//旋转中心
        //        private Dot dt = new Dot(0, 0);//旋转中心
        private boolean isDisplayOnLayout = false;//版面是否可见
        private boolean initOpenView = false;//初始打开视图
        private boolean isProjTrans = false;//动态投影
        private SRefData sRefData = new SRefData();//投影参照系
        private boolean isFixedScalesDisplay = false;//是否固定比例尺显示
        private double[] scales = null;

        @Override
        public boolean hasChanged() {
            return false;
        }

        @Override
        public void apply() {
            if (!isUpdate && hasChanged) {
                if (item instanceof Map) {
                    ((Map) item).setName(name);
                    ((Map) item).setDescription(description);
                    ((Map) item).setRotateAngle(angle);
                    ((Map) item).setRotateCenter(dt.getDot());
                    ((Map) item).setIsDisplayOnLayout(isDisplayOnLayout);
                    ((Map) item).setPropertyEx("InitOpenView", initOpenView ? "true" : "false");
                    ((Map) item).setIsProjTrans(isProjTrans);
                    ((Map) item).setProjTrans(sRefData);
//                    ((Map) item).setIsFixedScalesDisplay(isFixedScalesDisplay);
                    if (scales != null) {
//                        ((Map) item).setScaleRange(scales[0], scales[scales.length - 1]);
                    }
                }
            }
        }

        @Override
        public DocumentItem getDocItem() {
            return this.item;
        }

        @Override
        public void setDocItem(DocumentItem item) {
            if (item instanceof Map) {
                this.item = item;
                Map map = (Map) this.item;
                this.name = map.getName();
                this.description = map.getDescription();
                dataScope = new RectBound(map.getRange());
                this.angle = map.getRotateAngle();
                Dot dot = map.getRotateCenter();
                dt = new ZhDot(dot.getX(), dot.getY());
                isDisplayOnLayout = map.getIsDisplayOnLayout();
                Object obj = map.getPropertyEx("InitOpenView");
                if (obj instanceof String) {
                    this.initOpenView = obj.equals("true");
                }
                isProjTrans = map.getIsProjTrans();
                sRefData = map.getProjTrans();
                items = new PropertyItem[9];
                items[0] = new PropertyItem("名称", "名称", "常规", true, String.class, null);
                items[0].setValue(this.name);
                if (items[0].getObservableValue().isPresent()) {
                    items[0].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            hasChanged = true;
                            name = (String) newValue;
                            if (isUpdate) {
                                if (item instanceof Map) {
                                    ((Map) item).setName(name);
                                }
                            }
                        }
                    });
                }
                items[1] = new PropertyItem("描述", "描述", "常规", true, String.class, null);
                items[1].setValue(this.description);
                if (items[1].getObservableValue().isPresent()) {
                    items[1].getObservableValue().get().addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> {
                        hasChanged = true;
                        description = (String) newValue;
                        if (isUpdate) {
                            if (item instanceof Map) {
                                ((Map) item).setDescription(description);
                            }
                        }
                    });
                }
                items[2] = new PropertyItem("数据范围", "数据范围", "常规", true,
                        String.class, PopupPropertyEditor.class);
                items[2].setValue(dataScope);//旋转中心
                items[3] = new PropertyItem("旋转角度", "旋转角度", "常规", true,
                        Double.class, null);
                items[3].setValue(angle);
                if (items[3].getObservableValue().isPresent()) {
                    items[3].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            hasChanged = true;
                            angle = (double) newValue;
                            if (isUpdate) {
                                if (item instanceof Map) {
                                    ((Map) item).setRotateAngle(angle);
                                }
                            }
                        }
                    });
                }
                items[4] = new PropertyItem("旋转中心", "旋转中心", "常规", false, String.class, PopupPropertyEditor.class);
                items[4].setValue(this.dt);
//                items[4].setValue(this.dt);
                if (items[4].getObservableValue().isPresent()) {
                    items[4].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof ZhDot) {
                                hasChanged = true;
                                dt = (ZhDot) newValue;
                            }
                            if (isUpdate && hasChanged) {
                                if (item instanceof Map) {
                                    ((Map) item).setRotateCenter(dt.getDot());
                                }
                            }
                        }
                    });
                }
                items[5] = new PropertyItem("版面是否可见", "版面是否可见", "常规", true, Boolean.class, null);
                items[5].setValue(isDisplayOnLayout);
                items[5].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Boolean) {
                            isDisplayOnLayout = (boolean) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            if (item instanceof Map) {
                                ((Map) item).setIsDisplayOnLayout(isDisplayOnLayout);
                            }
                        }
                    }
                });
                items[6] = new PropertyItem("初始打开视图", "初始打开视图", "常规", true, Boolean.class, null);
                items[6].setValue(initOpenView);
                items[6].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        initOpenView = (boolean) newValue;
                        hasChanged = true;
                        if (isUpdate) {
                            ((Map) item).setPropertyEx("InitOpenView", initOpenView ? "true" : "false");
                        }
                    }
                });
                items[7] = new PropertyItem("动态投影", "动态投影", "常规", true, Boolean.class, null);
                items[7].setValue(this.isProjTrans);
                items[7].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        isProjTrans = (boolean) newValue;
                        items[8].setEditable(isProjTrans);
                        if (propertySheet != null) {
                            propertySheet.getItems().setAll(items);
                        }
                        hasChanged = true;
                        if (isUpdate) {
                            ((Map) item).setIsProjTrans(isProjTrans);
                        }
                    }
                });
                items[8] = new PropertyItem("投影参照系", "投影参照系", "常规",
                        true, String.class, SRefDataPropertyEditor.class);
                items[8].setValue(sRefData);
                items[8].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof SRefData) {
                            sRefData = (SRefData) newValue;
                            hasChanged = true;
                            if (isUpdate) {
                                items[8].setValue(sRefData);
                                ((Map) item).setProjTrans(sRefData);
                            }
                        }
                    }
                });
//                items[9] = new PropertyItem("是否固定比例尺显示", "是否固定比例尺显示", "常规", true, Boolean.class, null);
//                items[9].setValue(this.isFixedScalesDisplay);
//                items[9].getObservableValue().get().addListener(new ChangeListener<Object>() {
//                    @Override
//                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
//                        isFixedScalesDisplay = (boolean) newValue;
//                        items[10].setEditable(isFixedScalesDisplay);
//                        if (propertySheet != null) {
//                            propertySheet.getItems().setAll(items);
//                        }
//                        hasChanged = true;
//                        if (isUpdate) {
//                            ((Map) item).setIsFixedScalesDisplay(isFixedScalesDisplay);
//                        }
//                    }
//                });
//                items[10] = new PropertyItem("固定显示比", "固定显示比", "常规",
//                        true, String.class, null);
//                String scalesStr = "";
//                if (scales != null) {
//                    for (int i = 0; i < scales.length; i++) {
//                        scalesStr += String.valueOf(scales[i]);
//                        if (i < scales.length - 1) {
//                            scalesStr += ";";
//                        }
//                    }
//                }
//                items[10].setValue(scalesStr);
//                items[10].getObservableValue().get().addListener(new ChangeListener<Object>() {
//                    @Override
//                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
//                        if (newValue instanceof double[]) {
//                            scales = (double[]) newValue;
//                            hasChanged = true;
//                            if (isUpdate) {
//                                if (scales != null) {
//                                    ((Map) item).setFixedScalesCount(scales.length);
//                                    ((Map) item).setScaleRange(scales[0], scales[scales.length - 1]);
//                                }
//                            }
//                        }
//                    }
//                });
                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setDocumentItem(item);
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
    }

    /**
     * 地图-制图数据
     */
    public static class GraphicsDataProperty implements IProperty {
        private boolean isUpdate = false;//是否立即更新
        private boolean hasChanged = false;//类中的属性值是否被编辑过
        private PropertySheet propertySheet;
        private DocumentItem item = null;//当前显示属性的文档项
        private PropertyItem[] items;
        //GraphicsData未封装
//        private GraphicsData graphicsData = null;
        private boolean bSymbolShow = false;
        private SystemLibrary systemLib = null;
        private LayerState layerState = LayerState.UnVisible;

        @Override
        public boolean hasChanged() {
            return false;
        }

        @Override
        public void apply() {
            if (!isUpdate && hasChanged) {
                if (item instanceof Map) {

                }
            }
        }

        @Override
        public DocumentItem getDocItem() {
            return this.item;
        }

        @Override
        public void setDocItem(DocumentItem item) {
            if (item instanceof Map) {
                this.item = item;
                Map map = (Map) this.item;
//                graphicsData = map.GraphicsData;
//                if (graphicsData != null)
//                {
//                    //region 系统库和状态
//                    this.layerState = graphicsData.getState();
//                    this.systemLib.getSymbolLibarary(graphicsData.getSysLibrary());
//                    VectorLayer layer = (VectorLayer)graphicsData.getPntLayer();
//                    if (layer != null)
//                        this.bSymbolShow |= layer.getSymbolShow();
//                    layer = (VectorLayer)graphicsData.getLinLayer();
//                    if (layer != null)
//                        this.bSymbolShow |= layer.getSymbolShow();
//                    layer = (VectorLayer)graphicsData.getRegLayer();
//                    if (layer != null)
//                        this.bSymbolShow |= layer.getSymbolShow();
//                    layer = (VectorLayer)graphicsData.getAnnLayer();
//                    if (layer != null)
//                        this.bSymbolShow |= layer.getSymbolShow();
//                    //endregion
//                }
                items = new PropertyItem[3];
                items[0] = new PropertyItem("符号化显示", "符号化显示", "常规", true, Boolean.class, null);
                items[0].setValue(this.bSymbolShow);
                if (items[0].getObservableValue().isPresent()) {
                    items[0].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            hasChanged = true;
                            bSymbolShow = (boolean) newValue;
                            if (isUpdate) {
                                if (item instanceof Map) {
                                }
                            }
                        }
                    });
                }
                items[1] = new PropertyItem("状态", "状态", "图层", true, LayerState.class,
                        ComboBoxPropertyEditor.class);
                items[1].setValue(layerState);
                if (items[1].getObservableValue().isPresent()) {
                    items[1].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            hasChanged = true;
                            layerState = (LayerState) newValue;
                            if (isUpdate) {

                            }
                        }
                    });
                }
                items[2] = new PropertyItem("系统库", "系统库", "图层", true, SystemLibrary.class, SystemLibComboBoxEditor.class);
                items[2].setValue(this.systemLib);
                items[2].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof SystemLibrary) {
                            systemLib = (SystemLibrary) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate) {

                        }
                    }
                });
                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setDocumentItem(item);
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
    }

    /**
     * 场景-常规
     */
    public static class SceneProperty implements IProperty {
        private boolean isUpdate = false;//是否立即更新
        private boolean hasChanged = false;//类中的属性值是否被编辑过
        private PropertySheet propertySheet;
        private DocumentItem item = null;//当前显示属性的文档项
        private PropertyItem[] items;
        private String name = "新场景";//名称
        private String description = "";//描述
        private Rect3DBound dataScope = new Rect3DBound(new Rect3D(0, 0, 0, 0, 0, 0));//数据范围
        private SceneMode sceneMode = SceneMode.LOCAL;//场景模式
        private boolean initOpenView = false;//初始打开视图
        private boolean isAutoReset = true;//自动复位
        private boolean isProjTrans = false;//动态投影
        private SRefData sRefData = new SRefData();//投影参照系

        @Override
        public boolean hasChanged() {
            return false;
        }

        @Override
        public void apply() {
            if (!isUpdate && hasChanged) {
                if (this.item instanceof Scene) {
                    ((Scene) this.item).setSceneMode(this.sceneMode);
                    ((Scene) this.item).setPropertyEx("InitOpenView", initOpenView ? "true" : "false");
//                    ((Scene) this.item).IsReset = this.isAutoReset;
                    ((Scene) item).setProjTrans(isProjTrans);
                    ((Scene) item).setProjTrans(sRefData);
                }
            }
        }

        @Override
        public DocumentItem getDocItem() {
            return this.item;
        }

        @Override
        public void setDocItem(DocumentItem item) {
            if (item instanceof Scene) {
                this.item = item;
                Scene scene = (Scene) this.item;
                this.name = scene.getName();
                this.description = scene.getDescription();
                Rect3D rect3D = new Rect3D();
                scene.getExtent(rect3D);
                dataScope = new Rect3DBound(rect3D);
                sceneMode = scene.getSceneMode();
////                this.isAutoReset = scene.IsReset;//未实现
                Object obj = scene.getPropertyEx("InitOpenView");
                if (obj instanceof String) {
                    this.initOpenView = obj.equals("true");
                }
                isProjTrans = scene.isProjTrans();
                sRefData = scene.getProjTrans();

                items = new PropertyItem[8];
                items[0] = new PropertyItem("名称", "名称", "常规", false, String.class, null);
                items[0].setValue(this.name);
                items[1] = new PropertyItem("描述", "描述", "常规", false, String.class, null);
                items[1].setValue(this.description);
                items[2] = new PropertyItem("数据范围", "数据范围", "常规", true,
                        String.class, PopupPropertyEditor.class);
                items[2].setValue(dataScope);
                items[3] = new PropertyItem("场景模式", "场景模式", "常规", true,
                        String.class, ComboBoxPropertyEditor.class);
                items[3].setValue(sceneMode);
                if (items[3].getObservableValue().isPresent()) {
                    items[3].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            hasChanged = true;
                            sceneMode = (SceneMode) newValue;
                            if (isUpdate) {
                                if (item instanceof Scene) {
                                    ((Scene) item).setSceneMode(sceneMode);
                                }
                            }
                        }
                    });
                }
                items[4] = new PropertyItem("初始打开视图", "初始打开视图", "常规", true, Boolean.class, null);
                items[4].setValue(this.initOpenView);
                items[4].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        initOpenView = (boolean) newValue;
                        hasChanged = true;
                        if (isUpdate) {
                            scene.setPropertyEx("InitOpenView", initOpenView ? "true" : "false");
                        }
                    }
                });
                items[5] = new PropertyItem("添加图层后自动复位", "添加图层后自动复位", "常规", true, Boolean.class, null);
                items[5].setValue(isAutoReset);
                items[5].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        isAutoReset = (boolean) newValue;
                        hasChanged = true;
                        if (isUpdate) {
//                            scene.IsReset = this.isAutoReset;
                        }
                    }
                });
                items[6] = new PropertyItem("动态投影", "动态投影", "常规", true, Boolean.class, null);
                items[6].setValue(this.isProjTrans);
                items[6].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        isProjTrans = (boolean) newValue;
                        items[8].setEditable(isProjTrans);
                        if (propertySheet != null) {
                            propertySheet.getItems().setAll(items);
                        }
                        hasChanged = true;
                        if (isUpdate) {
                            ((Scene) item).setProjTrans(isProjTrans);
                        }
                    }
                });
                items[7] = new PropertyItem("投影参照系", "投影参照系", "常规",
                        true, String.class, SRefDataPropertyEditor.class);
                items[7].setValue(sRefData);
                items[7].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof SRefData) {
                            sRefData = (SRefData) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            items[8].setValue(sRefData);
                            ((Scene) item).setProjTrans(sRefData);
                        }
                    }
                });
                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setDocumentItem(item);
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
            return this.isUpdate;
        }
    }
    //endregion

    //region 二维图层

    //region 数据源

    /**
     * 图层的数据源属性
     */
    public static class DataSourceProperty implements IProperty {
        private boolean isUpdate = false;//是否立即更新
        private boolean hasChanged = false;//类中的属性值是否被编辑过
        IBasCls basCls;
        private String svrName;
        private String loginUser;
        private String databaseName;
        private int databaseID;
        private DocumentItem docItem = null;//当前显示属性的文档项
        private PropertyItem[] items;
        private PropertySheet propertySheet;

        @Override
        public void setDocItem(DocumentItem item) {
            if (item instanceof MapLayer) {
                this.docItem = item;
                basCls = ((MapLayer) this.docItem).getData();
                if (basCls != null) {
                    DataBase db = basCls.getGDataBase();
                    if (db != null) {
                        databaseName = db.getName();
                        databaseID = db.getdbID();
                        Server sr = db.getServer();
                        String[] info = sr.getLogin();
                        if (sr != null) {
                            svrName = sr.getSvrName();
                            loginUser = info[0];
                        }
                    }
                }

                items = new PropertyItem[4];
                items[0] = new PropertyItem("数据源名称", "数据源名称", "数据源", false, String.class, null);
                items[0].setValue(this.svrName);
                items[1] = new PropertyItem("登陆用户", "登陆用户", "数据源", false, String.class, null);
                items[1].setValue(loginUser);
                items[2] = new PropertyItem("数据库名称", "数据库名称", "数据源", false, String.class, null);
                items[2].setValue(databaseName);
                items[3] = new PropertyItem("数据库ID", "数据库ID", "数据源", false, int.class, null);
                items[3].setValue(databaseID);
                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setDocumentItem(item);
                }
            }
        }

        @Override
        public DocumentItem getDocItem() {
            return this.docItem;
        }

        @Override
        public boolean hasChanged() {
            return this.hasChanged;
        }

        @Override
        public PropertyItem[] getPropertyItems() {

            return items;
        }

        @Override
        public void apply() {
            this.hasChanged = false;
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
            return this.isUpdate;
        }
    }

    /**
     * 图层的数据源属性(带数据范围)
     */
    public static class SpatialDataSourceProperty extends DataSourceProperty {
        private RectBound dataScope;//数据范围
        private PropertyItem[] items;
        private PropertySheet propertySheet;

        public SpatialDataSourceProperty() {

        }

        @Override
        public void setDocItem(DocumentItem item) {
            super.setDocItem(item);
            if (item != null && item instanceof MapLayer) {
                PropertyItem[] oldItems = super.getPropertyItems();
                dataScope = new RectBound(((MapLayer) item).getRange());
                items = new PropertyItem[oldItems.length + 1];
                items[0] = new PropertyItem("数据源范围", "数据源范围", "数据源", true,
                        String.class, PopupPropertyEditor.class);
                items[0].setValue(dataScope);
                for (int i = 0; i < oldItems.length; i++) {
                    items[i + 1] = oldItems[i];
                }
                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setDocumentItem(item);
                }
            }
        }

        @Override
        public void apply() {
            super.apply();
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
    }

    /**
     * 简单要素类 图层的数据源属性
     */
    public static class SfClsDataSourceProperty extends SpatialDataSourceProperty {
        private String clsName;
        private int clsID;
        private String geometryType;
        private int count;
        private PropertyItem[] items;
        private PropertySheet propertySheet;

        public SfClsDataSourceProperty() {
        }

        @Override
        public void setDocItem(DocumentItem item) {
            super.setDocItem(item);
            if (item instanceof VectorLayer) {
                SFeatureCls cls = (SFeatureCls) super.basCls;
                if (cls != null) {
                    this.clsName = cls.getName();
                    this.clsID = cls.getClsID();
                    this.geometryType = LanguageConvert.geomTypeConvert(cls.getGeomType());
                    this.count = (int) cls.getObjCount();
                    DataBase db = cls.getGDataBase();
                    if (db != null) {
                        String databaseName = db.getName();
                        int databaseID = db.getdbID();
                        Server sr = db.getServer();
                        String[] info = sr.getLogin();
                        String svrName = sr.getSvrName();
                        String loginUser = info[0];
                    }
                }
                PropertyItem[] oldItems = super.getPropertyItems();
                items = new PropertyItem[oldItems.length + 4];
                System.arraycopy(oldItems, 0, items, 0, oldItems.length);
                items[oldItems.length] = new PropertyItem("类名称", "类名称", "数据源", false, String.class, null);
                items[oldItems.length].setValue(this.clsName);
                items[oldItems.length + 1] = new PropertyItem("类ID", "类ID", "数据源", false, int.class, null);
                items[oldItems.length + 1].setValue(clsID);
                items[oldItems.length + 2] = new PropertyItem("几何类型", "几何类型", "数据源", false, String.class, null);
                items[oldItems.length + 2].setValue(geometryType);
                items[oldItems.length + 3] = new PropertyItem("简单要素数", "简单要素数", "数据源", false, int.class, null);
                items[oldItems.length + 3].setValue(count);
                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setDocumentItem(item);
                }
            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {

            return items;
        }

        @Override
        public void apply() {
            super.apply();
        }

        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
            }
            return propertySheet;
        }
    }

    /**
     * 注记类 图层的数据源属性
     */
    public static class AClsDataSourceProperty extends DataSourceProperty {
        private String clsName;
        private int clsID;
        private String geometryType;
        private int count;
        private PropertyItem[] items;
        private PropertySheet propertySheet;

        @Override
        public void setDocItem(DocumentItem item) {
            super.setDocItem(item);
            AnnotationCls cls = null;
            if (item instanceof VectorLayer) {
                cls = (AnnotationCls) super.basCls;
            }
            if (cls != null) {
                this.clsName = cls.getName();
                this.clsID = cls.getClsID();
                this.geometryType = LanguageConvert.geomTypeConvert(GeomType.GeomAnn);
                this.count = (int) cls.getObjCount();
                DataBase db = cls.getGDataBase();
                if (db != null) {
                    String databaseName = db.getName();
                    int databaseID = db.getdbID();
                    Server sr = db.getServer();
                    String[] info = sr.getLogin();
                    String svrName = sr.getSvrName();
                    String loginUser = info[0];
                }
                PropertyItem[] oldItems = super.getPropertyItems();
                items = new PropertyItem[oldItems.length + 3];
                System.arraycopy(oldItems, 0, items, 0, oldItems.length);
                items[oldItems.length] = new PropertyItem("类名称", "类名称", "数据源", false, String.class, null);
                items[oldItems.length].setValue(this.clsName);
                items[oldItems.length + 1] = new PropertyItem("类ID", "类ID", "数据源", false, int.class, null);
                items[oldItems.length + 1].setValue(clsID);
                items[oldItems.length + 2] = new PropertyItem("注记数", "注记数", "数据源", false, int.class, null);
                items[oldItems.length + 2].setValue(count);
                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setDocumentItem(item);
                }
            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {

            return items;
        }

        @Override
        public void apply() {
            super.apply();
        }

        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
            }
            return propertySheet;
        }
    }

    /**
     * 对象类 图层的数据源属性
     */
    public static class OClsDataSourceProperty extends DataSourceProperty {
        private String clsName;
        private int clsID;
        private String geometryType;
        private int count;
        private PropertyItem[] items;
        private PropertySheet propertySheet;

        @Override
        public void setDocItem(DocumentItem item) {
            super.setDocItem(item);
            ObjectCls cls = null;
            if (item instanceof ObjectLayer) {
                cls = (ObjectCls) super.basCls;
            }
            if (cls != null) {
                this.clsName = cls.getName();
                this.clsID = cls.getClsID();
                this.geometryType = LanguageConvert.geomTypeConvert(GeomType.GeomAnn);
                this.count = (int) cls.getObjCount();
                DataBase db = cls.getGDataBase();
                if (db != null) {
                    String databaseName = db.getName();
                    int databaseID = db.getdbID();
                    Server sr = db.getServer();
                    String[] info = sr.getLogin();
                    String svrName = sr.getSvrName();
                    String loginUser = info[0];
                }
                PropertyItem[] oldItems = super.getPropertyItems();
                items = new PropertyItem[oldItems.length + 3];
                System.arraycopy(oldItems, 0, items, 0, oldItems.length);
                items[oldItems.length] = new PropertyItem("类名称", "类名称", "数据源", false, String.class, null);
                items[oldItems.length].setValue(this.clsName);
                items[oldItems.length + 1] = new PropertyItem("类ID", "类ID", "数据源", false, int.class, null);
                items[oldItems.length + 1].setValue(clsID);
                items[oldItems.length + 2] = new PropertyItem("对象数", "对象数", "数据源", false, int.class, null);
                items[oldItems.length + 2].setValue(count);
                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setDocumentItem(item);
                }
            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {

            return items;
        }

        @Override
        public void apply() {
            super.apply();
        }

        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
            }
            return propertySheet;
        }
    }

    //endregion 数据源

    //region 常规

    /**
     * 图层的通用属性-常规属性
     */
    public static class CommonProperty implements IProperty {
        protected boolean isUpdate = false;//是否立即更新
        protected boolean hasChanged = false;
        private DocumentItem item = null;
        private PropertyItem[] items;
        private PropertySheet propertySheet;
        private String layerName = "";
        private LayerState layerState = LayerState.UnVisible;

        @Override
        public boolean hasChanged() {
            return this.hasChanged;
        }

        @Override
        public void apply() {
            if (!this.isUpdate && hasChanged) {
                if (this.item instanceof MapLayer) {
                    ((MapLayer) this.item).setName(this.layerName);
                    ((MapLayer) this.item).setState(layerState);
                }
            }
        }

        @Override
        public DocumentItem getDocItem() {
            return this.item;
        }

        @Override
        public void setDocItem(DocumentItem item) {
            this.item = item;
            if (this.item instanceof MapLayer) {
                this.layerName = ((MapLayer) this.item).getName();
                layerState = ((MapLayer) this.item).getState();
                items = new PropertyItem[2];
                items[0] = new PropertyItem("名称", "名称", "图层", true, String.class, null);
                items[0].setValue(layerName);
                if (items[0].getObservableValue().isPresent()) {
                    items[0].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            hasChanged = true;
                            layerName = (String) newValue;
                            if (isUpdate) {
                                if (item instanceof MapLayer) {
                                    ((MapLayer) item).setName(layerName);
                                }
                            }
                        }
                    });
                }
                items[1] = new PropertyItem("状态", "状态", "图层", true, String.class,
                        ComboBoxPropertyEditor.class);
                items[1].setValue(layerState);
                if (items[1].getObservableValue().isPresent()) {
                    items[1].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof LayerState) {
                                layerState = (LayerState) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (item instanceof MapLayer) {
                                    ((MapLayer) item).setState(layerState);
                                }
                            }
                        }
                    });
                }
                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setDocumentItem(item);
                }

            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return this.items;
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
            return this.isUpdate;
        }
    }

    /**
     * 对象类、组 图层的通用属性-常规属性
     */
    public static class ObjClsCommonProperty extends CommonProperty {
        private int legendCode = -1;
        private DocumentItem item = null;
        private PropertyItem[] items;
        private PropertySheet propertySheet;

        @Override
        public void apply() {
            super.apply();
            if (!this.isUpdate && hasChanged) {
                if (this.item instanceof MapLayer) {
                    this.item.setProperty("LayerClassCode", this.legendCode);
                }
            }
        }

        @Override
        public void setDocItem(DocumentItem item) {
            super.setDocItem(item);
            this.item = item;
            if (this.item instanceof MapLayer) {
                Object val = this.item.getProperty("LayerClassCode");
                if (val instanceof Integer) {
                    this.legendCode = (int) val;
                }
                PropertyItem[] oldItems = super.getPropertyItems();
                items = new PropertyItem[oldItems.length + 1];
                System.arraycopy(oldItems, 0, items, 0, oldItems.length);
                items[oldItems.length] = new PropertyItem("关联图例的分类码", "关联图例的分类码", "图层", true, int.class, null);
                items[oldItems.length].setValue(legendCode);
                items[oldItems.length].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        legendCode = (int) newValue;
                        hasChanged = true;
                        if (isUpdate) {
                            item.setProperty("LayerClassCode", legendCode);
                        }
                    }
                });
                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setDocumentItem(item);
                }
            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return this.items;
        }

        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
            }
            return propertySheet;
        }
    }

    /**
     * 6x 图层的通用属性-常规属性
     */
    public static class File6xCommonProperty extends ObjClsCommonProperty {
        //        private SystemLibrary systemLib = null;
        private DocumentItem item = null;
        private PropertyItem[] items;
        private PropertySheet propertySheet;

        private SRefData sRefData = new SRefData();

        @Override
        public void apply() {
            super.apply();
            //TODO ((FileLayer6x) item).getLib FileLayer6x缺少getLib接口
            if (!this.isUpdate && hasChanged) {
                if (this.item instanceof MapLayer) {
                    ((MapLayer) this.item).setSrefInfo(this.sRefData);
                }
            }
        }

        @Override
        public void setDocItem(DocumentItem item) {
            super.setDocItem(item);
            this.item = item;
            if (item instanceof MapLayer) {
                this.sRefData = ((MapLayer) item).getSrefInfo();
                PropertyItem[] oldItems = super.getPropertyItems();
                items = new PropertyItem[oldItems.length + 1];
                for (int i = 0; i < oldItems.length; i++) {
                    items[i] = oldItems[i];
                }
                items[oldItems.length] = new PropertyItem("参照系", "参照系", "图层", true, String.class,
                        SRefDataPropertyEditor.class);
                items[oldItems.length].setValue(sRefData);
                if (items[oldItems.length].getObservableValue().isPresent()) {
                    items[oldItems.length].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof SRefData) {
                                sRefData = (SRefData) newValue;
                                hasChanged = true;
                                if (isUpdate) {
                                    items[oldItems.length].setValue(sRefData);
                                }
                            }
                        }
                    });
                }
                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setDocumentItem(item);
                }
            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return this.items;
        }

        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
            }
            return propertySheet;
        }
    }

    /**
     * 网络类 图层的通用属性-常规属性
     */
    public static class NetClsCommonProperty extends ObjClsCommonProperty {
        private double minScale;
        private double maxScale;
        private DocumentItem item = null;
        private PropertyItem[] items;
        private PropertySheet propertySheet;

        @Override
        public void apply() {
            super.apply();
            if (!this.isUpdate && hasChanged) {
                if (this.item instanceof MapLayer) {
                    ((MapLayer) this.item).setScaleRange(new ScaleRange(minScale, maxScale));
                }
            }
        }

        @Override
        public void setDocItem(DocumentItem item) {
            super.setDocItem(item);
            this.item = item;
            if (this.item instanceof MapLayer) {
                ScaleRange scaleRange = ((MapLayer) this.item).getScaleRange();
                minScale = scaleRange.getMinScale();
                maxScale = scaleRange.getMaxScale();

                PropertyItem[] oldItems = super.getPropertyItems();
                items = new PropertyItem[oldItems.length + 2];
                for (int i = 0; i < oldItems.length; i++) {
                    items[i] = oldItems[i];
                }
                items[oldItems.length] = new PropertyItem("最小比例尺", "最小比例尺", "图层", true, String.class, ScaleComboBoxPropertyEditor.class);
                items[oldItems.length].setValue(minScale);
                items[oldItems.length].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        minScale = (double) newValue;
                        hasChanged = true;
                        if (isUpdate) {
                            ((MapLayer) item).setScaleRange(new ScaleRange(minScale, maxScale));
                        }
                    }
                });
                items[oldItems.length + 1] = new PropertyItem("最大比例尺", "最大比例尺", "图层", true, String.class, ScaleComboBoxPropertyEditor.class);
                items[oldItems.length + 1].setValue(maxScale);
                items[oldItems.length + 1].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        maxScale = (double) newValue;
                        hasChanged = true;
                        if (isUpdate) {
                            ((MapLayer) item).setScaleRange(new ScaleRange(minScale, maxScale));
                        }
                    }
                });
                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setDocumentItem(item);
                }
            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return this.items;
        }

        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
            }
            return propertySheet;
        }
    }

    /**
     * 地图集层类 图层的通用属性-常规属性
     */
    public static class MapSetCommonProperty extends NetClsCommonProperty {
        private SRefData sRefData = new SRefData();
        private DocumentItem item = null;
        private PropertyItem[] items;
        private PropertySheet propertySheet;

        @Override
        public void apply() {
            super.apply();
            if (!this.isUpdate && hasChanged) {
                if (item instanceof MapLayer) {
                    ((MapLayer) item).setSrefInfo(this.sRefData);
                }
            }
        }

        @Override
        public void setDocItem(DocumentItem item) {
            super.setDocItem(item);
            this.item = item;
            if (this.item instanceof MapLayer) {
                this.sRefData = ((MapLayer) this.item).getSrefInfo();
                PropertyItem[] oldItems = super.getPropertyItems();
                items = new PropertyItem[oldItems.length + 1];
                System.arraycopy(oldItems, 0, items, 0, oldItems.length);
                items[oldItems.length] = new PropertyItem("参照系", "参照系", "图层",
                        true, String.class, SRefDataPropertyEditor.class);
                items[oldItems.length].setValue(sRefData);
                items[oldItems.length].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof SRefData) {
                            sRefData = (SRefData) newValue;
                            hasChanged = true;
                        }
                        if (hasChanged && isUpdate) {
                            items[oldItems.length].setValue(sRefData);
                        }
                    }
                });
                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setDocumentItem(item);
                }
            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return this.items;
        }

        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
            }
            return propertySheet;
        }
    }

    /**
     * 简单要素类、注记类 图层的通用属性-常规属性
     */
    public static class SfOrAClsCommonProperty extends MapSetCommonProperty {
        private SystemLibrary systemLib = null;
        private DocumentItem item = null;
        private PropertyItem[] items;
        private PropertySheet propertySheet;

        @Override
        public void apply() {
            super.apply();
            if (!this.isUpdate && hasChanged) {
                if (item instanceof VectorLayer) {
                    ((VectorLayer) item).setSysLibrary(this.systemLib);
                }
            }
        }

        @Override
        public void setDocItem(DocumentItem item) {
            super.setDocItem(item);
            this.item = item;
            if (item instanceof VectorLayer) {
                this.systemLib = ((VectorLayer) item).getSysLibrary();
                PropertyItem[] oldItems = super.getPropertyItems();
                items = new PropertyItem[oldItems.length + 1];
                System.arraycopy(oldItems, 0, items, 0, oldItems.length);
                items[oldItems.length] = new PropertyItem("系统库", "系统库", "图层", true, SystemLibrary.class,
                        SystemLibComboBoxEditor.class);
                items[oldItems.length].setValue(this.systemLib);
                items[oldItems.length].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof SystemLibrary) {
                            systemLib = (SystemLibrary) newValue;
                            hasChanged = true;
                        }
                        if (hasChanged && isUpdate) {
                            ((VectorLayer) item).setSysLibrary(systemLib);
                        }
                    }
                });
                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setDocumentItem(item);
                }
            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return this.items;
        }

        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
            }
            return propertySheet;
        }
    }

    //endregion

    //region 栅格图层

    /**
     * 栅格 图层的数据源属性
     */
    public static class RasDataSourceProperty implements IProperty {
        private boolean isUpdate = false;//是否立即更新
        private boolean hasChanged = false;//类中的属性值是否被编辑过
        IBasCls basCls = null;
        private RectBound rectBound = null;
        private String svrName = "";
        private String loginUser = "";
        private String databaseName = "";
        private int databaseID;
        private DocumentItem docItem = null;//当前显示属性的文档项
        private PropertyItem[] items;
        private PropertySheet propertySheet;
        private String clsName = "";
        private int clsID = 0;

        @Override
        public void setDocItem(DocumentItem item) {
            if (item instanceof MapLayer) {
                this.docItem = item;
                basCls = ((MapLayer) this.docItem).getData();
                rectBound = new RectBound(((MapLayer) item).getRange());
                if (basCls != null) {
                    clsName = basCls.getName();
                    clsID = basCls.getClsID();
                    DataBase db = basCls.getGDataBase();
                    if (db != null) {
                        databaseName = db.getName();
                        databaseID = db.getdbID();
                        Server sr = db.getServer();
                        String[] info = sr.getLogin();
                        if (sr != null) {
                            svrName = sr.getSvrName();
                            loginUser = info[0];
                        }
                    }
                }

                items = new PropertyItem[7];
                items[0] = new PropertyItem("数据范围", "数据范围", "数据源", true,
                        String.class, PopupPropertyEditor.class);
                items[0].setValue(rectBound);
                items[1] = new PropertyItem("数据源名称", "数据源名称", "数据源", false, String.class, null);
                items[1].setValue(this.svrName);
                items[2] = new PropertyItem("登陆用户", "登陆用户", "数据源", false, String.class, null);
                items[2].setValue(loginUser);
                items[3] = new PropertyItem("数据库名称", "数据库名称", "数据源", false, String.class, null);
                items[3].setValue(databaseName);
                items[4] = new PropertyItem("数据库ID", "数据库ID", "数据源", false, int.class, null);
                items[4].setValue(databaseID);
                items[5] = new PropertyItem("类名称", "类名称", "数据源", false, String.class, null);
                items[5].setValue(this.clsName);
                items[6] = new PropertyItem("类ID", "类ID", "数据源", false, int.class, null);
                items[6].setValue(clsID);
                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setDocumentItem(item);
                }
            }
        }

        @Override
        public DocumentItem getDocItem() {
            return this.docItem;
        }

        @Override
        public boolean hasChanged() {
            return this.hasChanged;
        }

        @Override
        public PropertyItem[] getPropertyItems() {

            return items;
        }

        @Override
        public void apply() {
            this.hasChanged = false;
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
            return this.isUpdate;
        }
    }

    /**
     * 栅格 图层的基础属性
     */
    public static class RasBaseProperty extends CommonProperty {
        private DocumentItem item = null;
        private PropertyItem[] items;
        private PropertySheet propertySheet;
        private double minScale;
        private double maxScale;

        public RasBaseProperty() {

        }

        @Override
        public boolean hasChanged() {
            return this.hasChanged;
        }

        @Override
        public DocumentItem getDocItem() {
            return this.item;
        }

        @Override
        public void apply() {
            super.apply();
            if (!this.isUpdate && hasChanged) {
                if (this.item instanceof MapLayer) {
                    ((MapLayer) this.item).setScaleRange(new ScaleRange(minScale, maxScale));
                }
            }
        }

        @Override
        public void setDocItem(DocumentItem item) {
            super.setDocItem(item);
            this.item = item;
            if (this.item instanceof MapLayer) {
                ScaleRange scaleRange = ((MapLayer) this.item).getScaleRange();
                minScale = scaleRange.getMinScale();
                maxScale = scaleRange.getMaxScale();
                PropertyItem[] oldItems = super.getPropertyItems();
                items = new PropertyItem[oldItems.length + 2];
                System.arraycopy(oldItems, 0, items, 0, oldItems.length);
                items[oldItems.length] = new PropertyItem("最小比例尺", "最小比例尺", "图层", true, String.class, ScaleComboBoxPropertyEditor.class);
                items[oldItems.length].setValue(minScale);
                items[oldItems.length].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        minScale = (double) newValue;
                        hasChanged = true;
                        if (isUpdate) {
                            ((MapLayer) item).setScaleRange(new ScaleRange(minScale, maxScale));
                        }
                    }
                });
                items[oldItems.length + 1] = new PropertyItem("最大比例尺", "最大比例尺", "图层", true, String.class, ScaleComboBoxPropertyEditor.class);
                items[oldItems.length + 1].setValue(maxScale);
                items[oldItems.length + 1].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        maxScale = (double) newValue;
                        hasChanged = true;
                        if (isUpdate) {
                            ((MapLayer) item).setScaleRange(new ScaleRange(minScale, maxScale));
                        }
                    }
                });
                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setDocumentItem(item);
                }
            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return this.items;
        }


        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
            }
            return propertySheet;
        }
    }

    /**
     * 栅格数据集 图层的常规属性
     */
    public static class RasCommonProperty extends RasBaseProperty {
        private SRefData sRefData = new SRefData();
        private DocumentItem item = null;
        private PropertyItem[] items;
        private PropertySheet propertySheet;

        @Override
        public void apply() {
            super.apply();
            if (!this.isUpdate && hasChanged) {
                if (item instanceof MapLayer) {
                    ((MapLayer) item).setSrefInfo(this.sRefData);
                }
            }
        }

        @Override
        public void setDocItem(DocumentItem item) {
            super.setDocItem(item);
            this.item = item;
            if (this.item instanceof MapLayer) {
                this.sRefData = ((MapLayer) this.item).getSrefInfo();
                PropertyItem[] oldItems = super.getPropertyItems();
                items = new PropertyItem[oldItems.length + 1];
                System.arraycopy(oldItems, 0, items, 0, oldItems.length);
                items[oldItems.length] = new PropertyItem("参照系", "参照系", "图层", true, String.class,
                        SRefDataPropertyEditor.class);
                items[oldItems.length].setValue(sRefData);
                items[oldItems.length].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof SRefData) {
                            sRefData = (SRefData) newValue;
                            hasChanged = true;
                            if (isUpdate) {
                                items[oldItems.length].setValue(sRefData);
                            }
                        }
                    }
                });
                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setDocumentItem(item);
                }
            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return this.items;
        }

        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
            }
            return propertySheet;
        }
    }

    /**
     * 栅格目录 图层的常规属性
     */
    public static class RasterCatalogCommonProperty extends RasCommonProperty {
        long num = 0;
        private DocumentItem item = null;
        private RectBound rectBound = null;
        private PropertyItem[] items;
        private PropertySheet propertySheet;

        @Override
        public void apply() {
            super.apply();
        }

        @Override
        public void setDocItem(DocumentItem item) {
            super.setDocItem(item);
            this.item = item;
            if (this.item instanceof RasterCatalogLayer) {
                RasterCatalog rasterCatalog = (RasterCatalog) ((RasterCatalogLayer) this.item).getData();
                this.num = rasterCatalog.getItemNum();
                rectBound = new RectBound(((RasterCatalogLayer) this.item).getRange());
                PropertyItem[] oldItems = super.getPropertyItems();
                items = new PropertyItem[oldItems.length + 1];
                System.arraycopy(oldItems, 0, items, 0, oldItems.length);
                items[oldItems.length] = new PropertyItem("数据集数", "数据集数", "常规", false, String.class,
                        SRefDataPropertyEditor.class);//
                items[oldItems.length].setValue(this.num);

                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setDocumentItem(item);
                }
            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return this.items;
        }

        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
            }
            return propertySheet;
        }
    }

    //RasImgDispInfo rasImgInfo = m_RasterLayer.GetRasDispInfo();

    /**
     * 栅格数据集 显示属性
     */
    public static class RasDisplayCommonProperty implements IProperty {
        protected boolean isUpdate = false;//是否立即更新
        protected boolean hasChanged = false;
        private DocumentItem item = null;
        private PropertyItem[] items;
        private PropertySheet propertySheet;

        //基本显示
        private RasterResampling rasterSampling = RasterResampling.NearestNeighbor;
        private long brightness = 0;
        private long contrast = 0;
        private long transparencyCeff = 0;

        //显示模式

        //增强显示
        private ImageEnhannced imageEnhannced;
        private GrayscaleTransform grayscaleTransform;
        private boolean isStdMode = false;//标准差显示
        private boolean isPerSub = false;//百分比截断显示
        private double standardDeviation = 0;//标准差
        private double percentMin = 0;//最小值
        private double percentMax = 0;//最大值
        private double gamma = 0;//Gamma校正

        private RasterColorInterp rasterColorInterp;

        private String String_RasResampleType = "栅格显示重采样";
        private String String_Brightness = "亮度";
        private String String_Contrast = "对比度";
        private String String_TransparencyCeff = "透明度";
        private String String_RasGrayConvertForm = "直方图显示";
        private String String_StandardDeviation = "标准差";
        private String String_PercentMin = "最小值";
        private String String_PercentMax = "最大值";
        private String String_Gamma = "Gamma校正";

        protected SimpleBooleanProperty isUIUpdate;//是否刷新界面控件属性(可见性、可编辑性)

        public RasDisplayCommonProperty() {
            isUIUpdate = new SimpleBooleanProperty(this, "isUIUpdate", true);
            isUIUpdate.addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (propertySheet != null && isUIUpdate.get()) {
                        ArrayList<PropertyItem> newItems = new ArrayList<>();
                        for (int i = 0; i < items.length; i++) {
                            if (items[i].isVisible()) {
                                newItems.add(items[i]);
                            }
                        }
                        if (propertySheet != null && isUIUpdate.get()) {
                            propertySheet.getItems().setAll(newItems);
                        }
                    }
                }
            });
        }

        public void beginUpdate() {
            isUIUpdate.set(false);
        }

        public void endUpdate() {
            isUIUpdate.set(true);
        }

        @Override
        public boolean hasChanged() {
            return this.hasChanged;
        }

        @Override
        public DocumentItem getDocItem() {
            return this.item;
        }

        @Override
        public void apply() {
            if (!this.isUpdate && hasChanged) {
                if (this.item instanceof RasterLayer) {
                    ((RasterLayer) item).setRasterResampleimg(rasterSampling);
                    ((RasterLayer) item).setBrightness(brightness);
                    ((RasterLayer) item).setContrast(contrast);
                    ((RasterLayer) item).setTransparency(transparencyCeff);
                    ((RasterLayer) item).setGamma(gamma);
                    ((RasterLayer) item).setRasImgDispInfo(rasterColorInterp);
                    ((RasterLayer) item).setImageEnhannced(imageEnhannced);
                }
            }
        }

        @Override
        public void setDocItem(DocumentItem item) {
            this.item = item;
            if (this.item instanceof RasterLayer) {

                rasterSampling = ((RasterLayer) this.item).getRasterResampleimg();
                brightness = ((RasterLayer) this.item).getBrightness();
                contrast = ((RasterLayer) this.item).getContrast();
                transparencyCeff = ((RasterLayer) this.item).getTransparency();
                gamma = ((RasterLayer) this.item).getGamma();
                imageEnhannced = ((RasterLayer) this.item).getImageEnhannced();
                if (imageEnhannced != null) {
                    grayscaleTransform = imageEnhannced.getGrayscaleTransform();
                }
                items = new PropertyItem[9];
                items[0] = new PropertyItem(String_RasResampleType, String_RasResampleType, String_Diaplay, true, String.class, ComboBoxPropertyEditor.class);
                items[0].setValue(rasterSampling);
                items[0].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof RasterResampling) {
                            rasterSampling = (RasterResampling) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((RasterLayer) item).setRasterResampleimg(rasterSampling);
                        }
                    }
                });
                items[1] = new PropertyItem(String_Brightness, String_Brightness, String_Diaplay, true, Long.class, null);
                items[1].setValue(brightness);
                items[1].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Long) {
                            brightness = (long) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((RasterLayer) item).setBrightness(brightness);
                        }
                    }
                });
                items[2] = new PropertyItem(String_Contrast, String_Contrast, String_Diaplay, true, Long.class, null);
                items[2].setValue(contrast);
                items[2].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Long) {
                            contrast = (long) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((RasterLayer) item).setContrast(contrast);
                        }
                    }
                });
                items[3] = new PropertyItem(String_TransparencyCeff, String_TransparencyCeff, String_Diaplay, true, Long.class, null);
                items[3].setValue(transparencyCeff);
                items[3].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Long) {
                            transparencyCeff = (long) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((RasterLayer) item).setTransparency(transparencyCeff);
                        }
                    }
                });
                items[4] = new PropertyItem(String_RasGrayConvertForm, String_RasGrayConvertForm, String_Diaplay, true, String.class, ComboBoxPropertyEditor.class);
                items[4].setValue(grayscaleTransform);
                items[4].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof GrayscaleTransform) {
                            grayscaleTransform = (GrayscaleTransform) newValue;
                            isStdMode = grayscaleTransform == GrayscaleTransform.StandardDeviations;
                            isPerSub = grayscaleTransform == GrayscaleTransform.MinMax;
                            items[5].setVisible(isStdMode);
                            items[6].setVisible(isPerSub);
                            items[7].setVisible(isPerSub);
                            if (imageEnhannced != null) {
                                imageEnhannced.setGrayscaleTransform(grayscaleTransform);
                            }
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            if (imageEnhannced != null) {
                                ((RasterLayer) item).setImageEnhannced(imageEnhannced);
                            }
                        }
                    }
                });
                items[5] = new PropertyItem(String_StandardDeviation, String_StandardDeviation, String_Diaplay, true, isStdMode, Double.class, null);
                items[5].setValue(standardDeviation);
                items[5].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Double) {
                            standardDeviation = (double) newValue;
                            if (imageEnhannced != null) {
                                imageEnhannced.setSTDRatio(standardDeviation);
                            }
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            if (imageEnhannced != null) {
                                ((RasterLayer) item).setImageEnhannced(imageEnhannced);
                            }
                        }
                    }
                });
                items[6] = new PropertyItem(String_PercentMin, String_PercentMin, String_Diaplay, true, isPerSub, Double.class, null);
                items[6].setValue(percentMin);
                items[6].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Double) {
                            percentMin = (double) newValue;
                            if (imageEnhannced != null) {
                                imageEnhannced.setPercentMin(percentMin);
                            }
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            if (imageEnhannced != null) {
                                ((RasterLayer) item).setImageEnhannced(imageEnhannced);
                            }
                        }
                    }
                });
                items[7] = new PropertyItem(String_PercentMax, String_PercentMax, String_Diaplay, true, isPerSub, Double.class, null);
                items[7].setValue(percentMax);
                items[7].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Double) {
                            percentMax = (double) newValue;
                            if (imageEnhannced != null) {
                                imageEnhannced.setPercentMax(percentMax);
                            }
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            if (imageEnhannced != null) {
                                ((RasterLayer) item).setImageEnhannced(imageEnhannced);
                            }
                        }
                    }
                });
                items[8] = new PropertyItem(String_Gamma, String_Gamma, String_Diaplay, true, Double.class, null);
                items[8].setValue(gamma);
                items[8].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Double) {
                            gamma = (double) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((RasterLayer) item).setGamma(gamma);
                        }
                    }
                });
                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setDocumentItem(item);
                }
            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return this.items;
        }

        @Override
        public void setImmediatelyUpdate(boolean isUpdate) {
            this.isUpdate = isUpdate;
        }

        @Override
        public boolean isImmediatelyUpdate() {
            return this.isUpdate;
        }

        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
                AddUnitedSetting(items, item, propertySheet, isUIUpdate);
            }
            return propertySheet;
        }
    }

    /**
     * 栅格目录 显示属性
     */
    public static class RcatDisplayCommonProperty implements IProperty {
        protected boolean isUpdate = false;//是否立即更新
        protected boolean hasChanged = false;
        private DocumentItem item = null;
        private PropertyItem[] items;
        private PropertySheet propertySheet;

        //基本显示
        private RasterResampling rasterSampling = RasterResampling.NearestNeighbor;
        private long brightness = 0;
        private long contrast = 0;
        private long transparencyCeff = 0;

        //显示模式

        //增强显示
        private ImageEnhannced imageEnhannced;
        private GrayscaleTransform grayscaleTransform;
        private boolean isStdMode = false;//标准差显示
        private boolean isPerSub = false;//百分比截断显示
        private double standardDeviation = 0;
        private double percentMin = 0;
        private double percentMax = 0;
        private double gamma = 0;

        //图幅显示
        private boolean isFrameDisplay = true;//图幅显示
        private int minItemForFrameDisplay = 9;//数据集个数>=
        private int frameColor = 1;//边框色
        private int fontColor = 1;//注记色
        private int fillColor = 4;//填充色

        private String String_RasResampleType = "栅格显示重采样";
        private String String_Brightness = "亮度";
        private String String_Contrast = "对比度";
        private String String_TransparencyCeff = "透明度";
        private String String_RasGrayConvertForm = "直方图显示";
        private String String_StandardDeviation = "标准差";
        private String String_PercentMin = "最小值";
        private String String_PercentMax = "最大值";
        private String String_Gamma = "Gamma校正";

        protected SimpleBooleanProperty isUIUpdate;//是否刷新界面控件属性(可见性、可编辑性)

        public RcatDisplayCommonProperty() {
            isUIUpdate = new SimpleBooleanProperty(this, "isUIUpdate", true);
            isUIUpdate.addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (propertySheet != null && isUIUpdate.get()) {
                        ArrayList<PropertyItem> newItems = new ArrayList<>();
                        for (int i = 0; i < items.length; i++) {
                            if (items[i].isVisible()) {
                                newItems.add(items[i]);
                            }
                        }
                        if (propertySheet != null && isUIUpdate.get()) {
                            propertySheet.getItems().setAll(newItems);
                        }
                    }
                }
            });
        }

        public void beginUpdate() {
            isUIUpdate.set(false);
        }

        public void endUpdate() {
            isUIUpdate.set(true);
        }

        @Override
        public boolean hasChanged() {
            return this.hasChanged;
        }

        @Override
        public DocumentItem getDocItem() {
            return this.item;
        }

        @Override
        public void apply() {
            if (!this.isUpdate && hasChanged) {
                if (this.item instanceof RasterCatalogLayer) {
                    ((RasterCatalogLayer) item).setRasterResampleimg(rasterSampling);
                    ((RasterCatalogLayer) item).setBrightness(brightness);
                    ((RasterCatalogLayer) item).setContrast(contrast);
                    ((RasterCatalogLayer) item).setTransparency(transparencyCeff);
                    ((RasterCatalogLayer) item).setIsFrameDisplay(isFrameDisplay);
                    ((RasterCatalogLayer) item).setMinItemForFrameDisplay(minItemForFrameDisplay);
                    ((RasterCatalogLayer) item).setFrameColor(frameColor);
                    ((RasterCatalogLayer) item).setFontColor(fontColor);
                    ((RasterCatalogLayer) item).setFillColor(fillColor);
                }
            }
        }

        @Override
        public void setDocItem(DocumentItem item) {
            this.item = item;
            if (this.item instanceof RasterCatalogLayer) {
                rasterSampling = ((RasterCatalogLayer) this.item).getRasterResampleimg();
                brightness = ((RasterCatalogLayer) this.item).getBrightness();
                contrast = ((RasterCatalogLayer) this.item).getContrast();
                transparencyCeff = ((RasterCatalogLayer) this.item).getTransparency();
                isFrameDisplay = ((RasterCatalogLayer) this.item).getIsFrameDisplay();
                minItemForFrameDisplay = (int) ((RasterCatalogLayer) this.item).getMinItemForFrameDisplay();
                frameColor = (int) ((RasterCatalogLayer) this.item).getFrameColor();
                fontColor = (int) ((RasterCatalogLayer) this.item).getFontColor();
                fillColor = (int) ((RasterCatalogLayer) this.item).getFillColor();
//                ((RasterLayer) this.item).get
//                RasImgDispInfo rasImgInfo = ((RasCatalogLayer) this.item).getRasDispInfo();
//                rasEnhanced =((RasterLayer) this.item).getEnHannced();
//                if (rasEnhanced != null){
//
//                }
                items = new PropertyItem[13];
                items[0] = new PropertyItem(String_RasResampleType, String_RasResampleType, String_Diaplay, true, String.class, ComboBoxPropertyEditor.class);
                items[0].setValue(rasterSampling);
                items[0].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof RasterResampling) {
                            rasterSampling = (RasterResampling) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((RasterCatalogLayer) item).setRasterResampleimg(rasterSampling);
                        }
                    }
                });
                items[1] = new PropertyItem(String_Brightness, String_Brightness, String_Diaplay, true, Long.class, null);
                items[1].setValue(brightness);
                items[1].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Long) {
                            brightness = (long) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((RasterCatalogLayer) item).setBrightness(brightness);
                        }
                    }
                });
                items[2] = new PropertyItem(String_Contrast, String_Contrast, String_Diaplay, true, Long.class, null);
                items[2].setValue(contrast);
                items[2].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Long) {
                            contrast = (long) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((RasterCatalogLayer) item).setContrast(contrast);
                        }
                    }
                });
                items[3] = new PropertyItem(String_TransparencyCeff, String_TransparencyCeff, String_Diaplay, true, Long.class, null);
                items[3].setValue(transparencyCeff);
                items[3].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Long) {
                            transparencyCeff = (long) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((RasterCatalogLayer) item).setTransparency(transparencyCeff);
                        }
                    }
                });
                items[4] = new PropertyItem(String_RasGrayConvertForm, String_RasGrayConvertForm, String_Diaplay, true, String.class, ComboBoxPropertyEditor.class);
                items[4].setValue(grayscaleTransform);
                items[4].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof GrayscaleTransform) {
                            grayscaleTransform = (GrayscaleTransform) newValue;
                            isStdMode = grayscaleTransform == GrayscaleTransform.StandardDeviations;
                            isPerSub = grayscaleTransform == GrayscaleTransform.MinMax;
                            items[5].setVisible(isStdMode);
                            items[6].setVisible(isPerSub);
                            items[7].setVisible(isPerSub);
                            if (imageEnhannced != null) {
                                imageEnhannced.setGrayscaleTransform(grayscaleTransform);
                            }
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            if (imageEnhannced != null) {
                                ((RasterCatalogLayer) item).setImageEnhannced(imageEnhannced);
                            }
                        }
                    }
                });
                items[5] = new PropertyItem(String_StandardDeviation, String_StandardDeviation, String_Diaplay, true, isStdMode, Double.class, null);
                items[5].setValue(standardDeviation);
                items[5].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Double) {
                            standardDeviation = (double) newValue;
                            if (imageEnhannced != null) {
                                imageEnhannced.setSTDRatio(standardDeviation);
                            }
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            if (imageEnhannced != null) {
                                ((RasterCatalogLayer) item).setImageEnhannced(imageEnhannced);
                            }
                        }
                    }
                });
                items[6] = new PropertyItem(String_PercentMin, String_PercentMin, String_Diaplay, true, isPerSub, Double.class, null);
                items[6].setValue(percentMin);
                items[6].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Double) {
                            percentMin = (double) newValue;
                            if (imageEnhannced != null) {
                                imageEnhannced.setPercentMin(standardDeviation);
                            }
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            if (imageEnhannced != null) {
                                ((RasterCatalogLayer) item).setImageEnhannced(imageEnhannced);
                            }
                        }
                    }
                });
                items[7] = new PropertyItem(String_PercentMax, String_PercentMax, String_Diaplay, true, isPerSub, Double.class, null);
                items[7].setValue(percentMax);
                items[7].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Double) {
                            percentMax = (double) newValue;
                            if (imageEnhannced != null) {
                                imageEnhannced.setPercentMax(percentMax);
                            }
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            if (imageEnhannced != null) {
                                ((RasterCatalogLayer) item).setImageEnhannced(imageEnhannced);
                            }
                        }
                    }
                });

                items[8] = new PropertyItem("图幅显示", "图幅显示", String_Diaplay, true, Boolean.class, null);
                items[8].setValue(isFrameDisplay);
                items[8].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Boolean) {
                            isFrameDisplay = (boolean) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((RasterCatalogLayer) item).setIsFrameDisplay(isFrameDisplay);
                        }
                    }
                });
                items[9] = new PropertyItem("数据集个数>=", "数据集个数>=", String_Diaplay, isFrameDisplay, Integer.class, null);
                items[9].setValue(minItemForFrameDisplay);
                items[9].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Integer) {
                            minItemForFrameDisplay = (int) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((RasterCatalogLayer) item).setMinItemForFrameDisplay(minItemForFrameDisplay);
                        }
                    }
                });
                items[10] = new PropertyItem("边框色", "边框色", String_Diaplay, isFrameDisplay, Integer.class,
                        MapGISColorPickerEditor.class);
                items[10].setValue(this.frameColor);
                if (items[10].getObservableValue().isPresent()) {
                    items[10].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Integer) {
                                frameColor = (int) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                ((RasterCatalogLayer) item).setFrameColor(frameColor);
                            }
                        }
                    });
                }
                items[11] = new PropertyItem("注记色", "注记色", String_Diaplay, isFrameDisplay, Integer.class,
                        MapGISColorPickerEditor.class);
                items[11].setValue(this.fontColor);
                if (items[11].getObservableValue().isPresent()) {
                    items[11].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Integer) {
                                fontColor = (int) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                ((RasterCatalogLayer) item).setFontColor(fontColor);
                            }
                        }
                    });
                }
                items[12] = new PropertyItem("填充色", "填充色", String_Diaplay, isFrameDisplay, Integer.class,
                        MapGISColorPickerEditor.class);
                items[12].setValue(this.fillColor);
                if (items[12].getObservableValue().isPresent()) {
                    items[12].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Integer) {
                                fillColor = (int) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                ((RasterCatalogLayer) item).setFillColor(fillColor);
                            }
                        }
                    });
                }
                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setDocumentItem(item);
                }
            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return this.items;
        }

        @Override
        public void setImmediatelyUpdate(boolean isUpdate) {
            this.isUpdate = isUpdate;
        }

        @Override
        public boolean isImmediatelyUpdate() {
            return this.isUpdate;
        }

        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
                AddUnitedSetting(items, item, propertySheet, isUIUpdate);
            }
            return propertySheet;
        }
    }
    //endregion

    //region 字段描述
    private static String String_Layer = "图层";
    private static String String_Diaplay = "显示";
    private static String String_DisplayFillingArea = "显示填充区";
    private static String String_MinScaleOfFillingArea = "填充区最小比例尺";
    private static String String_MaxScaleOfFillingArea = "填充区最大比例尺";
    private static String String_SymbolizedDisplayofFilledArea = "填充区符号化显示";
    private static String String_SymbolizedDisplayofFilledAreaMinimumScale = "填充区符号化最大比例尺";
    private static String String_SymbolizedDisplayofFilledAreaMaximumScale = "填充区符号化最小比例尺";
    private static String String_FillAreaScalingWithGraph = "填充区随图缩放";
    private static String String_BenchmarkScaleWithGraph = "随图缩放基准比例尺";
    private static String String_MinimumScaleofFilledAreawithMap = "填充区随图缩放最小比例尺";
    private static String String_MaximumScaleofFilledAreawithMap = "填充区随图缩放最大比例尺";

    private static String String_RestoreDiaplay = "符号化显示";
    private static String String_SymbolizedDisplayMinimumScale = "符号化显示最小比例尺";
    private static String String_SymbolizedDisplayMaximumScale = "符号化显示最大比例尺";
    private static String String_SymbolZoomWithMap = "符号随图缩放";
    private static String String_BenchmarkScaleOfSymbolsWithGraphs = "符号随图缩放基准比例尺";
    private static String String_MinimumScaleOfSymbolsWithGraphs = "符号随图缩放最小比例尺";
    private static String String_MaximumScaleOfSymbolsWithGraphs = "符号随图缩放最大比例尺";
    private static String String_DisplayCoord = "显示坐标点";
    private static String String_ShowGeomRect = "显示图元外包矩形";
    private static String String_Transparency = "透明度";
    private static String String_IsUseSuperLink = "是否使用超链接";
    private static String String_SetSuperLinkFieldName = "设置超链接字段";
    private static String String_SelfPaintDriver = "自绘驱动";
    private static String String_SelfPaintParmsSettings = "自绘参数设置";
    private static String String_ProjParmsSettings = "正向投影参数设置";
    private static String String_ReverseProjParmSettings = "逆向投影参数设置";
    private static String String_FiltrateMode = "过滤方式";
    private static String String_FiltrateCondition = "过滤条件";
    //endregion

    /**
     * 图层的配置属性—显示属性（点、线）
     */
    public static class ShowProperty implements IProperty {
        protected boolean isUpdate = false;//是否立即更新
        protected boolean hasChanged = false;
        private DocumentItem item = null;
        private PropertyItem[] items;
        private PropertySheet propertySheet;
        private com.zondy.mapgis.geometry.GeomType geomType = com.zondy.mapgis.geometry.GeomType.GeomUnknown;

        private boolean isAcls = false;

        //region 字段
        private boolean bSymbolShow = false;
        private double symbolShowMinScale;
        private double symbolShowMaxScale;
        private boolean bFollowZoom = false;
        private double symbolFollowZoomScale;//符号随图缩放基准比例尺
        private double symbolFollowZoomMinScale;
        private double symbolFollowZoomMaxScale;
        private boolean showCoord = false;
        private boolean bShowGeomRect = false;
        private int transParency = 0;
        private boolean isSetSuperLink = false;
        private String setSuperLinkFldName = "";
        //        private RenderInfo renderInfo = null;//自绘驱动暂时不要
        private String projParam;
        private String obProjParam;
        private FilterMode fMode = FilterMode.None;
        //        private String filterCondition = "";
        private QueryDef filterCondition;
        private String filter;
        private Rect rt;//= new Rect();
        //endregion

        protected SimpleBooleanProperty isUIUpdate;//是否刷新界面控件属性(可见性、可编辑性)

        public ShowProperty() {
            isUIUpdate = new SimpleBooleanProperty(this, "isUIUpdate", true);
            isUIUpdate.addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (propertySheet != null && isUIUpdate.get()) {
                        ArrayList<PropertyItem> newItems = new ArrayList<>();
                        for (int i = 0; i < items.length; i++) {
                            if (items[i].isVisible()) {
                                newItems.add(items[i]);
                            }
                        }
                        if (propertySheet != null && isUIUpdate.get()) {
                            propertySheet.getItems().setAll(newItems);
                        }
                    }
                }
            });
        }

        public void beginUpdate() {
            isUIUpdate.set(false);
        }

        public void endUpdate() {
            isUIUpdate.set(true);
        }

        @Override
        public boolean hasChanged() {
            return this.hasChanged;
        }

        @Override
        public void apply() {
            if (!this.isUpdate && hasChanged) {
                if (this.item instanceof VectorLayer) {
                    ((VectorLayer) item).setIsSymbolic(this.bSymbolShow);

                    ((VectorLayer) item).setScaleRangeOfSymbolShow(new ScaleRange(symbolShowMinScale, symbolShowMaxScale));
                    ((VectorLayer) item).setIsFollowZoom(this.bFollowZoom);
                    ((VectorLayer) item).setScaleOfSymbolSize(this.symbolFollowZoomScale);
                    ((VectorLayer) item).setScaleRangeOfSymbolFollowZoom(new ScaleRange(symbolFollowZoomMinScale, symbolFollowZoomMaxScale));
                    ((VectorLayer) item).setIsDipalyCoordinate(this.showCoord);
                    ((VectorLayer) item).setIsDispalyOutsourcingRectangle(this.bShowGeomRect);
                    ((VectorLayer) item).setTransparency(transParency);
                    ((VectorLayer) item).setIsUseSuperLink(isSetSuperLink);
                    ((VectorLayer) item).setSuperLinkFieldName(setSuperLinkFldName);
//                    ((VectorLayer) this.item).setRenderInfo(this.renderInfo);
//                    File file = new File(EnvConfig.getConfigDirectory(SysConfigDirType.Projection), "TransLst.dat");
//                    boolean rtn = ElpTransformation.loadElpTransParam(file.getPath());
//                    ElpTransParam[] paras = ElpTransformation.loadElpTransParam1(file.getPath());
//                    if (paras.length > 0) {
//                        ElpTransParam directParam = null;
//                        ElpTransParam reverseParam = null;
//                        int directIndex = 0;
//                        int reverseIndex = 0;
//                        int count = ElpTransformation.getElpTransParamCount();
//                        for (int i = 0; i < count; i++) {
//                            ElpTransParam elpTransParam = ElpTransformation.getElpTransParam(i);
//                            if (elpTransParam.getTransName() == projParam) {
//                                directParam = elpTransParam;
//                                directIndex = i;
//                            }
//                            if (elpTransParam.getTransName() == obProjParam) {
//                                reverseParam = elpTransParam;
//                                reverseIndex = i;
//                            }
//                            if (directParam != null && reverseParam != null) {
//                                break;
//                            }
//                        }
//                        if (directParam == null) {
//                            directParam = new ElpTransParam();
//                            directParam.setTransName("");
//                            directParam.setEquationX("");
//                            directParam.setEquationY("");
//                            directParam.setEquationZ("");
//                        }
//                        if (reverseParam == null) {
//                            reverseParam = new ElpTransParam();
//                            reverseParam.setTransName("");
//                            reverseParam.setEquationX("");
//                            reverseParam.setEquationY("");
//                            reverseParam.setEquationZ("");
//                        }
//                        ((VectorLayer) this.item).setSRSIndex(directIndex, reverseIndex);
//                        ElpTransformation.saveElpTransParam(file.getPath());
//                    }
                    ((VectorLayer) this.item).setQueryDef(filterCondition);
                }
            }
        }

        @Override
        public DocumentItem getDocItem() {
            return this.item;
        }

        @Override
        public void setDocItem(DocumentItem item) {
            this.item = item;
            if (this.item != null && this.item instanceof VectorLayer) {
                this.geomType = ((VectorLayer) this.item).getGeometryType();
                isAcls = this.geomType == GeomType.GeomAnn;
                this.bSymbolShow = ((VectorLayer) this.item).getIsSymbolic();
                ScaleRange scaleRange = ((VectorLayer) this.item).getScaleRangeOfSymbolShow();
                symbolShowMinScale = scaleRange.getMinScale();
                symbolShowMaxScale = scaleRange.getMaxScale();
                this.bFollowZoom = ((VectorLayer) this.item).getIsFollowZoom();
                this.symbolFollowZoomScale = ((VectorLayer) this.item).getScaleOfSymbolSize();
                ScaleRange scaleRange1 = ((VectorLayer) this.item).getScaleRangeOfRegBorderFollowZoom();
                this.symbolFollowZoomMinScale = scaleRange1.getMinScale();
                this.symbolFollowZoomMaxScale = scaleRange1.getMaxScale();
                this.showCoord = ((VectorLayer) this.item).getIsDipalyCoordinate();
                this.bShowGeomRect = ((VectorLayer) this.item).getIsDispalyOutsourcingRectangle();
                isSetSuperLink = ((VectorLayer) this.item).getIsUseSuperLink();
                setSuperLinkFldName = ((VectorLayer) this.item).getSuperLinkFieldName();
                this.transParency =  (int)((VectorLayer) this.item).getTransparency();

//                File file = new File(EnvConfig.getConfigDirectory(SysConfigDirType.Projection), "TransLst.dat");
//                boolean rtn = ElpTransformation.loadElpTransParam(file.getPath());
//                if (rtn) {
//
//                    ElpTransParam directParam = new ElpTransParam();
//                    ElpTransParam reverseParam = new ElpTransParam();
//                    int directIndex = 0;
//                    int reverseIndex = 0;
//                    rtn = ((VectorLayer) this.item).getSRSIndex(directIndex, reverseIndex);
//                    if (rtn) {
//                        directParam = ElpTransformation.getElpTransParam(directIndex);
//                        reverseParam = ElpTransformation.getElpTransParam(reverseIndex);
//                        if (directParam.getTransName() == "无") {
//                            projParam = "无";
//                        } else {
//                            projParam = directParam.getTransName();
//                        }
//                        if (reverseParam.getTransName() == "无") {
//                            obProjParam = "无";
//                        } else {
//                            obProjParam = reverseParam.getTransName();
//                        }
//                    }
//                }
                filterCondition = ((VectorLayer) this.item).getQueryDef();
                if (filterCondition != null) {
                    filter = filterCondition.getFilter();
                    filterCondition.getRect(rt);
                    if (filter != null && !filter.isEmpty())//属性过滤
                    {
                        this.fMode = FilterMode.AttFilter;
                    } else if (rt != null)//范围过滤
                    {
                        this.fMode = FilterMode.RectFilter;
                    } else//空过滤
                    {
                        this.fMode = FilterMode.None;
                    }
                }

                items = new PropertyItem[15];
                items[0] = new PropertyItem(String_RestoreDiaplay, String_RestoreDiaplay, String_Diaplay, true, !isAcls, Boolean.class, null);
                items[0].setValue(bSymbolShow);
                items[0].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        bSymbolShow = (boolean) newValue;
                        items[1].setEditable(bSymbolShow);
                        items[2].setEditable(bSymbolShow);
                        hasChanged = true;
                        if (isUpdate) {
                            ((VectorLayer) item).setIsSymbolic(bSymbolShow);
                        }
                    }
                });
                items[1] = new PropertyItem(String_SymbolizedDisplayMinimumScale, String_SymbolizedDisplayMinimumScale, String_Diaplay, this.bSymbolShow, !isAcls, String.class, ScaleComboBoxPropertyEditor.class);
                items[1].setValue(symbolShowMinScale);
                items[1].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        symbolShowMinScale = (double) newValue;
                        hasChanged = true;
                        if (isUpdate) {
                            ((VectorLayer) item).setScaleRangeOfSymbolShow(new ScaleRange(symbolShowMinScale, symbolShowMaxScale));
                        }
                    }
                });
                items[2] = new PropertyItem(String_SymbolizedDisplayMaximumScale, String_SymbolizedDisplayMaximumScale, String_Diaplay, this.bSymbolShow, !isAcls, String.class, ScaleComboBoxPropertyEditor.class);
                items[2].setValue(symbolShowMaxScale);
                items[2].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        symbolShowMaxScale = (double) newValue;
                        hasChanged = true;
                        if (isUpdate) {
                            ((VectorLayer) item).setScaleRangeOfSymbolShow(new ScaleRange(symbolShowMinScale, symbolShowMaxScale));
                        }
                    }
                });
                items[3] = new PropertyItem(String_SymbolZoomWithMap, String_SymbolZoomWithMap, String_Diaplay, true, Boolean.class, null);
                items[3].setValue(bFollowZoom);
                items[3].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        bFollowZoom = (boolean) newValue;
                        items[4].setEditable(bFollowZoom);
                        items[5].setEditable(bFollowZoom);
                        items[6].setEditable(bFollowZoom);
                        hasChanged = true;
                        if (isUpdate) {
                            ((VectorLayer) item).setIsFollowZoom(bFollowZoom);
                        }
                    }
                });
                items[4] = new PropertyItem(String_BenchmarkScaleOfSymbolsWithGraphs, String_BenchmarkScaleOfSymbolsWithGraphs, String_Diaplay, bFollowZoom, String.class, ScaleComboBoxPropertyEditor.class);
                items[4].setValue(symbolFollowZoomScale);
                items[4].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        symbolFollowZoomScale = (double) newValue;
                        hasChanged = true;
                        if (isUpdate) {
                            ((VectorLayer) item).setScaleOfSymbolSize(symbolFollowZoomScale);
                        }
                    }
                });
                items[5] = new PropertyItem(String_MinimumScaleOfSymbolsWithGraphs, String_MinimumScaleOfSymbolsWithGraphs, String_Diaplay, bFollowZoom, String.class, ScaleComboBoxPropertyEditor.class);
                items[5].setValue(symbolFollowZoomMinScale);
                items[5].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        symbolFollowZoomMinScale = (double) newValue;
                        hasChanged = true;
                        if (isUpdate) {
                            ((VectorLayer) item).setScaleRangeOfSymbolFollowZoom(new ScaleRange(symbolFollowZoomMinScale, symbolFollowZoomMaxScale));
                        }
                    }
                });
                items[6] = new PropertyItem(String_MaximumScaleOfSymbolsWithGraphs, String_MaximumScaleOfSymbolsWithGraphs, String_Diaplay, bFollowZoom, String.class, ScaleComboBoxPropertyEditor.class);
                items[6].setValue(symbolFollowZoomMaxScale);
                items[6].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        symbolFollowZoomMaxScale = (double) newValue;
                        hasChanged = true;
                        if (isUpdate) {
                            ((VectorLayer) item).setScaleRangeOfSymbolFollowZoom(new ScaleRange(symbolFollowZoomMinScale, symbolFollowZoomMaxScale));
                        }
                    }
                });
                items[7] = new PropertyItem(String_DisplayCoord, String_DisplayCoord, String_Diaplay, true, Boolean.class, null);
                items[7].setValue(showCoord);
                items[7].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        showCoord = (boolean) newValue;
                        hasChanged = true;
                        if (isUpdate) {
                            ((VectorLayer) item).setIsDipalyCoordinate(showCoord);
                        }
                    }
                });
                items[8] = new PropertyItem(String_ShowGeomRect, String_ShowGeomRect, String_Diaplay, true, Boolean.class, null);
                items[8].setValue(bShowGeomRect);
                items[8].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        bShowGeomRect = (boolean) newValue;
                        hasChanged = true;
                        if (isUpdate) {
                            ((VectorLayer) item).setIsDispalyOutsourcingRectangle(bShowGeomRect);
                        }
                    }
                });
                items[9] = new PropertyItem(String_Transparency, String_Transparency, String_Diaplay, true, Integer.class, null);
                items[9].setValue((int) transParency);
                items[9].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        transParency = (int) newValue;
                        hasChanged = true;
                        if (isUpdate) {
                            ((VectorLayer) item).setTransparency(transParency);
                        }
                    }
                });
                items[10] = new PropertyItem(String_IsUseSuperLink, String_IsUseSuperLink, String_Diaplay, true, Boolean.class, null);
                items[10].setValue(this.isSetSuperLink);
                items[10].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        isSetSuperLink = (boolean) newValue;
                        hasChanged = true;
                        if (isUpdate) {
                            ((VectorLayer) item).setIsUseSuperLink(isSetSuperLink);
                        }
                    }
                });
                items[11] = new PropertyItem(String_SetSuperLinkFieldName, String_SetSuperLinkFieldName, String_Diaplay, this.isSetSuperLink, String.class, null);
                items[11].setValue(this.setSuperLinkFldName);
                items[11].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        setSuperLinkFldName = (String) newValue;
                        hasChanged = true;
                        if (isUpdate) {
                            ((VectorLayer) item).setSuperLinkFieldName(setSuperLinkFldName);
                        }
                    }
                });
//                items[12] = new PropertyItem(String_SelfPaintDriver, String_SelfPaintDriver, String_Diaplay, true, String.class, null);
//                items[12].setValue(renderInfo);
//                items[12].getObservableValue().get().addListener(new ChangeListener<Object>() {
//                    @Override
//                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
//                        renderInfo = (RenderInfo) newValue;
//                        hasChanged = true;
//                        if (isUpdate) {
//                            ((VectorLayer) item).setRenderInfo(renderInfo);
//                        }
//                    }
//                });
//                items[12] = new PropertyItem(String_ProjParmsSettings, String_ProjParmsSettings, String_Diaplay, true, false,String.class, null);
//                items[12].setValue(this.projParam);
//                items[12].getObservableValue().get().addListener(new ChangeListener<Object>() {
//                    @Override
//                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
//                        projParam = (String) newValue;
//                        hasChanged = true;
//                        if (isUpdate) {
//
//                        }
//                    }
//                });
//                items[13] = new PropertyItem(String_ReverseProjParmSettings, String_ReverseProjParmSettings, String_Diaplay, true,false, String.class, null);
//                items[13].setValue(this.obProjParam);
//                items[13].getObservableValue().get().addListener(new ChangeListener<Object>() {
//                    @Override
//                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
//                        obProjParam = (String) newValue;
//                        hasChanged = true;
//                        if (isUpdate) {
//                        }
//                    }
//                });
                items[12] = new PropertyItem(String_FiltrateMode, String_FiltrateMode, String_Diaplay, true, String.class, ComboBoxPropertyEditor.class);
                items[12].setValue(this.fMode);
                items[12].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (fMode instanceof FilterMode) {
                            fMode = (FilterMode) newValue;
                            items[13].setVisible(fMode == FilterMode.AttFilter);
                            items[14].setVisible(fMode == FilterMode.RectFilter);
                            hasChanged = true;
                        }
                    }
                });

                items[13] = new PropertyItem(String_FiltrateCondition, String_FiltrateCondition, String_Diaplay, true, fMode == FilterMode.AttFilter, String.class, SQLQueryEditor.class);
                items[13].setValue(filter != null ? filter : "");
                items[13].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof String) {
                            filter = (String) newValue;
                            if (filterCondition == null) {
                                filterCondition = new QueryDef();
                            }
                            if (filterCondition != null) {
                                filterCondition.setFilter(filter);
                            }
                            hasChanged = true;
                        }
                        if (hasChanged && isUpdate) {
                            ((VectorLayer) item).setQueryDef(filterCondition);
                        }
                    }
                });
                RectBound1 rectBound1 = new RectBound1(rt);
                items[14] = new PropertyItem("范围过滤", "范围过滤", String_Diaplay, true, fMode == FilterMode.RectFilter, String.class, PopupPropertyEditor.class);
                items[14].setValue(rectBound1);
                items[14].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof RectBound1) {
                            rt =new Rect(((RectBound1) newValue).getXMin(),
                                    ((RectBound1) newValue).getYMin(),
                                    ((RectBound1) newValue).getXMax(),
                                    ((RectBound1) newValue).getYMax());
                            if (filterCondition == null) {
                                filterCondition = new QueryDef();
                            }
                            if (filterCondition != null) {
                                filterCondition.setRect(rt, SpaQueryMode.ModeMBRIntersect);
                            }
                            hasChanged = true;
                        }
                        if (hasChanged && isUpdate) {
                            ((VectorLayer) item).setQueryDef(filterCondition);
                        }
                    }
                });

            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return this.items;
        }

        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
                AddUnitedSetting(items, item, propertySheet, isUIUpdate);
            }
            return propertySheet;
        }

        @Override
        public void setImmediatelyUpdate(boolean isUpdate) {
            this.isUpdate = isUpdate;
        }

        @Override
        public boolean isImmediatelyUpdate() {
            return this.isUpdate;
        }

    }

    /**
     * 图层的配置属性—显示属性（区）RegShowProperty
     */
    public static class RegShowProperty implements IProperty {
        protected boolean isUpdate = false;//是否立即更新
        protected boolean hasChanged = false;
        private DocumentItem item = null;
        private PropertyItem[] items;
        private PropertySheet propertySheet;
        private com.zondy.mapgis.geometry.GeomType geomType = com.zondy.mapgis.geometry.GeomType.GeomUnknown;

        //region 字段
        private boolean fillReg = false;
        private double fillRegMinScale;
        private double fillRegMaxScale;

        private boolean bSymbolShow = false;
        private double symbolShowMinScale;
        private double symbolShowMaxScale;
        private boolean bFollowZoom = false;
        private double symbolFollowZoomScale;//填充区随图缩放基准比例尺
        private double symbolFollowZoomMinScale;
        private double symbolFollowZoomMaxScale;

        private boolean regBorder = false;//显示边线
        private LinInfo linInfo;
        private double regBorderMinScale;
        private double regBorderMaxScale;

        private boolean regBorderSymbolShow = false;
        private double regBorderSymbolShowMinScale;
        private double regBorderSymbolShowMaxScale;
        private boolean regBorderFollowZoom = false;
        private double regBorderFollowZoomScale;//边线随图缩放基准比例尺
        private double regBorderFollowZoomMinScale;
        private double regBorderFollowZoomMaxScale;

        private boolean showCoord = false;
        private boolean bShowGeomRect = false;
        private int transParency = 0;//透明度
        private boolean isSetSuperLink = false;
        private String setSuperLinkFldName = "";
        //        private RenderInfo renderInfo = null;//暂时不要
        private String projParam;
        private String obProjParam;
        private FilterMode fMode = FilterMode.None;
        //        private String filterCondition = "";
        private QueryDef filterCondition;
        private String filter;
        private Rect rt;//new Rect();
        //endregion

        protected SimpleBooleanProperty isUIUpdate;//是否刷新界面控件属性(可见性、可编辑性)

        public RegShowProperty() {
            isUIUpdate = new SimpleBooleanProperty(this, "isUIUpdate", true);
            isUIUpdate.addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (propertySheet != null && isUIUpdate.get()) {
                        ArrayList<PropertyItem> newItems = new ArrayList<>();
                        for (int i = 0; i < items.length; i++) {
                            if (items[i].isVisible()) {
                                newItems.add(items[i]);
                            }
                        }
                        if (propertySheet != null && isUIUpdate.get()) {
                            propertySheet.getItems().setAll(newItems);
                        }
                    }
                }
            });
        }

        public void beginUpdate() {
            isUIUpdate.set(false);
        }

        public void endUpdate() {
            isUIUpdate.set(true);
        }

        @Override
        public boolean hasChanged() {
            return this.hasChanged;
        }

        @Override
        public void apply() {
            if (!this.isUpdate && hasChanged) {
                if (this.item instanceof VectorLayer) {
                    ((VectorLayer) item).setIsFillRegion(this.fillReg);
                    ((VectorLayer) item).setScaleRangeOfFillReg(new ScaleRange(this.fillRegMinScale, this.fillRegMaxScale));
                    ((VectorLayer) item).setIsSymbolic(this.bSymbolShow);
                    ((VectorLayer) item).setScaleRangeOfSymbolShow(new ScaleRange(symbolShowMinScale, symbolShowMaxScale));
                    ((VectorLayer) item).setIsFollowZoom(this.bFollowZoom);
                    ((VectorLayer) item).setScaleOfSymbolSize(this.symbolFollowZoomScale);
                    ((VectorLayer) item).setScaleRangeOfSymbolFollowZoom(new ScaleRange(symbolFollowZoomMinScale, symbolFollowZoomMaxScale));
                    ((VectorLayer) item).setIsDisplayRegionBorder(this.regBorder);
                    if (linInfo != null) {
                        ((VectorLayer) item).setRegionBorderLinInfo(linInfo);
                    }
                    ((VectorLayer) item).setScaleRangeOfShowRegBorder(new ScaleRange(this.regBorderMinScale, this.regBorderMaxScale));
                    ((VectorLayer) item).setIsSymbolicRegionBorder(this.regBorderSymbolShow);
                    ((VectorLayer) item).setScaleRangeOfRegBorderSymbolShow(new ScaleRange(this.regBorderSymbolShowMinScale, this.regBorderSymbolShowMaxScale));
                    ((VectorLayer) item).setIsRegionBorderFollowZoom(this.regBorderFollowZoom);
                    ((VectorLayer) item).setScaleRangeOfRegBorderFollowZoom(new ScaleRange(this.regBorderFollowZoomMinScale, this.regBorderFollowZoomMaxScale));
                    ((VectorLayer) item).setIsDipalyCoordinate(this.showCoord);
                    ((VectorLayer) item).setIsDispalyOutsourcingRectangle(this.bShowGeomRect);
                    ((VectorLayer) item).setTransparency(transParency);
                    ((VectorLayer) item).setIsUseSuperLink(isSetSuperLink);
                    ((VectorLayer) item).setSuperLinkFieldName(setSuperLinkFldName);
//                    ((VectorLayer) this.item).setRenderInfo(this.renderInfo);
//                    File file = new File(EnvConfig.getConfigDirectory(SysConfigDirType.Projection), "TransLst.dat");
//                    boolean rtn = ElpTransformation.loadElpTransParam(file.getPath());
//                    ElpTransParam[] paras = ElpTransformation.loadElpTransParam1(file.getPath());
//                    if (paras.length > 0) {
//                        ElpTransParam directParam = null;
//                        ElpTransParam reverseParam = null;
//                        int directIndex = 0;
//                        int reverseIndex = 0;
//                        int count = ElpTransformation.getElpTransParamCount();
//                        for (int i = 0; i < count; i++) {
//                            ElpTransParam elpTransParam = ElpTransformation.getElpTransParam(i);
//                            if (elpTransParam.getTransName() == projParam) {
//                                directParam = elpTransParam;
//                                directIndex = i;
//                            }
//                            if (elpTransParam.getTransName() == obProjParam) {
//                                reverseParam = elpTransParam;
//                                reverseIndex = i;
//                            }
//                            if (directParam != null && reverseParam != null) {
//                                break;
//                            }
//                        }
//                        if (directParam == null) {
//                            directParam = new ElpTransParam();
//                            directParam.setTransName("");
//                            directParam.setEquationX("");
//                            directParam.setEquationY("");
//                            directParam.setEquationZ("");
//                        }
//                        if (reverseParam == null) {
//                            reverseParam = new ElpTransParam();
//                            reverseParam.setTransName("");
//                            reverseParam.setEquationX("");
//                            reverseParam.setEquationY("");
//                            reverseParam.setEquationZ("");
//                        }
//                        ((VectorLayer) this.item).setSRSIndex(directIndex, reverseIndex);
//                        ElpTransformation.saveElpTransParam(file.getPath());
//                    }
                    ((VectorLayer) this.item).setQueryDef(filterCondition);

                }
            }
        }

        @Override
        public DocumentItem getDocItem() {
            return this.item;
        }

        @Override
        public void setDocItem(DocumentItem item) {
            this.item = item;
            if (this.item != null && this.item instanceof VectorLayer) {
                this.geomType = ((VectorLayer) this.item).getGeometryType();
                this.fillReg = ((VectorLayer) this.item).getIsFillRegion();
                ScaleRange scaleRange = ((VectorLayer) this.item).getScaleRangeOfFillReg();
                fillRegMinScale = scaleRange.getMinScale();
                fillRegMaxScale = scaleRange.getMaxScale();
                this.bSymbolShow = ((VectorLayer) this.item).getIsSymbolic();
                ScaleRange scaleRange1 = ((VectorLayer) this.item).getScaleRangeOfSymbolShow();
                symbolShowMinScale = scaleRange1.getMinScale();
                symbolShowMaxScale = scaleRange1.getMaxScale();
                this.bFollowZoom = ((VectorLayer) this.item).getIsFollowZoom();
                ScaleRange scaleRange2 = ((VectorLayer) this.item).getScaleRangeOfSymbolFollowZoom();
                symbolFollowZoomMinScale = scaleRange2.getMinScale();
                symbolFollowZoomMaxScale = scaleRange2.getMaxScale();

                this.symbolFollowZoomScale = ((VectorLayer) this.item).getScaleOfSymbolSize();
                this.regBorder = ((VectorLayer) item).getIsDisplayRegionBorder();
                ScaleRange scaleRange3 = ((VectorLayer) this.item).getScaleRangeOfShowRegBorder();
                regBorderMinScale = scaleRange3.getMinScale();
                regBorderMaxScale = scaleRange3.getMaxScale();

                this.regBorderSymbolShow = ((VectorLayer) item).getIsSymbolicRegionBorder();
                ScaleRange scaleRange4 = ((VectorLayer) this.item).getScaleRangeOfRegBorderSymbolShow();
                regBorderSymbolShowMinScale = scaleRange4.getMinScale();
                regBorderSymbolShowMaxScale = scaleRange4.getMaxScale();

                this.regBorderFollowZoom = ((VectorLayer) item).getIsRegionBorderFollowZoom();
                ScaleRange scaleRange5 = ((VectorLayer) this.item).getScaleRangeOfRegBorderFollowZoom();
                regBorderFollowZoomMinScale = scaleRange5.getMinScale();
                regBorderFollowZoomMaxScale = scaleRange5.getMaxScale();

                this.showCoord = ((VectorLayer) this.item).getIsDipalyCoordinate();
                this.bShowGeomRect = ((VectorLayer) this.item).getIsDispalyOutsourcingRectangle();
                this.transParency =  (int)((VectorLayer) this.item).getTransparency();
                isSetSuperLink = ((VectorLayer) this.item).getIsUseSuperLink();
                setSuperLinkFldName = ((VectorLayer) this.item).getSuperLinkFieldName();

//                File file = new File(EnvConfig.getConfigDirectory(SysConfigDirType.Projection), "TransLst.dat");
//                boolean rtn = ElpTransformation.loadElpTransParam(file.getPath());
//                if (rtn) {
//
//                    ElpTransParam directParam = new ElpTransParam();
//                    ElpTransParam reverseParam = new ElpTransParam();
//                    int directIndex = 0;
//                    int reverseIndex = 0;
//                    rtn = ((VectorLayer) this.item).getSRSIndex(directIndex, reverseIndex);
//                    if (rtn) {
//                        directParam = ElpTransformation.getElpTransParam(directIndex);
//                        reverseParam = ElpTransformation.getElpTransParam(reverseIndex);
//                        if (directParam.getTransName() == "无") {
//                            projParam = "无";
//                        } else {
//                            projParam = directParam.getTransName();
//                        }
//                        if (reverseParam.getTransName() == "无") {
//                            obProjParam = "无";
//                        } else {
//                            obProjParam = reverseParam.getTransName();
//                        }
//                    }
//                }
                filterCondition = ((VectorLayer) this.item).getQueryDef();
                if (filterCondition != null) {
                    filter = filterCondition.getFilter();
                    filterCondition.getRect(rt);//jni挂
                    if (filter != null && !filter.isEmpty())//属性过滤
                    {
                        this.fMode = FilterMode.AttFilter;
                    } else if (rt != null)//范围过滤
                    {
                        this.fMode = FilterMode.RectFilter;
                    } else//空过滤
                    {
                        this.fMode = FilterMode.None;
                    }
                }

                ArrayList tmpLst = new ArrayList();
                items = new PropertyItem[28];

                items[0] = new PropertyItem("显示填充区", "显示填充区", "显示", true, Boolean.class, null);
                items[0].setValue(this.fillReg);
                if (items[0].getObservableValue().isPresent()) {
                    items[0].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Boolean) {
                                fillReg = (boolean) newValue;
                                hasChanged = true;
                                beginUpdate();
                                items[1].setEditable(fillReg);
                                items[2].setEditable(fillReg);
                                items[3].setEditable(fillReg);
                                items[4].setEditable(fillReg && bSymbolShow);
                                items[5].setEditable(fillReg && bSymbolShow);
                                items[6].setEditable(fillReg);
                                items[7].setEditable(fillReg && bFollowZoom);
                                items[8].setEditable(fillReg && bFollowZoom);
                                items[9].setEditable(fillReg && bFollowZoom);
                                endUpdate();
                            }
                            if (isUpdate && hasChanged) {
                                ((VectorLayer) item).setIsFillRegion(fillReg);
                            }
                        }
                    });
                }
                items[1] = new PropertyItem("填充区最小比例尺", "填充区最小比例尺", String_Diaplay, this.fillReg, String.class, ScaleComboBoxPropertyEditor.class);
                items[1].setValue(fillRegMinScale);
                items[1].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Boolean) {
                            fillRegMinScale = (double) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((VectorLayer) item).setScaleRangeOfFillReg(new ScaleRange(fillRegMinScale, fillRegMaxScale));
                        }
                    }
                });
                items[2] = new PropertyItem("填充区最大比例尺", "填充区最大比例尺", String_Diaplay, this.fillReg, String.class, ScaleComboBoxPropertyEditor.class);
                items[2].setValue(fillRegMaxScale);
                items[2].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Boolean) {
                            fillRegMaxScale = (double) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((VectorLayer) item).setScaleRangeOfFillReg(new ScaleRange(fillRegMinScale, fillRegMaxScale));
                        }
                    }
                });
                items[3] = new PropertyItem("填充区符号化显示", "填充区符号化显示", String_Diaplay, fillReg, Boolean.class, null);
                items[3].setValue(bSymbolShow);
                items[3].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        bSymbolShow = (boolean) newValue;
                        items[4].setEditable(fillReg && bSymbolShow);
                        items[5].setEditable(fillReg && bSymbolShow);
                        hasChanged = true;
                        if (isUpdate) {
                            ((VectorLayer) item).setIsSymbolic(bSymbolShow);
                        }
                    }
                });
                items[4] = new PropertyItem("填充区符号化最小比例尺", "填充区符号化最小比例尺", String_Diaplay, fillReg && bSymbolShow, String.class, ScaleComboBoxPropertyEditor.class);
                items[4].setValue(symbolShowMinScale);
                items[4].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Boolean) {
                            symbolShowMinScale = (double) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((VectorLayer) item).setScaleRangeOfSymbolShow(new ScaleRange(symbolShowMinScale, symbolShowMaxScale));
                        }
                    }
                });
                items[5] = new PropertyItem("填充区符号化最大比例尺", "填充区符号化最大比例尺", String_Diaplay, fillReg && bSymbolShow, String.class, ScaleComboBoxPropertyEditor.class);
                items[5].setValue(symbolShowMaxScale);
                items[5].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Boolean) {
                            symbolShowMaxScale = (double) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((VectorLayer) item).setScaleRangeOfSymbolShow(new ScaleRange(symbolShowMinScale, symbolShowMaxScale));
                        }
                    }
                });
                items[6] = new PropertyItem("填充区随图缩放", "填充区随图缩放", String_Diaplay, fillReg, Boolean.class, null);
                items[6].setValue(bFollowZoom);
                items[6].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        bFollowZoom = (boolean) newValue;
                        items[7].setEditable(fillReg && bFollowZoom);
                        items[8].setEditable(fillReg && bFollowZoom);
                        items[9].setEditable(fillReg && bFollowZoom);
                        hasChanged = true;
                        if (isUpdate) {
                            ((VectorLayer) item).setIsFollowZoom(bFollowZoom);
                        }
                    }
                });
                items[7] = new PropertyItem("填充区随图缩放基准比例尺", "填充区随图缩放基准比例尺", String_Diaplay, fillReg && bFollowZoom, String.class, ScaleComboBoxPropertyEditor.class);
                items[7].setValue(symbolFollowZoomScale);
                items[7].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        symbolFollowZoomScale = (double) newValue;
                        hasChanged = true;
                        if (isUpdate) {
                            ((VectorLayer) item).setScaleOfSymbolSize(symbolFollowZoomScale);
                        }
                    }
                });
                items[8] = new PropertyItem("填充区随图缩放最小比例尺", "填充区随图缩放最小比例尺", String_Diaplay, fillReg && bFollowZoom, String.class, ScaleComboBoxPropertyEditor.class);
                items[8].setValue(symbolFollowZoomMinScale);
                items[8].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        symbolFollowZoomMinScale = (double) newValue;
                        hasChanged = true;
                        if (isUpdate) {
                            ((VectorLayer) item).setScaleRangeOfSymbolFollowZoom(new ScaleRange(symbolFollowZoomMinScale, symbolFollowZoomMaxScale));
                        }
                    }
                });
                items[9] = new PropertyItem("填充区随图缩放最大比例尺", "填充区随图缩放最大比例尺", String_Diaplay, fillReg && bFollowZoom, String.class, ScaleComboBoxPropertyEditor.class);
                items[9].setValue(symbolFollowZoomMaxScale);
                items[9].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        symbolFollowZoomMaxScale = (double) newValue;
                        hasChanged = true;
                        if (isUpdate) {
                            ((VectorLayer) item).setScaleRangeOfSymbolFollowZoom(new ScaleRange(symbolFollowZoomMinScale, symbolFollowZoomMaxScale));
                        }
                    }
                });
                items[10] = new PropertyItem("显示边线", "显示填充区", "显示", true, Boolean.class, null);
                items[10].setValue(this.regBorder);
                if (items[10].getObservableValue().isPresent()) {
                    items[10].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Boolean) {
                                regBorder = (boolean) newValue;
                                hasChanged = true;
                                beginUpdate();
                                items[11].setEditable(regBorder);
                                items[12].setEditable(regBorder);
                                items[13].setEditable(regBorder);
                                items[14].setEditable(regBorder);
                                items[15].setEditable(regBorder && regBorderSymbolShow);
                                items[16].setEditable(regBorder && regBorderSymbolShow);
                                items[17].setEditable(regBorder);
                                items[18].setEditable(regBorder && regBorderFollowZoom);
                                items[19].setEditable(regBorder && regBorderFollowZoom);
                                endUpdate();

                            }
                            if (isUpdate && hasChanged) {
                                ((VectorLayer) item).setIsDisplayRegionBorder(regBorder);
                            }
                        }
                    });
                }
                items[11] = new PropertyItem("边线线型", "边线线型", "显示", regBorder, String.class, PopupPropertyGeomInfoEditor.class);
                items[11].setValue(linInfo);
                if (items[11].getObservableValue().isPresent()) {
                    items[11].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof LinInfo) {
                                linInfo = (LinInfo) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                items[11].setValue(linInfo);
                                ((VectorLayer) item).setRegionBorderLinInfo(linInfo);
                            }
                        }
                    });
                }
                items[12] = new PropertyItem("边线最小比例尺", "边线最小比例尺", String_Diaplay, this.regBorder, String.class, ScaleComboBoxPropertyEditor.class);
                items[12].setValue(regBorderMinScale);
                items[12].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Boolean) {
                            regBorderMinScale = (double) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((VectorLayer) item).setScaleRangeOfShowRegBorder(new ScaleRange(regBorderMinScale, regBorderMaxScale));
                        }
                    }
                });
                items[13] = new PropertyItem("边线最大比例尺", "边线最大比例尺", String_Diaplay, this.regBorder, String.class, ScaleComboBoxPropertyEditor.class);
                items[13].setValue(regBorderMaxScale);
                items[13].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Boolean) {
                            regBorderMaxScale = (double) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((VectorLayer) item).setScaleRangeOfShowRegBorder(new ScaleRange(regBorderMinScale, regBorderMaxScale));
                        }
                    }
                });
                items[14] = new PropertyItem("边线符号化显示", "边线符号化显示", String_Diaplay, regBorder, Boolean.class, null);
                items[14].setValue(regBorderSymbolShow);
                items[14].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        regBorderSymbolShow = (boolean) newValue;
                        items[15].setEditable(regBorder && regBorderSymbolShow);
                        items[16].setEditable(regBorder && regBorderSymbolShow);
                        hasChanged = true;
                        if (isUpdate) {
                            ((VectorLayer) item).setIsSymbolicRegionBorder(regBorderSymbolShow);
                        }
                    }
                });
                items[15] = new PropertyItem("边线符号化最小比例尺", "边线符号化最小比例尺", String_Diaplay, regBorder && regBorderSymbolShow, String.class, ScaleComboBoxPropertyEditor.class);
                items[15].setValue(regBorderSymbolShowMinScale);
                items[15].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Boolean) {
                            regBorderSymbolShowMinScale = (double) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((VectorLayer) item).setScaleRangeOfRegBorderSymbolShow(new ScaleRange(regBorderSymbolShowMinScale, regBorderSymbolShowMaxScale));
                        }
                    }
                });
                items[16] = new PropertyItem("边线符号化最大比例尺", "边线符号化最大比例尺", String_Diaplay, regBorder && regBorderSymbolShow, String.class, ScaleComboBoxPropertyEditor.class);
                items[16].setValue(regBorderSymbolShowMaxScale);
                items[16].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Boolean) {
                            regBorderSymbolShowMaxScale = (double) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((VectorLayer) item).setScaleRangeOfRegBorderSymbolShow(new ScaleRange(regBorderSymbolShowMinScale, regBorderSymbolShowMaxScale));
                        }
                    }
                });
                items[17] = new PropertyItem("边线随图缩放", "边线随图缩放", String_Diaplay, regBorder, Boolean.class, null);
                items[17].setValue(regBorderFollowZoom);
                items[17].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        regBorderFollowZoom = (boolean) newValue;
                        items[18].setEditable(regBorder && regBorderFollowZoom);
                        items[19].setEditable(regBorder && regBorderFollowZoom);
                        hasChanged = true;
                        if (isUpdate) {
                            ((VectorLayer) item).setIsRegionBorderFollowZoom(regBorderFollowZoom);
                        }
                    }
                });

                items[18] = new PropertyItem("边线随图缩放最小比例尺", "边线随图缩放最小比例尺", String_Diaplay, regBorder && regBorderFollowZoom, String.class, ScaleComboBoxPropertyEditor.class);
                items[18].setValue(regBorderFollowZoomMinScale);
                items[18].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        regBorderFollowZoomMinScale = (double) newValue;
                        hasChanged = true;
                        if (isUpdate) {
                            ((VectorLayer) item).setScaleRangeOfRegBorderFollowZoom(new ScaleRange(regBorderFollowZoomMinScale, regBorderFollowZoomMaxScale));
                        }
                    }
                });
                items[19] = new PropertyItem("边线随图缩放最大比例尺", "边线随图缩放最大比例尺", String_Diaplay, regBorder && regBorderFollowZoom, String.class, ScaleComboBoxPropertyEditor.class);
                items[19].setValue(regBorderFollowZoomMaxScale);
                items[19].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        regBorderFollowZoomMaxScale = (double) newValue;
                        hasChanged = true;
                        if (isUpdate) {
                            ((VectorLayer) item).setScaleRangeOfRegBorderFollowZoom(new ScaleRange(regBorderFollowZoomMinScale, regBorderFollowZoomMaxScale));
                        }
                    }
                });

                items[20] = new PropertyItem(String_DisplayCoord, String_DisplayCoord, String_Diaplay, true, Boolean.class, null);
                items[20].setValue(showCoord);
                items[20].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        showCoord = (boolean) newValue;
                        hasChanged = true;
                        if (isUpdate) {
                            ((VectorLayer) item).setIsDipalyCoordinate(showCoord);
                        }
                    }
                });
                items[21] = new PropertyItem(String_ShowGeomRect, String_ShowGeomRect, String_Diaplay, true, Boolean.class, null);
                items[21].setValue(bShowGeomRect);
                items[21].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        bShowGeomRect = (boolean) newValue;
                        hasChanged = true;
                        if (isUpdate) {
                            ((VectorLayer) item).setIsDispalyOutsourcingRectangle(bShowGeomRect);
                        }
                    }
                });
                items[22] = new PropertyItem(String_Transparency, String_Transparency, String_Diaplay, true, Integer.class, null);
                items[22].setValue((int) transParency);
                items[22].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        transParency = (int) newValue;
                        hasChanged = true;
                        if (isUpdate) {
                            ((VectorLayer) item).setTransparency(transParency);
                        }
                    }
                });
                items[23] = new PropertyItem(String_IsUseSuperLink, String_IsUseSuperLink, String_Diaplay, true, Boolean.class, null);
                items[23].setValue(this.isSetSuperLink);
                items[23].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        showCoord = (boolean) newValue;
                        hasChanged = true;
                        if (isUpdate) {
                            ((VectorLayer) item).setIsUseSuperLink(isSetSuperLink);
                        }
                    }
                });
                items[24] = new PropertyItem(String_SetSuperLinkFieldName, String_SetSuperLinkFieldName, String_Diaplay, this.isSetSuperLink, String.class, null);
                items[24].setValue(this.setSuperLinkFldName);
                items[24].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        setSuperLinkFldName = (String) newValue;
                        hasChanged = true;
                        if (isUpdate) {
                            ((VectorLayer) item).setSuperLinkFieldName(setSuperLinkFldName);
                        }
                    }
                });
//                items[12] = new PropertyItem(String_SelfPaintDriver, String_SelfPaintDriver, String_Diaplay, true, String.class, null);
//                items[12].setValue(renderInfo);
//                items[12].getObservableValue().get().addListener(new ChangeListener<Object>() {
//                    @Override
//                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
//                        renderInfo = (RenderInfo) newValue;
//                        hasChanged = true;
//                        if (isUpdate) {
//                            ((VectorLayer) item).setRenderInfo(renderInfo);
//                        }
//                    }
//                });
//                items[25] = new PropertyItem("正向投影参数设置", "正向投影参数设置", String_Diaplay, true,false, String.class, null);
//                items[25].setValue(this.projParam);
//                items[25].getObservableValue().get().addListener(new ChangeListener<Object>() {
//                    @Override
//                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
//                        projParam = (String) newValue;
//                        hasChanged = true;
//                        if (isUpdate) {
//
//                        }
//                    }
//                });
//                items[26] = new PropertyItem("逆向投影参数设置", "逆向投影参数设置", String_Diaplay, true,false, String.class, null);
//                items[26].setValue(this.obProjParam);
//                items[26].getObservableValue().get().addListener(new ChangeListener<Object>() {
//                    @Override
//                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
//                        obProjParam = (String) newValue;
//                        hasChanged = true;
//                        if (isUpdate) {
//                        }
//                    }
//                });
                items[25] = new PropertyItem(String_FiltrateMode, String_FiltrateMode, String_Diaplay, true, String.class, ComboBoxPropertyEditor.class);
                items[25].setValue(this.fMode);
                items[25].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        fMode = (FilterMode) newValue;
                        items[26].setVisible(fMode == FilterMode.AttFilter);
                        items[27].setVisible(fMode == FilterMode.RectFilter);
                        hasChanged = true;
                    }
                });
                items[26] = new PropertyItem("过滤条件", String_FiltrateCondition, String_Diaplay, true, fMode == FilterMode.AttFilter, String.class, SQLQueryEditor.class);
                items[26].setValue(this.filter != null ? this.filter : "");
                items[26].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof String) {
                            filter = (String) newValue;
                            if (filterCondition == null) {
                                filterCondition = new QueryDef();
                            }
                            if (filterCondition != null) {
                                filterCondition.setFilter(filter);
                            }
                            hasChanged = true;
                        }
                        if (hasChanged && isUpdate) {
                            ((VectorLayer) item).setQueryDef(filterCondition);
                        }
                    }
                });
                RectBound1 rectBound1 = new RectBound1(rt);
                items[27] = new PropertyItem("范围过滤", "范围过滤", String_Diaplay, true, fMode == FilterMode.RectFilter, String.class, PopupPropertyEditor.class);
                items[27].setValue(rectBound1);
                items[27].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof RectBound1) {
                            rt =new Rect(((RectBound1) newValue).getXMin(),
                                    ((RectBound1) newValue).getYMin(),
                                    ((RectBound1) newValue).getXMax(),
                                    ((RectBound1) newValue).getYMax());                            if (filterCondition == null) {
                                filterCondition = new QueryDef();
                            }
                            if (filterCondition != null) {
                                filterCondition.setRect(rt, SpaQueryMode.ModeMBRIntersect);
                            }
                            hasChanged = true;
                        }
                        if (hasChanged && isUpdate) {
                            ((VectorLayer) item).setQueryDef(filterCondition);
                        }
                    }
                });
            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return this.items;
        }

        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
                AddUnitedSetting(items, item, propertySheet, isUIUpdate);
            }
            return propertySheet;
        }

        @Override
        public void setImmediatelyUpdate(boolean isUpdate) {
            this.isUpdate = isUpdate;
        }

        @Override
        public boolean isImmediatelyUpdate() {
            return this.isUpdate;
        }

    }

    /**
     * 点 简单要素类-显示属性
     */
    public static class PntSFClsShowProperty extends ShowProperty {
        private DocumentItem item = null;
        private PropertyItem[] items;
        private PropertySheet propertySheet;

        private boolean useStandbyGeoInfo = false;
        private PntInfo pntInfo = null;

        public String String_Diaplay = "显示";
        public String String_UseStandbyGeoInfo = "启用备用图形";
        public String String_PointSymbol = "子图";

        public PntSFClsShowProperty() {
            isUIUpdate = new SimpleBooleanProperty(this, "isUIUpdate", true);
            isUIUpdate.addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (propertySheet != null && isUIUpdate.get()) {
                        ArrayList<PropertyItem> newItems = new ArrayList<>();
                        for (int i = 0; i < items.length; i++) {
                            if (items[i].isVisible()) {
                                newItems.add(items[i]);
                            }
                        }
                        if (propertySheet != null && isUIUpdate.get()) {
                            propertySheet.getItems().setAll(newItems);
                        }
                    }
                }
            });
        }

        @Override
        public void apply() {
            super.apply();
            if (!this.isUpdate && hasChanged) {
                if (item instanceof VectorLayer) {
                    this.item.setProperty("IsUseStandbyPntInfo", useStandbyGeoInfo);
                    this.item.setProperty("StandbyPntInfo", pntInfo);
                }
            }
        }

        @Override
        public void setDocItem(DocumentItem item) {
            super.setDocItem(item);
            this.item = item;
            if (this.item instanceof VectorLayer) {
                Object obj1 = this.item.getProperty("IsUseStandbyPntInfo");
                if (obj1 instanceof Boolean) {
                    this.useStandbyGeoInfo = (boolean) obj1;
                }
                Object obj2 = this.item.getProperty("StandbyPntInfo");
                if (obj2 instanceof PntInfo) {
                    this.pntInfo = (PntInfo) obj2;
                }
                PropertyItem[] oldItems = super.getPropertyItems();
                items = new PropertyItem[oldItems.length + 2];
                System.arraycopy(oldItems, 0, items, 0, oldItems.length);
                items[oldItems.length] = new PropertyItem(String_UseStandbyGeoInfo, String_UseStandbyGeoInfo, String_Diaplay,
                        true, Boolean.class, null);
                items[oldItems.length].setValue(useStandbyGeoInfo);
                if (items[oldItems.length].getObservableValue().isPresent()) {
                    items[oldItems.length].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            useStandbyGeoInfo = (boolean) newValue;
                            hasChanged = true;
                            if (isUpdate) {
                                item.setProperty("IsUseStandbyPntInfo", useStandbyGeoInfo);
                            }
                        }
                    });
                }
                items[oldItems.length + 1] = new PropertyItem(String_PointSymbol, String_PointSymbol, String_Diaplay,
                        true, String.class, PopupPropertyGeomInfoEditor.class);
                //测试代码
                if (pntInfo == null) {
                    pntInfo = new PntInfo();
                    pntInfo.setSymID(1);
                    pntInfo.setWidth(5);
                    pntInfo.setWidth(5);
                    pntInfo.setOutClr1(3);
                    pntInfo.setOutClr2(4);
                    pntInfo.setOutClr3(5);
                }
                items[oldItems.length + 1].setValue(pntInfo);
                if (items[oldItems.length + 1].getObservableValue().isPresent()) {
                    items[oldItems.length + 1].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof PntInfo) {
                                pntInfo = (PntInfo) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                items[oldItems.length + 1].setValue(pntInfo);
                                item.setProperty("StandbyPntInfo", pntInfo);
                            }
                        }
                    });
                }
            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return this.items;
        }

        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
                AddUnitedSetting(items, item, propertySheet, isUIUpdate);
            }
            return propertySheet;
        }
    }

    /**
     * 线 简单要素类-显示属性
     */
    public static class LinSFClsShowProperty extends ShowProperty {
        private boolean showLinDir = false;
        private int linDirColor = 1;
        private boolean useStandbyGeoInfo = false;
        private LinInfo linInfo = null;
        private boolean isReduceLine = false;
        private int linStep = 0;
        private boolean notDraw0Line = true;
        private DocumentItem item = null;
        private PropertyItem[] items;
        private PropertySheet propertySheet;

        public LinSFClsShowProperty() {
            isUIUpdate = new SimpleBooleanProperty(this, "isUIUpdate", true);
            isUIUpdate.addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (propertySheet != null && isUIUpdate.get()) {
                        ArrayList<PropertyItem> newItems = new ArrayList<>();
                        for (int i = 0; i < items.length; i++) {
                            if (items[i].isVisible()) {
                                newItems.add(items[i]);
                            }
                        }
                        if (propertySheet != null && isUIUpdate.get()) {
                            propertySheet.getItems().setAll(newItems);
                        }
                    }
                }
            });
        }

        @Override
        public void apply() {
            super.apply();
            if (!this.isUpdate && hasChanged) {
                if (item instanceof VectorLayer) {
                    this.item.setProperty("IsDispLinDirection", showLinDir);
                    this.item.setProperty("IsUseStandbyLinInfo", useStandbyGeoInfo);
                    this.item.setProperty("StandbyLinInfo", linInfo);
                    this.item.setProperty("bReduceLine", isReduceLine);
                    this.item.setProperty("ReduceLineStep", linStep);
                    Object obj = this.item.getProperty("DirectionLinInfo");
                    if (obj instanceof LinInfo) {
                        LinInfo tmpInfo = (LinInfo) obj;
                        tmpInfo.setOutClr1(this.linDirColor);
                        this.item.setProperty("DirectionLinInfo", tmpInfo);
                    }
                }
            }
        }

        @Override
        public void setDocItem(DocumentItem item) {
            super.setDocItem(item);
            this.item = item;
            if (this.item instanceof VectorLayer) {
                Object obj0 = this.item.getProperty("bUseStandbyRegInfo");
                if (obj0 instanceof Boolean) {
                    this.showLinDir = (boolean) obj0;
                }

                Object obj1 = this.item.getProperty("IsUseStandbyLinInfo");
                if (obj1 instanceof Boolean) {
                    this.useStandbyGeoInfo = (boolean) obj1;
                }
                Object obj2 = this.item.getProperty("StandbyLinInfo");
                if (obj2 instanceof LinInfo) {
                    this.linInfo = (LinInfo) obj2;
                }
                Object obj3 = this.item.getProperty("bReduceLine");
                if (obj3 instanceof Boolean) {
                    this.isReduceLine = (boolean) obj3;
                }
                Object obj4 = this.item.getProperty("ReduceLineStep");
                if (obj4 instanceof Integer) {
                    this.linStep = (int) obj4;
                }
                Object obj5 = this.item.getProperty("DirectionLinInfo");
                if (obj5 instanceof LinInfo) {
                    LinInfo tmpInfo = (LinInfo) obj5;
                    this.linDirColor = tmpInfo.getOutClr1();
                }
                PropertyItem[] oldItems = super.getPropertyItems();
                items = new PropertyItem[oldItems.length + 6];
                System.arraycopy(oldItems, 0, items, 0, oldItems.length);
                items[oldItems.length] = new PropertyItem("显示线方向", "显示线方向", "显示", true, Boolean.class, null);
                items[oldItems.length].setValue(this.showLinDir);
                if (items[oldItems.length].getObservableValue().isPresent()) {
                    items[oldItems.length].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            showLinDir = (boolean) newValue;
                            hasChanged = true;
                            if (isUpdate) {
                                item.setProperty("IsDispLinDirection", showLinDir);
                            }
                        }
                    });
                }
                items[oldItems.length + 1] = new PropertyItem("线方向颜色", "线方向颜色", "显示", true, Integer.class,
                        MapGISColorPickerEditor.class);
                items[oldItems.length + 1].setValue(this.linDirColor);
                if (items[oldItems.length + 1].getObservableValue().isPresent()) {
                    items[oldItems.length + 1].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Integer) {
                                linDirColor = (int) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                Object obj = item.getProperty("DirectionLinInfo");
                                if (obj instanceof LinInfo) {
                                    LinInfo tmpInfo = (LinInfo) obj;
                                    tmpInfo.setOutClr1(linDirColor);
                                    item.setProperty("DirectionLinInfo", tmpInfo);
                                }
                            }
                        }
                    });
                }

                items[oldItems.length + 2] = new PropertyItem("启用备用图形", "启用备用图形", "显示",
                        true, Boolean.class, null);
                items[oldItems.length + 2].setValue(useStandbyGeoInfo);
                if (items[oldItems.length + 2].getObservableValue().isPresent()) {
                    items[oldItems.length + 2].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            useStandbyGeoInfo = (boolean) newValue;
                            hasChanged = true;
                            if (isUpdate) {
                                item.setProperty("IsUseStandbyLinInfo", useStandbyGeoInfo);
                            }
                        }
                    });
                }
                items[oldItems.length + 3] = new PropertyItem("线型", "线型", "显示",
                        true, String.class, PopupPropertyGeomInfoEditor.class);
                if (linInfo == null) {
                    linInfo = new LinInfo();
                    linInfo.setOutClr1(1);
                    linInfo.setOutClr2(4);
                    linInfo.setOutClr3(3);
                }
                items[oldItems.length + 3].setValue(linInfo);
                if (items[oldItems.length + 3].getObservableValue().isPresent()) {
                    items[oldItems.length + 3].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof LinInfo) {
                                linInfo = (LinInfo) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                items[oldItems.length + 3].setValue(linInfo);
                                item.setProperty("StandbyLinInfo", linInfo);
                            }
                        }
                    });
                }
                items[oldItems.length + 4] = new PropertyItem("线化简", "线化简", "显示",
                        true, Boolean.class, null);
                items[oldItems.length + 4].setValue(isReduceLine);
                if (items[oldItems.length + 4].getObservableValue().isPresent()) {
                    items[oldItems.length + 4].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            isReduceLine = (boolean) newValue;
                            hasChanged = true;
                            if (isUpdate) {
                                item.setProperty("bReduceLine", isReduceLine);
                            }
                        }
                    });
                }
                items[oldItems.length + 5] = new PropertyItem("线化简步长值", "线化简步长值", "显示",
                        true, Integer.class, null);
                items[oldItems.length + 5].setValue(linStep);
                if (items[oldItems.length + 5].getObservableValue().isPresent()) {
                    items[oldItems.length + 5].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            linStep = (int) newValue;
                            hasChanged = true;
                            if (isUpdate) {
                                item.setProperty("bReduceLine", linStep);
                            }
                        }
                    });
                }

            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return this.items;
        }

        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
                AddUnitedSetting(items, item, propertySheet, isUIUpdate);
            }
            return propertySheet;
        }
    }

    /**
     * 区 简单要素类-显示属性
     */
    public static class RegSFClsShowProperty extends RegShowProperty {
        private RegInfo regInfo = null;
        private DocumentItem item = null;
        private PropertyItem[] items;
        private PropertySheet propertySheet;

        private boolean fillReg = false;
        private double fillRegMinScale;
        private double fillRegMaxScale;
//        private double fillSymbolShow;
//        private double fillSymbolShowMinScale;
//        private double fillSymbolShowMaxScale;
//        private boolean fillFollowZoom = false;
//        private double fillFollowZoomScale;//符号随图缩放基准比例尺
//        private double fillFollowZoomMinScale;
//        private double fillFollowZoomMaxScale;

        private boolean useStandbyGeoInfo = false;

        public RegSFClsShowProperty() {
            isUIUpdate = new SimpleBooleanProperty(this, "isUIUpdate", true);
            isUIUpdate.addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (propertySheet != null && isUIUpdate.get()) {
                        ArrayList<PropertyItem> newItems = new ArrayList<>();
                        for (int i = 0; i < items.length; i++) {
                            if (items[i].isVisible()) {
                                newItems.add(items[i]);
                            }
                        }
                        if (propertySheet != null && isUIUpdate.get()) {
                            propertySheet.getItems().setAll(newItems);
                        }
                    }
                }
            });
        }

        @Override
        public void apply() {
            super.apply();
            if (!this.isUpdate && hasChanged) {
                if (item instanceof VectorLayer) {
                    this.item.setProperty("bUseStandbyRegInfo", useStandbyGeoInfo);
                    this.item.setProperty("StandbyRegInfo", regInfo);
                }
            }
        }

        @Override
        public void setDocItem(DocumentItem item) {
            super.setDocItem(item);
            this.item = item;
            if (this.item instanceof VectorLayer) {
                Object obj1 = this.item.getProperty("bUseStandbyRegInfo");
                if (obj1 instanceof Boolean) {
                    this.useStandbyGeoInfo = (boolean) obj1;
                }
                Object obj2 = this.item.getProperty("StandbyRegInfo");
                if (obj2 instanceof RegInfo) {
                    this.regInfo = (RegInfo) obj2;
                }

                PropertyItem[] oldItems = super.getPropertyItems();
                items = new PropertyItem[oldItems.length + 2];
                System.arraycopy(oldItems, 0, items, 0, oldItems.length);
                items[oldItems.length] = new PropertyItem("启用备用图形", "启用备用图形", "显示",
                        true, Boolean.class, null);
                items[oldItems.length].setValue(useStandbyGeoInfo);
                if (items[oldItems.length].getObservableValue().isPresent()) {
                    items[oldItems.length].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            useStandbyGeoInfo = (boolean) newValue;
                            hasChanged = true;
                            if (isUpdate) {
                                item.setProperty("bUseStandbyRegInfo", useStandbyGeoInfo);
                            }
                        }
                    });
                }
                items[oldItems.length + 1] = new PropertyItem("图案", "图案", "显示",
                        true, String.class, PopupPropertyGeomInfoEditor.class);
                //测试代码
                if (regInfo == null) {
                    regInfo = new RegInfo();
                    regInfo.setFillClr(5);
                    regInfo.setPatClr(3);
                    regInfo.setPatWidth(5);
                    regInfo.setPatWidth(5);
                }
                items[oldItems.length + 1].setValue(regInfo);
                if (items[oldItems.length + 1].getObservableValue().isPresent()) {
                    items[oldItems.length + 1].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof RegInfo) {
                                regInfo = (RegInfo) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                items[oldItems.length + 1].setValue(regInfo);
                                item.setProperty("StandbyRegInfo", regInfo);
                            }
                        }
                    });
                }
//                items[oldItems.length + 3] = new PropertyItem("简单要素数", "简单要素数", "显示", false, int.class, null);
//                items[oldItems.length + 3].setValue(count);

            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return this.items;
        }

        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
                AddUnitedSetting(items, item, propertySheet, isUIUpdate);
            }
            return propertySheet;
        }

    }

    /**
     * 网络类 图层的显示设置属性
     */
    public static class DisplaySetProperty implements IProperty {
        protected boolean isUpdate = false;//是否立即更新
        protected boolean hasChanged = false;
        private DocumentItem item = null;
        private PropertyItem[] items;
        private PropertySheet propertySheet;
        private PntInfo pntInfo = new PntInfo();//方向子图
        private double dispPercDir = 0;//线上百分比[1-99]
        private int edgeColor = 6;//边线显示颜色
        private int edgeSize = 12;//边线显示大小
        private int nodeColor = 6;//简单结点显示颜色
        private int nodeSize = 12;//简单结点显示大小
        private int complexNodeColor = 6;//复杂结点显示颜色
        private int complexNodeSize = 12;//复杂结点显示大小
        private DspSetting dspSetting = null;

        @Override
        public boolean hasChanged() {
            return this.hasChanged;
        }

        @Override
        public void apply() {
            if (!this.isUpdate && hasChanged) {
                if (this.item instanceof NetClsLayer) {
                    if (dspSetting != null) {
                        dspSetting.setPntinfo(pntInfo);//setDirSUB
                        dspSetting.setDispPercDir(dispPercDir);
                        dspSetting.setDispEdgeIDClr(edgeColor);
                        dspSetting.setDispEdgeIDSz((short) edgeSize);
                        dspSetting.setDispNodIDClr(nodeColor);
                        dspSetting.setDispNodIDSz((short) nodeSize);
                        dspSetting.setDispCNodIDClr(complexNodeColor);
                        dspSetting.setDispCNodIDSz((short) complexNodeSize);
                        ((NetClsLayer) this.item).setDispSetting(dspSetting);
                    }
                }
            }
        }

        @Override
        public DocumentItem getDocItem() {
            return this.item;
        }

        @Override
        public void setDocItem(DocumentItem item) {
            this.item = item;
            if (this.item instanceof NetClsLayer) {
                dspSetting = ((NetClsLayer) this.item).getDispSetting();
                if (dspSetting != null) {
                    pntInfo = dspSetting.getPntinfo();
                    dispPercDir = dspSetting.getDispPercDir();
                    edgeColor = (int) dspSetting.getDispEdgeIDClr();
                    edgeSize = dspSetting.getDispEdgeIDSz();
                    nodeColor = (int) dspSetting.getDispNodIDClr();
                    nodeSize = dspSetting.getDispNodIDSz();
                    complexNodeColor = (int) dspSetting.getDispCNodIDClr();
                    complexNodeSize = dspSetting.getDispCNodIDSz();
                }
                items = new PropertyItem[8];
                items[0] = new PropertyItem("方向子图", "方向子图", "显示", true, String.class, PopupPropertyGeomInfoEditor.class);
                items[0].setValue(this.pntInfo);
                if (items[0].getObservableValue().isPresent()) {
                    items[0].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof PntInfo) {
                                pntInfo = (PntInfo) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (dspSetting != null) {
                                    dspSetting.setPntinfo(pntInfo);
                                    ((NetClsLayer) item).setDispSetting(dspSetting);
                                }
                            }
                        }
                    });
                }
                items[1] = new PropertyItem("线上百分比[1-99]", "线上百分比[1-99]", "显示", true, Integer.class, null);
                items[1].setValue(dispPercDir);
                if (items[1].getObservableValue().isPresent()) {
                    items[1].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Integer) {
                                dispPercDir = (int) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (dspSetting != null) {
                                    dspSetting.setDispPercDir(dispPercDir);
                                    ((NetClsLayer) item).setDispSetting(dspSetting);
                                }
                            }
                        }
                    });
                }
                items[2] = new PropertyItem("边线显示颜色", "边线显示颜色", "显示", true, Integer.class, MapGISColorPicker.class);
                items[2].setValue(edgeColor);
                if (items[2].getObservableValue().isPresent()) {
                    items[2].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Integer) {
                                edgeColor = (int) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (dspSetting != null) {
                                    dspSetting.setDispEdgeIDClr(edgeColor);
                                    ((NetClsLayer) item).setDispSetting(dspSetting);
                                }
                            }
                        }
                    });
                }
                items[3] = new PropertyItem("边线显示大小", "边线显示大小", "显示", true, Integer.class, null);
                items[3].setValue(edgeSize);
                if (items[3].getObservableValue().isPresent()) {
                    items[3].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Integer) {
                                edgeSize = (int) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (dspSetting != null) {
                                    dspSetting.setDispEdgeIDSz((short) edgeSize);
                                    ((NetClsLayer) item).setDispSetting(dspSetting);
                                }
                            }
                        }
                    });
                }
                items[4] = new PropertyItem("简单结点显示颜色", "简单结点显示颜色", "显示", true, Integer.class, MapGISColorPicker.class);
                items[4].setValue(nodeColor);
                if (items[4].getObservableValue().isPresent()) {
                    items[4].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Integer) {
                                nodeColor = (int) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (dspSetting != null) {
                                    dspSetting.setDispNodIDClr(nodeColor);
                                    ((NetClsLayer) item).setDispSetting(dspSetting);
                                }
                            }
                        }
                    });
                }
                items[5] = new PropertyItem("简单结点显示大小", "简单结点显示大小", "显示", true, Integer.class, null);
                items[5].setValue(nodeSize);
                if (items[5].getObservableValue().isPresent()) {
                    items[5].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Integer) {
                                nodeSize = (int) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (dspSetting != null) {
                                    dspSetting.setDispNodIDSz((short) nodeSize);
                                    ((NetClsLayer) item).setDispSetting(dspSetting);
                                }
                            }
                        }
                    });
                }
                items[6] = new PropertyItem("复杂结点显示颜色", "复杂结点显示颜色", "显示", true, Integer.class, MapGISColorPicker.class);
                items[6].setValue(complexNodeColor);
                if (items[6].getObservableValue().isPresent()) {
                    items[6].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Integer) {
                                complexNodeColor = (int) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (dspSetting != null) {
                                    dspSetting.setDispCNodIDClr(complexNodeColor);
                                    ((NetClsLayer) item).setDispSetting(dspSetting);
                                }
                            }
                        }
                    });
                }
                items[7] = new PropertyItem("复杂结点显示大小", "复杂结点显示大小", "显示", true, Integer.class, null);
                items[7].setValue(complexNodeSize);
                if (items[7].getObservableValue().isPresent()) {
                    items[7].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Integer) {
                                complexNodeSize = (int) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (dspSetting != null) {
                                    dspSetting.setDispCNodIDSz((short) complexNodeSize);
                                    ((NetClsLayer) item).setDispSetting(dspSetting);
                                }
                            }
                        }
                    });
                }
                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setDocumentItem(item);
                }

            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return this.items;
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
            return this.isUpdate;
        }
    }

    /**
     * 网络类 图层的构网边线元素属性
     */
    public static class EdgeLayerProperty implements IProperty {
        protected boolean isUpdate = false;//是否立即更新
        protected boolean hasChanged = false;
        private DocumentItem item = null;
        private PropertyItem[] items;
        private PropertySheet propertySheet;
        private NetClsLayer netClsLayer = null;
        private EdgeDispInfo edgeDispInfo = null;
        private int clsID = 0;

        private boolean isUseLinInf = true;//统一配置
        private LinInfo linInfo = new LinInfo();//配置线型
        private boolean dspState = true;//显示状态
        private boolean isDspEdgeID = true;//显示边线ID
        private boolean isDspDirect = true;//显示拓扑方向
        private boolean isDspVertex = true;//显示控制点
        //        private LinPlaceInfo linPlaceInfo = true;//动态注记
        private boolean isLayer = true;//分层显示
        private ArrayList<LayerInfo> layerList;//分层方案
        private boolean isOrigalDsp = true;//符号化显示
        private boolean isFixSize = true;//大小固定
        private boolean isFixPen = true;//笔宽固定

        @Override
        public boolean hasChanged() {
            return this.hasChanged;
        }

        @Override
        public void apply() {
            if (!this.isUpdate && hasChanged) {
                if (this.item instanceof NetClsLayer) {
                    if (edgeDispInfo != null) {
                        edgeDispInfo.setIsUseLinInf(isUseLinInf);
                        edgeDispInfo.setLinInf(linInfo);//setDirSUB
                        //TODO
//                        edgeDispInfo.setDspState(dspState);
                        edgeDispInfo.setIsDspEdgeID(isDspEdgeID);
                        edgeDispInfo.setIsDspDirect(isDspDirect);
                        edgeDispInfo.setIsDspVertex(isDspVertex);
                        edgeDispInfo.setBLayer(isLayer);
                        //TODO
//                        edgeDispInfo.setLayer(layerList);
                        edgeDispInfo.setIsOrigalDsp(isOrigalDsp);
                        edgeDispInfo.setIsFixSize(isFixSize);
                        edgeDispInfo.setIsFixPen(isFixPen);
                        ((NetClsLayer) this.item).setDispInfo(this.clsID, edgeDispInfo);
                    }
                }
            }
        }

        @Override
        public DocumentItem getDocItem() {
            return this.item;
        }

        @Override
        public void setDocItem(DocumentItem item) {
            this.item = item;
            if (this.item instanceof VectorLayer && this.item.getParent() instanceof NetClsLayer) {
                netClsLayer = (NetClsLayer) this.item.getParent();
                clsID = ((VectorLayer) this.item).getData().getClsID();
            }
            if (netClsLayer != null) {
                edgeDispInfo = ((NetClsLayer) this.item).getEdgeDispInfo(clsID);
                if (edgeDispInfo == null) {
                    edgeDispInfo = new EdgeDispInfo();
                }
                isUseLinInf = edgeDispInfo.getIsUseLinInf();
                linInfo = edgeDispInfo.getLinInf();//setDirSUB
                //TODO
//                dspState = edgeDispInfo.getDspState();
                isDspEdgeID = edgeDispInfo.getIsDspEdgeID();
                isDspDirect = edgeDispInfo.getIsDspDirect();
                isDspVertex = edgeDispInfo.getIsDspVertex();
                isLayer = edgeDispInfo.getBLayer();
//                layerList = edgeDispInfo.getLayer();
                isOrigalDsp = edgeDispInfo.getIsOrigalDsp();
                isFixSize = edgeDispInfo.getIsFixSize();
                isFixPen = edgeDispInfo.getIsFixPen();
//                items = new PropertyItem[8];
                ArrayList<PropertyItem> tmpItems = new ArrayList<>();
                PropertyItem propertyItem = new PropertyItem("统一配置", "统一配置", "边线显示", true, Boolean.class, null);
                propertyItem.setValue(isUseLinInf);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Boolean) {
                                isUseLinInf = (boolean) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (edgeDispInfo != null) {
                                    edgeDispInfo.setIsUseLinInf(isUseLinInf);
                                    netClsLayer.setDispInfo(clsID, edgeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                propertyItem = new PropertyItem("配置线型", "配置线型", "显示", true, String.class, PopupPropertyGeomInfoEditor.class);
                propertyItem.setValue(linInfo);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof LinInfo) {
                                linInfo = (LinInfo) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (edgeDispInfo != null) {
                                    edgeDispInfo.setIsUseLinInf(isUseLinInf);
                                    netClsLayer.setDispInfo(clsID, edgeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                propertyItem = new PropertyItem("显示状态", "显示状态", "显示", true, Boolean.class, null);
                propertyItem.setValue(dspState);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Boolean) {
                                dspState = (boolean) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (edgeDispInfo != null) {
//                        edgeDispInfo.setDspState(dspState);
                                    netClsLayer.setDispInfo(clsID, edgeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                propertyItem = new PropertyItem("显示边线ID", "显示边线ID", "显示", true, Boolean.class, null);
                propertyItem.setValue(isDspEdgeID);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Boolean) {
                                isDspEdgeID = (boolean) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (edgeDispInfo != null) {
                                    edgeDispInfo.setIsDspEdgeID(isDspEdgeID);
                                    netClsLayer.setDispInfo(clsID, edgeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                propertyItem = new PropertyItem("显示拓扑方向", "显示拓扑方向", "显示", true, Boolean.class, null);
                propertyItem.setValue(isDspDirect);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Boolean) {
                                isDspDirect = (boolean) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (edgeDispInfo != null) {
                                    edgeDispInfo.setIsDspDirect(isDspDirect);
                                    netClsLayer.setDispInfo(clsID, edgeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                propertyItem = new PropertyItem("显示控制点", "显示控制点", "显示", true, Boolean.class, null);
                propertyItem.setValue(isDspVertex);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Boolean) {
                                isDspVertex = (boolean) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (edgeDispInfo != null) {
                                    edgeDispInfo.setIsDspVertex(isDspVertex);
                                    netClsLayer.setDispInfo(clsID, edgeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                propertyItem = new PropertyItem("分层显示", "分层显示", "显示", true, Boolean.class, null);
                propertyItem.setValue(isLayer);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Boolean) {
                                isLayer = (boolean) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (edgeDispInfo != null) {
                                    edgeDispInfo.setBLayer(isLayer);
                                    netClsLayer.setDispInfo(clsID, edgeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                //TODO暂时屏蔽
                propertyItem = new PropertyItem("分层方案", "分层方案", "显示", true, false, String.class, null);
                propertyItem.setValue(layerList);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            layerList = (ArrayList<LayerInfo>) newValue;
                            hasChanged = true;

                            if (isUpdate && hasChanged) {
                                if (edgeDispInfo != null) {
//                                    edgeDispInfo.setLayer(layerList);
                                    netClsLayer.setDispInfo(clsID, edgeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                propertyItem = new PropertyItem("符号化显示", "符号化显示", "显示", true, Boolean.class, null);
                propertyItem.setValue(isOrigalDsp);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Boolean) {
                                isOrigalDsp = (boolean) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (edgeDispInfo != null) {
                                    edgeDispInfo.setIsOrigalDsp(isOrigalDsp);
                                    netClsLayer.setDispInfo(clsID, edgeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                propertyItem = new PropertyItem("大小固定", "大小固定", "显示", true, Boolean.class, null);
                propertyItem.setValue(isFixSize);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Boolean) {
                                isFixSize = (boolean) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (edgeDispInfo != null) {
                                    edgeDispInfo.setIsFixSize(isFixSize);
                                    netClsLayer.setDispInfo(clsID, edgeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                propertyItem = new PropertyItem("笔宽固定", "笔宽固定", "显示", true, Boolean.class, null);
                propertyItem.setValue(isFixPen);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Boolean) {
                                isFixPen = (boolean) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (edgeDispInfo != null) {
                                    edgeDispInfo.setIsFixPen(isFixPen);
                                    netClsLayer.setDispInfo(clsID, edgeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                items = new PropertyItem[tmpItems.size()];
                items = (PropertyItem[]) tmpItems.toArray(items);

                for (PropertyItem propertyItem1 :
                        items) {
                    propertyItem.setDocumentItem(item);
                }

            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return this.items;
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
            return this.isUpdate;
        }
    }

    /**
     * 网络类 图层的构网简单结点元素属性
     */
    public static class SNodeLayerProperty implements IProperty {
        protected boolean isUpdate = false;//是否立即更新
        protected boolean hasChanged = false;
        private DocumentItem item = null;
        private PropertyItem[] items;
        private PropertySheet propertySheet;
        private NetClsLayer netClsLayer = null;
        private SNodeDispInfo sNodeDispInfo = null;
        private int clsID = 0;

        private boolean isUsePntInf = true;         //统一配置
        private PntInfo pntInfo = new PntInfo();    //配置子图
        private boolean dspState = true;            //显示状态
        private boolean isDspNodeID = true;         //显示结点ID
        private double dspPercent;                  //显示比例
        //                private PointPlaceInfo pointPlaceInfo = true;//动态注记
        private boolean isLayer = true;             //分层显示
        private ArrayList<LayerInfo> layerList;     //分层方案
        private boolean isOrigalDsp = true;         //符号化显示
        private boolean isFixSize = true;           //大小固定
        private boolean isFixPen = true;            //笔宽固定

        @Override
        public boolean hasChanged() {
            return this.hasChanged;
        }

        @Override
        public void apply() {
            if (!this.isUpdate && hasChanged) {
                if (this.item instanceof NetClsLayer) {
                    if (sNodeDispInfo != null) {
//                        sNodeDispInfo.setIsUsePntInf(isUsePntInf);
                        sNodeDispInfo.setPntInf(pntInfo);//setDirSUB
//                        sNodeDispInfo.setDspState(dspState);
//                        sNodeDispInfo.setIsDspNodeID(isDspNodeID);
                        sNodeDispInfo.setDspPercent(dspPercent);
                        sNodeDispInfo.setBLayer(isLayer);
//                        sNodeDispInfo.setLayer(layerList);
//                        sNodeDispInfo.setIsOrigalDsp(isOrigalDsp);
//                        sNodeDispInfo.setIsFixSize(isFixSize);
//                        sNodeDispInfo.setIsFixPen(isFixPen);
                        ((NetClsLayer) this.item).setDispInfo(this.clsID, sNodeDispInfo);
                    }
                }
            }
        }

        @Override
        public DocumentItem getDocItem() {
            return this.item;
        }

        @Override
        public void setDocItem(DocumentItem item) {
            this.item = item;
            if (this.item instanceof VectorLayer && this.item.getParent() instanceof NetClsLayer) {
                netClsLayer = (NetClsLayer) this.item.getParent();
                clsID = ((VectorLayer) this.item).getData().getClsID();
            }
            if (netClsLayer != null) {
                sNodeDispInfo = ((NetClsLayer) this.item).getSNodeDispInfo(clsID);
                if (sNodeDispInfo == null) {
                    sNodeDispInfo = new SNodeDispInfo();
                }
//                isUsePntInf = sNodeDispInfo.getIsUsePntInf();
                pntInfo = sNodeDispInfo.getPntInf();//setDirSUB
//                    dspState = sNodeDispInfo.getDspState();
//                    isDspNodeID = sNodeDispInfo.getIsDspNodeID();
                dspPercent = sNodeDispInfo.getDspPercent();
                isLayer = sNodeDispInfo.getBLayer();
//                layerList = sNodeDispInfo.getLayer();
//                    isOrigalDsp = sNodeDispInfo.getIsOrigalDsp();
//                    isFixSize = sNodeDispInfo.getIsFixSize();
//                    isFixPen = sNodeDispInfo.getIsFixPen();
//                items = new PropertyItem[8];
                ArrayList<PropertyItem> tmpItems = new ArrayList<>();
                PropertyItem propertyItem = new PropertyItem("统一配置", "统一配置", "边线显示", true, Boolean.class, null);
                propertyItem.setValue(isUsePntInf);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Boolean) {
                                isUsePntInf = (boolean) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (sNodeDispInfo != null) {
//                                    sNodeDispInfo.setIsUsePntInf(isUsePntInf);
                                    netClsLayer.setDispInfo(clsID, sNodeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                propertyItem = new PropertyItem("配置子图", "配置子图", "显示", true, String.class, PopupPropertyGeomInfoEditor.class);
                propertyItem.setValue(pntInfo);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof PntInfo) {
                                pntInfo = (PntInfo) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (sNodeDispInfo != null) {
                                    sNodeDispInfo.setPntInf(pntInfo);//setDirSUB
                                    netClsLayer.setDispInfo(clsID, sNodeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                propertyItem = new PropertyItem("显示状态", "显示状态", "显示", true, Boolean.class, null);
                propertyItem.setValue(dspState);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Boolean) {
                                dspState = (boolean) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (sNodeDispInfo != null) {
//                                    sNodeDispInfo.setDspState(dspState);
                                    netClsLayer.setDispInfo(clsID, sNodeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                propertyItem = new PropertyItem("显示结点ID", "显示结点ID", "显示", true, Boolean.class, null);
                propertyItem.setValue(isDspNodeID);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Boolean) {
                                isDspNodeID = (boolean) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (sNodeDispInfo != null) {
//                                    sNodeDispInfo.setIsDspNodeID(isDspNodeID);
                                    netClsLayer.setDispInfo(clsID, sNodeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);


                propertyItem = new PropertyItem("显示比例", "显示比例", "显示", true, Double.class, null);
                propertyItem.setValue(dspPercent);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Double) {
                                dspPercent = (double) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (sNodeDispInfo != null) {
                                    sNodeDispInfo.setDspPercent(dspPercent);
                                    netClsLayer.setDispInfo(clsID, sNodeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                propertyItem = new PropertyItem("分层显示", "分层显示", "显示", true, Boolean.class, null);
                propertyItem.setValue(isLayer);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Boolean) {
                                isLayer = (boolean) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (sNodeDispInfo != null) {
                                    sNodeDispInfo.setBLayer(isLayer);
                                    netClsLayer.setDispInfo(clsID, sNodeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                propertyItem = new PropertyItem("分层方案", "分层方案", "显示", true, String.class, null);
                propertyItem.setValue(layerList);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            layerList = (ArrayList<LayerInfo>) newValue;
                            hasChanged = true;

                            if (isUpdate && hasChanged) {
                                if (sNodeDispInfo != null) {
//                                    sNodeDispInfo.setLayer(layerList);
                                    netClsLayer.setDispInfo(clsID, sNodeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                propertyItem = new PropertyItem("符号化显示", "符号化显示", "显示", true, Boolean.class, null);
                propertyItem.setValue(isOrigalDsp);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Boolean) {
                                isOrigalDsp = (boolean) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (sNodeDispInfo != null) {
//                                    sNodeDispInfo.setIsOrigalDsp(isOrigalDsp);
                                    netClsLayer.setDispInfo(clsID, sNodeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                propertyItem = new PropertyItem("大小固定", "大小固定", "显示", true, Boolean.class, null);
                propertyItem.setValue(isFixSize);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Boolean) {
                                isFixSize = (boolean) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (sNodeDispInfo != null) {
//                                    sNodeDispInfo.setIsFixSize(isFixSize);
                                    netClsLayer.setDispInfo(clsID, sNodeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                propertyItem = new PropertyItem("笔宽固定", "笔宽固定", "显示", true, Boolean.class, null);
                propertyItem.setValue(isFixPen);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Boolean) {
                                isFixPen = (boolean) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (sNodeDispInfo != null) {
//                                    sNodeDispInfo.setIsFixPen(isFixPen);
                                    netClsLayer.setDispInfo(clsID, sNodeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                items = new PropertyItem[tmpItems.size()];
                items = (PropertyItem[]) tmpItems.toArray(items);

                for (PropertyItem propertyItem1 :
                        items) {
                    propertyItem.setDocumentItem(item);
                }

            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return this.items;
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
            return this.isUpdate;
        }
    }

    /**
     * 网络类 图层的构网简单结点元素属性
     */
    public static class CNodeLayerProperty implements IProperty {
        protected boolean isUpdate = false;//是否立即更新
        protected boolean hasChanged = false;
        private DocumentItem item = null;
        private PropertyItem[] items;
        private PropertySheet propertySheet;
        private NetClsLayer netClsLayer = null;
        private CNodeDispInfo cNodeDispInfo = null;
        private int clsID = 0;

        private boolean isUsePntInf = true;         //统一配置
        private PntInfo pntInfo = new PntInfo();    //配置子图
        private boolean dspState = true;            //显示状态
        private boolean isDspNodeID = true;         //显示结点ID
        private boolean isDspPipeInWell = true;        //屏蔽边线
        private double dspPercent;                  //显示比例
        //                private PointPlaceInfo pointPlaceInfo = true;//动态注记
        private boolean isLayer = true;             //分层显示
        private ArrayList<LayerInfo> layerList;     //分层方案
        private boolean isOrigalDsp = true;         //符号化显示
        private boolean isFixSize = true;           //大小固定
        private boolean isFixPen = true;            //笔宽固定

        @Override
        public boolean hasChanged() {
            return this.hasChanged;
        }

        @Override
        public void apply() {
            if (!this.isUpdate && hasChanged) {
                if (this.item instanceof NetClsLayer) {
                    if (cNodeDispInfo != null) {
//                        cNodeDispInfo.setIsUsePntInf(isUsePntInf);
                        cNodeDispInfo.setPntInf(pntInfo);//setDirSUB
//                        cNodeDispInfo.setDspState(dspState);
//                        cNodeDispInfo.setIsDspNodeID(isDspNodeID);
//                        cNodeDispInfo.setIsDspPipeInWell(isDspPipeInWell);
                        cNodeDispInfo.setDspPercent(dspPercent);
                        cNodeDispInfo.setBLayer(isLayer);
//                        cNodeDispInfo.setLayer(layerList);
//                        cNodeDispInfo.setIsOrigalDsp(isOrigalDsp);
//                        cNodeDispInfo.setIsFixSize(isFixSize);
//                        cNodeDispInfo.setIsFixPen(isFixPen);
                        ((NetClsLayer) this.item).setDispInfo(this.clsID, cNodeDispInfo);
                    }
                }
            }
        }

        @Override
        public DocumentItem getDocItem() {
            return this.item;
        }

        @Override
        public void setDocItem(DocumentItem item) {
            this.item = item;
            if (this.item instanceof VectorLayer && this.item.getParent() instanceof NetClsLayer) {
                netClsLayer = (NetClsLayer) this.item.getParent();
                clsID = ((VectorLayer) this.item).getData().getClsID();
            }
            if (netClsLayer != null) {
                cNodeDispInfo = ((NetClsLayer) this.item).getCNodeDispInfo(clsID);
                if (cNodeDispInfo == null) {
                    cNodeDispInfo = new CNodeDispInfo();
//                    isUsePntInf = cNodeDispInfo.getIsUsePntInf();
                    pntInfo = cNodeDispInfo.getPntInf();//setDirSUB
//                    dspState = cNodeDispInfo.getDspState();
//                    isDspNodeID = cNodeDispInfo.getIsDspNodeID();
//                    isDspPipeInWell = cNodeDispInfo.getIsDspPipeInWell();
                    dspPercent = cNodeDispInfo.getDspPercent();
                    isLayer = cNodeDispInfo.getBLayer();
//                    layerList = cNodeDispInfo.getLayer();
//                    isOrigalDsp = cNodeDispInfo.getIsOrigalDsp();
//                    isFixSize = cNodeDispInfo.getIsFixSize();
//                    isFixPen = cNodeDispInfo.getIsFixPen();
                }
//                items = new PropertyItem[8];
                ArrayList<PropertyItem> tmpItems = new ArrayList<>();
                PropertyItem propertyItem = new PropertyItem("统一配置", "统一配置", "边线显示", true, Boolean.class, null);
                propertyItem.setValue(isUsePntInf);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Boolean) {
                                isUsePntInf = (boolean) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (cNodeDispInfo != null) {
//                                    sNodeDispInfo.setIsUsePntInf(isUsePntInf);
                                    netClsLayer.setDispInfo(clsID, cNodeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                propertyItem = new PropertyItem("配置子图", "配置子图", "显示", true, String.class, PopupPropertyGeomInfoEditor.class);
                propertyItem.setValue(pntInfo);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof PntInfo) {
                                pntInfo = (PntInfo) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (cNodeDispInfo != null) {
                                    cNodeDispInfo.setPntInf(pntInfo);//setDirSUB
                                    netClsLayer.setDispInfo(clsID, cNodeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                propertyItem = new PropertyItem("显示状态", "显示状态", "显示", true, Boolean.class, null);
                propertyItem.setValue(dspState);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Boolean) {
                                dspState = (boolean) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (cNodeDispInfo != null) {
//                                    cNodeDispInfo.setDspState(dspState);
                                    netClsLayer.setDispInfo(clsID, cNodeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                propertyItem = new PropertyItem("显示结点ID", "显示结点ID", "显示", true, Boolean.class, null);
                propertyItem.setValue(isDspNodeID);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Boolean) {
                                isDspNodeID = (boolean) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (cNodeDispInfo != null) {
//                                    cNodeDispInfo.setIsDspNodeID(isDspNodeID);
                                    netClsLayer.setDispInfo(clsID, cNodeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                propertyItem = new PropertyItem("屏蔽边线", "屏蔽边线", "显示", true, Boolean.class, null);
                propertyItem.setValue(isDspPipeInWell);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Boolean) {
                                isDspPipeInWell = (boolean) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (cNodeDispInfo != null) {
//                                    cNodeDispInfo.setIsDspPipeInWell(isDspPipeInWell);
                                    netClsLayer.setDispInfo(clsID, cNodeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                propertyItem = new PropertyItem("显示比例", "显示比例", "显示", true, Double.class, null);
                propertyItem.setValue(dspPercent);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Double) {
                                dspPercent = (double) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (cNodeDispInfo != null) {
                                    cNodeDispInfo.setDspPercent(dspPercent);
                                    netClsLayer.setDispInfo(clsID, cNodeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                propertyItem = new PropertyItem("分层显示", "分层显示", "显示", true, Boolean.class, null);
                propertyItem.setValue(isLayer);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Boolean) {
                                isLayer = (boolean) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (cNodeDispInfo != null) {
                                    cNodeDispInfo.setBLayer(isLayer);
                                    netClsLayer.setDispInfo(clsID, cNodeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                propertyItem = new PropertyItem("分层方案", "分层方案", "显示", true, String.class, null);
                propertyItem.setValue(layerList);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            layerList = (ArrayList<LayerInfo>) newValue;
                            hasChanged = true;

                            if (isUpdate && hasChanged) {
                                if (cNodeDispInfo != null) {
//                                    sNodeDispInfo.setLayer(layerList);
                                    netClsLayer.setDispInfo(clsID, cNodeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                propertyItem = new PropertyItem("符号化显示", "符号化显示", "显示", true, Boolean.class, null);
                propertyItem.setValue(isOrigalDsp);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Boolean) {
                                isOrigalDsp = (boolean) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (cNodeDispInfo != null) {
//                                    sNodeDispInfo.setIsOrigalDsp(isOrigalDsp);
                                    netClsLayer.setDispInfo(clsID, cNodeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                propertyItem = new PropertyItem("大小固定", "大小固定", "显示", true, Boolean.class, null);
                propertyItem.setValue(isFixSize);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Boolean) {
                                isFixSize = (boolean) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (cNodeDispInfo != null) {
//                                    sNodeDispInfo.setIsFixSize(isFixSize);
                                    netClsLayer.setDispInfo(clsID, cNodeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                propertyItem = new PropertyItem("笔宽固定", "笔宽固定", "显示", true, Boolean.class, null);
                propertyItem.setValue(isFixPen);
                if (propertyItem.getObservableValue().isPresent()) {
                    propertyItem.getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof Boolean) {
                                isFixPen = (boolean) newValue;
                                hasChanged = true;
                            }
                            if (isUpdate && hasChanged) {
                                if (cNodeDispInfo != null) {
//                                    sNodeDispInfo.setIsFixPen(isFixPen);
                                    netClsLayer.setDispInfo(clsID, cNodeDispInfo);
                                }
                            }
                        }
                    });
                }
                tmpItems.add(propertyItem);

                items = new PropertyItem[tmpItems.size()];
                items = (PropertyItem[]) tmpItems.toArray(items);

                for (PropertyItem propertyItem1 :
                        items) {
                    propertyItem.setDocumentItem(item);
                }

            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return this.items;
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
            return this.isUpdate;
        }
    }

    //endregion

    //region 三维图层
    //region 数据源

    /**
     * 三维图层的数据源属性
     */
    public static class DataSource3DProperty implements IProperty {
        private boolean isUpdate = false;//是否立即更新
        private boolean hasChanged = false;//类中的属性值是否被编辑过
        IBasCls basCls;
        private String svrName;
        private String loginUser;
        private String databaseName;
        private int databaseID;
        private DocumentItem docItem = null;//当前显示属性的文档项
        private PropertyItem[] items;
        private PropertySheet propertySheet;

        @Override
        public void setDocItem(DocumentItem item) {
            if (item instanceof Map3DLayer) {
                this.docItem = item;
                basCls = ((Map3DLayer) this.docItem).getData();
                if (basCls != null) {
                    DataBase db = basCls.getGDataBase();
                    if (db != null) {
                        databaseName = db.getName();
                        databaseID = db.getdbID();
                        Server sr = db.getServer();
                        String[] info = sr.getLogin();
                        if (sr != null) {
                            svrName = sr.getSvrName();
                            loginUser = info[0];
                        }
                    }
                }

                items = new PropertyItem[4];
                items[0] = new PropertyItem("数据源名称", "数据源名称", "数据源", false, String.class, null);
                items[0].setValue(this.svrName);
                items[1] = new PropertyItem("登陆用户", "登陆用户", "数据源", false, String.class, null);
                items[1].setValue(loginUser);
                items[2] = new PropertyItem("数据库名称", "数据库名称", "数据源", false, String.class, null);
                items[2].setValue(databaseName);
                items[3] = new PropertyItem("数据库ID", "数据库ID", "数据源", false, int.class, null);
                items[3].setValue(databaseID);
                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setDocumentItem(item);
                }
            }
        }

        @Override
        public DocumentItem getDocItem() {
            return this.docItem;
        }

        @Override
        public boolean hasChanged() {
            return this.hasChanged;
        }

        @Override
        public PropertyItem[] getPropertyItems() {

            return items;
        }

        @Override
        public void apply() {
            this.hasChanged = false;
//            if (this.docItem instanceof MapLayer) {
//
//            }
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
            return this.isUpdate;
        }
    }

    /**
     * 三维图层的数据源属性(带数据范围)
     */
    public static class SpatialDataSource3DProperty extends DataSource3DProperty {
        private Rect3DBound dataScope;//数据范围
        private PropertyItem[] items;
        private PropertySheet propertySheet;

        @Override
        public void setDocItem(DocumentItem item) {
            super.setDocItem(item);
            if (item instanceof Map3DLayer) {
                PropertyItem[] oldItems = super.getPropertyItems();
                dataScope = new Rect3DBound(((Map3DLayer) item).get3DRange());
                items = new PropertyItem[oldItems.length + 1];
                items[0] = new PropertyItem("数据源范围", "数据源范围", "数据源", true,
                        String.class, PopupPropertyEditor.class);
                items[0].setValue(dataScope);
                for (int i = 0; i < oldItems.length; i++) {
                    items[i + 1] = oldItems[i];
                }
                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setDocumentItem(item);
                }
            }
        }

        @Override
        public void apply() {
            super.apply();
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
    }

    /**
     * 三维 矢量 图层的数据源属性
     */
    public static class SFClsDataSource3DProperty extends SpatialDataSource3DProperty {
        private String clsName;
        private int clsID;
        private String geometryType;
        private int count;
        private PropertyItem[] items;
        private PropertySheet propertySheet;

        @Override
        public void setDocItem(DocumentItem item) {
            super.setDocItem(item);
            SFeatureCls cls = null;
            if (item instanceof Vector3DLayer || item instanceof ModelLayer) {
                cls = (SFeatureCls) super.basCls;
            }
            if (cls != null) {
                this.clsName = cls.getName();
                this.clsID = cls.getClsID();
                this.geometryType = LanguageConvert.geomTypeConvert(GeomType.GeomReg);
                this.count = (int) cls.getObjCount();
                DataBase db = cls.getGDataBase();
                if (db != null) {
                    String databaseName = db.getName();
                    int databaseID = db.getdbID();
                    Server sr = db.getServer();
                    String[] info = sr.getLogin();
                    String svrName = sr.getSvrName();
                    String loginUser = info[0];
                }
                PropertyItem[] oldItems = super.getPropertyItems();
                items = new PropertyItem[oldItems.length + 4];
                System.arraycopy(oldItems, 0, items, 0, oldItems.length);
                items[oldItems.length] = new PropertyItem("类名称", "类名称", "数据源", false, String.class, null);
                items[oldItems.length].setValue(this.clsName);
                items[oldItems.length + 1] = new PropertyItem("类ID", "类ID", "数据源", false, int.class, null);
                items[oldItems.length + 1].setValue(clsID);
                items[oldItems.length + 2] = new PropertyItem("几何类型", "几何类型", "数据源", false, String.class, null);
                items[oldItems.length + 2].setValue(geometryType);
                items[oldItems.length + 3] = new PropertyItem("简单要素数", "简单要素数", "数据源", false, int.class, null);
                items[oldItems.length + 3].setValue(count);
                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setDocumentItem(item);
                }
            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {

            return items;
        }

        @Override
        public void apply() {
            super.apply();
        }

        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
            }
            return propertySheet;
        }
    }

    /**
     * 三维 注记类 图层的数据源属性
     */
    public static class AClsDataSource3DProperty extends SpatialDataSource3DProperty {
        private String clsName;
        private int clsID;
        private String geometryType;
        private int count;
        private PropertyItem[] items;
        private PropertySheet propertySheet;

        @Override
        public void setDocItem(DocumentItem item) {
            super.setDocItem(item);
            AnnotationCls cls = null;
            if (item instanceof LabelLayer) {
                if (((LabelLayer) item).getData() instanceof AnnotationCls) {
                    cls = (AnnotationCls)((LabelLayer) item).getData();
                }
            }
            if (cls != null) {
                this.clsName = cls.getName();
                this.clsID = cls.getClsID();
                this.geometryType = LanguageConvert.geomTypeConvert(GeomType.GeomAnn);
                this.count = (int) cls.getObjCount();
                DataBase db = cls.getGDataBase();
                if (db != null) {
                    String databaseName = db.getName();
                    int databaseID = db.getdbID();
                    Server sr = db.getServer();
                    String[] info = sr.getLogin();
                    String svrName = sr.getSvrName();
                    String loginUser = info[0];
                }
                PropertyItem[] oldItems = super.getPropertyItems();
                items = new PropertyItem[oldItems.length + 3];
                System.arraycopy(oldItems, 0, items, 0, oldItems.length);
                items[oldItems.length] = new PropertyItem("类名称", "类名称", "数据源", false, String.class, null);
                items[oldItems.length].setValue(this.clsName);
                items[oldItems.length + 1] = new PropertyItem("类ID", "类ID", "数据源", false, int.class, null);
                items[oldItems.length + 1].setValue(clsID);
                items[oldItems.length + 2] = new PropertyItem("注记数", "注记数", "数据源", false, int.class, null);
                items[oldItems.length + 2].setValue(count);
                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setDocumentItem(item);
                }
            }
        }

        @Override
        public PropertyItem[] getPropertyItems() {

            return items;
        }

        @Override
        public void apply() {
            super.apply();
        }

        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
            }
            return propertySheet;
        }
    }

    /**
     * 三维缓存图层的数据源属性
     */
    public static class CacheDataSource3DProperty implements IProperty {
        private boolean isUpdate = false;//是否立即更新
        protected boolean hasChanged = false;
        private Rect3DBound dataScope;//数据范围
        private String dataSrcUrl = "";
        private ModelDataType layerType = ModelDataType.Oblique;//数据类型
        private PropertyItem[] items;
        private PropertySheet propertySheet;

        @Override
        public void setDocItem(DocumentItem item) {
            if (item instanceof ModelCacheLayer) {
                dataScope = new Rect3DBound(((ModelCacheLayer) item).get3DRange());
                this.dataSrcUrl = ((ModelCacheLayer) item).getURL();
                this.layerType = ((ModelCacheLayer) item).getDataType();
                String layerTypeStr = "";
                if (this.layerType == ModelDataType.Oblique) {
                    layerTypeStr = "倾斜摄影";
                } else if (this.layerType == ModelDataType.LandScape) {
                    layerTypeStr = "景观模型";
                } else if (this.layerType == ModelDataType.M3dCache) {
                    layerTypeStr = "M3D缓存";
                }
                items = new PropertyItem[3];
                items[0] = new PropertyItem("数据源范围", "数据源范围", "常规", true,
                        String.class, PopupPropertyEditor.class);
                items[0].setValue(dataScope);
                items[1] = new PropertyItem("数据源", "数据源", "常规", false, String.class, null);
                items[1].setValue(this.dataSrcUrl);
                items[2] = new PropertyItem("数据类型", "数据类型", "常规", false, String.class, null);
                items[2].setValue(layerTypeStr);
                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setDocumentItem(item);
                }
            }
        }

        @Override
        public boolean hasChanged() {
            return false;
        }

        @Override
        public void apply() {

        }

        @Override
        public DocumentItem getDocItem() {
            return null;
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
            return this.isUpdate;
        }
    }

    /**
     * 三维 点云 图层的数据源属性
     */
    public static class PointCloudDataSource3DProperty implements IProperty {
        private boolean isUpdate = false;//是否立即更新
        protected boolean hasChanged = false;
        private Rect3DBound dataScope;//数据范围
        private String dataSrcUrl = "";//数据源
        private PropertyItem[] items;
        private PropertySheet propertySheet;

        @Override
        public void setDocItem(DocumentItem item) {
            if (item instanceof PointCloudLayer) {
                dataScope = new Rect3DBound(((PointCloudLayer) item).get3DRange());
                this.dataSrcUrl = ((PointCloudLayer) item).getURL();
                items = new PropertyItem[2];
                items[0] = new PropertyItem("数据源范围", "数据源范围", "常规", true,
                        String.class, PopupPropertyEditor.class);
                items[0].setValue(dataScope);
                items[1] = new PropertyItem("数据源", "数据源", "常规", false, String.class, null);
                items[1].setValue(this.dataSrcUrl);
                for (PropertyItem propertyItem :
                        items) {
                    propertyItem.setDocumentItem(item);
                }
            }
        }

        @Override
        public boolean hasChanged() {
            return false;
        }

        @Override
        public void apply() {

        }

        @Override
        public DocumentItem getDocItem() {
            return null;
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
            return this.isUpdate;
        }
    }
    //endregion

    /**
     * 临时通用属性
     */
    public static class Layer3DCommonProperty3 implements IProperty {
        protected boolean isUpdate = false;//是否立即更新
        protected boolean hasChanged = false;
        private PropertyItem[] items;
        private PropertySheet propertySheet;
        protected SimpleBooleanProperty isUIUpdate;

        public Layer3DCommonProperty3() {
            isUIUpdate = new SimpleBooleanProperty(this, "isUIUpdate", true);
            isUIUpdate.addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (propertySheet != null && isUIUpdate.get()) {
                        ArrayList<PropertyItem> newItems = new ArrayList<>();
                        for (int i = 0; i < items.length; i++) {
                            if (items[i].isVisible()) {
                                newItems.add(items[i]);
                            }
                        }
                        if (propertySheet != null && isUIUpdate.get()) {
                            propertySheet.getItems().setAll(newItems);
                        }
                    }
                }
            });
        }

        public void beginUpdate() {
            isUIUpdate.set(false);
        }

        public void endUpdate() {
            isUIUpdate.set(true);
        }

        @Override
        public void setDocItem(DocumentItem item) {
            items = new PropertyItem[0];
        }

        @Override
        public boolean hasChanged() {
            return false;
        }

        @Override
        public void apply() {

        }

        @Override
        public DocumentItem getDocItem() {
            return null;
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
            return this.isUpdate;
        }
    }

    /**
     * 三维图层 通用属性(常规)属性(有参考系）
     */
    public static class Layer3DCommonProperty implements IProperty {
        protected boolean isUpdate = false;//是否立即更新
        protected boolean hasChanged = false;
        private PropertyItem[] items;
        private PropertySheet propertySheet;
        private DocumentItem item = null;
        private String layerName = "";
        private LayerState layerState = LayerState.UnVisible;
        private String layerType = "";
        private SRefData sRefData = new SRefData();

        protected SimpleBooleanProperty isUIUpdate;

        public Layer3DCommonProperty() {
            isUIUpdate = new SimpleBooleanProperty(this, "isUIUpdate", true);
            isUIUpdate.addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (propertySheet != null && isUIUpdate.get()) {
                        ArrayList<PropertyItem> newItems = new ArrayList<>();
                        for (int i = 0; i < items.length; i++) {
                            if (items[i].isVisible()) {
                                newItems.add(items[i]);
                            }
                        }
                        if (propertySheet != null && isUIUpdate.get()) {
                            propertySheet.getItems().setAll(newItems);
                        }
                    }
                }
            });
        }

        public void beginUpdate() {
            isUIUpdate.set(false);
        }

        public void endUpdate() {
            isUIUpdate.set(true);
        }

        @Override
        public void apply() {
            if (!this.isUpdate && hasChanged) {
                if (this.item instanceof Map3DLayer) {
                    ((Map3DLayer) this.item).setName(this.layerName);
                    ((Map3DLayer) this.item).setState(layerState);
                    ((Map3DLayer) this.item).setSrefInfo(this.sRefData);
                }
            }
        }

        @Override
        public void setDocItem(DocumentItem item) {
            if (item instanceof Map3DLayer) {
                this.item = item;
                this.layerName = ((Map3DLayer) this.item).getName();
                this.layerType = LanguageConvert.layerType3DConvert(((Map3DLayer) this.item).getType());
                this.layerState = ((Map3DLayer) this.item).getState();
                this.sRefData = ((Map3DLayer) this.item).getSrefInfo();
                items = new PropertyItem[4];
                items[0] = new PropertyItem("名称", "名称", "图层", true, String.class, null);
                items[0].setValue(layerName);
                if (items[0].getObservableValue().isPresent()) {
                    items[0].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            hasChanged = true;
                            layerName = (String) newValue;
                            if (isUpdate) {
                                if (item instanceof MapLayer) {
                                    ((MapLayer) item).setName(layerName);
                                }
                            }
                        }
                    });
                }
                items[1] = new PropertyItem("类型", "类型", "图层", false, String.class, null);
                items[1].setValue(layerType);
                items[2] = new PropertyItem("状态", "状态", "图层", true, String.class,
                        ComboBoxPropertyEditor.class);
                items[2].setValue(layerState);
                if (items[2].getObservableValue().isPresent()) {
                    items[2].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            hasChanged = true;
                            layerState = (LayerState) newValue;
                            if (isUpdate) {
                                if (item instanceof Map3DLayer) {
                                    ((Map3DLayer) item).setState(layerState);
                                }
                            }
                        }
                    });
                }
                items[3] = new PropertyItem("参照系", "参照系", "图层", true, String.class,
                        SRefDataPropertyEditor.class);
                items[3].setValue(sRefData);
                if (items[3].getObservableValue().isPresent()) {
                    items[3].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            if (newValue instanceof SRefData) {
                                sRefData = (SRefData) newValue;
                                hasChanged = true;
                                if (isUpdate) {
                                    items[3].setValue(sRefData);
                                    ((Map3DLayer) item).setSrefInfo(sRefData);
                                }
                            }
                        }
                    });
                }
            }
        }

        @Override
        public boolean hasChanged() {
            return false;
        }

        @Override
        public DocumentItem getDocItem() {
            return null;
        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return items;
        }

        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
                AddUnitedSetting(items, item, propertySheet, isUIUpdate);
            }
            return propertySheet;
        }

        @Override
        public void setImmediatelyUpdate(boolean isUpdate) {
            this.isUpdate = isUpdate;
        }

        @Override
        public boolean isImmediatelyUpdate() {
            return this.isUpdate;
        }
    }

    /**
     * 三维图层 通用属性(常规)属性(无参考系）
     */
    public static class Layer3DCommonProperty1 implements IProperty {
        protected boolean isUpdate = false;//是否立即更新
        protected boolean hasChanged = false;
        private PropertyItem[] items;
        private PropertySheet propertySheet;
        private DocumentItem item = null;
        private String layerName = "";
        private LayerState layerState = LayerState.UnVisible;
        private String layerType = "";
        protected SimpleBooleanProperty isUIUpdate;

        public Layer3DCommonProperty1() {
            isUIUpdate = new SimpleBooleanProperty(this, "isUIUpdate", true);
            isUIUpdate.addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (propertySheet != null && isUIUpdate.get()) {
                        ArrayList<PropertyItem> newItems = new ArrayList<>();
                        for (int i = 0; i < items.length; i++) {
                            if (items[i].isVisible()) {
                                newItems.add(items[i]);
                            }
                        }
                        if (propertySheet != null && isUIUpdate.get()) {
                            propertySheet.getItems().setAll(newItems);
                        }
                    }
                }
            });
        }

        public void beginUpdate() {
            isUIUpdate.set(false);
        }

        public void endUpdate() {
            isUIUpdate.set(true);
        }

        @Override
        public void apply() {
            if (!this.isUpdate && hasChanged) {
                if (this.item instanceof Map3DLayer) {
                    ((Map3DLayer) this.item).setName(this.layerName);
                    ((Map3DLayer) this.item).setState(layerState);
                }
            }
        }

        @Override
        public void setDocItem(DocumentItem item) {
            if (item instanceof Map3DLayer) {
                this.item = item;
                this.layerName = ((Map3DLayer) this.item).getName();
                this.layerType = LanguageConvert.layerType3DConvert(((Map3DLayer) this.item).getType());
                this.layerState = ((Map3DLayer) this.item).getState();
                items = new PropertyItem[4];
                items[0] = new PropertyItem("名称", "名称", "图层", true, String.class, null);
                items[0].setValue(layerName);
                if (items[0].getObservableValue().isPresent()) {
                    items[0].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            hasChanged = true;
                            layerName = (String) newValue;
                            if (isUpdate) {
                                if (item instanceof MapLayer) {
                                    ((MapLayer) item).setName(layerName);
                                }
                            }
                        }
                    });
                }
                items[1] = new PropertyItem("类型", "类型", "图层", false, String.class, null);
                items[1].setValue(layerType);
                items[2] = new PropertyItem("状态", "状态", "图层", true, String.class,
                        ComboBoxPropertyEditor.class);
                items[2].setValue(layerState);
                if (items[2].getObservableValue().isPresent()) {
                    items[2].getObservableValue().get().addListener(new ChangeListener<Object>() {
                        @Override
                        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                            hasChanged = true;
                            layerState = (LayerState) newValue;
                            if (isUpdate) {
                                if (item instanceof Map3DLayer) {
                                    ((Map3DLayer) item).setState(layerState);
                                }
                            }
                        }
                    });
                }
            }
        }

        @Override
        public boolean hasChanged() {
            return false;
        }

        @Override
        public DocumentItem getDocItem() {
            return null;
        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return items;
        }

        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
                AddUnitedSetting(items, item, propertySheet, isUIUpdate);
            }
            return propertySheet;
        }

        @Override
        public void setImmediatelyUpdate(boolean isUpdate) {
            this.isUpdate = isUpdate;
        }

        @Override
        public boolean isImmediatelyUpdate() {
            return this.isUpdate;
        }
    }

    /**
     * 三维 模型 图层的通用属性(常规)属性
     */
    public static class ModelLayerProperty extends Layer3DCommonProperty3 {
        private PropertyItem[] items;
        private PropertySheet propertySheet;
        private DocumentItem item = null;
        private SystemLibrary systemLib = new SystemLibrary();//系统库
        private int transparency = 0;//透明度
        private Dot3D dot3d = new Dot3D(1, 1, 1);//显示比例
        private boolean bDisp = false;//显示场景包围盒
        private boolean bDispCridNet = false;//显示场景坐标网格
        public boolean dispTriGrid = false;//显示三角网
        private ModelRenderType mrType = ModelRenderType.RenderCommon;//模型层渲染类型
        boolean isGrid = mrType == ModelRenderType.RenderGrid;
        private long gridSizeX = 1;//模型层分块网格大小
        private long gridSizeY = 1;//模型层分块网格大小
        private double[] lodDistance = null;//LOD各级显示距离
        private String cachePath = "";//缓存索引文件路径
        private boolean isSetMaxMinDispLen = false;//设置最远最近显示距离(获取远近距离显示控制标志)
        private double maxDispLen = 0;//最远显示距离
        private double minDispLen = 0;//最近显示距离
        private double minDispPix = 0;//最小显示像素
        private boolean isShowOutSideLine = false;//是否显示轮廓线
        private int outSideLineColor = 0;//轮廓线颜色
        private float outSideLineRadius = 1;//轮廓线半径

        public ModelLayerProperty() {
            isUIUpdate = new SimpleBooleanProperty(this, "isUIUpdate", true);
            isUIUpdate.addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (propertySheet != null && isUIUpdate.get()) {
                        ArrayList<PropertyItem> newItems = new ArrayList<>();
                        for (int i = 0; i < items.length; i++) {
                            if (items[i].isVisible()) {
                                newItems.add(items[i]);
                            }
                        }
                        if (propertySheet != null && isUIUpdate.get()) {
                            propertySheet.getItems().setAll(newItems);
                        }
                    }
                }
            });
        }

        @Override
        public void apply() {
            super.apply();
            if (!this.isUpdate && hasChanged) {
                if (this.item instanceof ModelLayer) {
                    ((ModelLayer) item).setSysLibrary(systemLib);
                    ((ModelLayer) item).setTransparency((short) transparency);
                    ((ModelLayer) item).setScale(dot3d);
                    ((ModelLayer) item).setDispScenceBox(bDisp);
                    ((ModelLayer) item).setDispCridNet(bDispCridNet);
                    ((ModelLayer) item).setRenderType(mrType);
                    ((ModelLayer) item).setRenderGridSize(this.gridSizeX, this.gridSizeY);
                    ((ModelLayer) item).setMinMaxDispLen(this.isSetMaxMinDispLen);
                    ((ModelLayer) item).setMaxDispLen(this.maxDispLen);
                    ((ModelLayer) item).setMinDispLen(this.minDispLen);
                    ((ModelLayer) item).setLodList(this.lodDistance);
                    ((ModelLayer) item).setMinDispPix(this.minDispPix);
                    ((ModelLayer) item).setCachePath(this.cachePath);
                    ((ModelLayer) item).setTriangleDisply(this.dispTriGrid);

//                    ((ModelLayer) item).setShowOutSideLine(this.isShowOutSideLine, this.outSideLineColor, this.outSideLineRadius);
                }
            }
        }

        @Override
        public void setDocItem(DocumentItem item) {
            super.setDocItem(item);
            this.item = item;
            if (item instanceof ModelLayer) {

                //region 初始化字段
                this.systemLib = ((ModelLayer) item).getSysLibrary();
                transparency = ((ModelLayer) item).getTransparency();
                dot3d = ((ModelLayer) item).getScale();
                bDisp = ((ModelLayer) item).isDispScenceBox();
                bDispCridNet = ((ModelLayer) item).isDispCridNet();
                mrType = ((ModelLayer) item).getRenderType();
                isGrid = mrType == ModelRenderType.RenderGrid;
                ((ModelLayer) item).getRenderGridSize((int) this.gridSizeX, (int) this.gridSizeY);
                this.isSetMaxMinDispLen = ((ModelLayer) item).isSetMaxMinDispLen();
                this.maxDispLen = ((ModelLayer) item).getMaxDispLen();
                this.minDispLen = ((ModelLayer) item).getMinDispLen();
                this.lodDistance = ((ModelLayer) item).getLodList();
                this.minDispPix = ((ModelLayer) item).getMinDispPix();
                this.cachePath = ((ModelLayer) item).getCachePath();
                this.dispTriGrid = ((ModelLayer) item).getTriangleDisply();

                //TODO getShowOutSideLine接口引起界面异常
//                this.isShowOutSideLine = ((ModelLayer) item).getShowOutSideLine(this.outSideLineColor, this.outSideLineRadius);
                //endregion

                //region 创建items
                PropertyItem[] oldItems = super.getPropertyItems();
                items = new PropertyItem[oldItems.length + 14];
                System.arraycopy(oldItems, 0, items, 0, oldItems.length);
                items[oldItems.length] = new PropertyItem("系统库", "系统库", "常规", true, SystemLibrary.class,
                        SystemLibComboBoxEditor.class);
                items[oldItems.length].setValue(this.systemLib);
                items[oldItems.length].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof SystemLibrary) {
                            systemLib = (SystemLibrary) newValue;
                            hasChanged = true;
                        }
                        if (hasChanged && isUpdate) {
                            ((ModelLayer) item).setSysLibrary(systemLib);

                        }
                    }
                });
                items[oldItems.length + 1] = new PropertyItem("透明度", "透明度", "常规", true, Integer.class, null);
                items[oldItems.length + 1].setValue(this.transparency);
                items[oldItems.length + 1].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Integer) {
                            transparency = (int) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((ModelLayer) item).setTransparency((short) transparency);
                        }
                    }
                });
                items[oldItems.length + 2] = new PropertyItem("显示比例", "显示比例", "常规", true, String.class, Dot3DEditor.class);
                items[oldItems.length + 2].setValue(dot3d);
                items[oldItems.length + 2].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Dot3D) {
                            dot3d = (Dot3D)newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((ModelLayer) item).setScale(dot3d);
                        }
                    }
                });
                items[oldItems.length + 3] = new PropertyItem("显示场景包围盒", "显示场景包围盒", "常规", true, Boolean.class, null);
                items[oldItems.length + 3].setValue(bDisp);
                items[oldItems.length + 3].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Boolean) {
                            bDisp = (boolean) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((ModelLayer) item).setDispScenceBox(bDisp);
                        }
                    }
                });
                items[oldItems.length + 4] = new PropertyItem("显示场景坐标网格", "显示场景坐标网格", "常规", true, Boolean.class, null);
                items[oldItems.length + 4].setValue(bDispCridNet);
                items[oldItems.length + 4].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Boolean) {
                            bDispCridNet = (boolean) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((ModelLayer) item).setDispCridNet(bDispCridNet);
                        }
                    }
                });
                items[oldItems.length + 5] = new PropertyItem("显示三角网", "显示三角网", "常规", true, Boolean.class, null);
                items[oldItems.length + 5].setValue(dispTriGrid);
                items[oldItems.length + 5].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Boolean) {
                            dispTriGrid = (boolean) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((ModelLayer) item).setTriangleDisply(dispTriGrid);
                        }
                    }
                });
                items[oldItems.length + 6] = new PropertyItem("渲染方式", "渲染方式", "常规", true, String.class,
                        ComboBoxPropertyEditor.class);
                items[oldItems.length + 6].setValue(mrType);
                items[oldItems.length + 6].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof ModelRenderType) {
                            mrType = (ModelRenderType) newValue;
                            isGrid = mrType == ModelRenderType.RenderGrid;
                            beginUpdate();
                            items[oldItems.length + 7].setEditable(isGrid);
                            items[oldItems.length + 8].setEditable(isGrid);
                            items[oldItems.length + 9].setEditable(isGrid);
                            endUpdate();
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((ModelLayer) item).setRenderType(mrType);
                        }
                    }
                });
                items[oldItems.length + 7] = new PropertyItem("分块行大小", "分块行大小", "常规", isGrid, Long.class, null);
                items[oldItems.length + 7].setValue(gridSizeX);
                items[oldItems.length + 7].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Long) {
                            gridSizeX = (long) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((ModelLayer) item).setRenderGridSize(gridSizeX, gridSizeY);
                        }
                    }
                });
                items[oldItems.length + 8] = new PropertyItem("分块列大小", "分块列大小", "常规", isGrid, Long.class, null);
                items[oldItems.length + 8].setValue(gridSizeY);
                items[oldItems.length + 8].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Long) {
                            gridSizeY = (long) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((ModelLayer) item).setRenderGridSize(gridSizeX, gridSizeY);
                        }
                    }
                });
                items[oldItems.length + 9] = new PropertyItem("缓存索引文件路径", "缓存索引文件路径", "常规", isGrid, String.class, null);
                items[oldItems.length + 9].setValue(cachePath);
                items[oldItems.length + 9].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof String) {
                            cachePath = (String) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((ModelLayer) item).setCachePath(cachePath);
                        }
                    }
                });
                items[oldItems.length + 10] = new PropertyItem("设置最远最近显示距离", "设置最远最近显示距离", "常规", true, Boolean.class, null);
                items[oldItems.length + 10].setValue(isSetMaxMinDispLen);
                items[oldItems.length + 10].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Boolean) {
                            isSetMaxMinDispLen = (boolean) newValue;
                            beginUpdate();
                            items[oldItems.length + 11].setEditable(isSetMaxMinDispLen);
                            items[oldItems.length + 12].setEditable(isSetMaxMinDispLen);
                            endUpdate();
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((ModelLayer) item).setMinMaxDispLen(isSetMaxMinDispLen);
                        }
                    }
                });
                items[oldItems.length + 11] = new PropertyItem("最近显示距离", "最近显示距离", "常规", isSetMaxMinDispLen, Double.class, null);
                items[oldItems.length + 11].setValue(minDispLen);
                items[oldItems.length + 11].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Double) {
                            minDispLen = (double) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((ModelLayer) item).setMinDispLen(minDispLen);
                        }
                    }
                });
                items[oldItems.length + 12] = new PropertyItem("最远显示距离", "最远显示距离", "常规", isSetMaxMinDispLen, Double.class, null);
                items[oldItems.length + 12].setValue(maxDispLen);
                items[oldItems.length + 12].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Double) {
                            maxDispLen = (double) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((ModelLayer) item).setMaxDispLen(maxDispLen);
                        }
                    }
                });
                items[oldItems.length + 13] = new PropertyItem("最小显示像素", "最小显示像素", "常规", true, Double.class, null);
                items[oldItems.length + 13].setValue(minDispPix);
                items[oldItems.length + 13].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Double) {
                            minDispPix = (double) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((ModelLayer) item).setMinDispPix(minDispPix);
                        }
                    }
                });
//                items[oldItems.length + 14] = new PropertyItem("是否显示轮廓线", "是否显示轮廓线", "常规", true, Boolean.class, null);
//                items[oldItems.length + 14].setValue(isShowOutSideLine);
//                items[oldItems.length + 14].getObservableValue().get().addListener(new ChangeListener<Object>() {
//                    @Override
//                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
//                        if (newValue instanceof Boolean) {
//                            isShowOutSideLine = (boolean) newValue;
//                            beginUpdate();
//                            items[oldItems.length + 15].setEditable(isShowOutSideLine);
//                            items[oldItems.length + 16].setEditable(isShowOutSideLine);
//                            endUpdate();
//                            hasChanged = true;
//                        }
//                        if (isUpdate && hasChanged) {
//                            ((ModelLayer) item).setShowOutSideLine(isShowOutSideLine, outSideLineColor, outSideLineRadius);
//                        }
//                    }
//                });
//                items[oldItems.length + 15] = new PropertyItem("轮廓线颜色", "轮廓线颜色", "常规", isShowOutSideLine, Integer.class, null);
//                items[oldItems.length + 15].setValue(outSideLineColor);
//                items[oldItems.length + 15].getObservableValue().get().addListener(new ChangeListener<Object>() {
//                    @Override
//                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
//                        if (newValue instanceof Integer) {
//                            outSideLineColor = (int) newValue;
//                            hasChanged = true;
//                        }
//                        if (isUpdate && hasChanged) {
////                            ((ModelLayer) item).setShowOutSideLine(isShowOutSideLine, outSideLineColor, outSideLineRadius);
//                        }
//                    }
//                });
//                items[oldItems.length + 16] = new PropertyItem("轮廓线半径", "轮廓线半径", "常规", isShowOutSideLine, Float.class, null);
//                items[oldItems.length + 16].setValue(outSideLineRadius);
//                items[oldItems.length + 16].getObservableValue().get().addListener(new ChangeListener<Object>() {
//                    @Override
//                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
//                        if (newValue instanceof Float) {
//                            outSideLineRadius = (float) newValue;
//                            hasChanged = true;
//                        }
//                        if (isUpdate && hasChanged) {
//                            ((ModelLayer) item).setShowOutSideLine(isShowOutSideLine, outSideLineColor, outSideLineRadius);
//                        }
//                    }
//                });
//                items[oldItems.length + 17] = new PropertyItem("LOD各级显示距离", "LOD各级显示距离", String_Diaplay, true, Integer.class, null);
//                items[oldItems.length + 17].setValue(lodDistance);
//                items[oldItems.length + 17].getObservableValue().get().addListener(new ChangeListener<Object>() {
//                    @Override
//                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
//                        transparency = (double[]) newValue;
//                        hasChanged = true;
//                        if (isUpdate) {
////                            ((ModelLayer) item).setTransparency(transparency);
//                        }
//                    }
//                });
                //endregion


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
                AddUnitedSetting(items, item, propertySheet, isUIUpdate);
            }
            return propertySheet;
        }
    }

    /**
     * 三维 矢量点 图层的通用属性(常规)属性
     */
    public static class Pnt3DLayerProperty extends Layer3DCommonProperty3 {
        private PropertyItem[] items;
        private PropertySheet propertySheet;
        private DocumentItem item = null;

        private SystemLibrary systemLib = new SystemLibrary();//系统库
        private Dot3D dot3d = new Dot3D(1, 1, 1);//显示比例
        private boolean isStretch = false;//是否拉伸
        private String heightExpr = "";//拉伸字段表达式
        private ModelRenderType mrType = ModelRenderType.RenderCommon;//渲染方式
        private boolean isGrid = mrType == ModelRenderType.RenderGrid;
        private int gridSizeX = 1;//分块行大小
        private int gridSizeY = 1;//分块列大小
        private boolean renderMode = false;//绘制方式：true矢量方式,false栅格绘制
        private RenderModeDef renderModeStr = renderMode ? RenderModeDef.RenderModeVector : RenderModeDef.RenderModeRaster;
        //        private String renderModeStr = renderMode ? "矢量绘制" : "栅格绘制";
        private boolean isPointCloudMode = false;//是否点云模式
        private double pointCloudSize = 0;//点云模式下点的大小
        private int boardOriginType = 1;//公告板位置
        private double autoScalePixel = 1;//自动缩放下的显示大小（像素）
        private double maxDistance = 1;//自动缩放的最大距离
        private double symbolScale = 1;//符号比率

        public Pnt3DLayerProperty() {
            isUIUpdate.addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (propertySheet != null && isUIUpdate.get()) {
                        ArrayList<PropertyItem> newItems = new ArrayList<>();
                        for (int i = 0; i < items.length; i++) {
                            if (items[i].isVisible()) {
                                newItems.add(items[i]);
                            }
                        }
                        if (propertySheet != null && isUIUpdate.get()) {
                            propertySheet.getItems().setAll(newItems);
                        }
                    }
                }
            });
        }

        @Override
        public void apply() {
            super.apply();
            if (!this.isUpdate && hasChanged) {
                if (this.item instanceof Vector3DLayer) {
                    ((Vector3DLayer) item).setSysLibrary(systemLib);
                    ((Vector3DLayer) item).setScale(this.dot3d);
                    ((Vector3DLayer) item).setGlobeStretch(this.isStretch);
                    ((Vector3DLayer) item).setStretchExpr(this.heightExpr);
                    ((Vector3DLayer) item).setRenderType(this.mrType);
                    ((Vector3DLayer) item).setRenderGridSize(this.gridSizeX, this.gridSizeY);
                    ((Vector3DLayer) item).setRenderMode(this.renderMode);
                    ((Vector3DLayer) item).setPointCloudMode(this.isPointCloudMode);
                    ((Vector3DLayer) item).setPointCloudSize(this.pointCloudSize);
                    ((Vector3DLayer) item).setBoardOrigin(this.boardOriginType);
                    ((Vector3DLayer) item).setBoardAutoPixelDistance(this.autoScalePixel, this.maxDistance);
                    ((Vector3DLayer) item).setProperty("SymbolScale", this.symbolScale);
                }
            }
        }

        @Override
        public void setDocItem(DocumentItem item) {
            super.setDocItem(item);
            this.item = item;
            if (item instanceof Vector3DLayer) {
                //region 初始化字段
                this.systemLib = ((Vector3DLayer) item).getSysLibrary();
                this.dot3d = ((Vector3DLayer) item).getScale();
                this.isStretch = ((Vector3DLayer) item).getGlobeStretch();
                this.heightExpr = ((Vector3DLayer) item).getStretchExpr();
                this.mrType = ((Vector3DLayer) item).getRenderType();
                isGrid = mrType == ModelRenderType.RenderGrid;

                ((Vector3DLayer) item).getRenderGridSize(this.gridSizeX, this.gridSizeY);
                this.renderMode = ((Vector3DLayer) item).getRenderMode();
                this.isPointCloudMode = ((Vector3DLayer) item).getPointCloudMode();
                this.pointCloudSize = ((Vector3DLayer) item).getPointCloudSize();
                this.boardOriginType = ((Vector3DLayer) item).getBoardOrigin();
                ((Vector3DLayer) item).getBoardAutoPixelDistance(this.autoScalePixel, this.maxDistance);
                Object obj = ((Vector3DLayer) item).getProperty("SymbolScale");
                if (obj instanceof Double) {
                    this.symbolScale = (double) obj;
                }
                //endregion

                //region 创建items
                PropertyItem[] oldItems = super.getPropertyItems();
                items = new PropertyItem[oldItems.length + 16];
                System.arraycopy(oldItems, 0, items, 0, oldItems.length);
                items[oldItems.length] = new PropertyItem("系统库", "系统库", "常规", true, SystemLibrary.class,
                        SystemLibComboBoxEditor.class);
                items[oldItems.length].setValue(this.systemLib);
                items[oldItems.length].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof SystemLibrary) {
                            systemLib = (SystemLibrary) newValue;
                            hasChanged = true;
                        }
                        if (hasChanged && isUpdate) {
                            ((Vector3DLayer) item).setSystemLibID(systemLib.getSysLibGuid());
                        }
                    }
                });

                items[oldItems.length + 1] = new PropertyItem("显示比例", "显示比例", "常规", true, String.class, Dot3DEditor.class);
                items[oldItems.length + 1].setValue(this.dot3d);
                items[oldItems.length + 1].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Dot3D) {
                            dot3d = (Dot3D) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((Vector3DLayer) item).setScale(dot3d);
                        }
                    }
                });
                items[oldItems.length + 2] = new PropertyItem("是否拉伸", "是否拉伸", "常规", true, Boolean.class, null);
                items[oldItems.length + 2].setValue(isStretch);
                items[oldItems.length + 2].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Boolean) {
                            isStretch = (boolean) newValue;
                            items[oldItems.length + 3].setEditable(isStretch);
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((Vector3DLayer) item).setGlobeStretch(isStretch);
                        }
                    }
                });
                items[oldItems.length + 3] = new PropertyItem("拉伸字段表达式", "拉伸字段表达式", "常规", isStretch, String.class, null);
                items[oldItems.length + 3].setValue(heightExpr);
                items[oldItems.length + 3].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof String) {
                            heightExpr = (String) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((Vector3DLayer) item).setStretchExpr(heightExpr);
                        }
                    }
                });

                //TODO接口有问题，暂时不可编辑
                items[oldItems.length + 4] = new PropertyItem("渲染方式", "渲染方式", "常规", false, String.class,
                        ComboBoxPropertyEditor.class);
                items[oldItems.length + 4].setValue(mrType);
                items[oldItems.length + 4].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof ModelRenderType) {
                            mrType = (ModelRenderType) newValue;
                            isGrid = mrType == ModelRenderType.RenderGrid;
                            items[oldItems.length + 5].setEditable(isGrid);
                            items[oldItems.length + 6].setEditable(isGrid);
                            if (isGrid) {
                                renderMode = true;
                                isPointCloudMode = true;
                                renderModeStr = renderMode ? RenderModeDef.RenderModeVector : RenderModeDef.RenderModeRaster;
                                items[oldItems.length + 7].setValue(renderModeStr);
                                items[oldItems.length + 8].setValue(isPointCloudMode);
                            }
                            items[oldItems.length + 7].setEditable(!isGrid);
                            items[oldItems.length + 8].setEditable(!isGrid);
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((Vector3DLayer) item).setRenderType(mrType);
                        }
                    }
                });
                items[oldItems.length + 5] = new PropertyItem("分块行大小", "分块行大小", "常规", isGrid, Integer.class, null);
                items[oldItems.length + 5].setValue(gridSizeX);
                items[oldItems.length + 5].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Integer) {
                            gridSizeX = (int) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((Vector3DLayer) item).setRenderGridSize(gridSizeX, gridSizeY);
                        }
                    }
                });
                items[oldItems.length + 6] = new PropertyItem("分块列大小", "分块列大小", "常规", isGrid, Integer.class, null);
                items[oldItems.length + 6].setValue(gridSizeY);
                items[oldItems.length + 6].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Integer) {
                            gridSizeY = (int) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((Vector3DLayer) item).setRenderGridSize(gridSizeX, gridSizeY);
                        }
                    }
                });
                items[oldItems.length + 7] = new PropertyItem("绘制方式", "绘制方式", "常规", !isGrid, String.class, ComboBoxPropertyEditor.class);
                items[oldItems.length + 7].setValue(renderModeStr);
                items[oldItems.length + 7].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof RenderModeDef) {
                            renderModeStr = (RenderModeDef) newValue;
                            renderMode = renderModeStr == RenderModeDef.RenderModeVector;
                            items[oldItems.length + 8].setEditable(renderMode);
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((Vector3DLayer) item).setRenderMode(renderMode);
                        }
                    }
                });
                items[oldItems.length + 8] = new PropertyItem("点云模式", "点云模式", "常规", (!isGrid) && renderMode, Boolean.class, null);
                items[oldItems.length + 8].setValue(isPointCloudMode);
                items[oldItems.length + 8].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Boolean) {
                            isPointCloudMode = (boolean) newValue;
                            items[oldItems.length + 9].setEditable(isPointCloudMode);
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((Vector3DLayer) item).setPointCloudMode(isPointCloudMode);
                        }
                    }
                });
                items[oldItems.length + 9] = new PropertyItem("点的大小", "点云模式下点的大小", "常规", isPointCloudMode, Double.class, null);
                items[oldItems.length + 9].setValue(pointCloudSize);
                items[oldItems.length + 9].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Double) {
                            pointCloudSize = (double) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((Vector3DLayer) item).setPointCloudSize(pointCloudSize);
                        }
                    }
                });
                items[oldItems.length + 12] = new PropertyItem("公告板位置", "公告板位置", "常规", true, Integer.class, null);
                items[oldItems.length + 12].setValue(boardOriginType);
                items[oldItems.length + 12].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Integer) {
                            boardOriginType = (int) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((Vector3DLayer) item).setBoardOrigin(boardOriginType);
                        }
                    }
                });
                items[oldItems.length + 13] = new PropertyItem("点的大小", "自动缩放下点的大小", "常规", true, Double.class, null);
                items[oldItems.length + 13].setValue(autoScalePixel);
                items[oldItems.length + 13].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Double) {
                            autoScalePixel = (double) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((Vector3DLayer) item).setBoardAutoPixelDistance(autoScalePixel, maxDistance);
                        }
                    }
                });
                items[oldItems.length + 14] = new PropertyItem("最大距离", "自动缩放的最大距离", "常规", true, Double.class, null);
                items[oldItems.length + 14].setValue(maxDistance);
                items[oldItems.length + 14].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Double) {
                            maxDistance = (double) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((Vector3DLayer) item).setBoardAutoPixelDistance(autoScalePixel, maxDistance);
                        }
                    }
                });
                items[oldItems.length + 15] = new PropertyItem("符号比率", "符号比率", "常规", true, Double.class, null);
                items[oldItems.length + 15].setValue(symbolScale);
                items[oldItems.length + 15].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Double) {
                            symbolScale = (int) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((Vector3DLayer) item).setProperty("SymbolScale", symbolScale);
                        }
                    }
                });
                //endregion
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
                AddUnitedSetting(items, item, propertySheet, isUIUpdate);
            }
            return propertySheet;
        }
    }

    /**
     * 三维 矢量点 图层的通用属性(常规)属性
     */
    public static class Lin3DLayerProperty extends Layer3DCommonProperty3 {
        private PropertyItem[] items;
        private PropertySheet propertySheet;
        private DocumentItem item = null;

        private SystemLibrary systemLib = new SystemLibrary();//系统库
        private Dot3D dot3d = new Dot3D(1, 1, 1);//显示比例
        //        private boolean isStretch = false;//是否拉伸
//        private String heightExpr = "";//拉伸字段表达式
        private ModelRenderType mrType = ModelRenderType.RenderCommon;//渲染方式
        private boolean isGrid = mrType == ModelRenderType.RenderGrid;
        private int gridSizeX = 1;//分块行大小
        private int gridSizeY = 1;//分块列大小
        private boolean renderMode = false;//绘制方式：true矢量方式,false栅格绘制
        private RenderModeDef renderModeStr = renderMode ? RenderModeDef.RenderModeVector : RenderModeDef.RenderModeRaster;
        //        private String renderModeStr = renderMode ? "矢量绘制" : "栅格绘制";
        private boolean isPointCloudMode = false;//是否点云模式
        private double pointCloudSize = 0;//点云模式下点的大小


        public Lin3DLayerProperty() {
            isUIUpdate.addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (propertySheet != null && isUIUpdate.get()) {
                        ArrayList<PropertyItem> newItems = new ArrayList<>();
                        for (int i = 0; i < items.length; i++) {
                            if (items[i].isVisible()) {
                                newItems.add(items[i]);
                            }
                        }
                        if (propertySheet != null && isUIUpdate.get()) {
                            propertySheet.getItems().setAll(newItems);
                        }
                    }
                }
            });
        }

        @Override
        public void apply() {
            super.apply();
            if (!this.isUpdate && hasChanged) {
                if (this.item instanceof Vector3DLayer) {
                    ((Vector3DLayer) item).setSysLibrary(this.systemLib);
                    ((Vector3DLayer) item).setScale(this.dot3d);
                    ((Vector3DLayer) item).setRenderType(this.mrType);
                    ((Vector3DLayer) item).setRenderGridSize(this.gridSizeX, this.gridSizeY);
                    ((Vector3DLayer) item).setRenderMode(this.renderMode);
                    ((Vector3DLayer) item).setPointCloudMode(this.isPointCloudMode);
                    ((Vector3DLayer) item).setPointCloudSize(this.pointCloudSize);
                }
            }
        }

        @Override
        public void setDocItem(DocumentItem item) {
            super.setDocItem(item);
            this.item = item;
            if (item instanceof Vector3DLayer) {
                //region 初始化字段
                this.systemLib = ((Vector3DLayer) item).getSysLibrary();
                this.dot3d = ((Vector3DLayer) item).getScale();
                this.mrType = ((Vector3DLayer) item).getRenderType();
                ((Vector3DLayer) item).getRenderGridSize(this.gridSizeX, this.gridSizeY);
                this.renderMode = ((Vector3DLayer) item).getRenderMode();
                this.isPointCloudMode = ((Vector3DLayer) item).getPointCloudMode();
                //TODO getPointCloudSize 接口参数不对
//                this.pointCloudSize = ((Vector3DLayer) item).getPointCloudSize();
                //endregion

                //region 创建items
                PropertyItem[] oldItems = super.getPropertyItems();
                items = new PropertyItem[oldItems.length + 8];
                System.arraycopy(oldItems, 0, items, 0, oldItems.length);
                items[oldItems.length] = new PropertyItem("系统库", "系统库", "常规", true, SystemLibrary.class,
                        SystemLibComboBoxEditor.class);
                items[oldItems.length].setValue(this.systemLib);
                items[oldItems.length].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof SystemLibrary) {
                            systemLib = (SystemLibrary) newValue;
                            hasChanged = true;
                        }
                        if (hasChanged && isUpdate) {
                            ((Vector3DLayer) item).setSystemLibID(systemLib.getSysLibGuid());
                        }
                    }
                });
                items[oldItems.length + 1] = new PropertyItem("显示比例", "显示比例", "常规", true, String.class, Dot3DEditor.class);
                items[oldItems.length + 1].setValue(this.dot3d);
                items[oldItems.length + 1].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Dot3D) {
                            dot3d = (Dot3D) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((Vector3DLayer) item).setScale(dot3d);
                        }
                    }
                });
                items[oldItems.length + 2] = new PropertyItem("渲染方式", "渲染方式", "常规", false, String.class,
                        ComboBoxPropertyEditor.class);
                items[oldItems.length + 2].setValue(mrType);
                items[oldItems.length + 2].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof ModelRenderType) {
                            mrType = (ModelRenderType) newValue;
                            isGrid = mrType == ModelRenderType.RenderGrid;
                            items[oldItems.length + 3].setEditable(isGrid);
                            items[oldItems.length + 4].setEditable(isGrid);
                            if (isGrid) {
                                renderMode = true;
                                isPointCloudMode = true;
//                                renderModeStr = renderMode ? "矢量绘制" : "栅格绘制";
                                renderModeStr = renderMode ? RenderModeDef.RenderModeVector : RenderModeDef.RenderModeRaster;
                                items[oldItems.length + 5].setValue(renderModeStr);
                                items[oldItems.length + 6].setValue(isPointCloudMode);
                            }
                            items[oldItems.length + 5].setEditable(!isGrid);
                            items[oldItems.length + 6].setEditable((!isGrid) && renderMode);
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((Vector3DLayer) item).setRenderType(mrType);
                        }
                    }
                });
                items[oldItems.length + 3] = new PropertyItem("分块行大小", "分块行大小", "常规", isGrid, Integer.class, null);
                items[oldItems.length + 3].setValue(gridSizeX);
                items[oldItems.length + 3].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Integer) {
                            gridSizeX = (int) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((Vector3DLayer) item).setRenderGridSize(gridSizeX, gridSizeY);
                        }
                    }
                });
                items[oldItems.length + 4] = new PropertyItem("分块列大小", "分块列大小", "常规", isGrid, Integer.class, null);
                items[oldItems.length + 4].setValue(gridSizeY);
                items[oldItems.length + 4].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Integer) {
                            gridSizeY = (int) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((Vector3DLayer) item).setRenderGridSize(gridSizeX, gridSizeY);
                        }
                    }
                });
                items[oldItems.length + 5] = new PropertyItem("绘制方式", "绘制方式", "常规", !isGrid, String.class, ComboBoxPropertyEditor.class);
                items[oldItems.length + 5].setValue(renderModeStr);
                items[oldItems.length + 5].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof RenderModeDef) {
                            renderModeStr = (RenderModeDef) newValue;
                            renderMode = renderModeStr == RenderModeDef.RenderModeVector;
                            items[oldItems.length + 6].setEditable(renderMode);
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((Vector3DLayer) item).setRenderMode(renderMode);
                        }
                    }
                });
                items[oldItems.length + 6] = new PropertyItem("点云模式", "点云模式", "常规", (!isGrid) && renderMode, Boolean.class, null);
                items[oldItems.length + 6].setValue(isPointCloudMode);
                items[oldItems.length + 6].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Boolean) {
                            isPointCloudMode = (boolean) newValue;
                            items[oldItems.length + 7].setEditable(isPointCloudMode);
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((Vector3DLayer) item).setPointCloudMode(isPointCloudMode);
                        }
                    }
                });
                items[oldItems.length + 7] = new PropertyItem("线粗", "点云模式下线的粗细", "常规", isPointCloudMode, Double.class, null);
                items[oldItems.length + 7].setValue(pointCloudSize);
                items[oldItems.length + 7].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Double) {
                            pointCloudSize = (double) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((Vector3DLayer) item).setPointCloudSize(pointCloudSize);
                        }
                    }
                });
                //endregion
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
                AddUnitedSetting(items, item, propertySheet, isUIUpdate);
            }
            return propertySheet;
        }
    }

    /**
     * 三维 矢量点 图层的通用属性(常规)属性
     */
    public static class Reg3DLayerProperty extends Layer3DCommonProperty3 {
        private PropertyItem[] items;
        private PropertySheet propertySheet;
        private DocumentItem item = null;

        private SystemLibrary systemLib = new SystemLibrary();//系统库
        private Dot3D dot3d = new Dot3D(1, 1, 1);//显示比例
        private boolean isStretch = false;//是否拉伸
        private String heightExpr = "";//拉伸字段表达式
        private ModelRenderType mrType = ModelRenderType.RenderCommon;//渲染方式
        private boolean isGrid = mrType == ModelRenderType.RenderGrid;
        private int gridSizeX = 1;//分块行大小
        private int gridSizeY = 1;//分块列大小
        private boolean renderMode = false;//绘制方式：true矢量方式,false栅格绘制
        private RenderModeDef renderModeStr = renderMode ? RenderModeDef.RenderModeVector : RenderModeDef.RenderModeRaster;
        private boolean isPointCloudMode = false;//是否点云模式
        private double pointCloudSize = 0;//点云模式下点的大小
        private int boardOriginType = 1;//公告板位置
        private double autoScalePixel = 1;//自动缩放下的显示大小（像素）
        private double maxDistance = 1;//自动缩放的最大距离
        private double symbolScale = 1;//符号比率

        public Reg3DLayerProperty() {
            isUIUpdate.addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (propertySheet != null && isUIUpdate.get()) {
                        ArrayList<PropertyItem> newItems = new ArrayList<>();
                        for (int i = 0; i < items.length; i++) {
                            if (items[i].isVisible()) {
                                newItems.add(items[i]);
                            }
                        }
                        if (propertySheet != null && isUIUpdate.get()) {
                            propertySheet.getItems().setAll(newItems);
                        }
                    }
                }
            });
        }

        @Override
        public void apply() {
            super.apply();
            if (!this.isUpdate && hasChanged) {
                if (this.item instanceof Vector3DLayer) {
                    ((Vector3DLayer) item).setSysLibrary(this.systemLib);
                    ((Vector3DLayer) item).setScale(this.dot3d);
                    ((Vector3DLayer) item).setGlobeStretch(this.isStretch);
                    ((Vector3DLayer) item).setStretchExpr(this.heightExpr);
                    ((Vector3DLayer) item).setRenderType(this.mrType);
                    ((Vector3DLayer) item).setRenderGridSize(this.gridSizeX, this.gridSizeY);
                    ((Vector3DLayer) item).setRenderMode(this.renderMode);
                    ((Vector3DLayer) item).setPointCloudMode(this.isPointCloudMode);
                    ((Vector3DLayer) item).setPointCloudSize(this.pointCloudSize);
                    ((Vector3DLayer) item).setBoardOrigin(this.boardOriginType);
                    ((Vector3DLayer) item).setBoardAutoPixelDistance(this.autoScalePixel, this.maxDistance);
                    ((Vector3DLayer) item).setProperty("SymbolScale", this.symbolScale);
                }
            }
        }

        @Override
        public void setDocItem(DocumentItem item) {
            super.setDocItem(item);
            this.item = item;
            if (item instanceof Vector3DLayer) {
                //region 初始化字段
                this.systemLib = ((Vector3DLayer) item).getSysLibrary();
                this.dot3d = ((Vector3DLayer) item).getScale();
                this.isStretch = ((Vector3DLayer) item).getGlobeStretch();
                this.heightExpr = ((Vector3DLayer) item).getStretchExpr();
                this.mrType = ((Vector3DLayer) item).getRenderType();
                ((Vector3DLayer) item).getRenderGridSize(this.gridSizeX, this.gridSizeY);
                this.renderMode = ((Vector3DLayer) item).getRenderMode();
                renderModeStr = renderMode ? RenderModeDef.RenderModeVector : RenderModeDef.RenderModeRaster;
                this.isPointCloudMode = ((Vector3DLayer) item).getPointCloudMode();
                this.pointCloudSize = ((Vector3DLayer) item).getPointCloudSize();
                this.boardOriginType = ((Vector3DLayer) item).getBoardOrigin();
                ((Vector3DLayer) item).getBoardAutoPixelDistance(this.autoScalePixel, this.maxDistance);
                this.symbolScale = (double) ((Vector3DLayer) item).getProperty("SymbolScale");
                //endregion

                //region 创建items
                PropertyItem[] oldItems = super.getPropertyItems();
                items = new PropertyItem[oldItems.length + 10];
                System.arraycopy(oldItems, 0, items, 0, oldItems.length);
                items[oldItems.length] = new PropertyItem("系统库", "系统库", "常规", true, SystemLibrary.class,
                        SystemLibComboBoxEditor.class);
                items[oldItems.length].setValue(this.systemLib);
                items[oldItems.length].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof SystemLibrary) {
                            systemLib = (SystemLibrary) newValue;
                            hasChanged = true;
                        }
                        if (hasChanged && isUpdate) {
                            ((Vector3DLayer) item).setSystemLibID(systemLib.getSysLibGuid());
                        }
                    }
                });

                items[oldItems.length + 1] = new PropertyItem("显示比例", "显示比例", "常规", true, String.class, Dot3DEditor.class);
                items[oldItems.length + 1].setValue(this.dot3d);
                items[oldItems.length + 1].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Dot3D) {
                            dot3d = (Dot3D) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((Vector3DLayer) item).setScale(dot3d);
                        }
                    }
                });
                items[oldItems.length + 2] = new PropertyItem("是否拉伸", "是否拉伸", "常规", true, Boolean.class, null);
                items[oldItems.length + 2].setValue(isStretch);
                items[oldItems.length + 2].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Boolean) {
                            isStretch = (boolean) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((Vector3DLayer) item).setGlobeStretch(isStretch);
                        }
                    }
                });
                items[oldItems.length + 3] = new PropertyItem("拉伸字段表达式", "拉伸字段表达式", "常规", true, String.class, null);
                items[oldItems.length + 3].setValue(heightExpr);
                items[oldItems.length + 3].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof String) {
                            heightExpr = (String) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((Vector3DLayer) item).setStretchExpr(heightExpr);
                        }
                    }
                });

                items[oldItems.length + 4] = new PropertyItem("渲染方式", "渲染方式", "常规", true, String.class,
                        ComboBoxPropertyEditor.class);
                items[oldItems.length + 4].setValue(mrType);
                items[oldItems.length + 4].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof ModelRenderType) {
                            mrType = (ModelRenderType) newValue;
                            isGrid = mrType == ModelRenderType.RenderGrid;
                            items[oldItems.length + 5].setEditable(isGrid);
                            items[oldItems.length + 6].setEditable(isGrid);
                            if (isGrid) {
                                renderMode = true;
                                isPointCloudMode = true;
                                renderModeStr = renderMode ? RenderModeDef.RenderModeVector :RenderModeDef.RenderModeRaster;
                                items[oldItems.length + 7].setValue(renderModeStr);
                                items[oldItems.length + 8].setValue(isPointCloudMode);
                            }
                            items[oldItems.length + 7].setEditable(!isGrid);
                            items[oldItems.length + 8].setEditable((!isGrid) && renderMode);
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((Vector3DLayer) item).setRenderType(mrType);
                        }
                    }
                });
                items[oldItems.length + 5] = new PropertyItem("分块行大小", "分块行大小", "常规", isGrid, Integer.class, null);
                items[oldItems.length + 5].setValue(gridSizeX);
                items[oldItems.length + 5].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Integer) {
                            gridSizeX = (int) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((Vector3DLayer) item).setRenderGridSize(gridSizeX, gridSizeY);
                        }
                    }
                });
                items[oldItems.length + 6] = new PropertyItem("分块列大小", "分块列大小", "常规", isGrid, Integer.class, null);
                items[oldItems.length + 6].setValue(gridSizeY);
                items[oldItems.length + 6].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Integer) {
                            gridSizeY = (int) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((Vector3DLayer) item).setRenderGridSize(gridSizeX, gridSizeY);
                        }
                    }
                });
                items[oldItems.length + 7] = new PropertyItem("绘制方式", "绘制方式", "常规", !isGrid, String.class, ComboBoxPropertyEditor.class);
                items[oldItems.length + 7].setValue(renderModeStr);
                items[oldItems.length + 7].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof RenderModeDef) {
                            renderModeStr = (RenderModeDef) newValue;
                            renderMode = renderModeStr == RenderModeDef.RenderModeVector;
                            items[oldItems.length + 8].setEditable((!isGrid) && renderMode);
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((Vector3DLayer) item).setRenderMode(renderMode);
                        }
                    }
                });
                items[oldItems.length + 8] = new PropertyItem("点云模式", "点云模式", "常规", (!isGrid) && renderMode, false, Boolean.class, null);
                items[oldItems.length + 8].setValue(isPointCloudMode);
                items[oldItems.length + 8].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Boolean) {
                            isPointCloudMode = (boolean) newValue;
                            items[oldItems.length + 9].setEditable(isPointCloudMode);
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((Vector3DLayer) item).setPointCloudMode(isPointCloudMode);
                        }
                    }
                });
                items[oldItems.length + 9] = new PropertyItem("点的大小", "点云模式下点的大小", "常规", isPointCloudMode, false, Double.class, null);
                items[oldItems.length + 9].setValue(pointCloudSize);
                items[oldItems.length + 9].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Double) {
                            pointCloudSize = (double) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((Vector3DLayer) item).setPointCloudSize(pointCloudSize);
                        }
                    }
                });
                //endregion
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
                AddUnitedSetting(items, item, propertySheet, isUIUpdate);
            }
            return propertySheet;
        }
    }

    /**
     * 三维 注记 图层的通用属性(常规)属性
     */
    public static class LabelLayerProperty extends Layer3DCommonProperty3 {
        private PropertyItem[] items;
        private PropertySheet propertySheet;
        private DocumentItem item = null;

        private String heightExpr = "";//高程字段表达式
        private boolean avoidFlag = false;//注记避让
        private double maxDispLen = 0;//最远显示距离
        private double minDispLen = 0;//最近显示距离
        private double symbolScale = 1;//符号比率

        public LabelLayerProperty() {
            isUIUpdate.addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (propertySheet != null && isUIUpdate.get()) {
                        ArrayList<PropertyItem> newItems = new ArrayList<>();
                        for (int i = 0; i < items.length; i++) {
                            if (items[i].isVisible()) {
                                newItems.add(items[i]);
                            }
                        }
                        if (propertySheet != null && isUIUpdate.get()) {
                            propertySheet.getItems().setAll(newItems);
                        }
                    }
                }
            });
        }

        @Override
        public void apply() {
            super.apply();
            if (!this.isUpdate && hasChanged) {
                if (this.item instanceof LabelLayer) {
                    ((LabelLayer) item).setProperty("HeightExpr", this.heightExpr);
                    ((LabelLayer) item).setLabelAvoidFlag(avoidFlag);
                    ((LabelLayer) item).setMaxDispDist(this.maxDispLen);
                    ((LabelLayer) item).setMinDispDist(this.minDispLen);
                    ((LabelLayer) item).setProperty("SymbolScale", this.symbolScale);

                }
            }
        }

        @Override
        public void setDocItem(DocumentItem item) {
            super.setDocItem(item);
            this.item = item;
            if (item instanceof LabelLayer) {
                //region 初始化字段

                Object obj1 = ((LabelLayer) item).getProperty("HeightExpr");
                if (obj1 instanceof String) {
                    this.heightExpr = (String) obj1;
                }
                //TODO getLabelAvoidFlag 接口挂掉  jni_GetLabelAvoidFlag
//                avoidFlag = ((LabelLayer) item).getLabelAvoidFlag();
                //TODO getMaxDispDist 接口挂掉 jni_GetMaxDispDist
//                this.maxDispLen = ((LabelLayer) item).getMaxDispDist();
//                this.minDispLen = ((LabelLayer) item).getMinDispDist();
                Object obj2 = ((LabelLayer) item).getProperty("SymbolScale");
                if (obj2 instanceof Double) {
                    this.symbolScale = (double) obj2;
                }

                //endregion

                //region 创建items
                PropertyItem[] oldItems = super.getPropertyItems();
                items = new PropertyItem[oldItems.length + 5];
                System.arraycopy(oldItems, 0, items, 0, oldItems.length);

                items[oldItems.length] = new PropertyItem("高度字段表达式", "高度字段表达式", "常规", true, String.class, null);
                items[oldItems.length].setValue(heightExpr);
                items[oldItems.length].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof String) {
                            heightExpr = (String) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((LabelLayer) item).setProperty("HeightExpr", heightExpr);
                        }
                    }
                });

                items[oldItems.length + 1] = new PropertyItem("注记避让", "注记避让", "常规", true, false, Boolean.class, null);
                items[oldItems.length + 1].setValue(avoidFlag);
                items[oldItems.length + 1].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Boolean) {
                            avoidFlag = (boolean) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((LabelLayer) item).setLabelAvoidFlag(avoidFlag);
                        }
                    }
                });
                items[oldItems.length + 2] = new PropertyItem("最远显示距离", "平面模式下最远显示距离", "常规", true, Double.class, null);
                items[oldItems.length + 2].setValue(maxDispLen);
                items[oldItems.length + 2].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Double) {
                            maxDispLen = (double) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((LabelLayer) item).setMaxDispDist(maxDispLen);
                        }
                    }
                });
                items[oldItems.length + 3] = new PropertyItem("最近显示距离", "平面模式下最近显示距离", "常规", true, Double.class, null);
                items[oldItems.length + 3].setValue(minDispLen);
                items[oldItems.length + 3].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Double) {
                            minDispLen = (double) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((LabelLayer) item).setMinDispDist(minDispLen);
                        }
                    }
                });
                items[oldItems.length + 4] = new PropertyItem("符号比率", "符号比率", "常规", true, Double.class, null);
                items[oldItems.length + 4].setValue(symbolScale);
                items[oldItems.length + 4].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Double) {
                            symbolScale = (int) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((LabelLayer) item).setProperty("SymbolScale", symbolScale);
                        }
                    }
                });
                //endregion
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
                AddUnitedSetting(items, item, propertySheet, isUIUpdate);
            }
            return propertySheet;
        }
    }

    /**
     * 三维 地形 图层的通用属性(常规)属性
     */
    public static class TerrainLayerProperty extends Layer3DCommonProperty3 {
        private PropertyItem[] items;
        private PropertySheet propertySheet;
        private DocumentItem item = null;
        private int alpha = 0;//透明度
        private double scale = 0;//高程缩放比
        private double layerHeight = 0;//图层高度
        private boolean bDisp = false;//是否高程设色显示
        private String colorTblName = "";//色表信息
        boolean isUserDef = false;
        private String clrPath = "";//自定义色表路径
        private boolean renderSkirts = false;//显示裙边
        private int cullingMode = 1;//裁剪模式
        private int num = 20;//瓦片分辨率
        private boolean isHorizonDetection = false;//是否开启地平线检测
        private boolean isDiscardNeedLessPass = false;//是否过滤不渲染通道
        private String cachePath = "";//本地缓存目录路径

        private boolean isCustomGridInfo = false;//是否自定义网格信息
        private Rect renderRect;//渲染范围
        private Dot gridSize = new Dot();//网格大小
        private ZhDot originalPoint = new ZhDot(0, 0);//网格原点
        private CutModelType cutModel = CutModelType.BIGER_RANGE;//地形叠加范围裁剪模式
        private int count = 0;//子图层数量
        CullingMode cullingModeDef = CullingMode.None;

        public TerrainLayerProperty() {
            isUIUpdate.addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (propertySheet != null && isUIUpdate.get()) {
                        ArrayList<PropertyItem> newItems = new ArrayList<>();
                        for (int i = 0; i < items.length; i++) {
                            if (items[i].isVisible()) {
                                newItems.add(items[i]);
                            }
                        }
                        if (propertySheet != null && isUIUpdate.get()) {
                            propertySheet.getItems().setAll(newItems);
                        }
                    }
                }
            });
        }

        @Override
        public void apply() {
            super.apply();
            if (!this.isUpdate && hasChanged) {
                if (this.item instanceof TerrainLayer) {
                    TerrainLayer layer = (TerrainLayer) this.item;

                    layer.setTransparency(this.alpha);
                    layer.setElevationScale(this.scale);
                    layer.setLayerHeight(this.layerHeight);
                    layer.setTerrainColorDisp(this.bDisp);
                    layer.setTerrainColor(this.colorTblName);
                    isUserDef = this.colorTblName == "自定义色表";
                    if (isUserDef) {
                        layer.setTerrainColorFile(this.clrPath);
                    }
                    layer.setRenderSkirts(this.renderSkirts);
                    layer.setCullingMode((short) this.cullingMode);
                    layer.setTileGridNum(this.num);
                    layer.setCheckHorizontal(this.isHorizonDetection);
                    layer.setDiscardNeedLessPass(this.isDiscardNeedLessPass);
                    layer.setCachePath(this.cachePath);
                    layer.setCustomGrid(this.isCustomGridInfo);
                    if (this.isCustomGridInfo) {
                        layer.setRenderRect(this.renderRect);
                        layer.setOriginalPoint(this.originalPoint.getDot());
                        layer.setMaxFrame(this.gridSize.getX(), this.gridSize.getY());
                    }
                    //SetCutModel后需要更新渲染范围
                    CutModelType type = layer.getCutModel();
                    if (type != this.cutModel) {
                        layer.setCutModel(this.cutModel);
                        layer.getRenderRect(this.renderRect);
                    }
                }
            }
        }

        @Override
        public void setDocItem(DocumentItem item) {
            super.setDocItem(item);
            this.item = item;
            if (item instanceof TerrainLayer) {

                //region 初始化字段

                TerrainLayer layer = (TerrainLayer) item;
                this.alpha = (int) layer.getTransparency();
                this.scale = layer.getElevationScale();
                this.layerHeight = layer.getLayerHeight();
                this.bDisp = layer.isTerrainColorDisp();
                this.colorTblName = layer.getTerrainColor();
                isUserDef = this.colorTblName == "自定义色表";
                this.clrPath = layer.getTerrainColorFile();
                this.renderSkirts = layer.getRenderSkirts();
                this.cullingMode = (int) layer.getCullingMode();
                this.num = (int) layer.getTileGridNum();
                this.isHorizonDetection = layer.isCheckHorizontal();
                this.isDiscardNeedLessPass = layer.isDiscardNeedLessPass();
                this.cachePath = layer.getCachePath();
                this.isCustomGridInfo = layer.getCustomGrid();
                layer.getRenderRect(this.renderRect);
                Dot dt = new Dot();
                layer.getOriginalPoint(dt);
                this.originalPoint = new ZhDot(dt.getX(), dt.getY());
                double x = 0, y = 0;
//                layer.getMaxFrame(x,y);
                this.gridSize = new Dot(x, y);
                if (cullingMode == 1) {
                    cullingModeDef = CullingMode.Clockwise;
                } else if (cullingMode == 2) {
                    cullingModeDef = CullingMode.AntiClockwise;
                }
                //endregion
            }
            //region 创建items
            PropertyItem[] oldItems = super.getPropertyItems();
            items = new PropertyItem[oldItems.length + 16];
            System.arraycopy(oldItems, 0, items, 0, oldItems.length);
            items[oldItems.length] = new PropertyItem("透明度", "透明度", "常规", true, Integer.class, null);
            items[oldItems.length].setValue(this.alpha);
            items[oldItems.length].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof Integer) {
                        alpha = (int) newValue;
                        hasChanged = true;
                    }
                    if (isUpdate && hasChanged) {
                        if (item instanceof TerrainLayer) {
                            ((TerrainLayer) item).setTransparency(alpha);
                        }
                    }
                }
            });
            items[oldItems.length + 1] = new PropertyItem("高程缩放比", "高程缩放比", "常规", true, Double.class, null);
            items[oldItems.length + 1].setValue(this.scale);
            items[oldItems.length + 1].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof Double) {
                        scale = (double) newValue;
                        hasChanged = true;
                    }
                    if (isUpdate && hasChanged) {
                        if (item instanceof TerrainLayer) {
                            ((TerrainLayer) item).setElevationScale(scale);
                        }
                    }
                }
            });
            items[oldItems.length + 2] = new PropertyItem("图层高度", "图层高度", "常规", true, Double.class, null);
            items[oldItems.length + 2].setValue(this.layerHeight);
            items[oldItems.length + 2].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof Double) {
                        layerHeight = (double) newValue;
                        hasChanged = true;
                    }
                    if (isUpdate && hasChanged) {
                        if (item instanceof TerrainLayer) {
                            ((TerrainLayer) item).setLayerHeight(layerHeight);
                        }
                    }
                }
            });
            items[oldItems.length + 3] = new PropertyItem("是否高程设色显示", "是否高程设色显示", "常规", true, Boolean.class, null);
            items[oldItems.length + 3].setValue(bDisp);
            items[oldItems.length + 3].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof Boolean) {
                        bDisp = (boolean) newValue;
                        hasChanged = true;
                    }
                    if (isUpdate && hasChanged) {
                        if (item instanceof TerrainLayer) {
                            ((TerrainLayer) item).setTerrainColorDisp(bDisp);
                        }
                    }
                }
            });
            items[oldItems.length + 4] = new PropertyItem("色表信息", "色表信息", "常规", false, String.class, null);
            items[oldItems.length + 4].setValue(colorTblName);
            items[oldItems.length + 4].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof String) {
                        colorTblName = (String) newValue;
                        isUserDef = colorTblName == "自定义色表";
                        items[oldItems.length + 5].setEditable(isUserDef);
                        hasChanged = true;
                    }
                    if (isUpdate && hasChanged) {
                        if (item instanceof TerrainLayer) {
                            ((TerrainLayer) item).setTerrainColor(colorTblName);
                        }
                    }
                }
            });
            items[oldItems.length + 5] = new PropertyItem("自定义色表路径", "自定义色表路径", "常规", isUserDef, String.class, FileChooserEditor.class);
            items[oldItems.length + 5].setValue(clrPath);
            items[oldItems.length + 5].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof String) {
                        clrPath = (String) newValue;
                        hasChanged = true;
                    }
                    if (isUpdate && hasChanged) {
                        if (item instanceof TerrainLayer) {
                            ((TerrainLayer) item).setTerrainColorFile(clrPath);
                        }
                    }
                }
            });
            items[oldItems.length + 6] = new PropertyItem("显示裙边", "显示裙边", "常规", true, Boolean.class, null);
            items[oldItems.length + 6].setValue(renderSkirts);
            items[oldItems.length + 6].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof Boolean) {
                        renderSkirts = (boolean) newValue;
                        hasChanged = true;
                    }
                    if (isUpdate && hasChanged) {
                        if (item instanceof TerrainLayer) {
                            ((TerrainLayer) item).setRenderSkirts(renderSkirts);
                        }
                    }
                }
            });
            items[oldItems.length + 7] = new PropertyItem("裁剪模式", "裁剪模式", "常规", true, String.class, ComboBoxPropertyEditor.class);
            items[oldItems.length + 7].setValue(cullingModeDef);
            items[oldItems.length + 7].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof CullingMode) {
                        cullingModeDef = (CullingMode) newValue;
                        if (cullingModeDef == CullingMode.Clockwise) {
                            cullingMode = 1;
                        } else if (cullingModeDef == CullingMode.AntiClockwise) {
                            cullingMode = 2;
                        } else {
                            cullingMode = 0;
                        }
                        hasChanged = true;
                    }
                    if (isUpdate && hasChanged) {
                        if (item instanceof TerrainLayer) {
                            ((TerrainLayer) item).setCullingMode((short) cullingMode);
                        }
                    }
                }
            });
            items[oldItems.length + 8] = new PropertyItem("瓦片分辨率", "瓦片分辨率", "常规", true, Integer.class, null);
            items[oldItems.length + 8].setValue(num);
            items[oldItems.length + 8].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof Integer) {
                        num = (int) newValue;
                        hasChanged = true;
                    }
                    if (isUpdate && hasChanged) {
                        if (item instanceof TerrainLayer) {
                            ((TerrainLayer) item).setTileGridNum(num);
                        }
                    }
                }
            });
            items[oldItems.length + 9] = new PropertyItem("是否开启地平线检测", "是否开启地平线检测", "常规", true, Boolean.class, null);
            items[oldItems.length + 9].setValue(isHorizonDetection);
            items[oldItems.length + 9].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof Boolean) {
                        isHorizonDetection = (boolean) newValue;
                        hasChanged = true;
                    }
                    if (isUpdate && hasChanged) {
                        if (item instanceof TerrainLayer) {
                            ((TerrainLayer) item).setCheckHorizontal(isHorizonDetection);
                        }
                    }
                }
            });
            items[oldItems.length + 10] = new PropertyItem("是否过滤不渲染通道", "是否过滤不渲染通道", "常规", true, Boolean.class, null);
            items[oldItems.length + 10].setValue(isDiscardNeedLessPass);
            items[oldItems.length + 10].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof Boolean) {
                        isDiscardNeedLessPass = (boolean) newValue;
                        hasChanged = true;
                    }
                    if (isUpdate && hasChanged) {
                        if (item instanceof TerrainLayer) {
                            ((TerrainLayer) item).setDiscardNeedLessPass(isDiscardNeedLessPass);
                        }
                    }
                }
            });
            items[oldItems.length + 11] = new PropertyItem("本地缓存目录路径", "本地缓存目录路径", "常规", true, String.class, DirectoryChooserEditor.class);
            items[oldItems.length + 11].setValue(cachePath);
            items[oldItems.length + 11].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof String) {
                        cachePath = (String) newValue;
                        hasChanged = true;
                    }
                    if (isUpdate && hasChanged) {
                        if (item instanceof TerrainLayer) {
                            ((TerrainLayer) item).setCachePath(cachePath);
                        }
                    }
                }
            });
            //暂时屏蔽
            items[oldItems.length + 12] = new PropertyItem("是否自定义网格信息", "是否自定义网格信息", "常规", true, false,Boolean.class, null);
            items[oldItems.length + 12].setValue(isCustomGridInfo);
            items[oldItems.length + 12].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof Boolean) {
                        isCustomGridInfo = (boolean) newValue;
                        items[oldItems.length + 13].setValue(isCustomGridInfo);
                        items[oldItems.length + 14].setValue(isCustomGridInfo);
                        items[oldItems.length + 15].setValue(isCustomGridInfo);
                        hasChanged = true;
                    }
                    if (isUpdate && hasChanged) {
                        if (item instanceof TerrainLayer) {
                            ((TerrainLayer) item).setCustomGrid(isCustomGridInfo);
                        }
                    }
                }
            });
            items[oldItems.length + 13] = new PropertyItem("渲染范围", "渲染范围", "常规", true, isCustomGridInfo, String.class, RectBoundEitor.class);
            items[oldItems.length + 13].setValue(renderRect);
            items[oldItems.length + 13].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof Rect) {
                        renderRect = (Rect) newValue;
                        hasChanged = true;
                    }
                    if (isUpdate && hasChanged) {
                        if (item instanceof TerrainLayer) {
                            ((TerrainLayer) item).setRenderRect(renderRect);
                        }
                    }
                }
            });
            items[oldItems.length + 14] = new PropertyItem("网格大小", "网格大小", "常规", true, isCustomGridInfo, String.class, PopupPropertyExEditor.class);

            items[oldItems.length + 14].setValue(new ZhDot(gridSize.getX(), gridSize.getY()));
            items[oldItems.length + 14].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof Dot) {
                        gridSize = (Dot) newValue;
                        hasChanged = true;
                    }
                    if (isUpdate && hasChanged) {
                        if (item instanceof TerrainLayer) {
                            ((TerrainLayer) item).setMaxFrame(gridSize.getX(), gridSize.getY());
                        }
                    }
                }
            });
            items[oldItems.length + 15] = new PropertyItem("网格原点", "网格原点", "常规", true, isCustomGridInfo, String.class, PopupPropertyEditor.class);
            items[oldItems.length + 15].setValue(originalPoint);
            items[oldItems.length + 15].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof ZhDot) {
                        originalPoint = (ZhDot) newValue;
                        hasChanged = true;
                    }
                    if (isUpdate && hasChanged) {
                        if (item instanceof TerrainLayer) {
                            ((TerrainLayer) item).setOriginalPoint(originalPoint.getDot());
                        }
                    }
                }
            });
//            items[oldItems.length + 16] = new PropertyItem("地形叠加范围裁剪", "地形叠加范围裁剪", "常规", true, isCustomGridInfo, String.class, ComboBoxPropertyEditor.class);
//            items[oldItems.length + 16].setValue(cutModel);
//            items[oldItems.length + 16].getObservableValue().get().addListener(new ChangeListener<Object>() {
//                @Override
//                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
//                    if (newValue instanceof CutModelType) {
//                        cutModel = (CutModelType) newValue;
//                        hasChanged = true;
//                    }
//                    if (isUpdate && hasChanged) {
//                        if (item instanceof TerrainLayer) {
//                            ((TerrainLayer) item).setCutModel(cutModel);
//                        }
//                    }
//                }
//            });

            //endregion

        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return items;
        }


        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
                AddUnitedSetting(items, item, propertySheet, isUIUpdate);
            }
            return propertySheet;
        }
    }

    /**
     * 三维 模型缓存 图层的通用属性(常规)属性
     */
    public static class CacheLayerProperty extends Layer3DCommonProperty1 {
        private PropertyItem[] items;
        private PropertySheet propertySheet;
        private DocumentItem item = null;
        private int transparency = 0;//透明度
        private Dot3D dot3d = new Dot3D(1, 1, 1);//显示比例
        //TODO 卸载顶级节点 预加载 预加载倍数 缺接口
        private boolean isSetUnLoadTopNode = false;//卸载顶级节点
        private boolean preloadFlag = false;//预加载
        private float preloadMultiple = 0;//预加载倍数
        private boolean bDisp = false;//显示场景包围盒
        private boolean bDispCridNet = false;//显示场景坐标网格
        private boolean isSetMaxMinDispLen = false;//设置最远最近显示距离(获取远近距离显示控制标志)
        private double maxDispLen = 0;//最远显示距离
        private double minDispLen = 0;//最近显示距离

        public CacheLayerProperty() {
            isUIUpdate = new SimpleBooleanProperty(this, "isUIUpdate", true);
            isUIUpdate.addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (propertySheet != null && isUIUpdate.get()) {
                        ArrayList<PropertyItem> newItems = new ArrayList<>();
                        for (int i = 0; i < items.length; i++) {
                            if (items[i].isVisible()) {
                                newItems.add(items[i]);
                            }
                        }
                        if (propertySheet != null && isUIUpdate.get()) {
                            propertySheet.getItems().setAll(newItems);
                        }
                    }
                }
            });
        }

        @Override
        public void apply() {
            super.apply();
            if (!this.isUpdate && hasChanged) {
                if (this.item instanceof ModelCacheLayer) {
                    ((ModelCacheLayer) item).setTransparency(transparency);
                    ((ModelCacheLayer) item).setScale(dot3d);
                    //TODO 接口参数不对
//                    ((ModelCacheLayer) item).setDispScenceBox(bDisp);
//                    ((ModelCacheLayer) item).setDispCridNet(bDispCridNet);
//                    ((ModelCacheLayer) item).setMinMaxDispLen(this.isSetMaxMinDispLen);
                    ((ModelCacheLayer) item).setMaxDispLen(this.maxDispLen);
                    ((ModelCacheLayer) item).setMinDispLen(this.minDispLen);
                }
            }
        }

        @Override
        public void setDocItem(DocumentItem item) {
            super.setDocItem(item);
            this.item = item;
            if (item instanceof ModelCacheLayer) {

                //region 初始化字段
                transparency = ((ModelCacheLayer) item).getTransparency();
                dot3d = ((ModelCacheLayer) item).getScale();
                bDisp = ((ModelCacheLayer) item).isDispScenceBox();
                bDispCridNet = ((ModelCacheLayer) item).isDispCridNet();
                this.isSetMaxMinDispLen = ((ModelCacheLayer) item).isSetMaxMinDispLen();
                this.maxDispLen = ((ModelCacheLayer) item).getMaxDispLen();
                this.minDispLen = ((ModelCacheLayer) item).getMinDispLen();

                //endregion

                //region 创建items
                PropertyItem[] oldItems = super.getPropertyItems();
                items = new PropertyItem[oldItems.length + 7];
                System.arraycopy(oldItems, 0, items, 0, oldItems.length);

                items[oldItems.length] = new PropertyItem("透明度", "透明度", "常规", true, Integer.class, null);
                items[oldItems.length].setValue(this.transparency);
                items[oldItems.length].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Integer) {
                            transparency = (short) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((ModelCacheLayer) item).setTransparency(transparency);
                        }
                    }
                });
                items[oldItems.length + 1] = new PropertyItem("显示比例", "显示比例", "常规", true, String.class, null);
                items[oldItems.length + 1].setValue("1,1,1");
                items[oldItems.length + 1].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Dot3D) {
                            dot3d = (Dot3D) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((ModelCacheLayer) item).setScale(dot3d);
                        }
                    }
                });
                items[oldItems.length + 2] = new PropertyItem("显示场景包围盒", "显示场景包围盒", "常规", true, Boolean.class, null);
                items[oldItems.length + 2].setValue(bDisp);
                items[oldItems.length + 2].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Boolean) {
                            bDisp = (boolean) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
//                            ((ModelCacheLayer) item).setDispScenceBox(bDisp);
                        }
                    }
                });
                items[oldItems.length + 3] = new PropertyItem("显示场景坐标网格", "显示场景坐标网格", "常规", true, Boolean.class, null);
                items[oldItems.length + 3].setValue(bDispCridNet);
                items[oldItems.length + 3].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Boolean) {
                            bDispCridNet = (boolean) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((ModelCacheLayer) item).setDispCridNet(bDispCridNet ? 1 : 0);
                        }
                    }
                });

                items[oldItems.length + 4] = new PropertyItem("设置最远最近显示距离", "设置最远最近显示距离", "常规", true, Boolean.class, null);
                items[oldItems.length + 4].setValue(isSetMaxMinDispLen);
                items[oldItems.length + 4].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Boolean) {
                            isSetMaxMinDispLen = (boolean) newValue;
                            beginUpdate();
                            items[oldItems.length + 5].setEditable(isSetMaxMinDispLen);
                            items[oldItems.length + 6].setEditable(isSetMaxMinDispLen);
                            endUpdate();
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((ModelCacheLayer) item).setMinMaxDispLen(isSetMaxMinDispLen ? 1 : 0);
                        }
                    }
                });
                items[oldItems.length + 5] = new PropertyItem("最近显示距离", "最近显示距离", "常规", isSetMaxMinDispLen, Double.class, null);
                items[oldItems.length + 5].setValue(minDispLen);
                items[oldItems.length + 5].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Double) {
                            minDispLen = (double) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((ModelCacheLayer) item).setMinDispLen(minDispLen);
                        }
                    }
                });
                items[oldItems.length + 6] = new PropertyItem("最远显示距离", "最远显示距离", "常规", isSetMaxMinDispLen, Double.class, null);
                items[oldItems.length + 6].setValue(maxDispLen);
                items[oldItems.length + 6].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Double) {
                            maxDispLen = (double) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            ((ModelCacheLayer) item).setMaxDispLen(maxDispLen);
                        }
                    }
                });
                //endregion


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
                AddUnitedSetting(items, item, propertySheet, isUIUpdate);
            }
            return propertySheet;
        }
    }

    /**
     * 三维 模型缓存 图层的通用属性(常规)属性
     */
    public static class MapRefLayerProperty extends Layer3DCommonProperty {
        private PropertyItem[] items;
        private PropertySheet propertySheet;
        private DocumentItem item = null;
        private short transparency = 0;//透明度
        private int colorNo = 0;//透明色
        private boolean isCustomGridInfo = false;//是否自定义网格信息
        private RectBound1 renderRect = new RectBound1(null);//渲染范围
        private Dot gridSize = new Dot();//网格大小
        private ZhDot originalPoint = new ZhDot(0, 0);//网格原点


        public MapRefLayerProperty() {
            isUIUpdate = new SimpleBooleanProperty(this, "isUIUpdate", true);
            isUIUpdate.addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (propertySheet != null && isUIUpdate.get()) {
                        ArrayList<PropertyItem> newItems = new ArrayList<>();
                        for (int i = 0; i < items.length; i++) {
                            if (items[i].isVisible()) {
                                newItems.add(items[i]);
                            }
                        }
                        if (propertySheet != null && isUIUpdate.get()) {
                            propertySheet.getItems().setAll(newItems);
                        }
                    }
                }
            });
        }

        @Override
        public void apply() {
            super.apply();
            if (!this.isUpdate && hasChanged) {
                if (this.item instanceof MapRefLayer) {
                    MapRefLayer layer = (MapRefLayer) this.item;
                    ((MapRefLayer) item).setTransparent(transparency);
                    ((MapRefLayer) item).setTransparentColor(colorNo);
                    layer.setCustomGrid(this.isCustomGridInfo);
                    if (this.isCustomGridInfo) {
                        layer.setRenderRect(new Rect(renderRect.getXMin(), renderRect.getYMin(), renderRect.getXMax(), renderRect.getYMax()));
                        layer.setOriginalPoint(this.originalPoint.getDot());
                        layer.setMaxFrame(this.gridSize.getX(), this.gridSize.getY());
                    }
                }
            }
        }

        @Override
        public void setDocItem(DocumentItem item) {
            super.setDocItem(item);
            this.item = item;
            if (item instanceof ModelCacheLayer) {
                MapRefLayer layer = (MapRefLayer) this.item;
                //region 初始化字段
                transparency = layer.getTransparent();
                this.isCustomGridInfo = layer.getCustomGrid();
                Rect rect = new Rect();
                layer.getRenderRect(rect);
                this.renderRect = new RectBound1(rect);
                Dot dt = new Dot();
                layer.getOriginalPoint(dt);
                this.originalPoint = new ZhDot(dt.getX(), dt.getY());
                double x = 0, y = 0;
                //TODO 接口错误
//                layer.getMaxFrame(x,y);
                this.gridSize = new Dot(x, y);

                //endregion

                //region 创建items
                PropertyItem[] oldItems = super.getPropertyItems();
                items = new PropertyItem[oldItems.length + 6];
                System.arraycopy(oldItems, 0, items, 0, oldItems.length);

                items[oldItems.length] = new PropertyItem("透明度", "透明度", "常规", true, Integer.class, null);
                items[oldItems.length].setValue(this.transparency);
                items[oldItems.length].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Integer) {
                            transparency = (short) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            if (item instanceof MapRefLayer) {
                                ((MapRefLayer) item).setTransparent(transparency);
                            }
                        }
                    }
                });
                items[oldItems.length + 1] = new PropertyItem("透明色", "透明色", "常规", true, Integer.class, MapGISColorPicker.class);
                items[oldItems.length + 1].setValue(this.colorNo);
                items[oldItems.length + 1].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Integer) {
                            colorNo = (int) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            if (item instanceof MapRefLayer) {
                                ((MapRefLayer) item).setTransparentColor(colorNo);
                            }
                        }
                    }
                });
                items[oldItems.length + 2] = new PropertyItem("是否自定义网格信息", "是否自定义网格信息", "常规", true, Boolean.class, null);
                items[oldItems.length + 2].setValue(isCustomGridInfo);
                items[oldItems.length + 2].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Boolean) {
                            isCustomGridInfo = (boolean) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            if (item instanceof MapRefLayer) {
                                ((MapRefLayer) item).setCustomGrid(isCustomGridInfo);
                            }
                        }
                    }
                });
                items[oldItems.length + 3] = new PropertyItem("渲染范围", "渲染范围", "常规", true, isCustomGridInfo, String.class, PopupPropertyEditor.class);
                items[oldItems.length + 3].setValue(renderRect);
                items[oldItems.length + 3].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof RectBound1) {
                            renderRect = (RectBound1) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            if (item instanceof MapRefLayer) {
//                                ((MapRefLayer) item).setRenderRect(renderRect.getRange());
                            }
                        }
                    }
                });
                items[oldItems.length + 4] = new PropertyItem("网格大小", "网格大小", "常规", true, isCustomGridInfo, String.class, PopupPropertyExEditor.class);
                items[oldItems.length + 4].setValue(new ZhDot(gridSize.getX(), gridSize.getY()));
                items[oldItems.length + 4].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof Dot) {
                            gridSize = (Dot) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            if (item instanceof MapRefLayer) {
                                ((MapRefLayer) item).setMaxFrame(gridSize.getX(), gridSize.getY());
                            }
                        }
                    }
                });
                items[oldItems.length + 5] = new PropertyItem("网格原点", "网格原点", "常规", true, isCustomGridInfo, String.class, PopupPropertyEditor.class);
                items[oldItems.length + 5].setValue(originalPoint);
                items[oldItems.length + 5].getObservableValue().get().addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        if (newValue instanceof ZhDot) {
                            originalPoint = (ZhDot) newValue;
                            hasChanged = true;
                        }
                        if (isUpdate && hasChanged) {
                            if (item instanceof TerrainLayer) {
                                ((TerrainLayer) item).setOriginalPoint(originalPoint.getDot());
                            }
                        }
                    }
                });
                //endregion


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
                AddUnitedSetting(items, item, propertySheet, isUIUpdate);
            }
            return propertySheet;
        }
    }
    //endregion

    //region 服务图层

    /**
     * 二三维 服务 图层的数据源属性
     */
    public static class ServerLayerDataSourceProperty implements IProperty {
        private boolean isUpdate = false;//是否立即更新
        protected boolean hasChanged = false;
        private PropertyItem[] items;
        private PropertySheet propertySheet;
        private DocumentItem item = null;

        private RectBound dataBound = new RectBound(null);//数据范围
        private String serverType = "";//服务类型
        //        private MapServerType serverType = "";//服务类型
//                private MapServerBrowseType browseType = MapServerBrowseType.Tile;//浏览类型
        private String url = "";//服务路径
        private String version = "";//版本
        private String description = "";//描述
        private String user = "";//用户名

        protected SimpleBooleanProperty isUIUpdate;

        public ServerLayerDataSourceProperty() {
            isUIUpdate = new SimpleBooleanProperty(this, "isUIUpdate", true);
            isUIUpdate.addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (propertySheet != null && isUIUpdate.get()) {
                        ArrayList<PropertyItem> newItems = new ArrayList<>();
                        for (int i = 0; i < items.length; i++) {
                            if (items[i].isVisible()) {
                                newItems.add(items[i]);
                            }
                        }
                        if (propertySheet != null && isUIUpdate.get()) {
                            propertySheet.getItems().setAll(newItems);
                        }
                    }
                }
            });
        }

        public void beginUpdate() {
            isUIUpdate.set(false);
        }

        public void endUpdate() {
            isUIUpdate.set(true);
        }

        //        private TileSliceType tileSliceType = TileSliceType.SliceWMTS;//切片方式
        @Override
        public void setDocItem(DocumentItem item) {
            this.item = item;
            MapServer server = null;
            if (item instanceof ImageLayer) {
                ((ImageLayer) item).getMapServer();
            } else if (item instanceof ImageLayer) {
                server = ((ServerLayer) item).getMapServer();
            }
            if (server != null) {
//                    this.serverType = server.getType();//MapServerType getType(MapServerType Type)????
//                    this.browseType = server.getBrowseType();????
                this.url = server.getURL();
//                    this.version = server.getCurrentVersion();???
//                    MapServerInfo[] infos = server.getServerInfos();???

//                    if (infos != null)
//                    {
//                        foreach (MapServerInfo info in infos)
//                        {
//                            if (string.Compare(info.ServerType, this.serverType, true) == 0)
//                            {
//                                this.description = info.Description;
//                            }
//                        }
//                    }
//                    String password = "";
//                    server.getAuthentication(out this.user, out password);
                this.dataBound = new RectBound(server.getEntireExtent());
                //仅瓦片服务图层才有瓦片切片方式，其它情况下该属性在界面不可见
//                    if (server.BrowseType == MapServerBrowseType.Tile)
//                    {
//                        this.tileSliceType = server.SliceType;
//                    }
            }
            items = new PropertyItem[8];
            items[0] = new PropertyItem("数据范围", "数据范围", "数据源", true,
                    String.class, PopupPropertyEditor.class);
            items[0].setValue(dataBound);
            items[1] = new PropertyItem("服务类型", "服务类型", "数据源", false, String.class, null);
            items[1].setValue(this.serverType);
            items[2] = new PropertyItem("浏览类型", "浏览类型", "数据源", false, String.class, null);
            items[2].setValue("");
            items[3] = new PropertyItem("服务路径", "服务路径", "数据源", false, String.class, null);
            items[3].setValue(this.url);
            items[4] = new PropertyItem("版本", "版本", "数据源", false, String.class, null);
            items[4].setValue("");
            items[5] = new PropertyItem("描述", "描述", "数据源", false, String.class, null);
            items[5].setValue("");
            items[6] = new PropertyItem("用户名", "用户名", "数据源", false, String.class, null);
            items[6].setValue("");
            items[7] = new PropertyItem("切片方式", "切片方式", "数据源", false, String.class, null);
            items[7].setValue("");
        }

        @Override
        public boolean hasChanged() {
            return false;
        }

        @Override
        public void apply() {

        }

        @Override
        public DocumentItem getDocItem() {
            return null;
        }

        @Override
        public PropertyItem[] getPropertyItems() {
            return items;
        }

        @Override
        public PropertySheet getPropertySheet() {
            if (propertySheet == null) {
                propertySheet = createPropertySheet(items);
                AddUnitedSetting(items, item, propertySheet, isUIUpdate);
            }
            return propertySheet;
        }

        @Override
        public void setImmediatelyUpdate(boolean isUpdate) {
            this.isUpdate = isUpdate;
        }

        @Override
        public boolean isImmediatelyUpdate() {
            return this.isUpdate;
        }
    }

    private static void AddUnitedSetting(PropertyItem[] items, DocumentItem item, PropertySheet propertySheet, SimpleBooleanProperty isUIUpdate) {
        if (items != null) {
            for (PropertyItem propertyItem : items) {
                if (propertyItem != null) {
                    propertyItem.setDocumentItem(item);
                    propertyItem.editableProperty().addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                            if (propertySheet != null && isUIUpdate.get()) {
                                ArrayList<PropertyItem> newItems = new ArrayList<>();
                                for (int i = 0; i < items.length; i++) {
                                    if (items[i].isVisible()) {
                                        newItems.add(items[i]);
                                    }
                                }
                                propertySheet.getItems().setAll(newItems);
                            }
                        }
                    });
                    propertyItem.visibleProperty().addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                            if (propertySheet != null && isUIUpdate.get()) {
                                ArrayList<PropertyItem> newItems = new ArrayList<>();
                                for (int i = 0; i < items.length; i++) {
                                    if (items[i].isVisible()) {
                                        newItems.add(items[i]);
                                    }
                                }
                                propertySheet.getItems().setAll(newItems);
                            }
                        }
                    });
                }
            }
        }
    }

    /**
     * 二三维 服务 图层的常规属性
     */
    public static class ServerLayerProperty implements IProperty {
        private boolean isUpdate = false;//是否立即更新
        protected boolean hasChanged = false;
        private PropertyItem[] items;
        private DocumentItem item = null;
        private PropertySheet propertySheet;

        private SRefData sRefData = new SRefData();//参照系
        private String layerName = "";//名称
        private LayerState layerState = LayerState.UnVisible;//状态
        private boolean showCopyright = false;//显示版权
        private String copyRight = "";//版权
        private short alpha = 0;//透明度
        private int transColorNo = 0;//透明色
        private boolean isCustomGridInfo = false;//是否自定义网格信息
        private RectBound renderRect = new RectBound(null);//渲染范围
        private double minScale;//最小比例尺
        private double maxScale;//最大比例尺

        @Override
        public void setDocItem(DocumentItem item) {
            this.item = item;
            if (this.item instanceof MapLayer) {
                ScaleRange scaleRange = ((MapLayer) this.item).getScaleRange();
                minScale = scaleRange.getMinScale();
                maxScale = scaleRange.getMaxScale();
            }
            MapServer server = null;
            boolean isShow = false;
            if (item instanceof ImageLayer) {
                server = ((ImageLayer) item).getMapServer();
//                server = ((Server3DLayer) item).getMapServer();
                this.layerName = ((ImageLayer) item).getName();
                this.layerState = ((ImageLayer) item).getState();
                this.showCopyright = ((ImageLayer) item).getIsShowCopyright();
                isShow = false;
            } else if (item instanceof ServerLayer) {
                this.layerName = ((ServerLayer) item).getName();
                this.layerState = ((ServerLayer) item).getState();
//                        this.showCopyright =  ((ServerLayer) item).getIsShowCopyright();
                this.alpha = ((ServerLayer) item).getTransparent();
//                        layer.Transparent = this.alpha;
                this.transColorNo = (int) ((ServerLayer) item).getTransparentColor();
                Rect rect = null;
                ((ServerLayer) item).getRenderRect(rect);
                this.renderRect = new RectBound(rect);
                this.isCustomGridInfo = ((ServerLayer) item).getCustomGrid();
                server = ((ServerLayer) item).getMapServer();
                isShow = true;
            }

            if (server != null) {
//                        if (server.getBrowseType() == MapServerBrowseType.Vector)
//                        {
//                            this.crs = server.CRS;
//                        }
                //只有矢量服务图层有动态参照系属性，其它情况下该属性在界面不可见
//                        bool value = server.BrowseType == MapServerBrowseType.Vector;
//                        SetBrowsable(this, Resources.String_DynamicReferenceSystem, value);
                this.sRefData = server.getSRS();
//                        if (this.sRefData != null && (this.sRefData.Type == SRefType.PRJ || (this.sRefData.Type != SRefType.JWD && !string.IsNullOrEmpty(this.sRefData.ProjName))))
//                            this.wsSRefData = new WsSRefDataPrj();
//                        else
//                            this.wsSRefData = new WsSRefData();
//                        this.copyRight = server.getCopyRight();
            }

            items = new PropertyItem[11];
            items[0] = new PropertyItem("参照系", "参照系", "常规",
                    true, String.class, SRefDataPropertyEditor.class);
            items[0].setValue(sRefData);
            items[0].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof SRefData) {
                        sRefData = (SRefData) newValue;
                        hasChanged = true;
                    }
                    if (hasChanged && isUpdate) {
                        items[0].setValue(sRefData);
                    }
                }
            });
            items[1] = new PropertyItem("最小比例尺", "最小比例尺", "常规", true, String.class, ScaleComboBoxPropertyEditor.class);
            items[1].setValue(minScale);
            items[1].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    minScale = (double) newValue;
                    hasChanged = true;
                    if (isUpdate) {
                        ((MapLayer) item).setScaleRange(new ScaleRange(minScale, maxScale));
                    }
                }
            });
            items[2] = new PropertyItem("最大比例尺", "最大比例尺", "常规", true, String.class, ScaleComboBoxPropertyEditor.class);
            items[2].setValue(maxScale);
            items[2].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    maxScale = (double) newValue;
                    hasChanged = true;
                    if (isUpdate) {
                        ((MapLayer) item).setScaleRange(new ScaleRange(minScale, maxScale));
                    }
                }
            });
            items[3] = new PropertyItem("名称", "名称", "常规", true, String.class, null);
            items[3].setValue(this.layerName);
            items[3].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    layerName = (String) newValue;
                    hasChanged = true;
                    if (isUpdate) {
                        ((MapLayer) item).setName(layerName);
                    }
                }
            });
            items[4] = new PropertyItem("状态", "状态", "常规", true, String.class, ComboBoxPropertyEditor.class);
            items[4].setValue(layerState);
            items[4].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof LayerState) {
                        layerState = (LayerState) newValue;
                        hasChanged = true;
                    }
                    if (isUpdate && hasChanged) {
                        ((MapLayer) item).setState(layerState);
                    }
                }
            });
            items[5] = new PropertyItem("显示版权", "显示版权", "常规", true, Boolean.class, null);
            items[5].setValue(this.showCopyright);
            items[5].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof Boolean) {
                        showCopyright = (boolean) newValue;
                        hasChanged = true;
                    }
                    if (isUpdate && hasChanged) {

                    }
                }
            });
            items[6] = new PropertyItem("版权", "版权", "常规", true, String.class, null);
            items[6].setValue(copyRight);
            items[6].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof String) {
                        copyRight = (String) newValue;
                        hasChanged = true;
                    }
                    if (isUpdate && hasChanged) {

                    }
                }
            });
            items[7] = new PropertyItem("是否自定义网格信息", "是否自定义网格信息", "常规", false, isShow, String.class, null);
            items[7].setValue(isCustomGridInfo);
            items[7].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof Boolean) {
                        isCustomGridInfo = (boolean) newValue;
                        items[8].setVisible(isCustomGridInfo);
                        hasChanged = true;
                    }
                    if (isUpdate && hasChanged) {
                        if (item instanceof ServerLayer) {
                            ((ServerLayer) item).setCustomGrid(isCustomGridInfo);
                        }
                    }
                }
            });
            items[8] = new PropertyItem("渲染范围", "渲染范围", "常规", true, isShow && isCustomGridInfo, String.class, PopupPropertyEditor.class);
            items[8].setValue(renderRect);
            items[9] = new PropertyItem("透明度", "透明度[0-100]", "常规", true, isShow, Integer.class, null);
            items[9].setValue(alpha);
            items[9].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof Integer) {
                        alpha = (short) newValue;
                        hasChanged = true;
                    }
                    if (isUpdate && hasChanged) {
                        if (item instanceof ServerLayer) {
                            ((ServerLayer) item).setTransparent((short) alpha);
                        }
                    }
                }
            });
            items[10] = new PropertyItem("透明色", "透明色", "常规", true, isShow, Integer.class, MapGISColorPicker.class);
            items[10].setValue((int) this.transColorNo);
            items[10].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof Integer) {
                        transColorNo = (int) newValue;
                        hasChanged = true;
                    }
                    if (isUpdate && hasChanged) {
                        if (item instanceof ServerLayer) {
                            ((ServerLayer) item).setTransparentColor(transColorNo);
                        }
                    }
                }
            });
            for (PropertyItem propertyItem :
                    items) {
                propertyItem.setDocumentItem(item);
            }
        }

        @Override
        public boolean hasChanged() {
            return false;
        }

        @Override
        public void apply() {

        }

        @Override
        public DocumentItem getDocItem() {
            return null;
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
            return this.isUpdate;
        }
    }

    /**
     * 二三维 服务 图层 配置属性（瓦片）
     */
    public static class TileProperty implements IProperty {
        private boolean isUpdate = false;//是否立即更新
        protected boolean hasChanged = false;
        private PropertyItem[] items;
        private DocumentItem item = null;
        private PropertySheet propertySheet;
        private String layerName = "";
        private String tileMatrixSet = "";
        private boolean showGrid = false;//显示网格
        private boolean stretch = false;//是否拉伸
        private double mmPerPixVal;
        private double mmPerUnitVal;
        private String levelInfo = "";//显示级数信息
        private MapServerAccessMode accessMode = MapServerAccessMode.Unknown;//缓存模式
        private String cachePath = "";//缓存路径
        private long minZoom = 1;
        private long maxZoom = 1;
        private long minZoomCapacity = 0;
        private long maxZoomCapacity = 19;
        private MapServer server = null;
        //        private TileCache cache = null;
        private boolean is2D = true;

        @Override
        public void setDocItem(DocumentItem item) {
            this.item = item;

            if (item instanceof ImageLayer) {
                //region 二维服务图层
                ImageLayer layer = (ImageLayer) item;
                this.showGrid = layer.getIsShowGrid();
//                this.stretch = layer.getTileStretch();
                accessMode = layer.getAccessMode();
                //endregion
                is2D = true;
            } else if (item instanceof ServerLayer) {
                //region 三维服务图层
                // 三维底层没有实现显示网格的逻辑
                ServerLayer layer = (ServerLayer) item;
                accessMode = layer.getAccessMode();
                this.showGrid = layer.getCustomGrid();
//                this.stretch = layer.getTileStretch();
                accessMode = layer.getAccessMode();
                //endregion
                is2D = false;
            }

            //region 读取缓存信息

            if (this.item instanceof ImageLayer) {
//                cache = ((ImageLayer) this.item).getTileCache();
            }
//            else if (this.item instanceof Server3DLayer) {
//                cache = ((Server3DLayer)this.item).getTileCache();
//            }
//            if (cache != null) {
//                //bug
//                //this.cachePath = cache.getCacheLocation();
//            }
            //endregion


            //region 读取裁剪信息

            if (this.item instanceof ImageLayer) {
                server = ((ImageLayer) this.item).getMapServer();
            } else if (this.item instanceof ServerLayer) {
                server = ((ServerLayer) this.item).getMapServer();
            }
            if (server != null) {
                if(server instanceof TileMapServer)
                {
                    TileMapServer tileMapServer = (TileMapServer)server;
                    minZoom = tileMapServer.getMinZoom();
                    maxZoom = tileMapServer.getMaxZoom();
                    IntUser minZoomCap = new IntUser(0);
                    IntUser maxZoomCap = new IntUser(0);
                    tileMapServer.getZoomCapacity(minZoomCap,maxZoomCap);
                    minZoomCapacity = minZoomCap.getValue();
                    maxZoomCapacity = maxZoomCap.getValue();
                    minZoomCapacity = Math.min(minZoom, minZoomCapacity);
                    maxZoomCapacity = Math.max(maxZoom, maxZoomCapacity);
                }

                Object obj = server.getProperty("CurrentLayer");
                layerName = obj instanceof String ? (String) obj : "";
                obj = server.getProperty("CurrentTileMatrixSet");
                tileMatrixSet = obj instanceof String ? (String) obj : "";

//                levelInfo = this.GetLevelInfo(server);
            }
            //endregion

            //region 读取单位换算信息
            Object mmPerPix = server.getProperty("ServerMMPerPix");
            //bug
            mmPerPixVal = mmPerPix instanceof Double ? (double) mmPerPix : -1;

            Object mmPerUnit = server.getProperty("ServerMMPerUnit");
            mmPerUnitVal = mmPerUnit instanceof Double ? (double) mmPerUnit : -1;


            //endregion

            items = new PropertyItem[10];
            items[0] = new PropertyItem("图层", "图层", "瓦片", true, String.class, LayersComboBoxEditor.class);
            items[0].setValue(layerName);
            items[0].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof String) {
                        layerName = (String) newValue;
                        hasChanged = true;
                    }
                    if (hasChanged && isUpdate) {
                    }
                }
            });
            items[1] = new PropertyItem("瓦片集", "瓦片集", "瓦片", true, String.class, LayersComboBoxEditor.class);
            items[1].setValue(tileMatrixSet);
            items[1].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof String) {
                        tileMatrixSet = (String) newValue;
                        hasChanged = true;
                    }
                    if (hasChanged && isUpdate) {

                    }
                }
            });
            items[2] = new PropertyItem("最小显示级", "最小显示级", "瓦片", true, Long.class, null);
            items[2].setValue(minZoom);
            items[2].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof Long) {
                        if ((long) newValue > minZoomCapacity && (long) newValue < maxZoomCapacity) {
                            minZoom = (long) newValue;
                            hasChanged = true;
                        }
                    }
                    if (isUpdate && hasChanged) {
                        if (server != null) {
                            ((TileMapServer)server).setMinZoom((int)minZoom);
                            if (item instanceof ImageLayer) {
                                ((ImageLayer) item).setMapServer(server);
                            }
                        }
                    }
                }
            });
            items[3] = new PropertyItem("最大显示级", "最大显示级", "瓦片", true, Long.class, null);
            items[3].setValue(maxZoom);
            items[3].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof Long) {
                        if ((long) newValue >= minZoomCapacity && (long) newValue <= maxZoomCapacity) {
                            maxZoom = (long) newValue;
                            hasChanged = true;
                        }
                    }
                    if (isUpdate && hasChanged) {
                        if (server != null) {
                            ((TileMapServer)server).setMaxZoom((int)maxZoom);
                            if (item instanceof ImageLayer) {
                                ((ImageLayer) item).setMapServer(server);
                            }
                        }
                    }
                }
            });
            items[4] = new PropertyItem("毫米/像素", "毫米/像素", "瓦片", true, Double.class, ServerUnitComboBoxEditor.class);
            items[4].setValue(this.mmPerPixVal);
            items[4].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof Double) {
                        mmPerPixVal = (double) newValue;
                        hasChanged = true;
                    }
                    if (isUpdate && hasChanged) {
                        if (server != null) {
                            server.setProperty("ServerMMPerPix", mmPerPixVal);
                            if (item instanceof ImageLayer) {
                                ((ImageLayer) item).setMapServer(server);
                            }
                        }
                    }
                }
            });
            items[5] = new PropertyItem("毫米/数据单位", "毫米/数据单位", "瓦片", true, Double.class, ServerUnitComboBoxEditor.class);
            items[5].setValue(mmPerUnitVal);
            items[5].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof Double) {
                        mmPerUnitVal = (double) newValue;
                        hasChanged = true;
                    }
                    if (isUpdate && hasChanged) {
                        if (server != null) {
                            server.setProperty("ServerMMPerUnit", mmPerUnitVal);
                            if (item instanceof ImageLayer) {
                                ((ImageLayer) item).setMapServer(server);
                            }
                        }
                    }
                }
            });
            items[6] = new PropertyItem("显示级数", "显示级数", "瓦片", true, String.class, LayersComboBoxEditor.class);
            items[6].setValue(this.levelInfo);
            items[6].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {

                }
            });
            items[7] = new PropertyItem("显示网格", "显示网格", "瓦片", true, is2D, Boolean.class, null);
            items[7].setValue(showGrid);
            items[7].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof Boolean) {
                        showGrid = (boolean) newValue;
                        hasChanged = true;
                    }
                    if (hasChanged && isUpdate) {
                        if (item instanceof ImageLayer) {
                            ((ImageLayer) item).setIsShowGrid(showGrid);
                        }
//                        else if (item instanceof ServerLayer) {
//                            ((ServerLayer) item).setIsShowGrid(showGrid);
//                        }
                    }
                }
            });
            items[8] = new PropertyItem("是否拉伸", "是否拉伸", "瓦片", true, Boolean.class, null);
            items[8].setValue(stretch);
            items[8].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof Boolean) {
                        stretch = (boolean) newValue;
                        hasChanged = true;
                    }
                    if (isUpdate && hasChanged) {
                        if (item instanceof ImageLayer) {
//                            ((ImageLayer) item).(showGrid);
                        } else if (item instanceof ServerLayer) {
//                            ((ServerLayer) item).(showGrid);
                        }
                    }
                }
            });
            items[9] = new PropertyItem("缓存模式", "缓存模式", "瓦片", true, String.class, ComboBoxPropertyEditor.class);
            items[9].setValue(accessMode);
            items[9].getObservableValue().get().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    if (newValue instanceof MapServerAccessMode) {
                        accessMode = (MapServerAccessMode) newValue;
                        items[0].setEditable(accessMode == MapServerAccessMode.CacheOnly || accessMode == MapServerAccessMode.ServerAndCache);
                        hasChanged = true;
                    }
                    if (hasChanged && isUpdate) {
                        if (item instanceof ImageLayer) {
                            ((ImageLayer) item).setAccessMode(accessMode);
                        } else if (item instanceof ServerLayer) {
                            ((ServerLayer) item).setAccessMode(accessMode);
                        }
                    }
                }
            });
//            items[10] = new PropertyItem("缓存路径", "缓存路径", "瓦片", true, String.class, CacheDealEditor.class);
//            items[10].setValue(cachePath);
//            items[10].getObservableValue().get().addListener(new ChangeListener<Object>() {
//                @Override
//                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
//                    if (newValue instanceof String) {
//                        cachePath = (String) newValue;
//                        hasChanged = true;
//                    }
//                    if (hasChanged && isUpdate) {
//                        if (cache != null) {
//                            cache.setCacheLocation(cachePath);
//                            if (item instanceof ImageLayer) {
//                                ((ImageLayer) item).setTileCache(cache);
//                            } else if (item instanceof ServerLayer) {
//                                ((ServerLayer) item).setTileCache(cache);
//                            }
//                        }
//                    }
//                }
//            });
            for (PropertyItem propertyItem :
                    items) {
                propertyItem.setDocumentItem(item);
            }
        }

        @Override
        public boolean hasChanged() {
            return false;
        }

        @Override
        public void apply() {

        }

        @Override
        public DocumentItem getDocItem() {
            return null;
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
            return this.isUpdate;
        }
    }
    //endregion

}
