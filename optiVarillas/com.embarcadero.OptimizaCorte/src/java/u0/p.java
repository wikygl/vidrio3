package u0;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public class p {

    /* renamed from: a  reason: collision with root package name */
    public static boolean f6005a = true;

    /* renamed from: b  reason: collision with root package name */
    public static Method f6006b;

    /* renamed from: c  reason: collision with root package name */
    public static boolean f6007c;

    /* renamed from: d  reason: collision with root package name */
    public static Field f6008d;

    /* renamed from: e  reason: collision with root package name */
    public static boolean f6009e;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class a {
        public static float a(View view) {
            float transitionAlpha;
            transitionAlpha = view.getTransitionAlpha();
            return transitionAlpha;
        }

        public static void b(View view, float f) {
            view.setTransitionAlpha(f);
        }
    }

    @SuppressLint({"NewApi"})
    public float a(View view) {
        if (f6005a) {
            try {
                return a.a(view);
            } catch (NoSuchMethodError unused) {
                f6005a = false;
            }
        }
        return view.getAlpha();
    }

    @SuppressLint({"BanUncheckedReflection"})
    public void b(View view, int i4, int i5, int i6, int i7) {
        if (!f6007c) {
            try {
                Class cls = Integer.TYPE;
                Method declaredMethod = View.class.getDeclaredMethod("setFrame", cls, cls, cls, cls);
                f6006b = declaredMethod;
                declaredMethod.setAccessible(true);
            } catch (NoSuchMethodException e4) {
                Log.i("ViewUtilsApi19", "Failed to retrieve setFrame method", e4);
            }
            f6007c = true;
        }
        Method method = f6006b;
        if (method != null) {
            try {
                method.invoke(view, Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6), Integer.valueOf(i7));
            } catch (IllegalAccessException unused) {
            } catch (InvocationTargetException e5) {
                throw new RuntimeException(e5.getCause());
            }
        }
    }

    @SuppressLint({"NewApi"})
    public void c(View view, float f) {
        if (f6005a) {
            try {
                a.b(view, f);
                return;
            } catch (NoSuchMethodError unused) {
                f6005a = false;
            }
        }
        view.setAlpha(f);
    }

    @SuppressLint({"SoonBlockedPrivateApi"})
    public void d(View view, int i4) {
        if (!f6009e) {
            try {
                Field declaredField = View.class.getDeclaredField("mViewFlags");
                f6008d = declaredField;
                declaredField.setAccessible(true);
            } catch (NoSuchFieldException unused) {
                Log.i("ViewUtilsApi19", "fetchViewFlagsField: ");
            }
            f6009e = true;
        }
        Field field = f6008d;
        if (field != null) {
            try {
                f6008d.setInt(view, i4 | (field.getInt(view) & (-13)));
            } catch (IllegalAccessException unused2) {
            }
        }
    }
}
