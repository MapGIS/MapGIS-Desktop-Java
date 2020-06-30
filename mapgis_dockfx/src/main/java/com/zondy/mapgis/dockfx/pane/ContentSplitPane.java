package com.zondy.mapgis.dockfx.pane;

import com.zondy.mapgis.dockfx.dock.DockPane;
import com.zondy.mapgis.dockfx.dock.DockPos;
import com.zondy.mapgis.dockfx.dock.DockWindow;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;

import java.util.Comparator;
import java.util.List;
import java.util.Stack;

/**
 * @file ContentSplitPane.java
 * @brief 分割布局
 *
 * @author CR
 * @date 2020-6-12
 */
public class ContentSplitPane extends SplitPane implements ContentPane {
    /**
     * 父级布局
     */
    transient ContentPane parent;

    @Override
    public Type getType() {
        return Type.SplitPane;
    }

    @Override
    public void setContentParent(ContentPane pane) {
        parent = pane;
    }

    @Override
    public ContentPane getContentParent() {
        return parent;
    }

    public ContentSplitPane() {
    }

    public ContentSplitPane(Node node) {
        getItems().add(node);
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
                    break;
                } else if (children.get(i) instanceof Parent) {
                    stack.push((Parent) children.get(i));
                }
            }

            if (pane != null) {
                break;
            }
        }
        return pane;
    }

    @Override
    public boolean removeNode(Stack<Parent> stack, Node node) {
        ContentPane pane;
        List<Node> children = getChildrenList();
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i) == node) {
                double[] pos = this.calcDividerPositions(this, i);
                getItems().remove(i);
                if (pos != null) {
                    this.setDividerPositions(pos);
                }
                return true;
            } else if (children.get(i) instanceof ContentPane) {
                pane = (ContentPane) children.get(i);
                if (pane.removeNode(stack, node)) {
                    if (pane != DockPane.getViewTabPane()) {
                        if (pane.getChildrenList().size() < 1) {
                            double[] pos = this.calcDividerPositions(this, i);
                            getItems().remove(i);
                            if (pos != null) {
                                this.setDividerPositions(pos);
                            }
                        } else if (pane.getChildrenList().size() == 1 && pane instanceof ContentTabPane && pane.getChildrenList().get(0) instanceof DockWindow) {
                            List<Node> childrenList = pane.getChildrenList();
                            Node sibling = childrenList.get(0);
                            ContentPane contentParent = pane.getContentParent();

                            contentParent.set((Node) pane, sibling);
                            ((DockWindow) sibling).tabbedProperty().setValue(false);
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 重新计算
     *
     * @param pane
     * @param removeIndex
     * @return
     */
    private double[] calcDividerPositions(ContentSplitPane pane, int removeIndex) {
        double[] newPos = null;
        if (pane != null) {
            double[] pos = pane.getDividerPositions();
            if (pos != null && pos.length > 1) {
                newPos = new double[pos.length - 1];
                for (int i = 0; i < newPos.length; i++) {
                    newPos[i] = i < removeIndex - 1 ? pos[i] : pos[i + 1];
                }
            }
        }
        return newPos;
    }

    @Override
    public List<Node> getChildrenList() {
        return getItems();
    }

    @Override
    public void set(Node sibling, Node node) {
        set(getItems().indexOf(sibling), node);
    }

    @Override
    public void set(int idx, Node node) {
        double[] pos = this.getDividerPositions();
        getItems().set(idx, node);
        this.setDividerPositions(pos);
        if (node instanceof ContentPane) {
            ((ContentPane) node).setContentParent(this);
        }
    }

    public void add(int idx, Node node) {
        getItems().add(idx, node);
        if (node instanceof ContentPane) {
            ((ContentPane) node).setContentParent(this);
        }
    }

    public void add(Node node) {
        getItems().add(node);
        if (node instanceof ContentPane) {
            ((ContentPane) node).setContentParent(this);
        }
    }

    @Override
    public void addNode(Node root, Node sibling, Node node, DockPos dockPos) {
        if (dockPos != DockPos.CENTER) {
            ObservableList<Node> splitItems = getItems();
            boolean isHorizontal = getOrientation() == Orientation.HORIZONTAL;
            if (sibling != null && sibling != root) {
                int index = splitItems.indexOf(sibling);
                double[] positions = getDividerPositions();
                double previousPosition = index - 1 >= 0 ? positions[index - 1] : 0;
                double nextPosition = positions.length > index ? positions[index] : 1;
                splitItems.add(index + ((dockPos == DockPos.LEFT || dockPos == DockPos.TOP) ? 0 : 1), node);
                double siblingLength = ContentPane.getRealLength(sibling, isHorizontal);
                double length = ContentPane.getRealLength(node, isHorizontal);
                double scale = length / (siblingLength + length);
                scale = (dockPos == DockPos.LEFT || dockPos == DockPos.TOP) ? previousPosition + (nextPosition - previousPosition) * scale : nextPosition - (nextPosition - previousPosition) * scale;
                setDividerPosition(index, scale);
                for (int i = index + 1; i < getDividerPositions().length; i++) {
                    setDividerPosition(i, positions[i - 1]);
                }
            } else {
                int index = (dockPos == DockPos.LEFT || dockPos == DockPos.TOP) ? 0 : splitItems.size();
                double[] positions = getDividerPositions();
                splitItems.add(index, node);
                int size = splitItems.size();
                double len = ContentPane.getRealLength(node, isHorizontal);
                if (size > 1) {
                    if ((dockPos == DockPos.LEFT || dockPos == DockPos.TOP)) {
                        double lenex = ContentPane.getRealLength(splitItems.get(1), isHorizontal);
                        setDividerPosition(0, (len / lenex) * (positions.length > 0 ? positions[0] : 1));
                        for (int i = 1; i < size - 1; i++) {
                            setDividerPosition(i, positions[i - 1]);
                        }
                    } else {
                        for (int i = 0; i < index - 1; i++) {
                            setDividerPosition(i, positions[i]);
                        }
                        double lenex = ContentPane.getRealLength(splitItems.get(splitItems.size() - 2), isHorizontal);
                        setDividerPosition(index - 1, 1 - (len / lenex) * (1 - (index >= 2 ? positions[index - 2] : 0)));
                    }
                }
            }

            if (node instanceof ContentPane) {
                ((ContentPane) node).setContentParent(this);
            }
        }
    }

    @Override
    protected double computeMaxWidth(double height) {
        if ((getOrientation() == Orientation.VERTICAL) && (!getItems().isEmpty())) {
            return getItems().stream().map(i -> i.maxWidth(height)).min(Comparator.naturalOrder()).get();
        }

        return super.computeMaxWidth(height);
    }
}