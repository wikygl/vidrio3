package D;

import D.f;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import j$.util.Objects;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.WeakHashMap;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class f {

    /* renamed from: a  reason: collision with root package name */
    public static final ThreadLocal<TypedValue> f527a = new ThreadLocal<>();

    /* renamed from: b  reason: collision with root package name */
    public static final WeakHashMap<d, SparseArray<c>> f528b = new WeakHashMap<>(0);

    /* renamed from: c  reason: collision with root package name */
    public static final Object f529c = new Object();

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class a {
        public static Drawable a(Resources resources, int i4, Resources.Theme theme) {
            return resources.getDrawable(i4, theme);
        }

        public static Drawable b(Resources resources, int i4, int i5, Resources.Theme theme) {
            return resources.getDrawableForDensity(i4, i5, theme);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class b {
        public static int a(Resources resources, int i4, Resources.Theme theme) {
            return resources.getColor(i4, theme);
        }

        public static ColorStateList b(Resources resources, int i4, Resources.Theme theme) {
            return resources.getColorStateList(i4, theme);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class c {

        /* renamed from: a  reason: collision with root package name */
        public final ColorStateList f530a;

        /* renamed from: b  reason: collision with root package name */
        public final Configuration f531b;

        /* renamed from: c  reason: collision with root package name */
        public final int f532c;

        public c(ColorStateList colorStateList, Configuration configuration, Resources.Theme theme) {
            int hashCode;
            this.f530a = colorStateList;
            this.f531b = configuration;
            if (theme == null) {
                hashCode = 0;
            } else {
                hashCode = theme.hashCode();
            }
            this.f532c = hashCode;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static final class d {

        /* renamed from: a  reason: collision with root package name */
        public final Resources f533a;

        /* renamed from: b  reason: collision with root package name */
        public final Resources.Theme f534b;

        public d(Resources resources, Resources.Theme theme) {
            this.f533a = resources;
            this.f534b = theme;
        }

        public final boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || d.class != obj.getClass()) {
                return false;
            }
            d dVar = (d) obj;
            if (this.f533a.equals(dVar.f533a) && Objects.equals(this.f534b, dVar.f534b)) {
                return true;
            }
            return false;
        }

        public final int hashCode() {
            return Objects.hash(this.f533a, this.f534b);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static abstract class e {
        public final void a(final int i4) {
            new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: D.h
                @Override // java.lang.Runnable
                public final void run() {
                    f.e.this.b(i4);
                }
            });
        }

        public abstract void b(int i4);

        public abstract void c(Typeface typeface);
    }

    /* renamed from: D.f$f  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static final class C0005f {

        /* renamed from: D.f$f$a */
        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
        public static class a {

            /* renamed from: a  reason: collision with root package name */
            public static final Object f535a = new Object();

            /* renamed from: b  reason: collision with root package name */
            public static Method f536b;

            /* renamed from: c  reason: collision with root package name */
            public static boolean f537c;
        }

        /* renamed from: D.f$f$b */
        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
        public static class b {
            public static void a(Resources.Theme theme) {
                theme.rebase();
            }
        }

        public static void a(Resources.Theme theme) {
            int i4 = Build.VERSION.SDK_INT;
            if (i4 >= 29) {
                b.a(theme);
            } else if (i4 >= 23) {
                synchronized (a.f535a) {
                    if (!a.f537c) {
                        try {
                            Method declaredMethod = Resources.Theme.class.getDeclaredMethod("rebase", null);
                            a.f536b = declaredMethod;
                            declaredMethod.setAccessible(true);
                        } catch (NoSuchMethodException e4) {
                            Log.i("ResourcesCompat", "Failed to retrieve rebase() method", e4);
                        }
                        a.f537c = true;
                    }
                    Method method = a.f536b;
                    if (method != null) {
                        try {
                            method.invoke(theme, null);
                        } catch (IllegalAccessException | InvocationTargetException e5) {
                            Log.i("ResourcesCompat", "Failed to invoke rebase() method via reflection", e5);
                            a.f536b = null;
                        }
                    }
                }
            }
        }
    }

    public static void a(d dVar, int i4, ColorStateList colorStateList, Resources.Theme theme) {
        synchronized (f529c) {
            try {
                WeakHashMap<d, SparseArray<c>> weakHashMap = f528b;
                SparseArray<c> sparseArray = weakHashMap.get(dVar);
                if (sparseArray == null) {
                    sparseArray = new SparseArray<>();
                    weakHashMap.put(dVar, sparseArray);
                }
                sparseArray.append(i4, new c(colorStateList, dVar.f533a.getConfiguration(), theme));
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static Typeface b(Context context, int i4) {
        if (context.isRestricted()) {
            return null;
        }
        return c(context, i4, new TypedValue(), 0, null, false, false);
    }

    /* JADX WARN: Removed duplicated region for block: B:38:0x00d8  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x00dd A[ADDED_TO_REGION] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static android.graphics.Typeface c(android.content.Context r16, int r17, android.util.TypedValue r18, int r19, D.f.e r20, boolean r21, boolean r22) {
        /*
            Method dump skipped, instructions count: 300
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: D.f.c(android.content.Context, int, android.util.TypedValue, int, D.f$e, boolean, boolean):android.graphics.Typeface");
    }
}
