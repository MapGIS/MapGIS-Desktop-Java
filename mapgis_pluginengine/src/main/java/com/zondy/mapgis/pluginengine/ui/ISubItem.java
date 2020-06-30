package com.zondy.mapgis.pluginengine.ui;

import javafx.scene.image.Image;

/**
 * 带有子项的界面元素
 *
 * @author cxy
 * @date 2019/09/11
 */
public interface ISubItem {
    /**
     * 获取图标
     *
     * @return 图标
     */
    Image getImage();

    /**
     * 获取标题
     *
     * @return 标题
     */
    String getCaption();

    /**
     * 获取鼠标移到命令按钮上时状态栏上显示的文本
     *
     * @return 显示的文本
     */
    String getMessage();

    /**
     * 获取鼠标停留在命令按钮上时弹出的提示文本
     *
     * @return 提示文本
     */
    String getTooltip();

    /**
     * 获取该按钮是否属于一个新组
     *
     * @return true/false
     */
    boolean isGroup();
}
