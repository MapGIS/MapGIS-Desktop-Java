package com.zondy.mapgis.dataconvert;

import com.zondy.mapgis.analysis.imageanalysis.RSImgTrans_Stru;
import javafx.beans.property.*;

import java.util.Optional;

/**
 * @author CR
 * @file ConvertOption.java
 * @brief 数据转换参数设置
 * @create 2020-03-17.
 */
public class ConvertOption
{
    //region 变量
    private StringProperty srcDrive = new SimpleStringProperty();//源数据类型为栅格文件时,源数据的驱动名
    private StringProperty destDrive = new SimpleStringProperty("MAPGISMSI");//目的数据类型为栅格文件时,目的数据的驱动名
    private StringProperty slib6x = new SimpleStringProperty();//6x系统库 符号库(SLIB)路径,null表示默认
    private StringProperty clib6x = new SimpleStringProperty();//6x系统库 矢量字库(CLIB)路径,/null表示默认
    //private StringProperty flib6x = new SimpleStringProperty();//6x系统库 TFT字库(program),null表示默认
    private StringProperty mifSymbolTable = new SimpleStringProperty();// Mif符号对照表选项(绝对路径)
    private StringProperty dxfSymbolTable = new SimpleStringProperty();//Dxf符号对照表选项(绝对路径)
    private StringProperty dwgVersion = new SimpleStringProperty("2007");// sfcls、ancls导出为dwg时版本
    private BooleanProperty keepNullData = new SimpleBooleanProperty(false);// 保留空数据(只有属性结构,不存在实体)
    private BooleanProperty adjustStrPosition = new SimpleBooleanProperty(false);//注记类->Dxf 调整字符串控制点位置
    private BooleanProperty adjustFontSize = new SimpleBooleanProperty(false);//注记类->Dxf 调整字体大小
    //private BooleanProperty manageInnerIncludeOuterError = new SimpleBooleanProperty(true);//处理区内圈包含外圈错误  //没用到
    private BooleanProperty groupByLayer = new SimpleBooleanProperty(true);//dwg转7x数据时组织方式
    private BooleanProperty txtDemToRas_DataType = new SimpleBooleanProperty(true);// Txt Dem数据(*.txt)转换为栅格数据集时，true为整型，false为浮点型
    private BooleanProperty sfclsToZShp = new SimpleBooleanProperty(false);// sfcls是否导出带有Z坐标的shp文件
    private BooleanProperty appendMode = new SimpleBooleanProperty(false);//是否为追加模式
    private IntegerProperty srsID = new SimpleIntegerProperty(-1);//空间参照系ID,-1表示“保持源空间参照系”
    private IntegerProperty fldInt64Operate = new SimpleIntegerProperty(0);//Int64字段处理选项,0:将int64类型字段转换为字符串字段 1:删除该int64类型字段
    private IntegerProperty mappingWay = new SimpleIntegerProperty(0);//映射方式 0:映射到子图方式 1:块分解方式
    private IntegerProperty pgdbVersion = new SimpleIntegerProperty(2);//导出为ArcGIS Personal GDB时，标识导出的PGDB版本.1标识9.x版本,2表示10.x版本
    private IntegerProperty batRWNum = new SimpleIntegerProperty(-1);//批量读写要素数目阈值

    private RSImgTrans_Stru rasTrans = new RSImgTrans_Stru();//遥感转换参数(是否建立金字塔等参数)
    // 未封装
    //private MapGIS.GeoDataBase.Convert.TableInfo tableInfoExcel = null;//7x->Excel表格转换时参数
    //private MapGIS.GeoDataBase.Convert.TableInfo tableInfoAccess = null;//7x->Access表格转换时参数
    //private MapGIS.GeoDataBase.Convert.TableInfo tableInfoDbf = null;//7x->Dbf表格转换时参数
    //private MapGIS.GeoDataBase.Convert.TableInfo tableInfoTxt = null;//7x->Txt 表格转换时参数
    //private MapGIS.GeoDataBase.Convert.TableInfo tableInfoOcls = null;//表格Table->对象类 转换时参数
    //private MapGIS.GeoDataBase.Convert.Txt27xParamStruct txt27xParam = null;//txt导入为简单要素类参数
    //private InBILParam_Stru bilDemToRas = new InBILParam_Stru();// Bil Dem数据(*.bil)转换为栅格数据集时参数
    //private OutBILParam_Stru rasToDemBil = new OutBILParam_Stru();// 栅格数据集转换为Bil Dem数据(*.bil)时参数
    //endregion

    // 三维转换参数
    private BooleanProperty complex = new SimpleBooleanProperty(false);
    private BooleanProperty overWrite =new SimpleBooleanProperty(true);
    private BooleanProperty global =new SimpleBooleanProperty(false);
    private DoubleProperty posX = new SimpleDoubleProperty(0);//Dot3D pos = new Dot3D(0,0,0);
    private DoubleProperty posY = new SimpleDoubleProperty(0);
    private DoubleProperty posZ = new SimpleDoubleProperty(0);
    private DoubleProperty scaleX = new SimpleDoubleProperty(1);//Dot3D scale= new Dot3D(1,1,1);
    private DoubleProperty scaleY = new SimpleDoubleProperty(1);
    private DoubleProperty scaleZ = new SimpleDoubleProperty(1);
    private DoubleProperty angleX = new SimpleDoubleProperty(0);//Dot3D angle= new Dot3D(0,0,0);
    private DoubleProperty angleY = new SimpleDoubleProperty(0);
    private DoubleProperty angleZ = new SimpleDoubleProperty(0);

    public ConvertOption()
    {
        //bilDemToRas.PixelType = 0;
        //bilDemToRas.EnlargeCeff = 1;
        //bilDemToRas.MoveCeff = 0;
        //
        //rasToDemBil.ByteOrderNo = 0;
        //rasToDemBil.PixelType = 0;
        //rasToDemBil.PixelBits = 32;
        //rasToDemBil.InvalidZValue = 0;
        //rasToDemBil.EnlargeCeff = 1;
        //rasToDemBil.MoveCeff = 0;
    }

    //region 属性Getter & Setter
    public String getSrcDrive()
    {
        return srcDrive.get();
    }

    public StringProperty srcDriveProperty()
    {
        return srcDrive;
    }

    public void setSrcDrive(String srcDrive)
    {
        this.srcDrive.set(srcDrive);
    }

    public String getDestDrive()
    {
        return destDrive.get();
    }

    public StringProperty destDriveProperty()
    {
        return destDrive;
    }

    public void setDestDrive(String destDrive)
    {
        this.destDrive.set(destDrive);
    }

    public String getSlib6x()
    {
        return slib6x.get();
    }

    public StringProperty slib6xProperty()
    {
        return slib6x;
    }

    public void setSlib6x(String slib6x)
    {
        this.slib6x.set(slib6x);
    }

    public String getClib6x()
    {
        return clib6x.get();
    }

    public StringProperty clib6xProperty()
    {
        return clib6x;
    }

    public void setClib6x(String clib6x)
    {
        this.clib6x.set(clib6x);
    }

    //public String getFlib6x()
    //{
    //    return flib6x.get();
    //}
    //
    //public StringProperty flib6xProperty()
    //{
    //    return flib6x;
    //}
    //
    //public void setFlib6x(String flib6x)
    //{
    //    this.flib6x.set(flib6x);
    //}

    public String getMifSymbolTable()
    {
        return mifSymbolTable.get();
    }

    public StringProperty mifSymbolTableProperty()
    {
        return mifSymbolTable;
    }

    public void setMifSymbolTable(String mifSymbolTable)
    {
        this.mifSymbolTable.set(mifSymbolTable);
    }

    public String getDxfSymbolTable()
    {
        return dxfSymbolTable.get();
    }

    public StringProperty dxfSymbolTableProperty()
    {
        return dxfSymbolTable;
    }

    public void setDxfSymbolTable(String dxfSymbolTable)
    {
        this.dxfSymbolTable.set(dxfSymbolTable);
    }

    public String getDwgVersion()
    {
        return dwgVersion.get();
    }

    public StringProperty dwgVersionProperty()
    {
        return dwgVersion;
    }

    public void setDwgVersion(String dwgVersion)
    {
        this.dwgVersion.set(dwgVersion);
    }

    public boolean isKeepNullData()
    {
        return keepNullData.get();
    }

    public BooleanProperty keepNullDataProperty()
    {
        return keepNullData;
    }

    public void setKeepNullData(boolean keepNullData)
    {
        this.keepNullData.set(keepNullData);
    }

    public boolean isAdjustStrPosition()
    {
        return adjustStrPosition.get();
    }

    public BooleanProperty adjustStrPositionProperty()
    {
        return adjustStrPosition;
    }

    public void setAdjustStrPosition(boolean adjustStrPosition)
    {
        this.adjustStrPosition.set(adjustStrPosition);
    }

    public boolean isAdjustFontSize()
    {
        return adjustFontSize.get();
    }

    public BooleanProperty adjustFontSizeProperty()
    {
        return adjustFontSize;
    }

    public void setAdjustFontSize(boolean adjustFontSize)
    {
        this.adjustFontSize.set(adjustFontSize);
    }

    //public boolean isManageInnerIncludeOuterError()
    //{
    //    return manageInnerIncludeOuterError.get();
    //}
    //
    //public BooleanProperty manageInnerIncludeOuterErrorProperty()
    //{
    //    return manageInnerIncludeOuterError;
    //}
    //
    //public void setManageInnerIncludeOuterError(boolean manageInnerIncludeOuterError)
    //{
    //    this.manageInnerIncludeOuterError.set(manageInnerIncludeOuterError);
    //}

    public boolean isGroupByLayer()
    {
        return groupByLayer.get();
    }

    public BooleanProperty groupByLayerProperty()
    {
        return groupByLayer;
    }

    public void setGroupByLayer(boolean groupByLayer)
    {
        this.groupByLayer.set(groupByLayer);
    }

    public boolean isTxtDemToRas_DataType()
    {
        return txtDemToRas_DataType.get();
    }

    public BooleanProperty txtDemToRas_DataTypeProperty()
    {
        return txtDemToRas_DataType;
    }

    public void setTxtDemToRas_DataType(boolean txtDemToRas_DataType)
    {
        this.txtDemToRas_DataType.set(txtDemToRas_DataType);
    }

    public boolean isSfclsToZShp()
    {
        return sfclsToZShp.get();
    }

    public BooleanProperty sfclsToZShpProperty()
    {
        return sfclsToZShp;
    }

    public void setSfclsToZShp(boolean sfclsToZShp)
    {
        this.sfclsToZShp.set(sfclsToZShp);
    }

    public boolean isAppendMode()
    {
        return appendMode.get();
    }

    public BooleanProperty appendModeProperty()
    {
        return appendMode;
    }

    public void setAppendMode(boolean appendMode)
    {
        this.appendMode.set(appendMode);
    }

    public int getSrsID()
    {
        return srsID.get();
    }

    public IntegerProperty srsIDProperty()
    {
        return srsID;
    }

    public void setSrsID(int srsID)
    {
        this.srsID.set(srsID);
    }

    public int getFldInt64Operate()
    {
        return fldInt64Operate.get();
    }

    public IntegerProperty fldInt64OperateProperty()
    {
        return fldInt64Operate;
    }

    public void setFldInt64Operate(int fldInt64Operate)
    {
        this.fldInt64Operate.set(fldInt64Operate);
    }

    public int getMappingWay()
    {
        return mappingWay.get();
    }

    public IntegerProperty mappingWayProperty()
    {
        return mappingWay;
    }

    public void setMappingWay(int mappingWay)
    {
        this.mappingWay.set(mappingWay);
    }

    public int getPgdbVersion()
    {
        return pgdbVersion.get();
    }

    public IntegerProperty pgdbVersionProperty()
    {
        return pgdbVersion;
    }

    public void setPgdbVersion(int pgdbVersion)
    {
        this.pgdbVersion.set(pgdbVersion);
    }

    public int getBatRWNum()
    {
        return batRWNum.get();
    }

    public IntegerProperty batRWNumProperty()
    {
        return batRWNum;
    }

    public void setBatRWNum(int batRWNum)
    {
        this.batRWNum.set(batRWNum);
    }
    //endregion

    public RSImgTrans_Stru getRasTrans()
    {
        return rasTrans;
    }

    public void setRasTrans(RSImgTrans_Stru rasTrans)
    {
        this.rasTrans = rasTrans;
    }

    //三维参数

    public boolean isComplex() {
        return complex.get();
    }

    public BooleanProperty complexProperty() {
        return complex;
    }

    public void setComplex(boolean complex) {
        this.complex.set(complex);
    }

    public boolean isOverWrite() {
        return overWrite.get();
    }

    public BooleanProperty overWriteProperty() {
        return overWrite;
    }

    public void setOverWrite(boolean overWrite) {
        this.overWrite.set(overWrite);
    }

    public boolean isGlobal() {
        return global.get();
    }

    public BooleanProperty globalProperty() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global.set(global);
    }

    public double getPosX() {
        return posX.get();
    }

    public DoubleProperty posXProperty() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX.set(posX);
    }

    public double getPosY() {
        return posY.get();
    }

    public DoubleProperty posYProperty() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY.set(posY);
    }

    public double getPosZ() {
        return posZ.get();
    }

    public DoubleProperty posZProperty() {
        return posZ;
    }

    public void setPosZ(double posZ) {
        this.posZ.set(posZ);
    }

    public double getScaleX() {
        return scaleX.get();
    }

    public DoubleProperty scaleXProperty() {
        return scaleX;
    }

    public void setScaleX(double scaleX) {
        this.scaleX.set(scaleX);
    }

    public double getScaleY() {
        return scaleY.get();
    }

    public DoubleProperty scaleYProperty() {
        return scaleY;
    }

    public void setScaleY(double scaleY) {
        this.scaleY.set(scaleY);
    }

    public double getScaleZ() {
        return scaleZ.get();
    }

    public DoubleProperty scaleZProperty() {
        return scaleZ;
    }

    public void setScaleZ(double scaleZ) {
        this.scaleZ.set(scaleZ);
    }

    public double getAngleX() {
        return angleX.get();
    }

    public DoubleProperty angleXProperty() {
        return angleX;
    }

    public void setAngleX(double angleX) {
        this.angleX.set(angleX);
    }

    public double getAngleY() {
        return angleY.get();
    }

    public DoubleProperty angleYProperty() {
        return angleY;
    }

    public void setAngleY(double angleY) {
        this.angleY.set(angleY);
    }

    public double getAngleZ() {
        return angleZ.get();
    }

    public DoubleProperty angleZProperty() {
        return angleZ;
    }

    public void setAngleZ(double angleZ) {
        this.angleZ.set(angleZ);
    }
}
