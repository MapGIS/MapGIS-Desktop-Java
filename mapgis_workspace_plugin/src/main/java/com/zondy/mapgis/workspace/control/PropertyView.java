package com.zondy.mapgis.workspace.control;

import com.zondy.mapgis.controls.AttControl;
import com.zondy.mapgis.controls.common.ZDToolBar;
import com.zondy.mapgis.geodatabase.IVectorCls;
import com.zondy.mapgis.geodatabase.RecordSet;
import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.map.MapLayer;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.scene.Map3DLayer;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.awt.*;

/**
 * 图层查看属性视图
 *
 */
public class PropertyView extends BorderPane {
    private IApplication app;//当前应用程序框架
    private DocumentItem curLayer;//当前图层
    private TabPane centerPane;
    public PropertyView(IApplication app)
    {
        this.app = app;
        //初始化界面
        initialize();
    }
    private void  initialize()
    {
        ZDToolBar toolBar = new ZDToolBar();
        CheckBox checkBox_IsReandOnly = new CheckBox("只读");
        checkBox_IsReandOnly.setSelected(true);
        toolBar.getItems().addAll(checkBox_IsReandOnly);
        this.setTop(toolBar);
        centerPane = new TabPane();
        this.setCenter(centerPane);
    }

    /**
     * 浏览给定图层的属性
     * @param item 图层对象
     * @param set 选择集
     */
     public void viewLayerAttribute(DocumentItem item, RecordSet set)
    {
        if (item instanceof MapLayer || item instanceof Map3DLayer)
        {
            AttControl attControl;
            Tab page = this.getLayerPage(item);
            if (page != null)
            {
                attControl = (AttControl) page.getContent();
                attControl.showAttribute(item, set);//刷新
                this.centerPane.getSelectionModel().select(page);
            }
            else
            {
                Tab tabPage = new Tab();
                String text = item instanceof MapLayer ? ((MapLayer)item).getName():((Map3DLayer)item).getName();
                tabPage.setText(text);
                IVectorCls vCls = null;
                if (item instanceof MapLayer)
                    vCls = (IVectorCls) ((MapLayer) item).getData();
                else if (item instanceof Map3DLayer)
                    vCls = (IVectorCls) ((Map3DLayer) item).getData();
                attControl = new AttControl(vCls);
                attControl.showAttribute(item, set);//刷新
                tabPage.setContent(attControl);
                this.centerPane.getTabs().addAll(tabPage);
                this.centerPane.getSelectionModel().select(tabPage);
            }
            this.curLayer = item;
        }
    }

    /**
     * 关闭属性数据（页面）
     * @param layer 图层数据
     */
    public void closeLayerAttribute(DocumentItem layer)
    {
        AttControl attControl;
        Tab page = this.getLayerPage(layer);
        if (page != null)
        {
            attControl = (AttControl) page.getContent();
            attControl.clearData();
            this.centerPane.getTabs().remove(page);
        }
    }

    /// <summary>
    /// 获取显示图层属性表的Tab页
    /// </summary>
    /// <param name="item">图层</param>
    /// <param name="attControl">Tab页中的属性控件界面</param>
    /// <returns>Tab页</returns>
    /**
     * 获取显示图层属性表的Tab页
     * @param item 图层
     * @return Tab页
     */
    private Tab getLayerPage(DocumentItem item)
    {
        Tab tabPage = null;
        if (item instanceof MapLayer || item instanceof Map3DLayer)
        {
            if(this.centerPane.getTabs() != null && this.centerPane.getTabs().size() >0) {
                for (int i = 0; i < this.centerPane.getTabs().size(); i++)
                {
                    Tab tab = this.centerPane.getTabs().get(i);
                    if(tab.getContent() != null && tab.getContent() instanceof AttControl) {
                        AttControl attControl = (AttControl) tab.getContent();
                        if (attControl != null && attControl.getAttLayer() != null) {
                            if (item.getHandle() == attControl.getAttLayer().getHandle()) {
                                tabPage = tab;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return tabPage;
    }

}
