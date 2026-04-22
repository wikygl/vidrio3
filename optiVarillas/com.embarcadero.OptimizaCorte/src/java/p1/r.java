package p1;

import D1.C0205z;
import p1.h;
import r1.InterfaceC0782a;
import r1.b;
import r1.c;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class r implements k3.a {

    /* renamed from: j  reason: collision with root package name */
    public final k3.a<InterfaceC0782a> f5541j;

    /* renamed from: k  reason: collision with root package name */
    public final k3.a<InterfaceC0782a> f5542k;

    /* renamed from: l  reason: collision with root package name */
    public final k3.a<e> f5543l;

    /* renamed from: m  reason: collision with root package name */
    public final k3.a<x> f5544m;

    /* renamed from: n  reason: collision with root package name */
    public final k3.a<String> f5545n;

    public r(C0205z c0205z, k3.a aVar) {
        r1.b bVar = b.a.f5707a;
        r1.c cVar = c.a.f5708a;
        h hVar = h.a.f5517a;
        this.f5541j = bVar;
        this.f5542k = cVar;
        this.f5543l = hVar;
        this.f5544m = c0205z;
        this.f5545n = aVar;
    }

    @Override // k3.a
    public final Object get() {
        return new q(this.f5541j.get(), this.f5542k.get(), this.f5543l.get(), this.f5544m.get(), this.f5545n);
    }
}
