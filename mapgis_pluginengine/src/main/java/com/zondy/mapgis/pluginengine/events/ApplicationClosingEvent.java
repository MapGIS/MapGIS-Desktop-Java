package com.zondy.mapgis.pluginengine.events;

import com.zondy.mapgis.pluginengine.enums.CloseReason;

import java.util.EventObject;

/**
 * 应用程序关闭前事件
 *
 * @author cxy
 * @date 2019/10/15
 */
public class ApplicationClosingEvent extends EventObject {
    private boolean cancel;

    /**
     * 应用程序关闭前事件
     *
     * @param source 事件源
     * @param cancel 是否取消
     * @throws IllegalArgumentException if source is null.
     */
    public ApplicationClosingEvent(Object source, boolean cancel) {
        super(source);
        this.cancel = cancel;
    }

    /**
     * 获取是否取消关闭事件
     *
     * @return 是否取消关闭事件
     */
    public boolean isCancel() {
        return cancel;
    }

    /**
     * 设置是否取消关闭事件
     *
     * @param cancel 是否取消关闭事件
     */
    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }
}
