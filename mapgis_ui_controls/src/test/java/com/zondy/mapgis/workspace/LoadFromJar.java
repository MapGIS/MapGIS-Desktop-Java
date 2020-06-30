package com.zondy.mapgis.workspace;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author cxy
 * @date 2020/05/11
 */
public class LoadFromJar {
    public static void main(String[] args) throws URISyntaxException {
        URL url = LoadFromJar.class.getResource("/export_16.png");
        File file = new File(url.toURI());
        String filePath = file.getAbsolutePath();
    }
}
