package com.zondy.mapgis.utilities;

import java.util.ArrayList;
import java.util.EventObject;

/**
 * @author cxy
 * @date 2019/09/16
 */
public class EventHelper<L extends MyEventListener, E extends EventObject> {
    private ArrayList<L> listeners;

    public EventHelper() {
        this.listeners = new ArrayList<>();
    }

    public void addListener(L listener) {
        this.listeners.add(listener);
    }

    public void removeListener(L listener) {
        this.listeners.remove(listener);
    }

    public void clear() {
        this.listeners.clear();
    }

    public void fireListeners(E event) {
        for (L listener : this.listeners) {
            listener.fire(event);
        }
    }
}
