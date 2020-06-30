package com.zondy.mapgis.mapeditor.plugin.command;

import com.zondy.mapgis.base.MessageBox;
import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.geodatabase.QueryDef;
import com.zondy.mapgis.map.*;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICommand;
import com.zondy.mapgis.pluginengine.plugin.IMapContentsView;
import com.zondy.mapgis.sqlquery.LayerSQLQueryDialog;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;

import java.util.Optional;

/**
 * @Description: 属性选择
 * @Author ysp
 * @Date 2020/3/23
 **/
public class SelectDataByAttCommand implements ICommand {
    private IApplication application;

    /**
     * 命令按钮的图标
     *
     * @return 图标
     */
    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_SelectByAttribute_32.png"));
    }

    /**
     * 命令按钮的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "属性选择";
    }

    /**
     * 命令按钮所属的类别
     *
     * @return 类别
     */
    @Override
    public String getCategory() {
        return "";
    }

    /**
     * 命令按钮是否可用
     *
     * @return true/false
     */
    @Override
    public boolean isEnabled() {
        return false;
    }


    /**
     * 鼠标移到命令按钮上时状态栏上显示的文本
     *
     * @return 显示的文本
     */
    @Override
    public String getMessage() {
        return "按属性查询";
    }

    /**
     * 鼠标停留在命令按钮上时弹出的提示文本
     *
     * @return 提示文本
     */
    @Override
    public String getTooltip() {
        return "按属性查询";
    }

    /**
     * 命令按钮被单击时引发的事件
     */
    @Override
    public void onClick() {
        if (this.application.getActiveContentsView() instanceof IMapContentsView) {
            MapControl mapControl = ((IMapContentsView) this.application.getActiveContentsView()).getMapControl();
            if (mapControl != null) {
                LayerSQLQueryDialog dlg = new LayerSQLQueryDialog(mapControl.getMap(), "矢量类|sfcls;acls");//"sfcls;acls"
                Optional<ButtonType> rtn = dlg.showAndWait();
                if (rtn.isPresent() && rtn.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                    MapLayer maplayer = dlg.getSelectMapLayer();
                    QueryDef queryDef = new QueryDef();
                    queryDef.setFilter(dlg.getSQLText());
                    queryDef.setWithSpatial(false);
                    SelectOption selOption = new SelectOption();
                    selOption.setDataType(SelectDataType.AnyVector);
                    selOption.setLayerCtrl(SelectLayerControl.Editable);
                    selOption.setSelMode(SelectMode.Multiply);
//                SelectSet selSet = maplayer.select(queryDef, true, ctrl.Transformation, selOption);
                    SelectSet selSet = maplayer.select(queryDef, true, new Transformation(), selOption);
                    SelectSet sel1 = mapControl.getMap().getSelectSet();
                    sel1.clear();
                    sel1.union(selSet, UnionMode.Add);
                    if (selSet != null) {
                        // TODO: 待添加图层闪烁
                        MessageBox.information(String.format("共查找到%d条记录", selSet.get().length));
                    }
                }
            }
        }
    }

    /**
     * 命令按钮被创建时引发的事件
     *
     * @param app 框架的宿主对象
     */
    @Override
    public void onCreate(IApplication app) {
        this.application = app;
        this.application.getPluginContainer().addContentsViewChangedListener(contentsViewChangedEvent ->
                this.application.getPluginContainer().setPluginEnable(this, this.application.getActiveContentsView() instanceof IMapContentsView));
    }
}
