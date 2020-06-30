package com.zondy.mapgis.pluginengine.ui;

import com.zondy.mapgis.pluginengine.plugin.IPlugin;
import javafx.scene.paint.Color;

/**
 * Ribbon页面类别插件
 *
 * @author cxy
 * @date 2019/09/11
 */
public interface IRibbonPageCategory extends IPlugin {
    /**
     * 获取页面类别绑定的视图的Key，其格式为“[命名空间].[视图的类名]”
     *
     * @return 视图的Key
     */
    String getAttachContentsViewKey();

    /**
     * 获取页面类别的标题
     *
     * @return 标题
     */
    String getText();

    /**
     * 获取页面类别是否可见
     *
     * @return true/false
     */
    boolean isVisible();

    /**
     * 获取 Ribbon 页面集合
     *
     * @return Ribbon 页面集合
     */
    IRibbonPage[] getRibbonPages();
}
