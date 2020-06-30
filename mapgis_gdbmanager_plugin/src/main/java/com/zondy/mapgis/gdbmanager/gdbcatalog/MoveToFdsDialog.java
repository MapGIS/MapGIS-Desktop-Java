package com.zondy.mapgis.gdbmanager.gdbcatalog;

import com.zondy.mapgis.base.XString;
import com.zondy.mapgis.controls.common.ZDComboBox;
import com.zondy.mapgis.geodatabase.*;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import javax.swing.text.StyledEditorKit;
import java.util.Arrays;
import java.util.List;

/**
 * @author CR
 * @file MoveToFdsDialog.java
 * @brief 移动到数据集
 * @create 2020-01-10.
 */
public class MoveToFdsDialog extends Dialog
{
    private final ZDComboBox<String> comboBoxDS = new ZDComboBox<>();
    private DataBase db;
    private int sourDs;
    private int destDs;
    private List<IXClsInfo> clsInfos;

    public MoveToFdsDialog(DataBase db, int sourDsID, XClsType clsType, int clsID)
    {
        this(db, sourDsID, db != null ? Arrays.asList(db.getXclsInfo(clsType, clsID)) : null);
    }

    public MoveToFdsDialog(DataBase db, int sourDsID, IXClsInfo clsInfo)
    {
        this(db, sourDsID, Arrays.asList(clsInfo));
    }

    public MoveToFdsDialog(DataBase db, int sourDsID, List<IXClsInfo> clsInfos)
    {
        this.setTitle("移动到数据集");
        this.db = db;
        this.sourDs = sourDsID;
        this.destDs = 0;
        this.clsInfos = clsInfos;

        if (this.db != null)
        {
            int[] dsIDs = this.db.getXclses(XClsType.XFds, 0);
            if (dsIDs != null && dsIDs.length > 0)
            {
                for (int dsID : dsIDs)
                {
                    if (dsID != this.sourDs)
                    {
                        String dsName = this.db.getXclsName(XClsType.XFds, dsID);
                        if (!XString.isNullOrEmpty(dsName))
                        {
                            this.comboBoxDS.getItems().add(dsName);
                        }
                    }
                }
            }
            if (this.comboBoxDS.getItems().size() > 0)
            {
                this.comboBoxDS.getSelectionModel().select(0);
            }
        }

        Label label = new Label("目的数据集:");
        GridPane gridPane = new GridPane();
        gridPane.setHgap(6);
        gridPane.add(new Label("目的数据集:"),0,0);
        gridPane.add(this.comboBoxDS,1,0);
        TextField tf = new TextField();
        gridPane.add(tf,1,0);
        tf.setVisible(false);
        GridPane.setHgrow(tf, Priority.ALWAYS);
        this.comboBoxDS.prefWidthProperty().bind(tf.widthProperty());

        DialogPane dialogPane = super.getDialogPane();
        //dialogPane.setPrefWidth(300);
        dialogPane.setContent(gridPane);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, this::okButtonClick);
    }

    public int getDestDs()
    {
        return this.destDs;
    }

    //确定
    private void okButtonClick(ActionEvent event)
    {
        int destDs = (int) this.db.xClsIsExist(XClsType.XFds, this.comboBoxDS.getValue());
        for (IXClsInfo clsInfo : this.clsInfos)
        {
            IVectorCls cls = null;
            if (clsInfo instanceof SFClsInfo)
            {
                cls = new SFeatureCls(this.db);
            } else if (clsInfo instanceof AClsInfo)
            {
                cls = new AnnotationCls(this.db);
            } else if (clsInfo instanceof OClsInfo)
            {
                cls = new ObjectCls(this.db);
            }

            if (cls != null && cls.open(clsInfo.getID(), 0) > 0)
            {
                cls.setdsID(destDs);
                this.destDs = destDs;
                cls.close();
            }
        }
    }
}
