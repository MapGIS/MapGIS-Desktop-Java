package com.zondy.mapgis.docitemproperty;

import com.zondy.mapgis.map.DocumentItem;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;

import java.util.Optional;

/**
 * 属性编辑项
 *
 * @author ysp
 * @date 2019-11-22
 */
public class PropertyItem implements PropertySheet.Item {

    private String name;
    private String description;
    private String category;
    private SimpleBooleanProperty editable;
    private SimpleBooleanProperty visible;
    private Class<?> type;
    private Class<?> editor;
    private Object obj;
    private Object objectItem;
    private DocumentItem documentItem;
    ObservableValue<? extends Object> observableValue;

    /**
     * 创建PropertyItem对象
     *
     * @param name
     * @param description
     * @param category
     * @param editable
     * @param visible
     * @param type
     * @param editor
     */
    public PropertyItem(String name, String description, String category, boolean editable, boolean visible, Class<?> type,
                        Class<?> editor) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.type = type;
        this.editor = editor;
        this.editable = new SimpleBooleanProperty(this, "editable", true);
        this.editable.set(editable);
        this.visible = new SimpleBooleanProperty(this, "visible", true);
        this.visible.set(visible);

    }

    /**
     * 创建PropertyItem对象
     *
     * @param name
     * @param description
     * @param category
     * @param editable
     * @param type
     * @param editor
     */
    public PropertyItem(String name, String description, String category, boolean editable, Class<?> type,
                        Class<?> editor) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.editable = new SimpleBooleanProperty(this, "editable", true);
        this.editable.set(editable);
        this.visible = new SimpleBooleanProperty(this, "visible", true);
        this.type = type;
        this.editor = editor;
    }

    /**
     * 类型
     *
     * @return
     */
    @Override
    public Class<?> getType() {
        return this.type;
    }

    /**
     * 分类
     *
     * @return
     */
    @Override
    public String getCategory() {
        return this.category;
    }

    /**
     * 名称
     *
     * @return
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * 描述
     *
     * @return
     */
    @Override
    public String getDescription() {
        return this.description;
    }

    /**
     * 获取值
     *
     * @return
     */
    @Override
    public Object getValue() {
        return this.obj;
    }

    /**
     * 设置值
     *
     * @param o
     */
    @Override
    public void setValue(Object o) {
        this.obj = o;
        if (this.observableValue == null) {
            if (this.obj instanceof Integer) {
                observableValue = new SimpleIntegerProperty((int) this.obj);
            } else if (this.obj instanceof Long) {
                observableValue = new SimpleLongProperty((long) this.obj);
            } else if (this.obj instanceof Double) {
                observableValue = new SimpleDoubleProperty((double) this.obj);
            } else if (this.obj instanceof String) {
                observableValue = new SimpleStringProperty((String) this.obj);
            } else if (this.obj instanceof Boolean) {
                observableValue = new SimpleBooleanProperty((boolean) this.obj);
            } else if (this.obj instanceof Float) {
                observableValue = new SimpleFloatProperty((float) this.obj);
            } else {
                observableValue = new SimpleObjectProperty(this.obj);
            }
        }
        else {
            if (this.obj instanceof Integer) {
                ((SimpleIntegerProperty) observableValue).setValue((int) this.obj);
            } else if (this.obj instanceof Long) {
                ((SimpleLongProperty) observableValue).setValue((long) this.obj);
            } else if (this.obj instanceof Double) {
                ((SimpleDoubleProperty) observableValue).setValue((double) this.obj);
            } else if (this.obj instanceof String) {
                ((SimpleStringProperty) observableValue).setValue((String) this.obj);
            } else if (this.obj instanceof Boolean) {
                ((SimpleBooleanProperty) observableValue).setValue((boolean) this.obj);
            } else if (this.obj instanceof Float) {
                ((SimpleFloatProperty) observableValue).setValue((float) this.obj);
            }else {
                ((SimpleObjectProperty) observableValue).setValue(this.obj);
            }
        }
    }

    /**
     * 设置观察值
     *
     * @param observableValue
     */
    public void setObservableValue(ObservableValue<?> observableValue) {
        this.observableValue = observableValue;
    }

    /**
     * 获取观察值
     *
     * @return
     */
    @Override
    public Optional<ObservableValue<? extends Object>> getObservableValue() {
        return this.observableValue != null ? Optional.of(this.observableValue) : Optional.empty();
    }

    @Override
    public Optional<Class<? extends PropertyEditor<?>>> getPropertyEditorClass() {
//        if (PropertyEditor.class.isAssignableFrom(PopupPropertyEditor.class))
//            System.out.println("PopupPropertyEditor");
//        DocItemPropertyClasses.PopupPropertyEditor editor = new DocItemPropertyClasses.PopupPropertyEditor(null);
//        return editor != null ? Optional.of(editor) : Optional.empty();
        return editor != null ? Optional.of((Class<PropertyEditor<?>>) editor) : Optional.empty();
    }

    /**
     * 设置可编辑性
     * @param editable
     */
    public void setEditable(boolean editable) {
        if (this.editable.get() != editable) {
            this.editable.set(editable);
        }
    }

    /**
     * 获取可编辑性
     * @return
     */
    @Override
    public boolean isEditable() {
        return this.editable.get();
    }

    /**
     * 可编辑属性
     * @return
     */
    public final SimpleBooleanProperty editableProperty() {
        return this.editable;
    }

    /**
     * 设置可见性
     * @param visible
     */
    public void setVisible(boolean visible) {
        if (this.visible.get() != visible) {
            this.visible.set(visible);
        }
    }

    /**
     * 获取可见性
     * @return
     */
    public boolean isVisible() {
        return this.visible.get();
    }

    /**
     * 可见性属性
     * @return
     */
    public final SimpleBooleanProperty visibleProperty() {
        return this.visible;
    }

    /**
     * 设置文档元素
     * @param documentItem
     */
    public void setDocumentItem(DocumentItem documentItem) {
        this.documentItem = documentItem;
    }

    /**
     * 获取文档元素
     * @return
     */
    public DocumentItem getDocumentItem() {
        return this.documentItem;
    }

    /**
     * 设置对象
     * @param objectItem
     */
    public void setItem(Object objectItem) {
        this.objectItem = objectItem;
    }

    /**
     * 获取对象
     * @return
     */
    public Object getItem() {
        return this.objectItem;
    }

}
