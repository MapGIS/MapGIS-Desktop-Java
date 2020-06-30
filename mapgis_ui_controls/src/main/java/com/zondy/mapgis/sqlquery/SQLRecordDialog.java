package com.zondy.mapgis.sqlquery;

import com.zondy.mapgis.base.MessageBox;
import com.zondy.mapgis.base.XPath;
import com.zondy.mapgis.base.XString;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.io.*;
import java.util.Properties;


/**
 * @Description: SQL记录框
 * @Author ysp
 * @Date 2020/3/16
 **/
public class SQLRecordDialog extends Alert {
    private String sqlDescrib = null;       //描述语句
    private String sqlExpress = null;       //Sql表达式
//    private static String sqlrecords = "";//临时记录
    private TableView<SQLInfo> tableView;
    private ObservableList<SQLInfo> sqlInfos;
    private String selSQlText = "";       //Sql表达式

    public SQLRecordDialog() {
        super(AlertType.NONE);
        this.sqlInfos = FXCollections.observableArrayList();
        this.initUI();
        this.loadSqlRecords();
        Button buttonDel = new Button("删除");
        buttonDel.setOnAction(event -> {
            int index = this.tableView.getFocusModel().getFocusedIndex();
            if (index >= 0){
                this.sqlInfos.remove(index);
                this.tableView.setItems(this.sqlInfos);
                this.saveSqlRecords();
            }
        });
        VBox vBox = new VBox(buttonDel,this.tableView);
        vBox.setFillWidth(true);
        vBox.setSpacing(10);
        VBox.setVgrow(this.tableView, Priority.ALWAYS);
        this.setResizable(true);
        this.setTitle("SQL记录");
        this.getDialogPane().setContent(vBox);
        ButtonType saveButtonType = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);
        this.getButtonTypes().addAll(saveButtonType,cancelButtonType);
//        final Button buttonOK = (Button) this.getDialogPane().lookupButton(saveButtonType);
//        buttonOK.addEventFilter(ActionEvent.ACTION,this::buttonOK_OnAction);
//        setResultConverter(dialogButton -> dialogButton == ButtonType.OK ? this.selSQlText : null);
    }

    private void initUI() {
        tableView = new TableView<>();
        tableView.setPrefHeight(140);
        // 每个Table的列
        TableColumn descripCol = new TableColumn("描述");
        descripCol.setMinWidth(140);
        descripCol.setCellValueFactory(new PropertyValueFactory<SQLInfo, String>("description"));
        TableColumn sqlTextCol = new TableColumn("SQL语句");
        sqlTextCol.setMinWidth(200);
        sqlTextCol.setCellValueFactory(new PropertyValueFactory<SQLInfo, String>("sqlText"));
        tableView.getColumns().addAll(descripCol, sqlTextCol);
        tableView.setMinWidth(340);
        tableView.setMinHeight(300);
        tableView.setPrefWidth(340);
        tableView.setPrefHeight(300);
        tableView.setAccessibleText("aaa");
        tableView.setRowFactory(param -> {
            TableRow<SQLInfo> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 & (!row.isEmpty())) {
                    SQLInfo sqlInfo = row.getItem();
                    selSQlText = sqlInfo.getSqlText();
                    this.setResult(ButtonType.OK);
                    this.close();
                }
            });
            return row;
        });
        tableView.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                int val = newValue.intValue();
                if (val >= 0 && sqlInfos.size() > val) {
                    SQLInfo sqlInfo = sqlInfos.get(val);
                    if (sqlInfo != null) {
                        selSQlText = sqlInfo.getSqlText();
                    }
                }
            }
        });

    }

    private static String getPropertiesByName(String propertyName) {
        String propertyVal = "";
        InputStream in = null;
        String path = XPath.combine(XPath.getProgramPath(), "SQLRecord.log");
        File file = new File(path);
        try {
            // 如果文件不存在则创建它
            if (!file.exists()) {
                file.createNewFile();
                file = new File(path); //重新实例化
            }
            in= new FileInputStream(path);
            Properties properties = new Properties();
            properties.load(in);
            propertyVal = properties.getProperty(propertyName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return propertyVal;
    }
    private static void setPropertiesVal(String propertyName,String val) {
        InputStream in = null;
        FileOutputStream out = null;
        String path = XPath.combine(XPath.getProgramPath(), "SQLRecord.log");
        File file = new File(path);
        try {
            // 如果文件不存在则创建它
            if (!file.exists()) {
                file.createNewFile();
                file = new File(path); //重新实例化
            }
            in= new FileInputStream(path);
            Properties properties = new Properties();
            properties.load(in);
            out = new FileOutputStream(path, false);//true表示追加打开
            properties.setProperty(propertyName,val);
            properties.store(out, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (in != null) {
                    out.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //初始化树
    private void loadSqlRecords() {
        String sqlrecords = getPropertiesByName("SqlRecords");
        this.sqlInfos.clear();
        if (sqlrecords != null) {
            String[] records = sqlrecords.split(";");
            for (int i = 0; i < records.length; i++) {
                if (records[i] != "") {
                    //"." 、"\"、“|”是特殊字符，需要转译，"\\." 、"\\\"、“\\|”
                    String[] st = records[i].split("\\|");
                    if (st.length >= 2 && st[0] != "" && st[1] != "") {
                        this.sqlInfos.add(new SQLInfo(st[0], st[1], records[i] + ";"));

                    }
                }
            }
        }
        this.tableView.setItems(this.sqlInfos);
    }

    //保存记录
    private void saveSqlRecords() {
        String str = "";
        for (int i = 0; i < this.sqlInfos.size(); i++) {
            str += this.sqlInfos.get(i).getTag();
        }
        this.setPropertiesVal("SqlRecords",str);
    }

    public String getSelSQlText() {
        return selSQlText;
    }

    public static boolean isExist(String sqlrecords,String describ,String express){
        boolean isExist = false;
        if (sqlrecords != null && sqlrecords != "")
        {
            String[] records = sqlrecords.split(";");
            for (int i = 0; i < records.length; i++)
            {
                if (records[i] != "")
                {
                    String[] st = records[i].split("\\|");
                    if (st.length >= 2 && st[0] != "" && st[1] != "")
                    {
                        if (st[0] == describ)
                        {
                            isExist = true;
                            break;
                        }
                    }
                }
            }
        }
        return isExist;
    }
    /**
     * 添加Sql记录
     * @param describ 描述语句
     * @param express Sql表达式
     * @param owner 提示框所在window
     */
    public static boolean addSqlRecord(String describ, String express,Window owner)
    {
        boolean rtn = false;
        if (describ != "" && express != "")
        {
            String sqlrecords = getPropertiesByName("SqlRecords");
            boolean isExist = false;//isExist(sqlrecords,describ,express);
            if (sqlrecords != null) {
                String[] records = sqlrecords.split(";");
                for (int i = 0; i < records.length; i++) {
                    if (records[i] != "") {
                        //"." 、"\"、“|”是特殊字符，需要转译，"\\." 、"\\\"、“\\|”
                        String[] st = records[i].split("\\|");
                        if (st.length >= 2 && st[0] != "" && st[1] != "") {
                            if (st[0] == describ) {
                                isExist = true;
                                ButtonType result = MessageBox.question("存在同样描述语句，是否替换", owner);
                                if (ButtonType.OK.equals(result)) {
                                    records[i] = describ + '|' + express;
                                    sqlrecords = "";
                                    for (int j = 0; j < records.length; j++) {
                                        sqlrecords += records[j] + ';';
                                    }
                                    setPropertiesVal("SqlRecords", sqlrecords);
                                    MessageBox.information("保存成功");
                                    break;
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            else
                sqlrecords = "";
            if (!isExist) {
                sqlrecords += describ + '|' + express + ';';
                setPropertiesVal("SqlRecords",sqlrecords);
                MessageBox.information("保存成功");
            }


        }
        return rtn;
    }

    protected class SQLInfo {
        private String description = "";
        private String sqlText = "";
        private String tag = "";

        public SQLInfo(String description, String sqlText, String tag) {
            this.description = description;
            this.sqlText = sqlText;
            this.tag = tag;
        }

        public String getSqlText() {
            return sqlText;
        }

        public String getDescription() {
            return description;
        }

        public String getTag() {
            return tag;
        }
    }

}
