package com.zondy.mapgis.workspace.menuitem;

import com.zondy.mapgis.base.Window;
import com.zondy.mapgis.map.Document;
import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.map.Map;
import com.zondy.mapgis.map.Maps;
import com.zondy.mapgis.scene.Scene;
import com.zondy.mapgis.scene.Scenes;
import com.zondy.mapgis.utilities.UtilityTool;
import com.zondy.mapgis.workspace.engine.ISingleMenuItem;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * 导入地图和场景
 *
 * @author cxy
 * @date 2019/11/18
 */
public class ImportMapOrSceneMenuItem implements ISingleMenuItem {
    private IWorkspace workspace;

    /**
     * 点击菜单项
     *
     * @param item 文档项
     */
    @Override
    public void onClick(DocumentItem item) {
        if (item instanceof Document) {
            Document doc = (Document) item;
            FileChooser fc = new FileChooser();
            fc.setTitle("打开地图文档");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Map Files(*.mapx)", "*.mapx"));
            File file = fc.showOpenDialog(Window.primaryStage);
            Document tempDoc = new Document();
            if (file != null && tempDoc.open(file.getAbsolutePath()) > 0) {
                this.workspace.beginUpdateTree();
                Maps maps = doc.getMaps();
                Maps tempMaps = tempDoc.getMaps();
                for (int i = 0; i < tempMaps.getCount(); i++) {
                    Map map = new Map();
                    map.fromXML(tempMaps.getMap(i).toXML());
                    try {
                        map.setName(UtilityTool.autoBreakString(this.getMapName(doc, map.getName()), UtilityTool.MAX_LENGTH_OF_MAPLAYER_NAME, "gbk"));
                    } catch (UnsupportedEncodingException e) {
                        map.setName(this.getMapName(doc, map.getName()));
                    }
                    maps.append(map);
                }
                Scenes scenes = doc.getScenes();
                Scenes tempScenes = tempDoc.getScenes();
                for (int i = 0; i < tempScenes.getCount(); i++) {
                    Scene scene = new Scene();
                    scene.fromXml(tempScenes.getScene(i).toXml());
                    try {
                        scene.setName(UtilityTool.autoBreakString(this.getSceneName(doc, scene.getName()), UtilityTool.MAX_LENGTH_OF_MAPLAYER_NAME, "gbk"));
                    } catch (UnsupportedEncodingException e) {
                        scene.setName(this.getSceneName(doc, scene.getName()));
                    }
                    scenes.addScene(scene);
                }
                this.workspace.endUpdateTree();
                tempDoc.close(false);
            }
        }
    }

    private String getMapName(Document document, String mapName) {
        int start = 1;
        boolean existed = true;
        while (existed) {
            Maps maps = document.getMaps();
            int count = maps.getCount();
            if (count == 0) {
                existed = false;
            } else {
                for (int i = 0; i < count; i++) {
                    Map map = maps.getMap(i);
                    if (map.getName().equals(mapName)) {
                        existed = true;
                        mapName += start++;
                        break;
                    }
                    existed = false;
                }
            }
        }
        return mapName;
    }

    private String getSceneName(Document document, String sceneName) {
        int start = 1;
        boolean existed = true;
        while (existed) {
            Scenes scenes = document.getScenes();
            int count = scenes.getCount();
            if (count == 0) {
                existed = false;
            } else {
                for (int i = 0; i < count; i++) {
                    Scene scene = scenes.getScene(i);
                    if (scene.getName().equals(sceneName)) {
                        existed = true;
                        sceneName += start++;
                        break;
                    }
                    existed = false;
                }
            }
        }
        return sceneName;
    }

    /**
     * 获取菜单项图标
     *
     * @return 菜单项图标
     */
    @Override
    public Image getImage() {
        return null;
    }

    /**
     * 获取命令按钮的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "导入地图和场景";
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
        return true;
    }

    /**
     * 创建后事件
     *
     * @param ws 工作空间引擎
     */
    @Override
    public void onCreate(IWorkspace ws) {
        workspace = ws;
    }
}
