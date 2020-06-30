package com.zondy.mapgis.mapeditor.common;

import com.zondy.mapgis.analysis.spatialanalysis.TopologyRuleTranslator;
import com.zondy.mapgis.analysis.spatialanalysis.TopologyRuleType;
import com.zondy.mapgis.geometry.GeomType;
import com.zondy.mapgis.srs.SRefData;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 拓扑检查公共方法类
 */
public class CustomClass {
    /**
     * 获取规则列表
     *
     * @param type0
     * @param type1
     * @return
     */
    public static ArrayList<String> getTopoRuleStringItems(GeomType type0, GeomType type1) {
        TopologyRuleTranslator spTopologyRuleTranslator = new TopologyRuleTranslator();
        ArrayList<String> items = new ArrayList<>();
        String strTopoRule = null;
        if (type0 != GeomType.GeomUnknown) {
            if (type1 != GeomType.GeomUnknown) {// region 二元拓扑规则
                switch (type0) {
                    case GeomPnt:
                        switch (type1) {
                            case GeomPnt:
                                strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Coincide_With_PntPnt);
                                if (strTopoRule != null && !strTopoRule.isEmpty()) {
                                    items.add(strTopoRule);
                                }
                                break;
                            case GeomLin:
                                strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Be_Covered_By_PntLin);
                                if (strTopoRule != null && !strTopoRule.isEmpty()) {
                                    items.add(strTopoRule);
                                }
                                strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Be_Covered_By_Endpoint_Of_PntLin);
                                if (strTopoRule != null && !strTopoRule.isEmpty()) {
                                    items.add(strTopoRule);
                                }
                                break;
                            case GeomReg:
                                strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Be_Covered_By_Boundary_Of_PntReg);
                                if (strTopoRule != null && !strTopoRule.isEmpty()) {
                                    items.add(strTopoRule);
                                }
                                strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Be_Properly_Inside_PntReg);
                                if (strTopoRule != null && !strTopoRule.isEmpty()) {
                                    items.add(strTopoRule);
                                }
                                strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Be_Properly_Inside_And_Reg_Contains_One_Pnt_PntReg);
                                if (strTopoRule != null && !strTopoRule.isEmpty()) {
                                    items.add(strTopoRule);
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                    case GeomLin:
                        switch (type1) {
                            case GeomPnt:
                                strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Endpoint_Must_Be_Covered_By_LinPnt);
                                if (strTopoRule != null && !strTopoRule.isEmpty()) {
                                    items.add(strTopoRule);
                                }
                                break;
                            case GeomLin:
                                strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Be_Covered_By_Feature_Class_Of_LinLin);
                                if (strTopoRule != null && !strTopoRule.isEmpty()) {
                                    items.add(strTopoRule);
                                }
                                strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Overlap_With_LinLin);
                                if (strTopoRule != null && !strTopoRule.isEmpty()) {
                                    items.add(strTopoRule);
                                }
                                strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Intersect_With_LinLin);
                                if (strTopoRule != null && !strTopoRule.isEmpty()) {
                                    items.add(strTopoRule);
                                }
                                break;
                            case GeomReg:
                                strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Be_Covered_By_Boundary_Of_LinReg);
                                if (strTopoRule != null && !strTopoRule.isEmpty()) {
                                    items.add(strTopoRule);
                                }
                                strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Be_Inside_LinReg);
                                if (strTopoRule != null && !strTopoRule.isEmpty()) {
                                    items.add(strTopoRule);
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                    case GeomReg:
                        switch (type1) {
                            case GeomPnt:
                                strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Contains_Point_RegPnt);
                                if (strTopoRule != null && !strTopoRule.isEmpty()) {
                                    items.add(strTopoRule);
                                }
                                strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Contains_One_Pnt_RegPnt);
                                if (strTopoRule != null && !strTopoRule.isEmpty()) {
                                    items.add(strTopoRule);
                                }
                                break;
                            case GeomLin:
                                strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Boundary_Must_Be_Covered_By_RegLin);
                                if (strTopoRule != null && !strTopoRule.isEmpty()) {
                                    items.add(strTopoRule);
                                }
                                break;
                            case GeomReg:
                                strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Be_Covered_By_Feature_Class_Of_RegReg);
                                if (strTopoRule != null && !strTopoRule.isEmpty()) {
                                    items.add(strTopoRule);
                                }
                                strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Cover_Each_Other_RegReg);
                                if (strTopoRule != null && !strTopoRule.isEmpty()) {
                                    items.add(strTopoRule);
                                }
                                strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Be_Covered_By_RegReg);
                                if (strTopoRule != null && !strTopoRule.isEmpty()) {
                                    items.add(strTopoRule);
                                }
                                strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Overlap_With_RegReg);
                                if (strTopoRule != null && !strTopoRule.isEmpty()) {
                                    items.add(strTopoRule);
                                }
                                strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Boundary_Must_Be_Covered_By_Boundary_Of_RegReg);
                                if (strTopoRule != null && !strTopoRule.isEmpty()) {
                                    items.add(strTopoRule);
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
                // endregion
            } else {// region 一元拓扑规则
                switch (type0) {
                    case GeomPnt:
                        strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Be_Single_Part_Pnt);
                        if (strTopoRule != null && !strTopoRule.isEmpty()) {
                            items.add(strTopoRule);
                        }
                        strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Be_Disjoint_Pnt);
                        if (strTopoRule != null && !strTopoRule.isEmpty()) {
                            items.add(strTopoRule);
                        }
                        break;
                    case GeomLin:
                        strTopoRule = "默认";
                        items.add(strTopoRule);
                        strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Be_Single_Part_Lin);
                        if (strTopoRule != null && !strTopoRule.isEmpty()) {
                            items.add(strTopoRule);
                        }
                        strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Short_Arc_Lin);
                        if (strTopoRule != null && !strTopoRule.isEmpty()) {
                            items.add(strTopoRule);
                        }
                        strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Overlap_Lin);
                        if (strTopoRule != null && !strTopoRule.isEmpty()) {
                            items.add(strTopoRule);
                        }
                        strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Intersect_Lin);
                        if (strTopoRule != null && !strTopoRule.isEmpty()) {
                            items.add(strTopoRule);
                        }
                        strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Have_Dangles_Lin);
                        if (strTopoRule != null && !strTopoRule.isEmpty()) {
                            items.add(strTopoRule);
                        }
                        strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Have_Pseudo_Nodes_Lin);
                        if (strTopoRule != null && !strTopoRule.isEmpty()) {
                            items.add(strTopoRule);
                        }
                        strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Self_Overlap_Lin);
                        if (strTopoRule != null && !strTopoRule.isEmpty()) {
                            items.add(strTopoRule);
                        }
                        strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Self_Intersect_Lin);
                        if (strTopoRule != null && !strTopoRule.isEmpty()) {
                            items.add(strTopoRule);
                        }
                        strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Have_Same_Dot_Lin);
                        if (strTopoRule != null && !strTopoRule.isEmpty()) {
                            items.add(strTopoRule);
                        }
                        break;
                    case GeomReg:
                        strTopoRule = "默认";
                        items.add(strTopoRule);
                        strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Be_Single_Part_Reg);
                        if (strTopoRule != null && !strTopoRule.isEmpty()) {
                            items.add(strTopoRule);
                        }
                        strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Ring_Must_Closed_Reg);
                        if (strTopoRule != null && !strTopoRule.isEmpty()) {
                            items.add(strTopoRule);
                        }
                        strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Ring_Must_Have_Enough_Points_Reg);
                        if (strTopoRule != null && !strTopoRule.isEmpty()) {
                            items.add(strTopoRule);
                        }
                        strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Hole_Must_Inside_Shell_Reg);
                        if (strTopoRule != null && !strTopoRule.isEmpty()) {
                            items.add(strTopoRule);
                        }
                        strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Hole_Must_Disjoint_Hole_Reg);
                        if (strTopoRule != null && !strTopoRule.isEmpty()) {
                            items.add(strTopoRule);
                        }
                        strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Thin_Reg_Reg);
                        if (strTopoRule != null && !strTopoRule.isEmpty()) {
                            items.add(strTopoRule);
                        }
                        strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Self_Intersect_Reg);
                        if (strTopoRule != null && !strTopoRule.isEmpty()) {
                            items.add(strTopoRule);
                        }
                        strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Have_Gaps_Reg);
                        if (strTopoRule != null && !strTopoRule.isEmpty()) {
                            items.add(strTopoRule);
                        }
                        strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Overlap_Reg);
                        if (strTopoRule != null && !strTopoRule.isEmpty()) {
                            items.add(strTopoRule);
                        }
                        strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Have_Same_Dot_Reg);
                        if (strTopoRule != null && !strTopoRule.isEmpty()) {
                            items.add(strTopoRule);
                        }
                        strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Small_Reg_Reg);
                        if (strTopoRule != null && !strTopoRule.isEmpty()) {
                            items.add(strTopoRule);
                        }
                        break;
                    default:
                        break;
                }
                // endregion
            }
        }
        return items;
    }

    /**
     * 获取规则列表
     *
     * @param type0
     * @param type1
     * @return
     */
    public static ArrayList<TopologyRuleType> getTopoRuleTypeItems(GeomType type0, GeomType type1) {
        TopologyRuleTranslator spTopologyRuleTranslator = new TopologyRuleTranslator();
        ArrayList<TopologyRuleType> items = new ArrayList<>();
        if (type0 != GeomType.GeomUnknown) {
            if (type1 != GeomType.GeomUnknown) {// region 二元拓扑规则
                switch (type0) {
                    case GeomPnt:
                        switch (type1) {
                            case GeomPnt:
                                items.add(TopologyRuleType.Must_Coincide_With_PntPnt);
                                break;
                            case GeomLin:
                                items.addAll(Arrays.asList(TopologyRuleType.Must_Be_Covered_By_PntLin, TopologyRuleType.Must_Coincide_With_PntPnt));
                                break;
                            case GeomReg:
                                items.addAll(Arrays.asList(
                                        TopologyRuleType.Must_Be_Covered_By_Boundary_Of_PntReg,
                                        TopologyRuleType.Must_Be_Properly_Inside_PntReg,
                                        TopologyRuleType.Must_Be_Properly_Inside_And_Reg_Contains_One_Pnt_PntReg));
                                break;
                            default:
                                break;
                        }
                        break;
                    case GeomLin:
                        switch (type1) {
                            case GeomPnt:
                                items.add(TopologyRuleType.Endpoint_Must_Be_Covered_By_LinPnt);
                                break;
                            case GeomLin:
                                items.addAll(Arrays.asList(
                                        TopologyRuleType.Must_Be_Covered_By_Feature_Class_Of_LinLin,
                                        TopologyRuleType.Must_Not_Overlap_With_LinLin,
                                        TopologyRuleType.Must_Not_Intersect_With_LinLin));
                                break;
                            case GeomReg:
                                items.addAll(Arrays.asList(
                                        TopologyRuleType.Must_Be_Covered_By_Boundary_Of_LinReg,
                                        TopologyRuleType.Must_Be_Inside_LinReg));
                                break;
                            default:
                                break;
                        }
                        break;
                    case GeomReg:
                        switch (type1) {
                            case GeomPnt:
                                items.addAll(Arrays.asList(
                                        TopologyRuleType.Contains_Point_RegPnt,
                                        TopologyRuleType.Contains_One_Pnt_RegPnt));
                                break;
                            case GeomLin:
                                items.add(TopologyRuleType.Boundary_Must_Be_Covered_By_RegLin);
                                break;
                            case GeomReg:
                                items.addAll(Arrays.asList(
                                        TopologyRuleType.Must_Be_Covered_By_Feature_Class_Of_RegReg,
                                        TopologyRuleType.Must_Cover_Each_Other_RegReg,
                                        TopologyRuleType.Must_Be_Covered_By_RegReg,
                                        TopologyRuleType.Must_Not_Overlap_With_RegReg,
                                        TopologyRuleType.Boundary_Must_Be_Covered_By_Boundary_Of_RegReg));
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
                // endregion
            } else {// region 一元拓扑规则
                switch (type0) {
                    case GeomPnt:
                        items.addAll(Arrays.asList(
                                TopologyRuleType.Must_Be_Single_Part_Pnt,
                                TopologyRuleType.Must_Be_Disjoint_Pnt));
                        break;
                    case GeomLin:
                        items.addAll(Arrays.asList(
                                TopologyRuleType.Unknown,
                                TopologyRuleType.Must_Be_Single_Part_Lin,
                                TopologyRuleType.Must_Not_Short_Arc_Lin,
                                TopologyRuleType.Must_Not_Overlap_Lin,
                                TopologyRuleType.Must_Not_Intersect_Lin,
                                TopologyRuleType.Must_Not_Have_Dangles_Lin,
                                TopologyRuleType.Must_Not_Have_Pseudo_Nodes_Lin,
                                TopologyRuleType.Must_Not_Self_Overlap_Lin,
                                TopologyRuleType.Must_Not_Self_Intersect_Lin,
                                TopologyRuleType.Must_Not_Have_Same_Dot_Lin));
                        break;
                    case GeomReg:
                        items.addAll(Arrays.asList(
                                TopologyRuleType.Unknown,
                                TopologyRuleType.Must_Be_Single_Part_Reg,
                                TopologyRuleType.Ring_Must_Closed_Reg,
                                TopologyRuleType.Ring_Must_Have_Enough_Points_Reg,
                                TopologyRuleType.Hole_Must_Inside_Shell_Reg,
                                TopologyRuleType.Hole_Must_Disjoint_Hole_Reg,
                                TopologyRuleType.Must_Not_Thin_Reg_Reg,
                                TopologyRuleType.Must_Not_Self_Intersect_Reg,
                                TopologyRuleType.Must_Not_Have_Gaps_Reg,
                                TopologyRuleType.Must_Not_Overlap_Reg,
                                TopologyRuleType.Must_Not_Have_Same_Dot_Reg,
                                TopologyRuleType.Must_Not_Small_Reg_Reg));
                        break;
                    default:
                        break;
                }
                // endregion
            }
        }
        return items;
    }

    /**
     * 获取默认规则列表(支持线、区部分规则)自定义新的拓扑检查规则的集合（用于通用编辑下）
     *
     * @param type
     * @return
     */
    public static ArrayList<String> getDefaultTopoRuleItems(GeomType type) {
        TopologyRuleTranslator spTopologyRuleTranslator = new TopologyRuleTranslator();
        ArrayList<String> items = new ArrayList<>();
        String strTopoRule = null;
        if (type != GeomType.GeomUnknown) {
            switch (type) {
                case GeomLin:
                    strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Have_Same_Dot_Lin);
                    if (strTopoRule != null && !strTopoRule.isEmpty()) {
                        items.add(strTopoRule);
                    }
                    strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Short_Arc_Lin);
                    if (strTopoRule != null && !strTopoRule.isEmpty()) {
                        items.add(strTopoRule);
                    }
                    strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Self_Overlap_Lin);
                    if (strTopoRule != null && !strTopoRule.isEmpty()) {
                        items.add(strTopoRule);
                    }
                    strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Self_Intersect_Lin);
                    if (strTopoRule != null && !strTopoRule.isEmpty()) {
                        items.add(strTopoRule);
                    }
                    break;
                case GeomReg:
                    strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Have_Same_Dot_Reg);
                    if (strTopoRule != null && !strTopoRule.isEmpty()) {
                        items.add(strTopoRule);
                    }
                    strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Ring_Must_Closed_Reg);
                    if (strTopoRule != null && !strTopoRule.isEmpty()) {
                        items.add(strTopoRule);
                    }
                    strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Ring_Must_Have_Enough_Points_Reg);
                    if (strTopoRule != null && !strTopoRule.isEmpty()) {
                        items.add(strTopoRule);
                    }
                    strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Hole_Must_Inside_Shell_Reg);
                    if (strTopoRule != null && !strTopoRule.isEmpty()) {
                        items.add(strTopoRule);
                    }
                    strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Hole_Must_Disjoint_Hole_Reg);
                    if (strTopoRule != null && !strTopoRule.isEmpty()) {
                        items.add(strTopoRule);
                    }
                    strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Thin_Reg_Reg);
                    if (strTopoRule != null && !strTopoRule.isEmpty()) {
                        items.add(strTopoRule);
                    }
                    strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Self_Intersect_Reg);
                    if (strTopoRule != null && !strTopoRule.isEmpty()) {
                        items.add(strTopoRule);
                    }
                    strTopoRule = spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Small_Reg_Reg);
                    if (strTopoRule != null && !strTopoRule.isEmpty()) {
                        items.add(strTopoRule);
                    }
                    break;
                default:
                    break;
            }
        }
        return items;
    }

    /**
     * 将中文字符串转换成拓扑规则类型
     *
     * @param strTopoRule 对应的中文字符串
     * @return 拓扑规则类型
     */
    public static TopologyRuleType convertTopoRuleType(String strTopoRule) {
        TopologyRuleTranslator spTopologyRuleTranslator = new TopologyRuleTranslator();
        TopologyRuleType topoRule = TopologyRuleType.Unknown;
        // region 1.区规则
        // region 1)区图层
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Be_Single_Part_Reg))) {
            topoRule = TopologyRuleType.Must_Be_Single_Part_Reg;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Ring_Must_Closed_Reg))) {
            topoRule = TopologyRuleType.Ring_Must_Closed_Reg;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Ring_Must_Have_Enough_Points_Reg))) {
            topoRule = TopologyRuleType.Ring_Must_Have_Enough_Points_Reg;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Hole_Must_Inside_Shell_Reg))) {
            topoRule = TopologyRuleType.Hole_Must_Inside_Shell_Reg;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Hole_Must_Disjoint_Hole_Reg))) {
            topoRule = TopologyRuleType.Hole_Must_Disjoint_Hole_Reg;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Thin_Reg_Reg))) {
            topoRule = TopologyRuleType.Must_Not_Thin_Reg_Reg;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Self_Intersect_Reg))) {
            topoRule = TopologyRuleType.Must_Not_Self_Intersect_Reg;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Have_Gaps_Reg))) {
            topoRule = TopologyRuleType.Must_Not_Have_Gaps_Reg;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Overlap_Reg))) {
            topoRule = TopologyRuleType.Must_Not_Overlap_Reg;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Have_Same_Dot_Reg))) {
            topoRule = TopologyRuleType.Must_Not_Have_Same_Dot_Reg;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Small_Reg_Reg))) {
            topoRule = TopologyRuleType.Must_Not_Small_Reg_Reg;
        }
        // endregion

        // region 2)区区图层
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Be_Covered_By_Feature_Class_Of_RegReg))) {
            topoRule = TopologyRuleType.Must_Be_Covered_By_Feature_Class_Of_RegReg;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Cover_Each_Other_RegReg))) {
            topoRule = TopologyRuleType.Must_Cover_Each_Other_RegReg;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Be_Covered_By_RegReg))) {
            topoRule = TopologyRuleType.Must_Be_Covered_By_RegReg;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Overlap_With_RegReg))) {
            topoRule = TopologyRuleType.Must_Not_Overlap_With_RegReg;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Boundary_Must_Be_Covered_By_Boundary_Of_RegReg))) {
            topoRule = TopologyRuleType.Boundary_Must_Be_Covered_By_Boundary_Of_RegReg;
        }
        // endregion

        // region 3)区线图层
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Boundary_Must_Be_Covered_By_RegLin))) {
            topoRule = TopologyRuleType.Boundary_Must_Be_Covered_By_RegLin;
        }
        // endregion

        // region 4)区点图层
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Contains_Point_RegPnt))) {
            topoRule = TopologyRuleType.Contains_Point_RegPnt;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Contains_One_Pnt_RegPnt))) {
            topoRule = TopologyRuleType.Contains_One_Pnt_RegPnt;
        }
        // endregion
        // endregion

        // region 2.线规则
        // region 1) 线图层
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Be_Single_Part_Lin))) {
            topoRule = TopologyRuleType.Must_Be_Single_Part_Lin;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Short_Arc_Lin))) {
            topoRule = TopologyRuleType.Must_Not_Short_Arc_Lin;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Overlap_Lin))) {
            topoRule = TopologyRuleType.Must_Not_Overlap_Lin;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Intersect_Lin))) {
            topoRule = TopologyRuleType.Must_Not_Intersect_Lin;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Have_Dangles_Lin))) {
            topoRule = TopologyRuleType.Must_Not_Have_Dangles_Lin;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Have_Pseudo_Nodes_Lin))) {
            topoRule = TopologyRuleType.Must_Not_Have_Pseudo_Nodes_Lin;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Self_Overlap_Lin))) {
            topoRule = TopologyRuleType.Must_Not_Self_Overlap_Lin;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Self_Intersect_Lin))) {
            topoRule = TopologyRuleType.Must_Not_Self_Intersect_Lin;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Have_Same_Dot_Lin))) {
            topoRule = TopologyRuleType.Must_Not_Have_Same_Dot_Lin;
        }
        // endregion

        // region 2) 线区图层
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Be_Covered_By_Boundary_Of_LinReg))) {
            topoRule = TopologyRuleType.Must_Be_Covered_By_Boundary_Of_LinReg;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Be_Inside_LinReg))) {
            topoRule = TopologyRuleType.Must_Be_Inside_LinReg;
        }
        // endregion

        // region 3) 线线图层
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Be_Covered_By_Feature_Class_Of_LinLin))) {
            topoRule = TopologyRuleType.Must_Be_Covered_By_Feature_Class_Of_LinLin;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Overlap_With_LinLin))) {
            topoRule = TopologyRuleType.Must_Not_Overlap_With_LinLin;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Not_Intersect_With_LinLin))) {
            topoRule = TopologyRuleType.Must_Not_Intersect_With_LinLin;
        }
        // endregion

        // region 4) 线点图层
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Endpoint_Must_Be_Covered_By_LinPnt))) {
            topoRule = TopologyRuleType.Endpoint_Must_Be_Covered_By_LinPnt;
        }
        // endregion
        // endregion

        // region 3.点规则
        // region 1)点图层
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Be_Single_Part_Pnt))) {
            topoRule = TopologyRuleType.Must_Be_Single_Part_Pnt;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Be_Disjoint_Pnt))) {
            topoRule = TopologyRuleType.Must_Be_Disjoint_Pnt;
        }
        // endregion

        // region 2)点区图层
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Be_Covered_By_Boundary_Of_PntReg))) {
            topoRule = TopologyRuleType.Must_Be_Covered_By_Boundary_Of_PntReg;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Be_Properly_Inside_PntReg))) {
            topoRule = TopologyRuleType.Must_Be_Properly_Inside_PntReg;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Be_Properly_Inside_And_Reg_Contains_One_Pnt_PntReg))) {
            topoRule = TopologyRuleType.Must_Be_Properly_Inside_And_Reg_Contains_One_Pnt_PntReg;
        }
        // endregion

        // region 3)点线图层
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Be_Covered_By_PntLin))) {
            topoRule = TopologyRuleType.Must_Be_Covered_By_PntLin;
        }
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Be_Covered_By_Endpoint_Of_PntLin))) {
            topoRule = TopologyRuleType.Must_Be_Covered_By_Endpoint_Of_PntLin;
        }
        // endregion

        // region 4)点点图层
        if (strTopoRule.equals(spTopologyRuleTranslator.convertTopologyRule(TopologyRuleType.Must_Coincide_With_PntPnt))) {
            topoRule = TopologyRuleType.Must_Coincide_With_PntPnt;
        }
        // endregion
        // endregion
        return topoRule;
    }

    /**
     * 转换不同参照系下的容差
     *
     * @param tolerance
     * @param sRefData
     * @return
     */
    public static double getToleranceBySRefData(double tolerance, SRefData sRefData) {
        double destTolerance = tolerance;
        if (sRefData != null) {
            double unit = 1;
            switch (sRefData.getType()) {
                case JWD: {// region 处理经纬度坐标系
                    switch (sRefData.getAngUnit()) {
                        case Degree:
                            unit = 1e5;
                            break;
                        case Minute:
                            unit = 1e5 / 60;
                            break;
                        case Second:
                        case DMS:
                            unit = 1e5 / 3600;
                            break;
                        case Radian:
                            unit = sRefData.getAngUnitFactor() * 180 / Math.PI * 1e5;
                            break;
                        default:
                            break;
                    }
                    // endregion
                    break;
                }
                case PRJ: {// region 处理平面直角坐标系
                    double rate = sRefData.getRate();
                    switch (sRefData.getUnit()) {
                        case MilliMeter:
                            unit = rate / 1000;
                            break;
                        case CentiMeter:
                            unit = rate / 100;
                            break;
                        case DeciMeter:
                            unit = rate / 10;
                            break;
                        case Meter:
                            unit = rate;
                            break;
                        case KiloMeter:
                            unit = 1000 * rate;
                            break;
                        default:
                            break;
                    }
                    // endregion
                    break;
                }
                default:
                    break;
            }
            destTolerance = tolerance / unit;
        }
        MAX_TOLERANCE = destTolerance * 100;
        MIN_TOLERANCE = destTolerance / 100;
        return destTolerance;
    }

    public static double TOLERANCE = 0.0001;
    public static double MAX_TOLERANCE = 0.01;
    public static double MIN_TOLERANCE = 0.000001;
}
