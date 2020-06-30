package com.zondy.mapgis.dockfx.pane;

import com.zondy.mapgis.dockfx.dock.*;
import com.zondy.mapgis.dockfx.pane.skin.ContentTabPaneSkin;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Skin;
import javafx.scene.control.TabPane;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * @file ContentTabPane.java
 * @brief 标签布局
 *
 * @author CR
 * @date 2020-6-12
 */
public class ContentTabPane extends TabPane implements ContentPane {
    ContentPane parent;

    public ContentTabPane() {
        this.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) ->
        {
            if (nv instanceof DockNodeTab) {
                DockNodeTab tab = (DockNodeTab) nv;
                DockWindow dockNode = tab.getDockNode();
                if (dockNode instanceof DockView) {
                    Event.fireEvent(dockNode, new DockEvent(dockNode, DockEvent.DOCKSELECTED, dockNode, DockPane.getOldSelectedView()));
                    DockPane.setOldSelectedView((DockView) dockNode);
                } else {
                    Event.fireEvent(dockNode, new DockEvent(dockNode, DockEvent.DOCKSELECTED, dockNode, DockPane.getOldSelectedWindow()));
                    DockPane.setOldSelectedWindow(dockNode);
                }
            } else if (nv == null) {
                DockNodeTab oldTab = (DockNodeTab) ov;
                DockWindow oldNode = oldTab.getDockNode();
                if (oldNode instanceof DockView) {
                    Event.fireEvent(oldNode, new DockEvent(oldNode, DockEvent.DOCKSELECTED, null, oldNode));
                    DockPane.setOldSelectedView(null);
                } else {
                    Event.fireEvent(oldNode, new DockEvent(oldNode, DockEvent.DOCKSELECTED, oldNode, null));
                    DockPane.setOldSelectedWindow(null);
                }
            }
        });
    }

      @Override
    protected Skin<?> createDefaultSkin() {
        return new ContentTabPaneSkin(this);
    }

    @Override
    public Type getType() {
        return Type.TabPane;
    }

    @Override
    public void setContentParent(ContentPane pane) {
        parent = pane;
    }

    @Override
    public ContentPane getContentParent() {
        return parent;
    }

    @Override
    public ContentPane getSiblingParent(Stack<Parent> stack, Node sibling) {
        ContentPane pane = null;
        while (!stack.isEmpty()) {
            Parent parent = stack.pop();
            List<Node> children = parent.getChildrenUnmodifiable();

            if (parent instanceof ContentPane) {
                children = ((ContentPane) parent).getChildrenList();
            }

            for (int i = 0; i < children.size(); i++) {
                if (children.get(i) == sibling) {
                    pane = (ContentPane) parent;
                } else if (children.get(i) instanceof Parent) {
                    stack.push((Parent) children.get(i));
                }
            }
        }
        return pane;
    }

    @Override
    public boolean removeNode(Stack<Parent> stack, Node node) {
        List<Node> children = getChildrenList();
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i) == node) {
                getTabs().remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public void set(int idx, Node node) {
        DockWindow newNode = (DockWindow) node;
        getTabs().set(idx, new DockNodeTab(newNode));
        getSelectionModel().select(idx);
        if (node instanceof ContentPane) {
            ((ContentPane) node).setContentParent(this);
        }
    }

    @Override
    public void set(Node sibling, Node node) {
        set(getChildrenList().indexOf(sibling), node);
    }

    @Override
    public List<Node> getChildrenList() {
        return getTabs().stream().map(i -> i.getContent()).collect(Collectors.toList());
    }

    /**
     * @param root    没用
     * @param sibling 没用
     * @param node    窗口对象
     * @param dockPos 没用
     */
    @Override
    public void addNode(Node root, Node sibling, Node node, DockPos dockPos) {
        DockWindow newNode = (DockWindow) node;
        DockNodeTab t = new DockNodeTab(newNode);
        addDockNodeTab(t);
        if (node instanceof ContentPane) {
            ((ContentPane) node).setContentParent(this);
        }
    }

    public void addDockNodeTab(DockNodeTab dockNodeTab) {
        if (dockNodeTab != null) {
            getTabs().add(dockNodeTab);
            this.getSelectionModel().select(dockNodeTab);
        }
    }

    @Override
    protected double computeMaxWidth(double height) {
        if (!getTabs().isEmpty()) {
            return getTabs().stream().map(i -> i.getContent().maxWidth(height)).min(Comparator.naturalOrder()).get();
        }
        return super.computeMaxWidth(height);
    }
}
