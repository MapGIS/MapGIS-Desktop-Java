package com.zondy.mapgis.sref;

import com.zondy.mapgis.geodatabase.DataBase;
import com.zondy.mapgis.srs.SRefData;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

/**
 * @author CR
 * @file GeoSRefDialog.java
 * @brief 地理参照系对话框
 * @create 2020-02-04.
 */
public class GeoSRefDialog extends Dialog
{
    private GeoSRefPane geoSRefPane;

    public GeoSRefDialog()
    {
        this(null);
    }

    /**
     * 查看参照系信息
     *
     * @param sref
     */
    public GeoSRefDialog(SRefData sref)
    {
        this.geoSRefPane = new GeoSRefPane(sref);
        this.geoSRefPane.setPadding(new Insets(12, 12, 0, 12));
        this.setTitle(sref != null ? "修改地理坐标系" : "新建地理坐标系");

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setPrefWidth(500);
        dialogPane.setContent(this.geoSRefPane);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, this::okButtonClick);
    }


    /**
     * 获取新建（或修改后）的参照系
     *
     * @return 新建（或修改后）的参照系
     */
    public SRefData getSpatialReference()
    {
        return this.geoSRefPane.getSpatialReference();
    }

    // 确定
    private void okButtonClick(ActionEvent event)
    {
        if (!this.geoSRefPane.validInput())
        {
            event.consume();
        }
    }
}

