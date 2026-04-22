package e;

import D.f;
import M.C0232n;
import M.O;
import M.V;
import S0.RunnableC0293z;
import android.app.Activity;
import android.app.Dialog;
import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.AndroidRuntimeException;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;
import androidx.appcompat.view.menu.f;
import androidx.appcompat.view.menu.j;
import androidx.appcompat.widget.ActionBarContextView;
import androidx.appcompat.widget.ContentFrameLayout;
import androidx.appcompat.widget.ViewStubCompat;
import b2.C0355a;
import com.google.android.gms.internal.ads.vZ;
import d.C0376a;
import e.y;
import e.z;
import j$.util.Objects;
import j.AbstractC0647a;
import j.C0649c;
import j.C0651e;
import j.C0652f;
import j.C0653g;
import j.Window$CallbackC0654h;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.WeakHashMap;
import l.C0701j;
import l.E;
import l.F;
import l.i0;

/* renamed from: e.j  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class LayoutInflater$Factory2C0401j extends AbstractC0399h implements f.a, LayoutInflater.Factory2 {

    /* renamed from: r0  reason: collision with root package name */
    public static final r.j<String, Integer> f3197r0 = new r.j<>();

    /* renamed from: s0  reason: collision with root package name */
    public static final int[] f3198s0 = {16842836};

    /* renamed from: t0  reason: collision with root package name */
    public static final boolean f3199t0 = !"robolectric".equals(Build.FINGERPRINT);

    /* renamed from: A  reason: collision with root package name */
    public E f3200A;

    /* renamed from: B  reason: collision with root package name */
    public b f3201B;

    /* renamed from: C  reason: collision with root package name */
    public m f3202C;

    /* renamed from: D  reason: collision with root package name */
    public AbstractC0647a f3203D;

    /* renamed from: E  reason: collision with root package name */
    public ActionBarContextView f3204E;

    /* renamed from: F  reason: collision with root package name */
    public PopupWindow f3205F;

    /* renamed from: G  reason: collision with root package name */
    public RunnableC0403l f3206G;

    /* renamed from: J  reason: collision with root package name */
    public boolean f3209J;

    /* renamed from: K  reason: collision with root package name */
    public ViewGroup f3210K;

    /* renamed from: L  reason: collision with root package name */
    public TextView f3211L;

    /* renamed from: M  reason: collision with root package name */
    public View f3212M;

    /* renamed from: N  reason: collision with root package name */
    public boolean f3213N;

    /* renamed from: O  reason: collision with root package name */
    public boolean f3214O;

    /* renamed from: P  reason: collision with root package name */
    public boolean f3215P;

    /* renamed from: Q  reason: collision with root package name */
    public boolean f3216Q;

    /* renamed from: R  reason: collision with root package name */
    public boolean f3217R;

    /* renamed from: S  reason: collision with root package name */
    public boolean f3218S;

    /* renamed from: T  reason: collision with root package name */
    public boolean f3219T;

    /* renamed from: U  reason: collision with root package name */
    public boolean f3220U;

    /* renamed from: V  reason: collision with root package name */
    public l[] f3221V;

    /* renamed from: W  reason: collision with root package name */
    public l f3222W;

    /* renamed from: X  reason: collision with root package name */
    public boolean f3223X;

    /* renamed from: Y  reason: collision with root package name */
    public boolean f3224Y;

    /* renamed from: Z  reason: collision with root package name */
    public boolean f3225Z;

    /* renamed from: a0  reason: collision with root package name */
    public boolean f3226a0;

    /* renamed from: b0  reason: collision with root package name */
    public Configuration f3227b0;

    /* renamed from: c0  reason: collision with root package name */
    public final int f3228c0;

    /* renamed from: d0  reason: collision with root package name */
    public int f3229d0;

    /* renamed from: e0  reason: collision with root package name */
    public int f3230e0;

    /* renamed from: f0  reason: collision with root package name */
    public boolean f3231f0;

    /* renamed from: g0  reason: collision with root package name */
    public C0044j f3232g0;

    /* renamed from: h0  reason: collision with root package name */
    public h f3233h0;

    /* renamed from: i0  reason: collision with root package name */
    public boolean f3234i0;

    /* renamed from: j0  reason: collision with root package name */
    public int f3235j0;
    public boolean l0;

    /* renamed from: m0  reason: collision with root package name */
    public Rect f3237m0;

    /* renamed from: n0  reason: collision with root package name */
    public Rect f3238n0;

    /* renamed from: o0  reason: collision with root package name */
    public u f3239o0;

    /* renamed from: p0  reason: collision with root package name */
    public OnBackInvokedDispatcher f3240p0;

    /* renamed from: q0  reason: collision with root package name */
    public OnBackInvokedCallback f3241q0;

    /* renamed from: s  reason: collision with root package name */
    public final Object f3242s;

    /* renamed from: t  reason: collision with root package name */
    public final Context f3243t;

    /* renamed from: u  reason: collision with root package name */
    public Window f3244u;

    /* renamed from: v  reason: collision with root package name */
    public g f3245v;

    /* renamed from: w  reason: collision with root package name */
    public final InterfaceC0398g f3246w;

    /* renamed from: x  reason: collision with root package name */
    public z f3247x;

    /* renamed from: y  reason: collision with root package name */
    public C0652f f3248y;

    /* renamed from: z  reason: collision with root package name */
    public CharSequence f3249z;

    /* renamed from: H  reason: collision with root package name */
    public V f3207H = null;

    /* renamed from: I  reason: collision with root package name */
    public final boolean f3208I = true;

    /* renamed from: k0  reason: collision with root package name */
    public final a f3236k0 = new a();

    /* renamed from: e.j$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public class a implements Runnable {
        public a() {
        }

        @Override // java.lang.Runnable
        public final void run() {
            LayoutInflater$Factory2C0401j layoutInflater$Factory2C0401j = LayoutInflater$Factory2C0401j.this;
            if ((layoutInflater$Factory2C0401j.f3235j0 & 1) != 0) {
                layoutInflater$Factory2C0401j.F(0);
            }
            if ((layoutInflater$Factory2C0401j.f3235j0 & 4096) != 0) {
                layoutInflater$Factory2C0401j.F(108);
            }
            layoutInflater$Factory2C0401j.f3234i0 = false;
            layoutInflater$Factory2C0401j.f3235j0 = 0;
        }
    }

    /* renamed from: e.j$b */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public final class b implements j.a {
        public b() {
        }

        public final void b(androidx.appcompat.view.menu.f fVar, boolean z4) {
            LayoutInflater$Factory2C0401j.this.B(fVar);
        }

        public final boolean c(androidx.appcompat.view.menu.f fVar) {
            Window.Callback callback = LayoutInflater$Factory2C0401j.this.f3244u.getCallback();
            if (callback != null) {
                callback.onMenuOpened(108, fVar);
                return true;
            }
            return true;
        }
    }

    /* renamed from: e.j$c */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class c implements AbstractC0647a.InterfaceC0054a {

        /* renamed from: a  reason: collision with root package name */
        public final AbstractC0647a.InterfaceC0054a f3252a;

        /* renamed from: e.j$c$a */
        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
        public class a extends H.a {
            public a() {
            }

            @Override // M.W
            public final void b() {
                c cVar = c.this;
                LayoutInflater$Factory2C0401j.this.f3204E.setVisibility(8);
                LayoutInflater$Factory2C0401j layoutInflater$Factory2C0401j = LayoutInflater$Factory2C0401j.this;
                PopupWindow popupWindow = layoutInflater$Factory2C0401j.f3205F;
                if (popupWindow != null) {
                    popupWindow.dismiss();
                } else if (layoutInflater$Factory2C0401j.f3204E.getParent() instanceof View) {
                    WeakHashMap<View, V> weakHashMap = O.f1526a;
                    O.c.c((View) layoutInflater$Factory2C0401j.f3204E.getParent());
                }
                layoutInflater$Factory2C0401j.f3204E.h();
                layoutInflater$Factory2C0401j.f3207H.d(null);
                layoutInflater$Factory2C0401j.f3207H = null;
                ViewGroup viewGroup = layoutInflater$Factory2C0401j.f3210K;
                WeakHashMap<View, V> weakHashMap2 = O.f1526a;
                O.c.c(viewGroup);
            }
        }

        public c(C0651e.a aVar) {
            this.f3252a = aVar;
        }

        @Override // j.AbstractC0647a.InterfaceC0054a
        public final boolean a(AbstractC0647a abstractC0647a, MenuItem menuItem) {
            return this.f3252a.a(abstractC0647a, menuItem);
        }

        @Override // j.AbstractC0647a.InterfaceC0054a
        public final boolean b(AbstractC0647a abstractC0647a, androidx.appcompat.view.menu.f fVar) {
            return this.f3252a.b(abstractC0647a, fVar);
        }

        @Override // j.AbstractC0647a.InterfaceC0054a
        public final void c(AbstractC0647a abstractC0647a) {
            this.f3252a.c(abstractC0647a);
            LayoutInflater$Factory2C0401j layoutInflater$Factory2C0401j = LayoutInflater$Factory2C0401j.this;
            if (layoutInflater$Factory2C0401j.f3205F != null) {
                layoutInflater$Factory2C0401j.f3244u.getDecorView().removeCallbacks(layoutInflater$Factory2C0401j.f3206G);
            }
            if (layoutInflater$Factory2C0401j.f3204E != null) {
                V v4 = layoutInflater$Factory2C0401j.f3207H;
                if (v4 != null) {
                    v4.b();
                }
                V a4 = O.a(layoutInflater$Factory2C0401j.f3204E);
                a4.a(0.0f);
                layoutInflater$Factory2C0401j.f3207H = a4;
                a4.d(new a());
            }
            layoutInflater$Factory2C0401j.f3203D = null;
            ViewGroup viewGroup = layoutInflater$Factory2C0401j.f3210K;
            WeakHashMap<View, V> weakHashMap = O.f1526a;
            O.c.c(viewGroup);
            layoutInflater$Factory2C0401j.T();
        }

        @Override // j.AbstractC0647a.InterfaceC0054a
        public final boolean d(AbstractC0647a abstractC0647a, Menu menu) {
            ViewGroup viewGroup = LayoutInflater$Factory2C0401j.this.f3210K;
            WeakHashMap<View, V> weakHashMap = O.f1526a;
            O.c.c(viewGroup);
            return this.f3252a.d(abstractC0647a, menu);
        }
    }

    /* renamed from: e.j$d */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class d {
        public static boolean a(PowerManager powerManager) {
            return powerManager.isPowerSaveMode();
        }

        public static String b(Locale locale) {
            return locale.toLanguageTag();
        }
    }

    /* renamed from: e.j$e */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class e {
        public static void a(Configuration configuration, Configuration configuration2, Configuration configuration3) {
            LocaleList locales;
            LocaleList locales2;
            boolean equals;
            locales = configuration.getLocales();
            locales2 = configuration2.getLocales();
            equals = locales.equals(locales2);
            if (!equals) {
                configuration3.setLocales(locales2);
                configuration3.locale = configuration2.locale;
            }
        }

        public static I.g b(Configuration configuration) {
            LocaleList locales;
            String languageTags;
            locales = configuration.getLocales();
            languageTags = locales.toLanguageTags();
            return I.g.b(languageTags);
        }

        public static void c(I.g gVar) {
            LocaleList forLanguageTags;
            forLanguageTags = LocaleList.forLanguageTags(gVar.f1135a.a());
            LocaleList.setDefault(forLanguageTags);
        }

        public static void d(Configuration configuration, I.g gVar) {
            LocaleList forLanguageTags;
            forLanguageTags = LocaleList.forLanguageTags(gVar.f1135a.a());
            configuration.setLocales(forLanguageTags);
        }
    }

    /* renamed from: e.j$f */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class f {
        public static OnBackInvokedDispatcher a(Activity activity) {
            return vZ.a(activity);
        }

        /* JADX WARN: Type inference failed for: r0v0, types: [android.window.OnBackInvokedCallback, e.r] */
        public static OnBackInvokedCallback b(Object obj, final LayoutInflater$Factory2C0401j layoutInflater$Factory2C0401j) {
            Objects.requireNonNull(layoutInflater$Factory2C0401j);
            ?? r02 = new OnBackInvokedCallback() { // from class: e.r
                public final void onBackInvoked() {
                    LayoutInflater$Factory2C0401j.this.O();
                }
            };
            o.a(obj).registerOnBackInvokedCallback(1000000, r02);
            return r02;
        }

        public static void c(Object obj, Object obj2) {
            o.a(obj).unregisterOnBackInvokedCallback(n.a(obj2));
        }
    }

    /* renamed from: e.j$h */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class h extends i {

        /* renamed from: c  reason: collision with root package name */
        public final PowerManager f3259c;

        public h(Context context) {
            super();
            this.f3259c = (PowerManager) context.getApplicationContext().getSystemService("power");
        }

        @Override // e.LayoutInflater$Factory2C0401j.i
        public final IntentFilter b() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
            return intentFilter;
        }

        @Override // e.LayoutInflater$Factory2C0401j.i
        public final int c() {
            if (d.a(this.f3259c)) {
                return 2;
            }
            return 1;
        }

        @Override // e.LayoutInflater$Factory2C0401j.i
        public final void d() {
            LayoutInflater$Factory2C0401j.this.x(true, true);
        }
    }

    /* renamed from: e.j$i */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public abstract class i {

        /* renamed from: a  reason: collision with root package name */
        public a f3261a;

        /* renamed from: e.j$i$a */
        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
        public class a extends BroadcastReceiver {
            public a() {
            }

            @Override // android.content.BroadcastReceiver
            public final void onReceive(Context context, Intent intent) {
                i.this.d();
            }
        }

        public i() {
        }

        public final void a() {
            a aVar = this.f3261a;
            if (aVar != null) {
                try {
                    LayoutInflater$Factory2C0401j.this.f3243t.unregisterReceiver(aVar);
                } catch (IllegalArgumentException unused) {
                }
                this.f3261a = null;
            }
        }

        public abstract IntentFilter b();

        public abstract int c();

        public abstract void d();

        public final void e() {
            a();
            IntentFilter b4 = b();
            if (b4.countActions() == 0) {
                return;
            }
            if (this.f3261a == null) {
                this.f3261a = new a();
            }
            LayoutInflater$Factory2C0401j.this.f3243t.registerReceiver(this.f3261a, b4);
        }
    }

    /* renamed from: e.j$j  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class C0044j extends i {

        /* renamed from: c  reason: collision with root package name */
        public final y f3264c;

        public C0044j(y yVar) {
            super();
            this.f3264c = yVar;
        }

        @Override // e.LayoutInflater$Factory2C0401j.i
        public final IntentFilter b() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.TIME_SET");
            intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
            intentFilter.addAction("android.intent.action.TIME_TICK");
            return intentFilter;
        }

        /* JADX WARN: Type inference failed for: r3v12, types: [java.lang.Object, e.x] */
        @Override // e.LayoutInflater$Factory2C0401j.i
        public final int c() {
            Location location;
            boolean z4;
            long j4;
            Location location2;
            y yVar = this.f3264c;
            y.a aVar = yVar.f3317c;
            if (aVar.f3319b > System.currentTimeMillis()) {
                z4 = aVar.f3318a;
            } else {
                Context context = yVar.f3315a;
                int a4 = C0355a.a(context, "android.permission.ACCESS_COARSE_LOCATION");
                Location location3 = null;
                LocationManager locationManager = yVar.f3316b;
                if (a4 == 0) {
                    try {
                    } catch (Exception e4) {
                        Log.d("TwilightManager", "Failed to get last known location", e4);
                    }
                    if (locationManager.isProviderEnabled("network")) {
                        location2 = locationManager.getLastKnownLocation("network");
                        location = location2;
                    }
                    location2 = null;
                    location = location2;
                } else {
                    location = null;
                }
                if (C0355a.a(context, "android.permission.ACCESS_FINE_LOCATION") == 0) {
                    try {
                        if (locationManager.isProviderEnabled("gps")) {
                            location3 = locationManager.getLastKnownLocation("gps");
                        }
                    } catch (Exception e5) {
                        Log.d("TwilightManager", "Failed to get last known location", e5);
                    }
                }
                if (location3 == null || location == null ? location3 != null : location3.getTime() > location.getTime()) {
                    location = location3;
                }
                z4 = false;
                if (location != null) {
                    long currentTimeMillis = System.currentTimeMillis();
                    if (x.f3310d == null) {
                        x.f3310d = new Object();
                    }
                    x xVar = x.f3310d;
                    xVar.a(currentTimeMillis - 86400000, location.getLatitude(), location.getLongitude());
                    xVar.a(currentTimeMillis, location.getLatitude(), location.getLongitude());
                    if (xVar.f3313c == 1) {
                        z4 = true;
                    }
                    long j5 = xVar.f3312b;
                    long j6 = xVar.f3311a;
                    xVar.a(currentTimeMillis + 86400000, location.getLatitude(), location.getLongitude());
                    long j7 = xVar.f3312b;
                    if (j5 != -1 && j6 != -1) {
                        if (currentTimeMillis <= j6) {
                            if (currentTimeMillis > j5) {
                                j7 = j6;
                            } else {
                                j7 = j5;
                            }
                        }
                        j4 = j7 + 60000;
                    } else {
                        j4 = currentTimeMillis + 43200000;
                    }
                    aVar.f3318a = z4;
                    aVar.f3319b = j4;
                } else {
                    Log.i("TwilightManager", "Could not get last known location. This is probably because the app does not have any location permissions. Falling back to hardcoded sunrise/sunset values.");
                    int i4 = Calendar.getInstance().get(11);
                    if (i4 < 6 || i4 >= 22) {
                        z4 = true;
                    }
                }
            }
            if (!z4) {
                return 1;
            }
            return 2;
        }

        @Override // e.LayoutInflater$Factory2C0401j.i
        public final void d() {
            LayoutInflater$Factory2C0401j.this.x(true, true);
        }
    }

    /* renamed from: e.j$k */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class k extends ContentFrameLayout {
        public k(C0649c c0649c) {
            super(c0649c, (AttributeSet) null);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public final boolean dispatchKeyEvent(KeyEvent keyEvent) {
            if (!LayoutInflater$Factory2C0401j.this.E(keyEvent) && !super/*android.widget.FrameLayout*/.dispatchKeyEvent(keyEvent)) {
                return false;
            }
            return true;
        }

        /* JADX WARN: Multi-variable type inference failed */
        public final boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0) {
                int x4 = (int) motionEvent.getX();
                int y4 = (int) motionEvent.getY();
                if (x4 < -5 || y4 < -5 || x4 > getWidth() + 5 || y4 > getHeight() + 5) {
                    LayoutInflater$Factory2C0401j layoutInflater$Factory2C0401j = LayoutInflater$Factory2C0401j.this;
                    layoutInflater$Factory2C0401j.C(layoutInflater$Factory2C0401j.K(0), true);
                    return true;
                }
            }
            return super/*android.widget.FrameLayout*/.onInterceptTouchEvent(motionEvent);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public final void setBackgroundResource(int i4) {
            setBackgroundDrawable(B2.a.f(getContext(), i4));
        }
    }

    /* renamed from: e.j$l */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static final class l {

        /* renamed from: a  reason: collision with root package name */
        public int f3267a;

        /* renamed from: b  reason: collision with root package name */
        public int f3268b;

        /* renamed from: c  reason: collision with root package name */
        public int f3269c;

        /* renamed from: d  reason: collision with root package name */
        public int f3270d;

        /* renamed from: e  reason: collision with root package name */
        public k f3271e;
        public View f;

        /* renamed from: g  reason: collision with root package name */
        public View f3272g;

        /* renamed from: h  reason: collision with root package name */
        public androidx.appcompat.view.menu.f f3273h;

        /* renamed from: i  reason: collision with root package name */
        public androidx.appcompat.view.menu.d f3274i;

        /* renamed from: j  reason: collision with root package name */
        public C0649c f3275j;

        /* renamed from: k  reason: collision with root package name */
        public boolean f3276k;

        /* renamed from: l  reason: collision with root package name */
        public boolean f3277l;

        /* renamed from: m  reason: collision with root package name */
        public boolean f3278m;

        /* renamed from: n  reason: collision with root package name */
        public boolean f3279n;

        /* renamed from: o  reason: collision with root package name */
        public boolean f3280o;

        /* renamed from: p  reason: collision with root package name */
        public Bundle f3281p;
    }

    /* renamed from: e.j$m */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public final class m implements j.a {
        public m() {
        }

        public final void b(androidx.appcompat.view.menu.f fVar, boolean z4) {
            boolean z5;
            int i4;
            l lVar;
            androidx.appcompat.view.menu.f k4 = fVar.k();
            int i5 = 0;
            if (k4 != fVar) {
                z5 = true;
            } else {
                z5 = false;
            }
            if (z5) {
                fVar = k4;
            }
            LayoutInflater$Factory2C0401j layoutInflater$Factory2C0401j = LayoutInflater$Factory2C0401j.this;
            l[] lVarArr = layoutInflater$Factory2C0401j.f3221V;
            if (lVarArr != null) {
                i4 = lVarArr.length;
            } else {
                i4 = 0;
            }
            while (true) {
                if (i5 < i4) {
                    lVar = lVarArr[i5];
                    if (lVar != null && lVar.f3273h == fVar) {
                        break;
                    }
                    i5++;
                } else {
                    lVar = null;
                    break;
                }
            }
            if (lVar != null) {
                if (z5) {
                    layoutInflater$Factory2C0401j.A(lVar.f3267a, lVar, k4);
                    layoutInflater$Factory2C0401j.C(lVar, true);
                    return;
                }
                layoutInflater$Factory2C0401j.C(lVar, z4);
            }
        }

        public final boolean c(androidx.appcompat.view.menu.f fVar) {
            Window.Callback callback;
            if (fVar == fVar.k()) {
                LayoutInflater$Factory2C0401j layoutInflater$Factory2C0401j = LayoutInflater$Factory2C0401j.this;
                if (layoutInflater$Factory2C0401j.f3215P && (callback = layoutInflater$Factory2C0401j.f3244u.getCallback()) != null && !layoutInflater$Factory2C0401j.f3226a0) {
                    callback.onMenuOpened(108, fVar);
                    return true;
                }
                return true;
            }
            return true;
        }
    }

    public LayoutInflater$Factory2C0401j(Context context, Window window, InterfaceC0398g interfaceC0398g, Object obj) {
        r.j<String, Integer> jVar;
        Integer orDefault;
        C0397f c0397f;
        this.f3228c0 = -100;
        this.f3243t = context;
        this.f3246w = interfaceC0398g;
        this.f3242s = obj;
        if (obj instanceof Dialog) {
            while (context != null) {
                if (context instanceof C0397f) {
                    c0397f = (C0397f) context;
                    break;
                } else if (!(context instanceof ContextWrapper)) {
                    break;
                } else {
                    context = ((ContextWrapper) context).getBaseContext();
                }
            }
            c0397f = null;
            if (c0397f != null) {
                this.f3228c0 = c0397f.x().g();
            }
        }
        if (this.f3228c0 == -100 && (orDefault = (jVar = f3197r0).getOrDefault(this.f3242s.getClass().getName(), null)) != null) {
            this.f3228c0 = orDefault.intValue();
            jVar.remove(this.f3242s.getClass().getName());
        }
        if (window != null) {
            y(window);
        }
        C0701j.d();
    }

    public static Configuration D(Context context, int i4, I.g gVar, Configuration configuration, boolean z4) {
        int i5;
        if (i4 != 1) {
            if (i4 != 2) {
                if (z4) {
                    i5 = 0;
                } else {
                    i5 = context.getApplicationContext().getResources().getConfiguration().uiMode & 48;
                }
            } else {
                i5 = 32;
            }
        } else {
            i5 = 16;
        }
        Configuration configuration2 = new Configuration();
        configuration2.fontScale = 0.0f;
        if (configuration != null) {
            configuration2.setTo(configuration);
        }
        configuration2.uiMode = i5 | (configuration2.uiMode & (-49));
        if (gVar != null) {
            if (Build.VERSION.SDK_INT >= 24) {
                e.d(configuration2, gVar);
            } else {
                I.j jVar = gVar.f1135a;
                configuration2.setLocale(jVar.get(0));
                configuration2.setLayoutDirection(jVar.get(0));
            }
        }
        return configuration2;
    }

    public static I.g J(Configuration configuration) {
        if (Build.VERSION.SDK_INT >= 24) {
            return e.b(configuration);
        }
        return I.g.b(d.b(configuration.locale));
    }

    public static I.g z(Context context) {
        I.g gVar;
        I.g b4;
        Locale locale;
        int i4 = Build.VERSION.SDK_INT;
        if (i4 >= 33 || (gVar = AbstractC0399h.f3184l) == null) {
            return null;
        }
        I.g J3 = J(context.getApplicationContext().getResources().getConfiguration());
        I.j jVar = gVar.f1135a;
        if (i4 >= 24) {
            if (jVar.isEmpty()) {
                b4 = I.g.f1134b;
            } else {
                LinkedHashSet linkedHashSet = new LinkedHashSet();
                for (int i5 = 0; i5 < J3.f1135a.size() + jVar.size(); i5++) {
                    if (i5 < jVar.size()) {
                        locale = jVar.get(i5);
                    } else {
                        locale = J3.f1135a.get(i5 - jVar.size());
                    }
                    if (locale != null) {
                        linkedHashSet.add(locale);
                    }
                }
                b4 = I.g.a((Locale[]) linkedHashSet.toArray(new Locale[linkedHashSet.size()]));
            }
        } else if (jVar.isEmpty()) {
            b4 = I.g.f1134b;
        } else {
            b4 = I.g.b(d.b(jVar.get(0)));
        }
        if (!b4.f1135a.isEmpty()) {
            return b4;
        }
        return J3;
    }

    public final void A(int i4, l lVar, androidx.appcompat.view.menu.f fVar) {
        if (fVar == null) {
            if (lVar == null && i4 >= 0) {
                l[] lVarArr = this.f3221V;
                if (i4 < lVarArr.length) {
                    lVar = lVarArr[i4];
                }
            }
            if (lVar != null) {
                fVar = lVar.f3273h;
            }
        }
        if ((lVar == null || lVar.f3278m) && !this.f3226a0) {
            g gVar = this.f3245v;
            Window.Callback callback = this.f3244u.getCallback();
            gVar.getClass();
            try {
                gVar.f3257m = true;
                callback.onPanelClosed(i4, fVar);
            } finally {
                gVar.f3257m = false;
            }
        }
    }

    public final void B(androidx.appcompat.view.menu.f fVar) {
        if (this.f3220U) {
            return;
        }
        this.f3220U = true;
        this.f3200A.l();
        Window.Callback callback = this.f3244u.getCallback();
        if (callback != null && !this.f3226a0) {
            callback.onPanelClosed(108, fVar);
        }
        this.f3220U = false;
    }

    public final void C(l lVar, boolean z4) {
        ContentFrameLayout contentFrameLayout;
        E e4;
        if (z4 && lVar.f3267a == 0 && (e4 = this.f3200A) != null && e4.b()) {
            B(lVar.f3273h);
            return;
        }
        WindowManager windowManager = (WindowManager) this.f3243t.getSystemService("window");
        if (windowManager != null && lVar.f3278m && (contentFrameLayout = lVar.f3271e) != null) {
            windowManager.removeView(contentFrameLayout);
            if (z4) {
                A(lVar.f3267a, lVar, null);
            }
        }
        lVar.f3276k = false;
        lVar.f3277l = false;
        lVar.f3278m = false;
        lVar.f = null;
        lVar.f3279n = true;
        if (this.f3222W == lVar) {
            this.f3222W = null;
        }
        if (lVar.f3267a == 0) {
            T();
        }
    }

    public final boolean E(KeyEvent keyEvent) {
        View decorView;
        boolean z4;
        boolean z5;
        Object obj = this.f3242s;
        boolean z6 = true;
        if (((obj instanceof C0232n.a) || (obj instanceof t)) && (decorView = this.f3244u.getDecorView()) != null && C0232n.a(decorView, keyEvent)) {
            return true;
        }
        if (keyEvent.getKeyCode() == 82) {
            g gVar = this.f3245v;
            Window.Callback callback = this.f3244u.getCallback();
            gVar.getClass();
            try {
                gVar.f3256l = true;
                if (callback.dispatchKeyEvent(keyEvent)) {
                    return true;
                }
            } finally {
                gVar.f3256l = false;
            }
        }
        int keyCode = keyEvent.getKeyCode();
        if (keyEvent.getAction() == 0) {
            if (keyCode != 4) {
                if (keyCode == 82) {
                    if (keyEvent.getRepeatCount() != 0) {
                        return true;
                    }
                    l K3 = K(0);
                    if (K3.f3278m) {
                        return true;
                    }
                    R(K3, keyEvent);
                    return true;
                }
            } else {
                if ((keyEvent.getFlags() & 128) == 0) {
                    z6 = false;
                }
                this.f3223X = z6;
            }
        } else if (keyCode != 4) {
            if (keyCode == 82) {
                if (this.f3203D != null) {
                    return true;
                }
                l K4 = K(0);
                E e4 = this.f3200A;
                Context context = this.f3243t;
                if (e4 != null && e4.g() && !ViewConfiguration.get(context).hasPermanentMenuKey()) {
                    if (!this.f3200A.b()) {
                        if (!this.f3226a0 && R(K4, keyEvent)) {
                            z4 = this.f3200A.f();
                        }
                        z4 = false;
                    } else {
                        z4 = this.f3200A.e();
                    }
                } else {
                    boolean z7 = K4.f3278m;
                    if (!z7 && !K4.f3277l) {
                        if (K4.f3276k) {
                            if (K4.f3280o) {
                                K4.f3276k = false;
                                z5 = R(K4, keyEvent);
                            } else {
                                z5 = true;
                            }
                            if (z5) {
                                P(K4, keyEvent);
                                z4 = true;
                            }
                        }
                        z4 = false;
                    } else {
                        C(K4, true);
                        z4 = z7;
                    }
                }
                if (!z4) {
                    return true;
                }
                AudioManager audioManager = (AudioManager) context.getApplicationContext().getSystemService("audio");
                if (audioManager != null) {
                    audioManager.playSoundEffect(0);
                    return true;
                }
                Log.w("AppCompatDelegate", "Couldn't get audio manager");
                return true;
            }
        } else if (O()) {
            return true;
        }
        return false;
    }

    public final void F(int i4) {
        l K3 = K(i4);
        if (K3.f3273h != null) {
            Bundle bundle = new Bundle();
            K3.f3273h.t(bundle);
            if (bundle.size() > 0) {
                K3.f3281p = bundle;
            }
            K3.f3273h.w();
            K3.f3273h.clear();
        }
        K3.f3280o = true;
        K3.f3279n = true;
        if ((i4 == 108 || i4 == 0) && this.f3200A != null) {
            l K4 = K(0);
            K4.f3276k = false;
            R(K4, null);
        }
    }

    public final void G() {
        ViewGroup viewGroup;
        CharSequence charSequence;
        Context context;
        if (!this.f3209J) {
            int[] iArr = C0376a.f3137j;
            Context context2 = this.f3243t;
            TypedArray obtainStyledAttributes = context2.obtainStyledAttributes(iArr);
            if (obtainStyledAttributes.hasValue(117)) {
                if (obtainStyledAttributes.getBoolean(126, false)) {
                    s(1);
                } else if (obtainStyledAttributes.getBoolean(117, false)) {
                    s(108);
                }
                if (obtainStyledAttributes.getBoolean(118, false)) {
                    s(109);
                }
                if (obtainStyledAttributes.getBoolean(119, false)) {
                    s(10);
                }
                this.f3218S = obtainStyledAttributes.getBoolean(0, false);
                obtainStyledAttributes.recycle();
                H();
                this.f3244u.getDecorView();
                LayoutInflater from = LayoutInflater.from(context2);
                if (!this.f3219T) {
                    if (this.f3218S) {
                        viewGroup = (ViewGroup) from.inflate(2131427340, (ViewGroup) null);
                        this.f3216Q = false;
                        this.f3215P = false;
                    } else if (this.f3215P) {
                        TypedValue typedValue = new TypedValue();
                        context2.getTheme().resolveAttribute(2130903049, typedValue, true);
                        if (typedValue.resourceId != 0) {
                            context = new C0649c(context2, typedValue.resourceId);
                        } else {
                            context = context2;
                        }
                        viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(2131427351, (ViewGroup) null);
                        E e4 = (E) viewGroup.findViewById(2131230890);
                        this.f3200A = e4;
                        e4.setWindowCallback(this.f3244u.getCallback());
                        if (this.f3216Q) {
                            this.f3200A.k(109);
                        }
                        if (this.f3213N) {
                            this.f3200A.k(2);
                        }
                        if (this.f3214O) {
                            this.f3200A.k(5);
                        }
                    } else {
                        viewGroup = null;
                    }
                } else {
                    viewGroup = this.f3217R ? (ViewGroup) from.inflate(2131427350, (ViewGroup) null) : (ViewGroup) from.inflate(2131427349, (ViewGroup) null);
                }
                if (viewGroup != null) {
                    D1.E e5 = new D1.E(this);
                    WeakHashMap<View, V> weakHashMap = O.f1526a;
                    O.d.u(viewGroup, e5);
                    if (this.f3200A == null) {
                        this.f3211L = (TextView) viewGroup.findViewById(2131231285);
                    }
                    boolean z4 = i0.f5158a;
                    try {
                        Method method = viewGroup.getClass().getMethod("makeOptionalFitsSystemWindows", null);
                        if (!method.isAccessible()) {
                            method.setAccessible(true);
                        }
                        method.invoke(viewGroup, null);
                    } catch (IllegalAccessException e6) {
                        Log.d("ViewUtils", "Could not invoke makeOptionalFitsSystemWindows", e6);
                    } catch (NoSuchMethodException unused) {
                        Log.d("ViewUtils", "Could not find method makeOptionalFitsSystemWindows. Oh well...");
                    } catch (InvocationTargetException e7) {
                        Log.d("ViewUtils", "Could not invoke makeOptionalFitsSystemWindows", e7);
                    }
                    ContentFrameLayout findViewById = viewGroup.findViewById(2131230769);
                    ViewGroup viewGroup2 = (ViewGroup) this.f3244u.findViewById(16908290);
                    if (viewGroup2 != null) {
                        while (viewGroup2.getChildCount() > 0) {
                            View childAt = viewGroup2.getChildAt(0);
                            viewGroup2.removeViewAt(0);
                            findViewById.addView(childAt);
                        }
                        viewGroup2.setId(-1);
                        findViewById.setId(16908290);
                        if (viewGroup2 instanceof FrameLayout) {
                            ((FrameLayout) viewGroup2).setForeground(null);
                        }
                    }
                    this.f3244u.setContentView(viewGroup);
                    findViewById.setAttachListener(new C0402k(this));
                    this.f3210K = viewGroup;
                    Object obj = this.f3242s;
                    if (obj instanceof Activity) {
                        charSequence = ((Activity) obj).getTitle();
                    } else {
                        charSequence = this.f3249z;
                    }
                    if (!TextUtils.isEmpty(charSequence)) {
                        E e8 = this.f3200A;
                        if (e8 != null) {
                            e8.setWindowTitle(charSequence);
                        } else {
                            z zVar = this.f3247x;
                            if (zVar != null) {
                                zVar.f3326e.setWindowTitle(charSequence);
                            } else {
                                TextView textView = this.f3211L;
                                if (textView != null) {
                                    textView.setText(charSequence);
                                }
                            }
                        }
                    }
                    ContentFrameLayout findViewById2 = this.f3210K.findViewById(16908290);
                    View decorView = this.f3244u.getDecorView();
                    findViewById2.p.set(decorView.getPaddingLeft(), decorView.getPaddingTop(), decorView.getPaddingRight(), decorView.getPaddingBottom());
                    if (findViewById2.isLaidOut()) {
                        findViewById2.requestLayout();
                    }
                    TypedArray obtainStyledAttributes2 = context2.obtainStyledAttributes(iArr);
                    obtainStyledAttributes2.getValue(124, findViewById2.getMinWidthMajor());
                    obtainStyledAttributes2.getValue(125, findViewById2.getMinWidthMinor());
                    if (obtainStyledAttributes2.hasValue(122)) {
                        obtainStyledAttributes2.getValue(122, findViewById2.getFixedWidthMajor());
                    }
                    if (obtainStyledAttributes2.hasValue(123)) {
                        obtainStyledAttributes2.getValue(123, findViewById2.getFixedWidthMinor());
                    }
                    if (obtainStyledAttributes2.hasValue(120)) {
                        obtainStyledAttributes2.getValue(120, findViewById2.getFixedHeightMajor());
                    }
                    if (obtainStyledAttributes2.hasValue(121)) {
                        obtainStyledAttributes2.getValue(121, findViewById2.getFixedHeightMinor());
                    }
                    obtainStyledAttributes2.recycle();
                    findViewById2.requestLayout();
                    this.f3209J = true;
                    l K3 = K(0);
                    if (!this.f3226a0 && K3.f3273h == null) {
                        M(108);
                        return;
                    }
                    return;
                }
                throw new IllegalArgumentException("AppCompat does not support the current theme features: { windowActionBar: " + this.f3215P + ", windowActionBarOverlay: " + this.f3216Q + ", android:windowIsFloating: " + this.f3218S + ", windowActionModeOverlay: " + this.f3217R + ", windowNoTitle: " + this.f3219T + " }");
            }
            obtainStyledAttributes.recycle();
            throw new IllegalStateException("You need to use a Theme.AppCompat theme (or descendant) with this activity.");
        }
    }

    public final void H() {
        if (this.f3244u == null) {
            Object obj = this.f3242s;
            if (obj instanceof Activity) {
                y(((Activity) obj).getWindow());
            }
        }
        if (this.f3244u != null) {
            return;
        }
        throw new IllegalStateException("We have not been given a Window");
    }

    public final i I(Context context) {
        if (this.f3232g0 == null) {
            if (y.f3314d == null) {
                Context applicationContext = context.getApplicationContext();
                y.f3314d = new y(applicationContext, (LocationManager) applicationContext.getSystemService("location"));
            }
            this.f3232g0 = new C0044j(y.f3314d);
        }
        return this.f3232g0;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v3, types: [java.lang.Object, e.j$l] */
    public final l K(int i4) {
        Object[] objArr = this.f3221V;
        if (objArr == null || objArr.length <= i4) {
            l[] lVarArr = new l[i4 + 1];
            if (objArr != null) {
                System.arraycopy(objArr, 0, lVarArr, 0, objArr.length);
            }
            this.f3221V = lVarArr;
            objArr = lVarArr;
        }
        l lVar = objArr[i4];
        if (lVar == 0) {
            ?? obj = new Object();
            obj.f3267a = i4;
            obj.f3279n = false;
            objArr[i4] = obj;
            return obj;
        }
        return lVar;
    }

    public final void L() {
        G();
        if (this.f3215P && this.f3247x == null) {
            Object obj = this.f3242s;
            if (obj instanceof Activity) {
                this.f3247x = new z((Activity) obj, this.f3216Q);
            } else if (obj instanceof Dialog) {
                this.f3247x = new z((Dialog) obj);
            }
            z zVar = this.f3247x;
            if (zVar != null) {
                boolean z4 = this.l0;
                if (!zVar.f3328h) {
                    zVar.a(z4);
                }
            }
        }
    }

    public final void M(int i4) {
        this.f3235j0 = (1 << i4) | this.f3235j0;
        if (!this.f3234i0) {
            View decorView = this.f3244u.getDecorView();
            WeakHashMap<View, V> weakHashMap = O.f1526a;
            decorView.postOnAnimation(this.f3236k0);
            this.f3234i0 = true;
        }
    }

    public final int N(Context context, int i4) {
        if (i4 == -100) {
            return -1;
        }
        if (i4 != -1) {
            if (i4 != 0) {
                if (i4 != 1 && i4 != 2) {
                    if (i4 == 3) {
                        if (this.f3233h0 == null) {
                            this.f3233h0 = new h(context);
                        }
                        return this.f3233h0.c();
                    }
                    throw new IllegalStateException("Unknown value set for night mode. Please use one of the MODE_NIGHT values from AppCompatDelegate.");
                }
            } else if (Build.VERSION.SDK_INT >= 23 && ((UiModeManager) context.getApplicationContext().getSystemService("uimode")).getNightMode() == 0) {
                return -1;
            } else {
                return I(context).c();
            }
        }
        return i4;
    }

    public final boolean O() {
        F f4;
        boolean z4 = this.f3223X;
        this.f3223X = false;
        l K3 = K(0);
        if (K3.f3278m) {
            if (!z4) {
                C(K3, true);
            }
            return true;
        }
        AbstractC0647a abstractC0647a = this.f3203D;
        if (abstractC0647a != null) {
            abstractC0647a.c();
            return true;
        }
        L();
        z zVar = this.f3247x;
        if (zVar == null || (f4 = zVar.f3326e) == null || !f4.j()) {
            return false;
        }
        zVar.f3326e.collapseActionView();
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:76:0x0152, code lost:
        if (r3 != null) goto L56;
     */
    /* JADX WARN: Code restructure failed: missing block: B:87:0x0172, code lost:
        if (r3.o.getCount() > 0) goto L65;
     */
    /* JADX WARN: Removed duplicated region for block: B:100:0x01d0  */
    /* JADX WARN: Removed duplicated region for block: B:104:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void P(e.LayoutInflater$Factory2C0401j.l r18, android.view.KeyEvent r19) {
        /*
            Method dump skipped, instructions count: 471
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: e.LayoutInflater$Factory2C0401j.P(e.j$l, android.view.KeyEvent):void");
    }

    public final boolean Q(l lVar, int i4, KeyEvent keyEvent) {
        androidx.appcompat.view.menu.f fVar;
        if (keyEvent.isSystem()) {
            return false;
        }
        if ((!lVar.f3276k && !R(lVar, keyEvent)) || (fVar = lVar.f3273h) == null) {
            return false;
        }
        return fVar.performShortcut(i4, keyEvent, 1);
    }

    public final boolean R(l lVar, KeyEvent keyEvent) {
        boolean z4;
        E e4;
        E e5;
        Resources.Theme theme;
        int i4;
        boolean z5;
        E e6;
        E e7;
        if (this.f3226a0) {
            return false;
        }
        if (lVar.f3276k) {
            return true;
        }
        l lVar2 = this.f3222W;
        if (lVar2 != null && lVar2 != lVar) {
            C(lVar2, false);
        }
        Window.Callback callback = this.f3244u.getCallback();
        int i5 = lVar.f3267a;
        if (callback != null) {
            lVar.f3272g = callback.onCreatePanelView(i5);
        }
        if (i5 != 0 && i5 != 108) {
            z4 = false;
        } else {
            z4 = true;
        }
        if (z4 && (e7 = this.f3200A) != null) {
            e7.c();
        }
        if (lVar.f3272g == null) {
            androidx.appcompat.view.menu.f fVar = lVar.f3273h;
            if (fVar == null || lVar.f3280o) {
                if (fVar == null) {
                    Context context = this.f3243t;
                    if ((i5 == 0 || i5 == 108) && this.f3200A != null) {
                        TypedValue typedValue = new TypedValue();
                        Resources.Theme theme2 = context.getTheme();
                        theme2.resolveAttribute(2130903049, typedValue, true);
                        if (typedValue.resourceId != 0) {
                            theme = context.getResources().newTheme();
                            theme.setTo(theme2);
                            theme.applyStyle(typedValue.resourceId, true);
                            theme.resolveAttribute(2130903050, typedValue, true);
                        } else {
                            theme2.resolveAttribute(2130903050, typedValue, true);
                            theme = null;
                        }
                        if (typedValue.resourceId != 0) {
                            if (theme == null) {
                                theme = context.getResources().newTheme();
                                theme.setTo(theme2);
                            }
                            theme.applyStyle(typedValue.resourceId, true);
                        }
                        if (theme != null) {
                            C0649c c0649c = new C0649c(context, 0);
                            c0649c.getTheme().setTo(theme);
                            context = c0649c;
                        }
                    }
                    androidx.appcompat.view.menu.f fVar2 = new androidx.appcompat.view.menu.f(context);
                    fVar2.e = this;
                    androidx.appcompat.view.menu.f fVar3 = lVar.f3273h;
                    if (fVar2 != fVar3) {
                        if (fVar3 != null) {
                            fVar3.r(lVar.f3274i);
                        }
                        lVar.f3273h = fVar2;
                        androidx.appcompat.view.menu.d dVar = lVar.f3274i;
                        if (dVar != null) {
                            fVar2.b(dVar, fVar2.a);
                        }
                    }
                    if (lVar.f3273h == null) {
                        return false;
                    }
                }
                if (z4 && (e5 = this.f3200A) != null) {
                    if (this.f3201B == null) {
                        this.f3201B = new b();
                    }
                    e5.a(lVar.f3273h, this.f3201B);
                }
                lVar.f3273h.w();
                if (!callback.onCreatePanelMenu(i5, lVar.f3273h)) {
                    androidx.appcompat.view.menu.f fVar4 = lVar.f3273h;
                    if (fVar4 != null) {
                        if (fVar4 != null) {
                            fVar4.r(lVar.f3274i);
                        }
                        lVar.f3273h = null;
                    }
                    if (z4 && (e4 = this.f3200A) != null) {
                        e4.a(null, this.f3201B);
                    }
                    return false;
                }
                lVar.f3280o = false;
            }
            lVar.f3273h.w();
            Bundle bundle = lVar.f3281p;
            if (bundle != null) {
                lVar.f3273h.s(bundle);
                lVar.f3281p = null;
            }
            if (!callback.onPreparePanel(0, lVar.f3272g, lVar.f3273h)) {
                if (z4 && (e6 = this.f3200A) != null) {
                    e6.a(null, this.f3201B);
                }
                lVar.f3273h.v();
                return false;
            }
            if (keyEvent != null) {
                i4 = keyEvent.getDeviceId();
            } else {
                i4 = -1;
            }
            if (KeyCharacterMap.load(i4).getKeyboardType() != 1) {
                z5 = true;
            } else {
                z5 = false;
            }
            lVar.f3273h.setQwertyMode(z5);
            lVar.f3273h.v();
        }
        lVar.f3276k = true;
        lVar.f3277l = false;
        this.f3222W = lVar;
        return true;
    }

    public final void S() {
        if (!this.f3209J) {
            return;
        }
        throw new AndroidRuntimeException("Window feature must be requested before adding content");
    }

    public final void T() {
        OnBackInvokedCallback onBackInvokedCallback;
        if (Build.VERSION.SDK_INT >= 33) {
            boolean z4 = false;
            if (this.f3240p0 != null && (K(0).f3278m || this.f3203D != null)) {
                z4 = true;
            }
            if (z4 && this.f3241q0 == null) {
                this.f3241q0 = f.b(this.f3240p0, this);
            } else if (!z4 && (onBackInvokedCallback = this.f3241q0) != null) {
                f.c(this.f3240p0, onBackInvokedCallback);
                this.f3241q0 = null;
            }
        }
    }

    public final boolean a(androidx.appcompat.view.menu.f fVar, MenuItem menuItem) {
        int i4;
        l lVar;
        Window.Callback callback = this.f3244u.getCallback();
        if (callback != null && !this.f3226a0) {
            androidx.appcompat.view.menu.f k4 = fVar.k();
            l[] lVarArr = this.f3221V;
            if (lVarArr != null) {
                i4 = lVarArr.length;
            } else {
                i4 = 0;
            }
            int i5 = 0;
            while (true) {
                if (i5 < i4) {
                    lVar = lVarArr[i5];
                    if (lVar != null && lVar.f3273h == k4) {
                        break;
                    }
                    i5++;
                } else {
                    lVar = null;
                    break;
                }
            }
            if (lVar != null) {
                return callback.onMenuItemSelected(lVar.f3267a, menuItem);
            }
        }
        return false;
    }

    public final void b(androidx.appcompat.view.menu.f fVar) {
        E e4 = this.f3200A;
        if (e4 != null && e4.g() && (!ViewConfiguration.get(this.f3243t).hasPermanentMenuKey() || this.f3200A.d())) {
            Window.Callback callback = this.f3244u.getCallback();
            if (this.f3200A.b()) {
                this.f3200A.e();
                if (!this.f3226a0) {
                    callback.onPanelClosed(108, K(0).f3273h);
                    return;
                }
                return;
            } else if (callback != null && !this.f3226a0) {
                if (this.f3234i0 && (1 & this.f3235j0) != 0) {
                    View decorView = this.f3244u.getDecorView();
                    a aVar = this.f3236k0;
                    decorView.removeCallbacks(aVar);
                    aVar.run();
                }
                l K3 = K(0);
                androidx.appcompat.view.menu.f fVar2 = K3.f3273h;
                if (fVar2 != null && !K3.f3280o && callback.onPreparePanel(0, K3.f3272g, fVar2)) {
                    callback.onMenuOpened(108, K3.f3273h);
                    this.f3200A.f();
                    return;
                }
                return;
            } else {
                return;
            }
        }
        l K4 = K(0);
        K4.f3279n = true;
        C(K4, false);
        P(K4, null);
    }

    @Override // e.AbstractC0399h
    public final void c(View view, ViewGroup.LayoutParams layoutParams) {
        G();
        ((ViewGroup) this.f3210K.findViewById(16908290)).addView(view, layoutParams);
        this.f3245v.a(this.f3244u.getCallback());
    }

    @Override // e.AbstractC0399h
    public final Context d(Context context) {
        int i4;
        int i5;
        int i6;
        int i7;
        int i8;
        int i9;
        int i10;
        int i11;
        this.f3224Y = true;
        int i12 = this.f3228c0;
        if (i12 == -100) {
            i12 = AbstractC0399h.f3183k;
        }
        int N3 = N(context, i12);
        if (AbstractC0399h.k(context) && AbstractC0399h.k(context)) {
            if (Build.VERSION.SDK_INT >= 33) {
                if (!AbstractC0399h.f3187o) {
                    AbstractC0399h.f3182j.execute(new RunnableC0293z(4, context));
                }
            } else {
                synchronized (AbstractC0399h.f3190r) {
                    try {
                        I.g gVar = AbstractC0399h.f3184l;
                        if (gVar == null) {
                            if (AbstractC0399h.f3185m == null) {
                                AbstractC0399h.f3185m = I.g.b(B.j.b(context));
                            }
                            if (!AbstractC0399h.f3185m.f1135a.isEmpty()) {
                                AbstractC0399h.f3184l = AbstractC0399h.f3185m;
                            }
                        } else if (!gVar.equals(AbstractC0399h.f3185m)) {
                            I.g gVar2 = AbstractC0399h.f3184l;
                            AbstractC0399h.f3185m = gVar2;
                            B.j.a(context, gVar2.f1135a.a());
                        }
                    } finally {
                    }
                }
            }
        }
        I.g z4 = z(context);
        Configuration configuration = null;
        if (context instanceof ContextThemeWrapper) {
            try {
                ((ContextThemeWrapper) context).applyOverrideConfiguration(D(context, N3, z4, null, false));
                return context;
            } catch (IllegalStateException unused) {
            }
        }
        if (context instanceof C0649c) {
            try {
                ((C0649c) context).a(D(context, N3, z4, null, false));
                return context;
            } catch (IllegalStateException unused2) {
            }
        }
        if (!f3199t0) {
            return context;
        }
        Configuration configuration2 = new Configuration();
        configuration2.uiMode = -1;
        configuration2.fontScale = 0.0f;
        Configuration configuration3 = context.createConfigurationContext(configuration2).getResources().getConfiguration();
        Configuration configuration4 = context.getResources().getConfiguration();
        configuration3.uiMode = configuration4.uiMode;
        if (!configuration3.equals(configuration4)) {
            configuration = new Configuration();
            configuration.fontScale = 0.0f;
            if (configuration3.diff(configuration4) != 0) {
                float f4 = configuration3.fontScale;
                float f5 = configuration4.fontScale;
                if (f4 != f5) {
                    configuration.fontScale = f5;
                }
                int i13 = configuration3.mcc;
                int i14 = configuration4.mcc;
                if (i13 != i14) {
                    configuration.mcc = i14;
                }
                int i15 = configuration3.mnc;
                int i16 = configuration4.mnc;
                if (i15 != i16) {
                    configuration.mnc = i16;
                }
                int i17 = Build.VERSION.SDK_INT;
                if (i17 >= 24) {
                    e.a(configuration3, configuration4, configuration);
                } else if (!Objects.equals(configuration3.locale, configuration4.locale)) {
                    configuration.locale = configuration4.locale;
                }
                int i18 = configuration3.touchscreen;
                int i19 = configuration4.touchscreen;
                if (i18 != i19) {
                    configuration.touchscreen = i19;
                }
                int i20 = configuration3.keyboard;
                int i21 = configuration4.keyboard;
                if (i20 != i21) {
                    configuration.keyboard = i21;
                }
                int i22 = configuration3.keyboardHidden;
                int i23 = configuration4.keyboardHidden;
                if (i22 != i23) {
                    configuration.keyboardHidden = i23;
                }
                int i24 = configuration3.navigation;
                int i25 = configuration4.navigation;
                if (i24 != i25) {
                    configuration.navigation = i25;
                }
                int i26 = configuration3.navigationHidden;
                int i27 = configuration4.navigationHidden;
                if (i26 != i27) {
                    configuration.navigationHidden = i27;
                }
                int i28 = configuration3.orientation;
                int i29 = configuration4.orientation;
                if (i28 != i29) {
                    configuration.orientation = i29;
                }
                int i30 = configuration3.screenLayout & 15;
                int i31 = configuration4.screenLayout & 15;
                if (i30 != i31) {
                    configuration.screenLayout |= i31;
                }
                int i32 = configuration3.screenLayout & 192;
                int i33 = configuration4.screenLayout & 192;
                if (i32 != i33) {
                    configuration.screenLayout |= i33;
                }
                int i34 = configuration3.screenLayout & 48;
                int i35 = configuration4.screenLayout & 48;
                if (i34 != i35) {
                    configuration.screenLayout |= i35;
                }
                int i36 = configuration3.screenLayout & 768;
                int i37 = configuration4.screenLayout & 768;
                if (i36 != i37) {
                    configuration.screenLayout |= i37;
                }
                if (i17 >= 26) {
                    i4 = configuration3.colorMode;
                    int i38 = i4 & 3;
                    i5 = configuration4.colorMode;
                    if (i38 != (i5 & 3)) {
                        i10 = configuration.colorMode;
                        i11 = configuration4.colorMode;
                        configuration.colorMode = i10 | (i11 & 3);
                    }
                    i6 = configuration3.colorMode;
                    int i39 = i6 & 12;
                    i7 = configuration4.colorMode;
                    if (i39 != (i7 & 12)) {
                        i8 = configuration.colorMode;
                        i9 = configuration4.colorMode;
                        configuration.colorMode = i8 | (i9 & 12);
                    }
                }
                int i40 = configuration3.uiMode & 15;
                int i41 = configuration4.uiMode & 15;
                if (i40 != i41) {
                    configuration.uiMode |= i41;
                }
                int i42 = configuration3.uiMode & 48;
                int i43 = configuration4.uiMode & 48;
                if (i42 != i43) {
                    configuration.uiMode |= i43;
                }
                int i44 = configuration3.screenWidthDp;
                int i45 = configuration4.screenWidthDp;
                if (i44 != i45) {
                    configuration.screenWidthDp = i45;
                }
                int i46 = configuration3.screenHeightDp;
                int i47 = configuration4.screenHeightDp;
                if (i46 != i47) {
                    configuration.screenHeightDp = i47;
                }
                int i48 = configuration3.smallestScreenWidthDp;
                int i49 = configuration4.smallestScreenWidthDp;
                if (i48 != i49) {
                    configuration.smallestScreenWidthDp = i49;
                }
                int i50 = configuration3.densityDpi;
                int i51 = configuration4.densityDpi;
                if (i50 != i51) {
                    configuration.densityDpi = i51;
                }
            }
        }
        Configuration D4 = D(context, N3, z4, configuration, true);
        C0649c c0649c = new C0649c(context, 2131886625);
        c0649c.a(D4);
        try {
            if (context.getTheme() != null) {
                f.C0005f.a(c0649c.getTheme());
            }
        } catch (NullPointerException unused3) {
        }
        return c0649c;
    }

    @Override // e.AbstractC0399h
    public final <T extends View> T e(int i4) {
        G();
        return (T) this.f3244u.findViewById(i4);
    }

    @Override // e.AbstractC0399h
    public final Context f() {
        return this.f3243t;
    }

    @Override // e.AbstractC0399h
    public final int g() {
        return this.f3228c0;
    }

    @Override // e.AbstractC0399h
    public final MenuInflater h() {
        Context context;
        if (this.f3248y == null) {
            L();
            z zVar = this.f3247x;
            if (zVar != null) {
                context = zVar.f();
            } else {
                context = this.f3243t;
            }
            this.f3248y = new C0652f(context);
        }
        return this.f3248y;
    }

    @Override // e.AbstractC0399h
    public final void i() {
        LayoutInflater from = LayoutInflater.from(this.f3243t);
        if (from.getFactory() == null) {
            from.setFactory2(this);
        } else if (!(from.getFactory2() instanceof LayoutInflater$Factory2C0401j)) {
            Log.i("AppCompatDelegate", "The Activity's LayoutInflater already has a Factory installed so we can not install AppCompat's");
        }
    }

    @Override // e.AbstractC0399h
    public final void j() {
        if (this.f3247x != null) {
            L();
            this.f3247x.getClass();
            M(0);
        }
    }

    @Override // e.AbstractC0399h
    public final void l() {
        if (this.f3215P && this.f3209J) {
            L();
            z zVar = this.f3247x;
            if (zVar != null) {
                zVar.h(zVar.f3322a.getResources().getBoolean(2130968576));
            }
        }
        C0701j a4 = C0701j.a();
        Context context = this.f3243t;
        synchronized (a4) {
            a4.f5163a.k(context);
        }
        this.f3227b0 = new Configuration(this.f3243t.getResources().getConfiguration());
        x(false, false);
    }

    @Override // e.AbstractC0399h
    public final void m() {
        String str;
        this.f3224Y = true;
        x(false, true);
        H();
        Object obj = this.f3242s;
        if (obj instanceof Activity) {
            try {
                Activity activity = (Activity) obj;
                try {
                    str = B.o.c(activity, activity.getComponentName());
                } catch (PackageManager.NameNotFoundException e4) {
                    throw new IllegalArgumentException(e4);
                }
            } catch (IllegalArgumentException unused) {
                str = null;
            }
            if (str != null) {
                z zVar = this.f3247x;
                if (zVar == null) {
                    this.l0 = true;
                } else if (!zVar.f3328h) {
                    zVar.a(true);
                }
            }
            synchronized (AbstractC0399h.f3189q) {
                AbstractC0399h.r(this);
                AbstractC0399h.f3188p.add(new WeakReference<>(this));
            }
        }
        this.f3227b0 = new Configuration(this.f3243t.getResources().getConfiguration());
        this.f3225Z = true;
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x0060  */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0067  */
    /* JADX WARN: Removed duplicated region for block: B:32:? A[RETURN, SYNTHETIC] */
    @Override // e.AbstractC0399h
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void n() {
        /*
            r3 = this;
            java.lang.Object r0 = r3.f3242s
            boolean r0 = r0 instanceof android.app.Activity
            if (r0 == 0) goto L11
            java.lang.Object r0 = e.AbstractC0399h.f3189q
            monitor-enter(r0)
            e.AbstractC0399h.r(r3)     // Catch: java.lang.Throwable -> Le
            monitor-exit(r0)     // Catch: java.lang.Throwable -> Le
            goto L11
        Le:
            r1 = move-exception
            monitor-exit(r0)     // Catch: java.lang.Throwable -> Le
            throw r1
        L11:
            boolean r0 = r3.f3234i0
            if (r0 == 0) goto L20
            android.view.Window r0 = r3.f3244u
            android.view.View r0 = r0.getDecorView()
            e.j$a r1 = r3.f3236k0
            r0.removeCallbacks(r1)
        L20:
            r0 = 1
            r3.f3226a0 = r0
            int r0 = r3.f3228c0
            r1 = -100
            if (r0 == r1) goto L4d
            java.lang.Object r0 = r3.f3242s
            boolean r1 = r0 instanceof android.app.Activity
            if (r1 == 0) goto L4d
            android.app.Activity r0 = (android.app.Activity) r0
            boolean r0 = r0.isChangingConfigurations()
            if (r0 == 0) goto L4d
            r.j<java.lang.String, java.lang.Integer> r0 = e.LayoutInflater$Factory2C0401j.f3197r0
            java.lang.Object r1 = r3.f3242s
            java.lang.Class r1 = r1.getClass()
            java.lang.String r1 = r1.getName()
            int r2 = r3.f3228c0
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r0.put(r1, r2)
            goto L5c
        L4d:
            r.j<java.lang.String, java.lang.Integer> r0 = e.LayoutInflater$Factory2C0401j.f3197r0
            java.lang.Object r1 = r3.f3242s
            java.lang.Class r1 = r1.getClass()
            java.lang.String r1 = r1.getName()
            r0.remove(r1)
        L5c:
            e.j$j r0 = r3.f3232g0
            if (r0 == 0) goto L63
            r0.a()
        L63:
            e.j$h r0 = r3.f3233h0
            if (r0 == 0) goto L6a
            r0.a()
        L6a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: e.LayoutInflater$Factory2C0401j.n():void");
    }

    @Override // e.AbstractC0399h
    public final void o() {
        L();
        z zVar = this.f3247x;
        if (zVar != null) {
            zVar.f3340t = true;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:62:0x010c, code lost:
        if (r12.equals("ImageButton") == false) goto L23;
     */
    @Override // android.view.LayoutInflater.Factory2
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final android.view.View onCreateView(android.view.View r11, java.lang.String r12, android.content.Context r13, android.util.AttributeSet r14) {
        /*
            Method dump skipped, instructions count: 718
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: e.LayoutInflater$Factory2C0401j.onCreateView(android.view.View, java.lang.String, android.content.Context, android.util.AttributeSet):android.view.View");
    }

    @Override // e.AbstractC0399h
    public final void p() {
        x(true, false);
    }

    @Override // e.AbstractC0399h
    public final void q() {
        L();
        z zVar = this.f3247x;
        if (zVar != null) {
            zVar.f3340t = false;
            C0653g c0653g = zVar.f3339s;
            if (c0653g != null) {
                c0653g.a();
            }
        }
    }

    @Override // e.AbstractC0399h
    public final boolean s(int i4) {
        if (i4 == 8) {
            Log.i("AppCompatDelegate", "You should now use the AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR id when requesting this feature.");
            i4 = 108;
        } else if (i4 == 9) {
            Log.i("AppCompatDelegate", "You should now use the AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY id when requesting this feature.");
            i4 = 109;
        }
        if (this.f3219T && i4 == 108) {
            return false;
        }
        if (this.f3215P && i4 == 1) {
            this.f3215P = false;
        }
        if (i4 != 1) {
            if (i4 != 2) {
                if (i4 != 5) {
                    if (i4 != 10) {
                        if (i4 != 108) {
                            if (i4 != 109) {
                                return this.f3244u.requestFeature(i4);
                            }
                            S();
                            this.f3216Q = true;
                            return true;
                        }
                        S();
                        this.f3215P = true;
                        return true;
                    }
                    S();
                    this.f3217R = true;
                    return true;
                }
                S();
                this.f3214O = true;
                return true;
            }
            S();
            this.f3213N = true;
            return true;
        }
        S();
        this.f3219T = true;
        return true;
    }

    @Override // e.AbstractC0399h
    public final void t(int i4) {
        G();
        ViewGroup viewGroup = (ViewGroup) this.f3210K.findViewById(16908290);
        viewGroup.removeAllViews();
        LayoutInflater.from(this.f3243t).inflate(i4, viewGroup);
        this.f3245v.a(this.f3244u.getCallback());
    }

    @Override // e.AbstractC0399h
    public final void u(View view) {
        G();
        ViewGroup viewGroup = (ViewGroup) this.f3210K.findViewById(16908290);
        viewGroup.removeAllViews();
        viewGroup.addView(view);
        this.f3245v.a(this.f3244u.getCallback());
    }

    @Override // e.AbstractC0399h
    public final void v(View view, ViewGroup.LayoutParams layoutParams) {
        G();
        ViewGroup viewGroup = (ViewGroup) this.f3210K.findViewById(16908290);
        viewGroup.removeAllViews();
        viewGroup.addView(view, layoutParams);
        this.f3245v.a(this.f3244u.getCallback());
    }

    @Override // e.AbstractC0399h
    public final void w(CharSequence charSequence) {
        this.f3249z = charSequence;
        E e4 = this.f3200A;
        if (e4 != null) {
            e4.setWindowTitle(charSequence);
            return;
        }
        z zVar = this.f3247x;
        if (zVar != null) {
            zVar.f3326e.setWindowTitle(charSequence);
            return;
        }
        TextView textView = this.f3211L;
        if (textView != null) {
            textView.setText(charSequence);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:122:0x01b9  */
    /* JADX WARN: Removed duplicated region for block: B:146:0x01ee  */
    /* JADX WARN: Removed duplicated region for block: B:169:0x0224  */
    /* JADX WARN: Removed duplicated region for block: B:174:0x0239  */
    /* JADX WARN: Removed duplicated region for block: B:178:0x0244  */
    /* JADX WARN: Removed duplicated region for block: B:181:0x0259  */
    /* JADX WARN: Removed duplicated region for block: B:189:0x0269  */
    /* JADX WARN: Removed duplicated region for block: B:193:0x0271  */
    /* JADX WARN: Removed duplicated region for block: B:196:0x027b  */
    /* JADX WARN: Removed duplicated region for block: B:198:0x0282  */
    /* JADX WARN: Removed duplicated region for block: B:203:0x02a4  */
    /* JADX WARN: Removed duplicated region for block: B:204:0x02ac  */
    /* JADX WARN: Removed duplicated region for block: B:209:0x02b6  */
    /* JADX WARN: Removed duplicated region for block: B:213:0x02c7  */
    /* JADX WARN: Removed duplicated region for block: B:219:0x020e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:221:0x01e0 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:38:0x0082  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x0098  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x009a  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x00a0  */
    /* JADX WARN: Removed duplicated region for block: B:45:0x00a3  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x00cf  */
    /* JADX WARN: Removed duplicated region for block: B:74:0x0105 A[ADDED_TO_REGION] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final boolean x(boolean r17, boolean r18) {
        /*
            Method dump skipped, instructions count: 719
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: e.LayoutInflater$Factory2C0401j.x(boolean, boolean):boolean");
    }

    public final void y(Window window) {
        Drawable drawable;
        OnBackInvokedDispatcher onBackInvokedDispatcher;
        OnBackInvokedCallback onBackInvokedCallback;
        int resourceId;
        if (this.f3244u == null) {
            Window.Callback callback = window.getCallback();
            if (!(callback instanceof g)) {
                g gVar = new g(callback);
                this.f3245v = gVar;
                window.setCallback(gVar);
                Context context = this.f3243t;
                TypedArray obtainStyledAttributes = context.obtainStyledAttributes((AttributeSet) null, f3198s0);
                if (obtainStyledAttributes.hasValue(0) && (resourceId = obtainStyledAttributes.getResourceId(0, 0)) != 0) {
                    C0701j a4 = C0701j.a();
                    synchronized (a4) {
                        drawable = a4.f5163a.g(context, resourceId, true);
                    }
                } else {
                    drawable = null;
                }
                if (drawable != null) {
                    window.setBackgroundDrawable(drawable);
                }
                obtainStyledAttributes.recycle();
                this.f3244u = window;
                if (Build.VERSION.SDK_INT >= 33 && (onBackInvokedDispatcher = this.f3240p0) == null) {
                    if (onBackInvokedDispatcher != null && (onBackInvokedCallback = this.f3241q0) != null) {
                        f.c(onBackInvokedDispatcher, onBackInvokedCallback);
                        this.f3241q0 = null;
                    }
                    Object obj = this.f3242s;
                    if (obj instanceof Activity) {
                        Activity activity = (Activity) obj;
                        if (activity.getWindow() != null) {
                            this.f3240p0 = f.a(activity);
                            T();
                            return;
                        }
                    }
                    this.f3240p0 = null;
                    T();
                    return;
                }
                return;
            }
            throw new IllegalStateException("AppCompat has already installed itself into the Window");
        }
        throw new IllegalStateException("AppCompat has already installed itself into the Window");
    }

    /* renamed from: e.j$g */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class g extends Window$CallbackC0654h {

        /* renamed from: k  reason: collision with root package name */
        public boolean f3255k;

        /* renamed from: l  reason: collision with root package name */
        public boolean f3256l;

        /* renamed from: m  reason: collision with root package name */
        public boolean f3257m;

        public g(Window.Callback callback) {
            super(callback);
        }

        public final void a(Window.Callback callback) {
            try {
                this.f3255k = true;
                callback.onContentChanged();
            } finally {
                this.f3255k = false;
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r2v10, types: [androidx.appcompat.view.menu.f$a, j.d, java.lang.Object, j.a] */
        public final C0651e b(ActionMode.Callback callback) {
            ViewGroup viewGroup;
            Context context;
            LayoutInflater$Factory2C0401j layoutInflater$Factory2C0401j = LayoutInflater$Factory2C0401j.this;
            C0651e.a aVar = new C0651e.a(layoutInflater$Factory2C0401j.f3243t, callback);
            AbstractC0647a abstractC0647a = layoutInflater$Factory2C0401j.f3203D;
            if (abstractC0647a != null) {
                abstractC0647a.c();
            }
            c cVar = new c(aVar);
            layoutInflater$Factory2C0401j.L();
            z zVar = layoutInflater$Factory2C0401j.f3247x;
            boolean z4 = true;
            if (zVar != null) {
                z.d dVar = zVar.f3329i;
                if (dVar != null) {
                    dVar.c();
                }
                zVar.f3324c.setHideOnContentScrollEnabled(false);
                zVar.f.h();
                z.d dVar2 = new z.d(zVar.f.getContext(), cVar);
                androidx.appcompat.view.menu.f fVar = dVar2.f3349m;
                fVar.w();
                try {
                    if (dVar2.f3350n.b(dVar2, fVar)) {
                        zVar.f3329i = dVar2;
                        dVar2.i();
                        zVar.f.f(dVar2);
                        zVar.d(true);
                    } else {
                        dVar2 = null;
                    }
                    layoutInflater$Factory2C0401j.f3203D = dVar2;
                } finally {
                    fVar.v();
                }
            }
            if (layoutInflater$Factory2C0401j.f3203D == null) {
                V v4 = layoutInflater$Factory2C0401j.f3207H;
                if (v4 != null) {
                    v4.b();
                }
                AbstractC0647a abstractC0647a2 = layoutInflater$Factory2C0401j.f3203D;
                if (abstractC0647a2 != null) {
                    abstractC0647a2.c();
                }
                if (layoutInflater$Factory2C0401j.f3246w != null) {
                    boolean z5 = layoutInflater$Factory2C0401j.f3226a0;
                }
                if (layoutInflater$Factory2C0401j.f3204E == null) {
                    boolean z6 = layoutInflater$Factory2C0401j.f3218S;
                    Context context2 = layoutInflater$Factory2C0401j.f3243t;
                    if (z6) {
                        TypedValue typedValue = new TypedValue();
                        Resources.Theme theme = context2.getTheme();
                        theme.resolveAttribute(2130903049, typedValue, true);
                        if (typedValue.resourceId != 0) {
                            Resources.Theme newTheme = context2.getResources().newTheme();
                            newTheme.setTo(theme);
                            newTheme.applyStyle(typedValue.resourceId, true);
                            C0649c c0649c = new C0649c(context2, 0);
                            c0649c.getTheme().setTo(newTheme);
                            context2 = c0649c;
                        }
                        layoutInflater$Factory2C0401j.f3204E = new ActionBarContextView(context2, (AttributeSet) null);
                        PopupWindow popupWindow = new PopupWindow(context2, (AttributeSet) null, 2130903064);
                        layoutInflater$Factory2C0401j.f3205F = popupWindow;
                        S.f.b(popupWindow, 2);
                        layoutInflater$Factory2C0401j.f3205F.setContentView(layoutInflater$Factory2C0401j.f3204E);
                        layoutInflater$Factory2C0401j.f3205F.setWidth(-1);
                        context2.getTheme().resolveAttribute(2130903043, typedValue, true);
                        layoutInflater$Factory2C0401j.f3204E.setContentHeight(TypedValue.complexToDimensionPixelSize(typedValue.data, context2.getResources().getDisplayMetrics()));
                        layoutInflater$Factory2C0401j.f3205F.setHeight(-2);
                        layoutInflater$Factory2C0401j.f3206G = new RunnableC0403l(layoutInflater$Factory2C0401j);
                    } else {
                        ViewStubCompat findViewById = layoutInflater$Factory2C0401j.f3210K.findViewById(2131230782);
                        if (findViewById != null) {
                            layoutInflater$Factory2C0401j.L();
                            z zVar2 = layoutInflater$Factory2C0401j.f3247x;
                            if (zVar2 != null) {
                                context = zVar2.f();
                            } else {
                                context = null;
                            }
                            if (context != null) {
                                context2 = context;
                            }
                            findViewById.setLayoutInflater(LayoutInflater.from(context2));
                            layoutInflater$Factory2C0401j.f3204E = findViewById.a();
                        }
                    }
                }
                if (layoutInflater$Factory2C0401j.f3204E != null) {
                    V v5 = layoutInflater$Factory2C0401j.f3207H;
                    if (v5 != null) {
                        v5.b();
                    }
                    layoutInflater$Factory2C0401j.f3204E.h();
                    Context context3 = layoutInflater$Factory2C0401j.f3204E.getContext();
                    ActionBarContextView actionBarContextView = layoutInflater$Factory2C0401j.f3204E;
                    ?? obj = new Object();
                    obj.f4656l = context3;
                    obj.f4657m = actionBarContextView;
                    obj.f4658n = cVar;
                    androidx.appcompat.view.menu.f fVar2 = new androidx.appcompat.view.menu.f(actionBarContextView.getContext());
                    fVar2.l = 1;
                    obj.f4661q = fVar2;
                    fVar2.e = obj;
                    if (cVar.f3252a.b(obj, fVar2)) {
                        obj.i();
                        layoutInflater$Factory2C0401j.f3204E.f((AbstractC0647a) obj);
                        layoutInflater$Factory2C0401j.f3203D = obj;
                        if ((layoutInflater$Factory2C0401j.f3209J && (viewGroup = layoutInflater$Factory2C0401j.f3210K) != null && viewGroup.isLaidOut()) ? false : false) {
                            layoutInflater$Factory2C0401j.f3204E.setAlpha(0.0f);
                            V a4 = O.a(layoutInflater$Factory2C0401j.f3204E);
                            a4.a(1.0f);
                            layoutInflater$Factory2C0401j.f3207H = a4;
                            a4.d(new C0404m(layoutInflater$Factory2C0401j));
                        } else {
                            layoutInflater$Factory2C0401j.f3204E.setAlpha(1.0f);
                            layoutInflater$Factory2C0401j.f3204E.setVisibility(0);
                            if (layoutInflater$Factory2C0401j.f3204E.getParent() instanceof View) {
                                WeakHashMap<View, V> weakHashMap = O.f1526a;
                                O.c.c((View) layoutInflater$Factory2C0401j.f3204E.getParent());
                            }
                        }
                        if (layoutInflater$Factory2C0401j.f3205F != null) {
                            layoutInflater$Factory2C0401j.f3244u.getDecorView().post(layoutInflater$Factory2C0401j.f3206G);
                        }
                    } else {
                        layoutInflater$Factory2C0401j.f3203D = null;
                    }
                }
                layoutInflater$Factory2C0401j.T();
                layoutInflater$Factory2C0401j.f3203D = layoutInflater$Factory2C0401j.f3203D;
            }
            layoutInflater$Factory2C0401j.T();
            AbstractC0647a abstractC0647a3 = layoutInflater$Factory2C0401j.f3203D;
            if (abstractC0647a3 == null) {
                return null;
            }
            return aVar.e(abstractC0647a3);
        }

        @Override // android.view.Window.Callback
        public final boolean dispatchKeyEvent(KeyEvent keyEvent) {
            boolean z4 = this.f3256l;
            Window.Callback callback = this.f4714j;
            if (z4) {
                return callback.dispatchKeyEvent(keyEvent);
            }
            if (!LayoutInflater$Factory2C0401j.this.E(keyEvent) && !callback.dispatchKeyEvent(keyEvent)) {
                return false;
            }
            return true;
        }

        /* JADX WARN: Code restructure failed: missing block: B:16:0x0039, code lost:
            if (r0 != false) goto L9;
         */
        /* JADX WARN: Code restructure failed: missing block: B:28:0x0067, code lost:
            if (r7 != false) goto L9;
         */
        /* JADX WARN: Removed duplicated region for block: B:33:0x006e A[ORIG_RETURN, RETURN] */
        /* JADX WARN: Removed duplicated region for block: B:36:? A[RETURN, SYNTHETIC] */
        @Override // android.view.Window.Callback
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public final boolean dispatchKeyShortcutEvent(android.view.KeyEvent r7) {
            /*
                r6 = this;
                android.view.Window$Callback r0 = r6.f4714j
                boolean r0 = r0.dispatchKeyShortcutEvent(r7)
                r1 = 1
                if (r0 != 0) goto L6f
                int r0 = r7.getKeyCode()
                e.j r2 = e.LayoutInflater$Factory2C0401j.this
                r2.L()
                e.z r3 = r2.f3247x
                r4 = 0
                if (r3 == 0) goto L3d
                e.z$d r3 = r3.f3329i
                if (r3 != 0) goto L1d
            L1b:
                r0 = 0
                goto L39
            L1d:
                androidx.appcompat.view.menu.f r3 = r3.f3349m
                if (r3 == 0) goto L1b
                int r5 = r7.getDeviceId()
                android.view.KeyCharacterMap r5 = android.view.KeyCharacterMap.load(r5)
                int r5 = r5.getKeyboardType()
                if (r5 == r1) goto L31
                r5 = 1
                goto L32
            L31:
                r5 = 0
            L32:
                r3.setQwertyMode(r5)
                boolean r0 = r3.performShortcut(r0, r7, r4)
            L39:
                if (r0 == 0) goto L3d
            L3b:
                r7 = 1
                goto L6b
            L3d:
                e.j$l r0 = r2.f3222W
                if (r0 == 0) goto L52
                int r3 = r7.getKeyCode()
                boolean r0 = r2.Q(r0, r3, r7)
                if (r0 == 0) goto L52
                e.j$l r7 = r2.f3222W
                if (r7 == 0) goto L3b
                r7.f3277l = r1
                goto L3b
            L52:
                e.j$l r0 = r2.f3222W
                if (r0 != 0) goto L6a
                e.j$l r0 = r2.K(r4)
                r2.R(r0, r7)
                int r3 = r7.getKeyCode()
                boolean r7 = r2.Q(r0, r3, r7)
                r0.f3276k = r4
                if (r7 == 0) goto L6a
                goto L3b
            L6a:
                r7 = 0
            L6b:
                if (r7 == 0) goto L6e
                goto L6f
            L6e:
                r1 = 0
            L6f:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: e.LayoutInflater$Factory2C0401j.g.dispatchKeyShortcutEvent(android.view.KeyEvent):boolean");
        }

        @Override // android.view.Window.Callback
        public final void onContentChanged() {
            if (this.f3255k) {
                this.f4714j.onContentChanged();
            }
        }

        @Override // android.view.Window.Callback
        public final boolean onCreatePanelMenu(int i4, Menu menu) {
            if (i4 == 0 && !(menu instanceof androidx.appcompat.view.menu.f)) {
                return false;
            }
            return this.f4714j.onCreatePanelMenu(i4, menu);
        }

        @Override // android.view.Window.Callback
        public final View onCreatePanelView(int i4) {
            return this.f4714j.onCreatePanelView(i4);
        }

        @Override // j.Window$CallbackC0654h, android.view.Window.Callback
        public final boolean onMenuOpened(int i4, Menu menu) {
            super.onMenuOpened(i4, menu);
            LayoutInflater$Factory2C0401j layoutInflater$Factory2C0401j = LayoutInflater$Factory2C0401j.this;
            if (i4 == 108) {
                layoutInflater$Factory2C0401j.L();
                z zVar = layoutInflater$Factory2C0401j.f3247x;
                if (zVar != null) {
                    zVar.e(true);
                }
            } else {
                layoutInflater$Factory2C0401j.getClass();
            }
            return true;
        }

        @Override // j.Window$CallbackC0654h, android.view.Window.Callback
        public final void onPanelClosed(int i4, Menu menu) {
            if (this.f3257m) {
                this.f4714j.onPanelClosed(i4, menu);
                return;
            }
            super.onPanelClosed(i4, menu);
            LayoutInflater$Factory2C0401j layoutInflater$Factory2C0401j = LayoutInflater$Factory2C0401j.this;
            if (i4 == 108) {
                layoutInflater$Factory2C0401j.L();
                z zVar = layoutInflater$Factory2C0401j.f3247x;
                if (zVar != null) {
                    zVar.e(false);
                }
            } else if (i4 == 0) {
                l K3 = layoutInflater$Factory2C0401j.K(i4);
                if (K3.f3278m) {
                    layoutInflater$Factory2C0401j.C(K3, false);
                }
            } else {
                layoutInflater$Factory2C0401j.getClass();
            }
        }

        @Override // android.view.Window.Callback
        public final boolean onPreparePanel(int i4, View view, Menu menu) {
            androidx.appcompat.view.menu.f fVar;
            if (menu instanceof androidx.appcompat.view.menu.f) {
                fVar = (androidx.appcompat.view.menu.f) menu;
            } else {
                fVar = null;
            }
            if (i4 == 0 && fVar == null) {
                return false;
            }
            if (fVar != null) {
                fVar.x = true;
            }
            boolean onPreparePanel = this.f4714j.onPreparePanel(i4, view, menu);
            if (fVar != null) {
                fVar.x = false;
            }
            return onPreparePanel;
        }

        @Override // j.Window$CallbackC0654h, android.view.Window.Callback
        public final void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> list, Menu menu, int i4) {
            androidx.appcompat.view.menu.f fVar = LayoutInflater$Factory2C0401j.this.K(0).f3273h;
            if (fVar != null) {
                super.onProvideKeyboardShortcuts(list, fVar, i4);
            } else {
                super.onProvideKeyboardShortcuts(list, menu, i4);
            }
        }

        @Override // android.view.Window.Callback
        public final ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int i4) {
            if (LayoutInflater$Factory2C0401j.this.f3208I && i4 == 0) {
                return b(callback);
            }
            return Window$CallbackC0654h.a.b(this.f4714j, callback, i4);
        }

        @Override // android.view.Window.Callback
        public final ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
            if (Build.VERSION.SDK_INT >= 23) {
                return null;
            }
            if (LayoutInflater$Factory2C0401j.this.f3208I) {
                return b(callback);
            }
            return this.f4714j.onWindowStartingActionMode(callback);
        }
    }

    @Override // android.view.LayoutInflater.Factory
    public final View onCreateView(String str, Context context, AttributeSet attributeSet) {
        return onCreateView(null, str, context, attributeSet);
    }
}
