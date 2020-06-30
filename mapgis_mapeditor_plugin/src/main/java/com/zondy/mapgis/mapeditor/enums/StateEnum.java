package com.zondy.mapgis.mapeditor.enums;

public enum StateEnum {
    /**
     *
     */
    ANN_VISIBLE(1),
    /**
     *
     */
    ANN_EDITABLE(1 << 1),
    /**
     *
     */
    ANN_ACTIVE(1 << 2),
    /**
     *
     */
    PNT_VISIBLE(1 << 3),
    /**
     *
     */
    PNT_EDITABLE(1 << 4),
    /**
     *
     */
    PNT_ACTIVE(1 << 5),
    /**
     *
     */
    LINE_VISIBLE(1 << 6),
    /**
     *
     */
    LINE_EDITABLE(1 << 7),
    /**
     *
     */
    LINE_ACTIVE(1 << 8),
    /**
     *
     */
    POLYGON_VISIBLE(1 << 9),
    /**
     *
     */
    POLYGON_EDITABLE(1 << 10),
    /**
     *
     */
    POLYGON_ACTIVE(1 << 11),
    /**
     *
     */
    RASTERDATASET_VISIBLE(1 << 12),
    /**
     *
     */
    RASTERDATASET_EDITABLE(1 << 13),
    /**
     *
     */
    RASTERDATASET_ACTIVE(1 << 14),
    /**
     *
     */
    RASTERCATALOG_VISIBLE(1 << 15),
    /**
     *
     */
    RASTERCATALOG_EDITABLE(1 << 16),
    /**
     *
     */
    RASTERCATALOG_ACTIVE(1 << 17),
    /**
     *
     */
    S57DATASET_VISIBLE(1 << 18),
    /**
     *
     */
    S57DATASET_EDITABLE(1 << 19),
    /**
     *
     */
    S57DATASET_ACTIVE(1 << 20),
    /**
     *
     */
    S57OBJ_VISIBLE(1 << 21),
    /**
     *
     */
    S57OBJ_EDITABLE(1 << 22),
    /**
     *
     */
    S57OBJ_ACTIVE(1 << 23);

    private int value;

    StateEnum(int value) {
        this.value = value;
    }

    public int toValue() {
        return value;
    }
}
