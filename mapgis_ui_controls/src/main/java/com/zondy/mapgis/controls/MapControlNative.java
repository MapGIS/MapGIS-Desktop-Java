package com.zondy.mapgis.controls;

import com.zondy.mapgis.geometry.Dot;
import com.zondy.mapgis.geometry.Rect;

/**
 * Created by zxf on 2019/10/31.
 */
public class MapControlNative {
    public static native long jni_CreateObj(MapControl mapControl);
    public static native void jni_DeleteObj(long nativeMapControl);
    public static native int jni_IsCancel(long handle);
    public static native long jni_GetMap(long nativeMapControl);
    public static native void jni_SetMap(long nativeMapControl, long map);
    public static native long jni_GetTransformation(long nativeMapControl);
    public static native void jni_ClearIATool(long nativeMapControl);
    public static native double jni_GetCurMapScale(long nativeMapControl);
    public static native int jni_SetCurMapScale(long nativeMapControl, double scale, boolean refresh);
    public static native void jni_SetZoomScale(long nativeMapControl, double zoomScale);
    public static native double jni_GetZoomScale(long nativeMapControl);
    public static native void jni_ZoomIn(long nativeMapControl);
    public static native void jni_ZoomOut(long nativeMapControl);
    public static native void jni_MoveWnd(long nativeMapControl);
    public static native void jni_RefreshWnd(long nativeMapControl);
    public static native void jni_RestoreWnd(long nativeMapControl);
    public static native void jni_ShowPreWnd(long nativeMapControl);
    public static native void jni_ShowNextWnd(long nativeMapControl);
    public static native void jni_JumpWnd(long nativeMapControl, double devX, double devY, boolean refresh);
    public static native void jni_JumpWnd(long nativeMapControl, Rect devRect, boolean refresh);
    public static native int jni_GetWinOpType(long nativeMapControl);
    public static native void jni_OnSize(long nativeMapControl, long nType, int cx, int cy);
    public static native void jni_OnLButtonDown(long nativeMapControl, long nFlags, int x, int y);
    public static native void jni_OnLButtonUp(long nativeMapControl, long nFlags, int x, int y);
    public static native void jni_OnLButtonDblClk(long nativeMapControl, long nFlags, int x, int y);
    public static native void jni_OnMouseMove(long nativeMapControl, long nFlags, int x, int y);
    public static native void jni_OnRButtonDown(long nativeMapControl, long nFlags, int x, int y);
    public static native void jni_OnRButtonUp(long nativeMapControl, long nFlags, int x, int y);
    public static native void jni_OnMButtonDblClk(long nativeMapControl, long nFlags, int x, int y);
    public static native void jni_OnMButtonDown(long nativeMapControl, long nFlags, int x, int y);
    public static native void jni_OnMButtonUp(long nativeMapControl, long nFlags, int x, int y);
    public static native void jni_OnKeyDown(long nativeMapControl, long nChar, long nRepCnt, long nFlags);
    public static native void jni_OnKeyUp(long nativeMapControl, long nChar, long nRepCnt, long nFlags);
    public static native void jni_OnMouseWheel(long nativeMapControl, long nFlags, short zDelta, int x, int y);
    public static native void jni_OnRefreshOverlay(long nativeMapControl);
    public static native long jni_GetSketchGraphicsOverlay(long nativeMapControl);
    public static native int  jni_SetSketchGraphicsOverlay(long nativeMapControl,long hOverlay);
    public static native long jni_GetSketchGeometrys(long nativeMapControl);
    public static native int jni_IdentifySelectionsByDot(long nativeMapControl, long hSelResultList, int withAtt, int withSymbol, Dot point, double dTol, boolean withSketch, int lMaxinumResults);
    public static native int jni_IdentifySelectionsByRect(long nativeMapControl, long hSelResultList, int withAtt, int withSymbol, Rect rect, boolean withSketch, int lMaxinumResults);
}
