package com.zondy.mapgis.symbolselect;

import com.zondy.mapgis.common.DrawSymbolItem;
import com.zondy.mapgis.docitemproperty.PropertyBaseClass;
import com.zondy.mapgis.info.GeomInfo;
import com.zondy.mapgis.info.LinInfo;
import com.zondy.mapgis.info.PntInfo;
import com.zondy.mapgis.info.RegInfo;
import com.zondy.mapgis.systemlib.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.util.Callback;
import org.controlsfx.control.PropertySheet;

import java.io.ByteArrayInputStream;
import java.util.UUID;

/**
 * @ClassName SymbolSelectControl
 * @Description: 符号选择控件
 * @Author ysp
 * @Date 2020/4/2
 **/
public class SymbolSelectControl extends HBox {

    private TreeView treeView;
    private ListView listViewSymbol;
    private Label symbolCountLabel;
    private Label symNumLabel;
    private ImageView imageViewSymbol;
    private UUID uuid;
    private GeomInfo geomInfo;
    private int selectSymbolNo = 0;
    private int selectSymbolSubNo = 0;
    private PropertySheet propertySheet;
    private VBox vBox3;
    private SystemLibrary sysLib;
    private SymbolGeomType symbolGeomType;
    private boolean showPreView;

    /**
     * 选择指定系统库指定类型符号
     * @param sysLib
     * @param symbolGeomType
     * @param showPreView
     * @param symNum
     */
    public SymbolSelectControl(SystemLibrary sysLib, SymbolGeomType symbolGeomType, boolean showPreView, int symNum){
        this.initUI();
        this.sysLib = sysLib;
        this.symbolGeomType = symbolGeomType;
        this.showPreView = showPreView;
        this.selectSymbolNo = symNum;
        this.initData(this.sysLib, this.symbolGeomType, this.showPreView, this.selectSymbolNo);
    }

    /**
     * 设置图形参数对象
     * @param sysLib
     * @param geomInfo
     */
    public SymbolSelectControl(SystemLibrary sysLib, GeomInfo geomInfo){
        this.sysLib = sysLib;
        this.showPreView = true;
        this.InitGeomInfoUI(geomInfo);
        this.initUI();
        this.initData(this.sysLib, this.symbolGeomType, this.showPreView, this.selectSymbolNo);
    }


    /**
     * 界面初始化
     */
    private void initUI() {

        treeView = new TreeView();
        treeView.setPrefWidth(200);
        treeView.setPrefHeight(430);
        treeView.setPadding(new Insets(0, 0, 0, 0));
        symbolCountLabel = new Label();
        symbolCountLabel.setText("符号数量:   ");
        VBox vBox1 = new VBox();
        vBox1.setFillWidth(true);
        vBox1.getChildren().addAll(treeView,symbolCountLabel);
        vBox1.setSpacing(10);
        VBox.setVgrow(treeView,Priority.ALWAYS);

        listViewSymbol = new ListView();
        listViewSymbol.setPrefWidth(330);
        listViewSymbol.setPrefHeight(430);
        symNumLabel = new Label();
        symNumLabel.setText("符号编号:   ");
        VBox vBox2 = new VBox();
        vBox2.setFillWidth(true);
        vBox2.getChildren().addAll(listViewSymbol,symNumLabel);
        vBox2.setSpacing(10);
        VBox.setVgrow(listViewSymbol,Priority.ALWAYS);

        Label preViewLabel = new Label();
        preViewLabel.setText("  预览:");
        imageViewSymbol = new ImageView();
        imageViewSymbol.setFitWidth(200);
        imageViewSymbol.setFitHeight(200);
        vBox3 = new VBox();
        vBox3.setFillWidth(true);
        if (propertySheet == null) {
            vBox3.getChildren().addAll(preViewLabel,imageViewSymbol);
        }else
        {
            vBox3.getChildren().addAll(preViewLabel,imageViewSymbol,propertySheet);
            VBox.setVgrow(propertySheet,Priority.ALWAYS);
        }
        vBox3.setSpacing(10);
        this.setFillHeight(true);

        this.setSpacing(10);
        HBox.setHgrow(vBox2, Priority.ALWAYS);
        //预览
        if (!showPreView) {
            this.getChildren().addAll(vBox1,vBox2);
        }else {
            this.getChildren().addAll(vBox1,vBox2,vBox3);
        }
    }

    /**
     * 图形参数界面初始化
     * @param info
     */
    private void InitGeomInfoUI(GeomInfo info) {
        //region 图形参数设置
        if (info instanceof PntInfo) {
            //region 二维点参数
            this.geomInfo = ((PntInfo) info).clone();
            symbolGeomType = SymbolGeomType.GeomPnt;
            selectSymbolNo = ((PntInfo) info).getSymID();

            PropertyBaseClass.ZhPntInfo zhPntInfo = new PropertyBaseClass.ZhPntInfo();
            zhPntInfo.setItem(this.geomInfo);
            zhPntInfo.setShowSymID(false);
            zhPntInfo.setImmediatelyUpdate(true);
            zhPntInfo.getHasChangedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    //newValue为false表示zhPntInfo.getItem()已经更新
                    if (!newValue) {
                        geomInfo = (PntInfo) zhPntInfo.getItem();
                        SymbolInfoItem item = (SymbolInfoItem) listViewSymbol.getSelectionModel().getSelectedItem();
                        ((PntInfo) geomInfo).setSymID(item.num);
                        updatePreview(item);
                    }}
            });
            propertySheet = zhPntInfo.getPropertySheet();
            //endregion
        } else if (info instanceof LinInfo) {
            //region 二维线参数
            this.geomInfo = ((LinInfo) info).clone();
            symbolGeomType = SymbolGeomType.GeomLin;
            selectSymbolNo = ((LinInfo) info).getLinStyID();
            selectSymbolSubNo = info.getLibID();

            PropertyBaseClass.ZhLinInfo zhLinInfo = new PropertyBaseClass.ZhLinInfo();
            zhLinInfo.setItem(this.geomInfo);
            zhLinInfo.setShowSymID(false);
            zhLinInfo.setImmediatelyUpdate(true);
            zhLinInfo.getHasChangedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (!newValue) {
                        geomInfo = (LinInfo) zhLinInfo.getItem();
                        SymbolInfoItem item = (SymbolInfoItem) listViewSymbol.getSelectionModel().getSelectedItem();
                        ((LinInfo) geomInfo).setLinStyID(item.num);
                        ((LinInfo) geomInfo).setLibID((short) item.subNum);
                        updatePreview(item);
                    }
                }
            });
            propertySheet = zhLinInfo.getPropertySheet();
            //endregion
        } else if (info instanceof RegInfo) {
            //region 二维区参数
            this.geomInfo = ((RegInfo) info).clone();
            symbolGeomType = SymbolGeomType.GeomReg;
            selectSymbolNo = ((RegInfo) info).getPatID();

            PropertyBaseClass.ZhRegInfo zhRegInfo = new PropertyBaseClass.ZhRegInfo();
            zhRegInfo.setItem(this.geomInfo);
            zhRegInfo.setShowSymID(false);
            zhRegInfo.setImmediatelyUpdate(true);
            zhRegInfo.getHasChangedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (!newValue) {
                        geomInfo = (RegInfo) zhRegInfo.getItem();
                        SymbolInfoItem item = (SymbolInfoItem) listViewSymbol.getSelectionModel().getSelectedItem();
                        ((RegInfo) geomInfo).setPatID(item.num);
                        updatePreview(item);
                    }
                }
            });
            propertySheet = zhRegInfo.getPropertySheet();
            //endregion
        }
        //endregion

//            else if (geomInfo instanceof PntInfo3D)
//        {
//                #region 三维点参数
//            flag = false;
//            this.m_PntInfo3D = (geomInfo as PntInfo3D).Clone() as PntInfo3D;
//            m_CloneUserGeomInfo = this.m_PntInfo3D;
//            selectSymType = SymbolType.Pnt3D;
//            selectSymbolNo = this.m_PntInfo3D.SymID;
//
//            this.m_Pnt3DUserControl = new PntInfo3DUserControl(symLib, this.m_PntInfo3D);
//            this.m_Pnt3DUserControl.Dock = DockStyle.Fill;
//            this.m_Pnt3DUserControl.GeoInfoChangedEvent += new GeoInfoChangedHandler(PntUserControl_GeoInfoChangedEvent);
//            this.groupBox2.Controls.Add(this.m_Pnt3DUserControl);
//            //HideTree();
//        }
//            else if (geomInfo instanceof LinInfo3D)
//        {
//                #region 三维线参数
//            flag = false;
//            this.m_LinInfo3D = (geomInfo as LinInfo3D).Clone() as LinInfo3D;
//            m_CloneUserGeomInfo = this.m_LinInfo3D;
//            selectSymType = SymbolType.Lin3D;
//            selectSymbolNo = this.m_LinInfo3D.SymID;
//
//            this.m_Lin3DUserControl = new LinInfo3DUserControl(symLib, this.m_LinInfo3D);
//            this.m_Lin3DUserControl.Dock = DockStyle.Fill;
//            this.m_Lin3DUserControl.GeoInfoChangedEvent += new GeoInfoChangedHandler(LinUserControl_GeoInfoChangedEvent);
//            this.groupBox2.Controls.Add(this.m_Lin3DUserControl);
//            //HideTree();
//                #endregion
//        }
//            else if (geomInfo instanceof SurfaceInfo)
//        {
//                #region 三维面参数
//            flag = false;
//            this.m_SurfaceInfo = (geomInfo as SurfaceInfo).Clone() as SurfaceInfo;
//            m_CloneUserGeomInfo = this.m_SurfaceInfo;
//            selectSymType = SymbolType.Sur3D;
//            selectSymbolNo = this.m_SurfaceInfo.PatID;
//
//            this.m_Sur3DUserControl = new SurfaceInfoUserControl(symLib, this.m_SurfaceInfo);
//            this.m_Sur3DUserControl.Dock = DockStyle.Fill;
//            this.m_Sur3DUserControl.GeoInfoChangedEvent += new GeoInfoChangedHandler(RegUserControl_GeoInfoChangedEvent);
//            this.groupBox2.Controls.Add(this.m_Sur3DUserControl);
//            //HideTree();
//                #endregion
//        }
//            else if (geomInfo instanceof EntityInfo)
//        {
//                #region 三维面参数
//            // 修改说明：三维体转为三维面来选择符号
//            // 修改人：华凯 2014-06-04
//            flag = false;
//            EntityInfo entityInfo = geomInfo as EntityInfo;
//            this.m_SurfaceInfo = new SurfaceInfo();
//            this.m_SurfaceInfo.LibID = entityInfo.LibID;
//            this.m_SurfaceInfo.FillColor = entityInfo.FillColor;
//            this.m_SurfaceInfo.Ovprnt = entityInfo.Ovprnt;
//            this.m_SurfaceInfo.PatID = entityInfo.PatID;
//            this.m_SurfaceInfo.TextureScale = entityInfo.TextureScale;
//            this.m_SurfaceInfo.TextureScaleY = entityInfo.TextureScaleY;
//            this.m_SurfaceInfo.TransparentColor = entityInfo.TransparentColor;
//            m_CloneUserGeomInfo = this.m_SurfaceInfo;
//            selectSymType = SymbolType.Sur3D;
//            selectSymbolNo = this.m_SurfaceInfo.PatID;
//
//            this.m_Sur3DUserControl = new SurfaceInfoUserControl(symLib, this.m_SurfaceInfo);
//            this.m_Sur3DUserControl.Dock = DockStyle.Fill;
//            this.m_Sur3DUserControl.GeoInfoChangedEvent += new GeoInfoChangedHandler(RegUserControl_GeoInfoChangedEvent);
//            this.groupBox2.Controls.Add(this.m_Sur3DUserControl);
//            //HideTree();
//                #endregion
//        }


        //endregion

    }

    /**
     * 数据初始化
     *
     * @param sysLib
     * @param symbolGeomType
     * @param showPreView
     * @param symbolNum
     */
    private void initData(SystemLibrary sysLib, SymbolGeomType symbolGeomType, boolean showPreView, int symbolNum) {
        if (sysLib == null) {
            return;
        }
        // TreeView和TreeItem名字和图标
        Image imageTmp = getImageByColor(Color.RED, 16, 16);
        ImageView rootIcon = new ImageView(imageTmp);
        TreeItem<String> rootTreeItem = new TreeItem<>("Inbox", rootIcon);//new TreeItem<>("Inbox", rootIcon);
        rootTreeItem.setExpanded(true);
        TreeItem<String> item = new TreeItem<>(sysLib.getName());
        item.setExpanded(true);
        String strLib = "符号库";
        if (symbolGeomType == SymbolGeomType.GeomPnt) {
            strLib = "点符号";
        } else if (symbolGeomType == SymbolGeomType.GeomLin) {
            strLib = "线符号";
        } else if (symbolGeomType == SymbolGeomType.GeomReg) {
            strLib = "填充符号";
        }
        item.getChildren().add(new TreeItem<String>(strLib));
        rootTreeItem.getChildren().add(item);
        TreeView tree = this.treeView;//(TreeView) root.lookup("#treeView");
        tree.setRoot(rootTreeItem);
        tree.setShowRoot(false);

        //1.获取ListView
//        ListView listView = this.listViewSymbol;//(ListView) root.lookup("#listViewSymbol");
        this.listViewSymbol.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                SymbolInfoItem item = (SymbolInfoItem) listViewSymbol.getSelectionModel().getSelectedItem();
                if (item != null) {
                    if (item.type >= 0) {
                        if (symbolGeomType == SymbolGeomType.GeomLin) {
                            symNumLabel.setText(String.format("符号编号: %d-%d", item.num, item.subNum));
                        } else {
                            symNumLabel.setText(String.format("符号编号: %d", item.num));
                        }
                        selectSymbolNo = item.num;
                        selectSymbolSubNo = item.subNum;
                        if(geomInfo != null){
                            if(symbolGeomType == SymbolGeomType.GeomPnt){
                                ((PntInfo)geomInfo).setSymID(selectSymbolNo);
                            }else if(symbolGeomType == SymbolGeomType.GeomLin){
                                ((LinInfo)geomInfo).setLinStyID(selectSymbolNo);
                                ((LinInfo)geomInfo).setLibID((short) selectSymbolSubNo);
                            }else if(symbolGeomType == SymbolGeomType.GeomReg){
                                ((RegInfo)geomInfo).setPatID(selectSymbolNo);
                            }
                        }
                        updatePreview(item);

//                    System.out.println("ChangeListener");
                    }
                }
            }
        });
        // 2.创建数据源
        ObservableList<SymbolInfoItem> listData = FXCollections.observableArrayList();

        //SystemLibrary sysLib = this.getDefaultSymbolLibrary();
        uuid = sysLib.getSysLibGuid();
        SymbolLibrary symLib = sysLib.getSymbolLibarary();
        if (symLib != null) {
            listData.add(new SymbolInfoItem("", -1, 0, 0, 0, 0));
//        long symbolCount = symLib.getColorCount();//名字写错了???
//        for (int i = 300; i < 500; i++) {
//            Symbol symbol = symLib.getSymbolByNo(SymbolGeomType.GeomPnt, i, 0);
//            if (symbol != null) {
//                int type = this.convertSymbolGeomType(symbol.getGeomType());
//                listData.add(new SymbolInfoItem(symbol.getName(), type, (int) symbol.getSymbolNo(), 0, i, i));
//            }
//        }
            Symbols symbols = symLib.selectSymbols(symbolGeomType);//this.getSymbols(sysLib,new PntInfo());
            long count = 0;
            // 3.添加数据
            if (symbols != null) {
                count = symbols.getCount();
                for (int i = 0; i < count; i++) {
                    Symbol symbol = symbols.getSymbol(i);
                    if (symbol != null) {
                        int type = convertSymbolGeomType(symbol.getGeomType());
                        listData.add(new SymbolInfoItem(symbol.getName(), type, (int) symbol.getSymbolNo(), 0, i, i));
//                    listData.add(new SymbolInfoItem(symbol.getName(), 1, (int) symbol.getSymbolNo(), i, i));
                        // byte[] largeBuffer = DrawSymbolItem.DrawSymbol(sn.geomType, sn.No, sn.SubNo, this.largeImageList.ImageSize, this.colorSelectComboBox1.SelectColorNumber, m_CurSysLib.Guid);
                    }
                }

            }
            String str = "符号数量：" + Long.toString(count);
            this.symbolCountLabel.setText(str);

        }
        // 5.设置数据源
        listViewSymbol.setItems(listData);

        // 6 设置单元格的显示
        // 设置单元格生成器
        listViewSymbol.setCellFactory(new Callback<ListView<SymbolInfoItem>, ListCell<SymbolInfoItem>>() {
            @Override
            public ListCell<SymbolInfoItem> call(ListView<SymbolInfoItem> param) {
                return new MyListCell();
            }
        });

//        listViewSymbol.getSelectionModel().selectFirst();
        if(listData.size() >1) {
            listViewSymbol.getSelectionModel().select(1);
        }
    }

    /**
     * 获取符号
     * @param symLib
     * @param geomInfo
     * @return
     */
    private Symbols getSymbols(SystemLibrary symLib, GeomInfo geomInfo) {
        Symbols symbols = null;
        SymbolGeomType selectGeomType = SymbolGeomType.UnknownGeom; //用于判断二维符号类型
        PntInfo pntInfo = geomInfo instanceof PntInfo ? ((PntInfo) geomInfo) : null;
        LinInfo linInfo = geomInfo instanceof LinInfo ? ((LinInfo) geomInfo) : null;
        RegInfo regInfo = geomInfo instanceof RegInfo ? ((RegInfo) geomInfo) : null;

        if (pntInfo != null) {
            selectGeomType = SymbolGeomType.GeomPnt;
        } else if (linInfo != null) {
            selectGeomType = SymbolGeomType.GeomLin;
        } else if (regInfo != null) {
            selectGeomType = SymbolGeomType.GeomReg;
        }
        SystemLibrary systemlibary = SystemLibrarys.getSystemLibrarys().getDefaultSystemLibrary();//DrawSymbolItem.getDefaultSymbolLibrary();
        if (systemlibary != null) {
            SymbolLibrary symbolLibary = systemlibary.getSymbolLibarary();
            if (symbolLibary != null) {
                symbols = symbolLibary.selectSymbols(selectGeomType);
            }
        }
        return symbols;
    }

    /**
     * 更新预览图
     * @param item
     */
    private void updatePreview(SymbolInfoItem item) {

        if (item != null) {
            byte[] bytes;
            if (geomInfo != null) {
                bytes = DrawSymbolItem.drawSymbol(geomInfo,100,100,9,uuid);
            }else
            {
                bytes = DrawSymbolItem.drawSymbol(symbolGeomType, item.num, 0, 100, 100, 9, uuid);
            }
            if (bytes != null && bytes.length > 0) {
                ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
                this.imageViewSymbol.setImage(new Image(stream));
            }
        }
    }

    /**
     * SymbolGeomType类型转字符串
     * @param type
     * @return
     */
    private static int convertSymbolGeomType(SymbolGeomType type) {
        int rtnType = 0;
        if (type == SymbolGeomType.GeomPnt) {
            rtnType = 1;
        } else if (type == SymbolGeomType.GeomLin) {
            rtnType = 2;
        } else if (type == SymbolGeomType.GeomReg) {
            rtnType = 3;
        }
        return rtnType;
    }

    /**
     * 绘制颜色图片
     * @param color
     * @param width
     * @param height
     * @return
     */
    private static Image getImageByColor(Color color, int width, int height) {
        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriterGray = writableImage.getPixelWriter();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixelWriterGray.setColor(i, j, color);
            }
        }
        Image image = writableImage;
        return image;
    }

    /**
     * 获取符号编号
     *
     * @return 符号编号
     */
    public int getSelectNum() {
        return this.selectSymbolNo;
    }

    /**
     * 获取线符号子编号(线符号专用)
     *
     * @return 子编号
     */
    public int getSelectSubNum() {
        return this.selectSymbolSubNo;
    }
    /**
     * 获取图形参数(修改图形参数专用)
     *
     * @return 图形参数
     */
    public GeomInfo getGeomInfo() {
        return this.geomInfo;
    }

    /**
     * 符号子项信息
     */
    public static class SymbolInfoItem {
        public String name;     //名称
        public int type;        //类型0未知，1点，2线，3区
        public int num;         //编号
        public int subNum;      //子编号(线符号专用)
        public int index;       //索引
        public int imageIndex;       //索引

        //        private final SimpleObjectProperty<Image> image;     //缩略图
        public SymbolInfoItem(String name, int type, int num, int subNum, int index, int imageIndex) {
            this.name = name;
            this.type = type;
            this.num = num;
            this.subNum = subNum;
            this.index = index;
            this.imageIndex = imageIndex;
        }
    }


    /**
     * ListCell负责列表项里每一个Cell的显示
     */
    class MyListCell extends ListCell<SymbolInfoItem> {

        public MyListCell() {
            super();
        }


        @Override
        protected void updateItem(SymbolInfoItem item, boolean arg1) {
            super.updateItem(item, arg1);
            // 实现的单元格显示
            if (item == null) {
                this.setText("");
            } else {
                if (item.type >= 0) {
                    Image imageTmp = null;
                    if (item.num > 0) {
                        byte[] bytes = DrawSymbolItem.drawSymbol(symbolGeomType, item.num, 0, 32, 32, 9, uuid);
                        if (bytes != null && bytes.length > 0) {
                            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
                            imageTmp = new Image(stream);
                        }
                    }
                    if (imageTmp == null) {
                        imageTmp = getImageByColor(Color.WHITE, 16, 16);
                    }
                    HBox hbox = new HBox();
                    hbox.setFillHeight(true);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    ImageView imageView = new ImageView(imageTmp);
                    hbox.getChildren().add(imageView);

                    Label labelName = new Label();
                    labelName.setText(item.name);
                    labelName.setPrefWidth(160);
                    hbox.getChildren().add(labelName);

                    Label labelType = new Label();
                    String strType = "类型";
                    if (item.type == 1) {
                        strType = "点符号";
                    } else if (item.type == 2) {
                        strType = "线符号";
                    } else if (item.type == 3) {
                        strType = "填充符号";
                    }
                    labelType.setText(strType);
                    labelType.setPrefWidth(80);
                    hbox.getChildren().add(labelType);

                    Label labelNum = new Label();
                    String strNum = Integer.toString(item.num);
                    labelNum.setText(strNum);
                    labelNum.setPrefWidth(80);
                    hbox.getChildren().add(labelNum);

                    Label labelIndex = new Label();
                    String strIndex = Integer.toString(item.index);
                    labelIndex.setText(strIndex);
                    labelIndex.setPrefWidth(80);
                    hbox.getChildren().add(labelIndex);
                    setGraphic(hbox);
                } else {
                    //表头
                    HBox hbox = new HBox();
                    hbox.setMinHeight(32);
                    hbox.setFillHeight(true);
                    hbox.setAlignment(Pos.CENTER_LEFT);
//                    ImageView imageView = new ImageView(getImageByColor(Color.WHITE, 16, 16));
//                    hbox.getChildren().add(imageView);
                    //占位列
                    Label label = new Label();
                    label.setText("");
                    label.setPrefWidth(32);
                    hbox.getChildren().add(label);

                    Label labelName = new Label();
                    labelName.setText("名称");
                    labelName.setPrefWidth(160);
                    hbox.getChildren().add(labelName);

                    Label labelType = new Label();
                    labelType.setText("类型");
                    labelType.setPrefWidth(80);
                    hbox.getChildren().add(labelType);

                    Label labelNum = new Label();
                    labelNum.setText("编号");
                    labelNum.setPrefWidth(80);
                    hbox.getChildren().add(labelNum);

                    Label labelIndex = new Label();
                    labelIndex.setText("索引");
                    labelIndex.setPrefWidth(80);
                    hbox.getChildren().add(labelIndex);
                    setGraphic(hbox);
                }
            }
        }
    }
}
