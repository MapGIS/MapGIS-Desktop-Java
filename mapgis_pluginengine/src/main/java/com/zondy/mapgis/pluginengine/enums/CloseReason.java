package com.zondy.mapgis.pluginengine.enums;

/**
 * 应用程序关闭原因
 *
 * @author cxy
 * @date 2019/10/15
 */
public enum CloseReason {
    /**
     *
     */
    NONE(0),
    /**
     *
     */
    WINDOWSSHUTDOWN(1),
    /**
     *
     */
    MDIFORMCLOSING(2),
    /**
     *
     */
    USERCLOSING(3),
    /**
     *
     */
    TASKMANAGERCLOSING(4),
    /**
     *
     */
    FORMOWNERCLOSING(5),
    /**
     *
     */
    APPLICATIONEXITCALL(6);

    private int value;

    private CloseReason(int value) {
        this.value = value;
    }

    public int toValue() {
        return value;
    }
}
