package com.zondy.mapgis.common;

import com.zondy.mapgis.geometry.Dot;
import com.zondy.mapgis.geometry.GeomType;
import com.zondy.mapgis.geometry.Rect;
import com.zondy.mapgis.geometry.TextFormat;
import com.zondy.mapgis.info.*;
import com.zondy.mapgis.info.GeomInfo;
import com.zondy.mapgis.map.*;
import com.zondy.mapgis.systemlib.ColorLibrary;
import com.zondy.mapgis.systemlib.SymbolGeomType;
import com.zondy.mapgis.systemlib.SystemLibrary;
import com.zondy.mapgis.systemlib.SystemLibrarys;

import java.util.UUID;

public class DrawSymbolItem {
//    public static SystemLibrary getDefaultSymbolLibrary() {
//        SystemLibrarys sysLibs = SystemLibrarys.getSystemLibrarys();
//        if (sysLibs != null) {
//            return SystemLibrarys.getSystemLibrarys().getDefaultSystemLibrary();
////            return sysLibs.getSystemLibrary2(new UUID());
//        }
//        return null;
//    }

    public static UUID convertGUID2UUID(UUID guid)
    {
        UUID uuid = null;
        String guidStr = guid.toString();
        String str1=guidStr.substring(0,8);
        String str2=guidStr.substring(8,12);
        String str3=guidStr.substring(12,16);
        String str4=guidStr.substring(16,20);
        String str5=guidStr.substring(20);

        String uuidStr = String.format("%s-%s-%s-%s-%s",str1,str2,str3,str4,str5);
        try{
            uuid = UUID.fromString(uuidStr);
        }catch (IllegalArgumentException e){
        }

        return uuid;
    }
    /// <summary>
    /// 绘制二维符号
    /// </summary>
    /// <param name="symbolType">符号几何类型</param>
    /// <param name="no">符号编号</param>
    /// <param name="subNo">符号子编号</param>
    /// <param name="size">图片大小</param>
    /// <param name="backColor">背景色</param>
    /// <param name="sysLibGuid">符号所在系统库Guid</param>
    /// <returns>图片流buffer</returns>
    public static byte[] drawSymbol(SymbolGeomType symbolType, int no, int subNo, int width, int height, int backColor, UUID sysLibGuid) {
        Boolean rtn = false;
        byte[] buffer = null;
        SystemLibrary sysLib = SystemLibrarys.getSystemLibrarys().getSystemLibrary(sysLibGuid);
//        String guidStr = sysLib.getGuid();
        int clrNo0 = 12, clrNo1 = 12, clrNo2 = 12;
        if (sysLib != null) {
            ColorLibrary clrLib = sysLib.getColorLibarary(); //getColorLibrary???
            if (clrLib != null) {
                clrNo0 = -2/*clrLib.FindNearColorNoByRgb(190, 190, 190)*/;
                clrNo1 = -3/*clrLib.FindNearColorNoByRgb(140, 140, 140)*/;
                clrNo2 = -4/*clrLib.FindNearColorNoByRgb(90, 90, 90)*/;
            }
        }
        int[] outClr = new int[]{clrNo0, clrNo1, clrNo2};
        float[] outPenW = new float[]{0.05f, 0.05f, 0.05f};
        if (symbolType == SymbolGeomType.GeomPnt) {
            PntInfo pntInfo = new PntInfo();
            pntInfo.setSymID(no);
            pntInfo.setFillFlg((short) 1);
            pntInfo.setBackClr(9);
            pntInfo.setAngle(0);
            pntInfo.setWidth(width - 4);
            pntInfo.setHeight(height - 4);
            pntInfo.setOutClr1(outClr[0]);
            pntInfo.setOutClr2(outClr[1]);
            pntInfo.setOutClr3(outClr[2]);
            pntInfo.setOutPenW1(outPenW[0]);
            pntInfo.setOutPenW1(outPenW[1]);
            pntInfo.setOutPenW1(outPenW[2]);
            buffer = drawPntSymbol(pntInfo, width, height, backColor, sysLibGuid, rtn);
        } else if (symbolType == SymbolGeomType.GeomLin) {
            LinInfo linInfo = new LinInfo();
            linInfo.setLinStyID(no);
            linInfo.setLibID((short) subNo);
            linInfo.setAdjustFlg(LinAdjustType.NoAdjust);
            linInfo.setXScale(width);
            linInfo.setYScale(height);
            linInfo.setOutClr1(outClr[0]);
            linInfo.setOutClr2(outClr[1]);
            linInfo.setOutClr3(outClr[2]);
            linInfo.setOutPenW1(outPenW[0]);
            linInfo.setOutPenW1(outPenW[1]);
            linInfo.setOutPenW1(outPenW[2]);
            buffer = drawLinSymbol(linInfo, width, height, backColor, sysLibGuid, rtn);
        } else if (symbolType == SymbolGeomType.GeomReg) {
            RegInfo rInfo = new RegInfo();
            rInfo.setAngle(0);
            rInfo.setFillClr(backColor);
            rInfo.setPatClr(outClr[0]);
            rInfo.setPatID(no);
            rInfo.setPatHeight(height - 4);
            rInfo.setPatWidth(width - 4);
            rInfo.setOvprnt(false);
            rInfo.setFillMode((short) 0);
            rInfo.setFullPatFlg((short) 1);
            rInfo.setOutPenW((double) 0.05);
            buffer = drawRegSymbol(rInfo, width, height, backColor, sysLibGuid, rtn);
        }
        return buffer;
    }

    public static byte[] drawSymbol(GeomInfo gInfo, int width, int height, int backColor, UUID sysLibGuid) {
        byte[] buffer = null;
        if (gInfo != null) {
            Boolean bRtn = new Boolean(false);
            GeomType type = gInfo.getType();
            if (type == GeomType.GeomPnt) {
                buffer = drawPntSymbol((PntInfo) gInfo, width, height, backColor, sysLibGuid, bRtn);
            } else if (type == GeomType.GeomLin) {
                buffer = drawLinSymbol((LinInfo) gInfo, width, height, backColor, sysLibGuid, bRtn);
            } else if (type == GeomType.GeomReg) {
                buffer = drawRegSymbol((RegInfo) gInfo, width, height, backColor, sysLibGuid, bRtn);
            } else if (type == GeomType.GeomAnn) {
//                buffer = DrawText((TextAnnInfo)gInfo, width,height, Resources.String_T, backColor, sysLibGuid, StringAlignment.Near);
            }
        }
        return buffer;
    }

    //绘制符号代码
    //绘制点符号
    public static byte[] drawPntSymbol(PntInfo gInfo, int width, int height, int backColor, UUID sysLibGuid, Boolean rtn) {
        byte[] buf = null;
        rtn = false;
        if (gInfo != null && width > 0 && height > 0)
            buf = Display.pntSymbolOutputToImageFile(gInfo, width, height, ImageType.PNG, true, backColor, true, sysLibGuid);
        if (buf != null && buf.length > 0)
            rtn = true;
        return buf;
    }

    //绘制线符号
    public static byte[] drawLinSymbol(LinInfo gInfo, int width, int height, int backColor, UUID sysLibGuid, Boolean rtn) {
        byte[] buf = null;
        rtn = false;
        if (gInfo != null && width > 0 && height > 0)
            buf = Display.linSymbolOutputToImageFile(gInfo, width, height, ImageType.PNG, true, backColor, true, sysLibGuid);
        if (buf != null && buf.length > 0)
            rtn = true;
        return buf;
    }

    //绘制区符号
    public static byte[] drawRegSymbol(RegInfo gInfo, int width, int height, int backColor, UUID sysLibGuid, Boolean rtn) {
        byte[] buf = null;
        rtn = false;
        if (gInfo != null && width > 0 && height > 0)
            buf = Display.regSymbolOutputToImageFile(gInfo, width, height, ImageType.PNG, true, backColor, true, sysLibGuid);
        if (buf != null && buf.length > 0)
            rtn = true;
        return buf;
    }

    public static byte[] DrawText(TextAnnInfo gInfo, int width, int height, String text, int backColor, UUID sysLibGuid, Boolean rtn) {
//        if (stringAlignment == StringAlignment.Center)
//        {
//            byte[] buf = Display.annSymbolOutputToImageFile(gInfo, text, width, height, ImgType.PNG, true, backColor, true, sysLibGuid);
//            return buf;
//        }
//        else
//        {
        rtn = false;
        byte[] buf = null;
        if (gInfo != null && width > 0 && height > 0) {
            GridImageInfo imgInfo = new GridImageInfo();
            imgInfo.setImageType(ImageType.PNG);
            imgInfo.setWidth(width);
            imgInfo.setHeight(height);
            imgInfo.setBackgroudColor(backColor);
            imgInfo.setBufCapacity(width * height * 4 + 512);
            imgInfo.setCount(1);
            imgInfo.setCacgWidth(width);//CachWid = size.Width;
            imgInfo.setCachHeight(height);
            imgInfo.setJpgRate(95);
            imgInfo.setGifTranFlag((short) 1);
            imgInfo.setChgPal(true);
            imgInfo.setQualityMode(QualityMode.LowQuality);

            Display display = new Display();
            Transformation trans = display.getTransformation();
            Rect devrc = new Rect(0, 0, width - 1, height - 1);
            Rect disprc = new Rect(0, 0, width, height);
            trans.setMapRange(devrc);
            trans.setClientRect(devrc);
            trans.setDeviceRect(devrc);
            trans.setDisplayRect(disprc);
            display.createGridImageDevice(imgInfo);
            display.setIsSymbolic(true);

            display.begin();
            display.setBrush(9, 1, 1, 1, 1f);
            display.setIsFixedAnnSize(true);
            display.setSymbolScale(1);
            display.setUnitMode(UnitMode.Pixel);//DisplayHint.Normal
//                display.setSystemLib();
            //Dot dot = gInfo.IsHzpl ? new Dot(0, 1.0 / 6 * size.Height) : new Dot(1.0 / 6 * size.Width, size.Height - 1);
            Dot dot = gInfo.isHZpl() ? new Dot(0, (height - gInfo.getHeight()) / 2.0) : new Dot((width - gInfo.getWidth()) / 2.0, height - 1);
            display.setPen(1, gInfo.getColor());
            //bRtn = display.DispText(dot, text, gInfo);
            rtn = display.drawText(dot, text, new TextFormat(gInfo.getHandle()));
//                rtn = display.drawText(dot, text, gInfo.getHeight(),gInfo.getWidth(),gInfo.getIfnt(),gInfo.getIfnx(),gInfo.getAngle(),
//                        gInfo.getSpace(),gInfo.isHZpl(),gInfo.getBackClr(),gInfo.getBackexp(),gInfo.isFilled());
            display.end();
            if (rtn)
                buf = imgInfo.getBufList()[0];
            display.dispose();
//            }
        }
        return buf;
//        }
    }

}
