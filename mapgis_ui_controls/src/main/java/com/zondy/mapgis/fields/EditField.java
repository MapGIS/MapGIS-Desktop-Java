package com.zondy.mapgis.fields;

import com.zondy.mapgis.att.*;
import com.zondy.mapgis.base.LanguageConvert;
import com.zondy.mapgis.base.XString;
import com.zondy.mapgis.geodatabase.IVectorCls;
import javafx.beans.property.*;

/**
 * @author CR
 * @file EditField.java
 * @brief Field编辑包装类
 * @create 2019-11-28.
 */
public class EditField
{
    private Field field;
    private Field.ExtField extField;
    private boolean fldRowEditable = true;//字段行是否可编辑，修改和删除该字段
    private StringProperty name = new SimpleStringProperty();
    private StringProperty alias = new SimpleStringProperty();
    private ObjectProperty<Field.FieldType> type = new SimpleObjectProperty<>(Field.FieldType.fldStr);
    private IntegerProperty length = new SimpleIntegerProperty();
    //private IntegerProperty pointLen = new SimpleIntegerProperty();
    private ObjectProperty defVal = new SimpleObjectProperty();
    private BooleanProperty allowEdit = new SimpleBooleanProperty();
    private BooleanProperty allowNull = new SimpleBooleanProperty();

    private EditField()
    {
    }

    /**
     * 构造界面编辑字段（Field包装类）
     *
     * @param fld 属性字段
     */
    public EditField(Field fld)
    {
        this(fld, true);
    }

    /**
     * 构造界面编辑字段（Field包装类）
     *
     * @param fld 属性字段
     */
    public EditField(Field fld, boolean editable)
    {
        this.field = fld;
        if (this.field != null)
        {
            Field.ExtField extField = fld.getExtField();

            this.name.set(this.field.getFieldName());
            if (extField != null)
            {
                this.alias.set(extField.getAlias());
                if (extField.hasDefVal() > 0)
                {
                    Object dv = extField.getDefVal();
                    //未完成。可能还需要如下转换
                    //if (dv != null)
                    //{
                    //    if (fld.getFieldType() == FieldType.fldBool)
                    //    {
                    //        dv = (boolean) dv ? "是" : "否";
                    //    } else if (extField.getShape() == FieldShape.fldShpCombo)
                    //    {
                    //        dv = FieldFunctions.getComboNameByFieldValue(extField, dv);
                    //    }
                    //    else if (this.isSubtypeField(fld))
                    //    {
                    //        SubTypes subTypes = ((IVectorCls) this.basCls).getSubTypes();
                    //        if (subTypes != null)
                    //        {
                    //            int subCode = (int) dv;
                    //            for (int i = 0; i < subTypes.getCount(); i++)
                    //            {
                    //                SubType subType = null;
                    //                if (subTypes.getItem(i, subType) && subType != null && subType.getCode() == subCode)
                    //                {
                    //                    dv = subType.getName();
                    //                    break;
                    //                }
                    //            }
                    //        }
                    //    }
                    //}
                    this.defVal.set(dv);
                }
            }
            this.type.set(this.field.getFieldType());
            this.length.set(this.field.getFieldLength());
            //this.pointLen.set(this.field.getPointLength());
            //this.allowEdit.set(this.field.getEditable() != 0 && this.field.getEditable() != 2);
            this.allowNull.set(extField == null || extField.getIsNull());
        }
        this.fldRowEditable = editable;
    }

    /**
     * 新建字段
     *
     * @param name 字段名称，为空则给NewField
     * @return 新建的字段
     */
    public static EditField newEditField(String name)
    {
        EditField editField = new EditField();
        editField.name.set(!XString.isNullOrEmpty(name) ? name : "NewField");
        editField.alias.set(null);
        editField.type.set(Field.FieldType.fldStr);
        editField.length.set((short) 255);
        //editField.pointLen.set((short) 0);
        editField.allowEdit.set(true);
        editField.allowNull.set(true);
        editField.setFieldRowEditable(true);
        return editField;
    }

    //region 字段、扩展字段、可编辑性

    /**
     * 获取本类中包装的属性字段对象
     *
     * @return 属性字段
     */
    public Field getField()
    {
        return this.field;
    }

    /**
     * 获取界面上的扩展字段，可能还没有保存到Field
     *
     * @return 编辑中的扩展字段
     */
    public Field.ExtField getExtField()
    {
        return this.extField;
    }

    /**
     * 设置扩展字段
     *
     * @param extField 扩展字段
     */
    public void setExtField(Field.ExtField extField)
    {
        this.extField = extField;
    }

    /**
     * 获取字段行是否可被编辑
     *
     * @return 字段行是否可被编辑
     */
    public boolean getFieldRowEditable()
    {
        return this.fldRowEditable;
    }

    /**
     * 设置字段行的可编辑性
     *
     * @param fldRowEditable 字段行是否可被编辑
     */
    public void setFieldRowEditable(boolean fldRowEditable)
    {
        this.fldRowEditable = fldRowEditable;
    }
    //endregion

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

    public String getAlias()
    {
        return alias.get();
    }

    public StringProperty aliasProperty()
    {
        return alias;
    }

    public void setAlias(String alias)
    {
        this.alias.set(alias);
    }

    public Field.FieldType getType()
    {
        return type.get();
    }

    public ObjectProperty<Field.FieldType> typeProperty()
    {
        return type;
    }

    public void setType(Field.FieldType type)
    {
        this.type.set(type);
    }

    public int getLength()
    {
        return length.get();
    }

    public IntegerProperty lengthProperty()
    {
        return length;
    }

    public void setLength(int mskLen)
    {
        this.length.set(mskLen);
        //字段长度发生改变后自动调整小数位数
        //Field.FieldType fldType = this.getType();
        //if (fldType == Field.FieldType.fldFloat || fldType == Field.FieldType.fldDouble )
        //{
        //    short maxPLen = (short) (mskLen - 2);
        //    if (maxPLen < this.pointLen.get())
        //    {
        //        this.pointLen.set(Math.max(0, maxPLen));
        //    }
        //}
    }

    //public int getPointLen()
    //{
    //    return pointLen.get();
    //}
    //
    //public IntegerProperty pointLenProperty()
    //{
    //    return pointLen;
    //}
    //
    //public void setPointLen(int pointLen)
    //{
    //    this.pointLen.set(pointLen);
    //}

    public Object getDefVal()
    {
        return defVal.get();
    }

    public ObjectProperty defValProperty()
    {
        return defVal;
    }

    public void setDefVal(Object defVal)
    {
        this.defVal.set(defVal);
    }

    public boolean isAllowEdit()
    {
        return allowEdit.get();
    }

    public BooleanProperty allowEditProperty()
    {
        return allowEdit;
    }

    public void setAllowEdit(boolean allowEdit)
    {
        this.allowEdit.set(allowEdit);
    }

    public boolean isAllowNull()
    {
        return allowNull.get();
    }

    public BooleanProperty allowNullProperty()
    {
        return allowNull;
    }

    public void setAllowNull(boolean allowNull)
    {
        this.allowNull.set(allowNull);
    }
}
