package com.zondy.mapgis.ribbonfx.control;

import com.zondy.mapgis.ribbonfx.skin.RibbonGroupSkin;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.Separator;
import javafx.scene.control.Skin;

/**
 * @file RibbonGroup.java
 * @brief 页面组
 *
 * @author CR
 * @date 2020-6-12
 */
public class RibbonGroup extends Labeled {
    private final static String DEFAULT_STYLE_CLASS = "ribbon-group";
    private final ObservableList<Node> nodes;
    private final SimpleStringProperty title;

    public RibbonGroup() {
        this("");
    }

    public RibbonGroup(String strTitle) {
        nodes = FXCollections.observableArrayList();
        title = new SimpleStringProperty(strTitle);
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
    }

    /**
     * 获取所有节点，包含按钮、分隔条、Column
     *
     * @return
     */
    public ObservableList<Node> getNodes() {
        return nodes;
    }

    /**
     * 获取有效节点（不包含分隔条，Column里面的内容拆出来）
     *
     * @return
     */
    public ObservableList<Node> getNodesWithoutSeprator() {
        ObservableList<Node> nodes = FXCollections.observableArrayList();
        for (Node node : this.nodes) {
            if (!(node instanceof Separator)) {
                if (node instanceof Column) {
                    for (Node n : ((Column) node).getChildren()) {
                        nodes.add(n);
                    }
                } else {
                    nodes.add(node);
                }
            }
        }
        return nodes;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new RibbonGroupSkin(this);
    }
}
