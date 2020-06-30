package com.zondy.mapgis.workspace.engine;

/**
 * 用户右键菜单基接口，
 * 用户勿直接实现，可以通过ISinglePopMenu和IMultiPopMenu来实现
 *
 * @author cxy
 * @date 2019/09/12
 */
public interface IPopMenu {
    /**
     * 获取菜单栏插件功能项的集合
     *
     * @return 菜单栏插件功能项的集合
     */
    IMenuItem[] getItems();

    /**
     * 创建后事件
     *
     * @param ws 工作空间
     */
    void onCreate(IWorkspace ws);
}
