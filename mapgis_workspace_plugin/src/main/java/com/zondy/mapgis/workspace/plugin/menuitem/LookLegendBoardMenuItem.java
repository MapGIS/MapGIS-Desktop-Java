package com.zondy.mapgis.workspace.plugin.menuitem;

import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.map.Map;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.events.ContentsViewChangedEvent;
import com.zondy.mapgis.pluginengine.events.ContentsViewChangedListener;
import com.zondy.mapgis.pluginengine.plugin.IContentsView;
import com.zondy.mapgis.pluginengine.plugin.IMapContentsView;
import com.zondy.mapgis.workspace.engine.ISingleMenuItem;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;

/**
 * 打开图例板
 *
 * @author cxy
 * @date 2019/11/21
 */
public class LookLegendBoardMenuItem implements ISingleMenuItem {
    private IWorkspace workspace;
    private IApplication application;
    private Map currentMap;

    /**
     * 打开图例板
     *
     * @param application 应用程序
     */
    public LookLegendBoardMenuItem(IApplication application) {
        this.application = application;
        this.application.getPluginContainer().addContentsViewChangedListener(contentsViewChangedEvent ->
                setMenuItemEnbale(contentsViewChangedEvent.getContentsView()));
    }

    /**
     * 点击菜单项
     *
     * @param item 文档项
     */
    @Override
    public void onClick(DocumentItem item) {
        // TODO: 待添加
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("打开图例板");
        alert.showAndWait();
    }

    /**
     * 获取菜单项图标
     *
     * @return 菜单项图标
     */
    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_LookLegendBoardMenuItem_16.png"));
    }

    /**
     * 获取命令按钮的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "打开图例板";
    }

    /**
     * 获取命令按钮是否可用
     *
     * @return true/false
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * 获取命令按钮是否可见
     *
     * @return true/false
     */
    @Override
    public boolean isVisible() {
        return true;
    }

    /**
     * 获取命令按钮是否选中
     *
     * @return true/false
     */
    @Override
    public boolean isChecked() {
        return false;
    }

    /**
     * 获取是否启用分割符
     *
     * @return true/false
     */
    @Override
    public boolean isBeginGroup() {
        return false;
    }

    /**
     * 创建后事件
     *
     * @param ws 工作空间引擎
     */
    @Override
    public void onCreate(IWorkspace ws) {
        workspace = ws;
        if (workspace != null) {
            // TODO: 待 workspace 添加事件
//            workspace.ClickNodeEvent += new ClickNodeHandler(_ws_ClickNodeEvent);
        }
    }

    private void _ws_ClickNodeEvent(DocumentItem item) {
        currentMap = (Map) item;
        setMenuItemEnbale(application.getActiveContentsView());
    }

    private void setMenuItemEnbale(IContentsView contentsView) {
        if (workspace != null && currentMap != null) {
            if (contentsView instanceof IMapContentsView) {
                IMapContentsView iMapView = (IMapContentsView) contentsView;
                // TODO: 待 MapControl 的完成
//                if (iMapView != null && iMapView.getMapControl() != null && iMapView.getMapControl().ActiveMap != null && iMapView.MapControl.ActiveMap.Handle == _CurMap.Handle) {
//                    LegendBoardUC legendBoardForm = null;
//                    LegendMangedEngine.Items.TryGetValue(iMapView.MapControl, out legendBoardForm);
//                    if (legendBoardForm == null) {
//                        _ws.SetMenuItemEnable(this, true);
//                        return;
//                    }
//                }
            }
        }
        workspace.setMenuItemEnable(this, false);
    }
}
