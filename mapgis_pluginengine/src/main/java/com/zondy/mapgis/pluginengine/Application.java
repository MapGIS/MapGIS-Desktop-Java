package com.zondy.mapgis.pluginengine;

import com.zondy.mapgis.map.Document;
import com.zondy.mapgis.pluginengine.events.*;
import com.zondy.mapgis.pluginengine.plugin.IContentsView;
import com.zondy.mapgis.pluginengine.plugincollection.*;
import com.zondy.mapgis.srs.SRefData;
import com.zondy.mapgis.workspace.Workspace;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import javafx.stage.Window;

import java.util.ArrayList;

/**
 * 应用程序
 *
 * @author cxy
 * @date 2019/09/10
 */
public class Application implements IApplication {
    private static Application application;

    public static Application getApplication() {
        if (application == null) {
            application = new Application();
        }
        return application;
    }

    private IPluginContainer pluginContainer;
    private IWorkspace workspace;
    private IContentsView activeContentsView;
    private IRecentFileManager recentFileManager;
    private IAppLogManager appLogManager;
    private IStatesManager statesManager;
    private Document document;
    private String[] args;
    private String title;

    private Application() {
        recentFileManager = new RecentFileManager();
        appLogManager = new AppLogManager();
        statesManager = new StatesManager(this);
        document = new Document();
        document.newDocument();
        document.setTitle("地图文档");
        document.clearDirty();
        workspace = new Workspace();
        workspace.loadCustomWorkSpace();
    }

    public void applicationLoaded() {
        if (this.applicationLoadedListeners != null) {
            this.fireApplicationLoaded(new ApplicationLoadedEvent(this));
        }
    }

    public void applicationClosing(ApplicationClosingEvent applicationClosingEvent) {
        if (this.applicationClosingListeners != null) {
            for (ApplicationClosingListener applicationClosingListener : this.applicationClosingListeners) {
                applicationClosingListener.applicationClosing(applicationClosingEvent);
                if (applicationClosingEvent.isCancel()) {
                    return;
                }
            }
        }
    }

    public void applicationClosed(ApplicationClosedEvent applicationClosedEvent) {
        if (this.applicationClosedListeners != null) {
            this.fireApplicationClosed(applicationClosedEvent);
        }
        this.document.clearDirty();
        this.document.close(false);
    }

    /**
     * 获取插件容器
     *
     * @return 插件容器
     */
    @Override
    public IPluginContainer getPluginContainer() {
        return pluginContainer;
    }

    /**
     * 设置插件容器
     *
     * @param pluginContainer 插件容器
     */
    public void setPluginContainer(IPluginContainer pluginContainer) {
        this.pluginContainer = pluginContainer;
    }

    /**
     * 获取工作空间
     *
     * @return 工作空间
     */
    @Override
    public IWorkspace getWorkSpace() {
        return workspace;
    }

    /**
     * 获取当前激活的视图
     *
     * @return 当前激活的视图
     */
    @Override
    public IContentsView getActiveContentsView() {
        return activeContentsView;
    }

    /**
     * 设置当前激活的视图
     *
     * @param activeContentsView 当前激活的视图
     */
    public void setActiveContentsView(IContentsView activeContentsView) {
        this.activeContentsView = activeContentsView;
    }

    /**
     * 获取最近打开文件管理器
     *
     * @return 最近打开文件管理器
     */
    @Override
    public IRecentFileManager getRecentFileManager() {
        return recentFileManager;
    }

    /**
     * 获取日志管理器
     *
     * @return 日志管理器
     */
    @Override
    public IAppLogManager getAppLogManager() {
        return appLogManager;
    }

    /**
     * 获取状态管理器
     *
     * @return 状态管理器
     */
    @Override
    public IStatesManager getStateManager() {
        return statesManager;
    }

    /**
     * 获取全局地图文档
     *
     * @return 全局地图文档
     */
    @Override
    public Document getDocument() {
        return document;
    }

    /**
     * 获取动态菜单管理器
     *
     * @return 动态菜单管理器
     */
    @Override
    public IDynamicMenuManager getDynamicMenuManager() {
        return null;
    }

    /**
     * 获取权限验证接口
     *
     * @return 权限验证接口
     */
    @Override
    public IPermissionManager getPermissionManager() {
        return null;
    }

    /**
     * 获取进程传入参数
     *
     * @return 进程传入参数
     */
    @Override
    public String[] getArgs() {
        return new String[0];
    }

    /**
     * 设置进程传入参数
     *
     * @param args 进程传入参数
     */
    public void setArgs(String[] args) {
        this.args = args;
    }

    private ArrayList<ApplicationLoadedListener> applicationLoadedListeners = new ArrayList<>();

    /**
     * 添加应用程序启动后事件监听器
     *
     * @param applicationLoadedListener 应用程序启动后事件监听器
     */
    @Override
    public void addApplicationLoadedListener(ApplicationLoadedListener applicationLoadedListener) {
        this.applicationLoadedListeners.add(applicationLoadedListener);
    }

    /**
     * 移除应用程序启动后事件监听器
     *
     * @param applicationLoadedListener 应用程序启动后事件监听器
     */
    @Override
    public void removeApplicationLoadedListener(ApplicationLoadedListener applicationLoadedListener) {
        this.applicationLoadedListeners.remove(applicationLoadedListener);
    }

    /**
     * 触发应用程序启动后事件
     *
     * @param applicationLoadedEvent 应用程序启动后事件
     */
    @Override
    public void fireApplicationLoaded(ApplicationLoadedEvent applicationLoadedEvent) {
        for (ApplicationLoadedListener applicationLoadedListener : applicationLoadedListeners) {
            applicationLoadedListener.applicationLoaded(applicationLoadedEvent);
        }
    }

    private ArrayList<ApplicationClosingListener> applicationClosingListeners = new ArrayList<>();

    /**
     * 添加关闭应用程序前事件监听器
     *
     * @param applicationClosingListener 关闭应用程序前事件监听器
     */
    @Override
    public void addApplicationClosingListener(ApplicationClosingListener applicationClosingListener) {
        this.applicationClosingListeners.add(applicationClosingListener);
    }

    /**
     * 移除关闭应用程序前事件
     *
     * @param applicationClosingListener 关闭应用程序前事件监听器
     */
    @Override
    public void removeApplicationClosingListener(ApplicationClosingListener applicationClosingListener) {
        this.applicationClosingListeners.remove(applicationClosingListener);
    }

    /**
     * 触发关闭应用程序前事件
     *
     * @param applicationClosingEvent 关闭应用程序前事件
     */
    @Override
    public void fireApplicationClosing(ApplicationClosingEvent applicationClosingEvent) {
        for (ApplicationClosingListener applicationClosingListener : applicationClosingListeners) {
            applicationClosingListener.applicationClosing(applicationClosingEvent);
        }
    }

    private ArrayList<ApplicationClosedListener> applicationClosedListeners = new ArrayList<>();

    /**
     * 添加关闭应用程序后事件监听器
     *
     * @param applicationClosedListener 关闭应用程序后事件监听器
     */
    @Override
    public void addApplicationClosedListener(ApplicationClosedListener applicationClosedListener) {
        this.applicationClosedListeners.add(applicationClosedListener);
    }

    /**
     * 移除关闭应用程序后事件监听器
     *
     * @param applicationClosedListener 关闭应用程序后事件监听器
     */
    @Override
    public void removeApplicationClosedListener(ApplicationClosedListener applicationClosedListener) {
        this.applicationClosedListeners.remove(applicationClosedListener);
    }

    /**
     * 触发关闭应用程序后事件
     *
     * @param applicationClosedEvent 关闭应用程序后事件
     */
    @Override
    public void fireApplicationClosed(ApplicationClosedEvent applicationClosedEvent) {
        for (ApplicationClosedListener applicationClosedListener : applicationClosedListeners) {
            applicationClosedListener.applicationClosed(applicationClosedEvent);
        }
    }

    private ArrayList<CloseApplicationListener> closeApplicationListeners = new ArrayList<>();

    public void addCloseApplicationListener(CloseApplicationListener closeApplicationListener) {
        this.closeApplicationListeners.add(closeApplicationListener);
    }

    public void removeCloseApplicationListener(CloseApplicationListener closeApplicationListener) {
        this.closeApplicationListeners.remove(closeApplicationListener);
    }

    public void fireCloseApplication(CloseApplicationEvent closeApplicationEvent) {
        for (CloseApplicationListener closeApplicationListener : closeApplicationListeners) {
            closeApplicationListener.closeApplication(closeApplicationEvent);
        }
    }

    private ArrayList<TitleChangedListener> titleChangedListeners = new ArrayList<>();

    public void addTitleChangedListener(TitleChangedListener titleChangedListener) {
        this.titleChangedListeners.add(titleChangedListener);
    }

    public void removeTitleChangedListener(TitleChangedListener titleChangedListener) {
        this.titleChangedListeners.remove(titleChangedListener);
    }

    public void fireTitleChanged(TitleChangedEvent titleChangedEvent) {
        for (TitleChangedListener titleChangedListener : titleChangedListeners) {
            titleChangedListener.titleChanged(titleChangedEvent);
        }
    }

    /**
     * 关闭应用程序
     */
    @Override
    public void closeApplication() {
        if (this.closeApplicationListeners != null) {
            this.fireCloseApplication(new CloseApplicationEvent(this));
        }
    }

    /**
     * 获取应用程序标题
     *
     * @return 应用程序标题
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * 设置应用程序标题
     *
     * @param title 应用程序标题
     */
    @Override
    public void setTitle(String title) {
        this.title = title;
        if (this.titleChangedListeners != null) {
            this.fireTitleChanged(new TitleChangedEvent(this, this.title));
        }
    }
}
