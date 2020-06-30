package com.zondy.mapgis.utilities;

import java.util.EventListener;
import java.util.EventObject;

/**
 * @author cxy
 * @date 2019/09/16
 */
public interface MyEventListener extends EventListener {
    void fire(EventObject eventObject);
}
