package com.zondy.mapgis.docitemproperty;

import com.zondy.mapgis.geodatabase.SFeatureCls;
import com.zondy.mapgis.geometry.GeomType;
import com.zondy.mapgis.map.*;
import com.zondy.mapgis.map.event.ClosingDocumentEvent;
import com.zondy.mapgis.map.event.RemoveLayerEvent;
import com.zondy.mapgis.map.event.RemoveMapEvent;
import com.zondy.mapgis.scene.*;
import com.zondy.mapgis.scene.event.G3DGroupLayerRemoveG3DLayerEvent;
import com.zondy.mapgis.scene.event.RemoveSinceEvent;
import com.zondy.mapgis.scene.event.Since3DRemoveG3DLayerEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PropertySheet;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * @Description: 属性面板
 * @Author ysp
 * @Date 2020/3/19
 **/
public class DocItemProperty extends BorderPane {
    //    public Button buttonOK;
//    public Button buttonCancel;
    private Button buttonApply;
    private CheckBox checkBox;
    //    public TreeView<Integer> treeView;
//    public BorderPane borderPane;
    private VBox centerBox;
    public HashMap<Integer, PropertySheet> nodes = new HashMap<>();
    private ArrayList<IProperty> propertyLst = new ArrayList<>();
    private ArrayList<IPropertyEx> propertyExLst = new ArrayList<>();
    private ArrayList<PropertySheet> propertySheetLst = new ArrayList<>();
    private DocumentItem item;//当前文档元素对象
    public DocItemProperty() {
        this.initUI();
    }

    public DocItemProperty(DocumentItem item) {
        this.item = item;
        this.initUI();
        this.initData(item);
    }

    public void displayItem(DocumentItem item) {
        this.item = item;
        this.AddEvent(this.item);
        this.initData(item);
    }
    /**
     * 初始化数据界面
     */
    private void initUI() {
        this.setTop(null);
        centerBox = new VBox();
        centerBox.setAlignment(Pos.TOP_LEFT);
        centerBox.setPrefWidth(300);
        centerBox.setPrefHeight(430);
        this.setCenter(this.centerBox);

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPrefWidth(300);
        hbox.setPrefHeight(40);
        hbox.setSpacing(10);
        hbox.setPadding(new Insets(0, 10, 0, 10));
        checkBox = new CheckBox("立即更新");
        checkBox.setSelected(false);
        checkBox.setPrefWidth(120);
        checkBox.setPrefHeight(23);

        checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    buttonApplyActon(null);
                }
                for (int i = 0; i < propertyLst.size(); i++) {
                    propertyLst.get(i).setImmediatelyUpdate(newValue);
                }
                for (int i = 0; i < propertyExLst.size(); i++) {
                    propertyExLst.get(i).setImmediatelyUpdate(newValue);
                }
                buttonApply.setDisable(newValue);
            }
        });

        hbox.getChildren().add(checkBox);
        buttonApply = new Button("应用");
        buttonApply.setDisable(false);
        buttonApply.setPrefWidth(60);
        buttonApply.setPrefHeight(23);
        buttonApply.setOnAction(this::buttonApplyActon);
        hbox.getChildren().add(buttonApply);
        this.setBottom(hbox);
        this.setPadding(new Insets(12, 12, 0, 12));
    }

    //region 文档元素移后清空面板
    /**
     * 监听文档元素移除事件
     */
    private void AddEvent(DocumentItem item){
        if (item != null){
            if (item instanceof Document){
                Document doc =(Document)item;
                doc.removeClosingDocumentListener(this::doc_closingDocument);
                doc.addClosingDocumentListener(this::doc_closingDocument);
            }
            else {
                DocumentItem par = item.getParent();
                if (par instanceof Maps){
                    Maps maps = (Maps)par;
                    maps.removeRemoveMapListener(this::maps_removeRemoveMap);
                    maps.addRemoveMapListener(this::maps_removeRemoveMap);
                    this.AddEvent(maps.getParent());
                }else if (par instanceof Scenes){
                    Scenes scenes = (Scenes)par;
                    scenes.removeRemoveSinceListener(this::scenes_removeRemoveScene);
                    scenes.addRemoveSinceListener(this::scenes_removeRemoveScene);
                    this.AddEvent(scenes.getParent());
                }
                if (par instanceof Map){
                    Map map = (Map)par;
                    map.removeRemoveLayerListener(this::map_removeRemoveMap);
                    map.addRemoveLayerListener(this::map_removeRemoveMap);
                    this.AddEvent(map.getParent());
                }else if (par instanceof Scene){
                    Scene scene = (Scene)par;
                    scene.removeRemoveLayerListener(this::scene_removeRemoveLayer);
                    scene.addRemoveLayerListener(this::scene_removeRemoveLayer);
                    this.AddEvent(scene.getParent());
                }
                else if (par instanceof GroupLayer){
                    GroupLayer groupLayer = (GroupLayer)par;
                    groupLayer.removeRemoveLayerListener(this::groupLayer_removeRemoveLayer);
                    groupLayer.addRemoveLayerListener(this::groupLayer_removeRemoveLayer);
                    this.AddEvent(groupLayer.getParent());
                }
                else if (par instanceof Group3DLayer){
                    Group3DLayer group3DLayer = (Group3DLayer)par;
                    group3DLayer.removeRemoveLayerListener(this::group3DLayer_removeRemoveLayer);
                    group3DLayer.addRemoveLayerListener(this::group3DLayer_removeRemoveLayer);
                    this.AddEvent(group3DLayer.getParent());
                }
            }
        }
    }

    private void scene_removeRemoveLayer(Since3DRemoveG3DLayerEvent since3DRemoveG3DLayerEvent) {
        if(item instanceof Map3DLayer)
        {
            if (since3DRemoveG3DLayerEvent.getArgs().getRemovedLayer().getHandle() == item.getHandle()){
                this.initData(null);
                this.item = null;
            }
        }
    }

    private void map_removeRemoveMap(RemoveLayerEvent removeLayerEvent) {
        if(item instanceof MapLayer)
        {
            if (removeLayerEvent.getArgs().getRemovedLayer().getHandle() == item.getHandle()){
                this.initData(null);
                this.item = null;
            }
        }
    }

    private void group3DLayer_removeRemoveLayer(G3DGroupLayerRemoveG3DLayerEvent g3DGroupLayerRemoveG3DLayerEvent) {
        if(item instanceof Map3DLayer)
        {
            if (g3DGroupLayerRemoveG3DLayerEvent.getArgs().getRemovedLayer().getHandle() == item.getHandle()){
                this.initData(null);
                this.item = null;
            }
        }
    }

    private void groupLayer_removeRemoveLayer(RemoveLayerEvent removeLayerEvent) {
        if(item instanceof MapLayer)
        {
            if (removeLayerEvent.getArgs().getRemovedLayer().getHandle() == item.getHandle()){
                this.initData(null);
                this.item = null;
            }
        }
    }

    private void scenes_removeRemoveScene(RemoveSinceEvent removeSinceEvent) {
        if(item instanceof Scene)
        {
            if (removeSinceEvent.getArgs().getRemovedScene().getHandle() == item.getHandle()){
                this.initData(null);
                this.item = null;
            }
        }
    }

    private void maps_removeRemoveMap(RemoveMapEvent removeMapEvent) {
        if(item instanceof Map)
        {
            if (removeMapEvent.getArgs().getRemovedMap().getHandle() == item.getHandle()){
                this.initData(null);
                this.item = null;
            }
        }
    }

    private void doc_closingDocument(ClosingDocumentEvent closingDocumentEvent) {
        this.initData(null);
        this.item = null;
    }
    //endregion
    /**
     * @param item 初始化数据
     */
    private void initData(DocumentItem item) {
        propertyLst.clear();
        propertySheetLst.clear();
        centerBox.getChildren().clear();
        //测试代码
        if (item instanceof MapLayer) {
            if (item instanceof VectorLayer) {
                if (((VectorLayer) item).getData() instanceof SFeatureCls) {
                    DocItemPropertyClasses.SfClsDataSourceProperty sfClsDataSourceProperty = new DocItemPropertyClasses.SfClsDataSourceProperty();
                    sfClsDataSourceProperty.setDocItem(item);
                    propertyLst.add(sfClsDataSourceProperty);
                    propertySheetLst.add(sfClsDataSourceProperty.getPropertySheet());

                    DocItemPropertyClasses.SfOrAClsCommonProperty sfOrAClsCommonProperty = new DocItemPropertyClasses.SfOrAClsCommonProperty();
                    sfOrAClsCommonProperty.setDocItem(item);
                    propertyLst.add(sfOrAClsCommonProperty);
                    propertySheetLst.add(sfOrAClsCommonProperty.getPropertySheet());

                    GeomType geomType = ((VectorLayer) item).getGeometryType();
                    if (geomType.equals(GeomType.GeomReg)) {
                        DocItemPropertyClasses.RegSFClsShowProperty showProperty = new DocItemPropertyClasses.RegSFClsShowProperty();
                        showProperty.setDocItem(item);
                        propertyLst.add(showProperty);
                        propertySheetLst.add(showProperty.getPropertySheet());
                    } else if (geomType.equals(GeomType.GeomLin)) {
                        DocItemPropertyClasses.LinSFClsShowProperty showProperty = new DocItemPropertyClasses.LinSFClsShowProperty();
                        showProperty.setDocItem(item);
                        propertyLst.add(showProperty);
                        propertySheetLst.add(showProperty.getPropertySheet());
                        if (item.getParent() instanceof NetClsLayer) {
                            DocItemPropertyClasses.EdgeLayerProperty edgeLayerProperty = new DocItemPropertyClasses.EdgeLayerProperty();
                            showProperty.setDocItem(item);
                            propertyLst.add(showProperty);
                            propertySheetLst.add(showProperty.getPropertySheet());
                        }
                    } else if (geomType.equals(GeomType.GeomPnt)) {
//                        DocItemPropertyClasses.ShowProperty showProperty = new DocItemPropertyClasses.ShowProperty();
                        DocItemPropertyClasses.PntSFClsShowProperty showProperty = new DocItemPropertyClasses.PntSFClsShowProperty();
                        showProperty.setDocItem(item);
                        propertyLst.add(showProperty);
                        propertySheetLst.add(showProperty.getPropertySheet());
                        if (item.getParent() instanceof NetClsLayer) {
                            int clsID = ((VectorLayer) item).getData().getClsID();
                            SNodeDispInfo sNodeDispInfo = ((NetClsLayer) item.getParent()).getSNodeDispInfo(clsID);
                            CNodeDispInfo cNodeDispInfo = ((NetClsLayer) item.getParent()).getCNodeDispInfo(clsID);
                            if (sNodeDispInfo != null) {
                                DocItemPropertyClasses.SNodeLayerProperty sNodeLayerProperty = new DocItemPropertyClasses.SNodeLayerProperty();
                                sNodeLayerProperty.setDocItem(item);
                                propertyLst.add(sNodeLayerProperty);
                                propertySheetLst.add(sNodeLayerProperty.getPropertySheet());
                            }
                            if (cNodeDispInfo != null) {
                                DocItemPropertyClasses.CNodeLayerProperty cNodeLayerProperty = new DocItemPropertyClasses.CNodeLayerProperty();
                                cNodeLayerProperty.setDocItem(item);
                                propertyLst.add(cNodeLayerProperty);
                                propertySheetLst.add(cNodeLayerProperty.getPropertySheet());
                            }
                        }
                    } else {
                        DocItemPropertyClasses.ShowProperty showProperty = new DocItemPropertyClasses.ShowProperty();
                        showProperty.setDocItem(item);
                        propertyLst.add(showProperty);
                        propertySheetLst.add(showProperty.getPropertySheet());
                    }
                } else {
                    DocItemPropertyClasses.AClsDataSourceProperty propertyDataSource = new DocItemPropertyClasses.AClsDataSourceProperty();
                    propertyDataSource.setDocItem(item);
                    propertyLst.add(propertyDataSource);
                    propertySheetLst.add(propertyDataSource.getPropertySheet());

                    DocItemPropertyClasses.SfOrAClsCommonProperty sfOrAClsCommonProperty = new DocItemPropertyClasses.SfOrAClsCommonProperty();
                    sfOrAClsCommonProperty.setDocItem(item);
                    propertyLst.add(sfOrAClsCommonProperty);
                    propertySheetLst.add(sfOrAClsCommonProperty.getPropertySheet());

                    DocItemPropertyClasses.ShowProperty showProperty = new DocItemPropertyClasses.ShowProperty();
                    showProperty.setDocItem(item);
                    propertyLst.add(showProperty);
                    propertySheetLst.add(showProperty.getPropertySheet());
                }
            } else if (item instanceof RasterLayer) {
                DocItemPropertyClasses.RasDataSourceProperty dataSourceProperty = new DocItemPropertyClasses.RasDataSourceProperty();
                dataSourceProperty.setDocItem(item);
                propertyLst.add(dataSourceProperty);
                propertySheetLst.add(dataSourceProperty.getPropertySheet());

                DocItemPropertyClasses.RasCommonProperty property = new DocItemPropertyClasses.RasCommonProperty();
                property.setDocItem(item);
                propertyLst.add(property);
                propertySheetLst.add(property.getPropertySheet());

                DocItemPropertyClasses.RasDisplayCommonProperty property2 = new DocItemPropertyClasses.RasDisplayCommonProperty();
                property2.setDocItem(item);
                propertyLst.add(property2);
                propertySheetLst.add(property2.getPropertySheet());
            } else if (item instanceof RasterCatalogLayer) {
                DocItemPropertyClasses.RasDataSourceProperty dataSourceProperty = new DocItemPropertyClasses.RasDataSourceProperty();
                dataSourceProperty.setDocItem(item);
                propertyLst.add(dataSourceProperty);
                propertySheetLst.add(dataSourceProperty.getPropertySheet());

                DocItemPropertyClasses.RasterCatalogCommonProperty property = new DocItemPropertyClasses.RasterCatalogCommonProperty();
                property.setDocItem(item);
                propertyLst.add(property);
                propertySheetLst.add(property.getPropertySheet());

                DocItemPropertyClasses.RcatDisplayCommonProperty property2 = new DocItemPropertyClasses.RcatDisplayCommonProperty();
                property2.setDocItem(item);
                propertyLst.add(property2);
                propertySheetLst.add(property2.getPropertySheet());
            } else if (item instanceof GroupLayer) {
                DocItemPropertyClasses.ObjClsCommonProperty property = new DocItemPropertyClasses.ObjClsCommonProperty();
                property.setDocItem(item);
                propertyLst.add(property);
                propertySheetLst.add(property.getPropertySheet());
            } else if (item instanceof FileLayer6x) {
                DocItemPropertyClasses.File6xCommonProperty property = new DocItemPropertyClasses.File6xCommonProperty();
                property.setDocItem(item);
                propertyLst.add(property);
                propertySheetLst.add(property.getPropertySheet());
            } else if (item instanceof NetClsLayer) {
                DocItemPropertyClasses.NetClsCommonProperty property = new DocItemPropertyClasses.NetClsCommonProperty();
                property.setDocItem(item);
                propertyLst.add(property);
                propertySheetLst.add(property.getPropertySheet());
            } else if (item instanceof ObjectLayer) {
                DocItemPropertyClasses.OClsDataSourceProperty propertyDataSource = new DocItemPropertyClasses.OClsDataSourceProperty();
                propertyDataSource.setDocItem(item);
                propertyLst.add(propertyDataSource);
                propertySheetLst.add(propertyDataSource.getPropertySheet());

                DocItemPropertyClasses.ObjClsCommonProperty property = new DocItemPropertyClasses.ObjClsCommonProperty();
                property.setDocItem(item);
                propertyLst.add(property);
                propertySheetLst.add(property.getPropertySheet());
            } else if (item instanceof ImageLayer) {
//
                DocItemPropertyClasses.ServerLayerDataSourceProperty propertyDataSource = new DocItemPropertyClasses.ServerLayerDataSourceProperty();
                propertyDataSource.setDocItem(item);
                propertyLst.add(propertyDataSource);
                propertySheetLst.add(propertyDataSource.getPropertySheet());

                DocItemPropertyClasses.ServerLayerProperty serverLayerProperty = new DocItemPropertyClasses.ServerLayerProperty();
                serverLayerProperty.setDocItem(item);
                propertyLst.add(serverLayerProperty);
                propertySheetLst.add(serverLayerProperty.getPropertySheet());

                DocItemPropertyClasses.TileProperty tileProperty = new DocItemPropertyClasses.TileProperty();
                tileProperty.setDocItem(item);
                propertyLst.add(tileProperty);
                propertySheetLst.add(tileProperty.getPropertySheet());
            }

        } else if (item instanceof Map3DLayer) {
            if (item instanceof ModelLayer) {
                DocItemPropertyClasses.SFClsDataSource3DProperty propertyDataSource = new DocItemPropertyClasses.SFClsDataSource3DProperty();
                propertyDataSource.setDocItem(item);
                propertyLst.add(propertyDataSource);
                propertySheetLst.add(propertyDataSource.getPropertySheet());

                DocItemPropertyClasses.Layer3DCommonProperty layer3DCommonProperty = new DocItemPropertyClasses.Layer3DCommonProperty();
                layer3DCommonProperty.setDocItem(item);
                propertyLst.add(layer3DCommonProperty);
                propertySheetLst.add(layer3DCommonProperty.getPropertySheet());

                DocItemPropertyClasses.ModelLayerProperty property = new DocItemPropertyClasses.ModelLayerProperty();
                property.setDocItem(item);
                propertyLst.add(property);
                propertySheetLst.add(property.getPropertySheet());

            } else if (item instanceof Vector3DLayer) {
                DocItemPropertyClasses.SFClsDataSource3DProperty propertyDataSource = new DocItemPropertyClasses.SFClsDataSource3DProperty();
                propertyDataSource.setDocItem(item);
                propertyLst.add(propertyDataSource);
                propertySheetLst.add(propertyDataSource.getPropertySheet());

                DocItemPropertyClasses.Layer3DCommonProperty layer3DCommonProperty = new DocItemPropertyClasses.Layer3DCommonProperty();
                layer3DCommonProperty.setDocItem(item);
                propertyLst.add(layer3DCommonProperty);
                propertySheetLst.add(layer3DCommonProperty.getPropertySheet());

                GeomType geomType = ((Vector3DLayer) item).getGeometryType();
                if (geomType == GeomType.GeomPnt) {
                    DocItemPropertyClasses.Pnt3DLayerProperty property = new DocItemPropertyClasses.Pnt3DLayerProperty();
                    property.setDocItem(item);
                    propertyLst.add(property);
                    propertySheetLst.add(property.getPropertySheet());
                } else if (geomType == GeomType.GeomLin) {
                    DocItemPropertyClasses.Lin3DLayerProperty property = new DocItemPropertyClasses.Lin3DLayerProperty();
                    property.setDocItem(item);
                    propertyLst.add(property);
                    propertySheetLst.add(property.getPropertySheet());
                } else if (geomType == GeomType.GeomReg) {
                    DocItemPropertyClasses.Reg3DLayerProperty property = new DocItemPropertyClasses.Reg3DLayerProperty();
                    property.setDocItem(item);
                    propertyLst.add(property);
                    propertySheetLst.add(property.getPropertySheet());
                }
            } else if (item instanceof TerrainLayer) {
                DocItemPropertyClasses.SpatialDataSource3DProperty propertyDataSource = new DocItemPropertyClasses.SpatialDataSource3DProperty();
                propertyDataSource.setDocItem(item);
                propertyLst.add(propertyDataSource);
                propertySheetLst.add(propertyDataSource.getPropertySheet());

                DocItemPropertyClasses.Layer3DCommonProperty layer3DCommonProperty = new DocItemPropertyClasses.Layer3DCommonProperty();
                layer3DCommonProperty.setDocItem(item);
                propertyLst.add(layer3DCommonProperty);
                propertySheetLst.add(layer3DCommonProperty.getPropertySheet());

                DocItemPropertyClasses.TerrainLayerProperty terrainLayerProperty = new DocItemPropertyClasses.TerrainLayerProperty();
                terrainLayerProperty.setDocItem(item);
                propertyLst.add(terrainLayerProperty);
                propertySheetLst.add(terrainLayerProperty.getPropertySheet());
            } else if (item instanceof LabelLayer) {
                DocItemPropertyClasses.AClsDataSource3DProperty propertyDataSource = new DocItemPropertyClasses.AClsDataSource3DProperty();
                propertyDataSource.setDocItem(item);
                propertyLst.add(propertyDataSource);
                propertySheetLst.add(propertyDataSource.getPropertySheet());

                DocItemPropertyClasses.Layer3DCommonProperty layer3DCommonProperty = new DocItemPropertyClasses.Layer3DCommonProperty();
                layer3DCommonProperty.setDocItem(item);
                propertyLst.add(layer3DCommonProperty);
                propertySheetLst.add(layer3DCommonProperty.getPropertySheet());

                DocItemPropertyClasses.LabelLayerProperty property = new DocItemPropertyClasses.LabelLayerProperty();
                property.setDocItem(item);
                propertyLst.add(property);
                propertySheetLst.add(property.getPropertySheet());
            } else if (item instanceof ModelCacheLayer) {
                DocItemPropertyClasses.CacheDataSource3DProperty propertyDataSource = new DocItemPropertyClasses.CacheDataSource3DProperty();
                propertyDataSource.setDocItem(item);
                propertyLst.add(propertyDataSource);
                propertySheetLst.add(propertyDataSource.getPropertySheet());

                DocItemPropertyClasses.CacheLayerProperty property = new DocItemPropertyClasses.CacheLayerProperty();
                property.setDocItem(item);
                propertyLst.add(property);
                propertySheetLst.add(property.getPropertySheet());
            } else if (item instanceof PointCloudLayer) {
                DocItemPropertyClasses.PointCloudDataSource3DProperty propertyDataSource = new DocItemPropertyClasses.PointCloudDataSource3DProperty();
                propertyDataSource.setDocItem(item);
                propertyLst.add(propertyDataSource);
                propertySheetLst.add(propertyDataSource.getPropertySheet());

//                    DocItemPropertyClasses.PointCloudLayerProperty property = new DocItemPropertyClasses.PointCloudLayerProperty();
//                    property.setDocItem(item);
//                    propertyLst.add(property);
//                    propertySheetLst.add(property.getPropertySheet());
            } else if (item instanceof ServerLayer) {
                DocItemPropertyClasses.ServerLayerDataSourceProperty propertyDataSource = new DocItemPropertyClasses.ServerLayerDataSourceProperty();
                propertyDataSource.setDocItem(item);
                propertyLst.add(propertyDataSource);
                propertySheetLst.add(propertyDataSource.getPropertySheet());

                DocItemPropertyClasses.ServerLayerProperty serverLayerProperty = new DocItemPropertyClasses.ServerLayerProperty();
                serverLayerProperty.setDocItem(item);
                propertyLst.add(serverLayerProperty);
                propertySheetLst.add(serverLayerProperty.getPropertySheet());

                DocItemPropertyClasses.TileProperty tileProperty = new DocItemPropertyClasses.TileProperty();
                tileProperty.setDocItem(item);
                propertyLst.add(tileProperty);
                propertySheetLst.add(tileProperty.getPropertySheet());
            } else if (item instanceof MapRefLayer) {
                DocItemPropertyClasses.SpatialDataSource3DProperty propertyDataSource = new DocItemPropertyClasses.SpatialDataSource3DProperty();
                propertyDataSource.setDocItem(item);
                propertyLst.add(propertyDataSource);
                propertySheetLst.add(propertyDataSource.getPropertySheet());

                DocItemPropertyClasses.MapRefLayerProperty property = new DocItemPropertyClasses.MapRefLayerProperty();
                property.setDocItem(item);
                propertyLst.add(property);
                propertySheetLst.add(property.getPropertySheet());
            }
        } else if (item instanceof com.zondy.mapgis.map.Map) {
            DocItemPropertyClasses.MapCommonProperty mapCommonProperty = new DocItemPropertyClasses.MapCommonProperty();
            mapCommonProperty.setDocItem(item);
            propertyLst.add(mapCommonProperty);
            propertySheetLst.add(createPropertySheet(mapCommonProperty.getPropertyItems()));
//            propertySheetLst.add(mapCommonProperty.getPropertySheet());
        } else if (item instanceof com.zondy.mapgis.scene.Scene) {
            DocItemPropertyClasses.SceneProperty sceneProperty = new DocItemPropertyClasses.SceneProperty();
            sceneProperty.setDocItem(item);
            propertyLst.add(sceneProperty);
            propertySheetLst.add(createPropertySheet(sceneProperty.getPropertyItems()));
//            propertySheetLst.add(sceneProperty.getPropertySheet());
        } else if (item instanceof com.zondy.mapgis.map.Document) {
            DocItemPropertyClasses.DocumentLayoutProperty documentLayoutProperty = new DocItemPropertyClasses.DocumentLayoutProperty();
            documentLayoutProperty.setItem((Document) item);
//            propertyLst.add(documentLayoutProperty);
            propertyExLst.add(documentLayoutProperty);
            propertySheetLst.add(documentLayoutProperty.getPropertySheet());
        }
        for (int i = 0; i < propertyLst.size(); i++) {
            propertyLst.get(i).setImmediatelyUpdate(checkBox.isSelected());
        }
        for (int i = 0; i < propertyExLst.size(); i++) {
            propertyExLst.get(i).setImmediatelyUpdate(checkBox.isSelected());
        }
        centerBox.getChildren().addAll(propertySheetLst);
    }

    /**
     * 根据属性编辑项集合创建属性表
     *
     * @param items 属性编辑项集合
     * @return
     */
    public static PropertySheet createPropertySheet(PropertyItem[] items) {
        PropertySheet propertySheet = null;
        if (items != null) {
            ObservableList<PropertySheet.Item> list = FXCollections.observableArrayList();
            list.addAll(items);
            propertySheet = new PropertySheet(list);
            propertySheet.setPadding(new Insets(0, 5, 0, 5));
            propertySheet.setSearchBoxVisible(false);
            propertySheet.setMode(PropertySheet.Mode.CATEGORY);
            propertySheet.setModeSwitcherVisible(false);
        }
        return propertySheet;
    }
//    private void initTreeView(DocumentItem item) {
//        if (item != null) {
//            this.treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<Integer>>() {
//                @Override
//                public void changed(ObservableValue<? extends TreeItem<Integer>> observable, TreeItem<Integer> oldValue, TreeItem<Integer> newValue) {
//                    borderPane.setCenter(null);
//                    if ((int) ((TreeItem) newValue).getValue() == 1) {
//                        //数据源
//                        borderPane.setCenter(nodes.get(1));
//                    } else if ((int) ((TreeItem) newValue).getValue() == 2 || (int) ((TreeItem) newValue).getValue() == 3) {
//                        //通用属性、常规
//                        System.out.println("常规");
//                        borderPane.setCenter(nodes.get(3));
////                        borderPane.setCenter(commonPropertySheet);
//                    } else if ((int) ((TreeItem) newValue).getValue() == 4 || (int) ((TreeItem) newValue).getValue() == 5) {
//                        System.out.println("显示");
//                        borderPane.setCenter(nodes.get(5));
////                        borderPane.setCenter(showPropertySheet);
//                    }
//                }
//            });
//            this.treeView.setCellFactory(param -> new TextFieldTreeCell<Integer>() {
//                @Override
//                public void updateItem(Integer item, boolean empty) {
//                    super.updateItem(item, empty);
//                    setText(NODE_TYPE.get(item));
//                }
//            });
////            ImageView rootIcon = new ImageView(CommonFuns.getImageByColor(Color.RED, 16, 16));
//            TreeItem<Integer> rootTreeItem = new TreeItem<>(0);//new TreeItem<>("Inbox", rootIcon);
//            rootTreeItem.setExpanded(true);
//            if (item instanceof MapLayer) {
//                TreeItem<Integer> treeItem1 = new TreeItem<>(1);
//                rootTreeItem.getChildren().add(treeItem1);
//                TreeItem<Integer> treeItem2 = new TreeItem<>(2);
//                rootTreeItem.getChildren().add(treeItem2);
//                treeItem2.setExpanded(true);
//                treeItem2.getChildren().add(new TreeItem<>(3));
//
//                TreeItem<Integer> treeItem3 = new TreeItem<>(4);
//                rootTreeItem.getChildren().add(treeItem3);
//                treeItem3.setExpanded(true);
//                treeItem3.getChildren().add(new TreeItem<>(5));
//
//            } else if (item instanceof Map) {
//                TreeItem<Integer> treeItem1 = new TreeItem<>(6);
//                rootTreeItem.getChildren().add(treeItem1);
//                treeItem1.setExpanded(true);
//
//                TreeItem<Integer> treeItem2 = new TreeItem<>(7);
//                treeItem1.getChildren().add(treeItem2);
//                TreeItem<Integer> treeItem3 = new TreeItem<>(8);
//                treeItem1.getChildren().add(treeItem3);
//                TreeItem<Integer> treeItem4 = new TreeItem<>(9);
//                treeItem1.getChildren().add(treeItem4);
//
//            }
//            this.treeView.setRoot(rootTreeItem);
//            this.treeView.setShowRoot(false);
//        }
//    }
    //region 长度单位（SRefLenUnit）
//    private static final java.util.Map<Integer, String> NODE_TYPE = new ImmutableMap.Builder<Integer, String>().
//            put(1, "数据源").
//            put(2, "通用属性").
//            put(3, "常规").
//            put(4, "配置属性").
//            put(5, "显示").
//            put(6, "地图").
//            put(7, "常规").
//            put(8, "位置").
//            put(9, "显示").build();
//    private void selectedItemChanged(Observable observable, Object oldValue, Object newValue) {
//    }

//    public void buttonOKAction(ActionEvent actionEvent) {
//        this.buttonApplyActon(null);
//    }


    public void buttonApplyActon(ActionEvent actionEvent) {
        IProperty property;
        for (int i = 0; i < propertyLst.size(); i++) {
            propertyLst.get(i).apply();
        }
        for (int i = 0; i < propertyExLst.size(); i++) {
            propertyExLst.get(i).apply();
        }
    }

}
