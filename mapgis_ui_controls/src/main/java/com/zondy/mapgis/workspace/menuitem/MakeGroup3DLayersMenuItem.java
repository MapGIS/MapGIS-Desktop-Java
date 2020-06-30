package com.zondy.mapgis.workspace.menuitem;

import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.scene.Group3DLayer;
import com.zondy.mapgis.scene.Map3DLayer;
import com.zondy.mapgis.scene.Scene;
import com.zondy.mapgis.workspace.engine.IMultiMenuItem;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import javafx.scene.image.Image;

/**
 * 成组
 *
 * @author cxy
 * @date 2019/11/18
 */
public class MakeGroup3DLayersMenuItem implements IMultiMenuItem {
    private IWorkspace workspace;

    /**
     * 点击菜单项
     *
     * @param items 文档项
     */
    @Override
    public void onClick(DocumentItem[] items) {
        if (items != null && items.length > 0) {
            Group3DLayer group3Dlayer = new Group3DLayer();
            group3Dlayer.setName("新组");
            workspace.beginUpdateTree();
            int n = items.length;
            //items重排序
            for (int i = 1; i < n; i++) {
                for (int j = 0; j < n - i; j++) {
                    if (items[i].getParent() instanceof Scene) {
                        Scene scene = (Scene) (items[i].getParent());
                        if (scene.indexOfLayer((Map3DLayer) items[j]) > scene.indexOfLayer((Map3DLayer) items[j + 1])) {
                            DocumentItem item = items[j + 1];
                            items[j + 1] = items[j];
                            items[j] = item;
                        }
                    } else if (items[j].getParent() instanceof Group3DLayer) {
                        Group3DLayer group3D = (Group3DLayer) (items[j].getParent());
                        if (group3D.indexOfLayer((Map3DLayer) items[j]) > group3D.indexOfLayer((Map3DLayer) items[j + 1])) {
                            DocumentItem item = items[j + 1];
                            items[j + 1] = items[j];
                            items[j] = item;
                        }
                    }
                }
            }
            //成组
            for (int i = 0; i < n; i++) {
                if (items[i].getParent() instanceof Scene) {
                    Scene scene = (Scene) (items[i].getParent());
                    if (i == 0) {
                        int layerno = scene.indexOfLayer((Map3DLayer) items[i]);
                        scene.insertLayer(group3Dlayer, layerno);
                    }
                    scene.dragOutLayer((Map3DLayer) items[i]);
                    group3Dlayer.addLayer((Map3DLayer) items[i]);
                } else if (items[i].getParent() instanceof Group3DLayer) {
                    Group3DLayer group3D = (Group3DLayer) (items[i].getParent());
                    if (i == 0) {
                        int layerno = group3D.indexOfLayer((Map3DLayer) items[i]);
                        group3D.insertLayer(group3Dlayer, layerno);
                    }
                    group3D.dragOutLayer((Map3DLayer) items[i]);
                    group3Dlayer.addLayer((Map3DLayer) items[i]);
                }
            }
            workspace.endUpdateTree();
        }
    }

    /**
     * 获取菜单项图标
     *
     * @return 菜单项图标
     */
    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_CombineGroupLayer_16.png"));
    }

    /**
     * 获取命令按钮的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "成组";
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
    }
}
