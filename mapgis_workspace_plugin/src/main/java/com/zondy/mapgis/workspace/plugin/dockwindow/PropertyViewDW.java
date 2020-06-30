package com.zondy.mapgis.workspace.plugin.dockwindow;

import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.enums.DockingStyleEnum;
import com.zondy.mapgis.pluginengine.plugin.IDockWindow;
import com.zondy.mapgis.workspace.WorkspacePanel;
import com.zondy.mapgis.workspace.control.PropertyView;
import com.zondy.mapgis.workspace.event.Remove3DLayerEvent;
import com.zondy.mapgis.workspace.event.Remove3DLayerListener;
import com.zondy.mapgis.workspace.event.RemoveLayerEvent;
import com.zondy.mapgis.workspace.event.RemoveLayerListener;
import javafx.scene.Node;
import javafx.scene.image.Image;

/**
 * 查看属性停靠面板
 * @author : zkj
 * @date : 2020-5-22
 **/
public class PropertyViewDW implements IDockWindow {
    private PropertyView pv;  //属性视图（界面）
    private IApplication app; //当前应用程序框架

    //region IDockWindow 成员
    @Override
    public Image getBitmap() {
        return null;
    }

    @Override
    public String getCaption() {
        return "属性视图";
    }

    @Override
    public Node getChildHWND() {
         return this.pv;
    }

    @Override
    public DockingStyleEnum getDefaultDock() {
        return DockingStyleEnum.BOTTOM;
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
        this.pv = new PropertyView(app);
        this.app = app;
       //监听工作空间树上的图层移除事件，移除图层后同步关闭对应的属性面板
        WorkspacePanel.getWorkspacePanel(app).addRemoveLayerListener(new RemoveLayerListener() {
            @Override
            public void fireRemoveLayer(RemoveLayerEvent removeLayerEvent) {
                if(removeLayerEvent != null && removeLayerEvent.getMapLayer() != null)
                    pv.closeLayerAttribute(removeLayerEvent.getMapLayer());
            }
        });
        WorkspacePanel.getWorkspacePanel(app).removeRemove3DLayerListener(new Remove3DLayerListener() {
            @Override
            public void fireRemove3DLayer(Remove3DLayerEvent remove3DLayerEvent) {
                if(remove3DLayerEvent != null && remove3DLayerEvent.getMap3DLayer() != null)
                    pv.closeLayerAttribute(remove3DLayerEvent.getMap3DLayer());
            }
        });
//        WorkspacePanel.RemoveLayerEvent += new WorkSpacePanel.RemoveLayerHandler(WorkSpacePanel_RemoveLayerEvent);
//        WorkSpacePanel.Remove3DLayerEvent += new WorkSpacePanel.Remove3DLayerHandler(WorkSpacePanel_Remove3DLayerEvent);
//        WorkSpacePanel.MenuItemOnClickEvent += new WorkSpaceEngine.MenuItemOnClickHandler(WorkSpacePanel_MenuItemOnClickEvent);
    }

    @Override
    public void onDestroy() {

    }
    //endregion

    //region 公共方法
    /// <summary>
    /// 浏览给定图层的属性
    /// </summary>
    /// <param name="item">图层对象</param>
    public void browseProperty(DocumentItem item)
    {
        if (this.pv != null)
            this.pv.viewLayerAttribute(item, null);
    }
    //endregion
}
