package com.zondy.mapgis.workspace.menuitem;

import com.zondy.mapgis.map.Document;
import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.map.Map;
import com.zondy.mapgis.map.Maps;
import com.zondy.mapgis.workspace.engine.ISingleMenuItem;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import javafx.scene.image.Image;

/**
 * 添加地图
 *
 * @author cxy
 * @date 2019/11/04
 */
public class AddMapMenuItem implements ISingleMenuItem {
    /**
     * 点击菜单项
     *
     * @param item 文档项
     */
    @Override
    public void onClick(DocumentItem item) {
        if (item instanceof Document) {
            Document doc = (Document) item;
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
                            mapName = "新地图" + ++start;
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
    }

    /**
     * 获取菜单项图标
     *
     * @return 菜单项图标
     */
    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_AddMap_16.png"));
    }

    /**
     * 获取命令按钮的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "添加地图";
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

    }
}
