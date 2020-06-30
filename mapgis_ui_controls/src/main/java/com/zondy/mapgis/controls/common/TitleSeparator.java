package com.zondy.mapgis.controls.common;

import com.zondy.mapgis.base.XString;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * @author CR
 * @file TitleSeparator.java
 * @brief 带标题的分隔条
 * @create 2020-03-26.
 */
public class TitleSeparator extends HBox
{
    public TitleSeparator(String text)
    {
        this(text, false);
    }

    /**
     * 创建Label(text)+Separator的分割界面
     *
     * @param text    文本内容
     * @param isFirst 是否是第一个，如果不是第一个，让其与上面间隔12
     * @return
     */
    public TitleSeparator(String text, boolean isFirst)
    {
        Separator separator = new Separator();
        this.setSpacing(6);
        if (!XString.isNullOrEmpty(text))
        {
            this.getChildren().add(new Label(text));
        }
        this.getChildren().add(separator);
        this.setPadding(new Insets(isFirst ? 0 : 12, 0, 6, 0));
        this.setAlignment(Pos.CENTER);
        HBox.setHgrow(separator, Priority.ALWAYS);
    }
}
