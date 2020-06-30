package com.zondy.mapgis.gdbmanager.gdbcatalog.index;

import com.zondy.mapgis.att.Field;
import com.zondy.mapgis.att.Fields;
import com.zondy.mapgis.base.*;
import com.zondy.mapgis.geodatabase.AttIndexType;
import com.zondy.mapgis.geodatabase.DataBase;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建属性索引
 *
 * @author CR
 * @file AddAttIndexDialog.java
 * @brief 创建属性索引
 * @create 2020-02-27.
 */
public class AddAttIndexDialog extends Dialog
{
    private final TextField textFieldName = new TextField();
    private final RadioButton radioButtonGeneral = new RadioButton("常规索引");
    private final RadioButton radioButtonUnique = new RadioButton("唯一索引");
    private final TableView<FieldAtt> tableView = new TableView<>();
    private DataBase db;//当前数据所在数据库
    private String indexName = "";//新创建的属性索引的名称
    private AttIndexType attIndexType;//新创建的属性索引的索引类型
    private String[] indexKeys;//新创建的属性索引使用的字段名称集合
    private Tooltip tooltipError = new Tooltip();

    public AddAttIndexDialog()
    {
        this(null, null);
    }

    public AddAttIndexDialog(DataBase db, Fields flds)
    {
        this.setTitle("创建属性索引");
        this.db = db;

        //region 索引名称、类型
        //索引名称不能包括特殊字符，且其长度不能超过限制
        this.textFieldName.textProperty().addListener((o, ov, nv) -> {
            List<Character> invalidCharList = GISDefaultValues.getInvalidNameCharList();
            StringProperty errorMsg = new SimpleStringProperty();
            if (!XString.isTextValid(nv, 30, invalidCharList, false, errorMsg))
            {
                UIFunctions.showErrorTip(this.textFieldName, errorMsg.get(), this.tooltipError);
                this.textFieldName.setText(ov);
            }
        });
        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(this.radioButtonGeneral, this.radioButtonUnique);
        this.radioButtonGeneral.setSelected(true);
        //endregion

        //region 移动字段顺序
        Button buttonUp = new Button("上移", new ImageView("/up.png"));
        Button buttonDown = new Button("下移", new ImageView("/down.png"));
        buttonUp.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        buttonDown.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        buttonUp.setMaxWidth(22);
        buttonUp.setMinWidth(22);
        buttonDown.setMaxWidth(22);
        buttonDown.setMinWidth(22);
        buttonUp.setOnAction(event -> {
            FieldAtt fieldAtt = this.tableView.getSelectionModel().getSelectedItem();
            if (fieldAtt != null) {
                int index = this.tableView.getItems().indexOf(fieldAtt);
                FieldAtt lastOne = this.tableView.getItems().get(index - 1);
                this.tableView.getItems().set(index - 1, fieldAtt);
                this.tableView.getItems().set(index, lastOne);
            }
        });
        buttonDown.setOnAction(event -> {
            FieldAtt fieldAtt = this.tableView.getSelectionModel().getSelectedItem();
            if (fieldAtt != null) {
                int index = this.tableView.getItems().indexOf(fieldAtt);
                FieldAtt nextOne = this.tableView.getItems().get(index + 1);
                this.tableView.getItems().set(index + 1, fieldAtt);
                this.tableView.getItems().set(index, nextOne);
            }
        });
        //endregion

        //region 字段表
        TableColumn<FieldAtt, Boolean> tcIsIndex = new TableColumn<>("");
        TableColumn<FieldAtt, String> tcName = new TableColumn<>("字段名称");
        TableColumn<FieldAtt, Field.FieldType> tcType = new TableColumn<>("字段类型");
        this.tableView.getColumns().addAll(tcIsIndex, tcName, tcType);
        this.tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.tableView.setEditable(true);
        tcName.setEditable(false);
        tcType.setEditable(false);
        tcIsIndex.setPrefWidth(30);
        tcIsIndex.setResizable(false);

        tcIsIndex.setCellValueFactory(new PropertyValueFactory<>("index"));
        tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcType.setCellValueFactory(new PropertyValueFactory<>("type"));

        tcIsIndex.setCellFactory(param -> new TableCell<FieldAtt, Boolean>()
        {
            @Override
            protected void updateItem(Boolean item, boolean empty)
            {
                super.updateItem(item, empty);
                if (!empty && item != null) {
                    CheckBox checkBox = new CheckBox();
                    checkBox.setSelected(item);
                    checkBox.selectedProperty().addListener((o, ov, nv) ->
                    {
                        ((FieldAtt) getTableRow().getItem()).setIndex(nv);
                    });
                    this.setGraphic(checkBox);
                    this.setAlignment(Pos.CENTER);
                }
            }
        });
        tcType.setCellFactory(param -> new TableCell<FieldAtt, Field.FieldType>()
        {
            @Override
            protected void updateItem(Field.FieldType item, boolean empty)
            {
                super.updateItem(item, empty);
                String text = null;
                if (!empty) {
                    text = LanguageConvert.fieldTypeConvert(item);
                }
                setText(text);
            }
        });

        // 第一行不能上移；最后一行不能下移
        this.tableView.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            FieldAtt fieldAtt = this.tableView.getSelectionModel().getSelectedItem();
            buttonUp.setDisable(fieldAtt == null || this.tableView.getItems().indexOf(fieldAtt) == 0);
            buttonDown.setDisable(fieldAtt == null || this.tableView.getItems().indexOf(fieldAtt) == this.tableView.getItems().size() - 1);
        });

        //endregion

        //region 界面布局
        GridPane gridPane = new GridPane();
        gridPane.setVgap(6);
        gridPane.setHgap(6);
        gridPane.add(new Label("名称:"), 0, 0);
        gridPane.add(new Label("类型:"), 0, 1);
        gridPane.add(this.textFieldName, 1, 0);
        gridPane.add(new HBox(30, this.radioButtonGeneral, this.radioButtonUnique), 1, 1);

        gridPane.add(new HBox(6, this.tableView, new VBox(6, buttonUp, buttonDown)), 1, 2);
        GridPane.setHgrow(this.textFieldName, Priority.ALWAYS);
        HBox.setHgrow(this.tableView, Priority.ALWAYS);
        gridPane.setAlignment(Pos.TOP_LEFT);

        VBox vBoxMove = new VBox(6, buttonUp, buttonDown);
        vBoxMove.setPadding(new Insets(24, 0, 0, 0));
        VBox vBox = new VBox(6, gridPane, new HBox(6, new Label("字段:"), this.tableView, vBoxMove));

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setPrefSize(400, 300);
        dialogPane.setMinSize(400, 300);
        dialogPane.setContent(vBox);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, this::okButtonClick);
        this.setResizable(true);
        //endregion

        //region 初始化字段信息
        if (flds != null) {
            for (short i = 0; i < flds.getFieldCount(); i++) {
                Field fld = flds.getField(i);
                if (fld != null) {
                    this.tableView.getItems().add(new FieldAtt(false, fld.getFieldName(), fld.getFieldType()));
                }
            }
        }
        if (this.tableView.getItems().size() > 0) {
            this.tableView.getSelectionModel().select(0);
        }
        //endregion

        this.setOnShown(event -> {
            this.textFieldName.requestFocus();
        });
    }

    /**
     * 读取属性索引名称
     *
     * @return 属性索引名称
     */
    public String getIndexName()
    {
        return this.indexName;
    }

    /**
     * 读取勾选的字段名称集合
     *
     * @return 勾选的字段名称集合
     */
    public String[] getIndexKeys()
    {
        return this.indexKeys;
    }

    /**
     * 读取索引类型
     *
     * @return 索引类型
     */
    public AttIndexType getAttIndexType()
    {
        return this.attIndexType;
    }

    private void okButtonClick(ActionEvent event)
    {
        this.indexName = this.textFieldName.getText();
        if (XString.isNullOrEmpty(this.indexName)) {
            MessageBox.information("索引名称不能为空。");
            event.consume();
        } else {
            List<String> fldNames = new ArrayList<>();
            for (FieldAtt fieldAtt : this.tableView.getItems()) {
                if (fieldAtt.isIndex()) {
                    fldNames.add(fieldAtt.getName());
                }
            }

            if (fldNames.size() == 0) {
                MessageBox.information("至少需选择一个索引字段。");
                event.consume();
            } else {
                this.attIndexType = this.radioButtonGeneral.isSelected() ? AttIndexType.Normal : AttIndexType.Unique;
                this.indexKeys = fldNames.toArray(new String[0]);
            }
        }
    }

    public class FieldAtt
    {
        private boolean index;
        private String name;
        private Field.FieldType type;

        public FieldAtt(boolean index, String name, Field.FieldType type)
        {
            this.index = index;
            this.name = name;
            this.type = type;
        }

        public boolean isIndex()
        {
            return index;
        }

        public void setIndex(boolean index)
        {
            this.index = index;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public Field.FieldType getType()
        {
            return type;
        }

        public void setType(Field.FieldType type)
        {
            this.type = type;
        }
    }
}
