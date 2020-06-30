package com.zondy.mapgis.sqlquery;

import com.zondy.mapgis.base.MessageBox;
import com.zondy.mapgis.geodatabase.IVectorCls;
import com.zondy.mapgis.geodatabase.SFeatureCls;
import javafx.event.ActionEvent;
import javafx.scene.control.*;

/**
 * @Description: SQL查询条件输入面框(不带图层选择)
 * @Author ysp
 * @Date 2020/3/19
 **/
public class SQLQueryDialog extends Alert {
    private SQLQuery sqlQuery;
    private String sqlText;

    public SQLQueryDialog(IVectorCls vectorCls, String filter) {
        super(AlertType.NONE);

        this.setResizable(false);
        this.setTitle("属性条件输入框");
        sqlQuery = new SQLQuery(vectorCls, filter);
        this.getDialogPane().setContent(sqlQuery);
        ButtonType saveButtonType = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        this.getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        final Button buttonOK = (Button) this.getDialogPane().lookupButton(saveButtonType);
        buttonOK.addEventFilter(ActionEvent.ACTION, this::buttonOK_OnAction);
//        setResultConverter(dialogButton -> dialogButton == ButtonType.OK ? this.selSQlText : null);
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
