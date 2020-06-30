package com.zondy.mapgis.controls;

import com.zondy.mapgis.att.Fields;
import com.zondy.mapgis.att.Record;
import com.zondy.mapgis.geodatabase.IVectorCls;
import com.zondy.mapgis.geodatabase.QueryDef;
import com.zondy.mapgis.geodatabase.RecordSet;
import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.map.MapLayer;
import com.zondy.mapgis.scene.Map3DLayer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 属性控件
 */
public class AttControl extends VBox{
    private DocumentItem attLayer; //当前显示属性的图层
    private IVectorCls attCls;//显示属性的数据类

    private CGISTableview m_TableView;
    private CPageButtonGroup m_PageBtnGroup;
    private boolean isReadOnly = false;

    /**
     * 构造方法
     */
    public AttControl(IVectorCls vectorCls)
    {
        this.isReadOnly = true;
        m_TableView = new CGISTableview(vectorCls);
        m_PageBtnGroup = new CPageButtonGroup(m_TableView);
        m_TableView.initView();
        m_TableView.refreshView();
        HBox hbox = new HBox();
        hbox.getChildren().addAll(m_TableView);
        hbox.setHgrow(m_TableView, Priority.ALWAYS);
        HBox btnHBox = m_PageBtnGroup.getHBox();
        getChildren().addAll(hbox,btnHBox);
        this.setMaxWidth(Double.MAX_VALUE);
    }

    /**
     * 构造方法
     */
    public AttControl(IVectorCls vectorCls,boolean isReadOnly)
    {
        this.isReadOnly = isReadOnly;
        m_TableView = new CGISTableview(vectorCls);
        m_PageBtnGroup = new CPageButtonGroup(m_TableView);
        m_TableView.initView();
        m_TableView.refreshView();
        HBox hbox = new HBox();
        hbox.getChildren().addAll(m_TableView);
        hbox.setHgrow(m_TableView, Priority.ALWAYS);
        HBox btnHBox = m_PageBtnGroup.getHBox();
        getChildren().addAll(hbox,btnHBox);
        this.setMaxWidth(Double.MAX_VALUE);

    }

    //region 公共方法
    /**
     * 获取当前显示属性的图层
     */
    public DocumentItem getAttLayer()
    {
       return this.attLayer;
    }
    /// <summary>
    /// 显示图层的指定属性
    /// </summary>
    /// <param name="item">图层</param>
    /// <param name="set">选择集</param>
    /**
     * 显示图层的指定属性
     * @param item 图层
     * @param set 选择集(传null显示所有)
     */
    public void showAttribute(DocumentItem item, RecordSet set)
    {
        IVectorCls vCls = null;
        if (item instanceof MapLayer)
        vCls = (IVectorCls)((MapLayer)item).getData();
            else if (item instanceof Map3DLayer)
        vCls = (IVectorCls)((Map3DLayer)item).getData();

        if (vCls != null)
        {
            this.attLayer = item;
            this.attCls = vCls;
            this.m_TableView.m_vectorcls = vCls;
            if(set != null)
            {
                this.m_TableView.fillView(set,false);
            }
            this.m_TableView.refreshView();
            this.m_TableView.refresh();
        }
    }
    /**
     * 清空空间数据
     */
    public  void clearData()
    {
        m_TableView.m_allData.clear();
        m_TableView.refresh();
        m_PageBtnGroup.refresh();
        this.attLayer = null;
    }
    //endregion

    /**
     * Created by Administrator on 2019/11/7.
     */
     class CGISTableview extends TableView {
        protected IVectorCls m_vectorcls = null;

        protected Object m_FirstValue = 1;
        protected Object m_LastValue = 1;
        protected long m_lMinOID = -1;
        protected long m_lMaxOID = -1;
        protected String m_strAttClause = "";
        protected String m_strOrderFld  = "mpoid";
        protected short  m_sOrderFldInx = -1;   //-1为OID
        protected boolean m_bIsASC = false;
        protected HashMap<Short, Short> m_fldNo2colNo = new  HashMap<Short, Short>();  //字段索引-列索引映射关系键值对
        protected int  m_nPageSize = 100;
        protected long m_nTotalCount = 0;

        protected Fields m_clsStru;
        protected ObservableList<Map> m_allData;

        //protected CPageButtonGroup m_btPage;
        //protected CSelectExp m_selExp;

        CGISTableview(IVectorCls vectorCls){
            if(vectorCls != null){
                m_vectorcls = vectorCls;
                m_clsStru = vectorCls.getFields();
                m_allData = FXCollections.observableArrayList();
                m_nTotalCount = vectorCls.getObjCount();
                System.out.println("m_nTotalCount" + m_nTotalCount);
            }
        }

//        public void  show(){
//            initView();
//            refreshView();
//
////            Stage stage = new Stage();
////            m_btPage = new  CPageButtonGroup(this);
////            VBox vbox = new VBox();
////
////            HBox hbox = new HBox();
////            hbox.setHgrow(this, Priority.ALWAYS);
////            this.setMaxWidth(Double.MAX_VALUE);
////            hbox.getChildren().addAll(this);
////
////            Scene scen = new Scene(new VBox(hbox, m_btPage.getHBox()));
////            stage.setMaxWidth(Double.MAX_VALUE);
////            stage.setMaxHeight(Double.MAX_VALUE);
////            stage.setScene(scen);
////            stage.showAndWait();
//        }

        public void refreshView(){
            m_strOrderFld = "mpoid";
            m_bIsASC = true;
            m_strAttClause = "";
            headPage();
            if(m_PageBtnGroup != null)
                m_PageBtnGroup.refresh();
        }
        private void fillView(QueryDef def, boolean bInverted){
            if(m_vectorcls != null) {
                RecordSet set = m_vectorcls.query(def);
                m_allData.removeAll(m_allData);
                if (null == set)
                    return;
                boolean bFirst = true;
                Record rcd;
                for (set.moveFirst(); !set.isBOF() && !set.isEOF(); set.moveNext()) {
                    rcd = set.getAtt();
                    if (bFirst) {
                        if (m_sOrderFldInx > -1)
                            m_FirstValue = rcd.getFieldVal(m_sOrderFldInx);
                        else
                            m_FirstValue = set.getID();
                        bFirst = false;
                    }
                    m_LastValue = set.getID();
                    addRecord((Long) m_LastValue, rcd);
                    if (m_sOrderFldInx > -1)
                        m_LastValue = rcd.getFieldVal(m_sOrderFldInx);
                }

                if (bInverted) {
                    ObservableList<Map> tempData = FXCollections.observableArrayList();
                    for (int i = m_allData.size(); i > 0; i--) {
                        tempData.add(m_allData.get(i - 1));
                    }
                    m_allData.setAll(tempData);
                    //
                    Object temp = m_LastValue;
                    m_LastValue = m_FirstValue;
                    m_FirstValue = temp;
                }
            }
        }
        private void fillView(RecordSet recordSet,boolean bInverted)
        {
            RecordSet set = recordSet;
            m_allData.removeAll(m_allData);
            if (null == set)
                return;
            boolean bFirst = true;
            Record rcd;
            for (set.moveFirst(); !set.isBOF() && !set.isEOF(); set.moveNext()) {
                rcd = set.getAtt();
                if (bFirst) {
                    if (m_sOrderFldInx > -1)
                        m_FirstValue = rcd.getFieldVal(m_sOrderFldInx);
                    else
                        m_FirstValue = set.getID();
                    bFirst = false;
                }
                m_LastValue = set.getID();
                addRecord((Long) m_LastValue, rcd);
                if (m_sOrderFldInx > -1)
                    m_LastValue = rcd.getFieldVal(m_sOrderFldInx);
            }

            if (bInverted) {
                ObservableList<Map> tempData = FXCollections.observableArrayList();
                for (int i = m_allData.size(); i > 0; i--) {
                    tempData.add(m_allData.get(i - 1));
                }
                m_allData.setAll(tempData);
                //
                Object temp = m_LastValue;
                m_LastValue = m_FirstValue;
                m_FirstValue = temp;
            }
        }
        private void addRecord(long oid, Record rcd) {
            Map<Short, Object> rowData = new HashMap<>();
            //oid
            rowData.put(Short.valueOf((short)-1), oid);
            for (short i = 0; i < m_clsStru.getFieldCount(); i++) {
                if (0 == rcd.isFieldULL(i))
                    rowData.put(Short.valueOf(i), rcd.getFieldVal(i));
            }
            m_allData.add(rowData);
            System.out.println("add record");
        }

        public void setPageSize(int  nPageSize){
            if (nPageSize > 0)
                m_nPageSize = nPageSize;
        }
        public int getPageSize(){
            return  m_nPageSize;
        }

        private void initView(){
            //xuhao
            TableColumn<Map, String> DataColumn1 = new TableColumn<>("序号");
            //DataColumn1.setCellValueFactory(new MapValueFactory("序号"));
            DataColumn1.setMinWidth(30);
            getColumns().add(DataColumn1);
            DataColumn1.setCellFactory((col) -> {
                TableCell<Map, String> cell = new TableCell<Map, String>() {
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        this.setText(null);
                        this.setGraphic(null);
                        if (!empty) {
                            this.setText(String.valueOf(this.getIndex() + 1));
                        }
                    }
                };
                return cell;
            });
            DataColumn1.setSortable(false);
            m_fldNo2colNo.put(Short.valueOf((short)-2), Short.valueOf((short)0));

            //OID
            TableColumn<Map, Long> DataColumn2 = new TableColumn<>("OID");
            DataColumn2.setCellValueFactory(new MapValueFactory((short)-1));
            DataColumn2.setMinWidth(30);
            getColumns().add(DataColumn2);
            DataColumn2.setCellFactory((col) -> {
                MyTextFieldTableCell<Map, Long> cell = new MyTextFieldTableCell<Map, Long>(this) {
                    public void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (null != item && !empty)
                            this.setText(String.valueOf(item));
                    }
                };
                return cell;
            });
            DataColumn2.setSortable(false);
            //不可编辑
            DataColumn2.setEditable(true);
            m_fldNo2colNo.put(Short.valueOf((short)-1), Short.valueOf((short)1));

            //属性结构
            for ( short i = 0; i < m_clsStru.getFieldCount(); i++) {
                TableColumn DataColumn;
                switch (m_clsStru.getField(i).getFieldType().value()){
                    case 4:
                        DataColumn = new TableColumn<Map, Long>(m_clsStru.getField(i).getFieldName());
                        DataColumn.setCellFactory((col) ->{
                            MyTextFieldTableCell<Map, Long> cell = new MyTextFieldTableCell<Map, Long>(this) {};
                            return cell;
                        });
                        DataColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Map, Long>>() {
                            @Override
                            public void handle(TableColumn.CellEditEvent<Map, Long> event) {
                                if (event.getNewValue() != event.getOldValue()){
                                    //                               try{
////                                    Long.valueOf(String.valueOf(event.getNewValue()));
                                    //                               }
                                    //                               catch (Exception()){
                                    //                                  Alert al = new Alert(Alert.AlertType.ERROR);
                                    //                                  al.showAndWait();
                                    //                              }
                                    EditCell(event.getTablePosition().getRow(),
                                            event.getTablePosition().getColumn(),
                                            Long.valueOf(String.valueOf(event.getNewValue())));
                                }
                            }
                        });
                        break;
                    case 7:
                        DataColumn = new TableColumn<Map, Double>(m_clsStru.getField(i).getFieldName());
                        DataColumn.setCellFactory((col) ->{
                            MyTextFieldTableCell<Map, Double> cell = new MyTextFieldTableCell<Map, Double>(this) {};
                            return cell;
                        });
                        DataColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Map, Double>>() {
                            @Override
                            public void handle(TableColumn.CellEditEvent<Map, Double> event) {
                                if (event.getNewValue() != event.getOldValue()){
                                    EditCell(event.getTablePosition().getRow(),
                                            event.getTablePosition().getColumn(),
                                            event.getNewValue());
                                }
                            }
                        });
                        break;
                    default:
                    case 0:
                        DataColumn = new TableColumn<Map, Object>(m_clsStru.getField(i).getFieldName());
                        DataColumn.setCellFactory((col) ->{
                            MyTextFieldTableCell<Map, Object> cell = new MyTextFieldTableCell<Map, Object>(this) {};
                            return cell;
                        });
                        break;

                }
                getColumns().add(DataColumn);
                DataColumn.setCellValueFactory(new MapValueFactory(i));
                DataColumn.setMinWidth(130);

                DataColumn.setSortable(false);
                DataColumn.setContextMenu(new MyContextMenu(this, DataColumn));
                m_fldNo2colNo.put(Short.valueOf(i), Short.valueOf((short)(i+2)));
            }
            setItems(m_allData);
            setEditable(!isReadOnly);
            getSelectionModel().setCellSelectionEnabled(true);
        }
        protected void EditCell(int nRow, int nCol, Object strDest) {
            Map<Short, Object> rowData = m_allData.get(nRow);
            if (rowData != null) {
                short sFldIdx = -1;
                //减去序号列和OID列
                Set<Short> keyset = m_fldNo2colNo.keySet();
                Iterator<Short> it = keyset.iterator();
                while (it.hasNext()) {
                    short sKey = it.next();
                    if (m_fldNo2colNo.get(sKey) == nCol){
                        sFldIdx = sKey;
                        break;
                    }
                }
                //更新记录纸缓存
                if(null == strDest)
                    rowData.remove(sFldIdx);
                else
                    rowData.put(sFldIdx, strDest);
                //更新类
                long oid = (long)(rowData.get(Short.valueOf((short)-1)));
                Record rcd = m_vectorcls.getAtt(oid);
                if(null == strDest)
                    rcd.setFieldNULL(sFldIdx);
                else
                    rcd.setFieldVal(sFldIdx, strDest);
                m_vectorcls.updateAtt(oid, rcd);
                m_allData.set(nRow, rowData);
            }
        }
        protected void Sort(String strCol, boolean bIsAsc){
            m_strOrderFld = strCol;
            m_bIsASC = bIsAsc;
            m_sOrderFldInx = m_clsStru.getFieldIndex(m_strOrderFld);
            headPage();
        }
        protected void headPage(){
            QueryDef def = new QueryDef();
            def.setSubFields("*");
            QueryDef.SetCursorType cursor = QueryDef.SetCursorType.ForwardOnly;
            def.setCursorType(cursor);
            def.setFilter(m_strAttClause);
            def.setPagination(0, m_nPageSize);
            def.setOrderField(m_strOrderFld);
            def.setIsAsc(m_bIsASC);
            //def.setIsAsc(m_strOrderFld, m_bIsASC);
            fillView(def, false);
            if (m_PageBtnGroup != null)
                m_PageBtnGroup.refresh();
        }
        protected void prevPage(){
            QueryDef def = new QueryDef();
            def.setSubFields("*");
            QueryDef.SetCursorType cursor = QueryDef.SetCursorType.ForwardOnly;
            def.setCursorType(cursor);
            String strClause = m_strOrderFld;
            if (m_bIsASC)
                strClause += " < ";
            else
                strClause += " > ";
            strClause += String.valueOf(m_FirstValue);
            if (m_strAttClause.length() > 0)
                strClause += " and " + m_strAttClause;
            def.setFilter(strClause);
            def.setPagination(0, m_nPageSize);
            def.setOrderByFID(true);
            def.setIsAsc(!m_bIsASC);
            fillView(def, true);
        }
        protected void nextPage(){
            QueryDef def = new QueryDef();
            def.setSubFields("*");
            QueryDef.SetCursorType cursor = QueryDef.SetCursorType.ForwardOnly;
            def.setCursorType(cursor);
            String strClause = m_strOrderFld;
            if (m_bIsASC)
                strClause += " > ";
            else
                strClause += " < ";
            strClause += String.valueOf(m_LastValue);
            if (m_strAttClause.length() > 0)
                strClause += " and " + m_strAttClause;
            def.setFilter(strClause);
            def.setPagination(0, m_nPageSize);
            def.setOrderField(m_strOrderFld);
            def.setIsAsc(m_bIsASC);
            fillView(def, false);
        }
        protected void  endPage(){
            QueryDef def = new QueryDef();
            def.setSubFields("*");
            QueryDef.SetCursorType cursor = QueryDef.SetCursorType.ForwardOnly;
            def.setCursorType(cursor);
            long nLimit = 0;
            int nPageCnt = 0;
            if (m_nTotalCount % m_nPageSize == 0)
                nPageCnt = (int) (m_nTotalCount / m_nPageSize);
            else {
                nPageCnt = (int) (m_nTotalCount / m_nPageSize) + 1;
                nLimit = m_nTotalCount % m_nPageSize;
            }
            def.setPagination(0, (int) nLimit);
            def.setFilter(m_strAttClause);
            def.setOrderField(m_strOrderFld);
            def.setIsAsc(!m_bIsASC);
            fillView(def, true);
        }
        protected void jumpPage(int nPageNo){
            QueryDef def = new QueryDef();
            def.setSubFields("*");
            QueryDef.SetCursorType cursor = QueryDef.SetCursorType.ForwardOnly;
            def.setCursorType(cursor);
            def.setPagination((nPageNo - 1)*m_nPageSize, m_nPageSize);
            def.setOrderField(m_strOrderFld);
            def.setIsAsc(m_bIsASC);
            fillView(def, false);
        }

        protected void query(String  strAttClause){
            m_strAttClause = strAttClause;
            headPage();
        }
    }

    /**
     * Created by Administrator on 2019/11/13.
     */
     class CPageButtonGroup {
        private Button m_btHead = new Button("首页");
        private Button m_btPrev = new Button("上一页");
        private int m_nCurrentPageno = 1;
        private int m_nEndPageno = 1;
        private TextField m_tfPageno = new TextField(String.valueOf(m_nCurrentPageno));
        private Button m_btNext = new Button("下一页");
        private Button m_btEnd = new Button("尾页");

        CPageButtonGroup(CGISTableview gisTableview){
            m_btHead.setDisable(true);
            m_btPrev.setDisable(true);
            m_nEndPageno = (int )gisTableview.m_nTotalCount/gisTableview.m_nPageSize;
            m_nEndPageno += gisTableview.m_nTotalCount % gisTableview.m_nPageSize == 0?0:1;
            if (m_nEndPageno == 1){
                m_btNext.setDisable(true);
                m_btEnd.setDisable(true);
            }
            m_btHead.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    gisTableview.headPage();
                    m_btHead.setDisable(true);
                    m_btPrev.setDisable(true);
                    m_btNext.setDisable(false);
                    m_btEnd.setDisable(false);
                    m_nCurrentPageno = 1;
                    m_tfPageno.setText(String.valueOf(m_nCurrentPageno));
                }
            });
            m_btPrev.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    gisTableview.prevPage();
                    m_tfPageno.setText(String.valueOf(--m_nCurrentPageno));
                    m_btNext.setDisable(false);
                    m_btEnd.setDisable(false);
                    if (1 == m_nCurrentPageno){
                        m_btHead.setDisable(true);
                        m_btPrev.setDisable(true);
                    }
                }
            });
            m_btNext.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    gisTableview.nextPage();
                    m_tfPageno.setText(String.valueOf(++m_nCurrentPageno));
                    m_btHead.setDisable(false);
                    m_btPrev.setDisable(false);
                    if (m_nEndPageno == m_nCurrentPageno){
                        m_btNext.setDisable(true);
                        m_btEnd.setDisable(true);
                    }
                }
            });
            m_btEnd.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    gisTableview.endPage();
                    m_nCurrentPageno = m_nEndPageno;
                    m_tfPageno.setText(String.valueOf(m_nCurrentPageno));
                    m_btHead.setDisable(false);
                    m_btPrev.setDisable(false);
                    m_btNext.setDisable(true);
                    m_btEnd.setDisable(true);
                }
            });

            //输入检测
            m_tfPageno.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if (!newValue.matches("\\d*")){
                        m_tfPageno.setText(newValue.replaceAll("[^\\d]", ""));
                    }
                }
            });
            m_tfPageno.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if (event.getCode().equals( KeyCode.ENTER)){
                        gisTableview.jumpPage(Integer.valueOf(m_tfPageno.getText()));

                    }
                }
            });
            // 监听焦点
            m_tfPageno.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                }
            });
        }

        protected void refresh(){
            m_btHead.setDisable(true);
            m_btPrev.setDisable(true);
            m_btNext.setDisable(false);
            m_btEnd.setDisable(false);
            if (m_nEndPageno == 1){
                m_btNext.setDisable(true);
                m_btEnd.setDisable(true);
            }
            m_nCurrentPageno = 1;
            m_tfPageno.setText(String.valueOf(m_nCurrentPageno));
        }

        protected HBox getHBox(){
            HBox hBox = new HBox();
            hBox.setHgrow(m_btHead, Priority.ALWAYS);
            hBox.setHgrow(m_btPrev, Priority.ALWAYS);
            hBox.setHgrow(m_tfPageno, Priority.ALWAYS);
            hBox.setHgrow(m_btNext, Priority.ALWAYS);
            hBox.setHgrow(m_btEnd, Priority.ALWAYS);
            m_btHead.setMaxWidth(Double.MAX_VALUE);
            m_btPrev.setMaxWidth(Double.MAX_VALUE);
            m_tfPageno.setMaxWidth(Double.MAX_VALUE);
            m_btNext.setMaxWidth(Double.MAX_VALUE);
            m_btEnd.setMaxWidth(Double.MAX_VALUE);

            hBox.getChildren().addAll(m_btHead, m_btPrev, m_tfPageno, m_btNext, m_btEnd);
            return  hBox;
        }

    }

    /**
     * Created by Administrator on 2019/11/12.
     */
     class MyContextMenu extends ContextMenu {
        private  MenuItem m_itemFrezeeCol = new MenuItem("冻结");
        private  MenuItem m_itemHideCol = new MenuItem("隐藏");
        private  MenuItem m_itemOrderAsc = new MenuItem("升序");
        private  MenuItem m_itemOrderDesc = new MenuItem("降序");
        private  MenuItem m_itemSelect = new MenuItem("按属性查询");
        MyContextMenu(CGISTableview gisTableview, TableColumn DataColumn){
            m_itemOrderAsc.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    gisTableview.Sort(DataColumn.getText(), true);
                    DataColumn.setSortNode(new TextField("asc"));
                }
            });
            m_itemOrderDesc.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    gisTableview.Sort(DataColumn.getText(), false);
                }
            });
            m_itemHideCol.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    DataColumn.setVisible(false);
                    short sFldCol = gisTableview.m_clsStru.getFieldIndex(DataColumn.getText());
                    short sColNo  = gisTableview.m_fldNo2colNo.get(sFldCol);
                    gisTableview.m_fldNo2colNo.remove(sFldCol);

                    Set<Short> keyset = gisTableview.m_fldNo2colNo.keySet();
                    Iterator<Short> it = keyset.iterator();
                    while (it.hasNext()){
                        short sKey = it.next();
                        short sTempColNo  = gisTableview.m_fldNo2colNo.get(sKey);
                        if(sTempColNo > sColNo){
                            gisTableview.m_fldNo2colNo.put(sKey, --sTempColNo);
                        }
                    }
                }
            });
            m_itemSelect.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    //查找界面暂不支持
//                    if (null == gisTableview.m_selExp) {
//                        gisTableview.m_selExp = new CSelectExp(gisTableview);
//                    }
//                    else
//                        gisTableview.m_selExp.reShow();
                }
            });

            Menu menu = new Menu("排序");
            menu.getItems().addAll(m_itemOrderAsc, m_itemOrderDesc);
            getItems().addAll(m_itemFrezeeCol, m_itemHideCol, menu, m_itemSelect);
        }


    }

    /**
     * Created by Administrator on 2019/11/7.
     */
     class MyTextFieldTableCell<S,T>   extends TextFieldTableCell<S,T> {

        private  boolean m_bIsChange = false;
        private  CGISTableview m_gisTableview;
        MyTextFieldTableCell(CGISTableview gisTableview)
        {
            super(new StringConverter(){
                @Override
                public String toString(Object t) {
                    if (t != null)
                        return t.toString();
                    return  null;
                }

                @Override
                public Object fromString(String string) {
                    if (string != null)
                        return string;
                    return  null;
                }
            });
            m_gisTableview = gisTableview;

        }

        public void startEdit() {
            super.startEdit();
            m_bIsChange = false;
            ((TextField) (getGraphic())).textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    m_bIsChange = true;
                }
            });
        }
        public void cancelEdit(){
        /*if (super.isEditable() && m_bIsChange) {
            m_gisTableview.EditCell(m_gisTableview.getEditingCell().getRow(),
                    m_gisTableview.getEditingCell().getColumn(),
                    ((TextField) (getGraphic())).getText());
            m_bIsChange = false;
        }*/
            super.cancelEdit();
        }
    }
}
