package com.zondy.mapgis.workspace.menuitem;

import com.zondy.mapgis.map.Document;
import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.workspace.engine.ISingleMenuItem;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;

/**
 * 打开文档所在目录
 *
 * @author cxy
 * @date 2019/11/18
 */
public class OpenToDocDirectoryMenuItem implements ISingleMenuItem {
    /**
     * 点击菜单项
     *
     * @param item 文档项
     */
    @Override
    public void onClick(DocumentItem item) {
        if (item instanceof Document) {
            Document doc = (Document) item;
            String filePath = doc.getFilePath();
            File file = new File(filePath);
            if (file.exists()) {
                // TODO: 待添加打开
                // Process.Start("explorer.exe", "/select," + filePath);
                try {
                    java.awt.Desktop.getDesktop().browse(file.getParentFile().toURI());
                } catch (IOException ex) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                }
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
        return new Image(getClass().getResourceAsStream("/Png_OpenToDirectory_16.png"));
    }

    /**
     * 获取命令按钮的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "打开文档所在目录";
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
