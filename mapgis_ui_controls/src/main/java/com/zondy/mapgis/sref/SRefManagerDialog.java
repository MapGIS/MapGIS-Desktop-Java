package com.zondy.mapgis.sref;

import com.zondy.mapgis.base.LanguageConvert;
import com.zondy.mapgis.base.MessageBox;
import com.zondy.mapgis.base.XFunctions;
import com.zondy.mapgis.base.XString;
import com.zondy.mapgis.controls.common.ZDToolBar;
import com.zondy.mapgis.filedialog.GDBSaveFileDialog;
import com.zondy.mapgis.geodatabase.DataBase;
import com.zondy.mapgis.map.SRSItem;
import com.zondy.mapgis.srs.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Optional;

/**
 * @author CR
 * @file SRefManagerDialog.java
 * @brief 坐标系管理设置界面
 * @create 2019-02-19.
 */
public class SRefManagerDialog extends Dialog
{
    //region 变量
    private final Button buttonImport = new Button("导入", new ImageView(new Image(getClass().getResourceAsStream("import_20.png"))));
    private final Button buttonExport = new Button("导出", new ImageView(new Image(getClass().getResourceAsStream("export_20.png"))));
    private final ToggleButton toggleButtonFavorite = new ToggleButton("收藏", new ImageView(new Image(getClass().getResourceAsStream("favorite_20.png"))));
    private final Button buttonRemove = new Button("移除", new ImageView(new Image(getClass().getResourceAsStream("remove_20.png"))));
    private final MenuItem menuItemGeo = new MenuItem("新建地理坐标系");
    private final MenuItem menuItemPrj = new MenuItem("新建投影坐标系");
    private final Button buttonAddGroup = new Button("新建组", new ImageView(new Image(getClass().getResourceAsStream("addgroup_20.png"))));
    private final TextField textFieldSearch = new TextField();
    private final TreeView<TreeItemObject> treeView = new TreeView<>();
    private final TableColumn<SRefItem, String> tcPrjType = new TableColumn<>("投影类型");
    private final TableView<SRefItem> tableView = new TableView<>();
    private final TextArea textArea = new TextArea();
    private static Document srsDocument = null;
    private static Element elementCustom;//自定义节点
    private Element elementFavorite;//收藏夹节点
    private SRefItem selectedSRef;
    private boolean isSelect;//是否要选择一个参照系
    private SimpleBooleanProperty isSearchingProperty = new SimpleBooleanProperty(false);//是否正在显示搜索结果
    private TreeItem<TreeItemObject> treeItemRoot;//根节点
    private Button buttonSearch;

    //endregion

    /**
     * 设置参照系（必须选择一个参照系才能确定）
     *
     * @return
     */
    public SRefManagerDialog()
    {
        this(true);
    }

    /**
     * 设置参照系
     *
     * @param isSelect 是否要设置参照系，设置参照系时必须要选中一个
     */
    public SRefManagerDialog(boolean isSelect)
    {
        //if (XFunctions.isSystemWindows())
        {
            this.setResizable(true);
        }
        this.isSelect = isSelect;
        this.setTitle("坐标系设置管理");

        srsDocument = getSrsDocument();

        //region 搜索
        this.textFieldSearch.setPromptText("输入名称或EPSG代码进行搜索");
        this.textFieldSearch.setPrefWidth(260);
        this.textFieldSearch.setBackground(new Background(new BackgroundFill(Paint.valueOf("white"), null, null)));
        this.textFieldSearch.setOnKeyPressed(event ->
        {
            if (event.getCode() == KeyCode.ENTER)
            {
                buttonSearch.fire();
            }
        });

        this.buttonSearch = new Button("搜索", new ImageView(new Image(getClass().getResourceAsStream("search_16.png"))));
        this.buttonSearch.setTooltip(new Tooltip("搜索"));
        this.buttonSearch.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.buttonSearch.setOnAction(event1 ->
        {
            String findText = this.textFieldSearch.getText();
            if (!XString.isNullOrEmpty(findText))
            {
                this.isSearchingProperty.set(true);
                this.treeItemRoot.getChildren().clear();
                this.addFindedGroupTree(srsDocument.getRootElement(), this.treeItemRoot, findText);
                TreeItem<TreeItemObject> tiFocus = this.getFocusFindGroup(this.treeItemRoot);
                if (tiFocus != null)
                {
                    this.treeView.getSelectionModel().select(tiFocus);
                    this.tableView.setPlaceholder(new Label("没有找到坐标系"));
                }
            }
        });

        Button buttonCloseSearch = new Button("关闭搜索结果", new ImageView(new Image(getClass().getResourceAsStream("closesearch_16.png"))));
        buttonCloseSearch.setTooltip(new Tooltip("关闭搜索结果"));
        buttonCloseSearch.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        buttonCloseSearch.setOnAction(event2 ->
        {
            this.isSearchingProperty.set(false);
            this.treeItemRoot.getChildren().clear();
            this.initAllGroupTree();
            this.tableView.setPlaceholder(new Label("当前分组中没有坐标系"));
        });

        HBox hBoxSearch = new HBox(this.textFieldSearch, this.buttonSearch, buttonCloseSearch);
        hBoxSearch.setPrefWidth(280);
        hBoxSearch.setBorder(new Border(new BorderStroke(Paint.valueOf("grey"), BorderStrokeStyle.SOLID, null, BorderStroke.THIN)));
        hBoxSearch.setBackground(new Background(new BackgroundFill(Paint.valueOf("white"), null, null)));
        HBox.setHgrow(this.textFieldSearch, Priority.ALWAYS);
        //endregion

        //region 工具栏按钮
        //导入
        this.buttonImport.setOnAction(event ->
        {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml文件(*.xml)", "*.xml"));
            File file = fileChooser.showOpenDialog(this.getCurrentWindow());
            if (file != null)
            {
                try
                {
                    SAXReader reader = new SAXReader();
                    Document xd = reader.read(file);
                    SRSItem srsItem = new SRSItem();
                    if (srsItem.fromXML(xd.getRootElement().asXML()))
                    {
                        TreeItem<TreeItemObject> tiParent = this.getCurrentCustomTreeItem();
                        Element eleParent = tiParent != null ? tiParent.getValue().getElement() : this.getElementCustom();
                        if (eleParent != null)
                        {
                            Element ele = (Element) xd.getRootElement().clone();
                            eleParent.add(ele);
                            if (tiParent != null)
                            {
                                tiParent.getValue().getSrsList().add(new SRefItem(ele));
                            }
                        }
                    } else
                    {
                        MessageBox.information("参照系xml格式错误。");
                    }
                } catch (Exception ex)
                {
                }
            }
        });
        //导出
        this.buttonExport.setOnAction(event ->
        {
            SRefItem sRefItem = this.tableView.getSelectionModel().getSelectedItem();
            if (sRefItem != null)
            {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml文件(*.xml)", "*.xml"));
                fileChooser.setInitialFileName(sRefItem.getSRSName());
                File file = fileChooser.showSaveDialog(this.getCurrentWindow());
                if (file != null)
                {
                    try
                    {
                        String path = file.getPath();
                        if (!path.toLowerCase().endsWith(".xml"))
                        {
                            path += ".xml";
                        }
                        Document xd = DocumentHelper.parseText(sRefItem.getElement().asXML());
                        XMLWriter xmlWriter = new XMLWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
                        xmlWriter.write(xd);
                        xmlWriter.close();
                    } catch (Exception ex)
                    {
                    }
                }
            }
        });
        //收藏
        this.toggleButtonFavorite.setOnAction(event ->
        {
            SRefItem sRefItem = this.tableView.getSelectionModel().getSelectedItem();
            TreeItem<TreeItemObject> tiFavorite = this.getFavoriteTreeItem();
            Element eleFavorite = tiFavorite != null ? tiFavorite.getValue().getElement() : this.getElementFavorite();
            if (eleFavorite != null)
            {
                if (this.toggleButtonFavorite.isSelected())
                {
                    Element newEle = (Element) sRefItem.getElement().clone();
                    eleFavorite.add(newEle);
                    if (tiFavorite != null && tiFavorite.getValue().getSrsList() != null)
                    {
                        tiFavorite.getValue().getSrsList().add(new SRefItem(newEle));
                    }
                } else
                {
                    List<Node> nodes = eleFavorite.selectNodes(String.format("参照系[sSRSName[text()='%s']]", sRefItem.getSRSName()));
                    if (nodes.size() > 0)
                    {
                        eleFavorite.remove((Element) nodes.get(0));
                        if (tiFavorite != null)
                        {
                            if (this.treeView.getSelectionModel().getSelectedItem().equals(tiFavorite))
                            {
                                tiFavorite.getValue().getSrsList().remove(sRefItem);
                            } else
                            {
                                tiFavorite.getValue().setSrsList(null);
                            }
                        }
                    }
                }
            }
        });
        //删除
        this.buttonRemove.setOnAction(event1 ->
        {
            SRefItem sRefItem = this.tableView.getSelectionModel().getSelectedItem();
            if (sRefItem != null)
            {
                Element ele = sRefItem.getElement();
                ele.getParent().remove(ele);
                this.tableView.getItems().remove(sRefItem);
                if (!sRefItem.isSRef())
                {
                    TreeItem<TreeItemObject> treeItemFoc = this.treeView.getSelectionModel().getSelectedItem();
                    for (TreeItem<TreeItemObject> ti : treeItemFoc.getChildren())
                    {
                        if (ti.getValue().getElement().equals(ele))
                        {
                            treeItemFoc.getChildren().remove(ti);
                            break;
                        }
                    }
                }
            }
        });
        //新建地理坐标系
        this.menuItemGeo.setOnAction(event1 ->
        {
            //TODO: 通过对话框得到SRefData对象，新建需判断名称不能重复
            GeoSRefDialog dlg = new GeoSRefDialog();
            dlg.initOwner(this.getCurrentWindow());
            if (Optional.of(ButtonType.OK).equals(dlg.showAndWait()))
            {
                SRefData sRef = dlg.getSpatialReference();
                TreeItem<TreeItemObject> tiParent = this.getCurrentCustomTreeItem();
                Element eleParent = tiParent != null ? tiParent.getValue().getElement() : this.getElementCustom();
                Element ele = addSref(sRef, eleParent);
                if (tiParent != null)
                {
                    tiParent.getValue().getSrsList().add(new SRefItem(ele));
                }
            }
        });
        //新建投影坐标系
        this.menuItemPrj.setOnAction(event1 ->
        {
            //TODO: 通过对话框得到SRefData对象，新建需判断名称不能重复
            PrjSRefDialog dlg = new PrjSRefDialog();
            dlg.initOwner(this.getCurrentWindow());
            if (Optional.of(ButtonType.OK).equals(dlg.showAndWait()))
            {
                SRefData sRef = dlg.getSpatialReference();
                TreeItem<TreeItemObject> tiParent = this.getCurrentCustomTreeItem();
                Element eleParent = tiParent != null ? tiParent.getValue().getElement() : getElementCustom();
                Element ele = addSref(sRef, eleParent);
                if (tiParent != null)
                {
                    tiParent.getValue().getSrsList().add(new SRefItem(ele));
                }
            }
        });
        MenuButton menuButton = new MenuButton("新建坐标系", new ImageView(new Image(getClass().getResourceAsStream("add_20.png"))), menuItemGeo, menuItemPrj);

        //新建组
        this.buttonAddGroup.setTooltip(new Tooltip("在自定义下面添加组，添加完保存。"));
        this.buttonAddGroup.setOnAction(event ->
        {
            NewGroupDialog dlg = new NewGroupDialog();
            dlg.initOwner(this.getCurrentWindow());
            if (Optional.of(ButtonType.OK).equals(dlg.showAndWait()))
            {
                TreeItem<TreeItemObject> tiParent = this.getCurrentCustomTreeItem();
                Element eleParent = tiParent != null ? tiParent.getValue().getElement() : getElementCustom();
                if (eleParent != null)
                {
                    Element newEle = eleParent.addElement("group");
                    newEle.addAttribute("name", dlg.getGroupName());
                    newEle.addAttribute("type", "0");
                    if (tiParent != null)
                    {
                        TreeItem<TreeItemObject> ti = new TreeItem<>(new TreeItemObject(newEle));
                        ti.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(ti.getValue().getImageName()))));
                        tiParent.getChildren().add(ti);
                    }
                }
            }
        });

        HBox hBoxLoc = new HBox();//占位控件，用于使得后面的搜索靠右停
        HBox.setHgrow(hBoxLoc, Priority.ALWAYS);

        ZDToolBar toolBar = new ZDToolBar(true, this.buttonImport, this.buttonExport, new Separator(), menuButton, this.buttonAddGroup, new Separator(), this.toggleButtonFavorite, this.buttonRemove, hBoxLoc, hBoxSearch);
        //endregion

        //region 坐标系列表
        TableColumn<SRefItem, String> tcName = new TableColumn<>("名称");
        TableColumn<SRefItem, String> tcType = new TableColumn<>("类型");
        TableColumn<SRefItem, String> tcEPSG = new TableColumn<>("EPSG代码");
        TableColumn<SRefItem, String> tcEllipse = new TableColumn<>("参考椭球");
        this.tableView.getColumns().addAll(tcName, tcType, tcEPSG, this.tcPrjType, tcEllipse);
        this.tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tcName.setMinWidth(200);
        tcType.setMinWidth(100);
        this.tcPrjType.setMinWidth(100);
        tcName.setCellValueFactory(new PropertyValueFactory<>("sRSName"));
        tcType.setCellValueFactory(new PropertyValueFactory<>("type"));
        tcEPSG.setCellValueFactory(new PropertyValueFactory<>("epsgCode"));
        tcEllipse.setCellValueFactory(new PropertyValueFactory<>("spheroid"));
        this.tcPrjType.setCellValueFactory(new PropertyValueFactory<>("projName"));
        tcName.setCellFactory(param -> new TableCell<SRefItem, String>()
        {
            @Override
            protected void updateItem(String item, boolean empty)
            {
                super.updateItem(item, empty);
                String text = null;
                ImageView graphic = null;
                if (!empty && !XString.isNullOrEmpty(item))
                {
                    text = item;
                    if (getTableRow().getItem() instanceof SRefItem)
                    {
                        String imageName = ((SRefItem) getTableRow().getItem()).getImageName();
                        if (!XString.isNullOrEmpty(imageName))
                        {
                            graphic = new ImageView(new Image(getClass().getResourceAsStream(imageName)));
                        }
                    }
                }
                this.setText(text);
                this.setGraphic(graphic);
            }
        });

        this.tableView.getSelectionModel().selectedItemProperty().addListener(this::tableViewFocusedItemChanged);
        this.tableView.setOnMouseClicked(event ->
        {
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY)
            {
                SRefItem sRefItem = this.tableView.getSelectionModel().getSelectedItem();
                if (sRefItem != null)
                {
                    if (sRefItem.isSRef)
                    {
                        if (this.isSelect)
                        {
                            ((Button) getDialogPane().lookupButton(ButtonType.OK)).fire();
                        }
                    } else//展开组
                    {
                        String groupName = sRefItem.getSRSName();
                        TreeItem<TreeItemObject> focTreeItem = this.treeView.getSelectionModel().getSelectedItem();
                        for (TreeItem<TreeItemObject> treeItem : focTreeItem.getChildren())
                        {
                            if (treeItem.getValue().getText().equals(groupName))
                            {
                                this.treeView.getSelectionModel().select(treeItem);
                                break;
                            }
                        }
                    }
                }
            }
        });
        //endregion

        //region 坐标系管理目录树
        this.treeView.setPrefWidth(230);
        this.treeView.setEditable(false);
        this.treeItemRoot = new TreeItem<>(new TreeItemObject(null));
        this.treeView.setRoot(this.treeItemRoot);
        this.treeView.setShowRoot(false);
        this.treeView.setCellFactory(p -> new TextFieldTreeCell<TreeItemObject>()
        {
            @Override
            public void updateItem(TreeItemObject item, boolean empty)
            {
                super.updateItem(item, empty);
                if (item != null)
                {
                    setText(item.getText());
                }
            }
        });
        this.treeView.getSelectionModel().selectedItemProperty().addListener(this::focusTreeItemChanged);
        this.initAllGroupTree();
        //endregion

        //region 坐标系信息
        this.textArea.setEditable(false);
        //endregion

        //region 界面布局
        VBox vBoxRight = new VBox(6, this.tableView, this.textArea);
        HBox hBox = new HBox(6, this.treeView, vBoxRight);
        VBox.setVgrow(this.tableView, Priority.ALWAYS);
        HBox.setHgrow(vBoxRight, Priority.ALWAYS);
        VBox vBoxRoot = new VBox(6, toolBar, hBox);

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setPrefSize(1000, 700);
        dialogPane.setContent(vBoxRoot);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CLOSE);
        this.setResizable(true);

        Button buttonOK = (Button) dialogPane.lookupButton(ButtonType.OK);
        buttonOK.setOnAction(this::buttonOKClick);
        //endregion

        this.setOnCloseRequest(event ->
        {
            this.saveXml();
        });
    }

    /**
     * 获取所选参照系
     *
     * @return 选择的参照系
     */
    public SRefData getSelectedSRef()
    {
        SRefData sRefData = null;
        if (this.selectedSRef != null)
        {
            SRSItem srsItem = new SRSItem();
            if (srsItem.fromXML(this.selectedSRef.getElement().asXML()))
            {
                sRefData = srsItem.getSrefInfo();
            }
        }
        return sRefData;
    }

    //region 事件
    //切选树节点时，读取坐标系列表
    private void focusTreeItemChanged(ObservableValue<? extends TreeItem<TreeItemObject>> observable, TreeItem<TreeItemObject> oldValue, TreeItem<TreeItemObject> newValue)
    {
        if (newValue == null)
        {
            this.tableView.setItems(null);
        } else
        {
            ObservableList<SRefItem> list = newValue.getValue().getSrsList();
            if (list == null && !this.isSearchingProperty.getValue())
            {
                list = FXCollections.observableArrayList();
                Element tiElement = newValue.getValue().getElement();
                List<Element> groups = tiElement.elements("group");
                for (Element ele : groups)
                {
                    list.add(new SRefItem(ele));
                }
                List<Element> elements = tiElement.elements("参照系");
                for (Element ele : elements)
                {
                    list.add(new SRefItem(ele));
                }
                newValue.getValue().setSrsList(list);
            }
            this.tableView.setItems(list);
            if (list != null && list.size() > 0)
            {
                this.tableView.getSelectionModel().select(0);
            }

            this.tcPrjType.setVisible(!newValue.getValue().getText().equals("地理坐标系"));
        }
    }

    //切选参照系时，更新参照系信息
    public void tableViewFocusedItemChanged(ObservableValue<? extends SRefItem> observable, SRefItem oldValue, SRefItem newValue)
    {
        String sRefInfo = "";
        boolean isSRS = newValue != null && newValue.isSRef();
        if (isSRS)
        {
            Element ele = newValue.getElement();
            sRefInfo = String.format("坐标系名称：%s%n", newValue.getSRSName());

            //region 投影坐标系信息
            if (ele.element("iType").getTextTrim().equals("3"))
            {
                short prjType = Short.valueOf(ele.element("iProjTypeId").getTextTrim());
                sRefInfo += String.format("%n投影类型：%s", LanguageConvert.sRefProjTypeConvertEx(SRefPrjType.valueOf(prjType)));
                switch (prjType)
                {
                    case 23:
                        break;
                    default:
                        sRefInfo += String.format("%n      投影东偏：%s", Double.valueOf(ele.element("dFalseE").getTextTrim()));
                        sRefInfo += String.format("%n      投影北偏：%s", Double.valueOf(ele.element("dFalseN").getTextTrim()));
                        break;
                }

                switch (prjType)
                {
                    case 23:
                    case 15:
                    case 17:
                    case 18:
                    case 21:
                    case 22:
                    {
                        sRefInfo += String.format("%n      中央经线：%s", Double.valueOf(ele.element("dLon").getTextTrim()));
                        break;
                    }
                    case 1:
                    case 5:
                    case 6:
                    case 24:
                    case 25:
                    case 26:
                    {
                        sRefInfo += String.format("%n      中央经线：%s", Double.valueOf(ele.element("dLon").getTextTrim()));
                        sRefInfo += String.format("%n      投影原点纬度：%s", Double.valueOf(ele.element("dLat").getTextTrim()));
                        break;
                    }
                    case 4:
                    case 16:
                    {
                        sRefInfo += String.format("%n      中央经线：%s", Double.valueOf(ele.element("dLon").getTextTrim()));
                        sRefInfo += String.format("%n      无变形纬度：%s", Double.valueOf(ele.element("dLat").getTextTrim()));
                        break;
                    }
                    case 2:
                    case 7:
                    {
                        sRefInfo += String.format("%n      中央经线：%s", Double.valueOf(ele.element("dLon").getTextTrim()));
                        sRefInfo += String.format("%n      投影原点纬度：%s", Double.valueOf(ele.element("dLat").getTextTrim()));
                        sRefInfo += String.format("%n      第一标准纬度：%s", Double.valueOf(ele.element("dLat1").getTextTrim()));
                        sRefInfo += String.format("%n      第二标准纬度：%s", Double.valueOf(ele.element("dLat2").getTextTrim()));
                        break;
                    }
                    case 3:
                    {
                        sRefInfo += String.format("%n      中央经线：%s", Double.valueOf(ele.element("dLon").getTextTrim()));
                        sRefInfo += String.format("%n      投影原点纬度：%s", Double.valueOf(ele.element("dLat").getTextTrim()));
                        sRefInfo += String.format("%n      第一标准纬度：%s", Double.valueOf(ele.element("dLat1").getTextTrim()));
                        sRefInfo += String.format("%n      第二标准纬度：%s", Double.valueOf(ele.element("dLat2").getTextTrim()));
                        sRefInfo += String.format("%n      比例因子：%s", Double.valueOf(ele.element("precision3").getTextTrim()));
                        break;
                    }
                    case 8:
                    {
                        sRefInfo += String.format("%n      中央经线：%s", Double.valueOf(ele.element("dLon").getTextTrim()));
                        sRefInfo += String.format("%n      投影原点纬度：%s", Double.valueOf(ele.element("dLat").getTextTrim()));
                        sRefInfo += String.format("%n      比例因子：%s", Double.valueOf(ele.element("precision3").getTextTrim()));
                        break;
                    }
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    {
                        sRefInfo += String.format("%n      中央经线：%s", Double.valueOf(ele.element("dLon").getTextTrim()));
                        sRefInfo += String.format("%n      投影中心点纬度：%s", Double.valueOf(ele.element("dLat").getTextTrim()));
                        break;
                    }
                    case 14:
                    {
                        sRefInfo += String.format("%n      中央经线：%s", Double.valueOf(ele.element("dLon").getTextTrim()));
                        sRefInfo += String.format("%n      投影中心点纬度：%s", Double.valueOf(ele.element("dLat").getTextTrim()));
                        sRefInfo += String.format("%n      透视点到球面的距离：%s", Double.valueOf(ele.element("dLat1").getTextTrim()));
                        break;
                    }
                    case 19:
                    {
                        sRefInfo += String.format("%n      投影中心点的比例因子：%s", Double.valueOf(ele.element("dLon").getTextTrim()));
                        sRefInfo += String.format("%n      投影原点纬度：%s", Double.valueOf(ele.element("dLat").getTextTrim()));
                        sRefInfo += String.format("%n      定义中心投影线的第一经度：%s", Double.valueOf(ele.element("dLon1").getTextTrim()));
                        sRefInfo += String.format("%n      定义中心投影线的第一纬度：%s", Double.valueOf(ele.element("dLat1").getTextTrim()));
                        sRefInfo += String.format("%n      定义中心投影线的第二经度：%s", Double.valueOf(ele.element("dLon2").getTextTrim()));
                        sRefInfo += String.format("%n      定义中心投影线的第二纬度：%s", Double.valueOf(ele.element("dLat2").getTextTrim()));
                        break;
                    }
                    case 20:
                    {
                        sRefInfo += String.format("%n      无变形纬度：%s", Double.valueOf(ele.element("dLat").getTextTrim()));
                        sRefInfo += String.format("%n      地球Y轴对应的经度：%s", Double.valueOf(ele.element("dLon").getTextTrim()));
                        break;
                    }
                    default:
                        break;
                }

                sRefInfo += String.format("%n      水平比例尺：%s", Double.valueOf(ele.element("rate").getTextTrim()));
                sRefInfo += String.format("%n      长度单位：%s", LanguageConvert.sRefLenUnitConvert(SRefLenUnit.valueOf(Integer.valueOf(ele.element("unit").getTextTrim()))));
                sRefInfo += String.format("%n      图形平移：dx = %s, dy = %s", Double.valueOf(ele.element("dx").getTextTrim()), Double.valueOf(ele.element("dy").getTextTrim()));

                sRefInfo += String.format("%n%n地理坐标系：%s", ele.element("sGCSName").getTextTrim());
            }
            //endregion

            //region 地理坐标系信息
            sRefInfo += String.format("%n标准椭球：%s", newValue.getSpheroid());//ElpTransformation.getElpParam(sRef.getSpheroid()).getName();//未完成
            sRefInfo += String.format("%n      长轴：%s", Double.valueOf(ele.element("a").getTextTrim()));
            sRefInfo += String.format("%n      扁率：%s", Double.valueOf(ele.element("af").getTextTrim()));
            sRefInfo += String.format("%n角度单位：%s", LanguageConvert.sRefLenUnitConvert(SRefLenUnit.valueOf(Integer.valueOf(ele.element("dAngUnit").getTextTrim()))));
            sRefInfo += String.format("%n本初子午线：%s", ele.element("sPMName").getTextTrim());
            if (ele.element("sPMName").getTextTrim().equals("<自定义...>"))
            {
                double dms = Double.valueOf(ele.element("dPMOffset").getTextTrim());
                boolean negative = dms < 0;
                dms = Math.abs(dms);
                int d = (int) Math.floor(dms / 10000);
                if (negative)
                {
                    d *= -1;
                }
                dms = dms % 10000;
                sRefInfo += String.format(" (经度：%d度%d分%f秒)", d, (int) Math.floor(dms / 100), dms % 100.0);
            }
            //endregion
        }
        this.textArea.setText(sRefInfo);

        //region 收藏
        this.toggleButtonFavorite.setDisable(!isSRS);
        if (isSRS)
        {
            TreeItem<TreeItemObject> tiFoc = this.treeView.getSelectionModel().getSelectedItem();
            boolean isFavorite = tiFoc != null && "100".equals(tiFoc.getValue().getElement().attributeValue("type"));
            if (!isFavorite)
            {
                String srsName = newValue.getSRSName();
                Element elementFavorite = this.getElementFavorite();
                List<Node> nodes = elementFavorite.selectNodes(String.format("参照系[sSRSName[text()='%s']]", srsName));
                isFavorite = nodes.size() > 0;
            }
            this.toggleButtonFavorite.setSelected(isFavorite);
        }
        //endregion

        //region 判断能否导出/删除
        boolean canRemove = false;
        if (isSRS)
        {
            if ("0".equals(newValue.getElement().getParent().attributeValue("type")) && !"DEFAULT Sref".equals(newValue.getSRSName()))
            {
                canRemove = true;
            }
        } else if (newValue != null && "0".equals(newValue.getElement().attributeValue("type")))
        {
            canRemove = true;
        }
        this.buttonRemove.setDisable(!canRemove);
        this.buttonExport.setDisable(!isSRS);
        //endregion
    }

    //确定
    private void buttonOKClick(ActionEvent event)
    {
        this.selectedSRef = this.tableView.getSelectionModel().getSelectedItem();
        if (this.isSelect && (this.selectedSRef == null || !this.selectedSRef.isSRef()))
        {
            MessageBox.information("请选择一个坐标系.");
            event.consume();
        }
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

    //region 内部函数

    /**
     * 获取搜索结果的焦点树节点（有可能是子级节点）
     *
     * @param tiParent 父树节点
     * @return 焦点树节点，用于选中
     */
    private TreeItem<TreeItemObject> getFocusFindGroup(TreeItem<TreeItemObject> tiParent)
    {
        TreeItem<TreeItemObject> tiFocus = null;
        for (TreeItem<TreeItemObject> ti : tiParent.getChildren())
        {
            ObservableList<SRefItem> list = ti.getValue().getSrsList();
            for (SRefItem sr : list)
            {
                if (sr.isSRef())
                {
                    tiFocus = ti;
                    break;
                }
            }
            if (tiFocus == null)
            {
                tiFocus = this.getFocusFindGroup(ti);
            }
            if (tiFocus != null)
            {
                break;
            }
        }
        return tiFocus;
    }

    /**
     * 初始化坐标系管理目录树
     */
    private void initAllGroupTree()
    {
        this.addGroupTree(srsDocument.getRootElement(), this.treeItemRoot);
        for (TreeItem<TreeItemObject> treeItem : this.treeItemRoot.getChildren())
        {
            treeItem.setExpanded(true);
        }
        if (treeItemRoot.getChildren().size() > 0)
        {
            this.treeView.getSelectionModel().select(treeItemRoot.getChildren().get(0));
        }
    }

    /**
     * 读取组并添加节点
     *
     * @param elementParent 父xml节点
     * @param tiParent      父树节点
     */
    private void addGroupTree(Element elementParent, TreeItem<TreeItemObject> tiParent)
    {
        if (elementParent != null && tiParent != null)
        {
            List<Node> nodes = elementParent.selectNodes("group");
            for (Node node : nodes)
            {
                Element ele = (Element) node;
                String text = ele.attributeValue("name");
                TreeItem<TreeItemObject> ti = new TreeItem<>(new TreeItemObject(ele));
                ti.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(ti.getValue().getImageName()))));
                tiParent.getChildren().add(ti);
                this.addGroupTree(ele, ti);
            }
        }
    }

    /**
     * 添加搜索结果目录树节点
     *
     * @param elementParent 父级xml节点
     * @param tiParent      父级树节点
     * @param findText      查找文本
     */
    private void addFindedGroupTree(Element elementParent, TreeItem<TreeItemObject> tiParent, String findText)
    {
        if (elementParent != null && tiParent != null && !XString.isNullOrEmpty(findText))
        {
            List<Node> nodes = elementParent.selectNodes("group");
            for (Node node : nodes)
            {
                List<Node> groupNodes = node.selectNodes("group");
                List<Node> findNodes = node.selectNodes(String.format("参照系[sSRSName[contains(text(),'%s')]|EPSGID[contains(text(),'%s')]]", findText, findText));
                ObservableList<SRefItem> srsList = null;
                if (findNodes.size() > 0)
                {
                    srsList = FXCollections.observableArrayList();
                    for (Node findNode : findNodes)
                    {
                        srsList.add(new SRefItem((Element) findNode));
                    }
                }

                if (groupNodes.size() > 0 || findNodes.size() > 0)
                {
                    TreeItem<TreeItemObject> ti = new TreeItem<>(new TreeItemObject((Element) node, srsList));
                    ti.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(ti.getValue().getImageName()))));
                    tiParent.getChildren().add(ti);

                    if (groupNodes.size() > 0)
                    {
                        this.addFindedGroupTree((Element) node, ti, findText);
                    }
                    if (ti.getValue().getSrsList() == null && ti.getChildren().size() == 0)
                    {
                        ti.getParent().getChildren().remove(ti);
                    }

                    if (ti.getChildren().size() > 0)
                    {
                        ObservableList<SRefItem> list = ti.getValue().getSrsList();
                        if (list == null)
                        {
                            list = FXCollections.observableArrayList();
                        }
                        for (TreeItem<TreeItemObject> treeItem : ti.getChildren())
                        {
                            list.add(new SRefItem(treeItem.getValue().getElement()));
                        }
                        ti.getValue().setSrsList(list);
                    }
                    tiParent.setExpanded(true);
                }
            }
        }
    }

    /**
     * @return 获取自定义xml节点
     */
    private static Element getElementCustom()
    {
        if (elementCustom == null)
        {
            List<Node> nodes = getSrsDocument().getRootElement().selectNodes("group");
            for (int i = nodes.size() - 1; i >= 0; i--)
            {
                Element ele = (Element) nodes.get(i);
                if ("0".equals(ele.attributeValue("type")))
                {
                    elementCustom = ele;
                    break;
                }
            }
        }
        return elementCustom;
    }

    /**
     * 获取当前自定义树节点（可能是“自定义”节点的子节点
     *
     * @return 当前自定义树节点（可能是“自定义”节点的子节点
     */
    private TreeItem<TreeItemObject> getCurrentCustomTreeItem()
    {
        TreeItem<TreeItemObject> treeItem = null;
        if (!this.isSearchingProperty.getValue())
        {
            TreeItem<TreeItemObject> treeItemFoc = this.treeView.getSelectionModel().getSelectedItem();
            if (treeItemFoc != null && "0".equals(treeItemFoc.getValue().getElement().attributeValue("type")))
            {
                treeItem = treeItemFoc;
            } else
            {
                for (TreeItem<TreeItemObject> ti : this.treeView.getRoot().getChildren())
                {
                    if ("0".equals(ti.getValue().getElement().attributeValue("type")))
                    {
                        treeItem = ti;
                    }
                }
            }
        }
        return treeItem;
    }

    /**
     * @return 获取收藏夹xml节点
     */
    private Element getElementFavorite()
    {
        if (this.elementFavorite == null)
        {
            List<Node> nodes = srsDocument.getRootElement().selectNodes("group");
            for (int i = nodes.size() - 1; i >= 0; i--)
            {
                Element ele = (Element) nodes.get(i);
                if ("100".equals(ele.attributeValue("type")))
                {
                    this.elementFavorite = ele;
                    break;
                }
            }
        }
        return this.elementFavorite;
    }

    /**
     * 获取收藏夹节点
     *
     * @return 当前自定义树节点（可能是“自定义”节点的子节点
     */
    private TreeItem<TreeItemObject> getFavoriteTreeItem()
    {
        TreeItem<TreeItemObject> treeItem = null;
        if (!this.isSearchingProperty.getValue())
        {
            TreeItem<TreeItemObject> treeItemFoc = this.treeView.getSelectionModel().getSelectedItem();
            if (treeItemFoc != null && "100".equals(treeItemFoc.getValue().getElement().attributeValue("type")))
            {
                treeItem = treeItemFoc;
            } else
            {
                for (TreeItem<TreeItemObject> ti : this.treeView.getRoot().getChildren())
                {
                    if ("100".equals(ti.getValue().getElement().attributeValue("type")))
                    {
                        treeItem = ti;
                        break;
                    }
                }
            }
        }
        return treeItem;
    }

    /**
     * 保存xml
     */
    private void saveXml()
    {
        try
        {
            String xmlPath = getClass().getResource("SRSManager.xml").getFile();
            XMLWriter xmlWriter = new XMLWriter(new OutputStreamWriter(new FileOutputStream(xmlPath), "UTF-8"));
            xmlWriter.write(srsDocument);
            xmlWriter.close();
        } catch (Exception ex)
        {
        }
    }

    private String initGroupImage(Element element)
    {
        String imageName = "folder_16.png";
        if (element != null)
        {
            int groupType = Integer.valueOf(element.attributeValue("type"));
            if (groupType == 1)
            {
                imageName = "foldergeosref_16.png";
            } else if (groupType == 2)
            {
                imageName = "folderprjsref_16.png";
            } else if (groupType == 0)
            {
                imageName = "folder_16.png";
            } else if (groupType == 100)
            {
                imageName = "favorite_16.png";
            }
        }
        return imageName;
    }
    //endregion

    //region 静态方法

    /**
     * 获取打开坐标系xml文档
     *
     * @return
     */
    public static Document getSrsDocument()
    {
        if (srsDocument == null)
        {
            try
            {
                SAXReader reader = new SAXReader();
                srsDocument = reader.read(SRefManagerDialog.class.getResourceAsStream("SRSManager.xml"));
            } catch (Exception ex)
            {
                System.out.println("加载SRSManager.xml出错。");
                srsDocument = null;
            }
        }
        return srsDocument;
    }

    /**
     * 将数据库中的自定义参照系添加到xml
     *
     * @param db
     */
    public static void addCustomSrefs(DataBase db)
    {
        if (db != null && db.hasOpened())
        {
            for (SRefType sRefType : SRefType.values())
            {
                int[] ids = db.getSpatialRefs(sRefType);
                if (ids != null && ids.length > 0)
                {
                    for (int id : ids)
                    {
                        if (id > 600)
                        {
                            SRefData sref = db.getSRef(id);
                            if (sref != null && !isSrefNameExisted(sref.getSRSName()))
                            {
                                addSref(sref);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 添加参照系到自定义
     *
     * @param sRef
     * @return
     */
    public static Element addSref(SRefData sRef)
    {
        return addSref(sRef, getElementCustom());
    }

    /**
     * 添加参照系到自定义节点
     *
     * @param sRef
     * @param eleParent
     * @return
     */
    public static Element addSref(SRefData sRef, Element eleParent)
    {
        Element ele = null;
        if (sRef != null && eleParent != null)
        {
            try
            {
                SRSItem srsItem = new SRSItem();
                srsItem.setSrefInfo(sRef);
                srsItem.setName(sRef.getSRSName());
                Document xd = DocumentHelper.parseText(srsItem.toXML());
                ele = (Element) xd.getRootElement().clone();
                eleParent.add(ele);
            } catch (Exception ex)
            {
            }
        }
        return ele;
    }

    /**
     * 判断指定名称的数据是否存在
     *
     * @param srsName
     * @return
     */
    public static boolean isSrefNameExisted(String srsName)
    {
        List<Node> nodes = SRefManagerDialog.getSrsDocument().selectNodes(String.format("参照系[sSRSName[text()='%s']]", srsName));
        return nodes != null && nodes.size() > 0;
    }

    /**
     * 根据名称获取参照系对象
     *
     * @param srsName
     * @return
     */
    public static SRefData getSrefByName(String srsName)
    {
        SRefData sref = null;
        List<Node> nodes = SRefManagerDialog.getSrsDocument().selectNodes(String.format("参照系[sSRSName[text()='%s']]", srsName));
        if (nodes != null && nodes.size() > 0)
        {
            SRSItem srsItem = new SRSItem();
            if (srsItem.fromXML(nodes.get(0).asXML()))
            {
                sref = srsItem.getSrefInfo();
            }
        }
        return sref;
    }
    //endregion

    //region 相关类
    class TreeItemObject
    {
        private String text;
        private Element element;
        private String imageName;
        private ObservableList<SRefItem> srsList;

        public TreeItemObject()
        {
            this(null);
            this.text = "坐标系设置管理";
        }

        public TreeItemObject(Element ele)
        {
            this(ele, null);
        }

        public TreeItemObject(Element ele, ObservableList<SRefItem> srsList)
        {
            this.element = ele;
            this.srsList = srsList;
            this.imageName = initGroupImage(this.element);
            if (this.element != null)
            {
                this.text = this.element.attributeValue("name");
            }
        }

        public String getText()
        {
            return text;
        }

        public void setText(String text)
        {
            this.text = text;
        }

        public Element getElement()
        {
            return element;
        }

        public void setElement(Element element)
        {
            this.element = element;
        }

        public String getImageName()
        {
            return imageName;
        }

        public void setImageName(String imageName)
        {
            this.imageName = imageName;
        }

        public ObservableList<SRefItem> getSrsList()
        {
            return srsList;
        }

        public void setSrsList(ObservableList<SRefItem> srsList)
        {
            this.srsList = srsList;
        }
    }

    public class SRefItem
    {
        private Element element;
        private String sRSName;
        private String type;
        private String epsgCode;
        private String spheroid;
        private String projName;
        private String imageName;
        private boolean isSRef = true;

        public SRefItem(Element ele)
        {
            this.element = ele;

            if (this.element != null)
            {
                if (this.element.getName().equals("group"))
                {
                    this.isSRef = false;
                    this.sRSName = this.element.attributeValue("name");
                    this.imageName = initGroupImage(this.element);
                } else
                {
                    this.sRSName = this.element.element("sSRSName").getTextTrim();
                    short iType = Short.valueOf(this.element.element("iType").getTextTrim());
                    this.type = LanguageConvert.sRefTypeConvert(SRefType.valueOf(iType));
                    this.epsgCode = this.element.element("EPSGID").getTextTrim();
                    this.spheroid = LanguageConvert.sRefEPTypeConvert(SRefEPType.valueOf(Integer.valueOf(this.element.element("iSpheroid").getTextTrim())));
                    this.projName = this.element.element("sProjName").getTextTrim();

                    this.imageName = "srefother_16.png";
                    if ("0".equals(this.element.getParent().attributeValue("type")))
                    {
                        this.imageName = "srefcustom_16.png";
                    } else
                    {
                        if (iType == 1)
                        {
                            this.imageName = "srefgeo_16.png";
                        } else if (iType == 3)
                        {
                            this.imageName = "srefprj_16.png";
                        }
                    }
                }
            }
        }

        public String getImageName()
        {
            return imageName;
        }

        public String getSRSName()
        {
            return sRSName;
        }

        public void setSRSName(String sRSName)
        {
            this.sRSName = sRSName;
        }

        public String getType()
        {
            return type;
        }

        public void setType(String type)
        {
            this.type = type;
        }

        public String getEpsgCode()
        {
            return epsgCode;
        }

        public void setEpsgCode(String epsgCode)
        {
            this.epsgCode = epsgCode;
        }

        public String getSpheroid()
        {
            return spheroid;
        }

        public void setSpheroid(String spheroid)
        {
            this.spheroid = spheroid;
        }

        public String getProjName()
        {
            return projName;
        }

        public Element getElement()
        {
            return element;
        }

        public void setElement(Element element)
        {
            this.element = element;
        }

        public boolean isSRef()
        {
            return isSRef;
        }
    }
    //endregion
}
