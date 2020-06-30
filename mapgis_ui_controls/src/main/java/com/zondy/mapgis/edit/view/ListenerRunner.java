package com.zondy.mapgis.edit.view;

import javafx.application.Platform;

import java.util.EventObject;
import java.util.List;

/**
 * ListenerRunner<L, E extends EventObject>
 *
 * @author cxy
 * @date 2020/05/29
 */
public abstract class ListenerRunner<L, E extends EventObject> {
    private final boolean mInvokeOnJavaFxApplicationThread = Platform.isFxApplicationThread();
    private final L mListener;

    public ListenerRunner(L listener) {
        this.mListener = listener;
    }

    public final void run(E event) {
        if (this.mInvokeOnJavaFxApplicationThread && !Platform.isFxApplicationThread()) {
            Platform.runLater(() -> {
                this.onRun(event);
            });
        } else {
            this.onRun(event);
        }

    }

    protected abstract void onRun(E var1);

    public L getListener() {
        return this.mListener;
    }

    public static <L, E extends EventObject> boolean removeListener(List<ListenerRunner<L, E>> runners, L listener) {
        return listener != null && runners != null ? runners.removeIf((runner) -> {
            return runner.getListener() == listener;
        }) : false;
    }
}
