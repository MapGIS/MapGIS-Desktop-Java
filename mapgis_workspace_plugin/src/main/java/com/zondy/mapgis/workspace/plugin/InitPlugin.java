package com.zondy.mapgis.workspace.plugin;

import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.IConnect;
import com.zondy.mapgis.workspace.engine.IMenuExtender;
import com.zondy.mapgis.workspace.engine.IMenuItem;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import com.zondy.mapgis.workspace.enums.ItemType;
import com.zondy.mapgis.workspace.plugin.menuitem.LegendSubMenu;
import com.zondy.mapgis.workspace.plugin.menuitem.LookRecords;

import java.util.ArrayList;

/**
 * 初始化插件
 *
 * @author cxy
 * @date 2019/11/21
 */
public class InitPlugin implements IConnect {
    private IApplication application;
    private ArrayList<IMenuItem> menuItems;

    /**
     * 当框架准备加载此 jar 时，首先调用此方法
     *
     * @param app 框架的宿主对象
     */
    @Override
    public void onConnection(IApplication app) {
        this.application = app;
        this.menuItems = new ArrayList<>();
        initMenuItem(this.application.getWorkSpace());
    }

    /**
     * 当框架卸载此 jar 后，调用此方法
     */
    @Override
    public void onDisconnection() {
        if (this.application != null) {
            this.unInitMenuItem(this.application.getWorkSpace());
        }
    }

    private void initMenuItem(IWorkspace workspace) {
        if (workspace == null) {
            return;
        }

        IMenuExtender ime = workspace.getMenuExtender(ItemType.MAP);
        if (ime != null) {
            IMenuItem item = new LegendSubMenu(this.application);
            item.onCreate(workspace);
            ime.insertItem(item, ime.getItems().length - 3);
            menuItems.add(item);
        }
        ime = workspace.getMenuExtender(ItemType.VECTORLAYER);
        if (ime != null) {
            IMenuItem item = new LookRecords();
            item.onCreate(workspace);
            ime.insertItem(item, 7); //属性结构设置菜单未添加，故目前插入的查看属性菜单索引为7
//            ime.insertItem(item, 8);
            menuItems.add(item);
//
//            item = new ThemeManager();
//            item.onCreate(workspace);
//            ime.insertItem(item, 9);
//            menuItems.add(item);
//
//            item = new ModifyUniform();
//            item.onCreate(workspace);
//            ime.insertItem(item, 10);
//            menuItems.add(item);
//
//            item = new LayerEditSubItem();
//            item.onCreate(workspace);
//            ime.insertItem(item, 11);
//            menuItems.add(item);
        }
//
//        ime = workspace.getMenuExtender(ItemType.RASTERLAYER);
//        if (ime != null) {
//            IMenuItem item = new ThemeManager();
//            item.onCreate(workspace);
//            ime.insertItem(item, 7);
//            menuItems.add(item);
//        }
//
//        ime = workspace.getMenuExtender(ItemType.RASTERCATALOGLAYER);
//        if (ime != null) {
//            IMenuItem item = new ThemeManager();
//            item.onCreate(workspace);
//            ime.insertItem(item, 7);
//            menuItems.add(item);
//        }

//        ime = workSpace.GetMenuExtand(typeof(MapGIS.GeoMap.ObjectLayer));
//        if (ime != null)
//        {
//            IMenuItem item = new LookRecords();
//            item.OnCreate(workSpace);
//            ime.InsertItem(item, 5);
//            menuItems.add(item);
//        }
        //镶嵌数据集图层增加属性表父菜单
        // TODO: 添加镶嵌数据集后取消注释
//        ime = workspace.getMenuExtender(typeof(MapGIS.GeoMap.MosaicDatasetLayer));
//        if (ime != null) {
//            IMenuItem item = new OpenMosaicTable();
//            item.onCreate(workspace);
//            ime.insertItem(item, 0);
//            menuItems.add(item);
//        }

//        //Vector3DLayer右键菜单--专题图
//        ime = workspace.getMenuExtender(ItemType.VECTOR3DLAYER);
//        if (ime != null) {
//            IMenuItem item = new LookRecords();
//            item.onCreate(workspace);
//            ime.insertItem(item, 10);
//            menuItems.add(item);
//
//            item = new ThemeManager();
//            item.onCreate(workspace);
//            ime.insertItem(item, 11);
//            menuItems.add(item);
//        }
//
//        ime = workspace.getMenuExtender(ItemType.MODELLAYER);
//        if (ime != null) {
//            IMenuItem item = new LookRecords();
//            item.onCreate(workspace);
//            ime.insertItem(item, 7);
//            menuItems.add(item);
//
//            item = new ModifyUniform();
//            item.onCreate(workspace);
//            ime.insertItem(item, 8);
//            menuItems.add(item);
//        }
    }

    private void unInitMenuItem(IWorkspace workspace) {
        if (workspace != null) {
            IMenuExtender ime = workspace.getMenuExtender(ItemType.MAP);
            if (ime != null) {
                for (IMenuItem mi : menuItems) {
                    ime.removeItem(mi);
                }
            }

            ime = workspace.getMenuExtender(ItemType.VECTORLAYER);
            if (ime != null) {
                for (IMenuItem mi : menuItems) {
                    ime.removeItem(mi);
                }
            }

            ime = workspace.getMenuExtender(ItemType.RASTERLAYER);
            if (ime != null) {
                for (IMenuItem mi : menuItems) {
                    ime.removeItem(mi);
                }
            }

            ime = workspace.getMenuExtender(ItemType.RASTERCATALOGLAYER);
            if (ime != null) {
                for (IMenuItem mi : menuItems) {
                    ime.removeItem(mi);
                }
            }

            // TODO: 添加镶嵌数据集后取消注释
//            ime = workspace.getMenuExtender(typeof(MapGIS.GeoMap.MosaicDatasetLayer));
//            if (ime != null) {
//                for (IMenuItem mi : menuItems) {
//                    ime.removeItem(mi);
//                }
//            }
//            ime = workspace.getMenuExtender(typeof(MapGIS.GeoMap.ObjectLayer));
//            if (ime != null) {
//                for (IMenuItem mi : menuItems) {
//                    ime.removeItem(mi);
//                }
//            }

            ime = workspace.getMenuExtender(ItemType.VECTOR3DLAYER);
            if (ime != null) {
                for (IMenuItem mi : menuItems) {
                    ime.removeItem(mi);
                }
            }

            ime = workspace.getMenuExtender(ItemType.MODELLAYER);
            if (ime != null) {
                for (IMenuItem mi : menuItems) {
                    ime.removeItem(mi);
                }
            }

            menuItems.clear();
        }
    }
}
