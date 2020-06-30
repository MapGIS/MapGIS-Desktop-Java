package com.zondy.mapgis.common;

import com.zondy.mapgis.geodatabase.XClsType;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class CommonFuns {
    public static Image getImageByColor(Color color, int width, int height) {
//        BufferedImage bufImage = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
//        int[] rgb = new int[width*height];
//        for(int i=0;i<rgb.length;i++){
//            rgb[i]= Color.RED.
//        }
//        bufImage.setRGB(0, 0, width, height, rgb/*数组*/, 0, width);
        WritableImage writableImage = new WritableImage(width, height);
//        PixelReader pixelReader = image.getPixelReader();
        PixelWriter pixelWriterGray = writableImage.getPixelWriter();

        for (int i = 0; i < width; i++) {
            //                int colorVal = pixelReader.getArgb(i, j);
            //
            //                int r = (color >> 16) & 0xff;
            //                int g = (color >> 8) & 0xff;
            //                int b = color & 0xff;
            for (int j = 0; j < height; j++) {
                pixelWriterGray.setColor(i, j, color);
            }
        }
        Image image = writableImage;
        return image;
    }


    /**
     * 从URL中取表示数据类型的字符串，并根据其获取数据类型
     * @param url 数据的URL
     * @return 数据类型
     */
    public static XClsType getTypeFromUrl(String url)
    {
        XClsType clsType =  XClsType.XSFCls;
        if (url != null && !url.isEmpty())
        {
            if (url.toLowerCase().startsWith("gdbp://"))
            {
                String[] names = url.substring(7).split("/");
                if (names.length == 4 || names.length == 6)//GDBP://server/database/sfcls/cls1 或者 GDBP://server/database/ds/dataset/sfcls/cls1
                {
                    String strType = names[names.length - 2].toLowerCase();
                    clsType = getTypeFromClsTypeStr(strType);
                }
            }
            else if (url.toLowerCase().startsWith("file:///"))
                clsType = XClsType.XSFCls;
        }
        return clsType;
    }

    /**
     * 从URL中取表示数据类型的字符串，并根据其获取数据类型
     * @param typeStr 类型字符串
     * @return
     */
    public static XClsType getTypeFromClsTypeStr(String typeStr)
    {
        XClsType clsType = XClsType.XSFCls;
        switch (typeStr)
        {
            case "ds":
                clsType = XClsType.XFds;
                break;
            case "sfcls":
                clsType = XClsType.XSFCls;
                break;
            case "acls":
                clsType = XClsType.XACls;
                break;
            case "ocls":
                clsType = XClsType.XOCls;
                break;
            case "rcls":
                clsType = XClsType.XRCls;
                break;
            case "ncls":
                clsType = XClsType.XGNet;
                break;
            case "mapset":
                clsType = XClsType.XMapSet;
                break;
            case "rcat":
                clsType = XClsType.XRcat;
                break;
            case "ras":
                clsType = XClsType.XRds;
                break;
            case "mds":
                clsType = XClsType.XMosaicDS;     //镶嵌数据集支持ch 20170122
                break;
            default:
                break;
        }
        return clsType;
    }
    /**
     * 获取URL中表示数据类型的部分的字符串，如sfcls、acls等
     * @param clsType 数据类型
     * @return 表示数据类型的字符串，如sfcls、acls等
     */
    public static String getTypeUrl(XClsType clsType)
    {
        String typeUrl = "";
        if (XClsType.XACls.equals(clsType)) {
            typeUrl = "acls";
        } else if (XClsType.XFds.equals(clsType)) {
            typeUrl = "ds";
        } else if (XClsType.XGNet.equals(clsType)) {
            typeUrl = "ncls";
        } else if (XClsType.XMapSet.equals(clsType)) {
            typeUrl = "mapset";
        } else if (XClsType.XOCls.equals(clsType)) {
            typeUrl = "ocls";
        } else if (XClsType.XRCls.equals(clsType)) {
            typeUrl = "rcls";
        } else if (XClsType.XRcat.equals(clsType)) {
            typeUrl = "rcat";
        } else if (XClsType.XRds.equals(clsType)) {
            typeUrl = "ras";
        } else if (XClsType.XMosaicDS.equals(clsType)) {
            typeUrl = "mds";
        } else if (XClsType.XSFCls.equals(clsType)) {
            typeUrl = "sfcls";
        }
        return typeUrl;
    }
}
