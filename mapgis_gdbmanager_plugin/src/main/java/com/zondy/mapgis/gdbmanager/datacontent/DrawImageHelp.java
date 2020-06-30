package com.zondy.mapgis.gdbmanager.datacontent;

import com.zondy.mapgis.geodatabase.AnnotationCls;
import com.zondy.mapgis.geodatabase.IBasCls;
import com.zondy.mapgis.geodatabase.SFeatureCls;
import com.zondy.mapgis.geodatabase.config.EnvConfig;
import com.zondy.mapgis.geodatabase.net.NetCls;
import com.zondy.mapgis.geodatabase.raster.MosaicDataset;
import com.zondy.mapgis.geodatabase.raster.RasterCatalog;
import com.zondy.mapgis.geodatabase.raster.RasterDataset;
import com.zondy.mapgis.geometry.Rect;
import com.zondy.mapgis.info.LinInfo;
import com.zondy.mapgis.map.*;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.File;

/**
 * 绘制图片辅助类
 */
public class DrawImageHelp {
    private static int imgWidth = 200; //缺省的图片尺寸
    private static int imgHeight = 160; //缺省的图片尺寸
    private static String baseDir = "";//缓存路径

    /// <summary>
    /// 缓存路径
    /// </summary>
    public static String getBaseDir() {
        File file = new File(DrawImageHelp.baseDir);
        if (!file.exists()) {
            String dir = EnvConfig.getGisEnv().getTemp();
            File dirFile = new File(dir);

            if (!dirFile.exists()) {
                String path = DrawImageHelp.class.getProtectionDomain().getCodeSource().getLocation().getFile();
                try {
                    path = java.net.URLDecoder.decode(path + File.separator + "temp\\GDBImageBuffer", "UTF-8"); // 转换处理中文及空格
                } catch (java.io.UnsupportedEncodingException e) {

                }
            } else {
                dir += File.separator + "GDBImageBuffer";
            }
            DrawImageHelp.baseDir = dir;
            return dir;
        } else {
            return DrawImageHelp.baseDir;
        }
    }

    /// <summary>
    /// 创建图片缓存目录
    /// </summary>
    public static void createTempFolder() {
        File file = new File(baseDir);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /// <summary>
    /// 绘制地理数据图片
    /// </summary>
    /// <param name="layer">图层</param>
    /// <returns>缩略图的字节数组表示形式</returns>
    public static byte[] drawMemImage(MapLayer layer) {
        return drawMemImage(layer, DrawImageHelp.imgWidth, DrawImageHelp.imgHeight);
    }

    /// <summary>
    /// 绘制地理数据图片
    /// </summary>
    /// <param name="layer">图层</param>
    /// <param name="size">给定的图片大小</param>
    /// <returns>缩略图的字节数组表示形式</returns>
    public static byte[] drawMemImage(MapLayer layer, int width, int height) {
        byte[] buf = null;
        if (layer != null) {
            boolean bValid = layer.getIsValid();
            if (!bValid && layer instanceof VectorLayer) {
                String url = layer.getURL();
                File file = new File(url);
                if (file.exists()) {
                    bValid = true;
                }
            }
            if (bValid && width > 0 && height > 0) {
                GridImageInfo gIInfo = new GridImageInfo();
                gIInfo.setImageType(ImageType.PNG);
                gIInfo.setWidth(width);
                gIInfo.setHeight(height);
                gIInfo.setBackgroudColor(9);
                gIInfo.setBufCapacity(width * height * 4 + 512);
                gIInfo.setCacgWidth(width);
                gIInfo.setCachHeight(height);
                gIInfo.setJpgRate(95);
                gIInfo.setGifTranFlag((short) 1);//true
                gIInfo.setChgPal(false);
                gIInfo.setQualityMode(QualityMode.LowQuality);
                gIInfo.setCount(1);

                Display dsp = new Display();
                Transformation transformation = null;
                transformation = new Transformation();
                dsp.setTransformation(transformation);
                Rect rt = new Rect();
                rt.setXMin(0);
                rt.setXMax(width);
                rt.setYMin(0);
                rt.setYMax(height);
                Transformation trans = dsp.getTransformation();
                if (trans != null) {
                    trans.setDeviceRect(rt);
                    trans.setDisplayRect(layer.getRange());
                    trans.setClientRect(rt);
                }
                dsp.setIsSymbolic(true);
                if (layer instanceof VectorLayer) {
                    double scale = ((VectorLayer) layer).getScaleOfSymbolSize();
                    dsp.setSymbolScale(scale);
                }
                dsp.createGridImageDevice(gIInfo);
                dsp.begin();
                layer.drawBase(dsp);
                dsp.end();
                if (gIInfo.getBufList() != null && gIInfo.getBufList().length > 0) {
                    buf = gIInfo.getBufList()[0];
                }
                dsp.dispose();
            }
        }
        return buf;
    }

    /// <summary>
    /// 缺省尺寸绘制图片
    /// </summary>
    /// <param name="layer">图层</param>
    /// <returns>缩略图</returns>
    public static Image drawImage(MapLayer layer) {
        return drawImage(layer, DrawImageHelp.imgWidth, DrawImageHelp.imgHeight);
    }

    /// <summary>
    /// 绘制地理数据图片
    /// </summary>
    /// <param name="layer">图层</param>
    /// <param name="size">给定的图片大小</param>
    /// <returns>缩略图</returns>
    public static Image drawImage(MapLayer layer, int width, int height) {
        Image img = null;
        byte[] buf = drawMemImage(layer, width, height);
        if (buf != null && buf.length > 0) {
            ByteArrayInputStream stream = new ByteArrayInputStream(buf);
            img = new Image(stream);
        }
        return img;
    }

    /// <summary>
    /// 缺省尺寸绘制图片
    /// </summary>
    /// <param name="xcls">数据类</param>
    /// <returns>缩略图</returns>
    public static Image drawImage(IBasCls xcls) {
        return drawImage(xcls, DrawImageHelp.imgWidth, DrawImageHelp.imgHeight);
    }

    /// <summary>
    /// 通过缓存来绘制图片
    /// </summary>
    /// <param name="xcls">数据类</param>
    /// <param name="size">给定的图片大小</param>
    /// <returns>缩略图</returns>
    public static Image drawImage(IBasCls xcls, int width, int height) {
        Image img = null;
        if (xcls != null) {
            MapLayer layer = null;
            if (xcls instanceof SFeatureCls) {
//region 简单要素类

                SFeatureCls sfcls = (SFeatureCls) xcls;
                layer = new VectorLayer(VectorLayerType.SFclsLayer);
                layer.attachData(xcls);

                //endregion
            } else if (xcls instanceof AnnotationCls) {
                //region 注记类

                AnnotationCls acls = (AnnotationCls) xcls;
                layer = new VectorLayer(VectorLayerType.AnnLayer);
                layer.attachData(xcls);

                //endregion
            }
//                    else if (xcls instanceof MapSet)
//            {
//                        #region 地图集
//
//                MapSet ms = xcls as MapSet;
//                layer = new MapSetLayer();
//                layer.AttachData(ms);
//
//                        #endregion
//            }
            else if (xcls instanceof RasterCatalog) {
                //region 栅格目录

                RasterCatalog rc = (RasterCatalog) xcls;
                layer = new RasterCatalogLayer();
                layer.attachData(rc);

                //endregion
            } else if (xcls instanceof RasterDataset) {
                //region 绘制栅格数据集

                RasterDataset rds = (RasterDataset) xcls;
                layer = new RasterLayer();
                layer.attachData(xcls);

                //endregion
            } else if (xcls instanceof NetCls) {
                //region 绘制网络类

                NetCls cls = (NetCls) xcls;
                layer = new NetClsLayer();
                layer.attachData(xcls);

                //endregion
            } else if (xcls instanceof MosaicDataset) {
                //region 绘制镶嵌数据集(实际上绘制的是轮廓线)
                //修改说明：为保持镶嵌数据集的缩略图和镶嵌数据集图层在地图视图显示的一致，需手动修改填充模式及边线颜色等参数
                //          考虑到直接绘制镶嵌数据集可能比较耗时故绘制轮廓线代替。由于底层接口有些问题，如new一个镶嵌数据集矢量图层
                //          设置填充模式及边线颜色属性无效，故new的是一般简单要素类矢量图层。
                //修改人：张凯俊 2018-4-28
                MosaicDataset mds = (MosaicDataset) xcls;
                SFeatureCls sfcls = mds.getCatalog();
                VectorLayer vlayer = new VectorLayer(VectorLayerType.SFclsLayer);
                vlayer.attachData(sfcls);
                vlayer.setIsFillRegion(false);
                vlayer.setIsDisplayRegionBorder(true);
                //vlayer.FillType = 1;
                LinInfo linInfo = new LinInfo();
                //LinAdjustType????LinHeadType???LinJointType???
                //linInfo.setAdjustFlg(LinAdjustType.NoAdjust);
                //linInfo.setHeadType(LinHeadType.Round);
                //linInfo.setJointType(LinJointType.Round);
                linInfo.setLibID((short) 0);
                linInfo.setLinStyID(0);
                //linInfo.setMakeMethod(LinStyleMakeType.Bypoint);
                linInfo.setOvprnt(false);// = false;
                //linInfo.OutClr[0] = 7;
                //linInfo.OutClr[1] = 4;
                //linInfo.OutClr[2] = 3;
                linInfo.setOutClr1(7);
                linInfo.setOutClr2(4);
                linInfo.setOutClr3(3);
//                linInfo.OutClr = new int[] { 7, 4, 3 };
                //linInfo.OutPenW[0] = 0.0f;
                //linInfo.OutPenW[1] = 0.0f;
                //linInfo.OutPenW[2] = 0.0f;
                linInfo.setXScale(0.0f);
                linInfo.setYScale(0.0f);
                vlayer.setRegionBorderLinInfo(linInfo);
                if (vlayer != null) {
                    img = drawImage(vlayer, width, height);
                    vlayer.detachData();
                    vlayer.dispose();
                }
                return img;
                //endregion
            }

            if (layer != null) {
                img = drawImage(layer, width, height);
                boolean rtn = layer.detachData();
                if (rtn) {
                    layer.dispose();
                }
            }
        }
        return img;
    }
}
