package com.zondy.mapgis.mapeditor.projecttransform;

import com.zondy.mapgis.geodatabase.config.EnvConfig;
import com.zondy.mapgis.geodatabase.config.SysConfigDirType;
import com.zondy.mapgis.srs.ElpTransParam;
import com.zondy.mapgis.srs.ElpTransformation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

/**
 * 地理转换参数设置面板
 *
 * @author zkj
 */
public class ElpTransParamSettingPane extends GridPane {
    private ComboBox<String> transMethodCombo = null;
    private ObservableList<String> transMethodList = null;
    private ArrayList<ElpTransParam> elpTransParamArrayList = new ArrayList<>();
    private ElpTransParam elpTransParam = null; //下拉选项对应的转换参数对象

    public ElpTransParamSettingPane() {
        Label transMethodLabel = new Label("转换方法:");
        transMethodCombo = new ComboBox<>();
        transMethodCombo.setMinWidth(150);
        transMethodCombo.setPrefWidth(200);
        transMethodList = FXCollections.observableArrayList();
        //初始化转换方法下拉列表
        transMethodList.addAll("无"); //第一项对应 null
        elpTransParamArrayList.add(null);
        //初始化地理转换项列表
        loadTransParamList();
        transMethodCombo.setItems(transMethodList);
        transMethodCombo.getSelectionModel().select(0);

        this.add(transMethodLabel, 0, 0);
        this.add(transMethodCombo, 1, 0);

        //设置参数
        Label settingLabel = new Label("转换参数:");
        Button btnSetting = new Button("设置...");
        btnSetting.setPrefWidth(200);
        this.add(settingLabel, 0, 1);
        this.add(btnSetting, 1, 1);

        this.setHgap(20);
        this.setVgap(5);
        this.setPadding(new Insets(5, 5, 5, 5));

        btnSetting.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ElpTransSettingDialog dialog = new ElpTransSettingDialog();
                Optional<ArrayList<ElpTransParam>> optional = dialog.showAndWait();
                if (optional.isPresent()) {
                    elpTransParamArrayList = optional.get();
                    //刷新下拉列表
                    transMethodList.clear();
                    transMethodList.addAll("无");
                    if (elpTransParamArrayList != null && elpTransParamArrayList.size() > 0) {
                        for (int i = 0; i < elpTransParamArrayList.size(); i++) {
                            ElpTransParam param = elpTransParamArrayList.get(i);
                            if (param != null) {
                                String name = param.getTransName();
                                if (name != null && name.length() > 0) {
                                    transMethodList.addAll(name);
                                }
                            }
                        }
                        transMethodCombo.setItems(transMethodList);
                        transMethodCombo.getSelectionModel().select(0);
                    }
                }
            }
        });
    }

    /**
     * 加载地理转换参数
     */
    private void loadTransParamList() {
        transMethodList.clear();
        elpTransParamArrayList.clear();
        transMethodList.addAll("无");
        elpTransParamArrayList.add(null);
        String sep = File.separator;
        String m_Path = (EnvConfig.getConfigDirectory(SysConfigDirType.Projection));//TransLst.dat
        m_Path += sep + "TransLst.dat";
        File file = new File(m_Path);
        if (file.exists()) {
            ElpTransformation.loadElpTransParam(m_Path);
            for (int i = 0; i < ElpTransformation.getElpTransParamCount(); i++) {
                if (ElpTransformation.getElpTransParam(i) != null) {
                    transMethodList.addAll((ElpTransformation.getElpTransParam(i).getTransName()));
                    elpTransParamArrayList.add(ElpTransformation.getElpTransParam(i));
                }
            }
            ElpTransformation.saveElpTransParam(m_Path);
        }
        transMethodCombo.getSelectionModel().select(0);
    }

    //获取选择的地理转换项
    public ElpTransParam getElpTransParam() {
        ElpTransParam param = null;
        int index = transMethodCombo.getSelectionModel().getSelectedIndex();
        if (index > 0) {
            if (elpTransParamArrayList != null && elpTransParamArrayList.size() > 0) {
                param = elpTransParamArrayList.get(index);
            }
        }
        return param;
    }

}
