package com.zondy.mapgis.gdbmanager.gdbcatalog.index;

import com.zondy.mapgis.base.MessageBox;
import com.zondy.mapgis.base.XString;
import com.zondy.mapgis.controls.common.ButtonEdit;
import com.zondy.mapgis.filedialog.GDBOpenFileDialog;
import com.zondy.mapgis.geodatabase.FrameNoIndex;
import com.zondy.mapgis.geodatabase.SFeatureCls;
import com.zondy.mapgis.geometry.Rect;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Optional;

/**
 * 创建图幅索引
 *
 * @author CR
 * @file AddFrameIndexDialog.java
 * @brief 创建图幅索引
 * @create 2020-02-27.
 */
public class AddFrameIndexDialog extends Dialog
{
    private final ButtonEdit buttonEditData = new ButtonEdit();
    private final TextField textFieldNum = new TextField();//未完成。使用创建整数框的函数，必须大于0
    private final TextField textFieldX = new TextField();//未完成。使用创建double数值框的函数，必须大于0
    private final TextField textFieldY = new TextField();//未完成。使用创建double数值框的函数，必须大于0
    private final RadioButton radioButton1 = new RadioButton("存在图幅号");
    private final RadioButton radioButton2 = new RadioButton("仅存在分幅数据");
    private final RadioButton radioButton4 = new RadioButton("指定分幅矩形");
    private final RadioButton radioButton3 = new RadioButton("指定分幅比例");
    private final CheckBox checkBoxData = new CheckBox("同时存在分幅数据");
    private final RadioButton radioButton11 = new RadioButton("根据MBR（默认）");
    private final RadioButton radioButton12 = new RadioButton("根据MBR中心");

    private Rect rect;    //数据范围，用于计算分幅所依据的矩形
    private String clsURL;//记录当前数据的URL，用于选择数据时取其目录作为初始目录
    private FrameNoIndex fIndex = new FrameNoIndex();//图幅索引对象

    public AddFrameIndexDialog(Rect rect, int num, String clsURL)
    {
        this.setTitle("创建图幅索引");
        this.rect = rect;
        this.clsURL = clsURL;

        //region 图幅索引类型
        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(radioButton1, radioButton2, radioButton3, radioButton4);
        VBox vBoxType = new VBox(6, radioButton1, radioButton2, radioButton3, radioButton4);
        TitledPane titledPaneType = new TitledPane("图幅索引类型", vBoxType);
        titledPaneType.setCollapsible(false);
        //endregion

        //region 参数设置
        //region MBR
        ToggleGroup toggleGroupMBR = new ToggleGroup();
        toggleGroupMBR.getToggles().addAll(radioButton11, radioButton12);
        toggleGroupMBR.selectToggle(radioButton11);
        HBox hBoxMBR = new HBox(30, radioButton11, radioButton12);
        //endregion

        //region 存在图幅号/图幅数据
        this.buttonEditData.setTooltip(new Tooltip("选择图幅数据"));
        this.buttonEditData.setOnButtonClick(event ->
        {
            GDBOpenFileDialog dlg = new GDBOpenFileDialog();
            dlg.setTitle("选择简单要素类");
            dlg.setFilter("简单要素类(*.sfcls)|sfcls");
            if (!XString.isNullOrEmpty(this.clsURL))
            {
                String initDir = XString.remove(this.clsURL, this.clsURL.lastIndexOf("/"));
                //dlg.setInitialDirectory(XString.remove(initDir, initDir.lastIndexOf("/")));
            }

            Optional<String[]> optional = dlg.showAndWait();
            if (optional != null && optional.isPresent())
            {
                String[] files = optional.get();
                if (files != null && files.length > 0)
                {
                    this.buttonEditData.setText(dlg.getFileName());
                }
            }
        });
        checkBoxData.selectedProperty().addListener((o, ov, nv) ->
        {
            this.buttonEditData.setDisable(!nv);
        });
        Label labelData = new Label("分幅数据:");
        VBox vBox1 = new VBox(6, checkBoxData, labelData, this.buttonEditData);
        //endregion

        //region 分幅比例
        this.textFieldNum.setText(String.valueOf(num));
        HBox hBox3 = new HBox(6, new Label("分幅比例:"), this.textFieldNum);
        HBox.setHgrow(this.textFieldNum, Priority.ALWAYS);
        //endregion

        //region 分幅矩形
        GridPane gridPane = new GridPane();
        gridPane.setHgap(6);
        gridPane.setVgap(6);
        gridPane.add(new Label("△x:"), 0, 0);
        gridPane.add(new Label("△y:"), 0, 1);
        gridPane.add(this.textFieldX, 1, 0);
        gridPane.add(this.textFieldY, 1, 1);
        GridPane.setHgrow(this.textFieldX, Priority.ALWAYS);
        VBox vBox4 = new VBox(6, new Label("分幅矩形:"), gridPane);
        //endregion

        VBox vBoxParam = new VBox(6, vBox1, hBox3, vBox4, hBoxMBR);
        TitledPane titledPaneParam = new TitledPane("参数设置", vBoxParam);
        titledPaneParam.setCollapsible(false);
        //endregion

        //region 界面布局
        VBox root = new VBox(6, titledPaneType, titledPaneParam);
        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setPrefSize(450, 414);
        dialogPane.setMinSize(450, 414);
        dialogPane.setContent(root);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, this::okButtonClick);
        //endregion

        //region 修改图幅索引类型时，更新参数设置界面
        toggleGroup.selectedToggleProperty().addListener((o, ov, nv) ->
        {
            vBox1.setManaged(nv == radioButton1 || nv == radioButton2);
            vBox1.setVisible(vBox1.isManaged());
            if (vBox1.isVisible())
            {
                boolean canSel = nv == radioButton2 || (nv == radioButton1 && checkBoxData.isSelected());
                this.buttonEditData.setDisable(!canSel);
            }
            checkBoxData.setManaged(nv == radioButton1);
            checkBoxData.setVisible(checkBoxData.isManaged());
            labelData.setManaged(nv == radioButton2);
            labelData.setVisible(labelData.isManaged());
            hBox3.setManaged(nv == radioButton3);
            hBox3.setVisible(hBox3.isManaged());
            vBox4.setManaged(nv == radioButton4);
            vBox4.setVisible(vBox4.isManaged());
            hBoxMBR.setManaged(nv != radioButton1);
            hBoxMBR.setVisible(hBoxMBR.isManaged());
        });

        toggleGroup.selectToggle(radioButton1);
        //endregion
    }

    /**
     * 设置图幅数据
     *
     * @return 设置成功返回true
     */
    private boolean setFrameData()
    {
        boolean rtn = true;
        String url = this.buttonEditData.getText();
        if (XString.isNullOrEmpty(url))
        {
            MessageBox.information("请选择图幅数据。");
        } else
        {
            SFeatureCls sfCls = new SFeatureCls();
            if (sfCls.openByURL(url) > 0)
            {
                this.fIndex.setDivClsID(sfCls.getClsID());
                sfCls.close();
            } else
            {
                MessageBox.information("所选图幅数据打开失败。");
                rtn = false;
            }
        }
        return rtn;
    }

    //确定
    private void okButtonClick(ActionEvent event)
    {
        if (this.radioButton1.isSelected())
        {
            if (this.checkBoxData.isSelected())
            {
                if (!this.setFrameData())
                {
                    event.consume();
                }
            }
            this.fIndex.setIsFrameNoKnown(true);
            this.fIndex.setHasFrameData(this.checkBoxData.isSelected());
        } else if (this.radioButton2.isSelected())
        {
            if (!this.setFrameData())
            {
                event.consume();
            } else
            {
                this.fIndex.setHasFrameData(true);
                this.fIndex.setUseRectCenter(this.radioButton12.isSelected());
            }
        } else if (this.radioButton2.isSelected())
        {
            this.fIndex.setUseRectCenter(this.radioButton12.isSelected());
            if (this.rect != null)
            {
                double scale = Double.valueOf(this.textFieldNum.getText());
                Rect rc = new Rect(this.rect.getXMin(), this.rect.getYMin(), this.rect.getXMin() + (this.rect.getXMax() - this.rect.getXMin()) / scale, this.rect.getYMin() + (this.rect.getYMax() - this.rect.getYMin()) / scale);
                this.fIndex.setDivRect(rc);
            }
        } else if (this.radioButton2.isSelected())
        {
            this.fIndex.setUseRectCenter(this.radioButton12.isSelected());
            if (this.rect != null)
            {
                Rect rc = new Rect(this.rect.getXMin(), this.rect.getYMin(), this.rect.getXMin() + Double.valueOf(this.textFieldX.getText()), this.rect.getYMin() + Double.valueOf(this.textFieldY.getText()));
                this.fIndex.setDivRect(rc);
            }
        }
    }

    /**
     * 获取创建的图幅索引
     *
     * @return 创建的图幅索引
     */
    public FrameNoIndex getFrameIndex()
    {
        return this.fIndex;
    }
}
