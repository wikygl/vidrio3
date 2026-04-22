package F;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import java.lang.reflect.Method;
import org.xmlpull.v1.XmlPullParser;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class a {

    /* renamed from: a  reason: collision with root package name */
    public static Method f882a;

    /* renamed from: b  reason: collision with root package name */
    public static boolean f883b;

    /* renamed from: c  reason: collision with root package name */
    public static Method f884c;

    /* renamed from: d  reason: collision with root package name */
    public static boolean f885d;

    /* renamed from: F.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class C0006a {
        public static void a(Drawable drawable, Resources.Theme theme) {
            drawable.applyTheme(theme);
        }

        public static boolean b(Drawable drawable) {
            return drawable.canApplyTheme();
        }

        public static ColorFilter c(Drawable drawable) {
            return drawable.getColorFilter();
        }

        public static void d(Drawable drawable, Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) {
            drawable.inflate(resources, xmlPullParser, attributeSet, theme);
        }

        public static void e(Drawable drawable, float f, float f4) {
            drawable.setHotspot(f, f4);
        }

        public static void f(Drawable drawable, int i4, int i5, int i6, int i7) {
            drawable.setHotspotBounds(i4, i5, i6, i7);
        }

        public static void g(Drawable drawable, int i4) {
            drawable.setTint(i4);
        }

        public static void h(Drawable drawable, ColorStateList colorStateList) {
            drawable.setTintList(colorStateList);
        }

        public static void i(Drawable drawable, PorterDuff.Mode mode) {
            drawable.setTintMode(mode);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class b {
        public static int a(Drawable drawable) {
            return drawable.getLayoutDirection();
        }

        public static boolean b(Drawable drawable, int i4) {
            return drawable.setLayoutDirection(i4);
        }
    }

    public static void a(Drawable drawable) {
        DrawableContainer.DrawableContainerState drawableContainerState;
        if (Build.VERSION.SDK_INT >= 23) {
            drawable.clearColorFilter();
            return;
        }
        drawable.clearColorFilter();
        if (drawable instanceof InsetDrawable) {
            a(((InsetDrawable) drawable).getDrawable());
        } else if (drawable instanceof c) {
            a(((c) drawable).b());
        } else if ((drawable instanceof DrawableContainer) && (drawableContainerState = (DrawableContainer.DrawableContainerState) ((DrawableContainer) drawable).getConstantState()) != null) {
            int childCount = drawableContainerState.getChildCount();
            for (int i4 = 0; i4 < childCount; i4++) {
                Drawable child = drawableContainerState.getChild(i4);
                if (child != null) {
                    a(child);
                }
            }
        }
    }

    public static int b(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= 23) {
            return b.a(drawable);
        }
        if (!f885d) {
            try {
                Method declaredMethod = Drawable.class.getDeclaredMethod("getLayoutDirection", null);
                f884c = declaredMethod;
                declaredMethod.setAccessible(true);
            } catch (NoSuchMethodException e4) {
                Log.i("DrawableCompat", "Failed to retrieve getLayoutDirection() method", e4);
            }
            f885d = true;
        }
        Method method = f884c;
        if (method != null) {
            try {
                return ((Integer) method.invoke(drawable, null)).intValue();
            } catch (Exception e5) {
                Log.i("DrawableCompat", "Failed to invoke getLayoutDirection() via reflection", e5);
                f884c = null;
                return 0;
            }
        }
        return 0;
    }

    public static boolean c(Drawable drawable, int i4) {
        if (Build.VERSION.SDK_INT >= 23) {
            return b.b(drawable, i4);
        }
        if (!f883b) {
            try {
                Method declaredMethod = Drawable.class.getDeclaredMethod("setLayoutDirection", Integer.TYPE);
                f882a = declaredMethod;
                declaredMethod.setAccessible(true);
            } catch (NoSuchMethodException e4) {
                Log.i("DrawableCompat", "Failed to retrieve setLayoutDirection(int) method", e4);
            }
            f883b = true;
        }
        Method method = f882a;
        if (method != null) {
            try {
                method.invoke(drawable, Integer.valueOf(i4));
                return true;
            } catch (Exception e5) {
                Log.i("DrawableCompat", "Failed to invoke setLayoutDirection(int) via reflection", e5);
                f882a = null;
            }
        }
        return false;
    }

    public static void d(Drawable drawable, int i4) {
        C0006a.g(drawable, i4);
    }

    public static void e(Drawable drawable, ColorStateList colorStateList) {
        C0006a.h(drawable, colorStateList);
    }

    public static void f(Drawable drawable, PorterDuff.Mode mode) {
        C0006a.i(drawable, mode);
    }

    /* JADX WARN: Type inference failed for: r0v2, types: [android.graphics.drawable.Drawable, F.d] */
    public static Drawable g(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= 23) {
            return drawable;
        }
        if (!(drawable instanceof F.b)) {
            ?? drawable2 = new Drawable();
            drawable2.f890m = drawable2.d();
            drawable2.a(drawable);
            e.f();
            return drawable2;
        }
        return drawable;
    }
}
