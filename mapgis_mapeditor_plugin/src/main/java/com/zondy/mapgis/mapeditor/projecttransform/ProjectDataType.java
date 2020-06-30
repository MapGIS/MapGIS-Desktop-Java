package com.zondy.mapgis.mapeditor.projecttransform;

/**
 * 投影数据类型
 */
public enum ProjectDataType {
    Sfcls(0),
    Acls(1),
    File6x(2),
    Ras(3),
    Catalog(4),
    Tdf(5),
    Mut(6);
    private int index;

    private ProjectDataType(int i) {
        index = 0;
    }

    public int value() {
        return index;
    }

    public static ProjectDataType valueOf(int i) {
        ProjectDataType type = ProjectDataType.Sfcls;
        switch (i) {
            case 0:
                type = ProjectDataType.Sfcls;
                break;
            case 1:
                type = ProjectDataType.Acls;
                break;
            case 2:
                type = ProjectDataType.File6x;
                break;
            case 3:
                type =  ProjectDataType.Ras;
                break;
            case 4:
                type = ProjectDataType.Catalog;
                break;
            case 5:
                type = ProjectDataType.Tdf;
                break;
            case 6:
                type = ProjectDataType.Mut;
                break;
            default:
                break;
        }
        return type;
    }
}
