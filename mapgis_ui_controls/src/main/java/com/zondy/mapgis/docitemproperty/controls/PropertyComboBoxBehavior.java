package com.zondy.mapgis.docitemproperty.controls;

import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import com.sun.javafx.scene.control.behavior.KeyBinding;

import java.util.ArrayList;
import java.util.List;

import static javafx.scene.input.KeyCode.*;
import static javafx.scene.input.KeyEvent.KEY_PRESSED;

/**
 * @ClassName PropertyComboBoxBehavior
 * @Description: TODO
 * @Author ysp
 * @Date 2020/3/30
 **/
public class PropertyComboBoxBehavior extends ComboBoxBaseBehavior<Object> {

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     *
     */
    public PropertyComboBoxBehavior(final PropertyComboBox propertyComboBox) {
        super(propertyComboBox, COLOR_PICKER_BINDINGS);
    }

    /***************************************************************************
     *                                                                         *
     * Key event handling                                                      *
     *                                                                         *
     **************************************************************************/

    /**
     * Opens the Color Picker Palette.
     */
    protected static final String OPEN_ACTION = "Open";

    /**
     * Closes the Color Picker Palette.
     */
    protected static final String CLOSE_ACTION = "Close";


    protected static final List<KeyBinding> COLOR_PICKER_BINDINGS = new ArrayList<KeyBinding>();
    static {
//        COLOR_PICKER_BINDINGS.addAll(COMBO_BOX_BASE_BINDINGS);
        COLOR_PICKER_BINDINGS.add(new KeyBinding(ESCAPE, KEY_PRESSED, CLOSE_ACTION));
        COLOR_PICKER_BINDINGS.add(new KeyBinding(SPACE, KEY_PRESSED, OPEN_ACTION));
        COLOR_PICKER_BINDINGS.add(new KeyBinding(ENTER, KEY_PRESSED, OPEN_ACTION));

    }

    @Override protected void callAction(String name) {
        if (OPEN_ACTION.equals(name)) {
            show();
        } else if(CLOSE_ACTION.equals(name)) {
            hide();
        }
        else super.callAction(name);
    }

    /**************************************************************************
     *                                                                        *
     * Mouse Events                                                           *
     *                                                                        *
     *************************************************************************/

    @Override public void onAutoHide() {
        // when we click on some non  interactive part of the
        // Color Palette - we do not want to hide.
        PropertyComboBox propertyComboBox = (PropertyComboBox)getControl();
        PropertyComboBoxSkin propertyComboBoxSkin = (PropertyComboBoxSkin)propertyComboBox.getSkin();
        propertyComboBoxSkin.syncWithAutoUpdate();
        // if the ColorPicker is no longer showing, then invoke the super method
        // to keep its show/hide state in sync.
        if (!propertyComboBox.isShowing()) super.onAutoHide();
    }
}