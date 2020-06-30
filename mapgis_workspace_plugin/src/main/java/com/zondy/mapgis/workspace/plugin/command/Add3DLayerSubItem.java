package com.zondy.mapgis.workspace.plugin.command;

import com.zondy.mapgis.pluginengine.ui.ISubItem;
import javafx.scene.image.Image;

/**
 * 添加图层
 *
 * @author cxy
 * @date 2019/11/29
 */
public class Add3DLayerSubItem implements ISubItem {
    /**
     * 获取图标
     *
     * @return 图标
     */
    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_AddLayerCmd_16.png"));
    }

    /**
     * 获取标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "添加图层";
    }

    /**
     * 获取鼠标移到命令按钮上时状态栏上显示的文本
     *
     * @return 显示的文本
     */
    @Override
    public String getMessage() {
        return "添加图层";
    }

    /**
     * 获取鼠标停留在命令按钮上时弹出的提示文本
     *
     * @return 提示文本
     */
    @Override
    public String getTooltip() {
        return "添加图层";
    }

    /**
     * 获取该按钮是否属于一个新组
     *
     * @return true/false
     */
    @Override
    public boolean isGroup() {
        return false;
    }
}
