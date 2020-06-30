package com.zondy.mapgis.dataconvert;

import com.zondy.mapgis.base.EnumUtils;
import com.zondy.mapgis.base.XPath;
import com.zondy.mapgis.base.XString;
import com.zondy.mapgis.common.URLParse;
import com.zondy.mapgis.dataconvert.option.UnificationDialog;
import com.zondy.mapgis.geodatabase.SFClsInfo;
import com.zondy.mapgis.geodatabase.SFeatureCls;
import com.zondy.mapgis.geodatabase.Server;
import com.zondy.mapgis.geometry.GeomType;
import javafx.beans.property.*;

import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据转换列表项
 *
 * @author CR
 * @file ConvertItem.java
 * @brief 数据转换列表项
 * @create 2020-03-10.
 */
public class ConvertItem
{
    private ObjectProperty<ConvertState> state = new SimpleObjectProperty<>(ConvertState.READY);
    private ObjectProperty<DataType> sourType = new SimpleObjectProperty<>(DataType.UNKNOWN);
    private ObjectProperty<DataType> destType = new SimpleObjectProperty(DataType.UNKNOWN);
    private ObjectProperty<ConvertOption> convertOption = new SimpleObjectProperty<>(new ConvertOption());
    private ObjectProperty<ErrorType> errorType = new SimpleObjectProperty<>(ErrorType.UNCHECK);//错误信息
    private List<DataType> destTypes = new ArrayList<>();
    private String sourPath;
    private StringProperty sourName = new SimpleStringProperty();
    private StringProperty destName = new SimpleStringProperty();
    private StringProperty destPath = new SimpleStringProperty("GDBP://MapGISLocal/DC");
    private DoubleProperty progress = new SimpleDoubleProperty(0.0);
    private GeomType geoType = GeomType.GeomUnknown;
    private String driveName = null;
    

    public ConvertItem(String sourPath)
    {
        this(sourPath, "");
    }

    public ConvertItem(String sourPath, String destDir)
    {
        //TODO: 根据sourPath获取上面各属性
        this.sourPath = sourPath;
        if (!XString.isNullOrEmpty(this.sourPath))
        {
            if (this.sourPath.toLowerCase().startsWith(CustomOperate.gdbProName))
            {
                String strType = URLParse.getXClsTypeString(this.sourPath);
                if (XString.isNullOrEmpty(strType))
                {
                    String[] strs = XString.splitRemoveEmpty(this.sourPath.substring(CustomOperate.gdbProName.length()), "/");
                    if (strs.length == 2)
                    {
                        this.setSourType(DataType.MAPGIS_GDB);
                    }
                } else
                {
                    this.sourType.set(this.getDataType(strType));
                    if ("sfcls".equalsIgnoreCase(strType))
                    {
                        SFeatureCls cls = new SFeatureCls();
                        if (cls.openByURL(this.sourPath) > 0)
                        {
                            SFClsInfo clsInfo = (SFClsInfo) cls.getClsInfo();
                            if (clsInfo != null)
                            {
                                geoType = clsInfo.getfType();
                                if (GeomType.GeomPnt.equals(geoType))
                                {
                                    this.setSourType(DataType.MAPGIS_SFCLSP);
                                } else if (GeomType.GeomLin.equals(geoType))
                                {
                                    this.setSourType(DataType.MAPGIS_SFCLSL);
                                } else if (GeomType.GeomReg.equals(geoType))
                                {
                                    this.setSourType(DataType.MAPGIS_SFCLSR);
                                } else if (GeomType.GeomSurface.equals(geoType))
                                {
                                    this.setSourType(DataType.MAPGIS_SFCLSS);
                                } else if (GeomType.GeomEntity.equals(geoType))
                                {
                                    this.setSourType(DataType.MAPGIS_SFCLSE);
                                }
                            }
                        }
                        cls.close();
                    }
                }
            } else if (this.sourPath.toLowerCase().startsWith(CustomOperate.tableProName))
            {
                String str = this.sourPath.substring(CustomOperate.tableProName.length());
                int intLast = str.lastIndexOf('/');
                if (intLast >= 0)
                {
                    str = XString.remove(str, intLast);
                }
                String ext = XPath.getExtension(str);
                this.setSourType(this.getDataType(ext));
            } else
            {
                String str = this.sourPath;
                if (this.sourPath.toLowerCase().startsWith(CustomOperate.demProName))
                {
                    str = this.sourPath.substring(CustomOperate.demProName.length());
                }
                String ext = XPath.getExtension(str);
                if (".txt".equals(ext) && this.sourPath.toLowerCase().startsWith(CustomOperate.demProName))
                {
                    //this.setSourType(DataType.DEM_TXT);
                } else if (ext == "")
                {
                    File file = new File(str);
                    if (file.isDirectory())
                    {
                        this.setSourType(DataType.ARCINFO_COVERAGE);
                    }
                } else
                {
                    this.setSourType(this.getDataType(ext));
                }

                //region 判断栅格文件，获取Drive
                String driveName = "";
                for (String key : CustomOperate.driveExtMap.keySet())
                {
                    List<String> vals = CustomOperate.driveExtMap.get(key);
                    if (vals.contains(ext))
                    {
                        driveName = key;
                        break;
                    }
                }
                //暂时屏蔽，底层报错
                //RasterDataSet rds = new RasterDataSet();
                //if (rds.openByURL(this.sourPath, RasterAccess.Read) > 0)
                //{
                //    driveName = rds.getDatasetDriverName();
                //    rds.close();
                //}

                if (XString.isNullOrEmpty(driveName))
                {
                    this.convertOption.get().setSrcDrive(driveName);
                }
                //endregion
            }

            this.calcDestTypes();

            this.sourName.set(this.getNameWithoutExtension(this.sourPath));
            this.destName.set(this.sourName.get());
        }

        errorType.addListener((o, ov, nv) ->
        {
            if (errorType.get() == ErrorType.UNCHECK)
            {
                setState(ConvertState.READY);
            } else if (errorType.get() == ErrorType.NOERROR)
            {
                setState(ConvertState.SUCCEED);
            } else if (errorType.get() == ErrorType.CONVERTERROR)
            {
                setState(ConvertState.FAILED);
            } else
            {
                setState(ConvertState.WARNING);
            }
        });

        if (!XString.isNullOrEmpty(destDir))
        {
            this.setDestPath(destDir);
        } else
        {
            switch (destType.get())
            {
                case MAPGIS_SFCLSP:
                case MAPGIS_SFCLSL:
                case MAPGIS_SFCLSR:
                case MAPGIS_SFCLSS:
                case MAPGIS_SFCLSE:
                case MAPGIS_SFCLS:
                case MAPGIS_ACLS:
                case MAPGIS_OCLS:
                case MAPGIS_FDS:
                case MAPGIS_RAS:
                case MAPGIS_RCAT:
                case MAPGIS_GDB:
                {
                    if (XString.isNullOrEmpty(UnificationDialog.getDestMapGISFolder()))
                    {
                        this.setDestPath(UnificationDialog.getDestMapGISFolder());
                    }
                    break;
                }
                default:
                {
                    if (XString.isNullOrEmpty(UnificationDialog.getDestFileFolder()))
                    {
                        this.setDestPath(UnificationDialog.getDestFileFolder());
                    }
                    break;
                }
            }
        }
    }

    private DataType getDataType(String ext)
    {
        return EnumUtils.valueContains(DataType.class, ext, "getExts");
    }

    //region 属性getter&setter
    public String getSourPath()
    {
        return sourPath;
    }

    public ConvertState getState()
    {
        return state.get();
    }

    public ObjectProperty<ConvertState> stateProperty()
    {
        return state;
    }

    public void setState(ConvertState state)
    {
        this.state.set(state);
    }

    public DataType getSourType()
    {
        return sourType.get();
    }

    public ObjectProperty<DataType> sourTypeProperty()
    {
        return sourType;
    }

    public void setSourType(DataType sourType)
    {
        this.sourType.set(sourType);
        this.calcDestTypes();
    }

    public DataType getDestType()
    {
        return destType.get();
    }

    public ObjectProperty<DataType> destTypeProperty()
    {
        return destType;
    }

    public void setDestType(DataType destType)
    {
        this.destType.set(destType);
    }

    public String getSourName()
    {
        return sourName.get();
    }

    public StringProperty sourNameProperty()
    {
        return sourName;
    }

    public String getDestName()
    {
        return destName.get();
    }

    public StringProperty destNameProperty()
    {
        return destName;
    }

    public void setDestName(String destName)
    {
        this.destName.set(destName);
    }

    public String getDestPath()
    {
        return destPath.get();
    }

    public StringProperty destPathProperty()
    {
        return destPath;
    }

    public void setDestPath(String destPath)
    {
        String path = destPath;
        if (this.getDestType().equals(DataType.MAPGIS_GDB))
        {
            StringProperty nameProperty = new SimpleStringProperty();
            path = URLParse.getServer(destPath, nameProperty);
            if (!XString.isNullOrEmpty(nameProperty))
            {
                this.setDestName(nameProperty.get());
            }
        } else if (this.getDestType().equals(DataType.MAPGIS_FDS))
        {
            path = URLParse.getDataBase(destPath);
        }
        this.destPath.set(path);
    }

    public double getProgress()
    {
        return progress.get();
    }

    public DoubleProperty progressProperty()
    {
        return progress;
    }

    public void setProgress(double progress)
    {
        this.progress.set(progress);
    }

    public String getDriveName()
    {
        return driveName;
    }

    public ConvertOption getConvertOption()
    {
        return convertOption.get();
    }

    public ObjectProperty<ConvertOption> convertOptionProperty()
    {
        return convertOption;
    }

    public GeomType getGeoType()
    {
        return geoType;
    }

    public ErrorType getErrorType()
    {
        return errorType.get();
    }

    public ObjectProperty<ErrorType> errorTypeProperty()
    {
        return errorType;
    }

    public void setErrorType(ErrorType errorType)
    {
        this.errorType.set(errorType);
    }

    public List<DataType> getDestTypes()
    {
        this.calcDestTypes();
        return destTypes;
    }
    //endregion

    /**
     * 计算目的类型
     */
    private void calcDestTypes()
    {
        this.destTypes.clear();
        this.destTypes.addAll(CustomOperate.getDestTypes(this.sourType.get()));
        if (this.destTypes.size() > 0)
        {
            this.destType.set(this.destTypes.get(0));
        }
    }

    /**
     * 计算目标URL
     *
     * @return 目标URL
     */
    public String calcDestURL()
    {
        String destURL = null;
        String destDir = this.getDestPath();
        String destName = this.getDestName();
        if (!XString.isNullOrEmpty(destName) && !XString.isNullOrEmpty(destDir))
        {
            if (destDir.toLowerCase().startsWith(CustomOperate.tableProName))
            {
                destDir = destDir.substring(CustomOperate.tableProName.length());
            }

            int preIndex = destName.indexOf('/');
            if (preIndex >= 0)
            {
                destName = XString.remove(destName, preIndex);
            }
            DataType destType = this.getDestType();
            switch (destType)
            {
                //region MapGIS GDB数据
                case MAPGIS_SFCLS:
                case MAPGIS_SFCLSP:
                case MAPGIS_SFCLSL:
                case MAPGIS_SFCLSR:
                case MAPGIS_SFCLSS:
                case MAPGIS_SFCLSE:
                case MAPGIS_ACLS:
                case MAPGIS_OCLS:
                case MAPGIS_FDS:
                case MAPGIS_RAS:
                case MAPGIS_RCAT:
                    destURL = String.format("%s/%s/%s", destDir, destType.getExts().get(0), destName);
                    break;
                //endregion
                case MAPGIS_6X_WT:
                case MAPGIS_6X_WL:
                case MAPGIS_6X_WP:
                case VECTOR_MIF:
                case VECTOR_SHP:
                case VECTOR_DXF:
                case VECTOR_E00:
                case VECTOR_VCT:
                case VECTOR_GML:
                case VECTOR_DGN:
                case VECTOR_KML:
                case VECTOR_DWG:
                case VECTOR_JSON:
                case TXT:
                case RASTER_6XDEM:
                case RASTER_MSI:
                case RASTER_TIFF:
                case RASTER_IMG:
                case RASTER_BMP:
                case RASTER_JPG:
                case RASTER_GIF:
                case RASTER_JP2:
                case RASTER_PNG:
                case RASTER_HDF5:
                case DEM_ADF:
                case DEM_GRD:
                case DEM_BIL:
                case ARCINFO_COVERAGE:
                case ARCGIS_FILEGDB:
                case ARCGIS_PERSONALGDB:
                    destURL = String.format("%s%s%s%s", destDir, File.separator, destName, destType.getExts().get(0));
                    break;
                case TABLE_6X:
                case TABLE_EXCEL:
                case TABLE_ACCESS:
                case TABLE_DBF:
                    //case TABLE_TXT:
                    destURL = String.format("%s%s%s%s%s", CustomOperate.tableProName, destDir, File.separator, destName, destType.getExts().get(0));
                    break;
                case MAPGIS_GDB:
                    destURL = String.format("%s/%s", destDir, destName);
                    break;
            }
        }
        return destURL;
    }

    /**
     * 根据源类型和目的类型判断是否有参数需要设置
     *
     * @return 有否参数需要设置
     */
    public boolean hasParams()
    {
        boolean rtn = false;
        DataType destType = this.getDestType();
        switch (this.getSourType())
        {
            //region MapGIS数据
            case MAPGIS_FDS:
            case MAPGIS_GDB:
            {
                rtn = DataType.ARCGIS_PERSONALGDB.equals(destType);
                break;
            }
            case MAPGIS_OCLS:
            {
                switch (destType)
                {
                    case ARCGIS_PERSONALGDB:
                        rtn = true;
                        break;
                    case TABLE_EXCEL:
                    case TABLE_ACCESS:
                    case TABLE_DBF:
                        //case TABLE_TXT:
                        rtn = true;
                        break;
                }
                break;
            }
            case MAPGIS_SFCLS:
            case MAPGIS_SFCLSP:
            case MAPGIS_SFCLSL:
            case MAPGIS_SFCLSR:
            case MAPGIS_SFCLSS:
            case MAPGIS_SFCLSE:
            {
                switch (destType)
                {
                    case MAPGIS_6X_FILE:
                    case MAPGIS_SFCLS:
                    case MAPGIS_ACLS:
                    case VECTOR_MIF:
                    case VECTOR_SHP:
                    case VECTOR_DXF:
                    case VECTOR_DWG:
                    case ARCGIS_PERSONALGDB:
                    case TABLE_EXCEL:
                    case TABLE_ACCESS:
                    case TABLE_DBF:
                        //case TABLE_TXT:
                        rtn = true;
                        break;
                }
                break;
            }
            case MAPGIS_ACLS:
            {
                switch (destType)
                {
                    case MAPGIS_6X_FILE:
                    case MAPGIS_SFCLS:
                    case MAPGIS_ACLS:
                    case VECTOR_MIF:
                    case VECTOR_DXF:
                    case VECTOR_DWG:
                    case ARCGIS_PERSONALGDB:
                    case TABLE_EXCEL:
                    case TABLE_ACCESS:
                    case TABLE_DBF:
                        //case TABLE_TXT:
                        rtn = true;
                        break;
                }
                break;
            }
            case MAPGIS_6X_FILE:
            {
                switch (destType)
                {
                    case MAPGIS_SFCLS:
                    case MAPGIS_ACLS:
                        rtn = true;
                        break;
                }
                break;
            }
            //endregion

            //region 其他矢量数据
            case VECTOR_DGN:
                //case VECTOR_DXF:
            case VECTOR_E00:
            case VECTOR_GML:
            case VECTOR_KML:
            case VECTOR_MIF:
            case VECTOR_SHP:
            case VECTOR_VCT:
            case TXT:
            {
                rtn = DataType.MAPGIS_SFCLS.equals(destType);
                break;
            }
            case VECTOR_DXF:
            case VECTOR_DWG:
                rtn = true;
                break;
            //endregion

            //region 表格数据
            case TABLE_EXCEL:
            case TABLE_ACCESS:
            case TABLE_DBF:
                //case TABLE_TXT:
            {
                rtn = DataType.MAPGIS_OCLS.equals(destType);
                break;
            }
            //endregion

            //region 栅格文件
            case RASTER_6XDEM:
            case RASTER_MSI:
            case RASTER_TIFF:
            case RASTER_IMG:
            case RASTER_BMP:
            case RASTER_JPG:
            case RASTER_GIF:
            case RASTER_JP2:
            case RASTER_PNG:
            case RASTER_HDF5:
            case MAPGIS_RAS:
            {
                switch (destType)
                {
                    case RASTER_6XDEM:
                    case RASTER_MSI:
                    case RASTER_TIFF:
                    case RASTER_IMG:
                    case RASTER_BMP:
                    case RASTER_JPG:
                    case RASTER_GIF:
                    case RASTER_JP2:
                    case RASTER_PNG:
                    case RASTER_HDF5:
                    case MAPGIS_RAS:
                        rtn = true;
                        break;
                }
                break;
            }
            //endregion
        }
        return rtn;
    }

    private String getNameWithoutExtension(String url)
    {
        String dataName = "";
        if (url.toLowerCase().startsWith(CustomOperate.gdbProName))
        {
            int index = url.lastIndexOf('/');
            if (index >= 0)
            {
                dataName = url.substring(index + 1);
            }
        } else if (url.toLowerCase().startsWith(CustomOperate.tableProName))
        {
            String newUrl = url.substring(CustomOperate.tableProName.length());
            int index = newUrl.indexOf('/');
            dataName = index >= 0 ? newUrl.substring(index + 1) : XPath.getNameWithoutExt(newUrl);
        } else
        {
            if (url.toLowerCase().startsWith(CustomOperate.demProName))
            {
                url = url.substring(CustomOperate.demProName.length());
            }
            dataName = XPath.getNameWithoutExt(url);
        }

        //去除名称里面的非法字符
        String destURL = this.calcDestURL();
        if (!XString.isNullOrEmpty(destURL) && destURL.toLowerCase().startsWith("gdbp://"))
        {
            Server ds = URLParse.openServer(destURL);
            if (ds != null)
            {
                char[] invalidChars = ds.getInvalidChars(3);//XClsName
                if (invalidChars != null && invalidChars.length > 0)
                {
                    for (char ch : invalidChars)
                    {
                        dataName = dataName.replace(String.valueOf(ch), "");
                    }
                }
                ds.disConnect();
            }
        }
        return dataName;
    }
}
