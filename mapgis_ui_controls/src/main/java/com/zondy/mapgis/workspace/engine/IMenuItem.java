package com.zondy.mapgis.workspace.engine;

import javafx.scene.image.Image;

/**
 * 用户菜单项，
 * 用户勿直接实现，可以通过ISingleMenuItem和IMultiMenuItem来实现
 *
 * @author cxy
 * @date 2019/09/12
 */
public interface IMenuItem {
    // TODO: 2019/09/12 暂用 Image 代替 BitMap
    /**
     * 获取菜单项图标
     *
     * @return 菜单项图标
     */
    Image getImage();

    /**
     * 获取命令按钮的标题
     *
     * @return 标题
     */
    String getCaption();

    /**
     * 获取命令按钮是否可用
     *
     * @return true/false
     */
    boolean isEnabled();

    /**
     * 获取命令按钮是否可见
     *
     * @return true/false
     */
    boolean isVisible();

    /**
     * 获取命令按钮是否选中
     *
     * @return true/false
     */
    boolean isChecked();

    /**
     * 获取是否启用分割符
     *
     * @return true/false
     */
    boolean isBeginGroup();

    /**
     * 创建后事件
     *
     * @param ws 工作空间引擎
     */
    void onCreate(IWorkspace ws);
}
