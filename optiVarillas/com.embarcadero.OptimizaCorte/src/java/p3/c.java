package p3;

import n3.e;
import n3.f;
import v3.h;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public abstract class c extends a {

    /* renamed from: k  reason: collision with root package name */
    public final n3.f f5579k;

    /* renamed from: l  reason: collision with root package name */
    public transient n3.d<Object> f5580l;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public c(n3.d<Object> dVar) {
        super(dVar);
        n3.f fVar;
        if (dVar != null) {
            fVar = dVar.getContext();
        } else {
            fVar = null;
        }
        this.f5579k = fVar;
    }

    @Override // n3.d
    public final n3.f getContext() {
        n3.f fVar = this.f5579k;
        h.b(fVar);
        return fVar;
    }

    @Override // p3.a
    public final void k() {
        n3.d<?> dVar = this.f5580l;
        if (dVar != null && dVar != this) {
            n3.f fVar = this.f5579k;
            h.b(fVar);
            f.b E4 = fVar.E(e.a.f5386j);
            h.b(E4);
            ((n3.e) E4).v(dVar);
        }
        this.f5580l = b.f5578j;
    }
}
