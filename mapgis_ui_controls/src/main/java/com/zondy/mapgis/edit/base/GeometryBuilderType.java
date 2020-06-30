package com.zondy.mapgis.edit.base;

/**
 * 几何构建工具类型
 *
 * @author cxy
 * @date 2020/05/19
 */
public enum GeometryBuilderType {
    /**
     *
     */
    UNKNOWN(-1),
    /**
     *
     */
    POINTBUILDER(1),
    /**
     *
     */
    RECTBUILDER(2),
    /**
     *
     */
    POLYLINEBUILDER(3),
    /**
     *
     */
    POLYGONBUILDER(4),
    /**
     *
     */
    MULTIPOINTBUILDER(5);

    private final int value;

    private GeometryBuilderType(int value) {
        this.value = value;
    }

    public static GeometryBuilderType fromValue(int value) {
        GeometryBuilderType result = null;
        GeometryBuilderType[] types = values();

        for (GeometryBuilderType type : types) {
            if (value == type.value) {
                result = type;
                break;
            }
        }

        if (result == null) {
            throw new UnsupportedOperationException("Value " + value + "not found in GeometryBuilderType.value()");
        } else {
            return result;
        }
    }

    public int getValue() {
        return this.value;
    }
}
