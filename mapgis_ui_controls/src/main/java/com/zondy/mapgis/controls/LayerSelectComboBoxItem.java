package com.zondy.mapgis.controls;

import com.zondy.mapgis.map.DocumentItem;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;

/**
 * @author chenxinyuan
 */
public class LayerSelectComboBoxItem extends HBox {
    private boolean createByUrl;
    private String documentItemUrl;
    private DocumentItem documentItem;
    private int offSet = 0;
    private boolean canSelect = false;
    private String caption;
    private Image image;

    public LayerSelectComboBoxItem(DocumentItem documentItem, String caption, int offSet, boolean canSelect, Image image) {
        this.createByUrl = false;
        this.documentItem = documentItem;
        this.caption = caption;
        this.offSet = offSet;
        this.canSelect = canSelect;
        this.image = image;
    }

    public LayerSelectComboBoxItem(String documentItemUrl, int offSet, boolean canSelect, Image image) {
        this.createByUrl = true;
        this.documentItemUrl = documentItemUrl;
        this.caption = documentItemUrl;
        this.offSet = offSet;
        this.canSelect = canSelect;
        this.image = image;
    }

    public LayerSelectComboBoxItem(String documentItemUrl, String caption, int offSet, boolean canSelect, Image image) {
        this.createByUrl = true;
        this.documentItemUrl = documentItemUrl;
        this.caption = caption;
        this.offSet = offSet;
        this.canSelect = canSelect;
        this.image = image;
    }


    public boolean isCreateByUrl() {
        return createByUrl;
    }

    public String getDocumentItemUrl() {
        return documentItemUrl;
    }

    public DocumentItem getDocumentItem() {
        return documentItem;
    }

    public int getOffSet() {
        return offSet;
    }

    public boolean isCanSelect() {
        return canSelect;
    }

    public String getCaption() {
        return caption;
    }

    public Image getImage() {
        return image;
    }
}
