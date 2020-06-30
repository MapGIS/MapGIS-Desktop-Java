package com.zondy.mapgis.dockfx.pane;

import com.zondy.mapgis.dockfx.dock.DockWindow;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Tab;

/**
 * @file DockNodeTab.java
 * @brief ContentTabPane里面的标签页
 *
 * @author CR
 * @date 2020-6-12
 */
public class DockNodeTab extends Tab {
    final private DockWindow dockNode;
    final private SimpleStringProperty title;

    public DockNodeTab(DockWindow node) {
        this.dockNode = node;
        setClosable(false);

        title = new SimpleStringProperty("");
        title.bind(dockNode.titleProperty());

        setGraphic(dockNode.getDockTitleBar());
        setContent(dockNode);
        dockNode.tabbedProperty().set(true);
        dockNode.setNodeTab(this);
    }

    public String getTitle() {
        return title.getValue();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public void select() {
        if (getTabPane() != null) {
            getTabPane().getSelectionModel().select(this);
        }
    }

    public DockWindow getDockNode() {
        return dockNode;
    }
}
