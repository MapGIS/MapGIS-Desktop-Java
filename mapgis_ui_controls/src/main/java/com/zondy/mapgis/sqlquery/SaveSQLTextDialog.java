package com.zondy.mapgis.sqlquery;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/**
 * @ClassName SaveSQLTextDialog
 * @Description: 保存SQL数据对话框
 * @Author ysp
 * @Date 2020/3/22
 **/
public class SaveSQLTextDialog extends Alert {

    private TextField textField;
    private String dscrib;
    public SaveSQLTextDialog() {
        super(AlertType.NONE);

        GridPane gridPane = new GridPane();
        Label label = new Label("描述语句:");
        label.setMinWidth(80);
        textField = new TextField();
        textField.setPrefWidth(300);
        gridPane.add(new Region(),0,0);
        gridPane.add(label,1,0);
        gridPane.add(textField,2,0);
        gridPane.add(new Region(),3,0);
        gridPane.setHgap(10);
        this.setResizable(true);
        this.setTitle("保存SQL语句");
        this.getDialogPane().setContent(gridPane);
        ButtonType saveButtonType = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        this.getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        final Button buttonOK = (Button) this.getDialogPane().lookupButton(saveButtonType);
        buttonOK.addEventFilter(ActionEvent.ACTION, this::buttonOK_OnAction);
    }
    private void buttonOK_OnAction(ActionEvent actionEvent) {
        dscrib = textField.getText();
    }

    public String getDscrib() {
        return dscrib;
    }
}
