package com.zondy.mapgis.filedialog;

/**
 * 选择目录
 * @author zkj
 */
public class GDBSelectFolderDialog extends FileDialog {
    public GDBSelectFolderDialog() {
        super("Select Directory", false);
    }

    @Override
    public void setFolderType(int val) {
        super.setFolderType(val);
//        super.m_folderType = val;
    }
}
