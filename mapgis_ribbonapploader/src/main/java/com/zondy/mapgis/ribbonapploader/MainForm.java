package com.zondy.mapgis.ribbonapploader;

import com.zondy.mapgis.base.Window;
import com.zondy.mapgis.base.XFunctions;
import com.zondy.mapgis.base.XPath;
import com.zondy.mapgis.base.XString;
import com.zondy.mapgis.controls.common.ZDComboBox;
import com.zondy.mapgis.dockfx.dock.*;
import com.zondy.mapgis.dockfx.pane.ContentPane;
import com.zondy.mapgis.dockfx.pane.ContentSplitPane;
import com.zondy.mapgis.dockfx.pane.ContentTabPane;
import com.zondy.mapgis.pluginengine.Application;
import com.zondy.mapgis.pluginengine.enums.CheckStateEnum;
import com.zondy.mapgis.pluginengine.events.*;
import com.zondy.mapgis.pluginengine.plugin.*;
import com.zondy.mapgis.pluginengine.plugincollection.PluginContainer;
import com.zondy.mapgis.pluginengine.ui.*;
import com.zondy.mapgis.ribbonfx.control.*;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

/**
 * @author CR
 * @file MainForm.java
 * @brief MapGIS的Ribbon程序主框架
 * @date 2020-6-12
 */
public class MainForm extends javafx.application.Application {
    //region 变量
    private Application application;
    private PluginContainer pluginContainer;
    private static String[] mainArgs;//通过main传来的命令行参数
    private boolean isLoadingPlugin = true;//初始化插件标志，当根据jar加载插件时设为true；当根据XML加载插件时设为false

    private Stage primaryStage;//主窗口Stage
    private Scene mainScene;   //界面主场景
    private Ribbon ribbon;     //Ribbon功能区
    private DockPane dockPane; //停靠容器
    private BorderPane borderPane;//根级容器
    private QuickAccessBar quickAccessBar;//快速启动栏
    private final List<String> ribbonCategoryColors = Arrays.asList("red", "green", "yellow", "purple", "orange", "blue");//页面类别的颜色，目前ribbon.css里面只写了这几种

    private Document appXmlDocument; //application.xml
    private Document realXmlDocument;//config文件中的application/appXmlDocument

    private List<String> jarPackages;//config中的插件jar包
    private List<Element> dockWindowElementList = null;//记录exe中application.xml中DockWindow的大致位置信息，以便卸载某个插件后再次加载位置更友好
    private List<Object> welcomeScreens = new ArrayList<>();//显示的欢迎屏
    private List<String> disableDickWindows = new ArrayList<>();//禁用的停靠窗口
    private List<Element> destroyDockWindows = new ArrayList<>();//记录销毁的DockWindow的位置信息节点
    private Map<Image, String> imageToResName = new HashMap<>();//图像转换为资源名称Map
    private Map<String, KeyCodeCombination> pluginShortcuts = new HashMap<>();//key为插件名称，value为快捷键
    private Map<String, Map<String, List<RibbonPage>>> viewRibbonCategories = new HashMap<>();//key为ContentsView，value为IRibbonPageCategory及其RibbonPage列表
    //endregion

    //region javafx.application
    public static void main(String[] args) {
        mainArgs = args;

        int index = 0;
        for (String arg : mainArgs) {
            System.out.println(String.format("arg-%d: %s", index++, arg));
        }
        launch(args);
    }

    @Override
    public void init() {
        try {
            File xmlFile = null;
            for (String arg : mainArgs) {
                if (!XString.isNullOrEmpty(arg) && XPath.getExtension(arg).equals(".xml")) {
                    File file = new File(arg);
                    if (file.exists()) {
                        xmlFile = file;
                        break;
                    }
                }
            }

            SAXReader saxReader = new SAXReader();
            if (xmlFile != null) {
                appXmlDocument = saxReader.read(xmlFile);
            } else {
                appXmlDocument = saxReader.read(getClass().getResourceAsStream("/application.xml"));
            }
            realXmlDocument = appXmlDocument;
            System.out.println("加载application.xml成功。");
        } catch (Exception e) {
            System.out.println("加载application.xml出错。");
            appXmlDocument = null;
        }

        if (appXmlDocument != null) {
            Element welcomescreensElement = (Element) appXmlDocument.selectSingleNode("/application/welcomescreens");
            if (welcomescreensElement != null) {
                boolean forbidOthers = Boolean.parseBoolean(welcomescreensElement.attributeValue("forbidothers"));
                if (forbidOthers || welcomescreensElement.isTextOnly()) {
                    this.showImageScreen(this.getImageFromResName(welcomescreensElement.attributeValue("globalimage"), false));
                } else if (!forbidOthers) {
                    List<Element> wsEles = welcomescreensElement.elements();
                    for (Element wsElement : wsEles) {
                        if ("imagewelcomescreen".equals(wsElement.getName())) {
                            this.showImageScreen(this.getImageFromResName(wsElement.attributeValue("image"), false));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        Window.primaryStage = primaryStage;

        // region 初始化界面
        ribbon = new Ribbon();
        quickAccessBar = new QuickAccessBar();
        ribbon.setQuickAccessBar(quickAccessBar);
        dockPane = new DockPane();
        borderPane = new BorderPane();
        borderPane.setTop(ribbon);
        borderPane.setCenter(dockPane);

        double width = 800.0;
        double height = 600.0;
        if (XFunctions.isSystemLinux()) {
            //此size为取消最大化时的size。由于Linux启动前异常贴图，故在linux下面直接用屏幕size。
            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            width = primScreenBounds.getWidth();
            height = primScreenBounds.getHeight();
        }
        mainScene = new Scene(borderPane, width, height);
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("RibbonAppLoader");
        this.primaryStage.setScene(mainScene);
        this.primaryStage.setMaximized(true);
        this.primaryStage.getIcons().add(new Image("/png_ribbonapploader_16.png"));
        this.primaryStage.setOnCloseRequest(event ->
        {
            ApplicationClosingEvent args = new ApplicationClosingEvent(application, false);
            application.fireApplicationClosing(args);
            if (args.isCancel()) {
                event.consume();
            } else /*if (appXmlDocument != null)*/ {
                saveConfigFile();
                application.fireApplicationClosed(new ApplicationClosedEvent(application));
            }
        });

        //endregion

        //region pluginContainer及其事件
        pluginContainer = new PluginContainer();
        pluginContainer.addPluginAddedListener(pluginAddedEvent -> {
            if (pluginAddedEvent != null && isLoadingPlugin) {
                IPlugin plugin = pluginAddedEvent.getPlugin();
                if (plugin instanceof IRibbonPage) {
                    IRibbonPage iRibbonPage = (IRibbonPage) plugin;
                    if (XString.isNullOrEmpty(iRibbonPage.getCategoryKey())) {
                        createRibbonPage(iRibbonPage);//为空的创建，否则在创建页面类别时才添加
                    }
                } else if (plugin instanceof IDockWindow) {
                    createDockWindowUI((IDockWindow) plugin);
                } else if (plugin instanceof IContentsView) {
                    IContentsView iContentsView = (IContentsView) plugin;
                    Object obj = pluginAddedEvent.getParameter2();
                    String text = obj instanceof String ? (String) obj : iContentsView.getCaption();
                    DockView dockView = createContentsView(iContentsView, (String) pluginAddedEvent.getParameter1(), text);
                    if (dockView != null) {
                        pluginContainer.firePluginLoaded(new PluginLoadedEvent(pluginContainer, iContentsView));
                    }
                }
            }
        });

        pluginContainer.addPluginRemovedListener(pluginRemovedEvent -> {
            if (pluginRemovedEvent != null) {
                IPlugin iPlugin = pluginRemovedEvent.getPlugin();
                if (iPlugin instanceof ICommand || iPlugin instanceof IDropDown || iPlugin instanceof IEditCombobox || iPlugin instanceof ICheckBox) {
                    String pluginName = iPlugin.getClass().getName();

                    //region quickAccessBar
                    boolean hasNode = false;
                    for (Button button : quickAccessBar.getButtons()) {
                        if (button.getId().replace('_', '.').equals(pluginName)) {
                            quickAccessBar.getButtons().remove(button);
                            hasNode = true;
                        }
                    }
                    for (Button button : quickAccessBar.getRightButtons()) {
                        if (button.getId().replace('_', '.').equals(pluginName)) {
                            quickAccessBar.getRightButtons().remove(button);
                            hasNode = true;
                        }
                    }
                    //endregion

                    //region RibbonTab
                    for (RibbonPage ribbonPage : ribbon.getTabs()) {
                        for (RibbonGroup ribbonGroup : ribbonPage.getRibbonGroups()) {
                            for (Node node : ribbonGroup.getNodesWithoutSeprator()) {
                                if (node.getId().replace('_', '.').equals(pluginName)) {
                                    ribbonGroup.getNodes().remove(node);
                                    hasNode = true;
                                } else if (node instanceof MenuButton) {
                                    MenuButton menuButton = (MenuButton) node;
                                    for (MenuItem menuItem : menuButton.getItems()) {
                                        if (menuItem.getId().replace('_', '.').equals(pluginName)) {
                                            menuButton.getItems().remove(menuItem);
                                            hasNode = true;
                                        }
                                    }
                                    if (!(menuButton instanceof SplitMenuButton) && menuButton.getItems().size() == 0) {
                                        ribbonGroup.getNodes().remove(menuButton);
                                    }
                                } else if (node instanceof ToolBar) {
                                    for (Node item : ((ToolBar) node).getItems()) {
                                        if (item.getId().replace('_', '.').equals(pluginName)) {
                                            ((ToolBar) node).getItems().remove(item);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //endregion

                    if (hasNode) {
                        pluginContainer.firePluginUnLoaded(new PluginUnLoadedEvent(pluginContainer, iPlugin));
                    }
                } else if (iPlugin instanceof IRibbonPageCategory) {
                    IRibbonPageCategory iRibbonPageCategory = (IRibbonPageCategory) iPlugin;
                    IRibbonPage[] iRibbonPages = iRibbonPageCategory.getRibbonPages();
                    if (iRibbonPages != null) {
                        for (IRibbonPage iRibbonPage : iRibbonPages) {
                            for (RibbonPage ribbonPage : ribbon.getTabs()) {
                                if (ribbonPage.getId().replace('_', '.').equals(iRibbonPage.getClass().getName())) {
                                    ribbon.getTabs().remove(ribbonPage);
                                    pluginContainer.firePluginUnLoaded(new PluginUnLoadedEvent(pluginContainer, iRibbonPage));
                                    break;
                                }
                            }
                        }
                        String cvKey = iRibbonPageCategory.getAttachContentsViewKey();
                        Map<String, List<RibbonPage>> rcs = viewRibbonCategories.get(cvKey);
                        if (rcs != null && rcs.remove(iRibbonPageCategory.getClass().getName()) != null) {
                            if (rcs.size() > 0) {
                                viewRibbonCategories.put(cvKey, rcs);
                            } else {
                                viewRibbonCategories.remove(cvKey);
                            }
                        }
                        pluginContainer.firePluginUnLoaded(new PluginUnLoadedEvent(pluginContainer, iRibbonPageCategory));
                    }
                } else if (iPlugin instanceof IRibbonPage) {
                    IRibbonPage iRibbonPage = (IRibbonPage) iPlugin;
                    for (RibbonPage ribbonPage : ribbon.getTabs()) {
                        if (ribbonPage.getId().replace('_', '.').equals(iRibbonPage.getClass().getName())) {
                            ribbon.getTabs().remove(ribbonPage);
                            pluginContainer.firePluginUnLoaded(new PluginUnLoadedEvent(pluginContainer, iRibbonPage));
                            break;
                        }
                    }
                } else if (iPlugin instanceof IRibbonPageGroup) {
                    IRibbonPageGroup iRibbonPageGroup = (IRibbonPageGroup) iPlugin;
                    for (RibbonPage ribbonPage : ribbon.getTabs()) {
                        boolean hasRemoved = false;
                        for (RibbonGroup ribbonGroup : ribbonPage.getRibbonGroups()) {
                            if (ribbonGroup.getId().replace('_', '.').equals(iRibbonPageGroup.getClass().getName())) {
                                ribbonPage.getRibbonGroups().remove(ribbonGroup);
                                pluginContainer.firePluginUnLoaded(new PluginUnLoadedEvent(pluginContainer, iRibbonPageGroup));
                                hasRemoved = true;
                                break;
                            }
                        }
                        if (hasRemoved) {
                            break;
                        }
                    }
                } else if (iPlugin instanceof IDockWindow) {
                    DockWindow dockWindow = getDockWindow((IDockWindow) iPlugin);
                    if (dockWindow != null) {
                        dockWindow.close();
                    }
                } else if (iPlugin instanceof IContentsView) {
                    DockView dockView = getDockView((IContentsView) iPlugin);
                    if (dockView != null) {
                        dockView.close();
                    }
                }
            }
        });

        pluginContainer.addPluginVisibleChangedListener(pluginVisibleChangedEvent -> {
            if (pluginVisibleChangedEvent != null) {
                IPlugin iPlugin = pluginVisibleChangedEvent.getPlugin();
                boolean isVisible = pluginVisibleChangedEvent.isVisible();
                String pluginName = iPlugin.getClass().getName();
                if (iPlugin instanceof IRibbonPageCategory) {
                    IRibbonPageCategory iRibbonPageCategory = (IRibbonPageCategory) iPlugin;
                    Map<String, List<RibbonPage>> categoryPages = viewRibbonCategories.get(iRibbonPageCategory.getAttachContentsViewKey());
                    if (categoryPages != null && categoryPages.containsKey(pluginName)) {
                        List<RibbonPage> pageList = categoryPages.get(pluginName);
                        if (pageList != null) {
                            for (int i = pageList.size() - 1; i >= 0; i--) {
                                ribbon.getTabs().remove(pageList.get(i));
                            }
                        }
                    }
                } else if (iPlugin instanceof IRibbonPage) {
                    for (RibbonPage ribbonPage : ribbon.getTabs()) {
                        if (ribbonPage.getId().replace('_', '.').equals(pluginName)) {
                            ribbon.getTabs().remove(ribbonPage);
                            break;
                        }
                    }
                } else if (iPlugin instanceof IRibbonPageGroup) {
                    for (RibbonPage ribbonPage : ribbon.getTabs()) {
                        boolean setted = false;
                        for (RibbonGroup ribbonGroup : ribbonPage.getRibbonGroups()) {
                            if (ribbonGroup.getId().replace('_', '.').equals(pluginName)) {
                                ribbonGroup.setVisible(isVisible);
                                setted = true;
                                break;
                            }
                        }
                        if (setted) {
                            break;
                        }
                    }
                } else if (iPlugin instanceof IDockWindow) {
                    DockWindow dockWindow = getDockWindow((IDockWindow) iPlugin);
                    if (dockWindow != null) {
                        dockWindow.setVisible(isVisible);
                    }
                } else if (iPlugin instanceof IContentsView) {
                    DockView dockView = getDockView((IContentsView) iPlugin);
                    if (dockView != null) {
                        dockView.setVisible(isVisible);
                    }
                }
            }
        });

        pluginContainer.addPluginEnableChangedListener(pluginEnableChangedEvent -> {
            if (pluginEnableChangedEvent != null) {
                setPluginEnableChanged(pluginEnableChangedEvent.getPlugin(), pluginEnableChangedEvent.isEnable());
            }
        });

        pluginContainer.addPluginActiveChangedListener(pluginActiveChangedEvent -> {
            if (pluginActiveChangedEvent != null) {
                IContentsView oldContentsView = application.getActiveContentsView();
                IContentsView contentsView = pluginActiveChangedEvent.getContentsView();
                application.setActiveContentsView(contentsView);
                if (contentsView != null) {
                    for (DockWindow dockWindow : dockPane.getAllNodes()) {
                        if (dockWindow instanceof DockView) {
                            String dockViewID = dockWindow.getId().replace('_', '.');
                            IContentsView icv = pluginContainer.getContentsViews().get(dockViewID);
                            if (icv != null) {
                                if (icv.equals(contentsView)) {
                                    icv.onActive(true);
                                    dockWindow.focus();
                                    pluginContainer.fireContentsViewChanged(new ContentsViewChangedEvent(pluginContainer, contentsView, oldContentsView));
                                } else {
                                    icv.onActive(false);
                                }
                            }
                        }
                    }
                } else {
                    application.setActiveContentsView(null);
                    pluginContainer.fireContentsViewChanged(new ContentsViewChangedEvent(pluginContainer, null, oldContentsView));
                }
                this.application.getStateManager().onStateChanged(this);
            }
        });

        pluginContainer.addSetContentsViewTextListener(setContentsViewTextEvent -> {
            if (setContentsViewTextEvent != null) {
                DockView dockView = getDockView(setContentsViewTextEvent.getContentsView());
                if (dockView != null) {
                    dockView.setTitle(setContentsViewTextEvent.getContentsViewText());
                }
            }
        });

        pluginContainer.addGetContentsViewTextListener(getContentsViewTextEvent -> {
            String text = null;
            if (getContentsViewTextEvent != null) {
                DockView dockView = getDockView(getContentsViewTextEvent.getContentsView());
                if (dockView != null) {
                    text = dockView.getTitle();
                }
            }
            return text;
        });

        pluginContainer.addSetCommandCheckedListener(setCommandCheckedEvent -> {
            if (setCommandCheckedEvent != null) {
                ICheckCommand iCheckCommand = setCommandCheckedEvent.getCheckCommand();
                if (iCheckCommand != null) {
                    String pluginName = iCheckCommand.getClass().getName();
                    boolean isChecked = setCommandCheckedEvent.isCheckState();
                    for (RibbonPage ribbonPage : ribbon.getTabs()) {
                        for (RibbonGroup ribbonGroup : ribbonPage.getRibbonGroups()) {
                            for (Node node : ribbonGroup.getNodesWithoutSeprator()) {
                                if (node instanceof ToggleButton) {
                                    String strID = node.getId().replace('_', '.');
                                    if (strID.equals(pluginName) || strID.startsWith(pluginName + "$")) {
                                        ((ToggleButton) node).setSelected(isChecked);
                                    }
                                } else if (node instanceof MenuButton) {
                                    MenuButton menuButton = (MenuButton) node;
                                    for (MenuItem menuItem : menuButton.getItems()) {
                                        if (menuItem instanceof CheckMenuItem && menuItem.getId().replace('_', '.').equals(pluginName)) {
                                            ((CheckMenuItem) menuItem).setSelected(isChecked);
                                        }
                                    }
                                } else if (node instanceof ToolBar) {
                                    for (Node item : ((ToolBar) node).getItems()) {
                                        if (item instanceof ToggleButton && item.getId().replace('_', '.').equals(pluginName)) {
                                            ((ToggleButton) item).setSelected(isChecked);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });

        pluginContainer.addCloseContentsViewListener(closeContentsViewEvent -> {
            if (closeContentsViewEvent != null) {
                DockView dockView = getDockView(closeContentsViewEvent.getContentsView());
                if (dockView != null) {
                    dockView.close();
                }
            }
        });

        pluginContainer.addLoadPackageListener(loadPackageEvent -> {
            if (loadPackageEvent != null) {
                IConnect[] iConnects = loadPackageEvent.getConnects();
                if (iConnects != null) {
                    for (IConnect connect : iConnects) {
                        connect.onConnection(application);
                    }
                }
            }
        });

        pluginContainer.addCreateDockWindowListener(createDockWindowEvent -> {
            if (createDockWindowEvent != null) {
                IDockWindow iDockWindow = createDockWindowEvent.getDockWindow();
                if (iDockWindow != null) {
                    String dwName = iDockWindow.getClass().getName();
                    if (disableDickWindows.contains(dwName)) {
                        iDockWindow.onCreate(application);
                    } else {
                        DockWindow dockWindow = initDockWindow(destroyDockWindows, dwName);
                        if (dockWindow == null) {
                            dockWindow = createDockWindowUI(iDockWindow);
                            if (!iDockWindow.isInitCreate() && realXmlDocument != null) {
                                //无XML节点，则创建保存dockWindow界面信息的XML节点，并保存界面信息
                                Element dockwindowsEle = (Element) realXmlDocument.selectSingleNode("/application/dockwindows");
                                if (dockwindowsEle != null) {
                                    Element element = dockwindowsEle.addElement("destroydockwindow");
                                    element.addAttribute("name", dockWindow.getId().replace('.', '_'));
                                    element.addAttribute("text", dockWindow.getTitle());
                                    element.addAttribute("prefwidth", String.valueOf(dockWindow.getPrefWidth()));
                                    element.addAttribute("prefheight", String.valueOf(dockWindow.getPrefHeight()));
                                    element.addAttribute("visibility", String.valueOf(dockWindow.isVisible()));
                                    element.addAttribute("dockpos", dockWindow.getLastDockPos().toString());
                                    destroyDockWindows.add(element);
                                }
                            }
                        }
                    }
                }
            }
        });

        pluginContainer.addDestroyDockWindowListener(destroyDockWindowEvent -> {
            if (destroyDockWindowEvent != null) {
                IDockWindow iDockWindow = destroyDockWindowEvent.getDockWindow();
                DockWindow dockWindow = getDockWindow(iDockWindow);
                if (dockWindow != null) {
                    //saveDestroyDockPanel(iDockWindow, dockWindow);//未完成
                    dockWindow.close();
                }
            }
        });

        pluginContainer.addSetActiveDockWindowListener(setActiveDockWindowEvent -> {
            if (setActiveDockWindowEvent != null) {
                DockWindow dockWindow = getDockWindow(setActiveDockWindowEvent.getDockWindow());
                if (dockWindow != null) {
                    if (!dockWindow.isVisible()) {
                        dockWindow.setVisible(true);
                    }
                    dockWindow.focus();
                }
            }
        });

        pluginContainer.addSetEditComboboxTextListener(setEditComboboxTextEvent -> {
            if (setEditComboboxTextEvent != null) {
                setEditComboboxTextValue(setEditComboboxTextEvent.getEditCombobox(), setEditComboboxTextEvent.getText());
            }
        });

        pluginContainer.addSetEditComboboxValueListener(setEditComboboxValueEvent -> {
            if (setEditComboboxValueEvent != null) {
                setEditComboboxTextValue(setEditComboboxValueEvent.getEditCombobox(), setEditComboboxValueEvent.getValue());
            }
        });

        pluginContainer.addClearComboBoxItemListener(clearComboBoxItemEvent -> {
            if (clearComboBoxItemEvent != null) {
                IEditCombobox iEditCombobox = clearComboBoxItemEvent.getEditCombobox();
                if (iEditCombobox != null) {
                    String pluginName = iEditCombobox.getClass().getName();
                    for (RibbonPage ribbonPage : ribbon.getTabs()) {
                        for (RibbonGroup ribbonGroup : ribbonPage.getRibbonGroups()) {
                            for (Node ribbonItem : ribbonGroup.getNodesWithoutSeprator()) {
                                if (ribbonItem instanceof RibbonItem && ribbonItem.getId().replace('_', '.').equals(pluginName)) {
                                    Node itemNode = ((RibbonItem) ribbonItem).getItem();
                                    if (itemNode instanceof ZDComboBox) {
                                        ((ZDComboBox) itemNode).getItems().clear();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });

        pluginContainer.addPluginIsVisibleListener(pluginIsVisibleEvent -> {
            boolean isVisible = false;
            if (pluginIsVisibleEvent != null) {
                IPlugin iPlugin = pluginIsVisibleEvent.getPlugin();
                if (iPlugin != null) {
                    String pluginName = iPlugin.getClass().getName();
                    if (iPlugin instanceof IRibbonPageCategory) {
                        IRibbonPageCategory iRibbonPageCategory = (IRibbonPageCategory) iPlugin;
                        Map<String, List<RibbonPage>> categoryPages = viewRibbonCategories.get(iRibbonPageCategory.getAttachContentsViewKey());
                        if (categoryPages != null) {
                            List<RibbonPage> ribbonPages = categoryPages.get(pluginName);
                            if (ribbonPages != null && ribbonPages.size() > 0) {
                                isVisible = ribbon.getTabs().contains(ribbonPages.get(0));
                            }
                        }
                    } else if (iPlugin instanceof IRibbonPage) {
                        for (RibbonPage ribbonPage : ribbon.getTabs()) {
                            if (ribbonPage.getId().replace('_', '.').equals(pluginName)) {
                                isVisible = true;
                                break;
                            }
                        }
                    } else if (iPlugin instanceof IRibbonPageGroup) {
                        for (RibbonPage ribbonPage : ribbon.getTabs()) {
                            for (RibbonGroup ribbonGroup : ribbonPage.getRibbonGroups()) {
                                if (ribbonGroup.getId().replace('_', '.').equals(pluginName)) {
                                    isVisible = true;
                                    break;
                                }
                            }
                            if (isVisible) {
                                break;
                            }
                        }
                    } else if (iPlugin instanceof IDockWindow) {
                        DockWindow dockWindow = getDockWindow((IDockWindow) iPlugin);
                        if (dockWindow != null) {
                            isVisible = dockWindow.isVisible();
                        }
                    } else if (iPlugin instanceof IContentsView) {
                        DockView dockView = getDockView((IContentsView) iPlugin);
                        if (dockView != null) {
                            isVisible = dockView.isVisible();
                        }
                    } else {
                        for (RibbonPage ribbonPage : ribbon.getTabs()) {
                            for (RibbonGroup ribbonGroup : ribbonPage.getRibbonGroups()) {
                                for (Node ribbonItem : ribbonGroup.getNodesWithoutSeprator()) {
                                    String strID = ribbonItem.getId().replace('_', '.');
                                    if (strID.equals(pluginName)) {
                                        isVisible = ribbonItem.isVisible();
                                    } else if (ribbonItem instanceof MenuButton) {
                                        MenuButton menuButton = (MenuButton) ribbonItem;
                                        for (MenuItem menuItem : menuButton.getItems()) {
                                            if (strID.equals(pluginName)) {
                                                isVisible = menuItem.isVisible();
                                            }
                                        }
                                    } else if (ribbonItem instanceof ToolBar) {
                                        for (Node item : ((ToolBar) ribbonItem).getItems()) {
                                            if (item.getId().replace('_', '.').equals(pluginName)) {
                                                isVisible = item.isVisible();
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (!isVisible) {
                            for (Button button : quickAccessBar.getAllButtons()) {
                                String strID = button.getId().replace('_', '.');
                                if (strID.equals(pluginName) || strID.startsWith(pluginName + "$")) {
                                    isVisible = button.isVisible();
                                }
                            }
                        }
                    }
                }
            }
            return isVisible;
        });

        pluginContainer.addPluginIsEnableListener(pluginIsEnableEvent -> {
            boolean isEnable = true;
            if (pluginIsEnableEvent != null) {
                IPlugin iPlugin = pluginIsEnableEvent.getPlugin();
                if (iPlugin != null) {
                    String pluginName = iPlugin.getClass().getName();
                    for (RibbonPage ribbonPage : ribbon.getTabs()) {
                        for (RibbonGroup ribbonGroup : ribbonPage.getRibbonGroups()) {
                            for (Node ribbonItem : ribbonGroup.getNodesWithoutSeprator()) {
                                if (ribbonItem.getId().replace('_', '.').equals(pluginName)) {
                                    isEnable = !ribbonItem.isDisable();
                                } else if (ribbonItem instanceof MenuButton) {
                                    MenuButton menuButton = (MenuButton) ribbonItem;
                                    for (MenuItem menuItem : menuButton.getItems()) {
                                        if (menuItem.getId().replace('_', '.').equals(pluginName)) {
                                            isEnable = !menuItem.isDisable();
                                        }
                                    }
                                } else if (ribbonItem instanceof ToolBar) {
                                    for (Node item : ((ToolBar) ribbonItem).getItems()) {
                                        if (item.getId().replace('_', '.').equals(pluginName)) {
                                            isEnable = !item.isDisable();
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (isEnable) {
                        for (Button button : quickAccessBar.getAllButtons()) {
                            String strID = button.getId().replace('_', '.');
                            if (strID.equals(pluginName) || strID.startsWith(pluginName + "$")) {
                                isEnable = button.isDisable();
                            }
                        }
                    }
                }
            }
            return isEnable;
        });

        pluginContainer.addAddComboBoxItemListener(addComboBoxItemEvent -> {
            int addIndex = -1;
            if (addComboBoxItemEvent != null) {
                IEditCombobox iEditCombobox = addComboBoxItemEvent.getEditCombobox();
                ComboBoxItem comboBoxItem = addComboBoxItemEvent.getComboBoxItem();
                if (iEditCombobox != null && comboBoxItem != null) {
                    String pluginName = iEditCombobox.getClass().getName();
                    for (RibbonPage ribbonPage : ribbon.getTabs()) {
                        for (RibbonGroup ribbonGroup : ribbonPage.getRibbonGroups()) {
                            for (Node ribbonItem : ribbonGroup.getNodesWithoutSeprator()) {
                                if (ribbonItem instanceof RibbonItem && ribbonItem.getId().replace('_', '.').equals(pluginName)) {
                                    Node itemNode = ((RibbonItem) ribbonItem).getItem();
                                    if (itemNode instanceof ZDComboBox) {
                                        boolean exist = false;
                                        ZDComboBox<ComboBoxItem> comboBox = (ZDComboBox<ComboBoxItem>) itemNode;
                                        for (ComboBoxItem item : comboBox.getItems()) {
                                            if (item.getValue().equals(comboBoxItem.getValue())) {
                                                exist = true;
                                                addIndex = comboBox.getItems().indexOf(item);
                                                break;
                                            }
                                        }

                                        if (!exist && comboBox.getItems().add(comboBoxItem)) {
                                            addIndex = comboBox.getItems().size() - 1;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return addIndex;
        });

        pluginContainer.addRemoveComboBoxItemListener(removeComboBoxItemEvent -> {
            if (removeComboBoxItemEvent != null) {
                IEditCombobox iEditCombobox = removeComboBoxItemEvent.getEditCombobox();
                Object removeValue = removeComboBoxItemEvent.getValue();
                if (iEditCombobox != null && removeValue != null) {
                    String pluginName = iEditCombobox.getClass().getName();
                    for (RibbonPage ribbonPage : ribbon.getTabs()) {
                        for (RibbonGroup ribbonGroup : ribbonPage.getRibbonGroups()) {
                            for (Node ribbonItem : ribbonGroup.getNodesWithoutSeprator()) {
                                if (ribbonItem instanceof RibbonItem && ribbonItem.getId().replace('_', '.').equals(pluginName)) {
                                    Node itemNode = ((RibbonItem) ribbonItem).getItem();
                                    if (itemNode instanceof ZDComboBox) {
                                        ZDComboBox<ComboBoxItem> comboBox = (ZDComboBox<ComboBoxItem>) itemNode;
                                        for (ComboBoxItem item : comboBox.getItems()) {
                                            if (removeValue.equals(item.getValue())) {
                                                comboBox.getItems().remove(item);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });

        pluginContainer.addCloseUpDropDownControlListener(closeUpDropDownControlEvent -> {
            if (closeUpDropDownControlEvent != null) {
                IDropDown iDropDown = closeUpDropDownControlEvent.getDropDown();
                if (iDropDown != null) {
                    String pluginName = iDropDown.getClass().getName();
                    boolean hided = false;
                    for (RibbonPage ribbonPage : ribbon.getTabs()) {
                        for (RibbonGroup ribbonGroup : ribbonPage.getRibbonGroups()) {
                            for (Node ribbonItem : ribbonGroup.getNodesWithoutSeprator()) {
                                if (ribbonItem instanceof ColorPicker && ribbonItem.getId().replace('_', '.').equals(pluginName) && ((ColorPicker) ribbonItem).isShowing()) {
                                    ((ColorPicker) ribbonItem).hide();
                                    hided = true;
                                    break;
                                }
                            }
                            if (hided) {
                                break;
                            }
                        }
                        if (hided) {
                            break;
                        }
                    }
                }
            }
        });

        pluginContainer.addSetPluginImageListener(setPluginImageEvent -> {
            if (setPluginImageEvent != null) {
                IPlugin iPlugin = setPluginImageEvent.getPlugin();
                if (iPlugin != null) {
                    String pluginName = iPlugin.getClass().getName();
                    Image image = setPluginImageEvent.getImage();
                    for (RibbonPage ribbonPage : ribbon.getTabs()) {
                        for (RibbonGroup ribbonGroup : ribbonPage.getRibbonGroups()) {
                            for (Node ribbonItem : ribbonGroup.getNodesWithoutSeprator()) {
                                if (ribbonItem.getId().replace('_', '.').equals(pluginName)) {
                                    if (ribbonItem instanceof Labeled) {
                                        ((Labeled) ribbonItem).setGraphic(new ImageView(image));
                                    } else if (ribbonItem instanceof RibbonItem) {
                                        ((RibbonItem) ribbonItem).setGraphic(new ImageView(image));
                                    }
                                }

                                if (ribbonItem instanceof MenuButton) {
                                    for (MenuItem item : ((MenuButton) ribbonItem).getItems()) {
                                        if (item.getId().replace('_', '.').equals(pluginName)) {
                                            item.setGraphic(new ImageView(image));
                                        }
                                    }
                                } else if (ribbonItem instanceof ToolBar) {
                                    for (Node node : ((ToolBar) ribbonItem).getItems()) {
                                        if (node instanceof Labeled && node.getId().replace('_', '.').equals(pluginName)) {
                                            ((Labeled) node).setGraphic(new ImageView(image));
                                        }
                                    }
                                }
                            }
                        }
                    }

                    for (Button button : quickAccessBar.getAllButtons()) {
                        String strID = button.getId().replace('_', '.');
                        if (strID.equals(pluginName)) {
                            button.setGraphic(new ImageView(image));
                        }
                    }
                }
            }
        });

        pluginContainer.addSetPluginCaptionListener(setPluginCaptionEvent -> {
            if (setPluginCaptionEvent != null) {
                IPlugin iPlugin = setPluginCaptionEvent.getPlugin();
                if (iPlugin != null) {
                    String pluginName = iPlugin.getClass().getName();
                    String newCaption = setPluginCaptionEvent.getCaption();
                    if (iPlugin instanceof IDockWindow) {
                        DockWindow dockWindow = getDockWindow((IDockWindow) iPlugin);
                        if (dockWindow != null) {
                            dockWindow.setTitle(newCaption);
                        }
                    } else if (iPlugin instanceof IContentsView) {
                        DockView dockView = getDockView((IContentsView) iPlugin);
                        if (dockView != null) {
                            dockView.setTitle(newCaption);
                        }
                    } else if (iPlugin instanceof IRibbonPage) {
                        for (RibbonPage ribbonPage : ribbon.getTabs()) {
                            if (ribbonPage.getId().replace('_', '.').equals(pluginName)) {
                                ribbonPage.setText(newCaption);
                                break;
                            }
                        }
                    } else if (iPlugin instanceof IRibbonPageGroup) {
                        for (RibbonPage ribbonPage : ribbon.getTabs()) {
                            boolean setted = false;
                            for (RibbonGroup ribbonGroup : ribbonPage.getRibbonGroups()) {
                                if (ribbonGroup.getId().replace('_', '.').equals(pluginName)) {
                                    ribbonGroup.setTitle(newCaption);
                                    setted = true;
                                    break;
                                }
                            }
                            if (setted) {
                                break;
                            }
                        }
                    } else {
                        for (RibbonPage ribbonPage : ribbon.getTabs()) {
                            for (RibbonGroup ribbonGroup : ribbonPage.getRibbonGroups()) {
                                for (Node ribbonItem : ribbonGroup.getNodesWithoutSeprator()) {
                                    if (ribbonItem.getId().replace('_', '.').equals(pluginName)) {
                                        if (ribbonItem instanceof Labeled) {
                                            ((Labeled) ribbonItem).setText(newCaption);
                                        } else if (ribbonItem instanceof RibbonItem) {
                                            ((RibbonItem) ribbonItem).setLabel(newCaption);
                                        }
                                    }

                                    if (ribbonItem instanceof MenuButton) {
                                        for (MenuItem item : ((MenuButton) ribbonItem).getItems()) {
                                            if (item.getId().replace('_', '.').equals(pluginName)) {
                                                item.setText(newCaption);
                                            }
                                        }
                                    } else if (ribbonItem instanceof ToolBar) {
                                        for (Node node : ((ToolBar) ribbonItem).getItems()) {
                                            if (node instanceof Labeled && node.getId().replace('_', '.').equals(pluginName)) {
                                                ((Labeled) node).setText(newCaption);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        for (Button button : quickAccessBar.getAllButtons()) {
                            String strID = button.getId().replace('_', '.');
                            if (strID.equals(pluginName)) {
                                button.setText(newCaption);
                            }
                        }
                    }
                }
            }
        });
        //endregion}

        //region 构造application
        application = Application.getApplication();
        application.setPluginContainer(pluginContainer);
        application.setTitle(primaryStage.getTitle());
        application.setArgs(mainArgs);

        application.addCloseApplicationListener(closeApplicationEvent -> primaryStage.close());

        application.addTitleChangedListener(titleChangedEvent -> primaryStage.setTitle(titleChangedEvent.getTitle()));
        //endregion

        this.getDisablePlugins(appXmlDocument, false);
        isLoadingPlugin = false;
        int loadResult = this.loadApplication();
        isLoadingPlugin = true;
        if (loadResult == -1) {
            this.primaryStage.close();
        } else {
            application.fireApplicationLoaded(new ApplicationLoadedEvent(application));
            for (String arg : mainArgs) {
                if (!XString.isNullOrEmpty(arg) && !(new File(arg)).exists()) {
                    pluginContainer.loadPackage(arg);
                }
            }
        }
        this.primaryStage.show();//由于uos加载时界面出现问题，改在最后，但有些功能需要在show之后处理（如快速启动栏），后面出问题需将其放到show之后来处理。
        DockPane.initDefaultUserAgentStylesheet();//必须在show之后初始化才有用
    }
    //endregion

    //region 加载应用程序

    /**
     * 加载应用程序(从xml）
     *
     * @return 成功返回1
     */
    public int loadApplication() {
        int loadResult = 1;
        if (appXmlDocument == null) {
            loadResult = 0;
            return loadResult;
        }

        try {
            String configFile = this.getConfigFilePath();
            SAXReader reader = new SAXReader();
            Document tempXDocument = reader.read(configFile);
            if (tempXDocument != null) {
                Element applicationEle = (Element) tempXDocument.selectSingleNode("application");
                if (applicationEle != null) {
                    Document configXmlDocument = DocumentHelper.parseText(applicationEle.asXML());
                    if (configXmlDocument != null) {
                        realXmlDocument = configXmlDocument;
                    }
                }
            }
        } catch (Exception e) {

        }

        //region 初始化应用程序标题、图标、窗口大小、皮肤
        Element mainappEle = (Element) appXmlDocument.selectSingleNode("/application/mainapp");
        if (mainappEle != null) {
            //设置标题
            application.setTitle(mainappEle.attributeValue("caption"));

            //设置图标
            Image image = this.getImageFromResName(mainappEle.attributeValue("defaulticon"), true);
            if (image != null) {
                primaryStage.getIcons().add(image);
            }
        }

        //region 设置最大化/显示位置+初始大小
        Element mainappEle1 = (Element) realXmlDocument.selectSingleNode("/application/mainapp");
        if (mainappEle1 != null) {
            String windowState = mainappEle1.attributeValue("windowstate");
            if (windowState != null && windowState.equalsIgnoreCase("Maximized")) {
                primaryStage.setMaximized(true);
            } else {
                String x = mainappEle1.attributeValue("x");
                if (x != null && x.length() > 0) {
                    primaryStage.setX(Double.valueOf(x.trim()));
                }
                String y = mainappEle1.attributeValue("y");
                if (y != null && y.length() > 0) {
                    primaryStage.setY(Double.valueOf(y.trim()));
                }

                String size = mainappEle1.attributeValue("windowsize");
                if (size != null && size.length() > 0) {
                    String[] ss = size.split(",");
                    if (ss != null && ss.length == 2) {
                        primaryStage.setWidth(Double.valueOf(ss[0].trim()));
                    } else {
                        primaryStage.setHeight(Double.valueOf(ss[1].trim()));
                    }
                }
            }
        }
        //endregion

        //endregion

        //region 获取并加载jar列表（路径）
        List<org.dom4j.Node> packageNodes = realXmlDocument.selectNodes("/application/packages/package");
        if (packageNodes != null && packageNodes.size() > 0) {
            jarPackages = new ArrayList<>();
            String errorMsg = "";
            for (org.dom4j.Node node : packageNodes) {
                String packagePath = node.getText();
                jarPackages.add(packagePath);
                pluginContainer.loadPackage(packagePath);
            }

            if (jarPackages.size() > 0) {
                ArrayList<String> addedPackages = pluginContainer.getAssemblyArray();
                List<String> lowerPackages = new ArrayList<String>();
                for (String ap : addedPackages) {
                    lowerPackages.add(ap.toLowerCase());
                }
                for (String pkg : jarPackages) {
                    if (!lowerPackages.contains(pkg.toLowerCase())) {
                        errorMsg = String.format("加载失败：%s", pkg);
                    }
                }
            }
        }
        //endregion

        //region 显示欢迎屏
        Element welcomesEle = (Element) appXmlDocument.selectSingleNode("/application/welcomescreens");
        if (welcomesEle != null && !"true".equalsIgnoreCase(welcomesEle.attributeValue("forbidothers"))) {
            List<Element> welcomeEles = welcomesEle.elements();
            for (Element ele : welcomeEles) {
                String wsName = ele.attributeValue("name");
                IWelcomeScreen iws = pluginContainer.getWelcomeScreens().get(wsName);
                if (iws instanceof IFormWelcomeScreen) {
                    IFormWelcomeScreen ifws = ((IFormWelcomeScreen) iws);
                    ifws.show();
                    welcomeScreens.add(ifws);
                    if (ifws.getResult() != ButtonType.OK && ifws.getResult() != ButtonType.YES)//未完成。DialogResult
                    {
                        loadResult = -1;
                        this.closeScreenForm();
                        return loadResult;
                    }
                } else if (iws instanceof IWelcomeScreen) {
                    showImageScreen(iws.getScreenImage());
                }
            }
        }
        //endregion

        //region 初始化Ribbon界面
        Element ribbonPagesEle = (Element) realXmlDocument.selectSingleNode("/application/ribboncontrol/ribbonpages");
        if (ribbonPagesEle != null) {
            this.createRibbonPages(ribbonPagesEle);
        }

        Element quickEle = (Element) realXmlDocument.selectSingleNode("/application/ribboncontrol/quickaccesstoolbar");
        if (quickEle != null) {
            this.createRibbonQuickAccessToolbar(quickEle);
        }
        //endregion

        //region 初始化内容视图
        Element cvsEle = (Element) realXmlDocument.selectSingleNode("/application/contentsviews");
        if (cvsEle != null) {
            List<Element> cvElements = cvsEle.elements();
            for (Element cvElement : cvElements) {
                String cvName = cvElement.attributeValue("name").replace('_', '.');
                IContentsView icv = pluginContainer.getContentsViews().get(cvName);
                List<String> contenViewNames = this.getContenViewNames();
                if (icv != null && !contenViewNames.contains(cvName)) {
                    Pane pane = this.createContentsView(icv, cvName, cvElement.attributeValue("text"));
                    if (pane != null) {
                        pluginContainer.firePluginLoaded(new PluginLoadedEvent(pluginContainer, icv));
                    }
                }
            }

            //创建没有XML节点但InitCreate为true的IContentsView插件
            List<String> contenViewNames = this.getContenViewNames();
            for (Map.Entry<String, IContentsView> entry : pluginContainer.getContentsViews().entrySet()) {
                IContentsView icv = entry.getValue();
                if (icv.isInitCreate() && !contenViewNames.contains(entry.getKey())) {
                    Pane pane = this.createContentsView(icv, icv.getClass().getName(), icv.getCaption());
                    if (pane != null) {
                        pluginContainer.firePluginLoaded(new PluginLoadedEvent(pluginContainer, icv));
                    }
                }
            }
        }
        //endregion

        //region 初始化停靠窗口
        Element dwsEle = (Element) realXmlDocument.selectSingleNode("/application/dockwindows");
        if (dwsEle != null) {
            this.createDockWindows(dwsEle);
        }
        //endregion

        //region 关闭欢迎屏
        this.closeScreenForm();
        welcomeScreens.clear();
        //endregion

        return loadResult;
    }

    //region 欢迎屏

    private void showImageScreen(Image image) {
        ScheduledService ss = new ScheduledService() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Object call() throws Exception {
                        Stage stage = new Stage();
                        stage.setTitle("MapGIS 10");
                        stage.setOnShown(event -> welcomeScreens.add(stage));
                        new WelcomeScreenForm(image).start(stage);
                        return null;
                    }
                };
            }
        };

        ss.setDelay(Duration.millis(0));
        ss.setPeriod(Duration.millis(1000));
        ss.start();
    }

    /**
     * 显示图片欢迎屏
     *
     * @param image 图片
     * @throws Exception
     */
    private void showImageScreenOld(Image image) {
        if (image != null) {
            WelcomeScreenThread thread = new WelcomeScreenThread(image, welcomeScreens);
            thread.start();
            try {
                while (!thread.isShown) {
                    Thread.sleep(1);
                }
            } catch (Exception ex) {
            }
        }
    }

    /**
     * 关闭欢迎屏，关闭ScreenForm时使用try，因为ScreenForm是多线程创建的
     */
    private void closeScreenForm() {
        for (Object closeScreen : welcomeScreens) {
            if (closeScreen instanceof IFormWelcomeScreen) {
                ((IFormWelcomeScreen) closeScreen).close();
            } else if (closeScreen instanceof Stage) {
                try {
                    ((Stage) closeScreen).close();
                } catch (Exception e) {
                }
            }
        }
    }
    //endregion

    //region Ribbon

    /**
     * 切换视图时，创建页面类别
     */
    public void createRibbonPageCategories() {
        IContentsView activeView = application.getActiveContentsView();
        if (activeView != null) {
            String activeViewType = activeView.getClass().getName();
            Map<String, List<RibbonPage>> ribbonCategoryPages = viewRibbonCategories.get(activeViewType);
            if (ribbonCategoryPages == null) {
                ribbonCategoryPages = new HashMap<>();
                if (realXmlDocument != null) {
                    Element categoriesElement = (Element) realXmlDocument.selectSingleNode("/application/ribboncontrol/ribbonpagecategories");
                    if (categoriesElement != null) {
                        List<Element> categoryEles = categoriesElement.elements();
                        int count = 0;
                        for (Element categoryElement : categoryEles) {
                            String cvKey = categoryElement.attributeValue("attachcontentsviewkey");
                            if (activeViewType.equalsIgnoreCase(cvKey)) {
                                List<Element> pageEles = categoryElement.elements();
                                if (pageEles != null && pageEles.size() > 0) {
                                    List<RibbonPage> ribbonPages = new ArrayList<RibbonPage>();
                                    String color = ribbonCategoryColors.get(count % ribbonCategoryPages.size());
                                    for (Element pageEle : pageEles) {
                                        RibbonPage rp = this.createRibbonPage(pageEle);
                                        if (rp != null) {
                                            rp.setContextualColor(color);
                                            ribbonPages.add(rp);
                                        }
                                    }
                                    IRibbonPageCategory iRibbonPageCategory = pluginContainer.getRibbonPageCategories().get(categoryElement.attributeValue("name"));
                                    if (iRibbonPageCategory != null) {
                                        pluginContainer.firePluginLoaded(new PluginLoadedEvent(pluginContainer, iRibbonPageCategory));
                                    }

                                    if (ribbonPages.size() > 0) {
                                        ribbonCategoryPages.put(categoryElement.attributeValue("name").replace('_', '.'), ribbonPages);
                                    }
                                }
                                count++;
                            }
                        }
                    }
                } else//从插件加
                {
                    int count = 0;
                    for (String rcKey : pluginContainer.getRibbonPageCategories().keySet()) {
                        IRibbonPageCategory iRibbonPageCategory = pluginContainer.getRibbonPageCategories().get(rcKey);
                        if (iRibbonPageCategory.getAttachContentsViewKey().equalsIgnoreCase(activeViewType)) {
                            List<RibbonPage> ribbonPages = new ArrayList<RibbonPage>();
                            String color = ribbonCategoryColors.get(count % ribbonCategoryPages.size());
                            for (IRibbonPage iRibbonPage : iRibbonPageCategory.getRibbonPages()) {
                                RibbonPage rp = this.createRibbonPage(iRibbonPage);
                                if (rp != null) {
                                    rp.setContextualColor(color);
                                    ribbonPages.add(rp);
                                }
                            }
                            pluginContainer.firePluginLoaded(new PluginLoadedEvent(pluginContainer, iRibbonPageCategory));

                            if (ribbonPages.size() > 0) {
                                ribbonCategoryPages.put(iRibbonPageCategory.getClass().getName(), ribbonPages);
                            }
                            count++;
                        }
                    }
                }

                if (ribbonCategoryPages.size() > 0) {
                    viewRibbonCategories.put(activeViewType, ribbonCategoryPages);
                }
            }

            for (String rcKey : ribbonCategoryPages.keySet()) {
                ribbon.getTabs().addAll(ribbonCategoryPages.get(rcKey));
            }
        }
    }

    //region 从插件加载

    /**
     * 创建页面
     *
     * @param iRibbonPage
     * @return
     */
    private RibbonPage createRibbonPage(IRibbonPage iRibbonPage) {
        RibbonPage ribbonPage = null;
        if (iRibbonPage != null) {
            ribbonPage = new RibbonPage(iRibbonPage.getText());
            ribbonPage.setId(iRibbonPage.getClass().getName().replace('.', '_'));
            ribbon.getTabs().add(ribbonPage);
            pluginContainer.firePluginLoaded(new PluginLoadedEvent(pluginContainer, iRibbonPage));
            if (ribbonPage.isSelected()) {
                ribbon.setSelectedRibbonTab(ribbonPage);
            }

            for (IRibbonPageGroup iRibbonPageGroup : iRibbonPage.getRibbonPageGroups()) {
                RibbonGroup ribbonGroup = this.createRibbonGroup(iRibbonPageGroup);
                if (ribbonGroup != null) {
                    ribbonPage.getRibbonGroups().add(ribbonGroup);
                }
            }
        }
        return ribbonPage;
    }

    /**
     * 根据插件创建Ribbon页面组
     *
     * @param iRibbonPageGroup 插件
     */
    private RibbonGroup createRibbonGroup(IRibbonPageGroup iRibbonPageGroup) {
        RibbonGroup ribbonGroup = null;
        if (iRibbonPageGroup != null) {
            ribbonGroup = new RibbonGroup(iRibbonPageGroup.getText());
            ribbonGroup.setId(iRibbonPageGroup.getClass().getName().replace('.', '_'));
            pluginContainer.firePluginLoaded(new PluginLoadedEvent(pluginContainer, iRibbonPageGroup));
            IItem[] iItems = iRibbonPageGroup.getIItems();
            if (iItems != null && iItems.length > 0) {
                Column lastColumn = null;
                int colSize = 3;
                for (IItem iItem : iItems) {
                    if (iItem.isGroup()) {
                        Separator separator = new Separator(Orientation.VERTICAL);
                        separator.setId("");
                        ribbonGroup.getNodes().add(separator);
                        lastColumn = null;
                    }

                    Node itemNode = this.createItemControl(iItem);
                    if (itemNode != null) {
                        //将创建出的控件添加到Group中
                        if (iItem.isShowLargeImage()) {
                            ribbonGroup.getNodes().add(itemNode);
                            lastColumn = null;
                        } else {
                            //一列最多放三个小图标控件，若有ButtonGroup只能放两个
                            if (lastColumn == null) {
                                lastColumn = new Column();
                                lastColumn.setId(UUID.randomUUID().toString());
                                ribbonGroup.getNodes().add(lastColumn);
                                colSize = 3;
                            }
                            if (iItem instanceof ButtonGroup) {
                                colSize = 2;
                            }
                            lastColumn.getChildren().add(itemNode);
                            if (lastColumn.getChildren().size() == colSize) {
                                lastColumn = null;
                            }
                        }
                    }
                }
            }
        }
        return ribbonGroup;
    }

    /**
     * 根据IItem创建控件
     *
     * @param items RibbonPageGroup/ButtonGroup的Items
     * @return 控件数组
     */
    private Node[] createItemControls(IItem[] items) {
        Node[] nodes = null;
        if (items != null && items.length > 0) {
            List<Node> nodeList = new ArrayList<>();
            for (IItem iItem : items) {
                if (iItem.isGroup()) {
                    Separator separator = new Separator(Orientation.VERTICAL);
                    separator.setId("");
                    nodeList.add(separator);
                }
                Node node = this.createItemControl(iItem);
                if (node != null) {
                    nodeList.add(node);
                }
            }

            if (nodeList.size() > 0) {
                nodes = nodeList.toArray(new Node[nodeList.size()]);
            }
        }
        return nodes;
    }

    /**
     * 根据IItem创建控件
     *
     * @param iItem 插件UI对象
     * @return 控件
     */
    private Node createItemControl(IItem iItem) {
        Node itemNode = null;
        if (iItem instanceof Item) {
            String key = iItem.getKey();
            IPlugin iPlugin = pluginContainer.getCommands().get(key);
            if (iPlugin == null) {
                iPlugin = pluginContainer.getCheckCommands().get(key);
                if (iPlugin == null) {
                    iPlugin = pluginContainer.getCheckBoxes().get(key);
                    if (iPlugin == null) {
                        iPlugin = pluginContainer.getEditComboboxes().get(key);
                        if (iPlugin == null) {
                            iPlugin = pluginContainer.getDropDowns().get(key);
                        }
                    }
                }
            }

            if (iPlugin != null) {
                itemNode = this.createPluginControl(iPlugin);
            }
        } else if (iItem instanceof ButtonGroup) {
            ButtonGroup buttonGroup = (ButtonGroup) iItem;
            ToolBar toolBar = new ToolBar();
            toolBar.setId("");
            Node[] controls = this.createItemControls(buttonGroup.getItems().toArray(new IItem[0]));
            if (controls != null && controls.length > 0) {
                toolBar.getItems().addAll(controls);
            }
            itemNode = toolBar;
        } else if (iItem instanceof SubItem) {
            SubItem subItem = (SubItem) iItem;
            MenuItem[] menuItems = this.createItemMenus(subItem.getItems());
            String caption = subItem.getCaption();
            String toolTip = "";
            ImageView imageView = null;
            String key = subItem.getKey();
            if (!XString.isNullOrEmpty(key)) {
                ICommand iCommand = pluginContainer.getCommands().get(key);
                if (iCommand != null) {
                    SplitMenuButton splitMenuButton = new SplitMenuButton(menuItems);
                    splitMenuButton.setText(caption != null && caption != "" ? caption : iCommand.getCaption());
                    splitMenuButton.setId(iCommand.getClass().getName().replace('.', '_'));
                    splitMenuButton.setGraphic(new ImageView(iCommand.getImage()));
                    splitMenuButton.setTooltip(new Tooltip(iCommand.getTooltip()));
                    splitMenuButton.setDisable(!iCommand.isEnabled());
                    splitMenuButton.setOnAction(e -> iCommand.onClick());
                    itemNode = splitMenuButton;
                    iCommand.onCreate(application);
                    pluginContainer.firePluginLoaded(new PluginLoadedEvent(pluginContainer, iCommand));
                } else {
                    ISubItem iSubItem = pluginContainer.getSubItems().get(key);
                    if (iSubItem != null) {
                        caption = iSubItem.getCaption();
                        imageView = new ImageView(iSubItem.getImage());
                        toolTip = iSubItem.getTooltip();
                    }
                }
            }

            if (itemNode == null) {
                MenuButton menuButton = new MenuButton(caption, imageView, menuItems);
                menuButton.setId(iItem.getKey().replace('.', '_'));
                if (!XString.isNullOrEmpty(toolTip)) {
                    menuButton.setTooltip(new Tooltip(toolTip));
                }
                itemNode = menuButton;
            }
        }
        if (iItem.isShowLargeImage() && itemNode instanceof Labeled) {
            ((Labeled) itemNode).setContentDisplay(ContentDisplay.TOP);
        }
        return itemNode;
    }

    /**
     * 根据插件创建控件
     *
     * @param plugin 插件
     * @return 控件
     */
    private Node createPluginControl(IPlugin plugin) {
        Node itemNode = null;
        if (plugin instanceof ICommand) {
            ICommand iCommand = (ICommand) plugin;
            Button button = new Button(iCommand.getCaption(), new ImageView(iCommand.getImage()));
            button.setId(iCommand.getClass().getName().replace('.', '_'));
            button.setTooltip(new Tooltip(iCommand.getTooltip()));
            button.setDisable(!iCommand.isEnabled());
            button.setOnAction(e -> iCommand.onClick());
            iCommand.onCreate(this.application);
            itemNode = button;
        } else if (plugin instanceof ICheckCommand) {
            ICheckCommand iCheckCommand = (ICheckCommand) plugin;
            ToggleButton toggleButton = new ToggleButton(iCheckCommand.getCaption(), new ImageView(iCheckCommand.getImage()));
            toggleButton.setId(iCheckCommand.getClass().getName().replace('.', '_'));
            toggleButton.setTooltip(new Tooltip(iCheckCommand.getTooltip()));
            toggleButton.setDisable(!iCheckCommand.isEnabled());
            toggleButton.selectedProperty().addListener((ov, oldVal, newVal) -> iCheckCommand.onSelectedChanged(newVal));
            iCheckCommand.onCreate(this.application);
            itemNode = toggleButton;
        } else if (plugin instanceof ICheckBox) {
            ICheckBox iCheckBox = (ICheckBox) plugin;
            CheckBox checkBox = new CheckBox(iCheckBox.getCaption());
            checkBox.setId(iCheckBox.getClass().getName().replace('.', '_'));
            checkBox.setTooltip(new Tooltip((iCheckBox.getTooltip())));
            CheckStateEnum checkState = iCheckBox.getInitState();
            checkBox.setSelected(checkState.equals(CheckStateEnum.CHECKED));
            checkBox.setIndeterminate(checkState.equals(CheckStateEnum.INDETERMINATE));
            checkBox.setDisable(!iCheckBox.isEnabled());
            checkBox.setPrefWidth(iCheckBox.getWidth());
            checkBox.selectedProperty().addListener((ov, old_val, new_val) -> iCheckBox.onCheckStateChanged(new_val ? CheckStateEnum.CHECKED : CheckStateEnum.UNCHECKED, old_val ? CheckStateEnum.CHECKED : CheckStateEnum.UNCHECKED));
            iCheckBox.onCreate(application);
            itemNode = checkBox;
        } else if (plugin instanceof IDropDown) {
            IDropDown iDropDown = (IDropDown) plugin;
            ColorPicker colorPicker = new ColorPicker();//未完成。平台中用到的IDropDown只有版面编辑和制图编辑里面给点线区注记选色共8个，后面重写，暂用colorPicker
            colorPicker.setId(iDropDown.getClass().getName().replace('.', '_'));
            colorPicker.setTooltip(new Tooltip(iDropDown.getTooltip()));
            colorPicker.setDisable(!iDropDown.isEnabled());
            colorPicker.setOnAction(e -> iDropDown.onClick());
            iDropDown.onCreate(application);
            itemNode = colorPicker;
        } else if (plugin instanceof IEditCombobox) {
            IEditCombobox iEditCombobox = (IEditCombobox) plugin;
            RibbonItem ribbonItem = new RibbonItem();
            ribbonItem.setId(iEditCombobox.getClass().getName().replace('.', '_'));
            ribbonItem.setLabel(iEditCombobox.getCaption());
            if (iEditCombobox.isDropDown()) {
                ZDComboBox<ComboBoxItem> comboBox = new ZDComboBox<>();
                comboBox.setTooltip(new Tooltip(iEditCombobox.getTooltip()));
                comboBox.setPrefWidth(iEditCombobox.getWidth());
                comboBox.setDisable(!iEditCombobox.isEnabled());
                comboBox.getItems().addAll(iEditCombobox.getItems());
                comboBox.setValue((ComboBoxItem) iEditCombobox.getValue());
                comboBox.setVisibleRowCount(iEditCombobox.getDropDownRows());
                comboBox.setEditable(iEditCombobox.isEditable());
                comboBox.valueProperty().addListener((observable, oldValue, newValue) -> iEditCombobox.editValueChanged(newValue));
                ribbonItem.setItem(comboBox);
            } else {
                TextField textField = new TextField(iEditCombobox.getValue().toString());
                textField.setPrefWidth(iEditCombobox.getWidth());
                textField.setTooltip(new Tooltip(iEditCombobox.getTooltip()));
                textField.setDisable(!iEditCombobox.isEnabled());
                textField.textProperty().addListener((observable, oldValue, newValue) -> iEditCombobox.editValueChanged(newValue));
                ribbonItem.setItem(textField);
            }
            iEditCombobox.onCreate(application);
            itemNode = ribbonItem;
        }

        if (itemNode != null) {
            pluginContainer.firePluginLoaded(new PluginLoadedEvent(pluginContainer, plugin));
        }
        return itemNode;
    }

    /**
     * 创建菜单项
     *
     * @param items SubItems的Items
     * @return 菜单项
     */
    private MenuItem[] createItemMenus(IItem[] items) {
        MenuItem[] menuItems = null;
        if (items != null && items.length > 0) {
            List<MenuItem> menuItemList = new ArrayList<>();
            for (IItem iItem : items) {
                if (iItem.isGroup()) {
                    SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
                    separatorMenuItem.setId("");
                    menuItemList.add(separatorMenuItem);
                }

                MenuItem menuItem = this.createItemMenu(iItem);
                if (menuItem != null) {
                    menuItemList.add(menuItem);
                }
            }

            if (menuItemList.size() > 0) {
                menuItems = menuItemList.toArray(new MenuItem[0]);
            }
        }
        return menuItems;
    }

    /**
     * 创建菜单项
     *
     * @param iItem 插件UI对象
     * @return 菜单项
     */
    private MenuItem createItemMenu(IItem iItem) {
        MenuItem menuItem = null;
        if (iItem instanceof Item) {
            String key = iItem.getKey();
            ICommand iCommand = pluginContainer.getCommands().get(key);
            if (iCommand != null) {
                menuItem = new MenuItem(iCommand.getCaption());
                menuItem.setId(iCommand.getClass().getName().replace('.', '_'));
                menuItem.setDisable(!iCommand.isEnabled());
                menuItem.setGraphic(new ImageView(iCommand.getImage()));
                iCommand.onCreate(application);
                pluginContainer.firePluginLoaded(new PluginLoadedEvent(pluginContainer, iCommand));
                menuItem.setOnAction(e -> iCommand.onClick());
            } else {
                ICheckCommand iCheckCommand = pluginContainer.getCheckCommands().get(key);
                if (iCheckCommand != null) {
                    menuItem = new CheckMenuItem(iCheckCommand.getCaption());
                    menuItem.setId(iCheckCommand.getClass().getName().replace('.', '_'));
                    menuItem.setDisable(!iCheckCommand.isEnabled());
                    menuItem.setGraphic(new ImageView(iCheckCommand.getImage()));
                    iCheckCommand.onCreate(application);
                    pluginContainer.firePluginLoaded(new PluginLoadedEvent(pluginContainer, iCheckCommand));
                    ((CheckMenuItem) menuItem).selectedProperty().addListener((ov, old_val, new_val) -> iCheckCommand.onSelectedChanged(new_val));
                }
            }
        } else if (iItem instanceof SubItem) {
            SubItem subItem = (SubItem) iItem;
            ImageView imageView = null;
            String key = subItem.getKey();
            if (!XString.isNullOrEmpty(key)) {
                ISubItem iSubItem = pluginContainer.getSubItems().get(key);
                if (iSubItem != null) {
                    imageView = new ImageView(iSubItem.getImage());
                } else {
                    ICommand iCommand = pluginContainer.getCommands().get(key);
                    if (iCommand != null) {
                        imageView = new ImageView(iCommand.getImage());
                    }
                }
            }
            MenuItem[] menuItems = this.createItemMenus(subItem.getItems());
            menuItem = new Menu(subItem.getCaption(), imageView, menuItems);
            menuItem.setId(key.replace('.', '_'));
        }
        return menuItem;
    }

    //endregion

    //region 从xml加载
    //初始化快速访问栏
    private void createRibbonQuickAccessToolbar(Element accessToolbarElement) {
        if (accessToolbarElement != null) {
            List<Element> itemList = accessToolbarElement.elements();
            for (Element element : itemList) {
                Button button = (Button) this.createItemControl(element);
                if (button != null) {
                    if ("true".equalsIgnoreCase(element.attributeValue("isright"))) {
                        quickAccessBar.getRightButtons().add(button);
                    } else {
                        quickAccessBar.getButtons().add(button);
                    }
                }
            }
        }
    }

    //初始化RibbonPages
    private void createRibbonPages(Element ribbonPagesElement) {
        if (ribbonPagesElement != null) {
            List<Element> itemList = ribbonPagesElement.elements();
            for (Element element : itemList) {
                this.createRibbonPage(element);
            }
        }
    }

    //初始化ribbonpage
    private RibbonPage createRibbonPage(Element ribbonPageElement) {
        RibbonPage ribbonPage = null;
        if (ribbonPageElement != null) {
            ribbonPage = new RibbonPage(ribbonPageElement.attributeValue("text"));
            ribbonPage.setId(ribbonPageElement.attributeValue("name").replace('.', '_'));
            ribbon.getTabs().add(ribbonPage);
            if ("true".equalsIgnoreCase(ribbonPageElement.attributeValue("isselected"))) {
                ribbon.setSelectedRibbonTab(ribbonPage);
            }

            IRibbonPage ribbonPagePlugin = pluginContainer.getRibbonPages().get(ribbonPageElement.attributeValue("name"));
            if (ribbonPagePlugin != null) {
                pluginContainer.firePluginLoaded(new PluginLoadedEvent(pluginContainer, ribbonPagePlugin));
            }

            this.createRibbonPageGroups(ribbonPageElement, ribbonPage);
        }
        return ribbonPage;
    }

    //初始化RibbonPageGroups
    private void createRibbonPageGroups(Element pageGroupsElement, RibbonPage ribbonPage) {
        if (pageGroupsElement != null && ribbonPage != null) {
            List<Element> itemList = pageGroupsElement.elements();
            for (Element pageGroupElement : itemList) {
                RibbonGroup ribbonGroup = new RibbonGroup(pageGroupElement.attributeValue("text"));
                ribbonGroup.setId(pageGroupElement.attributeValue("name").replace('.', '_'));
                ribbonPage.getRibbonGroups().add(ribbonGroup);
                IRibbonPageGroup pageGroupPlugin = pluginContainer.getRibbonPageGroups().get(pageGroupElement.attributeValue("name"));
                if (pageGroupPlugin != null) {
                    pluginContainer.firePluginLoaded(new PluginLoadedEvent(pluginContainer, pageGroupPlugin));
                }

                this.createRibbonPageGroupItems(pageGroupElement, ribbonGroup);
            }
        }
    }

    // 初始化RibbonPageGroup中的项
    private void createRibbonPageGroupItems(Element pageGroupElement, RibbonGroup ribbonGroup) {
        if (pageGroupElement != null && ribbonGroup != null) {
            List<Element> itemList = pageGroupElement.elements();
            for (Element itemElement : itemList) {
                if ("true".equalsIgnoreCase(itemElement.attributeValue("begingroup"))) {
                    Separator separator = new Separator(Orientation.VERTICAL);
                    separator.setId("");
                    ribbonGroup.getNodes().add(separator);
                }

                Node itemNode = this.createItemControl(itemElement);
                if (itemNode != null) {
                    //将创建出的控件添加到Group中
                    ribbonGroup.getNodes().add(itemNode);
                }
            }
        }
    }

    // 根据Element初始化Item
    private MenuItem[] createPopMenuItems(Element parElement) {
        MenuItem[] menuItems = null;
        if (parElement != null) {
            List<Element> itemList = parElement.elements();
            List<MenuItem> menuItemList = new ArrayList<>();
            for (Element itemElement : itemList) {
                if ("true".equalsIgnoreCase(itemElement.attributeValue("begingroup"))) {
                    SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
                    separatorMenuItem.setId("");
                    menuItemList.add(separatorMenuItem);
                }

                MenuItem menuItem = this.createItemMenu(itemElement);
                if (menuItem != null) {
                    menuItemList.add(menuItem);
                }
            }
            if (menuItemList.size() > 0) {
                menuItems = menuItemList.toArray(new MenuItem[menuItemList.size()]);
            }
        }
        return menuItems;
    }

    // 根据Element初始化ButtonGroup中的控件
    private Node[] createButtonGroupItems(Element parElement) {
        Node[] controls = null;
        if (parElement != null) {
            List<Element> itemList = parElement.elements();
            List<Node> controlsList = new ArrayList<>();
            for (Element itemElement : itemList) {
                if ("true".equalsIgnoreCase(itemElement.attributeValue("begingroup"))) {
                    Separator separator = new Separator(Orientation.VERTICAL);
                    separator.setId("");
                    controlsList.add(separator);
                }

                Node node = this.createItemControl(itemElement);
                if (node != null) {
                    controlsList.add(node);
                }
            }
            if (controlsList.size() > 0) {
                controls = controlsList.toArray(new Node[controlsList.size()]);
            }
        }
        return controls;
    }

    /**
     * 根据xml节点结合插件创建Ribbon区功能控件
     *
     * @param itemElement 界面控件对应的xml节点
     * @return Ribbon区控件
     */
    private Node createItemControl(Element itemElement) {
        Node itemNode = null;
        if (itemElement != null) {
            String strType = itemElement.attributeValue("type");
            String name = itemElement.attributeValue("name").replace('_', '.');
            String text = itemElement.attributeValue("caption");
            String toolTip = itemElement.attributeValue("hint");
            boolean isLargeImage = "true".equalsIgnoreCase(itemElement.attributeValue("showlargeimage"));
            ImageView imageView = null;
            if (isLargeImage) {
                String str = itemElement.attributeValue("largedefaulticon");
                if (!XString.isNullOrEmpty(str)) {
                    imageView = new ImageView(this.getImageFromResName(str, false));
                }
            } else {
                String str = itemElement.attributeValue("defaulticon");
                if (!XString.isNullOrEmpty(str)) {
                    imageView = new ImageView(this.getImageFromResName(str, false));
                }
            }

            switch (strType) {
                case "column": {
                    Column column = new Column();
                    column.setId(UUID.randomUUID().toString());
                    for (Object subElement : itemElement.elements()) {
                        Node subNode = this.createItemControl((Element) subElement);
                        if (subNode != null) {
                            column.getChildren().add(subNode);
                        }
                    }
                    if (column.getChildren().size() > 0) {
                        itemNode = column;
                    }
                    break;
                }
                case "command": {
                    Button button = new Button(text);
                    button.setId(name.replace('.', '_'));
                    ICommand iCommand = pluginContainer.getCommands().get(name);
                    if (iCommand != null) {
                        if (imageView == null) {
                            imageView = new ImageView(iCommand.getImage());
                        }
                        if (XString.isNullOrEmpty(toolTip)) {
                            toolTip = iCommand.getTooltip();
                        }
                        button.setDisable(!iCommand.isEnabled());
                        button.setOnAction(e -> iCommand.onClick());
                        iCommand.onCreate(application);
                        pluginContainer.firePluginLoaded(new PluginLoadedEvent(pluginContainer, iCommand));
                    }

                    button.setGraphic(imageView);
                    button.setTooltip(new Tooltip(toolTip));
                    itemNode = button;
                    break;
                }
                case "checkcommand": {
                    ToggleButton toggleButton = new ToggleButton(text);
                    toggleButton.setId(name.replace('.', '_'));
                    ICheckCommand iCheckCommand = pluginContainer.getCheckCommands().get(name);
                    if (iCheckCommand != null) {
                        if (imageView == null) {
                            imageView = new ImageView(iCheckCommand.getImage());
                        }
                        if (XString.isNullOrEmpty(toolTip)) {
                            toolTip = iCheckCommand.getTooltip();
                        }
                        toggleButton.setDisable(!iCheckCommand.isEnabled());
                        toggleButton.selectedProperty().addListener((ov, oldVal, newVal) -> iCheckCommand.onSelectedChanged(newVal));
                        iCheckCommand.onCreate(application);
                        pluginContainer.firePluginLoaded(new PluginLoadedEvent(pluginContainer, iCheckCommand));
                    }

                    toggleButton.setGraphic(imageView);
                    toggleButton.setTooltip(new Tooltip(toolTip));
                    itemNode = toggleButton;
                    break;
                }
                case "checkbox": {
                    CheckBox checkBox = new CheckBox(text);
                    checkBox.setId(name.replace('.', '_'));
                    ICheckBox iCheckBox = pluginContainer.getCheckBoxes().get(name);
                    if (iCheckBox != null) {
                        if (XString.isNullOrEmpty(toolTip)) {
                            toolTip = iCheckBox.getTooltip();
                        }
                        CheckStateEnum checkState = iCheckBox.getInitState();
                        checkBox.setSelected(checkState.equals(CheckStateEnum.CHECKED));
                        checkBox.setIndeterminate(checkState.equals(CheckStateEnum.INDETERMINATE));
                        checkBox.setDisable(!iCheckBox.isEnabled());
                        checkBox.setPrefWidth(iCheckBox.getWidth());
                        checkBox.selectedProperty().addListener((ov, old_val, new_val) -> iCheckBox.onCheckStateChanged(new_val ? CheckStateEnum.CHECKED : CheckStateEnum.UNCHECKED, old_val ? CheckStateEnum.CHECKED : CheckStateEnum.UNCHECKED));

                        iCheckBox.onCreate(application);
                        pluginContainer.firePluginLoaded(new PluginLoadedEvent(pluginContainer, iCheckBox));
                    }
                    checkBox.setTooltip(new Tooltip((toolTip)));
                    itemNode = checkBox;
                    break;
                }
                case "dropdown": {
                    ColorPicker colorPicker = new ColorPicker();//未完成。平台中用到的IDropDown只有版面编辑和制图编辑里面给点线区注记选色共8个，后面重写，暂用colorPicker
                    colorPicker.setId(name.replace('.', '_'));
                    IDropDown iDorpDown = pluginContainer.getDropDowns().get(name);
                    if (iDorpDown != null) {
                        colorPicker.setDisable(!iDorpDown.isEnabled());
                        if (XString.isNullOrEmpty(toolTip)) {
                            toolTip = iDorpDown.getTooltip();
                        }
                        colorPicker.setOnAction(e -> iDorpDown.onClick());

                        iDorpDown.onCreate(application);
                        pluginContainer.firePluginLoaded(new PluginLoadedEvent(pluginContainer, iDorpDown));
                    }
                    colorPicker.setTooltip(new Tooltip(toolTip));
                    itemNode = colorPicker;
                    break;
                }
                case "editcombobox": {
                    RibbonItem ribbonItem = new RibbonItem();
                    ribbonItem.setLabel(text);
                    ribbonItem.setId(name.replace('.', '_'));

                    IEditCombobox iEditCombobox = pluginContainer.getEditComboboxes().get(name);
                    if (iEditCombobox != null) {
                        if (iEditCombobox.isDropDown()) {
                            ZDComboBox<ComboBoxItem> comboBox = new ZDComboBox<>();
                            if (XString.isNullOrEmpty(toolTip)) {
                                toolTip = iEditCombobox.getTooltip();
                            }
                            comboBox.setTooltip(new Tooltip(toolTip));
                            comboBox.setPrefWidth(iEditCombobox.getWidth());
                            comboBox.setDisable(!iEditCombobox.isEnabled());
                            comboBox.getItems().addAll(iEditCombobox.getItems());
                            comboBox.setValue((ComboBoxItem) iEditCombobox.getValue());
                            comboBox.setVisibleRowCount(iEditCombobox.getDropDownRows());
                            comboBox.setEditable(iEditCombobox.isEditable());
                            comboBox.valueProperty().addListener((observable, oldValue, newValue) -> iEditCombobox.editValueChanged(newValue));
                            ribbonItem.setItem(comboBox);
                        } else {
                            TextField textField = new TextField(iEditCombobox.getValue().toString());
                            if (iEditCombobox != null) {
                                textField.setPrefWidth(iEditCombobox.getWidth());
                                if (XString.isNullOrEmpty(toolTip)) {
                                    toolTip = iEditCombobox.getTooltip();
                                }
                                textField.textProperty().addListener((observable, oldValue, newValue) -> iEditCombobox.editValueChanged(newValue));
                            }
                            textField.setTooltip(new Tooltip(toolTip));
                            textField.setDisable(!iEditCombobox.isEnabled());

                            ribbonItem.setItem(textField);
                        }

                        iEditCombobox.onCreate(application);
                        pluginContainer.firePluginLoaded(new PluginLoadedEvent(pluginContainer, iEditCombobox));
                    }
                    itemNode = ribbonItem;
                    break;
                }
                case "subitem": {
                    int length = name.indexOf('$');
                    if (length > 0) {
                        name = name.substring(0, length);
                    }
                    ICommand iCommand = pluginContainer.getCommands().get(name);
                    if (iCommand == null) {
                        MenuItem[] menuItems = this.createPopMenuItems(itemElement);
                        if (imageView == null) {
                            ISubItem subItem = pluginContainer.getSubItems().get(name);
                            if (subItem != null) {
                                imageView = new ImageView(subItem.getImage());
                            }
                            if (XString.isNullOrEmpty(toolTip)) {
                                toolTip = subItem.getTooltip();
                            }
                        }

                        MenuButton menuButton = new MenuButton(itemElement.attributeValue("caption"), imageView, menuItems);
                        menuButton.setId(name.replace('.', '_'));
                        menuButton.setTooltip(new Tooltip(toolTip));
                        itemNode = menuButton;
                    } else {
                        SplitMenuButton splitMenuButton = new SplitMenuButton();
                        splitMenuButton.setId(name.replace('.', '_'));
                        splitMenuButton.setText(itemElement.attributeValue("caption"));
                        splitMenuButton.setGraphic(imageView != null ? imageView : new ImageView(iCommand.getImage()));
                        if (XString.isNullOrEmpty(toolTip)) {
                            toolTip = iCommand.getTooltip();
                        }
                        splitMenuButton.setTooltip(new Tooltip(toolTip));
                        splitMenuButton.setDisable(!iCommand.isEnabled());
                        splitMenuButton.setOnAction(e -> iCommand.onClick());

                        MenuItem[] menuItems = this.createPopMenuItems(itemElement);
                        if (menuItems != null && menuItems.length > 0) {
                            splitMenuButton.getItems().addAll(menuItems);
                        }
                        iCommand.onCreate(application);
                        pluginContainer.firePluginLoaded(new PluginLoadedEvent(pluginContainer, iCommand));
                        itemNode = splitMenuButton;
                    }
                    break;
                }
                case "buttongroup": {
                    ToolBar toolBar = new ToolBar();
                    toolBar.setId("");
                    Node[] items = this.createButtonGroupItems(itemElement);
                    if (items != null && items.length > 0) {
                        toolBar.getItems().addAll(items);
                    }
                    itemNode = toolBar;
                    break;
                }
                default:
                    break;
            }

            if (itemNode != null) {
                this.handleShortcut(itemElement);
                if (imageView != null && imageView.prefWidth(0.0) == 32.0 && itemNode instanceof Labeled) {
                    ((Labeled) itemNode).setContentDisplay(ContentDisplay.TOP);
                }
            }
        }
        return itemNode;
    }

    /**
     * 根据xml节点结合插件创建菜单项
     *
     * @param itemElement 界面控件对应的xml节点
     * @return 菜单项
     */
    private MenuItem createItemMenu(Element itemElement) {
        MenuItem menuItem = null;
        if (itemElement != null) {
            String strType = itemElement.attributeValue("type");
            String name = itemElement.attributeValue("name").replace('_', '.');
            String text = itemElement.attributeValue("caption");
            ImageView imageView = new ImageView(this.getImageFromResName(itemElement.attributeValue("defaulticon"), false));
            switch (strType) {
                case "command": {
                    menuItem = new MenuItem(text);
                    menuItem.setId(itemElement.attributeValue("name").replace('.', '_'));
                    ICommand iCommand = pluginContainer.getCommands().get(name);
                    if (iCommand != null) {
                        if (imageView == null) {
                            imageView = new ImageView(iCommand.getImage());
                        }
                        menuItem.setDisable(!iCommand.isEnabled());
                        menuItem.setOnAction(e -> iCommand.onClick());
                        iCommand.onCreate(application);
                        pluginContainer.firePluginLoaded(new PluginLoadedEvent(pluginContainer, iCommand));
                    }

                    menuItem.setGraphic(imageView);
                    break;
                }
                case "checkcommand": {
                    menuItem = new CheckMenuItem(text);
                    menuItem.setId(itemElement.attributeValue("name").replace('.', '_'));
                    ICheckCommand iCheckCommand = pluginContainer.getCheckCommands().get(name);
                    if (iCheckCommand != null) {
                        if (imageView == null) {
                            imageView = new ImageView(iCheckCommand.getImage());
                        }
                        menuItem.setDisable(!iCheckCommand.isEnabled());
                        ((CheckMenuItem) menuItem).setSelected(iCheckCommand.isChecked());
                        ((CheckMenuItem) menuItem).selectedProperty().addListener((ov, oldVal, newVal) -> iCheckCommand.onSelectedChanged(newVal));

                        iCheckCommand.onCreate(application);
                        pluginContainer.firePluginLoaded(new PluginLoadedEvent(pluginContainer, iCheckCommand));
                    }

                    menuItem.setGraphic(imageView);
                    break;
                }
                case "subitem": {
                    int length = name.indexOf('$');
                    if (length > 0) {
                        name = name.substring(0, length);
                    }
                    if (!name.isEmpty() && imageView == null) {
                        ISubItem subItem = pluginContainer.getSubItems().get(name);
                        if (subItem != null) {
                            imageView = new ImageView(subItem.getImage());
                        } else {
                            ICommand iCommand = pluginContainer.getCommands().get(name);
                            if (iCommand != null) {
                                imageView = new ImageView(iCommand.getImage());
                            }
                        }
                    }
                    MenuItem[] menuItems = this.createPopMenuItems(itemElement);
                    menuItem = new Menu(text, imageView, menuItems);
                    menuItem.setId(itemElement.attributeValue("name").replace('.', '_'));
                    break;
                }
            }
            this.handleShortcut(itemElement);
        }
        return menuItem;
    }
    //endregion

    //endregion

    //region 停靠窗口

    //region 从插件加载

    /**
     * 根据插件创建停靠窗口
     *
     * @param iDockWindow
     * @return
     */
    private DockWindow createDockWindowUI(IDockWindow iDockWindow) {
        //isInitCreate?
        DockWindow dockWindow = null;
        if (disableDickWindows.contains(iDockWindow.getClass().getName())) {
            iDockWindow.onCreate(this.application);
        } else {
            if (dockWindowElementList == null) {
                dockWindowElementList = new ArrayList<>();
                if (appXmlDocument != null) {
                    Element dwsElement = (Element) appXmlDocument.selectSingleNode("/application/dockwindows");
                    if (dwsElement != null) {
                        this.getDockWindowElements(dwsElement, dockWindowElementList);
                    }
                }
            }
            dockWindow = this.initDockWindow(dockWindowElementList, iDockWindow.getClass().getName());
            if (dockWindow == null) {
                iDockWindow.onCreate(application);
                pluginContainer.firePluginLoaded(new PluginLoadedEvent(pluginContainer, iDockWindow));

                dockWindow = new DockWindow(iDockWindow.getChildHWND(), iDockWindow.getCaption(), new ImageView(iDockWindow.getBitmap()));
                dockWindow.setId(iDockWindow.getClass().getName().replace('.', '_'));
                DockPos dockPos = this.getDockPos(iDockWindow.getDefaultDock().toString());

                dockWindow.setOnDockSelected(dockWindowSelectHandler);
                dockWindow.setOnDockClosed(dockWindowClosedHandler);
                dockPane.dock(dockWindow, dockPos);
                iDockWindow.onActive(true);
            }
        }
        return dockWindow;
    }

    /**
     * 根据xml节点初始化DockWindow
     *
     * @param elementList     application.xml里面的dockwindow节点集
     * @param iDockWindowName DockWindow的类名串
     * @return DockWindow
     */
    private DockWindow initDockWindow(List<Element> elementList, String iDockWindowName) {
        DockWindow dockWindow = null;
        if (elementList != null && !XString.isNullOrEmpty(iDockWindowName)) {
            for (Element element : elementList) {
                if (element.attributeValue("name").replace('_', '.').equals(iDockWindowName)) {
                    dockWindow = this.createDockWindow(element);
                    break;
                }
            }
        }
        return dockWindow;
    }

    /**
     * 将dock的字符串转成DockPOS枚举
     *
     * @param strDock dock字符串
     * @return DockPOS枚举
     */
    private DockPos getDockPos(String strDock) {
        DockPos dockPos = DockPos.LEFT;
        try {
            dockPos = DockPos.valueOf(strDock.toUpperCase());
        } catch (Exception ex) {
        }
        return dockPos;
    }

    /**
     * 根据停靠位置获取已有的，进行叠放
     *
     * @param dockPos 停靠位置
     * @return 已有控件
     */
    private Node getSiblingNode(DockPos dockPos) {
        Node siblingNode = null;
        if (dockPane.getChildren().size() > 0) {
            Orientation orientation = dockPos == DockPos.LEFT || dockPos == DockPos.RIGHT ? Orientation.HORIZONTAL : Orientation.VERTICAL;
            ContentSplitPane rootPane = (ContentSplitPane) dockPane.getChildren().get(0);
            ContentSplitPane contentSplitPane = null;
            if (rootPane.getOrientation() == orientation) {
                contentSplitPane = rootPane;
            } else {
                for (int i = 0; i < rootPane.getItems().size(); i++) {
                    Node node = rootPane.getItems().get(i);
                    if (node instanceof ContentSplitPane && ((ContentSplitPane) node).getOrientation() == orientation) {
                        contentSplitPane = (ContentSplitPane) node;
                    }
                }
            }

            if (contentSplitPane != null) {
                switch (dockPos) {
                    case LEFT:
                        if (contentSplitPane.getItems().size() > 0) {
                            siblingNode = contentSplitPane.getItems().get(0);
                        }
                        break;
                    case RIGHT:
                        if (contentSplitPane.getItems().size() > 2) {
                            siblingNode = contentSplitPane.getItems().get(2);
                        }
                        break;
                    case TOP:
                        if (contentSplitPane.getItems().size() > 0) {
                            siblingNode = contentSplitPane.getItems().get(0);
                        }
                        break;
                    case BOTTOM:
                        if (contentSplitPane.getItems().size() > 1) {
                            siblingNode = contentSplitPane.getItems().get(1);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return siblingNode;
    }

    //endregion

    //region 从xml加载

    /**
     * 根据xml中的dockwindows节点创建其中的窗口
     *
     * @param dockwindowsElement
     */
    private void createDockWindows(Element dockwindowsElement) {
        if (dockwindowsElement != null) {
            List<Element> dockwindowElements = dockwindowsElement.elements();
            for (Element dockwindowElement : dockwindowElements) {
                String tagName = dockwindowElement.getName();
                if ("dockwindow".equals(tagName)) {
                    this.createDockWindow(dockwindowElement);
                } else if ("destroydockwindow".equals(tagName)) {
                    destroyDockWindows.add(dockwindowElement);
                }
            }
        }
    }

    /**
     * 根据Xml Element创建DockWindow
     *
     * @param dockWindowElement dockwindows下面的dockwindow节点
     * @return
     */
    private DockWindow createDockWindow(Element dockWindowElement) {
        DockWindow dockWindow = null;
        if (dockWindowElement != null) {
            if (dockWindowElement.isTextOnly()) {
                dockWindow = this.createDockWindow(dockWindowElement, null);
            } else {
                dockWindow = this.createDockWindowNested(dockWindowElement);
            }
        }
        return dockWindow;
    }

    /**
     * 根据dockwindow叶子节点创建DockWindow
     *
     * @param dockWindowElement 各级dockwindow叶子节点
     * @param siblingNode
     * @return
     */
    private DockWindow createDockWindow(Element dockWindowElement, Node siblingNode) {
        DockWindow dockWindow = null;
        if (dockWindowElement != null && dockWindowElement.isTextOnly()) {
            String name = dockWindowElement.attributeValue("name").replace('_', '.');
            IDockWindow iDockWindow = pluginContainer.getDockWindows().get(name);
            if (iDockWindow != null) {
                iDockWindow.onCreate(application);
                if (iDockWindow.isInitCreate()) {
                    dockWindow = new DockWindow(iDockWindow.getChildHWND(), dockWindowElement.attributeValue("text"), new ImageView(iDockWindow.getBitmap()));
                    dockWindow.setId(name.replace('.', '_'));
                    dockWindow.setPrefSize(Double.valueOf(dockWindowElement.attributeValue("prefwidth")), Double.valueOf(dockWindowElement.attributeValue("prefheight")));

                    DockPos dockPos = this.getDockPos(dockWindowElement.attributeValue("dockpos"));
                    if (siblingNode == null) {
                        DockPos siblingDockPos = dockPos;
                        Element parentElement = dockWindowElement.getParent();
                        while ("dockwindow".equals(parentElement.getName())) {
                            siblingDockPos = this.getDockPos(parentElement.attributeValue("dockpos"));
                            parentElement = parentElement.getParent();
                        }
                        siblingNode = this.getSiblingNode(siblingDockPos);
                    }

                    dockWindow.setOnDockSelected(dockWindowSelectHandler);
                    dockWindow.setOnDockClosed(dockWindowClosedHandler);
                    dockPane.dock(dockWindow, dockPos, siblingNode);
                    pluginContainer.firePluginLoaded(new PluginLoadedEvent(pluginContainer, iDockWindow));
                }
            }
        }
        return dockWindow;
    }

    /**
     * 创建嵌套的DockWindow
     *
     * @param dockWindowElement 有子节点的dockwindow节点
     * @return
     */
    private DockWindow createDockWindowNested(Element dockWindowElement) {
        DockWindow dockWindow = null;
        if (dockWindowElement != null && !dockWindowElement.isTextOnly()) {
            List<Element> elements = dockWindowElement.elements();
            for (Element element : elements) {
                if (element.isTextOnly()) {
                    dockWindow = this.createDockWindow(element, dockWindow);
                } else {
                    dockWindow = this.createDockWindowNested(element);
                }
            }
        }
        return dockWindow;
    }

    //endregion

    /**
     * 获取application.xml中的DockWindow节点
     *
     * @param parElement  父节点
     * @param elementList 返回的节点集
     */
    private void getDockWindowElements(Element parElement, List<Element> elementList) {
        if (parElement != null) {
            List<Element> eleList = parElement.elements();
            for (Element element : eleList) {
                if (element.isTextOnly()) {
                    elementList.add(element);

                    Element parEle = element;
                    while (parEle.getParent().getName() != "dockwindows") {
                        parEle = parEle.getParent();
                    }
                    if (parEle != element) {
                        element.addAttribute("dockpos", ((Element) parEle).attributeValue("dockpos"));
                        element.addAttribute("visibility", ((Element) parEle).attributeValue("visibility"));
                    }
                } else {
                    this.getDockWindowElements(element, elementList);
                }
            }
        }
    }

    /**
     * 从xml中读取禁用的窗口
     *
     * @param xDoc
     * @param isAdd
     */
    private void getDisablePlugins(Document xDoc, boolean isAdd) {
        if (xDoc != null) {
            Element element = (Element) xDoc.selectSingleNode("/application/disableplugins");
            if (element != null) {
                String strPluginNames = element.attributeValue("value");
                String[] pluginNames = strPluginNames.split("&");
                for (String pluginName : pluginNames) {
                    if (!XString.isNullOrEmpty(pluginName) && !XString.isNullOrEmpty(pluginName.trim())) {
                        if (isAdd && disableDickWindows.contains(pluginName)) {
                            disableDickWindows.add(pluginName.replace('_', '.'));
                        } else if (!isAdd) {
                            disableDickWindows.remove(pluginName.replace('_', '.'));
                        }
                    }
                }
            }
        }
    }

    /**
     * 根据停靠窗口插件找界面窗口
     *
     * @param iDockWindow
     * @return
     */
    private DockWindow getDockWindow(IDockWindow iDockWindow) {
        DockWindow dockWindow = null;
        if (iDockWindow != null) {
            String pluginName = iDockWindow.getClass().getName();
            for (DockWindow dw : dockPane.getAllNodes()) {
                if (!(dw instanceof DockView) && dw.getId().replace('_', '.').equals(pluginName)) {
                    dockWindow = dw;
                    break;
                }
            }
        }
        return dockWindow;
    }

    /**
     * 根据停靠窗口插件找界面窗口
     *
     * @param pluginName 插件名称
     * @return
     */
    private DockWindow getDockWindow(String pluginName) {
        DockWindow dockWindow = null;
        if (!XString.isNullOrEmpty(pluginName)) {
            for (DockWindow dw : dockPane.getAllNodes()) {
                if (dw.getId().replace('_', '.').equals(pluginName)) {
                    dockWindow = dw;
                    break;
                }
            }
        }
        return dockWindow;
    }
    //endregion

    //region 内容视图

    private DockView createContentsView(IContentsView iContentsView, String name, String text) {
        DockView dockView = null;
        if (iContentsView != null) {
            if (XString.isNullOrEmpty(name)) {
                name = iContentsView.getClass().getName();
            }
            if (XString.isNullOrEmpty(text)) {
                text = iContentsView.getCaption();
            }

            iContentsView.onCreate(application);
            dockView = new DockView(iContentsView.getObjecthWnd(), text, new ImageView(iContentsView.getImage()));
            dockView.setId(name.replace('.', '_'));
            if (!iContentsView.isControlBox()) {
                dockView.setClosable(false);
            }
            dockView.setOnDockSelected(dockViewSelectHandler);
            dockView.setOnDockClosing(dockEvent ->
            {
                DockWindow dockWindow = dockEvent.getDockWindow();
                if (dockWindow instanceof DockView) {
                    IContentsView icv = pluginContainer.getContentsViews().get(dockWindow.getId().replace('_', '.'));
                    if (icv != null) {
                        ContentsViewClosingEvent args = new ContentsViewClosingEvent(this, icv, dockEvent.isCancel());
                        pluginContainer.fireContentsViewClosing(args);
                        dockEvent.setCancel(args.isCancel());
                    }
                }
            });
            dockView.setOnDockClosed(dockEvent ->
            {
                DockWindow dockWindow = dockEvent.getDockWindow();
                if (dockWindow instanceof DockView) {
                    IContentsView icv = pluginContainer.getContentsViews().get(dockWindow.getId().replace('_', '.'));
                    if (icv != null) {
                        pluginContainer.firePluginUnLoaded(new PluginUnLoadedEvent(pluginContainer, icv));
                        pluginContainer.getContentsViews().remove(dockWindow.getId().replace('_', '.'));
                        icv.onClose();
                    }
                }
            });
            dockPane.dock(dockView);
        }
        return dockView;
    }

    /**
     * 获取已有的内容视图的名称集合
     *
     * @return
     */
    private List<String> getContenViewNames() {
        List<String> cvNames = new ArrayList<>();
        List<DockWindow> nodeList = dockPane.getAllNodes();
        if (nodeList != null) {
            for (DockWindow dw : nodeList) {
                if (dw instanceof DockView) {
                    cvNames.add(dw.getId().replace('_', '.'));
                }
            }
        }
        return cvNames;
    }

    /**
     * 根据内容视图插件找界面视图
     *
     * @param iContentsView
     * @return
     */
    private DockView getDockView(IContentsView iContentsView) {
        DockView dockView = null;
        if (iContentsView != null) {
            for (DockWindow dockWindow : dockPane.getAllNodes()) {
                if (dockWindow instanceof DockView) {
                    String viewKey = dockWindow.getId().replace('_', '.');
                    IContentsView icv = pluginContainer.getContentsViews().get(viewKey);
                    if (icv == iContentsView) {
                        dockView = (DockView) dockWindow;
                        break;
                    }
                }
            }
        }
        return dockView;
    }
    //endregion
    //endregion

    //region 保存

    /**
     * 保存界面布局
     */
    public void saveConfigFile() {
        Document xDoc = DocumentHelper.createDocument();

        //region 创建Xml文档，添加根节点
        Element rootElement = xDoc.addElement("application");
        rootElement.addAttribute("createtime", LocalDate.now().toString() + " " + LocalTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)));
        //endregion

        //region 保存jar包
        Element packagesElement = rootElement.addElement("packages");
        for (int i = 0; i < pluginContainer.getAssemblyArray().size(); i++) {
            String path = (String) pluginContainer.getAssemblyArray().get(i);
            Element packageElement = packagesElement.addElement("package");
            packageElement.setText(path);
        }
        //endregion

        //region 保存mainapp
        Element mainappElement = rootElement.addElement("mainapp");
        mainappElement.addAttribute("caption", this.application.getTitle());
        mainappElement.addAttribute("windowstate", primaryStage.isMaximized() ? "maximized" : "normal");
        mainappElement.addAttribute("width", String.valueOf(primaryStage.getWidth()));
        mainappElement.addAttribute("height", String.valueOf(primaryStage.getWidth()));
        mainappElement.addAttribute("x", String.valueOf(primaryStage.getX()));
        mainappElement.addAttribute("y", String.valueOf(primaryStage.getY()));
        //endregion

        //region 保存欢迎屏
        Element wcElement = rootElement.addElement("welcomescreens");
        //endregion

        //region 保存RibbonControl
        Element ribbonElement = rootElement.addElement("ribboncontrol");
        saveRibbonControl(ribbonElement);
        //endregion

        //region 保存DockWindow
        //保存停靠着的DockWindow
        Element windowsElement = rootElement.addElement("dockwindows");
        if (dockPane.getChildren().size() > 0) {
            for (Node node : ((ContentPane) dockPane.getChildren().get(0)).getChildrenList()) {
                this.saveDockWindow(node, windowsElement, windowsElement);
            }
        }

        //保存浮动的DockWindow
        for (DockWindow dockWindow : dockPane.getAllNodes()) {
            if (!(dockWindow instanceof DockView) && dockWindow.isFloating()) {
                Element dwElement = this.saveDockWindow(dockWindow, windowsElement, windowsElement);
                if (dwElement != null) {
                    dwElement.addAttribute("dockpos", "Float");
                    dwElement.addAttribute("floatx", String.valueOf(dockWindow.getStage().getX()));
                    dwElement.addAttribute("floaty", String.valueOf(dockWindow.getStage().getY()));
                }
            }
        }

        ////保存用户销毁的DockWindow位置信息
        //for (Element ele : destroyDockWindows) {
        //    DockWindow dockWindow = this.getDockWindow(ele.attributeValue("name"));
        //    if (dockWindow == null) {
        //        Element windowElement = windowsElement.addElement("destroydockwindow");
        //        List<Attribute> atts = ele.attributes();
        //        for (Attribute att : atts) {
        //            windowElement.addAttribute(att.getName(), att.getValue());
        //        }
        //    }
        //}
        //endregion

        //region 保存内容视图
        Element viewsElement = rootElement.addElement("contentsviews");
        saveContentsView(viewsElement);
        //endregion

        ////保存最近文档//未完成
        //for (int i = 0; i < application.getRecentFileManager().getLength(); i++)
        //{
        //    IRecentFileGroup rfg =application.getRecentFileManager().get(i);
        //    if (rfg != null)
        //    {
        //        config.AppSettings.Settings.Remove(rfg.Key);
        //        config.AppSettings.Settings.Add(rfg.Key, rfg.ToString());
        //    }
        //}

        //把XML文档输出到指定的文件
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            String configPath = this.getConfigFilePath();
            System.out.println(configPath);
            XMLWriter writer = new XMLWriter(new FileOutputStream(configPath), format);//如果用FIleWriter构造，直接运行jar包时所生成的xml文件的实际编码不是UFT-8
            writer.write(xDoc);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getConfigFilePath() {
        String configPath = XPath.getJarPath(getClass());
        if ((new File(configPath).isDirectory())) {
            configPath += application.getTitle() + ".config";
        } else {
            configPath += ".config";
        }
        return configPath;
    }

    /**
     * 保存RibbonControl
     *
     * @param ribbonElement
     */
    private void saveRibbonControl(Element ribbonElement) {
        //保存快速访问栏
        Element barElement = ribbonElement.addElement("quickaccesstoolbar");
        for (Button button : quickAccessBar.getAllButtons()) {
            Element itemElement = barElement.addElement("item");
            itemElement.addAttribute("name", button.getId().replace('_', '.'));
            if (quickAccessBar.getRightButtons().contains(button)) {
                itemElement.addAttribute("isright", "true");
            }
        }

        //保存非分组RibbonPages
        Element pagesElement = ribbonElement.addElement("ribbonpages");
        for (RibbonPage ribbonPage : ribbon.getTabs()) {
            IRibbonPage iRibbonPage = pluginContainer.getRibbonPages().get(ribbonPage.getId().replace('_', '.'));
            if (iRibbonPage != null && XString.isNullOrEmpty(iRibbonPage.getCategoryKey())) {
                this.saveRibbonPage(ribbonPage, pagesElement);
            }
        }

        //保存RibbonPageCategory
        Element categoriesElement = ribbonElement.addElement("ribbonpagecategories");
        for (String cvKey : viewRibbonCategories.keySet()) {
            Map<String, List<RibbonPage>> categoryPages = viewRibbonCategories.get(cvKey);
            for (String categoryKey : categoryPages.keySet()) {
                List<RibbonPage> ribbonPages = categoryPages.get(categoryKey);
                if (ribbonPages.size() > 0) {
                    Element categoryElement = categoriesElement.addElement("ribbonpagecategory");

                    IRibbonPageCategory iRibbonPageCategory = pluginContainer.getRibbonPageCategories().get(categoryKey);
                    categoryElement.addAttribute("name", categoryKey);
                    categoryElement.addAttribute("text", iRibbonPageCategory.getText());
                    categoryElement.addAttribute("visible", String.valueOf(ribbon.getTabs().contains(ribbonPages.get(0))));
                    categoryElement.addAttribute("color", ribbonPages.get(0).getContextualColor().toString());

                    for (RibbonPage ribbonPage : ribbonPages) {
                        this.saveRibbonPage(ribbonPage, categoryElement);
                    }
                }
            }
        }
    }

    /**
     * 保存RibbonPage
     *
     * @param ribbonPage
     * @param parElement
     */
    private void saveRibbonPage(RibbonPage ribbonPage, Element parElement) {
        if (ribbonPage != null && parElement != null) {
            Element pageElement = parElement.addElement("ribbonpage");
            pageElement.addAttribute("name", ribbonPage.getId().replace('_', '.'));
            pageElement.addAttribute("text", ribbonPage.getText());
            if (ribbon.getSelectedRibbonTab() == ribbonPage) {
                pageElement.addAttribute("isselected", "true");
            }

            for (RibbonGroup ribbonGroup : ribbonPage.getRibbonGroups()) {
                this.saveRibbonGroup(ribbonGroup, pageElement);
            }
        }
    }

    /**
     * 保存RibbonGroup
     *
     * @param ribbonGroup
     * @param pageElement
     */
    private void saveRibbonGroup(RibbonGroup ribbonGroup, Element pageElement) {
        Element groupElement = pageElement.addElement("ribbonpagegroup");
        groupElement.addAttribute("name", ribbonGroup.getId().replace('_', '.'));
        groupElement.addAttribute("text", ribbonGroup.getTitle());

        boolean beginGroup = false;
        for (Node ribbonItem : ribbonGroup.getNodes()) {
            if (ribbonItem instanceof Separator) {
                beginGroup = true;
            } else {
                this.saveRibbonItem(ribbonItem, groupElement, beginGroup);
                beginGroup = false;
            }
        }
    }

    /**
     * 保存ribbon区的控件
     *
     * @param node       ribbon区按钮等控件
     * @param parElement 父级xml节点
     * @param beginGroup 是否开始新的组
     */
    private void saveRibbonItem(Node node, Element parElement, boolean beginGroup) {
        if (node != null && parElement != null) {
            String strID = "";
            if (!XString.isNullOrEmpty(node.getId())) {
                strID = node.getId().replace('_', '.');
                int index = strID.indexOf("$");
                if (index > 0) {
                    strID = strID.substring(0, index);
                }
            } else {
                strID = UUID.randomUUID().toString();
            }

            Element itemElement = parElement.addElement("item");
            itemElement.addAttribute("name", strID);
            if (node instanceof Labeled) {
                Labeled labeled = (Labeled) node;
                Node graphic = labeled.getGraphic();
                String imageName = (graphic != null && graphic instanceof ImageView) ? this.imageToResName.get(((ImageView) graphic).getImage()) : "";
                if (imageName != null && !imageName.isEmpty()) {
                    itemElement.addAttribute("defaulticon", imageName);
                }

                itemElement.addAttribute("caption", labeled.getText());

                Tooltip tooltip = labeled.getTooltip();
                if (tooltip != null) {
                    itemElement.addAttribute("hint", tooltip.getText());
                }
            }

            if (node instanceof MenuButton) {
                MenuButton menuButton = (MenuButton) node;
                itemElement.addAttribute("type", "subitem");
                boolean tempBeginGroup = false;
                for (MenuItem menuItem : menuButton.getItems()) {
                    if (menuItem instanceof SeparatorMenuItem) {
                        tempBeginGroup = true;
                    } else {
                        this.saveMenuItem(menuItem, itemElement, tempBeginGroup);
                        tempBeginGroup = false;
                    }
                }
            } else if (node instanceof ToolBar) {
                ToolBar bar = (ToolBar) node;
                itemElement.addAttribute("type", "buttongroup");
                boolean tempBeginGroup = false;
                for (Node item : bar.getItems()) {
                    if (item instanceof Separator) {
                        tempBeginGroup = true;
                    } else {
                        this.saveRibbonItem(item, itemElement, tempBeginGroup);
                        tempBeginGroup = false;
                    }
                }
            } else if (node instanceof Column) {
                itemElement.addAttribute("type", "column");
                for (Node subNode : ((Column) node).getChildren()) {
                    this.saveRibbonItem(subNode, itemElement, false);
                }
            } else if (node instanceof Button) {
                ICommand icommand = pluginContainer.getCommands().get(strID);
                if (icommand != null) {
                    itemElement.addAttribute("type", "command");
                }
            } else if (node instanceof ToggleButton) {
                ICheckCommand iCheckCommand = pluginContainer.getCheckCommands().get(strID);
                if (iCheckCommand != null) {
                    itemElement.addAttribute("type", "checkcommand");
                }
            } else if (node instanceof ColorPicker) {
                IDropDown dropDown = pluginContainer.getDropDowns().get(strID);
                if (dropDown != null) {
                    itemElement.addAttribute("type", "dropdown");
                }
            } else if (node instanceof CheckBox) {
                ICheckBox icheckbox = pluginContainer.getCheckBoxes().get(strID);
                if (icheckbox != null) {
                    itemElement.addAttribute("type", "checkbox");
                }
            } else if (node instanceof RibbonItem) {
                RibbonItem ribbonItem = (RibbonItem) node;
                IEditCombobox ieditcombobox = pluginContainer.getEditComboboxes().get(strID);
                if (ieditcombobox != null) {
                    itemElement.addAttribute("type", "editcombobox");
                }
                itemElement.addAttribute("caption", ribbonItem.getLabel());

                Node graphic = ribbonItem.getGraphic();
                String imageName = (graphic != null && graphic instanceof ImageView) ? this.imageToResName.get(((ImageView) graphic).getImage()) : "";
                if (imageName != null && !imageName.isEmpty()) {
                    itemElement.addAttribute("defaulticon", imageName);
                }

                Tooltip tooltip = ((Control) ribbonItem.getItem()).getTooltip();
                if (tooltip != null) {
                    itemElement.addAttribute("hint", tooltip.getText());
                }
            }
            itemElement.addAttribute("begingroup", String.valueOf(beginGroup));

            if (pluginShortcuts.containsKey(strID)) {
                KeyCodeCombination kcc = pluginShortcuts.get(strID);
                itemElement.addAttribute("shortcutkey", kcc.toString());
            }
        }
    }

    /**
     * 保存下拉菜单项
     *
     * @param menuItem
     * @param parElement
     * @param beginGroup
     */
    private void saveMenuItem(MenuItem menuItem, Element parElement, boolean beginGroup) {
        if (menuItem != null && parElement != null) {
            Element itemElement = parElement.addElement("item");
            itemElement.addAttribute("name", menuItem.getId().replace('_', '.'));
            itemElement.addAttribute("caption", menuItem.getText());

            Node graphic = menuItem.getGraphic();
            String imageName = (graphic != null && graphic instanceof ImageView) ? this.imageToResName.get(((ImageView) graphic).getImage()) : "";
            if (imageName != null && !imageName.isEmpty()) {
                itemElement.addAttribute("defaulticon", imageName);
            }
            itemElement.addAttribute("type", menuItem instanceof Menu ? "subitem" : "command");

            if (menuItem instanceof CheckMenuItem) {
                itemElement.addAttribute("ischecked", "true");
            }
        }
    }

    /**
     * 保存窗口
     *
     * @param node       窗口对象（DockWindow/ContentPane)
     * @param parElement 父节点
     * @param dwsElement “dockwindows”节点
     * @return
     */
    private Element saveDockWindow(Node node, Element parElement, Element dwsElement) {
        Element dwElement = null;
        if (node != null && parElement != null) {
            if (node instanceof DockWindow && !(node instanceof DockView)) {
                DockWindow dockWindow = (DockWindow) node;
                String strID = dockWindow.getId().replace('_', '.');
                IDockWindow iDockWindow = pluginContainer.getDockWindows().get(strID);
                boolean isInitCreate = iDockWindow.isInitCreate();
                if (isInitCreate) {
                    dwElement = parElement.addElement("dockwindow");
                } else {
                    dwElement = dwsElement.addElement("destroydockwindow");
                }
                dwElement.addAttribute("name", strID);
                dwElement.addAttribute("text", dockWindow.getTitle());
                dwElement.addAttribute("prefwidth", String.valueOf(dockWindow.getWidth()));
                dwElement.addAttribute("prefheight", String.valueOf(dockWindow.getHeight()));
                dwElement.addAttribute("dockpos", dockWindow.getLastDockPos().toString());

                Element par = dwElement.getParent();
                while ("dockwindow".equals(par.getName()) && par.attribute("dockpos") == null) {
                    par.addAttribute("dockpos", dockWindow.getLastDockPos().toString());
                    par = par.getParent();
                }
            } else if (node instanceof ContentTabPane) {
                dwElement = parElement.addElement("dockwindow");
                for (Node subNode : ((ContentTabPane) node).getChildrenList()) {
                    this.saveDockWindow(subNode, dwElement, dwsElement);
                }
                if (dwElement.isTextOnly()) {
                    parElement.remove(dwElement);
                }
            } else if (node instanceof ContentSplitPane) {
                ContentSplitPane splitPane = (ContentSplitPane) node;
                dwElement = parElement.addElement("dockwindow");
                for (Node subNode : splitPane.getChildrenList()) {
                    this.saveDockWindow(subNode, dwElement, dwsElement);
                }
            }
        }
        return dwElement;
    }

    private void saveContentsView(Element contentsViewsElement) {
        if (contentsViewsElement != null) {
            for (DockWindow dockWindow : dockPane.getAllNodes()) {
                if (dockWindow instanceof DockView) {
                    String strID = dockWindow.getId().replace('_', '.');
                    IContentsView icv = pluginContainer.getContentsViews().get(strID);
                    if (icv != null && icv.isInitCreate()) {
                        Element contentsviewElement = contentsViewsElement.addElement("contentsview");
                        contentsviewElement.addAttribute("name", strID);
                        contentsviewElement.addAttribute("text", dockWindow.getTitle());
                        //contentsviewElement.addAttribute("isselectedpage",dockWindow==dockPane.getActiveView);//未完成。
                    }
                }
            }
        }
    }

    //endregion

    //region 其他

    /**
     * 根据资源名称获取图标
     *
     * @param resName  资源名称
     * @param getSmall 强制获取小图标（有时候给的是32的图标，但需要显示16的）//未完成
     * @return 图标控件
     */
    public Image getImageFromResName(String resName, boolean getSmall) {
        Image image = null;
        if (resName != null && !resName.isEmpty()) {
            String imageName = resName;
            int index = resName.lastIndexOf('?');
            if (index > 0) {
                imageName = resName.substring(0, index);
            }
            image = new Image(getClass().getResource(imageName).toExternalForm());
            // this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();//jar包目录
            // image = new Image(String.format("file:&s", resName));
            // image = new Image(getClass().getResourceAsStream(resName));
            if (image != null) {
                imageToResName.put(image, resName);
            }
        }
        return image;
    }

    public String Bitmap2ResName(Image image) {
        String resName = "";
        if (image != null && imageToResName.containsKey(image)) {
            resName = imageToResName.get(resName);
        }
        return resName;
    }

    /**
     * 处理快捷键
     *
     * @param itemElement 界面控件对应的xml节点
     */
    private void handleShortcut(Element itemElement) {
        if (itemElement != null) {
            KeyCodeCombination kc = this.getKeyCombination(itemElement.attributeValue("shortcutkey"));
            if (kc != null) {
                mainScene.getAccelerators().put(kc, new ShortcutRunable(itemElement));
                String strKey = itemElement.attributeValue("name").replace('_', '.');
                int index = strKey.indexOf("$");
                if (index > 0) {
                    strKey = strKey.substring(0, index);
                }
                if (pluginShortcuts.containsKey(strKey)) {
                    pluginShortcuts.put(strKey, kc);
                }
            }
        }
    }

    /**
     * 根据快捷键字串(如“Ctrl+C”)获取快捷键
     *
     * @param strShortcut 快捷键字串(如“Ctrl+C”)
     * @return 快捷键
     */
    private KeyCodeCombination getKeyCombination(String strShortcut) {
        KeyCodeCombination keyCombination = null;
        if (strShortcut != null && !strShortcut.isEmpty()) {
            int index = strShortcut.indexOf("+");
            if (index == -1) {
                KeyCode keyCode = KeyCode.getKeyCode(strShortcut);
                keyCombination = new KeyCodeCombination(keyCode);
            } else {
                String[] strKeys = strShortcut.split("\\+");
                if (strKeys != null && strKeys.length > 0) {
                    List<KeyCombination.Modifier> modifiers = new ArrayList<>();
                    KeyCode keyCode = null;
                    for (String strKey : strKeys) {
                        KeyCode kc = KeyCode.getKeyCode(strKey.trim());
                        if (kc == KeyCode.CONTROL) {
                            modifiers.add(KeyCombination.CONTROL_DOWN);
                        } else if (kc == KeyCode.SHIFT) {
                            modifiers.add(KeyCombination.SHIFT_DOWN);
                        } else if (kc == KeyCode.ALT) {
                            modifiers.add(KeyCombination.ALT_DOWN);
                        } else {
                            keyCode = kc;
                        }
                    }
                    if (keyCode != null) {
                        keyCombination = new KeyCodeCombination(keyCode, (KeyCombination.Modifier[]) modifiers.toArray());
                    }
                }
            }
        }
        return keyCombination;
    }

    private void setPluginEnableChanged(IPlugin iPlugin, boolean enable) {
        if (iPlugin != null) {
            this.setPluginEnableChanged(iPlugin.getClass().getName(), enable);
        }
    }

    private void setPluginEnableChanged(String pluginName, boolean enable) {
        for (RibbonPage ribbonPage : ribbon.getTabs()) {
            for (RibbonGroup ribbonGroup : ribbonPage.getRibbonGroups()) {
                for (Node node : ribbonGroup.getNodesWithoutSeprator()) {
                    String strID = node.getId().replace('_', '.');
                    if (strID.equals(pluginName) || strID.startsWith(pluginName + "$")) {
                        node.setDisable(!enable);
                    }

                    if (node instanceof MenuButton) {
                        MenuButton menuButton = (MenuButton) node;
                        for (MenuItem menuItem : menuButton.getItems()) {
                            strID = menuItem.getId().replace('_', '.');
                            if (menuItem.getId().replace('_', '.').equals(pluginName) || strID.startsWith(pluginName + "$")) {
                                menuItem.setDisable(!enable);
                            }
                        }
                    } else if (node instanceof ToolBar) {
                        for (Node item : ((ToolBar) node).getItems()) {
                            if (item.getId().replace('_', '.').equals(pluginName)) {
                                item.setDisable(!enable);
                            }
                        }
                    }
                }
            }
        }

        for (Button button : quickAccessBar.getAllButtons()) {
            String strID = button.getId().replace('_', '.');
            if (strID.equals(pluginName) || strID.startsWith(pluginName + "$")) {
                button.setDisable(!enable);
            }
        }
    }

    private void setEditComboboxTextValue(IEditCombobox iEditCombobox, Object value) {
        if (iEditCombobox != null) {
            String pluginName = iEditCombobox.getClass().getName();
            for (RibbonPage ribbonPage : ribbon.getTabs()) {
                for (RibbonGroup ribbonGroup : ribbonPage.getRibbonGroups()) {
                    for (Node ribbonItem : ribbonGroup.getNodesWithoutSeprator()) {
                        if (ribbonItem instanceof RibbonItem && ribbonItem.getId().replace('_', '.').equals(pluginName)) {
                            Node itemNode = ((RibbonItem) ribbonItem).getItem();
                            if (itemNode instanceof ZDComboBox) {
                                ((ZDComboBox) itemNode).setValue(value);
                            } else if (itemNode instanceof TextField) {
                                ((TextField) itemNode).setText(value != null ? value.toString() : "");
                            }
                        }
                    }
                }
            }
        }
    }

    //endregion

    private EventHandler dockViewSelectHandler = new EventHandler<DockEvent>() {
        @Override
        public void handle(DockEvent dockEvent) {
            IContentsView icv = null;
            if (dockEvent != null && dockEvent.getDockWindow() instanceof DockView) {
                icv = pluginContainer.getContentsViews().get(dockEvent.getDockWindow().getId().replace('_', '.'));
            }
            pluginContainer.firePluginActiveChanged(new PluginActiveChangedEvent(pluginContainer, icv));
        }
    };
    private EventHandler dockWindowSelectHandler = new EventHandler<DockEvent>() {
        @Override
        public void handle(DockEvent dockEvent) {
            if (dockEvent != null && !(dockEvent.getDockWindow() instanceof DockView)) {
                IDockWindow activeIDockWindow = dockEvent.getDockWindow() != null ? pluginContainer.getDockWindows().get(dockEvent.getDockWindow().getId().replace('_', '.')) : null;
                for (Map.Entry<String, IDockWindow> kvp : pluginContainer.getDockWindows().entrySet()) {
                    if (kvp.getValue() != null) {
                        kvp.getValue().onActive(kvp.getValue() == activeIDockWindow);
                    }
                }
            }
        }
    };
    private EventHandler dockWindowClosedHandler = new EventHandler<DockEvent>() {
        @Override
        public void handle(DockEvent dockEvent) {
            DockWindow dw = dockEvent.getDockWindow();
            if (!(dw instanceof DockView)) {
                IDockWindow idw = pluginContainer.getDockWindows().get(dw.getId().replace('_', '.'));
                if (idw != null) {
                    pluginContainer.firePluginUnLoaded(new PluginUnLoadedEvent(pluginContainer, idw));
                    pluginContainer.getDockWindows().remove(dw.getId().replace('_', '.'));
                    idw.onDestroy();
                }
            }
        }
    };
}