package com.zondy.mapgis.pluginengine.events;

import java.util.EventObject;

/**
 * 开始加载事件
 *
 * @author cxy
 * @date 2019/09/16
 */
public class BeginLoadEvent extends EventObject {
    private transient String filePath;

    /**
     * 开始加载事件
     *
     * @param source   事件源
     * @param filePath 文件路径
     * @throws IllegalArgumentException if source is null.
     */
    public BeginLoadEvent(Object source, String filePath) {
        super(source);
        this.filePath = filePath;
    }

    /**
     * 获取文件路径
     *
     * @return 文件路径
     */
    public String getFilePath() {
        return filePath;
    }
}
