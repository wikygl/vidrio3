package n3;

import n3.f;
import u3.p;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public abstract class a implements f.b {

    /* renamed from: j  reason: collision with root package name */
    public final f.c<?> f5380j;

    public a(f.c<?> cVar) {
        this.f5380j = cVar;
    }

    @Override // n3.f
    public final <R> R B(R r4, p<? super R, ? super f.b, ? extends R> pVar) {
        return pVar.f(r4, this);
    }

    @Override // n3.f
    public <E extends f.b> E E(f.c<E> cVar) {
        return (E) f.b.a.a(this, cVar);
    }

    @Override // n3.f.b
    public final f.c<?> getKey() {
        return this.f5380j;
    }

    @Override // n3.f
    public final f k(f fVar) {
        return f.b.a.c(this, fVar);
    }

    @Override // n3.f
    public f q(f.c<?> cVar) {
        return f.b.a.b(this, cVar);
    }
}
