package com.zondy.mapgis.mapeditor.dialogs;

import com.zondy.mapgis.analysis.spatialanalysis.TopologyErrorManager;
import com.zondy.mapgis.geodatabase.SFeatureCls;
import com.zondy.mapgis.pluginengine.IApplication;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

public class TopoErrorResultsDialog extends Pane {
    private double tolerane;

    public TopoErrorResultsDialog(IApplication app){

    }

    public double getTolerane() {
        return tolerane;
    }

    public void setTolerane(double tolerane) {
        this.tolerane = tolerane;
    }

    /**
     * 列表规则错误
     *
     * @param topologyErrorManager 拓扑错误管理
     * @param sfclsList 规则错误集合
     */
    public void listTopoErrors(TopologyErrorManager topologyErrorManager, ArrayList<SFeatureCls> sfclsList)
    {
//        this.dataTable.Rows.Clear();
//        this.strTopoTypeList.Clear();
//        this.repositoryItemComboBox1.Items.Clear();
//        this.barEditItem_RuleType.EditValue = null;
//        this.barStaticItem_ErrorCount.Caption = string.Format("{0}", "");
//        if (this.errorInfoList != null)
//            this.errorInfoList.Clear();
//        else
//            this.errorInfoList = new List<CustomTopoErrorInfo>();
//        if (this.undoInfos != null)
//            this.undoInfos.Clear();
//        else
//            this.undoInfos = new Stack();
//        if (this.redoInfos != null)
//            this.redoInfos.Clear();
//        else
//            this.redoInfos = new Stack();
//        this.sptem = spTopologyErrorManager;
//        this.sfclsList = sfclsList;
//        this.isImport = false;
//        this.InitGridView();
//        this.CloseCurCls();
//        this.repositoryItemComboBox1.Items.Add(Resources.String_ErrorInAllChecks);
//        this.repositoryItemComboBox1.Items.AddRange(strTopoTypeList.ToArray());
//        this.barEditItem_RuleType.EditValue = string.Format("{0}", this.repositoryItemComboBox1.Items[0].ToString());
//        if (this.dataTable.Rows.Count != 0)
//            this.AddLayerInMap();
//        SetFocus(this.Handle);
    }

}
