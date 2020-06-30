package com.zondy.mapgis.workspace.menuitem;

import com.zondy.mapgis.base.Window;
import com.zondy.mapgis.map.Document;
import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.workspace.engine.ISingleMenuItem;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import com.zondy.mapgis.workspace.event.MenuItemClickEvent;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * 打开地图文档
 *
 * @author cxy
 * @date 2019/10/29
 */
public class OpenDocumentMenuItem implements ISingleMenuItem {
    private IWorkspace workspace;

    /**
     * 点击菜单项
     *
     * @param item 文档项
     */
    @Override
    public void onClick(DocumentItem item) {
        if (item instanceof Document) {
            Document document = (Document) item;
            FileChooser fc = new FileChooser();
            fc.setTitle("打开地图文档");
            fc.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Map Files(*.mapx)", "*.mapx"),
                    new FileChooser.ExtensionFilter("Map Files(*.map)", "*.map"),
                    new FileChooser.ExtensionFilter("Map Bag(*.mbag)", "*.mbag"),
                    new FileChooser.ExtensionFilter("Map project(*.mpj)", "*.mpj"),
                    new FileChooser.ExtensionFilter("All File", "*.*")
            );
            File file = fc.showOpenDialog(Window.primaryStage);
            boolean closed = document.close(false);
            if (closed && file != null) {
                document.open(file.getAbsolutePath());
            }
        }
    }

    /**
     * 获取菜单项图标
     *
     * @return 菜单项图标
     */
    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_Open_16.png"));
    }

    /**
     * 获取命令按钮的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "打开";
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
