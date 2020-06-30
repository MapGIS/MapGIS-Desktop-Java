package com.zondy.mapgis.pluginengine;

import com.zondy.mapgis.map.Document;
import com.zondy.mapgis.pluginengine.events.*;
import com.zondy.mapgis.pluginengine.plugin.IContentsView;
import com.zondy.mapgis.pluginengine.plugincollection.*;
import com.zondy.mapgis.workspace.engine.IWorkspace;

/**
 * 插件框架 App 接口
 *
 * @author cxy
 * @date 2019/09/10
 */
public interface IApplication {
    /**
     * 获取插件容器
     *
     * @return 插件容器
     */
    IPluginContainer getPluginContainer();

    /**
     * 获取工作空间
     *
     * @return 工作空间
     */
    IWorkspace getWorkSpace();

    /**
     * 获取当前激活的视图
     *
     * @return 当前激活的视图
     */
    IContentsView getActiveContentsView();

    /**
     * 获取最近打开文件管理器
     *
     * @return 最近打开文件管理器
     */
    IRecentFileManager getRecentFileManager();

    /**
     * 获取日志管理器
     *
     * @return 日志管理器
     */
    IAppLogManager getAppLogManager();

    /**
     * 获取状态管理器
     *
     * @return 状态管理器
     */
    IStatesManager getStateManager();

    /**
     * 获取全局地图文档
     *
     * @return 全局地图文档
     */
    Document getDocument();

    /**
     * 获取进程传入参数
     *
     * @return 进程传入参数
     */
    String[] getArgs();

    /**
     * 获取动态菜单管理器
     *
     * @return 动态菜单管理器
     */
    IDynamicMenuManager getDynamicMenuManager();

    /**
     * 获取权限验证接口
     *
     * @return 权限验证接口
     */
    IPermissionManager getPermissionManager();

    /**
     * 添加应用程序启动后事件监听器
     *
     * @param applicationLoadedListener 应用程序启动后事件监听器
     */
    void addApplicationLoadedListener(ApplicationLoadedListener applicationLoadedListener);

    /**
     * 移除应用程序启动后事件监听器
     *
     * @param applicationLoadedListener 应用程序启动后事件监听器
     */
    void removeApplicationLoadedListener(ApplicationLoadedListener applicationLoadedListener);

    /**
     * 触发应用程序启动后事件
     * @param applicationLoadedEvent 应用程序启动后事件
     */
    void fireApplicationLoaded(ApplicationLoadedEvent applicationLoadedEvent);

    /**
     * 添加关闭应用程序前事件监听器
     *
     * @param applicationClosingListener 关闭应用程序前事件监听器
     */
    void addApplicationClosingListener(ApplicationClosingListener applicationClosingListener);

    /**
     * 移除关闭应用程序前事件
     *
     * @param applicationClosingListener 关闭应用程序前事件监听器
     */
    void removeApplicationClosingListener(ApplicationClosingListener applicationClosingListener);

    /**
     * 触发关闭应用程序前事件
     *
     * @param applicationClosingEvent 关闭应用程序前事件
     */
    void fireApplicationClosing(ApplicationClosingEvent applicationClosingEvent);

    /**
     * 添加关闭应用程序后事件监听器
     *
     * @param applicationClosedListener 关闭应用程序后事件监听器
     */
    void addApplicationClosedListener(ApplicationClosedListener applicationClosedListener);

    /**
     * 移除关闭应用程序后事件监听器
     *
     * @param applicationClosedListener 关闭应用程序后事件监听器
     */
    void removeApplicationClosedListener(ApplicationClosedListener applicationClosedListener);

    /**
     * 触发关闭应用程序后事件
     *
     * @param applicationClosedEvent 关闭应用程序后事件
     */
    void fireApplicationClosed(ApplicationClosedEvent applicationClosedEvent);

    /**
     * 关闭应用程序
     */
    void closeApplication();

    /**
     * 获取应用程序标题
     *
     * @return 应用程序标题
     */
    String getTitle();

    /**
     * 设置应用程序标题
     *
     * @param title 应用程序标题
     */
    void setTitle(String title);

    // 获取/设置应用程序标签模式
    // bool TabbedMdi { get; set; }
    // 在 MDI 父窗体内排列多文档界面 (MDI) 子窗体。
    // <param name="value">布局方式</param>
    // void LayoutMdi(MdiLayout value);
}
