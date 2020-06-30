package com.zondy.mapgis.workspace.event;

import java.util.EventObject;

/**
 * 地图文档打开事件
 *
 * @author cxy
 * @date 2019/12/02
 */
public class OpeningDocumentEvent extends EventObject {
    /**
     * 地图文档打开事件
     *
     * @param source 事件源
     * @throws IllegalArgumentException if source is null.
     */
    public OpeningDocumentEvent(Object source) {
        super(source);
    }
}
