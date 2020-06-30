package com.zondy.mapgis.workspace.plugin.command;

import com.zondy.mapgis.map.Document;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICommand;
import com.zondy.mapgis.pluginengine.ui.ISubItem;
import com.zondy.mapgis.scene.Scene;
import com.zondy.mapgis.scene.Scenes;
import com.zondy.mapgis.workspace.engine.ISubMenu;
import javafx.scene.image.Image;

/**
 * 添加场景
 *
 * @author cxy
 * @date 2019/11/29
 */
public class AddSceneCommand implements ICommand {
    private IApplication application;

    /**
     * 命令按钮的图标
     *
     * @return 图标
     */
    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_AddSceneCmd_32.png"));
    }

    /**
     * 命令按钮的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "添加场景";
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
        return true;
    }

    /**
     * 鼠标移到命令按钮上时状态栏上显示的文本
     *
     * @return 显示的文本
     */
    @Override
    public String getMessage() {
        return "添加场景";
    }

    /**
     * 鼠标停留在命令按钮上时弹出的提示文本
     *
     * @return 提示文本
     */
    @Override
    public String getTooltip() {
        return "添加场景";
    }

    /**
     * 命令按钮被单击时引发的事件
     */
    @Override
    public void onClick() {
        Document doc = this.application.getDocument();
        int start = 1;
        String sceneName = "新场景" + start;
        boolean existed = true;
        while (existed) {
            Scenes scenes = doc.getScenes();
            if (scenes.getCount() == 0) {
                existed = false;
            } else {
                for (int i = 0; i < scenes.getCount(); i++) {
                    Scene scene = scenes.getScene(i);
                    if (scene.getName().equals(sceneName)) {
                        existed = true;
                        sceneName = "新场景" + start;
                        break;
                    }
                    existed = false;
                }
            }
        }
        Scene scene = new Scene();
        scene.setName(sceneName);
        scene.setPropertyEx("InitOpenView", "true");
        doc.getScenes().addScene(scene);
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
