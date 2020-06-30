package com.zondy.mapgis.base;

import com.zondy.mapgis.geodatabase.config.EnvConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author CR
 * @file XPath.java
 * @brief 路径相关方法
 * @create 2019-12-12.
 */
public class XPath {
    /**
     * 获取program路径（mapgis_ui_controls.jar所在的路径）
     *
     * @return 路径
     */
    public static String getJarPath(Class cls) {
        String path = cls.getProtectionDomain().getCodeSource().getLocation().getFile();
        try {
            path = java.net.URLDecoder.decode(path, "UTF-8"); // 转换处理中文及空格
            File file = new File(path);
            if (file.isFile()) {
                path = file.getPath();
            }
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 获取program路径（mapgis_ui_controls.jar所在的路径）
     *
     * @return 路径
     */
    public static String getProgramPath() {
        String path = XPath.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        try {
            path = java.net.URLDecoder.decode(path, "UTF-8"); // 转换处理中文及空格
            File file = new File(path);
            if (file.isFile()) {
                path = file.getParent();
            }
        } catch (java.io.UnsupportedEncodingException e) {

        }
        return path;
    }

    /**
     * 获取文件扩展类型
     *
     * @param file 文件
     * @return 扩展类型（.txt）
     */
    public static String getExtension(File file) {
        return XPath.getExtension(file != null ? file.getPath() : null);
    }

    /**
     * 获取文件扩展类型（小写）
     *
     * @param filePath 文件路径
     * @return 扩展类型（.txt）
     */
    public static String getExtension(String filePath) {
        String ext = "";
        if (!XString.isNullOrEmpty(filePath)) {
            int index = filePath.lastIndexOf(".");
            if (index >= 0) {
                ext = filePath.substring(index).toLowerCase();
            }
        }
        return ext;
    }

    /**
     * 获取文件名（不带类型后缀）
     *
     * @param file 文件
     * @return 文件名（不带类型后缀）
     */
    public static String getNameWithoutExt(File file) {
        String name = "";
        if (file != null) {
            name = file.getName();
            int index = name.lastIndexOf(".");
            if (index >= 0) {
                name = name.substring(0, index);
            }
        }
        return name;
    }

    /**
     * 获取文件名（不带类型后缀）
     *
     * @param filePath 文件路径
     * @return 文件名（不带类型后缀）
     */
    public static String getNameWithoutExt(String filePath) {
        return XString.isNullOrEmpty(filePath) ? "" : XPath.getNameWithoutExt(new File(filePath));
    }

    /**
     * 合并路径
     *
     * @param path1
     * @param path2
     * @return
     */
    public static String combine(String path1, String path2) {
        String str = null;
        if (path1 != null) {
            str = path1;
            if (!XString.isNullOrEmpty(path2)) {
                if (!path1.endsWith(File.separator) && !path2.startsWith(File.separator)) {
                    str += File.separator;
                }
                str += path2;
            }
        } else if (path2 != null) {
            str = path2;
        }
        return str;
    }

    /**
     * 获取临时目录（先获取mapgis环境中的临时目录，如果为空或不存在则取系统临时目录）
     *
     * @return
     */
    public static String getTemp() {
        String temp = EnvConfig.getGisEnv().getTemp();
        if (XString.isNullOrEmpty(temp) || !(new File(temp)).exists()) {
            temp = System.getProperty("java.io.tmpdir");
        }
        return temp;
    }

    /**
     * 读取文件的文本内容
     *
     * @param fileName 文件路径
     * @return 文件的文本内容
     */
    public static String readFileContent(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr);
            }
            reader.close();
            return sbf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sbf.toString();
    }

    public static boolean isNameValid(String name) {
        return name != null && name.matches("[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$");
    }

    public static boolean isPathValid(String path) {
        boolean rtn = false;
        if (XFunctions.isSystemWindows()) {
            try {
                File f = new File(path);
                rtn = f.isDirectory();
            } catch (Exception ex) {
                rtn = false;
            }
            //return path.matches("^[A-z]:[^\\s/:\\*\\?\\\"<>\\|](\\x20|[^\\s/:\\*\\?\\\"<>\\|])*[^\\s/:\\*\\?\\\"<>\\|\\.]$");
        } else {
            rtn = true;
        }
        return rtn;
    }
}
