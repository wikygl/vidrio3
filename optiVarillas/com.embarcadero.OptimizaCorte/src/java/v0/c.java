package V0;

import B0.v;
import C1.A;
import S0.L0;
import S0.M0;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import i2.AbstractC0455a;
import i2.C0459e;
import i2.C0460f;
import i2.C0462h;
import i2.C0463i;
import i2.C0467m;
import i2.C0468n;
import i2.C0469o;
import i2.C0470p;
import i2.C0472s;
import i2.C0476w;
import i2.D;
import i2.G;
import i2.X;
import i2.Y;
import i2.Z;
import i2.b0;
import i2.c0;
import i2.f0;
import j$.util.Objects;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import t1.C0802d;
import t1.C0804f;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class c {
    public static c0 f;

    /* renamed from: a  reason: collision with root package name */
    public final Activity f2525a;

    /* renamed from: b  reason: collision with root package name */
    public final C0804f f2526b;

    /* renamed from: c  reason: collision with root package name */
    public final F1.b f2527c;

    /* renamed from: d  reason: collision with root package name */
    public final String f2528d;

    /* renamed from: e  reason: collision with root package name */
    public final boolean f2529e = true;

    public c(Activity activity, C0804f c0804f, String str, F1.b bVar) {
        this.f2525a = activity;
        this.f2526b = c0804f;
        this.f2527c = bVar;
        this.f2528d = str;
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [java.lang.Object, X2.b$a] */
    public final void a() {
        ?? obj = new Object();
        obj.f2818a = false;
        final X2.b bVar = new X2.b(obj);
        c0 b4 = AbstractC0455a.a(this.f2525a.getApplicationContext()).b();
        f = b4;
        final Activity activity = this.f2525a;
        final L0 l0 = new L0(this);
        final M0 m02 = new M0(this);
        synchronized (b4.f3716d) {
            b4.f3717e = true;
        }
        final f0 f0Var = b4.f3714b;
        f0Var.getClass();
        f0Var.f3737c.execute(new Runnable() { // from class: i2.e0
            @Override // java.lang.Runnable
            public final void run() {
                String str;
                Activity activity2 = activity;
                X2.b bVar2 = bVar;
                L0 l02 = l0;
                M0 m03 = m02;
                f0 f0Var2 = f0.this;
                Handler handler = f0Var2.f3736b;
                C0463i c0463i = f0Var2.f3738d;
                try {
                    bVar2.getClass();
                    String a4 = C.a(f0Var2.f3735a);
                    Log.i("UserMessagingPlatform", "Use new ConsentDebugSettings.Builder().addTestDeviceHashedId(\"" + a4 + "\") to set this as a debug device.");
                    i0 a5 = new h0(f0Var2.f3740g, f0Var2.a(f0Var2.f.d(activity2, bVar2))).a();
                    c0463i.f3760b.edit().putInt("consent_status", a5.f3762a).apply();
                    int i4 = a5.f3763b;
                    SharedPreferences.Editor edit = c0463i.f3760b.edit();
                    if (i4 != 1) {
                        if (i4 != 2) {
                            if (i4 == 3) {
                                str = "REQUIRED";
                            } else {
                                throw null;
                            }
                        } else {
                            str = "NOT_REQUIRED";
                        }
                    } else {
                        str = "UNKNOWN";
                    }
                    edit.putString("privacy_options_requirement_status", str).apply();
                    C0469o c0469o = f0Var2.f3739e;
                    c0469o.f3791c.set(a5.f3764c);
                    f0Var2.f3741h.f3709a.execute(new C1.z(f0Var2, l02, a5, 5));
                } catch (b0 e4) {
                    handler.post(new B.h(m03, 7, e4));
                } catch (RuntimeException e5) {
                    handler.post(new A1.L0(m03, 11, new b0("Caught exception when trying to request consent info update: ".concat(String.valueOf(Log.getStackTraceString(e5))), 1)));
                }
            }
        });
    }

    public final void b() {
        if (f.a() != 3 && f.a() != 1) {
            Log.d("consentUMP", "loadNonPersonalizedAds: ");
            c(true);
            if (!this.f2529e) {
                e(true, this.f2527c);
                return;
            }
            return;
        }
        Log.d("consentUMP", "loadPersonalizedAds: ");
        c(false);
        if (!this.f2529e) {
            e(false, this.f2527c);
        }
    }

    public final void c(boolean z4) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList(1);
        Object obj = new Object[]{"9E438B9A38FAA47A8A8A16E20E3B0D5E"}[0];
        Objects.requireNonNull(obj);
        arrayList2.add(obj);
        List unmodifiableList = Collections.unmodifiableList(arrayList2);
        arrayList.clear();
        if (unmodifiableList != null) {
            arrayList.addAll(unmodifiableList);
        }
        C0804f c0804f = this.f2526b;
        if (z4) {
            Bundle bundle = new Bundle();
            bundle.putString("npa", "1");
            c0804f.a(new C0802d(new C0802d.a().a(bundle)));
            return;
        }
        c0804f.a(new C0802d(new C0802d.a()));
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v1, types: [X2.d, java.lang.Object] */
    public final void d() {
        Log.d("consentUMP", "loadForm: ");
        a aVar = new a(this);
        ?? obj = new Object();
        C0469o c4 = AbstractC0455a.a(this.f2525a).c();
        c4.getClass();
        Handler handler = G.f3679a;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            C0470p c0470p = (C0470p) c4.f3791c.get();
            if (c0470p == null) {
                new b0("No available form can be built.", 3).a();
                return;
            }
            C0460f c0460f = (C0460f) c4.f3789a.a();
            c0460f.getClass();
            C0459e c0459e = c0460f.f3734a;
            X b4 = X.b(new v(c0459e.f3723c));
            Y y4 = new Y(c0470p);
            A a4 = new A();
            Y y5 = c0459e.f3723c;
            X x4 = c0459e.f3726g;
            C0462h c0462h = c0459e.f3727h;
            X x5 = c0459e.f3724d;
            X b5 = X.b(new C0468n(y5, c0459e.f3725e, b4, x5, y4, new C0472s(b4, new C0476w(y5, b4, x4, c0462h, a4, x5))));
            if (((Z) a4.f352k) == null) {
                a4.f352k = b5;
                ((C0467m) a4.a()).a(aVar, obj);
                return;
            }
            throw new IllegalStateException();
        }
        throw new IllegalStateException("Method must be call on main thread.");
    }

    public final void e(boolean z4, F1.b bVar) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList(1);
        Object obj = new Object[]{"9E438B9A38FAA47A8A8A16E20E3B0D5E"}[0];
        Objects.requireNonNull(obj);
        arrayList2.add(obj);
        List unmodifiableList = Collections.unmodifiableList(arrayList2);
        arrayList.clear();
        if (unmodifiableList != null) {
            arrayList.addAll(unmodifiableList);
        }
        String str = this.f2528d;
        Activity activity = this.f2525a;
        if (z4) {
            Bundle bundle = new Bundle();
            bundle.putString("npa", "1");
            F1.a.b(activity, str, new C0802d(new C0802d.a().a(bundle)), bVar);
            return;
        }
        F1.a.b(activity, str, new C0802d(new C0802d.a()), bVar);
    }

    public final void f() {
        Log.d("consentUMP", "resetConsent: ");
        c0 c0Var = f;
        c0Var.f3715c.f3791c.set(null);
        C0463i c0463i = c0Var.f3713a;
        HashSet hashSet = c0463i.f3761c;
        D.b(c0463i.f3759a, hashSet);
        hashSet.clear();
        c0463i.f3760b.edit().remove("stored_info").remove("consent_status").remove("consent_type").apply();
        synchronized (c0Var.f3716d) {
            c0Var.f3717e = false;
        }
        a();
    }

    public c(Activity activity, C0804f c0804f) {
        this.f2525a = activity;
        this.f2526b = c0804f;
    }
}
