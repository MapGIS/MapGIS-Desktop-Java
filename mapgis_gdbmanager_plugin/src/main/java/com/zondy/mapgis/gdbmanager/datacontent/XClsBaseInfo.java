package com.zondy.mapgis.gdbmanager.datacontent;

import com.zondy.mapgis.base.LanguageConvert;
import com.zondy.mapgis.common.CommonFuns;
import com.zondy.mapgis.fields.FieldsEditPane;
import com.zondy.mapgis.gdbmanager.gdbcatalog.TreeItemObject;
import com.zondy.mapgis.geodatabase.config.ConnectType;
import com.zondy.mapgis.geodatabase.*;
import com.zondy.mapgis.geodatabase.net.NetCls;
import com.zondy.mapgis.geodatabase.raster.*;
import com.zondy.mapgis.geometry.GeomType;
import com.zondy.mapgis.geometry.Rect;
import com.zondy.mapgis.sref.SRefInfoPane;
import com.zondy.mapgis.srs.SRefData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * 要素类基本信息
 *
 * @author : ysp
 * @date : 2019-12-18
 **/
public class XClsBaseInfo extends TabPane {
    private Tab baseInfoTab;
    private Tab clsInfoTab;
    private Tab fieldsTab;
    private Tab srefTab;
    private Tab previewTab;
    private ImageView previewImageView;
    private FieldsEditPane fieldsEditPane;
    private SRefInfoPane sRefInfoPane;
    ObservableList<BaseInfoItem> clsBaseInfos = FXCollections.observableArrayList();
    ObservableList<BaseInfoItem> clsClsAttInfos = FXCollections.observableArrayList();
    ObservableList<BaseInfoItem> clsFieldsInfos = FXCollections.observableArrayList();
    ListView<BaseInfoItem> listViewBaseInfo;
    private ListView<BaseInfoItem> listViewClsAtt;
    private HashMap<String, Image> images = new HashMap<>();
    /**
     * 当前显示数据的类ID
     */
    private int clsID;
    /**
     * 当前显示数据的URL
     */
    private String clsURL;
    /**
     * 当前显示数据的类名
     */
    private String clsName;
    /**
     * 当前显示数据的数据类型
     */
    private XClsType clsType;
    /**
     * 当前显示数据的数据库
     */
    private IVectorCls fieldCls = null;
    private SRefData sRefData = null;

    public XClsBaseInfo() {
        this.initImages();
        this.initUI();
    }

    /**
     * 在内容视图中显示类的信息
     *
     * @param obj 节点对象
     */
    public void displayClassInfo(TreeItemObject obj) {
        if (obj != null && obj.getTag() instanceof XClsType) {
            this.clsType = (XClsType) obj.getTag();
            this.clsName = obj.getText();
            this.clsID = obj.getId();
            this.clsURL = obj.getUrl();
            this.displayBaseInfo();
        }
    }

    public void displayInfo(TreeItemObject obj) {
        this.clearInfo();
        if (obj.getTag() instanceof Server) {
            Server server = (Server) obj.getTag();
            clsName = server.getSvrName();
            if (server.hasConnected()) {
                ConnectType connectType = server.getConnectType();
                this.clsBaseInfos.add(new BaseInfoItem("image", "Png_GDBServer_16"));
                this.clsBaseInfos.add(new BaseInfoItem("连接类型", connectType.toString()));
                this.clsBaseInfos.add(new BaseInfoItem("地理数据库数目", Long.toString(server.gdbNum())));
            } else {
                this.clsBaseInfos.add(new BaseInfoItem("image", "Png_GDBServerError_16"));
                this.clsBaseInfos.add(new BaseInfoItem("连接类型", "未连接"));
            }
            this.updatBaseInfos();
        } else if (obj.getTag() instanceof DataBase) {
            DataBase dataBase = (DataBase) obj.getTag();
            clsName = dataBase.getName();
            this.clsBaseInfos.add(new BaseInfoItem("image", "Png_GDataBase_16"));
            if (dataBase.hasOpened()) {
                ArrayList<XClsType> refTypes = new ArrayList<>();
                refTypes.add(XClsType.XFds);
                refTypes.add(XClsType.XSFCls);
                refTypes.add(XClsType.XACls);
                refTypes.add(XClsType.XOCls);
                refTypes.add(XClsType.XRCls);
                refTypes.add(XClsType.XGNet);
                refTypes.add(XClsType.XRcat);
                refTypes.add(XClsType.XRds);
                refTypes.add(XClsType.XMosaicDS);
                for (XClsType clsType : refTypes) {
                    long count = dataBase.getXclsNum(clsType, -1);
                    if (count > 0) {
                        this.clsBaseInfos.add(new BaseInfoItem(LanguageConvert.xClsTypeConvert(clsType) + "数目", Long.toString(count)));
                    }
                }
                ArrayList<DBInfo> dataFiles = dataBase.getDBInfo(HDFType.HDF_Data);
                if (dataFiles != null) {
                    for (DBInfo dbInfo : dataFiles) {
                        DBFileInfo dbFile = dbInfo.getDataBaseFileInfo();
                        if (dbFile != null && dbFile.getFilePath() != null) {
                            this.clsBaseInfos.add(new BaseInfoItem("存储路径", dbFile.getFilePath()));
                        }
                    }
                }
            } else {
                this.clsBaseInfos.add(new BaseInfoItem("image", "Png_GDataBaseError_16"));
            }
            this.updatBaseInfos();
        } else if (obj.getTag() instanceof XClsType) {
            displayClassInfo(obj);
        } else if (obj.getTag() instanceof SRefData) {
            this.sRefData = (SRefData) obj.getTag();
            SRefInfoPane sRefInfoPane = new SRefInfoPane(this.sRefData);
            this.srefTab.setContent(sRefInfoPane);
            if (this.getTabs().contains(this.baseInfoTab)) {
                this.getTabs().clear();
            }
            if (!this.getTabs().contains(this.srefTab)) {
                this.getTabs().add(this.srefTab);
            }
//            int type = this.sRefData.getType();
//            String typeStr = LanguageConvert.sRefTypeConvert((short) type);
//            this.clsBaseInfos.add(new BaseInfoItem("image", "Png_SRefGeo_16"));
//            this.clsBaseInfos.add(new BaseInfoItem("空间参照系类型", typeStr));
//            this.clsBaseInfos.add(new BaseInfoItem("空间参照系名称", this.sRefData.getSRSName()));
//            this.clsBaseInfos.add(new BaseInfoItem("空间参照系ID", Integer.toString(this.sRefData.getSRSID())));
//            if (type == 1){
//                //地理坐标系
////                this.clsBaseInfos.add(new BaseInfoItem("空间参照系名称", ""));
////                this.clsBaseInfos.add(new BaseInfoItem("空间参照系类型", ""));
////                this.clsBaseInfos.add(new BaseInfoItem("空间参照系名称", ""));
////                this.clsBaseInfos.add(new BaseInfoItem("空间参照系类型", ""));
////                this.clsBaseInfos.add(new BaseInfoItem("空间参照系名称", ""));
//            }
//            else if(type == 3){
//                //投影坐标系
//            }
//            this.updatBaseInfos();
        }
    }

    private void displayDataBaseInfo() {

    }

    /**
     * 显示基本信息
     */
    private void displayBaseInfo() {
        if (this.clsURL != null && !this.clsURL.isEmpty()) {//                     类对象节点

            if (XClsType.XFds.equals(this.clsType)) {
                String imageName = "Png_FDs_16";
                this.clsBaseInfos.add(new BaseInfoItem("image", imageName));
                this.clsBaseInfos.add(new BaseInfoItem("类型", "要素数据集"));
                this.clsBaseInfos.add(new BaseInfoItem("要素集ID", String.format("%d", this.clsID)));
            } else if (XClsType.XSFCls.equals(this.clsType)) {//                         简单要素类属性
                fieldCls = new SFeatureCls();
                if (((SFeatureCls) fieldCls).openByURL(this.clsURL) > 0) {
                    SFeatureCls sfcls = (SFeatureCls) this.fieldCls;
                    //处理属性结构设置页面
                    this.fieldsEditPane = new FieldsEditPane(sfcls);
                    DataBase dataBase = sfcls.getGDataBase();
                    SFClsInfo clsInfo = (SFClsInfo) dataBase.getXclsInfo(XClsType.XSFCls, this.clsID);
                    GeomType geomType = sfcls.getGeomType();
                    String type = "";
                    String imageName = "";
                    if (GeomType.GeomPnt.equals(geomType)) {
                        type = "点";
                        imageName = "Png_SfClsPnt_16";
                    } else if (GeomType.GeomLin.equals(geomType)) {
                        type = "线";
                        imageName = "Png_SfClsLin_16";
                    } else if (GeomType.GeomReg.equals(geomType)) {
                        type = "区";
                        imageName = "Png_SfClsReg_16";
                    } else if (GeomType.GeomSurface.equals(geomType)) {
                        type = "面";
                        imageName = "Png_SfClsSurface_16";
                    } else if (GeomType.GeomEntity.equals(geomType)) {
                        type = "体";
                        imageName = "Png_SfClsEntity_16";
                    }
                    this.clsBaseInfos.add(new BaseInfoItem("image", imageName));
                    this.clsBaseInfos.add(new BaseInfoItem("类型", "简单要素类"));
                    this.clsBaseInfos.add(new BaseInfoItem("类ID", Integer.toString(clsInfo.getID())));

                    Rect rect = sfcls.getRange();
                    if (rect != null) {
                        String rangeStr = String.format("左下角(%.2f,%.2f),右上角(%.2f,%.2f)",
                                rect.getXMin(), rect.getYMin(), rect.getXMax(), rect.getXMax());
                        this.clsBaseInfos.add(new BaseInfoItem("坐标范围", rangeStr));
                    }

                    SRefData sd = dataBase.getSRef(sfcls.getsrID());

                    sRefInfoPane = new SRefInfoPane(sd);
                    String srsName = String.format("%s(ID=%d)", sd != null ? sd.getSRSName() : "", sfcls.getsrID());
                    String ds = String.format("%s(ID=%d)", dataBase.getXclsName(XClsType.XFds, sfcls.getdsID()), sfcls.getdsID());

                    String netStr = clsInfo != null ? dataBase.getXclsName(XClsType.XGNet, clsInfo.getgNetID()) : "";
                    this.clsClsAttInfos.addAll(
                            new BaseInfoItem("别名", sfcls.getAliasName()),
                            new BaseInfoItem("Guid", clsInfo != null ? clsInfo.getGUID().toString() : ""),
                            new BaseInfoItem("系统库Guid", clsInfo.getModelName()),
                            new BaseInfoItem("所属数据集", ds),
                            new BaseInfoItem("几何形态", type),
                            new BaseInfoItem("包含要素数", Long.toString(sfcls.getObjCount())),
                            new BaseInfoItem("子类型字段", clsInfo != null ? clsInfo.getSubTypeField() : ""),
                            new BaseInfoItem("空间参照系", srsName),
                            new BaseInfoItem("参与的几何网络", netStr),
                            new BaseInfoItem("所有者", clsInfo != null ? clsInfo.getOwner() : ""),
                            new BaseInfoItem("创建时间", clsInfo != null ? getTimeStampStr(clsInfo.getCreateTime()) : ""),
                            new BaseInfoItem("修改时间", clsInfo != null ? getTimeStampStr(clsInfo.getModifyTime()) : "")
                    );
                    this.clsBaseInfos.add(new BaseInfoItem("previewimage", this.clsURL));

                }
            } else if (XClsType.XACls.equals(this.clsType)) {
                //                                注记类属性

                fieldCls = new AnnotationCls();
                if (((AnnotationCls) fieldCls).openByURL(this.clsURL) > 0) {
                    AnnotationCls acls = (AnnotationCls) (this.fieldCls);
                    //处理属性结构设置页面
                    this.fieldsEditPane = new FieldsEditPane(acls);
                    DataBase dataBase = acls.getGDataBase();
                    AClsInfo clsInfo = (AClsInfo) dataBase.getXclsInfo(XClsType.XACls, this.clsID);
                    if (clsInfo != null) {
                        this.clsBaseInfos.add(new BaseInfoItem("image", "Png_ACls_16"));
                        this.clsBaseInfos.add(new BaseInfoItem("类型", "注记类"));
                        this.clsBaseInfos.add(new BaseInfoItem("类ID", Integer.toString(clsInfo.getID())));

                        Rect rect = acls.getRange();
                        if (rect != null) {
                            String rangeStr = String.format("左下角(%.2f,%.2f),右上角(%.2f,%.2f)",
                                    rect.getXMin(), rect.getYMin(), rect.getXMax(), rect.getXMax());
                            this.clsBaseInfos.add(new BaseInfoItem("坐标范围", rangeStr));
                        }

                        SRefData sd = dataBase.getSRef(acls.getsrID());
                        sRefInfoPane = new SRefInfoPane(sd);
                        String srsName = String.format("%s(ID=%d)", sd != null ? sd.getSRSName() : "", acls.getsrID());
                        String ds = String.format("%s(ID=%d)", dataBase.getXclsName(XClsType.XFds, clsInfo.getdsID()), clsInfo.getdsID());

                        this.clsClsAttInfos.addAll(
                                new BaseInfoItem("别名", clsInfo.getAliasName()),
                                new BaseInfoItem("Guid", clsInfo.getGUID().toString()),
                                new BaseInfoItem("系统库Guid", clsInfo.getModelName()),
                                new BaseInfoItem("所属数据集", ds),
                                new BaseInfoItem("包含注记数", Long.toString(acls.getObjCount())),
                                new BaseInfoItem("子类型字段", clsInfo.getsubTypeField()),
                                new BaseInfoItem("空间参照系", srsName),
                                new BaseInfoItem("所有者", clsInfo.getOwner()),
                                new BaseInfoItem("创建时间", getTimeStampStr(clsInfo.getCreateTime())),
                                new BaseInfoItem("修改时间", getTimeStampStr(clsInfo.getModifyTime()))
                        );
                        this.clsBaseInfos.add(new BaseInfoItem("previewimage", this.clsURL));

                    }
                }
            } else if (XClsType.XOCls.equals(this.clsType)) {
                //对象类属性
                fieldCls = new ObjectCls();
                if (((ObjectCls) fieldCls).openByURL(this.clsURL) > 0) {
                    ObjectCls cls = (ObjectCls) (this.fieldCls);
                    //处理属性结构设置页面
                    this.fieldsEditPane = new FieldsEditPane(cls);
                    DataBase dataBase = cls.getGDataBase();
                    OClsInfo clsInfo = (OClsInfo) dataBase.getXclsInfo(XClsType.XOCls, this.clsID);
                    if (clsInfo != null) {
                        this.clsBaseInfos.add(new BaseInfoItem("image", "Png_OCls_16"));
                        this.clsBaseInfos.add(new BaseInfoItem("类型", "对象类"));
                        this.clsBaseInfos.add(new BaseInfoItem("类ID", Integer.toString(clsInfo.getID())));

                        String ds = String.format("%s(ID=%d)", dataBase.getXclsName(XClsType.XFds, clsInfo.getdsID()), clsInfo.getdsID());
                        this.clsClsAttInfos.addAll(
                                new BaseInfoItem("别名", clsInfo.getAliasName()),
                                new BaseInfoItem("Guid", clsInfo.getGUID().toString()),
                                new BaseInfoItem("所属数据集", ds),
                                new BaseInfoItem("包含对象数", Long.toString(cls.getObjCount())),
                                new BaseInfoItem("子类型字段", clsInfo.getSubTypeField()),
                                new BaseInfoItem("所有者", clsInfo.getOwner()),
                                new BaseInfoItem("创建时间", getTimeStampStr(clsInfo.getCreateTime())),
                                new BaseInfoItem("修改时间", getTimeStampStr(clsInfo.getModifyTime()))
                        );
                        this.clsBaseInfos.add(new BaseInfoItem("previewimage", this.clsURL));

                    }
                }
            } else if (XClsType.XRCls.equals(this.clsType)) {
//                                        关系类属性
                RelationCls cls = new RelationCls();
                if (cls.openByURL(this.clsURL) > 0) {
                    DataBase dataBase = cls.getGDataBase();
                    RClsInfo clsInfo = (RClsInfo) dataBase.getXclsInfo(XClsType.XRCls, this.clsID);
                    if (clsInfo != null) {
                        this.clsBaseInfos.add(new BaseInfoItem("image", "Png_RCls_16"));
                        this.clsBaseInfos.add(new BaseInfoItem("类型", "关系类"));
                        this.clsBaseInfos.add(new BaseInfoItem("类ID", Integer.toString(clsInfo.getID())));
                        String ds = String.format("%s(ID=%d)", dataBase.getXclsName(XClsType.XFds, clsInfo.getdsID()), clsInfo.getdsID());
                        this.clsClsAttInfos.addAll(
                                new BaseInfoItem("所属数据集", ds),
                                new BaseInfoItem("关系类类型", LanguageConvert.relTypeConvert(clsInfo.getRelType())),
                                new BaseInfoItem("映射关系", LanguageConvert.relCardTypeConvert(clsInfo.getCardinality())),
                                new BaseInfoItem("通知类型", LanguageConvert.relNotifyTypeConvert(clsInfo.getNotification())),
                                new BaseInfoItem("属性化", clsInfo.getIsAttributed() > 0 ? "是" : "否"),
                                new BaseInfoItem("向前标签", clsInfo.getFwardLabel()),
                                new BaseInfoItem("向后标签", clsInfo.getBwardLabel()),
                                new BaseInfoItem("所有者", clsInfo.getOwner())
                        );
                    }
                    cls.close();
                    this.clsBaseInfos.add(new BaseInfoItem("previewimage", this.clsURL));
                }
            } else if (XClsType.XGNet.equals(this.clsType)) {
                //                          网络类属性
//
//                        GNetClsInfo clsInfo = this.dataBase.GetXclsInfo(XClsType.GNet, this.clsID) as GNetClsInfo;
//                        if (clsInfo != null)
//                        {
//                            this.pictureEdit_Icon.Image = MapGIS.Desktop.Resources.Png_NetCls_32;
//                            this.textEdit_ClsName.Text = this.clsName;
//                            this.textEdit_ClsType.Text = Resources.String_Type + ": " + Resources.String_NCls;
//                            this.textEdit_ClsID.Text = Resources.String_ClassID + ": " + clsInfo.ID.ToString();
//                            NetCls ncls = new NetCls(this.dataBase);
//                            if (ncls.Open(this.clsID, 0))
//                            {
//                                NetClsLayer nLayer = new NetClsLayer();
//                                if (nLayer.AttachData(ncls) && nLayer.IsValid)
//                                {
//                                    Rect rect = nLayer.Range;
//                                    if (dspRange && rect != null)
//                                    {
//                                        this.labelControl1.Text = "(" + rect.XMax.ToString("F2") + "," + rect.YMax.ToString("F2") + ")";
//                                        this.labelControl2.Text = "(" + rect.XMin.ToString("F2") + "," + rect.YMin.ToString("F2") + ")";
//                                        this.pictureEdit_DataImage.Tag = new ThreadData(this.dataBase, clsInfo, this.pictureEdit_DataImage);
//                                    }
//                                    nLayer.DetachData();
//                                }
//                                this.DisplayBaseInfoEx_NetCls(ncls, new string[]{
//                                        Resources.String_DatasetBelonged + ": " + this.dataBase.GetXclsName(XClsType.Fds, clsInfo.DsID) + (clsInfo.DsID == 0 ? "" : "(ID=" + clsInfo.DsID.ToString() + ")")
//                                        ,Resources.String_TopPntSFeatureCls + ": " + this.dataBase.GetXclsName(XClsType.SFCls, clsInfo.TopoNodClsID)
//                                        ,Resources.String_Tolerance +": " + clsInfo.Tolerance.ToString()
//                                        ,Resources.String_Owner + ": " + clsInfo.Owner});
//                                ncls.Close();
//                            }
//                        }
            } else if (XClsType.XRcat.equals(this.clsType)) {
                //                        栅格目录属性
                RasterCatalog cls = new RasterCatalog();
                if (cls.openByURL(this.clsURL) > 0) {
                    DataBase dataBase = cls.getGDataBase();
                    RCatInfo clsInfo = (RCatInfo) dataBase.getXclsInfo(XClsType.XRcat, this.clsID);
                    if (clsInfo != null) {
                        this.clsBaseInfos.add(new BaseInfoItem("image", "Png_RasterCatalog_16"));
                        this.clsBaseInfos.add(new BaseInfoItem("类型", "栅格目录"));
                        this.clsBaseInfos.add(new BaseInfoItem("类ID", Integer.toString(clsInfo.getID())));

                        SRefData sd = dataBase.getSRef((int) clsInfo.getSrID());
                        sRefInfoPane = new SRefInfoPane(sd);
                        String srsName = String.format("%s(ID=%d)", sd != null ? sd.getSRSName() : "", clsInfo.getSrID());
                        long num = dataBase.getXclsNum(XClsType.XRds, this.clsID);
                        this.clsClsAttInfos.addAll(
                                new BaseInfoItem("空间参照系", srsName),
                                new BaseInfoItem("数据集数", String.format("%d", num))
                        );

                    }
                    cls.close();
                }
//                this.clsBaseInfos.add(new BaseInfoItem("previewimage", this.clsURL));
            } else if (XClsType.XRds.equals(this.clsType)) {//                        栅格数据集属性
                RasterDataset rds = new RasterDataset();
                if (rds.openByURL(this.clsURL, RasterAccess.Read) > 0) {
                    DataBase dataBase = rds.getGDataBase();
                    RDsInfo clsInfo = (RDsInfo) dataBase.getXclsInfo(XClsType.XRds, this.clsID);
                    if (clsInfo != null) {
                        this.clsBaseInfos.add(new BaseInfoItem("image", "Png_RasterDs_16"));
                        this.clsBaseInfos.add(new BaseInfoItem("类型", "栅格数据集"));
                        this.clsBaseInfos.add(new BaseInfoItem("类ID", Integer.toString(clsInfo.getID())));

                        SRefData sd = dataBase.getSRef((int) clsInfo.getSrID());
                        sRefInfoPane = new SRefInfoPane(sd);
                        String srsName = String.format("%s(ID=%d)", sd != null ? sd.getSRSName() : "", clsInfo.getSrID());

                        Rect rect = rds.getRange();
                        if (rect != null) {
                            String rangeStr = String.format("左下角(%.2f,%.2f),右上角(%.2f,%.2f)",
                                    rect.getXMin(), rect.getYMin(), rect.getXMax(), rect.getXMax());
                            this.clsBaseInfos.add(new BaseInfoItem("坐标范围", rangeStr));
                        }
                        ObservableList<BandInfoDef> bandInfoDefs = FXCollections.observableArrayList();
                        int bandNum = rds.getBandNum();
                        for (int i = 1; i <= bandNum; i++) {
                            RasterBand band = rds.getRasterBand(i);
                            RasterStatistics rasterStatistics = band.getStatistics();
                            bandInfoDefs.add(new BandInfoDef(String.format("波段%d", i),
                                    rasterStatistics.getMin(), rasterStatistics.getMax(),
                                    rasterStatistics.getMean(), rasterStatistics.getSD()));
                        }

                        ObservableList<PyramidInfoDef> pyramidInfoDefs = FXCollections.observableArrayList();
                        int pyramidNum = 1;
//                        int height = rds.getHeight();
//                        int width = rds.getWidth();
                        int sheight = rds.getHeight();//行数即影像高度
                        int swidth = rds.getWidth(); //列数即影像宽度
                        double row_Gap = rds.getResolutionX();//X间距即(X方向)分辨率:X范围除以列数
                        double col_Gap = rds.getResolutionY();//Y间距即(Y方向)分辨率:Y范围除以行数
                        //接口有问题？at com.zondy.mapgis.geodatabase.raster.RasterDataSetNative.jni_GetPyramidLayerNum(Native Method)
//                        pyramidNum = getPyramidNum();
//                        if (pyramidNum>0) {
//                            sheight = rds.getPyramidHeight(pyramidNum);
//                            swidth = rds.getPyramidWidth(pyramidNum);
//                            for (int i = 1; i <= pyramidNum; i++)
//                            {
//                                sheight = rds.getPyramidHeight(pyramidNum);
//                                swidth = rds.getPyramidWidth(pyramidNum);
//                                rds.getPyLayerCellSize(i, col_Gap, row_Gap);
//                                pyramidInfoDefs.add(new PyramidInfoDef(String.format("第%d层", i),row_Gap, col_Gap, sheight, swidth));
//                            }
//                        }
                        this.clsClsAttInfos.addAll(
                                new BaseInfoItem("别名", clsInfo.getAliasName()),
                                new BaseInfoItem("所属栅格目录", dataBase.getXclsName(XClsType.XRcat, (int) clsInfo.getCatID())),
                                new BaseInfoItem("行数", String.valueOf(rds.getHeight())),
                                new BaseInfoItem("列数", String.valueOf(rds.getWidth())),
                                new BaseInfoItem("行向分辨率", String.valueOf(rds.getResolutionX())),
                                new BaseInfoItem("列向分辨率", String.valueOf(rds.getResolutionY())),
                                new BaseInfoItem("像元类型", LanguageConvert.pixelTypeConvert(rds.getPixelType())),
                                new BaseInfoItem("空间参照系", srsName),
                                new BaseInfoItem("所有者", clsInfo.getOwner()),
                                new BaseInfoItem("创建时间", getTimeStampStr(clsInfo.getCreateTime())),
                                new BaseInfoItem("修改时间", getTimeStampStr(clsInfo.getModifyTime())),
                                new BaseInfoItem("波段信息", "flag_Blank"),
                                new BaseInfoItem("波段数", String.format("%d", bandNum)),
                                new BaseInfoItem("BandInfo", bandInfoDefs),
                                new BaseInfoItem("金字塔信息", "flag_Blank"),
                                new BaseInfoItem("金字塔层数", String.format("%d", pyramidNum)),
                                new BaseInfoItem("PyramidInfo", pyramidInfoDefs)
                        );
                    }
                    rds.close();
                }
                this.clsBaseInfos.add(new BaseInfoItem("previewimage", this.clsURL));

            } else if (XClsType.XMosaicDS.equals(this.clsType)) { //镶嵌数据集属性
//                MdsInfo clsInfo = (MdsInfo) this.dataBase.getXclsInfo(XClsType.XMosaicDS, this.clsID);
//                        if (clsInfo != null)
//                        {
//                            this.pictureEdit_Icon.Image = MapGIS.Desktop.Resources.Png_MosaicDataSet_32;
//                            this.textEdit_ClsName.Text = this.clsName;
//                            this.textEdit_ClsType.Text = Resources.String_Type + ": " + Resources.String_MosaicDS;
//                            this.textEdit_ClsID.Text = Resources.String_ClassID + ": " + clsInfo.ID.ToString();
//
//                            MosaicDataSet mds = new MosaicDataSet();
//                            if (mds.Open(this.dataBase, this.clsName))
//                            {
//                                string srsName = "";
//                                SRefData sd = this.dataBase.SpatialRefMng.Get(clsInfo.SrID);
//
//                                srsName = string.Format("{0}(ID={1})", sd != null ? sd.SRSName : string.Empty, clsInfo.SrID);
//
//                                        #region 显示到内容视图基本信息页
//
//                                string crtTime = Resources.String_CreateTime + ": ";
//                                string mdyTime = Resources.String_ModifyTime + ": ";
//                                int srcRasCount = 0;//源栅格数目
//                                int overViewCount = 0;//有效概视图数目
//                                SFeatureCls catalog = mds.Catalog;
//                                QueryDef queryDef = new QueryDef();
//                                queryDef.CursorType = SetCursorType.ForwardOnly;
//                                queryDef.Filter = String.Format("Category={0}", 1);
//                                RecordSet rcdSet = catalog.Select(queryDef);
//                                if (rcdSet != null)
//                                {
//                                    rcdSet.MoveFirst();
//                                    while (!rcdSet.IsEOF)
//                                    {
//                                        srcRasCount++;
//                                        rcdSet.MoveNext();
//                                    }
//                                    rcdSet.Detach();
//                                    rcdSet.Dispose();
//                                }
//
//                                queryDef = new QueryDef();
//                                queryDef.CursorType = SetCursorType.ForwardOnly;
//                                queryDef.Filter = String.Format("Category={0}", 3);
//                                rcdSet = catalog.Select(queryDef);
//                                if (rcdSet != null)
//                                {
//                                    rcdSet.MoveFirst();
//                                    while (!rcdSet.IsEOF)
//                                    {
//                                        overViewCount++;
//                                        rcdSet.MoveNext();
//                                    }
//                                    rcdSet.Detach();
//                                    rcdSet.Dispose();
//                                }
//
//                                try
//                                {
//                                    crtTime += clsInfo.CreateTime;
//                                    mdyTime += clsInfo.ModifyTime;
//                                }
//                                catch { }
//                                string[] attNameValues = new string[]{
//                                        Resources.String_Alias + ": " + clsInfo.aliasName
//                                        //,Resources.String_RasterCatalogBelonged + ": " + this.dataBase.GetXclsName(XClsType.Rcat, clsInfo.ID)
//                                        //,Resources.String_RasRowCount + ": " + mds.RasterInfo.lines.ToString()
//                                        //,Resources.String_RasColCount + ": " + mds.RasterInfo.linecells.ToString()
//                                        //,Resources.String_YResolution + ": " + mds.RasterInfo.cellSize.ysize.ToString()
//                                        //,Resources.String_XResolution + ": " + mds.RasterInfo.cellSize.xsize.ToString()
//                                        ,Resources.String_RasDataType + ": " + MapGIS.UI.Controls.LanguageConvert.RasDataTypeConvert(mds.RasterInfo.cellType)
//                                        ,Resources.String_OrigRasterCount + ": " + srcRasCount
//                                        ,Resources.String_ValidCount + ": " + overViewCount
//                                        ,Resources.String_SRS + ": " + srsName
//                                        ,Resources.String_Owner + ": " + clsInfo.Owner
//                                        ,crtTime
//                                        ,mdyTime};
//
//                                int subTableKey = 1;
//                                foreach (string nameValue in attNameValues)
//                                {
//                                    string[] nameVal = nameValue.Split(new string[] { ": " }, StringSplitOptions.None);
//                                    if (nameVal.Length == 2)
//                                        this.dataTable.Rows.Add("     " + nameVal[0] + ": ", nameVal[1], "1" + Resources.String_ClassProperties, subTableKey);
//                                    else if (nameVal.Length == 3)
//                                    {
//                                        if (nameVal[0] == Resources.String_SRS)//属性值里面会有“：”
//                                            this.dataTable.Rows.Add("     " + nameVal[0] + ": ", nameVal[1] + ": " + nameVal[2], "1" + Resources.String_ClassProperties, subTableKey);
//                                        else
//                                            this.dataTable.Rows.Add("     " + nameVal[0] + ": ", nameVal[1], nameVal[2], subTableKey);
//                                    }
//                                    subTableKey++;
//                                }
//
//                                //显示范围
//                                Rect range = mds.Catalog.Range;
//                                if (dspRange && range != null)
//                                {
//                                    this.labelControl1.Text = "(" + range.XMax.ToString("F2") + "," + range.YMax.ToString("F2") + ")";
//                                    this.labelControl2.Text = "(" + range.XMin.ToString("F2") + "," + range.YMin.ToString("F2") + ")";
//                                    if (mds.Catalog.Count > 0)
//                                        this.pictureEdit_DataImage.Tag = new ThreadData(this.dataBase, mds.ClsInfo, this.pictureEdit_DataImage);//此处实际上绘制的是轮廓线图层(仅显示绿色边线) by zkj 2018-4-28
//                                }
//
//                                //显示字段信息
//                                Fields flds = mds.Catalog.Fields;
//                                if (flds != null)
//                                {
//                                    this.dataTable.Rows.Add("     " + Resources.String_FieldCount + ": ", flds.Count.ToString(), "4" + Resources.String_AttStructure, subTableKey);
//
//                                    DataTable dtSub = new DataTable();
//                                    dtSub.Columns.Add("Name");
//                                    dtSub.Columns.Add("Type");
//                                    dtSub.Columns.Add("Length");
//                                    dtSub.Columns.Add("Key", typeof(int));
//                                    dtSub.TableName = "son";
//                                    this.dataSet.Tables.Add(dtSub);
//                                    this.dataSet.Relations.Add("Level1", this.dataSet.Tables["dad"].Columns["Key"], this.dataSet.Tables["son"].Columns["Key"]);
//
//                                    for (int i = 0; i < flds.Count; i++)
//                                    {
//                                        Field fld = flds.GetItem(i);
//                                        dtSub.Rows.Add(fld.FieldName, MapGIS.UI.Controls.LanguageConvert.FieldTypeConvert(fld.FieldType), fld.MskLength, subTableKey);
//                                    }
//                                }
//                                        #endregion
//
//                                mds.Close();    //不支持多线程，须在主线程中先关闭
//                            }
//                            else
//                                MapGIS.Desktop.UI.Controls.MapGISErrorForm.ShowLastError();
//                        }
            }
        }
        this.updatBaseInfos();
    }

    /**
     * 清除数据信息
     */
    public void clearInfo() {
        if (this.clsBaseInfos != null) {
            this.clsBaseInfos.clear();
            if (this.listViewBaseInfo != null) {
                this.listViewBaseInfo.setItems(this.clsBaseInfos);
            }
        }
        if (this.clsClsAttInfos != null) {
            this.clsClsAttInfos.clear();
            if (this.listViewClsAtt != null) {
                this.listViewClsAtt.setItems(this.clsClsAttInfos);
            }
        }
        this.sRefInfoPane = null;
        this.fieldsEditPane = null;
        this.previewImageView = null;
        if (fieldCls != null) {
            if (fieldCls.hasOpened()) {
                fieldCls.close();
            }
            fieldCls = null;
        }
    }

    /**
     * 更新数据信息
     */
    private void updatBaseInfos() {
        this.listViewBaseInfo.setItems(this.clsBaseInfos);
        this.listViewClsAtt.setItems(this.clsClsAttInfos);
        updateTab();
    }

    /**
     * 初始化页面
     */
    private void initUI() {
        this.setClip(new ImageView(new Image("/Png_GraphicsView_16.png")));
        this.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        baseInfoTab = new Tab("基本信息");
//        baseInfoTab.setGraphic(new ImageView(images.get("Png_SfClsReg_16")));
        this.initBaseInfoTab();

        clsInfoTab = new Tab("类信息");
//        clsInfoTab.setGraphic(new ImageView(images.get("Png_SfClsReg_16")));
        this.initClsInfoTab();

        fieldsTab = new Tab("属性结构");
//        attTab.setGraphic(new ImageView(images.get("Png_SfClsReg_16")));
//        this.initAttTab();
        srefTab = new Tab("参照系");

        previewTab = new Tab("预览");
        VBox vBox = new VBox();
        vBox.setFillWidth(true);
        previewTab.setContent(vBox);
        this.getTabs().addAll(baseInfoTab, clsInfoTab);
//        this.getTabs().addAll(baseInfoTab, clsInfoTab, fieldsTab,previewTab);
    }

    /**
     * 初始化基本信息页面
     */
    private void initBaseInfoTab() {
        listViewBaseInfo = new ListView<>();
        listViewBaseInfo.setPrefHeight(150);
        listViewBaseInfo.setEditable(true);
        listViewBaseInfo.setCellFactory(param -> new BaseInfoListCell());
        this.baseInfoTab.setContent(listViewBaseInfo);
    }

    /**
     * 初始化类信息页面
     */
    private void initClsInfoTab() {
        listViewClsAtt = new ListView<>();
        listViewClsAtt.setCellFactory(param -> new BaseInfoListCell());
        this.clsInfoTab.setContent(listViewClsAtt);
    }

    /**
     * 处理Tab页面是否显示
     */
    private void updateTab() {
        this.fieldsTab.setContent(null);
        if (this.fieldsEditPane != null) {
            this.fieldsTab.setContent(this.fieldsEditPane);
            if (!this.getTabs().contains(this.fieldsTab)) {
                this.getTabs().add(this.fieldsTab);
            }
        } else {
            if (this.getTabs().contains(this.fieldsTab)) {
                this.getTabs().remove(this.fieldsTab);
            }
        }
        if (this.clsBaseInfos != null && this.clsBaseInfos.size() > 0) {

            this.baseInfoTab.setContent(this.listViewBaseInfo);
            if (!this.getTabs().contains(this.baseInfoTab)) {
                this.getTabs().add(this.baseInfoTab);
            }
        } else {
            if (this.getTabs().contains(this.baseInfoTab)) {
                this.getTabs().remove(this.baseInfoTab);
            }
        }

        if (this.clsClsAttInfos != null && this.clsClsAttInfos.size() > 0) {

            this.clsInfoTab.setContent(this.listViewClsAtt);
            if (!this.getTabs().contains(this.clsInfoTab)) {
                this.getTabs().add(this.clsInfoTab);
            }
        } else {
            if (this.getTabs().contains(this.clsInfoTab)) {
                this.getTabs().remove(this.clsInfoTab);
            }
        }

        if (this.sRefInfoPane != null) {
            this.sRefInfoPane.setOnSRefDataChanged(event -> {
                if (event.getSRefData() != null) {
                    if (fieldCls != null && fieldCls.hasOpened()) {
                        int clsID = fieldCls.getGDataBase().addSRef(event.getSRefData());
                        fieldCls.setsrID(clsID);
                    }
                }
            });
            this.srefTab.setContent(this.sRefInfoPane);
            if (!this.getTabs().contains(this.srefTab)) {
                this.getTabs().add(this.srefTab);
            }
        } else {
            if (this.getTabs().contains(this.srefTab)) {
                this.getTabs().remove(this.srefTab);
            }
        }

    }

    /**
     * 初始化图标
     */
    private void initImages() {

        images.put("Png_GDBServerError_16", new Image(getClass().getResourceAsStream("/Png_GDBServerError_16.png")));
        images.put("Png_GDBServer_16", new Image(getClass().getResourceAsStream("/Png_GDBServer_16.png")));
        images.put("Png_GDataBase_16", new Image(getClass().getResourceAsStream("/Png_GDataBase_16.png")));
        images.put("Png_GDataBaseError_16", new Image(getClass().getResourceAsStream("/Png_GDataBaseError_16.png")));
        images.put("Png_SfClsPnt_16", new Image(getClass().getResourceAsStream("/Png_SfClsPnt_16.png")));
        images.put("Png_SfClsLin_16", new Image(getClass().getResourceAsStream("/Png_SfClsLin_16.png")));
        images.put("Png_SfClsReg_16", new Image(getClass().getResourceAsStream("/Png_SfClsReg_16.png")));
        images.put("Png_SfClsSurface_16", new Image(getClass().getResourceAsStream("/Png_SfClsSurface_16.png")));
        images.put("Png_SfClsEntity_16", new Image(getClass().getResourceAsStream("/Png_SfClsEntity_16.png")));
        images.put("Png_SRefGeoFolder_16", new Image(getClass().getResourceAsStream("/Png_SRefGeoFolder_16.png")));
        images.put("Png_FDs_16", new Image(getClass().getResourceAsStream("/Png_FDs_16.png")));
        images.put("Png_ACls_16", new Image(getClass().getResourceAsStream("/Png_ACls_16.png")));
        images.put("Png_OCls_16", new Image(getClass().getResourceAsStream("/Png_OCls_16.png")));
        images.put("Png_RCls_16", new Image(getClass().getResourceAsStream("/Png_RCls_16.png")));
        images.put("Png_NetCls_16", new Image(getClass().getResourceAsStream("/Png_NetCls_16.png")));
        images.put("Png_Mapset_16", new Image(getClass().getResourceAsStream("/Png_Mapset_16.png")));
        images.put("Png_RasterCatalog_16", new Image(getClass().getResourceAsStream("/Png_RasterCatalog_16.png")));
        images.put("Png_RasterDs_16", new Image(getClass().getResourceAsStream("/Png_RasterDs_16.png")));
        images.put("Png_MosaicDataSet_16", new Image(getClass().getResourceAsStream("/Png_MosaicDataSet_16.png")));
        images.put("Png_Backward_24", new Image(getClass().getResourceAsStream("/Png_Backward_24.png")));
        images.put("Png_SRefGeo_16", new Image(getClass().getResourceAsStream("/Png_SRefGeo_16.png")));
    }
    //-------内部类

    /**
     * 获取时间字符串
     */
    public static String getTimeStampStr(Calendar cl) {
        String str = "";
        if (cl != null) {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            str = sdf1.format(cl.getTime());
        }
        return str;
    }

    /**
     * 自定义列表单元格
     */
    public class BaseInfoListCell extends ListCell<BaseInfoItem> {
        public BaseInfoListCell() {
            super();
        }

        @Override
        protected void updateItem(BaseInfoItem item, boolean empty) {
            super.updateItem(item, empty);
            this.setText("");
            this.setGraphic(null);
            if (item == null) {
                this.setText("");
            } else {
                if (item.getName().equals("image")) {
                    ImageView imageView = new ImageView(images.get(item.getVal()));
                    this.setGraphic(imageView);
                    this.setText(clsName);
                } else if (item.getName().equals("previewimage")) {
                    if (item.getVal() instanceof String) {
                        String url = (String) item.getVal();
                        Image img = drawImage(url, 128, 128);
                        this.setGraphic(new ImageView(img));
                    }
                } else if (item.getName().equals("BandInfo")) {
                    if (item.getVal() != null) {
                        TableView<BandInfoDef> tableView = new TableView<>();
//                        tableView.setEditable(false);
                        tableView.setPrefHeight(200);
                        // 每个Table的列
                        TableColumn keyCol = new TableColumn("");
                        keyCol.setMinWidth(32);
                        keyCol.setCellValueFactory(new PropertyValueFactory<BandInfoDef, String>("key"));

                        TableColumn minCol = new TableColumn("最小值");
                        minCol.setMinWidth(100);
                        minCol.setCellValueFactory(new PropertyValueFactory<BandInfoDef, Double>("min"));

                        TableColumn maxCol = new TableColumn("最大值");
                        maxCol.setMinWidth(30);
                        maxCol.setCellValueFactory(new PropertyValueFactory<BandInfoDef, Double>("max"));

                        TableColumn meanCol = new TableColumn("平均值");
                        meanCol.setMinWidth(50);
                        meanCol.setCellValueFactory(new PropertyValueFactory<BandInfoDef, Double>("mean"));

                        TableColumn stdCol = new TableColumn("标准差");
                        stdCol.setMinWidth(50);
                        stdCol.setCellValueFactory(new PropertyValueFactory<BandInfoDef, Double>("std"));
                        // 一次添加列进TableView
                        tableView.getColumns().addAll(keyCol, minCol, maxCol, meanCol, stdCol);
                        tableView.setItems((ObservableList<BandInfoDef>) item.getVal());
                        this.setGraphic(tableView);
                    }
                } else if (item.getName().equals("PyramidInfo")) {
                    if (item.getVal() != null) {
                        TableView<PyramidInfoDef> tableView = new TableView<>();
//                        tableView.setEditable(false);
                        tableView.setPrefHeight(200);
                        // 每个Table的列
                        TableColumn keyCol = new TableColumn("");
                        keyCol.setMinWidth(32);
                        keyCol.setCellValueFactory(new PropertyValueFactory<PyramidInfoDef, String>("key"));

                        TableColumn xGapCol = new TableColumn("X间距");
                        xGapCol.setMinWidth(100);
                        xGapCol.setCellValueFactory(new PropertyValueFactory<PyramidInfoDef, Double>("xGap"));

                        TableColumn yGapCol = new TableColumn("Y间距");
                        yGapCol.setMinWidth(30);
                        yGapCol.setCellValueFactory(new PropertyValueFactory<PyramidInfoDef, Double>("yGap"));

                        TableColumn rowCol = new TableColumn("行数");
                        rowCol.setMinWidth(50);
                        rowCol.setCellValueFactory(new PropertyValueFactory<PyramidInfoDef, Integer>("row"));

                        TableColumn colCol = new TableColumn("列数");
                        colCol.setMinWidth(50);
                        colCol.setCellValueFactory(new PropertyValueFactory<PyramidInfoDef, Integer>("col"));
                        // 一次添加列进TableView
                        tableView.getColumns().addAll(keyCol, xGapCol, yGapCol, rowCol, colCol);
                        tableView.setItems((ObservableList<PyramidInfoDef>) item.getVal());
                        this.setGraphic(tableView);
                    }
                } else {
                    if (item.getVal() == "flag_Blank") {
                        this.setText(item.getName());
                    } else {
                        String valStr = "";
                        if (item.getVal() instanceof String) {
                            valStr = (String) item.getVal();
                        }
                        HBox hBox = new HBox();
                        hBox.setFillHeight(true);
                        hBox.setAlignment(Pos.CENTER_LEFT);
                        Label label1 = new Label(item.getName() + ":");
                        label1.setMinWidth(100);
                        Label label2 = new Label(valStr);
                        label2.setMinWidth(400);
                        HBox.setHgrow(label2, Priority.ALWAYS);
                        hBox.getChildren().addAll(label1, label2);
                        this.setGraphic(hBox);
                    }

//                    this.setText(item.getName() + item.getInfo());
                }
            }
        }

        private Image drawImage(String url, int width, int height) {
            Image img = null;
            if (XClsType.XSFCls.equals(clsType)) {//简单要素类
                SFeatureCls cls = new SFeatureCls();
                if (cls.openByURL(url) > 0) {
                    img = DrawImageHelp.drawImage(cls, width, height);
                    cls.close();
                }
            } else if (XClsType.XACls.equals(clsType)) {//                    注记类
                AnnotationCls cls = new AnnotationCls();
                if (cls.openByURL(url) > 0) {
                    img = DrawImageHelp.drawImage(cls, width, height);
                    cls.close();
                }

            } else if (XClsType.XOCls.equals(clsType)) {//                    对象类
                ObjectCls cls = new ObjectCls();
                if (cls.openByURL(url) > 0) {
                    img = DrawImageHelp.drawImage(cls, width, height);
                    cls.close();
                }
            } else if (XClsType.XRCls.equals(clsType)) {//                    关系类
                RelationCls cls = new RelationCls();
                if (cls.openByURL(url) > 0) {
                    img = DrawImageHelp.drawImage(cls, width, height);
                    cls.close();
                }
            } else if (XClsType.XGNet.equals(clsType)) {//                    网络类
                NetCls cls = new NetCls();
                if (cls.openByURL(url) > 0) {
                    img = DrawImageHelp.drawImage(cls, width, height);
                    cls.close();
                }
            } else if (XClsType.XMapSet.equals(clsType)) {//                    地图集

            } else if (XClsType.XRcat.equals(clsType)) {//                    栅格目录
                RasterCatalog cls = new RasterCatalog();
                if (cls.openByURL(url) > 0) {
                    img = DrawImageHelp.drawImage(cls, width, height);
                    cls.close();
                }
            } else if (XClsType.XRds.equals(clsType)) {//                    栅格数据集

                RasterDataset cls = new RasterDataset();
                if (cls.openByURL(url, RasterAccess.Read) > 0) {
                    img = DrawImageHelp.drawImage(cls, width, height);
                    cls.close();
                }
            } else if (XClsType.XMosaicDS.equals(clsType)) {//                    镶嵌数据集
                MosaicDataset cls = new MosaicDataset();
                if (cls.openByUrl(clsURL)) {
                    img = DrawImageHelp.drawImage(cls, width, height);
                    cls.close();
                }
            }
            if (img == null) {
                img = CommonFuns.getImageByColor(Color.WHITE, width, height);
            }
            return img;
        }

    }

    /**
     * 属性信息类
     */
    public class BaseInfoItem {
        private String name;
        private Object val;

        public BaseInfoItem(String name, Object val) {
            this.name = name;
            this.val = val;
        }

        public Object getVal() {
            return val;
        }

        public String getName() {
            return name;
        }
    }

    public class PyramidInfoDef {
        private double xGap;
        private double yGap;
        private int row;
        private int col;
        private String key;

        private PyramidInfoDef(String key, double xGap, double yGap, int row, int col) {
            this.xGap = xGap;
            this.yGap = yGap;
            this.row = row;
            this.col = col;
            this.key = key;
        }

        public double getxGap() {
            return xGap;
        }

        public double getyGap() {
            return yGap;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

        public String getKey() {
            return key;
        }
    }

    public class BandInfoDef {
        private double min;
        private double max;
        private double mean;
        private double std;
        private String key;

        private BandInfoDef(String key) {
            this.key = key;
        }

        private BandInfoDef(String key, double min, double max, double mean, double std) {
            this.min = min;
            this.max = max;
            this.mean = mean;
            this.std = std;
            this.key = key;
        }

        public double getMin() {
            return min;
        }

        public double getMax() {
            return max;
        }

        public double getMean() {
            return mean;
        }

        public double getStd() {
            return std;
        }

        public String getKey() {
            return key;
        }
    }
}
