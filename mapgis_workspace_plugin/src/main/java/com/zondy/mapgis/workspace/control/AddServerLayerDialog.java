package com.zondy.mapgis.workspace.control;

import com.zondy.mapgis.map.MapServer;
import javafx.scene.control.Dialog;

/**
 * 添加服务图层
 *
 * @author cxy
 * @date 2019/12/17
 */
public class AddServerLayerDialog extends Dialog {
    // TODO: 待 MapServer 接口补充
    private MapServer mapServer;

    public MapServer getMapServer() {
        return mapServer;
    }
}
