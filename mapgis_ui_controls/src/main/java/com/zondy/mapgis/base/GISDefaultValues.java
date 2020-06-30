package com.zondy.mapgis.base;

import com.zondy.mapgis.att.Field.ExtField;
import com.zondy.mapgis.att.Field;
import com.zondy.mapgis.att.Field.FieldType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author CR
 * @file FiledDefaultValues.java
 * @brief 关于Field(s)的一些默认的静态值
 * @create 2019-11-18.
 */
public final class GISDefaultValues
{
    //region 保留字和无效字符
    private static final String[] mapGISReservedWords = {"OID", "FID", "ANNID", "ARCID", "DOTID"};
    private static final String[] sqlReservedWords = {"ADD", "EXCEPT", "PERCENT", "ALL", "EXEC", "PLAN", "ALTER", "EXECUTE", "PRECISION", "AND", "EXISTS", "PRIMARY", "ANY", "EXIT", "PRINT", "AS", "FETCH", "PROC", "ASC", "FILE", "PROCEDURE", "AUTHORIZATION", "FILLFACTOR", "PUBLIC", "BACKUP", "FOR", "RAISERROR", "BEGIN", "FOREIGN", "READ", "BETWEEN", "FREETEXT", "READTEXT", "BREAK", "FREETEXTTABLE", "RECONFIGURE", "BROWSE", "FROM", "REFERENCES", "BULK", "FULL", "REPLICATION", "BY", "FUNCTION", "RESTORE", "CASCADE", "GOTO", "RESTRICT", "CASE", "GRANT", "RETURN", "CHECK", "GROUP", "REVOKE", "CHECKPOINT", "HAVING", "RIGHT", "CLOSE", "HOLDLOCK", "ROLLBACK", "CLUSTERED", "IDENTITY", "ROWCOUNT", "COALESCE", "IDENTITY_INSERT", "ROWGUIDCOL", "COLLATE", "IDENTITYCOL", "RULE", "COLUMN", "IF", "SAVE", "COMMIT", "IN", "SCHEMA", "COMPUTE", "INDEX", "SELECT", "CONSTRAINT", "INNER", "SESSION_USER", "CONTAINS", "INSERT", "SET", "CONTAINSTABLE", "INTERSECT", "SETUSER", "CONTINUE", "INTO", "SHUTDOWN", "CONVERT", "IS", "SOME", "CREATE", "JOIN", "STATISTICS", "CROSS", "KEY", "SYSTEM_USER", "CURRENT", "KILL", "TABLE", "CURRENT_DATE", "LEFT", "TEXTSIZE", "CURRENT_TIME", "LIKE", "THEN", "CURRENT_TIMESTAMP", "LINENO", "TO", "CURRENT_USER", "LOAD", "TOP", "CURSOR", "NATIONAL", "TRAN", "DATABASE", "NOCHECK", "TRANSACTION", "DBCC", "NONCLUSTERED", "TRIGGER", "DEALLOCATE", "NOT", "TRUNCATE", "DECLARE", "NULL", "TSEQUAL", "DEFAULT", "NULLIF", "UNION", "DELETE", "OF", "UNIQUE", "DENY", "OFF", "UPDATE", "DESC", "OFFSETS", "UPDATETEXT", "DISK", "ON", "USE", "DISTINCT", "OPEN", "USER", "DISTRIBUTED", "OPENDATASOURCE", "VALUES", "DOUBLE", "OPENQUERY", "VARYING", "DROP", "OPENROWSET", "VIEW", "DUMMY", "OPENXML", "WAITFOR", "DUMP", "OPTION", "WHEN", "ELSE", "OR", "WHERE", "END", "ORDER", "WHILE", "ERRLVL", "OUTER", "WITH", "ESCAPE", "OVER", "WRITETEXT"};
    private static final String[] oracleReservedWords = {"ACCESS", "ADD", "ALL", "ALTER", "AND", "ANY", "AS", "ASC", "AUDIT", "BETWEEN", "BY", "CHAR", "CHECK", "CLUSTER", "COLUMN", "COMMENT", "COMPRESS", "CONNECT", "CREATE", "CURRENT", "DATE", "DECIMAL", "DEFAULT", "DELETE", "DESC", "DISTINCT", "DROP", "ELSE", "EXCLUSIVE", "EXISTS", "FILE", "FLOAT", "FOR", "FROM", "GRANT", "GROUP", "HAVING", "IDENTIFIED", "IMMEDIATE", "IN", "INCREMENT", "INDEX", "INITIAL", "INSERT", "INTEGER", "INTERSECT", "INTO", "IS", "LEVEL", "LIKE", "LOCK", "LONG", "MAXEXTENTS", "MINUS", "MLSLABEL", "MODE", "MODIFY", "NOAUDIT", "NOCOMPRESS", "NOT", "NOWAIT", "NULL", "NUMBER", "OF", "OFFLINE", "ON", "ONLINE", "OPTION", "OR", "ORDER", "PCTFREE", "PRIOR", "PRIVILEGES", "PUBLIC", "RAW", "RENAME", "RESOURCE", "REVOKE", "ROW", "ROWID", "ROWNUM", "ROWS", "SELECT", "SESSION", "SET", "SHARE", "SIZE", "SMALLINT", "START", "SUCCESSFUL", "SYNONYM", "SYSDATE", "TABLE", "THEN", "TO", "TRIGGER", "UID", "UNION", "UNIQUE", "UPDATE", "USER", "VALIDATE", "VALUES", "VARCHAR", "VARCHAR2", "VIEW", "WHENEVER", "WHERE", "WITH"};
    private static final String[] defaultFieldNames = {"mparea", "mpperimeter", "mplength", "mplayer", "mpsurfarea", "mpvolume"};
    private static final Character[] spatialCharacters = {'`', '~', '!', '@', '%', '^', '&', '*', '(', ')', '-', '+', '=', '\\', '|', '{', '}', '[', ']', ';', ':', '\'', '\"', ',', '.', '<', '>', '?', '/', '·', '～', '！', '◎', '％', '…', '※', '×', '（', '）', '—', '－', '＋', '＝', '÷', '§', '『', '』', '【', '】', '；', '：', '‘', '’', '“', '”', '，', '。', '《', '》', '、', '？'};
    private static final Character[] invalidNameChars = {'\t', '\\', '/', ':', '*', '?', '\"', '<', '>', '|'};//数据名称无效字符
    private static final String[] sysReservedFileNames = new String[]{"CON", "AUX", "NUL", "PRN", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};//系统保留设备名称（全用大些，直接用）

    /**
     * @return MapGIS保留字。全用大写，方便使用Contains判断。
     */
    public static String[] getMapGISReservedWords()
    {
        return mapGISReservedWords;
    }

    /**
     * @return MapGIS保留字。全用大写，方便使用Contains判断。
     */
    public static List<String> getMapGISReservedWordList()
    {
        return new ArrayList<String>(Arrays.asList(mapGISReservedWords));
    }

    /**
     * @return SQL Server保留字。全用大写，方便使用Contains判断。
     */
    public static String[] getSQLReservedWords()
    {
        return sqlReservedWords;
    }

    /**
     * @return SQL Server保留字。全用大写，方便使用Contains判断。
     */
    public static List<String> getSQLReservedWordList()
    {
        return new ArrayList<String>(Arrays.asList(sqlReservedWords));
    }

    /**
     * @return Oracle保留字。全用大写，方便使用Contains判断。
     */
    public static String[] getOracleReservedWords()
    {
        return oracleReservedWords;
    }

    /**
     * @return Oracle保留字。全用大写，方便使用Contains判断。
     */
    public static List<String> getOracleReservedWordList()
    {
        return new ArrayList<String>(Arrays.asList(oracleReservedWords));
    }

    /**
     * @return 特殊字符
     */
    public static Character[] getSpatialCharacters()
    {
        return spatialCharacters;
    }

    /**
     * @return 特殊字符
     */
    public static List<Character> getSpatialCharacterList()
    {
        return new ArrayList<Character>(Arrays.asList(spatialCharacters));
    }

    /**
     * @return MapGIS矢量类的默认字段名称。全用大写，方便使用Contains判断。
     */
    public static String[] getDefaultFieldNames()
    {
        return defaultFieldNames;
    }

    /**
     * @return MapGIS矢量类的默认字段名称。全用大写，方便使用Contains判断。
     */
    public static List<String> getDefaultFieldNameList()
    {
        return new ArrayList<String>(Arrays.asList(defaultFieldNames));
    }

    /**
     * @return 数据名称无效字符集合
     */
    public static Character[] getInvalidNameChars()
    {
        return invalidNameChars;
    }

    /**
     * @return 数据名称无效字符集合
     */
    public static List<Character> getInvalidNameCharList()
    {
        return new ArrayList<Character>(Arrays.asList(invalidNameChars));
    }


    /**
     * @return 系统保留设备名称(大写 ）
     */
    public static String[] getReservedFileNames()
    {
        return sysReservedFileNames;
    }

    /**
     * @return 系统保留设备名称(大写 ）
     */
    public static List<String> getReservedFileNameList()
    {
        return new ArrayList<String>(Arrays.asList(sysReservedFileNames));
    }
    //endregion

    //region 默认字段
    private static Field fldLayer;    //默认的“图层”字段，简单要素类和注记类数据专有
    private static Field fldLength;   //默认的“长度”字段，线数据专有
    private static Field fldArea;     //默认的“面积”字段，区数据专有
    private static Field fldPerimeter;//默认的“边长”字段，区数据专有
    private static Field fldSurArea;  //默认的“面积”字段，面/体数据专有
    private static Field fldVolume;   //默认的“体积”字段，体数据专有
    private static Field fldCustom;//自定义ID字段，有时候非需要一个字段时使用，如对象类创建时

    /**
     * @return 默认字段：图层
     */
    public static Field getDefaultFieldLayer()
    {
        if (fldLayer == null)
        {
            fldLayer = new Field();
            fldLayer.setFieldName("mpLayer");
            fldLayer.setFieldType(Field.FieldType.fldLong);
            fldLayer.setFieldLength((short) 10);
            //fldLayer.setPointLength((short) 0);
            //fldLayer.setEditable((short) 1);
            //fldLayer.setPtcPosition((short) -1);//强制设置为新字段，不然属性值可能会出错。
            Field.ExtField fldLayerExt = fldLayer.getExtField();
            if (fldLayerExt == null)
            {
                fldLayerExt = new Field.ExtField(Field.FieldType.fldLong, (short) 0);
            }
            fldLayerExt.setAlias("图层");
            fldLayerExt.setIsNull(false);
            //fldLayerExt.DefVal = 0;//未完成。底层没有，等zkj封装
            fldLayer.setExtField(fldLayerExt);
        }
        return fldLayer;
    }

    /**
     * @return 默认字段：长度
     */
    public static Field getDefaultFieldLength()
    {
        if (fldLength == null)
        {
            fldLength = new Field();
            fldLength.setFieldName("mpLength");
            fldLength.setFieldType(Field.FieldType.fldDouble);
            fldLength.setFieldLength((short) 15);
            //fldLength.setPointLength((short) 6);
            //fldLength.setEditable((short) 0);
            //fldLength.setPtcPosition((short) -1);//强制设置为新字段，不然属性值可能会出错。
            Field.ExtField fldLengthExt = fldLength.getExtField();
            if (fldLengthExt == null)
            {
                fldLengthExt = new Field.ExtField(Field.FieldType.fldDouble, (short) 0);
            }
            fldLengthExt.setAlias("长度");
            fldLengthExt.setIsNull(true);
            fldLength.setExtField(fldLengthExt);
        }
        return fldLength;
    }

    /**
     * @return 默认字段：面积
     */
    public static Field getDefaultFieldArea()
    {
        if (fldArea == null)
        {
            fldArea = new Field();
            fldArea.setFieldName("mpArea");
            fldArea.setFieldType(Field.FieldType.fldDouble);
            fldArea.setFieldLength((short) 15);
            //fldArea.setPointLength((short) 6);
            //fldArea.setEditable((short) 0);
            //fldArea.setPtcPosition((short) -1);//强制设置为新字段，不然属性值可能会出错。
            Field.ExtField fldAreaExt = fldArea.getExtField();
            if (fldAreaExt == null)
            {
                fldAreaExt = new Field.ExtField(Field.FieldType.fldDouble, (short) 0);

            }
            fldAreaExt.setAlias("面积");
            fldAreaExt.setIsNull(true);
            fldArea.setExtField(fldAreaExt);
        }
        return fldArea;
    }

    /**
     * @return 默认字段：周长
     */
    public static Field getDefaultFieldPerimeter()
    {
        if (fldPerimeter == null)
        {
            fldPerimeter = new Field();
            fldPerimeter.setFieldName("mpPerimeter");
            fldPerimeter.setFieldType(Field.FieldType.fldDouble);
            fldPerimeter.setFieldLength((short) 15);
            //fldPerimeter.setPointLength((short) 6);
            //fldPerimeter.setEditable((short) 0);
            //fldPerimeter.setPtcPosition((short) -1);//强制设置为新字段，不然属性值可能会出错。
            Field.ExtField fldPerimeterExt = fldPerimeter.getExtField();
            if (fldPerimeterExt == null)
            {
                fldPerimeterExt = new Field.ExtField(Field.FieldType.fldDouble, (short) 0);
            }
            fldPerimeterExt.setAlias("周长");
            fldPerimeterExt.setIsNull(true);
            fldPerimeter.setExtField(fldPerimeterExt);
        }
        return fldPerimeter;
    }

    /**
     * @return 默认字段：面积(面/体的表面积)
     */
    public static Field getDefaultFieldSurfArea()
    {
        if (fldSurArea == null)
        {
            fldSurArea = new Field();
            fldSurArea.setFieldName("mpSurfArea");
            fldSurArea.setFieldType(Field.FieldType.fldDouble);
            fldSurArea.setFieldLength((short) 15);
            //fldSurArea.setPointLength((short) 6);
            //fldSurArea.setEditable((short) 0);
            //fldSurArea.setPtcPosition((short) -1);//强制设置为新字段，不然属性值可能会出错。
            Field.ExtField fldAreaExt = fldSurArea.getExtField();
            if (fldAreaExt == null)
            {
                fldAreaExt = new Field.ExtField(Field.FieldType.fldDouble, (short) 0);
            }
            fldAreaExt.setAlias("面积");
            fldAreaExt.setIsNull(true);
            fldSurArea.setExtField(fldAreaExt);
        }
        return fldSurArea;
    }

    /**
     * @return 默认字段：体积
     */
    public static Field getDefaultFieldVolume()
    {
        if (fldVolume == null)
        {
            fldVolume = new Field();
            fldVolume.setFieldName("mpVolume");
            fldVolume.setFieldType(Field.FieldType.fldDouble);
            fldVolume.setFieldLength((short) 15);
            //fldVolume.setPointLength((short) 6);
            //fldVolume.setEditable((short) 0);
            //fldVolume.setPtcPosition((short) -1);//强制设置为新字段，不然属性值可能会出错。
            Field.ExtField fldAreaExt = fldVolume.getExtField();
            if (fldAreaExt == null)
            {
                fldAreaExt = new Field.ExtField(Field.FieldType.fldDouble, (short) 0);
            }
            fldAreaExt.setAlias("体积");
            fldAreaExt.setIsNull(true);
            fldVolume.setExtField(fldAreaExt);
        }
        return fldVolume;
    }

    /**
     * @return 默认字段：图层
     */
    public static Field getFieldCustom()
    {
        if (fldCustom == null)
        {
            fldCustom = new Field();
            fldCustom.setFieldName("ID");
            fldCustom.setFieldType(Field.FieldType.fldLong);
            fldCustom.setFieldLength((short) 10);
            //fldCustom.setPointLength((short) 0);
            //fldCustom.setEditable((short) 1);
            //fldCustom.setPtcPosition((short) -1);//强制设置为新字段，不然属性值可能会出错。
        }
        return fldCustom;
    }
    //endregion

    //region 字段类型兼容性数组
    /**
     * 属性字段不同类型转换的兼容性数组，下标分别为新旧FieldType。0—不兼容；1—兼容；2—由字符长度决定
     */
    public static final int[][] FieldTypeCompatible = {
            {2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
            {2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}};
    //endregion

    //region 属性字段类型
    /// <summary>
    /// MapGIS属性字段的类型
    /// </summary>
    public static final Field.FieldType[] MapGISFieldTypes = {
            Field.FieldType.fldStr,
            Field.FieldType.fldByte,
            Field.FieldType.fldBool,
            Field.FieldType.fldShort,
            Field.FieldType.fldLong,
            Field.FieldType.fldInt64,
            Field.FieldType.fldFloat,
            Field.FieldType.fldDouble,
            Field.FieldType.fldDate,
            Field.FieldType.fldTime,
            Field.FieldType.fldTimeStamp,
            //Field.FieldType.fldBinary,
            Field.FieldType.fldBlob
    };
    //endregion
}
