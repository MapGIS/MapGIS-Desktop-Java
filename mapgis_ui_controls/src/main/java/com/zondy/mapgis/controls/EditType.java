package com.zondy.mapgis.controls;

/**
 * EditType
 *
 * @author CR
 * @file EditType.java
 * @brief MapControl中的编辑类型
 * @date 2020-06-17.
 */
public enum EditType {
    /*
     */
    NONE(0),
    /*
     */
    SELECT(1),
    /*
     */
    INPUT(2),
    /*
     */
    EDITVERTEX(3),
    /*
     */
    MOVE(4);

    private int value;

    EditType(int val) {
        this.value = val;
    }

    public int getValue() {
        return this.value;
    }

    public static EditType valueOf(int i) {
        EditType type = EditType.NONE;
        switch (i) {
            case 1:
                type = EditType.SELECT;
                break;
            case 2:
                type = EditType.INPUT;
                break;
            case 3:
                type = EditType.EDITVERTEX;
                break;
            case 4:
                type = EditType.MOVE;
                break;
            default:
                break;
        }
        return type;
    }
}
