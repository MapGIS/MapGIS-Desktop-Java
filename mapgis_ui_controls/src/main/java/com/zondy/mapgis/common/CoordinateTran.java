package com.zondy.mapgis.common;

import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.geometry.Dot;
import com.zondy.mapgis.geometry.Rect;
import com.zondy.mapgis.map.Transformation;
import com.zondy.mapgis.srs.SRefData;

/**
 * @author CR
 * @file CoordinateTran.java
 * @brief 坐标转换
 * @create 2020-05-25.
 */
public class CoordinateTran {
    /**
     * 设备坐标点转逻辑坐标点
     *
     * @param tran   坐标转换对象
     * @param sfData 参照系
     * @param dot    设备坐标点
     * @return 逻辑坐标点
     */
    public static Dot dpToLp(Transformation tran, SRefData sfData, Dot dot) {
        Dot rtnDot = null;
        if (tran != null && dot != null) {
            SRefData oldSref = tran.getSourceSRef();
            tran.setSourceSref(sfData);
            double[] ps = tran.dpToLp(dot.getX(), dot.getY());
            if (ps != null && ps.length >= 2) {
                rtnDot = new Dot(ps[0], ps[1]);
            }
            tran.setSourceSref(oldSref);
        }
        return rtnDot;
    }

    /**
     * 设备坐标矩形转逻辑坐标矩形
     *
     * @param tran   坐标转换对象
     * @param sfData 参照系
     * @param rect   设备坐标矩形
     * @return 逻辑坐标矩形
     */
    public static Rect dpToLp(Transformation tran, SRefData sfData, Rect rect) {
        Rect rtnRect = null;
        if (tran != null && rect != null) {
            SRefData oldSref = tran.getSourceSRef();
            tran.setSourceSref(sfData);
            double[] mins = tran.dpToLp(rect.getXMin(), rect.getYMin());
            double[] maxs = tran.dpToLp(rect.getXMax(), rect.getYMax());
            if (mins != null && mins.length >= 2 && maxs != null && maxs.length >= 2) {
                rtnRect = new Rect(mins[0], mins[1], maxs[0], maxs[1]);
            }
            tran.setSourceSref(oldSref);
        }
        return rtnRect;
    }

    /**
     * 设备坐标点转地图坐标点
     *
     * @param tran   坐标转换对象
     * @param sfData 参照系
     * @param dot    设备坐标点
     * @return 地图坐标点
     */
    public static Dot dpToMp(Transformation tran, SRefData sfData, Dot dot) {
        Dot rtnDot = null;
        if (tran != null && dot != null) {
            SRefData oldSref = tran.getSourceSRef();
            tran.setSourceSref(sfData);
            //底层未封装dpToMp
            //double[] ps = tran.dpToMp(dot.getX(), dot.getY());
            //if (ps != null && ps.length >= 2) {
            //    rtnDot = new Dot(ps[0], ps[1]);
            //}
            tran.setSourceSref(oldSref);
        }
        return rtnDot;
    }

    /**
     * 设备坐标矩形转地图坐标矩形
     *
     * @param tran   坐标转换对象
     * @param sfData 参照系
     * @param rect   设备坐标矩形
     * @return 地图坐标矩形
     */
    public static Rect dpToMp(Transformation tran, SRefData sfData, Rect rect) {
        Rect rtnRect = null;
        if (tran != null && rect != null) {
            SRefData oldSref = tran.getSourceSRef();
            tran.setSourceSref(sfData);
            //底层未封装dpToMp
            //double[] mins = tran.dpToMp(rect.getXMin(), rect.getYMin());
            //double[] maxs = tran.dpToMp(rect.getXMax(), rect.getYMax());
            //if (mins != null && mins.length >= 2 && maxs != null && maxs.length >= 2) {
            //    rtnRect = new Rect(mins[0], mins[1], maxs[0], maxs[1]);
            //}
            tran.setSourceSref(oldSref);
        }
        return rtnRect;
    }

    /**
     * 设备坐标距离转地图坐标距离
     *
     * @param tran   坐标转换对象
     * @param sfData 参照系
     * @param dis    设备坐标距离
     * @return 地图坐标距离
     */
    public static double dpToMp(Transformation tran, SRefData sfData, double dis) {
        double rtnDis = 0.0;
        if (tran != null) {
            SRefData oldSref = tran.getSourceSRef();
            tran.setSourceSref(sfData);
            double dx1 = 0.0, dy1 = 0.0, mx1 = 0.0, my1 = 0.0;
            double dx2 = dis, dy2 = 0.0, mx2 = 0.0, my2 = 0.0;
            //底层未封装dpToMp
            //double[] mins = tran.dpToMp(dx1, dy1);
            //double[] maxs = tran.dpToMp(dx2, dy2);
            //if (mins != null && mins.length >= 2 && maxs != null && maxs.length >= 2) {
            //    rtnDis = mx2 - mx1;
            //}
            tran.setSourceSref(oldSref);
        }
        return rtnDis;
    }

    /**
     * 设备坐标点转窗口坐标点
     *
     * @param tran   坐标转换对象
     * @param sfData 参照系
     * @param dot    设备坐标点
     * @return 窗口坐标点
     */
    public static Dot dpToWp(Transformation tran, SRefData sfData, Dot dot) {
        Dot rtnDot = null;
        if (tran != null && dot != null) {
            SRefData oldSref = tran.getSourceSRef();
            tran.setSourceSref(sfData);
            double[] ps = tran.dpToLp(dot.getX(), dot.getY());
            if (ps != null && ps.length >= 2) {
                double[] ps1 = tran.lpToWp(ps[0], ps[1]);
                if (ps1 != null && ps1.length >= 2) {
                    rtnDot = new Dot(ps1[0], ps1[1]);
                }
            }
            tran.setSourceSref(oldSref);
        }
        return rtnDot;
    }

    /**
     * 设备坐标矩形转窗口坐标矩形
     *
     * @param tran   坐标转换对象
     * @param sfData 参照系
     * @param rect   设备坐标矩形
     * @return 窗口坐标矩形
     */
    public static Rect dpToWp(Transformation tran, SRefData sfData, Rect rect) {
        Rect rtnRect = null;
        if (tran != null && rect != null) {
            SRefData oldSref = tran.getSourceSRef();
            tran.setSourceSref(sfData);
            Dot lbDot = CoordinateTran.dpToWp(tran, sfData, new Dot(rect.getXMin(), rect.getYMin()));
            Dot rtDot = CoordinateTran.dpToWp(tran, sfData, new Dot(rect.getXMax(), rect.getYMax()));
            if (lbDot != null && rtDot != null)
                rtnRect = new Rect(lbDot.getX(), lbDot.getY(), rtDot.getX(), rtDot.getY());
            tran.setSourceSref(oldSref);
        }
        return rtnRect;
    }

    /**
     * 逻辑坐标点转设备坐标点
     *
     * @param tran   坐标转换对象
     * @param sfData 参照系
     * @param dot    逻辑坐标点
     * @return 设备坐标点
     */
    public static Dot lpToDp(Transformation tran, SRefData sfData, Dot dot) {
        Dot rtnDot = null;
        if (tran != null && dot != null) {
            SRefData oldSref = tran.getSourceSRef();
            tran.setSourceSref(sfData);
            double[] ps = tran.lpToDp(dot.getX(), dot.getY());
            if (ps != null && ps.length >= 2) {
                rtnDot = new Dot(ps[0], ps[1]);
            }
            tran.setSourceSref(oldSref);
        }
        return rtnDot;
    }

    /**
     * 逻辑坐标矩形转设备坐标矩形
     *
     * @param tran   坐标转换对象
     * @param sfData 参照系
     * @param rect   逻辑坐标矩形
     * @return 设备坐标矩形
     */
    public static Rect lpToDp(Transformation tran, SRefData sfData, Rect rect) {
        Rect rtnRect = null;
        if (tran != null && rect != null) {
            SRefData oldSref = tran.getSourceSRef();
            tran.setSourceSref(sfData);
            double[] mins = tran.lpToDp(rect.getXMin(), rect.getYMin());
            double[] maxs = tran.lpToDp(rect.getXMax(), rect.getYMax());
            if (mins != null && mins.length >= 2 && maxs != null && maxs.length >= 2) {
                rtnRect = new Rect(mins[0], mins[1], maxs[0], maxs[1]);
            }
            tran.setSourceSref(oldSref);
        }
        return rtnRect;
    }

    /**
     * 逻辑坐标点转地图坐标点
     *
     * @param tran   坐标转换对象
     * @param sfData 参照系
     * @param dot    逻辑坐标点
     * @return 地图坐标点
     */
    public static Dot lpToMp(Transformation tran, SRefData sfData, Dot dot) {
        Dot rtnDot = null;
        if (tran != null && dot != null) {
            SRefData oldSref = tran.getSourceSRef();
            tran.setSourceSref(sfData);
            double[] ps = tran.lpToMp(dot.getX(), dot.getY());
            if (ps != null && ps.length >= 2) {
                rtnDot = new Dot(ps[0], ps[1]);
            }
            tran.setSourceSref(oldSref);
        }
        return rtnDot;
    }

    /**
     * 逻辑坐标矩形转地图坐标矩形
     *
     * @param tran   坐标转换对象
     * @param sfData 参照系
     * @param rect   逻辑坐标矩形
     * @return 地图坐标矩形
     */
    public static Rect lpToMp(Transformation tran, SRefData sfData, Rect rect) {
        Rect rtnRect = null;
        if (tran != null && rect != null) {
            SRefData oldSref = tran.getSourceSRef();
            tran.setSourceSref(sfData);
            double[] mins = tran.lpToMp(rect.getXMin(), rect.getYMin());
            double[] maxs = tran.lpToMp(rect.getXMax(), rect.getYMax());
            if (mins != null && mins.length >= 2 && maxs != null && maxs.length >= 2) {
                rtnRect = new Rect(mins[0], mins[1], maxs[0], maxs[1]);
            }
            tran.setSourceSref(oldSref);
        }
        return rtnRect;
    }

    /**
     * 逻辑坐标点转窗口坐标点
     *
     * @param tran   坐标转换对象
     * @param sfData 参照系
     * @param dot    逻辑坐标点
     * @return 窗口坐标点
     */
    public static Dot lpToWp(Transformation tran, SRefData sfData, Dot dot) {
        Dot rtnDot = null;
        if (tran != null && dot != null) {
            SRefData oldSref = tran.getSourceSRef();
            tran.setSourceSref(sfData);
            double[] ps = tran.lpToWp(dot.getX(), dot.getY());
            if (ps != null && ps.length >= 2) {
                rtnDot = new Dot(ps[0], ps[1]);
            }
            tran.setSourceSref(oldSref);
        }
        return rtnDot;
    }

    /**
     * 逻辑坐标矩形转窗口坐标矩形
     *
     * @param tran   坐标转换对象
     * @param sfData 参照系
     * @param rect   逻辑坐标矩形
     * @return 窗口坐标矩形
     */
    public static Rect lpToWp(Transformation tran, SRefData sfData, Rect rect) {
        Rect rtnRect = null;
        if (tran != null && rect != null) {
            SRefData oldSref = tran.getSourceSRef();
            tran.setSourceSref(sfData);
            double[] mins = tran.lpToWp(rect.getXMin(), rect.getYMin());
            double[] maxs = tran.lpToWp(rect.getXMax(), rect.getYMax());
            if (mins != null && mins.length >= 2 && maxs != null && maxs.length >= 2) {
                rtnRect = new Rect(mins[0], mins[1], maxs[0], maxs[1]);
            }
            tran.setSourceSref(oldSref);
        }
        return rtnRect;
    }

    /**
     * 地图坐标点转设备坐标点
     *
     * @param tran   坐标转换对象
     * @param sfData 参照系
     * @param dot    地图坐标点
     * @return 设备坐标点
     */
    public static Dot mpToDp(Transformation tran, SRefData sfData, Dot dot) {
        Dot rtnDot = null;
        if (tran != null && dot != null) {
            SRefData oldSref = tran.getSourceSRef();
            tran.setSourceSref(sfData);
            double[] ps = tran.mpToDp(dot.getX(), dot.getY());
            if (ps != null && ps.length >= 2) {
                rtnDot = new Dot(ps[0], ps[1]);
            }
            tran.setSourceSref(oldSref);
        }
        return rtnDot;
    }

    /**
     * 地图坐标矩形转设备坐标矩形
     *
     * @param tran   坐标转换对象
     * @param sfData 参照系
     * @param rect   地图坐标矩形
     * @return 设备坐标矩形
     */
    public static Rect mpToDp(Transformation tran, SRefData sfData, Rect rect) {
        Rect rtnRect = null;
        if (tran != null && rect != null) {
            SRefData oldSref = tran.getSourceSRef();
            tran.setSourceSref(sfData);
            double[] mins = tran.mpToDp(rect.getXMin(), rect.getYMin());
            double[] maxs = tran.mpToDp(rect.getXMax(), rect.getYMax());
            if (mins != null && mins.length >= 2 && maxs != null && maxs.length >= 2) {
                rtnRect = new Rect(mins[0], mins[1], maxs[0], maxs[1]);
            }
            tran.setSourceSref(oldSref);
        }
        return rtnRect;
    }

    /**
     * 地图坐标点转逻辑坐标点
     *
     * @param tran   坐标转换对象
     * @param sfData 参照系
     * @param dot    地图坐标点
     * @return 逻辑坐标点
     */
    public static Dot mpToLp(Transformation tran, SRefData sfData, Dot dot) {
        Dot rtnDot = null;
        if (tran != null && dot != null) {
            SRefData oldSref = tran.getSourceSRef();
            tran.setSourceSref(sfData);
            double[] ps = tran.mpToLp(dot.getX(), dot.getY());
            if (ps != null && ps.length >= 2) {
                rtnDot = new Dot(ps[0], ps[1]);
            }
            tran.setSourceSref(oldSref);
        }
        return rtnDot;
    }

    /**
     * 地图坐标矩形转逻辑坐标矩形
     *
     * @param tran   坐标转换对象
     * @param sfData 参照系
     * @param rect   地图坐标矩形
     * @return 逻辑坐标矩形
     */
    public static Rect mpToLp(Transformation tran, SRefData sfData, Rect rect) {
        Rect rtnRect = null;
        if (tran != null && rect != null) {
            SRefData oldSref = tran.getSourceSRef();
            tran.setSourceSref(sfData);
            double[] mins = tran.mpToLp(rect.getXMin(), rect.getYMin());
            double[] maxs = tran.mpToLp(rect.getXMax(), rect.getYMax());
            if (mins != null && mins.length >= 2 && maxs != null && maxs.length >= 2) {
                rtnRect = new Rect(mins[0], mins[1], maxs[0], maxs[1]);
            }
            tran.setSourceSref(oldSref);
        }
        return rtnRect;
    }

    /**
     * 地图坐标点转窗口坐标点
     *
     * @param tran   坐标转换对象
     * @param sfData 参照系
     * @param dot    地图坐标点
     * @return 窗口坐标点
     */
    public static Dot mpToWp(Transformation tran, SRefData sfData, Dot dot) {
        Dot rtnDot = null;
        if (tran != null && dot != null) {
            SRefData oldSref = tran.getSourceSRef();
            tran.setSourceSref(sfData);
            double[] ps = tran.mpToLp(dot.getX(), dot.getY());
            if (ps != null && ps.length >= 2) {
                double[] ps1 = tran.lpToWp(ps[0], ps[1]);
                if (ps1 != null && ps1.length >= 2) {
                    rtnDot = new Dot(ps1[0], ps1[1]);
                }
            }
            tran.setSourceSref(oldSref);
        }
        return rtnDot;
    }

    /**
     * 地图坐标矩形转窗口坐标矩形
     *
     * @param tran   坐标转换对象
     * @param sfData 参照系
     * @param rect   地图坐标矩形
     * @return 窗口坐标矩形
     */
    public static Rect mpToWp(Transformation tran, SRefData sfData, Rect rect) {
        Rect rtnRect = null;
        if (tran != null && rect != null) {
            SRefData oldSref = tran.getSourceSRef();
            tran.setSourceSref(sfData);
            Dot lbDot = CoordinateTran.mpToWp(tran, sfData, new Dot(rect.getXMin(), rect.getYMin()));
            Dot rtDot = CoordinateTran.mpToWp(tran, sfData, new Dot(rect.getXMax(), rect.getYMax()));
            if (lbDot != null && rtDot != null)
                rtnRect = new Rect(lbDot.getX(), lbDot.getY(), rtDot.getX(), rtDot.getY());
            tran.setSourceSref(oldSref);
        }
        return rtnRect;
    }

    /**
     * 窗口坐标点转设备坐标点
     *
     * @param tran   坐标转换对象
     * @param sfData 参照系
     * @param dot    窗口坐标点
     * @return 设备坐标点
     */
    public static Dot wpToDp(Transformation tran, SRefData sfData, Dot dot) {
        Dot rtnDot = null;
        if (tran != null && dot != null) {
            SRefData oldSref = tran.getSourceSRef();
            tran.setSourceSref(sfData);
            double[] ps = tran.wpToLp(dot.getX(), dot.getY());
            if (ps != null && ps.length >= 2) {
                double[] ps1 = tran.lpToDp(ps[0], ps[1]);
                if (ps1 != null && ps1.length >= 2) {
                    rtnDot = new Dot(ps1[0], ps1[1]);
                }
            }
            tran.setSourceSref(oldSref);
        }
        return rtnDot;
    }

    /**
     * 窗口坐标转设备坐标点
     *
     * @param tran   坐标转换对象
     * @param sfData 参照系
     * @param x      窗口坐标X
     * @param y      窗口坐标Y
     * @return 设备坐标点
     */
    public static Dot wpToDp(Transformation tran, SRefData sfData, double x, double y) {
        return CoordinateTran.wpToDp(tran, sfData, new Dot(x, y));
    }

    /**
     * 窗口坐标点转地图坐标点
     *
     * @param mapControl 用于取坐标转换对象和地图参照系
     * @param dot    窗口坐标点
     * @return 地图坐标点
     */
    public static Dot wpToMp(MapControl mapControl, Dot dot) {
        return wpToMp(mapControl.getTransformation(), mapControl.getSpatialReference(), dot);
    }

    /**
     * 窗口坐标点转地图坐标点
     *
     * @param tran   坐标转换对象
     * @param sfData 参照系
     * @param dot    窗口坐标点
     * @return 地图坐标点
     */
    public static Dot wpToMp(Transformation tran, SRefData sfData, Dot dot) {
        Dot rtnDot = null;
        if (tran != null && dot != null) {
            SRefData oldSref = tran.getSourceSRef();
            tran.setSourceSref(sfData);
            double[] ps = tran.wpToLp(dot.getX(), dot.getY());
            if (ps != null && ps.length >= 2) {
                double[] ps1 = tran.lpToMp(ps[0], ps[1]);
                if (ps1 != null && ps1.length >= 2) {
                    rtnDot = new Dot(ps1[0], ps1[1]);
                }
            }
            tran.setSourceSref(oldSref);
        }
        return rtnDot;
    }

    /**
     * 窗口坐标转地图坐标点
     *
     * @param mapControl 用于取坐标转换对象和地图参照系
     * @param x      窗口坐标X
     * @param y      窗口坐标Y
     * @return 地图坐标点
     */
    public static Dot wpToMp(MapControl mapControl, double x, double y) {
        return CoordinateTran.wpToMp(mapControl.getTransformation(), mapControl.getSpatialReference(), new Dot(x, y));
    }

    /**
     * 窗口坐标转地图坐标点
     *
     * @param tran   坐标转换对象
     * @param sfData 参照系
     * @param x      窗口坐标X
     * @param y      窗口坐标Y
     * @return 地图坐标点
     */
    public static Dot wpToMp(Transformation tran, SRefData sfData, double x, double y) {
        return CoordinateTran.wpToMp(tran, sfData, new Dot(x, y));
    }

    /**
     * 窗口坐标矩形转地图坐标矩形
     *
     * @param tran   坐标转换对象
     * @param sfData 参照系
     * @param rect   窗口坐标矩形
     * @return 地图坐标矩形
     */
    public static Rect wpToMp(Transformation tran, SRefData sfData, Rect rect) {
        Rect rtnRect = null;
        if (tran != null && rect != null) {
            SRefData oldSref = tran.getSourceSRef();
            tran.setSourceSref(sfData);
            Dot min = CoordinateTran.wpToMp(tran, sfData, (int) rect.getXMin(), (int) rect.getYMin());
            Dot max = CoordinateTran.wpToMp(tran, sfData, (int) rect.getXMax(), (int) rect.getYMax());
            if (min != null && max != null) {
                rtnRect = new Rect(min.getX(), min.getY(), max.getX(), max.getY());
            }
            tran.setSourceSref(oldSref);
        }
        return rtnRect;
    }

    /**
     * 窗口坐标点转逻辑坐标点
     *
     * @param tran   坐标转换对象
     * @param sfData 参照系
     * @param dot    窗口坐标点
     * @return 逻辑坐标点
     */
    public static Dot wpToLp(Transformation tran, SRefData sfData, Dot dot) {
        Dot rtnDot = null;
        if (tran != null && dot != null) {
            SRefData oldSref = tran.getSourceSRef();
            tran.setSourceSref(sfData);
            double[] ps = tran.wpToLp(dot.getX(), dot.getY());
            if (ps != null && ps.length >= 2) {
                rtnDot = new Dot(ps[0], ps[1]);
            }
            tran.setSourceSref(oldSref);
        }
        return rtnDot;
    }

    /**
     * 窗口坐标转逻辑坐标点
     *
     * @param tran   坐标转换对象
     * @param sfData 参照系
     * @param x      窗口坐标X
     * @param y      窗口坐标Y
     * @return 逻辑坐标点
     */
    public static Dot wpToLp(Transformation tran, SRefData sfData, double x, double y) {
        return CoordinateTran.wpToLp(tran, sfData, new Dot(x, y));
    }
}
