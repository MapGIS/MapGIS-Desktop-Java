package com.zondy.mapgis.mapeditor.common;

import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.map.EditLayerType;
import com.zondy.mapgis.map.Map;
import com.zondy.mapgis.map.MapLayer;
import com.zondy.mapgis.map.SelectLayerControl;
import com.zondy.mapgis.mapeditor.enums.StateEnum;
import com.zondy.mapgis.pluginengine.IApplication;
import com.zondy.mapgis.pluginengine.plugin.IMapContentsView;
import com.zondy.mapgis.pluginengine.plugin.IPlugin;
import com.zondy.mapgis.workspace.event.StateChangedEvent;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class CommonFunctions {
    public static void setPluginEnableEx(IApplication hk, StateChangedEvent e, ArrayList<Boolean> rtnBools, EnumSet<StateEnum> stateEnum, StateEnum oneStateEnum, EditLayerType layerType, SelectLayerControl layerState) {
        if (stateEnum.contains(oneStateEnum)) {
            boolean thisValue = false;
            if (hk.getActiveContentsView() instanceof IMapContentsView) {
                MapControl mapControl = ((IMapContentsView) hk.getActiveContentsView()).getMapControl();
                if (mapControl != null) {
                    Map activeMap = mapControl.getMap();
                    if (activeMap != null) {
                        List<MapLayer> layers = activeMap.getEditLayer(layerType, layerState);
                        if (layers != null) {
                            if (layers.size() > 0) {
                                thisValue = true;
                            }
                            for (MapLayer maplayer : layers) {
                                maplayer.dispose();
                            }
                        }

                    }
                }
            }
            e.getHashMap().put(oneStateEnum.toString(), thisValue);
            rtnBools.add(thisValue);
        }
    }

    public static boolean setPluginEnable(IApplication hk, IPlugin plugin, StateChangedEvent e, EnumSet<StateEnum> stateEnum, boolean and) {
          if (hk == null || plugin == null || e == null) {
            return false;
        }

        ArrayList<Boolean> rtnBools = new ArrayList<>();

        // 代码片段1
        setPluginEnableEx(hk, e, rtnBools, stateEnum, StateEnum.ANN_ACTIVE, EditLayerType.Ann, SelectLayerControl.Active);
        // 代码片段2
        setPluginEnableEx(hk, e, rtnBools, stateEnum, StateEnum.ANN_EDITABLE, EditLayerType.Ann, SelectLayerControl.Editable);
        // 代码片段3
        setPluginEnableEx(hk, e, rtnBools, stateEnum, StateEnum.ANN_VISIBLE, EditLayerType.Ann, SelectLayerControl.Visible);
        // 代码片段4
        setPluginEnableEx(hk, e, rtnBools, stateEnum, StateEnum.LINE_ACTIVE, EditLayerType.Line, SelectLayerControl.Active);
        // 代码片段5
        setPluginEnableEx(hk, e, rtnBools, stateEnum, StateEnum.LINE_EDITABLE, EditLayerType.Line, SelectLayerControl.Editable);
        // 代码片段6
        setPluginEnableEx(hk, e, rtnBools, stateEnum, StateEnum.LINE_VISIBLE, EditLayerType.Line, SelectLayerControl.Visible);
        // 代码片段7
        setPluginEnableEx(hk, e, rtnBools, stateEnum, StateEnum.PNT_ACTIVE, EditLayerType.Pnt, SelectLayerControl.Active);
        // 代码片段8
        setPluginEnableEx(hk, e, rtnBools, stateEnum, StateEnum.PNT_EDITABLE, EditLayerType.Pnt, SelectLayerControl.Editable);
        // 代码片段9
        setPluginEnableEx(hk, e, rtnBools, stateEnum, StateEnum.PNT_VISIBLE, EditLayerType.Pnt, SelectLayerControl.Visible);
        // 代码片段10
        setPluginEnableEx(hk, e, rtnBools, stateEnum, StateEnum.POLYGON_ACTIVE, EditLayerType.Reg, SelectLayerControl.Active);
        // 代码片段11
        setPluginEnableEx(hk, e, rtnBools, stateEnum, StateEnum.POLYGON_EDITABLE, EditLayerType.Reg, SelectLayerControl.Editable);
        // 代码片段12
        setPluginEnableEx(hk, e, rtnBools, stateEnum, StateEnum.POLYGON_VISIBLE, EditLayerType.Reg, SelectLayerControl.Visible);
        // 代码片段13
        setPluginEnableEx(hk, e, rtnBools, stateEnum, StateEnum.RASTERDATASET_ACTIVE, EditLayerType.RasterDataset, SelectLayerControl.Active);
        // 代码片段14
        setPluginEnableEx(hk, e, rtnBools, stateEnum, StateEnum.RASTERDATASET_EDITABLE, EditLayerType.RasterDataset, SelectLayerControl.Editable);
        // 代码片段15
        setPluginEnableEx(hk, e, rtnBools, stateEnum, StateEnum.RASTERDATASET_VISIBLE, EditLayerType.RasterDataset, SelectLayerControl.Visible);
        // 代码片段16
        setPluginEnableEx(hk, e, rtnBools, stateEnum, StateEnum.RASTERCATALOG_ACTIVE, EditLayerType.RasterCat, SelectLayerControl.Active);
        // 代码片段17
        setPluginEnableEx(hk, e, rtnBools, stateEnum, StateEnum.RASTERCATALOG_EDITABLE, EditLayerType.RasterCat, SelectLayerControl.Editable);
        // 代码片段18
        setPluginEnableEx(hk, e, rtnBools, stateEnum, StateEnum.RASTERCATALOG_VISIBLE, EditLayerType.RasterCat, SelectLayerControl.Visible);
        // 代码片段19
        setPluginEnableEx(hk, e, rtnBools, stateEnum, StateEnum.S57DATASET_ACTIVE, EditLayerType.S57DATASET, SelectLayerControl.Active);
        // 代码片段20
        setPluginEnableEx(hk, e, rtnBools, stateEnum, StateEnum.S57DATASET_EDITABLE, EditLayerType.S57DATASET, SelectLayerControl.Editable);
        // 代码片段21
        setPluginEnableEx(hk, e, rtnBools, stateEnum, StateEnum.S57DATASET_VISIBLE, EditLayerType.S57DATASET, SelectLayerControl.Visible);
        // 代码片段22
        setPluginEnableEx(hk, e, rtnBools, stateEnum, StateEnum.S57OBJ_ACTIVE, EditLayerType.S57OBJDATASET, SelectLayerControl.Active);
        // 代码片段23
        setPluginEnableEx(hk, e, rtnBools, stateEnum, StateEnum.S57OBJ_EDITABLE, EditLayerType.S57OBJDATASET, SelectLayerControl.Editable);
        // 代码片段24
        setPluginEnableEx(hk, e, rtnBools, stateEnum, StateEnum.S57OBJ_VISIBLE, EditLayerType.S57OBJDATASET, SelectLayerControl.Visible);


        if (and) {
            boolean enableValue = true;
            for (boolean rtnBool : rtnBools) {
                if (!rtnBool) {
                    enableValue = false;
                    break;
                }
            }
            hk.getPluginContainer().setPluginEnable(plugin, enableValue);;
            return enableValue;
        } else {
            boolean enableValue = false;
            for (boolean rtnBool : rtnBools) {
                if (rtnBool) {
                    enableValue = true;
                    break;
                }
            }
            hk.getPluginContainer().setPluginEnable(plugin, enableValue);
            return enableValue;
        }
    }
}
