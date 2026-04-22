package o1;

import android.content.Context;
import com.google.android.gms.internal.ads.yn;
import i2.Y;
import j1.InterfaceC0667e;
import java.util.concurrent.Executor;
import q1.InterfaceC0770b;
import r1.InterfaceC0782a;
import r1.b;
import r1.c;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class q implements k3.a {

    /* renamed from: j  reason: collision with root package name */
    public final k3.a<Context> f5472j;

    /* renamed from: k  reason: collision with root package name */
    public final k3.a<InterfaceC0667e> f5473k;

    /* renamed from: l  reason: collision with root package name */
    public final k3.a<p1.d> f5474l;

    /* renamed from: m  reason: collision with root package name */
    public final k3.a<t> f5475m;

    /* renamed from: n  reason: collision with root package name */
    public final k3.a<Executor> f5476n;

    /* renamed from: o  reason: collision with root package name */
    public final k3.a<InterfaceC0770b> f5477o;

    /* renamed from: p  reason: collision with root package name */
    public final k3.a<InterfaceC0782a> f5478p;

    /* renamed from: q  reason: collision with root package name */
    public final k3.a<InterfaceC0782a> f5479q;

    /* renamed from: r  reason: collision with root package name */
    public final k3.a<p1.c> f5480r;

    public q(Y y4, k3.a aVar, k3.a aVar2, yn ynVar, k3.a aVar3, k3.a aVar4, k3.a aVar5) {
        r1.b bVar = b.a.f5707a;
        r1.c cVar = c.a.f5708a;
        this.f5472j = y4;
        this.f5473k = aVar;
        this.f5474l = aVar2;
        this.f5475m = ynVar;
        this.f5476n = aVar3;
        this.f5477o = aVar4;
        this.f5478p = bVar;
        this.f5479q = cVar;
        this.f5480r = aVar5;
    }

    @Override // k3.a
    public final Object get() {
        return new p(this.f5472j.get(), this.f5473k.get(), this.f5474l.get(), this.f5475m.get(), this.f5476n.get(), this.f5477o.get(), this.f5478p.get(), this.f5479q.get(), this.f5480r.get());
    }
}
