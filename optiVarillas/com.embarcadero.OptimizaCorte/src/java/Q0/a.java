package Q0;

import K1.B;
import S0.J0;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import com.google.android.gms.internal.ads.UN;
import com.google.android.gms.internal.play_billing.H1;
import com.google.android.gms.internal.play_billing.I1;
import com.google.android.gms.internal.play_billing.X;
import com.google.android.gms.internal.play_billing.Y1;
import com.google.android.gms.internal.play_billing.v1;
import com.google.android.gms.internal.play_billing.y1;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class a extends G3.g {

    /* renamed from: A  reason: collision with root package name */
    public boolean f1918A;

    /* renamed from: B  reason: collision with root package name */
    public boolean f1919B;

    /* renamed from: C  reason: collision with root package name */
    public boolean f1920C;

    /* renamed from: D  reason: collision with root package name */
    public final B2.a f1921D;

    /* renamed from: E  reason: collision with root package name */
    public final boolean f1922E;

    /* renamed from: F  reason: collision with root package name */
    public ExecutorService f1923F;

    /* renamed from: k  reason: collision with root package name */
    public volatile int f1924k;

    /* renamed from: l  reason: collision with root package name */
    public final String f1925l;

    /* renamed from: m  reason: collision with root package name */
    public final Handler f1926m;

    /* renamed from: n  reason: collision with root package name */
    public volatile s f1927n;

    /* renamed from: o  reason: collision with root package name */
    public final Context f1928o;

    /* renamed from: p  reason: collision with root package name */
    public final n f1929p;

    /* renamed from: q  reason: collision with root package name */
    public volatile Y1 f1930q;

    /* renamed from: r  reason: collision with root package name */
    public volatile l f1931r;

    /* renamed from: s  reason: collision with root package name */
    public boolean f1932s;

    /* renamed from: t  reason: collision with root package name */
    public int f1933t;

    /* renamed from: u  reason: collision with root package name */
    public boolean f1934u;

    /* renamed from: v  reason: collision with root package name */
    public boolean f1935v;

    /* renamed from: w  reason: collision with root package name */
    public boolean f1936w;

    /* renamed from: x  reason: collision with root package name */
    public boolean f1937x;

    /* renamed from: y  reason: collision with root package name */
    public boolean f1938y;

    /* renamed from: z  reason: collision with root package name */
    public boolean f1939z;

    public a(B2.a aVar, Context context, Y0.a aVar2) {
        String str;
        try {
            str = (String) Class.forName("com.android.billingclient.ktx.BuildConfig").getField("VERSION_NAME").get(null);
        } catch (Exception unused) {
            str = "7.0.0";
        }
        this.f1924k = 0;
        this.f1926m = new Handler(Looper.getMainLooper());
        this.f1933t = 0;
        this.f1925l = str;
        this.f1928o = context.getApplicationContext();
        H1 s4 = I1.s();
        s4.f();
        I1.p(((X) s4).k, str);
        String packageName = this.f1928o.getPackageName();
        s4.f();
        I1.q(((X) s4).k, packageName);
        L0.f fVar = new L0.f(this.f1928o, s4.b());
        this.f1929p = fVar;
        this.f1927n = new s(this.f1928o, aVar2, fVar);
        this.f1921D = aVar;
        this.f1922E = false;
        this.f1928o.getPackageName();
    }

    public final boolean F() {
        if (this.f1924k == 2 && this.f1930q != null && this.f1931r != null) {
            return true;
        }
        return false;
    }

    public final void G(h hVar, J0 j02) {
        if (!F()) {
            com.android.billingclient.api.a aVar = com.android.billingclient.api.b.j;
            M(m.a(2, 9, aVar));
            com.google.android.gms.internal.play_billing.f fVar = com.google.android.gms.internal.play_billing.h.k;
            j02.a(aVar, com.google.android.gms.internal.play_billing.m.n);
            return;
        }
        String str = hVar.f1973a;
        if (TextUtils.isEmpty(str)) {
            com.google.android.gms.internal.play_billing.u.e("BillingClient", "Please provide a valid product type.");
            com.android.billingclient.api.a aVar2 = com.android.billingclient.api.b.e;
            M(m.a(50, 9, aVar2));
            com.google.android.gms.internal.play_billing.f fVar2 = com.google.android.gms.internal.play_billing.h.k;
            j02.a(aVar2, com.google.android.gms.internal.play_billing.m.n);
        } else if (L(new B(this, str, j02), 30000L, new UN(this, 2, j02), I()) == null) {
            com.android.billingclient.api.a K3 = K();
            M(m.a(25, 9, K3));
            com.google.android.gms.internal.play_billing.f fVar3 = com.google.android.gms.internal.play_billing.h.k;
            j02.a(K3, com.google.android.gms.internal.play_billing.m.n);
        }
    }

    public final void H(b bVar) {
        if (F()) {
            com.google.android.gms.internal.play_billing.u.d("BillingClient", "Service connection is valid. No need to re-initialize.");
            N(m.b(6));
            ((Y0.a) bVar).e(com.android.billingclient.api.b.i);
            return;
        }
        int i4 = 1;
        if (this.f1924k == 1) {
            com.google.android.gms.internal.play_billing.u.e("BillingClient", "Client is already in the process of connecting to billing service.");
            com.android.billingclient.api.a aVar = com.android.billingclient.api.b.d;
            M(m.a(37, 6, aVar));
            ((Y0.a) bVar).e(aVar);
        } else if (this.f1924k == 3) {
            com.google.android.gms.internal.play_billing.u.e("BillingClient", "Client was already closed and can't be reused. Please create another instance.");
            com.android.billingclient.api.a aVar2 = com.android.billingclient.api.b.j;
            M(m.a(38, 6, aVar2));
            ((Y0.a) bVar).e(aVar2);
        } else {
            this.f1924k = 1;
            com.google.android.gms.internal.play_billing.u.d("BillingClient", "Starting in-app billing setup.");
            this.f1931r = new l(this, bVar);
            Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
            intent.setPackage("com.android.vending");
            List<ResolveInfo> queryIntentServices = this.f1928o.getPackageManager().queryIntentServices(intent, 0);
            if (queryIntentServices != null && !queryIntentServices.isEmpty()) {
                ServiceInfo serviceInfo = queryIntentServices.get(0).serviceInfo;
                if (serviceInfo != null) {
                    String str = serviceInfo.packageName;
                    String str2 = serviceInfo.name;
                    if ("com.android.vending".equals(str) && str2 != null) {
                        ComponentName componentName = new ComponentName(str, str2);
                        Intent intent2 = new Intent(intent);
                        intent2.setComponent(componentName);
                        intent2.putExtra("playBillingLibraryVersion", this.f1925l);
                        if (this.f1928o.bindService(intent2, this.f1931r, 1)) {
                            com.google.android.gms.internal.play_billing.u.d("BillingClient", "Service was bonded successfully.");
                            return;
                        } else {
                            com.google.android.gms.internal.play_billing.u.e("BillingClient", "Connection to Billing service is blocked.");
                            i4 = 39;
                        }
                    } else {
                        com.google.android.gms.internal.play_billing.u.e("BillingClient", "The device doesn't have valid Play Store.");
                        i4 = 40;
                    }
                }
            } else {
                i4 = 41;
            }
            this.f1924k = 0;
            com.google.android.gms.internal.play_billing.u.d("BillingClient", "Billing service unavailable on device.");
            com.android.billingclient.api.a aVar3 = com.android.billingclient.api.b.c;
            M(m.a(i4, 6, aVar3));
            ((Y0.a) bVar).e(aVar3);
        }
    }

    public final Handler I() {
        if (Looper.myLooper() == null) {
            return this.f1926m;
        }
        return new Handler(Looper.myLooper());
    }

    public final void J(com.android.billingclient.api.a aVar) {
        if (Thread.interrupted()) {
            return;
        }
        this.f1926m.post(new t(this, 0, aVar));
    }

    public final com.android.billingclient.api.a K() {
        if (this.f1924k != 0 && this.f1924k != 3) {
            return com.android.billingclient.api.b.h;
        }
        return com.android.billingclient.api.b.j;
    }

    public final Future L(Callable callable, long j4, Runnable runnable, Handler handler) {
        if (this.f1923F == null) {
            this.f1923F = Executors.newFixedThreadPool(com.google.android.gms.internal.play_billing.u.a, new j());
        }
        try {
            Future submit = this.f1923F.submit(callable);
            handler.postDelayed(new UN(submit, 3, runnable), (long) (j4 * 0.95d));
            return submit;
        } catch (Exception e4) {
            com.google.android.gms.internal.play_billing.u.f("BillingClient", "Async task throws exception!", e4);
            return null;
        }
    }

    public final void M(v1 v1Var) {
        n nVar = this.f1929p;
        int i4 = this.f1933t;
        L0.f fVar = (L0.f) nVar;
        fVar.getClass();
        try {
            I1 i12 = (I1) fVar.f1433b;
            H1 h12 = (X) i12.o(5);
            if (!((X) h12).j.equals(i12)) {
                if (!((X) h12).k.n()) {
                    h12.h();
                }
                X.i(((X) h12).k, i12);
            }
            H1 h13 = h12;
            h13.f();
            I1.r(((X) h13).k, i4);
            fVar.f1433b = h13.b();
            fVar.c(v1Var);
        } catch (Throwable th) {
            com.google.android.gms.internal.play_billing.u.f("BillingLogger", "Unable to log.", th);
        }
    }

    public final void N(y1 y1Var) {
        n nVar = this.f1929p;
        int i4 = this.f1933t;
        L0.f fVar = (L0.f) nVar;
        fVar.getClass();
        try {
            I1 i12 = (I1) fVar.f1433b;
            H1 h12 = (X) i12.o(5);
            if (!((X) h12).j.equals(i12)) {
                if (!((X) h12).k.n()) {
                    h12.h();
                }
                X.i(((X) h12).k, i12);
            }
            H1 h13 = h12;
            h13.f();
            I1.r(((X) h13).k, i4);
            fVar.f1433b = h13.b();
            fVar.e(y1Var);
        } catch (Throwable th) {
            com.google.android.gms.internal.play_billing.u.f("BillingLogger", "Unable to log.", th);
        }
    }
}
