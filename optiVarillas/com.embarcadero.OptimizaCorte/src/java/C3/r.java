package C3;

import n3.f;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class r extends v3.i implements u3.p<n3.f, f.b, n3.f> {

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ v3.m<n3.f> f498k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ boolean f499l = true;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public r(v3.m mVar) {
        super(2);
        this.f498k = mVar;
    }

    /* JADX WARN: Type inference failed for: r1v5, types: [T, n3.f] */
    @Override // u3.p
    public final n3.f f(n3.f fVar, f.b bVar) {
        n3.f fVar2 = fVar;
        f.b bVar2 = bVar;
        if (!(bVar2 instanceof InterfaceC0166p)) {
            return fVar2.k(bVar2);
        }
        v3.m<n3.f> mVar = this.f498k;
        if (mVar.f6314j.E(bVar2.getKey()) == null) {
            InterfaceC0166p interfaceC0166p = (InterfaceC0166p) bVar2;
            if (this.f499l) {
                interfaceC0166p = interfaceC0166p.i();
            }
            return fVar2.k(interfaceC0166p);
        }
        mVar.f6314j = mVar.f6314j.q(bVar2.getKey());
        return fVar2.k(((InterfaceC0166p) bVar2).z());
    }
}
