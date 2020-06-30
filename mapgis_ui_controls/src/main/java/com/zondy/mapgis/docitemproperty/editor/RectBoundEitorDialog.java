package com.zondy.mapgis.docitemproperty.editor;

import com.zondy.mapgis.controls.common.NumberTextField;
import com.zondy.mapgis.geometry.Dot3D;
import com.zondy.mapgis.geometry.Rect;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.math.BigDecimal;

/**
 * <p>渲染范围编辑对话框</p>
 *
 * @author : ysp
 * @date : 2020-04-29
 **/
public class RectBoundEitorDialog extends Alert {
    private Rect rect;
    private NumberTextField numberTextFieldXmin;
    private NumberTextField numberTextFieldYmin;
    private NumberTextField numberTextFieldXmax;
    private NumberTextField numberTextFieldYmax;

    public RectBoundEitorDialog(Rect rect) {
        super(Alert.AlertType.NONE);
        this.rect = rect;
        GridPane gridPane = new GridPane();
        Label labelXmin = new Label("xmin:");
        Label labelYmin = new Label("ymin:");
        Label labelXmax = new Label("Xmax:");
        Label labelYmax = new Label("Xmax:");
        numberTextFieldXmin = new NumberTextField();
        numberTextFieldYmin = new NumberTextField();
        numberTextFieldXmax = new NumberTextField();
        numberTextFieldYmax = new NumberTextField();
        gridPane.add(labelXmin, 0, 0);
        gridPane.add(labelYmin, 0, 1);
        gridPane.add(labelXmax, 0, 2);
        gridPane.add(labelYmax, 0, 3);
        gridPane.add(numberTextFieldXmin, 1, 0);
        gridPane.add(numberTextFieldYmin, 1, 1);
        gridPane.add(numberTextFieldXmax, 1, 2);
        gridPane.add(numberTextFieldYmax, 1, 3);
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(20),new ColumnConstraints(100));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        GridPane.setHgrow(numberTextFieldXmin, Priority.ALWAYS);
        if (this.rect != null) {
            numberTextFieldXmin.setNumber(new BigDecimal(this.rect.getXMin()));
            numberTextFieldYmin.setNumber(new BigDecimal(this.rect.getYMin()));
            numberTextFieldXmax.setNumber(new BigDecimal(this.rect.getXMax()));
            numberTextFieldYmax.setNumber(new BigDecimal(this.rect.getYMax()));
        }
        this.setResizable(false);
        this.setTitle("范围");
        this.getDialogPane().setContent(gridPane);
        ButtonType saveButtonType = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        this.getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        final Button buttonOK = (Button) this.getDialogPane().lookupButton(saveButtonType);
        buttonOK.addEventFilter(ActionEvent.ACTION, this::buttonOK_OnAction);
    }

    private <T extends Event> void buttonOK_OnAction(T t) {
        rect = new Rect(
                numberTextFieldXmin.getNumber().doubleValue(),
                numberTextFieldYmin.getNumber().doubleValue(),
                numberTextFieldXmax.getNumber().doubleValue(),
                numberTextFieldYmax.getNumber().doubleValue());
    }

    public Rect getRect() {
        return this.rect;
    }
}
