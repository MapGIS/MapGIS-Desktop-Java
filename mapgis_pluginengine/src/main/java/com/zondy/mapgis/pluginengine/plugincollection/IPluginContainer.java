package com.zondy.mapgis.pluginengine.plugincollection;

import com.zondy.mapgis.pluginengine.enums.CheckStateEnum;
import com.zondy.mapgis.pluginengine.events.*;
import com.zondy.mapgis.pluginengine.plugin.*;
import com.zondy.mapgis.pluginengine.ui.*;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Map;

/**
 * 插件容器接口
 *
 * Created by cxy on 2019/9/10 0010.
 */
public interface IPluginContainer {
    /**
     * 添加插件加载事件监听器
     *
     * @param pluginLoadedListener 插件加载事件监听器
     */
    void addPluginLoadedListener(PluginLoadedListener pluginLoadedListener);

    /**
     * 删除插件加载事件监听器
     *
     * @param pluginLoadedListener 插件加载事件监听器
     */
    void removePluginLoadedListener(PluginLoadedListener pluginLoadedListener);

    /**
     * 触发插件加载事件
     *
     * @param pluginLoadedEvent 插件加载事件监听器
     */
    void firePluginLoaded(PluginLoadedEvent pluginLoadedEvent);

    /**
     * 添加插件卸载事件监听器
     *
     * @param pluginUnLoadedListener 插件卸载事件监听器
     */
    void addPluginUnLoadedListener(PluginUnLoadedListener pluginUnLoadedListener);

    /**
     * 删除插件卸载事件监听器
     *
     * @param pluginUnLoadedListener 插件卸载事件监听器
     */
    void removePluginUnLoadedListener(PluginUnLoadedListener pluginUnLoadedListener);

    /**
     * 触发插件卸载事件
     *
     * @param pluginUnLoadedEvent 插件卸载事件监听器
     */
    void firePluginUnLoaded(PluginUnLoadedEvent pluginUnLoadedEvent);

    /**
     * 添加激活内容视图改变事件监听器
     *
     * @param contentsViewChangedListener 激活内容视图改变事件监听器
     */
    void addContentsViewChangedListener(ContentsViewChangedListener contentsViewChangedListener);

    /**
     * 删除激活内容视图改变事件监听器
     *
     * @param contentsViewChangedListener 激活内容视图改变事件监听器
     */
    void removeContentsViewChangedListener(ContentsViewChangedListener contentsViewChangedListener);

    /**
     * 触发激活内容视图改变事件
     *
     * @param contentsViewChangedEvent 激活内容视图改变事件
     */
    void fireContentsViewChanged(ContentsViewChangedEvent contentsViewChangedEvent);

    /**
     * 添加内容视图关闭前事件监听器
     *
     * @param contentsViewClosingListener 内容视图关闭前事件监听器
     */
    void addContentsViewClosingListener(ContentsViewClosingListener contentsViewClosingListener);

    /**
     * 删除内容视图关闭前事件监听器
     *
     * @param contentsViewClosingListener 内容视图关闭前事件监听器
     */
    void removeContentsViewClosingListener(ContentsViewClosingListener contentsViewClosingListener);

    /**
     * 触发内容视图关闭前事件
     *
     * @param contentsViewClosingEvent 内容视图关闭前事件
     */
    void fireContentsViewClosing(ContentsViewClosingEvent contentsViewClosingEvent);

//    /**
//     * 添加激活停靠面板改变事件监听器
//     *
//     * @param dockWindowChangedListener 激活停靠面板改变事件监听器
//     */
//    void addDockWindowChangedListener(DockWindowChangedListener dockWindowChangedListener);
//    /**
//     * 删除激活停靠面板改变事件监听器
//     *
//     * @param dockWindowChangedListener 激活停靠面板改变事件监听器
//     */
//    void removeDockWindowChangedListener(DockWindowChangedListener dockWindowChangedListener);
//    /**
//     * 触发激活停靠面板改变事件
//     *
//     * @param dockWindowChangedEvent 激活停靠面板改变事件
//     */
//    void fireDockWindowChanged(DockWindowChangedEvent dockWindowChangedEvent);

    /**
     * 添加 Command 插件点击事件监听器
     *
     * @param commandClickedListener Command 插件点击事件监听器
     */
    void addCommandClickedListener(CommandClickedListener commandClickedListener);

    /**
     * 删除 Command 插件点击事件监听器
     *
     * @param commandClickedListener Command 插件点击事件监听器
     */
    void removeCommandClickedListener(CommandClickedListener commandClickedListener);

    /**
     * 触发 Command 插件点击事件
     * @param commandClickedEvent Command 插件点击事件
     */
    void fireCommandClicked(CommandClickedEvent commandClickedEvent);

    /**
     * 获得 DropDown 集合
     *
     * @return DropDown 集合
     */
    Map<String, IDropDown> getDropDowns();

    /**
     * 获得 Command 集合
     *
     * @return Command 集合
     */
    Map<String, ICommand> getCommands();

    /**
     * 获得 CheckCommand 集合
     *
     * @return CheckCommand 集合
     */
    Map<String, ICheckCommand> getCheckCommands();

    /**
     * 获得 EditCombobox 集合
     *
     * @return EditCombobox 集合
     */
    Map<String, IEditCombobox> getEditComboboxes();

    /**
     * 获得 Static 集合
     *
     * @return Static 集合
     */
    Map<String, IStatic> getStatics();

    /**
     * 获得 Progress 集合
     *
     * @return Progress 集合
     */
    Map<String, IProgress> getProgresses();

    /**
     * 获得工具栏集合
     *
     * @return 工具栏集合
     */
    Map<String, IToolBar> getToolBars();

    /**
     * 获得菜单集合
     *
     * @return 菜单集合
     */
    Map<String, IMenuBar> getMenuBars();

    /**
     * 获得状态栏集合
     *
     * @return 状态栏集合
     */
    Map<String, IStatusBar> getStatusBars();

    /**
     * 获得浮动窗口集合
     *
     * @return 浮动窗口集合
     */
    Map<String, IDockWindow> getDockWindows();

    /**
     * 获得内容视窗集合
     *
     * @return 内容视窗集合
     */
    Map<String, IContentsView> getContentsViews();

    /**
     * 获得 RibbonPageCategory 集合
     *
     * @return RibbonPageCategory 集合
     */
    Map<String, IRibbonPageCategory> getRibbonPageCategories();

    /// 获得RibbonPage集合

    /**
     * 获得 RibbonPage 集合
     *
     * @return RibbonPage 集合
     */
    Map<String, IRibbonPage> getRibbonPages();

    /**
     * 获得 RibbonPageGroup 集合
     *
     * @return RibbonPageGroup 集合
     */
    Map<String, IRibbonPageGroup> getRibbonPageGroups();

    /**
     * 获得 CheckBox 集合
     *
     * @return CheckBox 集合
     */
    Map<String, ICheckBox> getCheckBoxes();

    /**
     * 获得 Gallery 集合
     *
     * @return Gallery 集合
     */
    Map<String, IGallery> getGalleries();

    /**
     * 获得 WelcomeScreen 集合
     *
     * @return WelcomeScreen 集合
     */
    Map<String, IWelcomeScreen> getWelcomeScreens();

    /**
     * 获得 ISubItem 集合
     *
     * @return ISubItem 集合
     */
    Map<String, ISubItem> getSubItems();

    /**
     * 获得 Bitmap 集合
     *
     * @return Bitmap 集合
     */
    Map<String, IFunction> getFunctions();

    /**
     * 获得已经加载的程序集绝对路径集合
     *
     * @return 已加载的程序集绝对路径集合
     */
    ArrayList getAssemblyArray();

    /**
     * 获得已经加载扩展包路径集合
     *
     * @return 已加载扩展包路径集合
     */
    ArrayList getPackageArray();

    /**
     * 设置插件 Visible
     * 支持插件类型为：ICommand、ICheckCommand、ITool、IEditCombobox、ICheckBox、IGallery、IStatic、IProgress、IMenuBar、IToolBar、IRibbonPageCategory、IRibbonPage、IRibbonPageGroup、IDockWindow、IContentsView
     *
     * @param plugin 插件
     * @param visible true/false
     */
    void setPluginVisible(IPlugin plugin, boolean visible);

    /**
     * 获取插件Visible
     * 支持插件类型为：ICommand、ICheckCommand、ITool、IEditCombobox、ICheckBox、IGallery、IStatic、IProgress、IMenuBar、IToolBar、IRibbonPageCategory、IRibbonPage、IRibbonPageGroup、IDockWindow
     *
     * @param plugin 插件
     * @return true/false
     */
    boolean pluginIsVisible(IPlugin plugin);

    /**
     * 设置插件 Enable
     * 支持插件类型为：ICommand、ICheckCommand、ITool、IEditCombobox、ICheckBox、IGallery、IStatic、IProgress、IMenuBar、IToolBar(工具条下所有控件Enable)
     *
     * @param plugin 插件
     * @param enable true/false
     */
    void setPluginEnable(IPlugin plugin, boolean enable);

    /**
     * 获取插件 Enable
     * 支持插件类型为：ICommand、ICheckCommand、ITool、IEditCombobox、ICheckBox、IGallery、IStatic、IProgress
     *
     * @param plugin 插件
     * @return true/false
     */
    boolean pluginIsEnable(IPlugin plugin);

//    /**
//     * 选中 RibbonPage
//     *
//     * @param ribbonPage ribbon 页
//     */
//    void selectPage(IRibbonPage ribbonPage);

    /**
     * 设置 command 插件是否处于选中状态
     *
     * @param command 命令项
     * @param checkState true/false
     */
    void setCommandChecked(ICheckCommand command, boolean checkState);

//    /**
//     * 设置 CheckBox 状态
//     *
//     * @param checkBox 复选框
//     * @param checkState 选择状态
//     */
//    void setCheckBoxState(ICheckBox checkBox, CheckStateEnum checkState);

    /**
     * 创建 DockWindow
     *
     * @param typeName dockWindow 类型名
     * @return dockWindow
     */
    IDockWindow createDockWindow(String typeName);

    /**
     * 销毁 DockWindow
     *
     * @param dockWindow dockWindow
     */
    void destroyDockWindow(IDockWindow dockWindow);

    /**
     * 激活 DockWindow
     *
     * @param dockWindow dockWindow
     */
    void activeDockWindow(IDockWindow dockWindow);

    /**
     * 创建 ContentsView
     *
     * @param typeName 内容视图类型
     * @param key 内容视图 key
     * @return contentsView
     */
    IContentsView createContentsView(String typeName, String key);

    /**
     * 创建 ContentsView
     *
     * @param typeName 内容视图类型
     * @param key 内容视图 key
     * @param caption 内容视图标题
     * @return contentsView
     */
    IContentsView createContentsView(String typeName, String key, String caption);

    /**
     * 激活 ContentsView
     *
     * @param contentsView contentsView
     */
    void activeContentsView(IContentsView contentsView);

    /**
     * 设置 ContentsView 标题
     *
     * @param contentsView contentsView
     * @param contentsViewText contentsView 标题
     */
    void setContentsViewText(IContentsView contentsView, String contentsViewText);

    /**
     * 获取 ContentsView 标题，若不存在 contentsView，返回 null，否则返回其标题
     *
     * @param contentsView contentsView
     * @return contentsView 标题
     */
    String getContentsViewText(IContentsView contentsView);

    /**
     * 关闭 ContentsView
     *
     * @param contentsView contentsView
     */
    void closeContentsView(IContentsView contentsView);

    /**
     * 设置静态框标题
     *
     * @param staticPlugin 静态框
     * @param caption 静态框标题
     */
    void setStaticCaption(IStatic staticPlugin, String caption);

    /**
     * 设置进度条值
     *
     * @param progress 进度条
     * @param value 进度条值
     */
    void setProgressValue(IProgress progress, int value);

    /**
     * 设置 GalleryItem 的 Check 状态
     *
     * @param galleryItem galleryItem
     * @param checkState check 状态
     */
    void setGalleryItemCheckState(GalleryItem galleryItem, boolean checkState);

    /**
     * 按照 Step 属性的数量增加进度栏的当前位置
     *
     * @param progress 进度栏
     */
    void performStepProgress(IProgress progress);

    /**
     * 按指定的数量增加进度栏的当前位置
     *
     * @param progress 进度栏
     * @param value 数量
     */
    void incrementProgress(IProgress progress, int value);

    /**
     * 设置 IEditCombobox 的 Text
     *
     * @param editCombobox editCombobox
     * @param text 文本
     */
    void setEditComboboxText(IEditCombobox editCombobox, String text);

    /**
     * 设置 IEditCombobox 的 EditValue
     *
     * @param editCombobox editCombobox
     * @param value 对象
     */
    void setEditComboboxValue(IEditCombobox editCombobox, Object value);

    /**
     * 增加 IEditCombobox 的项(当 IEditCombobox 的 IsDropDown 为 true 时，此方法才有意义)
     *
     * @param editCombobox editCombobox
     * @param comboBoxItem comboBoxItem
     * @return 索引
     */
    int addComboBoxItem(IEditCombobox editCombobox, ComboBoxItem comboBoxItem);

    /**
     * 删除 IEditCombobox 的项(当 IEditCombobox 的 IsDropDown 为 true 时，此方法才有意义)
     *
     * @param editCombobox editCombobox
     * @param value editCombobox 的项
     */
    void removeComboBoxItem(IEditCombobox editCombobox, Object value);

    /**
     * 清空 IEditCombobox 的下拉项(当 IEditCombobox 的 IsDropDown 为 true 时，此方法才有意义)
     *
     * @param editCombobox editCombobox
     */
    void clearComboBoxItem(IEditCombobox editCombobox);

    /**
     * 关闭 DropDownControl
     *
     * @param dropDowm dropDowm
     */
    void closeUpDropDownControl(IDropDown dropDowm);

    /**
     * 设置插件图标(支持 ICommand、ICheckCommand、IDropDown 插件)
     *
     * @param plugin 插件
     * @param image 图标
     */
    void setPluginImage(IPlugin plugin, Image image);

    /**
     * 设置插件标题(支持 ICommand插件、IDockWindow 插件)
     *
     * @param plugin 插件
     * @param caption 标题
     */
    void setPluginCaption(IPlugin plugin, String caption);
}
