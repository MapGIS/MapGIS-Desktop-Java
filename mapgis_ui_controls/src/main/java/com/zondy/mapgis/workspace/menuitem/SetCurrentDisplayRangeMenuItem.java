package com.zondy.mapgis.workspace.menuitem;

import com.zondy.mapgis.controls.MapControl;
import com.zondy.mapgis.controls.SceneControl;
import com.zondy.mapgis.geometry.Dot3D;
import com.zondy.mapgis.geometry.Rect;
import com.zondy.mapgis.geometry.Rect3D;
import com.zondy.mapgis.map.DocumentItem;
import com.zondy.mapgis.map.MapLayer;
import com.zondy.mapgis.map.Transformation;
import com.zondy.mapgis.scene.*;
import com.zondy.mapgis.workspace.StaticFunction;
import com.zondy.mapgis.workspace.engine.ISingleMenuItem;
import com.zondy.mapgis.workspace.engine.IWorkspace;
import javafx.scene.image.Image;

/**
 * 设为当前显示范围
 *
 * @author cxy
 * @date 2019/11/18
 */
public class SetCurrentDisplayRangeMenuItem implements ISingleMenuItem {
    private IWorkspace workspace;

    /**
     * 点击菜单项
     *
     * @param item 文档项
     */
    @Override
    public void onClick(DocumentItem item) {
        if (item instanceof MapLayer || item instanceof Map3DLayer) {
            if (item instanceof MapLayer) {
                // region 二维图层
                MapLayer layer = (MapLayer) item;
                MapControl mc = this.workspace.getMapControl(StaticFunction.getOwnerMap(item));
                if (mc != null) {
                    if (mc.getMap().getIsProjTrans()) {
                        Rect rt = layer.getRange();
                        if (rt != null) {
                            mc.getTransformation().setIsProjTrans(true);
                            mc.getTransformation().setTargetSref(mc.getMap().getProjTrans());
                            // TODO: 待添加接口
//                            mc.getTransformation().SetSourceSRef(layer.getSrefInfo());
                            double[] temp = mc.getTransformation().lpToMp(rt.getXMin(), rt.getYMin());
                            double mXMin = temp[0];
                            double mYMin = temp[1];
                            temp = mc.getTransformation().lpToMp(rt.getXMax(), rt.getYMax());
                            double mXMax = temp[0];
                            double mYMax = temp[1];
                            StaticFunction.setDisplayScale(mc, StaticFunction.restoreRect(StaticFunction.calcRotateRect(mc, new Rect(mXMin, mYMin, mXMax, mYMax))));
                        }
                    } else {
                        StaticFunction.setDisplayScale(mc, StaticFunction.restoreRect(StaticFunction.calcRotateRect(mc, layer.getRange())));
                    }
                    mc.refreshWnd();
                }
                // endregion
            } else {
                // region 三维图层
                Map3DLayer layer = (Map3DLayer) item;
                // TODO: 待添加 map3DLayer.GetExtent() 接口
                Rect3D rt = null;//layer.GetExtent();
                if (rt != null) {
                    if (layer instanceof ModelLayer) {
                        // region 处理显示比
                        Dot3D scale = ((ModelLayer) layer).getScale();
                        if (scale != null) {
                            rt.setXMin(rt.getXMin() * scale.getX());
                            rt.setYMin(rt.getYMin() * scale.getY());
                            rt.setZMin(rt.getZMin() * scale.getZ());
                            rt.setXMax(rt.getXMax() * scale.getX());
                            rt.setYMax(rt.getYMax() * scale.getY());
                            rt.setZMax(rt.getZMax() * scale.getZ());
                        }
                        // endregion
                    } else if (layer instanceof ModelCacheLayer) {
                        // region 处理显示比
                        Dot3D scale = ((ModelCacheLayer) layer).getScale();
                        if (scale != null) {
                            rt.setXMin(rt.getXMin() * scale.getX());
                            rt.setYMin(rt.getYMin() * scale.getY());
                            rt.setZMin(rt.getZMin() * scale.getZ());
                            rt.setXMax(rt.getXMax() * scale.getX());
                            rt.setYMax(rt.getYMax() * scale.getY());
                            rt.setZMax(rt.getZMax() * scale.getZ());
                        }
                        // endregion
                    } else if (item instanceof TerrainLayer) {
                        // region 处理显示比
                        double scale = ((TerrainLayer) item).getElevationScale();
                        rt.setZMax(rt.getZMax() * scale);
                        rt.setZMin(rt.getZMin() * scale);
                        // endregion
                    }
                    this.setSceneViewRect(item, rt);
                }
                // endregion
            }
        }
    }

    /**
     * 设置场景视图显示范围
     *
     * @param item 文档项
     * @param rt   立方体
     */
    private void setSceneViewRect(DocumentItem item, Rect3D rt) {
        if (rt != null) {
            Scene scene = StaticFunction.getOwnerScene(item);
            SceneControl sc = this.workspace.getSceneControl(scene);
            if (sc != null) {
                if (scene.isProjTrans() && !(item instanceof ModelLayer || item instanceof ModelCacheLayer)) {
                    // region 非模型层动态投影下的特殊处理
                    Transformation trans = new Transformation();
                    trans.setIsProjTrans(scene.isProjTrans());
                    // TODO: 待添加 trans.SetSourceSRef(), map3DLayer.GetSRS()
//                    trans.SetSourceSRef(((Map3DLayer)item).GetSRS());
                    trans.setTargetSref(scene.getProjTrans());
                    double[] temp = trans.lpToMp(rt.getXMin(), rt.getYMin());
                    double mXMin = temp[0];
                    double mYMin = temp[1];
                    trans.lpToMp(rt.getXMax(), rt.getYMax());
                    double mXMax = temp[0];
                    double mYMax = temp[1];
                    rt.setXMin(mXMin);
                    rt.setYMin(mYMin);
                    rt.setXMax(mXMax);
                    rt.setYMax(mYMax);
                    // endregion
                }
                if ((item instanceof ModelLayer || item instanceof ModelCacheLayer) && sc.getSceneMode() == SceneMode.GLOBE) {
                    // region 模型层特殊处理
                    double x = rt.getXMin();
                    double y = rt.getYMin();
                    double z = rt.getZMin();
                    double x1 = rt.getXMax();
                    double y1 = rt.getYMax();
                    double z1 = rt.getZMax();
                    double[] temp = sc.cartesianToGeodetic(x, y, z);
                    x = temp[0];
                    y = temp[1];
                    z = temp[2];
                    temp = sc.cartesianToGeodetic(x1, y1, z1);
                    x1 = temp[0];
                    y1 = temp[1];
                    z1 = temp[2];
                    rt.setXMin(Math.min(x, x1));
                    rt.setXMax(Math.max(x, x1));
                    rt.setYMin(Math.min(y, y1));
                    rt.setYMax(Math.max(y, y1));
                    rt.setZMin(Math.min(z, z1));
                    rt.setZMax(Math.max(z, z1));
                    // endregion
                }
                if (sc.getSceneMode() == SceneMode.LOCAL) {
                    sc.setViewRect(rt);
                } else {
                    // region 设置球面观察点
                    double xMin = rt.getXMin(), yMin = rt.getYMin(), zMin = rt.getZMin();
                    double xMax = rt.getXMax(), yMax = rt.getYMax(), zMax = rt.getZMax();
                    Rect rect = new Rect(xMin, yMin, xMax, yMax);
                    Camera ca = new Camera();
                    // TODO: 三维底层定义接口太奇怪
                    sc.getCamera(ca);
                    double x = 0, y = 0, z = 0;
                    z = calcDistance(rect, ca.getFov() * Math.PI / 180.0);
                    x = (rect.getXMax() + rect.getXMin()) / 2;
                    y = (rect.getYMax() + rect.getYMin()) / 2;
                    sc.setViewPos(x, y, z, 0, 0, true);
                    // endregion
                }
            }
        }
    }

    /**
     * 计算视角高度
     *
     * @param rt  矩形对象
     * @param fov 视角弧度
     * @return 距离
     */
    private double calcDistance(Rect rt, double fov) {
        double distance = 0;
        double R = 6378137;

        //计算最大跨度
        double dx = rt.getXMax() - rt.getXMin();
        double dy = rt.getYMax() - rt.getYMin();
        if (dx < 0.0) {
            dx += 360.0;
        }

        double dt = Math.max(dx, dy);
        if (dt > 180.0) {
            dt = 180.0;
        }

        double alphe = dt * 0.5 * Math.PI / 180;
        double beta = Math.PI / 2 - (fov / 2.0);
        if (alphe < beta) {
            distance = R * Math.cos(alphe) + R * Math.sin(alphe) * Math.tan(beta) - R;
        } else {
            distance = R / Math.cos(alphe) - R;
        }
        return distance;
    }

    /**
     * 获取菜单项图标
     *
     * @return 菜单项图标
     */
    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/Png_SetCurrentDisplayRange_16.png"));
    }

    /**
     * 获取命令按钮的标题
     *
     * @return 标题
     */
    @Override
    public String getCaption() {
        return "设为当前显示范围";
    }

    /**
     * 获取命令按钮是否可用
     *
     * @return true/false
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * 获取命令按钮是否可见
     *
     * @return true/false
     */
    @Override
    public boolean isVisible() {
        return true;
    }

    /**
     * 获取命令按钮是否选中
     *
     * @return true/false
     */
    @Override
    public boolean isChecked() {
        return false;
    }

    /**
     * 获取是否启用分割符
     *
     * @return true/false
     */
    @Override
    public boolean isBeginGroup() {
        return true;
    }

    /**
     * 创建后事件
     *
     * @param ws 工作空间引擎
     */
    @Override
    public void onCreate(IWorkspace ws) {
        workspace = ws;
    }
}
