package com.zondy.mapgis.common;

import com.zondy.mapgis.base.XString;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * @author CR
 * @file MapGISErrorDialog.java
 * @brief MapGIS底层getLastError的弹框
 * @create 2020-04-17.
 */
public class MapGISErrorDialog extends Dialog
{
    private final Label labelErrorCode = new Label();
    private final TextArea textAreaMsg = new TextArea();
    private final CheckBox checkBox = new CheckBox("不再提示");//暂时未添加到界面
    private boolean showNoPrompt = false;

    public MapGISErrorDialog()
    {
        this(0, null);
    }

    public MapGISErrorDialog(int errorCode, String errorMsg)
    {
        this.labelErrorCode.setText(String.valueOf(errorCode));
        this.textAreaMsg.setText(errorMsg);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(6);
        gridPane.setVgap(6);
        gridPane.add(new Label("错误码:"), 0, 0);
        gridPane.add(new Label("错误消息:"), 0, 1);
        gridPane.add(this.labelErrorCode, 1, 0);

        this.textAreaMsg.setEditable(false);
        VBox vBox = new VBox(6, gridPane, this.textAreaMsg);
        vBox.setFillWidth(true);
        VBox.setVgrow(this.textAreaMsg, Priority.ALWAYS);

        DialogPane dialogPane = this.getDialogPane();
        dialogPane.setContent(vBox);
        dialogPane.getButtonTypes().addAll(ButtonType.CLOSE);
        dialogPane.setPrefSize(600, 400);

        this.setTitle("MapGIS 错误消息");
        this.setResizable(true);
        this.setOnShown(event -> this.checkBox.setVisible(this.showNoPrompt));
    }

    public boolean isShowNoPrompt()
    {
        return showNoPrompt;
    }

    public void setShowNoPrompt(boolean showNoPrompt)
    {
        this.showNoPrompt = showNoPrompt;
    }

    /**
     * 是否不再提示
     *
     * @return
     */
    public boolean isNoLongerPrompt()
    {
        return this.checkBox.isSelected();
    }


    /// <summary>
    /// 显示最近一次错误框
    /// </summary>
    /// <returns>显示了错误返回true</returns>
    public static boolean ShowLastError()
    {
        boolean rtn = false;
        int errorCode = GeoCommon.getLastError();
        if (errorCode != 0)
        {
            rtn = true;
            String msg = getErrorMsg(errorCode);
            MapGISErrorDialog dlg = new MapGISErrorDialog(errorCode, msg);
            dlg.show();
        }
        return rtn;
    }

    public static boolean ShowLastError(List<Integer> errorCodes)
    {
        boolean rtn = false;
        if (errorCodes != null)
        {
            int lastErrorCode = GeoCommon.getLastError();
            if (lastErrorCode != 0 && errorCodes.contains(lastErrorCode))
            {
                rtn = true;
                String msg = getErrorMsg(lastErrorCode);
                MapGISErrorDialog dlg = new MapGISErrorDialog(lastErrorCode, msg);
                dlg.show();
            }
        }
        return rtn;
    }

    private static String getErrorMsg(int errorCode)
    {
        String msg = GeoCommon.getErrorMsg(errorCode);
        if (msg != null)
        {
            String otherMsg = GeoCommon.getLastAuxiliaryErrorMsg();
            if (!XString.isNullOrEmpty(otherMsg))
            {
                msg += "\n" + otherMsg;
            }
        }
        return msg;
    }
}
