package com.zondy.mapgis.fields;

import com.zondy.mapgis.att.Field;
import com.zondy.mapgis.att.Fields;
import com.zondy.mapgis.att.SubType;
import com.zondy.mapgis.att.SubTypes;
import com.zondy.mapgis.base.*;
import com.zondy.mapgis.common.IntList;
import com.zondy.mapgis.controls.common.ZDComboBox;
import com.zondy.mapgis.controls.common.ZDToolBar;
import com.zondy.mapgis.filedialog.GDBOpenFileDialog;
import com.zondy.mapgis.geodatabase.*;
import com.zondy.mapgis.geodatabase.config.ConnectType;
import com.zondy.mapgis.geodatabase.net.GNetInfo;
import com.zondy.mapgis.geometry.GeomType;
import com.zondy.mapgis.map.SRSItem;
import com.zondy.mapgis.sref.SRefManagerDialog;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.StringConverter;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author CR
 * @file FieldsEditPane.java
 * @brief 属性结构设置界面Pane
 * @create 2019-11-18.
 */
public class FieldsEditPane extends VBox
{
    //region 成员变量
    private IBasCls basCls;   //当前要设置属性结构的数据
    private DataBase dataBase;//当前数据或待创建新数据的数据库
    private Fields fields;    //记录初始化时的字段信息，在多个构造函数中取，在初始化函数中使用
    private final TableView<EditField> tableView = new TableView<>();//字段编辑表格
    private String errorMessage = "";//记录字段名称错误消息，在字段名称列编辑器的Validing后显示出来
    private boolean canEdit = true;     //标记是否允许编辑
    private boolean is6xData = false;   //记录当前编辑属性结构的数据是否是6x数据
    private boolean calcDefFieldsAdded = false;//标记是否添加了需要计算的默认字段（线的mpLength、区的mpArea和mpPerimeter）
    private List<String> readonlyFields;    //禁止在属性结构设置中编辑的字段名称集合
    private Tooltip tooltipError = new Tooltip();
    private BooleanProperty canAddDefaults = new SimpleBooleanProperty(true);

    //endregion

    //region 构造函数

    /**
     * 构造对话框对象，用于类属性结构的修改和浏览
     *
     * @param basCls 需设置或浏览属性结构的数据类
     */
    public FieldsEditPane(IBasCls basCls)
    {
        this(basCls, true);
    }

    /**
     * 构造对话框对象，用于类属性结构的修改和浏览
     *
     * @param basCls  需设置或浏览属性结构的数据类
     * @param canEdit 是否能编辑，类在被打开着的时候可能会无法编辑，而只能浏览
     */
    public FieldsEditPane(IBasCls basCls, boolean canEdit)
    {
        this(basCls, null);
        this.canEdit = canEdit;
    }

    /**
     * 构造对话框对象，用于类属性结构的修改和浏览
     *
     * @param basCls         需设置或浏览属性结构的数据类
     * @param readonlyFields 禁止在属性结构设置中编辑的字段名称集合
     */
    public FieldsEditPane(IBasCls basCls, List<String> readonlyFields)
    {
        this.basCls = basCls;
        this.readonlyFields = readonlyFields;
        if (this.readonlyFields != null)
        {
            for (int i = 0; i < this.readonlyFields.size(); i++)
            {
                this.readonlyFields.set(i, this.readonlyFields.get(i).toLowerCase());
            }
        }
        if (this.basCls != null)
        {
            this.dataBase = this.basCls.getGDataBase();
            if (this.basCls.getURL() != null)
            {
                this.is6xData = this.basCls.getURL().toLowerCase().startsWith("file:///");
            }

            if (this.basCls instanceof IVectorCls)
            {
                this.fields = ((IVectorCls) this.basCls).getFields();
            }
        }
        this.initialize();
    }

    /**
     * 构造对话框对象，用于类创建时的属性结构设置
     *
     * @param db      待创建数据的数据库
     * @param geoType 要创建的数据的集合类型，根据该类型构造默认字段
     */
    public FieldsEditPane(DataBase db, GeomType geoType)
    {
        this.dataBase = db;
        this.fields = new Fields();
        if (GeomType.GeomAnn.equals(geoType) || GeomType.GeomPnt.equals(geoType))
        {
            this.fields.appendField(GISDefaultValues.getDefaultFieldLayer());
        } else if (GeomType.GeomLin.equals(geoType))
        {
            this.fields.appendField(GISDefaultValues.getDefaultFieldLayer());
            this.fields.appendField(GISDefaultValues.getDefaultFieldLength());
        } else if (GeomType.GeomReg.equals(geoType))
        {
            this.fields.appendField(GISDefaultValues.getDefaultFieldLayer());
            this.fields.appendField(GISDefaultValues.getDefaultFieldArea());
            this.fields.appendField(GISDefaultValues.getDefaultFieldPerimeter());
        } else if (GeomType.GeomSurface.equals(geoType))
        {
            this.fields.appendField(GISDefaultValues.getDefaultFieldLayer());
            this.fields.appendField(GISDefaultValues.getDefaultFieldSurfArea());
        } else if (GeomType.GeomEntity.equals(geoType))
        {
            this.fields.appendField(GISDefaultValues.getDefaultFieldLayer());
            this.fields.appendField(GISDefaultValues.getDefaultFieldSurfArea());
            this.fields.appendField(GISDefaultValues.getDefaultFieldVolume());
        }
        this.initialize();
    }

    /**
     * 构造对话框对象，用于类创建时的属性结构设置
     *
     * @param db     待创建数据的数据库
     * @param fields 属性结构，简单要素类和注记类包括默认属性字段。其他类为null
     */
    public FieldsEditPane(DataBase db, Fields fields)
    {
        this(db, fields, true);
    }

    /**
     * 构造对话框对象，用于类创建时的属性结构设置
     *
     * @param db      待创建数据的数据库
     * @param fields  属性结构，简单要素类和注记类包括默认属性字段。其他类为null
     * @param canEdit 是否能编辑，类在被打开着的时候可能会无法编辑，而只能浏览
     */
    public FieldsEditPane(DataBase db, Fields fields, boolean canEdit)
    {
        this.dataBase = db;
        this.fields = fields;
        this.canEdit = canEdit;

        this.initialize();
    }

    private void initialize()
    {
        //region 按钮及工具条

        ToggleButton toggleButtonEdit = new ToggleButton("编辑", new ImageView(new Image("/fields/editfield_16.png")));
        toggleButtonEdit.setTooltip(new Tooltip("是否能编辑属性结构表"));

        Button buttonAdd = new Button("添加", new ImageView(new Image("/fields/addfield_16.png")));
        buttonAdd.setTooltip(new Tooltip("添加新的字段"));
        buttonAdd.setOnAction(event ->
        {
            List<String> nameList = new ArrayList<>();
            for (EditField ef : this.tableView.getItems())
            {
                nameList.add(ef.getName().toLowerCase());
            }
            String name = "NewField";
            int i = 1;
            while (nameList.contains(name.toLowerCase()))
            {
                name = String.format("NewField_%d", i++);
            }

            EditField editField = EditField.newEditField(name);
            this.tableView.getItems().add(editField);
            this.tableView.getSelectionModel().clearSelection();
            this.tableView.getSelectionModel().select(this.tableView.getItems().size() - 1);
            this.tableView.edit(this.tableView.getItems().size() - 1, this.tableView.getColumns().get(0));
            this.tableView.scrollTo(editField);
        });

        Button buttonRemove = new Button("移除", new ImageView(new Image("/fields/removefield_16.png")));
        buttonRemove.setTooltip(new Tooltip("删除选中的字段"));
        buttonRemove.setOnAction(event ->
        {
            this.tableView.edit(-1, null);//先关闭编辑。因为如果删除的是名称无效的字段，不关闭会导致界面显示和DataTable数据不一致。
            List<EditField> delList = new ArrayList<>();
            for (EditField ef : this.tableView.getSelectionModel().getSelectedItems())
            {
                if (ef.getFieldRowEditable() && !this.isSubtypeField(ef.getField()))
                {
                    delList.add(ef);
                }
            }
            this.tableView.getItems().removeAll(delList);
        });

        Button buttonAddDefaults = new Button("添加默认字段", new ImageView(new Image("/fields/adddefault_16.png")));
        buttonAddDefaults.setTooltip(new Tooltip("添加MapGIS定义的默认字段。"));
        buttonAddDefaults.setOnAction(event ->
        {
            if (this.basCls instanceof SFeatureCls || this.basCls instanceof AnnotationCls)
            {
                boolean hasMpLayer = false;
                for (EditField ef : this.tableView.getItems())
                {
                    if (GISDefaultValues.getDefaultFieldLayer().getFieldName().equalsIgnoreCase(ef.getName()))
                    {
                        hasMpLayer = true;
                        break;
                    }
                }
                if (!hasMpLayer)
                {
                    this.addFieldDataRow(GISDefaultValues.getDefaultFieldLayer(), -1);
                }

                if (this.basCls instanceof SFeatureCls)
                {
                    SFeatureCls sfCls = (SFeatureCls) this.basCls;
                    GeomType geomType = sfCls.getGeomType();
                    if (GeomType.GeomLin.equals(geomType))
                    {
                        //region 添加默认字段mpLength
                        boolean hasMpLength = false;
                        for (EditField ef : this.tableView.getItems())
                        {
                            if (GISDefaultValues.getDefaultFieldLength().getFieldName().equalsIgnoreCase(ef.getName()))
                            {
                                hasMpLength = true;
                                break;
                            }
                        }
                        if (!hasMpLength)
                        {
                            this.calcDefFieldsAdded = true;
                            this.addFieldDataRow(GISDefaultValues.getDefaultFieldLength(), -1);
                        }
                        //endregion
                    } else if (GeomType.GeomReg.equals(geomType))
                    {
                        //region 添加默认字段mpArea和mpDefaultFieldPerimeter
                        boolean hasMpArea = false;
                        for (EditField ef : this.tableView.getItems())
                        {
                            if (GISDefaultValues.getDefaultFieldArea().getFieldName().equalsIgnoreCase(ef.getName()))
                            {
                                hasMpArea = true;
                                break;
                            }
                        }
                        if (!hasMpArea)
                        {
                            this.calcDefFieldsAdded = true;
                            this.addFieldDataRow(GISDefaultValues.getDefaultFieldArea(), -1);
                        }

                        boolean hasMpPerimeter = false;
                        for (EditField ef : this.tableView.getItems())
                        {
                            if (GISDefaultValues.getDefaultFieldPerimeter().getFieldName().equalsIgnoreCase(ef.getName()))
                            {
                                hasMpPerimeter = true;
                                break;
                            }
                        }
                        if (!hasMpPerimeter)
                        {
                            this.calcDefFieldsAdded = true;
                            this.addFieldDataRow(GISDefaultValues.getDefaultFieldPerimeter(), -1);
                        }
                        //endregion
                    } else if (GeomType.GeomSurface.equals(geomType))
                    {
                        //region 添加默认字段mpSurfArea
                        boolean hasSurfArea = false;
                        for (EditField ef : this.tableView.getItems())
                        {
                            if (GISDefaultValues.getDefaultFieldSurfArea().getFieldName().equalsIgnoreCase(ef.getName()))
                            {
                                hasSurfArea = true;
                                break;
                            }
                        }
                        if (!hasSurfArea)
                        {
                            this.calcDefFieldsAdded = true;
                            this.addFieldDataRow(GISDefaultValues.getDefaultFieldSurfArea(), -1);
                        }
                        //endregion
                    } else if (GeomType.GeomEntity.equals(geomType))
                    {
                        //region 添加默认字段mpSurfArea和mpVolume
                        boolean hasMpSurArea = false;
                        for (EditField ef : this.tableView.getItems())
                        {
                            if (GISDefaultValues.getDefaultFieldSurfArea().getFieldName().equalsIgnoreCase(ef.getName()))
                            {
                                hasMpSurArea = true;
                                break;
                            }
                        }
                        if (!hasMpSurArea)
                        {
                            this.calcDefFieldsAdded = true;
                            this.addFieldDataRow(GISDefaultValues.getDefaultFieldSurfArea(), -1);
                        }

                        boolean hasVolume = false;
                        for (EditField ef : this.tableView.getItems())
                        {
                            if (GISDefaultValues.getDefaultFieldVolume().getFieldName().equalsIgnoreCase(ef.getName()))
                            {
                                hasVolume = true;
                                break;
                            }
                        }
                        if (!hasVolume)
                        {
                            this.calcDefFieldsAdded = true;
                            this.addFieldDataRow(GISDefaultValues.getDefaultFieldVolume(), -1);
                        }
                        //endregion
                    }
                }
            }
            this.canAddDefaults.set(false);
        });

        Button buttonImport = new Button("导入", new ImageView(new Image("/fields/import_16.png")));
        buttonImport.setTooltip(new Tooltip("导入字段。"));
        buttonImport.setOnAction(event ->
        {
            GDBOpenFileDialog dlg = new GDBOpenFileDialog();
            dlg.setFilter("GDB矢量类|sfcls;acls;ocls|坐标系Xml|*.xml");
            Optional<String[]> optional = dlg.showAndWait();
            if (optional != null && optional.isPresent())
            {
                String[] files = optional.get();
                if (files != null && files.length > 0)
                {
                    String fileName = files[0];
                    Fields addFlds = null;
                    String fromDBURL = "";
                    if (fileName.toLowerCase().startsWith("gdbp://"))
                    {
                        int last1 = fileName.lastIndexOf('/');
                        if (last1 > 0)
                        {
                            String str = fileName.substring(0, last1);
                            int last2 = str.lastIndexOf('/');
                            String strType = str.substring(last2 + 1);

                            switch (strType.toLowerCase())
                            {
                                case "sfcls":
                                    SFeatureCls sfCls = new SFeatureCls();
                                    if (sfCls.openByURL(fileName) > 0)
                                    {
                                        addFlds = sfCls.getFields();
                                        fromDBURL = sfCls.getGDataBase().getURL();
                                        sfCls.close();
                                    }
                                    break;
                                case "acls":
                                    AnnotationCls aCls = new AnnotationCls();
                                    if (aCls.openByURL(fileName) > 0)
                                    {
                                        addFlds = aCls.getFields();
                                        fromDBURL = aCls.getGDataBase().getURL();
                                        aCls.close();
                                    }
                                    break;
                                case "ocls":
                                    ObjectCls oCls = new ObjectCls();
                                    if (oCls.openByURL(fileName) > 0)
                                    {
                                        addFlds = oCls.getFields();
                                        fromDBURL = oCls.getGDataBase().getURL();
                                        oCls.close();
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    } else
                    {
                        try
                        {
                            SAXReader reader = new SAXReader();
                            Document xd = reader.read(fileName);
                            if (xd != null)
                            {
                                Element rootElement = (Element) xd.selectSingleNode("/root");
                                if (rootElement != null)
                                {
                                    byte[] byteFields = DatatypeConverter.parseBase64Binary(rootElement.getStringValue());
                                    addFlds = new Fields();
                                    if (!addFlds.load(byteFields))
                                    {
                                        addFlds = null;
                                    }
                                }
                            } else
                            {
                                MessageBox.information("属性结构xml格式错误。");
                            }
                        } catch (Exception ex)
                        {
                            MessageBox.information("导入属性结构失败。");
                        }
                    }

                    if (addFlds != null)//将导入的属性结构添加到DataTable（界面GridView）中。其中，忽略掉默认属性、继承属性和相同名称的属性
                    {
                        for (short i = 0; i < addFlds.getFieldCount(); i++)
                        {
                            Field fld = addFlds.getField(i);
                            if (this.dataBase != null && fromDBURL != "" && !fromDBURL.equalsIgnoreCase(this.dataBase.getURL()))//导入属性结构的域信息：不同库时丢失域信息。
                            {
                                Field.ExtField extField = fld.getExtField();
                                if (extField != null && extField.getDmnID() > 0)
                                {
                                    extField.setDmnID(0);
                                    this.setExtField(extField, fld);
                                }
                            }
                            //fld.setPtcPosition((short) -1);//强制修改为新字段，不然属性值可能会出错。
                            String errorMsg = "";
                            if (this.validateFieldName(null, fld.getFieldName(), errorMsg))
                            {
                                this.addFieldDataRow(fld, -1);
                            }
                        }
                    }
                }
            }
        });

        Button buttonExport = new Button("导出", new ImageView(new Image("/fields/export_16.png")));
        buttonExport.setTooltip(new Tooltip("导出属性结构。"));
        buttonExport.setOnAction(event ->
        {
            Fields flds = this.saveFields(false);
            if (flds != null)
            {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml文件(*.xml)", "*.xml"));
                fileChooser.setInitialFileName("Fields" + (this.basCls != null ? "-" + this.basCls.getName() : ""));
                File file = fileChooser.showSaveDialog(this.getCurrentWindow());
                if (file != null)
                {
                    byte[] byteFields = flds.save();
                    try
                    {
                        String strFields = DatatypeConverter.printBase64Binary(byteFields);
                        Document xd = DocumentHelper.parseText(String.format("<root>%s</root>", strFields));
                        String path = file.getPath();
                        if (!path.toLowerCase().endsWith(".xml"))
                        {
                            path += ".xml";
                        }
                        XMLWriter xmlWriter = new XMLWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
                        xmlWriter.write(xd);
                        xmlWriter.close();
                    } catch (Exception ex)
                    {
                    }
                }
            }
        });

        Button buttonApply = new Button("应用", new ImageView(new Image("/fields/apply_16.png")));
        buttonApply.setTooltip(new Tooltip("应用界面所做的修改。"));
        buttonApply.setOnAction(event ->
        {
            this.fields = this.saveFields(true);
            if (this.fields != null)
            {
                if (this.basCls instanceof IVectorCls)
                {
                    long rtn = ((IVectorCls) this.basCls).setFields(this.fields);
                    if (rtn > 0)
                    {
                        //计算默认字段（只有简单要素类需要）
                        if (this.basCls instanceof SFeatureCls && this.calcDefFieldsAdded)
                        {
                            SFeatureCls sfCls = (SFeatureCls) this.basCls;
                            if (sfCls.getObjCount() > 0)
                            {
                                GeomType geomType = sfCls.getGeomType();
                                if (GeomType.GeomLin.equals(geomType))
                                {
                                    for (short i = 0; i < this.fields.getFieldCount(); i++)
                                    {
                                        Field fld = this.fields.getField(i);
                                        if ("mpLength".equalsIgnoreCase(fld.getFieldName()))
                                        {
                                            sfCls.lengthToField(false, i);
                                            break;
                                        }
                                    }
                                } else if (GeomType.GeomReg.equals(geomType))
                                {
                                    int count = 0;
                                    for (short i = 0; i < this.fields.getFieldCount(); i++)
                                    {
                                        Field fld = this.fields.getField(i);
                                        if ("mpPerimeter".equalsIgnoreCase(fld.getFieldName()))
                                        {
                                            sfCls.lengthToField(false, i);
                                            count++;
                                        } else if ("mpArea".equalsIgnoreCase(fld.getFieldName()))
                                        {
                                            sfCls.areaToField(false, i);
                                            count++;
                                        }

                                        if (count == 2)
                                        {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    } else
                    {
                        String message = FieldFunctions.getFieldsSettingError(rtn);
                        String caption = String.format("属性结构设置 - %s", this.basCls.getName());
                        if (rtn == -14501)
                        {
                            if (ButtonType.YES == MessageBox.questionEx(String.format("%s\n是否关闭其他引用后重试？", message), this.getCurrentWindow(), false, caption))
                            {
                                return;
                            }
                        } else
                        {
                            MessageBox.information(message, this.getCurrentWindow(), caption);
                        }
                    }
                }
            }
        });

        buttonAdd.disableProperty().bind(toggleButtonEdit.selectedProperty().not());
        buttonAddDefaults.disableProperty().bind(toggleButtonEdit.selectedProperty().not().and(this.canAddDefaults).not());
        buttonImport.disableProperty().bind(toggleButtonEdit.selectedProperty().not());
        buttonApply.disableProperty().bind(toggleButtonEdit.selectedProperty().not());
        this.tableView.editableProperty().bind(toggleButtonEdit.selectedProperty());
        buttonRemove.setDisable(true);
        toggleButtonEdit.selectedProperty().addListener((o, ov, nv) ->
        {
            boolean canRemove = false;
            if (nv)
            {
                EditField ef = this.tableView.getSelectionModel().getSelectedItem();
                canRemove = ef != null && ef.getFieldRowEditable();
            }
            buttonRemove.setDisable(!canRemove);
        });

        if (this.canEdit)
        {
            ZDToolBar toolBar = new ZDToolBar();
            toolBar.getItems().addAll(toggleButtonEdit, new Separator(), buttonAdd, buttonRemove, new Separator(), buttonAddDefaults, new Separator(), buttonImport, buttonExport, new Separator(), buttonApply);
            buttonAddDefaults.setVisible(this.canEdit && (this.basCls instanceof SFeatureCls || this.basCls instanceof AnnotationCls));
            this.getChildren().addAll(toolBar);
        }
        //endregion

        //region 表、列和数据
        TableColumn<EditField, String> tcName = new TableColumn<>("名称");
        TableColumn<EditField, String> tcAlias = new TableColumn<>("别名");
        TableColumn<EditField, Field.FieldType> tcType = new TableColumn<>("类型");
        TableColumn<EditField, Number> tcLength = new TableColumn<>("长度");
        TableColumn<EditField, Object> tcDefVal = new TableColumn<>("缺省值");
        TableColumn<EditField, Boolean> tcAllowEdit = new TableColumn<>("允许编辑");
        TableColumn<EditField, Boolean> tcAllowNull = new TableColumn<>("允许空");
        tableView.getColumns().addAll(tcName, tcAlias, tcType, tcLength, tcDefVal, tcAllowEdit, tcAllowNull);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) ->
        {
            boolean canRemove = false;
            if (toggleButtonEdit.isSelected())
            {
                EditField ef = this.tableView.getSelectionModel().getSelectedItem();
                canRemove = ef != null && ef.getFieldRowEditable();
            }
            buttonRemove.setDisable(!canRemove);
        });

        tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcAlias.setCellValueFactory(new PropertyValueFactory<>("alias"));
        tcType.setCellValueFactory(new PropertyValueFactory<>("type"));
        tcLength.setCellValueFactory(new PropertyValueFactory<>("length"));
        tcDefVal.setCellValueFactory(new PropertyValueFactory<>("defVal"));
        tcAllowEdit.setCellValueFactory(new PropertyValueFactory<>("allowEdit"));
        tcAllowNull.setCellValueFactory(new PropertyValueFactory<>("allowNull"));

        tcName.setCellFactory(param -> new TableCell<EditField, String>()
        {
            private TextField textField = new TextField();

            @Override
            public void startEdit()
            {
                super.startEdit();
                if (getTableRow() != null && getTableRow().getItem() instanceof EditField)
                {
                    EditField editField = (EditField) getTableRow().getItem();
                    boolean canEdit = editField.getFieldRowEditable();
                    if (canEdit)
                    {
                        Field fld = editField.getField();
                        Field.ExtField extFld = editField.getExtField();
                        boolean hasDomain = fld != null /*&& fld.getPtcPosition() != -1*/ && extFld != null && extFld.getDmnID() > 0;//导入的字段和添加的默认字段会通过PtcPosition强制修改为新字段。
                        canEdit = !isSubtypeField(fld) && !hasDomain;
                    }

                    if (canEdit)
                    {
                        textField.setText(getItem());
                        setGraphic(textField);
                        textField.requestFocus();

                        //按Enter键完成修改。
                        textField.setOnKeyPressed(event ->
                        {
                            if (KeyCode.ENTER.equals(event.getCode()))
                            {
                                commitEdit(textField.getText());
                            }
                        });

                        textField.textProperty().addListener((observable, oldValue, newValue) ->
                        {
                            StringProperty errorMsg = new SimpleStringProperty();
                            List<Character> invalidCharList = GISDefaultValues.getSpatialCharacterList();
                            invalidCharList.add(' ');
                            XString.isTextValid(newValue, 21, invalidCharList, errorMsg);

                            if (errorMsg.get() != "")
                            {
                                UIFunctions.showErrorTip(textField, errorMsg.get(), FieldsEditPane.this.tooltipError);
                                textField.setText(oldValue);
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
                }
            }

            @Override
            public void commitEdit(String value)
            {
                String errorMsg = "";
                if (!validateFieldName((EditField) getTableRow().getItem(), value, errorMsg))
                {
                    UIFunctions.showErrorTip(textField, errorMsg, FieldsEditPane.this.tooltipError);
                } else
                {
                    super.commitEdit(value);
                    EditField ef = (EditField) getTableRow().getItem();
                    if (ef != null)
                    {
                        ef.setName(value);
                    }
                    updateItem(value, XString.isNullOrEmpty(value));
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
                setGraphic(label);
            }
        });
        tcAlias.setCellFactory(param -> new TableCell<EditField, String>()
        {
            private TextField textField = new TextField();

            @Override
            public void startEdit()
            {
                super.startEdit();
                if (getTableRow() != null && getTableRow().getItem() instanceof EditField)
                {
                    EditField editField = (EditField) getTableRow().getItem();
                    boolean canEdit = editField.getFieldRowEditable();
                    if (canEdit)
                    {
                        Field fld = editField.getField();
                        Field.ExtField extFld = editField.getExtField();
                        boolean hasDomain = fld != null /*&& fld.getPtcPosition() != -1 */ && extFld != null && extFld.getDmnID() > 0;//导入的字段和添加的默认字段会通过PtcPosition强制修改为新字段。
                        canEdit = !isSubtypeField(fld) && !hasDomain;
                    }

                    if (canEdit)
                    {
                        textField.setText(getItem());
                        setGraphic(textField);
                        textField.requestFocus();

                        //按Enter键完成修改。
                        textField.setOnKeyPressed(event ->
                        {
                            if (KeyCode.ENTER.equals(event.getCode()))
                            {
                                commitEdit(textField.getText());
                            }
                        });

                        textField.textProperty().addListener((observable, oldValue, newValue) ->
                        {
                            StringProperty errorMsg = new SimpleStringProperty();
                            XString.isTextValid(newValue, 128, errorMsg);
                            if (errorMsg.get() != "")
                            {
                                UIFunctions.showErrorTip(textField, errorMsg.get(), FieldsEditPane.this.tooltipError);
                                textField.setText(oldValue);
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
                }
            }

            @Override
            public void commitEdit(String value)
            {
                super.commitEdit(value);
                EditField ef = (EditField) getTableRow().getItem();
                if (ef != null)
                {
                    ef.setAlias(value);
                }
                updateItem(value, XString.isNullOrEmpty(value));
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
                setGraphic(label);
            }
        });
        tcType.setCellFactory(param -> new TableCell<EditField, Field.FieldType>()
        {
            private ZDComboBox<Field.FieldType> comboBox = new ZDComboBox<>();
            private Field.FieldType editValue = null;

            @Override
            public void startEdit()
            {
                super.startEdit();
                EditField editField = (EditField) getTableRow().getItem();
                if (editField != null)
                {
                    boolean canEdit = editField.getFieldRowEditable();
                    if (canEdit)
                    {
                        Field fld = editField.getField();
                        Field.ExtField extFld = editField.getExtField();
                        boolean hasDomain = fld != null/* && fld.getPtcPosition() != -1*/ && extFld != null && extFld.getDmnID() > 0;//导入的字段和添加的默认字段会通过PtcPosition强制修改为新字段。
                        canEdit = !isSubtypeField(fld) && !hasDomain;
                    }

                    if (canEdit)
                    {
                        if (comboBox.getItems().size() == 0)
                        {
                            for (Field.FieldType fldType : GISDefaultValues.MapGISFieldTypes)
                            {
                                if (!is6xData)
                                {
                                    comboBox.getItems().add(fldType);
                                }
                            }
                        }
                        this.setGraphic(comboBox);

                        comboBox.setConverter(new StringConverter<Field.FieldType>()
                        {
                            @Override
                            public String toString(Field.FieldType object)
                            {
                                return LanguageConvert.fieldTypeConvert(object);
                            }

                            @Override
                            public Field.FieldType fromString(String string)
                            {
                                return LanguageConvert.fieldTypeConvert(string);
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
            }

            @Override
            public void commitEdit(Field.FieldType value)
            {
                super.commitEdit(value);

                EditField ef = (EditField) getTableRow().getItem();
                if (ef != null)
                {
                    ef.setType(value);
                    Field.FieldType fldType = ef.getType();
                    short[] lens = FieldFunctions.getDefaultLength(fldType);
                    if (lens != null && lens.length == 2)
                    {
                        Object defVal = null;
                        boolean allowNull = true;
                        if (is6xData)//6x数据字段为数值类型的时候，不允许空，默认值初始为0
                        {
                            if (Field.FieldType.fldByte.equals(fldType) || Field.FieldType.fldShort.equals(fldType) || Field.FieldType.fldLong.equals(fldType) || Field.FieldType.fldInt64.equals(fldType) || Field.FieldType.fldFloat.equals(fldType) || Field.FieldType.fldDouble.equals(fldType))
                            {
                                defVal = 0;
                                allowNull = false;
                            } else if (Field.FieldType.fldBool.equals(fldType))
                            {
                                defVal = false;
                                allowNull = false;
                            }
                        }
                        ef.setLength(lens[0]);
                        ef.setDefVal(defVal);
                        ef.setAllowEdit(allowNull);
                    }
                    updateItem(value, false);
                }
            }

            @Override
            public void cancelEdit()
            {
                super.cancelEdit();
                updateItem(this.getItem(), false);
            }

            @Override
            protected void updateItem(Field.FieldType item, boolean empty)
            {
                super.updateItem(item, empty);
                Label label = null;
                if (!empty && item != null)
                {
                    label = new Label(LanguageConvert.fieldTypeConvert(item));
                }
                setGraphic(label);
                tableView.requestFocus();
            }
        });
        tcLength.setCellFactory(param -> new TableCell<EditField, Number>()
        {
            private TextField textField = UIFunctions.newIntTextField(true);
            private int maxValue;

            @Override
            public void startEdit()
            {
                super.startEdit();
                EditField editField = (EditField) getTableRow().getItem();
                if (editField != null)
                {
                    boolean canEdit = editField.getFieldRowEditable();
                    if (canEdit)
                    {
                        Field.FieldType fldType = editField.getType();
                        canEdit = Field.FieldType.fldStr.equals(fldType) || Field.FieldType.fldBinary.equals(fldType);
                    }

                    if (canEdit)
                    {
                        textField.setText(this.getItem().toString());
                        this.setGraphic(textField);
                        textField.requestFocus();
                        maxValue = FieldFunctions.getMaxLength(editField.getType());

                        //按Enter键完成修改。
                        textField.setOnKeyPressed(event ->
                        {
                            if (KeyCode.ENTER.equals(event.getCode()))
                            {
                                commitEdit(Integer.valueOf(textField.getText()));
                            }
                        });

                        textField.textProperty().addListener((observable, oldValue, newValue) ->
                        {
                            String errorMsg = "";
                            try
                            {
                                int numValue = Integer.valueOf(newValue);
                                if (maxValue > 0 && numValue > maxValue)
                                {
                                    errorMsg = String.format("%s不能超过最大值%d。", getTableColumn().getText(), maxValue);
                                }
                            } catch (Exception ex)
                            {
                                errorMsg = ex.getMessage();
                            }

                            if (errorMsg != "")
                            {
                                UIFunctions.showErrorTip(textField, errorMsg, FieldsEditPane.this.tooltipError);
                                textField.setText(oldValue);
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
            }

            @Override
            public void commitEdit(Number value)
            {
                super.commitEdit(value);
                EditField ef = (EditField) getTableRow().getItem();
                if (ef != null)
                {
                    ef.setLength(value.intValue());
                }
                updateItem(value, false);
            }

            @Override
            public void cancelEdit()
            {
                super.cancelEdit();
                updateItem(this.getItem(), false);
            }

            @Override
            protected void updateItem(Number item, boolean empty)
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
        tcDefVal.setCellFactory(param -> new TableCell<EditField, Object>()
        {
            private TextField textField = new TextField();
            private Object maxDefValue = 0;  //用于缺省值的限制输入（不受字段域约束）
            private Object minDefValue = 0;  //用于缺省值的限制输入（不受字段域约束）

            @Override
            public void startEdit()
            {
                super.startEdit();
                EditField editField = (EditField) getTableRow().getItem();
                if (editField != null)
                {
                    Field.FieldType fldType = editField.getType();
                    boolean canEdit = editField.getFieldRowEditable();
                    if (canEdit)
                    {
                        canEdit = !isSubtypeField(editField.getField());
                        if (canEdit)
                        {
                            if (Field.FieldType.fldTime.equals(fldType) || Field.FieldType.fldTimeStamp.equals(fldType) || Field.FieldType.fldBinary.equals(fldType) || Field.FieldType.fldBlob.equals(fldType))
                            {
                                canEdit = false;
                            }
                        }
                    }

                    if (canEdit)
                    {
                        if (this.getItem() != null)
                        {
                            textField.setText(this.getItem().toString());
                        }
                        this.setGraphic(textField);
                        textField.requestFocus();

                        Object[] defVals = FieldFunctions.getFieldValueRange(fldType, editField.getLength());
                        if (defVals != null && defVals.length == 2)
                        {
                            this.minDefValue = defVals[0];
                            this.maxDefValue = defVals[1];
                        }

                        //按Enter键完成修改。
                        textField.setOnKeyPressed(event ->
                        {
                            if (event.getCode() == KeyCode.ENTER)
                            {
                                commitEdit(textField.getText());
                            }
                        });
                        textField.textProperty().addListener(new ChangeListener<String>()
                        {
                            @Override
                            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
                            {

                            }
                        });

                        textField.textProperty().addListener((observable, oldValue, newValue) ->
                        {
                            String strError = "";

                            if (fldType == Field.FieldType.fldStr)
                            {
                                if (!XString.isTextValid(newValue, editField.getLength() + 1, new SimpleStringProperty()))
                                {
                                    strError = String.format("默认值不能超过指定的长度%d", editField.getLength());
                                }
                            } else if (Field.FieldType.fldByte.equals(fldType) || Field.FieldType.fldShort.equals(fldType) || Field.FieldType.fldLong.equals(fldType) || Field.FieldType.fldInt64.equals(fldType))
                            {
                                if (!newValue.matches("(\\d|[1-9]\\d*)"))
                                {
                                    strError = String.format("默认值的类型（%s）须与字段类型一致。", editField.getType());
                                } else
                                {
                                    long lValue = Long.valueOf(newValue);
                                    if (this.minDefValue != null && this.maxDefValue != null && ((long) this.minDefValue > lValue || lValue > (long) this.maxDefValue))
                                    {
                                        strError = String.format("默认值的值不能超过字段设置的范围（%d-%d）。", this.minDefValue, this.maxDefValue);
                                    } else if (String.valueOf(Math.abs(lValue)).length() > editField.getLength())
                                    {
                                        strError = String.format("默认值的长度不能超过设置的长度%d。", editField.getLength());
                                    }
                                }
                            } else if (Field.FieldType.fldFloat.equals(fldType) || Field.FieldType.fldDouble.equals(fldType))
                            {
                                double val = Double.valueOf(newValue);
                                String[] vals = String.valueOf(Math.abs(val)).split("\\.");
                                if (vals.length > 0)
                                {
                                    if (vals[0].length() > (editField.getLength()))
                                    {
                                        strError = String.format("默认值的范围不能超过指定的长度", editField.getLength());
                                    }
                                }
                            } else if (Field.FieldType.fldDate.equals(fldType))
                            {
                                try
                                {
                                    LocalDate localDate = LocalDate.parse(newValue);
                                } catch (Exception ex)
                                {
                                    strError = "默认值不是日期型：" + ex.getMessage();
                                }
                            } else if (Field.FieldType.fldBool.equals(fldType))
                            {
                                if (newValue != null && !"true".equalsIgnoreCase(newValue) && !"false".equalsIgnoreCase(newValue))
                                {
                                    strError = "布尔值字段的默认值只能是true或者false";
                                }
                            }

                            if (strError != "")
                            {
                                UIFunctions.showErrorTip(textField, strError, FieldsEditPane.this.tooltipError);
                                textField.setText(oldValue);
                            }
                        });
                    }
                }
            }

            @Override
            public void commitEdit(Object newValue)
            {
                super.commitEdit(newValue);
                EditField ef = (EditField) getTableRow().getItem();
                if (ef != null)
                {
                    ef.setDefVal(newValue);
                }
                updateItem(newValue, false);
            }

            @Override
            public void cancelEdit()
            {
                super.cancelEdit();
                updateItem(this.getItem(), false);
            }

            @Override
            protected void updateItem(Object item, boolean empty)
            {
                super.updateItem(item, empty);
                Label label = null;
                if (!empty && item != null && !item.toString().isEmpty())
                {
                    label = new Label(item.toString());
                }
                this.setGraphic(label);
            }
        });
        tcAllowEdit.setCellFactory(param -> new TableCell<EditField, Boolean>()
        {
            private CheckBox checkBox;

            @Override
            public void startEdit()
            {
                super.startEdit();
                EditField editField = (EditField) getTableRow().getItem();
                if (editField != null)
                {
                    boolean canEdit = editField.getFieldRowEditable();
                    if (canEdit)
                    {
                        Field.FieldType fldType = editField.getType();

                        if (!canEdit)
                        {
                            cancelEdit();
                        } else
                        {
                            HBox hBox = new HBox();
                            checkBox = new CheckBox();
                            checkBox.setSelected(getItem());
                            hBox.setAlignment(Pos.CENTER);
                            hBox.getChildren().addAll(checkBox);
                            this.setGraphic(hBox);
                        }
                    }
                }
            }

            @Override
            public void commitEdit(Boolean newValue)
            {
                super.commitEdit(newValue);
                EditField ef = (EditField) getTableRow().getItem();
                if (ef != null)
                {
                    ef.setAllowEdit(newValue);
                }
                updateItem(newValue, false);
            }

            @Override
            public void cancelEdit()
            {
                super.cancelEdit();
                updateItem(this.getItem(), false);
            }

            @Override
            protected void updateItem(Boolean item, boolean empty)
            {
                super.updateItem(item, empty);
                CheckBox checkbox = null;
                if (!empty && item != null)
                {
                    checkbox = new CheckBox();
                    checkbox.setSelected(item);
                    checkbox.setDisable(true);
                }
                this.setGraphic(checkbox);
            }
        });
        tcAllowNull.setCellFactory(param -> new TableCell<EditField, Boolean>()
        {
            private CheckBox checkBox;

            @Override
            public void startEdit()
            {
                super.startEdit();
                EditField editField = (EditField) getTableRow().getItem();
                if (editField != null)
                {
                    boolean canEdit = editField.getFieldRowEditable();
                    if (canEdit)
                    {
                        Field.FieldType fldType = editField.getType();

                        if (Field.FieldType.fldTime.equals(fldType) || Field.FieldType.fldTimeStamp.equals(fldType) || Field.FieldType.fldBinary.equals(fldType) || Field.FieldType.fldBlob.equals(fldType))
                        {
                            canEdit = false;
                        } else if (Field.FieldType.fldByte.equals(fldType) || Field.FieldType.fldBool.equals(fldType) || Field.FieldType.fldShort.equals(fldType) || Field.FieldType.fldLong.equals(fldType) || Field.FieldType.fldInt64.equals(fldType) || Field.FieldType.fldFloat.equals(fldType) || Field.FieldType.fldDouble.equals(fldType))
                        {
                            canEdit = !is6xData;
                        }

                        if (!canEdit)
                        {
                            cancelEdit();
                        } else
                        {
                            HBox hBox = new HBox();
                            checkBox = new CheckBox();
                            checkBox.setSelected(getItem());
                            hBox.setAlignment(Pos.CENTER);
                            hBox.getChildren().addAll(checkBox);
                            this.setGraphic(hBox);
                        }
                    }
                }
            }

            @Override
            public void commitEdit(Boolean newValue)
            {
                super.commitEdit(newValue);
                EditField ef = (EditField) getTableRow().getItem();
                if (ef != null)
                {
                    ef.setAllowNull(newValue);
                }
                updateItem(newValue, false);
            }

            @Override
            public void cancelEdit()
            {
                super.cancelEdit();
                updateItem(this.getItem(), false);
            }

            @Override
            protected void updateItem(Boolean item, boolean empty)
            {
                super.updateItem(item, empty);
                CheckBox checkbox = null;
                if (!empty && item != null)
                {
                    checkbox = new CheckBox();
                    checkbox.setSelected(item);
                    checkbox.setDisable(true);
                }
                this.setGraphic(checkbox);
            }
        });
        //endregion

        this.getChildren().addAll(this.tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        this.tooltipError = new Tooltip();
        this.tooltipError.setAutoHide(true);
        this.initializeFields(this.fields);
    }

    //endregion

    //region 公共方法

    /**
     * 将给定的Fields塞到列表中显示到表格
     *
     * @param flds 属性结构
     */
    public void initializeFields(Fields flds)
    {
        this.tableView.getItems().clear();
        this.fields = flds;
        if (this.fields != null && this.fields.getFieldCount() > 0)
        {
            for (short i = 0; i < this.fields.getFieldCount(); i++)
            {
                this.addFieldDataRow(this.fields.getField(i), -1);
            }
        }
        this.initAddDefaults();
    }

    /**
     * 将界面数据存成Fields
     *
     * @param bCheck 是否需要验证有效性，一般确定时需验证，但导出时没有必要
     * @return 保存的Fields
     */
    public Fields saveFields(boolean bCheck)
    {
        Fields flds = null;
        if (this.validateDefaultValue())
        {
            Server server = null;
            if (bCheck && this.dataBase != null)
            {
                server = this.dataBase.getServer();
            }

            flds = new Fields();

            List<Integer> errorRows = new ArrayList<>();
            String errorMsg = "";
            String errorLen = "";//主要有两种错误：类型不兼容和长度变小了。为使提示信息简单易读，使用两个错误语句分类提示。
            for (short i = 0; i < this.tableView.getItems().size(); i++)
            {
                EditField ef = this.tableView.getItems().get(i);
                Field fld = ef.getField();
                if (fld == null /*|| fld.getPtcPosition() == -1*/)//导入的字段和添加的默认字段会通过PtcPosition强制修改为新字段。
                {
                    //region 新添加的字段
                    if (fld == null)
                    {
                        fld = new Field();
                    }
                    fld.setFieldName(ef.getName());
                    fld.setFieldType(ef.getType());
                    fld.setFieldLength((short) ef.getLength());
                    //fld.setPointLength((short) ef.getPointLen());
                    //fld.setEditable(ef.isAllowEdit() ? (short) 1 : (short) 0);
                    Field.ExtField extFld = ef.getExtField();
                    if (extFld == null)
                    {
                        extFld = new Field.ExtField(fld.getFieldType(), (short) 0);
                    }
                    extFld.setAlias(ef.getAlias());
                    Object defVal = ef.getDefVal();
                    if (defVal == null || defVal.toString().isEmpty())
                    {
                        defVal = null;
                    } else if (Field.FieldShape.fldShpCombo.equals(extFld.getShape()))
                    {
                        defVal = FieldFunctions.getComboFieldValueByName(extFld, defVal.toString());
                    }
                    extFld.setDefVal(defVal);
                    extFld.setIsNull(ef.isAllowNull());
                    this.setExtField(extFld, fld);
                    flds.appendField(fld);
                    //endregion
                } else
                {
                    //region 老字段
                    if (!ef.getFieldRowEditable())//不能编辑的字段肯定未被修改！
                    {
                        //if (fld.getPointLength() != pointLen)
                        //{
                        //    fld = fld.clone();
                        //    fld.setPointLength(pointLen);//默认双精度不可编辑字段的小数显示位数放开了编辑
                        //}
                        flds.appendField(fld);
                        flds.setField(i, fld);//AppendField字段被处理成新字段，即其PtcPosition为-1，所以需要重新设置一下。不然记录会丢失。
                        continue;
                    } else
                    {
                        Field.FieldType fldType = ef.getType();

                        //region 扩展信息
                        Field.ExtField extFld = ef.getExtField();
                        if (extFld == null)
                        {
                            extFld = new Field.ExtField(fldType, (short) 0);
                        }
                        extFld.setAlias(ef.getAlias());
                        Object defVal = ef.getDefVal();
                        if (defVal == null || defVal.toString().isEmpty())
                        {
                            defVal = null;
                        } else if (Field.FieldShape.fldShpCombo.equals(extFld.getShape()))
                        {
                            defVal = FieldFunctions.getComboFieldValueByName(extFld, defVal.toString());
                        } else if (this.isSubtypeField(fld))
                        {
                            SubTypes subTypes = ((IVectorCls) this.basCls).getSubTypes();
                            if (subTypes != null)
                            {
                                for (int j = 0; j < subTypes.getCount(); j++)
                                {
                                    SubType subType = null;
                                    subTypes.getItem(j, subType);
                                    if (subType.getName() == defVal.toString())
                                    {
                                        defVal = subType.getCode();
                                        break;
                                    }
                                }
                            }
                        }
                        extFld.setDefVal(defVal);
                        extFld.setIsNull(ef.isAllowNull());
                        //endregion

                        String fldName = ef.getName();
                        short fldMskLength = (short) ef.getLength();
                        fld = fld.clone();//出错时，用户可能会取消，所以要用Clone的对象
                        if (bCheck && server != null)
                        {
                            //region 检查修改字段，对可能丢失数据的情况给予提示
                            String strOldType = LanguageConvert.fieldTypeConvert(fld.getFieldType());
                            int compatible = GISDefaultValues.FieldTypeCompatible[fld.getFieldType().value()][fldType.value()];
                            if (compatible == 0)//不兼容
                            {
                                errorMsg += (errorMsg != "" ? "；\r\n" : "") + "      " + String.format("字段【%s】的类型与原类型（%s）不兼容", fldName, strOldType);
                                errorRows.add(new Integer(i));
                            } else if (compatible == 2)//由字符长度决定
                            {
                                if (fldMskLength < fld.getFieldLength())
                                {
                                    errorLen += (errorLen != "" ? "；\r\n" : "") + "      " + String.format("字段【%s】的长度小于原长度（%d）", fldName, fld.getFieldLength());
                                    errorRows.add(new Integer(i));
                                }
                            }

                            if (server.getConnectType() != ConnectType.Local && (errorMsg != "" || errorLen != ""))
                            {
                                continue;//非本地数据出现上述错误后，是不允许修改的。没有用break，是因为想要把所有的错误信息都显示出来
                            }
                            //endregion
                        }
                        fld.setFieldName(fldName);
                        fld.setFieldType(fldType);
                        fld.setFieldLength(fldMskLength);
                        //fld.setPointLength(fldPointLength);
                        //fld.setEditable(ef.isAllowEdit() ? (short) 1 : (short) 0);
                        this.setExtField(extFld, fld);
                        flds.appendField(fld);
                        flds.setField(i, fld);//AppendField字段被处理成新字段，即其PtcPosition为-1，所以需要重新设置一下。不然记录会丢失。
                    }
                    //endregion
                }
            }

            //region 错误提示

            errorMsg += ((errorMsg != "" && errorLen != "") ? "；\r\n" : "") + errorLen;
            if (errorRows.size() > 0)
            {
                this.tableView.getSelectionModel().clearSelection();
                for (int errorRow : errorRows)
                {
                    this.tableView.getSelectionModel().select(errorRow);
                }
                this.tableView.getFocusModel().focus(errorRows.get(0));

                if (server != null && ConnectType.Local.equals(server.getConnectType()))
                {
                    if (ButtonType.CANCEL == MessageBox.question(String.format("新的属性结构具有如下问题:\n%s。\n\n修改后可能会丢失信息，确定要继续吗？"), this.getCurrentWindow()))
                    {
                        flds = null;
                    }
                } else
                {
                    MessageBox.information(String.format("新的属性结构具有如下问题:\n%s。\n\n非本地数据库不支持这种修改。", errorMsg), this.getCurrentWindow());
                    flds = null;
                }
                this.tableView.requestFocus();
            }
            //endregion
        }
        return flds;
    }

    /**
     * 验证默认值是否合法：1、是否超出范围；2、组合框形态字段的默认值必须是组合框中的项；③、字段必须设置默认值或者允许空。
     *
     * @return 如果默认值没有问题，则返回True，否则返回False
     */
    public boolean validateDefaultValue()
    {
        String defValError = "";
        String errorOutRange = "";
        String errorNutNull = "";
        List<Integer> errorRows = new ArrayList<>();
        for (int i = 0; i < this.tableView.getItems().size(); i++)
        {
            EditField ef = this.tableView.getItems().get(i);
            Field.ExtField extFld = ef.getExtField();
            if (extFld != null)
            {
                Object defVal = extFld.getDefVal();
                if (defVal != null && !defVal.toString().isEmpty())
                {
                    Field fld = ef.getField();
                    if (this.isSubtypeField(fld))
                    {
                        continue;
                    }

                    if (Field.FieldShape.fldShpCombo.equals(extFld.getShape()))
                    {
                        //region 组合框形态字段的默认值必须是组合框中的项
                        List<String> itemList = FieldFunctions.getFieldComboItems(extFld);
                        if (itemList == null || !itemList.contains(defVal.toString()))
                        {
                            defValError += (defValError != "" ? "、" : "") + String.format("“%s”", ef.getName());
                            errorRows.add(i);
                        }
                        //endregion
                    } else
                    {
                        //region 是否超出范围
                        Field.FieldType fldType = extFld.getFieldType();
                        if (Field.FieldType.fldStr.equals(fldType))
                        {
                            int defLen = XString.getStringByteLength((String) defVal);
                            int len = ef.getLength();
                            if (defLen > len)
                            {
                                errorOutRange += (errorOutRange != "" ? "、" : "") + String.format("“%s”", ef.getName());
                                errorRows.add(i);
                            }
                        } else if (Field.FieldType.fldByte.equals(fldType) || Field.FieldType.fldShort.equals(fldType) || Field.FieldType.fldLong.equals(fldType) || Field.FieldType.fldInt64.equals(fldType) || Field.FieldType.fldDouble.equals(fldType) || Field.FieldType.fldFloat.equals(fldType) || Field.FieldType.fldDate.equals(fldType))
                        {
                            int mskLen = ef.getLength();
                            Object[] vals = FieldFunctions.getFieldValueRange(fldType, mskLen);
                            if (vals != null && vals.length == 2 && vals[0] != null && vals[1] != null)
                            {
                                if (Field.FieldType.fldDate.equals(fldType))
                                {
                                    LocalDate min = (LocalDate) vals[0];
                                    LocalDate max = (LocalDate) vals[1];
                                    LocalDate dtVal = (LocalDate) defVal;
                                    if (dtVal.isBefore(min) || dtVal.isAfter(max))
                                    {
                                        errorOutRange += (errorOutRange != "" ? "、" : "") + String.format("【%s】", ef.getName());
                                        errorRows.add(i);
                                    }
                                } else
                                {
                                    double max = (double) vals[1];
                                    double min = (double) vals[0];
                                    double dVal = (double) defVal;
                                    if (dVal < min || dVal > max)
                                    {
                                        errorOutRange += (errorOutRange != "" ? "、" : "") + String.format("【%s】", ef.getName());
                                        errorRows.add(i);
                                    }
                                }
                            }
                        }
                        //endregion
                    }
                } else if (!ef.isAllowNull())
                {
                    errorNutNull += (errorNutNull != "" ? "、" : "") + String.format("“%s”", ef.getName());
                    errorRows.add(i);
                }
            }
        }

        if (errorRows.size() > 0)
        {
            //region 选中出错的字段行并提示信息
            this.tableView.getSelectionModel().clearSelection();
            for (int errorRow : errorRows)
            {
                this.tableView.getSelectionModel().select(errorRow);
            }
            this.tableView.getFocusModel().focus(errorRows.get(0));

            if (defValError != "")
            {
                defValError = String.format("字段%s的默认值必须是组合框中定义的值。", defValError);
            }
            if (errorOutRange != "")
            {
                errorOutRange = String.format("字段%s的默认值超出范围。", errorOutRange);
            }
            if (errorNutNull != "")
            {
                errorNutNull = String.format("字段%s必须设置默认值或者允许空。", errorNutNull);
            }
            defValError += ((defValError != "" && errorOutRange != "") ? "\r\n" : "") + errorOutRange;
            defValError += ((defValError != "" && errorNutNull != "") ? "\r\n" : "") + errorNutNull;
            if (defValError != "")
            {
                MessageBox.information(defValError, this.getCurrentWindow());
            }
            //endregion
        }
        return defValError == "";
    }
    //endregion

    //region 内部方法

    private Window window;

    /**
     * 获取当前窗口的window对象
     *
     * @return 当前窗口的window对象
     */
    private Window getCurrentWindow()
    {
        if (this.window == null)
        {
            this.window = this.tableView.getScene().getWindow();
        }
        return this.window;
    }

    /**
     * 判断字段能否被编辑
     *
     * @param fld 字段
     * @return 被编辑返回true，否则返回false
     */
    private boolean isFieldRowEditable(Field fld)
    {
        boolean editable = true;
        if (fld != null)
        {
            String fldName = fld.getFieldName();
            if (GISDefaultValues.getDefaultFieldNameList().contains(fldName.toLowerCase()))
            {
                editable = false;//默认字段
            } else if (this.readonlyFields != null && this.readonlyFields.contains(fldName.toLowerCase()))
            {
                editable = false;//用户通过构造函数传入的指定不可编辑字段
                //} else if (fld.getIsInherited() > 0)
                //{
                //    editable = false;//继承字段
            } else if ("Enable".equals(fldName))//网络拓扑点的Enable字段
            {
                if (this.basCls instanceof SFeatureCls && ((SFeatureCls) this.basCls).getgNetID() > 0)
                {
                    IXClsInfo clsInfo = ((SFeatureCls) this.basCls).getGDataBase().getXclsInfo(XClsType.XGNet, ((SFeatureCls) this.basCls).getgNetID());
                    if (clsInfo instanceof GNetInfo && ((GNetInfo) clsInfo).getTopoNodClsID() == this.basCls.getClsID())
                    {
                        editable = false;
                    }
                }
            }

            if (editable)
            {
                //region 关系类外关键字、元关系类的MTDBID字段
                if (this.basCls instanceof RelationCls)
                {
                    IXClsInfo clsInfo = ((RelationCls) this.basCls).getClsInfo();
                    if (clsInfo instanceof RClsInfo)
                    {
                        if (Field.FieldType.fldInt64.equals(fld.getFieldType()) && ((RClsInfo) clsInfo).getIsAttributed() > 0)
                        {
                            if (fldName.equals(((RClsInfo) clsInfo).getOrigFKey()) || fldName.equals(((RClsInfo) clsInfo).getDestFKey()))
                            {
                                editable = false;//属性化关系类外关键字
                            }
                        } else if (RelType.Relmeta.equals(((RClsInfo) clsInfo).getRelType()) && "MTDBID".equals(fldName))
                        {
                            editable = false;//元关系类的MTDBID字段
                        }
                    }
                } else if (this.basCls instanceof IVectorCls && Field.FieldType.fldInt64.equals(fld.getFieldType()))
                {
                    IntList relations = ((IVectorCls) this.basCls).relations();
                    if (relations != null)
                    {
                        for (int i = 0; i < relations.size(); i++)
                        {
                            int rID = relations.get(i);
                            RClsInfo rClsInfo = (RClsInfo) this.basCls.getGDataBase().getXclsInfo(XClsType.XRCls, rID);
                            if (rClsInfo != null && rClsInfo.getDestClsID() == this.basCls.getClsID() && rClsInfo.getIsAttributed() <= 0)
                            {
                                if (fldName.equals(rClsInfo.getOrigFKey()))
                                {
                                    editable = false;//非属性化关系类外关键字
                                    break;
                                }
                            }
                        }
                    }
                }
                //endregion
            }
        }
        return editable;
    }

    /**
     * 判断给定字段是否是当前编辑数据的子类型字段
     *
     * @param fld
     * @return
     */
    private boolean isSubtypeField(Field fld)
    {
        return (fld != null && this.basCls instanceof IVectorCls && fld.getFieldName() == ((IVectorCls) this.basCls).getSubTypeFieldName());
    }

    /**
     * 添加给定Field对象对应的数据行
     *
     * @param fld   新添加的字段
     * @param index 新添加行的位置，若超出范围（小于0或大于Count）则直接添加到最后
     */
    private void addFieldDataRow(Field fld, int index)
    {
        if (fld != null)
        {
            EditField editField = new EditField(fld, this.isFieldRowEditable(fld));
            if (index < 0 || index > this.tableView.getItems().size())
            {
                this.tableView.getItems().add(editField);
            } else
            {
                this.tableView.getItems().add(index, editField);
            }
        }
    }

    /**
     * 设置工具条按钮的Enable属性
     */
    private void initAddDefaults()
    {
        if (this.canEdit)
        {
            if (this.basCls instanceof SFeatureCls || this.basCls instanceof AnnotationCls)
            {
                canAddDefaults.set(true);
                GeomType geoType = this.basCls instanceof AnnotationCls ? GeomType.GeomAnn : GeomType.GeomUnknown;
                if (this.basCls instanceof SFeatureCls)
                {
                    geoType = ((SFeatureCls) this.basCls).getGeomType();
                }

                if (GeomType.GeomAnn.equals(geoType) || GeomType.GeomPnt.equals(geoType))
                {
                    for (EditField ef : this.tableView.getItems())
                    {
                        String fldName = ef.getName();
                        if (GISDefaultValues.getDefaultFieldLayer().getFieldName().equals(fldName))
                        {
                            canAddDefaults.set(false);
                            break;
                        }
                    }
                } else if (GeomType.GeomLin.equals(geoType))
                {
                    int defCount = 0;
                    for (EditField ef : this.tableView.getItems())
                    {
                        String fldName = ef.getName();
                        if (GISDefaultValues.getDefaultFieldLayer().getFieldName().equals(fldName))
                        {
                            defCount++;
                        } else if (GISDefaultValues.getDefaultFieldLength().getFieldName().equals(fldName))
                        {
                            defCount++;
                        }

                        if (defCount == 2)
                        {
                            break;
                        }
                    }
                    canAddDefaults.set(defCount != 2);
                } else if (GeomType.GeomReg.equals(geoType))
                {
                    int defCount = 0;
                    for (EditField ef : this.tableView.getItems())
                    {
                        String fldName = ef.getName();
                        if (GISDefaultValues.getDefaultFieldLayer().getFieldName().equals(fldName))
                        {
                            defCount++;
                        } else if (GISDefaultValues.getDefaultFieldPerimeter().getFieldName().equals(fldName))
                        {
                            defCount++;
                        } else if (GISDefaultValues.getDefaultFieldArea().getFieldName().equals(fldName))
                        {
                            defCount++;
                        }

                        if (defCount == 3)
                        {
                            break;
                        }
                    }
                    canAddDefaults.set(defCount != 3);
                } else if (GeomType.GeomSurface.equals(geoType))
                {
                    int defCount = 0;
                    for (EditField ef : this.tableView.getItems())
                    {
                        String fldName = ef.getName();
                        if (GISDefaultValues.getDefaultFieldLayer().getFieldName().equals(fldName))
                        {
                            defCount++;
                        } else if (GISDefaultValues.getDefaultFieldSurfArea().getFieldName().equals(fldName))
                        {
                            defCount++;
                        }

                        if (defCount == 2)
                        {
                            break;
                        }
                    }
                    canAddDefaults.set(defCount != 2);
                } else if (GeomType.GeomEntity.equals(geoType))
                {
                    int defCount = 0;
                    for (EditField ef : this.tableView.getItems())
                    {
                        String fldName = ef.getName();
                        if (GISDefaultValues.getDefaultFieldLayer().getFieldName().equals(fldName))
                        {
                            defCount++;
                        } else if (GISDefaultValues.getDefaultFieldSurfArea().getFieldName().equals(fldName))
                        {
                            defCount++;
                        } else if (GISDefaultValues.getDefaultFieldVolume().getFieldName().equals(fldName))
                        {
                            defCount++;
                        }

                        if (defCount == 3)
                        {
                            break;
                        }
                    }
                    canAddDefaults.set(defCount != 3);
                }
            }
        }
    }

    /**
     * 设置字段的扩展信息，如果字段除IsNull为true外，没有设置其他值，则删掉其扩展信息。
     *
     * @param extField 字段扩展信息
     * @param fld      设置了扩展信息后的属性
     */
    private void setExtField(Field.ExtField extField, Field fld)
    {
        if (fld != null && extField != null)
        {
            if (!extField.getIsNull() || (!XString.isNullOrEmpty(extField.getAlias())) || extField.getDmnID() > 0 || extField.hasDefVal() > 0 || extField.hasMaxVal() > 0 || extField.hasMinVal() > 0 || extField.getShape() != Field.FieldShape.fldShpEdit || extField.getShapeInfoNum() > (short) 0)
            {
                fld.setExtField(extField);
            } else
            {
                fld.deleteExtField();
            }
        }
    }

    /**
     * 验证给定的字段名称是否有效
     *
     * @param editField 编辑行
     * @param fldName   名称
     * @param errorMsg  错误消息
     * @return true表示名称是有效的
     */
    private boolean validateFieldName(EditField editField, String fldName, String errorMsg)
    {
        errorMsg = "";
        if (XString.isNullOrEmpty(fldName))
        {
            errorMsg = "字段名称不能为空。";
        } else if (GISDefaultValues.getMapGISReservedWordList().contains(fldName.toUpperCase()) || GISDefaultValues.getSQLReservedWordList().contains(fldName.toUpperCase()) || GISDefaultValues.getOracleReservedWordList().contains(fldName.toUpperCase()))
        {
            errorMsg = "字段名称不能为保留字。";
        } else if (GISDefaultValues.getDefaultFieldNameList().contains(fldName.toLowerCase()))
        {
            errorMsg = "字段名称不能与默认字段重名。";
        } else
        {
            for (EditField ef : this.tableView.getItems())
            {
                if (ef != editField && fldName.equalsIgnoreCase(ef.getName()))//未完成。这样比较可以吗？
                {
                    errorMsg = "不能与已有字段重名。";
                    break;
                }
            }
        }
        return errorMsg == "";
    }

    //endregion
}
