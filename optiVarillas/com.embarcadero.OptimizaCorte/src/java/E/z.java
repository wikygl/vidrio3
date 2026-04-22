package e;

import M.O;
import M.V;
import M.X;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import androidx.appcompat.view.menu.f;
import androidx.appcompat.widget.ActionBarContainer;
import androidx.appcompat.widget.ActionBarContextView;
import androidx.appcompat.widget.ActionBarOverlayLayout;
import androidx.appcompat.widget.Toolbar;
import d.C0376a;
import e.AbstractC0392a;
import e.LayoutInflater$Factory2C0401j;
import j.AbstractC0647a;
import j.C0652f;
import j.C0653g;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.WeakHashMap;
import l.F;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class z extends AbstractC0392a implements ActionBarOverlayLayout.d {

    /* renamed from: y  reason: collision with root package name */
    public static final AccelerateInterpolator f3320y = new AccelerateInterpolator();

    /* renamed from: z  reason: collision with root package name */
    public static final DecelerateInterpolator f3321z = new DecelerateInterpolator();

    /* renamed from: a  reason: collision with root package name */
    public Context f3322a;

    /* renamed from: b  reason: collision with root package name */
    public Context f3323b;

    /* renamed from: c  reason: collision with root package name */
    public ActionBarOverlayLayout f3324c;

    /* renamed from: d  reason: collision with root package name */
    public ActionBarContainer f3325d;

    /* renamed from: e  reason: collision with root package name */
    public F f3326e;
    public ActionBarContextView f;

    /* renamed from: g  reason: collision with root package name */
    public final View f3327g;

    /* renamed from: h  reason: collision with root package name */
    public boolean f3328h;

    /* renamed from: i  reason: collision with root package name */
    public d f3329i;

    /* renamed from: j  reason: collision with root package name */
    public d f3330j;

    /* renamed from: k  reason: collision with root package name */
    public AbstractC0647a.InterfaceC0054a f3331k;

    /* renamed from: l  reason: collision with root package name */
    public boolean f3332l;

    /* renamed from: m  reason: collision with root package name */
    public final ArrayList<AbstractC0392a.b> f3333m;

    /* renamed from: n  reason: collision with root package name */
    public int f3334n;

    /* renamed from: o  reason: collision with root package name */
    public boolean f3335o;

    /* renamed from: p  reason: collision with root package name */
    public boolean f3336p;

    /* renamed from: q  reason: collision with root package name */
    public boolean f3337q;

    /* renamed from: r  reason: collision with root package name */
    public boolean f3338r;

    /* renamed from: s  reason: collision with root package name */
    public C0653g f3339s;

    /* renamed from: t  reason: collision with root package name */
    public boolean f3340t;

    /* renamed from: u  reason: collision with root package name */
    public boolean f3341u;

    /* renamed from: v  reason: collision with root package name */
    public final a f3342v;

    /* renamed from: w  reason: collision with root package name */
    public final b f3343w;

    /* renamed from: x  reason: collision with root package name */
    public final c f3344x;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public class a extends H.a {
        public a() {
        }

        @Override // M.W
        public final void b() {
            View view;
            z zVar = z.this;
            if (zVar.f3335o && (view = zVar.f3327g) != null) {
                view.setTranslationY(0.0f);
                zVar.f3325d.setTranslationY(0.0f);
            }
            zVar.f3325d.setVisibility(8);
            zVar.f3325d.setTransitioning(false);
            zVar.f3339s = null;
            AbstractC0647a.InterfaceC0054a interfaceC0054a = zVar.f3331k;
            if (interfaceC0054a != null) {
                interfaceC0054a.c(zVar.f3330j);
                zVar.f3330j = null;
                zVar.f3331k = null;
            }
            ActionBarOverlayLayout actionBarOverlayLayout = zVar.f3324c;
            if (actionBarOverlayLayout != null) {
                WeakHashMap<View, V> weakHashMap = O.f1526a;
                O.c.c(actionBarOverlayLayout);
            }
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public class b extends H.a {
        public b() {
        }

        @Override // M.W
        public final void b() {
            z zVar = z.this;
            zVar.f3339s = null;
            zVar.f3325d.requestLayout();
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class c implements X {
        public c() {
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class d extends AbstractC0647a implements f.a {

        /* renamed from: l  reason: collision with root package name */
        public final Context f3348l;

        /* renamed from: m  reason: collision with root package name */
        public final androidx.appcompat.view.menu.f f3349m;

        /* renamed from: n  reason: collision with root package name */
        public AbstractC0647a.InterfaceC0054a f3350n;

        /* renamed from: o  reason: collision with root package name */
        public WeakReference<View> f3351o;

        public d(Context context, LayoutInflater$Factory2C0401j.c cVar) {
            this.f3348l = context;
            this.f3350n = cVar;
            androidx.appcompat.view.menu.f fVar = new androidx.appcompat.view.menu.f(context);
            fVar.l = 1;
            this.f3349m = fVar;
            fVar.e = this;
        }

        public final boolean a(androidx.appcompat.view.menu.f fVar, MenuItem menuItem) {
            AbstractC0647a.InterfaceC0054a interfaceC0054a = this.f3350n;
            if (interfaceC0054a != null) {
                return interfaceC0054a.a(this, menuItem);
            }
            return false;
        }

        public final void b(androidx.appcompat.view.menu.f fVar) {
            if (this.f3350n == null) {
                return;
            }
            i();
            androidx.appcompat.widget.a aVar = z.this.f.f5094m;
            if (aVar != null) {
                aVar.l();
            }
        }

        @Override // j.AbstractC0647a
        public final void c() {
            z zVar = z.this;
            if (zVar.f3329i != this) {
                return;
            }
            if (zVar.f3336p) {
                zVar.f3330j = this;
                zVar.f3331k = this.f3350n;
            } else {
                this.f3350n.c(this);
            }
            this.f3350n = null;
            zVar.d(false);
            ActionBarContextView actionBarContextView = zVar.f;
            if (actionBarContextView.t == null) {
                actionBarContextView.h();
            }
            zVar.f3324c.setHideOnContentScrollEnabled(zVar.f3341u);
            zVar.f3329i = null;
        }

        @Override // j.AbstractC0647a
        public final View d() {
            WeakReference<View> weakReference = this.f3351o;
            if (weakReference != null) {
                return weakReference.get();
            }
            return null;
        }

        @Override // j.AbstractC0647a
        public final androidx.appcompat.view.menu.f e() {
            return this.f3349m;
        }

        @Override // j.AbstractC0647a
        public final MenuInflater f() {
            return new C0652f(this.f3348l);
        }

        @Override // j.AbstractC0647a
        public final CharSequence g() {
            return z.this.f.getSubtitle();
        }

        @Override // j.AbstractC0647a
        public final CharSequence h() {
            return z.this.f.getTitle();
        }

        @Override // j.AbstractC0647a
        public final void i() {
            if (z.this.f3329i != this) {
                return;
            }
            Menu menu = this.f3349m;
            menu.w();
            try {
                this.f3350n.d(this, menu);
            } finally {
                menu.v();
            }
        }

        @Override // j.AbstractC0647a
        public final boolean j() {
            return z.this.f.B;
        }

        @Override // j.AbstractC0647a
        public final void k(View view) {
            z.this.f.setCustomView(view);
            this.f3351o = new WeakReference<>(view);
        }

        @Override // j.AbstractC0647a
        public final void l(int i4) {
            m(z.this.f3322a.getResources().getString(i4));
        }

        @Override // j.AbstractC0647a
        public final void m(CharSequence charSequence) {
            z.this.f.setSubtitle(charSequence);
        }

        @Override // j.AbstractC0647a
        public final void n(int i4) {
            o(z.this.f3322a.getResources().getString(i4));
        }

        @Override // j.AbstractC0647a
        public final void o(CharSequence charSequence) {
            z.this.f.setTitle(charSequence);
        }

        @Override // j.AbstractC0647a
        public final void p(boolean z4) {
            this.f4650k = z4;
            z.this.f.setTitleOptional(z4);
        }
    }

    public z(Activity activity, boolean z4) {
        new ArrayList();
        this.f3333m = new ArrayList<>();
        this.f3334n = 0;
        this.f3335o = true;
        this.f3338r = true;
        this.f3342v = new a();
        this.f3343w = new b();
        this.f3344x = new c();
        View decorView = activity.getWindow().getDecorView();
        g(decorView);
        if (z4) {
            return;
        }
        this.f3327g = decorView.findViewById(16908290);
    }

    @Override // e.AbstractC0392a
    public final void a(boolean z4) {
        int i4;
        if (z4) {
            i4 = 4;
        } else {
            i4 = 0;
        }
        int n4 = this.f3326e.n();
        this.f3328h = true;
        this.f3326e.k((i4 & 4) | (n4 & (-5)));
    }

    @Override // e.AbstractC0392a
    public final void b(String str) {
        this.f3326e.m(str);
    }

    @Override // e.AbstractC0392a
    public final void c(String str) {
        this.f3326e.setTitle(str);
    }

    public final void d(boolean z4) {
        V p4;
        V e4;
        long j4;
        if (z4) {
            if (!this.f3337q) {
                this.f3337q = true;
                ActionBarOverlayLayout actionBarOverlayLayout = this.f3324c;
                if (actionBarOverlayLayout != null) {
                    actionBarOverlayLayout.setShowingForActionMode(true);
                }
                i(false);
            }
        } else if (this.f3337q) {
            this.f3337q = false;
            ActionBarOverlayLayout actionBarOverlayLayout2 = this.f3324c;
            if (actionBarOverlayLayout2 != null) {
                actionBarOverlayLayout2.setShowingForActionMode(false);
            }
            i(false);
        }
        if (this.f3325d.isLaidOut()) {
            if (z4) {
                e4 = this.f3326e.p(4, 100L);
                p4 = this.f.e(0, 200L);
            } else {
                p4 = this.f3326e.p(0, 200L);
                e4 = this.f.e(8, 100L);
            }
            C0653g c0653g = new C0653g();
            ArrayList<V> arrayList = c0653g.f4706a;
            arrayList.add(e4);
            View view = e4.f1551a.get();
            if (view != null) {
                j4 = view.animate().getDuration();
            } else {
                j4 = 0;
            }
            View view2 = p4.f1551a.get();
            if (view2 != null) {
                view2.animate().setStartDelay(j4);
            }
            arrayList.add(p4);
            c0653g.b();
        } else if (z4) {
            this.f3326e.i(4);
            this.f.setVisibility(0);
        } else {
            this.f3326e.i(0);
            this.f.setVisibility(8);
        }
    }

    public final void e(boolean z4) {
        if (z4 == this.f3332l) {
            return;
        }
        this.f3332l = z4;
        ArrayList<AbstractC0392a.b> arrayList = this.f3333m;
        int size = arrayList.size();
        for (int i4 = 0; i4 < size; i4++) {
            arrayList.get(i4).a();
        }
    }

    public final Context f() {
        if (this.f3323b == null) {
            TypedValue typedValue = new TypedValue();
            this.f3322a.getTheme().resolveAttribute(2130903050, typedValue, true);
            int i4 = typedValue.resourceId;
            if (i4 != 0) {
                this.f3323b = new ContextThemeWrapper(this.f3322a, i4);
            } else {
                this.f3323b = this.f3322a;
            }
        }
        return this.f3323b;
    }

    public final void g(View view) {
        String str;
        F wrapper;
        boolean z4;
        ActionBarOverlayLayout findViewById = view.findViewById(2131230890);
        this.f3324c = findViewById;
        if (findViewById != null) {
            findViewById.setActionBarVisibilityCallback(this);
        }
        Toolbar findViewById2 = view.findViewById(2131230768);
        if (findViewById2 instanceof F) {
            wrapper = (F) findViewById2;
        } else if (findViewById2 instanceof Toolbar) {
            wrapper = findViewById2.getWrapper();
        } else {
            if (findViewById2 != null) {
                str = findViewById2.getClass().getSimpleName();
            } else {
                str = "null";
            }
            throw new IllegalStateException("Can't make a decor toolbar out of ".concat(str));
        }
        this.f3326e = wrapper;
        this.f = view.findViewById(2131230776);
        ActionBarContainer findViewById3 = view.findViewById(2131230770);
        this.f3325d = findViewById3;
        F f = this.f3326e;
        if (f != null && this.f != null && findViewById3 != null) {
            this.f3322a = f.getContext();
            if ((this.f3326e.n() & 4) != 0) {
                z4 = true;
            } else {
                z4 = false;
            }
            if (z4) {
                this.f3328h = true;
            }
            Context context = this.f3322a;
            int i4 = context.getApplicationInfo().targetSdkVersion;
            this.f3326e.getClass();
            h(context.getResources().getBoolean(2130968576));
            TypedArray obtainStyledAttributes = this.f3322a.obtainStyledAttributes(null, C0376a.f3129a, 2130903045, 0);
            if (obtainStyledAttributes.getBoolean(14, false)) {
                ActionBarOverlayLayout actionBarOverlayLayout = this.f3324c;
                if (actionBarOverlayLayout.p) {
                    this.f3341u = true;
                    actionBarOverlayLayout.setHideOnContentScrollEnabled(true);
                } else {
                    throw new IllegalStateException("Action bar must be in overlay mode (Window.FEATURE_OVERLAY_ACTION_BAR) to enable hide on content scroll");
                }
            }
            int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(12, 0);
            if (dimensionPixelSize != 0) {
                ActionBarContainer actionBarContainer = this.f3325d;
                WeakHashMap<View, V> weakHashMap = O.f1526a;
                O.d.s(actionBarContainer, dimensionPixelSize);
            }
            obtainStyledAttributes.recycle();
            return;
        }
        throw new IllegalStateException(z.class.getSimpleName().concat(" can only be used with a compatible window decor layout"));
    }

    public final void h(boolean z4) {
        if (!z4) {
            this.f3326e.l();
            this.f3325d.setTabContainer((androidx.appcompat.widget.c) null);
        } else {
            this.f3325d.setTabContainer((androidx.appcompat.widget.c) null);
            this.f3326e.l();
        }
        this.f3326e.getClass();
        this.f3326e.s(false);
        this.f3324c.setHasNonEmbeddedTabs(false);
    }

    public final void i(boolean z4) {
        boolean z5;
        int[] iArr;
        int[] iArr2;
        boolean z6 = this.f3336p;
        if (this.f3337q || !z6) {
            z5 = true;
        } else {
            z5 = false;
        }
        View view = this.f3327g;
        ValueAnimator.AnimatorUpdateListener animatorUpdateListener = null;
        final c cVar = this.f3344x;
        if (z5) {
            if (!this.f3338r) {
                this.f3338r = true;
                C0653g c0653g = this.f3339s;
                if (c0653g != null) {
                    c0653g.a();
                }
                this.f3325d.setVisibility(0);
                int i4 = this.f3334n;
                b bVar = this.f3343w;
                if (i4 == 0 && (this.f3340t || z4)) {
                    this.f3325d.setTranslationY(0.0f);
                    float f = -this.f3325d.getHeight();
                    if (z4) {
                        this.f3325d.getLocationInWindow(new int[]{0, 0});
                        f -= iArr2[1];
                    }
                    this.f3325d.setTranslationY(f);
                    C0653g c0653g2 = new C0653g();
                    V a4 = O.a(this.f3325d);
                    a4.e(0.0f);
                    final View view2 = a4.f1551a.get();
                    if (view2 != null) {
                        if (cVar != null) {
                            animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: M.T
                                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    ((View) e.z.this.f3325d.getParent()).invalidate();
                                }
                            };
                        }
                        view2.animate().setUpdateListener(animatorUpdateListener);
                    }
                    boolean z7 = c0653g2.f4710e;
                    ArrayList<V> arrayList = c0653g2.f4706a;
                    if (!z7) {
                        arrayList.add(a4);
                    }
                    if (this.f3335o && view != null) {
                        view.setTranslationY(f);
                        V a5 = O.a(view);
                        a5.e(0.0f);
                        if (!c0653g2.f4710e) {
                            arrayList.add(a5);
                        }
                    }
                    DecelerateInterpolator decelerateInterpolator = f3321z;
                    boolean z8 = c0653g2.f4710e;
                    if (!z8) {
                        c0653g2.f4708c = decelerateInterpolator;
                    }
                    if (!z8) {
                        c0653g2.f4707b = 250L;
                    }
                    if (!z8) {
                        c0653g2.f4709d = bVar;
                    }
                    this.f3339s = c0653g2;
                    c0653g2.b();
                } else {
                    this.f3325d.setAlpha(1.0f);
                    this.f3325d.setTranslationY(0.0f);
                    if (this.f3335o && view != null) {
                        view.setTranslationY(0.0f);
                    }
                    bVar.b();
                }
                ActionBarOverlayLayout actionBarOverlayLayout = this.f3324c;
                if (actionBarOverlayLayout != null) {
                    WeakHashMap<View, V> weakHashMap = O.f1526a;
                    O.c.c(actionBarOverlayLayout);
                }
            }
        } else if (this.f3338r) {
            this.f3338r = false;
            C0653g c0653g3 = this.f3339s;
            if (c0653g3 != null) {
                c0653g3.a();
            }
            int i5 = this.f3334n;
            a aVar = this.f3342v;
            if (i5 == 0 && (this.f3340t || z4)) {
                this.f3325d.setAlpha(1.0f);
                this.f3325d.setTransitioning(true);
                C0653g c0653g4 = new C0653g();
                float f4 = -this.f3325d.getHeight();
                if (z4) {
                    this.f3325d.getLocationInWindow(new int[]{0, 0});
                    f4 -= iArr[1];
                }
                V a6 = O.a(this.f3325d);
                a6.e(f4);
                final View view3 = a6.f1551a.get();
                if (view3 != null) {
                    if (cVar != null) {
                        animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: M.T
                            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                ((View) e.z.this.f3325d.getParent()).invalidate();
                            }
                        };
                    }
                    view3.animate().setUpdateListener(animatorUpdateListener);
                }
                boolean z9 = c0653g4.f4710e;
                ArrayList<V> arrayList2 = c0653g4.f4706a;
                if (!z9) {
                    arrayList2.add(a6);
                }
                if (this.f3335o && view != null) {
                    V a7 = O.a(view);
                    a7.e(f4);
                    if (!c0653g4.f4710e) {
                        arrayList2.add(a7);
                    }
                }
                AccelerateInterpolator accelerateInterpolator = f3320y;
                boolean z10 = c0653g4.f4710e;
                if (!z10) {
                    c0653g4.f4708c = accelerateInterpolator;
                }
                if (!z10) {
                    c0653g4.f4707b = 250L;
                }
                if (!z10) {
                    c0653g4.f4709d = aVar;
                }
                this.f3339s = c0653g4;
                c0653g4.b();
                return;
            }
            aVar.b();
        }
    }

    public z(Dialog dialog) {
        new ArrayList();
        this.f3333m = new ArrayList<>();
        this.f3334n = 0;
        this.f3335o = true;
        this.f3338r = true;
        this.f3342v = new a();
        this.f3343w = new b();
        this.f3344x = new c();
        g(dialog.getWindow().getDecorView());
    }
}
