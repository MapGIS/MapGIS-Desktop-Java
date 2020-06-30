package com.zondy.mapgis.controls;

/**
 * Created by Administrator on 2019/11/5.
 */
public enum ToolType
{
    DEFAULT(0),
    ZOOMIN(1),
    ZOOMOUT(2),
    MOVE(3),
    USER(4);

    private int value;

    ToolType(int val)
    {
        this.value = val;
    }

    public int getValue()
    {
        return this.value;
    }
    public static ToolType valueOf(int i)
    {
        ToolType type = ToolType.DEFAULT;
        switch (i)
        {
            case 1:
                type = ToolType.ZOOMIN;
                break;
            case 2:
                type = ToolType.ZOOMOUT;
                break;
            case 3:
                type = ToolType.MOVE;
                break;
            case 4:
                type = ToolType.USER;
                break;
        }
        return type;
    }
}
