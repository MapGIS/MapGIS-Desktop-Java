package com.zondy.mapgis.controls;

import java.util.EventObject;

/**
 * Created by Administrator on 2020/4/21.
 */
public final class AttributionTextChangedEvent
        extends EventObject
{
    private static final long serialVersionUID = 1L;
    private final SceneControl mGeoView;

    public AttributionTextChangedEvent(SceneControl source) {
        super(source);

        this.mGeoView = source;
    }


    public String getAttributionText() { return this.mGeoView.getAttributionText(); }




    public SceneControl getSource() { return this.mGeoView; }
}