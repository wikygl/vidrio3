package z1;

import A1.A;
import A1.A0;
import A1.C1;
import A1.D0;
import A1.I1;
import A1.InterfaceC0133u;
import A1.InterfaceC0134u0;
import A1.InterfaceC0139x;
import A1.K;
import A1.S;
import A1.W;
import A1.Z;
import A1.s1;
import A1.y1;
import D1.C0182d;
import W1.C0324l;
import a3.InterfaceFutureC0346a;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.webkit.WebView;
import c2.InterfaceC0374a;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.internal.ads.Ri;
import com.google.android.gms.internal.ads.Xb;
import com.google.android.gms.internal.ads.gc;
import com.google.android.gms.internal.ads.l9;
import com.google.android.gms.internal.ads.s7;
import com.google.android.gms.internal.ads.xk;
import java.util.Iterator;
import java.util.TreeMap;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class o extends K {

    /* renamed from: j  reason: collision with root package name */
    public final E1.a f6566j;

    /* renamed from: k  reason: collision with root package name */
    public final C1 f6567k;

    /* renamed from: l  reason: collision with root package name */
    public final InterfaceFutureC0346a f6568l = xk.a.y(new l(this));

    /* renamed from: m  reason: collision with root package name */
    public final Context f6569m;

    /* renamed from: n  reason: collision with root package name */
    public final n f6570n;

    /* renamed from: o  reason: collision with root package name */
    public WebView f6571o;

    /* renamed from: p  reason: collision with root package name */
    public InterfaceC0139x f6572p;

    /* renamed from: q  reason: collision with root package name */
    public s7 f6573q;

    /* renamed from: r  reason: collision with root package name */
    public AsyncTask f6574r;

    public o(Context context, C1 c12, String str, E1.a aVar) {
        this.f6569m = context;
        this.f6566j = aVar;
        this.f6567k = c12;
        this.f6571o = new WebView(context);
        this.f6570n = new n(context, str);
        C4(0);
        this.f6571o.setVerticalScrollBarEnabled(false);
        this.f6571o.getSettings().setJavaScriptEnabled(true);
        this.f6571o.setWebViewClient(new j(this));
        this.f6571o.setOnTouchListener(new k(this));
    }

    @Override // A1.L
    public final void B1(InterfaceC0133u interfaceC0133u) {
        throw new IllegalStateException("Unused method");
    }

    @Override // A1.L
    public final String C() {
        return null;
    }

    public final void C4(int i4) {
        if (this.f6571o == null) {
            return;
        }
        this.f6571o.setLayoutParams(new ViewGroup.LayoutParams(-1, i4));
    }

    @Override // A1.L
    public final String D() {
        return null;
    }

    @Override // A1.L
    public final void H() {
        throw new IllegalStateException("Unused method");
    }

    @Override // A1.L
    public final void H1(Ri ri) {
        throw new IllegalStateException("Unused method");
    }

    @Override // A1.L
    public final void I() {
        C0324l.b("destroy must be called on the main UI thread.");
        this.f6574r.cancel(true);
        this.f6568l.cancel(true);
        this.f6571o.destroy();
        this.f6571o = null;
    }

    @Override // A1.L
    public final void M() {
        C0324l.b("resume must be called on the main UI thread.");
    }

    @Override // A1.L
    public final void M0(C1 c12) {
        throw new IllegalStateException("AdSize must be set before initialization");
    }

    @Override // A1.L
    public final void M2(boolean z4) {
        throw new IllegalStateException("Unused method");
    }

    @Override // A1.L
    public final void N3(S s4) {
        throw new IllegalStateException("Unused method");
    }

    @Override // A1.L
    public final void O() {
        throw new IllegalStateException("Unused method");
    }

    @Override // A1.L
    public final void P() {
        throw new IllegalStateException("Unused method");
    }

    @Override // A1.L
    public final void P3(Xb xb) {
        throw new IllegalStateException("Unused method");
    }

    @Override // A1.L
    public final boolean V1(y1 y1Var) {
        Bundle bundle;
        TreeMap treeMap;
        C0324l.e(this.f6571o, "This Search Ad has already been torn down");
        n nVar = this.f6570n;
        nVar.getClass();
        nVar.f6564d = y1Var.f198s.f166j;
        Bundle bundle2 = y1Var.f201v;
        if (bundle2 != null) {
            bundle = bundle2.getBundle(AdMobAdapter.class.getName());
        } else {
            bundle = null;
        }
        if (bundle != null) {
            String str = (String) gc.c.e();
            Iterator<String> it = bundle.keySet().iterator();
            while (true) {
                boolean hasNext = it.hasNext();
                treeMap = nVar.f6563c;
                if (!hasNext) {
                    break;
                }
                String next = it.next();
                if (str.equals(next)) {
                    nVar.f6565e = bundle.getString(next);
                } else if (next.startsWith("csa_")) {
                    treeMap.put(next.substring(4), bundle.getString(next));
                }
            }
            treeMap.put("SDKVersion", this.f6566j.f844j);
            if (((Boolean) gc.a.e()).booleanValue()) {
                Bundle a4 = C0182d.a(nVar.f6561a, (String) gc.b.e());
                for (String str2 : a4.keySet()) {
                    treeMap.put(str2, a4.get(str2).toString());
                }
            }
        }
        this.f6574r = new m(this).execute(new Void[0]);
        return true;
    }

    @Override // A1.L
    public final void Y() {
        C0324l.b("pause must be called on the main UI thread.");
    }

    @Override // A1.L
    public final void b0() {
        throw new IllegalStateException("Unused method");
    }

    @Override // A1.L
    public final void b2(l9 l9Var) {
        throw new IllegalStateException("Unused method");
    }

    @Override // A1.L
    public final void d0() {
        throw new IllegalStateException("Unused method");
    }

    @Override // A1.L
    public final InterfaceC0139x f() {
        throw new IllegalStateException("getIAdListener not implemented");
    }

    @Override // A1.L
    public final C1 h() {
        return this.f6567k;
    }

    @Override // A1.L
    public final Bundle i() {
        throw new IllegalStateException("Unused method");
    }

    @Override // A1.L
    public final S j() {
        throw new IllegalStateException("getIAppEventListener not implemented");
    }

    @Override // A1.L
    public final A0 k() {
        return null;
    }

    @Override // A1.L
    public final InterfaceC0374a l() {
        C0324l.b("getAdFrame must be called on the main UI thread.");
        return new c2.b(this.f6571o);
    }

    @Override // A1.L
    public final boolean l0() {
        return false;
    }

    @Override // A1.L
    public final D0 m() {
        return null;
    }

    @Override // A1.L
    public final void m0() {
        throw new IllegalStateException("Unused method");
    }

    @Override // A1.L
    public final void m1(I1 i12) {
        throw new IllegalStateException("Unused method");
    }

    @Override // A1.L
    public final void p1(InterfaceC0139x interfaceC0139x) {
        this.f6572p = interfaceC0139x;
    }

    public final String q() {
        String str = this.f6570n.f6565e;
        if (true == TextUtils.isEmpty(str)) {
            str = "www.google.com";
        }
        return C.b.b("https://", str, (String) gc.d.e());
    }

    @Override // A1.L
    public final void q2() {
        throw new IllegalStateException("Unused method");
    }

    @Override // A1.L
    public final boolean s0() {
        return false;
    }

    @Override // A1.L
    public final String t() {
        throw new IllegalStateException("getAdUnitId not implemented");
    }

    @Override // A1.L
    public final void t0() {
        throw new IllegalStateException("Unused method");
    }

    @Override // A1.L
    public final void v0(s1 s1Var) {
        throw new IllegalStateException("Unused method");
    }

    @Override // A1.L
    public final void x0(W w4) {
        throw new IllegalStateException("Unused method");
    }

    @Override // A1.L
    public final void E0(InterfaceC0374a interfaceC0374a) {
    }

    @Override // A1.L
    public final void X1(InterfaceC0134u0 interfaceC0134u0) {
    }

    @Override // A1.L
    public final void Y0(Z z4) {
    }

    @Override // A1.L
    public final void p4(boolean z4) {
    }

    @Override // A1.L
    public final void J3(y1 y1Var, A a4) {
    }
}
