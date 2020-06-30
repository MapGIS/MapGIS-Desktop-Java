package com.zondy.mapgis.base;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author CR
 * @file UIFunctions.java
 * @brief 界面上的相关方法
 * @create 2019-12-21.
 */
public class UIFunctions {
    /**
     * 在给定控件的右下角显示错误提示
     *
     * @param edit     控件
     * @param errorMsg 错误消息
     * @param tooltip  提示控件
     */
    public static void showErrorTip(Node edit, String errorMsg, Tooltip tooltip) {
        if (edit != null && !XString.isNullOrEmpty(errorMsg)) {
            if (tooltip == null) {
                tooltip = new Tooltip();
                tooltip.setAutoHide(true);
            }

            if (tooltip.isShowing()) {
                tooltip.hide();
            }

            tooltip.setText(errorMsg);
            Bounds screenBounds = edit.localToScreen(edit.getBoundsInLocal());
            if (screenBounds != null) {
                tooltip.show(edit, screenBounds.getMaxX(), screenBounds.getMaxY());
            }
        }
    }

    /**
     * 创建限制输入整数的文本框（整数）
     *
     * @return
     */
    public static TextField newIntTextField() {
        return newIntTextField(false);
    }

    /**
     * 创建限制输入整数的文本框
     *
     * @param isNonNegative 是否限制非负整数
     * @return
     */
    public static TextField newIntTextField(boolean isNonNegative) {
        return newIntTextField(0, isNonNegative);
    }

    /**
     * 创建限制输入整数的文本框
     *
     * @return
     */
    public static TextField newIntTextField(int val, boolean isNonNegative) {
        return newIntTextField(val, Integer.MIN_VALUE, Integer.MAX_VALUE, isNonNegative);
    }

    /**
     * 创建限制输入整数的文本框
     *
     * @return
     */
    public static TextField newIntTextField(int value, int minValue, int maxValue, boolean isNonNegative) {
        TextField tf = new TextField(String.valueOf(value));
        tf.textProperty().addListener((o, ov, nv) ->
        {
            String strError = "";
            String regEx = isNonNegative ? "^0|[1-9]\\d*" : "^0|-?[1-9]\\d*$";
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(nv);
            if (!matcher.matches()) {
                strError = "请输入整数";
            } else {
                int iVal = Integer.valueOf(nv);
                if (iVal < minValue) {
                    strError = String.format("不能小于%d", minValue);
                } else if (iVal > maxValue) {
                    strError = String.format("不能大于%d", maxValue);
                }
            }

            if (strError != "") {
                UIFunctions.showErrorTip(tf, strError, new Tooltip());
                tf.setText(ov);
            }
        });
        return tf;
    }

    /**
     * 创建限制输入小数的文本框(正数）
     *
     * @return 文本框
     */
    public static TextField newDecimalTextField() {
        return UIFunctions.newDecimalTextField(0);
    }

    /**
     * 创建限制输入小数的文本框(正数）
     *
     * @param initVal 初始值
     * @return 文本框
     */
    public static TextField newDecimalTextField(double initVal) {
        return UIFunctions.newDecimalTextField(initVal, -1 * Double.MAX_VALUE, Double.MAX_VALUE);
    }

    /**
     * 创建限制输入小数的文本框
     *
     * @param initVal  初始值
     * @param canMinus 是否能输入负数
     * @return 文本框
     */
    /**
     * 创建限制输入整数的文本框(正数）
     *
     * @return
     */
    public static TextField newDecimalTextField(double value, double minValue, double maxValue) {
        TextField tf = new TextField(String.valueOf(value));
        tf.textProperty().addListener((o, ov, nv) ->
        {
            String strError = "";
            String regEx = "^-?([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*|0?.0+|0)$";
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(nv);
            if (!matcher.matches()) {
                strError = "格式不匹配";
            } else {
                double iVal = Double.valueOf(nv);
                if (iVal < minValue) {
                    strError = String.format("不能小于%f", minValue);
                } else if (iVal > maxValue) {
                    strError = String.format("不能大于%f", maxValue);
                }
            }

            if (strError != "") {
                UIFunctions.showErrorTip(tf, strError, new Tooltip());
                tf.setText(ov);
            }
        });
        return tf;
    }
}
