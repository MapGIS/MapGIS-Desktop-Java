package com.zondy.mapgis.ribbonapploader;

import com.zondy.mapgis.pluginengine.Application;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.ICheckCommand;
import com.zondy.mapgis.pluginengine.plugin.ICommand;
import com.zondy.mapgis.pluginengine.plugin.IDropDown;
import com.zondy.mapgis.pluginengine.plugincollection.IPluginContainer;
import org.dom4j.Element;

/**
 * @file ShortcutRunable.java
 * @brief 快捷键处理
 *
 * @author CR
 * @date 2020-6-12
 */
public class ShortcutRunable implements Runnable {
    private Element itemElement;//功能对应的xml节点
    private IPluginContainer pluginContainer;

    public ShortcutRunable(Element itemElement) {
        this.itemElement = itemElement;
        IApplication app = Application.getApplication();
        if (app != null) {
            pluginContainer = app.getPluginContainer();
        }
    }

    @Override
    public void run() {
        if (pluginContainer == null) {
            return;
        }

        if (itemElement != null) {
            String name = itemElement.attributeValue("name").replace('_', '.');
            String type = itemElement.attributeValue("type");
            switch (type) {
                case "subitem":
                case "command": {
                    if ("subitem".equals(type)) {
                        int length = name.indexOf('$');
                        if (length > 0) {
                            name = name.substring(0, length);
                        } else {
                            name = "";
                        }
                    }

                    if (name != "") {
                        ICommand command = pluginContainer.getCommands().get(name);
                        if (command != null) {
                            command.onClick();
                        }
                    }
                    break;
                }
                case "checkcommand": {
                    ICheckCommand iCheckCommand = pluginContainer.getCheckCommands().get(name);
                    if (iCheckCommand != null) {
                        iCheckCommand.onSelectedChanged(!iCheckCommand.isChecked());
                    }
                }
                case "dropdown": {
                    IDropDown dropDown = pluginContainer.getDropDowns().get(name);
                    if (dropDown != null) {
                        dropDown.onClick();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }
}
