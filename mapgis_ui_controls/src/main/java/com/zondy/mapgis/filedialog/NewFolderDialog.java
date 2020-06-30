package com.zondy.mapgis.filedialog;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * 新建磁盘文件夹
 */
public class NewFolderDialog extends Dialog<String> {
    private String folderName = "";
    private GridPane gridPane = null;
    private TextField textField = null;

    public NewFolderDialog() {
        initialize();
        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setContent(this.gridPane);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Button btnOk = (Button) dialogPane.lookupButton(ButtonType.OK);
        btnOk.addEventFilter(ActionEvent.ACTION, this::okButtonClick);
        setResultConverter(dialogButton -> dialogButton == ButtonType.OK ? folderName : null);
    }

    private void initialize() {
        gridPane = new GridPane();
        gridPane.setPrefWidth(400);
        Label label = new Label("名称:");
        textField = new TextField();
        gridPane.add(label, 0, 0);
        gridPane.add(textField, 1, 0);
        textField.setPrefWidth(300);
        gridPane.setHgap(10);
        gridPane.setPadding(new Insets(5, 5, 5, 5));
    }

    private void okButtonClick(ActionEvent event) {
        String text = this.textField.getText();
        if (text == null || text.trim().length() == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "名称不能为空!", ButtonType.YES);
            alert.setHeaderText(null);
            alert.show();
            event.consume();
        } else {
            folderName = text;
        }
    }
}
