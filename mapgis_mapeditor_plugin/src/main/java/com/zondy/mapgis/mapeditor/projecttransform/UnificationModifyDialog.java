package com.zondy.mapgis.mapeditor.projecttransform;

import com.zondy.mapgis.filedialog.FolderType;
import com.zondy.mapgis.filedialog.GDBSelectFolderDialog;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

/**
 * 统改投影参数
 */
public class UnificationModifyDialog extends Dialog {
    private Stage stage;

    public UnificationModifyDialog() {
        setTitle("统改设置");
        setWidth(400);
        setHeight(450);
        //初始化界面布局
        initialize();
        //绑定事件
        bindAction();
        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setContent(this.gridPane);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        stage = (Stage) dialogPane.getScene().getWindow();

        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, this::okButtonClick);
        //setResultConverter(dialogButton -> dialogButton == ButtonType.OK ? ButtonType.OK : ButtonType.CANCEL);
    }

    private GridPane gridPane = null;

    //统改前后缀
    private CheckBox befExtCheckBox = null;
    private CheckBox afExtCheckBox = null;
    private TextField befExtText = null;
    private TextField afExtText = null;
    //统改目的数据GDB目录
    private CheckBox gdbDirCheck = null;
    private TextField gdbDirText = null;
    private Button gdbDirBtn = null;

    //统改目的数据File目录
    private CheckBox fileDirCheck = null;
    private TextField fileDirText = null;
    private Button fileDirBtn = null;

    /**
     * 初始化界面布局
     */
    private void initialize() {
        gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(5, 5, 5, 5));
        //region 统改目的数据名称
        TitledPane extTPane = new TitledPane();
        extTPane.setPadding(new Insets(5., 5, 5, 5));
        gridPane.add(extTPane, 0, 0);
        extTPane.setCollapsible(false);
        extTPane.setText("统改目的数据名称");

        GridPane desExtNameGrid = new GridPane();
        desExtNameGrid.setHgap(5);
        desExtNameGrid.setVgap(5);
        desExtNameGrid.setPadding(new Insets(5, 5, 5, 5));
        extTPane.setContent(desExtNameGrid);

        befExtCheckBox = new CheckBox("统加前缀:");
        befExtCheckBox.setSelected(false);
        afExtCheckBox = new CheckBox("统加后缀:");
        afExtCheckBox.setSelected(false);
        befExtText = new TextField();
        befExtText.setDisable(true);
        befExtText.setPrefWidth(200);
        befExtText.setText("F_");
        afExtText = new TextField();
        afExtText.setDisable(true);
        afExtText.setText("_0");
        int rowIndex = 0;
        desExtNameGrid.add(befExtCheckBox, 0, rowIndex);
        desExtNameGrid.add(befExtText, 1, rowIndex);
        rowIndex++;
        desExtNameGrid.add(afExtCheckBox, 0, rowIndex);
        desExtNameGrid.add(afExtText, 1, rowIndex);

        extTPane.setContent(desExtNameGrid);

        //endregion

        //region 统改MapGIS目的数据目录
        rowIndex++;
        TitledPane desDirGDBTPane = new TitledPane();
        desDirGDBTPane.setPadding(new Insets(5., 5, 5, 5));
        gridPane.add(desDirGDBTPane, 0, 1);
        desDirGDBTPane.setCollapsible(false);
        desDirGDBTPane.setText("统改MapGIS目的数据目录");

        GridPane desDirGDBGrid = new GridPane();
        desDirGDBGrid.setHgap(5);
        desDirGDBGrid.setVgap(5);
        desDirGDBGrid.setPadding(new Insets(5, 5, 5, 5));
        desDirGDBTPane.setContent(desDirGDBGrid);

        gdbDirCheck = new CheckBox("统改目录:");
        gdbDirText = new TextField();
        gdbDirText.setPrefWidth(200);
        gdbDirText.setEditable(false);
        gdbDirText.setDisable(true);
        gdbDirBtn = new Button("...");
        gdbDirBtn.setDisable(true);
        desDirGDBGrid.add(gdbDirCheck, 0, rowIndex);
        desDirGDBGrid.add(gdbDirText, 1, rowIndex);
        desDirGDBGrid.add(gdbDirBtn, 2, rowIndex);

        //endregion

        //region 统改File目的数据目录
        rowIndex++;
        TitledPane desDirFileTPane = new TitledPane();
        desDirFileTPane.setPadding(new Insets(5., 5, 5, 5));
        gridPane.add(desDirFileTPane, 0, 2);
        desDirFileTPane.setCollapsible(false);
        desDirFileTPane.setText("统改磁盘目的数据目录");

        GridPane desDirFileGrid = new GridPane();
        desDirFileGrid.setHgap(5);
        desDirFileGrid.setVgap(5);
        desDirFileGrid.setPadding(new Insets(5, 5, 5, 5));
        desDirFileTPane.setContent(desDirFileGrid);

        fileDirCheck = new CheckBox("统改目录:");
        fileDirText = new TextField();
        fileDirText.setEditable(false);
        fileDirText.setPrefWidth(200);
        fileDirText.setDisable(true);
        fileDirBtn = new Button("...");
        fileDirBtn.setDisable(true);

        desDirFileGrid.add(fileDirCheck, 0, rowIndex);
        desDirFileGrid.add(fileDirText, 1, rowIndex);
        desDirFileGrid.add(fileDirBtn, 2, rowIndex);

        //endregion
    }

    /**
     * 控件事件绑定
     */
    private void bindAction() {

        befExtCheckBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                boolean flag = befExtCheckBox.isSelected();
                befExtText.setDisable(!flag);
            }
        });
        afExtCheckBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                boolean flag = afExtCheckBox.isSelected();
                afExtText.setDisable(!flag);
            }
        });
        gdbDirCheck.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                boolean flag = gdbDirCheck.isSelected();
                gdbDirText.setDisable(!flag);
                gdbDirBtn.setDisable(!flag);
            }
        });
        fileDirCheck.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                boolean flag = fileDirCheck.isSelected();
                fileDirText.setDisable(!flag);
                fileDirBtn.setDisable(!flag);
            }
        });
        //选择GDB目录
        gdbDirBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                GDBSelectFolderDialog dialog = new GDBSelectFolderDialog();
                dialog.setFolderType(FolderType.MapGIS_DataBase | FolderType.MapGIS_Fds);
                Optional<String[]> optional = dialog.showAndWait();
                if (optional != null && optional.isPresent()) {
                    String dir = optional.get()[0];
                    gdbDirText.setText(dir);
                }
            }
        });
        //选中磁盘目录
        fileDirBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                //directoryChooser.setTitle("浏览文件夹");
                File directory = directoryChooser.showDialog(stage);
                if (directory != null) {
                    String dir = directory.getAbsolutePath();
                    fileDirText.setText(dir);
                }
            }
        });
    }

    /**
     * 确定按钮执行
     */
    private void okButtonClick(ActionEvent event) {


    }

    /**
     * 是否统改前缀
     */
    public boolean isModifyBefExt() {
        return this.befExtCheckBox.isSelected();
    }

    /**
     * 前缀内容
     */
    public String getBefExt() {
        return this.befExtText.getText();
    }

    /**
     * 是否统改后缀
     */
    public boolean isModifyAfExt() {
        return this.afExtCheckBox.isSelected();
    }

    /**
     * 后缀内容
     */
    public String getAfExt() {
        return this.afExtText.getText();
    }

    /**
     * 是否统改GDB目录
     */
    public boolean isModifyGDBDir() {
        return this.gdbDirCheck.isSelected();
    }

    /**
     * GDB目录
     */
    public String getGDBDir() {
        if (this.isModifyGDBDir() && this.gdbDirText.getText() != null && this.gdbDirText.getText().length() > 0)
            return this.gdbDirText.getText();
        else
            return null;
    }

    /**
     * 是否统改磁盘目录
     */
    public boolean isModifyFileDir() {
        return this.fileDirCheck.isSelected();
    }

    /**
     * 磁盘文件目录
     */
    public String getFielDir() {
        if (this.isModifyFileDir() && this.fileDirText.getText() != null && this.fileDirText.getText().length() > 0)
            return this.fileDirText.getText();
        else
            return null;
    }

    /**
     * 是否可以选择统改磁盘目录
     */
    public void setModifyDiskDir(boolean val) {
        this.fileDirCheck.setDisable(!val);
        this.fileDirText.setDisable(!val);
        this.fileDirBtn.setDisable(!val);
    }

    /**
     * 是否可以选择统改GDB目录
     */
    public void setModifyGDBDir(boolean val) {
        this.gdbDirCheck.setDisable(!val);
        this.gdbDirText.setDisable(!val);
        this.gdbDirBtn.setDisable(!val);
    }

}
