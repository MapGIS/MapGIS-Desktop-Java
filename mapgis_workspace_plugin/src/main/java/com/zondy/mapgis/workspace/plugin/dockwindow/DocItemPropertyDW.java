package com.zondy.mapgis.workspace.plugin.dockwindow;

import com.zondy.mapgis.docitemproperty.DocItemProperty;
import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.enums.DockingStyleEnum;
import com.zondy.mapgis.pluginengine.plugin.IDockWindow;
import javafx.scene.Node;
import javafx.scene.image.Image;

/**
 * 图层属性视窗
 * @author : ysp
 * @date : 2019-12-17
 **/
public class DocItemPropertyDW implements IDockWindow {

    private DocItemProperty docItemProperty = null;
    @Override
    public Image getBitmap() {
        return null;
    }

    @Override
    public String getCaption() {
        return "图层属性视窗";
    }

    @Override
    public Node getChildHWND() {
        return docItemProperty;
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
        if (app != null) {
            docItemProperty = new DocItemProperty();
        }
    }

    @Override
    public void onDestroy() {

    }

    public void displayItemInfo(DocumentItem item){
        if (docItemProperty != null){
            docItemProperty.displayItem(item);
        }
    }
}
