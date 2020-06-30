package com.zondy.mapgis.pluginengine.plugin;

import com.zondy.mapgis.pluginengine.IApplication;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;

/**
 * 命令按钮插件
 *
 * @author cxy
 * @date 2019/09/10
 */
public interface ICheckCommand extends IPlugin {
    // TODO: 2019/09/10 BitMap 暂用 Image 替代

    /**
     * 命令按钮的图标
     *
     * @return 图标
     */
    Image getImage();

    /**
     * 命令按钮的标题
     *
     * @return 标题
     */
    String getCaption();

    /**
     * 命令按钮所属的类别
     *
     * @return 类别
     */
    String getCategory();

    /**
     * 命令按钮是否可用
     *
     * @return true/false
     */
    boolean isEnabled();

    /**
     * 命令按钮是否按下
     *
     * @return true/false
     */
    boolean isChecked();

    /**
     * 鼠标移到命令按钮上时状态栏上显示的文本
     *
     * @return 显示的文本
     */
    String getMessage();

    /**
     * 鼠标停留在命令按钮上时弹出的提示文本
     *
     * @return 提示文本
     */
    String getTooltip();

    /**
     * 命令按钮选中时引发的事件：注需将此value设置到isChecked的返回值中。
     */
    void onSelectedChanged(boolean isChecked);

    /**
     * 命令按钮被创建时引发的事件
     *
     * @param app 框架的宿主对象
     */
    void onCreate(IApplication app);
}
