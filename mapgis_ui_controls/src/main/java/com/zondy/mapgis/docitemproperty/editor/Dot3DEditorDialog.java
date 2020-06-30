package com.zondy.mapgis.docitemproperty.editor;

import com.zondy.mapgis.controls.common.NumberTextField;
import com.zondy.mapgis.geometry.Dot3D;
import com.zondy.mapgis.sqlquery.SQLQuery;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.math.BigDecimal;

/**
 * <p>显示比例编辑框</p>
 *
 * @author : ysp
 * @date : 2020-04-28
 **/
public class Dot3DEditorDialog extends Alert {
    private Dot3D dot3D;
    private NumberTextField numberTextFieldX;
    private NumberTextField numberTextFieldY;
    private NumberTextField numberTextFieldZ;

    public Dot3DEditorDialog(Dot3D dot3D) {
        super(AlertType.NONE);
        this.dot3D = dot3D;
        GridPane gridPane = new GridPane();
        Label labelX = new Label("x:");
        Label labelY = new Label("y:");
        Label labelZ = new Label("z:");
        numberTextFieldX = new NumberTextField();
        numberTextFieldY = new NumberTextField();
        numberTextFieldZ = new NumberTextField();
        gridPane.add(labelX, 0, 0);
        gridPane.add(labelY, 0, 1);
        gridPane.add(labelZ, 0, 2);
        gridPane.add(numberTextFieldX, 1, 0);
        gridPane.add(numberTextFieldY, 1, 1);
        gridPane.add(numberTextFieldZ, 1, 2);
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(20),new ColumnConstraints(100));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        GridPane.setHgrow(numberTextFieldX, Priority.ALWAYS);
        if (dot3D != null) {
            numberTextFieldX.setNumber(new BigDecimal(dot3D.getX()));
            numberTextFieldY.setNumber(new BigDecimal(dot3D.getY()));
            numberTextFieldZ.setNumber(new BigDecimal(dot3D.getZ()));
        }
        this.setResizable(false);
        this.setTitle("显示比例");
        this.getDialogPane().setContent(gridPane);
        ButtonType saveButtonType = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        this.getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        final Button buttonOK = (Button) this.getDialogPane().lookupButton(saveButtonType);
        buttonOK.addEventFilter(ActionEvent.ACTION, this::buttonOK_OnAction);
    }

    private <T extends Event> void buttonOK_OnAction(T t) {
        dot3D = new Dot3D(numberTextFieldX.getNumber().intValue(),
                numberTextFieldY.getNumber().intValue(),
                numberTextFieldZ.getNumber().intValue());
    }

    public Dot3D getDot3D() {
        return this.dot3D;
    }
}
