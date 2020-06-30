package com.zondy.mapgis.gdbmanager.plugin;

import com.zondy.mapgis.gdbmanager.datacontent.XClsBaseInfo;
import com.zondy.mapgis.gdbmanager.gdbcatalog.TreeItemObject;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.enums.DockingStyleEnum;
import com.zondy.mapgis.pluginengine.plugin.IDockWindow;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;

/**
 * @author : ysp
 * @date : 2019-12-17
 **/
public class DataPropertyDW implements IDockWindow {
    private XClsBaseInfo xClsBaseInfo = null;//要素类基本信息页面

    /**
     * @return 要素类基本信息页面
     */
    public XClsBaseInfo getXClsBaseInfo() {
        return this.xClsBaseInfo;
    }
    @Override
    public Image getBitmap() {
        return null;
    }

    @Override
    public String getCaption() {
        return "数据属性";
    }

    @Override
    public Node getChildHWND() {
        return xClsBaseInfo;
    }

    @Override
    public DockingStyleEnum getDefaultDock() {
        return DockingStyleEnum.RIGHT;
    }

    @Override
    public boolean isInitCreate() {
        return false;
    }

    @Override
    public void onActive(boolean isActive) {

    }

    @Override
    public void onCreate(IApplication app) {
        if (app != null){
            this.xClsBaseInfo = new XClsBaseInfo();
        }
    }

    @Override
    public void onDestroy() {
        if (xClsBaseInfo != null) {
            xClsBaseInfo.clearInfo();
        }
    }
    /**
     * 显示选中对象的信息
     *
     * @param trNode GDBCatalog中的当前选中节点
     */
    public void displayInfo(TreeItem<TreeItemObject> trNode) {
        if(xClsBaseInfo != null && trNode != null){
            TreeItemObject obj =trNode.getValue();
            xClsBaseInfo.displayInfo(obj);
        }
    }
}
