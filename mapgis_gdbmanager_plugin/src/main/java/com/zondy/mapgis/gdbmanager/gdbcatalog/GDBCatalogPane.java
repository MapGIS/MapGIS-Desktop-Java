package com.zondy.mapgis.gdbmanager.gdbcatalog;

import com.zondy.mapgis.analysis.imageanalysis.RasTrans;
import com.zondy.mapgis.base.*;
import com.zondy.mapgis.common.URLParse;
import com.zondy.mapgis.controls.common.ZDToolBar;
import com.zondy.mapgis.dataconvert.DataConverts;
import com.zondy.mapgis.filedialog.GDBOpenFileDialog;
import com.zondy.mapgis.gdbmanager.CommonFunctions;
import com.zondy.mapgis.gdbmanager.clientconfig.ClientConfigDialog;
import com.zondy.mapgis.gdbmanager.gdbcatalog.index.IndexManagerDialog;
import com.zondy.mapgis.gdbmanager.plugin.DataPropertyDW;
import com.zondy.mapgis.geodatabase.*;
import com.zondy.mapgis.geodatabase.config.*;
import com.zondy.mapgis.geodatabase.middleware.MiddleWareConfigFactory;
import com.zondy.mapgis.geodatabase.middleware.MiddleWareType;
import com.zondy.mapgis.geodatabase.middleware.NodeType;
import com.zondy.mapgis.geodatabase.net.NetCls;
import com.zondy.mapgis.geodatabase.raster.*;
import com.zondy.mapgis.geometry.GeomType;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.IDockWindow;
import com.zondy.mapgis.sref.ConnectGisServerDialog;
import com.zondy.mapgis.sref.SRefManagerDialog;
import com.zondy.mapgis.srs.SRefData;
import com.zondy.mapgis.systemlib.SystemLibrarys;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/*节点的信息说明(0级根节点不显示）
节点	              Text    ID	        URL    Tag
1.Server	          name    ConnectType   url    server(hasConnected)
    2.DataBase	      name    id	        url    db(hasOpened)
        3.fds	      name    id	        url    XClsType
            4.sfcls   name    id	        url    XClsType
        3.sfcls	      name    id	        url    XClsType
 */

//删除数据这样用？sfcls.remove((new SFeatureCls(db)).remove(clsID))

/**
 * @author CR
 * @file GDBCatalogPane.java
 * @brief GDB目录树
 * @create 2019-11-29.
 */
public class GDBCatalogPane extends VBox {
    //region ImageList(树节点、右键菜单）
    private final List<Image> treeImageList = new ArrayList<>(Arrays.asList(
            new Image("/Png_GDBServer_16.png"),     //0
            new Image("/Png_GDBServerError_16.png"),//1
            new Image("/Png_GDataBase_16.png"),     //2
            new Image("/Png_GDataBaseError_16.png"),//3
            new Image("/Png_SRef_16.png"),          //4
            new Image("/Png_SpacialData_16.png"),   //5
            new Image("/Png_FDs_16.png"),         //6
            new Image("/Png_SfClsPnt_16.png"),    //7
            new Image("/Png_SfClsLin_16.png"),    //8
            new Image("/Png_SfClsReg_16.png"),    //9
            new Image("/Png_SfClsSurface_16.png"),//10
            new Image("/Png_SfClsEntity_16.png"),//11
            new Image("/Png_SfCls_16.png"),      //12
            new Image("/Png_ACls_16.png"),       //13
            new Image("/Png_OCls_16.png"),       //14
            new Image("/Png_NetCls_16.png"),     //15
            new Image("/Png_RasterCatalog_16.png"),//16
            new Image("/Png_RasterDs_16.png"),     //17
            new Image("/Png_SRefGeoFolder_16.png"),//18
            new Image("/Png_SRefGeo_16.png"),      //19
            new Image("/log_16.png"),              //20
            new Image("/logLogin_16.png"),         //21
            new Image("/logSys_16.png"),           //22
            new Image("/logUser_16.png"),          //23
            new Image("/Png_MosaicDataSet_16.png"),//24
            new Image("/unknown_16.png")));        //25

    private final List<Image> menuImageList = new ArrayList<>(Arrays.asList(
            new Image("/refresh_16.png"),//0-刷新
            new Image("/delete_16.png"),//1-删除
            new Image("/property_16.png"),//2-属性
            new Image("/search_16.png"),//3-搜索
            new Image("/copy_16.png"),//4-复制
            new Image("/paste_16.png"),//5-粘贴
            new Image("/copyURL_16.png"),//6-复制URL
            new Image("connectServer_16.png"),//7-连接
            new Image("disConnectServer_16.png")));//8-断开连接
    //endregion

    private IApplication app; //当前应用程序框架对象
    private TreeView<TreeItemObject> treeView;//参照系树

    public GDBCatalogPane(IApplication app) {
        this.app = app;

        //region 工具栏按钮
        Button buttonClientManage = new Button("客户端配置管理", new ImageView("/clientConfigManage_16.png"));
        buttonClientManage.setTooltip(new Tooltip("客户端配置管理"));
        buttonClientManage.setOnAction(event ->
        {
            MapGisEnv gisEnv = EnvConfig.getGisEnv();
            String oldFontLib = (gisEnv != null) ? gisEnv.getClib() : "";//记录原来的字体库
            String oldSysLib = (gisEnv != null) ? gisEnv.getSlib() : "";//记录原来的系统库

            ClientConfigDialog dlg = new ClientConfigDialog();
            dlg.initOwner(this.getCurrentWindow());
            if (dlg.showAndWait().equals(Optional.of(ButtonType.OK))) {
                gisEnv = EnvConfig.getGisEnv();
                String newFontLib = (gisEnv != null) ? gisEnv.getClib() : "";//记录原来的字体库
                String newSysLib = (gisEnv != null) ? gisEnv.getSlib() : "";//记录原来的字体库
                if (!oldFontLib.equalsIgnoreCase(newFontLib) || !oldSysLib.equalsIgnoreCase(newSysLib)) {
                    SystemLibrarys sysLibs = SystemLibrarys.getSystemLibrarys();
                    if (sysLibs != null) {
                        sysLibs.reOpen();
                    }
                }
            }
        });

        Button buttonAddServer = new Button("添加数据源", new ImageView("/addServer_16.png"));
        buttonAddServer.setTooltip(new Tooltip("添加数据源"));
        buttonAddServer.setOnAction(event ->
        {
            AddServerDialog dlg = new AddServerDialog();
            dlg.initOwner(this.getCurrentWindow());
            if (dlg.showAndWait().equals(Optional.of(ButtonType.OK))) {
                List<String> svrNames = this.sortDataServers();
                String dsName = dlg.getServerInfo().getSvcName();
                this.addServerTreeItem(dsName, svrNames.indexOf(dsName));
            }
        });

        Button buttonRefresh = new Button("刷新", new ImageView("/refresh_16.png"));
        buttonRefresh.setTooltip(new Tooltip("刷新"));
        buttonRefresh.setOnAction(event ->
        {
            this.closeAndDisConnect();//关闭打开的数据库和连接的数据源
            this.treeView.getRoot().getChildren().clear();
            this.addServerTreeItems();
        });

        ZDToolBar toolBar = new ZDToolBar(buttonClientManage, new Separator(), buttonAddServer, buttonRefresh);
        //endregion

        //region Catalog树
        this.treeView = new TreeView<>();
        this.treeView.setShowRoot(false);
        CatalogTreeItem root = new CatalogTreeItem(new TreeItemObject("GDBCatalog"));
        this.treeView.setRoot(root);
        this.treeView.setCellFactory(param ->
        {
            return new CatalogTreeCell();
        });

        //切换节点事件
        this.treeView.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) ->
        {
            if (newValue != null) {
                this.viewProperty(newValue, false);

                ////region 判断当前节点能否拖动
                //
                //boolean bAllowDrag = false;
                //String url = treeItem.Tag
                //as String;
                //if (!String.IsNullOrEmpty(url))
                //
                //{
                //    bAllowDrag = true;
                //    CatalogTreeItem ptNode = treeItem.ParentNode;
                //    if (ptNode != null)
                //    {
                //        XClsType clsType = MapGIS.UI.Controls.LanguageConvert.XClsTypeConvert(ptNode[0]as String);
                //        bAllowDrag = (clsType != XClsType.XFds);
                //    }
                //}
                //this.treeList1.OptionsBehavior.DragNodes = bAllowDrag;
                //
                ////endregion

                ////region 修改搜索视窗中的“查找范围”下拉框
                ////1、通过当前选中节点看是否有当前数据库（选中数据源时无法确定当前数据库）。2、是否可以从当前节点查找，如果当前节点是参照系什么的，就不可以
                //
                //IDockWindow dw = null;
                //this.app.getPluginContainer().DockWindows.TryGetValue(
                //
                //        typeof(DWSearch).
                //
                //                ToString(), out dw);
                //if (dw instanceof DWSearch)
                //
                //{
                //    (dw as DWSearch).Search.CanSearchInCurrentDataBase = treeItem.Level > 1;
                //    boolean canSearchInNode = false;
                //    if (treeItem.Level < 3)
                //        canSearchInNode = true;
                //    else if (this.getItemOnLevel2(treeItem)[0] as String ==Resources.String_SpatialData)
                //    {
                //        String strType = treeItem.ParentNode[0] as String;
                //        if (treeItem.Tag instanceof DataBase || (treeItem.Tag instanceof String && (strType == Resources.String_FeatureDataset || strType == Resources.String_RasterCatalog)))
                //            canSearchInNode = true;
                //    }
                //    (dw as DWSearch).Search.CanSearchInCurrentNode = canSearchInNode;
                //}
                //
                ////endregion
            }
        });
        //双击连接数据源
        this.treeView.setOnMouseClicked(event ->
        {
            TreeItem<TreeItemObject> treeItem = getTreeItemByNode(event.getPickResult().getIntersectedNode());
            if (treeItem != null) {
                this.treeView.getSelectionModel().select(treeItem);

                //region 双击
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    if (treeView.getTreeItemLevel(treeItem) == 1) {
                        Server ds = (Server) treeItem.getValue().getTag();
                        if (ds == null || !ds.hasConnected()) {
                            GDBCatalogPane.this.connectDataSource((CatalogTreeItem) treeItem);
                        }
                    }
                }
                //endregion
            }
        });
        //endregion

        this.getChildren().addAll(toolBar, this.treeView);
        VBox.setVgrow(this.treeView, Priority.ALWAYS);

        this.addServerTreeItems();//添加数据源节点
    }

    //region 公共方法

    /**
     * 获取当前的焦点树节点
     *
     * @return 当前的焦点树节点
     */
    public TreeItem<TreeItemObject> getFocusedTreeItem() {
        return this.treeView.getFocusModel().getFocusedItem();
    }

    /**
     * 关闭数据库、断开数据源的
     */
    public void closeAndDisConnect() {
        for (TreeItem<TreeItemObject> dsItem : this.treeView.getRoot().getChildren()) {
            Server ds = (Server) dsItem.getValue().getTag();
            if (ds != null && ds.hasConnected()) {
                for (TreeItem<TreeItemObject> dbItem : dsItem.getChildren()) {
                    DataBase db = getOwnerDataBase(dbItem);
                    if (db != null && db.hasOpened()) {
                        db.close();
                    }
                }
                ds.disConnect();
            }
        }
    }

    /**
     * 刷新数据源节点，我们可能在其他地方连接了数据源，在GDBCatalog被激活时，连接一下
     */
    public void refreshServerNodes() {
        for (TreeItem<TreeItemObject> dsItem : this.treeView.getRoot().getChildren()) {
            Server ds = (Server) dsItem.getValue().getTag();
            if (ds == null || !ds.hasConnected()) {
                ds = new Server();
                String dsName = dsItem.getValue().getText();
                String[] logins = Server.getLogInfo(dsName);
                if (logins != null && logins.length == 2) {
                    if (ds.connect(dsName, logins[0], logins[1]) > 0) {
                        dsItem.getValue().setTag(ds);
                        dsItem.setGraphic(new ImageView(this.treeImageList.get(0)));
                    }
                }
            }
        }
    }
    //endregion

    //region 私有方法

    /**
     * 将给定树节点设为焦点节点
     *
     * @param treeItem 给定树节点
     */
    private void focusTreeItem(TreeItem<TreeItemObject> treeItem) {
        if (treeItem != null) {
            treeItem.setExpanded(true);
            if (!treeItem.equals(this.getFocusedTreeItem())) {
                this.treeView.getSelectionModel().clearSelection();
                this.treeView.getSelectionModel().select(treeItem);
            }
        }
    }

    /**
     * 获取数据源节点集合
     *
     * @return 数据源节点集合
     */
    private ObservableList<TreeItem<TreeItemObject>> getServerNodes() {
        return this.treeView.getRoot().getChildren();
    }

    /**
     * 从指定节点的子节点中查找指定名称的节点
     *
     * @param parItem  父节点
     * @param nodeName 目标子节点的名称
     * @return
     */
    public CatalogTreeItem findTreeItemByName(TreeItem<TreeItemObject> parItem, String nodeName) {
        return this.findTreeItemByName(parItem, nodeName, null);
    }

    /**
     * 从指定节点的子节点中查找指定名称的节点
     *
     * @param parItem  父节点
     * @param nodeName 目标子节点的名称
     * @param clsType  数据类型（现在没有占位节点，不同类型可以有同名数据，因此查找类数据时需要传递数据类型）
     * @return
     */
    private CatalogTreeItem findTreeItemByName(TreeItem<TreeItemObject> parItem, String nodeName, XClsType clsType) {
        CatalogTreeItem treeItem = null;
        if (!XString.isNullOrEmpty(nodeName)) {
            ObservableList<TreeItem<TreeItemObject>> itemList = parItem == null ? this.treeView.getRoot().getChildren() : parItem.getChildren();
            for (TreeItem<TreeItemObject> subItem : itemList) {
                if (nodeName.equals(subItem.getValue().getText()) && (clsType == null || (subItem.getValue().getTag() instanceof XClsType && clsType.equals(subItem.getValue().getTag())))) {
                    treeItem = (CatalogTreeItem) subItem;
                    break;
                }
            }
        }
        return treeItem;
    }

    /**
     * 根据数据类型和几何类型获取排序类型。
     *
     * @param clsType
     * @param geomType
     * @return
     */
    private String getSortType(XClsType clsType, GeomType geomType) {
        String sortType = "";
        switch (clsType) {
            case XFds:
                sortType = "A";
                break;
            case XSFCls:
                switch (geomType) {
                    case GeomPnt:
                        sortType = "B";
                        return sortType;
                    case GeomLin:
                        sortType = "C";
                        return sortType;
                    case GeomReg:
                        sortType = "D";
                        return sortType;
                    case GeomSurface:
                        sortType = "E";
                        return sortType;
                    case GeomEntity:
                        sortType = "F";
                        return sortType;
                    default:
                        sortType = "G";
                        return sortType;
                }
            case XACls:
                sortType = "H";
                break;
            case XOCls:
                sortType = "I";
                break;
            case XGNet:
                sortType = "J";
                break;
            case XRcat:
                sortType = "K";
                break;
            case XRds:
                sortType = "L";
                break;
            case XMosaicDS:
                sortType = "M";
                break;
            default:
                sortType = "N";
                break;
        }

        return sortType;
    }

    /**
     * 计算新增数据的插入索引（排序）
     *
     * @param parItem
     * @param name
     * @param clsType
     * @return
     */
    private int calcAddedIndex(TreeItem<TreeItemObject> parItem, String name, XClsType clsType) {
        return this.calcAddedIndex(parItem, name, clsType, null);
    }

    /**
     * 计算新增数据的插入索引（排序）
     *
     * @param parItem
     * @param name
     * @param clsType
     * @param geomType
     * @return
     */
    private int calcAddedIndex(TreeItem<TreeItemObject> parItem, String name, XClsType clsType, GeomType geomType) {
        int index = 0;
        if (parItem != null && !XString.isNullOrEmpty(name)) {
            List<XClsType> typeList = Arrays.asList(XClsType.XFds, XClsType.XSFCls, XClsType.XACls, XClsType.XOCls, XClsType.XRcat, XClsType.XRds, XClsType.XMosaicDS);
            int typeIndex = typeList.indexOf(clsType);
            List<String> sortNames = new ArrayList();
            for (TreeItem<TreeItemObject> clsItem : parItem.getChildren()) {
                XClsType ct = (XClsType) clsItem.getValue().getTag();
                if (typeList.indexOf(ct) > typeIndex) {
                    break;
                }
                sortNames.add(String.format("%s_%s", clsItem.getValue().getSortType(), clsItem.getValue().getText()));
            }
            String sortName = String.format("%s_%s", this.getSortType(clsType, geomType), name);
            sortNames.add(sortName);
            sortNames.sort(new StringCompare());
            index = sortNames.indexOf(sortName);
        }
        return index;
    }

    /**
     * 通过选中树节点获取 TreeItem
     *
     * @param node 树节点
     * @return TreeItem
     */
    private TreeItem getTreeItemByNode(Node node) {
        if (node == null) {
            return null;
        } else if (node instanceof TreeCell) {
            return ((TreeCell) node).getTreeItem();
        } else {
            return getTreeItemByNode(node.getParent());
        }
    }

    /**
     * 读取数据源并按名称排序
     *
     * @return 排序后的数据源名称
     */
    private List<String> sortDataServers() {
        List<String> svrNames = new ArrayList<>();
        for (int i = 0; i < SvcConfig.count(); i++) {
            DataSrcInfo dsInfo = SvcConfig.get(i);
            if (dsInfo != null) {
                boolean canShow = true;
                switch (dsInfo.getSvcType()) {
                    case Local:
                    case DBSQL:
                        canShow = XFunctions.isSystemWindows();
                        break;
                }
                if (canShow) {
                    svrNames.add(dsInfo.getSvcName());
                }
            }
        }
        svrNames.sort(new StringCompare());
        return svrNames;
    }

    /**
     * 添加数据源节点
     */
    private void addServerTreeItems() {
        List<String> svrNames = this.sortDataServers();
        for (String svrName : svrNames) {
            this.addServerTreeItem(svrName, -1);
        }
    }

    /**
     * 创建数据源节点
     *
     * @param dsName 数据源名称
     * @param index  添加节点的索引，不在正常范围是追加到最后。想追加到最后给-1
     * @return 数据源节点
     */
    private CatalogTreeItem addServerTreeItem(String dsName, int index) {
        CatalogTreeItem treeItem = null;
        if (!XString.isNullOrEmpty(dsName)) {
            Server ds = this.tryConnectDataSource(dsName, null, null, false, false);//创建是在初始化或刷新时，连接失败不弹框错误消息
            ConnectType conType = ds != null ? ds.getConnectType() : SvcConfig.get(dsName).getSvcType();
            treeItem = new CatalogTreeItem(new TreeItemObject(dsName, ds != null ? ds : new Server(), conType.value(), String.format("GDBP://%s", dsName)), new ImageView(treeImageList.get((ds != null && ds.hasConnected()) ? 0 : 1)));
            if (index < 0 || index > this.treeView.getRoot().getChildren().size()) {
                this.treeView.getRoot().getChildren().add(treeItem);
            } else {
                this.treeView.getRoot().getChildren().add(index, treeItem);
            }
            CatalogTreeItem finalTreeItem = treeItem;
            treeItem.getValue().tagProperty().addListener((observable, oldValue, newValue) -> finalTreeItem.reset());
        }
        return treeItem;
    }

    /**
     * 创建数据库节点
     *
     * @param ds      数据库所属数据源
     * @param dbID    数据库ID
     * @param parItem 数据源节点，作为新增数据库节点的父节点
     * @return 数据库节点
     */
    private CatalogTreeItem addDataBaseTreeItem(Server ds, int dbID, TreeItem<TreeItemObject> parItem) {
        CatalogTreeItem treeItem = null;
        if (ds != null && ds.hasConnected() && dbID > 0 && parItem != null) {
            String dbName = ds.getDBName(dbID);
            treeItem = new CatalogTreeItem(new TreeItemObject(dbName, null, dbID, String.format("%s/%s", ds.getURL(), dbName)), new ImageView(treeImageList.get(2)));

            List<String> names = new ArrayList<>();
            for (TreeItem<TreeItemObject> ti : parItem.getChildren()) {
                names.add(ti.getValue().getText());
            }
            names.add(dbName);
            names.sort(new StringCompare());

            parItem.getChildren().add(names.indexOf(dbName), treeItem);
        }
        return treeItem;
    }

    /**
     * 添加数据库中指定类型的数据节点
     *
     * @param db      地理数据库
     * @param clsType 待创建对象的类型
     * @param parItem 待创建子节点的父节点
     */
    private void addClsTreeItems(DataBase db, XClsType clsType, TreeItem<TreeItemObject> parItem) {
        if (db != null && parItem != null) {
            int dsID = parItem.getValue().getTag() instanceof DataBase ? 0 : parItem.getValue().getId();
            ConnectType serverType = db.getServer().getConnectType();
            if ((XClsType.XSFCls.equals(clsType) || XClsType.XACls.equals(clsType) || XClsType.XOCls.equals(clsType)/* || XClsType.XRds.equals(clsType)*/)
                    && (ConnectType.DBOracle.equals(serverType) || ConnectType.DBPG.equals(serverType) || ConnectType.LocalPlus.equals(serverType))) {
                IXClsInfo[] infos = db.getXclsInfoList(clsType, dsID, XClsEnumType.Archived, true);
                if (infos != null) {
                    for (IXClsInfo info : infos) {
                        this.addClsTreeItem(db, clsType, info.getID(), info, parItem);
                    }
                }
            } else {
                int[] clsIDs = db.getXclses(clsType, dsID);
                if (clsIDs != null) {
                    Map<String, Integer> map = new TreeMap(new StringCompare());
                    for (int id : clsIDs) {
                        map.put(db.getXclsName(clsType, id), id);
                    }

                    for (int clsID : map.values()) {
                        this.addClsTreeItem(db, clsType, clsID, null, parItem);
                    }
                }
            }
        }
    }

    /**
     * 构造类节点
     *
     * @param db      地理数据库
     * @param clsType 待创建对象的类型
     * @param clsID   对象ID
     * @param clsInfo 新创建的数据对应的节点
     * @param parItem 待创建子节点的父节点
     * @return 数据库对象节点
     */
    private CatalogTreeItem constructClsTreeItem(DataBase db, XClsType clsType, int clsID, IXClsInfo clsInfo, TreeItem<TreeItemObject> parItem) {
        CatalogTreeItem treeItem = null;
        if (db != null && (clsID > 0 || clsInfo != null) && parItem != null) {
            int dsID = parItem.getValue().getTag() instanceof DataBase ? 0 : parItem.getValue().getId();
            String parURL = String.format("%s%s", db.getURL(), (db.getURL().endsWith("/") ? "" : "/"));
            if (dsID > 0) {
                XClsType parType = XClsType.XRds.equals(clsType) ? XClsType.XRcat : XClsType.XFds;
                parURL += String.format("%s/%s/", URLParse.xClsTypeToString(parType), db.getXclsName(parType, dsID));
            }
            String typeUrl = URLParse.xClsTypeToString(clsType);

            //region 根据类型获取图标索引
            int imageIndex = -1;
            GeomType geomType = GeomType.GeomUnknown;
            if (XClsType.XFds.equals(clsType)) {
                imageIndex = 6;
            } else if (XClsType.XSFCls.equals(clsType)) {
                imageIndex = 12;
                if (clsInfo == null) {
                    clsInfo = db.getXclsInfo(clsType, clsID);
                }
                if (clsInfo instanceof SFClsInfo) {
                    geomType = ((SFClsInfo) clsInfo).getfType();
                    if (GeomType.GeomPnt.equals(geomType)) {
                        imageIndex = 7;
                    } else if (GeomType.GeomLin.equals(geomType)) {
                        imageIndex = 8;
                    } else if (GeomType.GeomReg.equals(geomType)) {
                        imageIndex = 9;
                    } else if (GeomType.GeomSurface.equals(geomType)) {
                        imageIndex = 10;
                    } else if (GeomType.GeomEntity.equals(geomType)) {
                        imageIndex = 11;
                    }
                }
            } else if (XClsType.XACls.equals(clsType)) {
                imageIndex = 13;
            } else if (XClsType.XOCls.equals(clsType)) {
                imageIndex = 14;
            } else if (XClsType.XGNet.equals(clsType)) {
                imageIndex = 15;
            } else if (XClsType.XRcat.equals(clsType)) {
                imageIndex = 16;
            } else if (XClsType.XRds.equals(clsType)) {
                imageIndex = 17;
            } else if (XClsType.XMosaicDS.equals(clsType)) {
                imageIndex = 24;
            } else {
                imageIndex = 25;//未知
            }
            //endregion

            String clsName = clsInfo != null ? clsInfo.getName() : db.getXclsName(clsType, clsID);
            String clsUrl = String.format("%s%s/%s", parURL, typeUrl, clsName);
            treeItem = new CatalogTreeItem(new TreeItemObject(clsName, clsType, clsID, clsUrl, this.getSortType(clsType, geomType)), new ImageView(treeImageList.get(imageIndex)));
        }
        return treeItem;
    }

    /**
     * 添加类节点
     *
     * @param db
     * @param clsType
     * @param clsID
     * @param clsInfo
     * @param parItem
     * @return
     */
    private CatalogTreeItem addClsTreeItem(DataBase db, XClsType clsType, int clsID, IXClsInfo clsInfo, TreeItem<TreeItemObject> parItem) {
        CatalogTreeItem treeItem = null;
        if (parItem != null) {
            treeItem = this.constructClsTreeItem(db, clsType, clsID, clsInfo, parItem);
            if (treeItem != null) {
                parItem.getChildren().add(treeItem);
            }
        }
        return treeItem;
    }

    private CatalogTreeItem insertClsTreeItem(DataBase db, XClsType clsType, int clsID, IXClsInfo clsInfo, TreeItem<TreeItemObject> parItem) {
        CatalogTreeItem treeItem = null;
        if (parItem != null) {
            treeItem = this.constructClsTreeItem(db, clsType, clsID, clsInfo, parItem);
            GeomType geomType = GeomType.GeomUnknown;
            if (XClsType.XSFCls.equals(clsType)) {
                if (clsInfo == null) {
                    clsInfo = db.getXclsInfo(XClsType.XSFCls, clsID);
                }
                geomType = ((SFClsInfo) clsInfo).getfType();
            }

            int index = this.calcAddedIndex(parItem, db.getXclsName(clsType, clsID), clsType, geomType);
            if (index >= 0 && index < parItem.getChildren().size()) {
                parItem.getChildren().add(index, treeItem);
            } else {
                parItem.getChildren().add(treeItem);
            }
        }
        return treeItem;
    }

    /**
     * 连接数据源
     *
     * @param dsName          数据源名称
     * @param user            用户名
     * @param pswd            密码
     * @param connectByDialog 用给定的用户名密码连接失败时，是否弹框输入用户名密码进行连接
     * @param showError       是否显示连接错误信息
     * @return 连接的数据源对象
     */
    private Server tryConnectDataSource(String dsName, String user, String pswd, boolean connectByDialog, boolean showError) {
        Server ds = null;

        //region 用参数或获取的用户名连接
        if (user == null) {
            String[] logins = Server.getLogInfo(dsName);
            if (logins != null && logins.length > 2) {
                user = logins[0];
                pswd = logins[1];
            }
        }

        if (user != null) {
            ds = new Server();
            if (ds.connect(dsName, user, pswd) <= 0) {
                if (showError) {
                    MessageBox.information("数据源连接失败。");
                    //MapGISErrorForm.ShowLastError();
                }
                ds = null;
            }
        }
        //endregion

        //region 本地数据源连接
        if (ds == null && ("MapGISLocal".equalsIgnoreCase(dsName) || "MapGISLocalPlus".equalsIgnoreCase(dsName))) {
            ds = new Server();
            if (ds.connect(dsName, user, pswd) <= 0) {
                if (showError) {
                    MessageBox.information("数据源连接失败。");
                    //MapGISErrorForm.ShowLastError();
                }
                ds = null;
            }
        }
        //endregion

        //region 弹框输入用户名密码连接
        if (ds == null && connectByDialog) {
            ConnectGisServerDialog dlg = new ConnectGisServerDialog(dsName);
            dlg.initOwner(this.getCurrentWindow());
            if (dlg.showAndWait().equals(Optional.of(ButtonType.OK))) {
                ds = dlg.getServer();
                if (ds != null) {
                    Server.appendLogInfo(dsName, ds.getLogin()[0], ds.getLogin()[1]);
                }
            }
        }
        //endregion

        return ds;
    }

    /**
     * 获取数据库节点的数据库对象（打开后可能会更换图标）
     *
     * @param treeItem 数据库节点
     * @param open     没有打开时是否打开
     * @return 获取或打开的数据库
     */
    public DataBase getTreeItemDataBase(TreeItem<TreeItemObject> treeItem, boolean open) {
        DataBase db = null;
        if (treeItem != null && this.treeView.getTreeItemLevel(treeItem) == 2) {
            db = getOwnerDataBase(treeItem);
            if ((db == null || !db.hasOpened()) && open) {
                Server ds = (Server) treeItem.getParent().getValue().getTag();
                db = ds.openGDB(treeItem.getValue().getId());
                if (db == null || !db.hasOpened())//数据库错误
                {
                    db = new DataBase();
                    treeItem.setGraphic(new ImageView(GDBCatalogPane.this.treeImageList.get(3)));
                } else {
                    treeItem.setGraphic(new ImageView(GDBCatalogPane.this.treeImageList.get(2)));
                }
                treeItem.getValue().setTag(db);
            }
        }
        return db;
    }

    /**
     * 获取节点数据所属的数据库
     *
     * @param treeItem
     * @return
     */
    public DataBase getOwnerDataBase(TreeItem<TreeItemObject> treeItem) {
        DataBase db = null;
        if (treeItem != null && this.treeView.getTreeItemLevel(treeItem) >= 2) {
            TreeItem<TreeItemObject> dbItem = treeItem;
            while (this.treeView.getTreeItemLevel(dbItem) > 2) {
                dbItem = dbItem.getParent();
            }
            db = (DataBase) dbItem.getValue().getTag();
        }
        return db;
    }

    /**
     * 连接节点对应的数据源
     *
     * @param treeItem 数据源节点
     */
    private boolean connectDataSource(TreeItem<TreeItemObject> treeItem) {
        boolean rtn = false;
        if (treeItem != null && this.treeView.getTreeItemLevel(treeItem) == 1) {
            Server ds = this.tryConnectDataSource(treeItem.getValue().getText(), null, null, true, true);
            if (ds != null && ds.hasConnected()) {
                treeItem.setGraphic(new ImageView(this.treeImageList.get(0)));
                treeItem.getValue().setTag(ds);
                treeItem.setExpanded(true);
                this.treeView.refresh();

                for (TreeItem<TreeItemObject> dbItem : treeItem.getChildren()) {
                    SRefManagerDialog.addCustomSrefs((DataBase) dbItem.getValue().getTag());
                }

                rtn = true;
            }
        }
        return rtn;
    }

    /**
     * 属性
     */
    private void viewProperty(TreeItem<TreeItemObject> treeItem) {
        this.viewProperty(treeItem, true);
    }

    /**
     * 属性
     *
     * @param treeItem   要显示属性的节点
     * @param needActive 是否要激活/创建
     */
    private void viewProperty(TreeItem<TreeItemObject> treeItem, boolean needActive) {
        if (GDBCatalogPane.this.app != null) {
            IDockWindow dw = GDBCatalogPane.this.app.getPluginContainer().getDockWindows().get(DataPropertyDW.class.getName());
            if (dw == null) {
                if (needActive) {
                    dw = GDBCatalogPane.this.app.getPluginContainer().createDockWindow(DataPropertyDW.class.getName());
                }
            } else if (needActive) {
                GDBCatalogPane.this.app.getPluginContainer().activeDockWindow(dw);
            }

            if (dw instanceof DataPropertyDW) {
                ((DataPropertyDW) dw).displayInfo(treeItem);
            }
        }
    }

    private javafx.stage.Window window;

    /**
     * 获取当前窗口的window对象
     *
     * @return 当前窗口的window对象
     */
    private javafx.stage.Window getCurrentWindow() {
        if (this.window == null) {
            this.window = this.getScene().getWindow();
        }
        return this.window;
    }
    //endregion

    /**
     * 树节点类
     */
    private final class CatalogTreeItem extends TreeItem<TreeItemObject> {
        private boolean notInitialized = true;

        public CatalogTreeItem(final TreeItemObject obj) {
            this(obj, null);
        }

        public CatalogTreeItem(final TreeItemObject obj, final Node graphic) {
            super(obj, graphic);
        }

        @Override
        public ObservableList<TreeItem<TreeItemObject>> getChildren() {
            if (this.notInitialized) {
                this.notInitialized = false;

                int level = GDBCatalogPane.this.treeView.getTreeItemLevel(this);
                Object tag = getValue().getTag();
                if (level == 1)//数据源节点，列数据库
                {
                    Server ds = (Server) tag;
                    //if (ds == null || !ds.hasConnected())
                    //{
                    //    ds = GDBCatalogPane.this.tryConnectDataSource(getValue().getText(), null, null, true, true);
                    //    getValue().setTag(ds);
                    //}
                    //super.setGraphic(new ImageView(GDBCatalogPane.this.treeImageList.get((ds != null && ds.hasConnected()) ? 0 : 1)));
                    if (ds != null && ds.hasConnected()) {
                        int[] dbIDs = ds.getGdbs();
                        if (dbIDs != null) {
                            for (int dbID : dbIDs) {
                                if (dbID > 0) {
                                    GDBCatalogPane.this.addDataBaseTreeItem(ds, dbID, this);
                                }
                            }
                        }
                    }
                } else if (level == 2)//数据库节点（展开各类数据）
                {
                    DataBase db = GDBCatalogPane.this.getTreeItemDataBase(this, true);
                    if (db != null && db.hasOpened()) {
                        List<XClsType> typeList = new ArrayList<>();
                        ConnectType dsType = db.getServer().getConnectType();
                        if (dsType != ConnectType.Custom) {
                            typeList.addAll(Arrays.asList(XClsType.XFds, XClsType.XSFCls, XClsType.XACls, XClsType.XOCls));
                            if (dsType == ConnectType.Local) {
                                typeList.addAll(Arrays.asList(XClsType.XRcat, XClsType.XRds));//本地连接数据源暂时不支持镶嵌数据集功能 解决bug6091 zkj-2018-04-02
                            } else if (dsType != ConnectType.DBDm) {
                                typeList.addAll(Arrays.asList(XClsType.XRcat, XClsType.XRds, XClsType.XMosaicDS));
                            }
                        } else {
                            DataSrcInfo srcInfo = SvcConfig.get(db.getServer().getSvrName());
                            if (srcInfo != null) {
                                String dnsName = srcInfo.getDnsName();
                                String mwName = dnsName.substring(0, dnsName.indexOf('&'));
                                MiddleWareConfigFactory mwcFactory = new MiddleWareConfigFactory(mwName);
                                if (mwcFactory.catalogNodeValidChk(NodeType.FdsFloder)) {
                                    typeList.add(XClsType.XFds);
                                }
                                if (mwcFactory.catalogNodeValidChk(NodeType.SFCLSFloder)) {
                                    typeList.add(XClsType.XSFCls);
                                }
                                if (mwcFactory.catalogNodeValidChk(NodeType.ACLSFloder)) {
                                    typeList.add(XClsType.XACls);
                                }
                                if (mwcFactory.catalogNodeValidChk(NodeType.OCLSFloder)) {
                                    typeList.add(XClsType.XOCls);
                                }
                                if (mwcFactory.catalogNodeValidChk(NodeType.RcatFloder)) {
                                    typeList.add(XClsType.XRcat);
                                }
                                if (mwcFactory.catalogNodeValidChk(NodeType.RdsFloder)) {
                                    typeList.add(XClsType.XRds);
                                }
                                mwcFactory.freeFactory();
                            }
                        }

                        for (XClsType clsType : typeList) {
                            addClsTreeItems(db, clsType, this);
                        }
                    }
                } else if (level == 3)//展开含子元素的类对象节点（包括要素数据集、栅格目录）
                {
                    XClsType clsType = (XClsType) tag;
                    DataBase db = getOwnerDataBase(this);
                    if (db != null && db.hasOpened()) {
                        if (XClsType.XFds.equals(clsType)) {
                            List<XClsType> typeList = new ArrayList<>();
                            ConnectType dsType = db.getServer().getConnectType();
                            if (dsType != ConnectType.Custom) {
                                typeList.addAll(Arrays.asList(XClsType.XSFCls, XClsType.XACls, XClsType.XOCls));
                                if (!ConnectType.DBDm.equals(dsType)) {
                                    typeList.add(XClsType.XGNet);
                                }
                            } else {
                                DataSrcInfo dsInfo = SvcConfig.get(db.getServer().getSvrName());
                                String dnsName = dsInfo.getDnsName();
                                String mwName = dnsName.substring(0, dnsName.indexOf('&'));
                                MiddleWareConfigFactory mwcFactory = new MiddleWareConfigFactory(mwName);

                                if (mwcFactory.catalogNodeValidChk(NodeType.SFCLSFloder)) {
                                    typeList.add(XClsType.XSFCls);
                                }
                                if (mwcFactory.catalogNodeValidChk(NodeType.ACLSFloder)) {
                                    typeList.add(XClsType.XACls);
                                }
                                if (mwcFactory.catalogNodeValidChk(NodeType.OCLSFloder)) {
                                    typeList.add(XClsType.XOCls);
                                }
                                if (mwcFactory.catalogNodeValidChk(NodeType.NCLSFloder)) {
                                    typeList.add(XClsType.XGNet);
                                }
                                mwcFactory.freeFactory();
                            }

                            for (XClsType type : typeList) {
                                GDBCatalogPane.this.addClsTreeItems(db, type, this);
                            }
                        } else if (XClsType.XRcat.equals(clsType)) {
                            GDBCatalogPane.this.addClsTreeItems(db, XClsType.XRds, this);
                        }
                    }
                }
            }
            return super.getChildren();
        }

        @Override
        public boolean isLeaf() {
            boolean isLeaf = false;
            int level = GDBCatalogPane.this.treeView.getTreeItemLevel(this);
            Object tag = getValue().getTag();
            if (level == 1) {
                isLeaf = tag == null || (tag instanceof Server && !((Server) tag).hasConnected());
            } else if (level == 2) {
                isLeaf = tag == null || (tag instanceof DataBase && !((DataBase) tag).hasOpened());
            } else if (level >= 3) {
                XClsType clsType = (XClsType) tag;
                isLeaf = !XClsType.XFds.equals(clsType) && !XClsType.XRcat.equals(clsType);
            }
            return isLeaf;
        }

        public boolean hasGettedChildren() {
            return !this.notInitialized;
        }

        public void reset() {
            this.notInitialized = true;
        }
    }

    /**
     * 自定义树节点单元格
     */
    private final class CatalogTreeCell extends TreeCell<TreeItemObject> {
        private TextField textField;
        private Tooltip tooltipError = new Tooltip();//错误提示

        public CatalogTreeCell() {
            this.initServerMenu();
            this.initDatabaseMenu();
            this.initFdsMenu();
            this.initSFClsMenu();
            this.initAClsMenu();
            this.initOClsMenu();
            this.initNClsMenu();
            this.initRasCatMenu();
            this.initRasMenu();
            this.initMosaicMenu();
        }

        //region 重写
        @Override
        public void startEdit() {
            super.startEdit();
            if (this.textField == null) {
                this.textField = new TextField();
                this.textField.setOnKeyReleased(event ->
                {
                    if (event.getCode() == KeyCode.ENTER) {
                        TreeItemObject tiObj = getItem();
                        tiObj.setText(CatalogTreeCell.this.textField.getText());
                        commitEdit(tiObj);
                    } else if (event.getCode() == KeyCode.ESCAPE) {
                        cancelEdit();
                    }
                });
                this.textField.textProperty().addListener((observable, oldValue, newValue) ->
                {
                    if (!XString.isNullOrEmpty(newValue)) {
                        DataBase db = getOwnerDataBase(getTreeItem());
                        char[] invalidChars = db.getServer().getInvalidChars(3);//3-类名称
                        List<Character> invalidCharList = new ArrayList<>();
                        if (invalidChars != null && invalidChars.length > 0) {
                            for (char ch : invalidChars) {
                                invalidCharList.add(ch);
                            }
                        }
                        StringProperty errorMsg = new SimpleStringProperty();
                        if (!XString.isTextValid(newValue, 128, invalidCharList, errorMsg)) {
                            this.textField.setText(oldValue);
                            UIFunctions.showErrorTip(this.textField, errorMsg.get(), this.tooltipError);
                        }
                    }
                });
            }

            super.setText(null);
            super.setGraphic(this.textField);
            this.textField.requestFocus();
            this.textField.setText(getString());
            this.textField.selectAll();
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            super.setText(getItem().getText());
            super.setGraphic(getTreeItem().getGraphic());

            GDBCatalogPane.this.treeView.setEditable(false);
        }

        @Override
        public void commitEdit(TreeItemObject newValue) {
            super.commitEdit(newValue);
            updateItem(newValue, false);
            GDBCatalogPane.this.treeView.setEditable(false);//需在此处设置。setOnEditCommit会在super.commitEdit中间触发，若在事件中设置会触发后面的代码判断editable为false，从而导致后续功能有问题。
        }

        private String getString() {
            return getItem() == null ? "" : getItem().getText();
        }

        @Override
        public void updateItem(TreeItemObject item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                setText(item.getText());
                setGraphic(getTreeItem().getGraphic());

                int level = treeView.getTreeItemLevel(getTreeItem());
                if (level == 1) {
                    this.showServerMenu();
                } else if (level == 2) {
                    this.showDatabaseMenu();
                } else {
                    Object tag = getItem().getTag();
                    if (tag instanceof XClsType) {
                        XClsType clsType = (XClsType) tag;
                        if (XClsType.XFds.equals(clsType)) {
                            this.showFdsMenu();
                        } else if (XClsType.XSFCls.equals(clsType)) {
                            this.showSFClsMenu();
                        } else if (XClsType.XACls.equals(clsType)) {
                            this.showAClsMenu();
                        } else if (XClsType.XOCls.equals(clsType)) {
                            this.showOClsMenu();
                        } else if (XClsType.XGNet.equals(clsType)) {
                            this.showNClsMenu();
                        } else if (XClsType.XRcat.equals(clsType)) {
                            this.showRasCatMenu();
                        } else if (XClsType.XRds.equals(clsType)) {
                            this.showRasMenu();
                        } else if (XClsType.XMosaicDS.equals(clsType)) {
                            this.showMosaicMenu();
                        }
                    }
                }
            }
        }
        //endregion

        //region 数据源右键菜单
        private ContextMenu serverMenu = new ContextMenu();
        private MenuItem miServerConnect = new MenuItem("连接", new ImageView(menuImageList.get(7)));
        private MenuItem miServerDisConnect = new MenuItem("断开连接", new ImageView(menuImageList.get(8)));
        private MenuItem miServerRefresh = new MenuItem("刷新", new ImageView(menuImageList.get(0)));
        private MenuItem miServerCreateDB = new MenuItem("创建数据库");
        private MenuItem miServerAttachDB = new MenuItem("附加数据库");
        private MenuItem miServerAttachDBs = new MenuItem("批量附加数据库");
        private MenuItem miServerModify = new MenuItem("修改");
        private MenuItem miServerDelete = new MenuItem("移除", new ImageView(menuImageList.get(1)));
        private MenuItem miServerProperty = new MenuItem("属性", new ImageView(menuImageList.get(2)));
        private MenuItem miServerCopyURL = new MenuItem("复制URL", new ImageView(menuImageList.get(6)));

        private void initServerMenu() {
            this.serverMenu.getItems().addAll(this.miServerConnect, this.miServerDisConnect, this.miServerRefresh, new SeparatorMenuItem(), this.miServerCreateDB, this.miServerAttachDB, this.miServerAttachDBs, new SeparatorMenuItem(), this.miServerModify, this.miServerDelete, new SeparatorMenuItem(), this.miServerProperty, this.miServerCopyURL);
            //连接
            this.miServerConnect.setOnAction(event ->
            {
                GDBCatalogPane.this.connectDataSource((CatalogTreeItem) getTreeItem());
            });
            //断开连接
            this.miServerDisConnect.setOnAction(event ->
            {
                Server ds = (Server) getItem().getTag();
                if (ds != null && ds.hasConnected()) {
                    for (TreeItem<TreeItemObject> dbItem : getTreeItem().getChildren()) {
                        DataBase db = getOwnerDataBase(dbItem);
                        if (db != null && db.hasOpened()) {
                            db.close();
                        }
                    }

                    String user = (ds.getLogin() != null && ds.getLogin().length >= 2) ? ds.getLogin()[0] : null;
                    if (!XString.isNullOrEmpty(user)) {
                        Server.deleteLogInfo(getItem().getText(), ds.getLogin()[0]);
                    }
                    if (ds.disConnect() > 0) {
                        getItem().setTag(new Server());
                        System.out.println("Clear前：" + getTreeItem().getChildren().size());
                        getTreeItem().getChildren().clear();
                        getTreeItem().setExpanded(false);
                        System.out.println("Clear之后： " + getTreeItem().isExpanded());
                        System.out.println("Clear后：" + getTreeItem().getChildren().size());
                        getTreeItem().setGraphic(new ImageView(treeImageList.get(1)));
                    } else {
                        MessageBox.information("断开失败。");
                        Server.appendLogInfo(getItem().getText(), ds.getLogin()[0], ds.getLogin()[1]);
                        this.refreshTreeItem();
                    }
                }
            });
            //刷新
            this.miServerRefresh.setOnAction(event ->
            {
                this.refreshTreeItem();
            });
            //创建数据库
            this.miServerCreateDB.setOnAction(event ->
            {
                Server ds = (Server) getItem().getTag();
                if (ds != null && ds.hasConnected()) {
                    ConnectType conType = ds.getConnectType();
                    switch (conType) {
                        case Local:
                        case LocalPlus:
                        case DBSQL:
                        case DBOracle:
                        case DBMySQL:
                        case DBDm:
                        case DBDB2:
                        case DBGBase:
                        case DBBeyon:
                        case DBSybase:
                        case DBPG: {
                            List<Integer> oldList = ds.getGdbs() != null ? Arrays.stream(ds.getGdbs()).boxed().collect(Collectors.toList()) : null;
                            CreateGDBDialog dlg = new CreateGDBDialog(ds);
                            if (Optional.of(ButtonType.OK).equals(dlg.showAndWait())) {
                                int[] newIds = ds.getGdbs();
                                if (newIds != null) {
                                    for (int dbID : newIds) {
                                        if (oldList == null || !oldList.contains(dbID)) {
                                            CatalogTreeItem dbItem = GDBCatalogPane.this.addDataBaseTreeItem(ds, dbID, (CatalogTreeItem) getTreeItem());
                                            if (dbItem != null) {
                                                GDBCatalogPane.this.treeView.getSelectionModel().select(dbItem);
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        }
                        case Custom: {
                            List<Integer> oldList = ds.getGdbs() != null ? Arrays.stream(ds.getGdbs()).boxed().collect(Collectors.toList()) : null;
                            DataSrcInfo svcInfo = SvcConfig.get(ds.getSvrName());
                            String dnsName = svcInfo.getDnsName();
                            int nwIndex = dnsName.indexOf('&');
                            if (nwIndex > 0) {
                                String mwName = dnsName.substring(0, dnsName.indexOf('&'));
                                MiddleWareConfigFactory middlefig = new MiddleWareConfigFactory(mwName);
                                Object[] rtn = middlefig.createGDB();
                                if (rtn != null && rtn.length == 2 && (boolean) rtn[0]) {
                                    if (ds.attachDB((String) rtn[1], null, null) > 0) {
                                        int[] newIds = ds.getGdbs();
                                        if (newIds != null) {
                                            for (int dbID : newIds) {
                                                if (oldList == null || !oldList.contains(dbID)) {
                                                    CatalogTreeItem dbItem = GDBCatalogPane.this.addDataBaseTreeItem(ds, dbID, (CatalogTreeItem) getTreeItem());
                                                    if (dbItem != null) {
                                                        GDBCatalogPane.this.treeView.getSelectionModel().select(dbItem);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                middlefig.freeFactory();
                            }
                            break;
                        }
                    }
                }
            });
            //附加数据库
            this.miServerAttachDB.setOnAction(event ->
            {
                Server ds = (Server) getItem().getTag();
                if (ds != null && ds.hasConnected()) {
                    long attachID = -1;
                    switch (ds.getConnectType()) {
                        case Local:
                        case LocalPlus:
                        case DBSQL:
                        case DBOracle:
                        case DBMySQL:
                        case DBDm:
                        case DBPG://Postgre数据源
                        {
                            AttachGDBDialog dlg = new AttachGDBDialog(ds);
                            if (Optional.of(ButtonType.OK).equals(dlg.showAndWait())) {
                                attachID = dlg.getAttachID();
                            }
                            break;
                        }
                        case Custom: {
                            DataSrcInfo svcInfo = SvcConfig.get(ds.getSvrName());
                            String dnsName = svcInfo.getDnsName();
                            int nwIndex = dnsName.indexOf('&');
                            if (nwIndex > 0) {
                                String mwName = dnsName.substring(0, dnsName.indexOf('&'));
                                MiddleWareConfigFactory middlefig = new MiddleWareConfigFactory(mwName);
                                MiddleWareType type = middlefig.getMiddleWareType();
                                String strGDSN = "";
                                if (type == MiddleWareType.CROSS_FILEGDB || type == MiddleWareType.CROSS_MDB) {
                                    AttachGDBDialog dlg = new AttachGDBDialog(ds, type);
                                    if (Optional.of(ButtonType.OK).equals(dlg.showAndWait())) {
                                        attachID = dlg.getAttachID();
                                    }
                                } else {
                                    Object[] rtns = middlefig.attachGDB();
                                    strGDSN = (rtns != null && rtns.length == 2 && (boolean) rtns[0]) ? (String) rtns[1] : "";
                                    if (!XString.isNullOrEmpty(strGDSN)) {
                                        attachID = ds.attachDB(strGDSN, null, null);
                                        if (attachID <= 0) {
                                            MessageBox.information(AttachGDBDialog.getAttachError(attachID));
                                        }
                                    }
                                }
                                middlefig.freeFactory();
                            }
                            break;
                        }
                        default:
                            break;
                    }

                    if (attachID > 0) {
                        CatalogTreeItem dbItem = GDBCatalogPane.this.addDataBaseTreeItem(ds, (int) attachID, (CatalogTreeItem) getTreeItem());
                        if (dbItem != null) {
                            GDBCatalogPane.this.treeView.getSelectionModel().select(dbItem);
                            SRefManagerDialog.addCustomSrefs((DataBase) dbItem.getValue().getTag());
                        }
                    }
                }
            });
            //批量附加数据库
            this.miServerAttachDBs.setOnAction(event ->
            {
                Server ds = (Server) getItem().getTag();
                if (ds != null && ds.hasConnected()) {
                    FileChooser fileChooser = new FileChooser();
                    String ext = ds.getConnectType() == ConnectType.Local ? "*.hdf" : "*.hdb";
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(String.format("地理数据库(%s)", ext), ext));
                    List<File> files = fileChooser.showOpenMultipleDialog(getCurrentWindow());
                    if (files != null) {
                        String failedDB = null;
                        for (File file : files) {
                            String dbName = XPath.getNameWithoutExt(file);
                            long attachID = ds.attachDB(dbName, file.getPath(), "");
                            if (attachID > 0) {
                                GDBCatalogPane.this.addDataBaseTreeItem(ds, (int) attachID, (CatalogTreeItem) getTreeItem());
                            } else {
                                failedDB += "\n    " + dbName;
                            }
                        }
                        getTreeItem().setExpanded(true);
                        if (!XString.isNullOrEmpty(failedDB)) {
                            MessageBox.information("下列数据库附加失败:" + failedDB);
                        }
                    }
                }
            });
            //修改
            this.miServerModify.setOnAction(event ->
            {
                String oldName = getItem().getText();
                AddServerDialog dlg = new AddServerDialog(oldName, ConnectType.valueOf(getItem().getId()));
                dlg.initOwner(GDBCatalogPane.this.getCurrentWindow());
                if (dlg.showAndWait().equals(Optional.of(ButtonType.OK))) {
                    String dsName = dlg.getServerInfo().getSvcName();
                    if (!oldName.equals(dsName)) {
                        getItem().setText(dsName);
                        Server ds = GDBCatalogPane.this.tryConnectDataSource(dsName, null, null, false, false);//创建是在初始化或刷新时，连接失败不弹框错误消息
                        getItem().setTag(ds != null ? ds : new Server());
                        getTreeItem().setGraphic(new ImageView(GDBCatalogPane.this.treeImageList.get((ds != null && ds.hasConnected()) ? 0 : 1)));
                        treeView.refresh();
                    }
                }
            });
            //删除
            this.miServerDelete.setOnAction(event ->
            {
                if (ButtonType.OK == MessageBox.question(String.format("确定要删除数据源“%s”吗？", getItem().getText()))) {
                    Server server = (Server) getItem().getTag();
                    if (server != null && server.hasConnected()) {
                        for (TreeItem<TreeItemObject> dbItem : getTreeItem().getChildren()) {
                            DataBase db = getOwnerDataBase(dbItem);
                            if (db != null && db.hasOpened()) {
                                db.close();
                            }
                        }
                        server.disConnect();
                    }
                    for (int i = 0; i < SvcConfig.count(); i++) {
                        DataSrcInfo dsInfo = SvcConfig.get(i);
                        if (dsInfo != null && getItem().getText().equals(dsInfo.getSvcName())) {
                            if (SvcConfig.remove(i)) {
                                getTreeItem().getParent().getChildren().remove(getTreeItem());
                            } else {
                                MessageBox.information("删除数据源失败。");
                            }
                            break;
                        }
                    }
                }
            });
            //属性
            this.miServerProperty.setOnAction(event ->
            {
                GDBCatalogPane.this.viewProperty(getTreeItem());
            });
            //复制URL
            this.miServerCopyURL.setOnAction(event ->
            {
                this.copyURL();
            });
        }

        private void showServerMenu() {
            Server ds = (Server) getItem().getTag();
            boolean dsOK = ds != null && ds.hasConnected();
            ConnectType conType = dsOK ? ds.getConnectType() : SvcConfig.get(getItem().getText()).getSvcType();
            List<Integer> menuList = CommonFunctions.getCustomDataSourceMenuList(ds);

            MiddleWareType mwType = CommonFunctions.getCustomServerType(ds);
            boolean canAttach = !(mwType == MiddleWareType.ArcSDE || mwType == MiddleWareType.SDE);

            this.miServerConnect.setVisible(!dsOK);
            this.miServerDisConnect.setVisible(dsOK && !(ConnectType.Local.equals(conType)) && !(ConnectType.LocalPlus.equals(conType)));
            this.miServerRefresh.setVisible(dsOK && (menuList == null || menuList.contains(7)));
            this.miServerCreateDB.setVisible(dsOK && (menuList == null || menuList.contains(4)));
            this.miServerAttachDB.setVisible(dsOK && canAttach);
            this.miServerAttachDBs.setVisible(dsOK && (ConnectType.Local.equals(conType) || ConnectType.LocalPlus.equals(conType)));
            this.miServerModify.setVisible(!(ConnectType.Local.equals(conType) || ConnectType.LocalPlus.equals(conType)));
            this.miServerDelete.setVisible(!(ConnectType.Local.equals(conType) || ConnectType.LocalPlus.equals(conType)));
            this.miServerProperty.setVisible(dsOK && (menuList == null || menuList.contains(6)));
            this.miServerCopyURL.setVisible(dsOK);

            this.setSeparatorVisible(this.serverMenu);

            setContextMenu(this.serverMenu);
        }
        //endregion

        //region 数据库右键菜单
        private ContextMenu dataBaseMenu = new ContextMenu();
        private MenuItem miDBRefresh = new MenuItem("刷新", new ImageView(menuImageList.get(0)));
        private MenuItem miDBCopyURL = new MenuItem("复制URL", new ImageView(menuImageList.get(6)));
        private MenuItem miDBPaste = new MenuItem("粘贴", new ImageView(menuImageList.get(5)));
        private MenuItem miDBCreate = new MenuItem("创建");
        private MenuItem miDBImport = new MenuItem("导入");
        private MenuItem miDBExport = new MenuItem("导出");
        private MenuItem miDBDetach = new MenuItem("注销");
        private MenuItem miDBDelete = new MenuItem("删除", new ImageView(menuImageList.get(1)));
        private MenuItem miDBProperty = new MenuItem("属性", new ImageView(menuImageList.get(2)));

        private void initDatabaseMenu() {
            this.dataBaseMenu.getItems().addAll(this.miDBRefresh, new SeparatorMenuItem(), this.miDBCreate, this.miDBImport, this.miDBExport, new SeparatorMenuItem(), this.miDBDetach, this.miDBDelete, new SeparatorMenuItem(), this.miDBProperty, this.miDBCopyURL, new SeparatorMenuItem(), this.miDBPaste);
            //刷新
            this.miDBRefresh.setOnAction(event ->
            {
                this.refreshTreeItem();
            });
            //复制URL
            this.miDBCopyURL.setOnAction(event ->
            {
                this.copyURL();
            });
            //粘贴
            this.miDBPaste.setOnAction(event ->
            {
            });
            //创建
            this.miDBCreate.setOnAction(event ->
            {
                DataBase db = getOwnerDataBase(getTreeItem());
                CreateDataDialog dlg = new CreateDataDialog(db);
                dlg.initOwner(GDBCatalogPane.this.getCurrentWindow());
                if (dlg.showAndWait().equals(Optional.of(ButtonType.OK))) {
                    int clsID = dlg.getClsID();
                    if (clsID > 0) {
                        int dsID = dlg.getDsID();
                        XClsType clsType = dlg.getClsType();
                        TreeItem<TreeItemObject> parTreeItem = getTreeItem();
                        if (dsID > 0) {
                            if (clsType.equals(XClsType.XRds)) {
                                parTreeItem = GDBCatalogPane.this.findTreeItemByName(parTreeItem, db.getXclsName(XClsType.XRcat, dsID), XClsType.XRcat);
                            } else {
                                parTreeItem = GDBCatalogPane.this.findTreeItemByName(parTreeItem, db.getXclsName(XClsType.XFds, dsID), XClsType.XFds);
                            }
                        }

                        CatalogTreeItem clsItem = null;
                        if (parTreeItem.isExpanded()) {
                            clsItem = GDBCatalogPane.this.insertClsTreeItem(db, clsType, clsID, null, this.getTreeItem());
                        } else {
                            clsItem = GDBCatalogPane.this.findTreeItemByName(parTreeItem, db.getXclsName(clsType, clsID), clsType);
                        }
                        if (clsItem != null) {
                            GDBCatalogPane.this.treeView.getSelectionModel().select(clsItem);
                        }
                    }
                }
            });
            //导入
            this.miDBImport.setOnAction(event ->
            {
                DataConverts.convert(getCurrentWindow(), "", getItem().getUrl());
            });
            //导出
            this.miDBExport.setOnAction(event ->
            {
                DataConverts.convert(getCurrentWindow(), getItem().getUrl());
            });
            //注销
            this.miDBDetach.setOnAction(event ->
            {
                if (ButtonType.OK == MessageBox.question("该数据库可能包含有用的数据,确定要注销吗?")) {
                    boolean isClosed = false;
                    DataBase db = getOwnerDataBase(getTreeItem());
                    if (db != null && db.hasOpened()) {
                        db.close();
                        isClosed = true;
                    }

                    String dbName = getItem().getText();
                    Server ds = (Server) getTreeItem().getParent().getValue().getTag();
                    boolean detached = ConnectType.Custom.equals(ds.getConnectType()) ? ds.deleteGDB(dbName) : ds.detachGDB(dbName);
                    if (detached) {
                        getTreeItem().getParent().getChildren().remove(getTreeItem());
                    } else {
                        MessageBox.information(String.format("地理数据库“%s”注销失败。", dbName));
                        if (isClosed) {
                            DataBase db1 = ds.openGDB(dbName);
                            getItem().setTag(db1 != null ? db1 : new DataBase());
                        }
                    }
                }
            });
            //删除
            this.miDBDelete.setOnAction(event ->
            {
                CatalogTreeItem treeItem = (CatalogTreeItem) getTreeItem();
                String dbName = getItem().getText();
                DataBase db = getOwnerDataBase(treeItem);
                Server ds = (db != null && db.hasOpened()) ? db.getServer() : (Server) treeItem.getParent().getValue().getTag();
                switch (ds.getConnectType()) {
                    case DBSQL:
                    case DBOracle:
                    case DBDm:
                    case DBMySQL:
                    case DBPG://Postgre数据源
                    case DBKBS: {
                        //这类删除需要写日志，不过底层暂未封装
                        String masterDB = CommonFunctions.getMasterDBName(ds);
                        if (dbName.equals(masterDB)) {
                            if (ButtonType.OK == MessageBox.question(String.format("“%s”为数据源主数据库，删除它将同时删除数据源下所有数据库且无法恢复，确定要继续删除吗？", dbName))) {
                                int[] dbIDs = ds.getGdbs();
                                if (dbIDs != null && dbIDs.length > 0) {
                                    CatalogTreeItem dsItem = (CatalogTreeItem) treeItem.getParent();
                                    boolean allDel = true;
                                    for (int dbID : dbIDs) {
                                        String tempName = ds.getDBName(dbID);
                                        if (!tempName.equals(dbName))//主数据库最后删
                                        {
                                            CatalogTreeItem tempItem = (CatalogTreeItem) GDBCatalogPane.this.findTreeItemByName(dsItem, tempName);
                                            DataBase tempDB = getOwnerDataBase(tempItem);
                                            if (tempDB != null && tempDB.hasOpened()) {
                                                tempDB.close();
                                            }

                                            if (!ds.deleteGDB(tempName)) {
                                                allDel = false;
                                                break;
                                            }
                                        }
                                    }

                                    if (allDel) {
                                        ds.deleteGDB(dbName);
                                    }
                                    this.refreshTreeItem(dsItem, true);
                                }
                            }
                        } else {
                            if (ButtonType.OK == MessageBox.question("地理数据库一旦被删除，将无法恢复，确定要继续删除吗？")) {
                                boolean isClosed = false;
                                if (db != null && db.hasOpened()) {
                                    db.close();
                                    isClosed = true;
                                }

                                if (ds.deleteGDB(dbName)) {
                                    treeItem.getParent().getChildren().remove(treeItem);
                                } else {
                                    MessageBox.information(String.format("地理数据库“%s”删除失败。可能其中的数据正在被使用。", dbName));
                                    if (isClosed) {
                                        DataBase db1 = ds.openGDB(dbName);
                                        getItem().setTag(db1 != null ? db1 : new DataBase());
                                    }
                                }
                            }
                        }
                        break;
                    }
                    default: {
                        String tipText = "";
                        if (ConnectType.Custom.equals(ds.getConnectType())) {
                            tipText = "该数据库对应的配置文件将被删除,确定要继续吗?";
                        } else {
                            tipText = String.format("该数据库及其对应的%s文件将被永久删除,确定要继续吗?", ConnectType.LocalPlus.equals(ds.getConnectType()) ? "HDB" : "HDF");
                        }

                        if (ButtonType.OK == MessageBox.question(tipText)) {
                            boolean isClosed = false;
                            if (db != null && db.hasOpened()) {
                                db.close();
                                isClosed = true;
                            }

                            if (ds.deleteGDB(dbName)) {
                                treeItem.getParent().getChildren().remove(treeItem);
                            } else {
                                MessageBox.information(String.format("地理数据库“%s”删除失败。可能其中的数据正在被使用。", dbName));
                                if (isClosed) {
                                    DataBase db1 = ds.openGDB(dbName);
                                    getItem().setTag(db1 != null ? db1 : new DataBase());
                                }
                            }
                        }
                        break;
                    }
                }
            });
            //属性
            this.miDBProperty.setOnAction(event ->
            {
                GDBCatalogPane.this.viewProperty(getTreeItem());
            });
        }

        private void showDatabaseMenu() {
            CatalogTreeItem treeItem = (CatalogTreeItem) getTreeItem();
            Server server = (Server) treeItem.getParent().getValue().getTag();
            ConnectType conType = server.getConnectType();
            DataBase db = GDBCatalogPane.this.getTreeItemDataBase(treeItem, true);
            boolean dbOK = db != null && db.hasOpened();

            this.miDBRefresh.setVisible(dbOK);
            this.miDBCopyURL.setVisible(dbOK);
            this.miDBProperty.setVisible(dbOK);
            this.miDBCreate.setVisible(dbOK && CommonFunctions.canAddData(server));
            this.miDBPaste.setVisible(dbOK && CommonFunctions.canAddData(server) && CommonFunctions.clipboardContains(XClsType.XFds, XClsType.XSFCls, XClsType.XACls, XClsType.XOCls, XClsType.XRcat, XClsType.XRds));
            this.miDBImport.setVisible(dbOK && CommonFunctions.canImportExport(server) && CommonFunctions.hasLoadedPluginFunction(GDBCatalogPane.this.app, CommonFunctions.imExportFunctionKey));
            this.miDBExport.setVisible(dbOK && CommonFunctions.canImportExport(server) && CommonFunctions.hasLoadedPluginFunction(GDBCatalogPane.this.app, CommonFunctions.imExportFunctionKey));
            this.miDBDetach.setVisible((ConnectType.Local.equals(conType) || ConnectType.LocalPlus.equals(conType) || ConnectType.Custom.equals(conType)));
            this.miDBDelete.setVisible(!ConnectType.Custom.equals(conType));

            //1.0 暂时隐藏
            this.miDBPaste.setVisible(false);

            this.setSeparatorVisible(this.dataBaseMenu);

            setContextMenu(this.dataBaseMenu);
        }
        //endregion

        //region 要素数据集右键菜单
        private ContextMenu fdsMenu = new ContextMenu();       //要素数据集节点
        private MenuItem miFdsRefresh = new MenuItem("刷新", new ImageView(menuImageList.get(0)));
        private MenuItem miFdsCopyURL = new MenuItem("复制URL", new ImageView(menuImageList.get(6)));
        private MenuItem miFdsCopy = new MenuItem("复制", new ImageView(menuImageList.get(4)));
        private MenuItem miFdsPaste = new MenuItem("粘贴", new ImageView(menuImageList.get(5)));
        private MenuItem miFdsCreate = new MenuItem("创建");
        private MenuItem miFdsCreateNCls = new MenuItem("创建网络类");
        private MenuItem miFdsImport = new MenuItem("导入");
        private MenuItem miFdsExport = new MenuItem("导出");
        private MenuItem miFdsDelete = new MenuItem("删除", new ImageView(menuImageList.get(1)));
        private MenuItem miFdsRename = new MenuItem("重命名");
        private MenuItem miFdsProperty = new MenuItem("属性", new ImageView(menuImageList.get(2)));

        private void initFdsMenu() {
            this.fdsMenu.getItems().addAll(this.miFdsRefresh, new SeparatorMenuItem(), this.miFdsCreate, this.miFdsCreateNCls, this.miFdsImport, this.miFdsExport, new SeparatorMenuItem(), this.miFdsRename, this.miFdsDelete, new SeparatorMenuItem(), this.miFdsProperty, this.miFdsCopyURL, this.miFdsCopy, this.miFdsPaste);

            //刷新
            this.miFdsRefresh.setOnAction(event ->
            {
                this.refreshTreeItem();
            });
            //复制URL
            this.miFdsCopyURL.setOnAction(event ->
            {
                this.copyURL();
            });
            //复制
            this.miFdsCopy.setOnAction(event ->
            {
                this.copy();
            });
            //粘贴
            this.miFdsPaste.setOnAction(event ->
            {
            });
            //创建
            this.miFdsCreate.setOnAction(event ->
            {
                CatalogTreeItem treeItem = (CatalogTreeItem) getTreeItem();
                DataBase db = getOwnerDataBase(treeItem);
                CreateDataDialog dlg = new CreateDataDialog(db, getItem().getId());
                dlg.initOwner(GDBCatalogPane.this.getCurrentWindow());
                if (dlg.showAndWait().equals(Optional.of(ButtonType.OK))) {
                    int clsID = dlg.getClsID();
                    if (clsID > 0) {
                        int dsID = dlg.getDsID();
                        XClsType clsType = dlg.getClsType();
                        CatalogTreeItem parTreeItem = treeItem;
                        if (dsID <= 0) {
                            parTreeItem = (CatalogTreeItem) parTreeItem.getParent();
                        } else if (dsID != getItem().getId()) {
                            if (clsType.equals(dlg.getClsType())) {
                                parTreeItem = GDBCatalogPane.this.findTreeItemByName(parTreeItem, db.getXclsName(XClsType.XRcat, dsID), XClsType.XRcat);
                            } else {
                                parTreeItem = GDBCatalogPane.this.findTreeItemByName(parTreeItem, db.getXclsName(XClsType.XFds, dsID), XClsType.XFds);
                            }
                        }

                        CatalogTreeItem clsItem = null;
                        if (parTreeItem.isExpanded()) {
                            clsItem = GDBCatalogPane.this.insertClsTreeItem(db, clsType, clsID, null, this.getTreeItem());
                        } else {
                            clsItem = GDBCatalogPane.this.findTreeItemByName(parTreeItem, db.getXclsName(clsType, clsID), clsType);
                        }
                        if (clsItem != null) {
                            GDBCatalogPane.this.treeView.getSelectionModel().select(clsItem);
                        }
                    }
                }
            });
            //创建网络类
            this.miFdsCreateNCls.setOnAction(event ->
            {
                DataBase db = getOwnerDataBase(getTreeItem());
                int dsID = getItem().getId();
                int[] lins = db.getXclses(XClsType.XSFCls, dsID);
                boolean hasLin = false;
                if (lins != null && lins.length > 0) {
                    for (int id : lins) {
                        SFClsInfo sfClsInfo = (SFClsInfo) db.getXclsInfo(XClsType.XSFCls, id);
                        if (sfClsInfo.getfType().equals(GeomType.GeomLin) && sfClsInfo.getgNetID() <= 0) {
                            hasLin = true;
                            break;
                        }
                    }
                }

                if (!hasLin) {
                    MessageBox.information("当前要素数据集下无可参与建网的线简单要素类。");
                } else {
                    CreateNetclsDialog dlg = new CreateNetclsDialog(getCurrentWindow(), db, dsID);
                    if (Optional.of(ButtonType.FINISH) == dlg.showAndWait()) {
                        int clsID = dlg.getClsID();
                        if (clsID > 0) {
                            CatalogTreeItem treeItemCls = insertClsTreeItem(db, XClsType.XGNet, clsID, null, (CatalogTreeItem) getTreeItem());
                            if (treeItemCls != null) {
                                treeView.getSelectionModel().select(treeItemCls);
                            }
                        }
                    }
                }
            });
            //导入
            this.miFdsImport.setOnAction(event ->
            {
                DataConverts.convert(getCurrentWindow(), "", getItem().getUrl());
            });
            //导出
            this.miFdsExport.setOnAction(event ->
            {
                DataConverts.convert(getCurrentWindow(), getItem().getUrl());
            });
            //删除
            this.miFdsDelete.setOnAction(event ->
            {
                if (ButtonType.OK == MessageBox.question("该数据集下可能有类数据, 确定要删除吗?")) {
                    this.closePropertyData();

                    CatalogTreeItem treeItem = (CatalogTreeItem) getTreeItem();
                    DataBase db = getOwnerDataBase(treeItem);

                    int dsID = getItem().getId();
                    XClsType failedClsType = XClsType.Unknown;
                    String failedClsName = "";

                    //依次删除要素数据集下面的各类数据(先删网络类，否则参与建网的简单要素类会删除失败)
                    //网络类
                    int[] clsList = db.getXclses(XClsType.XGNet, dsID);
                    if (clsList != null) {
                        for (int clsID : clsList) {
                            if (!NetCls.remove(db, clsID)) {
                                failedClsType = XClsType.XGNet;
                                failedClsName = db.getXclsName(XClsType.XGNet, clsID);
                                break;
                            }
                        }
                    }
                    //简单要素类
                    if (!failedClsName.equals(XClsType.Unknown)) {
                        clsList = db.getXclses(XClsType.XSFCls, dsID);
                        if (clsList != null) {
                            for (int clsID : clsList) {
                                if (!SFeatureCls.remove(db, clsID)) {
                                    failedClsType = XClsType.XSFCls;
                                    failedClsName = db.getXclsName(XClsType.XSFCls, clsID);
                                    break;
                                }
                            }
                        }
                    }
                    //注记类
                    if (!failedClsName.equals(XClsType.Unknown)) {
                        clsList = db.getXclses(XClsType.XACls, dsID);
                        if (clsList != null) {
                            for (int clsID : clsList) {
                                if (!AnnotationCls.remove(db, clsID)) {
                                    failedClsType = XClsType.XACls;
                                    failedClsName = db.getXclsName(XClsType.XACls, clsID);
                                    break;
                                }
                            }
                        }
                    }
                    //对象类
                    if (!failedClsName.equals(XClsType.Unknown)) {
                        clsList = db.getXclses(XClsType.XOCls, dsID);
                        if (clsList != null) {
                            for (int clsID : clsList) {
                                if (!ObjectCls.remove(db, clsID)) {
                                    failedClsType = XClsType.XOCls;
                                    failedClsName = db.getXclsName(XClsType.XOCls, clsID);
                                    break;
                                }
                            }
                        }
                    }

                    if (!failedClsName.equals(XClsType.Unknown)) {
                        if (db.removeFds(dsID) > 0) {
                            treeItem.getParent().getChildren().remove(treeItem);
                        } else {
                            MessageBox.information("删除失败。");
                        }
                    } else {
                        MessageBox.information(String.format("要素数据集中的&s“%s”删除失败。", LanguageConvert.xClsTypeConvert(failedClsType), failedClsName));
                        this.refreshTreeItem();
                    }
                }
            });
            //重命名
            this.miFdsRename.setOnAction(event ->
            {
                GDBCatalogPane.this.treeView.setEditable(true);
                GDBCatalogPane.this.treeView.edit(getTreeItem());
                GDBCatalogPane.this.treeView.setOnEditCommit(event1 ->
                {
                    DataBase db = getOwnerDataBase(getTreeItem());
                    int id = getItem().getId();
                    String newName = event1.getNewValue().getText();
                    db.updateDsName(id, newName);
                    newName = db.getXclsName(XClsType.XFds, id);
                    if (!newName.equals(event1.getNewValue().getText())) {
                        getTreeItem().setValue(event1.getOldValue());
                        MessageBox.information("重命名失败,该数据库下可能已经存在同名数据。");
                    } else {
                        GDBCatalogPane.this.viewProperty(getTreeItem(), false);
                        CatalogTreeCell.this.refreshTreeItem();
                    }
                });
            });
            //属性
            this.miFdsProperty.setOnAction(event ->
            {
                GDBCatalogPane.this.viewProperty(getTreeItem());
            });
        }

        private void showFdsMenu() {
            CatalogTreeItem treeItem = (CatalogTreeItem) getTreeItem();
            DataBase db = getOwnerDataBase(treeItem);
            Server server = db.getServer();

            this.miFdsCreate.setVisible(CommonFunctions.canAddData(server));
            this.miFdsPaste.setVisible(CommonFunctions.canAddData(server) && CommonFunctions.clipboardContains(XClsType.XSFCls, XClsType.XACls, XClsType.XOCls, XClsType.XGNet));
            this.miFdsImport.setVisible(CommonFunctions.canImportExport(server) && CommonFunctions.hasLoadedPluginFunction(GDBCatalogPane.this.app, CommonFunctions.imExportFunctionKey));
            this.miFdsExport.setVisible(CommonFunctions.canImportExport(server) && CommonFunctions.hasLoadedPluginFunction(GDBCatalogPane.this.app, CommonFunctions.imExportFunctionKey));

            //1.0 暂时隐藏
            this.miFdsCreateNCls.setVisible(false);//据说只有local和oracle测试过。
            this.miFdsCopy.setVisible(false);
            this.miFdsPaste.setVisible(false);

            this.setSeparatorVisible(this.fdsMenu);

            setContextMenu(this.fdsMenu);
        }
        //endregion

        //region 简单要素类右键菜单
        private ContextMenu sfClsMenu = new ContextMenu();
        private MenuItem miSfclsCopyURL = new MenuItem("复制URL", new ImageView(menuImageList.get(6)));
        private MenuItem miSfclsProperty = new MenuItem("属性", new ImageView(menuImageList.get(2)));
        private MenuItem miSfclsCopy = new MenuItem("复制", new ImageView(menuImageList.get(4)));
        private MenuItem miSfclsExport = new MenuItem("导出");
        private MenuItem miSfclsStatistics = new MenuItem("属性统计");
        private MenuItem miSfclsIndexManage = new MenuItem("索引管理");
        private MenuItem miSfclsClear = new MenuItem("清空");
        private MenuItem miSfclsDelete = new MenuItem("删除", new ImageView(menuImageList.get(1)));
        private MenuItem miSfclsRename = new MenuItem("重命名");
        private MenuItem miSfclsMoveToDS = new MenuItem("移动到数据集");

        private void initSFClsMenu() {
            this.sfClsMenu.getItems().addAll(new SeparatorMenuItem(), this.miSfclsExport, this.miSfclsMoveToDS, new SeparatorMenuItem(), this.miSfclsStatistics, new SeparatorMenuItem(), this.miSfclsIndexManage, new SeparatorMenuItem(), this.miSfclsRename, this.miSfclsClear, this.miSfclsDelete, new SeparatorMenuItem(), this.miSfclsProperty, this.miSfclsCopyURL, this.miSfclsCopy);

            //复制URL
            this.miSfclsCopyURL.setOnAction(event ->
            {
                this.copyURL();
            });
            //属性
            this.miSfclsProperty.setOnAction(event ->
            {
                GDBCatalogPane.this.viewProperty(getTreeItem());
            });
            //复制
            this.miSfclsCopy.setOnAction(event ->
            {
                this.copy();
            });
            //导出
            this.miSfclsExport.setOnAction(event ->
            {
                DataConverts.convert(getCurrentWindow(), getItem().getUrl());
            });
            //属性统计
            this.miSfclsStatistics.setOnAction(event ->
            {
            });
            //索引管理
            this.miSfclsIndexManage.setOnAction(event ->
            {
                DataBase db = getOwnerDataBase(getTreeItem());
                if (db != null) {
                    SFeatureCls cls = new SFeatureCls(db);
                    if (cls.open(getItem().getId(), 0) <= 0) {
                        MessageBox.information("数据打开失败,无法进行索引管理。");
                    } else {
                        cls.close();
                        IndexManagerDialog dlg = new IndexManagerDialog(db, XClsType.XSFCls, getItem().getId());
                        dlg.initOwner(GDBCatalogPane.this.getCurrentWindow());
                        dlg.show();
                    }
                }
            });
            //清空
            this.miSfclsClear.setOnAction(event ->
            {
                if (ButtonType.OK == MessageBox.question("确定要清空该简单要素类中的所有信息吗?")) {
                    DataBase db = getOwnerDataBase(getTreeItem());
                    if (db != null) {
                        SFeatureCls cls = new SFeatureCls(db);
                        if (cls.open(getItem().getId(), 0) <= 0) {
                            MessageBox.information("数据打开失败, 无法进行清空操作。");
                        } else {
                            if (cls.clear() <= 0) {
                                MessageBox.information("清空失败，可能数据正被其他程序占用。");
                            }
                            cls.close();
                            GDBCatalogPane.this.viewProperty(getTreeItem(), false);
                        }
                    }
                }
            });
            //删除
            this.miSfclsDelete.setOnAction(event ->
            {
                if (ButtonType.OK == MessageBox.question("该简单要素类可能包含有用的要素, 确定要删除吗?")) {
                    this.closePropertyData();

                    DataBase db = getOwnerDataBase(getTreeItem());
                    if (db != null) {
                        if (SFeatureCls.remove(db, getItem().getId())) {
                            getTreeItem().getParent().getChildren().remove(getTreeItem());
                        } else {
                            MessageBox.information("删除失败。");
                        }
                    }
                }
            });
            //重命名
            this.miSfclsRename.setOnAction(event ->
            {
                DataBase db = getOwnerDataBase(getTreeItem());
                if (db != null) {
                    String oldName = getItem().getText();
                    GDBCatalogPane.this.treeView.setEditable(true);
                    GDBCatalogPane.this.treeView.edit(getTreeItem());
                    GDBCatalogPane.this.treeView.setOnEditCommit(event1 ->
                    {
                        String newName = event1.getNewValue().getText();
                        if (newName != oldName) {
                            SFeatureCls cls = new SFeatureCls(db);
                            int id = getItem().getId();
                            if (cls.open(id, 0) <= 0) {
                                MessageBox.information("数据打开失败, 无法进行重命名。");
                            } else {
                                cls.setName(newName);
                                newName = db.getXclsName(XClsType.XSFCls, id);
                                if (!newName.equals(event1.getNewValue().getText())) {
                                    getTreeItem().setValue(event1.getOldValue());
                                    MessageBox.information("重命名失败,该数据库下可能已经存在同名数据。");
                                } else {
                                    GDBCatalogPane.this.viewProperty(getTreeItem(), false);
                                    CatalogTreeCell.this.refreshTreeItem();
                                }
                                cls.close();
                            }
                        }
                    });
                }
            });
            //移动到数据集
            this.miSfclsMoveToDS.setOnAction(event ->
            {
                DataBase db = getOwnerDataBase(getTreeItem());
                TreeItem<TreeItemObject> parItem = this.getTreeItem().getParent();
                TreeItem<TreeItemObject> dbItem = GDBCatalogPane.this.treeView.getTreeItemLevel(this.getTreeItem()) == 3 ? parItem : parItem.getParent();
                int sourDS = GDBCatalogPane.this.treeView.getTreeItemLevel(getTreeItem()) == 3 ? 0 : getTreeItem().getParent().getValue().getId();
                int clsID = this.getItem().getId();
                MoveToFdsDialog dlg = new MoveToFdsDialog(db, sourDS, XClsType.XSFCls, getItem().getId());
                dlg.initOwner(GDBCatalogPane.this.getCurrentWindow());
                if (dlg.showAndWait().equals(Optional.of(ButtonType.OK))) {
                    parItem.getChildren().remove(this.getTreeItem());
                    String destDs = db.getXclsName(XClsType.XFds, dlg.getDestDs());
                    if (!XString.isNullOrEmpty(destDs)) {
                        CatalogTreeItem dsItem = GDBCatalogPane.this.findTreeItemByName(dbItem, destDs, XClsType.XFds);
                        if (dsItem.hasGettedChildren()) {
                            this.refreshTreeItem(dsItem, false);
                        }
                    }
                }
            });
        }

        private void showSFClsMenu() {
            CatalogTreeItem treeItem = (CatalogTreeItem) getTreeItem();
            DataBase db = getOwnerDataBase(getTreeItem());
            Server server = db.getServer();
            ConnectType conType = server.getConnectType();
            SFClsInfo clsInfo = (SFClsInfo) db.getXclsInfo(XClsType.XSFCls, getItem().getId());

            int[] dsIDs = db.getXclses(XClsType.XFds, 0);
            int sourDS = GDBCatalogPane.this.treeView.getTreeItemLevel(getTreeItem()) == 3 ? 0 : getTreeItem().getParent().getValue().getId();
            this.miSfclsMoveToDS.setDisable(!(dsIDs != null && (dsIDs.length > 1 || (sourDS == 0 && dsIDs.length > 0)) && clsInfo != null && clsInfo.getgNetID() <= 0));
            this.miSfclsStatistics.setVisible(CommonFunctions.hasLoadedPluginFunction(GDBCatalogPane.this.app, CommonFunctions.attStatisticsFunctionKey));
            this.miSfclsIndexManage.setVisible(!ConnectType.Custom.equals(conType));
            this.miSfclsExport.setVisible(CommonFunctions.canImportExport(server) && CommonFunctions.hasLoadedPluginFunction(GDBCatalogPane.this.app, CommonFunctions.imExportFunctionKey));

            //1.0 暂时隐藏
            this.miSfclsStatistics.setVisible(false);
            this.miSfclsCopy.setVisible(false);

            this.setSeparatorVisible(this.sfClsMenu);

            setContextMenu(this.sfClsMenu);
        }
        //endregion

        //region 注记类右键菜单
        private ContextMenu aClsMenu = new ContextMenu();
        private MenuItem miAclsCopyURL = new MenuItem("复制URL", new ImageView(menuImageList.get(6)));
        private MenuItem miAclsProperty = new MenuItem("属性", new ImageView(menuImageList.get(2)));
        private MenuItem miAclsCopy = new MenuItem("复制", new ImageView(menuImageList.get(4)));
        private MenuItem miAclsExport = new MenuItem("导出");
        private MenuItem miAclsStatistics = new MenuItem("属性统计");
        private MenuItem miAclsIndexManage = new MenuItem("索引管理");
        private MenuItem miAclsClear = new MenuItem("清空");
        private MenuItem miAclsDelete = new MenuItem("删除", new ImageView(menuImageList.get(1)));
        private MenuItem miAclsRename = new MenuItem("重命名");
        private MenuItem miAclsMoveToDS = new MenuItem("移动到数据集");

        private void initAClsMenu() {
            this.aClsMenu.getItems().addAll(new SeparatorMenuItem(), this.miAclsExport, this.miAclsMoveToDS, new SeparatorMenuItem(), this.miAclsStatistics, new SeparatorMenuItem(), this.miAclsIndexManage, new SeparatorMenuItem(), this.miAclsRename, this.miAclsClear, this.miAclsDelete, new SeparatorMenuItem(), this.miAclsProperty, this.miAclsCopyURL, this.miAclsCopy);
            //复制URL
            this.miAclsCopyURL.setOnAction(event ->
            {
                this.copyURL();
            });
            //属性
            this.miAclsProperty.setOnAction(event ->
            {
                GDBCatalogPane.this.viewProperty(getTreeItem());
            });
            //复制
            this.miAclsCopy.setOnAction(event ->
            {
                this.copy();
            });
            //导出
            this.miAclsExport.setOnAction(event ->
            {
                DataConverts.convert(getCurrentWindow(), getItem().getUrl());
            });
            //属性统计
            this.miAclsStatistics.setOnAction(event ->
            {
            });
            //索引管理
            this.miAclsIndexManage.setOnAction(event ->
            {
                DataBase db = getOwnerDataBase(getTreeItem());
                if (db != null) {
                    AnnotationCls cls = new AnnotationCls(db);
                    if (cls.open(getItem().getId(), 0) <= 0) {
                        MessageBox.information("数据打开失败,无法进行索引管理。");
                    } else {
                        cls.close();
                        IndexManagerDialog dlg = new IndexManagerDialog(db, XClsType.XACls, getItem().getId());
                        dlg.initOwner(GDBCatalogPane.this.getCurrentWindow());
                        dlg.show();
                    }
                }
            });
            //清空
            this.miAclsClear.setOnAction(event ->
            {
                if (ButtonType.OK == MessageBox.question("确定要清空该注记类中的所有信息吗?")) {
                    DataBase db = getOwnerDataBase(getTreeItem());
                    if (db != null) {
                        AnnotationCls cls = new AnnotationCls(db);
                        if (cls.open(getItem().getId(), 0) <= 0) {
                            MessageBox.information("数据打开失败, 无法进行清空操作。");
                        } else {
                            if (cls.clear() <= 0) {
                                MessageBox.information("清空失败，可能数据正被其他程序占用。");
                            }
                            cls.close();
                            GDBCatalogPane.this.viewProperty(getTreeItem(), false);
                        }
                    }
                }
            });
            //删除
            this.miAclsDelete.setOnAction(event ->
            {
                if (ButtonType.OK == MessageBox.question("该注记类可能包含有用的对象, 确定要删除吗?")) {
                    this.closePropertyData();

                    DataBase db = getOwnerDataBase(getTreeItem());
                    if (db != null) {
                        if (AnnotationCls.remove(db, getItem().getId())) {
                            getTreeItem().getParent().getChildren().remove(getTreeItem());
                        } else {
                            MessageBox.information("删除失败。");
                        }
                    }
                }
            });
            //重命名
            this.miAclsRename.setOnAction(event ->
            {
                DataBase db = getOwnerDataBase(getTreeItem());
                if (db != null) {
                    String oldName = getItem().getText();
                    GDBCatalogPane.this.treeView.setEditable(true);
                    GDBCatalogPane.this.treeView.edit(getTreeItem());
                    GDBCatalogPane.this.treeView.setOnEditCommit(event1 ->
                    {
                        AnnotationCls cls = new AnnotationCls(db);
                        int id = getItem().getId();
                        if (cls.open(id, 0) <= 0) {
                            MessageBox.information("数据打开失败, 无法进行重命名。");
                        } else {
                            String newName = event1.getNewValue().getText();
                            if (newName != oldName) {
                                cls.setName(newName);
                                newName = db.getXclsName(XClsType.XACls, id);
                                if (!newName.equals(event1.getNewValue().getText())) {
                                    getTreeItem().setValue(event1.getOldValue());
                                    MessageBox.information("重命名失败,该数据库下可能已经存在同名数据。");
                                } else {
                                    GDBCatalogPane.this.viewProperty(getTreeItem(), false);
                                    CatalogTreeCell.this.refreshTreeItem();
                                }
                            }
                            cls.close();
                        }
                    });
                }
            });
            //移动到数据集
            this.miAclsMoveToDS.setOnAction(event ->
            {
                DataBase db = getOwnerDataBase(getTreeItem());
                int sourDS = GDBCatalogPane.this.treeView.getTreeItemLevel(getTreeItem()) == 3 ? 0 : getTreeItem().getParent().getValue().getId();
                MoveToFdsDialog dlg = new MoveToFdsDialog(db, sourDS, XClsType.XACls, getItem().getId());
                dlg.initOwner(GDBCatalogPane.this.getCurrentWindow());
                if (dlg.showAndWait().equals(Optional.of(ButtonType.OK))) {
                    getTreeItem().getParent().getChildren().remove(getTreeItem());
                }
            });
        }

        private void showAClsMenu() {
            CatalogTreeItem treeItem = (CatalogTreeItem) getTreeItem();
            DataBase db = getOwnerDataBase(getTreeItem());
            Server server = db.getServer();
            ConnectType conType = server.getConnectType();

            int[] dsIDs = db.getXclses(XClsType.XFds, 0);
            int sourDS = GDBCatalogPane.this.treeView.getTreeItemLevel(getTreeItem()) == 3 ? 0 : getTreeItem().getParent().getValue().getId();
            this.miAclsMoveToDS.setDisable(!(dsIDs != null && (dsIDs.length > 1 || (sourDS == 0 && dsIDs.length > 0))));
            this.miAclsStatistics.setVisible(CommonFunctions.hasLoadedPluginFunction(GDBCatalogPane.this.app, CommonFunctions.attStatisticsFunctionKey));
            this.miAclsIndexManage.setVisible(!ConnectType.Custom.equals(conType));
            this.miAclsExport.setVisible(CommonFunctions.canImportExport(server) && CommonFunctions.hasLoadedPluginFunction(GDBCatalogPane.this.app, CommonFunctions.imExportFunctionKey));

            //1.0 暂时隐藏
            this.miAclsStatistics.setVisible(false);
            this.miAclsCopy.setVisible(false);

            this.setSeparatorVisible(this.aClsMenu);

            setContextMenu(this.aClsMenu);
        }
        //endregion

        //region 对象类右键菜单
        private ContextMenu oClsMenu = new ContextMenu();
        private MenuItem miOclsCopyURL = new MenuItem("复制URL", new ImageView(menuImageList.get(6)));
        private MenuItem miOclsProperty = new MenuItem("属性", new ImageView(menuImageList.get(2)));
        private MenuItem miOclsCopy = new MenuItem("复制", new ImageView(menuImageList.get(4)));
        private MenuItem miOclsPreview = new MenuItem("预览");
        private MenuItem miOclsExport = new MenuItem("导出");
        private MenuItem miOclsStatistics = new MenuItem("属性统计");
        private MenuItem miOclsIndexManage = new MenuItem("索引管理");
        private MenuItem miOclsClear = new MenuItem("清空");
        private MenuItem miOclsDelete = new MenuItem("删除", new ImageView(menuImageList.get(1)));
        private MenuItem miOclsRename = new MenuItem("重命名");
        private MenuItem miOclsMoveToDS = new MenuItem("移动到数据集");

        private void initOClsMenu() {
            this.oClsMenu.getItems().addAll(this.miOclsPreview, new SeparatorMenuItem(), this.miOclsExport, this.miOclsMoveToDS, new SeparatorMenuItem(), this.miOclsStatistics, this.miOclsIndexManage, new SeparatorMenuItem(), this.miOclsRename, this.miOclsClear, this.miOclsDelete, new SeparatorMenuItem(), this.miOclsProperty, this.miOclsCopyURL, this.miOclsCopy);

            //复制URL
            this.miOclsCopyURL.setOnAction(event ->
            {
                this.copyURL();
            });
            //属性
            this.miOclsProperty.setOnAction(event ->
            {
                GDBCatalogPane.this.viewProperty(getTreeItem());
            });
            //复制
            this.miOclsCopy.setOnAction(event ->
            {
                this.copy();
            });
            //预览
            this.miOclsPreview.setOnAction(event ->
            {
            });
            //导出
            this.miOclsExport.setOnAction(event ->
            {
                DataConverts.convert(getCurrentWindow(), getItem().getUrl());
            });
            //属性统计
            this.miOclsStatistics.setOnAction(event ->
            {
            });
            //索引管理
            this.miOclsIndexManage.setOnAction(event ->
            {
                DataBase db = getOwnerDataBase(getTreeItem());
                if (db != null) {
                    ObjectCls cls = new ObjectCls(db);
                    if (cls.open(getItem().getId(), 0) <= 0) {
                        MessageBox.information("数据打开失败,无法进行索引管理。");
                    } else {
                        cls.close();
                        IndexManagerDialog dlg = new IndexManagerDialog(db, XClsType.XOCls, getItem().getId());
                        dlg.initOwner(GDBCatalogPane.this.getCurrentWindow());
                        dlg.show();
                    }
                }
            });
            //清空
            this.miOclsClear.setOnAction(event ->
            {
                if (ButtonType.OK == MessageBox.question("确定要清空该对象类中的所有信息吗?")) {
                    DataBase db = getOwnerDataBase(getTreeItem());
                    if (db != null) {
                        ObjectCls cls = new ObjectCls(db);
                        if (cls.open(getItem().getId(), 0) <= 0) {
                            MessageBox.information("数据打开失败, 无法进行清空操作。");
                        } else {
                            if (cls.clear() <= 0) {
                                MessageBox.information("清空失败，可能数据正被其他程序占用。");
                            }
                            cls.close();
                            GDBCatalogPane.this.viewProperty(getTreeItem(), false);
                        }
                    }
                }
            });
            //删除
            this.miOclsDelete.setOnAction(event ->
            {
                if (ButtonType.OK == MessageBox.question("该对象类可能包含有用的对象, 确定要删除吗?")) {
                    DataBase db = getOwnerDataBase(getTreeItem());
                    if (db != null) {
                        this.closePropertyData();

                        if (ObjectCls.remove(db, getItem().getId())) {
                            getTreeItem().getParent().getChildren().remove(getTreeItem());
                        } else {
                            MessageBox.information("删除失败。");
                        }
                    }
                }
            });
            //重命名
            this.miOclsRename.setOnAction(event ->
            {
                DataBase db = getOwnerDataBase(getTreeItem());
                if (db != null) {
                    String oldName = getItem().getText();
                    GDBCatalogPane.this.treeView.setEditable(true);
                    GDBCatalogPane.this.treeView.edit(getTreeItem());
                    GDBCatalogPane.this.treeView.setOnEditCommit(event1 ->
                    {
                        int id = getItem().getId();
                        ObjectCls cls = new ObjectCls(db);
                        if (cls.open(id, 0) <= 0) {
                            MessageBox.information("数据打开失败, 无法进行重命名。");
                        } else {
                            String newName = event1.getNewValue().getText();
                            if (newName != oldName) {
                                cls.setName(newName);
                                newName = db.getXclsName(XClsType.XOCls, id);
                                if (!newName.equals(event1.getNewValue().getText())) {
                                    getTreeItem().setValue(event1.getOldValue());
                                    MessageBox.information("重命名失败,该数据库下可能已经存在同名数据。");
                                } else {
                                    GDBCatalogPane.this.viewProperty(getTreeItem(), false);
                                    CatalogTreeCell.this.refreshTreeItem();
                                }
                            }
                            cls.close();
                        }
                    });
                }
            });
            //移动到数据集
            this.miOclsMoveToDS.setOnAction(event ->
            {
                DataBase db = getOwnerDataBase(getTreeItem());
                int sourDS = GDBCatalogPane.this.treeView.getTreeItemLevel(getTreeItem()) == 3 ? 0 : getTreeItem().getParent().getValue().getId();
                MoveToFdsDialog dlg = new MoveToFdsDialog(db, sourDS, XClsType.XOCls, getItem().getId());
                dlg.initOwner(GDBCatalogPane.this.getCurrentWindow());
                if (dlg.showAndWait().equals(Optional.of(ButtonType.OK))) {
                    getTreeItem().getParent().getChildren().remove(getTreeItem());
                }
            });
        }

        private void showOClsMenu() {
            CatalogTreeItem treeItem = (CatalogTreeItem) getTreeItem();
            DataBase db = getOwnerDataBase(getTreeItem());
            Server server = db.getServer();
            ConnectType conType = server.getConnectType();
            int[] dsIDs = db.getXclses(XClsType.XFds, 0);
            int sourDS = GDBCatalogPane.this.treeView.getTreeItemLevel(getTreeItem()) == 3 ? 0 : getTreeItem().getParent().getValue().getId();

            this.miOclsMoveToDS.setDisable(!(dsIDs != null && (dsIDs.length > 1 || (sourDS == 0 && dsIDs.length > 0))));
            this.miOclsStatistics.setVisible(CommonFunctions.hasLoadedPluginFunction(GDBCatalogPane.this.app, CommonFunctions.attStatisticsFunctionKey));
            this.miOclsIndexManage.setVisible(!ConnectType.Custom.equals(conType) && !ConnectType.Local.equals(conType) && !ConnectType.LocalPlus.equals(conType));
            this.miOclsExport.setVisible(CommonFunctions.canImportExport(server) && CommonFunctions.hasLoadedPluginFunction(GDBCatalogPane.this.app, CommonFunctions.imExportFunctionKey));

            //1.0 暂时隐藏
            this.miOclsStatistics.setVisible(false);
            this.miOclsCopy.setVisible(false);
            this.miOclsPreview.setVisible(false);

            this.setSeparatorVisible(this.oClsMenu);

            setContextMenu(this.oClsMenu);
        }
        //endregion

        //region 网络类右键菜单
        private ContextMenu nClsMenu = new ContextMenu();
        private MenuItem miNclsCopyURL = new MenuItem("复制URL", new ImageView(menuImageList.get(6)));
        private MenuItem miNclsProperty = new MenuItem("属性", new ImageView(menuImageList.get(2)));
        private MenuItem miNclsDelete = new MenuItem("删除", new ImageView(menuImageList.get(1)));
        private MenuItem miNclsRename = new MenuItem("重命名");

        private void initNClsMenu() {
            this.nClsMenu.getItems().addAll(this.miNclsRename, this.miNclsDelete, new SeparatorMenuItem(), this.miNclsProperty, this.miNclsCopyURL);

            //复制URL
            this.miNclsCopyURL.setOnAction(event ->
            {
                this.copyURL();
            });
            //属性
            this.miNclsProperty.setOnAction(event ->
            {
                GDBCatalogPane.this.viewProperty(getTreeItem());
            });
            //删除
            this.miNclsDelete.setOnAction(event ->
            {
                if (ButtonType.OK == MessageBox.question("该网络类可能包含有用的数据, 确定要删除吗?")) {
                    this.closePropertyData();

                    DataBase db = getOwnerDataBase(getTreeItem());
                    if (db != null) {
                        if (NetCls.remove(db, getItem().getId())) {
                            CatalogTreeItem parItem = (CatalogTreeItem) getTreeItem().getParent();
                            parItem.getChildren().remove(getTreeItem());
                            this.refreshTreeItem(parItem, true);
                        } else {
                            MessageBox.information("删除失败。");
                        }
                    }
                }
            });
            //重命名
            this.miNclsRename.setOnAction(event ->
            {
                DataBase db = getOwnerDataBase(getTreeItem());
                if (db != null) {
                    String oldName = getItem().getText();
                    GDBCatalogPane.this.treeView.setEditable(true);
                    GDBCatalogPane.this.treeView.edit(getTreeItem());
                    GDBCatalogPane.this.treeView.setOnEditCommit(event1 ->
                    {
                        int id = getItem().getId();
                        NetCls cls = new NetCls(db);
                        if (cls.open(id, 0) <= 0) {
                            MessageBox.information("数据打开失败, 无法进行重命名。");
                        } else {

                            String newName = event1.getNewValue().getText();
                            if (newName != oldName) {
                                cls.setName(newName);
                                newName = db.getXclsName(XClsType.XGNet, id);
                                if (!newName.equals(event1.getNewValue().getText())) {
                                    getTreeItem().setValue(event1.getOldValue());
                                    MessageBox.information("重命名失败,该数据库下可能已经存在同名数据。");
                                } else {
                                    GDBCatalogPane.this.viewProperty(getTreeItem(), false);
                                    CatalogTreeCell.this.refreshTreeItem();
                                }
                            }
                            cls.close();
                        }
                    });
                }
            });
        }

        private void showNClsMenu() {
            //this.setSeparatorVisible(this.nClsMenu);

            setContextMenu(this.nClsMenu);
        }

        //endregion

        //region 栅格目录右键菜单
        private ContextMenu rasCatMenu = new ContextMenu();
        private MenuItem miRcatRefresh = new MenuItem("刷新", new ImageView(menuImageList.get(0)));
        private MenuItem miRcatCopyURL = new MenuItem("复制URL", new ImageView(menuImageList.get(6)));
        private MenuItem miRcatProperty = new MenuItem("属性", new ImageView(menuImageList.get(2)));
        private MenuItem miRcatCopy = new MenuItem("复制", new ImageView(menuImageList.get(4)));
        private MenuItem miRcatPaste = new MenuItem("粘贴", new ImageView(menuImageList.get(5)));
        private MenuItem miRcatAppend = new MenuItem("追加数据集");
        private MenuItem miRcatDelete = new MenuItem("删除", new ImageView(menuImageList.get(1)));
        private MenuItem miRcatRename = new MenuItem("重命名");
        private MenuItem miRcatExport = new MenuItem("导出");

        private void initRasCatMenu() {
            this.rasCatMenu.getItems().addAll(this.miRcatRefresh, new SeparatorMenuItem(), this.miRcatAppend, this.miRcatExport, new SeparatorMenuItem(), new SeparatorMenuItem(), this.miRcatRename, this.miRcatDelete, new SeparatorMenuItem(), this.miRcatProperty, this.miRcatCopyURL, new SeparatorMenuItem(), this.miRcatCopy, this.miRcatPaste);

            //刷新
            this.miRcatRefresh.setOnAction(event ->
            {
                this.refreshTreeItem();
            });
            //复制URL
            this.miRcatCopyURL.setOnAction(event ->
            {
                this.copyURL();
            });
            //属性
            this.miRcatProperty.setOnAction(event ->
            {
                GDBCatalogPane.this.viewProperty(getTreeItem());
            });
            //复制
            this.miRcatCopy.setOnAction(event ->
            {
                this.copy();
            });
            //粘贴
            this.miRcatPaste.setOnAction(event ->
            {
            });
            //追加数据集
            this.miRcatAppend.setOnAction(event ->
            {
                DataBase db = getOwnerDataBase(getTreeItem());
                int clsID = getItem().getId();
                RasterCatalog rcat = new RasterCatalog(db);
                if (rcat.open(clsID) <= 0) {
                    MessageBox.information("所选栅格目录打开失败.");
                } else {
                    GDBOpenFileDialog dlg = new GDBOpenFileDialog();
                    dlg.setFilter("栅格数据集|ras");
                    //dlg.setFilter("所有影像|ras;*.msi;*.tif;*.img;*.bmp;*.jpg;*.gif;*.jp2;*.png;|栅格数据集|ras|影像文件(*.msi;*.tif;*.img;*.bmp;*.jpg;*.gif;*.jp2;*.png;*.txt;*.grd;*.bil)|*.msi;*.tif;*.img;*.bmp;*.jpg;*.gif;*.jp2;*.png;*.txt;*.grd;*.bil|TIF文件(*.tif)|*.tif|MSI文件(*.msi)|*.msi");
                    dlg.setMultiSelect(true);
                    Optional<String[]> optional = dlg.showAndWait();
                    if (optional != null && optional.isPresent()) {
                        String[] files = optional.get();
                        if (files != null && files.length > 0) {
                            String strError = "";
                            String insertError = "";
                            for (String file : files) {
                                String rdsName = "";
                                String dbURL = db.getURL();
                                if (dbURL.endsWith("/")) {
                                    dbURL = dbURL.substring(0, dbURL.length() - 1);
                                }

                                boolean isThisDBData = file.toLowerCase().startsWith(dbURL.toLowerCase() + "/ras/");
                                if (isThisDBData) {
                                    rdsName = file.substring(file.lastIndexOf("/") + 1);
                                } else {
                                    boolean isGDBData = file.toLowerCase().startsWith("gdbp://");
                                    String srcName = file.substring(file.lastIndexOf(isGDBData ? '/' : File.separatorChar) + 1);
                                    String desName = srcName;
                                    int i = 1;
                                    while (db.xClsIsExist(XClsType.XRds, desName) > 0) {
                                        desName = srcName + "_" + (i++);
                                    }
                                    String destUrl = dbURL + "/ras/" + desName;

                                    RasTrans trans = new RasTrans();
                                    int rtn = 0;
                                    if (!isGDBData && XPath.getExtension(file) == ".adf") {
                                        rtn = trans.convertArcDEMToRasDataSet("file:///" + file, destUrl);
                                    } else//GDB数据、.msi、.tif、.img、.jpg、.bmp.....
                                    {
                                        rtn = trans.rsImgTrans(file, destUrl, "MAPGIS7MSI");
                                    }

                                    if (rtn > 0) {
                                        rdsName = desName;
                                        if (isGDBData) {
                                            RasterDataset srcCls = new RasterDataset();
                                            if (srcCls.openByURL(file, RasterAccess.Read) > 0) {
                                                SRefData srcRef = srcCls.getSref();
                                                if (srcRef != null) {
                                                    db.addSRef(srcRef);
                                                }
                                                srcCls.close();
                                            } else {
                                                //MapGISErrorForm.ShowLastError();
                                            }
                                        } else {
                                            RasterDataset rds = new RasterDataset(db);
                                            if (rds.open(desName, RasterAccess.Write) > 0) {
                                                RDsInfo rdsInfo = rds.getInfo();
                                                rdsInfo.setSrID((rcat.getInfo()).getSrID());
                                                rds.setInfo(rdsInfo);
                                                rds.close();
                                            } else {
                                                //MapGISErrorForm.ShowLastError();
                                            }
                                        }
                                    } else {
                                        //MapGISErrorForm.ShowLastError();
                                    }
                                }

                                if (XString.isNullOrEmpty(rdsName)) {
                                    strError += (strError != "" ? "\n" : "") + "      " + file;
                                } else {
                                    if (rcat.insertItem(rdsName) > 0) {
                                        refreshTreeItem();
                                        if (isThisDBData) {
                                            CatalogTreeItem tiRaster = GDBCatalogPane.this.findTreeItemByName(this.getTreeItem().getParent(), rdsName, XClsType.XRds);
                                            if (tiRaster != null) {
                                                this.getTreeItem().getParent().getChildren().remove(tiRaster);
                                            }
                                        }
                                    } else {
                                        insertError += (insertError != "" ? "\n" : "") + "      " + rdsName;
                                    }
                                }
                            }

                            if (strError != "") {
                                strError = "导入下列栅格数据集失败:\n" + strError;
                            }
                            if (insertError != "") {
                                strError += (strError != "" ? "\n\n" : "") + "将下列栅格数据集添加到栅格目录失败:\n" + insertError;
                            }
                            if (strError != "") {
                                MessageBox.information(strError);
                            }
                        }
                    }
                    rcat.close();
                }
            });
            //删除
            this.miRcatDelete.setOnAction(event ->
            {
                if (ButtonType.OK == MessageBox.question("该栅格目录中可能包含栅格数据集, 确定要删除吗?")) {
                    this.closePropertyData();

                    DataBase db = getOwnerDataBase(getTreeItem());
                    if (db != null) {
                        if (RasterCatalog.delete(db, getItem().getId(), true)) {
                            getTreeItem().getParent().getChildren().remove(getTreeItem());
                        } else {
                            MessageBox.information("删除失败。");
                        }
                    }
                }
            });
            //重命名
            this.miRcatRename.setOnAction(event ->
            {
                DataBase db = getOwnerDataBase(getTreeItem());
                if (db != null) {
                    String oldName = getItem().getText();
                    GDBCatalogPane.this.treeView.setEditable(true);
                    GDBCatalogPane.this.treeView.edit(getTreeItem());
                    GDBCatalogPane.this.treeView.setOnEditCommit(event1 ->
                    {
                        int id = getItem().getId();
                        RasterCatalog cls = new RasterCatalog(db);
                        if (cls.open(id) <= 0) {
                            MessageBox.information("数据打开失败, 无法进行重命名。");
                        } else {
                            String newName = event1.getNewValue().getText();
                            if (newName != oldName) {
                                cls.setName(newName);
                                newName = db.getXclsName(XClsType.XRcat, id);
                                if (!newName.equals(event1.getNewValue().getText())) {
                                    getTreeItem().setValue(event1.getOldValue());
                                    MessageBox.information("重命名失败,该数据库下可能已经存在同名数据。");
                                } else {
                                    GDBCatalogPane.this.viewProperty(getTreeItem(), false);
                                    CatalogTreeCell.this.refreshTreeItem();
                                }
                            }
                            cls.close();
                        }
                    });
                }
            });
            //导出（GDB）
            this.miRcatExport.setOnAction(event ->
            {
                DataConverts.convert(getCurrentWindow(), getItem().getUrl());
            });
        }

        private void showRasCatMenu() {
            CatalogTreeItem treeItem = (CatalogTreeItem) getTreeItem();
            DataBase db = getOwnerDataBase(getTreeItem());
            Server server = db.getServer();

            this.miRcatPaste.setVisible(CommonFunctions.canAddData(server));
            this.miRcatPaste.setDisable(!CommonFunctions.clipboardContains(XClsType.XRds));
            this.miRcatExport.setVisible(CommonFunctions.canImportExport(server) && CommonFunctions.hasLoadedPluginFunction(GDBCatalogPane.this.app, CommonFunctions.imExportFunctionKey));

            //1.0 暂时隐藏
            this.miRcatCopy.setVisible(false);
            this.miRcatPaste.setVisible(false);

            this.setSeparatorVisible(this.rasCatMenu);

            setContextMenu(this.rasCatMenu);
        }
        //endregion

        //region 栅格数据集右键菜单
        private ContextMenu rasMenu = new ContextMenu();
        private MenuItem miRasCopyURL = new MenuItem("复制URL", new ImageView(menuImageList.get(6)));
        private MenuItem miRasProperty = new MenuItem("属性", new ImageView(menuImageList.get(2)));
        private MenuItem miRasCopy = new MenuItem("复制", new ImageView(menuImageList.get(4)));
        private MenuItem miRasDelete = new MenuItem("删除", new ImageView(menuImageList.get(1)));
        private MenuItem miRasRename = new MenuItem("重命名");
        private MenuItem miRasMoveOut = new MenuItem("移出");
        private MenuItem miRasExport = new MenuItem("导出");

        private void initRasMenu() {
            this.rasMenu.getItems().addAll(this.miRasExport, new SeparatorMenuItem(), this.miRasRename, this.miRasMoveOut, this.miRasDelete, new SeparatorMenuItem(), this.miRasProperty, this.miRasCopyURL, this.miRasCopy);

            //复制URL
            this.miRasCopyURL.setOnAction(event ->
            {
                this.copyURL();
            });
            //属性
            this.miRasProperty.setOnAction(event ->
            {
                GDBCatalogPane.this.viewProperty(getTreeItem());
            });
            //复制
            this.miRasCopy.setOnAction(event ->
            {
                this.copy();
            });
            //删除
            this.miRasDelete.setOnAction(event ->
            {
                if (ButtonType.OK == MessageBox.question("该栅格数据集可能包含有用的信息, 确定要删除吗?")) {
                    this.closePropertyData();

                    DataBase db = getOwnerDataBase(getTreeItem());
                    if (db != null) {
                        boolean deleted = false;
                        int rdsID = getItem().getId();
                        int rCatID = GDBCatalogPane.this.treeView.getTreeItemLevel(getTreeItem()) == 3 ? 0 : (int) getTreeItem().getParent().getValue().getId();
                        if (rCatID <= 0) {
                            deleted = RasterDataset.deleteFromGDB(db, rdsID) > 0;
                        } else {
                            RasterCatalog rasCat = new RasterCatalog(db);
                            if (rasCat.open(rCatID) > 0) {
                                deleted = rasCat.removeItem(getItem().getText()) > 0;
                                rasCat.close();
                            } else {
                                //MapGISErrorForm.ShowLastError();
                            }
                        }

                        if (deleted) {
                            getTreeItem().getParent().getChildren().remove(getTreeItem());
                        } else {
                            MessageBox.information("删除失败。");
                        }
                    }
                }
            });
            //重命名
            this.miRasRename.setOnAction(event ->
            {
                DataBase db = getOwnerDataBase(getTreeItem());
                if (db != null) {
                    String oldName = getItem().getText();
                    GDBCatalogPane.this.treeView.setEditable(true);
                    GDBCatalogPane.this.treeView.edit(getTreeItem());
                    GDBCatalogPane.this.treeView.setOnEditCommit(event1 ->
                    {
                        int id = getItem().getId();
                        RasterDataset cls = new RasterDataset(db);
                        if (cls.open(oldName, RasterAccess.Write) <= 0) {
                            MessageBox.information("数据打开失败, 无法进行重命名。");
                        } else {
                            String newName = event1.getNewValue().getText();
                            if (newName != oldName) {
                                cls.setName(newName);
                                newName = db.getXclsName(XClsType.XRds, id);
                                if (!newName.equals(event1.getNewValue().getText())) {
                                    getTreeItem().setValue(event1.getOldValue());
                                    MessageBox.information("重命名失败,该数据库下可能已经存在同名数据。");
                                } else {
                                    GDBCatalogPane.this.viewProperty(getTreeItem(), false);
                                    CatalogTreeCell.this.refreshTreeItem();
                                }
                            }
                            cls.close();
                        }
                    });
                }
            });
            //移出
            this.miRasMoveOut.setOnAction(event ->
            {
                DataBase db = getOwnerDataBase(getTreeItem());
                if (db != null) {
                    RasterCatalog rasCat = new RasterCatalog(db);
                    TreeItem<TreeItemObject> rcatItem = this.getTreeItem().getParent();
                    if (rasCat.open(rcatItem.getValue().getId()) > 0) {
                        int clsID = this.getItem().getId();
                        if (rasCat.removeItem(getItem().getText()) > 0) {
                            rcatItem.getChildren().remove(this.getTreeItem());
                            GDBCatalogPane.this.insertClsTreeItem(db, XClsType.XRds, clsID, (IXClsInfo) null, rcatItem.getParent());
                        } else {
                            MessageBox.information("栅格数据集移出失败。");
                        }
                        rasCat.close();
                    } else {
                        //MapGISErrorForm.ShowLastError();
                    }
                }
            });
            //导出
            this.miRasExport.setOnAction(event ->
            {
                DataConverts.convert(getCurrentWindow(), getItem().getUrl());
            });
        }

        private void showRasMenu() {
            CatalogTreeItem treeItem = (CatalogTreeItem) getTreeItem();
            DataBase db = getOwnerDataBase(getTreeItem());
            Server server = db.getServer();
            int level = GDBCatalogPane.this.treeView.getTreeItemLevel(treeItem);

            this.miRasDelete.setVisible(level == 3);
            this.miRasRename.setVisible(level == 3);
            this.miRasMoveOut.setVisible(level == 4);
            this.miRasExport.setVisible(CommonFunctions.canImportExport(server) && CommonFunctions.hasLoadedPluginFunction(GDBCatalogPane.this.app, CommonFunctions.imExportFunctionKey));

            //1.0 暂时隐藏
            this.miRasCopy.setVisible(false);

            this.setSeparatorVisible(this.rasMenu);

            setContextMenu(this.rasMenu);
        }
        //endregion

        //region 镶嵌数据集右键菜单
        private ContextMenu mosaicMenu = new ContextMenu();
        private MenuItem miMosaicCopyURL = new MenuItem("复制URL", new ImageView(menuImageList.get(6)));
        private MenuItem miMosaicProperty = new MenuItem("属性", new ImageView(menuImageList.get(2)));
        private MenuItem miMosaicAddRaster = new MenuItem("添加栅格数据");
        private MenuItem miMosaicDelete = new MenuItem("删除", new ImageView(menuImageList.get(1)));
        private MenuItem miMosaicRename = new MenuItem("重命名");
        private MenuItem miMosaicDefineView = new MenuItem("定义概视图");
        private MenuItem miMosaicBuildView = new MenuItem("构建概视图");
        private MenuItem miMosaicBuildBorder = new MenuItem("构建边界");
        private MenuItem miMosaicBuildOutline = new MenuItem("构建轮廓线");
        private MenuItem miMosaicCalcVisibility = new MenuItem("计算项目可见性");
        private MenuItem miMosaicUpdateBorder = new MenuItem("更新边界线");
        private MenuItem miMosaicRemoveRaster = new MenuItem("移除栅格");

        private void initMosaicMenu() {
            this.mosaicMenu.getItems().addAll(new SeparatorMenuItem(), this.miMosaicAddRaster, new SeparatorMenuItem(), this.miMosaicDefineView, this.miMosaicBuildView, new SeparatorMenuItem(), this.miMosaicBuildBorder, this.miMosaicBuildOutline, this.miMosaicCalcVisibility, this.miMosaicUpdateBorder, new SeparatorMenuItem(), this.miMosaicRemoveRaster, new SeparatorMenuItem(), new SeparatorMenuItem(), this.miMosaicRename, this.miMosaicDelete, new SeparatorMenuItem(), this.miMosaicProperty, this.miMosaicCopyURL);

            //复制URL
            this.miMosaicCopyURL.setOnAction(event ->
            {
                this.copyURL();
            });
            //属性
            this.miMosaicProperty.setOnAction(event ->
            {
                GDBCatalogPane.this.viewProperty(getTreeItem());
            });
            //添加栅格数据
            this.miMosaicAddRaster.setOnAction(event ->
            {
                DataBase db = getOwnerDataBase(getTreeItem());
                int clsID = getItem().getId();
                MosaicDataset mosaicDs = new MosaicDataset();
                if (!mosaicDs.open(db, clsID)) {
                    MessageBox.information("所选镶嵌数据集打开失败.");
                } else {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("栅格文件(*.msi;*.tif;*.img)", "*.msi;*.tif;*.img"),
                            new FileChooser.ExtensionFilter("MapGIS Msi(*.msi)", "*.msi"),
                            new FileChooser.ExtensionFilter("GTIFF文件(*.tif)", "*.tif"),
                            new FileChooser.ExtensionFilter("HFA文件(*.img)", "*.img"));
                    List<File> files = fileChooser.showOpenMultipleDialog(GDBCatalogPane.this.getCurrentWindow());
                    if (files != null && files.size() > 0) {
                        //TODO：AddRasters未封装
                    }
                    mosaicDs.close();
                }
            });
            //删除
            this.miMosaicDelete.setOnAction(event ->
            {
                if (ButtonType.OK == MessageBox.question("该镶嵌数据集可能包含有用的信息, 确定要删除吗?")) {
                    this.closePropertyData();

                    DataBase db = getOwnerDataBase(getTreeItem());
                    if (db != null) {
                        //region
                        if (MosaicDataset.removeMosaicDS(db, getItem().getId())) {
                            getTreeItem().getParent().getChildren().remove(getTreeItem());
                        } else {
                            MessageBox.information("删除失败。");
                        }
                    }
                }
            });
            //重命名
            this.miMosaicRename.setOnAction(event ->
            {
                DataBase db = getOwnerDataBase(getTreeItem());
                if (db != null) {
                    String oldName = getItem().getText();
                    int id = getItem().getId();
                    GDBCatalogPane.this.treeView.setEditable(true);
                    GDBCatalogPane.this.treeView.edit(getTreeItem());
                    GDBCatalogPane.this.treeView.setOnEditCommit(event1 ->
                    {
                        MosaicDataset cls = new MosaicDataset();
                        if (!cls.open(db, id)) {
                            MessageBox.information("数据打开失败, 无法进行重命名。");
                        } else {
                            String newName = event1.getNewValue().getText();
                            if (newName != oldName) {
                                cls.setName(newName);
                                newName = db.getXclsName(XClsType.XMosaicDS, id);
                                if (!newName.equals(event1.getNewValue().getText())) {
                                    getTreeItem().setValue(event1.getOldValue());
                                    MessageBox.information("重命名失败,该数据库下可能已经存在同名数据。");
                                } else {
                                    GDBCatalogPane.this.viewProperty(getTreeItem(), false);
                                    CatalogTreeCell.this.refreshTreeItem();
                                }
                            }
                            cls.close();
                        }
                        cls.dispose();
                    });
                }
            });
            //定义概视图
            this.miMosaicDefineView.setOnAction(event ->
            {
            });
            //构建概视图
            this.miMosaicBuildView.setOnAction(event ->
            {
            });
            //构建边界
            this.miMosaicBuildBorder.setOnAction(event ->
            {
            });
            //构建轮廓线
            this.miMosaicBuildOutline.setOnAction(event ->
            {
            });
            //计算项目可见性
            this.miMosaicCalcVisibility.setOnAction(event ->
            {
            });
            //更新边界线
            this.miMosaicUpdateBorder.setOnAction(event ->
            {
            });
            //移除栅格
            this.miMosaicRemoveRaster.setOnAction(event ->
            {
            });
        }

        private void showMosaicMenu() {
            //1.0 暂时隐藏
            this.miMosaicDefineView.setVisible(false);
            this.miMosaicBuildView.setVisible(false);
            this.miMosaicBuildBorder.setVisible(false);
            this.miMosaicBuildOutline.setVisible(false);
            this.miMosaicCalcVisibility.setVisible(false);
            this.miMosaicUpdateBorder.setVisible(false);
            this.miMosaicRemoveRaster.setVisible(false);

            //this.setSeparatorVisible(this.mosaicMenu);
            setContextMenu(this.mosaicMenu);
        }
        //endregion

        //region 右键菜单通用方法

        /**
         * 根据设置的菜单的可见情况，设置分隔条的可见性，使不会出现连续两个分隔条或最后一项是分隔条
         *
         * @param menu
         */
        private void setSeparatorVisible(ContextMenu menu) {
            if (menu != null) {
                MenuItem firstVisibleItem = null;
                for (int i = 0; i < menu.getItems().size(); ) {
                    MenuItem menuItem = menu.getItems().get(i);

                    i++;
                    if (menuItem instanceof SeparatorMenuItem) {
                        boolean hasVisible = false;
                        if (firstVisibleItem != null) {
                            MenuItem mi = menu.getItems().get(i);
                            while (!(mi instanceof SeparatorMenuItem)) {
                                i++;
                                if (mi.isVisible()) {
                                    hasVisible = true;
                                    break;
                                }
                                if (i >= menu.getItems().size()) {
                                    break;
                                }
                                mi = menu.getItems().get(i);
                            }
                        }
                        menuItem.setVisible(hasVisible);
                    } else if (menuItem.isVisible() && firstVisibleItem == null) {
                        firstVisibleItem = menuItem;
                    }
                }
            }
        }

        /**
         * 刷新当前树节点（清空子节点，重加）
         */
        private void refreshTreeItem() {
            this.refreshTreeItem(getTreeItem(), true);
        }

        /**
         * 刷新节点（清空子节点，重加）
         *
         * @param treeItem 要刷新的树节点
         */
        private void refreshTreeItem(TreeItem<TreeItemObject> treeItem, boolean expand) {
            if (treeItem != null) {
                if (GDBCatalogPane.this.treeView.getTreeItemLevel(treeItem) == 1)//数据源，关闭open过的数据库
                {
                    for (TreeItem<TreeItemObject> dbItem : getTreeItem().getChildren()) {
                        DataBase db = getOwnerDataBase(dbItem);
                        if (db != null && db.hasOpened()) {
                            db.close();
                        }
                    }
                }

                boolean isExpanded = treeItem.isExpanded();
                treeItem.getChildren().clear();
                ((CatalogTreeItem) treeItem).reset();
                treeItem.getChildren();
                if (expand || isExpanded) {
                    treeItem.setExpanded(true);
                }
            }
        }

        /**
         * 复制URL
         *
         * @return url
         */
        private String copyURL() {
            String url = "";
            if (getTreeItem() != null) {
                url = getItem().getUrl();
                if (!XString.isNullOrEmpty(url)) {
                    Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clip.setContents(new StringSelection(url), null);
                }
            }
            return url;
        }

        /**
         * 复制
         */
        private void copy() {
            try {
                this.setClipboardXCls(getItem().getUrl());
            } catch (Exception ex) {
                MessageBox.information(ex.getMessage());
            }
        }

        private final DataFlavor xClsFlavor = new DataFlavor(IBasCls.class, "XCls");//剪贴类数据的形式定义

        /**
         * 将类数据复制到剪贴板
         *
         * @param xCls 类数据（实际粘贴的是URL)
         */
        private void setClipboardXCls(final Object xCls) throws Exception {
            Transferable trans = new Transferable() {
                @Override
                public DataFlavor[] getTransferDataFlavors() {
                    return new DataFlavor[]{xClsFlavor};
                }

                @Override
                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    return xClsFlavor.equals(flavor);
                }

                @Override
                public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                    if (isDataFlavorSupported(flavor)) {
                        return xCls;
                    }
                    throw new UnsupportedFlavorException(flavor);
                }
            };
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
        }

        /**
         * 从剪贴板获取复制的类数据
         *
         * @return 剪贴板里面的数据URL
         */
        private Object getXClsFromClipboard() throws Exception {
            Object obj = null;
            Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if (transferable != null && transferable.isDataFlavorSupported(xClsFlavor)) {
                obj = transferable.getTransferData(xClsFlavor);
            } else {
                obj = null;
            }
            return obj;
        }

        /**
         * 删除前关闭属性窗口中打开的对象
         */
        private void closePropertyData() {
            IDockWindow dw = GDBCatalogPane.this.app.getPluginContainer().getDockWindows().get(DataPropertyDW.class.getName());
            if (dw instanceof DataPropertyDW) {
                ((DataPropertyDW) dw).getXClsBaseInfo().clearInfo();
            }
        }

        //endregion
    }
}
