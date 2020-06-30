package com.zondy.mapgis.sqlquery;

import com.zondy.mapgis.att.*;
import com.zondy.mapgis.base.LanguageConvert;
import com.zondy.mapgis.base.MessageBox;
import com.zondy.mapgis.fields.FieldFunctions;
import com.zondy.mapgis.geodatabase.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.*;

/**
 * @ClassName SQLQuery
 * @Description: SQL查询条件输入面板
 * @Author ysp
 * @Date 2020/3/19
 **/
public class SQLQuery extends GridPane {

    private ObservableList<FieldInfo> fieldInfos;
    private ObservableList<FieldVal> fieldVals;
    private TableView<FieldInfo> tableViewFieldInfo;
    private TableView<FieldVal> tableViewFieldVal;
    private TitledPane titledPaneSQLExpress;
    private TextArea textArea;
    //    private SFeatureCls sfcls;
    private IVectorCls vCls;
    private Fields fields;
    private String filter;
    private RecordSet m_RcdSet = null;//通过记录集直接显示属性记录
    private int progressLimit = 5000; //显示进度条的界限。类记录数大于此值时显示进度条
    private int showLimit = 1000;     //自动显示界限。类记录数小于此值时直接显示，大于此值时则需要用户点击“获取属性值”后显示
    private int maxShow = 2000;     //自动显示界限。类记录数小于此值时直接显示，大于此值时则需要用户点击“获取属性值”后显示

    private static String sqlrecords = "";//临时记录

    public SQLQuery(IVectorCls vCls, String filter) {
        this.vCls = vCls;
        this.filter = filter;
        fieldInfos = FXCollections.observableArrayList();
        fieldVals = FXCollections.observableArrayList();
        this.initUI();
        this.initFldInfo();
    }

    public String getSQLText() {
        return this.textArea.getText();
    }

    public void updateData(IVectorCls vCls) {
        this.textArea.clear();
        this.vCls = vCls;
        this.initFldInfo();
    }

    public String verification() {
        String msg = "";
        if (this.fields != null) {
            SQLClauseParser parser = new SQLClauseParser(this.textArea.getText(), this.fields);
            if (parser.check() <= 0)
                msg = parser.getErrorDesc();
        }
        return msg;
    }

    /**
     * 初始化界面
     */
    private void initUI() {

        //region 字段与属性信息
        TitledPane titledPaneFld = new TitledPane();
        titledPaneFld.setText("字段与属性信息");


        //region 字段
        tableViewFieldInfo = new TableView<>();
        // 每个Table的列
        TableColumn nameCol = new TableColumn("字段名称");
        nameCol.setMinWidth(160);
        nameCol.setCellValueFactory(new PropertyValueFactory<FieldInfo, String>("name"));
        TableColumn typeCol = new TableColumn("类型");
        typeCol.setMinWidth(100);
        typeCol.setCellValueFactory(new PropertyValueFactory<FieldInfo, Double>("type"));
        TableColumn asiaCol = new TableColumn("别名");
        asiaCol.setMinWidth(100);
        asiaCol.setCellValueFactory(new PropertyValueFactory<FieldInfo, Double>("asiaName"));
        tableViewFieldInfo.getColumns().addAll(nameCol, typeCol, asiaCol);
        tableViewFieldInfo.setMinWidth(360);
        tableViewFieldInfo.setMinHeight(300);
        tableViewFieldInfo.setPrefWidth(380);
        tableViewFieldInfo.setPrefHeight(320);
        tableViewFieldInfo.setRowFactory(param -> {
            TableRow<FieldInfo> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 & (!row.isEmpty())) {
                    FieldInfo fieldInfo = row.getItem();
                    sqlEdit(fieldInfo.getName());
                }
            });
            return row;
        });
        tableViewFieldInfo.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                FieldInfo fieldInfo = fieldInfos.get(newValue.intValue());
                if (fieldInfo != null && m_RcdSet != null && vCls.getObjCount() < showLimit)
                    getSingleValueFromRecordSet(fieldInfo.getField());
                else if (fieldInfo != null && vCls != null && vCls.getObjCount() < showLimit)
                    getAttributeValues(fieldInfo.getField());
            }
        });
        //endregion

        //region 属性
        tableViewFieldVal = new TableView<>();
        tableViewFieldVal.setPrefHeight(160);
        // 每个Table的列
        TableColumn valCol = new TableColumn("属性值");
        valCol.setMinWidth(160);
        valCol.setCellValueFactory(new PropertyValueFactory<FieldVal, String>("dspVal"));
        tableViewFieldVal.getColumns().addAll(valCol);
        tableViewFieldVal.setMinWidth(160);
        tableViewFieldVal.setMinHeight(270);
        tableViewFieldVal.setPrefWidth(180);
        tableViewFieldVal.setPrefHeight(290);
        tableViewFieldVal.setRowFactory(param -> {
            TableRow<FieldVal> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 & (!row.isEmpty())) {
                    FieldVal fieldVal = row.getItem();
                    sqlEdit(fieldVal.getVal());
                }
            });
            return row;
        });
        Button button = new Button("获取属性值");
        button.setOnAction(event -> {
            this.fieldVals.clear();
            FieldInfo fieldInfo = tableViewFieldInfo.getSelectionModel().getSelectedItem();
            if (fieldInfo != null) {
                if (this.m_RcdSet != null)
                    this.getSingleValueFromRecordSet(fieldInfo.getField());
                else
                    this.getAttributeValues(fieldInfo.getField());
            } else {
                tableViewFieldVal.setItems(this.fieldVals);
            }
        });
        //endregion

        //region 属性字段和属性值布局
        VBox vBox = new VBox();
        vBox.getChildren().addAll(tableViewFieldVal, button);
        vBox.setSpacing(5);
        vBox.setAlignment(Pos.TOP_LEFT);
        vBox.setFillWidth(true);

//        HBox hBoxFld = new HBox();
//        hBoxFld.getChildren().addAll(tableViewFieldInfo,vBox);
//        hBoxFld.setSpacing(5);
//        hBoxFld.setAlignment(Pos.TOP_LEFT);
//        hBoxFld.setFillHeight(true);
        GridPane gridPaneFld = new GridPane();
        gridPaneFld.add(tableViewFieldInfo, 0, 0);
        gridPaneFld.add(vBox, 1, 0);
        gridPaneFld.setHgap(10);
        gridPaneFld.setVgap(10);
        gridPaneFld.setPrefHeight(320);
        gridPaneFld.setAlignment(Pos.TOP_LEFT);
        //endregion

        titledPaneFld.setContent(gridPaneFld);
        titledPaneFld.setExpanded(true);
        titledPaneFld.setCollapsible(false);
        //endregion

        //region 运算符与函数
        TitledPane titledPaneOperSym = new TitledPane();
        titledPaneOperSym.setText("运算符与函数");
        VBox vBoxOperSym = new VBox();
        Button button1 = new Button("+");
        Button button2 = new Button("-");
        Button button3 = new Button("*");
        Button button4 = new Button("/");
        Button button5 = new Button("()");
        Button button6 = new Button("%");
        Button button7 = new Button("=");
        Button button8 = new Button("!=");
        Button button9 = new Button(">");
        Button button10 = new Button("<");
        Button button11 = new Button(">=");
        Button button12 = new Button("LIKE");
        Button button13 = new Button("AND");
        Button button14 = new Button("OR");
        Button button15 = new Button("IS");
        Button button16 = new Button("NULL");
        GridPane gridPaneOperSyms = new GridPane();
        gridPaneOperSyms.add(button1, 0, 0);
        gridPaneOperSyms.add(button2, 1, 0);
        gridPaneOperSyms.add(button3, 2, 0);
        gridPaneOperSyms.add(button4, 3, 0);
        gridPaneOperSyms.add(button5, 0, 1);
        gridPaneOperSyms.add(button6, 1, 1);
        gridPaneOperSyms.add(button7, 2, 1);
        gridPaneOperSyms.add(button8, 3, 1);
        gridPaneOperSyms.add(button9, 0, 2);
        gridPaneOperSyms.add(button10, 1, 2);
        gridPaneOperSyms.add(button11, 2, 2);
        gridPaneOperSyms.add(button12, 3, 2);
        gridPaneOperSyms.add(button13, 0, 3);
        gridPaneOperSyms.add(button14, 1, 3);
        gridPaneOperSyms.add(button15, 2, 3);
        gridPaneOperSyms.add(button16, 3, 3);
        gridPaneOperSyms.setVgap(10);
        gridPaneOperSyms.setHgap(10);
        gridPaneOperSyms.setAlignment(Pos.TOP_LEFT);
//        TilePane tilePaneOperSym = new TilePane(
//                button1,
//                button2,
//                button3,
//                button4,
//                button5,
//                button6,
//                button7,
//                button8,
//                button9,
//                button10,
//                button11,
//                button12,
//                button13,
//                button14,
//                button15,
//                button16
//        );
//        tilePaneOperSym.setVgap(10);
//        tilePaneOperSym.setHgap(10);
//        tilePaneOperSym.setAlignment(Pos.TOP_LEFT);
//        tilePaneOperSym.setOrientation(Orientation.HORIZONTAL);
        List<String> syms1 = Arrays.asList("+", "-", "*", "/", "()", "%", ">", "<", ">=", "<=");
        List<String> syms2 = Arrays.asList("=", "!=", "LIKE");
        List<String> syms3 = Arrays.asList("AND", "OR", "NOT", "IS", "NULL");
        for (Node node : gridPaneOperSyms.getChildren()) {
            if (node instanceof Button) {
                ((Button) node).setMinWidth(60);
                ((Button) node).setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {

                        String txt = ((Button) node).getText();
                        if (syms1.contains(txt)) {
                            sqlEdit(txt);
                        } else if (syms2.contains(txt)) {
                            String preSqlEpress = textArea.getText().substring(0, textArea.getCaretPosition());
                            String[] texts = preSqlEpress.split(" ");
                            boolean findFlag = false;
                            for (int i = texts.length - 1; i >= 0; i--) {
                                if (!texts[i].isEmpty() && fields != null) {
                                    for (short j = 0; j < fields.getFieldCount(); j++) {
                                        Field fld = fields.getField(j);
                                        if (fld.getFieldName().equals(texts[i]) && fld.getFieldType().equals(Field.FieldType.fldStr)) {
                                            findFlag = true;
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
//                            String operText = (sender as SimpleButton).Text;
                            if (findFlag) {
                                if (txt == "LIKE") {
                                    if (!preSqlEpress.isEmpty()) {
                                        sqlEdit(" " + txt + " ''");
                                    } else {
                                        sqlEdit(txt + " ''");
                                    }
                                } else {
                                    sqlEdit(txt + "''");
                                }
                            } else {
                                if (txt == "LIKE") {
                                    if (!preSqlEpress.isEmpty()) {
                                        sqlEdit(" " + txt + " ");
                                    } else {
                                        sqlEdit(txt + " ");
                                    }
                                } else {
                                    sqlEdit(txt);
                                }
                            }

                        } else if (syms3.contains(txt)) {
                            if (!textArea.getText().isEmpty()) {
                                sqlEdit(" " + txt + " ");
                            } else {
                                sqlEdit(txt + " ");
                            }
                        }

                    }
                });
            }
        }


        ComboBox<String> comboBoxFuns = new ComboBox();
        ObservableList<String> funs = FXCollections.observableArrayList();
        funs.addAll("------数学函数------", "Abs()", "Max()", "Min()", "Round()", "------字符串函数------",
                "Lower()", "Upper()", "Substr()");
        comboBoxFuns.setItems(funs);
        comboBoxFuns.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.isEmpty()) {
                    if (!newValue.startsWith("--")) {
                        sqlEdit(newValue);
                    }
                }
            }
        });

        vBoxOperSym.getChildren().addAll(gridPaneOperSyms,
                new Label("选择函数:"),
                comboBoxFuns,
                new Region());
        vBoxOperSym.setSpacing(10);
        vBoxOperSym.setAlignment(Pos.TOP_LEFT);
//        vBoxOperSym.setFillWidth(true);
//        titledPaneOperSym.setMinWidth(280);

        titledPaneOperSym.setContent(vBoxOperSym);
        titledPaneOperSym.setExpanded(true);
        titledPaneOperSym.setCollapsible(false);

        //endregion

        //region 条件语句
        titledPaneSQLExpress = new TitledPane();
        String title = "条件语句";
        if (vCls != null) {
            title = "SELECT * FROM " + vCls.getName() + " WHERE:";
        }
        titledPaneSQLExpress.setText(title);

        textArea = new TextArea();
        textArea.setPrefHeight(80);
        textArea.setMinHeight(60);
        Button buttonClearAll = new Button("清空");
        buttonClearAll.setOnAction(event -> {
            textArea.clear();
        });
        Button buttonClearLoad = new Button("加载");
        buttonClearLoad.setOnAction(event -> {
            SQLRecordDialog sqlRecordDialog = new SQLRecordDialog();
            Optional<ButtonType> response = sqlRecordDialog.showAndWait();
            if (response.isPresent() && response.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                textArea.clear();
                textArea.insertText(0, sqlRecordDialog.getSelSQlText());
            }
        });
        Button buttonClearSave = new Button("保存");

        buttonClearSave.setOnAction(event -> {
            SaveSQLTextDialog saveSQLTextDialog = new SaveSQLTextDialog();
            Optional<ButtonType> response = saveSQLTextDialog.showAndWait();
            if (response.isPresent() && response.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                SQLRecordDialog.addSqlRecord(saveSQLTextDialog.getDscrib(), textArea.getText(), saveSQLTextDialog.getOwner());
            }
        });
        Button buttonClearTest = new Button("查询测试");
        buttonClearTest.setOnAction(event -> {
            String msg = "";
            SQLClauseParser parser = new SQLClauseParser(this.textArea.getText(), this.fields);
            if (parser.check() <= 0)
                msg = parser.getErrorDesc();
            else {
                RecordSet rcdSet = this.query();
                int count = 0;
                if (rcdSet != null) {
                    rcdSet.moveFirst();
                    while (!rcdSet.isEOF()) {
                        count++;
                        rcdSet.moveNext();
                    }
                    rcdSet.detach();
                    rcdSet.dispose();
                }
                msg = String.format("共查找到%d条记录", count);
            }
            if (!msg.isEmpty())
                MessageBox.information(msg);
        });
        TilePane tilePaneSQL = new TilePane(
                buttonClearAll,
                buttonClearLoad,
                buttonClearSave,
                buttonClearTest
        );
        for (Node node : tilePaneSQL.getChildren()) {
            if (node instanceof Button) {
                ((Button) node).setMinWidth(80);
            }
        }
        tilePaneSQL.setVgap(10);
        tilePaneSQL.setHgap(10);
        tilePaneSQL.setAlignment(Pos.TOP_LEFT);
        tilePaneSQL.setOrientation(Orientation.HORIZONTAL);

        VBox vBoxSQL = new VBox(textArea, tilePaneSQL);
        vBoxSQL.setSpacing(5);
        vBoxSQL.setAlignment(Pos.TOP_LEFT);

        titledPaneSQLExpress.setContent(vBoxSQL);
        titledPaneSQLExpress.setExpanded(true);
        titledPaneSQLExpress.setCollapsible(false);
        //endregion

        //region 布局
        titledPaneFld.setMinHeight(360);
        titledPaneOperSym.setMinHeight(360);
        GridPane gridPane = new GridPane();
        gridPane.add(titledPaneFld, 0, 0);
        gridPane.add(titledPaneOperSym, 1, 0);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.TOP_LEFT);

        this.add(gridPane, 0, 0);
        this.add(titledPaneSQLExpress, 0, 1);
        this.setHgap(10);
        this.setVgap(10);
        this.setMinHeight(500);
        this.setMinWidth(800);
        this.setAlignment(Pos.TOP_LEFT);
        //endregion
    }


    /**
     * 初始化字段信息
     */
    private void initFldInfo() {
        fieldInfos.clear();
        if (vCls != null) {
            fields = vCls.getFields();
            int count = fields.getFieldCount();
            for (short i = 0; i < count; i++) {
                Field fld = fields.getField(i);
                if (fld != null) {
                    // 屏蔽底层查询或统计算法暂时不支持的字段类型
                    if (FieldFunctions.canAttStatistic(fld.getFieldType()) && fld.getFieldType() != Field.FieldType.fldDate &&
                            fld.getFieldType() != Field.FieldType.fldTime && fld.getFieldType() != Field.FieldType.fldTimeStamp) {

                        fieldInfos.add(new FieldInfo(fld));
                    }
                }
            }
            this.tableViewFieldInfo.setItems(this.fieldInfos);
        }
    }

    /**
     * 将指定文本添加到SQL语句框中
     *
     * @param text
     */
    private void sqlEdit(String text) {
        if (text != null) {
            int oldLoc = this.textArea.getCaretPosition();//getSelectionStart;//Focus时，SelectionStart会变成0
            this.textArea.insertText(oldLoc, text);
//            this.textEdit_SQLExpress.Text = this.textEdit_SQLExpress.Text.Insert(oldLoc, text);

            int len = text.length();
            if (text.endsWith("()") || text.endsWith("''"))
                len -= 1;
            this.textArea.requestFocus();
            this.textArea.positionCaret(oldLoc + len);
        }
    }

    /**
     * 根据查询条件筛选结果
     *
     * @return 记录集
     */
    private RecordSet query() {
        RecordSet rcdSet = null;
        if (vCls != null) {
            QueryDef queryDef = new QueryDef();
            queryDef.setFilter(this.textArea.getText());
            queryDef.setWithSpatial(false);
            rcdSet = vCls.query(queryDef);
        }
        return rcdSet;
    }

    /// <summary>
    /// 获取属性值
    /// </summary>
    /// <param name="fld">获取属性值的字段</param>
    private void getAttributeValues(Field fld) {
        this.fieldVals.clear();
        if (fld != null && this.vCls != null) {
            Field.ExtField exFld = fld.getExtField();
            boolean flag = (exFld != null && exFld.getShape().equals(Field.FieldShape.fldShpCombo));//标识是否有键值
//            this.treeListColumn_keyVal.Visible = flag;
            this.getSingleValue(fld);
//            if (this.sfcls.getObjCount() <= progressLimit)
//            this.getSingleValue(fld);
//            else//显示进度条，故使用子线程
            {
//                this.waitForm = new WaitForm(true);
//                this.waitForm.SetText(MapGIS.Desktop.UI.Controls.Properties.Resources.String_GetSelectedAttrValue, MapGIS.Desktop.UI.Controls.Properties.Resources.String_GettingValue + "...");
//
//                // 修改说明：由于在子线程中会关闭waitForm，当子线程执行很快时，ShowDialog可能会在关闭后面执行（Bug7640）
//                // 修改人：陈容 2016-05-04
//                bool first = true;
//                this.waitForm.Load += (ws, es) =>
//                {
//                    if (first) {
//                        first = false;
//                        Thread threadCheck = new Thread(new ParameterizedThreadStart(getSingleValue));
//                        threadCheck.SetApartmentState(ApartmentState.STA);
//                        threadCheck.CurrentUICulture = System.Threading.Thread.CurrentThread.CurrentUICulture;
//                        threadCheck.Start(fld);
//                    }
//                }
//                ;
//                this.waitForm.ShowDialog(new Win32Window(XHelp.GetMainWindowHandle()));
            }
        }
        this.tableViewFieldVal.setItems(this.fieldVals);
    }

    /**
     * 获取单一属性值
     *
     * @param fld 获取属性值的字段
     */
    private void getSingleValue(Field fld) {
        this.fieldVals.clear();
        int displayCount = showLimit;//暂定
        if (fld != null && this.vCls != null && displayCount > 0) {
            Field.ExtField exFld = fld.getExtField();
            boolean flag = (exFld != null && exFld.getShape().equals(Field.FieldShape.fldShpCombo));//标识是否有键值

            Fields staFlds = new Fields();
            staFlds.appendField(fld);
            QueryDef queryDef = new QueryDef();
            //设置查找结果根据字段排序
            queryDef.setOrderField(fld.getFieldName());
//            queryDef.WithSpatial = false;
            queryDef.setCursorType(QueryDef.SetCursorType.ForwardOnly);
//            queryDef.setSubFields2(staFlds);
            queryDef.setIsAsc(true);
            RecordSet rcdSet = this.vCls.query(queryDef);
            if (rcdSet != null && rcdSet.getFields() != null) {
                Fields flds = rcdSet.getFields();
                boolean hasNull = false;
                short fldIndex = flds.getFieldIndex(fld.getFieldName());
                HashMap<Object, Object> dicts = new HashMap<>();
//                ArrayList<Object> dicts = new ArrayList<>();
                rcdSet.moveFirst();
                while (!rcdSet.isEOF()) {
                    Record rc = rcdSet.getAtt();
                    if (rc != null) {
                        Object val = rc.getFieldVal(fldIndex);
                        if (val != null) {
//                            if (val instanceof DateTime)
//                            {
//                                if (fld.getFieldType() == FieldType.fldDate)
//                                    val = ((DateTime)val).ToShortDateString();
//                                else if (fld.FieldType == FieldType.fldTime)
//                                    val = ((DateTime)val).ToLongTimeString();
//                            }
                            if (!dicts.containsKey(val)) {
                                String dspVal = flag ? FieldFunctions.getComboNameByFieldValue(fld, val) : val.toString();
                                dicts.put(val, null);
                                this.fieldVals.add(new FieldVal(val.toString(), flag, dspVal));
                                if (this.fieldVals.size() >= displayCount)
                                    break;
                            }
                        } else {
                            if (!hasNull) {
                                hasNull = true;
                                this.fieldVals.add(new FieldVal("", false, NullVal.getNullNal().toString()));
                            }
                        }
                    }
                    rcdSet.moveNext();
                }
                rcdSet.detach();
                rcdSet.dispose();
            }

            queryDef.dispose();
            staFlds.dispose();
        }
        this.tableViewFieldVal.setItems(this.fieldVals);
    }

    /// <summary>
    /// 获取单一属性值(根据已有的RecordSet记录集)
    /// </summary>
    /// <param name="fld"></param>
    private void getSingleValueFromRecordSet(Field fld) {
        this.fieldVals.clear();
        if (fld != null && this.m_RcdSet != null && this.m_RcdSet.getFields() != null) {

            boolean hasNull = false;
            int displayCount = this.showLimit;
            Field.ExtField exFld = fld.getExtField();
            boolean flag = (exFld != null && exFld.getShape().equals(Field.FieldShape.fldShpCombo));//标识是否有键值
//            this.treeListColumn_keyVal.Visible = flag;
            short fldIndex = this.m_RcdSet.getFields().getFieldIndex(fld.getFieldName());
            HashMap<Object, Object> dicts = new HashMap<>();
            if (m_RcdSet != null) {
                m_RcdSet.moveFirst();
                while (!m_RcdSet.isEOF()) {
                    Record rc = m_RcdSet.getAtt();
                    if (rc != null) {
                        Object val = rc.getFieldVal(fldIndex);
                        if (val != null) {
//                            if (val instanceof DateTime)
//                            {
//                                if (fld.FieldType == FieldType.FldDate)
//                                    val = ((DateTime) val).ToShortDateString();
//                                else if (fld.FieldType == FieldType.FldTime)
//                                    val = ((DateTime) val).ToLongTimeString();
//                            }
                            if (!dicts.containsKey(val)) {
                                String dspVal = flag ? FieldFunctions.getComboNameByFieldValue(fld, val) : val.toString();
                                dicts.put(val, null);
                                this.fieldVals.add(new FieldVal(val.toString(), flag, dspVal));
                                if (this.fieldVals.size() >= displayCount)
                                    break;
                            }
                        } else {
                            if (!hasNull) {
                                hasNull = true;
                                this.fieldVals.add(new FieldVal("", false, NullVal.getNullNal().toString()));

                            }
                        }
                    }
                    m_RcdSet.moveNext();
                }
            }
        }
        this.tableViewFieldVal.setItems(this.fieldVals);
    }

    protected class FieldInfo {
        private String name = "";
        private String type = "";
        private String asiaName = "";
        private Field field = null;

        public FieldInfo(Field field) {

            this.field = field;
            if (field != null) {
                this.name = field.getFieldName();
                this.type = LanguageConvert.fieldTypeConvert(field.getFieldType());
                String fldAlias = "";
                Field.ExtField extFld = field.getExtField();
                if (extFld != null)
                    fldAlias = extFld.getAlias();
                this.asiaName = fldAlias;
            }
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getAsiaName() {
            return asiaName;
        }

        public Field getField() {
            return field;
        }
    }

    protected class FieldVal {
        private String val;
        private boolean flag;
        private String dspVal;

        public FieldVal(String val) {
            this.val = val;
            this.flag = false;
            this.dspVal = val;
        }

        public FieldVal(String val, boolean flag, String dspVal) {
            this.val = val;
            this.flag = flag;
            this.dspVal = dspVal;
        }

        public String getVal() {
            return val;
        }

        public String getDspVal() {
            return dspVal;
        }
    }

    protected static class NullVal {
        private static NullVal _NullVal = null;

        private NullVal() {

        }

        public static NullVal getNullNal() {
            if (_NullVal == null)
                _NullVal = new NullVal();
            return _NullVal;
        }

        public String toString() {
            return "<NULL>";
        }
    }
}
