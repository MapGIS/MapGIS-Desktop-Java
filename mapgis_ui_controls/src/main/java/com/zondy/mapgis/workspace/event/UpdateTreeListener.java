package com.zondy.mapgis.workspace.event;

import java.util.EventListener;

/**
 * 更新树事件监听器
 *
 * @author cxy
 * @date 2019/11/27
 */
public interface UpdateTreeListener extends EventListener {
    /**
     * 触发更新树事件
     *
     * @param updateTreeEvent 更新树事件
     */
    public void fireUpdateTree(UpdateTreeEvent updateTreeEvent);
}
