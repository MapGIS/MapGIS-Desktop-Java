package com.zondy.mapgis.base;

/**
 * 通过排序类型的大小进行默认排序
 *
 * @author cxy
 * @date 2019/12/17
 */
public enum SortType {
    /**
     * -2
     */
    UNKNOWN(-2),
    /**
     * -1 服务图层
     */
    IMGLAYER(-1),
    /**
     * 0
     */
    GROUP(0),
    /**
     * 1
     */
    MOSAICDATASET(1),
    /**
     * 2
     */
    RASTERCAT(2),
    /**
     * 3
     */
    RASTER(3),
    /**
     * 4
     */
    WP(4),
    /**
     * 5
     */
    SFCLS_REG(5),
    /**
     * 6
     */
    WL(6),
    /**
     * 7
     */
    SFCLS_LIN(7),
    /**
     * 8
     */
    WT(8),
    /**
     * 9
     */
    SFCLS_PNT(9),
    /**
     * 10
     */
    NETCLS(10),
    /**
     * 11
     */
    ACLS(11),
    /**
     * 12 地形层
     */
    TERRAIN(12),
    /**
     * 13 地图引用层
     */
    MAPREF(13),
    /**
     * 14 三维矢量区
     */
    SFCLS3D_REG(14),
    /**
     * 15 三维矢量线
     */
    SFCLS3D_LIN(15),
    /**
     * 16 三维矢量点
     */
    SFCLS3D_PNT(16),
    /**
     * 17 三维注记
     */
    ACLS3D(17),
    /**
     * 18 模型层
     */
    MODEL(18);

    private int value;

    private SortType(int value) {
        this.value = value;
    }

    public int toValue() {
        return value;
    }
}
