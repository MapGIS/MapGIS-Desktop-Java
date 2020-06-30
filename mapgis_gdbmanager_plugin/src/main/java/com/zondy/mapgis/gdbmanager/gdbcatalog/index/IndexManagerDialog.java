package com.zondy.mapgis.gdbmanager.gdbcatalog.index;

import com.zondy.mapgis.att.Field;
import com.zondy.mapgis.att.Fields;
import com.zondy.mapgis.base.MessageBox;
import com.zondy.mapgis.base.XFunctions;
import com.zondy.mapgis.base.XString;
import com.zondy.mapgis.geodatabase.config.ConnectType;
import com.zondy.mapgis.geodatabase.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.util.Optional;

/**
 * 索引管理
 *
 * @author CR
 * @file IndexManagerDialog.java
 * @brief 索引管理
 * @create 2020-02-26.
 */
public class IndexManagerDialog extends Dialog
{
    private DataBase db;
    private XClsType clsType;
    private int clsID;
    private ConnectType connectType = ConnectType.DBPG;
    private final TableView<SpatialIndex> tableViewSpatial = new TableView<>();
    private final TableView<AttIndex> tableViewAtt = new TableView<>();
    private Button buttonCreate;
    private Button buttonRemove;
    private Button buttonRefresh;
    private TabPane tabPane;

    public IndexManagerDialog(DataBase db, XClsType clsType, int clsID)
    {
        this.setTitle("索引管理");

        this.db = db;
        this.clsType = clsType;
        this.clsID = clsID;
        if (this.db != null)
        {
            this.connectType = this.db.getServer().getConnectType();
        }
        boolean isLocal = ConnectType.Local.equals(this.connectType);

        //region 空间索引
        if (!XClsType.XOCls.equals(clsType))
        {
            TableColumn<SpatialIndex, String> colSpatialName = new TableColumn<>("名称");
            TableColumn<SpatialIndex, Boolean> colSpatialExist = new TableColumn<>("是否存在");
            TableColumn<SpatialIndex, Boolean> colSpatialValid = new TableColumn<>("是否有效");
            this.tableViewSpatial.getColumns().addAll(colSpatialName, colSpatialExist, colSpatialValid);
            this.tableViewSpatial.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            colSpatialName.setCellValueFactory(new PropertyValueFactory<>("name"));
            colSpatialExist.setCellValueFactory(new PropertyValueFactory<>("exist"));
            colSpatialValid.setCellValueFactory(new PropertyValueFactory<>("valid"));

            colSpatialExist.setCellFactory(param -> new BooleanTableCell());
            colSpatialValid.setCellFactory(param -> new BooleanTableCell());

            this.tableViewSpatial.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) ->
            {
                this.buttonCreate.setDisable(nv == null || nv.isExist());
                boolean canRemove = nv != null && nv.isExist() && !(isLocal && SIndexType.UnionRect.equals(nv.getIndexType()));
                this.buttonRemove.setDisable(!canRemove);
            });
        }
        //endregion

        //region 属性索引
        if (XClsType.XOCls.equals(clsType) || !isLocal)
        {
            TableColumn<AttIndex, String> colAttName = new TableColumn<>("名称");
            TableColumn<AttIndex, String> colAttFields = new TableColumn<>("字段");
            TableColumn<AttIndex, String> colAttDescription = new TableColumn<>("描述");
            this.tableViewAtt.getColumns().addAll(colAttName, colAttDescription, colAttFields);
            this.tableViewAtt.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            colAttName.setCellValueFactory(new PropertyValueFactory<>("name"));
            colAttFields.setCellValueFactory(new PropertyValueFactory<>("fields"));
            colAttDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

            this.tableViewAtt.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) ->
            {
                this.buttonRemove.setDisable(nv != null && nv.getFields() == "OID");
            });
        }
        //endregion

        //region 界面布局及初始化
        Node root = null;
        if (clsType.equals(XClsType.XOCls))
        {
            root = new VBox(this.tableViewAtt);
        } else if (isLocal)
        {
            root = new VBox(this.tableViewSpatial);
        } else
        {
            Tab tabSpatial = new Tab("空间索引", this.tableViewSpatial);
            tabSpatial.setClosable(false);
            Tab tabAtt = new Tab("属性索引", this.tableViewAtt);
            tabAtt.setClosable(false);
            this.tabPane = new TabPane(tabSpatial, tabAtt);
            root = this.tabPane;

            this.tabPane.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) ->
            {
                if (nv.equals(this.tabPane.getTabs().get(0)))
                {
                    SpatialIndex sIndex = this.tableViewSpatial.getSelectionModel().getSelectedItem();
                    if (sIndex != null)
                    {
                        this.tableViewSpatial.getSelectionModel().select(null);
                        this.tableViewSpatial.getSelectionModel().select(sIndex);
                    }
                } else
                {
                    this.initAttIndex();
                    this.buttonCreate.setDisable(false);
                }
            });
        }

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setPrefSize(450, 360);
        dialogPane.setMinSize(450, 360);
        dialogPane.setContent(root);
        if (XFunctions.isSystemWindows())
        {
            this.setResizable(true);
        }

        ButtonType buttonTypeCreate = new ButtonType("创建", ButtonBar.ButtonData.LEFT);
        ButtonType buttonTypeRemove = new ButtonType("移除", ButtonBar.ButtonData.LEFT);
        ButtonType buttonTypeRefresh = new ButtonType("刷新", ButtonBar.ButtonData.LEFT);
        dialogPane.getButtonTypes().addAll(buttonTypeCreate, buttonTypeRemove, buttonTypeRefresh, ButtonType.CLOSE);
        //endregion

        //region 按钮事件：创建、移除、刷新
        //创建
        this.buttonCreate = (Button) dialogPane.lookupButton(buttonTypeCreate);
        this.buttonCreate.addEventFilter(ActionEvent.ACTION, event ->
        {
            if (this.db != null)
            {
                if (isLocal || this.tabPane.getTabs().get(0).isSelected())
                {
                    //region 创建空间索引
                    SpatialIndex sIndex = this.tableViewSpatial.getSelectionModel().getSelectedItem();
                    if (sIndex != null)
                    {
                        SIndexType indexType = sIndex.getIndexType();
                        if (SIndexType.RTree.equals(indexType))
                        {
                            String errorMsg = "R树索引创建失败。";
                            if (!this.createSIndex(new RTreeIndex()))
                            {
                                MessageBox.information(errorMsg);
                            } else
                            {
                                sIndex.setExist(true);
                                sIndex.setValid(true);
                                this.tableViewSpatial.getSelectionModel().select(null);
                                this.tableViewSpatial.getSelectionModel().select(sIndex);
                            }
                        } else if (SIndexType.UnionRect.equals(indexType))
                        {
                            String errorMsg = "矩形范围索引创建失败。";
                            if (!this.createSIndex(new RectIndex()))
                            {
                                MessageBox.information(errorMsg);
                            }
                        } else if (SIndexType.UnionRectExt.equals(indexType))
                        {
                            String errorMsg = "要素外包索引创建失败。";
                            if (!this.createSIndex(new RectExtIndex()))
                            {
                                MessageBox.information(errorMsg);
                            }
                        } else if (SIndexType.FrameNo.equals(indexType))
                        {
                            IVectorCls vCls = null;
                            if (XClsType.XSFCls.equals(this.clsType))
                            {
                                vCls = new SFeatureCls(this.db);
                            } else if (XClsType.XACls.equals(this.clsType))
                            {
                                vCls = new AnnotationCls(this.db);
                            }
                            if (vCls.open(this.clsID, 0) > 0)
                            {
                                if (vCls.getObjCount() <= 0)
                                {
                                    MessageBox.information("当前数据为空,无法创建图幅索引。");
                                } else
                                {
                                    int num = (int) Math.sqrt(vCls.getObjCount() / 1500.0);
                                    AddFrameIndexDialog dlg = new AddFrameIndexDialog(vCls.getRange(), (num < 1 ? 1 : num), vCls.getURL());
                                    dlg.initOwner(this.getCurrentWindow());
                                    if (Optional.of(ButtonType.OK).equals(dlg.showAndWait()))
                                    {
                                        String errorMsg = "创建图幅索引失败。";
                                        if (!this.createSIndex(dlg.getFrameIndex()))
                                        {
                                            MessageBox.information(errorMsg);
                                        }
                                    }
                                }
                                vCls.close();
                            }
                        } else if (SIndexType.Aggregation.equals(indexType))
                        {
                            IVectorCls vCls = null;
                            if (XClsType.XSFCls.equals(this.clsType))
                            {
                                vCls = new SFeatureCls(this.db);
                            } else if (XClsType.XACls.equals(this.clsType))
                            {
                                vCls = new AnnotationCls(this.db);
                            }
                            if (vCls.open(this.clsID, 0) > 0)
                            {
                                Fields flds = vCls.getFields();
                                if (!this.canCreateAggregation(flds))
                                {
                                    MessageBox.information("没有可供选择的聚集字段,无法创建聚集索引。");
                                } else
                                {
                                    AddAggregationIndexDialog dlg = new AddAggregationIndexDialog(flds);
                                    dlg.initOwner(this.getCurrentWindow());
                                    if (Optional.of(ButtonType.OK).equals(dlg.showAndWait()))
                                    {
                                        String errorMsg = "创建聚集索引失败。";
                                        if (!this.createSIndex(dlg.getAggIndex()))
                                        {
                                            MessageBox.information(errorMsg);
                                        }
                                    }
                                }
                                vCls.close();
                            }
                        } else if (SIndexType.Cache.equals(indexType))
                        {
                            String errorMsg = "缓存索引创建失败。";
                            if (!this.createSIndex(new CacheIndex()))
                            {
                                MessageBox.information(errorMsg);
                            }
                        } else if (SIndexType.PGGIST.equals(indexType))
                        {
                            String errorMsg = "GIST索引创建失败。";
                            if (!this.createSIndex(new PGGistIndex()))
                            {
                                MessageBox.information(errorMsg);
                            }
                        }

                        if (!SIndexType.RTree.equals(indexType))
                        {
                            this.initSpatialIndex();//刷新
                            this.selectSpatialIndex(indexType);
                        }
                    }
                    //endregion
                } else
                {
                    //region 创建属性索引
                    if (XClsType.XSFCls.equals(this.clsType))
                    {
                        SFeatureCls sfCls = new SFeatureCls(this.db);
                        if (sfCls != null && sfCls.open(this.clsID, 0) > 0)
                        {
                            AddAttIndexDialog dlg = new AddAttIndexDialog(this.db, sfCls.getFields());
                            dlg.initOwner(this.getCurrentWindow());
                            if (Optional.of(ButtonType.OK).equals(dlg.showAndWait()))
                            {
                                String errorMsg = "";
                                if (sfCls.createAttIndex(dlg.getIndexName(), dlg.getIndexKeys(), dlg.getAttIndexType(), false, false, 0, errorMsg))
                                {
                                    this.initAttIndex();
                                } else if (errorMsg != "")
                                {
                                    MessageBox.information(errorMsg);
                                }
                            }
                            sfCls.close();
                        } else
                        {
                            System.out.println("打开失败。");
                        }
                    } else if (XClsType.XACls.equals(this.clsType))
                    {
                        AnnotationCls aCls = new AnnotationCls(this.db);
                        if (aCls != null && aCls.open(this.clsID, 0) > 0)
                        {
                            AddAttIndexDialog dlg = new AddAttIndexDialog(this.db, aCls.getFields());
                            dlg.initOwner(this.getCurrentWindow());
                            if (Optional.of(ButtonType.OK).equals(dlg.showAndWait()))
                            {
                                String errorMsg = "";
                                if (aCls.createAttIndex(dlg.getIndexName(), dlg.getIndexKeys(), dlg.getAttIndexType(), false, false, 0, errorMsg))
                                {
                                    this.initAttIndex();
                                } else if (errorMsg != "")
                                {
                                    MessageBox.information(errorMsg);
                                }
                            }
                            aCls.close();
                        }
                    } else if (XClsType.XOCls.equals(this.clsType))
                    {
                        ObjectCls oCls = new ObjectCls(this.db);
                        if (oCls != null && oCls.open(this.clsID, 0) > 0)
                        {
                            AddAttIndexDialog dlg = new AddAttIndexDialog(this.db, oCls.getFields());
                            dlg.initOwner(this.getCurrentWindow());
                            if (Optional.of(ButtonType.OK).equals(dlg.showAndWait()))
                            {
                                String errorMsg = "";
                                if (oCls.createAttIndex(dlg.getIndexName(), dlg.getIndexKeys(), dlg.getAttIndexType(), false, false, 0, errorMsg))
                                {
                                    this.initAttIndex();
                                } else if (errorMsg != "")
                                {
                                    MessageBox.information(errorMsg);
                                }
                            }
                            oCls.close();
                        }
                    }
                    //endregion
                }
            }
            event.consume();
        });

        //移除
        this.buttonRemove = (Button) dialogPane.lookupButton(buttonTypeRemove);
        this.buttonRemove.addEventFilter(ActionEvent.ACTION, event ->
        {
            if (isLocal || this.tabPane.getTabs().get(0).isSelected())
            {
                //region 删除空间索引
                SpatialIndex sIndex = this.tableViewSpatial.getSelectionModel().getSelectedItem();
                if (sIndex != null)
                {
                    IVectorCls vCls = null;
                    if (XClsType.XSFCls.equals(this.clsType))
                    {
                        vCls = new SFeatureCls(this.db);
                    } else if (XClsType.XACls.equals(this.clsType))
                    {
                        vCls = new AnnotationCls(this.db);
                    }

                    if (vCls != null && vCls.open(this.clsID, 0) > 0)
                    {
                        boolean dropped = this.dropSIndex(vCls, sIndex.getIndexType());
                        vCls.close();
                        if (!dropped)
                        {
                            MessageBox.information("删除空间索引失败。");
                        } else
                        {
                            if (isLocal)
                            {
                                sIndex.setExist(false);
                                sIndex.setValid(false);
                                this.buttonCreate.setDisable(false);
                                this.buttonRemove.setDisable(true);
                            } else
                            {
                                //底层逻辑太乱，有些连带，删完直接刷新吧
                                this.initSpatialIndex();
                                this.selectSpatialIndex(sIndex.getIndexType());
                            }
                        }
                    } else
                    {
                        System.out.println("打开失败。");
                    }
                }
                //endregion
            } else
            {
                //region 删除属性索引
                AttIndex aIndex = this.tableViewAtt.getSelectionModel().getSelectedItem();
                if (aIndex != null)
                {
                    String errorMsg = "";
                    if (XClsType.XSFCls.equals(this.clsType))
                    {
                        SFeatureCls sfCls = new SFeatureCls(this.db);
                        if (sfCls != null && sfCls.open(this.clsID, 0) > 0)
                        {
                            sfCls.dropAttIndex(aIndex.getName(), errorMsg);
                            sfCls.close();
                        } else
                        {
                            System.out.println("打开失败。");
                        }
                    } else if (XClsType.XACls.equals(this.clsType))
                    {
                        AnnotationCls aCls = new AnnotationCls(this.db);
                        if (aCls != null && aCls.open(this.clsID, 0) > 0)
                        {
                            aCls.dropAttIndex(aIndex.getName(), errorMsg);
                            aCls.close();
                        }
                    } else if (XClsType.XOCls.equals(this.clsType))
                    {
                        ObjectCls oCls = new ObjectCls(this.db);
                        if (oCls != null && oCls.open(this.clsID, 0) > 0)
                        {
                            oCls.dropAttIndex(aIndex.getName(), errorMsg);
                            oCls.close();
                        }
                    }

                    if (XString.isNullOrEmpty(errorMsg))
                    {
                        this.tableViewAtt.getItems().remove(aIndex);
                    } else if (errorMsg != "")
                    {
                        MessageBox.information(errorMsg);
                    }
                }
                //endregion
            }
            event.consume();
        });

        //刷新
        this.buttonRefresh = (Button) dialogPane.lookupButton(buttonTypeRefresh);
        this.buttonRefresh.addEventFilter(ActionEvent.ACTION, event ->
        {
            if (isLocal || this.tabPane.getTabs().get(0).isSelected())
            {
                this.initSpatialIndex();
            } else
            {
                boolean rtn = this.initAttIndex();
                if (!rtn)
                {
                    MessageBox.information("索引信息更新失败。");
                }

                this.buttonCreate.setDisable(!rtn);
                if (this.tableViewAtt.getItems().size() > 0)
                {
                    this.tableViewAtt.getSelectionModel().select(null);
                    this.tableViewAtt.getSelectionModel().select(0);
                }
            }
            event.consume();
        });
        //endregion

        if (!XClsType.XOCls.equals(clsType))
        {
            this.initSpatialIndex();
        } else
        {
            this.initAttIndex();
        }
    }

    //region 私有方法

    /**
     * 初始化/刷新空间索引
     */
    private void initSpatialIndex()
    {
        this.tableViewSpatial.getItems().clear();
        if (ConnectType.Local.equals(this.connectType) || ConnectType.LocalPlus.equals(this.connectType))
        {
            //region 判断类数据是否有R树索引
            boolean hasRTree = false;
            if (XClsType.XSFCls.equals(this.clsType))
            {
                SFeatureCls sfCls = new SFeatureCls(this.db);
                if (sfCls.open(this.clsID, 0) > 0)
                {
                    hasRTree = sfCls.hasSIndex(SIndexType.RTree);
                    sfCls.close();
                } else
                {
                    System.out.println("打开失败。");
                }
            } else if (XClsType.XACls.equals(this.clsType))
            {
                AnnotationCls aCls = new AnnotationCls(this.db);
                if (aCls.open(this.clsID, 0) > 0)
                {
                    hasRTree = aCls.hasSIndex(SIndexType.RTree);
                    aCls.close();
                }
            }
            //endregion

            this.tableViewSpatial.getItems().add(new SpatialIndex("R树索引", hasRTree, hasRTree, SIndexType.RTree));
            if (ConnectType.Local.equals(this.connectType))
            {
                this.tableViewSpatial.getItems().add(new SpatialIndex("矩形范围索引", true, true, SIndexType.UnionRect));
            }
        } else
        {
            //PG数据源的简单要素类和注记类只有GIST索引
            if (ConnectType.DBPG.equals(this.connectType))
            {
                boolean hasGist = false;
                if (XClsType.XSFCls.equals(this.clsType))
                {
                    SFeatureCls sfCls = new SFeatureCls(this.db);
                    if (sfCls.open(this.clsID, 0) > 0)
                    {
                        hasGist = sfCls.hasSIndex(SIndexType.PGGIST);
                        sfCls.close();
                    }
                } else if (XClsType.XACls.equals(this.clsType))
                {
                    AnnotationCls aCls = new AnnotationCls(this.db);
                    if (aCls.open(this.clsID, 0) > 0)
                    {
                        hasGist = aCls.hasSIndex(SIndexType.PGGIST);
                        aCls.close();
                    }
                }
                this.tableViewSpatial.getItems().add(new SpatialIndex("GIST索引", hasGist, hasGist, SIndexType.PGGIST));
            } else
            {
                boolean hasRect = true;
                boolean hasFrame = false;
                boolean hasAgg = false;
                boolean hasRectExt = false;
                boolean hasCache = false;
                if (XClsType.XSFCls.equals(this.clsType))
                {
                    SFeatureCls sfCls = new SFeatureCls(this.db);
                    if (sfCls.open(this.clsID, 0) > 0)
                    {
                        hasRect = sfCls.hasSIndex(SIndexType.UnionRect);
                        hasFrame = sfCls.hasSIndex(SIndexType.FrameNo);
                        hasAgg = sfCls.hasSIndex(SIndexType.Aggregation);
                        hasRectExt = sfCls.hasSIndex(SIndexType.UnionRectExt);
                        hasCache = sfCls.hasSIndex(SIndexType.Cache);
                        sfCls.close();
                    }
                } else if (XClsType.XACls.equals(this.clsType))
                {

                    AnnotationCls aCls = new AnnotationCls(this.db);
                    if (aCls.open(this.clsID, 0) > 0)
                    {
                        hasRect = aCls.hasSIndex(SIndexType.UnionRect);
                        hasFrame = aCls.hasSIndex(SIndexType.FrameNo);
                        hasAgg = aCls.hasSIndex(SIndexType.Aggregation);
                        hasRectExt = aCls.hasSIndex(SIndexType.UnionRectExt);
                        aCls.close();
                    }
                }
                this.tableViewSpatial.getItems().addAll(new SpatialIndex("矩形范围索引", hasRect, hasRect, SIndexType.UnionRect),
                        new SpatialIndex("图幅索引", hasFrame, hasFrame, SIndexType.Aggregation.FrameNo),
                        new SpatialIndex("聚集索引", hasAgg, hasAgg, SIndexType.Aggregation),
                        new SpatialIndex("要素外包索引", hasRectExt, hasRectExt, SIndexType.UnionRectExt));
                if (XClsType.XSFCls.equals(this.clsType))
                {
                    this.tableViewSpatial.getItems().add(new SpatialIndex("缓存索引", hasCache, hasCache, SIndexType.Cache));
                }
            }
        }

        if (this.tableViewSpatial.getItems().size() > 0)
        {
            this.tableViewSpatial.getSelectionModel().select(null);
            this.tableViewSpatial.getSelectionModel().select(0);
        }
    }

    /**
     * 初始化/刷新属性索引
     */
    private boolean initAttIndex()
    {
        this.tableViewAtt.getItems().clear();
        AttIndexInfo[] attIndexes = null;
        if (XClsType.XSFCls.equals(this.clsType))
        {
            SFeatureCls sfCls = new SFeatureCls(this.db);
            if (sfCls != null && sfCls.open(this.clsID, 0) > 0)
            {
                attIndexes = sfCls.getAttIndexList();
                sfCls.close();
            } else
            {
                System.out.println("打开失败。");
            }
        } else if (XClsType.XACls.equals(this.clsType))
        {
            AnnotationCls aCls = new AnnotationCls(this.db);
            if (aCls != null && aCls.open(this.clsID, 0) > 0)
            {
                attIndexes = aCls.getAttIndexList();
                aCls.close();
            }
        } else if (XClsType.XOCls.equals(this.clsType))
        {
            ObjectCls oCls = new ObjectCls(this.db);
            if (oCls != null && oCls.open(this.clsID, 0) > 0)
            {
                attIndexes = oCls.getAttIndexList();
                oCls.close();
            }
        }

        if (attIndexes != null)
        {
            for (AttIndexInfo attIndex : attIndexes)
            {
                if (attIndex != null)
                {
                    this.tableViewAtt.getItems().add(new AttIndex(attIndex.IndexName, attIndex.IndexKeys, attIndex.Desicribe));
                }
            }
        }
        return attIndexes != null;
    }

    /**
     * 删除空间索引
     *
     * @param vCls      矢量数据
     * @param indexType 索引类型
     * @return 删除成功返回true
     */
    private boolean dropSIndex(IVectorCls vCls, SIndexType indexType)
    {
        boolean rtn = false;
        if (vCls instanceof SFeatureCls)
        {
            rtn = ((SFeatureCls) vCls).dropSdIndex(indexType);
        } else if (vCls instanceof AnnotationCls)
        {
            rtn = ((AnnotationCls) vCls).dropSdIndex(indexType);
        }
        return rtn;
    }

    /**
     * 选择空间索引
     *
     * @param indexType 空间索引类型
     */
    private void selectSpatialIndex(SIndexType indexType)
    {
        for (SpatialIndex sIndex : this.tableViewSpatial.getItems())
        {
            if (indexType.equals(sIndex.getIndexType()))
            {
                this.tableViewSpatial.getSelectionModel().select(sIndex);
                break;
            }
        }
    }

    /**
     * 验证给的的属性结构能否创建聚集索引
     *
     * @param flds 属性结构
     * @return 能创建返回true，否则返回false
     */
    private boolean canCreateAggregation(Fields flds)
    {
        boolean canCreate = false;
        for (short i = 0; i < flds.getFieldCount(); i++)
        {
            Field fld = flds.getField(i);
            if (fld != null)
            {
                Field.FieldType fType = fld.getFieldType();
                if (Field.FieldType.fldByte.equals(fType) || Field.FieldType.fldBool.equals(fType) || Field.FieldType.fldShort.equals(fType) || Field.FieldType.fldLong.equals(fType)
                        || Field.FieldType.fldInt64.equals(fType) || Field.FieldType.fldFloat.equals(fType) || Field.FieldType.fldDouble.equals(fType)
                        || Field.FieldType.fldDate.equals(fType) || Field.FieldType.fldTime.equals(fType) || Field.FieldType.fldTimeStamp.equals(fType))
                {
                    canCreate = true;
                }
            }
            if (canCreate)
                break;
        }
        return canCreate;
    }

    /**
     * 创建空间索引
     *
     * @param sIndex 索引信息对象
     * @return 成功-true
     */
    private boolean createSIndex(ISIndex sIndex)
    {
        boolean rtn = false;
        if (XClsType.XSFCls.equals(this.clsType))
        {
            SFeatureCls sfCls = new SFeatureCls(this.db);
            if (sfCls.open(this.clsID, 0) > 0)
            {
                rtn = sfCls.createSIndex(sIndex);
                sfCls.close();
            } else
            {
                System.out.println("打开失败。");
            }
        } else if (XClsType.XACls.equals(this.clsType))
        {
            AnnotationCls aCls = new AnnotationCls(this.db);
            if (aCls.open(this.clsID, 0) > 0)
            {
                rtn = aCls.createSIndex(sIndex);
                aCls.close();
            }
        }
        //封装问题：.Net中createSIndex返回int值，可根据返回值得带说些错误信息和提示
        return rtn;
    }
    //endregion

    //region 获取窗口对象
    private Window window;

    /**
     * 获取当前窗口的window对象
     *
     * @return 当前窗口的window对象
     */
    private Window getCurrentWindow()
    {
        if (this.window == null)
        {
            this.window = this.getDialogPane().getScene().getWindow();
        }
        return this.window;
    }
    //endregion

    public class SpatialIndex
    {
        private String name;
        private BooleanProperty exist = new SimpleBooleanProperty();
        private BooleanProperty valid = new SimpleBooleanProperty();
        private SIndexType indexType;

        public SpatialIndex(String name, boolean exist, boolean valid, SIndexType indexType)
        {
            this.name = name;
            this.exist.set(exist);
            this.valid.set(valid);
            this.indexType = indexType;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String indexName)
        {
            this.name = indexName;
        }

        public boolean isExist()
        {
            return exist.get();
        }

        public BooleanProperty existProperty()
        {
            return exist;
        }

        public void setExist(boolean exist)
        {
            this.exist.set(exist);
        }

        public boolean isValid()
        {
            return valid.get();
        }

        public BooleanProperty validProperty()
        {
            return valid;
        }

        public void setValid(boolean valid)
        {
            this.valid.set(valid);
        }

        public SIndexType getIndexType()
        {
            return this.indexType;
        }

        public void setIndexType(SIndexType indexType)
        {
            this.indexType = indexType;
        }
    }

    public class AttIndex
    {
        private String name;
        private String fields;
        private String description;

        public AttIndex(String name, String flds, String des)
        {
            this.name = name;
            this.fields = flds;
            this.description = des;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String indexName)
        {
            this.name = indexName;
        }

        public String getDescription()
        {
            return description;
        }

        public void setDescription(String description)
        {
            this.description = description;
        }

        public String getFields()
        {
            return fields;
        }

        public void setFields(String fields)
        {
            this.fields = fields;
        }
    }

    /**
     * 用于允许编辑和允许空
     */
    class BooleanTableCell extends TableCell<SpatialIndex, Boolean>
    {
        @Override
        protected void updateItem(Boolean item, boolean empty)
        {
            super.updateItem(item, empty);
            ImageView graphic = null;
            if (!empty)
            {
                graphic = new ImageView(new Image(getClass().getResourceAsStream(item ? "/yes_16.png" : "/no_16.png")));
            }
            this.setText(null);
            this.setGraphic(graphic);
            this.setAlignment(Pos.CENTER);
        }
    }
}
