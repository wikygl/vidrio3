package l;

import android.graphics.Insets;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class G {

    /* renamed from: a  reason: collision with root package name */
    public static final int[] f4972a = {16842912};

    /* renamed from: b  reason: collision with root package name */
    public static final int[] f4973b = new int[0];

    /* renamed from: c  reason: collision with root package name */
    public static final Rect f4974c = new Rect();

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public static final boolean f4975a;

        /* renamed from: b  reason: collision with root package name */
        public static final Method f4976b;

        /* renamed from: c  reason: collision with root package name */
        public static final Field f4977c;

        /* renamed from: d  reason: collision with root package name */
        public static final Field f4978d;

        /* renamed from: e  reason: collision with root package name */
        public static final Field f4979e;
        public static final Field f;

        /* JADX WARN: Removed duplicated region for block: B:26:0x0056  */
        /* JADX WARN: Removed duplicated region for block: B:27:0x0063  */
        static {
            /*
                r0 = 1
                r1 = 0
                r2 = 0
                java.lang.String r3 = "android.graphics.Insets"
                java.lang.Class r3 = java.lang.Class.forName(r3)     // Catch: java.lang.NoSuchFieldException -> L46 java.lang.ClassNotFoundException -> L4a java.lang.NoSuchMethodException -> L4e
                java.lang.Class<android.graphics.drawable.Drawable> r4 = android.graphics.drawable.Drawable.class
                java.lang.String r5 = "getOpticalInsets"
                java.lang.reflect.Method r4 = r4.getMethod(r5, r1)     // Catch: java.lang.NoSuchFieldException -> L46 java.lang.ClassNotFoundException -> L4a java.lang.NoSuchMethodException -> L4e
                java.lang.String r5 = "left"
                java.lang.reflect.Field r5 = r3.getField(r5)     // Catch: java.lang.NoSuchFieldException -> L3a java.lang.ClassNotFoundException -> L3e java.lang.NoSuchMethodException -> L42
                java.lang.String r6 = "top"
                java.lang.reflect.Field r6 = r3.getField(r6)     // Catch: java.lang.NoSuchFieldException -> L30 java.lang.ClassNotFoundException -> L34 java.lang.NoSuchMethodException -> L37
                java.lang.String r7 = "right"
                java.lang.reflect.Field r7 = r3.getField(r7)     // Catch: java.lang.Throwable -> L2d
                java.lang.String r8 = "bottom"
                java.lang.reflect.Field r3 = r3.getField(r8)     // Catch: java.lang.Throwable -> L2b
                r8 = 1
                goto L54
            L2b:
                goto L52
            L2d:
                r7 = r1
                goto L52
            L30:
                r6 = r1
            L32:
                r7 = r6
                goto L52
            L34:
                r6 = r1
                goto L32
            L37:
                r6 = r1
                goto L32
            L3a:
                r5 = r1
            L3c:
                r6 = r5
                goto L32
            L3e:
                r5 = r1
            L40:
                r6 = r5
                goto L32
            L42:
                r5 = r1
            L44:
                r6 = r5
                goto L32
            L46:
                r4 = r1
                r5 = r4
                goto L3c
            L4a:
                r4 = r1
                r5 = r4
                goto L40
            L4e:
                r4 = r1
                r5 = r4
                goto L44
            L52:
                r3 = r1
                r8 = 0
            L54:
                if (r8 == 0) goto L63
                l.G.a.f4976b = r4
                l.G.a.f4977c = r5
                l.G.a.f4978d = r6
                l.G.a.f4979e = r7
                l.G.a.f = r3
                l.G.a.f4975a = r0
                goto L6f
            L63:
                l.G.a.f4976b = r1
                l.G.a.f4977c = r1
                l.G.a.f4978d = r1
                l.G.a.f4979e = r1
                l.G.a.f = r1
                l.G.a.f4975a = r2
            L6f:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: l.G.a.<clinit>():void");
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class b {
        public static Insets a(Drawable drawable) {
            return drawable.getOpticalInsets();
        }
    }

    public static void a(Drawable drawable) {
        String name = drawable.getClass().getName();
        int i4 = Build.VERSION.SDK_INT;
        int[] iArr = f4972a;
        int[] iArr2 = f4973b;
        if (i4 == 21 && "android.graphics.drawable.VectorDrawable".equals(name)) {
            int[] state = drawable.getState();
            if (state != null && state.length != 0) {
                drawable.setState(iArr2);
            } else {
                drawable.setState(iArr);
            }
            drawable.setState(state);
        } else if (i4 >= 29 && i4 < 31 && "android.graphics.drawable.ColorStateListDrawable".equals(name)) {
            int[] state2 = drawable.getState();
            if (state2 != null && state2.length != 0) {
                drawable.setState(iArr2);
            } else {
                drawable.setState(iArr);
            }
            drawable.setState(state2);
        }
    }

    public static Rect b(Drawable drawable) {
        int i4;
        int i5;
        int i6;
        int i7;
        int i8 = Build.VERSION.SDK_INT;
        if (i8 >= 29) {
            Insets a4 = b.a(drawable);
            i4 = a4.left;
            i5 = a4.top;
            i6 = a4.right;
            i7 = a4.bottom;
            return new Rect(i4, i5, i6, i7);
        }
        if (drawable instanceof F.c) {
            drawable = ((F.c) drawable).b();
        }
        if (i8 < 29) {
            if (a.f4975a) {
                try {
                    Object invoke = a.f4976b.invoke(drawable, null);
                    if (invoke != null) {
                        return new Rect(a.f4977c.getInt(invoke), a.f4978d.getInt(invoke), a.f4979e.getInt(invoke), a.f.getInt(invoke));
                    }
                } catch (IllegalAccessException | InvocationTargetException unused) {
                }
            }
        } else {
            boolean z4 = a.f4975a;
        }
        return f4974c;
    }

    public static PorterDuff.Mode c(int i4, PorterDuff.Mode mode) {
        if (i4 != 3) {
            if (i4 != 5) {
                if (i4 != 9) {
                    switch (i4) {
                        case 14:
                            return PorterDuff.Mode.MULTIPLY;
                        case 15:
                            return PorterDuff.Mode.SCREEN;
                        case 16:
                            return PorterDuff.Mode.ADD;
                        default:
                            return mode;
                    }
                }
                return PorterDuff.Mode.SRC_ATOP;
            }
            return PorterDuff.Mode.SRC_IN;
        }
        return PorterDuff.Mode.SRC_OVER;
    }
}
