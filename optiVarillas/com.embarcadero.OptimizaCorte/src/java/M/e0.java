package M;

import M.C0231m;
import android.annotation.SuppressLint;
import android.graphics.Insets;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowInsets;
import e0.C0405a;
import j$.util.Objects;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class e0 {

    /* renamed from: b  reason: collision with root package name */
    public static final e0 f1586b;

    /* renamed from: a  reason: collision with root package name */
    public final k f1587a;

    @SuppressLint({"SoonBlockedPrivateApi"})
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public static final Field f1588a;

        /* renamed from: b  reason: collision with root package name */
        public static final Field f1589b;

        /* renamed from: c  reason: collision with root package name */
        public static final Field f1590c;

        /* renamed from: d  reason: collision with root package name */
        public static final boolean f1591d;

        static {
            try {
                Field declaredField = View.class.getDeclaredField("mAttachInfo");
                f1588a = declaredField;
                declaredField.setAccessible(true);
                Class<?> cls = Class.forName("android.view.View$AttachInfo");
                Field declaredField2 = cls.getDeclaredField("mStableInsets");
                f1589b = declaredField2;
                declaredField2.setAccessible(true);
                Field declaredField3 = cls.getDeclaredField("mContentInsets");
                f1590c = declaredField3;
                declaredField3.setAccessible(true);
                f1591d = true;
            } catch (ReflectiveOperationException e4) {
                Log.w("WindowInsetsCompat", "Failed to get visible insets from AttachInfo " + e4.getMessage(), e4);
            }
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public static class d extends c {
        public d() {
        }

        @Override // M.e0.e
        public void c(int i4, E.b bVar) {
            this.f1597c.setInsets(l.a(i4), bVar.d());
        }

        public d(e0 e0Var) {
            super(e0Var);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class e {

        /* renamed from: a  reason: collision with root package name */
        public final e0 f1598a;

        /* renamed from: b  reason: collision with root package name */
        public E.b[] f1599b;

        public e() {
            this(new e0());
        }

        public final void a() {
            E.b[] bVarArr = this.f1599b;
            if (bVarArr != null) {
                E.b bVar = bVarArr[0];
                E.b bVar2 = bVarArr[1];
                e0 e0Var = this.f1598a;
                if (bVar2 == null) {
                    bVar2 = e0Var.f1587a.f(2);
                }
                if (bVar == null) {
                    bVar = e0Var.f1587a.f(1);
                }
                g(E.b.a(bVar, bVar2));
                E.b bVar3 = this.f1599b[4];
                if (bVar3 != null) {
                    f(bVar3);
                }
                E.b bVar4 = this.f1599b[5];
                if (bVar4 != null) {
                    d(bVar4);
                }
                E.b bVar5 = this.f1599b[6];
                if (bVar5 != null) {
                    h(bVar5);
                }
            }
        }

        public e0 b() {
            throw null;
        }

        public void c(int i4, E.b bVar) {
            char c4;
            if (this.f1599b == null) {
                this.f1599b = new E.b[9];
            }
            for (int i5 = 1; i5 <= 256; i5 <<= 1) {
                if ((i4 & i5) != 0) {
                    E.b[] bVarArr = this.f1599b;
                    if (i5 != 1) {
                        c4 = 2;
                        if (i5 != 2) {
                            if (i5 != 4) {
                                c4 = '\b';
                                if (i5 != 8) {
                                    if (i5 != 16) {
                                        if (i5 != 32) {
                                            if (i5 != 64) {
                                                if (i5 != 128) {
                                                    if (i5 != 256) {
                                                        throw new IllegalArgumentException(C0405a.c("type needs to be >= FIRST and <= LAST, type=", i5));
                                                    }
                                                } else {
                                                    c4 = 7;
                                                }
                                            } else {
                                                c4 = 6;
                                            }
                                        } else {
                                            c4 = 5;
                                        }
                                    } else {
                                        c4 = 4;
                                    }
                                } else {
                                    c4 = 3;
                                }
                            }
                        } else {
                            c4 = 1;
                        }
                    } else {
                        c4 = 0;
                    }
                    bVarArr[c4] = bVar;
                }
            }
        }

        public void e(E.b bVar) {
            throw null;
        }

        public void g(E.b bVar) {
            throw null;
        }

        public e(e0 e0Var) {
            this.f1598a = e0Var;
        }

        public void d(E.b bVar) {
        }

        public void f(E.b bVar) {
        }

        public void h(E.b bVar) {
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class f extends k {

        /* renamed from: h  reason: collision with root package name */
        public static boolean f1600h;

        /* renamed from: i  reason: collision with root package name */
        public static Method f1601i;

        /* renamed from: j  reason: collision with root package name */
        public static Class<?> f1602j;

        /* renamed from: k  reason: collision with root package name */
        public static Field f1603k;

        /* renamed from: l  reason: collision with root package name */
        public static Field f1604l;

        /* renamed from: c  reason: collision with root package name */
        public final WindowInsets f1605c;

        /* renamed from: d  reason: collision with root package name */
        public E.b[] f1606d;

        /* renamed from: e  reason: collision with root package name */
        public E.b f1607e;
        public e0 f;

        /* renamed from: g  reason: collision with root package name */
        public E.b f1608g;

        public f(e0 e0Var, WindowInsets windowInsets) {
            super(e0Var);
            this.f1607e = null;
            this.f1605c = windowInsets;
        }

        @SuppressLint({"WrongConstant"})
        private E.b r(int i4, boolean z4) {
            E.b bVar = E.b.f802e;
            for (int i5 = 1; i5 <= 256; i5 <<= 1) {
                if ((i4 & i5) != 0) {
                    bVar = E.b.a(bVar, s(i5, z4));
                }
            }
            return bVar;
        }

        private E.b t() {
            e0 e0Var = this.f;
            if (e0Var != null) {
                return e0Var.f1587a.h();
            }
            return E.b.f802e;
        }

        private E.b u(View view) {
            if (Build.VERSION.SDK_INT < 30) {
                if (!f1600h) {
                    v();
                }
                Method method = f1601i;
                if (method != null && f1602j != null && f1603k != null) {
                    try {
                        Object invoke = method.invoke(view, null);
                        if (invoke == null) {
                            Log.w("WindowInsetsCompat", "Failed to get visible insets. getViewRootImpl() returned null from the provided view. This means that the view is either not attached or the method has been overridden", new NullPointerException());
                            return null;
                        }
                        Rect rect = (Rect) f1603k.get(f1604l.get(invoke));
                        if (rect == null) {
                            return null;
                        }
                        return E.b.b(rect.left, rect.top, rect.right, rect.bottom);
                    } catch (ReflectiveOperationException e4) {
                        Log.e("WindowInsetsCompat", "Failed to get visible insets. (Reflection error). " + e4.getMessage(), e4);
                    }
                }
                return null;
            }
            throw new UnsupportedOperationException("getVisibleInsets() should not be called on API >= 30. Use WindowInsets.isVisible() instead.");
        }

        @SuppressLint({"PrivateApi"})
        private static void v() {
            try {
                f1601i = View.class.getDeclaredMethod("getViewRootImpl", null);
                Class<?> cls = Class.forName("android.view.View$AttachInfo");
                f1602j = cls;
                f1603k = cls.getDeclaredField("mVisibleInsets");
                f1604l = Class.forName("android.view.ViewRootImpl").getDeclaredField("mAttachInfo");
                f1603k.setAccessible(true);
                f1604l.setAccessible(true);
            } catch (ReflectiveOperationException e4) {
                Log.e("WindowInsetsCompat", "Failed to get visible insets. (Reflection error). " + e4.getMessage(), e4);
            }
            f1600h = true;
        }

        @Override // M.e0.k
        public void d(View view) {
            E.b u4 = u(view);
            if (u4 == null) {
                u4 = E.b.f802e;
            }
            w(u4);
        }

        @Override // M.e0.k
        public boolean equals(Object obj) {
            if (!super.equals(obj)) {
                return false;
            }
            return Objects.equals(this.f1608g, ((f) obj).f1608g);
        }

        @Override // M.e0.k
        public E.b f(int i4) {
            return r(i4, false);
        }

        @Override // M.e0.k
        public final E.b j() {
            if (this.f1607e == null) {
                WindowInsets windowInsets = this.f1605c;
                this.f1607e = E.b.b(windowInsets.getSystemWindowInsetLeft(), windowInsets.getSystemWindowInsetTop(), windowInsets.getSystemWindowInsetRight(), windowInsets.getSystemWindowInsetBottom());
            }
            return this.f1607e;
        }

        @Override // M.e0.k
        public e0 l(int i4, int i5, int i6, int i7) {
            e bVar;
            e0 g4 = e0.g(null, this.f1605c);
            int i8 = Build.VERSION.SDK_INT;
            if (i8 >= 30) {
                bVar = new d(g4);
            } else if (i8 >= 29) {
                bVar = new c(g4);
            } else {
                bVar = new b(g4);
            }
            bVar.g(e0.e(j(), i4, i5, i6, i7));
            bVar.e(e0.e(h(), i4, i5, i6, i7));
            return bVar.b();
        }

        @Override // M.e0.k
        public boolean n() {
            return this.f1605c.isRound();
        }

        @Override // M.e0.k
        public void o(E.b[] bVarArr) {
            this.f1606d = bVarArr;
        }

        @Override // M.e0.k
        public void p(e0 e0Var) {
            this.f = e0Var;
        }

        public E.b s(int i4, boolean z4) {
            int i5;
            C0231m e4;
            int i6;
            int i7;
            int i8;
            int i9 = 0;
            if (i4 != 1) {
                E.b bVar = null;
                if (i4 != 2) {
                    E.b bVar2 = E.b.f802e;
                    if (i4 != 8) {
                        if (i4 != 16) {
                            if (i4 != 32) {
                                if (i4 != 64) {
                                    if (i4 != 128) {
                                        return bVar2;
                                    }
                                    e0 e0Var = this.f;
                                    if (e0Var != null) {
                                        e4 = e0Var.f1587a.e();
                                    } else {
                                        e4 = e();
                                    }
                                    if (e4 != null) {
                                        int i10 = Build.VERSION.SDK_INT;
                                        if (i10 >= 28) {
                                            i6 = C0231m.a.d(e4.f1644a);
                                        } else {
                                            i6 = 0;
                                        }
                                        if (i10 >= 28) {
                                            i7 = C0231m.a.f(e4.f1644a);
                                        } else {
                                            i7 = 0;
                                        }
                                        if (i10 >= 28) {
                                            i8 = C0231m.a.e(e4.f1644a);
                                        } else {
                                            i8 = 0;
                                        }
                                        if (i10 >= 28) {
                                            i9 = C0231m.a.c(e4.f1644a);
                                        }
                                        return E.b.b(i6, i7, i8, i9);
                                    }
                                    return bVar2;
                                }
                                return k();
                            }
                            return g();
                        }
                        return i();
                    }
                    E.b[] bVarArr = this.f1606d;
                    if (bVarArr != null) {
                        bVar = bVarArr[3];
                    }
                    if (bVar != null) {
                        return bVar;
                    }
                    E.b j4 = j();
                    E.b t3 = t();
                    int i11 = j4.f806d;
                    if (i11 > t3.f806d) {
                        return E.b.b(0, 0, 0, i11);
                    }
                    E.b bVar3 = this.f1608g;
                    if (bVar3 != null && !bVar3.equals(bVar2) && (i5 = this.f1608g.f806d) > t3.f806d) {
                        return E.b.b(0, 0, 0, i5);
                    }
                    return bVar2;
                } else if (z4) {
                    E.b t4 = t();
                    E.b h4 = h();
                    return E.b.b(Math.max(t4.f803a, h4.f803a), 0, Math.max(t4.f805c, h4.f805c), Math.max(t4.f806d, h4.f806d));
                } else {
                    E.b j5 = j();
                    e0 e0Var2 = this.f;
                    if (e0Var2 != null) {
                        bVar = e0Var2.f1587a.h();
                    }
                    int i12 = j5.f806d;
                    if (bVar != null) {
                        i12 = Math.min(i12, bVar.f806d);
                    }
                    return E.b.b(j5.f803a, 0, j5.f805c, i12);
                }
            } else if (z4) {
                return E.b.b(0, Math.max(t().f804b, j().f804b), 0, 0);
            } else {
                return E.b.b(0, j().f804b, 0, 0);
            }
        }

        public void w(E.b bVar) {
            this.f1608g = bVar;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public static class g extends f {

        /* renamed from: m  reason: collision with root package name */
        public E.b f1609m;

        public g(e0 e0Var, WindowInsets windowInsets) {
            super(e0Var, windowInsets);
            this.f1609m = null;
        }

        @Override // M.e0.k
        public e0 b() {
            return e0.g(null, this.f1605c.consumeStableInsets());
        }

        @Override // M.e0.k
        public e0 c() {
            return e0.g(null, this.f1605c.consumeSystemWindowInsets());
        }

        @Override // M.e0.k
        public final E.b h() {
            if (this.f1609m == null) {
                WindowInsets windowInsets = this.f1605c;
                this.f1609m = E.b.b(windowInsets.getStableInsetLeft(), windowInsets.getStableInsetTop(), windowInsets.getStableInsetRight(), windowInsets.getStableInsetBottom());
            }
            return this.f1609m;
        }

        @Override // M.e0.k
        public boolean m() {
            return this.f1605c.isConsumed();
        }

        @Override // M.e0.k
        public void q(E.b bVar) {
            this.f1609m = bVar;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public static class h extends g {
        public h(e0 e0Var, WindowInsets windowInsets) {
            super(e0Var, windowInsets);
        }

        @Override // M.e0.k
        public e0 a() {
            WindowInsets consumeDisplayCutout;
            consumeDisplayCutout = this.f1605c.consumeDisplayCutout();
            return e0.g(null, consumeDisplayCutout);
        }

        @Override // M.e0.k
        public C0231m e() {
            DisplayCutout displayCutout;
            displayCutout = this.f1605c.getDisplayCutout();
            if (displayCutout == null) {
                return null;
            }
            return new C0231m(displayCutout);
        }

        @Override // M.e0.f, M.e0.k
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof h)) {
                return false;
            }
            h hVar = (h) obj;
            if (Objects.equals(this.f1605c, hVar.f1605c) && Objects.equals(this.f1608g, hVar.f1608g)) {
                return true;
            }
            return false;
        }

        @Override // M.e0.k
        public int hashCode() {
            return this.f1605c.hashCode();
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static final class l {
        public static int a(int i4) {
            int statusBars;
            int i5 = 0;
            for (int i6 = 1; i6 <= 256; i6 <<= 1) {
                if ((i4 & i6) != 0) {
                    if (i6 == 1) {
                        statusBars = WindowInsets.Type.statusBars();
                    } else if (i6 == 2) {
                        statusBars = WindowInsets.Type.navigationBars();
                    } else if (i6 == 4) {
                        statusBars = WindowInsets.Type.captionBar();
                    } else if (i6 == 8) {
                        statusBars = WindowInsets.Type.ime();
                    } else if (i6 == 16) {
                        statusBars = WindowInsets.Type.systemGestures();
                    } else if (i6 == 32) {
                        statusBars = WindowInsets.Type.mandatorySystemGestures();
                    } else if (i6 != 64) {
                        if (i6 == 128) {
                            statusBars = WindowInsets.Type.displayCutout();
                        }
                    } else {
                        statusBars = H2.w.a();
                    }
                    i5 |= statusBars;
                }
            }
            return i5;
        }
    }

    static {
        if (Build.VERSION.SDK_INT >= 30) {
            f1586b = j.f1613q;
        } else {
            f1586b = k.f1614b;
        }
    }

    public e0(WindowInsets windowInsets) {
        int i4 = Build.VERSION.SDK_INT;
        if (i4 >= 30) {
            this.f1587a = new j(this, windowInsets);
        } else if (i4 >= 29) {
            this.f1587a = new i(this, windowInsets);
        } else if (i4 >= 28) {
            this.f1587a = new h(this, windowInsets);
        } else {
            this.f1587a = new g(this, windowInsets);
        }
    }

    public static E.b e(E.b bVar, int i4, int i5, int i6, int i7) {
        int max = Math.max(0, bVar.f803a - i4);
        int max2 = Math.max(0, bVar.f804b - i5);
        int max3 = Math.max(0, bVar.f805c - i6);
        int max4 = Math.max(0, bVar.f806d - i7);
        if (max == i4 && max2 == i5 && max3 == i6 && max4 == i7) {
            return bVar;
        }
        return E.b.b(max, max2, max3, max4);
    }

    public static e0 g(View view, WindowInsets windowInsets) {
        windowInsets.getClass();
        e0 e0Var = new e0(windowInsets);
        if (view != null && view.isAttachedToWindow()) {
            e0 h4 = O.h(view);
            k kVar = e0Var.f1587a;
            kVar.p(h4);
            kVar.d(view.getRootView());
        }
        return e0Var;
    }

    @Deprecated
    public final int a() {
        return this.f1587a.j().f806d;
    }

    @Deprecated
    public final int b() {
        return this.f1587a.j().f803a;
    }

    @Deprecated
    public final int c() {
        return this.f1587a.j().f805c;
    }

    @Deprecated
    public final int d() {
        return this.f1587a.j().f804b;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof e0)) {
            return false;
        }
        return Objects.equals(this.f1587a, ((e0) obj).f1587a);
    }

    public final WindowInsets f() {
        k kVar = this.f1587a;
        if (kVar instanceof f) {
            return ((f) kVar).f1605c;
        }
        return null;
    }

    public final int hashCode() {
        k kVar = this.f1587a;
        if (kVar == null) {
            return 0;
        }
        return kVar.hashCode();
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class b extends e {

        /* renamed from: e  reason: collision with root package name */
        public static Field f1592e;
        public static boolean f;

        /* renamed from: g  reason: collision with root package name */
        public static Constructor<WindowInsets> f1593g;

        /* renamed from: h  reason: collision with root package name */
        public static boolean f1594h;

        /* renamed from: c  reason: collision with root package name */
        public WindowInsets f1595c;

        /* renamed from: d  reason: collision with root package name */
        public E.b f1596d;

        public b() {
            this.f1595c = i();
        }

        private static WindowInsets i() {
            if (!f) {
                try {
                    f1592e = WindowInsets.class.getDeclaredField("CONSUMED");
                } catch (ReflectiveOperationException e4) {
                    Log.i("WindowInsetsCompat", "Could not retrieve WindowInsets.CONSUMED field", e4);
                }
                f = true;
            }
            Field field = f1592e;
            if (field != null) {
                try {
                    WindowInsets windowInsets = (WindowInsets) field.get(null);
                    if (windowInsets != null) {
                        return new WindowInsets(windowInsets);
                    }
                } catch (ReflectiveOperationException e5) {
                    Log.i("WindowInsetsCompat", "Could not get value from WindowInsets.CONSUMED field", e5);
                }
            }
            if (!f1594h) {
                try {
                    f1593g = WindowInsets.class.getConstructor(Rect.class);
                } catch (ReflectiveOperationException e6) {
                    Log.i("WindowInsetsCompat", "Could not retrieve WindowInsets(Rect) constructor", e6);
                }
                f1594h = true;
            }
            Constructor<WindowInsets> constructor = f1593g;
            if (constructor != null) {
                try {
                    return constructor.newInstance(new Rect());
                } catch (ReflectiveOperationException e7) {
                    Log.i("WindowInsetsCompat", "Could not invoke WindowInsets(Rect) constructor", e7);
                }
            }
            return null;
        }

        @Override // M.e0.e
        public e0 b() {
            a();
            e0 g4 = e0.g(null, this.f1595c);
            E.b[] bVarArr = this.f1599b;
            k kVar = g4.f1587a;
            kVar.o(bVarArr);
            kVar.q(this.f1596d);
            return g4;
        }

        @Override // M.e0.e
        public void e(E.b bVar) {
            this.f1596d = bVar;
        }

        @Override // M.e0.e
        public void g(E.b bVar) {
            WindowInsets windowInsets = this.f1595c;
            if (windowInsets != null) {
                this.f1595c = windowInsets.replaceSystemWindowInsets(bVar.f803a, bVar.f804b, bVar.f805c, bVar.f806d);
            }
        }

        public b(e0 e0Var) {
            super(e0Var);
            this.f1595c = e0Var.f();
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class c extends e {

        /* renamed from: c  reason: collision with root package name */
        public final WindowInsets.Builder f1597c;

        public c() {
            this.f1597c = i0.c();
        }

        @Override // M.e0.e
        public e0 b() {
            WindowInsets build;
            a();
            build = this.f1597c.build();
            e0 g4 = e0.g(null, build);
            g4.f1587a.o(this.f1599b);
            return g4;
        }

        @Override // M.e0.e
        public void d(E.b bVar) {
            this.f1597c.setMandatorySystemGestureInsets(bVar.d());
        }

        @Override // M.e0.e
        public void e(E.b bVar) {
            this.f1597c.setStableInsets(bVar.d());
        }

        @Override // M.e0.e
        public void f(E.b bVar) {
            this.f1597c.setSystemGestureInsets(bVar.d());
        }

        @Override // M.e0.e
        public void g(E.b bVar) {
            this.f1597c.setSystemWindowInsets(bVar.d());
        }

        @Override // M.e0.e
        public void h(E.b bVar) {
            this.f1597c.setTappableElementInsets(bVar.d());
        }

        public c(e0 e0Var) {
            super(e0Var);
            WindowInsets.Builder c4;
            WindowInsets f = e0Var.f();
            if (f != null) {
                c4 = j0.b(f);
            } else {
                c4 = i0.c();
            }
            this.f1597c = c4;
        }
    }

    public e0() {
        this.f1587a = new k(this);
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public static class i extends h {

        /* renamed from: n  reason: collision with root package name */
        public E.b f1610n;

        /* renamed from: o  reason: collision with root package name */
        public E.b f1611o;

        /* renamed from: p  reason: collision with root package name */
        public E.b f1612p;

        public i(e0 e0Var, WindowInsets windowInsets) {
            super(e0Var, windowInsets);
            this.f1610n = null;
            this.f1611o = null;
            this.f1612p = null;
        }

        @Override // M.e0.k
        public E.b g() {
            Insets mandatorySystemGestureInsets;
            if (this.f1611o == null) {
                mandatorySystemGestureInsets = this.f1605c.getMandatorySystemGestureInsets();
                this.f1611o = E.b.c(mandatorySystemGestureInsets);
            }
            return this.f1611o;
        }

        @Override // M.e0.k
        public E.b i() {
            Insets systemGestureInsets;
            if (this.f1610n == null) {
                systemGestureInsets = this.f1605c.getSystemGestureInsets();
                this.f1610n = E.b.c(systemGestureInsets);
            }
            return this.f1610n;
        }

        @Override // M.e0.k
        public E.b k() {
            Insets tappableElementInsets;
            if (this.f1612p == null) {
                tappableElementInsets = this.f1605c.getTappableElementInsets();
                this.f1612p = E.b.c(tappableElementInsets);
            }
            return this.f1612p;
        }

        @Override // M.e0.f, M.e0.k
        public e0 l(int i4, int i5, int i6, int i7) {
            WindowInsets inset;
            inset = this.f1605c.inset(i4, i5, i6, i7);
            return e0.g(null, inset);
        }

        @Override // M.e0.g, M.e0.k
        public void q(E.b bVar) {
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public static class j extends i {

        /* renamed from: q  reason: collision with root package name */
        public static final e0 f1613q;

        static {
            WindowInsets windowInsets;
            windowInsets = WindowInsets.CONSUMED;
            f1613q = e0.g(null, windowInsets);
        }

        public j(e0 e0Var, WindowInsets windowInsets) {
            super(e0Var, windowInsets);
        }

        @Override // M.e0.f, M.e0.k
        public E.b f(int i4) {
            Insets insets;
            insets = this.f1605c.getInsets(l.a(i4));
            return E.b.c(insets);
        }

        @Override // M.e0.f, M.e0.k
        public final void d(View view) {
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class k {

        /* renamed from: b  reason: collision with root package name */
        public static final e0 f1614b;

        /* renamed from: a  reason: collision with root package name */
        public final e0 f1615a;

        static {
            e bVar;
            int i4 = Build.VERSION.SDK_INT;
            if (i4 >= 30) {
                bVar = new d();
            } else if (i4 >= 29) {
                bVar = new c();
            } else {
                bVar = new b();
            }
            f1614b = bVar.b().f1587a.a().f1587a.b().f1587a.c();
        }

        public k(e0 e0Var) {
            this.f1615a = e0Var;
        }

        public e0 a() {
            return this.f1615a;
        }

        public e0 b() {
            return this.f1615a;
        }

        public e0 c() {
            return this.f1615a;
        }

        public C0231m e() {
            return null;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof k)) {
                return false;
            }
            k kVar = (k) obj;
            if (n() == kVar.n() && m() == kVar.m() && Objects.equals(j(), kVar.j()) && Objects.equals(h(), kVar.h()) && Objects.equals(e(), kVar.e())) {
                return true;
            }
            return false;
        }

        public E.b f(int i4) {
            return E.b.f802e;
        }

        public E.b g() {
            return j();
        }

        public E.b h() {
            return E.b.f802e;
        }

        public int hashCode() {
            return Objects.hash(Boolean.valueOf(n()), Boolean.valueOf(m()), j(), h(), e());
        }

        public E.b i() {
            return j();
        }

        public E.b j() {
            return E.b.f802e;
        }

        public E.b k() {
            return j();
        }

        public e0 l(int i4, int i5, int i6, int i7) {
            return f1614b;
        }

        public boolean m() {
            return false;
        }

        public boolean n() {
            return false;
        }

        public void d(View view) {
        }

        public void o(E.b[] bVarArr) {
        }

        public void p(e0 e0Var) {
        }

        public void q(E.b bVar) {
        }
    }
}
