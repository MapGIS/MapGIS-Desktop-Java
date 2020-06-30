package com.zondy.mapgis.utilities;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

/**
 * 实用工具
 *
 * @author cxy
 * @date 2019/11/04
 */
public class UtilityTool {
    public static void main(String[] args) {
        String s = "中地数码";
        try {
            String r = autoBreakString(s, 8, "gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 类名称允许的最大字节长度（非字符长度）
     */
    public static final int MAX_LENGTH_OF_CLASS_NAME = 128;
    /**
     * 图层名称允许的最大字节长度（非字符长度）
     */
    public static final int MAX_LENGTH_OF_MAPLAYER_NAME = 128;

    /**
     * 智能截断text，使其不超过max字节长度，同时截断时不劈开中文
     *
     * @param text 输入文本
     * @param max  允许的最大字节长度（注意不是字符长度，实际允许最大字节数比该值小1）
     * @return 根据max最大长度截断后的文本，如果max无效则直接返回text文本
     */
    public static String autoBreakString(String text, int max, String charsetName) throws UnsupportedEncodingException {
        String rtn = text;
        if (text != null && !text.isEmpty() && max > 0) {
            while (rtn.getBytes(charsetName).length >= max) {
                rtn = rtn.substring(0, rtn.length() - 1);
            }
        }
        return rtn;
    }
}
