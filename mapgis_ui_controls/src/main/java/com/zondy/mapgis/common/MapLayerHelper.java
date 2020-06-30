package com.zondy.mapgis.common;

import com.zondy.mapgis.base.XPath;
import com.zondy.mapgis.base.XString;
import com.zondy.mapgis.geodatabase.XClsType;
import com.zondy.mapgis.geodatabase.config.EnvConfig;
import com.zondy.mapgis.geodatabase.config.SysConfigDirType;
import com.zondy.mapgis.geodatabase.raster.RasterAccess;
import com.zondy.mapgis.geodatabase.raster.RasterDataset;
import com.zondy.mapgis.geodatabase.raster.RasterResampling;
import com.zondy.mapgis.hdfsubdata.AddRasHdfSubDataSetDialog;
import com.zondy.mapgis.hdfsubdata.CreatePyramidTipDialog;
import com.zondy.mapgis.hdfsubdata.HdfSubDataSetItem;
import com.zondy.mapgis.map.*;
import com.zondy.mapgis.utilities.UtilityTool;
import javafx.scene.control.ButtonType;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

/**
 * @author cxy
 * @date 2019/12/19
 */
public class MapLayerHelper {
    /**
     * 从配置文件中读取是否提示选择遥感hdf4或5文件子集框参数
     *
     * @return 是否提示选择遥感hdf4或5文件子集框参数
     */
    public static boolean getAskAgain() {
        boolean rtn = false;
        String filePath = XPath.combine(EnvConfig.getConfigDirectory(SysConfigDirType.Raster), "RsParameterSet.xml");
        File file = new File(filePath);
        if (file.exists()) {
            try {
                SAXReader reader = new SAXReader();
                Document doc = reader.read(filePath);

                Element element = (Element) doc.selectSingleNode("/系统配置/栅格数据集/是否提示选择子集");
                if (element != null) {
                    rtn = !element.attributeValue("value").equals("0");
                }
            } catch (DocumentException ignored) {
            }
        }
        return rtn;
    }

    /**
     * 向配置文件中设置是否提示选择遥感hdf4或5文件子集框参数
     *
     * @param value 是否提示选择遥感hdf4或5文件子集框参数
     */
    public static void setAskAgain(boolean value) {
        String filePath = XPath.combine(EnvConfig.getConfigDirectory(SysConfigDirType.Raster), "RsParameterSet.xml");
        File file = new File(filePath);
        if (file.exists()) {
            try {
                XMLWriter writer = new XMLWriter(new FileOutputStream(filePath));
                SAXReader reader = new SAXReader();
                Document doc = reader.read(file);
                Element element = (Element) doc.selectSingleNode("/系统配置/栅格数据集/是否提示选择子集");
                if (element != null) {
                    element.attribute("value").setData(value ? "1" : "0");
                    writer.write(doc);
                    writer.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 从配置文件中读金字塔重采样方式
     *
     * @return 金字塔重采样方式
     */
    public static int getResampleType() {
        int rtn = 0;
        String filePath = XPath.combine(EnvConfig.getConfigDirectory(SysConfigDirType.Raster), "RsParameterSet.xml");
        File file = new File(filePath);
        if (file.exists()) {
            try {
                SAXReader reader = new SAXReader();
                Document doc = reader.read(filePath);

                Element element = (Element) doc.selectSingleNode("/系统配置/栅格数据集/金字塔采样方式");
                if (element != null) {
                    rtn = Integer.parseInt(element.attributeValue("value"));
                }
            } catch (DocumentException ignored) {
            }
        }
        return rtn;
    }

    /**
     * 向配置文件中写金字塔重采样方式
     *
     * @param value 金字塔重采样方式
     */
    public static void setResampleType(int value) {
        String filePath = XPath.combine(EnvConfig.getConfigDirectory(SysConfigDirType.Raster), "RsParameterSet.xml");
        File file = new File(filePath);
        if (file.exists()) {
            try {
                XMLWriter writer = new XMLWriter(new FileOutputStream(filePath));
                SAXReader reader = new SAXReader();
                Document doc = reader.read(file);
                Element element = (Element) doc.selectSingleNode("/系统配置/栅格数据集/金字塔采样方式");
                if (element != null) {
                    element.attribute("value").setData(value);
                    writer.write(doc);
                    writer.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 从配置文件中读金字塔构建提示对话框设置选项
     *
     * @return 金字塔构建提示对话框设置选项
     */
    public static int getBuildPyramidSet() {
        int rtn = 0;
        String filePath = XPath.combine(EnvConfig.getConfigDirectory(SysConfigDirType.Raster), "RsParameterSet.xml");
        File file = new File(filePath);
        if (file.exists()) {
            try {
                SAXReader reader = new SAXReader();
                Document doc = reader.read(file);
                Element element = (Element) doc.selectSingleNode("/系统配置/栅格数据集/金字塔构建提示");
                if (element != null) {
                    rtn = Integer.parseInt(element.attributeValue("value"));
                }
            } catch (Exception ignored) {
            }
        }
        return rtn;
    }

    /**
     * 向配置文件中写金字塔构建提示对话框设置选项
     *
     * @param value 金字塔构建提示对话框设置选项
     */
    public static void setBuildPyramidSet(int value) {
        String filePath = XPath.combine(EnvConfig.getConfigDirectory(SysConfigDirType.Raster), "RsParameterSet.xml");
        File file = new File(filePath);
        if (file.exists()) {
            try {
                XMLWriter writer = new XMLWriter(new FileOutputStream(filePath));
                SAXReader reader = new SAXReader();
                Document doc = reader.read(file);
                Element element = (Element) doc.selectSingleNode("/系统配置/栅格数据集/金字塔构建提示");
                if (element != null) {
                    element.attribute("value").setData(value);
                    writer.write(doc);
                    writer.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 从配置文件中读金字塔压缩方式
     *
     * @return 金字塔压缩方式
     */
    public static int getPyramidCompressMethod() {
        int rtn = 0;
        String filePath = XPath.combine(EnvConfig.getConfigDirectory(SysConfigDirType.Raster), "RsParameterSet.xml");
        File file = new File(filePath);
        if (file.exists()) {
            try {
                SAXReader reader = new SAXReader();
                Document doc = reader.read(file);
                Element element = (Element) doc.selectSingleNode("/系统配置/栅格数据集/Ovr金字塔压缩方式");
                if (element != null) {
                    rtn = Integer.parseInt(element.attributeValue("value"));
                }
            } catch (Exception ignored) {
            }
        }
        return rtn;
    }

    /**
     * 向配置文件中写金字塔压缩方式
     *
     * @param value 金字塔压缩方式
     */
    public static void setPyramidCompressMethod(int value) {
        String filePath = XPath.combine(EnvConfig.getConfigDirectory(SysConfigDirType.Raster), "RsParameterSet.xml");
        File file = new File(filePath);
        if (file.exists()) {
            try {
                XMLWriter writer = new XMLWriter(new FileOutputStream(filePath));
                SAXReader reader = new SAXReader();
                Document doc = reader.read(file);
                Element element = (Element) doc.selectSingleNode("/系统配置/栅格数据集/Ovr金字塔压缩方式");
                if (element != null) {
                    element.attribute("value").setData(value);
                    writer.write(doc);
                    writer.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 从配置文件中读金字塔压缩质量
     *
     * @return 金字塔压缩质量
     */
    public static int getPyramidCompressQuality() {
        int rtn = 0;
        String filePath = XPath.combine(EnvConfig.getConfigDirectory(SysConfigDirType.Raster), "RsParameterSet.xml");
        File file = new File(filePath);
        if (file.exists()) {
            try {
                SAXReader reader = new SAXReader();
                Document doc = reader.read(file);
                Element element = (Element) doc.selectSingleNode("/系统配置/栅格数据集/Ovr金字塔压缩质量");
                if (element != null) {
                    rtn = Integer.parseInt(element.attributeValue("value"));
                }
            } catch (Exception ignored) {
            }
        }
        return rtn;
    }

    /**
     * 向配置文件中写金字塔压缩质量
     *
     * @param value 金字塔压缩质量
     */
    public static void setPyramidCompressQuality(int value) {
        String filePath = XPath.combine(EnvConfig.getConfigDirectory(SysConfigDirType.Raster), "RsParameterSet.xml");
        File file = new File(filePath);
        if (file.exists()) {
            try {
                XMLWriter writer = new XMLWriter(new FileOutputStream(filePath));
                SAXReader reader = new SAXReader();
                Document doc = reader.read(file);
                Element element = (Element) doc.selectSingleNode("/系统配置/栅格数据集/Ovr金字塔压缩质量");
                if (element != null) {
                    element.attribute("value").setData(value);
                    writer.write(doc);
                    writer.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 根据 url 创建图层(返回的图层已经连接数据，因此不需要再次调用 ConnectData())
     * 暂不支持 showHdfSubSelectForm 和 isCancel 参数
     *
     * @param url url
     * @return 创建的图层
     */
    public static MapLayer createMapLayerByUrl(String url, boolean showHdfSubSelectForm/*, out bool isCancel*/) {
        boolean cancel = false;
        String srcURL = url;
        if (url == null || url.isEmpty()) {
            return null;
        }
        url = url.toLowerCase();
        if (url.startsWith("file:///")) {
            url = url.substring("file:///".length());
        }
        if (url.startsWith("/vsihdfs//")) {
            url = String.format("%s%s", "file:///", url);//分布式文件系统文件头加上"file:///"前缀
        }
        MapLayer mapLayer = null;
        if (srcURL.toLowerCase().startsWith("gdbp://")) {
            XClsType clsType = URLParse.getXClsType(srcURL);
            // region GDB数据
            if (url.indexOf("/ras/") > 0) {
                // 栅格图层
                mapLayer = new RasterLayer();
                mapLayer.setURL(url);
            } else if (url.indexOf("/mds/") > 0) {
                // 镶嵌数据集图层
                // TODO: 待添加 MosaicDatasetLayer
                // mapLayer = new MosaicDatasetLayer();
            } else if (url.indexOf("/rcat/") > 0) {
                // 栅格目录
                mapLayer = new RasterCatalogLayer();
                mapLayer.setURL(url);
            } else if (url.indexOf("/ncls/") > 0) {
                // 网络类
                mapLayer = new NetClsLayer();
                mapLayer.setURL(url);
            } else if (url.indexOf("/mapset/") > 0) {
                // 地图集
                // TODO: 待添加 MapSetLayer
                // mapLayer = new MapSetLayer();
            } else if (url.indexOf("/acls/") > 0) {
                // 注记类
                mapLayer = new VectorLayer(VectorLayerType.AnnLayer);
                mapLayer.setURL(url);
            } else if (url.indexOf("/ocls/") > 0) {
                // 对象类
                mapLayer = new ObjectLayer();
                mapLayer.setURL(url);
            } else {
                // 矢量图层
                mapLayer = new VectorLayer(VectorLayerType.SFclsLayer);//简单要素类
                url = url.replace("/s3dfcls/", "/sfcls/");
                StringBuilder sb = new StringBuilder(url);
                int index = sb.indexOf("/s3dfcls/");
                if (index > 0) {
                    sb.delete(index, "/s3dfcls/".length());
                    sb.insert(index, "/sfcls/");
                }
                mapLayer.setURL(sb.toString());
            }

            if (mapLayer != null && !mapLayer.connectData()) {
                mapLayer.dispose();
                mapLayer = null;
            }

            // endregion
        } else {
            // region 文件数据

            if (new File(url).exists())//磁盘文件
            {
                switch (XPath.getExtension(url).toLowerCase()) {
                    case ".wt":
                    case ".wl":
                    case ".wp":
                        mapLayer = new FileLayer6x();
                        mapLayer.setURL("file:///" + url);    //6x图层必须加file前缀才可以打开
                        if (!mapLayer.connectData()) {
                            mapLayer.dispose();
                            mapLayer = null;
                        }
                        break;
                    case ".tdf":
                        mapLayer = createHDFImageLayer(url);
                        if (!mapLayer.connectData()) {
                            mapLayer.dispose();
                            mapLayer = null;
                        }
                        break;
                    case ".hdf"://hdf文件比较特殊，存在两种含义，一种为遥感hdf4文件，一种为MapGIS 瓦片文件，因此先采用遥感打开，若打开失败再使用瓦片打开
                        mapLayer = createRasHdfSubSetLayer(url, showHdfSubSelectForm/*, out isCancel*/);
                        if (mapLayer != null) {
                            return mapLayer;
                        }
                        //采用遥感未打开hdf4文件，此时再使用瓦片hdf图层打开
                        mapLayer = createHDFImageLayer(url);
                        if (!mapLayer.connectData()) {
                            mapLayer.dispose();
                            mapLayer = null;
                        }
                        break;
                    //case ".h5":
                    //case ".nc":
                    //    return CreateRasHdfSubSetLayer(url, showHdfSubSelectForm);
                    // 修改说明：原本所有这些文件都是当简单要素类或栅格数据集处理的，但他们也可能是注记类或对象类等。
                    // 修改人：陈容 2017-06-22
                    case ".e00":
                    case ".mdb":
                    case ".shp":
                    case ".tab":
                    case ".mif":
                    case ".dgn":
                    case ".dxf":
                    case ".vct":
                    case ".kml":
                    case ".gml":
                        //矢量图层
                        mapLayer = new VectorLayer(VectorLayerType.SFclsLayer);//简单要素类
                        mapLayer.setURL("file:///" + url);
                        if (!mapLayer.connectData()) {
                            mapLayer.dispose();
                            mapLayer = new VectorLayer(VectorLayerType.AnnLayer);
                            mapLayer.setURL("file:///" + url);
                            if (!mapLayer.connectData()) {
                                mapLayer.dispose();
                                mapLayer = null;
                            }
                        }
                        break;
                    case ".txt":
                        //矢量图层
                        mapLayer = new VectorLayer(VectorLayerType.SFclsLayer);//简单要素类
                        mapLayer.setURL("file:///" + url);
                        if (!mapLayer.connectData()) {
                            mapLayer.dispose();
                            mapLayer = new VectorLayer(VectorLayerType.AnnLayer);
                            mapLayer.setURL("file:///" + url);
                            if (!mapLayer.connectData()) {
                                mapLayer.dispose();
                                mapLayer = new ObjectLayer();
                                mapLayer.setURL("file:///" + url);
                                if (!mapLayer.connectData()) {
                                    mapLayer.dispose();
                                    mapLayer = null;
                                    return createRasHdfSubSetLayer(url, showHdfSubSelectForm/*, out isCancel*/);
                                }
                            }
                        }
                        break;
                    default:
                        //默认用栅格图层打开
                        return createRasHdfSubSetLayer(url, showHdfSubSelectForm/*, out isCancel*/);
                }
            } else//文件不存在也有可能，例如这种子集URL:  NETCDF:"C:\Documents and Settings\Administrator\桌面\ref_nctest_classic.nc":bb
            {
                return createRasHdfSubSetLayer(srcURL, showHdfSubSelectForm/*, out isCancel*/);
            }

            // endregion
        }

        if (mapLayer != null) {
            String name = "";
            if (new java.io.File(url).exists()) {
                try {
                    name = UtilityTool.autoBreakString(Paths.get(url).getFileName().toString(), UtilityTool.MAX_LENGTH_OF_MAPLAYER_NAME, "gbk");
                } catch (Exception e) {
                    name = Paths.get(url).getFileName().toString();
                }
            } else {
                try {
                    name = UtilityTool.autoBreakString(url.substring(url.lastIndexOf('/') + 1), UtilityTool.MAX_LENGTH_OF_MAPLAYER_NAME, "gbk");
                } catch (Exception e) {
                    name = url.substring(url.lastIndexOf('/') + 1);
                }
            }
            mapLayer.setName(name);
            mapLayer.setState(LayerState.Visible);
            mapLayer = buildPyramidPrompt(mapLayer/*, out isCancel*/);
            return mapLayer;
        } else {
            return null;
        }
    }

    /**
     * 创建hdf4或5文件、nc文件图层和其他栅格文件图层(返回的图层已经连接数据，因此不需要再次调用ConnectData())
     *
     * @param hdfFilePath          文件路径
     * @param showHdfSubSelectForm 当栅格文件存在多个子集时是否弹出选择框进行选择(例如hdf4、hdf5、nc文件可能包含多个子集)，不弹选择框时默认选中第一个子集(提示:仅当此参数为真并且遥感配置中全局提示子集也为真时，才弹出选择子集框)
     * @return 栅格图层，可能为组图层，组图层包含多个hdf4、hdf5或nc文件子集图层
     */
    public static MapLayer createRasHdfSubSetLayer(String hdfFilePath, boolean showHdfSubSelectForm/*, out bool isCancel*/) {
        boolean isCancel = false;
        if (hdfFilePath == null || hdfFilePath.isEmpty()) {
            return null;
        }
        if (hdfFilePath.toLowerCase().startsWith("file:///")) {
            hdfFilePath = hdfFilePath.substring("file:///".length());
        }
        if (hdfFilePath.toLowerCase().startsWith("/vsihdfs//")) {
            hdfFilePath = "file:///" + hdfFilePath;//分布式文件系统文件头加上"file:///"前缀
        }
        File file = new File(hdfFilePath);
        if (file.exists()) {
            ArrayList<HdfSubDataSetItem> selectItems = new ArrayList<>();
            ArrayList<HdfSubDataSetItem> items = new ArrayList<>();
            RasterDataset rds = new RasterDataset();
            if (rds.openByURL("file:///" + hdfFilePath, RasterAccess.Read) > 0) {
                if (rds.hasSubset()) {
                    int subsetNum = rds.subsetNum();
                    for (int i = 0; i < subsetNum; i++) {
                        HdfSubDataSetItem item = new HdfSubDataSetItem();
                        item.setId(i);
                        item.setUrl(rds.getSubsetURL(i));
                        item.setDescription(rds.getSubsetDescription(i));
                        items.add(item);
                        //item.ID = i;
                        //item.URL = rds.GetSubsetURL(i);
                        //item.Description = rds.GetSubsetDescription(i);
                        // items.Add(item);
                    }
                } else {
                    //当栅格文件无子集时，也要打开的，即采用以下方式打开
                    HdfSubDataSetItem item = new HdfSubDataSetItem();
                    item.setId(-1);
                    item.setUrl("file:///"+hdfFilePath);
                    item.setDescription(hdfFilePath);
                    items.add(item);
//                    item.ID = -1;
//                    item.URL = "file:///" + hdfFilePath;
//                    item.Description = hdfFilePath;
//                    items.Add(item);
                }
                rds.close();
            } else {
//                MapGISErrorForm.ShowLastError();
            }
            if (items.size() > 1) {
                if (getAskAgain() && showHdfSubSelectForm) {
                    AddRasHdfSubDataSetDialog form = new AddRasHdfSubDataSetDialog(items);
                    if (form.showAndWait().equals(Optional.of(ButtonType.OK))) {
                        selectItems.addAll(form.m_SelectItems);
                        if (!form.m_bAskAagin) {
                            setAskAgain(form.m_bAskAagin);
                        }
                    }
                    //form.Dispose();
                } else {
                    selectItems.add(items.get(0));
                }
            } else {
                selectItems.addAll(items);
            }
            if (selectItems.size() > 0) {
                ArrayList<RasterLayer> layers = new ArrayList<>();
                String fileName = XPath.getNameWithoutExt(hdfFilePath);
                for (HdfSubDataSetItem item : selectItems) {
                    RasterLayer rasLayer = new RasterLayer();
                    rasLayer.setURL(item.getUrl());
                    if (rasLayer.connectData()) {
                        rasLayer.setState(LayerState.Visible);
                        String name = item.getId() == -1 ? fileName : String.format("%s:%d", fileName, item.getId());
                        try {
                            rasLayer.setName(UtilityTool.autoBreakString(XPath.getNameWithoutExt(name), XString.maxLengthOfMapLayerName, ""));
                        } catch (Exception ex) {
                            rasLayer.setName(name);
                        }
                        layers.add(rasLayer);
                    }
                }
                if (layers.size() > 0) {
                    MapLayer mapLayer = null;
                    if (layers.size() > 1) {
                        GroupLayer groupLayer = new GroupLayer();
                        try {
                            groupLayer.setName(UtilityTool.autoBreakString(XPath.getNameWithoutExt(fileName), XString.maxLengthOfMapLayerName, ""));
                        } catch (Exception ex) {
                            groupLayer.setName(fileName);
                        }
                        for (RasterLayer layer : layers) {
                            groupLayer.append(layer);
                        }
                        mapLayer = groupLayer;
                    } else {
                        mapLayer = layers.get(0);
                    }
                    //修改说明：底层弹框构建金字塔提示改为由上层来弹提示框,解决bug10071
                    //修改人：张凯俊 2018-5-14
                    mapLayer = buildPyramidPrompt(mapLayer/*, out isCancel*/);
                    return mapLayer;
                }
            }
        } else {//不存在也有可能，例如这种子集URL:  NETCDF:"C:\Documents and Settings\Administrator\桌面\ref_nctest_classic.nc":bb
            RasterLayer rasLayer = new RasterLayer();
            rasLayer.setURL(hdfFilePath);
            if (rasLayer.connectData()) {
                rasLayer = (RasterLayer) buildPyramidPrompt(rasLayer/*, out isCancel*/);
                try {
                    rasLayer.setName(UtilityTool.autoBreakString(XPath.getNameWithoutExt(hdfFilePath), XString.maxLengthOfMapLayerName, ""));
                } catch (Exception ex) {
                    rasLayer.setName(hdfFilePath.substring(hdfFilePath.lastIndexOf(File.pathSeparator) + 1));
                }
                rasLayer.setState(LayerState.Visible);
                return rasLayer;
            }
        }
        return null;
    }

    /**
     * 构造MapGISHDF图层(返回的图层需要调用ConnectData())
     *
     * @param url 文件路径
     * @return ImageLayer对象
     */
    public static MapLayer createHDFImageLayer(String url) {
//        if (url == null || url.isEmpty()) {
//            return null;
//        }
//        if (url.toLowerCase().startsWith("file:///")) {
//            url = url.substring("file:///".length());
//        }
//        ImageLayer mapLayer = new ImageLayer();
//        MapServer server = MapServer.CreateInstance(MapServer.GetMapServerType(MapServerType.MapGISHDF));
//        if (server != null) {
//            mapLayer.setMapServer(server);
//            mapLayer.setURL(url);
//        }
//        return mapLayer;
        return null;
    }

    /**
     * 遥感图层构建金字塔提示
     * 暂不支持 out bool isCancel
     *
     * @param mapLayer 图层
     * @return 图层
     */
    private static MapLayer buildPyramidPrompt(MapLayer mapLayer/*, out bool isCancel*/) {
        boolean cancel = false;
        // region 构建金字塔提示框
        if (mapLayer instanceof RasterLayer) {
            RasterDataset rasterDataset = (RasterDataset) mapLayer.getData();
            if (rasterDataset == null) {
                return mapLayer;
            }
            //判断是否要提示构建金字塔
            int n = rasterDataset.getPyramidNum();
            if (n <= 1) {
                // 若行列数大于1024但没有构建金字塔则检查是否需要弹框提示构建金字塔
                if (rasterDataset.getWidth() > 1024 || rasterDataset.getHeight() > 1024) {
                    int buildPyramidSet = MapLayerHelper.getBuildPyramidSet();
                    if (buildPyramidSet == 0) {
                        //弹框提示根据设置决定是否构建本次金字塔并在配置文件保存设置
                        String formText = String.format("为 %s(%dx%d) 构建金字塔", mapLayer.getName(), rasterDataset.getWidth(), rasterDataset.getHeight());
                        CreatePyramidTipDialog createPyramid = new CreatePyramidTipDialog(MapLayerHelper.getResampleType(), MapLayerHelper.getPyramidCompressMethod(), MapLayerHelper.getPyramidCompressQuality(), formText);

                        Optional optional = createPyramid.showAndWait();
                        boolean isBuild = false;//综合判断本次是否构建金字塔
                        if (optional.equals(Optional.of(ButtonType.YES)))
                        {
                            isBuild = true;
                            if (createPyramid.getUseSelectedItem()) {
                                MapLayerHelper.setBuildPyramidSet(1);//始终构建不再提示
                            }
                        }else if (optional.equals(Optional.of(ButtonType.NO))){
                            isBuild = false;
                            if (createPyramid.getUseSelectedItem()) {
                                MapLayerHelper.setBuildPyramidSet(2);//始终不构建永不提示
                            }
                        }else if (optional.equals(Optional.of(ButtonType.CANCEL))){
                            isBuild = false;
                            mapLayer.dispose();
                            mapLayer = null;
                            return mapLayer;
                        }
//                        switch (optional) {
//                            case Optional.of(ButtonType.YES): {
//                                isBuild = true;
//                                if (createPyramid.getUseSelectedItem()) {
//                                    MapLayerHelper.setBuildPyramidSet(1);//始终构建不再提示
//                                }
//                                break;
//                            }
//
//                            case Optional.of(ButtonType.NO): {
//                                isBuild = false;
//                                if (createPyramid.getUseSelectedItem()) {
//                                    MapLayerHelper.setBuildPyramidSet(2);//始终不构建永不提示
//                                }
//                                break;
//                            }
//                            case Optional.of(ButtonType.CANCEL): {
//                                isBuild = false;
//                                mapLayer.dispose();
//                                mapLayer = null;
////                                isCancel = true;
//                                return mapLayer;
//                            }
//                            default:
//                                break;
//                        }
                        if (isBuild) {
                            MapLayerHelper.setResampleType(createPyramid.getResampleType());
                            MapLayerHelper.setPyramidCompressMethod(createPyramid.getCompressMethod());
                            MapLayerHelper.setPyramidCompressQuality(createPyramid.getCompressQuality());
                            rasterDataset.buildPyramidLayer(RasterResampling.valueOf(MapLayerHelper.getResampleType()), 128);
                        }

                    } else if (buildPyramidSet == 1) {
                        //直接构建金字塔不提示
                        rasterDataset.buildPyramidLayer(RasterResampling.valueOf(MapLayerHelper.getResampleType()), 128);
                    } else if (buildPyramidSet == 2) {
                        //始终不构建，永不提示
                    }
                }
            }
        }
        // endregion
        return mapLayer;
    }
}
