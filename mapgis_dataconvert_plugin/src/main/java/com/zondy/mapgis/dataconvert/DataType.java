package com.zondy.mapgis.dataconvert;

import com.zondy.mapgis.base.XString;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 转换数据的类型
 *
 * @author CR
 * @file DataType.java
 * @brief 转换数据类型
 * @create 2020-03-10.
 */
public enum DataType
{
    UNKNOWN("", null, ""),

    //region GDB数据
    MAPGIS_SFCLSP("点简单要素类", "Png_SfClsPnt_16.png", "sfclsp"),
    MAPGIS_SFCLSL("线简单要素类", "Png_SfClsLin_16.png", "sfclsl"),
    MAPGIS_SFCLSR("区简单要素类", "Png_SfClsReg_16.png", "sfclsr"),
    MAPGIS_SFCLSS("面简单要素类", "Png_SfClsSurface_16.png", "sfclss"),
    MAPGIS_SFCLSE("体简单要素类", "Png_SfClsEntity_16.png", "sfclse"),
    MAPGIS_SFCLS("简单要素类", "Png_SfCls_16.png", "sfcls", "sfclsp", "sfclsl", "sfclsr", "sfclss", "sfclse"),
    MAPGIS_ACLS("注记类", "Png_ACls_16.png", "acls"),
    MAPGIS_OCLS("对象类", "Png_OCls_16.png", "ocls"),
    MAPGIS_FDS("要素数据集", "Png_FDs_16.png", "ds"),
    MAPGIS_RAS("栅格数据集", "Png_RasterDs_16.png", "ras"),
    MAPGIS_RCAT("栅格目录", "Png_RasterCatalog_16.png", "rcat"),
    MAPGIS_GDB("地理数据库", "Png_GDataBase_16.png", "GDB"),
    //endregion

    //region 6x文件
    MAPGIS_6X_WT("6x点文件", "Png_6xPnt_16.png", ".wt"),
    MAPGIS_6X_WL("6x线文件", "Png_6xLin_16.png", ".wl"),
    MAPGIS_6X_WP("6x区文件", "Png_6xReg_16.png", ".wp"),
    MAPGIS_6X_FILE("6x文件", "Png_6xData_16.png", ".wt", ".wl", ".wp"),
    MAPGIS_6X_MPJ("6x工程", "file_16.png", ".mpj"),
    //endregion

    //region ArcGIS
    VECTOR_SHP("Shape文件", "file_16.png", ".shp"),
    VECTOR_E00("e00文件", "file_16.png", ".e00"),
    ARCINFO_COVERAGE("ArcInfo Coverage文件", "folderfile.png", ""),
    ARCGIS_FILEGDB("ArcGIS File GDB", "file_16.png", ".gdb"),
    ARCGIS_PERSONALGDB("ArcGIS Personal GDB", "file_16.png", ".mdb"),
    //endregion

    //region 其他矢量文件
    VECTOR_MIF("mif文件", "file_16.png", ".mif"),
    VECTOR_DXF("dxf文件", "file_16.png", ".dxf"),
    VECTOR_VCT("vct文件", "file_16.png", ".vct"),
    VECTOR_GML("gml文件", "file_16.png", ".gml"),
    VECTOR_DGN("dgn文件", "file_16.png", ".dgn"),
    VECTOR_KML("kml文件", "file_16.png", ".kml"),
    VECTOR_DWG("dwg文件", "file_16.png", ".dwg"),
    VECTOR_JSON("json文件", "file_16.png", ".json"),
    TXT("txt文件", "file_16.png", ".txt"),//注：txt有可能时矢量、表格、DEM
    //endregion

    //region 表格数据
    TABLE_6X("6x表文件", "file_16.png", ".wb"),
    TABLE_EXCEL("Excel表格", "file_16.png", ".xls", ".xlsx"),
    TABLE_ACCESS("Access表格", "file_16.png", ".mdb", ".accdb"),
    TABLE_DBF("Foxpro表格", "file_16.png", ".dbf"),
//    TABLE_TXT( "","file_16.png",".txt"),
    //endregion

    //region 栅格文件
    RASTER_6XDEM("6x DEM文件", "file_16.png", ".inf"),
    RASTER_MSI("msi文件", "file_16.png", ".msi"),
    RASTER_TIFF("tif文件", "file_16.png", ".tif", ".tiff"),
    RASTER_IMG("img文件", "file_16.png", ".img"),
    RASTER_BMP("bmp文件", "file_16.png", ".bmp"),
    RASTER_JPG("jpg文件", "file_16.png", ".jpg", ".jpeg"),
    RASTER_GIF("gif文件", "file_16.png", ".gif"),
    RASTER_JP2("jp2文件", "file_16.png", ".jp2"),
    RASTER_PNG("png文件", "file_16.png", ".png"),
    RASTER_HDF5("hdf5文件", "file_16.png", ".h5"),
    //endregion

    //region Dem数据
//    DEM_TXT( "","file_16.png",".txt"),
    DEM_ADF("adf文件", "file_16.png", ".adf"),
    DEM_GRD("grd文件", "file_16.png", ".grd"),
    DEM_BIL("bil文件", "file_16.png", ".bil"),
    //endregion

    //"三维模型文件(*.3ds;*.obj;*.dae;*.osgb;*.fbx;*.xml;*.x;*.las)|*.3ds;*.obj;*.dae;*.osgb;*.fbx;*.xml;*.x;*.las"

    //region 外部三维模型数据
    Model_3DS("3ds文件", "file_16.png", ".3ds"),
    Model_OBJ("obj文件", "file_16.png", ".obj"),
    Model_DAE("dae文件", "file_16.png", ".dae"),
    Model_OSGB("osgb文件", "file_16.png", ".osgb"),
    Model_FBX("fbx文件", "file_16.png", ".fbx"),
    Model_XML("xml文件", "file_16.png", ".xml"),
    Model_X("x文件", "file_16.png", ".x"),
    Model_LAS("las文件", "file_16.png", ".las");
    //endregion

    private ObservableList<String> exts = FXCollections.observableArrayList();
    private String text;
    private Image image;
    private HashMap<String, Image> imageMap = new HashMap<>();

    DataType(String text, String imageName, String... exts)
    {
        this.text = text;
        if (!XString.isNullOrEmpty(imageName))
        {
            if (!imageMap.containsKey(imageName))
            {
                imageMap.put(imageName, new Image(getClass().getResourceAsStream(imageName)));
            }
            this.image = imageMap.get(imageName);
        }
        this.exts.addAll(exts);
    }

    public ObservableList<String> getExts()
    {
        return this.exts;
    }

    public String getText()
    {
        return this.text;
    }

    public Image getImage()
    {
        return image;
    }
}
  