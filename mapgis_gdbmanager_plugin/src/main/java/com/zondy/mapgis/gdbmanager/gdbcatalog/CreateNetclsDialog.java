package com.zondy.mapgis.gdbmanager.gdbcatalog;

import com.google.common.collect.ImmutableMap;
import com.zondy.mapgis.att.Field;
import com.zondy.mapgis.att.Fields;
import com.zondy.mapgis.base.*;
import com.zondy.mapgis.controls.common.ZDComboBox;
import com.zondy.mapgis.controls.wizard.Direction;
import com.zondy.mapgis.controls.wizard.Wizard;
import com.zondy.mapgis.controls.wizard.WizardPage;
import com.zondy.mapgis.geodatabase.*;
import com.zondy.mapgis.geodatabase.net.*;
import com.zondy.mapgis.geometry.GeomType;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.util.*;

/**
 * 创建网络类
 *
 * @author CR
 * @file CreateNetclsDialog.java
 * @brief 创建网络类
 * @create 2020-02-26.
 */
public class CreateNetclsDialog extends Dialog
{
    //region 变量
    private final TextField textFieldName = new TextField();
    private final TextField textFieldRadius = new TextField("0.000015");//未完成。构造double输入控件，必须大于0
    private final CheckBox checkBoxJustFrame = new CheckBox("仅创建网络类框架");
    private final CheckBox checkBoxBuildRel = new CheckBox("建立关联关系");
    private final TableView<NLayer> tvLayer = new TableView<>();
    private final TableView<NLayerSettings> tvLayerSettings = new TableView<>();
    private final TableView<NWeight> tvWeight = new TableView<>();
    private final TableView<NWeightField> tvWeightField = new TableView<>();
    private DataBase db;   //待创建网络类的数据库
    private int dsID = 0;  //新创建的网络类所属的要素数据集的ID
    private int clsID = -1;//新创建的网络类的ID
    private Tooltip tooltipError = new Tooltip();
    private Wizard wizard;
    private WizardPage wizardPageBase;
    private WizardPage wizardPageLayer;
    private WizardPage wizardPageWeight;
    //endregion

    //region 构造函数
    public CreateNetclsDialog(Window owner)
    {
        this(owner, null, 0);
    }

    /**
     * 构造创建网络类的向导界面
     *
     * @param owner 窗体父容器
     * @param db    数据库
     * @param dsID  数据库ID
     */
    public CreateNetclsDialog(Window owner, DataBase db, int dsID)
    {
        this.setTitle("创建网络类");
        this.setResizable(true);
        this.db = db;
        this.dsID = dsID;

        //region Page-基本信息
        this.textFieldRadius.setTooltip(new Tooltip("创建网络时将根据指定的半径进行捏合。"));
        this.checkBoxBuildRel.setTooltip(new Tooltip("默认只能两层建立关系。"));

        GridPane gridPane = new GridPane();
        gridPane.setVgap(6);
        gridPane.setHgap(6);
        gridPane.add(new Label("网络类名称:"), 0, 0);
        gridPane.add(new Label("捕捉半径:"), 0, 1);
        gridPane.add(this.textFieldName, 1, 0);
        gridPane.add(this.textFieldRadius, 1, 1);
        gridPane.add(this.checkBoxJustFrame, 1, 2);
        gridPane.add(this.checkBoxBuildRel, 1, 3);
        GridPane.setHgrow(this.textFieldRadius, Priority.ALWAYS);

        this.textFieldName.textProperty().addListener((o, ov, nv) ->
        {
            List<Character> list = new ArrayList<>();
            if (this.db != null)
            {
                char[] invalidChars = this.db.getServer().getInvalidChars(3);//类名：XClsName
                if (invalidChars != null)
                {
                    for (char ch : invalidChars)
                    {
                        list.add(ch);
                    }
                }
            }
            StringProperty errorMsg = new SimpleStringProperty();
            if (!XString.isTextValid(nv, 120, list, errorMsg))
            {
                UIFunctions.showErrorTip(this.textFieldName, errorMsg.get(), this.tooltipError);
                this.textFieldName.setText(ov);
            }
        });

        List<String> nameList = new ArrayList<>();
        if (this.db != null)
        {
            int[] dses = this.db.getXclses(XClsType.XFds, 0);
            if (dses != null)
            {
                for (int ds : dses)
                {
                    int[] ns = this.db.getXclses(XClsType.XGNet, ds);
                    if (ns != null)
                    {
                        for (int n : ns)
                        {
                            nameList.add(this.db.getXclsName(XClsType.XGNet, n));
                        }
                    }
                }
            }
        }
        this.wizardPageBase = new WizardPage(gridPane, "基本信息", "设置网络类的名称等基本信息。");
        this.wizardPageBase.setOnPageValidating(event ->
        {
            if (event.getDirection() == Direction.Forward)
            {
                String errorMsg = "";
                String clsName = this.textFieldName.getText();
                if (XString.isNullOrEmpty(clsName))
                {
                    errorMsg = "网络类名称不能为空。";
                } else if (this.db != null && this.db.xClsIsExist(XClsType.XGNet, clsName) > 0)
                {
                    errorMsg = String.format("当前数据库中已经存在名为%s的网络类。", clsName);
                }

                event.setValid(errorMsg == "");
                if (!event.isValid())
                {
                    Notification.showInformation(getCurrentWindow(), "基本信息", errorMsg);
                    this.textFieldName.requestFocus();
                }
            }
        });
        //endregion

        //region Page-网络层信息

        //region 按钮
        Button buttonLayerAdd = new Button("", new ImageView(new Image(getClass().getResourceAsStream("/add_16.png"))));
        buttonLayerAdd.setTooltip(new Tooltip("添加层"));
        Button buttonLayerRemove = new Button("", new ImageView(new Image(getClass().getResourceAsStream("/remove_16.png"))));
        buttonLayerRemove.setTooltip(new Tooltip("移除最后一层"));
        buttonLayerAdd.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        buttonLayerRemove.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        // 增加层
        buttonLayerAdd.setOnAction(event ->
        {
            int layerNo = this.tvLayer.getItems().size();
            List<String> list = new ArrayList<String>();
            for (NLayer nl : this.tvLayer.getItems())
            {
                list.add(nl.getName());
            }
            while (list.contains("Layer" + (++layerNo))) ;//遍历层名称，使新添加的默认名不重名
            this.tvLayer.getItems().add(new NLayer("Layer" + layerNo, 1));
        });
        // 移除层
        buttonLayerRemove.setOnAction(event ->
        {
            this.tvLayer.getItems().remove(this.tvLayer.getItems().size() - 1);
        });

        VBox vBoxLayerButton = new VBox(6, buttonLayerAdd, buttonLayerRemove);
        vBoxLayerButton.setPadding(new Insets(24, 0, 0, 0));
        //endregion

        //region 层表
        TableColumn<NLayer, String> tcLayerName = new TableColumn<>("层名");
        TableColumn<NLayer, Integer> tcLayerType = new TableColumn<>("建网策略");
        this.tvLayer.getColumns().addAll(tcLayerName, tcLayerType);
        this.tvLayer.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tcLayerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcLayerType.setCellValueFactory(new PropertyValueFactory<>("type"));

        this.tvLayer.setEditable(true);
        tcLayerName.setCellFactory(param -> new TableCell<NLayer, String>()
        {
            private final TextField textField = new TextField();

            @Override
            public void startEdit()
            {
                super.startEdit();

                textField.setText(getItem());
                setGraphic(textField);
                textField.requestFocus();

                textField.textProperty().addListener((o, ov, nv) ->
                {
                    StringProperty errorMsg = new SimpleStringProperty();
                    if (!XString.isTextValid(nv, 20, GISDefaultValues.getInvalidNameCharList(), errorMsg))
                    {
                        UIFunctions.showErrorTip(textField, errorMsg.get(), tooltipError);
                        textField.setText(ov);
                    }
                });
                //按Enter键完成修改。
                textField.setOnKeyPressed(event ->
                {
                    if (KeyCode.ENTER.equals(event.getCode()))
                    {
                        tvLayer.requestFocus();
                    }
                });

                textField.focusedProperty().addListener((o, ov, nv) ->
                {
                    if (!nv)
                    {
                        String errorMsg = "";
                        if (XString.isNullOrEmpty(textField.getText()))
                        {
                            errorMsg = "层名称不能为空。";
                        } else
                        {
                            NLayer netLayer = (NLayer) getTableRow().getItem();
                            if (netLayer != null)
                            {
                                for (NLayer nl : getTableView().getItems())
                                {
                                    if (!nl.equals(netLayer) && nl.getName().equals(textField.getText()))
                                    {
                                        errorMsg = "与已有层重名。";
                                        break;
                                    }
                                }
                            }
                        }

                        if (!XString.isNullOrEmpty(errorMsg))
                        {
                            UIFunctions.showErrorTip(textField, errorMsg, tooltipError);
                        } else
                        {
                            commitEdit(textField.getText());
                        }
                    }
                });
            }

            @Override
            public void commitEdit(String newValue)
            {
                super.commitEdit(newValue);
                NLayer nLayer = (NLayer) getTableRow().getItem();
                if (nLayer != null)
                {
                    nLayer.setName(newValue);
                    updateItem(newValue, false);
                }
            }

            @Override
            protected void updateItem(String item, boolean empty)
            {
                super.updateItem(item, empty);
                Label label = null;
                if (!empty)
                {
                    label = new Label(item);
                }
                this.setGraphic(label);
            }
        });
        tcLayerType.setCellFactory(param -> new TableCell<NLayer, Integer>()
        {
            private ZDComboBox<Integer> comboBox = new ZDComboBox<>(FXCollections.observableArrayList(1, 2));
            private Integer editValue = null;

            @Override
            public void startEdit()
            {
                super.startEdit();
                NLayer netLayer = (NLayer) getTableRow().getItem();
                if (netLayer != null)
                {
                    comboBox.setValue(getItem());
                    comboBox.prefWidthProperty().bind(getTableColumn().widthProperty());
                    comboBox.setConverter(new StringConverter<Integer>()
                    {
                        @Override
                        public String toString(Integer object)
                        {
                            return netTypeMap.get(object);
                        }

                        @Override
                        public Integer fromString(String string)
                        {
                            return LanguageConvert.getKey(netTypeMap, string);
                        }
                    });
                    comboBox.setOnShown(event -> editValue = comboBox.getValue());
                    comboBox.setOnHidden(event ->
                    {
                        if (!editValue.equals(comboBox.getValue()))
                        {
                            commitEdit(comboBox.getValue());
                        } else
                        {
                            cancelEdit();
                        }
                    });
                    comboBox.focusedProperty().addListener((o, ov, nv) ->
                    {
                        if (!nv)
                        {
                            cancelEdit();
                        }
                    });
                    this.setGraphic(comboBox);
                    comboBox.requestFocus();
                }
            }

            @Override
            public void commitEdit(Integer newValue)
            {
                super.commitEdit(newValue);
                NLayer netLayer = (NLayer) getTableRow().getItem();
                if (netLayer != null)
                {
                    netLayer.setType(newValue);
                    updateItem(newValue, false);
                }
            }

            @Override
            public void cancelEdit()
            {
                super.cancelEdit();
                updateItem(getItem(), false);
            }

            @Override
            protected void updateItem(Integer item, boolean empty)
            {
                super.updateItem(item, empty);
                Label label = null;
                if (!empty)
                {
                    label = new Label(netTypeMap.get(item));
                }
                this.setGraphic(label);
            }
        });

        this.tvLayer.getItems().addListener(new ListChangeListener<NLayer>()
        {
            @Override
            public void onChanged(Change<? extends NLayer> c)
            {
                c.next();
                if (c.wasAdded())
                {
                    int no = tvLayerSettings.getColumns().size() - 2;
                    TableColumn<NLayerSettings, Boolean> tcLayerNo = new TableColumn<>("层" + no);
                    tvLayerSettings.getColumns().add(tcLayerNo);
                    for (NLayerSettings nls : tvLayerSettings.getItems())
                    {
                        nls.getLayerList().add(false);
                    }

                    tcLayerNo.setCellValueFactory(param ->
                    {
                        int index = param.getTableView().getColumns().indexOf(param.getTableColumn()) - 3;
                        return new SimpleBooleanProperty(param.getValue().getLayerList().get(index));
                    });
                    tcLayerNo.setCellFactory(param -> new TableCell<NLayerSettings, Boolean>()
                    {
                        @Override
                        protected void updateItem(Boolean item, boolean empty)
                        {
                            super.updateItem(item, empty);
                            int index = getTableView().getColumns().indexOf(getTableColumn()) - 3;
                            if (!empty && item != null)
                            {
                                CheckBox checkBox = new CheckBox();
                                checkBox.setSelected(item);
                                checkBox.selectedProperty().addListener((o, ov, nv) ->
                                {
                                    NLayerSettings nls = (NLayerSettings) getTableRow().getItem();
                                    nls.getLayerList().set(index, nv);

                                    //选择参与的简单要素类，点可以属于两层，线只属于一层
                                    if (nv)
                                    {
                                        GeomType geoType = (GeomType) nls.getGeoType();
                                        if (geoType.equals(GeomType.GeomLin))
                                        {
                                            if (tvLayer.getItems().size() > 1)
                                            {
                                                for (int i = 0; i < nls.getLayerList().size(); i++)
                                                {
                                                    if (i != index)
                                                    {
                                                        nls.getLayerList().set(i, false);
                                                    }
                                                }
                                            }
                                        } else if (geoType.equals(GeomType.GeomPnt))
                                        {
                                            if (tvLayer.getItems().size() > 2)
                                            {
                                                int count = 0;
                                                for (int i = 0; i < nls.getLayerList().size(); i++)
                                                {
                                                    if (i != index)
                                                    {
                                                        if (nls.getLayerList().get(i))
                                                        {
                                                            count++;
                                                        }
                                                        if (count == 2)
                                                        {
                                                            nls.getLayerList().set(i, false);
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                                this.setGraphic(checkBox);
                                this.setAlignment(Pos.CENTER);
                            }
                        }
                    });
                } else if (c.wasRemoved())
                {
                    tvLayerSettings.getColumns().remove(tvLayerSettings.getColumns().size() - 1);
                    for (NLayerSettings nls : tvLayerSettings.getItems())
                    {
                        nls.getLayerList().remove(nls.getLayerList().size() - 1);
                    }
                }
                buttonLayerRemove.setDisable(tvLayer.getItems().size() == 1);
            }
        });

        HBox hBoxLayer = new HBox(6, new Label("层信息管理:"), this.tvLayer, vBoxLayerButton);
        hBoxLayer.setMinHeight(130);
        hBoxLayer.setMaxHeight(130);
        HBox.setHgrow(this.tvLayer, Priority.ALWAYS);
        //endregion

        //region 层详细设置表
        TableColumn<NLayerSettings, String> tcLSSFCls = new TableColumn<>("简单要素类");
        TableColumn<NLayerSettings, GeomType> tcLSType = new TableColumn<>("类型");
        TableColumn<NLayerSettings, ComplexTypeExt> tcLSLinkMode = new TableColumn<>("几何连通策略");
        this.tvLayerSettings.getColumns().addAll(tcLSSFCls, tcLSType, tcLSLinkMode);
        for (int i = 0; i < this.tvLayer.getItems().size(); i++)
        {
            TableColumn<NLayerSettings, Boolean> tcLayerNo = new TableColumn<>("层" + String.valueOf(i + 1));
            this.tvLayerSettings.getColumns().add(tcLayerNo);
        }

        this.tvLayerSettings.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tcLSType.setPrefWidth(50);
        tcLSType.setResizable(false);

        tcLSSFCls.setCellValueFactory(new PropertyValueFactory<>("sfCls"));
        tcLSType.setCellValueFactory(new PropertyValueFactory<>("geoType"));
        tcLSLinkMode.setCellValueFactory(new PropertyValueFactory<>("linkMode"));

        this.tvLayerSettings.setEditable(true);
        tcLSSFCls.setEditable(false);
        tcLSType.setEditable(false);
        tcLSType.setCellFactory(param -> new TableCell<NLayerSettings, GeomType>()
        {
            @Override
            protected void updateItem(GeomType item, boolean empty)
            {
                super.updateItem(item, empty);
                Label label = null;
                if (!empty && item != null)
                {
                    label = new Label(LanguageConvert.geomTypeConvert(item));
                }
                this.setGraphic(label);
            }
        });
        tcLSLinkMode.setCellFactory(param -> new TableCell<NLayerSettings, ComplexTypeExt>()
        {
            private ZDComboBox<ComplexTypeExt> comboBox = new ZDComboBox<>();
            private ComplexTypeExt editValue;

            @Override
            public void startEdit()
            {
                super.startEdit();
                NLayerSettings netLayer = (NLayerSettings) getTableRow().getItem();
                if (netLayer != null)
                {
                    if (comboBox.getItems().size() == 0)
                    {
                        GeomType geoType = (GeomType) netLayer.getGeoType();
                        if (geoType == GeomType.GeomLin)
                        {
                            comboBox.getItems().addAll(ComplexTypeExt.EndPntSnapEdge, ComplexTypeExt.VertexSnapEdge);
                        } else if (geoType == GeomType.GeomPnt)
                        {
                            comboBox.getItems().addAll(ComplexTypeExt.HonerNode, ComplexTypeExt.OverRideNode, ComplexTypeExt.ComplexNode);
                        }
                    }
                    comboBox.setConverter(new StringConverter<ComplexTypeExt>()
                    {
                        @Override
                        public String toString(ComplexTypeExt object)
                        {
                            return LanguageConvert.complexTypeExtConvert(object);
                        }

                        @Override
                        public ComplexTypeExt fromString(String string)
                        {
                            return LanguageConvert.complexTypeExtConvert(string);
                        }
                    });
                    comboBox.setOnShown(event -> editValue = comboBox.getValue());
                    comboBox.setOnHidden(event ->
                    {
                        if (editValue != comboBox.getValue())
                        {
                            commitEdit(comboBox.getValue());
                        } else
                        {
                            cancelEdit();
                        }
                    });
                    comboBox.focusedProperty().addListener((o, ov, nv) ->
                    {
                        if (!nv)
                        {
                            cancelEdit();
                        }
                    });
                    comboBox.setValue(getItem());
                    comboBox.prefWidthProperty().bind(getTableColumn().widthProperty());
                    this.setGraphic(comboBox);
                    comboBox.requestFocus();
                }
            }

            @Override
            public void commitEdit(ComplexTypeExt newValue)
            {
                super.commitEdit(newValue);
                NLayerSettings netLayer = (NLayerSettings) getTableRow().getItem();
                if (netLayer != null)
                {
                    netLayer.setLinkMode(newValue);
                    updateItem(newValue, false);
                }
            }

            @Override
            public void cancelEdit()
            {
                super.cancelEdit();
                updateItem(getItem(), false);
            }

            @Override
            protected void updateItem(ComplexTypeExt item, boolean empty)
            {
                super.updateItem(item, empty);
                Label label = null;
                if (!empty)
                {
                    label = new Label(LanguageConvert.complexTypeExtConvert(item));
                }
                this.setGraphic(label);
            }
        });

        HBox hBoxLayerSettings = new HBox(6, new Label("层详细设置:"), this.tvLayerSettings);
        HBox.setHgrow(this.tvLayerSettings, Priority.ALWAYS);
        //endregion

        this.wizardPageLayer = new WizardPage(new VBox(6, hBoxLayer, hBoxLayerSettings), "网络层信息", "设置网络类的网络层及其详细信息。");
        this.wizardPageLayer.setOnPageValidating(event ->
        {
            if (event.getDirection() == Direction.Forward)
            {
                boolean hasLin = this.checkBoxJustFrame.isSelected();
                if (!hasLin)
                {
                    for (NLayerSettings nls : this.tvLayerSettings.getItems())
                    {
                        GeomType geoType = (GeomType) nls.getGeoType();
                        if (geoType == GeomType.GeomLin)
                        {
                            for (boolean val : nls.getLayerList())
                            {
                                if (val)
                                {
                                    hasLin = true;
                                    break;
                                }
                            }
                        }
                    }
                }

                if (!hasLin)
                {
                    Notification.showInformation(getCurrentWindow(), "网络层信息", "至少需要选择一个线简单要素类参与建网。");
                }

                event.setValid(hasLin);
                if (!event.isValid())
                {
                    if (this.tvLayer.getItems().size() == 1 && (int) this.tvLayer.getItems().get(0).getType() == 1 && this.tvLayer.getItems().get(0).getName() != "Layer1")
                    {
                        this.tvLayer.getItems().get(0).setName("Layer1");
                        UIFunctions.showErrorTip(this.tvLayer, "当新建的网络类只有一层,且为几何建网时,层名将会被默认修改为\"Layer1\"。", this.tooltipError);
                    }
                }
            }
        });
        //endregion

        //region Page-网络权信息
        //region 按钮
        Button buttonWeightAdd = new Button("", new ImageView(new Image(getClass().getResourceAsStream("/add_16.png"))));
        Button buttonWeightRemove = new Button("", new ImageView(new Image(getClass().getResourceAsStream("/remove_16.png"))));
        buttonWeightAdd.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        buttonWeightRemove.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        // 增加权
        buttonWeightAdd.setOnAction(event ->
        {
            int weightNo = this.tvWeight.getItems().size() - 3;
            List<String> list = new ArrayList<>();
            for (NWeight nw : this.tvWeight.getItems())
            {
                list.add(nw.getName());
            }
            while (list.contains("Weight" + (++weightNo))) ;//遍历网络权名称，使新添加的默认名不重名
            this.tvWeight.getItems().add(new NWeight("Weight" + weightNo, WgtType.ratioType, 1, 1, true, this.initWeightFieldsInfo()));
            this.tvWeight.getSelectionModel().select(this.tvLayer.getItems().size() - 1);
        });

        // 删除权
        buttonWeightRemove.setOnAction(event ->
        {
            NWeight netWeight = this.tvWeight.getSelectionModel().getSelectedItem();
            if (netWeight != null)
            {
                this.tvWeight.getItems().remove(netWeight);
            }
        });
        VBox vBoxWeightButton = new VBox(6, buttonWeightAdd, buttonWeightRemove);
        vBoxWeightButton.setPadding(new Insets(24, 0, 0, 0));
        //endregion

        //region 网络权表
        TableColumn<NWeight, String> tcWeightName = new TableColumn<>("名称");
        TableColumn<NWeight, WgtType> tcWeightType = new TableColumn<>("类型");
        TableColumn<NWeight, Integer> tcWeightDataType = new TableColumn<>("数据类型");
        TableColumn<NWeight, Integer> tcWeightSize = new TableColumn<>("位数");
        this.tvWeight.getColumns().addAll(tcWeightName, tcWeightType, tcWeightDataType, tcWeightSize);
        this.tvWeight.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tcWeightName.setSortable(false);
        tcWeightType.setSortable(false);
        tcWeightDataType.setSortable(false);
        tcWeightSize.setSortable(false);
        tcWeightName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcWeightType.setCellValueFactory(new PropertyValueFactory<>("type"));
        tcWeightDataType.setCellValueFactory(new PropertyValueFactory<>("dataType"));
        tcWeightSize.setCellValueFactory(new PropertyValueFactory<>("bitSize"));

        this.tvWeight.setEditable(true);
        tcWeightName.setCellFactory(param -> new TableCell<NWeight, String>()
        {
            private final TextField textField = new TextField();

            @Override
            public void startEdit()
            {
                super.startEdit();

                textField.setText(getItem());
                setGraphic(textField);
                textField.requestFocus();

                textField.textProperty().addListener((o, ov, nv) ->
                {
                    StringProperty errorMsg = new SimpleStringProperty();
                    if (!XString.isTextValid(nv, 128, GISDefaultValues.getInvalidNameCharList(), errorMsg))
                    {
                        UIFunctions.showErrorTip(textField, errorMsg.get(), tooltipError);
                        textField.setText(ov);
                    }
                });
                //按Enter键完成修改。
                textField.setOnKeyPressed(event ->
                {
                    if (KeyCode.ENTER.equals(event.getCode()))
                    {
                        tvLayer.requestFocus();
                    }
                });

                textField.focusedProperty().addListener((o, ov, nv) ->
                {
                    if (!nv)
                    {
                        commitEdit(textField.getText());
                    }
                });
            }

            @Override
            public void commitEdit(String newValue)
            {
                String errorMessage = "";
                if (XString.isNullOrEmpty(getText()))
                {
                    errorMessage = "网络权名称不能为空。";
                } else
                {
                    NWeight netWeight = (NWeight) getTableRow().getItem();
                    if (netWeight != null)
                    {
                        for (NWeight nw : tvWeight.getItems())
                        {
                            if (!nw.equals(netWeight))
                            {
                                if (nw.getName() == newValue)
                                {
                                    errorMessage = "与已有网络权重名。";
                                    break;
                                }
                            }
                        }
                    }
                }

                if (XString.isNullOrEmpty(errorMessage))
                {
                    UIFunctions.showErrorTip(textField, errorMessage, tooltipError);
                } else
                {
                    super.commitEdit(newValue);
                }
            }

            @Override
            protected void updateItem(String item, boolean empty)
            {
                super.updateItem(item, empty);
                Label label = null;
                if (!empty)
                {
                    label = new Label(item);
                }
                this.setGraphic(label);
            }
        });
        tcWeightType.setCellFactory(param -> new TableCell<NWeight, WgtType>()
        {
            private final ZDComboBox<WgtType> comboBox = new ZDComboBox<>(WgtType.ratioType, WgtType.absoluteType);
            private WgtType editValue;

            @Override
            public void startEdit()
            {
                super.startEdit();
                NWeight netWeight = (NWeight) getTableRow().getItem();
                if (netWeight != null)
                {
                    comboBox.setConverter(new StringConverter<WgtType>()
                    {
                        @Override
                        public String toString(WgtType object)
                        {
                            return weightTypeMap.get(object);
                        }

                        @Override
                        public WgtType fromString(String string)
                        {
                            return LanguageConvert.getKey(weightTypeMap, string);
                        }
                    });
                    comboBox.setOnShown(event -> editValue = comboBox.getValue());
                    comboBox.setOnHidden(event ->
                    {
                        if (editValue != comboBox.getValue())
                        {
                            commitEdit(comboBox.getValue());
                        } else
                        {
                            cancelEdit();
                        }
                    });
                    comboBox.focusedProperty().addListener((o, ov, nv) ->
                    {
                        if (!nv)
                        {
                            cancelEdit();
                        }
                    });
                    comboBox.setValue(getItem());
                    comboBox.prefWidthProperty().bind(getTableColumn().widthProperty());
                    this.setGraphic(comboBox);
                    comboBox.requestFocus();
                }
            }

            @Override
            public void commitEdit(WgtType newValue)
            {
                super.commitEdit(newValue);
                NWeight netWeight = (NWeight) getTableRow().getItem();
                if (netWeight != null)
                {
                    netWeight.setType(newValue);
                    updateItem(newValue, false);
                }
            }

            @Override
            public void cancelEdit()
            {
                super.cancelEdit();
                updateItem(getItem(), false);
            }

            @Override
            protected void updateItem(WgtType item, boolean empty)
            {
                super.updateItem(item, empty);
                Label label = null;
                if (!empty)
                {
                    label = new Label(weightTypeMap.get(item));
                }
                this.setGraphic(label);
            }
        });
        tcWeightDataType.setCellFactory(param -> new TableCell<NWeight, Integer>()
        {
            private ZDComboBox<Integer> comboBox = new ZDComboBox(1, 2, 3, 4, 5);
            private Integer editValue;

            @Override
            public void startEdit()
            {
                super.startEdit();
                NWeight netWeight = (NWeight) getTableRow().getItem();
                if (netWeight != null)
                {
                    comboBox.setConverter(new StringConverter<Integer>()
                    {
                        @Override
                        public String toString(Integer object)
                        {
                            return dataTypeMap.get(object);
                        }

                        @Override
                        public Integer fromString(String string)
                        {
                            return LanguageConvert.getKey(dataTypeMap, string);
                        }
                    });
                    comboBox.setOnShown(event -> editValue = comboBox.getValue());
                    comboBox.setOnHidden(event ->
                    {
                        if (editValue != comboBox.getValue())
                        {
                            commitEdit(comboBox.getValue());
                        } else
                        {
                            cancelEdit();
                        }
                    });
                    comboBox.focusedProperty().addListener((o, ov, nv) ->
                    {
                        if (!nv)
                        {
                            cancelEdit();
                        }
                    });
                    comboBox.setValue(getItem());
                    comboBox.prefWidthProperty().bind(getTableColumn().widthProperty());
                    this.setGraphic(comboBox);
                    comboBox.requestFocus();
                }
            }

            @Override
            public void commitEdit(Integer newValue)
            {
                super.commitEdit(newValue);
                NWeight netWeight = (NWeight) getTableRow().getItem();
                if (netWeight != null)
                {
                    netWeight.setDataType(newValue);
                    for (NWeightField nwf : tvWeightField.getItems())
                    {
                        nwf.setBindField("(无)");
                    }
                    updateItem(newValue, false);
                }
            }

            @Override
            public void cancelEdit()
            {
                super.cancelEdit();
                updateItem(getItem(), false);
            }

            @Override
            protected void updateItem(Integer item, boolean empty)
            {
                super.updateItem(item, empty);
                Label label = null;
                if (!empty)
                {
                    label = new Label(dataTypeMap.get(item));
                }
                this.setGraphic(label);
            }
        });
        tcWeightSize.setCellFactory(param -> new TableCell<NWeight, Integer>()
        {
            private final TextField textField = UIFunctions.newIntTextField(true);

            @Override
            public void startEdit()
            {
                super.startEdit();
                NWeight netWeight = (NWeight) getTableRow().getItem();
                if (netWeight != null)
                {
                    textField.setText(String.valueOf(getItem()));
                    setGraphic(textField);
                    textField.requestFocus();

                    textField.textProperty().addListener((o, ov, nv) ->
                    {
                        double val = Double.valueOf(nv);
                        if (val > 32 || val < 1)
                        {
                            textField.setText(ov);
                        } else
                        {
                            UIFunctions.showErrorTip(textField, "网络权位数的范围为[1,32]。", tooltipError);
                        }
                    });
                    //按Enter键完成修改。
                    textField.setOnKeyPressed(event ->
                    {
                        if (KeyCode.ENTER.equals(event.getCode()))
                        {
                            tvLayer.requestFocus();
                        }
                    });

                    textField.focusedProperty().addListener((o, ov, nv) ->
                    {
                        if (!nv)
                        {
                            commitEdit(Integer.valueOf(textField.getText()));
                        }
                    });
                }
            }

            @Override
            public void commitEdit(Integer newValue)
            {
                super.commitEdit(newValue);
                NWeight netWeight = (NWeight) getTableRow().getItem();
                if (netWeight != null)
                {
                    netWeight.setBitSize(newValue);
                    updateItem(newValue, false);
                }
            }

            @Override
            protected void updateItem(Integer item, boolean empty)
            {
                super.updateItem(item, empty);
                Label label = null;
                if (!empty)
                {
                    label = new Label(String.valueOf(item));
                }
                this.setGraphic(label);
            }
        });

        this.tvWeight.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) ->
        {
            this.tvWeightField.getItems().clear();
            LinkedHashMap<String, String> fldsMap = null;
            NWeight netWeight = this.tvWeight.getSelectionModel().getSelectedItem();
            int index = -1;
            if (netWeight != null)
            {
                fldsMap = netWeight.getFieldsMap();
                index = this.tvWeight.getItems().indexOf(netWeight);
            }
            buttonWeightRemove.setDisable(index >= 0 && index < 3);
            this.tvWeight.setEditable(index >= 3);

            if (this.tvWeightField.getItems().size() == 0)
            {
                for (NLayerSettings nls : this.tvLayerSettings.getItems())
                {
                    boolean joinNet = false;
                    for (boolean b : nls.getLayerList())
                    {
                        if (b)
                        {
                            joinNet = true;
                            break;
                        }
                    }
                    if (joinNet)
                    {
                        if (this.db != null)
                        {
                            SFeatureCls sfCls = new SFeatureCls(this.db);
                            Fields flds = null;
                            if (sfCls.open(nls.getClsID(), 0) > 0)
                            {
                                flds = sfCls.getFields();
                                sfCls.close();
                            }
                            this.tvWeightField.getItems().add(new NWeightField(nls.getSfCls(),  nls.getGeoType(), fldsMap.containsKey(nls.getSfCls()) ? fldsMap.get(nls.getSfCls()) : "(无)", flds));
                        }
                    } else
                    {
                        if (fldsMap != null)
                        {
                            fldsMap.put(nls.getSfCls(), "(无)");
                        }
                    }
                }
            } else
            {
                LinkedHashMap<String, String> hmap = netWeight.getFieldsMap();
                if (hmap != null)
                {
                    for (NWeightField nwf : this.tvWeightField.getItems())
                    {
                        nwf.setBindField(hmap.get(nwf.getSfCls()));
                    }
                }
            }
        });

        HBox hBoxWeight = new HBox(6, new Label("网络权设置:"), this.tvWeight, vBoxWeightButton);
        HBox.setHgrow(this.tvWeight, Priority.ALWAYS);
        //endregion

        //region 绑定字段表
        TableColumn<NWeightField, String> tcWFSFCls = new TableColumn<>("建网简单要素类");
        TableColumn<NWeightField, Object> tcWFType = new TableColumn<>("类型");
        TableColumn<NWeightField, String> tcWFBindFields = new TableColumn<>("绑定字段");
        this.tvWeightField.getColumns().addAll(tcWFSFCls, tcWFType, tcWFBindFields);
        this.tvWeightField.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tcWFType.setPrefWidth(50);
        tcWFType.setResizable(false);

        tcWFSFCls.setCellValueFactory(new PropertyValueFactory<>("sfCls"));
        tcWFType.setCellValueFactory(new PropertyValueFactory<>("geoType"));
        tcWFBindFields.setCellValueFactory(new PropertyValueFactory<>("bindFields"));

        this.tvWeightField.setEditable(true);
        tcWFSFCls.setEditable(false);
        tcWFType.setEditable(false);
        tcWFType.setCellFactory(param -> new TableCell<NWeightField, Object>()
        {
            @Override
            protected void updateItem(Object item, boolean empty)
            {
                super.updateItem(item, empty);
                Label label = null;
                if (!empty)
                {
                    label = new Label(LanguageConvert.geomTypeConvert((GeomType) item));
                }
                this.setGraphic(label);
            }
        });
        tcWFBindFields.setCellFactory(param -> new TableCell<NWeightField, String>()
        {
            private ZDComboBox<String> comboBox = new ZDComboBox<>();
            private String editValue;

            @Override
            public void startEdit()
            {
                super.startEdit();

                NWeightField weightField = (NWeightField) getTableRow().getItem();
                NWeight weight = tvWeight.getSelectionModel().getSelectedItem();
                if (weightField != null && weight != null)
                {
                    //region 添加字段下拉
                    if (comboBox.getItems().size() == 0)
                    {
                        Fields flds = weightField.getFields();
                        if (!weight.isEditable())
                        {
                            for (short i = 0; i < flds.getFieldCount(); i++)
                            {
                                Field fld = flds.getField(i);
                                if (fld != null)
                                {
                                    comboBox.getItems().add(fld.getFieldName());
                                }
                            }
                        } else
                        {
                            int wdType = weight.getDataType();
                            for (short i = 0; i < flds.getFieldCount(); i++)
                            {
                                Field fld = flds.getField(i);
                                Field.FieldType fldType = fld.getFieldType();
                                switch (wdType)
                                {
                                    case 1://长整型
                                    {
                                        if (fldType.equals(Field.FieldType.fldShort) || fldType.equals(Field.FieldType.fldByte) || fldType.equals(Field.FieldType.fldBool) || fldType.equals(Field.FieldType.fldLong))
                                        {
                                            comboBox.getItems().add(fld.getFieldName());
                                        }
                                        break;
                                    }
                                    case 2://短整数型
                                    {
                                        if (fldType.equals(Field.FieldType.fldShort) || fldType.equals(Field.FieldType.fldByte) || fldType.equals(Field.FieldType.fldBool))
                                        {
                                            comboBox.getItems().add(fld.getFieldName());
                                        }
                                        break;
                                    }
                                    case 3://浮点型
                                    {
                                        if (fldType.equals(Field.FieldType.fldFloat))
                                        {
                                            comboBox.getItems().add(fld.getFieldName());
                                        }
                                        break;
                                    }
                                    case 4://双精度型
                                    {
                                        if (fldType.equals(Field.FieldType.fldFloat) || fldType.equals(Field.FieldType.fldDouble))
                                        {
                                            comboBox.getItems().add(fld.getFieldName());
                                        }
                                        break;
                                    }
                                    case 5://32位二进制型
                                    {
                                        if (fldType.equals(Field.FieldType.fldLong))
                                        {
                                            comboBox.getItems().add(fld.getFieldName());
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                        comboBox.getItems().add(0, "(无)");
                    }
                    //endregion

                    comboBox.setOnShown(event -> editValue = comboBox.getValue());
                    comboBox.setOnHidden(event ->
                    {
                        if (editValue != comboBox.getValue())
                        {
                            commitEdit(comboBox.getValue());
                        } else
                        {
                            cancelEdit();
                        }
                    });
                    comboBox.focusedProperty().addListener((o, ov, nv) ->
                    {
                        if (!nv)
                        {
                            cancelEdit();
                        }
                    });
                    if (!XString.isNullOrEmpty(getItem()))
                    {
                        comboBox.setValue(getItem());
                    } else
                    {
                        comboBox.getSelectionModel().select(0);
                    }
                    comboBox.prefWidthProperty().bind(getTableColumn().widthProperty());
                    this.setGraphic(comboBox);
                    comboBox.requestFocus();
                }
            }

            @Override
            public void commitEdit(String newValue)
            {
                super.commitEdit(newValue);
                NWeightField weightField = (NWeightField) getTableRow().getItem();
                NWeight weight = tvWeight.getSelectionModel().getSelectedItem();
                if (weightField != null && weight != null)
                {
                    weightField.setBindField(newValue);
                    weight.getFieldsMap().put(weightField.getSfCls(), newValue);
                    updateItem(newValue, false);
                }
            }

            @Override
            public void cancelEdit()
            {
                super.cancelEdit();
                updateItem(getItem(), false);
            }

            @Override
            protected void updateItem(String item, boolean empty)
            {
                super.updateItem(item, empty);
                Label label = null;
                if (!empty)
                {
                    label = new Label(item);
                }
                this.setGraphic(label);
            }
        });

        HBox hBoxWeightField = new HBox(6, new Label("绑定字段:   "), this.tvWeightField);
        HBox.setHgrow(this.tvWeightField, Priority.ALWAYS);
        //endregion

        this.wizardPageWeight = new WizardPage(new VBox(6, hBoxWeight, hBoxWeightField), "网络权信息", "设置网络类的网络权及其绑定字段信息。");
        //endregion

        this.wizard = new Wizard(this, this.wizardPageBase, this.wizardPageLayer, this.wizardPageWeight);
        this.wizard.setPrefSize(800, 600);
        this.setDialogPane(this.wizard);

        //region 初始化界面信息
        this.tvLayer.getItems().addAll(new NLayer("Layer1", 1));
        if (this.db != null)
        {
            int[] sfClses = this.db.getXclses(XClsType.XSFCls, dsID);
            if (sfClses != null)
            {
                for (int sfClsID : sfClses)
                {
                    SFClsInfo sfClsInfo = (SFClsInfo) db.getXclsInfo(XClsType.XSFCls, sfClsID);
                    if (sfClsInfo.getgNetID() <= 0)
                    {
                        GeomType geomType = sfClsInfo.getfType();
                        if (geomType.equals(geomType.GeomPnt) || geomType.equals(GeomType.GeomLin))
                        {
                            ComplexTypeExt cType = geomType.equals(GeomType.GeomPnt) ? ComplexTypeExt.HonerNode : ComplexTypeExt.EndPntSnapEdge;
                            this.tvLayerSettings.getItems().add(new NLayerSettings(sfClsInfo.getName(), sfClsID, geomType, cType, true));
                        }
                    }
                }
            }

            this.tvWeight.getItems().addAll(new NWeight("网络需求", null, 0, 1),
                    new NWeight("指示流向", null, 0, 1),
                    new NWeight("使能状态", null, 0, 1));

            this.tvWeight.getSelectionModel().select(0);
        }
        //endregion

        this.wizard.setOnFinish(event ->
        {
            this.createNetCls();
        });
    }
    //endregion

    //region 公有方法

    /**
     * 新创建的网络类的ID
     *
     * @return 新创建的网络类的ID
     */
    public int getClsID()
    {
        return this.clsID;
    }

    public void createNetCls()
    {
        //region 从界面上读设置
        String nClsName = this.textFieldName.getText();
        double radius = Double.valueOf(this.textFieldRadius.getText());

        //网络权
        NetWeightList nwList = new NetWeightList();
        List<NetWeight> weightList = new ArrayList<>();
        for (int i = 3; i < this.tvWeight.getItems().size(); i++)
        {
            NWeight weight = this.tvWeight.getItems().get(i);
            NetWeight netWeight = new NetWeight();
            netWeight.setWgtName(weight.getName());
            if (weight.getType() != null)
            {
                netWeight.setWgtType(weight.getType());
            }
            netWeight.setDataType(Field.FieldType.fldLong);//封装问题：此处不是FieldType
            netWeight.setBiteSize((short) weight.getBitSize());
            netWeight.setWgtID(weightList.size() + 1);
            weightList.add(netWeight);
            nwList.append(netWeight);
        }

        //网络权字段
        NetWeightFldList nwfList = new NetWeightFldList();
        List<NetWeightFld> weightFldList = new ArrayList<>();
        for (int i = 0; i < this.tvWeight.getItems().size(); i++)
        {
            NWeight weight = this.tvWeight.getItems().get(i);
            int wgtID = (i < 3 ? i - 3 : i - 2);
            LinkedHashMap<String, String> dicFlds = weight.getFieldsMap();

            for (String clsName : dicFlds.keySet())
            {
                String fldName = dicFlds.get(clsName);
                if (fldName != "" && fldName != "(无)")
                {
                    NetWeightFld weightFld = new NetWeightFld();
                    weightFld.setFldName(fldName);
                    weightFld.setWgtID(wgtID);
                    weightFld.setFClsID((int) this.db.xClsIsExist(XClsType.XSFCls, clsName));
                    weightFldList.add(weightFld);
                    nwfList.append(weightFld);
                }
            }
        }
        //endregion

        if (this.tvLayer.getItems().size() == 1 && (int) this.tvLayer.getItems().get(0).getType() == 1)//只有一层，且为几何建网
        {
            NetFeatureList featureList = new NetFeatureList();
            for (NLayerSettings nls : this.tvLayerSettings.getItems())
            {
                if (nls.getLayerList().get(0))
                {
                    NetFeature feature = new NetFeature();
                    feature.setFClsID(nls.getClsID());
                    if (nls.getLinkMode() == ComplexTypeExt.ComplexNode)//复杂点
                    {
                        feature.setAsComplex(true);//ComplexType.ComplexNode；1-复杂点
                    } else if (nls.getLinkMode() == ComplexTypeExt.VertexSnapEdge)//顶点策略
                    {
                        feature.setAsComplex(false);//ComplexType.ComplexEdge; 2-复杂边
                    }
                    featureList.append(feature);
                }
            }
            NetCls netCls = new NetCls(this.db);
            this.clsID = (int) netCls.create(nClsName, radius, true, this.dsID, featureList, nwList, nwfList);
            netCls.close();
        } else
        {
            List<NetLayer> layerList = new ArrayList<>();
            int layerID = 1;
            for (NLayer nl : this.tvLayer.getItems())
            {
                NetLayer nLayer = new NetLayer();
                nLayer.setLayerID(layerID++);
                nLayer.setLayerName(nl.getName());
                nLayer.setBuildNetMode(nl.getType() == 1 ? NetBuildMode.GeomMode : NetBuildMode.AttMode);
                layerList.add(nLayer);
            }

            List<NetFeatureExt> featureExtList = new ArrayList<>();
            for (NLayerSettings nls : this.tvLayerSettings.getItems())
            {
                int num = 0;
                NetFeatureExt featureExt = null;
                for (int i = 0; i < nls.getLayerList().size(); i++)
                {
                    if (nls.getLayerList().get(i))
                    {
                        num++;
                        if (featureExt == null)//此时num=1
                        {
                            featureExt = new NetFeatureExt();
                            int layerType = this.tvLayer.getItems().get(i).getType();
                            featureExt.setFClsID(nls.getClsID());
                            featureExt.setAsComplex(nls.getLinkMode());
                            featureExt.setLayerID(i + 1);
                        }
                        featureExt.setIsConnector(num == 2);
                        featureExt.setConnLayerID((num == 2 ? i - 2 : 0));

                        GeomType geoType = (GeomType) nls.getGeoType();
                        if (geoType == GeomType.GeomLin || (geoType == GeomType.GeomPnt && num == 2))
                        {
                            break;
                        }
                    }
                }
                if (featureExt != null)
                    featureExtList.add(featureExt);
            }
            int changeGeom = 1 + (this.checkBoxJustFrame.isSelected() ? 0 : 2) + (this.checkBoxBuildRel.isSelected() ? 4 : 0);
            NetCls netCls = new NetCls(this.db);
            this.clsID = netCls.createNet(this.dsID, nClsName, radius, changeGeom, layerList.toArray(new NetLayer[0]), featureExtList.toArray(new NetFeatureExt[0]), weightList.toArray(new NetWeight[0]), weightFldList.toArray(new NetWeightFld[0]));
            netCls.close();
        }
        if (this.clsID <= 0)
        {
            MessageBox.information("创建网络类失败。");
        }
    }
    //endregion

    //region 私有方法

    /**
     * 初始化权值的字段信息
     *
     * @return 权值的字段信息(以构网简单要素类名为Key 、 绑定字段名为Value的集合)
     */
    private LinkedHashMap<String, String> initWeightFieldsInfo()
    {
        LinkedHashMap<String, String> fieldsMap = new LinkedHashMap<>();
        for (NLayerSettings nls : this.tvLayerSettings.getItems())
        {
            fieldsMap.put(nls.getSfCls(), "(无)");
        }
        return fieldsMap;
    }
    //endregion

    //region 下拉框整形-文本对应处理
    private static final Map<Integer, String> dataTypeMap = new ImmutableMap.Builder<Integer, String>().
            put(1, "长整型").
            put(2, "短整型").
            put(3, "浮点型").
            put(4, "双精度型").
            put(5, "32位二进制型").build();
    private static final Map<Integer, String> netTypeMap = new ImmutableMap.Builder<Integer, String>().
            put(1, "几何建网").
            put(2, "属性建网").build();
    private static final Map<WgtType, String> weightTypeMap = new ImmutableMap.Builder<WgtType, String>().
            put(WgtType.ratioType, "比例权").
            put(WgtType.absoluteType, "绝对权").build();

    //endregion

    //region 获取Window
    private javafx.stage.Window window;

    /**
     * 获取当前窗口的window对象
     *
     * @return 当前窗口的window对象
     */
    private javafx.stage.Window getCurrentWindow()
    {
        if (this.window == null)
        {
            this.window = this.getDialogPane().getScene().getWindow();
        }
        return this.window;
    }
    //endregion

    //region 自定义表显示类
    public class NLayer
    {
        private StringProperty name = new SimpleStringProperty();
        private IntegerProperty type = new SimpleIntegerProperty();

        public NLayer(String name, int type)
        {
            this.name.set(name);
            this.type.set(type);
        }

        public String getName()
        {
            return name.get();
        }

        public StringProperty nameProperty()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name.set(name);
        }

        public int getType()
        {
            return type.get();
        }

        public IntegerProperty typeProperty()
        {
            return type;
        }

        public void setType(int type)
        {
            this.type.set(type);
        }
    }

    public class NLayerSettings
    {
        private StringProperty sfCls = new SimpleStringProperty();
        private ObjectProperty<GeomType> geoType = new SimpleObjectProperty();
        private ObjectProperty<ComplexTypeExt> linkMode = new SimpleObjectProperty<>();
        private ObservableList<Boolean> layerList = FXCollections.observableArrayList();
        private int clsID;

        public NLayerSettings(String sfCls, int clsID, GeomType gType, ComplexTypeExt linkMode, Boolean... layers)
        {
            this.sfCls.set(sfCls);
            this.clsID = clsID;
            this.geoType.set(gType);
            this.linkMode.set(linkMode);
            this.layerList.addAll(layers);
        }

        public String getSfCls()
        {
            return sfCls.get();
        }

        public StringProperty sfClsProperty()
        {
            return sfCls;
        }

        public void setSfCls(String sfCls)
        {
            this.sfCls.set(sfCls);
        }

        public GeomType getGeoType()
        {
            return geoType.get();
        }

        public ObjectProperty<GeomType> geoTypeProperty()
        {
            return geoType;
        }

        public void setGeoType(GeomType geoType)
        {
            this.geoType.set(geoType);
        }

        public ComplexTypeExt getLinkMode()
        {
            return linkMode.get();
        }

        public ObjectProperty<ComplexTypeExt> linkModeProperty()
        {
            return linkMode;
        }

        public void setLinkMode(ComplexTypeExt linkMode)
        {
            this.linkMode.set(linkMode);
        }

        public ObservableList<Boolean> getLayerList()
        {
            return layerList;
        }

        public void setLayerList(ObservableList<Boolean> layerList)
        {
            this.layerList = layerList;
        }

        public int getClsID()
        {
            return clsID;
        }
    }

    public class NWeight
    {
        private StringProperty name = new SimpleStringProperty();
        private ObjectProperty<WgtType> type = new SimpleObjectProperty(WgtType.ratioType);
        private IntegerProperty dataType = new SimpleIntegerProperty();
        private IntegerProperty bitSize = new SimpleIntegerProperty();
        private LinkedHashMap<String, String> fieldsMap = new LinkedHashMap<>();//记录绑定字段信息.Key为简单要素类名称，Value为绑定字段名
        private boolean editable;//用于将使能状态等三个特殊权显示成灰色

        public NWeight(String name, WgtType type, int dataType, int size)
        {
            this(name, type, dataType, size, false);
        }

        public NWeight(String name, WgtType type, int dataType, int size, boolean editable)
        {
            this(name, type, dataType, size, editable, null);
        }

        public NWeight(String name, WgtType type, int dataType, int size, boolean editable, LinkedHashMap<String, String> fieldsMap)
        {
            this.name.set(name);
            this.type.set(type);
            this.dataType.set(dataType);
            this.bitSize.set(size);
            this.editable = editable;
            if (fieldsMap != null)
            {
                this.fieldsMap = fieldsMap;
            }
        }

        public String getName()
        {
            return name.get();
        }

        public StringProperty nameProperty()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name.set(name);
        }

        public WgtType getType()
        {
            return (WgtType) type.get();
        }

        public ObjectProperty<WgtType> typeProperty()
        {
            return type;
        }

        public void setType(WgtType type)
        {
            this.type.set(type);
        }

        public int getDataType()
        {
            return dataType.get();
        }

        public IntegerProperty dataTypeProperty()
        {
            return dataType;
        }

        public void setDataType(int dataType)
        {
            this.dataType.set(dataType);
            for (String k : this.fieldsMap.keySet())
            {
                this.fieldsMap.put(k, ("无"));
            }
        }

        public int getBitSize()
        {
            return bitSize.get();
        }

        public IntegerProperty bitSizeProperty()
        {
            return bitSize;
        }

        public void setBitSize(int bitSize)
        {
            this.bitSize.set(bitSize);
        }

        public LinkedHashMap<String, String> getFieldsMap()
        {
            return fieldsMap;
        }

        public void setFieldsMap(LinkedHashMap<String, String> fieldsMap)
        {
            this.fieldsMap = fieldsMap;
        }

        public boolean isEditable()
        {
            return editable;
        }

        public void setEditable(boolean style)
        {
            this.editable = style;
        }
    }

    public class NWeightField
    {
        private StringProperty sfCls = new SimpleStringProperty();
        private ObjectProperty<GeomType> geoType = new SimpleObjectProperty();
        private StringProperty bindFields = new SimpleStringProperty();
        private Fields fields;

        public NWeightField(String sfcls, GeomType geomType, String bindFields, Fields flds)
        {
            this.sfCls.set(sfcls);
            this.geoType.set(geomType);
            this.bindFields.set(bindFields);
            this.fields = flds;
        }

        public String getSfCls()
        {
            return sfCls.get();
        }

        public StringProperty sfClsProperty()
        {
            return sfCls;
        }

        public void setSfCls(String sfCls)
        {
            this.sfCls.set(sfCls);
        }

        public GeomType getGeoType()
        {
            return geoType.get();
        }

        public ObjectProperty<GeomType> geoTypeProperty()
        {
            return geoType;
        }

        public void setGeoType(GeomType geoType)
        {
            this.geoType.set(geoType);
        }

        public String getBindFields()
        {
            return bindFields.get();
        }

        public StringProperty bindFieldsProperty()
        {
            return bindFields;
        }

        public void setBindField(String bindField)
        {
            this.bindFields.set(bindField);
        }

        public Fields getFields()
        {
            return fields;
        }

        public void setFields(Fields fields)
        {
            this.fields = fields;
        }
    }
    //endregion
}
