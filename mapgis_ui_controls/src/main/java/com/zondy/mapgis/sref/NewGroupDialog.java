package com.zondy.mapgis.sref;

import com.zondy.mapgis.base.MessageBox;
import com.zondy.mapgis.base.XString;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Created by Administrator on 2020/2/24.
 */
public class NewGroupDialog extends Dialog
{
    private final TextField textFieldName = new TextField();

    public NewGroupDialog()
    {
        this.setTitle("新建组");

        HBox hBox = new HBox(6, new Label("名称:"), this.textFieldName);
        hBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(this.textFieldName, Priority.ALWAYS);

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setContent(hBox);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Button button = (Button) dialogPane.lookupButton(ButtonType.OK);
        button.addEventFilter(ActionEvent.ACTION, event ->
        {
            String name = this.textFieldName.getText();
            if (XString.isNullOrEmpty(name))
            {
                MessageBox.information("名称不能为空。");
                event.consume();
            }
        });
    }

    public String getGroupName()
    {
        return this.textFieldName.getText();
    }
}
