package com.zondy.mapgis.controls.wizard;

/**
 * @author CR
 * @file Direction.java
 * @brief 向导当前页面发生变化时的变化方向，下一步对应Forward，上一步对应Backward
 * @create 2019-03-06
 */
public enum Direction
{
    Forward(0),
    Backward(1);

    private int val;

    Direction(int val)
    {
        this.val = val;
    }

    public int getValue()
    {
        return this.val;
    }
}

