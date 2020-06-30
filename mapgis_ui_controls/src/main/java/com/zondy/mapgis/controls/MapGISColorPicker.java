package com.zondy.mapgis.controls;

import com.zondy.mapgis.controls.event.ColorChangedListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import com.zondy.mapgis.controls.skin.MapGISColorPickerSkin;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * MapGIS颜色选择器
 */
public class MapGISColorPicker extends ComboBoxBase<Color> {

    /**
     * The style class to specify a Button like appearance of ColorPicker control.
     */
    public static final String STYLE_CLASS_BUTTON = "button";

    /**
     * The style class to specify a SplitMenuButton like appearance of ColorPicker control.
     */
    public static final String STYLE_CLASS_SPLIT_BUTTON = "split-button";

    /**
     * The custom colors added to the Color Palette by the user.
     */
    private ObservableList<Color> customColors = FXCollections.<Color>observableArrayList();
    /**
     * Gets the list of custom colors added to the Color Palette by the user.
     */
    public final ObservableList<Color>  getCustomColors() {
        return customColors;
    }

    /**
     * Creates a default ColorPicker instance with a selected color set to white.
     */
    public MapGISColorPicker() {
        //this(Color.WHITE);
    }

    /**
     * Creates a ColorPicker instance and sets the selected color to the given color.
     * @param color to be set as the currently selected color of the ColorPicker.
     */
//    public MapGISColorPicker(Color color) {
//        setValue(color);
//        //getStyleClass().add(DEFAULT_STYLE_CLASS); -zkj
//    }

    /***************************************************************************
     *                                                                         *
     * Methods                                                                 *
     *                                                                         *
     **************************************************************************/

    /** {@inheritDoc} */
    @Override protected Skin<?> createDefaultSkin() {
        return new MapGISColorPickerSkin(this);
    }

    /***************************************************************************
     *                                                                         *
     * Stylesheet Handling                                                     *
     *                                                                         *
     **************************************************************************/

    private static final String DEFAULT_STYLE_CLASS = "color-picker";
    private int num = 0;

    public int getSelectColorNumber()
    {
        return num;
    }
    public void setSelectColorNumber(int val)
    {
        num = val;
    }

    private Collection colorChangedListenerList;
    public void addSelectColorChangedListener(ColorChangedListener listener)
    {
        if (colorChangedListenerList == null) {
            colorChangedListenerList = new HashSet();
        }
        colorChangedListenerList.add(listener);
    }
    public void removeSelectColorChangedListener(ColorChangedListener listener)
    {
        if (colorChangedListenerList == null)
            return;
        colorChangedListenerList.remove(listener);
    }
    public void notifySelectColorChangedListener(Event event)
    {
        if(colorChangedListenerList != null) {
            Iterator iter = colorChangedListenerList.iterator();
            while (iter.hasNext()) {
                ColorChangedListener listener = (ColorChangedListener) iter.next();
                listener.colorChanged();
            }
        }
    }
}
