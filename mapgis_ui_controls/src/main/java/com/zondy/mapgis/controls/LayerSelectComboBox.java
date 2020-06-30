package com.zondy.mapgis.controls;

import com.zondy.mapgis.controls.event.PreAddLayerEvent;
import com.zondy.mapgis.controls.event.PreAddLayerListener;
import com.zondy.mapgis.geodatabase.XClsType;
import com.zondy.mapgis.geometry.GeomType;
import com.zondy.mapgis.map.*;
import com.zondy.mapgis.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author chenxinyuan
 */
public class LayerSelectComboBox extends ComboBox<LayerSelectComboBoxItem> {
    private Image imageMap = new Image(getClass().getResourceAsStream("/Png_MapView_16.png"));
    private Image imageScene = new Image(getClass().getResourceAsStream("/Png_SceneView_16.png"));
    private Image imagePnt = new Image(getClass().getResourceAsStream("/Png_SfClsPnt_16.png"));
    private Image imageLin = new Image(getClass().getResourceAsStream("/Png_SfClsLin_16.png"));
    private Image imageReg = new Image(getClass().getResourceAsStream("/Png_SfClsReg_16.png"));
    private Image imageSurface = new Image(getClass().getResourceAsStream("/Png_SfClsSurface_16.png"));
    private Image imageEntity = new Image(getClass().getResourceAsStream("/Png_SfClsEntity_16.png"));
    private Image imageAnn = new Image(getClass().getResourceAsStream("/Png_ACls_16.png"));
    private Image imageOCls = new Image(getClass().getResourceAsStream("/Png_OCls_16.png"));
    private Image imageRds = new Image(getClass().getResourceAsStream("/Png_RasterDs_16.png"));
    private Image imageRcat = new Image(getClass().getResourceAsStream("/Png_RasterCatalog_16.png"));

    private ArrayList<XClsType> includeXClsTypes = new ArrayList<>();
    private ArrayList<GeomType> includeGeomTypes = new ArrayList<>();

    public LayerSelectComboBox(Document document, String filter) {
        this.initComboBox();
        this.calculateIncludeClassType(filter);
        this.getItems().clear();
        if (document != null) {
            Maps maps = document.getMaps();
            if (maps != null) {
                for (int i = 0; i < maps.getCount(); i++) {
                    Map map = maps.getMap(i);
                    if (map != null) {
                        LayerSelectComboBoxItem ci = new LayerSelectComboBoxItem(map, map.getName(), 0, false, imageMap);
                        this.getItems().add(ci);
                        //GetMaxWidth(map.Name, 0);
                        getLayer(map, 1);
                    }
                }
            }
            Scenes scenes = document.getScenes();
            if (scenes != null) {
                for (int i = 0; i < scenes.getCount(); i++) {
                    Scene scene = scenes.getScene(i);
                    if (scene != null) {
                        LayerSelectComboBoxItem ci = new LayerSelectComboBoxItem(scene, scene.getName(), 0, false, imageScene);
                        this.getItems().add(ci);
                        //GetMaxWidth(scene.Name, 0);
                        getLayer(scene, 1);
                    }
                }
            }
        }
    }

    public LayerSelectComboBox(Map map, String filter) {
        this.initComboBox();
        calculateIncludeClassType(filter);
        this.getItems().clear();
        if (map != null) {
            LayerSelectComboBoxItem ci = new LayerSelectComboBoxItem(map, map.getName(), 0, false, imageMap);
            this.getItems().add(ci);
            //GetMaxWidth(m_Map.Name, 0);
            getLayer(map, 1);
        }
    }

    public LayerSelectComboBox(Scene scene, String filter) {
        this.initComboBox();
        calculateIncludeClassType(filter);
        this.getItems().clear();
        if (scene != null) {
            LayerSelectComboBoxItem ci = new LayerSelectComboBoxItem(scene, scene.getName(), 0, false, imageScene);
            this.getItems().add(ci);
            //GetMaxWidth(m_Map.Name, 0);
            getLayer(scene, 1);
        }
    }

    private void initComboBox() {
        this.setCellFactory(new Callback<ListView<LayerSelectComboBoxItem>, ListCell<LayerSelectComboBoxItem>>() {
            @Override
            public ListCell<LayerSelectComboBoxItem> call(ListView<LayerSelectComboBoxItem> param) {
                ListCell<LayerSelectComboBoxItem> layerSelectListCell = new ListCell<LayerSelectComboBoxItem>() {
                    @Override
                    protected void updateItem(LayerSelectComboBoxItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null && item.getImage() != null) {
                            setGraphic(new HBox(new Rectangle(item.getOffSet() * 16, 16, Color.TRANSPARENT), new ImageView(item.getImage())));
                            setText(item.getCaption());
                        }
                    }
                };

                layerSelectListCell.setOnMouseEntered(event -> {
                    if (layerSelectListCell.getItem() != null && !layerSelectListCell.getItem().isCanSelect()) {
                        layerSelectListCell.setDisable(true);
                    }
                });
                return layerSelectListCell;
            }
        });
        this.setButtonCell(new ListCell<LayerSelectComboBoxItem>() {
            @Override
            protected void updateItem(LayerSelectComboBoxItem item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && item.getImage() != null) {
                    setGraphic(new ImageView(item.getImage()));
                    setText(item.getCaption());
                }
            }
        });
    }

    private void calculateIncludeClassType(String layerFilter) {
        includeXClsTypes.clear();
        includeGeomTypes.clear();
        ArrayList<String> classTypes = new ArrayList<>();
        if (layerFilter != null) {
            if (layerFilter.isEmpty()) {
                classTypes.addAll(Arrays.asList("sfcls", "sfclsp", "sfclsl", "sfclsr", "sfclss", "sfclse", "acls", "ocls", "ras", "rcat", "mapset", "ncls"));
            } else {
                String[] filterParts = layerFilter.split("\\|");
                for (int i = 1; i < filterParts.length; i += 2) {
                    classTypes.addAll(Arrays.asList(filterParts[i].split(";")));
                }
            }
        }

        for (String str : classTypes) {
            if (str == null) {
                continue;
            }
            switch (str.toLowerCase()) {
                case "ras":
                    if (!includeXClsTypes.contains(XClsType.XRds)) {
                        includeXClsTypes.add(XClsType.XRds);
                    }
                    break;
                case "sfcls":
                    if (!includeXClsTypes.contains(XClsType.XSFCls)) {
                        includeXClsTypes.add(XClsType.XSFCls);
                    }
                    if (!includeGeomTypes.contains(GeomType.GeomPnt)) {
                        includeGeomTypes.add(GeomType.GeomPnt);
                    }
                    if (!includeGeomTypes.contains(GeomType.GeomLin)) {
                        includeGeomTypes.add(GeomType.GeomLin);
                    }
                    if (!includeGeomTypes.contains(GeomType.GeomReg)) {
                        includeGeomTypes.add(GeomType.GeomReg);
                    }
                    if (!includeGeomTypes.contains(GeomType.GeomSurface)) {
                        includeGeomTypes.add(GeomType.GeomSurface);
                    }
                    if (!includeGeomTypes.contains(GeomType.GeomEntity)) {
                        includeGeomTypes.add(GeomType.GeomEntity);
                    }
                    break;
                case "sfclsp":
                    if (!includeXClsTypes.contains(XClsType.XSFCls)) {
                        includeXClsTypes.add(XClsType.XSFCls);
                    }
                    if (!includeGeomTypes.contains(GeomType.GeomPnt)) {
                        includeGeomTypes.add(GeomType.GeomPnt);
                    }
                    break;
                case "sfclsl":
                    if (!includeXClsTypes.contains(XClsType.XSFCls)) {
                        includeXClsTypes.add(XClsType.XSFCls);
                    }
                    if (!includeGeomTypes.contains(GeomType.GeomLin)) {
                        includeGeomTypes.add(GeomType.GeomLin);
                    }
                    break;
                case "sfclsr":
                    if (!includeXClsTypes.contains(XClsType.XSFCls)) {
                        includeXClsTypes.add(XClsType.XSFCls);
                    }
                    if (!includeGeomTypes.contains(GeomType.GeomReg)) {
                        includeGeomTypes.add(GeomType.GeomReg);
                    }
                    break;
                case "sfclss":
                    if (!includeXClsTypes.contains(XClsType.XSFCls)) {
                        includeXClsTypes.add(XClsType.XSFCls);
                    }
                    if (!includeGeomTypes.contains(GeomType.GeomSurface)) {
                        includeGeomTypes.add(GeomType.GeomSurface);
                    }
                    break;
                case "sfclse":
                    if (!includeXClsTypes.contains(XClsType.XSFCls)) {
                        includeXClsTypes.add(XClsType.XSFCls);
                    }
                    if (!includeGeomTypes.contains(GeomType.GeomEntity)) {
                        includeGeomTypes.add(GeomType.GeomEntity);
                    }
                    break;
                case "acls":
                    if (!includeXClsTypes.contains(XClsType.XACls)) {
                        includeXClsTypes.add(XClsType.XACls);
                    }
                    break;
                case "ocls":
                    if (!includeXClsTypes.contains(XClsType.XOCls)) {
                        includeXClsTypes.add(XClsType.XOCls);
                    }
                    break;
                case "rcat":
                    if (!includeXClsTypes.contains(XClsType.XRcat)) {
                        includeXClsTypes.add(XClsType.XRcat);
                    }
                    break;
                case "mapset":
                    if (!includeXClsTypes.contains(XClsType.XMapSet)) {
                        includeXClsTypes.add(XClsType.XMapSet);
                    }
                    break;
                case "ncls":
                    if (!includeXClsTypes.contains(XClsType.XGNet)) {
                        includeXClsTypes.add(XClsType.XGNet);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void getLayer(DocumentItem documentItem, int offSet) {
        int layerCount = 0;
        if (documentItem instanceof Map) {
            layerCount = ((Map) documentItem).getLayerCount();
        } else if (documentItem instanceof Scene) {
            layerCount = ((Scene) documentItem).getLayerCount();
        } else if (documentItem instanceof GroupLayer) {
            layerCount = ((GroupLayer) documentItem).getCount();
        } else if (documentItem instanceof Group3DLayer) {
            layerCount = ((Group3DLayer) documentItem).getLayerCount();
        } else {
            return;
        }

        if ((documentItem instanceof Map) || (documentItem instanceof GroupLayer)) {
            // region 父节点为地图或组图层

            for (int i = 0; i < layerCount; i++) {
                MapLayer mapLayer = documentItem instanceof Map ? ((Map) documentItem).getLayer(i) : ((GroupLayer) documentItem).item(i);
                if (mapLayer == null || !mapLayer.getIsValid()) {
                    continue;
                }

                if (mapLayer instanceof NetClsLayer) {
                    LayerSelectComboBoxItem ci = new LayerSelectComboBoxItem(mapLayer, mapLayer.getName(), offSet, includeXClsTypes.contains(XClsType.XGNet), imageMap);
                    this.getItems().add(ci);
                    //GetMaxWidth(mapLayer.getName(), off);
                    getLayer((GroupLayer) mapLayer, offSet + 1);
                } /*else if (mapLayer instanceof MapSetLayer) {
                    int imageIndex = 7;
                    ComboBoxItemValue icbi = new ComboBoxItemValue(layer, off, m_IncludeClsType.Contains(XClsType.MapSet), layer.Name, imageIndex);
                    this.Properties.Items.Add(icbi);
                    GetMaxWidth(layer.Name, off);
                    GetLayer((GroupLayer) layer, off + m_OffSet);
                }*/ else if (mapLayer instanceof FileLayer6x) {
                    LayerSelectComboBoxItem ci = new LayerSelectComboBoxItem(mapLayer, mapLayer.getName(), offSet, false, imageMap);
                    this.getItems().add(ci);
                    //GetMaxWidth(mapLayer.getName(), off);
                    getLayer((GroupLayer) mapLayer, offSet + 1);
                } else if (mapLayer instanceof GroupLayer) {
                    LayerSelectComboBoxItem ci = new LayerSelectComboBoxItem(mapLayer, mapLayer.getName(), offSet, false, imageMap);
                    this.getItems().add(ci);
                    //GetMaxWidth(mapLayer.getName(), off);
                    getLayer((GroupLayer) mapLayer, offSet + 1);
                } else {
                    addMapLayer(mapLayer, offSet);
                }
            }

            // endregion
        } else {
            // region 父节点为场景或三维组图层
            for (int i = 0; i < layerCount; i++) {
                Map3DLayer map3DLayer = documentItem instanceof Scene ? ((Scene) documentItem).getLayer(i) : ((Group3DLayer) documentItem).getLayer(i);
                if (map3DLayer == null || !map3DLayer.getIsValid()) {
                    continue;
                }

                if (map3DLayer instanceof Group3DLayer) {
                    LayerSelectComboBoxItem ci = new LayerSelectComboBoxItem(map3DLayer, map3DLayer.getName(), offSet, false, imageScene);
                    this.getItems().add(ci);
                    //GetMaxWidth(layer.Name, off);
                    getLayer((Group3DLayer) map3DLayer, offSet + 1);
                } else {
                    addMap3DLayer(map3DLayer, offSet);
                }
            }
            // endregion
        }
    }

    private void addMapLayer(MapLayer mapLayer, int offSet) {
        if (mapLayer != null && preAddLayerListeners.size() > 0) {
            if (!firePreAddLayer(new PreAddLayerEvent(this, mapLayer.getData()))) {
                return;
            }
        }
        if (mapLayer instanceof RasterLayer && includeXClsTypes.contains(XClsType.XRds)) {
            // 栅格数据集图层
            LayerSelectComboBoxItem ci = new LayerSelectComboBoxItem(mapLayer, mapLayer.getName(), offSet, true, imageRds);
            this.getItems().add(ci);
            //GetMaxWidth(mapLayer.Name, off);
        } else if (mapLayer instanceof RasterCatalogLayer && includeXClsTypes.contains(XClsType.XRcat)) {
            // 栅格目录图层
            LayerSelectComboBoxItem ci = new LayerSelectComboBoxItem(mapLayer, mapLayer.getName(), offSet, true, imageRcat);
            this.getItems().add(ci);
            //GetMaxWidth(mapLayer.Name, off);
        } else if (mapLayer instanceof VectorLayer) {
            // 矢量图层
            VectorLayer vectorLayer = (VectorLayer) mapLayer;
            // TODO: XClsType 需继承 enum
            if (vectorLayer.getClsType().value() == XClsType.XSFCls.value() && includeXClsTypes.contains(XClsType.XSFCls) && includeGeomTypes.contains(vectorLayer.getGeometryType())) {
                // region 矢量图层（点线区）
                Image sfClsimage = imagePnt;
                GeomType geometryType = vectorLayer.getGeometryType();
                if (GeomType.GeomPnt.equals(geometryType)) {
                    sfClsimage = imagePnt;
                } else if (GeomType.GeomLin.equals(geometryType)) {
                    sfClsimage = imageLin;
                } else if (GeomType.GeomReg.equals(geometryType)) {
                    sfClsimage = imageReg;
                } else if (GeomType.GeomSurface.equals(geometryType)) {
                    sfClsimage = imageSurface;
                } else if (GeomType.GeomEntity.equals(geometryType)) {
                    sfClsimage = imageEntity;
                }
                LayerSelectComboBoxItem ci = new LayerSelectComboBoxItem(mapLayer, mapLayer.getName(), offSet, true, sfClsimage);
                this.getItems().add(ci);
                //GetMaxWidth(mapLayer.Name, off);
                // endregion
            } else if (vectorLayer.getClsType() == XClsType.XACls && includeXClsTypes.contains(XClsType.XACls)) {
                // 矢量图层（注记）
                LayerSelectComboBoxItem ci = new LayerSelectComboBoxItem(mapLayer, mapLayer.getName(), offSet, true, imageAnn);
                this.getItems().add(ci);
                //GetMaxWidth(mapLayer.Name, off);
            }
        } else if (mapLayer instanceof ObjectLayer && includeXClsTypes.contains(XClsType.XOCls)) {
            // 对象类图层
            LayerSelectComboBoxItem ci = new LayerSelectComboBoxItem(mapLayer, mapLayer.getName(), offSet, true, imageOCls);
            this.getItems().add(ci);
            //GetMaxWidth(mapLayer.Name, off);
        }
    }

    private void addMap3DLayer(Map3DLayer map3DLayer, int offSet) {
        if (map3DLayer != null && preAddLayerListeners.size() > 0) {
            if (!firePreAddLayer(new PreAddLayerEvent(this, map3DLayer.getData()))) {
                return;
            }
        }
        if (map3DLayer instanceof ModelLayer) {
            // region 模型图层

            if (map3DLayer.getClsType() == XClsType.XSFCls && includeXClsTypes.contains(XClsType.XSFCls) && includeGeomTypes.contains(map3DLayer.getGeometryType())) {
                Image sfClsimage = imagePnt;
                GeomType geometryType = map3DLayer.getGeometryType();
                if (GeomType.GeomPnt.equals(geometryType)) {
                    sfClsimage = imagePnt;
                } else if (GeomType.GeomLin.equals(geometryType)) {
                    sfClsimage = imageLin;
                } else if (GeomType.GeomReg.equals(geometryType)) {
                    sfClsimage = imageReg;
                } else if (GeomType.GeomSurface.equals(geometryType)) {
                    sfClsimage = imageSurface;
                } else if (GeomType.GeomEntity.equals(geometryType)) {
                    sfClsimage = imageEntity;
                }
                LayerSelectComboBoxItem ci = new LayerSelectComboBoxItem(map3DLayer, map3DLayer.getName(), offSet, true, sfClsimage);
                this.getItems().add(ci);
                //GetMaxWidth(map3DLayer.Name, off);
            }

            // endregion
        } else if (map3DLayer instanceof Vector3DLayer) {
            // region 三维矢量图层

            if (map3DLayer.getClsType() == XClsType.XSFCls && includeXClsTypes.contains(XClsType.XSFCls) && includeGeomTypes.contains(map3DLayer.getGeometryType())) {
                Image sfClsimage = imagePnt;
                GeomType geometryType = map3DLayer.getGeometryType();
                if (GeomType.GeomPnt.equals(geometryType)) {
                    sfClsimage = imagePnt;
                } else if (GeomType.GeomLin.equals(geometryType)) {
                    sfClsimage = imageLin;
                } else if (GeomType.GeomReg.equals(geometryType)) {
                    sfClsimage = imageReg;
                } else if (GeomType.GeomSurface.equals(geometryType)) {
                    sfClsimage = imageSurface;
                } else if (GeomType.GeomEntity.equals(geometryType)) {
                    sfClsimage = imageEntity;
                }
                LayerSelectComboBoxItem ci = new LayerSelectComboBoxItem(map3DLayer, map3DLayer.getName(), offSet, true, sfClsimage);
                this.getItems().add(ci);
                //GetMaxWidth(map3DLayer.Name, off);
            }

            // endregion
        } else if (map3DLayer instanceof LabelLayer && includeXClsTypes.contains(XClsType.XACls)) {
            // 三维注记图层
            LayerSelectComboBoxItem ci = new LayerSelectComboBoxItem(map3DLayer, map3DLayer.getName(), offSet, true, imageAnn);
            this.getItems().add(ci);
            //GetMaxWidth(map3DLayer.Name, off);
        } else if (map3DLayer instanceof TerrainLayer && includeXClsTypes.contains(XClsType.XRds)) {
            // 地形图层
            LayerSelectComboBoxItem ci = new LayerSelectComboBoxItem(map3DLayer, map3DLayer.getName(), offSet, true, imageRds);
            this.getItems().add(ci);
            //GetMaxWidth(map3DLayer.Name, off);
        }
    }

    private ArrayList<PreAddLayerListener> preAddLayerListeners = new ArrayList<>();

    public void addPreAddLayerListener(PreAddLayerListener preAddLayerListener) {
        this.preAddLayerListeners.add(preAddLayerListener);
    }

    public void removePreAddLayerListener(PreAddLayerListener preAddLayerListener) {
        this.preAddLayerListeners.remove(preAddLayerListener);
    }

    public boolean firePreAddLayer(PreAddLayerEvent preAddLayerEvent) {
        boolean rtn = true;
        for (PreAddLayerListener preAddLayerListener : this.preAddLayerListeners) {
            rtn = preAddLayerListener.firePreAddLayer(preAddLayerEvent);
        }
        return rtn;
    }
}
