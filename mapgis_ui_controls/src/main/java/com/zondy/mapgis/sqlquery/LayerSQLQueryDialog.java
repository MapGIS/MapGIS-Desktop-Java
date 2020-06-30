package com.zondy.mapgis.sqlquery;

import com.zondy.mapgis.base.MessageBox;
import com.zondy.mapgis.controls.LayerSelectComboBoxItem;
import com.zondy.mapgis.controls.LayerSelectControl;
import com.zondy.mapgis.geodatabase.IVectorCls;
import com.zondy.mapgis.geodatabase.SFeatureCls;
import com.zondy.mapgis.map.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * @ClassName LayerSQLQueryDialog
 * @Description: 属性条件输入框(带图层选择)
 * @Author ysp
 * @Date 2020/3/23
 **/
public class LayerSQLQueryDialog extends Alert {
    private SQLQuery sqlQuery;
    private String sqlText;
    private VectorLayer mapLayer;
    public LayerSQLQueryDialog(Map map, String filter) {
        super(AlertType.NONE);

        LayerSelectControl layerSelectControl = new LayerSelectControl(map,filter);
        layerSelectControl.selectFirstItem();
        layerSelectControl.setOnSelectedItemChanged(new ChangeListener<LayerSelectComboBoxItem>() {
            @Override
            public void changed(ObservableValue<? extends LayerSelectComboBoxItem> observable, LayerSelectComboBoxItem oldValue, LayerSelectComboBoxItem newValue) {
                DocumentItem documentItem = newValue.getDocumentItem();
                if (documentItem instanceof VectorLayer) {
                    mapLayer = (VectorLayer) documentItem;
                    sqlQuery.updateData((IVectorCls) mapLayer.getData());
                }
            }
        });
        IVectorCls sfcls = null;
        if (layerSelectControl.getSelectedItem() != null && layerSelectControl.getSelectedItem().getDocumentItem() instanceof VectorLayer){
            mapLayer = (VectorLayer) layerSelectControl.getSelectedItem().getDocumentItem();
            if (mapLayer.getData() instanceof SFeatureCls) {
                sfcls =(IVectorCls)mapLayer.getData();
            }
        }
//        layerSelectControl.setWidth(827);
//        GridPane gridPaneLayer = new GridPane();
//        gridPaneLayer.add(new Label("选择图层:"),0,0);
//        gridPaneLayer.add(layerSelectControl,1,0);
//        gridPaneLayer.setHgap(10);
//        gridPaneLayer.setAlignment(Pos.TOP_LEFT);
        HBox hBox = new HBox();
        hBox.getChildren().addAll(new Label("选择图层:"),layerSelectControl);
        hBox.setSpacing(10);
        HBox.setHgrow(layerSelectControl, Priority.ALWAYS);
        sqlQuery = new SQLQuery(sfcls, "");
        GridPane gridPane = new GridPane();
        gridPane.add(hBox,0,0);
        gridPane.add(sqlQuery,0,1);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.TOP_LEFT);
        this.getDialogPane().setContent(gridPane);
        //        this.setResizable(false);
        this.setTitle("属性条件输入框");
        ButtonType saveButtonType = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        this.getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        final Button buttonOK = (Button) this.getDialogPane().lookupButton(saveButtonType);
        buttonOK.addEventFilter(ActionEvent.ACTION, this::buttonOK_OnAction);
//        setResultConverter(dialogButton -> dialogButton == ButtonType.OK ? this.selSQlText : null);
    }

    public MapLayer getSelectMapLayer(){
        return mapLayer;
    }
    public String getSQLText() {
        return this.sqlText;
    }

    private void buttonOK_OnAction(ActionEvent actionEvent) {
        String msg = sqlQuery.verification();
        if (msg.isEmpty()){
            sqlText = sqlQuery.getSQLText();
        }
        else {
            actionEvent.consume();
            MessageBox.information(msg);
        }
    }
}