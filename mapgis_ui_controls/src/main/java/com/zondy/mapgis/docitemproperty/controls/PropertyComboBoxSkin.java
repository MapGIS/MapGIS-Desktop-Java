package com.zondy.mapgis.docitemproperty.controls;

import com.sun.javafx.scene.control.skin.ComboBoxPopupControl;
import com.sun.javafx.scene.control.skin.resources.ControlResources;
import com.zondy.mapgis.controls.MapGISColorPicker;
import com.zondy.mapgis.docitemproperty.IPropertyEx;
import com.zondy.mapgis.docitemproperty.PropertyBaseClass;
import com.zondy.mapgis.geometry.Dot;
import javafx.css.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.TextAlignment;
import org.controlsfx.control.PropertySheet;

import java.util.*;

/**
 * @Description: TODO
 * @Author ysp
 * @Date 2020/3/30
 **/
public class PropertyComboBoxSkin extends ComboBoxPopupControl<Object> {

    private IPropertyEx iPropertyEx;
    private Label displayNode;
    private PropertySheet propertySheet;
    public PropertyComboBoxSkin(final PropertyComboBox propertyComboBox) {
        super(propertyComboBox, new PropertyComboBoxBehavior(propertyComboBox));
        updateComboBoxMode();
        registerChangeListener(propertyComboBox.valueProperty(), "VALUE");

        // label graphic
        PropertyBaseClass.SizeDouble sizeDouble = new PropertyBaseClass.SizeDouble();
        sizeDouble.setItem(new Dot(1,1));
//        Object obj = propertyComboBox.getIPropertyEx();
        iPropertyEx = propertyComboBox.getIPropertyEx();
        propertySheet =iPropertyEx.getPropertySheet();
        displayNode = new Label();
        displayNode.setText(iPropertyEx.toString());
        displayNode.setTextAlignment(TextAlignment.CENTER);
        displayNode.setMinWidth(propertySheet.getWidth());
    }


    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        if (propertySheet == null) {
            return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
        }
        double width = Math.max(propertySheet.getWidth(), super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset));
        if(width <100) {
            width = 100;
        }
        width = Math.max(width, super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset));
        return width;
    }
    @Override
    protected double computePrefHeight(double height, double topInset, double rightInset, double bottomInset, double leftInset){
        return 30;
    }
    private void updateComboBoxMode() {
        List<String> styleClass = getSkinnable().getStyleClass();
        if (styleClass.contains(MapGISColorPicker.STYLE_CLASS_BUTTON)) {
            //setMode(ComboBoxMode.BUTTON);-zkj
        } else if (styleClass.contains(MapGISColorPicker.STYLE_CLASS_SPLIT_BUTTON)) {
            //setMode(ComboBoxMode.SPLITBUTTON); -zkj
        }
    }


    @Override protected Node getPopupContent() {
        if (propertySheet == null) {

        }
        return propertySheet;
    }

    @Override protected void focusLost() {
        // do nothing
    }

    @Override public void show() {
        super.show();
    }

    /**
     *
     */
    @Override public Node getDisplayNode() {
        displayNode.setText(this.iPropertyEx.toString());
        return displayNode;
    }

    public void syncWithAutoUpdate() {
        if (!getPopup().isShowing() && getSkinnable().isShowing()) {
            // Popup was dismissed. Maybe user clicked outside or typed ESCAPE.
            // Make sure ColorPicker button is in sync.
            getSkinnable().hide();
        }
    }

    @Override protected void layoutChildren(final double x, final double y,
                                            final double w, final double h) {
        updateComboBoxMode();
        super.layoutChildren(x,y,w,h);
    }

    static String getString(String key) {
        return ControlResources.getString("ColorPicker."+key);
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }

    @Override protected javafx.util.StringConverter<Object> getConverter() {
        return null;
    }

    /**
     * ColorPicker does not use a main text field.
     */
    @Override protected TextField getEditor() {
        return null;
    }

}