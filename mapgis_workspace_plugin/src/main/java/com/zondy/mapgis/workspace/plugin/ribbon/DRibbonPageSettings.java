package com.zondy.mapgis.workspace.plugin.ribbon;

import com.zondy.mapgis.pluginengine.ui.IRibbonPage;
import com.zondy.mapgis.pluginengine.ui.IRibbonPageGroup;

public class DRibbonPageSettings implements IRibbonPage {
    @Override
    public String getCategoryKey() {
        return null;
    }

    @Override
    public String getText() {
        return "设置";
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public IRibbonPageGroup[] getRibbonPageGroups() {
        return new IRibbonPageGroup[0];
    }
}
