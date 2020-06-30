import com.zondy.mapgis.analysis.g3danalysis.TopoErrorInfo;
import com.zondy.mapgis.analysis.spatialanalysis.*;
import com.zondy.mapgis.common.IntList;
import com.zondy.mapgis.geodatabase.ClassFactory;
import com.zondy.mapgis.geodatabase.DataBase;
import com.zondy.mapgis.geodatabase.SFeatureCls;
import com.zondy.mapgis.geometry.*;
import com.zondy.mapgis.srs.ElpTransParam;
import com.zondy.mapgis.srs.SRefData;

/**
 * Created by wuqi on 2020/4/25 0025.
 */
public class Spatest {
    public static void main(String[] args){
        //    testPrj();
        //    testTopoCheck();
        testBuffer();
    }

    public static int testBuffer(){
        SFeatureCls srcCls = new SFeatureCls();
        SFeatureCls desCls = new SFeatureCls();
        srcCls.openByURL("gdbp://MapGISLocalPlus/aaa/sfcls/pnt");
        if (desCls.openByURL("gdbp://MapGISLocalPlus/aaa/sfcls/pnt_rtn_1") <= 0) {
            desCls.create("gdbp://MapGISLocalPlus/aaa/sfcls/pnt_rtn_1", GeomType.GeomReg);
        }
        desCls.clear();

        BufferOption bufferOp = new BufferOption();
        bufferOp.setLineEndType(BufferCapType.ROUND);
        bufferOp.setLeftRad(10);
        bufferOp.setRightRad(10);
        bufferOp.setIsDissolve(true);
        bufferOp.setTolerance(0.0001);
        SpatialAnalysis.buffer(srcCls,desCls,bufferOp,null);
        srcCls.close();
        desCls.close();
        return  1;
    }

    public static int testPrj(){
        GeoPolygon geoPoly = CreateTestPoly0();
        SRefData srcSRef;
        SRefData desSRef;
        //   DataBase dataBase = ClassFactory.openTempGdb();
        SFeatureCls cls = new SFeatureCls();
        cls.openByURL("gdbp://MapGisLocal/Templates/sfcls/湖北省市驻地");
        DataBase dataBase = cls.getGDataBase();
        int idSrc = dataBase.getSRefIDByName("地理坐标系(西安)_度");
        int idDes = dataBase.getSRefIDByName("高斯大地坐标系_西安80_38带3_北");
        srcSRef = dataBase.getSRef(idSrc);
        desSRef = dataBase.getSRef(idDes);

        Geometry desGeom = GeometryOperator.project(geoPoly,srcSRef,desSRef,null);
        cls.close();
        return  1;
    }

    public static int testTopoCheck(){
        SFeatureCls cls = new SFeatureCls();
        cls.openByURL("gdbp://MapGisLocal/testBuffer/sfcls/testRegaaa");

        TopologyChecker checker = new TopologyChecker();
        checker.setCheckSfcls(cls,null);

        TopologyCheckOption checkOp = new TopologyCheckOption();
        Rect rect = cls.getRange();
        checkOp.setTolerance(0.0001);
        checkOp.setMaxErrorCount(100);

        checker.addTopologyRule(TopologyRuleType.Ring_Must_Closed_Reg);

        TopologyErrorManager checkMng = new TopologyErrorManager();
        int rtn = checker.checkTopology(checkOp,checkMng);

        TopologyErrorFix topoFix = new TopologyErrorFix();
        TopologyErrorFixOption fixOp = new TopologyErrorFixOption();
//        TopologyErrorFixOption.LayerOidPair layerPair = new TopologyErrorFixOption.LayerOidPair();
//        fixOp.setMergeParam(layerPair);

        fixOp.setFixType(TopologyErrorFixType.Error_Fix_Close_Ring);
        topoFix.topologyErrorFix(checkMng.getTopoError(0),fixOp);

        cls.close();
        return rtn;
    }

    public static GeoPolygon CreateTestPoly0(){
        GeoPolygon poly = new GeoPolygon();
        IntList intList = new IntList();
        Dots polyDots = new Dots();
        polyDots.append(new Dot(0,0));
        polyDots.append(new Dot(0,1));
        polyDots.append(new Dot(1,1));
        polyDots.append(new Dot(1,0));
        polyDots.append(new Dot(0,0));
        intList.append(5);
        poly.setDots(polyDots,intList);
        return poly;
    }
}
