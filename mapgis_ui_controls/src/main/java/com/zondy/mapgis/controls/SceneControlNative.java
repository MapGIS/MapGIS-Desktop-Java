package com.zondy.mapgis.controls;
import java.nio.ByteBuffer;

/**
 * Created by Administrator on 2020/3/9.
 */
public class SceneControlNative {
    public static native long jni_CreateObj(SceneControl sceneControl);
    public static native void jni_DeleteObj(long handle);
    public static native void jni_OnSize(long handle, int width, int height);
    public static native void jni_Draw(long handle, int width, int height, ByteBuffer buffer);
    public static native void jni_SetScene(long handle, long secneHandle);
    public static native long jni_GetScene(long handle);
    public static native void jni_InterActionUpdatePanOrigin(long handle, double dragX, double dragY);
    public static native void jni_OnMouseMove(long nativeSceneControl, long nFlags, int x, int y);
    public static native void jni_OnLButtonDown(long nativeMapControl, long nFlags, int x, int y);
    public static native void jni_OnLButtonUp(long nativeMapControl, long nFlags, int x, int y);
    public static native void jni_OnMouseWheel(long nativeMapControl, long nFlags, short zDelta, int x, int y);
}
