package A1;

import D1.C0195o;
import D1.C0198s;
import a3.InterfaceFutureC0346a;
import android.app.Activity;
import android.content.Context;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.internal.ads.Av;
import com.google.android.gms.internal.ads.Bt;
import com.google.android.gms.internal.ads.FI;
import com.google.android.gms.internal.ads.H7;
import com.google.android.gms.internal.ads.H8;
import com.google.android.gms.internal.ads.Hv;
import com.google.android.gms.internal.ads.Ip;
import com.google.android.gms.internal.ads.JA;
import com.google.android.gms.internal.ads.Kb;
import com.google.android.gms.internal.ads.Mk;
import com.google.android.gms.internal.ads.Mn;
import com.google.android.gms.internal.ads.Rk;
import com.google.android.gms.internal.ads.Vv;
import com.google.android.gms.internal.ads.Xh;
import com.google.android.gms.internal.ads.cL;
import com.google.android.gms.internal.ads.em;
import com.google.android.gms.internal.ads.jm;
import com.google.android.gms.internal.ads.lZ;
import com.google.android.gms.internal.ads.vF;
import com.google.android.gms.internal.ads.xt;
import com.google.android.gms.internal.ads.zk;
import java.util.HashSet;
import java.util.LinkedHashMap;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;
import p2.C0761j;
import p2.C0762k;
import t1.AbstractC0806h;

/* renamed from: A1.e1  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class RunnableC0098e1 implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f113j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f114k;

    public /* synthetic */ RunnableC0098e1(int i4, Object obj) {
        this.f113j = i4;
        this.f114k = obj;
    }

    private final void a() {
        String str;
        Mn mn = (Mn) this.f114k;
        mn.getClass();
        z1.p pVar = z1.p.f6575A;
        if (pVar.f6581g.c().o()) {
            D1.j0 c4 = pVar.f6581g.c();
            c4.p();
            synchronized (c4.f701a) {
                str = c4.f696B;
            }
            if (!pVar.f6587m.f(mn.j, str, mn.k.f844j)) {
                pVar.f6581g.c().d(false);
                pVar.f6581g.c().B("");
            }
        }
    }

    private final void b() {
        xt xtVar = (xt) this.f114k;
        xtVar.l.f();
        Bt bt = xtVar.k;
        synchronized (bt) {
            try {
                em emVar = bt.i;
                if (emVar != null) {
                    emVar.destroy();
                    bt.i = null;
                }
                em emVar2 = bt.j;
                if (emVar2 != null) {
                    emVar2.destroy();
                    bt.j = null;
                }
                em emVar3 = bt.k;
                if (emVar3 != null) {
                    emVar3.destroy();
                    bt.k = null;
                }
                InterfaceFutureC0346a interfaceFutureC0346a = bt.m;
                if (interfaceFutureC0346a != null) {
                    interfaceFutureC0346a.cancel(false);
                    bt.m = null;
                }
                zk zkVar = bt.n;
                if (zkVar != null) {
                    zkVar.cancel(false);
                    bt.n = null;
                }
                bt.l = null;
                bt.v.clear();
                bt.w.clear();
                bt.b = null;
                bt.c = null;
                bt.d = null;
                bt.e = null;
                bt.h = null;
                bt.o = null;
                bt.p = null;
                bt.q = null;
                bt.s = null;
                bt.t = null;
                bt.u = null;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private final void c() {
        JA ja = (JA) this.f114k;
        synchronized (ja) {
            ja.h = ja.a.b() - ja.i;
        }
    }

    private final void d() {
        synchronized (((C0762k) this.f114k).f5563k) {
            C0761j c0761j = ((C0762k) this.f114k).f5564l;
            if (c0761j != null) {
                c0761j.f5561l.n();
            }
        }
    }

    @Override // java.lang.Runnable
    public final void run() {
        boolean z4 = true;
        switch (this.f113j) {
            case 0:
                InterfaceC0139x interfaceC0139x = ((C0101f1) this.f114k).f119j;
                if (interfaceC0139x != null) {
                    try {
                        interfaceC0139x.w(1);
                        return;
                    } catch (RemoteException e4) {
                        E1.m.h("Could not notify onAdFailedToLoad event.", e4);
                        return;
                    }
                }
                return;
            case 1:
                C0195o c0195o = (C0195o) this.f114k;
                c0195o.getClass();
                C0198s c0198s = z1.p.f6575A.f6587m;
                String str = c0195o.f748d;
                String str2 = c0195o.f749e;
                String str3 = c0195o.f;
                boolean h4 = c0198s.h();
                Context context = c0195o.f745a;
                boolean f = c0198s.f(context, str, str2);
                synchronized (c0198s.f767a) {
                    c0198s.f770d = f;
                }
                if (c0198s.h()) {
                    if (!h4 && !TextUtils.isEmpty(str3)) {
                        c0198s.c(context, str2, str3, str);
                    }
                    E1.m.b("Device is linked for debug signals.");
                    C0198s.e(context, "The device is successfully linked for troubleshooting.", false, true);
                    return;
                }
                c0198s.b(context, str, str2);
                return;
            case 2:
                com.google.android.gms.internal.ads.K k4 = (com.google.android.gms.internal.ads.K) this.f114k;
                k4.getClass();
                int i4 = cL.a;
                lZ lZVar = k4.b.j.p;
                lZVar.B(lZVar.F(), 1019, new FI(8, (byte) 0));
                return;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                ((H8) this.f114k).c(3);
                return;
            case 4:
                Rk rk = ((Mk) this.f114k).y;
                if (rk != null) {
                    Rk rk2 = rk;
                    rk2.c("pause", new String[0]);
                    rk2.b();
                    rk2.q = false;
                    return;
                }
                return;
            case 5:
                int i5 = jm.O;
                Kb b4 = z1.p.f6575A.f6581g.b();
                HashSet hashSet = b4.g;
                String str4 = (String) this.f114k;
                if (!hashSet.contains(str4)) {
                    LinkedHashMap linkedHashMap = new LinkedHashMap();
                    linkedHashMap.put("sdkVersion", b4.f);
                    linkedHashMap.put("ue", str4);
                    b4.b(b4.a(b4.b, linkedHashMap), (Ip) null);
                    return;
                }
                return;
            case 6:
                a();
                return;
            case 7:
                b();
                return;
            case 8:
                ((Hv) this.f114k).a();
                return;
            case 9:
                Vv vv = (Vv) this.f114k;
                vv.b.a.a(vv.a, false);
                return;
            case 10:
                c();
                return;
            case 11:
                ((Av) this.f114k).a();
                return;
            case 12:
                vF vFVar = (vF) this.f114k;
                vFVar.j.a().execute(new H7(9, vFVar));
                return;
            case 13:
                StringBuilder sb = new StringBuilder("init: isGDPR? ");
                V0.c cVar = (V0.c) ((S0.L0) this.f114k).f2158j;
                Activity activity = cVar.f2525a;
                if (PreferenceManager.getDefaultSharedPreferences(activity).getInt("IABTCF_gdprApplies", 0) != 1) {
                    z4 = false;
                }
                sb.append(z4);
                Log.d("consentUMP", sb.toString());
                Log.d("consentUMP", "init: canShowAds? " + A3.d.b(activity));
                Log.d("consentUMP", "init: canShowPersonalizedAds? " + A3.d.c(activity));
                Log.d("consentUMP", "onConsentInfoUpdateSuccess: " + V0.c.f.a());
                activity.invalidateOptionsMenu();
                if (V0.c.f.f3715c.f3791c.get() != null) {
                    cVar.d();
                    return;
                } else {
                    cVar.b();
                    return;
                }
            case 14:
                d();
                return;
            default:
                AbstractC0806h abstractC0806h = (AbstractC0806h) this.f114k;
                try {
                    O0 o02 = abstractC0806h.f5800j;
                    o02.getClass();
                    try {
                        L l2 = o02.f74i;
                        if (l2 != null) {
                            l2.I();
                        }
                    } catch (RemoteException e5) {
                        E1.m.i("#007 Could not call remote method.", e5);
                    }
                    return;
                } catch (IllegalStateException e6) {
                    Xh.b(abstractC0806h.getContext()).a("BaseAdView.destroy", e6);
                    return;
                }
        }
    }

    public /* synthetic */ RunnableC0098e1(com.google.android.gms.internal.ads.K k4, String str) {
        this.f113j = 2;
        this.f114k = k4;
    }
}
