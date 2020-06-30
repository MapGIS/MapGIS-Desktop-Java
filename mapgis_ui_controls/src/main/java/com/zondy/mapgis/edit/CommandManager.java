package com.zondy.mapgis.edit;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 撤销回退管理器
 *
 * @author cxy
 * @date 2020/05/18
 */
public class CommandManager {
    private final Deque<CommandManager.Command> redoStack;
    private final Deque<CommandManager.Command> undoStack;
    private int limit;

    /**
     * 撤销回退管理器
     */
    public CommandManager() {
        this(100);
    }

    /**
     * 撤销回退管理器
     *
     * @param limit 最大步骤数量
     */
    public CommandManager(int limit) {
        this.redoStack = new ArrayDeque<>();
        this.undoStack = new ArrayDeque<>();
        this.limit = limit > 0 ? limit : 100;
    }

    public void undo() {
        if (this.canUndo()) {
            CommandManager.Command command = this.undoStack.pop();
            this.redoStack.push(command);
            command.undo();
        }
    }

    public void redo() {
        if (this.canRedo()) {
            CommandManager.Command command = this.redoStack.pop();
            this.undoStack.push(command);
            command.redo();
        }
    }

    public boolean canUndo() {
        return !this.undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !this.redoStack.isEmpty();
    }

    public void clear() {
        this.undoStack.clear();
        this.redoStack.clear();
    }

    public void addCommand(CommandManager.Command command) {
        if (command != null) {
            if (this.undoStack.size() == this.limit) {
                this.undoStack.removeLast();
            }

            this.execute(command);
        }
    }

    private void execute(CommandManager.Command command) {
        this.undoStack.push(command);
        this.redoStack.clear();
        command.redo();
    }

    public interface Command {
        void redo();
        void undo();
    }
}
