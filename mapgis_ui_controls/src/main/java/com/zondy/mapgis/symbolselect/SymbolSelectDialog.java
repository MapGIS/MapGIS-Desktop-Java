package com.zondy.mapgis.symbolselect;

import com.zondy.mapgis.base.MessageBox;
import com.zondy.mapgis.common.DrawSymbolItem;
import com.zondy.mapgis.docitemproperty.PropertyBaseClass;
import com.zondy.mapgis.info.GeomInfo;
import com.zondy.mapgis.info.LinInfo;
import com.zondy.mapgis.info.PntInfo;
import com.zondy.mapgis.info.RegInfo;
import com.zondy.mapgis.systemlib.*;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.util.Callback;
import org.controlsfx.control.PropertySheet;

import java.io.ByteArrayInputStream;
import java.util.UUID;

/**
 * @author ysp
 */
public class SymbolSelectDialog extends Alert {




    private SystemLibrary sysLib;
    private SymbolGeomType symbolGeomType;
    private boolean showPreView;
    private int selectSymbolNo = 0;
    private int selectSymbolSubNo = 0;
    private GeomInfo geomInfo;
    SymbolSelectControl symbolSelectControl;
    /**
     * 选择默认系统库点符号
     */
    public SymbolSelectDialog() {
        super(AlertType.NONE);
        this.sysLib = SystemLibrarys.getSystemLibrarys().getDefaultSystemLibrary();
        this.symbolGeomType = SymbolGeomType.GeomPnt;
        this.showPreView = true;
        this.selectSymbolNo = 0;
        symbolSelectControl = new SymbolSelectControl(sysLib,symbolGeomType,showPreView,selectSymbolNo);
        this.initUI();
    }

    /**
     * 选择指定系统库指定类型符号
     *
     * @param sysLib
     * @param symbolGeomType
     * @param showPreView
     * @param symNum
     */
    public SymbolSelectDialog(SystemLibrary sysLib, SymbolGeomType symbolGeomType, boolean showPreView, int symNum) {
        super(AlertType.NONE);
        this.sysLib = sysLib;
        this.symbolGeomType = symbolGeomType;
        this.showPreView = showPreView;
        this.selectSymbolNo = symNum;
        symbolSelectControl = new SymbolSelectControl(sysLib,symbolGeomType,showPreView,symNum);
        this.initUI();
    }

    /**
     * 设置图形参数对象
     *
     * @param sysLib
     * @param geomInfo
     */
    public SymbolSelectDialog(SystemLibrary sysLib, GeomInfo geomInfo) {
        super(AlertType.NONE);
        this.sysLib = sysLib;
        this.showPreView = true;
        this.geomInfo = geomInfo;
        symbolSelectControl = new SymbolSelectControl(sysLib,geomInfo);

        this.initUI();
    }

    private void initUI() {
        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setContent(symbolSelectControl);
        this.initModality(Modality.APPLICATION_MODAL);
        this.setTitle("选择符号");
        this.setResizable(true);
        ButtonType saveButtonType = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        this.getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        final Button buttonOK = (Button) this.getDialogPane().lookupButton(saveButtonType);
        buttonOK.addEventFilter(ActionEvent.ACTION, this::buttonOK_OnAction);
//        Button buttonCancel = (Button) dialogPane.lookupButton(ButtonType.OK);
//        buttonCancel.addEventFilter(ActionEvent.ACTION, this::buttonCancel_Action);
//        dialogPane.getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);
    }

    private void buttonOK_OnAction(ActionEvent actionEvent) {
        this.selectSymbolNo = symbolSelectControl.getSelectNum();
        if (this.selectSymbolNo <= 0) {
            actionEvent.consume();
            MessageBox.information("请先选择一个符号.", this.getOwner());
        }
    }

    private void buttonCancel_Action(ActionEvent actionEvent) {
//        this.close();
    }

    /**
     * 获取符号编号
     *
     * @return 符号编号
     */
    public int getSelectNum() {
        return this.selectSymbolNo;
    }

    /**
     * 获取线符号子编号(线符号专用)
     *
     * @return 子编号
     */
    public int getSelectSubNum() {
        return this.selectSymbolSubNo;
    }
    /**
     * 获取图形参数(修改图形参数专用)
     *
     * @return 图形参数
     */
    public GeomInfo getGeomInfo() {
        return this.geomInfo;
    }
}
