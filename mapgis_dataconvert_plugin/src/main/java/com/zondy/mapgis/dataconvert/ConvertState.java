package com.zondy.mapgis.dataconvert;

import javafx.concurrent.Worker;

/**
 * Created by Administrator on 2020/3/10.
 */
public enum ConvertState
{
    READY(1, "ready_16.png"),
    WARNING(2, "warning_16.png"),
    CONVERTING(3, "running_16.png"),
    ERROR(4, "error_16.png"),
    SUCCEED(3, "success_16.png"),
    FAILED(4, "fail_16.png");

    private int value;
    private String imageName;

    ConvertState(int value, String imageName)
    {
        this.value = value;
        this.imageName = imageName;
    }

    public String getImageName()
    {
        return imageName;
    }

    public int getValue()
    {
        return value;
    }

  }
