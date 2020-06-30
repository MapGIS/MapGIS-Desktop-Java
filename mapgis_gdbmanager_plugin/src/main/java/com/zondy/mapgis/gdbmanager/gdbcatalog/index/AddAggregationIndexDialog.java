package com.zondy.mapgis.gdbmanager.gdbcatalog.index;

import com.zondy.mapgis.att.Field;
import com.zondy.mapgis.att.Fields;
import com.zondy.mapgis.base.LanguageConvert;
import com.zondy.mapgis.base.MessageBox;
import com.zondy.mapgis.geodatabase.AggregationIndex;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建聚集索引
 *
 * @author CR
 * @file AddAggregationIndexDialog.java
 * @brief 创建聚集索引
 * @create 2020-02-27.
 */
public class AddAggregationIndexDialog extends Dialog
{
    private AggregationIndex aggIndex = new AggregationIndex();//聚集索引对象
    private final TableView<FieldAggregation> tableView = new TableView<>();

    /**
     * 创建聚集索引
     *
     * @param flds 属性结构
     */
    public AddAggregationIndexDialog(Fields flds)
    {
        this.setTitle("创建聚集索引");

        TableColumn<FieldAggregation, String> tcName = new TableColumn<>("字段名称");
        TableColumn<FieldAggregation, Field.FieldType> tcType = new TableColumn<>("字段类型");
        TableColumn<FieldAggregation, Boolean> tcIsAgg = new TableColumn<>("是否聚集");
        this.tableView.getColumns().addAll(tcName, tcType, tcIsAgg);
        this.tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.tableView.setEditable(true);
        tcName.setEditable(false);
        tcType.setEditable(false);

        tcName.setCellValueFactory(new PropertyValueFactory<>("fldName"));
        tcType.setCellValueFactory(new PropertyValueFactory<>("fldType"));
        tcIsAgg.setCellValueFactory(new PropertyValueFactory<>("agg"));

        tcType.setCellFactory(param -> new TableCell<FieldAggregation, Field.FieldType>()
        {
            @Override
            protected void updateItem(Field.FieldType item, boolean empty)
            {
                super.updateItem(item, empty);
                String text = null;
                if (!empty)
                {
                    text = LanguageConvert.fieldTypeConvert(item);
                }
                setText(text);
            }
        });

        tcIsAgg.setCellFactory(param -> new TableCell<FieldAggregation, Boolean>()
        {
            @Override
            protected void updateItem(Boolean item, boolean empty)
            {
                super.updateItem(item, empty);
                if (!empty && item != null)
                {
                    CheckBox checkBox = new CheckBox();
                    checkBox.setSelected(item);
                    checkBox.selectedProperty().addListener((o, ov, nv) ->
                    {
                        ((FieldAggregation) getTableRow().getItem()).setAgg(nv);
                    });
                    this.setGraphic(checkBox);
                    this.setAlignment(Pos.CENTER);
                }
            }
        });

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setPrefSize(520, 400);
        dialogPane.setMinSize(520, 400);
        dialogPane.setContent(new VBox(this.tableView));
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, this::okButtonClick);
        this.setResizable(true);

        if (flds != null)
        {
            for (short i = 0; i < flds.getFieldCount(); i++)
            {
                Field fld = flds.getField(i);
                if (fld != null)
                {
                    Field.FieldType fType = fld.getFieldType();
                    if (Field.FieldType.fldByte.equals(fType) || Field.FieldType.fldBool.equals(fType) || Field.FieldType.fldShort.equals(fType) || Field.FieldType.fldLong.equals(fType)
                            || Field.FieldType.fldInt64.equals(fType) || Field.FieldType.fldFloat.equals(fType) || Field.FieldType.fldDouble.equals(fType)
                            || Field.FieldType.fldDate.equals(fType) || Field.FieldType.fldTime.equals(fType) || Field.FieldType.fldTimeStamp.equals(fType))
                    {
                        this.tableView.getItems().add(new FieldAggregation(fld.getFieldName(), i, fType, false));
                    }
                }
            }
        }
    }

    // 确定：创建聚集索引
    private void okButtonClick(ActionEvent event)
    {
        List<Integer> list = new ArrayList<>();
        for (FieldAggregation fa : this.tableView.getItems())
        {
            if (fa.isAgg())
            {
                list.add(fa.getFldIndex());
                System.out.println(fa.getFldName());
            }
        }
        if (list.size() == 0)
        {
            MessageBox.information("请勾选聚集字段。");
            event.consume();
        } else
        {
            this.aggIndex.setFieldIDs(list.stream().mapToInt(Integer::valueOf).toArray());
        }
    }

    /**
     * 获取创建的聚集索引
     *
     * @return 创建的聚集索引
     */
    public AggregationIndex getAggIndex()
    {
        return this.aggIndex;
    }


    public class FieldAggregation
    {
        private String fldName;
        private Field.FieldType fldType;
        private SimpleBooleanProperty agg = new SimpleBooleanProperty();
        private int fldIndex;

        public FieldAggregation(String name, int index, Field.FieldType type, boolean isAgg)
        {
            this.fldName = name;
            this.fldIndex = index;
            this.fldType = type;
            this.agg.set(isAgg);
        }

        public String getFldName()
        {
            return fldName;
        }

        public void setFldName(String fldName)
        {
            this.fldName = fldName;
        }

        public Field.FieldType getFldType()
        {
            return fldType;
        }

        public void setFldType(Field.FieldType fldType)
        {
            this.fldType = fldType;
        }

        public boolean isAgg()
        {
            return agg.get();
        }

        public void setAgg(boolean isAgg)
        {
            this.agg.set(isAgg);
        }

        public SimpleBooleanProperty getAggProperty()
        {
            return this.agg;
        }

        public int getFldIndex()
        {
            return fldIndex;
        }

        public void setFldIndex(int fldIndex)
        {
            this.fldIndex = fldIndex;
        }
    }
}
