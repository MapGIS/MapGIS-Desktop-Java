package com.zondy.mapgis.hdfsubdata;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

/**
 * 添加hdf4或5子集图层对话框
 */
public class AddRasHdfSubDataSetDialog extends Dialog {
    //
    public ArrayList<HdfSubDataSetItem> m_SelectItems = null;
    public boolean m_bAskAagin = true;
    //
    private VBox vBox = null;
    private Label label1 = null;
    private Button button_SelectAll = null;
    private Button button_UnSelect = null;
    private TableView<HdfSubDataSetItem> tableView = null;
    private ObservableList<HdfSubDataSetItem> data = null;
    private CheckBox checkBox_AskAgain = null;
    //
    private Button button_Ok = null;
    private Button button_Cancle = null;

    public AddRasHdfSubDataSetDialog(ArrayList<HdfSubDataSetItem> items)
    {
        setTitle("添加hdf4/5子集图层");
        setResizable(false);

        InitDialog();
        m_SelectItems = new ArrayList<>();
        for (HdfSubDataSetItem item : items) {
            tableView.getItems().add(item);
        }
        tableView.refresh();

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setContent(vBox);
        dialogPane.getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);
        dialogPane.setPrefSize(430,330);
        dialogPane.setMinSize(430,330);

        button_Ok = (Button)dialogPane.lookupButton(ButtonType.OK);
        button_Ok.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("提示");
                if (tableView.getSelectionModel().getSelectedItems().size()<=0)
                {
                    alert.headerTextProperty().set("请至少选择一个子集!");
                    alert.showAndWait();
                    return;
                }
                m_bAskAagin = !checkBox_AskAgain.isSelected();
                for (HdfSubDataSetItem item:tableView.getSelectionModel().getSelectedItems())
                {
                    m_SelectItems.add(item);
                }
            }
        });

        button_SelectAll.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tableView.getSelectionModel().selectAll();
            }
        });

        button_UnSelect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tableView.getSelectionModel().clearSelection();
            }
        });

    }

    private void InitDialog()
    {
        vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setStyle("-fx-font-size: 9pt;");

        GridPane gridPane1 = new GridPane();
        gridPane1.setHgap(50);
        gridPane1.setVgap(5);

        label1 = new Label("此文件包含多个子集数据.选择一个子集作为一个栅格图层.选择多个子集作为一个组图层.你必须至少选择一个子集.");
        label1.setWrapText(true);
        gridPane1.add(label1,0,0,3,1);
        button_SelectAll = new Button("全部选定");
        gridPane1.add(button_SelectAll,0,1);
        button_UnSelect = new Button("反向选定");
        gridPane1.add(button_UnSelect,1,1);
        checkBox_AskAgain = new CheckBox("不再询问");
        gridPane1.add(checkBox_AskAgain,2,1);

        data = FXCollections.observableArrayList();
        tableView = new TableView<>(data);
        tableView.setPrefSize(400,200);
        tableView.setMinSize(400,200);
        gridPane1.add(tableView,0,2,3,1);
        TableColumn<HdfSubDataSetItem, Integer> tc_Id = new TableColumn<>("子集ID");
        tc_Id.setPrefWidth(80);
        tc_Id.setMinWidth(80);
        tc_Id.setSortable(false);
        tc_Id.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<HdfSubDataSetItem, String> tc_Description = new TableColumn<>("描述");
        tc_Description.setPrefWidth(330);
        tc_Description.setMinWidth(330);
        tc_Description.setSortable(false);
        tc_Description.setCellValueFactory(new PropertyValueFactory<>("description"));
        tableView.getColumns().addAll(tc_Id,tc_Description);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        vBox.getChildren().addAll(gridPane1);
    }

}
