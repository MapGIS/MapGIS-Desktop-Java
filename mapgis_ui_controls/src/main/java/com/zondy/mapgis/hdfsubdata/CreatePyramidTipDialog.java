package com.zondy.mapgis.hdfsubdata;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * 为...创建金字塔
 */
public class CreatePyramidTipDialog extends Dialog{
    //
    private int m_nResampleType = -1;    // 金字塔采样方式
    private int m_nCompressMethod = -1;  // 金字塔压缩方式
    private int m_nCompressQuality = 100; // 压缩质量
    private boolean m_bUseSelectedItem = false;  // 是否使用所选项以后不再提示

    //
    private Label label1 = null;
    private Label label2 = null;
    private Label label3 = null;
    //
    private VBox vBox = null;
    private Label lable_ResampleType = null;
    private ComboBox<String> comboBox_ResampleType = null;
    private Label lable_CompressMethod = null;
    private ComboBox<String> comboBox_CompressMethod = null;
    private Label lable_CompressQuality = null;
    private TextField textField_CompressQuality = null;
    private CheckBox checkBox_UseSelectItem = null;
    //
    private Button button_Yes = null;
    private Button button_No = null;
    private Button button_Cancle = null;

    public CreatePyramidTipDialog(int resampleType, int compressMethod, int compressQuality, String text)
    {
        setTitle("为...创建金字塔");
        setResizable(false);

        InitDialog();

        DialogPane dialogPane = this.getDialogPane();
        dialogPane.setContent(vBox);
        dialogPane.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        dialogPane.setPrefSize(400,220);
        dialogPane.setMinSize(400,220);

        textField_CompressQuality.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (!"0123456789".contains(event.getCharacter()))
                {
                    event.consume();
                }
            }
        });

        textField_CompressQuality.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("输入出错");

                if (newValue==null || newValue.isEmpty())
                {
                    alert.headerTextProperty().set("请输入一个1到100之间的整数。");
                    alert.showAndWait();
                    return;
                }
                Integer value = Integer.parseInt(newValue);
                if (value>100)
                {
                    alert.headerTextProperty().set("请输入一个1到100之间的整数。");
                    alert.showAndWait();
                    textField_CompressQuality.setText("100");
                }
                else if (value<1)
                {
                    alert.headerTextProperty().set("请输入一个1到100之间的整数。");
                    alert.showAndWait();
                    textField_CompressQuality.setText("1");
                }
            }
        });

        checkBox_UseSelectItem.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
               m_bUseSelectedItem = checkBox_UseSelectItem.isSelected();
            }
        });

        comboBox_CompressMethod.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (comboBox_CompressMethod.getSelectionModel().getSelectedIndex() == 3)
                {
                    textField_CompressQuality.setDisable(false);
                }
                else
                {
                    textField_CompressQuality.setDisable(true);
                }
            }
        });

        button_Yes = (Button)dialogPane.lookupButton(ButtonType.YES);
        button_Yes.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                m_nResampleType = comboBox_ResampleType.getSelectionModel().getSelectedIndex();
                m_nCompressMethod = comboBox_CompressMethod.getSelectionModel().getSelectedIndex();
            }
        });

        button_No = (Button)dialogPane.lookupButton(ButtonType.NO);
        button_No.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                m_nResampleType = comboBox_ResampleType.getSelectionModel().getSelectedIndex();
                m_nCompressMethod = comboBox_CompressMethod.getSelectionModel().getSelectedIndex();
            }
        });

        button_Cancle = (Button)dialogPane.lookupButton(ButtonType.CANCEL);
        button_Cancle.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                m_nResampleType = comboBox_ResampleType.getSelectionModel().getSelectedIndex();
                m_nCompressMethod = comboBox_CompressMethod.getSelectionModel().getSelectedIndex();
            }
        });



    }

    void InitDialog()
    {
        vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setStyle("-fx-font-size: 9pt;");

        GridPane gridPane1 = new GridPane();
        gridPane1.setHgap(5);
        gridPane1.setVgap(5);

        label1 = new Label("此栅格数据无金字塔。金字塔允许以不同的分辨率快速显示。");
        gridPane1.add(label1,0,0);
        label2 = new Label("构建金字塔可能要花费一些时间。");
        gridPane1.add(label2,0,1);
        label3 = new Label("是否要创建金字塔?");
        gridPane1.add(label3,0,2);
        //gridPane1.setAlignment(Pos.CENTER);

        GridPane gridPane2 = new GridPane();
        gridPane2.setHgap(5);
        gridPane2.setVgap(5);

        lable_ResampleType = new Label("金字塔重采样方式:");
        lable_ResampleType.setPrefWidth(110);
        gridPane2.add(lable_ResampleType,0,0);
        comboBox_ResampleType = new ComboBox<>();
        comboBox_ResampleType.setPrefWidth(280);
        comboBox_ResampleType.getItems().addAll("最邻近重采样", "双线性重采样", "双三次重采样" );
        comboBox_ResampleType.getSelectionModel().select(m_nResampleType);
        gridPane2.add(comboBox_ResampleType,1,0);
        lable_CompressMethod = new Label("金字塔压缩方式:");
        gridPane2.add(lable_CompressMethod,0,1);
        comboBox_CompressMethod = new ComboBox<>();
        comboBox_CompressMethod.setPrefWidth(280);
        comboBox_CompressMethod.getItems().addAll("无", "LZW", "DEFLATE", "JPEG");
        comboBox_CompressMethod.getSelectionModel().select(m_nCompressMethod);
        gridPane2.add(comboBox_CompressMethod,1,1);
        lable_CompressQuality = new Label("压缩质量:");
        gridPane2.add(lable_CompressQuality,0,2);
        textField_CompressQuality = new TextField();
        textField_CompressQuality.setText(Integer.toString(m_nCompressQuality));
        gridPane2.add(textField_CompressQuality,1,2);
        checkBox_UseSelectItem = new CheckBox("使用所选项，以后不再显示此对话框。");
        gridPane2.add(checkBox_UseSelectItem,0,3,2,1);

        vBox.getChildren().addAll(gridPane1, gridPane2);
    }

    public int getResampleType()
    {
        return m_nResampleType;
    }

    public int getCompressMethod()
    {
        return m_nCompressMethod;
    }

    public int getCompressQuality()
    {
        return m_nCompressQuality;
    }

    public boolean getUseSelectedItem()
    {
        return m_bUseSelectedItem;
    }

}
