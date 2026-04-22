package A1;

import android.content.Context;
import android.os.Bundle;
import com.google.android.gms.internal.ads.EF;
import com.google.android.gms.internal.ads.Fn;
import com.google.android.gms.internal.ads.Gq;
import com.google.android.gms.internal.ads.Jq;
import com.google.android.gms.internal.ads.UF;
import com.google.android.gms.internal.ads.Xr;
import com.google.android.gms.internal.ads.mQ;
import com.google.android.gms.internal.ads.pn;
import j1.InterfaceC0667e;
import java.util.concurrent.Executor;
import n1.C0743c;
import q1.InterfaceC0770b;

/* renamed from: A1.n  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0120n implements Gq, k3.a {

    /* renamed from: j  reason: collision with root package name */
    public final Object f153j;

    /* renamed from: k  reason: collision with root package name */
    public Object f154k;

    /* renamed from: l  reason: collision with root package name */
    public Object f155l;

    /* renamed from: m  reason: collision with root package name */
    public Object f156m;

    /* renamed from: n  reason: collision with root package name */
    public Object f157n;

    public static void b(Context context, String str) {
        Bundle bundle = new Bundle();
        bundle.putString("action", "no_ads_fallback");
        bundle.putString("flow", str);
        C0124p c0124p = C0124p.f;
        E1.f fVar = c0124p.f161a;
        String str2 = c0124p.f164d.f844j;
        fVar.getClass();
        E1.f.l(context, str2, bundle, new P0(fVar));
    }

    /* renamed from: a */
    public Fn g() {
        mQ.f((Xr) this.f156m, Xr.class);
        mQ.f((Jq) this.f157n, Jq.class);
        return new Fn((pn) this.f153j, (Xr) this.f156m, (Jq) this.f157n, new com.google.android.gms.internal.ads.M0(), (UF) this.f154k, (EF) this.f155l);
    }

    public /* synthetic */ Gq d(UF uf) {
        this.f154k = uf;
        return this;
    }

    public /* synthetic */ Gq f(EF ef) {
        this.f155l = ef;
        return this;
    }

    @Override // k3.a
    public Object get() {
        return new C0743c((Executor) ((k3.a) this.f153j).get(), (InterfaceC0667e) ((k3.a) this.f154k).get(), (o1.t) ((k3.a) this.f155l).get(), (p1.d) ((k3.a) this.f156m).get(), (InterfaceC0770b) ((k3.a) this.f157n).get());
    }

    public /* synthetic */ C0120n(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        this.f153j = obj;
        this.f154k = obj2;
        this.f155l = obj3;
        this.f156m = obj4;
        this.f157n = obj5;
    }
}
