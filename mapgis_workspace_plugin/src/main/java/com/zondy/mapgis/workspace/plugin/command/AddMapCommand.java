package com.zondy.mapgis.workspace.plugin.command;

import com.zondy.mapgis.map.Document;
import com.zondy.mapgis.map.Map;
import com.zondy.mapgis.map.Maps;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICommand;
import javafx.scene.image.Image;

/**
 * 添加地图
 *
 * @author cxy
 * @date 2019/11/29
 */
public class AddMapCommand implements ICommand {
    private IApplication application;

    /**
     * 命令按钮的图标
     *
     * @return 图标
     */
    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_AddMapCmd_32.png"));
    }

    /**
     * 命令按钮的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "添加地图";
    }

    /**
     * 命令按钮所属的类别
     *
     * @return 类别
     */
    @Override
    public String getCategory() {
        return null;
    }

    /**
     * 命令按钮是否可用
     *
     * @return true/false
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * 鼠标移到命令按钮上时状态栏上显示的文本
     *
     * @return 显示的文本
     */
    @Override
    public String getMessage() {
        return "添加二维地图";
    }

    /**
     * 鼠标停留在命令按钮上时弹出的提示文本
     *
     * @return 提示文本
     */
    @Override
    public String getTooltip() {
        return "添加二维地图";
    }

    /**
     * 命令按钮被单击时引发的事件
     */
    @Override
    public void onClick() {
        Document doc = this.application.getDocument();
        int start = 1;
        String mapName = "新地图" + start;
        boolean existed = true;
        while (existed) {
            Maps maps = doc.getMaps();
            if (maps.getCount() == 0) {
                existed = false;
            } else {
                for (int i = 0; i < maps.getCount(); i++) {
                    Map map = maps.getMap(i);
                    if (map.getName().equals(mapName)) {
                        existed = true;
                        mapName = "新地图" + (++start);
                        break;
                    }
                    existed = false;
                }
            }
        }
        Map m = new Map();
        m.setName(mapName);
        m.setPropertyEx("InitOpenView", "true");
        doc.getMaps().append(m);
    }

    /**
     * 命令按钮被创建时引发的事件
     *
     * @param app 框架的宿主对象
     */
    @Override
    public void onCreate(IApplication app) {
        this.application = app;
    }
}
