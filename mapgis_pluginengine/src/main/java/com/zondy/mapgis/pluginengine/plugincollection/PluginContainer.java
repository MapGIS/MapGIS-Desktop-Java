package com.zondy.mapgis.pluginengine.plugincollection;

import com.zondy.mapgis.base.XString;
import com.zondy.mapgis.pluginengine.events.*;
import com.zondy.mapgis.pluginengine.plugin.*;
import com.zondy.mapgis.pluginengine.ui.*;
import com.zondy.mapgis.utilities.PackageUtility;
import javafx.scene.image.Image;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 插件容器
 *
 * @author cxy
 * @date 2019/09/11
 */
public class PluginContainer implements IPluginContainer {
    //region 私有成员

    private Map<String, IDropDown> dropDowns;
    private Map<String, IConnect> connects;
    private Map<String, ICommand> commands;
    private Map<String, ICheckCommand> checkCommands;
    private Map<String, IEditCombobox> editComboboxes;
    private Map<String, ICheckBox> checkBoxes;
    private Map<String, IStatic> statics;
    private Map<String, IProgress> progresses;
    private Map<String, IGallery> galleries;
    private Map<String, IDockWindow> dockWindows;
    private Map<String, IContentsView> contentsViews;
    private Map<String, IWelcomeScreen> welcomeScreens;

    private Map<String, IToolBar> toolBars;
    private Map<String, IMenuBar> menuBars;
    private Map<String, IRibbonPageCategory> ribbonPageCategories;
    private Map<String, IRibbonPage> ribbonPages;
    private Map<String, IRibbonPageGroup> ribbonPageGroups;
    private Map<String, IStatusBar> statusBars;

    private Map<String, Type> dockWindowsTypes;
    private Map<String, Type> contentsViewsTypes;

    /**
     * 插件程序集集合
     */
    private ArrayList<String> assemblyArray;
    /**
     * 扩展包集合
     */
    private ArrayList<String> packageArray;

    private Map<String, ISubItem> subItems;
    private Map<String, IFunction> functions;

    //endregion

    /**
     * 构造器
     */
    public PluginContainer() {
        // 初始化所有的集合容器
        this.dropDowns = new HashMap<>();
        this.connects = new HashMap<>();
        this.commands = new HashMap<>();
        this.checkCommands = new HashMap<>();
        this.editComboboxes = new HashMap<>();
        this.checkBoxes = new HashMap<>();
        this.statics = new HashMap<>();
        this.progresses = new HashMap<>();
        this.galleries = new HashMap<>();
        this.dockWindows = new HashMap<>();
        this.contentsViews = new HashMap<>();
        this.welcomeScreens = new HashMap<>();

        this.toolBars = new HashMap<>();
        this.menuBars = new HashMap<>();
        this.ribbonPageCategories = new HashMap<>();
        this.ribbonPages = new HashMap<>();
        this.ribbonPageGroups = new HashMap<>();
        this.statusBars = new HashMap<>();

        this.dockWindowsTypes = new HashMap<>();
        this.contentsViewsTypes = new HashMap<>();

        this.assemblyArray = new ArrayList<>();
        this.packageArray = new ArrayList<>();

        this.subItems = new HashMap<>();
        this.functions = new HashMap<>();
    }

    //region IPluginContainer 属性

    /**
     * 获得 DropDown 集合
     *
     * @return DropDown 集合
     */
    @Override
    public Map<String, IDropDown> getDropDowns() {
        return this.dropDowns;
    }

    /**
     * 获得 Command 集合
     *
     * @return Command 集合
     */
    @Override
    public Map<String, ICommand> getCommands() {
        return this.commands;
    }

    /**
     * 获得 CheckCommand 集合
     *
     * @return CheckCommand 集合
     */
    @Override
    public Map<String, ICheckCommand> getCheckCommands() {
        return this.checkCommands;
    }

    /**
     * 获得 EditCombobox 集合
     *
     * @return EditCombobox 集合
     */
    @Override
    public Map<String, IEditCombobox> getEditComboboxes() {
        return this.editComboboxes;
    }

    /**
     * 获得 Static 集合
     *
     * @return Static 集合
     */
    @Override
    public Map<String, IStatic> getStatics() {
        return this.statics;
    }

    /**
     * 获得 Progress 集合
     *
     * @return Progress 集合
     */
    @Override
    public Map<String, IProgress> getProgresses() {
        return this.progresses;
    }

    /**
     * 获得 ToolBar 集合
     *
     * @return ToolBar 集合
     */
    @Override
    public Map<String, IToolBar> getToolBars() {
        return this.toolBars;
    }

    /**
     * 获得 MenuBar 集合
     *
     * @return MenuBar 集合
     */
    @Override
    public Map<String, IMenuBar> getMenuBars() {
        return this.menuBars;
    }

    /**
     * 获得 StatusBar 集合
     *
     * @return StatusBar 集合
     */
    @Override
    public Map<String, IStatusBar> getStatusBars() {
        return this.statusBars;
    }

    /**
     * 获得 DockWindow 集合
     *
     * @return DockWindow 集合
     */
    @Override
    public Map<String, IDockWindow> getDockWindows() {
        return this.dockWindows;
    }

    /**
     * 获得 ContentsView 集合
     *
     * @return ContentsView 集合
     */
    @Override
    public Map<String, IContentsView> getContentsViews() {
        return this.contentsViews;
    }

    /**
     * 获得 RibbonPageCategory 集合
     *
     * @return RibbonPageCategory 集合
     */
    @Override
    public Map<String, IRibbonPageCategory> getRibbonPageCategories() {
        return this.ribbonPageCategories;
    }

    /**
     * 获得 RibbonPage 集合
     *
     * @return RibbonPage 集合
     */
    @Override
    public Map<String, IRibbonPage> getRibbonPages() {
        return this.ribbonPages;
    }

    /**
     * 获得 RibbonPageGroup 集合
     *
     * @return RibbonPageGroup 集合
     */
    @Override
    public Map<String, IRibbonPageGroup> getRibbonPageGroups() {
        return this.ribbonPageGroups;
    }

    /**
     * 获得 CheckBox 集合
     *
     * @return CheckBox 集合
     */
    @Override
    public Map<String, ICheckBox> getCheckBoxes() {
        return this.checkBoxes;
    }

    /**
     * 获得 Gallery 集合
     *
     * @return Gallery 集合
     */
    @Override
    public Map<String, IGallery> getGalleries() {
        return this.galleries;
    }

    /**
     * 获得 WelcomeScreen 集合
     *
     * @return WelcomeScreen 集合
     */
    @Override
    public Map<String, IWelcomeScreen> getWelcomeScreens() {
        return this.welcomeScreens;
    }

    /**
     * 获得 SubItem 集合
     *
     * @return SubItem 集合
     */
    @Override
    public Map<String, ISubItem> getSubItems() {
        return this.subItems;
    }

    /**
     * 获得 Function 集合
     *
     * @return Function 集合
     */
    @Override
    public Map<String, IFunction> getFunctions() {
        return this.functions;
    }

    /**
     * 获得已经加载的程序集路径集合
     *
     * @return AssemblyArray 集合
     */
    @Override
    public ArrayList getAssemblyArray() {
        return this.assemblyArray;
    }

    /**
     * 获得已经加载扩展包集合
     *
     * @return PackageArray 集合
     */
    @Override
    public ArrayList getPackageArray() {
        return this.packageArray;
    }

    //endregion

    //region IPluginContainer 事件

    private ArrayList<PluginLoadedListener> pluginLoadedListeners = new ArrayList<>();

    /**
     * 添加插件加载事件监听器
     *
     * @param pluginLoadedListener 插件加载事件监听器
     */
    @Override
    public void addPluginLoadedListener(PluginLoadedListener pluginLoadedListener) {
        this.pluginLoadedListeners.add(pluginLoadedListener);
    }

    /**
     * 删除插件加载事件监听器
     *
     * @param pluginLoadedListener 插件加载事件监听器
     */
    @Override
    public void removePluginLoadedListener(PluginLoadedListener pluginLoadedListener) {
        this.pluginLoadedListeners.remove(pluginLoadedListener);
    }

    /**
     * 触发插件加载事件
     *
     * @param pluginLoadedEvent 插件加载事件监听器
     */
    @Override
    public void firePluginLoaded(PluginLoadedEvent pluginLoadedEvent) {
        for (PluginLoadedListener pluginLoadedListener : this.pluginLoadedListeners) {
            pluginLoadedListener.pluginLoaded(pluginLoadedEvent);
        }
    }

    private ArrayList<PluginUnLoadedListener> pluginUnLoadedListeners = new ArrayList<>();

    /**
     * 添加插件卸载事件监听器
     *
     * @param pluginUnLoadedListener 插件卸载事件监听器
     */
    @Override
    public void addPluginUnLoadedListener(PluginUnLoadedListener pluginUnLoadedListener) {
        this.pluginUnLoadedListeners.add(pluginUnLoadedListener);
    }

    /**
     * 删除插件卸载事件监听器
     *
     * @param pluginUnLoadedListener 插件卸载事件监听器
     */
    @Override
    public void removePluginUnLoadedListener(PluginUnLoadedListener pluginUnLoadedListener) {
        this.pluginUnLoadedListeners.remove(pluginUnLoadedListener);
    }

    /**
     * 触发插件卸载事件
     *
     * @param pluginUnLoadedEvent 插件卸载事件监听器
     */
    @Override
    public void firePluginUnLoaded(PluginUnLoadedEvent pluginUnLoadedEvent) {
        for (PluginUnLoadedListener pluginUnLoadedListener : this.pluginUnLoadedListeners) {
            pluginUnLoadedListener.pluginUnLoaded(pluginUnLoadedEvent);
        }
    }

    private ArrayList<ContentsViewChangedListener> contentsViewChangedListeners = new ArrayList<>();

    /**
     * 添加激活内容视图改变事件监听器
     *
     * @param contentsViewChangedListener 激活内容视图改变事件监听器
     */
    @Override
    public void addContentsViewChangedListener(ContentsViewChangedListener contentsViewChangedListener) {
        this.contentsViewChangedListeners.add(contentsViewChangedListener);
    }

    /**
     * 删除激活内容视图改变事件监听器
     *
     * @param contentsViewChangedListener 激活内容视图改变事件监听器
     */
    @Override
    public void removeContentsViewChangedListener(ContentsViewChangedListener contentsViewChangedListener) {
        this.contentsViewChangedListeners.remove(contentsViewChangedListener);
    }

    /**
     * 触发激活内容视图改变事件
     *
     * @param contentsViewChangedEvent 激活内容视图改变事件
     */
    @Override
    public void fireContentsViewChanged(ContentsViewChangedEvent contentsViewChangedEvent) {
        for (ContentsViewChangedListener contentsViewChangedListener : this.contentsViewChangedListeners) {
            contentsViewChangedListener.contentsViewChanged(contentsViewChangedEvent);
        }
    }

    private ArrayList<ContentsViewClosingListener> contentsViewClosingListeners = new ArrayList<>();

    /**
     * 添加内容视图关闭前事件监听器
     *
     * @param contentsViewClosingListener 内容视图关闭前事件监听器
     */
    @Override
    public void addContentsViewClosingListener(ContentsViewClosingListener contentsViewClosingListener) {
        this.contentsViewClosingListeners.add(contentsViewClosingListener);
    }

    /**
     * 删除内容视图关闭前事件监听器
     *
     * @param contentsViewClosingListener 内容视图关闭前事件监听器
     */
    @Override
    public void removeContentsViewClosingListener(ContentsViewClosingListener contentsViewClosingListener) {
        this.contentsViewClosingListeners.remove(contentsViewClosingListener);
    }

    /**
     * 触发内容视图关闭前事件
     *
     * @param contentsViewClosingEvent 内容视图关闭前事件
     */
    @Override
    public void fireContentsViewClosing(ContentsViewClosingEvent contentsViewClosingEvent) {
        for (ContentsViewClosingListener contentsViewClosingListener : this.contentsViewClosingListeners) {
            contentsViewClosingListener.contentsViewClosing(contentsViewClosingEvent);
        }
    }

//    private ArrayList<DockWindowChangedListener> dockWindowChangedListeners = new ArrayList<>();
//
//    /**
//     * 添加激活停靠面板改变事件监听器
//     *
//     * @param dockWindowChangedListener 激活停靠面板改变事件监听器
//     */
//    @Override
//    public void addDockWindowChangedListener(DockWindowChangedListener dockWindowChangedListener) {
//        this.dockWindowChangedListeners.add(dockWindowChangedListener);
//    }
//
//    /**
//     * 删除激活停靠面板改变事件监听器
//     *
//     * @param dockWindowChangedListener 激活停靠面板改变事件监听器
//     */
//    @Override
//    public void removeDockWindowChangedListener(DockWindowChangedListener dockWindowChangedListener) {
//        this.dockWindowChangedListeners.remove(dockWindowChangedListener);
//    }
//
//    /**
//     * 触发激活停靠面板改变事件
//     *
//     * @param dockWindowChangedEvent 激活停靠面板改变事件
//     */
//    @Override
//    public void fireDockWindowChanged(DockWindowChangedEvent dockWindowChangedEvent) {
//        for (DockWindowChangedListener dockWindowChangedListener : this.dockWindowChangedListeners) {
//            dockWindowChangedListener.dockWindowChanged(dockWindowChangedEvent);
//        }
//    }

    private ArrayList<CommandClickedListener> commandClickedListeners = new ArrayList<>();

    /**
     * 添加 Command 插件点击事件监听器
     *
     * @param commandClickedListener Command 插件点击事件监听器
     */
    @Override
    public void addCommandClickedListener(CommandClickedListener commandClickedListener) {
        this.commandClickedListeners.add(commandClickedListener);
    }

    /**
     * 删除 Command 插件点击事件监听器
     *
     * @param commandClickedListener Command 插件点击事件监听器
     */
    @Override
    public void removeCommandClickedListener(CommandClickedListener commandClickedListener) {
        this.commandClickedListeners.remove(commandClickedListener);
    }

    /**
     * 触发 Command 插件点击事件
     *
     * @param commandClickedEvent Command 插件点击事件
     */
    @Override
    public void fireCommandClicked(CommandClickedEvent commandClickedEvent) {
        for (CommandClickedListener commandClickedListener : this.commandClickedListeners) {
            commandClickedListener.commandClicked(commandClickedEvent);
        }
    }

    //endregion

    //region IPluginContainer方法

    @Override
    public void setPluginVisible(IPlugin plugin, boolean visible) {
        if (this.pluginVisibleChangedListeners != null && plugin != null) {
            this.firePluginVisibleChanged(new PluginVisibleChangedEvent(this, plugin, visible));
        }
    }

    @Override
    public boolean pluginIsVisible(IPlugin plugin) {
        if (this.pluginIsVisibleListeners != null && plugin != null) {
            return this.firePluginIsVisible(new PluginIsVisibleEvent(this, plugin));
        }
        return false;
    }

    @Override
    public void setPluginEnable(IPlugin plugin, boolean enable) {
        if (this.pluginEnableChangedListeners != null && plugin != null) {
            this.firePluginEnableChanged(new PluginEnableChangedEvent(this, plugin, enable));
        }
    }

    @Override
    public boolean pluginIsEnable(IPlugin plugin) {
        if (this.pluginIsEnableListeners != null && plugin != null) {
            return this.firePluginIsEnable(new PluginIsEnableEvent(this, plugin));
        }
        return false;
    }

//    @Override
//    public void selectPage(IRibbonPage ribbonPage) {
//        if (this.selectRibbonPageListeners != null && ribbonPage != null) {
//            this.fireSelectRibbonPage(new SelectRibbonPageEvent(this, ribbonPage));
//        }
//    }

    @Override
    public void setCommandChecked(ICheckCommand command, boolean checkState) {
        if (this.setCommandCheckedListeners != null && command != null) {
            this.fireSetCommandChecked(new SetCommandCheckedEvent(this, command, checkState));
        }
    }

//    @Override
//    public void setCheckBoxState(ICheckBox checkBox, CheckStateEnum checkState) {
//        if (this.setCheckBoxStateListeners != null && checkBox != null) {
//            this.fireSetCheckBoxState(new SetCheckBoxStateEvent(this, checkBox, checkState));
//        }
//    }

    @Override
    public IDockWindow createDockWindow(String typeName) {
        if (typeName == null || typeName.isEmpty()) {
            return null;
        }
        Type myType = this.dockWindowsTypes.getOrDefault(typeName, null);
        if (myType == null) {
            return null;
        }
        IDockWindow tempDW = this.dockWindows.getOrDefault(typeName, null);
        if (tempDW != null) {
            return tempDW;
        }

        Object o = this.createInstance((Class<?>) myType);
        tempDW = o instanceof IDockWindow ? ((IDockWindow) o) : null;

        if (tempDW != null && this.createDockWindowListeners != null) {
            this.dockWindows.put(typeName, tempDW);
            this.fireCreateDockWindow(new CreateDockWindowEvent(this, tempDW));
        }
        return tempDW;
    }

    /**
     * 创建 Type 的实例
     *
     * @param type 类型
     * @return 类型的实例
     */
    private Object createInstance(Class<?> type) {
        Object rtn = null;
        try {
            rtn = type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return rtn;
    }

    @Override
    public void destroyDockWindow(IDockWindow dockWindow) {
        if (this.destroyDockWindowListeners != null && dockWindow != null) {
            this.fireDestroyDockWindow(new DestroyDockWindowEvent(this, dockWindow));
        }
    }

    @Override
    public void activeDockWindow(IDockWindow dockWindow) {
        if (this.setActiveDockWindowListeners != null && dockWindow != null) {
            this.fireSetActiveDockWindow(new SetActiveDockWindowEvent(this, dockWindow));
        }
    }

    @Override
    public IContentsView createContentsView(String typeName, String key) {
        return createContentsView(typeName, key, null);
    }

    @Override
    public IContentsView createContentsView(String typeName, String key, String caption) {
        if (typeName == null || typeName.isEmpty()) {
            return null;
        }

        IContentsView contentsView = this.contentsViews.getOrDefault(XString.isNullOrEmpty(key) ? typeName : key, null);
        if (contentsView != null) {
            return contentsView;
        }

        Type type = this.contentsViewsTypes.getOrDefault(typeName, null);
        if (type == null) {
            return null;
        }

        Object o = this.createInstance((Class<?>) type);
        contentsView = o instanceof IContentsView ? ((IContentsView) o) : null;

        if (contentsView != null) {
            String param;
            if (key != null && !key.isEmpty()) {
                this.contentsViews.put(key, contentsView);
                param = key;
            } else {
                this.contentsViews.put(typeName, contentsView);
                param = typeName;
            }
            this.firePluginAdded(new PluginAddedEvent(this, contentsView, param, caption));
        }
        return contentsView;
    }

    @Override
    public void activeContentsView(IContentsView contentsView) {
        if (this.pluginActiveChangedListeners != null && this.contentsViews.containsValue(contentsView)) {
            this.firePluginActiveChanged(new PluginActiveChangedEvent(this, contentsView));
        }
    }

    @Override
    public void setContentsViewText(IContentsView contentsView, String contentsViewText) {
        if (this.setContentsViewTextListeners != null && contentsView != null) {
            this.fireSetContentsViewText(new SetContentsViewTextEvent(this, contentsView, contentsViewText));
        }
    }

    @Override
    public String getContentsViewText(IContentsView contentsView) {
        if (this.getContentsViewTextListeners != null && contentsView != null) {
            return this.fireGetContentsViewText(new GetContentsViewTextEvent(this, contentsView));
        }
        return null;
    }

    @Override
    public void closeContentsView(IContentsView contentsView) {
        if (this.closeContentsViewListeners != null && contentsView != null) {
            this.fireCloseContentsView(new CloseContentsViewEvent(this, contentsView));
        }
    }

    @Override
    public void setStaticCaption(IStatic staticPlugin, String caption) {
        if (this.setStaticCaptionListeners != null && staticPlugin != null) {
            this.fireSetStaticCaption(new SetStaticCaptionEvent(this, staticPlugin, caption));
        }
    }

    @Override
    public void setProgressValue(IProgress progress, int value) {
        if (this.setProgressValueListeners != null && progress != null) {
            this.fireSetProgressValue(new SetProgressValueEvent(this, progress, value));
        }
    }

    @Override
    public void setGalleryItemCheckState(GalleryItem galleryItem, boolean checkState) {
        if (this.setGalleryItemCheckStateListeners != null && galleryItem != null) {
            this.fireSetGalleryItemCheckState(new SetGalleryItemCheckStateEvent(this, galleryItem, checkState));
        }
    }

    @Override
    public void performStepProgress(IProgress progress) {
        if (this.performStepProgressListeners != null && progress != null) {
            this.firePerformStepProgress(new PerformStepProgressEvent(this, progress));
        }
    }

    @Override
    public void incrementProgress(IProgress progress, int value) {
        if (this.incrementProgressListeners != null && progress != null) {
            this.fireIncrementProgress(new IncrementProgressEvent(this, progress, value));
        }
    }

    @Override
    public void setEditComboboxText(IEditCombobox editCombobox, String text) {
        if (this.setEditComboboxTextListeners != null && editCombobox != null) {
            this.fireSetEditComboboxText(new SetEditComboboxTextEvent(this, editCombobox, text));
        }
    }

    @Override
    public void setEditComboboxValue(IEditCombobox editCombobox, Object value) {
        if (this.setEditComboboxValueListeners != null && editCombobox != null) {
            this.fireSetEditComboboxValue(new SetEditComboboxValueEvent(this, editCombobox, value));
        }
    }

    @Override
    public int addComboBoxItem(IEditCombobox editCombobox, ComboBoxItem comboBoxItem) {
        if (this.addComboBoxItemListeners != null && editCombobox != null && comboBoxItem != null && comboBoxItem.getValue() != null) {
            this.fireAddComboBoxItem(new AddComboBoxItemEvent(this, editCombobox, comboBoxItem));
        }
        return -1;
    }

    @Override
    public void removeComboBoxItem(IEditCombobox editCombobox, Object value) {
        if (this.removeComboBoxItemListeners != null && editCombobox != null && value != null) {
            this.fireRemoveComboBoxItem(new RemoveComboBoxItemEvent(this, editCombobox, value));
        }
    }

    @Override
    public void clearComboBoxItem(IEditCombobox editCombobox) {
        if (this.clearComboBoxItemListeners != null && editCombobox != null) {
            this.fireClearComboBoxItem(new ClearComboBoxItemEvent(this, editCombobox));
        }
    }

    @Override
    public void closeUpDropDownControl(IDropDown dropDowm) {
        if (this.closeUpDropDownControlListeners != null && dropDowm != null) {
            this.fireCloseUpDropDownControl(new CloseUpDropDownControlEvent(this, dropDowm));
        }
    }

    @Override
    public void setPluginImage(IPlugin plugin, Image image) {
        if (this.setPluginImageListeners != null && plugin != null && (plugin instanceof ICommand || plugin instanceof ICheckCommand || plugin instanceof IDropDown)) {
            this.fireSetPluginImage(new SetPluginImageEvent(this, plugin, image));
        }
    }

    @Override
    public void setPluginCaption(IPlugin plugin, String caption) {
        if (this.setPluginCaptionListeners != null && plugin != null) {
            this.fireSetPluginCaption(new SetPluginCaptionEvent(this, plugin, caption));
        }
    }

    //endregion

    //region 通知宿主事件

    //public static EventHelper<BeginLoadListener, BeginLoadEvent> beginLoadListenerss;

    private ArrayList<BeginLoadListener> beginLoadPackageListeners = new ArrayList<>();

    /**
     * 添加开始加载包事件监听器
     *
     * @param beginLoadPackageListener 开始加载包事件监听器
     */
    public void addBeginLoadPackageListener(BeginLoadListener beginLoadPackageListener) {
        this.beginLoadPackageListeners.add(beginLoadPackageListener);
    }

    /**
     * 删除开始加载包事件监听器
     *
     * @param beginLoadPackageListener 开始加载包事件监听器
     */
    public void removeBeginLoadPackageListener(BeginLoadListener beginLoadPackageListener) {
        this.beginLoadPackageListeners.remove(beginLoadPackageListener);
    }

    /**
     * 触发开始加载包事件
     *
     * @param beginLoadPackageEvent 开始加载包事件
     */
    public int fireBeginLoadPackage(BeginLoadEvent beginLoadPackageEvent) {
        int rtn = 0;
        for (BeginLoadListener beginLoadPackageListener : this.beginLoadPackageListeners) {
            rtn = beginLoadPackageListener.beginLoad(beginLoadPackageEvent);
        }
        return rtn;
    }

    private ArrayList<LoadPackageListener> loadPackageListeners = new ArrayList<>();

    /**
     * 添加加载包事件监听器
     *
     * @param loadPackageListener 加载包事件监听器
     */
    public void addLoadPackageListener(LoadPackageListener loadPackageListener) {
        this.loadPackageListeners.add(loadPackageListener);
    }

    /**
     * 删除加载包事件监听器
     *
     * @param loadPackageListener 加载包事件监听器
     */
    public void removeLoadPackageListener(LoadPackageListener loadPackageListener) {
        this.loadPackageListeners.remove(loadPackageListener);
    }

    /**
     * 触发加载包事件
     *
     * @param loadPackageEvent 加载包事件
     */
    public void fireloadPackage(LoadPackageEvent loadPackageEvent) {
        for (LoadPackageListener loadPackageListener : this.loadPackageListeners) {
            loadPackageListener.loadPackage(loadPackageEvent);
        }
    }

    private ArrayList<UnLoadPackageListener> unLoadPackageListeners = new ArrayList<>();

    /**
     * 添加卸载包事件监听器
     *
     * @param unLoadPackageListener 卸载包事件监听器
     */
    public void addUnLoadPackageListener(UnLoadPackageListener unLoadPackageListener) {
        this.unLoadPackageListeners.add(unLoadPackageListener);
    }

    /**
     * 删除卸载包事件监听器
     *
     * @param unLoadPackageListener 卸载包事件监听器
     */
    public void removeUnLoadPackageListener(UnLoadPackageListener unLoadPackageListener) {
        this.unLoadPackageListeners.remove(unLoadPackageListener);
    }

    /**
     * 触发卸载包事件
     *
     * @param unLoadPackageEvent 卸载包事件
     */
    public void fireUnLoadPackage(UnLoadPackageEvent unLoadPackageEvent) {
        for (UnLoadPackageListener unLoadPackageListener : this.unLoadPackageListeners) {
            unLoadPackageListener.unLoadPackage(unLoadPackageEvent);
        }
    }

    //public event BeginLoadHandler BeginLoadAssemblyEvent;
    //public event LoadAssemblyHandler LoadAssemblyEvent;
    //public event UnLoadAssemblyHandler UnLoadAssemblyEvent;

    private ArrayList<PluginAddedListener> pluginAddedListeners = new ArrayList<>();

    /**
     * 添加添加插件事件监听器
     *
     * @param pluginAddedListener 添加插件事件监听器
     */
    public void addPluginAddedListener(PluginAddedListener pluginAddedListener) {
        this.pluginAddedListeners.add(pluginAddedListener);
    }

    /**
     * 删除添加插件事件监听器
     *
     * @param pluginAddedListener 添加插件事件监听器
     */
    public void removePluginAddedListener(PluginAddedListener pluginAddedListener) {
        this.pluginAddedListeners.remove(pluginAddedListener);
    }

    /**
     * 触发添加插件事件
     *
     * @param pluginAddedEvent 添加插件事件
     */
    public void firePluginAdded(PluginAddedEvent pluginAddedEvent) {
        for (PluginAddedListener pluginAddedListener : this.pluginAddedListeners) {
            pluginAddedListener.pluginAdded(pluginAddedEvent);
        }
    }

    //public event PluginRemovedHandler PluginRemoved;
    private ArrayList<PluginRemovedListener> pluginRemovedListeners = new ArrayList<>();

    public void addPluginRemovedListener(PluginRemovedListener pluginRemovedListener) {
        this.pluginRemovedListeners.add(pluginRemovedListener);
    }

    public void removePluginRemovedListener(PluginRemovedListener pluginRemovedListener) {
        this.pluginRemovedListeners.remove(pluginRemovedListener);
    }

    public void firePluginRemoved(PluginRemovedEvent pluginRemovedEvent) {
        for (PluginRemovedListener pluginRemovedListener : this.pluginRemovedListeners) {
            pluginRemovedListener.pluginRemoved(pluginRemovedEvent);
        }
    }

    //public event PluginVisibleChangedHandler PluginVisibleChanged;
    private ArrayList<PluginVisibleChangedListener> pluginVisibleChangedListeners = new ArrayList<>();

    public void addPluginVisibleChangedListener(PluginVisibleChangedListener pluginVisibleChangedListener) {
        this.pluginVisibleChangedListeners.add(pluginVisibleChangedListener);
    }

    public void removePluginVisibleChangedListener(PluginVisibleChangedListener pluginVisibleChangedListener) {
        this.pluginVisibleChangedListeners.remove(pluginVisibleChangedListener);
    }

    public void firePluginVisibleChanged(PluginVisibleChangedEvent pluginVisibleChangedEvent) {
        for (PluginVisibleChangedListener pluginVisibleChangedListener : this.pluginVisibleChangedListeners) {
            pluginVisibleChangedListener.pluginVisibleChanged(pluginVisibleChangedEvent);
        }
    }

    //public event PluginIsVisibleHandler PluginIsVisibleEvent;
    private ArrayList<PluginIsVisibleListener> pluginIsVisibleListeners = new ArrayList<>();

    public void addPluginIsVisibleListener(PluginIsVisibleListener pluginIsVisibleListener) {
        this.pluginIsVisibleListeners.add(pluginIsVisibleListener);
    }

    public void removePluginIsVisibleListener(PluginIsVisibleListener pluginIsVisibleListener) {
        this.pluginIsVisibleListeners.remove(pluginIsVisibleListener);
    }

    public boolean firePluginIsVisible(PluginIsVisibleEvent pluginIsVisibleEvent) {
        boolean rtn = false;
        for (PluginIsVisibleListener pluginIsVisibleListener : this.pluginIsVisibleListeners) {
            rtn = pluginIsVisibleListener.pluginIsVisible(pluginIsVisibleEvent);
        }
        return rtn;
    }

    //public event PluginEnableChangedHandler PluginEnableChanged;
    private ArrayList<PluginEnableChangedListener> pluginEnableChangedListeners = new ArrayList<>();

    public void addPluginEnableChangedListener(PluginEnableChangedListener pluginEnableChangedListener) {
        this.pluginEnableChangedListeners.add(pluginEnableChangedListener);
    }

    public void removePluginEnableChangedListener(PluginEnableChangedListener pluginEnableChangedListener) {
        this.pluginEnableChangedListeners.remove(pluginEnableChangedListener);
    }

    public void firePluginEnableChanged(PluginEnableChangedEvent pluginEnableChangedEvent) {
        for (PluginEnableChangedListener pluginEnableChangedListener : this.pluginEnableChangedListeners) {
            pluginEnableChangedListener.pluginEnableChanged(pluginEnableChangedEvent);
        }
    }

    //public event PluginIsEnableHandler PluginIsEnableEvent;
    private ArrayList<PluginIsEnableListener> pluginIsEnableListeners = new ArrayList<>();

    public void addPluginIsEnableListener(PluginIsEnableListener pluginIsEnableListener) {
        this.pluginIsEnableListeners.add(pluginIsEnableListener);
    }

    public void removePluginIsEnableListener(PluginIsEnableListener pluginIsEnableListener) {
        this.pluginIsEnableListeners.remove(pluginIsEnableListener);
    }

    public boolean firePluginIsEnable(PluginIsEnableEvent pluginIsEnableEvent) {
        boolean rtn = false;
        for (PluginIsEnableListener pluginIsEnableListener : this.pluginIsEnableListeners) {
            rtn = pluginIsEnableListener.pluginIsEnable(pluginIsEnableEvent);
        }
        return rtn;
    }

    //public event PluginActiveChangedHandler PluginActiveChanged;
    private ArrayList<PluginActiveChangedListener> pluginActiveChangedListeners = new ArrayList<>();

    public void addPluginActiveChangedListener(PluginActiveChangedListener pluginActiveChangedListener) {
        this.pluginActiveChangedListeners.add(pluginActiveChangedListener);
    }

    public void removePluginActiveChangedListener(PluginActiveChangedListener pluginActiveChangedListener) {
        this.pluginActiveChangedListeners.remove(pluginActiveChangedListener);
    }

    public void firePluginActiveChanged(PluginActiveChangedEvent pluginActiveChangedEvent) {
        for (PluginActiveChangedListener pluginActiveChangedListener : this.pluginActiveChangedListeners) {
            pluginActiveChangedListener.pluginActiveChanged(pluginActiveChangedEvent);
        }
    }

    //public event SetContentsViewTextHandler SetContentsViewTextEvent;
    private ArrayList<SetContentsViewTextListener> setContentsViewTextListeners = new ArrayList<>();

    public void addSetContentsViewTextListener(SetContentsViewTextListener setContentsViewTextListener) {
        this.setContentsViewTextListeners.add(setContentsViewTextListener);
    }

    public void removeSetContentsViewTextListener(SetContentsViewTextListener setContentsViewTextListener) {
        this.setContentsViewTextListeners.remove(setContentsViewTextListener);
    }

    public void fireSetContentsViewText(SetContentsViewTextEvent setContentsViewTextEvent) {
        for (SetContentsViewTextListener setContentsViewTextListener : this.setContentsViewTextListeners) {
            setContentsViewTextListener.setContentsViewText(setContentsViewTextEvent);
        }
    }

    //public event GetContentsViewTextHandler GetContentsViewTextEvent;
    private ArrayList<GetContentsViewTextListener> getContentsViewTextListeners = new ArrayList<>();

    public void addGetContentsViewTextListener(GetContentsViewTextListener getContentsViewTextListener) {
        this.getContentsViewTextListeners.add(getContentsViewTextListener);
    }

    public void removeGetContentsViewTextListener(GetContentsViewTextListener getContentsViewTextListener) {
        this.getContentsViewTextListeners.remove(getContentsViewTextListener);
    }

    public String fireGetContentsViewText(GetContentsViewTextEvent getContentsViewTextEvent) {
        String rtn = "";
        for (GetContentsViewTextListener getContentsViewTextListener : this.getContentsViewTextListeners) {
            rtn = getContentsViewTextListener.getContentsViewText(getContentsViewTextEvent);
        }
        return rtn;
    }

    //public event CreateDockWindowHandler CreateDockWindowEvent;
    private ArrayList<CreateDockWindowListener> createDockWindowListeners = new ArrayList<>();

    public void addCreateDockWindowListener(CreateDockWindowListener createDockWindowListener) {
        this.createDockWindowListeners.add(createDockWindowListener);
    }

    public void removeCreateDockWindowListener(CreateDockWindowListener createDockWindowListener) {
        this.createDockWindowListeners.remove(createDockWindowListener);
    }

    public void fireCreateDockWindow(CreateDockWindowEvent createDockWindowEvent) {
        for (CreateDockWindowListener createDockWindowListener : this.createDockWindowListeners) {
            createDockWindowListener.createDockWindow(createDockWindowEvent);
        }
    }

    //public event DestroyDockWindowHandler DestroyDockWindowEvent;
    private ArrayList<DestroyDockWindowListener> destroyDockWindowListeners = new ArrayList<>();

    public void addDestroyDockWindowListener(DestroyDockWindowListener destroyDockWindowListener) {
        this.destroyDockWindowListeners.add(destroyDockWindowListener);
    }

    public void removeDestroyDockWindowListener(DestroyDockWindowListener destroyDockWindowListener) {
        this.destroyDockWindowListeners.remove(destroyDockWindowListener);
    }

    public void fireDestroyDockWindow(DestroyDockWindowEvent destroyDockWindowEvent) {
        for (DestroyDockWindowListener destroyDockWindowListener : this.destroyDockWindowListeners) {
            destroyDockWindowListener.destroyDockWindow(destroyDockWindowEvent);
        }
    }

    //public event SetActiveDockWindowHandler SetActiveDockWindowEvent;
    private ArrayList<SetActiveDockWindowListener> setActiveDockWindowListeners = new ArrayList<>();

    public void addSetActiveDockWindowListener(SetActiveDockWindowListener setActiveDockWindowListener) {
        this.setActiveDockWindowListeners.add(setActiveDockWindowListener);
    }

    public void removeSetActiveDockWindowListener(SetActiveDockWindowListener setActiveDockWindowListener) {
        this.setActiveDockWindowListeners.remove(setActiveDockWindowListener);
    }

    public void fireSetActiveDockWindow(SetActiveDockWindowEvent setActiveDockWindowEvent) {
        for (SetActiveDockWindowListener setActiveDockWindowListener : this.setActiveDockWindowListeners) {
            setActiveDockWindowListener.setActiveDockWindow(setActiveDockWindowEvent);
        }
    }

    //public event CloseContentsViewHandler CloseContentsViewEvent;
    private ArrayList<CloseContentsViewListener> closeContentsViewListeners = new ArrayList<>();

    public void addCloseContentsViewListener(CloseContentsViewListener closeContentsViewListener) {
        this.closeContentsViewListeners.add(closeContentsViewListener);
    }

    public void removeCloseContentsViewListener(CloseContentsViewListener closeContentsViewListener) {
        this.closeContentsViewListeners.remove(closeContentsViewListener);
    }

    public void fireCloseContentsView(CloseContentsViewEvent closeContentsViewEvent) {
        for (CloseContentsViewListener closeContentsViewListener : this.closeContentsViewListeners) {
            closeContentsViewListener.closeContentsView(closeContentsViewEvent);
        }
    }

    //public event SetEditComboboxTextHandler SetEditComboboxTextEvent;
    private ArrayList<SetEditComboboxTextListener> setEditComboboxTextListeners = new ArrayList<>();

    public void addSetEditComboboxTextListener(SetEditComboboxTextListener setEditComboboxTextListener) {
        this.setEditComboboxTextListeners.add(setEditComboboxTextListener);
    }

    public void removeSetEditComboboxTextListener(SetEditComboboxTextListener setEditComboboxTextListener) {
        this.setEditComboboxTextListeners.remove(setEditComboboxTextListener);
    }

    public void fireSetEditComboboxText(SetEditComboboxTextEvent setEditComboboxTextEvent) {
        for (SetEditComboboxTextListener setEditComboboxTextListener : this.setEditComboboxTextListeners) {
            setEditComboboxTextListener.setEditComboboxText(setEditComboboxTextEvent);
        }
    }

    //public event SetEditComboboxValueHandler SetEditComboboxValueEvent;
    private ArrayList<SetEditComboboxValueListener> setEditComboboxValueListeners = new ArrayList<>();

    public void addSetEditComboboxValueListener(SetEditComboboxValueListener setEditComboboxValueListener) {
        this.setEditComboboxValueListeners.add(setEditComboboxValueListener);
    }

    public void removeSetEditComboboxValueListener(SetEditComboboxValueListener setEditComboboxValueListener) {
        this.setEditComboboxValueListeners.remove(setEditComboboxValueListener);
    }

    public void fireSetEditComboboxValue(SetEditComboboxValueEvent setEditComboboxValueEvent) {
        for (SetEditComboboxValueListener setEditComboboxValueListener : this.setEditComboboxValueListeners) {
            setEditComboboxValueListener.setEditComboboxValue(setEditComboboxValueEvent);
        }
    }

    //public event ClearComboBoxItemHandler ClearComboBoxItemEvent;
    private ArrayList<ClearComboBoxItemListener> clearComboBoxItemListeners = new ArrayList<>();

    public void addClearComboBoxItemListener(ClearComboBoxItemListener clearComboBoxItemListener) {
        this.clearComboBoxItemListeners.add(clearComboBoxItemListener);
    }

    public void removeClearComboBoxItemListener(ClearComboBoxItemListener clearComboBoxItemListener) {
        this.clearComboBoxItemListeners.remove(clearComboBoxItemListener);
    }

    public void fireClearComboBoxItem(ClearComboBoxItemEvent clearComboBoxItemEvent) {
        for (ClearComboBoxItemListener clearComboBoxItemListener : this.clearComboBoxItemListeners) {
            clearComboBoxItemListener.clearComboBoxItem(clearComboBoxItemEvent);
        }
    }

    //public event SetStaticCaptionHandler SetStaticCaptionEvent;
    private ArrayList<SetStaticCaptionListener> setStaticCaptionListeners = new ArrayList<>();

    public void addSetStaticCaptionListener(SetStaticCaptionListener setStaticCaptionListener) {
        this.setStaticCaptionListeners.add(setStaticCaptionListener);
    }

    public void removeSetStaticCaptionListener(SetStaticCaptionListener setStaticCaptionListener) {
        this.setStaticCaptionListeners.remove(setStaticCaptionListener);
    }

    public void fireSetStaticCaption(SetStaticCaptionEvent setStaticCaptionEvent) {
        for (SetStaticCaptionListener setStaticCaptionListener : this.setStaticCaptionListeners) {
            setStaticCaptionListener.setStaticCaption(setStaticCaptionEvent);
        }
    }

    //public event SetProgressValueHandler SetProgressValueEvent;
    private ArrayList<SetProgressValueListener> setProgressValueListeners = new ArrayList<>();

    public void addSetProgressValueListener(SetProgressValueListener setProgressValueListener) {
        this.setProgressValueListeners.add(setProgressValueListener);
    }

    public void removeSetProgressValueListener(SetProgressValueListener setProgressValueListener) {
        this.setProgressValueListeners.remove(setProgressValueListener);
    }

    public void fireSetProgressValue(SetProgressValueEvent setProgressValueEvent) {
        for (SetProgressValueListener setProgressValueListener : this.setProgressValueListeners) {
            setProgressValueListener.setProgressValue(setProgressValueEvent);
        }
    }

    //public event SetCommandCheckedHandler SetCommandCheckedEvent = new ArrayList<>();
    private ArrayList<SetCommandCheckedListener> setCommandCheckedListeners = new ArrayList<>();

    public void addSetCommandCheckedListener(SetCommandCheckedListener setCommandCheckedListener) {
        this.setCommandCheckedListeners.add(setCommandCheckedListener);
    }

    public void removeSetCommandCheckedListener(SetCommandCheckedListener setCommandCheckedListener) {
        this.setCommandCheckedListeners.remove(setCommandCheckedListener);
    }

    public void fireSetCommandChecked(SetCommandCheckedEvent setCommandCheckedEvent) {
        for (SetCommandCheckedListener setCommandCheckedListener : this.setCommandCheckedListeners) {
            setCommandCheckedListener.setCommandChecked(setCommandCheckedEvent);
        }
    }

    //public event SetGalleryItemCheckStateHandler SetGalleryItemCheckStateEvent = new ArrayList<>();
    private ArrayList<SetGalleryItemCheckStateListener> setGalleryItemCheckStateListeners = new ArrayList<>();

    public void addSetGalleryItemCheckStateListener(SetGalleryItemCheckStateListener setGalleryItemCheckStateListener) {
        this.setGalleryItemCheckStateListeners.add(setGalleryItemCheckStateListener);
    }

    public void removeSetGalleryItemCheckStateListener(SetGalleryItemCheckStateListener setGalleryItemCheckStateListener) {
        this.setGalleryItemCheckStateListeners.remove(setGalleryItemCheckStateListener);
    }

    public void fireSetGalleryItemCheckState(SetGalleryItemCheckStateEvent setGalleryItemCheckStateEvent) {
        for (SetGalleryItemCheckStateListener setGalleryItemCheckStateListener : this.setGalleryItemCheckStateListeners) {
            setGalleryItemCheckStateListener.setGalleryItemCheckState(setGalleryItemCheckStateEvent);
        }
    }

    //public event PerformStepProgressHandler PerformStepProgressEvent;
    private ArrayList<PerformStepProgressListener> performStepProgressListeners = new ArrayList<>();

    public void addPerformStepProgressListener(PerformStepProgressListener performStepProgressListener) {
        this.performStepProgressListeners.add(performStepProgressListener);
    }

    public void removePerformStepProgressListener(PerformStepProgressListener performStepProgressListener) {
        this.performStepProgressListeners.remove(performStepProgressListener);
    }

    public void firePerformStepProgress(PerformStepProgressEvent performStepProgressEvent) {
        for (PerformStepProgressListener performStepProgressListener : this.performStepProgressListeners) {
            performStepProgressListener.performStepProgress(performStepProgressEvent);
        }
    }

    //public event IncrementProgressHandler IncrementProgressEvent;
    private ArrayList<IncrementProgressListener> incrementProgressListeners = new ArrayList<>();

    public void addIncrementProgressListener(IncrementProgressListener incrementProgressListener) {
        this.incrementProgressListeners.add(incrementProgressListener);
    }

    public void removeIncrementProgressListener(IncrementProgressListener incrementProgressListener) {
        this.incrementProgressListeners.remove(incrementProgressListener);
    }

    public void fireIncrementProgress(IncrementProgressEvent incrementProgressEvent) {
        for (IncrementProgressListener incrementProgressListener : this.incrementProgressListeners) {
            incrementProgressListener.incrementProgress(incrementProgressEvent);
        }
    }

    //public event AddComboBoxItemHandler AddComboBoxItemEvent;
    private ArrayList<AddComboBoxItemListener> addComboBoxItemListeners = new ArrayList<>();

    public void addAddComboBoxItemListener(AddComboBoxItemListener addComboBoxItemListener) {
        this.addComboBoxItemListeners.add(addComboBoxItemListener);
    }

    public void removeAddComboBoxItemListener(AddComboBoxItemListener addComboBoxItemListener) {
        this.addComboBoxItemListeners.remove(addComboBoxItemListener);
    }

    public void fireAddComboBoxItem(AddComboBoxItemEvent addComboBoxItemEvent) {
        for (AddComboBoxItemListener addComboBoxItemListener : this.addComboBoxItemListeners) {
            addComboBoxItemListener.addComboboxItem(addComboBoxItemEvent);
        }
    }

    //public event RemoveComboBoxItemHandler RemoveComboBoxItemEvent;
    private ArrayList<RemoveComboBoxItemListener> removeComboBoxItemListeners = new ArrayList<>();

    public void addRemoveComboBoxItemListener(RemoveComboBoxItemListener removeComboBoxItemListener) {
        this.removeComboBoxItemListeners.add(removeComboBoxItemListener);
    }

    public void removeRemoveComboBoxItemListener(RemoveComboBoxItemListener removeComboBoxItemListener) {
        this.removeComboBoxItemListeners.remove(removeComboBoxItemListener);
    }

    public void fireRemoveComboBoxItem(RemoveComboBoxItemEvent removeComboBoxItemEvent) {
        for (RemoveComboBoxItemListener removeComboBoxItemListener : this.removeComboBoxItemListeners) {
            removeComboBoxItemListener.removeComboBoxItem(removeComboBoxItemEvent);
        }
    }

    //public event CloseUpDropDownControlHandler CloseUpDropDownControlEvent;
    private ArrayList<CloseUpDropDownControlListener> closeUpDropDownControlListeners = new ArrayList<>();

    public void addCloseUpDropDownControlListener(CloseUpDropDownControlListener closeUpDropDownControlListener) {
        this.closeUpDropDownControlListeners.add(closeUpDropDownControlListener);
    }

    public void removeCloseUpDropDownControlListener(CloseUpDropDownControlListener closeUpDropDownControlListener) {
        this.closeUpDropDownControlListeners.remove(closeUpDropDownControlListener);
    }

    public void fireCloseUpDropDownControl(CloseUpDropDownControlEvent closeUpDropDownControlEvent) {
        for (CloseUpDropDownControlListener closeUpDropDownControlListener : this.closeUpDropDownControlListeners) {
            closeUpDropDownControlListener.closeUpDropDownControl(closeUpDropDownControlEvent);
        }
    }

    //public event SetPluginImageHandler SetPluginImageEvent;
    private ArrayList<SetPluginImageListener> setPluginImageListeners = new ArrayList<>();

    public void addSetPluginImageListener(SetPluginImageListener setPluginImageListener) {
        this.setPluginImageListeners.add(setPluginImageListener);
    }

    public void removeSetPluginImageListener(SetPluginImageListener setPluginImageListener) {
        this.setPluginImageListeners.remove(setPluginImageListener);
    }

    public void fireSetPluginImage(SetPluginImageEvent setPluginImageEvent) {
        for (SetPluginImageListener setPluginImageListener : this.setPluginImageListeners) {
            setPluginImageListener.setPluginImage(setPluginImageEvent);
        }
    }

    private ArrayList<SetPluginCaptionListener> setPluginCaptionListeners = new ArrayList<>();

    public void addSetPluginCaptionListener(SetPluginCaptionListener setPluginCaptionListener) {
        this.setPluginCaptionListeners.add(setPluginCaptionListener);
    }

    public void removeSetPluginCaptionListener(SetPluginCaptionListener setPluginCaptionListener) {
        this.setPluginCaptionListeners.remove(setPluginCaptionListener);
    }

    public void fireSetPluginCaption(SetPluginCaptionEvent setPluginCaptionEvent) {
        for (SetPluginCaptionListener setPluginCaptionListener : this.setPluginCaptionListeners) {
            setPluginCaptionListener.setPluginCaption(setPluginCaptionEvent);
        }
    }

    //endregion

    //region 公共方法

    public void loadPackage(String packagePath) {
        if (packagePath != null && !packagePath.isEmpty()) {
            loadPackage(new String[]{packagePath});
        }
    }

    public void loadPackage(String[] packagePaths) {
        this.loadPackage(packagePaths, true);
    }

    //endregion

    /**
     * 获得插件对象
     *
     * @param pluginCollection 当前插件集合
     * @param aClass           插件类型
     * @return 插件对象
     */
    private IPlugin getPluginObject(PluginCollection pluginCollection, Class<?> aClass) {
        Object o = createInstance(aClass);
        IPlugin plugin = o instanceof IPlugin ? ((IPlugin) o) : null;
        // 判断该插件是否已经存在插件集合中了,如果不是则加入该对象
        if (plugin != null && !pluginCollection.contains(plugin)) {
            pluginCollection.add(plugin);
        }
        return plugin;
    }

    /**
     * 创建 IConnect 实例
     *
     * @param aClass IConnect 类型
     * @return IConnect 实例
     */
    private IConnect getIConnectObject(Class<?> aClass) {
        Object o = createInstance(aClass);
        return o instanceof IConnect ? ((IConnect) o) : null;
    }

    /**
     * 解析插件集合中所有的对象
     *
     * @param pluginCollection 插件集合
     */
    private void getPluginArray(PluginCollection pluginCollection) {
        for (IPlugin plugin : pluginCollection) {
            if (this.pluginAddedListeners != null) {
                this.firePluginAdded(new PluginAddedEvent(this, plugin));
            }
        }
    }

    /**
     * 加载包
     *
     * @param packagePaths 包
     * @param check        是否验证授权
     */
    private void loadPackage(String[] packagePaths, boolean check) {
        if (packagePaths == null || packagePaths.length <= 0) {
            return;
        }

        ArrayList<String> pps = new ArrayList<>();
        for (String pp : packagePaths) {
            if (pp != null && !pp.isEmpty() && !this.assemblyArray.contains(pp)) {
                pps.add(pp);
            }
        }

        // 根据包名加载包
        PluginCollection pluginCollection = new PluginCollection();
        Class<ICommand> commandClass = ICommand.class;
        Class<ICheckCommand> checkCommandClass = ICheckCommand.class;
        Class<ISubItem> subItemClass = ISubItem.class;
        Class<IEditCombobox> editComboboxClass = IEditCombobox.class;
        Class<IMenuBar> menuBarClass = IMenuBar.class;
        Class<IToolBar> toolBarClass = IToolBar.class;
        Class<IDockWindow> dockWindowClass = IDockWindow.class;
        Class<IContentsView> contentsViewClass = IContentsView.class;
        Class<IRibbonPageCategory> ribbonPageCategoryClass = IRibbonPageCategory.class;
        Class<IRibbonPage> ribbonPageClass = IRibbonPage.class;
        Class<IRibbonPageGroup> ribbonPageGroupClass = IRibbonPageGroup.class;
        Class<IGallery> galleryClass = IGallery.class;
        Class<ICheckBox> checkBoxClass = ICheckBox.class;
        Class<IStatic> staticClass = IStatic.class;
        Class<IProgress> progressClass = IProgress.class;
        Class<IStatusBar> statusBarClass = IStatusBar.class;
        Class<IWelcomeScreen> welcomeScreenClass = IWelcomeScreen.class;
        Class<IConnect> connectClass = IConnect.class;
        Class<IDropDown> dropDownClass = IDropDown.class;
        Class<IFunction> functionClass = IFunction.class;

        for (String packagePath : pps) {
            // 认证授权
            if (check) {

            }

            Set<Class<?>> classes = PackageUtility.getClasses(packagePath);
            if (classes.size() == 0) {
                continue;
            }

            if (this.beginLoadPackageListeners != null) {
                if (this.fireBeginLoadPackage(new BeginLoadEvent(this, packagePath)) > 0) {
                    continue;
                }
            }

            ArrayList<IConnect> connects = new ArrayList<>();
            // 不抛送异常，一个插件处理失败时可以不影响其他插件
            try {
                this.assemblyArray.add(packagePath);
                // 获得包中定义的类型
                for (Class<?> aClass : classes) {
                    if (!aClass.getName().contains("plugin")) {
                        continue;
                    }
                    if (Modifier.isAbstract(aClass.getModifiers())) {
                        continue;
                    }
                    if (commandClass.isAssignableFrom(aClass)) {
                        IPlugin plugin = getPluginObject(pluginCollection, aClass);
                        if (plugin != null && !this.commands.containsKey(plugin.getClass().getName())) {
                            this.commands.put(plugin.getClass().getName(), (ICommand) plugin);
                        }
                    } else if (checkCommandClass.isAssignableFrom(aClass)) {
                        IPlugin plugin = getPluginObject(pluginCollection, aClass);
                        if (plugin != null && !this.checkCommands.containsKey(plugin.getClass().getName())) {
                            this.checkCommands.put(plugin.getClass().getName(), (ICheckCommand) plugin);
                        }
                    } else if (subItemClass.isAssignableFrom(aClass)) {
                        Object o = createInstance(aClass);
                        ISubItem subItem = o instanceof ISubItem ? ((ISubItem) o) : null;
                        if (subItem != null && !this.subItems.containsKey(subItem.getClass().getName())) {
                            this.subItems.put(subItem.getClass().getName(), subItem);
                        }
                    } else if (editComboboxClass.isAssignableFrom(aClass)) {
                        IPlugin plugin = getPluginObject(pluginCollection, aClass);
                        if (plugin != null && !this.editComboboxes.containsKey(plugin.getClass().getName())) {
                            this.editComboboxes.put(plugin.getClass().getName(), (IEditCombobox) plugin);
                        }
                    } else if (menuBarClass.isAssignableFrom(aClass)) {
                        IPlugin plugin = getPluginObject(pluginCollection, aClass);
                        if (plugin != null && !this.menuBars.containsKey(plugin.getClass().getName())) {
                            this.menuBars.put(plugin.getClass().getName(), (IMenuBar) plugin);
                        }
                    } else if (toolBarClass.isAssignableFrom(aClass)) {
                        IPlugin plugin = getPluginObject(pluginCollection, aClass);
                        if (plugin != null && !this.toolBars.containsKey(plugin.getClass().getName())) {
                            this.toolBars.put(plugin.getClass().getName(), (IToolBar) plugin);
                        }
                    } else if (dockWindowClass.isAssignableFrom(aClass)) {
                        IPlugin plugin = getPluginObject(pluginCollection, aClass);
                        if (plugin instanceof IDockWindow) {
                            IDockWindow dockWindow = (IDockWindow) plugin;
                            if (!this.dockWindowsTypes.containsKey(dockWindow.getClass().getName())) {
                                this.dockWindowsTypes.put(dockWindow.getClass().getName(), dockWindow.getClass());
                            }
                            if (dockWindow.isInitCreate() && !this.dockWindows.containsKey(dockWindow.getClass().getName())) {
                                this.dockWindows.put(dockWindow.getClass().getName(), dockWindow);
                            } else {
                                pluginCollection.remove(dockWindow);
                            }
                        }
                    } else if (contentsViewClass.isAssignableFrom(aClass)) {
                        IPlugin plugin = getPluginObject(pluginCollection, aClass);
                        if (plugin instanceof IContentsView) {
                            IContentsView contentsView = (IContentsView) plugin;
                            if (!this.contentsViewsTypes.containsKey(contentsView.getClass().getName())) {
                                this.contentsViewsTypes.put(contentsView.getClass().getName(), contentsView.getClass());
                            }
                            if (contentsView.isInitCreate() && !this.contentsViews.containsKey(contentsView.getClass().getName())) {
                                this.contentsViews.put(contentsView.getClass().getName(), contentsView);
                            } else {
                                pluginCollection.remove(contentsView);
                            }
                        }
                    } else if (ribbonPageCategoryClass.isAssignableFrom(aClass)) {
                        IPlugin plugin = getPluginObject(pluginCollection, aClass);
                        if (plugin != null && !this.ribbonPageCategories.containsKey(plugin.getClass().getName())) {
                            this.ribbonPageCategories.put(plugin.getClass().getName(), (IRibbonPageCategory) plugin);
                        }
                    } else if (ribbonPageClass.isAssignableFrom(aClass)) {
                        IPlugin plugin = getPluginObject(pluginCollection, aClass);
                        if (plugin != null && !this.ribbonPages.containsKey(plugin.getClass().getName())) {
                            this.ribbonPages.put(plugin.getClass().getName(), (IRibbonPage) plugin);
                        }
                    } else if (ribbonPageGroupClass.isAssignableFrom(aClass)) {
                        IPlugin plugin = getPluginObject(pluginCollection, aClass);
                        if (plugin != null && !this.ribbonPageGroups.containsKey(plugin.getClass().getName())) {
                            this.ribbonPageGroups.put(plugin.getClass().getName(), (IRibbonPageGroup) plugin);
                        }
                    } else if (galleryClass.isAssignableFrom(aClass)) {
                        IPlugin plugin = getPluginObject(pluginCollection, aClass);
                        if (plugin != null && !this.galleries.containsKey(plugin.getClass().getName())) {
                            this.galleries.put(plugin.getClass().getName(), (IGallery) plugin);
                        }
                    } else if (checkBoxClass.isAssignableFrom(aClass)) {
                        IPlugin plugin = getPluginObject(pluginCollection, aClass);
                        if (plugin != null && !this.checkBoxes.containsKey(plugin.getClass().getName())) {
                            this.checkBoxes.put(plugin.getClass().getName(), (ICheckBox) plugin);
                        }
                    } else if (staticClass.isAssignableFrom(aClass)) {
                        IPlugin plugin = getPluginObject(pluginCollection, aClass);
                        if (plugin != null && !this.statics.containsKey(plugin.getClass().getName())) {
                            this.statics.put(plugin.getClass().getName(), (IStatic) plugin);
                        }
                    } else if (progressClass.isAssignableFrom(aClass)) {
                        IPlugin plugin = getPluginObject(pluginCollection, aClass);
                        if (plugin != null && !this.progresses.containsKey(plugin.getClass().getName())) {
                            this.progresses.put(plugin.getClass().getName(), (IProgress) plugin);
                        }
                    } else if (statusBarClass.isAssignableFrom(aClass)) {
                        IPlugin plugin = getPluginObject(pluginCollection, aClass);
                        if (plugin != null && !this.statusBars.containsKey(plugin.getClass().getName())) {
                            this.statusBars.put(plugin.getClass().getName(), (IStatusBar) plugin);
                        }
                    } else if (welcomeScreenClass.isAssignableFrom(aClass)) {
                        IPlugin plugin = getPluginObject(pluginCollection, aClass);
                        if (plugin != null && !this.welcomeScreens.containsKey(plugin.getClass().getName())) {
                            this.welcomeScreens.put(plugin.getClass().getName(), (IWelcomeScreen) plugin);
                        }
                    } else if (connectClass.isAssignableFrom(aClass)) {
                        Object o = createInstance(aClass);
                        IConnect connect = o instanceof IConnect ? ((IConnect) o) : null;
                        if (connect != null && !this.connects.containsKey(connect.getClass().getName())) {
                            this.connects.put(connect.getClass().getName(), connect);
                            connects.add(connect);
                        }
                    } else if (dropDownClass.isAssignableFrom(aClass)) {
                        IPlugin plugin = getPluginObject(pluginCollection, aClass);
                        if (plugin != null && !this.dropDowns.containsKey(plugin.getClass().getName())) {
                            this.dropDowns.put(plugin.getClass().getName(), (IDropDown) plugin);
                        }
                    } else if (functionClass.isAssignableFrom(aClass)) {
                        Object o = createInstance(aClass);
                        IFunction function = o instanceof IFunction ? ((IFunction) o) : null;
                        if (function != null && !this.functions.containsKey(function.getClass().getName())) {
                            this.functions.put(function.getClass().getName(), function);
                        }
                    }
                }
            } catch (Exception e) {
                // TODO: 输出日志 log4j
                e.printStackTrace();
            }
            if (this.loadPackageListeners != null) {
                this.fireloadPackage(new LoadPackageEvent(this, packagePath, connects.toArray(new IConnect[0])));
            }
            this.getPluginArray(pluginCollection);
        }
    }
}
