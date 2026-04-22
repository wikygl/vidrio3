package Q0;

import A1.I;
import A1.s1;
import D1.C0183d0;
import W1.C0324l;
import com.google.android.gms.internal.ads.AG;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.KG;
import com.google.android.gms.internal.ads.Mn;
import com.google.android.gms.internal.ads.Mz;
import com.google.android.gms.internal.ads.Ql;
import com.google.android.gms.internal.ads.Rk;
import com.google.android.gms.internal.ads.Ur;
import com.google.android.gms.internal.ads.Zf;
import com.google.android.gms.internal.ads.ag;
import com.google.android.gms.internal.ads.al;
import com.google.android.gms.internal.ads.bw;
import com.google.android.gms.internal.ads.eg;
import com.google.android.gms.internal.ads.em;
import com.google.android.gms.internal.ads.hg;
import com.google.android.gms.internal.ads.jl;
import com.google.android.gms.internal.ads.um;
import com.google.android.gms.internal.ads.wz;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;
import org.json.JSONObject;
import p2.AbstractC0757f;
import p2.C0764m;
import p2.InterfaceC0755d;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class v implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2010j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f2011k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ Object f2012l;

    public /* synthetic */ v(Object obj, int i4, Object obj2) {
        this.f2010j = i4;
        this.f2011k = obj;
        this.f2012l = obj2;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f2010j) {
            case 0:
                a aVar = (a) this.f2011k;
                aVar.getClass();
                com.android.billingclient.api.a aVar2 = com.android.billingclient.api.b.k;
                aVar.M(m.a(24, 3, aVar2));
                ((I) this.f2012l).getClass();
                I.c(aVar2);
                return;
            case 1:
                ((E1.q) this.f2011k).i((String) this.f2012l);
                return;
            case 2:
                Rk rk = ((jl) this.f2011k).o;
                if (rk != null) {
                    rk.c("error", new String[]{"what", "ExoPlayerAdapter error", "extra", (String) this.f2012l});
                    return;
                }
                return;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                int i4 = Ql.F;
                ((al) this.f2011k).b("onGcacheInfoEvent", (Map) this.f2012l);
                return;
            case 4:
                Mn mn = (Mn) this.f2011k;
                mn.getClass();
                C0324l.b("Adapters must be initialized on the main thread.");
                HashMap hashMap = z1.p.f6575A.f6581g.c().h().c;
                if (!hashMap.isEmpty()) {
                    Runnable runnable = (Runnable) this.f2012l;
                    if (runnable != null) {
                        try {
                            runnable.run();
                        } catch (Throwable th) {
                            E1.m.h("Could not initialize rewarded ads.", th);
                            return;
                        }
                    }
                    if (((eg) ((AtomicReference) mn.l.a.l).get()) != null) {
                        HashMap hashMap2 = new HashMap();
                        for (ag agVar : hashMap.values()) {
                            for (Zf zf : agVar.a) {
                                String str = zf.g;
                                for (String str2 : zf.a) {
                                    if (!hashMap2.containsKey(str2)) {
                                        hashMap2.put(str2, new ArrayList());
                                    }
                                    if (str != null) {
                                        ((List) hashMap2.get(str2)).add(str);
                                    }
                                }
                            }
                        }
                        JSONObject jSONObject = new JSONObject();
                        for (Map.Entry entry : hashMap2.entrySet()) {
                            String str3 = (String) entry.getKey();
                            try {
                                wz a4 = mn.m.a(str3, jSONObject);
                                if (a4 != null) {
                                    KG kg = (KG) a4.b;
                                    boolean a5 = kg.a();
                                    hg hgVar = kg.a;
                                    if (!a5 && hgVar.H()) {
                                        hgVar.x4(new c2.b(mn.j), a4.c, (List) entry.getValue());
                                        E1.m.b("Initialized rewarded video mediation adapter " + str3);
                                    }
                                }
                            } catch (AG e4) {
                                E1.m.h("Failed to initialize rewarded video mediation adapter \"" + str3 + "\"", e4);
                            }
                        }
                        return;
                    }
                    return;
                }
                return;
            case 5:
                try {
                    ((Ur) this.f2011k).d(this.f2012l);
                    return;
                } catch (Throwable th2) {
                    z1.p.f6575A.f6581g.g("EventEmitter.notify", th2);
                    C0183d0.l("Event emitter exception.", th2);
                    return;
                }
            case 6:
                ((bw) this.f2011k).d.i((String) this.f2012l);
                return;
            case 7:
                Mz mz = (Mz) this.f2011k;
                mz.getClass();
                em emVar = (em) this.f2012l;
                emVar.s0();
                um q4 = emVar.q();
                s1 s1Var = mz.d.a;
                if (s1Var != null && q4 != null) {
                    q4.E4(s1Var);
                }
                if (((Boolean) A1.r.f168d.f171c.a(Gb.Y0)).booleanValue() && !emVar.isAttachedToWindow()) {
                    emVar.onPause();
                    emVar.M0();
                    return;
                }
                return;
            default:
                synchronized (((C0764m) this.f2012l).f5569k) {
                    try {
                        InterfaceC0755d interfaceC0755d = ((C0764m) this.f2012l).f5570l;
                        if (interfaceC0755d != null) {
                            Exception g4 = ((AbstractC0757f) this.f2011k).g();
                            C0324l.d(g4);
                            interfaceC0755d.b(g4);
                        }
                    } finally {
                    }
                }
                return;
        }
    }

    public v(C0764m c0764m, AbstractC0757f abstractC0757f) {
        this.f2010j = 8;
        this.f2012l = c0764m;
        this.f2011k = abstractC0757f;
    }
}
