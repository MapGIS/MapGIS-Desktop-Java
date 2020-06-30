package com.zondy.mapgis.docitemproperty.controls;

import com.zondy.mapgis.docitemproperty.IPropertyEx;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Skin;

import java.util.Optional;

/**
 * @ClassName PropertyComboBox
 * @Description: TODO
 * @Author ysp
 * @Date 2020/3/30
 **/
public class PropertyComboBox extends ComboBoxBase<Object> {

    /**
     * The style class to specify a Button like appearance of ColorPicker control.
     */
    public static final String STYLE_CLASS_BUTTON = "button";

    /**
     * The style class to specify a SplitMenuButton like appearance of ColorPicker control.
     */
    public static final String STYLE_CLASS_SPLIT_BUTTON = "split-button";

    /**
     * Creates a default ColorPicker instance with a selected color set to white.
     */
    public PropertyComboBox(IPropertyEx iPropertyEx) {
        //this(Color.WHITE);
        this.iPropertyEx = iPropertyEx;
        if (this.iPropertyEx != null) {
            observableValue = new SimpleObjectProperty(this.iPropertyEx.getItem());
        }
    }


    /** {@inheritDoc} */
    @Override protected Skin<?> createDefaultSkin() {
        return new PropertyComboBoxSkin(this);
    }

    private int num = 0;
    private IPropertyEx iPropertyEx;
    public IPropertyEx getIPropertyEx()
    {
        return iPropertyEx;
    }
    private SimpleObjectProperty observableValue;
    public void setIPropertyEx(IPropertyEx val)
    {
        iPropertyEx = val;
    }
    public SimpleObjectProperty getObservableValue() {
        return this.observableValue;
    }

}
