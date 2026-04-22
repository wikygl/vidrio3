package S;

import android.os.Build;
import android.util.Log;
import android.widget.PopupWindow;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import l.C0708q;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class f {

    /* renamed from: a  reason: collision with root package name */
    public static Method f2098a;

    /* renamed from: b  reason: collision with root package name */
    public static boolean f2099b;

    /* renamed from: c  reason: collision with root package name */
    public static Field f2100c;

    /* renamed from: d  reason: collision with root package name */
    public static boolean f2101d;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class a {
        public static boolean a(PopupWindow popupWindow) {
            return popupWindow.getOverlapAnchor();
        }

        public static int b(PopupWindow popupWindow) {
            return popupWindow.getWindowLayoutType();
        }

        public static void c(PopupWindow popupWindow, boolean z4) {
            popupWindow.setOverlapAnchor(z4);
        }

        public static void d(PopupWindow popupWindow, int i4) {
            popupWindow.setWindowLayoutType(i4);
        }
    }

    public static void a(C0708q c0708q, boolean z4) {
        if (Build.VERSION.SDK_INT >= 23) {
            a.c(c0708q, z4);
            return;
        }
        if (!f2101d) {
            try {
                Field declaredField = PopupWindow.class.getDeclaredField("mOverlapAnchor");
                f2100c = declaredField;
                declaredField.setAccessible(true);
            } catch (NoSuchFieldException e4) {
                Log.i("PopupWindowCompatApi21", "Could not fetch mOverlapAnchor field from PopupWindow", e4);
            }
            f2101d = true;
        }
        Field field = f2100c;
        if (field != null) {
            try {
                field.set(c0708q, Boolean.valueOf(z4));
            } catch (IllegalAccessException e5) {
                Log.i("PopupWindowCompatApi21", "Could not set overlap anchor field in PopupWindow", e5);
            }
        }
    }

    public static void b(PopupWindow popupWindow, int i4) {
        if (Build.VERSION.SDK_INT >= 23) {
            a.d(popupWindow, i4);
            return;
        }
        if (!f2099b) {
            try {
                Method declaredMethod = PopupWindow.class.getDeclaredMethod("setWindowLayoutType", Integer.TYPE);
                f2098a = declaredMethod;
                declaredMethod.setAccessible(true);
            } catch (Exception unused) {
            }
            f2099b = true;
        }
        Method method = f2098a;
        if (method != null) {
            try {
                method.invoke(popupWindow, Integer.valueOf(i4));
            } catch (Exception unused2) {
            }
        }
    }
}
