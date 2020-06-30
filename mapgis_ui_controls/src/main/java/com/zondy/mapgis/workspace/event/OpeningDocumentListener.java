package com.zondy.mapgis.workspace.event;

import java.util.EventListener;

/**
 * 地图文档打开事件监听器
 *
 * @author cxy
 * @date 2019/12/02
 */
public interface OpeningDocumentListener extends EventListener {
    /**
     * 触发地图文档打开事件
     *
     * @param openingDocumentEvent 地图文档打开事件
     */
    public void fireOpeningDocument(OpeningDocumentEvent openingDocumentEvent);
}
