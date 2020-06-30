package com.zondy.mapgis.gdbmanager.plugin;

import com.zondy.mapgis.gdbmanager.gdbcatalog.GDBCatalogPane;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.enums.DockingStyleEnum;
import com.zondy.mapgis.pluginengine.plugin.IDockWindow;
import javafx.scene.Node;
import javafx.scene.image.Image;

/**
 * @author cxy
 * @date 2019/09/24
 */
public class DWGDBCatalog implements IDockWindow
{
    private IApplication app;
    private GDBCatalogPane gdbCatalogPane;

    //region IDockWindow 成员
    @Override
    public Image getBitmap()
    {
        return new Image("/gdbCatalog_16.png");
    }

    @Override
    public String getCaption()
    {
        return "GDBCatalog";
    }

    @Override
    public Node getChildHWND()
    {
        return this.gdbCatalogPane;
    }

    @Override
    public DockingStyleEnum getDefaultDock()
    {
        return DockingStyleEnum.LEFT;
    }

    @Override
    public boolean isInitCreate()
    {
        return true;
    }

    @Override
    public void onActive(boolean isActive)
    {
        if (isActive && this.gdbCatalogPane != null)
        {
            this.gdbCatalogPane.refreshServerNodes();//刷新数据源节点，我们可能在其他地方连接了数据源
        }
    }

    @Override
    public void onCreate(IApplication app)
    {
        if (app != null)
        {
            this.app = app;
            this.gdbCatalogPane = new GDBCatalogPane(this.app);
        }
    }

    @Override
    public void onDestroy()
    {
        if (this.gdbCatalogPane != null)
        {
            this.gdbCatalogPane.closeAndDisConnect();
        }
    }
    //endregion
}
