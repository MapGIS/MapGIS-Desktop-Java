package com.zondy.mapgis.workspace.plugin.command;

import com.zondy.mapgis.base.MessageBox;
import com.zondy.mapgis.geodatabase.config.EnvConfig;
import com.zondy.mapgis.geodatabase.config.SysConfigDirType;
import com.zondy.mapgis.map.Document;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICommand;
import com.zondy.mapgis.pluginengine.plugin.IDockWindow;
import com.zondy.mapgis.workspace.plugin.DwWorkspace;
import javafx.scene.image.Image;

import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Files;

/**
 * 新建(F)
 *
 * @author cxy
 * @date 2019/09/18
 */
public class CreateCommand implements ICommand {
    private IApplication application;

    /**
     * 命令按钮的图标
     *
     * @return 图标
     */
    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_New_32.png"));
    }

    /**
     * 命令按钮的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "新建";
    }

    /**
     * 命令按钮所属的类别
     *
     * @return 类别
     */
    @Override
    public String getCategory() {
        return "常用工具";
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
        return "新建地图文档";
    }

    /**
     * 鼠标停留在命令按钮上时弹出的提示文本
     *
     * @return 提示文本
     */
    @Override
    public String getTooltip() {
        return "新建地图文档";
    }

    /**
     * 命令按钮被单击时引发的事件
     */
    @Override
    public void onClick() {
        IDockWindow dockWindow = this.application.getPluginContainer().getDockWindows().getOrDefault(DwWorkspace.class.getName(), null);
        if (dockWindow instanceof DwWorkspace) {
            // TODO: 待补充
            // NewDocumentTool.ShowNewDocumentDialog(this.app.Document, this.app.WorkSpaceEngine, WorkSpacePanel.GetRecentOpenMapxFiles(this.app), true, new MapGIS.PlugUtility.Win32Window(MapGIS.PlugUtility.XHelp.GetMainWindowHandle()));
            Document document = this.application.getDocument();
            if (document != null) {
                boolean closed = document.close(false);
                if (closed) {
                    //document = new Document();
                    //                        File file = new File(URLDecoder.decode(getClass().getResource("/空地图.mapx").getFile(), "utf-8"));
//                        if (file.exists()) {
//                            document.open(file.getAbsolutePath());
//                            System.out.println(document.getTitle() + "打开" + file.getAbsolutePath());
//                        } else {
                    String path = EnvConfig.getConfigDirectory(SysConfigDirType.Root);
                    path = path + File.separator + "MapTemplates" + File.separator + "空地图.mapx";
                    System.out.println(path);
                    if (!new File(path).exists()) {
                        try {
                            InputStream inputStream = getClass().getResourceAsStream("/空地图.mapx");
                            FileOutputStream fos = new FileOutputStream(path);
                            byte[] b = new byte[1024];
                            while ((inputStream.read(b)) != -1) {
                                fos.write(b);// 写入数据
                            }
                            inputStream.close();
                            fos.close();// 保存数据
                        } catch (Exception ex) {
                            System.out.println(ex);
                        }
                    }
                    Document tempDocument = new Document();
                    tempDocument.open(path);
                    document.fromXML(tempDocument.toXML());
                    tempDocument.close(false);
//                        }
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
        if (app != null) {
            this.application = app;
        }
    }
}
