package M;

import M.e0;
import a0.C0339a;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Insets;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowInsetsAnimation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import j$.util.Objects;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class Z {

    /* renamed from: a  reason: collision with root package name */
    public e f1552a;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static final class a {

        /* renamed from: a  reason: collision with root package name */
        public final E.b f1553a;

        /* renamed from: b  reason: collision with root package name */
        public final E.b f1554b;

        public a(E.b bVar, E.b bVar2) {
            this.f1553a = bVar;
            this.f1554b = bVar2;
        }

        public final String toString() {
            return "Bounds{lower=" + this.f1553a + " upper=" + this.f1554b + "}";
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static abstract class b {

        /* renamed from: a  reason: collision with root package name */
        public WindowInsets f1555a;

        /* renamed from: b  reason: collision with root package name */
        public final int f1556b = 0;

        public abstract e0 a(e0 e0Var, List<Z> list);
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class c extends e {

        /* renamed from: e  reason: collision with root package name */
        public static final PathInterpolator f1557e = new PathInterpolator(0.0f, 1.1f, 0.0f, 1.0f);
        public static final C0339a f = new C0339a();

        /* renamed from: g  reason: collision with root package name */
        public static final DecelerateInterpolator f1558g = new DecelerateInterpolator();

        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
        public static class a implements View.OnApplyWindowInsetsListener {

            /* renamed from: a  reason: collision with root package name */
            public final b f1559a;

            /* renamed from: b  reason: collision with root package name */
            public e0 f1560b;

            /* renamed from: M.Z$c$a$a  reason: collision with other inner class name */
            /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
            public class C0013a implements ValueAnimator.AnimatorUpdateListener {

                /* renamed from: a  reason: collision with root package name */
                public final /* synthetic */ Z f1561a;

                /* renamed from: b  reason: collision with root package name */
                public final /* synthetic */ e0 f1562b;

                /* renamed from: c  reason: collision with root package name */
                public final /* synthetic */ e0 f1563c;

                /* renamed from: d  reason: collision with root package name */
                public final /* synthetic */ int f1564d;

                /* renamed from: e  reason: collision with root package name */
                public final /* synthetic */ View f1565e;

                public C0013a(Z z4, e0 e0Var, e0 e0Var2, int i4, View view) {
                    this.f1561a = z4;
                    this.f1562b = e0Var;
                    this.f1563c = e0Var2;
                    this.f1564d = i4;
                    this.f1565e = view;
                }

                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    e0.e bVar;
                    float f;
                    Z z4;
                    float animatedFraction = valueAnimator.getAnimatedFraction();
                    Z z5 = this.f1561a;
                    z5.f1552a.d(animatedFraction);
                    float b4 = z5.f1552a.b();
                    PathInterpolator pathInterpolator = c.f1557e;
                    int i4 = Build.VERSION.SDK_INT;
                    e0 e0Var = this.f1562b;
                    if (i4 >= 30) {
                        bVar = new e0.d(e0Var);
                    } else if (i4 >= 29) {
                        bVar = new e0.c(e0Var);
                    } else {
                        bVar = new e0.b(e0Var);
                    }
                    int i5 = 1;
                    while (i5 <= 256) {
                        if ((this.f1564d & i5) == 0) {
                            bVar.c(i5, e0Var.f1587a.f(i5));
                            f = b4;
                            z4 = z5;
                        } else {
                            E.b f4 = e0Var.f1587a.f(i5);
                            E.b f5 = this.f1563c.f1587a.f(i5);
                            float f6 = 1.0f - b4;
                            f = b4;
                            z4 = z5;
                            bVar.c(i5, e0.e(f4, (int) (((f4.f803a - f5.f803a) * f6) + 0.5d), (int) (((f4.f804b - f5.f804b) * f6) + 0.5d), (int) (((f4.f805c - f5.f805c) * f6) + 0.5d), (int) (((f4.f806d - f5.f806d) * f6) + 0.5d)));
                        }
                        i5 <<= 1;
                        b4 = f;
                        z5 = z4;
                    }
                    c.g(this.f1565e, bVar.b(), Collections.singletonList(z5));
                }
            }

            /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
            public class b extends AnimatorListenerAdapter {

                /* renamed from: a  reason: collision with root package name */
                public final /* synthetic */ Z f1566a;

                /* renamed from: b  reason: collision with root package name */
                public final /* synthetic */ View f1567b;

                public b(Z z4, View view) {
                    this.f1566a = z4;
                    this.f1567b = view;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public final void onAnimationEnd(Animator animator) {
                    Z z4 = this.f1566a;
                    z4.f1552a.d(1.0f);
                    c.e(z4, this.f1567b);
                }
            }

            /* renamed from: M.Z$c$a$c  reason: collision with other inner class name */
            /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
            public class RunnableC0014c implements Runnable {

                /* renamed from: j  reason: collision with root package name */
                public final /* synthetic */ View f1568j;

                /* renamed from: k  reason: collision with root package name */
                public final /* synthetic */ Z f1569k;

                /* renamed from: l  reason: collision with root package name */
                public final /* synthetic */ a f1570l;

                /* renamed from: m  reason: collision with root package name */
                public final /* synthetic */ ValueAnimator f1571m;

                public RunnableC0014c(View view, Z z4, a aVar, ValueAnimator valueAnimator) {
                    this.f1568j = view;
                    this.f1569k = z4;
                    this.f1570l = aVar;
                    this.f1571m = valueAnimator;
                }

                @Override // java.lang.Runnable
                public final void run() {
                    c.h(this.f1568j, this.f1569k, this.f1570l);
                    this.f1571m.start();
                }
            }

            public a(View view, w2.d dVar) {
                e0 e0Var;
                e0.e bVar;
                this.f1559a = dVar;
                e0 h4 = O.h(view);
                if (h4 != null) {
                    int i4 = Build.VERSION.SDK_INT;
                    if (i4 >= 30) {
                        bVar = new e0.d(h4);
                    } else if (i4 >= 29) {
                        bVar = new e0.c(h4);
                    } else {
                        bVar = new e0.b(h4);
                    }
                    e0Var = bVar.b();
                } else {
                    e0Var = null;
                }
                this.f1560b = e0Var;
            }

            @Override // android.view.View.OnApplyWindowInsetsListener
            public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                e0.k kVar;
                Interpolator interpolator;
                if (!view.isLaidOut()) {
                    this.f1560b = e0.g(view, windowInsets);
                    return c.i(view, windowInsets);
                }
                e0 g4 = e0.g(view, windowInsets);
                if (this.f1560b == null) {
                    this.f1560b = O.h(view);
                }
                if (this.f1560b == null) {
                    this.f1560b = g4;
                    return c.i(view, windowInsets);
                }
                b j4 = c.j(view);
                if (j4 != null && Objects.equals(j4.f1555a, windowInsets)) {
                    return c.i(view, windowInsets);
                }
                e0 e0Var = this.f1560b;
                int i4 = 0;
                int i5 = 1;
                while (true) {
                    kVar = g4.f1587a;
                    if (i5 > 256) {
                        break;
                    }
                    if (!kVar.f(i5).equals(e0Var.f1587a.f(i5))) {
                        i4 |= i5;
                    }
                    i5 <<= 1;
                }
                if (i4 == 0) {
                    return c.i(view, windowInsets);
                }
                e0 e0Var2 = this.f1560b;
                if ((i4 & 8) != 0) {
                    if (kVar.f(8).f806d > e0Var2.f1587a.f(8).f806d) {
                        interpolator = c.f1557e;
                    } else {
                        interpolator = c.f;
                    }
                } else {
                    interpolator = c.f1558g;
                }
                Z z4 = new Z(i4, interpolator, 160L);
                z4.f1552a.d(0.0f);
                ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(z4.f1552a.a());
                E.b f = kVar.f(i4);
                E.b f4 = e0Var2.f1587a.f(i4);
                int min = Math.min(f.f803a, f4.f803a);
                int i6 = f.f804b;
                int i7 = f4.f804b;
                int min2 = Math.min(i6, i7);
                int i8 = f.f805c;
                int i9 = f4.f805c;
                int min3 = Math.min(i8, i9);
                int i10 = f.f806d;
                int i11 = i4;
                int i12 = f4.f806d;
                a aVar = new a(E.b.b(min, min2, min3, Math.min(i10, i12)), E.b.b(Math.max(f.f803a, f4.f803a), Math.max(i6, i7), Math.max(i8, i9), Math.max(i10, i12)));
                c.f(view, z4, windowInsets, false);
                duration.addUpdateListener(new C0013a(z4, g4, e0Var2, i11, view));
                duration.addListener(new b(z4, view));
                A.a(view, new RunnableC0014c(view, z4, aVar, duration));
                this.f1560b = g4;
                return c.i(view, windowInsets);
            }
        }

        public static void e(Z z4, View view) {
            b j4 = j(view);
            if (j4 != null) {
                ((w2.d) j4).f6409c.setTranslationY(0.0f);
                if (j4.f1556b == 0) {
                    return;
                }
            }
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i4 = 0; i4 < viewGroup.getChildCount(); i4++) {
                    e(z4, viewGroup.getChildAt(i4));
                }
            }
        }

        public static void f(View view, Z z4, WindowInsets windowInsets, boolean z5) {
            b j4 = j(view);
            if (j4 != null) {
                j4.f1555a = windowInsets;
                if (!z5) {
                    w2.d dVar = (w2.d) j4;
                    View view2 = dVar.f6409c;
                    int[] iArr = dVar.f;
                    view2.getLocationOnScreen(iArr);
                    dVar.f6410d = iArr[1];
                    if (j4.f1556b == 0) {
                        z5 = true;
                    } else {
                        z5 = false;
                    }
                }
            }
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i4 = 0; i4 < viewGroup.getChildCount(); i4++) {
                    f(viewGroup.getChildAt(i4), z4, windowInsets, z5);
                }
            }
        }

        public static void g(View view, e0 e0Var, List<Z> list) {
            b j4 = j(view);
            if (j4 != null) {
                j4.a(e0Var, list);
                if (j4.f1556b == 0) {
                    return;
                }
            }
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i4 = 0; i4 < viewGroup.getChildCount(); i4++) {
                    g(viewGroup.getChildAt(i4), e0Var, list);
                }
            }
        }

        public static void h(View view, Z z4, a aVar) {
            b j4 = j(view);
            if (j4 != null) {
                w2.d dVar = (w2.d) j4;
                View view2 = dVar.f6409c;
                int[] iArr = dVar.f;
                view2.getLocationOnScreen(iArr);
                int i4 = dVar.f6410d - iArr[1];
                dVar.f6411e = i4;
                view2.setTranslationY(i4);
                if (j4.f1556b == 0) {
                    return;
                }
            }
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i5 = 0; i5 < viewGroup.getChildCount(); i5++) {
                    h(viewGroup.getChildAt(i5), z4, aVar);
                }
            }
        }

        public static WindowInsets i(View view, WindowInsets windowInsets) {
            if (view.getTag(2131231248) != null) {
                return windowInsets;
            }
            return view.onApplyWindowInsets(windowInsets);
        }

        public static b j(View view) {
            Object tag = view.getTag(2131231256);
            if (tag instanceof a) {
                return ((a) tag).f1559a;
            }
            return null;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class d extends e {

        /* renamed from: e  reason: collision with root package name */
        public final WindowInsetsAnimation f1572e;

        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
        public static class a extends WindowInsetsAnimation.Callback {

            /* renamed from: a  reason: collision with root package name */
            public final b f1573a;

            /* renamed from: b  reason: collision with root package name */
            public List<Z> f1574b;

            /* renamed from: c  reason: collision with root package name */
            public ArrayList<Z> f1575c;

            /* renamed from: d  reason: collision with root package name */
            public final HashMap<WindowInsetsAnimation, Z> f1576d;

            public a(w2.d dVar) {
                super(dVar.f1556b);
                this.f1576d = new HashMap<>();
                this.f1573a = dVar;
            }

            public final Z a(WindowInsetsAnimation windowInsetsAnimation) {
                Z z4 = this.f1576d.get(windowInsetsAnimation);
                if (z4 == null) {
                    z4 = new Z(0, null, 0L);
                    if (Build.VERSION.SDK_INT >= 30) {
                        z4.f1552a = new d(windowInsetsAnimation);
                    }
                    this.f1576d.put(windowInsetsAnimation, z4);
                }
                return z4;
            }

            @Override // android.view.WindowInsetsAnimation.Callback
            public final void onEnd(WindowInsetsAnimation windowInsetsAnimation) {
                b bVar = this.f1573a;
                a(windowInsetsAnimation);
                ((w2.d) bVar).f6409c.setTranslationY(0.0f);
                this.f1576d.remove(windowInsetsAnimation);
            }

            @Override // android.view.WindowInsetsAnimation.Callback
            public final void onPrepare(WindowInsetsAnimation windowInsetsAnimation) {
                b bVar = this.f1573a;
                a(windowInsetsAnimation);
                w2.d dVar = (w2.d) bVar;
                View view = dVar.f6409c;
                int[] iArr = dVar.f;
                view.getLocationOnScreen(iArr);
                dVar.f6410d = iArr[1];
            }

            @Override // android.view.WindowInsetsAnimation.Callback
            public final WindowInsets onProgress(WindowInsets windowInsets, List<WindowInsetsAnimation> list) {
                float fraction;
                ArrayList<Z> arrayList = this.f1575c;
                if (arrayList == null) {
                    ArrayList<Z> arrayList2 = new ArrayList<>(list.size());
                    this.f1575c = arrayList2;
                    this.f1574b = Collections.unmodifiableList(arrayList2);
                } else {
                    arrayList.clear();
                }
                for (int size = list.size() - 1; size >= 0; size--) {
                    WindowInsetsAnimation c4 = a0.c(list.get(size));
                    Z a4 = a(c4);
                    fraction = c4.getFraction();
                    a4.f1552a.d(fraction);
                    this.f1575c.add(a4);
                }
                b bVar = this.f1573a;
                e0 g4 = e0.g(null, windowInsets);
                bVar.a(g4, this.f1574b);
                return g4.f();
            }

            @Override // android.view.WindowInsetsAnimation.Callback
            public final WindowInsetsAnimation.Bounds onStart(WindowInsetsAnimation windowInsetsAnimation, WindowInsetsAnimation.Bounds bounds) {
                Insets lowerBound;
                Insets upperBound;
                b bVar = this.f1573a;
                a(windowInsetsAnimation);
                lowerBound = bounds.getLowerBound();
                E.b c4 = E.b.c(lowerBound);
                upperBound = bounds.getUpperBound();
                E.b c5 = E.b.c(upperBound);
                w2.d dVar = (w2.d) bVar;
                View view = dVar.f6409c;
                int[] iArr = dVar.f;
                view.getLocationOnScreen(iArr);
                int i4 = dVar.f6410d - iArr[1];
                dVar.f6411e = i4;
                view.setTranslationY(i4);
                c0.c();
                return b0.c(c4.d(), c5.d());
            }
        }

        public d(WindowInsetsAnimation windowInsetsAnimation) {
            super(0, null, 0L);
            this.f1572e = windowInsetsAnimation;
        }

        @Override // M.Z.e
        public final long a() {
            long durationMillis;
            durationMillis = this.f1572e.getDurationMillis();
            return durationMillis;
        }

        @Override // M.Z.e
        public final float b() {
            float interpolatedFraction;
            interpolatedFraction = this.f1572e.getInterpolatedFraction();
            return interpolatedFraction;
        }

        @Override // M.Z.e
        public final int c() {
            int typeMask;
            typeMask = this.f1572e.getTypeMask();
            return typeMask;
        }

        @Override // M.Z.e
        public final void d(float f) {
            this.f1572e.setFraction(f);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class e {

        /* renamed from: a  reason: collision with root package name */
        public final int f1577a;

        /* renamed from: b  reason: collision with root package name */
        public float f1578b;

        /* renamed from: c  reason: collision with root package name */
        public final Interpolator f1579c;

        /* renamed from: d  reason: collision with root package name */
        public final long f1580d;

        public e(int i4, Interpolator interpolator, long j4) {
            this.f1577a = i4;
            this.f1579c = interpolator;
            this.f1580d = j4;
        }

        public long a() {
            return this.f1580d;
        }

        public float b() {
            Interpolator interpolator = this.f1579c;
            if (interpolator != null) {
                return interpolator.getInterpolation(this.f1578b);
            }
            return this.f1578b;
        }

        public int c() {
            return this.f1577a;
        }

        public void d(float f) {
            this.f1578b = f;
        }
    }

    public Z(int i4, Interpolator interpolator, long j4) {
        if (Build.VERSION.SDK_INT >= 30) {
            this.f1552a = new d(H2.x.d(i4, interpolator, j4));
        } else {
            this.f1552a = new e(i4, interpolator, j4);
        }
    }
}
